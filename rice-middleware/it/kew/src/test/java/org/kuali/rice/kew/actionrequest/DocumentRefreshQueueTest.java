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
package org.kuali.rice.kew.actionrequest;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.document.DocumentRefreshQueue;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;

/**
 * Tests the reference implementation of the {@link DocumentRefreshQueue}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentRefreshQueueTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionRequestsConfig.xml");
    }

    private static final String SEQ_DOCUMENT_TYPE_NAME = "DRSeqDocType";
    private static final String PAR_DOCUMENT_TYPE_NAME = "DRParDocType";

    /**
     * Tests document requeueing at a single node.
     *
     * @throws Exception encountered during testing
     */
    @Test
    public void testDocumentRequeueSingleNode() throws Exception {
        String initiatorPrincipalId = getPrincipalIdForName("ewestfal");
        String firstApproverPrincipalId = getPrincipalIdForName("bmcgough");
        String secondApproverPrincipalId = getPrincipalIdForName("rkirkend");

        // Create and route a document
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(initiatorPrincipalId, SEQ_DOCUMENT_TYPE_NAME);
        String documentNumber = document.getDocumentId();
        document.route("");
        document = WorkflowDocumentFactory.loadDocument(initiatorPrincipalId, documentNumber);
        assertTrue(document.isEnroute());
        assertEquals("Wrong number of requests", 2, document.getRootActionRequests().size());

        // Get all of the request ids from the initial action requests
        Set<String> initialRequestIds = new HashSet<String>();

        for (ActionRequest request : document.getRootActionRequests()) {
            initialRequestIds.add(request.getId());
        }

        // Requeue the document
        DocumentRouteHeaderValue documentRouteHeader
                = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentNumber);
        DocumentRefreshQueue documentRequeuer = KewApiServiceLocator.getDocumentRequeuerService(
                documentRouteHeader.getDocumentType().getApplicationId(), documentNumber, 0);
        documentRequeuer.refreshDocument(documentNumber);

        // Load the document for the first approver, check its state, and approve
        document = WorkflowDocumentFactory.loadDocument(firstApproverPrincipalId, documentNumber);
        assertTrue(document.isEnroute());
        assertEquals("Wrong number of requests", 2, document.getRootActionRequests().size());

        for (ActionRequest request : document.getRootActionRequests()) {
            assertTrue("Request ids should be different", !initialRequestIds.contains(request.getId()));
        }

        assertTrue(document.isApprovalRequested());
        document.approve("");

        // Requeue the document again
        documentRequeuer.refreshDocument(document.getDocumentId());

        // Load the document for the second approver and make sure that there is only one request to the second approver
        // and that all other requests are complete
        document = WorkflowDocumentFactory.loadDocument(secondApproverPrincipalId, documentNumber);
        assertTrue(document.isEnroute());
        assertEquals("Wrong number of requests", 2, document.getRootActionRequests().size());

        boolean pendingToSecondApprover = false;

        for (ActionRequest request : document.getRootActionRequests()) {
            if (request.getPrincipalId().equals(secondApproverPrincipalId) && request.isActivated()) {
                assertFalse("Second approver has too many requests", pendingToSecondApprover);
                pendingToSecondApprover = true;
            } else {
                assertTrue("Previous requests to all others should be done", request.isDone());
            }
        }

        assertTrue(document.isApprovalRequested());
    }

    /**
     * Tests document requeueing at multiple nodes.
     *
     * @throws Exception encountered during testing
     */
    @Test
    public void testDocumentRequeueMultipleNodes() throws Exception {
        String initiatorPrincipalId = getPrincipalIdForName("ewestfal");
        String firstApproverPrincipalId = getPrincipalIdForName("bmcgough");
        String secondApproverPrincipalId = getPrincipalIdForName("rkirkend");
        String thirdApproverPrincipalId = getPrincipalIdForName("pmckown");

        // Create and route a document
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(initiatorPrincipalId, PAR_DOCUMENT_TYPE_NAME);
        String documentNumber = document.getDocumentId();
        document.route("");
        document = WorkflowDocumentFactory.loadDocument(initiatorPrincipalId, documentNumber);
        assertTrue(document.isEnroute());
        assertEquals("Wrong number of requests", 3, document.getRootActionRequests().size());

        // Get all of the request ids from the initial action requests
        Set<String> initialRequestIds = new HashSet<String>();

        for (ActionRequest request : document.getRootActionRequests()) {
            initialRequestIds.add(request.getId());
        }

        // Requeue the document
        DocumentRouteHeaderValue documentRouteHeader
                = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentNumber);
        DocumentRefreshQueue documentRequeuer = KewApiServiceLocator.getDocumentRequeuerService(
                documentRouteHeader.getDocumentType().getApplicationId(), documentNumber, 0);
        documentRequeuer.refreshDocument(documentNumber);

        // Load the document for the first approver, check its state, and approve
        document = WorkflowDocumentFactory.loadDocument(firstApproverPrincipalId, documentNumber);
        assertTrue(document.isEnroute());
        assertEquals("Wrong number of requests", 3, document.getRootActionRequests().size());

        for (ActionRequest request : document.getRootActionRequests()) {
            assertTrue("Request ids should be different", !initialRequestIds.contains(request.getId()));
        }

        assertTrue(document.isApprovalRequested());
        document.approve("");

        // Requeue the document again
        documentRequeuer.refreshDocument(document.getDocumentId());

        // Load the document for the second approver and make sure that there is only one request to the second approver
        document = WorkflowDocumentFactory.loadDocument(secondApproverPrincipalId, documentNumber);
        assertTrue(document.isEnroute());
        assertEquals("Wrong number of requests", 3, document.getRootActionRequests().size());

        boolean pendingToSecondApprover = false;

        for (ActionRequest request : document.getRootActionRequests()) {
            if (request.getPrincipalId().equals(secondApproverPrincipalId) && request.isActivated()) {
                assertFalse("Second approver has too many requests.", pendingToSecondApprover);
                pendingToSecondApprover = true;
            }
        }

        assertTrue(document.isApprovalRequested());
        document.approve("");

        // Requeue the document again
        documentRequeuer.refreshDocument(document.getDocumentId());

        // Load the document for the third approver and make sure that there is only one request to the third approver
        // and that all other requests are complete
        document = WorkflowDocumentFactory.loadDocument(thirdApproverPrincipalId, documentNumber);
        assertTrue(document.isEnroute());
        assertEquals("Wrong number of requests", 3, document.getRootActionRequests().size());

        boolean pendingToThirdApprover = false;

        for (ActionRequest request : document.getRootActionRequests()) {
            if (request.getPrincipalId().equals(thirdApproverPrincipalId) && request.isActivated()) {
                assertFalse("Third approver has too many requests.", pendingToThirdApprover);
                pendingToThirdApprover = true;
            } else {
                assertTrue("Previous requests to all others should be done.", request.isDone());
            }
        }

        assertTrue(document.isApprovalRequested());
        document.approve("");
    }

}