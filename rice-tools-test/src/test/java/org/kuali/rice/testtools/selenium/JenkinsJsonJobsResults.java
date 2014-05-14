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
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;

/**
 * Retrieve Last Completed Build Jenkins Job(s) Results as JSON.
 * @See JenkinsJsonJobResultsBase for required and optional JVM args.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class JenkinsJsonJobsResults extends JenkinsJsonJobResultsBase {

    @Before
    public void setUp() throws MalformedURLException, InterruptedException {
        super.setUp();
    }

    @After
    public void tearDown() {
        closeAndQuitWebDriver();
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

                json = json.replace("},{", "},\n{");
                json = json.replaceAll("}],", "}],\n\n\n");
                FileUtils.writeStringToFile(new File(outputFile), json);

            } catch (Exception e) {
                passed = false;
                System.out.println("job: " + job + " url: " + url + " " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
