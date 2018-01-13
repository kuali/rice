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
package org.kuali.rice.krad.demo.uif.library.containers;

import org.junit.Ignore;
import org.junit.Test;

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LibraryContainerDialogGroupAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/dialog?viewId=Demo-DialogGroupView
     */
    public static final String BOOKMARK_URL = "/kr-krad/dialog?viewId=Demo-DialogGroupView";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Containers");
        waitAndClickByLinkText("Dialog Group");
    }
    
    protected void testContainerDialogGroupConfirmAction() throws Exception {
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-Example1']/button");
    	waitAndClickByXpath("//section[@id='Uif-DialogGroup-YesNotmp']/div/div/div[@data-parent='Uif-DialogGroup-YesNo']/button[contains(text(),'No')]");
    }

    protected void testContainerDialogGroupConfirmWithExplaination() throws Exception {
    	waitAndClickByLinkText("Confirm with Explanation");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-Example2']/button");
    	waitForElementPresentByXpath("//section[@id='Demo-DialogEx2']/div/div/div[@class='modal-body']/div/div/textarea");
    	waitAndClickByXpath("//section[@id='Demo-DialogEx2']/div/div/div[@data-parent='Demo-DialogEx2']/button[contains(text(),'OK')]");
    	waitForElementPresentByXpath("//a[contains(text(),'Required')]");
    	waitAndClickByXpath("//section[@id='Demo-DialogEx2']/div/div/div[@data-parent='Demo-DialogEx2']/button[contains(text(),'Cancel')]");
    }
    
    protected void testContainerDialogGroupGetDialogResponse1() throws Exception {
    	waitAndClickByLinkText("Get Dialog Response Ex. 1");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-Example3']/button");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-DialogResponse1']/div/div/div[@data-parent='Demo-DialogGroup-DialogResponse1']/button[contains(text(),'OK')]");
    	acceptAlertIfPresent();
        waitAndClickByXpath("//section[@id='Demo-DialogGroup-Example3']/button");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-DialogResponse1']/div/div/div[@data-parent='Demo-DialogGroup-DialogResponse1']/button[contains(text(),'Cancel')]");
    	acceptAlertIfPresent();
    }

    protected void testContainerDialogGroupGetDialogResponse2() throws Exception {
    	waitAndClickByLinkText("Get Dialog Response Ex. 2");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-Example4']/button");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-DialogResponse2']/div/div/div[@data-parent='Demo-DialogGroup-DialogResponse2']/button[contains(text(),'Green Eggs and Ham')]");
    	acceptAlertIfPresent();
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-Example4']/button");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-DialogResponse2']/div/div/div[@data-parent='Demo-DialogGroup-DialogResponse2']/button[contains(text(),'Cat in the Hat')]");
    	acceptAlertIfPresent();
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-Example4']/button");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-DialogResponse2']/div/div/div[@data-parent='Demo-DialogGroup-DialogResponse2']/button[contains(text(),'The Grinch')]");
    	acceptAlertIfPresent();
    }
    
    protected void testContainerDialogGroupShowDialog1() throws Exception {
    	waitAndClickByLinkText("Show Dialog Ex. 1");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-Example5']/a[contains(text(),'Show Dialog')]");
    	waitForElementPresentByXpath("//input[@name='inputField1']");
    	waitForElementPresentByXpath("//input[@name='inputField2']");
    	waitForElementPresentByXpath("//input[@name='inputField3']");
    	waitForElementPresentByXpath("//input[@name='inputField4']");
    	waitForElementPresentByXpath("//input[@name='inputField5']");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-ShowDialog1']/div/div/div[@data-parent='Demo-DialogGroup-ShowDialog1']/button[contains(text(),'Continue')]");
    }

    protected void testContainerDialogGroupShowDialog2() throws Exception {
    	waitAndClickByLinkText("Show Dialog Ex. 2");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-Example6']/a[contains(text(),'Show Dialog')]");
    	waitForElementPresentByXpath("//section[@id='Demo-DialogGroup-ShowDialog2Refresh' and @style='display: none; background-color: rgba(255, 248, 198, 0);']");
    	waitAndClickByXpath("//input[@name='inputField6' and @value='show']");
    	waitForElementPresentByXpath("//section[@id='Demo-DialogGroup-ShowDialog2Refresh' and @style='display: block; background-color: rgba(255, 248, 198, 0);']");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-ShowDialog2']/div/div/div[@data-parent='Demo-DialogGroup-ShowDialog2']/button[contains(text(),'OK')]");
    }
    
    protected void testContainerDialogGroupDialogEvents() throws Exception {
    	waitAndClickByLinkText("Dialog Events");
        waitAndClickByXpath("//section[@id='Demo-DialogGroup-Example7']/button");
        waitAndClickByXpath("//section[@id='Demo-DialogGroup-DialogEvents']/div/div/div[@data-parent='Demo-DialogGroup-DialogEvents']/button[contains(text(),'Continue')]");
        acceptAlertIfPresent();
        acceptAlertIfPresent();
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-Example7']/button");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-DialogEvents']/div/div/div[@data-parent='Demo-DialogGroup-DialogEvents']/button[contains(text(),'Log out')]");
    	acceptAlertIfPresent();
    	acceptAlertIfPresent();
    }
    
    protected void testContainerDialogGroupServerDialog1() throws Exception {
    	waitAndClickByLinkText("Server Dialog Ex. 1");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-Example8']/button");
        waitForProgressLoading();
        waitAndClickByXpath(
                "//section[@id='Demo-DialogGroup-ServerResponse1']/div/div/div[@data-parent='Demo-DialogGroup-ServerResponse1']/button[contains(text(),'Yes')]");
        waitForElementPresentByXpath("//div[@id='Demo-DialogGroup-Example8_messages']/ul/li[contains(text(), 'Save was completed.')]");
    }
    
    protected void testContainerDialogGroupServerDialog2() throws Exception {
        waitAndClickByLinkText("Server Dialog Ex. 2");
        waitAndClickByXpath("//section[@id='Demo-DialogGroup-Example9']/button");
        waitForProgressLoading();
        waitAndClickByXpath("//section[@id='Demo-DialogGroup-ServerResponse2']/div/div/div[@data-parent='Demo-DialogGroup-ServerResponse2']/button[contains(text(),'OK')]");
        waitForElementPresentByXpath("//a[contains(text(),'Required')]");
        waitIsVisible(By.xpath("//section[@id='Demo-DialogGroup-ServerResponse2']/div/div/div[@data-parent='Demo-DialogGroup-ServerResponse2']/button[contains(text(),'Cancel')]"));
        waitAndClickByXpath("//section[@id='Demo-DialogGroup-ServerResponse2']/div/div/div[@data-parent='Demo-DialogGroup-ServerResponse2']/button[contains(text(),'Cancel')]");
    }

    protected void testContainerDialogGroupAjaxRetrieval() throws Exception {
    	waitAndClickByLinkText("Ajax Retrieval");
    	//Needs to fix the functionality.
    }
    
    protected void testContainerDialogGroupValidationInDialog() throws Exception {
    	waitAndClickByLinkText("Validation in Dialog");
    	waitAndClickByLinkText("Show Dialog");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-ShowDialog11']/div/div/div[@data-parent='Demo-DialogGroup-ShowDialog11']/button[contains(text(),'Continue')]");
    	waitForElementPresentByXpath("//a[contains(text(),'Field 1: Required')]");
    	waitForElementPresentByXpath("//a[contains(text(),'Field 2: Required')]");
    	waitForElementPresentByXpath("//a[contains(text(),'Field 3: Required')]");
    	waitForElementPresentByXpath("//a[contains(text(),'Field 4: Required')]");
    	waitForElementPresentByXpath("//a[contains(text(),'Field 5: Required')]");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-ShowDialog11']/div/div/header/button");
    }

    protected void testContainerDialogGroupDialogReuse() throws Exception {
        Thread.sleep(1000); // try to avoid cache problem
    	waitAndClickByLinkText("Dialog Reuse");

        // assert first row data
        assertEquals("1", waitForElementVisibleBy(By.name("collection2[1].field1")).getAttribute("value"));
        assertEquals("2", waitForElementVisibleBy(By.name("collection2[1].field2")).getAttribute("value"));
        assertEquals("3", waitForElementVisibleBy(By.name("collection2[1].field3")).getAttribute("value"));

        jGrowl("Click Delete");
        // First row is del_line1, noting this here as the second row is del_line0
    	waitAndClickByXpath("//button[@id='Demo-DialogGroup-WizardStep2_del_line1']");
        jGrowl("Cancel Delete");
        waitForTextPresent("Are you sure you wish to delete line: 1?");
    	waitAndClickByXpath("//section[@id='Uif-DialogGroup-YesNotmp']/div/div/div[@data-parent='Uif-DialogGroup-YesNo']/button[contains(text(),'No')]");

        // assert first row data not deleted
        assertEquals("1", waitForElementVisibleBy(By.name("collection2[1].field1")).getAttribute("value"));
        assertEquals("2", waitForElementVisibleBy(By.name("collection2[1].field2")).getAttribute("value"));
        assertEquals("3", waitForElementVisibleBy(By.name("collection2[1].field3")).getAttribute("value"));

        jGrowl("Click Delete");
        waitAndClickByXpath("//button[@id='Demo-DialogGroup-WizardStep2_del_line0']");
        waitForTextPresent("Are you sure you wish to delete line: A?");
        jGrowl("Confirm Delete");
        waitAndClickByXpath("//section[@id='Uif-DialogGroup-YesNotmp']/div/div/div[@data-parent='Uif-DialogGroup-YesNo']/button[contains(text(),'Yes')]");
        waitForProgressDeletingLine();

        // assert new second row data
        assertEquals("a", waitForElementVisibleBy(By.name("collection2[2].field1")).getAttribute("value"));
        assertEquals("b", waitForElementVisibleBy(By.name("collection2[2].field2")).getAttribute("value"));
        assertEquals("c", waitForElementVisibleBy(By.name("collection2[2].field3")).getAttribute("value"));
    }
    
    protected void testContainerDialogGroupSmallDialog() throws Exception {
    	waitAndClickByLinkText("Small Dialog");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-Example13']/a");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-ShowSmallDialog']/div/div/div[@data-parent='Demo-DialogGroup-ShowSmallDialog']/button[contains(text(),'OK')]");
    }
    
    protected void testContainerDialogGroupLargeDialog() throws Exception {
    	waitAndClickByLinkText("Large Dialog");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-Example14']/a");
    	waitAndClickByXpath("//section[@id='Demo-DialogGroup-ShowLargeDialog']/div/div/div[@data-parent='Demo-DialogGroup-ShowLargeDialog']/button[contains(text(),'OK')]");
    }

    protected void testContainerDialogGroupDestroyDialogOnHidden() throws Exception {
        WebElement exampleAnchorElement = findElement(By.cssSelector("a[id^='Demo-DialogGroup-Example15']"));
        exampleAnchorElement.click();
        WebElement dialog = findElement(By.cssSelector(
                "section[id='Demo-DialogGroup-DestroyDialogOnHidden'] > div.modal-dialog"));
        assertTrue("Dialog should not be visisble on page load.", !dialog.isDisplayed());
        WebElement dialogLink = findElement(By.cssSelector("section[id='Demo-DialogGroup-Example15'] > a"));
        dialogLink.click();
        waitForProgressLoading();
        dialog = waitForElementVisibleBy(By.cssSelector("section[id='Demo-DialogGroup-DestroyDialogOnHidden']"));
        WebElement anyButton = dialog.findElement(By.cssSelector("button:first-of-type"));
        anyButton.click();
        try {
            dialog = waitAndGetElementByAttributeValue("id", "Demo-DialogGroup-DestroyDialogOnHidden");
            assertFalse("Dialog should not be present.", dialog.isDisplayed());
        } catch (NoSuchElementException exception) {
        }
    }
    
    protected void testContainerDialogGroupAll() throws Exception {
    	testContainerDialogGroupConfirmAction();
    	testContainerDialogGroupGetDialogResponse1();
    	testContainerDialogGroupGetDialogResponse2();
    	testContainerDialogGroupShowDialog1();
    	testContainerDialogGroupShowDialog2();
        testContainerDialogGroupServerDialog1();
//      testContainerDialogGroupServerDialog2(); // fails when run with others, test below
    	testContainerDialogGroupAjaxRetrieval();
//    	testContainerDialogGroupValidationInDialog(); // fails when run with others, test below
    	testContainerDialogGroupDialogReuse();
//    	testContainerDialogGroupSmallDialog(); // fails when run with others, test below
//    	testContainerDialogGroupLargeDialog(); // run below
//        testContainerDialogGroupDestroyDialogOnHidden(); // run below
        testContainerDialogGroupDialogEvents(); // run last as it logs out via dialog
    }

    @Test
    public void testContainerDialogGroupBookmark() throws Exception {
    	testContainerDialogGroupAll();
        passed();
    }

    @Test
    public void testContainerDialogGroupNav() throws Exception {
    	testContainerDialogGroupAll();
        passed();
    }

    @Test
    public void testContainerDialogGroupLargeDialogBookmark() throws Exception {
        testContainerDialogGroupLargeDialog();
        passed();
    }

    @Test
    public void testContainerDialogGroupLargeDialogNav() throws Exception {
        testContainerDialogGroupLargeDialog();
        passed();
    }

    @Test
    public void testContainerDialogGroupServerDialog2Bookmark() throws Exception {
        testContainerDialogGroupServerDialog2();
        passed();
    }

    @Test
    public void testContainerDialogGroupServerDialog2Nav() throws Exception {
        testContainerDialogGroupServerDialog2();
        passed();
    }

    @Test
    public void testContainerDialogGroupValidationInDialogBookmark() throws Exception {
        testContainerDialogGroupValidationInDialog();
        passed();
    }

    @Test
    public void testContainerDialogGroupValidationInDialogNav() throws Exception {
        testContainerDialogGroupValidationInDialog();
        passed();
    }

    @Test
    public void testContainerDialogGroupSmallDialogBookmark() throws Exception {
        testContainerDialogGroupSmallDialog();
        passed();
    }

    @Test
    public void testContainerDialogGroupSmallDialogNav() throws Exception {
        testContainerDialogGroupSmallDialog();
        passed();
    }

    @Test
    public void testContainerDialogGroupDestroyDialogOnHiddenBookmark() throws Exception {
        testContainerDialogGroupDestroyDialogOnHidden();
        passed();
    }

    @Test
    public void testContainerDialogGroupDestroyDialogOnHiddenNav() throws Exception {
        testContainerDialogGroupDestroyDialogOnHidden();
        passed();
    }
}
