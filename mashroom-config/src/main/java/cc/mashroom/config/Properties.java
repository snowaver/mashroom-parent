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
package cc.mashroom.config;

import org.apache.commons.lang3.StringUtils;
import  org.slf4j.LoggerFactory;

public  class  Properties  extends  java.util.Properties
{
	private  static  final  org.slf4j.Logger  logger = LoggerFactory.getLogger( Properties.class );
	
	public  Properties()
	{
		
	}
	
	public  int  getInt( String  key,int  defaultValue )
	{
		String  originalValue= super.getProperty( key );
		try
		{
			return  StringUtils.isBlank( originalValue ) ? defaultValue : Integer.parseInt(     originalValue );
		}
		catch( Exception  e )
		{
			logger.error( e.getMessage(),e );
		}
		
		return  defaultValue;
	}
	
	public  boolean  getBoolean( String  key,boolean  defaultValue )
	{
		String  originalValue= super.getProperty( key );
		try
		{
			return  StringUtils.isBlank( originalValue ) ? defaultValue : Boolean.parseBoolean( originalValue );
		}
		catch( Exception  e )
		{
			logger.error( e.getMessage(),e );
		}
		
		return  defaultValue;
	}
		
	public  int  getInt( String  key )
	{
		return  getInt( key , 0 );
	}
		
	public  Properties(  String  configName )
	{
		try
		{
			super.load( Properties.class.getResourceAsStream(new  StringBuilder().append("/").append(configName).toString()) );
		}
		catch( Exception  e )
		{
			logger.error( e.getMessage(),e );
		}
	}
}