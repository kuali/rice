/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.core.test;

import org.junit.After;
import org.junit.Test;
import org.kuali.rice.test.BaselineTestCase;
import org.kuali.rice.test.ClearDatabaseLifecycle;
import org.kuali.rice.test.data.PerSuiteUnitTestData;
import org.kuali.rice.test.data.PerTestUnitTestData;
import org.kuali.rice.test.data.UnitTestData;

/**
 * Used to test the annotation data entry provided by {@link UnitTestData}, {@link PerTestUnitTestData}, and {@link PerSuiteUnitTestData}
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@PerSuiteUnitTestData({
        @UnitTestData("insert into " + AnnotationTestParent.TEST_TABLE_NAME + " (COL) values ('3')"),
        @UnitTestData(filename = "classpath:org/kuali/rice/test/DataLoaderAnnotationTestData.sql")
})
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class DataLoaderAnnotationTest extends AnnotationTestParent {
    
    public DataLoaderAnnotationTest() {}

    @After
    public void clearDb() throws Exception {
        // cleanup database from @PerSuiteUnitTestData
        ClearDatabaseLifecycle clearDatabaseLifeCycle = new ClearDatabaseLifecycle();
        clearDatabaseLifeCycle.start();
    }

    @Test public void testParentAndSubClassImplementation() throws Exception {
        // check sql statement from this class
        verifyExistence("3");
        verifyCount("3", 1);
        
        // check sql file from this class
        verifyExistence("4");
        verifyCount("4", 1);
        
        // check sql statement from parent class
        verifyExistence("1");
        verifyCount("1", 1);
        
        // check sql file from parent class
        verifyExistence("2");
        verifyCount("2", 1);
    }
}
