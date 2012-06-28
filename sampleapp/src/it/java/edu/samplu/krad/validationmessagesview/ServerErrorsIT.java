/*
 * Copyright 2006-2012 The Kuali Foundation
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

package edu.samplu.krad.validationmessagesview;

import com.thoughtworks.selenium.Selenium;
import junit.framework.Assert;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServerErrorsIT {
    private Selenium selenium;

    @Before
    public void setUp() throws Exception {
        WebDriver driver = new FirefoxDriver();
        selenium = new WebDriverBackedSelenium(driver,
                "http://localhost:8080/kr-dev/kr-krad/uicomponents?viewId=Demo-ValidationLayout&methodToCall=start");

        // Login
        selenium.open(
                "http://localhost:8080/kr-dev/kr-krad/uicomponents?viewId=Demo-ValidationLayout&methodToCall=start");
        Assert.assertEquals("Login", selenium.getTitle());
        selenium.type("__login_user", "admin");
        selenium.click("//input[@value='Login']");
        selenium.waitForPageToLoad("30000");
    }

    @Test
    public void testServerErrorsIT() throws Exception {
        selenium.click("//button[contains(.,'Get Error Messages')]");
        selenium.waitForPageToLoad("30000");
        Assert.assertTrue(selenium.isVisible("css=div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"]"));
        Assert.assertTrue(selenium.isElementPresent(
                "css=div[data-messagesfor=\"Demo-ValidationLayout-SectionsPage\"] .uif-errorMessageItem"));
        Assert.assertTrue(selenium.isVisible("css=div[data-messagesfor=\"Demo-ValidationLayout-Section1\"]"));
        Assert.assertTrue(selenium.isElementPresent(
                "css=div[data-messagesfor=\"Demo-ValidationLayout-Section1\"] .uif-errorMessageItem"));
        Assert.assertTrue(selenium.isElementPresent("css=div[data-role=\"InputField\"] img[alt=\"Error\"]"));
        selenium.click("//a[contains(.,'\"Section 1 Title\"')]");
        selenium.mouseOver("//a[contains(.,'Field 1')]");
        Assert.assertTrue(selenium.isElementPresent("css=.uif-errorHighlight"));
        selenium.click("//a[contains(.,'Field 1')]");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (selenium.isVisible("css=.jquerybubblepopup-innerHtml")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(selenium.isVisible("css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems"));
        Assert.assertTrue(selenium.isVisible(
                "css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-errorMessageItem-field"));
        selenium.type("name=field1", "");
        selenium.fireEvent("name=field1", "blur");
        selenium.fireEvent("name=field1", "focus");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (selenium.isVisible("css=.jquerybubblepopup-innerHtml")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(selenium.isVisible(
                "css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-errorMessageItem-field"));
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (selenium.isVisible("css=.jquerybubblepopup-innerHtml > .uif-clientMessageItems")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(selenium.isVisible(
                "css=.jquerybubblepopup-innerHtml > .uif-clientMessageItems  .uif-errorMessageItem-field"));
        selenium.keyDown("name=field1", "t");
        selenium.keyPress("name=field1", "t");
        selenium.keyUp("name=field1", "t");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (!selenium.isElementPresent("css=.jquerybubblepopup-innerHtml > .uif-clientMessageItems")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(selenium.isVisible(
                "css=.jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-errorMessageItem-field"));
        Assert.assertFalse(selenium.isElementPresent("css=.jquerybubblepopup-innerHtml > .uif-clientMessageItems"));
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
