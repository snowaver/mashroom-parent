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
import  java.util.Collection;
import  java.util.List;
import  java.util.concurrent.CopyOnWriteArrayList;

import  org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import  org.apache.commons.lang3.RandomUtils;

import  cc.mashroom.util.CollectionUtils;
import  cc.mashroom.util.collection.map.HashMap;
import  cc.mashroom.util.collection.map.Map;
import  lombok.Getter;
import  lombok.Setter;
import  lombok.experimental.Accessors;

public  class  ServiceRouteManager
{
	public  ServiceRouteManager(ServiceListRequestStrategy strategy )
	{
		this.strategy  = strategy;
	}
	
	@Accessors( chain=true )
	@Setter
	private  ServiceListRequestStrategy   strategy;
	
	private  List<ServiceRouteListener>   listeners       = new  CopyOnWriteArrayList<ServiceRouteListener>();
	
	private  Map<Long,Service>  ids   = new  HashMap<Long,Service>();
	
	private  ArrayListValuedHashMap<Schema,Service>  services = new  ArrayListValuedHashMap<Schema,Service>();
	
	private  Map<Schema , Service>   currents = new  HashMap<Schema,Service>();
	@Getter
	private  boolean  isRequested= false;
	
	public  void  addListener(       ServiceRouteListener  listener )
	{
		CollectionUtils.addIfAbsent( this.listeners,listener);
	}
	
	public  void  removeListener(    ServiceRouteListener  listener )
	{
		CollectionUtils.remove(      this.listeners,listener);
	}
	
	public   List<Service>  getServices()
	{
		return  new  ArrayList<Service>(    this.services.values() );
	}
	
	public  Service  current(      Schema  schema )
	{
		return  currents.get(   schema );
	}
	
	public  void     clear()
	{
		services.clear();
		
		this.ids.clear();
		
		currents.clear();
	}
	
	public  synchronized  void  request()
	{
//		if( !  isRequested )
		{
			try
			{
				for( ServiceRouteListener  listener :this.listeners )  listener.onBeforeRequest();
			}
			catch( Throwable  th )   { th.printStackTrace(); }
			
			List<Service>  services = this.strategy.request();
			
			try
			{
				for( ServiceRouteListener  listener :this.listeners )   listener.onRequestComplete( services );
			}
			catch( Throwable  th )   { th.printStackTrace(); }
			
			if( !    services.isEmpty() )
			{
				clear( );
				
				add(   services );
				
				this.isRequested =  true;
			}
		}
	}
	
	public  void  add(     Collection<Service>   newServices )
	{
		for(    Service  newService : newServices )
		{
			Service  oldService   = ids.remove( newService.getId() );
			
			if(      oldService != null )
			{
				this.services.removeMapping( Schema.valueOf(oldService.getSchema().toUpperCase()),oldService );
			}
			this.ids.put(     newService.getId(),newService );
			
			this.services.put( Schema.valueOf(newService.getSchema().toUpperCase()),newService  );
		}
	}
	
	public   Service  tryNext(     Schema  schema )
	{
		List<Service>  pendingServices = this.services.get( schema );
		
		Service  currentService = this.currents.get( schema );
		
		Service  nextService = pendingServices.isEmpty() ? null : pendingServices.get( currentService == null ? RandomUtils.nextInt(0,pendingServices.size()) : (pendingServices.indexOf(currentService) == pendingServices.size()-1 ? 0 : pendingServices.indexOf(currentService)+1) );
		{
			try
			{
				for( ServiceRouteListener  listener :this.listeners )  listener.onChanged( currentService , nextService );
			}
			catch( Throwable  th )   { th.printStackTrace(); }
		}
		
		return  nextService;
	}
}