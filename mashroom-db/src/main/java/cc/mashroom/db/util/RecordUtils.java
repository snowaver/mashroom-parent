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

import  java.sql.ResultSet;
import  java.sql.ResultSetMetaData;
import  java.sql.SQLException;
import  java.util.LinkedList;
import  java.util.List;

import  cc.mashroom.db.Record;

public  class  RecordUtils
{
	public  static  <T extends Record>  List<T>  list( ResultSet  resultSet,Class<T>  clazz )  throws  SQLException,InstantiationException,IllegalAccessException
	{
		List<T>  result = new  LinkedList<T>();
		
		while( resultSet.next() )
		{
			result.add( fillColumns(clazz.newInstance(),resultSet.getMetaData(),resultSet) );
		}
		
		return  result;
	}
		
	public  static  <T extends Record>  T  fillColumns( T  record,ResultSetMetaData  metadata,ResultSet  rs )  throws  SQLException
	{
		for( int  i = 1;i <= metadata.getColumnCount();i = i+1 )
		{
			/*
			System.err.println( "DB.DEBUG: //"+record.getClass().getName()+"-"+metadata.getColumnName(i)+"-"+metadata.getColumnTypeName(i)+"-"+metadata.getColumnClassName(i)+"-"+(rs.getObject(i) == null ? null : rs.getObject(i).getClass().getName()) );
			*/
			record.put( metadata.getColumnLabel(i),FieldsConverter.convert(metadata.getColumnTypeName(i),rs.getObject(i)) );
		}
		
		return  record;
	}
}