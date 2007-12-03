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
package org.kuali.rice.kim.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.GroupQualifiedRole;
import org.kuali.rice.kim.bo.Permission;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.PersonQualifiedRole;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

/**
 * Basic test to verify we can access the QualifiedRoleService through the GRL. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoleServiceTest extends KIMTestCase {
    private RoleService roleService;

    public void setUp() throws Exception {
	super.setUp();
	roleService = (RoleService)GlobalResourceLoader.getService("roleService");
    }
    @Test public void testGroupTestHarness() throws Exception {
	assertNotNull(roleService);
    }
    @Test public void testGetGroupNames() throws Exception {
	assertEquals(0, roleService.getGroupNames("test").size());
    }
    @Test public void testGetGroupQualifiedRoles1() throws Exception {
	assertEquals(0, roleService.getGroupQualifiedRoles("test").size());
    }
    @Test public void testGetGroupQualifiedRoles2() throws Exception {
	assertEquals(0, roleService.getGroupQualifiedRoles("test",
		new HashMap<String, String>()).size());
    }
    @Test public void testGetGroups() throws Exception {
	assertEquals(0, roleService.getGroups("test").size());
    }
    @Test public void testGetPermissionNames() throws Exception {
	assertEquals(0, roleService.getPermissionNames("test").size());
    }
    @Test public void testGetPermissions() throws Exception {
	assertEquals(0, roleService.getPermissions("test").size());
    }
    @Test public void testGetPersonQualifiedRoles1() throws Exception {
	assertEquals(0, roleService.getPermissions("test").size());
    }
    @Test public void testGetPersonQualifiedRoles2() throws Exception {
	assertEquals(0, roleService.getPersonQualifiedRoles("test",
		new HashMap<String, String>()).size());
    }
    @Test public void testGetPersonUsernames() throws Exception {
	assertEquals(0, roleService.getPersonUsernames("test").size());
    }
    @Test public void testGetPersons() throws Exception {
	assertEquals(0, roleService.getPersons("test").size());
    }
}
