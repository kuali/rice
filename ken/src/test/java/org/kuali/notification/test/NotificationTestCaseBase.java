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

import java.util.ArrayList;
import java.util.List;

import org.kuali.notification.core.SpringNotificationServiceLocator;
import org.kuali.rice.config.SpringModuleConfigurer;
import org.kuali.rice.core.lifecycle.BaseLifecycle;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.resourceloader.SpringResourceLoader;
import org.kuali.rice.test.BaselineTestCase;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;
import org.kuali.rice.test.lifecycles.SQLDataLoaderLifecycle;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.batch.KEWXmlDataLoaderLifecycle;

/**
 * Base test case for KEN that extends RiceTestCase
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@BaselineMode(Mode.CLEAR_DB)
public abstract class NotificationTestCaseBase extends BaselineTestCase {
    private static final String KEN_MODULE_NAME = "ken";
    private static final String TX_MGR_BEAN_NAME = "transactionManager";

    protected SpringNotificationServiceLocator services;
    protected PlatformTransactionManager transactionManager;

    public NotificationTestCaseBase() {
        super(KEN_MODULE_NAME);
    }

    /*
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
    }*/

    /**
     * Avoid clearing the Quartz tables because it's deadlockey
     * @see org.kuali.rice.test.RiceTestCase#getTablesNotToClear()
     */
    /*@Override
    protected List<String> getTablesNotToClear() {
        List<String> l =  super.getTablesNotToClear();
        l.add("KR_.*");
        return l;
    }*/

    protected List<Lifecycle> getNotificationPerTestLifecycles() {
        List<Lifecycle> lifecycles = new ArrayList<Lifecycle>();
        lifecycles.add(new BaseLifecycle() {
            @Override
            public void start() throws Exception {
                // grab KEN's resource loader to pull out the context
                SpringResourceLoader kenResourceLoader = (SpringResourceLoader) GlobalResourceLoader.getResourceLoader(SpringModuleConfigurer.getDefaultResourceLoaderQName(KEN_MODULE_NAME)); 
                ConfigurableApplicationContext moduleContext = kenResourceLoader.getContext();
                // This method sets up the Spring services so that they can be accessed by the tests.
                services = new SpringNotificationServiceLocator(moduleContext);
                // grab the module's transaction manager
                transactionManager = (PlatformTransactionManager) moduleContext.getBean(TX_MGR_BEAN_NAME, PlatformTransactionManager.class);
                super.start();
            }

        });

        // clear out the KEW cache
        lifecycles.add(new BaseLifecycle() {
            @Override
            public void start() throws Exception {
                super.start();

                LOG.info("Status of Ken scheduler on start: " + (services.getScheduler().isStarted() ? "started" : "stopped"));
                // stop quartz if a test failed to do so
                disableQuartzJobs();
            }
            public void stop() throws Exception {
                KEWServiceLocator.getCacheAdministrator().flushAll();

                LOG.info("Status of Ken scheduler on stop: " + (services.getScheduler().isStarted() ? "started" : "stopped"));
                // stop quartz if a test failed to do so
                disableQuartzJobs();

                super.stop();
            }
        });

        // load the KEW bootstrap
        lifecycles.add(new KEWXmlDataLoaderLifecycle("file:" + getBaseDir() + "/../kew/src/main/config/xml/KEWBootstrap.xml"));
        lifecycles.add(new KEWXmlDataLoaderLifecycle("file:" + getBaseDir() + "/../kew/src/main/config/bootstrap/widgets.xml"));

        // load the KEN bootstrap
        lifecycles.add(new KEWXmlDataLoaderLifecycle("file:" + getBaseDir() + "/src/main/config/xml/KENBootstrap.xml"));
        lifecycles.add(new SQLDataLoaderLifecycle("file:" + getBaseDir() + "/src/main/config/sql/KENBootstrap.sql", "/" ));

        // load the KEN test data
        // some test data has to be loaded via SQL because we do not have XML loaders for it yet
        lifecycles.add(new KEWXmlDataLoaderLifecycle("classpath:org/kuali/ken/test/DefaultTestData.xml"));
        lifecycles.add(new SQLDataLoaderLifecycle("classpath:org/kuali/ken/test/DefaultTestData.sql", ";"));
        
        return lifecycles;

    }
    @Override
    protected List<Lifecycle> getPerTestLifecycles() {
        List<Lifecycle> lifecycles = super.getPerTestLifecycles();
        lifecycles.addAll(getNotificationPerTestLifecycles());
        return lifecycles;
    }

    /**
     * This method makes sure to disable the Quartz scheduler
     * @throws SchedulerException
     */
    protected void disableQuartzJobs() throws SchedulerException {
        // do this so that our quartz jobs don't go off - we don't care about
        // these in our unit tests
        Scheduler scheduler = services.getScheduler();
        scheduler.standby();
        //scheduler.shutdown();
    }

    /**
     * This method enables the Quartz scheduler
     * @throws SchedulerException
     */
    protected void enableQuartzJobs() throws SchedulerException {
        // do this so that our quartz jobs don't go off - we don't care about
        // these in our unit tests
        Scheduler scheduler = services.getScheduler();
        scheduler.start();
    }

}