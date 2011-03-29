/*
 * Copyright 2007 The Kuali Foundation
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
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.framework.persistence.ojb.DataAccessUtils;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.ksb.messaging.exceptionhandling.DefaultMessageExceptionHandler;
import org.kuali.rice.ksb.messaging.exceptionhandling.MessageExceptionHandler;
import org.kuali.rice.ksb.messaging.remotedservices.TestRepeatMessageQueue;
import org.kuali.rice.ksb.messaging.resourceloader.KSBResourceLoaderFactory;
import org.kuali.rice.ksb.messaging.service.KSBJavaService;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.kuali.rice.ksb.test.KSBTestCase;

import javax.xml.namespace.QName;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


/**
 * Tests RemoteResourceLoader is working correctly by itself and working correctly with the RemoteServiceRegistry in marking
 * services bad/refreshing services/etc.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
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
	KSBServiceLocator.getScheduledPool().stop();
	KSBServiceLocator.getServiceDeployer().getPublishedServices().clear();
}

    /*
         * test scenarios service marked inactive/changed at db picked up by RRL service 'removed' by RRL marked inactive at
         * db/can no longer be fetched service 'removed' by RRL - refreshed on by RSR - refresh on by RRL - now active
         */

    /**
         * mark a service inactive through the rrl api verify that the db reflects the change.
         */
    @Test
    public void testMarkingInactiveWorksAtDBLevel() throws Exception {
	this.rrl.removeResourceLoader(this.testTopicName);
	ServiceInfo testTopicInfo = this.findServiceInfo(this.testTopicName, KSBServiceLocator.getServiceRegistry()
		.fetchAll());
	assertTrue("Service should be marked inactive at the db", testTopicInfo.getAlive());
    }

    /**
         * change service at the db level (change by another node) and verify the service is removed from current rrl
         */
    @Test
    public void testChangedServicePickedUpOnRefresh() throws Exception {
	ServiceInfo testTopicInfo = this.findServiceInfo(this.testTopicName, KSBServiceLocator.getServiceRegistry()
		.fetchAll());
	testTopicInfo.setAlive(false);
	this.saveServiceInfo(testTopicInfo, 0);
	((Runnable) this.rrl).run();
	try {
	    this.rrl.getService(this.testTopicName);
	    fail("Should have thrown exception trying to fetch non-existent service");
	} catch (Exception e) {
	    // this is okay
	}
    }

    /**
         * change service start rrl. make sure service isn't there.
         *
         * @throws Exception
         */
    @Test
    public void testChangedServicePickedUpOnStartup() throws Exception {
	ServiceInfo testTopicInfo = this.findServiceInfo(this.testTopicName, KSBServiceLocator.getServiceRegistry()
		.fetchAll());
	testTopicInfo.setAlive(false);
	this.saveServiceInfo(testTopicInfo, 0);
	((Runnable)this.rrl).run();
	try {
	    this.rrl.getService(this.testTopicName);
	    fail("Should have thrown exception trying to fetch non-existent service");
	} catch (Exception e) {
	    // this is okay
	}
    }

    /**
         * add service refresh rrl. make sure it's there.
         *
         * @throws Exception
         */
    @Test
    public void testServiceAddedShowUpOnRefresh() throws Exception {
	this.addServiceToDB();
	((Runnable) this.rrl).run();
	Object service = this.rrl.getService(this.mockServiceName);
	assertNotNull("service should be in memory after run on RemoteResourceLocator", service);
    }

    /**
         * add service start rrl. make sure it's there.
         *
         * @throws Exception
         */
    @Test
    public void testServiceAddedShowUpOnStartup() throws Exception {
//	this.rrl.stop();
	this.addServiceToDB();
	((Runnable)this.rrl).run();
	Object service = this.rrl.getService(this.mockServiceName);
	assertNotNull("service should be in memory after starting RemoteResourceLocator", service);
    }



    /**
         * test fetching of service that is there and not
         */
    @Test
    public void testServiceFetch() throws Exception {

	KSBJavaService testTopic = (KSBJavaService) this.rrl.getService(this.testTopicName);
	assertNotNull("Sould have fetched service", testTopic);
	try {
	    this.rrl.getService(new QName("KEW", "FakeyMcService"));
	    fail("Should have thrown exception fetching non-existent service");
	} catch (Exception e) {
	    // this is okay
	}
    }

    @Test
    public void testExceptionHandlerFetching() throws Exception {
	MessageExceptionHandler exceptionHandler = this.rrl.getMessageExceptionHandler(this.testTopicName);
	assertNotNull("Exception handler should have been fetched", exceptionHandler);
	Object obj = ClassLoaderUtils.unwrapFromProxy(exceptionHandler);
	assertTrue("Exception handler should be instance of default exception handler",
		obj instanceof DefaultMessageExceptionHandler);

	try {
	    this.rrl.getMessageExceptionHandler(new QName("KEW", "FakeyMcService"));
	    fail("should have thrown exception get exception handler by non existent service");
	} catch (Exception e) {
	    // this is okay
	}
    }

    /**
     * put in because of a weird bug where inactive services were forcing client refreshes no matter what.
     *
     * this test verifies that setting a service inactive will for a refresh ONCE.  but not again.
     *
     * @throws Exception
     */
    @Test public void testInactiveServiceDoesntForceRefresh() throws Exception {
	ServiceInfo testTopicInfo = this.findServiceInfo(this.testTopicName, KSBServiceLocator.getServiceRegistry()
		.fetchAll());
	testTopicInfo.setAlive(false);
	this.saveServiceInfo(testTopicInfo, 0);
	((Runnable)this.rrl).run();
	Map<QName, List<RemotedServiceHolder>> clients1 = ((RemoteResourceServiceLocatorImpl)this.rrl).getClients();
	((Runnable)this.rrl).run();
	Map<QName, List<RemotedServiceHolder>> clients2 = ((RemoteResourceServiceLocatorImpl)this.rrl).getClients();
	for (QName name : clients1.keySet()) {
	    List<RemotedServiceHolder> remotedServices1 = clients1.get(name);
	    List<RemotedServiceHolder> remotedServices2 = clients2.get(name);
	    assertEquals(remotedServices1, remotedServices2);
	}
    }

    @Test public void testAddingServiceWithDifferentIPSameURL() throws Exception {
	KSBServiceLocator.getServiceRegistry().removeEntries(KSBServiceLocator.getServiceRegistry().fetchAll());
	assertTrue(KSBServiceLocator.getServiceRegistry().fetchAll().isEmpty());
	ServiceDefinition serviceDef = addServiceToDB();
	
//	ServiceInfo servInfo1 = new ServiceInfo(serviceDef);
	ServiceInfo servInfo2 = new ServiceInfo(serviceDef);
	servInfo2.setServerIp("somethingnew");
	
	List<ServiceInfo> fetchedServices = KSBServiceLocator.getServiceRegistry().fetchAll();
	assertEquals(1, fetchedServices.size());
//	fetchedServices.add(servInfo1);
	List<ServiceInfo> configuredServices = new ArrayList<ServiceInfo>();
	configuredServices.add(servInfo2);
	
	RoutingTableDiffCalculator diffCalc = new RoutingTableDiffCalculator();
	diffCalc.calculateServerSideUpdateLists(configuredServices, fetchedServices);
	diffCalc.getMasterServiceList();
	KSBServiceLocator.getServiceRegistry().saveEntries(diffCalc.getServicesNeedUpdated());
	
	assertEquals(1, KSBServiceLocator.getServiceRegistry().fetchAll().size());
    }
    
    private ServiceInfo findServiceInfo(QName serviceName, List<ServiceInfo> serviceInfos) {
	for (ServiceInfo info : serviceInfos) {
	    if (info.getQname().equals(serviceName)) {
		return info;
	    }
	}
	throw new RuntimeException("Should have found service " + serviceName);
    }

    private void saveServiceInfo(ServiceInfo serviceInfo, int count) {
	if (count++ > 5) {
	    throw new RiceRuntimeException("saveServiceInfo called 5 times and received opt lock exception each time");
	}
	try {
	    KSBServiceLocator.getServiceRegistry().saveEntry(serviceInfo);
	} catch (Exception e) {
	    if (DataAccessUtils.isOptimisticLockFailure(e)) {
		saveServiceInfo(serviceInfo, count);
	    }
	}
    }

    private ServiceDefinition addServiceToDB() throws Exception {
	ServiceDefinition mockServiceDef = getMockServiceDefinition();
	mockServiceDef.validate();
	ServiceInfo mockService = new ServiceInfo(mockServiceDef);
	saveServiceInfo(mockService, 0);
	return mockServiceDef;
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
