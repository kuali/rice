/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.service;

import org.junit.Test;
import org.kuali.rice.krad.data.jpa.testbo.TestDataObject;
import org.kuali.rice.krad.test.document.bo.Account;
import org.kuali.rice.test.BaselineTestCase;
import org.kuali.rice.test.data.PerTestUnitTestData;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.UnitTestFile;
import org.kuali.rice.test.data.UnitTestSql;
import org.kuali.rice.krad.test.KRADTestCase;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * LookupServiceTest tests KULRICE-984: Lookups - Relative Limit Gap
 *
 * <p>Making sure that lookup resultSetLimits set in the DD for
 * a BO will override the system wide default.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

@PerTestUnitTestData(
        value = @UnitTestData(
                order = {UnitTestData.Type.SQL_STATEMENTS, UnitTestData.Type.SQL_FILES},
                sqlStatements = {
                        @UnitTestSql("delete from trv_acct where acct_fo_id = '1'")
                        ,@UnitTestSql("delete from trv_acct_type")
                        ,@UnitTestSql("delete from krtst_test_table_t")
                },
                sqlFiles = {
                        @UnitTestFile(filename = "classpath:testAccountType.sql", delimiter = ";")
                        ,@UnitTestFile(filename = "classpath:testAccounts.sql", delimiter = ";")
                        ,@UnitTestFile(filename = "classpath:testDataObjects.sql", delimiter = ";")
                }
        ),
        tearDown = @UnitTestData(
                sqlStatements = {
                        @UnitTestSql("delete from trv_acct where acct_fo_id = '1'")
                        ,@UnitTestSql("delete from krtst_test_table_t")
                }
       )
)
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class LookupServiceTest extends KRADTestCase {

    // Methods which can be overridden by subclasses to reuse test cases. Here we are just testing the LookupService
    protected <T> Collection<T> findCollectionBySearchHelper(Class<T> clazz, Map<String, String> formProps, boolean unbounded) {
        return KRADServiceLocatorWeb.getLookupService().findCollectionBySearchHelper(clazz, formProps, unbounded);
    }

    protected <T> Collection<T> findCollectionBySearch(Class<T> clazz, Map<String, String> formProps) {
        return KRADServiceLocatorWeb.getLookupService().findCollectionBySearch(clazz, formProps);
    }

    protected <T> Collection<T> findCollectionBySearchUnbounded(Class<T> clazz, Map<String, String> formProps) {
        return KRADServiceLocatorWeb.getLookupService().findCollectionBySearchUnbounded(clazz, formProps);
    }

    /**
     * tests lookup return limits
     *
     * @throws Exception
     */
     @Test
     public void testLookupReturnLimits_Account() throws Exception {
        Map formProps = new HashMap();
        Collection travelAccounts = findCollectionBySearchHelper(Account.class, formProps, false);
        assertEquals(200, travelAccounts.size());

        travelAccounts = findCollectionBySearch(Account.class, formProps);
        assertEquals(200, travelAccounts.size());
     }

    /**
     * tests lookup return limits
     *
     * @throws Exception
     */
    @Test
    public void testLookupReturnLimits_TestDataObject() throws Exception {
        Map formProps = new HashMap();
        Collection testDataObjects = findCollectionBySearchHelper(TestDataObject.class, formProps, false);
        assertEquals(200, testDataObjects.size());

        testDataObjects = findCollectionBySearch(TestDataObject.class, formProps);
        assertEquals(200, testDataObjects.size());
    }

    /**
     * tests a lookup with the default limit
     *
     * @throws Exception
     */
    @Test
    public void testLookupReturnDefaultLimit() throws Exception {
        Map formProps = new HashMap();
        Collection travelAccounts = findCollectionBySearchHelper(Account.class, formProps, false);
        assertEquals(200, travelAccounts.size());

        travelAccounts = findCollectionBySearch(Account.class, formProps);
        assertEquals(200, travelAccounts.size());
    }

    /**
     * tests an unbounded lookup
     *
     * @throws Exception
     */
    @Test
    public void testLookupReturnDefaultUnbounded_Account() throws Exception {
        Map formProps = new HashMap();
        Collection travelAccounts = findCollectionBySearchHelper(Account.class, formProps, true);
        int size = travelAccounts.size();
        assertTrue("# of Travel Accounts should be > 200", size > 200);

        travelAccounts = findCollectionBySearchUnbounded(Account.class, formProps);
        size = travelAccounts.size();
        assertTrue("# of Travel Accounts should be > 200", size > 200);
    }

    /**
     * tests an unbounded lookup
     *
     * @throws Exception
     */
    @Test
    public void testLookupReturnDefaultUnbounded_TestDataObject() throws Exception {
        Map formProps = new HashMap();
        Collection testDataObjects = findCollectionBySearchHelper(TestDataObject.class, formProps, true);
        int size = testDataObjects.size();
        assertTrue("# of Test Data objects should be > 200", size > 200);

        testDataObjects = findCollectionBySearchUnbounded(TestDataObject.class, formProps);
        size = testDataObjects.size();
        assertTrue("# of Test Data objects should be > 200", size > 200);
    }

}
