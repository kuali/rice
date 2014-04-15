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

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ConfigComponentActionListNavAft extends ConfigComponentActionListAftBase {

    @Test
    public void testActionListAcknowledgeGroupNav() throws Exception {
        testActionListAcknowledgeGroup();
    }

    @Test
    public void testActionListAcknowledgePersonNav() throws Exception {
        testActionListAcknowledgePerson();
    }

    @Test
    public void testActionListApproveGroupNav() throws Exception {
        testActionListApproveGroup();
    }

    @Test
    public void testActionListApprovePersonNav() throws Exception {
        testActionListApprovePerson();
    }

    @Test
    public void testActionListCompleteGroupNav() throws Exception {
        testActionListCompleteGroup();
    }

    @Test
    public void testActionListCompletePersonNav() throws Exception {
        testActionListCompletePerson();
    }

    @Test
    public void  testActionListCompletePerson_WithPendingAcknowledge_Nav() throws Exception {
        testActionListCompletePerson_WithPendingAcknowledge();
    }

    @Test
    public void testActionListDisapproveGroupNav() throws Exception {
        testActionListDisapproveGroup();
    }

    @Test
    public void testActionListDisapprovePersonNav() throws Exception {
        testActionListDisapprovePerson();
    }

    @Test
    public void testActionListFyiGroupNav() throws Exception {
        testActionListFyiGroup();
    }

    @Test
    public void testActionListFyiPersonNav() throws Exception {
        testActionListFyiPerson();
    }

    @Test
    public void testActionListAcknowledgePerson_WithPendingApprove_Nav() throws Exception {
        testActionListAcknowledgePerson_WithPendingApprove();
    }

    @Test
    public void testActionListAcknowledgePerson_WithPendingAcknowledge_Nav() throws Exception {
        testActionListAcknowledgePerson_WithPendingAcknowledge();
    }

    @Test
    public void testActionListApprovePerson_WithPendingApprove_Nav() throws Exception {
        testActionListApprovePerson_WithPendingApprove();
    }

    @Test
    public void testActionListApprovePerson_WithPendingAcknowledge_Nav() throws Exception {
        testActionListApprovePerson_WithPendingAcknowledge();
    }


    @Test
    public void testComponentSave_WithPendingPersonApprove_Nav() throws Exception {
        testComponentSave_WithPendingPersonApprove();
    }

    @Test
    public void testComponentCancel_WithPendingPersonApprove_Nav() throws Exception {
        testComponentCancel_WithPendingPersonApprove();
    }

    @Test
    public void testComponentRecallAndCancel_WithPendingPersonApprove_Nav() throws Exception {
        testComponentRecallAndCancel_WithPendingPersonApprove();
    }

    @Test
    public void testComponentRecallToActionList_WithPendingPersonApprove_Nav() throws Exception {
        testComponentRecallToActionList_WithPendingPersonApprove();
    }

    @Test
    public void testComponentSubmit_WithPendingPersonApprove_Nav() throws Exception {
        testComponentSubmit_WithPendingPersonApprove();
    }

    @Test
    public void testComponentSubmit_WithPendingPersonAcknowledge_Nav() throws Exception {
        testComponentSubmit_WithPendingPersonAcknowledge();
    }

    @Test
    public void testComponentSubmit_WithPendingPersonFyi_Nav() throws Exception {
        testComponentSubmit_WithPendingPersonFyi();
    }
}

