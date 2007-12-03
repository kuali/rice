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
package edu.iu.uis.eden.server;


import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.applicationconstants.ApplicationConstant;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.DocumentDetailVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.ReportActionToTakeVO;
import edu.iu.uis.eden.clientapp.vo.ReportCriteriaVO;
import edu.iu.uis.eden.clientapp.vo.RuleExtensionVO;
import edu.iu.uis.eden.clientapp.vo.RuleReportCriteriaVO;
import edu.iu.uis.eden.clientapp.vo.RuleResponsibilityVO;
import edu.iu.uis.eden.clientapp.vo.RuleVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupNameIdVO;
import edu.iu.uis.eden.test.TestUtilities;
import edu.iu.uis.eden.util.Utilities;

public class WorkflowUtilityTest extends KEWTestCase {

    private WorkflowUtility utility;

    protected void loadTestData() throws Exception {
        loadXmlFile("WorkflowUtilityConfig.xml");
    }

    protected void setUpTransaction() throws Exception {
        super.setUpTransaction();
        utility = KEWServiceLocator.getWorkflowUtilityService();
    }

    @Test public void testIsUserInRouteLog() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), SeqSetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        assertTrue(document.stateIsEnroute());
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("ewestfal"), false));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("bmcgough"), false));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("rkirkend"), false));
        assertFalse("User should not be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("pmckown"), false));
        assertFalse("User should not be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("temay"), false));
        assertFalse("User should not be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("jhopf"), false));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("pmckown"), true));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("temay"), true));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("jhopf"), true));

        // test that we can run isUserInRouteLog on a SAVED document
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), SeqSetup.DOCUMENT_TYPE_NAME);
        document.saveDocument("");
        assertTrue(document.stateIsSaved());
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("ewestfal"), false));
        assertFalse("User should not be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("bmcgough"), false));
        assertFalse("User should not be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("rkirkend"), false));
        assertFalse("User should not be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("pmckown"), false));
        assertFalse("User should not be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("temay"), false));
        assertFalse("User should not be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("jhopf"), false));

        // now look all up in the future of this saved document
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("bmcgough"), true));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("rkirkend"), true));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("pmckown"), true));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("temay"), true));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("jhopf"), true));
    }

    @Test public void testIsUserInRouteLogAfterReturnToPrevious() throws Exception {
	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), SeqSetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        assertTrue(document.stateIsEnroute());

        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());

        // bmcgough and rkirkend should be in route log
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("bmcgough"), false));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("rkirkend"), false));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("bmcgough"), true));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("rkirkend"), true));
        assertFalse("User should NOT be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("pmckown"), false));
        // Phil of the future
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("pmckown"), true));
        TestUtilities.assertAtNode(document, "WorkflowDocument");

        document.returnToPreviousNode("", "AdHoc");
        TestUtilities.assertAtNode(document, "AdHoc");
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());

        document.approve("");

        // we should be back where we were
        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        TestUtilities.assertAtNode(document, "WorkflowDocument");

        // now verify that is route log authenticated still works
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("bmcgough"), false));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("rkirkend"), false));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("bmcgough"), true));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("rkirkend"), true));
        assertFalse("User should NOT be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("pmckown"), false));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("pmckown"), true));

        // let's look at the revoked node instances

        List revokedNodeInstances = KEWServiceLocator.getRouteNodeService().getRevokedNodeInstances(KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId()));
        assertNotNull(revokedNodeInstances);
        assertEquals(2, revokedNodeInstances.size());

        // let's approve past this node and another
        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        document.approve("");
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        document.approve("");

        // should be at WorkflowDocument2
        document = new WorkflowDocument(new NetworkIdVO("pmckown"), document.getRouteHeaderId());
        TestUtilities.assertAtNode(document, "WorkflowDocument2");
        assertTrue(document.isApprovalRequested());
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("bmcgough"), false));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("rkirkend"), false));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("bmcgough"), true));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("rkirkend"), true));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("pmckown"), false));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("pmckown"), true));

        // now return back to WorkflowDocument
        document.returnToPreviousNode("", "WorkflowDocument");
        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        // Phil should no longer be non-future route log authenticated
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("bmcgough"), false));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("rkirkend"), false));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("bmcgough"), true));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("rkirkend"), true));
        assertFalse("User should NOT be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("pmckown"), false));
        assertTrue("User should be authenticated.", utility.isUserInRouteLog(document.getRouteHeaderId(), new NetworkIdVO("pmckown"), true));


    }

    public abstract interface ReportCriteriaGenerator { public abstract ReportCriteriaVO buildCriteria(WorkflowDocument workflowDoc) throws Exception; }

    @Test public void testDocumentWillHaveApproveOrCompleteRequestAtNode_RouteHeaderId() throws Exception {
        ReportCriteriaGenerator generator = new ReportCriteriaGenerator() {
            public ReportCriteriaVO buildCriteria(WorkflowDocument workflowDoc) throws Exception {
                ReportCriteriaVO criteria = new ReportCriteriaVO(workflowDoc.getRouteHeaderId());
                return criteria;
            }
        };
        runDocumentWillHaveApproveOrCompleteRequestAtNode(generator);
    }

    @Test public void testDocumentWillHaveApproveOrCompleteRequestAtNode_XmlContent() throws Exception {
        ReportCriteriaGenerator generator = new ReportCriteriaGenerator() {
            public ReportCriteriaVO buildCriteria(WorkflowDocument workflowDoc) throws Exception {
                ReportCriteriaVO criteria = new ReportCriteriaVO(workflowDoc.getDocumentType());
                criteria.setXmlContent(workflowDoc.getDocumentContent().getApplicationContent());
                return criteria;
            }
        };
        runDocumentWillHaveApproveOrCompleteRequestAtNode(generator);
    }

    private void runDocumentWillHaveApproveOrCompleteRequestAtNode(ReportCriteriaGenerator generator) throws Exception {
      /*
        name="WorkflowDocument"
          -  bmcgough - Approve - false
          -  rkirkend - Approve - false
        name="WorkflowDocument2"
          -  pmckown - Approve - false
        name="Acknowledge1"
          -  temay - Ack - false
        name="Acknowledge2"
          -  jhopf - Ack - false
      */
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), SeqSetup.DOCUMENT_TYPE_NAME);

        ReportCriteriaVO reportCriteriaVO = generator.buildCriteria(document);
