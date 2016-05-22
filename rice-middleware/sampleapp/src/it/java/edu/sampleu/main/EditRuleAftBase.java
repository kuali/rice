/**
 * Copyright 2005-2016 The Kuali Foundation
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

import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class EditRuleAftBase extends MainTmplMthdSTNavBase{

    /**
     * ITUtil.PORTAL + "?channelTitle=Create%20New%20Agenda&channelUrl=" + WebDriverUtils.getBaseUrlString() +
     *  "/kr-krad/krmsAgendaEditor?methodToCall=start&dataObjectClassName=org.kuali.rice.krms.impl.ui.AgendaEditor&returnLocation=" +
     *  ITUtil.PORTAL_URL + ITUtil.HIDE_RETURN_LINK;
     */
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL + "?channelTitle=Create%20New%20Agenda&channelUrl=" + WebDriverUtils
            .getBaseUrlString() +"/kr-krad/krmsAgendaEditor?methodToCall=start&dataObjectClassName=org.kuali.rice.krms.impl.ui.AgendaEditor&returnLocation=" +
            AutomatedFunctionalTestUtils.PORTAL_URL + AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Create New Agenda
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Create New Agenda";
    }

    protected void testEditRuleSaveSubmit() throws Exception {
        selectFrameIframePortlet();
        String desc = getDescriptionUnique();
        String docId = waitForAgendaDocId();
        waitAndSelectLabeled("Namespace:", "Kuali Rules Test");
        waitAndTypeLabeledInput("Name:", desc);
        waitAndTypeLabeledInput("Context:", "Context1");
        addAndEditRule();
        waitAndClickButtonByExactText("Add Rule");
        saveSuccessfully();
        submitSuccessfully();
//        waitAndClickButtonByExactText("Save");
//        waitAndClickButtonByExactText("Submit");
//        waitAndClickConfirmSubmitOk();
    }

    protected void testEditRuleBlanketApprove() throws Exception {
        selectFrameIframePortlet();
        String desc = getDescriptionUnique();
        String docId = waitForAgendaDocId();
        waitAndSelectLabeled("Namespace:", "Kuali Rules Test");
        waitAndTypeLabeledInput("Name:", desc);
        waitAndTypeLabeledInput("Context:", "Context1");
        addAndEditRule();
        waitAndClickButtonByExactText("Add Rule");
        waitAndClickButtonByExactText("Blanket Approve");
        waitAndClickConfirmBlanketApproveOk();
    }

    private void addAndEditRule() throws Exception {
        //Adding Rules so we do not need to rely on the existing data.
        waitAndClickButtonByExactText("Add Rule");
        String desc = getDescriptionUnique();
        waitAndTypeByName("document.newMaintainableObject.dataObject.agendaItemLine.rule.name", "Rule Name " + desc);
        waitAndClickButtonByExactText("Add");
        waitAndTypeByName(
                "document.newMaintainableObject.dataObject.agendaItemLine.rule.propositionTree.rootElement.children[0].data.proposition.description",
                "Rule 1");
        selectByName(
                "document.newMaintainableObject.dataObject.agendaItemLine.rule.propositionTree.rootElement.children[0].data.proposition.parameters[0].value",
                "Account");
        waitAndTypeByName(
                "document.newMaintainableObject.dataObject.agendaItemLine.rule.propositionTree.rootElement.children[0].data.proposition.parameters[1].value",
                "12345");
        selectByName(
                "document.newMaintainableObject.dataObject.agendaItemLine.rule.propositionTree.rootElement.children[0].data.proposition.parameters[2].value",
                "=");
        waitAndClickByXpath("//input[@type='image' and @alt='Add Parent']");
        waitAndClickButtonByExactText("Add");
        waitForElementPresentByXpath(
                "document.newMaintainableObject.dataObject.agendaItemLine.rule.propositionTree.rootElement.children[0].children[2].data.proposition.description");
        waitAndTypeByName(
                "document.newMaintainableObject.dataObject.agendaItemLine.rule.propositionTree.rootElement.children[0].children[2].data.proposition.description",
                "Rule 2");
        selectByName(
                "document.newMaintainableObject.dataObject.agendaItemLine.rule.propositionTree.rootElement.children[0].children[2].data.proposition.parameters[0].value",
                "Account");
        selectByName(
                "document.newMaintainableObject.dataObject.agendaItemLine.rule.propositionTree.rootElement.children[0].children[2].data.proposition.parameters[2].value",
                "=");
        waitAndTypeByName(
                "document.newMaintainableObject.dataObject.agendaItemLine.rule.propositionTree.rootElement.children[0].children[2].data.proposition.parameters[1].value",
                "12345");
        waitAndClickByXpath("//div[4]/fieldset/div/input[@type='image' and @alt='Edit']");
        waitForTextPresent("Rule 2");

        //Moving Rules Up, Down, Right and Left & Add Parent
        waitAndClickButtonByExactText("Up");
        waitAndClickButtonByExactText("Down");
        waitAndClickButtonByExactText("Add Parent");
        waitAndClickButtonByExactText("Left");
        waitAndClickButtonByExactText("Up");
        waitAndClickButtonByExactText("Right");

        //Checking Cut & Paste with Refresh
        waitAndClickButtonByExactText("Cut");
        waitAndClickByXpath("//a[@class='ruleTreeNode compoundNode']");
        waitAndClickButtonByExactText("Paste");
        waitAndClickButtonByExactText("refresh");
    }

}
