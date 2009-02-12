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
import org.kuali.rice.kim.bo.entity.EntityEntityType;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.service.impl.IdentityServiceImpl;
import org.kuali.rice.test.RiceTestCase;
import org.kuali.rice.test.lifecycles.JettyServerLifecycle;
import org.kuali.rice.test.web.HtmlUnitUtil;

/**
 * TODO: This is just a quick stab at testing some of this kim stuff. It needs expanded and 
 * moved into the test module. It also needs cleaned up as I am hacking the base classes.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class IdentityServiceImplTest extends RiceTestCase {

	private IdentityServiceImpl identityService;

	private String contextName = "/knstest";

	private String relativeWebappRoot = "/../web/src/main/webapp";

	private String testConfigFilename = "classpath:META-INF/kim-test-config.xml";

	@Override
	protected List<Lifecycle> getSuiteLifecycles() {
		List<Lifecycle> lifecycles = super.getSuiteLifecycles();
		lifecycles.add(new Lifecycle() {
			boolean started = false;

			public boolean isStarted() {
				return this.started;
			}

			public void start() throws Exception {
				System.setProperty(KEWConstants.BOOTSTRAP_SPRING_FILE, "SampleAppBeans-test.xml");
				ConfigFactoryBean.CONFIG_OVERRIDE_LOCATION = testConfigFilename;
				//new SQLDataLoaderLifecycle(sqlFilename, sqlDelimiter).start();
				new JettyServerLifecycle(HtmlUnitUtil.getPort(), contextName, relativeWebappRoot).start();
				//new KEWXmlDataLoaderLifecycle(xmlFilename).start();
				System.getProperties().remove(KEWConstants.BOOTSTRAP_SPRING_FILE);
				this.started = true;
			}

			public void stop() throws Exception {
				this.started = false;
			}

		});
		return lifecycles;
	}

	@Override
	protected String getModuleName() {
		return "kim";
	}

	@Override
	protected List<Lifecycle> getDefaultSuiteLifecycles() {
		List<Lifecycle> lifecycles = getInitialLifecycles();
		return lifecycles;
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		identityService = (IdentityServiceImpl) GlobalResourceLoader.getService(new QName("KIM", "kimIdentityService"));
	}

	@After
	public void tearDown() throws Exception {}

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
		KimEntity entity = identityService.getEntityImpl( "3776" );
		assertNotNull( "Entity Must not be null", entity );
		EntityEntityType eet = entity.getEntityType( "PERSON" );
		assertNotNull( "PERSON EntityEntityType Must not be null", eet );
		assertEquals( "there should be 1 email address", 1, eet.getEmailAddresses().size() );
		assertEquals( "email address does not match", "KGLTEST-L@INDIANA.EDU", eet.getDefaultEmailAddress().getEmailAddress() );
	}

}
