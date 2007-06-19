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
 * @author rkirkend
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
			methodCall = (AsynchronousCall) KSBServiceLocator.getMessageHelper().deserializeObject(message.getPayload());
		}
		message.setMethodCall(methodCall);
		MessageExceptionHandler exceptionHandler = remoteResourceServiceLocator.getMessageExceptionHandler(methodCall.getServiceInfo().getQname());
		exceptionHandler.handleException(throwable, message, service);
	}
}