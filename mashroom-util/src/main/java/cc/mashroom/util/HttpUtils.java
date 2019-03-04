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

import  java.io.UnsupportedEncodingException;
import  java.net.URLDecoder;
import  java.net.URLEncoder;

public  class  HttpUtils
{
	public  static  String  encodeQuietly( String  string,String  enc )
	{
		try
		{
			return  URLEncoder.encode( string,enc );
		}
		catch( UnsupportedEncodingException  e )
		{
			e.printStackTrace();
		}
		
		return  null;
	}
	
	public  static  String  decodeQuietly( String  string,String  enc )
	{
		try
		{
			return  URLDecoder.decode( string,enc );
		}
		catch( UnsupportedEncodingException  e )
		{
			e.printStackTrace();
		}
		
		return  null;
	}
}