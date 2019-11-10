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

import  java.util.Collection;
import  java.util.concurrent.CountDownLatch;
import  java.util.concurrent.LinkedBlockingQueue;
import  java.util.concurrent.ThreadPoolExecutor;
import  java.util.concurrent.TimeUnit;

import  org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import  cc.mashroom.router.Service;
import  cc.mashroom.router.ServiceListRequestStrategy;
import  cc.mashroom.router.Service.Schema;
import  lombok.AccessLevel;
import  lombok.AllArgsConstructor;
import  lombok.Setter;
import  lombok.SneakyThrows;
import  lombok.experimental.Accessors;
import  okhttp3.OkHttpClient;

@AllArgsConstructor
public  class      DefaultServiceListRequestStrategy  implements  ServiceListRequestStrategy
{
	@Setter( value= AccessLevel.PROTECTED )
	@Accessors( chain= true )
	private  OkHttpClient okHttpClient;
	@Setter( value= AccessLevel.PROTECTED )
	@Accessors( chain= true )
	private  Collection <String>  urls;
	@Setter( value= AccessLevel.PROTECTED )
	@Accessors( chain= true )
	private  long  timeout;
	@Setter( value= AccessLevel.PROTECTED )
	@Accessors( chain= true )
	private  TimeUnit  timeoutTimeUnit;
	@SneakyThrows
	public  ArrayListValuedHashMap<Schema , Service>  request( )
	{
		ThreadPoolExecutor  requestAwaitPool = new  ThreadPoolExecutor( this.urls.size(),this.urls.size(),60,TimeUnit.SECONDS,new  LinkedBlockingQueue<Runnable>() );
		
		try
		{
			ArrayListValuedHashMap<Schema , Service>  services = new  ArrayListValuedHashMap<Schema,Service>();
			
			CountDownLatch  ctdlatch = new  CountDownLatch( 1 );
			
			for(  String  url : this.urls )  requestAwaitPool.submit( new  ServiceListRequester(services,ctdlatch,this.okHttpClient,url) );
			
			ctdlatch.await( this.timeout,this.timeoutTimeUnit );
			
			return  services;
		}
		finally
		{
			requestAwaitPool.shutdownNow();
		}
	}
}