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
package edu.iu.uis.eden.actionitem;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionlist.ActionListService;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupNameIdVO;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.workgroup.BaseWorkgroup;
import edu.iu.uis.eden.workgroup.BaseWorkgroupMember;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;
import edu.iu.uis.eden.workgroup.Workgroup;
import edu.iu.uis.eden.workgroup.WorkgroupService;

public class ActionItemServiceTest extends KEWTestCase {

	private ActionListService actionListService;

    protected void loadTestData() throws Exception {
    	loadXmlFile("ActionItemConfig.xml");
    }

    protected void setUpTransaction() throws Exception {
		super.setUpTransaction();
		actionListService = KEWServiceLocator.getActionListService();
	}

	/**
     * When workgroup membership changes all action items to that workgroup need to reflect
     * the new membership
     *
     * @throws Exception
     */
    @Test public void testUpdateActionItemsForWorkgroupChange() throws Exception {

        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), "ActionItemDocumentType");
        document.setTitle("");
        document.routeDocument("");

        WorkgroupService workgroupService = KEWServiceLocator.getWorkgroupService();
        BaseWorkgroup oldWorkgroup = (BaseWorkgroup)workgroupService.getWorkgroup(new GroupNameId("WorkflowAdmin"));
        WorkflowUser user1 = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("user1"));
        WorkflowUser user2 = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("user2"));

        BaseWorkgroup newWorkgroup = (BaseWorkgroup)KEWServiceLocator.getWorkgroupService().getBlankWorkgroup();
        newWorkgroup.setGroupNameId(oldWorkgroup.getGroupNameId());
        newWorkgroup.setActiveInd(Boolean.TRUE);
        newWorkgroup.setCurrentInd(Boolean.TRUE);
        newWorkgroup.setVersionNumber(new Integer(oldWorkgroup.getVersionNumber().intValue()+1));
        newWorkgroup.setLockVerNbr(new Integer(oldWorkgroup.getLockVerNbr().intValue()));
        newWorkgroup.setWorkflowGroupId(oldWorkgroup.getWorkflowGroupId());
        newWorkgroup.setWorkgroupId(oldWorkgroup.getWorkflowGroupId().getGroupId());
        newWorkgroup.setGroupNameId(new GroupNameId(oldWorkgroup.getGroupNameId().getNameId()));
        newWorkgroup.setMembers(new ArrayList<Recipient>(oldWorkgroup.getMembers()));

        List<WorkflowUser> usersToRemove = new ArrayList<WorkflowUser>();
        //remove 'rkirkend' and 'shenl' from the workgroup
        for (Recipient recipient : oldWorkgroup.getMembers()) {
        	if (recipient instanceof WorkflowUser) {
        		WorkflowUser user = (WorkflowUser)recipient;
        		if (user.getAuthenticationUserId().getAuthenticationId().equals("rkirkend") || user.getAuthenticationUserId().getAuthenticationId().equals("shenl")) {
        			usersToRemove.add(user);
        		}
        	}
        }

        newWorkgroup.getMembers().removeAll(usersToRemove);

        List<WorkflowUser> newMembers = new ArrayList<WorkflowUser>();
        newMembers.add(user1);
        newMembers.add(user2);
        newWorkgroup.getMembers().addAll(newMembers);

        // copy the WorkflowUser members into WorkgroupMember members for persistence through OJB
        newWorkgroup.getWorkgroupMembers().clear();
        for (Recipient recipient : newWorkgroup.getMembers()) {
        	if (recipient instanceof WorkflowUser) {
        		WorkflowUser user = (WorkflowUser) recipient;
        		BaseWorkgroupMember member = new BaseWorkgroupMember();
        		member.setWorkflowId(user.getWorkflowId());
        		member.setMemberType(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
        		member.setWorkgroup(newWorkgroup);
        		member.setWorkgroupVersionNumber(newWorkgroup.getVersionNumber());
        		member.setWorkgroupId(newWorkgroup.getWorkgroupId());
        		newWorkgroup.getWorkgroupMembers().add(member);
        	}
		}
        workgroupService.save(newWorkgroup);
        // make the old workgroup non-current
        oldWorkgroup.setCurrentInd(Boolean.FALSE);
        workgroupService.save(oldWorkgroup);

        // verify that the new workgroup is sane...
        Workgroup loadedNewWorkgroup = workgroupService.getWorkgroup(new WorkflowGroupId(newWorkgroup.getWorkgroupId()));
        boolean foundUser1 = false;
        boolean foundUser2 = false;
        assertEquals("Workgroup should have 6 members.", 6, loadedNewWorkgroup.getUsers().size());
        for (Iterator iterator = loadedNewWorkgroup.getUsers().iterator(); iterator.hasNext();) {
			WorkflowUser user = (WorkflowUser) iterator.next();
			if (user.getAuthenticationUserId().equals(user1.getAuthenticationUserId())) {
				foundUser1 = true;
			} else if (user.getAuthenticationUserId().equals(user2.getAuthenticationUserId())) {
				foundUser2 = true;
			}
		}
        assertTrue("Did not find user 1 on workgroup.", foundUser1);
        assertTrue("Did not find user 2 on workgroup.", foundUser2);

        KEWServiceLocator.getActionListService().updateActionItemsForWorkgroupChange(oldWorkgroup, newWorkgroup);

        Collection actionItems = KEWServiceLocator.getActionListService().findByRouteHeaderId(document.getRouteHeaderId());
        boolean foundrkirkend = false;
        boolean foundlshen = false;
        boolean founduser1 = false;
        boolean founduser2 = false;

        for (Iterator iter = actionItems.iterator(); iter.hasNext();) {
            ActionItem actionItem = (ActionItem) iter.next();
            String authId = actionItem.getUser().getAuthenticationUserId().getAuthenticationId();
            if (authId.equals("rkirkend")) {
                foundrkirkend = true;
            } else if (authId.equals("user1")) {
                founduser1 = true;
            } else if (authId.equals("lshen")) {
                foundlshen = true;
            } else if (authId.equals("user2")) {
                founduser2 = true;
            }
        }

        assertTrue("rkirkend should still have an AI because he is in 2 workgroups that are routed to.", foundrkirkend);
        assertTrue("user1 should have an AI because they were added to 'WorkflowAdmin'", founduser1);
        assertTrue("user2 should have an AI because they were added to 'WorkflowAdmin'", founduser2);
        assertFalse("lshen should not have an AI because they were removed from 'WorkflowAdmin'", foundlshen);

    }

    /**
     * When workgroup membership changes all action items to that workgroup need to reflect
     * the new membership even in the case of nested workgroups.
     *
     * @throws Exception
     */
    @Test public void testUpdateActionItemsForNestedWorkgroupChange() throws Exception {

        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), "ActionItemDocumentType");
        document.setTitle("");
        document.appSpecificRouteDocumentToWorkgroup(EdenConstants.ACTION_REQUEST_APPROVE_REQ, "", new WorkgroupNameIdVO("AIWGNested2"), "", true);
        document.routeDocument("");

        // remove a user from the AGWG1 workgroup
        WorkflowUser ewestfal = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("ewestfal"));
        BaseWorkgroup workgroup1 = (BaseWorkgroup)KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("AIWG1"));
        assertNotNull(workgroup1);
        BaseWorkgroup oldWorkgroup1 = copy(workgroup1);

        assertEquals("Workgroup should have 2 members.", 2, workgroup1.getWorkgroupMembers().size());
        for (Iterator iterator = workgroup1.getWorkgroupMembers().iterator(); iterator.hasNext();) {
			BaseWorkgroupMember member = (BaseWorkgroupMember) iterator.next();
			// remove ewestfal
			if (member.getWorkflowId().equals(ewestfal.getWorkflowId())) {
				iterator.remove();
			}
		}
        workgroup1.getMembers().clear();
        workgroup1.materializeMembers();
        assertEquals("Workgroup should have 1 members.", 1, workgroup1.getWorkgroupMembers().size());
        KEWServiceLocator.getWorkgroupService().save(workgroup1);

        Workgroup workgroupNested2 = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("AIWGNested2"));
        Collection actionItems = KEWServiceLocator.getActionListService().findByRouteHeaderId(document.getRouteHeaderId());
        assertEquals("Should be 6 action items.", 6, actionItems.size());
        boolean foundEwestfal = false;
        for (Iterator iterator = actionItems.iterator(); iterator.hasNext();) {
			ActionItem actionItem = (ActionItem) iterator.next();
			if (actionItem.getWorkflowId().equals(ewestfal.getWorkflowId())) {
				assertEquals("Action Item should be for the AIWGNested2 workgroup", actionItem.getWorkgroupId(), workgroupNested2.getWorkflowGroupId().getGroupId());
				foundEwestfal = true;
			}
		}
        assertTrue("Should have found an action item to ewestfal.", foundEwestfal);

        // now, let's update the action items for our workgroup change
        KEWServiceLocator.getActionListService().updateActionItemsForWorkgroupChange(oldWorkgroup1, workgroup1);

        // there should no longer be a request to ewestfal
        actionItems = KEWServiceLocator.getActionListService().findByRouteHeaderId(document.getRouteHeaderId());
        assertEquals("Should be 5 action items.", 5, actionItems.size());
        foundEwestfal = false;
        for (Iterator iterator = actionItems.iterator(); iterator.hasNext();) {
			ActionItem actionItem = (ActionItem) iterator.next();
			if (actionItem.getWorkflowId().equals(ewestfal.getWorkflowId())) {
				foundEwestfal = true;
			}
		}
        assertFalse("Should not have found an action item to ewestfal.", foundEwestfal);

        // add user user1 to AIWGNested1
        WorkflowUser user1 = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("user1"));
        BaseWorkgroup workgroup2 = (BaseWorkgroup)KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("AIWGNested1"));
        assertNotNull(workgroup2);
        BaseWorkgroup oldWorkgroup2 = copy(workgroup2);
        assertEquals("Workgroup should have 2 members.", 2, workgroup2.getWorkgroupMembers().size());
        assertEquals("Workgroup should have 2 members.", 2, workgroup2.getMembers().size());
        assertEquals("Workgroup should have 2 users.", 2, workgroup2.getUsers().size());
        // add user1
        BaseWorkgroupMember member = new BaseWorkgroupMember();
		member.setWorkflowId(user1.getWorkflowId());
		member.setMemberType(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
		member.setWorkgroup(workgroup2);
		member.setWorkgroupVersionNumber(workgroup2.getVersionNumber());
		member.setWorkgroupId(workgroup2.getWorkgroupId());
		workgroup2.getWorkgroupMembers().add(member);
		workgroup2.getMembers().clear();
		workgroup2.materializeMembers();
		KEWServiceLocator.getWorkgroupService().save(workgroup2);

		// update action items
        KEWServiceLocator.getActionListService().updateActionItemsForWorkgroupChange(oldWorkgroup2, workgroup2);
        // there should now be an action item to user1
        actionItems = KEWServiceLocator.getActionListService().findByRouteHeaderId(document.getRouteHeaderId());
        assertEquals("Should be 6 action items.", 6, actionItems.size());
        boolean foundUser1 = false;
        for (Iterator iterator = actionItems.iterator(); iterator.hasNext();) {
			ActionItem actionItem = (ActionItem) iterator.next();
			if (actionItem.getWorkflowId().equals(user1.getWorkflowId())) {
				foundUser1 = true;
			}
		}
        assertTrue("Should have found an action item to user1.", foundUser1);

        // remove a user from the AIWGNested1 workgroup from AIWGNested2
        Workgroup aiwgNested1Workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("AIWGNested1"));
        BaseWorkgroup workgroup3 = (BaseWorkgroup)KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("AIWGNested2"));
        assertNotNull(workgroup3);
        BaseWorkgroup oldWorkgroup3 = copy(workgroup3);

        assertEquals("Workgroup should have 3 members.", 3, workgroup3.getWorkgroupMembers().size());
        assertEquals("Workgroup should have 6 users.", 6, workgroup3.getUsers().size());
        for (Iterator iterator = workgroup3.getWorkgroupMembers().iterator(); iterator.hasNext();) {
			member = (BaseWorkgroupMember) iterator.next();
			// remove ewestfal
			if (member.getMemberType().equals(EdenConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD) &&
					member.getWorkflowId().equals(aiwgNested1Workgroup.getWorkflowGroupId().getGroupId().toString())) {
				iterator.remove();
			}
		}
        workgroup3.getMembers().clear();
        workgroup3.materializeMembers();
        assertEquals("Workgroup should have 2 members.", 2, workgroup3.getWorkgroupMembers().size());
        assertEquals("Workgroup should have 3 users.", 3, workgroup3.getUsers().size());
        KEWServiceLocator.getWorkgroupService().save(workgroup3);

        actionItems = KEWServiceLocator.getActionListService().findByRouteHeaderId(document.getRouteHeaderId());
        assertEquals("Should be 6 action items.", 6, actionItems.size());

        // now, let's update the action items for our workgroup change
        KEWServiceLocator.getActionListService().updateActionItemsForWorkgroupChange(oldWorkgroup3, workgroup3);

        // there should only be 3 action items now
        actionItems = KEWServiceLocator.getActionListService().findByRouteHeaderId(document.getRouteHeaderId());
        assertEquals("Should be 3 action items.", 3, actionItems.size());
        WorkflowUser pmckown = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("pmckown"));
        WorkflowUser jhopf = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("jhopf"));
        WorkflowUser bmcgough = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("bmcgough"));
        boolean foundPmckown = false;
        boolean foundJhopf = false;
        boolean foundBmcgough = false;
        for (Iterator iterator = actionItems.iterator(); iterator.hasNext();) {
			ActionItem actionItem = (ActionItem) iterator.next();
			if (actionItem.getWorkflowId().equals(pmckown.getWorkflowId())) {
				foundPmckown = true;
			} if (actionItem.getWorkflowId().equals(jhopf.getWorkflowId())) {
				foundJhopf = true;
			} if (actionItem.getWorkflowId().equals(bmcgough.getWorkflowId())) {
				foundBmcgough = true;
			}
		}
        assertTrue("Should have found an action item to pmckown.", foundPmckown);
        assertTrue("Should have found an action item to jhopf.", foundJhopf);
        assertTrue("Should have found an action item to bmcgough.", foundBmcgough);


