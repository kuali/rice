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
public class DemoLookUpAdvancedLookUpFeaturesAft extends ViewDemoAftBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LookupAdvancedFeaturesView
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LookupAdvancedFeaturesView";
    
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
     * lookupCriteria[accountTypeCode]
     */
    private static final String TYPE_CODE_NAME="lookupCriteria[accountTypeCode]";
    
    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickDemoLink();
        waitAndClickByLinkText("Advanced lookup features");
    }

    protected void testLookUpAdvancedLookUpFeatures() throws InterruptedException {
    	//Search By Name
    	waitAndTypeByName(TRAVEL_ACCOUNT_NAME,"Travel Account 9");
    	waitAndClickButtonByText(SEARCH_BUTTON_TEXT);
    	waitForTextPresent("Travel Account 9");
    	waitAndClickButtonByText(CLEAR_VALUES_BUTTON_TEXT);
    	
    	//Search By Account Type Code
    	waitAndClickByXpath("//input[@name='"+TYPE_CODE_NAME+"' and @value='CAT']");
    	waitAndClickButtonByText(SEARCH_BUTTON_TEXT);
    	waitForTextPresent("Travel Account 9");
    	waitAndClickButtonByText(CLEAR_VALUES_BUTTON_TEXT);
    }
   
    @Test
    public void testLookUpAdvancedLookUpFeaturesBookmark() throws Exception {
        testLookUpAdvancedLookUpFeatures();
        passed();
    }

    @Test
    public void testLookUpAdvancedLookUpFeaturesNav() throws Exception {
        testLookUpAdvancedLookUpFeatures();
        passed();
    }
}
