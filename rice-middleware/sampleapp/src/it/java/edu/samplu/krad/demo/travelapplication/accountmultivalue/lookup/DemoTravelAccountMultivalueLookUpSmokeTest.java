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
package edu.samplu.krad.demo.travelapplication.accountmultivalue.lookup;

import edu.samplu.common.SmokeTestBase;
import org.junit.Test;
import org.openqa.selenium.WebElement;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountMultivalueLookUpSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true&multipleValuesSelect=true&suppressActions=true&conversionFields=number:foo,name:foo
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true&multipleValuesSelect=true&suppressActions=true&conversionFields=number:foo,name:foo";
   
    /**
     * lookupCriteria[accountTypeCode]
     */
    public static final String ACCOUNT_TYPE_CODE_NAME = "lookupCriteria[accountTypeCode]";

    /**
     * return selected
     */
    public static final String RETURN_SELECTED_BUTTON_TEXT = "return selected";

    /**
     * Search
     */
    public static final String SEARCH = "Search";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Account Multi-Value Lookup");
    }

    protected void testTravelAccountMultivalueLookUp() throws Exception {
        testSearchSelect();

        testSelectThisPage();

        testSelectAllPages();
    }

    private void testSelectAllPages() throws InterruptedException {
        waitAndSelectByName(ACCOUNT_TYPE_CODE_NAME, "Income Account Type");
        waitAndClickButtonByText(SEARCH);
        waitAndClickButtonByText("select all from all pages");
        assertTextPresent("a1");
        assertTextPresent("a3");
        assertButtonEnabledByText(RETURN_SELECTED_BUTTON_TEXT);
        waitAndClickButtonByText("deselect all from all pages");
        assertButtonDisabledByText(RETURN_SELECTED_BUTTON_TEXT);
    }

    private void testSelectThisPage() throws InterruptedException {
        waitAndSelectByName(ACCOUNT_TYPE_CODE_NAME, "Expense Account Type");
        waitAndClickButtonByText(SEARCH);
        WebElement dropdownMenu = getElementByAttributeValue("class", "dropdown-toggle");
        dropdownMenu.click();
        waitAndClickLinkContainingText("select all items on this page", "dropdown click select all items on this page problem");
        Thread.sleep(5000);
        assertButtonEnabledByText(RETURN_SELECTED_BUTTON_TEXT);
        assertTextPresent("a2");
        assertTextPresent("a8");
        dropdownMenu = getElementByAttributeValue("class", "dropdown-toggle");
        dropdownMenu.click();
        waitAndClickButtonByText("deselect all from this page");
        waitForPageToLoad();
        assertButtonDisabledByText(RETURN_SELECTED_BUTTON_TEXT);
    }

    private void testSearchSelect() throws InterruptedException {
        waitAndSelectByName(ACCOUNT_TYPE_CODE_NAME, "Clearing Account Type");
        waitAndClickButtonByText(SEARCH);
        waitAndClickByName("selectedCollectionLines['lookupResults']");
        assertTextPresent("a14");
        assertTextPresent("a6");
        assertTextPresent("a9");
        assertButtonEnabledByText(RETURN_SELECTED_BUTTON_TEXT);
        waitAndClickByName("selectedCollectionLines['lookupResults']");
        assertButtonDisabledByText(RETURN_SELECTED_BUTTON_TEXT);
    }

    @Test
    public void testTravelAccountMultivalueLookUpBookmark() throws Exception {
        testTravelAccountMultivalueLookUp();
        passed();
    }

    @Test
    public void testTravelAccountMultivalueLookUpNav() throws Exception {
        testTravelAccountMultivalueLookUp();
        passed();
    }
}