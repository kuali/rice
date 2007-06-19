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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.RiceConstants;
import org.kuali.rice.core.Core;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import edu.iu.uis.eden.messaging.callforwarding.ForwardedCallHandler;
import edu.iu.uis.eden.messaging.resourceloading.KSBResourceLoaderFactory;

/**
 * Handles invocation of a {@link PersistedMessage}.
 *
 * @author rkirkend
 */
public class MessageServiceInvoker implements Runnable {

	protected static final Logger LOG = Logger.getLogger(MessageServiceInvoker.class);

	private PersistedMessage message;
	private Object service;
	private AsynchronousCall methodCall;

	public MessageServiceInvoker(PersistedMessage message) {
		this.message = message;
	}

	public void run() {
		LOG.debug("calling service from persisted message " + getMessage().getRouteQueueId());
		try {
			KSBServiceLocator.getTransactionTemplate().execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus status) {
					Object result = null;
					AsynchronousCall methodCall = (AsynchronousCall) KSBServiceLocator.getMessageHelper().deserializeObject(getMessage().getPayload());
					
					try {
						result = invokeService(methodCall);
						KSBServiceLocator.getRouteQueueService().delete(getMessage());
					} catch (Throwable t) {
						LOG.warn("Caught throwable making async service call " + methodCall, t);
						throw new MessageProcessingException(t);
					} finally {
						try {
							notifyOnCallback(methodCall, result);	
						} catch (Exception e) {
							LOG.warn("Exception caught notifying callback", e);
						}
						try {
							notifyGlobalCallbacks(methodCall, result);	
						} catch (Exception e) {
							LOG.warn("Exception caught notifying callback", e);
						}
						
					}
					try {
						requeueForRepeatCall(methodCall);
					} catch (Throwable t) {
						LOG.warn("Caught throwable checking/requeing service call for repeat invokation.  Putting in Exception Routing", t);
						throw new MessageProcessingException(t);
					}
					return null;
				}
			});
		} catch (Throwable t) {
			placeInExceptionRouting(t, getMethodCall(), getService());
		}
	}
	
	public void placeInExceptionRouting(Throwable t, AsynchronousCall call, Object service) {
		LOG.error("Error processing message: " + this.message, t);
		final Throwable throwable;
		if (t instanceof MessageProcessingException) {
			throwable = t.getCause();
		} else {
			throwable = t;
		}
		KSBServiceLocator.getExceptionRoutingService().placeInExceptionRouting(throwable, this.message, service);
	}
	
	/**
	 * Invokes the AsynchronousCall represented on the methodCall on the service contained in 
	 * the ServiceInfo object on the AsynchronousCall.
	 *
	 */
	public Object invokeService(AsynchronousCall methodCall) throws Exception {
		this.methodCall = methodCall;
		ServiceInfo serviceInfo = methodCall.getServiceInfo();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Attempting to call service " + serviceInfo.getQname());
		}
		
		if (Core.getCurrentContextConfig().getStoreAndForward() && ! methodCall.isIgnoreStoreAndForward()) {
			QName serviceName = serviceInfo.getQname();
			RemoteResourceServiceLocator remoteResourceLocator = KSBResourceLoaderFactory.getRemoteResourceLocator();
			QName storeAndForwardName = new QName(serviceName.getNamespaceURI(), serviceName.getLocalPart() + RemotedServiceRegistry.FORWARD_HANDLER_SUFFIX);
			List<RemotedServiceHolder> forwardServices = remoteResourceLocator.getAllServices(storeAndForwardName);
			if (forwardServices.isEmpty()) {
				LOG.warn("Could not find store and forward service " + storeAndForwardName + ".  Defaulting to regular messaging.");
			} else {
				serviceInfo = forwardServices.get(0).getServiceInfo();
			}
			ForwardedCallHandler service = (ForwardedCallHandler) getService(serviceInfo);
			this.message.setMethodCall(methodCall);
			service.handleCall(this.message);
			return null;
		}

		Object service = getService(serviceInfo);
		Method method = service.getClass().getMethod(methodCall.getMethodName(), methodCall.getParamTypes());
		return method.invoke(service, methodCall.getArguments());
	}
	
	public Object getService(ServiceInfo serviceInfo) {
		Object service;
		if (serviceInfo.getServiceDefinition().getQueue()) {
			service = getQueueService(serviceInfo);
		} else {
			service = getTopicService(serviceInfo);
		}
		return service;
	}
	
	/**
	 * Get the service as a topic. This means we want to contact every service
	 * that is a part of this topic. We've grabbed all the services that are a
	 * part of this topic and we want to make sure that we get everyone of them =
	 * that is we want to circumvent loadbalancing and therefore not ask for the
	 * service by it's name but the url to get the exact service we want.
	 * 
	 * @param serviceInfo
	 * @return
	 */
	public Object getTopicService(ServiceInfo serviceInfo) {
		//get the service locally if we have it so we don't go through any remoting
		RemotedServiceRegistry remoteRegistry = KSBServiceLocator.getServiceDeployer(); 
		Object service = remoteRegistry.getService(serviceInfo.getQname(), serviceInfo.getEndpointUrl());
		if (service != null) {
			return service;
		}
		RemoteResourceServiceLocator remoteResourceLocator = KSBResourceLoaderFactory.getRemoteResourceLocator();
		return remoteResourceLocator.getService(serviceInfo.getQname(), serviceInfo.getEndpointUrl());
	}
	
	/**
	 * Because this is a queue we just need to grab one.
	 * 
	 * @param serviceInfo
	 * @return
	 */
	public Object getQueueService(ServiceInfo serviceInfo) {
		RemotedServiceRegistry remoteRegistry = KSBServiceLocator.getServiceDeployer(); 
		Object service = remoteRegistry.getLocalService(serviceInfo.getQname());
		if (service != null) {
			return service;
		} 
		//get client to remote service if not in our local repository
		return GlobalResourceLoader.getService(serviceInfo.getQname());
	}

	/**
	 * Used in case the thread that dumped this work into the queue is waiting for the work to be done to 
	 * continue processing.
	 * 
	 * @param callback
	 */
	public void notifyOnCallback(AsynchronousCall methodCall, Object callResult) {
		AsynchronousCallback callback = methodCall.getCallback();
		notifyOnCallback(methodCall, callback, callResult);
	}
	
	public void notifyGlobalCallbacks(AsynchronousCall methodCall, Object callResult) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Notifying global callbacks");
		}
		for (AsynchronousCallback globalCallBack : GlobalCallbackRegistry.getCallbacks()) {
			notifyOnCallback(methodCall, globalCallBack, callResult);
		}
	}
	
	public void notifyOnCallback(AsynchronousCall methodCall, AsynchronousCallback callback, Object callResult) {
		if (callback != null) {
			try {
				synchronized (callback) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Notifying callback " + callback + " with callResult " + callResult);
					}
					callback.notify();
					if (callResult instanceof Serializable || callResult == null) {
						callback.callback((Serializable) callResult, methodCall);
					} else {
						// may never happen
						LOG.warn("Attempted to call callback with non-serializable object.");
					}
				}
			} catch (Throwable t) {
				LOG.error("Caught throwable from callback object " + callback.getClass(), t);
			}
		}
	}
	
	protected void requeueForRepeatCall(AsynchronousCall methodCall) {
		ServiceInfo serviceInfo = methodCall.getServiceInfo();
		if (! serviceInfo.getServiceDefinition().getQueue()) {
			return;
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Checking if service " + serviceInfo + " is configured for a repeatable delay");
		}
		Long repeatCallInterval = methodCall.getRepeatCallTimeIncrement();
		if (repeatCallInterval == null) {
			LOG.debug("Service " + serviceInfo + " does not have a configured repeatind delayed call");
			return;
		}
		Timestamp lastInvokation = getMessage().getQueueDate();
		Timestamp newInvokationDate = new Timestamp(lastInvokation.getTime() + repeatCallInterval);
		PersistedMessage nextMessage = getMessage();
		nextMessage.setLockVerNbr(null);
		nextMessage.setRouteQueueId(null);
		nextMessage.setRetryCount(0);
		nextMessage.setQueueDate(newInvokationDate);
		nextMessage.setQueueStatus(RiceConstants.ROUTE_QUEUE_QUEUED);
		KSBServiceLocator.getRouteQueueService().save(nextMessage);
	}

	public PersistedMessage getMessage() {
		return this.message;
	}

	public void setMessage(PersistedMessage message) {
		this.message = message;
	}

	public Object getService() {
		return this.service;
	}

	public AsynchronousCall getMethodCall() {
		return this.methodCall;
	}

	public void setMethodCall(AsynchronousCall methodCall) {
		this.methodCall = methodCall;
	}

	public void setService(Object service) {
		this.service = service;
	}
}
