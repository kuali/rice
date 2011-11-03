/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.kew.routeheader;

import org.junit.Test;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.test.KEWTestCase;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class AppDocStatusTest extends KEWTestCase {
    	    
    protected void loadTestData() throws Exception {
    	super.loadTestData();
        loadXmlFile("AppDocStatusTestConfig.xml");
    }
        
    /**
     * 
     * This method performs several positive tests related to Application Document Status
     * For these tests the doctype definition defines a valid set of statuses.
     * It also defines two status transitions in the route path
     * It tests:
     * 	- That the AppDocStatus is properly set by the workflow engine during
     *    appropriate transitions.
     *  - That the AppDocStatus may be retrieved by the client API
     *  - That the AppDocStatus may be set by the client API
     *  - That a history of AppDocStatus transitions is created.
     * 
     */
    @Test public void testValidAppDocStatus() throws Exception {
    	// Create document
    	WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName("ewestfal"), "TestAppDocStatusDoc2");
    	document.saveDocumentData();
    	assertNotNull(document.getDocumentId());
    	assertTrue("Document should be initiatied", document.isInitiated());
    	assertTrue("Invalid route level.", document.getNodeNames().contains("Initiated"));
    	
    	// route document to first stop and check status, etc.
    	document.route("Test Routing.");    	
    	String appDocStatus = document.getDocument().getApplicationDocumentStatus();
    	assertTrue("Application Document Status:" + appDocStatus +" is invalid", "Approval in Progress".equalsIgnoreCase(appDocStatus));
        
        // should have generated a request to "bmcgough"
    	document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("bmcgough"), document.getDocumentId());
        assertTrue("Document should be enroute", document.isEnroute());
    	Set<String> nodeNames = document.getNodeNames();
    	assertEquals("Wrong number of node names.", 1, nodeNames.size());
    	assertTrue("Wrong node name.", document.getNodeNames().contains("DestinationApproval"));

    	// check action request
        List<ActionRequest> requests = document.getRootActionRequests();
        assertEquals(1, requests.size());
        ActionRequest request = requests.get(0);
        assertEquals(getPrincipalIdForName("bmcgough"), request.getPrincipalId());
        assertEquals(ActionRequestType.APPROVE, request.getActionRequested());
        assertEquals("DestinationApproval", request.getNodeName());
        assertTrue(document.isApprovalRequested());
        
        // approve the document to send it to its next route node
        document.approve("Test approve by bmcgough");
        
        // check status 
        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("temay"), document.getDocumentId());
        Document rh = document.getDocument();
    	appDocStatus = rh.getApplicationDocumentStatus();
    	assertTrue("Application Document Status:" + appDocStatus +" is invalid", "Submitted".equalsIgnoreCase(appDocStatus));
        
        // should have generated a request to "temay"
    	assertTrue("Document should be enroute", document.isEnroute());
    	nodeNames = document.getNodeNames();
    	assertEquals("Wrong number of node names.", 1, nodeNames.size());
    	assertTrue("Wrong node name.", nodeNames.contains("TravelerApproval"));
    	document.approve("Test approve by temay");
    	
    	// update the AppDocStatus via client API
        document.setApplicationDocumentStatus("Completed");
        document.saveDocumentData();

        // get a refreshed document and check it out
        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("temay"), document.getDocumentId());
