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

import  java.net.InetAddress;
import  java.net.URL;
import  java.util.HashSet;
import  java.util.LinkedList;
import  java.util.List;
import  java.util.Set;
import  java.util.concurrent.CountDownLatch;
import  java.util.concurrent.LinkedBlockingQueue;
import  java.util.concurrent.ThreadPoolExecutor;
import  java.util.concurrent.TimeUnit;

import  javax.net.ssl.SSLSocketFactory;

import  com.google.common.collect.Sets;

import  cc.mashroom.util.JsonUtils;
import  cc.mashroom.util.NoopHostnameVerifier;
import  cc.mashroom.util.NoopX509TrustManager;
import  lombok.AccessLevel;
import  lombok.Setter;
import  lombok.experimental.Accessors;
import  okhttp3.OkHttpClient;
import  okhttp3.Request;
import  okhttp3.Response;

public  class  DefaultBalancingProxyFactory  implements  BalancingProxyFactory
{
	public  DefaultBalancingProxyFactory( URL  url,List<String>  backupAddresses,SSLSocketFactory  sslSocketFactory,long  timeout,TimeUnit  timeunit )
	{
		this.setBackupAddresses(new  LinkedList<String>(backupAddresses)).setUrl(url).setClient( new  OkHttpClient.Builder().hostnameVerifier(new  NoopHostnameVerifier()).sslSocketFactory(sslSocketFactory,new  NoopX509TrustManager()).connectTimeout(2,TimeUnit.SECONDS).readTimeout(timeout,timeunit).build() );
	}
	
	@Setter( value= AccessLevel.PROTECTED )
	@Accessors( chain= true )
	private  OkHttpClient  client;
	@Setter( value= AccessLevel.PROTECTED )
	@Accessors( chain= true )
	private  URL  url;
	@Setter( value= AccessLevel.PROTECTED )
	@Accessors( chain= true )
	private  List<String>  backupAddresses;
		
	public  List<BalancingProxy>  get()
	{
		String  address = "";
		
		try
		{
			address = InetAddress.getByName( url.getHost() ).getHostAddress();
			
			return  invokeAny( true,Sets.newHashSet(url.toString().replaceFirst(url.getProtocol()+":\\/\\/.*:[0-9]+\\/",url.getProtocol()+"://"+address+":"+url.getPort()+"/")) );
		}
		catch( Throwable  e )
		{
			
		}
		
		Set<String>  urls = new  HashSet<String>();
		
		for( String  backupAddress : backupAddresses )
		{
			if( ! address.equals( backupAddress ) )
			{
				urls.add( url.toString().replaceFirst(url.getProtocol()+":\\/\\/.*:[0-9]+\\/",url.getProtocol()+"://"+backupAddress+":"+url.getPort()+"/") );
			}
		}
		
		return  invokeAny(false,urls );
	}
	
	private  List<BalancingProxy>  invokeAny(boolean  errorPermissible,Set<String>  urls )
	{
		final  List<BalancingProxy>  balancingProxies = new  LinkedList<BalancingProxy>();
		
		if( urls.isEmpty() )
		{
			return    balancingProxies;
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
						try( Response  response = client.newCall(new  Request.Builder().url(url).build()).execute() )
						{
							synchronized( DefaultBalancingProxyFactory.class )
							{
								if( balancingProxies.isEmpty() )
								{
									if( response.code() == 200 )
									{
										String  connectingHost  = new  URL(url).getHost();
										
										for( BalancingProxy  balancingProxy : (List<BalancingProxy>)  JsonUtils.mapper.readValue(response.body().string(),JsonUtils.mapper.getTypeFactory().constructParametricType(List.class,BalancingProxy.class)) )
										{
											if( connectingHost.equals(balancingProxy.getHost().trim()) )
											{
												balancingProxies.add(    balancingProxy );
											}
										}
									}
								}
							}
						}
						catch( Throwable  ioe )
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
			cdlatcher.await( 5,TimeUnit.SECONDS );
		}
		catch( InterruptedException  e )
		{
			
		}
		
		executor.shutdownNow();
		
		return    balancingProxies;
		/*
		if( balancingProxies.isEmpty() )
		{
			throw  new  IllegalStateException( "SQUIRREL-ROUTER:  ** DEFAULT  BALANCING  PROXY  FACTORY **  no  proxy  is  available" );
		}
		*/
	}
}