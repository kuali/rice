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

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;

/**
 * Tests the pessimistic locking feature in a transactional document.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DemoTravelAuthorizationPessimisticLockingAft extends WebDriverLegacyITBase {

    /**
     * /kr-krad/approval?methodToCall=docHandler&command=initiate&docTypeName=TravelAuthorization
     */
    public static final String BOOKMARK_URL = "/kr-krad/approval?methodToCall=docHandler&command=initiate&docTypeName=TravelAuthorization";

    private static final String DOC_HANDLER_URL = "/kew/DocHandler.do?command=displayDocSearchView&docId=";

    private static final String APPROVER_PRINCIPAL_NAME_VALUE = "edna";
    private static final String EDITOR_PRINCIPAL_NAME_VALUE = "dev1";

    private static final String DOCUMENT_DESCRIPTION_FIELD = "document.documentHeader.documentDescription";
    private static final String CONTACT_NUMBER_FIELD = "document.cellPhoneNumber";

    private static final String DOCUMENT_DESCRIPTION_VALUE = "Test Pessimistic Locking";
    private static final String CONTACT_NUMBER_VALUE = "555-555-5555";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickDemoLink();
        waitAndClickByLinkText("Travel Authorization Document");
    }

    @Override
    public String getUserName() {
        return "admin";
    }

    /**
     * Tests whether a document is locked for editing for any other user opening it before it has been routed.
     *
     * @throws Exception for any test exceptions
     */
    protected void testPessimisticLockingOnSave() throws Exception {
        String documentNumber = createTravelAuthorization();

        openTravelAuthorization(documentNumber, EDITOR_PRINCIPAL_NAME_VALUE);

        waitForTextPresent("This document currently has a full lock");
        waitForElementNotPresent(By.cssSelector("input[name = '" + DOCUMENT_DESCRIPTION_FIELD + "']"));
        waitForElementNotPresent(By.cssSelector("input[name = '" + CONTACT_NUMBER_FIELD + "']"));

        waitForElementNotPresent(By.cssSelector("button[data-submit_data = '{\\\"methodToCall\\\":\\\"save\\\"}']"));
        waitForElementNotPresent(By.cssSelector("button[data-submit_data = '{\\\"methodToCall\\\":\\\"route\\\"}']"));
        waitForElementNotPresent(By.cssSelector("button[data-submit_data = '{\\\"methodToCall\\\":\\\"cancel\\\"}']"));

        openTravelAuthorization(documentNumber, getUserName());

        waitForTextNotPresent("This document currently has a full lock");
        assertElementPresent(By.cssSelector("input[name = '" + DOCUMENT_DESCRIPTION_FIELD + "']"));
        assertElementPresent(By.cssSelector("input[name = '" + CONTACT_NUMBER_FIELD + "']"));

        assertElementPresent(By.cssSelector("button[data-submit_data = '{\\\"methodToCall\\\":\\\"save\\\"}']"), "Save button not found");
        assertElementPresent(By.cssSelector("button[data-submit_data = '{\\\"methodToCall\\\":\\\"route\\\"}']"), "Submit button not found");
        assertElementPresent(By.cssSelector("button[data-submit_data = '{\\\"methodToCall\\\":\\\"cancel\\\"}']"), "Cancel button not found");

        waitAndClickCloseByText();
        waitAndClickConfirmSaveOnClose();
        openTravelAuthorization(documentNumber, EDITOR_PRINCIPAL_NAME_VALUE);

        waitForTextNotPresent("This document currently has a full lock");
        assertElementPresent(By.cssSelector("input[name = '" + DOCUMENT_DESCRIPTION_FIELD + "']"));
        assertElementPresent(By.cssSelector("input[name = '" + CONTACT_NUMBER_FIELD + "']"));

        assertElementPresent(By.cssSelector("button[data-submit_data = '{\\\"methodToCall\\\":\\\"save\\\"}']"), "Save button not found");
        assertElementPresent(By.cssSelector("button[data-submit_data = '{\\\"methodToCall\\\":\\\"route\\\"}']"), "Submit button not found");
        assertElementPresent(By.cssSelector("button[data-submit_data = '{\\\"methodToCall\\\":\\\"cancel\\\"}']"), "Cancel button not found");

        openTravelAuthorization(documentNumber, getUserName());

        waitForTextPresent("This document currently has a full lock");
        waitForElementNotPresent(By.cssSelector("input[name = '" + DOCUMENT_DESCRIPTION_FIELD + "']"));
        waitForElementNotPresent(By.cssSelector("input[name = '" + CONTACT_NUMBER_FIELD + "']"));

        waitForElementNotPresent(By.cssSelector("button[data-submit_data = '{\\\"methodToCall\\\":\\\"save\\\"}']"));
        waitForElementNotPresent(By.cssSelector("button[data-submit_data = '{\\\"methodToCall\\\":\\\"route\\\"}']"));
        waitForElementNotPresent(By.cssSelector("button[data-submit_data = '{\\\"methodToCall\\\":\\\"cancel\\\"}']"));
    }

    /**
     * Tests whether a document is locked for editing for any other user opening it after it has been routed.
     *
     * @throws Exception for any test exceptions
     */
    protected void testPessimisticLockingOnRoute() throws Exception {
        String documentNumber = createTravelAuthorization();

        addAdHocRecipient(ActionRequestType.APPROVE.name(), APPROVER_PRINCIPAL_NAME_VALUE, getUserName());

        submitSuccessfully();

        openTravelAuthorization(documentNumber, APPROVER_PRINCIPAL_NAME_VALUE);

        waitForTextPresent("This document currently has a full lock");
        waitForElementNotPresent(By.cssSelector("input[name = '" + DOCUMENT_DESCRIPTION_FIELD + "']"));
        waitForElementNotPresent(By.cssSelector("input[name = '" + CONTACT_NUMBER_FIELD + "']"));

        waitForElementNotPresent(By.cssSelector("button[data-submit_data = '{\\\"methodToCall\\\":\\\"blanketApprove\\\"}']"));

        openTravelAuthorization(documentNumber, getUserName());

        waitForTextNotPresent("This document currently has a full lock");
        assertElementPresent(By.cssSelector("input[name = '" + DOCUMENT_DESCRIPTION_FIELD + "']"));
        assertElementPresent(By.cssSelector("input[name = '" + CONTACT_NUMBER_FIELD + "']"));

        assertElementPresent(By.cssSelector("button[data-submit_data = '{\\\"methodToCall\\\":\\\"blanketApprove\\\"}']"), "Blanket Approve not found");

        waitAndClickCloseByText();

        openTravelAuthorization(documentNumber, APPROVER_PRINCIPAL_NAME_VALUE);

        waitForTextNotPresent("This document currently has a full lock");
        assertElementPresent(By.cssSelector("input[name = '" + DOCUMENT_DESCRIPTION_FIELD + "']"));
        assertElementPresent(By.cssSelector("input[name = '" + CONTACT_NUMBER_FIELD + "']"));

        waitForElementNotPresent(By.cssSelector("button[data-submit_data = '{\\\"methodToCall\\\":\\\"blanketApprove\\\"}']"));

        openTravelAuthorization(documentNumber, getUserName());

        waitForTextPresent("This document currently has a full lock");
        waitForElementNotPresent(By.cssSelector("input[name = '" + DOCUMENT_DESCRIPTION_FIELD + "']"));
        waitForElementNotPresent(By.cssSelector("input[name = '" + CONTACT_NUMBER_FIELD + "']"));

        waitForElementNotPresent(By.cssSelector("button[data-submit_data = '{\\\"methodToCall\\\":\\\"blanketApprove\\\"}']"));
    }

    /**
     * Tests whether a document is unlocked when routed.
     *
     * @throws Exception for any test exceptions
     */
    protected void testPessimisticLockingUnlockingOnRoute() throws Exception {
        String documentNumber = createTravelAuthorization();

        addAdHocRecipient(ActionRequestType.APPROVE.name(), APPROVER_PRINCIPAL_NAME_VALUE);

        submitSuccessfully(WebDriverUtils.configuredImplicityWait() * 5);

        waitAndClickCloseByText();

        openTravelAuthorization(documentNumber, EDITOR_PRINCIPAL_NAME_VALUE);

        waitForTextNotPresent("This document currently has a full lock");
    }

    /**
     * Tests whether a document is unlocked when approved.
     *
     * @throws Exception for any test exceptions
     */
    protected void testPessimisticLockingUnlockingOnApprove() throws Exception {
        String documentNumber = createTravelAuthorization();

        addAdHocRecipient(ActionRequestType.APPROVE.name(), APPROVER_PRINCIPAL_NAME_VALUE);

        submitSuccessfully();

        waitAndClickCloseByText();

        openTravelAuthorization(documentNumber, APPROVER_PRINCIPAL_NAME_VALUE);

        waitAndClickApproveByText();

        openTravelAuthorization(documentNumber, EDITOR_PRINCIPAL_NAME_VALUE);

        waitForTextNotPresent("This document currently has a full lock");
    }

    /**
     * Tests whether a document is unlocked when disapproved.
     *
     * @throws Exception for any test exceptions
     */
    protected void testPessimisticLockingUnlockingOnDisapprove() throws Exception {
        String documentNumber = createTravelAuthorization();

        addAdHocRecipient(ActionRequestType.APPROVE.name(), APPROVER_PRINCIPAL_NAME_VALUE);

        submitSuccessfully();

        waitAndClickCloseByText();

        openTravelAuthorization(documentNumber, APPROVER_PRINCIPAL_NAME_VALUE);

        waitAndClickDisapproveByText();
        waitAndType(By.cssSelector("div[data-parent = 'DisapproveExplanationDialog'] textarea"), "Document incomplete");
        waitAndClick(By.cssSelector("div[data-parent = 'DisapproveExplanationDialog'] button[data-response = 'true']"));
        acceptAlertIfPresent();

        openTravelAuthorization(documentNumber, EDITOR_PRINCIPAL_NAME_VALUE);

        waitForTextNotPresent("This document currently has a full lock");
    }

    /**
     * Tests whether a document is unlocked when cancelled.
     *
     * @throws Exception for any test exceptions
     */
    protected void testPessimisticLockingUnlockingOnCancel() throws Exception {
        String documentNumber = createTravelAuthorization();
        waitAndClick(By.cssSelector("button[data-submit_data = '{\\\"methodToCall\\\":\\\"cancel\\\"}']"));
        waitAndClick(By.cssSelector("div[data-parent = 'ConfirmCancelDialog'] button[data-response = 'true']"));

        openTravelAuthorization(documentNumber, EDITOR_PRINCIPAL_NAME_VALUE);

        waitForTextNotPresent("This document currently has a full lock");
    }

    /**
     * Tests whether a document is unlocked when acknowledged.
     *
     * @throws Exception for any test exceptions
     */
    protected void testPessimisticLockingUnlockingOnAcknowledge() throws Exception {
        String documentNumber = createTravelAuthorization();

        addAdHocRecipient(ActionRequestType.ACKNOWLEDGE.name(), APPROVER_PRINCIPAL_NAME_VALUE);

        submitSuccessfully();

        waitAndClickCloseByText();

        openTravelAuthorization(documentNumber, APPROVER_PRINCIPAL_NAME_VALUE);

        waitAndClickAcknowledgeByText();

        openTravelAuthorization(documentNumber, EDITOR_PRINCIPAL_NAME_VALUE);

        waitForTextNotPresent("This document currently has a full lock");
    }

    private String createTravelAuthorization() throws Exception {
        String documentNumber = waitForDocIdKrad();

        waitAndTypeByName(DOCUMENT_DESCRIPTION_FIELD, DOCUMENT_DESCRIPTION_VALUE);

        waitAndTypeByName(CONTACT_NUMBER_FIELD, CONTACT_NUMBER_VALUE);

        waitAndClick(By.cssSelector("div[data-label = 'Primary Destination Id'] button"));
        gotoLightBox();
        waitAndClickButtonByText(SEARCH);
        waitAndClickByLinkText(RETURN_VALUE_LINK_TEXT);

        saveSuccessfully();

        return documentNumber;
    }

    private void addAdHocRecipient(String actionRequested, String... principalNames) throws Exception {
        waitAndClickByLinkText("Ad Hoc Recipients");

        for (String principalName : principalNames) {
            waitAndSelectByName("newCollectionLines['document.adHocRoutePersons'].actionRequested", actionRequested);
            waitAndTypeByName("newCollectionLines['document.adHocRoutePersons'].id", principalName);
            waitAndClickById("Uif-AdHocPersonCollection_add");
        }

        waitAndClickByLinkText("Ad Hoc Recipients");
    }

    private void openTravelAuthorization(String documentNumber, String principalName) throws Exception {
        open(getBaseUrlString() + "/portal.do");

        acceptAlertIfPresent();

        impersonateUser(principalName);

        open(getBaseUrlString() + DOC_HANDLER_URL + documentNumber);
    }

    @Test
    public void testPessimisticLockingOnSaveBookmark() throws Exception {
        testPessimisticLockingOnSave();
        passed();
    }

    @Test
    public void testPessimisticLockingOnSaveNav() throws Exception {
        testPessimisticLockingOnSave();
        passed();
    }

    @Ignore("https://jira.kuali.org/browse/KULRICE-13330 AFT Failure DemoTravelAuthorizationPessimisticLockingAft.testPessimisticLockingOnRoute full lock is not present in CI")
    @Test
    public void testPessimisticLockingOnRouteBookmark() throws Exception {
        testPessimisticLockingOnRoute();
        passed();
    }

    @Ignore("https://jira.kuali.org/browse/KULRICE-13330 AFT Failure DemoTravelAuthorizationPessimisticLockingAft.testPessimisticLockingOnRoute full lock is not present in CI")
    @Test
    public void testPessimisticLockingOnRouteNav() throws Exception {
        testPessimisticLockingOnRoute();
        passed();
    }

    @Ignore ("https://jira.kuali.org/browse/KULRICE-13357 AFT Failures Dialogs on CI")
    @Test
    public void testPessimisticLockingUnlockingOnRouteBookmark() throws Exception {
        testPessimisticLockingUnlockingOnRoute();
        passed();
    }

    @Test
    public void testPessimisticLockingUnlockingOnRouteNav() throws Exception {
        testPessimisticLockingUnlockingOnRoute();
        passed();
    }

    @Ignore ("https://jira.kuali.org/browse/KULRICE-13357 AFT Failures Dialogs on CI")
    @Test
    public void testPessimisticLockingUnlockingOnApproveBookmark() throws Exception {
        testPessimisticLockingUnlockingOnApprove();
        passed();
    }

    @Test
    public void testPessimisticLockingUnlockingOnApproveNav() throws Exception {
        testPessimisticLockingUnlockingOnApprove();
        passed();
    }

    @Ignore ("https://jira.kuali.org/browse/KULRICE-13357 AFT Failures Dialogs on CI")
    @Test
    public void testPessimisticLockingUnlockingOnDisapproveBookmark() throws Exception {
        testPessimisticLockingUnlockingOnDisapprove();
        passed();
    }

    @Test
    public void testPessimisticLockingUnlockingOnDisapproveNav() throws Exception {
        testPessimisticLockingUnlockingOnDisapprove();
        passed();
    }

    @Test
    public void testPessimisticLockingUnlockingOnCancelBookmark() throws Exception {
        testPessimisticLockingUnlockingOnCancel();
        passed();
    }

    @Test
    public void testPessimisticLockingUnlockingOnCancelNav() throws Exception {
        testPessimisticLockingUnlockingOnCancel();
        passed();
    }

    @Ignore ("https://jira.kuali.org/browse/KULRICE-13357 AFT Failures Dialogs on CI")
    @Test
    public void testPessimisticLockingUnlockingOnAcknowledgeBookmark() throws Exception {
        testPessimisticLockingUnlockingOnAcknowledge();
        passed();
    }

    @Ignore ("https://jira.kuali.org/browse/KULRICE-13357 AFT Failures Dialogs on CI")
    @Test
    public void testPessimisticLockingUnlockingOnAcknowledgeNav() throws Exception {
        testPessimisticLockingUnlockingOnAcknowledge();
        passed();
    }

}
