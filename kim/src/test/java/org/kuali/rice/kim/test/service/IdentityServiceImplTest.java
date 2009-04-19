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
package org.kuali.rice.kim.test.service;

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.config.spring.ConfigFactoryBean;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimEntityEntityType;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.service.impl.IdentityServiceImpl;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.test.lifecycles.JettyServerLifecycle;
import org.kuali.rice.test.web.HtmlUnitUtil;

/**
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class IdentityServiceImplTest extends KIMTestCase {

	private IdentityServiceImpl identityService;

	public void setUp() throws Exception {
		super.setUp();
		identityService = (IdentityServiceImpl) GlobalResourceLoader.getService(new QName("KIM", "kimIdentityService"));
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
		
		KimEntity entity = identityService.getEntityImpl( principal.getEntityId() );
		assertNotNull( "Entity Must not be null", entity );
		KimEntityEntityType eet = entity.getEntityType( "PERSON" );
		assertNotNull( "PERSON EntityEntityType Must not be null", eet );
		assertEquals( "there should be 1 email address", 1, eet.getEmailAddresses().size() );
		assertEquals( "email address does not match", "p1@kuali.org", eet.getDefaultEmailAddress().getEmailAddress() );
	}

}
