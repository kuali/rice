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
package org.kuali.rice.test.runners;

import java.lang.reflect.Method;

import org.apache.commons.beanutils.MethodUtils;
import org.junit.internal.runners.TestClassMethodsRunner;
import org.junit.internal.runners.TestMethodRunner;
import org.junit.runner.notification.RunNotifier;

/**
 * A Runner which invokes setName() on the Test (if the method exists) and sets
 * it to the name of the test method being invoked.
 * 
 * @author Eric Westfall
 * @version $Revision: 1.3 $ $Date: 2007-08-15 15:49:52 $
 * @since 0.9
 */
public class NamedTestClassMethodsRunner extends TestClassMethodsRunner {

    public NamedTestClassMethodsRunner(final Class< ? > klass) {
        super(klass);
    }

    @Override
    protected TestMethodRunner createMethodRunner(final Object test,
        final Method method, final RunNotifier notifier) {
        final TestMethodRunner runner = super.createMethodRunner(test, method,
            notifier);
        setTestName(test, method.getName());
        return runner;
    }

    protected void setTestName(final Object test, final String name) {
        try {
            final Method setNameMethod = MethodUtils.getAccessibleMethod(test
                .getClass(), "setName", new Class[] {String.class});
            setNameMethod.invoke(test, new Object[] {name});
        } catch (final Exception e) {
            // no setName method, or we failed to invoke it, so we can't set the
            // name
        }
    }
}
