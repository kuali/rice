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
package edu.iu.uis.eden.core.dependencylifecycles;

import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.BaseLifecycle;

import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledFuture;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.batch.XmlPollerService;

public class XmlPipelineLifeCycle extends BaseLifecycle {

	protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(XmlPipelineLifeCycle.class);

	private ScheduledExecutorService scheduledExecutor;
	private ScheduledFuture future;

	public void start() throws Exception {
		LOG.info("Configuring XML ingestion pipeline...");
		scheduledExecutor = Executors.newScheduledThreadPool(1);
		final XmlPollerService xmlPoller = KEWServiceLocator.getXmlPollerService();
		LOG.info("Starting XML data loader.  Polling at " + xmlPoller.getPollIntervalSecs() + "-second intervals");
		if (!Core.getCurrentContextConfig().getDevMode()) {
			future = scheduledExecutor.scheduleWithFixedDelay(xmlPoller, xmlPoller.getInitialDelaySecs(), xmlPoller.getPollIntervalSecs(), TimeUnit.SECONDS);
			super.start();
		}
	}

	public void stop() throws Exception {
		if (isStarted()) {
			LOG.warn("Shutting down XML file polling timer");
			try {
				if (future != null) {
					if (!future.cancel(false)) {
						LOG.warn("Failed to cancel the XML Poller service.");
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
