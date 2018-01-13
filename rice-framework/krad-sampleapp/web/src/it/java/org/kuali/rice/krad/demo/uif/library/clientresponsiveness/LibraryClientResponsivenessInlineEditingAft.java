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
package org.kuali.rice.krad.demo.uif.library.clientresponsiveness;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 * Functional Test ensures correct behavior of the client responsiveness component refresh actions.
 */
public class LibraryClientResponsivenessInlineEditingAft extends WebDriverLegacyITBase {

    /* Bookmark url for client repsonsiveness component refresh view */
    protected static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-InlineEdit";
    
    /**
     * Returns the client responsiveness component refresh view
     * @return the url for the client responsiveness component refresh view
     */
    @Override
    protected String getBookmarkUrl() { return BOOKMARK_URL; }

    /**
     * Navigates to the base page, before the tests start
     * @throws Exception if errors while navigating to base page
     */
    @Override
    protected void navigate() throws Exception {
        waitAndClickLibraryLink();
        waitAndClickByLinkText("Client Responsiveness");
        waitAndClickByLinkText("Inline Editing");
    }

    /**
     * Ensures proper behaviour of the method to call on refresh action
     * @throws Exception if errors while testing the method to call on refresh action
     */
    protected void testClientResponsivenessInlineEditing() throws Exception {
    	testClientResponsivenessInlineEditTextField();   //   Need to fix this from developement side
    	testClientResponsivenessInlineEditTextFieldWithComplexMarkUp();   //Need to fix this from developement side
    	testClientResponsivenessInlineEditTextFieldViaAjax();
    	testClientResponsivenessInlineEditTextArea();
    	testClientResponsivenessInlineEditTableCollection();
    	testClientResponsivenessInlineEditLabelColumnCollectionGrid();
    }

    /**
     * Ensures proper behavior of the reset data on refresh action
     * @throws Exception if errors while testing rest data on refresh
     */
    protected void testClientResponsivenessInlineEditTextField() throws Exception {
    	waitForElementPresent("//section[@id='Demo-InlineEdit-Example1']/div/button[contains(text(),'My InLine Book Title')]");
    	waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example1']/div/button");
        waitAndTypeByXpath("//div[@data-parent='Demo-InlineEdit-Example1']/div/input[@name='dataField3']", "1");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example1']/div/div/div/button[@title='Save']");
        waitForElementPresent("//section[@id='Demo-InlineEdit-Example1']/div/button[contains(text(),'My Inline Book Title1')]");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example1']/div/button");
        waitAndTypeByXpath("//div[@data-parent='Demo-InlineEdit-Example1']/div/input[@name='dataField3']", "a");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example1']/div/div/div/button[@title='Cancel']");
        waitForElementPresent("//section[@id='Demo-InlineEdit-Example1']/div/button[contains(text(),'My Inline Book Title1')]");
    }
    
    /**
     * Ensures proper behavior of the reset data on refresh action
     * @throws Exception if errors while testing rest data on refresh
     */
    protected void testClientResponsivenessInlineEditTextFieldWithComplexMarkUp() throws Exception {
    	selectByName("exampleShown","Inline Edit Text Field w Complex Markup");
    	waitForElementPresent("//section[@id='Demo-InlineEdit-Example2']/div/button[contains(text(),'My Book Title')]");
    	waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example2']/div/button");
        waitAndTypeByXpath("//div[@data-parent='Demo-InlineEdit-Example2']/div/input[@name='dataField6']", "a");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example2']/div/div/div/button[@title='Save']");
        waitForElementPresent("//section[@id='Demo-InlineEdit-Example2']/div/button[contains(text(),'My Book Titlea')]");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example2']/div/button");
        waitAndTypeByXpath("//div[@data-parent='Demo-InlineEdit-Example2']/div/input[@name='dataField6']", "b");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example2']/div/div/div/button[@title='Cancel']");
        waitForElementPresent("//section[@id='Demo-InlineEdit-Example2']/div/button[contains(text(),'My Book Titlea')]");
    }
    
