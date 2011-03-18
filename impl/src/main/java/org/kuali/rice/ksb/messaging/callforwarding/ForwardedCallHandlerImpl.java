/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
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
package org.kuali.rice.ksb.messaging.callforwarding;

import org.apache.log4j.Logger;
import org.kuali.rice.core.util.RiceUtilities;
import org.kuali.rice.ksb.messaging.AsynchronousCall;
import org.kuali.rice.ksb.messaging.PersistedMessageBO;
import org.kuali.rice.ksb.messaging.PersistedMessagePayload;
import org.kuali.rice.ksb.messaging.serviceproxies.MessageSender;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.kuali.rice.ksb.util.KSBConstants;

import java.sql.Timestamp;


/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ForwardedCallHandlerImpl implements ForwardedCallHandler {

	private static final Logger LOG = Logger.getLogger(ForwardedCallHandlerImpl.class);

	public void handleCall(PersistedMessageBO message) throws Exception {
		LOG.debug("Recieved forwared message from service " + message.getMethodCall().getServiceInfo().getQname());
		PersistedMessageBO copy = new PersistedMessageBO();
		copy.setExpirationDate(message.getExpirationDate());
		copy.setIpNumber(RiceUtilities.getIpNumber());
		copy.setServiceNamespace(message.getServiceNamespace());
		copy.setMethodCall(message.getMethodCall());
		copy.setMethodName(message.getMethodName());
		copy.setQueueDate(new Timestamp(System.currentTimeMillis()));
		copy.setQueuePriority(message.getQueuePriority());
		copy.setQueueStatus(KSBConstants.ROUTE_QUEUE_QUEUED);
		copy.setRetryCount(message.getRetryCount());
		AsynchronousCall methodCall = message.getPayload().getMethodCall();
		methodCall.setIgnoreStoreAndForward(true);
		copy.setPayload(new PersistedMessagePayload(methodCall, copy));
		copy.setServiceName(message.getServiceName());
		message.setQueueStatus(KSBConstants.ROUTE_QUEUE_ROUTING);
		KSBServiceLocator.getRouteQueueService().save(copy);
		MessageSender.sendMessage(copy);
	}
}
