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
import org.kuali.rice.kim.api.entity.principal.Principal;
import org.kuali.rice.kim.bo.entity.KimEntity;

import org.kuali.rice.kim.api.entity.type.EntityTypeDataContract;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.kim.service.impl.IdentityServiceImpl;
import org.kuali.rice.kim.test.KIMTestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class IdentityServiceImplTest extends KIMTestCase {

	private IdentityServiceImpl identityService;

	public void setUp() throws Exception {
		super.setUp();
		identityService = (IdentityServiceImpl) KIMServiceLocatorInternal.getBean("kimIdentityDelegateService");
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
	public void testGetContainedAttributes() {
		Principal principal = identityService.getPrincipal("p1");
		
		KimEntity entity = identityService.getEntityImpl( principal.getEntityId() );
		assertNotNull( "Entity Must not be null", entity );
		EntityTypeDataContract eet = entity.getEntityType( "PERSON" );
		assertNotNull( "PERSON EntityEntityType Must not be null", eet );
		assertEquals( "there should be 1 email address", 1, eet.getEmailAddresses().size() );
		assertEquals( "email address does not match", "p1@kuali.org", eet.getDefaultEmailAddress().getEmailAddressUnmasked() );
	}

}
