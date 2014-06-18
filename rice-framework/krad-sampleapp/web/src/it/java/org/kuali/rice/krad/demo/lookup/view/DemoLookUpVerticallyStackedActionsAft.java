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
public class DemoLookUpVerticallyStackedActionsAft extends ViewDemoAftBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LookupSampleViewVerticalActions
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LookupSampleViewVerticalActions";

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
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Lookup vertically stacked actions");
    }

    protected void testLookUpVerticallyStackedActions() throws InterruptedException {
    	//Search By Number 
    	waitAndTypeByName(TRAVEL_ACCOUNT_NUMBER,"A139638996");
    	waitAndClickButtonByText(SEARCH_BUTTON_TEXT);
    	waitForElementPresentByXpath("//a[contains(text(),'A139638996')]");
    	assertVerticallyStackedActions();
    	waitAndClickButtonByText(CLEAR_VALUES_BUTTON_TEXT);
    	
    	//Search By Name
    	waitAndTypeByName(TRAVEL_ACCOUNT_NAME,"A1396389962525av");
    	waitAndClickButtonByText(SEARCH_BUTTON_TEXT);
    	waitForTextPresent("A1396389962525av");
    	assertVerticallyStackedActions();
    	waitAndClickButtonByText(CLEAR_VALUES_BUTTON_TEXT);
    	
    	//Search By Fiscal Officer
    	waitAndTypeByName(PRINCIPAL_NAME,"fran");
    	waitAndClickButtonByText(SEARCH_BUTTON_TEXT);
    	waitForTextPresent("fran");
    	assertVerticallyStackedActions();
    	waitAndClickButtonByText(CLEAR_VALUES_BUTTON_TEXT);
    }
    
    private void assertVerticallyStackedActions() throws InterruptedException{
    	waitForElementPresentByXpath("//a[contains(text(),'edit') and @class='uif-link uif-boxLayoutVerticalItem clearfix']");
    	waitForElementPresentByXpath("//a[contains(text(),'copy') and @class='uif-link uif-boxLayoutVerticalItem clearfix']");
    }

    @Test
    public void testLookUpVerticallyStackedActionsBookmark() throws Exception {
        testLookUpVerticallyStackedActions();
        passed();
    }

    @Test
    public void testLookUpVerticallyStackedActionsNav() throws Exception {
        testLookUpVerticallyStackedActions();
        passed();
    }
}
