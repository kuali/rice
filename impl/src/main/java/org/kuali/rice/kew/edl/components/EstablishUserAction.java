/*
 * Copyright 2005-2008 The Kuali Foundation
 *
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
package org.kuali.rice.kew.edl.components;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.edl.EDLContext;
import org.kuali.rice.kew.edl.EDLModelComponent;
import org.kuali.rice.kew.edl.RequestParser;
import org.kuali.rice.kew.edl.UserAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Handles establishing what action the user submitted.  It's important to normalize this because
 * the action could be submitted in the "userAction" request parameter, in the "command" request
 * parameter or not passed at all.
 * 
 * <p>This is primarily important in identifying whether the submission is the first-time "load"
 * of a document or an action being executed against an already loaded document.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EstablishUserAction implements EDLModelComponent {
	
    public static final String USER_ACTION_PARAM = "userAction";
    public static final String COMMAND_PARAM = "command";
    
    public void updateDOM(Document dom, Element configElement, EDLContext edlContext) {
	RequestParser requestParser = edlContext.getRequestParser();
	String userAction = requestParser.getParameterValue(USER_ACTION_PARAM);
	if (StringUtils.isEmpty(userAction)) {
	    String command = requestParser.getParameterValue(COMMAND_PARAM);
	    if (!StringUtils.isEmpty(command)) {
		// from Workflow Quick Links, the "command" parameter will be passed with a value of "initiate"
		if (UserAction.ACTION_CREATE.equals(command)) {
		    userAction = UserAction.ACTION_CREATE;
		}
		// from Document Search/Action List a command parameter is passed but we want to load the document
		userAction = UserAction.ACTION_LOAD;
	    } else {
		userAction = UserAction.ACTION_UNDEFINED;
	    }
	}
	edlContext.setUserAction(new UserAction(userAction));
    }
	
}
