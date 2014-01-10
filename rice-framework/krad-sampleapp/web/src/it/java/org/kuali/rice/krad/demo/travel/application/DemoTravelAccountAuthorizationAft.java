/**
 * Copyright 2005-2013 The Kuali Foundation
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

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountAuthorizationAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/approval?methodToCall=docHandler&command=initiate&docTypeName=TravelAuthorization
     */
    public static final String BOOKMARK_URL = "/kr-krad/approval?methodToCall=docHandler&command=initiate&docTypeName=TravelAuthorization";

    /**
     * submit
     */
    public static final String SUBMIT = "submit";
    
    /**
     * Search
     */
    public static final String SEARCH = "Search";
    
    /**
     * return value
     */
    public static final String RETURN_VALUE = "return value";
    
    /**
     * blanket approve
     */
    public static final String BLANKET_APPROVE = "blanket approve";

    /**
     * document.documentHeader.documentDescription
     */
    public static final String DESCRIPTION_NAME = "document.documentHeader.documentDescription";

    /**
     * document.cellPhoneNumber
     */
    public static final String CONTACT_NUMBER_NAME = "document.cellPhoneNumber";
    
    /**
     * //div[@id='Uif-PageContentWrapper']/div/div[4]/div[2]/div/table/tbody/tr/
     */
    public static final String ACTUAL_EXPENSE_GENERIC_XPATH = "//div[@id='Uif-PageContentWrapper']/div/div[4]/div[2]/div/table/tbody/tr/";
    
    /**
     * //div[@id='Uif-PageContentWrapper']/div/div[5]/div[2]/div/table/tbody/tr/
     */
    public static final String DAILY_COST_ESTIMATE_GENERIC_XPATH = "//div[@id='Uif-PageContentWrapper']/div/div[5]/div[2]/div/table/tbody/tr/";
    
    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Travel Authorization Document");
    }

    protected void testTravelAccountAuthorizationSubmit() throws Exception {
    	testTravelAccountAuthorization();
    	waitAndClickButtonByText("submit");
// No isn't visible but Yes is?!
//        waitForElementVisibleBy(By.xpath("//label[contains(text(), 'No')]"), "").click();
//        waitAndClick(By.xpath("//label[contains(text(), 'No')]"));
//        waitAndClickButtonByText("submit");
        waitAndClick(By.xpath("//label[contains(text(), 'Yes')]"));
    	waitForTextPresent("Document was successfully");
    }

    protected void testTravelAccountAuthorizationBlanketApprove() throws Exception {
    	testTravelAccountAuthorization();
    	waitAndClickButtonByText("blanket approve");
    	waitForTextPresent("Document was successfully approved.");
    }

    protected void testTravelAccountAuthorization() throws Exception {
    	waitAndTypeByName(DESCRIPTION_NAME,"Travel Authorization Description");
    	waitAndTypeByName(CONTACT_NUMBER_NAME,"918000884215");
    	waitAndClickByXpath("//a[@id='travelerQuickfinder_quickfinder_act']");
    	waitAndGetReturnValue();

        // KNS/KRAD Equiv M38
    	waitAndClickByXpath("//div[@data-label='Primary Destination Id']/div/span/a");
    	waitAndGetReturnValue();
    	
    	//Actual Expense
    	waitAndTypeByXpath(ACTUAL_EXPENSE_GENERIC_XPATH+"td[2]/div/input","Kuali");
    	selectByXpath(ACTUAL_EXPENSE_GENERIC_XPATH+"td[3]/div/select","Airfare");
    	waitAndTypeByXpath(ACTUAL_EXPENSE_GENERIC_XPATH+"td[4]/div/input","Expence Description");
    	waitAndTypeByXpath(ACTUAL_EXPENSE_GENERIC_XPATH+"td[6]/div/input","123.12");
    	waitAndClickByXpath(ACTUAL_EXPENSE_GENERIC_XPATH+"td[9]/div/fieldset/div/button");
    	
    	//Daily Cost Estimate
    	waitAndTypeByXpath(DAILY_COST_ESTIMATE_GENERIC_XPATH+"td[2]/div/input","12/12/13");
    	selectByXpath(DAILY_COST_ESTIMATE_GENERIC_XPATH+"td[3]/div/div/select","Colorado");
    	waitAndTypeByXpath(DAILY_COST_ESTIMATE_GENERIC_XPATH+"td[4]/div/input","100");
    	waitAndTypeByXpath(DAILY_COST_ESTIMATE_GENERIC_XPATH+"td[5]/div/input","100");
    	waitAndTypeByXpath(DAILY_COST_ESTIMATE_GENERIC_XPATH+"td[6]/div/input","100");
    	waitAndTypeByXpath(DAILY_COST_ESTIMATE_GENERIC_XPATH+"td[7]/div/input","200");
    	selectByXpath(DAILY_COST_ESTIMATE_GENERIC_XPATH+"td[8]/div/div/select","DO");
    	waitAndTypeByXpath(DAILY_COST_ESTIMATE_GENERIC_XPATH+"td[9]/div/input","10");
    	waitAndClickByXpath(DAILY_COST_ESTIMATE_GENERIC_XPATH+"td[10]/div/fieldset/div/button");
    }
    
    protected void waitAndGetReturnValue() throws Exception {
    	gotoLightBox();
    	waitAndClickButtonByText(SEARCH);
    	waitAndClickByLinkText(RETURN_VALUE);
    }
    
    @Test
    public void testTravelAccountAuthorizationSubmitNav() throws Exception {
        testTravelAccountAuthorizationSubmit();
        passed();
    }
    
    @Test
    public void testTravelAccountAuthorizationBlanketApproveNav() throws Exception {
        testTravelAccountAuthorizationBlanketApprove();
        passed();
    }

    @Test
    public void testTravelAccountAuthorizationSubmitBookmark() throws Exception {
    	testTravelAccountAuthorizationSubmit();
        passed();
    }
    
    @Test
    public void testTravelAccountAuthorizationBlanketApproveBookmark() throws Exception {
        testTravelAccountAuthorizationBlanketApprove();
        passed();
    }
}
