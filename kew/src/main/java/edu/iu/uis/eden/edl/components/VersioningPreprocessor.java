/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.edl.components;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.edl.EDLContext;
import edu.iu.uis.eden.edl.EDLModelComponent;
import edu.iu.uis.eden.edl.EDLXmlUtils;
import edu.iu.uis.eden.edl.RequestParser;

/**
 * Versions the data element if necessary by checking 'currentVersion' param on request.  If this request is 
 * a doc handler request this will configure the dom so the next request will cause the data to be incremented.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class VersioningPreprocessor implements EDLModelComponent {

	public void updateDOM(Document dom, Element configElement, EDLContext edlContext) {
		RequestParser requestParser = edlContext.getRequestParser();
		Element edlContentElement = EDLXmlUtils.getEDLContent(dom, false);
		Element dataElement = EDLXmlUtils.getDataFromEDLDocument(edlContentElement, false);
		Element currentVersion = findCurrentVersion(dom);
		if (currentVersion == null) {
			Integer currentVersionCount = new Integer(0);
			currentVersion = EDLXmlUtils.getVersionFromData(dataElement, currentVersionCount);
		} else if (requestParser.getParameterValue("incrementVersion") != null || requestParser.getParameterValue("methodToCall") != null)  {
//		} else { 
			currentVersion.getAttributeNode("current").setNodeValue("false");
			int currentVersionCount = new Integer(currentVersion.getAttribute("version")).intValue() + 1;
			EDLXmlUtils.getVersionFromData(dataElement, new Integer(currentVersionCount));
		}
		requestParser.setAttribute("currentVersion", currentVersion.getAttribute("currentVersion"));
		if (requestParser.getParameterValue("command") != null) { 
			EDLXmlUtils.createTextElementOnParent(currentVersion, "incrementVersion", "true");
		}
	}

	public static Element findCurrentVersion(Document dom) {
		Element edlContentElement = EDLXmlUtils.getEDLContent(dom, false);
		Element dataElement = EDLXmlUtils.getDataFromEDLDocument(edlContentElement, false);
		NodeList versionElements = dataElement.getElementsByTagName(EDLXmlUtils.VERSION_E);
		for (int i = 0; i < versionElements.getLength(); i++) {
			Element version = (Element) versionElements.item(i);
			Boolean currentVersion = new Boolean(version.getAttribute("current"));
			if (currentVersion.booleanValue()) {
				return version;
			}
		}
		return null;
	}

}
