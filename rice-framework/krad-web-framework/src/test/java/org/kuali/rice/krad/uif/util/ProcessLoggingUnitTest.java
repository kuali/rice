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

    @Rule
    public MethodRule processLogRule = new MethodRule() {
        @Override
        public Statement apply(final Statement base,
                final FrameworkMethod method, Object target) {
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
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
                    }
                }
            };
        }
    };

}
