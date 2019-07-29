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

import  java.util.Map.Entry;

import  javax.annotation.Nonnull;

import  cc.mashroom.db.config.JDBCConfig;
import  cc.mashroom.db.connection.Connection;
import  cc.mashroom.db.connection.ConnectionPool;
import  cc.mashroom.util.StringUtils;
import  cc.mashroom.util.collection.map.ConcurrentHashMap;
import  cc.mashroom.util.collection.map.Map;
import  lombok.AccessLevel;
import  lombok.Getter;
import  lombok.NoArgsConstructor;
import  lombok.Setter;
import  lombok.experimental.Accessors;

@NoArgsConstructor( access= AccessLevel.PRIVATE )

public  class  ConnectionManager
{
	private  Map<String,ConnectionPool>  connectionPools    = new  ConcurrentHashMap<String,ConnectionPool>();
	@Accessors(   chain = true )
	@Setter
	@Getter
	private  DataSourceLocator dataSourceLocator;
	
	public  void  release()
	{
		for( Entry<String,ConnectionPool>  entry:    connectionPools.entrySet() )
		{
			entry.getValue().close();
		}
		
		connectionPools.clear();
	}
	
	public  final  static  ConnectionManager  INSTANCE= new  ConnectionManager();
	/**
	 *  use  the  connection  in  connection  thread  reference  or  create  a  new  one  if  no  connection  held  by  connection  thread  reference.  the  connection  will  be  held  by  connection  thread  reference  if  autoCloseable  is  false.
	 */
	public  Connection  getConnection(@Nonnull  String  dataSourceName,boolean  autoClose )  throws  Exception
	{
		if( StringUtils.isBlank(dataSourceName) )
		{
			throw  new  IllegalArgumentException( "MASHROOM-DB:  ** CONNECTION  FACTORY **  data  source  name  should  not  be  blank." );
		}
		
		Connection  connection = ConnectionThreadReference.get( dataSourceName );

		if( connection == null )
		{
			connection = this.getConnectionPool( dataSourceName ).borrowObject();
		}
		
		if( !   autoClose )
		{
			ConnectionThreadReference.set( dataSourceName,connection );
		}
		
		return  connection;
	}
	
	public  Connection  getConnection(String  dataSourceName )  throws  Exception
	{
		return  getConnection( dataSourceName , true );
	}
	
	public  void  initialize(  Object...  parameters )throws  Exception
	{
		for( String  dataSourceName : JDBCConfig.getDataSourceNames() )
		{
			this.getConnection(dataSourceName).close();
		}
	}
	/**
	 *  get  the  connection  pool  or  create  a  new  one  by  dataSourceName  if  absent
	 */
	public  ConnectionPool  getConnectionPool(  @Nonnull    final  String  dataSourceName )
	{
		return  connectionPools.computeIfLackof( dataSourceName,new  Map.Computer<String,ConnectionPool>(){public  ConnectionPool  compute(String  key)  throws  Exception{ return  DataSourceBuilder.build(dataSourceName); }} );
	}
}