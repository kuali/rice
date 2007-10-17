/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import org.kuali.rice.core.Core;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;

/**
 * Quartz job for sending weekly email reminders.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WeeklyEmailJob implements Job {

	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		if (shouldExecute()) {
			KEWServiceLocator.getActionListEmailService().sendWeeklyReminder();
		}
	}

	protected boolean shouldExecute() {
		return Boolean.valueOf(Core.getCurrentContextConfig().getProperty(EdenConstants.WEEKLY_EMAIL_ACTIVE));
	}

}
