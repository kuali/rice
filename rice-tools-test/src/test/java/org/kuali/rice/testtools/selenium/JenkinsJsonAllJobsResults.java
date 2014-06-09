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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class JenkinsJsonAllJobsResults extends JenkinsJsonJobResultsBase {

    @Before
    public void setUp() throws MalformedURLException, InterruptedException {
        super.setUp();
    }

    @After
    public void tearDown() {
        closeAndQuitWebDriver();
    }

    @Test
    public void testGetAllJobResults() throws Exception {
        for (int i = 0, s = jobs.length; i < s; i++) {
            getResults(jobs[i]);
        }
    }

    private void getResults(String job) throws Exception {
        String url = null;
        String jobJson;

        url = jenkinsBase + "/job/" + job + "/api/json";

        jobJson = retrieveJson(url);

        String jsonJobNumber;
        while (jobJson.contains(("{\"number\":"))) {
            jsonJobNumber = jobJson.substring(jobJson.indexOf("{\"number\":") + 10, jobJson.length());
            jsonJobNumber = jsonJobNumber.substring(0, jsonJobNumber.indexOf(","));

            jobJson = jobJson.substring(jobJson.indexOf("{\"number\":") + 9, jobJson.length()); // strip off while condition

            try {
                fetchAndWriteJobResults(job, jsonJobNumber);
            } catch (Exception e) {
                passed = false;
                System.out.println("job: " + job + " url: " + url + " " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}