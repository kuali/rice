/**
 * Copyright 2005-2017 The Kuali Foundation
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

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelCompanyCompletionRequestAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/maintenance?methodToCall=start&dataObjectClassName=edu.sampleu.travel.dataobject.TravelCompany
     */
    public static final String BOOKMARK_URL = "/kr-krad/maintenance?methodToCall=start&dataObjectClassName=edu.sampleu.travel.dataobject.TravelCompany";

    private static final String DOC_HANDLER_URL = "/kew/DocHandler.do?command=displayDocSearchView&docId=";
    private static final String USER1_PRINCIPAL_NAME = "user1";
    private static final String ADMIN_PRINCIPAL_NAME = "admin";

    private static final String FINAL = "FINAL";
    private static final String CANCELED = "CANCELED";

    private static final String COMPLETE = "Complete";
    private static final String UCANCEL = "ucancel";
    private static final String CANCEL = "Cancel";
    private static final String APPROVE = "Approve";
    private static final String DISAPPROVE = "Disapprove";
    private static final String BLANKET_APPROVE = "Blanket Approve";
    private static final String SUPER_USER_ACTIONS = "Super User Actions";

    public static final String DESCRIPTION_FIELD = "document.documentHeader.documentDescription";
    public static final String COMPANY_NAME_FIELD = "document.newMaintainableObject.dataObject.travelCompanyName";

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    protected void navigate() throws Exception {
        waitAndClickDemoLink();
        waitAndClickByLinkText("Travel Company Lookup");
        waitAndClickByLinkText("Create New");
    }

    @Override
    public String getUserName() {
        if ((testName.getMethodName().equals("testDemoTravelCompanyCompletionRequestForAdminBookmark"))||
                (testName.getMethodName().equals("testDemoTravelCompanyCompletionRequestForAdminNav"))) {
            return USER1_PRINCIPAL_NAME;
        } else {
            return super.getUserName();
        }
    }

    private void openTravelCompanyDoc(String documentNumber, String principalName) throws Exception {
        open(getBaseUrlString() + "/portal.do");
        impersonateUser(principalName);

        open(getBaseUrlString() + DOC_HANDLER_URL + documentNumber);
    }

    protected void travelAccountCreateDocument(String principalName) throws Exception {
        waitAndTypeByName(DESCRIPTION_FIELD,"Travel Company Super User Test");
        String randomCode = RandomStringUtils.randomAlphabetic(9).toUpperCase();
        waitAndTypeByName(COMPANY_NAME_FIELD, "Company Name " + randomCode);

        waitAndClickByLinkText("Ad Hoc Recipients");
        waitAndTypeByName("newCollectionLines['document.adHocRoutePersons'].actionRequested", "Complete");
        waitAndTypeByName("newCollectionLines['document.adHocRoutePersons'].id", principalName);
        jGrowl("Click Add button");
        waitAndClickById("Uif-AdHocPersonCollection_add");
        waitForElementPresentByXpath(
                "//div[@data-parent=\"Uif-AdHocPersonCollection\"]/div/span[contains(text(), principalName]");
        waitAndClickByLinkText("Ad Hoc Recipients");

        waitAndClickSubmitByText();
        waitAndClickConfirmSubmitOk();
        waitForProgress("Loading...", WebDriverUtils.configuredImplicityWait() * 4);
        waitForTextPresent("Document was successfully submitted.", WebDriverUtils.configuredImplicityWait() * 2);
    }

    protected void testDemoTravelCompanyCompletionRequestForAdmin() throws Exception {
        String documentNumber = waitForDocIdKrad();

        travelAccountCreateDocument(ADMIN_PRINCIPAL_NAME);

        openTravelCompanyDoc(documentNumber, ADMIN_PRINCIPAL_NAME);

        //Make sure the document can be edited
        waitAndTypeByName(COMPANY_NAME_FIELD, " Extra");

        // Admin is a super user but the approve and disapprove buttons will not
        // be shown since the completion request was for Admin
        Thread.sleep(3000);
        waitAndClickByLinkText(SUPER_USER_ACTIONS);
        verifyButtons();
        waitAndClickButtonByText(COMPLETE);
        waitForProgress("Loading...", WebDriverUtils.configuredImplicityWait() * 8);

        ReloadAndCheckDocStatus(FINAL);
    }

    protected void testDemoTravelCompanyCompletionRequestForUser1() throws Exception {
        String documentNumber = waitForDocIdKrad();

        travelAccountCreateDocument(USER1_PRINCIPAL_NAME);

        // Admin is a super user so the approve and disapprove buttons should be shown to Admin but not to user1
        waitAndClickByLinkText(SUPER_USER_ACTIONS);
        waitForTextPresent(APPROVE);
        waitForTextPresent(DISAPPROVE);

        openTravelCompanyDoc(documentNumber, USER1_PRINCIPAL_NAME);

        //Make sure the document can be edited
        waitAndTypeByName(COMPANY_NAME_FIELD, " Extra");

        assertTextNotPresent(SUPER_USER_ACTIONS);
        verifyButtons();

        waitAndClickButtonByText(COMPLETE);
        waitForProgress("Loading...", WebDriverUtils.configuredImplicityWait() * 8);

        ReloadAndCheckDocStatus(FINAL);
    }

    protected void testDemoTravelCompanyCompletionRequestForUser1Cancel() throws Exception {
        String documentNumber = waitForDocIdKrad();

        travelAccountCreateDocument(USER1_PRINCIPAL_NAME);

        openTravelCompanyDoc(documentNumber, USER1_PRINCIPAL_NAME);

        Thread.sleep(2000);
        verifyButtons();

        waitAndClickById(UCANCEL);
        waitAndClickByXpath("//div[@data-parent='ConfirmCancelDialog']/button[contains(text(),'OK')]");
        Thread.sleep(2000);

        openTravelCompanyDoc(documentNumber, USER1_PRINCIPAL_NAME);
        assertTextPresent(CANCELED);
    }

    private void ReloadAndCheckDocStatus(String docStatus) throws Exception {
        waitAndClickButtonByText("Reload");
        waitForProgressLoading();
        waitForTextPresent("Document was successfully reloaded.");
        assertTextPresent(docStatus);
    }

    private void verifyButtons() throws Exception {
        Thread.sleep(3000);
        assertTextNotPresent(APPROVE);
        assertTextNotPresent(DISAPPROVE);
        assertTextNotPresent(BLANKET_APPROVE);
        assertTextPresent(CANCEL);
        assertTextPresent(COMPLETE);
    }

    @Test
    public void testDemoTravelCompanyCompletionRequestForUser1Bookmark() throws Exception {
        testDemoTravelCompanyCompletionRequestForUser1();
        passed();
    }

    @Test
    public void testDemoTravelCompanyCompletionRequestForUser1Nav() throws Exception {
        testDemoTravelCompanyCompletionRequestForUser1();
        passed();
    }

    @Test
    public void testDemoTravelCompanyCompletionRequestForAdminBookmark() throws Exception {
        testDemoTravelCompanyCompletionRequestForAdmin();
        passed();
    }

    @Test
    public void testDemoTravelCompanyCompletionRequestForAdminNav() throws Exception {
        testDemoTravelCompanyCompletionRequestForAdmin();
        passed();
    }

    @Test
    public void testDemoTravelCompanyCompletionRequestForUser1CancelBookmark() throws Exception {
        testDemoTravelCompanyCompletionRequestForUser1Cancel();
        passed();
    }

    @Test
    public void testDemoTravelCompanyCompletionRequestForUser1CancelNav() throws Exception {
        testDemoTravelCompanyCompletionRequestForUser1Cancel();
        passed();
    }
}
