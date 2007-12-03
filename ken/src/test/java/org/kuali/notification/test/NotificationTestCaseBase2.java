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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.kuali.notification.core.NotificationServiceLocator;
import org.kuali.notification.core.SpringNotificationServiceLocator;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.SchedulerRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.batch.DirectoryXmlDocCollection;
import edu.iu.uis.eden.batch.FileXmlDocCollection;
import edu.iu.uis.eden.batch.XmlDocCollection;

/**
 * Base test case that loads the unit test Spring context and wraps unit tests with a
 * transaction that automatically rolls back all changes.
 * TODO: move to AbstractSingleSpringContextTests when we migrate to Spring 2.x
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class NotificationTestCaseBase2 extends AbstractTransactionalSpringContextTests {
    /**
     * Name of the bean in the test Spring context used as the unit test data source
     */
    public static final String UNIT_TEST_DATASOURCE = "unitTestDataSource";

    protected final Logger LOG = Logger.getLogger(getClass());
    protected NotificationServiceLocator services;
    protected DataSource unitTestDataSource;
    protected PlatformTransactionManager transactionManager;

    /**
     * Sets up the services for use by the different methods - put here b/c we want to make 
     * sure that each method re-obtains the context in a non-cached state.  Also 
     * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpBeforeTransaction()
     */
    @Override
    protected void onSetUpBeforeTransaction() throws Exception {
        // always reload the context...because we will be blowing the test db data away
        //setDirty(getConfigLocations());
        super.onSetUpBeforeTransaction();

        initialize();
    }

    private void initialize() throws Exception {
        ApplicationContext context = getContext();

        // try hard to stop/shutdown bus
// commented for integration with rice KEWServiceLocator.getCacheAdministrator().flushAll();
// commented for integration with rice KEWServiceLocator.getServiceDeployer().stop();
// commented for integration with rice KEWServiceLocator.getBAMService().clearBAMTables();

        // clear tables...
        List<String> tablesToClear = new ArrayList<String>();
        tablesToClear.add("(?i)EN_.*");
        tablesToClear.add("(?i)notifications");
        tablesToClear.add("(?i)notification_.*");
        tablesToClear.add("(?i)recipient_preferences");
        tablesToClear.add("(?i)user_channel_subscriptions");
        tablesToClear.add("(?i)user_deliverer_config");

        // pull datasource and transaction manager out of context
        unitTestDataSource = (DataSource) context.getBean(UNIT_TEST_DATASOURCE, DataSource.class);
        String[] txMgrBeans = context.getBeanNamesForType(PlatformTransactionManager.class);
        if (txMgrBeans == null || txMgrBeans.length == 0) {
            throw new RuntimeException("Could not find PlatformTransactionManager in context: " + getContext().getDisplayName());
        }
        transactionManager = (PlatformTransactionManager) getContext().getBean(txMgrBeans[0], PlatformTransactionManager.class);
        // clear the db...
        ClearDatabaseUtil.clearTables(transactionManager, unitTestDataSource, unitTestDataSource.getConnection().getMetaData().getUserName().toUpperCase(), tablesToClear, true, "UNITTEST");

        // now load the db up...
        // have to use 'file:' prefix as these paths are run through Springs resource loaders
        loadSQL("file:support/db/sql/oracle/common.sql");
        loadSQL("file:support/db/sql/oracle/load_test_tables.sql");
        
        Thread.sleep(5000);

        // initialize our locator
        initializeServices();
        
        //disableQuartzJobs();

        // start bus back up...
// commented for integration with rice        KEWServiceLocator.getServiceDeployer().start();

        // finally load Kew bootstrap data
        loadKewBootstrapData();
    }
    
    /**
     * This method sets up the Spring services so that they can be accessed by the tests.
     */
    private void initializeServices() throws Exception {
        ApplicationContext context = getContext();
        services = new SpringNotificationServiceLocator(context);
    }


    /**
     * This method loads the KEW bootstrap data once and only once.
     * @throws Exception
     */
    private synchronized void loadKewBootstrapData() throws Exception {
        //if(!loadedBootstrapDataIntoKew) {
            // check for the quickstart user
        //    if(!services.getNotificationRecipientService().isUserRecipientValid("TestUser1")) {
                LOG.info("Ingesting bootstrap");

                loadKEWDataFilesFromDirectory("support/kew-bootstrap-files");

                //loadedBootstrapDataIntoKew = true;
                LOG.info("Successfully ingested bootstrap data into KEW.");
            //}
        //}
    }

    protected void loadKEWDataFile(String file) throws Exception {
        List<XmlDocCollection> data = new ArrayList<XmlDocCollection>(1);
        FileXmlDocCollection xmlfile = new FileXmlDocCollection(new File(file));
        data.add(xmlfile);
        KEWServiceLocator.getXmlIngesterService().ingest(data);        
    }
    
    protected void loadKEWDataFilesFromDirectory(String directory) throws Exception {
        List<XmlDocCollection> data = new ArrayList<XmlDocCollection>(1);
        DirectoryXmlDocCollection datadir = new DirectoryXmlDocCollection(new File(directory));
        data.add(datadir);
        KEWServiceLocator.getXmlIngesterService().ingest(data);        
    }

    protected void loadSQL(String sqlFile) throws Exception {
        SQLDataLoader sdl = new SQLDataLoader(sqlFile, ";", unitTestDataSource);
        sdl.runSql();
    }


    protected DataSource getUnitTestDataSource() {
        return unitTestDataSource;
    }

    /**
     * This method makes sure to disable the Quartz
     * @throws SchedulerException
     */
    protected void disableQuartzJobs() throws SchedulerException {
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
    protected void enableQuartzJobs() throws SchedulerException {
        // do this so that our quartz jobs don't go off - we don't care about
        // these in our unit tests
        Collection<Scheduler> schedulers = SchedulerRepository.getInstance().lookupAll();
        
        Iterator<Scheduler> i = schedulers.iterator();
        while(i.hasNext()) {
            (i.next()).start();
        }
    }

    /**
     * Spring context file to use for unit tests (loaded as resource from classloader)
     */
    private static final String TEST_CONTEXT_FILE = "test-spring.xml";

    /**
     * Specifies the TEST_CONTEXT_FILE config location should be used for unit test Spring context.
     * @see org.springframework.test.AbstractDependencyInjectionSpringContextTests#getConfigLocations()
     */
    protected String[] getConfigLocations() {
        return new String[] { TEST_CONTEXT_FILE };
    }

    /**
     * This method obtains the ConfigurableApplicationContext base on the config locations.
     * @return ConfigurableApplicationContext
     */
    protected ApplicationContext getContext() throws Exception {
        return getContext(getConfigLocations());
    }
}