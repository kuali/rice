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
package edu.samplu.mainmenu.test;

import edu.samplu.common.ITUtil;
import edu.samplu.common.WebDriverLegacyITBase;

import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * tests that user 'admin', on blanket approving a new Routing Rule maintenance document, results in a final document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkFlowRouteRulesBlanketAppLegacyIT extends WebDriverLegacyITBase {
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }
    @Test
    public void testUntitled() throws Exception {    
        assertEquals("Kuali Portal Index", getTitle());
        // click on the main menu Routing Rules link
        waitAndClickByLinkText("Routing Rules");
        waitForPageToLoad();
        Thread.sleep(3000);
        assertEquals("Kuali Portal Index", getTitle());
        selectFrame("iframeportlet");
        // click on the create new button
        waitAndClickByXpath("//img[@alt='create new']");
        waitForPageToLoad();
        // lookup on the Document Type Name
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kew.doctype.bo.DocumentType!!).(((name:documentTypeName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");
        waitForPageToLoad();
        // type in the name field the text RoutingRuleDocument
        waitAndTypeByName("name", "RoutingRuleDocument");
        // click the search button
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        waitForPageToLoad();
        // click the return value link
        waitAndClickByLinkText("return value");
        waitForPageToLoad();
        // lookup on the Rule Template Name
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kew.rule.bo.RuleTemplateBo!!).(((name:ruleTemplateName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");
        waitForPageToLoad();
        // type in the name field the text RuleRoutingTemplate
        waitAndTypeByName("name", "RuleRoutingTemplate");
        // click the search button
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        waitForPageToLoad();
        // click the return value link
        waitAndClickByLinkText("return value");
        waitForPageToLoad();
        // click the create new button
        waitAndClickByName("methodToCall.createRule");
        waitForPageToLoad();
        Thread.sleep(3000);        
        String docId = getTextByXpath("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        assertTrue(isElementPresentByName("methodToCall.cancel"));
        // type in the Document Overview Description the text Test Routing Rule
        waitAndTypeByXpath("//input[@id='document.documentHeader.documentDescription']", "Test Routing Rule");
        // click the Force Action checkbox
        waitAndClickByXpath("//input[@id='document.newMaintainableObject.forceAction']");
        // type in the Description text area the text Test Routing Rule1
        waitAndTypeByXpath("//textarea[@id='document.newMaintainableObject.description']", "Test Routing Rule1");
        // type in the Document type name field the text DocumentTypeDocument
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.fieldValues(1321~docTypeFullName)']", "DocumentTypeDocument");
        // lookup on Person
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalName:document.newMaintainableObject.add.personResponsibilities.principalName,))).((`document.newMaintainableObject.add.personResponsibilities.principalName:principalName,`)).((<>)).(([])).((**)).((^^)).((&&)).((/personImpl/)).((~~)).(::::;" + getBaseUrlString() + "/kr/lookup.do;::::).anchor15");
        waitForPageToLoad();
        // click the search button
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        waitForPageToLoad();
        // click the return value
        waitAndClickByLinkText("return value");
        waitForPageToLoad();
        // select from the Action Request ACKNOWLEDGE
        selectByXpath("//select[@id='document.newMaintainableObject.add.personResponsibilities.actionRequestedCd']", "ACKNOWLEDGE");
        // type in the Priority field the text 1
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.add.personResponsibilities.priority']", "1");
        // click the add button
        waitAndClickByName("methodToCall.addLine.personResponsibilities.(!!org.kuali.rice.kew.rule.PersonRuleResponsibility!!).(:::;15;:::).anchor15");
        waitForPageToLoad();
        checkForIncidentReport("methodToCall.blanketApprove");
        waitAndClickByName("methodToCall.blanketApprove");
        waitForPageToLoad();
        driver.switchTo().defaultContent();  //selectWindow("null");
        Thread.sleep(2000);
        waitAndClickByXpath("//img[@alt='doc search']");
        waitForPageToLoad();
        assertEquals("Kuali Portal Index", getTitle());
        selectFrame("iframeportlet");
        waitAndClickByXpath("//input[@name='methodToCall.search' and @value='search']");
        waitForPageToLoad();
       
        //docId= "link=" + docId;
        assertTrue(isElementPresent(By.linkText(docId)));
        if(isElementPresent(By.linkText(docId))){
            assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        }else{
            assertEquals(docId, getTextByXpath("//table[@id='row']/tbody/tr[1]/td[1]"));
            assertEquals("FINAL", getTextByXpath("//table[@id='row']/tbody/tr[1]/td[4]"));
        }
        
    }
}

