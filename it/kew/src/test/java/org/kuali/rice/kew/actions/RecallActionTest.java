/*
 * Copyright 2006-2012 The Kuali Foundation
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actions.BlanketApproveTest.NotifySetup;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.InvalidActionTakenException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.services.KimImplServiceLocator;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class RecallActionTest extends KEWTestCase {
    private static final String RECALL_TEST_DOC = "RecallTest";
    private static final String RECALL_NOTIFY_TEST_DOC = "RecallWithPrevNotifyTest";
    private static final String RECALL_NOTIFY_THIRDPARTY_TEST_DOC = "RecallWithThirdPartyNotifyTest";

    private String EWESTFAL = null;
    private String JHOPF = null;
    private String RKIRKEND = null;
    private String NATJOHNS = null;
    private String BMCGOUGH = null;

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }

    @Override
    protected void setUpAfterDataLoad() throws Exception {
        super.setUpAfterDataLoad();
        EWESTFAL = getPrincipalIdForName("ewestfal");
        JHOPF = getPrincipalIdForName("jhopf");
        RKIRKEND = getPrincipalIdForName("rkirkend");
        NATJOHNS = getPrincipalIdForName("natjohns");
        BMCGOUGH = getPrincipalIdForName("bmcgough");
    }

    @Test(expected=InvalidActionTakenException.class) public void testCantRecallUnroutedDoc() {
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(EWESTFAL, RECALL_TEST_DOC);
        document.recall("recalling", true);
    }

    @Test public void testRecallAsInitiatorBeforeAnyApprovals() throws Exception {
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(EWESTFAL, RECALL_TEST_DOC);
        document.route("");

        document.recall("recalling", true);

        assertTrue("Document should be recalled", document.isRecalled());

        //verify that the document is truly dead - no more action requests or action items.
        
        List requests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getDocumentId());
        assertEquals("Should not have any active requests", 0, requests.size());
        
        Collection<ActionItem> actionItems = KEWServiceLocator.getActionListService().findByDocumentId(document.getDocumentId());
        assertEquals("Should not have any action items", 0, actionItems.size());
    }

    @Test public void testRecallAsInitiatorAfterSingleApproval() throws Exception {
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(EWESTFAL, RECALL_TEST_DOC);
        document.route("");

        document = WorkflowDocumentFactory.loadDocument(JHOPF, document.getDocumentId());
        document.approve("");

        document = WorkflowDocumentFactory.loadDocument(EWESTFAL, document.getDocumentId());
        document.recall("recalling", true);

        assertTrue("Document should be recalled", document.isRecalled());

        //verify that the document is truly dead - no more action requests or action items.

        List requests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getDocumentId());
        assertEquals("Should not have any active requests", 0, requests.size());

        Collection<ActionItem> actionItems = KEWServiceLocator.getActionListService().findByDocumentId(document.getDocumentId());
        assertEquals("Should not have any action items", 0, actionItems.size());
    }

    @Test(expected=InvalidActionTakenException.class)
    public void testRecallInvalidWhenProcessed() throws Exception {
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(EWESTFAL, RECALL_TEST_DOC);
        document.route("");

        for (String user: new String[] { JHOPF, EWESTFAL, RKIRKEND, NATJOHNS, BMCGOUGH }) {
            document = WorkflowDocumentFactory.loadDocument(user, document.getDocumentId());
            document.approve("");
        }

        document.refresh();
        assertTrue("Document should be processed", document.isProcessed());
        assertTrue("Document should be approved", document.isApproved());
        assertFalse("Document should not be final", document.isFinal());

        document = WorkflowDocumentFactory.loadDocument(EWESTFAL, document.getDocumentId());
        document.recall("recalling when processed should fail", true);
    }

    @Test(expected=InvalidActionTakenException.class)
    public void testRecallInvalidWhenFinal() throws Exception {
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(EWESTFAL, RECALL_TEST_DOC);
        document.route("");
        
        for (String user: new String[] { JHOPF, EWESTFAL, RKIRKEND, NATJOHNS, BMCGOUGH }) {
            document = WorkflowDocumentFactory.loadDocument(user, document.getDocumentId());
            document.approve("");
        }
        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("xqi"), document.getDocumentId());
        document.acknowledge("");

        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("jthomas"), document.getDocumentId());
        document.fyi();

        for (ActionRequest a: document.getRootActionRequests()) {
            System.err.println(a);
            if (a.isAcknowledgeRequest() || a.isFyiRequest()) {
                System.err.println(a.getPrincipalId());
                System.err.println(KimApiServiceLocator.getIdentityService().getPrincipal(a.getPrincipalId()).getPrincipalName());
            }
        }

        assertFalse("Document should not be processed", document.isProcessed());
        assertTrue("Document should be approved", document.isApproved());
        assertTrue("Document should be final", document.isFinal());

        document = WorkflowDocumentFactory.loadDocument(EWESTFAL, document.getDocumentId());
        document.recall("recalling when processed should fail", true);
    }

//    @Test public void testInitiatorOnlyCancel() throws Exception {
//        WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
//
//        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("user1"), document.getDocumentId());
//        try {
//            document.cancel("");
//            fail("Document should not be allowed to be cancelled due to initiator check.");
//        } catch (Exception e) {
//
//        }
//    }

    @Test public void testRecallToActionListAsInitiatorBeforeAnyApprovals() throws Exception {
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(EWESTFAL, RECALL_TEST_DOC);
        document.route("");

        document.recall("recalling", false);

        assertTrue("Document should be saved", document.isSaved());

        // initiator has completion request
        assertTrue(document.isCompletionRequested());
        // first approver has FYI
        assertTrue(WorkflowDocumentFactory.loadDocument(JHOPF, document.getDocumentId()).isFYIRequested());

        document.complete("completing");

        assertTrue("Document should be enroute", document.isEnroute());

        assertTrue(WorkflowDocumentFactory.loadDocument(JHOPF, document.getDocumentId()).isApprovalRequested());
    }

    @Test public void testRecallToActionListAsInitiatorAfterApprovals() throws Exception {
        this.testRecallToActionListAsInitiatorAfterApprovals(false, null);
    }

    @Test public void testRecallToActionListAsInitiatorWithNotificationAfterApprovals() throws Exception {
        this.testRecallToActionListAsInitiatorAfterApprovals(true, null);
    }

    @Test public void testRecallToActionListAsInitiatorWithThirdPartyNotificationAfterApprovals() throws Exception {
        this.testRecallToActionListAsInitiatorAfterApprovals(true, new String[] { "quickstart", "admin" });
    }

    protected void testRecallToActionListAsInitiatorAfterApprovals(boolean notifyPreviousRecipients, String[] thirdPartiesNotified) {
        String docType = !ArrayUtils.isEmpty(thirdPartiesNotified) ? RECALL_NOTIFY_THIRDPARTY_TEST_DOC : (notifyPreviousRecipients ? RECALL_NOTIFY_TEST_DOC : RECALL_TEST_DOC);
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(EWESTFAL, docType);
        document.route("");

        WorkflowDocumentFactory.loadDocument(JHOPF, document.getDocumentId()).approve("");
        WorkflowDocumentFactory.loadDocument(EWESTFAL, document.getDocumentId()).approve("");
        WorkflowDocumentFactory.loadDocument(RKIRKEND, document.getDocumentId()).approve("");

        document = WorkflowDocumentFactory.loadDocument(EWESTFAL, document.getDocumentId());
        document.recall("recalling", false);

        assertTrue("Document should be saved", document.isSaved());

        // initiator has completion request
        assertTrue(document.isCompletionRequested());
        
        // pending approver has FYI
        assertTrue(WorkflowDocumentFactory.loadDocument(NATJOHNS, document.getDocumentId()).isFYIRequested());
        // third approver has FYI
        assertEquals(notifyPreviousRecipients, WorkflowDocumentFactory.loadDocument(RKIRKEND, document.getDocumentId()).isFYIRequested());
        // second approver does not have FYI - approver is initiator, FYI is skipped
        assertFalse(WorkflowDocumentFactory.loadDocument(EWESTFAL, document.getDocumentId()).isFYIRequested());
        // first approver has FYI
        assertEquals(notifyPreviousRecipients, WorkflowDocumentFactory.loadDocument(JHOPF, document.getDocumentId()).isFYIRequested());

        if (!ArrayUtils.isEmpty(thirdPartiesNotified)) {
            for (String recipient: thirdPartiesNotified) {
                assertTrue("Expected FYI to be sent to: " + recipient, WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(recipient), document.getDocumentId()).isFYIRequested());
            }
        }
        
        // omit JHOPF, and see if FYI is subsumed by approval request
        for (String user: new String[] { RKIRKEND, NATJOHNS }) {
            WorkflowDocumentFactory.loadDocument(user, document.getDocumentId()).fyi();
        }

        document.complete("completing");

        assertTrue("Document should be enroute", document.isEnroute());

        // generation of approval requests nullify FYIs (?)
        // if JHOPF had an FYI, he doesn't any longer
        for (String user: new String[] { JHOPF, RKIRKEND, NATJOHNS }) {
            document = WorkflowDocumentFactory.loadDocument(user, document.getDocumentId());
            assertFalse(getPrincipalNameForId(user) + " should not have an FYI", document.isFYIRequested());
        }

        // submit all approvals
        for (String user: new String[] { JHOPF, EWESTFAL, RKIRKEND, NATJOHNS, BMCGOUGH }) {
            document = WorkflowDocumentFactory.loadDocument(user, document.getDocumentId());
            assertTrue(getPrincipalNameForId(user) + " should have approval request", document.isApprovalRequested());
            document.approve("approving");
        }

        // 2 acks outstanding, we're PROCESSED
        assertTrue("Document should be processed", document.isProcessed());
        assertTrue("Document should be approved", document.isApproved());

        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("xqi"), document.getDocumentId());
        document.acknowledge("");

        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("jthomas"), document.getDocumentId());
        document.fyi();

        assertTrue("Document should be approved", document.isApproved());
        assertTrue("Document should be final", document.isFinal());
    }
}
