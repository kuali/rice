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
package org.kuali.rice.krad.demo.uif.library.elements;

import org.junit.Test;
import org.kuali.rice.krad.demo.uif.library.DemoLibraryBase;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoElementsActionAft extends DemoLibraryBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-ActionView&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-ActionView&methodToCall=start";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Elements");
        waitAndClickByLinkText("Action");
    }

    protected void testActionDefault() throws Exception {
        waitForElementPresentByClassName("uif-headerText-span");
        assertTextPresent("Default");
        assertTrue(getTextByClassName("uif-instructionalMessage").contains(
                "Action with action script"));
        assertElementPresentByLinkText("Action Link");
        waitAndClickByLinkText("Action Link");
        acceptAlert();
    }

    protected void testActionPresubmit() throws Exception {
        waitAndClickByLinkText("Presubmit");
        waitForElementPresentByClassName("uif-headerText-span");
        assertTextPresent("Presubmit");
        assertTextPresent("ActionLinkField with presubmit script");
        assertElementPresentByLinkText("Pre submit returning true Link");
        assertElementPresentByLinkText("Pre submit returning false Link");

        waitAndClickByLinkText("Pre submit returning true Link");
        assertTrue(driver.switchTo().alert().getText().contains("Pre submit call was invoked, returning true"));
        driver.switchTo().alert().accept();

        waitAndClickByLinkText("Pre submit returning false Link");
        assertTrue(driver.switchTo().alert().getText().contains("Pre submit call was invoked, returning false"));
        driver.switchTo().alert().accept();
    }

    protected void testActionSuccessCallback() throws Exception {
        waitAndClickByLinkText("Success Callback");
        waitForElementPresentByClassName("uif-headerText-span");
        assertTextPresent("Action Field with a success callback script");
        assertElementPresentByLinkText("Action Link success callback");

        waitAndClickByLinkText("Action Link success callback");
        Thread.sleep(1500); // give the alert time to get triggered via callback
        assertTrue(driver.switchTo().alert().getText().contains("Refresh called successfully"));
        driver.switchTo().alert().accept();
    }

    protected void testActionValidation() throws Exception {
        waitForElementPresentByClassName("uif-page"); // make sure the page is there before we use the driver
        findElement(By.className("uif-page")).findElement(By.linkText("Validation")).click();

        waitForElementPresentByClassName("uif-headerText-span");
        assertTextPresent("Action Field with client side validation");
        assertTextPresent("InputField 1");
        assertIsNotVisibleByXpath("//a[contains(text(),'Required')]");

        waitAndClickByLinkText("Action Field with client side required validation");
        assertIsVisibleByXpath("//a[contains(text(),'Required')]", "");

        waitAndTypeByName("inputField1", "some text");
        waitAndClickByLinkText("Action Field with client side required validation");
        assertIsNotVisibleByXpath("//a[contains(text(),'Required')]");
    }

    protected void testActionImages() throws Exception {
        waitAndClickByLinkText("Images");
        waitForElementPresentByClassName("uif-headerText-span");
        assertTextPresent("Images");
        assertTextPresent("Action Field with images");
        findElement(By.partialLinkText("Action Link with left image")).findElement(By.className("leftActionImage"));
        findElement(By.partialLinkText("Action Link with right image")).findElement(By.className("rightActionImage"));
        waitAndClickByLinkText("Action Link with left image");
        acceptAlert();
        waitAndClickByLinkText("Action Link with right image");
        acceptAlert();
    }

    protected void testActionButton() throws Exception {
        waitAndClickByLinkText("Buttons");
        waitForElementPresentByClassName("prettyprint");
        assertTextPresent("Buttons");
        assertTextPresent("Action Field buttons");

        waitAndClickButtonByText("button");
        assertTrue(driver.switchTo().alert().getText().contains("You clicked a button"));
        driver.switchTo().alert().accept();

        waitAndClickButtonByText("Image BOTTOM");
        assertTrue(driver.switchTo().alert().getText().contains("You clicked a button"));
        driver.switchTo().alert().accept();

        waitAndClickById("ST-DemoButtonImageTop");
        assertTrue(driver.switchTo().alert().getText().contains("You clicked a button"));
        driver.switchTo().alert().accept();

        findElement(By.xpath("//span[contains(text(),'Image LEFT')]"));
        findElement(By.id("ST-DemoButtonImageLeft")).click();
        assertTrue(driver.switchTo().alert().getText().contains("You clicked a button"));
        driver.switchTo().alert().accept();

        findElement(By.id("ST-DemoButtonImageRight")).click();
        assertTrue(driver.switchTo().alert().getText().contains("You clicked a button"));
        driver.switchTo().alert().accept();

        findElement(By.id("ST-DemoButtonImageOnly")).click();
        assertTrue(driver.switchTo().alert().getText().contains("You clicked a button"));
        driver.switchTo().alert().accept();

        findElement(By.xpath("//button[contains(text(),'Disabled Button') and @disabled]/preceding-sibling::button/img"));
        findElement(By.xpath("//button/img[contains(@alt,'Image Only button')]"));

        findElement(By.xpath("//button[contains(text(),'Disabled Button') and @disabled]"));
    }
    
    protected void testActionStyleVarities() throws Exception {
    	waitAndClickByLinkText("Style Varieties");
    	
    	//Assertion of Large Buttons
    	waitForElementPresentByXpath("//button[@class='btn btn-primary btn-lg uif-action uif-boxLayoutVerticalItem pull-left clearfix']");
    	waitForElementPresentByXpath("//button[@class='btn btn-primary btn-lg uif-action uif-boxLayoutVerticalItem pull-left clearfix disabled' and @disabled]");
    	waitForElementPresentByXpath("//button[@class='btn btn-default btn-lg uif-action uif-boxLayoutVerticalItem pull-left clearfix']");
    	waitForElementPresentByXpath("//button[@class='btn btn-default btn-lg uif-action uif-boxLayoutVerticalItem pull-left clearfix disabled' and @disabled]");
    	
    	//Assertion of Small Buttons
    	waitForElementPresentByXpath("//button[@class='btn btn-primary btn-sm uif-action uif-boxLayoutVerticalItem pull-left clearfix']");
    	waitForElementPresentByXpath("//button[@class='btn btn-primary btn-sm uif-action uif-boxLayoutVerticalItem pull-left clearfix disabled' and @disabled]");
    	waitForElementPresentByXpath("//button[@class='btn btn-default btn-sm uif-action uif-boxLayoutVerticalItem pull-left clearfix']");
    	waitForElementPresentByXpath("//button[@class='btn btn-default btn-sm uif-action uif-boxLayoutVerticalItem pull-left clearfix disabled' and @disabled]");
    	
    	//Assertion of Mini Buttons
    	waitForElementPresentByXpath("//button[@class='btn btn-primary btn-xs uif-action uif-boxLayoutVerticalItem pull-left clearfix']");
    	waitForElementPresentByXpath("//button[@class='btn btn-primary btn-xs uif-action uif-boxLayoutVerticalItem pull-left clearfix disabled' and @disabled]");
    	waitForElementPresentByXpath("//button[@class='btn btn-default btn-xs uif-action uif-boxLayoutVerticalItem pull-left clearfix']");
    	waitForElementPresentByXpath("//button[@class='btn btn-default btn-xs uif-action uif-boxLayoutVerticalItem pull-left clearfix disabled' and @disabled]");
    	
    	//ICON Examples
    	waitForElementPresentByXpath("//button[@class='btn btn-primary btn-lg uif-action uif-boxLayoutVerticalItem pull-left clearfix']/span[@class='icon-ok']");
    	waitForElementPresentByXpath("//button[@class='btn btn-primary uif-action uif-boxLayoutVerticalItem pull-left clearfix']/span[@class='icon-ok']");
    	waitForElementPresentByXpath("//button[@class='btn btn-primary btn-sm uif-action uif-boxLayoutVerticalItem pull-left clearfix']/span[@class='icon-ok']");
    	waitForElementPresentByXpath("//button[@class='btn btn-primary btn-xs uif-action uif-boxLayoutVerticalItem pull-left clearfix']/span[@class='icon-ok']");
    	waitForElementPresentByXpath("//button[@class='btn btn-primary btn-lg uif-action uif-boxLayoutVerticalItem pull-left clearfix icon-ok']");
    	waitForElementPresentByXpath("//button[@class='btn btn-primary uif-action uif-boxLayoutVerticalItem pull-left clearfix icon-ok']");
    	waitForElementPresentByXpath("//button[@class='btn btn-primary btn-sm uif-action uif-boxLayoutVerticalItem pull-left clearfix icon-ok']");
    	waitForElementPresentByXpath("//button[@class='btn btn-primary btn-xs uif-action uif-boxLayoutVerticalItem pull-left clearfix icon-ok']");
    }

    protected void testActionIcons() throws Exception {
    	waitAndClickByXpath("//a[@href='#Demo-Action-Example6_tabPanel' and contains(text(),'Icons')]");
    	waitForElementPresentByXpath("//button[@class='btn btn-primary uif-action uif-boxLayoutVerticalItem pull-left clearfix']/span[@class='icon-office']");
    	waitForElementPresentByXpath("//button[@class='btn btn-primary uif-action uif-boxLayoutVerticalItem pull-left clearfix icon-office']");
    }
    
    private void testAllActionTabs() throws Exception {
        testActionDefault();
        testActionSuccessCallback();
        testActionValidation();
        testActionImages();
        testActionButton();
        testActionStyleVarities();
        testActionIcons();
        testActionPresubmit(); // last because it is failing https://jira.kuali.org/browse/KULRICE-10961 Library Action Presubmit Pre submit returning true Link redirects to Library Action Default
    }

    @Test
    public void testActionBookmark() throws Exception {
        testAllActionTabs();
        passed();
    }

    @Test
    public void testActionNav() throws Exception {
        testAllActionTabs();
        passed();
    }
}
