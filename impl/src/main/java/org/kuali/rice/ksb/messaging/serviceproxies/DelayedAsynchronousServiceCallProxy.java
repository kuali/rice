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
package org.kuali.rice.ksb.messaging.serviceproxies;

import org.apache.log4j.Logger;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.core.proxy.BaseInvocationHandler;
import org.kuali.rice.core.proxy.TargetedInvocationHandler;
import org.kuali.rice.core.resourceloader.ContextClassLoaderProxy;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.ksb.messaging.AsynchronousCall;
import org.kuali.rice.ksb.messaging.PersistedMessageBO;
import org.kuali.rice.ksb.messaging.RemotedServiceHolder;
import org.kuali.rice.ksb.messaging.ServiceInfo;
import org.kuali.rice.ksb.messaging.quartz.MessageServiceExecutorJob;
import org.kuali.rice.ksb.messaging.quartz.MessageServiceExecutorJobListener;
import org.kuali.rice.ksb.service.KSBServiceLocator;
import org.quartz.*;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;


/**
 * A proxy which schedules a service to be executed asynchronously after some delay period.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DelayedAsynchronousServiceCallProxy extends BaseInvocationHandler implements TargetedInvocationHandler {

    private static final Logger LOG = Logger.getLogger(DelayedAsynchronousServiceCallProxy.class);

    List<RemotedServiceHolder> serviceDefs;
    private Serializable context;
    private String value1;
    private String value2;
    private long delayMilliseconds;

    protected DelayedAsynchronousServiceCallProxy(List<RemotedServiceHolder> serviceDefs, Serializable context,
	    String value1, String value2, long delayMilliseconds) {
	this.serviceDefs = serviceDefs;
	this.context = context;
	this.value1 = value1;
	this.value2 = value2;
	this.delayMilliseconds = delayMilliseconds;
    }

    public static Object createInstance(List<RemotedServiceHolder> serviceDefs, Serializable context, String value1,
	    String value2, long delayMilliseconds) {
	if (serviceDefs == null || serviceDefs.isEmpty()) {
	    throw new RuntimeException("Cannot create service proxy, no service(s) passed in.");
	}
	try {
	    return Proxy.newProxyInstance(ClassLoaderUtils.getDefaultClassLoader(), ContextClassLoaderProxy
		    .getInterfacesToProxy(serviceDefs.get(0).getService()),
		    new DelayedAsynchronousServiceCallProxy(serviceDefs, context, value1, value2, delayMilliseconds));
	} catch (Exception e) {
	    throw new RiceRuntimeException(e);
	}
    }

    @Override
    protected Object invokeInternal(Object proxy, Method method, Object[] arguments) throws Throwable {
	// there are multiple service calls to make in the case of topics.
	AsynchronousCall methodCall = null;
	PersistedMessageBO message = null;
	synchronized (this) {
	    // consider moving all this topic invocation stuff to the service
	    // invoker for speed reasons
	    for (RemotedServiceHolder remotedServiceHolder : this.serviceDefs) {
		ServiceInfo serviceInfo = remotedServiceHolder.getServiceInfo();
		methodCall = new AsynchronousCall(method.getParameterTypes(), arguments, serviceInfo, method.getName(),
			null, this.context);
		message = KSBServiceLocator.getRouteQueueService().getMessage(serviceInfo, methodCall);
		message.setValue1(this.value1);
		message.setValue2(this.value2);
		Calendar now = Calendar.getInstance();
		now.add(Calendar.MILLISECOND, (int) delayMilliseconds);
		message.setQueueDate(new Timestamp(now.getTimeInMillis()));
		scheduleMessage(message);
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

    protected void scheduleMessage(PersistedMessageBO message) throws SchedulerException {
	LOG.debug("Scheduling execution of a delayed asynchronous message.");
	Scheduler scheduler = KSBServiceLocator.getScheduler();
	JobDataMap jobData = new JobDataMap();
	jobData.put(MessageServiceExecutorJob.MESSAGE_KEY, message);
	JobDetail jobDetail = new JobDetail("Delayed_Asynchronous_Call-" + Math.random(), "Delayed_Asynchronous_Call",
		MessageServiceExecutorJob.class);
	jobDetail.setJobDataMap(jobData);
	jobDetail.addJobListener(MessageServiceExecutorJobListener.NAME);
	Trigger trigger = new SimpleTrigger("Delayed_Asynchronous_Call_Trigger-" + Math.random(),
		"Delayed_Asynchronous_Call", message.getQueueDate());
	trigger.setJobDataMap(jobData);// 1.6 bug required or derby will choke
	scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
         * Returns the List<RemotedServiceHolder> of asynchronous services which will be invoked by calls to this proxy.
         * This is a List because, in the case of Topics, there can be more than one service invoked.
         */
    public Object getTarget() {
	return this.serviceDefs;
    }

    public List<RemotedServiceHolder> getServiceDefs() {
	return this.serviceDefs;
    }

    public void setServiceDefs(List<RemotedServiceHolder> serviceDefs) {
	this.serviceDefs = serviceDefs;
    }

}
