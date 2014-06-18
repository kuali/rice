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
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelPerDiemLookUpAft extends ViewDemoAftBase {

    /**
     * /kr-krad/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.travel.dataobject.TravelPerDiemExpense
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.travel.dataobject.TravelPerDiemExpense";

    /**
     * Search
     */
    public static final String SEARCH = "Search";
    
    /**
     * Clear Values
     */
    public static final String CLEAR_VALUES = "Clear Values";
    
    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Travel Per Diem Lookup");
    }

    protected void testTravelPerDiemLookUp() throws Exception {
    	waitAndTypeByName("lookupCriteria[travelPerDiemExpenseId]","1");
    	waitAndTypeByName("lookupCriteria[travelAuthorizationDocumentId]","1");
    	selectByName("lookupCriteria[travelDestinationId]","Colorado");
    	waitAndClickByXpath("//div[@data-label='Primary Destination']/div/div/button[@class='btn btn-default uif-action icon-search']");
    	waitSearchAndReturnFromLightbox();
        assertEquals("lookupCriteria[travelAuthorizationDocumentId] value not 1 as set before Lightbox search", "1", waitAndGetAttributeByName("lookupCriteria[travelAuthorizationDocumentId]", "value"));
    	waitAndClickByXpath("//div[@data-label='Mileage Rate']/div/div/button[@class='btn btn-default uif-action icon-search']");
    	waitSearchAndReturnFromLightbox();
    	waitAndClickButtonByText(SEARCH);
    	waitForTextPresent("No values match this search.");
    	waitForTextPresent("You have entered the primary key for this table (Id) in the search criteria. Since these fields can be used to uniquely identify a row in this table, the other search criteria entered will be ignored.");
    	waitAndClickButtonByText(CLEAR_VALUES);
    	waitAndTypeByName("lookupCriteria[travelPerDiemExpenseId]","10000");
    	waitAndClickButtonByText(SEARCH);
        // Last data first so data is fully loaded
    	String activeResults [] ={"30","10000","10000","10000","10","10","15","20","DO"};
    	assertTextPresent(activeResults);
    }

    @Test
    public void testTravelPerDiemLookUpBookmark() throws Exception {
        testTravelPerDiemLookUp();
        passed();
    }

    @Test
    public void testTravelPerDiemLookUpNav() throws Exception {
        testTravelPerDiemLookUp();
        passed();
    }
    
    private void waitSearchAndReturnFromLightbox() throws Exception {
    	gotoLightBox();
    	waitAndClickButtonByText("Search");
    	waitAndClickByLinkText("return value");
    }
}
