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
package cc.mashroom.plugin.h2;

import  java.io.InputStream;
import  java.lang.management.ManagementFactory;
import  java.util.List;
import  java.util.UUID;
import  java.util.concurrent.atomic.AtomicLong;

import  org.apache.commons.io.IOUtils;
import  org.hyperic.sigar.Sigar;
import  org.hyperic.sigar.SigarException;

import  com.google.common.collect.Lists;

import  cc.mashroom.db.ConnectionManager;
import  cc.mashroom.db.common.Db;
import  cc.mashroom.db.connection.Connection;
import  cc.mashroom.plugin.Plugin;
import  cc.mashroom.util.collection.map.ConcurrentHashMap;
import  cc.mashroom.util.collection.map.HashMap;
import  cc.mashroom.util.collection.map.Map;
import  cc.mashroom.xcache.CacheFactory;
import  cc.mashroom.xcache.CacheFactoryStrategy;
import  cc.mashroom.xcache.RemoteCallable;
import  cc.mashroom.xcache.XClusterNode;
import  cc.mashroom.xcache.XKeyValueCache;
import  cc.mashroom.xcache.XMemTableCache;

public  class  H2CacheFactoryStrategy  implements  CacheFactoryStrategy , Plugin
{
	private  XClusterNode  localNode = new  XClusterNode( 0,UUID.randomUUID(),"0.0.0.0",new  HashMap<String,Object>() );
	
	private  Sigar  sigar = new  Sigar();
	
	public   String      getLocalNodeId()
	{
		return this.localNode.getId().toString();
	}
	
	private  H2MemTableCacheRepository  cacheRepository= new  H2MemTableCacheRepository();
	
	private  Map<String,H2KeyValueCache>  keyValueCaches = new  ConcurrentHashMap<String,H2KeyValueCache>();
	
	private  Map<String,AtomicLong>  sequenceLongs = new  ConcurrentHashMap<String,AtomicLong>();
	
	private  Map<String,H2MemTableCache>  memTableCaches = new  ConcurrentHashMap<String,H2MemTableCache>();
	
	public  <V>  V  call( RemoteCallable<V>  remoteCallable,List<String>  clusterNodeIds )
	{
		throw  new  UnsupportedOperationException( "MASHROOM-PLUGIN:  ** H2  CACHE  FACTORY  STRATEGY **  this  operation  is  not  supported  for  single  node  server" );
	}
	
	public  <T>  T  tx( int  transactionIsolationLevel , Db.Callback  callback )throws  Exception
	{
		return  Db.tx( "xcache-memtable-datasource", transactionIsolationLevel,callback );
	}
	
	public  <K,V>  XKeyValueCache<K,V>  getOrCreateKeyValueCache( String  name )
	{
		return  keyValueCaches.computeIfLackof(name,new  Map.Computer<String,H2KeyValueCache>(){public  H2KeyValueCache  compute(String  key)  throws  Exception{return  new  H2KeyValueCache<K,V>(key);}});
	}
	
	public  XMemTableCache  getOrCreateMemTableCache( String  name )
	{
		return  memTableCaches.computeIfLackof(name,new  Map.Computer<String,H2MemTableCache>(){public  H2MemTableCache  compute(String  key)  throws  Exception{return  new  H2MemTableCache(key,cacheRepository);}});
	}
	
	public  void  initialize(       Object  ...  parameters )  throws  Exception
	{
		if( ConnectionManager.INSTANCE.addDataSource("org.h2.Driver","xcache-memtable-datasource","jdbc:h2:mem:squirrel;DB_CLOSE_DELAY=-1",null,null,2,4,null,"SELECT  2") )
		{
			throw  new  IllegalStateException( "MASHROOM-PLUGIN:  ** H2  CACHE  FACTORY  STRATEGY **  error  while  adding  memtable  data  source" );
		}
		
		try( InputStream  input = getClass().getResourceAsStream(System.getProperty("xcache.memtable.ddl.location","/memory-policy.ddl")) )
		{
			if( input  !=  null )
			{
				try(   Connection  connection = ConnectionManager.INSTANCE.getConnection("xcache-memtable-datasource") )
				{
					connection.runScripts( IOUtils.toString( input, "UTF-8" ) );
				}
			}
		}
		
		CacheFactory.setStrategy( this );
	}
	
	public  void  stop()
	{
		ConnectionManager.INSTANCE.release(/**/);
	}
	
	public  List<XClusterNode>  getClusterNodes()
	{
		try
		{
			localNode.getMetrics().addEntry("CURRENT_CPU_LOAD",sigar.getCpuPerc().getCombined()).addEntry("HEAP_MEMORY_MAXIMUM",ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()).addEntry("HEAP_MEMORY_USED",ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed()).addEntry("CURRENT_THREAD_COUNT",Thread.getAllStackTraces().size()).addEntry( "MAXIMUM_THREAD_COUNT",null );
		}
		catch(SigarException  se)
		{
			se.printStackTrace();
		}
		
		return  Lists.newArrayList(  localNode );
	}
	
	public  long  getNextSequence( String  name )
	{
		return  sequenceLongs.computeIfLackof(name,new  Map.Computer<String,AtomicLong>(){public  AtomicLong  compute(String  key)  throws  Exception{return  new  AtomicLong();}}).incrementAndGet();
	}
}