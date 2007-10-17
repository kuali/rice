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
import org.kuali.rice.kim.service.NamespaceService;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

/**
 * Basic test to verify we can access the NamespaceService through the GRL. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class NamespaceServiceTest extends KIMTestCase {
    private NamespaceService namespaceService;

    public void setUp() throws Exception {
	super.setUp();
	namespaceService = (NamespaceService)GlobalResourceLoader.getService("namespaceService");
    }
    @Test public void testGroupTestHarness() throws Exception {
	assertNotNull(namespaceService);
    }
    @Test public void testGetPermissionNames() throws Exception {
	assertEquals(0, namespaceService.getPermissionNames("test").size());
    }
    @Test public void testGetPermissions() throws Exception {
	assertEquals(0, namespaceService.getPermissions("test").size());
    }
}