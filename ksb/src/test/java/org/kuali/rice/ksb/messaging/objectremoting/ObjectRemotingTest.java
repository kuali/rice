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
package org.kuali.rice.ksb.messaging.objectremoting;

import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.rice.core.Core;
import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.reflect.ObjectDefinition;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.ksb.messaging.RemotedServiceRegistry;
import org.kuali.rice.ksb.services.KSBServiceLocator;
import org.kuali.rice.ksb.test.KSBTestCase;
import org.kuali.rice.ksb.testclient1.RemotedObject;
import org.kuali.rice.ksb.testclient1.TestClient1ObjectToBeRemoted;
import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;


public class ObjectRemotingTest extends KSBTestCase {
	
	@Override
	public boolean startClient1() {
		return true;
	}

	@Test
	public void testInvokingRemotedObject() throws Exception {

		KSBServiceLocator.getTransactionTemplate().execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus status) {

				ObjectDefinition od = new ObjectDefinition(TestClient1ObjectToBeRemoted.class.getName(), "TestCl1");
				RemotedObject remotedOjb = (RemotedObject) GlobalResourceLoader.getObject(od);
				String returnParam = remotedOjb.invoke("call1");
				assertEquals(TestClient1ObjectToBeRemoted.METHOD_INVOKED, returnParam);
				return null;
			}
		});
		
		boolean madeTempServicesCheck = false;
		for (Map.Entry<ClassLoader, Config> configEntry : Core.getConfigs()) {
			if (configEntry.getKey() instanceof WebAppClassLoader) {
				ClassLoader old = Thread.currentThread().getContextClassLoader();
				//to make KSBServiceLocator select services from Client1WebApp
				Thread.currentThread().setContextClassLoader(configEntry.getKey());
				RemotedServiceRegistry serviceRegistry = KSBServiceLocator.getServiceDeployer();
				try {
					assertTrue(serviceRegistry.getPublishedTempServices().isEmpty());
					madeTempServicesCheck = true;
				} finally {
					Thread.currentThread().setContextClassLoader(old);	
				}
			}
		}
		assertTrue(madeTempServicesCheck);
		
		//verify service worked with bam
		assertTrue(verifyServiceCallsViaBam(QName.valueOf("{TestCl1}org.kuali.rice.kew.testclient1.TestClient1ObjectToBeRemoted0"), "invoke", true));
		assertTrue(verifyServiceCallsViaBam(QName.valueOf("{TestCl1}org.kuali.rice.kew.testclient1.TestClient1ObjectToBeRemoted0"), "invoke", false));
		assertTrue(verifyServiceCallsViaBam(QName.valueOf("{TestCl1}ObjectRemoterService"), "getRemotedClassURL", true));
		assertTrue(verifyServiceCallsViaBam(QName.valueOf("{TestCl1}ObjectRemoterService"), "getRemotedClassURL", false));
		assertTrue(verifyServiceCallsViaBam(QName.valueOf("{TestCl1}ObjectRemoterService"), "removeService", true));
	}

}