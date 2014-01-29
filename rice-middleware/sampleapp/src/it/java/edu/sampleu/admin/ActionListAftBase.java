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

    public void testActionListDisapproveBookmark(JiraAwareFailable failable) throws Exception {
        testActionListDisapprove();
        passed();
    }

    public void testActionListDisapproveNav(JiraAwareFailable failable) throws Exception {
        testActionListDisapprove();
        passed();
    }

    public void testActionListCompleteBookmark(JiraAwareFailable failable) throws Exception {
        testActionListComplete();
        passed();
    }

    public void testActionListCompleteNav(JiraAwareFailable failable) throws Exception {
        testActionListComplete();
        passed();
    }

    public void testActionListAcknowledgeBookmark(JiraAwareFailable failable) throws Exception {
        testActionListAcknowledge();
        passed();
    }

    public void testActionListAcknowledgeNav(JiraAwareFailable failable) throws Exception {
        testActionListAcknowledge();
        passed();
    }

    public void testActionListFYIBookmark(JiraAwareFailable failable) throws Exception {
        testActionListFYI();
        passed();
    }

    public void testActionListFYINav(JiraAwareFailable failable) throws Exception {
        testActionListFYI();
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
        waitAndClickCreateNew();
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
        String docId = testCreateActionRequest("fred", "A");
        impersonateUser("fred");
        assertActionList(docId, "A");
        selectTopFrame();
    }

    /**
     * tests the  ActionRequest.
     * Creates an approve request for a user. Then performs the Disapprove action.
     * @throws Exception
     */
    public void testActionListDisapprove() throws Exception {
        String docId = testCreateActionRequest("fred", "D");
        impersonateUser("fred");
        assertActionList(docId, "D");
        selectTopFrame();
    }

    /**
     * tests the complete ActionRequest.
     * Creates an complete request for a user. Then performs the Complete action.
     * @throws Exception
     */
    public void testActionListComplete() throws Exception {
        String docId = testCreateActionRequest("fran", "C");
        impersonateUser("fran");
        assertActionList(docId, "C");
        selectTopFrame();
    }

    /**
     * tests the Acknowledge ActionRequest.
     * Creates an Acknowledge request for a user. Then performs the Acknowledge action.
     * @throws Exception
     */
    public void testActionListAcknowledge() throws Exception {
        String docId = testCreateActionRequest("erin", "K");
        impersonateUser("erin");
        assertActionList(docId, "K");
        selectTopFrame();
    }

    /**
     * tests the FYI ActionRequest.
     * Creates an FYI request for a user. Then performs the FYI action.
     * @throws Exception
     */
    public void testActionListFYI() throws Exception {
        String docId = testCreateActionRequest("eric", "F");
        impersonateUser("eric");
        assertActionList(docId, "F");
        selectTopFrame();
    }
}
