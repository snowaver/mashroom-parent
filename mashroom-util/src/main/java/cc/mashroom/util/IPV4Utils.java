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
package cc.mashroom.util;

public  class  IPV4Utils
{
	public  static  int  toInt( String  stringIp )
	{
		byte[]  ipBytes  = new  byte[ 4 ];
		
		for( int  i = 0;i <= 3;i = i + 1 )
		{
			ipBytes[i] = (byte)  ( Integer.parseInt(StringUtils.split(stringIp,'.')[i]) & 0xFF );
		}
		
		int  intIp = (ipBytes[3] & 0xFF );
		
		intIp = intIp | ( (ipBytes[2] << 8 ) &     0xFF00 );
		
		intIp = intIp | ( (ipBytes[1] << 16) &   0xFF0000 );
		
		intIp = intIp | ( (ipBytes[0] << 24) & 0xFF000000 );
		
		return  intIp;
	}
	
	public  static  String  toString( int  intIp )
	{
		return  new  StringBuilder().append(((intIp >> 24) & 0xFF)).append('.').append((intIp >> 16) & 0xFF).append('.').append((intIp >> 8) & 0xFF).append('.').append((intIp & 0xFF)).toString();
	}
}