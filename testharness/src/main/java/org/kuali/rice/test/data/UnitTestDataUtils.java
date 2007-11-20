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
package org.kuali.rice.test.data;

import org.kuali.rice.test.SQLDataLoader;

/**
 * Utilities for unit test data annotations. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class UnitTestDataUtils {

    public static void executeDataLoader(UnitTestData data) throws Exception {
        SQLDataLoader sqlDataLoader;
        for (UnitTestData.Type type : data.order()) {
            switch (type) {
                case SQL_FILES : 
                    for (UnitTestFile file : data.sqlFiles()) {
                        sqlDataLoader = new SQLDataLoader(file.filename(), file.delimiter());
                        sqlDataLoader.runSql();
                    }
                    break;
                case SQL_STATEMENTS : 
                    for (UnitTestSql statement : data.sqlStatements()) {
                        sqlDataLoader = new SQLDataLoader(statement.value());
                        sqlDataLoader.runSql();
                    }
                    break;
                default: break;
            }
        }
    }
}
