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
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class JenkinsJsonJobResultsBase {

    /**
     * REQUIRED -Dcas.username= CAS username.
     */
    private static final String CAS_USERNAME = "cas.username";

    /**
     * REQUIRED -Dcas.password= CAS password.
     */
    private static final String CAS_PASSWORD = "cas.password";

    /**
     * -Djenkins.base.url= default is http://ci.rice.kuali.org.
     */
    private static final String JENKINS_BASE_URL = "jenkins.base.url";

    /**
     * -Djenkins.jobs= comma delimited list of jobs default is rice-2.4-smoke-test.
     */
    private static final String JENKINS_JOBS = "jenkins.jobs";

    /**
     * -Djson.output.dir= default is directory java is run from, directory must exist.
     */
    private static final String JSON_OUTPUT_DIR = "json.output.dir";

    WebDriver driver;
    boolean passed = false;
    String jenkinsBase;
    String outputDirectory;
    String[] jobs;

    public void setUp() throws MalformedURLException, InterruptedException {
        jenkinsBase = System.getProperty(JENKINS_BASE_URL, "http://ci.rice.kuali.org");
        outputDirectory = System.getProperty(JSON_OUTPUT_DIR);
        jobs = System.getProperty(JENKINS_JOBS, "rice-2.4-smoke-test").split("[,\\s]");

        DesiredCapabilities capabilities = new DesiredCapabilities();
        FirefoxProfile profile = new FirefoxProfile();
        profile.setEnableNativeEvents(false);
        capabilities.setCapability(FirefoxDriver.PROFILE, profile);

        driver = new FirefoxDriver(capabilities);
        driver.manage().timeouts().implicitlyWait(WebDriverUtils.configuredImplicityWait(), TimeUnit.SECONDS);
        driver.get(jenkinsBase + "/login?form");

        // CAS
        WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.id("username"),
                this.getClass().toString());
        driver.findElement(By.id("username")).sendKeys(System.getProperty(CAS_USERNAME));
        driver.findElement(By.id("password")).sendKeys(System.getProperty(CAS_PASSWORD));
        driver.findElement(By.name("submit")).click();
        Thread.sleep(1000);

        exitOnLoginProblems();

        // Jenkins login page (don't login, we have authenticated through CAS already
        WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.xpath("//span[contains(text(), 'Page generated')]"), this.getClass().toString());

        passed = true;
    }

    protected String calcOutputFile(String job, String jobNumber) {
        String outputFile  = job + "-" + jobNumber + ".json";
        if (outputDirectory != null) {
            outputFile = outputDirectory + File.separatorChar + outputFile;
        }
        return outputFile;
    }

    protected void closeAndQuitWebDriver() {
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

    private void exitOnLoginProblems() {
        boolean exit = false;
        String pageSource = driver.getPageSource();
        if (pageSource.contains("Username is a required field.")) {
            System.out.println("CAS Username is a required did you set -D" + CAS_USERNAME + "=");
            exit = true;
        }
        if (pageSource.contains("Password is a required field.")) {
            System.out.println("CAS Password is a required did you set -D" + CAS_PASSWORD + "=");
            exit = true;
        }
        if (pageSource.contains("The credentials you provided cannot be determined to be authentic.")) {
            System.out.println("CAS Login Error");
            exit = true;
        }
        if (exit) {
            System.exit(1);
        }
    }

    protected void fetchAndWriteJobResults(String job, String jobNumber) throws InterruptedException, IOException {
        String url;
        String json;
        String outputFile;
        url = jenkinsBase + "/job/" + job + "/" + jobNumber + "/testReport/api/json";
        json = retrieveJson(url);

        outputFile = calcOutputFile(job, jobNumber);

        json = json.replaceAll("}],", "}],\n");
        FileUtils.writeStringToFile(new File(outputFile), json);
    }

    protected String retrieveJson(String url) throws InterruptedException {
        driver.get(url);
        Thread.sleep(500);
        int secondsWaited = 0;

        while (!driver.getPageSource().contains("</pre></body></html>") && secondsWaited++ < 2 * WebDriverUtils.configuredImplicityWait()) {
            Thread.sleep(1000);
        }

        String json = driver.getPageSource();

        try {
            // index out of bounds can be cause by testRport json not existing (canceled, or a job with no tests), which results in the regular html view of the job number
            json = json.substring(json.indexOf("<pre>") + 5, json.indexOf("</pre></body></html>"));
        } catch (IndexOutOfBoundsException iooobe) {
            System.out.println("No JSON results for " + url + " this can be caused by jobs with no test results, from either the test being stopped, aborted or non-test jobs.");
        }

        return json;
    }
}
