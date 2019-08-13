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

import  java.lang.reflect.Field;
import  java.sql.SQLException;
import  java.util.LinkedList;
import  java.util.List;

import  cc.mashroom.util.collection.map.Map;

public  class  ConnectionUtils
{	
	public  static  List<Object[]>  prepare( List<? extends Map>  records,BatchPrediction  prediction )
	{
		List<Object[]>  params = new  LinkedList<Object[]>();
		
		for(    Map<String,Object>  record : records )
		{
			params.add(prediction.predicate(record) );
		}
		
		return  params;
	}
	
	public  static  int  batchUpdatedCount(  int[]  updates )
	{
		int  count = 0;
		
		for( int  update:updates )   if( update >= 1 )  count =count+1;
		
		return   count;
	}
	
	public  static  List<Object[]>  prepare(        List<? extends Map>  records,List<String>  fields )
	{
		List<Object[]>  params = new  LinkedList<Object[]>();
		
		for(    Map<String,Object>  record : records )
		{
			Object[]  values  = new  Object[ fields.size() ];
			
			for( int  i = 0,length = fields.size();i <length; i = i+1 )
			{
				values[i] = record.get(fields.get(i));
			}
		}
		
		return  params;
	}
	
	public  static  List<Object[]>  prepare( List<?>  rcs,List<String>  fields,Map<String,Field>  columnBeanFieldMapper )  throws  IllegalArgumentException, IllegalAccessException
	{
		List<Object[]>  params = new  LinkedList<Object[]>();
		
		for(Object  record : rcs )
		{
			Object[]  values  = new  Object[ fields.size() ];
			
			for( int  i = 0,length = fields.size();i <length; i = i+1 )
			{
				Field  columnField = columnBeanFieldMapper.get( fields.get(i) );
				
				columnField.setAccessible(     true );
				
				values[ i ]    = columnField.get(   record );
			}
			
			params.add(  values );
		}
		
		return  params;
	}
	
	public  static  java.sql.PreparedStatement  prepare( java.sql.PreparedStatement  statement,Object...   params )  throws  SQLException
	{
		for( int  i = 0;i  < params.length;i = i + 1 )
		{
			statement.setObject( i+1,params[i] );
		}
		
		return  statement;
	}
		
	public  static  java.sql.PreparedStatement  prepare( java.sql.PreparedStatement  statement,Object[][]  params )  throws  SQLException
	{
		for( int  i = 0;i  < params.length;i = i + 1 )
		{
			for( int  j = 0;j  < params[i].length;j = j + 1 )
			{
				statement.setObject(j+1,params[i][j]);
			}
			
			statement.addBatch( );
		}
		
		return  statement;
	}
}