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

import org.kuali.rice.kew.engine.node.*;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;


public class DynamicRoutingTest extends KEWTestCase {

    private static final String SEQ_DOC_TYPE_NAME = "DynSeqDocType";
    private static final String INIT = "Initial";
    private static final String FIRST = "First";
    private static final String DYNAMIC_SEQ = "DynSeq";
    private static final String SUB_REQUESTS = "SubRequests";
    private static final String LAST = "Last";

    protected void loadTestData() throws Exception {
        loadXmlFile("EngineConfig.xml");
    }

    @Test public void testDynamicParallelRoute() throws Exception {
        WorkflowDocument document = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), SEQ_DOC_TYPE_NAME);
        document.saveRoutingData();
        assertTrue("Document should be initiated", document.stateIsInitiated());
        assertEquals("Should be no action requests.", 0, document.getActionRequests().length);
        assertEquals("Invalid route level.", new Integer(0), document.getRouteHeader().getDocRouteLevel());
        Collection nodeInstances = KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(document.getDocumentId());
        assertEquals("Wrong number of active nodes.", 1, nodeInstances.size());
        assertEquals("Wrong active node.", INIT, ((RouteNodeInstance) nodeInstances.iterator().next()).getRouteNode().getRouteNodeName());
        document.routeDocument("");

        document = WorkflowDocument.loadDocument(getPrincipalIdForName("bmcgough"), document.getDocumentId());
        assertTrue("Approve should be requested.", document.isApprovalRequested());
        document.approve("");

        nodeInstances = KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(document.getDocumentId());
        assertEquals("Wrong number of active nodes.", 1, nodeInstances.size());
        document = WorkflowDocument.loadDocument(getPrincipalIdForName("pmckown"), document.getDocumentId());
        assertTrue("Approve should be requested.", document.isApprovalRequested());
        document.approve("");

        document = WorkflowDocument.loadDocument(getPrincipalIdForName("temay"), document.getDocumentId());
        assertTrue("Approve should be requested.", document.isApprovalRequested());
        document.approve("");

        document = WorkflowDocument.loadDocument(getPrincipalIdForName("jhopf"), document.getDocumentId());
        assertTrue("Approve should be requested.", document.isApprovalRequested());
        document.approve("");

        document = WorkflowDocument.loadDocument(getPrincipalIdForName("rkirkend"), document.getDocumentId());
        assertTrue("Approve should be requested.", document.isApprovalRequested());
        document.approve("");

        //        document = WorkflowDocument.loadDocument(new NetworkIdVO("ewestfal"), document.getDocumentId());
        //        assertTrue("Document should be final.", document.stateIsFinal());

        verifyRoutingPath(document.getDocumentId());
    }

    private void verifyRoutingPath(String documentId) {
        DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
        List initial = document.getInitialRouteNodeInstances();
        assertEquals(1, initial.size());
        RouteNodeInstance init = (RouteNodeInstance) initial.get(0);
        assertEquals(INIT, init.getRouteNode().getRouteNodeName());
        assertEquals(0, init.getPreviousNodeInstances().size());

        List next = init.getNextNodeInstances();
        assertEquals(1, next.size());
        RouteNodeInstance first = (RouteNodeInstance) next.get(0);
        assertEquals(FIRST, first.getRouteNode().getRouteNodeName());
        assertEquals(1, first.getPreviousNodeInstances().size());

        next = first.getNextNodeInstances();
        assertEquals(1, next.size());
        RouteNodeInstance pmckownNode = (RouteNodeInstance) next.get(0);
        assertEquals(SUB_REQUESTS, pmckownNode.getRouteNode().getRouteNodeName());
        assertEquals(1, pmckownNode.getPreviousNodeInstances().size());
        assertInSubProcess(pmckownNode);

        next = pmckownNode.getNextNodeInstances();
        assertEquals(1, next.size());
        RouteNodeInstance temayNode = (RouteNodeInstance) next.get(0);
        assertEquals(SUB_REQUESTS, temayNode.getRouteNode().getRouteNodeName());
        assertEquals(1, temayNode.getPreviousNodeInstances().size());
        assertInSubProcess(temayNode);

        next = temayNode.getNextNodeInstances();
        assertEquals(1, next.size());
        RouteNodeInstance jhopfNode = (RouteNodeInstance) next.get(0);
        assertEquals(SUB_REQUESTS, jhopfNode.getRouteNode().getRouteNodeName());
        assertEquals(1, jhopfNode.getPreviousNodeInstances().size());
        assertInSubProcess(jhopfNode);

        next = jhopfNode.getNextNodeInstances();
        assertEquals(1, next.size());
        RouteNodeInstance last = (RouteNodeInstance) next.get(0);
        assertNull(last.getProcess());
        assertEquals(LAST, last.getRouteNode().getRouteNodeName());
        assertEquals(1, last.getPreviousNodeInstances().size());
        assertEquals(0, last.getNextNodeInstances().size());
    }

    private void assertInSubProcess(RouteNodeInstance nodeInstance) {
        RouteNodeInstance subProcess = nodeInstance.getProcess();
        assertNotNull(subProcess);
        assertEquals(DYNAMIC_SEQ, subProcess.getRouteNode().getRouteNodeName());
    }

    public static class MockSequentialDynamicNode implements DynamicNode {

        public DynamicResult transitioningInto(RouteContext context, RouteNodeInstance process, RouteHelper helper) throws Exception {
            RouteNodeInstance routeNodeInstance = context.getNodeInstance();
            RouteNode dynamicRequestNode = helper.getNodeFactory().getRouteNode(context, SUB_REQUESTS);
            RouteNodeInstance dynamicRequestNodeInstance1 = helper.getNodeFactory().createRouteNodeInstance(context.getDocument().getDocumentId(), dynamicRequestNode);
            dynamicRequestNodeInstance1.addNodeState(new NodeState("role", "pmckown"));
            RouteNodeInstance dynamicRequestNodeInstance2 = helper.getNodeFactory().createRouteNodeInstance(context.getDocument().getDocumentId(), dynamicRequestNode);
            dynamicRequestNodeInstance2.addNodeState(new NodeState("role", "temay"));
            RouteNodeInstance dynamicRequestNodeInstance3 = helper.getNodeFactory().createRouteNodeInstance(context.getDocument().getDocumentId(), dynamicRequestNode);
            dynamicRequestNodeInstance3.addNodeState(new NodeState("role", "jhopf"));
            dynamicRequestNodeInstance1.addNextNodeInstance(dynamicRequestNodeInstance2);
            dynamicRequestNodeInstance2.addNextNodeInstance(dynamicRequestNodeInstance3);
            routeNodeInstance.addNodeState(new NodeState("beenHere", "val"));
            return new DynamicResult(true, dynamicRequestNodeInstance1);
        }

        public DynamicResult transitioningOutOf(RouteContext context, RouteHelper helper) throws Exception {
            return new DynamicResult(true, null);
        }
    }
}
