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

package edu.samplu.common;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Base class for Selenium Webdriver integration tests
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class WebDriverITBase {

    int DEFAULT_IMPLICIT_WAIT_TIME = 30;
    int SHORT_IMPLICIT_WAIT_TIME = 1;

    public WebDriver driver;

    /**
     * Returns the URL to be used with this test
     *
     * @return URL of the test
     */
    public abstract String getTestUrl();

    /**
     * Setup the WebDriver test, login and load the tested web page
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setEnableNativeEvents(false);
        driver = new FirefoxDriver(profile);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        // Login
        driver.get(getTestUrl());
        driver.findElement(By.name("__login_user")).clear();
        driver.findElement(By.name("__login_user")).sendKeys("admin");
        driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
    }

    /**
     * Tear down the WebDriver test
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        driver.quit();
    }

    /**
     * Check if an element is present
     *
     * <p>
     * This test takes a while due to the 'implicit wait' time.
     * </p>
     *
     * @param by The locating mechanism of the element
     * @return true if the element is present, false otherwise
     */
    public boolean isElementPresent(By by) {
        if (driver.findElements(by).isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Quickly check if an element is present
     *
     * <p>
     * Just like {@link #isElementPresent(org.openqa.selenium.By)} but with a short 'implicit wait' time.  Use this only
     * if it is guaranteed that all elements are rendered.
     * </p>
     *
     * @param by The locating mechanism of the element
     * @return true if the element is present, false otherwise
     */
    public boolean isElementPresentQuick(By by) {
        driver.manage().timeouts().implicitlyWait(SHORT_IMPLICIT_WAIT_TIME, TimeUnit.SECONDS);
        boolean result = isElementPresent(by);
        driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT_TIME, TimeUnit.SECONDS);
        return result;
    }

    /**
     * Assert that clicking an element causes a popup window with a specific URL
     *
     * @param by The locating mechanism of the element to be clicked
     * @param windowName The name of the popup window
     * @param url The URL of the popup window
     */
    public void assertPopUpWindowUrl(By by, String windowName, String url) {
        driver.findElement(by).click();
        String parentWindowHandle = driver.getWindowHandle();
        // wait page to be loaded
        driver.switchTo().window(windowName).findElements(By.tagName("head"));
        assertEquals(url, driver.getCurrentUrl());
        driver.switchTo().window(parentWindowHandle);
    }

}
