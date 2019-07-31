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

import  cc.mashroom.util.ObjectUtils;
import  cc.mashroom.xcache.XMemTableCache;
import  lombok.NoArgsConstructor;
import  lombok.Setter;
import lombok.experimental.Accessors;

@NoArgsConstructor

public  class  H2MemTableCache      extends  AbstractH2Cache     implements  XMemTableCache
{
	public  H2MemTableCache(  String  name,H2MemTableCacheRepository  repository )
	{
		setName(name).setRepository(  repository );
	}
	
	@Accessors( chain = true )
	@Setter
	private  String  name;
	@Accessors( chain = true )
	@Setter
	private  H2MemTableCacheRepository  repository;
	
	public  <T>  T  lookupOne(    Class<T>  resultBeanClazz,String  sql,Object...  params )
	{
		List<T>  rcs = lookup( resultBeanClazz,sql,params );
		
		if( rcs.size()  >= 2 )
		{
			throw  new  IllegalStateException( String.format("MASHROOM-PLUGIN:  ** H2  MEM  TABLE  CACHE **  unique  constrains,  but  found  %d",rcs.size()) );
		}
		
		return   rcs.isEmpty()  ? null: rcs.get(0);
	}
	
	public  boolean  update( String  sql,Object...  params )
	{
		return   repository.update(sql,params)>= 0;
	}
	
	public  <T>  List<T>  lookup( Class<T>  resultBeanClazz,String  sql,Object...  params )
	{
		return  ObjectUtils.cast( repository.lookup(resultBeanClazz,sql,params) );
	}
}