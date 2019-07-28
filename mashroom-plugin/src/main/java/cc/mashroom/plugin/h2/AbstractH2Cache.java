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
package cc.mashroom.plugin.h2;

import  java.util.concurrent.locks.Lock;
import  java.util.concurrent.locks.ReentrantLock;

import  cc.mashroom.util.collection.map.ConcurrentHashMap;
import  cc.mashroom.util.collection.map.Map;
import  cc.mashroom.xcache.XCacheStrategy;
import  lombok.NoArgsConstructor;

@NoArgsConstructor

public  class  AbstractH2Cache<K, V>  implements  XCacheStrategy<K,V>
{
	protected  static  final  Map<Object,ReentrantLock>  LOCKERS = new  ConcurrentHashMap<Object,ReentrantLock>();
	
	public  Lock  getLock(  K  key )
	{
		if( LOCKERS.containsKey(key) && !LOCKERS.get(key).isHeldByCurrentThread() )
		{
			return   null;
		}
		return  LOCKERS.computeIfLackof( key,new  Map.Computer<Object, ReentrantLock>(){public  ReentrantLock  compute(Object  key)  throws  Exception{return  new  ReentrantLock();}} );
	}
}