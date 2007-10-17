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
package edu.iu.uis.eden.edl;

import java.util.Stack;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.XmlHelper;
import edu.iu.uis.eden.web.UserLoginFilter;

/**
 * Contains a stack of EDLControllers.  Allows EDL components to add new controllers to the chain runtime.  The idea 
 * being that this is how you would page between edls.  Uses the template associated with the last edl controller 
 * to render the dom of the last edl controller.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class EDLControllerChain {

	private static final Logger LOG = Logger.getLogger(EDLControllerChain.class);
	
	private Stack edlControllers = new Stack();
	
	public void renderEDL(RequestParser requestParser, HttpServletResponse response) throws Exception {
		EDLController edlController = (EDLController) edlControllers.pop();
		edlController.setEdlContext(getEDLContext(edlController, requestParser, edlController.getStyle().newTransformer()));
		
		Document dom = edlController.notifyComponents();
		if (edlControllers.isEmpty()) {
			transform(edlController.getEdlContext(), dom, response);
		} else {
			renderEDL(requestParser, response);
		}
	}
	
	public void addEdlController(EDLController edlController) {
		edlControllers.add(edlController);
	}
	
	private void transform(EDLContext edlContext, Document dom, HttpServletResponse response) throws Exception {        
		Transformer transformer = edlContext.getTransformer();
		
        transformer.setOutputProperty("indent", "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        String user = null;
        String loggedInUser = null; 
        if (edlContext.getUserSession() != null) {
            WorkflowUser wu = edlContext.getUserSession().getWorkflowUser();
            if (wu != null) user = wu.getWorkflowUserId().getId();
            wu = edlContext.getUserSession().getLoggedInWorkflowUser();
            if (wu != null) loggedInUser = wu.getWorkflowUserId().getId();
        }
        transformer.setParameter("user", user);
        transformer.setParameter("loggedInUser", loggedInUser);
        if (LOG.isDebugEnabled()) {
        	LOG.debug("Transforming dom " + XmlHelper.jotNode(dom, true));
        }
        transformer.transform(new DOMSource(dom), new StreamResult(response.getOutputStream()));
	}
	
	private EDLContext getEDLContext(EDLController edlController, RequestParser requestParser, Transformer transformer) {
		EDLContext edlContext = new EDLContext();
		edlContext.setEdlControllerChain(this);
		edlContext.setEdocLiteAssociation(edlController.getEdocLiteAssociation());
		edlContext.setUserSession(UserLoginFilter.getUserSession(requestParser.getRequest()));
		edlContext.setTransformer(transformer);
		edlContext.setRequestParser(requestParser);
		return edlContext;
	}

}