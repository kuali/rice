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
 * Any passed in job numbers are ignored, only the number that is the current lastCompletedBuild is saved.
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
    public void testFetchLastCompletedBuildNumberReport() {
        String jobNumber = null;

        for (int i = 0, s = jobs.size(); i < s; i++) {
            try {

                jobNumber = fetchLastCompletedBuildNumber(jobs.get(i));

                fetchAndWriteTestReport(jobs.get(i), jobNumber);

            } catch (Exception e) {
                passed = false;
                e.printStackTrace();
            }
        }
    }
}
