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
import org.kuali.rice.kim.bo.Permission;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

/**
 * Basic test to verify we can access the GroupService through the GRL. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class GroupServiceTest extends KIMTestCase {
    private GroupService groupService;

    public void setUp() throws Exception {
	super.setUp();
	groupService = (GroupService)GlobalResourceLoader.getService("groupService");
    }
    @Test public void testGroupTestHarness() throws Exception {
	assertNotNull(groupService);
    }
    @Test public void testGetGroupNames1() throws Exception {
	assertEquals(0, groupService.getGroupNames("test").size());
    }
    @Test public void testGetGroupNames2() throws Exception {
	assertEquals(0, groupService.getGroupNames(new HashMap<String, String>()).size());
    }
    @Test public void testGetGroups1() throws Exception {
	assertEquals(0, groupService.getGroups("test").size());
    }
    @Test public void testGetGroups2() throws Exception {
	assertEquals(0, groupService.getGroups(new HashMap<String, String>()).size());
    }
    @Test public void testGetPermissionNames() throws Exception {
	assertEquals(0, groupService.getPermissionNames("test").size());
    }
    @Test public void testGetPermissions() throws Exception {
	assertEquals(0, groupService.getPermissions("test").size());
    }
    @Test public void testGetPersonUsernames() throws Exception {
	assertEquals(0, groupService.getPersonUsernames("test").size());
    }
    @Test public void testGetPersons() throws Exception {
	assertEquals(0, groupService.getPersons("test").size());
    }
    @Test public void testGetRoleNames() throws Exception {
	assertEquals(0, groupService.getRoleNames("test").size());
    }
    @Test public void testGetRoles() throws Exception {
	assertEquals(0, groupService.getRoles("test").size());
    }
    @Test public void testHasAttributes() throws Exception {
	assertEquals(false, groupService.hasAttributes("test", new HashMap<String, String>()));
    }
    @Test public void testHasQualifiedPermission() throws Exception {
	assertEquals(false, groupService.hasQualifiedPermission("test", "test", 
		new HashMap<String, String>()));
    }

}
