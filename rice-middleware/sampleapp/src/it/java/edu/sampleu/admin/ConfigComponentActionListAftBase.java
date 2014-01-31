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
import org.kuali.rice.testtools.selenium.AutomatedFunctionalTestUtils;
import org.kuali.rice.testtools.selenium.WebDriverUtils;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ConfigComponentActionListAftBase extends AdminTmplMthdAftNavBase {

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

    private void assertActionListPersonRequest(String backdoorUser, String actionCode) throws InterruptedException {
        String docId = testCreateActionPersonRequest(backdoorUser, actionCode);
        impersonateUser(backdoorUser);
        assertActionList(docId, actionCode);
        selectTopFrame();
    }

    protected void createNewEnterDetails() throws InterruptedException {
        waitAndTypeByName("document.documentHeader.documentDescription",
                "Test description of " + getLinkLocator() + " create new");
        selectByName("document.newMaintainableObject.namespaceCode", "KR-WKFLW - Workflow");
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
    protected String testCreateActionPersonRequest(String user, String actionType) throws InterruptedException{
        selectFrameIframePortlet();
        waitAndClickCreateNew();
        String docId = waitForDocId();
        fourLetters = RandomStringUtils.randomAlphabetic(4);
        createNewEnterDetails();
        addAdHocPersonRecipients(new String[]{user, actionType});
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
    public void testActionListPersonApprove() throws Exception {
        assertActionListPersonRequest("fred", "A");
    }

    /**
     * tests the  ActionRequest.
     * Creates an approve request for a user. Then performs the Disapprove action.
     * @throws Exception
     */
    public void testActionListPersonDisapprove() throws Exception {
        assertActionListPersonRequest("fred", "D");
    }

    /**
     * tests the complete ActionRequest.
     * Creates an complete request for a user. Then performs the Complete action.
     * @throws Exception
     */
    public void testActionListPersonComplete() throws Exception {
        assertActionListPersonRequest("fran", "C");
    }

    /**
     * tests the Acknowledge ActionRequest.
     * Creates an Acknowledge request for a user. Then performs the Acknowledge action.
     * @throws Exception
     */
    public void testActionListPersonAcknowledge() throws Exception {
        assertActionListPersonRequest("erin", "K");
    }

    /**
     * tests the FYI ActionRequest.
     * Creates an FYI request for a user. Then performs the FYI action.
     * @throws Exception
     */
    public void testActionListPersonFyi() throws Exception {
        assertActionListPersonRequest("eric", "F");
    }
}
