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
package org.kuali.rice.kew.actionlist;


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
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionlist.service.ActionListService;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.service.RouteHeaderService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;
import org.kuali.rice.kew.user.AuthenticationUserId;
import org.kuali.rice.kew.user.Recipient;
import org.kuali.rice.kew.user.UserService;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.user.WorkflowUserId;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.WebFriendlyRecipient;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springmodules.orm.ojb.PersistenceBrokerCallback;


/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ActionListTest extends KEWTestCase {

    private static final String[] AUTHENTICATION_IDS = { "ewestfal", "rkirkend", "jhopf", "bmcgough" };
    private static final String[] WORKGROUP_IDS = { "1", "2", "3", "4" };

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
                            PreparedStatement ps = conn.prepareStatement("select distinct PRNCPL_ID from krew_actn_itm_t");
                            ResultSet rs = ps.executeQuery();
                            int emplIdCnt = 0;
                            int loopCnt = 0;
                            UserService userService = (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
                            //do first 5 for time sake
                            while (rs.next() && ++loopCnt < 6) {
                                String workflowId = rs.getString(1);
                                PreparedStatement ps1 = conn.prepareStatement("select count(*) from krew_actn_itm_t where PRNCPL_ID = ?");
                                ps1.setString(1, workflowId);
                                ResultSet rsWorkflowIdCnt = ps1.executeQuery();
                                if (rsWorkflowIdCnt.next()) {
                                    emplIdCnt = rsWorkflowIdCnt.getInt(1);
                                } else {
                                    throw new Exception("WorkflowId " + workflowId + " didn't return a count.  Test SQL invalid.");
                                }
                                Collection actionList = getActionListService().findByPrincipalId(workflowId);
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
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("jhopf"), "ActionListDocumentType");
    	document.routeDocument("");

    	// at this point the document should be routed to the following people
    	// 1) approve to bmcgough with primary delegate of rkirkend and secondary delegates of ewestfal and bmcgough (himself)
    	// 2) approve to jitrue with a secondary delegate of jitrue (himself)
    	// 3) acknowledge to user1
    	// 4) approve to NonSIT workgroup (which should include user1)

    	// now lets verify that everyone's action lists look correct

    	WorkflowUser bmcgough = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("bmcgough"));
    	//KimPrincipal bmcgough = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("bmcgough");
    	WorkflowUser rkirkend = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("rkirkend"));
    	//KimPrincipal rkirkend = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("rkirkend");
    	WorkflowUser ewestfal = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("ewestfal"));
    	//KimPrincipal ewestfal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("ewestfal");
    	WorkflowUser jitrue = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("jitrue"));
    	//KimPrincipal jitrue = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("jitrue");
    	WorkflowUser user1 = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("user1"));
    	//KimPrincipal user1 = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName("user1");
    	KimGroup NonSIT = KIMServiceLocator.getIdentityManagementService().getGroupByName(KimConstants.KIM_GROUP_WORKFLOW_NAMESPACE_CODE, "NonSIT");

    	ActionListFilter noFilter = new ActionListFilter();
    	ActionListFilter excludeSecondaryFilter = new ActionListFilter();
    	excludeSecondaryFilter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
    	excludeSecondaryFilter.setExcludeDelegationType(true);
    	ActionListFilter secondaryFilter = new ActionListFilter();
    	secondaryFilter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
    	Collection actionItems = null;
    	ActionItem actionItem = null;

    	actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), excludeSecondaryFilter);
    	assertEquals("bmcgough should have 0 items in his primary action list.", 0, actionItems.size());
    	actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), secondaryFilter);
    	assertEquals("bmcgough should have 1 item in his secondary action list.", 1, actionItems.size());
        actionItem = (ActionItem)actionItems.iterator().next();
        assertEquals("Should be an approve request.", KEWConstants.ACTION_REQUEST_APPROVE_REQ, actionItem.getActionRequestCd());
        assertEquals("Should be a secondary delegation request.", KEWConstants.DELEGATION_SECONDARY, actionItem.getDelegationType());
    	actionItem = (ActionItem)actionItems.iterator().next();
    	assertEquals("Should be an approve request.", KEWConstants.ACTION_REQUEST_APPROVE_REQ, actionItem.getActionRequestCd());
    	assertEquals("Should be a secondary delegation request.", KEWConstants.DELEGATION_SECONDARY, actionItem.getDelegationType());
    	actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), noFilter);
    	assertEquals("bmcgough should have 1 item in his entire action list.", 1, actionItems.size());

    	actionItems = getActionListService().getActionList(rkirkend.getWorkflowId(), excludeSecondaryFilter);
    	assertEquals("bmcgough should have 1 item in his primary action list.", 1, actionItems.size());

    	actionItems = getActionListService().getActionList(jitrue.getWorkflowId(), excludeSecondaryFilter);
    	assertEquals("jitrue should have 1 item in his primary action list.", 1, actionItems.size());

    	actionItems = getActionListService().getActionList(ewestfal.getWorkflowId(), secondaryFilter);
    	assertEquals("ewestfal should have 1 item in his secondary action list.", 1, actionItems.size());

    	// check that user1's approve comes out as their action item from the action list
    	actionItems = getActionListService().getActionList(user1.getWorkflowId(), noFilter);
    	assertEquals("user1 should have 1 item in his primary action list.", 1, actionItems.size());
    	actionItem = (ActionItem)actionItems.iterator().next();
    	assertEquals("Should be an approve request.", KEWConstants.ACTION_REQUEST_APPROVE_REQ, actionItem.getActionRequestCd());
    	assertEquals("Should be to a workgroup.", NonSIT.getGroupId(), actionItem.getGroupId());
    	// check that user1 acknowledge shows up when filtering
    	ActionListFilter ackFilter = new ActionListFilter();
    	ackFilter.setActionRequestCd(KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ);
    	actionItems = getActionListService().getActionList(user1.getWorkflowId(), ackFilter);
    	assertEquals("user1 should have 1 item in his primary action list.", 1, actionItems.size());
    	actionItem = (ActionItem)actionItems.iterator().next();
    	assertEquals("Should be an acknowledge request.", KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, actionItem.getActionRequestCd());
    	assertNull("Should not be to a workgroup.", actionItem.getGroupId());

    	// all members of NonSIT should have a single primary Approve Request
    	for (Iterator iterator = KIMServiceLocator.getIdentityManagementService().getGroupMemberPrincipalIds(NonSIT.getGroupId()).iterator(); iterator.hasNext(); ) {
			String userId = (String)iterator.next();
			//will want to convert to Kim Principal
    	    WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(userId));
			actionItems = getActionListService().getActionList(user.getWorkflowId(), excludeSecondaryFilter);
			assertEquals("Workgroup Member " + user.getDisplayName() + " should have 1 action item.", 1, actionItems.size());
			actionItem = (ActionItem)actionItems.iterator().next();
			assertEquals("Should be an approve request.", KEWConstants.ACTION_REQUEST_APPROVE_REQ, actionItem.getActionRequestCd());
			assertEquals("Should be to a workgroup.", NonSIT.getGroupId(), actionItem.getGroupId());
		}

        document = new WorkflowDocument(new NetworkIdDTO("jhopf"), "ActionListDocumentType_PrimaryDelegate");
        document.routeDocument("");
        document = new WorkflowDocument(new NetworkIdDTO("jhopf"), "ActionListDocumentType_PrimaryDelegate2");
        document.routeDocument("");

        actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), excludeSecondaryFilter);
        assertEquals("bmcgough should have 0 items in his primary action list.", 0, actionItems.size());
        actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), secondaryFilter);
        assertEquals("bmcgough should have 1 item in his secondary action list.", 3, actionItems.size());
        actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), new ActionListFilter());
        assertEquals("bmcgough should have 1 item in his entire action list.", 3, actionItems.size());

        ActionListFilter filter = null;
        // test a standard filter with no delegations
        filter = new ActionListFilter();
        filter.setDelegatorId(KEWConstants.DELEGATION_DEFAULT);
        filter.setPrimaryDelegateId(KEWConstants.PRIMARY_DELEGATION_DEFAULT);
        actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), filter);
        assertEquals("bmcgough should have 0 items in his entire action list.", 0, actionItems.size());

        // test secondary delegation with all selected returns all
        filter = new ActionListFilter();
        filter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
        filter.setDelegatorId(KEWConstants.ALL_CODE);
        actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), filter);
        assertEquals("bmcgough has incorrect action list item count.", 3, actionItems.size());

        // test that primary delegation with none selected returns none
        filter = new ActionListFilter();
        filter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
        filter.setDelegatorId(KEWConstants.DELEGATION_DEFAULT);
        actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), filter);
        assertEquals("bmcgough has incorrect action list item count.", 0, actionItems.size());

        // test that primary delegation with single user ids works corectly
        filter = new ActionListFilter();
        filter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
        filter.setDelegatorId(bmcgough.getWorkflowId());
        actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), filter);
        assertEquals("bmcgough has incorrect action list item count.", 3, actionItems.size());
        filter = new ActionListFilter();
        filter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
        filter.setDelegatorId(bmcgough.getWorkflowId());
        actionItems = getActionListService().getActionList(ewestfal.getWorkflowId(), filter);
        assertEquals("ewestfal has incorrect action list item count.", 3, actionItems.size());
        filter = new ActionListFilter();
        filter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
        filter.setDelegatorId(jitrue.getWorkflowId());
        actionItems = getActionListService().getActionList(jitrue.getWorkflowId(), filter);
        assertEquals("jitrue has incorrect action list item count.", 3, actionItems.size());
    }

    /**
     * Tests that the user's secondary action list works appropriately.  Also checks that if a user
     * is their own secondary delegate, their request shows up in their main action list rather than
     * their secondary list.
     */
    @Test public void testPrimaryDelegationActionList() throws Exception {
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("jhopf"), "ActionListDocumentType");
    	document.routeDocument("");

    	// at this point the document should be routed to the following people
    	// 1) approve to bmcgough with primary delegate of rkirkend and secondary delegates of ewestfal and bmcgough (himself)
    	// 2) approve to jitrue with a secondary delegate of jitrue (himself)
    	// 3) acknowledge to user1
    	// 4) approve to NonSIT workgroup (which should include user1)

    	// now lets verify that everyone's action lists look correct

    	WorkflowUser bmcgough = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("bmcgough"));
    	WorkflowUser rkirkend = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("rkirkend"));
