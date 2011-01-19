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
package org.kuali.rice.kew.actionrequest;

import org.junit.Test;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.service.KIMServiceLocator;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This test exercises various Action Request graph scenarios and tests them for correctness.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
/**
 * This is a description of what this class does - jjhanso don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
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

        WorkflowDocument document = new WorkflowDocument(getPrincipalIdFromPrincipalName("arh14"), "InlineRequestsDocumentType");
        document.setApplicationContent("<blah><step>step1</step></blah>");
        document.routeDocument("");

        TestUtilities.assertAtNode(document, "step1");
        List requests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(document.getRouteHeaderId());
        assertEquals("Should be 1 request.", 1, requests.size());
        ActionRequestValue user1Request = (ActionRequestValue) requests.get(0);
        assertEquals(getPrincipalIdForName("user1"), user1Request.getPrincipalId());

        // open doc as user1 and route it
        document = new WorkflowDocument(getPrincipalIdFromPrincipalName("user1"), document.getRouteHeaderId());
        document.setApplicationContent("<blah><step>step2</step></blah>");
        document.approve("");

        TestUtilities.assertAtNode(document, "step2");
        requests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(document.getRouteHeaderId());
        assertEquals("Should be 1 request.", 1, requests.size());
        ActionRequestValue workgroupRequest = (ActionRequestValue) requests.get(0);
        assertEquals(getGroupIdFromGroupName("KR-WKFLW", "TestWorkgroup"), workgroupRequest.getGroupId());

        // open doc as user in TestWorkgroup and route it
        document = new WorkflowDocument(getPrincipalIdFromPrincipalName("temay"), document.getRouteHeaderId());
        document.setApplicationContent("<blah><step>step3</step></blah>");
        document.approve("");

        TestUtilities.assertAtNode(document, "step3");
        requests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(document.getRouteHeaderId());
        assertEquals("Should be 1 request.", 1, requests.size());
        ActionRequestValue initiatorRequest = (ActionRequestValue) requests.get(0);
        assertEquals("INITIATOR", initiatorRequest.getRoleName());

        //assertEquals(document.getRouteHeader().getInitiator().getDisplayName(), initiatorRequest.getRecipient().getDisplayName());

        assertFalse("Document should not be FINAL", document.stateIsFinal());

        // open doc as initiator and route it
        document = new WorkflowDocument(getPrincipalIdFromPrincipalName("arh14"), document.getRouteHeaderId());
        document.approve("");

        assertTrue("Document should be FINAL", document.stateIsFinal());
    }

    @Test public void testInlineRequestsRouteModule_UsingAttributes() throws Exception {
        /*WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("arh14"), "InlineRequestsDocumentType_UsingAttributes");
        try {
            document.routeDocument("");
            fail("Bad route succeeded");
        } catch (WorkflowException we) {
            // should throw exception as no approvals were generated
        }*/

        WorkflowDocument document = new WorkflowDocument(getPrincipalIdFromPrincipalName("arh14"), "InlineRequestsDocumentType_UsingAttributes");
        document.setApplicationContent("<blah><step>step1</step></blah>");
        document.routeDocument("");

        TestUtilities.assertAtNode(document, "step1");
        List requests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(document.getRouteHeaderId());
        assertEquals("Should be 1 request.", 1, requests.size());
        ActionRequestValue user1Request = (ActionRequestValue) requests.get(0);
        assertEquals(getPrincipalIdForName("user1"), user1Request.getPrincipalId());

        // open doc as user1 and route it
        document = new WorkflowDocument(getPrincipalIdFromPrincipalName("user1"), document.getRouteHeaderId());
        document.setApplicationContent("<blah><step>step2</step></blah>");
        document.approve("");

        TestUtilities.assertAtNode(document, "step2");
        requests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(document.getRouteHeaderId());
        assertEquals("Should be 1 request.", 1, requests.size());
        ActionRequestValue workgroupRequest = (ActionRequestValue) requests.get(0);
        assertEquals(getGroupIdFromGroupName("KR-WKFLW", "TestWorkgroup"), workgroupRequest.getGroupId());

        // open doc as user in TestWorkgroup and route it
        document = new WorkflowDocument(getPrincipalIdFromPrincipalName("temay"), document.getRouteHeaderId());
        document.setApplicationContent("<blah><step>step3</step></blah>");
        document.approve("");

        TestUtilities.assertAtNode(document, "step3");
        requests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(document.getRouteHeaderId());
        assertEquals("Should be 1 request.", 1, requests.size());
        ActionRequestValue initiatorRequest = (ActionRequestValue) requests.get(0);
        assertEquals("INITIATOR", initiatorRequest.getRoleName());
        //assertEquals(getPrincipalIdForName("INITIATOR"), initiatorRequest.getPrincipalId());
        //assertEquals(document.getRouteHeader().getInitiator().getDisplayName(), initiatorRequest.getRecipient().getDisplayName());

        assertFalse("Document should not be FINAL", document.stateIsFinal());

        // open doc as initiator and route it
        document = new WorkflowDocument(getPrincipalIdFromPrincipalName("arh14"), document.getRouteHeaderId());
        document.approve("");

        assertTrue("Document should be FINAL", document.stateIsFinal());
    }

    /**
	 * Test that force action works properly in the face of delegations.
	 * Tests the resolution of KULWF-642.
	 *
	 * @throws Exception
	 */
	@Test public void testForceActionWithDelegation() throws Exception {
		// at first, we'll route the document so that the bug is not exposed and verify the action request graph
		WorkflowDocument document = new WorkflowDocument(getPrincipalIdFromPrincipalName("user1"), "testForceActionWithDelegation");
		document.routeDocument("");
		TestUtilities.assertAtNode(document, "Node1");
		List rootRequests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(document.getRouteHeaderId());
		assertEquals("Should be 1 root request.", 1, rootRequests.size());
		ActionRequestValue ewestfalRequest = (ActionRequestValue)rootRequests.get(0);
		assertTrue("Request to ewestfal should be force action of true", ewestfalRequest.getForceAction());
		assertEquals("Should have 1 child request.", 1, ewestfalRequest.getChildrenRequests().size());
		ActionRequestValue rkirkendRequest = (ActionRequestValue)ewestfalRequest.getChildrenRequests().get(0);
		assertFalse("Request to rkirkend should be force action of false", rkirkendRequest.getForceAction());

		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("ewestfal"), "testForceActionWithDelegation");

		// After we route the document it should be at the first node in the document where "ewestfal"
		// is the primary approver with force action = true and "rkirkend" is the primary
		// delegate with force action = false.  In the KULWF-642 bug, the document would have
		// progressed past the first node in an auto-approve scenario even though ewestfal's rule
		// is force action = true;
		document.routeDocument("");

		// we should be at the first node in the document
		TestUtilities.assertAtNode(document, "Node1");

		document.approve("");
		assertTrue("Document should be FINAL", document.stateIsFinal());


	}

	/**
	 * Test that Role to Role Delegation works properly.
	 * Implemented to expose the bug and test the fix for KULWF-655.
     * @throws Exception
     */
	@Test public void testRoleToRoleDelegation() throws Exception {
		WorkflowDocument document = new WorkflowDocument(getPrincipalIdFromPrincipalName("user1"), "testRoleToRoleDelegation");
		document.routeDocument("");

		// after routing the document we should have an approve request to ewestfal, this request should have
		// one primary delegate and three secondary delegates
		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("ewestfal"), document.getRouteHeaderId());
		assertTrue("ewestfal should have an approve request.", document.isApprovalRequested());
		// now check all of ewestfal's delegates
		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("jhopf"), document.getRouteHeaderId());
		assertTrue("Should have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("xqi"), document.getRouteHeaderId());
		assertTrue("Should have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("jitrue"), document.getRouteHeaderId());
		assertTrue("Should have an approve request.", document.isApprovalRequested());

		// now approve as the primary delegator, this is where we were seeing the problem in KULWF-655, the
		// action request graph was not getting properly deactivated and it was not getting associated with the
		// "ActionTaken" properly
		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("jhopf"), document.getRouteHeaderId());
		document.approve("Approving as primary delegate.");

		// after the primary delegate approves, verify that the entire action request graph was
		// deactivated in grand fashion
		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("ewestfal"), document.getRouteHeaderId());
		assertFalse("the primary approver should no longer have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("jhopf"), document.getRouteHeaderId());
		assertFalse("Should not have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("xqi"), document.getRouteHeaderId());
		assertFalse("Should not have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("jitrue"), document.getRouteHeaderId());
		assertFalse("Should not have an approve request.", document.isApprovalRequested());

		List actionRequests = KEWServiceLocator.getActionRequestService().findAllActionRequestsByRouteHeaderId(document.getRouteHeaderId());
		assertEquals("Wrong number of action requests.", 7, actionRequests.size());
		for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iterator.next();
			assertTrue("Request should be deactivated.", request.isDeactivated());
			if (request.isRoleRequest()) {
				assertEquals("Should be all approve request", KEWConstants.APPROVE_POLICY_ALL_APPROVE, request.getApprovePolicy());
			} else {
				assertEquals("Should not have first approve policy set", KEWConstants.APPROVE_POLICY_FIRST_APPROVE, request.getApprovePolicy());
			}
		}

	}

	//testMixedbagRoleToRoleDelegation

	@Test public void testRoleToRoleMixedApprovePoliciesDelegation() throws Exception {
		WorkflowDocument document = new WorkflowDocument(getPrincipalIdFromPrincipalName("user1"), "testMixedbagRoleToRoleDelegation");
		document.routeDocument("");

		// after routing the document we should have an approve request to ewestfal, this request should have
		// one primary delegate and three secondary delegates
		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("ewestfal"), document.getRouteHeaderId());
		assertTrue("ewestfal should have an approve request.", document.isApprovalRequested());
		// now check all of ewestfal's delegates
		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("jhopf"), document.getRouteHeaderId());
		assertTrue("Should have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("xqi"), document.getRouteHeaderId());
		assertTrue("Should have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("jitrue"), document.getRouteHeaderId());
		assertTrue("Should have an approve request.", document.isApprovalRequested());

		// now approve as the primary delegator, this is where we were seeing the problem in KULWF-655, the
		// action request graph was not getting properly deactivated and it was not getting associated with the
		// "ActionTaken" properly
		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("jhopf"), document.getRouteHeaderId());
		document.approve("Approving as primary delegate.");

		// after the primary delegate approves, verify that the entire action request graph was
		// deactivated in grand fashion
		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("ewestfal"), document.getRouteHeaderId());
		assertFalse("the primary approver should no longer have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("jhopf"), document.getRouteHeaderId());
		assertFalse("Should not have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("xqi"), document.getRouteHeaderId());
		assertFalse("Should not have an approve request.", document.isApprovalRequested());
		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("jitrue"), document.getRouteHeaderId());
		assertFalse("Should not have an approve request.", document.isApprovalRequested());

		List actionRequests = KEWServiceLocator.getActionRequestService().findAllActionRequestsByRouteHeaderId(document.getRouteHeaderId());
		assertEquals("Wrong number of action requests.", 7, actionRequests.size());
		for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iterator.next();
			assertTrue("Request should be deactivated.", request.isDeactivated());
			if (request.isRoleRequest() && request.getRoleName().equals(RoleToRoleDelegationRole.MAIN_ROLE)) {
				assertEquals("Should be all approve request", KEWConstants.APPROVE_POLICY_ALL_APPROVE, request.getApprovePolicy());
			} else if (request.isRoleRequest() && request.getRoleName().equals(RoleToRoleDelegationRole.PRIMARY_DELEGATE_ROLE)) {
				assertEquals("Should be first approve request", KEWConstants.APPROVE_POLICY_FIRST_APPROVE, request.getApprovePolicy());
			} else if (request.isRoleRequest() && request.getRoleName().equals(RoleToRoleDelegationRole.SECONDARY_DELEGATE_ROLE)) {
				assertEquals("Should be first approve request", KEWConstants.APPROVE_POLICY_FIRST_APPROVE, request.getApprovePolicy());
			} else if (request.isRoleRequest()) {
				fail("the roles have been messed up");
			} else {
				assertEquals("Should not have first approve policy set", KEWConstants.APPROVE_POLICY_FIRST_APPROVE, request.getApprovePolicy());
			}
		}

	}

	// see: https://test.kuali.org/jira/browse/KULRICE-2001
	@Test public void testUnresolvableRoleAttributeRecipients() throws Exception {
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdFromPrincipalName("user1"), "UnresolvableRoleRecipsDocType");
        try {
        	document.routeDocument("");
        } catch (Exception e) {
            // this doc has a rule with a role that produces an invalid recipient id
            // should receive an error when it attempts to route to the invalid recipient and trigger exception routing on the document
        	TestUtilities.getExceptionThreader().join();
        	document = new WorkflowDocument(getPrincipalIdForName("user1"), document.getRouteHeaderId());
            assertTrue("Document should be in exception routing", document.stateIsException());
        }
	}
	
	/*
	 * The test was created to test Groups with with the All approve policy
	 * This is commented out because that is currently not supported in rice.
	 */