    /**
     * Ensures proper behavior of the reset data on refresh action
     * @throws Exception if errors while testing rest data on refresh
     */
    protected void testClientResponsivenessInlineEditTextFieldViaAjax() throws Exception {
    	selectByName("exampleShown","Inline Edit Text Field via Ajax");
    	waitForElementPresent("//section[@id='Demo-InlineEdit-Example3']/div/button[contains(text(),'10011')]");
    	waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example3']/div/button");
        waitAndTypeByName("dataField1","a");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example3']/div/div/div/button[@title='Save']");
        waitForElementPresent("//section[@id='Demo-InlineEdit-Example3']/div/button[contains(text(),'10011a')]");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example3']/div/button");
        waitAndTypeByName("dataField1","b");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example3']/div/div/div/button[@title='Cancel']");
        waitForElementPresent("//section[@id='Demo-InlineEdit-Example3']/div/button[contains(text(),'10011a')]");
    }
    
    /**
     * Ensures proper behavior of the reset data on refresh action
     * @throws Exception if errors while testing rest data on refresh
     */
    protected void testClientResponsivenessInlineEditTextArea() throws Exception {
    	selectByName("exampleShown","Inline Edit Textarea");
    	waitForElementPresent("//section[@id='Demo-InlineEdit-Example4']/div/button/pre[contains(text(),'Book')]");
    	waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example4']/div/button");
        waitAndTypeByName("dataField5","asdf");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example4']/div/div/div/button[@title='Save']");
        waitForElementPresent("//section[@id='Demo-InlineEdit-Example4']/div/button/pre[contains(text(),'asdf')]");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example4']/div/button");
        waitAndTypeByName("dataField5","b");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example4']/div/div/div/button[@title='Cancel']");
        waitForElementPresent("//section[@id='Demo-InlineEdit-Example4']/div/button/pre[contains(text(),'asdf')]");
    }
    
    /**
     * Ensures proper behavior of the reset data on refresh action
     * @throws Exception if errors while testing rest data on refresh
     */
    protected void testClientResponsivenessInlineEditSelect() throws Exception {
    	selectByName("exampleShown","Inline Edit Select");
    	waitForElementPresent("//section[@id='Demo-InlineEdit-Example5']/div/button[contains(text(),'Option 3')]");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example5']/div/button");
        selectByName("dataField7","Option 1");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example5']/div/div/div/button[@title='Save']");
        waitForElementPresent("//section[@id='Demo-InlineEdit-Example5']/div/button[contains(text(),'1')]");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example5']/div/button");
        selectByName("dataField7","Option 2");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example5']/div/div/div/button[@title='Cancel']");
        waitForElementPresent("//section[@id='Demo-InlineEdit-Example5']/div/button[contains(text(),'1')]");
    }

    /**
     * Ensures proper behavior of the reset data on refresh action
     * @throws Exception if errors while testing rest data on refresh
     */
    protected void testClientResponsivenessInlineEditPopoverWidget() throws Exception {
    	selectByName("exampleShown","Inline Edit in Popover widget");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example6']/button");
        waitForElementPresentByXpath("//div[@id='Demo-PopoverContent-Group']/div/button[contains(text(),'My Second Book Title')]");
        waitAndClickByXpath("//div[@id='Demo-PopoverContent-Group']/div/button");
        waitAndTypeByName("dataField8","a");
        waitAndClickByXpath("//div[@id='Demo-PopoverContent-Group']/div/div/div/button[@title='Save']");
        waitForElementPresentByXpath("//div[@id='Demo-PopoverContent-Group']/div/button[contains(text(),'My Second Book Titlea')]");
        waitAndClickByXpath("//div[@id='Demo-PopoverContent-Group']/div/button");
        waitAndTypeByName("dataField8","b");
        waitAndClickByXpath("//div[@id='Demo-PopoverContent-Group']/div/div/div/button[@title='Cancel']");
        waitForElementPresentByXpath("//div[@id='Demo-PopoverContent-Group']/div/button[contains(text(),'My Second Book Titlea')]");
        waitForElementPresentByXpath("//div[@id='Demo-PopoverContent-Group']/div[2]/button");
        waitForElementPresentByXpath("//div[@id='Demo-PopoverContent-Group']/div[3]/input");
        waitAndClickByXpath("//div[@id='Demo-PopoverContent-Group']/button");
        acceptAlertIfPresent();
    }
    
