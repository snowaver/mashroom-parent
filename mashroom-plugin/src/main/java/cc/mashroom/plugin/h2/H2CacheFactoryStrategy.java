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

import  org.apache.commons.io.IOUtils;
import  org.hyperic.sigar.Sigar;
import  org.hyperic.sigar.SigarException;

import  com.google.common.collect.Lists;

import  cc.mashroom.db.ConnectionFactory;
import  cc.mashroom.db.config.JDBCConfig;
import  cc.mashroom.db.connection.Connection;
import  cc.mashroom.plugin.Plugin;
import  cc.mashroom.util.collection.map.ConcurrentHashMap;
import  cc.mashroom.util.collection.map.HashMap;
import  cc.mashroom.util.collection.map.Map;
import  cc.mashroom.xcache.CacheFactory;
import  cc.mashroom.xcache.CacheFactoryStrategy;
import  cc.mashroom.xcache.XCache;
import  cc.mashroom.xcache.XClusterNode;
import  cc.mashroom.xcache.util.RemoteCallable;

public  class  H2CacheFactoryStrategy  implements  CacheFactoryStrategy,Plugin
{
	private  XClusterNode  localNode = new  XClusterNode( 0,UUID.randomUUID(),"0.0.0.0",new  HashMap<String,Object>() );
		
	public  String  getLocalNodeId()
	{
		return      localNode.getId().toString();
	}
	
	private  Sigar  sigar = new  Sigar();
	
	private  Map<String , H2Cache>  caches = new  ConcurrentHashMap<String , H2Cache>();
	
	public  <V>  V  call( RemoteCallable< V >  callable,List< String >  clusterNodeIds )
	{
		throw  new  UnsupportedOperationException( "MASHROOM-PLUGIN:  ** H2  CACHE  FACTORY  STRATEGY **  this  operation  is  not  supported  for  single  node  server" );
	}
	
	public  <K,V>  XCache<K,V>  createCache( String  name )
	{
		return  caches.computeIfAbsent( name,(key)-> new  H2Cache<K,V>(key) );
	}
	
	public  void  initialize()  throws  Exception
	{
		JDBCConfig.addDataSource( new  HashMap<String,Object>().addEntry("jdbc.h2mem.driverClass","org.h2.Driver").addEntry("jdbc.h2mem.jdbcUrl","jdbc:h2:mem:squirrel;DB_CLOSE_DELAY=-1") );
		
		try( InputStream  input = getClass().getResourceAsStream("/memory-policy.ddl") )
		{
			if( input   != null )
			{
				try( Connection  connection = ConnectionFactory.getConnection("h2mem") )
				{
					connection.runScripts( IOUtils.toString(input, "UTF-8") );
				}
			}
		}
		
		CacheFactory.setStrategy( this );
	}
	
	public  void  stop()
	{
		ConnectionFactory.stop();
	}
	
	public  List<XClusterNode>  getClusterNodes()
	{
		try
		{
			localNode.getMetrics().addEntry("CURRENT_CPU_LOAD",sigar.getCpuPerc().getCombined()).addEntry("HEAP_MEMORY_MAXIMUM",ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()).addEntry("HEAP_MEMORY_USED",ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed()).addEntry("CURRENT_THREAD_COUNT",Thread.getAllStackTraces().size()).addEntry( "MAXIMUM_THREAD_COUNT",null );
		}
		catch(SigarException  e )
		{
			e.printStackTrace( );
		}
		
		return  Lists.newArrayList(  localNode );
	}
}