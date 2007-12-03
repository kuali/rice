/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package edu.iu.uis.eden.messaging;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.bus.test.KSBTestCase;

import edu.iu.uis.eden.messaging.callbacks.SimpleCallback;

/**
 * Test that a context object passed through messaging is preserved in
 * 
 * async queue async topic
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class ContextObjectMessagingTest extends KSBTestCase {

    public boolean startClient1() {
	return true;
    }

    @Test
    public void testCallingQueueAsnyc() throws Exception {
	
	KSBTestUtils.setMessagingToAsync();
	QName serviceName = new QName("testAppsSharedQueue", "sharedQueue");
	String contextObject = "my_context_object";
	SimpleCallback callback = new SimpleCallback();
	
		KEWJavaService testJavaAsyncService = (KEWJavaService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName, callback, contextObject);
	    
	synchronized (callback) {
	    testJavaAsyncService.invoke(new ClientAppServiceSharedPayloadObj("message content", false));
	    callback.waitForAsyncCall();
	}

	Object contextAfterMessaging = callback.getMethodCall().getContext();
	assertEquals(contextObject, contextAfterMessaging);
    }

    @Test
    public void testCallingAsyncTopics() throws Exception {
	KSBTestUtils.setMessagingToAsync();
	QName serviceName = new QName("testAppsSharedTopic", "sharedTopic");

	SimpleCallback callback = new SimpleCallback();
	String contextObject = "my_context_object";
		KEWJavaService testJavaAsyncService = (KEWJavaService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName, callback, contextObject);
	
	synchronized (callback) {
	    testJavaAsyncService.invoke(new ClientAppServiceSharedPayloadObj("message content", false));
	    callback.waitForAsyncCall();    
	}
	
	Object contextAfterMessaging = callback.getMethodCall().getContext();
	assertEquals(contextObject, contextAfterMessaging);
    }

}
