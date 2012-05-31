/*
 * Copyright 2007-2010 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.core.xml.schema;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.kuali.rice.core.util.RiceUtilities;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * This is an entity resolver for loading schemas.
 * It finds resources in the impl/src/main/resources/schema directory.
 * 
 * TODO:  Handle schemas that are formally published
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class SchemaLSResourceResolver implements LSResourceResolver {

	private static final String SCHEMA_DIR = "classpath:schema/";
	/**
	 * This overridden method currently looks for resources in the "classpath:schema/" directory.
	 * 
	 * @see org.w3c.dom.ls.LSResourceResolver#resolveResource(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public LSInput resolveResource(String type, String namespaceURI, String publicId,
			String systemId, String baseURI) {
		
		LSInput input = null;
		try{
			InputStream xsdFile = RiceUtilities.getResourceAsStream(SCHEMA_DIR + systemId);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			DOMImplementation domImpl = builder.getDOMImplementation();
			DOMImplementationLS dils = (DOMImplementationLS) domImpl;
			input = dils.createLSInput();
			input.setByteStream(xsdFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return input;
	}

}
