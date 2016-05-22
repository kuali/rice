/**
 * Copyright 2005-2016 The Kuali Foundation
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
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelCompanySuperUserTabAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/maintenance?methodToCall=start&dataObjectClassName=edu.sampleu.travel.dataobject.TravelCompany
     */
    public static final String BOOKMARK_URL = "/kr-krad/maintenance?methodToCall=start&dataObjectClassName=edu.sampleu.travel.dataobject.TravelCompany";

    /**
     *  Approve
     */
    private static final String APPROVE="Approve";

    /**
     *  Disapprove
     */
    private static final String DISAPPROVE="Disapprove";

    /**
     *  Take Selected Actions
     */
    private static final String TAKE_SELECTED_ACTIONS="Take Selected Actions";

    /**
     * Description field
     */
    public static final String DESCRIPTION_FIELD = "document.documentHeader.documentDescription";

    /**
     * Travel account name field
     */
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
        if ((testName.getMethodName().equals("testDemoTravelCompanySuperUserTabNotAppearBookmark"))||
            (testName.getMethodName().equals("testDemoTravelCompanySuperUserTabNotAppearNav"))) {
            return "testUser2";
        } else {
            return super.getUserName();
        }
    }

    protected void travelAccountCreateDocument() throws Exception {
        waitForDocIdKrad();
        waitAndTypeByName(DESCRIPTION_FIELD,"Travel Company Super User Test");
        String randomCode = RandomStringUtils.randomAlphabetic(9).toUpperCase();
        waitAndTypeByName(COMPANY_NAME_FIELD, "Company Name " + randomCode);

        waitAndClickByLinkText("Ad Hoc Recipients");
        waitAndTypeByName("newCollectionLines['document.adHocRoutePersons'].id", "user1");
        jGrowl("Click Add button");
        waitAndClickById("Uif-AdHocPersonCollection_add");
        waitForElementPresentByXpath(
                "//div[@data-parent=\"Uif-AdHocPersonCollection\"]/div/span[contains(text(), 'user1']");
        waitAndClickByLinkText("Ad Hoc Recipients");

        submitSuccessfully(WebDriverUtils.configuredImplicityWait() * 8);
        waitAndClickButtonByText("Reload");
        waitForProgressLoading();
        checkForDocErrorKrad();
        waitForTextPresent("Document was successfully reloaded.");
    }

    protected void travelAccountCreateDocumentWith2Adhoc() throws Exception {
        waitForDocIdKrad();
        waitAndTypeByName(DESCRIPTION_FIELD,"Travel Company Super User Test");
        String randomCode = RandomStringUtils.randomAlphabetic(9).toUpperCase();
        waitAndTypeByName(COMPANY_NAME_FIELD, "Company Name " + randomCode);

        waitAndClickByLinkText("Ad Hoc Recipients");
        waitAndTypeByName("newCollectionLines['document.adHocRoutePersons'].id", "user1");
        jGrowl("Click Add button");
        waitAndClickById("Uif-AdHocPersonCollection_add");
        waitForElementPresentByXpath(
                "//div[@data-parent=\"Uif-AdHocPersonCollection\"]/div/span[contains(text(), 'user1']");

        waitAndTypeByName("newCollectionLines['document.adHocRoutePersons'].id", "user2");
        jGrowl("Click Add button");
        waitAndClickById("Uif-AdHocPersonCollection_add");
        waitForElementPresentByXpath(
                "//div[@data-parent=\"Uif-AdHocPersonCollection\"]/div/span[contains(text(), 'user2']");
        waitAndClickByLinkText("Ad Hoc Recipients");

        submitSuccessfully(WebDriverUtils.configuredImplicityWait() * 16);
        waitAndClickButtonByText("Reload");
        waitForProgressLoading();
        checkForDocErrorKrad();
        waitForTextPresent("Document was successfully reloaded.");
    }

    protected void testTravelAccountSuperUserApprove() throws Exception {
        travelAccountCreateDocument();
        waitAndClickByLinkText("Super User Actions");
        waitAndTypeByName("document.superUserAnnotation","Reason for approval");
        assertElementPresent(By.xpath("//section[@id = 'Uif-SuperUserActionRequests']//a[contains(text(), 'One, User')]"));
        waitAndClickButtonByText(APPROVE);
        waitForProgressLoading(WebDriverUtils.configuredImplicityWait() * 2);
        waitForTextPresent("was superuser approved.");
        reloadAndCheckDocStatus("FINAL");
    }

    protected void testTravelAccountSuperUserDisapprove() throws Exception {
        travelAccountCreateDocument();
        waitAndClickByLinkText("Super User Actions");
        waitAndTypeByName("document.superUserAnnotation","Reason For disapproval");
        assertElementPresent(By.xpath("//section[@id = 'Uif-SuperUserActionRequests']//a[contains(text(), 'One, User')]"));
        waitAndClickButtonByText(DISAPPROVE);
        waitForTextPresent("was superuser disapproved.");
        reloadAndCheckDocStatus("DISAPPROVED");
    }

    protected void testTravelAccountSuperUserTakeActions() throws Exception {
        travelAccountCreateDocumentWith2Adhoc();
        waitAndClickByLinkText("Super User Actions");
        waitAndTypeByName("document.superUserAnnotation","Reason For Taking Action for user1");

        // Submitted document throws an error when no checkbox is selected and the user presses the Take Action button
        waitAndClickButtonByText(TAKE_SELECTED_ACTIONS);
        waitForProgressLoading();
        waitForTextPresent("Super User Actions: 1 error");
        waitForTextPresent("Please select an action to perform");

        checkByName("selectedCollectionLines['document.actionRequests']");
        waitAndClickButtonByText(TAKE_SELECTED_ACTIONS);
        waitForProgressLoading();
        waitForTextPresent("Action request");
        waitForTextPresent("was superuser approved in Document");

        // This is currently broken - Once KULRICE-13093 is fixed this line can be uncommented
        //assertFalse("Remaining action should not be checked", isCheckedByName("selectedCollectionLines['document.actionRequests']"));

        waitAndTypeByName("document.superUserAnnotation","Reason For Taking Action for user2");
        checkByName("selectedCollectionLines['document.actionRequests']");
        waitAndClickButtonByText(TAKE_SELECTED_ACTIONS);
        waitForProgressLoading();
        waitForTextPresent("Action request");
        waitForTextPresent("was superuser approved in Document");

        reloadAndCheckDocStatus("FINAL");
    }

    protected void testTravelAccountSuperUserErrors() throws Exception {
        travelAccountCreateDocument();
        waitAndClickByLinkText("Super User Actions");

        // Submitted document throws an error when a checkbox is selected and the Approve button is pressed
        checkByName("selectedCollectionLines['document.actionRequests']");
        waitAndClickButtonByText(APPROVE);
        waitForProgressLoading();
        waitForTextPresent(
                "button was selected but there are actions checked. Please either uncheck the actions or select the");
        uncheckByName("selectedCollectionLines['document.actionRequests']");

        // Submitted document throws an error when the annotation is not filled in and the Approve button is pressed
        waitAndClickButtonByText(APPROVE);
        waitForProgressLoading();
        waitForTextPresent("Annotation: Please provide an annotation to continue with the approval of this document");

        // Submitted document throws an error when the annotation is not filled in and the Disapprove button is pressed
        waitAndClickButtonByText(DISAPPROVE);
        waitForProgressLoading();
        waitForTextPresent(
                "Annotation: Please provide an annotation to continue with the disapproval of this document");

        // Submitted document throws an error when the annotation is not filled in and the Take Selected Action
        // button is pressed
        waitAndClickButtonByText(TAKE_SELECTED_ACTIONS);
        waitForProgressLoading();
        waitForTextPresent("Annotation: Please provide an annotation to continue taking these actions on the document");

        //Set value in annotation field
        waitAndTypeByName("document.superUserAnnotation", "Reason for disapproval");

        // Submitted document throws an error when a checkbox is selected and the user presses the Disapprove button
        checkByName("selectedCollectionLines['document.actionRequests']");
        waitAndClickButtonByText(DISAPPROVE);
        waitForProgressLoading();
        waitForTextPresent(
                "button was selected but there are actions checked. Please either uncheck the actions or select the");
        uncheckByName("selectedCollectionLines['document.actionRequests']");

        // Go ahead and approve the document
        waitAndClickButtonByText(DISAPPROVE);
        waitForProgressLoading();
        reloadAndCheckDocStatus("DISAPPROVED");
    }

    protected void testTravelAccountSuperUserTabNotAppear() throws Exception {
        travelAccountCreateDocument();

        // Super User tab does not show up when regular user routes the document
        assertTextNotPresent("Super User Actions");
        reloadAndCheckDocStatus("ENROUTE");
    }

    private void reloadAndCheckDocStatus(String docStatus) throws Exception {
        waitAndClickButtonByText("Reload");
        waitForProgressLoading();
        waitForTextPresent("Document was successfully reloaded.");
        assertTextPresent(docStatus);
    }

    @Test
    public void testDemoTravelCompanySuperUserApproveBookmark() throws Exception {
        testTravelAccountSuperUserApprove();
        passed();
    }

    @Test
    public void testDemoTravelCompanySuperUserApproveNav() throws Exception {
        testTravelAccountSuperUserApprove();
        passed();
    }

    @Test
    public void testDemoTravelCompanySuperUserDisapproveBookmark() throws Exception {
        testTravelAccountSuperUserDisapprove();
        passed();
    }

    @Test
    public void testDemoTravelCompanySuperUserDisapproveNav() throws Exception {
        testTravelAccountSuperUserDisapprove();
        passed();
    }

    @Test
    public void testDemoTravelCompanySuperUserTakeActionsBookmark() throws Exception {
        testTravelAccountSuperUserTakeActions();
        passed();
    }

    @Test
    public void testDemoTravelCompanySuperUserTakeActionsNav() throws Exception {
        testTravelAccountSuperUserTakeActions();
        passed();
    }

    @Test
    public void testDemoTravelCompanySuperUserErrorsBookmark() throws Exception {
        testTravelAccountSuperUserErrors();
        passed();
    }

    @Test
    public void testDemoTravelCompanySuperUserErrorsNav() throws Exception {
        testTravelAccountSuperUserErrors();
        passed();
    }

    @Test
    public void testDemoTravelCompanySuperUserTabNotAppearBookmark() throws Exception {
        testTravelAccountSuperUserTabNotAppear();
        passed();
    }

    @Test
    public void testDemoTravelCompanySuperUserTabNotAppearNav() throws Exception {
        testTravelAccountSuperUserTabNotAppear();
        passed();
    }
}
