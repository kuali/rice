/*
 * Copyright 2007-2010 The Kuali Foundation
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
package org.kuali.rice.kew.routeheader;


import org.junit.Test;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.DocumentStatusTransitionDTO;

import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWConstants;

import static org.junit.Assert.*;


public class AppDocStatusTest extends KEWTestCase {
    
    
    private static final String DOCUMENT_TYPE_NAME = "TestAppDocStatusDoc2";
	private static final String ADHOC_NODE = "AdHoc";
	private static final String WORKFLOW_DOCUMENT_NODE = "WorkflowDocument";
    private static final String ACKNOWLEDGE_1_NODE = "Acknowledge1";
    private static final String ACKNOWLEDGE_2_NODE = "Acknowledge2";
	    
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
    	WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), "TestAppDocStatusDoc2");
    	document.saveRoutingData();
    	assertNotNull(document.getDocumentId());
    	assertTrue("Document should be initiatied", document.stateIsInitiated());
    	assertEquals("Invalid route level.", new Integer(0), document.getRouteHeader().getDocRouteLevel());
    	
    	// route document to first stop and check status, etc.
    	document.routeDocument("Test Routing.");    	
    	RouteHeaderDTO rh = document.getRouteHeader();
    	String appDocStatus = rh.getAppDocStatus();
    	assertTrue("Application Document Status:" + appDocStatus +" is invalid", "Approval in Progress".equalsIgnoreCase(appDocStatus));
        
        // should have generated a request to "bmcgough"
    	document = WorkflowDocument.loadDocument(getPrincipalIdForName("bmcgough"), document.getDocumentId());
        assertTrue("Document should be enroute", document.stateIsEnroute());
    	assertEquals("Invalid route level.", new Integer(1), document.getRouteHeader().getDocRouteLevel());
    	String[] nodeNames = document.getNodeNames();
    	assertEquals("Wrong number of node names.", 1, nodeNames.length);
    	assertEquals("Wrong node name.", "DestinationApproval", nodeNames[0]);

    	// check action request
        ActionRequestDTO[] requests = document.getActionRequests();
        assertEquals(1, requests.length);
        ActionRequestDTO request = requests[0];
        assertEquals(getPrincipalIdForName("bmcgough"), request.getPrincipalId());
        assertEquals(KEWConstants.ACTION_REQUEST_APPROVE_REQ, request.getActionRequested());
        assertEquals(new Integer(1), request.getRouteLevel());
        assertTrue(document.isApprovalRequested());
        
        // approve the document to send it to its next route node
        document.approve("Test approve by bmcgough");
        
        // check status 
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("temay"), document.getDocumentId());
        rh = document.getRouteHeader();
    	appDocStatus = rh.getAppDocStatus();
    	assertTrue("Application Document Status:" + appDocStatus +" is invalid", "Submitted".equalsIgnoreCase(appDocStatus));
        
        // should have generated a request to "temay"
    	assertTrue("Document should be enroute", document.stateIsEnroute());
    	assertEquals("Invalid route level.", new Integer(2), document.getRouteHeader().getDocRouteLevel());
    	nodeNames = document.getNodeNames();
    	assertEquals("Wrong number of node names.", 1, nodeNames.length);
    	assertEquals("Wrong node name.", "TravelerApproval", nodeNames[0]);
    	document.approve("Test approve by temay");
    	
    	// update the AppDocStatus via client API
        document.updateAppDocStatus("Completed");

        // get a refreshed document and check it out
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("temay"), document.getDocumentId());
//        assertTrue("Document should be processed.", document.stateIsProcessed());        
        rh = document.getRouteHeader();
    	appDocStatus = rh.getAppDocStatus();
    	assertTrue("Application Document Status:" + appDocStatus +" is invalid", "Completed".equalsIgnoreCase(appDocStatus));
    	
        // check app doc status transition history
        WorkflowInfo info = new WorkflowInfo();
        DocumentStatusTransitionDTO[] history = info.getDocumentStatusTransitionHistory(document.getDocumentId());
        
        assertEquals(3, history.length);
    	assertTrue("First History record has incorrect status", "Approval In Progress".equalsIgnoreCase(history[0].getNewAppDocStatus()));
    	assertTrue("Second History record has incorrect old status", "Approval In Progress".equalsIgnoreCase(history[1].getOldAppDocStatus()));
    	assertTrue("Second History record has incorrect new status", "Submitted".equalsIgnoreCase(history[1].getNewAppDocStatus()));
    	assertTrue("Third History record has incorrect old status", "Submitted".equalsIgnoreCase(history[2].getOldAppDocStatus()));
    	assertTrue("Third History record has incorrect new status", "Completed".equalsIgnoreCase(history[2].getNewAppDocStatus()));
               
    	// TODO when we are able to, we should also verify the RouteNodeInstances are correct
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("ewestfal"), document.getDocumentId());
    	assertTrue("Document should be final.", document.stateIsFinal());
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
    	WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), "TestAppDocStatusDoc1");
    	document.saveRoutingData();
    	assertNotNull(document.getDocumentId());
    	assertTrue("Document should be initiatied", document.stateIsInitiated());
    	assertEquals("Invalid route level.", new Integer(0), document.getRouteHeader().getDocRouteLevel());
    	
    	// route document to first stop and check status, etc.
    	document.routeDocument("Test Routing.");    	
    	RouteHeaderDTO rh = document.getRouteHeader();
    	String appDocStatus = rh.getAppDocStatus();
    	assertTrue("Application Document Status:" + appDocStatus +" is invalid", "Approval in Progress".equalsIgnoreCase(appDocStatus));
        
        // should have generated a request to "bmcgough"
    	document = WorkflowDocument.loadDocument(getPrincipalIdForName("bmcgough"), document.getDocumentId());
        assertTrue("Document should be enroute", document.stateIsEnroute());
    	assertEquals("Invalid route level.", new Integer(1), document.getRouteHeader().getDocRouteLevel());
    	String[] nodeNames = document.getNodeNames();
    	assertEquals("Wrong number of node names.", 1, nodeNames.length);
    	assertEquals("Wrong node name.", "step1", nodeNames[0]);

    	// check action request
        ActionRequestDTO[] requests = document.getActionRequests();
        assertEquals(1, requests.length);
        ActionRequestDTO request = requests[0];
        assertEquals(getPrincipalIdForName("bmcgough"), request.getPrincipalId());
        assertEquals(KEWConstants.ACTION_REQUEST_APPROVE_REQ, request.getActionRequested());
        assertEquals(new Integer(1), request.getRouteLevel());
        assertTrue(document.isApprovalRequested());
        
        // approve the document to send it to its next route node
        document.approve("Test approve by bmcgough");
        
        // check status 
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("temay"), document.getDocumentId());
        rh = document.getRouteHeader();
    	appDocStatus = rh.getAppDocStatus();
    	assertTrue("Application Document Status:" + appDocStatus +" is invalid", "Submitted".equalsIgnoreCase(appDocStatus));
        
        // should have generated a request to "temay"
    	assertTrue("Document should be enroute", document.stateIsEnroute());
    	assertEquals("Invalid route level.", new Integer(2), document.getRouteHeader().getDocRouteLevel());
    	nodeNames = document.getNodeNames();
    	assertEquals("Wrong number of node names.", 1, nodeNames.length);
    	assertEquals("Wrong node name.", "step2", nodeNames[0]);
    	document.approve("Test approve by temay");
    	
    	// update the AppDocStatus via client API
        document.updateAppDocStatus("Some Random Value");

        // get a refreshed document and check it out
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("temay"), document.getDocumentId());
//        assertTrue("Document should be processed.", document.stateIsProcessed());        
        rh = document.getRouteHeader();
    	appDocStatus = rh.getAppDocStatus();
    	assertTrue("Application Document Status:" + appDocStatus +" is invalid", "Some Random Value".equalsIgnoreCase(appDocStatus));
    	
        // check app doc status transition history
        WorkflowInfo info = new WorkflowInfo();
        DocumentStatusTransitionDTO[] history = info.getDocumentStatusTransitionHistory(document.getDocumentId());
        
        assertEquals(3, history.length);
    	assertTrue("First History record has incorrect status", "Approval In Progress".equalsIgnoreCase(history[0].getNewAppDocStatus()));
    	assertTrue("Second History record has incorrect old status", "Approval In Progress".equalsIgnoreCase(history[1].getOldAppDocStatus()));
    	assertTrue("Second History record has incorrect new status", "Submitted".equalsIgnoreCase(history[1].getNewAppDocStatus()));
    	assertTrue("Third History record has incorrect old status", "Submitted".equalsIgnoreCase(history[2].getOldAppDocStatus()));
    	assertTrue("Third History record has incorrect new status", "Some Random Value".equalsIgnoreCase(history[2].getNewAppDocStatus()));
               
    	// TODO when we are able to, we should also verify the RouteNodeInstances are correct
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("ewestfal"), document.getDocumentId());
    	assertTrue("Document should be final.", document.stateIsFinal());
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
    	WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), "TestAppDocStatusDoc2");
    	document.saveRoutingData();
    	assertNotNull(document.getDocumentId());
    	assertTrue("Document should be initiatied", document.stateIsInitiated());
    	assertEquals("Invalid route level.", new Integer(0), document.getRouteHeader().getDocRouteLevel());
    	    	
    	// update the AppDocStatus via client API
    	boolean gotException = false;
    	try {
    		document.updateAppDocStatus("BAD STATUS");
    	} catch (Throwable t){
    		gotException = true;
    		WorkflowRuntimeException ex = new WorkflowRuntimeException();
    		assertEquals("WrongExceptionType", t.getClass(), ex.getClass());
    	} finally {
    		assertTrue("Expected WorkflowRuntimeException not thrown.", gotException);
    		
    	}
    }
}