//    	WorkflowUser ewestfal = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("ewestfal"));
//    	WorkflowUser jitrue = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("jitrue"));
//    	WorkflowUser user1 = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("user1"));
        WorkflowUser delyea = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("delyea"));
        WorkflowUser temay = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("temay"));

//    	ActionListFilter excludeSecondaryFilter = new ActionListFilter();
//    	excludeSecondaryFilter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
//    	excludeSecondaryFilter.setExcludeDelegationType(true);
//    	ActionListFilter showSecondaryFilter = new ActionListFilter();
//    	showSecondaryFilter.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
    	ActionListFilter showPrimaryFilter = new ActionListFilter();
    	showPrimaryFilter.setDelegationType(KEWConstants.DELEGATION_PRIMARY);
    	Collection actionItems = null;
    	ActionItem actionItem = null;

    	// make sure showing primary delegations show primary delegated action items
    	actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), showPrimaryFilter);
    	assertEquals("bmcgough should have 1 item in his primary delegation action list.", 1, actionItems.size());
//    	actionItems = getActionListService().getActionList(bmcgough, excludeSecondaryFilter);
//    	assertEquals("bmcgough should have 0 items in his primary action list.", 0, actionItems.size());
//    	actionItems = getActionListService().getActionList(bmcgough, showSecondaryFilter);
//    	assertEquals("bmcgough should have 1 item in his secondary action list.", 1, actionItems.size());
//    	actionItem = (ActionItem)actionItems.iterator().next();
//    	assertEquals("Should be an approve request.", KEWConstants.ACTION_REQUEST_APPROVE_REQ, actionItem.getActionRequestCd());
//    	assertEquals("Should be a secondary delegation request.", KEWConstants.DELEGATION_SECONDARY, actionItem.getDelegationType());
    	actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), new ActionListFilter());
    	assertEquals("bmcgough should have 1 item in his entire action list.", 1, actionItems.size());

