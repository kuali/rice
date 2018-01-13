/**
 * Copyright 2005-2018 The Kuali Foundation
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
        PeopleFlowDocInfo docInfo = peopleFlowCreateNew();

        jGrowl("Blanket Approve");
        waitAndClickBlanketApprove();
        waitAndClickConfirmBlanketApproveOk();
        acceptAlertIfPresent();
        waitForProgressLoading();
        checkForIncidentReport();

        driver.switchTo().window(driver.getWindowHandles().toArray()[0].toString());
        findElement(By.cssSelector("img[alt=\"doc search\"]")).click();
        Thread.sleep(5000);
        jGrowl("Document Search is " + docInfo.getDocId() + " present?");
        selectFrameIframePortlet();
        waitAndTypeByName("documentId", docInfo.getDocId());
        jGrowl("Click search");
        findElement(By.cssSelector("td.infoline > input[name=\"methodToCall.search\"]")).click();
        waitForTextPresent(DOC_STATUS_FINAL);
    }

    protected void testPeopleFlowCreateNew() throws Exception {
        PeopleFlowDocInfo docInfo = peopleFlowCreateNew();

        waitAndClickSubmitByText();
        waitAndClickConfirmSubmitOk();
        Thread.sleep(3000);
        checkForDocError();
        checkForIncidentReport();

        driver.switchTo().window(driver.getWindowHandles().toArray()[0].toString());
        findElement(By.cssSelector("img[alt=\"doc search\"]")).click();
        Thread.sleep(5000);
        jGrowl("Document Search is " + docInfo.getDocId() + " present?");
        selectFrameIframePortlet();
        waitAndTypeByName("documentId", docInfo.getDocId());
        jGrowl("Click search");
        findElement(By.cssSelector("td.infoline > input[name=\"methodToCall.search\"]")).click();
        waitForTextPresent(DOC_STATUS_FINAL);


        jGrowl("Find our PeopleFlow by lookup");
        driver.switchTo().window(driver.getWindowHandles().toArray()[0].toString());
        waitAndClick(By.linkText("Main Menu"));
        Thread.sleep(3000);

        waitAndClick(By.linkText("People Flow"));
        waitForPageToLoad();
        selectFrameIframePortlet();
        waitAndTypeByName("lookupCriteria[name]", docInfo.getName());

        jGrowl("Click search");
        waitAndClickByXpath(SEARCH_XPATH_3);
        waitForPageToLoad();
        waitAndClickByLinkText("edit");
        waitForPageToLoad();

        jGrowl("verify the forceAction values for our two stops");
        assertFalse(findElement(By.name("document.newMaintainableObject.dataObject.members[0].forceAction")).isSelected());
        assertTrue(findElement(By.name("document.newMaintainableObject.dataObject.members[1].forceAction")).isSelected());
    }

    private PeopleFlowDocInfo peopleFlowCreateNew() throws InterruptedException {
        selectFrameIframePortlet();

        waitAndClickByLinkText("Create New");
        String peopleFlowNamespace = "KUALI - Kuali Systems";
        String peopleFlowName = "Document Name" +
                AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomChars();

        //Save docId
        waitForElementPresent("div[data-label='Document Number']");
        String docId = getText("div[data-label='Document Number']");
        assertTrue(docId != null);
        jGrowlSticky("Doc Id is " + docId);

        findElement(By.name("document.documentHeader.documentDescription")).clear();
        waitAndTypeByName("document.documentHeader.documentDescription", "Description for Document");
        waitAndSelectByName("document.newMaintainableObject.dataObject.namespaceCode", peopleFlowNamespace);
        findElement(By.name("document.newMaintainableObject.dataObject.name")).clear();
        waitAndTypeByName("document.newMaintainableObject.dataObject.name", peopleFlowName);

        jGrowl("Add Member kr");
        findElement(By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName")).clear();
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName", "kr");
        findElement(By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].forceAction")).click();
        waitAndClick(By.cssSelector("button[data-loadingmessage='Adding Line...']"));
        Thread.sleep(3000);
        checkForIncidentReport();

        jGrowl("Add Member admin");
        findElement(By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].priority")).clear();
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.members'].priority", "2");
        findElement(By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName")).clear();
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName", "admin");
        waitAndClick(By.cssSelector("button[data-loadingmessage='Adding Line...']"));
        Thread.sleep(3000);

        return new PeopleFlowDocInfo(docId, peopleFlowNamespace, peopleFlowName);
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
        waitAndClickConfirmSubmitOk();
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
        waitAndClickConfirmSubmitOk();
        waitForTextPresent("A PeopleFlow already exists with the name");
    }

    /**
     * holds a few key pieces of info about a PeopleFlow document
     */
    public static class PeopleFlowDocInfo {
        private final String docId;
        private final String namespace;
        private final String name;

        /**
         * Construct an object with information about a PeopleFlow document
         */
        public PeopleFlowDocInfo(String docId, String namespace, String name) {
            this.docId = docId;
            this.namespace = namespace;
            this.name = name;
        }

        /**
         * @return the document ID
         */
        public String getDocId() {
            return docId;
        }

        /**
         * @return the namespace of the PeopleFlow
         */
        public String getNamespace() {
            return namespace;
        }

        /**
         * @return the name of the PeopleFlow
         */
        public String getName() {
            return name;
        }
    }
}
