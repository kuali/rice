/**
 * Copyright 2005-2014 The Kuali Foundation
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
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

/**
 * -Dcas.username=casusername -Dcas.password=caspassword
 * -Djira.input.dir=/Users/eghm/Desktop/JenkinsResults-2.5-IT-05-12/JiraGroups -Djira.summary.start=IT_Failure
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class JiraIssueCreation {

    WebDriver driver;
    String jiraBase;
    List<File> jiraDataDirs = new LinkedList();
    boolean passed = false;
    Map<String, String> jiraMap = new HashMap<String, String>();
    Map<String, Map<String, String>> jiraMaps = new HashMap<String, Map<String, String>>();

    @Before
    public void setUp() throws IOException, InterruptedException {
        jiraMap.put("project-field", System.getProperty("jira.project", "Kuali Rice Development"));
        jiraMap.put("jira.versions", System.getProperty("jira.versions", "2.5").replaceAll(",", " "));
        jiraMap.put("jira.fixVersions", System.getProperty("jira.fixVersions", "").replaceAll(",", " "));
        jiraMap.put("issuetype-field", System.getProperty("jira.issuetype", "Bug Fix"));
        jiraMap.put("jira.component", System.getProperty("jira.component", "Regression,Development").replaceAll(",", " "));
        jiraMap.put("jira.priority", System.getProperty("jira.priority", "Critical"));

        jiraBase = System.getProperty("jira.base.url", "https://jira.kuali.org");
        File inputDir = new File(System.getProperty("jira.input.dir", "."));
        File listDir[] = inputDir.listFiles();
        for (int i = 0; i < listDir.length; i++) {
            if (listDir[i].isDirectory()) {
                jiraDataDirs.add(listDir[i]);
            }
        }

        createJiraMaps();
        dumpJiraMaps();
        login();

        passed = true;
    }

    private void dumpJiraMaps() {
        for (Map<String, String> jiraMapped : jiraMaps.values()) {
            for (String key : jiraMapped.keySet()) {
                System.out.println(key + " = " + jiraMapped.get(key));
            }
            System.out.println("\n\n\n\n\n\n\n\n");
        }
    }

    private void login() throws InterruptedException {DesiredCapabilities capabilities = new DesiredCapabilities();
        FirefoxProfile profile = new FirefoxProfile();
        profile.setEnableNativeEvents(false);
        capabilities.setCapability(FirefoxDriver.PROFILE, profile);

        driver = new FirefoxDriver(capabilities);
        driver.manage().timeouts().implicitlyWait(WebDriverUtils.configuredImplicityWait(), TimeUnit.SECONDS);
        driver.get(jiraBase + "/secure/Dashboard.jspa");

        WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.className("login-link"),
                this.getClass().toString()).click();

        // CAS
        WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("username"),
                this.getClass().toString());
        driver.findElement(By.id("username")).sendKeys(System.getProperty("cas.username"));
        driver.findElement(By.id("password")).sendKeys(System.getProperty("cas.password"));
        driver.findElement(By.name("submit")).click();
    }

    @After
    public void tearDown() {
//        closeAndQuitWebDriver(); // don't close yet, doing the submit manually
    }

    public void createJiraMaps() throws InterruptedException, IOException {
        List<JiraData> jiraDatas = new LinkedList();
        String summary;

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

            String testClass = jiraDatas.get(0).fullTestName.substring(0, jiraDatas.get(0).fullTestName.lastIndexOf("."));
            testClass = testClass.replace("org.kuali.rice.", "");
            testClass = testClass.replace("edu.sampleu.", "");
            summary = System.getProperty("jira.summary.start", "").replaceAll("_", " ") + " "  + testClass;

            if (jiraDatas.size() == 1) {
                summary += " " + jiraDatas.get(0).testDetails;
            }

            summary = summary.replace("java.lang.AssertionError: ", "");
            summary = summary.replace("org.eclipse.persistence.exceptions.DatabaseException: \nInternal Exception: com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException: ", "");
            if (summary.indexOf("\n") > -1) {
                summary = summary.substring(0, summary.indexOf("\n")).trim();
            } else if (summary.indexOf("\t") > -1) {
                summary = summary.substring(0, summary.indexOf("\t")).trim();
            }

            if (summary.length() > 180) {
                summary = summary.substring(0, 179);
            }

            jiraMap.put("jira.summary", summary);

            StringBuilder description = new StringBuilder(summary).append(" ").append(System.getProperty("jira.description.start", "").replaceAll("_", " ")).append("\n");

            for (JiraData jiraData : jiraDatas) {
                if (!"".equals(jiraData.aftSteps)) {
                    description.append("\n").append(jiraData.aftSteps);
                }
                description.append("\n").append(jiraData.fullTestName).append(" ( ").append(jiraData.shortTestName).append(" ) - ");
                description.append(jiraData.testUrl).append("\n");
                description.append("\n{code}\n\n").append(jiraDatas.get(0).testDetails).append("\n\n{code}\n");
            }

            jiraMap.put("jira.description", description.toString());
            jiraMaps.put(dir.getName(), new HashMap<String, String>(jiraMap));
        }
    }

    @Test
    public void testCreateJira() throws InterruptedException, IOException {
        for (Map<String, String> jiraMap : jiraMaps.values()) {

            // Jira
            WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("create_link"), this.getClass().toString());
            driver.get(jiraBase + "/secure/CreateIssue!default.jspa");

            // Project
            WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("project-field"),
                    this.getClass().toString()).sendKeys(jiraMap.get("jira.project"));

            // Issue type
            WebElement issue = WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id(
                    "issuetype-field"), this.getClass().toString());
            issue.click();
            issue.sendKeys(Keys.BACK_SPACE);
            issue.sendKeys(jiraMap.get("jira.issuetype"));
//            issue.sendKeys(Keys.ARROW_DOWN);
            issue.sendKeys(Keys.TAB);

            WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("issue-create-submit"),
                    this.getClass().toString()).click();

            // Summary // TODO remove things that look like java object references
            // TODO if the error messages are the same for all jiras then include it in the summary
            WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("summary"),
                    this.getClass().toString()).sendKeys(jiraMap.get("jira.summary"));

            // Components
            WebElement component = WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id(
                    "components-textarea"), this.getClass().toString());
            String components = jiraMap.get("jira.component");
            StringTokenizer tokens = new StringTokenizer(components);
            while (tokens.hasMoreElements()) {
                component.click();
                component.sendKeys(tokens.nextToken());
//                component.sendKeys(Keys.ARROW_DOWN);
                component.sendKeys(Keys.TAB);
            }

            // Description
            WebElement descriptionElement = WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("description"),
                    this.getClass().toString());
            descriptionElement.click();
            descriptionElement.sendKeys(jiraMap.get("jira.description"));

            // Priority
            WebElement priority = WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("priority-field"),
                    this.getClass().toString());
            priority.click();
            priority.sendKeys(Keys.BACK_SPACE);
            priority.sendKeys(jiraMap.get("jira.priority"));
//            priority.sendKeys(Keys.ARROW_DOWN);
            priority.sendKeys(Keys.TAB);

            // Version
            WebElement version = WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("versions-textarea"),
                    this.getClass().toString());
            version.click();
            version.sendKeys(jiraMap.get("jira.versions"));
//            version.sendKeys(Keys.ARROW_DOWN);
            version.sendKeys(Keys.TAB);

            // Fix version
            WebElement fixVersion = WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("fixVersions-textarea"),
                    this.getClass().toString());
            fixVersion.click();
            fixVersion.sendKeys(jiraMap.get("jira.fixVersions"));
//            fixVersion.sendKeys(Keys.ARROW_DOWN);
            fixVersion.sendKeys(Keys.TAB);

            // Release notes unchecked
            WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("customfield_11621-1"),
                    this.getClass().toString()).click();

            WebDriverUtils.waitFor(driver, 8, By.id("issue-create-submit"), this.getClass().toString());

//            WebDriverUtils.acceptAlertIfPresent(driver); // Dialog present when running Se.....
        }
    }

    protected JiraData parseJiraData(File inputFile) throws IOException {
        String rawData = FileUtils.readFileToString(inputFile, null);
        JiraData jiraData = new JiraData();
        try {
            jiraData.aftSteps = rawData.substring(rawData.indexOf("AFT Step:"), rawData.indexOf("Abbreviated test name: "));
        } catch (IndexOutOfBoundsException ioobe) {
            // ignore only AFTs have AFT steps
        }
        jiraData.shortTestName = rawData.substring(rawData.indexOf("Abbreviated test name: ") + 23, rawData.indexOf("Full test name: ")).trim(); // Abbreviated test name:
        jiraData.fullTestName = rawData.substring(rawData.indexOf("Full test name: ") + 16, rawData.indexOf("Test results url: ")).trim();
        jiraData.testUrl = rawData.substring(rawData.indexOf("Test results url: ") + 18, rawData.indexOf("Error Message: ")).trim();
        jiraData.errorMessage = rawData.substring(rawData.indexOf("Error Message: ") + 15, rawData.indexOf("Test Details: ")).trim();
        jiraData.testDetails = rawData.substring(rawData.indexOf("Test Details: ") + 14, rawData.length()).trim().replace("\t", "       ");
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
        String aftSteps = ""; // only AFTs have AFT Steps
        String shortTestName;
        String fullTestName;
        String testUrl;
        String errorMessage;
        String testDetails;
    }
}
