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

import org.junit.Ignore;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.openqa.selenium.By;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountMaintenanceEditAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/maintenance?methodToCall=maintenanceEdit&number=a14&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/maintenance?methodToCall=maintenanceEdit&number=a14&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true";

    /**
     * Description field
     */
    public static final String DESCRIPTION_FIELD = "document.documentHeader.documentDescription";

    /**
     * Explanation field
     */
    public static final String EXPLANATION_FIELD = "document.documentHeader.explanation";

    /**
     * Organization document number field
     */
    public static final String ORGANIZATION_DOCUMENT_NUMBER_FIELD = "document.documentHeader.organizationDocumentNumber";

    /**
     * Travel sub account field
     */
    public static final String SUB_ACCOUNT_FIELD = "newCollectionLines['document.newMaintainableObject.dataObject.subAccounts'].subAccount";

    /**
     * Travel sub account name field
     */
    public static final String SUB_ACCOUNT_NAME_FIELD = "newCollectionLines['document.newMaintainableObject.dataObject.subAccounts'].subAccountName";

    /**
     * Subsidized percent
     */
    public static final String SUBSIDIZED_PERCENT_FIELD = "document.newMaintainableObject.dataObject.subsidizedPercent";

    /**
     * Date created.
     */
    public static final String DATE_CREATED_FIELD = "document.newMaintainableObject.dataObject.createDate";

    /**
     * Fiscal officer ID
     */
    public static final String FISCAL_OFFICER_ID_FIELD = "document.newMaintainableObject.dataObject.foId";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigate() throws Exception {
        waitAndClickById("Demo-DemoLink", "");
        waitAndClickByLinkText("Travel Account Maintenance (Edit)");
    }

    protected void testTravelAccountMaintenanceEdit() throws Exception {
        waitAndTypeByName("document.documentHeader.documentDescription", "Travel Account Edit"+RandomStringUtils.randomAlphabetic(2));

        // Verify that adding a duplicate Sub Account is not allowed.
        String subAccountDuplicate = "A";
        waitAndTypeByName(SUB_ACCOUNT_FIELD, subAccountDuplicate);
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.subAccounts'].subAccountName", "Sub Account 1"+RandomStringUtils.randomAlphabetic(2));
        waitAndClickButtonByText("Add");
        String errorMessage []={"Duplicate Sub Accounts (Travel Sub Account Number) are not allowed."};
        assertTextPresent(errorMessage);

        // Verify that adding a duplicate Sub Account and Sub Account Name is not allowed.
        waitAndTypeByName(SUB_ACCOUNT_FIELD, subAccountDuplicate);
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.subAccounts'].subAccountName", "Sub Account A");
        waitAndClickButtonByText("Add");
        String errorMessage2 []={"Duplicate Sub Accounts (Travel Sub Account Number) are not allowed."};
        assertTextPresent(errorMessage2);
        
        //Check for LookUp search
        waitAndClickByXpath("//button[@class='btn btn-default uif-action icon-search']");
        gotoLightBox();
        waitAndClickSearchByText();
        waitAndClickReturnValue();
        waitAndClickByXpath("//a/span[contains(text(),'Ad Hoc Recipients')]");
        waitAndClickByXpath("//div[@data-parent='Uif-AdHocPersonCollection']/div/div/button[@class='btn btn-default uif-action icon-search']");
        gotoLightBox();
        waitAndClickSearchByText();
        waitAndClickReturnValue();
        waitAndClickByXpath("//div[@data-parent='CollectionGroup_AdHocWorkgroup']/div/div/button[@class='btn btn-default uif-action icon-search']");
        gotoLightBox();
        waitAndClickSearchByText();
        waitAndClickReturnValue();

        // Add a new sub account
        String subAccount = "Z1" + RandomStringUtils.randomAlphabetic(2);
        waitAndTypeByName(SUB_ACCOUNT_FIELD, subAccount);
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.subAccounts'].subAccountName", "Sub Account 1"+RandomStringUtils.randomAlphabetic(2));
        waitForElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.number' and @value='a14']");
        waitForElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.name' and @value='Travel Account 14']");
        waitForElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.foId' and @value='fran']");
        waitAndClickButtonByText("Add");
        waitForElementPresentByXpath("//a[contains(text(),subAccount)]");

        waitAndClickSaveByText();
        waitForTextPresent("Document was successfully saved.");
        assertTextPresent("SAVED");
        waitAndClickSubmitByText();
        waitAndClickConfirmationOk();
        waitAndClickButtonByText("reload");
        waitForTextPresent("Document was successfully reloaded.");
        assertTextPresent("FINAL");
    }

    protected void testTravelAccountMaintenanceEditBlanketApprove() throws Exception {
        waitAndTypeByName("document.documentHeader.documentDescription", "Travel Account Edit"+RandomStringUtils.randomAlphabetic(2));
        clearTextByName("document.newMaintainableObject.dataObject.subsidizedPercent");
        waitAndTypeByName("document.newMaintainableObject.dataObject.subsidizedPercent", "42");
        waitAndClickById("Demo-DemoLink", "");
        acceptAlert();
        waitAndClickByLinkText("Travel Account Maintenance (Edit)");
        if(!isElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.subsidizedPercent' and @value='42']")) {
            jiraAwareFail("BlanketApprove was not successful. subsidizedPercent should be 42");
        }
        waitAndTypeByName("document.documentHeader.documentDescription", "Travel Account Edit"+RandomStringUtils.randomAlphabetic(2));
        clearTextByName("document.newMaintainableObject.dataObject.subsidizedPercent");
        waitAndClickBlanketApprove();
        waitAndClickConfirmationOk();
    }


    protected void testTravelAccountMaintenanceEditXss() throws Exception {
        waitAndTypeByName(DESCRIPTION_FIELD,"\"/><script>alert('!')</script>");
        waitAndTypeByName(EXPLANATION_FIELD,"\"/><script>alert('!')</script>");
        waitAndTypeByName(ORGANIZATION_DOCUMENT_NUMBER_FIELD,"\"/><script>alert('!')</script>");
        waitAndTypeByName(SUB_ACCOUNT_FIELD,"blah");
        waitAndTypeByName(SUB_ACCOUNT_NAME_FIELD,"\"/><script>alert('!')</script>");
        waitAndTypeByName(SUBSIDIZED_PERCENT_FIELD,"\"/><script>alert('!')</script>");
//        waitAndTypeByName(DATE_CREATED_FIELD,"\"/><script>alert('!')</script>"); // no longer an input field
//        waitAndTypeByName(FISCAL_OFFICER_ID_FIELD,"\"/><script>alert('!')</script>");
        waitAndClickSaveByText();
        Thread.sleep(1000);
        if(isAlertPresent())    {
            fail("XSS vulnerability identified.");
        }
    }

    protected boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        }   // try
        catch (Exception Ex) {
            return false;
        }   // catch
    }
    
    protected void testEditFiscalOfficer() throws Exception {
        String currentFiscalOfficer = findElement(By.name("document.newMaintainableObject.dataObject.foId")).getAttribute("value");
        String newFiscalOfficer = "fran";
        if ("fran".equals(currentFiscalOfficer)) {
            newFiscalOfficer = "eric";
        }

        checkForRequiredFields();
        changeFiscalOfficer(newFiscalOfficer);
        
        // change eric back to fran
        changeFiscalOfficer(currentFiscalOfficer);
    }
    
    protected void testSubAccountOperations() throws Exception {
        waitForElementNotPresent(By.xpath("//button[contains(text(),'Delete')]"));
        waitAndTypeByXpath("//div[@data-label='Travel Sub Account Number']/input","A");
        waitAndTypeByXpath("//div[@data-label='Sub Account Name']/input","Sub Account A");
        waitAndClickButtonByExactText("Add");
        waitForTextPresent("Duplicate Sub Accounts (Travel Sub Account Number) are not allowed.");
    }

    private void changeFiscalOfficer(String newUser) throws Exception {
        waitAndTypeByName("document.documentHeader.documentDescription", "Edit Fiscal Officer to " + newUser + " "  + RandomStringUtils.randomAlphabetic(2));
        clearTextByName("document.newMaintainableObject.dataObject.foId");
        waitAndTypeByName("document.newMaintainableObject.dataObject.foId", newUser);
        waitAndClickBlanketApprove();
        jGrowl("Click OK");
        waitAndClickByXpath("//div[@data-parent='ConfirmBlanketApproveDialog']/button[contains(text(),'OK')]");
        acceptAlert();
        if(!isElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.foId' and @value='" + newUser + "']")) {
            jiraAwareFail("Fiscal Officer Not Changed to " + newUser);
        }
    }

    private void checkForRequiredFields() throws Exception{
    	waitForElementPresentByXpath("//label[contains(text(),'Description')]/span[contains(text(),'*')]");
    	waitForElementPresentByXpath("//label[contains(text(),'Travel Account Number:')]/span[contains(text(),'*')]");
    	waitForElementPresentByXpath("//label[contains(text(),'Travel Account Name:')]/span[contains(text(),'*')]");
    	waitForElementPresentByXpath("//label[contains(text(),'Travel Account Type Code:')]/span[contains(text(),'*')]");
    	waitForElementPresentByXpath("//label[contains(text(),'Date Created:')]/span[contains(text(),'*')]");
    	waitForElementPresentByXpath("//label[contains(text(),'Travel Sub Account Number:')]/span[contains(text(),'*')]");
    	waitForElementPresentByXpath("//label[contains(text(),'Sub Account Name:')]/span[contains(text(),'*')]");
        jGrowl("Verify required messages are displayed");
        clearTextByName("document.newMaintainableObject.dataObject.name");
        waitAndClickSubmitByText();
        waitForElementPresentByXpath("//h3[@id='pageValidationHeader' and contains(text(),'This page has 2 errors')]");
        waitForElementPresentByXpath("//ul[@id='pageValidationList']/li/a[contains(text(),'Document Overview: 1 error')]");
        waitForElementPresentByXpath("//ul[@id='pageValidationList']/li/a[contains(text(),'Account Information: 1 error')]");
        waitForElementPresentByXpath("//div[@class='uif-messageCount' and contains(text(),'  1 error')]");
    	String requiredMessage []={"Description: Required","Travel Account Name: Required"};
    	assertTextPresent(requiredMessage);
        waitAndClickSaveByText();
    	assertTextPresent(requiredMessage);
        waitAndClickBlanketApprove();
    	assertTextPresent(requiredMessage);
    	waitForElementPresentByXpath("//div[@data-label='Date Created']");
    	waitAndTypeByName("document.newMaintainableObject.dataObject.name","Travel Account 14");
    }

    @Test
    @Ignore // https://jira.kuali.org/browse/KULRICE-12799 Won't be fixed in 2.4 - AFT Failure DemoTravelAccountMaintenanceEditAft Blanket Approve doesn't go to FINAL or save changes
    public void testDemoTravelAccountMaintenanceEditBookmark() throws Exception {
        testTravelAccountMaintenanceEdit();
        passed();
    }

    @Test
    @Ignore // https://jira.kuali.org/browse/KULRICE-12799 Won't be fixed in 2.4 - AFT Failure DemoTravelAccountMaintenanceEditAft Blanket Approve doesn't go to FINAL or save changes
    public void testDemoTravelAccountMaintenanceEditNav() throws Exception {
        testTravelAccountMaintenanceEdit();
        passed();
    }

    @Test
    @Ignore // https://jira.kuali.org/browse/KULRICE-12799 Won't be fixed in 2.4 - AFT Failure DemoTravelAccountMaintenanceEditAft Blanket Approve doesn't go to FINAL or save changes
    public void testDemoTravelAccountMaintenanceEditBlanketApproveBookmark() throws Exception {
        testTravelAccountMaintenanceEditBlanketApprove();
        passed();
    }

    @Test
    @Ignore // https://jira.kuali.org/browse/KULRICE-12799 Won't be fixed in 2.4 - AFT Failure DemoTravelAccountMaintenanceEditAft Blanket Approve doesn't go to FINAL or save changes
    public void testDemoTravelAccountMaintenanceEditBlanketApproveNav() throws Exception {
        testTravelAccountMaintenanceEditBlanketApprove();
        passed();
    }

    @Test
    public void testDemoTravelAccountMaintenanceEditXssBookmark() throws Exception {
        testTravelAccountMaintenanceEditXss();
        passed();
    }

    @Test
    public void testDemoTravelAccountMaintenanceEditXssNav() throws Exception {
        testTravelAccountMaintenanceEditXss();
        passed();
    }
    
    @Test
    @Ignore // https://jira.kuali.org/browse/KULRICE-12799 Won't be fixed in 2.4 - AFT Failure DemoTravelAccountMaintenanceEditAft Blanket Approve doesn't go to FINAL or save changes
    public void testDemoTravelAccountMaintenanceEditFiscalOfficerBookmark() throws Exception {
    	testEditFiscalOfficer();
        passed();
    }

    @Test
    @Ignore // https://jira.kuali.org/browse/KULRICE-12799 Won't be fixed in 2.4 - AFT Failure DemoTravelAccountMaintenanceEditAft Blanket Approve doesn't go to FINAL or save changes
    public void testDemoTravelAccountMaintenanceEditFiscalOfficerNav() throws Exception {
        testEditFiscalOfficer();
        passed();
    }

    @Test
    public void testDemoTravelAccountMaintenanceSubAccountOperationsBookmark() throws Exception {
    	testSubAccountOperations();
        passed();
    }
    @Test
    public void testDemoTravelAccountMaintenanceSubAccountOperationsNav() throws Exception {
        testSubAccountOperations();
        passed();
    }
}
