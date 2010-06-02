/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.core.xml.schema;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.kuali.rice.core.util.RiceUtilities;
import org.kuali.rice.core.xml.TestSchemaLSResourceResolver;
import org.kuali.rice.core.xml.TestSchemaValidationErrorHandler;
import org.kuali.rice.ksb.messaging.resourceloader.KSBResourceLoaderFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * This factory class manages compiled XML .xsd schema files.
 * Schemas are immutable, in-memory representation of grammar specified in an .xsd file.
 * They represent a set of constraints that can be checked/ enforced against an XML document.  
 * They are object is thread safe, so they are shared here. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RiceXmlSchemaFactory {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RiceXmlSchemaFactory.class);
	private static final String SCHEMA_DIR = "classpath:schema/";

	private static ConcurrentHashMap schemaMap = new ConcurrentHashMap();
	
	public static Schema getSchema(String schemaName){
		Schema schema = null;
		if (schemaName != null) {
			// if it has already been found, return it, 
			// otherwise attempt to create it;
			schema = (Schema) schemaMap.get("schemaName");
			if (schema == null){
				schema  = addSchema(schemaName);
			}
		}
		return schema;
	}

	public static Schema addSchema(String schemaName){
		Schema schema = null;
		if (schemaName != null){
			schema = compileSchema(schemaName);
		}
		if (schema != null){
			schemaMap.put(schemaName, schema);
		}
		return schema;
	}

	public static Schema addSchema(String schemaName, InputStream stream){
		Schema schema = null;
		if (schemaName != null){
			schema = compileSchema(schemaName);
		}
		if (schema != null){
			schemaMap.put(schemaName, schema);
		}
		return schema;
	}
	
	public static Schema addSchema(String schemaName, ErrorHandler eHandler){
		Schema schema = null;
		if (schemaName != null){
			schema = compileSchema(schemaName, getInputStream(schemaName), eHandler);
		}
		if (schema != null){
			schemaMap.put(schemaName, schema);
		}
		return schema;
	}

	public static Schema addSchema(String schemaName, InputStream inStream, ErrorHandler eHandler){
		Schema schema = null;
		if (schemaName != null){
			// if no input stream specified, get from default location using schemaName as filename
			if (inStream == null){
				inStream = getInputStream(schemaName);
			}
			// if no error handler specified, use default
			if (eHandler == null){
				eHandler = new SchemaValidationErrorHandler();
			}
			schema = compileSchema(schemaName, inStream, eHandler);
		}
		if (schema != null){
			schemaMap.put(schemaName, schema);
		}
		return schema;
	}

	private static Schema compileSchema(String schemaName){
		InputStream inStream = getInputStream(schemaName);
		ErrorHandler eHandler = new SchemaValidationErrorHandler();
		return compileSchema(schemaName, inStream, eHandler);
	}
	
	
	private static Schema compileSchema(String schemaName, InputStream inStream, ErrorHandler errorHandler){
		Schema schema = null;
		// get input stream
		if (!schemaName.isEmpty()){
			if (inStream != null){
				try{
					String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
					SchemaFactory factory = SchemaFactory.newInstance(language);
					factory.setErrorHandler(errorHandler);
					factory.setResourceResolver( new SchemaLSResourceResolver());
					StreamSource ss = new StreamSource(inStream);
					schema = factory.newSchema(ss);
				} catch (SAXException e) {
					LOG.error("The input schema ("+schemaName+") encountered a parsing error");
					LOG.error(e.getMessage(), e);
					e.printStackTrace();
				} 
			}
		}
		return schema;
	}

	private static InputStream getInputStream(String schemaName){
		InputStream xmlFile = null;
		if (!schemaName.isEmpty()){
			try {
				// this will look in classpath:/schema directory
				xmlFile = RiceUtilities.getResourceAsStream(SCHEMA_DIR + schemaName);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				throw new RuntimeException("Error getting XML Schema Input Stream using filename: "+schemaName, e);
			}
		}
		return xmlFile;
	}
}
