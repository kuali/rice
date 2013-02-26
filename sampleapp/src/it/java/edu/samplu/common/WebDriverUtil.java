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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class WebDriverUtil {

    public static int DEFAULT_IMPLICIT_WAIT_TIME = 30;
    public static int SHORT_IMPLICIT_WAIT_TIME = 1;
    public static final String REMOTE_DRIVER_SAUCELABS_PROPERTY = "remote.driver.saucelabs";

    /**
     *
     * @param username
     * @param url
     * @return
     * @throws Exception
     */
    public static WebDriver setUp(String username, String url) throws Exception {
        return setUp(username, url, null, null);
    }

    /**
     * Setup the WebDriver test, login and load the tested web page
     *
     * @throws Exception
     */
    public static WebDriver setUp(String username, String url, String className, TestName testName) throws Exception {
        WebDriver driver = null;
        if (System.getProperty(REMOTE_DRIVER_SAUCELABS_PROPERTY) == null) {
            driver = ITUtil.getWebDriver();
        } else {
            SauceLabsWebDriverHelper saucelabs = new SauceLabsWebDriverHelper();
            saucelabs.setUp(className, testName);
            driver = saucelabs.getDriver();
        }
        driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
        driver.get(url);
        driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT_TIME, TimeUnit.SECONDS);
        return driver;
    }

    public static void checkForIncidentReport(WebDriver driver, String locator) {
        checkForIncidentReport(driver, locator, "");
    }

    public static void checkForIncidentReport(WebDriver driver, String locator, String message) {
        ITUtil.checkForIncidentReport(driver.getPageSource(), locator, message);
    }

    public static ChromeDriverService createAndStartService() {
        String driverParam = System.getProperty(ITUtil.HUB_DRIVER_PROPERTY);
        // TODO can the saucelabs driver stuff be leveraged here?
        if (driverParam != null && "chrome".equals(driverParam.toLowerCase())) {
            if (System.getProperty("webdriver.chrome.driver") == null) {
                if (System.getProperty("remote.public.chrome") != null) {
                    System.setProperty("webdriver.chrome.driver", System.getProperty("remote.public.chrome"));
                }
            }
            try {
                ChromeDriverService chromeDriverService = new ChromeDriverService.Builder()
                        .usingChromeDriverExecutable(new File(System.getProperty("webdriver.chrome.driver")))
                        .usingAnyFreePort()
                        .build();
                return chromeDriverService;
            } catch (Throwable t) {
                throw new RuntimeException("Exception starting chrome driver service, is chromedriver ( http://code.google.com/p/chromedriver/downloads/list ) installed? You can include the path to it using -Dremote.public.chrome", t)   ;
            }
        }
        return null;
    }
}
