/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.rice.kew.clientapp;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.JAXBConfigImpl;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.kuali.rice.core.resourceloader.SpringResourceLoader;
import org.kuali.rice.kew.config.ThinClientResourceLoader;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kim.bo.entity.dto.KimPrincipalInfo;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;

import javax.xml.namespace.QName;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 
 * Test case that confirms functionality of the KEW thin client.  All configuration for
 * the thin client is external, so this test should be helpful as a model for setting
 * up thin client mode. 
 * 
 * One potentially confusing element here is the use of a ClassLoader, but all it is really
 * there for is to allow us to bind the thin client configuration in the ConfigContext while
 * at the same time running a rice instance with its own config.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class ThinClientTest extends KEWTestCase {
	private static final Logger LOG = Logger.getLogger(ThinClientTest.class);

	/**
	 * configuration files used in this test 
	 */
	private static final String CONFIG_FILE = "classpath:/org/kuali/rice/kew/clientapp/thin-client-app-config.xml";
	private static final String SPRING_BEANS_FILE = "classpath:/org/kuali/rice/kew/clientapp/ThinClientSpringBeans.xml";

	private static final String KIM_PRINCIPAL_NAME = "testuser1";

	private static SpringResourceLoader thinClientResourceLoader = new SpringResourceLoader(new QName("ThinClientTest"), 
			SPRING_BEANS_FILE, null);
	
	private ClassLoader ourClassLoader = null;
	private ClassLoader parentClassLoader = null;
	
	public ThinClientTest() {
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		// put our own ClassLoader in place
		ourClassLoader = AccessController.doPrivileged(new DelegatingClassLoaderCreater());

		parentClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(ourClassLoader);

		try {
			//Config config = new SimpleConfig(CONFIG_FILE);
			Config config = new JAXBConfigImpl(CONFIG_FILE);
			config.parseConfig();
			// initialize our config using properties bound to our custom classloader
			ConfigContext.init(ourClassLoader, config);

			thinClientResourceLoader.start();

		} catch (Exception e) {
			LOG.error("Failed to set up thin client test", e);
			throw new RiceRuntimeException(e);
		}
	}

	@Override
	public void tearDown() throws Exception {
		// put the original ClassLoader back for the current thread
		Thread.currentThread().setContextClassLoader(parentClassLoader);
		// remove the reference to our config from the ConfigContext map
		ConfigContext.overrideConfig(ourClassLoader, null);
		
		// I can't imagine this is necessary
		ourClassLoader = null;
		thinClientResourceLoader = null;

		super.tearDown();
	}

	@Test
	public void testThinClientServices() throws Exception {
		//verify the ThinClientResourceLoader is in the GRL.
		ResourceLoader rl = GlobalResourceLoader.getResourceLoader();
		List<ResourceLoader> resourceLoaders = rl.getResourceLoaders();
		ResourceLoader tempThinRL = rl.getResourceLoaders().get(0);
		assertTrue("First resource loader should be thin", tempThinRL instanceof ThinClientResourceLoader);
		ThinClientResourceLoader thinRL = (ThinClientResourceLoader)tempThinRL;

		// test KIM identity service
		KimPrincipalInfo principal = null;
		principal = thinRL.getIdentityService().getPrincipalByPrincipalName(KIM_PRINCIPAL_NAME);
		assertTrue(principal.getPrincipalName().equals(KIM_PRINCIPAL_NAME));

		// test KIM group service
		List<GroupInfo> groups = thinRL.getGroupService().getGroupsForPrincipal(principal.getPrincipalId());
		assertNotNull(groups);
		assertTrue(groups.size() > 0);

		// test Workflow
		RouteHeaderDTO routeHeader = new RouteHeaderDTO();
		routeHeader.setDocTypeName("RiceDocument");
		routeHeader = thinRL.getWorkflowDocument().createDocument(principal.getPrincipalId(), routeHeader);
		assertTrue(principal.getPrincipalId().equals(routeHeader.getInitiatorPrincipalId()));
	}

	/**
	 * a privileged action to create a new classloader with our thread's classloader as its parent 
	 * 
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 *
	 */
	private class DelegatingClassLoaderCreater implements PrivilegedAction<ClassLoader> {
		public ClassLoader run() {
			return new DelegatingClassLoader(Thread.currentThread().getContextClassLoader());
		};
	}

	/**
	 * a very minimal classloader to allow us to bind our thin client cofiguration 
	 * 
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 *
	 */
	private class DelegatingClassLoader extends ClassLoader {
		
		// silly default constructor so that Surefire doesn't get messed up
		// TODO: remove when http://jira.codehaus.org/browse/SUREFIRE-535 is fixed
		public DelegatingClassLoader() { }

		public DelegatingClassLoader(ClassLoader parent) {
			super(parent);
		}
	}

}

