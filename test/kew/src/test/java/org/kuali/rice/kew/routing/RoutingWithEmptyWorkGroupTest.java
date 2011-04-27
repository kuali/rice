/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.routing;

import org.junit.Test;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;
import org.kuali.rice.kim.api.services.KIMServiceLocator;

import static org.junit.Assert.assertTrue;

public class RoutingWithEmptyWorkGroupTest extends KEWTestCase {

	protected void loadTestData() throws Exception {
		loadXmlFile("RoutingWithEmptyGroupConfig.xml");
	}

	@Test public void testRoutingToEmptyWorkgroup() throws Exception {

		String user1PrincipalId = getPrincipalIdForName("user1");
		String user2PrincipalId = getPrincipalIdForName("user2");
		String user3PrincipalId = getPrincipalIdForName("user3");

		WorkflowDocument doc = new WorkflowDocument(user1PrincipalId, "EmptyWorkgroupDocType");

		doc = new WorkflowDocument("user1", doc.getRouteHeaderId());

		doc.routeDocument("");

		// the document should skip node 1 because it is routing to user1, it should
		// skip node 2 (effectively) because that node is using a group with no members,
		// and then it should land on node 3 being in user 2's action list
		
		doc = new WorkflowDocument(user2PrincipalId, doc.getRouteHeaderId());
		
		assertTrue("Document should be enroute", doc.stateIsEnroute());
		TestUtilities.assertAtNode(doc, "Node3");
		
		TestUtilities.assertInActionList(user2PrincipalId, doc.getRouteHeaderId());
		
		// verify that an action request was generated at Node 2 to the "EmptyWorkgroup" but was immediately deactivated
		ActionRequestDTO[] actionRequests = doc.getActionRequests();
		for (ActionRequestDTO actionRequest : actionRequests) {
			if ("Node2".equals(actionRequest.getNodeName())) {
				assertTrue("action request should be for a group", actionRequest.isGroupRequest());
				assertTrue("action request should be marked as \"done\"", actionRequest.isDone());
				assertTrue("Group should have no members.", KIMServiceLocator.getIdentityManagementService().getGroupMemberPrincipalIds(actionRequest.getGroupId()).isEmpty());
			}
		}
	}
}
