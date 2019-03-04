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

import  cc.mashroom.config.Properties;
import  cc.mashroom.db.config.JDBCConfig;
import  cc.mashroom.db.connection.ConnectionPool;
import  cc.mashroom.db.connection.DefaultsConnectionPool;
import  cc.mashroom.db.connection.SquirrelConnectionPool;

public  class  DataSourceBuilder
{
	public  final  static  ConnectionPool  build( String  dataSourceName )  throws  Exception
	{
		Properties  properties = JDBCConfig.getProperties( dataSourceName );
		
		if( properties == null )
		{
			throw  new  IllegalArgumentException( String.format("DB:  ** DATASOURCE  BIND **  properties  of  data  source  ( %s )  is  not  found  in  jdbc.properties",dataSourceName) );
		}
		
		return  System.getProperty("java.runtime.name").toLowerCase().contains("android") ? new  SquirrelConnectionPool(properties) : new  DefaultsConnectionPool( dataSourceName,properties );
	}
}