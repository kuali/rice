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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionSynchronization;

import edu.iu.uis.eden.messaging.callbacks.SimpleCallback;
import edu.iu.uis.eden.messaging.serviceproxies.AsynchronousMessageCaller;

/**
 * Verify that messaging works in the context of a transaction and message invokation is done via the
 * {@link TransactionSynchronization} messagei
 * 
 * @author rkirkend
 * @author ewestfal
 * 
 */
public class TransactionMessagingTest extends KSBTestCase {

    @Override
    public boolean startClient1() {
	return true;
    }

    @Override
    public void setUp() throws Exception {
	super.setUp();
	AsynchronousMessageCaller.CALLED_TRANS_COMMITTED = false;
	AsynchronousMessageCaller.CALLED_TRANS_ROLLEDBACKED = false;
    }

    @Test
    public void testMessageSentOnCommittedTransaction() throws Exception {
	KSBTestUtils.setMessagingToAsync();

	KSBServiceLocator.getTransactionTemplate().execute(new TransactionCallback() {
	    public Object doInTransaction(TransactionStatus status) {

		QName serviceName = new QName("testAppsSharedQueue", "sharedQueue");
		SimpleCallback callback = new SimpleCallback();
		KEWJavaService testJavaAsyncService = (KEWJavaService) KSBServiceLocator.getMessageHelper()
			.getServiceAsynchronously(serviceName, callback);
		testJavaAsyncService.invoke(new ClientAppServiceSharedPayloadObj("message content", false));

		// this is a sanity check that we haven't sent the message before the trans is committed. dont remove this
                // line.
		assertFalse(AsynchronousMessageCaller.CALLED_TRANS_COMMITTED);
		return null;
	    }
	});

	assertTrue("Message not sent transactionallY", AsynchronousMessageCaller.CALLED_TRANS_COMMITTED);

    }

    @Test
    public void testMessageNotSentOnRolledBackTransaction() throws Exception {
	KSBTestUtils.setMessagingToAsync();

	KSBServiceLocator.getTransactionTemplate().execute(new TransactionCallback() {
	    public Object doInTransaction(TransactionStatus status) {

		QName serviceName = new QName("testAppsSharedQueue", "sharedQueue");
		SimpleCallback callback = new SimpleCallback();
		KEWJavaService testJavaAsyncService = (KEWJavaService) KSBServiceLocator.getMessageHelper()
			.getServiceAsynchronously(serviceName, callback);
		testJavaAsyncService.invoke(new ClientAppServiceSharedPayloadObj("message content", false));

		status.setRollbackOnly();
		// this is a sanity check that we haven't sent the message before the trans is committed. dont remove this
                // line.
		assertFalse(AsynchronousMessageCaller.CALLED_TRANS_ROLLEDBACKED);
		return null;
	    }
	});

	assertFalse("Message not sent transactionallY", AsynchronousMessageCaller.CALLED_TRANS_COMMITTED);
	assertTrue("Message not sent transactionallY", AsynchronousMessageCaller.CALLED_TRANS_ROLLEDBACKED);

    }

}
