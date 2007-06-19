package edu.iu.uis.eden.messaging;

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.bus.test.KSBTestCase;

import edu.iu.uis.eden.messaging.bam.BAMService;
import edu.iu.uis.eden.messaging.bam.BAMTargetEntry;
import edu.iu.uis.eden.messaging.remotedservices.SOAPService;
import edu.iu.uis.eden.messaging.remotedservices.ServiceCallInformationHolder;
import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;

/**
 * simple test verifying if distributed topics are working.
 * 
 * @author rkirkend
 *
 */
public class DistributedTopicTest extends KSBTestCase {
	
	public boolean startClient1() {
		return true;
	}
	
	@Test
	public void testSuccessfullyCallingAllTopics() throws Exception {
		
//		ensure test harness has entries for TestClient1
		((Runnable) KSBResourceLoaderFactory.getRemoteResourceLocator()).run();
		
		
		QName serviceName = new QName("testAppsSharedTopic", "sharedTopic");
		
		KEWJavaService testJavaAsyncService = (KEWJavaService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName);
		testJavaAsyncService.invoke(new ClientAppServiceSharedPayloadObj("message content", false));
		verifyServiceCallsViaBam(serviceName);
		
		assertTrue("Test harness topic never called", ((Boolean)ServiceCallInformationHolder.stuff.get("TestHarnessCalled")).booleanValue());
		assertTrue("Cliet1 app topic never called", ((Boolean)ServiceCallInformationHolder.stuff.get("Client1Called")).booleanValue());
	}
	
	@Test public void testSuccessfullyCallingSOAPTopic() throws Exception {
//		ensure test harness has entries for TestClient1
		((Runnable) KSBResourceLoaderFactory.getRemoteResourceLocator()).run();
		
		QName serviceName = new QName("testNameSpace", "soap-repeatTopic");
		
		SOAPService testJavaAsyncService = (SOAPService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName);
		testJavaAsyncService.doTheThing("The param");
		verifyServiceCallsViaBam(serviceName);
		
		assertTrue("Test harness topic never called", ((Boolean)ServiceCallInformationHolder.stuff.get("TestHarnessCalled")).booleanValue());
		assertTrue("Cliet1 app topic never called", ((Boolean)ServiceCallInformationHolder.stuff.get("Client1SOAPServiceCalled")).booleanValue());
	}
	
	
	private void verifyServiceCallsViaBam(QName serviceName) throws Exception {
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
		//only 2 calls for server and client on the remote client1 app the other is locally and 
		//thus not recorded by the bam
		assertTrue("No client call recorded", foundClientCall);
		assertTrue("No service call recorded", foundServiceCall);
		assertEquals("Wrong number of calls recorded", 2, bamCalls.size());
	}
}
