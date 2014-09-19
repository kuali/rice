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

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.kuali.rice.krad.demo.uif.library.LibraryBase;
import org.kuali.rice.krad.test.KRADTestConstants;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.util.ComponentFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LibraryCollectionFeaturesEditLineAft extends LibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-CollectionEditLineView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CollectionEditLineView";
    public static final int FIVE_SECOND_WAIT_TIME = 5;
    public static final int RANDOM_STRING_LENGTH = 3;
    public static final String LIBRARY_MENU_CATEGORY_NAME = "Collection Features";
    public static final String DEMO_ITEM_NAME = "Edit Line";

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
     *
     * @throws Exception
     */
    protected void testDefaultEditLine() throws Exception {
        testAllFeatures("Demo-CollectionEditLine-Example1", 1, 1, false, false, false);
    }

    /**
     * Method for custom dialog edit.
     *
     * @throws Exception
     */
    protected void testCustomDialogEditLine1() throws Exception {
        waitAndSelectByName("exampleShown", "Custom Dialog Edit Line 1");
        testAllFeatures("Demo-CollectionEditLine-Example2", 1, 1, false, false, false);
    }

    /**
     * Method for custom dialog edit.
     *
     * @throws Exception
     */
    protected void testCustomDialogEditLine2() throws Exception {
        waitAndSelectByName("exampleShown", "Custom Dialog Edit Line 2");
        testAllFeatures("Demo-CollectionEditLine-Example3", 2, 1, false, false, false);
    }

    /**
     * Method too check custom edit line action edit.
     *
     * @throws Exception
     */
    protected void testCustomDialogLineActionEditLine() throws Exception {
        waitAndSelectByName("exampleShown", "Custom Dialog Line Action Edit Line");
        testAllFeatures("Demo-CollectionEditLine-Example4", 2, 2, false, false, false);
    }

    /**
     * Method to check custom save action edit.
     *
     * @throws Exception
     */
    protected void testCustomDialogSaveActionEditLine() throws Exception {
        waitAndSelectByName("exampleShown", "Custom Dialog Save Action Edit Line");
        testAllFeatures("Demo-CollectionEditLine-Example5", 2, 2, true, false, false);
    }

    /**
     * Method to check readOnly fields edit.
     *
     * @throws Exception
     */
    protected void testReadOnlyEditLine() throws Exception {
        waitAndSelectByName("exampleShown", "ReadOnly Edit Line");
        testAllFeatures("Demo-CollectionEditLine-Example6", 2, 1, false, true, false);
    }

    /**
     * Method to check the readOnly collection edit.
     *
     * @throws Exception
     */
    protected void testEditAuthorizationEditLineCollectionReadOnly() throws Exception {
        waitAndSelectByName("exampleShown", "Edit Authorization Edit Line (collection readOnly)");
        verifyCollectionRowFieldsAreNonEditable("Demo-CollectionEditLine-Example7");
    }

    /**
     * Method to checck the edit line authorization edit.
     *
     * @throws Exception
     */
    protected void testEditAuthorizationEditLineLineAuthorization() throws Exception {
        waitAndSelectByName("exampleShown", "Edit Authorization Edit Line (line authorization)");
        testAllFeatures("Demo-CollectionEditLine-Example8", 1, 1, false, false, true);
    }

    /**
     * Method to test the row details group edit.
     *
     * @throws Exception
     */
    protected void testRowDetailsGroup() throws Exception {
        waitAndSelectByName("exampleShown", "Row Details Edit Line");
        testAllFeatures("Demo-CollectionEditLine-Example10", 1, 1, false, false, false);
    }

    /**
     * Method to test all the examples of the edit line.
     *
     * @throws Exception
     */
    protected void testCollectionFeaturesEditLine() throws Exception {
        this.waitSeconds = FIVE_SECOND_WAIT_TIME;
        testDefaultEditLine();
        testCustomDialogEditLine1();
        testCustomDialogEditLine2();
        testCustomDialogLineActionEditLine();
        testCustomDialogSaveActionEditLine();
        testReadOnlyEditLine();
        testEditAuthorizationEditLineCollectionReadOnly();
        testEditAuthorizationEditLineLineAuthorization();
        testRowDetailsGroup();
    }

    /**
     * Method to test all the features for the edit line in modal.
     *
     * @param exampleId the id of the example the collection is in
     * @param lineIndex the index of the line field to edit
     * @param dialogIndex the index of the dialog field to edit
     * @param custom where it is a custom dialog
     * @param readOnly whether we are doing a readOnly
     * @param auth whether it is an authorization check
     * @throws Exception
     */
    protected void testAllFeatures(String exampleId, int lineIndex, int dialogIndex, boolean custom, boolean readOnly,
            boolean auth) throws Exception {
        verifyCollectionRowFieldsAreNonEditable(exampleId);
                if(!readOnly) {
                    verifyAddLineAndDeleteLineStillWork(exampleId);
                }
        verifyEditInDialogWorks(exampleId, lineIndex, dialogIndex, custom, auth);
    }

    /**
     * Helper method that randomly picks a row to edit and verifies that editing the row fields work as expected.
     *
     * @param exampleId the id of the example the collection is in
     * @param lineIndex the index of the line field to edit
     * @param dialogIndex the index of the dialog field to edit
     * @param custom whether it is a custom dialog
     * @param auth whether it is an authorization check
     * @throws Exception
     */
    protected void verifyEditInDialogWorks(String exampleId, int lineIndex, int dialogIndex, boolean custom,
            boolean auth) throws Exception {
        String buttonXPath = verifyCollectionRowsHaveEditButtons(exampleId);

        if (auth) {
            return;
        }

        // pick a random line to edit
        List<WebElement> webElements = waitAndGetElementsFor(By.xpath(buttonXPath), "");
        int index = RandomUtils.nextInt(webElements.size()) + 1; // for 1-based index
        String collectionRowsXPath = getCollectionRowsXPath(exampleId, false);
        String fieldGroupXPath = collectionRowsXPath +
                "[" +
                Integer.toString(index) +
                "]" +
                "/td[4]/div[contains(@class, '" +
                KRADTestConstants.CssClassNames.HORIZONTAL_FIELD_GROUP +
                "')]";
        String fieldXPath = collectionRowsXPath +
                "[" +
                Integer.toString(index) +
                "]" +
                "/td/div[contains(@class, '" +
                KRADTestConstants.CssClassNames.INPUT_FIELD +
                "')]/span";
        String editDialogXPath = fieldGroupXPath +
                "//section[contains(@id, '" +
                ComponentFactory.EDIT_LINE_DIALOG +
                "')]";

        // verify dialog fields match line fields on save
        verifyDialogEdit(fieldGroupXPath, fieldXPath, lineIndex, editDialogXPath, dialogIndex, true, false, custom, index);

        // verify dialog fields match line fields on don't save
        verifyDialogEdit(fieldGroupXPath, fieldXPath, lineIndex, editDialogXPath, dialogIndex, false, false, custom, index);

        // verify dialog fields match line fields on close
        verifyDialogEdit(fieldGroupXPath, fieldXPath, lineIndex, editDialogXPath, dialogIndex, false, true, custom,
                index);
    }

    /**
     * Helper method that opens the dialog, edits the value, and checks results based on save or don't save.
     *
     * @param fieldGroupXPath the xpath of the field group the modal is housed in
     * @param fieldXPath the xpath of the field to edit
     * @param lineIndex the index of the line field to edit
     * @param editDialogXPath the xpath of the dialog
     * @param dialogIndex the index of the dialog field to edit
     * @param save whether this is a save action or not
     * @param close whether this is a close action
     * @param custom where it is a custom dialog
     * @param rowIndex the index of the row to edit
     * @throws Exception
     */
    protected void verifyDialogEdit(String fieldGroupXPath, String fieldXPath, int lineIndex, String editDialogXPath,
            int dialogIndex, boolean save, boolean close, boolean custom, int rowIndex) throws Exception {
        // get the original values of the line fields
        List<WebElement> elements = findVisibleElements(By.xpath(fieldXPath));
        String field1Value = elements.get(0).getText();
        String field2Value = elements.get(1).getText();
        String fieldValue = elements.get(lineIndex - 1).getText();

        // open the edit line dialog
        openEditLineDialog(fieldGroupXPath, editDialogXPath);

        // get the xpath to the input fields in the dialog since we will edit that one
        String inputFieldXPath = editDialogXPath +
                "//div[contains(@class, '" +
                KRADTestConstants.CssClassNames.MODAL_DIALOG_BODY +
                "')]//input";

        String randomString = RandomStringUtils.random(RANDOM_STRING_LENGTH);

        // get the original values of the input fields
        List<WebElement> inputElements = waitAndGetElementsFor(By.xpath(inputFieldXPath), "");
        if (custom && !save) {
            fieldValue = inputElements.get(dialogIndex - 1).getAttribute("value");
        }
        processDialogField(inputElements.get(dialogIndex - 1), randomString, fieldValue);

        // get the xpath to the buttons in the dialog
        String buttonsXPath = editDialogXPath +
                "//div[contains(@class, '" +
                KRADTestConstants.CssClassNames.MODAL_DIALOG_FOOTER +
                "')]//button";
        String button1XPath = buttonsXPath + "[1]";
        String button2XPath = buttonsXPath + "[2]";

        // if its a close, then we point button 2 to the close button
        if (close) {
            button2XPath = editDialogXPath +
                    "//header[contains(@class, '" +
                    KRADTestConstants.CssClassNames.MODAL_DIALOG_HEADER +
                    "')]//button";
        }

        if (save) {
            // click the save button
            waitAndClickByXpath(button1XPath);
        } else {
            // click the no button
            waitAndClickByXpath(button2XPath);
        }
        waitForProgress("Editing Line...");

        // get the new values of the line fields
        elements = findVisibleElements(By.xpath(fieldXPath));
        String newField1Value = elements.get(0).getText();
        String newField2Value = elements.get(1).getText();

        // verify changed values or not changed
        if (save) {
            if (lineIndex == 1) {
                assertEquals("Saving changes did not edit the value.", field1Value + randomString, newField1Value);
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
                    assertEquals("Saving changes did not edit the value.", field2Value + randomString, newField2Value);
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
     * @param element the element in the dialog we are editing
     * @param randomString the string to edit with
     * @param fieldValue the value of the original field
     * @throws Exception
     */
    protected void processDialogField(WebElement element, String randomString, String fieldValue) throws Exception {
        // get the original values of the input fields
        String inputFieldValue = element.getAttribute("value");

        // make sure the input fields in the dialog match the line fields
        assertEquals("Value of input field in dialog does not match that of the line field's value.", fieldValue,
                inputFieldValue);

        // type the random string in the first input field
        element.sendKeys(randomString);

        // get the new values after typing
        String newInputFieldValue = element.getAttribute("value");

        // the first should have the original and the random string appended, while the second should be the same
        assertEquals(inputFieldValue + randomString, newInputFieldValue);
    }

    /**
     * Helper method to open the modal dialog.
     *
     * @param fieldGroupXPath the xpath field group where the modal is housed
     * @param editDialogXPath the xpath of the dialog
     * @throws Exception
     */
    protected void openEditLineDialog(String fieldGroupXPath, String editDialogXPath) throws Exception {
        // update the button xpath to point to the chosen line's button
        String buttonXPath = fieldGroupXPath +
                "//button[contains(@data-onclick, '" +
                UifConstants.JsFunctions.SHOW_EDIT_LINE_DIALOG +
                "')]";

        waitAndClickByXpath(buttonXPath);
        waitForElementPresentByXpath(editDialogXPath);
    }

    /**
     * Helper method to verify that add and delete line still work.
     *
     * <p>The line that is added is also the line that is deleted returning the contents of the list back to its
     * original state.</p>
     *
     * @param exampleId the id of the example the collection is in
     */
    protected void verifyAddLineAndDeleteLineStillWork(String exampleId) throws Exception {
        String randomString1 = RandomStringUtils.random(RANDOM_STRING_LENGTH);
        String randomString2 = RandomStringUtils.random(RANDOM_STRING_LENGTH);
        String addedField1Xpath = getCollectionRowsDivByClassNameXPath(exampleId, 2,
                KRADTestConstants.CssClassNames.INPUT_FIELD, false) +
                "/span[contains(./text(), '" +
                randomString1 +
                "')]";
        String addedField2Xpath = getCollectionRowsDivByClassNameXPath(exampleId, 3,
                KRADTestConstants.CssClassNames.INPUT_FIELD, false) +
                "/span[contains(./text(), '" +
                randomString2 +
                "')]";

        verifyAddLineStillWorks(exampleId, randomString1, randomString2, addedField1Xpath, addedField2Xpath);
        verifyDeleteLineStillWorks(exampleId, randomString1, randomString2, addedField1Xpath, addedField2Xpath);
    }

    /**
     * Helper method to verify that adding a line in a collection works.
     *
     * @param exampleId the id of the example the collection is in
     * @param randomString1 the string field that is part of the line to delete
     * @param randomString2 the other string field that is part of the line to delete
     * @param field1XPath the xpath of the string field
     * @param field2XPath the xpath of the other string field
     */
    protected void verifyAddLineStillWorks(String exampleId, String randomString1, String randomString2,
            String field1XPath, String field2XPath) throws Exception {
        String addInputField1XPath = verifyCollectionRowFieldIsEditable(exampleId, 2, true, true);
        waitFor(By.xpath(addInputField1XPath));
        waitAndTypeByXpath(addInputField1XPath, randomString1);
        String addInputField2XPath = verifyCollectionRowFieldIsEditable(exampleId, 3, true, true);
        waitFor(By.xpath(addInputField2XPath));
        waitAndTypeByXpath(addInputField2XPath, randomString2);

        String addLineActionXPath = getCollectionRowsDivByClassNameXPath(exampleId, 4,
                KRADTestConstants.CssClassNames.HORIZONTAL_FIELD_GROUP, true) +
                "//button[contains(@id, '" +
                UifConstants.IdSuffixes.ADD_LINE +
                "')]";

        assertIsVisibleByXpath(addLineActionXPath, "Add line is missing add button.");
        waitAndClickByXpath(addLineActionXPath);
        waitForProgressAddingLine();
        waitForTextPresent(randomString1);
        assertIsVisibleByXpath(field1XPath, "Added field is missing.");
        waitForTextPresent(randomString2);
        assertIsVisibleByXpath(field2XPath, "Added field is missing.");
    }

    /**
     * Helper method to verify that deleting a line in a collection works.
     *
     * @param exampleId the id of the example the collection is in
     * @param randomString1 the string field that is part of the line to delete
     * @param randomString2 the other string field that is part of the line to delete
     * @param field1XPath the xpath of the string field
     * @param field2XPath the xpath of the other string field
     */
    protected void verifyDeleteLineStillWorks(String exampleId, String randomString1, String randomString2,
            String field1XPath, String field2XPath) throws Exception {

        String deleteLineActionXPath = getCollectionRowsDivByClassNameXPath(exampleId, 4,
                KRADTestConstants.CssClassNames.HORIZONTAL_FIELD_GROUP, false) +
                "//button[contains(@id, '" +
                UifConstants.IdSuffixes.DELETE_LINE +
                "')]";

        assertIsVisibleByXpath(deleteLineActionXPath, "Line is missing delete button.");
        waitAndClickByXpath(deleteLineActionXPath);
        waitForProgress("Deleting Line...");
        waitForTextNotPresent(randomString1);
        waitForTextNotPresent(randomString2);
        assertIsNotVisibleByXpath(field1XPath, "Deleted field is still there.");
        assertIsNotVisibleByXpath(field2XPath, "Deleted field is still there.");
    }

    /**
     * Helper method to make sure that all rows in a collection have a button to edit the line in a dialog (with the
     * exception of the add line row).
     *
     * @return the xpath to the edit in dialog button
     */
    protected String verifyCollectionRowsHaveEditButtons(String exampleId) {
        String fieldGroupXPath = getCollectionRowsDivByClassNameXPath(exampleId, 4,
                KRADTestConstants.CssClassNames.HORIZONTAL_FIELD_GROUP, false);
        String fieldGroupDivXPath = fieldGroupXPath +
                "//button[contains(@data-onclick, '" +
                UifConstants.JsFunctions.SHOW_EDIT_LINE_DIALOG +
                "')]";
        //assertIsVisibleByXpath(fieldGroupXPath, "Row to edit is missing edit button.");
        //assertIsVisibleByXpath(fieldGroupDivXPath, "Row to edit is missing edit button.");
        List<WebElement> elements = findVisibleElements(By.xpath(fieldGroupXPath));
        List<WebElement> buttonElements = findVisibleElements(By.xpath(fieldGroupDivXPath));
        int sizeElements = elements.size();
        int sizeButtons = buttonElements.size();
        if (elements.size() != buttonElements.size()) {
            sizeButtons += 2;
        }
        assertEquals(sizeElements, sizeButtons);
        return fieldGroupDivXPath;
    }

    /**
     * Helper method to check that all row fields in a collection are not editable (with the exception of the
     * add line row).
     *
     * @param exampleId the id of the example the collection is in
     */
    protected void verifyCollectionRowFieldsAreNonEditable(String exampleId) throws Exception {
        verifyCollectionRowFieldIsEditable(exampleId, 2, false, false);
        verifyCollectionRowFieldIsEditable(exampleId, 3, false, false);
    }

    /**
     * Helper method to check that a row field in a collection is editable or not.
     *
     * @param exampleId the id of the example the collection is in
     * @param index the index of the row field
     * @param editable whether the field should be editable or not
     * @param isAddLine whether we are looking at an add line field
     * @return the xpath of the field
     */
    protected String verifyCollectionRowFieldIsEditable(String exampleId, int index, boolean editable,
            boolean isAddLine) throws Exception {
        String inputFieldsDivXPath = getCollectionRowsDivByClassNameXPath(exampleId, index,
                KRADTestConstants.CssClassNames.INPUT_FIELD, isAddLine) + "/input";
        waitFor(By.xpath(inputFieldsDivXPath));
        if (editable) {
            assertIsVisibleByXpath(inputFieldsDivXPath, "Row to edit's input field is not an input.");
        } else {
            assertIsNotVisibleByXpath(inputFieldsDivXPath, "Row to edit's input field is an input.");
        }
        return inputFieldsDivXPath;
    }

    /**
     * Helper method to form the xpath leading to the collection row's field div with the given class name.
     *
     * @param exampleId the id of the example that the collection is in
     * @param index the index of the row field
     * @param className the class name of the div we want
     * @param isAddLine whether the row is an add line row
     * @return the xpath
     */
    private String getCollectionRowsDivByClassNameXPath(String exampleId, int index, String className,
            boolean isAddLine) {
        Map<String, String> dataAttributes = new HashMap<String, String>();
        dataAttributes.put(KRADTestConstants.DataAttributesConstants.PARENT, exampleId);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getCollectionRowsXPath(exampleId, isAddLine));
        if (isAddLine) {
            stringBuilder.append("/td[");
            stringBuilder.append(index);
            stringBuilder.append("]/div[contains(@class, '");
            stringBuilder.append(className);
            stringBuilder.append("')]");
        } else {
            stringBuilder.append("/td[");
            stringBuilder.append(index);
            stringBuilder.append("]/div[contains(@class, '");
            stringBuilder.append(className);
            stringBuilder.append("')]");
        }
        return stringBuilder.toString();
    }

    /**
     * Helper method to form the xpath leading to the collection rows.
     *
     * @param exampleId the id of the example that the collection is in
     * @param isAddLine whether the row is an add line row
     * @return the xpath
     */
    private String getCollectionRowsXPath(String exampleId, boolean isAddLine) {
        Map<String, String> dataAttributes = new HashMap<String, String>();
        dataAttributes.put(KRADTestConstants.DataAttributesConstants.PARENT, exampleId);

        StringBuilder stringBuilder = new StringBuilder();
        if (isAddLine) {
            stringBuilder.append(getExampleXPath(dataAttributes));
            stringBuilder.append("//table[1]/tbody/tr[contains(@class, '");
            stringBuilder.append(KRADTestConstants.CssClassNames.ADD_LINE_ROW);
            stringBuilder.append("')]");
        } else {
            stringBuilder.append(getExampleXPath(dataAttributes));
            stringBuilder.append("//table[1]/tbody/tr[not(contains(@class, '");
            stringBuilder.append(KRADTestConstants.CssClassNames.ADD_LINE_ROW);
            stringBuilder.append("'))]");
        }
        return stringBuilder.toString();
    }

    /**
     * Helper method to get the xpath for an example within the edit line demo.
     *
     * @param dataAttributes the data attributes to apply when looking for the example
     * @return the xpath to the example
     */
    private String getExampleXPath(Map<String, String> dataAttributes) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("//div");

        if (dataAttributes == null || dataAttributes.isEmpty()) {
            return stringBuilder.toString();
        }

        stringBuilder.append("[");
        Iterator<String> keysIterator = dataAttributes.keySet().iterator();
        while (keysIterator.hasNext()) {
            String key = keysIterator.next();
            String value = dataAttributes.get(key);
            stringBuilder.append("@data-");
            stringBuilder.append(key);
            stringBuilder.append("='");
            stringBuilder.append(value);
            stringBuilder.append("'");

            if (keysIterator.hasNext()) {
                stringBuilder.append(" and ");
            } else {
                stringBuilder.append("]");
            }
        }
        return stringBuilder.toString();
    }

    @Test
    public void testCollectionFeaturesEditLineBookmark() throws Exception {
        testCollectionFeaturesEditLine();
        passed();
    }

    @Test
    public void testCollectionFeaturesEditLineNav() throws Exception {
        testCollectionFeaturesEditLine();
        passed();
    }
}