//    	actionItems = getActionListService().getActionList(rkirkend, excludeSecondaryFilter);
//    	assertEquals("rkirkend should have 1 item in his primary action list.", 1, actionItems.size());

    	document = new WorkflowDocument(new NetworkIdDTO("jhopf"), "ActionListDocumentType_PrimaryDelegate");
    	document.routeDocument("");
    	document = new WorkflowDocument(new NetworkIdDTO("jhopf"), "ActionListDocumentType_PrimaryDelegate2");
        document.routeDocument("");

        actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), showPrimaryFilter);
        // should be 6 total action items but 3 distinct doc ids
        assertEquals("bmcgough should have 1 item in his primary delegation action list.", 3, actionItems.size());
//        actionItems = getActionListService().getActionList(bmcgough, excludeSecondaryFilter);
//        assertEquals("bmcgough should have 0 items in his primary action list.", 0, actionItems.size());
//        actionItems = getActionListService().getActionList(bmcgough, showSecondaryFilter);
//        assertEquals("bmcgough should have 1 item in his secondary action list.", 3, actionItems.size());
//        actionItem = (ActionItem)actionItems.iterator().next();
//        assertEquals("Should be an approve request.", KEWConstants.ACTION_REQUEST_APPROVE_REQ, actionItem.getActionRequestCd());
//        assertEquals("Should be a secondary delegation request.", KEWConstants.DELEGATION_SECONDARY, actionItem.getDelegationType());
//        actionItems = getActionListService().getActionList(bmcgough, new ActionListFilter());
//        assertEquals("bmcgough should have 1 item in his entire action list.", 3, actionItems.size());

