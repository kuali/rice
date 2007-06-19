package org.kuali.rice.test;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.kuali.rice.test.runners.NamedTestClassRunner;

/**
 * TestCase subclass that merely introduces a protected 'log' member for
 * subclass access
 * 
 * @author Aaron Hamid (arh14 at cornell dot edu)
 * @version $Revision: 1.2 $ $Date: 2007-06-19 14:35:13 $
 * @since 0.9
 */
@RunWith(NamedTestClassRunner.class)
public abstract class LoggableTestCase extends Assert {

    protected final Logger log = Logger.getLogger(getClass());

    private String name;

    public LoggableTestCase() {
        super();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}