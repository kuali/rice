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

import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.bus.test.KSBTestCase;
import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;

import edu.iu.uis.eden.messaging.bam.BAMService;
import edu.iu.uis.eden.messaging.bam.BAMTargetEntry;
import edu.iu.uis.eden.messaging.callbacks.SimpleCallback;
import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;

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
	    KSBTestUtils.setMessagingToAsync();
		
		QName serviceName = new QName("TestCl1", "testJavaAsyncService");
		SimpleCallback callback = new SimpleCallback();
		KEWJavaService testJavaAsyncService = (KEWJavaService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName, callback);
		testJavaAsyncService.invoke(new MessagingTestObject("message content"));
		callback.waitForAsyncCall();
		verifyServiceCalls(serviceName);
	}
	
	@Test public void testAsyncXmlCall() throws Exception {
	    KSBTestUtils.setMessagingToAsync();
		
		QName serviceName = new QName("TestCl1", "testXmlAsyncService");
		SimpleCallback callback = new SimpleCallback();
		KEWXMLService testXmlAsyncService = (KEWXMLService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName, callback);
		testXmlAsyncService.invoke("message content");
		callback.waitForAsyncCall();
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