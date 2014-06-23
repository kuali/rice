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
package org.kuali.rice.krad.labs.lookups;

import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsLookUpConditionalCriteriaAft extends LabsLookupBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LabsLookup-ConditionalCriteriaView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LabsLookup-ConditionalCriteriaView&hideReturnLink=true";

    /**
     *  lookupCriteria[rangeLowerBoundKeyPrefix_createDate]
     */
    private static final String LOOKUP_CRITERIA_DATE_NAME="lookupCriteria[rangeLowerBoundKeyPrefix_createDate]";
    
    /**
     *  Not read only. Date input field present.
     */
    private static final String FAILURE_MESSAGE="Not read only. Date input field present.";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup Conditional Criteria");
    }

    // copied from DemoLookUpConditionalCriteriaAft
    protected void testLookUpConditionalCriteria() throws InterruptedException {
        //Case 1 - Date field required by number a1
        //It requires "Search" to be clicked twice, for the date required message to show.
        waitAndTypeByName(LOOKUP_CRITERIA_NUMBER_NAME, "a1");
        waitAndClickByName("lookupCriteria[name]");
        waitForElementPresent(By.className("uif-requiredMessage"));
        checkForIncidentReport();

        //Case 2 - Date field read only by number a2
        clearTextByName(LOOKUP_CRITERIA_NUMBER_NAME);
        waitAndTypeByName(LOOKUP_CRITERIA_NUMBER_NAME, "a2");
        waitAndClickByName("lookupCriteria[name]");
        Thread.sleep(3000);
        checkForIncidentReport();
        if(isElementPresentByName(LOOKUP_CRITERIA_DATE_NAME)) {
            fail(FAILURE_MESSAGE);
        }

        //Case 3 - Date field hide by number a3
        clearTextByName(LOOKUP_CRITERIA_NUMBER_NAME);
        waitAndTypeByName(LOOKUP_CRITERIA_NUMBER_NAME, "a3");
        waitAndClickByName("lookupCriteria[name]");
        Thread.sleep(3000);
        checkForIncidentReport();
        assertTrue(isNotVisible(By.name(LOOKUP_CRITERIA_DATE_NAME)));
        assertTrue(isNotVisible(By.name("lookupCriteria[createDate]")));
    }


    @Test
    public void testLookUpConditionalCriteriaBookmark() throws Exception {
        testLookUpConditionalCriteria();
        passed();
    }

    @Test
    public void testLookUpConditionalCriteriaNav() throws Exception {
        testLookUpConditionalCriteria();
        passed();
    }
}
