/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.messaging;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.bus.test.KSBTestCase;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.messaging.remotedservices.GenericTestService;
import edu.iu.uis.eden.messaging.remotedservices.TestServiceInterface;
import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;
import edu.iu.uis.eden.messaging.serviceconnectors.BusLocalConnector;
import edu.iu.uis.eden.messaging.serviceconnectors.ServiceConnector;
import edu.iu.uis.eden.messaging.serviceconnectors.ServiceConnectorFactory;

/**
 *
 * @author rkirkend
 */
public class DevModeTest extends KSBTestCase {
	
	@Override
	public void setUp() throws Exception {		
		System.setProperty("dev.mode", "true");
		System.setProperty("additional.config.locations", "classpath:edu/iu/uis/eden/messaging/dev_mode_config.xml");
		super.setUp();
	}
	
	

	@Override
	public void tearDown() throws Exception {
		System.clearProperty("dev.mode");
		System.clearProperty("additional.config.locations");
		super.tearDown();
	}



	@Test public void testCallInDevMode() throws Exception {
		QName serviceName = new QName("KEW", "testLocalServiceFavoriteCall");
		TestServiceInterface service = (TestServiceInterface) GlobalResourceLoader.getService(serviceName);
		service.invoke();
		assertTrue("No calls to dev defined service", GenericTestService.NUM_CALLS > 0);
		
		RemoteResourceServiceLocatorImpl rrsl = (RemoteResourceServiceLocatorImpl)KSBResourceLoaderFactory.getRemoteResourceLocator();
		
		ServiceConnector serviceConnector = ServiceConnectorFactory.getServiceConnector(rrsl.getAllServices(serviceName).get(0).getServiceInfo());
		assertTrue("Not BusLocalConnector", serviceConnector instanceof BusLocalConnector);
		assertNull("Service in service definition needs to be null for async communications serialization", serviceConnector.getServiceHolder().getServiceInfo().getServiceDefinition().getService());
		
		service = (TestServiceInterface) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName);
		service.invoke();
		assertTrue("No calls to dev defined service", GenericTestService.NUM_CALLS > 1);
		
		assertTrue("should be no registered services", KSBServiceLocator.getIPTableService().fetchAll().size() == 0);
	}
}