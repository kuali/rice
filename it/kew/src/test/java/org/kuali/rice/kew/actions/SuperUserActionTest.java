/*
 * Copyright 2006-2011 The Kuali Foundation
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
package org.kuali.rice.kew.actions;

import org.junit.Test;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actions.BlanketApproveTest.NotifySetup;
import org.kuali.rice.kew.dto.ActionRequestDTO;

import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.test.BaselineTestCase;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests the super user actions available on the API.
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class SuperUserActionTest extends KEWTestCase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SuperUserActionTest.class);

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }
	
    @Test public void testSuperUserApprove() throws Exception {
        WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("jhopf"), document.getDocumentId());
        assertTrue("WorkflowDocument should indicate jhopf as SuperUser", document.isSuperUser());
        document.superUserApprove("");
        assertTrue("Document should be 'processed' after Super User Approve", document.stateIsProcessed());
        List requests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getDocumentId());
        assertTrue("Should be active requests still", requests.size() == 2);//number of acks and fyi's configured through rules
        for (Iterator iter = requests.iterator(); iter.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iter.next();
			if (request.isApproveOrCompleteRequest()) {
				fail("There should be no approve or complete requests after su approve");
			}
		}
	}
	
    @Test public void testSuperUserApproveExceptionCases() throws Exception {
    	WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("user1"), "SUApproveDocument");
        document.routeDocument("");
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("user2"), document.getDocumentId());
        try {
        	document.approve("");
        } catch (Exception e) {
        }
        TestUtilities.getExceptionThreader().join();
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), document.getDocumentId());
        assertTrue("Document should be in exception routing", document.stateIsException());
        document.superUserApprove("");
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), document.getDocumentId());
        assertTrue("Document should be final", document.stateIsFinal());
        
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getDocumentId());
        assertTrue("Should be no active requests for SU Approved document", actionRequests.isEmpty());
    }
    
    @Test public void testSuperUserApproveExceptionCasesWithNotifications() throws Exception {
    	WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("user1"), "SUApproveDocumentNotifications");
        document.routeDocument("");
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("user2"), document.getDocumentId());
        try {
        	document.approve("");
        } catch (Exception e) {
        }
        TestUtilities.getExceptionThreader().join();
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), document.getDocumentId());
        assertTrue("Document should be in exception routing", document.stateIsException());
        document.superUserApprove("");
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), document.getDocumentId());
        assertTrue("Document should be 'processed'", document.stateIsProcessed());
        
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getDocumentId());
        assertFalse("Should be active requests for SU Approved document", actionRequests.isEmpty());
        for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iter.next();
			assertTrue("Should be an ack notification request", request.isAcknowledgeRequest());
		}
    }
    
    @Test public void testSuperUserInitiatorApprove() throws Exception {
		WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        assertTrue("WorkflowDocument should indicate ewestfal as SuperUser", document.isSuperUser());
        document.superUserApprove("");
        assertTrue("Document should be 'processed' after Super User Approve", document.stateIsProcessed());
        List requests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getDocumentId());
        assertTrue("Should be active requests still", requests.size() == 2);//number of acks and fyi's configured through rules
        for (Iterator iter = requests.iterator(); iter.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iter.next();
			if (request.isApproveOrCompleteRequest()) {
				fail("There should be no approve or complete requests after su approve");
			}
		}
	}
	
	@Test public void testSuperUserApproveWithNotifications() throws Exception {
		WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), "NotificationTestChild");
        assertTrue("WorkflowDocument should indicate ewestfal as SuperUser", document.isSuperUser());
        document.superUserApprove("");
        assertTrue("Document should be 'processed' after Super User Approve", document.stateIsProcessed());
        List requests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getDocumentId());
        assertTrue("Should be active requests still", requests.size() > 2);//number of acks and fyi's configured through rules - we need these for approvals too
        for (Iterator iter = requests.iterator(); iter.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iter.next();
			if (request.isApproveOrCompleteRequest()) {
				fail("There should be no approve or complete requests after su approve");
			}
		} 
	}
	
	@Test public void testSuperUserApproveInvalidUser() throws Exception {
		WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("quickstart"), document.getDocumentId());
        try {
        	assertFalse("WorkflowDocument should not indicate quickstart as SuperUser", document.isSuperUser());
        	document.superUserApprove("");
        	fail("invalid user attempted to SuperUserApprove");
        } catch (Exception e) {
        }
        
	}
	
	@Test public void testSuperUserActionDisregardPostProcessing() throws Exception {
		
		String bmcgoughPrincipalId = getPrincipalIdForName("bmcgough");
		
	    // verify that the post processor class still throws exceptions when post processing document
        WorkflowDocument document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), generateDummyEnrouteDocument("ewestfal").getDocumentId());
        try {
            document.superUserApprove("");
            fail("Document should throw exception from post processor");
        } catch (Exception e) {
        }
        
        // test that ignoring the post processor works correctly
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), generateDummyEnrouteDocument("ewestfal").getDocumentId());
        try {
            KEWServiceLocator.getWorkflowDocumentService().superUserCancelAction(bmcgoughPrincipalId, KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getDocumentId()), "", false);
        } catch (Exception e) {
            LOG.error("Exception Found:", e);
            fail("Document should not throw an exception when ignoring post processor during superUserCancelAction");
        }

        document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), generateDummyEnrouteDocument("ewestfal").getDocumentId());
        try {
            KEWServiceLocator.getWorkflowDocumentService().superUserDisapproveAction(bmcgoughPrincipalId, KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getDocumentId()), "", false);
        } catch (Exception e) {
            LOG.error("Exception Found:", e);
            fail("Document should not throw an exception when ignoring post processor during superUserDisapproveAction");
        }

        document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), generateDummyEnrouteDocument("ewestfal").getDocumentId());
        try {
            KEWServiceLocator.getWorkflowDocumentService().superUserApprove(bmcgoughPrincipalId, KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getDocumentId()), "", false);
        } catch (Exception e) {
            LOG.error("Exception Found:", e);
            fail("Document should not throw an exception when ignoring post processor during superUserApprove");
        }

        document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), generateDummyEnrouteDocument("ewestfal").getDocumentId());
        try {
            KEWServiceLocator.getWorkflowDocumentService().superUserNodeApproveAction(bmcgoughPrincipalId, document.getDocumentId(), "Acknowledge1", "", false);
        } catch (Exception e) {
            LOG.error("Exception Found:", e);
            fail("Document should not throw an exception when ignoring post processor during superUserNodeApprove");
        }

        document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), generateDummyEnrouteDocument("ewestfal").getDocumentId());
        try {
            KEWServiceLocator.getWorkflowDocumentService().superUserReturnDocumentToPreviousNode(bmcgoughPrincipalId, document.getDocumentId(), "WorkflowDocumentTemplate", "", false);
        } catch (Exception e) {
            LOG.error("Exception Found:", e);
            fail("Document should not throw an exception when ignoring post processor during superUserReturnDocumentToPreviousNode");
        }

        document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), generateDummyEnrouteDocument("ewestfal").getDocumentId());
        try {
            Long actionRequestId = null;
            // get actionRequestId to use... there should only be one active action request
            ActionRequestDTO[] actionRequests = new WorkflowInfo().getActionRequests(document.getDocumentId());
            for (int i = 0; i < actionRequests.length; i++) {
                ActionRequestDTO actionRequestVO = actionRequests[i];
                if (actionRequestVO.isActivated()) {
                    // if we already found an active action request fail the test
                    if (actionRequestId != null) {
                        fail("Found two active action requests for document.  Ids: " + actionRequestId + "  &  " + actionRequestVO.getActionRequestId());
                    }
                    actionRequestId = actionRequestVO.getActionRequestId();
                }
            }
            
            KEWServiceLocator.getWorkflowDocumentService().superUserActionRequestApproveAction(bmcgoughPrincipalId, document.getDocumentId(), actionRequestId, "", false);
        } catch (Exception e) {
            LOG.error("Exception Found:", e);
            fail("Document should not throw an exception when ignoring post processor during superUserActionRequestApproveAction");
        }

	}
	
	private WorkflowDocument generateDummyEnrouteDocument(String initiatorNetworkId) throws Exception {
        WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName(initiatorNetworkId), "SuperUserActionInvalidPostProcessor");
        assertEquals("Document should be at start node","AdHoc", document.getNodeNames()[0]);
        document.routeDocument("");
        assertEquals("Document should be at WorkflowDocument2 node","WorkflowDocument2", document.getNodeNames()[0]);
        assertEquals("Document should be enroute",KEWConstants.ROUTE_HEADER_ENROUTE_CD, document.getRouteHeader().getDocRouteStatus());
        return document;
	}
	
	
}
