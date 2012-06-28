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
 * Selenium test that tests that tooltips are rendered on mouse over and focus events
 * and hidden on mouse out and blur events
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifTooltipIT {
    private Selenium selenium;

    @Before
    public void setUp() throws Exception {
        WebDriver driver = new FirefoxDriver();
        String baseUrl = "http://localhost:8080/";
        selenium = new WebDriverBackedSelenium(driver, baseUrl);
    }

    @Test
    public void testTooltip() throws Exception {
        // open Other Examples page in kitchen sink view
        selenium.open(
                "http://localhost:8080/kr-dev/kr-krad/uicomponents?viewId=UifCompView&methodToCall=start&pageId=UifCompView-Page10");
        selenium.type("name=__login_user", "admin");
        selenium.click("//input[@value='Login']");
        selenium.waitForPageToLoad("30000");
        // check if tooltip opens on focus
        selenium.fireEvent("name=field1", "focus");
        Assert.assertTrue(selenium.isVisible(
                "//td[contains(.,\"This tooltip is triggered by focus or and mouse over.\")]"));
        // check if tooltip closed on blur
        selenium.fireEvent("name=field1", "blur");
        Assert.assertFalse(selenium.isVisible(
                "//td[contains(.,\"This tooltip is triggered by focus or and mouse over.\")]"));
        // check if tooltip opens on mouse over
        selenium.mouseOver("name=field2");
        Assert.assertTrue(selenium.isVisible(
                "//td[contains(.,\"This is a tool-tip with different position and tail options\")]"));
        // check if tooltip closed on mouse out
        selenium.mouseOut("name=field2");
        Assert.assertFalse(selenium.isVisible(
                "//td[contains(.,\"This is a tool-tip with different position and tail options\")]"));
        // check that default tooltip does not display when there are an error message on the field
        selenium.type("name=field1", "1");
        selenium.fireEvent("name=field1", "blur");
        selenium.fireEvent("name=field1", "focus");
        Assert.assertFalse(selenium.isVisible(
                "//td[contains(.,\"This tooltip is triggered by focus or and mouse over.\")]"));
    }

    @After
    public void tearDown() throws Exception {
        selenium.stop();
    }

}
