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
import org.w3c.dom.Node;

import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.edl.EDLContext;
import edu.iu.uis.eden.edl.EDLModelComponent;
import edu.iu.uis.eden.edl.EDLXmlUtils;
import edu.iu.uis.eden.edl.RequestParser;

/**
 * This class makes xml for the instructions template of widgets.  Processes config elements
 * instructions and createInstructions.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class InstructionsEDLComponent implements EDLModelComponent {

	public void updateDOM(Document dom, Element configElement, EDLContext edlContext) {
		
		Element edlElement = EDLXmlUtils.getEDLContent(dom, false);
		Element edlSubElement = EDLXmlUtils.getOrCreateChildElement(edlElement, "edl", true);
		WorkflowDocument document = (WorkflowDocument)edlContext.getRequestParser().getAttribute(RequestParser.WORKFLOW_DOCUMENT_SESSION_KEY);
		edlSubElement.setAttribute("title", document.getTitle());
		
		if(configElement.getTagName().equals("instructions")) {
			Node instTextNode = configElement.getChildNodes().item(0);
			if (instTextNode == null) {
				return ;
			}
			String instructions = instTextNode.getNodeValue(); 
			EDLXmlUtils.createTextElementOnParent(edlSubElement, "instructions", instructions);
			edlElement.setAttribute("title", instructions);	
		} else if (configElement.getTagName().equals("createInstructions")) {
			Node instTextNode = configElement.getChildNodes().item(0);
			if (instTextNode == null) {
				return ;
			}
			String instructions = instTextNode.getNodeValue();
			EDLXmlUtils.createTextElementOnParent(edlSubElement, "createInstructions", instructions);	
		}
	}

}
