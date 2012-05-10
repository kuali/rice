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

package edu.samplu.krad.compview;

import com.thoughtworks.selenium.Selenium;
import junit.framework.Assert;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ContraintsIT {
    private Selenium selenium;

    @Before
    public void setUp() throws Exception {
        WebDriver driver = new FirefoxDriver();
        selenium = new WebDriverBackedSelenium(driver,
                "http://localhost:8080/kr-dev/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page3");

        // Login
        selenium.open(
                "http://localhost:8080/kr-dev/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page3");
        Assert.assertEquals("Login", selenium.getTitle());
        selenium.type("__login_user", "admin");
        selenium.click("//input[@value='Login']");
        selenium.waitForPageToLoad("30000");
    }

    @Test
    public void testContraintsIT() throws Exception {
        selenium.focus("name=field9");
        selenium.type("name=field9", "1");
        selenium.fireEvent("name=field9", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field9@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.focus("name=field9");
        selenium.type("name=field9", "12345");
        selenium.fireEvent("name=field9", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field9@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.focus("name=field10");
        selenium.type("name=field10", "2");
        selenium.fireEvent("name=field10", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field10@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.focus("name=field10");
        selenium.type("name=field10", "51");
        selenium.fireEvent("name=field10", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field10@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.focus("name=field10");
        selenium.type("name=field10", "25");
        selenium.fireEvent("name=field10", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field10@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.focus("name=field6");
        selenium.type("name=field6", "A");
        selenium.fireEvent("name=field6", "blur");
        selenium.fireEvent("name=field7", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field7@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.type("name=field7", "B");
        selenium.fireEvent("name=field7", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field7@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.fireEvent("name=field8", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field8@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.type("name=field8", "C");
        selenium.fireEvent("name=field8", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field8@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.type("name=field6", "");
        selenium.fireEvent("name=field6", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field6@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.type("name=field7", "");
        selenium.fireEvent("name=field7", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field7@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.type("name=field8", "");
        selenium.fireEvent("name=field8", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field6@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(selenium.getAttribute("name=field7@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(selenium.getAttribute("name=field8@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.type("name=field8", "C");
        selenium.fireEvent("name=field8", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field6@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(selenium.getAttribute("name=field7@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(selenium.getAttribute("name=field8@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.type("name=field6", "A");
        selenium.fireEvent("name=field6", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field6@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(selenium.getAttribute("name=field7@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        Assert.assertTrue(selenium.getAttribute("name=field8@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.type("name=field14", "A");
        selenium.fireEvent("name=field14", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field14@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.type("name=field11", "A");
        selenium.fireEvent("name=field11", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field11@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(selenium.getAttribute("name=field14@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.type("name=field11", "");
        selenium.fireEvent("name=field11", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field14@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.type("name=field12", "A");
        selenium.fireEvent("name=field12", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field14@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.type("name=field13", "A");
        selenium.fireEvent("name=field13", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field13@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(selenium.getAttribute("name=field14@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.type("name=field11", "A");
        selenium.fireEvent("name=field11", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field11@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(selenium.getAttribute("name=field14@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.type("name=field18", "A");
        selenium.fireEvent("name=field18", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field18@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.type("name=field15", "A");
        selenium.fireEvent("name=field15", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field15@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(selenium.getAttribute("name=field18@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.type("name=field15", "");
        selenium.fireEvent("name=field15", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field18@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.type("name=field16", "A");
        selenium.fireEvent("name=field16", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field18@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.type("name=field17", "A");
        selenium.fireEvent("name=field17", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field17@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        Assert.assertTrue(selenium.getAttribute("name=field18@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.type("name=field15", "A");
        selenium.fireEvent("name=field15", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field18@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.type("name=field23", "A");
        selenium.fireEvent("name=field23", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field23@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.type("name=field19", "A");
        selenium.fireEvent("name=field19", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field23@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.type("name=field19", "");
        selenium.fireEvent("name=field19", "blur");
        selenium.type("name=field20", "B");
        selenium.fireEvent("name=field20", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field23@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.type("name=field20", "");
        selenium.fireEvent("name=field20", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field23@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.type("name=field21", "C");
        selenium.fireEvent("name=field21", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field23@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.type("name=field22", "D");
        selenium.fireEvent("name=field22", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field23@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.type("name=field19", "D");
        selenium.fireEvent("name=field19", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field23@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.type("name=field20", "D");
        selenium.fireEvent("name=field20", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field23@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.check("name=field24 value=case1");
        selenium.type("name=field25", "");
        selenium.fireEvent("name=field25", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field25@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.check("name=field24 value=case4");
        selenium.fireEvent("name=field24", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (selenium.getAttribute("name=field25@class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(selenium.getAttribute("name=field25@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.check("name=field24 value=case1");
        selenium.fireEvent("name=field24", "blur");
        selenium.type("name=field25", "$100");
        selenium.fireEvent("name=field25", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (selenium.getAttribute("name=field25@class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(selenium.getAttribute("name=field25@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.check("name=field24 value=case2");
        selenium.fireEvent("name=field24", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (selenium.getAttribute("name=field25@class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(selenium.getAttribute("name=field25@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.type("name=field25", "A100");
        selenium.fireEvent("name=field25", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field25@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.check("name=field24 value=case3");
        selenium.fireEvent("name=field24", "blur");
        selenium.type("name=field26", "6000");
        selenium.fireEvent("name=field26", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (selenium.getAttribute("name=field26@class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(selenium.getAttribute("name=field26@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.type("name=field26", "501");
        selenium.fireEvent("name=field26", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field26@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.type("name=field26", "499");
        selenium.fireEvent("name=field26", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field26@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.type("name=field26", "6000");
        selenium.fireEvent("name=field26", "blur");
        selenium.check("name=field24 value=case3");
        selenium.fireEvent("name=field24", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (selenium.getAttribute("name=field26@class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(selenium.getAttribute("name=field26@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.check("name=field24 value=case4");
        selenium.type("name=field27", "A");
        selenium.fireEvent("name=field27", "blur");
        selenium.type("name=field28", "");
        selenium.fireEvent("name=field28", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (selenium.getAttribute("name=field28@class").matches("^[\\s\\S]*error[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(selenium.getAttribute("name=field28@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.check("name=field24 value=case3");
        selenium.fireEvent("name=field24", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (selenium.getAttribute("name=field28@class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(selenium.getAttribute("name=field28@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.type("name=field28", "B");
        selenium.fireEvent("name=field28", "blur");
        selenium.check("name=field24 value=case4");
        selenium.fireEvent("name=field24", "blur");
        for (int second = 0; ; second++) {
            if (second >= 60) {
                Assert.fail("timeout");
            }
            try {
                if (selenium.getAttribute("name=field28@class").matches("^[\\s\\S]*valid[\\s\\S]*$")) {
                    break;
                }
            } catch (Exception e) {
            }
            Thread.sleep(1000);
        }

        Assert.assertTrue(selenium.getAttribute("name=field28@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.type("name=field31", "B");
        selenium.type("name=field32", "B");
        selenium.fireEvent("name=field33", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field33@class").matches("^[\\s\\S]*error[\\s\\S]*$"));
        selenium.type("name=field33", "B");
        selenium.fireEvent("name=field33", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field33@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
        selenium.type("name=field32", "A");
        selenium.type("name=field33", "");
        selenium.fireEvent("name=field33", "blur");
        Assert.assertTrue(selenium.getAttribute("name=field33@class").matches("^[\\s\\S]*valid[\\s\\S]*$"));
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }
}
