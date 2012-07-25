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

import com.thoughtworks.selenium.Selenium;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;

import static junit.framework.Assert.fail;

/**
 * @deprecated Use WebDriverITBase for new tests.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class UpgradedSeleniumITBase {
    public final static String PORTAL = "/portal.do";
    protected Selenium selenium;
    protected WebDriver driver;

    /**
     * Returns the URL to be used with this test
     *
     * @return URL of the test
     */
    public abstract String getTestUrl();

    @Before
    public void setUp() throws Exception {
        driver = new FirefoxDriver();
        if (!getTestUrl().startsWith("/")) {
            fail("getTestUrl does not start with /"); // TODO add it?
        }
        selenium = new WebDriverBackedSelenium(driver, getBaseUrlString() + getTestUrl());

        // Login
        selenium.open(getBaseUrlString() + getTestUrl());
        login(selenium);
    }

    public static String getBaseUrlString() {
        String baseUrl = System.getProperty("remote.public.url");
        if (baseUrl == null) {
            baseUrl = "http://localhost:8080";
        } else if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        } else if (!baseUrl.startsWith("http")) {
            baseUrl = "http://" + baseUrl;
        }
        return baseUrl;
    }

    public static void login(Selenium selenium) {
        Assert.assertEquals("Login", selenium.getTitle());
        selenium.type("__login_user", "admin");
        selenium.click("//input[@value='Login']");
        selenium.waitForPageToLoad("30000");
    }

    /**
     * Useful to set -Dremote.driver.dontTearDown=f  -Dremote.driver.dontTearDown=n to not shutdown the browser when
     * working on tests.
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        if (System.getProperty("remote.driver.dontTearDown") == null ||
                !"f".startsWith(System.getProperty("remote.driver.dontTearDown").toLowerCase()) ||
                !"n".startsWith(System.getProperty("remote.driver.dontTearDown").toLowerCase())) {
            selenium.stop();
            driver.quit(); // TODO not tested with chrome, the service stop might need this check too
        }
    }
}
