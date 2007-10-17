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
package edu.iu.uis.eden.mail;

import org.apache.commons.lang.StringUtils;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.Lifecycle;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import edu.iu.uis.eden.EdenConstants;

/**
 * A {@link Lifecycle} which is initialized on system startup that sets up
 * the daily and weekly email reminders.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EmailReminderLifecycle implements Lifecycle {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EmailReminderLifecycle.class);

	private static final String DAILY_TRIGGER_NAME = "Daily Email Trigger";
	private static final String DAILY_JOB_NAME = "Daily Email";
	private static final String WEEKLY_TRIGGER_NAME = "Weekly Email Trigger";
	private static final String WEEKLY_JOB_NAME = "Weekly Email";

	private boolean started;

	public boolean isStarted() {
		return started;
	}

	public void start() throws Exception {
		String emailBatchGroup = "Email Batch";

		String dailyCron = Core.getCurrentContextConfig().getProperty(EdenConstants.DAILY_EMAIL_CRON_EXPRESSION);
		if (!StringUtils.isBlank(dailyCron)) {
		    LOG.info("Scheduling Daily Email batch with cron expression: " + dailyCron);
		    CronTrigger dailyTrigger = new CronTrigger(DAILY_TRIGGER_NAME, emailBatchGroup, dailyCron);
		    JobDetail dailyJobDetail = new JobDetail(DAILY_JOB_NAME, emailBatchGroup, DailyEmailJob.class);
		    dailyTrigger.setJobName(dailyJobDetail.getName());
		    dailyTrigger.setJobGroup(dailyJobDetail.getGroup());
		    addJobToScheduler(dailyJobDetail);
		    addTriggerToScheduler(dailyTrigger);
		} else {
		    LOG.warn("No " + EdenConstants.DAILY_EMAIL_CRON_EXPRESSION + " parameter was configured.  Daily Email batch was not scheduled!");
		}

		String weeklyCron = Core.getCurrentContextConfig().getProperty(EdenConstants.WEEKLY_EMAIL_CRON_EXPRESSION);
		if (!StringUtils.isBlank(dailyCron)) {
		    LOG.info("Scheduling Weekly Email batch with cron expression: " + weeklyCron);
		    CronTrigger weeklyTrigger = new CronTrigger(WEEKLY_TRIGGER_NAME, emailBatchGroup, weeklyCron);
		    JobDetail weeklyJobDetail = new JobDetail(WEEKLY_JOB_NAME, emailBatchGroup, WeeklyEmailJob.class);
		    weeklyTrigger.setJobName(weeklyJobDetail.getName());
		    weeklyTrigger.setJobGroup(weeklyJobDetail.getGroup());
		    addJobToScheduler(weeklyJobDetail);
		    addTriggerToScheduler(weeklyTrigger);
		} else {
		    LOG.warn("No " + EdenConstants.WEEKLY_EMAIL_CRON_EXPRESSION + " parameter was configured.  Weekly Email batch was not scheduled!");
		}

		started = true;
	}

	public void stop() throws Exception {
		started = false;
	}

	private void addJobToScheduler(JobDetail jobDetail) throws SchedulerException {
		getScheduler().addJob(jobDetail, true);
	}

	private void addTriggerToScheduler(Trigger trigger) throws SchedulerException {
		boolean triggerExists = (getScheduler().getTrigger(trigger.getName(), trigger.getGroup()) != null);
		if (!triggerExists) {
			try {
				getScheduler().scheduleJob(trigger);
			} catch (ObjectAlreadyExistsException ex) {
				getScheduler().rescheduleJob(trigger.getName(), trigger.getGroup(), trigger);
			}
		} else {
			getScheduler().rescheduleJob(trigger.getName(), trigger.getGroup(), trigger);
		}
	}

	private Scheduler getScheduler() {
		return KSBServiceLocator.getScheduler();
	}

}
