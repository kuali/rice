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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.dto.WorkgroupNameIdDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.test.TestUtilities;

public class AdHocRouteTest extends KEWTestCase {

	private static final String ADHOC_DOC = "AdhocRouteTest";
	private Long docId;

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }

	@Test
	public void testParallelAdHocRouting() throws Exception {
    	WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("rkirkend"), ADHOC_DOC);
    	docId = doc.getRouteHeaderId();
    	doc.appSpecificRouteDocumentToUser(KEWConstants.ACTION_REQUEST_APPROVE_REQ, "AdHoc", "annotation1", new NetworkIdDTO("dewey"), "respDesc1", false);

    	doc = getDocument("dewey");
    	assertFalse("User andlee should not have an approve request yet.  Document not yet routed.", doc.isApprovalRequested());
    	doc.appSpecificRouteDocumentToWorkgroup(KEWConstants.ACTION_REQUEST_APPROVE_REQ, "AdHoc", "annotation2", new WorkgroupNameIdDTO("WorkflowAdmin"), "respDesc2", true);

    	doc = getDocument("quickstart");
    	assertFalse("User should not have approve request yet.  Document not yet routed.", doc.isApprovalRequested());

    	doc = getDocument("rkirkend");
    	doc.routeDocument("");

    	// there should be two adhoc requests
    	ActionRequestDTO[] actionRequests = doc.getActionRequests();
    	for (int index = 0; index < actionRequests.length; index++) {
    		assertTrue("Request should be an adhoc request.", actionRequests[index].isAdHocRequest());
    	}

    	//all users should now have active approvals
    	WorkflowDocument deweyDoc = getDocument("dewey");
    	assertTrue("Dewey should have an approve request", deweyDoc.isApprovalRequested());

		doc = getDocument("ewestfal");// test that more than 1 member got
										// requests
    	assertTrue("WorkflowAdmin should have an approve request", doc.isApprovalRequested());

    	deweyDoc.approve("");
    	doc.approve("");
    	doc = getDocument("user1");//this dude has a rule in ActionsConfig.xml
    	doc.approve("");
    	assertTrue("The document should be final", doc.stateIsFinal());
    }

    /**
	 * Test generation of an initial ad-hoc request to initiator prior to
	 * routing.
     *
     * This test will fail until EN-643 is resolved.
     */
	@Test
	public void testAdHocToInitiator() throws Exception {
        final String ADHOC_NODE = "AdHoc";
        WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("rkirkend"), ADHOC_DOC);
        docId = doc.getRouteHeaderId();
        doc.appSpecificRouteDocumentToUser(KEWConstants.ACTION_REQUEST_APPROVE_REQ, ADHOC_NODE, "annotation1", new NetworkIdDTO("rkirkend"), "", true);

        doc.routeDocument("");
        assertTrue(doc.stateIsEnroute());

        doc = getDocument("rkirkend");
        assertTrue("rkirkend should have an approval request on the document", doc.isApprovalRequested());
        TestUtilities.assertAtNode(doc, ADHOC_NODE);

        // now try it with ignore previous=false
        doc = new WorkflowDocument(new NetworkIdDTO("rkirkend"), ADHOC_DOC);
        docId = doc.getRouteHeaderId();
        doc.appSpecificRouteDocumentToUser(KEWConstants.ACTION_REQUEST_APPROVE_REQ, ADHOC_NODE, "annotation1", new NetworkIdDTO("rkirkend"), "", false);

        doc.routeDocument("");
        assertTrue(doc.stateIsEnroute());

        doc = getDocument("rkirkend");
        assertFalse("rkirkend should NOT have an approval request on the document", doc.isApprovalRequested());
        TestUtilities.assertAtNode(doc, "One");
        doc = getDocument("user1");
        assertTrue("user1 should have an approval request on the document", doc.isApprovalRequested());
    }

	@Test
	public void testSerialAdHocRouting() throws Exception {
    	WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("rkirkend"), ADHOC_DOC);
    	docId = doc.getRouteHeaderId();
    	doc.routeDocument("");
    	doc = getDocument("user1");
    	doc.appSpecificRouteDocumentToUser(KEWConstants.ACTION_REQUEST_APPROVE_REQ, "One", "annotation1", new NetworkIdDTO("user2"), "", false);
    	doc.appSpecificRouteDocumentToUser(KEWConstants.ACTION_REQUEST_APPROVE_REQ, "One", "annotation1", new NetworkIdDTO("rkirkend"), "", true);
    	doc.approve("");
    	doc = getDocument("rkirkend");
    	assertFalse("rkirkend should not have the document at this point 'S' activation", doc.isApprovalRequested());
    	doc = getDocument("user2");
    	assertTrue("user2 should have an approve request", doc.isApprovalRequested());
    	doc.approve("");
    	doc = getDocument("rkirkend");
    	doc.approve("");
    	assertTrue("The document should be final", doc.stateIsFinal());
    }

	@Test
    public void testRepeatedAdHocRouting() throws Exception {
        final String ADHOC_NODE = "AdHoc";
        WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("rkirkend"), ADHOC_DOC);
        docId = doc.getRouteHeaderId();
        doc.appSpecificRouteDocumentToUser(KEWConstants.ACTION_REQUEST_APPROVE_REQ, ADHOC_NODE, "annotation1", new NetworkIdDTO("user2"), "", false);
        doc.routeDocument("");
        doc = getDocument("rkirkend");
        assertFalse("rkirkend should NOT have an approval request on the document", doc.isApprovalRequested());
        // user1 shouldn't have an approve request YET - to prove that we have not yet advanced past this initial AdHoc node
        doc = getDocument("user1");
        assertFalse("user1 should NOT have an approval request on the document", doc.isApprovalRequested());

        doc = getDocument("user2");
        assertTrue("user2 should have an approval request on document", doc.isApprovalRequested());
        doc.appSpecificRouteDocumentToUser(KEWConstants.ACTION_REQUEST_APPROVE_REQ, ADHOC_NODE, "annotation1", new NetworkIdDTO("user3"), "", false);
        doc.approve("");
        doc = getDocument("user2");
        assertFalse("user2 should NOT have an approval request on the document", doc.isApprovalRequested());

        doc = getDocument("user3");
        assertTrue("user3 should have an approval request on document", doc.isApprovalRequested());
        doc.appSpecificRouteDocumentToUser(KEWConstants.ACTION_REQUEST_APPROVE_REQ, ADHOC_NODE, "annotation1", new NetworkIdDTO("rkirkend"), "", true);
        doc.approve("");
        doc = getDocument("user3");
        assertFalse("user3 should NOT have an approval request on the document", doc.isApprovalRequested());

        doc = getDocument("rkirkend");
        assertTrue("rkirkend should have an approval request on document", doc.isApprovalRequested());
        doc.approve("");
        doc = getDocument("rkirkend");
        assertFalse("rkirkend should NOT have an approval request on the document", doc.isApprovalRequested());

        // the last node that is defined on the doc goes to user1
        doc = getDocument("user1");
        assertTrue("user1 should have an approval request on document", doc.isApprovalRequested());
        doc.approve("");
        doc = getDocument("user1");
        assertFalse("user1 should NOT have an approval request on the document", doc.isApprovalRequested());

        // just to be extra sure...let's double check those other ad-hoc-ees
        doc = getDocument("rkirkend");
        assertFalse("rkirkend should NOT have an approval request on the document", doc.isApprovalRequested());
        doc = getDocument("user2");
        assertFalse("user2 should NOT have an approval request on the document", doc.isApprovalRequested());
        doc = getDocument("user3");
        assertFalse("user3 should NOT have an approval request on the document", doc.isApprovalRequested());
        
        assertTrue("The document should be final", doc.stateIsFinal());
    }

    @Test public void testAdHocWhenDocumentIsInitiated() throws Exception {
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), "TakeWorkgroupAuthorityDoc");
        document.saveRoutingData();
        assertTrue(document.stateIsInitiated());

        document.appSpecificRouteDocumentToUser(KEWConstants.ACTION_REQUEST_APPROVE_REQ, "My Annotation", new NetworkIdDTO("rkirkend"), "", true);
        document.appSpecificRouteDocumentToUser(KEWConstants.ACTION_REQUEST_FYI_REQ, "My Annotation", new NetworkIdDTO("user1"), "", true);

        // this is an initiated document, the requests should not be activated yet
        document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), document.getRouteHeaderId());
        assertFalse(document.isApprovalRequested());
        document = new WorkflowDocument(new NetworkIdDTO("user1"), document.getRouteHeaderId());
        assertFalse(document.isFYIRequested());

        // now route the document, the requests should be activated
        document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), document.getRouteHeaderId());
        document.routeDocument("");
        document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        document = new WorkflowDocument(new NetworkIdDTO("user1"), document.getRouteHeaderId());
        assertTrue(document.isFYIRequested());
    }

    @Test public void testAdHocWhenDocumentIsFinal() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), "TakeWorkgroupAuthorityDoc");
        document.routeDocument("");

        try {
        	document.appSpecificRouteDocumentToUser("A", "AdHoc", "", new NetworkIdDTO("ewestfal"), "", true);
        	fail("document should not be allowed to route to nodes that are complete");
		} catch (Exception e) {
		}


        document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), document.getRouteHeaderId());
        document.approve("");

        assertTrue("Document should be final", document.stateIsFinal());

        document = new WorkflowDocument(new NetworkIdDTO("user1"), document.getRouteHeaderId());
        ActionRequestDTO[] requests = document.getActionRequests();
        for (int i = 0; i < requests.length; i++) {
			ActionRequestDTO request = requests[i];
			if (request.isActivated()) {
				fail("Active requests should not be present on a final document");
			}
		}

        // try and adhoc a request to a final document, should blow up
        try {
        	document.appSpecificRouteDocumentToUser("A", "WorkgroupByDocument", "", new NetworkIdDTO("ewestfal"), "", true);
        	fail("Should not be allowed to adhoc approve to a final document.");
        } catch (Exception e) {

        }

        // it should be legal to adhoc an FYI on a FINAL document
        document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), document.getRouteHeaderId());
        assertFalse("rkirkend should not have an FYI request.", document.isFYIRequested());
    	document.appSpecificRouteDocumentToUser(KEWConstants.ACTION_REQUEST_FYI_REQ, "WorkgroupByDocument", "", new NetworkIdDTO("rkirkend"), "", true);
    	document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), document.getRouteHeaderId());
    	assertTrue("rkirkend should have an FYI request", document.isFYIRequested());
    }

    @Test public void testAdHocWhenDocumentIsSaved() throws Exception {
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), "TakeWorkgroupAuthorityDoc");
        document.saveDocument("");

        // TODO test adhocing of approve requests

        assertTrue("Document should be saved.", document.stateIsSaved());
    	document.appSpecificRouteDocumentToUser(KEWConstants.ACTION_REQUEST_FYI_REQ, "AdHoc", "", new NetworkIdDTO("rkirkend"), "", true);
    	document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), document.getRouteHeaderId());
    	assertTrue("rkirkend should have an FYI request", document.isFYIRequested());
    }

    @Test public void testAdHocFieldsSavedCorrectly() throws Exception  {
    	WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("rkirkend"), ADHOC_DOC);
    	docId = doc.getRouteHeaderId();
    	doc.routeDocument("");
    	// find all current request and get a list of ids for elimination purposes later
    	List<Long> oldRequestIds = new ArrayList<Long>();
        for (Iterator iterator = KEWServiceLocator.getActionRequestService().findAllActionRequestsByRouteHeaderId(doc.getRouteHeaderId()).iterator(); iterator.hasNext();) {
            oldRequestIds.add(((ActionRequestValue) iterator.next()).getActionRequestId());
        }

        // send the adhoc route request
        doc = getDocument("user1");
    	doc.appSpecificRouteDocumentToUser(KEWConstants.ACTION_REQUEST_APPROVE_REQ, "One", "annotation1", new NetworkIdDTO("user2"), "respDesc", false);
 
    	// adhoc request should be only new request on document
    	ActionRequestValue request = null;
    	for (Iterator iter = KEWServiceLocator.getActionRequestService().findAllActionRequestsByRouteHeaderId(doc.getRouteHeaderId()).iterator(); iter.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
            if (!oldRequestIds.contains(actionRequest.getActionRequestId())) {
                request = actionRequest;
                break;
            }
        }
    	assertNotNull("Could not find adhoc routed action request", request);

    	// verify request data
        assertEquals("wrong person", request.getWorkflowUser().getAuthenticationUserId().getAuthenticationId(), "user2");
    	assertEquals("annotation incorrect", "annotation1", request.getAnnotation());
    	assertEquals("action requested code incorrect", request.getActionRequested(), KEWConstants.ACTION_REQUEST_APPROVE_REQ);
    	assertEquals("responsibility desc incorrect", request.getResponsibilityDesc(), "respDesc");
    	assertEquals("wrong ignore previous", request.getIgnorePrevAction(), Boolean.FALSE);

    }

	@Test
	public void testAdHocDissaprovedDocument() throws Exception {
    	WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("ewestfal"), ADHOC_DOC);
    	docId = doc.getRouteHeaderId();
    	doc.routeDocument("");

    	doc = getDocument("user1");
    	TestUtilities.assertAtNode(doc, "One");
    	doc.disapprove("");
    	TestUtilities.assertAtNode(doc, "One");
    	//adhoc an ack and fyi

    	doc.appSpecificRouteDocumentToUser("F", "One", "", new NetworkIdDTO("rkirkend"), "", true);
    	doc.appSpecificRouteDocumentToUser("K", "One", "", new NetworkIdDTO("user2"), "", true);

    	doc = getDocument("rkirkend");
    	assertTrue(doc.isFYIRequested());
    	doc.fyi();
    	doc = getDocument("user2");
    	assertTrue(doc.isAcknowledgeRequested());
    	doc.acknowledge("");

    	//make sure we cant ad hoc approves or completes
    	doc = new WorkflowDocument(new NetworkIdDTO("rkirkend"), ADHOC_DOC);
    	docId = doc.getRouteHeaderId();
    	doc.routeDocument("");

    	doc = getDocument("user1");
    	doc.disapprove("");

    	// try to ad hoc an approve request
    	try {
    		doc.appSpecificRouteDocumentToUser("A", "One", "", new NetworkIdDTO("rkirkend"), "", true);
    		fail("should have thrown exception cant adhoc approvals on dissaproved documents");
    	} catch (Exception e) {

    	}

    	// try to ad hoc a complete request
    	try {
    		doc.appSpecificRouteDocumentToUser("C", "One", "", new NetworkIdDTO("rkirkend"), "", true);
    		fail("should have thrown exception cant ad hoc completes on dissaproved documents");
    	} catch (Exception e) {

    	}

        // try to ad hoc an ack request
        try {
            doc.appSpecificRouteDocumentToUser("K", "", new NetworkIdDTO("user1"), "", true);
        } catch (Exception e) {
            e.printStackTrace();
            fail("should have thrown exception cant ad hoc completes on dissaproved documents");
    }

        // try to ad hoc a fyi request
        try {
            doc.appSpecificRouteDocumentToUser("F", "", new NetworkIdDTO("user1"), "", true);
        } catch (Exception e) {
            e.printStackTrace();
            fail("should have thrown exception cant ad hoc completes on dissaproved documents");
        }

    }

    /**
     *
     * @throws Exception
     */
	@Test
	public void testAdHocNoNodeName() throws Exception {
    	WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("user1"), ADHOC_DOC);
    	docId = doc.getRouteHeaderId();

		// do an appspecific route to jitrue and NonSIT (w/ ignore previous =
		// false), should end up at the current node
    	doc.appSpecificRouteDocumentToUser("A", "", new NetworkIdDTO("jitrue"), "", false);
    	doc.appSpecificRouteDocumentToWorkgroup("A", "", new WorkgroupNameIdDTO("NonSIT"), "", false);
    	doc.routeDocument("");

		// user1 should not have a request, his action should have counted for
		// the ad hoc request to the workgroup
    	doc = getDocument("user1");
    	assertFalse(doc.isApprovalRequested());
    	doc = getDocument("jitrue");
    	assertTrue(doc.isApprovalRequested());
		// we should still be at the "AdHoc" node because we sent an ad hoc
		// request to jitrue and the workgroup
    	TestUtilities.assertAtNode(doc, "AdHoc");

    	// now disapprove as jitrue
    	doc = getDocument("jitrue");
    	TestUtilities.assertAtNode(doc, "AdHoc");
    	doc.disapprove("");
    	// we should stay at the AdHoc node following the disapprove
    	TestUtilities.assertAtNode(doc, "AdHoc");
    	assertTrue(doc.stateIsDisapproved());
    	//adhoc an ack and fyi

    	doc.appSpecificRouteDocumentToUser("F", "", new NetworkIdDTO("rkirkend"), "", true);
    	doc.appSpecificRouteDocumentToUser("K", "", new NetworkIdDTO("user2"), "", true);
    	doc.appSpecificRouteDocumentToWorkgroup("K", "", new WorkgroupNameIdDTO("NonSIT"), "", true);

    	// rkirkend should have an FYI ad hoc request
    	doc = getDocument("rkirkend");
    	assertTrue(doc.isFYIRequested());
    	doc.fyi();

		// user3 should have an ACK ad hoc request because user3 is in the
		// NonSIT workgroup
    	doc = getDocument("user3");
    	assertTrue(doc.isAcknowledgeRequested());

		// user2 should have an ACK ad hoc request (because of their individual
		// ack and because they are in the NonSIT workgroup)
    	doc = getDocument("user2");
    	assertTrue(doc.isAcknowledgeRequested());
    	doc.acknowledge("");

		// user2's ACK should have counted for the NonSIT workgroup request,
		// user 3 should no longer have an ACK
    	doc = getDocument("user3");
    	assertFalse(doc.isAcknowledgeRequested());

		// finally user1 should have an acknowledge as a result of the
		// disapprove since user1 was the initiator
    	doc = getDocument("user1");
    	assertTrue(doc.isAcknowledgeRequested());
    	doc.acknowledge("");

    	// there should now be no remaining pending requests on the document
    	TestUtilities.assertNumberOfPendingRequests(doc.getRouteHeaderId(), 0);

		// make sure we cant ad hoc approves or completes on a "terminal"
		// document
    	try {
    		doc.appSpecificRouteDocumentToUser("A", "", new NetworkIdDTO("rkirkend"), "", true);
    		fail("should have thrown exception cant adhoc approvals on dissaproved documents");
		} catch (Exception e) {
		}
    	// try to ad hoc a complete request
    	try {
    		doc.appSpecificRouteDocumentToUser("C", "", new NetworkIdDTO("rkirkend"), "", true);
    		fail("should have thrown exception cant ad hoc completes on dissaproved documents");
		} catch (Exception e) {
		}

    }

    private WorkflowDocument getDocument(String netid) throws WorkflowException {
    	return new WorkflowDocument(new NetworkIdDTO(netid), docId);
    }

}