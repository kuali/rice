package org.kuali.rice.kew.impl.peopleflow;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionRequestStatus;
import org.kuali.rice.kew.api.action.RecipientType;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowService;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.test.BaselineTestCase;

import java.util.List;

import static org.junit.Assert.*;

/**
 * An integration test which tests document types with nodes on them which route to PeopleFlows in various configurations.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class PeopleFlowRoutingTest extends KEWTestCase {

    private static final String NAMESPACE_CODE = "TEST";
    private static final String PEOPLE_FLOW_1 = "PeopleFlow1";
    private static final String PEOPLE_FLOW_2 = "PeopleFlow2";

    private static final String SINGLE_PEOPLE_FLOW_PARALLEL_APPROVE = "SinglePeopleFlow-Parallel-Approve";
    private static final String SINGLE_PEOPLE_FLOW_SEQUENTIAL_APPROVE = "SinglePeopleFlow-Sequential-Approve";
    private static final String SINGLE_PEOPLE_FLOW_PRIORITY_PARALLEL_APPROVE = "SinglePeopleFlow-PriorityParallel-Approve";

    private PeopleFlowService peopleFlowService;

    private String user1;
    private String user2;
    private String user3;
    private String testuser1;
    private String testuser2;
    private String testuser3;
    private String ewestfal;
    private String testWorkgroup;

    protected void loadTestData() throws Exception {
        loadXmlFile("PeopleFlowRoutingTest.xml");
    }

    @Before
    public void setupServiceUnderTest() {
        setPeopleFlowService(KewApiServiceLocator.getPeopleFlowService());

        // setup principal and group ids
        user1 = getPrincipalIdForName("user1");
        user2 = getPrincipalIdForName("user2");
        user3 = getPrincipalIdForName("user3");
        testuser1 = getPrincipalIdForName("testuser1");
        testuser2 = getPrincipalIdForName("testuser2");
        testuser3 = getPrincipalIdForName("testuser3");
        ewestfal = getPrincipalIdForName("ewestfal");
        testWorkgroup = getGroupIdForName("KR-WKFLW", "TestWorkgroup");
    }

    protected void setPeopleFlowService(PeopleFlowService peopleFlowService) {
        this.peopleFlowService = peopleFlowService;
    }

    protected PeopleFlowService getPeopleFlowService() {
        return peopleFlowService;
    }

    private void createSimplePeopleFlow() {
        PeopleFlowDefinition.Builder peopleFlow = PeopleFlowDefinition.Builder.create(NAMESPACE_CODE, PEOPLE_FLOW_1);
        peopleFlow.addPrincipal(user1).setPriority(1);
        peopleFlow.addPrincipal(user2).setPriority(2);
        peopleFlow.addGroup(testWorkgroup).setPriority(3);
        peopleFlowService.createPeopleFlow(peopleFlow.build());
    }

    private void createPriorityParallelPeopleFlow() {
        PeopleFlowDefinition.Builder peopleFlow = PeopleFlowDefinition.Builder.create(NAMESPACE_CODE, PEOPLE_FLOW_2);
        peopleFlow.addPrincipal(user1).setPriority(1);
        peopleFlow.addPrincipal(user2).setPriority(1);
        peopleFlow.addPrincipal(testuser1).setPriority(2);
        peopleFlow.addPrincipal(testuser2).setPriority(2);
        // add the last two at a priority which is not contiguous, should still work as expected
        peopleFlow.addGroup(testWorkgroup).setPriority(10);
        peopleFlow.addPrincipal(testuser3).setPriority(10);
        peopleFlowService.createPeopleFlow(peopleFlow.build());
    }

    @Test
    public void test_SinglePeopleFlow_Parallel_Approve() throws Exception {
        createSimplePeopleFlow();

        // now route a document to it
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(user3, SINGLE_PEOPLE_FLOW_PARALLEL_APPROVE);
        document.route("");
        assertTrue("Document should be enroute.", document.isEnroute());

        // user3 should not have an approval request since they initiated the document
        document.switchPrincipal(user3);

        // user1, user2, and TestWorkgroup (of which ewestfal is a member) should have the request
        assertApproveRequested(document, user1, user2, ewestfal);

        // now approve as each, the document should be FINAL at the end
        document.switchPrincipal(ewestfal);
        document.approve("approving as ewestfal");
        assertTrue("Document should still be enroute.", document.isEnroute());
        document.switchPrincipal(user1);
        document.approve("approving as user1");
        assertTrue("Document should still be enroute.", document.isEnroute());
        document.switchPrincipal(user2);
        document.approve("approving as user2");
        assertTrue("Document should now be FINAL.", document.isFinal());
    }

    @Test
    public void test_SinglePeopleFlow_Sequential_Approve() throws Exception {
        createSimplePeopleFlow();

        // now route a document to it
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(user3, SINGLE_PEOPLE_FLOW_SEQUENTIAL_APPROVE);
        document.route("");
        assertTrue("Document should be enroute.", document.isEnroute());

        // user3 should not have an approval request since they initiated the document
        document.switchPrincipal(user3);

        // document should be routed to user1, user2, and TestWorkgroup (of which "ewestfal" is a member) but only
        // user1 request should be activated

        List<ActionRequest> rootActionRequests = document.getRootActionRequests();
        assertEquals("Should have 3 root action requests", 3, rootActionRequests.size());
        ActionRequest user1Request = null;
        ActionRequest user2Request = null;
        ActionRequest testWorkgroupRequest = null;
        for (ActionRequest actionRequest : rootActionRequests) {
            RecipientType recipientType = actionRequest.getRecipientType();
            if (recipientType == RecipientType.PRINCIPAL) {
                if (user1.equals(actionRequest.getPrincipalId())) {
                    user1Request = actionRequest;
                } else if (user2.equals(actionRequest.getPrincipalId())) {
                    user2Request = actionRequest;
                }
            } else if (recipientType == RecipientType.GROUP) {
                if (testWorkgroup.equals(actionRequest.getGroupId())) {
                    testWorkgroupRequest = actionRequest;
                }
            }
        }
        // now let's ensure we got the requests we wanted
        assertNotNull(user1Request);
        assertEquals(ActionRequestStatus.ACTIVATED, user1Request.getStatus());
        assertNotNull(user2Request);
        assertEquals(ActionRequestStatus.INITIALIZED, user2Request.getStatus());
        assertNotNull(testWorkgroupRequest);
        assertEquals(ActionRequestStatus.INITIALIZED, testWorkgroupRequest.getStatus());

        // let's double-check that before we start approving
        assertApproveNotRequested(document, user2, ewestfal);

        // user1 should have the request for approval, however
        assertApproveRequested(document, user1);

        // approve as user1
        document.switchPrincipal(user1);
        document.approve("");

        // should still be enroute
        assertTrue(document.isEnroute());

        // now user1 should no longer have it, it should be activated approve to user2 with TestWorkgroup still initialized but not activated
        assertApproveNotRequested(document, user1, ewestfal);
        assertApproveRequested(document, user2);

        // approve as user2
        document.switchPrincipal(user2);
        document.approve("");
        // should still be enroute
        assertTrue(document.isEnroute());

        // now user1 and user2 have approved, should be activated to TestWorkgroup of which ewestfal is a member
        assertApproveNotRequested(document, user2, user1);

        // ewestfal should have an approve request because he is a member of TestWorkgroup
        assertApproveRequested(document, ewestfal);
        document.switchPrincipal(ewestfal);
        document.approve("");

        // now document should be final!
        assertTrue(document.isFinal());
    }

    @Test
    public void test_SinglePeopleFlow_PriorityParallel_Approve() throws Exception {
        createPriorityParallelPeopleFlow();

        // now route a document to it
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(user3, SINGLE_PEOPLE_FLOW_PRIORITY_PARALLEL_APPROVE);
        document.route("");
        assertTrue("Document should be enroute.", document.isEnroute());

        // user3 should not have an approval request since they initiated the document
        document.switchPrincipal(user3);

        // document should be routed to user1, user2, testuser1, testuser2, TestWorkgroup, and testuser3
        // But only user1 and user2 requests should be activated since those are at priority 1

        List<ActionRequest> rootActionRequests = document.getRootActionRequests();
        assertEquals("Should have 6 root action requests", 6, rootActionRequests.size());
        ActionRequest user1Request = null;
        ActionRequest user2Request = null;
        ActionRequest testuser1Request = null;
        ActionRequest testuser2Request = null;
        ActionRequest testWorkgroupRequest = null;
        ActionRequest testuser3Request = null;
        for (ActionRequest actionRequest : rootActionRequests) {
            RecipientType recipientType = actionRequest.getRecipientType();
            if (recipientType == RecipientType.PRINCIPAL) {
                if (user1.equals(actionRequest.getPrincipalId())) {
                    user1Request = actionRequest;
                } else if (user2.equals(actionRequest.getPrincipalId())) {
                    user2Request = actionRequest;
                } else if (testuser1.equals(actionRequest.getPrincipalId())) {
                    testuser1Request = actionRequest;
                } else if (testuser2.equals(actionRequest.getPrincipalId())) {
                    testuser2Request = actionRequest;
                } else if (testuser3.equals(actionRequest.getPrincipalId())) {
                    testuser3Request = actionRequest;
                }
            } else if (recipientType == RecipientType.GROUP) {
                if (testWorkgroup.equals(actionRequest.getGroupId())) {
                    testWorkgroupRequest = actionRequest;
                }
            }
        }
        // now let's ensure we got the requests we wanted
        assertNotNull(user1Request);
        assertEquals(ActionRequestStatus.ACTIVATED, user1Request.getStatus());
        assertNotNull(user2Request);
        assertEquals(ActionRequestStatus.ACTIVATED, user2Request.getStatus());
        assertNotNull(testuser1Request);
        assertEquals(ActionRequestStatus.INITIALIZED, testuser1Request.getStatus());
        assertNotNull(testuser2Request);
        assertEquals(ActionRequestStatus.INITIALIZED, testuser2Request.getStatus());
        assertNotNull(testWorkgroupRequest);
        assertEquals(ActionRequestStatus.INITIALIZED, testWorkgroupRequest.getStatus());
        assertNotNull(testuser3Request);
        assertEquals(ActionRequestStatus.INITIALIZED, testuser3Request.getStatus());

        // let's double-check that before we start approving

        assertApproveRequested(document, user1, user2);
        assertApproveNotRequested(document, testuser1, testuser2, testuser3, ewestfal);

        // TODO...

    }

    private void assertApproveRequested(WorkflowDocument document, String... principalIds) {
        for (String principalId : principalIds) {
            document.switchPrincipal(principalId);
            assertTrue("Approve should have been requested for '" + principalId + "'", document.isApprovalRequested());
        }
    }

    private void assertApproveNotRequested(WorkflowDocument document, String... principalIds) {
        for (String principalId : principalIds) {
            document.switchPrincipal(principalId);
            assertFalse("Approve should *NOT* have been requested for '" + principalId + "'", document.isApprovalRequested());
        }
    }



}
