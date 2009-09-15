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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityEntityTypeDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityNameInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityNamePrincipalNameInfo;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.test.KIMTestCase;

/**
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
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
		List<String> entityIds= new ArrayList<String>();
		entityIds.add("p1");
		entityIds.add("kuluser");
		Map<String,KimEntityNameInfo> results = identityService.getDefaultNamesForEntityIds(entityIds);
		assertEquals(2,results.size());
		assertTrue(results.containsKey("p1"));
		assertTrue(results.containsKey("kuluser"));
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
		KimPrincipal principal = identityService.getPrincipal("KULUSER");
		assertNotNull("principal must not be null", principal);
		assertEquals("Principal name did not match expected result","kuluser", principal.getPrincipalName());
	}

	@Test
	public void testGetPrincipalByPrincipalName() {
		KimPrincipal principal = identityService.getPrincipalByPrincipalName("kuluser");
		assertNotNull("principal must not be null", principal);
		assertEquals("Principal ID did not match expected result","KULUSER", principal.getPrincipalId());
	}
	
	@Test
	public void testGetContainedAttributes() {
		KimPrincipal principal = identityService.getPrincipal("p1");
		
		KimEntityDefaultInfo entity = identityService.getEntityDefaultInfo( principal.getEntityId() );
		assertNotNull( "Entity Must not be null", entity );
		KimEntityEntityTypeDefaultInfo eet = entity.getEntityType( "PERSON" );
		assertNotNull( "PERSON EntityEntityType must not be null", eet );
		assertNotNull( "EntityEntityType's default email address must not be null", eet.getDefaultEmailAddress() );
		assertEquals( "p1@kuali.org", eet.getDefaultEmailAddress().getEmailAddressUnmasked() );
	}

	protected IdentityService findIdSvc() throws Exception {
		return (IdentityService) GlobalResourceLoader.getService(new QName("KIM", "kimIdentityService"));
	}

	protected void setIdentityService(IdentityService idSvc) {
		this.identityService = idSvc;
	}
}
