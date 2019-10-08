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

public  interface  XAtomicLong
{
	public  long  decrementAndGet()  throws  RuntimeException;
	public  long  getAndDecrement()  throws  RuntimeException;
	public  long  incrementAndGet()  throws  RuntimeException;
	public  long  getAndIncrement()  throws  RuntimeException;
	public  long  addAndGet( long  delta )  throws  RuntimeException;
	public  long  getAndAdd( long  delta )  throws  RuntimeException;
	public  long  get()  throws  RuntimeException;
	public  void  set(long  value )  throws  RuntimeException;
	public  boolean  compareAndSet(long  expectValue,long  newValue )  throws  RuntimeException;
}