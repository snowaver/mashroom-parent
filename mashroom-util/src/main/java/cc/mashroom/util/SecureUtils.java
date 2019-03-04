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

import  java.io.InputStream;
import  java.security.KeyStore;
import  java.security.SecureRandom;
import  java.security.cert.CertificateFactory;

import  javax.net.ssl.KeyManagerFactory;
import  javax.net.ssl.SSLContext;
import  javax.net.ssl.TrustManagerFactory;

public  class  SecureUtils
{
	public  static  SSLContext  getSSLContext(String...  certificateResourcePaths )
	{
	    try
	    {
	        CertificateFactory  certificateFactory = CertificateFactory.getInstance( "X.509" );
	        
	        KeyStore  keyStore = KeyStore.getInstance( KeyStore.getDefaultType() );
	        
	        keyStore.load(null);
	        
	        int  index   = 0;
	        
	        for( String  certificatePath : certificateResourcePaths )
	        {
	        	try( InputStream  is = SecureUtils.class.getResourceAsStream(certificatePath) )
	        	{
	        		keyStore.setCertificateEntry( Integer.toString(index++),certificateFactory.generateCertificate(is) );
	        	}
	        }
	        
	        SSLContext  sslContext = SSLContext.getInstance( "TLS" );
	        
	        TrustManagerFactory  trustManagerFactory = TrustManagerFactory.getInstance( TrustManagerFactory.getDefaultAlgorithm() );
	        
	        trustManagerFactory.init(  keyStore );
	        
	        sslContext.init( null,trustManagerFactory.getTrustManagers(),new  SecureRandom() );
	        
	        return   sslContext;
	        
	    }
	    catch( Throwable  e )
	    {
	        e.printStackTrace();
	    }
	    
	    return  null;
	}
	
	public  static  SSLContext  getSSLContext( String  password,String  keystoreResourcePath )
	{
	    try
	    {
	        KeyStore  keyStore = KeyStore.getInstance( KeyStore.getDefaultType() );
	        
	        try( InputStream  is=SecureUtils.class.getResourceAsStream(keystoreResourcePath) )
        	{
	        	keyStore.load( is,password.toCharArray() );
        	}
	        
	        KeyManagerFactory  keyManagerFactory         = KeyManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
	        
	        keyManagerFactory.init(keyStore,password.toCharArray() );
	        
	        SSLContext  sslContext = SSLContext.getInstance( "TLS" );
	        
	        sslContext.init(    keyManagerFactory.getKeyManagers() , null , null );
	        
	        return   sslContext;
	        
	    }
	    catch( Throwable  e )
	    {
	        e.printStackTrace();
	    }
	    
	    return  null;
	}
}