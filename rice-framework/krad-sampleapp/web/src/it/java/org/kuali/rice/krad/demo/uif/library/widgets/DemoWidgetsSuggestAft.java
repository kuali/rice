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
package org.kuali.rice.krad.demo.uif.library.widgets;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.krad.demo.uif.library.DemoLibraryBase;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoWidgetsSuggestAft extends DemoLibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-SuggestView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-SuggestView&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo("Widgets", "Suggest");
    }

    //  this is the one
    protected void testWidgetsSuggest(String exampleLink, String inputFieldName, String search,
            String result) throws Exception {

        //go to correct example
        waitAndClickByLinkText(exampleLink);

        //enter value
        waitAndTypeByName(inputFieldName, search);

        //verify expect suggest results
        waitForElementPresent(By.linkText(result), "Library Widget Suggest, " + result + " not suggested");
        waitAndClickByLinkText(result);

        //verify text is populated
        if (!driver.findElement(By.name(inputFieldName)).getAttribute("value").equals(result)) {
            fail("input text is incorrect");
        }

    }

    protected void testWidgetsSuggestHelperMethod2() throws Exception {

        final String EXAMPLE_LINK_NAME = "View Helper Method Configuration 2";
        final String INPUT_FIELD_NAME = "inputField4";
        final String SEARCH_VALUE = "a";
        final String RESULT = "a2";

        //go to correct example
        waitAndClickByLinkText(EXAMPLE_LINK_NAME);

        //enter values
        waitAndTypeByName("inputField3", "SUB1");
        waitAndTypeByName(INPUT_FIELD_NAME, SEARCH_VALUE);

        //verify expect suggest results
        assertElementPresentByLinkText(RESULT);
        waitAndClickByLinkText(RESULT);

        //verify text is populated
        if (!driver.findElement(By.name(INPUT_FIELD_NAME)).getAttribute("value").equals(RESULT)) {
            fail("input text is incorrect");
        }
    }

    protected void testWidgetsSuggestValidationHelperMethod() throws Exception {
        final String EXAMPLE_LINK_NAME = "Service Method and Sorting Configuration";
        final String INPUT_FIELD_NAME = "inputField6";
        final String invalidSearch = "Travel Account 10";
        final String validSearch = "Travel Account 2";

        waitAndClickLinkContainingText(EXAMPLE_LINK_NAME);

        assertFocusTypeBlurError(INPUT_FIELD_NAME, invalidSearch);
        assertFocusTypeBlurValid(INPUT_FIELD_NAME, validSearch);
    }

    protected void testWidgetsSuggestRichText() throws Exception {

        final String EXAMPLE_LINK_NAME = "Rich suggest options";
        final String INPUT_FIELD_NAME = "inputField8";
        final String SEARCH_VALUE = "r";

        //go to correct example
        waitAndClickByLinkText(EXAMPLE_LINK_NAME);

        //enter values
        waitAndTypeByName(INPUT_FIELD_NAME, SEARCH_VALUE);

        //verify expect suggest results
        WebElement resultLink = driver.findElement(By.partialLinkText("Rich Option 1"));
        resultLink.click();

        //verify text is populated
        if (!driver.findElement(By.name(INPUT_FIELD_NAME)).getAttribute("value").equals("r1")) {
            fail("input text is incorrect");
        }
    }

    protected void testTooltips() throws Exception {
        testWidgetsSuggest("View Helper Method Configuration 1", "inputField2", "a1", "a14");
        testWidgetsSuggestHelperMethod2();
        testWidgetsSuggestValidationHelperMethod();
        testWidgetsSuggest("Local suggest options", "inputField7", "cold", "ColdFusion");
        testWidgetsSuggest("Configured suggest options", "inputField9", "cold", "ColdFusion");
    }

    @Test
    public void testWidgetsTooltipBookmark() throws Exception {
        testTooltips();
        passed();
    }

    @Test
    public void testWidgetsTooltipAutoQueryBookmark() throws Exception {
        testWidgetsSuggest("Auto-Query Configuration", "inputField1", "ca", "CAT");
        passed();
    }

    @Test
    public void testWidgetsTooltipCustomSelectionBookmark() throws Exception {
        testWidgetsSuggest("Custom selection", "inputField10", "jm", "jmcross");
        passed();
    }

    @Test
    public void testWidgetsTooltipRichTextBookmark() throws Exception {
        testWidgetsSuggestRichText();
        passed();
    }

    @Test
    @Ignore // https://jira.kuali.org/browse/RICEQA-434 AFT Failures in CI that pass locally
    public void testWidgetsTooltipServiceSortingBookmark() throws Exception {
        testWidgetsSuggest("Service Method and Sorting Configuration", "inputField6", "Tr", "Travel Account 1");
        passed();
    }

    @Test
    public void testWidgetsTooltipNav() throws Exception {
        testTooltips();
        passed();
    }

    @Test
    public void testWidgetsTooltipAutoQueryNav() throws Exception {
        testWidgetsSuggest("Auto-Query Configuration", "inputField1", "ca", "CAT");
        passed();
    }

    @Test
    public void testWidgetsTooltipCustomSelectionNav() throws Exception {
        testWidgetsSuggest("Custom selection", "inputField10", "jm", "jmcross");
        passed();
    }

    @Test
    public void testWidgetsTooltipRichTextNav() throws Exception {
        testWidgetsSuggestRichText();
        passed();
    }

    @Test
    @Ignore // https://jira.kuali.org/browse/RICEQA-434 AFT Failures in CI that pass locally
    public void testWidgetsTooltipServiceSortingNav() throws Exception {
        testWidgetsSuggest("Service Method and Sorting Configuration", "inputField6", "Tr", "Travel Account 1");
        passed();
    }
}


