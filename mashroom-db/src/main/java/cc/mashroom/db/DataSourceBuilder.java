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
package cc.mashroom.db;

import  org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import  cc.mashroom.config.Properties;
import  cc.mashroom.db.connection.ConnectionPool;
import  cc.mashroom.db.connection.Connection;
import  cc.mashroom.db.connection.ConnectionFactory;

public  class  DataSourceBuilder
{
	public  final  static  ConnectionPool  build( String  dataSourceName,Properties  properties )  throws  Exception
	{
		if( properties == null )
		{
			throw  new  IllegalArgumentException( String.format("MASHROOM-DB:  ** DATASOURCE  BUILDER **  properties  of  data  source  ( %s )  is  not  found  in  jdbc.properties",dataSourceName) );
		}
		
		GenericObjectPoolConfig<Connection>  poolConfig = new  GenericObjectPoolConfig<Connection>();
		
		poolConfig.setJmxEnabled( false );
		
		poolConfig.setMinIdle( properties.getInt("minPoolSize",2) );
		
		poolConfig.setMaxIdle( properties.getInt("maxPoolSize",4) );
		
		poolConfig.setTimeBetweenEvictionRunsMillis( properties.getInt("idleConnectionTestPeriod",120*1000) );
		
		return  new  ConnectionPool( dataSourceName, new  ConnectionFactory(dataSourceName,properties),poolConfig );
	}
}