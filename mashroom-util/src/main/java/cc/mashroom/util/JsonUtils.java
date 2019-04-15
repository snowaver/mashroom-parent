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
package cc.mashroom.util;

import  java.text.SimpleDateFormat;
import  java.util.Map;
import  java.util.TimeZone;

import  com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import  com.fasterxml.jackson.databind.DeserializationFeature;
import  com.fasterxml.jackson.databind.ObjectMapper;
import  com.fasterxml.jackson.databind.module.SimpleModule;

public  class  JsonUtils
{
	public  final  static  ObjectMapper  mapper      = new  ObjectMapper();
	
	static
	{
		SimpleDateFormat  greenwichDateFormater = new  SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" );
		
		greenwichDateFormater.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
		
		mapper.setDateFormat(greenwichDateFormater).configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES , false );
		
		mapper.registerModule( new  SimpleModule().addDeserializer(cc.mashroom.util.collection.map.Map.class,new  MapDeserializer()) );
	}
	
	public  static  <T>  T  fromJson( String  jsonString,TypeReference<T>  typeReference )
	{
		try
		{
			return  mapper.readValue( jsonString,typeReference );
		}
		catch( Throwable  e)
		{
			throw  new  IllegalStateException( String.format("MASHROOM-UTIL:  ** JSONUTILS **  error  parsing  json  to  %s  instance.",Map.class.getName()),e );
		}
	}
	
	public  static  <T>  T  fromJson( String  jsonString,Class<T>  clazz )
	{
		try
		{
			return  mapper.readValue(jsonString,clazz);
		}
		catch( Throwable  e)
		{
			throw  new  IllegalStateException( String.format("MASHROOM-UTIL:  ** JSONUTILS **  error  parsing  json  to  %s  instance.",Map.class.getName()),e );
		}
	}
	
	public  static  String  toJson(    Object  object )
	{
		try
		{
			return  mapper.writeValueAsString(object );
		}
		catch( JsonProcessingException  e )
		{
			throw  new  IllegalStateException( "MASHROOM-UTIL:  ** JSONUTILS **  error  converting  object  to  json.",e );
		}
	}
	

}