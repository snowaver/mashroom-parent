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
package cc.mashroom.xcache.atomic;

import java.util.concurrent.atomic.AtomicLong;

public  class  NativeAtomicLong  implements  XAtomicLong
{
	private AtomicLong   atomicLong = new  AtomicLong();
	
	@Override
	public  long  incrementAndGet()
	{
		return  this.atomicLong.incrementAndGet();
	}
	@Override
	public  long  getAndIncrement()
	{
		return  this.atomicLong.getAndIncrement();
	}
	@Override
	public  long  get()
	{
		return    atomicLong.get();
	}
	@Override
	public  long  decrementAndGet()
	{
		return  this.atomicLong.decrementAndGet();
	}
	@Override
	public  long  getAndDecrement()
	{
		return  this.atomicLong.getAndDecrement();
	}
	@Override
	public  long  addAndGet( long  delta )
	{
		return  this.atomicLong.addAndGet(delta );
	}
	@Override
	public  long  getAndAdd( long  delta )
	{
		return  this.atomicLong.getAndAdd(delta );
	}
	@Override
	public  void  set(long  value )
	{
		this.atomicLong.set(value);
	}
	@Override
	public  boolean  compareAndSet( long  expectValue,long  newValue )
	{
		return  this.atomicLong.compareAndSet( expectValue ,newValue);
	}
}