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
package edu.iu.uis.eden.routemanager;

import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.messaging.MessageQueueService;
import edu.iu.uis.eden.messaging.PersistedMessage;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routeheader.RouteHeaderService;
import edu.iu.uis.eden.test.TestUtilities;

/**
 * @author ewestfal
 */
public abstract class RouteManagerTestCase extends KEWTestCase {
    
//    protected PersistedMessage makeNewRouteQueue() throws Exception {
//        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("rkirkend"), "TestDocumentType");
//        document.saveRoutingData();
//        DocumentRouteHeaderValue routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(document.getRouteHeaderId());
//        PersistedMessage routeQueue = TestUtilities.createRouteQueue(routeHeader);
//        return routeQueue;
//    }

    protected String getAltAppContextFile() {
        return "edu/iu/uis/eden/routequeue/RouteQueueSpring.xml";
    }

    protected DocumentTypeService getDocumentTypeService() {
        return (DocumentTypeService)KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
    }
    
    protected RouteHeaderService getRouteHeaderService() {
        return (RouteHeaderService)KEWServiceLocator.getService(KEWServiceLocator.DOC_ROUTE_HEADER_SRV);
    }
    
    protected MessageQueueService getRouteQueueService() {
        return (MessageQueueService)KEWServiceLocator.getService(KEWServiceLocator.ROUTE_QUEUE_SRV);
    }
    
//    protected RouteManagerQueueService getRouteManagerQueueService() {
//        return (RouteManagerQueueService)SpringServiceLocator.getService(SpringServiceLocator.ROUTE_MANAGER_QUEUE_SERVICE);
//    }
    
}
