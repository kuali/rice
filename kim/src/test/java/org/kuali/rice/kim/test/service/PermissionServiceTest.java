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

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.KimPermissionTemplateInfo;
import org.kuali.rice.kim.bo.role.dto.PermissionAssigneeInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.test.KIMTestCase;

/**
 * Test the PermissionService
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PermissionServiceTest extends KIMTestCase {

	private PermissionService permissionService;

	public void setUp() throws Exception {
		super.setUp();
		permissionService = (PermissionService) getKimService(ServiceTestUtils.getConfigProp("kim.test.namespace.permission"),
															  ServiceTestUtils.getConfigProp("kim.test.servicename.permission"),
															  ServiceTestUtils.getConfigProp("kim.test.serviceclass.permission"));
	}

	@Test
	public void testHasPermission() {
		assertTrue(permissionService.hasPermission("entity123pId", "KR-NS", "perm1", new AttributeSet()));
		assertTrue(permissionService.hasPermission("entity123pId", "KR-NS", "perm2", new AttributeSet()));
		assertFalse(permissionService.hasPermission("entity124pId", "KR-NS", "perm2", new AttributeSet()));
	}
	
	@Test
	public void testIsAuthorized() {
		assertTrue(permissionService.isAuthorized("entity123pId", "KR-NS", "perm1", new AttributeSet(), new AttributeSet()));
		assertTrue(permissionService.isAuthorized("entity123pId", "KR-NS", "perm2", new AttributeSet(), new AttributeSet()));
		assertFalse(permissionService.isAuthorized("entity124pId", "KR-NS", "perm2", new AttributeSet(), new AttributeSet()));
	}
	
	@Test
	public void testHasPermissionByTemplateName() {
		assertTrue(permissionService.hasPermissionByTemplateName("entity123pId", "KUALI", "Default", new AttributeSet()));
		// TODO - getting a SOAPFaultException on this call; fix and un-comment
		// assertFalse(permissionService.hasPermissionByTemplateName("entity124pId", "KUALI", "Default", new AttributeSet()));
	}
	
	@Test
	public void testIsAuthorizedByTemplateName() {
		// assertTrue(permissionService.isAuthorizedByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails, qualification)("entity123pId", "KR-NS", "1", new AttributeSet()));
	}
	
	@Test
	public void testGetPermissionAssignees() {
		
		List<PermissionAssigneeInfo> assignees = permissionService.getPermissionAssignees("KUALI", "Log In", null, null);
		assertNotNull(assignees);
		assertEquals(1, assignees.size());
		PermissionAssigneeInfo permInfo = assignees.get(0);
		assertEquals("entity123pId", permInfo.getPrincipalId());
		assignees = permissionService.getPermissionAssignees("KUALI", "Not A Valid Permission Name", null, null);
		// TODO - jax-ws remoted service returns null; local return empty List. Fix webservice return
		assertTrue(null == assignees || assignees.size() == 0);
	}
	
	@Test
	public void testGetPermissionAssigneesForTemplateName() {
		/*
		List<PermissionAssigneeInfo> assignees = permissionService.getPermissionAssignees("KUALI", "Log In", null, null);
		assertNotNull(assignees);
		assertEquals(1, assignees.size());
		PermissionAssigneeInfo permInfo = assignees.get(0);
		assertEquals("entity123pId", permInfo.getPrincipalId());
		assignees = permissionService.getPermissionAssignees("KUALI", "Not A Valid Permission Name", null, null);
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
		KimPermissionInfo permInfo = permissionService.getPermission("p1");
		
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
		
		permInfo = permissionService.getPermission("p0");
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
	
	/**
	 * This method tries to get a client proxy for the specified KIM service
	 * 
	 * @param  class1 - name of the KIM service desired
	 * @return the proxy object
	 * @throws Exception 
	 */
	protected Object getKimService(String svcNamespace, String... svcNames) throws Exception {
		// TODO: local namespace should be a valid, non-partial namespace (unlike 'KIM')
		return GlobalResourceLoader.getService(new QName("KIM", svcNames[0]));
	}
}
