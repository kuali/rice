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

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class JiraIssueCreation {

    WebDriver driver;
    String jiraBase;
    List<File> jiraDataDirs = new LinkedList();
    boolean passed = false;

    @Before
    public void setUp() throws MalformedURLException, InterruptedException {
        jiraBase = System.getProperty("jira.base.url", "https://jira.kuali.org");
        File inputDir = new File(System.getProperty("jira.input.dir", "."));
        File listDir[] = inputDir.listFiles();
        for (int i = 0; i < listDir.length; i++) {
            if (listDir[i].isDirectory()) {
                jiraDataDirs.add(listDir[i]);
            }
        }

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

        passed = true;
    }

    @After
    public void tearDown() {
//        closeAndQuitWebDriver(); // don't close yet, doing the submit manually
    }

    @Test
    public void testCreateJira() throws InterruptedException, IOException {
        List<JiraData> jiraDatas = new LinkedList();
        String summary;
        StringBuilder description = null;

        for (File dir: jiraDataDirs) {

            File[] inputFiles = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jira");
                }
            });

            for (int i = 0, s = inputFiles.length; i < s; i++) {
                jiraDatas.add(parseJiraData(inputFiles[i]));
            }

            // Jira
            WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("create_link"), this.getClass().toString());
            driver.get("/secure/CreateIssue!default.jspa");
            WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("project-field"),
                    this.getClass().toString()).sendKeys(System.getProperty("jira.project", "Kuali Rice Development"));
            WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("issuetype-field"),
                    this.getClass().toString()).sendKeys(System.getProperty("jira.issuetype", "Bug Fix"));
            WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("issue-create-submit"),
                    this.getClass().toString()).click();

            // uncheck include in release notes
            WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("customfield_11621-1"),
                    this.getClass().toString()).click();

            WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("components-textarea"),
                    this.getClass().toString()).sendKeys(System.getProperty("jira.component", "Regression,Development,AFT Failure").replaceAll(",", " "));
            WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("priority-field"),
                    this.getClass().toString()).sendKeys(System.getProperty("jira.priority", "Critical"));


            WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("versions-textarea"),
                    this.getClass().toString()).sendKeys(System.getProperty("jira.versions", "2.4.0-m3").replaceAll(",",
                    " "));
            WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("fixVersions-textarea"),
                    this.getClass().toString()).sendKeys(System.getProperty("jira.fixVersions", "2.4").replaceAll(",", " "));


            summary = description + jiraDatas.get(0).errorMessage;
            if (summary.length() > 80) {
                summary = summary.substring(0, 79);
            }

            WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("summary"),
                    this.getClass().toString()).sendKeys(summary);

            for (JiraData jiraData : jiraDatas) {
                description.append("\n").append(jiraData.aftSteps);
                description.append("\n").append(jiraData.fullTestName).append(" ( ").append(jiraData.shortTestName).append(" ) - ");
                description.append(jiraData.testUrl).append("\n");
            }

            description.append("{code}\n\n").append(jiraDatas.get(0).testDetails).append("\n\n{code}");

            WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("description"),
                    this.getClass().toString()).sendKeys(description);

            WebDriverUtils.waitFor(driver, 600000, By.id("issue-create-submit"), this.getClass().toString());
        }
    }

    protected JiraData parseJiraData(File inputFile) throws IOException {
        String rawData = FileUtils.readFileToString(inputFile, null);
        JiraData jiraData = new JiraData();
        jiraData.aftSteps = rawData.substring(rawData.indexOf("AFT Step:"), rawData.indexOf("Abbreviated test name: "));
        jiraData.shortTestName = rawData.substring(rawData.indexOf("Abbreviated test name: ") + 23, rawData.indexOf("Full test name: ")).trim(); // Abbreviated test name:
        jiraData.fullTestName = rawData.substring(rawData.indexOf("Full test name: ") + 16, rawData.indexOf("Test results url: ")).trim();
        jiraData.testUrl = rawData.substring(rawData.indexOf("Test results url: ") + 18, rawData.indexOf("Error Message: ")).trim();
        jiraData.errorMessage = rawData.substring(rawData.indexOf("Error Message: ") + 15, rawData.indexOf("Test Details: ")).trim();
        jiraData.testDetails = rawData.substring(rawData.indexOf("Test Details: ") + 14, rawData.length()).trim();
        return jiraData;
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

    class JiraData {
        String aftSteps;
        String shortTestName;
        String fullTestName;
        String testUrl;
        String errorMessage;
        String testDetails;
    }
}
