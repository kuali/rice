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
package org.kuali.rice.krad.uif.util;

import java.util.concurrent.Callable;

import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * Base class for wrapping all JUnit tests in call to
 * {@link ProcessLogger#follow(String, String, Callable)}.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ProcessLoggingUnitTest {

    /**
     * Thread local, contains the current repetition index while running.
     */
    private static final ThreadLocal<Integer> REPETITION = new ThreadLocal<Integer>();

    protected static int getRepetition() {
        return REPETITION.get() == null ? 0 : REPETITION.get();
    }

    /**
     * Provides the number of times the test should be run.  Subclasses may provide an alternate value.
     * @return
     */
    protected int getRepetitions() {
        return 1;
    }

    @Rule
    public MethodRule processLogRule = new MethodRule() {
        @Override
        public Statement apply(final Statement base,
                final FrameworkMethod method, Object target) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    int repetitions = getRepetitions();
                    for (int i=0;i < repetitions;i++) {
                        REPETITION.set(i);
                        try {
                            ProcessLogger.follow("test", "Test Run " + method.getName(), new Callable<Void>() {

                                @Override
                                public Void call() throws Exception {
                                    try {
                                        base.evaluate();
                                    } catch (Throwable e) {
                                        if (e instanceof Error)
                                            throw (Error) e;
                                        else
                                            throw (Exception) e;
                                    }
                                    return null;
                                }

                                @Override
                                public String toString() {
                                    return "Test Run " + method.getName();
                                }
                            });
                        } catch (Throwable t) {
                            System.err.println("Error running test "
                                    + method.getName());
                            t.printStackTrace();
                            throw t;
                        } finally {
                            REPETITION.remove();
                        }
                    }
                }
            };
        }
    };

}
