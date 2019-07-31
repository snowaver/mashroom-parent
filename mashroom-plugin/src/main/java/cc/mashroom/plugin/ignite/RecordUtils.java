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

public  class  RecordUtils
{
	private final  static  Map<Class<?>,Map<String,Field>>  COLUMN_BEAN_FIELD_MAPPER_CACHE = new  ConcurrentHashMap<Class<?>,Map<String,Field>>();
	
	public  static  <T>  List<T>  list(FieldsQueryCursor<List<Object>>  cursor,Class<T>  clazz )        throws  SQLException,InstantiationException,IllegalAccessException,IntrospectionException,IllegalArgumentException,InvocationTargetException
	{
		List<T>  result  =  new  LinkedList<T>();
		
		for( List<Object>  values : cursor.getAll() )
		{
			result.add( (T)  fillColumns(java.util.Map.class.isAssignableFrom(clazz) ? new  HashMap<String,Object>() : clazz.newInstance(),java.util.Map.class.isAssignableFrom(clazz) ? null : createColumnBeanFieldMapper(clazz),cursor,values) );
		}
		
		return  result;
	}
	
	public  static  Map<String,Field>  createColumnBeanFieldMapper(Class<?>  clazz )
	{
		Map<String,Field>   columnBeanFieldMapper = COLUMN_BEAN_FIELD_MAPPER_CACHE.get( clazz );
		
		synchronized(     clazz )
		{
			columnBeanFieldMapper     = COLUMN_BEAN_FIELD_MAPPER_CACHE.get( clazz );
			
			if( columnBeanFieldMapper ==   null )
			{
				columnBeanFieldMapper = new  HashMap<String,Field>();
				
				for(     Field  field : ReflectionUtils.getAnnotatedFields(clazz,Column.class) )
				{
					columnBeanFieldMapper.put( field.getAnnotation(Column.class).name(),field );
				}
				
				COLUMN_BEAN_FIELD_MAPPER_CACHE.put(   clazz,columnBeanFieldMapper );
			}
		}
		
		return     columnBeanFieldMapper;
	}
	
	public  static  <T>  T  fillColumns( final  T  record,Map<String,Field>  columnBeanFieldMapper,FieldsQueryCursor<List<Object>>  cursor,List<Object>  values )  throws  SQLException,IntrospectionException,IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		for( int  i = 1;i <= cursor.getColumnsCount()-1;i = i+1 )
		{
			if( record instanceof java.util.Map )
			{
				ObjectUtils.cast(record,Map.class).put( cursor.getFieldName(i ),values.get(i) );
			}
			else
			{
				Field  fd =  columnBeanFieldMapper.get( cursor.getFieldName(i )   );
				
				fd.setAccessible( true );
				
				fd.set( record , values.get(i) );
			}
		}
		
		return  record;
	}
}