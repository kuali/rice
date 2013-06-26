/**
 * Copyright 2005-2013 The Kuali Foundation
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
package edu.samplu.krad.demo.uif.library.elements.action;

import com.thoughtworks.selenium.SeleneseTestBase;
import edu.samplu.common.Failable;
import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;
import org.openqa.selenium.By;

import static org.junit.Assert.assertTrue;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryElementsActionSmokeTestBase extends WebDriverLegacyITBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-Action-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-Action-View&methodToCall=start";

    @Override
    public String getTestUrl() {
        return ITUtil.KRAD_PORTAL;
    }

    protected void navigation() throws Exception {
        waitAndClickById("Demo-LibraryLink", "");
        waitAndClickByLinkText("Elements");
        waitAndClickByLinkText("Action");
    }

    protected void testActionDefault() throws Exception {
        waitForElementPresentByClassName("uif-headerText-span");
        assertTextPresent("Default");
        SeleneseTestBase.assertTrue(getTextByClassName("uif-instructionalMessage").contains(
                "Action with action script"));
        assertElementPresentByLinkText("Action Link");
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
        assertTrue(driver.switchTo().alert().getText().contains("Refresh called successfully"));
        driver.switchTo().alert().accept();
    }

    protected void testActionValidation() throws Exception {
        waitForElementPresentByClassName("uif-page"); // make sure the page is there before we use the driver
        driver.findElement(By.className("uif-page")).findElement(By.linkText("Validation")).click();

        waitForElementPresentByClassName("uif-headerText-span");
        assertTextPresent("Action Field with client side validation");
        assertTextPresent("InputField 1");
        assertTextNotPresent("InputField 1: Required"); // no validation error yet

        waitAndClickByLinkText("Action Link with clientside validation");
        assertTextPresent("InputField 1: Required"); // now we have a validation error

        waitAndTypeByName("inputField1", "some text");
        waitAndClickByLinkText("Action Link with clientside validation");
        assertTextNotPresent("InputField 1: Required"); // now the error goes away
    }

    protected void testActionImages() throws Exception {
        waitAndClickByLinkText("Images");
        waitForElementPresentByClassName("uif-headerText-span");
        assertTextPresent("Images");
        assertTextPresent("Action Field with images");

        driver.findElement(By.partialLinkText("Action Link with left image")).findElement(By.className(
                "leftActionImage"));
        driver.findElement(By.partialLinkText("Action Link with right image")).findElement(By.className(
                "rightActionImage"));
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

        waitAndClickById("imageTop");
        assertTrue(driver.switchTo().alert().getText().contains("You clicked a button"));
        driver.switchTo().alert().accept();

        driver.findElement(By.xpath("//span[contains(text(),'Image LEFT')]"));
        driver.findElement(By.id("imageLeft")).click();
        assertTrue(driver.switchTo().alert().getText().contains("You clicked a button"));
        driver.switchTo().alert().accept();

        driver.findElement(By.id("imageRight")).click();
        assertTrue(driver.switchTo().alert().getText().contains("You clicked a button"));
        driver.switchTo().alert().accept();

        driver.findElement(By.id("imageOnlyButton")).click();
        assertTrue(driver.switchTo().alert().getText().contains("You clicked a button"));
        driver.switchTo().alert().accept();

        driver.findElement(By.xpath("//button[contains(text(),'Disabled Button') and @disabled]/preceding-sibling::button/img"));
        driver.findElement(By.xpath("//button/img[contains(@alt,'Image Only button')]"));

        driver.findElement(By.xpath("//button[contains(text(),'Disabled Button') and @disabled]"));
    }

    private void testAllActionTabs() throws Exception {
        testActionDefault();
        testActionPresubmit();
        testActionSuccessCallback();
        testActionValidation();
        testActionImages();
        testActionButton();
    }

    public void testActionButtonBookmark(Failable failable) throws Exception {
        testActionButton();
        passed();
    }

    public void testActionBookmark(Failable failable) throws Exception {
        testAllActionTabs();
        passed();
    }

    public void testActionNav(Failable failable) throws Exception {
        navigation();
        testAllActionTabs();
        passed();
    }

}
