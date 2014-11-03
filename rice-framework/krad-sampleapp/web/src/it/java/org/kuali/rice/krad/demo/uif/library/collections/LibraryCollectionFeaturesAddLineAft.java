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
package org.kuali.rice.krad.demo.uif.library.collections;

import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
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
        waitForElementPresent("input[name = 'collection1_3[0].field1'][value = '42']");
        waitForElementPresent("input[name = 'collection1_3[0].field2'][value = '55']");
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
        selectByName("exampleShown","Collection Add Line with Custom Actions");

        // Add Income Verify
        waitAndTypeByName("newCollectionLines['collection1_8'].field1","999");
        waitAndTypeByName("newCollectionLines['collection1_8'].field2","999");
        waitAndClickByXpath("//section[@data-parent='Demo-CollectionAddLine-Example5']/div/table/tbody/tr/td[4]/div/fieldset/div/button[contains(text(),'Add')]");
        waitForElementPresentByXpath("//input[@value='999']");

        //Trash Check
        waitAndClickByXpath("//button[@class='btn btn-default btn-xs uif-action uif-boxLayoutHorizontalItem icon-trash']");
        waitForElementNotPresent(By.xpath("//input[@value='999']"));
        waitForTextPresent("You have deleted an item from Project Income.");
    }

    protected void testCollectionFeaturesAddLineWithDialogAndCustomActions() throws Exception {
        selectByName("exampleShown","Collection Add Line with Dialog and Custom Actions");

        // Add Income Cancel
        waitAndClickByXpath("//section[@data-parent='Demo-CollectionAddLine-Example6']/button/span[@class='icon-plus']");
        waitAndTypeByName("newCollectionLines['collection1_8'].field3","999");
        waitAndTypeByName("newCollectionLines['collection1_8'].field1","999");
        waitAndTypeByName("newCollectionLines['collection1_8'].field2","999");
        waitAndClickByXpath("//section[@style='display: block;']/div/div/div[@class='modal-footer']/button[contains(text(),'Cancel')]");
        waitForElementNotPresent(By.xpath("//section[@style='display: block;']"));
        waitForElementNotPresent(By.xpath("//input[@value='999']"));

        //Add Income and Verify
        waitAndClickByXpath("//section[@data-parent='Demo-CollectionAddLine-Example6']/button/span[@class='icon-plus']");
        waitAndTypeByName("newCollectionLines['collection1_8'].field3","999");
        waitAndTypeByName("newCollectionLines['collection1_8'].field1","999");
        waitAndTypeByName("newCollectionLines['collection1_8'].field2","999");
        waitAndClickByXpath("//section[@style='display: block;']/div/div/div[@class='modal-footer']/button[contains(text(),'Cancel')]");
        waitForElementPresentByXpath("//input[@value='999']");

        //Trash Check
        waitAndClickByXpath("//button[@class='btn btn-default btn-xs uif-action uif-boxLayoutHorizontalItem icon-trash']");
        waitForElementNotPresent(By.xpath("//input[@value='999']"));
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
        waitForElementPresent(By.xpath("//section[@id = 'Demo-CollectionAddLine-Example7-AddLineDialog']//button[contains(text(), 'Add Income')]"));
    }

    @Test
    public void testCollectionFeaturesAddLineBookmark() throws Exception {
        testCollectionFeaturesAddLine();
        testCollectionFeaturesAddLineWithDuplicatePropertyName();
        testCollectionFeaturesAddLineWithDialog();
        testCollectionFeaturesAddBlankLine();
        testCollectionFeaturesAddLineWithCustomActions();
        testCollectionFeaturesAddLineWithDialogAndCustomActions();
        testCollectionFeaturesAddRefreshingDialog();
        passed();
    }

    @Test
    public void testCollectionFeaturesAddLineNav() throws Exception {
        testCollectionFeaturesAddLine();
        testCollectionFeaturesAddLineWithDuplicatePropertyName();
        testCollectionFeaturesAddLineWithDialog();
        testCollectionFeaturesAddBlankLine();
        testCollectionFeaturesAddLineWithCustomActions();
        testCollectionFeaturesAddLineWithDialogAndCustomActions();
        testCollectionFeaturesAddRefreshingDialog();
        passed();
    }

}
