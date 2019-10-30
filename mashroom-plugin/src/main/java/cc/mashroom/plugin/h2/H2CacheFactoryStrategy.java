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

import  java.lang.management.ManagementFactory;
import  java.nio.charset.Charset;
import  java.util.List;
import  java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import  org.hyperic.sigar.Sigar;

import  com.google.common.collect.Lists;

import  cc.mashroom.db.ConnectionManager;
import  cc.mashroom.db.common.Db;
import  cc.mashroom.db.connection.Connection;
import  cc.mashroom.plugin.Plugin;
import  cc.mashroom.util.IOUtils;
import cc.mashroom.util.ObjectUtils;
import  cc.mashroom.util.collection.map.ConcurrentHashMap;
import  cc.mashroom.util.collection.map.HashMap;
import  cc.mashroom.util.collection.map.Map;
import  cc.mashroom.xcache.CacheFactory;
import  cc.mashroom.xcache.CacheFactoryStrategy;
import  cc.mashroom.xcache.RemoteCallable;
import  cc.mashroom.xcache.XClusterNode;
import  cc.mashroom.xcache.XKeyValueCache;
import  cc.mashroom.xcache.XMemTableCache;
import  cc.mashroom.xcache.atomic.NativeAtomicLong;
import  cc.mashroom.xcache.atomic.XAtomicLong;
import  lombok.SneakyThrows;

public  class  H2CacheFactoryStrategy  implements  CacheFactoryStrategy , Plugin
{
	private  XClusterNode  localNode = new  XClusterNode(0,UUID.randomUUID(),"0.0.0.0",new  HashMap<String,Object>() );
	
	private  Sigar  sigar  = new  Sigar();
	
	private  H2MemTableCacheRepository cacheRepository = new  H2MemTableCacheRepository();
	
	private  Map<String,H2KeyValueCache>  keyValueCaches = new  ConcurrentHashMap<String,H2KeyValueCache>();
	
	private  Map<String,BlockingQueue <?>>      queues = new  ConcurrentHashMap<String, BlockingQueue<?>>();
	
	private  Map<String,XAtomicLong >  atomicLongs = new  ConcurrentHashMap<String, XAtomicLong>();
	
	private  Map<String,H2MemTableCache>  memTableCaches = new  ConcurrentHashMap<String,H2MemTableCache>();
	@Override
	public  <V>  V  call( RemoteCallable<V>  remoteCallable,List<String>  clusterNodeIds )
	{
		throw  new  UnsupportedOperationException( "MASHROOM-PLUGIN:  ** H2  CACHE  FACTORY  STRATEGY **  this  operation  is  not  supported  for  single  node  server" );
	}
	@Override
	public  <T>  T  tx( int  transactionIsolationLevel , Db.Callback  callback )  throws  Exception
	{
		return  Db.tx( "xcache-memtable-datasource", transactionIsolationLevel,callback );
	}
	@Override
	public  XAtomicLong  atomicLong(      String  name,boolean  createIfAbsent )
	{
		return  !createIfAbsent ? this.atomicLongs.get(name) :this.atomicLongs.computeIfAbsent(name  , (key) -> new  NativeAtomicLong() );
	}
	@Override
	public  <K,V>  XKeyValueCache<K,V>  getOrCreateKeyValueCache( String  name )
	{
		return  this.keyValueCaches.computeIfAbsent( name,(key)-> new  H2KeyValueCache<K,V>(key) );
	}
	@Override
	public  XMemTableCache  getOrCreateMemTableCache(  String  name )
	{
		return  this.memTableCaches.computeIfAbsent( name,(key)-> new  H2MemTableCache(key   , this.cacheRepository) );
	}
	@Override
	public  void  stop()
	{
		ConnectionManager.INSTANCE.removeDataSource( /*DS*/"xcache-memtable-datasource" );
	}
	@Override
	public  <E>  BlockingQueue<E>  queue( String  name )
	{
		return  ObjectUtils.cast( this.queues.computeIfAbsent(name,(key)-> new  LinkedBlockingQueue<E>()) );
	}
	@Override
	public  void  initialize( Object ...  params )
	{
		try
		{
			ConnectionManager.INSTANCE.addDataSource("org.h2.Driver","xcache-memtable-datasource","jdbc:h2:mem:squirrel;DB_CLOSE_DELAY=-1",null,null,2,4,null,"SELECT  2" );
		
			try(      Connection  connection = ConnectionManager.INSTANCE.getConnection("xcache-memtable-datasource") )
			{
				connection.runScripts( IOUtils.resourceToString(    params[0].toString() , Charset.forName("UTF-8")) );
			}
		}
		catch( Exception  error )
		{
			throw  new  IllegalStateException( String.format("MASHROOM-PLUGIN:  ** H2  CACHE  FACTORY  STRATEGY **  error  while  adding  a  new  memtable  data  source  ( %s )","xcache-memtable-datasource"),error );
		}
		
		CacheFactory.setStrategy(  this );
	}
	@SneakyThrows
	public  List<XClusterNode>  getClusterNodes( )
	{
		localNode.getMetrics().addEntry("CURRENT_CPU_LOAD",sigar.getCpuPerc().getCombined()).addEntry("HEAP_MEMORY_MAXIMUM",ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()).addEntry("HEAP_MEMORY_USED",ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed()).addEntry("CURRENT_THREAD_COUNT",Thread.getAllStackTraces().size()).addEntry( "MAXIMUM_THREAD_COUNT",null );
		
		return  Lists.newArrayList(   localNode );
	}
	
	public        String  getLocalNodeId()
	{
		return  this.localNode.getId().toString();
	}
	/*
	public  void  stop()
	{
		ConnectionManager.INSTANCE.stop();
	}
	*/
}