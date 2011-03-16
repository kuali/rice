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
package org.kuali.rice.kew.engine;


import org.junit.Test;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;

import org.kuali.rice.kew.dto.RouteNodeInstanceDTO;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWConstants;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class SubProcessRoutingTest extends KEWTestCase {
    
    private static final String DOCUMENT_TYPE_NAME = "SubProcessDocType";
	private static final String SUB_PROCESS_NODE = "MySubProcess";
    private static final String ACKNOWLEDGE_NODE = "Acknowledge";
    private static final String APPROVE_NODE = "Approve";
	
    protected void loadTestData() throws Exception {
        loadXmlFile("EngineConfig.xml");
    }

    @Test public void testSubProcessRoute() throws Exception {
    	WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), DOCUMENT_TYPE_NAME);
    	document.saveRoutingData();
        assertTrue("Document should be initiated", document.stateIsInitiated());
        assertEquals("Should be no action requests.", 0, document.getActionRequests().length);
        assertEquals("Invalid route level.", new Integer(0), document.getRouteHeader().getDocRouteLevel());
        document.routeDocument("");
        assertTrue("Document shoule be ENROUTE.", document.stateIsEnroute());
        
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertEquals("Incorrect pending action requests.", 2, actionRequests.size());
        boolean isAck = false;
        boolean isApprove = false;
        for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue request = (ActionRequestValue) iterator.next();
            RouteNodeInstance nodeInstance = request.getNodeInstance();
            assertNotNull("Node instance should be non null.", nodeInstance);
            if (request.getPrincipalId().equals(getPrincipalIdForName("bmcgough"))) {
                isAck = true;
                assertEquals("Wrong request type.", KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, request.getActionRequested());
                assertEquals("Wrong node.", ACKNOWLEDGE_NODE, nodeInstance.getRouteNode().getRouteNodeName());
                assertNotNull("Should be in a sub process.", nodeInstance.getProcess());
                assertEquals("Wrong sub process.", SUB_PROCESS_NODE, nodeInstance.getProcess().getRouteNode().getRouteNodeName());
                assertFalse("Sub process should be non-initial.", nodeInstance.getProcess().isInitial());
                assertFalse("Sub process should be non-active.", nodeInstance.getProcess().isActive());
                assertFalse("Sub process should be non-complete.", nodeInstance.getProcess().isComplete());
            } else if (request.getPrincipalId().equals(getPrincipalIdForName("temay"))) {
                isApprove = true;
                assertEquals("Wrong request type.", KEWConstants.ACTION_REQUEST_APPROVE_REQ, request.getActionRequested());
                assertEquals("Wrong node.", APPROVE_NODE, request.getNodeInstance().getRouteNode().getRouteNodeName());
                assertNotNull("Should be in a sub process.", request.getNodeInstance().getProcess());
                assertEquals("Wrong sub process.", SUB_PROCESS_NODE, request.getNodeInstance().getProcess().getRouteNode().getRouteNodeName());
                assertFalse("Sub process should be non-initial.", nodeInstance.getProcess().isInitial());
                assertFalse("Sub process should be non-active.", nodeInstance.getProcess().isActive());
                assertFalse("Sub process should be non-complete.", nodeInstance.getProcess().isComplete());
            }
        }
        assertTrue(isAck);
        assertTrue(isApprove);
        document = new WorkflowDocument(getPrincipalIdForName("bmcgough"), document.getRouteHeaderId());
        assertTrue("Should have acknowledge.", document.isAcknowledgeRequested());
        document.acknowledge("");
        
        document = new WorkflowDocument(getPrincipalIdForName("temay"), document.getRouteHeaderId());
        document.approve("");
        
        // find the subprocess and assert it is complete, not active, and not initial
        boolean foundSubProcess = false;
        RouteNodeInstanceDTO[] nodeInstances = document.getRouteNodeInstances();
        for (int index = 0; index < nodeInstances.length; index++) {
            RouteNodeInstanceDTO instanceVO = nodeInstances[index];
            if (instanceVO.getName().equals(SUB_PROCESS_NODE)) {
                foundSubProcess = true;
                assertFalse("Sub process should be non-initial.", instanceVO.isInitial());
                assertFalse("Sub process should be non-active.", instanceVO.isActive());
                assertTrue("Sub process should be complete.", instanceVO.isComplete());
            }
        }
        assertTrue("Could not locate sub process node.", foundSubProcess);
        
        document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
        document.approve("");
        
        document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), document.getRouteHeaderId());
        assertTrue("Document should be final.", document.stateIsFinal());
    }
    
}
