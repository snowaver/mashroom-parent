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

import  java.io.IOException;
import  java.util.ArrayList;
import  java.util.Iterator;
import  java.util.List;

import  com.fasterxml.jackson.core.JsonParser;
import  com.fasterxml.jackson.core.JsonProcessingException;
import  com.fasterxml.jackson.core.TreeNode;
import  com.fasterxml.jackson.databind.DeserializationContext;
import  com.fasterxml.jackson.databind.JsonDeserializer;
import  com.fasterxml.jackson.databind.JsonNode;
import  com.fasterxml.jackson.databind.node.ArrayNode;

import  cc.mashroom.util.collection.map.HashMap;
import  cc.mashroom.util.collection.map.Map;

public  class  MapDeserializer  extends  JsonDeserializer< Map >
{
	public  Object  deserializeValue(JsonNode  valNode )
	{
        if(     valNode.isInt() )
        {
            return      valNode.asInt();
        }
        else
        if(valNode.isBigInteger()  || valNode.isLong() )
        {
            return     valNode.asLong();
        }
        else
		if(    valNode.isNull() )
		{
			return  null;
		}
		else
        if( valNode.isFloat()    || valNode.isDouble() )
        {
            return   valNode.asDouble();
        }
        else
        if( valNode.isBoolean() )
        {
            return  valNode.asBoolean();
        }
        
        return  valNode.asText();
    }
	
	public  Map  deserialize( JsonParser  parser,DeserializationContext  context )  throws  IOException,JsonProcessingException
	{
		Map<String,Object>  map = new  HashMap<String,Object>();
		
		TreeNode  treeNode = parser.getCodec().readTree(parser);
		
		for( Iterator<String>  iterator = treeNode.fieldNames();  iterator.hasNext(); )
		{
			String  fieldName  = iterator.next();
			
			TreeNode  value = treeNode.get( fieldName );
			
			map.addEntry( fieldName,value instanceof ArrayNode ? deserialize((ArrayNode)  value) : deserializeValue((JsonNode)  value) );
		}
		
		return  map;
	}
	
	public  Object  deserialize( JsonNode  node )
	{
		if(      node.isArray() )
		{
			List<Object>  deserialized=new  ArrayList<Object>();
			
			for( JsonNode  secondaryNode : node )
			{
				deserialized.add( deserialize(secondaryNode ) );
			}
			
			return  deserialized;
		}
		else
		if(     node.isObject() )
		{
			Map<String,Object>  deserialized= new  HashMap<String,Object>();
			
			for( Iterator<String>  iterator = node.fieldNames();  iterator.hasNext(); )
			{
				String  field  = iterator.next();
				
				deserialized.addEntry( field,deserialize(node.get(field)) );
			}
			
			return  deserialized;
		}
		
		return  deserializeValue(node );
	}
}