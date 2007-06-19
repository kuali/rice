package edu.iu.uis.eden.messaging;

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.bus.test.KSBTestCase;

import edu.iu.uis.eden.messaging.bam.BAMService;
import edu.iu.uis.eden.messaging.bam.BAMTargetEntry;

/**
 * Tests calling services in a very simple scenario.  This test could probably go 
 * now that more 'feature-full' tests are out there.
 * 
 * @author rkirkend
 *
 */
public class SimpleServiceCallTest extends KSBTestCase {

	public boolean startClient1() {
		return true;
	}
	
	@Test public void testAsyncJavaCall() throws Exception  {
		QName serviceName = new QName("TestCl1", "testJavaAsyncService");
		
		KEWJavaService testJavaAsyncService = (KEWJavaService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName);
		testJavaAsyncService.invoke(new MessagingTestObject("message content"));
		verifyServiceCalls(serviceName);
	}
	
	@Test public void testAsyncXmlCall() throws Exception {
		QName serviceName = new QName("TestCl1", "testXmlAsyncService");
		KEWXMLService testXmlAsyncService = (KEWXMLService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName);
		testXmlAsyncService.invoke("message content");
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
	}
	
}