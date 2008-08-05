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
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.actions.BlanketApproveTest.NotifySetup;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.test.TestUtilities;

/**
 * Tests the super user actions available on the API.
 */
public class SuperUserActionTest extends KEWTestCase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SuperUserActionTest.class);

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }
	
    @Test public void testSuperUserApprove() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        
        document = new WorkflowDocument(new NetworkIdDTO("jhopf"), document.getRouteHeaderId());
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
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("user1"), "SUApproveDocument");
        document.routeDocument("");
        document = new WorkflowDocument(new NetworkIdDTO("user2"), document.getRouteHeaderId());
        try {
        	document.approve("");
        } catch (Exception e) {
        }
        TestUtilities.getExceptionThreader().join();
        document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), document.getRouteHeaderId());
        assertTrue("Document should be in exception routing", document.stateIsException());
        document.superUserApprove("");
        document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), document.getRouteHeaderId());
        assertTrue("Document should be final", document.stateIsFinal());
        
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertTrue("Should be no active requests for SU Approved document", actionRequests.isEmpty());
    }
    
    @Test public void testSuperUserApproveExceptionCasesWithNotifications() throws Exception {
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("user1"), "SUApproveDocumentNotifications");
        document.routeDocument("");
        document = new WorkflowDocument(new NetworkIdDTO("user2"), document.getRouteHeaderId());
        try {
        	document.approve("");
        } catch (Exception e) {
        }
        TestUtilities.getExceptionThreader().join();
        document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), document.getRouteHeaderId());
        assertTrue("Document should be in exception routing", document.stateIsException());
        document.superUserApprove("");
        document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), document.getRouteHeaderId());
        assertTrue("Document should be 'processed'", document.stateIsProcessed());
        
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertFalse("Should be active requests for SU Approved document", actionRequests.isEmpty());
        for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iter.next();
			assertTrue("Should be an ack notification request", request.isAcknowledgeRequest());
		}
    }
    
    @Test public void testSuperUserInitiatorApprove() throws Exception {
		WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
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
		WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), "NotificationTestChild");
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
		WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        
        document = new WorkflowDocument(new NetworkIdDTO("quickstart"), document.getRouteHeaderId());
        try {
        	assertFalse("WorkflowDocument should not indicate quickstart as SuperUser", document.isSuperUser());
        	document.superUserApprove("");
        	fail("invalid user attempted to SuperUserApprove");
        } catch (Exception e) {
        }
        
	}
	
	@Test public void testSuperUserActionDisregardPostProcessing() throws Exception {
	    // verify that the post processor class still throws exceptions when post processing document
        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), generateDummyEnrouteDocument("ewestfal").getRouteHeaderId());
        try {
            document.superUserApprove("");
            fail("Document should throw exception from post processor");
        } catch (Exception e) {
        }
        
        // test that ignoring the post processor works correctly
        document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), generateDummyEnrouteDocument("ewestfal").getRouteHeaderId());
        try {
            KEWServiceLocator.getWorkflowDocumentService().superUserCancelAction(KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdDTO("bmcgough")), KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId()), "", false);
        } catch (Exception e) {
            LOG.error("Exception Found:", e);
            fail("Document should not throw an exception when ignoring post processor during superUserCancelAction");
        }

        document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), generateDummyEnrouteDocument("ewestfal").getRouteHeaderId());
        try {
            KEWServiceLocator.getWorkflowDocumentService().superUserDisapproveAction(KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdDTO("bmcgough")), KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId()), "", false);
        } catch (Exception e) {
            LOG.error("Exception Found:", e);
            fail("Document should not throw an exception when ignoring post processor during superUserDisapproveAction");
        }

        document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), generateDummyEnrouteDocument("ewestfal").getRouteHeaderId());
        try {
            KEWServiceLocator.getWorkflowDocumentService().superUserApprove(KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdDTO("bmcgough")), KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId()), "", false);
        } catch (Exception e) {
            LOG.error("Exception Found:", e);
            fail("Document should not throw an exception when ignoring post processor during superUserApprove");
        }

        document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), generateDummyEnrouteDocument("ewestfal").getRouteHeaderId());
        try {
            KEWServiceLocator.getWorkflowDocumentService().superUserNodeApproveAction(KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdDTO("bmcgough")), document.getRouteHeaderId(), "Acknowledge1", "", false);
        } catch (Exception e) {
            LOG.error("Exception Found:", e);
            fail("Document should not throw an exception when ignoring post processor during superUserNodeApprove");
        }

        document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), generateDummyEnrouteDocument("ewestfal").getRouteHeaderId());
        try {
            KEWServiceLocator.getWorkflowDocumentService().superUserReturnDocumentToPreviousNode(KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdDTO("bmcgough")), document.getRouteHeaderId(), "WorkflowDocumentTemplate", "", false);
        } catch (Exception e) {
            LOG.error("Exception Found:", e);
            fail("Document should not throw an exception when ignoring post processor during superUserReturnDocumentToPreviousNode");
        }

        document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), generateDummyEnrouteDocument("ewestfal").getRouteHeaderId());
        try {
            Long actionRequestId = null;
            // get actionRequestId to use... there should only be one active action request
            ActionRequestDTO[] actionRequests = new WorkflowInfo().getActionRequests(document.getRouteHeaderId());
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
            KEWServiceLocator.getWorkflowDocumentService().superUserActionRequestApproveAction(KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdDTO("bmcgough")), document.getRouteHeaderId(), actionRequestId, "", false);
        } catch (Exception e) {
            LOG.error("Exception Found:", e);
            fail("Document should not throw an exception when ignoring post processor during superUserActionRequestApproveAction");
        }

	}
	
	private WorkflowDocument generateDummyEnrouteDocument(String initiatorNetworkId) throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO(initiatorNetworkId), "SuperUserActionInvalidPostProcessor");
        assertEquals("Document should be at start node","AdHoc", document.getNodeNames()[0]);
        document.routeDocument("");
        assertEquals("Document should be at WorkflowDocument2 node","WorkflowDocument2", document.getNodeNames()[0]);
        assertEquals("Document should be enroute",KEWConstants.ROUTE_HEADER_ENROUTE_CD, document.getRouteHeader().getDocRouteStatus());
        return document;
	}
	
	
}
