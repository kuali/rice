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
package org.kuali.rice.krad.demo.lookup;

import org.kuali.rice.krad.demo.ViewDemoAftBase;
import org.openqa.selenium.By;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLookUpAft extends ViewDemoAftBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LookupSampleView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LookupSampleView&hideReturnLink=true";

    /**
     *  lookupCriteria[number]
     */
    private static final String LOOKUP_CRITERIA_NUMBER_NAME="lookupCriteria[number]";
    
    /**
     *  Search
     */
    private static final String SEARCH="Search";
    
    /**
     * Clear Values
     */
    private static final String CLEAR_VALUES="Clear Values";
    
    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Lookup");
    }

    protected void testLookUp() throws InterruptedException {
        waitAndTypeByName(LOOKUP_CRITERIA_NUMBER_NAME,"a1");
        waitAndClickButtonByText(SEARCH);
        Thread.sleep(3000);
        waitForElementPresentByXpath("//a[contains(text(), 'a1')]");
        waitAndClickButtonByText(CLEAR_VALUES);
        waitAndClickButtonByText(SEARCH);
        waitForElementPresentByXpath("//a[contains(text(), 'a1')]");
        waitForElementPresentByXpath("//a[contains(text(), 'a6')]");
        waitAndClickButtonByText(CLEAR_VALUES);
        waitAndTypeByName("lookupCriteria[name]","Travel Account 1");
        waitAndClickButtonByText(SEARCH);
        waitForElementPresentByXpath("//a[contains(text(), 'a1')]");
        waitForElementNotPresent(By.xpath("//a[contains(text(), 'a2')]"));
        waitAndClickButtonByText(CLEAR_VALUES);
        waitAndTypeByName("lookupCriteria[rangeLowerBoundKeyPrefix_createDate]","02/01/2014");
        waitAndTypeByName("lookupCriteria[createDate]","02/13/2014");
        waitAndClickButtonByText(SEARCH);
        waitForTextPresent("No values match this search.");
    }

    protected void testLookUpFancy() throws InterruptedException {
        waitAndTypeByName("lookupCriteria[name]","*");
        waitAndTypeByName("lookupCriteria[fiscalOfficer.principalName]","eri*");
        waitAndClickButtonByText(SEARCH);
        Thread.sleep(3000);
        waitForElementPresentByXpath("//a[contains(text(), 'a10')]");
    }

    @Test
    public void testLookUpBookmark() throws Exception {
        testLookUp();
        passed();
    }

    @Test
    public void testLookUpNav() throws Exception {
        testLookUp();
        passed();
    }

    @Test
    public void testLookUpFancyNav() throws Exception {
        testLookUpFancy();
        passed();
    }
}
