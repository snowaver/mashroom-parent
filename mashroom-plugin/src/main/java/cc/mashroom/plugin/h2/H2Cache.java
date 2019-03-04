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

import  java.util.List;
import  java.util.concurrent.locks.Lock;
import  java.util.concurrent.locks.ReentrantLock;

import  cc.mashroom.util.CollectionUtils;
import  cc.mashroom.util.ObjectUtils;
import  cc.mashroom.util.collection.map.ConcurrentHashMap;
import  cc.mashroom.util.collection.map.Map;
import  cc.mashroom.xcache.XCache;
import  lombok.NoArgsConstructor;
import  lombok.Setter;

@NoArgsConstructor

public  class  H2Cache<K,V>  implements  XCache<K,V>
{
	public  H2Cache(   String  name )
	{
		this.setName(name );
	}
	
	private  Map<K, V>  cache = new  ConcurrentHashMap<K, V>();
	
	private  ReentrantLock  lock  = new  ReentrantLock( true );
	
	public  V  get( K  key )
	{
		return  cache.get( key );
	}
	@Setter
	private  String    name;
	
	public  Lock  getLock(   K  key )
	{
		lock.lock();    return  lock;
	}
	
	public  V  get( K  key,Class<V>  clazz )
	{
		return  cache.get( key );
		/*
		throw  new  UnsupportedOperationException( "MASHROOM-PLUGIN:  ** H2  CACHE **  this  operation  is  not  supported." );
		*/
	}
	
	public  boolean  put(  K  key,V  value )
	{
		if( !   lock.isLocked() )
		{
			cache.put( key , value );
			
			return  true;
		}
		
		return  false;
		/*
		throw  new  UnsupportedOperationException( "MASHROOM-PLUGIN:  ** H2  CACHE **  this  operation  is  not  supported." );
		*/
	}

	public  Map<String,Object>  getOne( String  sql,Object...  params )
	{
		return  CollectionUtils.getFirst( search(sql,params) );
	}
	
	public  List<Map<String,Object>>  search( String  sql,Object...  params )
	{
		return  ObjectUtils.cast( H2Model.dao.search( sql , params ) );
	}
	
	public  boolean  update(    String  sql,Object...  params )
	{
		if( !   lock.isLocked() )
		{
			return  H2Model.dao.update(sql,params) >= 0;
		}
		
		return  false;
	}
	
	public  boolean  remove( K  key )
	{
		if( !   lock.isLocked() )
		{
			cache.remove(  key );
			
			return  true;
		}
		
		return  false;
		/*
		throw  new  UnsupportedOperationException( "MASHROOM-PLUGIN:  ** H2  CACHE **  this  operation  is  not  supported." );
		*/
	}
}