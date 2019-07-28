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
package cc.mashroom.db;

import  java.lang.reflect.ParameterizedType;
import  java.sql.PreparedStatement;
import  java.sql.ResultSet;
import  java.util.Collection;
import  java.util.LinkedList;
import  java.util.List;

import  com.google.common.collect.Lists;

import  cc.mashroom.db.annotation.DataSourceBind;
import  cc.mashroom.db.connection.Connection;
import  cc.mashroom.db.util.ConnectionUtils;
import  cc.mashroom.db.util.RecordUtils;
import  cc.mashroom.util.ObjectUtils;
import  cc.mashroom.util.Reference;
import  cc.mashroom.util.StringUtils;
import  cc.mashroom.util.collection.map.Map;
import  lombok.Setter;
import  lombok.SneakyThrows;

public  class  GenericRepository<M>  //  extends  HashMap<String,Object>
{
	@Setter
	protected  DataSourceBind     customizedDataSourceBind;
	
	@SneakyThrows
	public  int  insert( Collection  <? extends Map>  rcs )
	{
		if( !rcs.isEmpty() )
		{
			List<String>  fields = new  LinkedList<String>(   rcs.iterator().next().keySet() );
			
			return  ConnectionUtils.batchUpdatedCount( insert(new  LinkedList<Reference<Object>>(),"INSERT  INTO  "+getDataSourceBind().table()+"  ("+StringUtils.join(fields,",")+")  VALUES  ("+StringUtils.rightPad("?",2*(fields.size()-1)+1,",?")+")",ConnectionUtils.prepare(Lists.newArrayList(rcs),fields).toArray(new  Object[rcs.size()][])) );
		}
		
		return  0;
	}
	
	@SneakyThrows
	public  int[]  update( String  sql,Object[][]  params )
	{
		try( Connection  connection = ConnectionFactory.getConnection(getDataSourceName());PreparedStatement  statement = connection.prepareStatement(sql,params) )
		{
			return  statement.executeBatch( );
		}
	}
	
	@SneakyThrows
	public  int    update( String  sql,Object...   params )
	{
		try( Connection  connection = ConnectionFactory.getConnection(getDataSourceName());PreparedStatement  statement = connection.prepareStatement(sql,params) )
		{
			return  statement.executeUpdate();
		}
	}
	
	@SneakyThrows
	public  int  insert(  Reference<Object>  keyRef,String  sql,Object...  params )
	{
		try( Connection  connection = ConnectionFactory.getConnection(getDataSourceName());PreparedStatement  preparedStatement = connection.prepareStatement(true,sql,params) )
		{
			int  counts= preparedStatement.executeUpdate();
			
			ResultSet  autoGeneratedKeySet  = preparedStatement.getGeneratedKeys();
			
			if( autoGeneratedKeySet.next()   )  keyRef.set( autoGeneratedKeySet.getObject(1) );
			
			return   counts;
		}
	}
	
	@SneakyThrows
	public  int[]  insert(   List<Reference<Object>>  keyRefs,String  sql,Object[]...  params )
	{
		try( Connection  connection = ConnectionFactory.getConnection(getDataSourceName());PreparedStatement  preparedStatement = connection.prepareStatement(true,sql,params) )
		{
			int[]  counts=preparedStatement.executeBatch();
			
			ResultSet  autoGeneratedKeySet  = preparedStatement.getGeneratedKeys();
			
			for(  int  count : counts )
			{
				keyRefs.add( new  Reference<Object>( count >= 1 && autoGeneratedKeySet.next() ? autoGeneratedKeySet.getObject( 1 ) : null ) );
			}
			
			return   counts;
		}
	}
	
	public  DataSourceBind getDataSourceBind()
	{
		DataSourceBind  dataSourceBind = !getClass().isAnnotationPresent(DataSourceBind.class) ? null : getClass().getAnnotation( DataSourceBind.class );
		
		if( dataSourceBind    == null )
		{
			throw  new  IllegalStateException(String.format("MASHROOM-DB:  ** GENERIC  REPOSITORY **  data  source  bind  annotation  is  absent  in  class  ( %s )",getClass().getName()) );
		}
		
		return  dataSourceBind;
	}
	
	protected  Class<?>  getBeanClass()
	{
		for( Class<?>  currentClass = getClass();currentClass != Object.class;currentClass = currentClass.getSuperclass() )
		{
			if( currentClass.getSuperclass() == GenericRepository.class  && currentClass.getGenericSuperclass() instanceof ParameterizedType )
			{
				return  (Class<?>)  ObjectUtils.cast(currentClass.getGenericSuperclass(),ParameterizedType.class).getActualTypeArguments()[0];
			}
		}
		
		throw  new  IllegalArgumentException( String.format("MASHROOM-DB:  ** GENERIC  REPOSITORY **  bean  class  definition  is  not  found  for  class  ( %s )",  getClass().getName()) );
	}
	
	public  String  getDataSourceName()
	{
		return  ConnectionFactory.DATA_SOURCE_LOCATOR!= null ? ConnectionFactory.DATA_SOURCE_LOCATOR.locate(this,getDataSourceBind()) : this.getDataSourceBind().name();
	}
	
	public  M  lookupOne(   String  sql,Object...  params )
	{
		List<M>  rcds = lookup( sql,params );
		
		if( rcds.size() >= 2 )
		{
			throw  new  IllegalStateException( String.format("MASHROOM-DB:  ** GENERIC  REPOSITORY **  requires  unique,  but  found  %d",rcds.size()) );
		}
		
		return  rcds.isEmpty()      ? null : rcds.get( 0 );
	}
	
	@SneakyThrows
	public  List<M>  lookup(String  sql,Object...  params )
	{
		try( Connection  connection = ConnectionFactory.getConnection(getDataSourceName());PreparedStatement  statement = connection.prepareStatement(sql,params);ResultSet  rs = statement.executeQuery() )
		{
			return  ObjectUtils.cast( RecordUtils.list(rs,getClass()) );
		}
	}
}