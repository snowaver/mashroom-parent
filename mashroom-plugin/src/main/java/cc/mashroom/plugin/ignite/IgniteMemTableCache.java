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

import  java.util.List;
import  java.util.concurrent.locks.Lock;

import  org.apache.ignite.cache.query.FieldsQueryCursor;
import  org.apache.ignite.cache.query.SqlFieldsQuery;

import  cc.mashroom.xcache.XMemTableCache;
import  lombok.Setter;
import  lombok.SneakyThrows;

public  class  IgniteMemTableCache  implements  XMemTableCache
{
	@Setter
	private  org.apache.ignite.IgniteCache     xcache;
	/*
	private  final  org.slf4j.Logger  logger= LoggerFactory.getLogger( IgniteCache.class );
	*/
	public  IgniteMemTableCache( org.apache.ignite.IgniteCache  xcache )
	{
		this.xcache= xcache;
	}
	
	public  Lock  getLock( Object  key )
	{
		return  this.xcache.lock( key );
	}
	
	public  boolean  update(   String  sql,Object...  params )
	{
		return  !xcache.query(new  SqlFieldsQuery(sql).setArgs(params)).getAll().isEmpty();
	}
	
	public  <T>  T  lookupOne(    Class<T>  resultBeanClazz,String  sql,Object...  params )
	{
		List<T>  rcs = lookup( resultBeanClazz, sql, params );
		
		if( rcs.size() >= 2 )
		{
			throw  new  IllegalStateException( String.format("MASHROOM-PLUGIN:  ** IGNITE  MEM  TABLE  CACHE **  unique  constrains,  but  found  %d",rcs.size()) );
		}
		
		return  rcs.isEmpty() ? null : rcs.get( 0 );
	}
	
	@SneakyThrows
	public  <T>  List<T>  lookup( Class<T>  resultBeanClazz,String  sql,Object...  params )
	{
		try( FieldsQueryCursor<List<Object>>  cursor =xcache.query(new  SqlFieldsQuery(sql).setArgs(params)) )
		{
			return  RecordUtils.list(cursor,resultBeanClazz );
		}
	}
}