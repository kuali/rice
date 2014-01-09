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
package edu.sampleu.admin.workflow;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class StatisticsAft extends WebDriverLegacyITBase {

    /**
     *  AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Statistics&channelUrl="+ WebDriverUtils
     *   .getBaseUrlString()+"/kew/Stats.do?returnLocation=";
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Statistics&channelUrl="+ WebDriverUtils
            .getBaseUrlString()+"/kew/Stats.do?returnLocation=";
 
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickAdministration();
        waitAndClickByLinkText("Statistics");
    }

    protected void testStatistics() throws Exception { 
        selectFrameIframePortlet();
        waitForElementPresentByXpath("//input[@src='images/buttonsmall_refresh.gif']");
        assertTextPresent("Aggregates");
        assertTextPresent("Number of Document Types:");
        assertTextPresent("Number of Users:");
        assertTextPresent("Number of Active Items In All Action Lists:");
        assertTextPresent("Number of Documents Initiated in the Last 30 Days:");
        assertTextPresent("Date Range");
        assertTextPresent("Begin Date:");
        assertTextPresent("End Date:");
        assertTextPresent("Routing Status");
        assertTextPresent("Number of Documents for Date Range");
        assertTextPresent("CANCELED:");
        assertTextPresent("DISAPPROVED:");
        assertTextPresent("ENROUTE:");
        assertTextPresent("EXCEPTION");
        assertTextPresent("FINAL:");
        assertTextPresent("INITIATED:");
        assertTextPresent("PROCESSED:");
        assertTextPresent("SAVED:");
    }

    @Test
    public void testStatisticsBookmark() throws Exception {
        testStatistics();
        passed();
    }

    @Test
    public void testStatisticsNav() throws Exception {
        testStatistics();
        passed();
    }
}
