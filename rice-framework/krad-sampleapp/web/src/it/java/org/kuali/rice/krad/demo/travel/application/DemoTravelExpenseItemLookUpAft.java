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
public class DemoTravelExpenseItemLookUpAft extends ViewDemoAftBase {

    /**
     * /kr-krad/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.travel.dataobject.TravelExpenseItem
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&dataObjectClassName=edu.sampleu.travel.dataobject.TravelExpenseItem";

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
        waitAndClickByLinkText("Travel Expense Item Lookup");
    }

    protected void testTravelExpenseItemLookUp() throws Exception {
    	//Search with primary key
    	waitAndTypeByName("lookupCriteria[travelExpenseItemId]","10000");
    	waitAndClickButtonByText(SEARCH);
    	waitForTextPresent("One item retrieved.");
    	waitForTextPresent("You have entered the primary key for this table (Id) in the search criteria. Since these fields can be used to uniquely identify a row in this table, the other search criteria entered will be ignored.");
    	waitAndClickButtonByText(CLEAR_VALUES);
    	
    	//Search with document ID
    	waitAndTypeByName("lookupCriteria[travelAuthorizationDocumentId]","10000");
    	waitAndClickButtonByText(SEARCH);
    	waitForTextPresent("One item retrieved.");
    	waitAndClickButtonByText(CLEAR_VALUES);
    	
    	//Search with Company Name
    	waitAndTypeByName("lookupCriteria[travelCompanyName]","Discount Travel");
    	waitAndClickButtonByText(SEARCH);
    	waitForTextPresent("Discount Travel");
    	waitAndClickButtonByText(CLEAR_VALUES);
    	
    	//Search with Expense Type & Description
    	waitAndTypeByName("lookupCriteria[travelExpenseTypeCd]","ME");
    	waitAndTypeByName("lookupCriteria[expenseDesc]","Family Related");
    	waitAndClickButtonByText(SEARCH);
    	String assertResultForExpenseTypeAndDesc [] ={"ME","Family Related"};
    	assertTextPresent(assertResultForExpenseTypeAndDesc);
    	waitAndClickButtonByText(CLEAR_VALUES);
    	
    	//Search with Expense Amount Reimbursable and Taxable
    	waitAndTypeByName("lookupCriteria[expenseAmount]","1,278.97");
    	waitAndClickByXpath("//input[@name='lookupCriteria[reimbursable]' and @value='Y']");
    	waitAndClickByXpath("//input[@name='lookupCriteria[taxable]' and @value='Y']");
    	String assertResultForExpenseAmount [] ={"1,278.97","true","true"};
    	assertTextPresent(assertResultForExpenseAmount);
    	waitAndClickButtonByText(CLEAR_VALUES);
    }

    @Test
    public void testTravelExpenseItemLookUpBookmark() throws Exception {
        testTravelExpenseItemLookUp();
        passed();
    }

    @Test
    public void testTravelExpenseItemLookUpNav() throws Exception {
        testTravelExpenseItemLookUp();
        passed();
    }
}
