/*
 * Copyright 2007 The Kuali Foundation
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
package edu.iu.uis.eden.routetemplate;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Tests KRA meta-rule functionality KULRICE-1045
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KRAMetaRuleTest extends KEWTestCase {
    protected void loadTestData() throws Exception {
        loadXmlFile("KRAMetaRule.xml");
    }

    @Test public void testKRAMetaRule() throws WorkflowException {
        WorkflowDocument doc = new WorkflowDocument(new NetworkIdVO("arh14"), "KRAMetaRuleTest");
        doc.routeDocument("routing");

        doc = new WorkflowDocument(new NetworkIdVO("user2"), doc.getRouteHeaderId());

        // user2 defined on bizRule4...the first rule that yields responsibilities
        assertTrue(doc.isApprovalRequested());
        
        doc.approve("approving as user2");
        
        doc = new WorkflowDocument(new NetworkIdVO("user2"), doc.getRouteHeaderId());
        
        assertFalse(doc.isApprovalRequested());
        
        // now load it up as user3
        doc = new WorkflowDocument(new NetworkIdVO("user3"), doc.getRouteHeaderId());

        // user1 defined on bizRule5...the second rule that yields responsibilities
        assertTrue(doc.isApprovalRequested());
        
        doc.approve("approving as user3");

        doc = new WorkflowDocument(new NetworkIdVO("user3"), doc.getRouteHeaderId());
        assertFalse(doc.isApprovalRequested());
        doc = new WorkflowDocument(new NetworkIdVO("user2"), doc.getRouteHeaderId());
        assertFalse(doc.isApprovalRequested());
        doc = new WorkflowDocument(new NetworkIdVO("user1"), doc.getRouteHeaderId());
        assertFalse(doc.isApprovalRequested());
     
        assertTrue(doc.stateIsFinal());
    }
}