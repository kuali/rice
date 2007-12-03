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
package edu.iu.uis.eden.engine.node;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.jdom.Element;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.RouteHelper;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routeheader.StandardDocumentContent;
import edu.iu.uis.eden.user.EmplId;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.XmlHelper;

/**
 * A node which will generate an FYI request to a university ID specified in the document content.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class FYIByUniversityId extends RequestActivationNode {
    private static final Logger LOG = Logger.getLogger(FYIByUniversityId.class);

	public SimpleResult process(RouteContext context, RouteHelper helper)
			throws Exception {

        LOG.debug("processing FYIByUniversityId node");
        Element rootElement = getRootElement(new StandardDocumentContent(context.getDocument().getDocContent()));
 		List fieldElements = XmlHelper.findElements(rootElement, "field");
        ListIterator elementIter = fieldElements.listIterator();
        while (elementIter.hasNext()) {
        	Element field = (Element) elementIter.next();
        	Element version = field.getParentElement();
        	if (version.getAttribute("current").getValue().equals("true")) {
        		LOG.debug("Looking for studentUid field:  " + field.getAttributeValue("name"));
               	if (field.getAttribute("name")!= null && field.getAttributeValue("name").equals("studentUid")) {
            		LOG.debug("Should send an FYI to UID:  " + field.getChildText("value"));
               		if (field.getChildText("value") != null) {
               			WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new EmplId(field.getChildText("value")));
               			//WorkflowDocument wfDoc = new WorkflowDocument(new EmplIdVO(field.getChildText("value")), routeHeaderId);
               			if (!context.isSimulation()) {
                   			KEWServiceLocator.getWorkflowDocumentService().appSpecificRouteDocument(user, context.getDocument(), EdenConstants.ACTION_REQUEST_FYI_REQ, null, "Notification Request", user, "Notification Request", true);
               		}
               			//wfDoc.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_FYI_REQ, "Notification Request", new EmplIdVO(field.getChildText("value")), "Notification Request", true);
                		LOG.debug("Sent FYI using the appSpecificRouteDocumentToUser function to UniversityID:  " + user.getEmplId());
                		break;
               	}
        	}
        }
        }
		return super.process(context, helper);
	}


    private static Element getRootElement(DocumentContent docContent) {
        Element rootElement = null;
        try {
            rootElement = XmlHelper.buildJDocument(docContent.getDocument()).getRootElement();
        } catch (Exception e) {
            throw new WorkflowServiceErrorException("Invalid XML submitted", new ArrayList<Object>());
        }
        return rootElement;
    }


	protected Object getService(String serviceName) {
		return KEWServiceLocator.getService(serviceName);
	}


}


