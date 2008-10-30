/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
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
package org.kuali.rice.kew.test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.kuali.rice.core.lifecycle.BaseLifecycle;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.web.jetty.JettyServer;
import org.kuali.rice.kew.batch.KEWXmlDataLoader;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.test.ClearDatabaseLifecycle;
import org.kuali.rice.test.RiceTestCase;
import org.kuali.rice.test.lifecycles.TransactionalLifecycle;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * Useful superclass for all Workflow test cases. Handles setup of test utilities and a test environment. Configures the
 * Spring test environment providing a template method for custom context files in test mode. Also provides a template method
 * for running custom transactional setUp. Tear down handles automatic tear down of objects created inside the test
 * environment.
 */
public abstract class KEWTestCase extends RiceTestCase {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KEWTestCase.class);

	private TransactionalLifecycle transactionalLifecycle;
	
	@Override
	protected String getModuleName() {
		return TestUtils.getModuleName();
	}

	/**
     * Initiates loading of test data within a transaction.
     */
	protected void loadTestDataInternal() throws Exception {
		getTransactionTemplate().execute(new TransactionCallbackWithoutResult() {
			public void doInTransactionWithoutResult(final TransactionStatus status) {
				try {
					setUpTransactionInternal();
				} catch (Exception e) {
					throw new WrappedTransactionRuntimeException(e);
				}
			}
		});
	}

	protected void setUpTransactionInternal() throws Exception {
		final long t1 = System.currentTimeMillis();

		loadDefaultTestData();

		final long t2 = System.currentTimeMillis();
		report("Time to load default test data: " + (t2 - t1));

		loadTestData();

		final long t3 = System.currentTimeMillis();
		report("Time to load test-specific test data: " + (t3 - t2));

		setUpTransaction();

		final long t4 = System.currentTimeMillis();
		report("Time to run test-specific setup: " + (t4 - t3));
	}

	/**
     * Default implementation does nothing. Subclasses should override this method if they want to perform setup work inside
     * of a database transaction.
     */
	protected void setUpTransaction() throws Exception {
		// override
	}

	/**
     * @Override
     */
	protected void loadTestData() throws Exception {
		// override this to load your own test data
	}

	protected TransactionTemplate getTransactionTemplate() {
		return TestUtilities.getTransactionTemplate();
	}

	private class WrappedTransactionRuntimeException extends RuntimeException {

		private static final long serialVersionUID = 3097909333572509600L;

		public WrappedTransactionRuntimeException(Exception e) {
			super(e);
		}
	}

	/**
	 * Override the RiceTestCase setUpInternal in order to set a system property beforehand,
	 * and load test data afterwards
	 * @see org.kuali.rice.test.RiceTestCase#setUpInternal()
	 */
	@Override
	public void setUpInternal() throws Exception {
	    System.setProperty(KEWConstants.BOOTSTRAP_SPRING_FILE, "classpath:org/kuali/rice/kew/config/TestKEWSpringBeans.xml");
	    super.setUpInternal();
	    loadTestDataInternal();
	    boolean needsTransaction = getClass().isAnnotationPresent(KEWTransactionalTest.class);
		if (needsTransaction) {
			transactionalLifecycle = new TransactionalLifecycle();
			transactionalLifecycle.setTransactionManager(KEWServiceLocator.getPlatformTransactionManager());
			transactionalLifecycle.start();
		}
	}
	
	@After
    public void tearDown() throws Exception {
	    if ( (transactionalLifecycle != null) && (transactionalLifecycle.isStarted()) ) {
			transactionalLifecycle.stop();
		}
        super.tearDown();
    }

	/**
	 * Override the standard per-test lifecycles to prepend ClearDatabaseLifecycle and ClearCacheLifecycle
	 * @see org.kuali.rice.test.RiceTestCase#getPerTestLifecycles()
	 */
	@Override
	protected List<Lifecycle> getPerTestLifecycles() {
		List<Lifecycle> lifecycles = new ArrayList<Lifecycle>();
		lifecycles.add(new ClearDatabaseLifecycle(getTablesToClear(), getTablesNotToClear()));
		lifecycles.add(new ClearCacheLifecycle());
		lifecycles.add(getPerTestDataLoaderLifecycle());
		return lifecycles;
	}

	/**
	 * Override the suite lifecycles to avoid the ClearDatabaseLifecycle (which we do on a per-test basis, as above)
	 * and to add on a JettyServer lifecycle
	 * @see org.kuali.rice.test.RiceTestCase#getSuiteLifecycles()
	 */
	@Override
	protected List<Lifecycle> getSuiteLifecycles() {
	    List<Lifecycle> lifeCycles = super.getInitialLifecycles();
		// we want to only clear out the quartz tables one time, therefore we want to pass this lifecycle the
		// opposite of what is passed to the clear database lifecycle that runs on every test execution
		JettyServer server = new JettyServer(getJettyServerPort(), "/en-test", "/../web/src/main/webapp/en");
		server.setFailOnContextFailure(true);
		server.setTestMode(true);
		lifeCycles.add(server);
		lifeCycles.add(new InitializeGRL());
		return lifeCycles;
	}
	
	protected int getJettyServerPort() {
//	    String port = ConfigContext.getCurrentContextConfig().getProperty(KEWConstants.HTTP_SERVICE_PORT);
//	    if (StringUtils.isNotBlank(port)) {
//	        return Integer.valueOf(port).intValue();
//	    }
	    return 9952;
	}

	/**
	 * Adds any ResourceLoaders that have been registered for WebAppClassLoaders to the GlobalResourceLoader 
	 */
	private class InitializeGRL extends BaseLifecycle {
		@Override
		public void start() throws Exception {
		    org.kuali.rice.test.TestUtilities.addWebappsToContext();
			super.start();
		}

	}

	/**
	 * Flushes the KEW cache(s)
	 */
	public class ClearCacheLifecycle extends BaseLifecycle {
		@Override
		public void stop() throws Exception {
			KEWServiceLocator.getCacheAdministrator().flushAll();
			super.stop();
		}

	}

	/**
     * Returns the List of tables that should be cleared on every test run.
     */
	@Override
	protected List<String> getTablesToClear() {
		List<String> tablesToClear = new ArrayList<String>();
		tablesToClear.add("KREW_.*");
		tablesToClear.add("KRSB_.*");
		tablesToClear.add("KRIM_.*");
		return tablesToClear;
	}

	/**
     * By default this loads the "default" data set from the DefaultTestData.sql and DefaultTestData.xml files. Subclasses
     * can override this to change this behaviour
     */
	protected void loadDefaultTestData() throws Exception {
		// at this point this is constants. loading these through xml import is
		// problematic because of cache notification
		// issues in certain low level constants.
		new SQLDataLoader("DefaultTestData.sql", KEWTestCase.class).runSql();
		
		KEWXmlDataLoader.loadXmlClassLoaderResource(KEWTestCase.class, "DefaultTestData.xml");
	}

	protected void loadXmlFile(String fileName) {
	    try {
	    	KEWXmlDataLoader.loadXmlClassLoaderResource(getClass(), fileName);
	    } catch (Exception e) {
	        throw new WorkflowRuntimeException(e);
	    }
	}

	protected void loadXmlFile(Class clazz, String fileName) {
	    try {
	        KEWXmlDataLoader.loadXmlClassLoaderResource(clazz, fileName);
	    } catch (Exception e) {
	        throw new WorkflowRuntimeException(e);
	    }
	}

	protected void loadXmlFileFromFileSystem(String fileName) {
	    try {
	        KEWXmlDataLoader.loadXmlFile(fileName);
	    } catch (Exception e) {
	        throw new WorkflowRuntimeException(e);
	    }
	}

	protected void loadXmlStream(InputStream xmlStream) {
	    try {
	        KEWXmlDataLoader.loadXmlStream(xmlStream);
	    } catch (Exception e) {
	        throw new WorkflowRuntimeException(e);
	    }
	}
}