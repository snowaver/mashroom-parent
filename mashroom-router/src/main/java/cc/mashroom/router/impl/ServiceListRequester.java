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
package cc.mashroom.router.impl;

import  java.io.IOException;
import  java.util.List;

import  com.fasterxml.jackson.core.type.TypeReference;

import  cc.mashroom.router.Service;
import  cc.mashroom.util.JsonUtils;
import  cc.mashroom.util.ObjectUtils;
import  lombok.AccessLevel;
import  lombok.AllArgsConstructor;
import  lombok.Setter;
import  lombok.experimental.Accessors;
import  okhttp3.OkHttpClient;
import  okhttp3.Request;
import  okhttp3.Response;

@AllArgsConstructor
public  class  ServiceListRequester//  implements  Runnable
{
	@Setter( value=  AccessLevel.PROTECTED )
	@Accessors( chain  = true )
	private  OkHttpClient  okHttpClient;
	public  List<Service>  request( String  url )  throws  IOException
	{
		try( Response  response =  this.okHttpClient.newCall( new  Request.Builder().url(url).build()).execute() )
		{
			if( response.code() == 200 )
			{
				return  ObjectUtils.cast( JsonUtils.mapper.readValue(response.body().string(),JsonUtils.mapper.getTypeFactory().constructParametricType(List.class,Service.class)),new  TypeReference<List<Service>>(){} );
			}
			
			throw  new  IllegalStateException( String.format("MASHROOM-ROUTER:  ** SERVICE  LIST  REQUEST ** can  not  request  the  service  list  for  response  code  (%d).",response.code()) );
		}
	}
}
