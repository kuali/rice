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
package org.kuali.rice.krad.demo.travel.application;

import org.kuali.rice.krad.demo.ViewDemoAftBase;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelCompanyLookUpAft extends ViewDemoAftBase {

    /**
     * /kr-krad/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.travel.dataobject.TravelCompany
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.travel.dataobject.TravelCompany";
    
    /**
     * Search
     */
    public static final String SEARCH = "Search";
    
    /**
     * Clear Values
     */
    public static final String CLEAR_VALUES = "Clear Values";

    /**
     * lookupCriteria[travelCompanyId]
     */
    public static final String TRAVEL_COMPANY_ID = "lookupCriteria[travelCompanyId]";

    /**
     * lookupCriteria[travelCompanyName]
     */
    public static final String TRAVEL_COMPANY_NAME = "lookupCriteria[travelCompanyName]";
    
    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Travel Company Lookup");
    }

    protected void testTravelCompanyLookUp() throws Exception {
    	waitAndTypeByName(TRAVEL_COMPANY_ID,"1");
    	waitAndTypeByName(TRAVEL_COMPANY_NAME,"a*");
    	waitAndClickButtonByText(SEARCH);
    	waitForTextPresent("No values match this search.");
    	waitForTextPresent("You have entered the primary key for this table (Id) in the search criteria. Since these fields can be used to uniquely identify a row in this table, the other search criteria entered will be ignored.");
    	waitAndClickButtonByText(CLEAR_VALUES);
    	waitAndClickByXpath("//input[@name='lookupCriteria[active]' and @value='Y']");
    	waitAndClickButtonByText(SEARCH);
    	String activeResults [] ={"10000","Value Rentals","true"};
    	Thread.sleep(1000); //Required as the below method "only waits for first element" in array. Here first element gets loaded and second takes a while.
    	assertTextPresent(activeResults);
    	waitAndClickButtonByText(CLEAR_VALUES);
    	waitAndClickByXpath("//input[@name='lookupCriteria[active]' and @value='N']");
    	waitAndClickButtonByText(SEARCH);
    	String activeResultsNo [] ={"10001","AAA Travel","false"};
    	Thread.sleep(1000); //Required as the below method "only waits for first element" in array. Here first element gets loaded and second takes a while.
    	assertTextPresent(activeResultsNo);
    }

    @Test
    public void testTravelCompanyLookUpBookmark() throws Exception {
        testTravelCompanyLookUp();
        passed();
    }

    @Test
    public void testTravelCompanyLookUpNav() throws Exception {
        testTravelCompanyLookUp();
        passed();
    }
}
