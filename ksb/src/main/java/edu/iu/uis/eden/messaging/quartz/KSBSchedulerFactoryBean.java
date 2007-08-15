/*
 * Copyright 2007 The Kuali Foundation
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
package edu.iu.uis.eden.messaging.quartz;

import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.RiceConstants;
import org.kuali.rice.config.ConfigurationException;
import org.kuali.rice.core.Core;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * An implementation of the Quartz SchedulerFactoryBean which uses a database-backed quartz if the useQuartzDatabase property
 * is set.
 *
 * @author Eric Westfall
 */
public class KSBSchedulerFactoryBean extends SchedulerFactoryBean {

    private PlatformTransactionManager jtaTransactionManager;
    
    @Override
	protected Scheduler createScheduler(SchedulerFactory schedulerFactory, String schedulerName) throws SchedulerException {
    	if (Core.getCurrentContextConfig().getObject(RiceConstants.INJECTED_EXCEPTION_MESSAGE_SCHEDULER_KEY) != null) {
    	    try {
    	    	Scheduler scheduler = (Scheduler) Core.getCurrentContextConfig().getObject(
    			RiceConstants.INJECTED_EXCEPTION_MESSAGE_SCHEDULER_KEY);
    	    	scheduler.addJobListener(new MessageServiceExecutorJobListener());
    	    	return scheduler;
    	    } catch (Exception e) {
    	    	throw new ConfigurationException(e);
    	    }
    	}
		return super.createScheduler(schedulerFactory, schedulerName);
	}

    @Override
    public void afterPropertiesSet() throws Exception {

	boolean useQuartzDatabase = new Boolean(Core.getCurrentContextConfig()
		.getProperty(RiceConstants.USE_QUARTZ_DATABASE));
	if (useQuartzDatabase) {
	    if (jtaTransactionManager == null) {
		throw new ConfigurationException("No jta transaction manager was configured for the KSB Quartz Scheduler");
	    }
	    setTransactionManager(jtaTransactionManager);
	    setDataSource(KSBServiceLocator.getMessageDataSource());
	}
	super.afterPropertiesSet();
    }

    /**
         * This is to work around an issue with the GRL when you've got more than one module with a bean named
         * "transactionManager".
     * 
     * @param jtaTransactionManager
     */
    public void setJtaTransactionManager(PlatformTransactionManager jtaTransactionManager) {
        this.jtaTransactionManager = jtaTransactionManager;
    }
}