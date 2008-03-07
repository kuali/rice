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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sql.DataSource;

import org.junit.Test;
import org.kuali.rice.test.data.PerSuiteUnitTestData;
import org.kuali.rice.test.data.PerTestUnitTestData;
import org.kuali.rice.test.data.UnitTestData;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * This class is used to test the annotation data entry provided by {@link UnitTestData}, {@link PerTestUnitTestData}, and {@link PerSuiteUnitTestData}
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@PerSuiteUnitTestData({
        @UnitTestData("insert into " + AnnotationTestParent.TEST_TABLE_NAME + " (ID) values (2111)"),
        @UnitTestData(filename = "classpath:DataLoaderAnnotationTestData.sql", delimiter = ";")
})
public class DataLoaderAnnotationTest extends AnnotationTestParent {
    
    /**
     * This overridden method clears the unit test table of all inserted data
     * 
     * @see org.kuali.rice.test.RiceTestCase#tearDown()
     */
    @Override
    public void tearDown() throws Exception {
        new ClearDatabaseLifecycle(Arrays.asList(new String[]{TEST_TABLE_NAME}),new ArrayList<String>()).start();
        super.tearDown();
    }

    @Override
    protected String getModuleName() {
        return "testharness";
    }
    
    @Test public void testParentAndSubClassImplementation() throws Exception {
        // check sql statement from this class
        verifyExistence("2111");
        
        // check sql file from this class
        verifyExistence("2112");
        
        // check sql statement from parent class
        verifyExistence("1111");
        
        // check sql file from parent class
        verifyExistence("1112");
    }
    
    private void verifyExistence(String valueToVerify) throws SQLException {
        final ResultSet tableResults = getTableResults(valueToVerify);
        assertNotNull("Valid results should come back", tableResults);
        assertTrue("Value should be found for id " + valueToVerify, tableResults.next());
    }
    
    private ResultSet getTableResults(String valueToVerify) {
        final String valueToCheck = valueToVerify;
        final DataSource dataSource = TestHarnessServiceLocator.getDataSource();
        return (ResultSet) new JdbcTemplate(dataSource).execute(new ConnectionCallback() {
            public Object doInConnection(final Connection connection) throws SQLException {
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
                final ResultSet resultSet = statement.executeQuery("Select * from " + TEST_TABLE_NAME + " where ID = " + valueToCheck);
                return resultSet;
            }
        });
    }

    @Override
    protected String getDerbySQLFileLocation() {
        return "classpath:db/derby/testharness.sql";
    }
}
