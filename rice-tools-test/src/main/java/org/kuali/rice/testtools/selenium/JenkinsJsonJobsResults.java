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
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class JenkinsJsonJobsResults {

    WebDriver driver;
    String jenkinsBase;
    String[] jobs;
    String outputDirectory;
    boolean passed = false;

    @Before
    public void setUp() throws MalformedURLException, InterruptedException {
        jenkinsBase = System.getProperty("jenkins.base.url", "http://ci.rice.kuali.org");
        outputDirectory = System.getProperty("json.output.dir");
        jobs = System.getProperty("jenkins.jobs", "rice-2.4-smoke-test").split("[,\\s]");

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
        driver.findElement(By.id("username")).sendKeys(System.getProperty("cas.username"));
        driver.findElement(By.id("password")).sendKeys(System.getProperty("cas.password"));
        driver.findElement(By.name("submit")).click();
        Thread.sleep(1000);
        if (driver.getPageSource().contains("The credentials you provided cannot be determined to be authentic.")) {
            System.out.println("CAS Login Error");
            System.exit(1);
        }

        // Jenkins login page (don't login, we have authenticated through CAS already
        WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.xpath("//span[contains(text(), 'Page generated')]"), this.getClass().toString());

        passed = true;
    }

    @After
    public void tearDown() {
        closeAndQuitWebDriver();
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

    @Test
    public void testGetJobResults() {
        String jobJson = null;
        String json = null;
        String outputFile = null;
        String url = null;
        String job = null;

        for (int i = 0, s = jobs.length; i < s; i++) {
            try {
                job = jobs[i];
                url = jenkinsBase + "/job/" + job + "/api/json";

                jobJson = retrieveJson(url);
                jobJson = jobJson.substring(jobJson.indexOf("\"lastCompletedBuild\":{\"number\":") + 31, jobJson.length());
                jobJson = jobJson.substring(0, jobJson.indexOf(","));

                url = jenkinsBase + "/job/" + job + "/" + jobJson + "/testReport/api/json";
                json = retrieveJson(url);

                outputFile = job + "-" + jobJson + ".json";
                if (outputDirectory != null) {
                    outputFile = outputDirectory + File.separatorChar + outputFile;
                }

                json = json.replaceAll("}],", "}],\n");
                FileUtils.writeStringToFile(new File(outputFile), json);

            } catch (Exception e) {
                passed = false;
                System.out.println("job: " + job + " url: " + url + " " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String retrieveJson(String url) throws InterruptedException {
        driver.get(url);
        Thread.sleep(500);
        int secondsWaited = 0;

        while (!driver.getPageSource().contains("</pre></body></html>") && secondsWaited++ < 2 * WebDriverUtils.configuredImplicityWait()) {
            Thread.sleep(1000);
        }

        String json = driver.getPageSource();
        json = json.substring(json.indexOf("<pre>") + 5, json.indexOf("</pre></body></html>"));

        return json;
    }
}
