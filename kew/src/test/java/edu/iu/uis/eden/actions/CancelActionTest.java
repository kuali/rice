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
import java.util.List;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actions.BlanketApproveTest.NotifySetup;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;

public class CancelActionTest extends KEWTestCase {

    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }

    @Test public void testCancel() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        document.routeDocument("");
        
        document = new WorkflowDocument(new NetworkIdVO("jhopf"), document.getRouteHeaderId());
        document.approve("");
        
        document = new WorkflowDocument(new NetworkIdVO("ewestfal"), document.getRouteHeaderId());
        document.approve("");//ewestfal had ignore previous rule
        
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), document.getRouteHeaderId());
        document.approve("");
        
        //this be the role delegate of jitrue
        document = new WorkflowDocument(new NetworkIdVO("natjohns"), document.getRouteHeaderId());
        document.approve("");
        
        document = new WorkflowDocument(new NetworkIdVO("bmcgough"), document.getRouteHeaderId());
        document.cancel("");
        
        assertTrue("Document should be disapproved", document.stateIsCanceled());

        //verify that the document is truly dead - no more action requests or action items.
        
        List requests = KEWServiceLocator.getActionRequestService().findPendingByDoc(document.getRouteHeaderId());
        assertEquals("Should not have any active requests", 0, requests.size());
        
        Collection actionItems = KEWServiceLocator.getActionListService().findByRouteHeaderId(document.getRouteHeaderId());
        assertEquals("Should not have any action items", 0, actionItems.size());
        
        
    }

    @Test public void testInitiatorOnlyCancel() throws Exception {
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("ewestfal"), NotifySetup.DOCUMENT_TYPE_NAME);
        
        document = new WorkflowDocument(new NetworkIdVO("user1"), document.getRouteHeaderId());
        try {
            document.cancel("");
            fail("Document should not be allowed to be cancelled due to initiator check.");
        } catch (Exception e) {
            
        }
    }
}
