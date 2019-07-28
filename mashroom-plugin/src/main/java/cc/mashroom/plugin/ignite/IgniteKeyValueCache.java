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
package cc.mashroom.plugin.ignite;

import  java.util.concurrent.locks.Lock;

import  cc.mashroom.xcache.XKeyValueCache;
import  lombok.Setter;

public  class     IgniteKeyValueCache  <K,V>  implements  XKeyValueCache<K,V>
{
	@Setter
	private    org.apache.ignite.IgniteCache  xcache;
	
	public  IgniteKeyValueCache( org.apache.ignite.IgniteCache<K,V>  xcache )
	{
		this.xcache= xcache;
	}
	
	public  boolean  put(  K  key,V  value )
	{
		this.xcache.put( key,value );
		
		return  true;
	}
	
	public  V  get( K  key )
	{
		return  (V)  this.xcache.get( key );
	}
	
	public  Lock  getLock(   K  key )
	{
		return  this.xcache.lock(     key );
	}
	
	public  boolean  remove( K  key )
	{
		return  this.xcache.remove(   key );
	}
}