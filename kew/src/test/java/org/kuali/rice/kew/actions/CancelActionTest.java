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
package org.kuali.rice.kew.actions;

import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.kuali.rice.kew.actions.BlanketApproveTest.NotifySetup;
import org.kuali.rice.kew.clientapp.WorkflowDocument;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.workflow.test.KEWTestCase;


public class CancelActionTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }

    @Test public void testCancel() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        
        document = new WorkflowDocument(new NetworkIdDTO("jhopf"), document.getRouteHeaderId());
        document.approve("");
        
        document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), document.getRouteHeaderId());
        document.approve("");//ewestfal had ignore previous rule
        
        document = new WorkflowDocument(new NetworkIdDTO("rkirkend"), document.getRouteHeaderId());
        document.approve("");
        
        //this be the role delegate of jitrue
        document = new WorkflowDocument(new NetworkIdDTO("natjohns"), document.getRouteHeaderId());
        document.approve("");
        
        document = new WorkflowDocument(new NetworkIdDTO("bmcgough"), document.getRouteHeaderId());
        document.cancel("");
        
        assertTrue("Document should be disapproved", document.stateIsCanceled());

        //verify that the document is truly dead - no more action requests or action items.
        
        List requests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertEquals("Should not have any active requests", 0, requests.size());
        
        Collection actionItems = KEWServiceLocator.getActionListService().findByRouteHeaderId(document.getRouteHeaderId());
        assertEquals("Should not have any action items", 0, actionItems.size());
        
        
    }

    @Test public void testInitiatorOnlyCancel() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdDTO("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        
        document = new WorkflowDocument(new NetworkIdDTO("user1"), document.getRouteHeaderId());
        try {
            document.cancel("");
            fail("Document should not be allowed to be cancelled due to initiator check.");
        } catch (Exception e) {
            
        }
    }
}
