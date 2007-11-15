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
package edu.iu.uis.eden.engine.node;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.engine.node.hierarchyrouting.SimpleHierarchyProvider;
import edu.iu.uis.eden.engine.node.hierarchyrouting.HierarchyProvider.Stop;
import edu.iu.uis.eden.exception.WorkflowException;


/**
 * Tests HeirarchyRoutingNode
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class HierarchyRoutingNodeTest extends KEWTestCase {

    private static final String HIERARCHY =
    "<stop id=\"root\" type=\"user\" recipient=\"dewey\">" +
      "<stop id=\"child1\" type=\"user\" recipient=\"user3\">" +
        "<stop id=\"child1-1\" type=\"user\" recipient=\"user2\"/>" +
        "<stop id=\"child1-2\" type=\"user\" recipient=\"user1\"/>" +
      "</stop>" +
      "<stop id=\"child2\" type=\"user\" recipient=\"quickstart\">" +
        "<stop id=\"child2-1\" type=\"user\" recipient=\"temay\"/>" +
        "<stop id=\"child2-2\" type=\"user\" recipient=\"jhopf\"/>" +
      "</stop>" +
    "</stop>";
    private static final String HIERARCHY_UPDATED =
    "<stop id=\"root\" type=\"user\" recipient=\"dewey\">" +
      "<stop id=\"child1\" type=\"user\" recipient=\"user3\">" +
        "<stop id=\"child1-1\" type=\"user\" recipient=\"user2\"/>" +
        "<stop id=\"child1-2\" type=\"user\" recipient=\"user1\"/>" +
        "<stop id=\"child1-3\" type=\"user\" recipient=\"delyea\"/>" +
      "</stop>" +
      "<stop id=\"child2\" type=\"user\" recipient=\"quickstart\">" +
        "<stop id=\"child2-1\" type=\"user\" recipient=\"temay\"/>" +
        "<stop id=\"child2-2\" type=\"user\" recipient=\"jhopf\"/>" +
        "<stop id=\"child2-3\" type=\"user\" recipient=\"pzhang\"/>" +
      "</stop>" +
      "<stop id=\"child3\" type=\"user\" recipient=\"shenl\"/>" +
    "</stop>";

    @Test
    public void testParseHierarchy() throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(HIERARCHY)));
        SimpleHierarchyProvider provider = new SimpleHierarchyProvider(doc.getDocumentElement());
        Stop rootStop = provider.getStopByIdentifier("root");
        assertNotNull(rootStop);
        assertNull(provider.getParent(rootStop));
        Stop childStop = provider.getStopByIdentifier("child1");
        assertNotNull(childStop);
        assertEquals(rootStop, provider.getParent(childStop));
    }

    protected void assertApprovals(Long docId, String[] users, boolean shouldHaveApproval) throws WorkflowException {
        List<String> failedUsers = new ArrayList<String>();
        for (String user: users) {
            WorkflowDocument doc = new WorkflowDocument(new NetworkIdVO(user), docId);
            boolean appRqsted = doc.isApprovalRequested();
            if (shouldHaveApproval != appRqsted) {
                failedUsers.add(user);
            }
            log.error("User " + user + (appRqsted ? " HAS " : " HAS NO ") + "approval request");
        }
        for (String user: failedUsers) {
            log.error("User " + user + (shouldHaveApproval ? " should have " : " should NOT have ") + " approval");
        }
        if (failedUsers.size() > 0) {
            fail("Outstanding approvals are incorrect");
        }
    }

    @Test
    public void testHierarchyRoutingNode() throws WorkflowException {
        loadXmlFile("HierarchyRoutingNodeConfig.xml");
        
        WorkflowDocument doc = new WorkflowDocument(new NetworkIdVO("arh14"), "HierarchyDocType");
        
        
        doc.getDocumentContent().setApplicationContent(HIERARCHY);
        doc.routeDocument("initial route");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user2", "user1", "temay", "jhopf" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "quickstart", "dewey" }, false);
        
        doc = new WorkflowDocument(new NetworkIdVO("user2"), doc.getRouteHeaderId());
        doc.approve("approving as user2");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user1", "temay", "jhopf" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user2", "user3", "quickstart", "dewey" }, false);
        
        doc = new WorkflowDocument(new NetworkIdVO("jhopf"), doc.getRouteHeaderId());
        doc.approve("approving as jhopf");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user1", "temay" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "jhopf", "user2", "user3", "quickstart", "dewey" }, false);
        
        doc = new WorkflowDocument(new NetworkIdVO("user1"), doc.getRouteHeaderId());
        doc.approve("approving as user1");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "temay" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user1", "jhopf", "user2", "quickstart", "dewey" }, false);
        
        doc = new WorkflowDocument(new NetworkIdVO("temay"), doc.getRouteHeaderId());
        doc.approve("approving as temay");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "quickstart" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "temay", "user1", "jhopf", "user2", "dewey" }, false);
        
        doc = new WorkflowDocument(new NetworkIdVO("user3"), doc.getRouteHeaderId());
        doc.approve("approving as user3");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "quickstart" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "temay", "user1", "jhopf", "user2", "dewey" }, false);
        
        doc = new WorkflowDocument(new NetworkIdVO("quickstart"), doc.getRouteHeaderId());
        doc.approve("approving as quickstart");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "dewey" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "temay", "user1", "jhopf", "user2", "quickstart" }, false);
        

        doc = new WorkflowDocument(new NetworkIdVO("dewey"), doc.getRouteHeaderId());
        doc.approve("approving as dewey");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "dewey", "user3", "temay", "user1", "jhopf", "user2", "quickstart" }, false);
        
        assertTrue(doc.stateIsFinal());
    }
    
    @Test
    public void testHierarchyRoutingNodeUnevenApproval() throws WorkflowException {
        loadXmlFile("HierarchyRoutingNodeConfig.xml");
        
        WorkflowDocument doc = new WorkflowDocument(new NetworkIdVO("arh14"), "HierarchyDocType");
        
        doc.getDocumentContent().setApplicationContent(HIERARCHY);
        doc.routeDocument("initial route");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user2", "user1", "temay", "jhopf" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "quickstart", "dewey" }, false);
        
        doc = new WorkflowDocument(new NetworkIdVO("user2"), doc.getRouteHeaderId());
        doc.approve("approving as user2");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user1", "temay", "jhopf" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user2", "user3", "quickstart", "dewey" }, false);
        
        doc = new WorkflowDocument(new NetworkIdVO("jhopf"), doc.getRouteHeaderId());
        doc.approve("approving as jhopf");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user1", "temay" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "jhopf", "user2", "user3", "quickstart", "dewey" }, false);
        
        doc = new WorkflowDocument(new NetworkIdVO("user1"), doc.getRouteHeaderId());
        doc.approve("approving as user1");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "temay" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user1", "jhopf", "user2", "quickstart", "dewey" }, false);
        
        doc = new WorkflowDocument(new NetworkIdVO("user3"), doc.getRouteHeaderId());
        doc.approve("approving as user3");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "temay" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "user1", "jhopf", "user2", "dewey" }, false);
        
        doc = new WorkflowDocument(new NetworkIdVO("temay"), doc.getRouteHeaderId());
        doc.approve("approving as temay");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "quickstart" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "temay", "user1", "jhopf", "user2", "dewey" }, false);
        
        doc = new WorkflowDocument(new NetworkIdVO("quickstart"), doc.getRouteHeaderId());
        doc.approve("approving as quickstart");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "dewey" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "temay", "user1", "jhopf", "user2", "quickstart" }, false);

        doc = new WorkflowDocument(new NetworkIdVO("dewey"), doc.getRouteHeaderId());
        doc.approve("approving as dewey");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "dewey", "user3", "temay", "user1", "jhopf", "user2", "quickstart" }, false);
        
        assertTrue(doc.stateIsFinal());
    }
    
    @Test
    public void testHierarchyRoutingNodeUnevenApprovalExtraStops() throws WorkflowException {
        loadXmlFile("HierarchyRoutingNodeConfig.xml");
        
        WorkflowDocument doc = new WorkflowDocument(new NetworkIdVO("arh14"), "HierarchyDocType");
        
        doc.getDocumentContent().setApplicationContent(HIERARCHY);
        doc.routeDocument("initial route");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user2", "user1", "temay", "jhopf" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "quickstart", "dewey" }, false);
        
        doc = new WorkflowDocument(new NetworkIdVO("user2"), doc.getRouteHeaderId());
        doc.approve("approving as user2");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user1", "temay", "jhopf" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user2", "user3", "quickstart", "dewey" }, false);
        
        doc = new WorkflowDocument(new NetworkIdVO("jhopf"), doc.getRouteHeaderId());
        doc.approve("approving as jhopf");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user1", "temay" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "jhopf", "user2", "user3", "quickstart", "dewey" }, false);
        
        doc = new WorkflowDocument(new NetworkIdVO("user1"), doc.getRouteHeaderId());
        doc.setApplicationContent(HIERARCHY_UPDATED);
        doc.approve("approving as user1");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "temay", "delyea", "pzhang", "shenl" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user1", "jhopf", "user2", "quickstart", "dewey" }, false);
        
        doc = new WorkflowDocument(new NetworkIdVO("user3"), doc.getRouteHeaderId());
        doc.approve("approving as user3");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "temay", "delyea", "pzhang", "shenl" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "user1", "jhopf", "user2", "dewey" }, false);
        
        doc = new WorkflowDocument(new NetworkIdVO("temay"), doc.getRouteHeaderId());
        doc.approve("approving as temay");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "delyea", "pzhang", "shenl" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "temay", "user1", "jhopf", "user2", "dewey", "quickstart" }, false);
        
        doc = new WorkflowDocument(new NetworkIdVO("delyea"), doc.getRouteHeaderId());
        doc.approve("approving as delyea");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "user3", "pzhang", "shenl" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "delyea", "temay", "user1", "jhopf", "user2", "quickstart", "dewey" }, false);

        doc = new WorkflowDocument(new NetworkIdVO("user3"), doc.getRouteHeaderId());
        doc.approve("approving as user3");

        assertApprovals(doc.getRouteHeaderId(), new String[] { "pzhang", "shenl" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "delyea", "temay", "user1", "jhopf", "user2", "quickstart", "dewey" }, false);

        doc = new WorkflowDocument(new NetworkIdVO("pzhang"), doc.getRouteHeaderId());
        doc.approve("approving as pzhang");

        assertApprovals(doc.getRouteHeaderId(), new String[] { "quickstart", "shenl" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "pzhang", "delyea", "temay", "user1", "jhopf", "user2", "dewey" }, false);

        doc = new WorkflowDocument(new NetworkIdVO("quickstart"), doc.getRouteHeaderId());
        doc.approve("approving as quickstart");

        assertApprovals(doc.getRouteHeaderId(), new String[] { "shenl" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "pzhang", "delyea", "temay", "user1", "jhopf", "user2", "quickstart", "dewey" }, false);

        doc = new WorkflowDocument(new NetworkIdVO("shenl"), doc.getRouteHeaderId());
        doc.approve("approving as shenl");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "dewey" }, true);
        assertApprovals(doc.getRouteHeaderId(), new String[] { "pzhang", "delyea", "temay", "user1", "jhopf", "user2", "quickstart", "shenl" }, false);

        doc = new WorkflowDocument(new NetworkIdVO("dewey"), doc.getRouteHeaderId());
        doc.approve("approving as dewey");
        
        assertApprovals(doc.getRouteHeaderId(), new String[] { "shenl", "dewey", "pzhang", "delyea", "user3", "temay", "user1", "jhopf", "user2", "quickstart" }, false);
        
        assertTrue(doc.stateIsFinal());
    }
}
