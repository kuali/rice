/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kew.rule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.test.KEWTestCase;

/**
 * Tests KRA meta-rule functionality KULRICE-1045
 * 
 * Tests meta rule implemented purely as a Groovy expression
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KRAGroovyMetaRuleTest extends KEWTestCase {
    protected void loadTestData() throws Exception {
        loadXmlFile("KRAGroovyMetaRule.xml");
        loadXmlFile("KRAGroovyMetaRuleMaps.xml");
    }

    @Test public void testKRAGroovyMetaRule() throws WorkflowException {
        WorkflowDocument doc = WorkflowDocumentFactory.createDocument(getPrincipalIdForName("arh14"), "KRAMetaRuleTest");
        doc.route("routing");

        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("user2"), doc.getDocumentId());

        // user2 defined on bizRule4...the first rule that yields responsibilities
        assertTrue(doc.isApprovalRequested());
        
        doc.approve("approving as user2");
        
        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("user2"), doc.getDocumentId());
        
        assertFalse(doc.isApprovalRequested());
        
        // now load it up as user3
        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("user3"), doc.getDocumentId());

        // user1 defined on bizRule5...the second rule that yields responsibilities
        assertTrue(doc.isApprovalRequested());
        
        doc.approve("approving as user3");

        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("user3"), doc.getDocumentId());
        assertFalse(doc.isApprovalRequested());
        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("user2"), doc.getDocumentId());
        assertFalse(doc.isApprovalRequested());
        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("user1"), doc.getDocumentId());
        assertFalse(doc.isApprovalRequested());
     
        assertTrue(doc.isFinal());
    }

    @Test public void testKRAGroovyMetaRuleMaps() throws WorkflowException {
        WorkflowDocument doc = WorkflowDocumentFactory.createDocument(getPrincipalIdForName("arh14"), "KRAMetaRuleMapsTest");
        doc.route("routing");

        // xqi, shenl, dewey

        // test that TestWorkgroup requests get activated first
        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("xqi"), doc.getDocumentId());
        assertTrue(doc.isApprovalRequested());
        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("shenl"), doc.getDocumentId());
        assertFalse(doc.isApprovalRequested());
        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("dewey"), doc.getDocumentId());
        assertFalse(doc.isApprovalRequested());
        
        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("xqi"), doc.getDocumentId());
        doc.approve("approving as xqi");
        
        // next is shenl from the mock role
        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("xqi"), doc.getDocumentId());
        assertFalse(doc.isApprovalRequested());
        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("shenl"), doc.getDocumentId());
        assertTrue(doc.isApprovalRequested());
        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("jhopf"), doc.getDocumentId());
        assertTrue(doc.isApprovalRequested());
        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("dewey"), doc.getDocumentId());
        assertFalse(doc.isApprovalRequested());

        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("shenl"), doc.getDocumentId());
        doc.approve("approving as shenl");
        
        // last is dewey from NonSIT workgroup
        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("xqi"), doc.getDocumentId());
        assertFalse(doc.isApprovalRequested());
        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("shenl"), doc.getDocumentId());
        assertFalse(doc.isApprovalRequested());
        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("jhopf"), doc.getDocumentId());
        assertFalse(doc.isApprovalRequested());
        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("dewey"), doc.getDocumentId());
        assertTrue(doc.isApprovalRequested());

        doc = WorkflowDocumentFactory.loadDocument(getPrincipalIdForName("dewey"), doc.getDocumentId());
        doc.approve("approving as dewey");
     
        assertTrue(doc.isFinal());
    }
}
