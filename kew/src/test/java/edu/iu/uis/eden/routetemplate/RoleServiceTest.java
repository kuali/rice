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
package edu.iu.uis.eden.routetemplate;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.kuali.workflow.role.RoleService;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routemodule.TestDocContent;
import edu.iu.uis.eden.routemodule.TestRecipient;
import edu.iu.uis.eden.routemodule.TestResponsibility;
import edu.iu.uis.eden.routemodule.TestRouteLevel;
import edu.iu.uis.eden.routemodule.TestRouteModuleXMLHelper;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.UserId;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Tests the role re-resolving.  This test depends on the route queue being synchronous.
 */
public class RoleServiceTest extends KEWTestCase {

	private static final String TEST_ROLE = "TestRole";
	private static final String TEST_GROUP_1 = "TestGroup1";
	private static final String TEST_GROUP_2 = "TestGroup2";
	private RoleService roleService;
	private Long documentId;
	private List group1 = new ArrayList();
	private List group2 = new ArrayList();


	protected void setUpTransaction() throws Exception {
		super.setUpTransaction();
		roleService = KEWServiceLocator.getRoleService();
		initializeAttribute();
		documentId = routeDocument();
	}

	private void initializeAttribute() throws Exception {
		group1.add(new AuthenticationUserId("jhopf"));
		group1.add(new AuthenticationUserId("pmckown"));
		group2.add(new AuthenticationUserId("xqi"));
		group2.add(new AuthenticationUserId("tbazler"));
		TestRuleAttribute.addRole(TEST_ROLE);
		TestRuleAttribute.addQualifiedRole(TEST_ROLE, TEST_GROUP_1);
		TestRuleAttribute.addQualifiedRole(TEST_ROLE, TEST_GROUP_2);
		TestRuleAttribute.setRecipients(TEST_ROLE, TEST_GROUP_1, group1);
		TestRuleAttribute.setRecipients(TEST_ROLE, TEST_GROUP_2, group2);
	}

