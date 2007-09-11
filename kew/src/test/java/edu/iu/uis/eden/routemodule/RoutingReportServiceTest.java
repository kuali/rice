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
package edu.iu.uis.eden.routemodule;


import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.DocumentDetailVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.ReportCriteriaVO;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;

public class RoutingReportServiceTest extends KEWTestCase {
    

    protected void loadTestData() throws Exception {
        loadXmlFile("RouteModuleConfig.xml");
    }

    /**
     * Tests the report() method against a sequential document type.
     */
    @Test public void testReportSequential() throws Exception {
        
        
        // route a document to the first node
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), SeqSetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        
        // there should now be 1 active node and 2 pending requests on the document
        Collection activeNodeInstances = KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(document.getRouteHeaderId());
        List requests = KEWServiceLocator.getActionRequestService().findAllActionRequestsByRouteHeaderId(document.getRouteHeaderId());
        assertEquals("Should be one active node.", 1, activeNodeInstances.size());
        Long activeNodeId = ((RouteNodeInstance)activeNodeInstances.iterator().next()).getRouteNodeInstanceId();
        assertEquals("Should be 2 pending requests.", 2, requests.size());
        
        // now, lets "get our report on", the WorkflowInfo.routingReport method will call the service's report method.
        WorkflowInfo info = new WorkflowInfo();
        ReportCriteriaVO criteria = new ReportCriteriaVO(document.getRouteHeaderId());
        
        long start = System.currentTimeMillis();
        DocumentDetailVO documentDetail = info.routingReport(criteria);
        long end = System.currentTimeMillis();
        System.out.println("Time to run routing report: " + (end-start)+" milliseconds.");
        
        // document detail should have all of our requests on it, 2 activated approves, 1 initialized approve, 2 initialized acknowledges
        assertEquals("There should be 5 requests.", 5, documentDetail.getActionRequests().length);
        boolean approveToBmcgough = false;
        boolean approveToRkirkend = false;
        boolean approveToPmckown = false;
        boolean ackToTemay = false;
        boolean ackToJhopf = false;
        for (int index = 0; index < documentDetail.getActionRequests().length; index++) {
            ActionRequestVO requestVO = documentDetail.getActionRequests()[index];
            String netId = requestVO.getUserVO().getNetworkId(); 
            if (netId.equals("bmcgough")) {
                assertEquals("Should be approve.", EdenConstants.ACTION_REQUEST_APPROVE_REQ, requestVO.getActionRequested());
                assertEquals("Should be activated.", EdenConstants.ACTION_REQUEST_ACTIVATED, requestVO.getStatus());
                assertEquals("Wrong node name", SeqSetup.WORKFLOW_DOCUMENT_NODE, requestVO.getNodeName());
                approveToBmcgough = true;
            } else if (netId.equals("rkirkend")) {
                assertEquals("Should be approve.", EdenConstants.ACTION_REQUEST_APPROVE_REQ, requestVO.getActionRequested());
                assertEquals("Should be activated.", EdenConstants.ACTION_REQUEST_ACTIVATED, requestVO.getStatus());
                assertEquals("Wrong node name", SeqSetup.WORKFLOW_DOCUMENT_NODE, requestVO.getNodeName());
                approveToRkirkend = true;
            } else if (netId.equals("pmckown")) {
                assertEquals("Should be approve.", EdenConstants.ACTION_REQUEST_APPROVE_REQ, requestVO.getActionRequested());
                assertEquals("Should be initialized.", EdenConstants.ACTION_REQUEST_INITIALIZED, requestVO.getStatus());
                assertEquals("Wrong node name", SeqSetup.WORKFLOW_DOCUMENT_2_NODE, requestVO.getNodeName());
                approveToPmckown = true;
            } else if (netId.equals("temay")) {
                assertEquals("Should be acknowledge.", EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, requestVO.getActionRequested());
                assertEquals("Should be initialized.", EdenConstants.ACTION_REQUEST_INITIALIZED, requestVO.getStatus());
                assertEquals("Wrong node name", SeqSetup.ACKNOWLEDGE_1_NODE, requestVO.getNodeName());
                ackToTemay = true;
            } else if (netId.equals("jhopf")) {
                assertEquals("Should be acknowledge.", EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, requestVO.getActionRequested());
                assertEquals("Should be initialized.", EdenConstants.ACTION_REQUEST_INITIALIZED, requestVO.getStatus());
                assertEquals("Wrong node name", SeqSetup.ACKNOWLEDGE_2_NODE, requestVO.getNodeName());
                ackToJhopf = true;
            } 
            assertNotNull(requestVO.getNodeInstanceId());            
        }
        assertTrue("There should be an approve to bmcgough", approveToBmcgough);
        assertTrue("There should be an approve to rkirkend", approveToRkirkend);
        assertTrue("There should be an approve to pmckown", approveToPmckown);
        assertTrue("There should be an ack to temay", ackToTemay);
        assertTrue("There should be an ack to jhopf", ackToJhopf);
        
        // assert that the report call didn't save any of the nodes or requests
        activeNodeInstances = KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(document.getRouteHeaderId());
        requests = KEWServiceLocator.getActionRequestService().findAllActionRequestsByRouteHeaderId(document.getRouteHeaderId());
        assertEquals("Should be one active node.", 1, activeNodeInstances.size());
        assertEquals("Should be at the same node.", activeNodeId, ((RouteNodeInstance)activeNodeInstances.iterator().next()).getRouteNodeInstanceId());
        assertEquals("Should be 2 pending requests.", 2, requests.size());
        
        // test reporting to a specified target node
        criteria = new ReportCriteriaVO(document.getRouteHeaderId(), SeqSetup.ACKNOWLEDGE_1_NODE);
        documentDetail = info.routingReport(criteria);
        
        // document detail should have all of our requests except for the final acknowledge
        assertEquals("There should be 4 requets.", 4, documentDetail.getActionRequests().length);
        // assert that we don't have an acknowledge to jhopf
        for (int index = 0; index < documentDetail.getActionRequests().length; index++) {
            ActionRequestVO requestVO = documentDetail.getActionRequests()[index];
            if (requestVO.getUserVO().getNetworkId().equals("jhopf")) {
                fail("There should be no request to jhopf");
            }
        }
    }

    private static class SeqSetup {
        public static final String DOCUMENT_TYPE_NAME = "SeqDocType";
        public static final String ADHOC_NODE = "AdHoc";
        public static final String WORKFLOW_DOCUMENT_NODE = "WorkflowDocument";
        public static final String WORKFLOW_DOCUMENT_2_NODE = "WorkflowDocument2";
        public static final String ACKNOWLEDGE_1_NODE = "Acknowledge1";
        public static final String ACKNOWLEDGE_2_NODE = "Acknowledge2";
    }
    
    private static class DynSetup {
        public static final String DOCUMENT_TYPE_NAME = "DynChartOrgDocType";
        public static final String INITIAL_NODE = "Initial";
        public static final String CHART_ORG_NODE = "ChartOrg";
        public static final String SPLIT_NODE_NAME = "Organization Split";
        public static final String JOIN_NODE_NAME = "Organization Join";
        public static final String REQUEST_NODE_NAME = "Organization Request";
    }
}