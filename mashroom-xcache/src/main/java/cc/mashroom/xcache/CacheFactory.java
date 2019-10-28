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
import  cc.mashroom.xcache.atomic.XAtomicLong;
import  lombok.Setter;

public  class  CacheFactory
{	
	public  static  List<XClusterNode>   getClusterNodes()
	{
		return  strategy.getClusterNodes();
	}
	@Setter
	private  static  CacheFactoryStrategy  strategy;

	public  static  XAtomicLong  atomicLong( String  name,boolean  createIfAbsent )
	{
		return  strategy.atomicLong(name,createIfAbsent );
	}
	
	public  static  <K,V>  XKeyValueCache<K,V>  getOrCreateKeyValueCache( String  name )
	{
		return  strategy.getOrCreateKeyValueCache( name );
	}
	
	public  static  String getLocalNodeId()
	{
		return  strategy.getLocalNodeId( );
	}
	
	public  static  XMemTableCache              getOrCreateMemTableCache( String  name )
	{
		return  strategy.getOrCreateMemTableCache( name );
	}
	
	public  static  <T>  T  tx( int  transactionIsolationLevel,  Db.Callback  callback )  throws  Exception
	{
		return  strategy.tx(    transactionIsolationLevel,callback );
	}
	
	public  static  <V>  V  call( RemoteCallable <V>  callable,  List<String>   clusterNodeIds )
	{
		return  strategy.call(  callable,clusterNodeIds );
	}
}