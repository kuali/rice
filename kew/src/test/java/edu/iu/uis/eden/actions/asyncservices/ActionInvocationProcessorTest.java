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
package edu.iu.uis.eden.actions.asyncservices;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.routetemplate.TestRuleAttribute;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.WorkflowUser;


/**
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ActionInvocationProcessorTest extends KEWTestCase {

    
    @Test public void testActionInvocationProcessorWorksWithNoActionItem() throws Exception {
	
	
	
	TestRuleAttribute.setRecipients("TestRole", "QualRole", getRecipients());
	
	NetworkIdVO netId = new NetworkIdVO("rkirkend");
	WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(netId);
	WorkflowDocument doc = new WorkflowDocument(netId, "TestDocumentType");
	doc.routeDocument("");
	
	List<ActionRequestValue> requests = KEWServiceLocator.getActionRequestService().findAllActionRequestsByRouteHeaderId(doc.getRouteHeaderId());
	assertFalse(requests.isEmpty());
	
	ActionRequestValue request = null;
	for (ActionRequestValue tempRequest : requests) {
	    if (tempRequest.getWorkflowUser() != null && tempRequest.getWorkflowUser().getAuthenticationUserId().getAuthenticationId().equals("user1")) {
		request = tempRequest;
		break;
	    }
	}
	
	assertNotNull(request);
	
	user = KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdVO("user1"));
	new ActionInvocationProcessor().invokeAction(user, request.getRouteHeaderId(), new ActionInvocation(request.getRouteHeaderId(), request.getActionRequested()));
	//do it again and make sure we don't have a blow up
	new ActionInvocationProcessor().invokeAction(user, request.getRouteHeaderId(), new ActionInvocation(request.getRouteHeaderId(), request.getActionRequested()));
	
	//verify that user1 doesn't have any AR's
	requests = KEWServiceLocator.getActionRequestService().findAllActionRequestsByRouteHeaderId(doc.getRouteHeaderId());
	assertFalse(requests.isEmpty());
	
	request = null;
	for (ActionRequestValue tempRequest : requests) {
	    if (tempRequest.getWorkflowUser() != null && tempRequest.getWorkflowUser().getAuthenticationUserId().getAuthenticationId().equals("user1") && tempRequest.isActive()) {
		request = tempRequest;
		break;
	    }
	}
	
	assertNull(request);
	
    }
    
    
    public static List getRecipients()	{
	List recipients = new ArrayList();
	recipients.add(new AuthenticationUserId("user1"));
	recipients.add(new AuthenticationUserId("user2"));
	return recipients;
    }
    
    
}
