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
package org.kuali.rice.krad.demo.lookup.multivalue;

import org.kuali.rice.krad.demo.ViewDemoAftBase;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * No Nav test, there is no link to this page.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLookUpMultiValueAft extends ViewDemoAftBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LookupSampleViewMultipleValuesSelectLimit&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LookupSampleViewMultipleValuesSelectLimit&hideReturnLink=true";
    
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
    
    /**
     * table table-condensed table-bordered uif-tableCollectionLayout dataTable
     */
    private static final String TABLE_ROW_SIX_XPATH="//table[@class='table table-condensed table-bordered uif-tableCollectionLayout dataTable']/tbody/tr[6]";
    
    /**
     * Clear Values
     */
    private static final String FAILURE_MESSAGE="Results are not restricted to 5 rows.";
    
    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Account Multi-Value Lookup");
    }

    protected void testLookUpMultiValue() throws InterruptedException {
        waitAndTypeByName(LOOKUP_CRITERIA_NUMBER_NAME, "a1");
        waitAndClickButtonByText(SEARCH);
        waitForElementPresentByXpath("//a[contains(text(), 'a1')]");
        waitAndClickButtonByText(CLEAR_VALUES);
        waitAndClickButtonByText(SEARCH);
        waitForElementNotPresent(By.xpath(TABLE_ROW_SIX_XPATH));
    }

    // No Nav test, there is no link to this page
    @Test
    public void testLookUpMultiValueBookmark() throws Exception {
        testLookUpMultiValue();
        passed();
    }
}
