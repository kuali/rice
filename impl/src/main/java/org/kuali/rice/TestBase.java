/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.kuali.rice.test.KNSTestCase;
import org.kuali.rice.test.lifecycles.TransactionalLifecycle;

@Ignore
public class TestBase extends KNSTestCase {

	private TransactionalLifecycle transactionalLifecycle;

    @Before
    public void setUp() throws Exception {
        /*
         * This overridden method is used to set up default values already contained in KNSTestCase but
         * createproject.groovy script requires them here for replacement
         */
        setRelativeWebappRoot("/../kns/src/test/webapp");
        super.setUp();
        transactionalLifecycle = new TransactionalLifecycle();
        transactionalLifecycle.setTransactionManager(KNSServiceLocator.getTransactionManager());
        transactionalLifecycle.start();
    }

	@After
    public void tearDown() throws Exception {
        try {
		    if ( (transactionalLifecycle != null) && (transactionalLifecycle.isStarted()) ) {
                transactionalLifecycle.stop();
            }
        } finally {
            super.tearDown();
        }
    }

    @Override
    protected String getModuleName() {
        /*
         * This method is duplicate from KNSTestCase but exists for createproject.groovy script
         */
        return "kns";
    }

}
