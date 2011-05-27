/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.ksb.messaging.resourceloading;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.Test;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.ResourceLoader;
import org.kuali.rice.core.impl.config.property.JAXBConfigImpl;
import org.kuali.rice.ksb.messaging.resourceloader.KSBResourceLoaderFactory;
import org.kuali.rice.test.config.SimpleConfig;


public class KSBResourceLoaderFactoryTest {

	private static String simpleConfig = "SIMPLE_CONFIG";
	private static String jaxbConfig = "JAXB_CONFIG";
	
	
	// We want to test with both impls
	protected Config getConfigObject(String configType, Properties p){
		Config cRet = null;
		if(simpleConfig.equals(configType)){
			cRet = new SimpleConfig(p);
		}else if(jaxbConfig.equals(configType)){
			cRet = new JAXBConfigImpl(p);
		}
		return cRet;
	}
	
	@Test public void testCreateKSBResourceLoader() throws Exception {
		createKSBResourceLoaderImpl(simpleConfig);
		createKSBResourceLoaderImpl(jaxbConfig);
	}
	protected void createKSBResourceLoaderImpl(String configType) throws Exception {
		String me = "TestME";
		Properties props = new Properties();
		props.put(Config.SERVICE_NAMESPACE, me);
		Config config = getConfigObject(configType, props);
		config.parseConfig();
		ConfigContext.init(config);
		
		ResourceLoader rl = KSBResourceLoaderFactory.createRootKSBRemoteResourceLoader();
		assertNotNull(rl.getResourceLoader(KSBResourceLoaderFactory.getRemoteResourceLoaderName()));
	}
	
	@Test public void testCreateKSBResourceLoaderNoserviceNamespace() throws Exception {
		createKSBResourceLoaderNoserviceNamespaceImpl(simpleConfig);
		createKSBResourceLoaderNoserviceNamespaceImpl(jaxbConfig);
		
	}
	
	protected void createKSBResourceLoaderNoserviceNamespaceImpl(String configType) throws Exception {
		
		Properties props = new Properties();
		Config config = getConfigObject(configType,props);
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
