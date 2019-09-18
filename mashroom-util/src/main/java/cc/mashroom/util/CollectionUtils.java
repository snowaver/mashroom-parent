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

import  java.util.Collection;
import  java.util.List;

import  lombok.NonNull;

public  class  CollectionUtils
{
	public  static  <T>  Collection<T>  addIfAbsent( @NonNull  Collection<T>  collection,@NonNull  T  element )
	{
		if( !collection.contains(element) )  collection.add(element );
		
		return collection;
	}
	
	public  static  <T>  Collection<T>  remove(      @NonNull  Collection<T>  collection,@NonNull  T  element )
	{
		collection.remove(       element );
		
		return collection;
	}
	
	public  static  Long[]  toLongArray( Object...  objects )
	{
		Long[]  longArray= new  Long[ objects.length ];
		
		for( int  i = 0,length = objects.length;i<= length-1;i = i+1 )
		{
			longArray[ i ]  = Long.valueOf( objects[ i ].toString() );
		}
		
		return  longArray;
	}
	
	public  static  <T>  T  getFirst(   List<T>  list )
	{
		return  list == null || list.isEmpty() ? null : list.get( 0 );
	}
}