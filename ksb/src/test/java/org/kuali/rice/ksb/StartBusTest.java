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
package org.kuali.rice.ksb;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.rice.ksb.messaging.KEWJavaService;
import org.kuali.rice.ksb.messaging.MessagingTestObject;
import org.kuali.rice.ksb.messaging.resourceloading.KSBResourceLoaderFactory;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.kuali.rice.ksb.test.KSBTestCase;


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
