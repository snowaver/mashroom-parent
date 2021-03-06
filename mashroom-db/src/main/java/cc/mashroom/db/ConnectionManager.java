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

import  cc.mashroom.config.Config;
import  cc.mashroom.config.Properties;
import  cc.mashroom.db.connection.Connection;
import  cc.mashroom.db.connection.ConnectionPool;
import  cc.mashroom.db.util.DataSourceUtils;
import  cc.mashroom.util.collection.map.ConcurrentHashMap;
import  cc.mashroom.util.collection.map.Map;
import  lombok.AccessLevel;
import  lombok.Getter;
import  lombok.NoArgsConstructor;
import  lombok.NonNull;
import  lombok.Setter;
import  lombok.experimental.Accessors;

@NoArgsConstructor( access= AccessLevel.PRIVATE )

public  class  ConnectionManager
{
	private  Map<String,ConnectionPool>   connectionPools  = new  ConcurrentHashMap<String, ConnectionPool>();
	@Accessors(   chain = true )
	@Setter
	@Getter
	private  DataSourceLocator dataSourceLocator;
	
	private  Map<String,Properties>  dataSourceProperties  = new  ConcurrentHashMap<String,     Properties>();

	public   void    stop()
	{
		for( ConnectionPool  connectionPool     : this.connectionPools.values() )      connectionPool.close();
		
		connectionPools.clear();
	}
	
	public  final  static  ConnectionManager  INSTANCE= new  ConnectionManager();
	
	public  void  addDataSource( @NonNull  String  driverClassName,@NonNull  String  dataSourceName,@NonNull  String  jdbcUrl,String  user,String  password,Integer  minPoolSize,Integer  maxPoolSize,Long  idleConnectionTestPeriod,String  preferredTestQuery )  throws  Exception
	{
		addDataSource( driverClassName,dataSourceName,jdbcUrl,user,password,minPoolSize,maxPoolSize,idleConnectionTestPeriod,preferredTestQuery,false );
	}
	
	public  void  addDataSource( @NonNull  String  driverClassName,@NonNull  String  dataSourceName,@NonNull  String  jdbcUrl,String  user,String  password,Integer  minPoolSize,Integer  maxPoolSize,Long  idleConnectionTestPeriod,String  preferredTestQuery,boolean  ignoreIfExists )  throws  Exception
	{
		if( dataSourceProperties.containsKey(dataSourceName) && !ignoreIfExists )
		{
			throw  new  IllegalStateException( String.format("MASHROOM-DB:  ** JDBC  CONFIG **  data  source  ( %s )  exists.",dataSourceName) );
		}
		
		this.dataSourceProperties.put( dataSourceName,DataSourceUtils.createDataSourceProperties(driverClassName,dataSourceName,jdbcUrl,user,password,minPoolSize,maxPoolSize,idleConnectionTestPeriod,preferredTestQuery) );
		
		try
		{
			getConnection( dataSourceName ).close();
		}
		catch(    Exception  e )
		{
			e.printStackTrace();   dataSourceProperties.remove( dataSourceName );  throw  e;
		}
	}
	/**
	 *  use  the  connection  in  connection  thread  reference  or  create  a  new  one  if  no  connection  held  by  connection  thread  reference.  the  connection  will  be  held  by  connection  thread  reference  if  autoCloseable  is  false.
	 */
	public  Connection  getConnection(@NonNull  String  dataSourceName,boolean  autoClose  ) throws  Exception
	{
		Connection  connection = ConnectionThreadReference.get( dataSourceName );

		if( connection == null )
		{
			connection =  this.getConnectionPool( dataSourceName).borrowObject();
		}
		
		if( !   autoClose )
		{
			ConnectionThreadReference.set(dataSourceName,connection );
		}
		
		return  connection;
	}
	
	public  Connection  getConnection(String  dataSourceName )  throws  Exception
	{
		return  getConnection(dataSourceName, true);
	}
	
	public  void  removeDataSource(   String  dataSourceName )
	{
		ConnectionPool   connectionPool     = this.connectionPools.remove( dataSourceName );
		
		if( connectionPool    != null )connectionPool.close();
	}
	
	public  void  initialize(         Object  ... parameters )  throws  Exception
	{
		for( Entry<Object,Object>  entry : Config.use(parameters == null || parameters.length == 0 ? "jdbc.properties" : (String)  parameters[0]).entrySet() )
		{
			this.dataSourceProperties.computeIfLackof(String.valueOf(entry.getKey()).split("\\.")[1],new  Map.Computer<String,Properties>(){public  Properties  compute(String  key)  throws  Exception{ return  new  Properties(); }}).put( String.valueOf(entry.getKey()).split("\\.")[2],entry.getValue() );
		}
		
		for( String  dataSourceName : dataSourceProperties.keySet()  )  getConnection(dataSourceName).close();
	}
	/**
	 *  get  the  connection  pool  or  create  a  new  one  by  datasource  name
	 */
	public  ConnectionPool  getConnectionPool( @NonNull  final  String  dataSourceName     )
	{
		return  connectionPools.computeIfLackof( dataSourceName,new  Map.Computer<String,ConnectionPool>(){public  ConnectionPool  compute(String  key)  throws  Exception{ return  DataSourceBuilder.build(dataSourceName,dataSourceProperties.get(dataSourceName)); }} );
	}
}