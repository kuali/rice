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


import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.lifecycle.Lifecycle;
import org.quartz.Scheduler;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * A {@link Lifecycle} which is initialized on system startup that sets up
 * the daily and weekly email reminders.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EmailReminderLifecycle implements Lifecycle {

	private boolean started;

	public boolean isStarted() {
		return started;
	}

	public void start() throws Exception {
		// fetch scheduler here to initialize it ouside of a transactional context, otherwise we get weird transaction errors
	    Scheduler scheduler = KSBServiceLocator.getScheduler();
	    if (scheduler == null) {
		throw new WorkflowException("Failed to locate Quartz Scheduler Service.");
	    }
	    KEWServiceLocator.getActionListEmailService().scheduleBatchEmailReminders();
	    started = true;
	}

	public void stop() throws Exception {
		started = false;
	}

}
