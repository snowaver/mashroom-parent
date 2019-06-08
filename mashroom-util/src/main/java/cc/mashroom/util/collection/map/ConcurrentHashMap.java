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

import  java.sql.Timestamp;

import  org.joda.time.DateTime;
import  org.joda.time.DateTimeZone;
import  org.joda.time.format.DateTimeFormat;

import  lombok.SneakyThrows;

public  class  ConcurrentHashMap<K,V>  extends  java.util.concurrent.ConcurrentHashMap<K,V>  implements  Map<K,V>
{
	@Override
	public  ConcurrentHashMap<K,V>  addEntry(  K  key,V  value )
	{
		super.put( key,value );
		
		return  this;
	}

	@Override
	public  ConcurrentHashMap<K,V>  addEntries( java.util.Map<K,V>  map )
	{
		super.putAll(    map );
		
		return  this;
	}
	
	@SneakyThrows
	@Override
	public  V  computeIfLackof( K  key,Computer<K,V>  computer )
	{
		synchronized( hashCode()+ "&"+key )
		{
			if( !super.containsKey( key ) )  super.put(    key ,  computer.compute(key) );
		}

		return  super.get(key);
	}
	
	@Override
	public  Map<K,V>  valuesToLong( K  ...  keys )
	{
		for(    K  key : keys )
		{
			V   value   = super.get( key );
			
			if( value != null )         super.put( key,(V)  new  Long(value.toString()) );
		}
		
		return  this;
	}
	
	@Override
	public  ConcurrentHashMap<K,V>  valuesToTimestamp(     K  ...  keys )
	{
		return  valuesToTimestamp( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",DateTimeZone.UTC,keys );
	}
	
	@Override
	public  ConcurrentHashMap<K,V>  valuesToTimestamp( String  datetimeFormat,DateTimeZone  datetimeZone,K  ...  keys )
	{
		for(    K  key : keys )
		{
			V   value   = super.get( key );
			
			if( value != null )  super.put( key,(V)  new  Timestamp(DateTime.parse(super.get(key).toString(),DateTimeFormat.forPattern(datetimeFormat)).withZone(datetimeZone).getMillis()) );
		}
		
		return  this;
	}
	
	@Override
	public  V  get(K  key,V  defaultValue )
	{
		return  super.containsKey(key) ? super.get( key ) : defaultValue;
	}
	
	@Override
	public  String  getString( K  key )
	{
		return  (String)  super.get( key );
	}
	
	@Override
	public  Short  getShort(   K  key )
	{
		return  (Short)   super.get( key );
	}

	@Override
	public  Long  getLong( K  key )
	{
		return  (Long)    super.get( key );
	}

	@Override
	public  Integer  getInteger(   K  key )
	{
		return  (Integer) super.get( key );
	}

	@Override
	public  Map<K,V>  removeEntry( K  key )
	{
		super.remove( key );
		
		return  this;
	}
	
	@Override
	public  Double  getDouble( K  key )
	{
		return  (Double)  super.get( key );
	}
	
	@Override
	public  Boolean  getBoolean(   K  key )
	{
		return  (Boolean) super.get( key );
	}
}