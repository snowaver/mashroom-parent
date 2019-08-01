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
package cc.mashroom.util;

import  java.text.SimpleDateFormat;
import  java.util.Date;

import  cc.mashroom.util.collection.map.HashMap;
import  cc.mashroom.util.collection.map.Map;
import  cc.mashroom.util.collection.map.Map.Computer;

public  class  DateUtils
{
	private  final  static  Map<String,SimpleDateFormat>  FORMATTER_CACHE = new  HashMap<String,SimpleDateFormat>();
	
	public  static  String  toString( Date  date,String  format,String  defaultValue )
	{
		if( date==null )
		{
			return  defaultValue;
		}
		
		return  FORMATTER_CACHE.computeIfLackof(format,new  Computer<String, SimpleDateFormat>(){public SimpleDateFormat compute( String  key )  throws  Exception{return  new  SimpleDateFormat(key);}}).format( date );
	}
}