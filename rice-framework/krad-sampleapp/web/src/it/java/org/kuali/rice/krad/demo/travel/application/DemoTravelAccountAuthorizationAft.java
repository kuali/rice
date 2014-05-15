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
    	waitAndClickButtonByText("Submit");
// No isn't visible but Yes is?!
//        waitForElementVisibleBy(By.xpath("//label[contains(text(), 'No')]"), "").click();
//        waitAndClick(By.xpath("//label[contains(text(), 'No')]"));
//        waitAndClickButtonByText("submit");
    	waitAndClickByXpath("//div[@data-parent='ConfirmSubmitDialog']/button[contains(text(),'OK')]");
    }

    protected void testTravelAccountAuthorizationBlanketApprove() throws Exception {
    	testTravelAccountAuthorization();
    	waitAndClickButtonByText("Blanket Approve");
    	waitAndClickByXpath("//div[@data-parent='ConfirmBlanketApproveDialog']/button[contains(text(),'OK')]");
        checkForIncidentReport();
// blanket approve redirects to hub so can no longer test for message
//    	waitForTextPresent("Document was successfully approved.");
    }

    protected void testTravelAccountAuthorization() throws Exception {
    	waitAndTypeByName(DESCRIPTION_NAME,"Travel Authorization Description");
    	waitAndTypeByName("document.documentHeader.organizationDocumentNumber","1");
    	waitAndTypeByName("document.documentHeader.explanation","This is an explanation for Travel Authorization !'");
    	waitAndClickByXpath("//button[@id='travelerQuickfinder_quickfinder_act']");
    	waitAndGetReturnValue();
    	waitAndTypeByName(CONTACT_NUMBER_NAME,"918000884215");
    	waitAndClickByXpath("//div[@data-label='Primary Destination Id']/div/div/button");
    	waitAndGetReturnValue();
    	selectByName("document.travelTypeCode","In State");
    	waitAndTypeByName("document.tripBegin","02/01/2014");
    	waitAndTypeByName("document.tripEnd","02/04/2014");
        jGrowl("Traveler Lookup Quickfinder");
    	waitAndClickByXpath("//button[@id='travelerQuickfinder_quickfinder_act']");
    	waitAndGetReturnValue();

        // KNS/KRAD Equiv M38
        jGrowl("Click Primary Destination Id Quickfinder");
    	waitAndClickByXpath("//div[@data-label='Primary Destination Id']/div/div/button");
    	waitAndGetReturnValue();
        String primaryDestinationId = waitAndGetAttributeByName("document.tripDestinationId", "value");
    	waitAndTypeByName("document.expenseLimit","1000");

    	//Actual Expense
        waitAndTypeByName("newCollectionLines['document.actualExpenseItems'].travelCompanyName", "Kuali");
        selectOptionByName("newCollectionLines['document.actualExpenseItems'].travelExpenseTypeCd", "A"); // Airfare
        waitAndTypeByName("newCollectionLines['document.actualExpenseItems'].expenseDesc", "Expense Description");
        waitAndTypeByName("newCollectionLines['document.actualExpenseItems'].expenseAmount", "123.12");
        jGrowl("Click Actual Expense Items Add Button");
    	waitAndClickByXpath("//button[contains(@data-submit_data, 'document.actualExpenseItems')]");
    	
    	 //AdHoc
        waitAndClickByXpath("//a/span[contains(text(),'Ad Hoc Recipients')]");
        waitAndClickByXpath("//div[@data-parent='Uif-AdHocPersonCollection']/div/div/button[@class='btn btn-default uif-action icon-search']");
        waitAndGetReturnValue();
        waitAndClickByXpath("//div[@data-parent='CollectionGroup_AdHocWorkgroup']/div/div/button[@class='btn btn-default uif-action icon-search']");
        waitAndGetReturnValue();
        
    	//Daily Cost Estimate
    	waitAndTypeByName("newCollectionLines['document.dailyExpenseEstimates'].perDiemDate","12/12/13");
        selectOptionByName("newCollectionLines['document.dailyExpenseEstimates'].travelDestinationId", primaryDestinationId);
        waitAndTypeByName("newCollectionLines['document.dailyExpenseEstimates'].breakfastValue","16");
        waitAndTypeByName("newCollectionLines['document.dailyExpenseEstimates'].lunchValue","18");
        waitAndTypeByName("newCollectionLines['document.dailyExpenseEstimates'].dinnerValue","20");
        waitAndTypeByName("newCollectionLines['document.dailyExpenseEstimates'].incidentalsValue","7");
        selectOptionByName("newCollectionLines['document.dailyExpenseEstimates'].mileageRateId", "10000"); // DO
        waitAndTypeByName("newCollectionLines['document.dailyExpenseEstimates'].estimatedMileage","13");
        waitAndClickByXpath("//tr[@class='uif-collectionAddItem odd']/td[3]/div/div/div/button");
        waitAndGetReturnValue();
        waitAndClickByXpath("//tr[@class='uif-collectionAddItem odd']/td[8]/div/div/div/button");
        waitAndGetReturnValue();
        jGrowl("Click Daily Expense Estimates Add Button");
        clearTextByName("document.expenseLimit");
    }
    
    protected void waitAndGetReturnValue() throws Exception {
    	gotoLightBox();
    	waitAndClickButtonByText(SEARCH);
    	waitAndClickByLinkText(RETURN_VALUE);
    }
    
//    @Test
    public void testTravelAccountAuthorizationSubmitNav() throws Exception {
        testTravelAccountAuthorizationSubmit();
        passed();
    }
    
//    @Test
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
