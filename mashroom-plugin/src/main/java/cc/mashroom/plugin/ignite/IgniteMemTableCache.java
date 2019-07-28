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

import  cc.mashroom.util.collection.map.HashMap;
import  cc.mashroom.util.collection.map.Map;
import  cc.mashroom.xcache.XMemTableCache;
import  lombok.Setter;

public  class  IgniteMemTableCache<K,V>  implements  XMemTableCache<K,V>
{
	@Setter
	private  org.apache.ignite.IgniteCache     xcache;
	/*
	private  final  org.slf4j.Logger  logger= LoggerFactory.getLogger( IgniteCache.class );
	*/
	public  IgniteMemTableCache(  org.apache.ignite.IgniteCache<K,V>   xcache )
	{
		this.xcache= xcache;
	}
	
	public  Lock  getLock( K  key )
	{
		return  this.xcache.lock( key );
	}
	
	public  boolean  update( String  sql,Object...  params )
	{
		return  !xcache.query(new  SqlFieldsQuery(sql).setArgs(params)).getAll().isEmpty();
	}
	
	private  Map<String,Object>  fillColumns( Map<String,Object>  record,FieldsQueryCursor<List<Object>>  cursor,List<Object>  values )
	{
		for( int  i = 0;i <= values.size()-1;i = i+1 )
		{
			record.addEntry( cursor.getFieldName(i),values.get(i) );
		}
		
		return  record;
	}
	
	public  V  lookupOne(    String  sql,Object...  params )
	{
		List<V>  rcds = lookup( sql,params );
		
		if( rcds.size() >= 2 )
		{
			throw  new  IllegalStateException( String.format("MASHROOM-PLUGIN:  ** IGNITE  MEM  TABLE  CACHE **  unique  constrains,  but  found  %d",rcds.size()) );
		}
		
		return  rcds.isEmpty() ? null : rcds.get( 0 );
	}
	
	public  List<V>  lookup( String  sql,Object...  params )
	{
		List<Map<String,Object>>  list = new  LinkedList<Map<String,Object>>();
		
		try( FieldsQueryCursor<List<Object>>  cursor =xcache.query(new  SqlFieldsQuery(sql).setArgs(params)) )
		{
			for( Iterator<List<Object>>  iterator = cursor.iterator();iterator.hasNext(); )  list.add( fillColumns(new HashMap<String,Object>(),cursor,iterator.next()) );  return  (List<V>)  list;
		}
	}
}