/**
 * Copyright 2005-2016 The Kuali Foundation
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
package edu.sampleu.krad.reference;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AddressTypeAft extends WebDriverLegacyITBase {

    /**
     * AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Address%20Type&channelUrl="
     * + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KRAD_LOOKUP_METHOD +
     * "org.kuali.rice.kim.impl.identity.address.EntityAddressTypeBo&renderReturnLink=true";
     */
    public static final String BOOKMARK_URL =
            AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Address%20Type&channelUrl=" + WebDriverUtils
                    .getBaseUrlString() + AutomatedFunctionalTestUtils.KRAD_LOOKUP_METHOD +
                    "org.kuali.rice.kim.impl.identity.address.EntityAddressTypeBo&renderReturnLink=true";

    protected void clickSearch() throws InterruptedException {
        waitAndClickSearchByText();
    }

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByLinkText("Address Type");
    }

    //Code for KRAD Test Package.
    protected void testAddressType() throws Exception {
        selectFrameIframePortlet();

        //Search by "Both" Filter in Active Indicator
        clickSearch();
        String[][] data = {{"HM", "Home", "b"},
                {"OTH", "Other", "c"},
                {"WRK", "Work", "a"}};
        assertTextPresent(data);
        waitAndClickClearValues();

        //Search by "Yes" Filter in Active Indicator
        waitAndClickByXpath("//input[@name='lookupCriteria[active]' and @value='Y']");
        clickSearch();
        assertTextPresent(data);
        waitAndClickClearValues();

        //Search by "No" Filter in Active Indicator
        waitAndClickByXpath("//input[@name='lookupCriteria[active]' and @value='N']");
        clickSearch();
        waitForTextPresent("No values match this search.");
        waitAndClickClearValues();

        //Search by Address Type Code Filter
        waitAndTypeByName("lookupCriteria[code]","HM");
        clickSearch();
        String[] assertSearchResultForTypeCode = {"HM", "Home", "b"};
        assertTextPresent(assertSearchResultForTypeCode);
        waitAndClickClearValues();

        //Search by Address Type Name Filter
        waitAndTypeByName("lookupCriteria[name]","Home");
        clickSearch();
        String[] assertSearchResultForTypeName = {"HM", "Home", "b"};
        assertTextPresent(assertSearchResultForTypeName);
        waitAndClickClearValues();
    }

    protected void testAddressTypeNoResults() throws Exception {
        selectFrameIframePortlet();
        waitAndClickByXpath("//input[@value='N']");
        clickSearch();
        waitForTextPresent("No values match this search.");
    }

    @Test
    public void testAddressTypeBookmark() throws Exception {
        testAddressType();
//        assertTextPresent("return value"); // bookmark should have return values as BOOKMARK_URL has renderReturnLink=true
        passed();
    }

    @Test
    public void testAddressTypeNoResultsBookmark() throws Exception {
        testAddressTypeNoResults();
        passed();
    }

    @Test
    public void testAddressTypeNav() throws Exception {
        testAddressType();
        assertTextNotPresent("return value"); // navigation should not have return values
        passed();
    }

    @Test
    public void testAddressTypeNoResultsNav() throws Exception {
        testAddressTypeNoResults();
        passed();
    }
}
