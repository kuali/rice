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
package org.kuali.rice.test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.kuali.rice.config.Config;
import org.kuali.rice.config.SimpleConfig;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.resourceloader.SpringResourceLoader;

/**
 * Useful superclass for all Workflow test cases. Handles setup of test
 * utilities and a test environment. Configures the Spring test environment
 * providing a template method for custom context files in test mode. Also
 * provides a template method for running custom transactional setUp. Tear down
 * handles automatic tear down of objects created inside the test environment.
 * 
 * @author rkirkend
 * @since 0.9
 */
public abstract class RiceTestCase extends LoggableTestCase {

	private static final Logger LOG = Logger.getLogger(RiceTestCase.class);

	protected static boolean SUITE_LIFE_CYCLES_RAN = false;

	private List<Lifecycle> perTestLifeCycles = new LinkedList<Lifecycle>();

	private List<Lifecycle> suiteLifeCycles = new LinkedList<Lifecycle>();

	private List<String> reports = new ArrayList<String>();

	private SpringResourceLoader testHarnessSpringResourceLoader;

	@Before
	public void setUp() throws Exception {
		try {
			beforeRun();
			final long initTime = System.currentTimeMillis();
			this.perTestLifeCycles = getPerTestLifecycles();
			this.suiteLifeCycles = getSuiteLifecycles();

			startLifecycles(this.perTestLifeCycles);

			if (!SUITE_LIFE_CYCLES_RAN) {
				startLifecycles(this.suiteLifeCycles);
				SUITE_LIFE_CYCLES_RAN = true;
			}
			report("Time to start all Lifecycles: " + (System.currentTimeMillis() - initTime));
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * maven will set this property and find resources from the config based on
	 * it. This makes eclipse testing work because we have to put the basedir in
	 * our config files in order to find things when testing from maven
	 * 
	 */
	protected void setBaseDirSystemProperty(String moduleBaseDir) {
		if (System.getProperty("basedir") == null) {
			System.setProperty("basedir", System.getProperty("user.dir") + "/" + moduleBaseDir);
		}
	}

	protected void setModuleName(String moduleName) {
		if (System.getProperty("module.name") == null) {
			System.setProperty("module.name", moduleName);
		}
	}

	@After
	public void tearDown() throws Exception {
		stopLifecycles(this.perTestLifeCycles);
		afterRun();
	}

	protected void beforeRun() {
		System.out.println("##############################################################");
		System.out.println("# Starting test " + getClass().getSimpleName() + "...");
		System.out.println("# " + dumpMemory());
		System.out.println("##############################################################");
	}

	protected void afterRun() {
		System.out.println("##############################################################");
		System.out.println("# ...finished test " + getClass().getSimpleName());
		System.out.println("# " + dumpMemory());
		for (final String report : this.reports) {
			System.out.println("# " + report);
		}
		System.out.println("##############################################################\n\n\n");
	}

	protected void startLifecycles(List<Lifecycle> lifecycles) throws Exception {
		for (Lifecycle lifecycle : lifecycles) {
			lifecycle.start();
		}
	}

	protected void stopLifecycles(List<Lifecycle> lifecycles) throws Exception {
		final ListIterator<Lifecycle> iter = lifecycles.listIterator();
		while (iter.hasNext()) {
			iter.next();
		}
		while (iter.hasPrevious()) {
			final Lifecycle lifeCycle = iter.previous();
			try {
				lifeCycle.stop();
			} catch (Exception e) {
				LOG.warn("Failed to shutdown one of the lifecycles!", e);
			}
		}
	}

	/**
	 * 
	 * @return Lifecycles ran every test run
	 */
	protected List<Lifecycle> getPerTestLifecycles() {
		LinkedList<Lifecycle> lifeCycles = new LinkedList<Lifecycle>();
		lifeCycles.add(new Lifecycle() {
			boolean started = false;

			public boolean isStarted() {
				return this.started;
			}

			public void start() throws Exception {
				setModuleName(getModuleName());
				setBaseDirSystemProperty(getModuleName());
				Config config = getTestHarnessConfig();
				Core.init(config);
				this.started = true;
			}

			public void stop() throws Exception {
				this.started = false;
			}
		});
		lifeCycles.add(getTestHarnessSpringResourceLoader());
		lifeCycles.add(new Lifecycle() {
			boolean started = false;

			public boolean isStarted() {
				return this.started;
			}

			public void start() throws Exception {
				TestHarnessServiceLocator.setContext(getTestHarnessSpringResourceLoader().getContext());
				this.started = true;
			}

			public void stop() throws Exception {
				this.started = false;
			}
		});
		lifeCycles.add(new DerbyDBCreationLifecycle(getDerbySQLFileLocation()));
		lifeCycles.add(new ClearDatabaseLifecycle());
		return lifeCycles;
	}

	/**
	 * 
	 * @return Lifecycles run once during the suite
	 */
	protected List<Lifecycle> getSuiteLifecycles() {
		return new LinkedList<Lifecycle>();
	}

	protected void report(final String report) {
		this.reports.add(report);
	}

	protected String dumpMemory() {
		final long total = Runtime.getRuntime().totalMemory();
		final long free = Runtime.getRuntime().freeMemory();
		final long max = Runtime.getRuntime().maxMemory();
		return "[Memory] max: " + max + ", total: " + total + ", free: " + free;
	}

	public SpringResourceLoader getTestHarnessSpringResourceLoader() {
		if (testHarnessSpringResourceLoader == null) {
			testHarnessSpringResourceLoader = new SpringResourceLoader(new QName("TestHarnessSpringContext"), "classpath:TestHarnessSpringBeans.xml");
		}
		return testHarnessSpringResourceLoader;
	}

	protected Config getTestHarnessConfig() throws Exception {
		Config config = new SimpleConfig(getConfigLocations(), System.getProperties());
		config.parseConfig();
		return config;
	}

	/**
	 * @return List of config locations to add to this tests config location.
	 */
	protected abstract List<String> getConfigLocations();

	/**
	 * same as the module directory in the project.
	 * 
	 * @return name of module that the tests located
	 */
	protected abstract String getModuleName();

	/**
	 * Note: We may want to make this more automagical and base this off of
	 * convention from the module name Typical return value
	 * "classpath:db/derby/testharness.sql"
	 * 
	 * @return location of sql file containing ddl for a derby db to be ran
	 *         before start up.
	 */
	protected abstract String getDerbySQLFileLocation();

}