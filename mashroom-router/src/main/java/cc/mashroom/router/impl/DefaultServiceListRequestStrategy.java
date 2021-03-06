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
import  java.util.List;

import  cc.mashroom.router.Service;
import  cc.mashroom.router.ServiceListRequestStrategy;
import  lombok.AllArgsConstructor;
import  okhttp3.OkHttpClient;

@AllArgsConstructor
public  class  DefaultServiceListRequestStrategy  implements  ServiceListRequestStrategy
{
	private  OkHttpClient  okHttpClient;
	
	private  Collection    <String>urls;
	
	public   List  <Service>   request()
	{
		ServiceListRequester   requester   = new  ServiceListRequester(  okHttpClient );
		
		for( String   url  : this.urls )
		{
			try
			{
				return  requester.request( url);
			}
			catch(Throwable  e )
			{
			e.printStackTrace();
			}
		}
		
		System.err.println( "MASHROOM-ROUTER:  ** DEFAULT  SERVICE  LIST  REQUEST  STRATEGY **  no  service  is  available,  use  backup  service  list  instead." );  return  null;
	}
}