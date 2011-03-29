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

import org.apache.commons.lang.StringUtils;
import org.custommonkey.xmlunit.XMLAssert;
import org.junit.Assert;
import org.junit.Test;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.dto.ActionItemDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;
import org.kuali.rice.kew.util.KEWWebServiceConstants;
import org.kuali.rice.kew.webservice.DocumentResponse;
import org.kuali.rice.kew.webservice.SimpleDocumentActionsWebService;
import org.kuali.rice.kew.webservice.StandardResponse;
import org.kuali.rice.kim.bo.Group;

import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This is a description of what this class does - Daniel Epstein don't forget
 * to fill this in.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 */
public class SimpleDocumentActionsWebServiceTest extends KEWTestCase {
	
	@Override
	protected void loadTestData() throws Exception {
		loadXmlFile("ActionsConfig.xml");
	}

	protected SimpleDocumentActionsWebService getSimpleDocumentActionsWebService() {
		return (SimpleDocumentActionsWebService) GlobalResourceLoader.getService(new QName(KEWWebServiceConstants.MODULE_TARGET_NAMESPACE, KEWWebServiceConstants.SimpleDocumentActionsWebService.WEB_SERVICE_NAME));
	}

	@Test
	public void testCreateAndRoute() throws Exception{

		SimpleDocumentActionsWebService simpleService = getSimpleDocumentActionsWebService();
		DocumentResponse dr = simpleService.create("admin","doc1", "BlanketApproveSequentialTest", "Doc1Title");
		StandardResponse sr = simpleService.route(dr.getDocId(), "admin", "Doc1Title", "<foo>bar</foo>", "Annotation!");
		sr = simpleService.approve(dr.getDocId(), "admin", "Doc1Title", "<foo>b</foo>", "Annotation!!!");
		assertTrue(StringUtils.isEmpty(sr.getErrorMessage()));
		
	}

	@Test
	public void testSave_NoDocContent() throws Exception{

		SimpleDocumentActionsWebService simpleService = getSimpleDocumentActionsWebService();
		DocumentResponse dr = simpleService.create("admin","doc1", "BlanketApproveSequentialTest", "Doc1Title");
		String docId = dr.getDocId();
		assertTrue(StringUtils.isEmpty(dr.getErrorMessage()));
		StandardResponse sr = simpleService.save(dr.getDocId(), "admin", "Doc1Title", null, "Annotation!");
		assertTrue(StringUtils.isEmpty(sr.getErrorMessage()));
		sr = simpleService.approve(dr.getDocId(), "admin", "Doc1Title", "<foo>b</foo>", "Annotation!!!");
		assertTrue(StringUtils.isEmpty(sr.getErrorMessage()));
	}

	@Test
	public void testSave_WithDocContent() throws Exception{

		SimpleDocumentActionsWebService simpleService = getSimpleDocumentActionsWebService();
		DocumentResponse dr = simpleService.create("admin","doc1", "BlanketApproveSequentialTest", "Doc1Title");
		String docId = dr.getDocId();
		assertTrue(StringUtils.isEmpty(dr.getErrorMessage()));
		String docContent1 = "<foo>bar</foo>";
		String docTitle1 = "Doc1Title";
		StandardResponse sr = simpleService.save(dr.getDocId(), "admin", docTitle1, docContent1, "Annotation!");
		assertTrue(StringUtils.isEmpty(sr.getErrorMessage()));
        verifyDocumentDataChanges(simpleService, docId, docContent1, docTitle1);
        String docContent2 = "<foo>b</foo>";
        String docTitle2 = "Doc2Title";
		sr = simpleService.approve(dr.getDocId(), "admin", docTitle2, docContent2, "Annotation!!!");
		assertTrue(StringUtils.isEmpty(sr.getErrorMessage()));
        verifyDocumentDataChanges(simpleService, docId, docContent2, docTitle2);

	}

