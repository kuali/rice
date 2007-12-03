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
package edu.iu.uis.eden.messaging.exceptionhandling;

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.bus.test.KSBTestCase;
import org.kuali.rice.RiceConstants;
import org.kuali.rice.test.TestUtilities;

import edu.iu.uis.eden.messaging.GlobalCallbackRegistry;
import edu.iu.uis.eden.messaging.KEWJavaService;
import edu.iu.uis.eden.messaging.PersistedMessage;
import edu.iu.uis.eden.messaging.TestCallback;
import edu.iu.uis.eden.messaging.remotedservices.TesetHarnessExplodingQueue;

/**
 * Tests various exception messaging cases
 *
 * Millis to live - that a message with no home is still sending messages while it's time to live hasn't expired
 * Retry count - that a message configured with a retry count will send x number of messages before being marked exception
 * Being marked as exception - that a message in exception is in the route log and marked with a status of 'E'
 * Defuault retry count - that a message configured with no retry or time to live is retry the default number of times as
 * 	noted in an app constant and a class default if that constant is not a number or doesn't exist
 * App Constant to determine the default time increment works (we need this to effectively test anyway)
 * Things work without the timeincrement constant in place
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ExceptionMessagingTest extends KSBTestCase {


	private QName queueTimeToLiveServiceName = new QName("KEW", "explodingQueueTimeLimit");
//	private QName retryCountServiceName = new QName("KEW", "testExplodingRetryCount");
	private TestCallback callback = new TestCallback();

	@Override
	public void setUp() throws Exception {
		System.setProperty(RiceConstants.ROUTE_QUEUE_TIME_INCREMENT_KEY, "500");
		System.setProperty(RiceConstants.ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_KEY, "5");
//		System.setProperty(RiceConstants.IMMEDIATE_EXCEPTION_ROUTING, "false");
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
	 * test that service is in queue marked 'E' when the time to live is expired.
	 * @throws Exception
	 */
	@Test public void testTimeToLive() throws Exception {

		KEWJavaService explodingQueue = (KEWJavaService)KSBServiceLocator.getMessageHelper().getServiceAsynchronously(this.queueTimeToLiveServiceName);
		explodingQueue.invoke("");
		TestUtilities.waitForExceptionRouting();
		//this service is on a 3 second wait the queue is on a 1 sec.
		Thread.sleep(10000);

		//verify the entry is in exception routing
		List<PersistedMessage> messagesQueued = KSBServiceLocator.getRouteQueueService().findByServiceName(this.queueTimeToLiveServiceName, "invoke");
		PersistedMessage message = messagesQueued.get(0);
		assertEquals("Message should be in exception status", RiceConstants.ROUTE_QUEUE_EXCEPTION, message.getQueueStatus());
		assertTrue("Message expiration date should be equal to or earlier than last queue date", message.getExpirationDate().getTime() <= message.getQueueDate().getTime());
	}


}
