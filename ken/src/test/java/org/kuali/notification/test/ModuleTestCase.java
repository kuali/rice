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
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.kuali.rice.config.Config;
import org.kuali.rice.core.Core;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.kuali.rice.resourceloader.SpringResourceLoader;
import org.kuali.rice.test.ClearDatabaseLifecycle;
import org.kuali.rice.test.DerbyDBCreationLifecycle;
import org.kuali.rice.test.RiceTestCase;
import org.kuali.rice.test.TestHarnessServiceLocator;

/**
 * Base test case for module that defines context and configuration locations
 * based on convention
 * configLocation: classpath:META-INF/(lowercase moduleName)-test-config.xml
 * context: classpath:(uppercase moduleName)TestHarnessSpringBeans.xml
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class ModuleTestCase extends RiceTestCase {
    protected final Logger log = Logger.getLogger(getClass());
    protected final String moduleName;
    protected final String contextLocation;

    protected ResourceLoader testHarnessResourceLoader;

    public ModuleTestCase(String moduleName) {
	this.moduleName = moduleName;
	this.contextLocation = "classpath:" + moduleName.toUpperCase() + "TestHarnessSpringBeans.xml";
    }

    /**
     * @see org.kuali.rice.test.RiceTestCase#getModuleName()
     */
    @Override
    protected String getModuleName() {
	return moduleName;
    }

    /**
     * @see org.kuali.rice.test.RiceTestCase#getConfigLocations()
     */
    @Override
    protected List<String> getConfigLocations() {
	return Arrays.asList(new String[] { "classpath:META-INF/" + getModuleName().toLowerCase() + "-test-config.xml" });
    }

    /**
     * @see org.kuali.rice.test.RiceTestCase#getDerbySQLFileLocation()
     */
    @Override
    protected String getDerbySQLFileLocation() {
	return null;
    }

    protected ResourceLoader getResourceLoader() {
	return testHarnessResourceLoader;
    }

    /**
     * HACK HACK HACK
     * overriding rice test case setup so that we can switch the order of suite and per-test
     * lifecycles...suite should run first
     * @see org.kuali.rice.test.RiceTestCase#setUp()
     */
    @Before
    @Override
    public void setUp() throws Exception {
	try {
	    beforeRun();
	    configureLogging();
	    log.info("test, test, test");
	    final long initTime = System.currentTimeMillis();

	    if (!SUITE_LIFE_CYCLES_RAN) {
		this.suiteLifeCycles = getSuiteLifecycles();
		startLifecycles(this.suiteLifeCycles);
		SUITE_LIFE_CYCLES_RAN = true;
	    }

	    this.perTestLifeCycles = getPerTestLifecycles();
	    startLifecycles(this.perTestLifeCycles);

	    report("Time to start all Lifecycles: " + (System.currentTimeMillis() - initTime));
	} catch (Throwable e) {
	    e.printStackTrace();
	    throw new RuntimeException(e);
	}
    }

    protected List<Lifecycle> getDefaultSuiteLifecycles() {
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
	return lifeCycles;
    }

    protected List<Lifecycle> getDefaultPerTestLifecycles() {
	LinkedList<Lifecycle> lifeCycles = new LinkedList<Lifecycle>();
	lifeCycles.add(new ClearDatabaseLifecycle(getTablesToClear(), getTablesNotToClear()));
	return lifeCycles;
    }

    @Override
    protected List<Lifecycle> getPerTestLifecycles() {
	return getDefaultPerTestLifecycles();
    }

    @Override
    protected List<Lifecycle> getSuiteLifecycles() {
	// rice test case suite lifecycles are empty... use these defaults
	List<Lifecycle> lifeCycles = getDefaultSuiteLifecycles();
	this.testHarnessResourceLoader = new SpringResourceLoader(new QName(getModuleName() + "TestHarness"), contextLocation);
	lifeCycles.add(this.testHarnessResourceLoader);
	return lifeCycles;
    }
}