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
package edu.sampleu.admin;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class PermissionActionListBkMrkAft extends PermissionActionListAftBase {

    @Override
    public String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Test
    public void testActionListAcknowledgeGroupBookmark() throws Exception {
        testActionListAcknowledgeGroup();
    }

    @Test
    public void testActionListAcknowledgePersonBookmark() throws Exception {
        testActionListAcknowledgePerson();
    }

    @Test
    public void testActionListApproveGroupBookmark() throws Exception {
        testActionListApproveGroup();
    }

    @Test
    public void testActionListApprovePersonBookmark() throws Exception {
        testActionListApprovePerson();
    }

    @Test
    public void testActionListCompleteGroupBookmark() throws Exception {
        testActionListCompleteGroup();
    }

    @Test
    public void testActionListCompletePersonBookmark() throws Exception {
        testActionListCompletePerson();
    }

    @Test
    public void  testActionListCompletePerson_WithPendingAcknowledge_Bookmark() throws Exception {
        testActionListCompletePerson_WithPendingAcknowledge();
    }

    @Test
    public void testActionListDisapproveGroupBookmark() throws Exception {
        testActionListDisapproveGroup();
    }

    @Test
    public void testActionListDisapprovePersonBookmark() throws Exception {
        testActionListDisapprovePerson();
    }

    @Test
    public void testActionListFyiGroupBookmark() throws Exception {
        testActionListFyiGroup();
    }

    @Test
    public void testActionListFyiPersonBookmark() throws Exception {
        testActionListFyiPerson();
    }

    @Test
    public void testActionListAcknowledgePerson_WithPendingApprove_Bookmark() throws Exception {
        testActionListAcknowledgePerson_WithPendingApprove();
    }

    @Test
    public void testActionListAcknowledgePerson_WithPendingAcknowledge_Bookmark() throws Exception {
        testActionListAcknowledgePerson_WithPendingAcknowledge();
    }

    @Test
    public void testActionListApprovePerson_WithPendingApprove_Bookmark() throws Exception {
        testActionListApprovePerson_WithPendingApprove();
    }

    @Test
    public void testActionListApprovePerson_WithPendingAcknowledge_Bookmark() throws Exception {
        testActionListApprovePerson_WithPendingAcknowledge();
    }


    @Test
    public void testPermissionActionListSave_WithPendingPersonApprove_Bookmark() throws Exception {
        testPermissionActionListSave_WithPendingPersonApprove();
    }

    @Test
    public void testPermissionActionListCancel_WithPendingPersonApprove_Bookmark() throws Exception {
        testPermissionActionListCancel_WithPendingPersonApprove();
    }

    @Test
    @Ignore("Permission recall not implemented on screen")
    public void testPermissionActionListRecallAndCancel_WithPendingPersonApprove_Bookmark() throws Exception {
        testPermissionActionListRecallAndCancel_WithPendingPersonApprove();
    }

    @Test
    @Ignore("Permission recall not implemented on screen")
    public void testPermissionActionListRecallToActionList_WithPendingPersonApprove_Bookmark() throws Exception {
        testPermissionActionListRecallToActionList_WithPendingPersonApprove();
    }

    @Test
    public void testPermissionActionListSubmit_WithPendingPersonApprove_Bookmark() throws Exception {
        testPermissionActionListSubmit_WithPendingPersonApprove();
    }

    @Test
    public void testPermissionActionListSubmit_WithPendingPersonAcknowledge_Bookmark() throws Exception {
        testPermissionActionListSubmit_WithPendingPersonAcknowledge();
    }

    @Test
    public void testPermissionActionListSubmit_WithPendingPersonFyi_Bookmark() throws Exception {
        testPermissionActionListSubmit_WithPendingPersonFyi();
    }
}