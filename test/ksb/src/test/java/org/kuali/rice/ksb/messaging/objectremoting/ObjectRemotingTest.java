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
package org.kuali.rice.ksb.messaging.objectremoting;

import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.junit.Test;
import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.reflect.ObjectDefinition;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.ksb.messaging.RemotedServiceRegistry;
import org.kuali.rice.ksb.messaging.bam.BAMTargetEntry;
import org.kuali.rice.ksb.messaging.bam.service.BAMService;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.kuali.rice.ksb.test.KSBTestCase;
import org.kuali.rice.ksb.testclient1.RemotedObject;
import org.kuali.rice.ksb.testclient1.TestClient1ObjectToBeRemoted;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


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
		for (Map.Entry<ClassLoader, Config> configEntry : ConfigContext.getConfigs()) {
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
		assertTrue(verifyServiceCallsViaBam(QName.valueOf("{TestCl1}org.kuali.rice.ksb.testclient1.TestClient1ObjectToBeRemoted0"), "invoke", true));
		assertTrue(verifyServiceCallsViaBam(QName.valueOf("{TestCl1}org.kuali.rice.ksb.testclient1.TestClient1ObjectToBeRemoted0"), "invoke", false));
		assertTrue(verifyServiceCallsViaBam(QName.valueOf("{TestCl1}ObjectRemoterService"), "getRemotedClassURL", true));
		assertTrue(verifyServiceCallsViaBam(QName.valueOf("{TestCl1}ObjectRemoterService"), "getRemotedClassURL", false));
		assertTrue(verifyServiceCallsViaBam(QName.valueOf("{TestCl1}ObjectRemoterService"), "removeService", true));
	}

    public static boolean verifyServiceCallsViaBam(QName serviceName, String methodName, boolean serverInvocation) throws Exception {
        BAMService bamService = KSBServiceLocator.getBAMService();
        List<BAMTargetEntry> bamCalls = null;
        if (methodName == null) {
            bamCalls = bamService.getCallsForService(serviceName);
        } else {
            bamCalls = bamService.getCallsForService(serviceName, methodName);
        }

        if (bamCalls.size() == 0) {
            return false;
        }
        for (BAMTargetEntry bamEntry : bamCalls) {
            if (bamEntry.getServerInvocation() && serverInvocation) {
                return true;
            } else if (!serverInvocation) {
                return true;
            }
        }
        return false;
    }

}
