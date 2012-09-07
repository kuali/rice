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
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Base class for Selenium Webdriver integration tests
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class WebDriverITBase {

    protected int DEFAULT_IMPLICIT_WAIT_TIME = 30;
    protected int SHORT_IMPLICIT_WAIT_TIME = 1;

    public WebDriver driver;
    static ChromeDriverService chromeDriverService;

    /**
     * Returns the URL to be used with this test
     *
     * @return URL of the test
     */
    public abstract String getTestUrl();

    /**
     * Override in test to define a user other than admin
     * @return
     */
    public String getUserName() {
        return "admin";
    }

    @BeforeClass
    public static void createAndStartService() throws Exception {
        String driverParam = System.getProperty("remote.public.driver");
        if (driverParam != null && "chrome".equals(driverParam.toLowerCase())) {
            if (System.getProperty("webdriver.chrome.driver") == null) {
                if (System.getProperty("remote.public.chrome") != null) {
                    System.setProperty("webdriver.chrome.driver", System.getProperty("remote.public.chrome"));
                }
            }
            ChromeDriverService chromeDriverService = new ChromeDriverService.Builder()
                    .usingChromeDriverExecutable(new File(System.getProperty("remote.public.chrome")))
                    .usingAnyFreePort()
                    .build();
            chromeDriverService.start();
        }
    }


    /**
     * Setup the WebDriver test, login and load the tested web page
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        driver = ITUtil.getWebDriver();
        driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT_TIME, TimeUnit.SECONDS);
        String userName = getUserName();
        driver.get(ITUtil.getBaseUrlString() + getTestUrl());
        // Login
        ITUtil.login(driver, userName);
    }

    /**
     * Tear down the WebDriver test
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        if (ITUtil.dontTearDownPropertyNotSet()) {
            driver.quit(); // TODO not tested with chrome, the service stop might need this check too
        }
    }

    /**
     * Tear down the WebDriver test
     *
     * @throws Exception
     */
    @AfterClass
    public static void stopService() throws Exception {
        if (chromeDriverService != null) {
            chromeDriverService.stop();
        }
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

