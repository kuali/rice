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
package org.kuali.rice.kim.test.service;

import org.junit.Test;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;

import org.kuali.rice.kim.api.entity.principal.Principal;
import org.kuali.rice.kim.api.entity.services.IdentityService;
import org.kuali.rice.kim.api.entity.type.EntityTypeDataDefault;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

import org.kuali.rice.kim.bo.entity.dto.*;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.kim.service.impl.PersonServiceImpl;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.kim.util.KIMPropertyConstants;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class IdentityServiceTest extends KIMTestCase {

	private IdentityService identityService;

	public void setUp() throws Exception {
		super.setUp();
		if (null == identityService) {
			identityService = findIdSvc();
		}
		GlobalResourceLoader.getService(new QName("KIM", "kimIdentityService"));
	}

	@Test
	public void testGetDefaultNamesForEntityIds(){
		//List<String> entityIds= new ArrayList<String>();
		//entityIds.add("p1");
		//entityIds.add("kuluser");
		List<String> principalIds= new ArrayList<String>();
		principalIds.add("p1");
		principalIds.add("KULUSER");
		List<String> entityIds= new ArrayList<String>();
		for (String principalId : principalIds) {
			KimEntityInfo entityInfo = identityService.getEntityInfoByPrincipalId(principalId);
			entityIds.add(entityInfo.getEntityId());
		}
		
		Map<String,KimEntityNameInfo> results = identityService.getDefaultNamesForEntityIds(entityIds);
		assertEquals(2,results.size());
		for (String entityId : entityIds) {
			assertTrue(results.containsKey(entityId));
		}
		//assertTrue(results.containsKey("kuluser"));
	}
	
	@Test
	public void getDefaultNamesForPrincipalIds(){
		List<String> principalIds= new ArrayList<String>();
		principalIds.add("p1");
		principalIds.add("KULUSER");
		Map<String, KimEntityNamePrincipalNameInfo> results = identityService.getDefaultNamesForPrincipalIds(principalIds);
		assertEquals(2,results.size());
		assertTrue(results.containsKey("p1"));
		assertTrue(results.containsKey("KULUSER"));
	}
	
	@Test
	public void testGetPrincipal() {
		Principal principal = identityService.getPrincipal("KULUSER");
		assertNotNull("principal must not be null", principal);
		assertEquals("Principal name did not match expected result","kuluser", principal.getPrincipalName());
	}

	@Test
	public void testGetPrincipalByPrincipalName() {
		Principal principal = identityService.getPrincipalByPrincipalName("kuluser");
		assertNotNull("principal must not be null", principal);
		assertEquals("Principal ID did not match expected result","KULUSER", principal.getPrincipalId());
	}
	
	@Test
	public void testGetDefaultEntityByPrincipalId() {
		String principalId = "KULUSER";
		KimEntityDefaultInfo info = identityService.getEntityDefaultInfoByPrincipalId(principalId);
		assertNotNull("entity must not be null", info);
		assertNotNull("entity principals must not be null", info.getPrincipals());
		assertEquals("entity must have exactly 1 principal", 1, info.getPrincipals().size());
		for (Principal principalInfo : info.getPrincipals()) {
			assertEquals("Wrong principal id", principalId, principalInfo.getPrincipalId());
		}
		assertTrue("entity external identifiers must not be null", (info.getExternalIdentifiers() == null) || info.getExternalIdentifiers().isEmpty());
	}

	@Test
	public void testGetDefaultEntityByPrincipalName() {
		String principalName = "kuluser";
		KimEntityDefaultInfo info = identityService.getEntityDefaultInfoByPrincipalName(principalName);
		assertNotNull("entity must not be null", info);
		assertNotNull("entity principals must not be null", info.getPrincipals());
		assertEquals("entity must have exactly 1 principal", 1, info.getPrincipals().size());
		for (Principal principalInfo : info.getPrincipals()) {
			assertEquals("Wrong principal name", principalName, principalInfo.getPrincipalName());
		}
		assertTrue("entity external identifiers must not be null", (info.getExternalIdentifiers() == null) || info.getExternalIdentifiers().isEmpty());
	}

	@Test
	public void testGetEntityByPrincipalId() {
		String principalId = "KULUSER";
		KimEntityInfo info = identityService.getEntityInfoByPrincipalId(principalId);
		assertNotNull("entity must not be null", info);
		assertNotNull("entity principals must not be null", info.getPrincipals());
		assertEquals("entity must have exactly 1 principal", 1, info.getPrincipals().size());
		for (Principal principalInfo : info.getPrincipals()) {
			assertEquals("Wrong principal id", principalId, principalInfo.getPrincipalId());
		}
		assertTrue("entity external identifiers must not be null", (info.getExternalIdentifiers() == null) || info.getExternalIdentifiers().isEmpty());
		assertTrue("entity residencies must not be null", (info.getResidencies() == null) || info.getResidencies().isEmpty());
	}

	@Test
	public void testGetEntityByPrincipalName() {
		String principalName = "kuluser";
		KimEntityInfo info = identityService.getEntityInfoByPrincipalName(principalName);
		assertNotNull("entity must not be null", info);
		assertNotNull("entity principals must not be null", info.getPrincipals());
		assertEquals("entity must have exactly 1 principal", 1, info.getPrincipals().size());
		for (Principal principalInfo : info.getPrincipals()) {
			assertEquals("Wrong principal name", principalName, principalInfo.getPrincipalName());
		}
		assertTrue("entity external identifiers must not be null", (info.getExternalIdentifiers() == null) || info.getExternalIdentifiers().isEmpty());
		assertTrue("entity residencies must not be null", (info.getResidencies() == null) || info.getResidencies().isEmpty());
	}

	@Test
	public void testGetContainedAttributes() {
		Principal principal = identityService.getPrincipal("p1");
		
		KimEntityDefaultInfo entity = identityService.getEntityDefaultInfo( principal.getEntityId() );
		assertNotNull( "Entity Must not be null", entity );
		EntityTypeDataDefault eet = entity.getEntityType( "PERSON" );
		assertNotNull( "PERSON EntityEntityType must not be null", eet );
		assertNotNull( "EntityEntityType's default email address must not be null", eet.getDefaultEmailAddress() );
		assertEquals( "p1@kuali.org", eet.getDefaultEmailAddress().getEmailAddressUnmasked() );
	}

	protected Map<String,String> setUpEntityLookupCriteria(String principalId) {
		PersonServiceImpl personServiceImpl = (PersonServiceImpl) KIMServiceLocatorInternal.getService(KimApiServiceLocator.KIM_PERSON_SERVICE);
		Map<String,String> criteria = new HashMap<String,String>(1);
		criteria.put(KIMPropertyConstants.Person.PRINCIPAL_ID, principalId);
		return personServiceImpl.convertPersonPropertiesToEntityProperties(criteria);
	}

	@Test
	public void testLookupEntityDefaultInfo() {
		String principalIdToTest = "p1";
		List<KimEntityDefaultInfo> results = identityService.lookupEntityDefaultInfo(setUpEntityLookupCriteria(principalIdToTest), false);
		assertNotNull("Lookup results should never be null", results);
		assertEquals("Lookup result count is invalid", 1, results.size());
		for (KimEntityDefaultInfo kimEntityDefaultInfo : results) {
			assertEquals("Entity should have only one principal for this test", 1, kimEntityDefaultInfo.getPrincipals().size());
			assertEquals("Principal Ids should match", principalIdToTest, kimEntityDefaultInfo.getPrincipals().get(0).getPrincipalId());
		}
	}

	@Test
	public void testLookupEntityInfo() {
		String principalIdToTest = "p1";
		List<KimEntityInfo> results = identityService.lookupEntityInfo(setUpEntityLookupCriteria(principalIdToTest), false);
		assertNotNull("Lookup results should never be null", results);
		assertEquals("Lookup result count is invalid", 1, results.size());
		for (KimEntityInfo kimEntityInfo : results) {
			assertEquals("Entity should have only one principal for this test", 1, kimEntityInfo.getPrincipals().size());
			assertEquals("Principal Ids should match", principalIdToTest, kimEntityInfo.getPrincipals().get(0).getPrincipalId());
		}
	}

	protected IdentityService findIdSvc() throws Exception {
		return (IdentityService) GlobalResourceLoader.getService(new QName("KIM", "kimIdentityService"));
	}

	protected void setIdentityService(IdentityService idSvc) {
		this.identityService = idSvc;
	}
}
