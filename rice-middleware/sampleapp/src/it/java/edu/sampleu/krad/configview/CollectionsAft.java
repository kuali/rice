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
package edu.sampleu.krad.configview;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CollectionsAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/uicomponents?viewId=ConfigurationTestView-Collections&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/uicomponents?viewId=ConfigurationTestView-Collections&methodToCall=start";

    /**
     * (//a[contains(text(),'Collections Configuration Test View')])[2]
     */
    public static final String TEXT_COLLECTIONS_CONFIGURATION_TEST_VIEW_XPATH =
            "(//a[contains(text(),'Collections Configuration Test View')])[1]";

    /**
     * Kuali :: Collection Test View
     */
    public static final String KUALI_COLLECTION_WINDOW_TITLE = "Kuali :: Collection Test View";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigate() throws Exception {
        waitAndClickKRAD();
        jGrowl("Click Collections Configuration Test View link");
        waitAndClickByXpath(TEXT_COLLECTIONS_CONFIGURATION_TEST_VIEW_XPATH);
        switchToWindow(KUALI_COLLECTION_WINDOW_TITLE);
    }

    protected void testCollections() throws Exception {
        testActionColumnPlacement();
        testAddViaLightbox();
        testColumnSequence();
        testSequencerow();
        passed();
    }

    @Test
    public void testCollectionsBookmark() throws Exception {
        testCollections();
    }

    @Test
    public void testCollectionsNav() throws Exception {
        testCollections();
    }

    @Test
    public void testAddBlankLineBookmark() throws Exception {
        testAddBlankLine();
        passed();
    }

    @Test
    public void testAddBlankLineNav() throws Exception {
        testAddBlankLine();
        passed();
    }

    @Test
    public void testAddDeleteBookmark() throws Exception {
        testAddRowOfText();
        passed();
    }

    @Test
    public void testAddDeleteNav() throws Exception {
        testAddRowOfText();
        passed();
    }

    @Test
    public void testSumBookmark() throws Exception {
        testSum();
        passed();
    }

    @Test
    public void testSumNav() throws Exception {
        testSum();
        passed();
    }

    protected void testAddRowOfText() throws Exception {
        assertTableLayout();
        waitAndTypeByName("newCollectionLines['list1'].field1", "asdf1");
        waitAndTypeByName("newCollectionLines['list1'].field2", "asdf2");
        waitAndTypeByName("newCollectionLines['list1'].field3", "asdf3");
        waitAndTypeByName("newCollectionLines['list1'].field4", "asdf4");
        clickAdd();

        for (int second = 0;; second++) {
            if (second >= waitSeconds)
                jiraAwareFail(TIMEOUT_MESSAGE);
            try {
                if (waitAndGetAttributeByName("newCollectionLines['list1'].field1", "value").equals(""))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        assertEquals("", waitAndGetAttributeByName("newCollectionLines['list1'].field1", "value"));
        assertEquals("", waitAndGetAttributeByName("newCollectionLines['list1'].field2", "value"));
        assertEquals("", waitAndGetAttributeByName("newCollectionLines['list1'].field3", "value"));
        assertEquals("", waitAndGetAttributeByName("newCollectionLines['list1'].field4", "value"));
        assertEquals("asdf1", waitAndGetAttributeByName("list1[0].field1", "value"));
        assertEquals("asdf2", waitAndGetAttributeByName("list1[0].field2", "value"));
        assertEquals("asdf3", waitAndGetAttributeByName("list1[0].field3", "value"));
        assertEquals("asdf4", waitAndGetAttributeByName("list1[0].field4", "value"));
    }

    /**
     * Test adding a column of values to the Add Blank Line Tests Table Layout
     */
    protected void testAddBlankLine() throws Exception {
        waitAndClickByLinkText("Add Blank Line");
        waitAndClickById("Collections-AddBlankLine-TableTop_del_line0"); // the line withe asdf1, etc.
        Thread.sleep(3000); //  TODO a wait until the loading.gif isn't visible would be better
        waitAndClickByXpath("//button[contains(.,'Add Line')]");
        Thread.sleep(3000); //  TODO a wait until the loading.gif isn't visible would be better
        waitForElementPresentByName("list1[0].field1");
        assertTableLayout();
        assertEquals("", waitAndGetAttributeByName("list1[0].field1", "value"));
        assertEquals("", waitAndGetAttributeByName("list1[0].field2", "value"));
        assertEquals("", waitAndGetAttributeByName("list1[0].field3", "value"));
        assertEquals("", waitAndGetAttributeByName("list1[0].field4", "value"));
    }

    private void testSum() throws InterruptedException {
        assertEquals("5", waitAndGetAttributeByName("list1[0].field1", "value"));
        assertEquals("6", waitAndGetAttributeByName("list1[0].field2", "value"));
        assertEquals("7", waitAndGetAttributeByName("list1[0].field3", "value"));
        assertEquals("8", waitAndGetAttributeByName("list1[0].field4", "value"));
        Integer preValue= Integer.parseInt(driver.findElement(By.xpath("//fieldset/div/div[2]/p")).getText());
        waitAndTypeByName("list1[0].field1", "1");
        waitAndTypeByName("list1[0].field2", "1");
        waitAndTypeByName("list1[0].field3", "1");
        waitAndTypeByName("list1[0].field4", "1");
        Integer postValue= Integer.parseInt(driver.findElement(By.xpath("//fieldset/div/div[2]/p")).getText());
        if(preValue>postValue) {
            jiraAwareFail("Totalling not working in " + getClass().toString());
        }
    }

    /**
     * Test action column placement in table layout collections
     */
    protected void testActionColumnPlacement() throws Exception {
        //Lack of proper locators its not possible to uniquely identify/locate this elements without use of ID's.
        //This restricts us to use the XPath to locate elements from the dome.
        //This test is prone to throw error in case of any changes in the dom Html graph.
        waitAndClickByLinkText("Column Sequence");
        Thread.sleep(2000);

        //jiraAwareWaitAndClick("css=div.jGrowl-close");
        // check if actions column RIGHT by default
        //assertTrue(isElementPresent("//div[@id='ConfigurationTestView-collection1']//tr[2]/td[6]//button[contains(.,\"delete\")]"));
        for (int second = 0;; second++) {
            if (second >= waitSeconds)
                jiraAwareFail(TIMEOUT_MESSAGE);
            try {
                if (isElementPresentByXpath("//tr[2]/td[6]/div/fieldset/div/button"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }
        assertTrue(getClass().toString(), isElementPresentByXpath("//tr[2]/td[6]/div/fieldset/div/button"));

        // check if actions column is LEFT
        //assertTrue(isElementPresent("//div[@id='ConfigurationTestView-collection2']//tr[2]/td[1]//button[contains(.,\"delete\")]"));
        for (int second = 0;; second++) {
            if (second >= waitSeconds)
                jiraAwareFail(TIMEOUT_MESSAGE);
            try {
                if (isElementPresentByXpath("//tr/td/div/fieldset/div/button[@id='Collections-ColumnSequence-TableLeft_add']"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }
        assertTrue(getClass().toString(), isElementPresentByXpath("//tr/td/div/fieldset/div/button[@id='Collections-ColumnSequence-TableLeft_add']"));

        // check if actions column is 3rd in a sub collection
        //assertTrue(isElementPresent("//div[@id='ConfigurationTestView-subCollection2_line0']//tr[2]/td[3]//button[contains(.,\"delete\")]"));
        for (int second = 0;; second++) {
            if (second >= waitSeconds)
                jiraAwareFail(TIMEOUT_MESSAGE);
            try {
                if (isElementPresentByXpath("//tr[2]/td[3]/div/fieldset/div/button"))
                    break;
            } catch (Exception e) {}
            Thread.sleep(1000);
        }
        assertTrue(getClass().toString(), isElementPresentByXpath("//tr[2]/td[3]/div/fieldset/div/button"));
    }

    protected void testAddViaLightbox() throws Exception {
        waitAndClickByLinkText("Add Via Lightbox");
        Integer preValue= Integer.parseInt(driver.findElement(By.xpath("//fieldset/div/div[2]/p")).getText());
        waitAndClickButtonByText("Add Line");
        Thread.sleep(3000);
        waitAndTypeByXpath("//div[@class='modal-body']/div[2]/div/input", "1");
        waitAndTypeByXpath("//div[@class='modal-body']/div[4]/div/input", "1");
        waitAndTypeByXpath("//div[@class='modal-body']/div[6]/div/input", "1");
        waitAndTypeByXpath("//div[@class='modal-body']/div[8]/div/input", "1");
        waitAndClickByXpath("//button[@id='Collections-AddViaLightbox-TableTop_add']");
        Thread.sleep(3000);
        Integer postValue= Integer.parseInt(driver.findElement(By.xpath("//fieldset/div/div[2]/p")).getText());
        if(preValue>postValue) {
            jiraAwareFail("Totalling not working in " + getClass().toString());
        }
    }

    protected void testColumnSequence() throws Exception {
        waitAndClickByLinkText("Column Sequence");
        acceptAlert();
        Thread.sleep(3000);
        waitAndTypeByName("newCollectionLines['list1'].field1", "1");
        waitAndTypeByName("newCollectionLines['list1'].field2", "1");
        waitAndTypeByName("newCollectionLines['list1'].field3", "1");
        waitAndTypeByName("newCollectionLines['list1'].field4", "1");
        waitAndClick(By.id("Collections-ColumnSequence-TableDefault_add"));
        Thread.sleep(3000);

        //Check if row has been added really or not
        testIfRowHasBeenAdded();

        //Check for the added if delete is present or not
        assertTrue(getClass().toString(), isElementPresentByXpath("//div[@id='Collections-ColumnSequence-TableDefault_disclosureContent']/div[@class='dataTables_wrapper']/table/tbody/tr[2]/td[6]/div/fieldset/div/button"));
    }

    protected void testSequencerow() throws Exception {
        waitAndClickByLinkText("Save Row");
        acceptAlert();
        Thread.sleep(3000);
        waitAndTypeByName("newCollectionLines['list1'].field1", "1");
        waitAndTypeByName("newCollectionLines['list1'].field2", "1");
        waitAndTypeByName("newCollectionLines['list1'].field3", "1");
        waitAndTypeByName("newCollectionLines['list1'].field4", "1");
        clickAdd();
        Thread.sleep(3000);

        //Check if row has been added really or not
        testIfRowHasBeenAdded();

        //Check for the added if delete is present or not
        assertTrue(getClass().toString(), isElementPresentByXpath("//div[@id='Collections-SaveRow-Table_disclosureContent']/div[@class='dataTables_wrapper']/table/tbody/tr[2]/td[6]/div/fieldset/div/button"));
        //        assertTrue(isElementPresentByXpath("//div[@id='Collections-SaveRow-Table_disclosureContent']/div[@class='dataTables_wrapper']/table/tbody/tr[2]/td[6]/div/fieldset/div/div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button[@class='uif-action uif-secondaryActionButton uif-smallActionButton uif-saveLineAction']"));
    }

    private void clickAdd() throws InterruptedException {
        jGrowl("Click Add");
        waitAndClickByXpath("//button[contains(.,'Add')]");
    }

    protected void testIfRowHasBeenAdded() throws Exception {
        //Check if row has been added really or not
        assertEquals("", waitAndGetAttributeByName("newCollectionLines['list1'].field1", "value"));
        assertEquals("", waitAndGetAttributeByName("newCollectionLines['list1'].field2", "value"));
        assertEquals("", waitAndGetAttributeByName("newCollectionLines['list1'].field3", "value"));
        assertEquals("", waitAndGetAttributeByName("newCollectionLines['list1'].field4", "value"));
        assertEquals("1", waitAndGetAttributeByName("list1[0].field1", "value"));
        assertEquals("1", waitAndGetAttributeByName("list1[0].field2", "value"));
        assertEquals("1", waitAndGetAttributeByName("list1[0].field3", "value"));
        assertEquals("1", waitAndGetAttributeByName("list1[0].field4", "value"));
    }
}