	@Test
	public void testSaveDocContent() throws Exception{
        String docTitle1 = "Doc1Title";
        String docTitle2 = "Doc2Title";

		SimpleDocumentActionsWebService simpleService = getSimpleDocumentActionsWebService();
		DocumentResponse dr = simpleService.create(getPrincipalIdForName("admin"),"doc1", "BlanketApproveSequentialTest", "Doc1Title");
		assertTrue(StringUtils.isEmpty(dr.getErrorMessage()));
		// verify document content is empty
        assertTrue("doc content should be empty", ((dr.getDocContent() == null) || (StringUtils.isBlank(dr.getDocContent()))));
        assertEquals("doc title is wrong", docTitle1, dr.getTitle());
        String docId = dr.getDocId();
        String docContent = "<foo>bar</foo>";
		StandardResponse sr = simpleService.saveDocumentContent(docId, getPrincipalIdForName("admin"), docTitle2, docContent);
		assertTrue(StringUtils.isEmpty(sr.getErrorMessage()));
		verifyDocumentDataChanges(simpleService, docId, docContent, docTitle2);
	}

	protected void verifySuccess(StandardResponse sr) {
        assertTrue("response was invalid with error message: " + sr.getErrorMessage(), StringUtils.isEmpty(sr.getErrorMessage()));
	}

	protected void verifyDocumentDataChanges(SimpleDocumentActionsWebService simpleService, String docId, String newDocContent, String newDocTitle) throws Exception {
        DocumentResponse dr = simpleService.getDocument(docId, getPrincipalIdForName("admin"));
        verifySuccess(dr);

        // verify document content changed
        XMLAssert.assertXMLEqual("doc content value is incorrect", newDocContent, dr.getDocContent());
        // verify that the document title changed
        assertEquals("doc title should have changed", newDocTitle, dr.getTitle());
	}

	protected String createAndRouteDocument(SimpleDocumentActionsWebService simpleService, String docTitle, String docContent) throws Exception {
        DocumentResponse dr = simpleService.create(getPrincipalIdForName("admin"),"doc1", "BlanketApproveSequentialTest", docTitle);
        verifySuccess(dr);
        StandardResponse sr = simpleService.route(dr.getDocId(), getPrincipalIdForName("admin"), docTitle, docContent, "Annotation!");
        verifySuccess(sr);
        return dr.getDocId();
	}

	/*
	 * bmcgough A
	 * rkirkend A
	 * 
	 * pmckown A
	 * 
	 * temay K
	 * 
	 * jhopf K
	 * 
	 */

//	protected List getAdHocActionRequests(String docId) throws Exception {
//	    List<ActionRequestDTO> matchingRequests = new ArrayList<ActionRequestDTO>();
//	    ActionRequestDTO[] actionRequests = KEWServiceLocator.getWorkflowUtilityService().getActionRequests(Long.valueOf(docId), null, null);
//	    for (int i = 0; i < actionRequests.length; i++) {
//            ActionRequestDTO actionRequestDTO = actionRequests[i];
//            if (actionRequestDTO.isActivated() && actionRequestDTO.isInitialized())
//        }
//	    return matchingRequests;
//	}

