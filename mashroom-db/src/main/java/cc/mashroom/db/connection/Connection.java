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

import  java.sql.PreparedStatement;
import  java.sql.SQLException;
import  java.sql.Statement;

import  com.google.common.collect.Lists;

import  cc.mashroom.db.ConnectionThreadReference;
import  cc.mashroom.db.util.ConnectionUtils;
import  cc.mashroom.util.stream.Consumer;
import  cc.mashroom.util.stream.Stream;
import  lombok.AccessLevel;
import  lombok.AllArgsConstructor;
import  lombok.Setter;
import  lombok.experimental.Accessors;

@AllArgsConstructor

public  class     Connection  implements  AutoCloseable
{
	@Setter( value= AccessLevel.PROTECTED )
	@Accessors( chain=true )
	protected  ConnectionPool    connectionPool;
	@Setter( value= AccessLevel.PROTECTED )
	@Accessors( chain=true )
	protected  java.sql.Connection   connection;
	
	public  Connection  setAutoCommit( boolean    autoCommit )  throws  SQLException
	{
		this.connection.setAutoCommit(    autoCommit );
		
		return  this;
	}
	
	public  Connection  setTransactionIsolation(    int  transactionIsolationLevel )  throws  SQLException
	{
		connection.setTransactionIsolation( transactionIsolationLevel );
		
		return  this;
	}
	
	public  PreparedStatement  prepareStatement( boolean  returnGeneratedKeys,String  sql,Object...   params )  throws  SQLException
	{
		return  ConnectionUtils.prepare( returnGeneratedKeys ? this.connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS) : this.connection.prepareStatement(sql),params );
	}
	
	public  PreparedStatement  prepareStatement( String  sql )  throws  SQLException
	{
		return  this.connection.prepareStatement(sql );
	}
	
	public  void  close()
	{
		if( ConnectionThreadReference.get(this.connectionPool.getDataSourceName()) == null )    connectionPool.returnObject( this );
	}
	
	public  PreparedStatement  prepareStatement( String  sql,   Object...   params )  throws  SQLException
	{
		return  prepareStatement(   false,sql,params );
	}
			
	public  void  commit()  throws  SQLException
	{
		this.connection.commit(  );
	}
	
	public  PreparedStatement  prepareStatement( boolean  returnGeneratedKeys,String  sql,Object[][]  params )  throws  SQLException
	{
		return  ConnectionUtils.prepare( returnGeneratedKeys ? this.connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS) : this.connection.prepareStatement(sql),params );
	}
	
	public  PreparedStatement  prepareStatement( String  sql,   Object[][]  params )  throws  SQLException
	{
		return  prepareStatement(   false,sql,params );
	}

	public  void  rollback()throws  SQLException
	{
		this.connection.rollback();
	}
	
	public  void  runScripts(  String  scripts )    throws  SQLException
	{
		Stream.forEach( Lists.newArrayList(scripts.split("(;\\s*\\r\\n)|(;\\s*\\n)")),new  Consumer<String>(){public  void  consume(String  sqlScript)  throws  Exception{ prepareStatement(sqlScript).executeUpdate(); }} );
	}
}