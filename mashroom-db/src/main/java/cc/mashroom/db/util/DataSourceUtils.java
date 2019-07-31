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

import  javax.annotation.Nonnull;

import  cc.mashroom.config.Properties;

public  class  DataSourceUtils
{
	public  static  Properties  createDataSourceProperties( @Nonnull  String  driverClassName,@Nonnull  String  dataSourceName,@Nonnull  String  jdbcUrl,String  user,String  password,Integer  minPoolSize,Integer  maxPoolSize,Long  idleConnectionTestPeriod,String  preferredTestQuery )
	{
		Properties  properties = new  Properties();
		
		properties.put( "driverClass",driverClassName );
		
		properties.put( "jdbcUrl",  jdbcUrl );
		
		if( user != null )
		{
			properties.put( "user",    user );
		}
		if( password != null )
		{
			properties.put( "password", password );
		}
		if( minPoolSize != null )
		{
			properties.put( "minPoolSize",minPoolSize );
		}
		if( preferredTestQuery != null )
		{
			properties.put( "preferredTestQuery",preferredTestQuery );
		}
		if( maxPoolSize != null )
		{
			properties.put( "maxPoolSize",maxPoolSize );
		}
		if( idleConnectionTestPeriod != null )
		{
			properties.put( "idleConnectionTestPeriod" , idleConnectionTestPeriod );
		}
		
		return        properties;
	}
}