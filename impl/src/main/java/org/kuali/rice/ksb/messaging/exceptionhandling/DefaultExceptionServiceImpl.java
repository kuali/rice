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
package org.kuali.rice.ksb.messaging.exceptionhandling;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.ksb.messaging.AsynchronousCall;
import org.kuali.rice.ksb.messaging.PersistedMessage;
import org.kuali.rice.ksb.messaging.RemoteResourceServiceLocator;
import org.kuali.rice.ksb.messaging.quartz.MessageServiceExecutorJob;
import org.kuali.rice.ksb.messaging.quartz.MessageServiceExecutorJobListener;
import org.kuali.rice.ksb.messaging.resourceloading.KSBResourceLoaderFactory;
import org.kuali.rice.ksb.services.KSBServiceLocator;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;


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

	public void placeInExceptionRouting(Throwable throwable, PersistedMessage message, Object service) throws Exception {
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

	public void scheduleExecution(Throwable throwable, PersistedMessage message, String description) throws Exception {
		KSBServiceLocator.getRouteQueueService().delete(message);
		Scheduler scheduler = KSBServiceLocator.getScheduler();
		JobDataMap jobData = new JobDataMap();
		jobData.put(MessageServiceExecutorJob.MESSAGE_KEY, message);
		JobDetail jobDetail = new JobDetail("Exception_Message_Job " + Math.random(), "Exception Messaging",
			MessageServiceExecutorJob.class);
		jobDetail.setJobDataMap(jobData);
		if (!StringUtils.isBlank(description)) {
		    jobDetail.setDescription(description);
		}
		jobDetail.addJobListener(MessageServiceExecutorJobListener.NAME);
		Trigger trigger = new SimpleTrigger("Exception_Message_Trigger " + Math.random(), "Exception Messaging", message
			.getQueueDate());
		trigger.setJobDataMap(jobData);// 1.6 bug required or derby will choke
		scheduler.scheduleJob(jobDetail, trigger);    
	}
		
}