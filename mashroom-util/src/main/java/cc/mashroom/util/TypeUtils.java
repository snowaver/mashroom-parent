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

import  javax.annotation.Nonnull;
import  javax.annotation.Nullable;

public  class  TypeUtils
{
	public  static  boolean  isPresent( @Nonnull  String  className,@Nullable  ClassLoader  classLoader )
	{
		try
		{
			if( classLoader == null )
			{
				Class.forName( className );
			}
			else
			{
				classLoader.loadClass( className );
			}
			
			return   true;
		}
		catch(  ClassNotFoundException  e )
		{
			return  false;
		}
	}
}