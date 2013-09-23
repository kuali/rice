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

import edu.samplu.common.JiraAwareFailureUtil;
import edu.samplu.common.SmokeTestBase;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

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

    private void testSelectAllPages() throws InterruptedException {
        waitAndClickButtonByText(SEARCH);
        assertButtonDisabledByText(RETURN_SELECTED_BUTTON_TEXT);

        // select all, all checkboxes should be checked and return button enabled
        waitAndClickDropDown("select all items");
        if (!areAllChecked()) {
            JiraAwareFailureUtil.fail("select all items failure", this);
        }
        assertButtonEnabledByText(RETURN_SELECTED_BUTTON_TEXT);

        boolean anotherPageOfResults = false;
        if (Integer.parseInt(resultCount()) > 10) {
            anotherPageOfResults = true;
        }

        // all should be checked and button enabled on the next page as well (server side paging)
        if (!anotherPageOfResults) {
            JiraAwareFailureUtil.fail("select all items server side paging failure not enough results for next page", this);
        }
        waitAndClickByLinkText("Next");

        if (!areAllChecked()) {
            JiraAwareFailureUtil.fail("select all items server side paging failure", this);
        }
        assertButtonEnabledByText(RETURN_SELECTED_BUTTON_TEXT);

        // deselect all no checkboxes should be checked and return button disabled
        waitAndClickDropDown("deselect all items");
        if (!areNoneChecked()) {
            JiraAwareFailureUtil.fail("deselect all items failure", this);
        }
        assertButtonDisabledByText(RETURN_SELECTED_BUTTON_TEXT);

        waitAndClickByLinkText("Previous");
        if (!areNoneChecked()) {
            JiraAwareFailureUtil.fail("deselect all items failure", this);
        }
        assertButtonDisabledByText(RETURN_SELECTED_BUTTON_TEXT);
    }

    private void testSelectThisPage() throws InterruptedException {
        waitAndClickButtonByText(SEARCH);
        assertButtonDisabledByText(RETURN_SELECTED_BUTTON_TEXT);

        // select all on this page, all checkboxes should be checked and return button enabled
        assertSelectAllThisPage();

        boolean anotherPageOfResults = false;
        if (Integer.parseInt(resultCount()) > 10) {
            anotherPageOfResults = true;
        }

        // the next page should not have any checkboxes checked return button should still be enabled
        waitAndClickByLinkText("Next");
        if (!areNoneChecked()) {
            if (anotherPageOfResults) {
                JiraAwareFailureUtil.fail("select all items on this page failure", this);
            } else {
                JiraAwareFailureUtil.fail("select all items on this page failure not enough results for next page", this);
            }
        }
        assertButtonEnabledByText(RETURN_SELECTED_BUTTON_TEXT);

        // back to the previous page, checkboxes should be checked and return button enabled still
        waitAndClickByLinkText("Previous");
        if (!areAllChecked()) {
            JiraAwareFailureUtil.fail("select all items on previous page failure", this);
        }

        // deselect no checkboxes should be checked and the return button should be disabled
        assertDeselectAllThisPage();
    }

    private void assertDeselectAllThisPage() throws InterruptedException {
        waitAndClickDropDown("deselect all items on this page");
        if (!areNoneChecked()) {
            JiraAwareFailureUtil.fail("deselect all items on this page failure", this);
        }
        assertButtonDisabledByText(RETURN_SELECTED_BUTTON_TEXT);
    }

    private void assertSelectAllThisPage() throws InterruptedException {
        waitAndClickDropDown("select all items on this page");
        if (!areAllChecked()) {
            JiraAwareFailureUtil.fail("select all items on this page failure", this);
        }
        assertButtonEnabledByText(RETURN_SELECTED_BUTTON_TEXT);
    }

    private String resultCount() throws InterruptedException {
        List<WebElement> resultLi = waitAndGetElementsByAttributeValue("class", "uif-infoMessageItem");
        String resultsCount = resultLi.get(1).getText(); // second uif-infoMessageItem contains count
        resultsCount = resultsCount.substring(0, resultsCount.indexOf((" ")));
        return resultsCount;
    }

    private boolean areAllChecked() throws InterruptedException {
        WebElement tbody = waitAndGetElementByAttributeValue("role", "alert"); // results table body
        List<WebElement> checkboxes = findElements(By.className("uif-checkboxControl"),tbody);
        for (WebElement checkbox: checkboxes) {
            if (!"true".equals(checkbox.getAttribute("checked"))) {
                return false;
            }
        }
        return true;
    }

    private boolean areNoneChecked() throws InterruptedException {
        WebElement tbody = waitAndGetElementByAttributeValue("role", "alert"); // results table body
        List<WebElement> checkboxes = findElements(By.className("uif-checkboxControl"),tbody);
        for (WebElement checkbox: checkboxes) {
            if (null != checkbox.getAttribute("checked")) {
                return false;
            }
        }
        return true;
    }

    private void waitAndClickDropDown(String dropDownText) throws InterruptedException {
        WebElement dropdownMenu = waitAndGetElementByAttributeValue("class", "dropdown-toggle");
        Thread.sleep(1000);
        dropdownMenu.click();
        waitAndClickLinkContainingText(dropDownText, "dropdown click " + dropDownText + " problem");
    }

    private void testSearchSelect() throws InterruptedException {
        waitAndClickByValue("CAT");
        waitAndClickButtonByText(SEARCH);
        waitAndClickByName("selectedCollectionLines['lookupResults']");
        assertTextPresent("a14");
        assertTextPresent("a6");
        assertTextPresent("a9");
        assertButtonEnabledByText(RETURN_SELECTED_BUTTON_TEXT);
        waitAndClickByName("selectedCollectionLines['lookupResults']");
        assertButtonDisabledByText(RETURN_SELECTED_BUTTON_TEXT);

        assertSelectAllThisPage();
        assertDeselectAllThisPage();

        waitAndClickByName("selectedCollectionLines['lookupResults']");
        waitAndClickButtonByText(SEARCH);
        checkForIncidentReport();
    }

    @Test
    public void testTravelAccountMultivalueLookUpSearchSelectBookmark() throws Exception {
        testSearchSelect();
        passed();
    }

    @Test
    public void testTravelAccountMultivalueLookUpSearchSelectNav() throws Exception {
        testSearchSelect();
        passed();
    }

    @Test
    public void testTravelAccountMultivalueLookUpSelectThisPageBookmark() throws Exception {
        testSelectThisPage();
        passed();
    }

    @Test
    public void testTravelAccountMultivalueLookUpSelectThisPageNav() throws Exception {
        testSelectThisPage();
        passed();
    }

    @Test
    public void testTravelAccountMultivalueLookUpSelectAllPagesBookmark() throws Exception {
        testSelectAllPages();
        passed();
    }

    @Test
    public void testTravelAccountMultivalueLookUpSelectAllPagesNav() throws Exception {
        testSelectAllPages();
        passed();
    }
}