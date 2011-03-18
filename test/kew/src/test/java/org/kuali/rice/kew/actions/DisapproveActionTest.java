/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.actions;

import mocks.MockEmailNotificationService;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.kew.actions.BlanketApproveTest.NotifySetup;

import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;
import org.kuali.rice.kew.util.KEWConstants;

import static org.junit.Assert.*;

public class DisapproveActionTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }

    @Test public void testDisapprove() throws Exception {
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");

        document = new WorkflowDocument(getPrincipalIdForName("jhopf"), document.getRouteHeaderId());
        assertTrue("This user should have an approve request", document.isApprovalRequested());
        document.approve("");

        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), document.getRouteHeaderId());
        assertTrue("This user should have an approve request", document.isApprovalRequested());
        document.approve("");//ewestfal had force action rule

        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        assertTrue("This user should have an approve request", document.isApprovalRequested());
        document.approve("");

        //this be the role delegate of jitrue
        document = new WorkflowDocument(getPrincipalIdForName("natjohns"), document.getRouteHeaderId());
        assertTrue("This user should have an approve request", document.isApprovalRequested());
        document.approve("");

        document = new WorkflowDocument(getPrincipalIdForName("bmcgough"), document.getRouteHeaderId());
        assertTrue("This user should have an approve request", document.isApprovalRequested());
        // assert that the document is at the same node before and after disapprove
        TestUtilities.assertAtNode(document, NotifySetup.NOTIFY_FINAL_NODE);
        document.disapprove("");
        TestUtilities.assertAtNode(document, NotifySetup.NOTIFY_FINAL_NODE);
        // reload just to double check
        document = new WorkflowDocument(getPrincipalIdForName("bmcgough"), document.getRouteHeaderId());
        TestUtilities.assertAtNode(document, NotifySetup.NOTIFY_FINAL_NODE);

        assertTrue("Document should be disapproved", document.stateIsDisapproved());
        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), document.getRouteHeaderId());
        assertTrue("ack should be requested as part of disapprove notification", document.isAcknowledgeRequested());

        document = new WorkflowDocument(getPrincipalIdForName("jhopf"), document.getRouteHeaderId());
        assertTrue("ack should be requested as part of disapprove notification", document.isAcknowledgeRequested());

        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        assertTrue("ack should be requested as part of disapprove notification", document.isAcknowledgeRequested());

        //jitrue while part of original approval chain did not take approve action and therefore should
        //not get action
        document = new WorkflowDocument(getPrincipalIdForName("jitrue"), document.getRouteHeaderId());
        assertFalse("ack should be requested as part of disapprove notification", document.isAcknowledgeRequested());

        document = new WorkflowDocument(getPrincipalIdForName("natjohns"), document.getRouteHeaderId());
        assertTrue("ack should be requested as part of disapprove notification", document.isAcknowledgeRequested());

        //shenl part of approval chain but didn't take action
        document = new WorkflowDocument(getPrincipalIdForName("shenl"), document.getRouteHeaderId());
        assertFalse("ack should be requested as part of disapprove notification", document.isAcknowledgeRequested());

        //check that all the emailing went right.
        assertEquals("jhopf should have been sent an approve email", 1, getMockEmailService().immediateReminderEmailsSent("jhopf", document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_APPROVE_REQ));
        assertEquals("jhopf should have been sent an ack email", 1, getMockEmailService().immediateReminderEmailsSent("jhopf", document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));

        assertEquals("ewestfal should have been sent an approve email", 1, getMockEmailService().immediateReminderEmailsSent("ewestfal", document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_APPROVE_REQ));
        assertEquals("ewestfal should have been sent an ack email", 1, getMockEmailService().immediateReminderEmailsSent("ewestfal", document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));

        //rkirkend is a primary delegate and therefore should not receive email notification
        assertEquals("rkirkend should not have been sent an approve email", 0, getMockEmailService().immediateReminderEmailsSent("rkirkend", document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_APPROVE_REQ));
        assertEquals("rkirkend should have been sent an ack email", 1, getMockEmailService().immediateReminderEmailsSent("rkirkend", document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));

        //temay is rkirkend primary delegate she should have received notification
        assertEquals("temay should have been sent an approve email", 1, getMockEmailService().immediateReminderEmailsSent("temay", document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_APPROVE_REQ));

        //there should be no ack emails for temay
        assertEquals("temay should have been sent an ack email", 0, getMockEmailService().immediateReminderEmailsSent("temay", document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));

        // pmckown is a secondary delegate here so he should NOT have received a notification
        assertEquals("pmckown should not have been sent an approve email", 0, getMockEmailService().immediateReminderEmailsSent("pmckown", document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_APPROVE_REQ));
        assertEquals("pmckown should not have been sent an ack email", 0, getMockEmailService().immediateReminderEmailsSent("pmckown", document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));

        //this is a secondary delegator and should receive notifications
        assertEquals("jitrue should have been sent an approve email", 1, getMockEmailService().immediateReminderEmailsSent("jitrue", document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_APPROVE_REQ));
        //no ack emails to jitrue
        assertEquals("jitrue should have been sent an ack email", 0, getMockEmailService().immediateReminderEmailsSent("jitrue", document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));

        //the 2nd delegates should NOT receive notifications by default
        assertEquals("natjohns should not have been sent an approve email", 0, getMockEmailService().immediateReminderEmailsSent("natjohns", document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_APPROVE_REQ));
        //2ndary delegate
        assertEquals("natjohns should not have been sent an ack email", 1, getMockEmailService().immediateReminderEmailsSent("natjohns", document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));
        assertEquals("shenl should not have been sent an approve email", 0, getMockEmailService().immediateReminderEmailsSent("shenl", document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_APPROVE_REQ));
        assertEquals("shenl should not have been sent an ack email", 0, getMockEmailService().immediateReminderEmailsSent("shenl", document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));

        assertEquals("bmcgough should have been sent an approve email", 1, getMockEmailService().immediateReminderEmailsSent("bmcgough", document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_APPROVE_REQ));
        assertEquals("bmcgough should not have been sent an ack email", 0, getMockEmailService().immediateReminderEmailsSent("bmcgough", document.getRouteHeaderId(), KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));
    }

    /**
     * Tests whether the initator who disapproved a doc gets an acknowledgement
     *
     */
    @Ignore("This test will fail until KULRICE-752 is resolved")
    @Test public void testInitiatorRoleDisapprove() throws WorkflowException {
        // test initiator disapproval of their own doc via InitiatorRoleAttribute
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("arh14"), "InitiatorRoleApprovalTest");
        document.routeDocument("routing document");

        document = new WorkflowDocument(getPrincipalIdForName("arh14"), document.getRouteHeaderId());
        document.disapprove("disapproving the document");

        document = new WorkflowDocument(getPrincipalIdForName("arh14"), document.getRouteHeaderId());
        assertFalse("Initiator should not have an Ack request from disapproval because they were the disapprover user", document.isAcknowledgeRequested());
    }

    /**
     * Tests whether the initator who disapproved a doc gets an acknowledgement
     * 
     */
    @Ignore("This test will fail until KULRICE-752 is resolved")
    @Test public void testInitiatorDisapprove() throws WorkflowException {
        // test initiator disapproval, via normal request with forceAction=true
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");

        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), document.getRouteHeaderId());
        document.disapprove("");

        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), document.getRouteHeaderId());
        assertFalse("Initiator should not have an Ack request from disapproval because they were the disapprover user", document.isAcknowledgeRequested());
    }

    @Test public void testDisapproveByArbitraryRecipient() throws WorkflowException {
        // test approval by some other person
        WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), "BlanketApproveSequentialTest");
        document.routeDocument("");

        document = new WorkflowDocument(getPrincipalIdForName("bmcgough"), document.getRouteHeaderId());
        document.disapprove("disapproving as bmcgough");

        document = new WorkflowDocument(getPrincipalIdForName("bmcgough"), document.getRouteHeaderId());
        assertFalse("Acknowledge was incorrectly sent to non-initiator disapprover", document.isAcknowledgeRequested());

        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), document.getRouteHeaderId());
        assertTrue("Acknowledge was not sent to initiator", document.isAcknowledgeRequested());
    }

    private MockEmailNotificationService getMockEmailService() {
        return (MockEmailNotificationService)KEWServiceLocator.getActionListEmailService();
    }
}
