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
package cc.mashroom.plugin;

import  java.util.LinkedList;
import  java.util.List;

import  com.google.common.collect.Lists;

import  cc.mashroom.util.stream.Stream;
import  lombok.AccessLevel;
import  lombok.NoArgsConstructor;

@NoArgsConstructor( access=AccessLevel.PRIVATE )

public  class   PluginRegistry
{
	public  final  static  PluginRegistry  INSTANCE  = new  PluginRegistry();
	
	private  List<Plugin>  registered = new  LinkedList<Plugin>();
	
	public  PluginRegistry  register( Plugin...  plugins )
	{
		registered.addAll(  Lists.newArrayList(plugins) );
		
		return  this;
	}
		
	public  void  stop()
	{
		registered.forEach(   (plugin) -> plugin.stop() );
	}
	
	public  void  initialize()
	{
		Stream.forEach( registered,(Plugin  plugin) -> plugin.initialize() );
	}
}