	protected static void assertNumberOfPendingAdHocRequests(Long documentId, int expectedNumberOfPendingAdHocRequests) {
	    int actualPendingAdHocRequests = 0;
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(documentId);
        for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
            if (actionRequest.isAdHocRequest()) {
                actualPendingAdHocRequests++;
            }
        }
        Assert.assertEquals("Wrong number of pending requests for document: " + documentId, expectedNumberOfPendingAdHocRequests, actualPendingAdHocRequests);
    }

	@Test
    public void testRevokeAdHocRequestsByNodeName() throws Exception {
        SimpleDocumentActionsWebService simpleService = getSimpleDocumentActionsWebService();
        String docId = createAndRouteDocument(simpleService, "Doc1Title", "<foo>bar</foo>");
        // add adhoc at first node
        StandardResponse sr = simpleService.requestAdHocAckToPrincipal(docId, getPrincipalIdForName("admin"), getPrincipalIdForName("fran"), "");
        verifySuccess(sr);
        sr = simpleService.requestAdHocAckToPrincipal(docId, getPrincipalIdForName("admin"), getPrincipalIdForName("delyea"), "");
        verifySuccess(sr);
        // approve past first node
        sr = simpleService.approve(docId, getPrincipalIdForName("bmcgough"), "newDocTitle", "<foobar>test</foobar>", "");
        verifySuccess(sr);
        sr = simpleService.approve(docId, getPrincipalIdForName("rkirkend"), "newDocTitle", "<foobar>test</foobar>", "");
        verifySuccess(sr);
        // add adhoc at second node
        sr = simpleService.requestAdHocAckToPrincipal(docId, getPrincipalIdForName("admin"), getPrincipalIdForName("fred"), "");
        verifySuccess(sr);
        // check to make sure the adhoc requests remaining are correct
        assertNumberOfPendingAdHocRequests(Long.valueOf(docId), 3);
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "delyea");
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "fred");
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "fran");
        String newDocTitle = "newestDocTitle";
        String newDocContent = "<foobar>testerIsNew</foobar>";
        sr = simpleService.revokeAdHocRequestsByNodeName(docId, getPrincipalIdForName("pmckown"), newDocTitle, newDocContent, "WorkflowDocument", "");
        verifySuccess(sr);
        verifyDocumentDataChanges(simpleService, docId, newDocContent, newDocTitle);
        // check to make sure the adhoc requests remaining are correct
        assertNumberOfPendingAdHocRequests(Long.valueOf(docId), 1);
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "fred");
        TestUtilities.assertNotInActionList(getPrincipalIdForName("delyea"), Long.valueOf(docId));
        TestUtilities.assertNotInActionList(getPrincipalIdForName("fran"), Long.valueOf(docId));
    }

    @Test
    public void testRevokeAdHocRequestsByPrincipalId() throws Exception {
        SimpleDocumentActionsWebService simpleService = getSimpleDocumentActionsWebService();
        String docId = createAndRouteDocument(simpleService, "Doc1Title", "<foo>bar</foo>");
        // add adhoc at first node
        StandardResponse sr = simpleService.requestAdHocAckToPrincipal(docId, getPrincipalIdForName("admin"), getPrincipalIdForName("fran"), "");
        verifySuccess(sr);
        sr = simpleService.requestAdHocAckToPrincipal(docId, getPrincipalIdForName("admin"), getPrincipalIdForName("fred"), "");
        verifySuccess(sr);
        // verify requests
        assertNumberOfPendingAdHocRequests(Long.valueOf(docId), 2);
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "fred");
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "fran");
        // revoke the requests
        String newDocTitle = "newestDocTitle";
        String newDocContent = "<foobar>testerIsNew</foobar>";
        sr = simpleService.revokeAdHocRequestsByPrincipalId(docId, getPrincipalIdForName("admin"), newDocTitle, newDocContent, getPrincipalIdForName("fran"), "");
        verifySuccess(sr);
        verifyDocumentDataChanges(simpleService, docId, newDocContent, newDocTitle);
        // check to make sure the adhoc requests remaining are correct
        assertNumberOfPendingAdHocRequests(Long.valueOf(docId), 1);
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "fred");
        TestUtilities.assertNotInActionList(getPrincipalIdForName("fran"), Long.valueOf(docId));
    }

    @Test
    public void testRevokeAdHocRequestsByGroupId() throws Exception {
        SimpleDocumentActionsWebService simpleService = getSimpleDocumentActionsWebService();
        String docId = createAndRouteDocument(simpleService, "Doc1Title", "<foo>bar</foo>");
        // add adhoc at first node
        String groupNamespaceCode = "KR-WKFLW";
        String groupName = "NonSIT";
        StandardResponse sr = simpleService.requestAdHocAckToGroup(docId, getPrincipalIdForName("admin"), getGroupIdForName(groupNamespaceCode, groupName), "");
        verifySuccess(sr);
        sr = simpleService.requestAdHocAckToPrincipal(docId, getPrincipalIdForName("admin"), getPrincipalIdForName("fred"), "");
        verifySuccess(sr);
        // verify requests
        assertNumberOfPendingAdHocRequests(Long.valueOf(docId), 2);
        assertTrue("group should have pending request", doesGroupHavePendingRequest(docId, groupNamespaceCode, groupName));
        // revoke the requests
        String newDocTitle = "newestDocTitle";
        String newDocContent = "<foobar>testerIsNew</foobar>";
        sr = simpleService.revokeAdHocRequestsByGroupId(docId, getPrincipalIdForName("admin"), newDocTitle, newDocContent, getGroupIdForName("KR-WKFLW", "NonSIT"), "");
        verifySuccess(sr);
        verifyDocumentDataChanges(simpleService, docId, newDocContent, newDocTitle);
        // check to make sure the adhoc requests remaining are correct
        assertNumberOfPendingAdHocRequests(Long.valueOf(docId), 1);
        assertFalse("group should not have pending request", doesGroupHavePendingRequest(docId, groupNamespaceCode, groupName));
    }

    /**
     * Asserts that the group details passed in are a valid group and that it has an outstanding pending request
     */
    public boolean doesGroupHavePendingRequest(String documentId, String groupNamespaceCode, String groupName) throws WorkflowException {
        Group group = KEWServiceLocator.getIdentityHelperService().getGroupByName(groupNamespaceCode, groupName);
        assertNotNull("group not found for namespace '" + groupNamespaceCode + "' and name '" + groupName, group);
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(Long.valueOf(documentId));
        boolean foundRequest = false;
        for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
            if ( (actionRequest.isGroupRequest()) && (StringUtils.equals(group.getGroupId(),actionRequest.getGroupId())) ) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testRevokeAdHocRequestsByActionRequestId() throws Exception {
        SimpleDocumentActionsWebService simpleService = getSimpleDocumentActionsWebService();
        String docId = createAndRouteDocument(simpleService, "Doc1Title", "<foo>bar</foo>");
        // add adhoc at first node
        StandardResponse sr = simpleService.requestAdHocAckToPrincipal(docId, getPrincipalIdForName("admin"), getPrincipalIdForName("fran"), "");
        verifySuccess(sr);
        sr = simpleService.requestAdHocAckToPrincipal(docId, getPrincipalIdForName("admin"), getPrincipalIdForName("delyea"), "");
        verifySuccess(sr);
        // approve past first node
        sr = simpleService.approve(docId, getPrincipalIdForName("bmcgough"), "newDocTitle", "<foobar>test</foobar>", "");
        verifySuccess(sr);
        sr = simpleService.approve(docId, getPrincipalIdForName("rkirkend"), "newDocTitle", "<foobar>test</foobar>", "");
        verifySuccess(sr);
        // add adhoc at second node
        sr = simpleService.requestAdHocAckToPrincipal(docId, getPrincipalIdForName("admin"), getPrincipalIdForName("fred"), "");
        verifySuccess(sr);
        // check to make sure the adhoc requests remaining are correct
        assertNumberOfPendingAdHocRequests(Long.valueOf(docId), 3);
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "delyea");
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "fred");
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "fran");
        // revoke a single request
        String newDocTitle = "newestDocTitle";
        String newDocContent = "<foobar>testerIsNew</foobar>";
        ActionItemDTO[] actionItems = KEWServiceLocator.getWorkflowUtilityService().getActionItemsForPrincipal(getPrincipalIdForName("delyea"));
        assertEquals("incorrect number of action items for user", 1, actionItems.length);
        sr = simpleService.revokeAdHocRequestsByActionRequestId(docId, getPrincipalIdForName("admin"), newDocTitle, newDocContent, actionItems[0].getActionRequestId().toString(), "");
        verifySuccess(sr);
        verifyDocumentDataChanges(simpleService, docId, newDocContent, newDocTitle);
        // check to make sure the adhoc requests remaining are correct
        assertNumberOfPendingAdHocRequests(Long.valueOf(docId), 2);
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "fred");
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "fran");
        TestUtilities.assertNotInActionList(getPrincipalIdForName("delyea"), Long.valueOf(docId));
        // revoke another request
        actionItems = KEWServiceLocator.getWorkflowUtilityService().getActionItemsForPrincipal(getPrincipalIdForName("fran"));
        assertEquals("incorrect number of action items for user", 1, actionItems.length);
        sr = simpleService.revokeAdHocRequestsByActionRequestId(docId, getPrincipalIdForName("admin"), newDocTitle, newDocContent, actionItems[0].getActionRequestId().toString(), "");
        verifySuccess(sr);
        // check to make sure the adhoc requests remaining are correct
        assertNumberOfPendingAdHocRequests(Long.valueOf(docId), 1);
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "fred");
        TestUtilities.assertNotInActionList(getPrincipalIdForName("delyea"), Long.valueOf(docId));
        TestUtilities.assertNotInActionList(getPrincipalIdForName("fran"), Long.valueOf(docId));
    }

    protected void verifySuperUser(SimpleDocumentActionsWebService simpleService, String docId, String principalName) throws Exception {
        WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName(principalName), Long.valueOf(docId));
        assertTrue(principalName + " should be a super user", doc.isSuperUser());
    }

    protected void verifyNotSuperUser(SimpleDocumentActionsWebService simpleService, String docId, String principalName) throws Exception {
        WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName(principalName), Long.valueOf(docId));
        assertFalse(principalName + " should not be a super user", doc.isSuperUser());
    }

    @Test
    public void testSuperUserApprove() throws Exception {
        SimpleDocumentActionsWebService simpleService = getSimpleDocumentActionsWebService();
        String docId = createAndRouteDocument(simpleService, "Doc1Title", "<foo>bar</foo>");
        TestUtilities.assertAtNode(new WorkflowDocument(getPrincipalIdForName("admin"), Long.valueOf(docId)), "WorkflowDocument");

        // attempt to take action as invalid super user
        String notSuperUserPrincipalName = "fred";
        verifyNotSuperUser(simpleService, docId, notSuperUserPrincipalName);
        StandardResponse sr = simpleService.superUserApprove(docId, getPrincipalIdForName(notSuperUserPrincipalName), null, null, "");
        if ( (sr.getErrorMessage() == null) || StringUtils.isBlank(sr.getErrorMessage()) ) {
            fail("document was successfully approved by invalid super user");
        }

        // take action as valid super user
        String superUserPrincipalName = "shenl";
        verifySuperUser(simpleService, docId, superUserPrincipalName);
        TestUtilities.assertAtNode(new WorkflowDocument(getPrincipalIdForName(superUserPrincipalName), Long.valueOf(docId)), "WorkflowDocument");
        // super user approve the document
        String newDocTitle = "newestDocTitle";
        String newDocContent = "<foobar>testerIsNew</foobar>";
        sr = simpleService.superUserApprove(docId, getPrincipalIdForName(superUserPrincipalName), newDocTitle, newDocContent, "");
        verifySuccess(sr);
        verifyDocumentDataChanges(simpleService, docId, newDocContent, newDocTitle);
        // wait 2 minutes
        WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName(superUserPrincipalName), Long.valueOf(docId));
        assertTrue("state of the document should be final", doc.stateIsProcessed());
    }

    @Test
    public void testSuperUserDisapprove() throws Exception {
        SimpleDocumentActionsWebService simpleService = getSimpleDocumentActionsWebService();
        String docId = createAndRouteDocument(simpleService, "Doc1Title", "<foo>bar</foo>");
        TestUtilities.assertAtNode(new WorkflowDocument(getPrincipalIdForName("admin"), Long.valueOf(docId)), "WorkflowDocument");

        // attempt to take action as invalid super user
        String notSuperUserPrincipalName = "fred";
        verifyNotSuperUser(simpleService, docId, notSuperUserPrincipalName);
        StandardResponse sr = simpleService.superUserDisapprove(docId, getPrincipalIdForName(notSuperUserPrincipalName), null, null, "");
        if ( (sr.getErrorMessage() == null) || StringUtils.isBlank(sr.getErrorMessage()) ) {
            fail("document was successfully disapproved by invalid super user");
        }

        // take action as valid super user
        String superUserPrincipalName = "shenl";
        verifySuperUser(simpleService, docId, superUserPrincipalName);
        TestUtilities.assertAtNode(new WorkflowDocument(getPrincipalIdForName(superUserPrincipalName), Long.valueOf(docId)), "WorkflowDocument");
        // super user approve the document
        String newDocTitle = "newestDocTitle";
        String newDocContent = "<foobar>testerIsNew</foobar>";
        sr = simpleService.superUserDisapprove(docId, getPrincipalIdForName(superUserPrincipalName), newDocTitle, newDocContent, "");
        verifySuccess(sr);
        verifyDocumentDataChanges(simpleService, docId, newDocContent, newDocTitle);
        WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName(superUserPrincipalName),Long.valueOf(docId));
        assertTrue("document should be disapproved", doc.stateIsDisapproved());
    }

    @Test
    public void testSuperUserCancel() throws Exception {
        SimpleDocumentActionsWebService simpleService = getSimpleDocumentActionsWebService();
        String docId = createAndRouteDocument(simpleService, "Doc1Title", "<foo>bar</foo>");
        TestUtilities.assertAtNode(new WorkflowDocument(getPrincipalIdForName("admin"), Long.valueOf(docId)), "WorkflowDocument");

        // attempt to take action as invalid super user
        String notSuperUserPrincipalName = "fred";
        verifyNotSuperUser(simpleService, docId, notSuperUserPrincipalName);
        StandardResponse sr = simpleService.superUserCancel(docId, getPrincipalIdForName(notSuperUserPrincipalName), null, null, "");
        if ( (sr.getErrorMessage() == null) || StringUtils.isBlank(sr.getErrorMessage()) ) {
            fail("document was successfully cancelled by invalid super user");
        }

        // take action as valid super user
        String superUserPrincipalName = "shenl";
        verifySuperUser(simpleService, docId, superUserPrincipalName);
        // super user approve the document
        String newDocTitle = "newestDocTitle";
        String newDocContent = "<foobar>testerIsNew</foobar>";
        sr = simpleService.superUserCancel(docId, getPrincipalIdForName(superUserPrincipalName), newDocTitle, newDocContent, "");
        verifySuccess(sr);
        verifyDocumentDataChanges(simpleService, docId, newDocContent, newDocTitle);
        WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName(superUserPrincipalName),Long.valueOf(docId));
        assertTrue("document should be cancelled", doc.stateIsCanceled());
    }

    @Test
    public void testReturnToPrevious() throws Exception {
        SimpleDocumentActionsWebService simpleService = getSimpleDocumentActionsWebService();
        String docId = createAndRouteDocument(simpleService, "Doc1Title", "<foo>bar</foo>");
        TestUtilities.assertAtNode(new WorkflowDocument(getPrincipalIdForName("admin"), Long.valueOf(docId)), "WorkflowDocument");
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "bmcgough");
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "rkirkend");
        // approve past first node
        StandardResponse sr = simpleService.approve(docId, getPrincipalIdForName("bmcgough"), "newDocTitle", "<foobar>test</foobar>", "");
        verifySuccess(sr);
        sr = simpleService.approve(docId, getPrincipalIdForName("rkirkend"), "newDocTitle", "<foobar>test</foobar>", "");
        verifySuccess(sr);
        // verify document is in correct locations
        TestUtilities.assertAtNode(new WorkflowDocument(getPrincipalIdForName("admin"), Long.valueOf(docId)), "WorkflowDocument2");
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "pmckown");
        TestUtilities.assertNotInActionList(getPrincipalIdForName("bmcgough"), Long.valueOf(docId));
        TestUtilities.assertNotInActionList(getPrincipalIdForName("rkirkend"), Long.valueOf(docId));

        // attempt to return document to previous with invalid user
        sr = simpleService.returnToPreviousNodeWithUpdates(docId, getPrincipalIdForName("delyea"), "", "WorkflowDocument", null, null);
        if ( (sr.getErrorMessage() == null) || StringUtils.isBlank(sr.getErrorMessage())) {
            fail("document was successfully returned to previous by invalid user");
        }

        // return the document to previous with the correct user
        String newDocTitle = "newestDocTitle";
        String newDocContent = "<foobar>testerIsNew</foobar>";
        sr = simpleService.returnToPreviousNodeWithUpdates(docId, getPrincipalIdForName("pmckown"), "", "WorkflowDocument", newDocTitle, newDocContent);
        verifySuccess(sr);
        verifyDocumentDataChanges(simpleService, docId, newDocContent, newDocTitle);
        // verify document is in correct locations
        TestUtilities.assertAtNode(new WorkflowDocument(getPrincipalIdForName("admin"), Long.valueOf(docId)), "WorkflowDocument");
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "bmcgough");
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "rkirkend");
        TestUtilities.assertNotInActionList(getPrincipalIdForName("pmckown"), Long.valueOf(docId));
    }

    @Test
    public void testSuperUserReturnToPrevious() throws Exception {
        SimpleDocumentActionsWebService simpleService = getSimpleDocumentActionsWebService();
        String docId = createAndRouteDocument(simpleService, "Doc1Title", "<foo>bar</foo>");
        TestUtilities.assertAtNode(new WorkflowDocument(getPrincipalIdForName("admin"), Long.valueOf(docId)), "WorkflowDocument");
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "bmcgough");
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "rkirkend");
        // approve past first node
        StandardResponse sr = simpleService.approve(docId, getPrincipalIdForName("bmcgough"), "newDocTitle", "<foobar>test</foobar>", "");
        verifySuccess(sr);
        sr = simpleService.approve(docId, getPrincipalIdForName("rkirkend"), "newDocTitle", "<foobar>test</foobar>", "");
        verifySuccess(sr);
        // verify document is in correct locations
        TestUtilities.assertAtNode(new WorkflowDocument(getPrincipalIdForName("admin"), Long.valueOf(docId)), "WorkflowDocument2");
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "pmckown");
        TestUtilities.assertNotInActionList(getPrincipalIdForName("bmcgough"), Long.valueOf(docId));
        TestUtilities.assertNotInActionList(getPrincipalIdForName("rkirkend"), Long.valueOf(docId));

        // attempt to return document to previous with invalid user
        sr = simpleService.superUserReturnToPrevious(docId, getPrincipalIdForName("delyea"), null, null, "WorkflowDocument", "");
        if ( (sr.getErrorMessage() == null) || StringUtils.isBlank(sr.getErrorMessage())) {
            fail("document was successfully returned to previous by invalid user");
        }

        // return the document to previous with the correct user
        String newDocTitle = "newestDocTitle";
        String newDocContent = "<foobar>testerIsNew</foobar>";
        String superUserPrincipalName = "shenl";
        verifySuperUser(simpleService, docId, superUserPrincipalName);
        sr = simpleService.superUserReturnToPrevious(docId, getPrincipalIdForName(superUserPrincipalName), newDocTitle, newDocContent, "WorkflowDocument", "");
        verifySuccess(sr);
        verifyDocumentDataChanges(simpleService, docId, newDocContent, newDocTitle);
        // verify document is in correct locations
        TestUtilities.assertAtNode(new WorkflowDocument(getPrincipalIdForName("admin"), Long.valueOf(docId)), "WorkflowDocument");
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "bmcgough");
        TestUtilities.assertUserHasPendingRequest(Long.valueOf(docId), "rkirkend");
    }

}
