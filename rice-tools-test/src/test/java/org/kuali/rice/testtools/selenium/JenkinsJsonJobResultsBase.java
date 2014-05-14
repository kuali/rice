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

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

/**
 * See constants for required and option JVM args.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class JenkinsJsonJobResultsBase {

    /**
     * REQUIRED -Dcas.password
     */
    public static final String CAS_PASSWORD = "cas.password";

    /**
     * REQUIRED -Dcas.username
     */
    public static final String CAS_USERNAME = "cas.username";

    /**
     * -Djenkins.base.url default value of http://ci.rice.kuali.org
     */
    public static final String JENKINS_BASE_URL = "jenkins.base.url";

    /**
     * REQUIRED -Djenkins.jobs common delimited list of jobs to retrieve json results for
     */
    public static final String JENKINS_JOBS = "jenkins.jobs";

    /**
     * REQUIRED -Djson.output.dir
     */
    public static final String JSON_OUTPUT_DIR = "json.output.dir";

    WebDriver driver;
    boolean passed = false;
    String jenkinsBase;
    String outputDirectory;
    String[] jobs;

    public void setUp() throws MalformedURLException, InterruptedException {
        jenkinsBase = System.getProperty(JENKINS_BASE_URL, "http://ci.rice.kuali.org");
        outputDirectory = System.getProperty(JSON_OUTPUT_DIR);
        jobs = System.getProperty(JENKINS_JOBS).split("[,\\s]");

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
        if (driver.getPageSource().contains("The credentials you provided cannot be determined to be authentic.")) {
            System.out.println("CAS Login Error check you have correctly set -D" + CAS_USERNAME +
                    " and -D" + CAS_PASSWORD);
            System.exit(1);
        }

        // Jenkins login page (don't login, we have authenticated through CAS already
        WebDriverUtils.waitFor(driver, WebDriverUtils.configuredImplicityWait(), By.xpath("//span[contains(text(), 'Page generated')]"), this.getClass().toString());

        passed = true;
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

    protected String retrieveJson(String url) throws InterruptedException {
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
