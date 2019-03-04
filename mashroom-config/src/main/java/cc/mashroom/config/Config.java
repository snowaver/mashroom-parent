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

import  cc.mashroom.util.StringUtils;
import  cc.mashroom.util.collection.map.ConcurrentHashMap;
import  cc.mashroom.util.collection.map.Map;

public  class  Config
{
	private  static  Map<String,Properties>  configs = new  ConcurrentHashMap<String,Properties>();
	
	public  static  Properties  use( String  configName )
	{
		if( StringUtils.isBlank(configName) )
		{
			throw  new  IllegalArgumentException( "CONFIG:  ** CONFIG **  the  config  name  can  not  be  null  or  empty" );
		}
		
		return  configs.computeIfLackof( configName,new  Map.Computer<String,Properties>(){public  Properties  compute(String  key)  throws  Exception{ return  new  Properties(key); }} );
	}
	
	public  final  static  Properties  server = Config.use( "server.properties" );
}