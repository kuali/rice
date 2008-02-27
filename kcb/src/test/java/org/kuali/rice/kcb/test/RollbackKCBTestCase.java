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
import org.kuali.rice.test.TransactionalLifecycle;

/**
 * KCBTestCase specialization that runs tests in a transaction and then rolls back 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public abstract class RollbackKCBTestCase extends KCBTestCase {
    @Override
    protected List<Lifecycle> getPerTestLifecycles() {
        List<Lifecycle> lifecycles = super.getDefaultPerTestLifecycles();
        // if some previous teset case did not roll back the data
        // clear the db
        if (dirty) {
            log.warn("Previous test case did not clean up the database; clearing database...");
            lifecycles.add(new ClearDatabaseLifecycle());
        }
        lifecycles.add(0, new TransactionalLifecycle() {
            @Override
            public void stop() throws Exception {
                super.stop();
                dirty = false;
            }
            
        });
        return lifecycles;
    }
}