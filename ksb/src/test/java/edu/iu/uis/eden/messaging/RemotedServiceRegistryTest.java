package edu.iu.uis.eden.messaging;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.bus.test.KSBTestCase;
import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;

import edu.iu.uis.eden.messaging.remotedservices.TestRepeatMessageQueue;
import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;

public class RemotedServiceRegistryTest extends KSBTestCase {
	
	private QName mockServiceName = new QName("KEW", "mockService");
	private QName testTopicName = new QName("testAppsSharedTopic", "sharedTopic");
	
	//TODO: need to test that publishing a service with duplicate end point throws an exception
	
	@SuppressWarnings("unchecked")
	private ServiceDefinition addServiceToConfig() throws Exception {
		List httpServices = (List) Core.getCurrentContextConfig().getObject(Config.BUS_DEPLOYED_SERVICES);
		ServiceDefinition mockServiceDef = getMockServiceDefinition();
		httpServices.add(mockServiceDef);
		return mockServiceDef;
	}
	
	private void removeServiceFromConfig() throws Exception {
		List httpServices = (List) Core.getCurrentContextConfig().getObject(Config.BUS_DEPLOYED_SERVICES);
		ServiceDefinition serviceToRemove = null;
		for (Iterator iter = httpServices.iterator(); iter.hasNext();) {
			ServiceDefinition serviceDef = (ServiceDefinition)iter.next();
			if (serviceDef.getServiceName().equals(this.testTopicName)) {
				serviceToRemove = serviceDef;
				break;
			}
		}
		httpServices.remove(serviceToRemove);
	}
	
	private void verifyServiceAdded(int originalSize, int newSize, ServiceDefinition mockServiceDef) {
		//adding +2 here because of the service and the forwarding service.
		assertTrue("new service didn't get added to the registry", originalSize + 2 == newSize);
		RemotedServiceHolder serviceHolder = KSBServiceLocator.getServiceDeployer().getPublishedServices().get(this.mockServiceName);
		assertNotNull("Mock service never put in registry memory", serviceHolder);
		assertEquals("End point url of service info is wrong", mockServiceDef.getServiceEndPoint().toString(), serviceHolder.getServiceInfo().getEndpointUrl());
	}
	
	/**
	 * Verifies when service is added to configuration that differs from the config in the db the change is reflected.
	 */
	@Test public void testServiceRefreshAddedService() throws Exception {
		//stop the registry so we thread pool doesn't mess up the result
		KSBServiceLocator.getThreadPool().stop();
		int originalSize = KSBServiceLocator.getServiceDeployer().getPublishedServices().size(); 
		ServiceDefinition mockServiceDef = getMockServiceDefinition();
		KSBServiceLocator.getServiceDeployer().registerService(mockServiceDef, true);
		((Runnable)KSBServiceLocator.getServiceDeployer()).run();
		int newSize = KSBServiceLocator.getServiceDeployer().getPublishedServices().size();
		verifyServiceAdded(originalSize, newSize, mockServiceDef);
	}
	
	@Test public void testServiceRefreshServiceRemoved() throws Exception {
		//stop the registry so we thread pool doesn't mess up the result
		KSBServiceLocator.getThreadPool().stop();
		int originalSize = KSBServiceLocator.getServiceDeployer().getPublishedServices().size(); 
		removeServiceFromConfig();
		((Runnable)KSBServiceLocator.getServiceDeployer()).run();
		int newSize = KSBServiceLocator.getServiceDeployer().getPublishedServices().size();
		verifyServiceRemoved(originalSize, newSize);
	}
	
	/**
	 * Verifies that a service added to the configuration when the app is offline will be 
	 * added when the app starts.
	 * @throws Exception
	 */
	@Test public void testRestartAddedService() throws Exception {
		int originalSize = KSBServiceLocator.getServiceDeployer().getPublishedServices().size(); 
		KSBServiceLocator.getServiceDeployer().stop();
		ServiceDefinition mockServiceDef = addServiceToConfig();
		KSBServiceLocator.getServiceDeployer().start();
		int newSize = KSBServiceLocator.getServiceDeployer().getPublishedServices().size();
		verifyServiceAdded(originalSize, newSize, mockServiceDef);
	}
	
