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
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.dto.ActionRequestDTO;

import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.services.KIMServiceLocator;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.test.BaselineTestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class AdHocRouteTest extends KEWTestCase {

	private static final String ADHOC_DOC = "AdhocRouteTest";
	private static final String ADHOC_NO_NODE_DOC = "AdHocNoNodeTest";
	private Long docId;

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }

    @Test
    public void testRequestLabel() throws Exception{
    	String note = "test note";
    	Person per = KIMServiceLocator.getPersonService().getPersonByPrincipalName("rkirkend");
    	WorkflowDocument doc = new WorkflowDocument(per.getPrincipalId(), ADHOC_DOC);

    	docId = doc.getRouteHeaderId();

    	doc.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_FYI_REQ, "AdHoc", "annotation1", getPrincipalIdForName("dewey"), "respDesc1", false, note);

    	doc = getDocument(per.getPrincipalId(), docId);
    	List<ActionRequestValue> actionRequests = KEWServiceLocator.getActionRequestService().findAllActionRequestsByRouteHeaderId(docId);
    	for(ActionRequestValue arv : actionRequests){
    		assertTrue("The note we passed in should equal the one we get out. note=["+ note +"]", note.equals(arv.getRequestLabel()));
    	}
    }

	@Test
	public void testParallelAdHocRouting() throws Exception {
    	WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName("rkirkend"), ADHOC_DOC);
    	docId = doc.getRouteHeaderId();
    	doc.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_APPROVE_REQ, "AdHoc", "annotation1", getPrincipalIdForName("dewey"), "respDesc1", false);

    	doc = getDocument("dewey");
    	assertFalse("User andlee should not have an approve request yet.  Document not yet routed.", doc.isApprovalRequested());

    	doc.adHocRouteDocumentToGroup(KEWConstants.ACTION_REQUEST_APPROVE_REQ, "AdHoc", "annotation2", getGroupIdForName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "WorkflowAdmin"), "respDesc2", true);

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
        WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName("rkirkend"), ADHOC_DOC);
        docId = doc.getRouteHeaderId();
        doc.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_APPROVE_REQ, ADHOC_NODE, "annotation1", getPrincipalIdForName("rkirkend"), "", true);

        doc.routeDocument("");
        assertTrue(doc.stateIsEnroute());

        doc = getDocument("rkirkend");
        assertTrue("rkirkend should have an approval request on the document", doc.isApprovalRequested());
        TestUtilities.assertAtNode(doc, ADHOC_NODE);

        // now try it with force action=false
        doc = new WorkflowDocument(getPrincipalIdForName("rkirkend"), ADHOC_DOC);
        docId = doc.getRouteHeaderId();
        doc.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_APPROVE_REQ, ADHOC_NODE, "annotation1", getPrincipalIdForName("rkirkend"), "", false);

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
    	WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName("rkirkend"), ADHOC_DOC);
    	docId = doc.getRouteHeaderId();
    	doc.routeDocument("");
    	doc = getDocument("user1");
    	doc.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_APPROVE_REQ, "One", "annotation1", getPrincipalIdForName("user2"), "", false);
    	doc.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_APPROVE_REQ, "One", "annotation1", getPrincipalIdForName("rkirkend"), "", true);
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
        WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName("rkirkend"), ADHOC_DOC);
        docId = doc.getRouteHeaderId();
        doc.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_APPROVE_REQ, ADHOC_NODE, "annotation1", getPrincipalIdForName("user2"), "", false);
        doc.routeDocument("");
        doc = getDocument("rkirkend");
        assertFalse("rkirkend should NOT have an approval request on the document", doc.isApprovalRequested());
        // user1 shouldn't have an approve request YET - to prove that we have not yet advanced past this initial AdHoc node
        doc = getDocument("user1");
        assertFalse("user1 should NOT have an approval request on the document", doc.isApprovalRequested());

        doc = getDocument("user2");
        assertTrue("user2 should have an approval request on document", doc.isApprovalRequested());
        doc.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_APPROVE_REQ, ADHOC_NODE, "annotation1", getPrincipalIdForName("user3"), "", false);
        doc.approve("");
        doc = getDocument("user2");
        assertFalse("user2 should NOT have an approval request on the document", doc.isApprovalRequested());

        doc = getDocument("user3");
        assertTrue("user3 should have an approval request on document", doc.isApprovalRequested());
        doc.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_APPROVE_REQ, ADHOC_NODE, "annotation1", getPrincipalIdForName("rkirkend"), "", true);
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
    	WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), "TakeWorkgroupAuthorityDoc");
        document.saveRoutingData();
        assertTrue(document.stateIsInitiated());

        document.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_APPROVE_REQ, "My Annotation", getPrincipalIdForName("rkirkend"), "", true);
        document.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_FYI_REQ, "My Annotation", getPrincipalIdForName("user1"), "", true);

        // this is an initiated document, the requests should not be activated yet
        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        assertFalse(document.isApprovalRequested());
        document = new WorkflowDocument(getPrincipalIdForName("user1"), document.getRouteHeaderId());
        assertFalse(document.isFYIRequested());

        // now route the document, the requests should be activated
        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), document.getRouteHeaderId());
        document.routeDocument("");
        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        document = new WorkflowDocument(getPrincipalIdForName("user1"), document.getRouteHeaderId());
        assertTrue(document.isFYIRequested());
    }

    @Test public void testAdHocWhenDocumentIsFinal() throws Exception {
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), "TakeWorkgroupAuthorityDoc");
        document.routeDocument("");
        TestUtilities.assertAtNode(document, "WorkgroupByDocument");

        try {
        	document.adHocRouteDocumentToPrincipal("A", "AdHoc", "", getPrincipalIdForName("ewestfal"), "", true);
        	fail("document should not be allowed to route to nodes that are complete");
		} catch (Exception e) {
		}


        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        assertTrue("rkirkend should have request", document.isApprovalRequested());
        document.approve("");

        assertTrue("Document should be final", document.stateIsFinal());

        document = new WorkflowDocument(getPrincipalIdForName("user1"), document.getRouteHeaderId());
        ActionRequestDTO[] requests = document.getActionRequests();
        for (int i = 0; i < requests.length; i++) {
			ActionRequestDTO request = requests[i];
			if (request.isActivated()) {
				fail("Active requests should not be present on a final document");
			}
		}

        // try and adhoc a request to a final document, should blow up
        try {
        	document.adHocRouteDocumentToPrincipal("A", "WorkgroupByDocument", "", getPrincipalIdForName("ewestfal"), "", true);
        	fail("Should not be allowed to adhoc approve to a final document.");
        } catch (Exception e) {

        }

        // it should be legal to adhoc an FYI on a FINAL document
        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        assertFalse("rkirkend should not have an FYI request.", document.isFYIRequested());
    	document.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_FYI_REQ, "WorkgroupByDocument", "", getPrincipalIdForName("rkirkend"), "", true);
    	document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
    	assertTrue("rkirkend should have an FYI request", document.isFYIRequested());
    }

    @Test public void testAdHocWhenDocumentIsSaved() throws Exception {
    	WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), "TakeWorkgroupAuthorityDoc");
        document.saveDocument("");

        // TODO test adhocing of approve requests

        assertTrue("Document should be saved.", document.stateIsSaved());
    	document.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_FYI_REQ, "AdHoc", "", getPrincipalIdForName("rkirkend"), "", true);
    	document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
    	assertTrue("rkirkend should have an FYI request", document.isFYIRequested());
    }

    @Test public void testAdHocFieldsSavedCorrectly() throws Exception  {
    	WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName("rkirkend"), ADHOC_DOC);
    	docId = doc.getRouteHeaderId();
    	doc.routeDocument("");
    	// find all current request and get a list of ids for elimination purposes later
    	List<Long> oldRequestIds = new ArrayList<Long>();
        for (Iterator iterator = KEWServiceLocator.getActionRequestService().findAllActionRequestsByRouteHeaderId(doc.getRouteHeaderId()).iterator(); iterator.hasNext();) {
            oldRequestIds.add(((ActionRequestValue) iterator.next()).getActionRequestId());
        }

        // send the adhoc route request
        doc = getDocument("user1");
    	doc.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_APPROVE_REQ, "One", "annotation1", getPrincipalIdForName("user2"), "respDesc", false);

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
        assertEquals("wrong person", request.getPrincipalId(), getPrincipalIdForName("user2"));
    	assertEquals("annotation incorrect", "annotation1", request.getAnnotation());
    	assertEquals("action requested code incorrect", request.getActionRequested(), KEWConstants.ACTION_REQUEST_APPROVE_REQ);
    	assertEquals("responsibility desc incorrect", request.getResponsibilityDesc(), "respDesc");
    	assertEquals("wrong force action", request.getForceAction(), Boolean.FALSE);

    }

	@Test
	public void testAdHocDissaprovedDocument() throws Exception {
    	WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName("ewestfal"), ADHOC_DOC);
    	docId = doc.getRouteHeaderId();
    	doc.routeDocument("");

    	doc = getDocument("user1");
    	TestUtilities.assertAtNode(doc, "One");
    	doc.disapprove("");
    	TestUtilities.assertAtNode(doc, "One");
    	//adhoc an ack and fyi

    	doc.adHocRouteDocumentToPrincipal("F", "One", "", getPrincipalIdForName("rkirkend"), "", true);
    	doc.adHocRouteDocumentToPrincipal("K", "One", "", getPrincipalIdForName("user2"), "", true);

    	doc = getDocument("rkirkend");
    	assertTrue(doc.isFYIRequested());
    	doc.fyi();
    	doc = getDocument("user2");
    	assertTrue(doc.isAcknowledgeRequested());
    	doc.acknowledge("");

    	//make sure we cant ad hoc approves or completes
    	doc = new WorkflowDocument(getPrincipalIdForName("rkirkend"), ADHOC_DOC);
    	docId = doc.getRouteHeaderId();
    	doc.routeDocument("");

    	doc = getDocument("user1");
    	doc.disapprove("");

    	// try to ad hoc an approve request
    	try {
    		doc.adHocRouteDocumentToPrincipal("A", "One", "", getPrincipalIdForName("rkirkend"), "", true);
    		fail("should have thrown exception cant adhoc approvals on dissaproved documents");
    	} catch (Exception e) {

    	}

    	// try to ad hoc a complete request
    	try {
    		doc.adHocRouteDocumentToPrincipal("C", "One", "", getPrincipalIdForName("rkirkend"), "", true);
    		fail("should have thrown exception cant ad hoc completes on dissaproved documents");
    	} catch (Exception e) {

    	}

        // try to ad hoc an ack request
        try {
            doc.adHocRouteDocumentToPrincipal("K", "", getPrincipalIdForName("user1"), "", true);
        } catch (Exception e) {
            e.printStackTrace();
            fail("should have thrown exception cant ad hoc completes on dissaproved documents");
    }

        // try to ad hoc a fyi request
        try {
            doc.adHocRouteDocumentToPrincipal("F", "", getPrincipalIdForName("user1"), "", true);
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
    	WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName("user1"), ADHOC_DOC);
    	docId = doc.getRouteHeaderId();

		// do an appspecific route to jitrue and NonSIT (w/ force action =
		// false), should end up at the current node

    	doc.adHocRouteDocumentToPrincipal("A", "", getPrincipalIdForName("jitrue"), "", false);
    	doc.adHocRouteDocumentToGroup("A", "", getGroupIdForName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "NonSIT"), "", false);
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

    	doc.adHocRouteDocumentToPrincipal("F", "", getPrincipalIdForName("rkirkend"), "", true);
    	doc.adHocRouteDocumentToPrincipal("K", "", getPrincipalIdForName("user2"), "", true);

    	doc.adHocRouteDocumentToGroup("K", "", getGroupIdForName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "NonSIT"), "", true);

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
    		doc.adHocRouteDocumentToPrincipal("A", "", getPrincipalIdForName("rkirkend"), "", true);
    		fail("should have thrown exception cant adhoc approvals on dissaproved documents");
		} catch (Exception e) {
		}
    	// try to ad hoc a complete request
    	try {
    		doc.adHocRouteDocumentToPrincipal("C", "", getPrincipalIdForName("rkirkend"), "", true);
    		fail("should have thrown exception cant ad hoc completes on dissaproved documents");
		} catch (Exception e) {
		}

    }
	
	@Test
	public void testAdHocWithRequestLabel_ToPrincipal() throws Exception {
		WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName("user1"), ADHOC_DOC);
		String label = "MY PRINCIPAL LABEL";
		doc.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_APPROVE_REQ, null, "", getPrincipalIdForName("ewestfal"), "", true, label);
		
		List<ActionRequestValue> actionRequests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(doc.getRouteHeaderId());
		assertEquals("Shoudl have 1 request.", 1, actionRequests.size());
		ActionRequestValue actionRequest = actionRequests.get(0);
		assertEquals("Should be an approve request", KEWConstants.ACTION_REQUEST_APPROVE_REQ, actionRequest.getActionRequested());
		assertEquals("Invalid request label", label, actionRequest.getRequestLabel());
		assertEquals("Request should be initialized", KEWConstants.ACTION_REQUEST_INITIALIZED, actionRequest.getStatus());
		
		// now route the document, it should activate the request and create an action item
		doc.routeDocument("");
		
		Collection<ActionItem> actionItems = KEWServiceLocator.getActionListService().getActionListForSingleDocument(doc.getRouteHeaderId());
		assertEquals("Should have 1 action item.", 1, actionItems.size());
		ActionItem actionItem = actionItems.iterator().next();
		assertEquals("ActionItem should be constructed from request.", actionRequest.getActionRequestId(), actionItem.getActionRequestId());
		assertEquals("ActionItem should have same label", label, actionItem.getRequestLabel());
	}

	@Test
	public void testAdHocWithRequestLabel_ToGroup() throws Exception {
		WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName("user1"), ADHOC_DOC);
		String label = "MY GROUP LABEL";
		Group workflowAdmin = KEWServiceLocator.getIdentityHelperService().getGroupByName("KR-WKFLW", "WorkflowAdmin");
		doc.adHocRouteDocumentToGroup(KEWConstants.ACTION_REQUEST_APPROVE_REQ, null, "", workflowAdmin.getId(), "", true, label);
		
		List<ActionRequestValue> actionRequests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(doc.getRouteHeaderId());
		assertEquals("Should have 1 request.", 1, actionRequests.size());
		ActionRequestValue actionRequest = actionRequests.get(0);
		assertEquals("Should be an approve request", KEWConstants.ACTION_REQUEST_APPROVE_REQ, actionRequest.getActionRequested());
		assertEquals("Invalid request label", label, actionRequest.getRequestLabel());
		assertEquals("Request should be initialized", KEWConstants.ACTION_REQUEST_INITIALIZED, actionRequest.getStatus());
		
		// now route the document, it should activate the request and create action items
		doc.routeDocument("");
		
		Collection<ActionItem> actionItems = KEWServiceLocator.getActionListService().getActionListForSingleDocument(doc.getRouteHeaderId());
		assertTrue("Should have more than 1 action item.", actionItems.size() > 1);
		for (ActionItem actionItem : actionItems) {
			assertEquals("ActionItem should be constructed from request.", actionRequest.getActionRequestId(), actionItem.getActionRequestId());
			assertEquals("ActionItem should have same label", label, actionItem.getRequestLabel());
		}
	}
	
	   @Test
	    public void testAdHocWithNoNodes() throws Exception {
	        WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName("user1"), ADHOC_NO_NODE_DOC);
	        String label = "MY PRINCIPAL LABEL";
	        //DocumentAuthorizer documentAuthorizer = getDocumentHelperService().getDocumentAuthorizer(document);
	        
	        doc.adHocRouteDocumentToPrincipal(KEWConstants.ACTION_REQUEST_FYI_REQ, null, "", getPrincipalIdForName("ewestfal"), "", true, label);
	        
	        List<ActionRequestValue> actionRequests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(doc.getRouteHeaderId());
	        assertEquals("Should have 1 request.", 1, actionRequests.size());
	        ActionRequestValue actionRequest = actionRequests.get(0);
	        assertEquals("Should be a FYI request", KEWConstants.ACTION_REQUEST_FYI_REQ, actionRequest.getActionRequested());
	        assertEquals("Invalid request label", label, actionRequest.getRequestLabel());
	        assertEquals("Request should be initialized", KEWConstants.ACTION_REQUEST_INITIALIZED, actionRequest.getStatus());
	        
	        // now route the document, it should activate the request and create an action item
	        doc.routeDocument("");
	        
	        Collection<ActionItem> actionItems = KEWServiceLocator.getActionListService().getActionListForSingleDocument(doc.getRouteHeaderId());
	        assertEquals("Should have 0 action item.", 0, actionItems.size());
	        //ActionItem actionItem = actionItems.iterator().next();
	        //assertEquals("ActionItem should be constructed from request.", actionRequest.getActionRequestId(), actionItem.getActionRequestId());
	        //assertEquals("ActionItem should have same label", label, actionItem.getRequestLabel());
	    }

	
    private WorkflowDocument getDocument(String netid) throws WorkflowException {
    	return new WorkflowDocument(getPrincipalIdForName(netid), docId);
    }
    private WorkflowDocument getDocument(String principalId, Long docId) throws WorkflowException {
    	return new WorkflowDocument(principalId, docId);
    }

}
