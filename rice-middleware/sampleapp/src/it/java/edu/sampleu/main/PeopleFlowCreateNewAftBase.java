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
package edu.sampleu.main;

import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PeopleFlowCreateNewAftBase extends MainTmplMthdSTNavBase{

    /**
     * AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=People%20Flow&channelUrl="
     *  + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KRAD_LOOKUP_METHOD
     *  + "org.kuali.rice.kew.impl.peopleflow.PeopleFlowBo"
     *  + "&returnLocation=" + AutomatedFunctionalTestUtils.AutomatedFunctionalTestUtils + ITUtil.SHOW_MAINTENANCE_LINKS;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=People%20Flow&channelUrl="
            + WebDriverUtils.getBaseUrlString() + AutomatedFunctionalTestUtils.KRAD_LOOKUP_METHOD
            + "org.kuali.rice.kew.impl.peopleflow.PeopleFlowBo"
            + "&returnLocation=" + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.SHOW_MAINTENANCE_LINKS;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * People Flow
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "People Flow";
    }

    public void testPeopleFlowBlanketApproveBookmark(JiraAwareFailable failable) throws Exception {
        testPeopleFlowBlanketApprove();
        passed();
    }
    public void testPeopleFlowBlanketApproveNav(JiraAwareFailable failable) throws Exception {
        testPeopleFlowBlanketApprove();
        passed();
    }
    
    public void testPeopleFlowDuplicateEntryBookmark(JiraAwareFailable failable) throws Exception {
    	testPeopleFlowDuplicateEntry();
    	passed();
    }
    
    public void testPeopleFlowDuplicateEntryNav(JiraAwareFailable failable) throws Exception {
    	testPeopleFlowDuplicateEntry();
    	passed();
    }

    protected void testPeopleFlowBlanketApprove() throws Exception {
        String docId = peopleFlowCreateNew();

        waitAndClickBlanketApprove();
        waitAndClickConfirmBlanketApproveOk();
        Thread.sleep(3000);
        acceptAlert();
        Thread.sleep(3000);
        checkForIncidentReport();
        jGrowl("Blanket Approve");
        Thread.sleep(5000);

        //Close the Doc
        //findElement(By.id("uif-close")).click();
        //Thread.sleep(3000);
        driver.switchTo().window(driver.getWindowHandles().toArray()[0].toString());
        findElement(By.cssSelector("img[alt=\"doc search\"]")).click();
        Thread.sleep(5000);
        jGrowl("Document Search is " + docId + " present?");
        selectFrameIframePortlet();
        waitAndTypeByName("documentId", docId);
        jGrowl("Click search");
        findElement(By.cssSelector("td.infoline > input[name=\"methodToCall.search\"]")).click();
        waitForTextPresent(DOC_STATUS_FINAL);
    }

    protected void testPeopleFlowCreateNew() throws Exception {
        String docId = peopleFlowCreateNew();

        waitAndClickSubmitByText();
        waitAndClickConfirmationOk();
        Thread.sleep(3000);
        checkForDocError();
        checkForIncidentReport();

        //Close the Doc
        //findElement(By.id("uif-close")).click();
        //Thread.sleep(3000);
        driver.switchTo().window(driver.getWindowHandles().toArray()[0].toString());
        findElement(By.cssSelector("img[alt=\"doc search\"]")).click();
        Thread.sleep(5000);
        jGrowl("Document Search is " + docId + " present?");
        selectFrameIframePortlet();
        waitAndTypeByName("documentId", docId);
        jGrowl("Click search");
        findElement(By.cssSelector("td.infoline > input[name=\"methodToCall.search\"]")).click();
        waitForTextPresent(DOC_STATUS_FINAL);
    }

    private String peopleFlowCreateNew() throws InterruptedException {
        selectFrameIframePortlet();

        waitAndClickByLinkText("Create New");

        //Save docId
        waitForElementPresent("div[data-label='Document Number']");
        String docId = getText("div[data-label='Document Number']");
        assertTrue(docId != null);
        jGrowlSticky("Doc Id is " + docId);

        findElement(By.name("document.documentHeader.documentDescription")).clear();
        waitAndTypeByName("document.documentHeader.documentDescription", "Description for Document");
        waitAndSelectByName("document.newMaintainableObject.dataObject.namespaceCode", "KUALI - Kuali Systems");
        findElement(By.name("document.newMaintainableObject.dataObject.name")).clear();
        waitAndTypeByName("document.newMaintainableObject.dataObject.name", "Document Name" +
                AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomChars());

        jGrowl("Add Member kr");
        findElement(By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName")).clear();
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName", "kr");
        waitAndClick(By.cssSelector("button[data-loadingmessage='Adding Line...']"));
        Thread.sleep(3000);
        checkForIncidentReport();

        jGrowl("Add Member admin");
        findElement(By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName")).clear();
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName", "admin");
        waitAndClick(By.cssSelector("button[data-loadingmessage='Adding Line...']"));
        Thread.sleep(3000);
        return docId;
    }

    private void testPeopleFlowDuplicateEntry() throws Exception {
    	selectFrameIframePortlet();
        waitAndClickByLinkText("Create New");
        clearTextByName("document.documentHeader.documentDescription");
        waitAndTypeByName("document.documentHeader.documentDescription", "Description for Duplicate");
        waitAndSelectByName("document.newMaintainableObject.dataObject.namespaceCode", "KUALI - Kuali Systems");
        clearTextByName("document.newMaintainableObject.dataObject.name");
        String tempValue=AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomChars();
        waitAndTypeByName("document.newMaintainableObject.dataObject.name", "Document Name"+tempValue);
        waitAndClickSubmitByText();
        waitAndClickConfirmationOk();
        waitForTextPresent("Document was successfully submitted.");
        selectTopFrame();
        waitAndClickByLinkText("Main Menu");
        waitAndClickByLinkText("People Flow");
        selectFrameIframePortlet();
        waitAndClickByLinkText("Create New");
        clearTextByName("document.documentHeader.documentDescription");
        waitAndTypeByName("document.documentHeader.documentDescription", "Description for Duplicate");
        waitAndSelectByName("document.newMaintainableObject.dataObject.namespaceCode", "KUALI - Kuali Systems");
        clearTextByName("document.newMaintainableObject.dataObject.name");
        waitAndTypeByName("document.newMaintainableObject.dataObject.name", "Document Name"+tempValue);
        waitAndClickSubmitByText();
        waitAndClickConfirmationOk();
        waitForTextPresent("A PeopleFlow already exists with the name");
    }
}
