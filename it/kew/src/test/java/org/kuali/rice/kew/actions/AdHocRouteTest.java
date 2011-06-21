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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionRequestStatus;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.test.BaselineTestCase;

@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class AdHocRouteTest extends KEWTestCase {

	private static final String ADHOC_DOC = "AdhocRouteTest";
	private static final String ADHOC_NO_NODE_DOC = "AdHocNoNodeTest";
	private String docId;

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }

    @Test
    public void testRequestLabel() throws Exception{
    	String note = "test note";
    	Person per = KimApiServiceLocator.getPersonService().getPersonByPrincipalName("rkirkend");
    	WorkflowDocument doc = WorkflowDocument.createDocument(per.getPrincipalId(), ADHOC_DOC);

    	docId = doc.getDocumentId();

    	doc.adHocToPrincipal(ActionRequestType.FYI, "AdHoc", "annotation1", getPrincipalIdForName("dewey"), "respDesc1", false, note);

    	doc = getDocument(per.getPrincipalId(), docId);
    	List<ActionRequestValue> actionRequests = KEWServiceLocator.getActionRequestService().findAllActionRequestsByDocumentId(docId);
    	for(ActionRequestValue arv : actionRequests){
    		assertTrue("The note we passed in should equal the one we get out. note=["+ note +"]", note.equals(arv.getRequestLabel()));
    	}
    }

	@Test
	public void testParallelAdHocRouting() throws Exception {
    	WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), ADHOC_DOC);
    	docId = doc.getDocumentId();
    	doc.adHocToPrincipal(ActionRequestType.APPROVE, "AdHoc", "annotation1", getPrincipalIdForName("dewey"), "respDesc1", false);

    	doc = getDocument("dewey");
    	assertFalse("User andlee should not have an approve request yet.  Document not yet routed.", doc.isApprovalRequested());

    	doc.adHocToGroup(ActionRequestType.APPROVE, "AdHoc", "annotation2", getGroupIdForName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "WorkflowAdmin"), "respDesc2", true);

    	doc = getDocument("quickstart");
    	assertFalse("User should not have approve request yet.  Document not yet routed.", doc.isApprovalRequested());

    	doc = getDocument("rkirkend");
    	doc.route("");

    	// there should be two adhoc requests
    	List<ActionRequest> actionRequests = doc.getRootActionRequests();
    	for (ActionRequest actionRequest : actionRequests) {
    		assertTrue("Request should be an adhoc request.", actionRequest.isAdHocRequest());
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
    	assertTrue("The document should be final", doc.isFinal());
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
        WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), ADHOC_DOC);
        docId = doc.getDocumentId();
        doc.adHocToPrincipal(ActionRequestType.APPROVE, ADHOC_NODE, "annotation1", getPrincipalIdForName("rkirkend"), "", true);

        doc.route("");
        assertTrue(doc.isEnroute());

        doc = getDocument("rkirkend");
        assertTrue("rkirkend should have an approval request on the document", doc.isApprovalRequested());
        TestUtilities.assertAtNodeNew(doc, ADHOC_NODE);

        // now try it with force action=false
        doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), ADHOC_DOC);
        docId = doc.getDocumentId();
        doc.adHocToPrincipal(ActionRequestType.APPROVE, ADHOC_NODE, "annotation1", getPrincipalIdForName("rkirkend"), "", false);

        doc.route("");
        assertTrue(doc.isEnroute());

        doc = getDocument("rkirkend");
        assertFalse("rkirkend should NOT have an approval request on the document", doc.isApprovalRequested());
        TestUtilities.assertAtNodeNew(doc, "One");
        doc = getDocument("user1");
        assertTrue("user1 should have an approval request on the document", doc.isApprovalRequested());
    }

	@Test
	public void testSerialAdHocRouting() throws Exception {
    	WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), ADHOC_DOC);
    	docId = doc.getDocumentId();
    	doc.route("");
    	doc = getDocument("user1");
    	doc.adHocToPrincipal(ActionRequestType.APPROVE, "One", "annotation1", getPrincipalIdForName("user2"), "", false);
    	doc.adHocToPrincipal(ActionRequestType.APPROVE, "One", "annotation1", getPrincipalIdForName("rkirkend"), "", true);
    	doc.approve("");
    	doc = getDocument("rkirkend");
    	assertFalse("rkirkend should not have the document at this point 'S' activation", doc.isApprovalRequested());
    	doc = getDocument("user2");
    	assertTrue("user2 should have an approve request", doc.isApprovalRequested());
    	doc.approve("");
    	doc = getDocument("rkirkend");
    	doc.approve("");
    	assertTrue("The document should be final", doc.isFinal());
    }

	@Test
    public void testRepeatedAdHocRouting() throws Exception {
        final String ADHOC_NODE = "AdHoc";
        WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), ADHOC_DOC);
        docId = doc.getDocumentId();
        doc.adHocToPrincipal(ActionRequestType.APPROVE, ADHOC_NODE, "annotation1", getPrincipalIdForName("user2"), "", false);
        doc.route("");
        doc = getDocument("rkirkend");
        assertFalse("rkirkend should NOT have an approval request on the document", doc.isApprovalRequested());
        // user1 shouldn't have an approve request YET - to prove that we have not yet advanced past this initial AdHoc node
        doc = getDocument("user1");
        assertFalse("user1 should NOT have an approval request on the document", doc.isApprovalRequested());

        doc = getDocument("user2");
        assertTrue("user2 should have an approval request on document", doc.isApprovalRequested());
        doc.adHocToPrincipal(ActionRequestType.APPROVE, ADHOC_NODE, "annotation1", getPrincipalIdForName("user3"), "", false);
        doc.approve("");
        doc = getDocument("user2");
        assertFalse("user2 should NOT have an approval request on the document", doc.isApprovalRequested());

        doc = getDocument("user3");
        assertTrue("user3 should have an approval request on document", doc.isApprovalRequested());
        doc.adHocToPrincipal(ActionRequestType.APPROVE, ADHOC_NODE, "annotation1", getPrincipalIdForName("rkirkend"), "", true);
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

        assertTrue("The document should be final", doc.isFinal());
    }

    @Test public void testAdHocWhenDocumentIsInitiated() throws Exception {
    	WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), "TakeWorkgroupAuthorityDoc");
        document.saveDocumentData();
        assertTrue(document.isInitiated());

        document.adHocToPrincipal(ActionRequestType.APPROVE, "My Annotation", getPrincipalIdForName("rkirkend"), "", true);
        document.adHocToPrincipal(ActionRequestType.FYI, "My Annotation", getPrincipalIdForName("user1"), "", true);

        // this is an initiated document, the requests should not be activated yet
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), document.getDocumentId());
        assertFalse(document.isApprovalRequested());
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("user1"), document.getDocumentId());
        assertFalse(document.isFYIRequested());

        // now route the document, the requests should be activated
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("ewestfal"), document.getDocumentId());
        document.route("");
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), document.getDocumentId());
        assertTrue(document.isApprovalRequested());
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("user1"), document.getDocumentId());
        assertTrue(document.isFYIRequested());
    }

    @Test public void testAdHocWhenDocumentIsFinal() throws Exception {
        WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), "TakeWorkgroupAuthorityDoc");
        document.route("");
        TestUtilities.assertAtNodeNew(document, "WorkgroupByDocument");

        try {
        	document.adHocToPrincipal(ActionRequestType.APPROVE, "AdHoc", "", getPrincipalIdForName("ewestfal"), "", true);
        	fail("document should not be allowed to route to nodes that are complete");
		} catch (Exception e) {
		}


        document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), document.getDocumentId());
        assertTrue("rkirkend should have request", document.isApprovalRequested());
        document.approve("");

        assertTrue("Document should be final", document.isFinal());

        document = WorkflowDocument.loadDocument(getPrincipalIdForName("user1"), document.getDocumentId());
        List<ActionRequest> requests = document.getRootActionRequests();
        for (ActionRequest request : requests) {
			if (request.isActivated()) {
				fail("Active requests should not be present on a final document");
			}
		}

        // try and adhoc a request to a final document, should blow up
        try {
        	document.adHocToPrincipal(ActionRequestType.APPROVE, "WorkgroupByDocument", "", getPrincipalIdForName("ewestfal"), "", true);
        	fail("Should not be allowed to adhoc approve to a final document.");
        } catch (Exception e) {

        }

        // it should be legal to adhoc an FYI on a FINAL document
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), document.getDocumentId());
        assertFalse("rkirkend should not have an FYI request.", document.isFYIRequested());
    	document.adHocToPrincipal(ActionRequestType.FYI, "WorkgroupByDocument", "", getPrincipalIdForName("rkirkend"), "", true);
    	document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), document.getDocumentId());
    	assertTrue("rkirkend should have an FYI request", document.isFYIRequested());
    }

    @Test public void testAdHocWhenDocumentIsSaved() throws Exception {
    	WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), "TakeWorkgroupAuthorityDoc");
        document.saveDocument("");

        // TODO test adhocing of approve requests

        assertTrue("Document should be saved.", document.isSaved());
    	document.adHocToPrincipal(ActionRequestType.FYI, "AdHoc", "", getPrincipalIdForName("rkirkend"), "", true);
    	document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), document.getDocumentId());
    	assertTrue("rkirkend should have an FYI request", document.isFYIRequested());
    }

    @Test public void testAdHocFieldsSavedCorrectly() throws Exception  {
    	WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), ADHOC_DOC);
    	docId = doc.getDocumentId();
    	doc.route("");
    	// find all current request and get a list of ids for elimination purposes later
    	List<Long> oldRequestIds = new ArrayList<Long>();
        for (Iterator iterator = KEWServiceLocator.getActionRequestService().findAllActionRequestsByDocumentId(doc.getDocumentId()).iterator(); iterator.hasNext();) {
            oldRequestIds.add(((ActionRequestValue) iterator.next()).getActionRequestId());
        }

        // send the adhoc route request
        doc = getDocument("user1");
    	doc.adHocToPrincipal(ActionRequestType.APPROVE, "One", "annotation1", getPrincipalIdForName("user2"), "respDesc", false);

    	// adhoc request should be only new request on document
    	ActionRequestValue request = null;
    	for (Iterator iter = KEWServiceLocator.getActionRequestService().findAllActionRequestsByDocumentId(doc.getDocumentId()).iterator(); iter.hasNext();) {
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
    	assertEquals("action requested code incorrect", request.getActionRequested(), ActionRequestType.APPROVE.getCode());
    	assertEquals("responsibility desc incorrect", request.getResponsibilityDesc(), "respDesc");
    	assertEquals("wrong force action", request.getForceAction(), Boolean.FALSE);

    }

	@Test
	public void testAdHocDissaprovedDocument() throws Exception {
    	WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), ADHOC_DOC);
    	docId = doc.getDocumentId();
    	doc.route("");

    	doc = getDocument("user1");
    	TestUtilities.assertAtNodeNew(doc, "One");
    	doc.disapprove("");
    	TestUtilities.assertAtNodeNew(doc, "One");
    	//adhoc an ack and fyi

    	doc.adHocToPrincipal(ActionRequestType.FYI, "One", "", getPrincipalIdForName("rkirkend"), "", true);
    	doc.adHocToPrincipal(ActionRequestType.ACKNOWLEDGE, "One", "", getPrincipalIdForName("user2"), "", true);

    	doc = getDocument("rkirkend");
    	assertTrue(doc.isFYIRequested());
    	doc.fyi();
    	doc = getDocument("user2");
    	assertTrue(doc.isAcknowledgeRequested());
    	doc.acknowledge("");

    	//make sure we cant ad hoc approves or completes
    	doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), ADHOC_DOC);
    	docId = doc.getDocumentId();
    	doc.route("");

    	doc = getDocument("user1");
    	doc.disapprove("");

    	// try to ad hoc an approve request
    	try {
    		doc.adHocToPrincipal(ActionRequestType.APPROVE, "One", "", getPrincipalIdForName("rkirkend"), "", true);
    		fail("should have thrown exception cant adhoc approvals on dissaproved documents");
    	} catch (Exception e) {

    	}

    	// try to ad hoc a complete request
    	try {
    		doc.adHocToPrincipal(ActionRequestType.COMPLETE, "One", "", getPrincipalIdForName("rkirkend"), "", true);
    		fail("should have thrown exception cant ad hoc completes on dissaproved documents");
    	} catch (Exception e) {

    	}

        // try to ad hoc an ack request
        try {
            doc.adHocToPrincipal(ActionRequestType.ACKNOWLEDGE, "", getPrincipalIdForName("user1"), "", true);
        } catch (Exception e) {
            e.printStackTrace();
            fail("should have thrown exception cant ad hoc completes on dissaproved documents");
    }

        // try to ad hoc a fyi request
        try {
            doc.adHocToPrincipal(ActionRequestType.FYI, "", getPrincipalIdForName("user1"), "", true);
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
    	WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("user1"), ADHOC_DOC);
    	docId = doc.getDocumentId();

		// do an appspecific route to jitrue and NonSIT (w/ force action =
		// false), should end up at the current node

    	doc.adHocToPrincipal(ActionRequestType.APPROVE, "", getPrincipalIdForName("jitrue"), "", false);
    	doc.adHocToGroup(ActionRequestType.APPROVE, "", getGroupIdForName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "NonSIT"), "", false);
    	doc.route("");

		// user1 should not have a request, his action should have counted for
		// the ad hoc request to the workgroup
    	doc = getDocument("user1");
    	assertFalse(doc.isApprovalRequested());
    	doc = getDocument("jitrue");
    	assertTrue(doc.isApprovalRequested());
		// we should still be at the "AdHoc" node because we sent an ad hoc
		// request to jitrue and the workgroup
    	TestUtilities.assertAtNodeNew(doc, "AdHoc");

    	// now disapprove as jitrue
    	doc = getDocument("jitrue");
    	TestUtilities.assertAtNodeNew(doc, "AdHoc");
    	doc.disapprove("");
    	// we should stay at the AdHoc node following the disapprove
    	TestUtilities.assertAtNodeNew(doc, "AdHoc");
    	assertTrue(doc.isDisapproved());
    	//adhoc an ack and fyi

    	doc.adHocToPrincipal(ActionRequestType.FYI, "", getPrincipalIdForName("rkirkend"), "", true);
    	doc.adHocToPrincipal(ActionRequestType.ACKNOWLEDGE, "", getPrincipalIdForName("user2"), "", true);

    	doc.adHocToGroup(ActionRequestType.ACKNOWLEDGE, "", getGroupIdForName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "NonSIT"), "", true);

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
    	TestUtilities.assertNumberOfPendingRequests(doc.getDocumentId(), 0);

		// make sure we cant ad hoc approves or completes on a "terminal"
		// document
    	try {
    		doc.adHocToPrincipal(ActionRequestType.APPROVE, "", getPrincipalIdForName("rkirkend"), "", true);
    		fail("should have thrown exception cant adhoc approvals on dissaproved documents");
		} catch (Exception e) {
		}
    	// try to ad hoc a complete request
    	try {
    		doc.adHocToPrincipal(ActionRequestType.COMPLETE, "", getPrincipalIdForName("rkirkend"), "", true);
    		fail("should have thrown exception cant ad hoc completes on dissaproved documents");
		} catch (Exception e) {
		}

    }
	
	@Test
	public void testAdHocWithRequestLabel_ToPrincipal() throws Exception {
		WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("user1"), ADHOC_DOC);
		String label = "MY PRINCIPAL LABEL";
		doc.adHocToPrincipal(ActionRequestType.APPROVE, null, "", getPrincipalIdForName("ewestfal"), "", true, label);
		
		List<ActionRequestValue> actionRequests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(doc.getDocumentId());
		assertEquals("Shoudl have 1 request.", 1, actionRequests.size());
		ActionRequestValue actionRequest = actionRequests.get(0);
		assertEquals("Should be an approve request", ActionRequestType.APPROVE.getCode(), actionRequest.getActionRequested());
		assertEquals("Invalid request label", label, actionRequest.getRequestLabel());
		assertEquals("Request should be initialized", ActionRequestStatus.INITIALIZED.getCode(), actionRequest.getStatus());
		
		// now route the document, it should activate the request and create an action item
		doc.route("");
		
		Collection<ActionItem> actionItems = KEWServiceLocator.getActionListService().getActionListForSingleDocument(doc.getDocumentId());
		assertEquals("Should have 1 action item.", 1, actionItems.size());
		ActionItem actionItem = actionItems.iterator().next();
		assertEquals("ActionItem should be constructed from request.", actionRequest.getActionRequestId(), actionItem.getActionRequestId());
		assertEquals("ActionItem should have same label", label, actionItem.getRequestLabel());
	}

	@Test
	public void testAdHocWithRequestLabel_ToGroup() throws Exception {
		WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("user1"), ADHOC_DOC);
		String label = "MY GROUP LABEL";
		Group workflowAdmin = KEWServiceLocator.getIdentityHelperService().getGroupByName("KR-WKFLW", "WorkflowAdmin");
		doc.adHocToGroup(ActionRequestType.APPROVE, null, "", workflowAdmin.getId(), "", true, label);
		
		List<ActionRequestValue> actionRequests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(doc.getDocumentId());
		assertEquals("Should have 1 request.", 1, actionRequests.size());
		ActionRequestValue actionRequest = actionRequests.get(0);
		assertEquals("Should be an approve request", ActionRequestType.APPROVE.getCode(), actionRequest.getActionRequested());
		assertEquals("Invalid request label", label, actionRequest.getRequestLabel());
		assertEquals("Request should be initialized", ActionRequestStatus.INITIALIZED.getCode(), actionRequest.getStatus());
		
		// now route the document, it should activate the request and create action items
		doc.route("");
		
		Collection<ActionItem> actionItems = KEWServiceLocator.getActionListService().getActionListForSingleDocument(doc.getDocumentId());
		assertTrue("Should have more than 1 action item.", actionItems.size() > 1);
		for (ActionItem actionItem : actionItems) {
			assertEquals("ActionItem should be constructed from request.", actionRequest.getActionRequestId(), actionItem.getActionRequestId());
			assertEquals("ActionItem should have same label", label, actionItem.getRequestLabel());
		}
	}
	
	   @Test
	    public void testAdHocWithNoNodes() throws Exception {
	        WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("user1"), ADHOC_NO_NODE_DOC);
	        String label = "MY PRINCIPAL LABEL";
	        //DocumentAuthorizer documentAuthorizer = getDocumentHelperService().getDocumentAuthorizer(document);
	        
	        doc.adHocToPrincipal(ActionRequestType.FYI, null, "", getPrincipalIdForName("ewestfal"), "", true, label);
	        
	        List<ActionRequestValue> actionRequests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(doc.getDocumentId());
	        assertEquals("Should have 1 request.", 1, actionRequests.size());
	        ActionRequestValue actionRequest = actionRequests.get(0);
	        assertEquals("Should be a FYI request", ActionRequestType.FYI.getCode(), actionRequest.getActionRequested());
	        assertEquals("Invalid request label", label, actionRequest.getRequestLabel());
	        assertEquals("Request should be initialized", ActionRequestStatus.INITIALIZED.getCode(), actionRequest.getStatus());
	        
	        // now route the document, it should activate the request and create an action item
	        doc.route("");
	        
	        Collection<ActionItem> actionItems = KEWServiceLocator.getActionListService().getActionListForSingleDocument(doc.getDocumentId());
	        assertEquals("Should have 0 action item.", 0, actionItems.size());
	        //ActionItem actionItem = actionItems.iterator().next();
	        //assertEquals("ActionItem should be constructed from request.", actionRequest.getActionRequestId(), actionItem.getActionRequestId());
	        //assertEquals("ActionItem should have same label", label, actionItem.getRequestLabel());
	    }

	
    private WorkflowDocument getDocument(String netid) throws WorkflowException {
    	return WorkflowDocument.loadDocument(getPrincipalIdForName(netid), docId);
    }
    private WorkflowDocument getDocument(String principalId, String docId) throws WorkflowException {
    	return WorkflowDocument.loadDocument(principalId, docId);
    }

}
