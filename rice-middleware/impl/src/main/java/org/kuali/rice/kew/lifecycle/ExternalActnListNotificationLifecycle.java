/*
 * Copyright 2006-2015 The Kuali Foundation
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
package org.kuali.rice.kew.lifecycle;

import org.kuali.rice.core.api.lifecycle.BaseLifecycle;
import org.kuali.rice.kew.batch.ExternalActnListNotificationService;
import org.kuali.rice.kew.service.KEWServiceLocator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * This lifecycle will only be started if the rice.kew.externalActnListNotification.enabled parameter is set to true.
 * If it is set up to run, it will notify an external action list if anything changes on the KREW_ACTN_ITM_T table.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ExternalActnListNotificationLifecycle extends BaseLifecycle {

	protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExternalActnListNotificationLifecycle.class);

	private ScheduledExecutorService scheduledExecutor;
	private ScheduledFuture future;

	public void start() throws Exception {
		scheduledExecutor = Executors.newScheduledThreadPool(1);
		final ExternalActnListNotificationService alcPoller = KEWServiceLocator.getExternalActnListNotificationService();
		LOG.info("Starting the external action list notification service.  Polling at " +
				alcPoller.getExternalActnListNotificationPollIntervalSeconds() + " second intervals!");

		future = scheduledExecutor.scheduleWithFixedDelay(alcPoller,
				alcPoller.getExternalActnListNotificationInitialDelaySeconds(),
				alcPoller.getExternalActnListNotificationPollIntervalSeconds(), TimeUnit.SECONDS);

		super.start();
	}

	public void stop() throws Exception {
		if (isStarted()) {
			LOG.warn("Shutting down the external action list notification service");
			try {
				if (future != null) {
					if (!future.cancel(false)) {
						LOG.warn("Failed to cancel the external action list notification service.");
					}
					future = null;
				}
				if (scheduledExecutor != null) {
					scheduledExecutor.shutdownNow();
					scheduledExecutor = null;
				}
			} finally {
				super.stop();
			}
		}
	}

}
