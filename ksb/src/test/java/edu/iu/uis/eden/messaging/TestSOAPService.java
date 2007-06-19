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

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.bus.test.KSBTestCase;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.messaging.remotedservices.EchoService;
import edu.iu.uis.eden.messaging.remotedservices.SOAPService;

/**
 *
 * @author rkirkend
 */
public class TestSOAPService extends KSBTestCase {

	public boolean startClient1() {
		return true;
	}
	
	@Test public void testSimpleSOAPService() {
		EchoService echoService = (EchoService)GlobalResourceLoader.getService(new QName("TestCl1", "soap-echoService"));
		String result = echoService.trueEcho("Yo yo yo");
		assertNotNull(result);
		
		QName serviceName = new QName("testNameSpace", "soap-repeatTopic");
		SOAPService soapService = (SOAPService) GlobalResourceLoader.getService(serviceName);
		soapService.doTheThing("hello");
	}
}