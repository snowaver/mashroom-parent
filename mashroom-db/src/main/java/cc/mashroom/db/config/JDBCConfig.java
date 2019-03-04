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

import  java.util.Map.Entry;

import  cc.mashroom.config.Config;
import  cc.mashroom.config.Properties;
import  cc.mashroom.util.collection.map.ConcurrentHashMap;
import  cc.mashroom.util.collection.map.Map;

public  class  JDBCConfig
{
	private  final  static  Map<String,Properties>  properties  = new  ConcurrentHashMap<String,Properties>();

	static
	{
		for( Entry<Object,Object>  entry : Config.use("jdbc.properties").entrySet() )
		{
			properties.computeIfLackof(String.valueOf(entry.getKey()).split("\\.")[1],new  Map.Computer<String,Properties>(){public  Properties  compute(String  key)  throws  Exception{ return  new  Properties(); }}).put( String.valueOf(entry.getKey()).split("\\.")[2],entry.getValue() );
		}
	}
	
	public  static  void  addDataSource( Map<String,Object>  dataSource )
	{
		for( Entry<String,Object>  entry : dataSource.entrySet() )
		{
			properties.computeIfLackof(String.valueOf(entry.getKey()).split("\\.")[1],new  Map.Computer<String,Properties>(){public  Properties  compute(String  key)  throws  Exception{ return  new  Properties(); }}).put( String.valueOf(entry.getKey()).split("\\.")[2],entry.getValue() );
		}
	}
	
	public  static  Map<String,Properties>  getProperties()
	{
		return  properties;
	}
	
	public  static  Properties  getProperties(   String  dataSourceName )
	{
		if( properties.containsKey(dataSourceName) )
		{
			return  properties.get(dataSourceName );
		}
		
		throw  new  IllegalStateException( String.format("DB:  ** JDBC  CONFIG **  data  source  ( %s )  is  not  configured  in  jdbc.properties",dataSourceName) );
	}
}