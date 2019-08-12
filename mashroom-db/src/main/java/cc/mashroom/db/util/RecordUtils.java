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
package cc.mashroom.db.util;

import  java.beans.IntrospectionException;
import  java.lang.reflect.Field;
import  java.lang.reflect.InvocationTargetException;
import  java.sql.ResultSet;
import  java.sql.ResultSetMetaData;
import  java.sql.SQLException;
import  java.util.LinkedList;
import  java.util.List;

import  cc.mashroom.db.annotation.Column;
import  cc.mashroom.util.ObjectUtils;
import  cc.mashroom.util.ReflectionUtils;
import  cc.mashroom.util.collection.map.ConcurrentHashMap;
import  cc.mashroom.util.collection.map.HashMap;
import  cc.mashroom.util.collection.map.Map;

public  class  RecordUtils
{
	private final  static  Map<Class<?>,Map<String,Field>>  COLUMN_BEAN_FIELD_MAPPER_CACHE = new  ConcurrentHashMap<Class<?>,Map<String,Field>>();
	
	public  static  <T>  List<T>  list(       ResultSet  resultSet,Class<T>  clazz )  throws  SQLException,InstantiationException,IllegalAccessException,IntrospectionException,IllegalArgumentException,InvocationTargetException
	{
		List<T>  result   = new  LinkedList<T>();
		
		while( resultSet.next() )
		{
			result.add( (T)  fillColumns(clazz,clazz.getPackage().getName().startsWith("java." ) || java.util.Map.class.isAssignableFrom(clazz) ? null : createColumnBeanFieldMapper(clazz),resultSet.getMetaData(),resultSet) );
		}
		
		return  result;
	}
	
	public  static  Map<String,Field>  createColumnBeanFieldMapper(Class<?>  clazz )
	{
		Map<String,Field>   columnBeanFieldMapper = COLUMN_BEAN_FIELD_MAPPER_CACHE.get( clazz );
		
		synchronized(     clazz )
		{
			columnBeanFieldMapper     = COLUMN_BEAN_FIELD_MAPPER_CACHE.get( clazz );
			
			if( columnBeanFieldMapper ==  null  )
			{
				columnBeanFieldMapper =   new  HashMap<String,Field>();
				
				for(     Field  field : ReflectionUtils.getAnnotatedFields(clazz,Column.class) )
				{
					columnBeanFieldMapper.put( field.getAnnotation(Column.class).name(),field );
				}
				
				COLUMN_BEAN_FIELD_MAPPER_CACHE.put(   clazz,columnBeanFieldMapper );
			}
		}
		
		return     columnBeanFieldMapper;
	}
	
	public  static  <T>  T  fillColumns( Class<T>  resultBeanClazz,Map<String,Field>  columnBeanFieldMapper,ResultSetMetaData  metadata,ResultSet  rs )  throws  SQLException,IntrospectionException,IllegalAccessException, IllegalArgumentException,InvocationTargetException,InstantiationException
	{
		if( metadata.getColumnCount()== 1 && rs.getObject(1) != null &&rs.getObject(1).getClass() == resultBeanClazz )
		{
			return  (T)  rs.getObject(1);
		}
		
		if( java.util.Map.class.isAssignableFrom(resultBeanClazz) || columnBeanFieldMapper == null || columnBeanFieldMapper.isEmpty() )
		{
			T  record = (T)  (java.util.Map.class.isAssignableFrom(resultBeanClazz ) ? new  HashMap<String,Object>() : resultBeanClazz.newInstance() );
			
			for( int  i = 1;i <= metadata.getColumnCount()- 1;i = i+1 )
			{
				if( record instanceof java.util.Map )
				{
					ObjectUtils.cast(record,java.util.Map.class).put( metadata.getColumnLabel(i)  , rs.getObject(i) );
				}
				else
				{
					Field  columnField = columnBeanFieldMapper.get(metadata.getColumnLabel(i) );
					
					columnField.setAccessible(true );
					
					columnField.set( record,rs.getObject(i) );
				}
			}
			
			return  record;
		}
		else
		if( metadata.getColumnCount() == 1 && rs.getObject(1) == null )
		{
			return    null;
		}
		
		return  null;
	}
}