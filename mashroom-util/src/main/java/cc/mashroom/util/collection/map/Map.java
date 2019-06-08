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
package cc.mashroom.util.collection.map;

import  org.joda.time.DateTimeZone;

public  interface  Map<K,V>  extends  java.util.Map<K,V>
{
	public  Map<K,V>  valuesToLong(K  ...  keys );
	
	public  Map<K,V>  valuesToTimestamp( K  ...  keys );
	/**
	 *  replace  the  computeIfAbsent  method  while  considering  jdk7  or  lower  version  dose  not  contains  it.
	 */
	public  V  computeIfLackof( K  key,Computer<K,V>  computer );
	
	public  Map<K,V>  addEntries(  java.util.Map<K,V>  map );
	
	public  Map<K,V>  valuesToTimestamp( String  datetimeFormat,DateTimeZone  datetimeZone,K  ...  keys );
	
	public  interface  Computer  < K,V >
	{
		public  V  compute( K  key )  throws  Exception;
	}
	
	public  Map<K,V>  addEntry( K  key,V  value );
		
	public  V  get(K  key,V  defValue );
	
	public  Boolean  getBoolean(   K  key );
	
	public  String  getString( K  key );
	
	public  Long  getLong( K  key );
	
	public  Short  getShort(   K  key );
		
	public  Integer  getInteger(   K  key );
	
	public  Map<K,V>  removeEntry( K  key );
	
	public  Double  getDouble( K  key );
}