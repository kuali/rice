/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.actions;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.edl.WorkflowFunctions;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.web.session.UserSession;

public class RouteLogAuthenticationTest extends KEWTestCase {

	public static final String DOCUMENT_TYPE_NAME = "BlanketApproveSequentialTest";
    
    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }
        
	/**
     * Tests WorkflowFunctions.isUserRouteLogAuthenticated
     */
    @Test public void testUserRouteLogAuthenticated() throws Exception {
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), DOCUMENT_TYPE_NAME);
    	document.routeDocument("");

        // false because we didn't set up the user session properly
        assertFalse(WorkflowFunctions.isUserRouteLogAuthenticated(document.getRouteHeaderId() + ""));
        
        // these two should be in the route log
        UserSession.setAuthenticatedUser(new UserSession(KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("user1"))));
        assertTrue(WorkflowFunctions.isUserRouteLogAuthenticated(document.getRouteHeaderId() + ""));
        UserSession.setAuthenticatedUser(new UserSession(KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("bmcgough"))));
        assertTrue(WorkflowFunctions.isUserRouteLogAuthenticated(document.getRouteHeaderId() + ""));
        
        // user2 should NOT be in the route log
        UserSession.setAuthenticatedUser(new UserSession(KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("user2"))));
        assertFalse(WorkflowFunctions.isUserRouteLogAuthenticated(document.getRouteHeaderId() + ""));
    }
	
}
