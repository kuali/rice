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

import org.junit.Test;

import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;

import static org.junit.Assert.assertTrue;

/**
 * Test hierarchical routing coupled with KRA meta-rule
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class KRAMetaRuleHierarchyTest extends KEWTestCase {
    // this matches the hierarchy of meta-rules defined
    // in KRAMetaRuleHierarchy.xml
    private static final String HIERARCHY =
        "<stop id=\"top\">" +
          "<stop id=\"a\">" +
            "<stop id=\"a-1\"/>" +
            "<stop id=\"a-2\"/>" +
          "</stop>" +
          "<stop id=\"b\">" +
            "<stop id=\"b-1\"/>" +
            "<stop id=\"b-2\"/>" +
          "</stop>" +
        "</stop>";

    protected void approve(String user, Long docId) throws WorkflowException {
        log.info("Approving as " + user);
        WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName(user), docId);
        doc.approve("approving as " + user);
    }

    @Test
    public void test() throws WorkflowException {
        loadXmlFile("KRAMetaRuleHierarchy.xml");
        
        WorkflowDocument doc = new WorkflowDocument(getPrincipalIdForName("quickstart"), "KRAMetaRuleHierarchyTest");
        
        doc.getDocumentContent().setApplicationContent(HIERARCHY);
        doc.routeDocument("initial route");

        TestUtilities.logActionRequests(doc.getRouteHeaderId());
        // user 2 is before user3 because of ordering between business rules included by meta-rule
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "user2", "shenl", "jhopf", "ewestfal" }, true);
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "user1", "arh14", "rkirkend", "xqi" }, false);
        
        approve("user2", doc.getRouteHeaderId());

        TestUtilities.logActionRequests(doc.getRouteHeaderId());
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "shenl", "jhopf", "ewestfal" }, true);
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "user2", "user1", "arh14", "rkirkend", "xqi" }, false);

        approve("shenl", doc.getRouteHeaderId());
        
        TestUtilities.logActionRequests(doc.getRouteHeaderId());
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "jhopf", "ewestfal" }, true);
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "shenl", "user2", "user1", "arh14", "rkirkend", "xqi" }, false);
        
        approve("jhopf", doc.getRouteHeaderId());
        
        TestUtilities.logActionRequests(doc.getRouteHeaderId());
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "ewestfal" }, true);
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "jhopf", "shenl", "user2", "user1", "arh14", "rkirkend", "xqi" }, false);
        
        approve("ewestfal", doc.getRouteHeaderId());
        
        TestUtilities.logActionRequests(doc.getRouteHeaderId());
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "rkirkend" }, true);
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "ewestfal", "shenl", "user2", "user1", "arh14", "xqi" }, false);
        
        approve("rkirkend", doc.getRouteHeaderId());
        
        TestUtilities.logActionRequests(doc.getRouteHeaderId());
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "user3" }, true);
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "rkirkend", "ewestfal", "shenl", "user2", "user1", "arh14", "xqi" }, false);
        
        approve("user3", doc.getRouteHeaderId());
        
        TestUtilities.logActionRequests(doc.getRouteHeaderId());
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "arh14" }, true);
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "rkirkend", "ewestfal", "shenl", "user2", "user1", "xqi" }, false);
        
        approve("arh14", doc.getRouteHeaderId());
        
        TestUtilities.logActionRequests(doc.getRouteHeaderId());
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "user1" }, true);
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "arh14", "user3", "rkirkend", "ewestfal", "shenl", "user2", "arh14", "xqi" }, false);
        
        approve("user1", doc.getRouteHeaderId());
        
        TestUtilities.logActionRequests(doc.getRouteHeaderId());        
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "xqi" }, true);
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "user1", "arh14", "user3", "rkirkend", "ewestfal", "shenl", "user2" }, false);
        
        approve("xqi", doc.getRouteHeaderId());
        
        TestUtilities.logActionRequests(doc.getRouteHeaderId());
        TestUtilities.assertApprovals(doc.getRouteHeaderId(), new String[] { "xqi", "user1", "arh14", "user3", "rkirkend", "ewestfal", "shenl", "user2" }, false);

        TestUtilities.logActionRequests(doc.getRouteHeaderId());

        doc = new WorkflowDocument(getPrincipalIdForName("quickstart"), doc.getRouteHeaderId());
        assertTrue(doc.stateIsFinal());
    }
}
