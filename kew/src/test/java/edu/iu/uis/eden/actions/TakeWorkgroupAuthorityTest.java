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
package edu.iu.uis.eden.actions;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionlist.ActionListService;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.ActionTakenVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowGroupIdVO;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 *<authenticationId>ewestfal</authenticationId>
					<authenticationId>rkirkend</authenticationId>
					<authenticationId>jhopf</authenticationId>
					<authenticationId>bmcgough</authenticationId>
					<authenticationId>temay</authenticationId>
					<authenticationId>xqi</authenticationId>
					<authenticationId>natjohns</authenticationId>
					<authenticationId>pmckown</authenticationId>
					<authenticationId>jthomas</authenticationId>
					<authenticationId>jitrue</authenticationId>
 *
 */
public class TakeWorkgroupAuthorityTest extends KEWTestCase {

    public static final String DOC_TYPE = "TakeWorkgroupAuthorityDoc";
    public static List<String> WORKGROUP_MEMBERS = new ArrayList<String>();
    
    static {
        WORKGROUP_MEMBERS.add("ewestfal");
        WORKGROUP_MEMBERS.add("rkirkend");
        WORKGROUP_MEMBERS.add("jhopf");
        WORKGROUP_MEMBERS.add("bmcgough");
        WORKGROUP_MEMBERS.add("temay");
        WORKGROUP_MEMBERS.add("xqi");
        WORKGROUP_MEMBERS.add("natjohns");
        WORKGROUP_MEMBERS.add("pmckown");
        WORKGROUP_MEMBERS.add("jthomas");
        WORKGROUP_MEMBERS.add("jitrue");
    }
    
    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }
    
    @Test public void testTakeWorkgroupAuthorityAction() throws Exception {
        
        WorkflowDocument doc = new WorkflowDocument(new NetworkIdVO("user1"), DOC_TYPE);
        doc.routeDocument("");
        
        Workgroup workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("TestWorkgroup"));
        
        //verify that all members have the action item
        ActionListService aiService = KEWServiceLocator.getActionListService();
        Collection actionItems = aiService.findByRouteHeaderId(doc.getRouteHeaderId());
        assertTrue("There should be more than one action item", actionItems.size() > 1);
        for (Iterator iter = actionItems.iterator(); iter.hasNext();) {
            ActionItem actionItem = (ActionItem) iter.next();
            assertTrue("Action Item not to workgroup member", WORKGROUP_MEMBERS.contains(actionItem.getUser().getAuthenticationUserId().getAuthenticationId()));
        }
        
        //have member rkirkend take authority
        doc = new WorkflowDocument(new NetworkIdVO("rkirkend"), doc.getRouteHeaderId());
        doc.takeWorkgroupAuthority("", new WorkflowGroupIdVO(workgroup.getWorkflowGroupId().getGroupId()));
        
        //verify that only rkirkend has an action item now.
        actionItems = aiService.findByRouteHeaderId(doc.getRouteHeaderId());
        assertEquals("There should only be a single action item to rkirkend", 1, actionItems.size());
        for (Iterator iter = actionItems.iterator(); iter.hasNext();) {
            ActionItem actionItem = (ActionItem) iter.next();
            assertEquals("Action item should be to rkirkend", "rkirkend", actionItem.getUser().getAuthenticationUserId().getAuthenticationId());
        }
        
        //verify the action was recorded and by rkirkend
        WorkflowInfo wUtil = new WorkflowInfo();
        ActionTakenVO[] actionsTaken = wUtil.getActionsTaken(doc.getRouteHeaderId());
        boolean rkirkendATFound = false;
        for (int i = 0; i < actionsTaken.length; i++) {
            ActionTakenVO at = actionsTaken[i];
            if (at.getUserVO().getNetworkId().equals("rkirkend")) {
                assertEquals("Incorrect action code recorded", EdenConstants.ACTION_TAKEN_TAKE_WORKGROUP_AUTHORITY_CD, at.getActionTaken());
                rkirkendATFound = true;
            }
        }
        
        assertTrue("should have found action taken for rkirkend", rkirkendATFound);
    }
}