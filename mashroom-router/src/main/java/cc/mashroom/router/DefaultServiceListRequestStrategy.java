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

import  java.util.Collection;
import  java.util.LinkedList;
import  java.util.List;
import  java.util.concurrent.CountDownLatch;
import  java.util.concurrent.LinkedBlockingQueue;
import  java.util.concurrent.ThreadPoolExecutor;
import  java.util.concurrent.TimeUnit;

import  javax.annotation.Nonnull;
import  javax.net.ssl.SSLSocketFactory;

import  cc.mashroom.util.JsonUtils;
import  cc.mashroom.util.NoopHostnameVerifier;
import  cc.mashroom.util.NoopX509TrustManager;
import  lombok.AccessLevel;
import  lombok.Setter;
import  lombok.experimental.Accessors;
import  okhttp3.OkHttpClient;
import  okhttp3.Request;
import  okhttp3.Response;

public  class    DefaultServiceListRequestStrategy  implements  ServiceListRequestStrategy
{
	public  DefaultServiceListRequestStrategy( @Nonnull  Collection<String>  urls,@Nonnull  SSLSocketFactory  sslSocketFactory,long  timeout,@Nonnull  TimeUnit  timeoutTimeUnit )
	{
		this.setTimeout(timeout).setTimeoutTimeUnit(timeoutTimeUnit).setUrls(urls).setClient( new  OkHttpClient.Builder().hostnameVerifier(new  NoopHostnameVerifier()).sslSocketFactory(sslSocketFactory,new  NoopX509TrustManager()).connectTimeout(5,TimeUnit.SECONDS).writeTimeout(5,TimeUnit.SECONDS).readTimeout(10,TimeUnit.SECONDS).build() );
	}
	
	@Setter( value= AccessLevel.PROTECTED )
	@Accessors( chain=true )
	private  OkHttpClient   client;
	@Setter( value= AccessLevel.PROTECTED )
	@Accessors( chain=true )
	private  Collection<String>   urls;
	@Setter( value= AccessLevel.PROTECTED )
	@Accessors( chain=true )
	private  long    timeout;
	@Setter( value= AccessLevel.PROTECTED )
	@Accessors( chain=true )
	private  TimeUnit  timeoutTimeUnit;
		
	public  List<Service>     request()
	{
		return  invokeAny(  urls );
	}
	
	private  List<Service>  invokeAny(Collection<String>  urls )
	{
		final  List<Service>  services          =  new  LinkedList<Service>();
		
		if( urls.isEmpty() )
		{
			return services;
		}
		
		ThreadPoolExecutor  executor = new  ThreadPoolExecutor( urls.size(),urls.size(),60,TimeUnit.SECONDS,new  LinkedBlockingQueue<Runnable>() );
		
		final  CountDownLatch  cdlatcher = new  CountDownLatch( urls.size() );
		
		for(final  String  url : urls )
		{
			executor.execute
			(
				new  Runnable()
				{
					public  void  run()
					{
						try( Response  response =client.newCall(new  Request.Builder().url(url).build()).execute() )
						{
							synchronized(         DefaultServiceListRequestStrategy.this )
							{
								if(         services.isEmpty() )
								{
									if( response.code() == 200 )
									{
										services.addAll( (List<Service>)  JsonUtils.mapper.readValue(response.body().string(),JsonUtils.mapper.getTypeFactory().constructParametricType(List.class,Service.class)) );
									}
								}
							}
						}
						catch(    Throwable  ioe )
						{
							ioe.printStackTrace();
						}
						finally
						{
							cdlatcher.countDown();
						}
					}
				}
			);
		}
		
		try
		{
			cdlatcher.await(this.timeout,this.timeoutTimeUnit );
		}
		catch(InterruptedException  e )
		{
			
		}
		finally
		{
			executor.shutdownNow();
		}
		
		return     services;
		/*
		if( balancingProxies.isEmpty())
		{
			throw  new  IllegalStateException( "SQUIRREL-ROUTER:  ** DEFAULT  SERVICE  LIST  REQUESST  STRATEGY **  no  service  is  available." );
		}
		*/
	}
}