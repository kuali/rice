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
package org.kuali.rice.kew.actions;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kuali.rice.kew.actionlist.ActionListFilter;
import org.kuali.rice.kew.dto.DocumentDetailDTO;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.dto.ReportActionToTakeDTO;
import org.kuali.rice.kew.dto.ReportCriteriaDTO;
import org.kuali.rice.kew.engine.node.BranchState;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.rule.TestRuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.FutureRequestDocumentStateManager;
import org.kuali.rice.kew.util.KEWConstants;


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
        List<String> ids = new ArrayList<String>();
        ids.add(getPrincipalIdForName("user1"));
        TestRuleAttribute.setRecipientPrincipalIds("TestRole", "TestRole-user1", ids);

        // Test receiving future requests

        String rkirkendPrincipalId = getPrincipalIdForName("rkirkend");
        WorkflowDocument document = new WorkflowDocument(rkirkendPrincipalId, "TestDocumentType");
        document.setReceiveFutureRequests();
        document.routeDocument("");

        DocumentRouteHeaderValue routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId());
        
        FutureRequestDocumentStateManager futRequestStateMan = new FutureRequestDocumentStateManager(routeHeader, rkirkendPrincipalId);
        assertTrue(futRequestStateMan.isReceiveFutureRequests());
        assertFalse(futRequestStateMan.isClearFutureRequestState());
        assertFalse(futRequestStateMan.isDoNotReceiveFutureRequests());

        // Test not receiving future requests

        document = new WorkflowDocument(rkirkendPrincipalId, "TestDocumentType");
        document.setDoNotReceiveFutureRequests();
        document.routeDocument("");

        routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId());
        
        futRequestStateMan = new FutureRequestDocumentStateManager(routeHeader, rkirkendPrincipalId);
        assertFalse(futRequestStateMan.isReceiveFutureRequests());
        assertFalse(futRequestStateMan.isClearFutureRequestState());
        assertTrue(futRequestStateMan.isDoNotReceiveFutureRequests());

        // test clearing state from existing document
        document = new WorkflowDocument(rkirkendPrincipalId, document.getRouteHeaderId());
        document.setClearFutureRequests();
        document.approve("");

        routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId());
        futRequestStateMan = new FutureRequestDocumentStateManager(routeHeader, rkirkendPrincipalId);
        assertFalse(futRequestStateMan.isReceiveFutureRequests());
        assertTrue(futRequestStateMan.isClearFutureRequestState());
        assertFalse(futRequestStateMan.isDoNotReceiveFutureRequests());

        // reload the route header
        routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId());
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
        document = new WorkflowDocument(rkirkendPrincipalId, "TestDocumentType");
        document.routeDocument("");
        routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId());
        futRequestStateMan = new FutureRequestDocumentStateManager(routeHeader, rkirkendPrincipalId);
        assertFalse(futRequestStateMan.isReceiveFutureRequests());
        assertFalse(futRequestStateMan.isClearFutureRequestState());
        assertFalse(futRequestStateMan.isDoNotReceiveFutureRequests());
    }

    /**
     * Tests future requests work with routing and force action rules
     *
     * @throws Exception
     */
    @Test
    public void testFutureRequestsWithRouting() throws Exception {
        this.loadXmlFile(this.getClass(), "FutureRequestsConfig.xml");

        String user1PrincipalId = getPrincipalIdForName("user1");
        String user2PrincipalId = getPrincipalIdForName("user2");

        // Node 1 - user1 approval (forceAction true)
        //          user2 approval (forceAction false)
        // Node 2 - NonSIT approval (forceAction false)
        //          user1 approval (forceAction true)
        // Node 3 - user2 approval (forceAction false)
        WorkflowDocument document = new WorkflowDocument(user1PrincipalId, "FutureRequestsDoc");
        document.setDoNotReceiveFutureRequests();
        document.routeDocument("");

        document = new WorkflowDocument(user1PrincipalId, document.getRouteHeaderId());
        assertFalse(document.isApprovalRequested());

        document = new WorkflowDocument(user2PrincipalId, document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
        document.setReceiveFutureRequests();
        document.approve("");

        // should have another request from second rule that is not force action because
        // of policy
        document = new WorkflowDocument(user2PrincipalId, document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());

        // user2 should have action items. user1 should not
        assertEquals(1, KEWServiceLocator.getActionListService().getActionList(user2PrincipalId, new ActionListFilter()).size());
        assertEquals(1, KEWServiceLocator.getActionListService().getActionList(user1PrincipalId, new ActionListFilter()).size());

        document.approve("");

        // test for request to user2 and not a workgroup
        document = new WorkflowDocument(user2PrincipalId, document.getRouteHeaderId());
        assertTrue(document.isApprovalRequested());
    }

    /**
     * Tests future requests operation in conjunction with the {@link WorkflowInfo#documentWillHaveAtLeastOneActionRequest(ReportCriteriaDTO, String[])} method
     *
     * @throws Exception
     */
    @Test
    public void testFutureRequestsWithRoutingAndWorkflowInfoActionRequestCheck() throws Exception {
        this.loadXmlFile(this.getClass(), "FutureRequestsConfig.xml");

        String user1PrincipalId = getPrincipalIdForName("user1");
        String user2PrincipalId = getPrincipalIdForName("user2");

        WorkflowDocument document = new WorkflowDocument(user1PrincipalId, "FutureRequestsDoc");
        document.routeDocument("");

        // Node1
        //user1 should have approval requested
        document = new WorkflowDocument(user1PrincipalId, document.getRouteHeaderId());
        WorkflowInfo info = new WorkflowInfo();
        ReportCriteriaDTO reportCriteriaDTO = new ReportCriteriaDTO(document.getRouteHeaderId());
        reportCriteriaDTO.setTargetPrincipalIds(new String[]{user1PrincipalId});
        String actionToTakeNode = "Node1";
        reportCriteriaDTO.setActionsToTake(new ReportActionToTakeDTO[]{new ReportActionToTakeDTO(KEWConstants.ACTION_TAKEN_APPROVED_CD, user1PrincipalId, actionToTakeNode)});
        assertTrue("User " + user1PrincipalId + " should have approval requests on the document", info.documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ}, false));

        info = new WorkflowInfo();
        reportCriteriaDTO = new ReportCriteriaDTO(document.getRouteHeaderId());
        reportCriteriaDTO.setTargetPrincipalIds(new String[]{user1PrincipalId});
        actionToTakeNode = "Node1";
        reportCriteriaDTO.setActionsToTake(new ReportActionToTakeDTO[]{new ReportActionToTakeDTO(KEWConstants.ACTION_TAKEN_APPROVED_CD, user1PrincipalId, actionToTakeNode)});
        DocumentDetailDTO documentVO = info.routingReport(reportCriteriaDTO);
        assertTrue("User " + user1PrincipalId + " should have one or more approval requests on the document", documentVO.getActionRequests().length > 0);

        info = new WorkflowInfo();
        reportCriteriaDTO = new ReportCriteriaDTO(document.getRouteHeaderId());
        String delyeaPrincipalId = getPrincipalIdForName("delyea");
        NetworkIdDTO delyeaIdDTO = new NetworkIdDTO("delyea");
        reportCriteriaDTO.setTargetPrincipalIds(new String[]{user1PrincipalId});
        actionToTakeNode = "Node1";
        reportCriteriaDTO.setActionsToTake(new ReportActionToTakeDTO[]{new ReportActionToTakeDTO(KEWConstants.ACTION_TAKEN_APPROVED_CD, user1PrincipalId, actionToTakeNode)});
        documentVO = info.routingReport(reportCriteriaDTO);
        assertTrue("User " + delyeaPrincipalId + " should not have any requests on the document but routingReport() method should return all action requests anyway", documentVO.getActionRequests().length > 0);

        document = new WorkflowDocument(user1PrincipalId, "FutureRequestsDoc");
        document.setDoNotReceiveFutureRequests();
        document.routeDocument("");

        document = new WorkflowDocument(user1PrincipalId, document.getRouteHeaderId());
        assertFalse(document.isApprovalRequested());

        // user1 should not have approval requested
        info = new WorkflowInfo();
        reportCriteriaDTO = new ReportCriteriaDTO(document.getRouteHeaderId());
        reportCriteriaDTO.setTargetPrincipalIds(new String[]{user1PrincipalId});
        assertFalse("User " + user1PrincipalId + " should not have any approval request on the document", info.documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ}, false));

        // user2 should have approval requested
        info = new WorkflowInfo();
        reportCriteriaDTO = new ReportCriteriaDTO(document.getRouteHeaderId());
        reportCriteriaDTO.setTargetPrincipalIds(new String[]{user2PrincipalId});
        assertTrue("User " + user2PrincipalId + " should have any approval request on the document", info.documentWillHaveAtLeastOneActionRequest(reportCriteriaDTO, new String[]{KEWConstants.ACTION_REQUEST_APPROVE_REQ}, false));

    }
}
