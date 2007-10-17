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
import org.kuali.rice.kim.service.QualifiedRoleService;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

/**
 * Basic test to verify we can access the QualifiedRoleService through the GRL. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class QualifiedRoleServiceTest extends KIMTestCase {
    private QualifiedRoleService qualifiedRoleService;

    public void setUp() throws Exception {
	super.setUp();
	qualifiedRoleService = (QualifiedRoleService)GlobalResourceLoader.getService("qualifiedRoleService");
    }
    @Test public void testGroupTestHarness() throws Exception {
	assertNotNull(qualifiedRoleService);
    }
    @Test public void testGetGroupNames() throws Exception {
	assertEquals(0, qualifiedRoleService.getGroupNames("test", 
		new HashMap<String, String>()).size());
    }
    @Test public void testGetGroups() throws Exception {
	assertEquals(0, qualifiedRoleService.getGroups("test", 
		new HashMap<String, String>()).size());
    }
    @Test public void testGetPersonUsernames() throws Exception {
	assertEquals(0, qualifiedRoleService.getPersonUsernames("test", 
		new HashMap<String, String>()).size());
    }
    @Test public void testGetPersons() throws Exception {
	assertEquals(0, qualifiedRoleService.getPersons("test", 
		new HashMap<String, String>()).size());
    }
    @Test public void testGetRoleNames() throws Exception {
	assertEquals(0, qualifiedRoleService.getRoleNames(new HashMap<String, String>()).size());
    }
    @Test public void testGetRoles() throws Exception {
	assertEquals(0, qualifiedRoleService.getRoles(new HashMap<String, String>()).size());
    }
}

