/*
 * Copyright 2007-2008 The Kuali Foundation
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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
@PerSuiteUnitTestData(
        overrideSuperClasses = true,
        value = {@UnitTestData("insert into " + AnnotationTestParent.TEST_TABLE_NAME + " (COL) values ('3')"),
        @UnitTestData(filename = "classpath:org/kuali/rice/test/DataLoaderAnnotationTestData.sql")
})
@DataLoaderAnnotationOverrideTest.Nothing
public class DataLoaderAnnotationOverrideTest extends AnnotationTestParent {
    // a dummy annotation to test that data loading annotations work in presence of
    // other annotations
    @Documented
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public static @interface Nothing {
    }
    
    @Test public void testParentAndSubClassImplementation() throws Exception {
        // verify that the sql only ran once...

        // check sql statement from this class
        verifyCount("3", 1);
        
        // check sql file from this class
        verifyCount("4", 1);
        
        // check sql statement from parent class
        verifyNonExistent("1");
        
        // check sql file from parent class
        verifyNonExistent("2");
    }
    
}
