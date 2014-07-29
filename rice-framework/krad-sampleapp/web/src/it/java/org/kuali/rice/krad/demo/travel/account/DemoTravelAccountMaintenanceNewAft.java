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

import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.openqa.selenium.WebElement;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAccountMaintenanceNewAft extends WebDriverLegacyITBase {

    /**
     * //div[@class='fancybox-item fancybox-close']
     */
    public static final String FANCY_BOX_CLOSE_XPATH = "//div[@class='fancybox-item fancybox-close']";

    /**
     * /kr-krad/maintenance?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/maintenance?methodToCall=start&dataObjectClassName=org.kuali.rice.krad.demo.travel.dataobject.TravelAccount&hideReturnLink=true";

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
     * Travel account name field
     */
    public static final String TRAVEL_ACCOUNT_NAME_FIELD = "document.newMaintainableObject.dataObject.name";

    /**
     * Travel account nUMBER field
     */
    public static final String TRAVEL_ACCOUNT_NUMBER_FIELD = "document.newMaintainableObject.dataObject.number";

    /**
     * Travel account type code field
     */
    public static final String TRAVEL_ACCOUNT_TYPE_CODE_FIELD = "document.newMaintainableObject.dataObject.accountTypeCode";

    /**
     * Travel sub account field
     */
    public static final String SUB_ACCOUNT_FIELD_XPATH = "//div[@data-label='Travel Sub Account Number']/fieldset/input";

    /**
     * Travel sub account name field
     */
    public static final String SUB_ACCOUNT_NAME_FIELD_XPATH = "//div[@data-label='Sub Account Name']/input";

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
        waitAndClickDemoLink();
        waitAndClickByLinkText("Travel Account Maintenance (New)");
    }

    protected void testTravelAccountMaintenanceNew() throws Exception {
        waitForDocIdKrad();
        waitAndTypeByName("document.documentHeader.documentDescription","Travel Account Maintenance New Test Document");
        String randomCode = RandomStringUtils.randomAlphabetic(9).toUpperCase();
        waitAndTypeByName("document.newMaintainableObject.dataObject.number",randomCode);
        waitAndTypeByName("document.newMaintainableObject.dataObject.name","Test Account Name");
        waitAndClickByXpath("//button[@class='btn btn-default uif-action icon-search']",
                "Travel Account Type Code icon-search link not found");
        gotoLightBox();
        waitAndClickSearchByText();
        waitForElementNotPresent(By.xpath("//button[contains(text(),'Add New Line')]"));
        waitAndClickLinkContainingText("return value");
        waitAndClickByXpath("//a/span[contains(text(),'Ad Hoc Recipients')]");
        waitAndClickByXpath("//div[@data-parent='Uif-AdHocPersonCollection']/div/div/button[@class='btn btn-default uif-action icon-search']");
        gotoLightBox();
        waitAndClickSearchByText();
        waitAndClickLinkContainingText("return value");
        waitAndClickByXpath("//div[@data-parent='Uif-AdHocPersonCollection']/fieldset/div/button");
        waitAndClickByXpath("//div[@data-parent='CollectionGroup_AdHocWorkgroup']/div/div/button[@class='btn btn-default uif-action icon-search']");
        gotoLightBox();
        waitAndClickSearchByText();
        waitAndClickLinkContainingText("return value");
        waitAndClickByXpath("//div[@data-parent='CollectionGroup_AdHocWorkgroup']/fieldset/div/button");
        waitForTextPresent("Delete"); // wait for ajax to finish adding ad hoc group
        waitAndClickSubmitByText();
        waitAndClickConfirmationOk();
        failOnErrorMessageItem();
        waitForTextPresent("Document was successfully submitted.", WebDriverUtils.configuredImplicityWait() * 2);

        //unlock record
        WebElement webElement = findElement(By.xpath("./html/body/form/div/header/div/table/tbody/tr[1]/td[1]/div"));
        String documentId = webElement.getText();
        if(documentId != "") {
            open(getBaseUrlString() + "/kew/DocHandler.do?docId=" + documentId + "&command=displayActionListView");
            waitAndClickByXpath("/html/body/form/div/div[2]/main/section[7]/header/h3/a/span/span[1]");
            waitAndTypeByXpath("/html/body/form/div/div[2]/main/section[7]/div/div[1]/textarea", "test");
            waitAndClickByXpath("/html/body/form/div/div[2]/main/section[7]/div/div[2]/button[2]");
        }
    }

    protected void testTravelAccountMaintenanceEditXss() throws Exception {
        waitForDocIdKrad();
    	checkForRequiredFields();
        waitAndTypeByName(DESCRIPTION_FIELD,"\"/><script>alert('!')</script>");
        waitAndTypeByName(EXPLANATION_FIELD,"\"/><script>alert('!')</script>");
        waitAndTypeByName(ORGANIZATION_DOCUMENT_NUMBER_FIELD,"\"/><script>alert('!')</script>");
        waitAndTypeByName(TRAVEL_ACCOUNT_NAME_FIELD,"Xss");
        waitAndTypeByName(TRAVEL_ACCOUNT_NUMBER_FIELD,"Xss");
        waitAndClickByXpath("//input[@name='document.newMaintainableObject.dataObject.accountTypeCode' and @value='CAT']");
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.subAccounts'].subAccount","a1");
        waitAndTypeByXpath(SUB_ACCOUNT_NAME_FIELD_XPATH,"\"/><script>alert('!')</script>");
        waitAndTypeByName(FISCAL_OFFICER_ID_FIELD,"\"/><script>alert('!')</script>");
        waitAndClickSaveByText();
        Thread.sleep(1000);
        if(isAlertPresent())    {
            fail("XSS vulnerability identified.");
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
        waitAndClickSubmitByText();
        waitForElementPresentByXpath("//h3[contains(text(),'This page has 4 errors')]");
        waitForElementPresentByXpath("//ul[@id='pageValidationList']/li/a[contains(text(),'Document Overview: 1 error')]");
        waitForElementPresentByXpath("//ul[@id='pageValidationList']/li/a[contains(text(),'Account Information: 3 errors')]");
        waitForElementPresentByXpath("//div[@class='uif-messageCount' and contains(text(),'  1 error')]");
        waitForElementPresentByXpath("//div[@class='uif-messageCount' and contains(text(),'  3 errors')]");
        waitForElementPresentByXpath("//div[@class='uif-messageCount' and contains(text(),'  1 error')]");
    	String requiredMessage []={"Description: Required","Travel Account Number: Required","Travel Account Name: Required","Travel Account Type Code: Required"};
    	assertTextPresent(requiredMessage);
        assertTrue(findElement(By.xpath("//h3[@id='pageValidationHeader']")).getText().contains("This page has"));
        waitAndClickSaveByText();
    	assertTextPresent(requiredMessage);
        waitAndClickBlanketApprove();
    	assertTextPresent(requiredMessage);
    }

    public boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        }   // try
        catch (Exception Ex) {
            return false;
        }   // catch
    }

    @Test
    public void testDemoTravelAccountMaintenanceNewBookmark() throws Exception {
        testTravelAccountMaintenanceEditXss();
        testTravelAccountMaintenanceNew();
        passed();
    }

    @Test
    public void testDemoTravelAccountMaintenanceNewNav() throws Exception {
        testTravelAccountMaintenanceEditXss();
        testTravelAccountMaintenanceNew();
        passed();
    }
}
