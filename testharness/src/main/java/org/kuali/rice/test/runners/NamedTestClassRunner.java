package org.kuali.rice.test.runners;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;

/**
 * A Runner which invokes setName() on the JUnit tests before running them. Used
 * for backward compatibility.
 * 
 * @author Eric Westfall
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:13 $
 * @since 0.9
 */
public class NamedTestClassRunner extends TestClassRunner {

    public NamedTestClassRunner(final Class< ? > testClass)
        throws InitializationError {
        super(testClass, new NamedTestClassMethodsRunner(testClass));
    }
}
