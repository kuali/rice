package org.kuali.bus;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.bus.test.KSBTestCase;

import edu.iu.uis.eden.messaging.KEWJavaService;
import edu.iu.uis.eden.messaging.MessagingTestObject;
import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;

public class StartBusTest extends KSBTestCase {

	@Override
	public boolean startClient1() {
		return true;
	}

	@Test
	public void testStartTheBus() {
		QName serviceName = new QName("TestCl1", "testJavaAsyncService");
		
		((Runnable)KSBResourceLoaderFactory.getRemoteResourceLocator()).run();
		
		KEWJavaService testJavaAsyncService = (KEWJavaService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName);
		testJavaAsyncService.invoke(new MessagingTestObject("message content"));
		
		// verifyServiceCalls(serviceName);
	}

	// @Test public void testStartClient1() throws Exception {
	// new TestClient1().start();
	// }
	//	
	// @Test public void testStartClient2() throws Exception {
	// new TestClient1().start();
	// new TestClient2().start();
	// }
	//	
}
