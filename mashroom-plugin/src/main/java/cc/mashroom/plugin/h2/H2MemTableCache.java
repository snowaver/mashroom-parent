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

@NoArgsConstructor

public  class  H2MemTableCache<K,V>  extends  AbstractH2Cache<K,V>  implements  XMemTableCache<K,V>
{
	public  H2MemTableCache( String  name,H2MemTableCacheRepository  repository )
	{
		this.name  = name;
	}
	@Setter
	private  String  name;
	@Setter
	private  H2MemTableCacheRepository   repository;
	
	public  V  lookupOne(    String  sql,Object...  params )
	{
		List<V>  rcds = lookup( sql,params );
		
		if( rcds.size() >= 2 )
		{
			throw  new  IllegalStateException( String.format("MASHROOM-PLUGIN:  ** H2  MEM  TABLE  CACHE **  unique  constrains,  but  found  %d",rcds.size()) );
		}
		
		return  rcds.isEmpty() ? null : rcds.get(0);
	}
	
	public  List<V>  lookup( String  sql,Object...  params )
	{
		return  ObjectUtils.cast( repository.lookup( sql , params ) );
	}
	
	public  boolean  update( String  sql,Object...  params )
	{
		return  repository.update(sql, params) >= 0;
	}
}