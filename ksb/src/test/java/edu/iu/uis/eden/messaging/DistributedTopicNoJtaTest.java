package edu.iu.uis.eden.messaging;

import org.junit.Ignore;
import org.junit.Test;


/**
 * This test is currently being ignored.  See KULRICE-1852 for details.
 */
@Ignore
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
