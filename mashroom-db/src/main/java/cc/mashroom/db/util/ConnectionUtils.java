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

import  java.sql.SQLException;
import  java.util.LinkedList;
import  java.util.List;

import  cc.mashroom.util.collection.map.Map;
import cc.mashroom.util.stream.Consumer;
import cc.mashroom.util.stream.Stream;

public  class  ConnectionUtils
{
	public  static  int  batchUpdatedCount(  int[]  updates )
	{
		int  count = 0;
		
		for( int  update : updates )
		{
			if( update >= 1 )  count = count + 1;
		}
		
		return   count;
	}
	
	public  static  List<Object[]>  prepare( List<? extends Map>  records,BatchPrediction  prediction )
	{
		List<Object[]>  params = new  LinkedList<Object[]>();
		
		for(    Map<String,Object>  record : records )
		{
			params.add(prediction.predicate(record) );
		}
		
		return  params;
	}
	
	public  static  List<Object[]>  prepare( List<? extends Map>  records,List<String>  fields )
	{
		List<Object[]>  params = new  LinkedList<Object[]>();
		
		for(    final  Map<String,Object>  record : records )
		{
			final  List<Object>  values = new  LinkedList<Object>();
			
			Stream.forEach( fields,new  Consumer<String>(){public  void  consume(String  field)  throws  Exception{ values.add(record.get(field)); }} );
			
			params.add( values.toArray() );
		}
		
		return  params;
	}
	
	public  static  java.sql.PreparedStatement  prepare( java.sql.PreparedStatement  statement,Object...   params )  throws  SQLException
	{
		for( int  i = 0;i <= params.length-1;i = i+1 )
		{
			statement.setObject( i+1,params[i] );
		}
		
		return  statement;
	}
		
	public  static  java.sql.PreparedStatement  prepare( java.sql.PreparedStatement  statement,Object[][]  params )  throws  SQLException
	{
		for( int  i = 0;i <= params.length-1;i = i+1 )
		{
			for( int  j = 0;j <= params[i].length-1;j = j+1 )
			{
				statement.setObject(j+1,params[i][j]);
			}
			
			statement.addBatch();
		}
		
		return  statement;
	}
}