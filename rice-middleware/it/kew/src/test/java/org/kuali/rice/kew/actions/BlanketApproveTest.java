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
package org.kuali.rice.kew.actions;

import mocks.MockEmailNotificationService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.action.InvalidActionTakenException;
import org.kuali.rice.kew.api.document.DocumentContentUpdate;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.PropertyDefinition;
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.engine.node.service.RouteNodeService;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kew.framework.postprocessor.ProcessDocReport;
import org.kuali.rice.kew.postprocessor.DefaultPostProcessor;
import org.kuali.rice.kew.rule.GenericWorkflowAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.test.BaselineTestCase.BaselineMode;
import org.kuali.rice.test.BaselineTestCase.Mode;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@BaselineMode(Mode.CLEAR_DB)
public class BlanketApproveTest extends KEWTestCase {

    public static String TEST_USER_EWESTFAL = "ewestfal";
    public static String TEST_USER_USER1 = "user1";
    public static String TEST_USER_USER2 = "user2";
    public static String TEST_USER_TEMAY = "temay";
    public static String TEST_USER_JHOPF = "jhopf";
    public static String TEST_USER_JITRUE = "jitrue";
    public static String TEST_USER_JTHOMAS = "jthomas";
    public static String TEST_USER_BMCGOUGH = "bmcgough";
    public static String TEST_USER_RKIRKEND = "rkirkend";
    public static String TEST_USER_PMCKOWN = "pmckown";
    public static String TEST_USER_XQI = "xqi";
    
