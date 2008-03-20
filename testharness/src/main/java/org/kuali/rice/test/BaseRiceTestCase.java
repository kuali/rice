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
package org.kuali.rice.test;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.kuali.rice.test.lifecycles.PerTestDataLoaderLifecycle;
import org.kuali.rice.test.runners.RiceUnitTestClassRunner;

/**
 * A generic Rice Unit Test base class.
 * 
 * 1) Sets up a generic logger.
 * 2) Sets the name of the class being run to mimic jUnit 3 functionality.
 * 3) Stores the name of the method being run for use by subclasses (set by {@link RiceUnitTestClassRunner}
 * 4) Sets the PerTestDataLoaderLifecycle that will load sql for the currently running test.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * @since 0.9
 */
@RunWith(RiceUnitTestClassRunner.class)
public abstract class BaseRiceTestCase extends Assert implements MethodAware {

	protected final Logger log = Logger.getLogger(getClass());

	private String name;
	private PerTestDataLoaderLifecycle perTestDataLoaderLifecycle;
	protected Method method;

	public BaseRiceTestCase() {
		super();
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @see org.kuali.rice.test.MethodAware#setTestMethod(java.lang.reflect.Method)
	 */
	public void setTestMethod(Method testMethod) {
        this.method = testMethod;

        perTestDataLoaderLifecycle = new PerTestDataLoaderLifecycle(method);
    }

    protected PerTestDataLoaderLifecycle getPerTestDataLoaderLifecycle() {
		return this.perTestDataLoaderLifecycle;
	}
}