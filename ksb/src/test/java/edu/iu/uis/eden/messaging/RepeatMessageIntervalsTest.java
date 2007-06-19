package edu.iu.uis.eden.messaging;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.bus.test.KSBTestCase;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import edu.iu.uis.eden.messaging.remotedservices.TestHarnessSharedTopic;
import edu.iu.uis.eden.messaging.remotedservices.TestRepeatMessageQueue;

public class RepeatMessageIntervalsTest extends KSBTestCase {

	private QName repeatQueueName = new QName("KEW", "testRepeatMessageQueue");
	private QName repeatTopicName = new QName("KEW", "repeatTopic");
	
	@Override
	public void setUp() throws Exception {
		System.setProperty("threadPool.fetchFrequency", "1");
		super.setUp();
	}

	public boolean startClient1() {
		return true;
	}

	/** 
	 * Test that a queue can be called repeatedly with delay.  
	 * 
	 * @throws Exception
	 */
	@Test public void testQueueDelay() throws Exception {
		//put a new MessageFetcher in the queue to ensure that queue is being queried fast enough for our test
		//there is no easy way at this point to speed this up run-time so a new one will have to do
		KSBServiceLocator.getThreadPool().scheduleWithFixedDelay(new MessageFetcher(), 250, 250, TimeUnit.MILLISECONDS);
		KEWJavaService repeatQueue = (KEWJavaService)KSBServiceLocator.getMessageHelper().getServiceAsynchronously(this.repeatQueueName, null, null, TimeUnit.SECONDS, new Long(3));
		// service set for 3 sec call intervals.  Call and pause for 10 seconds should have 4 calls
		// 1 for sync queue, 3 the 10 seconds of pause
		repeatQueue.invoke("");
		Thread.sleep(10000);
		KSBServiceLocator.getThreadPool().stop();
		assertEquals("Service with repeatable called not called correct number of times", 4, TestRepeatMessageQueue.CALL_COUNT);
	}
	
	/**
	 * Tests that a topic can be called repeatedly with a delay
	 * 
	 * @throws Exception
	 */
	@Test public void testTopicDelay() throws Exception {
		//put a new MessageFetcher in the queue to ensure that queue is being queried fast enough for our test
		//there is no easy way at this point to speed this up run-time so a new one will have to dos
		KEWJavaService repeatTopic = (KEWJavaService)KSBServiceLocator.getMessageHelper().getServiceAsynchronously(this.repeatTopicName, null, null, TimeUnit.SECONDS, new Long(7));
		// service set for 3 sec call intervals.  Call and pause for 10 seconds should have 
		// 3 calls.  One for sync, other for delays
		repeatTopic.invoke("");
		Thread.sleep(10000);
		KSBServiceLocator.getThreadPool().stop();
		assertEquals("Repeatable topic not called correct number of times", 2, TestHarnessSharedTopic.CALL_COUNT);
	}
}
