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
package cc.mashroom.xcache.util;

import  java.util.concurrent.Callable;
import  java.util.concurrent.TimeUnit;
import  java.util.concurrent.locks.Lock;

import  cc.mashroom.xcache.CacheFactory;
import  cc.mashroom.xcache.XKeyValueCache;
import  cc.mashroom.xcache.atomic.XAtomicLong;

public  class     SafeCacher
{
	public  static  XAtomicLong  createAndSetIfAbsent(String  name,Callable  <Long>  callable )
	{
		XAtomicLong  value = CacheFactory.atomicLong( name,false );
		
		try
		{
		if( value ==  null )
			{
				Long  ival =  callable.call();
				
				(value=CacheFactory.atomicLong(name,true)).compareAndSet( 0  , ival == null ? 0 : ival );
			}
		}
		catch(Throwable  e )
		{
			throw  new  RuntimeException( e );
		}
		
		return  value;
	}
	
	/**
	 *  acquire  the  lock,  call  the  callable  and  cache  the  value  if  not  cached,  returns  the  cached  value  and  unlock  the  locker  finally.
	 */
	public  static  <K,V>  V  cache( Lock  locker,long  acquireLockTimeout,TimeUnit  acquireLockTimeoutTimeUnit,XKeyValueCache<K,V>  cache,K  key,Callable<V>  callable )
	{
		V  value     = null;
		
		try
		{
			if( !locker.tryLock(acquireLockTimeout  ,acquireLockTimeoutTimeUnit ) )
			{
				throw  new  IllegalStateException( "SQUIRREL-XCACHE:  ** SAFE  CACHER **  the  lock  is  not  acquired  before  the  waiting  time  (2  seconds)  elapsed,  give  up." );
			}
			
			if( (value = cache.get(key)) == null )  value = callable.call();
		}
		catch(Throwable  e )
		{
			throw  new  RuntimeException( e );
		}
		finally
		{
			locker.unlock();
		}
		
		return  value;
	}
}