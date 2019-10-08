/*
 * Copyright 2019 snowaver.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.mashroom.plugin.ignite;

import  java.net.InetSocketAddress;
import  java.nio.charset.Charset;
import  java.util.List;
import  java.util.stream.Collectors;

import  org.apache.commons.io.IOUtils;
import  org.apache.ignite.Ignite;
import  org.apache.ignite.Ignition;
import  org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import  org.apache.ignite.transactions.Transaction;
import  org.apache.ignite.transactions.TransactionConcurrency;
import  org.apache.ignite.transactions.TransactionIsolation;

import  cc.mashroom.db.ConnectionManager;
import  cc.mashroom.db.common.Db;
import  cc.mashroom.db.connection.Connection;
import  cc.mashroom.plugin.Plugin;
import cc.mashroom.plugin.ignite.atomic.IgniteAtomicLong;
import  cc.mashroom.util.ObjectUtils;
import  cc.mashroom.util.StringUtils;
import  cc.mashroom.util.collection.map.HashMap;
import  cc.mashroom.util.collection.map.Map;
import  cc.mashroom.xcache.CacheFactory;
import  cc.mashroom.xcache.CacheFactoryStrategy;
import  cc.mashroom.xcache.RemoteCallable;
import  cc.mashroom.xcache.XClusterNode;
import  cc.mashroom.xcache.XKeyValueCache;
import  cc.mashroom.xcache.XMemTableCache;
import  cc.mashroom.xcache.atomic.XAtomicLong;
import  lombok.AccessLevel;
import  lombok.Setter;
import  lombok.experimental.Accessors;

public  class    IgniteCacheFactoryStrategy  implements  CacheFactoryStrategy , Plugin
{
	protected  Map<Integer, TransactionIsolation>  transactionIsolationLevels = new  HashMap<Integer,TransactionIsolation>().addEntry(java.sql.Connection.TRANSACTION_READ_COMMITTED,TransactionIsolation.READ_COMMITTED).addEntry(java.sql.Connection.TRANSACTION_REPEATABLE_READ,TransactionIsolation.REPEATABLE_READ).addEntry( java.sql.Connection.TRANSACTION_SERIALIZABLE,TransactionIsolation.SERIALIZABLE );	
	@Accessors( chain  =true )
	@Setter(value=AccessLevel.PRIVATE )
	protected  Ignite  ignite;
	@Accessors( chain  =true )
	@Setter(value=AccessLevel.PRIVATE )
	protected  String  script;
	
	public  void  stop()
	{
		Ignition.stop( false);
	}
	
	public  List<XClusterNode>  getClusterNodes()
	{
		return  this.ignite.cluster().forClients().nodes().parallelStream().map((node) -> new  XClusterNode(0,node.id(),node.addresses().stream().filter((address) -> !"127.0.0.1".equals(address) && address.matches("^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$")).findFirst().get(),new  HashMap<String,Object>().addEntry("CURRENT_CPU_LOAD",node.metrics().getCurrentCpuLoad()).addEntry("HEAP_MEMORY_MAXIMUM",node.metrics().getHeapMemoryMaximum()).addEntry("HEAP_MEMORY_USED",node.metrics().getHeapMemoryUsed()).addEntry("CURRENT_THREAD_COUNT",node.metrics().getCurrentThreadCount()).addEntry("MAXIMUM_THREAD_COUNT",node.metrics().getMaximumThreadCount()))).collect( Collectors.toList() );
	}
	
	public  String     getLocalNodeId()
	{
		return  this.ignite.cluster().localNode().id().toString( );
	}
	
	protected  boolean  runScript(  String  script,String  dataSourceName,InetSocketAddress  address )
	{
		try
		{
			ConnectionManager.INSTANCE.addDataSource( "org.apache.ignite.IgniteJdbcThinDriver",dataSourceName,"jdbc:ignite:thin://"+address.getAddress().getHostAddress()+"/",     null,null,2,4,null,"SELECT  2" );
		
			try(   Connection  connection = ConnectionManager.INSTANCE.getConnection(dataSourceName) )
			{
				connection.runScripts(  script );    return  true ;
			}
		}
		catch( Exception  adddtsError )
		{
			throw  new  IllegalStateException( String.format("MASHROOM-PLUGIN:  ** IGNITE  CACHE  FACTORY  STRATEGY **  error  while  adding  a  new  memtable  data  source  ( %s )",dataSourceName),adddtsError );
		}
	}
	
	public   void  initialize(   Object   ...  parameters )  throws   Exception
	{
		this.setIgnite(Ignition.start(parameters.length > 0 ? parameters[0].toString() : "ignite-config.xml")).setScript( IOUtils.resourceToString(parameters.length > 1 ? parameters[1].toString() : "/memory-policy.ddl",Charset.forName("UTF-8")) );
		
		if( this.ignite.configuration().getDiscoverySpi() instanceof TcpDiscoverySpi && StringUtils.isNotBlank(script) && !ObjectUtils.cast(this.ignite.configuration().getDiscoverySpi(),TcpDiscoverySpi.class).getIpFinder().getRegisteredAddresses().stream().anyMatch((address) -> runScript(script,"xcache-memtable-datasource",address)) )
		{
			throw  new  IllegalStateException( "SQUIRREL-PLUGIN:  ** IGNITE  CACHE  FACTORY  STRATEGY **  can  not  execute  sql  script." );
		}
		
		CacheFactory.setStrategy(this);
	}
	
	public  <T>  T  tx( int  transactionIsolationLevel, Db.Callback  callback )
	{
		try( Transaction  transaction = this.ignite.transactions().txStart(TransactionConcurrency.OPTIMISTIC,transactionIsolationLevels.containsKey(transactionIsolationLevel) ? transactionIsolationLevels.get(transactionIsolationLevel ) : TransactionIsolation.READ_COMMITTED) )
		{
			try
			{
				T  returnedObject = ObjectUtils.cast( callback.execute(null) );  transaction.commit();
				
				return  returnedObject;
			}
			catch( Throwable  txError )
			{
				transaction.rollback();  throw  new  RuntimeException( txError.getMessage(),txError );  
			}
		}
	}
	@Override
	public  XAtomicLong  atomicLong( String name)
	{
		return  new  IgniteAtomicLong(    this.ignite.atomicLong(name,0,true));
	}
	
	public  <K,V>  XKeyValueCache<K,V>  getOrCreateKeyValueCache(String  name )
	{
		return  new  IgniteKeyValueCache( this.ignite.getOrCreateCache(name) );
	}
	
	public  XMemTableCache  getOrCreateMemTableCache(String  name )
	{
		return  new  IgniteMemTableCache( this.ignite.getOrCreateCache(name) );
	}
	
	public  <V>  V  call( RemoteCallable<V>  callable, List<String>   clusterNodeIds )
	{
		return  this.ignite.compute(this.ignite.cluster().forPredicate(  (node) -> clusterNodeIds.contains(node.id().toString()))).call( new  IgniteCallable<V>(callable) );
	}
}