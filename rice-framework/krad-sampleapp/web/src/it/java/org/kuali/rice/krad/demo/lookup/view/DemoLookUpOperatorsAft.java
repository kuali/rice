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
import org.openqa.selenium.By;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoLookUpOperatorsAft extends ViewDemoAftBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=OperatorsLookupSampleView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=OperatorsLookupSampleView&hideReturnLink=true";

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
        waitAndClickByLinkText("Lookup Operators");
    }

    protected void testLookUpOperators() throws InterruptedException {
    	waitAndTestTravelAccountInput("a1*", new String[]{"a1","a10","a11","a12"}, null);
    	waitAndTestTravelAccountInput("!a1", new String[]{"a7","a10","a11","a12","a2","a3","a8","a9"}, null);
    	waitAndTestTravelAccountInput("a1?", new String[]{"a10","a13","a14","a11","a12"}, null);
    	waitAndTestTravelAccountInput("a1|a6", new String[]{"a1","a6"}, "a10");
    	waitAndTestTravelAccountInput("a1..a3", new String[]{"a1","a2","a3","a10","a11","a12","a13","a14"}, null);
    	waitAndTestTravelAccountInput("a1..a3", new String[]{"a1","a2","a3","a10","a11","a12","a13","a14"}, null);
    	waitAndTestTravelAccountInput(">a3", new String[]{"a4","a5","a6","a7","a8","a9"}, "a3");
    	waitAndTestTravelAccountInput(">=a3", new String[]{"a3","a4","a5","a6","a7","a8","a9"}, "a1");
    	waitAndTestTravelAccountInput("<a3", new String[]{"a1","a2","a10","a11","a12","a13","a14"}, "a3");
    	waitAndTestTravelAccountInput("<=a3", new String[]{"a1","a2","a3","a10","a11","a12","a13","a14"}, "a4");
    	waitAndTestTravelAccountNameInput("!NULL",new String[]{"a1","a6","a7","a10","a11","a12","a13","a14"}, "a2");
    	waitAndTypeByName(TRAVEL_ACCOUNT_NAME,"NULL");
    	waitAndClickButtonByText(SEARCH_BUTTON_TEXT);
    	waitForTextPresent("No values match this search.");
    	waitAndTypeByName(PRINCIPAL_NAME,"fr* && *k");
    	waitAndClickButtonByText(SEARCH_BUTTON_TEXT);
    }
    
    private void waitAndTestTravelAccountInput(String inputValue, String [] assertPresentStringValue, String assertNotPresentStringValue) throws InterruptedException{
    	 waitAndTypeByName(TRAVEL_ACCOUNT_NUMBER,inputValue);
         waitAndClickButtonByText(SEARCH_BUTTON_TEXT);
         if(assertPresentStringValue!=null) {
        	 for(int i=0; i<assertPresentStringValue.length ; i++) {
        		 waitForElementPresentByXpath("//table[@id='uLookupResults_layout']/*//a[contains(text(),'"+assertPresentStringValue[i]+"')]");
        	 }
         }
         if(assertNotPresentStringValue!=null) {
        	 waitForElementNotPresent(By.xpath(assertNotPresentStringValue));
         }
         waitAndClickButtonByText(CLEAR_VALUES_BUTTON_TEXT);
    }
    
    private void waitAndTestTravelAccountNameInput(String inputValue, String [] assertPresentStringValue, String assertNotPresentStringValue) throws InterruptedException{
   	 waitAndTypeByName(TRAVEL_ACCOUNT_NAME,inputValue);
        waitAndClickButtonByText(SEARCH_BUTTON_TEXT);
        if(assertPresentStringValue!=null) {
        	for(int i=0; i<assertPresentStringValue.length ; i++) {
	       		 waitForElementPresentByXpath("//table[@id='uLookupResults_layout']/*//a[contains(text(),'"+assertPresentStringValue[i]+"')]");
	       	 }
        }
        if(assertNotPresentStringValue!=null) {
        	waitForElementNotPresent(By.xpath(assertNotPresentStringValue));
        }
        waitAndClickButtonByText(CLEAR_VALUES_BUTTON_TEXT);
   }

    @Test
    public void testLookUpOperatorsBookmark() throws Exception {
        testLookUpOperators();
        passed();
    }

    @Test
    public void testLookUpOperatorsNav() throws Exception {
        testLookUpOperators();
        passed();
    }
}
