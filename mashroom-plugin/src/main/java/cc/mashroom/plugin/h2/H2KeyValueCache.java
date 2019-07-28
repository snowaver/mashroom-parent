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

import  cc.mashroom.util.collection.map.ConcurrentHashMap;
import  cc.mashroom.util.collection.map.Map;
import  cc.mashroom.xcache.XKeyValueCache;
import  lombok.NoArgsConstructor;
import  lombok.Setter;

@NoArgsConstructor

public  class  H2KeyValueCache<K,V>  extends  AbstractH2Cache<K,V>  implements  XKeyValueCache<K,V>
{
	public  H2KeyValueCache(  String  name )
	{
		this.name  = name;
	}
	
	private  Map<K,V>  cache   = new  ConcurrentHashMap<K,V>();
	
	public  V  get(K  key)
	{
		return  cache.get( key );
	}
	@Setter
	private  String  name;
	
	public  boolean  put(  K  key,V  value )
	{
		if( LOCKERS.containsKey(key ) && !LOCKERS.get(key).isHeldByCurrentThread() )
		{
			return  false;
		}
		this.cache.put( key ,value );
		
		return  true;
		/*
		throw  new  UnsupportedOperationException( "MASHROOM-PLUGIN:  ** H2  CACHE **  this  operation  is  not  supported." );
		*/
	}

	public  boolean  remove( K  key )
	{
		if( LOCKERS.containsKey(key ) && !LOCKERS.get(key).isHeldByCurrentThread() )
		{
			return  false;
		}
		cache.remove(key);
		
		return  true;
		/*
		throw  new  UnsupportedOperationException( "MASHROOM-PLUGIN:  ** H2  CACHE **  this  operation  is  not  supported." );
		*/
	}
}