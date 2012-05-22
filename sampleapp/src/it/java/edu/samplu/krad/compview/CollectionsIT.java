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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Selenium test that tests collections
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CollectionsIT {
    private Selenium selenium;

    @Before
    public void setUp() throws Exception {
        WebDriver driver = new FirefoxDriver();
        String baseUrl = "http://localhost:8080/";
        selenium = new WebDriverBackedSelenium(driver, baseUrl);

        // open Collections page in kitchen sink view
        selenium.open(
                "http://localhost:8080/kr-dev/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page7");
        selenium.type("name=__login_user", "admin");
        selenium.click("//input[@value='Login']");
        selenium.waitForPageToLoad("30000");
    }

    @Test
    public void testActionColumnLeft() throws Exception {
        // check if actions column is LEFT
        Assert.assertTrue(selenium.isElementPresent("//div[@id='collection2']//tr[2]/td[1]//button[contains(.,\"delete\")]"));
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }

}
