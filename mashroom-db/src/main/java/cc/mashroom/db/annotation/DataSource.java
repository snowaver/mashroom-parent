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
package cc.mashroom.db.annotation;

import  java.lang.annotation.Retention;
import  java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.RUNTIME )

public  @interface  DataSource
{
	/**
	 *  data  source  type.  db  for  database  but  cache  for  cache  data  source.
	 */
	public  String  type();
	/**
	 *  data  source  name.  default:  empty  string.  you  should  supply  a  non-empty  string  for  a  database  data  source,  but  it  is  not  necessary  for  a  cache  data  source  while  the  cache  is  shared  global.
	 */
	public  String  name()  default  "";
}