/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package edu.iu.uis.eden.messaging;

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.bus.test.KSBTestCase;

import edu.iu.uis.eden.messaging.bam.BAMService;
import edu.iu.uis.eden.messaging.bam.BAMTargetEntry;
import edu.iu.uis.eden.messaging.callbacks.SimpleCallback;
import edu.iu.uis.eden.messaging.remotedservices.SOAPService;
import edu.iu.uis.eden.messaging.remotedservices.ServiceCallInformationHolder;
import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;

/**
 * Tests that queues work over soap
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SOAPMessagingTest extends KSBTestCase {

    public boolean startClient1() {
	return true;
    }

    @Test
    public void testSuccessfullyCallingSOAPTopic() throws Exception {
	// ensure test harness has entries for TestClient1
	((Runnable) KSBResourceLoaderFactory.getRemoteResourceLocator()).run();

	QName serviceName = new QName("testNameSpace", "soap-repeatTopic");

		SOAPService testJavaAsyncService = (SOAPService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName);
	testJavaAsyncService.doTheThing("The param");
	verifyServiceCalls(serviceName);

		assertTrue("Test harness topic never called", ((Boolean) ServiceCallInformationHolder.stuff.get("TestHarnessCalled")).booleanValue());
		assertTrue("Cliet1 app topic never called", ((Boolean) ServiceCallInformationHolder.stuff.get("Client1SOAPServiceCalled")).booleanValue());
    }

    @Test
    public void testSuccessfullyCallingSOAPTopicAsync() throws Exception {
	KSBTestUtils.setMessagingToAsync();

	QName serviceName = new QName("testNameSpace", "soap-repeatTopic");

	SimpleCallback callback = new SimpleCallback();
	SOAPService testJavaAsyncService = (SOAPService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName);
	testJavaAsyncService.doTheThing("The param");
	callback.waitForAsyncCall(3000);
	verifyServiceCalls(serviceName);
		assertTrue("Test harness topic never called", ((Boolean) ServiceCallInformationHolder.stuff.get("TestHarnessCalled")).booleanValue());
		assertTrue("Cliet1 app topic never called", ((Boolean) ServiceCallInformationHolder.stuff.get("Client1SOAPServiceCalled")).booleanValue());
    }

    private void verifyServiceCalls(QName serviceName) throws Exception {
	BAMService bamService = KSBServiceLocator.getBAMService();
	List<BAMTargetEntry> bamCalls = bamService.getCallsForService(serviceName);
	assertTrue("No service call recorded", bamCalls.size() > 0);
	boolean foundClientCall = false;
	boolean foundServiceCall = false;
	for (BAMTargetEntry bamEntry : bamCalls) {
	    if (bamEntry.getServerInvocation()) {
		foundServiceCall = true;
	    } else {
		foundClientCall = true;
	    }
	}
	assertTrue("No client call recorded", foundClientCall);
	assertTrue("No service call recorded", foundServiceCall);
	assertEquals("Wrong number of calls recorded", 2, bamCalls.size());
    }
}
