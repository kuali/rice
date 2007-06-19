package edu.iu.uis.eden.messaging;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.bus.test.KSBTestCase;
import org.kuali.rice.util.ClassLoaderUtils;

import edu.iu.uis.eden.messaging.exceptionhandling.DefaultMessageExceptionHandler;
import edu.iu.uis.eden.messaging.exceptionhandling.MessageExceptionHandler;
import edu.iu.uis.eden.messaging.remotedservices.TestRepeatMessageQueue;
import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;

/**
 * Tests RemoteResourceLoader is working correctly by itself and working correctly with the 
 * RemoteServiceRegistry in marking services bad/refreshing services/etc.
 * 
 * @author rkirkend
 *
 */
public class RemoteResourceLoaderTest extends KSBTestCase {

	private QName mockServiceName = new QName("KEW", "mockService");
	private QName testTopicName = new QName("testAppsSharedTopic", "sharedTopic");
	private RemoteResourceServiceLocator rrl;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		this.rrl = KSBResourceLoaderFactory.getRemoteResourceLocator();
	}
	
	/*
	 * test scenarios
	 * service marked inactive/changed at db picked up by RRL
	 * service 'removed' by RRL marked inactive at db/can no longer be fetched
	 * service 'removed' by RRL - refreshed on by RSR - refresh on by RRL - now active
	 */
	
	
	/**
	 * mark a service inactive through the rrl api verify that the db reflects the change.
	 */
	@Test public void testMarkingInactiveWorksAtDBLevel() throws Exception {
		KSBServiceLocator.getThreadPool().stop();
		this.rrl.removeResourceLoader(this.testTopicName);
		ServiceInfo testTopicInfo = this.findServiceInfo(this.testTopicName, KSBServiceLocator.getIPTableService().fetchAll());
		assertTrue("Service should be marked inactive at the db", testTopicInfo.getAlive());
	}
	
	/** 
	 * mark the service inactive then fire the RemoteServiceRegistry to see that it marks the service 
	 * active again.  refresh the rrl to make sure it gets the change.
	 * @throws Exception
	 */
	@Test public void testMarkingInactiveCausesRefreshOfRemoteServiceRegistry() throws Exception {
		testMarkingInactiveWorksAtDBLevel();
		((Runnable)KSBServiceLocator.getServiceDeployer()).run();
		Object service = this.rrl.getService(this.testTopicName);
		((Runnable)this.rrl).run();
		assertNotNull("Service should have been marked active again by the RemoteServiceRegistry and re-registered with the rrl", service);
	}
	
	/**
	 * change service at the db level (change by another node) and verify the service is removed from 
	 * current rrl
	 */
	@Test public void testChangedServicePickedUpOnRefresh() throws Exception {
		KSBServiceLocator.getThreadPool().stop();
		//this will clear out the locally deployed services so we can test what the rrl is doing
		KSBServiceLocator.getServiceDeployer().stop();
		ServiceInfo testTopicInfo = this.findServiceInfo(this.testTopicName, KSBServiceLocator.getIPTableService().fetchAll());
		testTopicInfo.setAlive(false);
		KSBServiceLocator.getIPTableService().saveEntry(testTopicInfo);
		((Runnable)this.rrl).run();
		try {
		    this.rrl.getService(this.testTopicName);
			fail("Should have thrown exception trying to fetch non-existent service");
		} catch (Exception e) {	
		    // this is okay
		}
	}
	
	/**
	 * change service start rrl.  make sure service isn't there.
	 * @throws Exception
	 */
	@Test public void testChangedServicePickedUpOnStartup() throws Exception {
	    this.rrl.stop();
//		this will clear out the locally deployed services so we can test what the rrl is doing
		KSBServiceLocator.getServiceDeployer().stop();
		ServiceInfo testTopicInfo = this.findServiceInfo(this.testTopicName, KSBServiceLocator.getIPTableService().fetchAll());
		testTopicInfo.setAlive(false);
		KSBServiceLocator.getIPTableService().saveEntry(testTopicInfo);
		this.rrl.start();
		try {
		    this.rrl.getService(this.testTopicName);
			fail("Should have thrown exception trying to fetch non-existent service");
		} catch (Exception e) {
		    // this is okay
		}
	}
	
	/**
	 * add service refresh rrl.  make sure it's there.
	 * @throws Exception
	 */
	@Test public void testServiceAddedShowUpOnRefresh() throws Exception {
		KSBServiceLocator.getThreadPool().stop();
		this.addServiceToDB();
		((Runnable)this.rrl).run();
		Object service = this.rrl.getService(this.mockServiceName);
		assertNotNull("service should be in memory after run on RemoteResourceLocator", service);
	}
	
	/**
	 * add service start rrl.  make sure it's there.
	 * @throws Exception
	 */
	@Test public void testServiceAddedShowUpOnStartup() throws Exception {
	    this.rrl.stop();
		this.addServiceToDB();
		this.rrl.start();
		Object service = this.rrl.getService(this.mockServiceName);
		assertNotNull("service should be in memory after starting RemoteResourceLocator", service);
	}

	/**
	 * test fetching of service that is there and not
	 */
	@Test public void testServiceFetch() throws Exception {
		
		KEWJavaService testTopic = (KEWJavaService) this.rrl.getService(this.testTopicName);
		assertNotNull("Sould have fetched service", testTopic);
		try {
		    this.rrl.getService(new QName("KEW", "FakeyMcService"));
			fail("Should have thrown exception fetching non-existent service");
		} catch (Exception e) {
		    // this is okay
		}
	}
	
	@Test public void testExceptionHandlerFetching() throws Exception {
		MessageExceptionHandler exceptionHandler = this.rrl.getMessageExceptionHandler(this.testTopicName);
		assertNotNull("Exception handler should have been fetched", exceptionHandler);
		Object obj = ClassLoaderUtils.unwrapFromProxy(exceptionHandler);
		assertTrue("Exception handler should be instance of default exception handler", obj instanceof DefaultMessageExceptionHandler);

		try {
		    this.rrl.getMessageExceptionHandler(new QName("KEW", "FakeyMcService"));
			fail("should have thrown exception get exception handler by non existent service");
		} catch (Exception e) {
		    // this is okay
		}
	}
	
	private ServiceInfo findServiceInfo(QName serviceName, List<ServiceInfo> serviceInfos) {
		for (ServiceInfo info : serviceInfos) {
			if (info.getQname().equals(serviceName)) {
				return info;
			}
		}
		throw new RuntimeException("Should have found service " + serviceName);
	}
	
	private void addServiceToDB() throws Exception {
		ServiceDefinition mockServiceDef = getMockServiceDefinition();
		mockServiceDef.validate();
		ServiceInfo mockService = new ServiceInfo(mockServiceDef);
		KSBServiceLocator.getIPTableService().saveEntry(mockService);
	}	
	
	private ServiceDefinition getMockServiceDefinition() throws Exception {
		ServiceDefinition serviceDef = new JavaServiceDefinition();
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
