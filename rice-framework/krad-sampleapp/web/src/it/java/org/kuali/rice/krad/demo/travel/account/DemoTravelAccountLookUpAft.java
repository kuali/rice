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
package org.kuali.rice.krad.demo.travel.account;

import org.kuali.rice.krad.demo.ViewDemoAftBase;
import org.junit.Ignore;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountLookUpAft extends ViewDemoAftBase {

    /**
     * /kr-krad/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true&showMaintenanceLinks=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true&showMaintenanceLinks=true";
    
    /**
     * Search
     */
    public static final String SEARCH = "Search";
    
    /**
     * Clear Values
     */
    public static final String CLEAR_VALUES = "Clear Values";

    /**
     * Travel account number field
     */
    public static final String TRAVEL_ACCOUNT_NUMBER_FIELD = "lookupCriteria[number]";

    /**
     * Travel account name field
     */
    public static final String TRAVEL_ACCOUNT_NAME_FIELD = "lookupCriteria[name]";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Travel Account Lookup");
    }

    protected void testTravelAccountLookUp() throws Exception {
    	//Search by Travel Account Number
        waitAndTypeByName(TRAVEL_ACCOUNT_NUMBER_FIELD, "a1");
        waitAndClickButtonByText(SEARCH);
        waitForElementPresentByXpath("//a[contains(text(), 'a1')]");
        waitAndClickButtonByText(CLEAR_VALUES);
        
        //Inquiry check on Travel Account Number
        waitAndTypeByName(TRAVEL_ACCOUNT_NUMBER_FIELD, "a1");
        waitAndClickByXpath("//button[@title='Direct Inquiry']");
        gotoLightBox();
        waitForElementPresentByXpath("//a[contains(text(),'a1')]");
        waitForTextPresent("Travel Account 1");
        waitAndClickButtonByText("Close");
        selectTopFrame();
        
        //Search by Travel Account Name
        waitAndTypeByName("lookupCriteria[name]","Travel Account 1");
        waitAndClickButtonByText(SEARCH);
        waitForElementPresentByXpath("//a[contains(text(),'a1')]");
        waitForTextPresent("Travel Account 1");
        waitAndClickButtonByText(CLEAR_VALUES);
         
        //Search by Travel Account Type Code
        testSearchAndSelect("CAT");
        waitAndClickButtonByText(SEARCH);
        waitForTextPresent("CAT - Clearing");
        waitAndClickButtonByText(CLEAR_VALUES);
        testSearchAndSelect("EAT");
        waitAndClickButtonByText(SEARCH);
        waitForTextPresent("EAT - Expense");
        waitAndClickButtonByText(CLEAR_VALUES);
        testSearchAndSelect("IAT");
        waitAndClickButtonByText(SEARCH);
        waitForTextPresent("IAT - Income");
        waitAndClickButtonByText(CLEAR_VALUES);
        
        //LookUp on Travel Account Type Code
        waitAndClickByXpath("//input[@name='lookupCriteria[accountTypeCode]' and @value='CAT']");
        waitAndClickByXpath("//button[@class='btn btn-default uif-action icon-search']");
        gotoLightBox();
        waitForElementPresentByXpath("//input[@name='lookupCriteria[accountTypeCode]' and @value='CAT']");
        waitAndClickButtonByText("Close");
        selectTopFrame();

        //Search by Travel Account Date Created
        waitAndTypeByName("lookupCriteria[createDate]", "06/01/2014");
        waitAndClickButtonByText(SEARCH);
        waitForElementPresentByXpath("//a[contains(text(),'a16')]");
        waitForTextPresent("fran");
        waitAndClickButtonByText(CLEAR_VALUES);
        
        //Search by Travel Account Fiscal Officer
        waitAndTypeByName("lookupCriteria[fiscalOfficer.principalName]","fred");
        waitAndClickButtonByText(SEARCH);
        waitForElementPresentByXpath("//a[contains(text(),'a1')]");
        waitForTextPresent("fred");
        waitAndClickButtonByText(CLEAR_VALUES);
        
        //Search by Default (No filters)
        waitAndClickButtonByText(SEARCH);
        By[] bysPresent = new By[] {By.xpath("//a[contains(text(), 'a1')]"), By.xpath("//a[contains(text(), 'a2')]")};
        assertElementsPresentInResultPages(bysPresent);
    }

    protected void testTravelAccountLookUpXss(String fieldName) throws Exception {
        waitAndTypeByName(fieldName,"\"/><script>alert('!')</script>");
        waitAndClickButtonByText(SEARCH);
        Thread.sleep(1000);
        if(isAlertPresent())    {
            jiraAwareFail(fieldName + " caused XSS.");
        }
        waitAndClickButtonByText(CLEAR_VALUES);
        Thread.sleep(1000);
    }

    public boolean isAlertPresent()
    {
        try {
            driver.switchTo().alert();
            return true;
        }  catch (Exception Ex) {
            return false;
        } 
    }

    private void testTravelAccountLookUpDocumentLocking() throws Exception {
        waitAndTypeByName(TRAVEL_ACCOUNT_NUMBER_FIELD, "a4");
    	waitAndClickButtonByText(SEARCH);
    	waitAndClickByLinkText("edit");
    	waitAndTypeByName("document.documentHeader.documentDescription","Document Locking Description");
    	waitAndClickByLinkText("Ad Hoc Recipients");
        waitAndClickAdHocPersonAdd();
    	gotoLightBox();
    	waitAndClickButtonByText(SEARCH);
    	waitAndClickByLinkText("return value");
    	waitAndClickByXpath("//button[@id='Uif-AdHocPersonCollection_add']");
        waitAndClickAdHocGroupAdd();
    	gotoLightBox();
    	waitAndClickButtonByText(SEARCH);
    	waitAndClickByLinkText("return value");
    	waitAndClickByXpath("//button[@id='CollectionGroup_AdHocWorkgroup_add']");
        waitAndClickSubmitByText();
        waitAndClickConfirmationOk();
    	if(waitForIsTextPresent("Document was successfully submitted.")) {
    		navigate();
            waitAndTypeByName(TRAVEL_ACCOUNT_NUMBER_FIELD, "a4");
    		waitAndClickButtonByText(SEARCH);
        	waitAndClickByLinkText("edit");
        	waitAndTypeByName("document.documentHeader.documentDescription","Document Locking Description");
        	waitAndClickByLinkText("Ad Hoc Recipients");
            waitAndClickAdHocPersonAdd();
        	gotoLightBox();
        	waitAndClickButtonByText(SEARCH);
        	waitAndClickByLinkText("return value");
        	waitAndClickByXpath("//button[@id='Uif-AdHocPersonCollection_add']");
            waitAndClickAdHocGroupAdd();
        	gotoLightBox();
        	waitAndClickButtonByText(SEARCH);
        	waitAndClickByLinkText("return value");
        	waitAndClickByXpath("//button[@id='CollectionGroup_AdHocWorkgroup_add']");
            waitAndClickSubmitByText();
    		waitForTextPresent("This document cannot be Saved or Routed");
    	}
    	else{
            waitForTextPresent("This document cannot be Saved or Routed");
    	}
    }

    @Test
    public void testTravelAccountLookUpBookmark() throws Exception {
        testTravelAccountLookUp();
        testXss();
        passed();
    }

    private void testXss() throws Exception {
        testTravelAccountLookUpXss(TRAVEL_ACCOUNT_NUMBER_FIELD);
        testTravelAccountLookUpXss(TRAVEL_ACCOUNT_NAME_FIELD); // in 2.3 this is readonly (and masked)
//        testTravelAccountLookUpXss(SUB_ACCOUNT_FIELD); removed in 2.4
//        testTravelAccountLookUpXss(SUB_ACCOUNT_NAME_FIELD);  // in 2.3 this is readonly (and masked), removed in 2.4
//        testTravelAccountLookUpXss("lookupCriteria[foId]"); // in 2.3 this has a different name, removed in 2.4
    }
    
    private void testSearchAndSelect(String selectorText) throws Exception {
    	waitAndClickByXpath("//button[@class='btn btn-default uif-action icon-search']");
    	gotoLightBox();
        waitAndClickSearchByText();
    	if(selectorText.equalsIgnoreCase("CAT")){
    		waitAndClickByXpath("//table/tbody/tr[1]/td/div/fieldset/div/a");
    	}
    	if(selectorText.equalsIgnoreCase("EAT")){
    		waitAndClickByXpath("//table/tbody/tr[2]/td/div/fieldset/div/a");
    	}
    	if(selectorText.equalsIgnoreCase("IAT")){
    		waitAndClickByXpath("//table/tbody/tr[3]/td/div/fieldset/div/a");
    	}
    }

    @Test
    public void testTravelAccountLookUpNav() throws Exception {
        testTravelAccountLookUp();
        testXss();
        passed();
    }

    @Test
    public void testTravelAccountLookUpDocumentLockingBookmark() throws Exception {
        testTravelAccountLookUpDocumentLocking();
        passed();
    }

    @Test
    public void testTravelAccountLookUpDocumentLockingNav() throws Exception {
        testTravelAccountLookUpDocumentLocking();
        passed();
    }


}

