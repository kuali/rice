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
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
/**
 * Basic test to verify we can access the PersonService through the GRL.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PersonServiceTest extends KIMTestCase {
    private PersonService personService;
    
	public void setUp() throws Exception {
	    super.setUp();
	    personService = (PersonService)GlobalResourceLoader.getService("personService");
	}

/**
 * This method ...
 * 
 * @throws Exception
 */
	@Test public void testPersonTestHarness() throws Exception {
	    assertNotNull(personService);
	}
	@Test public void testGetAttributeValue() throws Exception {
	    assertEquals(0, personService.getAttributeValue("test", "test").length());

	} 
	@Test public void testGetPersonUserNames() throws Exception {	
	    assertEquals(0, personService.getPersonUserNames(new HashMap<String, String>()).size());

	} 
	@Test public void testGetPersons() throws Exception {	
	    assertEquals(0, personService.getPersonUserNames(new HashMap<String, String>()).size());

	} 
	@Test public void testHasAttributes() throws Exception {	
	    assertEquals(false, personService.hasAttributes("test", new HashMap<String, String>()));

	} 
	@Test public void testHasPermission() throws Exception {	
	    assertEquals(false, personService.hasPermission("test", "test"));

	} 
	@Test public void testHasQualifiedPermission() throws Exception {	
	    assertEquals(false, personService.hasQualifiedPermission("test", "test",
		    new HashMap<String, String>()));

	} 
	@Test public void testHasQualifiedRole() throws Exception {	
	    assertEquals(false, personService.hasQualifiedRole("test", "test", 
		    new HashMap<String, String>()));

	} 
	@Test public void testHasRole() throws Exception {	
	    assertEquals(false, personService.hasRole("test", "test")); 

	} 
	@Test public void testIsMemberOfGroup() throws Exception {	
	    assertEquals(false, personService.isMemberOfGroup("test", "test")); 

	} 
}
