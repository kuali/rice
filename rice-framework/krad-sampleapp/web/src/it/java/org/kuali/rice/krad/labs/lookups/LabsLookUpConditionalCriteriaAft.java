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
     *  lookupCriteria[number]
     */
    private static final String LOOKUP_CRITERIA_NUMBER_NAME="lookupCriteria[number]";
    
    /**
     *  Search
     */
    private static final String SEARCH="Search";
    
    /**
     *  lookupCriteria[rangeLowerBoundKeyPrefix_createDate]
     */
    private static final String LOOKUP_CRITERIA_DATE_NAME="lookupCriteria[rangeLowerBoundKeyPrefix_createDate]";
    
    /**
     *  Not read only. Date input field present.
     */
    private static final String FAILURE_MESSAGE="Not read only. Date input field present.";
    
    /**
     *  Date Created:
     */
    private static final String DATE_CREATED_LABEL="Date Created:";
    
    /**
     *  Date Created is a required field.
     */
    private static final String DATE_REQUIRED_MESSAGE="Date Created is a required field.";
    
    /**
     *  LookUp Criteria Name 
     */
    private static final String LOOKUP_CRITERIA_NAME_NAME="lookupCriteria[name]";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup Conditional Criteria");
    }

    protected void testLookUpConditionalCriteria() throws InterruptedException {
        //Case 2 - Date field read only by number a2
        clearTextByName(LOOKUP_CRITERIA_NUMBER_NAME);
        waitAndTypeByName(LOOKUP_CRITERIA_NUMBER_NAME,"a2");
        waitAndTypeByName(LOOKUP_CRITERIA_NAME_NAME,"");
        waitForElementNotPresent(By.name(LOOKUP_CRITERIA_DATE_NAME));

        //Case 3 - Date field hide by number a3
        clearTextByName(LOOKUP_CRITERIA_NUMBER_NAME);
        waitAndTypeByName(LOOKUP_CRITERIA_NUMBER_NAME,"a3");
        waitAndClickButtonByText(SEARCH);
        waitForTextNotPresent(DATE_CREATED_LABEL);

        //Case 1 - Date field required by number a1
        //It requires "Search" to be clicked twice, for the date required message to show.
        clearTextByName(LOOKUP_CRITERIA_NUMBER_NAME);
        waitAndTypeByName(LOOKUP_CRITERIA_NUMBER_NAME,"a1");
        waitAndClickButtonByText(SEARCH);
        Thread.sleep(10000); // If we don't wait long enough we'll get concurrency issues.
        waitAndClickButtonByText(SEARCH);
        Thread.sleep(3000);
        assertTextPresent(DATE_REQUIRED_MESSAGE);
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