	@Override
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
    	WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_USER1), SequentialSetup.DOCUMENT_TYPE_NAME);
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
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_USER1), SequentialSetup.DOCUMENT_TYPE_NAME);
        WorkflowDocument newDocument = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), document.getDocumentId());
        try {
            newDocument.blanketApprove("");
            fail("Exception should have been thrown when non-initiator user attempts blanket approve on default blanket approve policy document");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("deprecation")
	@Test public void testBlanketApproveSequential() throws Exception {
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), SequentialSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("");
        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), document.getDocumentId());
        assertTrue("Document should be processed.", document.isProcessed());
        Collection nodeInstances = getRouteNodeService().getActiveNodeInstances(document.getDocumentId());
        // once the document is processed there are no active nodes
        assertEquals("Wrong number of active nodes.", 0, nodeInstances.size());
        nodeInstances = getRouteNodeService().getTerminalNodeInstances(document.getDocumentId());
        assertEquals("Wrong number of active nodes.", 1, nodeInstances.size());
        RouteNodeInstance ackNodeInstance = (RouteNodeInstance)nodeInstances.iterator().next();
        assertEquals("At wrong node.", SequentialSetup.ACKNOWLEDGE_2_NODE, ackNodeInstance.getRouteNode().getRouteNodeName());
        assertTrue("Node should be complete.", ackNodeInstance.isComplete());
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getDocumentId());
        assertEquals("Wrong number of pending action requests.", 5, actionRequests.size());
        boolean isNotification1 = false;
        boolean isNotification2 = false;
        boolean isNotification3 = false;
        boolean isAck1 = false;
        boolean isAck2 = false;
        for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
            assertEquals("Should only be acknowledges.", KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, actionRequest.getActionRequested());
            RouteNodeInstance nodeInstance = actionRequest.getNodeInstance();
            assertNotNull(nodeInstance);
            String nodeName = nodeInstance.getRouteNode().getRouteNodeName();
            if (actionRequest.getPrincipalId().equals(getPrincipalIdForName(TEST_USER_BMCGOUGH))) {
                isNotification1 = true;
                assertEquals(SequentialSetup.WORKFLOW_DOCUMENT_NODE, nodeName);
                assertEquals(KewApiConstants.MACHINE_GENERATED_RESPONSIBILITY_ID, actionRequest.getResponsibilityId());
            } else if (actionRequest.getPrincipalId().equals(getPrincipalIdForName(TEST_USER_RKIRKEND))) {
                isNotification2 = true;
                assertEquals(SequentialSetup.WORKFLOW_DOCUMENT_NODE, nodeName);
                assertEquals(KewApiConstants.MACHINE_GENERATED_RESPONSIBILITY_ID, actionRequest.getResponsibilityId());
            } else if (actionRequest.getPrincipalId().equals(getPrincipalIdForName(TEST_USER_PMCKOWN))) {
                isNotification3 = true;
                assertEquals(SequentialSetup.WORKFLOW_DOCUMENT_2_NODE, nodeName);
                assertEquals(KewApiConstants.MACHINE_GENERATED_RESPONSIBILITY_ID, actionRequest.getResponsibilityId());
            } else if (actionRequest.getPrincipalId().equals(getPrincipalIdForName(TEST_USER_TEMAY))) {
                isAck1 = true;
                assertEquals(SequentialSetup.ACKNOWLEDGE_1_NODE, nodeName);
                assertFalse(KewApiConstants.MACHINE_GENERATED_RESPONSIBILITY_ID.equals(actionRequest.getResponsibilityId()));
            } else if (actionRequest.getPrincipalId().equals(getPrincipalIdForName(TEST_USER_JHOPF))) {
                isAck2 = true;
                assertEquals(SequentialSetup.ACKNOWLEDGE_2_NODE, nodeName);
                assertFalse(KewApiConstants.MACHINE_GENERATED_RESPONSIBILITY_ID.equals(actionRequest.getResponsibilityId()));
            }
        }
        assertTrue(isNotification1);
        assertTrue(isNotification2);
        assertTrue(isNotification3);
        assertTrue(isAck1);
        assertTrue(isAck2);
        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(TEST_USER_BMCGOUGH), document.getDocumentId());
        assertTrue(document.isProcessed());
        assertTrue(document.isAcknowledgeRequested());
        assertEquals("bmcgough should not have been sent an approve email", 0, getMockEmailService().immediateReminderEmailsSent(TEST_USER_BMCGOUGH, document.getDocumentId(), KewApiConstants.ACTION_REQUEST_APPROVE_REQ));
        assertEquals("bmcgough should not have been sent an ack email", 1, getMockEmailService().immediateReminderEmailsSent(TEST_USER_BMCGOUGH, document.getDocumentId(), KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));
        document.acknowledge("");

        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(TEST_USER_RKIRKEND), document.getDocumentId());
        assertTrue(document.isProcessed());
        assertTrue(document.isAcknowledgeRequested());
        assertEquals("rkirkend should not have been sent an approve email", 0, getMockEmailService().immediateReminderEmailsSent("rkirkend", document.getDocumentId(), KewApiConstants.ACTION_REQUEST_APPROVE_REQ));
        assertEquals("rkirkend should not have been sent an ack email", 1, getMockEmailService().immediateReminderEmailsSent("rkirkend", document.getDocumentId(), KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));
        document.acknowledge("");
        
        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(TEST_USER_PMCKOWN), document.getDocumentId());
        assertTrue(document.isProcessed());
        assertTrue(document.isAcknowledgeRequested());
        assertEquals("pmckown should not have been sent an approve email", 0, getMockEmailService().immediateReminderEmailsSent("pmckown", document.getDocumentId(), KewApiConstants.ACTION_REQUEST_APPROVE_REQ));
        assertEquals("pmckown should not have been sent an ack email", 1, getMockEmailService().immediateReminderEmailsSent("pmckown", document.getDocumentId(), KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));
        document.acknowledge("");
        
        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(TEST_USER_TEMAY), document.getDocumentId());
        assertTrue(document.isProcessed());
        assertTrue(document.isAcknowledgeRequested());
        assertEquals("rkirkend should have been sent an temay", 1, getMockEmailService().immediateReminderEmailsSent(TEST_USER_TEMAY, document.getDocumentId(), KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));
        document.acknowledge("");
        
        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(TEST_USER_JHOPF), document.getDocumentId());
        assertTrue(document.isProcessed());
        assertTrue(document.isAcknowledgeRequested());
        assertEquals("rkirkend should have been sent an jhopf", 1, getMockEmailService().immediateReminderEmailsSent(TEST_USER_JHOPF, document.getDocumentId(), KewApiConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ));
        document.acknowledge("");
        
        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), document.getDocumentId());
        assertTrue(document.isFinal());
        
        document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), SequentialSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("", SequentialSetup.WORKFLOW_DOCUMENT_2_NODE);
        assertTrue("Document should be enroute.", document.isEnroute());
        nodeInstances = getRouteNodeService().getActiveNodeInstances(document.getDocumentId());
        assertEquals("Should be one active node.", 1, nodeInstances.size());
        RouteNodeInstance doc2Instance = (RouteNodeInstance)nodeInstances.iterator().next();
        assertEquals("At wrong node.", SequentialSetup.WORKFLOW_DOCUMENT_2_NODE, doc2Instance.getRouteNode().getRouteNodeName());
        
    }
    
    @Test public void testBlanketApproveSequentialErrors() throws Exception {
        // blanket approve to invalid node
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), SequentialSetup.DOCUMENT_TYPE_NAME);
        try {
            document.blanketApprove("", "TotallyInvalidNode");
            fail("Should have thrown exception");
        } catch (Exception e) {}
        
        // blanket approve backwards
        document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), SequentialSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("", SequentialSetup.WORKFLOW_DOCUMENT_2_NODE);
        try {
            document.blanketApprove("", SequentialSetup.WORKFLOW_DOCUMENT_NODE);
            fail("Should have thrown exception");
        } catch (Exception e) {}
        
        // blanket approve to current node
        document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), SequentialSetup.DOCUMENT_TYPE_NAME);
        document.route("");
        try {
            document.blanketApprove("", SequentialSetup.WORKFLOW_DOCUMENT_NODE);
            fail("Should have thrown exception");
        } catch (Exception e) {}
        
        // blanket approve as user not in the blanket approve workgroup
        document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_USER1), SequentialSetup.DOCUMENT_TYPE_NAME);
        try {
            document.blanketApprove("");
            fail("Shouldn't be able to blanket approve if not in blanket approve workgroup");
        } catch (Exception e) {}
    }
    
    @Test public void testBlanketApproveParallel() throws Exception {
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), ParallelSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("");        
        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), document.getDocumentId());
        assertTrue("Document should be processed.", document.isProcessed());
        Collection nodeInstances = getRouteNodeService().getActiveNodeInstances(document.getDocumentId());
        // once the document has gone processed there are no active nodes
        assertEquals("Wrong number of active nodes.", 0, nodeInstances.size());
        nodeInstances = getRouteNodeService().getTerminalNodeInstances(document.getDocumentId());
        assertEquals("Wrong number of terminal nodes.", 1, nodeInstances.size());
        RouteNodeInstance ackNodeInstance = (RouteNodeInstance)nodeInstances.iterator().next();
        assertEquals("At wrong node.", SequentialSetup.ACKNOWLEDGE_2_NODE, ackNodeInstance.getRouteNode().getRouteNodeName());
        assertTrue("Node should be complete.", ackNodeInstance.isComplete());
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getDocumentId());
        assertEquals("Wrong number of pending action requests.", 10, actionRequests.size());

    }

    /**
     * Test the blanket approval of a document chained by the post processor
     *
     * @throws Exception
     */
    @Ignore
    @Test
    public void testChainedBlanketApproval() throws Exception {
        // Generate child document
        WorkflowDocument childDocument = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), ChainedChildSetup.DOCUMENT_TYPE_NAME);
        WorkflowAttributeDefinition.Builder childDocAttribute = WorkflowAttributeDefinition.Builder.create(ChainedParentSetup.CHILD_DOC_ATTRIBUTE);
        PropertyDefinition docIdProperty = PropertyDefinition.create(
                ChainedParentSetup.DOC_ID_PROPERTY, childDocument.getDocumentId());
        childDocAttribute.addPropertyDefinition(docIdProperty);

        // Generate a parent document, apply child attribute
        WorkflowDocument parentDocument = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), ChainedParentSetup.DOCUMENT_TYPE_NAME);
        //parentDocument.addAttributeDefinition(childDocAttribute.build());
        //parentDocument.saveDocumentData();

        // Issue with attribute definition save
        String fullContent = parentDocument.getDocumentContent().getFullContent();
        String newContent = "<documentContent><attributeContent><documentId>" + childDocument.getDocumentId() + "</documentId></attributeContent></documentContent>";
        DocumentContentUpdate.Builder documentContentUpdateBuilder = DocumentContentUpdate.Builder.create();
        documentContentUpdateBuilder.setAttributeContent(newContent);
        parentDocument.updateDocumentContent(documentContentUpdateBuilder.build());
        parentDocument.saveDocumentData();

        parentDocument.blanketApprove("");
        parentDocument = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), parentDocument.getDocumentId());
        assertTrue("Parent Document should be processed.", parentDocument.isProcessed());

        childDocument = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), childDocument.getDocumentId());
        assertTrue("Child Document should be processed.", childDocument.isProcessed());

    }
    
    @Test public void testBlanketApproveIntoBranch() throws Exception {
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), ParallelSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("", ParallelSetup.WORKFLOW_DOCUMENT_2_B1_NODE);
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getDocumentId());
        assertEquals("Wrong number of pending action requests.", 5, actionRequests.size());
        
        // document should now be at the node we blanket approved to and the join node
        Collection activeNodes = getRouteNodeService().getActiveNodeInstances(document.getDocumentId());
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
        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), document.getDocumentId());
        assertTrue("Document should be processed.", document.isProcessed());
        actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getDocumentId());
        
        assertEquals("Wrong number of pending action requests.", 10, actionRequests.size());
    }
    
    @Test public void testBlanketApproveToMultipleNodes() throws Exception {
        
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), ParallelSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("", new String[] { ParallelSetup.WORKFLOW_DOCUMENT_2_B1_NODE, ParallelSetup.WORKFLOW_DOCUMENT_3_B2_NODE });
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getDocumentId());
        assertEquals("Wrong number of pending action requests.", 5, actionRequests.size());
        
        // document should now be at both nodes we blanket approved to and the join node
        Collection activeNodes = getRouteNodeService().getActiveNodeInstances(document.getDocumentId());
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
        assertTrue("Should be at blanket approved node WD4B3.  https://jira.kuali.org/browse/KULRICE-8481 - "
                + "BlanketApproveTest.testBlanketApproveToMultipleNodes fails in CI with Should be at blanket approved node WD4B3.", isAtWD4B3);
        assertFalse("Should not be at join node.", isAtJoin);
        
        document.blanketApprove("");
        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), document.getDocumentId());
        assertTrue("Document should be processed.", document.isProcessed());
        actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getDocumentId());
        assertEquals("Wrong number of pending action requests.", 10, actionRequests.size());
    }
    
    @Test public void testBlanketApproveToJoin() throws Exception {
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), ParallelSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("", ParallelSetup.JOIN_NODE);
        assertTrue("Document should still be enroute.", document.isEnroute());

        // document should now be at the workflow document final node
        Collection activeNodes = getRouteNodeService().getActiveNodeInstances(document.getDocumentId());
        assertEquals("Wrong number of active nodes.", 1, activeNodes.size());
        RouteNodeInstance nodeInstance = (RouteNodeInstance)activeNodes.iterator().next();
        assertEquals("Document at wrong node.", ParallelSetup.WORKFLOW_DOCUMENT_FINAL_NODE, nodeInstance.getName());
        
        document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(TEST_USER_XQI), document.getDocumentId());
        assertTrue("Should have approve request.", document.isApprovalRequested());
        document.blanketApprove("", ParallelSetup.ACKNOWLEDGE_1_NODE);
        
        activeNodes = getRouteNodeService().getActiveNodeInstances(document.getDocumentId());

        assertEquals("Wrong number of active nodes.", 0, activeNodes.size());
        Collection terminalNodes = getRouteNodeService().getTerminalNodeInstances(document.getDocumentId());
        assertEquals("Wrong number of terminal nodes.", 1, terminalNodes.size());
        nodeInstance = (RouteNodeInstance)terminalNodes.iterator().next();
        assertEquals("Document at wrong node.", ParallelSetup.ACKNOWLEDGE_2_NODE, nodeInstance.getName());
        assertTrue("Final node not complete.", nodeInstance.isComplete());
    }
    
    @Test public void testBlanketApproveToAcknowledge() throws Exception {
        
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), ParallelSetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("", ParallelSetup.ACKNOWLEDGE_1_NODE);
        assertTrue("Document should be processed.", document.isProcessed());

        // document should now be terminal
        Collection activeNodes = getRouteNodeService().getActiveNodeInstances(document.getDocumentId());
        assertEquals("Wrong number of active nodes.", 0, activeNodes.size());
        Collection terminalNodes = getRouteNodeService().getTerminalNodeInstances(document.getDocumentId());
        assertEquals("Wrong number of terminal nodes.", 1, terminalNodes.size());
        RouteNodeInstance nodeInstance = (RouteNodeInstance)terminalNodes.iterator().next();
        assertEquals("Document at wrong node.", ParallelSetup.ACKNOWLEDGE_2_NODE, nodeInstance.getName());
        assertTrue("Final node not complete.", nodeInstance.isComplete());
    }
    
    @Test public void testBlanketApproveToMultipleNodesErrors() throws Exception {
        
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), ParallelSetup.DOCUMENT_TYPE_NAME);
        try {
            document.blanketApprove("", new String[] { ParallelSetup.WORKFLOW_DOCUMENT_2_B1_NODE, ParallelSetup.ACKNOWLEDGE_1_NODE });    
            fail("document should have thrown exception");
        } catch (Exception e) {
            // Shouldn't be able to blanket approve past the join in conjunction with blanket approve within a branch
        	TestUtilities.getExceptionThreader().join();
        	document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), document.getDocumentId());
            assertTrue("Document should be in exception routing.", document.isException());            
        }
    }
    
    /**
     * Tests that the notifications are generated properly on a blanket approve.  Works against the "NotificationTest" document type.
     */
    @Test public void testBlanketApproveNotification() throws Exception {
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), NotifySetup.DOCUMENT_TYPE_NAME);
        document.blanketApprove("");
        ActionRequestService arService = KEWServiceLocator.getActionRequestService(); 
        List actionRequests = arService.getRootRequests(arService.findPendingByDoc(document.getDocumentId()));
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
            String netId = (actionRequest.getPrincipalId() == null ? null : getPrincipalNameForId(actionRequest.getPrincipalId()));
            if (TEST_USER_JHOPF.equals(netId)) {
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
                    String childId = (childRequest.isGroupRequest() ? childRequest.getGroup().getName(): getPrincipalNameForId(childRequest.getPrincipalId()));
                    if (TEST_USER_TEMAY.equals(childId)) {
                        foundTemayDelegate = true;
                        assertEquals("Should be primary delegation.", DelegationType.PRIMARY, childRequest.getDelegationType());
                    } else if ("pmckown".equals(childId)) {
                        foundPmckownDelegate = true;
                        assertEquals("Should be secondary delegation.", DelegationType.SECONDARY, childRequest.getDelegationType());
                    } else if ("NonSIT".equals(childId)) {
                        foundNonSITWGDelegate = true;
                        assertEquals("Should be primary delegation.", DelegationType.PRIMARY, childRequest.getDelegationType());
                    }
                }
                assertTrue("Could not locate delegate request for temay.", foundTemayDelegate);
                assertTrue("Could not locate delegate request for NonSIT Group.", foundNonSITWGDelegate);
                assertTrue("Could not locate delegate request for pmckown.", foundPmckownDelegate);
            } else if (TEST_USER_BMCGOUGH.equals(netId)) {
                foundBmcgoughNotification = true;
                assertTrue("Action request should be an acknowledge.", actionRequest.isAcknowledgeRequest());
                assertEquals(NotifySetup.NOTIFY_FINAL_NODE, nodeInstance.getName());
                
            } else if ("xqi".equals(netId)) {
                foundXqiAck = true;
                assertTrue("Action request should be an acknowledge.", actionRequest.isAcknowledgeRequest());
                assertEquals(NotifySetup.NOTIFY_FINAL_NODE, nodeInstance.getName());
                
            } else if (TEST_USER_JTHOMAS.equals(netId)) {
                foundJthomasFYI = true;
                assertTrue("Action request should be an FYI.", actionRequest.isFYIRequest());
                assertEquals(NotifySetup.NOTIFY_FINAL_NODE, nodeInstance.getName());
            } else if (actionRequest.isRoleRequest()) {
               List topLevelRequests = arService.getTopLevelRequests(actionRequest);
               assertEquals(1, topLevelRequests.size());
               actionRequest = (ActionRequestValue)topLevelRequests.get(0);
               // this tests the notofication of the role to jitrue with delegates
               assertEquals("Should be to jitrue.", TEST_USER_JITRUE, getPrincipalNameForId(actionRequest.getPrincipalId()));
               foundJitrueNotification = true;
               List delegateRoleRequests = arService.getDelegateRequests(actionRequest);
               assertEquals("Should be 1 delegate role requests", 1, delegateRoleRequests.size());
               ActionRequestValue delegateRoleRequest = (ActionRequestValue)delegateRoleRequests.get(0);
               assertEquals("Should be NotifyDelegate role", "NotifyDelegate", delegateRoleRequest.getRoleName());
               assertEquals("Should be secondary delegation", DelegationType.SECONDARY, delegateRoleRequest.getDelegationType());
               List delegateRequests = arService.getTopLevelRequests(delegateRoleRequest);
               assertEquals("Should be 2 delegate requests", 2, delegateRequests.size());
               boolean foundNatjohnsDelegate = false;
               boolean foundShenlDelegate = false;
               for (Iterator iterator2 = delegateRequests.iterator(); iterator2.hasNext();) {
                   ActionRequestValue delegateRequest = (ActionRequestValue) iterator2.next();
                   String delNetId = getPrincipalNameForId(delegateRequest.getPrincipalId());
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
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), "BlanketApproveMandatoryNodeTest");
        document.blanketApprove("");
        assertTrue("Document should be processed.", document.isProcessed());
    }
    
    /**
     * Tests the behavior of blanket approve through a role node and then through a node with a Workgroup including
     * the individual(s) in the role.  Verifies that the Action List contains the proper entries in this case.
     */
    @Test public void testBlanketApproveThroughRoleAndWorkgroup() throws Exception {
    	String jitruePrincipalId = getPrincipalIdForName(TEST_USER_JITRUE);
    	WorkflowDocument document = WorkflowDocumentFactory.createDocument(getPrincipalIdForName(TEST_USER_USER1), "BlanketApproveThroughRoleAndWorkgroupTest");
    	document.saveDocument("");
    	assertTrue(document.isSaved());
    	TestUtilities.assertNotInActionList(jitruePrincipalId, document.getDocumentId());

    	document.blanketApprove("");
    	
    	// document should now be processed
    	document = WorkflowDocumentFactory.loadDocument(jitruePrincipalId, document.getDocumentId());
    	assertTrue(document.isProcessed());
    	assertTrue(document.isAcknowledgeRequested());
    	
    	// there should be 3 root acknowledge requests, one to the WorkflowAdmin workgroup, one to jitrue in the Notify role and one to jitrue in the Notify2 role
    	List actionRequests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(document.getDocumentId());
    	assertEquals("There should be 3 root requests.", 3, actionRequests.size());
    	
    	// now check that the document is in jitrue's action list
    	TestUtilities.assertInActionList(jitruePrincipalId, document.getDocumentId());
    	
    	// acknowledge as a member of the workgroup who is not jitrue
    	document = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName(TEST_USER_EWESTFAL), document.getDocumentId());
    	assertTrue(document.isAcknowledgeRequested());
    	document.acknowledge("");
    	
    	// document should still be processed
    	document = WorkflowDocumentFactory.loadDocument(jitruePrincipalId, document.getDocumentId());
    	assertTrue(document.isProcessed());
    	assertTrue(document.isAcknowledgeRequested());
    	
    	// there should now be 2 root acknowledge requests, one to jitrue in the Notify role and one to jitrue in the Notify2 role
    	actionRequests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocId(document.getDocumentId());
    	assertEquals("There should be 2 root requests.", 2, actionRequests.size());
    	
    	// jitrue should still have this in his action list
    	TestUtilities.assertInActionList(jitruePrincipalId, document.getDocumentId());
    	document.acknowledge("");
    	
    	// document should now be final
    	assertTrue(document.isFinal());
    }

    @Test
    public void testBlanketApprovePostProcessorSUApproveAnotherDoc() throws Exception {

        WorkflowDocument document1 = WorkflowDocumentFactory.createDocument(getPrincipalIdForName("ewestfal"),
                NotifySetup.DOCUMENT_TYPE_NAME);
        assertEquals("Document should be at start node", "AdHoc", document1.getNodeNames().iterator().next());
        assertEquals("Document should be initiated", DocumentStatus.INITIATED, document1.getStatus());

        WorkflowDocument document2 = WorkflowDocumentFactory.createDocument(getPrincipalIdForName("ewestfal"),
                "BAActionSUApproveAnotherDocPostProcessor");
        assertEquals("Document should be at start node", "AdHoc", document2.getNodeNames().iterator().next());
        assertEquals("Document should be initiated", DocumentStatus.INITIATED, document2.getStatus());

        document2 = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("rkirkend"), document2.getDocumentId());

        document2.superUserBlanketApprove("");
        assertEquals("Document should be Final", DocumentStatus.FINAL, document2.getStatus());

        // Document1 was super user approved in the post processor of document2 so it should have a status of PROCESSED.
        document1 = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("rkirkend"), document1.getDocumentId());
        assertEquals("Document should be Processed", DocumentStatus.PROCESSED, document1.getStatus());
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

    private class ChainedParentSetup {
        public static final String DOCUMENT_TYPE_NAME = "ChainedBlanketApproveTestParent";
        public static final String CHILD_DOC_ATTRIBUTE = "ChildDocumentAttribute";
        public static final String DOC_ID_PROPERTY = "documentId";
        public static final String ADHOC_NODE = "AdHoc";
        public static final String WORKFLOW_DOCUMENT_NODE = "WorkflowDocument";
        public static final String WORKFLOW_DOCUMENT_2_NODE = "WorkflowDocument2";
        public static final String ACKNOWLEDGE_1_NODE = "Acknowledge1";
        public static final String ACKNOWLEDGE_2_NODE = "Acknowledge2";
    }

    private class ChainedChildSetup {
        public static final String DOCUMENT_TYPE_NAME = "ChainedBlanketApproveTestChild";
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

    /**
     * Verifying Blanket approve on a chained document works properly
     *
     *
     */
    public static class ChainedApprovalPostProcessor extends DefaultPostProcessor {

        public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) throws Exception {

            if (KewApiConstants.ROUTE_HEADER_PROCESSED_CD.equals(statusChangeEvent.getNewRouteStatus())) {
                // Using KimApiServiceLocator to avoid issues with static class and getPrincipalIdFromName
                String principalId = KimApiServiceLocator
                        .getIdentityService().getPrincipalByPrincipalName(TEST_USER_EWESTFAL).getPrincipalId();
                WorkflowDocument parentDocument = WorkflowDocumentFactory.loadDocument(principalId, statusChangeEvent.getDocumentId());
                List<WorkflowAttributeDefinition> attributeDefinitions = parentDocument.getAttributeDefinitions();

                // Workaround to get document id  for chained blanket approve test
                String documentContent = parentDocument.getDocumentContent().getFullContent();
                String documentId = StringUtils.split(documentContent, "\\<(\\/)?documentId\\>")[1];
                WorkflowDocument childDocument = WorkflowDocumentFactory.loadDocument(principalId, documentId);
                childDocument.blanketApprove("");

                // Standard approach (attribute definition not coming across)
                for(WorkflowAttributeDefinition attributeDefinition: parentDocument.getAttributeDefinitions()) {
                    if(ChainedParentSetup.CHILD_DOC_ATTRIBUTE.equals(attributeDefinition.getAttributeName())) {
                        String childDocumentId = attributeDefinition.getPropertyDefinitionsAsMap().get(ChainedParentSetup.DOC_ID_PROPERTY);
                        WorkflowDocument childDocumentItem = WorkflowDocumentFactory.loadDocument(principalId, childDocumentId);
                        childDocumentItem.blanketApprove("");
                    }
                }
            }

            return new ProcessDocReport(true);
        }

    }

    public class ChainedApprovalWorkflowAttribute extends GenericWorkflowAttribute {

        public ChainedApprovalWorkflowAttribute() {

        }

        Map<String, String> propertiesMap = new HashMap<String, String>();

        @Override
        public Map<String, String> getProperties() {
            return propertiesMap;
        }
    }


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
