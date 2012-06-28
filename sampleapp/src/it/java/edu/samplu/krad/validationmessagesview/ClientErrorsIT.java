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

public class ClientErrorsIT {
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
    public void testClientErrors() throws Exception {
        selenium.fireEvent("name=field1", "focus");
        selenium.type("name=field1", "");
        selenium.fireEvent("name=field1", "blur");
        Assert.assertEquals("true", selenium.getAttribute("name=field1@aria-invalid"));
        Assert.assertTrue(selenium.getAttribute("name=field1@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(selenium.isElementPresent("//textarea[@name='field1']/../img[@alt='Error']"));
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
                "css=.jquerybubblepopup-innerHtml > .uif-clientMessageItems  .uif-errorMessageItem-field"));
        selenium.type("name=field1", "a");
        selenium.keyDown("name=field1", "b");
        selenium.keyUp("name=field1", "b");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (!selenium.isVisible("css=.jquerybubblepopup-innerHtml")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertFalse(selenium.isVisible("css=.jquerybubblepopup-innerHtml > .uif-clientMessageItems"));
        selenium.fireEvent("name=field1", "blur");
        Assert.assertFalse(selenium.isElementPresent("name=field1@aria-invalid"));
        Assert.assertTrue(selenium.getAttribute("name=field1@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertFalse(selenium.isElementPresent("//textarea[@name='field1']/../img[@alt='Error']"));
        selenium.fireEvent("name=field2", "focus");
        selenium.type("name=field2", "");
        selenium.fireEvent("name=field2", "blur");
        Assert.assertEquals("true", selenium.getAttribute("name=field2@aria-invalid"));
        Assert.assertTrue(selenium.getAttribute("name=field2@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(selenium.isElementPresent("//input[@name='field2']/../img[@alt='Error']"));
        selenium.fireEvent("name=field2", "focus");
        selenium.type("name=field2", "a");
        selenium.fireEvent("name=field2", "blur");
        Assert.assertFalse(selenium.isElementPresent("name=field2@aria-invalid"));
        Assert.assertTrue(selenium.getAttribute("name=field2@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertFalse(selenium.isElementPresent("//textarea[@name='field2']/../img[@alt='Error']"));
        selenium.fireEvent("name=field3", "focus");
        selenium.select("name=field3", "");
        selenium.fireEvent("name=field3", "blur");
        Assert.assertEquals("true", selenium.getAttribute("name=field3@aria-invalid"));
        Assert.assertTrue(selenium.getAttribute("name=field3@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(selenium.isElementPresent("//select[@name='field3']/../img[@alt='Error']"));
        selenium.fireEvent("name=field3", "focus");
        selenium.select("name=field3", "Option 1");
        selenium.fireEvent("name=field3", "blur");
        Assert.assertFalse(selenium.isElementPresent("name=field3@aria-invalid"));
        Assert.assertTrue(selenium.getAttribute("name=field3@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertFalse(selenium.isElementPresent("//select[@name='field3']/../img[@alt='Error']"));
        selenium.fireEvent("name=field4", "focus");
        selenium.removeAllSelections("name=field4");
        selenium.fireEvent("name=field4", "blur");
        Assert.assertEquals("true", selenium.getAttribute("name=field4@aria-invalid"));
        Assert.assertTrue(selenium.getAttribute("name=field4@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(selenium.isElementPresent("//select[@name='field4']/../img[@alt='Error']"));
        selenium.fireEvent("name=field4", "focus");
        selenium.select("name=field4", "Option 1");
        selenium.fireEvent("name=field4", "blur");
        Assert.assertFalse(selenium.isElementPresent("name=field4@aria-invalid"));
        Assert.assertTrue(selenium.getAttribute("name=field4@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertFalse(selenium.isElementPresent("//select[@name='field4']/../img[@alt='Error']"));
        selenium.fireEvent("name=field117 value=3", "focus");
        selenium.uncheck("name=field117 value=3");
        selenium.fireEvent("name=field117", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (selenium.isElementPresent("//input[@name='field117']/../../../img[@alt='Error']")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertEquals("true", selenium.getAttribute("name=field117 value=1@aria-invalid"));
        Assert.assertTrue(selenium.getAttribute("name=field117 value=1@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(selenium.isElementPresent("//input[@name='field117']/../../../img[@alt='Error']"));
        selenium.fireEvent("name=field117 value=3", "focus");
        selenium.check("name=field117 value=3");
        selenium.fireEvent("name=field117 value=3", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (!selenium.isElementPresent("//input[@name='field117']/../../../img[@alt='Error']")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertFalse(selenium.isElementPresent("name=field117 value=1@aria-invalid"));
        Assert.assertTrue(selenium.getAttribute("name=field117 value=3@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertFalse(selenium.isElementPresent("//input[@name='field117']/../../../img[@alt='Error']"));
        selenium.fireEvent("name=bField1", "focus");
        selenium.uncheck("name=bField1");
        selenium.fireEvent("name=bField1", "blur");
        Assert.assertEquals("true", selenium.getAttribute("name=bField1@aria-invalid"));
        Assert.assertTrue(selenium.getAttribute("name=bField1@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(selenium.isElementPresent(
                "//input[@name='bField1' and following-sibling::img[@alt='Error']]"));
        selenium.fireEvent("name=bField1", "focus");
        selenium.check("name=bField1");
        selenium.fireEvent("name=bField1", "blur");
        Assert.assertFalse(selenium.isElementPresent("name=bField1@aria-invalid"));
        Assert.assertTrue(selenium.getAttribute("name=bField1@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertFalse(selenium.isElementPresent(
                "//input[@name='bField1' and following-sibling::img[@alt='Error']]"));
        selenium.fireEvent("name=field114 value=3", "focus");
        selenium.uncheck("name=field114 value=3");
        selenium.uncheck("name=field114 value=4");
        selenium.fireEvent("name=field114", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (selenium.isElementPresent("//input[@name='field114']/../../../img[@alt='Error']")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertEquals("true", selenium.getAttribute("name=field114 value=1@aria-invalid"));
        Assert.assertTrue(selenium.getAttribute("name=field114 value=1@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(selenium.isElementPresent("//input[@name='field114']/../../../img[@alt='Error']"));
        selenium.fireEvent("name=field114 value=3", "focus");
        selenium.check("name=field114 value=3");
        selenium.check("name=field114 value=4");
        selenium.fireEvent("name=field114", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (!selenium.isElementPresent("//input[@name='field114']/../../../img[@alt='Error']")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertFalse(selenium.isElementPresent("name=field114 value=1@aria-invalid"));
        Assert.assertFalse(selenium.isElementPresent("//input[@name='field114']/../../../img[@alt='Error']"));
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
