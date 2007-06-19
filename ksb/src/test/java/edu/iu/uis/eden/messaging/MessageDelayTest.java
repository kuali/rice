package edu.iu.uis.eden.messaging;

import java.util.Date;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.bus.test.KSBTestCase;
import org.kuali.rice.RiceConstants;
import org.kuali.rice.core.Core;

import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;

/**
 * Test message delay scenarios
 * 
 * @author rkirkend
 *
 */
public class MessageDelayTest extends KSBTestCase {

	public boolean startClient1() {
		return true;
	}
	
	/**
	 * Persisted messages need to work with message delays
	 * 
	 * @throws Exception
	 */
	@Test public void testDelayedCalledUsingTopic() throws Exception {
		Core.getCurrentContextConfig().overrideProperty(RiceConstants.MESSAGE_PERSISTENCE, "default");
		QName serviceName = new QName("testAppsSharedTopic", "sharedTopic");

		RemoteResourceServiceLocatorImpl remoteResourceServiceLocator = (RemoteResourceServiceLocatorImpl)KSBResourceLoaderFactory.getRemoteResourceLocator();
		remoteResourceServiceLocator.run();
		TestCallback callback = new TestCallback();
		//25 second wait, should guarantee a real wait on the part of the system and not slowness interpretted as a wait.
		long deliverDateMillis = System.currentTimeMillis() + 25000;
		System.out.println("delivery date millis " + deliverDateMillis);
		Date deliveryDate = new Date(deliverDateMillis);
		
		KEWJavaService topic = (KEWJavaService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName, deliveryDate, callback);
		topic.invoke(new ClientAppServiceSharedPayloadObj("message content", false));
		callback.pauseUntilNumberCallbacksUsingStaticCounter(2, null);
		
		assertTrue("Service called before wait time expired", deliveryDate.getTime() <= TestCallback.CURRENT_MILLIS_WHEN_CALLED.get(0));
		assertTrue("Service called before wait time expired", deliveryDate.getTime() <= TestCallback.CURRENT_MILLIS_WHEN_CALLED.get(1));
		assertEquals("Should only be 2 called to the topic used", 2, TestCallback.SERVICE_CALL_COUNT_TRACKED.get(QName.valueOf("{testAppsSharedTopic}sharedTopic")));
		
	}
}