//        WorkgroupService workgroupService = KEWServiceLocator.getWorkgroupService();
//        BaseWorkgroup oldWorkgroup = (BaseWorkgroup)workgroupService.getWorkgroup(new GroupNameId("WorkflowAdmin"));
//        WorkflowUser user1 = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("user1"));
//        WorkflowUser user2 = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("user2"));
//
//        BaseWorkgroup newWorkgroup = (BaseWorkgroup)KEWServiceLocator.getWorkgroupService().getBlankWorkgroup();
//        newWorkgroup.setGroupNameId(oldWorkgroup.getGroupNameId());
//        newWorkgroup.setActiveInd(Boolean.TRUE);
//        newWorkgroup.setCurrentInd(Boolean.TRUE);
//        newWorkgroup.setVersionNumber(new Integer(oldWorkgroup.getVersionNumber().intValue()+1));
//        newWorkgroup.setLockVerNbr(new Integer(oldWorkgroup.getLockVerNbr().intValue()));
//        newWorkgroup.setWorkflowGroupId(oldWorkgroup.getWorkflowGroupId());
//        newWorkgroup.setWorkgroupId(oldWorkgroup.getWorkflowGroupId().getGroupId());
//        newWorkgroup.setGroupNameId(new GroupNameId(oldWorkgroup.getGroupNameId().getNameId()));
//        newWorkgroup.setMembers(new ArrayList<Recipient>(oldWorkgroup.getMembers()));
//
//        List<WorkflowUser> usersToRemove = new ArrayList<WorkflowUser>();
//        //remove 'rkirkend' and 'shenl' from the workgroup
//        for (Recipient recipient : oldWorkgroup.getMembers()) {
//        	if (recipient instanceof WorkflowUser) {
//        		WorkflowUser user = (WorkflowUser)recipient;
//        		if (user.getAuthenticationUserId().getAuthenticationId().equals("rkirkend") || user.getAuthenticationUserId().getAuthenticationId().equals("shenl")) {
//        			usersToRemove.add(user);
//        		}
//        	}
//        }
//
//        newWorkgroup.getMembers().removeAll(usersToRemove);
//
//        List<WorkflowUser> newMembers = new ArrayList<WorkflowUser>();
//        newMembers.add(user1);
//        newMembers.add(user2);
//        newWorkgroup.getMembers().addAll(newMembers);
//
//        // copy the WorkflowUser members into WorkgroupMember members for persistence through OJB
//        newWorkgroup.getWorkgroupMembers().clear();
//        for (Recipient recipient : newWorkgroup.getMembers()) {
//        	if (recipient instanceof WorkflowUser) {
//        		WorkflowUser user = (WorkflowUser) recipient;
//        		BaseWorkgroupMember member = new BaseWorkgroupMember();
//        		member.setWorkflowId(user.getWorkflowId());
//        		member.setMemberType(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
//        		member.setWorkgroup(newWorkgroup);
//        		member.setWorkgroupVersionNumber(newWorkgroup.getVersionNumber());
//        		member.setWorkgroupId(newWorkgroup.getWorkgroupId());
//        		newWorkgroup.getWorkgroupMembers().add(member);
//        	}
//		}
//        workgroupService.save(newWorkgroup);
//        // make the old workgroup non-current
//        oldWorkgroup.setCurrentInd(Boolean.FALSE);
//        workgroupService.save(oldWorkgroup);
//
//        // verify that the new workgroup is sane...
//        Workgroup loadedNewWorkgroup = workgroupService.getWorkgroup(new WorkflowGroupId(newWorkgroup.getWorkgroupId()));
//        boolean foundUser1 = false;
//        boolean foundUser2 = false;
//        assertEquals("Workgroup should have 6 members.", 6, loadedNewWorkgroup.getUsers().size());
//        for (Iterator iterator = loadedNewWorkgroup.getUsers().iterator(); iterator.hasNext();) {
//			WorkflowUser user = (WorkflowUser) iterator.next();
//			if (user.getAuthenticationUserId().equals(user1.getAuthenticationUserId())) {
//				foundUser1 = true;
//			} else if (user.getAuthenticationUserId().equals(user2.getAuthenticationUserId())) {
//				foundUser2 = true;
//			}
//		}
//        assertTrue("Did not find user 1 on workgroup.", foundUser1);
//        assertTrue("Did not find user 2 on workgroup.", foundUser2);
//
//        KEWServiceLocator.getActionListService().updateActionItemsForWorkgroupChange(oldWorkgroup, newWorkgroup);
//
//        Collection actionItems = KEWServiceLocator.getActionListService().findByRouteHeaderId(document.getRouteHeaderId());
//        boolean foundrkirkend = false;
//        boolean foundlshen = false;
//        boolean founduser1 = false;
//        boolean founduser2 = false;
//
//        for (Iterator iter = actionItems.iterator(); iter.hasNext();) {
//            ActionItem actionItem = (ActionItem) iter.next();
//            String authId = actionItem.getUser().getAuthenticationUserId().getAuthenticationId();
//            if (authId.equals("rkirkend")) {
//                foundrkirkend = true;
//            } else if (authId.equals("user1")) {
//                founduser1 = true;
//            } else if (authId.equals("lshen")) {
//                foundlshen = true;
//            } else if (authId.equals("user2")) {
//                founduser2 = true;
//            }
//        }
//
//        assertTrue("rkirkend should still have an AI because he is in 2 workgroups that are routed to.", foundrkirkend);
//        assertTrue("user1 should have an AI because they were added to 'WorkflowAdmin'", founduser1);
//        assertTrue("user2 should have an AI because they were added to 'WorkflowAdmin'", founduser2);
//        assertFalse("lshen should not have an AI because they were removed from 'WorkflowAdmin'", foundlshen);

    }

    /**
     * addresses the following bug http://fms.dfa.cornell.edu:8080/browse/KULWF-428
     *
     * @throws Exception
     */
    @Test public void testWorkgroupActionItemGenerationWhenMultipleWorkgroupRequests() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), "ActionItemDocumentType");
        document.setTitle("");
        document.routeDocument("");

        document = new WorkflowDocument(new NetworkIdVO("jitrue"), document.getRouteHeaderId());

        ActionRequestVO[] ars = document.getActionRequests();
        boolean routedWorkflowAdmin = false;
        boolean routedTestWorkgroup = false;
        for (int i = 0; i < ars.length; i++) {
            ActionRequestVO request = ars[i];
            if (request.isWorkgroupRequest() && request.getWorkgroupVO().getWorkgroupName().equals("TestWorkgroup")) {
                routedTestWorkgroup = true;
            } else if (request.isWorkgroupRequest() && request.getWorkgroupVO().getWorkgroupName().equals("WorkflowAdmin")) {
                routedWorkflowAdmin = true;
            }
        }

        //verify that our test is sane
        assertTrue("Should have routed to 'TestWorkgroup'", routedTestWorkgroup);
        assertTrue("Should have routed to 'WorkflowAdmin'", routedWorkflowAdmin);
        assertTrue("Approve should be requested to member of 'TestWorkgroup'", document.isApprovalRequested());

        document.approve("");

        Collection actionItems = KEWServiceLocator.getActionListService().findByRouteHeaderId(document.getRouteHeaderId());

        assertEquals("There should be 6 action items to the WorkflowAdmin.", 6, actionItems.size());

        for (Iterator iter = actionItems.iterator(); iter.hasNext();) {
            ActionItem actionItem = (ActionItem)iter.next();
            //don't worry about which workgroup - they can get activated in any order
            assertNotNull("this should be a workgroup request", actionItem.getWorkgroup());
        }
    }

    /**
     * This test verifies that if someone gets more than one request routed to them then they will get
     * multiple Action Items but only one of them will show up in their Action List.
     */
    @Test public void testMultipleActionItemGeneration() throws Exception {
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), "ActionItemDocumentType");
        document.setTitle("");
        document.routeDocument("");

        // now the document should be at both the WorkflowAdmin workgroup and the TestWorkgroup
        // ewestfal is a member of both Workgroups so verify that he has two action items
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("ewestfal"));
        Collection actionItems = KEWServiceLocator.getActionListService().findByWorkflowUserRouteHeaderId(user.getWorkflowId(), document.getRouteHeaderId());
        assertEquals("Ewestfal should have two action items.", 2, actionItems.size());

        // now check the action list, there should be only one entry
        actionItems = KEWServiceLocator.getActionListService().getActionList(user, null);
        assertEquals("Ewestfal should have one action item in his action list.", 1, actionItems.size());
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
        assertTrue("Ewestfal should have an approval requested.", document.isApprovalRequested());

        // approve as a member from the first workgroup
        document = new WorkflowDocument(new NetworkIdVO("jitrue"), document.getRouteHeaderId());
        assertTrue("Jitrue should have an approval requested.", document.isApprovalRequested());
        document.approve("");

        // now ewestfal should have only one action item in both his action items and his action list
        actionItems = KEWServiceLocator.getActionListService().findByWorkflowUserRouteHeaderId(user.getWorkflowId(), document.getRouteHeaderId());
        assertEquals("Ewestfal should have one action item.", 1, actionItems.size());
        Long actionItemId = ((ActionItem)actionItems.iterator().next()).getActionItemId();
        actionItems = KEWServiceLocator.getActionListService().getActionList(user, null);
        assertEquals("Ewestfal should have one action item in his action list.", 1, actionItems.size());
        assertEquals("The two action items should be the same.", actionItemId, ((ActionItem)actionItems.iterator().next()).getActionItemId());
    }

    /**
     * This tests verifies that bug KULWF-507 has been fixed:
     *
     * https://test.kuali.org/jira/browse/KULWF-507
     *
     * To fix this, we implemented the system so that multiple action items are generated rather then just
     * one which gets reassigned across multiple requests as needed.
     *
     * This test verifies that after the blanket approval, there should no longer be an orphaned Acknowledge
     * request.  The workgroup used here is the TestWorkgroup and "user1" is ewestfal with "user2" as rkirkend.
     *
     * The routing is configured in the BAOrphanedRequestDocumentType.
     */
    @Test public void testOrphanedAcknowledgeFromBlanketApprovalFix() throws Exception {
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "BAOrphanedRequestDocumentType");
    	document.blanketApprove("");
    	assertTrue("Document should be processed.", document.stateIsProcessed());

    	// after the document has blanket approved there should be 2 action items since the blanket approver
    	// is in the final workgroup.  These action items should be the acknowledges generated to both
    	// rkirkend and user1
    	int numActionItems = actionListService.findByRouteHeaderId(document.getRouteHeaderId()).size();
    	assertEquals("Incorrect number of action items.", 2, numActionItems);

    	WorkflowUser user1 = KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdVO("user1"));
    	WorkflowUser rkirkend = KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdVO("rkirkend"));

    	// check that user1 has 1 action item
    	Collection actionItems = actionListService.findByWorkflowUserRouteHeaderId(user1.getWorkflowId(), document.getRouteHeaderId());
    	assertEquals("user1 should have one action item.", 1, actionItems.size());

    	// check that rkirkend still has 1, the is where the bug would have manifested itself before, rkirkend would have had
    	// no action item (hence the orphaned request)
    	actionItems = actionListService.findByWorkflowUserRouteHeaderId(rkirkend.getWorkflowId(), document.getRouteHeaderId());
    	assertEquals("rkirkend should have one action item.", 1, actionItems.size());

    	// lets go ahead and take it to final for funsies
    	document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
    	assertTrue("Should have ack request.", document.isAcknowledgeRequested());
    	document.acknowledge("");
    	assertTrue("Should still be PROCESSED.", document.stateIsProcessed());

    	document = new WorkflowDocument(new NetworkIdVO("user1"), document.getRouteHeaderId());
    	assertTrue("Should have ack request.", document.isAcknowledgeRequested());
    	document.acknowledge("");
    	assertTrue("Should now be FINAL.", document.stateIsFinal());
    }

    /**
     * Executes a deep copy of the BaseWorkgroup
     */
    private BaseWorkgroup copy(BaseWorkgroup workgroup) throws Exception {
    	BaseWorkgroup workgroupCopy = (BaseWorkgroup)KEWServiceLocator.getWorkgroupService().copy(workgroup);
    	// copy above does a shallow copy so we need to deep copy members
    	List<BaseWorkgroupMember> members = workgroupCopy.getWorkgroupMembers();
    	List<BaseWorkgroupMember> membersCopy = new ArrayList<BaseWorkgroupMember>();
    	for (BaseWorkgroupMember member : members) {
    		membersCopy.add(copy(member));
    	}
    	workgroupCopy.setWorkgroupMembers(membersCopy);
    	workgroupCopy.setMembers(new ArrayList<Recipient>());
    	workgroupCopy.materializeMembers();
    	return workgroupCopy;
    }

    private BaseWorkgroupMember copy(BaseWorkgroupMember member) throws Exception {
    	return (BaseWorkgroupMember)BeanUtils.cloneBean(member);
    }

}