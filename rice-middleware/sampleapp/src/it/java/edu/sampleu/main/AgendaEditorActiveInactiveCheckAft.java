/**
 * Copyright 2005-2017 The Kuali Foundation
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

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * test that checks that rules can be added to agendas
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AgendaEditorActiveInactiveCheckAft extends WebDriverLegacyITBase {
    public static final String BOOKMARK_URL =
            AutomatedFunctionalTestUtils.PORTAL
                    + "?channelTitle=Agenda%20Lookup&channelUrl="
                    + WebDriverUtils.getBaseUrlString()
                    + AutomatedFunctionalTestUtils.KRAD_LOOKUP_METHOD
                    + "org.kuali.rice.krms.impl.repository.AgendaBo"
                    + AutomatedFunctionalTestUtils.SHOW_MAINTENANCE_LINKS
                    + "&returnLocation="
                    + AutomatedFunctionalTestUtils.PORTAL_URL
                    + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickByLinkText(AGENDA_LOOKUP_LINK_TEXT);
        waitForPageToLoad();
    }

    protected void testAgendaEditorActiveInactiveCheck() throws Exception {
        String uniqueText = RandomStringUtils.randomAlphabetic(9).toUpperCase();
        String uniqueRuleText = RandomStringUtils.randomAlphabetic(9).toUpperCase();

        //Add Inactive & Verify
        selectFrameIframePortlet();
        waitAndClickByXpath("//a[contains(text(),'Create New')]");
        selectFrameIframePortlet();
        selectByName("document.newMaintainableObject.dataObject.namespace","Kuali Rules Test");
        waitAndTypeByName("document.newMaintainableObject.dataObject.agenda.name",uniqueText+"Inactive");
        waitAndTypeByName("document.newMaintainableObject.dataObject.contextName","Context1");
        waitAndClickByName("document.newMaintainableObject.dataObject.agenda.active");
        copyRuleFromExisting(uniqueText);
        selectFrameIframePortlet();
        waitAndClickByXpath("//button[contains(text(),'Submit')]");
        waitAndClickConfirmSubmitOk();
        waitForTextPresent("Document was successfully submitted.");
        selectParentWindow();
        waitAndClickMainMenu();
        selectFrameIframePortlet();
        navigate();
        selectFrameIframePortlet();
        waitAndTypeByName("lookupCriteria[name]",uniqueText+"Inactive");
        waitAndClickByXpath("//input[@name='lookupCriteria[active]' and @value='N']");
        waitAndClickButtonByExactText("Search");
        waitForTextPresent(uniqueText+"Inactive");

        // Add Active & Verify
        waitAndClickByXpath("//a[contains(text(),'Create New')]");
        selectFrameIframePortlet();
        selectByName("document.newMaintainableObject.dataObject.namespace","Kuali Rules Test");
        waitAndTypeByName("document.newMaintainableObject.dataObject.agenda.name",uniqueText+"Active");
        waitAndTypeByName("document.newMaintainableObject.dataObject.contextName","Context1");
        copyRuleFromExisting(uniqueRuleText);
        selectFrameIframePortlet();
        waitAndClickByXpath("//button[contains(text(),'Submit')]");
        waitAndClickConfirmSubmitOk();
        waitForTextPresent("Document was successfully submitted.");
        selectParentWindow();
        waitAndClickMainMenu();
        selectFrameIframePortlet();
        navigate();
        selectFrameIframePortlet();
        waitAndTypeByName("lookupCriteria[name]",uniqueText+"Active");
        waitAndClickByXpath("//input[@name='lookupCriteria[active]' and @value='Y']");
        waitAndClickButtonByExactText("Search");
        waitForTextPresent(uniqueText+"Active");

        //Verify Both
        waitAndClickButtonByExactText("Clear Values");
        waitAndTypeByName("lookupCriteria[name]",uniqueText+"Active");
        waitAndClickByXpath("//input[@name='lookupCriteria[active]' and @value='']");
        waitAndClickButtonByExactText("Search");
        waitForTextPresent(uniqueText+"Active");
        waitAndClickButtonByExactText("Clear Values");
        waitAndTypeByName("lookupCriteria[name]",uniqueText+"Inactive");
        waitAndClickByXpath("//input[@name='lookupCriteria[active]' and @value='']");
        waitAndClickButtonByExactText("Search");
        waitForTextPresent(uniqueText+"Inactive");
    }

    protected void copyRuleFromExisting(String ruleName) throws Exception {
        waitAndClickButtonByExactText("Add Rule");
        selectFrameIframePortlet();
        waitAndClickById("copyRuleName_quickfinder_act");
        gotoLightBox();
        waitAndClickButtonByText("Search");
        waitAndClickLinkContainingText("return value");
        selectFrameIframePortlet();
        waitAndClickButtonByExactText("Copy Rule");
        waitAndTypeByName("document.newMaintainableObject.dataObject.agendaItemLine.rule.name",ruleName);
        if(!isElementPresentByXpath("//input[@name='document.newMaintainableObject.dataObject.agendaItemLine.rule.name' and @value='"+ruleName+"']")) {
            waitAndTypeByName("document.newMaintainableObject.dataObject.agendaItemLine.rule.name",ruleName);
        }
        waitAndClickButtonByExactText("Add Rule");
        waitForElementPresentByXpath("//a[@class='ruleTreeNode simplePropositionNode']/p");
    }

    /**
     * test ActiveInactive Check
     */
    @Test
    public void testAgendaEditorActiveInactiveCheckBookmark() throws Exception {
        testAgendaEditorActiveInactiveCheck();
    }

    /**
     * test ActiveInactive Check
     */
    @Test
    public void testAgendaEditorActiveInactiveCheckNav() throws Exception {
        testAgendaEditorActiveInactiveCheck();
    }
}
