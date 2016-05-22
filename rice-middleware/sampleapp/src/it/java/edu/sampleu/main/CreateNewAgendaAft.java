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
package edu.sampleu.main;

import org.junit.Before;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CreateNewAgendaAft extends WebDriverLegacyITBase {

    protected static final String NAME_LABEL = "Name:";
    protected static final String NAMESPACE_LABEL = "Namespace:";
    protected static final String CONTEXT_LABEL = "Context:";
    private boolean useUi = false;

    protected static String[][] inputVerifyDetails;

    /**
     * ITUtil.PORTAL + "?channelTitle=Create%20New%20Agenda&channelUrl=" + WebDriverUtils.getBaseUrlString() +
     *  "/kr-krad/krmsAgendaEditor?methodToCall=start&dataObjectClassName=org.kuali.rice.krms.impl.ui.AgendaEditor&returnLocation=" +
     *  ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Create%20New%20Agenda&channelUrl=" + WebDriverUtils
            .getBaseUrlString() +"/kr-krad/krmsAgendaEditor?methodToCall=start&dataObjectClassName=org.kuali.rice.krms.impl.ui.AgendaEditor&returnLocation=" +
            AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    @Before
    @Override
    public void testSetUp() {
        super.testSetUp();
        inputVerifyDetails = new String[][] {
                {NAME_LABEL, getDescriptionUnique()},
                {NAMESPACE_LABEL, "Kuali Rules Test"},
                {CONTEXT_LABEL, "Context1"}
        };

    }

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    // Lots of Overrides as KRMS was early KRAD
    @Override
    protected void assertBlanketApproveButtonsPresent() {
        assertTrue(isVisible(By.xpath("//button[contains( text(), 'Cancel')]")));
        assertTrue(isVisible(By.xpath("//button[contains( text(), 'Save')]")));
        assertTrue(isVisible(By.xpath("//button[contains( text(), 'Blanket Approve')]")));
        assertTrue(isVisible(By.xpath("//button[contains( text(), 'Submit')]")));
    }

    @Override
    protected void assertRouteStatus(String status) throws InterruptedException {
        super.assertRouteStatus(status);
    }

    private void verify(String docId) throws InterruptedException {
        waitAndClickLinkContainingText(docId);

        driver.switchTo().window((String) driver.getWindowHandles().toArray()[1]);

        waitIsVisible(By.xpath("//tr/th/label[contains(text(), '" + CONTEXT_LABEL + "')]"));
        assertLabeledTextPresent(inputVerifyDetails);
        screenshot();
    }

    private void verifyInputs(String docId) throws InterruptedException {
        waitAndClickLinkContainingText(docId);

        driver.switchTo().window((String) driver.getWindowHandles().toArray()[1]);

        waitIsVisible(By.xpath("//tr/th/label[contains(text(), '" + CONTEXT_LABEL + "')]"));
        assertLabeledInputTextPresent(inputVerifyDetails);
        screenshot();
    }

    @Override
    public void checkForDocError() {
        checkForDocErrorKrad();
    }

    @Override
    protected void waitAndClickBlanketApproveKns() throws InterruptedException {
        waitAndClickBlanketApprove();
        waitAndClickConfirmBlanketApproveOk();
        acceptAlertIfPresent();
        waitForProgressLoading();
    }

    @Override
    protected void waitAndCancelConfirmation() throws InterruptedException {
        waitAndClickCancel();
        acceptAlertIfPresent();
        waitAndClickConfirmCancelOk();
    }

    @Override
    protected void waitAndClickCancel() throws InterruptedException {
        waitAndClickCancelByText();
    }

    @Override
    protected void waitAndCreateNew() {
        // no-op as the create new link is on the portal page.
    }

    @Override
    protected String waitForDocId() throws InterruptedException {
        checkForDocError();
        String docId = waitForLabeledText("Document Number:");
        jGrowl("Document Number is " + docId);
        return docId;
    }

    @Override
    protected void waitAndClickSave() throws InterruptedException {
        waitAndClickSaveByText();
    }

    @Override
    protected void waitAndClickSubmit() throws InterruptedException {
        waitAndClickSubmitByText();
    }

    @Override
    protected void waitForSuccessfulSaveMessage() throws InterruptedException {
        assertTrue(waitForElementVisibleBy(By.xpath("//div[@id='AgendaEditorView-Agenda-Page_messages']")).getText()
                .contains("Document was successfully saved."));
    }

    @Override
    protected void waitForSuccessfulSubmitMessage() throws InterruptedException {
        waitAndClickConfirmSubmitOk();
        checkForDocErrorKrad();
        assertTrue(waitForElementVisibleBy(By.xpath("//div[@id='AgendaEditorView-Agenda-Page_messages']")).getText().contains("Document was successfully submitted."));
    }

    @Override
    protected void testCreateNewRequired() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        waitAndClickSubmit();
        String mainError = waitForElementVisibleBy(By.xpath("//div[@id='AgendaEditorView-Agenda-Page_messages']")).getText();
        assertTrue(mainError + " does not contain Agenda: 3 errors", mainError.contains("Agenda: 3 errors"));
        String detailError = waitForElementVisibleBy(By.xpath("//section[@data-parent='AgendaEditorView-Agenda-Page']/div/div")).getText();
        assertTrue(detailError + " does not contain 3 errors", detailError.contains("3 errors"));
        assertTrue(detailError + " does not contain Namespace: Required", detailError.contains("Namespace: Required"));
        assertTrue(detailError + " does not contain Name: Required", detailError.contains("Name: Required"));
        assertTrue(detailError + " does not contain Context: Required", detailError.contains("Context: Required"));

        // verify required fields divs have the uif-hasError class
        isVisible(By.xpath("//select[@name='document.newMaintainableObject.dataObject.namespace']/../../div[@class='uif-inputField uif-hasError']"));
        isVisible(By.xpath("//input[@name='document.newMaintainableObject.dataObject.agenda.name']/../../div[@class='uif-inputField uif-hasError']"));
        isVisible(By.xpath("//input[@name='document.newMaintainableObject.dataObject.contextName']/../../div[@class='uif-inputField uif-hasError']"));
    }

    @Override
    protected String verifyDocInitiated() throws InterruptedException {
        String docId = waitForDocId();
        assertEquals("INITIATED", waitForLabeledText("Document Status:"));
        assertEquals(getUserName(), waitForLabeledText(" Initiator Network Id:"));
        return docId;
    }

    @Override
    protected void navigate() throws Exception {
        useUi = true; // nav test will use UI components (lookups), bookmark types it in
        waitForTitleToEqualKualiPortalIndex();
        selectTopFrame();
        waitAndClickByLinkText("Main Menu","");
        waitForTitleToEqualKualiPortalIndex();
        waitAndClickByLinkText("Create New Agenda", "");
        waitForTitleToEqualKualiPortalIndex("");
        selectFrameIframePortlet();
        checkForIncidentReport("Create New Agenda", "");
    }

    protected void createNewEnterDetails() throws InterruptedException {
        selectFrameIframePortlet();
        waitAndSelectLabeled(NAMESPACE_LABEL, inputVerifyDetails[1][1]);
        inputDetails();

        if (useUi) {
            waitAndClickLabeledQuickFinder(CONTEXT_LABEL);
            waitForProgressLoading();
            gotoIframeByXpath("//iframe");
            waitAndClickSearchByText();
            waitAndClickReturnValue(); // Context1
            selectTopFrame();
            gotoNestedFrame();
        }
    }

    @Override
    protected void createNewLookupDetails() throws InterruptedException {
        createNewEnterDetails();
    }

    private void inputDetails() throws InterruptedException {
        waitAndTypeLabeledInput(NAME_LABEL, inputVerifyDetails[0][1]);
        if (!useUi) {
            waitAndTypeLabeledInput(CONTEXT_LABEL, inputVerifyDetails[2][1]);
        }
    }

    @Test
    public void testCreateNewCancelBookmark() throws Exception {
        // Use the UI to populate the context so that the cancel confirmation popups are the same for both the
        // nav and bookmark tests.
        useUi = true;
        testCreateNewCancel();
        passed();
    }

    @Test
    public void testCreateNewCancelNav() throws Exception {
        testCreateNewCancel();
        passed();
    }

    @Test
    public void testCreateNewRequiredBookmark() throws Exception {
        testCreateNewRequired();
        passed();
    }

    @Test
    public void testCreateNewRequiredNav() throws Exception {
        testCreateNewRequired();
        passed();
    }

    @Test
    public void testCreateNewSaveBookmark() throws Exception {
        verifyInputs(testCreateNewSave());
        passed();
    }

    @Test
    public void testCreateNewSaveNav() throws Exception {
        verifyInputs(testCreateNewSave());
        passed();
    }

    @Test
    public void testCreateNewSaveSubmitBookmark() throws Exception {
        verify(testCreateNewSaveSubmit());
        passed();
    }

    @Test
    public void testCreateNewSaveSubmitNav() throws Exception {
        verify(testCreateNewSaveSubmit());
        passed();
    }

    @Test
    public void testCreateNewSubmitBookmark() throws Exception {
        verify(testCreateNewSubmit());
        passed();
    }

    @Test
    public void testCreateNewSubmitNav() throws Exception {
        verify(testCreateNewSubmit());
        passed();
    }

    @Test
    public void testBlanketAppBookmark() throws Exception {
        verify(testBlanketApprove());
        passed();
    }

    @Test
    public void testBlanketAppNav() throws Exception {
        verify(testBlanketApprove());
        passed();
    }
}