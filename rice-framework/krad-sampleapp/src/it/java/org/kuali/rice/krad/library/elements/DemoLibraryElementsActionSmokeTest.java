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
package org.kuali.rice.krad.library.elements;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;

import com.thoughtworks.selenium.SeleneseTestBase;

import org.kuali.rice.testtools.selenium.SmokeTestBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLibraryElementsActionSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/kradsampleapp?viewId=Demo-Action-View&methodToCall=start
     */
    public static final String BOOKMARK_URL = "/kr-krad/kradsampleapp?viewId=Demo-Action-View&methodToCall=start";

    @Override
    protected String getBookmarkUrl() {
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
        assertIsNotVisibleByXpath("//a[contains(text(),'Required')]");

        waitAndClickByLinkText("Action Link with clientside validation");
        assertIsVisibleByXpath("//a[contains(text(),'Required')]", "");

        waitAndTypeByName("inputField1", "some text");
        waitAndClickByLinkText("Action Link with clientside validation");
        assertIsNotVisibleByXpath("//a[contains(text(),'Required')]");
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
    
    @Test
    public void testLibraryElementsActionBookmark() throws Exception {
        testActionDefault();
        testActionPresubmit();
        testActionSuccessCallback();
        testActionValidation();
        testActionImages();
        passed();
    }

    @Test
    public void testLibraryElementsActionNav() throws Exception {
        testActionDefault();
        testActionPresubmit();
        testActionSuccessCallback();
        testActionValidation();
        testActionImages();
        passed();
    }  
}