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
package org.kuali.rice.krad.demo.travel.account;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class DemoTravelAccountLookUpAutoSearchAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/lookup?methodToCall=search&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&lookupCriteria['number']=a*&readOnlyFields=number&hideReturnLink=true&showMaintenanceLinks=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=search&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&lookupCriteria['number']=a*&readOnlyFields=number&hideReturnLink=true&showMaintenanceLinks=true";
    
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

    protected void navigation() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Account Lookup Auto Search");
    }

    protected void testDemoTravelAccountLookUpAutoSearch() throws Exception {
        assertElementPresentByXpath("//a[contains(text(), 'a1')]");
        assertElementPresentByXpath("//a[contains(text(), 'a2')]");
        assertElementPresentByXpath("//a[contains(text(), 'a3')]");
        waitAndTypeByName("lookupCriteria[foId]","1");
        waitAndClickButtonByText(SEARCH);
        Thread.sleep(3000);
        assertElementPresentByXpath("//a[contains(text(), 'a1')]");
        if(isElementPresentByLinkText("a2") || isElementPresentByLinkText("a3")) {
            fail("Search Functionality not working properly.");
        }
    }

    @Test
    public void testDemoTravelAccountLookUpAutoSearchNav() throws Exception {
        testDemoTravelAccountLookUpAutoSearch();
        passed();
    }

    @Test
    public void testDemoTravelAccountLookUpAutoSearchBookmark() throws Exception {
        testDemoTravelAccountLookUpAutoSearch();
        passed();
    }
}
