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
import  java.util.concurrent.BlockingQueue;

import  cc.mashroom.db.common.Db;
import  cc.mashroom.xcache.atomic.XAtomicLong;
import  cc.mashroom.xcache.remote.RemoteCallable;

public  interface  CacheFactoryStrategy
{
	public  String  getLocalNodeId();
	/**
	 *  get  the  atomic  long  for  the  special  name.
	 *  @param  name  name  of  the  atomic  long.
	 *  @param  createIfAbsent  true  to  create  a  new  atomic  long  ( initial  value  is  zero )  for  the  name  if  absent.
	 *  @return the  atomic  long  for  the  name,  may  be  null  if  the  atomic  long  for  the  name  is  absent  and  parameter  createIfAbsent  is  false.
	 */
	public  XAtomicLong  atomicLong( String  name,boolean  createIfAbsent );
	
	public  <E>  BlockingQueue<E>  queue( String  name );
	
	public  <R>  R  tx(   int  transactionIsolationLevel,Db.Callback  callback )  throws  Exception;
	
	public  List<XClusterNode>  getClusterNodes();
	
	public  <K,V>  XKeyValueCache<K,V>  getOrCreateKeyValueCache( String  name);
	
	public  XMemTableCache  getOrCreateMemTableCache( String  name );
	
	public  <R>  R  call( RemoteCallable  <R>  callable, List<String>  clusterNodeIds );
}