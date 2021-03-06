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

import  java.lang.annotation.Annotation;
import  java.lang.reflect.Field;
import  java.util.ArrayList;
import  java.util.List;

import lombok.NonNull;

public  class   ReflectionUtils
{
	public  static  List<Field>  getAnnotatedFields( Class<?>  clazz,Class<? extends Annotation>  annotation )
	{
		return  getAnnotatedFields(null,annotation,clazz);
	}
	
	public  static  Object  getValue( @NonNull  Field  field,@NonNull  Object  object )  throws  IllegalArgumentException,IllegalAccessException
	{
		field.setAccessible(     true );
		
		return  field.get( object );
	}
	
	public  static  List<Field>  getAnnotatedFields( List<Field>  fields,Class<? extends Annotation>  annotation,Class<?>...  clazzes )
	{
		fields = fields != null        ? fields : new  ArrayList<Field>();
		
		for( Class<?>  clazz : clazzes )
		{
			if( clazz == null )
			{
				continue;
			}
			
			for(Field  field : clazz.getDeclaredFields() )
			{
				if(field.isAnnotationPresent(annotation) )
				{
					fields.add( field );
				}
			}
			
			getAnnotatedFields( fields,annotation,clazz.getSuperclass() );
			
			getAnnotatedFields( fields,annotation,clazz.getInterfaces() );
		}
		
		return  fields;
	}
}