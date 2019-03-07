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
import org.apache.ignite.lang.IgnitePredicate;
import  org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;

import  cc.mashroom.db.ConnectionFactory;
import  cc.mashroom.db.config.JDBCConfig;
import  cc.mashroom.db.connection.Connection;
import  cc.mashroom.plugin.Plugin;
import  cc.mashroom.util.ObjectUtils;
import  cc.mashroom.util.StringUtils;
import  cc.mashroom.util.collection.map.HashMap;
import  cc.mashroom.xcache.CacheFactory;
import  cc.mashroom.xcache.CacheFactoryStrategy;
import  cc.mashroom.xcache.XCache;
import  cc.mashroom.xcache.XClusterNode;
import  cc.mashroom.xcache.util.RemoteCallable;
import  lombok.SneakyThrows;

public  class  IgniteCacheFactoryStrategy   implements  CacheFactoryStrategy , Plugin
{
	public  <V>  V  call( RemoteCallable<V>  callable, List<String>  clusterNodeIds )
	{
		try
		{
			return  ignite.compute(createClusterGroup(new  HashSet<String>(clusterNodeIds))).call( new  IgniteCallable<V>(callable) );
		}
		catch( IgniteException  e )
		{
			
		}
		
		return  null;
	}
	
	public  List<XClusterNode>  getClusterNodes()
	{
		List<XClusterNode>  clusterClientNodes     = new  LinkedList<XClusterNode>();
		
		for( ClusterNode  clusterClientNode : ignite.cluster().forClients().nodes() )
		{
			for( String  address : clusterClientNode.addresses() )
			{
				if( !"127.0.0.1".equals(address) && address.matches("^[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+$") )
				{
					clusterClientNodes.add( new  XClusterNode(0,clusterClientNode.id(),address,new  HashMap<String,Object>().addEntry("CURRENT_CPU_LOAD",clusterClientNode.metrics().getCurrentCpuLoad()).addEntry("HEAP_MEMORY_MAXIMUM",clusterClientNode.metrics().getHeapMemoryMaximum()).addEntry("HEAP_MEMORY_USED",clusterClientNode.metrics().getHeapMemoryUsed()).addEntry("CURRENT_THREAD_COUNT",clusterClientNode.metrics().getCurrentThreadCount()).addEntry("MAXIMUM_THREAD_COUNT",clusterClientNode.metrics().getMaximumThreadCount())) );  break;
				}
			}
		}
		
		return  clusterClientNodes;
	}
	
	private  Ignite     ignite;
	
	public  void  stop()
	{
		Ignition.stop(     false );
	}
	
	@SneakyThrows
	public   void  initialize()
	{
		this.ignite = Ignition.start( System.getProperty("ignite.spring.config-file","ignite-config.xml") );
		
		try( InputStream  is = getClass().getResourceAsStream("/memory-policy.ddl") )
		{
			if( is != null && ignite.configuration().getDiscoverySpi() instanceof TcpDiscoverySpi )
			{
				String  scripts = IOUtils.toString( is, "UTF-8" );
				
				if( StringUtils.isNotBlank(scripts) )
				{
					boolean  sqlScriptUpdated= false;
					
					for( InetSocketAddress  alreadyRegisteredAddress : ObjectUtils.cast(ignite.configuration().getDiscoverySpi(),TcpDiscoverySpi.class).getIpFinder().getRegisteredAddresses() )
					{
						JDBCConfig.addDataSource( new  HashMap<String,Object>().addEntry("jdbc.ignitemem.driverClass","org.apache.ignite.IgniteJdbcThinDriver").addEntry("jdbc.ignitemem.jdbcUrl","jdbc:ignite:thin://"+alreadyRegisteredAddress.getAddress().getHostAddress()+"/") );
						
						try( Connection  connection= ConnectionFactory.getConnection("ignitemem") )
						{
							connection.runScripts(      scripts );
							
							sqlScriptUpdated  = true;
							
							break;
						}
						catch(   Throwable  e ){/* DO  NOTHING */}
					}
					
					if( !sqlScriptUpdated )
					{
						throw  new  IllegalStateException( "SQUIRREL-PLUGIN:  ** IGNITE  CACHE  FACTORY  STRATEGY **  can  not  execute  the  sql  scripts." );
					}
				}
			}
		}
		
		CacheFactory.setStrategy(IgniteCacheFactoryStrategy.this);
	}
	
	public  <K,V>  XCache<K,V>  createCache(   String  name )
	{
		return  new  IgniteCache( ignite.getOrCreateCache(name) );
	}
	
	public  String getLocalNodeId()
	{
		return  ignite.cluster().localNode().id().toString();
	}
		
	private  ClusterGroup  createClusterGroup( final  Set< String >  clusterNodeIds )
	{
		return  ignite.cluster().forPredicate( new  IgnitePredicate<ClusterNode>(){public  boolean  apply(ClusterNode  clusterNode){return  clusterNodeIds.contains(clusterNode.id().toString());}} );
	}
}