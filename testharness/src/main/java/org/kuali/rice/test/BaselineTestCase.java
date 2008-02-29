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
package org.kuali.rice.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import org.kuali.rice.lifecycle.Lifecycle;

/**
 * Test case which supports common styles of "baselining" the test environment before/after running
 * a unit test.
 * Currently supports three modes, which are specifyiable either via constructor, {@link #getMode()} override,
 * or by annotation:
 * <dl>
 *   <dt>NONE</dt>
 *   <dd>No baselining is performed.  Because the base RiceTestCase includes the ClearDatabaseLifecycle by default, this lifecycle is
 *       explicitly omitted</dd>
 *   <dt>CLEAR_DB</dt>
 *   <dd>The database is cleared for each test.  The suite ClearDatabaseLifecycle is omitted (since it's getting cleared each test
 *       anyway)</dd>
 *   <dt>ROLLBACK</dt>
 *   <dd>A TransactionalLifecycle is installed that wraps each test and rolls back data.  The suite ClearDatabaseLifecycle will be
 *       invoked once initially, and subsequently if the test has detected that the environment has been left "dirty" by a previous
 *       test.  After a successful rollback, the test environment is marked clean again.</dd>
 * </dl>
 * 
 * The BaselineMode annotation can be used on a per-test-class basis to indicate to the base class which mode to use for test
 * subclass.  It accepts a {@link Mode} value.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class BaselineTestCase extends BaseModuleTestCase {
    /**
     * Enum of "baselining" modes that this test case supports
     */
    public static enum Mode {
        CLEAR_DB, ROLLBACK, NONE
    }

    @Target({ElementType.TYPE})
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    public @interface BaselineMode {
        Mode value();
    }

    private Mode mode = Mode.NONE;

    // propagate constructors
    public BaselineTestCase(String moduleName, boolean overrideTestHarness) {
        super(moduleName, overrideTestHarness);
        readModeAnnotation();
    }

    // propagate constructors
    public BaselineTestCase(String moduleName) {
        super(moduleName);
        readModeAnnotation();
    }
    
    /**
     * Adds the ability to specify Mode
     */
    public BaselineTestCase(String moduleName, boolean overrideTestHarness, Mode mode) {
        super(moduleName, overrideTestHarness);
        if (mode == null) throw new IllegalArgumentException("Mode cannot be null");
        this.mode = mode;
    }

    /**
     * Adds the ability to specify Mode
     */
    public BaselineTestCase(String moduleName, Mode mode) {
        super(moduleName);
        if (mode == null) throw new IllegalArgumentException("Mode cannot be null");
        this.mode = mode;
    }

    private void readModeAnnotation() {
        BaselineMode m = this.getClass().getAnnotation(BaselineMode.class);
        if (m != null) {
            if (m.value() != null) {
                mode = m.value();
            }
        }
    }

    /**
     * @return the configured mode
     */
    protected Mode getMode() {
        return mode;
    }

    /**
     * Overridden to set dirty=true each time
     * @see org.kuali.rice.test.RiceTestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dirty = true;
    }
   

    @Override
    protected List<Lifecycle> getSuiteLifecycles() {
        switch (mode) {
            case ROLLBACK: return getRollbackSuiteLifecycles();
            case CLEAR_DB: return getClearDbSuiteLifecycles();
            case NONE: return getInitialLifecycles();
            default:
                throw new RuntimeException("Invalid mode specified: " + mode);        
        }
    }

    /**
     * Override the suite lifecycles to exclude the cleardatabaselifecycle
     * which is added by default, because we're doing it on a per-test basis anyway
     */
    protected List<Lifecycle> getClearDbSuiteLifecycles() {
        return getInitialLifecycles();
    }

    /**
     * Just returns the normal suite lifecycles since by default they clear the db (once)
     */
    protected List<Lifecycle> getRollbackSuiteLifecycles() {
        return super.getSuiteLifecycles();
    }

    @Override
    protected List<Lifecycle> getPerTestLifecycles() {
        switch (mode) {
            case ROLLBACK: return getRollbackPerTestLifecycles();
            case CLEAR_DB: return getClearDbPerTestLifecycles();
            case NONE: return super.getPerTestLifecycles();
            default:
                throw new RuntimeException("Invalid mode specified: " + mode);        
        }
    }

    /**
     * @return the per-test lifecycles for clearing the database
     */
    protected List<Lifecycle> getClearDbPerTestLifecycles() {
        List<Lifecycle> lifecycles = super.getPerTestLifecycles();
        lifecycles.add(0, new ClearDatabaseLifecycle(getTablesToClear(), getTablesNotToClear()));
        return lifecycles;
    }

    /**
     * @return the per-test lifecycles for rolling back the database
     */
    protected List<Lifecycle> getRollbackPerTestLifecycles() {
        List<Lifecycle> lifecycles = super.getDefaultPerTestLifecycles();
        lifecycles.add(0, new TransactionalLifecycle() {
            @Override
            public void stop() throws Exception {
                super.stop();
                dirty = false;
            }
            
        });
        // if some previous test case did not roll back the data
        // clear the db
        if (dirty) {
            log.warn("Previous test case did not clean up the database; clearing database...");
            lifecycles.add(0, new ClearDatabaseLifecycle());
        }
        return lifecycles;
    }
}