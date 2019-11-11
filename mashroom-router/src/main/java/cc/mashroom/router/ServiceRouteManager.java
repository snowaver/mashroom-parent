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

import  org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import  org.apache.commons.lang3.RandomUtils;

import  com.fasterxml.jackson.core.type.TypeReference;
import  com.google.common.collect.Lists;

import  cc.mashroom.router.Service.Schema;
import  cc.mashroom.util.ObjectUtils;
import  cc.mashroom.util.collection.map.HashMap;
import  cc.mashroom.util.collection.map.Map;
import  lombok.Getter;
import  lombok.NonNull;
import  lombok.RequiredArgsConstructor;
import  lombok.Setter;
import  lombok.experimental.Accessors;

@RequiredArgsConstructor
public  class  ServiceRouteManager
{
	@Getter
	private  ServiceListRequestEventDispatcher  serviceListRequestEventDispatcher = new  ServiceListRequestEventDispatcher();
	
	private  ArrayListValuedHashMap<Schema,Service>  services = new  ArrayListValuedHashMap<Schema,Service>();
	
	private  Map<Schema,Service>  currents=new   HashMap<Schema, Service>();
	@Getter
	private  boolean isRequested=false;
	@Accessors( chain = true )
	@Setter
	@NonNull
	private  ServiceListRequestStrategy          serviceListRequestStrategy;
	@Getter
	private  ServiceChangeEventDispatcher  serviceChangeEventDispatcher = new  ServiceChangeEventDispatcher();
	
	public  Service  tryNext( Schema  schema )
	{
		List<Service>  services = new  ArrayList<Service>( this.services.get(schema) );
		
		Service  currentService = this.current(schema );
		
		Service  nextService = services.isEmpty() ? null : services.get( currentService == null ? RandomUtils.nextInt(0,services.size()) : (services.indexOf(currentService) == services.size()-1 ? 0 : services.indexOf(currentService)+1) );
		
		serviceChangeEventDispatcher.onChanged(currentService,nextService );  return  nextService;
	}
	
	public  Service  current( Schema  schema )
	{
		return  currents.get( schema );
	}
	
	public  List<Service> getServices()
	{
		return  Lists.newArrayList( services.values() );
	}
	
	public  synchronized  ArrayListValuedHashMap<Schema, Service>  request()
	{
		if( isRequested )
		{
			throw  new  IllegalStateException( "MASHROOM-ROUTER:  ** SERVICE  ROUTE  MANAGER **  the  service  list  is  requested  already." );
		}
		
		this.serviceListRequestEventDispatcher.onBeforeRequest();
		
		ArrayListValuedHashMap<Schema,Service>  requestedServices = this.serviceListRequestStrategy.request();
		
		this.isRequested = !requestedServices.isEmpty();
		
		this.services.clear();  this.services.putAll(   requestedServices );
		
		this.serviceListRequestEventDispatcher.onRequestComplete( ObjectUtils.cast(this.services.values(), new  TypeReference<ArrayList<Service>>(){}) );  return  services;
	}
}