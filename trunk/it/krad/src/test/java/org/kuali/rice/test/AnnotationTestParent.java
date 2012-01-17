/**
 * Copyright 2005-2011 The Kuali Foundation
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

import org.kuali.rice.test.data.PerSuiteUnitTestData;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.test.KRADTestCase;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

/**
 * This class is used by the {@link DataLoaderAnnotationTest} and {@link DataLoaderAnnotationOverrideTest} classes to verify parent class annotation usage
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@PerSuiteUnitTestData({
        @UnitTestData("insert into " + AnnotationTestParent.TEST_TABLE_NAME + " (COL) values ('1')"),
        @UnitTestData(filename = "classpath:org/kuali/rice/test/AnnotationTestParentData.sql")
})
public abstract class AnnotationTestParent extends KRADTestCase {

    protected static final String TEST_TABLE_NAME = "EN_UNITTEST_T";
    
    protected void verifyCount(String valueToVerify, int count) throws SQLException {
        assertEquals(count + " value(s) should be found for id " + valueToVerify, count, countTableResults(valueToVerify));
    }

    protected void verifyExistence(String valueToVerify) throws SQLException {
        assertTrue("Value should be found for id " + valueToVerify, hasTableResults(valueToVerify));
    }

    protected void verifyNonExistent(String valueToVerify) throws SQLException {
        assertFalse("No value should be found for id " + valueToVerify, hasTableResults(valueToVerify));
    }

    protected boolean hasTableResults(String valueToVerify) {
        return countTableResults(valueToVerify) > 0;
    }

    protected int countTableResults(String valueToVerify) {
        final String valueToCheck = valueToVerify;
        final DataSource dataSource = TestHarnessServiceLocator.getDataSource();
        return (Integer) new JdbcTemplate(dataSource).execute(new ConnectionCallback() {
            public Object doInConnection(final Connection connection) throws SQLException {
                Statement statement = null;
                try {
                    statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    final ResultSet resultSet = statement.executeQuery("Select * from " + TEST_TABLE_NAME + " where COL = '" + valueToCheck + "'");
                    assertNotNull("ResultSet should not be null",resultSet);
                    int count = 0;
                    while (resultSet.next()) {
                        count++;
                    }
                    return count;
                } finally {
                    if (statement != null) {
                        statement.close();
                    }
                }
            }
        });
    }

}
