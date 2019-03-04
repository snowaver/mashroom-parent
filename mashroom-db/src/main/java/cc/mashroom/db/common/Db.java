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

import  cc.mashroom.db.ConnectionFactory;
import  cc.mashroom.db.ConnectionThreadReference;
import  cc.mashroom.db.connection.Connection;

public  class  Db
{
	public  static  <T>  T  tx( String  dataSourceName,int  transactionLevel,Callback  callback )  throws  Exception
	{
		try( Connection  connection = ConnectionFactory.getConnection(dataSourceName,false).setAutoCommit(transactionLevel == java.sql.Connection.TRANSACTION_NONE) )
		{
			try
			{
				if( transactionLevel != java.sql.Connection.TRANSACTION_NONE )  connection.setTransactionIsolation( transactionLevel );
				
				T  returned  = (T)  callback.execute( connection );
				
				if( transactionLevel != java.sql.Connection.TRANSACTION_NONE )  connection.commit(  );
				
				return  returned;
			}
			catch( Throwable  e )
			{
				if( transactionLevel != java.sql.Connection.TRANSACTION_NONE )  connection.rollback();
				
				throw  new  RuntimeException(   e.getMessage(),e );
			}
			finally
			{
				ConnectionThreadReference.remove( dataSourceName );
			}
		}
	}
	
	public  interface    Callback
	{
		public  Object  execute( Connection  connection )   throws  Throwable;
	}
}