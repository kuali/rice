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
package edu.iu.uis.eden.actionrequests;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.test.TestUtilities;

/**
 * This test exercises various Action Request graph scenarios and tests them for correctness.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionRequestScenariosTest extends KEWTestCase {

	protected void loadTestData() throws Exception {
		loadXmlFile("ActionRequestsConfig.xml");
	}

    /**
     * Tests InlineRequestsRouteModule routing.
     * 
     * @throws Exception
     */
    @Test public void testInlineRequestsRouteModule() throws Exception {
        /*WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("arh14"), "InlineRequestsDocumentType");
        try {
            document.routeDocument("");
            fail("Bad route succeeded");
        } catch (WorkflowException we) {
            // should throw exception as no approvals were generated 
        }*/
        
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("arh14"), "InlineRequestsDocumentType");
        document.setApplicationContent("<blah><step>step1</step></blah>");
        document.routeDocument("");
        
        TestUtilities.assertAtNode(document, "step1");
        List requests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(document.getRouteHeaderId());
        assertEquals("Should be 1 request.", 1, requests.size());
        ActionRequestValue user1Request = (ActionRequestValue) requests.get(0);
        assertEquals("User One", user1Request.getRecipient().getDisplayName());

        // open doc as user1 and route it
        document = new WorkflowDocument(new NetworkIdVO("user1"), document.getRouteHeaderId());
        document.setApplicationContent("<blah><step>step2</step></blah>");
        document.approve("");
        
        TestUtilities.assertAtNode(document, "step2");
        requests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(document.getRouteHeaderId());
        assertEquals("Should be 1 request.", 1, requests.size());
        ActionRequestValue workgroupRequest = (ActionRequestValue) requests.get(0);
        assertEquals("TestWorkgroup", workgroupRequest.getRecipient().getDisplayName());

        // open doc as user in TestWorkgroup and route it
        document = new WorkflowDocument(new NetworkIdVO("temay"), document.getRouteHeaderId());
        document.setApplicationContent("<blah><step>step3</step></blah>");
        document.approve("");

        TestUtilities.assertAtNode(document, "step3");
        requests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(document.getRouteHeaderId());
        assertEquals("Should be 1 request.", 1, requests.size());
        ActionRequestValue initiatorRequest = (ActionRequestValue) requests.get(0);
        assertEquals("INITIATOR", initiatorRequest.getRecipient().getDisplayName());
        //assertEquals(document.getRouteHeader().getInitiator().getDisplayName(), initiatorRequest.getRecipient().getDisplayName());

        assertFalse("Document should not be FINAL", document.stateIsFinal());

        // open doc as initiator and route it
        document = new WorkflowDocument(new NetworkIdVO("arh14"), document.getRouteHeaderId());
        document.approve("");

        assertTrue("Document should be FINAL", document.stateIsFinal());
    }

	/**
	 * Test that ignore previous works properly in the face of delegations.
	 * Tests the resolution of KULWF-642.
	 * 
	 * @throws Exception
	 */
	@Test public void testIgnorePreviousWithDelegation() throws Exception {
		// at first, we'll route the document so that the bug is not exposed and verify the action request graph
		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), "testIgnorePreviousWithDelegation");
		document.routeDocument("");
		TestUtilities.assertAtNode(document, "Node1");
		List rootRequests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(document.getRouteHeaderId());
		assertEquals("Should be 1 root request.", 1, rootRequests.size());
		ActionRequestValue ewestfalRequest = (ActionRequestValue)rootRequests.get(0);
		assertTrue("Request to ewestfal should be ignore previous of true", ewestfalRequest.getIgnorePrevAction().booleanValue());
		assertEquals("Should have 1 child request.", 1, ewestfalRequest.getChildrenRequests().size());
		ActionRequestValue rkirkendRequest = (ActionRequestValue)ewestfalRequest.getChildrenRequests().get(0);
		assertFalse("Request to rkirkend should be ignore previous of false", rkirkendRequest.getIgnorePrevAction().booleanValue());
		
		document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "testIgnorePreviousWithDelegation");
		
		// After we route the document it should be at the first node in the document where "ewestfal"
		// is the primary approver with ignore previous = true and "rkirkend" is the primary
		// delegate with ignore previous = false.  In the KULWF-642 bug, the document would have
		// progressed past the first node in an auto-approve scenario even though ewestfal's rule
		// is ignore previous = true;
		document.routeDocument("");
		
		// we should be at the first node in the document
		TestUtilities.assertAtNode(document, "Node1");
		
		document.approve("");
		assertTrue("Document should be FINAL", document.stateIsFinal());
		
		
	}

	/**
	 * Test that Role to Role Delegation works properly.
	 * Implemented to expose the bug and test the fix for KULWF-655. 
	 */
	@Test public void testRoleToRoleDelegation() throws Exception {
		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), "testRoleToRoleDelegation");
		document.routeDocument("");
		
		// after routing the document we should have an approve request to ewestfal, this request should have
		// one primary delegate and three secondary delegates
		document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
		assertTrue("ewestfal should have an approve request.", document.isApprovalRequested());
		// now check all of ewestfal's delegates
		document = new WorkflowDocument(new NetworkIdVO("jhopf"), document.getRouteHeaderId());
		assertTrue("Should have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdVO("xqi"), document.getRouteHeaderId());
		assertTrue("Should have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdVO("jitrue"), document.getRouteHeaderId());
		assertTrue("Should have an approve request.", document.isApprovalRequested());
	
		// now approve as the primary delegator, this is where we were seeing the problem in KULWF-655, the
		// action request graph was not getting properly deactivated and it was not getting associated with the
		// "ActionTaken" properly
		document = new WorkflowDocument(new NetworkIdVO("jhopf"), document.getRouteHeaderId());
		document.approve("Approving as primary delegate.");
		
		// after the primary delegate approves, verify that the entire action request graph was
		// deactivated in grand fashion
		document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
		assertFalse("the primary approver should no longer have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdVO("jhopf"), document.getRouteHeaderId());
		assertFalse("Should not have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdVO("xqi"), document.getRouteHeaderId());
		assertFalse("Should not have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdVO("jitrue"), document.getRouteHeaderId());
		assertFalse("Should not have an approve request.", document.isApprovalRequested());
		
		List actionRequests = KEWServiceLocator.getActionRequestService().findAllActionRequestsByRouteHeaderId(document.getRouteHeaderId());
		assertEquals("Wrong number of action requests.", 7, actionRequests.size());
		for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iterator.next();
			assertTrue("Request should be deactivated.", request.isDeactivated());
			if (request.isRoleRequest()) {
				assertEquals("Should be all approve request", EdenConstants.APPROVE_POLICY_ALL_APPROVE, request.getApprovePolicy());
			} else {
				assertEquals("Should not have first approve policy set", EdenConstants.APPROVE_POLICY_FIRST_APPROVE, request.getApprovePolicy());
			}
		}
		
	}

	//testMixedbagRoleToRoleDelegation
	
	@Test public void testRoleToRoleMixedApprovePoliciesDelegation() throws Exception {
		WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), "testMixedbagRoleToRoleDelegation");
		document.routeDocument("");
		
		// after routing the document we should have an approve request to ewestfal, this request should have
		// one primary delegate and three secondary delegates
		document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
		assertTrue("ewestfal should have an approve request.", document.isApprovalRequested());
		// now check all of ewestfal's delegates
		document = new WorkflowDocument(new NetworkIdVO("jhopf"), document.getRouteHeaderId());
		assertTrue("Should have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdVO("xqi"), document.getRouteHeaderId());
		assertTrue("Should have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdVO("jitrue"), document.getRouteHeaderId());
		assertTrue("Should have an approve request.", document.isApprovalRequested());
	
		// now approve as the primary delegator, this is where we were seeing the problem in KULWF-655, the
		// action request graph was not getting properly deactivated and it was not getting associated with the
		// "ActionTaken" properly
		document = new WorkflowDocument(new NetworkIdVO("jhopf"), document.getRouteHeaderId());
		document.approve("Approving as primary delegate.");
		
		// after the primary delegate approves, verify that the entire action request graph was
		// deactivated in grand fashion
		document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
		assertFalse("the primary approver should no longer have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdVO("jhopf"), document.getRouteHeaderId());
		assertFalse("Should not have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdVO("xqi"), document.getRouteHeaderId());
		assertFalse("Should not have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdVO("jitrue"), document.getRouteHeaderId());
		assertFalse("Should not have an approve request.", document.isApprovalRequested());
		
		List actionRequests = KEWServiceLocator.getActionRequestService().findAllActionRequestsByRouteHeaderId(document.getRouteHeaderId());
		assertEquals("Wrong number of action requests.", 7, actionRequests.size());
		for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iterator.next();
			assertTrue("Request should be deactivated.", request.isDeactivated());
			if (request.isRoleRequest() && request.getRoleName().equals(RoleToRoleDelegationRole.MAIN_ROLE)) {
				assertEquals("Should be all approve request", EdenConstants.APPROVE_POLICY_ALL_APPROVE, request.getApprovePolicy());
			} else if (request.isRoleRequest() && request.getRoleName().equals(RoleToRoleDelegationRole.PRIMARY_DELEGATE_ROLE)) {
				assertEquals("Should be first approve request", EdenConstants.APPROVE_POLICY_FIRST_APPROVE, request.getApprovePolicy());
			} else if (request.isRoleRequest() && request.getRoleName().equals(RoleToRoleDelegationRole.SECONDARY_DELEGATE_ROLE)) {
				assertEquals("Should be first approve request", EdenConstants.APPROVE_POLICY_FIRST_APPROVE, request.getApprovePolicy());
			} else if (request.isRoleRequest()) {
				fail("the roles have been messed up");
			} else {
				assertEquals("Should not have first approve policy set", EdenConstants.APPROVE_POLICY_FIRST_APPROVE, request.getApprovePolicy());
			}
		}
		
	}
	
}
