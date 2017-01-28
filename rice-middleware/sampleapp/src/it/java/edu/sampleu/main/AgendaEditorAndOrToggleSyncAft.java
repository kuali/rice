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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtils;
import org.openqa.selenium.By;
import static edu.sampleu.main.PeopleFlowCreateNewAftBase.PeopleFlowDocInfo;
/**
 * test that checks that rules can be added to agendas
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AgendaEditorAndOrToggleSyncAft extends WebDriverLegacyITBase {
    public static final String PEOPLE_FLOW_LINK_TEXT = "People Flow";
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

    public static final String ID_TEXT = "T1001";

    private String getPeopleFlowId() throws Exception {
        selectParentWindow();
        waitAndClickMainMenu();
        waitAndClickByLinkText(PEOPLE_FLOW_LINK_TEXT);
        waitForPageToLoad();

        PeopleFlowDocInfo docInfo = peopleFlowCreateNew();
        jGrowl("people flow id is created");

        selectParentWindow();
        waitAndClickMainMenu();
        waitAndClickByLinkText(PEOPLE_FLOW_LINK_TEXT);
        waitForPageToLoad();
        jGrowl("ready to search people flow");

        selectFrameIframePortlet();
        selectByName("lookupCriteria[namespaceCode]", "KUALI - Kuali Systems");
        waitClearAndType("lookupCriteria[name]", docInfo.getName());
        waitAndClickByXpath("//button[contains(text(),'Search')]");
        waitForTextPresent("edit");
        jGrowl("search people flow search complete");

        String id = getTextByXpath("(//span[contains(@id,'line0_control')])[1]");
        assertTrue(id != null);
        jGrowl("people flow id is " + id);

        selectParentWindow();
        waitAndClickMainMenu();
        waitAndClickByLinkText(AGENDA_LOOKUP_LINK_TEXT);
        waitForPageToLoad();

        return id;
    }

    private PeopleFlowDocInfo peopleFlowCreateNew() throws Exception {
        selectFrameIframePortlet();

        waitAndClickByLinkText("Create New");
        String peopleFlowNamespace = "KUALI - Kuali Systems";
        String peopleFlowName = "Document Name" + AutomatedFunctionalTestUtils.createUniqueDtsPlusTwoRandomChars();

        //Save docId
        waitForElementPresentByXpath("//div[@data-label='Document Number']/span");
        String docId = getTextByXpath("//div[@data-label='Document Number']/span");
        assertTrue(docId != null);
        jGrowlSticky("Doc Id is " + docId);

        clearTextByName("document.documentHeader.documentDescription");
        waitAndTypeByName("document.documentHeader.documentDescription", "Description for Document");
        waitAndSelectByName("document.newMaintainableObject.dataObject.namespaceCode", peopleFlowNamespace);
        clearTextByName("document.newMaintainableObject.dataObject.name");
        waitAndTypeByName("document.newMaintainableObject.dataObject.name", peopleFlowName);

        jGrowl("Add Member kr");
        clearTextByName("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName");
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName", "kr");
        findElement(By.name("newCollectionLines['document.newMaintainableObject.dataObject.members'].forceAction")).click();
        waitAndClick(By.cssSelector("button[data-loadingmessage='Adding Line...']"));
        Thread.sleep(3000);
        checkForIncidentReport();

        jGrowl("Add Member admin");
        clearTextByName("newCollectionLines['document.newMaintainableObject.dataObject.members'].priority");
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.members'].priority", "2");
        clearTextByName("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName");
        waitAndTypeByName("newCollectionLines['document.newMaintainableObject.dataObject.members'].memberName", "admin");
        waitAndClick(By.cssSelector("button[data-loadingmessage='Adding Line...']"));
        Thread.sleep(3000);

        waitAndClickButtonByExactText("Save");
        waitForTextPresent("SAVED", WebDriverUtils.configuredImplicityWait() * 4);
        waitForTextPresent(" Document was successfully saved.");

        submit();

        return new PeopleFlowDocInfo(docId, peopleFlowNamespace, peopleFlowName);
    }

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickByLinkText(AGENDA_LOOKUP_LINK_TEXT);
        waitForPageToLoad();
    }

    protected void testAgendaEditorAndOrToggleSync() throws Exception {
        selectFrameIframePortlet();
        waitClearAndType("lookupCriteria[name]", "SimpleAgendaCompoundProp");
        waitAndClickByXpath("//button[contains(text(),'Search')]");
        waitAndClickByXpath("//a[contains(text(),'edit')]");
        selectFrameIframePortlet();
        waitAndClickByXpath("//a/div[contains(text(),'CmpdTestRule: For testing compound props')]");
        waitAndClickByXpath("//button[contains(text(),'Edit Rule')]");
        selectFrameIframePortlet();
        waitForTextPresent("( Campus Code = Muir AND Campus Code = Revelle AND Campus Code = Warren )");
        selectByName("document.newMaintainableObject.dataObject.agendaItemLine.rule.propositionTree.rootElement.children[0].children[1].data.proposition.compoundOpCode","OR");
        selectByName("document.newMaintainableObject.dataObject.agendaItemLine.rule.propositionTree.rootElement.children[0].children[3].data.proposition.compoundOpCode","OR");
        waitForTextNotPresent("( Campus Code = Muir AND Campus Code = Revelle AND Campus Code = Warren )");
        waitForTextPresent("( Campus Code = Muir OR Campus Code = Revelle OR Campus Code = Warren )");
    }

    protected void testAgendaEditorEditRuleAddActionsBlank() throws Exception {
        //Save & Submit
        testFillRequiredDetails("", null);
        save();
        submit();

        //Blanket Approve
        selectParentWindow();
        waitAndClickMainMenu();
        navigate();
        testFillRequiredDetails("",null);
        waitForTextPresent("INITIATED");
        waitAndClickButtonByExactText("Blanket Approve");
        waitAndClickConfirmBlanketApproveOk();
    }

    protected void save() throws InterruptedException {
        waitForTextPresent("INITIATED");
        waitAndClickButtonByExactText("Save");
        waitForProgressSaving();
        waitForTextPresent("SAVED");
        waitForTextPresent(" Document was successfully saved.");
    }

    protected void testAgendaEditorEditRuleAddActionsKrmsActionResolverType() throws Exception {
        //Save & Submit
        selectParentWindow();
        waitAndClickMainMenu();
        navigate();
        testFillRequiredDetails("KrmsActionResolverType", null);
        save();
        submit();

        //Blanket Approve
        selectParentWindow();
        waitAndClickMainMenu();
        navigate();
        testFillRequiredDetails("KrmsActionResolverType",null);
        waitForTextPresent("INITIATED");
        waitAndClickButtonByExactText("Blanket Approve");
        waitAndClickConfirmBlanketApproveOk();
    }

    protected void submit() throws InterruptedException {
        waitAndClickButtonByExactText("Submit");
        waitAndClickConfirmSubmitOk();
        waitForTextPresent("ENROUTE");
        waitForTextPresent("Document was successfully submitted.");
    }

    protected void testAgendaEditorEditRuleAddActionsNotifyPeopleFlow(String peopleFlowId) throws Exception {
        //Save & Submit
        selectParentWindow();
        waitAndClickMainMenu();
        navigate();
        testFillRequiredDetails("Notify PeopleFlow", peopleFlowId);
        save();
        submit();

        //Blanket Approve
        selectParentWindow();
        waitAndClickMainMenu();
        navigate();
        testFillRequiredDetails("Notify PeopleFlow",peopleFlowId);
        waitForTextPresent("INITIATED");
        waitAndClickButtonByExactText("Blanket Approve");
        waitAndClickConfirmBlanketApproveOk();
    }

    protected void testAgendaEditorEditRuleAddActionsRouteToPeopleFlow(String peopleFlowId) throws Exception {
        //Save & Submit
        selectParentWindow();
        waitAndClickMainMenu();
        navigate();
        testFillRequiredDetails("Route to PeopleFlow", peopleFlowId);
        save();
        submit();

        //Blanket Approve
        selectParentWindow();
        waitAndClickMainMenu();
        navigate();
        testFillRequiredDetails("Route to PeopleFlow",peopleFlowId);
        waitForTextPresent("INITIATED");
        waitAndClickButtonByExactText("Blanket Approve");
        waitAndClickConfirmBlanketApproveOk();
    }

    protected void testAgendaEditorEditRuleAddActionsValidationAction() throws Exception {
        //Save & Submit
        selectParentWindow();
        waitAndClickMainMenu();
        navigate();
        testFillRequiredDetails("Validation Action",null);
        save();
        submit();

        //Blanket Approve
        selectParentWindow();
        waitAndClickMainMenu();
        navigate();
        testFillRequiredDetails("Validation Action",null);
        waitForTextPresent("INITIATED");
        waitAndClickButtonByExactText("Blanket Approve");
        waitAndClickConfirmBlanketApproveOk();
    }

    private void testFillRequiredDetails(String selectValue,String peopleFlowId) throws Exception {
        selectFrameIframePortlet();
        waitClearAndType("lookupCriteria[id]", ID_TEXT);
        waitAndClickByXpath("//button[contains(text(),'Search')]");
        waitAndClickByXpath("//a[contains(text(),'edit')]");
        selectFrameIframePortlet();
        waitAndClickByXpath("//a[@class='agendaNode ruleNode']");
        waitAndClickByXpath("//button[contains(text(),'Edit Rule')]");
        selectFrameIframePortlet();
        selectByName("document.newMaintainableObject.dataObject.agendaItemLineRuleAction.typeId", selectValue);

        if (selectValue.equals("KrmsActionResolverType") || selectValue.equalsIgnoreCase("Validation Action")){
            waitClearAndType("document.newMaintainableObject.dataObject.agendaItemLineRuleAction.name", "ActionName");
        }

        waitForElementPresentByXpath("document.newMaintainableObject.dataObject.customRuleActionAttributesMap[peopleFlowId]");

        if(selectValue.equalsIgnoreCase("Notify PeopleFlow") || selectValue.equalsIgnoreCase("Route to PeopleFlow")){
            waitClearAndType("document.newMaintainableObject.dataObject.customRuleActionAttributesMap[peopleFlowId]", peopleFlowId);
            waitClearAndType("document.newMaintainableObject.dataObject.customRuleActionAttributesMap[peopleFlowName]", "Document Name1412916151718sz");
            waitClearAndType("document.newMaintainableObject.dataObject.agendaItemLineRuleAction.name", "test");
        }else if(selectValue.equalsIgnoreCase("Validation Action")) {
            waitAndClickByName("document.newMaintainableObject.dataObject.customRuleActionAttributesMap[actionTypeCode]");
            waitClearAndType("document.newMaintainableObject.dataObject.customRuleActionAttributesMap[actionMessage]","This is msg");
        }
        waitAndClickButtonByExactText("Update Rule");
        acceptAlertIfPresent();
    }

    protected void waitClearAndType(String name, String value) throws Exception {
        clearTextByName(name);
        waitAndTypeByName(name,value);
    }

    @Test
    public void testAgendaEditorAndOrToggleSyncBookmark() throws Exception {
        testAgendaEditorAndOrToggleSync();
        passed();
    }

    @Test
    public void testAgendaEditorAndOrToggleSyncNav() throws Exception {
        testAgendaEditorAndOrToggleSync();
        passed();
    }

    @Test
    public void testAgendaEditorAddActionsBookmark() throws Exception {
        String peopleFlowId = getPeopleFlowId();
        testAgendaEditorEditRuleAddActionsBlank();
        testAgendaEditorEditRuleAddActionsKrmsActionResolverType();
        testAgendaEditorEditRuleAddActionsNotifyPeopleFlow(peopleFlowId);
        testAgendaEditorEditRuleAddActionsRouteToPeopleFlow(peopleFlowId);
        testAgendaEditorEditRuleAddActionsValidationAction();
        passed();
    }

    @Test
    public void testAgendaEditorAddActionsNav() throws Exception {
        String peopleFlowId = getPeopleFlowId();
        testAgendaEditorEditRuleAddActionsBlank();
        testAgendaEditorEditRuleAddActionsKrmsActionResolverType();
        testAgendaEditorEditRuleAddActionsNotifyPeopleFlow(peopleFlowId);
        testAgendaEditorEditRuleAddActionsRouteToPeopleFlow(peopleFlowId);
        testAgendaEditorEditRuleAddActionsValidationAction();
        passed();
    }
}

