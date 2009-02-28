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
package org.kuali.rice.ksb.messaging.resourceloading;

import java.util.ArrayList;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Test;
import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.ConfigurationException;
import org.kuali.rice.core.config.SimpleConfig;
import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.kuali.rice.ksb.messaging.config.ServiceHolder;
import org.kuali.rice.ksb.messaging.resourceloader.KSBResourceLoaderFactory;


public class KSBResourceLoaderFactoryTest extends TestCase {

	
	@Test public void testCreateKSBResourceLoader() throws Exception {
		String me = "TestME";
		Properties props = new Properties();
		props.put(Config.SERVICE_NAMESPACE, me);
		Config config = new SimpleConfig(props);
		config.parseConfig();
		ConfigContext.init(config);
		
		ResourceLoader rl = KSBResourceLoaderFactory.createRootKSBRemoteResourceLoader();
		assertNotNull(rl.getResourceLoader(KSBResourceLoaderFactory.getRemoteResourceLoaderName()));
	}
	
	@Test public void testCreateKSBResourceLoaderNoserviceNamespace() throws Exception {
		
		Properties props = new Properties();
		Config config = new SimpleConfig(props);
		config.parseConfig();
		ConfigContext.init(config);
		
		boolean errorThrown = false;
		try {
			KSBResourceLoaderFactory.createRootKSBRemoteResourceLoader();
			fail("should have thrown configuration exception with no service namespace present");
		} catch (ConfigurationException ce) {
			errorThrown = true;
		}
		assertTrue(errorThrown);
	}
	
}