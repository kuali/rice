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
package org.kuali.rice.kew.engine.node;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.kew.rule.xmlrouting.XPathHelper;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * A simple class for performing operations on RouteNode.  In particular, this class provides some
 * convenience methods for processing custom RouteNode XML content fragments. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RouteNodeUtils {

	/**
	 * Searches a RouteNode's "contentFragment" (it's XML definition) for an XML element with
	 * the given name and returns it's value.
	 * 
	 * <p>For example, in a node with the following definition:
	 *
	 * <pre><routeNode name="...">
	 *   ...
	 *   <myCustomProperty>propertyValue</myCustomProperty>
	 * </routeNode></pre>
	 * 
	 * <p>An invocation of getValueOfCustomProperty(routeNode, "myCustomProperty") would return
	 * "propertyValue".
	 * 
	 * @param routeNode RouteNode to examine
	 * @param propertyName name of the XML element to search for
	 * 
	 * @return the value of the XML element, or null if it could not be located
	 */
	public static String getValueOfCustomProperty(RouteNode routeNode, String propertyName) {
		String contentFragment = routeNode.getContentFragment();
		String elementValue = null;
		if (!StringUtils.isBlank(contentFragment)) {
			try {
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document document = db.parse(new InputSource(new StringReader(contentFragment)));	
				elementValue = XPathHelper.newXPath().evaluate("//" + propertyName, document);
			} catch (Exception e) {
				throw new RiceRuntimeException("Error when attempting to parse Document Type content fragment for property name: " + propertyName, e);
			}
		}
		return elementValue;
	}
	
}
