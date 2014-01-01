/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.testtools.selenium;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.lang.reflect.Method;

/**
 * JUnit Test Runner to run test Automated Functional Tests.  Enables bookmark mode for test methods
 * ending in Bookmark and navigation mode for test methods ending in Nav. {@see AutomatedFunctionalTestBase}.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AutomatedFunctionalTestRunner extends BlockJUnit4ClassRunner {

    /**
     * AutomatedFunctionalTestRunner constructor.
     *
     * @param type
     * @throws InitializationError
     */
    public AutomatedFunctionalTestRunner(Class<?> type) throws InitializationError {
        super(type);
    }

    /**
     * Test methods ending with Bookmark will have {@see AutomatedFunctionalTestBase#enableBookmarkMode} called,
     * test methods ending with Nav will have {@see AutomatedFunctionalTestBase#enableNavigationMode} called.
     *
     * @param method test method to check for ending in Bookmark or Nav
     * @param test which extends AutomatedFunctionalTestBase
     * @return {@see BlockJUnit4ClassRunner#methodInvoker}
     */
    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        Method testMethod = method.getMethod();
        if (testMethod.getName().endsWith("Bookmark")) {
            ((AutomatedFunctionalTestBase) test).enableBookmarkMode();
        } else if (testMethod.getName().endsWith("Nav")) {
            ((AutomatedFunctionalTestBase) test).enableNavigationMode();
        }
        return super.methodInvoker(method, test);
    }
}