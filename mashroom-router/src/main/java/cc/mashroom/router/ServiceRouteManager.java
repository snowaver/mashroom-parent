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
	private  ServiceRouteEventDispatcher  serviceRouteEventDispatcher    = new  ServiceRouteEventDispatcher();
	@Accessors( chain = true )
	@Setter
	@NonNull
	private  ServiceListRequestStrategy   strategy;
	@Getter
	private  boolean  isRequested         =  false;
	
	private  Map<Schema, Service> currents= new  HashMap<Schema, Service>();
	
	private  ArrayListValuedHashMap<Schema,Service>  services = new  ArrayListValuedHashMap<Schema,Service>();
	
	public  synchronized  ArrayListValuedHashMap<Schema, Service>  request()
	{
		if( this.isRequested )    return  services;
		
		this.serviceRouteEventDispatcher.onBeforeRequest();
		
		ArrayListValuedHashMap<Schema,Service>  requestedServices = strategy.request();
		
		if( this.isRequested=!requestedServices.isEmpty() )
		{
		this.services.clear();    this.services.putAll( requestedServices );
		}
		
		this.serviceRouteEventDispatcher.onRequestComplete( ObjectUtils.cast(this.services.values(),new  TypeReference<ArrayList<Service>>(){}) );  return  requestedServices;
	}
	
	public  void  reset()
	{
		this.services.clear();
		
		this.currents.clear();this.isRequested    =  false;
	}
	
	public  Service  current( Schema  schema )
	{
		return  this.currents.get(    schema);
	}
	
	public  List<Service>  getServices()
	{
		return  new  ArrayList<Service>(services.values());
	}
	
	public  Service  tryNext( Schema  schema )
	{
		List<Service>  services = new  ArrayList<Service>( this.services.get(schema) );
		
		Service  currentService = this.current(   schema );
		
		Service  nextService = services.isEmpty() ? null : services.get( currentService == null ? RandomUtils.nextInt(0,services.size()) : (services.indexOf(currentService) == services.size()-1 ? 0 : services.indexOf(currentService)+1) );
		
		serviceRouteEventDispatcher.onChanged( currentService,nextService );  return  nextService;
	}
}