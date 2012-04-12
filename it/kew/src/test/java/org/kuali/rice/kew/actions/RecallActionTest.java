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
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actions.BlanketApproveTest.NotifySetup;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.action.InvalidActionTakenException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo;
import org.kuali.rice.kim.impl.permission.PermissionBo;
import org.kuali.rice.kim.impl.permission.PermissionTemplateBo;
import org.kuali.rice.kim.impl.responsibility.ResponsibilityTemplateBo;
import org.kuali.rice.kim.impl.services.KimImplServiceLocator;
import org.kuali.rice.kim.impl.type.KimTypeAttributeBo;
import org.kuali.rice.kim.impl.type.KimTypeBo;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class RecallActionTest extends KEWTestCase {
    private static final String RECALL_TEST_DOC = "RecallTest";
    private static final String RECALL_NOTIFY_TEST_DOC = "RecallWithPrevNotifyTest";
    private static final String RECALL_NO_PENDING_NOTIFY_TEST_DOC = "RecallWithoutPendingNotifyTest";
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

        // can't recall recalled doc
        assertFalse(document.getValidActions().getValidActions().contains(ActionType.RECALL));
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

    @Test public void testRecallToActionListAsInitiatorBeforeAnyApprovals() throws Exception {
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(EWESTFAL, RECALL_TEST_DOC);
        document.route("");

        document.recall("recalling", false);

        assertTrue("Document should be saved", document.isSaved());
        assertEquals(1, document.getCurrentNodeNames().size());
        assertTrue(document.getCurrentNodeNames().contains("AdHoc"));

        // initiator has completion request
        assertTrue(document.isCompletionRequested());
        // can't recall saved doc
        assertFalse(document.getValidActions().getValidActions().contains(ActionType.RECALL));

        // first approver has FYI
        assertTrue(WorkflowDocumentFactory.loadDocument(JHOPF, document.getDocumentId()).isFYIRequested());

        document.complete("completing");

        assertTrue("Document should be enroute", document.isEnroute());

        assertTrue(WorkflowDocumentFactory.loadDocument(JHOPF, document.getDocumentId()).isApprovalRequested());
    }

    private static final String PERM_APP_DOC_STATUS = "recallable by admins";
    private static final String ROUTE_NODE = "NotifyFirst";
    private static final String ROUTE_STATUS = "R";

    protected Permission createRecallPermission(String docType, String appDocStatus, String routeNode, String routeStatus) {
        Template permTmpl = KimApiServiceLocator.getPermissionService().findPermTemplateByNamespaceCodeAndName(KewApiConstants.KEW_NAMESPACE, KewApiConstants.RECALL_PERMISSION);
        assertNotNull(permTmpl);
        Permission.Builder permission = Permission.Builder.create(KewApiConstants.KEW_NAMESPACE, KewApiConstants.RECALL_PERMISSION + " for test case");
        permission.setDescription("Recall");
        permission.setTemplate(Template.Builder.create(permTmpl));
        Map<String, String> attrs = new HashMap<String, String>();
        attrs.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, docType);
        attrs.put(KimConstants.AttributeConstants.APP_DOC_STATUS, appDocStatus);
        attrs.put(KimConstants.AttributeConstants.ROUTE_NODE_NAME, routeNode);
        attrs.put(KimConstants.AttributeConstants.ROUTE_STATUS_CODE, routeStatus);
        permission.setActive(true);
        permission.setAttributes(attrs);

        // save the permission and check that's it's wired up correctly
        Permission perm = KimApiServiceLocator.getPermissionService().createPermission(permission.build());
        assertEquals(perm.getTemplate().getId(), permTmpl.getId());
        int num = 1;
        if (appDocStatus != null) num++;
        if (routeNode != null) num++;
        if (routeStatus != null) num++;
        assertEquals(num, perm.getAttributes().size());
        assertEquals(docType, perm.getAttributes().get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));
        assertEquals(appDocStatus, perm.getAttributes().get(KimConstants.AttributeConstants.APP_DOC_STATUS));
        assertEquals(routeNode, perm.getAttributes().get(KimConstants.AttributeConstants.ROUTE_NODE_NAME));
        assertEquals(routeStatus, perm.getAttributes().get(KimConstants.AttributeConstants.ROUTE_STATUS_CODE));
        
        return perm;
    }

    // disable the existing Recall Permission assigned to Initiator Role for test purposes
    protected void disableRecallPermission() {
        Permission p = KimApiServiceLocator.getPermissionService().findPermByNamespaceCodeAndName("KR-WKFLW", "Recall Document");
        Permission.Builder pb = Permission.Builder.create(p);
        pb.setActive(false);
        KimApiServiceLocator.getPermissionService().updatePermission(pb.build());
    }

    /**
     * Tests that a new permission can be configured with the Recall Permission template and that matching works correctly
     * against the new permission
     */
    @Test public void testRecallPermissionMatching() {
        disableRecallPermission();
        createRecallPermission(RECALL_TEST_DOC, PERM_APP_DOC_STATUS, ROUTE_NODE, ROUTE_STATUS);

        Map<String, String> details = new HashMap<String, String>();
        details.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, RECALL_TEST_DOC);
        details.put(KimConstants.AttributeConstants.APP_DOC_STATUS, PERM_APP_DOC_STATUS);
        details.put(KimConstants.AttributeConstants.ROUTE_NODE_NAME, ROUTE_NODE);
        details.put(KimConstants.AttributeConstants.ROUTE_STATUS_CODE, ROUTE_STATUS);

        // test all single field mismatches
        for (Map.Entry<String, String> entry: details.entrySet()) {
            Map<String, String> testDetails = new HashMap<String, String>(details);
            // change a single detail to a non-matching value
            testDetails.put(entry.getKey(), entry.getValue() + " BOGUS ");
            assertFalse("non-matching " + entry.getKey() + " detail should cause template to not match", KimApiServiceLocator.getPermissionService().isPermissionDefinedByTemplate(KewApiConstants.KEW_NAMESPACE, KewApiConstants.RECALL_PERMISSION, testDetails));
        }

        assertTrue("template should match details", KimApiServiceLocator.getPermissionService().isPermissionDefinedByTemplate(KewApiConstants.KEW_NAMESPACE, KewApiConstants.RECALL_PERMISSION, details));
    }

    @Test public void testRecallPermissionTemplate() throws Exception {
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(EWESTFAL, RECALL_TEST_DOC);
        document.route("");

        // nope, technical admins can't recall
        assertFalse(WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("admin"), document.getDocumentId()).getValidActions().getValidActions().contains(ActionType.RECALL));
        assertFalse(WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("quickstart"), document.getDocumentId()).getValidActions().getValidActions().contains(ActionType.RECALL));

        // create a recall permission for the RECALL_TEST_DOC doctype
        Permission perm = createRecallPermission(RECALL_TEST_DOC, PERM_APP_DOC_STATUS, ROUTE_NODE, ROUTE_STATUS);

        // assign the permission to Technical Administrator role
        Role techadmin = KimApiServiceLocator.getRoleService().getRoleByNamespaceCodeAndName("KR-SYS", "Technical Administrator");
        KimApiServiceLocator.getRoleService().assignPermissionToRole(perm.getId(), techadmin.getId());

        // our recall permission is assigned to the technical admin role

        // but the doc will not match...
        document = WorkflowDocumentFactory.createDocument(EWESTFAL, RECALL_NOTIFY_TEST_DOC);
        document.route(PERM_APP_DOC_STATUS);
        assertFalse(WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("admin"), document.getDocumentId()).getValidActions().getValidActions().contains(ActionType.RECALL));
        assertFalse(WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("quickstart"), document.getDocumentId()).getValidActions().getValidActions().contains(ActionType.RECALL));

        // .. the app doc status will not match...
        document = WorkflowDocumentFactory.createDocument(EWESTFAL, RECALL_TEST_DOC);
        document.route("");
        // technical admins can't recall since the app doc status is not correct
        assertFalse(WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("admin"), document.getDocumentId()).getValidActions().getValidActions().contains(ActionType.RECALL));
        assertFalse(WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("quickstart"), document.getDocumentId()).getValidActions().getValidActions().contains(ActionType.RECALL));

        // ... the node will not match ...
        document = WorkflowDocumentFactory.createDocument(EWESTFAL, RECALL_TEST_DOC);
        document.route("");
        WorkflowDocumentFactory.loadDocument(JHOPF, document.getDocumentId()).approve(""); // approve past notifyfirstnode
        assertFalse(WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("admin"), document.getDocumentId()).getValidActions().getValidActions().contains(ActionType.RECALL));
        assertFalse(WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("quickstart"), document.getDocumentId()).getValidActions().getValidActions().contains(ActionType.RECALL));

        // ... the doc status will not match (not recallable anyway) ...
        document = WorkflowDocumentFactory.createDocument(EWESTFAL, RECALL_TEST_DOC);
        document.route("");
        document.cancel("cancelled");
        assertFalse(WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("admin"), document.getDocumentId()).getValidActions().getValidActions().contains(ActionType.RECALL));
        assertFalse(WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("quickstart"), document.getDocumentId()).getValidActions().getValidActions().contains(ActionType.RECALL));

        // everything should match
        document = WorkflowDocumentFactory.createDocument(EWESTFAL, RECALL_TEST_DOC);
        document.setApplicationDocumentStatus(PERM_APP_DOC_STATUS);
        document.route("");
        // now technical admins can recall by virtue of having the recall permission on this doc
        assertTrue(WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("admin"), document.getDocumentId()).getValidActions().getValidActions().contains(ActionType.RECALL));
        assertTrue(WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("quickstart"), document.getDocumentId()).getValidActions().getValidActions().contains(ActionType.RECALL));
    }

    @Test public void testRecallToActionListAsInitiatorAfterApprovals() throws Exception {
        this.testRecallToActionListAsInitiatorAfterApprovals(RECALL_TEST_DOC);
    }

    @Test public void testRecallToActionListAsInitiatorWithNotificationAfterApprovals() throws Exception {
        this.testRecallToActionListAsInitiatorAfterApprovals(RECALL_NOTIFY_TEST_DOC);
    }

    @Test public void testRecallToActionListAsInitiatorWithoutPendingNotificationAfterApprovals() throws Exception {
        this.testRecallToActionListAsInitiatorAfterApprovals(RECALL_NO_PENDING_NOTIFY_TEST_DOC);
    }

    @Test public void testRecallToActionListAsInitiatorWithThirdPartyNotificationAfterApprovals() throws Exception {
        this.testRecallToActionListAsInitiatorAfterApprovals(RECALL_NOTIFY_THIRDPARTY_TEST_DOC);
    }

    /**
     * Tests that the document is returned to the *recaller*'s action list, not the original initiator
     * @throws Exception
     */
    @Test public void testRecallToActionListAsThirdParty() throws Exception {
        Permission perm = createRecallPermission(RECALL_TEST_DOC, null, null, null);
        // assign the permission to Technical Administrator role
        Role techadmin = KimApiServiceLocator.getRoleService().getRoleByNamespaceCodeAndName("KR-SYS", "Technical Administrator");
        KimApiServiceLocator.getRoleService().assignPermissionToRole(perm.getId(), techadmin.getId());
        // recall as 'admin' user
        testRecallToActionListAfterApprovals(EWESTFAL, getPrincipalIdForName("admin"), RECALL_TEST_DOC);
    }

    protected void testRecallToActionListAsInitiatorAfterApprovals(String doctype) {
        testRecallToActionListAfterApprovals(EWESTFAL, EWESTFAL, doctype);
    }

    // Implements various permutations of recalls - with and without doctype policies/notifications of various sorts
    // and as initiator or a third party recaller
    protected void testRecallToActionListAfterApprovals(String initiator, String recaller, String doctype) {
        boolean notifyPreviousRecipients = !RECALL_TEST_DOC.equals(doctype);
        boolean notifyPendingRecipients = !RECALL_NO_PENDING_NOTIFY_TEST_DOC.equals(doctype);
        String[] thirdPartiesNotified = RECALL_NOTIFY_THIRDPARTY_TEST_DOC.equals(doctype) ? new String[] { "quickstart", "admin" } : new String[] {};
        
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(initiator, doctype);
        document.route("");

        WorkflowDocumentFactory.loadDocument(JHOPF, document.getDocumentId()).approve("");
        WorkflowDocumentFactory.loadDocument(initiator, document.getDocumentId()).approve("");
        WorkflowDocumentFactory.loadDocument(RKIRKEND, document.getDocumentId()).approve("");

        document = WorkflowDocumentFactory.loadDocument(recaller, document.getDocumentId());
        System.err.println(document.getValidActions().getValidActions());
        assertTrue("recaller '" + recaller + "' should be able to RECALL", document.getValidActions().getValidActions().contains(ActionType.RECALL));
        document.recall("recalling", false);

        assertTrue("Document should be saved", document.isSaved());

        // the recaller has a completion request
        assertTrue(document.isCompletionRequested());
        
        // pending approver has FYI
        assertEquals(notifyPendingRecipients, WorkflowDocumentFactory.loadDocument(NATJOHNS, document.getDocumentId()).isFYIRequested());
        // third approver has FYI
        assertEquals(notifyPreviousRecipients, WorkflowDocumentFactory.loadDocument(RKIRKEND, document.getDocumentId()).isFYIRequested());
        // second approver does not have FYI - approver is initiator, FYI is skipped
        assertFalse(WorkflowDocumentFactory.loadDocument(initiator, document.getDocumentId()).isFYIRequested());
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
        for (String user: new String[] { JHOPF, initiator, RKIRKEND, NATJOHNS, BMCGOUGH }) {
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
