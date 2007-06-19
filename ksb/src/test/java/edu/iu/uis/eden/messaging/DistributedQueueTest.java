package edu.iu.uis.eden.messaging;

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.bus.test.KSBTestCase;

import edu.iu.uis.eden.messaging.bam.BAMService;
import edu.iu.uis.eden.messaging.bam.BAMTargetEntry;

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
	 * @throws Exception
	 */
	@Test
	public void testSuccessfullyCallingQueueOnce() throws Exception {
		
		QName serviceName = new QName("testAppsSharedQueue", "sharedQueue");
		
		KEWJavaService testJavaAsyncService = (KEWJavaService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName);
		testJavaAsyncService.invoke(new ClientAppServiceSharedPayloadObj("message content", false));
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
