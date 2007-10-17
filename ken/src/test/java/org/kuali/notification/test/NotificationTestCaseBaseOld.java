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

import org.apache.log4j.Logger;
import org.kuali.notification.core.NotificationServiceLocator;
import org.kuali.notification.core.SpringNotificationServiceLocator;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.SchedulerRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.test.AbstractTransactionalSpringContextTests;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.batch.DirectoryXmlDocCollection;
import edu.iu.uis.eden.batch.XmlDocCollection;
import edu.iu.uis.eden.batch.XmlIngesterService;

/**
 * Base test case that loads the unit test Spring context and wraps unit tests with a
 * transaction that automatically rolls back all changes.
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class NotificationTestCaseBaseOld extends AbstractTransactionalSpringContextTests {
    protected final Logger LOG = Logger.getLogger(getClass());
    protected NotificationServiceLocator services;
    private static boolean loadedBootstrapDataIntoKew = false;
    
    /**
     * Sets up the services for use by the different methods - put here b/c we want to make 
     * sure that each method re-obtains the context in a non-cached state.  Also 
     * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpBeforeTransaction()
     */
    @Override
    protected void onSetUpBeforeTransaction() throws Exception {
	super.onSetUpBeforeTransaction();
	
	initializeServices();
        
        //disableQuartzJobs();
        
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
    private void loadKewBootstrapData() throws Exception {
	if(!loadedBootstrapDataIntoKew) {
            // check for the quickstart user
	    if(!services.getNotificationRecipientService().isUserRecipientValid("TestUser1")) {
		LOG.info("Ingesting bootstrap");
		XmlIngesterService s = KEWServiceLocator
			.getXmlIngesterService();
		List<XmlDocCollection> data = new ArrayList<XmlDocCollection>();
		File f = new File("support/kew-bootstrap-files");
		LOG.info(f);

		data.add(new DirectoryXmlDocCollection(f));
		s.ingest(data);
		loadedBootstrapDataIntoKew = true;
		LOG.info("Successfully ingested bootstrap data into KEW.");
	    }
	}
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
    @Override
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