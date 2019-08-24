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
package cc.mashroom.db.common;

import  cc.mashroom.db.ConnectionManager;
import  cc.mashroom.db.ConnectionThreadReference;
import  cc.mashroom.db.connection.Connection;

public  class  Db
{
	public  static  <T>  T  tx( String  dataSourceName,int  transactionIsolationLevel,Callback  callback )  throws  RuntimeException
	{
		try
		{
			try( Connection  connection = ConnectionManager.INSTANCE.getConnection(dataSourceName,false).setAutoCommit(transactionIsolationLevel == java.sql.Connection.TRANSACTION_NONE) )
			{
				try
				{
					if( transactionIsolationLevel != java.sql.Connection.TRANSACTION_NONE )  connection.setTransactionIsolation( transactionIsolationLevel );
					
					T  returned  = (T)  callback.execute( connection );
					
					if( transactionIsolationLevel != java.sql.Connection.TRANSACTION_NONE )  connection.commit(  );
					
					return  returned;
				}
				catch( Throwable  e )
				{
					if( transactionIsolationLevel != java.sql.Connection.TRANSACTION_NONE )  connection.rollback();
					
					throw    e;
				}
				finally
				{
					ConnectionThreadReference.remove( dataSourceName );
				}
			}
		}
		catch(   Throwable  e )
		{
			throw  new  RuntimeException( e );
		}
	}
	
	public  interface  Callback
	{
		public  Object  execute( Connection  connection )  throws  Throwable;
	}
}