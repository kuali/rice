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
package org.kuali.rice.kew.actionrequest;


import org.junit.Test;
import org.kuali.rice.kew.actionrequest.service.DocumentRequeuerService;
import org.kuali.rice.kew.dto.ActionRequestDTO;

import org.kuali.rice.kew.messaging.MessageServiceNames;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests the DocumentRequeuer route queue processor.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentRequeuerTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionRequestsConfig.xml");
    }

    @Test public void testDocumentRequeueSingleNode() throws Exception {
       WorkflowDocument document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), SeqSetup.DOCUMENT_TYPE_NAME);
       document.routeDocument("");
       document = new WorkflowDocument(getPrincipalIdForName("ewestfal"), document.getRouteHeaderId());
       assertTrue(document.stateIsEnroute());
       ActionRequestDTO[] requests = document.getActionRequests();
       assertEquals("Should be 2 requests.", 2, requests.length);
       // save off request ids
       Set<Long> requestIds = new HashSet<Long>();
        for (ActionRequestDTO requestVO : requests)
        {
            requestIds.add(requestVO.getActionRequestId());
        }

       DocumentRouteHeaderValue documentH = KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId());
       DocumentRequeuerService documentRequeuer = MessageServiceNames.getDocumentRequeuerService(documentH.getDocumentType().getServiceNamespace(), documentH.getRouteHeaderId(), 0);
       documentRequeuer.requeueDocument(document.getRouteHeaderId());

       // initiate a requeue of the document
//       SpringServiceLocator.getRouteQueueService().requeueDocument(document.getRouteHeaderId(), DocumentRequeuerImpl.class.getName());

       document = new WorkflowDocument(getPrincipalIdForName("bmcgough"), document.getRouteHeaderId());
       assertTrue(document.stateIsEnroute());
       requests = document.getActionRequests();
       assertEquals("Should be 2 requests.", 2, requests.length);
       for (int index = 0; index < requests.length; index++) {
           ActionRequestDTO requestVO = requests[index];
           assertTrue("Request ids should be different.", !requestIds.contains(requestVO.getActionRequestId()));
       }
       assertTrue(document.isApprovalRequested());
       document.approve("");

       // now there should just be a pending request to ryan, let's requeue again, because of force action = false we should still
       // have only one pending request to ryan
//       SpringServiceLocator.getRouteQueueService().requeueDocument(document.getRouteHeaderId(), DocumentRequeuerImpl.class.getName());
       documentRequeuer.requeueDocument(document.getRouteHeaderId());
       document = new WorkflowDocument(getPrincipalIdForName("rkirkend"), document.getRouteHeaderId());
       assertTrue(document.stateIsEnroute());
       requests = document.getActionRequests();
       assertEquals("Should be 2 requests.", 2, requests.length);
       // there should only be one pending request to rkirkend
       boolean pendingToRkirkend = false;
        for (ActionRequestDTO requestVO : requests)
        {
            if (requestVO.getPrincipalId().equals(getPrincipalIdForName("rkirkend")) && requestVO.isActivated())
            {
                assertFalse("rkirkend has too many requests!", pendingToRkirkend);
                pendingToRkirkend = true;
            } else
            {
                assertTrue("previous request to all others should be done.", requestVO.isDone());
            }
        }
       assertTrue(document.isApprovalRequested());
   }

   private class SeqSetup {
       public static final String DOCUMENT_TYPE_NAME = "DRSeqDocType";
       public static final String ADHOC_NODE = "AdHoc";
       public static final String WORKFLOW_DOCUMENT_NODE = "WorkflowDocument";
       public static final String WORKFLOW_DOCUMENT_2_NODE = "WorkflowDocument2";
   }

}
