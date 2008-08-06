/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.rice.kns.lookup.keyvalues.PersistableBusinessObjectValuesFinder;
import org.kuali.rice.kns.test.document.bo.AccountManager;
import org.kuali.rice.kns.test.document.bo.AccountType;
import org.kuali.test.TestBase;

/**
 * This class tests the PersistableBusinessObjectValuesFinder.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PersistableBusinessObjectValuesFinderTest extends TestBase {

    private List<KeyLabelPair> testKeyValues = new ArrayList<KeyLabelPair>();
    private List<KeyLabelPair> testKeyValuesKeyInLabel = new ArrayList<KeyLabelPair>();
    private List<KeyLabelPair> testKeyValuesLongKey = new ArrayList<KeyLabelPair>();
    private List<KeyLabelPair> testKeyValuesKeyInLabelLongKey = new ArrayList<KeyLabelPair>();

    /**
     * Default Constructor builds KeyLabelPair Lists used for tests.
     *
     */
    public PersistableBusinessObjectValuesFinderTest() {
	testKeyValues.add(new KeyLabelPair("CAT", "Clearing Account Type"));
	testKeyValues.add(new KeyLabelPair("EAT", "Expense Account Type"));
	testKeyValues.add(new KeyLabelPair("IAT", "Income Account Type"));

	testKeyValuesKeyInLabel.add(new KeyLabelPair("CAT", "CAT - Clearing Account Type"));
	testKeyValuesKeyInLabel.add(new KeyLabelPair("EAT", "EAT - Expense Account Type"));
	testKeyValuesKeyInLabel.add(new KeyLabelPair("IAT", "IAT - Income Account Type"));

	testKeyValuesLongKey.add(new KeyLabelPair(new Long(1), "fred"));
	testKeyValuesLongKey.add(new KeyLabelPair(new Long(2), "fran"));
	testKeyValuesLongKey.add(new KeyLabelPair(new Long(3), "frank"));

	testKeyValuesKeyInLabelLongKey.add(new KeyLabelPair(new Long(1), "1 - fred"));
	testKeyValuesKeyInLabelLongKey.add(new KeyLabelPair(new Long(2), "2 - fran"));
	testKeyValuesKeyInLabelLongKey.add(new KeyLabelPair(new Long(3), "3 - frank"));
    }

    /**
     * This method tests to make sure teh PersistableBusinessObjectValuesFinder works
     * as expected for the TravelAccountType BO.
     *
     * @throws Exception
     */
    @Test public void testGetKeyValues() throws Exception {
	PersistableBusinessObjectValuesFinder valuesFinder = new PersistableBusinessObjectValuesFinder();
	valuesFinder.setBusinessObjectClass(AccountType.class);
	valuesFinder.setKeyAttributeName("accountTypeCode");
	valuesFinder.setLabelAttributeName("name");
	valuesFinder.setIncludeKeyInDescription(false);
	List<KeyLabelPair> keyValues = valuesFinder.getKeyValues();
	assertEquals(testKeyValues.size(), keyValues.size());
	for (KeyLabelPair testKeyLabelPair: testKeyValues) {
            assertEquals(testKeyLabelPair.getLabel(), valuesFinder.getKeyLabel(testKeyLabelPair.getKey()));
	}
    }

    /**
     * This method tests to make sure teh PersistableBusinessObjectValuesFinder works
     * as expected for the TravelAccountType BO with the key included in the label.
     *
     * @throws Exception
     */
    @Test public void testGetKeyValuesKeyInLabel() throws Exception {
	PersistableBusinessObjectValuesFinder valuesFinder = new PersistableBusinessObjectValuesFinder();
	valuesFinder.setBusinessObjectClass(AccountType.class);
	valuesFinder.setKeyAttributeName("accountTypeCode");
	valuesFinder.setLabelAttributeName("name");
	valuesFinder.setIncludeKeyInDescription(true);
	List<KeyLabelPair> keyValues = valuesFinder.getKeyValues();
	assertEquals(testKeyValuesKeyInLabel.size(), keyValues.size());
	for (KeyLabelPair testKeyLabelPair: testKeyValuesKeyInLabel) {
            assertEquals(testKeyLabelPair.getLabel(), valuesFinder.getKeyLabel(testKeyLabelPair.getKey()));
	}
    }

    /**
     * This method tests to make sure teh PersistableBusinessObjectValuesFinder works
     * as expected for the FiscalOfficer BO.
     *
     * @throws Exception
     */
    @Test public void testGetKeyValuesLongKey() throws Exception {
	PersistableBusinessObjectValuesFinder valuesFinder = new PersistableBusinessObjectValuesFinder();
	valuesFinder.setBusinessObjectClass(AccountManager.class);
	valuesFinder.setKeyAttributeName("amId");
	valuesFinder.setLabelAttributeName("userName");
	valuesFinder.setIncludeKeyInDescription(false);
	List<KeyLabelPair> keyValues = valuesFinder.getKeyValues();
	assertEquals(testKeyValuesLongKey.size(), keyValues.size());
	for (KeyLabelPair testKeyLabelPair: testKeyValuesLongKey) {
            assertEquals(testKeyLabelPair.getLabel(), valuesFinder.getKeyLabel(testKeyLabelPair.getKey()));
	}
    }

    /**
     * This method tests to make sure teh PersistableBusinessObjectValuesFinder works
     * as expected for the FiscalOfficer BO with the key included in the label.
     *
     * @throws Exception
     */
    @Test public void testGetKeyValuesKeyInLabelLongKey() throws Exception {
	PersistableBusinessObjectValuesFinder valuesFinder = new PersistableBusinessObjectValuesFinder();
	valuesFinder.setBusinessObjectClass(AccountManager.class);
	valuesFinder.setKeyAttributeName("amId");
	valuesFinder.setLabelAttributeName("userName");
	valuesFinder.setIncludeKeyInDescription(true);
	List<KeyLabelPair> keyValues = valuesFinder.getKeyValues();
	assertEquals(testKeyValuesKeyInLabelLongKey.size(), keyValues.size());
	for (KeyLabelPair testKeyLabelPair: testKeyValuesKeyInLabelLongKey) {
            assertEquals(testKeyLabelPair.getLabel(), valuesFinder.getKeyLabel(testKeyLabelPair.getKey()));
	}
    }

}
