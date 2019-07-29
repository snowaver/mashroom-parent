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
package cc.mashroom.db.config;

import  java.util.HashSet;
import  java.util.Map.Entry;
import  java.util.Set;

import  javax.annotation.Nonnull;

import  cc.mashroom.config.Config;
import  cc.mashroom.config.Properties;
import  cc.mashroom.util.collection.map.ConcurrentHashMap;
import  cc.mashroom.util.collection.map.Map;

public  class  JDBCConfig
{
	private  final  static  Map<String,Properties>  PROPERTIES = new  ConcurrentHashMap<String,Properties>();

	static
	{
		for(          Entry<Object,Object>  entry : Config.use("jdbc.properties").entrySet() )
		{
			PROPERTIES.computeIfLackof(String.valueOf(entry.getKey()).split("\\.")[1],new  Map.Computer<String,Properties>(){public  Properties  compute(String  key)  throws  Exception{ return  new  Properties(); }}).put( String.valueOf(entry.getKey()).split("\\.")[2],entry.getValue() );
		}
	}
	
	public  static  Set<String> getDataSourceNames()
	{
		return          new  HashSet<String>( PROPERTIES.keySet() );
	}
	
	public  static  void  addDataSource( @Nonnull  String  driverClassName,@Nonnull  String  dataSourceName,@Nonnull  String  jdbcUrl,String  user,String  password,Integer  minPoolSize,Integer  maxPoolSize,Long  idleConnectionTestPeriod,String  preferredTestQuery )
	{
		if( PROPERTIES.containsKey(dataSourceName) )
		{
			throw  new  IllegalStateException( String.format("MASHROOM-DB:  ** JDBC  CONFIG **  data  source  ( %s )  exists.",dataSourceName) );
		}
		
		Properties  properties  = new  Properties();
		
		properties.put( "jdbc."+dataSourceName+".driverClass",driverClassName );
		
		properties.put( "jdbc."+dataSourceName+".jdbcUrl",jdbcUrl );
		
		if( user != null )
		{
			properties.put( "jdbc."+dataSourceName+".user",  user );
		}
		if( password != null )
		{
			properties.put( "jdbc."+dataSourceName+".password",      password );
		}
		if( minPoolSize != null )
		{
			properties.put( "jdbc."+dataSourceName+".minPoolSize",minPoolSize );
		}
		if( preferredTestQuery != null )
		{
			properties.put( "jdbc."+dataSourceName+".preferredTestQuery",preferredTestQuery );
		}
		if( maxPoolSize != null )
		{
			properties.put( "jdbc."+dataSourceName+".maxPoolSize",maxPoolSize );
		}
		if( idleConnectionTestPeriod != null )
		{
			properties.put( "jdbc."+dataSourceName+".idleConnectionTestPeriod" ,  idleConnectionTestPeriod );
		}
	}
	
	public  static  Properties  getProperties(String dataSourceName)
	{
		if( PROPERTIES.containsKey(dataSourceName) )
		{
			return  PROPERTIES.get(dataSourceName );
		}
		
		throw  new  IllegalStateException( String.format("DB:  ** JDBC  CONFIG **  data  source  ( %s )  is  not  configured  in  jdbc.properties",dataSourceName) );
	}
}