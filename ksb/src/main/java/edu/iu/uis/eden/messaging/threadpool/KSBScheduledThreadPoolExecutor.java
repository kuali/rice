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
package edu.iu.uis.eden.messaging.threadpool;
import org.apache.log4j.Logger;
import org.kuali.rice.RiceConstants;
import org.kuali.rice.core.Core;

import edu.emory.mathcs.backport.java.util.concurrent.ScheduledThreadPoolExecutor;

public class KSBScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor implements KSBScheduledPool {

	private static final Logger LOG = Logger.getLogger(KSBScheduledThreadPoolExecutor.class);

	private boolean started;
	private static final int DEFAULT_SIZE = 2;

	public KSBScheduledThreadPoolExecutor() {
		super(DEFAULT_SIZE);
	}

	public boolean isStarted() {
		return started;
	}

	public void start() throws Exception {
		LOG.info("starting " + KSBScheduledThreadPoolExecutor.class.getSimpleName());
		try {
			Integer size = new Integer(Core.getCurrentContextConfig().getProperty(RiceConstants.FIXED_POOL_SIZE));
			this.setCorePoolSize(size);
		} catch (NumberFormatException nfe) {

		}
	}

	public void stop() throws Exception {
		LOG.info("stopping " + KSBScheduledThreadPoolExecutor.class.getSimpleName());
		try {
			this.shutdownNow();
		} catch (Exception e) {
			LOG.warn("Exception thrown shutting down " + KSBScheduledThreadPoolExecutor.class.getSimpleName(), e);
		}

	}

}
