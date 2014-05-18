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

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.Keys;

/**
 * test that checks that rules can be added to agendas
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AgendaEditorAddRuleAft extends WebDriverLegacyITBase {

    public static String KRAD_PORTAL_BASE = "/kr-krad/";
    public static String AGENDA_EDITOR_BASE = "krmsAgendaEditor";

    public static String NEW_DATA_OBJ_PATH = "document.newMaintainableObject.dataObject.";

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

    protected void testAgendaEditorAddRuleWithSimpleProposition() throws Exception {
        String uniqId = AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomCharsNot9Digits();
        String agendaName = "AgendaEditorAddRuleAft " + uniqId;
        String ruleName = "AgendaEditorAddRuleAft Rule " + "1" + " " + uniqId;

        selectFrameIframePortlet();
        waitAndClickLinkContainingText("Create New");

        // add agenda information
        String docId = waitForAgendaDocId();
        addNewAgendaInformation(agendaName, "Kuali Rules Test", "Context1");
        // dynamic agenda details
        Thread.sleep(500); // tab is happening to fast
        unfocusElement();
        unfocusElement();
        waitAndTypeByName(NEW_DATA_OBJ_PATH + "agenda.typeId", "Campus Agenda");
        unfocusElement();
        waitAndTypeByName(NEW_DATA_OBJ_PATH + "customAttributesMap[Campus]", "BL");
        waitAndClickButtonByText("Add Rule");

        // add agenda rule information
        waitForPageToLoad();
        addNewAgendaRuleInformation("Validation Rule", "", ruleName);
        waitAndClickButtonByExactText("Add"); // add proposition button
        addRulePropositionInfo("0", "Campus must have students", "", "Bloomington Campus Size", ">", "1");
        addNewRuleActionInformation("KrmsActionResolverType", "test", "test");
        waitAndClickButtonByText("Add Rule");

        // validate rule exists
        waitForTextPresent(ruleName);
        waitAndClickSubmitByText();
        waitAndClickConfirmationOk();
        waitForTextPresent("Document was successfully submitted", WebDriverUtils.configuredImplicityWait() * 2);
        assertDocSearch(docId, "FINAL");
        passed();
    }


    protected void addNewAgendaInformation(String agendaName, String agendaNamespace, String agendaContext) throws Exception {
        waitAndSelectByName(NEW_DATA_OBJ_PATH + "namespace", agendaNamespace);
        waitAndTypeByName(NEW_DATA_OBJ_PATH + "agenda.name", agendaName);
        waitAndTypeByName(NEW_DATA_OBJ_PATH + "contextName", agendaContext);
    }

    protected void addNewAgendaRuleInformation(String ruleType, String ruleTypeCode, String ruleName) throws Exception {
        waitAndSelectByName(NEW_DATA_OBJ_PATH + "agendaItemLine.rule.typeId", ruleType);

        if (StringUtils.isNotBlank(ruleTypeCode)) {
            waitAndSelectByName(NEW_DATA_OBJ_PATH + "customRuleAttributesMap[ruleTypeCode]", ruleTypeCode);
        }

        // validate that Type selected is Validation Rule
        assertTrue("Expected ruleTypeCode dropdown value not found ",isElementPresentByXpath(
                "//option[@selected='selected' and @value='1002']"));

        waitAndTypeByName(NEW_DATA_OBJ_PATH + "agendaItemLine.rule.name", ruleName);
    }

    protected void addRulePropositionInfo(String childIndex, String description, String category, String propTerm,
            String propComparison, String propositionValue) throws Exception {
        String propTreeRootPath = NEW_DATA_OBJ_PATH + "agendaItemLine.rule.propositionTree.rootElement.";
        String propTreeChildPath = propTreeRootPath + "children[" + childIndex + "].";
        String propositionPath = propTreeChildPath + "data.proposition.";

        if (StringUtils.isNoneBlank(description)) {
            waitAndTypeByName(propositionPath + "description", description);
        }

        if (StringUtils.isNoneBlank(category)) {
            waitAndSelectByName(propositionPath + "categoryId", category);
        }

        if (StringUtils.isNoneBlank(propTerm)) {
            waitAndSelectByName(propositionPath + "parameters[0].value", propTerm);
        }

        if (StringUtils.isNoneBlank(propComparison)) {
            waitAndSelectByName(propositionPath + "parameters[2].value", propComparison);
            unfocusElement();
            Thread.sleep(3000); // need time for next input to be reloaded
        }

        if (StringUtils.isNoneBlank(propositionValue)) {
            waitAndTypeByName(propositionPath + "parameters[1].value", propositionValue);
        }
    }

    protected void addNewRuleActionInformation(String actionType, String actionName,
            String actionDescription) throws Exception {
        // select action elements
        waitAndSelectByName(NEW_DATA_OBJ_PATH + "agendaItemLineRuleAction.typeId", actionType);
        waitAndTypeByName(NEW_DATA_OBJ_PATH + "agendaItemLineRuleAction.name", actionName);
        waitAndTypeByName(NEW_DATA_OBJ_PATH + "agendaItemLineRuleAction.description", actionDescription);
    }


    protected void unfocusElement() {
        typeTab();
    }

    /**
     * test that a rule can be added to an agenda
     */
    @Test
    public void testAgendaEditorAddRuleNav() throws Exception {
        testAgendaEditorAddRuleWithSimpleProposition();
    }

}

