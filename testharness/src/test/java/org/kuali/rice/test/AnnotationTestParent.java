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

import org.kuali.rice.test.data.PerSuiteUnitTestData;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.UnitTestFile;
import org.kuali.rice.test.data.UnitTestSql;

/**
 * This class is used by the {@link DataLoaderAnnotationTest} to verify parent class annotation usage 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@PerSuiteUnitTestData(
        @UnitTestData(
            sqlStatements = {
                @UnitTestSql("insert into " + AnnotationTestParent.TEST_TABLE_NAME + " (ID) values (1111)")
            },
            sqlFiles = {
                @UnitTestFile(filename = "classpath:AnnotationTestParentData.sql", delimiter = ";")
            }
        )
    )
public abstract class AnnotationTestParent extends RiceTestCase {
    
    protected static final String TEST_TABLE_NAME = "EN_UNITTEST_T";

}
