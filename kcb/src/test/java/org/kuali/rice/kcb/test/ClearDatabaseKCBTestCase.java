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

import org.kuali.rice.lifecycle.Lifecycle;
import org.kuali.rice.test.ClearDatabaseLifecycle;

/**
 * KCBTestCase that clears the database for each test 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class ClearDatabaseKCBTestCase extends KCBTestCase {
    /**
     * Override the suite lifecycles to exclude the cleardatabaselifecycle
     * which is added by default, because we're doing it on a per-test basis anyway
     * 
     * @see org.kuali.rice.test.RiceTestCase#getSuiteLifecycles()
     */
    @Override
    protected List<Lifecycle> getSuiteLifecycles() {
        return getInitialLifecycles();
    }

    @Override
    protected List<Lifecycle> getPerTestLifecycles() {
        List<Lifecycle> lifecycles = super.getPerTestLifecycles();
        lifecycles.add(0, new ClearDatabaseLifecycle(getTablesToClear(), getTablesNotToClear()));
        return lifecycles;
    }
}