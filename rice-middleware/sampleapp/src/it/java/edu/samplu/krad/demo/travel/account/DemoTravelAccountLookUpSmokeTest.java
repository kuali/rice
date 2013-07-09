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
public class DemoTravelAccountLookUpSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.account.TravelAccount&hideReturnLink=true&showMaintenanceLinks=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.account.TravelAccount&hideReturnLink=true&showMaintenanceLinks=true";
    
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
        waitAndClickByLinkText("Account Lookup");
    }

    protected void testTravelAccountLookUp() throws Exception {
        waitAndTypeByName("lookupCriteria[number]","a1");
        waitAndClickButtonByText(SEARCH);
        waitForElementPresentByXpath("//a[contains(text(), 'a1')]");
        waitAndClickButtonByText(CLEAR_VALUES);
        Thread.sleep(3000);
        waitAndClickButtonByText(SEARCH);
        Thread.sleep(3000);
        assertElementPresentByXpath("//a[contains(text(), 'a1')]");
        assertElementPresentByXpath("//a[contains(text(), 'a2')]");
    }

    @Test
    public void testTravelAccountLookUpBookmark() throws Exception {
        testTravelAccountLookUp();
        passed();
    }

    @Test
    public void testTravelAccountLookUpNav() throws Exception {
        testTravelAccountLookUp();
        passed();
    }
}