    /**
     * Ensures proper behavior of the reset data on refresh action
     * @throws Exception if errors while testing rest data on refresh
     */
    protected void testClientResponsivenessInlineEditDisclosureWidget() throws Exception {
    	selectByName("exampleShown","Inline Edit in Disclosure widget");
    	waitForElementPresentByXpath("//div[@style='display: none; overflow: hidden;']");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example7']/section/header/h3/a");
        waitForElementPresentByXpath("//div[@style='display: block; overflow: hidden;']");
        waitForElementPresent("//section[@id='Demo-InlineEdit-Example7']/section/div/div/button[contains(text(),'My Third Book Title')]");
    	waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example7']/section/div/div/button");
        waitAndTypeByName("dataField11","a");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example7']/section/div/div/div/div/button[@title='Save']");
        waitForElementPresent("//section[@id='Demo-InlineEdit-Example7']/section/div/div/button[contains(text(),'My Third Book Titlea')]");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example7']/section/div/div/button");
        waitAndTypeByName("dataField11","b");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example7']/section/div/div/div/div/button[@title='Cancel']");
        waitForElementPresent("//section[@id='Demo-InlineEdit-Example7']/section/div/div/button[contains(text(),'My Third Book Titlea')]");
    }
    
    /**
     * Ensures proper behavior of the reset data on refresh action
     * @throws Exception if errors while testing rest data on refresh
     */
    protected void testClientResponsivenessInlineEditTableCollection() throws Exception {
    	selectByName("exampleShown","Inline Edit in Table Collection");
    	waitForElementPresent("//table/tbody/tr/td/div/button[contains(text(),'1')]");
    	waitAndClickByXpath("//table/tbody/tr/td/div/button");
        waitAndTypeByName("collection1[0].field1","a");
        waitAndClickByXpath("//table/tbody/tr/td/div/div/div/button[@title='Save']");
        waitForElementPresent("//table/tbody/tr/td/div/button[contains(text(),'1a')]");
        waitAndClickByXpath("//table/tbody/tr/td/div/button");
        waitAndTypeByName("collection1[0].field1","b");
        waitAndClickByXpath("//table/tbody/tr/td/div/div/div/button[@title='Cancel']");
        waitForElementPresent("//table/tbody/tr/td/div/button[contains(text(),'1a')]");
    }
    
    /**
     * Ensures proper behavior of the reset data on refresh action
     * @throws Exception if errors while testing rest data on refresh
     */
    protected void testClientResponsivenessInlineEditLabelColumnCollectionGrid() throws Exception {
    	selectByName("exampleShown","Inline Edit in a 1 Label Column CSS Grid");
    	waitForElementPresent("//section[@id='Demo-InlineEdit-Example9']/div/div/button");
    	waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example9']/div/div/button");
        waitAndTypeByName("inputField10","1");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example9']/div/div/div/div/button[@title='Save']");
        waitForElementPresent("//section[@id='Demo-InlineEdit-Example9']/div/div/button[contains(text(),'1')]");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example9']/div/div/button");
        waitAndTypeByName("inputField10","a");
        waitAndClickByXpath("//section[@id='Demo-InlineEdit-Example9']/div/div/div/div/button[@title='Cancel']");
        waitForElementPresent("//section[@id='Demo-InlineEdit-Example9']/div/div/button[contains(text(),'1')]");
    }
    
    /**
     * Test by bookmark
     * @throws Exception if errors while testing bookmark
     */
    @Test
    public void testClientResponsivenessComponentRefreshBookmark() throws Exception {
        testClientResponsivenessInlineEditing();
        passed();
    }

    /**
     * Test by navigation
     * @throws Exception if errors while testing navigation
     */
    @Test
    public void testClientResponsivenessComponentRefreshNav() throws Exception {
        testClientResponsivenessInlineEditing();
        passed();
    }

    @Test
    public void testClientResponsivenessInlineEditDisclosureWidgetBookmark() throws Exception {
        testClientResponsivenessInlineEditDisclosureWidget();
        passed();
    }

    @Test
    public void testClientResponsivenessInlineEditDisclosureWidgetNav() throws Exception {
        testClientResponsivenessInlineEditDisclosureWidget();
        passed();
    }

    @Test
    public void testClientResponsivenessInlineEditSelectBookmark() throws Exception {
        testClientResponsivenessInlineEditSelect();
        passed();
    }

    @Test
    public void testClientResponsivenessInlineEditSelectNav() throws Exception {
        testClientResponsivenessInlineEditSelect();
        passed();
    }

    @Test
    public void testClientResponsivenessInlineEditPopoverWidgetBookmark() throws Exception {
        testClientResponsivenessInlineEditPopoverWidget();
        passed();
    }

    @Test
    public void testClientResponsivenessInlineEditPopoverWidgetNav() throws Exception {
        testClientResponsivenessInlineEditPopoverWidget();
        passed();
    }
}
