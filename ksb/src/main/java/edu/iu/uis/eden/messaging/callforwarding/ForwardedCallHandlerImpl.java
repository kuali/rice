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
package edu.iu.uis.eden.messaging.callforwarding;

import java.sql.Timestamp;

import javax.transaction.Status;

import org.apache.log4j.Logger;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.RiceConstants;
import org.kuali.rice.core.Core;
import org.kuali.rice.util.RiceUtilities;

import edu.iu.uis.eden.messaging.AsynchronousCall;
import edu.iu.uis.eden.messaging.MessageServiceInvoker;
import edu.iu.uis.eden.messaging.PersistedMassagePayload;
import edu.iu.uis.eden.messaging.PersistedMessage;
import edu.iu.uis.eden.messaging.serviceproxies.AsynchronousMessageCaller;

/**
 * @author rkirkend
 */
public class ForwardedCallHandlerImpl implements ForwardedCallHandler {

	private static final Logger LOG = Logger.getLogger(ForwardedCallHandlerImpl.class);

	public void handleCall(PersistedMessage message) throws Exception {
		LOG.debug("Recieved forwared message from service " + message.getMethodCall().getServiceInfo().getQname());
		PersistedMessage copy = new PersistedMessage();
		copy.setExpirationDate(message.getExpirationDate());
		copy.setIpNumber(RiceUtilities.getIpNumber());
		copy.setMessageEntity(message.getMessageEntity());
		copy.setMethodCall(message.getMethodCall());
		copy.setMethodName(message.getMethodName());
		copy.setQueueDate(new Timestamp(System.currentTimeMillis()));
		copy.setQueuePriority(message.getQueuePriority());
		copy.setQueueStatus(RiceConstants.ROUTE_QUEUE_QUEUED);
		copy.setRetryCount(message.getRetryCount());
		AsynchronousCall methodCall = message.getPayload().getMethodCall();
		methodCall.setIgnoreStoreAndForward(true);
		copy.setPayload(new PersistedMassagePayload(methodCall, copy));
		copy.setServiceName(message.getServiceName());
		saveMessage(copy);
		executeMessage(copy);

	}

	// TODO copied from AsynchronousServiceCallProxy
	protected void saveMessage(PersistedMessage message) {
		if (new Boolean(Core.getCurrentContextConfig().getProperty(RiceConstants.MESSAGE_PERSISTENCE))) {
		    if (LOG.isDebugEnabled()) {
			LOG.debug("Persisting Message " + message);
}		    message.setQueueStatus(RiceConstants.ROUTE_QUEUE_ROUTING);
		    KSBServiceLocator.getRouteQueueService().save(message);
		}
	    }

	// TODO copied from AsynchronousServiceCallProxy
	protected void executeMessage(PersistedMessage message) throws Exception {
	    if (!new Boolean(Core.getCurrentContextConfig().getProperty(RiceConstants.MESSAGING_OFF))) {
		if (KSBServiceLocator.getJtaTransactionManager().getStatus() == Status.STATUS_ACTIVE) {
		    KSBServiceLocator.getJtaTransactionManager().getTransaction().registerSynchronization(
			    new AsynchronousMessageCaller(message));
		} else {
		    KSBServiceLocator.getThreadPool().execute(new MessageServiceInvoker(message));
		}
	    }
	}
}