/*
 * Copyright 2005-2007 The Kuali Foundation
 *
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.kuali.rice.kew.test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.lifecycle.BaseLifecycle;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.web.jetty.JettyServer;
import org.kuali.rice.kew.batch.KEWXmlDataLoader;
import org.kuali.rice.kew.batch.KEWXmlDataLoaderLifecycle;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.MessageMap;
import org.kuali.rice.test.ClearDatabaseLifecycle;
import org.kuali.rice.test.RiceInternalSuiteDataTestCase;
import org.kuali.rice.test.SQLDataLoader;
import org.kuali.rice.test.lifecycles.JettyServerLifecycle;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Useful superclass for all KEW test cases. Handles setup of test utilities and
 * a test environment. Configures the Spring test environment providing a
 * template method for custom context files in test mode. Also provides a
 * template method for running custom transactional setUp. Tear down handles
 * automatic tear down of objects created inside the test environment.
 */
public abstract class KEWTestCase extends RiceInternalSuiteDataTestCase {

	/**
	 * This is the "bootstrap", aka Rice client, Spring beans file that the KEW
	 * test harness will load
	 */
	private static final String DEFAULT_KEW_BOOTSTRAP_SPRING_FILE = "classpath:org/kuali/rice/kew/config/TestKEWSpringBeans.xml";

	@Override
	protected String getModuleName() {
		return TestUtils.getModuleName();
	}

	/**
	 * Default implementation does nothing. Subclasses should override this
	 * method if they want to perform setup work inside of a database
	 * transaction.
	 */
	protected void setUpAfterDataLoad() throws Exception {
		// override
	}

	protected void loadTestData() throws Exception {
		// override this to load your own test data
	}

	protected TransactionTemplate getTransactionTemplate() {
		return TestUtilities.getTransactionTemplate();
	}

	/**
	 * Override the RiceTestCase setUpInternal in order to set a system property
	 * beforehand.
	 *
	 * @see org.kuali.rice.test.RiceTestCase#setUpInternal()
	 */
	@Override
	protected void setUpInternal() throws Exception {
		System.setProperty(KEWConstants.BOOTSTRAP_SPRING_FILE,
				getKEWBootstrapSpringFile());
		super.setUpInternal();
	}

	/**
	 * Initiates loading of per-test data
	 */
	@Override
	protected void loadPerTestData() throws Exception {
		final long t1 = System.currentTimeMillis();

		loadDefaultTestData();

		final long t2 = System.currentTimeMillis();
		report("Time to load default test data: " + (t2 - t1));

		loadTestData();

		final long t3 = System.currentTimeMillis();
		report("Time to load test-specific test data: " + (t3 - t2));

		setUpAfterDataLoad();

		final long t4 = System.currentTimeMillis();
		report("Time to run test-specific setup: " + (t4 - t3));
	}

	/**
	 * Returns the "bootstrap", aka Rice client, Spring beans file that the KEW
	 * test harness will load. KEW test cases can override this to provide an
	 * alternative bootstrap spring file. Currently only one file is supported,
	 * so one must override this file and then import the core file.
	 *
	 * @return the "bootstrap", aka Rice client, Spring beans file that the KEW
	 *         test harness will load
	 */
	protected String getKEWBootstrapSpringFile() {
		return DEFAULT_KEW_BOOTSTRAP_SPRING_FILE;
	}

	/**
	 * Override the standard per-test lifecycles to prepend
	 * ClearDatabaseLifecycle and ClearCacheLifecycle
	 *
	 * @see org.kuali.rice.test.RiceTestCase#getPerTestLifecycles()
	 */
	@Override
	protected List<Lifecycle> getPerTestLifecycles() {
		List<Lifecycle> lifecycles = new ArrayList<Lifecycle>();
		lifecycles.add(new ClearDatabaseLifecycle(getPerTestTablesToClear(),
				getPerTestTablesNotToClear()));
		lifecycles.add(new ClearCacheLifecycle());
		lifecycles.addAll(super.getPerTestLifecycles());
		return lifecycles;
	}

