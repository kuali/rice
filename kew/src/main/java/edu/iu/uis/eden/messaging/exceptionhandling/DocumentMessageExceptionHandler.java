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

import org.apache.commons.lang.StringUtils;
import org.kuali.bus.services.KSBServiceLocator;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.messaging.PersistedMessage;
import edu.iu.uis.eden.messaging.quartz.MessageServiceExecutorJob;
import edu.iu.uis.eden.messaging.quartz.MessageServiceExecutorJobListener;

/**
 * A {@link MessageExceptionHandler} which handles putting documents into exception routing.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentMessageExceptionHandler extends DefaultMessageExceptionHandler {

    @Override
    protected void placeInException(Throwable throwable, PersistedMessage message) {
	KEWServiceLocator.getExceptionRoutingService()
		.placeInExceptionRouting(throwable, message, getRouteHeaderId(message));
    }

    protected void scheduleExecution(Throwable throwable, PersistedMessage message) throws Exception {
	Scheduler scheduler = KSBServiceLocator.getScheduler();
	JobDataMap jobData = new JobDataMap();
	jobData.put(MessageServiceExecutorJob.MESSAGE_KEY, message);
	JobDetail jobDetail = new JobDetail("Exception_Message_Job " + Math.random(), "Exception Messaging",
		MessageServiceExecutorJob.class);
	jobDetail.setJobDataMap(jobData);
	jobDetail.setDescription("DocumentId: " + getRouteHeaderId(message));
	jobDetail.addJobListener(MessageServiceExecutorJobListener.NAME);
	Trigger trigger = new SimpleTrigger("Exception_Message_Trigger " + Math.random(), "Exception Messaging", message
		.getQueueDate());
	trigger.setJobDataMap(jobData);// 1.6 bug required or derby will choke
	scheduler.scheduleJob(jobDetail, trigger);
	KSBServiceLocator.getRouteQueueService().delete(message);
    }

    protected Long getRouteHeaderId(PersistedMessage message) {
	if (!StringUtils.isEmpty(message.getValue1()) && StringUtils.isNumeric(message.getValue1())) {
	    return Long.valueOf(message.getValue1());
	}
	throw new WorkflowRuntimeException("Unable to put this message in exception routing service name " + message.getServiceName());
    }
}