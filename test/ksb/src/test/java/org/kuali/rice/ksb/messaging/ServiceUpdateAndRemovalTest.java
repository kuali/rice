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

package org.kuali.rice.ksb.messaging;

import org.junit.Test;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.util.RiceUtilities;
import org.kuali.rice.ksb.messaging.remotedservices.TestRepeatMessageQueue;
import org.kuali.rice.ksb.messaging.service.ServiceRegistry;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.kuali.rice.ksb.test.KSBTestCase;
import org.kuali.rice.ksb.util.KSBConstants;

import javax.xml.namespace.QName;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This test ensures that ServiceInfo and ServiceDefinition instances are being modified and removed correctly. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ServiceUpdateAndRemovalTest extends KSBTestCase {

	/**
	 * Tests the removeLocallyPublishedServices() method of the service registry to ensure that the local services are being deleted properly.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRemovalOfAllLocalServices() throws Exception {
		ServiceRegistry serviceRegistry = KSBServiceLocator.getServiceRegistry();
		String ipNumber = RiceUtilities.getIpNumber();
		String serviceNamespace = ConfigContext.getCurrentContextConfig().getServiceNamespace();
		List<ServiceInfo> serviceInfos = serviceRegistry.findLocallyPublishedServices(ipNumber, serviceNamespace);
		assertTrue("There should be at least one locally published service in the database.", serviceInfos.size() > 0);
		serviceRegistry.removeLocallyPublishedServices(ipNumber, serviceNamespace);
		serviceInfos = serviceRegistry.findLocallyPublishedServices(ipNumber, serviceNamespace);
		assertEquals("There should not be any locally published services in the database.", 0, serviceInfos.size());
	}
	
	/**
	 * Tests the deployment and modification of local services to ensure that they are being updated accordingly.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testModificationOfLocalServices() throws Exception {
		RemotedServiceRegistry remotedServiceRegistry = KSBServiceLocator.getServiceDeployer();
		QName serviceName = new QName("KEW", "serviceForTestingModifications");
		QName forwardServiceName = new QName("KEW", "serviceForTestingModifications" + KSBConstants.FORWARD_HANDLER_SUFFIX);
		ServiceInfo regularInfo = null;
		ServiceInfo forwardInfo = null;
		// Create and deploy a simple test service.
		ServiceDefinition serviceDefinition = new JavaServiceDefinition();
		serviceDefinition.setServiceName(serviceName);
		serviceDefinition.setPriority(4);
		serviceDefinition.setService(new TestRepeatMessageQueue());
		serviceDefinition.validate();
		remotedServiceRegistry.registerService(serviceDefinition, true);
		// Retrieve the ServiceInfo for the original service and the ServiceInfo for the related forward.
		regularInfo = remotedServiceRegistry.getRemotedServiceHolder(serviceName).getServiceInfo();
		forwardInfo = remotedServiceRegistry.getRemotedServiceHolder(forwardServiceName).getServiceInfo();
		// Ensure that refreshing the local registry without modifying the ServiceDefinition yields the expected results.
		assertRegistryRefreshHasExpectedResults(remotedServiceRegistry, regularInfo, forwardInfo, serviceName, forwardServiceName, false);
		// Ensure that refreshing the local registry after modifying the ServiceDefinition yields the expected results.
		regularInfo = remotedServiceRegistry.getRemotedServiceHolder(serviceName).getServiceInfo();
		forwardInfo = remotedServiceRegistry.getRemotedServiceHolder(forwardServiceName).getServiceInfo();
		serviceDefinition.setPriority(3);
		serviceDefinition.validate();
		assertRegistryRefreshHasExpectedResults(remotedServiceRegistry, regularInfo, forwardInfo, serviceName, forwardServiceName, true);
	}
	
	/**
	 * A convenience method for asserting that expected similarities and differences should have occurred after refreshing the registry. This includes
	 * comparing the checksums and ensuring that non-similar checksums are corresponding to modifications to the serialized and non-serialized
	 * ServiceDefinition instances.
	 * 
	 * @param remotedServiceRegistry The service registry to test with.
	 * @param regularInfo The ServiceInfo containing the configured service.
	 * @param forwardInfo The ServiceInfo containing the ForwardedCallHandler for the configured service.
	 * @param serviceName The QName of the configured service.
	 * @param forwardServiceName The QName of the ForwardedCallHandler for the configured service.
	 * @param serviceDefinitionsShouldDiffer A flag indicating if the service definitions should be tested for similarity or difference after the refresh.
	 * @throws Exception
	 */
	private void assertRegistryRefreshHasExpectedResults(RemotedServiceRegistry remotedServiceRegistry, ServiceInfo regularInfo, ServiceInfo forwardInfo,
			QName serviceName, QName forwardServiceName, boolean serviceDefinitionsShouldDiffer) throws Exception {
		MessageHelper messageHelper = KSBServiceLocator.getMessageHelper();
		// Refresh the registry.
		remotedServiceRegistry.refresh();
		ServiceInfo newRegularInfo = remotedServiceRegistry.getRemotedServiceHolder(serviceName).getServiceInfo();
		ServiceInfo newForwardInfo = remotedServiceRegistry.getRemotedServiceHolder(forwardServiceName).getServiceInfo();
		// Perform the assertions that should have the same outcome regardless of whether or not a ServiceDefinition was modified.
		assertTrue("The ServiceInfo instances for the service should satisy the non-ServiceDefinition part of an isSame() check",
				regularInfo.getAlive().equals(newRegularInfo.getAlive()) && regularInfo.getQname().equals(newRegularInfo.getQname()) &&
						regularInfo.getServerIp().equals(newRegularInfo.getServerIp()) &&
								regularInfo.getServiceNamespace().equals(newRegularInfo.getServiceNamespace()));
		assertTrue("The ServiceInfo instances for the ForwardedCallHandler should satisy the non-ServiceDefinition part of an isSame() check",
				forwardInfo.getAlive().equals(newForwardInfo.getAlive()) && forwardInfo.getQname().equals(newForwardInfo.getQname()) &&
						forwardInfo.getServerIp().equals(newForwardInfo.getServerIp()) &&
								forwardInfo.getServiceNamespace().equals(newForwardInfo.getServiceNamespace()));
		assertTrue("The service definition references should be pointing to the same instance",
				regularInfo.getServiceDefinition(messageHelper) == newRegularInfo.getServiceDefinition(messageHelper));
		// Perform the appropriate assertions based on whether or not any updates are expected.
		if (serviceDefinitionsShouldDiffer) {
			assertNotSame("The checksum for the configured service should have been modified after refreshing the registry.",
					regularInfo.getChecksum(), newRegularInfo.getChecksum());
			assertNotSame("The checksum for the ForwardedCallHandler service should have been modified after refreshing the registry.",
					forwardInfo.getChecksum(), newForwardInfo.getChecksum());
			assertTrue("The ForwardedCallHandler service definitions should not be the same",
					!forwardInfo.getServiceDefinition(messageHelper).isSame(newForwardInfo.getServiceDefinition(messageHelper)));
			assertNotSame("The serialized versions of the service definitions should not be the same",
					regularInfo.getSerializedServiceNamespace().getFlattenedServiceDefinitionData(),
							newRegularInfo.getSerializedServiceNamespace().getFlattenedServiceDefinitionData());
			assertNotSame("The serialized versions of the ForwardedCallHandler service definitions should not be the same",
					forwardInfo.getSerializedServiceNamespace().getFlattenedServiceDefinitionData(),
							newForwardInfo.getSerializedServiceNamespace().getFlattenedServiceDefinitionData());
		} else {
			assertEquals("The checksum for the configured service should not have been modified after refreshing the registry.",
					regularInfo.getChecksum(), newRegularInfo.getChecksum());
			assertEquals("The checksum for the ForwardedCallHandler service should not have been modified after refreshing the registry.",
					forwardInfo.getChecksum(), newForwardInfo.getChecksum());
			assertTrue("The ForwardedCallHandler service definitions should be the same",
					forwardInfo.getServiceDefinition(messageHelper).isSame(newForwardInfo.getServiceDefinition(messageHelper)));
			assertEquals("The serialized versions of the service definitions should be the same",
					regularInfo.getSerializedServiceNamespace().getFlattenedServiceDefinitionData(),
							newRegularInfo.getSerializedServiceNamespace().getFlattenedServiceDefinitionData());
			assertEquals("The serialized versions of the ForwardedCallHandler service definitions should be the same",
					forwardInfo.getSerializedServiceNamespace().getFlattenedServiceDefinitionData(),
							newForwardInfo.getSerializedServiceNamespace().getFlattenedServiceDefinitionData());
		}
	}
}
