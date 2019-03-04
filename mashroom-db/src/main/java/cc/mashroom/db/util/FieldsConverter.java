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

import  java.sql.Timestamp;

import  org.joda.time.DateTime;

public  class  FieldsConverter
{
	public  static  Object  convert( String  columnTypeName,Object  value )
	{
		if( value != null )
		{
			if( "DATETIME".equalsIgnoreCase(columnTypeName) && !(value instanceof Timestamp) )
			{
				return  new  Timestamp( value instanceof Long ? (Long)  value : DateTime.parse(value.toString()).getMillis() );
			}
			else
			if( "BIGINT".equalsIgnoreCase(columnTypeName) && !(value instanceof Long) )
			{
				return  Long.parseLong( value.toString() );
			}
		}
		
		return  value;
	}
}