	private Long routeDocument() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("rkirkend"), "TestDocumentType");
        document.setApplicationContent(TestRouteModuleXMLHelper.toXML(generateDocContent()));
        document.routeDocument("testing only");
        return document.getRouteHeaderId();
	}

	@Test public void testReResolveQualifiedRole() throws Exception {
		DocumentRouteHeaderValue loadedDocument = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
		assertEquals(EdenConstants.ROUTE_HEADER_ENROUTE_CD, loadedDocument.getDocRouteStatus());
		List requests = getTestRoleRequests(loadedDocument);
		assertEquals("Incorrect number of role control requests.", 2, requests.size());
		assertRequestGraphs(requests);

		// change the membership in TEST_GROUP_1
		List newGroup1Recipients = new ArrayList();
		newGroup1Recipients.add(new AuthenticationUserId("bmcgough"));
		newGroup1Recipients.add(new AuthenticationUserId("xqi"));
		newGroup1Recipients.add(new AuthenticationUserId("rkirkend"));
		TestRuleAttribute.setRecipients(TEST_ROLE, TEST_GROUP_1, newGroup1Recipients);
		roleService.reResolveQualifiedRole(loadedDocument, TEST_ROLE, TEST_GROUP_1);
		loadedDocument = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
		assertEquals(EdenConstants.ROUTE_HEADER_ENROUTE_CD, loadedDocument.getDocRouteStatus());
		requests = getTestRoleRequests(loadedDocument);
        // rkirkend is the initiator so his action should count for the TEST_GROUP_1 role after re-resolving, leaving only a single role request
		assertEquals("Incorrect number of role control requests.", 1, requests.size());
		assertRequestGraphs(requests);
		assertInitiatorRequestDone(TEST_ROLE, TEST_GROUP_1);

        // if we attempt to re-resolve with an non-existant qualified role, it _should_ be legal
        roleService.reResolveQualifiedRole(loadedDocument, TEST_ROLE, "random cool name");
        requests = getTestRoleRequests(loadedDocument);
        assertEquals("Incorrect number of role control requests.", 1, requests.size());
        assertRequestGraphs(requests);
	}

	@Test public void testReResolveQualifiedRoleErrors() throws Exception {
        // attempting to re-resolve with null values should throw exceptions
        try {
            roleService.reResolveQualifiedRole((DocumentRouteHeaderValue)null, null, null);
            fail("Exception should have been thrown when null values are passed.");
        } catch (Exception e) {}

        DocumentRouteHeaderValue loadedDocument = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
        try {
            roleService.reResolveQualifiedRole(loadedDocument, null, null);
            fail("Exception should have been thrown when null values are passed.");
        } catch (Exception e) {}

        // need to have a valid role name
        try {
            roleService.reResolveQualifiedRole(loadedDocument, "GimpyRoleName", TEST_GROUP_1);
            fail("Exception should be thrown when attempting to re-resolve with a bad role name.");
        } catch (Exception e) {}

        // now blanket approve a document to make it processed
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("rkirkend"), "TestDocumentType");
        document.setApplicationContent(TestRouteModuleXMLHelper.toXML(generateDocContent()));
        document.blanketApprove("");
        DocumentRouteHeaderValue baDoc = KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId());
        try {
            roleService.reResolveQualifiedRole(baDoc, TEST_ROLE, TEST_GROUP_1);
            fail("Shouldn't be able to resolve on a document with no active nodes.");
        } catch (Exception e) {}

    }

	@Test public void testReResolveRole() throws Exception {
		DocumentRouteHeaderValue loadedDocument = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
		assertEquals(EdenConstants.ROUTE_HEADER_ENROUTE_CD, loadedDocument.getDocRouteStatus());
		List requests = getTestRoleRequests(loadedDocument);
		assertEquals("Incorrect number of role control requests.", 2, requests.size());
		assertRequestGraphs(requests);

		// change membership in TEST_GROUP_1 and TEST_GROUP_2
		List newGroup1 = new ArrayList();
		List newGroup2 = new ArrayList();
		newGroup2.add(new AuthenticationUserId("ewestfal"));
		newGroup2.add(new AuthenticationUserId("jthomas"));
		// TEST_GROUP_1 should now be an empty role, therefore there should not be a request generated for it after re-resolution
		TestRuleAttribute.setRecipients(TEST_ROLE, TEST_GROUP_1, newGroup1);
		TestRuleAttribute.setRecipients(TEST_ROLE, TEST_GROUP_2, newGroup2);
		// re-resolve entire role
		roleService.reResolveRole(loadedDocument, TEST_ROLE);
		loadedDocument = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
		assertEquals(EdenConstants.ROUTE_HEADER_ENROUTE_CD, loadedDocument.getDocRouteStatus());
		requests = getTestRoleRequests(loadedDocument);
		// should be 1 because group 1 has no members
		assertEquals("Incorrect number of role control requests.", 1, requests.size());
		assertRequestGraphs(requests);
	}

	@Test public void testReResolveRoleErrors() throws Exception {
        // attempting to re-resolve with null values should throw exceptions
        try {
            roleService.reResolveRole((DocumentRouteHeaderValue)null, null);
            fail("Exception should have been thrown when null values are passed.");
        } catch (Exception e) {}

        DocumentRouteHeaderValue loadedDocument = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
        try {
            roleService.reResolveRole(loadedDocument, null);
            fail("Exception should have been thrown when null values are passed.");
        } catch (Exception e) {}

        // need to have a valid role name
        try {
            roleService.reResolveRole(loadedDocument, "GimpyRoleName");
            fail("Exception should be thrown when attempting to re-resolve with a bad role name.");
        } catch (Exception e) {}

        // now blanket approve a document to make it processed
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("rkirkend"), "TestDocumentType");
        document.setApplicationContent(TestRouteModuleXMLHelper.toXML(generateDocContent()));
        document.blanketApprove("");
        DocumentRouteHeaderValue baDoc = KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId());
        try {
            roleService.reResolveRole(baDoc, TEST_ROLE);
            fail("Shouldn't be able to re-resolve on a document with no active nodes.");
        } catch (Exception e) {}
    }

	/**
	 * Extract requests sent to TestRole.
	 */
	private List getTestRoleRequests(DocumentRouteHeaderValue document) {
		List testRoleRequests = new ArrayList();
		List requests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocIdAtRouteLevel(document.getRouteHeaderId(), document.getDocRouteLevel());
		for (Iterator iterator = requests.iterator(); iterator.hasNext();) {
			ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
			if (TEST_ROLE.equals(actionRequest.getRoleName())) {
				testRoleRequests.add(actionRequest);
			}
		}
		return testRoleRequests;
	}

	private void assertRequestGraphs(List requests) throws Exception {
		for (Iterator iterator = requests.iterator(); iterator.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iterator.next();
			if (TEST_GROUP_1.equals(request.getQualifiedRoleName())) {
				assertQualifiedRoleRequest(request, TEST_ROLE, TEST_GROUP_1);
			} else if (TEST_GROUP_2.equals(request.getQualifiedRoleName())) {
				assertQualifiedRoleRequest(request, TEST_ROLE, TEST_GROUP_2);
			}
		}
	}

	private void assertQualifiedRoleRequest(ActionRequestValue request, String roleName, String qualifiedRoleName) throws Exception {
		assertActionRequest(request, roleName, qualifiedRoleName);
		List recipients = TestRuleAttribute.getRecipients(roleName, qualifiedRoleName);
		assertEquals("Incorrect number of children requests.", recipients.size(), request.getChildrenRequests().size());
		for (Iterator childIt = request.getChildrenRequests().iterator(); childIt.hasNext();) {
			ActionRequestValue childRequest = (ActionRequestValue) childIt.next();
			assertActionRequest(childRequest, roleName, qualifiedRoleName);
			assertTrue("Child request to invalid user: "+childRequest.getWorkflowUser().getAuthenticationUserId().getAuthenticationId(), containsUser(recipients, childRequest.getWorkflowUser()));
			assertEquals("Child request should have no children.", 0, childRequest.getChildrenRequests().size());
		}
	}

	private void assertActionRequest(ActionRequestValue request, String roleName, String qualifiedRoleName) {
		assertEquals("Incorrect role name.", roleName, request.getRoleName());
		assertEquals("Incorrect qualified role name.", qualifiedRoleName, request.getQualifiedRoleName());
		assertEquals("Incorrect qualified role name label.", qualifiedRoleName, request.getQualifiedRoleNameLabel());
		assertTrue("Request should be activated or done.", EdenConstants.ACTION_REQUEST_ACTIVATED.equals(request.getStatus()) ||
				EdenConstants.ACTION_REQUEST_DONE_STATE.equals(request.getStatus()));
	}

	private boolean containsUser(List users, WorkflowUser user) throws Exception {
		for (Iterator iterator = users.iterator(); iterator.hasNext();) {
			UserId userId = (UserId) iterator.next();
			WorkflowUser itUser = KEWServiceLocator.getUserService().getWorkflowUser(userId);
			if (itUser.getWorkflowId().equals(user.getWorkflowId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets all "DONE" action requests that are to the initiator (rkirkend).  It then verifies that the initiator has a
	 * complete request and a re-resolved request.
	 */
	private void assertInitiatorRequestDone(String roleName, String qualifiedRoleNameLabel) throws Exception {
        WorkflowUser initiator = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("rkirkend"));
		List requests = KEWServiceLocator.getActionRequestService().findByStatusAndDocId(EdenConstants.ACTION_REQUEST_DONE_STATE, documentId);
		for (Iterator iterator = requests.iterator(); iterator.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iterator.next();
			if (!initiator.getWorkflowId().equals(request.getWorkflowId())) {
				iterator.remove();
			}
		}
		assertEquals("Initiator should have a complete request and their re-resolved request.", 2, requests.size());
		int roleRequestCount = 0;
		for (Iterator iterator = requests.iterator(); iterator.hasNext();) {
			ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
			if (TEST_ROLE.equals(actionRequest.getRoleName())) {
				roleRequestCount++;
				assertActionRequest(actionRequest, roleName, qualifiedRoleNameLabel);
				assertTrue("Initiator request should have a parent.", actionRequest.getParentActionRequest() != null);
			}
		}
		assertEquals("There should be 1 DONE request from the result of the role re-resolve.", 1, roleRequestCount);
	}

	private TestDocContent generateDocContent() {
		TestDocContent docContent = new TestDocContent();
		List routeLevels = new ArrayList();
		TestRouteLevel routeLevel1 = new TestRouteLevel();
		routeLevels.add(routeLevel1);
		docContent.setRouteLevels(routeLevels);
		routeLevel1.setPriority(1);
		List responsibilities = new ArrayList();
		routeLevel1.setResponsibilities(responsibilities);
		TestResponsibility responsibility1 = new TestResponsibility();
		responsibility1.setActionRequested(EdenConstants.ACTION_REQUEST_APPROVE_REQ);
		responsibility1.setPriority(1);
		TestRecipient recipient1 = new TestRecipient();
		recipient1.setId("rkirkend");
		recipient1.setType(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
		responsibility1.setRecipient(recipient1);
		responsibilities.add(responsibility1);
		return docContent;
	}

}
