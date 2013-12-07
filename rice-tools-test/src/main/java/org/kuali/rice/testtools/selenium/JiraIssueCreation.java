/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.testtools.selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class JiraIssueCreation {

    WebDriver driver;
    String jiraBase;
//    File inputFile;
    boolean passed = false;

    @Before
    public void setUp() throws MalformedURLException, InterruptedException {
        jiraBase = System.getProperty("jira.base.url", "https://jira.kuali.org");
//        inputFile = new File(System.getProperty("jira.input.file"));

        DesiredCapabilities capabilities = new DesiredCapabilities();
        FirefoxProfile profile = new FirefoxProfile();
        profile.setEnableNativeEvents(false);
        capabilities.setCapability(FirefoxDriver.PROFILE, profile);

        driver = new FirefoxDriver(capabilities);
        driver.manage().timeouts().implicitlyWait(WebDriverUtils.configuredImplicityWait(), TimeUnit.SECONDS);
        driver.get(jiraBase + "/secure/Dashboard.jspa");

        // CAS
        WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("username"),
                this.getClass().toString());
        driver.findElement(By.id("username")).sendKeys(System.getProperty("cas.username"));
        driver.findElement(By.id("password")).sendKeys(System.getProperty("cas.password"));
        driver.findElement(By.name("submit")).click();

        // Jira
        WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("create_link"), this.getClass().toString());
        driver.get("/secure/CreateIssue!default.jspa");
        WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("project-field"),
                this.getClass().toString()).sendKeys("Kuali Rice Development");
        WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("issuetype-field"),
                this.getClass().toString()).sendKeys("Bug Fix");
        WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("issue-create-submit"),
                this.getClass().toString()).click();

        passed = true;
    }

    @After
    public void tearDown() {
//        closeAndQuitWebDriver(); // don't close yet, doing the submit manually
    }

    @Test
    public void testCreateJira() throws InterruptedException {
        // uncheck include in release notes
        WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("customfield_11621-1"),
                this.getClass().toString()).click();

        WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("components-textarea"),
                this.getClass().toString()).sendKeys("Regression Development");
        WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("priority-field"),
                this.getClass().toString()).sendKeys("Critical");


        WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("versions-textarea"),
                this.getClass().toString()).sendKeys("2.4.0-m3");
        WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("fixVersions-textarea"),
                this.getClass().toString()).sendKeys("2.4");
    }

    private void closeAndQuitWebDriver() {
        if (driver != null) {
            if (WebDriverUtils.dontTearDownPropertyNotSet() && WebDriverUtils.dontTearDownOnFailure(passed)) {
                try {
                    driver.close();
                } catch (NoSuchWindowException nswe) {
                    System.out.println("NoSuchWindowException closing WebDriver " + nswe.getMessage());
                } finally {
                    if (driver != null) {
                        driver.quit();
                    }
                }
            }
        } else {
            System.out.println("WebDriver is null for " + this.getClass().toString());
        }
    }
}
