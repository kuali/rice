/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.test;

import org.junit.Test;
import org.kuali.rice.test.data.PerSuiteUnitTestData;
import org.kuali.rice.test.data.PerTestUnitTestData;
import org.kuali.rice.test.data.UnitTestData;

/**
 * This class is used to test the annotation data entry provided by {@link UnitTestData}, {@link PerTestUnitTestData}, and {@link PerSuiteUnitTestData}
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@PerSuiteUnitTestData({
        @UnitTestData("insert into " + AnnotationTestParent.TEST_TABLE_NAME + " (COL) values ('3')"),
        @UnitTestData(filename = "classpath:org/kuali/rice/test/DataLoaderAnnotationTestData.sql")
})
public class DataLoaderAnnotationTest extends AnnotationTestParent {
    
    public DataLoaderAnnotationTest() {}

    @Test public void testParentAndSubClassImplementation() throws Exception {
        // check sql statement from this class
        verifyExistence("3");
        
        // check sql file from this class
        verifyExistence("4");
        
        // check sql statement from parent class
        verifyExistence("1");
        
        // check sql file from parent class
        verifyExistence("2");
    }
    
}