	/**
	 * Verifies that a service is removed when it is removed from the config when app is offline 
	 * and started again.
	 * @throws Exception
	 */
	@Test public void testRestartServiceRemoved() throws Exception {
		int originalSize = KSBServiceLocator.getServiceDeployer().getPublishedServices().size(); 
		KSBServiceLocator.getServiceDeployer().stop();
		removeServiceFromConfig();
		KSBServiceLocator.getServiceDeployer().start();
		int newSize = KSBServiceLocator.getServiceDeployer().getPublishedServices().size();
		verifyServiceRemoved(originalSize, newSize);
	}
	
	/**
	 * Verifies that a service modified when the app is offline is reflected in when the 
	 * app comes online.
	 * 
	 * Exact scenario: the service is set alive=false (presumably by being down and another node 
	 * marking the service as alive=false).  The node comes back and marks it's service as alive.
	 * 
	 * @throws Exception
	 */
	@Test public void testRestartServiceModified() throws Exception {
		KSBServiceLocator.getServiceDeployer().stop();
		ServiceInfo testTopic = findServiceInfo(this.testTopicName, KSBServiceLocator.getIPTableService().fetchAll());
		testTopic.setAlive(false);
		KSBServiceLocator.getIPTableService().saveEntry(testTopic);
		KSBServiceLocator.getServiceDeployer().start();
		testTopic = findServiceInfo(this.testTopicName, KSBServiceLocator.getIPTableService().fetchAll());
		assertTrue("test topic should now be marked as alive", testTopic.getAlive());
	}
	
	/**
	 * Verifies when a node marking a service as dead that service is marked alive 
	 * when this node refreshes it's services 
	 *  
	 * @throws Exception
	 */
	@Test public void testServiceRefreshServiceModified() throws Exception {
//		stop the registry so we thread pool doesn't mess up the result
		KSBServiceLocator.getServiceDeployer().stop();
		KSBServiceLocator.getThreadPool().stop();
		ServiceInfo testTopic = findServiceInfo(this.testTopicName, KSBServiceLocator.getIPTableService().fetchAll());
		testTopic.setAlive(false);
		KSBServiceLocator.getIPTableService().saveEntry(testTopic);
		((Runnable)KSBServiceLocator.getServiceDeployer()).run();
		testTopic = findServiceInfo(this.testTopicName, KSBServiceLocator.getIPTableService().fetchAll());
		assertTrue("test topic should now be marked as alive", testTopic.getAlive());
	}
	
	private ServiceInfo findServiceInfo(QName serviceName, List<ServiceInfo> serviceInfos) {
		for (ServiceInfo info : serviceInfos) {
			if (info.getQname().equals(serviceName)) {
				return info;
			}
		}
		throw new RuntimeException("Should have found service " + serviceName);
	}
	
	@Test public void testMultipleManualRefreshes() throws Exception {
		((Runnable)KSBResourceLoaderFactory.getRemoteResourceLocator()).run();
		((Runnable)KSBResourceLoaderFactory.getRemoteResourceLocator()).run();
		((Runnable)KSBResourceLoaderFactory.getRemoteResourceLocator()).run();
		assertTrue(true);
	}
	
	private void verifyServiceRemoved(int originalSize, int newSize) {
		//-2 because of store and forward service registered for each service.
		assertEquals("new service didn't get removed from the registry", originalSize - 2, newSize);
		RemotedServiceHolder serviceHolder = KSBServiceLocator.getServiceDeployer().getPublishedServices().get(this.testTopicName);
		assertNull("Service should be removed from memory", serviceHolder);
		
		//should be gone from table
		List<ServiceInfo> serviceInfos = KSBServiceLocator.getIPTableService().fetchAll();
		for (ServiceInfo info : serviceInfos) {
			if (info.getQname().equals(this.testTopicName)) {
				fail("This service should no longer be present in the service def table");
			}
		}
	}
	
	private JavaServiceDefinition getMockServiceDefinition() throws Exception {
		JavaServiceDefinition serviceDef = new JavaServiceDefinition();
		serviceDef.setServiceEndPoint(new URL("http://mockServiceURL"));
		serviceDef.setPriority(3);
		serviceDef.setRetryAttempts(3);
		serviceDef.setService(new TestRepeatMessageQueue());
		serviceDef.setServiceName(this.mockServiceName);
		serviceDef.setQueue(false);
		serviceDef.validate();
		return serviceDef;
	}
}
