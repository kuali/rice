/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.messaging;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.bus.test.KSBTestCase;
import org.kuali.rice.RiceConstants;
import org.kuali.rice.core.Core;

import edu.iu.uis.eden.messaging.callbacks.SimpleCallback;

/**
 * verify that value1 and value2 are preserved when passed into message helper and making an async call.  
 * 
 * @author rkirkend
 *
 */
public class Value1AndValue2PersistedOnMessageCall extends KSBTestCase {
    
    @Test public void testCallingQueueAsnyc() throws Exception {
	KSBTestUtils.setMessagingToAsync();
	Core.getCurrentContextConfig().overrideProperty(RiceConstants.MESSAGING_OFF, "true");
	
	QName serviceName = QName.valueOf("{testAppsSharedTopic}sharedTopic");
	SimpleCallback callback = new SimpleCallback();
	String value1 = "value1";
	String value2 = "value2";
	KEWJavaService testJavaAsyncService = (KEWJavaService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName, callback, null, value1, value2);
	testJavaAsyncService.invoke(new ClientAppServiceSharedPayloadObj("message content", false));
	
	PersistedMessage message = KSBServiceLocator.getRouteQueueService().getNextDocuments(null).get(0);
	assertEquals("value1 incorrectly saved", value1, message.getValue1());
	assertEquals("value2 incorrectly saved", value2, message.getValue2());
	
    }

}