//	@Test public void testGroupRecipientsWithAllApprovePolicy() throws Exception {
//        WorkflowDocument document = new WorkflowDocument(getPrincipalIdFromPrincipalName("user1"), "testGroupAllApprovePolicy");
//        document.routeDocument("");
//        
//        assertTrue("Should have approval policy of All", document.getActionRequests()[0].getApprovePolicy().equals(KEWConstants.APPROVE_POLICY_ALL_APPROVE));
//        
//    	document = new WorkflowDocument(getPrincipalIdFromPrincipalName("ewestfal"), document.getRouteHeaderId());
//		assertTrue("ewestfal should have an approve request.", document.isApprovalRequested());
//		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("jhopf"), document.getRouteHeaderId());
//		assertTrue("Should have an approve request.", document.isApprovalRequested());
//		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("xqi"), document.getRouteHeaderId());
//		assertTrue("Should have an approve request.", document.isApprovalRequested());
//		document = new WorkflowDocument(getPrincipalIdFromPrincipalName("jitrue"), document.getRouteHeaderId());
//		assertTrue("Should have an approve request.", document.isApprovalRequested());
		
		//approve document as jitrue
		//document.approve("Approving as primary jitrue.");
		
		//make sure other group members still have approve requests.
		//document = new WorkflowDocument(getPrincipalIdFromPrincipalName("ewestfal"), document.getRouteHeaderId());
		//assertTrue("ewestfal should have an approve request.", document.isApprovalRequested());
		//document = new WorkflowDocument(getPrincipalIdFromPrincipalName("jhopf"), document.getRouteHeaderId());
		//assertTrue("Should have an approve request.", document.isApprovalRequested());
		//document = new WorkflowDocument(getPrincipalIdFromPrincipalName("xqi"), document.getRouteHeaderId());
		//assertTrue("Should have an approve request.", document.isApprovalRequested());
		
		//document = new WorkflowDocument(getPrincipalIdFromPrincipalName("jitrue"), document.getRouteHeaderId());
		//assertTrue("Should NOT have an approve request.", document.isApprovalRequested());
//	}


	private String getPrincipalIdFromPrincipalName(String principalName) {
	    return KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(principalName).getPrincipalId();
	}

    private String getGroupIdFromGroupName(String namespace, String groupName) {
        return KIMServiceLocator.getIdentityManagementService().getGroupByName(namespace, groupName).getGroupId();
    }

    private String getRoleIdFromRoleName(String namespaceCode, String roleName) {
        return KIMServiceLocator.getRoleService().getRoleIdByName(namespaceCode, roleName);
    }
}
