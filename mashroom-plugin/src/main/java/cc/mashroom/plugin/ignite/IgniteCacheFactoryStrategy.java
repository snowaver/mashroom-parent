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

import  java.io.InputStream;
import  java.net.InetSocketAddress;
import  java.util.HashSet;
import  java.util.LinkedList;
import  java.util.List;
import  java.util.Set;

import  org.apache.commons.io.IOUtils;
import  org.apache.ignite.Ignite;
import  org.apache.ignite.IgniteException;
import  org.apache.ignite.Ignition;
import  org.apache.ignite.cluster.ClusterGroup;
import  org.apache.ignite.cluster.ClusterNode;
import  org.apache.ignite.lang.IgnitePredicate;
import  org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import  org.apache.ignite.transactions.Transaction;
import  org.apache.ignite.transactions.TransactionConcurrency;
import  org.apache.ignite.transactions.TransactionIsolation;

import  com.fasterxml.jackson.core.type.TypeReference;

import  cc.mashroom.db.ConnectionManager;
import  cc.mashroom.db.common.Db;
import  cc.mashroom.db.connection.Connection;
import  cc.mashroom.plugin.Plugin;
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
import  lombok.SneakyThrows;

public  class  IgniteCacheFactoryStrategy   implements  CacheFactoryStrategy , Plugin
{
	protected  Map<Integer,TransactionIsolation>  transactionIsolationLevels = new  HashMap<Integer,TransactionIsolation>().addEntry(java.sql.Connection.TRANSACTION_READ_COMMITTED,TransactionIsolation.READ_COMMITTED).addEntry(java.sql.Connection.TRANSACTION_REPEATABLE_READ,TransactionIsolation.REPEATABLE_READ).addEntry( java.sql.Connection.TRANSACTION_SERIALIZABLE,TransactionIsolation.SERIALIZABLE );
	
	public  <V>  V  call( RemoteCallable<V>  callable, List<String>  clusterNodeIds )
	{
		try
		{
			return  ignite.compute( createClusterGroup(new  HashSet<String>(clusterNodeIds)) ).call( new  IgniteCallable<V>(callable) );
		}
		catch(IgniteException igne)
		{
			igne.printStackTrace();
		}
		
		return  null;
	}
	
	public  List<XClusterNode>  getClusterNodes()
	{
		List<XClusterNode>  clusterClientNodes     = new  LinkedList<XClusterNode>();
		
		for( ClusterNode  clusterClientNode : ignite.cluster().forClients().nodes() )
		{
			for( String  address  : clusterClientNode.addresses() )
			{
				if( !"127.0.0.1".equals(address) && address.matches("^[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+$") )
				{
					clusterClientNodes.add( new  XClusterNode(0,clusterClientNode.id(),address,new  HashMap<String,Object>().addEntry("CURRENT_CPU_LOAD",clusterClientNode.metrics().getCurrentCpuLoad()).addEntry("HEAP_MEMORY_MAXIMUM",clusterClientNode.metrics().getHeapMemoryMaximum()).addEntry("HEAP_MEMORY_USED",clusterClientNode.metrics().getHeapMemoryUsed()).addEntry("CURRENT_THREAD_COUNT",clusterClientNode.metrics().getCurrentThreadCount()).addEntry("MAXIMUM_THREAD_COUNT",clusterClientNode.metrics().getMaximumThreadCount())) );  break;
				}
			}
		}
		
		return  clusterClientNodes;
	}
	
	private  Ignite  ignite;
	
	public  void  stop()
	{
		Ignition.stop(     false );
	}
		
	@SneakyThrows
	public   void  initialize(            Object  ...  parameters )
	{
		this.ignite = Ignition.start( System.getProperty("ignite.spring.config-file","ignite-config.xml") );
		
		try( InputStream  is = getClass().getResourceAsStream("/memory-policy.ddl") )
		{
			if( is != null && ignite.configuration().getDiscoverySpi() instanceof TcpDiscoverySpi )
			{
				String  sqlScript = IOUtils.toString( is,"UTF-8" );
				
				if( StringUtils.isNotBlank(    sqlScript.trim() ) )
				{
					boolean initialized = false;
					
					for( InetSocketAddress  address : ObjectUtils.cast(ignite.configuration().getDiscoverySpi(),TcpDiscoverySpi.class).getIpFinder().getRegisteredAddresses() )
					{
						if( initialized=runScript(sqlScript,"xcache-memtable-datasource",address) )
						{
							break;
						}
					}
					
					if( !initialized )  throw  new  IllegalStateException(  "SQUIRREL-PLUGIN:  ** IGNITE  CACHE  FACTORY  STRATEGY **  can  not  execute  the  sql  script." );
				}
			}
		}
		
		CacheFactory.setStrategy(IgniteCacheFactoryStrategy.this );
	}
	
	public  <T>  T  tx( int  transactionIsolationLevel , Db.Callback  callback )  throws  Exception
	{
		try( Transaction  transaction = ignite.transactions().txStart(TransactionConcurrency.OPTIMISTIC,transactionIsolationLevels.containsKey(transactionIsolationLevel) ? transactionIsolationLevels.get(transactionIsolationLevel) : TransactionIsolation.READ_COMMITTED) )
		{
			try
			{
				T  returned = ObjectUtils.cast( callback.execute(null),new  TypeReference<T>(){} );  transaction.commit();
				
				return    returned;
			}
			catch( Throwable  txe )
			{
				transaction.rollback();     throw  new  RuntimeException( txe.getMessage() , txe );  
			}
		}
	}
	
	protected  boolean  runScript( String  sqlScript , String  dataSourceName , InetSocketAddress  address )
	{
		if( ConnectionManager.INSTANCE.addDataSource("org.apache.ignite.IgniteJdbcThinDriver","xcache-memtable-datasource","jdbc:ignite:thin://"+address.getAddress().getHostAddress()+"/",null,null,2,4,null,"SELECT  2") )
		{
			throw  new  IllegalStateException( "MASHROOM-PLUGIN:  ** H2  CACHE  FACTORY  STRATEGY **  error  while  adding  memtable  data  source" );
		}
		
		try(Connection  connection= ConnectionManager.INSTANCE.getConnection("xcache-memtable-datasource") )
		{
			connection.runScripts(  sqlScript );      return  true;
		}
		catch(    Throwable  rsst )
		{
			rsst.printStackTrace();
		}
		
		return  false;
	}
	
	public  <K,V>  XKeyValueCache<K,V>  getOrCreateKeyValueCache(      String  name )
	{
		return  new  IgniteKeyValueCache( this.ignite.getOrCreateCache(name) );
	}
	
	public  XMemTableCache  getOrCreateMemTableCache(String  name )
	{
		return  new  IgniteMemTableCache( this.ignite.getOrCreateCache(name) );
	}
	
	public  String getLocalNodeId()
	{
		return  this.ignite.cluster().localNode().id().toString( );
	}
	
	public  long  getNextSequence(String  name )
	{
		return  this.ignite.atomicLong(name, 0, true).incrementAndGet();
	}
		
	private  ClusterGroup  createClusterGroup( final  Set< String >  clusterNodeIds )
	{
		return  this.ignite.cluster().forPredicate( new  IgnitePredicate<ClusterNode>(){public  boolean  apply(ClusterNode  clusterNode){return  clusterNodeIds.contains(clusterNode.id().toString());}} );
	}
}