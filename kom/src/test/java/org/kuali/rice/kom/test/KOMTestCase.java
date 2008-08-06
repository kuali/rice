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
package org.kuali.rice.kom.test;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.SpringResourceLoader;
import org.kuali.rice.test.RiceTestCase;

/**
 * This is test base that should be used for all KOM unit tests. All non-web unit tests for KOM should extend this base
 * class.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class KOMTestCase extends RiceTestCase {

	private static final String KOM_TEST_CONTEXT_LOC = "classpath:KomTestHarnessSpring.xml";
	private SpringResourceLoader springContextResourceLoader;

	/**
     * This overridden method just returns an empty array.
     * 
     * @see org.kuali.rice.test.RiceTestCase#getPerTestLifecycles()
     */
	@Override
	public List<Lifecycle> getPerTestLifecycles() {
		return new ArrayList<Lifecycle>();
	}

	/**
     * This overridden method is responsible for loading up the komtestharness from Spring.
     * 
     * @see org.kuali.rice.test.RiceTestCase#getSuiteLifecycles()
     */
	@Override
	protected List<Lifecycle> getSuiteLifecycles() {
		List<Lifecycle> lifeCycles = super.getSuiteLifecycles();
		this.springContextResourceLoader = new SpringResourceLoader(new QName("komtestharness"), KOM_TEST_CONTEXT_LOC);
		lifeCycles.add(this.springContextResourceLoader);
		return lifeCycles;
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
     * At this time Derby for KOM is not supported.
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
		return "kom";
	}

	/**
     * @return SpringResourceLoader
     */
	public SpringResourceLoader getSpringContextResourceLoader() {
		return this.springContextResourceLoader;
	}

	/**
     * @param springContextResourceLoader
     */
	public void setSpringContextResourceLoader(SpringResourceLoader springContextResourceLoader) {
		this.springContextResourceLoader = springContextResourceLoader;
	}
}