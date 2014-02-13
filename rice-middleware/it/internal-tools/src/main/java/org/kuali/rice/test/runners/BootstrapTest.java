/*
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

package org.kuali.rice.test.runners;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines a test class which should be loaded and run at the start of a suite of tests.
 *
 * <p>This annotation should be applied to a class which uses the {@link LoadTimeWeavableTestRunner} which will load
 * and execute the specified bootstrap test.</p>
 *
 * <p>Usually, this is used for one-time initialization for the test suite. It also helps to get around issues of eager
 * classloading of JPA entities before their ClassFileTransformers can be registered for load-time weaving.</p>
 *
 * <p>The test class referenced must have at least one {@link org.junit.Test}</p> method even if it doesn't actually
 * do anything.</p>
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface BootstrapTest {

    Class<?> value();

}
