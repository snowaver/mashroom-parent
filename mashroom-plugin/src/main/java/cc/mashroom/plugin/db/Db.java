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
package cc.mashroom.plugin.db;

import  cc.mashroom.plugin.Plugin;

import  cc.mashroom.db.ConnectionManager;

public  class  Db  implements  Plugin
{
	public  void  initialize( Object  ...  parameters )
	{
		try
		{
			ConnectionManager.INSTANCE.initialize( parameters );
		}
		catch(  Exception  e )
		{
			throw  new  IllegalStateException( "MASHROOM-PLUGIN:  ** DB  PLUGIN **  db  initialization  error" );
		}
	}
	
	public  void  stop()  { ConnectionManager.INSTANCE.stop(); }
}