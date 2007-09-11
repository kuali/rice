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


import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import mocks.MockEmailNotificationService;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestService;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.engine.node.RouteNodeService;
import edu.iu.uis.eden.exception.InvalidActionTakenException;
import edu.iu.uis.eden.test.TestUtilities;

public class BlanketApproveTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }
    
    /**
     * When a user is not in the blanket approver workgroup an exception should be thrown and 
     * it should have a good message.
     * 
     * @throws Exception
     */
    @Test public void testBlanketApproverNotInBlanketApproverWorkgroup() throws Exception  {
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), SequentialSetup.DOCUMENT_TYPE_NAME);
    	try {
    		document.blanketApprove("");
    		fail("InvalidActionTakenException should have been thrown");
    	} catch (InvalidActionTakenException iate) {
    		assertEquals("Exception on message is incorrent", "User is not authorized to BlanketApprove document", iate.getMessage());
    	}
        
    }
    
    /**
     * When a user is in the blanket approve workgroup but the user is not the initiator an exception
     * should be thrown.
     */
    @Test public void testBlanketApproverNotInitiator() throws Exception  {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), SequentialSetup.DOCUMENT_TYPE_NAME);
        WorkflowDocument newDocument = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
        try {
            newDocument.blanketApprove("");
            fail("Exception should have been thrown when non-initiator user attempts blanket approve on default blanket approve policy document");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("deprecation")
	@Test public void testBlanketApproveSequential() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), SequentialSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("");
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
        assertTrue("Document should be processed.", document.stateIsProcessed());
        Collection nodeInstances = getRouteNodeService().getActiveNodeInstances(document.getRouteHeaderId());
        // once the document is processed there are no active nodes
        assertEquals("Wrong number of active nodes.", 0, nodeInstances.size());
        nodeInstances = getRouteNodeService().getTerminalNodeInstances(document.getRouteHeaderId());
        assertEquals("Wrong number of active nodes.", 1, nodeInstances.size());
        RouteNodeInstance ackNodeInstance = (RouteNodeInstance)nodeInstances.iterator().next();
        assertEquals("At wrong node.", SequentialSetup.ACKNOWLEDGE_2_NODE, ackNodeInstance.getRouteNode().getRouteNodeName());
        assertTrue("Node should be complete.", ackNodeInstance.isComplete());
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertEquals("Wrong number of pending action requests.", 5, actionRequests.size());
        boolean isNotification1 = false;
        boolean isNotification2 = false;
        boolean isNotification3 = false;
        boolean isAck1 = false;
        boolean isAck2 = false;
        for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
            assertEquals("Should only be acknowledges.", EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, actionRequest.getActionRequested());
            RouteNodeInstance nodeInstance = actionRequest.getNodeInstance();
            assertNotNull(nodeInstance);
            String nodeName = nodeInstance.getRouteNode().getRouteNodeName();
            if (actionRequest.getWorkflowUser().getAuthenticationUserId().getId().equals("bmcgough")) {
                isNotification1 = true;
                assertEquals(SequentialSetup.WORKFLOW_DOCUMENT_NODE, nodeName);
                assertEquals(EdenConstants.MACHINE_GENERATED_RESPONSIBILITY_ID, actionRequest.getResponsibilityId());
            } else if (actionRequest.getWorkflowUser().getAuthenticationUserId().getId().equals("rkirkend")) {
                isNotification2 = true;
                assertEquals(SequentialSetup.WORKFLOW_DOCUMENT_NODE, nodeName);
                assertEquals(EdenConstants.MACHINE_GENERATED_RESPONSIBILITY_ID, actionRequest.getResponsibilityId());
            } else if (actionRequest.getWorkflowUser().getAuthenticationUserId().getId().equals("pmckown")) {
                isNotification3 = true;
                assertEquals(SequentialSetup.WORKFLOW_DOCUMENT_2_NODE, nodeName);
                assertEquals(EdenConstants.MACHINE_GENERATED_RESPONSIBILITY_ID, actionRequest.getResponsibilityId());
            } else if (actionRequest.getWorkflowUser().getAuthenticationUserId().getId().equals("temay")) {
                isAck1 = true;
                assertEquals(SequentialSetup.ACKNOWLEDGE_1_NODE, nodeName);
                assertFalse(EdenConstants.MACHINE_GENERATED_RESPONSIBILITY_ID.equals(actionRequest.getResponsibilityId()));
            } else if (actionRequest.getWorkflowUser().getAuthenticationUserId().getId().equals("jhopf")) {
                isAck2 = true;
                assertEquals(SequentialSetup.ACKNOWLEDGE_2_NODE, nodeName);
                assertFalse(EdenConstants.MACHINE_GENERATED_RESPONSIBILITY_ID.equals(actionRequest.getResponsibilityId()));
            }
        }
        assertTrue(isNotification1);
        assertTrue(isNotification2);
        assertTrue(isNotification3);
        assertTrue(isAck1);
        assertTrue(isAck2);
        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        assertTrue(document.stateIsProcessed());
        assertTrue(document.isAcknowledgeRequested());
        assertEquals("bmcgough should not have been sent an approve email", 0, getMockEmailService().emailsSent("bmcgough", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
        assertEquals("bmcgough should not have been sent an ack email", 1, getMockEmailService().emailsSent("bmcgough", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));
        document.acknowledge("");

        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertTrue(document.stateIsProcessed());
        assertTrue(document.isAcknowledgeRequested());
        assertEquals("rkirkend should not have been sent an approve email", 0, getMockEmailService().emailsSent("rkirkend", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));        
        assertEquals("rkirkend should not have been sent an ack email", 1, getMockEmailService().emailsSent("rkirkend", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));
        document.acknowledge("");
        
        document = new WorkflowDocument(new NetworkIdVO("pmckown"), document.getRouteHeaderId());
        assertTrue(document.stateIsProcessed());
        assertTrue(document.isAcknowledgeRequested());
        assertEquals("pmckown should not have been sent an approve email", 0, getMockEmailService().emailsSent("pmckown", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_APPROVE_REQ));
        assertEquals("pmckown should not have been sent an ack email", 1, getMockEmailService().emailsSent("pmckown", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));
        document.acknowledge("");
        
        document = new WorkflowDocument(new NetworkIdVO("temay"), document.getRouteHeaderId());
        assertTrue(document.stateIsProcessed());
        assertTrue(document.isAcknowledgeRequested());
        assertEquals("rkirkend should have been sent an temay", 1, getMockEmailService().emailsSent("temay", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));        
        document.acknowledge("");
        
        document = new WorkflowDocument(new NetworkIdVO("jhopf"), document.getRouteHeaderId());
        assertTrue(document.stateIsProcessed());
        assertTrue(document.isAcknowledgeRequested());
        assertEquals("rkirkend should have been sent an jhopf", 1, getMockEmailService().emailsSent("jhopf", document.getRouteHeaderId(), EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));
        document.acknowledge("");
        
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
        assertTrue(document.stateIsFinal());
        
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), SequentialSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("", SequentialSetup.WORKFLOW_DOCUMENT_2_NODE);
        assertTrue("Document should be enroute.", document.stateIsEnroute());
        nodeInstances = getRouteNodeService().getActiveNodeInstances(document.getRouteHeaderId());
        assertEquals("Should be one active node.", 1, nodeInstances.size());
        RouteNodeInstance doc2Instance = (RouteNodeInstance)nodeInstances.iterator().next();
        assertEquals("At wrong node.", SequentialSetup.WORKFLOW_DOCUMENT_2_NODE, doc2Instance.getRouteNode().getRouteNodeName());
        
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), SequentialSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("", new Integer(2));
        assertTrue("Document should be enroute.", document.stateIsEnroute());
        nodeInstances = getRouteNodeService().getActiveNodeInstances(document.getRouteHeaderId());
        assertEquals("Should be one active node.", 1, nodeInstances.size());
        doc2Instance = (RouteNodeInstance)nodeInstances.iterator().next();
        assertEquals("At wrong node.", SequentialSetup.WORKFLOW_DOCUMENT_2_NODE, doc2Instance.getRouteNode().getRouteNodeName());
        
    }
    
    @Test public void testBlanketApproveSequentialErrors() throws Exception {
        // blanket approve to invalid node
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), SequentialSetup.DOCUMENT_TYPE_NAME);
        try {
            document.blanketApprove("", "TotallyInvalidNode");
            fail("Should have thrown exception");
        } catch (Exception e) {}
        
        // blanket approve backwards
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), SequentialSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("", SequentialSetup.WORKFLOW_DOCUMENT_2_NODE);
        try {
            document.blanketApprove("", SequentialSetup.WORKFLOW_DOCUMENT_NODE);
            fail("Should have thrown exception");
        } catch (Exception e) {}
        
        // blanket approve to current node
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), SequentialSetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        try {
            document.blanketApprove("", SequentialSetup.WORKFLOW_DOCUMENT_NODE);
            fail("Should have thrown exception");
        } catch (Exception e) {}
        
        // blanket approve as user not in the blanket approve workgroup
        document = new WorkflowDocument(new NetworkIdVO("user1"), SequentialSetup.DOCUMENT_TYPE_NAME);
        try {
            document.blanketApprove("");
            fail("Shouldn't be able to blanket approve if not in blanket approve workgroup");
        } catch (Exception e) {}
    }
    
    @Test public void testBlanketApproveParallel() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), ParallelSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("");        
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
        assertTrue("Document should be processed.", document.stateIsProcessed());
        Collection nodeInstances = getRouteNodeService().getActiveNodeInstances(document.getRouteHeaderId());
        // once the document has gone processed there are no active nodes
        assertEquals("Wrong number of active nodes.", 0, nodeInstances.size());
        nodeInstances = getRouteNodeService().getTerminalNodeInstances(document.getRouteHeaderId());
        assertEquals("Wrong number of terminal nodes.", 1, nodeInstances.size());
        RouteNodeInstance ackNodeInstance = (RouteNodeInstance)nodeInstances.iterator().next();
        assertEquals("At wrong node.", SequentialSetup.ACKNOWLEDGE_2_NODE, ackNodeInstance.getRouteNode().getRouteNodeName());
        assertTrue("Node should be complete.", ackNodeInstance.isComplete());
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertEquals("Wrong number of pending action requests.", 10, actionRequests.size());

    }
    
    @Test public void testBlanketApproveIntoBranch() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), ParallelSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("", ParallelSetup.WORKFLOW_DOCUMENT_2_B1_NODE);
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertEquals("Wrong number of pending action requests.", 5, actionRequests.size());
        
        // document should now be at the node we blanket approved to and the join node
        Collection activeNodes = getRouteNodeService().getActiveNodeInstances(document.getRouteHeaderId());
        assertEquals("Wrong number of active nodes.", 3, activeNodes.size());
        boolean isAtWD2B1 = false;
        boolean isAtJoin = false;
        boolean isAtWD3B2 = false;
        boolean isAtWD4B3 = false;
        for (Iterator iterator = activeNodes.iterator(); iterator.hasNext();) {
            RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
            isAtWD2B1 = isAtWD2B1 || nodeInstance.getName().equals(ParallelSetup.WORKFLOW_DOCUMENT_2_B1_NODE);
            isAtWD3B2 = isAtWD3B2 || nodeInstance.getName().equals(ParallelSetup.WORKFLOW_DOCUMENT_3_B2_NODE);
            isAtWD4B3 = isAtWD4B3 || nodeInstance.getName().equals(ParallelSetup.WORKFLOW_DOCUMENT_4_B3_NODE);
            isAtJoin = isAtJoin || nodeInstance.getName().equals(ParallelSetup.JOIN_NODE);
        }
        assertTrue("Should be at blanket approved node.", isAtWD2B1);
        assertTrue("Should be at blanket approved node WD3B2.", isAtWD3B2);
        assertTrue("Should be at blanket approved node WD4B3.", isAtWD4B3);        
        assertFalse("Should be at join node.", isAtJoin);
        
        document.blanketApprove("");
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
        assertTrue("Document should be processed.", document.stateIsProcessed());
        actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        
        assertEquals("Wrong number of pending action requests.", 10, actionRequests.size());
    }
    
    @Test public void testBlanketApproveToMultipleNodes() throws Exception {
        
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), ParallelSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("", new String[] { ParallelSetup.WORKFLOW_DOCUMENT_2_B1_NODE, ParallelSetup.WORKFLOW_DOCUMENT_3_B2_NODE });
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertEquals("Wrong number of pending action requests.", 5, actionRequests.size());
        
        // document should now be at both nodes we blanket approved to and the join node
        Collection activeNodes = getRouteNodeService().getActiveNodeInstances(document.getRouteHeaderId());
        assertEquals("Wrong number of active nodes.", 3, activeNodes.size());
        boolean isAtWD2B1 = false;
        boolean isAtWD3B2 = false;
        boolean isAtJoin = false;
        boolean isAtWD4B3 = false;
        for (Iterator iterator = activeNodes.iterator(); iterator.hasNext();) {
            RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
            isAtWD2B1 = isAtWD2B1 || nodeInstance.getName().equals(ParallelSetup.WORKFLOW_DOCUMENT_2_B1_NODE);
            isAtWD3B2 = isAtWD3B2 || nodeInstance.getName().equals(ParallelSetup.WORKFLOW_DOCUMENT_3_B2_NODE);
            isAtWD4B3 = isAtWD4B3 || nodeInstance.getName().equals(ParallelSetup.WORKFLOW_DOCUMENT_4_B3_NODE);
            isAtJoin = isAtJoin || nodeInstance.getName().equals(ParallelSetup.JOIN_NODE);
        }
        assertTrue("Should be at blanket approved node WD2B1.", isAtWD2B1);
        assertTrue("Should be at blanket approved node WD3B2.", isAtWD3B2);
        assertTrue("Should be at blanket approved node WD4B3.", isAtWD4B3);
        assertFalse("Should not be at join node.", isAtJoin);
        
        document.blanketApprove("");
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
        assertTrue("Document should be processed.", document.stateIsProcessed());
        actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertEquals("Wrong number of pending action requests.", 10, actionRequests.size());
    }
    
    @Test public void testBlanketApproveToJoin() throws Exception {
        
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), ParallelSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("", ParallelSetup.JOIN_NODE);
        assertTrue("Document should still be enroute.", document.stateIsEnroute());

        // document should now be at the workflow document final node
        Collection activeNodes = getRouteNodeService().getActiveNodeInstances(document.getRouteHeaderId());
        assertEquals("Wrong number of active nodes.", 1, activeNodes.size());
        RouteNodeInstance nodeInstance = (RouteNodeInstance)activeNodes.iterator().next();
        assertEquals("Document at wrong node.", ParallelSetup.WORKFLOW_DOCUMENT_FINAL_NODE, nodeInstance.getName());
        
        document = new WorkflowDocument(new NetworkIdVO("xqi"), document.getRouteHeaderId());
        assertTrue("Should have approve request.", document.isApprovalRequested());
        document.blanketApprove("", ParallelSetup.ACKNOWLEDGE_1_NODE);
        
        activeNodes = getRouteNodeService().getActiveNodeInstances(document.getRouteHeaderId());
        assertEquals("Wrong number of active nodes.", 0, activeNodes.size());
        Collection terminalNodes = getRouteNodeService().getTerminalNodeInstances(document.getRouteHeaderId());
        assertEquals("Wrong number of terminal nodes.", 1, terminalNodes.size());
        nodeInstance = (RouteNodeInstance)terminalNodes.iterator().next();
        assertEquals("Document at wrong node.", ParallelSetup.ACKNOWLEDGE_2_NODE, nodeInstance.getName());
        assertTrue("Final node not complete.", nodeInstance.isComplete());
    }
    
    @Test public void testBlanketApproveToAcknowledge() throws Exception {
        
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), ParallelSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("", ParallelSetup.ACKNOWLEDGE_1_NODE);
        assertTrue("Document should be processed.", document.stateIsProcessed());

        // document should now be terminal
        Collection activeNodes = getRouteNodeService().getActiveNodeInstances(document.getRouteHeaderId());
        assertEquals("Wrong number of active nodes.", 0, activeNodes.size());
        Collection terminalNodes = getRouteNodeService().getTerminalNodeInstances(document.getRouteHeaderId());
        assertEquals("Wrong number of terminal nodes.", 1, terminalNodes.size());
        RouteNodeInstance nodeInstance = (RouteNodeInstance)terminalNodes.iterator().next();
        assertEquals("Document at wrong node.", ParallelSetup.ACKNOWLEDGE_2_NODE, nodeInstance.getName());
        assertTrue("Final node not complete.", nodeInstance.isComplete());
    }
    
    @Test public void testBlanketApproveToMultipleNodesErrors() throws Exception {
        
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), ParallelSetup.DOCUMENT_TYPE_NAME);
        try {
            document.blanketApprove("", new String[] { ParallelSetup.WORKFLOW_DOCUMENT_2_B1_NODE, ParallelSetup.ACKNOWLEDGE_1_NODE });    
            fail("document should have thrown exception");
        } catch (Exception e) {
            // Shouldn't be able to blanket approve past the join in conjunction with blanket approve within a branch
        	TestUtilities.getExceptionThreader().join();
        	document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
            assertTrue("Document should be in exception routing.", document.stateIsException());            
        }
    }
    
    @Ignore("This test needs to be implemented!")
    @Test public void testBlanketApproveCycle() throws Exception {
        // TODO it would be cool to get this implemented but right now it looks like we can't quite handle cycles
        /*CustomCycleSplit.configureCycle("B1", "B2", 5);
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), CycleSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("");
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
        assertTrue("Document should be processed.", document.stateIsProcessed());
        // should have cycled 5 times
        assertEquals("Wrong number of cycles.", 5, CustomCycleSplit.getTimesCycled());
        
        CustomCycleSplit.configureCycle("B1", "B2", 1000);
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), CycleSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("");
        // an exception should have happened in routing
        assertTrue("Exception should have happened because of runaway cycle.", TestUtilities.isInExceptionRouting(document.getRouteHeaderId()));*/
    }
    
    /**
     * Tests that the notifications are generated properly on a blanket approve.  Works against the "NotificationTest" document type.
     */
    @Test public void testBlanketApproveNotification() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("");
        ActionRequestService arService = KEWServiceLocator.getActionRequestService(); 
        List actionRequests = arService.getRootRequests(arService.findPendingByDoc(document.getRouteHeaderId()));
        assertEquals("Should be 5 pending acknowledgements and 1 pending fyi", 6, actionRequests.size());
        boolean foundJhopfNotification = false;
        boolean foundRkirkendNotification = false;
        boolean foundJitrueNotification = false;
        boolean foundBmcgoughNotification = false;
        boolean foundXqiAck = false;
        boolean foundJthomasFYI = false;
        for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
            RouteNodeInstance nodeInstance = actionRequest.getNodeInstance();
            String netId = (actionRequest.getWorkflowUser() == null ? null : actionRequest.getWorkflowUser().getAuthenticationUserId().getId());
            if ("jhopf".equals(netId)) {
                foundJhopfNotification = true;
                assertTrue("Action request should be an acknowledge.", actionRequest.isAcknowledgeRequest());
                assertEquals(NotifySetup.NOTIFY_FIRST_NODE, nodeInstance.getName());
            } else if ("rkirkend".equals(netId)) {
                foundRkirkendNotification = true;
                assertTrue("Action request should be an acknowledge.", actionRequest.isAcknowledgeRequest());
                assertEquals(NotifySetup.NOTIFY_LEFT_NODE, nodeInstance.getName());
                assertEquals("Rkirkend should have three delegate acks.", 3, actionRequest.getChildrenRequests().size());
                assertTrue("Should be primary delegation.", actionRequest.isPrimaryDelegator());
                boolean foundTemayDelegate = false;
                boolean foundNonSITWGDelegate = false;
                boolean foundPmckownDelegate = false;
                for (Iterator iterator2 = actionRequest.getChildrenRequests().iterator(); iterator2.hasNext();) {
                    ActionRequestValue childRequest = (ActionRequestValue) iterator2.next();
                    assertTrue("Child request should be an acknowledge.", actionRequest.isAcknowledgeRequest());
                    String childId = (childRequest.isWorkgroupRequest() ? childRequest.getWorkgroup().getGroupNameId().getNameId() : childRequest.getWorkflowUser().getAuthenticationUserId().getId());
                    if ("temay".equals(childId)) {
                        foundTemayDelegate = true;
                        assertEquals("Should be primary delegation.", EdenConstants.DELEGATION_PRIMARY, childRequest.getDelegationType());
                    } else if ("pmckown".equals(childId)) {
                        foundPmckownDelegate = true;
                        assertEquals("Should be secondary delegation.", EdenConstants.DELEGATION_SECONDARY, childRequest.getDelegationType());
                    } else if ("NonSIT".equals(childId)) {
                        foundNonSITWGDelegate = true;
                        assertEquals("Should be primary delegation.", EdenConstants.DELEGATION_PRIMARY, childRequest.getDelegationType());
                    }
                }
                assertTrue("Could not locate delegate request for temay.", foundTemayDelegate);
                assertTrue("Could not locate delegate request for NonSIT Workgroup.", foundNonSITWGDelegate);
                assertTrue("Could not locate delegate request for pmckown.", foundPmckownDelegate);
            } else if ("bmcgough".equals(netId)) {
                foundBmcgoughNotification = true;
                assertTrue("Action request should be an acknowledge.", actionRequest.isAcknowledgeRequest());
                assertEquals(NotifySetup.NOTIFY_FINAL_NODE, nodeInstance.getName());
                
            } else if ("xqi".equals(netId)) {
                foundXqiAck = true;
                assertTrue("Action request should be an acknowledge.", actionRequest.isAcknowledgeRequest());
                assertEquals(NotifySetup.NOTIFY_FINAL_NODE, nodeInstance.getName());
                
            } else if ("jthomas".equals(netId)) {
                foundJthomasFYI = true;
                assertTrue("Action request should be an FYI.", actionRequest.isFYIRequest());
                assertEquals(NotifySetup.NOTIFY_FINAL_NODE, nodeInstance.getName());
            } else if (actionRequest.isRoleRequest()) {
               List topLevelRequests = arService.getTopLevelRequests(actionRequest);
               assertEquals(1, topLevelRequests.size());
               actionRequest = (ActionRequestValue)topLevelRequests.get(0);
               // this tests the notofication of the role to jitrue with delegates
               assertEquals("Should be to jitrue.", "jitrue", actionRequest.getWorkflowUser().getAuthenticationUserId().getId());
               foundJitrueNotification = true;
               List delegateRoleRequests = arService.getDelegateRequests(actionRequest);
               assertEquals("Should be 1 delegate role requests", 1, delegateRoleRequests.size());
               ActionRequestValue delegateRoleRequest = (ActionRequestValue)delegateRoleRequests.get(0);
               assertEquals("Should be NotifyDelegate role", "NotifyDelegate", delegateRoleRequest.getRoleName());
               assertEquals("Should be secondary delegation", EdenConstants.DELEGATION_SECONDARY, delegateRoleRequest.getDelegationType());
               List delegateRequests = arService.getTopLevelRequests(delegateRoleRequest);
               assertEquals("Should be 2 delegate requests", 2, delegateRequests.size());
               boolean foundNatjohnsDelegate = false;
               boolean foundShenlDelegate = false;
               for (Iterator iterator2 = delegateRequests.iterator(); iterator2.hasNext();) {
                   ActionRequestValue delegateRequest = (ActionRequestValue) iterator2.next();
                   String delNetId = delegateRequest.getWorkflowUser().getAuthenticationUserId().getId();
                   if ("natjohns".equals(delNetId)) {
                       foundNatjohnsDelegate = true;
                   } else if ("shenl".equals(delNetId)) {
                       foundShenlDelegate = true;
                   }
               }
               assertTrue("Could not locate natjohns role delegate request.", foundNatjohnsDelegate);
               assertTrue("Could not locate shenl role delegate request.", foundShenlDelegate);
            }
        }
        assertTrue("Could not locate notification for jhopf.", foundJhopfNotification);
        assertTrue("Could not locate notification for rkirkend.", foundRkirkendNotification);
        assertTrue("Could not locate notification for bmcgough.", foundBmcgoughNotification);
        assertTrue("Could not locate acknowledgment for xqi.", foundXqiAck);
        assertTrue("Could not locate FYI for jthomas.", foundJthomasFYI);
        assertTrue("Could not locate notification for jitrue.", foundJitrueNotification);
    }
    
    /**
     * Tests that we can blanket approve past mandatory route nodes.
     * Addresses issue http://fms.dfa.cornell.edu:8080/browse/KULWF-461
     */
    @Test public void testBlanketApprovePastMandatoryNode() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), "BlanketApproveMandatoryNodeTest");
        document.blanketApprove("");
        assertTrue("Document should be processed.", document.stateIsProcessed());
    }
    
    /**
     * Tests the behavior of blanket approve through a role node and then through a node with a Workgroup including
     * the individual(s) in the role.  Verifies that the Action List contains the proper entries in this case.
     */
    @Test public void testBlanketApproveThroughRoleAndWorkgroup() throws Exception {
    	NetworkIdVO jitrue = new NetworkIdVO("jitrue");
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), "BlanketApproveThroughRoleAndWorkgroupTest");
    	document.saveDocument("");
    	assertTrue(document.stateIsSaved());
    	TestUtilities.assertNotInActionList(jitrue, document.getRouteHeaderId());
    	document.blanketApprove("");
    	
    	// document should now be processed
    	document = new WorkflowDocument(jitrue, document.getRouteHeaderId());
    	assertTrue(document.stateIsProcessed());
    	assertTrue(document.isAcknowledgeRequested());
    	
    	// there should be 3 root acknowledge requests, one to the WorkflowAdmin workgroup, one to jitrue in the Notify role and one to jitrue in the Notify2 role
    	List actionRequests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(document.getRouteHeaderId());
    	assertEquals("There should be 3 root requests.", 3, actionRequests.size());
    	
    	// now check that the document is in jitrue's action list
    	TestUtilities.assertInActionList(jitrue, document.getRouteHeaderId());
    	
    	// acknowledge as a member of the workgroup who is not jitrue
    	document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
    	assertTrue(document.isAcknowledgeRequested());
    	document.acknowledge("");
    	
    	// document should still be processed
    	document = new WorkflowDocument(jitrue, document.getRouteHeaderId());
    	assertTrue(document.stateIsProcessed());
    	assertTrue(document.isAcknowledgeRequested());
    	
    	// there should now be 2 root acknowledge requests, one to jitrue in the Notify role and one to jitrue in the Notify2 role
    	actionRequests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(document.getRouteHeaderId());
    	assertEquals("There should be 2 root requests.", 2, actionRequests.size());
    	
    	// jitrue should still have this in his action list
    	TestUtilities.assertInActionList(jitrue, document.getRouteHeaderId());
    	document.acknowledge("");
    	
    	// document should now be final
    	assertTrue(document.stateIsFinal());
    }
    
    private RouteNodeService getRouteNodeService() {
        return KEWServiceLocator.getRouteNodeService();
    }
    
    private class SequentialSetup {

        public static final String DOCUMENT_TYPE_NAME = "BlanketApproveSequentialTest";
        public static final String ADHOC_NODE = "AdHoc";
        public static final String WORKFLOW_DOCUMENT_NODE = "WorkflowDocument";
        public static final String WORKFLOW_DOCUMENT_2_NODE = "WorkflowDocument2";
        public static final String ACKNOWLEDGE_1_NODE = "Acknowledge1";
        public static final String ACKNOWLEDGE_2_NODE = "Acknowledge2";
        
    }
    
    private class ParallelSetup {

        public static final String DOCUMENT_TYPE_NAME = "BlanketApproveParallelTest";
        public static final String ADHOC_NODE = "AdHoc";
        public static final String WORKFLOW_DOCUMENT_NODE = "WorkflowDocument";
        public static final String WORKFLOW_DOCUMENT_2_B1_NODE = "WorkflowDocument2-B1";
        public static final String WORKFLOW_DOCUMENT_2_B2_NODE = "WorkflowDocument2-B2";
        public static final String WORKFLOW_DOCUMENT_3_B1_NODE = "WorkflowDocument3-B1";
        public static final String WORKFLOW_DOCUMENT_3_B2_NODE = "WorkflowDocument3-B2";
        public static final String WORKFLOW_DOCUMENT_4_B3_NODE = "WorkflowDocument4-B3";
        public static final String ACKNOWLEDGE_1_NODE = "Acknowledge1";
        public static final String ACKNOWLEDGE_2_NODE = "Acknowledge2";
        public static final String JOIN_NODE = "Join";
        public static final String SPLIT_NODE = "Split";
        public static final String WORKFLOW_DOCUMENT_FINAL_NODE = "WorkflowDocumentFinal";
        
    }
    
    /*private class CycleSetup {

        public static final String DOCUMENT_TYPE_NAME = "BlanketApproveCycleTest";
        public static final String ADHOC_NODE = "AdHoc";
        public static final String WORKFLOW_DOCUMENT_NODE = "WorkflowDocument";
        public static final String WORKFLOW_DOCUMENT_2_NODE = "WorkflowDocument2";
        public static final String WORKFLOW_DOCUMENT_FINAL_NODE = "WorkflowDocumentFinal";
        public static final String JOIN_NODE = "Join";
        public static final String CUSTOM_CYCLE_SPLIT_NODE = "CustomCycleSplit";
        
    }*/
    
    public static class NotifySetup {

        public static final String DOCUMENT_TYPE_NAME = "NotificationTest";
        public static final String ADHOC_NODE = "AdHoc";
        public static final String NOTIFY_FIRST_NODE = "NotifyFirst";
        public static final String NOTIFY_LEFT_NODE = "NotifyLeftBranch";
        public static final String NOTIFY_RIGHT_NODE = "NotifyRightBranch";
        public static final String NOTIFY_FINAL_NODE = "NotifyFinal";
        public static final String SPLIT_NODE = "Split";
        public static final String JOIN_NODE = "Join";
        
    }

    private MockEmailNotificationService getMockEmailService() {
        return (MockEmailNotificationService)KEWServiceLocator.getActionListEmailService();
    }
    
}
