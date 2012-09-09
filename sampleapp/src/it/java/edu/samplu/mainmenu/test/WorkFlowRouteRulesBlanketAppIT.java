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
        assertEquals("Kuali Portal Index", selenium.getTitle());
        // click on the main menu Routing Rules link
        selenium.click("link=Routing Rules");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.selectFrame("iframeportlet");
        // click on the create new button
        selenium.click("//img[@alt='create new']");
        selenium.waitForPageToLoad("30000");
        // lookup on the Document Type Name
        selenium.click("methodToCall.performLookup.(!!org.kuali.rice.kew.doctype.bo.DocumentType!!).(((name:documentTypeName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");
        selenium.waitForPageToLoad("30000");
        // type in the name field the text RoutingRuleDocument
        selenium.type("name", "RoutingRuleDocument");
        // click the search button
        selenium.click("//input[@name='methodToCall.search' and @value='search']");
        selenium.waitForPageToLoad("30000");
        // click the return value link
        selenium.click("link=return value");
        selenium.waitForPageToLoad("30000");
        // lookup on the Rule Template Name
        selenium.click("methodToCall.performLookup.(!!org.kuali.rice.kew.rule.bo.RuleTemplateBo!!).(((name:ruleTemplateName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");
        selenium.waitForPageToLoad("30000");
        // type in the name field the text RuleRoutingTemplate
        selenium.type("name", "RuleRoutingTemplate");
        // click the search button
        selenium.click("//input[@name='methodToCall.search' and @value='search']");
        selenium.waitForPageToLoad("30000");
        // click the return value link
        selenium.click("link=return value");
        selenium.waitForPageToLoad("30000");
        // click the create new button
        selenium.click("methodToCall.createRule");
        selenium.waitForPageToLoad("30000");
        String docId = selenium.getText("//div[@id='headerarea']/div/table/tbody/tr[1]/td[1]");
        assertTrue(selenium.isElementPresent("methodToCall.cancel"));
        // type in the Document Overview Description the text Test Routing Rule
        selenium.type("//input[@id='document.documentHeader.documentDescription']", "Test Routing Rule");
        // click the Force Action checkbox
        selenium.click("//input[@id='document.newMaintainableObject.forceAction']");
        // type in the Description text area the text Test Routing Rule1
        selenium.type("//textarea[@id='document.newMaintainableObject.description']", "Test Routing Rule1");
        // type in the Document type name field the text DocumentTypeDocument
        selenium.type("//input[@id='document.newMaintainableObject.fieldValues(1321~docTypeFullName)']", "DocumentTypeDocument");
        // lookup on Person
        selenium.click("methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalName:document.newMaintainableObject.add.personResponsibilities.principalName,))).((`document.newMaintainableObject.add.personResponsibilities.principalName:principalName,`)).((<>)).(([])).((**)).((^^)).((&&)).((/personImpl/)).((~~)).(::::;" + getBaseUrlString() + "/kr/lookup.do;::::).anchor15");
        selenium.waitForPageToLoad("30000");
        // click the search button
        selenium.click("//input[@name='methodToCall.search' and @value='search']");
        selenium.waitForPageToLoad("30000");
        // click the return value
        selenium.click("link=return value");
        selenium.waitForPageToLoad("30000");
        // select from the Action Request ACKNOWLEDGE
        selenium.select("//select[@id='document.newMaintainableObject.add.personResponsibilities.actionRequestedCd']", "label=ACKNOWLEDGE");
        // type in the Priority field the text 1
        selenium.type("//input[@id='document.newMaintainableObject.add.personResponsibilities.priority']", "1");
        // click the add button
        selenium.click("methodToCall.addLine.personResponsibilities.(!!org.kuali.rice.kew.rule.PersonRuleResponsibility!!)");
        selenium.waitForPageToLoad("30000");
        ITUtil.checkForIncidentReport(selenium, "methodToCall.blanketApprove", "KULRICE-8142 Work Flow Route Rule Blanket Approval Incident Report NPE in MaintenanceDocumentRuleBase.processAddCollectionLineBusinessRules");
        selenium.click("methodToCall.blanketApprove");
        selenium.waitForPageToLoad("30000");
        selenium.selectWindow("null");
        Thread.sleep(2000);
        selenium.click("//img[@alt='doc search']");
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.selectFrame("iframeportlet");
        selenium.click("//input[@name='methodToCall.search' and @value='search']");
        selenium.waitForPageToLoad("30000");
       
        docId= "link=" + docId;
        assertTrue(selenium.isElementPresent(docId));       
        if(selenium.isElementPresent(docId)){            
            assertEquals("FINAL", selenium.getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        }else{
            assertEquals(docId, selenium.getText("//table[@id='row']/tbody/tr[1]/td[1]"));            
            assertEquals("FINAL", selenium.getText("//table[@id='row']/tbody/tr[1]/td[4]"));
        }
    }
}
