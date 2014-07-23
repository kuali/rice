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

import org.kuali.rice.krad.demo.ViewDemoAftBase;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountLookUpAutoSearchAft extends ViewDemoAftBase {

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

    public void navigate() throws Exception {
        waitAndClickDemoLink();
        waitAndClickByLinkText("Account Lookup Auto Search Pre-Filtered");
    }

    protected void testDemoTravelAccountLookUpAutoSearch() throws Exception {
        waitForTextPresent("Showing 1 to 10");

        By[] results = {By.linkText("a1"), By.linkText("a2"), By.linkText("a3"), By.linkText("a4"), By.linkText("a5"),
                By.linkText("a6"), By.linkText("a7"), By.linkText("a8"), By.linkText("a9"), By.linkText("a10"),
                By.linkText("a11"), By.linkText("a12"), By.linkText("a13"), By.linkText("a14")};

        assertElementsPresentInResultPages(results);
        waitAndClickByXpath("//div[@data-label='Travel Account Type Code']/div/div/button[@class='btn btn-default uif-action icon-search']");
    	waitSearchAndReturnFromLightbox();

// TODO should the foid work?
//        waitAndTypeByName("lookupCriteria[foId]","1");
//        waitAndClickButtonByText(SEARCH);
//        Thread.sleep(3000);
//        assertElementPresentByXpath("//a[contains(text(), 'a1')]");
//        if(isElementPresentByLinkText("a2") || isElementPresentByLinkText("a3")) {
//            fail("Search Functionality not working properly.");
//        }
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
    
    private void waitSearchAndReturnFromLightbox() throws Exception {
    	gotoLightBox();
    	waitAndClickButtonByText("Search");
    	waitAndClickByLinkText("return value");
    }
}
