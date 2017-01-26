/**
 * Copyright 2005-2017 The Kuali Foundation
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
package org.kuali.rice.krad.demo.uif.library.collections;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LibraryCollectionFeaturesAddLineAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-CollectionAddLineView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CollectionAddLineView";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Collection Features");
        waitAndClickByLinkText("Add Line");
    }

    protected void testCollectionFeaturesAddLine() throws Exception {
        selectByName("exampleShown", "Collection Default Add Line");

        // add some values to the collection
        waitAndTypeByName("newCollectionLines['collection1'].field1", "12");
        waitAndTypeByName("newCollectionLines['collection1'].field2", "5");
        jGrowl("Click Add button.");
        waitAndClickByXpath("//section[@id = 'Demo-CollectionAddLine-Example1']//button[contains(text(), 'Add')]");

        // assert that the values have been added
        waitForElementPresent("input[name = 'collection1[0].field1'][value = '12']");
        waitForElementPresent("input[name = 'collection1[0].field2'][value = '5']");
    }

    protected void testCollectionFeaturesAddLineWithDuplicatePropertyName() throws Exception {
        selectByName("exampleShown", "Collection Add Line with duplicateLinePropertyNames");

        // attempt to add some values to the collection that have already been added
        waitAndTypeByName("newCollectionLines['collection1_2'].field1", "13");
        waitAndTypeByName("newCollectionLines['collection1_2'].field2", "14");
        jGrowl("Click Add button.");
        waitAndClickByXpath("//section[@id = 'Demo-CollectionAddLine-Example2']//button[contains(text(), 'Add')]");

        // assert that an error message is shown
        waitForProgressAddingLine(WebDriverUtils.configuredImplicityWait() * 3);
        waitForTextPresent("Duplicate Default Add Line with duplicateLinePropertyNames property configured");
    }

    protected void testCollectionFeaturesAddLineWithDialog() throws Exception {
        selectByName("exampleShown", "Collection Add Line with Dialog");

        // open the add line dialog
        jGrowl("Click Add Line button.");
        waitAndClickByXpath("//section[@id = 'Demo-CollectionAddLine-Example3']//button[contains(text(), 'Add Line')]");

        // attempt to enter an empty value via the dialog for the required field
        waitForElementPresentByName("newCollectionLines['collection1_3'].field1");
        jGrowl("Click Add button.");
        waitAndClickByXpath("//div[@id = 'Uif-Dialogs']//button[contains(text(), 'Add')]");

        // assert that an error message is shown
        waitIsVisible("div[data-label = 'Field 1'] li[class = 'uif-errorMessageItem-field']");

        // add some values to the collection via the dialog
        waitAndTypeByName("newCollectionLines['collection1_3'].field1", "42");
        waitAndTypeByName("newCollectionLines['collection1_3'].field2", "55");
        jGrowl("Click Add button.");
        waitAndClickByXpath("//div[@id = 'Uif-Dialogs']//button[contains(text(), 'Add')]");

        // assert that the values have been added
        waitForProgressAddingLine(WebDriverUtils.configuredImplicityWait() * 3);
        assertEquals("42", waitForElementVisibleBy(By.name("collection1_3[0].field1")).getAttribute("value"));
        assertEquals("55", waitForElementVisibleBy(By.name("collection1_3[0].field2")).getAttribute("value"));
    }

    protected void testCollectionFeaturesAddBlankLine() throws Exception {
        selectByName("exampleShown", "Collection Add Blank Line");

        // click the add line button
        jGrowl("Click Add Line button.");
        waitAndClickByXpath("//section[@id = 'Demo-CollectionAddLine-Example4']//button[contains(text(), 'Add Line')]");

        // assert that the blank line has been added
        waitForElementPresent("input[name = 'collection1_7[0].field1'][value = '']");
        waitForElementPresent("input[name = 'collection1_7[0].field2'][value = '']");
    }

    protected void testCollectionFeaturesAddLineWithCustomActions() throws Exception {
        selectByName("exampleShown", "Collection Add Line with Custom Actions");

        // add some values to the collection via the dialog
        waitAndTypeByName("newCollectionLines['collection1_8'].field1", "999");
        waitAndTypeByName("newCollectionLines['collection1_8'].field2", "998");
        jGrowl("Click Add button.");
        waitAndClickByXpath("//section[@data-parent='Demo-CollectionAddLine-Example5']//button[contains(text(),'Add')]");

        // assert that the values have been added
        waitForProgressAddingLine(WebDriverUtils.configuredImplicityWait() * 3);
        assertEquals("999", waitForElementVisibleBy(By.name("collection1_8[0].field1")).getAttribute("value"));
        assertEquals("998", waitForElementVisibleBy(By.name("collection1_8[0].field2")).getAttribute("value"));

        // delete the added line from the collection
        jGrowl("Click Trash button.");
        waitAndClick("#Demo-CollectionAddLine-Example5 button.icon-trash");
        waitForProgressDeletingLine();

        // assert that the values have been deleted and that the message shows
        assertFalse("999".equals(waitForElementVisibleBy(By.name("collection1_8[0].field1")).getAttribute("value")));
        assertFalse("998".equals(waitForElementVisibleBy(By.name("collection1_8[0].field2")).getAttribute("value")));
        waitForTextPresent("You have deleted an item from Project Income.");
    }

    protected void testCollectionFeaturesAddLineWithDialogAndCustomActions() throws Exception {
        selectByName("exampleShown", "Collection Add Line with Dialog and Custom Actions");

        // add some values to the dialog and cancel
        waitAndClickByXpath("//section[@id = 'Demo-CollectionAddLine-Example6']//button/text()[contains(., 'Add Income')]/..");
        waitAndTypeByName("newCollectionLines['collection1_9'].field1", "999");
        waitAndTypeByName("newCollectionLines['collection1_9'].field2", "999");
        waitAndClickByXpath("//section[@id = 'Demo-CollectionAddLine-Example6-AddLineDialog']//button[contains(text(),'Cancel')]");

        // assert that the values have not been added to the collection
        waitForElementNotPresent(By.cssSelector("input[name = 'collection1_9[0].field1'][value = '999']"));
        waitForElementNotPresent(By.cssSelector("input[name = 'collection1_9[0].field2'][value = '999']"));

        // add some values to the collection via the dialog
        waitAndClickByXpath("//section[@id = 'Demo-CollectionAddLine-Example6']//button/text()[contains(., 'Add Income')]/..");
        waitForElementPresentByName("newCollectionLines['collection1_9'].field1");
        clearTextByName("newCollectionLines['collection1_9'].field1");
        waitAndTypeByName("newCollectionLines['collection1_9'].field1", "9996");
        waitForElementPresentByName("newCollectionLines['collection1_9'].field2");
        clearTextByName("newCollectionLines['collection1_9'].field2");
        waitAndTypeByName("newCollectionLines['collection1_9'].field2", "9996");
        jGrowl("Click Add Income button.");
        waitAndClickByXpath("//section[@id = 'Demo-CollectionAddLine-Example6-AddLineDialog']//button[contains(text(), 'Add Income')]");
        waitForProgressAddingLine(WebDriverUtils.configuredImplicityWait() * 3);

        // assert that the values have been added
        waitForElementVisibleBy(By.cssSelector("input[name = 'collection1_9[0].field1'][value = '9996']"));
        waitForElementVisibleBy(By.cssSelector("input[name = 'collection1_9[0].field2'][value = '9996']"));

        // delete the added line from the collection
        jGrowl("Click Trash button.");
        waitAndClickByXpath("//section[@id = 'Demo-CollectionAddLine-Example6']//button[contains(@class,'icon-trash')]");
        waitForProgressDeletingLine();

        // assert that the values have been deleted and that the message shows
        waitForElementNotPresent(By.cssSelector("input[name = 'collection1_9[0].field1'][value = '9996']"));
        waitForElementNotPresent(By.cssSelector("input[name = 'collection1_9[0].field2'][value = '9996']"));
        waitForTextPresent("You have deleted an item from Project Income.");
    }

    protected void testCollectionFeaturesAddRefreshingDialog() throws Exception {
        selectByName("exampleShown", "Collection Add Line with Refreshing Dialog");

        // open the add line dialog
        jGrowl("Click Add Income button.");
        waitAndClickByXpath("//section[@id = 'Demo-CollectionAddLine-Example7']//button/text()[contains(., 'Add Income')]/..");

        // assert that the button does not exist
        waitForElementNotPresent(By.xpath("//section[@id = 'Demo-CollectionAddLine-Example7-AddLineDialog']//button[contains(text(), 'Add Income')]"));

        // enter the required value and trigger the change
        waitAndTypeByName("newCollectionLines['collection1_10'].field1", "77");
        waitAndTypeByName("newCollectionLines['collection1_10'].field2", "");
        waitForProgressLoading();

        // assert that the button now exists
        waitForElementVisibleBy(By.xpath(
                "//section[@id = 'Demo-CollectionAddLine-Example7-AddLineDialog']//button[contains(text(), 'Add Income')]"));
    }

    @Test
    public void testCollectionFeaturesAddLineBookmark() throws Exception {
        testCollectionFeaturesAddLine();
        passed();
    }

    @Test
    public void testCollectionFeaturesAddLineWithDuplicatePropertyNameBookmark() throws Exception {
        testCollectionFeaturesAddLineWithDuplicatePropertyName();
        passed();
    }

    @Test
    public void testCollectionFeaturesAddLineWithDialogBookmark() throws Exception {
        testCollectionFeaturesAddLineWithDialog();
        passed();
    }

    @Test
    public void testCollectionFeaturesAddBlankLineBookmark() throws Exception {
        testCollectionFeaturesAddBlankLine();
        passed();
    }

    @Test
    public void testCollectionFeaturesAddLineWithCustomActionsBookmark() throws Exception {
        testCollectionFeaturesAddLineWithCustomActions();
        passed();
    }

    @Test
    public void testCollectionFeaturesAddLineWithDialogAndCustomActionsBookmark() throws Exception {
        testCollectionFeaturesAddLineWithDialogAndCustomActions();
        passed();
    }

    @Test
    public void testCollectionFeaturesAddRefreshingDialogBookmark() throws Exception {
        testCollectionFeaturesAddRefreshingDialog();
        passed();
    }

    @Test
    public void testCollectionFeaturesAddLineNav() throws Exception {
        testCollectionFeaturesAddLine();
        passed();
    }

    @Test
    public void testCollectionFeaturesAddLineWithDuplicatePropertyNameNav() throws Exception {
        testCollectionFeaturesAddLineWithDuplicatePropertyName();
        passed();
    }

    @Test
    public void testCollectionFeaturesAddLineWithDialogNav() throws Exception {
        testCollectionFeaturesAddLineWithDialog();
        passed();
    }

    @Test
    public void testCollectionFeaturesAddBlankLineNav() throws Exception {
        testCollectionFeaturesAddBlankLine();
        passed();
    }

    @Test
    public void testCollectionFeaturesAddLineWithCustomActionsNav() throws Exception {
        testCollectionFeaturesAddLineWithCustomActions();
        passed();
    }

    @Test
    public void testCollectionFeaturesAddLineWithDialogAndCustomActionsNav() throws Exception {
        testCollectionFeaturesAddLineWithDialogAndCustomActions();
        passed();
    }

    @Test
    public void testCollectionFeaturesAddRefreshingDialogNav() throws Exception {
        testCollectionFeaturesAddRefreshingDialog();
        passed();
    }
}
