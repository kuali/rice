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
 * @author Kuali Rice Team (rice.collab@kuali.org)
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

    private void assertActionListRequestGroup(String userInGroup, String group, String namespace, String actionCode, String state) throws InterruptedException {
        namespaceCode = namespace;
        String docId = testCreateActionRequestGroup(group, namespace, actionCode);
        impersonateUser(userInGroup);
        assertActionList(docId, actionCode, state);
        selectTopFrame();
    }

    private void assertActionListRequestPerson(String backdoorUser, String actionCode, String state) throws InterruptedException {
        String docId = testCreateActionRequestPerson(backdoorUser, actionCode);
        impersonateUser(backdoorUser);
        assertActionList(docId, actionCode, state);
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
     * @param user
     * @param actionType
     *
     * @return documentID of the newly initiated document to which the created action request applies.
     */
    protected String testCreateActionRequestPerson(String user, String actionType) throws InterruptedException {
        String docId = testCreateNew();
        addAdHocRecipientsPerson(new String[]{user, actionType});
        submitAndClose();
        return docId;
    }

    public void testActionListAcknowledgeGroup() throws Exception {
        assertActionListRequestGroup("fran", "RecipeMasters", "KR-WKFLW", "K", "PROCESSED");
        passed();
    }

    /**
     * tests the Acknowledge ActionRequest.
     * Creates an Acknowledge request for a user. Then performs the Acknowledge action.
     * @throws Exception
     */
    public void testActionListAcknowledgePerson() throws Exception {
        assertActionListRequestPerson("erin", "K", "PROCESSED");
        passed();
    }

    public void testActionListApproveGroup() throws Exception {
        assertActionListRequestGroup("fred", "RecipeMasters", "KR-WKFLW", "A", "ENROUTE");
        passed();
    }

    /**
     * tests the Approve ActionRequest.
     * Creates an approve request for a user. Then performs the Approve action.
     * @throws Exception
     */
    public void testActionListApprovePerson() throws Exception {
        assertActionListRequestPerson("fred", "A", "ENROUTE");
        passed();
    }

    public void testActionListCompleteGroup() throws Exception {
        assertActionListRequestGroup("dev1", "Kuali Developers", "KUALI", "C", "ENROUTE");
        passed();
    }

    /**
     * tests the complete ActionRequest.
     * Creates an complete request for a user. Then performs the Complete action.
     * @throws Exception
     */
    public void testActionListCompletePerson() throws Exception {
        assertActionListRequestPerson("fran", "C", "ENROUTE");
        passed();
    }

    public void testActionListDisapproveGroup() throws Exception {
        assertActionListRequestGroup("director", "ChickenRecipeMasters", "KR-WKFLW", "D", "ENROUTE");
        passed();
    }

    /**
     * tests the  ActionRequest.
     * Creates an approve request for a user. Then performs the Disapprove action.
     * @throws Exception
     */
    public void testActionListDisapprovePerson() throws Exception {
        assertActionListRequestPerson("fred", "D", "ENROUTE");
        passed();
    }

    public void testActionListFyiGroup() throws Exception {
        assertActionListRequestGroup("dev2", "Kuali Developers", "KUALI", "F", "FINAL");
        passed();
    }

    /**
     * tests the FYI ActionRequest.
     * Creates an FYI request for a user. Then performs the FYI action.
     * @throws Exception
     */
    public void testActionListFyiPerson() throws Exception {
        assertActionListRequestPerson("eric", "F", "FINAL");
        passed();
    }
}
