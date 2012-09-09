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
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;

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

    /**
     * Override in test to define a user other than admin
     * @return
     */
    public String getUserName() {
        return "admin";
    }

    @Before
    public void setUp() throws Exception {
        driver = ITUtil.getWebDriver();
        if (!getTestUrl().startsWith("/")) {
            fail("getTestUrl does not start with /"); // TODO add it?
        }
        selenium = new WebDriverBackedSelenium(driver, ITUtil.getBaseUrlString() + getTestUrl());

        // Login
        selenium.open(ITUtil.getBaseUrlString() + getTestUrl());
        ITUtil.loginSe(selenium, getUserName());
    }

    protected void waitForTitleToEqualKualiPortalIndex() throws InterruptedException {
        ITUtil.waitForTitleToEqual(selenium, "Kuali Portal Index");
    }

    protected void waitForTitleToEqualKualiPortalIndex(String message) throws InterruptedException {
        ITUtil.waitForTitleToEqual(selenium, "Kuali Portal Index", message);
    }

    /**
     * Useful to set -Dremote.driver.dontTearDown=f  -Dremote.driver.dontTearDown=n to not shutdown the browser when
     * working on tests.
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        if (ITUtil.dontTearDownPropertyNotSet()) {
            selenium.stop();
            driver.quit(); // TODO not tested with chrome, the service stop might need this check too
        }
    }
    
    protected String getBaseUrlString() {
        return ITUtil.getBaseUrlString();
    }
}
