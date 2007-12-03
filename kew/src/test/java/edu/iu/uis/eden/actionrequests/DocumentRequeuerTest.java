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
package edu.iu.uis.eden.actionrequests;


import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.messaging.MessageServiceNames;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;

/**
 * Tests the DocumentRequeuer route queue processor.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentRequeuerTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionRequestsConfig.xml");
    }

    @Test public void testDocumentRequeueSingleNode() throws Exception {
       WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), SeqSetup.DOCUMENT_TYPE_NAME);
       document.routeDocument("");
       document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
       assertTrue(document.stateIsEnroute());
       ActionRequestVO[] requests = document.getActionRequests();
       assertEquals("Should be 2 requests.", 2, requests.length);
       // save off request ids
       Set<Long> requestIds = new HashSet<Long>();
       for (int index = 0; index < requests.length; index++) {
           ActionRequestVO requestVO = requests[index];
           requestIds.add(requestVO.getActionRequestId());
       }

       DocumentRouteHeaderValue documentH = KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId());
       DocumentRequeuerService documentRequeuer = MessageServiceNames.getDocumentRequeuerService(documentH.getDocumentType().getMessageEntity(), documentH.getRouteHeaderId(), 0);
       documentRequeuer.requeueDocument(document.getRouteHeaderId());

       // initiate a requeue of the document
//       SpringServiceLocator.getRouteQueueService().requeueDocument(document.getRouteHeaderId(), DocumentRequeuerImpl.class.getName());

       document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
       assertTrue(document.stateIsEnroute());
       requests = document.getActionRequests();
       assertEquals("Should be 2 requests.", 2, requests.length);
       for (int index = 0; index < requests.length; index++) {
           ActionRequestVO requestVO = requests[index];
           assertTrue("Request ids should be different.", !requestIds.contains(requestVO.getActionRequestId()));
       }
       assertTrue(document.isApprovalRequested());
       document.approve("");

       // now there should just be a pending request to ryan, let's requeue again, because of ignore previous = false we should still
       // have only one pending request to ryan
//       SpringServiceLocator.getRouteQueueService().requeueDocument(document.getRouteHeaderId(), DocumentRequeuerImpl.class.getName());
       documentRequeuer.requeueDocument(document.getRouteHeaderId());
       document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
       assertTrue(document.stateIsEnroute());
       requests = document.getActionRequests();
       assertEquals("Should be 2 requests.", 2, requests.length);
       // there should only be one pending request to rkirkend
       boolean pendingToRkirkend = false;
       for (int index = 0; index < requests.length; index++) {
           ActionRequestVO requestVO = requests[index];
           if (requestVO.getUserVO().getNetworkId().equals("rkirkend") && requestVO.isActivated()) {
               assertFalse("rkirkend has too many requests!", pendingToRkirkend);
               pendingToRkirkend = true;
           } else {
               assertTrue("previous request to all others should be done.", requestVO.isDone());
           }
       }
       assertTrue(document.isApprovalRequested());
//       WorkflowReports reports = new WorkflowReports();
//       assertTrue(reports.isLastApproverAtNode(document.getRouteHeaderId(), new NetworkIdVO("rkirkend"), SeqSetup.WORKFLOW_DOCUMENT_NODE));
   }

   @Ignore("This test needs to be implemented!")
   @Test public void testDocumentRequeueMultipleNodes() throws Exception {
       // TODO time permitting we should write a test here which attempts to requeue a document which is sitting at multiple nodes
   }

   private class SeqSetup {
       public static final String DOCUMENT_TYPE_NAME = "DRSeqDocType";
       public static final String ADHOC_NODE = "AdHoc";
       public static final String WORKFLOW_DOCUMENT_NODE = "WorkflowDocument";
       public static final String WORKFLOW_DOCUMENT_2_NODE = "WorkflowDocument2";
   }

}
