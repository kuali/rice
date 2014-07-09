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
package org.kuali.rice.krad.demo.lookup.view;

import org.kuali.rice.krad.demo.ViewDemoAftBase;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoBasicLookUpAft extends ViewDemoAftBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LookupSampleView
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LookupSampleView";
    
    /**
     *	lookupCriteria[number]
     */
    private static final String TRAVEL_ACCOUNT_NUMBER_NAME="lookupCriteria[number]";
    
    /**
     *	lookupCriteria[name]
     */
    private static final String TRAVEL_ACCOUNT_NAME="lookupCriteria[name]";
    
    /**
     *	Search 
     */
    private static final String SEARCH_BUTTON_TEXT="Search";
    
    /**
     *	Clear Values 
     */
    private static final String CLEAR_VALUES_BUTTON_TEXT="Clear Values";
    
    /**
     * lookupCriteria[fiscalOfficer.principalName]
     */
    private static final String PRINCIPAL_NAME="lookupCriteria[fiscalOfficer.principalName]";
    
    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByXpath("//a[@href='lookup?methodToCall=start&viewId=LookupSampleView']");
    }

    protected void testBasicLookUp() throws InterruptedException {
    	//Search By Number
    	waitAndTypeByName(TRAVEL_ACCOUNT_NUMBER_NAME,"BALFTYHTB");
    	waitAndClickButtonByText(SEARCH_BUTTON_TEXT);
    	waitForTextPresent("BALFTYHTB");
    	waitAndClickButtonByText(CLEAR_VALUES_BUTTON_TEXT);
    	
    	//Search By Name
    	waitAndTypeByName(TRAVEL_ACCOUNT_NAME,"Travel Account 2");
    	waitAndClickButtonByText(SEARCH_BUTTON_TEXT);
    	waitForTextPresent("Travel Account 2");
    	waitAndClickButtonByText(CLEAR_VALUES_BUTTON_TEXT);
    	
    	//Search By Principal Name
    	waitAndTypeByName(PRINCIPAL_NAME,"fred");
    	waitAndClickButtonByText(SEARCH_BUTTON_TEXT);
    	waitForTextPresent("Travel Account 1");
    	waitAndClickButtonByText(CLEAR_VALUES_BUTTON_TEXT);
    	
    	//Search By Date
    	waitAndTypeByName("lookupCriteria[rangeLowerBoundKeyPrefix_createDate]","06/01/2000");
    	waitAndTypeByName("lookupCriteria[createDate]","06/01/2050");
    	waitAndClickButtonByText(SEARCH_BUTTON_TEXT);
    	waitForTextPresent("Travel Account");
    }
    
    protected void testBasicLookUpSearchEditSave() throws InterruptedException {
    	//Search 
    	waitAndClickButtonByText(SEARCH_BUTTON_TEXT);
    	waitAndClickByLinkText("edit");
    	
    	//Edit & Save
    	waitAndTypeByName("document.documentHeader.documentDescription","Edited Description");
    	getTextByXpath("//div[@data-label='Travel Account Name']");
    	waitAndTypeByName("document.newMaintainableObject.dataObject.name"," Edited");
    	waitAndClickButtonByExactText("Save");
    	waitForTextPresent("Document was successfully saved.");
    }
   
    @Test
    public void testBasicLookUpBookmark() throws Exception {
        testBasicLookUp();
        passed();
    }

    @Test
    public void testBasicLookUpNav() throws Exception {
        testBasicLookUp();
        passed();
    }
    
    @Test
    public void testBasicLookUpSearchEditSaveBookmark() throws Exception {
    	testBasicLookUpSearchEditSave();
        passed();
    }

    @Test
    public void testBasicLookUpSearchEditSaveNav() throws Exception {
    	testBasicLookUpSearchEditSave();
        passed();
    }
}
