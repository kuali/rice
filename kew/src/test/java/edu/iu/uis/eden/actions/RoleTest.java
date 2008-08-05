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
import java.util.List;

import org.junit.Test;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.actionrequests.ActionRequestValue;
import org.kuali.rice.kew.clientapp.WorkflowDocument;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.actions.BlanketApproveTest.NotifySetup;

public class RoleTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }

    @Test public void testRoleRequestGeneration() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        
        document = new WorkflowDocument(new NetworkIdDTO("jhopf"), document.getRouteHeaderId());
        assertTrue("This user should have an approve request", document.isApprovalRequested());
        document.approve("");
        
        document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), document.getRouteHeaderId());
        assertTrue("This user should have an approve request", document.isApprovalRequested());
        document.approve("");//ewestfal had ignore previous rule
        
        document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), document.getRouteHeaderId());
        assertTrue("This user should have an approve request", document.isApprovalRequested());
        document.approve("");
        
        //this be the role delegate of jitrue
        document = new WorkflowDocument(new NetworkIdDTO("natjohns"), document.getRouteHeaderId());
        assertTrue("This user should have an approve request", document.isApprovalRequested());
        document.approve("");
        
        document = new WorkflowDocument(new NetworkIdDTO("bmcgough"), document.getRouteHeaderId());
        document.approve("");
        
        document = new WorkflowDocument(new NetworkIdDTO("xqi"), document.getRouteHeaderId());
        document.acknowledge("");
        
        assertTrue("Document should be final", document.stateIsFinal());

        List requests = KEWServiceLocator.getActionRequestService().findAllActionRequestsByRouteHeaderId(document.getRouteHeaderId());
        List rootRequests = KEWServiceLocator.getActionRequestService().getRootRequests(requests);
        
        //verify our requests have been made correctly
        for (Iterator iter = rootRequests.iterator(); iter.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iter.next();
			if (request.isRoleRequest()) {
				//direct children should not be role requests
				iterateChildrenRequests(request.getChildrenRequests(), new String[] {"U", "W"}, request);
			}
		}
    }
	
    private void iterateChildrenRequests(Collection childrenRequests, String[] requestTypes, ActionRequestValue parentRequest) {
    	for (Iterator iter = childrenRequests.iterator(); iter.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iter.next();
			boolean matched = false;
			for (int i = 0; i < requestTypes.length; i++) {
				if (request.getRecipientTypeCd().equals(requestTypes[i])) {
					matched = true;
				}
			}
			if (!matched) {
				fail("Didn't find request of types expected Recipient Type: " + parentRequest.getRecipientTypeCd() + " RoleName: " + parentRequest.getRoleName() + " Qualified Role Name:" + parentRequest.getQualifiedRoleName() + " RuleId: " + parentRequest.getRuleBaseValuesId());
			}
			//if this is a role then it can't have a child role
			if (request.isRoleRequest()) {
				//direct children should not be role requests
				iterateChildrenRequests(request.getChildrenRequests(), new String[] {"U", "W"}, request);
			}
		}
    }
}