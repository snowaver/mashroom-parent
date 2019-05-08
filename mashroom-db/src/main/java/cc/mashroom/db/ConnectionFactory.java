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

import  java.sql.SQLException;

import  com.mchange.v2.c3p0.ComboPooledDataSource;

import  cc.mashroom.db.config.JDBCConfig;
import  cc.mashroom.db.connection.Connection;
import  cc.mashroom.db.connection.ConnectionPool;
import  cc.mashroom.util.ObjectUtils;
import  cc.mashroom.util.StringUtils;
import  cc.mashroom.util.collection.map.ConcurrentHashMap;
import  cc.mashroom.util.collection.map.Map;
import  cc.mashroom.util.stream.Consumer;
import  cc.mashroom.util.stream.Stream;

public  class  ConnectionFactory
{
	private  static  Map<String,ConnectionPool>  DATA_SOURCES = new  ConcurrentHashMap<String,ConnectionPool>();
		
	public  static  void  stop()
	{
		Stream.forEach( DATA_SOURCES.values(),new  Consumer<ConnectionPool>(){public  void  consume(ConnectionPool  dataSource)  throws  Exception{ ObjectUtils.cast(dataSource,ComboPooledDataSource.class).close(); }} );
	}
	
	public  static  Connection  getConnection(String  dataSourceName,boolean  autoClosable )throws  SQLException
	{
		if( StringUtils.isBlank(dataSourceName) )
		{
			throw  new  IllegalArgumentException( "MASHROOM-DB:  ** CONNECTION  FACTORY **  data  source  name  should  not  be  blank." );
		}
		
		Connection  connection = ConnectionThreadReference.get( dataSourceName );

		if( connection == null )
		{
			connection = new  Connection( getConnectionPool( dataSourceName ).getConnection(), dataSourceName );
		}
		
		if( !autoClosable )
		{
			ConnectionThreadReference.set( dataSourceName,connection );
		}
		
		return  connection;
	}
	
	private  static  ConnectionPool  getConnectionPool(       final  String  dataSourceName )
	{
		if( StringUtils.isBlank(dataSourceName) )
		{
			throw  new  IllegalArgumentException( "MASHROOM-DB:  ** CONNECTION  FACTORY **  data  source  name  should  not  be  blank." );
		}
		
		return  DATA_SOURCES.computeIfLackof( dataSourceName,new  Map.Computer<String,ConnectionPool>(){public  ConnectionPool  compute(String  key)  throws  Exception{ return  DataSourceBuilder.build(dataSourceName); }} );
	}
		
	public  static  void  setDataSourceLocator( final  DataSourceLocator  dataSourceLocator )
	{
		DATA_SOURCE_LOCATOR  = dataSourceLocator;
	}
	
	static  DataSourceLocator      DATA_SOURCE_LOCATOR;
		
	public  static  Connection  getConnection( String  dataSourceName )  throws  SQLException
	{
		return  getConnection( dataSourceName , true );
	}
		
	public  static  void  initialize()  throws  SQLException
	{
		Stream.forEach( JDBCConfig.getProperties().keySet(),new  Consumer<String>(){public  void  consume(String  key)  throws  Exception{ ConnectionFactory.getConnection(key.toString()).close(); }} );
	}
	

}