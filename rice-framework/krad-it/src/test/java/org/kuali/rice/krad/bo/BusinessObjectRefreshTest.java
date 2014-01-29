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
package org.kuali.rice.krad.bo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.test.KRADTestCase;
import org.kuali.rice.krad.test.document.bo.AccountExtension;
import org.kuali.rice.location.impl.county.CountyBo;
import org.kuali.rice.location.impl.state.StateBo;
import org.kuali.rice.test.BaselineTestCase;
import org.kuali.rice.test.data.PerTestUnitTestData;
import org.kuali.rice.test.data.UnitTestData;
import org.kuali.rice.test.data.UnitTestFile;
import org.kuali.rice.test.data.UnitTestSql;

/**
 * Tests how refreshing works for Business Objects
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@PerTestUnitTestData(
        value = @UnitTestData(
                order = {UnitTestData.Type.SQL_STATEMENTS, UnitTestData.Type.SQL_FILES},
                sqlStatements = {
                        @UnitTestSql("delete from trv_acct_ext")
                        ,@UnitTestSql("delete from trv_acct_type")
                },
                sqlFiles = {
                        @UnitTestFile(filename = "classpath:testAccountType.sql", delimiter = ";"),
                        @UnitTestFile(filename = "classpath:testAccountExtensions.sql", delimiter = ";")
                }
        ),
        tearDown = @UnitTestData(
                sqlStatements = {
                        @UnitTestSql("delete from trv_acct_ext")
                        ,@UnitTestSql("delete from trv_acct_type")
                }
       )
)
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.NONE)
@KRADTestCase.Legacy
public class BusinessObjectRefreshTest extends KRADTestCase {

	@Test
    /**
     * tests that {@link PersistableBusinessObjectBase#refreshReferenceObject(String)} works for a lazy loaded reference when the foreign key is changed
     */
    public void testLazyRefreshField() {
        final String number = "a1";
        AccountExtension accountExtension = KNSServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(AccountExtension.class, number);

        assertNotNull( "Unable to retrieve account extension a1", accountExtension );
        assertEquals("Retrieved account extension should have an account type of IAT", "IAT",
                accountExtension.getAccountType().getAccountTypeCode());
        assertEquals("Retrieved account extension should have an account type name of Income Account Type",
                "Income Account Type", accountExtension.getAccountType().getName());
        accountExtension.setAccountTypeCode("CAT");
        accountExtension.refreshReferenceObject("accountType");
        assertEquals("Account extension should now have an account type name of Clearing Account Type",
                "Clearing Account Type", accountExtension.getAccountType().getName());
    }

    @Test
    /**
     * tests that {@link PersistableBusinessObjectBase#refresh()} works for a lazy loaded reference when the foreign key is changed
     */
    public void testLazyRefreshWholeObject() {
        final String number = "a1";
        AccountExtension accountExtension = KNSServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(AccountExtension.class, number);

        assertNotNull( "Unable to retrieve account extension a1", accountExtension );
        assertEquals("Retrieved account extension should have account type IAT", "IAT",
                accountExtension.getAccountType().getAccountTypeCode());
        assertEquals("Retrieved account extension should have an account type name of Income Account Type",
                "Income Account Type", accountExtension.getAccountType().getName());

        accountExtension.setAccountTypeCode("CAT");
        accountExtension.refresh();

        assertEquals("Account extension should now have an account type name of Clearing Account Type",
                "Clearing Account Type", accountExtension.getAccountType().getName());
    }

    @Test
    /**
     * tests that {@link PersistableBusinessObjectBase#refresh()} works for an non lazy loaded reference when the foreign key is changed
     */
    public void testEagerRefreshEboField() {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put("code", "COCONINO");
        primaryKeys.put("countryCode", "US");
        primaryKeys.put("stateCode","AZ");
        //final CountyId countyId = new CountyId("COCONINO", "US", "AZ");
        CountyBo county = KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(CountyBo.class, primaryKeys);

        primaryKeys.clear();
        primaryKeys.put("countryCode","US");
        primaryKeys.put("code","AZ");
        //final StateId arizonaStateId = new StateId("US", "AZ");
        final StateBo arizonaState = KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(StateBo.class, primaryKeys);

        assertEquals("On retrieval from database, state code should be AZ", arizonaState.getCode(),
                county.getState().getCode());
        assertEquals("On retrieval from database, state name should be ARIZONA", arizonaState.getName(),
                county.getState().getName());

        county.setStateCode("CA");
        county.setCode("VENTURA");
        // NOTE: since county is an EBO, whether or not refresh() fetches references is an implementation choice in the LocationModuleService
        county.refresh();

        //final StateId californiaStateId = new StateId("US", "CA");
        primaryKeys.clear();
        primaryKeys.put("countryCode","US");
        primaryKeys.put("code","CA");
        final StateBo californiaState = KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(StateBo.class, primaryKeys);

        assertEquals("Does eager fetching automatically refresh?", californiaState.getCode(),
                county.getState().getCode());
        assertEquals("On refresh, state name should be CALIFORNIA", californiaState.getName(),
                county.getState().getName());
    }
}