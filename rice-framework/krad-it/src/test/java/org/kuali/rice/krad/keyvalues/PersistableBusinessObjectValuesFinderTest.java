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
package org.kuali.rice.krad.keyvalues;

import org.junit.Test;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.PersistableBusinessObjectValuesFinder;
import org.kuali.rice.krad.test.document.bo.AccountType;
import org.kuali.rice.test.BaselineTestCase;
import org.kuali.rice.test.data.PerTestUnitTestData;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.UnitTestFile;
import org.kuali.rice.test.data.UnitTestSql;
import org.kuali.rice.krad.test.KRADTestCase;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * PersistableBusinessObjectValuesFinderTest tests the {@link PersistableBusinessObjectValuesFinder}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@PerTestUnitTestData(
        value = @UnitTestData(
                order = {UnitTestData.Type.SQL_STATEMENTS, UnitTestData.Type.SQL_FILES},
                sqlStatements = {
                        @UnitTestSql("delete from trv_acct_type")
                },
                sqlFiles = {
                        @UnitTestFile(filename = "classpath:testAccountType.sql", delimiter = ";")
                }
        ),
        tearDown = @UnitTestData(
                sqlStatements = {
                        @UnitTestSql("delete from trv_acct_type")
                }
        )
)
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
public class PersistableBusinessObjectValuesFinderTest extends KRADTestCase {

    private List<KeyValue> testKeyValues = new ArrayList<KeyValue>();
    private List<KeyValue> testKeyValuesKeyInLabel = new ArrayList<KeyValue>();

    /**
     * Default Constructor builds KeyValue Lists used for tests.
     *
     */
    public PersistableBusinessObjectValuesFinderTest() {
        testKeyValues.add(new ConcreteKeyValue("CAT", "Clearing Account Type"));
        testKeyValues.add(new ConcreteKeyValue("EAT", "Expense Account Type"));
        testKeyValues.add(new ConcreteKeyValue("IAT", "Income Account Type"));

        testKeyValuesKeyInLabel.add(new ConcreteKeyValue("CAT", "CAT - Clearing Account Type"));
        testKeyValuesKeyInLabel.add(new ConcreteKeyValue("EAT", "EAT - Expense Account Type"));
        testKeyValuesKeyInLabel.add(new ConcreteKeyValue("IAT", "IAT - Income Account Type"));
    }

    /**
     * tests to make sure the <code>PersistableBusinessObjectValuesFinder</code> works
     * as expected for the TravelAccountType BO
     *
     * @throws Exception
     */
    @Test public void testGetKeyValues() throws Exception {
        PersistableBusinessObjectValuesFinder valuesFinder = new PersistableBusinessObjectValuesFinder();
        valuesFinder.setBusinessObjectClass(AccountType.class);
        valuesFinder.setKeyAttributeName("accountTypeCode");
        valuesFinder.setLabelAttributeName("name");
        valuesFinder.setIncludeKeyInDescription(false);
        List<KeyValue> keyValues = valuesFinder.getKeyValues();
        assertEquals(testKeyValues.size(), keyValues.size());
        for (KeyValue testKeyValue: testKeyValues) {
            assertEquals(testKeyValue.getValue(), valuesFinder.getKeyLabel(testKeyValue.getKey()));
        }
    }

    /**
     * tests to make sure the <code>PersistableBusinessObjectValuesFinder</code> works
     * as expected for the TravelAccountType BO with the key included in the label
     *
     * @throws Exception
     */
    @Test public void testGetKeyValuesKeyInLabel() throws Exception {
        PersistableBusinessObjectValuesFinder valuesFinder = new PersistableBusinessObjectValuesFinder();
        valuesFinder.setBusinessObjectClass(AccountType.class);
        valuesFinder.setKeyAttributeName("accountTypeCode");
        valuesFinder.setLabelAttributeName("name");
        valuesFinder.setIncludeKeyInDescription(true);
        List<KeyValue> keyValues = valuesFinder.getKeyValues();
        assertEquals(testKeyValuesKeyInLabel.size(), keyValues.size());
        for (KeyValue testKeyValue: testKeyValuesKeyInLabel) {
            assertEquals(testKeyValue.getValue(), valuesFinder.getKeyLabel(testKeyValue.getKey()));
        }
    }

    /**
     * KULRICE-11708 - removing the two tests for FiscalOfficer since the TRV_ACCT_FO table no longer exists.
     * No replacement tests are needed since testGetKeyValues and testGetKeyValuesKeyInLabel are testing similar
     * functionality for the accountTypeCode.  Choosing to delete these tests rather than ingore them so that
     * the AccountManager class can be deleted if so desired.
     */
}
