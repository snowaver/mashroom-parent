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

import  java.util.Iterator;
import  java.util.LinkedList;
import  java.util.List;
import  java.util.concurrent.locks.Lock;

import  org.apache.ignite.cache.query.FieldsQueryCursor;
import  org.apache.ignite.cache.query.SqlFieldsQuery;

import  cc.mashroom.util.CollectionUtils;
import  cc.mashroom.util.collection.map.HashMap;
import  cc.mashroom.util.collection.map.Map;
import  cc.mashroom.xcache.XCache;
import  lombok.Setter;

public  class  IgniteCache<K,V>  implements  XCache<K,V>
{
	@Setter
	private  org.apache.ignite.IgniteCache  xcache;
	/*
	private  final  org.slf4j.Logger  logger= LoggerFactory.getLogger( IgniteCache.class );
	*/
	public  IgniteCache(  org.apache.ignite.IgniteCache<K,V>  xcache )
	{
		setXcache( xcache );
	}
	
	public  V  get( K  key )
	{
		return  (V)  xcache.get( key );
	}
	
	public  Lock  getLock( K  key )
	{
		return  xcache.lock( key );
	}
	
	public  V  get( K  key,Class<V>  clazz )
	{
		return  (V)  xcache.get( key );
	}
			
	public  boolean  put(  K  key,V  value )
	{
		xcache.put(  key , value );
		
		return  true;
	}
	
	public  boolean  remove(   K  key )
	{
		return  xcache.remove(   key );
	}
	
	private  Map<String,Object>  fillColumns( Map<String,Object>  record,FieldsQueryCursor<List<Object>>  cursor,List<Object>  values )
	{
		for( int  i = 0;i <= values.size()- 1;i = i+ 1 )
		{
			record.addEntry( cursor.getFieldName(i),values.get( i ) );
		}
		
		return  record;
	}
		
	public  boolean  update(    String  sql,Object...  params )
	{
		return  !xcache.query(new  SqlFieldsQuery(sql).setArgs(params)).getAll().isEmpty();
	}
	
	public  Map<String,Object>  getOne(String  sql,Object...  params )
	{
		return  CollectionUtils.getFirst( search(sql,params) );
	}
	
	public  List<Map<String,Object>>  search( String  sql , Object...  params )
	{
		List<Map<String,Object>>  list = new  LinkedList<Map<String,Object>>();
		
		try( FieldsQueryCursor<List<Object>>  cursor = xcache.query(new  SqlFieldsQuery(sql).setArgs(params)) )
		{
			for( Iterator<List<Object>>  iterator = cursor.iterator();iterator.hasNext(); )  { list.add(fillColumns(new HashMap<String,Object>(),cursor,iterator.next())); }  return  list;
		}
	}
}