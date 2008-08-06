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
package org.kuali.rice.kim.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.Core;
import org.kuali.rice.core.config.Config;
import org.kuali.rice.core.lifecycle.BaseLifecycle;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.resourceloader.ResourceLoader;
import org.kuali.rice.test.RiceTestCase;
import org.kuali.rice.test.TestUtilities;
import org.kuali.rice.test.lifecycles.SQLDataLoaderLifecycle;
import org.kuali.rice.web.jetty.JettyServer;
import org.mortbay.jetty.webapp.WebAppClassLoader;

/**
 * This is test base that should be used for all KIM unit tests. All non-web unit tests for KIM should extend this base
 * class.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class KIMTestCase extends RiceTestCase {

	/**
     * This overridden method is responsible for loading up the kimtestharness from Spring.
     *
     * @see org.kuali.rice.test.RiceTestCase#getSuiteLifecycles()
     */
	@Override
	protected List<Lifecycle> getSuiteLifecycles() {
		List<Lifecycle> lifeCycles = super.getSuiteLifecycles();
		JettyServer server = new JettyServer(9972, "/kim-test", "/../kim/src/test/webapp");
		server.setFailOnContextFailure(true);
		server.setTestMode(true);
        lifeCycles.add(server);
        lifeCycles.add(new InitializeGRL());
        lifeCycles.add(new SQLDataLoaderLifecycle());
		return lifeCycles;
	}

	private class InitializeGRL extends BaseLifecycle {
        @Override
        public void start() throws Exception {
            TestUtilities.addWebappsToContext();
            super.start();
        }

    }

	/**
     * @see org.kuali.rice.test.RiceTestCase#getTablesNotToClear()
     */
	@Override
	protected List<String> getTablesNotToClear() {
		List<String> tables = new ArrayList<String>();
		tables.add("EN.*");
		tables.add("FS.*");
		tables.add("FP.*");
		tables.add("KR.*");
		return tables;
	}

	/**
     * At this time Derby for KIM is not supported.
     *
     * @see org.kuali.rice.test.RiceTestCase#getDerbySQLFileLocation()
     */
	@Override
	protected String getDerbySQLFileLocation() {
		return null;
	}

	/**
     * @see org.kuali.rice.test.RiceTestCase#getModuleName()
     */
	@Override
	protected String getModuleName() {
		return "kim";
	}

//	/**
//     * @return SpringResourceLoader
//     */
//	public SpringResourceLoader getSpringContextResourceLoader() {
//		return this.springContextResourceLoader;
//	}
//
//	/**
//     * @param springContextResourceLoader
//     */
//	public void setSpringContextResourceLoader(SpringResourceLoader springContextResourceLoader) {
//		this.springContextResourceLoader = springContextResourceLoader;
//	}
}
