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
package org.kuali.rice.krad.labs;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LabsConfigurationAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/labs?viewId=LabsConfigurationView&formKey=b553a5b1-9748-4d86-bd9a-5cb3829f74d2&cacheKey=ge1ddsklfoqa7454h9j07e
     */
    public static final String BOOKMARK_URL = "/kr-krad/labs?viewId=LabsConfigurationView&formKey=b553a5b1-9748-4d86-bd9a-5cb3829f74d2&cacheKey=ge1ddsklfoqa7454h9j07e";
    
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
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
    }

    protected void testLabsConfiguration() throws InterruptedException {
    	testLabsConfigurationWithClassInstantiation();
    }

    private void testLabsConfigurationWithClassInstantiation() throws InterruptedException {
    	waitAndClickByLinkText("Configuration with Class Instantiation");
    	
    	//Search By Number
    	waitAndTypeByName(TRAVEL_ACCOUNT_NUMBER_NAME,"a2");
    	waitAndClickButtonByText(SEARCH_BUTTON_TEXT);
    	waitForTextPresent("a2");
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
    
    private void testConfigurationWithSpringServiceExpression() throws InterruptedException {
    	//Waiting for Incident report to fix.
    }
    
    @Test
    public void testLabsConfigurationBookmark() throws Exception {
    	testLabsConfiguration();
        passed();
    }
}
