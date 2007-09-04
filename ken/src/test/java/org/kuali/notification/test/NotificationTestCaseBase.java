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
package org.kuali.notification.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.notification.config.KENResourceLoaderFactory;
import org.kuali.notification.core.SpringNotificationServiceLocator;
import org.kuali.rice.lifecycle.BaseLifecycle;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.test.lifecycles.SQLDataLoaderLifecycle;
import org.kuali.workflow.test.KEWXmlDataLoaderLifecycle;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.SchedulerRepository;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

import edu.iu.uis.eden.KEWServiceLocator;

/**
 * Base test case for KEN that extends RiceTestCase
 * @author Aaron Hamid
 */
public abstract class NotificationTestCaseBase extends ModuleTestCase {
    private static final String KEN_MODULE_NAME = "ken";
    private static final String TX_MGR_BEAN_NAME = "transactionManager";
    
    protected final Logger LOG = Logger.getLogger(getClass());
    
    protected static SpringNotificationServiceLocator services;
    protected static PlatformTransactionManager transactionManager;

    public NotificationTestCaseBase() {
	super(KEN_MODULE_NAME);
    }

    @Override
    public void setUp() throws Exception {
	super.setUp();
	onSetUpBeforeTransaction();
    }
    
    // XXX: only exists for compatibility with existing unit tests
    protected void onSetUpBeforeTransaction() throws Exception {
	
    }

    @Override
    protected List<Lifecycle> getSuiteLifecycles() {
	List<Lifecycle> lifecycles = super.getSuiteLifecycles();
	lifecycles.add(new BaseLifecycle() {
	    @Override
	    public void start() throws Exception {
		// can't find a generic way to get the module's resource loader and context (would have to rely on standardized conventions)
		// in the super class ModuleTestCase, so just special case it here for KEN for now
		ConfigurableApplicationContext moduleContext = KENResourceLoaderFactory.getSpringResourceLoader().getContext();
		// This method sets up the Spring services so that they can be accessed by the tests.
	        services = new SpringNotificationServiceLocator(moduleContext);
	        // grab the module's transaction manager
	        transactionManager = (PlatformTransactionManager) moduleContext.getBean(TX_MGR_BEAN_NAME, PlatformTransactionManager.class);
		super.start();
	    }
	    
	});
	return lifecycles;
    }

    @Override
    protected List<Lifecycle> getPerTestLifecycles() {
	List<Lifecycle> lifecycles = super.getPerTestLifecycles();
	
	// clear out the KEW cache
	lifecycles.add(new BaseLifecycle() {
	    @Override
	    public void stop() throws Exception {
		KEWServiceLocator.getCacheAdministrator().flushAll();
		super.stop();
	    }
	});
	
	lifecycles.add(new KEWXmlDataLoaderLifecycle("classpath:testdata/BootstrapApplicationConstantsContent.xml"));
	lifecycles.add(new KEWXmlDataLoaderLifecycle("classpath:testdata/NotificationData.xml"));
	lifecycles.add(new KEWXmlDataLoaderLifecycle("classpath:testdata/BootstrapRuleTemplateContent.xml"));
	lifecycles.add(new KEWXmlDataLoaderLifecycle("classpath:testdata/BootstrapDocumentTypesContent.xml"));
	lifecycles.add(new KEWXmlDataLoaderLifecycle("classpath:testdata/BootstrapRuleContent.xml"));
	lifecycles.add(new KEWXmlDataLoaderLifecycle("classpath:testdata/widgets.xml"));
	lifecycles.add(new KEWXmlDataLoaderLifecycle("classpath:testdata/SendNotificationMessageRoutingConfiguration.xml"));
	
	lifecycles.add(new SQLDataLoaderLifecycle("classpath:testdata/common.sql", ";" ));
	lifecycles.add(new SQLDataLoaderLifecycle("classpath:testdata/load_test_tables.sql", ";"));
	
	return lifecycles;
    }

    /**
     * This method makes sure to disable the Quartz
     * @throws SchedulerException
     */
    protected static void disableQuartzJobs() throws SchedulerException {
	// do this so that our quartz jobs don't go off - we don't care about
        // these in our unit tests
        Collection<Scheduler> schedulers = SchedulerRepository.getInstance().lookupAll();
        
        Iterator<Scheduler> i = schedulers.iterator();
        while(i.hasNext()) {
            (i.next()).shutdown();
        }
    }

    /**
     * This method makes sure to disable the Quartz
     * @throws SchedulerException
     */
    protected static void enableQuartzJobs() throws SchedulerException {
        // do this so that our quartz jobs don't go off - we don't care about
        // these in our unit tests
        Collection<Scheduler> schedulers = SchedulerRepository.getInstance().lookupAll();
        
        Iterator<Scheduler> i = schedulers.iterator();
        while(i.hasNext()) {
            (i.next()).start();
        }
    }

    /**
     * @see org.kuali.rice.test.RiceTestCase#getConfigLocations()
     */
    @Override
    protected List<String> getConfigLocations() {
	return Arrays.asList(new String[] { "classpath:META-INF/" + getModuleName() + "-test-config.xml" });
    }

    /**
     * @see org.kuali.rice.test.RiceTestCase#getDerbySQLFileLocation()
     */
    @Override
    protected String getDerbySQLFileLocation() {
	//return "classpath:db/derby/" + getModuleName() + "-derby.sql";
	return null;
    }

    @Override
    protected String getModuleName() {
	return KEN_MODULE_NAME;
    }
}