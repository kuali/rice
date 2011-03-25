/*
 * Copyright 2006-2011 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.rice.ksb.messaging;

import org.junit.Test;
import org.kuali.rice.core.api.config.property.Config;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.ksb.messaging.bam.BAMTargetEntry;
import org.kuali.rice.ksb.messaging.bam.service.BAMService;
import org.kuali.rice.ksb.messaging.service.KSBXMLService;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.kuali.rice.ksb.test.KSBTestCase;

import javax.xml.namespace.QName;
import java.util.List;

import static org.junit.Assert.assertTrue;


/**
 * Test store and forward capabilities in calling services
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StoreAndForwardTest extends KSBTestCase {

	public boolean startClient1() {
		return true;
	}
	

	@Test public void testServiceCall() throws Exception {
		ConfigContext.getCurrentContextConfig().putProperty(Config.STORE_AND_FORWARD, "true");
		QName serviceName = new QName("TestCl1", "testXmlAsyncService");
		KSBXMLService testXmlAsyncService = (KSBXMLService) KSBServiceLocator.getMessageHelper().getServiceAsynchronously(serviceName);
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
