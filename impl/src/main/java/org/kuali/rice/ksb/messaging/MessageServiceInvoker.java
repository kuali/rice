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

package org.kuali.rice.ksb.messaging;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.ksb.messaging.callforwarding.ForwardedCallHandler;
import org.kuali.rice.ksb.messaging.resourceloader.KSBResourceLoaderFactory;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.kuali.rice.ksb.util.KSBConstants;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import javax.xml.namespace.QName;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.List;

/**
 * Handles invocation of a {@link PersistedMessageBO}.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MessageServiceInvoker implements Runnable {

    protected static final Logger LOG = Logger.getLogger(MessageServiceInvoker.class);

    private PersistedMessageBO message;
    private Object service;
    private AsynchronousCall methodCall;

    public MessageServiceInvoker(PersistedMessageBO message) {
        this.message = message;
    }

    public void run() {
        LOG.debug("calling service from persisted message " + getMessage().getRouteQueueId());
        Object result = null;
        try {
            result = KSBServiceLocator.getTransactionTemplate().execute(new TransactionCallback() {
                public Object doInTransaction(TransactionStatus status) {
                    AsynchronousCall methodCall = getMessage().getPayload().getMethodCall();
                    Object result = null;
                    try {
                        result = invokeService(methodCall);
                        KSBServiceLocator.getRouteQueueService().delete(getMessage());
                    } catch (Throwable t) {
                        LOG.warn("Caught throwable making async service call " + methodCall, t);
                        throw new MessageProcessingException(t);
                    }
                    return result;
                }
            });
        } catch (Throwable t) {
        	// if we are in synchronous mode, we can't put the message into exception routing, let's instead throw the error up to the calling code
        	// however, for the purposes of the unit tests, even when in synchronous mode, we want to call the exception routing service, so check a parameter for that as well
        	boolean allowSyncExceptionRouting = new Boolean(ConfigContext.getCurrentContextConfig().getProperty(KSBConstants.Config.ALLOW_SYNC_EXCEPTION_ROUTING));
     	 	if (!allowSyncExceptionRouting && KSBConstants.MESSAGING_SYNCHRONOUS.equals(ConfigContext.getCurrentContextConfig().getProperty(KSBConstants.Config.MESSAGE_DELIVERY))) {
     	 		if (t instanceof RuntimeException) {
     	 			throw (RuntimeException)t;
     	 		}
     	 		throw new RiceRuntimeException(t);
     	 	} else {
     	 		placeInExceptionRouting(t, getMethodCall(), getService());
     	 	}
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
    }

    /**
     * Executed when an exception is encountered during message invocation.
     * Attempts to call the ExceptionHandler for the message, if that fails it
     * will attempt to set the status of the message in the queue to
     * "EXCEPTION".
     */
    protected void placeInExceptionRouting(Throwable t, AsynchronousCall call, Object service) {
        LOG.error("Error processing message: " + this.message, t);
        final Throwable throwable;
        if (t instanceof MessageProcessingException) {
            throwable = t.getCause();
        } else {
            throwable = t;
        }
        try {
        	try {
        		KSBServiceLocator.getExceptionRoutingService().placeInExceptionRouting(throwable, this.message, service);
        	} catch (Throwable t1) {
        		KSBServiceLocator.getExceptionRoutingService().placeInExceptionRoutingLastDitchEffort(throwable, this.message, service);
        	}
        } catch (Throwable t2) {
            LOG.error("An error was encountered when invoking exception handler for message. Attempting to change message status to EXCEPTION.", t2);
            message.setQueueStatus(KSBConstants.ROUTE_QUEUE_EXCEPTION);
            message.setQueueDate(new Timestamp(System.currentTimeMillis()));
            try {
                KSBServiceLocator.getRouteQueueService().save(message);
            } catch (Throwable t3) {
                LOG.fatal("Failed to flip status of message to EXCEPTION!!!", t3);
            }
        }
    }

    /**
     * Invokes the AsynchronousCall represented on the methodCall on the service
     * contained in the ServiceInfo object on the AsynchronousCall.
     * 
     */
    protected Object invokeService(AsynchronousCall methodCall) throws Exception {
        this.methodCall = methodCall;
        ServiceInfo serviceInfo = methodCall.getServiceInfo();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Attempting to call service " + serviceInfo.getQname());
        }

        if (ConfigContext.getCurrentContextConfig().getStoreAndForward() && !methodCall.isIgnoreStoreAndForward()) {
            QName serviceName = serviceInfo.getQname();
            RemoteResourceServiceLocator remoteResourceLocator = KSBResourceLoaderFactory.getRemoteResourceLocator();
            QName storeAndForwardName = new QName(serviceName.getNamespaceURI(), serviceName.getLocalPart() + KSBConstants.FORWARD_HANDLER_SUFFIX);
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

    protected Object getService(ServiceInfo serviceInfo) {
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
    protected Object getTopicService(ServiceInfo serviceInfo) {
        // get the service locally if we have it so we don't go through any
        // remoting
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
    protected Object getQueueService(ServiceInfo serviceInfo) {
        RemotedServiceRegistry remoteRegistry = KSBServiceLocator.getServiceDeployer();
        Object service = remoteRegistry.getLocalService(serviceInfo.getQname());
        if (service != null) {
            return service;
        }
        // get client to remote service if not in our local repository
        return GlobalResourceLoader.getService(serviceInfo.getQname());
    }

    /**
     * Used in case the thread that dumped this work into the queue is waiting
     * for the work to be done to continue processing.
     * 
     * @param callback
     */
    protected void notifyOnCallback(AsynchronousCall methodCall, Object callResult) {
        AsynchronousCallback callback = methodCall.getCallback();
        notifyOnCallback(methodCall, callback, callResult);
    }

    protected void notifyGlobalCallbacks(AsynchronousCall methodCall, Object callResult) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Notifying global callbacks");
        }
        for (AsynchronousCallback globalCallBack : GlobalCallbackRegistry.getCallbacks()) {
            notifyOnCallback(methodCall, globalCallBack, callResult);
        }
    }

    protected void notifyOnCallback(AsynchronousCall methodCall, AsynchronousCallback callback, Object callResult) {
        if (callback != null) {
            try {
                synchronized (callback) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Notifying callback " + callback + " with callResult " + callResult);
                    }
                    callback.notifyAll();
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

    public PersistedMessageBO getMessage() {
        return this.message;
    }

    public void setMessage(PersistedMessageBO message) {
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
