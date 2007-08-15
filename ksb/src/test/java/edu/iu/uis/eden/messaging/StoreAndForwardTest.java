/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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

/**
 * Test store and forward capabilities in calling services
 * 
 * @author rkirkend
 */
public class StoreAndForwardTest extends KSBTestCase {

	public boolean startClient1() {
		return true;
	}
	

	@Test public void testServiceCall() throws Exception {
		Core.getCurrentContextConfig().overrideProperty(Config.STORE_AND_FORWARD, "true");
		QName serviceName = new QName("TestCl1", "testXmlAsyncService");
		KEWXMLService testXmlAsyncService = (KEWXMLService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName);
		testXmlAsyncService.invoke("message content");
		verifyServiceCalls();
	}
	
	private void verifyServiceCalls() throws Exception {
		BAMService bamService = KSBServiceLocator.getBAMService();
		List<BAMTargetEntry> bamCalls = bamService.getCallsForService(QName.valueOf("{TestCl1}testXmlAsyncService-forwardHandler"));
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
