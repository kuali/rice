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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Base class for Selenium Jenkins Json saving
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class JenkinsJsonJobResultsBase {

    /**
     * -Dbrowser.download.dir= default is Downloads in user.home.
     */
    public static final String BROWSER_DOWNLOAD_DIR = "browser.download.dir";

    /**
     * -Dbrowser.helperApps.neverAsk.saveToDisk= comma delimited list of MIME/Types to saveToDisk default is "application/zip".
     */
    public static final String BROWSER_HELPER_APPS_NEVER_ASK_SAVE_TO_DISK = "browser.helperApps.neverAsk.saveToDisk";

    /**
     * REQUIRED -Dcas.username= CAS username.
     */
    public static final String CAS_USERNAME = "cas.username";

    /**
     * REQUIRED -Dcas.password= CAS password.
     */
    public static final String CAS_PASSWORD = "cas.password";

    /**
     * -Djenkins.base.url= default is http://ci.rice.kuali.org.
     */
    public static final String JENKINS_BASE_URL = "jenkins.base.url";

    /**
     * -Djenkins.jobs= comma delimited with optional colon delimited list of jobs:jobNumbers default is rice-2.4-smoke-test.
     *
     * If no jobNumbers are included the last completed build number for the given job will be used.  If "all" is given for
     * the jobNumbers all available job builds will be used.
     */
    public static final String JENKINS_JOBS = "jenkins.jobs";

    /**
     * -Djson.output.dir= default is directory java is run from, directory must exist.
     */
    public static final String JSON_OUTPUT_DIR = "json.output.dir";

    WebDriver driver;
    boolean passed = false;
    String jenkinsBase;
    String outputDirectory;
    String[] jobsBuildsStrings;
    Map<String, List<String>> jobsBuildsMap = new HashMap<String, List<String>>();
    List<String> jobs = new LinkedList<String>();

    String downloadDir;

    public void setUp() throws MalformedURLException, InterruptedException {
        jenkinsBase = System.getProperty(JENKINS_BASE_URL, "http://ci.rice.kuali.org");
        outputDirectory = System.getProperty(JSON_OUTPUT_DIR);

        FirefoxProfile profile = new FirefoxProfile();
        profile.setEnableNativeEvents(false);

        downloadDir = System.getProperty(BROWSER_DOWNLOAD_DIR, System.getProperty("user.home") + File.separator + "Downloads");
        // download files automatically (don't prompt)
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.download.dir", downloadDir);
        profile.setPreference(BROWSER_HELPER_APPS_NEVER_ASK_SAVE_TO_DISK, System.getProperty(BROWSER_HELPER_APPS_NEVER_ASK_SAVE_TO_DISK, "application/zip"));

        DesiredCapabilities capabilities = new DesiredCapabilities();
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

        // setup jobs builds
        jobsBuildsStrings = System.getProperty(JENKINS_JOBS, "rice-2.4-smoke-test").split("[,\\s]");
        String job;
        for (String jobsBuildsString : jobsBuildsStrings) {
            if (jobsBuildsString.contains(":")) {
                List<String> jobBuilds = Arrays.asList(jobsBuildsString.split(":"));
                job = jobBuilds.get(0); // first item is the job name
                jobs.add(job);
                if (jobBuilds.size() == 2 && "all".equals(jobBuilds.get(1))) { // job:all
                    jobsBuildsMap.put(job, fetchAllJobNumbers(job));
                } else { // regular usage
                    jobsBuildsMap.put(job, jobBuilds.subList(1,jobBuilds.size())); // first item is the job name
                }
            } else { // no jobNumbers specified, use last complete build number
                List<String> jobBuilds = new LinkedList<String>();
                jobBuilds.add(fetchLastCompletedBuildNumber(jobsBuildsString) + "");
                jobs.add(jobsBuildsString);
                jobsBuildsMap.put(jobsBuildsString, jobBuilds);
            }
        }

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

    protected List<String> fetchAllJobNumbers(String job) {
        List<String> allJobNumbers = new LinkedList<String>();
        String url = null;
        String jobJson;

        url = jenkinsBase + "/job/" + job + "/api/json";

        try {
            jobJson = retrieveJson(url);

            String jsonJobNumber;

            while (jobJson.contains(("{\"number\":"))) {
                jsonJobNumber = jobJson.substring(jobJson.indexOf("{\"number\":") + 10, jobJson.length());
                jsonJobNumber = jsonJobNumber.substring(0, jsonJobNumber.indexOf(","));

                allJobNumbers.add(jsonJobNumber);

                jobJson = jobJson.substring(jobJson.indexOf("{\"number\":") + 9, jobJson.length()); // strip off while condition
            }
        } catch (Exception e) {
            System.err.println("Exception fetching job " + job + " with url " + url + e.getMessage());
        }

        return allJobNumbers;
    }

    protected void fetchArchive(String job, String jobNumber) throws InterruptedException, IOException {
        String archiveUrl = jenkinsBase;

        // Views will need to be updated/improved to work with ci.kuali.org
        if (job.contains("rice-2.4")) {
            archiveUrl += "/view/rice-2.4";
        }

        archiveUrl += "/job/" + job + "/" + jobNumber+ "/artifact/*zip*/archive.zip";
        driver.get(archiveUrl);
        Thread.sleep(10000); //zip needs time to download
        FileUtils.moveFile(new File(downloadDir + File.separator + "archive.zip"), new File(downloadDir + File.separator + job + "-" + jobNumber + ".zip"));
    }

    protected void fetchAndWriteTestReport(String job, String jobNumber) throws InterruptedException, IOException {
        String url;
        String json;
        String outputFile;
        url = jenkinsBase + "/job/" + job + "/" + jobNumber + "/testReport/api/json";
        json = retrieveJson(url);

        outputFile = calcOutputFile(job, jobNumber);

        json = json.replaceAll("}],", "}],\n");
        FileUtils.writeStringToFile(new File(outputFile), json);
    }

    protected void fetchAndWriteTestReport(String job, String[] jobNumbers) throws InterruptedException, IOException {
        for (String jobNumber : jobNumbers) {
            fetchAndWriteTestReport(job, jobNumber);
        }
    }

    protected String fetchLastCompletedBuildNumber(String job) throws InterruptedException {
        String url = jenkinsBase + "/job/" + job + "/api/json";
        String jobNumber = null;
        try {
            jobNumber = retrieveJson(url);
            jobNumber = jobNumber.substring(jobNumber.indexOf("\"lastCompletedBuild\":{\"number\":") + 31, jobNumber.length());
            jobNumber = jobNumber.substring(0, jobNumber.indexOf(","));
        } catch (InterruptedException e) {
            System.err.println("Exception fetching job " + job + " with url " + url + e.getMessage());
            throw e;
        }
        return jobNumber;
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
            // index out of bounds can be cause by testReport json not existing (canceled, or a job with no tests), which results in the regular html view of the job number
            json = json.substring(json.indexOf("<pre>") + 5, json.indexOf("</pre></body></html>"));
        } catch (IndexOutOfBoundsException iooobe) {
            System.out.println("No JSON results for " + url + " this can be caused by jobs with no test results, from either the test being stopped, aborted or non-test jobs.");
        }

        return json;
    }
}
