/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.messaging.exceptionhandling;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.RiceConstants;
import org.kuali.rice.core.Core;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import edu.iu.uis.eden.messaging.PersistedMessage;
import edu.iu.uis.eden.messaging.ServiceInfo;
import edu.iu.uis.eden.messaging.quartz.MessageServiceExecutorJob;
import edu.iu.uis.eden.messaging.quartz.MessageServiceExecutorJobListener;

/**
 * Default implementation of the {@link MessageExceptionHandler} which handles exceptions thrown from message processing.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DefaultMessageExceptionHandler implements MessageExceptionHandler {

    private static final Logger LOG = Logger.getLogger(DefaultMessageExceptionHandler.class);

    private static final long DEFAULT_TIME_INCREMENT = 60 * 60 * 1000;

    private static final int DEFAULT_MAX_RETRIES = 7;

    public void handleException(Throwable throwable, PersistedMessage message, Object service) {
	LOG.error("Exception caught processing message " + message.getRouteQueueId(), throwable);
	try {
	    if (isInException(message)) {
		placeInException(throwable, message);
	    } else {
		requeue(throwable, message);
	    }
	} catch (Throwable t) {
	    LOG.error("Caught Exception trying to put message in exception routing!!!  Returning without notifying callbacks.", t);
	}
    }

    public boolean isInException(PersistedMessage message) {
	ServiceInfo serviceInfo = message.getMethodCall().getServiceInfo();

	if (getImmediateExceptionRouting()) {
	    return true;
	}

	Integer globalMaxRetryAttempts = getGlobalMaxRetryAttempts();
	if (globalMaxRetryAttempts != null) {
	    LOG.info("Global Max Retry has been set, so is overriding other max retry attempts.");
	    LOG.info("Global Max Retry count = " + globalMaxRetryAttempts + ".");
	    return (message.getRetryCount().intValue() >= globalMaxRetryAttempts.intValue());
	}

	if (serviceInfo.getServiceDefinition().getRetryAttempts() > 0) {
	    LOG.info("Message set for retry exception handling.  Message retry count = " + message.getRetryCount());
	    if (message.getRetryCount() >= serviceInfo.getServiceDefinition().getRetryAttempts()) {
		return true;
	    }
	} else if (serviceInfo.getServiceDefinition().getMillisToLive() > 0) {
	    LOG.info("Message set for time to live exception handling.  Message expiration date = "
		    + message.getExpirationDate().getTime());
	    if (System.currentTimeMillis() > message.getExpirationDate().getTime()) {
		return true;
	    }
	} else if (message.getRetryCount() >= this.getMaxRetryAttempts()) {
	    LOG.info("Message set for default exception handling.  Comparing retry count = " + message.getRetryCount()
		    + " against default max count.");
	    return true;
	}
	return false;
    }

    protected void requeue(Throwable throwable, PersistedMessage message) throws Exception {
	Integer retryCount = message.getRetryCount();
	message.setQueueStatus(RiceConstants.ROUTE_QUEUE_QUEUED);
	long addMilliseconds = Math.round(getTimeIncrement() * Math.pow(2, retryCount));
	Timestamp currentTime = message.getQueueDate();
	Timestamp newTime = new Timestamp(currentTime.getTime() + addMilliseconds);
	message.setQueueStatus(RiceConstants.ROUTE_QUEUE_QUEUED);
	message.setRetryCount(new Integer(retryCount + 1));
	message.setQueueDate(newTime);
	scheduleExecution(throwable, message);
    }

    protected void placeInException(Throwable throwable, PersistedMessage message) {
	message.setQueueStatus(RiceConstants.ROUTE_QUEUE_EXCEPTION);
	message.setQueueDate(new Timestamp(System.currentTimeMillis()));
	KSBServiceLocator.getExceptionRoutingService().placeInExceptionRouting(throwable, message);
    }

    protected void scheduleExecution(Throwable throwable, PersistedMessage message) throws Exception {
	KSBServiceLocator.getRouteQueueService().delete(message);
	Scheduler scheduler = KSBServiceLocator.getScheduler();
	JobDataMap jobData = new JobDataMap();
	jobData.put(MessageServiceExecutorJob.MESSAGE_KEY, message);
	JobDetail jobDetail = new JobDetail("Exception_Message_Job " + Math.random(), "Exception Messaging",
		MessageServiceExecutorJob.class);
	jobDetail.setJobDataMap(jobData);
	jobDetail.addJobListener(MessageServiceExecutorJobListener.NAME);
	Trigger trigger = new SimpleTrigger("Exception_Message_Trigger " + Math.random(), "Exception Messaging", message
		.getQueueDate());
	trigger.setJobDataMap(jobData);// 1.6 bug required or derby will choke
	scheduler.scheduleJob(jobDetail, trigger);
    }

    public Integer getMaxRetryAttempts() {
	try {
	    return new Integer(Core.getCurrentContextConfig().getProperty(RiceConstants.ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_KEY));
	} catch (NumberFormatException e) {
	    LOG.error("Constant '" + RiceConstants.ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_KEY + "' is not a number and is being "
		    + "used as a default for exception messages.  " + DEFAULT_MAX_RETRIES
		    + " will be used as a retry limit until this number is fixed");
	    return DEFAULT_MAX_RETRIES;
	}
    }

    protected Integer getGlobalMaxRetryAttempts() {
	String globalMax = Core.getCurrentContextConfig().getProperty(
		RiceConstants.ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_OVERRIDE_KEY);
	if (StringUtils.isBlank(globalMax)) {
	    return null;
	}
	try {
	    Integer globalMaxRetries = new Integer(globalMax);
	    if (globalMaxRetries >= 0) {
		return globalMaxRetries;
	    }
	} catch (NumberFormatException e) {
	    LOG.error("Constant '" + RiceConstants.ROUTE_QUEUE_MAX_RETRY_ATTEMPTS_OVERRIDE_KEY
		    + "' is not a number and is being " + "used as a default for exception messages.  "
		    + DEFAULT_MAX_RETRIES + " will be used as a retry limit until this number is fixed");
	}
	return null;
    }

    public Long getTimeIncrement() {
	try {
	    return new Long(Core.getCurrentContextConfig().getProperty(RiceConstants.ROUTE_QUEUE_TIME_INCREMENT_KEY));
	} catch (NumberFormatException e) {
	    LOG.error("Constant '" + RiceConstants.ROUTE_QUEUE_TIME_INCREMENT_KEY
		    + "' is not a number and will not be used "
		    + "as the default time increment for exception routing.  Default of " + DEFAULT_TIME_INCREMENT
		    + " will be used.");
	    return DEFAULT_TIME_INCREMENT;
	}
    }

    public Boolean getImmediateExceptionRouting() {
	return new Boolean(Core.getCurrentContextConfig().getProperty(RiceConstants.IMMEDIATE_EXCEPTION_ROUTING));
    }
}