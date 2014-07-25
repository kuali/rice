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
public class DemoLookUpActionsGroupedInHierarchyAft extends ViewDemoAftBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LookupSampleViewActionsHierarchy
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LookupSampleViewActionsHierarchy";

    /**
     *	lookupCriteria[number] 
     */
    private static final String TRAVEL_ACCOUNT_NUMBER="lookupCriteria[number]";
    
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
        waitAndClickDemoLink();
        waitAndClickByLinkText("Lookup actions grouped into a hierarchy");
    }

    protected void testLookUpActionsGroupedInHierarchy() throws InterruptedException {
    	//Search By Number 
    	waitAndTypeByName(TRAVEL_ACCOUNT_NUMBER,"A139638996");
    	waitAndClickButtonByText(SEARCH_BUTTON_TEXT);
    	waitForElementPresentByXpath("//a[contains(text(),'A139638996')]");
    	assertActionsGroupedInHierarchy();
    	waitAndClickButtonByText(CLEAR_VALUES_BUTTON_TEXT);
    	
    	//Search By Name
    	waitAndTypeByName(TRAVEL_ACCOUNT_NAME,"A1396389962525av");
    	waitAndClickButtonByText(SEARCH_BUTTON_TEXT);
        waitForProgressLoading();
        waitForTextPresent("A1396389962525av");
    	assertActionsGroupedInHierarchy();
    	waitAndClickButtonByText(CLEAR_VALUES_BUTTON_TEXT);
    	
    	//Search By Fiscal Officer
    	waitAndTypeByName(PRINCIPAL_NAME,"fran");
    	waitAndClickButtonByText(SEARCH_BUTTON_TEXT);
    	waitForTextPresent("fran");
    	assertActionsGroupedInHierarchy();
    	waitAndClickButtonByText(CLEAR_VALUES_BUTTON_TEXT);
    }
    
    private void assertActionsGroupedInHierarchy() throws InterruptedException{
    	waitForElementPresentByXpath("//a[contains(text(),'edit')]");
    	waitForElementPresentByXpath("//a[contains(text(),'loan')]");
    	waitForElementPresentByXpath("//a[contains(text(),'renew')]");
    	waitForElementPresentByXpath("//a[contains(text(),'return')]");
    	waitForElementPresentByXpath("//a[contains(text(),'copy')]");
    	waitForElementPresentByXpath("//a[contains(text(),'merge')]");
    	waitForElementPresentByXpath("//a[contains(text(),'separate')]");
    	waitForElementPresentByXpath("//a[contains(text(),'transfer')]");
    }

    @Test
    public void testLookUpActionsGroupedInHierarchyBookmark() throws Exception {
        testLookUpActionsGroupedInHierarchy();
        passed();
    }

    @Test
    public void testLookUpActionsGroupedInHierarchyNav() throws Exception {
        testLookUpActionsGroupedInHierarchy();
        passed();
    }
}
