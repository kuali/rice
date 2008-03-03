package edu.iu.uis.eden.messaging;

import org.junit.Test;

public class DistributedTopicNoJtaTest extends DistributedTopicTest {

	@Test public void testSuccessfullyCallingSyncTopics() throws Exception {
		super.testSuccessfullyCallingSyncTopics();
	}
	
	@Test public void testCallingAsyncTopics() throws Exception {
		super.testCallingAsyncTopics();
	}
	
	@Override
	protected boolean disableJta() {
		return true;
	}
	
}
