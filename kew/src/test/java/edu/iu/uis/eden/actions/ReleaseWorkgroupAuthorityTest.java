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


import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionlist.ActionListService;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowGroupIdVO;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class ReleaseWorkgroupAuthorityTest extends KEWTestCase {
    
    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }
    
    @Test public void testReleaseWorkgroupAuthority() throws Exception {
        WorkflowDocument doc = new WorkflowDocument(new NetworkIdVO("user1"), TakeWorkgroupAuthorityTest.DOC_TYPE);
        doc.routeDocument("");
        
        Workgroup workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("TestWorkgroup"));
        
        //have member rkirkend take authority
        doc = new WorkflowDocument(new NetworkIdVO("rkirkend"), doc.getRouteHeaderId());
        doc.takeWorkgroupAuthority("", new WorkflowGroupIdVO(workgroup.getWorkflowGroupId().getGroupId()));

        //have rkirkend release authority
        doc = new WorkflowDocument(new NetworkIdVO("rkirkend"), doc.getRouteHeaderId());
        doc.releaseWorkgroupAuthority("", new WorkflowGroupIdVO(workgroup.getWorkflowGroupId().getGroupId()));
        
        //verify that all members have the action item
        ActionListService aiService = KEWServiceLocator.getActionListService();
        Collection actionItems = aiService.findByRouteHeaderId(doc.getRouteHeaderId());
        assertTrue("There should be more than one action item", actionItems.size() > 1);
        for (Iterator iter = actionItems.iterator(); iter.hasNext();) {
            ActionItem actionItem = (ActionItem) iter.next();
            assertTrue("Action Item not to workgroup member", TakeWorkgroupAuthorityTest.WORKGROUP_MEMBERS.contains(actionItem.getUser().getAuthenticationUserId().getAuthenticationId()));
        }
    }   
}