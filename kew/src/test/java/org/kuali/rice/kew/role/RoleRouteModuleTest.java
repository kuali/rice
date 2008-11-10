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
package org.kuali.rice.kew.role;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.role.impl.KimResponsibilityImpl;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.role.impl.ResponsibilityAttributeDataImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.UnitTestFile;

/**
 * Tests Role-based routing integration between KEW and KIM. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@UnitTestData(sqlFiles = {@UnitTestFile(filename = "classpath:org/kuali/rice/kew/role/RoleRouteModuleTest.sql", delimiter = ";")})
public class RoleRouteModuleTest extends KEWTestCase {

	String namespace = "TEST";
	
	protected void loadTestData() throws Exception {
        loadXmlFile("RoleRouteModuleTestConfig.xml");
        KimRoleImpl role = new KimRoleImpl();
        role.setNamespaceCode(namespace);
        role.setRoleDescription("abc");
        role.setRoleName("RoleRouteModuleTestRole");
        KNSServiceLocator.getBusinessObjectService().save(role);
        
        // yikes, there's a lot to set up here, come back to this later
        
//        KimTypeImpl kimTypeImpl = new KimTypeImpl();
//        kimTypeImpl.
//        
//        List<ResponsibilityAttributeDataImpl> detailObjects = new ArrayList<ResponsibilityAttributeDataImpl>();
//        ResponsibilityAttributeDataImpl documentTypeDetail = new ResponsibilityAttributeDataImpl();
//        documentTypeDetail.setAttributeDataId("chart");
//        documentTypeDetail.setAttributeValue("BL");
//        documentTypeDetail.setKimAttribute(attribute);
//        documentTYpeDetail.setKimType(type);
//        
//        KimResponsibilityImpl responsibility = new KimResponsibilityImpl();
//        responsibility.setActive(true);
//        responsibility.setDescription("cba");
//        responsibility.setDetails(detailObjects);
//        responsibility.setName("VoluntaryReview");
//        responsibility.setNamespaceCode(namespace);
//        responsibility.setResponsibilityId("155463");
        
    }
	
	@Test
	public void testRoleRouteModule_FirstApprove() throws Exception {
		WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), "RouteRouteModuleTest1");
		document.routeDocument("");
		
		// in this case we should have a first approve role that contains admin and user2, we
		// should also have a first approve role that contains just user1
		
		document = new WorkflowDocument(new NetworkIdDTO("admin"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdDTO("user1"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdDTO("user2"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());
		
		// examine the action requests
		ActionRequestDTO[] actionRequests = new WorkflowInfo().getActionRequests(document.getRouteHeaderId());
		// there should be 2 root action requests returned here, 1 containing the 2 requests for "BL", and one containing the request for "IN"
		assertEquals("Should have 5 action requests.", 5, actionRequests.length);
		int numRoots = 0;
		for (ActionRequestDTO actionRequest : actionRequests) {
			// each of these should be "first approve"
			if (actionRequest.getApprovePolicy() != null) {
				assertEquals(KEWConstants.APPROVE_POLICY_FIRST_APPROVE, actionRequest.getApprovePolicy());
			}
			if (actionRequest.getParentActionRequestId() == null) {
				numRoots++;
			}
		}
		assertEquals("There should have been 2 root requests.", 2, numRoots);
		
		// let's approve as "user1" and verify the document is still ENROUTE
		document = new WorkflowDocument(new NetworkIdDTO("user1"), document.getRouteHeaderId());
		document.approve("");
		assertTrue("Document should be ENROUTE.", document.stateIsEnroute());
		
		// verify that admin and user2 still have requests
		document = new WorkflowDocument(new NetworkIdDTO("admin"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdDTO("user2"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());
		
		// let's approve as "user2" and verify the document has gone FINAL
		document.approve("");
		assertTrue("Document should be FINAL.", document.stateIsFinal());
		
	}
	
	@Test
	public void testRoleRouteModule_AllApprove() throws Exception {
		WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), "RouteRouteModuleTest2");
		document.routeDocument("");
		
		// in this case we should have all approve roles for admin, user1 and user2
		
		document = new WorkflowDocument(new NetworkIdDTO("admin"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdDTO("user1"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdDTO("user2"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());
		
		// examine the action requests
		ActionRequestDTO[] actionRequests = new WorkflowInfo().getActionRequests(document.getRouteHeaderId());
		assertEquals("Should have 6 action requests.", 6, actionRequests.length);
		int numRoots = 0;
		for (ActionRequestDTO actionRequest : actionRequests) {
			if (actionRequest.getApprovePolicy() != null) {
				assertEquals(KEWConstants.APPROVE_POLICY_ALL_APPROVE, actionRequest.getApprovePolicy());
			}
			if (actionRequest.getParentActionRequestId() == null) {
				numRoots++;
			}
		}
		assertEquals("There should have been 3 root requests.", 3, numRoots);
		
		// let's approve as "user1" and verify the document does NOT go FINAL
		document = new WorkflowDocument(new NetworkIdDTO("user1"), document.getRouteHeaderId());
		document.approve("");
		assertTrue("Document should still be enroute.", document.stateIsEnroute());
		
		// verify that admin and user2 still have requests
		document = new WorkflowDocument(new NetworkIdDTO("admin"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());
		document = new WorkflowDocument(new NetworkIdDTO("user2"), document.getRouteHeaderId());
		assertTrue("Approval should be requested.", document.isApprovalRequested());
		
		// approve as "user2" and verify document is still ENROUTE
		document.approve("");
		assertTrue("Document should be ENROUTE.", document.stateIsEnroute());
		
		// now approve as "admin", coument should be FINAL
		document = new WorkflowDocument(new NetworkIdDTO("admin"), document.getRouteHeaderId());
		document.approve("");
		assertTrue("Document should be FINAL.", document.stateIsFinal());
	}
	
}
