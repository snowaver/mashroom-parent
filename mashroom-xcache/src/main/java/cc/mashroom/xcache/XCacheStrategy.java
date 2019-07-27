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
import  java.util.concurrent.locks.Lock;

import  cc.mashroom.util.collection.map.Map;

public  interface  XCache<K,V>
{
	public  V  get( K  key  );
	
	public  V  get( K  key,Class<V>  clazz );
	
	public  boolean  remove( K  key );
	
	public  boolean  put( K  key,V  object );
	
	public  Lock  getLock(   K  key );
	
	public  List<Map<String,Object>>  search( String  sql,Object...  params );
	
	public  Map<String,Object>  getOne( String  sql,Object...  params );
	
	public  boolean  update( String  sql,Object...  params );
}