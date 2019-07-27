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
package cc.mashroom.xcache;

import  java.util.List;

import  cc.mashroom.db.common.Db;

public  class  CacheFactory
{
	public  static  void  setStrategy( CacheFactoryStrategy  strategy )
	{
		CacheFactory.strategy   = strategy;
	}
	
	public  static  List<XClusterNode>  getClusterNodes()
	{
		return  strategy.getClusterNodes();
	}
		
	private  static  CacheFactoryStrategy  strategy;
	
	public  static  long  getNextSequence( String  name )
	{
		return  strategy.getNextSequence(    name );
	}
	
	public  static  <K,V>  XKeyValueCache<K,V>  getOrCreateKeyValueCache( String  name )
	{
		return  strategy.getOrCreateKeyValueCache(name );
	}
	
	public  static  <K,V>  XMemTableCache<K,V>  getOrCreateMemTableCache( String  name )
	{
		return  strategy.getOrCreateMemTableCache(name );
	}
	
	public  static  <V>  V  call( RemoteCallable <V>  callable , List<String>  clusterNodeIds )
	{
		return  strategy.call( callable,clusterNodeIds );
	}
	
	public  static  <T>  T  tx( int  transactionIsolationLevel,  Db.Callback  callback )  throws  Exception
	{
		return  strategy.tx(   transactionIsolationLevel,   callback );
	}
}