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
package cc.mashroom.db.connection;

import  org.apache.commons.pool2.impl.AbandonedConfig;
import  org.apache.commons.pool2.impl.GenericObjectPool;
import  org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import  lombok.Getter;

public  class  ConnectionPool  extends  GenericObjectPool<Connection>
{	
	public  ConnectionPool( String  dataSourceName,ConnectionFactory  factory )
	{
		super( factory );
		
		factory.setConnectionPool(    this );
		
		this.dataSourceName = dataSourceName;
	}
	
	public  ConnectionPool( String  dataSourceName,ConnectionFactory  factory,GenericObjectPoolConfig<Connection>  poolConfig )
	{
		super( factory,poolConfig );
		
		factory.setConnectionPool(    this );
		
		this.dataSourceName = dataSourceName;
	}

	public  ConnectionPool( String  dataSourceName,ConnectionFactory  factory,GenericObjectPoolConfig<Connection>  poolConfig,AbandonedConfig  abandonedConfig )
	{
		super( factory,poolConfig,abandonedConfig );
		
		factory.setConnectionPool(    this );
		
		this.dataSourceName = dataSourceName;
	}
	
	@Getter
	private  String  dataSourceName;
}