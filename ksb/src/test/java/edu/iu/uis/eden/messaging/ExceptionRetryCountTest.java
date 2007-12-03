/*
 * Copyright 2007 The Kuali Foundation
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
import org.kuali.rice.RiceConstants;
import org.kuali.rice.core.Core;
import org.kuali.rice.test.TestUtilities;

import edu.iu.uis.eden.messaging.remotedservices.TesetHarnessExplodingQueue;

/**
 * This is a description of what this class does - rkirkend don't forget to fill this in.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class ExceptionRetryCountTest extends KSBTestCase {

    private QName retryCountServiceName = new QName("KEW", "testExplodingRetryCount");
    private TestCallback callback = new TestCallback();

    @Override
    public void setUp() throws Exception {
	System.setProperty(RiceConstants.ROUTE_QUEUE_TIME_INCREMENT_KEY, "500");
	System.setProperty(RiceConstants.ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_KEY, "2");
	super.setUp();
	GlobalCallbackRegistry.getCallbacks().clear();
	GlobalCallbackRegistry.getCallbacks().add(this.callback);
	TestCallback.clearCallbacks();
	TesetHarnessExplodingQueue.NUM_CALLS = 0;
    }

    @Override
    public void tearDown() throws Exception {
	try {
	    KSBServiceLocator.getScheduler().shutdown();
	} finally {
	    super.tearDown();
	}
    }

    /**
         * Test that a message with retry count gets retried that many times.
         * 
         * @throws Exception
         */
    @Test
    public void testRetryCount() throws Exception {
	// Turn the requeue up very high so the message will go through all it's requeues immediately

	Core.getCurrentContextConfig().overrideProperty(RiceConstants.ROUTE_QUEUE_TIME_INCREMENT_KEY, "100");

	KEWJavaService explodingQueue = (KEWJavaService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(
		this.retryCountServiceName);
	explodingQueue.invoke("");
	TestUtilities.waitForExceptionRouting();

	this.callback.pauseUntilNumberCallbacksUsingStaticCounter(3, this.retryCountServiceName);
	
	//pause to let save to queue in status 'E' happen
	int i = 0;
	while (i++ < 30) {
	    List<PersistedMessage> queuedItems = KSBServiceLocator.getRouteQueueService().findAll();
	    if (queuedItems.size() != 1) {
		fail("test setup wrong should have a single item in the queue.");
	    }
	    PersistedMessage message = queuedItems.get(0);
	    if (message.getQueueStatus().equals("E")) {
		break;
	    }
	    System.out.println("Message not saved to queue in 'E' status.  Sleeping 1 sec.");
	    Thread.sleep(1000);
	}

	assertEquals("Service should have been called 3 times", 3, TesetHarnessExplodingQueue.NUM_CALLS);

	List<PersistedMessage> messagesQueued = KSBServiceLocator.getRouteQueueService().findByServiceName(
		this.retryCountServiceName, "invoke");
	PersistedMessage message = messagesQueued.get(0);
	assertEquals("Message should be in exception status", RiceConstants.ROUTE_QUEUE_EXCEPTION, message.getQueueStatus());
	assertEquals("Message retry count not what was configured", new Integer(2), message.getRetryCount());
    }
}