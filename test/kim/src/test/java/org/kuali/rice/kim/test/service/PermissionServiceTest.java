/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.api.services.KIMServiceLocator;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.KimPermissionTemplateInfo;
import org.kuali.rice.kim.bo.role.dto.PermissionAssigneeInfo;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.test.KIMTestCase;

/**
 * Test the PermissionService
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class PermissionServiceTest extends KIMTestCase {

	private PermissionService permissionService;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		setPermissionService(KIMServiceLocator.getPermissionService());
	}

	@Test
	public void testHasPermission() {
		assertTrue(getPermissionService().hasPermission("entity123pId", "KR-NS", "perm1", new AttributeSet()));
		assertTrue(getPermissionService().hasPermission("entity123pId", "KR-NS", "perm2", new AttributeSet()));
		assertFalse(getPermissionService().hasPermission("entity124pId", "KR-NS", "perm2", new AttributeSet()));
	}
	
	@Test
	public void testIsAuthorized() {
		assertTrue(getPermissionService().isAuthorized("entity123pId", "KR-NS", "perm1", new AttributeSet(), new AttributeSet()));
		assertTrue(getPermissionService().isAuthorized("entity123pId", "KR-NS", "perm2", new AttributeSet(), new AttributeSet()));
		assertFalse(getPermissionService().isAuthorized("entity124pId", "KR-NS", "perm2", new AttributeSet(), new AttributeSet()));
	}
	
	@Test
	public void testHasPermissionByTemplateName() {
		assertTrue(getPermissionService().hasPermissionByTemplateName("entity123pId", "KUALI", "Default", new AttributeSet()));
		// TODO - getting a SOAPFaultException on this call; fix and un-comment
		// assertFalse(getPermissionService().hasPermissionByTemplateName("entity124pId", "KUALI", "Default", new AttributeSet()));
	}
	
	@Test
	public void testIsAuthorizedByTemplateName() {
		// assertTrue(getPermissionService().isAuthorizedByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails, qualification)("entity123pId", "KR-NS", "1", new AttributeSet()));
	}
	
	@Test
	public void testGetPermissionAssignees() {
		
		List<PermissionAssigneeInfo> assignees = getPermissionService().getPermissionAssignees("KUALI", "Log In", null, null);
		assertNotNull(assignees);
		assertEquals(1, assignees.size());
		PermissionAssigneeInfo permInfo = assignees.get(0);
		assertEquals("entity123pId", permInfo.getPrincipalId());
		assignees = getPermissionService().getPermissionAssignees("KUALI", "Not A Valid Permission Name", null, null);
		// TODO - jax-ws remoted service returns null; local return empty List. Fix webservice return
		assertTrue(null == assignees || assignees.size() == 0);
	}
	
	@Test
	public void testGetPermissionAssigneesForTemplateName() {
		/*
		List<PermissionAssigneeInfo> assignees = getPermissionService().getPermissionAssignees("KUALI", "Log In", null, null);
		assertNotNull(assignees);
		assertEquals(1, assignees.size());
		PermissionAssigneeInfo permInfo = assignees.get(0);
		assertEquals("entity123pId", permInfo.getPrincipalId());
		assignees = getPermissionService().getPermissionAssignees("KUALI", "Not A Valid Permission Name", null, null);
		assertNull(assignees);
		*/
	}
	
	@Test
	public void testIsPermissionDefined() {
	}
	
	@Test
	public void testIsPermissionDefinedForTemplateName() {
	}
	
	@Test
	public void testGetAuthorizedPermissions() {
	}
	
	@Test
	public void testGetAuthorizedPermissionsByTemplateName() {
	}
	
	@Test
	public void testGetPermission() {
		KimPermissionInfo permInfo = getPermissionService().getPermission("p1");
		
		assertNotNull(permInfo);
		assertEquals("perm1", permInfo.getName());
		assertEquals("KR-NS", permInfo.getNamespaceCode());
		assertEquals(0, permInfo.getDetails().size());
		assertTrue(permInfo.isActive());
		
		KimPermissionTemplateInfo templateInfo = permInfo.getTemplate();
		assertNotNull(templateInfo);
		assertTrue(templateInfo.isActive());
		assertEquals("1", templateInfo.getKimTypeId());
		assertEquals("Default", templateInfo.getName());
		assertEquals("KUALI", templateInfo.getNamespaceCode());
		
		permInfo = getPermissionService().getPermission("p0");
		assertNull(permInfo);
	}
	
	@Test
	public void testGetPermissionsByTemplateName() {
	}
	
	@Test
	public void testGetPermissionsByName() {
	}
	
	@Test
	public void testLookupPermissions() {
	}
	
	@Test
	public void testGetRoleIdsForPermission() {
	}
	
	@Test
	public void testGetRoleIdsForPermissions() {
	}
	
	@Test
	public void testGetPermissionDetailLabel() {
	}

	public PermissionService getPermissionService() {
		return this.permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

}
