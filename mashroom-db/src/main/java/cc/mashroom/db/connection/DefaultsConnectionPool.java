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

import  java.beans.PropertyVetoException;
import  java.sql.Connection;
import  java.sql.SQLException;

import  com.mchange.v2.c3p0.ComboPooledDataSource;

import  cc.mashroom.config.Properties;

public  class  DefaultsConnectionPool  implements  ConnectionPool
{	
	private  ComboPooledDataSource  c3p0DataSource   = new  ComboPooledDataSource();
	
	public  void  release()
	{
		c3p0DataSource.close();
	}
	
	public  Connection  getConnection()     throws  SQLException
	{
		return  c3p0DataSource.getConnection();
	}
	
	public  DefaultsConnectionPool( String  dsName,Properties  properties )  throws  PropertyVetoException
	{
		c3p0DataSource.setDataSourceName( dsName );
		
		c3p0DataSource.setInitialPoolSize( properties.getInt("initialPoolSize",2) );
		
		c3p0DataSource.setMinPoolSize( properties.getInt("minPoolSize",2) );
		
		c3p0DataSource.setDriverClass( properties.getProperty("driverClass") );

		c3p0DataSource.setMaxPoolSize( properties.getInt("maxPoolSize",8) );
					
		c3p0DataSource.setJdbcUrl(properties.getProperty("jdbcUrl") );
		
		c3p0DataSource.setPassword(properties.getProperty("password") );
		
		c3p0DataSource.setUser(properties.getProperty("user") );
		
		c3p0DataSource.setPreferredTestQuery( properties.getProperty("preferredTestQuery","SELECT  2") );
		
		c3p0DataSource.setIdleConnectionTestPeriod(  properties.getInt("idleConnectionTestPeriod",120) );
	}
}