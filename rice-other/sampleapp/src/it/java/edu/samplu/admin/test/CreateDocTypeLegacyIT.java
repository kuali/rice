/**
 * Copyright 2005-2011 The Kuali Foundation
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
package edu.samplu.admin.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;

import edu.samplu.common.AdminMenuLegacyITBase;
import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

/**
 * Create a document type via the document type creation screen and verify it in docsearch
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CreateDocTypeLegacyIT extends AdminMenuLegacyITBase{

    @Override
    protected String getLinkLocator() {
        return "Document Type";
    }
    @Test
    public void createDocType() throws Exception {
        super.gotoCreateNew();
        waitForPageToLoad();
        Thread.sleep(2000);
        assertElementPresentByXpath("//*[@name='methodToCall.route' and @alt='submit']","save button does not exist on the page");
        waitForElementPresentByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        String docId = driver.findElement(By.xpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]")).getText();
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Creating new Document Type");
        String parentDocType = "//input[@name='methodToCall.performLookup.(!!org.kuali.rice.kew.doctype.bo.DocumentType!!).(((name:document.newMaintainableObject.parentDocType.name,documentTypeId:document.newMaintainableObject.docTypeParentId,))).((`document.newMaintainableObject.parentDocType.name:name,`)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;" + getBaseUrlString() + "/kr/lookup.do;::::).anchor4']";
        waitAndClickByXpath(parentDocType);
        waitForPageToLoad();
        Thread.sleep(2000);
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        waitForPageToLoad();
        waitAndClickByLinkText("return value");
        String docTypeName = "TestDocType " + ITUtil.DTS;
        waitForElementPresentByXpath("//input[@id='document.newMaintainableObject.name']");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.name']", docTypeName);
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.unresolvedDocHandlerUrl']", "${kr.url}/maintenance.do?methodToCall=docHandler");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.actualNotificationFromAddress']", "NFA");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.label']", "TestDocument Label");
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.unresolvedHelpDefinitionUrl']", "default.htm?turl=WordDocuments%2Fdocumenttype.htm");
        
        waitAndClickByXpath("//*[@name='methodToCall.route' and @alt='submit']");
        checkForIncidentReport();
        waitForPageToLoad();
        selectTopFrame();
       // Thread.sleep(2000);
       // assertElementPresentByXpath("//div[contains(div,'Document was successfully submitted.')]", "Document is not submitted successfully");
        
        waitAndClickByXpath("//a[@title='Document Search']");
        waitForPageToLoad();
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        Thread.sleep(2000);
        
        assertEquals(docId, driver.findElement(By.xpath("//table[@id='row']/tbody/tr[1]/td[1]/a")).getText());
        
    }
}
