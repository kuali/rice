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

import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 *  @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ConfigComponentActionListAftBase extends ConfigComponentAftBase {

    /**
     * ITUtil.PORTAL+"?channelTitle=Component&channelUrl="+WebDriverUtils.getBaseUrlString()+
     * "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.component.ComponentBo&docFormKey=88888888&returnLocation="+
     * +ITUtil.PORTAL_URL+ ITUtil.HIDE_RETURN_LINK;
     */    
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Component&channelUrl="+ WebDriverUtils
            .getBaseUrlString()+"/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.component.ComponentBo&docFormKey=88888888&returnLocation="+
            AutomatedFunctionalTestUtils.PORTAL_URL+ AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    private void assertActionListRequestGroup(String userInGroup, String group, String namespace, String actionCode, String beforeState, String afterState) throws InterruptedException {
        namespaceCode = namespace;
        String docId = testCreateActionRequestGroup(group, namespace, actionCode);
        impersonateUser(userInGroup);
        assertActionList(docId, actionCode, beforeState);
        assertOutbox(docId, afterState);
        selectTopFrame();
    }

    private void assertActionListRequestPerson(String user, String actionType, String beforeState, String afterState) throws Exception {
        String[][] adhocRequests = new String [][]{{user, actionType}};
        assertActionListRequestPerson(adhocRequests, beforeState, afterState);
    }

    private void assertActionListRequestPerson(String[][] adhocRequests, String beforeState, String afterState) throws Exception {
        String docId = testCreateActionRequestPerson(adhocRequests);
        impersonateUser(adhocRequests[0][0]);
        assertActionList(docId, adhocRequests[0][1], beforeState);
        assertOutbox(docId, afterState);
        selectTopFrame();
    }

    protected String testCreateActionRequestGroup(String user, String namespace, String actionType) throws InterruptedException{
        String docId = testCreateNew();
        addAdHocRecipientsGroup(new String[]{user, actionType, namespace});
        submitAndClose();
        return docId;
    }

    /**
     * Creates an Action Request in a users action list.
     *
     * Initiates a new maintenance document (Component BO) with added adHoc request to create an action request
     * in a users action list for the document
     * @param userActions 2d array containing username, action pairs. (ex: "fred","A")
     *
     * @return documentID of the newly initiated document to which the created action request applies.
     */
    protected String testCreateActionRequestPerson(String[][] userActions) throws InterruptedException {
        String docId = testCreateNew();
        if (!userActions[0][0].isEmpty()){
            addAdHocRecipientsPerson(userActions);
        }
        submitAndClose();
        return docId;
    }

    public void testActionListAcknowledgeGroup() throws Exception {
        assertActionListRequestGroup("fran", "RecipeMasters", "KR-WKFLW", "K", "PROCESSED", "FINAL");
        passed();
    }

    /**
     * tests the Acknowledge ActionRequest.
     * Creates an Acknowledge request for a user. Then performs the Acknowledge action.
     * @throws Exception
     */
    public void testActionListAcknowledgePerson() throws Exception {
        assertActionListRequestPerson("erin", "K", "PROCESSED", "FINAL");
        passed();
    }

    /**
     * tests the Acknowledge ActionRequest.
     * Creates an Acknowledge request for a user and an approve request for a different user.
     * Then performs the Acknowledge action.
     * @throws Exception
     */
    public void testActionListAcknowledgePerson_WithPendingApprove() throws Exception {
        String[][] adhocRequests = new String [][]{{"fred","A"},{"fran","K"}};
        String docId = testCreateActionRequestPerson(adhocRequests);
        impersonateUser("fran");
        assertActionList(docId, "K", "ENROUTE");
        assertOutbox(docId, "ENROUTE");
        selectTopFrame();
        passed();
    }

    /**
     * tests the Acknowledge ActionRequest.
     * Creates an Acknowledge request for a user and an approve request for a different user.
     * Then performs the Acknowledge action.
     * @throws Exception
     */
    public void testActionListAcknowledgePerson_WithPendingAcknowledge() throws Exception {
        String[][] adhocRequests = new String [][]{{"fred","K"},{"fran","K"}};
        String docId = testCreateActionRequestPerson(adhocRequests);
        impersonateUser("fran");
        assertActionList(docId, "K", "PROCESSED");
        assertOutbox(docId, "PROCESSED");
        selectTopFrame();
        passed();
    }

    /**
     * tests the Approve ActionRequest.
     * Creates an approve request for a user. Then performs the Approve action.
     * @throws Exception
     */
    public void testActionListApprovePerson() throws Exception {
        assertActionListRequestPerson("fred", "A", "ENROUTE", "FINAL");
        passed();
    }

    /**
     * tests the Approve ActionRequest.
     * Creates an Approve request for a user and a separate approve request for a different user.
     * Then performs the first users Approve action.
     * @throws Exception
     */
    public void testActionListApprovePerson_WithPendingApprove() throws Exception {
        String[][] adhocRequests = new String [][]{{"fred","A"},{"fran","A"}};
        assertActionListRequestPerson(adhocRequests, "ENROUTE", "ENROUTE");
        passed();
    }

    /**
     * tests the Approve ActionRequest.
     * Creates an Approve request for a user and a separate approve request for a different user.
     * Then performs the first users Approve action.
     * @throws Exception
     */
    public void testActionListApprovePerson_WithPendingAcknowledge() throws Exception {
        String[][] adhocRequests = new String [][]{{"fran","A"},{"fred","K"}};
        assertActionListRequestPerson(adhocRequests, "ENROUTE", "PROCESSED");
        passed();
    }

    public void testActionListApproveGroup() throws Exception {
        assertActionListRequestGroup("fred", "RecipeMasters", "KR-WKFLW", "A", "ENROUTE", "FINAL");
        passed();
    }

    public void testActionListCompleteGroup() throws Exception {
        assertActionListRequestGroup("dev1", "Kuali Developers", "KUALI", "C", "ENROUTE", "FINAL");
        passed();
    }

    /**
     * tests the complete ActionRequest.
     * Creates an complete request for a user. Then performs the Complete action.
     * @throws Exception
     */
    public void testActionListCompletePerson() throws Exception {
        assertActionListRequestPerson("fran", "C", "ENROUTE", "FINAL");
        passed();
    }

    public void testActionListCompletePerson_WithPendingAcknowledge() throws Exception {
        String[][] adhocRequests = new String [][]{{"fran","C"},{"fred","K"}};
        assertActionListRequestPerson(adhocRequests, "ENROUTE", "PROCESSED");
        passed();
    }

    public void testActionListDisapproveGroup() throws Exception {
        assertActionListRequestGroup("director", "ChickenRecipeMasters", "KR-WKFLW", "D", "ENROUTE", "DISAPPROVED");
        passed();
    }

    /**
     * tests the  ActionRequest.
     * Creates an approve request for a user. Then performs the Disapprove action.
     * @throws Exception
     */
    public void testActionListDisapprovePerson() throws Exception {
        assertActionListRequestPerson("fred", "D", "ENROUTE", "DISAPPROVED");
        passed();
    }

    public void testActionListFyiGroup() throws Exception {
        assertActionListRequestGroup("dev2", "Kuali Developers", "KUALI", "F", "FINAL", "FINAL");
        passed();
    }

    /**
     * tests the FYI ActionRequest.
     * Creates an FYI request for a user. Then performs the FYI action.
     * @throws Exception
     */
    public void testActionListFyiPerson() throws Exception {
        assertActionListRequestPerson("eric", "F", "FINAL", "FINAL");
        passed();
    }



    public void testActionListCancelPerson() throws Exception {
        assertActionListRequestGroup("dev2", "Kuali Developers", "KUALI", "F", "FINAL", "FINAL");
        passed();
    }

    public void testComponentRecallAndCancel_WithPendingPersonApprove() throws Exception {
        String user = "erin";
        String docId = testCreateNew();
        addAdHocRecipientsPerson(new String[]{user, "A"});
        submit();
        recall(true);
        impersonateUser(user);
        assertNotInActionList(docId);
        passed();
    }

    public void testComponentRecallToActionList_WithPendingPersonApprove() throws Exception {
        String user = "erin";

        String docId = testCreateNew();
        addAdHocRecipientsPerson(new String[]{user, "A"});
        submit();
        waitForTextPresent("ENROUTE");
        recall(false);
        // TODO: new window vs. new tab issue
        assertActionList(docId, "CR", "SAVED");

        driver.navigate().to(WebDriverUtils.getBaseUrlString() + BOOKMARK_URL);
        waitAndClickDocSearch();
        selectFrameIframePortlet();
        waitAndTypeByName("documentId",docId);
        waitAndClickByXpath(SEARCH_XPATH);
        waitForTextPresent("FINAL");
        passed();
    }

    public void testComponentCancel_WithPendingPersonApprove()throws Exception {
        String docId = testCreateNew();
        addAdHocRecipientsPerson(new String[]{"fred", "A"});
        waitAndClickByName("methodToCall.cancel");
        assertDocSearchNoResults(docId);
        passed();
    }

    public void testComponentSave_WithPendingPersonApprove() throws Exception {
        String user = "erin";
        String docId = testCreateNew();
        addAdHocRecipientsPerson(new String[]{user, "A"});
        saveAndReload();
        waitForTextPresent("SAVED");
        passed();
    }

    public void assertComponentSubmit_WithPersonRequest(String user, String action, String state) throws Exception {
        String[][] userActions = new String [][]{{user, action}};
        String docId = testCreateNew();
        if (!userActions[0][0].isEmpty()){
            addAdHocRecipientsPerson(userActions);
        }
        submit();
        waitForTextPresent("ENROUTE");
        waitAndClickByName("methodToCall.reload");
        waitForTextPresent(state);
        close();
    }

    public void testComponentSubmit() throws Exception {
        assertComponentSubmit_WithPersonRequest("", "", "FINAL");
        passed();
    }

    public void testComponentSubmit_WithPendingPersonApprove() throws Exception {
        assertComponentSubmit_WithPersonRequest("erin", "A", "ENROUTE");
        passed();
    }

    public void testComponentSubmit_WithPendingPersonAcknowledge() throws Exception {
        assertComponentSubmit_WithPersonRequest("erin", "K", "PROCESSED");
        passed();
    }

    public void testComponentSubmit_WithPendingPersonFyi() throws Exception {
        assertComponentSubmit_WithPersonRequest("erin", "F", "FINAL");
        passed();
    }


}
