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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.BaseLifecycle;
import org.kuali.rice.ojb.OjbPlatformConfigurer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import edu.iu.uis.eden.exception.WorkflowRuntimeException;

public class DatabaseLifeCycle extends BaseLifecycle implements ApplicationListener {

	protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DatabaseLifeCycle.class);

	private String ojbPropertyFile;

	public DatabaseLifeCycle() {
		String ojbFileLocation = Core.getCurrentContextConfig().getAlternateOJBFile();
		if (StringUtils.isBlank(ojbFileLocation)) {
			ojbFileLocation = "OJB.properties";
		}
		this.ojbPropertyFile = ojbFileLocation;
	}

	public DatabaseLifeCycle(String ojbPropertyFile) {
		this.ojbPropertyFile = ojbPropertyFile;
	}

	public void start() throws Exception {
		LOG.info("Configuring database...");

		if (StringUtils.isBlank(ojbPropertyFile)) {
			System.setProperty("OJB.properties", "OJB.properties");
		} else {
			System.setProperty("OJB.properties", ojbPropertyFile);
		}

		String platform = Core.getCurrentContextConfig().getProperty(Config.OJB_PLATFORM);
		if (platform == null) {
			throw new WorkflowRuntimeException("No platform was configured, please configure the datasource.ojb.platform property.");
		}

		LOG.info("Setting OJB platform to: " + platform);
		OjbPlatformConfigurer.configureDefaultOJBConnectionDescriptor(platform);

		// important to fetch this directory from the SpringLoader because we
		// don't want it wrapped in a proxy so that instanceof Current works
		// properly
//        TransactionManager transactionManager = (TransactionManager)SpringLoader.getInstance().getService(new QName(KEWServiceLocator.JTA_TRANSACTION_MANAGER));
//		if (transactionManager instanceof Current) {
//			Current jotm = (Current) transactionManager;
//			if (!Utilities.isEmpty(Core.getRootConfig().getTransactionTimeout())) {
//				jotm.setDefaultTimeout(Integer.parseInt(Core.getRootConfig().getTransactionTimeout()));
//				jotm.setTransactionTimeout(jotm.getDefaultTimeout());
//			} else {
//				jotm.setDefaultTimeout(EdenConstants.DEFAULT_TRANSACTION_TIMEOUT_SECONDS);
//				jotm.setTransactionTimeout(jotm.getDefaultTimeout());
//			}
//		}
//
//		Platform p = KEWServiceLocator.getDbPlatform();
//		LOG.info("Workflow database Platform from Spring: " + p);

		LOG.info("Trying to clear OJB cache from start()");
		super.start();
	}

	/**
	 * Initialize this LifeCycle when spring starts.
	 */
	public void onApplicationEvent(ApplicationEvent event) {
		try {
			if (event instanceof ContextRefreshedEvent) {
				if (isStarted()) {
					stop();
				}
				start();
			} else if (event instanceof ContextClosedEvent) {
				stop();
			}
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw (RuntimeException)e;
			}
			throw new WorkflowRuntimeException("Failed to handle application context event: " + event, e);
		}
	}

}