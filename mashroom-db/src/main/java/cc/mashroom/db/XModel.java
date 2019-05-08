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

import  java.sql.PreparedStatement;
import  java.sql.ResultSet;
import  java.util.List;

import  cc.mashroom.db.connection.Connection;
import  cc.mashroom.db.util.RecordUtils;
import  cc.mashroom.util.CollectionUtils;
import  lombok.SneakyThrows;

public  class  XModel  <M extends XModel  <?>>  extends  Record
{
	public  M  getOne( String  sql,Object...  params )
	{
		return  CollectionUtils.getFirst( search(sql,params) );
	}
	
	@SneakyThrows
	public  List<M>  search(    String  sql,Object...  params )
	{
		try( Connection  connection = ConnectionFactory.getConnection(super.getDataSourceName());PreparedStatement  statement = connection.prepareStatement(sql,params);ResultSet  rs = statement.executeQuery() )
		{
			return  (List<M>)RecordUtils.list( rs,getClass() );
		}
	}
}