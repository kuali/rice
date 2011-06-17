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
package org.kuali.rice.kew.clientapp;


import org.junit.Test;

import org.kuali.rice.kew.dto.RouteNodeInstanceDTO;
import org.kuali.rice.kew.dto.UserIdDTO;
import org.kuali.rice.kew.dto.WorkflowIdDTO;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;

import static org.junit.Assert.*;

/**
 * Place to test WorkflowDocument.
 *
 */
public class WorkflowDocumentTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("ClientAppConfig.xml");
    }

    @Test public void testLoadNonExistentDocument() throws Exception {
    	WorkflowDocument document = WorkflowDocument.loadDocument(getPrincipalIdForName("ewestfal"), "123456789");
    	assertNull("RouteHeaderVO should be null.", document.getRouteHeader());
    }

    @Test public void testWorkflowDocument() throws Exception {
        WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "UnitTestDocument");
        document.routeDocument("");

        document = WorkflowDocument.loadDocument(getPrincipalIdForName("ewestfal"), document.getDocumentId());
        document.approve("");

        document = WorkflowDocument.loadDocument(getPrincipalIdForName("jhopf"), document.getDocumentId());
        document.approve("");

        RouteNodeInstanceDTO[] nodeInstances = document.getRouteNodeInstances();
        boolean containsInitiated = false;
        boolean containsTemplate1 = false;
        boolean containsTemplate2 = false;
        for (int j = 0; j < nodeInstances.length; j++) {
            RouteNodeInstanceDTO routeNodeInstance = nodeInstances[j];
            if (routeNodeInstance.getName().equals("Initiated")) {
                containsInitiated = true;
            } else if (routeNodeInstance.getName().equals("Template1")) {
                containsTemplate1 = true;
            } else if (routeNodeInstance.getName().equals("Template2")) {
                containsTemplate2 = true;
            }
        }

        assertTrue("Should have gone through initiated node", containsInitiated);
        assertTrue("Should have gone through template1 node", containsTemplate1);
        assertTrue("Should have gone through template2 node", containsTemplate2);
    }

    /**
     * Test that the document is being updated appropriately after a return to previous call
     *
     * @throws Exception
     */
    @Test public void testReturnToPreviousCorrectlyUpdatingDocumentStatus() throws Exception {

        WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "UnitTestDocument");
        document.routeDocument("");

        document = WorkflowDocument.loadDocument(getPrincipalIdForName("ewestfal"), document.getDocumentId());
        document.returnToPreviousNode("", "Initiated");

        assertFalse("ewestfal should no longer have approval status", document.isApprovalRequested());
        assertFalse("ewestfal should no long have blanket approve status", document.isBlanketApproveCapable());

        //just for good measure
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), document.getDocumentId());
        assertTrue("rkirkend should now have an approve request", document.isApprovalRequested());
    }

    @Test public void testGetPreviousRouteNodeNames() throws Exception {

    	WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "UnitTestDocument");
        document.routeDocument("");

        document = WorkflowDocument.loadDocument(getPrincipalIdForName("ewestfal"), document.getDocumentId());
        document.approve("");

        document = WorkflowDocument.loadDocument(getPrincipalIdForName("jhopf"), document.getDocumentId());
        String[] previousNodeNames = document.getPreviousNodeNames();
        assertEquals("Should have 2 previous Node Names", 2, previousNodeNames.length);
        assertEquals("Last node name should be the first visisted", "Initiated", previousNodeNames[0]);
        assertEquals("First node name should be last node visited", "Template1", previousNodeNames[1]);
        String[] currentNodes = document.getNodeNames();
        assertEquals("Should have 1 current node name", 1, currentNodes.length);
        assertEquals("Current node name incorrect", "Template2", currentNodes[0]);
        document.returnToPreviousNode("", "Template1");
        previousNodeNames = document.getPreviousNodeNames();
        assertEquals("Should have 1 previous Node Name", 1, previousNodeNames.length);
        assertEquals("Previous Node name incorrect", "Initiated", previousNodeNames[0]);

    }

    @Test public void testIsRouteCapable() throws Exception {

    	WorkflowDocument doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "UnitTestDocument");

    	verifyIsRouteCapable(false, getPrincipalIdForName("ewestfal"), doc.getDocumentId());
    	verifyIsRouteCapable(false, "2001", doc.getDocumentId());

    	verifyIsRouteCapable(true, getPrincipalIdForName("rkirkend"), doc.getDocumentId());
    	verifyIsRouteCapable(true, "2002", doc.getDocumentId());

        doc = WorkflowDocument.createDocument(getPrincipalIdForName("rkirkend"), "NonInitiatorCanRouteDocument");

        verifyIsRouteCapable(true, getPrincipalIdForName("ewestfal"), doc.getDocumentId());
        verifyIsRouteCapable(true, "2001", doc.getDocumentId());

        verifyIsRouteCapable(true, getPrincipalIdForName("rkirkend"), doc.getDocumentId());
        verifyIsRouteCapable(true, "2002", doc.getDocumentId());
    }

    private void verifyIsRouteCapable(boolean routeCapable, String userId, String docId) throws Exception {
    	WorkflowDocument doc = WorkflowDocument.loadDocument(userId, docId);
    	assertEquals(routeCapable, doc.isRouteCapable());
    }

}
