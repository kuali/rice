package edu.iu.uis.eden.messaging.objectremoting;

import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.bus.test.KSBTestCase;
import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;
import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import edu.iu.uis.eden.messaging.RemotedServiceRegistry;
import edu.iu.uis.eden.testclient1.RemotedObject;
import edu.iu.uis.eden.testclient1.TestClient1ObjectToBeRemoted;

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
		
		Map<ClassLoader, Config> configs = Core.getCONFIGS();
		boolean madeTempServicesCheck = false;
		for (Map.Entry<ClassLoader, Config> configEntry : configs.entrySet()) {
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
		assertTrue(verifyServiceCallsViaBam(QName.valueOf("{TestCl1}edu.iu.uis.eden.testclient1.TestClient1ObjectToBeRemoted0"), "invoke", true));
		assertTrue(verifyServiceCallsViaBam(QName.valueOf("{TestCl1}edu.iu.uis.eden.testclient1.TestClient1ObjectToBeRemoted0"), "invoke", false));
		assertTrue(verifyServiceCallsViaBam(QName.valueOf("{TestCl1}ObjectRemoterService"), "getRemotedClassURL", true));
		assertTrue(verifyServiceCallsViaBam(QName.valueOf("{TestCl1}ObjectRemoterService"), "getRemotedClassURL", false));
		assertTrue(verifyServiceCallsViaBam(QName.valueOf("{TestCl1}ObjectRemoterService"), "removeService", true));
	}

}