//        actionItems = getActionListService().getActionList(rkirkend, excludeSecondaryFilter);
//        assertEquals("rkirkend should have 1 item in his primary action list.", 3, actionItems.size());

        ActionListFilter filter = null;

        // test a standard filter with no delegations
        filter = new ActionListFilter();
        filter.setDelegatorId(KEWConstants.DELEGATION_DEFAULT);
        filter.setPrimaryDelegateId(KEWConstants.PRIMARY_DELEGATION_DEFAULT);
        actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), filter);
        assertEquals("bmcgough should have 0 items in his entire action list.", 0, actionItems.size());

        // test primary delegation with all selected returns all
        filter = new ActionListFilter();
        filter.setDelegationType(KEWConstants.DELEGATION_PRIMARY);
        filter.setPrimaryDelegateId(KEWConstants.ALL_CODE);
        actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), filter);
        assertEquals("bmcgough should have 1 item in his entire action list.", 3, actionItems.size());

        // test that primary delegation with none selected returns none
        filter = new ActionListFilter();
        filter.setDelegationType(KEWConstants.DELEGATION_PRIMARY);
        filter.setPrimaryDelegateId(KEWConstants.PRIMARY_DELEGATION_DEFAULT);
        actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), filter);
        assertEquals("bmcgough should have 1 item in his entire action list.", 0, actionItems.size());

        // test that primary delegation with single user ids works corectly
        filter = new ActionListFilter();
        filter.setDelegationType(KEWConstants.DELEGATION_PRIMARY);
        filter.setPrimaryDelegateId(rkirkend.getWorkflowId());
        actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), filter);
        assertEquals("bmcgough should have 3 items in his entire action list.", 3, actionItems.size());
        filter = new ActionListFilter();
        filter.setDelegationType(KEWConstants.DELEGATION_PRIMARY);
        filter.setPrimaryDelegateId(delyea.getWorkflowId());
        actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), filter);
        assertEquals("bmcgough should have 2 items in his entire action list.", 2, actionItems.size());
        filter = new ActionListFilter();
        filter.setDelegationType(KEWConstants.DELEGATION_PRIMARY);
        filter.setPrimaryDelegateId(temay.getWorkflowId());
        actionItems = getActionListService().getActionList(bmcgough.getWorkflowId(), filter);
        assertEquals("bmcgough should have 1 item in his entire action list.", 1, actionItems.size());

    }

    /**
     * Tests that the retrieval of primary and secondary delegation users is working correctly
     */
    @Test public void testGettingDelegationUsers() throws Exception {

        Person jhopf = KIMServiceLocator.getPersonService().getPersonByPrincipalName("jhopf");
        Person bmcgough = KIMServiceLocator.getPersonService().getPersonByPrincipalName("bmcgough");
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("jhopf"), "ActionListDocumentType");
    	document.routeDocument("");
        document = new WorkflowDocument(new NetworkIdDTO("jhopf"), "ActionListDocumentType_PrimaryDelegate");
        document.routeDocument("");
        document = new WorkflowDocument(new NetworkIdDTO("jhopf"), "ActionListDocumentType_PrimaryDelegate2");
        document.routeDocument("");

        Collection<Recipient> recipients = getActionListService().findUserPrimaryDelegations(jhopf.getPrincipalId());
        assertEquals("Wrong size of users who were delegated to via Primary Delegation", 0, recipients.size());
    	recipients = getActionListService().findUserPrimaryDelegations(bmcgough.getPrincipalId());
    	assertEquals("Wrong size of users who were delegated to via Primary Delegation", 3, recipients.size());
    	String user1 = KIMServiceLocator.getPersonService().getPersonByPrincipalName("rkirkend").getPrincipalId();
    	String user2 = KIMServiceLocator.getPersonService().getPersonByPrincipalName("temay").getPrincipalId();
    	String user3 = KIMServiceLocator.getPersonService().getPersonByPrincipalName("delyea").getPrincipalId();

    	boolean foundUser1 = false;
        boolean foundUser2 = false;
        boolean foundUser3 = false;
    	for (Recipient recipient : recipients) {
            if (user1.equals(((WebFriendlyRecipient)recipient).getRecipientId())) {
                foundUser1 = true;
            } else if (user2.equals(((WebFriendlyRecipient)recipient).getRecipientId())) {
                foundUser2 = true;
            } else if (user3.equals(((WebFriendlyRecipient)recipient).getRecipientId())) {
                foundUser3 = true;
            } else {
                fail("Found invalid recipient in list with display name '" + recipient.getDisplayName() + "'");
            }
        }
    	assertTrue("Should have found user " + user1, foundUser1);
        assertTrue("Should have found user " + user2, foundUser2);
        assertTrue("Should have found user " + user3, foundUser3);

    	recipients = getActionListService().findUserSecondaryDelegators(bmcgough.getPrincipalId());
    	assertEquals("Wrong size of users who were have delegated to given user via Secondary Delegation", 1, recipients.size());
    	assertEquals("Wrong employee id of primary delegate", "bmcgough", ((WorkflowUser)recipients.iterator().next()).getAuthenticationUserId().getAuthenticationId());
    }

    private DocumentRouteHeaderValue generateDocRouteHeader() {
        DocumentRouteHeaderValue routeHeader = new DocumentRouteHeaderValue();
        routeHeader.setAppDocId("Test");
        routeHeader.setApprovedDate(null);
        routeHeader.setCreateDate(new Timestamp(new Date().getTime()));
        routeHeader.setDocContent("test");
        routeHeader.setDocRouteLevel(new Integer(1));
        routeHeader.setDocRouteStatus(KEWConstants.ROUTE_HEADER_ENROUTE_CD);
        routeHeader.setDocTitle("Test");
        routeHeader.setDocumentTypeId(new Long(1));
        routeHeader.setDocVersion(new Integer(KEWConstants.CURRENT_DOCUMENT_VERSION));
        routeHeader.setRouteStatusDate(new Timestamp(new Date().getTime()));
        routeHeader.setStatusModDate(new Timestamp(new Date().getTime()));
        routeHeader.setInitiatorWorkflowId("someone");
        return routeHeader;
    }

    private ActionItem generateActionItem(DocumentRouteHeaderValue routeHeader, String actionRequested, String authenticationId, String groupId) throws KEWUserNotFoundException {
        ActionItem actionItem = new ActionItem();
        actionItem.setActionRequestCd(actionRequested);
        actionItem.setActionRequestId(new Long(1));
        actionItem.setPrincipalId(KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(authenticationId)).getWorkflowUserId().getWorkflowId());
        actionItem.setRouteHeaderId(routeHeader.getRouteHeaderId());
        actionItem.setRouteHeader(routeHeader);
        actionItem.setDateAssigned(new Timestamp(new Date().getTime()));
        actionItem.setDocHandlerURL("Unit testing");
        actionItem.setDocLabel("unit testing");
        actionItem.setDocTitle(routeHeader.getDocTitle());
        actionItem.setDocName("docname");
        actionItem.setGroupId(groupId);
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