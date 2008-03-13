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
package edu.iu.uis.eden.messaging.resourceloading;

import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Test;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.config.SimpleConfig;
import org.kuali.rice.core.Core;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoader;


public class KSBResourceLoaderFactoryTest extends TestCase {

	
	@Test public void testCreateKSBResourceLoader() throws Exception {
		String me = "TestME";
		Properties props = new Properties();
		props.put(Config.MESSAGE_ENTITY, me);
		Config config = new SimpleConfig(props);
		config.parseConfig();
		Core.init(config);
		
		ResourceLoader rl = KSBResourceLoaderFactory.createRootKSBResourceLoader();
		assertNotNull(rl.getResourceLoader(KSBResourceLoaderFactory.getSpringResourceLoaderName()));
		assertNotNull(rl.getResourceLoader(KSBResourceLoaderFactory.getRemoteResourceLoaderName()));
		assertNotNull(GlobalResourceLoader.getResourceLoader(KSBResourceLoaderFactory.getSpringResourceLoaderName()));
		assertNotNull(GlobalResourceLoader.getResourceLoader(KSBResourceLoaderFactory.getRemoteResourceLoaderName()));
	}
	
	@Test public void testCreateKSBResourceLoaderNoMessageEntity() throws Exception {
		
		Properties props = new Properties();
		Config config = new SimpleConfig(props);
		config.parseConfig();
		Core.init(config);
		
		boolean errorThrown = false;
		try {
			KSBResourceLoaderFactory.createRootKSBResourceLoader();
			fail("should have thrown configuration exception with no message entity present");
		} catch (ConfigurationException ce) {
			errorThrown = true;
		}
		assertTrue(errorThrown);
	}
	
}