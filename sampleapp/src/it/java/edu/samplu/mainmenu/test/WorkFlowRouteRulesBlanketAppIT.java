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
import edu.samplu.common.UpgradedSeleniumITBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * tests that user 'admin', on blanket approving a new Routing Rule maintenance document, results in a final document
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WorkFlowRouteRulesBlanketAppIT extends UpgradedSeleniumITBase {
    @Override
    public String getTestUrl() {
        return PORTAL;
    }
    @Test
    public void testUntitled() throws Exception {    
        assertEquals("Kuali Portal Index", getTitle());
        // click on the main menu Routing Rules link
        waitAndClick("link=Routing Rules");
        waitForPageToLoad();
        assertEquals("Kuali Portal Index", getTitle());
        selectFrame("iframeportlet");
        // click on the create new button
        waitAndClick("//img[@alt='create new']");
        waitForPageToLoad();
        // lookup on the Document Type Name
        waitAndClick("methodToCall.performLookup.(!!org.kuali.rice.kew.doctype.bo.DocumentType!!).(((name:documentTypeName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");
        waitForPageToLoad();
        // type in the name field the text RoutingRuleDocument
        waitAndType("name", "RoutingRuleDocument");
        // click the search button
        waitAndClick("//input[@name='methodToCall.search' and @value='search']");
        waitForPageToLoad();
        // click the return value link
        waitAndClick("link=return value");
        waitForPageToLoad();
        // lookup on the Rule Template Name
        waitAndClick("methodToCall.performLookup.(!!org.kuali.rice.kew.rule.bo.RuleTemplateBo!!).(((name:ruleTemplateName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");
        waitForPageToLoad();
        // type in the name field the text RuleRoutingTemplate
        waitAndType("name", "RuleRoutingTemplate");
        // click the search button
        waitAndClick("//input[@name='methodToCall.search' and @value='search']");
        waitForPageToLoad();
        // click the return value link
        waitAndClick("link=return value");
        waitForPageToLoad();
        // click the create new button
        waitAndClick("methodToCall.createRule");
        waitForPageToLoad();
        String docId = getText("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        assertTrue(isElementPresent("methodToCall.cancel"));
        // type in the Document Overview Description the text Test Routing Rule
        waitAndType("//input[@id='document.documentHeader.documentDescription']", "Test Routing Rule");
        // click the Force Action checkbox
        waitAndClick("//input[@id='document.newMaintainableObject.forceAction']");
        // type in the Description text area the text Test Routing Rule1
        waitAndType("//textarea[@id='document.newMaintainableObject.description']", "Test Routing Rule1");
        // type in the Document type name field the text DocumentTypeDocument
        waitAndType("//input[@id='document.newMaintainableObject.fieldValues(1321~docTypeFullName)']", "DocumentTypeDocument");
        // lookup on Person
        waitAndClick("methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalName:document.newMaintainableObject.add.personResponsibilities.principalName,))).((`document.newMaintainableObject.add.personResponsibilities.principalName:principalName,`)).((<>)).(([])).((**)).((^^)).((&&)).((/personImpl/)).((~~)).(::::;" + getBaseUrlString() + "/kr/lookup.do;::::).anchor15");
        waitForPageToLoad();
        // click the search button
        waitAndClick("//input[@name='methodToCall.search' and @value='search']");
        waitForPageToLoad();
        // click the return value
        waitAndClick("link=return value");
        waitForPageToLoad();
        // select from the Action Request ACKNOWLEDGE
        select("//select[@id='document.newMaintainableObject.add.personResponsibilities.actionRequestedCd']", "label=ACKNOWLEDGE");
        // type in the Priority field the text 1
        waitAndType("//input[@id='document.newMaintainableObject.add.personResponsibilities.priority']", "1");
        // click the add button
        waitAndClick("methodToCall.addLine.personResponsibilities.(!!org.kuali.rice.kew.rule.PersonRuleResponsibility!!)");
        waitForPageToLoad();
        checkForIncidentReport("methodToCall.blanketApprove", "KULRICE-8142 Work Flow Route Rule Blanket Approval Incident Report NPE in MaintenanceDocumentRuleBase.processAddCollectionLineBusinessRules");
        waitAndClick("methodToCall.blanketApprove");
        waitForPageToLoad();
        selectWindow("null");
        Thread.sleep(2000);
        waitAndClick("//img[@alt='doc search']");
        waitForPageToLoad();
        assertEquals("Kuali Portal Index", getTitle());
        selectFrame("iframeportlet");
        waitAndClick("//input[@name='methodToCall.search' and @value='search']");
        waitForPageToLoad();
       
        docId= "link=" + docId;
        assertTrue(isElementPresent(docId));
        if(isElementPresent(docId)){
            assertEquals("FINAL", getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        }else{
            assertEquals(docId, getText("//table[@id='row']/tbody/tr[1]/td[1]"));
            assertEquals("FINAL", getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        }
    }
}
