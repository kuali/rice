/*
 * Copyright 2007 The Kuali Foundation
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
import java.util.List;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.Id;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionlist.ActionListFilter;
import edu.iu.uis.eden.clientapp.FutureRequestDocumentStateManager;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.DocumentDetailVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.ReportActionToTakeVO;
import edu.iu.uis.eden.clientapp.vo.ReportCriteriaVO;
import edu.iu.uis.eden.engine.node.BranchState;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routetemplate.TestRuleAttribute;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Tests users requesting to see all future requests, not seeing any future requests on documents and the clearing of those
 * statuses on documents.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class FutureRequestsTest extends KEWTestCase {

    /**
     * Verify future requests status are being preserved on {@link DocumentRouteHeaderValue} objects when set from the
     * {@link WorkflowDocument}
     * 
     * @throws Exception
     */
    @Test
    public void testSavingFutureRequestsStatuses() throws Exception {
        List<Id> ids = new ArrayList<Id>();
        ids.add(new AuthenticationUserId("user1"));
        TestRuleAttribute.setRecipients("TestRole", "TestRole-user1", ids);

        // Test receiving future requests

        NetworkIdVO networkId = new NetworkIdVO("rkirkend");
        WorkflowDocument document = new WorkflowDocument(networkId, "TestDocumentType");
        document.setReceiveFutureRequests();
        document.routeDocument("");

        DocumentRouteHeaderValue routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId());
        WorkflowUser rkirkend = KEWServiceLocator.getUserService().getWorkflowUser(networkId);
        FutureRequestDocumentStateManager futRequestStateMan = new FutureRequestDocumentStateManager(routeHeader, rkirkend);
        assertTrue(futRequestStateMan.isReceiveFutureRequests());
        assertFalse(futRequestStateMan.isClearFutureRequestState());
        assertFalse(futRequestStateMan.isDoNotReceiveFutureRequests());

        // Test not receiving future requests

        document = new WorkflowDocument(networkId, "TestDocumentType");
        document.setDoNotReceiveFutureRequests();
        document.routeDocument("");

        routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId());
        rkirkend = KEWServiceLocator.getUserService().getWorkflowUser(networkId);
        futRequestStateMan = new FutureRequestDocumentStateManager(routeHeader, rkirkend);
        assertFalse(futRequestStateMan.isReceiveFutureRequests());
        assertFalse(futRequestStateMan.isClearFutureRequestState());
        assertTrue(futRequestStateMan.isDoNotReceiveFutureRequests());

        // test clearing state from existing document
        document = new WorkflowDocument(networkId, document.getRouteHeaderId());
        document.setClearFutureRequests();
        document.approve("");

        routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId());
        rkirkend = KEWServiceLocator.getUserService().getWorkflowUser(networkId);
        futRequestStateMan = new FutureRequestDocumentStateManager(routeHeader, rkirkend);
        assertFalse(futRequestStateMan.isReceiveFutureRequests());
        assertTrue(futRequestStateMan.isClearFutureRequestState());
        assertFalse(futRequestStateMan.isDoNotReceiveFutureRequests());

        int deactivatedCount = 0;
        for (BranchState state : routeHeader.getRootBranchState()) {
            if (state.getKey().contains(FutureRequestDocumentStateManager.FUTURE_REQUESTS_VAR_KEY)) {
                fail("state clearing should have removed all future request vars");
            } else if (state.getKey().contains(FutureRequestDocumentStateManager.DEACTIVATED_REQUESTS_VARY_KEY)) {
                deactivatedCount++;
            }
        }
        assertEquals(2, deactivatedCount);
        // test standard scenario of not setting a future request status on the document
        document = new WorkflowDocument(networkId, "TestDocumentType");
        document.routeDocument("");
        routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId());
        rkirkend = KEWServiceLocator.getUserService().getWorkflowUser(networkId);
        futRequestStateMan = new FutureRequestDocumentStateManager(routeHeader, rkirkend);
        assertFalse(futRequestStateMan.isReceiveFutureRequests());
        assertFalse(futRequestStateMan.isClearFutureRequestState());
        assertFalse(futRequestStateMan.isDoNotReceiveFutureRequests());
    }

    /**
     * Tests future requests work with routing and ignore previous rules
     * 
     * @throws Exception
     */
    @Test
    public void testFutureRequestsWithRouting() throws Exception {
        this.loadXmlFile(this.getClass(), "FutureRequestsConfig.xml");

        NetworkIdVO user1 = new NetworkIdVO("user1");
        NetworkIdVO user2 = new NetworkIdVO("user2");

        // Node 1 - user1 approval (ignorePref true)
        //          user2 approval (ignorePref false)
        // Node 2 - NonSIT approval (ignorePrev false)
        //          user1 approval (ignorePref true)
        // Node 3 - user2 approval (ignorePrev false)
        WorkflowDocument document = new WorkflowDocument(user1, "FutureRequestsDoc");
        document.setDoNotReceiveFutureRequests();
        document.routeDocument("");

        document = new WorkflowDocument(user1, document.getRouteHeaderId());
        assertFalse(document.isApprovalRequested());

        document = new WorkflowDocument(user2, document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        document.setReceiveFutureRequests();
        document.approve("");

        // should have another request from second rule that is not ignore previous because
        // of policy
        document = new WorkflowDocument(user2, document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());

        // user2 should have action items. user1 should not
        assertEquals(1, KEWServiceLocator.getActionListService().getActionList(KEWServiceLocator.getUserService().getWorkflowUser(user2), new ActionListFilter()).size());
        assertEquals(1, KEWServiceLocator.getActionListService().getActionList(KEWServiceLocator.getUserService().getWorkflowUser(user1), new ActionListFilter()).size());

        document.approve("");

        // test for request to user2 and not a workgroup
        document = new WorkflowDocument(user2, document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
    }

    /**
     * Tests future requests operation in conjunction with the {@link WorkflowInfo#documentWillHaveAtLeastOneActionRequest(ReportCriteriaVO, String[])} method
     * 
     * @throws Exception
     */
    @Test
    public void testFutureRequestsWithRoutingAndWorkflowInfoActionRequestCheck() throws Exception {
        this.loadXmlFile(this.getClass(), "FutureRequestsConfig.xml");

        NetworkIdVO user1 = new NetworkIdVO("user1");
        NetworkIdVO user2 = new NetworkIdVO("user2");

        WorkflowDocument document = new WorkflowDocument(user1, "FutureRequestsDoc");
        document.routeDocument("");
        
        // Node1
        //user1 should have approval requested
        document = new WorkflowDocument(user1, document.getRouteHeaderId());
        WorkflowInfo info = new WorkflowInfo();
        ReportCriteriaVO reportCriteriaVO = new ReportCriteriaVO(document.getRouteHeaderId());
        reportCriteriaVO.setTargetUsers(new NetworkIdVO[]{user1});
        String actionToTakeNode = "Node1";
        reportCriteriaVO.setActionsToTake(new ReportActionToTakeVO[]{new ReportActionToTakeVO(EdenConstants.ACTION_TAKEN_APPROVED_CD, user1, actionToTakeNode)});
        assertTrue("User " + user1 + " should have approval requests on the document", info.documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, new String[]{EdenConstants.ACTION_REQUEST_APPROVE_REQ}));

        info = new WorkflowInfo();
        reportCriteriaVO = new ReportCriteriaVO(document.getRouteHeaderId());
        reportCriteriaVO.setTargetUsers(new NetworkIdVO[]{user1});
        actionToTakeNode = "Node1";
        reportCriteriaVO.setActionsToTake(new ReportActionToTakeVO[]{new ReportActionToTakeVO(EdenConstants.ACTION_TAKEN_APPROVED_CD, user1, actionToTakeNode)});
        DocumentDetailVO documentVO = info.routingReport(reportCriteriaVO);
        assertTrue("User " + user1 + " should have one or more approval requests on the document", documentVO.getActionRequests().length > 0);

        info = new WorkflowInfo();
        reportCriteriaVO = new ReportCriteriaVO(document.getRouteHeaderId());
        NetworkIdVO tempUser = new NetworkIdVO("delyea");
        reportCriteriaVO.setTargetUsers(new NetworkIdVO[]{tempUser});
        actionToTakeNode = "Node1";
        reportCriteriaVO.setActionsToTake(new ReportActionToTakeVO[]{new ReportActionToTakeVO(EdenConstants.ACTION_TAKEN_APPROVED_CD, user1, actionToTakeNode)});
        documentVO = info.routingReport(reportCriteriaVO);
        assertTrue("User " + tempUser + " should not have any requests on the document but routingReport() method should return all action requests anyway", documentVO.getActionRequests().length > 0);

        document = new WorkflowDocument(user1, "FutureRequestsDoc");
        document.setDoNotReceiveFutureRequests();
        document.routeDocument("");

        document = new WorkflowDocument(user1, document.getRouteHeaderId());
        assertFalse(document.isApprovalRequested());
        
        // user1 should not have approval requested
        info = new WorkflowInfo();
        reportCriteriaVO = new ReportCriteriaVO(document.getRouteHeaderId());
        reportCriteriaVO.setTargetUsers(new NetworkIdVO[]{user1});
        assertFalse("User " + user1 + " should not have any approval request on the document", info.documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, new String[]{EdenConstants.ACTION_REQUEST_APPROVE_REQ}));

        // user2 should have approval requested
        info = new WorkflowInfo();
        reportCriteriaVO = new ReportCriteriaVO(document.getRouteHeaderId());
        reportCriteriaVO.setTargetUsers(new NetworkIdVO[]{user2});
        assertTrue("User " + user2 + " should have any approval request on the document", info.documentWillHaveAtLeastOneActionRequest(reportCriteriaVO, new String[]{EdenConstants.ACTION_REQUEST_APPROVE_REQ}));

    }
}
