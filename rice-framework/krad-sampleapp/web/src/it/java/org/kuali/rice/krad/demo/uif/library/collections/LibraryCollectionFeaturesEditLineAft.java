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

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LibraryCollectionFeaturesEditLineAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-CollectionEditLineView
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-CollectionEditLineView";
    public static final int FIVE_SECOND_WAIT_TIME = 5;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Collection Features");
        waitAndClickByLinkText("Edit Line");
    }
    
    protected void testDefaultEditLine() throws Exception {
    	testAllFeatures("Demo-CollectionEditLine-Example1", "collection1[0].field1", "collection1[0].field2");
    }
    
    protected void testCustomDialogEditLine1() throws Exception {
    	waitAndSelectByName("exampleShown", "Custom Dialog Edit Line 1");
    	testAllFeatures("Demo-CollectionEditLine-Example2", "collection2[0].field1", null);
    }
    
    protected void testCustomDialogEditLine2() throws Exception {
    	waitAndSelectByName("exampleShown", "Custom Dialog Edit Line 2");
    	testAllFeatures("Demo-CollectionEditLine-Example3","collection3[0].field3",null);
    }
    
    protected void testCustomDialogLineActionEditLine() throws Exception {
    	waitAndSelectByName("exampleShown", "Custom Dialog Line Action Edit Line");
    	testAllFeatures("Demo-CollectionEditLine-Example4","collection1_4[0].field1","collection1_4[0].field2");
    }
    
    protected void testCustomDialogSaveActionEditLine() throws Exception {
    	waitAndSelectByName("exampleShown", "Custom Dialog Save Action Edit Line");
    	testCustomDialogSaveActionEditLineFeatures("Demo-CollectionEditLine-Example5", "collection6[0].field1",
                "collection6[0].field2");
    }
    
    protected void testReadOnlyEditLine() throws Exception {
    	waitAndSelectByName("exampleShown", "ReadOnly Edit Line");
//    	testAllFeatures("Demo-CollectionEditLine-Example6",null,null);
    }
    
    protected void testEditAuthorizationEditLineCollectionReadOnly() throws Exception {
    	waitAndSelectByName("exampleShown", "Edit Authorization Edit Line (collection readOnly)");
     	testEditAuthorizationEditLineCollectionReadOnlyFeatures("Demo-CollectionEditLine-Example7");
    }

    protected void testEditAuthorizationEditLineLineAuthorization() throws Exception {
    	waitAndSelectByName("exampleShown", "Edit Authorization Edit Line (line authorization)");
   	    testEditAuthorizationEditLineLineAuthorizationFeatures("Demo-CollectionEditLine-Example8");
    }
    
    protected void testLookupCollectionEditLine() throws Exception {
    	waitAndSelectByName("exampleShown", "Lookup Collection Edit Line");
    	testLookupCollectionEditLineFeatures("Demo-CollectionEditLine-Example9");
    }

    protected void testCollectionFeaturesEditLine() throws Exception {
        testDefaultEditLine();
        testCustomDialogEditLine1();
        testCustomDialogEditLine2();
        testCustomDialogLineActionEditLine();
        testCustomDialogSaveActionEditLine();
        testReadOnlyEditLine();
        testEditAuthorizationEditLineCollectionReadOnly();
        testEditAuthorizationEditLineLineAuthorization();
        testLookupCollectionEditLine();
    }

    protected void testAllFeatures(String sectionDataParent, String firstFieldToEdit, String secondFieldToEdit)
            throws Exception {

        //Generate random four digit numbers
        String randomNumberString = generate4DigitRandomNumber();
        String randomNumberString2 = generate4DigitRandomNumber();
        String randomNumberString3 = generate4DigitRandomNumber();

        addAndVerify(sectionDataParent, randomNumberString);

        editAndSave(sectionDataParent, randomNumberString2, firstFieldToEdit, secondFieldToEdit);

        if (firstFieldToEdit != null && secondFieldToEdit != null) {
            jGrowl("Since we could edit both fields, verify " + randomNumberString + " is not present");
            waitForTextNotPresent(randomNumberString);
        }
        waitForTextPresent(randomNumberString2);

        editAndCancel(sectionDataParent, randomNumberString3, firstFieldToEdit, secondFieldToEdit);

        if (firstFieldToEdit != null && secondFieldToEdit != null) {
            jGrowl("Since we could edit both fields, verify " + randomNumberString + " is not present");
            waitForTextNotPresent(randomNumberString);
        }

        waitForTextPresent(randomNumberString2);
    }

    protected void testCustomDialogSaveActionEditLineFeatures(String sectionDataParent, String firstFieldToEdit,
            String secondFieldToEdit) throws Exception {
        //Generate random four digit numbers
        String randomNumberString = generate4DigitRandomNumber();
        String randomNumberString2 = generate4DigitRandomNumber();
        String randomNumberString3 = generate4DigitRandomNumber();

        addAndVerify(sectionDataParent, randomNumberString);

        editAndCancel(sectionDataParent, randomNumberString3, firstFieldToEdit, secondFieldToEdit);

        waitForTextNotPresent("Custom Edit Line 1");
        waitForTextNotPresent("Custom Edit Line 2");
        waitForTextNotPresent("Custom Edit Line 3");

        editAndSave(sectionDataParent, randomNumberString2, firstFieldToEdit, secondFieldToEdit);

        waitForTextNotPresent(randomNumberString2);
        waitForTextPresent("Custom Edit Line 1");
        waitForTextPresent("Custom Edit Line 2");
        waitForTextPresent("Custom Edit Line 3");
    }

    protected void testEditAuthorizationEditLineCollectionReadOnlyFeatures(String sectionDataParent) throws Exception {
        if (isElementPresentByXpath("//div[@data-parent='" + sectionDataParent + "']//div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/button[1]")) {
            jiraAwareFail("No 'Delete' or 'Edit in Dialog' buttons should be present" );
        }

        if (isElementPresentByXpath("//div[@data-parent='" + sectionDataParent + "']/div/div/table/tbody/tr/td[4]/div/fieldset/div/button")) {
            jiraAwareFail("No 'Add' button should be present" );
        }

        if (isElementPresentByXpath("/div[@data-parent='" + sectionDataParent + "']//div/div/table/tbody/tr/td[2]/div/input")) {
            jiraAwareFail("There should be no input field." );
        }
    }

    protected void testEditAuthorizationEditLineLineAuthorizationFeatures(String sectionDataParent) throws Exception {
        //Generate random four digit number
        String randomNumberString = generate4DigitRandomNumber();

        if (isElementPresentByXpath("//div[@data-parent='" + sectionDataParent + "']//div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/button[2]")) {
            jiraAwareFail("The 'Edit in Dialog' button should not be present for the first row." );
        }

        if (isElementPresentByXpath("//div[@data-parent='" + sectionDataParent + "']//div/div/table/tbody/tr[6]/td[4]/div/fieldset/div/button[2]")) {
            jiraAwareFail("The 'Edit in Dialog' button should not be present for the fifth row." );
        }

        addAndVerify(sectionDataParent, randomNumberString);
    }

    protected void testLookupCollectionEditLineFeatures(String sectionDataParent) throws Exception {
        String randomNumberString = generate4DigitRandomNumber();

        // edit existing row
        waitAndClickByXpath("//div[@data-parent='" + sectionDataParent + "']/div/table/tbody/tr[2]/td[4]/div/fieldset/div/button[2]");
        waitAndClickByXpath("//button[@class='btn btn-default uif-action icon-search']");
        gotoLightBox();
        waitAndClickButtonByText("Search");
        waitAndClickByLinkText("return value");

        //  THIS IS BROKEN.   The lookup is returning to the main page instead of the edit dialog.  It saves the change
        // but the main page does not get updated.  It should probably update on the edit dialog and allow the
        // person to hit the 'save changes' button
        // Once KULRICE-13246 is fixed the following lines can be uncommented and may require some changes.
        // waitForTextPresent("a1");
        // END of broken section

        // add new row using lookup and then delete it
        if (sectionDataParent != null) {
            waitAndClickByXpath("//button[@class='btn btn-default uif-action icon-search']");
            waitForProgressLoading();
            gotoLightBox();
            waitAndClickButtonByText("Search");
            waitAndClickByLinkText("return value");
            waitAndTypeByXpath("//div[@data-parent='" + sectionDataParent + "']/div/table/tbody/tr/td[3]/div/input", randomNumberString);
            waitAndClickByXpath("//div[@data-parent='" + sectionDataParent + "']/div/table/tbody/tr/td[4]/div/fieldset/div/button");
            waitForProgressAddingLine();
            waitForTextPresent("a1");
            waitForTextPresent(randomNumberString);
            waitAndClickByXpath("//div[@data-parent='" + sectionDataParent + "']/div/table/tbody/tr[2]/td[4]/div/fieldset/div/button");
            waitForProgress("Deleting Line", FIVE_SECOND_WAIT_TIME);
        }
    }

    protected void addAndVerify(String sectionDataParent, String randomNumberString) throws Exception {
        //Add & verify
        if (sectionDataParent != null) {
            waitAndTypeByXpath("//div[@data-parent='" + sectionDataParent + "']//div/div/table/tbody/tr/td[2]/div/input",
                    randomNumberString);
            waitAndTypeByXpath("//div[@data-parent='" + sectionDataParent + "']/div/div/table/tbody/tr/td[3]/div/input",
                    randomNumberString);
            waitAndClickByXpath("//div[@data-parent='" + sectionDataParent + "']/div/div/table/tbody/tr/td[4]/div/fieldset/div/button");
            waitForProgressAddingLine();
            waitForTextPresent(randomNumberString);

            // Delete & verify  (Need commented as functionality is not working properly.)
            waitAndClickByXpath("//div[@data-parent='" + sectionDataParent + "']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/button");
            waitForProgress("Deleting Line", FIVE_SECOND_WAIT_TIME);
            waitForTextPresent("You have deleted an item from");
            waitForTextNotPresent(randomNumberString);

            // Add the row back so we can edit it later
            waitAndTypeByXpath("//div[@data-parent='" + sectionDataParent + "']//div/div/table/tbody/tr/td[2]/div/input", randomNumberString);
            waitAndTypeByXpath("//div[@data-parent='" + sectionDataParent + "']/div/div/table/tbody/tr/td[3]/div/input", randomNumberString);
            waitAndClickByXpath("//div[@data-parent='" + sectionDataParent + "']/div/div/table/tbody/tr/td[4]/div/fieldset/div/button");
            waitForProgressAddingLine();
            waitForTextPresent(randomNumberString);
        }
    }

    private void editAndSave(String sectionDataParent, String randomNumber, String firstFieldToEdit,
            String secondFieldToEdit) throws Exception {
        // Edit & Save
        waitAndClickByXpath("//div[@data-parent='" + sectionDataParent + "']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/button[2]");
        waitForProgressLoading();

        if (firstFieldToEdit != null) {
            clearTextByXpath("//div[@data-parent='" + sectionDataParent + "']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/section/div/div/div/div/div/input");
            waitAndTypeByXpath("//div[@data-parent='" + sectionDataParent + "']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/section/div/div/div/div/div/input", randomNumber);
        }

        if (secondFieldToEdit != null) {
            clearTextByXpath("//div[@data-parent='" + sectionDataParent + "']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/section/div/div/div/div[2]/div/input");
            waitAndTypeByXpath("//div[@data-parent='" + sectionDataParent + "']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/section/div/div/div/div[2]/div/input", randomNumber);
        }
        waitAndClickByXpath("//div[@data-parent='" + sectionDataParent + "']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/section/div/div/div[2]/button");
        waitForProgress("Editing Line", FIVE_SECOND_WAIT_TIME);
    }

    private void editAndCancel(String sectionDataParent, String randomNumber, String firstFieldToEdit,
            String secondFieldToEdit) throws Exception {
        // Edit & Cancel
        waitAndClickByXpath("//div[@data-parent='" + sectionDataParent + "']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/button[2]");
        waitForProgressLoading();

        if(firstFieldToEdit!=null){
            clearTextByXpath("//div[@data-parent='" + sectionDataParent + "']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/section/div/div/div/div/div/input");
            waitAndTypeByXpath("//div[@data-parent='" + sectionDataParent + "']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/section/div/div/div/div/div/input", randomNumber);
        }

        if(secondFieldToEdit!=null){
            clearTextByXpath("//div[@data-parent='" + sectionDataParent + "']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/section/div/div/div/div[2]/div/input");
            waitAndTypeByXpath("//div[@data-parent='" + sectionDataParent + "']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/section/div/div/div/div[2]/div/input", randomNumber);
        }
        waitAndClickByXpath("//div[@data-parent='"+sectionDataParent+"']/div/div/table/tbody/tr[2]/td[4]/div/fieldset/div/section/div/div/div[2]/button[contains(text(),'No')]");
        waitForProgressLoading();

        // Verify that randomNumber is not present - it should never be since we didn't save that change
        waitForTextNotPresent(randomNumber);
    }

    protected String generate4DigitRandomNumber() {
        int randomNumber = (int)(Math.random()*9000)+1000;
        return String.valueOf(randomNumber);
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
