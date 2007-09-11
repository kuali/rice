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

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.actions.BlanketApproveTest.NotifySetup;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.test.TestUtilities;

/**
 * Tests the super user actions available on the API.
 */
public class SuperUserActionTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }
	
    @Test public void testSuperUserApprove() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        
        document = new WorkflowDocument(new NetworkIdVO("jhopf"), document.getRouteHeaderId());
        assertTrue("WorkflowDocument should indicate jhopf as SuperUser", document.isSuperUser());
        document.superUserApprove("");
        assertTrue("Document should be 'processed' after Super User Approve", document.stateIsProcessed());
        List requests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertTrue("Should be active requests still", requests.size() == 2);//number of acks and fyi's configured through rules
        for (Iterator iter = requests.iterator(); iter.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iter.next();
			if (request.isApproveOrCompleteRequest()) {
				fail("There should be no approve or complete requests after su approve");
			}
		}
	}
	
    @Test public void testSuperUserApproveExceptionCases() throws Exception {
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), "SUApproveDocument");
        document.routeDocument("");
        document = new WorkflowDocument(new NetworkIdVO("user2"), document.getRouteHeaderId());
        try {
        	document.approve("");
        } catch (Exception e) {
        }
        TestUtilities.getExceptionThreader().join();
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertTrue("Document should be in exception routing", document.stateIsException());
        document.superUserApprove("");
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertTrue("Document should be final", document.stateIsFinal());
        
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertTrue("Should be no active requests for SU Approved document", actionRequests.isEmpty());
    }
    
    @Test public void testSuperUserApproveExceptionCasesWithNotifications() throws Exception {
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), "SUApproveDocumentNotifications");
        document.routeDocument("");
        document = new WorkflowDocument(new NetworkIdVO("user2"), document.getRouteHeaderId());
        try {
        	document.approve("");
        } catch (Exception e) {
        }
        TestUtilities.getExceptionThreader().join();
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertTrue("Document should be in exception routing", document.stateIsException());
        document.superUserApprove("");
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertTrue("Document should be 'processed'", document.stateIsProcessed());
        
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertFalse("Should be active requests for SU Approved document", actionRequests.isEmpty());
        for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iter.next();
			assertTrue("Should be an ack notification request", request.isAcknowledgeRequest());
		}
    }
    
    @Test public void testSuperUserInitiatorApprove() throws Exception {
		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        assertTrue("WorkflowDocument should indicate ewestfal as SuperUser", document.isSuperUser());
        document.superUserApprove("");
        assertTrue("Document should be 'processed' after Super User Approve", document.stateIsProcessed());
        List requests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertTrue("Should be active requests still", requests.size() == 2);//number of acks and fyi's configured through rules
        for (Iterator iter = requests.iterator(); iter.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iter.next();
			if (request.isApproveOrCompleteRequest()) {
				fail("There should be no approve or complete requests after su approve");
			}
		}
	}
	
	@Test public void testSuperUserApproveWithNotifications() throws Exception {
		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "NotificationTestChild");
        assertTrue("WorkflowDocument should indicate ewestfal as SuperUser", document.isSuperUser());
        document.superUserApprove("");
        assertTrue("Document should be 'processed' after Super User Approve", document.stateIsProcessed());
        List requests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertTrue("Should be active requests still", requests.size() > 2);//number of acks and fyi's configured through rules - we need these for approvals too
        for (Iterator iter = requests.iterator(); iter.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iter.next();
			if (request.isApproveOrCompleteRequest()) {
				fail("There should be no approve or complete requests after su approve");
			}
		} 
	}
	
	@Test public void testSuperUserApproveInvalidUser() throws Exception {
		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        
        document = new WorkflowDocument(new NetworkIdVO("quickstart"), document.getRouteHeaderId());
        try {
        	assertFalse("WorkflowDocument should not indicate quickstart as SuperUser", document.isSuperUser());
        	document.superUserApprove("");
        	fail("invalid user attempted to SuperUserApprove");
        } catch (Exception e) {
        }
        
	}
	
}
