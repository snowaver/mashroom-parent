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
package cc.mashroom.util.stream;

import  java.util.Collection;

public  class  Stream
{
	public  static  <T>  void  forEach( Collection<T>  collection,Consumer<T>  computer )
	{
		for( T  item : collection )  Stream.consume( item,computer );
	}
	
	private  static  <T>  void  consume( T  item,Consumer<T>  consumer )
	{
		try
		{
			consumer.consume( item );
		}
		catch( Exception  e )
		{
			throw  new  RuntimeException( "MASHROOM-UTIL:  ** STREAM **  consumption  error",e );
		}
	}
}