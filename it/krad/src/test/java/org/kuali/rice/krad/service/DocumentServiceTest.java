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

package org.kuali.rice.krad.service;

import org.junit.Test;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.doctype.DocumentTypePolicy;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.test.document.AccountRequestDocument;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.location.impl.campus.CampusTypeBo;
import org.kuali.test.KRADTestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * This class tests the DocumentService (currently only getNewDocument is tested).
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class DocumentServiceTest extends KRADTestCase {
    private static DocumentService documentService;
    private static ActionRequestService actionRequestService;

    public DocumentServiceTest() {
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        GlobalVariables.setMessageMap(new MessageMap());
        GlobalVariables.setUserSession(new UserSession("quickstart"));
    }

    @Override
    public void tearDown() throws Exception {
        GlobalVariables.setMessageMap(new MessageMap());
        GlobalVariables.setUserSession(null);
        super.tearDown();
    }

    /**
     * This method tests getNewDocument
     *
     * @throws Exception
     */
    @Test public void testGetNewDocument() throws Exception {
        AccountRequestDocument travelDocument = (AccountRequestDocument) KRADServiceLocatorWeb.getDocumentService().getNewDocument("AccountRequest");
        WorkflowDocument wd =  travelDocument.getDocumentHeader().getWorkflowDocument();

        assertEquals("Initiator should be the current user", wd.getInitiatorPrincipalId(), GlobalVariables.getUserSession().getPerson().getPrincipalId());
    }

    /**
     * This method tests getNewDocument but the initiator is not the current user
     *
     * @throws Exception
     */
    @Test public void testGetNewDocumentDifferentInitiatorThanCurrentUser() throws Exception {
        AccountRequestDocument travelDocument = (AccountRequestDocument) KRADServiceLocatorWeb.getDocumentService().getNewDocument("AccountRequest", "testuser1");
        WorkflowDocument wd =  travelDocument.getDocumentHeader().getWorkflowDocument();

        assertEquals("Initiator should be testuser1", wd.getInitiatorPrincipalId(), "testuser1");
    }

    /**
     * This method tests getNewDocument but the initiator is invalid
     *
     * @throws Exception
     */
    @Test public void testGetNewDocumentInvalidInitiator() throws Exception {
        AccountRequestDocument travelDocument = (AccountRequestDocument) KRADServiceLocatorWeb.getDocumentService().getNewDocument("AccountRequest", "notValidUserAtAll");
        WorkflowDocument wd =  travelDocument.getDocumentHeader().getWorkflowDocument();

        assertEquals("Initiator should be the current user", wd.getInitiatorPrincipalId(), GlobalVariables.getUserSession().getPerson().getPrincipalId());
    }

    /**
     * This method tests superUserDisapproveDocumentWithoutSaving
     *
     * @throws Exception
     */
    @Test
    public void testSuperUserDisapproveDocumentWithoutSaving() throws Exception {
        final String user1PrincipalId = getPrincipalIdForName("user1");
        final String user2PrincipalId = getPrincipalIdForName("user2");

        Document document = getDocumentService().getNewDocument("CampusTypeMaintenanceDocument", "testuser1");

        String documentId = document.getDocumentHeader().getWorkflowDocument().getDocumentId();
        String docTypeName = document.getDocumentHeader().getWorkflowDocument().getDocument().getDocumentTypeName();
        DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByNameCaseInsensitive(docTypeName);

        assertEquals("There should be one policy on the document type", 1, documentType.getPolicies().size());
        for (Map.Entry<DocumentTypePolicy, String> entry: documentType.getPolicies().entrySet()) {
            assertEquals(DocumentTypePolicy.SEND_NOTIFICATION_ON_SU_DISAPPROVE, entry.getKey());
            assertEquals("true", entry.getValue());
        }

        document.getDocumentHeader().setDocumentDescription("Doc Desc");

        Maintainable newMaintainable = ((MaintenanceDocumentBase) document).getNewMaintainableObject();
        CampusTypeBo cc = (CampusTypeBo)newMaintainable.getDataObject();
        cc.setName("New Name - will be disapproved");
        cc.setCode("X");
        ((MaintenanceDocumentBase) document).setNewMaintainableObject(newMaintainable);

        AdHocRouteRecipient routePerson1 = new AdHocRoutePerson();
        routePerson1.setdocumentNumber(documentId);
        routePerson1.setType(AdHocRouteRecipient.PERSON_TYPE);
        routePerson1.setActionRequested(ActionRequestType.APPROVE.getCode());
        routePerson1.setId(user1PrincipalId);

        AdHocRouteRecipient routePerson2 = new AdHocRoutePerson();
        routePerson2.setdocumentNumber(documentId);
        routePerson1.setType(AdHocRouteRecipient.PERSON_TYPE);
        routePerson2.setActionRequested(ActionRequestType.APPROVE.getCode());
        routePerson2.setId(user2PrincipalId);

        List<AdHocRouteRecipient> routeRecipients = new ArrayList<AdHocRouteRecipient>();
        routeRecipients.add(routePerson1);
        routeRecipients.add(routePerson2);

        getDocumentService().routeDocument(document, "route document", routeRecipients);

        WorkflowDocument workflowDocument = WorkflowDocumentFactory.loadDocument("user1", documentId);
        workflowDocument.approve("approve Doc");

        Document updatedDoc = getDocumentService().getByDocumentHeaderId(documentId);

        List<ActionRequestValue> actionRequests = getActionRequestService().findPendingByDoc(documentId);

        assertEquals("There should be one approve action request for user2", 1, actionRequests.size());
        for (ActionRequestValue actionRequest : actionRequests) {
            assertEquals("user2 should be the principal id of the request", "user2", actionRequest.getPrincipalId());
            assertEquals("The action requested should be approve", "A", actionRequest.getActionRequested());
        }

        KRADServiceLocatorWeb.getDocumentService().superUserDisapproveDocumentWithoutSaving(updatedDoc, "Disapprove");

        Document updatedDoc2 = getDocumentService().getByDocumentHeaderId(documentId);

        assertEquals(true, updatedDoc2.getDocumentHeader().getWorkflowDocument().isDisapproved());

        List<ActionRequestValue> actionRequestsAfterDisapprove = getActionRequestService().findPendingByDoc(documentId);

        assertEquals("There should be 2 action requests", 2, actionRequestsAfterDisapprove.size());
        for (ActionRequestValue actionRequest : actionRequestsAfterDisapprove) {
            if (!(actionRequest.getPrincipalId().equals("testuser1") ||
                    (actionRequest.getPrincipalId().equals("user1")))) {
                fail("The principal ID on the action request should be testuser1 or user1.  Principal Id: " +
                        actionRequest.getPrincipalId());
            }
            assertEquals("Action requested should be an acknowledgment request", "K",
                    actionRequest.getActionRequested());
        }
    }

    protected String getPrincipalIdForName(String principalName) {
        return KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(principalName).getPrincipalId();
    }

    public DocumentService getDocumentService() {
        if ( documentService == null ) {
            documentService = KRADServiceLocatorWeb.getDocumentService();
        }
        return documentService;
    }

    public ActionRequestService getActionRequestService() {
        if ( actionRequestService == null ) {
            actionRequestService = KEWServiceLocator.getActionRequestService();
        }
        return actionRequestService;
    }
}
