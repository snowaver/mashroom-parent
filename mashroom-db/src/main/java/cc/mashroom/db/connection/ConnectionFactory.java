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
package cc.mashroom.db.connection;

import  java.sql.DriverManager;
import  java.sql.SQLException;

import  org.apache.commons.pool2.BasePooledObjectFactory;
import  org.apache.commons.pool2.PooledObject;
import  org.apache.commons.pool2.impl.DefaultPooledObject;

import  cc.mashroom.config.Properties;
import  lombok.Getter;
import  lombok.Setter;
import  lombok.experimental.Accessors;

public  class  ConnectionFactory  extends  BasePooledObjectFactory<Connection>
{
	public  ConnectionFactory( String  dataSourceName,Properties  properties )  throws  ClassNotFoundException
	{
		Class.forName(     properties.getProperty("driverClass") );
		
		this.setDataSourceName(dataSourceName).setProperties(    properties );
	}
	
	public  PooledObject<Connection>  wrap(Connection  connection )
	{
		return  new  DefaultPooledObject<Connection>( connection );
	}
	
	@Accessors( chain=true )
	@Getter
	@Setter
	private  String  dataSourceName;
	@Accessors( chain=true )
	@Getter
	@Setter
	private  ConnectionPool  connectionPool;
	@Accessors( chain=true )
	@Getter
	@Setter
	private  Properties  properties;
	
	public  Connection  create()    throws  Exception
	{
		return  new  Connection( this.connectionPool,DriverManager.getConnection(this.properties.getProperty("jdbcUrl"),this.properties) );
	}
	
	public  boolean  validateObject(  PooledObject<Connection>  pooledObject )
	{
		try
		{
			pooledObject.getObject().prepareStatement(this.properties.getProperty("preferredTestQuery","SELECT  2")).executeQuery();
		}
		catch( SQLException  sqlex )
		{
			sqlex.printStackTrace();
			
			return    false;
		}
		
		return  super.validateObject( pooledObject );
	}
}