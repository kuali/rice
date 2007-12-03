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


import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.MovePointVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.RouteNodeInstanceVO;
import edu.iu.uis.eden.test.TestUtilities;

public class MoveDocumentTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }
   
    /**
     * Tests that we can move a sequential document forward and backward.
     *
     */
    @Test public void testMoveDocumentSequential() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), SeqSetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        
        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        assertTrue("Bmcgough should have an approve.", document.isApprovalRequested());
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertTrue("Rkirkend should have an approve.", document.isApprovalRequested());
        assertEquals("Should be at the WorkflowDocument Node.", SeqSetup.WORKFLOW_DOCUMENT_NODE, document.getNodeNames()[0]);
        
        // move the document forward one node
        document.moveDocument(new MovePointVO(SeqSetup.WORKFLOW_DOCUMENT_NODE, 1), "");
        
        List actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertEquals("Should be only 1 pending approve request to pmckown.", 1, actionRequests.size());
        assertEquals("Should be at the WorkflowDocument2 Node.", SeqSetup.WORKFLOW_DOCUMENT_2_NODE, document.getNodeNames()[0]);
        
        // after moving the document forward, bmcgough and rkirkend should no longer have requests, but phil should
        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        assertFalse("Bmcgough should NOT have an approve.", document.isApprovalRequested());
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertFalse("Rkirkend should NOT have an approve.", document.isApprovalRequested());
        
        document = new WorkflowDocument(new NetworkIdVO("pmckown"), document.getRouteHeaderId());
        assertTrue("Pmckown should have an approve.", document.isApprovalRequested());
        ActionRequestValue pmckownRequest = (ActionRequestValue)actionRequests.get(0);
        
        // now try moving it to itself, effectively refreshing the node
        document.moveDocument(new MovePointVO(SeqSetup.WORKFLOW_DOCUMENT_2_NODE, 0), "");
        assertTrue("Pmckown should still have an approve.", document.isApprovalRequested());
        actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertEquals("Should be only 1 pending approve request to pmckown.", 1, actionRequests.size());
        assertEquals("Should be at the WorkflowDocument2 Node.", SeqSetup.WORKFLOW_DOCUMENT_2_NODE, document.getNodeNames()[0]);
        
        // since this should have invoked a refresh, let's ensure that the action request ids are different after the move
        assertFalse("Action request ids should be different.", pmckownRequest.getActionRequestId().equals(((ActionRequestValue)actionRequests.get(0)).getActionRequestId()));
        
        // now try moving it back
        document.moveDocument(new MovePointVO(SeqSetup.WORKFLOW_DOCUMENT_2_NODE, -1), "");
        
        // document should now be back at the WorkflowDocumentNode with requests to rkirkend and brian
        actionRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertEquals("Should be 2 pending requests.", 2, actionRequests.size());
        
        assertEquals("Should be at the WorkflowDocument Node.", SeqSetup.WORKFLOW_DOCUMENT_NODE, document.getNodeNames()[0]);
        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        assertTrue("Bmcgough should have an approve.", document.isApprovalRequested());
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertTrue("Rkirkend should have an approve.", document.isApprovalRequested());
        
        
        // Let's do a sanity check to make sure we're still ENROUTE and move the doc to an ack node, rendering it PROCESSED,
        // also, we'll check that there are no permissions enforced on the move document action by moving as a random user
        document = new WorkflowDocument(new NetworkIdVO("xqi"), document.getRouteHeaderId());
        assertTrue("Doc should be ENROUTE.", document.stateIsEnroute());
        document.moveDocument(new MovePointVO(SeqSetup.WORKFLOW_DOCUMENT_NODE, 2), "");
        assertTrue("Doc should be PROCESSED.", document.stateIsProcessed());
    }

    /**
     * This tests that we can invoke the move document command inside of a sub process.
     */
    @Test public void testMoveDocumentInsideProcess() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), "MoveInProcessTest");
        document.routeDocument("");
        
        // approve as bmcgough and rkirkend to move into process
        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        assertTrue("bmcgough should have approve", document.isApprovalRequested());
        document.approve("");
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        assertTrue("rkirkend should have approve", document.isApprovalRequested());
        document.approve("");
        
        WorkflowInfo info = new WorkflowInfo();
        RouteNodeInstanceVO[] activeNodeInstances = info.getActiveNodeInstances(document.getRouteHeaderId());
        assertEquals("Should be 1 active node instance.", 1, activeNodeInstances.length);
        RouteNodeInstanceVO node2 = activeNodeInstances[0];
        assertEquals("Should be at the WorkflowDocument2 node.", SeqSetup.WORKFLOW_DOCUMENT_2_NODE, node2.getName());
        assertTrue("Node should be in a process.", node2.getProcessId() != null);
        
        // now try to move the document forward one which will keep us inside the subprocess
        document.moveDocument(new MovePointVO(SeqSetup.WORKFLOW_DOCUMENT_2_NODE, 1), "");
        
        activeNodeInstances = info.getActiveNodeInstances(document.getRouteHeaderId());
        RouteNodeInstanceVO node3 = activeNodeInstances[0];
        assertEquals("Should be at the WorkflowDocument3 node.", SeqSetup.WORKFLOW_DOCUMENT_3_NODE, node3.getName());
        assertTrue("Node should be in a process.", node3.getProcessId() != null);
        assertEquals("Node 2 and 3 should be in the same process.", node2.getProcessId(), node3.getProcessId());
        
        document.moveDocument(new MovePointVO(SeqSetup.WORKFLOW_DOCUMENT_3_NODE, 0), "");
        
        document.moveDocument(new MovePointVO(SeqSetup.WORKFLOW_DOCUMENT_3_NODE, -1), "");
    }
    
    @Test public void testMoveDocumentParallel() throws Exception {
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), ParallelSetup.DOCUMENT_TYPE_NAME);
    	document.blanketApprove("", new String[] { ParallelSetup.WORKFLOW_DOCUMENT_2_B2_NODE, ParallelSetup.WORKFLOW_DOCUMENT_3_B1_NODE, ParallelSetup.WORKFLOW_DOCUMENT_4_B3_NODE });
    	Set nodeNames = TestUtilities.createNodeInstanceNameSet(KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(document.getRouteHeaderId()));
    	assertEquals("There should be 3 active nodes.", 3, nodeNames.size());
    	assertTrue("Should be at WorkflowDocument3-B1", nodeNames.contains(ParallelSetup.WORKFLOW_DOCUMENT_3_B1_NODE));
    	assertTrue("Should be at WorkflowDocument2-B2", nodeNames.contains(ParallelSetup.WORKFLOW_DOCUMENT_2_B2_NODE));
    	assertTrue("Should be at WorkflowDocument4-B3", nodeNames.contains(ParallelSetup.WORKFLOW_DOCUMENT_4_B3_NODE));
    	
    	// try to move the document from WorkflowDocument3-B1 to WorkflowDocument2-B1
    	document.moveDocument(new MovePointVO(ParallelSetup.WORKFLOW_DOCUMENT_3_B1_NODE, -1), "");
    	nodeNames = TestUtilities.createNodeInstanceNameSet(KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(document.getRouteHeaderId()));
    	assertEquals("There should be 3 active nodes.", 3, nodeNames.size());
    	assertTrue("Should be at WorkflowDocument2-B1", nodeNames.contains(ParallelSetup.WORKFLOW_DOCUMENT_2_B1_NODE));
    	assertTrue("Should be at WorkflowDocument2-B2", nodeNames.contains(ParallelSetup.WORKFLOW_DOCUMENT_2_B2_NODE));
    	assertTrue("Should be at WorkflowDocument4-B3", nodeNames.contains(ParallelSetup.WORKFLOW_DOCUMENT_4_B3_NODE));
    }
    
    private class SeqSetup {

        public static final String DOCUMENT_TYPE_NAME = "MoveSequentialTest";
        public static final String ADHOC_NODE = "AdHoc";
        public static final String WORKFLOW_DOCUMENT_NODE = "WorkflowDocument";
        public static final String WORKFLOW_DOCUMENT_2_NODE = "WorkflowDocument2";
        public static final String WORKFLOW_DOCUMENT_3_NODE = "WorkflowDocument3";
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
   
}