//        assertTrue("Document should be processed.", document.isProcessed());        
        rh = document.getDocument();
    	appDocStatus = rh.getApplicationDocumentStatus();
    	assertTrue("Application Document Status:" + appDocStatus +" is invalid", "Completed".equalsIgnoreCase(appDocStatus));
    	
        // check app doc status transition history
        List<org.kuali.rice.kew.api.document.DocumentStatusTransition> history = KewApiServiceLocator.getWorkflowDocumentService().getDocumentStatusTransitionHistory(
                document.getDocumentId());
        
        assertEquals(3, history.size());
    	assertTrue("First History record has incorrect status", "Approval In Progress".equalsIgnoreCase(history.get(0).getNewStatus()));
    	assertTrue("Second History record has incorrect old status", "Approval In Progress".equalsIgnoreCase(
                history.get(1).getOldStatus()));
    	assertTrue("Second History record has incorrect new status", "Submitted".equalsIgnoreCase(history.get(1).getNewStatus()));
    	assertTrue("Third History record has incorrect old status", "Submitted".equalsIgnoreCase(history.get(2).getOldStatus()));
    	assertTrue("Third History record has incorrect new status", "Completed".equalsIgnoreCase(history.get(2).getNewStatus()));
               
    	// TODO when we are able to, we should also verify the RouteNodeInstances are correct
        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("ewestfal"), document.getDocumentId());
    	assertTrue("Document should be final.", document.isFinal());
    }        

    /**
     * 
     * This method is similar to the above test, except that the doctype definition
     * does NOT specify a valid set of values.  This means that the value can be any valid string.
     * 
     * @throws Exception
     */
    @Test public void testAppDocStatusValuesNotDefined() throws Exception {
    	// Create document
    	WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName("ewestfal"), "TestAppDocStatusDoc1");
    	document.saveDocumentData();
    	assertNotNull(document.getDocumentId());
    	assertTrue("Document should be initiatied", document.isInitiated());
    	assertTrue("Invalid route level.", document.getNodeNames().contains("Initiated"));
    	
    	// route document to first stop and check status, etc.
    	document.route("Test Routing.");    	
    	Document rh = document.getDocument();
    	String appDocStatus = rh.getApplicationDocumentStatus();
    	assertTrue("Application Document Status:" + appDocStatus +" is invalid", "Approval in Progress".equalsIgnoreCase(appDocStatus));
        
        // should have generated a request to "bmcgough"
    	document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("bmcgough"), document.getDocumentId());
        assertTrue("Document should be enroute", document.isEnroute());
    	Set<String> nodeNames = document.getNodeNames();
    	assertEquals("Wrong number of node names.", 1, nodeNames.size());
    	assertTrue("Wrong node name.", nodeNames.contains("step1"));

    	// check action request
        List<ActionRequest> requests = document.getRootActionRequests();
        assertEquals(1, requests.size());
        ActionRequest request = requests.get(0);
        assertEquals(getPrincipalIdForName("bmcgough"), request.getPrincipalId());
        assertEquals(ActionRequestType.APPROVE, request.getActionRequested());
        assertEquals("step1", request.getNodeName());
        assertTrue(document.isApprovalRequested());
        
        // approve the document to send it to its next route node
        document.approve("Test approve by bmcgough");
        
        // check status 
        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("temay"), document.getDocumentId());
        rh = document.getDocument();
    	appDocStatus = rh.getApplicationDocumentStatus();
    	assertTrue("Application Document Status:" + appDocStatus +" is invalid", "Submitted".equalsIgnoreCase(appDocStatus));
        
        // should have generated a request to "temay"
    	assertTrue("Document should be enroute", document.isEnroute());
    	nodeNames = document.getNodeNames();
    	assertEquals("Wrong number of node names.", 1, nodeNames.size());
    	assertTrue("Wrong node name.", nodeNames.contains("step2"));
    	document.approve("Test approve by temay");
    	
    	// update the AppDocStatus via client API
        document.setApplicationDocumentStatus("Some Random Value");
        document.saveDocumentData();

        // get a refreshed document and check it out
        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("temay"), document.getDocumentId());
//        assertTrue("Document should be processed.", document.isProcessed());        
        rh = document.getDocument();
    	appDocStatus = rh.getApplicationDocumentStatus();
    	assertTrue("Application Document Status:" + appDocStatus +" is invalid", "Some Random Value".equalsIgnoreCase(appDocStatus));
    	
        // check app doc status transition history
        List<org.kuali.rice.kew.api.document.DocumentStatusTransition> history = KewApiServiceLocator.getWorkflowDocumentService().getDocumentStatusTransitionHistory(
                document.getDocumentId());
        
        assertEquals(3, history.size());
    	assertTrue("First History record has incorrect status", "Approval In Progress".equalsIgnoreCase(history.get(0)
                .getNewStatus()));
    	assertTrue("Second History record has incorrect old status", "Approval In Progress".equalsIgnoreCase(
                history.get(1).getOldStatus()));
    	assertTrue("Second History record has incorrect new status", "Submitted".equalsIgnoreCase(history.get(1)
                .getNewStatus()));
    	assertTrue("Third History record has incorrect old status", "Submitted".equalsIgnoreCase(history.get(2).getOldStatus()));
    	assertTrue("Third History record has incorrect new status", "Some Random Value".equalsIgnoreCase(history.get(2)
                .getNewStatus()));
               
    	// TODO when we are able to, we should also verify the RouteNodeInstances are correct
        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("ewestfal"), document.getDocumentId());
    	assertTrue("Document should be final.", document.isFinal());
    }        

    /**
     * 
     * This test attempts to set an invalid status value for a document that has a valid set
     * of statuses defined.
     * It expects to throw a WorkflowRuntimeException when attempting to set the invalid status value.
     * 
     * @throws Exception
     */
    @Test public void testInvalidAppDocStatusValue() throws Exception {
    	WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName("ewestfal"), "TestAppDocStatusDoc2");
    	document.saveDocumentData();
    	assertNotNull(document.getDocumentId());
    	assertTrue("Document should be initiatied", document.isInitiated());
    	assertTrue("Invalid route level.", document.getNodeNames().contains("Initiated"));
    	    	
    	// update the AppDocStatus via client API
    	boolean gotException = false;
    	try {
    		document.setApplicationDocumentStatus("BAD STATUS");
    		document.saveDocumentData();
    	} catch (Throwable t){
    		gotException = true;
    		WorkflowRuntimeException ex = new WorkflowRuntimeException();
    		assertEquals("WrongExceptionType", t.getClass(), ex.getClass());
    	} finally {
    		assertTrue("Expected WorkflowRuntimeException not thrown.", gotException);
    		
    	}
    }
}
