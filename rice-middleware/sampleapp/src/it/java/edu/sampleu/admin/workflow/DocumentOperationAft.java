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
package edu.sampleu.admin.workflow;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentOperationAft extends WebDriverLegacyITBase {

    /**
     *   AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Document%20Operation&channelUrl="+ WebDriverUtils
     *   .getBaseUrlString()+"/kew/DocumentOperation.do";
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Document%20Operation&channelUrl="+ WebDriverUtils
            .getBaseUrlString()+"/kew/DocumentOperation.do";

    private static String documentId = null;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws InterruptedException {
        waitAndClickAdministration();
        waitAndClickByLinkText("Document Operation");
    }

    protected void testCreateDocument() throws Exception {
        waitForPageToLoad();
        waitAndClickAdministration();
        waitAndClickByLinkText("Document Type");
        selectFrameIframePortlet();
        waitAndClickByXpath("//a[@title='Create a new record']");
        selectFrameIframePortlet();
        String randomString = RandomStringUtils.randomAlphabetic(9).toUpperCase();
        documentId = getTextByXpath("//table[@summary='document header: general information']/tbody/tr/td");
        waitAndTypeByName("document.documentHeader.documentDescription",randomString);
        waitAndTypeByName("document.newMaintainableObject.name",randomString);
        waitAndTypeByName("document.newMaintainableObject.label","Label "+randomString);
        waitAndClickByName("methodToCall.route");
        waitForTextPresent("Document was successfully submitted.");
        selectParentWindow();
        waitAndClickAdministration();
        acceptAlertIfPresent();
        waitForPageToLoad();
        waitAndClickByLinkText("Document Operation");
    }

    protected void testDocumentOperation() throws Exception {
        waitForPageToLoad();
        selectFrameIframePortlet();
        if(documentId!=null) {
            waitAndTypeByName("documentId", documentId);
            waitAndClickByName("methodToCall.getDocument");
            waitForElementPresentByXpath("//input[@src='images/buttonsmall_save.gif']");
            assertTextPresent(
                    new String[] {"Document Actions", "Queue Document", "Queue Action Invocation", "Document ID:",
                            documentId});
        }
    }

    /**
     * Tests the document disapprove operation on the document operation screen
     * @throws Exception if errors while disapproving a document on the document operation screen
     */
    protected void testDocumentOperationDisapprove() throws Exception {
        waitAndClickMainMenu();
        String docId = createTestEdocLite();
        if (docId == null || "".equals(docId)) {
            fail("Returned document id is empty or null!");
        }

        disapproveDocViaDocOpScreen(docId);
        validateDocDisapprovedViaRouteLog(docId);
    }

    /**
     * Method validates the disapproved status within the document route log.
     * @param docId the document id
     * @throws Exception if errors while validating the document status in the route log.
     */
    private void validateDocDisapprovedViaRouteLog(String docId) throws Exception {
        jGrowl("Redirecting to portal screen");
        driver.get(WebDriverUtils.getBaseUrlString());
        waitAndClickDocSearch();
        selectFrameIframePortlet();
        waitAndTypeByName("documentId", docId);
        waitAndClickSearch();
        jGrowl("Clicking Route Log link.");
        waitAndClickByXpath("//*[@id=\"row\"]/tbody/tr/td[7]/a/img");
        assertTextPresent("DISAPPROVED");
    }

    /**
     * Given an example edoclite document id this method will disapproved the document from the document operations screen
     * @param docId the document id
     * @throws Exception if errors while disapproving this document.
     */
    private void disapproveDocViaDocOpScreen(String docId) throws Exception {
        jGrowl("Redirecting to portal screen");
        driver.get(WebDriverUtils.getBaseUrlString());
        waitAndClickAdministration();
        waitAndClickByLinkText("Document Operation");
        selectFrameIframePortlet();
        waitAndTypeByName("documentId", docId);
        waitAndClickByName("methodToCall.getDocument");
        jGrowl("Clicking Update Radio Button");
        waitAndClickByXpath("//*[@id=\"kualiForm\"]/table/tbody/tr/td[2]/table[2]/tbody/tr[5]/td[2]/table/tbody/tr[2]/td[2]/input[1]");
        selectByName("routeHeader.docRouteStatus", "DISAPPROVED");

        jGrowl("Clicking Update Radio on Action Request");
        waitAndClickByXpath("//*[@id=\"kualiForm\"]/table/tbody/tr/td[2]/table[2]/tbody/tr[6]/td[2]/table/tbody/tr[2]/td[2]/input[1]");
        jGrowl("Selecting DONE status for action request.");
        selectByName("actionRequests[0].status", "DONE");

        jGrowl("Clicking delete radio on Action Item");
        waitAndClickByXpath("//*[@id=\"kualiForm\"]/table/tbody/tr/td[2]/table[2]/tbody/tr[8]/td[2]/table/tbody/tr[2]/td[2]/input[2]");

        jGrowl("Clicking the Save button.");
        waitAndClickByXpath("//*[@id=\"kualiForm\"]/table/tbody/tr/td[2]/table[2]/tbody/tr[12]/td[2]/table/tbody/tr/th/div/input");
    }

    /**
     * Creates an edoclite from the example doctype
     * @return the document id as a String
     * @throws Exception if errors while creating the example edoc lite
     */
    private String createTestEdocLite() throws Exception {
        waitAndClickByLinkText("eDoc Lite");
        selectFrameIframePortlet();
        waitAndTypeByName("edlName", "eDoc.Example1Doctype");
        waitAndClickSearch();
        waitAndClickByLinkText("Create Document");
        waitAndTypeByName("userName", "Test User");
        waitAndTypeByName("rqstDate", getDateToday());
        waitAndSelectByName("campus", "IUPUI");
        waitAndTypeByName("addText", "This is a sample note.");
        jGrowl("Click Note Save Button");
        waitAndClickByXpath("//*[@id=\"edoclite\"]/table[2]/tbody/tr[3]/td[4]/div/img");
        selectFrameIframePortlet();
        jGrowl("Getting the document id.");
        String docId = getText(By.xpath("/html/body/table[2]/tbody/tr/td[2]/table/tbody/tr[4]/td[2]"));
        jGrowl("Document id is: " + docId);
        jGrowl("Click Edoc Lite Save Button.");
        waitAndClickByXpath("//*[@id=\"edoclite\"]/table[3]/tbody/tr/td/input[2]");
        return docId;
    }

    @Test
    public void testDocumentOperationBookmark() throws Exception {
        testCreateDocument();
        testDocumentOperation();
        passed();
    }

    @Test
    public void testDocumentOperationNav() throws Exception {
        testCreateDocument();
        testDocumentOperation();
        passed();
    }

    /**
     * Test the document disapproval process from the bookmark link
     * @throws Exception if errors while disapproving a document in the document operation.
     */
    @Test
    public void testDocumentOperationDisapproveBookmark() throws Exception {
        testDocumentOperationDisapprove();
        passed();
    }

    /**
     * Thest the document disapproval process by navigating to it.
     * @throws Exception if errors while disapproving a document in the document operation.
     */
    @Test
    public void testDocumentOperationDisapproveNav() throws Exception {
        testDocumentOperationDisapprove();
        passed();
    }
}
