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
package org.kuali.rice.krad.demo.lookup.stackedresults;

import org.junit.Test;
import org.kuali.rice.krad.demo.ViewDemoAftBase;

/**
 * This class performs simple tests on the stacked results lookup
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLookUpStackedResultsAft extends ViewDemoAftBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LookupSampleViewMultipleValuesSelectLimit&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LookupSampleViewStackedResults&hideReturnLink=true";

    /**
     *  lookupCriteria[number]
     */
    private static final String LOOKUP_CRITERIA_NUMBER_NAME="lookupCriteria[number]";
    private static final String LOOKUP_CRITERIA_DATE="lookupCriteria[createDate]";

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
        waitAndClickDemoLink();
        waitAndClickByLinkText("Lookup with Stacked Results");
    }

    protected void testLookUpStackedResults() throws InterruptedException {
        waitAndTypeByName(LOOKUP_CRITERIA_NUMBER_NAME, "a1");
        waitAndClickButtonByText(SEARCH);
        waitForElementPresentByXpath("//a[contains(text(), 'a1')]");
        waitAndClickButtonByText(CLEAR_VALUES);
        waitAndClickButtonByText(SEARCH);
        waitForElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-gridLayout uif-table-fixed']/tbody/tr/td/div/span/a[contains(text(),'a1')]");
        assertTextPresent(new String[]{"Travel Account Number: Required"});
        waitAndClickButtonByText(CLEAR_VALUES);
        waitAndTypeByName(LOOKUP_CRITERIA_NUMBER_NAME, "");
        waitAndTypeByName(LOOKUP_CRITERIA_DATE, "234");
        fireMouseOverEventByName(LOOKUP_CRITERIA_NUMBER_NAME);
        waitForElementPresentByXpath("//div[@class='popover top in uif-tooltip-error-cs']");
        waitAndTypeByName(LOOKUP_CRITERIA_NUMBER_NAME, "");
        fireMouseOverEventByName(LOOKUP_CRITERIA_DATE);
        waitForElementPresentByXpath("//div[@class='popover top in uif-tooltip-error-cs']");
        waitAndClickButtonByText(SEARCH);
        assertTextPresent(new String[]{
            "Date Created: Must be a date in the following format(s): MM/dd/yy, MM/dd/yyyy, MM-dd-yy, MM-dd-yyyy, yyyy-MM-dd","This page has 2 errors","Travel Account Number: Required"});
        waitAndTypeByName(LOOKUP_CRITERIA_NUMBER_NAME, "a1");
        waitAndTypeByName(LOOKUP_CRITERIA_DATE, "08/23/2012");
        waitAndClickButtonByText(SEARCH);
    }

    @Test
    public void testLookUpMultiValueBookmark() throws Exception {
        testLookUpStackedResults();
        passed();
    }

    @Test
    public void testLookUpMultiValueNav() throws Exception {
        testLookUpStackedResults();
        passed();
    }
}
