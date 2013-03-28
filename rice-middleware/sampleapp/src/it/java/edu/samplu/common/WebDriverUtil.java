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

import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * The goal of the WebDriverUtil class is to invert the dependencies on WebDriver from WebDriverLegacyITBase for reuse
 * without having to extend WebDriverLegacyITBase.  For the first example see waitFor
 *
 * @see WebDriverLegacyITBase
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WebDriverUtil {

    /**
     * TODO apparent dup WebDriverITBase.DEFAULT_WAIT_SEC
     * TODO parametrize for JVM Arg
     * 30 Seconds
     */
    public static int DEFAULT_IMPLICIT_WAIT_TIME = 30;

    /**
     * TODO introduce SHORT_IMPLICIT_WAIT_TIME with param in WebDriverITBase
     * TODO parametrize for JVM Arg
     * 1 Second
     */
    public static int SHORT_IMPLICIT_WAIT_TIME = 1;

    /**
     * Set -Dremote.driver.saucelabs for running on saucelabs
     * @link https://wiki.kuali.org/display/KULRICE/How+To+Run+a+Selenium+Test for patch required
     */
    public static final String REMOTE_DRIVER_SAUCELABS_PROPERTY = "remote.driver.saucelabs";

    /**
     * Selenium's webdriver.chrome.driver parameter, you can set -Dwebdriver.chrome.driver= or Rice's REMOTE_PUBLIC_CHROME
     */
    public static final String WEBDRIVER_CHROME_DRIVER = "webdriver.chrome.driver";

    /**
     * Set -Dremote.public.chrome= or WEBDRIVER_CHROME_DRIVER
     */
    public static final String REMOTE_PUBLIC_CHROME = "remote.public.chrome";

    /**
     * Time to wait for the URL used in setup to load.  Sometimes this is the first hit on the app and it needs a bit
     * longer than any other.  120 Seconds.
     * TODO parametrize for JVM Arg
     */
    public static final int SETUP_URL_LOAD_WAIT_SECONDS = 120;

    /**
     * Setup the WebDriver test, login, and load the given web page
     *
     * @param username
     * @param url
     * @return driver
     * @throws Exception
     */
    public static WebDriver setUp(String username, String url) throws Exception {
        return setUp(username, url, null, null);
    }

    /**
     * Setup the WebDriver test, login, and load the given web page
     *
     * @param username
     * @param url
     * @param className
     * @param testName
     * @return driver
     * @throws Exception
     */
    public static WebDriver setUp(String username, String url, String className, TestName testName) throws Exception {
        WebDriver driver = null;
        if (System.getProperty(REMOTE_DRIVER_SAUCELABS_PROPERTY) == null) {
            driver = ITUtil.getWebDriver();
//        } else {
//            SauceLabsWebDriverHelper saucelabs = new SauceLabsWebDriverHelper();
//            saucelabs.setUp(className, testName);
//            driver = saucelabs.getDriver();
        }
        driver.manage().timeouts().implicitlyWait(SETUP_URL_LOAD_WAIT_SECONDS, TimeUnit.SECONDS);
        // TODO Got into the situation where the first url doesn't expect server, but all others do.  Readdress once
        // the NavIT WDIT conversion has been completed.
        if (!url.startsWith("http")) {
            url = ITUtil.getBaseUrlString() + url;
        }
        driver.get(url);
        driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT_TIME, TimeUnit.SECONDS);
        return driver;
    }

    /**
     *
     * @param passed
     * @param sessionId
     * @param testParam
     * @param userParam
     * @throws Exception
     */
    public static void tearDown(boolean passed, String sessionId, String testParam, String userParam) throws Exception {

//        if (System.getProperty(SauceLabsWebDriverHelper.SAUCE_PROPERTY) != null) {
//            SauceLabsWebDriverHelper.tearDown(passed, sessionId, System.getProperty(SauceLabsWebDriverHelper.SAUCE_USER_PROPERTY),
//                    System.getProperty(SauceLabsWebDriverHelper.SAUCE_KEY_PROPERTY));
//        }

        if (System.getProperty(WebDriverLegacyITBase.REMOTE_PUBLIC_USERPOOL_PROPERTY) != null) {
            getHTML(ITUtil.prettyHttp(System.getProperty(WebDriverLegacyITBase.REMOTE_PUBLIC_USERPOOL_PROPERTY) + "?test="
                    + testParam + "&user=" + userParam));
        }
    }

    /**
     *
     * @param testParam
     * @return
     */
    public static String determineUser(String testParam) {
        String user = null;
        if (System.getProperty(WebDriverLegacyITBase.REMOTE_PUBLIC_USER_PROPERTY) != null) {
            return System.getProperty(WebDriverLegacyITBase.REMOTE_PUBLIC_USER_PROPERTY);
        } else if (System.getProperty(WebDriverLegacyITBase.REMOTE_PUBLIC_USERPOOL_PROPERTY) != null) { // deprecated
            String userResponse = WebDriverUtil.getHTML(ITUtil.prettyHttp(System.getProperty(
                    WebDriverLegacyITBase.REMOTE_PUBLIC_USERPOOL_PROPERTY) + "?test=" + testParam.trim()));
            return userResponse.substring(userResponse.lastIndexOf(":") + 2, userResponse.lastIndexOf("\""));
        }
        return user;
    }

    /**
     * @link ITUtil#checkForIncidentReport
     * @param driver
     * @param locator
     */
    public static void checkForIncidentReport(WebDriver driver, String locator) {
        checkForIncidentReport(driver, locator, "");
    }

    /***
     * @link ITUtil#checkForIncidentReport
     * @param driver
     * @param locator
     * @param message
     */
    public static void checkForIncidentReport(WebDriver driver, String locator, String message) {
        ITUtil.checkForIncidentReport(driver.getPageSource(), locator, message);
    }

    /***
     * @link ITUtil#checkForIncidentReport
     * @param driver
     * @param locator
     * @param message
     */
    public static void checkForIncidentReport(WebDriver driver, String locator, Failable failable,
            String message) {
        ITUtil.checkForIncidentReport(driver.getPageSource(), locator, failable, message);
    }

    /**
     * @link http://code.google.com/p/chromedriver/downloads/list
     * @link #REMOTE_PUBLIC_CHROME
     * @link #WEBDRIVER_CHROME_DRIVER
     * @link ITUtil#HUB_DRIVER_PROPERTY
     * @return chromeDriverService
     */
    public static ChromeDriverService chromeDriverCreateCheck() {
        String driverParam = System.getProperty(ITUtil.HUB_DRIVER_PROPERTY);
        // TODO can the saucelabs driver stuff be leveraged here?
        if (driverParam != null && "chrome".equals(driverParam.toLowerCase())) {
            if (System.getProperty(WEBDRIVER_CHROME_DRIVER) == null) {
                if (System.getProperty(REMOTE_PUBLIC_CHROME) != null) {
                    System.setProperty(WEBDRIVER_CHROME_DRIVER, System.getProperty(REMOTE_PUBLIC_CHROME));
                }
            }
            try {
                ChromeDriverService chromeDriverService = new ChromeDriverService.Builder()
                        .usingChromeDriverExecutable(new File(System.getProperty(WEBDRIVER_CHROME_DRIVER)))
                        .usingAnyFreePort()
                        .build();
                return chromeDriverService;
            } catch (Throwable t) {
                throw new RuntimeException("Exception starting chrome driver service, is chromedriver ( http://code.google.com/p/chromedriver/downloads/list ) installed? You can include the path to it using -Dremote.public.chrome", t)   ;
            }
        }
        return null;
    }

    public static String getHTML(String urlToRead) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";

        try {
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    protected static void selectFrameSafe(WebDriver driver, String locator) {
        try {
            driver.switchTo().frame(locator);
        } catch (NoSuchFrameException nsfe) {
            // don't fail
        }
    }

    /**
     * Wait for the given amount of seconds, for the given by, using the given driver.  The message is displayed if the
     * by cannot be found.  No action is performed on the by, so it is possible that the by found is not visible or enabled.
     *
     * @param driver WebDriver
     * @param waitSeconds int
     * @param by By
     * @param message String
     * @throws InterruptedException
     */
    public static void waitFor(WebDriver driver, int waitSeconds, By by, String message) throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(waitSeconds, TimeUnit.SECONDS);
        Thread.sleep(1000);
        driver.findElement(by);  // NOTICE just the find, no action, so by is found, but might not be visiable or enabled.
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
    }
}
