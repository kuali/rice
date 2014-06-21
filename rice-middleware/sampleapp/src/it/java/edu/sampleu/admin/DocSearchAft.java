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

import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocSearchAft extends WebDriverLegacyITBase {

    public static String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Document%20Type&channelUrl=" + WebDriverUtils
            .getBaseUrlString() + "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kew.doctype.bo.DocumentType&returnLocation=" + AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    String docId;
    String parentName;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

   public void createAndSaveDoc() throws Exception{
        waitForTitleToEqualKualiPortalIndex();
        waitAndClickByLinkText("Administration");
        waitForTitleToEqualKualiPortalIndex();
        waitAndClickByLinkText("Document Type");
        waitForTitleToEqualKualiPortalIndex();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//img[contains(@alt,'create new')]");
        waitForElementPresentByXpath("//*[@name='methodToCall.route' and @alt='submit']","save button does not exist on the page");
        waitForElementPresentByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        docId = driver.findElement(By.xpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]")).getText();
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Creating new Document Type");
        String parentDocType = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.kew.doctype.bo.DocumentType!!).(((name:document.newMaintainableObject.parentDocType.name,documentTypeId:document.newMaintainableObject.docTypeParentId,))).((`document.newMaintainableObject.parentDocType.name:name,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;" + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(parentDocType);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        waitForPageToLoad();
        parentName= driver.findElement(By.xpath("//table[@id='row']/tbody/tr[1]/td[3]")).getText();
        waitAndClickByLinkText("return value");
        String docTypeName = "TestDocType " + AutomatedFunctionalTestUtils.DTS;
        waitForElementPresentByXpath("//input[@id='document.newMaintainableObject.name']");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", docTypeName);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.unresolvedDocHandlerUrl']", "${kr.url}/maintenance.do?methodToCall=docHandler");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.actualNotificationFromAddress']", "NFA");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.label']", "TestDocument Label");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.unresolvedHelpDefinitionUrl']", "default.htm?turl=WordDocuments%2Fdocumenttype.htm");
        waitAndClickByXpath("//input[@name='methodToCall.save' and @alt='save']");
        Thread.sleep(2000); // TODO wait for save confirmation
        checkForIncidentReport();
        selectTopFrame();
    }
    @Test
    public void testBasicDocSearchBookmark() throws Exception {
        createAndSaveDoc();
        waitAndClickByXpath("//a/img[@alt='doc search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='detailed search']");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='superuser search']");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='clear saved searches search']");
        waitAndTypeByName("documentTypeName", "DocumentTypeDocument");
        waitAndTypeByName("initiatorPrincipalName", "admin");
        waitAndTypeByName("documentId", docId);
        //waitAndTypeByName("rangeLowerBoundKeyPrefix_dateCreated", "10/01/2010");
        //waitAndTypeByName("dateCreated", "10/13/2010");
        waitAndClickMethodToCallSearchButton();
        selectFrameIframePortlet();
        assertEquals(docId, waitFor(By.xpath("//table[@id='row']/tbody/tr[1]/td[1]/a")).getText());
        //Thread.sleep(2000);
        waitAndClickByXpath("//input[@name='methodToCall.clearValues' and @alt='clear']");
        assertEquals("", driver.findElement(By.xpath("//input[@name='documentTypeName']")).getAttribute("value"));
        assertEquals("", driver.findElement(By.xpath("//input[@name='initiatorPrincipalName']")).getAttribute("value"));
        assertEquals("", driver.findElement(By.xpath("//input[@name='documentId']")).getAttribute("value"));
        assertEquals("", driver.findElement(By.xpath("//input[@name='rangeLowerBoundKeyPrefix_dateCreated']")).getAttribute("value"));
        assertEquals("", driver.findElement(By.xpath("//input[@name='dateCreated']")).getAttribute("value"));
        waitAndClickByXpath("//a[@title='cancel']");
        passed();
    }

    @Test
    public void testBasicFullDocSearchBookmark() throws Exception {
        waitAndClickByXpath("//a/img[@alt='doc search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='detailed search']");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='superuser search']");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='clear saved searches search']");
        waitAndTypeByName("rangeLowerBoundKeyPrefix_dateCreated", "1/1/1900"); // remove default today's date
        waitAndClickMethodToCallSearchButton();
        waitForTextPresent("Export options:");
        passed();
    }

    @Test
    public void testDetailedDocSearchBookmark() throws Exception{
        createAndSaveDoc();
        waitAndClickByXpath("//a/img[@alt='doc search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='detailed search']");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='superuser search']");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='clear saved searches search']");
        waitAndClickByXpath("//div[@class='lookupcreatenew']/input[@alt='detailed search']");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='basic search']", "DocSearchAft.testDetailedDocSearch");
        //waitAndTypeByName("documentTypeName", parentName);
        waitAndTypeByName("initiatorPrincipalName", "admin");
        waitAndTypeByName("documentId", docId);
        //waitAndTypeByName("rangeLowerBoundKeyPrefix_dateCreated", "10/01/2010");
        //waitAndTypeByName("dateCreated", "10/13/2010");
        assertElementPresentByName("approverPrincipalName", "Approver input field is not there in the detailed search");
        //waitAndTypeByName("approverPrincipalName", "director");
        assertElementPresentByName("viewerPrincipalName", "Viewer input field is not there in the detailed search");
        //waitAndTypeByName("viewerPrincipalName", "superviser");
        assertElementPresentByXpath("//select[@id='statusCode']", "Document Status select field is not there in the detailed search");
        selectByXpath("//select[@id='statusCode']", "- SAVED");
        waitAndClickMethodToCallSearchButton();
        assertTextNotPresent("No values match this search.");
        assertTrue(driver.findElement(By.id("row")).getText().contains("SAVED"));
        assertElementPresentByXpath("//table[@id='row']/tbody/tr[1]/td[contains(a,'admin')]");
        //Thread.sleep(2000);
        waitAndClickByXpath("//input[@name='methodToCall.clearValues' and @alt='clear']");
        //assertEquals("", driver.findElement(By.xpath("//input[@name='documentTypeName']")).waitAndGetAttribute("value"));
        assertEquals("", driver.findElement(By.xpath("//input[@name='initiatorPrincipalName']")).getAttribute("value"));
        //assertEquals("", driver.findElement(By.xpath("//input[@name='documentId']")).waitAndGetAttribute("value"));
        //assertEquals("", driver.findElement(By.xpath("//input[@name='rangeLowerBoundKeyPrefix_dateCreated']")).waitAndGetAttribute("value"));
        //assertEquals("", driver.findElement(By.xpath("//input[@name='dateCreated']")).waitAndGetAttribute("value"));
        waitAndClickByXpath("//a[@title='cancel']");
        passed();
    }
    
    @Test
    public void testSuperUserSearchBookmark() throws Exception{
        createAndSaveDoc();
        waitAndClickByXpath("//a/img[@alt='doc search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='detailed search']");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='superuser search']");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='clear saved searches search']");
        waitAndClickByXpath("//div[@class='lookupcreatenew']/input[@alt='superuser search']");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='non-superuser search']",
                "DocSearchAft.testSuperUserSearch");
        waitAndClickMethodToCallSearchButton();
        Thread.sleep(3000);
        selectFrameIframePortlet();
        waitAndClickByXpath("//table[@id='row']/tbody/tr[1]/td[1]/a");
        selectTopFrame();
        Thread.sleep(3000);
        switchToWindow("Kuali :: Superuser Document Service");
        waitForPageToLoad();
        //Thread.sleep(4000);

        waitAndClickByXpath("//input[@src='images/buttonsmall_complete.gif']");
        waitForElementPresentByName("methodToCall.approve", "approve button does not exist on the page");
        assertElementPresentByName("methodToCall.disapprove", "disapprove button does not exist on the page");
        assertElementPresentByName("methodToCall.cancel", "cancel button does not exist on the page");
        waitAndClickByName("methodToCall.approve", "approve button does not exist on the page");
        jGrowl("Click Cancel Button");
        waitAndClickByXpath("//a[@href='DocumentSearch.do']/img[@alt='cancel']");
        waitAndClickMethodToCallSearchButton();
        waitForPageToLoad();
        assertEquals("FINAL", driver.findElement(By.xpath("//table[@id='row']/tbody/tr[1]/td[4]")).getText());
        passed();
    }

    protected void waitAndClickMethodToCallSearchButton() throws InterruptedException {
        jGrowl("Click Search Button");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @alt='search']");
    }

    @Test
    public void testClearSavedSearchesBookmark() throws Exception{
        waitAndClickByXpath("//a/img[@alt='doc search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='detailed search']");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='superuser search']");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='clear saved searches search']");
        waitAndClickByXpath("//div[@class='lookupcreatenew']/input[@alt='clear saved searches search']");
        waitForPageToLoad();
        WebElement select1 = findElement(By.xpath("//select[@id='savedSearchToLoadAndExecute']"));
        List<WebElement> options = select1.findElements(By.tagName("option"));
        int count= options.size();
        assertEquals(5,count);
        passed();
    }
    
    @Test
    public void testAjaxPageReloadBookmark() throws Exception{
        waitAndClickByXpath("//a/img[@alt='doc search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='detailed search']");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='superuser search']");
        waitForElementPresentByXpath("//div[@class='lookupcreatenew']/input[@alt='clear saved searches search']");
        waitForElementPresentByXpath("//select[@id='savedSearchToLoadAndExecute']");
        waitAndTypeByName("documentTypeName", "KualiNotification");
        fireEvent("documentTypeName", "blur");
        waitForElementPresentByName("documentAttribute.notificationContentType");
        assertElementPresentByName("documentAttribute.notificationChannel");
        assertElementPresentByName("documentAttribute.notificationProducer");
        assertElementPresentByName("documentAttribute.notificationPriority");
        assertElementPresentByName("documentAttribute.notificationRecipients");
        assertElementPresentByName("documentAttribute.notificationSenders");
        waitAndClickByXpath("//a[@title='cancel']");
        passed();
    }
}
