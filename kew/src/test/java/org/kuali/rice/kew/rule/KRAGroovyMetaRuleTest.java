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
package org.kuali.rice.kew.rule;

import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.kew.clientapp.WorkflowDocument;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.workflow.test.KEWTestCase;


/**
 * Tests KRA meta-rule functionality KULRICE-1045
 * 
 * Tests meta rule implemented purely as a Groovy expression
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class KRAGroovyMetaRuleTest extends KEWTestCase {
    protected void loadTestData() throws Exception {
        loadXmlFile("KRAGroovyMetaRule.xml");
        loadXmlFile("KRAGroovyMetaRuleMaps.xml");
    }

    @Test public void testKRAGroovyMetaRule() throws WorkflowException {
        WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("arh14"), "KRAMetaRuleTest");
        doc.routeDocument("routing");

        doc = new WorkflowDocument(new NetworkIdDTO("user2"), doc.getRouteHeaderId());

        // user2 defined on bizRule4...the first rule that yields responsibilities
        assertTrue(doc.isApprovalRequested());
        
        doc.approve("approving as user2");
        
        doc = new WorkflowDocument(new NetworkIdDTO("user2"), doc.getRouteHeaderId());
        
        assertFalse(doc.isApprovalRequested());
        
        // now load it up as user3
        doc = new WorkflowDocument(new NetworkIdDTO("user3"), doc.getRouteHeaderId());

        // user1 defined on bizRule5...the second rule that yields responsibilities
        assertTrue(doc.isApprovalRequested());
        
        doc.approve("approving as user3");

        doc = new WorkflowDocument(new NetworkIdDTO("user3"), doc.getRouteHeaderId());
        assertFalse(doc.isApprovalRequested());
        doc = new WorkflowDocument(new NetworkIdDTO("user2"), doc.getRouteHeaderId());
        assertFalse(doc.isApprovalRequested());
        doc = new WorkflowDocument(new NetworkIdDTO("user1"), doc.getRouteHeaderId());
        assertFalse(doc.isApprovalRequested());
     
        assertTrue(doc.stateIsFinal());
    }

    @Test public void testKRAGroovyMetaRuleMaps() throws WorkflowException {
        WorkflowDocument doc = new WorkflowDocument(new NetworkIdDTO("arh14"), "KRAMetaRuleMapsTest");
        doc.routeDocument("routing");

        // xqi, shenl, dewey

        // test that TestWorkgroup requests get activated first
        doc = new WorkflowDocument(new NetworkIdDTO("xqi"), doc.getRouteHeaderId());
        assertTrue(doc.isApprovalRequested());
        doc = new WorkflowDocument(new NetworkIdDTO("shenl"), doc.getRouteHeaderId());
        assertFalse(doc.isApprovalRequested());
        doc = new WorkflowDocument(new NetworkIdDTO("dewey"), doc.getRouteHeaderId());
        assertFalse(doc.isApprovalRequested());
        
        doc = new WorkflowDocument(new NetworkIdDTO("xqi"), doc.getRouteHeaderId());
        doc.approve("approving as xqi");
        
        // next is shenl from the mock role
        doc = new WorkflowDocument(new NetworkIdDTO("xqi"), doc.getRouteHeaderId());
        assertFalse(doc.isApprovalRequested());
        doc = new WorkflowDocument(new NetworkIdDTO("shenl"), doc.getRouteHeaderId());
        assertTrue(doc.isApprovalRequested());
        doc = new WorkflowDocument(new NetworkIdDTO("jhopf"), doc.getRouteHeaderId());
        assertTrue(doc.isApprovalRequested());
        doc = new WorkflowDocument(new NetworkIdDTO("dewey"), doc.getRouteHeaderId());
        assertFalse(doc.isApprovalRequested());

        doc = new WorkflowDocument(new NetworkIdDTO("shenl"), doc.getRouteHeaderId());
        doc.approve("approving as shenl");
        
        // last is dewey from NonSIT workgroup
        doc = new WorkflowDocument(new NetworkIdDTO("xqi"), doc.getRouteHeaderId());
        assertFalse(doc.isApprovalRequested());
        doc = new WorkflowDocument(new NetworkIdDTO("shenl"), doc.getRouteHeaderId());
        assertFalse(doc.isApprovalRequested());
        doc = new WorkflowDocument(new NetworkIdDTO("jhopf"), doc.getRouteHeaderId());
        assertFalse(doc.isApprovalRequested());
        doc = new WorkflowDocument(new NetworkIdDTO("dewey"), doc.getRouteHeaderId());
        assertTrue(doc.isApprovalRequested());

        doc = new WorkflowDocument(new NetworkIdDTO("dewey"), doc.getRouteHeaderId());
        doc.approve("approving as dewey");
     
        assertTrue(doc.stateIsFinal());
    }
}