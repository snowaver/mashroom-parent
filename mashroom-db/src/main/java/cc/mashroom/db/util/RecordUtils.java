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
import  java.beans.PropertyDescriptor;
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
import  cc.mashroom.util.collection.map.Map.Computer;

public  class  RecordUtils
{
	private final  static  Map<String,PropertyDescriptor>  PROPERTY_DESCRIPTOR_CACHE = new  ConcurrentHashMap<String,PropertyDescriptor>();
	
	public  static  <T>  List<T>  list( ResultSet  resultSet,Class<T>  clazz )  throws  SQLException,InstantiationException,IllegalAccessException,IntrospectionException,IllegalArgumentException, InvocationTargetException
	{
		Map<String,String>  columnBeanFieldMapper   = new  HashMap<String,String>();
		
		List<T>  result   = new  LinkedList<T>();
		
		for( Field  field : ReflectionUtils.getAnnotatedFields(clazz,Column.class) )
		{
			columnBeanFieldMapper.put( field.getAnnotation(Column.class).name(),field.getName() );
		}
		
		while( resultSet.next() )
		{
			result.add( fillColumns(clazz.newInstance(),columnBeanFieldMapper,resultSet.getMetaData(),resultSet) );
		}
		
		return  result;
	}
		
	public  static  <T>  T  fillColumns( final  T  record,Map<String,String>  columnBeanFieldMapper,ResultSetMetaData  metadata,ResultSet  rs )  throws  SQLException,IntrospectionException,IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		for( int  i = 1;i <= metadata.getColumnCount();i = i+1 )
		{
			if( record instanceof java.util.Map )
			{
				ObjectUtils.cast(record,Map.class).put( metadata.getColumnLabel(i),FieldsConverter.convert(metadata.getColumnTypeName(i),rs.getObject(i)) );
			}
			else
			{
				String  fn = columnBeanFieldMapper.get( metadata.getColumnLabel(i));
				
				PROPERTY_DESCRIPTOR_CACHE.computeIfLackof(record.getClass().getName()+"."+fn,new  Computer<String,PropertyDescriptor>(){public PropertyDescriptor compute(String  key)  throws  Exception{return  new  PropertyDescriptor(key,record.getClass());}}).getWriteMethod().invoke( record,FieldsConverter.convert(metadata.getColumnTypeName(i),rs.getObject(i)) );
			}
		}
		
		return  record;
	}
}