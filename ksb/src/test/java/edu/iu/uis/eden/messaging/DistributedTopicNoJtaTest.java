package edu.iu.uis.eden.messaging;

import org.junit.Ignore;
import org.junit.Test;

public class DistributedTopicNoJtaTest extends DistributedTopicTest {

	@Ignore
	@Test public void testSuccessfullyCallingSyncTopics() throws Exception {
		super.testSuccessfullyCallingSyncTopics();
	}
	
	@Ignore
	@Test public void testCallingAsyncTopics() throws Exception {
		super.testCallingAsyncTopics();
	}
	
	@Override
	protected boolean disableJta() {
		return true;
	}
	
}
