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

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.config.spring.ConfigFactoryBean;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.impl.RoleServiceImpl;
import org.kuali.rice.test.RiceTestCase;
import org.kuali.rice.test.lifecycles.JettyServerLifecycle;
import org.kuali.rice.test.web.HtmlUnitUtil;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoleServiceImplTest extends RiceTestCase {

	private RoleServiceImpl roleService;

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
		roleService = (RoleServiceImpl)GlobalResourceLoader.getService(new QName("KIM", "kimRoleService"));
	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testPrincipaHasRoleOfDirectAssignment() {
		List <String>roleIds = new ArrayList<String>();
		roleIds.add("r1");
		assertTrue( "p1 has direct role r1", roleService.principalHasRole("p1", roleIds, null ));	
		//assertFalse( "p4 has no direct/higher level role r1", roleService.principalHasRole("p4", roleIds, null ));	
		AttributeSet qualification = new AttributeSet();
		qualification.put("Attribute 2", "CHEM");
		assertTrue( "p1 has direct role r1 with rp2 attr data", roleService.principalHasRole("p1", roleIds, qualification ));	
		qualification.clear();
		//requested qualification rolls up to a higher element in some hierarchy 
		// method not implemented yet, not quite clear how this works
		qualification.put("Attribute 3", "PHYS");
		assertTrue( "p1 has direct role r1 with rp2 attr data", roleService.principalHasRole("p1", roleIds, qualification ));	
	}

	@Test
	public void testPrincipalHasRoleOfHigherLevel() {
		// "p3" is in "r2" and "r2 contains "r1"
		List <String>roleIds = new ArrayList<String>();
		roleIds.add("r2");
		assertTrue( "p1 has assigned in higher level role r1", roleService.principalHasRole("p1", roleIds, null ));		
	}
	
	@Test
	public void testPrincipalHasRoleContainsGroupAssigned() {
		// "p2" is in "g2" and "g2" assigned to "r1"
		List <String>roleIds = new ArrayList<String>();
		roleIds.add("r1");
		assertTrue( "p2 has assigned to g2 and g2 assigned to r1", roleService.principalHasRole("p3", roleIds, null ));		
	}

}
