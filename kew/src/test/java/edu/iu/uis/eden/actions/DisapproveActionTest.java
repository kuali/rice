/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.actions;

import mocks.MockEmailNotificationService;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actions.BlanketApproveTest.NotifySetup;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.test.TestUtilities;

public class DisapproveActionTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }

    @Test public void testDisapprove() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");

        document = new WorkflowDocument(new NetworkIdVO("jhopf"), document.getRouteHeaderId());
        assertTrue("This user should have an approve request", document.isApprovalRequested());
        document.approve("");

        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
        assertTrue("This user should have an approve request", document.isApprovalRequested());
        document.approve("");//ewestfal had ignore previous rule

        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertTrue("This user should have an approve request", document.isApprovalRequested());
        document.approve("");

        //this be the role delegate of jitrue
        document = new WorkflowDocument(new NetworkIdVO("natjohns"), document.getRouteHeaderId());
        assertTrue("This user should have an approve request", document.isApprovalRequested());
        document.approve("");

        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        assertTrue("This user should have an approve request", document.isApprovalRequested());
        // assert that the document is at the same node before and after disapprove
        TestUtilities.assertAtNode(document, NotifySetup.NOTIFY_FINAL_NODE);
        document.disapprove("");
        TestUtilities.assertAtNode(document, NotifySetup.NOTIFY_FINAL_NODE);
        // reload just to double check
        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        TestUtilities.assertAtNode(document, NotifySetup.NOTIFY_FINAL_NODE);

        assertTrue("Document should be disapproved", document.stateIsDisapproved());
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
        assertTrue("ack should be requested as part of disapprove notification", document.isAcknowledgeRequested());

        document = new WorkflowDocument(new NetworkIdVO("jhopf"), document.getRouteHeaderId());
        assertTrue("ack should be requested as part of disapprove notification", document.isAcknowledgeRequested());

        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertTrue("ack should be requested as part of disapprove notification", document.isAcknowledgeRequested());

        //jitrue while part of original approval chain did not take approve action and therefore should
        //not get action
        document = new WorkflowDocument(new NetworkIdVO("jitrue"), document.getRouteHeaderId());
        assertFalse("ack should be requested as part of disapprove notification", document.isAcknowledgeRequested());

        document = new WorkflowDocument(new NetworkIdVO("natjohns"), document.getRouteHeaderId());
        assertTrue("ack should be requested as part of disapprove notification", document.isAcknowledgeRequested());

        //shenl part of approval chain but didn't take action
        document = new WorkflowDocument(new NetworkIdVO("shenl"), document.getRouteHeaderId());
        assertFalse("ack should be requested as part of disapprove notification", document.isAcknowledgeRequested());

        //check that all the emailing went right.
        assertEquals("jhopf should have been sent an approve email", 1, getMockEmailService().emailsSent("jhopf", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
        assertEquals("jhopf should have been sent an ack email", 1, getMockEmailService().emailsSent("jhopf", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));

        assertEquals("ewestfal should have been sent an approve email", 1, getMockEmailService().emailsSent("ewestfal", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
        assertEquals("ewestfal should have been sent an ack email", 1, getMockEmailService().emailsSent("ewestfal", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));

        //rkirkend is a primary delegate and therefore should not receive email notification
        assertEquals("rkirkend should not have been sent an approve email", 0, getMockEmailService().emailsSent("rkirkend", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
        assertEquals("rkirkend should have been sent an ack email", 1, getMockEmailService().emailsSent("rkirkend", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));

        //temay is rkirkend primary delegate she should have received notification
        assertEquals("temay should have been sent an approve email", 1, getMockEmailService().emailsSent("temay", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));

        //there should be no ack emails for temay
        assertEquals("temay should have been sent an ack email", 0, getMockEmailService().emailsSent("temay", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));

        // pmckown is a secondary delegate here so he should NOT have received a notification
        assertEquals("pmckown should not have been sent an approve email", 0, getMockEmailService().emailsSent("pmckown", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
        assertEquals("pmckown should not have been sent an ack email", 0, getMockEmailService().emailsSent("pmckown", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));

        //this is a secondary delegator and should receive notifications
        assertEquals("jitrue should have been sent an approve email", 1, getMockEmailService().emailsSent("jitrue", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
        //no ack emails to jitrue
        assertEquals("jitrue should have been sent an ack email", 0, getMockEmailService().emailsSent("jitrue", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));

        //the 2nd delegates should NOT receive notifications by default
        assertEquals("natjohns should not have been sent an approve email", 0, getMockEmailService().emailsSent("natjohns", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
        //2ndary delegate
        assertEquals("natjohns should not have been sent an ack email", 1, getMockEmailService().emailsSent("natjohns", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));
        assertEquals("shenl should not have been sent an approve email", 0, getMockEmailService().emailsSent("shenl", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
        assertEquals("shenl should not have been sent an ack email", 0, getMockEmailService().emailsSent("shenl", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));

        assertEquals("bmcgough should have been sent an approve email", 1, getMockEmailService().emailsSent("bmcgough", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
        assertEquals("bmcgough should not have been sent an ack email", 0, getMockEmailService().emailsSent("bmcgough", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));
    }

    /**
     * Tests whether the person who disapproved a doc gets an acknowledgement
     *
     * This test will fail until EN-637 is resolved.
     */
    @Ignore
    @Test public void testInitiatorRoleDisapprove() throws WorkflowException {
        // test initiator disapproval of their own doc via InitiatorRoleAttribute
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("arh14"), "InitiatorRoleApprovalTest");
        document.routeDocument("routing document");

        document = new WorkflowDocument(new NetworkIdVO("arh14"), document.getRouteHeaderId());
        document.disapprove("disapproving the document");

        document = new WorkflowDocument(new NetworkIdVO("arh14"), document.getRouteHeaderId());
        assertFalse("Initiator was sent redundant Ack request after personal disapproval - This test will fail until EN-637 is resolved", document.isAcknowledgeRequested());
    }

    /**
     * This test will fail until EN-637 is resolved.
     */
    @Ignore
    @Test public void testInitiatorDisapprove() throws WorkflowException {
        // test initiator disapproval, via normal request with ignoreprevious=true
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");

        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
        document.disapprove("");

        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
        assertFalse("Initiator was sent redundant Ack request after personal disapproval - This test will fail until EN-637 is resolved", document.isAcknowledgeRequested());
    }

    @Test public void testDisapproveByArbitraryRecipient() throws WorkflowException {
        // test approval by some other person
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "BlanketApproveSequentialTest");
        document.routeDocument("");

        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        document.disapprove("disapproving as bmcgough");

        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        assertFalse("Acknowledge was incorrectly sent to non-initiator disapprover", document.isAcknowledgeRequested());

        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
        assertTrue("Acknowledge was not sent to initiator", document.isAcknowledgeRequested());
    }

    private MockEmailNotificationService getMockEmailService() {
        return (MockEmailNotificationService)KEWServiceLocator.getActionListEmailService();
    }
}