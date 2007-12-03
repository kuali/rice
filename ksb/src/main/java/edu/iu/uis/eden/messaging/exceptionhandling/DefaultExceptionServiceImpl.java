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
package edu.iu.uis.eden.messaging.exceptionhandling;

import org.apache.log4j.Logger;
import org.kuali.bus.services.KSBServiceLocator;

import edu.iu.uis.eden.messaging.AsynchronousCall;
import edu.iu.uis.eden.messaging.PersistedMessage;
import edu.iu.uis.eden.messaging.RemoteResourceServiceLocator;
import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;

/**
 * Default implementation of {@link ExceptionRoutingService}.  Just saves 
 * the message in the queue as is, which should be marked Exception by the 
 * {@link MessageExceptionHandler}.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DefaultExceptionServiceImpl implements ExceptionRoutingService {
	
	private static final Logger LOG = Logger.getLogger(DefaultExceptionServiceImpl.class);

	public void placeInExceptionRouting(Throwable throwable, PersistedMessage message) {
		KSBServiceLocator.getRouteQueueService().save(message);
	}

	public void placeInExceptionRouting(Throwable throwable, PersistedMessage message, Object service) {
		LOG.error("Exception caught processing message " + message.getRouteQueueId() + " " + message.getServiceName() + ": " + throwable);
		
		RemoteResourceServiceLocator remoteResourceServiceLocator = KSBResourceLoaderFactory.getRemoteResourceLocator();
		AsynchronousCall methodCall = null;
		if (message.getMethodCall() != null) {
			methodCall = message.getMethodCall();
		} else {
			methodCall = message.getPayload().getMethodCall();
		}
		message.setMethodCall(methodCall);
		MessageExceptionHandler exceptionHandler = remoteResourceServiceLocator.getMessageExceptionHandler(methodCall.getServiceInfo().getQname());
		exceptionHandler.handleException(throwable, message, service);
	}
}