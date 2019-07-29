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

import  java.beans.IntrospectionException;
import  java.beans.PropertyDescriptor;
import  java.lang.reflect.Field;
import  java.lang.reflect.InvocationTargetException;
import  java.sql.SQLException;
import  java.util.LinkedList;
import  java.util.List;

import  org.apache.ignite.cache.query.FieldsQueryCursor;

import  cc.mashroom.db.annotation.Column;
import  cc.mashroom.util.ObjectUtils;
import  cc.mashroom.util.ReflectionUtils;
import  cc.mashroom.util.collection.map.ConcurrentHashMap;
import  cc.mashroom.util.collection.map.HashMap;
import  cc.mashroom.util.collection.map.Map;
import  cc.mashroom.util.collection.map.Map.Computer;

public  class  RecordUtils
{
	private final  static  Map<String,PropertyDescriptor>  PROPERTY_DESCRIPTOR_CACHE = new  ConcurrentHashMap<String,PropertyDescriptor>();
	
	public  static  <T>  List<T>  list( FieldsQueryCursor<List<Object>>  cursor, Class<T>  clazz )      throws  SQLException,InstantiationException,IllegalAccessException,IntrospectionException,IllegalArgumentException,InvocationTargetException
	{
		List<T>  result  =  new  LinkedList<T>();
		
		for( List<Object>  values : cursor.getAll() )
		{
			result.add( (T)  fillColumns(java.util.Map.class.isAssignableFrom(clazz) ? new  HashMap<String,Object>() : clazz.newInstance(),java.util.Map.class.isAssignableFrom(clazz) ? null : createColumnBeanFieldMapper(clazz),cursor,values) );
		}
		
		return  result;
	}
	
	public  static  Map<String,String>  createColumnBeanFieldMapper(Class<?> clazz )
	{
		Map<String,String>  columnBeanFieldMapper   = new  HashMap<String,String>();
		
		for( Field  field : ReflectionUtils.getAnnotatedFields(clazz,Column.class) )
		{
			columnBeanFieldMapper.put( field.getAnnotation(Column.class).name(),field.getName() );
		}
		
		return  columnBeanFieldMapper;
	}
	
	public  static  <T>  T  fillColumns( final  T  record,Map<String,String>  columnBeanFieldMapper,FieldsQueryCursor<List<Object>>  cursor,List<Object>  values )  throws  SQLException,IntrospectionException,IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		for( int  i = 1;i <= cursor.getColumnsCount()-1;i = i+1 )
		{
			if( record instanceof java.util.Map )
			{
				ObjectUtils.cast(record,Map.class).put(    cursor.getFieldName(i),values.get(i) );
			}
			else
			{
				String  fn = columnBeanFieldMapper.get(    cursor.getFieldName(i) );
				
				PROPERTY_DESCRIPTOR_CACHE.computeIfLackof(record.getClass().getName()+"."+fn,new  Computer<String,PropertyDescriptor>(){public PropertyDescriptor compute(String  key)  throws  Exception{return  new  PropertyDescriptor(key,record.getClass());}}).getWriteMethod().invoke( record,values.get(i) );
			}
		}
		
		return  record;
	}
}