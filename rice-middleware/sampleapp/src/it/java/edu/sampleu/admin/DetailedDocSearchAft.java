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
package edu.sampleu.admin;

import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

/**
 * Tests whether the ENABLE_FIELD_LEVEL_HELP_IND parameter is being considered and loaded on each request.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DetailedDocSearchAft extends WebDriverLegacyITBase {
    /*
     * AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Document%20Search&channelUrl=" + WebDriverUtils
     * .getBaseUrlString() + "/kew/DocumentSearch.do?docFormKey=88888888&returnLocation=" + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;
     */
    
    public static String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Document%20Search&channelUrl=" + WebDriverUtils
            .getBaseUrlString() + "/kew/DocumentSearch.do?docFormKey=88888888&returnLocation=" + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    private String groupId = null;
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
       waitAndClickByXpath("//img[@alt='doc search']"); 
    }

    @Test
    public void testAdvancedDocSearchNav() throws Exception {
        advancedDocSearchAll();
    }
    
    @Test
    public void testAdvancedDocSearchBookmark() throws Exception {
        advancedDocSearchAll();
    }
    
    private void advancedDocSearchAll() throws Exception{
    	createGroupDocument();
    	selectTopFrame();
    	waitAndClickByXpath("//a[@title='Document Search']");
    	acceptAlertIfPresent();
    	selectFrameIframePortlet();
        waitAndClickByName("toggleAdvancedSearch");
        acceptAlertIfPresent();
        searchByDocumentType();
        searchByInitiator();
        searchByApprover();
        searchByViewer();
        searchByGroupViewer();
        searchByDocumentId();
        searchByApplicationDocumentId();
        searchByDocumentStatus();
        searchByDateCreatedFrom();
        searchByDateCreatedTo();
        searchByDateApprovedFrom();
        searchByDateApprovedTo();
        searchByDateLastModifiedFrom();
        searchByDateLastModifiedTo();
        searchByDateLastFinalizedFrom();
        searchByDateLastFinalizedTo();
        searchByTitle();
    }
    
    private void createGroupDocument() throws Exception{
    	waitAndClickAdministration();
    	selectFrameIframePortlet();
    	waitAndClickByLinkText("Group");
    	selectFrameIframePortlet();
    	waitAndClickByXpath("//a[@title='Create a new record']");
    	selectFrameIframePortlet();
    	String randomCode = RandomStringUtils.randomAlphabetic(9).toUpperCase();
        waitAndSelectByName("document.groupNamespace","KR-BUS - Service Bus");
    	waitAndTypeByName("document.documentHeader.documentDescription","Group");
    	waitAndTypeByName("document.groupName","Group 1"+randomCode);
    	groupId=waitForElementPresentByXpath("//div[@id='tab-Overview-div']/div[@class='tab-container']/table/tbody/tr/td").getText();
    	waitAndClickByXpath("//input[@name='methodToCall.route']");
    }

    private void searchByDocumentType() throws Exception {
        selectFrameIframePortlet();
        waitAndTypeByName("documentTypeName","ParameterMaintenanceDocument");
        waitAndTypeByName("rangeLowerBoundKeyPrefix_dateCreated","03/24/2000");
        waitAndClickByXpath("//td/input[@type='image' and @name='methodToCall.search']");
        waitForTextPresent("Parameter Maintenance Document");
        waitAndClickByName("methodToCall.clearValues");
    }
    
    private void searchByInitiator() throws Exception {
        selectFrameIframePortlet();
        waitAndTypeByName("initiatorPrincipalName","admin");
        waitAndClickByXpath("//td/input[@type='image' and @name='methodToCall.search']");
        waitForTextPresent("items retrieved");
        waitForElementPresentByXpath("//a[contains(text(),'admin, admin')]");
        waitAndClickByName("methodToCall.clearValues");
    }

    private void searchByApprover() throws Exception {
        selectFrameIframePortlet();
        waitAndTypeByName("approverPrincipalName","admin");
        waitAndClickByXpath("//td/input[@type='image' and @name='methodToCall.search']");
//        waitForTextPresent("items retrieved");
//        waitForElementPresentByXpath("//a[contains(text(),'admin, admin')]");
//        waitAndClickByName("methodToCall.clearValues");
    }

    private void searchByViewer() throws Exception {
        waitAndTypeByName("viewerPrincipalName","admin");
        clearTextByName("approverPrincipalName");
        waitAndTypeByName("rangeLowerBoundKeyPrefix_dateCreated","03/24/2000");
        waitAndClickByXpath("//td/input[@type='image' and @name='methodToCall.search']");
        waitForTextPresent("items retrieved");
        waitForElementPresentByXpath("//a[contains(text(),'admin, admin')]");
        waitAndClickByName("methodToCall.clearValues");
    }
    
    private void searchByGroupViewer() throws Exception {
        waitAndClickByXpath("//input[@type='image' and @alt='Search Group Viewer Id']");
        selectFrameIframePortlet();
        waitAndTypeByName("id",groupId);
        waitAndClickByXpath("//td/input[@type='image' and @name='methodToCall.search']");
        waitAndClickLinkContainingText("return value");
        selectFrameIframePortlet();
        waitAndTypeByName("rangeLowerBoundKeyPrefix_dateCreated","03/24/2000");
        waitAndClickByXpath("//td/input[@type='image' and @name='methodToCall.search']");
        waitForTextPresent("Component Maintenance Document");
        waitForElementPresentByXpath("//a[contains(text(),'admin, admin')]");
        waitAndClickByName("methodToCall.clearValues");
    }
    
    private void searchByDocumentId() throws Exception {
        waitAndTypeByName("documentId","2700");
        waitAndClickByXpath("//td/input[@type='image' and @name='methodToCall.search']");
        waitForElementPresentByXpath("//a[contains(text(),'2700')]");
        waitAndClickByName("methodToCall.clearValues");
    }
    
    private void searchByApplicationDocumentId() throws Exception {
        waitAndTypeByName("applicationDocumentId","7777777");
        waitAndClickByXpath("//td/input[@type='image' and @name='methodToCall.search']");
        waitForElementPresentByXpath("//a[contains(text(),'3221')]");
        waitAndClickByName("methodToCall.clearValues");
    }
    
    private void searchByDocumentStatus() throws Exception {
        selectByName("statusCode","Successful Statuses");
        waitAndClickByXpath("//td/input[@type='image' and @name='methodToCall.search']");
        waitForTextPresent("FINAL");
        waitAndClickByName("methodToCall.clearValues");
    }
    
    private void searchByDateCreatedFrom() throws Exception {
        waitAndTypeByName("rangeLowerBoundKeyPrefix_dateCreated","04/17/2005");
        waitAndClickByXpath("//td/input[@type='image' and @name='methodToCall.search']");
        waitForTextPresent("items retrieved");
        waitAndClickByName("methodToCall.clearValues");
    }
    
    private void searchByDateCreatedTo() throws Exception {
        waitAndTypeByName("dateCreated","04/17/2014");
        waitAndClickByXpath("//td/input[@type='image' and @name='methodToCall.search']");
        waitForTextPresent("items retrieved");
        waitAndClickByName("methodToCall.clearValues");
    }
    
    private void searchByDateApprovedFrom() throws Exception {
        waitAndTypeByName("rangeLowerBoundKeyPrefix_dateApproved","04/17/2005");
        waitAndClickByXpath("//td/input[@type='image' and @name='methodToCall.search']");
        waitForTextPresent("items retrieved");
        waitAndClickByName("methodToCall.clearValues");
    }
    
    private void searchByDateApprovedTo() throws Exception {
        waitAndTypeByName("dateApproved","04/17/2014");
        waitAndClickByXpath("//td/input[@type='image' and @name='methodToCall.search']");
        waitForTextPresent("items retrieved");
        waitAndClickByName("methodToCall.clearValues");
    }
    
    private void searchByDateLastModifiedFrom() throws Exception {
        waitAndTypeByName("rangeLowerBoundKeyPrefix_dateLastModified","04/17/2005");
        waitAndClickByXpath("//td/input[@type='image' and @name='methodToCall.search']");
        waitForTextPresent("items retrieved");
        waitAndClickByName("methodToCall.clearValues");
    }
 
    private void searchByDateLastModifiedTo() throws Exception {
        waitAndTypeByName("dateLastModified","04/17/2014");
        waitAndClickByXpath("//td/input[@type='image' and @name='methodToCall.search']");
        waitForTextPresent("items retrieved");
        waitAndClickByName("methodToCall.clearValues");
    }
    
    private void searchByDateLastFinalizedFrom() throws Exception {
        waitAndTypeByName("rangeLowerBoundKeyPrefix_dateFinalized","04/17/2005");
        waitAndClickByXpath("//td/input[@type='image' and @name='methodToCall.search']");
        waitForTextPresent("items retrieved");
        waitAndClickByName("methodToCall.clearValues");
    }

    private void searchByDateLastFinalizedTo() throws Exception {
        waitAndTypeByName("dateFinalized","04/17/2014");
        waitAndClickByXpath("//td/input[@type='image' and @name='methodToCall.search']");
        waitForTextPresent("items retrieved");
        waitAndClickByName("methodToCall.clearValues");
    }
    
    private void searchByTitle() throws Exception {
        waitAndTypeByName("title","Travel Doc 2 - dfads");
        waitAndTypeByName("rangeLowerBoundKeyPrefix_dateCreated","03/24/2000");
        waitAndClickByXpath("//td/input[@type='image' and @name='methodToCall.search']");
        waitForTextPresent("Travel Doc 2 - dfads");
        waitAndClickByName("methodToCall.clearValues");
    }
}
