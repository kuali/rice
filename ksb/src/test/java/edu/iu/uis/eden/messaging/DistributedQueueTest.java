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
package edu.iu.uis.eden.messaging;

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.bus.test.KSBTestCase;

import edu.iu.uis.eden.messaging.bam.BAMService;
import edu.iu.uis.eden.messaging.bam.BAMTargetEntry;
import edu.iu.uis.eden.messaging.callbacks.SimpleCallback;

/**
 * Tests distributed Queue scenarios
 * 
 * @author rkirkend
 * 
 */
public class DistributedQueueTest extends KSBTestCase {

    public boolean startClient1() {
	return true;
    }

    public boolean startClient2() {
	return true;
    }

    /**
     * If calling a queue with multiple subscribers only one subscriber should be calld.
     * 
     * @throws Exception
     */
    @Test public void testSuccessfullyCallingQueueOnce() throws Exception {

	QName serviceName = new QName("testAppsSharedQueue", "sharedQueue");

	KEWJavaService testJavaAsyncService = (KEWJavaService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName);
	testJavaAsyncService.invoke(new ClientAppServiceSharedPayloadObj("message content", false));
	verifyServiceCalls(serviceName);

    }
    
    @Test public void testCallingQueueAsnyc() throws Exception {
	KSBTestUtils.setMessagingToAsync();
	
	QName serviceName = new QName("testAppsSharedQueue", "sharedQueue");
	SimpleCallback callback = new SimpleCallback();
	KEWJavaService testJavaAsyncService = (KEWJavaService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName, callback);
	testJavaAsyncService.invoke(new ClientAppServiceSharedPayloadObj("message content", false));
	callback.waitForAsyncCall();
	verifyServiceCalls(serviceName);
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
