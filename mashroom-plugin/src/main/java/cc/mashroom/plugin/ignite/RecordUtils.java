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

import  cc.mashroom.util.ObjectUtils;
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
			result.add( (T)  fillColumns(clazz, clazz.getPackage().getName().startsWith("java.") || java.util.Map.class.isAssignableFrom(clazz) ?null : cc.mashroom.db.util.RecordUtils.createColumnBeanFieldMapper(clazz),cursor,values) );
		}
		
		return  result;
	}
	
	public  static  <T>  T  fillColumns( Class<T>  resultBeanClazz,Map<String,Field>  columnBeanFieldMapper,FieldsQueryCursor<List<Object>>  cursor,List<Object>  values )  throws  SQLException,IntrospectionException,IllegalAccessException,IllegalArgumentException,InvocationTargetException,InstantiationException
	{
		if( cursor.getColumnsCount() == 1 && values.get(0) != null && values.get(0).getClass() == resultBeanClazz )
		{
			return  (T)  values.get( 0 );
		}
		
		if( java.util.Map.class.isAssignableFrom(resultBeanClazz) || columnBeanFieldMapper == null || columnBeanFieldMapper.isEmpty() )
		{
			T  record = (T)  (java.util.Map.class.isAssignableFrom(resultBeanClazz) ? new  HashMap<String,Object>() : resultBeanClazz.newInstance() );
			
			for( int  i = 1;i <= cursor.getColumnsCount()-1;i = i+ 1 )
			{
				if( record instanceof java.util.Map )
				{
					ObjectUtils.cast(record,Map.class).put( cursor.getFieldName(i ) , values.get( i ) );
				}
				else
				{
					Field  columnField =  columnBeanFieldMapper.get( cursor.getFieldName( i ) );
					
					columnField.setAccessible(true );
					
					columnField.set( record,values.get(i) );
				}
			}
			
			return  record;
		}
		else
		if( cursor.getColumnsCount() == 1 && values.get( 0 ) == null )
		{
			return    null;
		}
		
		return  null;
	}
}