	/**
	 * Override the suite lifecycles to avoid the ClearDatabaseLifecycle (which
	 * we do on a per-test basis, as above) and to add on a JettyServer
	 * lifecycle
	 *
	 * @see org.kuali.rice.test.RiceTestCase#getSuiteLifecycles()
	 */
	@Override
	protected List<Lifecycle> getSuiteLifecycles() {
		
		List<Lifecycle> suiteLifecycles = super.getSuiteLifecycles();
		suiteLifecycles.add(new KEWXmlDataLoaderLifecycle("classpath:org/kuali/rice/kew/test/DefaultSuiteTestData.xml"));
		//lifeCycles.add( buildJettyServer(getJettyServerPort(), getJettyServerContextName(), getJettyServerRelativeWebappRoot()));
		//lifeCycles.add(new InitializeGRL());
		//lifeCycles.add(new BaseLifecycle() {
		//	public void start() throws Exception {
		//		KEWXmlDataLoader.loadXmlClassLoaderResource(getClass(), "DefaultSuiteTestData.xml");
		//		super.start();
		//	}
		//});
		return suiteLifecycles;
	}
	
	@Override
	protected Lifecycle getLoadApplicationLifecycle() {
		return new BaseLifecycle() {
			public void start() throws Exception {
				new JettyServerLifecycle(getJettyServerPort(), getJettyServerContextName(), getJettyServerRelativeWebappRoot()).start();
				new InitializeGRL().start();
				super.start();
			}
		};	
	}

	@Override
	protected void loadSuiteTestData() throws Exception {
		super.loadSuiteTestData();
		new SQLDataLoader(
				"classpath:org/kuali/rice/kew/test/DefaultSuiteTestData.sql", ";")
				.runSql();
	}

	protected JettyServer buildJettyServer(int port, String contextName, String relativeWebappRoot) {
		JettyServer server = new JettyServer(port, contextName, relativeWebappRoot);
		server.setFailOnContextFailure(true);
		server.setTestMode(true);
		return server;
	}

	/*
	 * Checks to see if a Jetty server is runningneeds to be randomly generated and then made available to subsequent
	 * tests
	 */
	protected int getJettyServerPort() {
		Config cfgCtx = null;
		try {
			cfgCtx = getTestHarnessConfig();
		} catch (Exception e) {
			log.error("Caught exception attempting to load test harness prior to aggregating suite lifecycles.");
		}
		return Integer.parseInt(cfgCtx.getProperty(KEWConstants.HTTP_SERVICE_PORT));
	}

	protected String getJettyServerContextName() {
		Config cfgCtx = null;
		try {
			cfgCtx = getTestHarnessConfig();
		} catch (Exception e) {
			log.error("Caught exception attempting to load test harness prior to aggregating suite lifecycles.");
		}
		return cfgCtx.getProperty(KEWConstants.KEW_SERVER_CONTEXT);		
	}

	protected String getJettyServerRelativeWebappRoot() {
		return "/../web/src/main/webapp";
	}

	/**
	 * Adds any ResourceLoaders that have been registered for WebAppClassLoaders
	 * to the GlobalResourceLoader
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
			KIMServiceLocator.getIdentityManagementService().flushAllCaches();
			KIMServiceLocator.getRoleManagementService().flushRoleCaches();
			super.stop();
		}

	}

	/**
	 * Returns the List of tables that should be cleared on every test run.
	 */
	protected List<String> getPerTestTablesToClear() {
		List<String> tablesToClear = new ArrayList<String>();
		tablesToClear.add("KREW_.*");
		tablesToClear.add("KRSB_.*");
		tablesToClear.add("KREN_.*");
		return tablesToClear;
	}

	protected List<String> getPerTestTablesNotToClear() {
		return new ArrayList<String>();
	}

	/**
	 * By default this loads the "default" data set from the DefaultTestData.sql
	 * and DefaultTestData.xml files. Subclasses can override this to change
	 * this behaviour
	 */
	protected void loadDefaultTestData() throws Exception {
		// at this point this is constants. loading these through xml import is
		// problematic because of cache notification
		// issues in certain low level constants.
		new SQLDataLoader(
				"classpath:org/kuali/rice/kew/test/DefaultPerTestData.sql", ";")
				.runSql();

		KEWXmlDataLoader.loadXmlClassLoaderResource(KEWTestCase.class,
				"DefaultPerTestData.xml");
		GlobalVariables.setMessageMap(new MessageMap());
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

	protected String getPrincipalIdForName(String principalName) {
		return KEWServiceLocator.getIdentityHelperService()
				.getIdForPrincipalName(principalName);
	}

	protected String getPrincipalNameForId(String principalId) {
		return KEWServiceLocator.getIdentityHelperService().getPrincipal(
				principalId).getPrincipalName();
	}

	protected String getGroupIdForName(String namespace, String groupName) {
		return KEWServiceLocator.getIdentityHelperService().getIdForGroupName(
				namespace, groupName);
	}
}
