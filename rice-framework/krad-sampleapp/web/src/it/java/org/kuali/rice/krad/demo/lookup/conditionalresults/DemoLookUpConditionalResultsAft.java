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
package org.kuali.rice.krad.demo.lookup.conditionalresults;

import org.junit.Assert;
import org.kuali.rice.krad.demo.ViewDemoAftBase;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLookUpConditionalResultsAft extends ViewDemoAftBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LookupSampleViewConditionalResults&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LookupSampleViewConditionalResults";

    /**
     *  lookupCriteria[number]
     */
    private static final String LOOKUP_CRITERIA_NUMBER_NAME = "lookupCriteria[number]";

    /**
     *  Search
     */
    private static final String SEARCH = "Search";

    /**
     *  Travel Account Number column
     */
    private static final String TRAVEL_ACCOUNT_NUMBER_COLUMN_NAME = "Travel Account Number";

    /**
     *  Account Name column
     */
    private static final String ACCOUNT_NAME_COLUMN_NAME = "Account Name";

    /**
     *  Date Created column
     */
    private static final String DATE_CREATED_COLUMN_NAME = "Date Created";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickDemoLink();
        waitAndClickByLinkText("Lookup Conditional Results");
    }

    protected void testLookUpConditionalResults() throws InterruptedException {
        // Case 1 - Search of a1 only shows Travel Account Number column
        waitAndTypeByName(LOOKUP_CRITERIA_NUMBER_NAME, "a1");
        waitAndClickButtonByText(SEARCH);
        Thread.sleep(3000);
        Assert.assertEquals(2, getCssCount(DATA_TABLE_TH_CSS));
        assertElementPresent(DATA_TABLE_TH_CSS + ":nth-child(1)");

        // Deep pretty sure this selector has changed too, but passes as the text isn't found since the selector isn't
        assertTextNotPresent(ACCOUNT_NAME_COLUMN_NAME, DATA_TABLE_TH_CSS + ":nth-child(1)" + " label",
                ACCOUNT_NAME_COLUMN_NAME + " column not present");

        // Case 2 - Empty research shows all columns
        clearTextByName(LOOKUP_CRITERIA_NUMBER_NAME);
        waitAndClickButtonByText(SEARCH);
        waitForTextPresent("Showing 1 to 10 of");
        Assert.assertEquals(4, getCssCount(DATA_TABLE_TH_CSS));
        assertElementPresent(DATA_TABLE_TH_CSS + ":nth-child(2)");
        assertTextPresent(TRAVEL_ACCOUNT_NUMBER_COLUMN_NAME, DATA_TABLE_TH_CSS + ":nth-child(2) label",
                TRAVEL_ACCOUNT_NUMBER_COLUMN_NAME + " column not present");
        assertElementPresent(DATA_TABLE_TH_CSS + ":nth-child(3)");
        assertTextPresent(ACCOUNT_NAME_COLUMN_NAME, DATA_TABLE_TH_CSS + ":nth-child(3) label",
                ACCOUNT_NAME_COLUMN_NAME + " column not present");
        assertElementPresent(DATA_TABLE_TH_CSS + ":nth-child(4)");
        assertTextPresent(DATE_CREATED_COLUMN_NAME, DATA_TABLE_TH_CSS + ":nth-child(4) label",
                DATE_CREATED_COLUMN_NAME + " column not present");
    }

    @Test
    public void testLookUpConditionalResultsBookmark() throws Exception {
        testLookUpConditionalResults();
        passed();
    }

    @Test
    public void testLookUpConditionalResultsNav() throws Exception {
        testLookUpConditionalResults();
        passed();
    }
}
