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
package org.kuali.rice.krad.demo.lookupviews.lookup.conditionalresults;

import org.junit.Assert;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLookUpConditionalResultsAft extends AutomatedFunctionalTestBase {

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
    private static final String TRAVEL_ACCOUNT_NAME_COLUMN_NAME = "Travel Account Number";

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
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Lookup Conditional Results");
    }

    protected void testLookUpConditionalResults() throws InterruptedException {
        // Case 1 - Search of a1 only shows Travel Account Number column
        waitAndTypeByName(LOOKUP_CRITERIA_NUMBER_NAME, "a1");
        waitAndClickButtonByText(SEARCH);
        Thread.sleep(3000);
        Assert.assertEquals(1, getCssCount("div#uLookupResults thead th"));
        assertElementPresent("div#uLookupResults thead th:nth-child(1)");
        assertTextPresent(TRAVEL_ACCOUNT_NAME_COLUMN_NAME, "div#uLookupResults thead th:nth-child(1) label",
                "Travel Account Name column not present");

        // Case 2 - Empty research shows all columns
        clearTextByName(LOOKUP_CRITERIA_NUMBER_NAME);
        waitAndClickButtonByText(SEARCH);
        Thread.sleep(3000);
        Assert.assertEquals(3, getCssCount("div#uLookupResults thead th"));
        assertElementPresent("div#uLookupResults thead th:nth-child(1)");
        assertTextPresent(TRAVEL_ACCOUNT_NAME_COLUMN_NAME, "div#uLookupResults thead th:nth-child(1) label",
                "Travel Account Name column not present");
        assertElementPresent("div#uLookupResults thead th:nth-child(2)");
        assertTextPresent(ACCOUNT_NAME_COLUMN_NAME, "div#uLookupResults thead th:nth-child(2) label",
                "Account Name column not present");
        assertElementPresent("div#uLookupResults thead th:nth-child(3)");
        assertTextPresent(DATE_CREATED_COLUMN_NAME, "div#uLookupResults thead th:nth-child(3) label",
                "Date Created column not present");
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
