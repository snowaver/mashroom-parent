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

import  java.util.ArrayList;
import  java.util.List;

import  org.apache.commons.lang3.RandomUtils;

import  lombok.Getter;
import  lombok.RequiredArgsConstructor;
import  lombok.Setter;
import  lombok.experimental.Accessors;

@RequiredArgsConstructor
public  class  ServiceRouteManager
{
	@Getter
	private  ServiceChangeEventDispatcher  serviceChangeEventDispatcher= new  ServiceChangeEventDispatcher();
	@Accessors( chain = true )
	@Setter
	private  ServiceListRequestStrategy    strategy;
	private  int  currentServiceIndex=-1;
	private  List<Service>  services= new  ArrayList<Service>();
	@Getter
	private  ServiceListRequestEventDispatcher   serviceListRequestEventDispatcher= new  ServiceListRequestEventDispatcher();
	
	public   synchronized   List<Service>  request()
	{
		synchronized  ( this )
		{
			serviceListRequestEventDispatcher.onBeforeRequest();
			
			List<Service>  services =   this.strategy.request();
			
		this.services.clear();  this.services.addAll( services);
			
			serviceListRequestEventDispatcher.onRequestComplete( services );
			
		return  this.services;
		}
	}
	
	public  Service  service()
	{
		synchronized  ( this )
		{
			return  this.currentServiceIndex < 0 ? null : this.services.get(      this.currentServiceIndex );
		}
	}
	
	public  Service  tryNext()
	{
		synchronized  ( this )
		{
			this.serviceChangeEventDispatcher.onChanged( this.currentServiceIndex == -1 ? null : this.services.get(this.currentServiceIndex),this.services.get(this.currentServiceIndex = this.currentServiceIndex < 0 ? RandomUtils.nextInt(0, this.services.size()) : (this.currentServiceIndex == this.services.size()-1 ? 0 : this.currentServiceIndex+1)) );  return  service();
		}
	}
}