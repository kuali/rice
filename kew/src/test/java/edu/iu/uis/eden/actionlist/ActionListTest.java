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
package edu.iu.uis.eden.actionlist;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springmodules.orm.ojb.PersistenceBrokerCallback;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routeheader.RouteHeaderService;
import edu.iu.uis.eden.test.TestUtilities;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionListTest extends KEWTestCase {

    private static final String[] AUTHENTICATION_IDS = { "ewestfal", "rkirkend", "jhopf", "bmcgough" };
    private static final Long[] WORKGROUP_IDS = { new Long(1), new Long(2), new Long(3), new Long(4) };

    private DocumentRouteHeaderValue routeHeader1;
    private DocumentRouteHeaderValue routeHeader2;
    private DocumentRouteHeaderValue routeHeader3;
    private List actionItems = new ArrayList();

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionListConfig.xml");
    }

    private void setUpOldSchool() throws Exception {
        super.setUpTransaction();
        routeHeader1 = generateDocRouteHeader();
        routeHeader1.setActionItems(new ArrayList());
        routeHeader2 = generateDocRouteHeader();
        routeHeader2.setActionItems(new ArrayList());
        for (int i = 0; i < AUTHENTICATION_IDS.length; i++) {
            routeHeader1.getActionItems().add(generateActionItem(routeHeader1, "K", AUTHENTICATION_IDS[i], null));
            routeHeader2.getActionItems().add(generateActionItem(routeHeader2, "A", AUTHENTICATION_IDS[i], null));
        }
        routeHeader3 = generateDocRouteHeader();
        routeHeader3.setActionItems(new ArrayList());
        for (int i = 0; i < WORKGROUP_IDS.length; i++) {
            routeHeader3.getActionItems().add(generateActionItem(routeHeader3, "A", AUTHENTICATION_IDS[i], WORKGROUP_IDS[i]));
        }

        getRouteHeaderService().saveRouteHeader(routeHeader1);
        getRouteHeaderService().saveRouteHeader(routeHeader2);
        getRouteHeaderService().saveRouteHeader(routeHeader3);

        actionItems.addAll(routeHeader1.getActionItems());
        actionItems.addAll(routeHeader2.getActionItems());
        actionItems.addAll(routeHeader3.getActionItems());
        for (Iterator iterator = actionItems.iterator(); iterator.hasNext();) {
            ActionItem actionItem = (ActionItem) iterator.next();
            getActionListService().saveActionItem(actionItem);
        }


    }

    @Test public void testRouteHeaderDelete() throws Exception {
    	setUpOldSchool();
        Collection actionItems = getActionListService().findByRouteHeaderId(routeHeader1.getRouteHeaderId());
        assertEquals("Route header " + routeHeader1.getRouteHeaderId() + " should have action items.", AUTHENTICATION_IDS.length, actionItems.size());
        getActionListService().deleteByRouteHeaderId(routeHeader1.getRouteHeaderId());
        actionItems = getActionListService().findByRouteHeaderId(routeHeader1.getRouteHeaderId());
        assertEquals("There should be no remaining action items for route header " + routeHeader1.getRouteHeaderId(), 0, actionItems.size());
        actionItems = getActionListService().findByRouteHeaderId(routeHeader2.getRouteHeaderId());
        assertEquals("Route header " + routeHeader2.getRouteHeaderId() + " should have action items.", AUTHENTICATION_IDS.length, actionItems.size());
    }

    @Test public void testActionListCount() throws Exception {
    	setUpOldSchool();
        TransactionTemplate transactionTemplate = getTransactionTemplate();
        transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                return TestUtilities.getPersistenceBrokerTemplate().execute(new PersistenceBrokerCallback() {
                    public Object doInPersistenceBroker(PersistenceBroker pb) {
                        try {
                            Connection conn = pb.serviceConnectionManager().getConnection();
                            PreparedStatement ps = conn.prepareStatement("select distinct ACTN_ITM_PRSN_EN_ID from en_actn_itm_t");
                            ResultSet rs = ps.executeQuery();
                            int emplIdCnt = 0;
                            int loopCnt = 0;
                            UserService userService = (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
                            //do first 5 for time sake
                            while (rs.next() && ++loopCnt < 6) {
                                String workflowId = rs.getString(1);
                                PreparedStatement ps1 = conn.prepareStatement("select count (*) from en_actn_itm_t where ACTN_ITM_PRSN_EN_ID = '" + workflowId + "'");
                                ResultSet rsWorkflowIdCnt = ps1.executeQuery();
                                if (rsWorkflowIdCnt.next()) {
                                    emplIdCnt = rsWorkflowIdCnt.getInt(1);
                                } else {
                                    throw new Exception("WorkflowId " + workflowId + " didn't return a count.  Test SQL invalid.");
                                }
                                Collection actionList = getActionListService().findByWorkflowUser(userService.getWorkflowUser(new WorkflowUserId(workflowId)));
                                assertEquals("ActionItemService returned incorrect number of ActionItems for user " + workflowId + " ActionList", emplIdCnt, actionList.size());
                                ps1.close();
                                rsWorkflowIdCnt.close();
                            }
                            rs.close();
                            ps.close();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        return null;
                    }
                });
            }
        });
    }

    /**
     * Tests that the user's secondary action list works appropriately.  Also checks that if a user
     * is their own secondary delegate, their request shows up in their main action list rather than
     * their secondary list.
     */
    @Test public void testSecondaryActionList() throws Exception {
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("jhopf"), "ActionListDocumentType");
    	document.routeDocument("");

    	// at this point the document should be routed to the following people
    	// 1) approve to bmcgough with primary delegate of rkirkend and secondary delegates of ewestfal and bmcgough (himself)
    	// 2) approve to jitrue with a secondary delegate of jitrue (himself)
    	// 3) acknowledge to user1
    	// 4) approve to NonSIT workgroup (which should include user1)

    	// now lets verify that everyone's action lists look correct

    	WorkflowUser bmcgough = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("bmcgough"));
    	WorkflowUser rkirkend = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("rkirkend"));
    	WorkflowUser ewestfal = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("ewestfal"));
    	WorkflowUser jitrue = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("jitrue"));
    	WorkflowUser user1 = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("user1"));
    	Workgroup NonSIT = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("NonSIT"));

    	ActionListFilter noFilter = new ActionListFilter();
    	ActionListFilter primaryFilter = new ActionListFilter();
    	primaryFilter.setDelegationType(EdenConstants.DELEGATION_SECONDARY);
    	primaryFilter.setExcludeDelegationType(true);
    	ActionListFilter secondaryFilter = new ActionListFilter();
    	secondaryFilter.setDelegationType(EdenConstants.DELEGATION_SECONDARY);
    	Collection actionItems = null;
    	ActionItem actionItem = null;

    	actionItems = getActionListService().getActionList(bmcgough, primaryFilter);
    	assertEquals("bmcgough should have 0 items in his primary action list.", 0, actionItems.size());
    	actionItems = getActionListService().getActionList(bmcgough, secondaryFilter);
    	assertEquals("bmcgough should have 1 item in his secondary action list.", 1, actionItems.size());
    	actionItem = (ActionItem)actionItems.iterator().next();
    	assertEquals("Should be an approve request.", EdenConstants.ACTION_REQUEST_APPROVE_REQ, actionItem.getActionRequestCd());
    	assertEquals("Should be a secondary delegation request.", EdenConstants.DELEGATION_SECONDARY, actionItem.getDelegationType());
    	actionItems = getActionListService().getActionList(bmcgough, noFilter);
    	assertEquals("bmcgough should have 1 item in his entire action list.", 1, actionItems.size());

    	actionItems = getActionListService().getActionList(rkirkend, primaryFilter);
    	assertEquals("bmcgough should have 1 item in his primary action list.", 1, actionItems.size());

    	actionItems = getActionListService().getActionList(jitrue, primaryFilter);
    	assertEquals("jitrue should have 1 item in his primary action list.", 1, actionItems.size());

    	actionItems = getActionListService().getActionList(ewestfal, secondaryFilter);
    	assertEquals("ewestfal should have 1 item in his secondary action list.", 1, actionItems.size());

    	// check that user1's approve comes out as their action item from the action list
    	actionItems = getActionListService().getActionList(user1, noFilter);
    	assertEquals("user1 should have 1 item in his primary action list.", 1, actionItems.size());
    	actionItem = (ActionItem)actionItems.iterator().next();
    	assertEquals("Should be an approve request.", EdenConstants.ACTION_REQUEST_APPROVE_REQ, actionItem.getActionRequestCd());
    	assertEquals("Should be to a workgroup.", NonSIT.getWorkflowGroupId().getGroupId(), actionItem.getWorkgroupId());
    	// check that user1 acknowledge shows up when filtering
    	ActionListFilter ackFilter = new ActionListFilter();
    	ackFilter.setActionRequestCd(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ);
    	actionItems = getActionListService().getActionList(user1, ackFilter);
    	assertEquals("user1 should have 1 item in his primary action list.", 1, actionItems.size());
    	actionItem = (ActionItem)actionItems.iterator().next();
    	assertEquals("Should be an acknowledge request.", EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, actionItem.getActionRequestCd());
    	assertNull("Should not be to a workgroup.", actionItem.getWorkgroupId());

    	// all members of NonSIT should have a single primary Approve Request
    	for (Iterator iterator = NonSIT.getUsers().iterator(); iterator.hasNext(); ) {
			WorkflowUser user = (WorkflowUser) iterator.next();
			actionItems = getActionListService().getActionList(user, primaryFilter);
			assertEquals("Workgroup Member " + user.getDisplayName() + " should have 1 action item.", 1, actionItems.size());
			actionItem = (ActionItem)actionItems.iterator().next();
			assertEquals("Should be an approve request.", EdenConstants.ACTION_REQUEST_APPROVE_REQ, actionItem.getActionRequestCd());
			assertEquals("Should be to a workgroup.", NonSIT.getWorkflowGroupId().getGroupId(), actionItem.getWorkgroupId());
		}
    }

    private DocumentRouteHeaderValue generateDocRouteHeader() {
        DocumentRouteHeaderValue routeHeader = new DocumentRouteHeaderValue();
        routeHeader.setAppDocId("Test");
        routeHeader.setApprovedDate(null);
        routeHeader.setCreateDate(new Timestamp(new Date().getTime()));
        routeHeader.setDocContent("test");
        routeHeader.setDocRouteLevel(new Integer(1));
        routeHeader.setDocRouteStatus(EdenConstants.ROUTE_HEADER_ENROUTE_CD);
        routeHeader.setDocTitle("Test");
        routeHeader.setDocumentTypeId(new Long(1));
        routeHeader.setDocVersion(new Integer(EdenConstants.CURRENT_DOCUMENT_VERSION));
        routeHeader.setRouteStatusDate(new Timestamp(new Date().getTime()));
        routeHeader.setStatusModDate(new Timestamp(new Date().getTime()));
        routeHeader.setInitiatorWorkflowId("someone");
        return routeHeader;
    }

    private ActionItem generateActionItem(DocumentRouteHeaderValue routeHeader, String actionRequested, String authenticationId, Long workgroupId) throws EdenUserNotFoundException {
        ActionItem actionItem = new ActionItem();
        actionItem.setActionRequestCd(actionRequested);
        actionItem.setActionRequestId(new Long(1));
        actionItem.setWorkflowId(KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(authenticationId)).getWorkflowUserId().getWorkflowId());
        actionItem.setRouteHeaderId(routeHeader.getRouteHeaderId());
        actionItem.setRouteHeader(routeHeader);
        actionItem.setDateAssigned(new Timestamp(new Date().getTime()));
        actionItem.setDocHandlerURL("Unit testing");
        actionItem.setDocLabel("unit testing");
        actionItem.setDocTitle(routeHeader.getDocTitle());
        actionItem.setDocName("docname");
        actionItem.setWorkgroupId(workgroupId);
//        actionItem.setResponsibilityId(new Long(-1));
        return actionItem;
    }

    private ActionListService getActionListService() {
        return (ActionListService) KEWServiceLocator.getActionListService();
    }

    private RouteHeaderService getRouteHeaderService() {
        return KEWServiceLocator.getRouteHeaderService();
    }
}