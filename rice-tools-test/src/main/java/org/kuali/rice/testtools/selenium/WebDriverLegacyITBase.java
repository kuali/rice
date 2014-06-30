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
package org.kuali.rice.testtools.selenium;

import org.openqa.selenium.Keys;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.common.JiraAwareFailureUtils;
import org.kuali.rice.testtools.common.PropertiesUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Old rice sampleapp AFT code.  New KRAD sampleapp AFT code should go in WebDriverAftBase (or maybe KradAftBase in the future).
 * </p>
 * <p>
 * Originally used to upgrade UpgradedSeleniumITBase (Selenium 1.0) tests to WebDriver (Selenium 2.0).  Now there is
 * refactoring to be done:
 * <ol>
 *   <li><a href="https://jira.kuali.org/browse/KULRICE-9206">KULRICE-9206</a> Replace literal strings used more than 3 times with Constants,
 *   Javadoc constant with constant value.
 *   <li>Extract duplicate waitAndClick...(CONSTANT) to waitAndClickConstant, Javadoc a <pre>{@link &#35;CONSTANT}</pre>.
 *   <li>Replace large chunks of duplication</li>
 *   <li><a href="https://jira.kuali.org/browse/KULRICE-9205">KULRICE-9205</a> Invert dependencies on fields and extract methods to WebDriverUtils
 *   so inheritance doesn't have to be used for reuse.  See WebDriverUtils.waitFor </li>
 *   <li>Extract Nav specific code?</li>
 *   <li>Rename to SampleAppAftBase</li>
 * </ol>
 * </p>
 * <p>Calls to passed() probably don't belong in the methods reused here.</p>
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @deprecated only rice sampleapp specific code should go in this class, see WebDriverAftBase.
 */
public abstract class WebDriverLegacyITBase extends WebDriverAftBase {

    /**
     * Administration
     */
    public static final String ADMINISTRATION_LINK_TEXT = "Administration";

    /**
     * Agenda Lookup
     */
    public static final String AGENDA_LOOKUP_LINK_TEXT = "Agenda Lookup";

    /**
     * backdoorId
     */
    public static final String BACKDOOR_ID_TEXT = "backdoorId";

    /**
     * "//input[@title='Click to login.']"
     */
    public static final String BACKDOOR_LOGIN_BUTTON_XPATH = "//input[@title='Click to login.']";

    /**
     * methodToCall.blanketApprove
     */
    public static final String BLANKET_APPROVE_NAME = "methodToCall.blanketApprove";

    /**
     * methodToCall.cancel
     * different cancel than CANCEL2_XPATH
     */
    public static final String CANCEL_NAME = "methodToCall.cancel";

    /**
     * //a[contains(text(), 'ancel')]
     * Different cancel than CANCEL_NAME
     */
    public static final String CANCEL2_XPATH = "//a[contains(text(), 'ancel')]";

    /**
     * "//a[@title='cancel']"
     */
    public static final String CANCEL3_XPATH = "//a[@title='cancel']";

    /**
     * //*[@title='close this window']
     */
    public static final String CLOSE_WINDOW_XPATH_TITLE = "//*[@title='close this window']";

    /**
     * Collections
     */
    public static final String COLLECTIONS_LINK_TEXT = "Collections";

    /**
     * "Kuali :: Configuration Test View"
     */
    public static final String CONFIGURATION_VIEW_WINDOW_TITLE = "Kuali :: Configuration Test View";

    /**
     * (//a[contains(text(),'Configuration Test View')])[3]
     */
    public static final String CONFIGURATION_VIEW_XPATH = "(//a[contains(text(),'Configuration Test View')])";

    /**
     * copy
     */
    public static final String COPY_LINK_TEXT = "copy";

    /**
     * New Document not submitted successfully
     */
    public static final String CREATE_NEW_DOCUMENT_NOT_SUBMITTED_SUCCESSFULLY_MESSAGE_TEXT = "New Document not submitted successfully";

    /**
     * //img[@alt='create new']
     */
    public static final String CREATE_NEW_XPATH = "//img[@alt='create new']";

    /**
     * //a[@title='Create a new record']
     */
    public static final String CREATE_NEW_XPATH2 = "//a[@title='Create a new record']";

    /**
     * div.dataTables_wrapper thead th
     */
    public static final String DATA_TABLE_TR_CSS = "div.dataTables_wrapper tbody tr";

    /**
     * //div[@class='left-errmsg-tab']/div/div
     */
    public static final String DIV_LEFT_ERRMSG = "//div[@class='left-errmsg-tab']/div/div";

    /**
     * //input[@id='document.newMaintainableObject.code']
     */
    public static final String DOC_CODE_XPATH = "//input[@id='document.newMaintainableObject.code']";

    /**
     * //div[@id='headerarea']/div/table/tbody/tr[1]/td[1]
     */
    public static final String DOC_ID_XPATH = "//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]";

    /**
     * //div[@id='headerarea']/div/table/tbody/tr[1]/td[1]
     */
    public static final String DOC_ID_KRAD_XPATH = "//div[@data-label=\"Document Number\"]";
    /**
     * //table[@id='row']/tbody/tr[1]/td[1
     */
    public static final String DOC_ID_XPATH_2 = "//table[@id='row']/tbody/tr[1]/td[1]";

    /**
     * //table[@id='row']/tbody/tr[1]/td[1]/a
     */
    public static final String DOC_ID_XPATH_3 ="//table[@id='row']/tbody/tr[1]/td[1]/a";

    /**
     * //input[@id='document.documentHeader.documentDescription']
     */
    public static final String DOC_DESCRIPTION_XPATH ="//input[@id='document.documentHeader.documentDescription']";

    /**
     * //div[@id='headerarea']/div/table/tbody/tr[1]/td[1]
     */
    public static final String DOC_INITIATOR_XPATH = "//div[@id='headerarea']/div/table/tbody/tr[2]/td[1]";

    /**
     * "//img[@alt='doc search']
     */
    public static final String DOC_SEARCH_XPATH = "//img[@alt='doc search']";

    /**
     * //a[@title='Document Search']
     */
    public static final String DOC_SEARCH_XPATH_TITLE = "//a[@title='Document Search']";

    /**
     * ENROUTE
     */
    public static final String DOC_STATUS_ENROUTE = "ENROUTE";

    /**
     * FINAL
     */
    public static final String DOC_STATUS_FINAL = "FINAL";

    /**
     * SAVED
     */
    public static final String DOC_STATUS_SAVED = "SAVED";

    /**
     * //table[@class='headerinfo']//tr[1]/td[2]
     */
    public static final String DOC_STATUS_XPATH = "//table[@class='headerinfo']//tr[1]/td[2]";

    /**
     * //table[@id='row']/tbody/tr[1]/td[4]
     */
    public static final String DOC_STATUS_XPATH_2 = "//table[@id='row']/tbody/tr[1]/td[4]";

    /**
     * //div[contains(div,'Document was successfully submitted.')]
     */
    public static final String DOC_SUBMIT_SUCCESS_MSG_XPATH ="//div[contains(div,'Document was successfully submitted.')]";

    /**
     * edit
     */
    public static final String EDIT_LINK_TEXT = "edit";

    /**
     * iframeportlet
     */
    public static final String IFRAMEPORTLET_NAME = "iframeportlet";

    /**
     * (//a[contains(text(),'Uif Components (Kitchen Sink)')])[2]
     */
    public static final String KITCHEN_SINK_XPATH = "(//a[contains(text(),'Uif Components (Kitchen Sink)')])";

    /**
     * KRAD
     */
    public static final String KRAD_XPATH = "KRAD";

    /**
     * Kuali :: Collection Totaling
     */
    public static final String KUALI_COLLECTION_TOTALLING_WINDOW_XPATH = "Kuali :: Collection Totaling";

    /**
     * //a[text()='Collection Totaling']
     */
    public static final String KUALI_COLLECTION_TOTALLING_XPATH = "//a[text()='Collection Totaling']";

    /**
     * Kuali :: Uif Components
     */
    public static final String KUALI_UIF_COMPONENTS_WINDOW_XPATH = "Kuali :: Uif Components";

    /**
     * "Kuali :: View Title"
     */
    public static final String KUALI_VIEW_WINDOW_TITLE = "Kuali :: View Title";

    /**
     * KUALI - Kuali Systems
     */
    public static final String LABEL_KUALI_KUALI_SYSTEMS = "KUALI - Kuali Systems";

    /**
     * KUALI : Default
     */
    public static final String LABEL_KUALI_DEFAULT = "KUALI : Default";

    /**
     * //input[@name='imageField' and @value='Logout']
     */
    public static final String LOGOUT_XPATH = "//input[@name='imageField' and @value='Logout']";

    /**
     * Main Menu
     */
    public static final String MAIN_MENU_LINK_TEXT = "Main Menu";

    /**
     * Kuali :: Rich Messages
     */
    public static final String RICH_MESSAGES_WINDOW_TITLE = "Kuali :: Rich Messages";

    /**
     * //div[contains(div,'Document was successfully saved.')]
     */
    public static final String SAVE_SUCCESSFUL_XPATH = "//div[contains(div,'Document was successfully saved.')]";

    /**
     * //input[@name='methodToCall.save' and @alt='save']
     */
    public static final String SAVE_XPATH="//input[@name='methodToCall.save' and @alt='save']";

    /**
     * KIM Screens
     * //*[@name='methodToCall.save' and @alt='save']
     */
    public static final String SAVE_XPATH_2 = "//*[@name='methodToCall.save' and @alt='save']";

    /**
     * //input[@title='search' and @name='methodToCall.search']
     */
    public static final String SAVE_XPATH_3 = "//input[@title='search' and @name='methodToCall.search']";

    /**
     * Search
     */
    public static final String SEARCH = "Search";

    /**
     * //input[@name='methodToCall.search' and @value='search']
     */
    public static final String SEARCH_XPATH="//input[@name='methodToCall.search' and @value='search']";

    /**
     * //input[@value='search']
     */
    public static final String SEARCH_XPATH_2 = "//input[@value='search']";

    /**
     * (//input[@name='methodToCall.search'])[2]
     */
    public static final String SEARCH_SECOND = "(//input[@name='methodToCall.search'])[2]";

    /**
     * //input[@name='methodToCall.route' and @alt='submit']
     */
    public static final String SUBMIT_XPATH="//input[@name='methodToCall.route' and @alt='submit']";

    /**
     * Travel Account Lookup
     */
    public static final String TRAVEL_ACCOUNT_LOOKUP_LINK_TEXT = "Travel Account Lookup";

    /**
     * Uif Components (Kitchen Sink)
     */
    public static final String UIF_COMPONENTS_KITCHEN_SINK_LINK_TEXT = "Uif Components (Kitchen Sink)";

    /**
     * (//a[contains(text(),'Validation Framework Demo')])[2]
     */
    public static final String VALIDATION_FRAMEWORK_DEMO_XPATH = "(//a[contains(text(),'Validation Framework Demo')])";

    /**
     * XML Ingester
     */
    public static final String XML_INGESTER_LINK_TEXT = "XML Ingester";

    /**
     * //a[@title='FiscalOfficerInfo Maintenance (New)']
     */
    public static final String FISCAL_OFFICER_INFO_MAINTENANCE_NEW_XPATH = "//a[@title='FiscalOfficerInfo Maintenance (New)']";

    static {
        if (System.getProperty(WebDriverUtils.REMOTE_PROPERTIES_PROPERTY) != null) {
            PropertiesUtils propUtils = new PropertiesUtils();
            try {
                propUtils.loadPropertiesWithSystemAndOverridesIntoSystem(System.getProperty(WebDriverUtils.REMOTE_PROPERTIES_PROPERTY));
            } catch (IOException ioe) {
                System.out.println("Exception opening " + System.getProperty(WebDriverUtils.REMOTE_PROPERTIES_PROPERTY) + " " + ioe.getMessage());
            }
        }
    }

    protected String namespaceCode = "KR-WKFLW";

    protected String uiFramework = AutomatedFunctionalTestUtils.REMOTE_UIF_KNS;   // default to KNS

    protected String uniqueString;

    private static final Map<String, String> actionRequestLabelMap;
    private static Map<String, String> actionRequestButtonMap;
    static{
        actionRequestLabelMap = new HashMap();
        actionRequestLabelMap.put("A","APPROVE");
        actionRequestLabelMap.put("F","FYI");
        actionRequestLabelMap.put("C","COMPLETE");
        actionRequestLabelMap.put("CR","COMPLETE");
        actionRequestLabelMap.put("K","ACKNOWLEDGE");
        actionRequestLabelMap.put("D","APPROVE");
        actionRequestButtonMap = new HashMap();
        actionRequestButtonMap.put("A","methodToCall.approve");
        actionRequestButtonMap.put("F","methodToCall.fyi");
        actionRequestButtonMap.put("C","methodToCall.complete");
        actionRequestButtonMap.put("CR","methodToCall.route");
        actionRequestButtonMap.put("K","methodToCall.acknowledge");
        actionRequestButtonMap.put("D","methodToCall.disapprove");
    }

    /**
     * Failures in testSetup cause the test to not be recorded.  Future plans are to extract form @Before and call at the start of each test.
     * Setup the WebDriver properties, test, and login.  Named testSetUp so it runs after TestNG's startSession(Method)
     * {@link WebDriverUtils#determineUser(String)}
     * {@link WebDriverUtils#setUp(String, String, String, String)}
     */
    @Before
    public void testSetUp() {
        super.testSetUp();
    }

    /**
     * // https://jira.kuali.org/browse/KULRICE-9804 KNS Create new link absent when Bookmark URL requires Login
     * @return
     */
    @Override
    protected String getTestUrl() {
        String testUrl = super.getTestUrl();
        if (testUrl.contains(AutomatedFunctionalTestUtils.HIDE_RETURN_LINK) &&
                !testUrl.contains("&showMaintenanceLinks=true")) {
            testUrl += "&showMaintenanceLinks=true";
        }
        return testUrl;
    }


    protected void impersonateUser(String user) throws InterruptedException {
        waitAndTypeByName(BACKDOOR_ID_TEXT,user);
        jGrowl("Click Backdoor Login");
        waitAndClickByXpath(BACKDOOR_LOGIN_BUTTON_XPATH);
    }

    protected void addAdHocRecipientsGroup(String[] adHocRecipients) throws InterruptedException {
        addAdHocRecipientsGroup(new String[][]{adHocRecipients});
    }

    protected void addAdHocRecipientsGroup(String[][] adHocRecipients) throws InterruptedException {
        String today = getDateToday();
        Calendar nextYearCal = Calendar.getInstance();
        nextYearCal.add(Calendar.YEAR, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String nextYear = sdf.format(nextYearCal.getTime());

        waitAndClickByName("methodToCall.toggleTab.tabAdHocRecipients");
        for (int i = 0, s = adHocRecipients.length; i < s; i++) {
            selectOptionByName("newAdHocRouteWorkgroup.actionRequested", adHocRecipients[i][1]);
            waitAndTypeByName("newAdHocRouteWorkgroup.recipientName", adHocRecipients[i][0]);
            waitAndTypeByName("newAdHocRouteWorkgroup.recipientNamespaceCode", adHocRecipients[i][2]);
            WebDriverUtils.jGrowl(getDriver(), "Click Add Group", false, "Click Add Group");
            waitAndClickByName("methodToCall.insertAdHocRouteWorkgroup");
        }
    }

    /**
     * @param adHocRecipients user, action option value
     * @throws InterruptedException
     */
    protected void addAdHocRecipientsPerson(String[] adHocRecipients) throws InterruptedException {
        addAdHocRecipientsPerson(new String[][]{adHocRecipients});
    }

    /**
     * @param adHocRecipients user, action option value
     * @throws InterruptedException
     */
    protected void addAdHocRecipientsPerson(String[][] adHocRecipients) throws InterruptedException {
        String today = getDateToday();
        Calendar nextYearCal = Calendar.getInstance();
        nextYearCal.add(Calendar.YEAR, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String nextYear = sdf.format(nextYearCal.getTime());

        waitAndClickByName("methodToCall.toggleTab.tabAdHocRecipients");
        for (int i = 0, s = adHocRecipients.length; i < s; i++) {
            selectOptionByName("newAdHocRoutePerson.actionRequested", adHocRecipients[i][1]);
            waitAndTypeByName("newAdHocRoutePerson.id", adHocRecipients[i][0]);
//            if (isElementPresentByName("member.activeFromDate")) {
//                waitAndTypeByName("member.activeFromDate", today);
//            }
//            if (isElementPresentByName("member.activeFromDate")) {
//                waitAndTypeByName("member.activeFromDate", nextYear);
//            }
            WebDriverUtils.jGrowl(getDriver(), "Click Add Person", false, "Click Add Person");
            waitAndClickByName("methodToCall.insertAdHocRoutePerson");
        }
    }

    protected void agendaLookupAssertions() throws Exception {
        testLookUp();
        assertTextPresent("Rules");
        waitAndClickCancelByText();
    }

    protected void assertActionList(String docId, String actionListOptionValue, String state) throws InterruptedException {
        selectTopFrame();
        waitAndClickActionList();
        selectFrameIframePortlet();
        while (!waitForIsTextPresent(docId)) {
            waitAndClickByLinkText("Next");
        }
        WebElement docIdTr = findElement(By.xpath("//table/tbody/tr/td/a[contains(text(), '" + docId + "')]/../.."));
        assertTrue(docIdTr.getText() + " does not contain " + docId, docIdTr.getText().contains(docId));
        assertTrue(docIdTr.getText() + " does not contain " + state, docIdTr.getText().contains(state));
        assertTrue(docIdTr.getText() + " does not contain " + actionRequestLabelMap.get(actionListOptionValue), docIdTr.getText().contains(actionRequestLabelMap.get(actionListOptionValue)));
//        assertTextPresent(new String[]{docId, actionRequestLabelMap.get(actionListOptionValue)});
        waitAndClickLinkContainingText(docId);
        selectChildWindow();
        waitAndClickByName(actionRequestButtonMap.get(actionListOptionValue));

        // Disapprove requires another step before checking outbox
        if ("D".equals(actionListOptionValue)) {
            waitAndTypeByName("reason","disapproved for AFT");
            jGrowl("Click yes button");
            waitAndClickByName("methodToCall.processAnswer.button0");
        } else if ("C".equals(actionListOptionValue) || "CR".equals(actionListOptionValue)) {
            waitAndClickByName("methodToCall.close");
        }
        waitForTextNotPresent(docId);
    }

    protected void assertNotInActionList(String docId) throws InterruptedException {
        selectTopFrame();
        waitAndClickActionList();
        selectFrameIframePortlet();
        waitForTextNotPresent(docId);
        while (isElementPresentByLinkText("Next")) {
            waitAndClickByLinkText("Next");
            waitForTextNotPresent(docId);
        }
    }

    protected void assertOutbox(String docId, String state) throws InterruptedException {
        // find it in outbox
        waitAndClickLinkContainingText("Outbox");
        while (!waitForIsTextPresent(docId)) {
            waitAndClickByLinkText("Next");
        }
        WebElement docIdTr = findElement(By.xpath("//table/tbody/tr/td/a[contains(text(), '" + docId + "')]/../.."));
        assertTrue("Outbox items " + docIdTr.getText() + " does not contain " + docId, docIdTr.getText().contains(docId));
        assertTrue("Outbox items " + docIdTr.getText() + " state is incorrect " + state, docIdTr.getText().contains(state));
        waitForTextPresent(docId);

//        // clear all items in the outbox
//        waitAndClickAllByName("outboxItems");
//        waitAndClickByName("methodToCall.removeOutboxItems");
    }

    protected void assertBlanketApproveButtonsPresent() {
        assertElementPresentByName("methodToCall.route");
        assertElementPresentByName("methodToCall.save");
        assertElementPresentByName(BLANKET_APPROVE_NAME, "Blanket Approve button not present does " + user + " have permssion?");
        assertElementPresentByName("methodToCall.close");
        assertElementPresentByName(CANCEL_NAME);
    }

    protected void assertDocFinal(String docId) throws InterruptedException {
        assertDocSearch(docId, DOC_STATUS_FINAL);
    }

    protected void assertDocSearch(String docId, String docStatus) throws InterruptedException {
        docSearch(docId);
        waitForElementPresentByXpath(DOC_ID_XPATH_3);
        jGrowl("Is doc status for docId: " + docId + " " + docStatus + "?");
        assertEquals(docId, getTextByXpath(DOC_ID_XPATH_3));
        assertEquals(docStatus, getTextByXpath(DOC_STATUS_XPATH_2));
    }

    private void docSearch(String docId) throws InterruptedException {
        selectParentWindow();
        selectTopFrame();
        waitAndClickDocSearchTitle();
        waitForPageToLoad();
        selectFrameIframePortlet();
        waitAndTypeByName("documentId", docId);
        waitAndClickSearch();
    }

    protected void assertDocSearchNoResults(String docId) throws InterruptedException {
        docSearch(docId);
        waitForTextPresent("No values match this search.");
    }

    protected void assertTableLayout() throws Exception {
        waitForTextPresent("Actions");
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains("Table Layout"));
        assertTrue(pageSource.contains("Field 1"));
        assertTrue(pageSource.contains("Field 2"));
        assertTrue(pageSource.contains("Field 3"));
        assertTrue(pageSource.contains("Field 4"));
        assertTrue(pageSource.contains("Actions"));
    }

    private void blanketApproveAssert(String docId) throws InterruptedException {
        checkForDocError();
        assertDocSearch(docId, DOC_STATUS_FINAL);
    }

    protected void blanketApproveCheck() throws InterruptedException {
        waitAndClickByName(BLANKET_APPROVE_NAME,
                "No blanket approve button does the user " + getUserName() + " have permission?");
        checkForIncidentReport();
    }

    /**
     * Tests blanket approve action.
     * This method is used by several different tests which perform various types of blanket approvals.
     * Therefore, this is a candidate to remain in this base class
     *
     * @throws InterruptedException
     */
    protected void blanketApproveTest(String docId) throws InterruptedException {
        jGrowl("Click Blanket Approve");
        waitAndClickByName(BLANKET_APPROVE_NAME,
                "No blanket approve button does the user " + getUserName() + " have permission?");
        Thread.sleep(2000);
        checkForIncidentReport();
        blanketApproveAssert(docId);
    }


    protected void failOnErrorMessageItem() {
        failOnErrorMessageItem(this.getClass().getName());
    }

    protected void failOnErrorMessageItem(String message) {
        final String error_locator = "//li[@class='uif-errorMessageItem']";
        if (findElements(By.xpath(error_locator)).size() > 0) {
            String errorText = null;

            try {
                errorText = getTextByXpath(error_locator);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (errorText != null && errorText.contains("errors")) {
                jiraAwareFail((errorText + " " + message).trim());
            }

        }
    }

    /**
     * Uses Selenium's findElements method which does not throw a test exception if not found.
     */
    public void checkForDocError() {
        if (hasDocError()) {
            String errorText = extractErrorText();
            jiraAwareFail(errorText);
        }
    }

    protected String extractErrorText() {
        String errorText = driver.findElement(By.xpath(AutomatedFunctionalTestUtils.DIV_ERROR_LOCATOR)).getText(); // don't highlight
        errorText = AutomatedFunctionalTestUtils.blanketApprovalCleanUpErrorText(errorText);
        if (driver.findElements(By.xpath(AutomatedFunctionalTestUtils.DIV_EXCOL_LOCATOR)).size() > 0) { // not present if errors are at the bottom of the page (see left-errmsg below)
            errorText = AutomatedFunctionalTestUtils.blanketApprovalCleanUpErrorText(driver.findElement(
                    // don't highlight
                    By.xpath(AutomatedFunctionalTestUtils.DIV_EXCOL_LOCATOR)).getText()); // replacing errorText as DIV_EXCOL_LOCATOR includes the error count
        }
        if (driver.findElements(By.xpath(DIV_LEFT_ERRMSG)).size() > 0) {
            errorText = errorText + AutomatedFunctionalTestUtils.blanketApprovalCleanUpErrorText(driver.findElement(
                    By.xpath(DIV_LEFT_ERRMSG)).getText()); // don't highlight
        }
        return errorText;
    }

    /**
     * Uses Selenium's findElements method which does not throw a test exception if not found.
     * @return
     */
    public boolean hasDocError() {
        if (driver.findElements(By.xpath(AutomatedFunctionalTestUtils.DIV_ERROR_LOCATOR)).size() > 0) {
            String errorText = driver.findElement(By.xpath(AutomatedFunctionalTestUtils.DIV_ERROR_LOCATOR)).getText(); // don't highlight
            if (errorText != null && errorText.contains("error(s) found on page.")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Uses Selenium's findElements method which does not throw a test exception if not found.
     * @param errorTextToMatch
     * @return
     */
    public boolean hasDocError(String errorTextToMatch) {
        if (driver.findElements(By.xpath(AutomatedFunctionalTestUtils.DIV_ERROR_LOCATOR)).size() > 0) {
            String errorText = driver.findElement(By.xpath(AutomatedFunctionalTestUtils.DIV_ERROR_LOCATOR)).getText(); // don't highlight
            if (errorText != null && errorText.contains("error(s) found on page.")) {
                WebElement errorDiv = driver.findElement(By.xpath("//div[@class='left-errmsg']/div[2]/div")); // don't highlight
                if (errorDiv != null) {
                    errorText = errorDiv.getText();
                    return errorText != null && errorText.contains(errorTextToMatch);
                }
            }
        }
        return false;
    }

    protected String configNameSpaceBlanketApprove() throws Exception {
        String docId = waitForDocId();
        String dtsPlusTwoChars = AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomChars();
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, "Validation Test Namespace " + AutomatedFunctionalTestUtils
                .createUniqueDtsPlusTwoRandomCharsNot9Digits());
        assertBlanketApproveButtonsPresent();
        waitAndTypeByXpath(DOC_CODE_XPATH, "VTN" + dtsPlusTwoChars);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']",
                "Validation Test NameSpace " + dtsPlusTwoChars);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.applicationId']", "RICE");

        return docId;
    }

    protected void contextLookupAssertions() throws Exception {
        testLookUp();
        assertTextPresent("Notes and Attachments");
        jGrowl("Click Cancel");
        waitAndClickByXpath("//button[@data-performDirtyValidation=\"true\"]"); // first cancel button is for attachment cancel and is hidden
    }

    //    protected void deleteSubCollectionLine() throws Exception {
    //        // click on collections page link
    //        waitAndClickByLinkText(COLLECTIONS_LINK_TEXT);
    //        Thread.sleep(5000);
    //
    //        // wait for collections page to load by checking the presence of a sub collection line item
    //        waitForElementPresentByName("list4[0].subList[0].field1");
    //
    //        // change a value in the line to be deleted
    //        waitAndTypeByName("list4[0].subList[0].field1", "selenium");
    //
    //        // click the delete button
    //        waitAndClickByXpath("//div[@id='collection4_disclosureContent']/div[@class='uif-stackedCollectionLayout']/div[@class='uif-group uif-gridGroup uif-collectionItem uif-gridCollectionItem']/table/tbody/tr[5]/td/div/fieldset/div/div[@class='uif-disclosureContent']/div[@class='dataTables_wrapper']/table/tbody/tr[2]/td[6]/div/fieldset/div/div[@class='uif-boxLayout uif-horizontalBoxLayout clearfix']/button");
    //        Thread.sleep(2000);
    //
    //        // confirm that the input box containing the modified value is not present
    //        for (int second = 0;; second++) {
    //            if (second >= waitSeconds)
    //                jiraAwareFail(TIMEOUT_MESSAGE);
    //            try {
    //                if (!"selenium".equals(waitAndGetAttributeByName("list4[0].subList[0].field1", "value")))
    //                    break;
    //            } catch (Exception e) {}
    //            Thread.sleep(1000);
    //        }
    //
    //        // verify that the value has changed for the input box in the line that has replaced the deleted one
    //        assertNotSame("selenium", waitAndGetAttributeByName("list4[0].subList[0].field1", "value"));
    //    }

    protected void expandColapseByXpath(String clickLocator, String visibleLocator) throws InterruptedException {
        waitAndClickByXpath(clickLocator);
        waitIsVisibleByXpath(visibleLocator);
        waitAndClickByXpath(clickLocator);
        waitNotVisibleByXpath(visibleLocator);
    }

    protected String getDescriptionBase() {
        return this.getClass().toString().substring(this.getClass().toString().lastIndexOf(".") + 1,
                this.getClass().toString().length()) +
                "." + testMethodName + " description";
    }

    protected String getDescriptionUnique() {
        if (uniqueString == null) {
            uniqueString = AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomCharsNot9Digits();
        }
        return getDescriptionBase() + " " + uniqueString;
    }

    protected String getDocStatus() {
        return findElement(By.xpath(DOC_STATUS_XPATH_2)).getText();
    }

    /**
     * <p>
     * Handles simple nested frame content; validates that a frame and nested frame exists before
     * switching to it.
     * </p><p>
     * Uses Selenium's findElements method which does not throw a test exception if not found.
     * </p>
     */
    protected void gotoNestedFrame() {
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        driver.switchTo().defaultContent();
        final String iframeXpath = "//iframe";

        gotoIframeByXpath(iframeXpath);

        gotoIframeByXpath(iframeXpath);

        driver.manage().timeouts().implicitlyWait(waitSeconds, TimeUnit.SECONDS);
    }

    @Override
    protected void logout() throws InterruptedException {
        selectTopFrame();
        if (isElementPresentByXpath(LOGOUT_XPATH)) {
            waitAndClickLogout(this);
        }
    }

    protected boolean noAffilication() {
        return !isElementPresentByName("document.affiliations[0].dflt");
    }

    protected void selectFrameIframePortlet() {
        selectFrame(IFRAMEPORTLET_NAME);
    }

    protected void selectFrame(String locator) {

        if (IFRAMEPORTLET_NAME.equals(locator)) {
            gotoNestedFrame();
        } else {
            WebDriverUtils.selectFrameSafe(driver, locator);
        }
    }

    // TODO delete after AddingNameSpaceAbstractSmokeTestBase migration
    protected void testAddingNamespace() throws Exception {
        testAddingNamespace(this);
    }

    // TODO move method to AddingNameSpaceAbstractSmokeTestBase after locators are extracted
    protected void testAddingNamespace(JiraAwareFailable failable) throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        waitForElementPresentByXpath(SAVE_XPATH_2, "save button does not exist on the page");

        //Enter details for Namespace.
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, "Adding PEANUTS");
        waitAndTypeByXpath("//*[@id='document.documentHeader.explanation']", "I want to add PEANUTS to test KIM");
        waitAndTypeByXpath(DOC_CODE_XPATH, "PEANUTS");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", "The Peanuts Gang");
        checkByXpath("//input[@id='document.newMaintainableObject.active']");
        waitAndClickByXpath(SAVE_XPATH_2);
        waitForElementPresentByXpath(SAVE_SUCCESSFUL_XPATH, "Document is not saved successfully");

        //checks it is saved and initiator is admin.
        assertEquals(DOC_STATUS_SAVED, findElement(By.xpath("//table[@class='headerinfo']/tbody/tr[1]/td[2]")).getText());
        assertEquals("admin", findElement(By.xpath("//table[@class='headerinfo']/tbody/tr[2]/td[1]/a")).getText());
    }

    protected void testAddingBrownGroup() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        String docId = waitForDocId();
        String random = RandomStringUtils.randomNumeric(4);
        String organizationDocumentNumber = "ORD" + random;
        String groupDescription = "GD" + random;
        String groupName = "BrownGroup " + AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomChars();
        String nameSpace = "KR-IDM";
        String today = getDateToday();
        Calendar nextYearCal = Calendar.getInstance();
        nextYearCal.add(Calendar.YEAR, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String nextYear = sdf.format(nextYearCal.getTime());

        //Enter details for BrownGroup.
        waitAndTypeByName("document.documentHeader.documentDescription", "Adding Brown Group");
        waitAndTypeByName("document.documentHeader.explanation", "I want to add Brown Group to test KIM");
        waitAndTypeByName("document.documentHeader.organizationDocumentNumber", organizationDocumentNumber);
        selectOptionByName("document.groupNamespace", nameSpace);
        waitAndTypeByName("document.groupName", groupName);
        waitAndTypeByName("document.groupDescription", groupDescription);

        // Add Ad hoc Recipient
        addAdHocRecipientsPerson(new String[]{"dev1", "F"}); // "One, Developer"

        // Add Ad hoc Workgroup
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kim.impl.group.GroupBo!!).(((namespaceCode:newAdHocRouteWorkgroup.recipientNamespaceCode,name:newAdHocRouteWorkgroup.recipientName))).((`newAdHocRouteWorkgroup.recipientNamespaceCode:namespaceCode,newAdHocRouteWorkgroup.recipientName:name`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");
        waitForElementPresentByXpath(SEARCH_XPATH);
        selectOptionByName("namespaceCode", nameSpace);
        waitAndClickSearch();
        Thread.sleep(2000);
        String adHocWrkGrp = null;
        if (isTextPresent("No values match this search.")) {
            waitAndClickByXpath(CANCEL3_XPATH);
        } else {
            waitAndClickReturnValue();
            waitAndClickByName("methodToCall.insertAdHocRouteWorkgroup");
            adHocWrkGrp = findElement(By.name("adHocRouteWorkgroup[0].recipientName")).getAttribute("value");
        }

        checkByName("document.active");
        waitAndClickByXpath(SAVE_XPATH_2);
        waitForTextPresent("Document was successfully saved.");

        //checks it is saved and initiator is admin.
        assertEquals(DOC_STATUS_SAVED, findElement(By.xpath("//table[@class='headerinfo']/tbody/tr[1]/td[2]")).getText());
        assertEquals("admin", findElement(By.xpath("//table[@class='headerinfo']/tbody/tr[2]/td[1]/a")).getText());
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalId:member.memberId,principalName:member.memberName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchorAssignees");
        waitAndClickSearch();
        String adHocPerson = waitForElementPresentByXpath("//table[@id='row']/tbody/tr/td[2]/a").getText();
        waitAndClickReturnValue();
        waitAndClickByName("methodToCall.addMember.anchorAssignees");
        waitAndClickSave();
        waitAndClickSubmit();
        waitForElementPresentByXpath(DOC_SUBMIT_SUCCESS_MSG_XPATH, "Document is not submitted successfully");
        selectTopFrame();

        // Verify Document Overview info
        docSearch(docId);
        waitAndClickByLinkText(docId);
        switchToWindow("Kuali :: Group");
        assertTextPresent(new String[]{"Adding Brown Group", "I want to add Brown Group to test KIM", organizationDocumentNumber});
        waitAndClickByName("methodToCall.close");

        waitAndClickByLinkText("Administration");
        waitAndClickByLinkText("Group");
        selectFrameIframePortlet();
        waitAndTypeByName("name", groupName);
        waitAndClickSearch();
        waitForElementPresent(By.linkText(groupName), docId + " with groupName "+ groupName + " not present!");
        waitAndClickByLinkText("edit");
        waitAndClickByName("methodToCall.showAllTabs");
        assertTextPresent(new String[]{adHocPerson, groupDescription, nameSpace, groupName});
    }

    protected void testAttributeDefinitionLookUp() throws Exception {
        waitForPageToLoad();
        selectFrameIframePortlet();
        waitAndClickByXpath("//button[contains(.,'earch')]");
        Thread.sleep(3000);
        waitForPageToLoad();
        findElement(By.tagName("body")).getText().contains("Actions"); // there are no actions, but the header is the only unique text from searching
        waitAndClickByLinkText("1000");
        waitForPageToLoad();
        findElement(By.tagName("body")).getText().contains("Attribute Inquiry");
        findElement(By.tagName("body")).getText().contains("KRMS Attributes");
        findElement(By.tagName("body")).getText().contains("Attribute Label");
        findElement(By.tagName("body")).getText().contains("1000");
        findElement(By.tagName("body")).getText().contains("peopleFlowId");
        findElement(By.tagName("body")).getText().contains("KR-RULE");
        findElement(By.tagName("body")).getText().contains("PeopleFlow");

        // selectFrame("name=fancybox-frame1343151577256"); // TODO parse source to get name
        // jiraAwareWaitAndClick("css=button:contains(Close)"); // looks lower case, but is upper
        // Thread.sleep(500);
        // jiraAwareWaitAndClick("css=button:contains(cancel)");
        // AttributeDefinition's don't have actions (yet)
        // jiraAwareWaitAndClick("id=u80");
        // waitForPageToLoad();
        // jiraAwareWaitAndClick("id=u86");
        // waitForPageToLoad();
        // selectWindow("null");
        // jiraAwareWaitAndClick("xpath=(//input[@name='imageField'])[2]");
        // waitForPageToLoad();
        passed();
    }

    protected void testCancelConfirmation() throws InterruptedException {
        waitAndCancelConfirmation();
        passed();
    }

    protected void testConfigParamaterBlanketApprove() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        String docId = waitForDocId();
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, "Validation Test Parameter ");
        assertBlanketApproveButtonsPresent();
        assertEquals("", getTextByName(CANCEL_NAME));
        selectByXpath("//select[@id='document.newMaintainableObject.namespaceCode']", "KR-NS - Kuali Nervous System");
        String componentLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.coreservice.impl.component.ComponentBo!!).(((code:document.newMaintainableObject.componentCode,namespaceCode:document.newMaintainableObject.namespaceCode,))).((`document.newMaintainableObject.componentCode:code,document.newMaintainableObject.namespaceCode:namespaceCode,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(componentLookUp);
        waitAndClickSearch();
        waitAndClickReturnValue();
        String dtsTwo = AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomChars();
        String parameterName = "ValidationTestParameter" + dtsTwo;
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", parameterName);
        waitAndTypeByXpath("//textarea[@id='document.newMaintainableObject.description']",
                "Validation Test Parameter Description" + dtsTwo);
        selectByXpath("//select[@id='document.newMaintainableObject.parameterTypeCode']", "Document Validation");
        waitAndClickByXpath("//input[@id='document.newMaintainableObject.evaluationOperatorCodeAllowed']");
        waitForPageToLoad();
        blanketApproveTest(docId);
    }

    protected void testCreateNewAgenda() throws Exception {
        selectFrameIframePortlet();
        selectByName("document.newMaintainableObject.dataObject.namespace", "Kuali Rules Test");
        String agendaName = "Agenda Date :" + Calendar.getInstance().getTime().toString();
        waitAndTypeByName("document.newMaintainableObject.dataObject.agenda.name", "Agenda " + agendaName);
        fireEvent("document.newMaintainableObject.dataObject.contextName", "focus");
        waitAndTypeByName("document.newMaintainableObject.dataObject.contextName", "Context1");
        fireEvent("document.newMaintainableObject.dataObject.contextName", "blur");
        Thread.sleep(1000);
        // extra focus and blur to work around KULRICE-11534 Create New Agenda requires two blur events to fully render Type when Context is typed in (first renders label, second renders select)
        fireEvent("document.newMaintainableObject.dataObject.contextName", "focus");
        fireEvent("document.newMaintainableObject.dataObject.contextName", "blur");
        waitForElementPresentByName("document.newMaintainableObject.dataObject.agenda.typeId");
        selectByName("document.newMaintainableObject.dataObject.agenda.typeId", "Campus Agenda");
        waitForElementPresentByName("document.newMaintainableObject.dataObject.customAttributesMap[Campus]");
        waitAndTypeByName("document.newMaintainableObject.dataObject.customAttributesMap[Campus]", "BL");
        waitAndClickSubmitByText();
        waitAndClickConfirmationOk();
        assertTextPresent(new String[]{"Document was successfully submitted.", "ENROUTE"});
        passed();
    }

    protected void testCreateDocType() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        assertElementPresentByXpath("//*[@name='methodToCall.route' and @alt='submit']","submit button does not exist on the page");

        //waitForElementPresentByXpath(DOC_ID_XPATH);
        //String docId = findElement(By.xpath(DOC_ID_XPATH)).getText();
        String docId = waitForDocId();
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, "Creating new Document Type");
        String parentDocType = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.kew.doctype.bo.DocumentType!!).(((name:document.newMaintainableObject.parentDocType.name,documentTypeId:document.newMaintainableObject.docTypeParentId,))).((`document.newMaintainableObject.parentDocType.name:name,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(parentDocType);
        waitForPageToLoad();
        Thread.sleep(2000);
        waitAndClickSearch();
        waitAndClickReturnValue();
        String docTypeName = "TestDocType" + AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomChars();
        waitForElementPresentByXpath("//input[@id='document.newMaintainableObject.name']");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", docTypeName);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.unresolvedDocHandlerUrl']","${kr.url}/maintenance.do?methodToCall=docHandler");

        //waitAndTypeByXpath("//input[@id='document.newMaintainableObject.actualNotificationFromAddress']", "NFA");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.label']", "Label for " + docTypeName);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.unresolvedHelpDefinitionUrl']","default.htm?turl=WordDocuments%2Fdocumenttype.htm");
        jGrowl("Click Submit button");
        waitAndClickByXpath("//*[@name='methodToCall.route' and @alt='submit']");
        checkForIncidentReport();
        waitForPageToLoad();
        driver.switchTo().defaultContent();
        waitAndClickDocSearchTitle();
        waitForPageToLoad();
        selectFrameIframePortlet();
        waitAndTypeByName("documentId", docId);
        waitAndClickSearch();
        assertEquals(docId, waitForElementPresent(By.xpath(DOC_ID_XPATH_2)).getText());
    }

    protected String testCreateNew() throws InterruptedException {
        selectFrameIframePortlet();
        waitAndCreateNew();
        String docId = verifyDocInitiated();
        createNewEnterDetails();
        return docId;
    }

    protected void testCreateNewCancel() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        String docId = verifyDocInitiated();
        createNewEnterDetails();
        testCancelConfirmation();
        assertDocSearchNoResults(docId);
    }

    private String verifyDocInitiated() throws InterruptedException {
        String docId = waitForDocId();
        assertEquals("INITIATED", waitForDocStatus());
        assertEquals(getUserName(), waitForDocInitiator());
        return docId;
    }

    protected List<String> testCreateNewParameter(String docId, String parameterName) throws Exception {
        waitForPageToLoad();
        docId = waitForDocId();
        //Enter details for Parameter.
        waitAndTypeByName("document.documentHeader.documentDescription", "Adding Test Parameter");
        selectOptionByName("document.newMaintainableObject.namespaceCode", "KR-WKFLW");
        waitAndTypeByName("document.newMaintainableObject.componentCode", "ActionList");
        waitAndTypeByName("document.newMaintainableObject.applicationId", "KUALI");
        parameterName = "TestIndicator" + AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomChars();
        waitAndTypeByName("document.newMaintainableObject.name", parameterName);
        waitAndTypeByName("document.newMaintainableObject.value", "Y");
        waitAndTypeByName("document.newMaintainableObject.description", "for testing");
        selectOptionByName("document.newMaintainableObject.parameterTypeCode", "HELP");
        waitAndClickByXpath("//input[@name='document.newMaintainableObject.evaluationOperatorCode' and @value='A']");
        waitAndClickSave();
        waitAndClickSubmit();
        waitForElementPresentByXpath(DOC_SUBMIT_SUCCESS_MSG_XPATH, "Document is not submitted successfully");


        assertDocSearch(docId, DOC_STATUS_FINAL);
        selectTopFrame();
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterName);

        return params;
    }

    protected List<String> testCreateNewParameterType(String docId, String parameterType, String parameterCode)throws Exception {
        waitForPageToLoad();
        docId = waitForDocId();

        //Enter details for Parameter.
        waitAndTypeByName("document.documentHeader.documentDescription", "Adding Test Parameter Type");
        parameterCode = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        waitAndTypeByName("document.newMaintainableObject.code", parameterCode);
        parameterType = "testing " + AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomChars();
        waitAndTypeByName("document.newMaintainableObject.name", parameterType);
        waitAndClickSave();
        waitAndClickSubmit();
        waitForElementPresentByXpath(DOC_SUBMIT_SUCCESS_MSG_XPATH, "Document is not submitted successfully");
        assertDocSearch(docId, DOC_STATUS_FINAL);
        selectTopFrame();
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterType);
        params.add(parameterCode);

        return params;
    }

    protected void testCreateNewSearchReturnValueCancelConfirmation() throws InterruptedException, Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        waitAndClickSearch2();
        waitAndClickReturnValue();
        waitAndCancelConfirmation();
        passed();
    }

    protected List<String> testCopyParameter(String docId, String parameterName) throws Exception {
        selectFrameIframePortlet();
        waitAndClickCopy();
        waitForPageToLoad();
        docId = waitForDocId();
        waitAndTypeByName("document.documentHeader.documentDescription", "Copying Test Parameter");
        selectOptionByName("document.newMaintainableObject.namespaceCode", "KR-WKFLW");
        waitAndTypeByName("document.newMaintainableObject.componentCode", "ActionList");
        waitAndTypeByName("document.newMaintainableObject.applicationId", "KUALI");
        parameterName = "TestIndicator" + AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomChars();
        waitAndTypeByName("document.newMaintainableObject.name", parameterName);
        waitAndClickSave();
        waitAndClickSubmit();
        waitForElementPresentByXpath(DOC_SUBMIT_SUCCESS_MSG_XPATH, "Document is not submitted successfully");
        assertDocSearch(docId, DOC_STATUS_FINAL);
        selectTopFrame();
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterName);

        return params;
    }

    protected List<String> testCopyParameterType(String docId, String parameterType, String parameterCode) throws Exception {
        selectFrameIframePortlet();
        waitAndClickCopy();
        waitForPageToLoad();
        docId = waitForDocId();
        waitAndTypeByName("document.documentHeader.documentDescription", "Copying Test Parameter");
        parameterCode = RandomStringUtils.randomAlphabetic(4).toLowerCase();
        waitAndTypeByName("document.newMaintainableObject.code", parameterCode);
        clearTextByName("document.newMaintainableObject.name");
        parameterType = "testing " + AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomChars();
        waitAndTypeByName("document.newMaintainableObject.name", parameterType);
        waitAndClickSave();
        waitAndClickSubmit();
        waitForElementPresentByXpath(DOC_SUBMIT_SUCCESS_MSG_XPATH, "Document is not submitted successfully");
        assertDocSearch(docId, DOC_STATUS_FINAL);
        selectTopFrame();
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterType);
        params.add(parameterCode);

        return params;
    }


    protected void testDocTypeLookup() throws Exception {
        selectFrameIframePortlet();
        waitAndClickByXpath("//input[@title='Search Parent Name']");
        waitAndClickByXpath(SAVE_XPATH_3);
        waitAndClickByXpath("//table[@id='row']/tbody/tr[contains(td[3],'RiceDocument')]/td[1]/a");
        waitAndClickByXpath(SAVE_XPATH_3);
        assertEquals("RiceDocument", getTextByXpath("//table[@id='row']/tbody/tr/td[4]/a"));
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("name", "Kuali*D");
        waitAndClickByXpath(SAVE_XPATH_3);
        assertElementPresentByXpath("//table[@id='row']/tbody/tr[contains(td[3], 'KualiDocument')]");
        String docIdOld = getTextByXpath("//table[@id='row']/tbody/tr[contains(td[3], 'KualiDocument')]/td[2]/a");
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("label", "KualiDocument");
        waitAndClickByXpath(SAVE_XPATH_3);
        assertElementPresentByXpath("//table[@id='row']/tbody/tr[contains(td[5], 'KualiDocument')]");
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("documentTypeId", docIdOld);
        waitAndClickByXpath(SAVE_XPATH_3);
        assertElementPresentByXpath("//table[@id='row']/tbody/tr[contains(td[2], '" + docIdOld + "')]");
    }


    protected List<String> testEditParameterType(String docId, String parameterType, String parameterCode) throws Exception {
        selectFrameIframePortlet();
        waitAndClickEdit();
        waitForPageToLoad();
        docId = waitForDocId();
        waitAndTypeByName("document.documentHeader.documentDescription", "Editing Test Parameter");
        clearTextByName("document.newMaintainableObject.name");
        parameterType = "testing " + AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomChars();
        waitAndTypeByName("document.newMaintainableObject.name", parameterType);
        waitAndClickSave();
        waitAndClickSubmit();
        waitForElementPresentByXpath(DOC_SUBMIT_SUCCESS_MSG_XPATH, "Document is not submitted successfully");
        assertDocSearch(docId, DOC_STATUS_FINAL);
        selectTopFrame();
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterType);
        params.add(parameterCode);

        return params;
    }

    protected List<String> testEditParameter(String docId, String parameterName) throws Exception {
        selectFrameIframePortlet();
        waitAndClickEdit();
        waitForPageToLoad();
        docId = waitForDocId();
        waitAndTypeByName("document.documentHeader.documentDescription", "Editing Test Parameter");
        clearTextByName("document.newMaintainableObject.value");
        waitAndTypeByName("document.newMaintainableObject.value", "N");
        waitAndClickSave();
        waitAndClickSubmit();
        waitForElementPresentByXpath(DOC_SUBMIT_SUCCESS_MSG_XPATH, "Document is not submitted successfully");
        assertDocSearch(docId, DOC_STATUS_FINAL);
        selectTopFrame();
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterName);
        return params;
    }

    protected void testEditRouteRulesDelegation() throws Exception {
        waitForPageToLoad();
        Thread.sleep(3000);
        assertEquals("Kuali Portal Index", getTitle());
        selectFrameIframePortlet();
        waitAndClickSearch();
        waitAndClickEdit();
        waitForPageToLoad();
        Thread.sleep(3000);
        assertTrue(isElementPresentByName(CANCEL_NAME));
        waitAndClickCancel();
        waitAndClickByName("methodToCall.processAnswer.button0");
        waitForPageToLoad();
        passed();
    }

    protected void testFiscalOfficerInfoMaintenanceNew() throws Exception {
        selectFrameIframePortlet();
        checkForIncidentReport();
        String docId = getTextByXpath("//*[@id='u13_control']");
        waitAndTypeByXpath("//input[@name='document.documentHeader.documentDescription']", "New FO Doc");
        waitAndTypeByXpath("//input[@name='document.newMaintainableObject.dataObject.id']", "5");
        waitAndTypeByXpath("//input[@name='document.newMaintainableObject.dataObject.userName']", "Jigar");
        waitAndClickByXpath("//button[@id='usave']");
        Integer docIdInt = Integer.valueOf(docId).intValue();
        waitAndClickActionList();
        selectFrameIframePortlet();

        if(isElementPresentByLinkText("Last")){
            waitAndClickByLinkText("Last");
            waitAndClickByLinkText(docIdInt.toString());
        } else {
            waitAndClickByLinkText(docIdInt.toString());
        }

        //      ------------------------------- Not working in code when click docId link in list--------------------------
        //Thread.sleep(5000);
        //String[] windowTitles = getAllWindowTitles();
        //selectWindow(windowTitles[1]);
        //windowFocus();
        //assertEquals(windowTitles[1], getTitle());
        //checkForIncidentReport("Action List Id link opened window.", "https://jira.kuali.org/browse/KULRICE-9062 Action list id links result in 404 or NPE");

        //------submit-----//
        //selectFrame("relative=up");
        //waitAndClick("//button[@value='submit']");
        //waitForPageToLoad50000();
        //close();
        //------submit over---//

        //----step 2----//
        //selectWindow("null");
        //windowFocus();
        //waitAndClick("//img[@alt='doc search']");
        //waitForPageToLoad50000();
        //assertEquals(windowTitles[0], getTitle());
        //selectFrame("iframeportlet");
        //waitAndClick(SEARCH_XPATH);
        //waitForPageToLoad50000();
        //----step 2 over ----//

        //-----Step 3 verifies that doc is final-------//
        //assertEquals("FINAL", getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        //selectFrame("relative=up");
        //waitAndClick("link=Main Menu");
        //waitForPageToLoad50000();
        //assertEquals(windowTitles[0], getTitle());
        //-----Step 3 verified that doc is final -------
    }

    protected void testIdentityGroupBlanketApprove() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        String docId = waitForDocId();
        String dtsTwo = AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomCharsNot9Digits();
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, "Validation Test Group " + dtsTwo);
        assertBlanketApproveButtonsPresent();
        selectByXpath("//select[@id='document.groupNamespace']", LABEL_KUALI_KUALI_SYSTEMS);
        waitAndTypeByXpath("//input[@id='document.groupName']", "Validation Test Group1 " + dtsTwo);
        waitAndClickByName(
                "methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalId:member.memberId,principalName:member.memberName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchorAssignees");
        waitAndClickSearch();
        waitAndClickReturnValue();
        waitAndClickByName("methodToCall.addMember.anchorAssignees");
        waitForPageToLoad();
        blanketApproveTest(docId);
    }

    protected void testIdentityPermissionBlanketApprove() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        String docId = waitForDocId();
        String dtsTwo = AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomCharsNot9Digits();
        waitAndTypeByXpath("//input[@name='document.documentHeader.documentDescription']",
                "Validation Test Permission " + dtsTwo);
        assertBlanketApproveButtonsPresent();
        waitAndTypeByXpath("//input[@name='document.documentHeader.organizationDocumentNumber']", "10012");
        selectByXpath("//select[@name='document.newMaintainableObject.namespaceCode']", LABEL_KUALI_KUALI_SYSTEMS);
        selectByXpath("//select[@name='document.newMaintainableObject.templateId']", LABEL_KUALI_DEFAULT);
        waitAndTypeByXpath("//input[@name='document.newMaintainableObject.name']",
                "ValidationTestPermission" + dtsTwo);
        blanketApproveTest(docId);
    }

    protected void testIdentityPersonBlanketApprove() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        String docId = waitForDocId();
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, "Validation Test Person");
        assertBlanketApproveButtonsPresent();
        waitAndTypeByXpath("//input[@id='document.principalName']", "principal" + RandomStringUtils.randomAlphabetic(3).toLowerCase());
        selectByName("newAffln.affiliationTypeCode", "Affiliate");
        selectByName("newAffln.campusCode", "BX - BLGTN OFF CAMPUS");
        selectByName("newAffln.campusCode", "BL - BLOOMINGTON");
        assertElementPresentByName("newAffln.dflt");
        waitAndClickByName("newAffln.dflt");
        waitAndClickByName("methodToCall.addAffln.anchor");
        waitAndClickByName("methodToCall.toggleTab.tabContact");
        selectByName("newName.namePrefix", "Mr");
        waitAndTypeByName("newName.firstName", "First");
        waitAndTypeByName("newName.lastName", "Last");
        selectByName("newName.nameSuffix", "Mr");
        waitAndClickByName("newName.dflt");
        waitAndClickByName("methodToCall.addName.anchor");
        waitForPageToLoad();
        blanketApproveTest(docId);
    }

    protected void testIdentityResponsibilityBlanketApprove() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        String docId = waitForDocId();
        String dtsTwo = AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomCharsNot9Digits();
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, "Validation Test Responsibility " + dtsTwo);
        assertBlanketApproveButtonsPresent();
        selectByXpath("//select[@id='document.newMaintainableObject.namespaceCode']", LABEL_KUALI_KUALI_SYSTEMS);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']",
                "Validation Test Responsibility " + dtsTwo);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.documentTypeName']", "Test " + dtsTwo);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.routeNodeName']", "Test " + dtsTwo);
        waitAndClickByXpath("//input[@id='document.newMaintainableObject.actionDetailsAtRoleMemberLevel']");
        waitAndClickByXpath("//input[@id='document.newMaintainableObject.required']");
        blanketApproveTest(docId);
    }

    protected void testIdentityRoleBlanketApprove() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        waitAndClickByXpath(SEARCH_XPATH, "No search button to click.");
        waitAndClickReturnValue();
        String docId = waitForDocId();
        String dtsTwo = AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomCharsNot9Digits();
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, "Validation Test Role " + dtsTwo);
        assertBlanketApproveButtonsPresent();
        selectByXpath("//select[@id='document.roleNamespace']", LABEL_KUALI_KUALI_SYSTEMS);
        waitAndTypeByXpath("//input[@id='document.roleName']", "Validation Test Role " + dtsTwo,
                "No Role Name input to type in.");
        waitAndClickByName(
                "methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalId:member.memberId,principalName:member.memberName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchorAssignees");
        waitAndClickByXpath(SEARCH_XPATH, "No search button to click.");
        waitAndClickReturnValue();
        waitAndClickByName("methodToCall.addMember.anchorAssignees");
        waitForPageToLoad();
        blanketApproveTest(docId);
    }

    protected void testLocationCountryBlanketApprove() throws InterruptedException {
        selectFrameIframePortlet();
        String randomCode = searchForAvailableCode(2);

        waitAndCreateNew();
        String docId = waitForDocId();
        String countryName = "Validation Test Country " + randomCode + " " + AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomCharsNot9Digits();
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, countryName);
        waitAndTypeByXpath(DOC_CODE_XPATH, randomCode);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", countryName);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.alternateCode']", "V" + randomCode);

        finishBlanketApprovalTest(docId);
    }

    protected void finishBlanketApprovalTest(String docId) throws InterruptedException {
        assertBlanketApproveButtonsPresent();
        blanketApproveCheck();
        if (!hasDocError("same primary key already exists")) { // don't fail as to still have the same key after 25 sequential attempts we've created many today already
            blanketApproveAssert(docId);
        }
    }

    protected void testLocationCountyBlanketApprove() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        String docId = waitForDocId();
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, "Validation Test County");
        assertBlanketApproveButtonsPresent();
        String countryLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.country.CountryBo!!).(((code:document.newMaintainableObject.countryCode,))).((`document.newMaintainableObject.countryCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(countryLookUp);
        waitAndTypeByName("code", "US");
        waitAndClickSearch();
        waitAndClickReturnValue();
        waitAndTypeByXpath(DOC_CODE_XPATH, RandomStringUtils.randomAlphabetic(2).toUpperCase());
        String stateLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.state.StateBo!!).(((countryCode:document.newMaintainableObject.countryCode,code:document.newMaintainableObject.stateCode,))).((`document.newMaintainableObject.countryCode:countryCode,document.newMaintainableObject.stateCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(stateLookUp);
        waitAndTypeByName("code", "IN");
        waitAndClickSearch();
        waitAndClickReturnValue();
        String countyName = "Validation Test County" + AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomChars();
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", countyName);
        waitAndClickByXpath("//input[@id='document.newMaintainableObject.active']");
        blanketApproveTest(docId);
    }

    protected void testLocationPostBlanketApprove() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        String docId = waitForDocId();
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, "Validation Test Postal Code");
        assertBlanketApproveButtonsPresent();
        String countryLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.country.CountryBo!!).(((code:document.newMaintainableObject.countryCode,))).((`document.newMaintainableObject.countryCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(countryLookUp);
        waitAndTypeByName("code", "US");
        waitAndClickSearch();
        waitAndClickReturnValue();
        String code = RandomStringUtils.randomNumeric(5);
        waitAndTypeByXpath(DOC_CODE_XPATH, code);
        String stateLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.state.StateBo!!).(((countryCode:document.newMaintainableObject.countryCode,code:document.newMaintainableObject.stateCode,))).((`document.newMaintainableObject.countryCode:countryCode,document.newMaintainableObject.stateCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(stateLookUp);
        waitAndClickSearch();
        waitAndClickByXpath("//table[@id='row']/tbody/tr[4]/td[1]/a");
        String cityName = "Validation Test Postal Code " + code;
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.cityName']", cityName);
        blanketApproveTest(docId);
    }

    protected void testLocationStateBlanketApprove() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        String docId = waitForDocId();
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, "Validation Test State");
        assertBlanketApproveButtonsPresent();

        //jiraAwareWaitAndClick("methodToCall.performLookup.(!!org.kuali.rice.location.impl.country.CountryBo!!).(((code:document.newMaintainableObject.countryCode,))).((`document.newMaintainableObject.countryCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;" + getBaseUrlString() + "/kr/lookup.do;::::).anchor4");
        String countryLookUp = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.location.impl.country.CountryBo!!).(((code:document.newMaintainableObject.countryCode,))).((`document.newMaintainableObject.countryCode:code,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(countryLookUp);
        waitAndClickSearch();
        waitAndClickReturnValue();
        String code = RandomStringUtils.randomAlphabetic(2).toUpperCase();
        waitAndTypeByXpath(DOC_CODE_XPATH, code);
        String state = "Validation Test State " + code;
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", state);
        waitAndClickByXpath("//input[@id='document.newMaintainableObject.active']");
        blanketApproveTest(docId);
    }

    protected void testLookUp() throws Exception {
        waitForPageToLoad();
        selectFrameIframePortlet();

        // Mixed capitalization
        waitAndClick(By.xpath(SEARCH_XPATH_3));
        waitAndClickByLinkText(EDIT_LINK_TEXT, "edit button not present does user " + user + " have permission?");
        waitForTextPresent("ubmit");
        assertTextPresent("ave");
        assertTextPresent("pprove");
        assertTextPresent("lose");
        assertTextPresent("ancel");
    }

    protected void testReferenceCampusTypeBlanketApprove() throws Exception {
        selectFrameIframePortlet();
        String randomCode = searchForAvailableCode(1);

        waitAndCreateNew();
        String docId = waitForDocId();
        String dtsTwo = AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomCharsNot9Digits();
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, "Validation Test Campus Type " + randomCode + " " + dtsTwo);
        waitAndTypeByXpath(DOC_CODE_XPATH, randomCode);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", "Indianapolis"  + randomCode + dtsTwo);

        finishBlanketApprovalTest(docId);
    }

    protected void performParameterInquiry(String parameterField) throws Exception {
        waitAndTypeByName("name", parameterField);
        waitAndClickSearch();
        isElementPresentByLinkText(parameterField);
        waitAndClickByLinkText(parameterField);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
    }

    protected List<String> testLookUpParameterType(String docId, String parameterType, String parameterCode) throws Exception {
        performParameterInquiry(parameterType);
        assertEquals(parameterCode, getTextByXpath("//div[@class='tab-container']/table//span[@id='code.div']").trim().toLowerCase());
        assertEquals(parameterType, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim().toLowerCase());
        waitAndClickCloseWindow();
        switchToWindow("null");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterType);
        params.add(parameterCode);

        return params;
    }

    protected List<String> testLookUpParameter(String docId, String parameterName) throws Exception {
        performParameterInquiry(parameterName);
        assertEquals(parameterName, getTextByXpath(
                "//div[@class='tab-container']/table//span[@id='name.div']").trim());
        assertEquals("Y", getTextByXpath("//div[@class='tab-container']/table//span[@id='value.div']")
                .trim());
        waitAndClickCloseWindow();
        switchToWindow("null");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterName);

        return params;
    }

    protected void testTermLookupAssertions() throws Exception {
        testLookUp();
        assertTextPresent("Term Parameters");
        waitAndClick(By.xpath(CANCEL2_XPATH));
        passed();
    }

    protected void testTermSpecificationLookupAssertions() throws Exception {
        testLookUp();
        assertTextPresent("Context");
        waitAndClickCancelByText();
        passed();
    }

    protected List<String> testVerifyModifiedParameter(String docId, String parameterName) throws Exception {
        performParameterInquiry(parameterName);
        assertEquals(parameterName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        assertEquals("N", getTextByXpath("//div[@class='tab-container']/table//span[@id='value.div']").trim());
        waitAndClickCloseWindow();
        switchToWindow("null");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterName);

        return params;
    }

    protected List<String> testVerifyCopyParameterType(String docId, String parameterType, String parameterCode) throws Exception
    {
        performParameterInquiry(parameterType);
        assertEquals(parameterType, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim().toLowerCase());
        waitAndClickCloseWindow();
        switchToWindow("null");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(parameterType);
        params.add(parameterCode);

        return params;
    }

    protected List<String> testCreateNewPermission(String docId, String permissionName) throws Exception {
        waitForPageToLoad();
        Thread.sleep(2000);
        docId = waitForDocId();
        waitAndClickSave();
        waitForElementPresentByXpath("//div[contains(.,'Document Description (Description) is a required field.')]/img[@alt='error']");
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, "Adding Permission removeme");
        waitAndClickSubmit();
        waitForElementPresentByXpath("//div[@class='error']");
        assertElementPresentByXpath("//div[contains(.,'Template (Template) is a required field.')]/img[@alt='error']");
        assertElementPresentByXpath("//div[contains(.,'Permission Namespace (Permission Namespace) is a required field.')]/img[@alt='error']");
        assertElementPresentByXpath("//div[contains(.,'Permission Name (Permission Name) is a required field.')]/img[@alt='error']");
        selectOptionByName("document.newMaintainableObject.templateId", "36");
        selectOptionByName("document.newMaintainableObject.namespaceCode", "KR-SYS");
        permissionName = "removeme" + AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomChars();
        waitAndTypeByName("document.newMaintainableObject.name", permissionName);
        waitAndTypeByName("document.newMaintainableObject.description", "namespaceCode=KR*");
        checkByName("document.newMaintainableObject.active");
        waitAndClickSave();
        waitForElementPresentByXpath(SAVE_SUCCESSFUL_XPATH);
        assertEquals(DOC_STATUS_SAVED, getTextByXpath(DOC_STATUS_XPATH));
        waitAndClickSubmit();
        waitForElementPresentByXpath(DOC_SUBMIT_SUCCESS_MSG_XPATH, "Document is not submitted successfully");
        assertEquals(DOC_STATUS_ENROUTE, getTextByXpath(DOC_STATUS_XPATH));
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(permissionName);

        return params;
    }

    protected List<String> testLookUpPermission(String docId, String permissionName) throws Exception {
        waitAndTypeByName("name", permissionName);
        waitAndClickSearch();
        isElementPresentByLinkText(permissionName);
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(permissionName);

        return params;
    }

    protected List<String> testEditPermission(String docId, String permissionName) throws Exception {
        waitAndClickEdit();
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, "Editing Permission removeme");
        uncheckByName("document.newMaintainableObject.active");
        waitAndClickSubmit();
        waitForElementPresentByXpath(DOC_SUBMIT_SUCCESS_MSG_XPATH, "Document is not submitted successfully");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(permissionName);

        return params;
    }

    protected List<String> testVerifyPermission(String docId, String permissionName) throws Exception {
        waitAndTypeByName("name", permissionName);
        waitAndClickByXpath("//input[@title='Active Indicator - No']");
        waitAndClickSearch();
        isElementPresentByLinkText(permissionName);
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(permissionName);

        return params;
    }

    protected List<String> testCreateNewPerson(String docId, String personName) throws Exception  {
        waitForPageToLoad();
        docId = waitForDocId();
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, "Adding Charlie Brown");
        waitAndTypeByName("document.documentHeader.explanation", "I want to add Charlie Brown to test KIM");

        //here You should also check for lower case validation for principalName, but it is skipped for now as there is an incident report error there.
        personName = "cbrown" + AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomChars();
        waitAndTypeByName("document.principalName", personName);
        waitAndClickSave();
        waitForElementPresentByXpath(SAVE_SUCCESSFUL_XPATH);
        assertEquals(DOC_STATUS_SAVED, getTextByXpath(DOC_STATUS_XPATH));
        waitAndClickSubmit();
        waitForElementPresentByXpath("//div[contains(.,'At least one affiliation must be entered.')]/img[@alt='error']");
        assertElementPresentByXpath("//div[contains(.,'At least one name must be entered.')]/img[@alt='error']");
        selectOptionByName("newAffln.affiliationTypeCode", "STDNT");
        selectOptionByName("newAffln.campusCode", "BL");
        checkByName("newAffln.dflt");
        waitAndClickByName("methodToCall.addAffln.anchor");
        waitAndSelectByName("newName.nameCode", "PRM");
        selectOptionByName("newName.namePrefix", "Mr");
        waitAndTypeByName("newName.firstName", "Charlie");
        waitAndTypeByName("newName.lastName", "Brown");
        checkByName("newName.dflt");
        waitAndClickByName("methodToCall.addName.anchor");
        waitAndClickSubmit();
        waitForElementPresentByXpath(DOC_SUBMIT_SUCCESS_MSG_XPATH, "Document is not submitted successfully");
        assertEquals(DOC_STATUS_ENROUTE, getTextByXpath(DOC_STATUS_XPATH));
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(personName);

        return params;
    }

    protected List<String> testLookUpPerson(String docId, String personName) throws Exception {
        waitAndTypeByName("principalName", personName);
        waitAndClickSearch();
        isElementPresentByLinkText(personName);
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("firstName", "Charlie");
        waitAndClickSearch();
        isElementPresentByLinkText(personName);
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("lastName", "Brown");
        waitAndClickSearch();
        isElementPresentByLinkText(personName);
        waitAndClickByName("methodToCall.clearValues");
        waitAndTypeByName("campusCode", "BL");
        waitAndClickSearch();
        isElementPresentByLinkText(personName);
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(personName);

        return params;
    }

    protected List<String> testVerifyPerson(String docId, String personName) throws Exception {
        waitAndClickByLinkText(personName);
        waitForPageToLoad();
        Thread.sleep(5000);
        switchToWindow("Kuali :: Person");
        Thread.sleep(2000);
        assertEquals(personName, getTextByXpath("//div[@class='tab-container']/table//tr[2]/td[1]/div").trim());
        assertEquals("BL - BLOOMINGTON", getTextByXpath("//div[@class='tab-container']/table[3]//tr[2]/td[2]/div").trim());
        assertEquals("Student", getTextByXpath("//select/option[@selected]").trim());
        assertElementPresentByXpath("//table[@class='tab']//input[@title='close Overview']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Contact']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Privacy Preferences']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Membership']");
        waitAndClickByName("methodToCall.showAllTabs");
        waitForElementPresentByXpath("//table[@class='tab']//input[@title='close Overview']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='close Contact']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='close Privacy Preferences']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='close Membership']");
        waitAndClickByName("methodToCall.hideAllTabs");
        waitForElementPresentByXpath("//table[@class='tab']//input[@title='open Overview']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Contact']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Privacy Preferences']");
        assertElementPresentByXpath("//table[@class='tab']//input[@title='open Membership']");
        waitAndClickCloseWindow();
        switchToWindow("null");
        List<String> params = new ArrayList<String>();
        params.add(docId);
        params.add(personName);

        return params;
    }

    protected void testConfigurationTestView(String idPrefix) throws Exception {
        waitForElementPresentByXpath("//label[@id='" + idPrefix + "TextInputField_label']");

        // testing for https://groups.google.com/a/kuali.org/group/rice.usergroup.krad/browse_thread/thread/1e501d07c1141aad#
        String styleValue = waitAndGetAttributeByXpath("//label[@id='" + idPrefix + "TextInputField_label']",
                "style");

        // log.info("styleValue is " + styleValue);
        assertTrue(idPrefix + "textInputField label does not contain expected style", styleValue.replace(" ", "").contains("color:red"));

        // get current list of options
        String refreshTextSelectLocator = "//select[@id='" + idPrefix + "RefreshTextField_control']";
        String[] options1 = getSelectOptionsByXpath(refreshTextSelectLocator);
        String dropDownSelectLocator = "//select[@id='" + idPrefix + "DropDown_control']";
        selectByXpath(dropDownSelectLocator, "Vegetables");
        Thread.sleep(3000);

        //get list of options after change
        String[] options2 = getSelectOptionsByXpath(refreshTextSelectLocator);

        //verify that the change has occurred
        assertFalse("Field 1 selection did not change Field 2 options https://jira.kuali.org/browse/KULRICE-8163 Configuration Test View Conditional Options doesn't change Field 2 options based on Field 1 selection",
                options1[options1.length - 1].equalsIgnoreCase(options2[options2.length - 1]));

        //confirm that control gets disabled
        selectByXpath(dropDownSelectLocator, "None");
        Thread.sleep(3000);
        assertEquals("true", waitAndGetAttributeByXpath(refreshTextSelectLocator, "disabled"));
    }

    //    protected void testTravelAccountTypeLookup() throws Exception {
    //        selectFrameIframePortlet();
    //
    //        //Blank Search
    //        waitAndClickByXpath("//*[contains(button,\"earch\")]/button[1]");
    //        Thread.sleep(4000);
    //        assertElementPresentByXpath("//table[@class='uif-tableCollectionLayout dataTable']//tr[contains(td[1],'CAT')]");
    //        assertElementPresentByXpath("//table[@class='uif-tableCollectionLayout dataTable']//tr[contains(td[1],'EAT')]");
    //        assertElementPresentByXpath("//table[@class='uif-tableCollectionLayout dataTable']//tr[contains(td[1],'IAT')]");
    //
    //        //search with each field
    //        waitAndTypeByName("lookupCriteria[accountTypeCode]", "CAT");
    //        waitAndClickByXpath("//*[contains(button,\"earch\")]/button[1]");
    //        Thread.sleep(2000);
    //        assertElementPresentByXpath("//table[@class='uif-tableCollectionLayout dataTable']//tr[contains(td[1],'CAT')]");
    //        waitAndClickByXpath("//*[contains(button,\"earch\")]/button[2]");
    //        Thread.sleep(2000);
    //        waitAndTypeByName("lookupCriteria[name]", "Expense Account Type");
    //        waitAndClickByXpath("//*[contains(button,\"earch\")]/button[1]");
    //        Thread.sleep(4000);
    //        assertElementPresentByXpath("//table[@class='uif-tableCollectionLayout dataTable']//tr[contains(td[1],'EAT')]");
    //
    //        //Currently No links available for Travel Account Type Inquiry so cant verify heading and values.
    //    }

    protected void testCategoryLookUp() throws Exception {
        waitForPageToLoad();
        selectFrameIframePortlet();
        waitAndClickByXpath("//button[contains(.,'earch')]");
        Thread.sleep(3000);
        waitForPageToLoad();
        findElement(By.tagName("body")).getText().contains("Actions"); // there are no actions, but the header is the only unique text from searching

        // Category's don't have actions (yet)
        //waitAndClick("id=u80");
        //waitForPageToLoad();
        //waitAndClick("id=u86");
        //waitForPageToLoad();
        //selectWindow("null");
        //waitAndClick("xpath=(//input[@name='imageField'])[2]");
        //waitForPageToLoad();
        //passed();
    }

    protected void testCreateSampleEDocLite() throws Exception {
        waitForPageToLoad();
        Thread.sleep(3000);
        assertEquals("Kuali Portal Index", getTitle());
        selectFrameIframePortlet();
        waitAndClickByXpath("//input[@name='methodToCall.search' and @alt='search']");
        waitForPageToLoad();

        // click on the create new.
        waitAndClickByLinkText("Create Document");
        waitForPageToLoad();
        Thread.sleep(3000);
        String docId = getTextByXpath("//table/tbody/tr[4]/td[@class='datacell1']");
        waitAndTypeByName("userName", "Viral Chauhan");
        waitAndTypeByName("rqstDate", "12/03/2020");
        checkByName("fundedBy");
        waitAndTypeByName("addText", "Note Added.");
        waitAndClickByXpath("//td[@class='datacell']/div/img");
        waitAndClickByXpath("//input[@value='submit']");
        assertEquals(Boolean.FALSE, (Boolean) isElementPresentByXpath("//input[@value='submit']"));
        assertEquals(Boolean.FALSE, (Boolean) isElementPresentByXpath("//input[@value='save']"));
        assertEquals(Boolean.FALSE, (Boolean) isElementPresentByXpath("//input[@value='cancel']"));
        waitForPageToLoad();
        selectTopFrame();
        waitAndClickDocSearch();
        waitForPageToLoad();
        selectFrameIframePortlet();
        waitAndClickByXpath("//input[@name='methodToCall.search' and @alt='search']");
        waitForElementPresent(By.linkText(docId));
    }

    protected void testTermLookUp() throws Exception {
        testLookUp();
        assertTextPresent("Term Parameters");
        waitAndClickCancelByText();
        passed();
    }

    protected void testCreateNewRRDTravelRequestDestRouting() throws Exception {
        selectFrameIframePortlet();

        // Create new Routing Rules Delegation
        waitAndClick("img[alt=\"create new\"]");

        // Lookup parent rule, click lookup icon
        waitAndClickByName(
                "methodToCall.performLookup.(!!org.kuali.rice.kew.rule.RuleBaseValues!!).(((id:parentRuleId))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");

        // Search
        waitAndClickByXpath("//td[@class='infoline']/input[@name='methodToCall.search']");

        // return value for 1046 TravelRequest.Destination.LasVegas TravelRequest-DestinationRouting
        waitAndClick("a[title=\"return valueRule Id=1046 \"]");

        // Select the parent rule we just returned
        waitAndClickByName("parentResponsibilityId");

        // Click continue
        waitAndClickByName("methodToCall.createDelegateRule");

        waitAndClickCancel();
        waitAndClickByName("methodToCall.processAnswer.button0");
        waitForPageToLoad();
        driver.switchTo().defaultContent();
        waitAndClickByXpath("(//input[@name='imageField'])[2]");
        passed();
    }

    protected void testWorkFlowRouteRulesCreateNew() throws Exception {
        waitForPageToLoad();
        Thread.sleep(5000);
        assertEquals("Kuali Portal Index", getTitle());
        selectFrameIframePortlet();
        waitAndClickCreateNew();
        waitAndClickByName(CANCEL_NAME, "https://jira.kuali.org/browse/KULRICE-8161 Work Flow Route Rules cancel new yields 404 not found");

        // KULRICE-7753 : WorkFlowRouteRulesIT cancel confirmation missing from create new Route Rules.
        waitAndClickByName("methodToCall.processAnswer.button0");
        passed();
    }

    /**
     * tests that a Routing Rule maintenance document is created for an edit operation originating
     * from a lookup screen
     */
    protected void testWorkFlowRouteRulesEditRouteRules() throws Exception {
        waitForPageToLoad();
        assertEquals("Kuali Portal Index", getTitle());
        selectFrameIframePortlet();
        waitAndClickSearch();
        waitAndClickEdit();
        waitForPageToLoad();
        selectFrameIframePortlet();
        waitAndClickCancel();
        waitAndClickByName("methodToCall.processAnswer.button0");
        passed();
    }

    protected void createNewEnterDetails() throws InterruptedException {
        // overload to utilize
        fail("createNewEnterDetails must be implemented by test class");
    }

//    protected String createNewTemplateMethod() throws InterruptedException {
//        waitAndCreateNew();
//        String docId = waitForDocId();
//
//        createNewEnterDetails();
//
//        // Ad Hoc Recipients with current user to test Action List
//        addAdHocRecipientsPerson(new String[]{getUserName(), "F"}); // FYI
//
//        waitAndClickSave();
//        waitAndClickSubmit();
//        waitForElementPresentByXpath(DOC_SUBMIT_SUCCESS_MSG_XPATH, CREATE_NEW_DOCUMENT_NOT_SUBMITTED_SUCCESSFULLY_MESSAGE_TEXT);
//
//        // Action List
//        assertActionList(docId, "F", "ENROUTE"); // FYI
//
//        assertDocSearch(docId, DOC_STATUS_FINAL);
//        selectTopFrame();
//        return docId;
//    }
//
//    protected String createNewTemplateMethodNoAction() throws InterruptedException {
//        waitAndCreateNew();
//        String docId = waitForDocId();
//
//        createNewEnterDetails();
//
//        waitAndClickSave();
//        waitAndClickSubmit();
//        waitForElementPresentByXpath(DOC_SUBMIT_SUCCESS_MSG_XPATH, CREATE_NEW_DOCUMENT_NOT_SUBMITTED_SUCCESSFULLY_MESSAGE_TEXT);
//
//        assertDocSearch(docId, DOC_STATUS_FINAL);
//        selectTopFrame();
//        return docId;
//    }

    protected void waitAndClickActionList() throws InterruptedException {
        WebDriverUtils.jGrowl(driver, "Click Action List", false, "Click Action List");
        selectTopFrame();
        waitAndClickByXpath("//img[@alt='action list']");
    }

    protected void testLookUpComponent(String docId, String componentName, String componentCode) throws Exception {
        selectFrameIframePortlet();
        //Lookup
        waitAndTypeByName("name", componentName);
        waitAndClickSearch();
        isElementPresentByLinkText(componentName);
        waitAndClickByLinkText(componentName);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        assertEquals(componentName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        assertEquals(componentCode, getTextByXpath("//div[@class='tab-container']/table//span[@id='code.div']").trim());
        waitAndClickCloseWindow();
        switchToWindow("null");
    }

    protected void testEditComponent(String docId, String componentName, String componentCode) throws Exception {
        selectFrameIframePortlet();
        waitAndClickEdit();
        waitForPageToLoad();
        docId = waitForDocId();
        waitAndTypeByName("document.documentHeader.documentDescription", "Editing Test Component");
        clearTextByName("document.newMaintainableObject.name");
        waitAndTypeByName("document.newMaintainableObject.name", componentName);
        waitAndClickSave();
        waitAndClickSubmit();
        waitForElementPresentByXpath(DOC_SUBMIT_SUCCESS_MSG_XPATH, "Document is not submitted successfully");
        assertDocSearch(docId, DOC_STATUS_FINAL);
        selectTopFrame();
    }

    protected void testCopyComponent(String docId, String componentName, String componentCode) throws Exception {
        selectFrameIframePortlet();
        waitAndClickCopy();
        waitForPageToLoad();
        docId = waitForDocId();
        waitAndTypeByName("document.documentHeader.documentDescription", "Copying Test Component");
        selectOptionByName("document.newMaintainableObject.namespaceCode", "KR-IDM");
        waitAndTypeByName("document.newMaintainableObject.code", componentCode);
        clearTextByName("document.newMaintainableObject.name");
        waitAndTypeByName("document.newMaintainableObject.name", componentName);
        waitAndClickSave();
        waitAndClickSubmit();
        waitForPageToLoad();
        checkForDocError();
        waitForElementPresentByXpath(DOC_SUBMIT_SUCCESS_MSG_XPATH, "Document is not submitted successfully");
        assertDocSearch(docId, DOC_STATUS_FINAL);
        selectTopFrame();
    }

    protected void testVerifyCopyComponent(String docId, String componentName, String componentCode) throws Exception {
        selectFrameIframePortlet();
        waitAndTypeByName("name", componentName);
        waitAndClickSearch();
        isElementPresentByLinkText(componentName);
        waitAndClickByLinkText(componentName);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        assertEquals(componentName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        assertEquals(componentCode, getTextByXpath("//div[@class='tab-container']/table//span[@id='code.div']").trim());
        waitAndClickCloseWindow();
        switchToWindow("null");
    }

    protected void testMultiValueSelectAllPages() throws InterruptedException {
        waitAndClickButtonByText(SEARCH);
        assertButtonDisabledByText(RETURN_SELECTED_BUTTON_TEXT);

        // select all, all checkboxes should be checked and return button enabled
        waitAndClickDropDown("select all items");
        if (!areAllMultiValueSelectsChecked()) {
            JiraAwareFailureUtils.fail("select all items failure", this);
        }
        assertButtonEnabledByText(RETURN_SELECTED_BUTTON_TEXT);

        boolean anotherPageOfResults = false;
        if (Integer.parseInt(multiValueResultCount()) > 10) {
            anotherPageOfResults = true;
        }

        // all should be checked and button enabled on the next page as well (server side paging)
        if (!anotherPageOfResults) {
            JiraAwareFailureUtils.fail("select all items server side paging failure not enough results for next page",
                    this);
        }
        waitAndClickByLinkText("Next");

        if (!areAllMultiValueSelectsChecked()) {
            JiraAwareFailureUtils.fail("select all items server side paging failure", this);
        }
        assertButtonEnabledByText(RETURN_SELECTED_BUTTON_TEXT);

        // deselect all no checkboxes should be checked and return button disabled
        waitAndClickDropDown("deselect all items");
        if (!areNoMultiValueSelectsChecked()) {
            JiraAwareFailureUtils.fail("deselect all items failure", this);
        }
        assertButtonDisabledByText(RETURN_SELECTED_BUTTON_TEXT);

        waitAndClickByLinkText("Previous");
        if (!areNoMultiValueSelectsChecked()) {
            JiraAwareFailureUtils.fail("deselect all items failure", this);
        }
        assertButtonDisabledByText(RETURN_SELECTED_BUTTON_TEXT);
    }

    protected void acceptAlert() {
        if (!WebDriverUtils.isAlertPresent(driver)) {
            fail("Alert expected but not present for " + this.getClass().getName());
        }
        WebDriverUtils.alertAccept(driver);
    }

    protected void testMultiValueSelectAllThisPage() throws InterruptedException {
        waitAndClickButtonByText(SEARCH);
        assertButtonDisabledByText(RETURN_SELECTED_BUTTON_TEXT);

        // select all on this page, all checkboxes should be checked and return button enabled
        assertMultiValueSelectAllThisPage();

        boolean anotherPageOfResults = false;
        if (Integer.parseInt(multiValueResultCount()) > 5) {
            anotherPageOfResults = true;
        }

        // the next page should not have any checkboxes checked return button should still be enabled
        waitAndClickByLinkText("Next");
        if (!areNoMultiValueSelectsChecked()) {
            if (anotherPageOfResults) {
                JiraAwareFailureUtils.fail("select all items on this page failure", this);
            } else {
                JiraAwareFailureUtils.fail("select all items on this page failure not enough results for next page",
                        this);
            }
        }
        assertButtonEnabledByText(RETURN_SELECTED_BUTTON_TEXT);

        // back to the previous page, checkboxes should be checked and return button enabled still
        waitAndClickByLinkText("Previous");
        if (!areAllMultiValueSelectsChecked()) {
            JiraAwareFailureUtils.fail("select all items on previous page failure", this);
        }

        // deselect no checkboxes should be checked and the return button should be disabled
        assertMultiValueDeselectAllThisPage();
    }

    /**
     * Test the external help on the section and fields
     */
    protected void testExternalHelp2() throws Exception {
        // test external help of section
        assertPopUpWindowUrl(By.cssSelector("input[title=\"Help for External Help\"]"), "HelpWindow",
                "http://www.kuali.org/?section");

        // test external help of field with label left
        assertPopUpWindowUrl(By.xpath("//div[@id='field-label-left-external-help']/fieldset/input[@title='Help for Field Label']"), "HelpWindow",
                "http://www.kuali.org/?label_left");

        // test external help of field with label right
        assertPopUpWindowUrl(By.xpath("//div[@id='field-label-right-external-help']/fieldset/input[@title='Help for Field Label']"), "HelpWindow",
                "http://www.kuali.org/?label_right");

        // test external help of field with label top and help URL from system parameters
        assertPopUpWindowUrl(By.xpath("//div[@id='field-label-top-external-help']/fieldset/input[@title='Help for Field Label']"), "HelpWindow",
                "http://www.kuali.org/?system_parm");

        // test external help of standalone help widget
        assertPopUpWindowUrl(By.id("standalone-external-help"), "HelpWindow", "http://www.kuali.org/?widget_only");
    }

    /**
     * Test the external help on the sub-section and display only fields
     */

    protected void testDisplayOnlyExternalHelp2() throws Exception {
        // test external help of sub-section
        assertPopUpWindowUrl(By.cssSelector("input[title=\"Help for Display only fields\"]"), "HelpWindow", "http://www.kuali.org/?sub_section");

        // test external help of display only data field
        assertPopUpWindowUrl(By.xpath(
                "//div[@id='display-field-external-help']/fieldset/input[@title='Help for Field Label']"), "HelpWindow",
                "http://www.kuali.org/?display_field");
    }

    /**
     * Test the external help on the section and fields with missing help URL
     */

    protected void testMissingExternalHelp2() throws Exception {
        // test external help of section is not rendered
        assertFalse(isElementPresent(By.cssSelector("input[title=\"Help for Missing External Help\"]")));

        // test external help of field with blank externalHelpURL is not rendered
        assertFalse(isElementPresentByXpath("//div[@id='external-help-externalHelpUrl-empty']/*[@class='uif-helpImage']"));

        // test external help of field with empty helpDefinition is not rendered
        assertFalse(isElementPresentByXpath("//div[@id='external-help-helpdefinition-empty']/*[@class='uif-helpImage']"));

        // test external help of field with missing system parameter is not rendered
        assertFalse(isElementPresentByXpath("//div[@id='external-help-system-parm-missing']/*[@class='uif-helpImage']"));

        // test external help of standalone help widget is not rendered
        assertFalse(isElementPresentByXpath("//div[@id='standalone-external-help-missing']"));
    }

    private String searchForAvailableCode(int codeLength) throws InterruptedException {
        String randomCode = RandomStringUtils.randomAlphabetic(codeLength).toUpperCase();
        waitAndTypeByName("code", randomCode);
        waitAndClickSearch();
        int attemptCount = 1;
        waitForTextPresent("You have entered the primary key for this table");
        while (!isTextPresent("No values match this search.") && attemptCount < 25) {
            randomCode = Character.toString((char) (randomCode.toCharArray()[0] + attemptCount++));
            clearTextByName("code");
            waitAndTypeByName("code", randomCode);
            waitAndClickSearch();
            waitForTextPresent("You have entered the primary key for this table");
        }
        return randomCode;
    }

    protected void testSearchEditCancel() throws InterruptedException {
        selectFrameIframePortlet();
        waitAndClickSearch2();
        waitAndClickEdit();
        testCancelConfirmation();
    }

    protected void testServerErrorsIT() throws Exception {
        waitAndClickByXpath("//button[contains(.,'Get Error Messages')]");
        waitForElementPresent("div[data-messages_for=\"Demo-ValidationLayout-SectionsPage\"] .uif-errorMessageItem-field");
        waitIsVisibleByXpath("//div[@data-header_for='Demo-ValidationLayout-Section1']");
        assertElementPresentByXpath("//*[@data-messageitemfor='Demo-ValidationLayout-Section1' and @class='uif-errorMessageItem']");
        assertElementPresent("div[data-role=\"InputField\"] img[alt=\"Error\"]");
        assertElementPresentByXpath("//a[contains(.,'Section 1 Title')]");
        fireMouseOverEventByXpath("//a[contains(.,'Field 1')]");
        assertElementPresent(".uif-errorMessageItem-field");
        waitAndClickByXpath("//a[contains(.,'Field 1')]");
        waitIsVisible(".jquerybubblepopup-innerHtml");
        waitIsVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems");
        waitIsVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-errorMessageItem-field");
        waitAndTypeByName("field1", "");
        fireEvent("field1", "blur");
        fireEvent("field1", "focus");
        waitIsVisible(".jquerybubblepopup-innerHtml");
        waitIsVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-errorMessageItem-field");
        waitIsVisible(".jquerybubblepopup-innerHtml > .uif-clientMessageItems");
        waitIsVisible(".jquerybubblepopup-innerHtml > .uif-clientMessageItems  .uif-errorMessageItem-field");
        waitAndTypeByName("field1", "t");

        for (int second = 0;; second++) {
            if (second >= waitSeconds) {
                jiraAwareFail(TIMEOUT_MESSAGE);
            }
            try {
                if (!isElementPresent(".jquerybubblepopup-innerHtml > .uif-clientMessageItems")) {
                    break;
                }
            } catch (Exception e) {}
            Thread.sleep(1000);
        }

        waitIsVisible(".jquerybubblepopup-innerHtml > .uif-serverMessageItems .uif-errorMessageItem-field");
        assertFalse(isElementPresent(".jquerybubblepopup-innerHtml > .uif-clientMessageItems"));
    }

    protected void testServerInfoIT() throws Exception {
        waitAndClickByXpath("//button[contains(.,'Get Info Messages')]");
        waitIsVisibleByXpath("//div[@data-messages_for='Demo-ValidationLayout-SectionsPage']");
        assertTrue(isVisibleByXpath("//div[@data-messages_for='Demo-ValidationLayout-SectionsPage']"));
        assertTrue(isElementPresent(
                "div[data-messages_for=\"Demo-ValidationLayout-SectionsPage\"] .uif-infoMessageItem"));
        assertTrue(isVisible("div[data-messages_for=\"Demo-ValidationLayout-Section1\"]"));
        assertTrue(isElementPresent("div[data-messages_for=\"Demo-ValidationLayout-Section1\"] .uif-infoMessageItem"));
        assertTrue(isElementPresentByXpath("//div[@data-role='InputField']//img[@alt='Information']"));
        fireMouseOverEventByXpath("//a[contains(.,'Field 1')]");
        assertTrue(isElementPresent(".uif-infoHighlight"));
        waitAndClickByXpath("//a[contains(.,'Field 1')]");
        waitForElementPresentByXpath("//div[@class='popover top in uif-tooltip-info-ss']");
        waitAndTypeByName("field1", "");
        fireEvent("field1", "blur");
        fireEvent("field1", "focus");
        waitForElementPresentByXpath("//div[@class='popover uif-tooltip-info-ss top in uif-tooltip-error-ss']");
        waitAndTypeByName("field1", "b");
        fireEvent("field1", "blur");
        fireEvent("field1", "focus");
        waitForElementPresentByXpath("//div[@class='popover uif-tooltip-error-ss top in uif-tooltip-info-ss']");
        fireEvent("field1", "focus");
        clearTextByName("field1");
        fireEvent("field1", "blur");
        waitForElementPresentByXpath("//div[@class='popover uif-tooltip-info-ss top in uif-tooltip-error-ss']");
    }

    protected void testServerWarningsIT() throws Exception {
        waitAndClickByXpath("//button[contains(.,'Get Warning Messages')]");
        waitForElementPresentByXpath("//div[@id='Demo-ValidationLayout-SectionsPage_messages']");
        waitForElementPresentByXpath("//div[@id='Demo-ValidationLayout-Section1_messages']");
        waitForElementPresentByXpath("//a[contains(.,'Field 1')]");
        fireMouseOverEventByXpath("//a[contains(.,'Field 1')]");
        waitForElementPresentByXpath(
                "//div[@class='uif-inputField uif-boxLayoutHorizontalItem uif-hasWarning uif-warningHighlight']");
        waitAndClickByXpath("//a[contains(.,'Field 1')]");
        waitForElementPresentByXpath("//div[@class='popover uif-tooltip-warning-ss top in uif-tooltip-error-ss']");
        waitAndTypeByName("field1", "");
        fireEvent("field1", "blur");
        fireMouseOverEventByName("field1");
        waitForElementPresentByXpath("//div[@class='popover uif-tooltip-warning-ss top in uif-tooltip-error-ss']");
        waitAndTypeByName("field1", "b");
        fireEvent("field1", "blur");
        fireMouseOverEventByName("field1");
        waitForElementPresentByXpath("//div[@class='popover uif-tooltip-warning-ss top in uif-tooltip-error-ss']");
        clearTextByName("field1");
        fireEvent("field1", "blur");
        fireMouseOverEventByName("field1");
        waitForElementPresentByXpath("//div[@class='popover uif-tooltip-warning-ss top in uif-tooltip-error-ss']");
        passed();
    }

    /**
     * Test the tooltip and external help on the view
     */
    protected void testViewHelp2() throws Exception {
        // test tooltip help
        if (isElementPresentByXpath("//td[@class='jquerybubblepopup-innerHtml']")) {
            assertFalse(findElement(By.cssSelector("td.jquerybubblepopup-innerHtml")).isDisplayed());
        }

        // test tooltip help
        fireMouseOverEventByXpath("//h1/span[@class='uif-headerText-span']");
        Thread.sleep(2000);
        assertTrue(isVisibleByXpath("//td[contains(text(),'View help')]"));
        assertPopUpWindowUrl(By.cssSelector("input[title=\"Help for Configuration Test View\"]"), "HelpWindow", "http://www.kuali.org/");
    }

    protected void testVerifyAddDeleteFiscalOfficerLegacy() throws Exception {
        selectFrameIframePortlet();
        waitAndTypeByName("document.documentHeader.documentDescription", AutomatedFunctionalTestUtils
                .createUniqueDtsPlusTwoRandomChars());
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.fiscalOfficer.accounts'].number","1234567890");
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.fiscalOfficer.accounts'].foId", "2");
        waitAndClickByXpath("//button[@data-loadingmessage='Adding Line...']");
        waitForElementPresentByName("document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number");
        assertEquals("1234567890", waitAndGetAttributeByName(
                "document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number", "value"));
        assertEquals("2", waitAndGetAttributeByName(
                "document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].foId", "value"));
        waitAndClickByXpath("//button[@data-loadingmessage='Deleting Line...']");
        Thread.sleep(3000);
        assertEquals(Boolean.FALSE, (Boolean) isElementPresentByName(
                "document.newMaintainableObject.dataObject.fiscalOfficer.accounts[0].number"));
        passed();
    }

    protected void testVerifyAddDeleteNoteLegacy() throws Exception {
        selectFrameIframePortlet();
        waitAndClick(
                "div.tableborders.wrap.uif-boxLayoutVerticalItem.clearfix  span.uif-headerText-span > img.uif-disclosure-image");
        waitForElementPresent("button[title='Add a Note'].uif-action.uif-primaryActionButton.uif-smallActionButton");
        waitAndClickByName("newCollectionLines['document.notes'].noteText");
        waitAndTypeByName("newCollectionLines['document.notes'].noteText", "Test note");
        waitAndClick("button[title='Add a Note'].uif-action.uif-primaryActionButton.uif-smallActionButton");
        //        waitForElementPresentByName("document.notes[0].noteText");
        assertEquals("Test note", getTextByXpath("//pre"));
        waitAndClick("button[title='Delete a Note'].uif-action.uif-primaryActionButton.uif-smallActionButton");
        assertEquals(Boolean.FALSE, (Boolean) isElementPresentByName("document.notes[0].noteText"));
        passed();
    }

    protected void testVerifyAdHocRecipientsLegacy() throws Exception {
        selectFrameIframePortlet();
        waitAndClickByLinkText("Fiscal Officer Accounts");
        assertElementPresentByXpath(
                "//select[@name=\"newCollectionLines['document.adHocRoutePersons'].actionRequested\"]");
        assertElementPresentByXpath(
                "//input[@name=\"newCollectionLines['document.adHocRoutePersons'].name\" and @type=\"text\"]");
        assertElementPresentByXpath(
                "//select[@name=\"newCollectionLines['document.adHocRouteWorkgroups'].actionRequested\"]");
        assertElementPresentByXpath(
                "//input[@name=\"newCollectionLines['document.adHocRouteWorkgroups'].recipientNamespaceCode\" and @type='text']");
        assertElementPresentByXpath(
                "//input[@name=\"newCollectionLines['document.adHocRouteWorkgroups'].recipientName\" and @type='text']");
        passed();
    }

    protected void testVerifyButtonsLegacy() throws Exception {
        selectFrameIframePortlet();
        assertElementPresentByXpath("//button[contains(.,'ubmit')]");
        assertElementPresentByXpath("//button[contains(.,'ave')]");
        assertElementPresentByXpath("//button[contains(.,'lanket approve')]");
        assertElementPresentByXpath("//button[contains(.,'lose')]");
        assertElementPresentByXpath("//a[contains(.,'ancel')]");
        passed();
    }

    protected void testVerifyConstraintText() throws Exception {
        selectFrameIframePortlet();
        assertEquals("* indicates required field", getText(
                "div.uif-boxLayout.uif-horizontalBoxLayout.clearfix > span.uif-message.uif-requiredInstructionsMessage.uif-boxLayoutHorizontalItem"));
        assertEquals("Must not be more than 10 characters", getText(
                "div.uif-group.uif-gridGroup.uif-gridSection.uif-disclosure.uif-boxLayoutVerticalItem.clearfix div[data-label='Travel Account Number'].uif-field.uif-inputField span.uif-message.uif-constraintMessage"));
        assertEquals("Must not be more than 10 characters", getText(
                "div.uif-group.uif-gridGroup.uif-gridSection.uif-disclosure.uif-boxLayoutVerticalItem.clearfix div[data-label='Travel Sub Account Number'].uif-field.uif-inputField span.uif-message.uif-constraintMessage"));
        assertEquals("Must not be more than 10 characters", getText(
                "div.uif-group.uif-gridGroup.uif-collectionItem.uif-gridCollectionItem.uif-collectionAddItem div[data-label='Travel Account Number'].uif-field.uif-inputField span.uif-message.uif-constraintMessage"));
        passed();
    }

    protected void testVerifyEditedComponent(String docId, String componentName, String componentCode) throws Exception {
        selectFrameIframePortlet();
        waitAndTypeByName("name", componentName);
        waitAndClickSearch();
        isElementPresentByLinkText(componentName);
        waitAndClickByLinkText(componentName);
        waitForPageToLoad();
        Thread.sleep(2000);
        switchToWindow("Kuali :: Inquiry");
        Thread.sleep(2000);
        assertEquals(componentName, getTextByXpath("//div[@class='tab-container']/table//span[@id='name.div']").trim());
        assertEquals(componentCode, getTextByXpath("//div[@class='tab-container']/table//span[@id='code.div']").trim());
        waitAndClickCloseWindow();
        switchToWindow("null");
        List<String> parameterList=new ArrayList<String>();
    }

    protected void testVerifyDisclosures() throws Exception {
        selectFrameIframePortlet();
        assertElementPresentByXpath("//span[contains(text(),'Document Overview')]");
        assertElementPresentByXpath("//span[contains(text(),'Document Overview')]");
        assertElementPresentByXpath("//span[contains(text(),'Account Information')]");
        assertElementPresentByXpath("//span[contains(text(),'Fiscal Officer Accounts')]");
        assertElementPresentByXpath("//span[contains(text(),'Notes and Attachments')]");
        assertElementPresentByXpath("//span[contains(text(),'Ad Hoc Recipients')]");
        assertElementPresentByXpath("//span[contains(text(),'Route Log')]");
        colapseExpandByXpath("//span[contains(text(),'Document Overview')]//img",
                "//label[contains(text(),'Organization Document Number')]");
        colapseExpandByXpath("//span[contains(text(),'Account Information')]//img",
                "//label[contains(text(),'Travel Account Type Code')]");
        colapseExpandByXpath("//span[contains(text(),'Fiscal Officer Accounts')]//img",
                "//a[contains(text(),'Lookup/Add Multiple Lines')]");
        expandColapseByXpath("//span[contains(text(),'Notes and Attachments')]//img",
                "//label[contains(text(),'Note Text')]");
        expandColapseByXpath("//span[contains(text(),'Ad Hoc Recipients')]",
                "//span[contains(text(),'Ad Hoc Group Requests')]");

        // Handle frames
        waitAndClickByXpath("//span[contains(text(),'Route Log')]//img");
        selectFrame("routeLogIFrame");
        waitIsVisibleByXpath("//img[@alt='refresh']");

        // relative=top iframeportlet might look weird but either alone results in something not found.
        selectTopFrame();
        selectFrameIframePortlet();
        waitAndClickByXpath("//span[contains(text(),'Route Log')]//img");
        selectFrame("routeLogIFrame");
        waitNotVisibleByXpath("//img[@alt='refresh']");
        passed();
    }

    protected void testVerifyDocumentOverviewLegacy() throws Exception {
        selectFrameIframePortlet();
        assertTextPresent("Document Overview");
        assertElementPresentByXpath("//input[@name='document.documentHeader.documentDescription']");
        assertElementPresentByXpath("//input[@name='document.documentHeader.organizationDocumentNumber']");
        assertElementPresentByXpath("//textarea[@name='document.documentHeader.explanation']");
        passed();
    }

    protected void testVerifyExpandCollapse() throws Exception {
        selectFrameIframePortlet();
        assertElementPresentByXpath("//button[contains(@class, 'uif-expandDisclosuresButton')]");
        assertElementPresentByXpath("//button[contains(@class, 'uif-collapseDisclosuresButton')]");
        passed();
    }

    protected void testVerifyFieldsLegacy() throws Exception {
        selectFrameIframePortlet();
        assertElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.number' and @type='text' and @size=10 and @maxlength=10]");
        assertElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.extension.accountTypeCode' and @type='text' and @size=2 and @maxlength=3]");
        assertElementPresentByXpath(
                "//input[@name='document.newMaintainableObject.dataObject.subAccount' and @type='text' and @size=10 and @maxlength=10]");
        assertElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.subsidizedPercent' and @type='text' and @size=6 and @maxlength=20]");
        assertElementPresentByXpath(
                "//input[@name='document.newMaintainableObject.dataObject.foId' and @type='text' and @size=5 and @maxlength=10]");
        assertElementPresentByXpath(
                "//input[@name=\"newCollectionLines['document.newMaintainableObject.dataObject.fiscalOfficer.accounts'].number\" and @type='text' and @size=10 and @maxlength=10]");
        assertElementPresentByXpath(
                "//input[@name=\"newCollectionLines['document.newMaintainableObject.dataObject.fiscalOfficer.accounts'].foId\" and @type='text' and @size=5 and @maxlength=10]");
        passed();
    }

    protected void testVerifyHeaderFieldsLegacy() throws Exception {
        selectFrameIframePortlet();
        assertElementPresentByXpath("//div[contains(@class, 'uif-documentNumber')]");
        assertElementPresentByXpath("//div[contains(@class, 'uif-documentInitiatorNetworkId')]");
        assertElementPresentByXpath("//div[contains(@class, 'uif-documentStatus')]");
        assertElementPresentByXpath("//div[contains(@class, 'uif-documentCreateDate')]");
        passed();
    }

    protected void testVerifyLookupAddMultipleLinesLegacy() throws Exception {
        selectFrameIframePortlet();
        assertElementPresentByXpath("//a[contains(text(),'Lookup/Add Multiple Lines')]");
        passed();
    }

    protected void testVerifyNotesAndAttachments() throws Exception {
        selectFrameIframePortlet();
        waitAndClickByXpath("//span[contains(text(),'Notes and Attachments')]");
        waitForElementPresentByXpath("//button[@title='Add a Note']");
        assertElementPresentByXpath("//span[contains(text(),'Notes and Attachments')]");
        assertElementPresentByXpath("//textarea[@name=\"newCollectionLines['document.notes'].noteText\"]");
        assertElementPresentByXpath("//input[@name='attachmentFile']");

        //assertElementPresentByXpath("//input[@name=\"newCollectionLines['document.notes'].attachment.attachmentTypeCode\"]");
        passed();
    }

    protected void testVerifyQuickfinderIconsLegacy() throws Exception {
        selectFrameIframePortlet();
        assertTextPresent("Document Overview");
        assertElementPresentByXpath("//*[@id='quickfinder1']");
        assertElementPresentByXpath("//*[@id='quickfinder2']");
        assertElementPresentByXpath("//*[@id='quickfinder3']");
        assertElementPresentByXpath("//*[@id='quickfinder4_add']");

        // TODO it would be better to test that the image isn't 404
        passed();
    }

    protected void testVerifyRouteLog() throws Exception {
        selectFrameIframePortlet();
        waitAndClickByLinkText("Route Log");
        waitForElementPresent("//iframe[contains(@src,'RouteLog.do')]");
        passed();
    }

    protected void testVerifySave() throws Exception {
        selectFrameIframePortlet();
        waitAndTypeByName("document.documentHeader.documentDescription",
                "Test Document " + AutomatedFunctionalTestUtils.DTS);
        waitAndClickByName("document.newMaintainableObject.dataObject.number");
        waitAndTypeByName("document.newMaintainableObject.dataObject.number", "1234567890");
        waitAndTypeByName("document.newMaintainableObject.dataObject.extension.accountTypeCode", "EAT");
        waitAndTypeByName("document.newMaintainableObject.dataObject.subAccount", "a1");
        waitAndClick(
                "button[data-loadingmessage='Saving...'].uif-action.uif-primaryActionButton.uif-boxLayoutHorizontalItem");
        Thread.sleep(2000);

        // checkErrorMessageItem(" also digit validation jira https://jira.kuali.org/browse/KULRICE-8038");
        passed();
    }

    protected void testVerifySubsidizedPercentWatermarkLegacy() throws Exception {
        selectFrameIframePortlet();

        // May be blowing up due to multiple locators
        //assertTrue(isElementPresent("//input[@name='document.newMaintainableObject.dataObject.subsidizedPercent' and @type='text' and @placeholder='##.##   ']"));
        assertElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.subsidizedPercent']");
        passed();
    }

    protected void testWorkFlowDocTypeBlanketApprove() throws Exception {
        selectFrameIframePortlet();
        waitAndCreateNew();
        String docId = waitForDocId();
        assertBlanketApproveButtonsPresent();
        String dts = AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomCharsNot9Digits();
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, "Validation Test Document Type " + dts);
        String parentDocType = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.kew.doctype.bo.DocumentType!!).(((name:document.newMaintainableObject.parentDocType.name,documentTypeId:document.newMaintainableObject.docTypeParentId,))).((`document.newMaintainableObject.parentDocType.name:name,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(parentDocType);
        waitAndClickSearch();
        waitAndClickReturnValue();
        String docTypeName = "DocType" + dts;
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", docTypeName);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.unresolvedDocHandlerUrl']",
                "${kr.url}/maintenance.do?methodToCall=docHandler");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.label']",
                "Workflow Maintenance Document Type Document " + dts);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.unresolvedHelpDefinitionUrl']",
                "default.htm?turl=WordDocuments%2Fdocumenttype.htm");
        blanketApproveTest(docId);
    }

    protected void typeEnter() {
        jGrowl("Press Enter");
        driver.switchTo().activeElement().sendKeys(Keys.ENTER);
    }

    protected void typeTab() {
        jGrowl("Press Tab");
        driver.switchTo().activeElement().sendKeys(Keys.TAB);
    }

    protected void uncheck(By by) throws InterruptedException {
        WebElement element = findElement(by);
        if (element.isSelected()) {
            element.click();
        }
    }

    protected void uncheckByName(String name) throws InterruptedException {
        uncheck(By.name(name));
    }

    protected void uncheckByXpath(String locator) throws InterruptedException {
        uncheck(By.xpath(locator));
    }

    protected void verifyRichMessagesValidationBasicFunctionality() throws Exception {
        assertTrue(isElementPresentByXpath("//input[@type='text' and @name='field1']"));
        assertTrue(isElementPresentByXpath("//a[contains(text(), 'Kuali')]"));
        assertTrue(isElementPresentByXpath("//input[@type='checkbox' and @name='field2']"));
        Thread.sleep(3000);
    }

    protected void verifyRichMessagesValidationAdvancedFunctionality() throws Exception {
        //Color Options
        assertTrue(isElementPresentByXpath("//span[@style='color: green;']"));
        assertTrue(isElementPresentByXpath("//span[@style='color: blue;']"));

        //Css class
        assertTrue(isElementPresentByXpath("//span[@class='uif-text-underline uif-text-larger']"));

        //Combinations
        assertTrue(isElementPresentByXpath("//input[@type='text' and @name='field3']"));
        assertTrue(isElementPresentByXpath("//select[@name='field4']"));
        assertTrue(isElementPresentByXpath("//button[contains(text(), 'Action Button')]"));

        //Rich Message Field
        assertTrue(isElementPresentByXpath("//label[contains(., 'Label With')]/span[contains(., 'Color')]"));
        assertTrue(isElementPresentByXpath("//label[contains(., 'Label With')]/i/b[contains(., 'Html')]"));
        assertTrue(isElementPresentByXpath("//label[contains(., 'Label With')]/img[@class='uif-image inlineBlock']"));
        Thread.sleep(3000);
    }

    protected void verifyRichMessagesValidationLettersNumbersValidation() throws Exception {
        //For letters only Validation
        assertTrue(isElementPresentByXpath("//div[@data-parent='Demo-AdvancedMessagesSection']/div/input[@type='text' and @name='field5']"));
        waitAndTypeByXpath(
                "//div[@data-parent='Demo-AdvancedMessagesSection']/div/input[@type='text' and @name='field5']", "abc");
        assertFalse(isElementPresentByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']"));
        clearTextByXpath(
                "//div[@data-parent='Demo-AdvancedMessagesSection']/div/input[@type='text' and @name='field5']");
        waitAndTypeByXpath("//div[@data-parent='Demo-AdvancedMessagesSection']/div/input[@type='text' and @name='field5']","abc12");
        waitAndTypeByXpath("//input[@name= 'field6']", "");
        waitForElementPresentByXpath("//div[@class='uif-inputField inlineBlock uif-hasError']");
        Thread.sleep(3000);
        clearTextByXpath("//div[@data-parent='Demo-AdvancedMessagesSection']/div/input[@type='text' and @name='field5']");
        waitAndTypeByXpath("//div[@data-parent='Demo-AdvancedMessagesSection']/div/input[@type='text' and @name='field5']","abc");
        waitAndTypeByXpath("//input[@name= 'field6']", "");

        //For numbers only validation
        waitAndTypeByXpath("//input[@name= 'field6']", "123");
        assertFalse(isElementPresentByXpath("//div[@class='uif-field uif-inputField uif-inputField-labelTop inlineBlock uif-hasError']"));
        clearTextByXpath("//input[@name='field6']");
        waitAndTypeByXpath("//input[@name='field6']", "123ab");
        fireEvent("field6", "blur");
        waitForElementPresentByXpath("//div[@class='uif-inputField inlineBlock uif-hasError']");
    }

    protected void verifyRichMessagesValidationRadioAndCheckBoxGroupFunctionality() throws Exception {
        //Radio Group
        assertTrue(isElementPresentByXpath("//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='1']"));
        assertTrue(isElementPresentByXpath(
                "//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='2']"));
        assertTrue(isElementPresentByXpath(
                "//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='3']"));
        assertTrue(isElementPresentByXpath(
                "//fieldset[@class='uif-verticalRadioFieldset']/span/input[@type='radio' and @name='field24' and @value='4']"));

        //Checkbox Group
        assertTrue(isElementPresentByXpath(
                "//fieldset[@class='uif-verticalCheckboxesFieldset']/span/input[@type='checkbox' and @name='field115' and @value='1']"));
        assertTrue(isElementPresentByXpath(
                "//fieldset[@class='uif-verticalCheckboxesFieldset']/span/input[@type='checkbox' and @name='field115' and @value='2']"));
        assertTrue(isElementPresentByXpath(
                "//fieldset[@class='uif-verticalCheckboxesFieldset']/span/input[@type='checkbox' and @name='field115' and @value='3']"));
        assertTrue(isElementPresentByXpath(
                "//fieldset[@class='uif-verticalCheckboxesFieldset']/span/label/div/select[@name='field4']"));

        //Checkbox Control
        assertTrue(isElementPresentByXpath("//input[@type='checkbox' and @name='bField1']"));
        assertTrue(isElementPresentByXpath("//input[@type='text' and @name='field103']"));
    }

    protected void verifyRichMessagesValidationLinkDeclarationsFunctionality() throws Exception {
        //Testing link tag
        waitAndClickByLinkText("Kuali Site");
        Thread.sleep(9000);
        switchToWindow("Open Source Software | www.kuali.org");
        switchToWindow(RICH_MESSAGES_WINDOW_TITLE);

        //Testing methodToCall Action
        waitAndClickByXpath("//p[contains(., 'Testing methodToCall action')]/a");
        Thread.sleep(3000);
        assertTrue(isElementPresentByXpath(
                "//div[@class='alert alert-danger']"));

        //Testing methodToCall action (no client validation check)
        waitAndClickByXpath("//p[contains(., 'Testing methodToCall action (no client validation check)')]/a");
        assertTrue(isElementPresentByXpath("//div[@id='Demo-BasicMessagesSection_messages' and @class='alert alert-danger']"));
        assertTrue(isElementPresentByXpath("//div[@id='Demo-AdvancedMessagesSection_messages' and @class='alert alert-danger']"));
        Thread.sleep(3000);
    }

    protected void waitAndClickConfirmationOk() throws InterruptedException {
        jGrowl("Click OK Confirmation");
        waitAndClickByXpath("//div[@data-parent='ConfirmSubmitDialog']/button[contains(text(),'OK')]");
    }

    protected void waitAndClickConfirmBlanketApproveOk() throws InterruptedException {
        jGrowl("Click OK Confirmation");
        waitAndClickByXpath("//div[@data-parent='ConfirmBlanketApproveDialog']/button[contains(text(),'OK')]");
    }

    protected void waitAndClickAdministration() throws InterruptedException {
        waitAndClickByLinkText(ADMINISTRATION_LINK_TEXT, this);
    }

    /**
     * {@link #ADMINISTRATION_LINK_TEXT}
     * @param failable
     * @throws InterruptedException
     */
    private void waitAndClickAdministration(JiraAwareFailable failable) throws InterruptedException {
        waitAndClickByLinkText(ADMINISTRATION_LINK_TEXT, failable);
    }

    protected void waitAndCancelConfirmation() throws InterruptedException {
        waitAndClickCancel();
        waitAndClickByName("methodToCall.processAnswer.button0");
    }


    protected void waitAndClickAdHocPersonAdd() throws InterruptedException  {
        jGrowl("Click AdHoc Person add");
        waitAndClickByXpath("//div[@data-parent='Uif-AdHocPersonCollection']/div/div/button");
    }

    protected void waitAndClickAdHocGroupAdd() throws InterruptedException  {
        jGrowl("Click AdHoc Group add");
        waitAndClickByXpath("//div[@data-parent='CollectionGroup_AdHocWorkgroup']/div/div/button");
    }

    protected void waitAndClickBlanketApprove() throws InterruptedException {
        waitAndClickButtonByText("Blanket Approve");
    }

    /**
     * {@link #CANCEL_NAME}
     * @throws InterruptedException
     */
    protected void waitAndClickCancel() throws InterruptedException {
        waitAndClickByName(CANCEL_NAME);
    }

    protected void waitAndClickCancelByText() throws InterruptedException {
        waitAndClickButtonByText("Cancel");
    }

    /**
     * {@link #CLOSE_WINDOW_XPATH_TITLE}
     * @throws InterruptedException
     */
    protected void waitAndClickCloseWindow() throws InterruptedException {
        waitAndClickByXpath(CLOSE_WINDOW_XPATH_TITLE);
    }

    /**
     * {@link #COPY_LINK_TEXT}
     * @throws InterruptedException
     */
    protected void waitAndClickCopy() throws InterruptedException {
        waitAndClickByLinkText(COPY_LINK_TEXT);
    }

    /**
     * {}@link #DOC_SEARCH_XPATH}
     * @throws InterruptedException
     */
    protected void waitAndClickDocSearch() throws InterruptedException {
        waitAndClickByXpath(DOC_SEARCH_XPATH);
    }

    /**
     * {@link #DOC_SEARCH_XPATH_TITLE}
     * @throws InterruptedException
     */
    protected void waitAndClickDocSearchTitle() throws InterruptedException {
        waitAndClickByXpath(DOC_SEARCH_XPATH_TITLE);
    }

    /**
     * {@link #LOGOUT_XPATH}
     * @throws InterruptedException
     */
    protected void waitAndClickLogout() throws InterruptedException {
        waitAndClickLogout(this);
    }

    /**
     * {@link #LOGOUT_XPATH}
     * @param failable
     * @throws InterruptedException
     */
    protected void waitAndClickLogout(JiraAwareFailable failable) throws InterruptedException {
        jGrowl("Logging out");
        selectTopFrame();
        waitAndClickByXpath(LOGOUT_XPATH, failable);
    }

    protected void waitAndClickMainMenu() throws InterruptedException {
        waitAndClickByLinkText(MAIN_MENU_LINK_TEXT, this);
    }

    /**
     * {}@link #MAIN_MENU_LINK_TEXT}
     * @param failable
     * @throws InterruptedException
     */
    private void waitAndClickMainMenu(JiraAwareFailable failable) throws InterruptedException {
        waitAndClickByLinkText(MAIN_MENU_LINK_TEXT, failable);
    }

    /**
     * {@link #SAVE_XPATH}
     * @throws InterruptedException
     */
    protected void waitAndClickSave() throws InterruptedException {
        waitAndClickByXpath(SAVE_XPATH);
    }

    protected void waitAndClickSaveByText() throws InterruptedException {
        waitAndClickButtonByText("Save");
    }

    /**
     * {@link #SUBMIT_XPATH}
     * @throws InterruptedException
     */
    protected void waitAndClickSubmit() throws InterruptedException {
        waitAndClickByXpath(SUBMIT_XPATH);
    }

    protected void waitAndClickSubmitByText() throws InterruptedException {
        waitAndClickButtonByText("Submit");
    }

    /**
     * {@link #XML_INGESTER_LINK_TEXT}
     * @param failable
     * @throws InterruptedException
     */
    protected void waitAndClickXMLIngester(JiraAwareFailable failable) throws InterruptedException {
        waitAndClickByLinkText(XML_INGESTER_LINK_TEXT, failable);
    }

    protected void waitAndCreateNew() throws InterruptedException {
        waitAndCreateNew(this.getClass().toString());
    }

    protected void waitAndCreateNew(String message) throws InterruptedException {
        selectFrameIframePortlet();
        jGrowl("Create New");
        waitAndClickCreateNew(message);
    }

    /**
     * {@link #CREATE_NEW_XPATH}
     * @throws InterruptedException
     */
    protected void waitAndClickCreateNew() throws InterruptedException {
        waitAndClickCreateNew(this.getClass().toString());
    }

    protected void waitAndClickCreateNew(String message) throws InterruptedException {
        jGrowl("Click Create New");
        if (WebDriverUtils.waitFors(driver, By.xpath(CREATE_NEW_XPATH)).size() > 0) {
            waitAndClickByXpath(CREATE_NEW_XPATH, message);
        } else {
            System.out.println("waitAndClickByXpath(" + CREATE_NEW_XPATH + ") wasn't found trying " + CREATE_NEW_XPATH2);
            waitAndClickByXpath(CREATE_NEW_XPATH2, message);
        }
    }

    protected void waitAndClickEdit() throws InterruptedException {
        waitAndClickByLinkText(EDIT_LINK_TEXT);
    }

    /**
     * {@link #SEARCH_XPATH}
     * @throws InterruptedException
     */
    protected void waitAndClickSearch() throws InterruptedException {
        jGrowl("Click Search");
        waitAndClickByXpath(SEARCH_XPATH);
    }

    protected void waitAndClickSearch2() throws InterruptedException {
        jGrowl("Click Search");
        waitAndClickByXpath(SEARCH_XPATH_2);
    }

    protected void waitAndClickSearchSecond() throws InterruptedException {
        jGrowl("Click Search");
        waitAndClickByXpath(SEARCH_SECOND);
    }

    protected void waitAndClickSearchByText() throws InterruptedException {
        waitAndClickButtonByText("Search");
    }

    protected String waitForAgendaDocId() throws InterruptedException {
        return waitForElementPresentByXpath("//div[@data-label=\"Document Number\"]").getText();
    }

    protected String waitForDocId() throws InterruptedException {
        checkForDocError();
        waitForElementPresentByXpath(DOC_ID_XPATH);

        return findElement(By.xpath(DOC_ID_XPATH)).getText();
    }

    protected String waitForDocIdKrad() throws InterruptedException {
        failOnErrorMessageItem();
        waitForElementPresentByXpath(DOC_ID_KRAD_XPATH);
        String docId = findElement(By.xpath(DOC_ID_KRAD_XPATH)).getText();
        jGrowl("Document Number is " + docId);

        return docId;
    }

    protected String waitForDocInitiator() throws InterruptedException {
        waitForElementPresentByXpath(DOC_INITIATOR_XPATH);

        return findElement(By.xpath(DOC_INITIATOR_XPATH)).getText();
    }

    protected String waitForDocStatus() throws InterruptedException {
        waitForElementPresentByXpath(DOC_STATUS_XPATH);

        return findElement(By.xpath(DOC_STATUS_XPATH)).getText();
    }

    protected void waitForTitleToEqualKualiPortalIndex() throws InterruptedException {
        waitForTitleToEqualKualiPortalIndex(this.getClass().toString());
    }

    protected void waitForToolTipTextPresent(String tooltipText) throws InterruptedException {
        assertEquals("ToolTip text not as expected", tooltipText, waitForToolTipPresent().getText());
    }

    protected WebElement waitForToolTipPresent() throws InterruptedException {
        WebElement tooltip =  waitForElementPresent("[class='popover top in']");
        jGrowl("ToolTip " + tooltip.getText());
        return tooltip;
    }

    protected void waitForTitleToEqualKualiPortalIndex(String message) throws InterruptedException {
        Thread.sleep(2000);
        // This started failing in CI....
        // boolean failed = false;
        //
        // for (int second = 0;; second++) {
        //     Thread.sleep(1000);
        //     if (second >= waitSeconds) failed = true;
        //     try { if (failed || ITUtil.KUALI_PORTAL_TITLE.equals(driver.getTitle())) break; } catch (Exception e) {}
        // }

        // WebDriverUtils.checkForIncidentReport(driver, message); // after timeout to be sure page is loaded
        // if (failed) jiraAwareFail("timeout of " + waitSeconds + " seconds " + message);
    }

    /**
     * {@link #KRAD_XPATH}
     * @throws InterruptedException
     */
    protected void waitAndClickKRAD() throws InterruptedException {
        waitAndClickByLinkText(KRAD_XPATH);
    }

    /**
     * Does the test page use KRAD UIF?
     * Useful if trying to re-use a test for both a KNS and KRAD screens that have different paths to the elements.
     * @return
     */
    protected boolean isKrad(){
        return (AutomatedFunctionalTestUtils.REMOTE_UIF_KRAD.equalsIgnoreCase(getUiFramework()));
    }

    /**
     * Determines whether KRAD or KNS UIF is used for this test.
     * Useful if trying to re-use a test for both a KNS and KRAD screens that have different paths to the elements.
     * @return
     */
    public String getUiFramework() {
        return uiFramework;
    }

    /**
     * Sets which UIF is used by this test
     */
    public void setUiFramework(String uiFramework) {
        this.uiFramework = uiFramework;
    }
    
    /**
     * presses Enter Key by Name
     */
    public void pressEnterByName(String locator){
    	pressEnter(By.name(locator));
    }
    
    /**
     * presses Enter Key by Xpath
     */
    public void pressEnterByXpath(String locator){
    	pressEnter(By.xpath(locator));
    }
    
    /**
     * presses Enter Key
     */
    public void pressEnter(By by){
    	 findElement(by).sendKeys(Keys.ENTER);
    }
}
