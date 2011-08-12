/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.service.impl;

import com.google.common.collect.Maps;
import org.junit.Test;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.KimApiConstants;
import org.kuali.rice.kim.impl.role.RoleServiceImpl;
import org.kuali.rice.kim.test.KIMTestCase;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;


public class RoleServiceImplTest extends KIMTestCase {

	private RoleServiceImpl roleService;

	public void setUp() throws Exception {
		super.setUp();
		roleService = (RoleServiceImpl) GlobalResourceLoader.getService(
                new QName(KimApiConstants.Namespaces.KIM_NAMESPACE_2_0, KimApiConstants.ServiceNames.ROLE_SERVICE_SOAP));
	}

	@Test
	public void testPrincipaHasRoleOfDirectAssignment() {
		List <String>roleIds = new ArrayList<String>();
		roleIds.add("r1");
		assertTrue( "p1 has direct role r1", roleService.principalHasRole("p1", roleIds, null ));	
		//assertFalse( "p4 has no direct/higher level role r1", roleService.principalHasRole("p4", roleIds, null ));
		Map<String, String> qualification = new HashMap<String, String>();
		qualification.put("Attribute 2", "CHEM");
		assertTrue( "p1 has direct role r1 with rp2 attr data", roleService.principalHasRole("p1", roleIds, qualification));
		qualification.clear();
		//requested qualification rolls up to a higher element in some hierarchy 
		// method not implemented yet, not quite clear how this works
		qualification.put("Attribute 3", "PHYS");
		assertTrue( "p1 has direct role r1 with rp2 attr data", roleService.principalHasRole("p1", roleIds, Maps.newHashMap(
                qualification)));
	}

	@Test
	public void testPrincipalHasRoleOfHigherLevel() {
		// "p3" is in "r2" and "r2 contains "r1"
		List <String>roleIds = new ArrayList<String>();
		roleIds.add("r2");
		assertTrue( "p1 has assigned in higher level role r1", roleService.principalHasRole("p1", roleIds, null ));		
	}
	
	@Test
	public void testPrincipalHasRoleContainsGroupAssigned() {
		// "p2" is in "g1" and "g1" assigned to "r2"
		List <String>roleIds = new ArrayList<String>();
		roleIds.add("r2");
		assertTrue( "p2 is assigned to g1 and g1 assigned to r2", roleService.principalHasRole("p2", roleIds, null ));		
	}
	
	/**
	 * Tests to ensure that a circular role membership cannot be created via the RoleService.
	 * 
	 * @throws Exception
	 */
	@Test (expected=IllegalArgumentException.class)
	public void testCircularRoleAssignment() {
		Map<String, String> map = new HashMap<String, String>();
		List <String>roleIds = new ArrayList<String>();
		roleIds.add("r1");
		roleService.assignRoleToRole("r5", "AUTH_SVC_TEST2", "RoleThree", map);
	}
}
