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

import  cc.mashroom.db.connection.Connection;
import  cc.mashroom.util.collection.map.ConcurrentHashMap;

public  class  ConnectionThreadReference
{
	private  final  static  ThreadLocal<ConcurrentHashMap<String,Connection>>  CONNECTIONS = new  ThreadLocal<ConcurrentHashMap<String,Connection>>();
	
	public  static  void  remove(    String  dataSourceName )
	{
		if( CONNECTIONS.get()  != null )
		{
			CONNECTIONS.get().remove( dataSourceName );
		}
	}
	
	public  static  void  set( String  dataSourceName,Connection  connection )
	{
		if( CONNECTIONS.get()  == null )
		{
			CONNECTIONS.set( new  ConcurrentHashMap<String,Connection>().addEntry(dataSourceName,connection) );
		}
		else
		{
			CONNECTIONS.get().addEntry( dataSourceName,connection );
		}
	}
	
	public  static  Connection  get( String  dataSourceName )
	{
		return  CONNECTIONS.get() == null ? null : CONNECTIONS.get().get( dataSourceName );
	}
}