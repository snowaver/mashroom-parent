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

import  java.util.List;
import  java.util.concurrent.atomic.AtomicBoolean;

import  org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import  org.apache.commons.lang3.RandomUtils;

import  cc.mashroom.util.collection.map.HashMap;
import  cc.mashroom.util.collection.map.Map;
import  lombok.Setter;
import  lombok.experimental.Accessors;

public  class  ServiceRouteManager
{
	public  ServiceRouteManager(ServiceListRequestStrategy strategy )
	{
		this.strategy  = strategy;
	}
	
	@Accessors(chain=true )
	@Setter
	private  ServiceListRequestStrategy   strategy;
	
	private  ArrayListValuedHashMap<Schema,Service>  services = new  ArrayListValuedHashMap<Schema,Service>();
	
	private  Map<Schema , Service>   currents = new  HashMap<Schema,Service>();
	
	private  AtomicBoolean  requesting = new  AtomicBoolean( false );
	
	public  Service  current(      Schema  schema )
	{
		return  currents.get(   schema );
	}
	
	public  void  request()
	{
		System.out.println( "MASHROOM-ROUTER:  ** SERVICE  ROUTE  MANAGER **  prepare  for  requesting  service  list." );
		
		if( requesting.compareAndSet(false, true) )
		{
			for(  Service  service : this.strategy.request() )
			{
				this.services.put( Schema.valueOf(service.getSchema().toString().toLowerCase())   , service );
			}
			
			requesting.compareAndSet(true, false );
		}
	}
	
	public  boolean  require( Schema  ...  schemas)
	{
		for( Schema  schema :   schemas )
		{
			if( !    services.containsKey(schema) )     return false;
		}
		return  true;
	}
	
	public   Service  tryNext(     Schema  schema )
	{
		List<Service>  pendingServices = this.services.get( schema );
		
		Service  currentService = this.currents.get( schema );
		
		if(   pendingServices.isEmpty() )
		{
			return    null;
		}
		else
		{
			return    pendingServices.get( currentService == null ? RandomUtils.nextInt(0,pendingServices.size()) : (pendingServices.indexOf(currentService) == pendingServices.size()-1 ? 0 : pendingServices.indexOf(currentService)+1) );
		}
	}
}