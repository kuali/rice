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

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.edl.EDLXmlUtils;
import edu.iu.uis.eden.edl.RequestParser;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.user.AuthenticationUserId;

/**
 * Matches network ID param to UserService to validate network Id.  Returns error message if networkId does NOT match.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class NetworkIdWorkflowEDLConfigComponent extends SimpleWorkflowEDLConfigComponent {
	
	private boolean required = false;
	
	@Override
	public Element getReplacementConfigElement(Element element) {
		Element replacementEl = (Element)element.cloneNode(true);
		Element type = (Element)((NodeList)replacementEl.getElementsByTagName(EDLXmlUtils.TYPE_E)).item(0);
		type.setTextContent("text");
		
		//find the validation element if required is true set a boolean and determin if blanks
		//are allowed based on that
		Element validation = (Element)((NodeList)replacementEl.getElementsByTagName(EDLXmlUtils.VALIDATION_E)).item(0);
		if (validation != null && validation.getAttribute("required").equals("true")) {
			required = true;
		}
		return replacementEl;
	}
	
	@Override
	public String getErrorMessage(Element originalConfigElement, RequestParser requestParser, MatchingParam param) {
		
		if (param.getParamValue().length() == 0 && required == true) {
			//empty and required so send error
			return ("Network ID is a required field");
		} else if (param.getParamValue().length() == 0 && required == false) { 
			//empty but not required then just return 
			return null;			
		} else {
			//not blank validate as normal whether required or not
			try {
				KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(param.getParamValue()));
			} catch (EdenUserNotFoundException e) {
				return ("The value " + param.getParamValue() + " is an invalid Network ID");
			}
		}
		return null;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

}
