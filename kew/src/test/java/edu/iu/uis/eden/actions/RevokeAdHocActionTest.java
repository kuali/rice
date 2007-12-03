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

import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.AdHocRevokeVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupNameIdVO;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.test.TestUtilities;

public class RevokeAdHocActionTest extends KEWTestCase {

	private static final String ADH0C_DOC = "AdhocRouteTest";
	private Long docId;
	
    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }
    
    /**
     * Tests revoking by action request id.
     */
    @Test public void testRevokeByActionRequestId() throws Exception {
    	WorkflowDocument doc = new WorkflowDocument(new NetworkIdVO("rkirkend"), ADH0C_DOC);
    	docId = doc.getRouteHeaderId();
    	doc.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_APPROVE_REQ, "AdHoc", "annotation1", new NetworkIdVO("dewey"), "respDesc1", false);
    	
    	// check the action requests
    	TestUtilities.assertNumberOfPendingRequests(docId, 1);
    	TestUtilities.assertUserHasPendingRequest(docId, "dewey");
    	
    	// now revoke by a bad action request id, we should recieve a WorkflowException
    	try {
    		doc.revokeAdHocRequests(new AdHocRevokeVO(new Long(123456789)), "");
    		fail("Revoking by a bad action request id should have thrown an exception!");
    	} catch (WorkflowException e) {}
    	
    	// revoke by the real action request id
    	ActionRequestVO[] actionRequestVOs = new WorkflowInfo().getActionRequests(docId);
    	assertEquals(1, actionRequestVOs.length);
    	Long actionRequestId = actionRequestVOs[0].getActionRequestId();
    	doc.revokeAdHocRequests(new AdHocRevokeVO(actionRequestId), "");
    	
    	// there should now be no pending requests
    	TestUtilities.assertNumberOfPendingRequests(docId, 0);
    	
    	// route the document, this doc type is configured to route to user1
    	doc.routeDocument("");
    	doc = getDocument("user1");
    	assertTrue(doc.isApprovalRequested());
    	
    	// now attempt to revoke this non-adhoc request by id, it should throw an error
    	actionRequestVOs = new WorkflowInfo().getActionRequests(docId);
    	for (int index = 0; index < actionRequestVOs.length; index++) {
    		if (actionRequestVOs[index].isPending()) {
    			try {
    				doc.revokeAdHocRequests(new AdHocRevokeVO(actionRequestVOs[index].getActionRequestId()), "");
    				fail("Attempted to revoke by an invalid action request id, should have thrown an error!");
    			} catch (WorkflowException e) {}
    		}
    	}
    	
    }
    
    /**
     * Tests revoking by user and workgroup.
     */
    @Test public void testRevokeByUserAndWorkgroup() throws Exception {
    	// ad hoc the document to dewey (twice) and the workgroup WorkflowAdmin
    	WorkflowDocument doc = new WorkflowDocument(new NetworkIdVO("rkirkend"), ADH0C_DOC);
    	docId = doc.getRouteHeaderId();
    	doc.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_APPROVE_REQ, "AdHoc", "annotationDewey1", new NetworkIdVO("dewey"), "respDesc1", false);
    	doc.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_APPROVE_REQ, "AdHoc", "annotationDewey2", new NetworkIdVO("dewey"), "respDesc1", false);
    	doc.appSpecificRouteDocumentToWorkgroup(EdenConstants.ACTION_REQUEST_APPROVE_REQ, "AdHoc", "Annotation WorkflowAdmin", new WorkgroupNameIdVO("WorkflowAdmin"), "respDesc2", true);    	
    
    	TestUtilities.assertNumberOfPendingRequests(docId, 3);
    	TestUtilities.assertUserHasPendingRequest(docId, "dewey");
    	TestUtilities.assertUserHasPendingRequest(docId, "quickstart");
    	
    	// route the document, this should activate the ad hoc requests
    	doc.routeDocument("");
    	assertTrue(doc.stateIsEnroute());
    	TestUtilities.assertAtNode(doc, "AdHoc");
    	TestUtilities.assertNumberOfPendingRequests(docId, 3);
    	
    	// try revoking by a user and workgroup without adhoc requests, it should effectively be a no-op
    	doc.revokeAdHocRequests(new AdHocRevokeVO(new NetworkIdVO("ewestfal")), "This should be a no-op");
    	doc.revokeAdHocRequests(new AdHocRevokeVO(new WorkgroupNameIdVO("TestWorkgroup")), "This should be a no-op");
    	doc = getDocument("rkirkend");
    	TestUtilities.assertNumberOfPendingRequests(docId, 3);
    	TestUtilities.assertUserHasPendingRequest(docId, "dewey");
    	TestUtilities.assertUserHasPendingRequest(docId, "quickstart");
    	
    	// now revoke each request in turn, after the second one is invoked the document should transition to it's next route level
    	// and route to user1
    	doc.revokeAdHocRequests(new AdHocRevokeVO(new NetworkIdVO("dewey")), "revokeUser");
    	TestUtilities.assertNumberOfPendingRequests(docId, 1);
    	doc = getDocument("dewey");
    	assertFalse("dewey should no longer have an approve request.", doc.isApprovalRequested());
    	doc.revokeAdHocRequests(new AdHocRevokeVO(new WorkgroupNameIdVO("WorkflowAdmin")), "revokeWorkgroup");
    	
    	// the doc should now transition to the next node
    	doc = getDocument("user1");
    	TestUtilities.assertAtNode(doc, "One");
    	assertTrue("user1 should have an approve request.", doc.isApprovalRequested());
    	doc.approve("");
    	
    	// doc should now be final
    	assertTrue("doc should be final", doc.stateIsFinal());
    }
    
    /**
     * Tests revoking by node name.
     */
    @Test public void testRevokeByNodeName() throws Exception {
    	// ad hoc requests at the AdHoc node and then revoke the entire node
    	WorkflowDocument doc = new WorkflowDocument(new NetworkIdVO("rkirkend"), ADH0C_DOC);
    	docId = doc.getRouteHeaderId();
    	doc.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_APPROVE_REQ, "AdHoc", "annotationDewey1", new NetworkIdVO("dewey"), "respDesc1", false);
    	doc.appSpecificRouteDocumentToWorkgroup(EdenConstants.ACTION_REQUEST_APPROVE_REQ, "AdHoc", "Annotation WorkflowAdmin", new WorkgroupNameIdVO("WorkflowAdmin"), "respDesc2", true);
    	TestUtilities.assertNumberOfPendingRequests(docId, 2);
    	
    	// now revoke the node
    	doc.revokeAdHocRequests(new AdHocRevokeVO("AdHoc"), "");
    	TestUtilities.assertNumberOfPendingRequests(docId, 0);
    	// send an Acknowledge to the AdHoc node prior to routing
    	doc.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, "AdHoc", "annotationEwestfal1", new NetworkIdVO("ewestfal"), "respDesc1", false);
    	
    	// route the document
    	doc = getDocument("rkirkend");
    	doc.routeDocument("");
    	TestUtilities.assertAtNode(doc, "One");

    	// ewestfal should have an acknowledge request
    	doc = getDocument("ewestfal");
    	assertTrue(doc.isAcknowledgeRequested());
    	
    	// approve the document, it should go PROCESSED
    	doc = getDocument("user1");
    	assertTrue(doc.isApprovalRequested());
    	doc.approve("");
    	assertTrue(doc.stateIsProcessed());
    	
    	// revoke at the "One" node where there are no requests, it should be a no-op (the document should stay processed)
    	doc.revokeAdHocRequests(new AdHocRevokeVO("One"), "");
    	doc = getDocument("ewestfal");
    	assertTrue(doc.stateIsProcessed());
    	
    	// now revoke the ACKNOWLEDGE to ewestfal by revoking at the "AdHoc" node, the document should go FINAL
    	doc.revokeAdHocRequests(new AdHocRevokeVO("AdHoc"), "");
    	doc = getDocument("ewestfal");
    	assertTrue(doc.stateIsFinal());
    }
    
    /**
     * Tests the behavior revocation of ad hoc requests prior to the routing of the document.
     * 
     * @throws Exception
     */
    @Test public void testRevokePriorToRouting() throws Exception {
    	// ad hoc the document to dewey and the workgroup WorkflowAdmin
    	WorkflowDocument doc = new WorkflowDocument(new NetworkIdVO("rkirkend"), ADH0C_DOC);
    	docId = doc.getRouteHeaderId();
    	doc.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_APPROVE_REQ, "AdHoc", "annotation1", new NetworkIdVO("dewey"), "respDesc1", false);
    	
    	doc = getDocument("dewey");
    	assertFalse("User andlee should not have an approve request yet.  Document not yet routed.", doc.isApprovalRequested());
    	doc.appSpecificRouteDocumentToWorkgroup(EdenConstants.ACTION_REQUEST_APPROVE_REQ, "AdHoc", "annotation2", new WorkgroupNameIdVO("WorkflowAdmin"), "respDesc2", true);
    	doc = getDocument("quickstart");
    	assertFalse("User should not have approve request yet.  Document not yet routed.", doc.isApprovalRequested());
    	
    	// the document should be initiated at this point
    	assertTrue("Document should still be intitiated.", doc.stateIsInitiated());
    	
    	// check and revoke the actual ActionRequestVOs
    	WorkflowInfo info = new WorkflowInfo();
    	// reaquire the document as the initiator
    	doc = getDocument("rkirkend");
    	ActionRequestVO[] actionRequestVOs = info.getActionRequests(doc.getRouteHeaderId());
    	assertEquals("There should be 2 ad hoc requests.", 2, actionRequestVOs.length);
    	for (int index = 0; index < actionRequestVOs.length; index++) {
    		ActionRequestVO requestVO = actionRequestVOs[index];
    		assertTrue("Should be an ad hoc request.", requestVO.isAdHocRequest());
    		// revoke by id
    		doc.revokeAdHocRequests(new AdHocRevokeVO(requestVO.getActionRequestId()), "");
    	}
    	
    	// now the document should have no pending action requests on it
    	List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(docId);
    	assertEquals("There should be no pending requests.", 0, actionRequests.size());
    	
    	// check that the "ActionTakens" have been properly recorded
    	Collection actionTakens = KEWServiceLocator.getActionTakenService().findByDocIdAndAction(docId, EdenConstants.ACTION_TAKEN_ADHOC_REVOKED_CD);
    	assertEquals("There should be 2 'AdHocRevoked' action takens", 2, actionTakens.size());
    	
    	// now check that the document is still intiated
    	doc = getDocument("rkirkend");
    	assertTrue("Document should still be intitiated.", doc.stateIsInitiated());
    }

    /**
     * Tests the revocation of ad hoc requests after a blanket approve.  The goal of this test is to verify that revocation of
     * ad hoc requests doesn't have any adverse effects on the notification requests
     */
    @Test public void testRevokeAfterBlanketApprove() throws Exception {
    	WorkflowDocument doc = new WorkflowDocument(new NetworkIdVO("rkirkend"), ADH0C_DOC);
    	docId = doc.getRouteHeaderId();
    	// send an FYI to the AdHoc node prior to blanket approving
    	doc.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_FYI_REQ, "AdHoc", "annotationEwestfal1", new NetworkIdVO("ewestfal"), "respDesc1", false);
    	
    	// blanket approve the document
    	doc.blanketApprove("");
    	assertTrue(doc.stateIsProcessed());
    	
    	// ewestfal should have his ad hoc FYI and user1 should have an ack from the blanket approve
    	doc = getDocument("ewestfal");
    	assertTrue(doc.isFYIRequested());
    	doc = getDocument("user1");
    	assertTrue(doc.isAcknowledgeRequested());
    	
    	// revoke all ad hoc requests
    	doc.revokeAdHocRequests(new AdHocRevokeVO(), "revoking all adhocs");
    	assertTrue(doc.stateIsProcessed());
    	TestUtilities.assertNumberOfPendingRequests(docId, 1);
    	
    	// user1 should still have acknowledge request
    	assertTrue(doc.isAcknowledgeRequested());
    	
    	// ewestfal should no longer have an fyi
    	doc = getDocument("ewestfal");
    	assertFalse(doc.isFYIRequested());
    }
    
    private WorkflowDocument getDocument(String netid) throws WorkflowException {
    	return new WorkflowDocument(new NetworkIdVO(netid), docId);
    }
    
}