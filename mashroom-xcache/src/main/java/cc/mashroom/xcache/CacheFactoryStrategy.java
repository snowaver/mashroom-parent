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

public  interface  CacheFactoryStrategy
{
	public  String  getLocalNodeId();
	/**
	 *  increment  the  current  value  and  get.  initial  value  is  zero,  but  don't  reset  the  value  if  reset  initialize  value  is  null.
	 */
	public  long  getNextSequence( String  name  , Long  resetInitializeValue );
	
	public  <R>  R  tx(   int  transactionIsolationLevel,Db.Callback  callback )  throws  Exception;
	
	public  List<XClusterNode>  getClusterNodes();
	
	public  <K,V>  XKeyValueCache<K,V>  getOrCreateKeyValueCache( String  name);
	
	public  XMemTableCache  getOrCreateMemTableCache( String  name );
	
	public  <R>  R  call( RemoteCallable  <R>  callable, List<String>  clusterNodeIds );
}