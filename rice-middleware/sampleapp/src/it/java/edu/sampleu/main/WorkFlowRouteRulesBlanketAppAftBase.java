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
public class WorkFlowRouteRulesBlanketAppAftBase extends MainTmplMthdSTNavBase{

    /**
     * ITUtil.PORTAL + "?channelTitle=Routing%20Rules&channelUrl=" + WebDriverUtils.getBaseUrlString() +
     * "/kr/lookup.do?businessObjectClassName=org.kuali.rice.kew.rule.RuleBaseValues&docFormKey=88888888&returnLocation=" +
     * ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK+ "&showMaintenanceLinks=true";
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Routing%20Rules&channelUrl=" + WebDriverUtils
            .getBaseUrlString() +
            "/kr/lookup.do?businessObjectClassName=org.kuali.rice.kew.rule.RuleBaseValues&docFormKey=88888888&returnLocation=" +
            AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK+ "&showMaintenanceLinks=true";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Routing Rules
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Routing Rules";
    }

    protected void testWorkFlowRouteRulesBlanketApp() throws Exception {
        String random = AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomCharsNot9Digits();
        waitForPageToLoad();
        Thread.sleep(3000);
        assertEquals("Kuali Portal Index", getTitle());
        selectFrameIframePortlet();

        // click on the create new button
        waitAndClickCreateNew();

        // lookup on the Document Type Name
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kew.doctype.bo.DocumentType!!).(((name:documentTypeName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");

        // type in the name field the text RoutingRuleDocument
        waitAndTypeByName("name", "RoutingRuleDocument");

        // click the search button
        waitAndClickSearch();

        // click the return value link
        waitAndClickReturnValue();

        // lookup on the Rule Template Name
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kew.rule.bo.RuleTemplateBo!!).(((name:ruleTemplateName))).((``)).((<>)).(([])).((**)).((^^)).((&&)).((//)).((~~)).(::::;;::::).anchor");

        // type in the name field the text RuleRoutingTemplate
        waitAndTypeByName("name", "RuleRoutingTemplate");

        // click the search button
        waitAndClickSearch();

        // click the return value link
        waitAndClickReturnValue("testWorkFlowRouteRulesBlanketApp");

        // click the create new button
        waitAndClickByName("methodToCall.createRule");
        waitForPageToLoad();
        String docId = waitForDocId();
        assertTrue(isElementPresentByName(CANCEL_NAME));

        // type in the Document Overview Description the text Test Routing Rule
        waitAndTypeByXpath(DOC_DESCRIPTION_XPATH, "Test Routing Rule " + random);

        // click the Force Action checkbox
        waitAndClickByXpath("//input[@id='document.newMaintainableObject.forceAction']");

        // type in the Description text area the text Test Routing Rule1
        waitAndTypeByXpath("//textarea[@id='document.newMaintainableObject.description']", "Test Routing Rule1 "
                + random);

        // type in the Document type name field the text DocumentTypeDocument
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.fieldValues(1321~docTypeFullName)']",
                "DocumentTypeDocument");

        // lookup on Person
        waitAndClickByName("methodToCall.performLookup.(!!org.kuali.rice.kim.impl.identity.PersonImpl!!).(((principalName:document.newMaintainableObject.add.personResponsibilities.principalName,))).((`document.newMaintainableObject.add.personResponsibilities.principalName:principalName,`)).((<>)).(([])).((**)).((^^)).((&&)).((/personImpl/)).((~~)).(::::;"
                + getBaseUrlString() + "/kr/lookup.do;::::).anchor15");

        // click the search button
        waitAndClickSearch();

        // click the return value
        waitAndClickReturnValue();

        // select from the Action Request ACKNOWLEDGE
        selectByXpath("//select[@id='document.newMaintainableObject.add.personResponsibilities.actionRequestedCd']",
                "ACKNOWLEDGE");

        // type in the Priority field the text 1
        waitAndTypeByXpath("//input[@id='document.newMaintainableObject.add.personResponsibilities.priority']", "1");

        // click the add button
        waitAndClickByName("methodToCall.addLine.personResponsibilities.(!!org.kuali.rice.kew.rule.PersonRuleResponsibility!!).(:::;15;:::).anchor15");
        waitForPageToLoad();

        // click Blanket Approve
        waitAndClickByName(BLANKET_APPROVE_NAME);

        // doc search for the docId
        waitForPageToLoad();
        driver.switchTo().defaultContent(); //selectWindow("null");
        waitAndClickDocSearch();
        waitForPageToLoad();
        assertEquals("Kuali Portal Index", getTitle());
        selectFrameIframePortlet();
        waitAndTypeByName("documentId", docId);
        waitAndClickSearch();

        // Expect the doc status to be FINAL
        waitForElementPresent(By.linkText(docId));
        if (isElementPresent(By.linkText(docId))) {
            if (!DOC_STATUS_FINAL.equalsIgnoreCase(getTextByXpath(DOC_STATUS_XPATH_2))) {
                jiraAwareFail("WorkFlowRouteRulesBlanketApp expected:<[FINAL]> but was " + getTextByXpath(DOC_STATUS_XPATH_2));
            }
        } else {
            assertEquals(docId, getTextByXpath(DOC_ID_XPATH_2));
            assertEquals(DOC_STATUS_FINAL, getTextByXpath(DOC_STATUS_XPATH_2));
        }
    }

    public void testWorkFlowRouteRulesBlanketAppBookmark(JiraAwareFailable failable) throws Exception {
        testWorkFlowRouteRulesBlanketApp();
        passed();
    }
    public void testWorkFlowRouteRulesBlanketAppNav(JiraAwareFailable failable) throws Exception {
        testWorkFlowRouteRulesBlanketApp();
        passed();
    }
}
