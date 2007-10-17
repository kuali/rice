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

import org.junit.Test;
import org.kuali.rice.kim.service.KIMService;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

/**
 * Basic test to verify we can access the KIMService through the GRL. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KIMServiceTest extends KIMTestCase {
    private KIMService kimService;
    
	public void setUp() throws Exception {
	    super.setUp();
	    kimService = (KIMService)GlobalResourceLoader.getService("kimService");
	}

    /**
     * This method ...
     * 
     * @throws Exception
     */
    @Test public void testKimTestHarness() throws Exception {
	assertNotNull(kimService);
    }
    @Test public void testGetPersons() throws Exception {	
	assertEquals(0, kimService.getPersons().size());

    } 
    @Test public void testGetGroups() throws Exception {
	assertEquals(0, kimService.getGroups().size());

    }
    @Test public void testGetGroupNames() throws Exception {
	assertEquals(0, kimService.getGroupNames().size());

    }
    @Test public void testGetNamespaceNames() throws Exception {
	assertEquals(0, kimService.getNamespaceNames().size());

    }
    @Test public void testGetNamespaces() throws Exception {
	assertEquals(0, kimService.getNamespaces().size());

    }
    @Test public void testGetPersonUsernames() throws Exception {
	assertEquals(0, kimService.getPersonUsernames().size());

    }
    
    
}
