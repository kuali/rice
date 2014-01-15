/**
 * Copyright 2005-2013 The Kuali Foundation
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

import org.apache.commons.lang.RandomStringUtils;
import org.kuali.rice.testtools.common.JiraAwareFailable;
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ActionListAftBase extends AdminTmplMthdAftNavBase {

    /**
     * ITUtil.PORTAL+"?channelTitle=Component&channelUrl="+WebDriverUtils.getBaseUrlString()+
     * "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.component.ComponentBo&docFormKey=88888888&returnLocation="+
     * +ITUtil.PORTAL_URL+ ITUtil.HIDE_RETURN_LINK;
     */    
    public static final String BOOKMARK_URL = AutomatedFunctionalTestUtils.PORTAL+"?channelTitle=Component&channelUrl="+ WebDriverUtils
            .getBaseUrlString()+"/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.component.ComponentBo&docFormKey=88888888&returnLocation="+
            AutomatedFunctionalTestUtils.PORTAL_URL+ AutomatedFunctionalTestUtils.HIDE_RETURN_LINK;

    String fourLetters;

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    /**
     * {@inheritDoc}
     * Component
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Component";
    }

    public void testActionListApproveBookmark(JiraAwareFailable failable) throws Exception {
        testActionListApprove();
        passed();
    }

    public void testActionListApproveNav(JiraAwareFailable failable) throws Exception {
        testActionListApprove();
        passed();
    }

    protected void createNewEnterDetails() throws InterruptedException {
        waitAndTypeByName("document.documentHeader.documentDescription","Test description of Component create new");
        selectByName("document.newMaintainableObject.namespaceCode","KR-WKFLW - Workflow");
        waitAndTypeByName("document.newMaintainableObject.code","Test1" + fourLetters);
        waitAndTypeByName("document.newMaintainableObject.name","Test1ComponentCode" + fourLetters);
    }

    /**
     * Creates an Action Request in a users action list.
     *
     * Initiates a new maintenance document (Component BO) with added adHoc request to create an action request
     * in a users action list for the document
     * @param user
     * @param actionType
     *
     * @return documentID of the newly initiated document to which the created action request applies.
     */
    protected String testCreateActionRequest(String user, String actionType) throws InterruptedException{
        selectFrameIframePortlet();
        waitAndClickByXpath(CREATE_NEW_XPATH);
        String docId = waitForDocId();
        fourLetters = RandomStringUtils.randomAlphabetic(4);
        createNewEnterDetails();
        addAdHocRecipients(new String[]{user, actionType});
        waitAndClickByName("methodToCall.route");
        checkForDocError();
        waitAndClickByName("methodToCall.close");
        waitAndClickByName("methodToCall.processAnswer.button1");
        return docId;
    }

    /**
     * tests the Approve ActionRequest.
     * Creates an approve request for a user. Then performs the Approve action.
     * @throws Exception
     */
    public void testActionListApprove() throws Exception {
        // create some doc to generate actions in the action list
        String docId = testCreateActionRequest("fred", "A");

        // impersonate fred
        waitAndTypeByName(BACKDOOR_ID_TEXT,"fred");
        waitAndClickByXpath(BACKDOOR_LOGIN_BUTTON_XPATH);

        // assert his approve action
        assertActionList(docId, "A");

        // approve the action
        waitAndClickLinkContainingText(docId);
        selectChildWindow();
        waitAndClickByName("methodToCall.approve");

        // assert that approve action is no longer in list
        waitForTextNotPresent(docId);

        // find it in outbox
        waitAndClickLinkContainingText("Outbox");
        waitForTextPresent(docId);

        // clear outbox ??
        waitAndClickAllByName("outboxItems");
        waitAndClickByName("methodToCall.removeOutboxItems");

        // close child window
        // driver.close();
        selectTopFrame();

    }

    /**
     * tests the Approve ActionRequest.
     * Creates an approve request for a user. Then performs the Approve action.
     * @throws Exception
     */
    public void testActionListDisapprove() throws Exception {
        // create some doc to generate actions in the action list
        String docId = testCreateActionRequest("fred", "A");

        // impersonate fred
        waitAndTypeByName(BACKDOOR_ID_TEXT,"fred");
        waitAndClickByXpath(BACKDOOR_LOGIN_BUTTON_XPATH);

        // assert his approve action
        assertActionList(docId, "A");

        // approve the action
        waitAndClickLinkContainingText(docId);
        selectChildWindow();
        waitAndClickByName("methodToCall.approve");

        // assert that approve action is no longer in list
        waitForTextNotPresent(docId);

        // find it in outbox
        waitAndClickLinkContainingText("Outbox");
        waitForTextPresent(docId);

        // clear outbox ??
        waitAndClickAllByName("outboxItems");
        waitAndClickByName("methodToCall.removeOutboxItems");

        // close child window
        // driver.close();
        selectTopFrame();
    }

    public void testActionListApproveFull() throws Exception {
        fourLetters = RandomStringUtils.randomAlphabetic(4);
        createNewTemplateMethod();
    }
}
