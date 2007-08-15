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
package edu.iu.uis.eden.messaging.serviceproxies;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import javax.transaction.Status;

import org.apache.log4j.Logger;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.RiceConstants;
import org.kuali.rice.core.Core;
import org.kuali.rice.proxy.BaseInvocationHandler;
import org.kuali.rice.proxy.TargetedInvocationHandler;
import org.kuali.rice.resourceloader.ContextClassLoaderProxy;
import org.kuali.rice.util.ClassLoaderUtils;

import edu.iu.uis.eden.messaging.AsynchronousCall;
import edu.iu.uis.eden.messaging.AsynchronousCallback;
import edu.iu.uis.eden.messaging.MessageServiceInvoker;
import edu.iu.uis.eden.messaging.PersistedMessage;
import edu.iu.uis.eden.messaging.RemotedServiceHolder;
import edu.iu.uis.eden.messaging.ServiceInfo;

/**
 * Standard default proxy used to call services asynchronously. Persists the method call to the db so call is never lost and
 * only sent when transaction is committed.
 * 
 * @author rkirkend
 * 
 */
public class AsynchronousServiceCallProxy extends BaseInvocationHandler implements TargetedInvocationHandler {

    private static final Logger LOG = Logger.getLogger(AsynchronousServiceCallProxy.class);

    private AsynchronousCallback callback;

    private List<RemotedServiceHolder> serviceDefs;

    private Serializable context;
    
    private String value1;
    private String value2;

    protected AsynchronousServiceCallProxy(List<RemotedServiceHolder> serviceDefs, AsynchronousCallback callback,
	    Serializable context, String value1, String value2) {
	this.serviceDefs = serviceDefs;
	this.callback = callback;
	this.context = context;
	this.value1 = value1;
	this.value2 = value2;
    }

    public static Object createInstance(List<RemotedServiceHolder> serviceDefs, AsynchronousCallback callback,
	    Serializable context, String value1, String value2) {
	if (serviceDefs == null || serviceDefs.isEmpty()) {
	    throw new RuntimeException("Cannot create service proxy, no service(s) passed in.");
	}
	return Proxy.newProxyInstance(ClassLoaderUtils.getDefaultClassLoader(), ContextClassLoaderProxy
		.getInterfacesToProxyIncludeSpring(serviceDefs.get(0).getService()), new AsynchronousServiceCallProxy(
		serviceDefs, callback, context, value1, value2));
    }

    @Override
    protected Object invokeInternal(Object proxy, Method method, Object[] arguments) throws Throwable {
	// there are multiple service calls to make in the case of topics.
	AsynchronousCall methodCall = null;
	PersistedMessage message = null;
	synchronized (this) {
	    // consider moving all this topic invocation stuff to the service
	    // invoker for speed reasons
	    for (RemotedServiceHolder remotedServiceHolder : this.serviceDefs) {
		ServiceInfo serviceInfo = remotedServiceHolder.getServiceInfo();
		methodCall = new AsynchronousCall(method.getParameterTypes(), arguments, serviceInfo, method.getName(),
			this.callback, this.context);
		message = KSBServiceLocator.getRouteQueueService().getMessage(serviceInfo, methodCall);
		message.setValue1(this.value1);
		message.setValue2(this.value2);
		saveMessage(message);
		executeMessage(message);
		// only do one iteration if this is a queue. The load balancing
		// will be handled when the service is
		// fetched by the MessageServiceInvoker through the GRL (and
		// then through the RemoteResourceServiceLocatorImpl)
		if (serviceInfo.getServiceDefinition().getQueue()) {
		    break;
		}
	    }
	}
	return null;
    }

    protected void saveMessage(PersistedMessage message) {
	if (new Boolean(Core.getCurrentContextConfig().getProperty(RiceConstants.MESSAGE_PERSISTENCE))) {
	    if (LOG.isDebugEnabled()) {
		LOG.debug("Persisting Message " + message);
	    }
	    message.setQueueStatus(RiceConstants.ROUTE_QUEUE_ROUTING);
	    KSBServiceLocator.getRouteQueueService().save(message);
	}
    }

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

    /**
         * Returns the List<RemotedServiceHolder> of asynchronous services which will be invoked by calls to this proxy.
         * This is a List because, in the case of Topics, there can be more than one service invoked.
         */
    public Object getTarget() {
	return this.serviceDefs;
    }

    public AsynchronousCallback getCallback() {
	return this.callback;
    }

    public void setCallback(AsynchronousCallback callback) {
	this.callback = callback;
    }

    public List<RemotedServiceHolder> getServiceDefs() {
	return this.serviceDefs;
    }

    public void setServiceDefs(List<RemotedServiceHolder> serviceDefs) {
	this.serviceDefs = serviceDefs;
    }
}