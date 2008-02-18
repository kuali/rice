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

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.test.ClearDatabaseLifecycle;
import org.kuali.rice.test.RiceTestCase;

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

    public ModuleTestCase(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * @see org.kuali.rice.test.RiceTestCase#getModuleName()
     */
    @Override
    protected String getModuleName() {
        return moduleName;
    }

    /**
     * Simply prepends a ClearDatabaseLifecycle for all unit tests
     * @see org.kuali.rice.test.RiceTestCase#getPerTestLifecycles()
     */
    @Override
    protected List<Lifecycle> getPerTestLifecycles() {
        LinkedList<Lifecycle> lifeCycles = new LinkedList<Lifecycle>();
        lifeCycles.add(0, new ClearDatabaseLifecycle(getTablesToClear(), getTablesNotToClear()));
        return lifeCycles;
    }

    /**
     * Overrides to allow (enforce) per-module (MODULE)TestHarnessSpringBeans.xml 
     * @see org.kuali.rice.test.RiceTestCase#getTestHarnessSpringBeansLocation()
     */
    @Override
    protected String getTestHarnessSpringBeansLocation() {
        return "classpath:" + moduleName.toUpperCase() + "TestHarnessSpringBeans.xml";
    }
}