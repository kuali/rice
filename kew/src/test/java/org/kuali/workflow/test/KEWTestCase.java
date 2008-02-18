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
package org.kuali.workflow.test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.BaseLifecycle;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.kuali.rice.test.ClearDatabaseLifecycle;
import org.kuali.rice.test.RiceTestCase;
import org.kuali.rice.web.jetty.JettyServer;
import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.batch.KEWXmlDataLoader;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.test.SQLDataLoader;
import edu.iu.uis.eden.test.TestUtilities;

/**
 * Useful superclass for all Workflow test cases. Handles setup of test utilities and a test environment. Configures the
 * Spring test environment providing a template method for custom context files in test mode. Also provides a template method
 * for running custom transactional setUp. Tear down handles automatic tear down of objects created inside the test
 * environment.
 */
public abstract class KEWTestCase extends RiceTestCase {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KEWTestCase.class);

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
	    System.setProperty(EdenConstants.BOOTSTRAP_SPRING_FILE, "org/kuali/workflow/resources/TestKewSpringBeans.xml");
	    super.setUpInternal();
	    loadTestDataInternal();
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
		JettyServer server = new JettyServer(9952, "/en-test", "/../kns/src/test/webapp/en");
		lifeCycles.add(server);
		lifeCycles.add(new InitializeGRL());
		return lifeCycles;
	}

	/**
	 * Adds any ResourceLoaders that have been registered for WebAppClassLoaders to the GlobalResourceLoader 
	 */
	private class InitializeGRL extends BaseLifecycle {
		@Override
		public void start() throws Exception {
			Map<ClassLoader, Config> configs = Core.getCONFIGS();
			for (Map.Entry<ClassLoader, Config> configEntry : configs.entrySet()) {
				if (configEntry.getKey() instanceof WebAppClassLoader) {
					ResourceLoader rl = GlobalResourceLoader.getResourceLoader(configEntry.getKey());
					if (rl == null) {
						fail("didn't find resource loader for workflow test harness web app");
					}
					GlobalResourceLoader.addResourceLoader(rl);
					configs.put(Thread.currentThread().getContextClassLoader(), configEntry.getValue());
				}
			}
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
		tablesToClear.add("EN_.*");
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