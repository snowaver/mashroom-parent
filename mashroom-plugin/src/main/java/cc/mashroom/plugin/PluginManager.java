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

import  java.util.LinkedHashMap;

import  lombok.AccessLevel;
import  lombok.NoArgsConstructor;

@NoArgsConstructor( access=AccessLevel.PRIVATE )

public  class    PluginManager
{
	private  LinkedHashMap<Plugin,Object[]>  registeredPlugins = new  LinkedHashMap<Plugin,Object[]>();
	
	public  PluginManager  register( Plugin  plugin,Object  ... parameters )
	{
		this.registeredPlugins.put(  plugin ,parameters );
		
		return  this;
	}
	
	public  final  static  PluginManager    INSTANCE = new  PluginManager();
		
	public  void  initialize()
	{
		this.registeredPlugins.entrySet().forEach((pluginParametersEntry) -> pluginParametersEntry.getKey().initialize(pluginParametersEntry.getValue()) );
	}
	
	public  void  stop()
	{
		this.registeredPlugins.keySet().forEach(  (plugin)->plugin.stop() );
	}
}