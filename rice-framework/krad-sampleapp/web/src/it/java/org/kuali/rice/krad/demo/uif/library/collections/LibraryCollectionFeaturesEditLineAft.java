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
package org.kuali.rice.krad.demo.uif.library.collections;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.krad.demo.uif.library.LibraryBase;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LibraryCollectionFeaturesEditLineAft extends LibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-CollectionEditLineView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CollectionEditLineView";
    public static final int FIVE_SECOND_WAIT_TIME = 5;
    public static final int THREE_SECOND_WAIT_TIME = 3000;
    public static final String LIBRARY_MENU_CATEGORY_NAME = "Collection Features";
    public static final String DEMO_ITEM_NAME = "Edit Line";

    public enum SubCollectionType {
        ROW_DETAILS, SUB_COLLECTION, CUSTOM_SUB_COLLECTION, NO_SUB_COLLECTION
    }

    public static final String UIF_DIALOG_MODAL_SELECTOR = "#Uif-Dialogs .modal[aria-hidden='false']";
    public static final String ADD_BUTTON_CSS_SELECTOR =
            " .uif-collectionAddItem  .uif-collection-column-action button";
    public static final String ADD_FIELDS_CSS_SELECTOR = " .uif-collectionAddItem input:not([type='hidden'])";
    public static final String EDIT_BUTTON_CSS_SELECTOR =
            " tr:not(.uif-collectionAddItem) .uif-collection-column-action button[data-onclick^='"
                    + UifConstants.JsFunctions.SHOW_EDIT_LINE_DIALOG + "']";
    public static final String ADD_FIELDS_IN_DIALOG_CSS_SELECTOR =
            UIF_DIALOG_MODAL_SELECTOR + " .modal-body .uif-inputField input:not([type='hidden'])";
    public static final String ADD_LINE_ACTIONS_IN_DIALOG_CSS_SELECTOR =
            UIF_DIALOG_MODAL_SELECTOR + " .modal-footer button[id$='_add']";
    public static final String EDIT_DIALOG_BUTTONS_CSS_SELECTOR =
            UIF_DIALOG_MODAL_SELECTOR + " .modal-footer button";
    public static final String EDIT_DIALOG_CLOSE_BUTTON_CSS_SELECTOR =
            UIF_DIALOG_MODAL_SELECTOR + " .modal-header button.close";
    public static final String EDIT_DIALOG_INPUT_FIELDS_CSS_SELECTOR =
            UIF_DIALOG_MODAL_SELECTOR + " .modal-body input:not([type='hidden'])";
    public static final String EDIT_DIALOG_LOOKUP_CSS_SELECTOR =
            UIF_DIALOG_MODAL_SELECTOR + " .modal-body .input-group-btn > button";
    public static final String EDIT_DIALOG_CSS_SELECTOR =
            UIF_DIALOG_MODAL_SELECTOR + " .modal-body";
    public static final String ROW_DETAILS_LINK_CSS_SELECTOR =
            " table tbody tr:not(.uif-collectionAddItem) a.uif-detailsAction";
    public static final String TABLE_ROWS_CSS_SELECTOR = " table tbody tr:not(.uif-collectionAddItem)";
    public static final String SUB_COLLECTION_CSS_SELECTOR =
            " table tbody > tr:not(.uif-collectionAddItem) > td > div.uif-subCollection";

    @Before
    @Override
    public void testSetUp() {
        super.testSetUp();
        getDriver().manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
    }

    @After
    @Override
    public void tearDown() {
        getDriver().manage().timeouts().implicitlyWait(WebDriverUtils.configuredImplicityWait(), TimeUnit.SECONDS);
        super.tearDown();
    }

    /**
     * URL to the edit line demo page
     */
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * Navigation path to the edit line demo
     */
    @Override
    protected void navigate() throws Exception {
        navigateToLibraryDemo(LibraryCollectionFeaturesEditLineAft.LIBRARY_MENU_CATEGORY_NAME,
                LibraryCollectionFeaturesEditLineAft.DEMO_ITEM_NAME);
    }

    /**
     * Method to check default edit line edit.
     */
    protected void testDefaultEditLine() throws Exception {
        testAllFeatures("Demo-CollectionEditLine-Example1", 1, 1, false, false, false,
                SubCollectionType.NO_SUB_COLLECTION, -1);
    }

    /**
     * Method for custom dialog edit.
     */
    protected void testCustomDialogEditLine1() throws Exception {
        waitAndSelectByName("exampleShown", "Custom Dialog Edit Line 1");
        testAllFeatures("Demo-CollectionEditLine-Example2", 1, 1, false, false, false,
                SubCollectionType.NO_SUB_COLLECTION, -1);
    }

    /**
     * Method for custom dialog edit.
     */
    protected void testCustomDialogEditLine2() throws Exception {
        waitAndSelectByName("exampleShown", "Custom Dialog Edit Line 2");
        testAllFeatures("Demo-CollectionEditLine-Example3", 2, 1, false, false, false,
                SubCollectionType.NO_SUB_COLLECTION, -1);
    }

    /**
     * Method too check custom edit line action edit.
     */
    protected void testCustomDialogLineActionEditLine() throws Exception {
        waitAndSelectByName("exampleShown", "Custom Dialog Line Action Edit Line");
        testAllFeatures("Demo-CollectionEditLine-Example4", 2, 2, false, false, false,
                SubCollectionType.NO_SUB_COLLECTION, -1);
    }

    /**
     * Method to check custom save action edit.
     */
    protected void testCustomDialogSaveActionEditLine() throws Exception {
        waitAndSelectByName("exampleShown", "Custom Dialog Save Action Edit Line");
        testAllFeatures("Demo-CollectionEditLine-Example5", 2, 2, true, false, false,
                SubCollectionType.NO_SUB_COLLECTION, -1);
    }

    /**
     * Method to check readOnly fields edit.
     */
    protected void testReadOnlyEditLine() throws Exception {
        waitAndSelectByName("exampleShown", "ReadOnly Edit Line");
        testAllFeatures("Demo-CollectionEditLine-Example6", 2, 1, false, true, false,
                SubCollectionType.NO_SUB_COLLECTION, -1);
    }

    /**
     * Method to check the readOnly collection edit.
     */
    protected void testEditAuthorizationEditLineCollectionReadOnly() throws Exception {
        waitAndSelectByName("exampleShown", "Edit Authorization Edit Line (collection readOnly)");
        verifyCollectionRowFieldsAreNonEditable("Demo-CollectionEditLine-Example7");
    }

    /**
     * Method to checck the edit line authorization edit.
     */
    protected void testEditAuthorizationEditLineLineAuthorization() throws Exception {
        waitAndSelectByName("exampleShown", "Edit Authorization Edit Line (line authorization)");
        testAllFeatures("Demo-CollectionEditLine-Example8", 1, 1, false, false, true,
                SubCollectionType.NO_SUB_COLLECTION, -1);
    }

    /**
     * Method to check edit line's lookup from edit dialog.
     */
    protected void testLookup() throws Exception {
        waitAndSelectByName("exampleShown", "Lookup Collection Edit Line");
        testAllFeatures("Demo-CollectionEditLine-Example9", 2, 2, false, false, false,
                SubCollectionType.NO_SUB_COLLECTION, 1);
    }

    /**
     * Method to test the row details group edit.
     */
    protected void testRowDetailsGroup() throws Exception {
        waitAndSelectByName("exampleShown", "Row Details Edit Line");
        testAllFeatures("Demo-CollectionEditLine-Example10", 1, 1, false, false, false, SubCollectionType.ROW_DETAILS,
                -1);
    }

    /**
     * Method to test the row details group edit.
     */
    protected void testSubCollection() throws Exception {
        waitAndSelectByName("exampleShown", "SubCollection Edit Line");
        testAllFeatures("Demo-CollectionEditLine-Example11", 1, 1, false, false, false,
                SubCollectionType.SUB_COLLECTION, -1);
    }

    /**
     * Method to test various examples of the edit line
     */
    protected void testCollectionFeaturesEditLine() throws Exception {
        jGrowl("Calling testReadOnlyEditLine");
        testReadOnlyEditLine();
        jGrowl("Calling testEditAuthorizationEditLineCollectionReadOnly");
        testEditAuthorizationEditLineCollectionReadOnly();
        jGrowl("Calling testEditAuthorizationEditLineLineAuthorization");
        testEditAuthorizationEditLineLineAuthorization();
        jGrowl("Calling testLookup");
        testLookup();
        //        testRowDetailsGroup(); // tested separately
        //        testSubCollection(); // tested separately
    }

    /**
     * Method to test all the features for the edit line in modal.
     *
     * @param exampleId the id of the example the collection is in
     * @param lineFieldToEditIndex the index of the line field to edit
     * @param dialogFieldToEditIndex the index of the dialog field to edit
     * @param custom where it is a custom dialog
     * @param readOnly whether we are doing a readOnly
     * @param auth whether it is an authorization check
     * @param subCollectionType the type of sub-collection
     * @param lookupFieldToEditIndex the index of the lookup field
     */
    protected void testAllFeatures(String exampleId, int lineFieldToEditIndex, int dialogFieldToEditIndex,
            boolean custom, boolean readOnly, boolean auth, SubCollectionType subCollectionType,
            int lookupFieldToEditIndex) throws Exception {
        waitFor(By.cssSelector("jQuery('#" + exampleId + "')"));
        verifyCollectionRowFieldsAreNonEditable(exampleId);
        verifyAddLineAndDeleteLineStillWork(exampleId, readOnly, subCollectionType);
        verifyEditInDialogWorks(exampleId, lineFieldToEditIndex, dialogFieldToEditIndex, custom, auth,
                subCollectionType, readOnly, lookupFieldToEditIndex);
    }

    /**
     * Helper method that randomly picks a row to edit and verifies that editing the row fields work as expected.
     *
     * @param exampleId the id of the example the collection is in
     * @param lineFieldToEditIndex the index of the line field to edit
     * @param dialogFieldToEditIndex the index of the dialog field to edit
     * @param custom whether it is a custom dialog
     * @param auth whether it is an authorization check
     * @param subCollectionType the type of sub-collection
     * @param readOnly whether we are doing a readOnly
     * @param lookupFieldToEditIndex the index of the lookup field
     */
    protected void verifyEditInDialogWorks(String exampleId, int lineFieldToEditIndex, int dialogFieldToEditIndex,
            boolean custom, boolean auth, SubCollectionType subCollectionType, boolean readOnly,
            int lookupFieldToEditIndex) throws Exception {
        int rowElementsSize = 0;

        // sub-collection rows don't have edit buttons
        if (subCollectionType != SubCollectionType.SUB_COLLECTION
                && subCollectionType != SubCollectionType.CUSTOM_SUB_COLLECTION) {
            List<WebElement> rowElements = findVisibleElements(By.cssSelector(
                    "#" + exampleId + TABLE_ROWS_CSS_SELECTOR));
            rowElementsSize = rowElements.size();
        } else {
            List<WebElement> rowElements = findVisibleElements(By.cssSelector(
                    "#" + exampleId + " table:nth-child(1) tbody > tr:not(.uif-collectionAddItem)"));
            List<WebElement> subCollectionElements = findVisibleElements(By.cssSelector(
                    "#" + exampleId + SUB_COLLECTION_CSS_SELECTOR));
            rowElementsSize = rowElements.size() - subCollectionElements.size();
        }
        verifyCollectionRowsHaveEditButtons(exampleId, rowElementsSize);

        if (auth) {
            return;
        }

        // pick a random line to edit
        int index = RandomUtils.nextInt(rowElementsSize) + 1; // for 1-based index

        // open the row detils cause we want to see it all
        if (subCollectionType == SubCollectionType.ROW_DETAILS) {
            // open up the row details
            WebElement rowDetailsAchorElement = findElements(By.cssSelector(
                    "#" + exampleId + ROW_DETAILS_LINK_CSS_SELECTOR)).get(index - 1);
            rowDetailsAchorElement.click();
        }

        if (subCollectionType == SubCollectionType.NO_SUB_COLLECTION) {
            // verify dialog fields match line fields on save
            verifyDialogEdit(exampleId, lineFieldToEditIndex, dialogFieldToEditIndex, true, false, custom, index,
                    readOnly, lookupFieldToEditIndex);

            // verify dialog fields match line fields on don't save
            verifyDialogEdit(exampleId, lineFieldToEditIndex, dialogFieldToEditIndex, false, false, custom, index,
                    readOnly, lookupFieldToEditIndex);

            // verify dialog fields match line fields on close
            verifyDialogEdit(exampleId, lineFieldToEditIndex, dialogFieldToEditIndex, false, true, custom, index,
                    readOnly, lookupFieldToEditIndex);
        } else {
            // verify dialog fields and dialog sub-collection fields match those of line fields and line
            // sub-collection fields on save
            String dialogCssSelector = EDIT_DIALOG_CSS_SELECTOR;
            String tableRowsCssSelector = "#" + exampleId + TABLE_ROWS_CSS_SELECTOR;

            // these have to be processed separately since there are essential differences in each
            if (subCollectionType == SubCollectionType.ROW_DETAILS) {
                verifyRowDetails(exampleId, dialogCssSelector, tableRowsCssSelector, index);
            } else if (subCollectionType == SubCollectionType.SUB_COLLECTION) {
                verifyRowSubCollections(exampleId, dialogCssSelector, tableRowsCssSelector, index);
            }
        }
    }

    /**
     * Helper method to process and verify sub-collections in row details group.
     *
     * @param exampleId the id of the example the collection is in
     * @param dialogCssSelector the css selector of the selected row's dialog
     * @param tableRowsCssSelector the css selector of the collection's rows
     * @param index the index of the selected row
     */
    protected void verifyRowDetails(String exampleId, String dialogCssSelector, String tableRowsCssSelector, int index)
            throws Exception {
        String subCollectionFieldCssSelector =
                "#" + exampleId + " table tbody tr.detailsRow td.uif-rowDetails > div > div.uif-inputField > span";
        String dialogSubCollectionFieldCssSelector =
                dialogCssSelector + " > div > div.uif-inputField > input:not([type='hidden'])";
        String subCollectionRowsCssSelector =
                tableRowsCssSelector + " td.uif-rowDetails section " + TABLE_ROWS_CSS_SELECTOR;
        String dialogSubCollectionRowsCssSelector = dialogCssSelector + " section " + TABLE_ROWS_CSS_SELECTOR;
        String dialogSubCollectionAddLineButtonCssSelector = dialogCssSelector + " section " + ADD_BUTTON_CSS_SELECTOR;
        String dialogSubCollectionAddLineFieldsCssSelector = dialogCssSelector + " section " + ADD_FIELDS_CSS_SELECTOR;
        String dialogSubCollectionAddedLineFieldsCssSelector =
                dialogCssSelector + " section table tr:not(.uif-collectionAddItem)";
        String dialogSubCollectionDeleteLineButtonCssSelector = dialogCssSelector
                + " section table tr:not(.uif-collectionAddItem) .uif-collection-column-action button";
        int dialogSubCollectionFieldIndex = 2;

        verifySubCollections(exampleId, index, subCollectionFieldCssSelector, dialogSubCollectionFieldCssSelector,
                subCollectionRowsCssSelector, dialogSubCollectionRowsCssSelector,
                dialogSubCollectionAddLineButtonCssSelector, dialogSubCollectionAddLineFieldsCssSelector,
                dialogSubCollectionAddedLineFieldsCssSelector, dialogSubCollectionDeleteLineButtonCssSelector,
                dialogCssSelector, dialogSubCollectionFieldIndex);
    }

    /**
     * Helper method to process and verify sub-collections in row's sub-collection.
     *
     * @param exampleId the id of the example the collection is in
     * @param dialogCssSelector the css selector of the selected row's dialog
     * @param tableRowsCssSelector the css selector of the collection's rows
     * @param index the index of the selected row
     */
    protected void verifyRowSubCollections(String exampleId, String dialogCssSelector, String tableRowsCssSelector,
            int index) throws Exception {
        int dialogSubCollectionFieldIndex = 0;
        String subCollectionRowsCssSelector =
                "#" + exampleId + " table > tbody > tr:not(.uif-collectionAddItem):nth-child(" + Integer.toString(
                        index * 2) + ") > td > div.uif-subCollection section " + TABLE_ROWS_CSS_SELECTOR;
        String dialogSubCollectionRowsCssSelector =
                dialogCssSelector + " > div > div.uif-subCollection section " + TABLE_ROWS_CSS_SELECTOR;
        String dialogSubCollectionAddLineButtonCssSelector =
                dialogCssSelector + " > div > div.uif-subCollection section " + ADD_BUTTON_CSS_SELECTOR;
        String dialogSubCollectionAddLineFieldsCssSelector =
                dialogCssSelector + " > div > div.uif-subCollection section " + ADD_FIELDS_CSS_SELECTOR;
        String dialogSubCollectionAddedLineFieldsCssSelector =
                dialogCssSelector + " > div > div.uif-subCollection section table tr:not(.uif-collectionAddItem)";
        String dialogSubCollectionDeleteLineButtonCssSelector = dialogCssSelector
                + " > div > div.uif-subCollection section table tr:not(.uif-collectionAddItem) .uif-collection-column-action button";

        verifySubCollections(exampleId, index, null, null, subCollectionRowsCssSelector,
                dialogSubCollectionRowsCssSelector, dialogSubCollectionAddLineButtonCssSelector,
                dialogSubCollectionAddLineFieldsCssSelector, dialogSubCollectionAddedLineFieldsCssSelector,
                dialogSubCollectionDeleteLineButtonCssSelector, dialogCssSelector, dialogSubCollectionFieldIndex);
    }

    /**
     * Helper method to verify the action behaviors of all sub-collections wherein the location of the sub-collections
     * might be different (row details or row sub-collection) and are identified by the provided css selectors.
     *
     * @param exampleId the id of the example the collection is in
     * @param rowIndex the index of the row to edit
     * @param subCollectionFieldCssSelector the css selector to the row field with the sub-collection
     * @param dialogSubCollectionFieldCssSelector the css selector to the dialog field with the sub-collection
     * @param subCollectionRowsCssSelector the collection's sub-collection's rows css selector
     * @param dialogSubCollectionRowsCssSelector the dialog's sub-collection's rows css selector
     * @param dialogSubCollectionAddLineButtonCssSelector the css selector to the sub-collections add line button
     * @param dialogSubCollectionAddLineFieldsCssSelector the css selector to the sub-collections add line fields
     * @param dialogSubCollectionAddedLineFieldsCssSelector the css selector to the sub-collections added line fields
     * @param dialogSubCollectionDeleteLineButtonCssSelector the css selector to the sub-collections delete line button
     * @param dialogCssSelector the css selector of the selected row's dialog
     * @param dialogSubCollectionFieldIndex the index of the field in the dialog with the sub-collection
     */
    protected void verifySubCollections(String exampleId, int rowIndex, String subCollectionFieldCssSelector,
            String dialogSubCollectionFieldCssSelector, String subCollectionRowsCssSelector,
            String dialogSubCollectionRowsCssSelector, String dialogSubCollectionAddLineButtonCssSelector,
            String dialogSubCollectionAddLineFieldsCssSelector, String dialogSubCollectionAddedLineFieldsCssSelector,
            String dialogSubCollectionDeleteLineButtonCssSelector, String dialogCssSelector,
            int dialogSubCollectionFieldIndex) throws Exception {

        String subCollectionFieldValue = null;
        if (subCollectionFieldCssSelector != null) {
            // get the field the sub-collection belongs to
            WebElement subCollectionRowField = findElement(By.cssSelector(subCollectionFieldCssSelector));
            subCollectionFieldValue = subCollectionRowField.getText();
        }

        // open the edit line dialog
        openEditLineDialog(exampleId, rowIndex);

        WebElement dialogSubCollectionField = null;
        String dialogSubCollectionFieldValue = null;
        if (dialogSubCollectionFieldCssSelector != null) {
            // get the field in the dialog that the sub-collection belongs to
            List<WebElement> dialogFields = findElements(By.cssSelector(dialogSubCollectionFieldCssSelector));
            dialogSubCollectionField = dialogFields.get(dialogSubCollectionFieldIndex);
            dialogSubCollectionFieldValue = dialogSubCollectionField.getAttribute("value");
        }

        if (subCollectionFieldValue != null && dialogSubCollectionFieldValue != null) {
            assertEquals("Dialog field the sub-collection is in does not match that of the line.",
                    subCollectionFieldValue, dialogSubCollectionFieldValue);
        }

        // modify the value of the sub-collection's parent field
        if (dialogSubCollectionField != null) {
            dialogSubCollectionField.sendKeys("sub");
        }

        // get the dialog's sub-collection add line fields and type values into them
        List<WebElement> dialogSubCollectionAddLineRowFields = findElements(By.cssSelector(
                dialogSubCollectionAddLineFieldsCssSelector));
        int index = 1;
        for (WebElement element : dialogSubCollectionAddLineRowFields) {
            element.sendKeys("add" + Integer.toString(index));
            index++;
        }

        // click the add line button
        WebElement subCollectionAddLineButton = findElement(By.cssSelector(
                dialogSubCollectionAddLineButtonCssSelector));
        subCollectionAddLineButton.click();
        waitForProgress("Adding Line...", WebDriverUtils.configuredImplicityWait() * 15);
        waitFor(By.cssSelector(dialogCssSelector));

        // get the added line fields and make sure the values match the add line
        List<WebElement> dialogSubCollectionAddedLineRows = findElements(By.cssSelector(
                dialogSubCollectionAddedLineFieldsCssSelector));
        assertTrue("Dialog rows count is 0", dialogSubCollectionAddedLineRows.size() > 0);
        List<WebElement> dialogSubCollectionAddedLineRowFields = dialogSubCollectionAddedLineRows.get(0).
                findElements(By.cssSelector(" td > div.uif-inputField > input:not([type='hidden'])"));
        index = 1;
        for (WebElement element : dialogSubCollectionAddedLineRowFields) {
            assertEquals("Added line fields in the sub-collection do not match.", "add" + Integer.toString(index),
                    element.getText());
            index++;
        }

        // verify the sub-collection field and dialog sub-collection field values
        if (subCollectionFieldCssSelector != null && dialogSubCollectionFieldCssSelector != null) {
            // make sure the underlying fields are untouched
            WebElement subCollectionRowField = findElement(By.cssSelector(
                    "#" + exampleId + subCollectionFieldCssSelector));
            subCollectionFieldValue = subCollectionRowField.getText();
            List<WebElement> dialogFields = findElements(By.cssSelector(dialogSubCollectionFieldCssSelector));
            dialogSubCollectionField = dialogFields.get(dialogSubCollectionFieldIndex);
            dialogSubCollectionFieldValue = dialogSubCollectionField.getAttribute("value");
            assertEquals(
                    "Changing the dialog' sub-collection field value also changed the value of that of the collection's.",
                    subCollectionFieldValue + "sub", dialogSubCollectionFieldValue);
        }

        // make sure the collection row's sub-collection do have have the changes of the add line of the
        // sub-collection in the dialog
        List<WebElement> subCollectionRows = findElements(By.cssSelector(subCollectionRowsCssSelector));
        List<WebElement> dialogSubCollectionRows = findElements(By.cssSelector(dialogSubCollectionRowsCssSelector));
        assertEquals("Dialog sub-collection rows size after add line match those of the line.",
                subCollectionRows.size() + 1, dialogSubCollectionRows.size());

        // get the buttons in the dialog
        List<WebElement> buttonElements = findVisibleElements(By.cssSelector(EDIT_DIALOG_BUTTONS_CSS_SELECTOR));
        WebElement saveButton = buttonElements.get(0);

        // click the save button and verify changes are saved
        saveButton.click();
        waitForProgress("Editing Line...", WebDriverUtils.configuredImplicityWait() * 15);

        // verify the save was processed correctly and the sub-collection field values match with that of the dialog's
        if (subCollectionFieldCssSelector != null && dialogSubCollectionFieldCssSelector != null) {
            WebElement subCollectionRowField = findElement(By.cssSelector(
                    "#" + exampleId + subCollectionFieldCssSelector));
            subCollectionFieldValue = subCollectionRowField.getText();
            assertEquals("Saving the dialog' sub-collection field's new value did not change that of the collection's.",
                    subCollectionFieldValue, dialogSubCollectionFieldValue);
        }

        subCollectionRows = findElements(By.cssSelector(subCollectionRowsCssSelector));
        assertEquals("Dialog sub-collection rows size do not match those of the line after saving changes.",
                subCollectionRows.size(), dialogSubCollectionRows.size());

        // delete the line added to the sub-collection but hit the no button and verify no changes
        verifySubCollectionActionMakesNoChanges(exampleId, rowIndex, dialogSubCollectionDeleteLineButtonCssSelector,
                subCollectionRowsCssSelector, dialogSubCollectionRowsCssSelector, false);

        // delete the line added to the sub-collection but hit the close button and verify no changes
        verifySubCollectionActionMakesNoChanges(exampleId, rowIndex, dialogSubCollectionDeleteLineButtonCssSelector,
                subCollectionRowsCssSelector, dialogSubCollectionRowsCssSelector, true);
    }

    /**
     * Helper method to perform no/close actions of the dialog and verify that, while changes were made to the
     * dialog's sub-collection, the underlying collection's sub-collection have no changes.
     *
     * @param exampleId the id of the example the collection is in
     * @param rowIndex the index of the row to edit
     * @param dialogSubCollectionDeleteLineButtonCssSelector dialog's sub-collection's delete line button css selector
     * @param subCollectionRowsCssSelector the collection's sub-collection's rows css selector
     * @param dialogSubCollectionRowsCssSelector the dialog's sub-collection's rows css selector
     * @param close whether this is a close action
     */
    protected void verifySubCollectionActionMakesNoChanges(String exampleId, int rowIndex,
            String dialogSubCollectionDeleteLineButtonCssSelector, String subCollectionRowsCssSelector,
            String dialogSubCollectionRowsCssSelector, boolean close) throws Exception {
        // open the edit line dialog
        openEditLineDialog(exampleId, rowIndex);

        // delete the line added to the sub-collection but hit the action button and verify no changes
        WebElement subCollectionDeleteButton = waitAndGetElementsFor(By.cssSelector(
                dialogSubCollectionDeleteLineButtonCssSelector), "Could not find delete button.").get(0);
        subCollectionDeleteButton.click();
        waitForProgress("Editing Line...", WebDriverUtils.configuredImplicityWait() * 15);
        waitFor(By.cssSelector(EDIT_DIALOG_CSS_SELECTOR));

        // get the dialog's sub-collection's contents for comparing
        List<WebElement> dialogSubCollectionRows = waitAndGetElementsFor(By.cssSelector(
                dialogSubCollectionRowsCssSelector), "Could not get sub-collection rows.");

        // click either the close button or the button to not save changes
        WebElement button = null;
        if (close) {
            button = waitFor(By.cssSelector(EDIT_DIALOG_CLOSE_BUTTON_CSS_SELECTOR));
        } else {
            List<WebElement> buttons = waitAndGetElementsFor(By.cssSelector(
                    EDIT_DIALOG_BUTTONS_CSS_SELECTOR), "Could not find buttons.");
            button = buttons.get(1);
        }
        button.click();
        waitForProgressLoading();

        // get the underlying line field's sub-collection rows to compare to that of the dialog's
        List<WebElement> subCollectionRows = waitAndGetElementsFor(By.cssSelector(subCollectionRowsCssSelector),
                "Cannot see elements.");
        assertEquals("Dialog sub-collection rows size after delete line match those of the line.",
                subCollectionRows.size() - 1, dialogSubCollectionRows.size());
    }

    /**
     * Helper method that opens the dialog, edits the value, and checks results based on save or don't save.
     *
     * @param exampleId the id of the example the collection is in
     * @param lineFieldToEditIndex the index of the line field to edit
     * @param dialogFieldToEditIndex the index of the dialog field to edit
     * @param save whether this is a save action or not
     * @param close whether this is a close action
     * @param custom where it is a custom dialog
     * @param rowIndex the index of the row to edit
     * @param readOnly whether we are doing a readOnly
     * @param lookupFieldToEditIndex the index of the lookup field (this can be -1 which means there is no lookup
     * configured on any field)
     */
    protected void verifyDialogEdit(String exampleId, int lineFieldToEditIndex, int dialogFieldToEditIndex,
            boolean save, boolean close, boolean custom, int rowIndex, boolean readOnly, int lookupFieldToEditIndex)
            throws Exception {
        String tableRowsCssSelector = "#" + exampleId + TABLE_ROWS_CSS_SELECTOR;

        // get the values of the line fields in the given row
        Thread.sleep(THREE_SECOND_WAIT_TIME); // avoid cache change by going to quick
        List<WebElement> spanElements = findVisibleElements(By.cssSelector(tableRowsCssSelector)).get(rowIndex - 1).
                findElements(By.cssSelector(" div.uif-inputField > span"));
        String field1Value = spanElements.get(0).getText();
        String field2Value = spanElements.get(1).getText();
        String fieldValue = spanElements.get(lineFieldToEditIndex - 1).getText();

        // open the edit line dialog
        Thread.sleep(THREE_SECOND_WAIT_TIME); // avoid cache change by going to quick
        openEditLineDialog(exampleId, rowIndex);

        // get the original values of the input fields
        List<WebElement> inputElements = findElements(By.cssSelector(EDIT_DIALOG_INPUT_FIELDS_CSS_SELECTOR));

        if (inputElements.isEmpty()) {
            jiraAwareFail("inputElements should not be empty for example " + exampleId);
        }

        if (custom && !save) {
            fieldValue = inputElements.get(dialogFieldToEditIndex - 1).getAttribute("value");
        }

        // check if there is a lookup first
        if (lookupFieldToEditIndex >= 0) {
            WebElement lookup = waitFor(By.cssSelector(EDIT_DIALOG_LOOKUP_CSS_SELECTOR));
            lookup.click();

            // switch to the lookup dialog iframe
            WebElement lookupDialogFrame = waitFor(By.cssSelector("#Uif-DialogGroup-Lookup iframe"));
            getDriver().switchTo().frame(lookupDialogFrame);

            List<WebElement> buttons = findElements(By.cssSelector(".uif-footer button"));

            WebElement searchButton = buttons.get(2);
            searchButton.click();
            waitForProgressLoading();

            List<WebElement> searchResults = findElements(By.cssSelector(
                    ".uif-lookupPage section table > tbody > tr > td.uif-collection-column-action a"));

            // pick a random result row (except the last 2 cause they are garbage)
            int searchResultIndex = RandomUtils.nextInt(searchResults.size());

            List<WebElement> searchResultValues = findElements(By.cssSelector(
                    ".uif-lookupPage section table > tbody > tr > td.sorting_1 a"));

            WebElement searchResultPickedAnchor = searchResults.get(searchResultIndex);
            WebElement searchResultPicked = searchResultValues.get(searchResultIndex);

            String resultValue = searchResultPicked.getText();
            searchResultPickedAnchor.click();

            // switch back to the parent frame
            getDriver().switchTo().defaultContent();

            // wait for the edit line dialog to re-appear
            waitFor(By.cssSelector(EDIT_DIALOG_CSS_SELECTOR));
            inputElements = findElements(By.cssSelector(EDIT_DIALOG_INPUT_FIELDS_CSS_SELECTOR));
            verifyDialogField(inputElements.get(lookupFieldToEditIndex - 1), "", resultValue);
            if (save) {
                if (lookupFieldToEditIndex == 1) {
                    field1Value = resultValue;
                } else if (lookupFieldToEditIndex == 2) {
                    field2Value = resultValue;
                }
            }
        }

        // verify and/or edit the fields individually
        String editString = "edit";
        verifyDialogField(inputElements.get(dialogFieldToEditIndex - 1), editString, fieldValue);

        // if there are other dialog fields, then check those (this is necessary in the case where user provides the
        // list of dialog fields which is a subset of the list of line fields)
        if (!readOnly && lineFieldToEditIndex % 2 < inputElements.size() && spanElements.size() != inputElements
                .size()) {
            verifyDialogField(inputElements.get(dialogFieldToEditIndex % 2), null, spanElements.get(
                    lineFieldToEditIndex % 2).getText());
        }

        // get the buttons in the dialog
        List<WebElement> buttonElements = waitAndGetElementsFor(By.cssSelector(
                EDIT_DIALOG_BUTTONS_CSS_SELECTOR), "Could not find buttons.");
        WebElement saveButton = buttonElements.get(0);
        WebElement noButton = buttonElements.get(1);

        // if its a close, then we point the no button to the close button
        if (close) {
            noButton = findElement(By.cssSelector(EDIT_DIALOG_CLOSE_BUTTON_CSS_SELECTOR));
        }

        if (save) {
            // click the save button
            saveButton.click();
        } else {
            // click the no button
            noButton.click();
        }
        waitForProgress("Editing Line...", WebDriverUtils.configuredImplicityWait() * 15);

        // get the new values of the line fields
        spanElements = findVisibleElements(By.cssSelector(tableRowsCssSelector)).get(rowIndex - 1).
                findElements(By.cssSelector(" div.uif-inputField > span"));
        String newField1Value = spanElements.get(0).getText();
        String newField2Value = spanElements.get(1).getText();

        // verify changed values or not changed
        if (save) {
            if (lineFieldToEditIndex == 1) {
                assertEquals("Saving changes did not edit the value.", field1Value + editString, newField1Value);
                assertEquals("Saving changes did not edit the value.", field2Value, newField2Value);
            } else {
                if (custom) {
                    assertEquals("Saving changes did not edit the value.", "Custom Edit Line " + rowIndex,
                            newField1Value);
                } else {
                    assertEquals("Saving changes did not edit the value.", field1Value, newField1Value);
                }
                if (custom) {
                    assertEquals("Saving changes did not edit the value.", field2Value, newField2Value);
                } else {
                    assertEquals("Saving changes did not edit the value.", field2Value + editString, newField2Value);
                }
            }
        } else {
            assertEquals("Cancelling the edit line still edits the value.", field1Value, newField1Value);
            assertEquals("Cancelling the edit line still edits the value.", field2Value, newField2Value);
        }
    }

    /**
     * Helper method to edit a field's value.
     *
     * <p>This method has performs the following functions:<ul>
     * <li>Verifies that the values of the original dialog fields match those of the line fields.</li>
     * <li>Types the string provided, if provided, into the provided element, thereby editing the value of
     * the field.</li>
     * <li>If value of the field is edited, then the new values are checked for correctness.</li>
     * </ul></p>
     *
     * @param element the element in the dialog we are editing
     * @param randomString the string to edit with
     * @param fieldValue the value of the original field
     */
    protected void verifyDialogField(WebElement element, String randomString, String fieldValue) throws Exception {
        // get the original values of the input fields
        String inputFieldValue = element.getAttribute("value");

        // make sure the input fields in the dialog match the line fields
        assertEquals("Value of input field in dialog does not match that of the line field's value.", fieldValue,
                inputFieldValue);

        if (StringUtils.isNotEmpty(randomString)) {
            // type the random string in the given input field
            element.sendKeys(randomString);

            // get the new values after typing
            String newInputFieldValue = element.getAttribute("value");

            // the first should have the original and the random string appended, while the second should be the same
            assertEquals(inputFieldValue + randomString, newInputFieldValue);
        }
    }

    /**
     * Helper method to open the modal dialog.
     *
     * @param exampleId the id of the example the collection is in
     * @param index the index of the row
     */
    protected void openEditLineDialog(String exampleId, int index) throws Exception {
        List<WebElement> elements = findVisibleElements(By.cssSelector("#" + exampleId + EDIT_BUTTON_CSS_SELECTOR));
        elements.get(index - 1).click();

        // verify that the dialog is visible soon
        waitFor(By.cssSelector(EDIT_DIALOG_CSS_SELECTOR));
    }

    /**
     * Helper method to verify that add and delete line still work.
     *
     * <p>The line that is added is also the line that is deleted returning the contents of the list back to its
     * original state.</p>
     *
     * @param exampleId the id of the example the collection is in
     * @param readOnly whether we are doing a readOnly
     * @param subCollectionType the type of sub-collection
     */
    protected void verifyAddLineAndDeleteLineStillWork(String exampleId, boolean readOnly,
            SubCollectionType subCollectionType) throws Exception {
        if (readOnly) {
            return;
        }

        String randomString = "add";

        verifyAddLineStillWorks(exampleId, randomString, subCollectionType);
        verifyDeleteLineStillWorks(exampleId);
    }

    /**
     * Helper method to verify that adding a line in a collection works.
     *
     * @param exampleId the id of the example the collection is in
     * @param randomString the string field that is part of the line to delete
     * @param subCollectionType the type of sub-collection
     */
    protected void verifyAddLineStillWorks(String exampleId, String randomString, SubCollectionType subCollectionType)
            throws Exception {
        String addFieldsCssSelector = null;
        String addLineActionCssSelector = null;

        if (subCollectionType != SubCollectionType.NO_SUB_COLLECTION) {
            String addInDialogButtonCssSelector = "#" + exampleId + " button:first-of-type";
            WebElement addInDialogButton = findElement(By.cssSelector(addInDialogButtonCssSelector));
            jGrowl("Click add in dialog button");
            addInDialogButton.click();
            addFieldsCssSelector = ADD_FIELDS_IN_DIALOG_CSS_SELECTOR;
            addLineActionCssSelector = ADD_LINE_ACTIONS_IN_DIALOG_CSS_SELECTOR;
        } else {
            addFieldsCssSelector = "#" + exampleId + ADD_FIELDS_CSS_SELECTOR;
            addLineActionCssSelector = "#" + exampleId +  ADD_BUTTON_CSS_SELECTOR;
        }

        List<WebElement> addFields = findElements(By.cssSelector(addFieldsCssSelector));

        int index = 1;
        for (WebElement addField : addFields) {
            jGrowl("Type in " + addField.getAttribute("name") + " " + randomString + index);
            addField.sendKeys(randomString + index);
            index++;
        }

        WebElement buttonElement = findElement(By.cssSelector(addLineActionCssSelector));
        jGrowl("Click add line");
        buttonElement.click();
        waitForProgress("Adding Line...", WebDriverUtils.configuredImplicityWait() * 15);
        waitFor(By.cssSelector("#" + exampleId + " .uif-newCollectionItem"));
        List<WebElement> fields = null;

        // if its row details then the first column has the details group so we want to look at the second
        // and third columns only since there could be other input fields in row details
        if (subCollectionType == SubCollectionType.NO_SUB_COLLECTION) {
            fields = findVisibleElements(By.cssSelector("#" + exampleId +
                    " .uif-newCollectionItem .uif-inputField span"));
            assertEquals(addFields.size(), fields.size());
        } else if (subCollectionType == SubCollectionType.ROW_DETAILS) {
            fields = findVisibleElements(By.cssSelector("#" + exampleId +
                    " .uif-newCollectionItem td:nth-of-type(2) .uif-inputField span"));
            fields.addAll(findVisibleElements(By.cssSelector("#" + exampleId +
                    " .uif-newCollectionItem td:nth-of-type(3) .uif-inputField span")));
            assertEquals(addFields.size(), fields.size() + 1);
        } else {
            fields = findVisibleElements(By.cssSelector("#" + exampleId +
                    " .uif-newCollectionItem td:nth-of-type(1) .uif-inputField span"));
            fields.addAll(findVisibleElements(By.cssSelector("#" + exampleId +
                    " .uif-newCollectionItem td:nth-of-type(2) .uif-inputField span")));
            assertEquals(addFields.size(), fields.size() + 1);
        }

        index = 1;
        for (WebElement field : fields) {
            assertEquals("Added field does not match what is in the collection.", randomString + index,
                    field.getText());
            index++;
        }
    }

    /**
     * Helper method to verify that deleting a line in a collection works.
     *
     * @param exampleId the id of the example the collection is in
     */
    protected void verifyDeleteLineStillWorks(String exampleId) throws Exception {
        WebElement deleteAction = findElement(By.cssSelector(
                "#" + exampleId + " .uif-newCollectionItem .uif-collection-column-action button"));
        deleteAction.click();

        waitForProgress("Deleting Line...", WebDriverUtils.configuredImplicityWait() * 15);

        assertIsNotVisible(By.cssSelector("#" + exampleId + " .uif-newCollectionItem"));
    }

    /**
     * Helper method to make sure that all rows in a collection have a button to edit the line in a dialog (with the
     * exception of the add line row).
     *
     * @param exampleId the id of the example the collection is in
     * @param rowsSize the number of rows of the collection on the page
     */
    protected void verifyCollectionRowsHaveEditButtons(String exampleId, int rowsSize) {
        List<WebElement> buttonElements = findVisibleElements(By.cssSelector(
                "#" + exampleId + EDIT_BUTTON_CSS_SELECTOR));
        int buttonsSize = buttonElements.size();
        if (rowsSize != buttonsSize) {
            buttonsSize += 2;
        }
        assertEquals("The number of rows do not match the number of edit line buttons.", rowsSize, buttonsSize);
    }

    /**
     * Helper method to check that all row fields in a collection are not editable (with the exception of the
     * add line row).
     *
     * @param exampleId the id of the example the collection is in
     */
    protected void verifyCollectionRowFieldsAreNonEditable(String exampleId) throws Exception {
        List<WebElement> inputFields = findElements(By.cssSelector(
                "#" + exampleId + " tr:not(.uif-collectionAddItem) input:not([type='hidden'])"));
        assertTrue(inputFields.isEmpty());
    }

    @Test
    public void testDefaultEditLineBookmark() throws Exception {
        this.waitSeconds = FIVE_SECOND_WAIT_TIME;
        testDefaultEditLine();
        passed();
    }

    @Test
    public void testDefaultEditLineNav() throws Exception {
        this.waitSeconds = FIVE_SECOND_WAIT_TIME;
        testDefaultEditLine();
        passed();
    }

    @Test
    public void testCustomDialogEditLine1Bookmark() throws Exception {
        this.waitSeconds = FIVE_SECOND_WAIT_TIME;
        testCustomDialogEditLine1();
        passed();
    }

    @Test
    public void testCustomDialogEditLine1Nav() throws Exception {
        this.waitSeconds = FIVE_SECOND_WAIT_TIME;
        testCustomDialogEditLine1();
        passed();
    }

    @Test
    public void testCustomDialogEditLine2Bookmark() throws Exception {
        this.waitSeconds = FIVE_SECOND_WAIT_TIME;
        testCustomDialogEditLine2();
        passed();
    }

    @Test
    public void testCustomDialogEditLine2Nav() throws Exception {
        this.waitSeconds = FIVE_SECOND_WAIT_TIME;
        testCustomDialogEditLine2();
        passed();
    }

    @Test
    public void testCustomDialogLineActionEditLineBookmark() throws Exception {
        this.waitSeconds = FIVE_SECOND_WAIT_TIME;
        testCustomDialogLineActionEditLine();
        passed();
    }

    @Test
    public void testCustomDialogLineActionEditLineNav() throws Exception {
        this.waitSeconds = FIVE_SECOND_WAIT_TIME;
        testCustomDialogLineActionEditLine();
        passed();
    }

    @Test
    public void testCustomDialogSaveActionEditLineBookmark() throws Exception {
        this.waitSeconds = FIVE_SECOND_WAIT_TIME;
        testCustomDialogSaveActionEditLine();
        passed();
    }

    @Test
    public void testCustomDialogSaveActionEditLineNav() throws Exception {
        this.waitSeconds = FIVE_SECOND_WAIT_TIME;
        testCustomDialogSaveActionEditLine();
        passed();
    }

    @Test
    public void testCollectionFeaturesEditLineBookmark() throws Exception {
        this.waitSeconds = FIVE_SECOND_WAIT_TIME;
        testCollectionFeaturesEditLine();
        passed();
    }

    @Test
    public void testCollectionFeaturesEditLineNav() throws Exception {
        this.waitSeconds = FIVE_SECOND_WAIT_TIME;
        testCollectionFeaturesEditLine();
        passed();
    }

    @Test
    public void testRowDetailsGroupBookmark() throws Exception {
        this.waitSeconds = FIVE_SECOND_WAIT_TIME;
        testRowDetailsGroup();
        passed();
    }

    @Test
    public void testRowDetailsGroupNav() throws Exception {
        this.waitSeconds = FIVE_SECOND_WAIT_TIME;
        testRowDetailsGroup();
        passed();
    }

    @Test
    public void testSubCollectionBookmark() throws Exception {
        this.waitSeconds = FIVE_SECOND_WAIT_TIME;
        testSubCollection();
        passed();
    }

    @Test
    public void testSubCollectionNav() throws Exception {
        this.waitSeconds = FIVE_SECOND_WAIT_TIME;
        testSubCollection();
        passed();
    }
}
