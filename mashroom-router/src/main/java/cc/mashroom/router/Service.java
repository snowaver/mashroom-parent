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
package cc.mashroom.router;

import  com.fasterxml.jackson.annotation.JsonProperty;

import  lombok.Data;
import  lombok.NoArgsConstructor;
import  lombok.ToString;
import  lombok.experimental.Accessors;

@Accessors( chain= true )
@Data
@NoArgsConstructor
@ToString
public  class  Service
{
	@JsonProperty( "ID" )
	private  Long  id;
	@JsonProperty( "HOST" )
	private  String   host;
	@JsonProperty( "PORT" )
	private  Integer  port;
	@JsonProperty( "SCHEMA"   )
	private  String schema;
	
	public  enum   Schema
	{
		TCP,UDP,HTTP,HTTPS,FTP;
	}
	public  Schema  getSchema()
	{
		return  Schema.valueOf( this.schema.toUpperCase() );
	}
}