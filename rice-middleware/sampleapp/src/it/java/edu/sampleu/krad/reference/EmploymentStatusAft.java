/**
 * Copyright 2005-2018 The Kuali Foundation
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

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * Tests the Component section in Rice.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EmploymentStatusAft extends WebDriverLegacyITBase {

    /**
     *  AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Employment%20Status&channelUrl="
     *   + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KRAD_LOOKUP_METHOD +
     *   "org.kuali.rice.kim.impl.identity.employment.EntityEmploymentStatusBo&docFormKey=88888888&returnLocation="
     *   + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Employment%20Status&channelUrl="
            + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KRAD_LOOKUP_METHOD +
            "org.kuali.rice.kim.impl.identity.employment.EntityEmploymentStatusBo&docFormKey=88888888&returnLocation="
            + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;
         
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickKRAD();
        waitAndClickByLinkText("Employment Status");
    }

    //Code for KRAD Test Package.
    protected void testEmploymentStatus() throws Exception {
        selectFrameIframePortlet();
        waitAndClickClearValues();

        //Search by "Both" Filter in Active Indicator
        clickSearch();
        String[][] data = {{"A", "Active", "01","true"},
                {"D", "Deceased", "99","true"}};
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

        //Search by Code Filter
        waitAndTypeByName("lookupCriteria[code]","A");
        clickSearch();
        String[] assertSearchResultForTypeCode = {"A", "Active", "01","true"};
        assertTextPresent(assertSearchResultForTypeCode);
        waitAndClickClearValues();

        //Search by Name Filter
        waitAndTypeByName("lookupCriteria[name]","Active");
        clickSearch();
        String[] assertSearchResultForTypeName = {"A", "Active", "01","true"};
        assertTextPresent(assertSearchResultForTypeName);
        waitAndClickClearValues();

        //Search by Short Code Filter
        waitAndTypeByName("lookupCriteria[sortCode]","01");
        clickSearch();
        String[] assertSearchResultForShortCode = {"A", "Active", "01","true"};
        assertTextPresent(assertSearchResultForShortCode);
        waitAndClickClearValues();
    }

    protected void clickSearch() throws InterruptedException {
        waitAndClickSearchByText();
    }

    @Test
    public void testEmploymentStatusBookmark() throws Exception {
        testEmploymentStatus();
        passed();
    }

    @Test
    public void testEmploymentStatusNav() throws Exception {
        testEmploymentStatus();
        passed();
    }
}