//        ReportCriteriaVO reportCriteriaVO = new ReportCriteriaVO(document.getRouteHeaderId());
        reportCriteriaVO.setTargetNodeName("WorkflowDocument2");
        reportCriteriaVO.setRoutingUser(new NetworkIdVO("bmcgough"));
        assertTrue("Document should have one unfulfilled approve/complete request",utility.documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, new String[]{EdenConstants.ACTION_REQUEST_APPROVE_REQ,EdenConstants.ACTION_REQUEST_COMPLETE_REQ}));
        reportCriteriaVO.setTargetUsers(new UserIdVO[]{new NetworkIdVO("bmcgough")});
        assertFalse("Document should not have any unfulfilled approve/complete requests",utility.documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, new String[]{EdenConstants.ACTION_REQUEST_APPROVE_REQ,EdenConstants.ACTION_REQUEST_COMPLETE_REQ}));

        reportCriteriaVO = new ReportCriteriaVO(document.getRouteHeaderId());
        reportCriteriaVO.setTargetNodeName("WorkflowDocument2");
        reportCriteriaVO.setRoutingUser(new NetworkIdVO("bmcgough"));
        ReportActionToTakeVO[] actionsToTake = new ReportActionToTakeVO[1];
        actionsToTake[0] = new ReportActionToTakeVO(EdenConstants.ACTION_TAKEN_APPROVED_CD,new NetworkIdVO("rkirkend"),"WorkflowDocument");
        reportCriteriaVO.setActionsToTake(actionsToTake);
        assertFalse("Document should not have any unfulfilled approve/complete requests",utility.documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, new String[]{EdenConstants.ACTION_REQUEST_APPROVE_REQ,EdenConstants.ACTION_REQUEST_COMPLETE_REQ}));

        reportCriteriaVO = new ReportCriteriaVO(document.getRouteHeaderId());
        reportCriteriaVO.setTargetNodeName("WorkflowDocument2");
        actionsToTake = new ReportActionToTakeVO[2];
        actionsToTake[0] = new ReportActionToTakeVO(EdenConstants.ACTION_TAKEN_APPROVED_CD,new NetworkIdVO("bmcgough"),"WorkflowDocument");
        actionsToTake[1] = new ReportActionToTakeVO(EdenConstants.ACTION_TAKEN_APPROVED_CD,new NetworkIdVO("rkirkend"),"WorkflowDocument");
        reportCriteriaVO.setActionsToTake(actionsToTake);
        reportCriteriaVO.setRoutingUser(new NetworkIdVO("pmckown"));
        assertFalse("Document should not have any unfulfilled approve/complete requests",utility.documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, new String[]{EdenConstants.ACTION_REQUEST_APPROVE_REQ,EdenConstants.ACTION_REQUEST_COMPLETE_REQ}));

        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), SeqSetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        assertTrue(document.stateIsEnroute());

        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        document.approve("");

        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        document.approve("");

        reportCriteriaVO = new ReportCriteriaVO(document.getRouteHeaderId());
        reportCriteriaVO.setTargetNodeName("WorkflowDocument2");
        assertTrue("Document should have one unfulfilled approve/complete request",utility.documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, new String[]{EdenConstants.ACTION_REQUEST_APPROVE_REQ,EdenConstants.ACTION_REQUEST_COMPLETE_REQ}));

        document = new WorkflowDocument(new NetworkIdVO("pmckown"), document.getRouteHeaderId());
        document.approve("");
        assertTrue(document.stateIsProcessed());

        reportCriteriaVO = new ReportCriteriaVO(document.getRouteHeaderId());
        reportCriteriaVO.setTargetNodeName("Acknowledge1");
        assertFalse("Document should not have any unfulfilled approve/complete requests when in processed status",utility.documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, new String[]{EdenConstants.ACTION_REQUEST_APPROVE_REQ,EdenConstants.ACTION_REQUEST_COMPLETE_REQ}));

        reportCriteriaVO = new ReportCriteriaVO(document.getRouteHeaderId());
        reportCriteriaVO.setTargetNodeName("Acknowledge1");
        assertTrue("Document should have one unfulfilled Ack request when in final status",utility.documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, new String[]{EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ}));

        document = new WorkflowDocument(new NetworkIdVO("temay"), document.getRouteHeaderId());
        document.acknowledge("");
        assertTrue(document.stateIsProcessed());

        reportCriteriaVO = new ReportCriteriaVO(document.getRouteHeaderId());
        reportCriteriaVO.setTargetNodeName("Acknowledge1");
        assertFalse("Document should not have any unfulfilled Ack requests as node 'Acknowledge1' at this time",utility.documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, new String[]{EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ}));

        document = new WorkflowDocument(new NetworkIdVO("jhopf"), document.getRouteHeaderId());
        document.acknowledge("");

        reportCriteriaVO = new ReportCriteriaVO(document.getRouteHeaderId());
        reportCriteriaVO.setTargetNodeName("Acknowledge2");
        assertFalse("Document should not have any unfulfilled approve/complete requests when in final status",utility.documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, new String[]{EdenConstants.ACTION_REQUEST_APPROVE_REQ,EdenConstants.ACTION_REQUEST_COMPLETE_REQ}));

        reportCriteriaVO = new ReportCriteriaVO(document.getRouteHeaderId());
        reportCriteriaVO.setTargetNodeName("Acknowledge2");
        assertFalse("Document should not have any unfulfilled FYI requests when in final status",utility.documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, new String[]{EdenConstants.ACTION_REQUEST_FYI_REQ}));

    }

    @Test public void testIsLastApprover() throws Exception {
        // test the is last approver in route level against our sequential document type
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), SeqSetup.DOCUMENT_TYPE_NAME);
        document.saveRoutingData();

        // the initial "route level" should have no requests initially so it should return false
        assertFalse("Should not be last approver.", utility.isLastApproverInRouteLevel(document.getRouteHeaderId(), new NetworkIdVO("ewestfal"), new Integer(0)));
        assertFalse("Should not be last approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("ewestfal"), SeqSetup.ADHOC_NODE));

        // app specific route a request to a workgroup at the initial node (TestWorkgroup)
        document.appSpecificRouteDocumentToWorkgroup(EdenConstants.ACTION_REQUEST_APPROVE_REQ, "AdHoc", "", new WorkgroupNameIdVO("TestWorkgroup"), "", false);
        assertTrue("Should be last approver.", utility.isLastApproverInRouteLevel(document.getRouteHeaderId(), new NetworkIdVO("ewestfal"), new Integer(0)));
        assertTrue("Should be last approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("ewestfal"), SeqSetup.ADHOC_NODE));

        // app specific route a request to a member of the workgroup (jitrue)
        document.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_APPROVE_REQ, "AdHoc", "", new NetworkIdVO("jitrue"), "", false);
        // member of the workgroup with the user request should be last approver
        assertTrue("Should be last approver.", utility.isLastApproverInRouteLevel(document.getRouteHeaderId(), new NetworkIdVO("jitrue"), new Integer(0)));
        assertTrue("Should be last approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("jitrue"), SeqSetup.ADHOC_NODE));
        // other members of the workgroup will not be last approvers because they don't satisfy the individuals request (ewestfal)
        assertFalse("Should not be last approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("ewestfal"), SeqSetup.ADHOC_NODE));

        // route the document, should stay at the adhoc node until those requests have been completed
        document.routeDocument("");
        document = new WorkflowDocument(new NetworkIdVO("jitrue"), document.getRouteHeaderId());
        assertEquals("Document should be at adhoc node.", SeqSetup.ADHOC_NODE, document.getNodeNames()[0]);
        assertTrue("Approve should be requested.", document.isApprovalRequested());
        document.approve("");

        // document should now be at the WorkflowDocument node with a request to bmcgough and rkirkend
        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        assertTrue("Approve should be requested.", document.isApprovalRequested());
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertTrue("Approve should be requested.", document.isApprovalRequested());
        // since there are two requests, neither should be last approver
        assertFalse("Should not be last approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("bmcgough"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
        assertFalse("Should not be last approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("rkirkend"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
        document.approve("");

        // request to rirkend has been satisfied, now request to bmcgough is only request remaining at level so he should be last approver
        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        assertTrue("Approve should be requested.", document.isApprovalRequested());
        assertTrue("Should be last approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("bmcgough"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
        document.approve("");

    }

    /**
     * This method tests how the isLastApproverAtNode method deals with ignore previous requests, there is an app constant
     * with the value specified in EdenConstants.IS_LAST_APPROVER_ACTIVATE_FIRST which dictates whether or not to simulate
     * activation of initialized requests before running the method.
     *
     * Tests the fix to issue http://fms.dfa.cornell.edu:8080/browse/KULWF-366
     */
    @Test public void testIsLastApproverActivation() throws Exception {
        // first test without the constant set
        ApplicationConstant appConstant = KEWServiceLocator.getApplicationConstantsService().findByName(EdenConstants.IS_LAST_APPROVER_ACTIVATE_FIRST);
        assertNull("The IS_LAST_APPROVER_ACTIVATE_FIRST constant should not be set.", appConstant);
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), SeqSetup.LAST_APPROVER_DOCUMENT_TYPE_NAME);
        document.routeDocument("");

        // at the first node (WorkflowDocument) we should have a request to rkirkend, bmcgough and to ewestfal with ignorePrevious=true,
        assertEquals("We should be at the WorkflowDocument node.", SeqSetup.WORKFLOW_DOCUMENT_NODE, document.getNodeNames()[0]);
        assertFalse("ewestfal should have not have approve because it's initiated", document.isApprovalRequested());
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertFalse("rkirkend should not have approve because it's initiated", document.isApprovalRequested());
        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        assertTrue("bmcgough should have approve", document.isApprovalRequested());
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertEquals("Should be 3 pending requests.", 3, actionRequests.size());
        // the requests to bmcgough should be activated, the request to rkirkend should be initialized,
        // and the request to ewestfal should be initialized and ignorePrevious=true
        boolean foundBmcgoughRequest = false;
        boolean foundRkirkendRequest = false;
        boolean foundEwestfalRequest = false;
        for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
            String netId = actionRequest.getWorkflowUser().getAuthenticationUserId().getId();
            if ("bmcgough".equals(netId)) {
                assertTrue("Request to bmcgough should be activated.", actionRequest.isActive());
                foundBmcgoughRequest = true;
            } else if ("rkirkend".equals(netId)) {
                assertTrue("Request to rkirkend should be initialized.", actionRequest.isInitialized());
                foundRkirkendRequest = true;
            } else if ("ewestfal".equals(netId)) {
                assertTrue("Request to ewestfal should be initialized.", actionRequest.isInitialized());
                assertTrue("Request to ewestfal should be ignorePrevious.", actionRequest.getIgnorePrevAction().booleanValue());
                foundEwestfalRequest = true;
            }
        }
        assertTrue("Did not find request to bmcgough.", foundBmcgoughRequest);
        assertTrue("Did not find request to rkirkend.", foundRkirkendRequest);
        assertTrue("Did not find request to ewestfal.", foundEwestfalRequest);

        // at this point, neither bmcgough, rkirkend nor ewestfal should be the last approver
        assertFalse("Bmcgough should not be the final approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("bmcgough"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
        assertFalse("Rkirkend should not be the final approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("rkirkend"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
        assertFalse("Ewestfal should not be the final approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("ewestfal"), SeqSetup.WORKFLOW_DOCUMENT_NODE));

        // approve as bmcgough
        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        document.approve("");

        // still, neither rkirkend nor ewestfal should be "final approver"
        // at this point, neither bmcgough, rkirkend nor ewestfal should be the last approver
        assertFalse("Rkirkend should not be the final approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("rkirkend"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
        assertFalse("Ewestfal should not be the final approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("ewestfal"), SeqSetup.WORKFLOW_DOCUMENT_NODE));

        // approve as rkirkend
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        document.approve("");

        // should be one pending activated to ewestfal now
        actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertEquals("Should be 1 pending requests.", 1, actionRequests.size());
        ActionRequestValue actionRequest = (ActionRequestValue)actionRequests.get(0);
        assertTrue("Should be activated.", actionRequest.isActive());

        // ewestfal should now be the final approver
        assertTrue("Ewestfal should be the final approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("ewestfal"), SeqSetup.WORKFLOW_DOCUMENT_NODE));

        // approve as ewestfal to send to next node
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
        assertTrue("ewestfal should have approve request", document.isApprovalRequested());
        document.approve("");

        // should be at the workflow document 2 node
        assertEquals("Should be at the WorkflowDocument2 Node.", SeqSetup.WORKFLOW_DOCUMENT_2_NODE, document.getNodeNames()[0]);
        // at this node there should be two requests, one to ewestfal with ignorePrevious=false and one to pmckown,
        // since we haven't set the application constant, the non-ignore previous request won't be activated first so pmckown
        // will not be the final approver
        assertFalse("Pmckown should not be the final approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("pmckown"), SeqSetup.WORKFLOW_DOCUMENT_2_NODE));
        assertFalse("Ewestfal should not be the final approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("ewestfal"), SeqSetup.WORKFLOW_DOCUMENT_2_NODE));
        actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertEquals("Should be 2 action requests.", 2, actionRequests.size());

        // Now set up the app constant that checks ignore previous properly and try a new document
        appConstant = new ApplicationConstant();
        appConstant.setApplicationConstantName(EdenConstants.IS_LAST_APPROVER_ACTIVATE_FIRST);
        appConstant.setApplicationConstantValue("true");
        // TODO FIX this when we figure out what's up with the flusing problem in the tests
        KEWServiceLocator.getApplicationConstantsService().save(appConstant);

        appConstant = KEWServiceLocator.getApplicationConstantsService().findByName(EdenConstants.IS_LAST_APPROVER_ACTIVATE_FIRST);
        assertNotNull("AppConstant should not be null.", appConstant);
        assertEquals("AppConstant should be true.", "true", appConstant.getApplicationConstantValue());
        // check Utilities class
        String appConstantValue = Utilities.getApplicationConstant(EdenConstants.IS_LAST_APPROVER_ACTIVATE_FIRST);
        assertEquals("AppConstant should be true.", "true", appConstantValue);

        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), SeqSetup.LAST_APPROVER_DOCUMENT_TYPE_NAME);
        document.routeDocument("");

        // at this point, neither bmcgough, rkirkend nor ewestfal should be the last approver
        assertFalse("Bmcgough should not be the final approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("bmcgough"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
        assertFalse("Rkirkend should not be the final approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("rkirkend"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
        assertFalse("Ewestfal should not be the final approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("ewestfal"), SeqSetup.WORKFLOW_DOCUMENT_NODE));

        // approve as bmcgough
        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        document.approve("");

        // now there is just a request to rkirkend and ewestfal, since ewestfal is ignore previous true, neither should be final approver
        assertFalse("Rkirkend should not be the final approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("rkirkend"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
        assertFalse("Ewestfal should not be the final approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("ewestfal"), SeqSetup.WORKFLOW_DOCUMENT_NODE));

        // approve as ewestfal
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
        document.approve("");

        // rkirkend should now be the final approver
        assertTrue("Rkirkend should now be the final approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("rkirkend"), SeqSetup.WORKFLOW_DOCUMENT_NODE));

        // approve as rkirkend to send it to the next node
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        document.approve("");

        // now, there are requests to pmckown and ewestfal here, the request to ewestfal is ingorePrevious=false and since ewestfal
        // routed the document, this request should be auto-approved.  However, it's priority is 2 so it is activated after the
        // request to pmckown which is the situation we are testing
        assertTrue("Pmckown should be the final approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("pmckown"), SeqSetup.WORKFLOW_DOCUMENT_2_NODE));
        assertFalse("Ewestfal should not be the final approver.", utility.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("ewestfal"), SeqSetup.WORKFLOW_DOCUMENT_2_NODE));

        // if we approve as pmckown, the document should go into acknowledgement and become processed
        document = new WorkflowDocument(new NetworkIdVO("pmckown"), document.getRouteHeaderId());
        document.approve("");
        assertTrue("Document should be processed.", document.stateIsProcessed());

    }

    @Test public void testIsFinalApprover() throws Exception {
        // for this document, pmckown should be the final approver
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), SeqSetup.DOCUMENT_TYPE_NAME);
        assertFinalApprover(document);
    }

    @Test public void testIsFinalApproverChild() throws Exception {
        // 12-13-2005: HR ran into a bug where this method was not correctly locating the final approver node when using a document type whic
        // inherits the route from a parent, so we will incorporate this into the unit test to prevent regression
        WorkflowDocument childDocument = new WorkflowDocument(new NetworkIdVO("ewestfal"), SeqSetup.CHILD_DOCUMENT_TYPE_NAME);
        assertFinalApprover(childDocument);
    }

    /**
     * Factored out so as not to duplicate a bunch of code between testIsFinalApprover and testIsFinalApproverChild.
     */
    private void assertFinalApprover(WorkflowDocument document) throws Exception {
        document.routeDocument("");

        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        assertTrue("Document should be enroute.", document.stateIsEnroute());
        assertTrue("Should have approve request.", document.isApprovalRequested());

        // bmcgough is not the final approver
        assertFalse("Should not be final approver.", utility.isFinalApprover(document.getRouteHeaderId(), new NetworkIdVO("bmcgough")));
        // approve as bmcgough
        document.approve("");

        // should be to Ryan now, who is also not the final approver on the document
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertTrue("Document should be enroute.", document.stateIsEnroute());
        assertTrue("Should have approve request.", document.isApprovalRequested());
        assertFalse("Should not be final approver.", utility.isFinalApprover(document.getRouteHeaderId(), new NetworkIdVO("rkirkend")));
        document.approve("");

        // should be to Phil now, who *IS* the final approver on the document
        document = new WorkflowDocument(new NetworkIdVO("pmckown"), document.getRouteHeaderId());
        assertTrue("Document should be enroute.", document.stateIsEnroute());
        assertTrue("Should have approve request.", document.isApprovalRequested());
        assertTrue("Should be final approver.", utility.isFinalApprover(document.getRouteHeaderId(), new NetworkIdVO("pmckown")));

        // now adhoc an approve to temay, phil should no longer be the final approver
        document.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_APPROVE_REQ, SeqSetup.WORKFLOW_DOCUMENT_2_NODE,
                "", new NetworkIdVO("temay"), "", true);
        assertFalse("Should not be final approver.", utility.isFinalApprover(document.getRouteHeaderId(), new NetworkIdVO("pmckown")));
        assertFalse("Should not be final approver.", utility.isFinalApprover(document.getRouteHeaderId(), new NetworkIdVO("temay")));

        // now approve as temay and then adhoc an ack to jeremy
        document = new WorkflowDocument(new NetworkIdVO("temay"), document.getRouteHeaderId());
        assertTrue("SHould have approve request.", document.isApprovalRequested());
        document.approve("");

        // phil should be final approver again
        assertTrue("Should be final approver.", utility.isFinalApprover(document.getRouteHeaderId(), new NetworkIdVO("pmckown")));
        document.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, SeqSetup.WORKFLOW_DOCUMENT_2_NODE,
                "", new NetworkIdVO("jhopf"), "", true);
        document = new WorkflowDocument(new NetworkIdVO("jhopf"), document.getRouteHeaderId());
        assertTrue("Should have acknowledge request.", document.isAcknowledgeRequested());

        // now there should be an approve to phil and an ack to jeremy, so phil should be the final approver and jeremy should not
        assertTrue("Should be final approver.", utility.isFinalApprover(document.getRouteHeaderId(), new NetworkIdVO("pmckown")));
        assertFalse("Should not be final approver.", utility.isFinalApprover(document.getRouteHeaderId(), new NetworkIdVO("jhopf")));

        // after approving as phil, the document should go processed
        document = new WorkflowDocument(new NetworkIdVO("pmckown"), document.getRouteHeaderId());
        document.approve("");
        assertTrue("Document should be processed.", document.stateIsProcessed());
    }

    @Test public void testRoutingReportOnDocumentType() throws Exception {
    	WorkflowInfo info = new WorkflowInfo();
    	ReportCriteriaVO criteria = new ReportCriteriaVO("SeqDocType");
    	criteria.setRuleTemplateNames(new String[] { "WorkflowDocumentTemplate" });
    	DocumentDetailVO documentDetail = info.routingReport(criteria);
    	assertNotNull(documentDetail);
    	assertEquals("Should have been 2 requests generated.", 2, documentDetail.getActionRequests().length);

    	// let's try doing both WorkflowDocumentTemplate and WorkflowDocumentTemplate2 together
    	criteria.setRuleTemplateNames(new String[] { "WorkflowDocumentTemplate", "WorkflowDocument2Template" });
    	documentDetail = info.routingReport(criteria);
    	assertEquals("Should have been 3 requests generated.", 3, documentDetail.getActionRequests().length);

    	boolean foundRkirkend = false;
    	boolean foundBmcgough = false;
    	boolean foundPmckown = false;
    	for (int index = 0; index < documentDetail.getActionRequests().length; index++) {
			ActionRequestVO actionRequest = documentDetail.getActionRequests()[index];
			String netId = actionRequest.getUserVO().getNetworkId();
			if (netId.equals("rkirkend")) {
				foundRkirkend = true;
				assertEquals(SeqSetup.WORKFLOW_DOCUMENT_NODE, actionRequest.getNodeName());
			} else if (netId.equals("bmcgough")) {
				foundBmcgough = true;
				assertEquals(SeqSetup.WORKFLOW_DOCUMENT_NODE, actionRequest.getNodeName());
			} else if (netId.equals("pmckown")) {
				foundPmckown = true;
				assertEquals(SeqSetup.WORKFLOW_DOCUMENT_2_NODE, actionRequest.getNodeName());
			}
		}
    	assertTrue("Did not find request for rkirkend", foundRkirkend);
    	assertTrue("Did not find request for bmcgough", foundBmcgough);
    	assertTrue("Did not find request for pmckown", foundPmckown);

    }

    @Test public void testRuleReportGeneralFunction() throws Exception {
        WorkflowInfo info = new WorkflowInfo();

        RuleReportCriteriaVO ruleReportCriteria = null;
        this.ruleExceptionTest(info, ruleReportCriteria, "Sending in null RuleReportCriteriaVO should throw Exception");

        ruleReportCriteria = new RuleReportCriteriaVO();
        this.ruleExceptionTest(info, ruleReportCriteria, "Sending in empty RuleReportCriteriaVO should throw Exception");

        ruleReportCriteria = new RuleReportCriteriaVO();
        ruleReportCriteria.setResponsibleUser(new NetworkIdVO("hobo_man"));
        this.ruleExceptionTest(info, ruleReportCriteria, "Sending in an invalid User Network ID should throw Exception");

        ruleReportCriteria = new RuleReportCriteriaVO();
        ruleReportCriteria.setResponsibleWorkgroup(new WorkgroupNameIdVO("hobos_united"));
        this.ruleExceptionTest(info, ruleReportCriteria, "Sending in an invalid Workgroup Name should throw Exception");

        ruleReportCriteria = new RuleReportCriteriaVO();
        RuleExtensionVO ruleExtensionVO = new RuleExtensionVO("key","value");
        ruleReportCriteria.setRuleExtensionVOs(new RuleExtensionVO[]{ruleExtensionVO});
        this.ruleExceptionTest(info, ruleReportCriteria, "Sending in one or more RuleExtentionVO objects with no Rule Template Name should throw Exception");

        RuleVO[] rules = null;
        ruleReportCriteria = new RuleReportCriteriaVO();
        ruleReportCriteria.setConsiderWorkgroupMembership(Boolean.FALSE);
        ruleReportCriteria.setDocumentTypeName(RuleTestGeneralSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        rules = info.ruleReport(ruleReportCriteria);
        assertEquals("Number of Rules Returned Should be 3",3,rules.length);

        rules = null;
        ruleReportCriteria = new RuleReportCriteriaVO();
        ruleReportCriteria.setActionRequestCodes(new String[]{EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ});
        ruleReportCriteria.setConsiderWorkgroupMembership(Boolean.FALSE);
        ruleReportCriteria.setDocumentTypeName(RuleTestGeneralSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        ruleReportCriteria.setResponsibleUser(new NetworkIdVO("temay"));
        rules = info.ruleReport(ruleReportCriteria);
        assertEquals("Number of Rules Returned Should be 0",0,rules.length);

        rules = null;
        ruleReportCriteria = new RuleReportCriteriaVO();
        ruleReportCriteria.setActionRequestCodes(new String[]{EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ});
        ruleReportCriteria.setConsiderWorkgroupMembership(Boolean.FALSE);
        ruleReportCriteria.setDocumentTypeName(RuleTestGeneralSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        rules = info.ruleReport(ruleReportCriteria);
        assertEquals("Number of Rules Returned Should be 1",1,rules.length);
        // check the rule returned
        RuleVO ruleVO = rules[0];
        assertEquals("Rule Document Type is not " + RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,ruleVO.getDocTypeName());
        assertEquals("Rule Template Named returned is not " + RuleTestGeneralSetup.RULE_TEST_TEMPLATE_2,RuleTestGeneralSetup.RULE_TEST_TEMPLATE_2,ruleVO.getRuleTemplateName());
        assertEquals("Rule did not have ignore previous set to false",Boolean.FALSE,ruleVO.getIgnorePrevious());
        assertEquals("Number of Rule Responsibilities returned is incorrect",2,ruleVO.getRuleResponsibilities().length);
        RuleResponsibilityVO responsibilityVO = null;
        for (int i = 0; i < ruleVO.getRuleResponsibilities().length; i++) {
            responsibilityVO = ruleVO.getRuleResponsibilities()[i];
            if ("temay".equals(responsibilityVO.getUser().getNetworkId())) {
                assertEquals("Rule user is not correct","temay",responsibilityVO.getUser().getNetworkId());
                assertEquals("Rule priority is incorrect",Integer.valueOf(1),responsibilityVO.getPriority());
                assertEquals("Rule should be Ack Request",EdenConstants.ACTION_REQUEST_APPROVE_REQ,responsibilityVO.getActionRequestedCd());
            } else if ("ewestfal".equals(responsibilityVO.getUser().getNetworkId())) {
                assertEquals("Rule user is not correct","ewestfal",responsibilityVO.getUser().getNetworkId());
                assertEquals("Rule priority is incorrect",Integer.valueOf(2),responsibilityVO.getPriority());
                assertEquals("Rule should be Ack Request",EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ,responsibilityVO.getActionRequestedCd());
            } else {
                fail("Network ID of user for this responsibility is neither temay or ewestfal");
            }
        }

        rules = null;
        ruleVO = null;
        responsibilityVO = null;
        ruleReportCriteria = new RuleReportCriteriaVO();
        ruleReportCriteria.setConsiderWorkgroupMembership(Boolean.FALSE);
        ruleReportCriteria.setDocumentTypeName(RuleTestGeneralSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        ruleReportCriteria.setResponsibleUser(new NetworkIdVO("temay"));
        rules = info.ruleReport(ruleReportCriteria);
        assertEquals("Number of Rules returned is not correct",2,rules.length);
        for (int i = 0; i < rules.length; i++) {
            ruleVO = rules[i];
            if (RuleTestGeneralSetup.RULE_TEST_TEMPLATE_1.equals(ruleVO.getRuleTemplateName())) {
                assertEquals("Rule Document Type is not " + RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,ruleVO.getDocTypeName());
                assertEquals("Rule Template Named returned is not " + RuleTestGeneralSetup.RULE_TEST_TEMPLATE_1,RuleTestGeneralSetup.RULE_TEST_TEMPLATE_1,ruleVO.getRuleTemplateName());
                assertEquals("Rule did not have ignore previous set to true",Boolean.TRUE,ruleVO.getIgnorePrevious());
                assertEquals("Number of Rule Responsibilities Returned Should be 1",1,ruleVO.getRuleResponsibilities().length);
                responsibilityVO = ruleVO.getRuleResponsibilities()[0];
                assertEquals("Rule user is incorrect","temay",responsibilityVO.getUser().getNetworkId());
                assertEquals("Rule priority is incorrect",Integer.valueOf(3),responsibilityVO.getPriority());
                assertEquals("Rule action request is incorrect",EdenConstants.ACTION_REQUEST_APPROVE_REQ,responsibilityVO.getActionRequestedCd());
            } else if (RuleTestGeneralSetup.RULE_TEST_TEMPLATE_2.equals(ruleVO.getRuleTemplateName())) {
                assertEquals("Rule Document Type is not " + RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,ruleVO.getDocTypeName());
                assertEquals("Rule Template Named returned is not " + RuleTestGeneralSetup.RULE_TEST_TEMPLATE_2,RuleTestGeneralSetup.RULE_TEST_TEMPLATE_2,ruleVO.getRuleTemplateName());
                assertEquals("Rule did not have ignore previous set to false",Boolean.FALSE,ruleVO.getIgnorePrevious());
                assertEquals("Number of Rule Responsibilities returned is incorrect",2,ruleVO.getRuleResponsibilities().length);
                responsibilityVO = null;
                for (int l = 0; l < ruleVO.getRuleResponsibilities().length; l++) {
                    responsibilityVO = ruleVO.getRuleResponsibilities()[l];
                    if ("temay".equals(responsibilityVO.getUser().getNetworkId())) {
                        assertEquals("Rule user is not correct","temay",responsibilityVO.getUser().getNetworkId());
                        assertEquals("Rule priority is incorrect",Integer.valueOf(1),responsibilityVO.getPriority());
                        assertEquals("Rule should be Ack Request",EdenConstants.ACTION_REQUEST_APPROVE_REQ,responsibilityVO.getActionRequestedCd());
                    } else if ("ewestfal".equals(responsibilityVO.getUser().getNetworkId())) {
                        assertEquals("Rule user is not correct","ewestfal",responsibilityVO.getUser().getNetworkId());
                        assertEquals("Rule priority is incorrect",Integer.valueOf(2),responsibilityVO.getPriority());
                        assertEquals("Rule should be Ack Request",EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ,responsibilityVO.getActionRequestedCd());
                    } else {
                        fail("Network ID of user for this responsibility is neither temay or ewestfal");
                    }
                }
            } else {
                fail("Rule Template of returned rule is not of type " + RuleTestGeneralSetup.RULE_TEST_TEMPLATE_1 + " nor " + RuleTestGeneralSetup.RULE_TEST_TEMPLATE_2);
            }
        }

        rules = null;
        ruleVO = null;
        responsibilityVO = null;
        ruleReportCriteria = new RuleReportCriteriaVO();
        ruleReportCriteria.setDocumentTypeName(RuleTestGeneralSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        ruleReportCriteria.setResponsibleWorkgroup(new WorkgroupNameIdVO(RuleTestGeneralSetup.RULE_TEST_WORKGROUP));
        rules = info.ruleReport(ruleReportCriteria);
        assertEquals("Number of Rules Returned Should be 1",1,rules.length);
        ruleVO = rules[0];
        assertEquals("Rule Document Type is not " + RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,ruleVO.getDocTypeName());
        assertEquals("Rule Template Named returned is not " + RuleTestGeneralSetup.RULE_TEST_TEMPLATE_3,RuleTestGeneralSetup.RULE_TEST_TEMPLATE_3,ruleVO.getRuleTemplateName());
        assertEquals("Rule did not have ignore previous set to true",Boolean.TRUE,ruleVO.getIgnorePrevious());
        assertEquals("Number of Rule Responsibilities Returned Should be 1",1,ruleVO.getRuleResponsibilities().length);
        responsibilityVO = ruleVO.getRuleResponsibilities()[0];
        assertEquals("Rule workgroup name is incorrect",RuleTestGeneralSetup.RULE_TEST_WORKGROUP,responsibilityVO.getWorkgroup().getWorkgroupName());
        assertEquals("Rule priority is incorrect",Integer.valueOf(1),responsibilityVO.getPriority());
        assertEquals("Rule action request is incorrect",EdenConstants.ACTION_REQUEST_FYI_REQ,responsibilityVO.getActionRequestedCd());

        rules = null;
        ruleVO = null;
        responsibilityVO = null;
        ruleReportCriteria = new RuleReportCriteriaVO();
        ruleReportCriteria.setDocumentTypeName(RuleTestGeneralSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        ruleReportCriteria.setResponsibleUser(new NetworkIdVO("user1"));
        rules = info.ruleReport(ruleReportCriteria);
        assertEquals("Number of Rules Returned Should be 1",1,rules.length);
        ruleVO = rules[0];
        assertEquals("Rule Document Type is not " + RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,RuleTestGeneralSetup.DOCUMENT_TYPE_NAME,ruleVO.getDocTypeName());
        assertEquals("Rule Template Named returned is not " + RuleTestGeneralSetup.RULE_TEST_TEMPLATE_3,RuleTestGeneralSetup.RULE_TEST_TEMPLATE_3,ruleVO.getRuleTemplateName());
        assertEquals("Rule did not have ignore previous set to true",Boolean.TRUE,ruleVO.getIgnorePrevious());
        assertEquals("Number of Rule Responsibilities Returned Should be 1",1,ruleVO.getRuleResponsibilities().length);
        responsibilityVO = ruleVO.getRuleResponsibilities()[0];
        assertEquals("Rule workgroup name is incorrect",RuleTestGeneralSetup.RULE_TEST_WORKGROUP,responsibilityVO.getWorkgroup().getWorkgroupName());
        assertEquals("Rule priority is incorrect",Integer.valueOf(1),responsibilityVO.getPriority());
        assertEquals("Rule action request is incorrect",EdenConstants.ACTION_REQUEST_FYI_REQ,responsibilityVO.getActionRequestedCd());
    }

    /**
     * Tests specific rule scenario relating to standard org review routing
     *
     * @throws Exception
     */
    @Test public void testRuleReportOrgReviewTest() throws Exception {
        loadXmlFile("WorkflowUtilityRuleReportConfig.xml");
        WorkflowInfo info = new WorkflowInfo();
        RuleReportCriteriaVO ruleReportCriteria = new RuleReportCriteriaVO();
        ruleReportCriteria.setDocumentTypeName(RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setRuleTemplateName(RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE);
        ruleReportCriteria.setResponsibleUser(new NetworkIdVO("user1"));
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        RuleVO[] rules = info.ruleReport(ruleReportCriteria);
        assertEquals("Number of rules returned is incorrect",2,rules.length);

        ruleReportCriteria = null;
        rules = null;
        ruleReportCriteria = new RuleReportCriteriaVO();
        ruleReportCriteria.setDocumentTypeName(RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setRuleTemplateName(RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE);
        ruleReportCriteria.setResponsibleUser(new NetworkIdVO("user1"));
        ruleReportCriteria.setConsiderWorkgroupMembership(Boolean.FALSE);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        rules = info.ruleReport(ruleReportCriteria);
        assertEquals("Number of rules returned is incorrect",1,rules.length);

        ruleReportCriteria = null;
        rules = null;
        ruleReportCriteria = new RuleReportCriteriaVO();
        ruleReportCriteria.setDocumentTypeName(RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setRuleTemplateName(RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE);
        RuleExtensionVO ruleExtensionVO = new RuleExtensionVO(RuleTestOrgReviewSetup.RULE_TEST_CHART_CODE_NAME,"BA");
        RuleExtensionVO ruleExtensionVO2 = new RuleExtensionVO(RuleTestOrgReviewSetup.RULE_TEST_ORG_CODE_NAME,"FMOP");
        RuleExtensionVO[] ruleExtensionVOs = new RuleExtensionVO[] {ruleExtensionVO,ruleExtensionVO2};
        ruleReportCriteria.setRuleExtensionVOs(ruleExtensionVOs);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        rules = info.ruleReport(ruleReportCriteria);
        assertEquals("Number of rules returned is incorrect",2,rules.length);

        ruleReportCriteria = null;
        rules = null;
        ruleExtensionVO = null;
        ruleExtensionVO2 = null;
        ruleExtensionVOs = null;
        ruleReportCriteria = new RuleReportCriteriaVO();
        ruleReportCriteria.setDocumentTypeName(RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setRuleTemplateName(RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE);
        ruleReportCriteria.setResponsibleUser(new NetworkIdVO("ewestfal"));
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        rules = info.ruleReport(ruleReportCriteria);
        assertEquals("Number of rules returned is incorrect",1,rules.length);
        RuleVO ruleVO = rules[0];
        assertEquals("Rule Document Type is not " + RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME,RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME,ruleVO.getDocTypeName());
        assertEquals("Rule Template Named returned is not " + RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE,RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE,ruleVO.getRuleTemplateName());
        assertEquals("Rule did not have ignore previous set to true",Boolean.TRUE,ruleVO.getIgnorePrevious());
        assertEquals("Number of Rule Responsibilities Returned Should be 1",1,ruleVO.getRuleResponsibilities().length);
        RuleResponsibilityVO responsibilityVO = ruleVO.getRuleResponsibilities()[0];
        assertEquals("Rule workgroup name is incorrect",RuleTestOrgReviewSetup.RULE_TEST_WORKGROUP2,responsibilityVO.getWorkgroup().getWorkgroupName());
        assertEquals("Rule priority is incorrect",Integer.valueOf(4),responsibilityVO.getPriority());
        assertEquals("Rule action request is incorrect",EdenConstants.ACTION_REQUEST_FYI_REQ,responsibilityVO.getActionRequestedCd());
        ruleExtensionVOs = ruleVO.getRuleExtensions();
        assertEquals("Number of Rule Extensions Returned Should be 2",2,ruleExtensionVOs.length);
        for (int i = 0; i < ruleExtensionVOs.length; i++) {
            RuleExtensionVO extensionVO = ruleExtensionVOs[i];
            // if rule key is chartCode.... should equal UA
            // else if rule key is orgCode.... should equal VPIT
            // otherwise error
            if (RuleTestOrgReviewSetup.RULE_TEST_CHART_CODE_NAME.equals(extensionVO.getKey())) {
                assertEquals("Rule Extension for key '" + RuleTestOrgReviewSetup.RULE_TEST_CHART_CODE_NAME + "' is incorrect","UA",extensionVO.getValue());
            } else if (RuleTestOrgReviewSetup.RULE_TEST_ORG_CODE_NAME.equals(extensionVO.getKey())) {
                assertEquals("Rule Extension for key '" + RuleTestOrgReviewSetup.RULE_TEST_ORG_CODE_NAME + "' is incorrect","VPIT",extensionVO.getValue());
            } else {
                fail("Rule Extension has attribute key that is neither '" + RuleTestOrgReviewSetup.RULE_TEST_CHART_CODE_NAME +
                        "' nor '" + RuleTestOrgReviewSetup.RULE_TEST_ORG_CODE_NAME + "'");
            }
        }

        ruleReportCriteria = null;
        rules = null;
        ruleVO = null;
        responsibilityVO = null;
        ruleReportCriteria = new RuleReportCriteriaVO();
        ruleReportCriteria.setDocumentTypeName(RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setRuleTemplateName(RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE);
        ruleReportCriteria.setResponsibleUser(new NetworkIdVO("user1"));
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        rules = info.ruleReport(ruleReportCriteria);
        assertEquals("Number of rules returned is incorrect",2,rules.length);

        ruleReportCriteria = null;
        rules = null;
        ruleVO = null;
        responsibilityVO = null;
        ruleExtensionVO = null;
        ruleExtensionVO2 = null;
        ruleExtensionVOs = null;
        ruleReportCriteria = new RuleReportCriteriaVO();
        ruleReportCriteria.setDocumentTypeName(RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME);
        ruleReportCriteria.setRuleTemplateName(RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE);
        ruleExtensionVO = new RuleExtensionVO(RuleTestOrgReviewSetup.RULE_TEST_CHART_CODE_NAME,"UA");
        ruleExtensionVO2 = new RuleExtensionVO(RuleTestOrgReviewSetup.RULE_TEST_ORG_CODE_NAME,"FMOP");
        ruleExtensionVOs = new RuleExtensionVO[] {ruleExtensionVO,ruleExtensionVO2};
        ruleReportCriteria.setRuleExtensionVOs(ruleExtensionVOs);
        ruleReportCriteria.setIncludeDelegations(Boolean.FALSE);
        rules = info.ruleReport(ruleReportCriteria);
        assertEquals("Number of rules returned is incorrect",1,rules.length);
        ruleVO = rules[0];
        assertEquals("Rule Document Type is not " + RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME,RuleTestOrgReviewSetup.DOCUMENT_TYPE_NAME,ruleVO.getDocTypeName());
        assertEquals("Rule Template Named returned is not " + RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE,RuleTestOrgReviewSetup.RULE_TEST_TEMPLATE,ruleVO.getRuleTemplateName());
        assertEquals("Rule did not have ignore previous set to true",Boolean.TRUE,ruleVO.getIgnorePrevious());
        assertEquals("Number of Rule Responsibilities Returned Should be 1",1,ruleVO.getRuleResponsibilities().length);
        responsibilityVO = ruleVO.getRuleResponsibilities()[0];
        assertEquals("Rule workgroup name is incorrect",RuleTestOrgReviewSetup.RULE_TEST_WORKGROUP,responsibilityVO.getWorkgroup().getWorkgroupName());
        assertEquals("Rule priority is incorrect",Integer.valueOf(1),responsibilityVO.getPriority());
        assertEquals("Rule action request is incorrect",EdenConstants.ACTION_REQUEST_APPROVE_REQ,responsibilityVO.getActionRequestedCd());
    }

    private void ruleExceptionTest(WorkflowInfo info, RuleReportCriteriaVO ruleReportCriteria, String message) {
        try {
            info.ruleReport(ruleReportCriteria);
            fail(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class RuleTestGeneralSetup {
        public static final String DOCUMENT_TYPE_NAME = "RuleTestDocType";
        public static final String RULE_TEST_TEMPLATE_1 = "WorkflowDocumentTemplate";
        public static final String RULE_TEST_TEMPLATE_2 = "WorkflowDocument2Template";
        public static final String RULE_TEST_TEMPLATE_3 = "WorkflowDocument3Template";
        public static final String RULE_TEST_WORKGROUP = "NonSIT";
    }

    private class RuleTestOrgReviewSetup {
        public static final String DOCUMENT_TYPE_NAME = "OrgReviewTestDocType";
        public static final String RULE_TEST_TEMPLATE = "OrgReviewTemplate";
        public static final String RULE_TEST_WORKGROUP = "Org_Review_Group";
        public static final String RULE_TEST_WORKGROUP2 = "Org_Review_Group_2";
        public static final String RULE_TEST_CHART_CODE_NAME = "chartCode";
        public static final String RULE_TEST_ORG_CODE_NAME = "orgCode";
    }

    private class SeqSetup {
        public static final String DOCUMENT_TYPE_NAME = "SeqDocType";
        public static final String LAST_APPROVER_DOCUMENT_TYPE_NAME = "SeqLastApproverDocType";
        public static final String CHILD_DOCUMENT_TYPE_NAME = "SeqChildDocType";
        public static final String ADHOC_NODE = "AdHoc";
        public static final String WORKFLOW_DOCUMENT_NODE = "WorkflowDocument";
        public static final String WORKFLOW_DOCUMENT_2_NODE = "WorkflowDocument2";
        public static final String ACKNOWLEDGE_1_NODE = "Acknowledge1";
        public static final String ACKNOWLEDGE_2_NODE = "Acknowledge2";
    }
}
