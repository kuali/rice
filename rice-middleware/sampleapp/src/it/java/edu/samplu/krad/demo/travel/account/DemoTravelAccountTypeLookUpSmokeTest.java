/**
 * Copyright 2005-2013 The Kuali Foundation
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
package edu.samplu.krad.demo.travel.account;

import edu.samplu.common.SmokeTestBase;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountTypeLookUpSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.account.TravelAccountType&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.account.TravelAccountType&hideReturnLink=true";
    
    /**
     * Search
     */
    public static final String SEARCH = "Search";
    
    /**
     * Clear Values
     */
    public static final String CLEAR_VALUES = "Clear Values";
    
    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Account Type Lookup");
    }

    protected void testTravelAccountTypeLookUp() throws Exception {
        waitAndTypeByName("lookupCriteria[accountTypeCode]","CAT");
        waitAndClickButtonByText(SEARCH);
        assertElementPresentByXpath("//input[@value='CAT' and @name='lookupResults[0].accountTypeCode']");
        waitAndClickButtonByText(CLEAR_VALUES);
        waitAndClickButtonByText(SEARCH);
        assertElementPresentByXpath("//input[@value='CAT' and @name='lookupResults[0].accountTypeCode']");
        assertElementPresentByXpath("//input[@value='EAT' and @name='lookupResults[1].accountTypeCode']");
        assertElementPresentByXpath("//input[@value='IAT' and @name='lookupResults[2].accountTypeCode']");
    }

    @Test
    public void testTravelAccountTypeLookUpNav() throws Exception {
        testTravelAccountTypeLookUp();
        passed();
    }

    @Test
    public void testTravelAccountTypeLookUpBookmark() throws Exception {
        testTravelAccountTypeLookUp();
        passed();
    }
}