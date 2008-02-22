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
package org.kuali.rice.kcb.test;

import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.rice.kcb.GlobalKCBServiceLocator;
import org.kuali.rice.kcb.KCBServiceLocator;
import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.test.RiceTestCase;
import org.kuali.rice.test.TransactionalLifecycle;

/**
 * Base KCBTestCase 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public abstract class KCBTestCase extends RiceTestCase {
    protected final Logger LOG = Logger.getLogger(getClass());
    protected KCBServiceLocator services;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        services = GlobalKCBServiceLocator.getInstance();
    }

    /**
     * @see org.kuali.rice.test.RiceTestCase#getModuleName()
     */
    @Override
    protected String getModuleName() {
        return "kcb";
    }

    /**
     * We enhance the default TestHarnessSpringBeans with a RiceConfigurer so Rice is set up
     * and good to go in the test harness.
     * @see org.kuali.rice.test.RiceTestCase#getTestHarnessSpringBeansLocation()
     */
    @Override
    protected String getTestHarnessSpringBeansLocation() {
        return "classpath:KCBTestHarnessSpringBeans.xml";
    }
   
    @Override
    protected List<Lifecycle> getPerTestLifecycles() {
        List<Lifecycle> lifecycles = super.getPerTestLifecycles();
        //lifecycles.add(0, new ClearDatabaseLifecycle(getTablesToClear(), getTablesNotToClear()));
        //lifecycles.add(0, new SQLDataLoaderLifecycle("classpath:KCBDefaultTestData.sql", "/"));
        lifecycles.add(0, new TransactionalLifecycle());
        return lifecycles;
    }
}
