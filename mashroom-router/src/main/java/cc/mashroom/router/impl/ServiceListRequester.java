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

import  java.util.List;
import  java.util.concurrent.CountDownLatch;

import  org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import  com.fasterxml.jackson.core.type.TypeReference;

import  cc.mashroom.router.Service;
import  cc.mashroom.router.Service.Schema;
import  cc.mashroom.util.JsonUtils;
import  cc.mashroom.util.ObjectUtils;
import  lombok.AccessLevel;
import  lombok.AllArgsConstructor;
import  lombok.Setter;
import  lombok.SneakyThrows;
import  lombok.experimental.Accessors;
import  okhttp3.OkHttpClient;
import  okhttp3.Request;
import  okhttp3.Response;

@AllArgsConstructor
public  class  ServiceListRequester  implements  Runnable
{
	private  ArrayListValuedHashMap<Schema,Service>  services;
	private  CountDownLatch     ctdlatch;
	@Setter(value=AccessLevel.PROTECTED )
	@Accessors(chain=true )
	private  OkHttpClient   okHttpClient;
	@Setter(value=AccessLevel.PROTECTED )
	@Accessors(chain=true )
	private  String  url;
	@Override
	@SneakyThrows
	public   void   run()
	{
		try( Response  response =  this.okHttpClient.newCall( new  Request.Builder().url(this.url).build()).execute() )
		{
			if( 200 ==  response.code() )
			{
				synchronized(  services )
				{
				if( services.isEmpty( ) )
				{
					for( Service  service : ObjectUtils.cast(JsonUtils.mapper.readValue(response.body().string(),JsonUtils.mapper.getTypeFactory().constructParametricType(List.class,Service.class)),new  TypeReference<List<Service>>(){}) )  this.services.put( service.getSchema(),service );  this.ctdlatch.countDown();
				}
				}
			}
		}
	}
}
