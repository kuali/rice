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

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;

/**
 * A Runner which invokes setName() on the JUnit tests before running them. Used
 * for backward compatibility.
 * 
 * @author Eric Westfall
 * @version $Revision: 1.3 $ $Date: 2007-08-15 15:49:52 $
 * @since 0.9
 */
public class NamedTestClassRunner extends TestClassRunner {

    public NamedTestClassRunner(final Class< ? > testClass)
        throws InitializationError {
        super(testClass, new NamedTestClassMethodsRunner(testClass));
    }
}
