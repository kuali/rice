/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kew.actions;

import org.junit.Test;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actions.BlanketApproveTest.NotifySetup;

import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWConstants;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests the super user actions available on the API.
 */
public class SuperUserActionRequestApproveEventTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }

    @Test public void testSuperUserActionsOnEnroute() throws Exception {
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        document.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_FYI_REQ, "", getPrincipalIdForName("rkirkend"), "", true);
        document.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_APPROVE_REQ, "", getPrincipalIdForName("jhopf"), "", true);
        document.routeDocument("");

        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        assertTrue("rkirkend should have an FYI request.", document.isFYIRequested());

        String rkirkendPrincipalId = getPrincipalIdForName("rkirkend");
        List<ActionRequestValue> actionRequests = KEWServiceLocator.getActionRequestService().findAllValidRequests(rkirkendPrincipalId, document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_FYI_REQ);
        assertEquals("There should only be 1 fyi request to rkirkend.", 1, actionRequests.size());
        document = new WorkflowDocument(rkirkendPrincipalId, document.getRouteHeaderId());
        document.superUserActionRequestApprove(actionRequests.get(0).getActionRequestId(), "");

        // FYI should no longer be requested
        document = new WorkflowDocument(rkirkendPrincipalId, document.getRouteHeaderId());
        assertFalse("rkirkend should no longer have an FYI request.", document.isFYIRequested());

        // doc should still be enroute
        assertTrue("Document should still be ENROUTE", document.stateIsEnroute());

    }

    @Test public void testSuperUserActionsOnFinal() throws Exception {
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), "SuperUserApproveActionRequestFyiTest");
        document.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_FYI_REQ, "", getPrincipalIdForName("rkirkend"), "", true);
        document.routeDocument("");

        // doc should still be final
        assertEquals("Document should be FINAL", KEWConstants.ROUTE_HEADER_FINAL_CD, document.getRouteHeader().getDocRouteStatus());

        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        assertTrue("rkirkend should have an FYI request.", document.isFYIRequested());

        String rkirkendPrincipalId = getPrincipalIdForName("rkirkend");
        List<ActionRequestValue> actionRequests = KEWServiceLocator.getActionRequestService().findAllValidRequests(rkirkendPrincipalId, document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_FYI_REQ);
        assertEquals("There should only be 1 fyi request to rkirkend.", 1, actionRequests.size());
        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), document.getRouteHeaderId());
        document.superUserActionRequestApprove(actionRequests.get(0).getActionRequestId(), "");

        // FYI should no longer be requested
        document = new WorkflowDocument(rkirkendPrincipalId, document.getRouteHeaderId());
        assertFalse("rkirkend should no longer have an FYI request.", document.isFYIRequested());
    }
    
    @Test public void testSuperUserActionsOnProcessed() throws Exception {
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), "SuperUserApproveActionRequestFyiTest");
        document.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, "", getPrincipalIdForName("jhopf"), "", true);
        document.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_FYI_REQ, "", getPrincipalIdForName("rkirkend"), "", true);
        document.routeDocument("");

        // doc should still be processed
        assertEquals("Document should be PROCESSED", KEWConstants.ROUTE_HEADER_PROCESSED_CD, document.getRouteHeader().getDocRouteStatus());

        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        assertTrue("rkirkend should have an FYI request.", document.isFYIRequested());

        String rkirkendPrincipalId = getPrincipalIdForName("rkirkend");
        List<ActionRequestValue> fyiActionRequests = KEWServiceLocator.getActionRequestService().findAllValidRequests(rkirkendPrincipalId, document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_FYI_REQ);
        assertEquals("There should only be 1 fyi request to rkirkend.", 1, fyiActionRequests.size());
        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), document.getRouteHeaderId());
        document.superUserActionRequestApprove(fyiActionRequests.get(0).getActionRequestId(), "");

        // FYI should no longer be requested
        document = new WorkflowDocument(rkirkendPrincipalId, document.getRouteHeaderId());
        assertFalse("rkirkend should no longer have an FYI request.", document.isFYIRequested());

        // doc should still be processed
        assertEquals("Document should be PROCESSED", KEWConstants.ROUTE_HEADER_PROCESSED_CD, document.getRouteHeader().getDocRouteStatus());

        String jhopfPrincipalId = getPrincipalIdForName("jhopf");
        List<ActionRequestValue> ackActionRequests = KEWServiceLocator.getActionRequestService().findAllValidRequests(jhopfPrincipalId, document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ);
        assertEquals("There should only be 1 ACK request to jhopf.", 1, ackActionRequests.size());
        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), document.getRouteHeaderId());
        document.superUserActionRequestApprove(ackActionRequests.get(0).getActionRequestId(), "");

        // ACK should no longer be requested
        document = new WorkflowDocument(jhopfPrincipalId, document.getRouteHeaderId());
        assertFalse("jhopf should no longer have an ACK request.", document.isAcknowledgeRequested());

        // doc should be final
        assertEquals("Document should be FINAL", KEWConstants.ROUTE_HEADER_FINAL_CD, document.getRouteHeader().getDocRouteStatus());
    }
    

    @Test public void testSuperUserActionRoutesDocumentToEnroute() throws Exception {
	Long routeHeaderId = testSuperUserActionRoutesDocument("SuperUserApproveActionRequestApproveTest");
	WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), routeHeaderId);
        // doc should now be enroute
        assertEquals("Document should be ENROUTE", KEWConstants.ROUTE_HEADER_ENROUTE_CD, document.getRouteHeader().getDocRouteStatus());
    }

    @Test public void testSuperUserActionRoutesDocumentToFinal() throws Exception {
	Long routeHeaderId = testSuperUserActionRoutesDocument("SuperUserApproveActionRequestFyiTest");
	WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), routeHeaderId);
        // doc should now be enroute
        assertEquals("Document should be FINAL", KEWConstants.ROUTE_HEADER_FINAL_CD, document.getRouteHeader().getDocRouteStatus());
    }

    private Long testSuperUserActionRoutesDocument(String documentType) throws Exception {
    	String ewestfalPrincipalId = getPrincipalIdForName("ewestfal");
        WorkflowDocument document = new WorkflowDocument(ewestfalPrincipalId, documentType);
        document.saveDocument("");
        // doc should saved
        assertEquals("Document should be SAVED", KEWConstants.ROUTE_HEADER_SAVED_CD, document.getRouteHeader().getDocRouteStatus());

        document = new WorkflowDocument(ewestfalPrincipalId, document.getRouteHeaderId());
        assertTrue("ewestfal should have Complete request", document.isCompletionRequested());

        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        assertFalse("rkirkend should not have Complete request", document.isCompletionRequested());
        assertFalse("rkirkend should not have Approve request", document.isApprovalRequested());
        assertTrue("rkirkend should be a super user of the document", document.isSuperUser());
        
        List<ActionRequestValue> actionRequests = KEWServiceLocator.getActionRequestService().findAllValidRequests(ewestfalPrincipalId, document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_COMPLETE_REQ);
        assertEquals("There should only be 1 complete request to ewestfal as result of the save.", 1, actionRequests.size());
        document.superUserActionRequestApprove(actionRequests.get(0).getActionRequestId(), "");

        // Complete should no longer be requested
        document = new WorkflowDocument(ewestfalPrincipalId, document.getRouteHeaderId());
        assertFalse("ewestfal should not have Complete request", document.isCompletionRequested());

        return document.getRouteHeaderId();
    }

    @Test public void testSavedDocumentSuperUserAdhocActionsApprove() throws Exception {
	String initiatorNetworkId = "ewestfal";
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName(initiatorNetworkId), "SuperUserApproveActionRequestFyiTest");
        String adhocActionRequestCode = KEWConstants.ACTION_REQUEST_APPROVE_REQ;
        String adhocActionUserNetworkId = "jhopf";
        document.adHocRouteDocumentToPrincipal(adhocActionRequestCode, "", getPrincipalIdForName(adhocActionUserNetworkId), "", true);
        document.saveDocument("");
        // doc should be saved
        assertEquals("Document should be SAVED", KEWConstants.ROUTE_HEADER_SAVED_CD, document.getRouteHeader().getDocRouteStatus());

        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), document.getRouteHeaderId());
        assertTrue("ewestfal should have Complete request", document.isCompletionRequested());

        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        assertFalse("rkirkend should not have Complete request", document.isCompletionRequested());
        assertFalse("rkirkend should not have Approve request", document.isApprovalRequested());
        assertTrue("rkirkend should be a super user of the document", document.isSuperUser());
        String adhocPrincipalId = getPrincipalIdForName(adhocActionUserNetworkId);
        List<ActionRequestValue> actionRequests = KEWServiceLocator.getActionRequestService().findAllValidRequests(adhocPrincipalId, document.getRouteHeaderId(), adhocActionRequestCode);
        assertEquals("There should only be 1 approve request to " + adhocActionUserNetworkId + ".", 1, actionRequests.size());
        document.superUserActionRequestApprove(actionRequests.get(0).getActionRequestId(), "");

        // approve should no longer be requested
        document = new WorkflowDocument(adhocPrincipalId, document.getRouteHeaderId());
        assertFalse(adhocPrincipalId + " should not have approve request", document.isApprovalRequested());

        // complete should no longer be requested
        document = new WorkflowDocument(getPrincipalIdForName(initiatorNetworkId), document.getRouteHeaderId());
        assertTrue(initiatorNetworkId + " should not have complete request", document.isCompletionRequested());

        // doc should still be saved
        assertEquals("Document should be SAVED", KEWConstants.ROUTE_HEADER_SAVED_CD, document.getRouteHeader().getDocRouteStatus());
    }

}
