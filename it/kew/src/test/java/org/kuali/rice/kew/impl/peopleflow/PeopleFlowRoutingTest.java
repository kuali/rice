package org.kuali.rice.kew.impl.peopleflow;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionRequestStatus;
import org.kuali.rice.kew.api.action.DelegationType;
import org.kuali.rice.kew.api.action.RecipientType;
import org.kuali.rice.kew.api.peopleflow.MemberType;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDelegate;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowMember;
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
    private static final String PEOPLE_FLOW_3 = "PeopleFlow3";

    private static final String SINGLE_PEOPLE_FLOW_PARALLEL_APPROVE = "SinglePeopleFlow-Parallel-Approve";
    private static final String SINGLE_PEOPLE_FLOW_SEQUENTIAL_APPROVE = "SinglePeopleFlow-Sequential-Approve";
    private static final String SINGLE_PEOPLE_FLOW_PRIORITY_PARALLEL_APPROVE = "SinglePeopleFlow-PriorityParallel-Approve";
    private static final String MULTIPLE_PEOPLE_FLOW_PRIORITY_PARALLEL = "MultiplePeopleFlow-PriorityParallel";
    private static final String DELEGATE_PEOPLE_FLOW_PRIORITY_PARALLEL_APPROVE = "DelegatePeopleFlow-PriorityParallel-Approve";

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

        // approve as user1 and user2
        document.switchPrincipal(user1);
        document.approve("");
        assertApproveRequested(document, user2);
        assertApproveNotRequested(document, user1);
        document.switchPrincipal(user2);
        document.approve("");
        assertTrue(document.isEnroute());

        assertApproveRequested(document, testuser1, testuser2);
        assertApproveNotRequested(document, user1, user2, testuser3, ewestfal);

        // approve as testuser1 and testuser2
        document.switchPrincipal(testuser1);
        document.approve("");
        document.switchPrincipal(testuser2);
        document.approve("");
        assertTrue(document.isEnroute());

        assertApproveRequested(document, testuser3, ewestfal);
        assertApproveNotRequested(document, user1, user2, testuser1, testuser2);

        // now approve as ewestfal and testuser3, then doc should be final
        document.switchPrincipal(testuser3);
        document.approve("");
        document.switchPrincipal(ewestfal);
        document.approve("");
        assertTrue(document.isFinal());
    }

    /**
     * Defines PeopleFlow as follows:
     *
     * 1 - PeopleFlow - TEST:PeopleFlow1
     *   -> Priority 1
     *   ----> user1
     *   -> Priority 2
     *   ----> user2
     * 2 - PeopleFlow - TEST:PeopleFlow2
     *   -> Priority 1
     *   ----> testuser1
     *   -> Priority 2
     *   ----> testuser2
     * 3 - PeopleFlow - TEST:PeopleFlow3
     *   -> Priority 1
     *   ----> TestWorkgroup
     *   -> Priority 10
     *   ----> testuser3
     */
    private void createMultiplePeopleFlows() {
        PeopleFlowDefinition.Builder peopleFlow1 = PeopleFlowDefinition.Builder.create(NAMESPACE_CODE, PEOPLE_FLOW_1);
        peopleFlow1.addPrincipal(user1).setPriority(1);
        peopleFlow1.addPrincipal(user2).setPriority(2);
        peopleFlowService.createPeopleFlow(peopleFlow1.build());

        PeopleFlowDefinition.Builder peopleFlow2 = PeopleFlowDefinition.Builder.create(NAMESPACE_CODE, PEOPLE_FLOW_2);
        peopleFlow2.addPrincipal(testuser1).setPriority(1);
        peopleFlow2.addPrincipal(testuser2).setPriority(2);
        peopleFlowService.createPeopleFlow(peopleFlow2.build());

        PeopleFlowDefinition.Builder peopleFlow3 = PeopleFlowDefinition.Builder.create(NAMESPACE_CODE, PEOPLE_FLOW_3);
        peopleFlow3.addGroup(testWorkgroup).setPriority(1);
        peopleFlow3.addPrincipal(testuser3).setPriority(10);
        peopleFlowService.createPeopleFlow(peopleFlow3.build());
    }


    @Test
    public void test_MultiplePeopleFlow_PriorityParallel() throws Exception {
        createMultiplePeopleFlows();

        // now route a document to it
        WorkflowDocument document = WorkflowDocumentFactory.createDocument(user3, MULTIPLE_PEOPLE_FLOW_PRIORITY_PARALLEL);
        document.route("");
        assertTrue("Document should be enroute.", document.isEnroute());

        // user3 should not have an approval request since they initiated the document
        document.switchPrincipal(user3);

        // document should only send requests to the first people flow which should be user1 and user2
        // But only user1 request should be activated

        List<ActionRequest> rootActionRequests = document.getRootActionRequests();
        assertEquals("Should have 2 root action requests", 2, rootActionRequests.size());
        ActionRequest user1Request = null;
        ActionRequest user2Request = null;
        for (ActionRequest actionRequest : rootActionRequests) {
            RecipientType recipientType = actionRequest.getRecipientType();
            if (recipientType == RecipientType.PRINCIPAL) {
                if (user1.equals(actionRequest.getPrincipalId())) {
                    user1Request = actionRequest;
                } else if (user2.equals(actionRequest.getPrincipalId())) {
                    user2Request = actionRequest;
                }
            }
        }
        // now let's ensure we got the requests we wanted
        assertNotNull(user1Request);
        assertEquals(ActionRequestStatus.ACTIVATED, user1Request.getStatus());
        assertNotNull(user2Request);
        assertEquals(ActionRequestStatus.INITIALIZED, user2Request.getStatus());

        // now approve as user1
        document.switchPrincipal(user1);
        document.approve("");
        document.switchPrincipal(user2);
        assertTrue(document.isApprovalRequested());
        document.approve("");

        // at this point, it should transition to the next people flow and generate Acknowledge requests to testuser1 and testuser2,
        // and then generate an activated approve request to TestWorkgroup (of which "ewestfal" is a member) and then an
        // initialized approve request to testuser3
        assertTrue(document.isEnroute());
        assertApproveRequested(document, ewestfal);
        assertApproveNotRequested(document, user1, user2, testuser1, testuser2, testuser3);
        assertAcknowledgeRequested(document, testuser1, testuser2);

        // now load as ewestfal (member of TestWorkgroup) and approve
        document.switchPrincipal(ewestfal);
        assertTrue(document.isApprovalRequested());
        document.approve("");
        assertTrue(document.isEnroute());

        // now the only remaining approval request should be to testuser3
        assertApproveRequested(document, testuser3);
        // just for fun, let's take testuser2's acknowledge action
        document.switchPrincipal(testuser2);
        assertTrue(document.isAcknowledgeRequested());
        document.acknowledge("");
        assertTrue(document.isEnroute());

        // testuser3 should still have an approve request, let's take it
        assertApproveRequested(document, testuser3);
        document.switchPrincipal(testuser3);
        document.approve("");

        // document should now be in the processed state
        assertTrue(document.isProcessed());
        // load the last ack to testuser1 and take it
        document.switchPrincipal(testuser1);
        assertTrue(document.isAcknowledgeRequested());
        document.acknowledge("");

        // now the document should be final!
        assertTrue(document.isFinal());
    }

    /**
     * Defines a PeopleFlow as follows:
     *
     * <pre>
     *
     * Priority 1:
     *   -> user1
     *   -> user2
     *   ----> testuser2 - primary delegate
     * Priority 2:
     *   -> testuser1
     *   ----> TestWorkgroup - secondary delegate
     *   ----> testuser3 - primary delegate
     *   -> user3
     *
     * </pre>
     *
     */
    private void createDelegatePeopleFlow() {
        PeopleFlowDefinition.Builder peopleFlow = PeopleFlowDefinition.Builder.create(NAMESPACE_CODE, PEOPLE_FLOW_1);
        peopleFlow.addPrincipal(user1).setPriority(1);

        PeopleFlowMember.Builder user2Builder = peopleFlow.addPrincipal(user2);
        user2Builder.setPriority(1);
        PeopleFlowDelegate.Builder user2PrimaryDelegateBuilder = PeopleFlowDelegate.Builder.create(testuser2,
                MemberType.PRINCIPAL);
        user2PrimaryDelegateBuilder.setDelegationType(DelegationType.PRIMARY);
        user2Builder.getDelegates().add(user2PrimaryDelegateBuilder);

        PeopleFlowMember.Builder testuser1Builder = peopleFlow.addPrincipal(testuser1);
        testuser1Builder.setPriority(2);
        PeopleFlowDelegate.Builder testWorkgroupSecondaryDelegateBuilder = PeopleFlowDelegate.Builder.create(testWorkgroup, MemberType.GROUP);
        testWorkgroupSecondaryDelegateBuilder.setDelegationType(DelegationType.SECONDARY);
        testuser1Builder.getDelegates().add(testWorkgroupSecondaryDelegateBuilder);
        PeopleFlowDelegate.Builder testuser3PrimaryDelegateBuilder = PeopleFlowDelegate.Builder.create(testuser3, MemberType.PRINCIPAL);
        testuser3PrimaryDelegateBuilder.setDelegationType(DelegationType.PRIMARY);
        testuser1Builder.getDelegates().add(testuser3PrimaryDelegateBuilder);

        peopleFlow.addPrincipal(user3).setPriority(2);

        peopleFlowService.createPeopleFlow(peopleFlow.build());
    }

    @Test
    public void test_DelegatePeopleFlow_PriorityParallel_Approve() throws Exception {
        createDelegatePeopleFlow();

        // Priority 1:
        //   -> user1
        //   -> user2
        //   ----> testuser2 - primary delegate
        // Priority 2:
        //   -> testuser1
        //   ----> TestWorkgroup - secondary delegate
        //   ----> testuser3 - primary delegate
        //   -> user3

        WorkflowDocument document = WorkflowDocumentFactory.createDocument(user3, DELEGATE_PEOPLE_FLOW_PRIORITY_PARALLEL_APPROVE);
        document.route("");
        assertTrue("Document should be enroute.", document.isEnroute());

        // user3 should not have an approval request since they initiated the document
        document.switchPrincipal(user3);

        // user1, user2, and testuser2 (as user2's primary delegate) should all have activated requests, make sure the requests look correct
        // user3 and testuser1 should have root requests as well, TestWorkgroup and testuser3 should be delegates of testuser1

        List<ActionRequest> rootActionRequests = document.getRootActionRequests();
        assertEquals("Should have 4 root action requests", 4, rootActionRequests.size());
        ActionRequest user1Request = null;
        ActionRequest user2Request = null;
        ActionRequest user3Request = null;
        ActionRequest testuser1Request = null;
        for (ActionRequest actionRequest : rootActionRequests) {
            RecipientType recipientType = actionRequest.getRecipientType();
            if (recipientType == RecipientType.PRINCIPAL) {
                if (user1.equals(actionRequest.getPrincipalId())) {
                    user1Request = actionRequest;
                } else if (user2.equals(actionRequest.getPrincipalId())) {
                    user2Request = actionRequest;
                } else if (user3.equals(actionRequest.getPrincipalId())) {
                    user3Request = actionRequest;
                } else if (testuser1.equals(actionRequest.getPrincipalId())) {
                    testuser1Request = actionRequest;
                }
            }
        }
        // now let's ensure we got the requests we wanted
        assertNotNull(user1Request);
        assertEquals(ActionRequestStatus.ACTIVATED, user1Request.getStatus());
        assertNotNull(user2Request);
        assertEquals(ActionRequestStatus.ACTIVATED, user2Request.getStatus());
        assertNotNull(user3Request);
        assertEquals(ActionRequestStatus.INITIALIZED, user3Request.getStatus());
        assertNotNull(testuser1Request);
        assertEquals(ActionRequestStatus.INITIALIZED, testuser1Request.getStatus());

        // check delegate requests on user2Request now
        assertEquals(1, user2Request.getChildRequests().size());
        ActionRequest testuser2DelegateRequest = user2Request.getChildRequests().get(0);
        assertEquals(testuser2, testuser2DelegateRequest.getPrincipalId());
        assertEquals(DelegationType.PRIMARY, testuser2DelegateRequest.getDelegationType());
        assertEquals(ActionRequestStatus.ACTIVATED, testuser2DelegateRequest.getStatus());

        // check delegate requests on testuser1Request now
        assertEquals(2, testuser1Request.getChildRequests().size());
        ActionRequest testWorkgroupRequest = null;
        ActionRequest testuser3Request = null;
        for (ActionRequest childActionRequest : testuser1Request.getChildRequests()) {
            RecipientType recipientType = childActionRequest.getRecipientType();
            if (recipientType == RecipientType.PRINCIPAL) {
                if (testuser3.equals(childActionRequest.getPrincipalId())) {
                    testuser3Request = childActionRequest;
                }
            } else if (recipientType == RecipientType.GROUP) {
                if (testWorkgroup.equals(childActionRequest.getGroupId())) {
                    testWorkgroupRequest = childActionRequest;
                }
            }
        }
        assertNotNull(testWorkgroupRequest);
        assertEquals(ActionRequestStatus.INITIALIZED, testWorkgroupRequest.getStatus());
        assertEquals(DelegationType.SECONDARY, testWorkgroupRequest.getDelegationType());
        assertNotNull(testuser3Request);
        assertEquals(ActionRequestStatus.INITIALIZED, testuser3Request.getStatus());
        assertEquals(DelegationType.PRIMARY, testuser3Request.getDelegationType());

        // whew! now that that's done, let's run through the approvals for this peopleflow
        assertApproveRequested(document, user1, user2, testuser2);
        assertApproveNotRequested(document, testuser1, user3, ewestfal, testuser3);
        // approve as testuser2 who is user2's primary delegate
        document.switchPrincipal(testuser2);
        document.approve("");

        // now approve as user1, this should push it to priority 2 in the peopleflow
        assertApproveRequested(document, user1);
        assertApproveNotRequested(document, user2, testuser2, testuser1, user3, ewestfal, testuser3);
        document.switchPrincipal(user1);
        document.approve("");

        // at this point all priorty2 folks should have approvals
        assertApproveRequested(document, testuser1, user3, ewestfal, testuser3);
        assertApproveNotRequested(document, user1, user2, testuser2);
        // approve as ewestfal, a member of TestWorkgroup which is a delegate of testuser1
        document.switchPrincipal(ewestfal);
        document.approve("");

        // the only remaining approval at this point should be to user3, note that user3 initiated the document, but forceAction should = true by default on peopleflows
        assertApproveRequested(document, user3);
        assertApproveNotRequested(document, user1, user2, testuser2, testuser1, ewestfal, testuser3);
        document.switchPrincipal(user3);
        document.approve("");

        // document should now be FINAL!
        assertTrue(document.isFinal());

    }

    private void assertApproveRequested(WorkflowDocument document, String... principalIds) {
        for (String principalId : principalIds) {
            document.switchPrincipal(principalId);
            assertTrue("Approve should have been requested for '" + principalId + "'", document.isApprovalRequested());
        }
    }

    private void assertAcknowledgeRequested(WorkflowDocument document, String... principalIds) {
        for (String principalId : principalIds) {
            document.switchPrincipal(principalId);
            assertTrue("Acknowledge should have been requested for '" + principalId + "'", document.isAcknowledgeRequested());
        }
    }

    private void assertApproveNotRequested(WorkflowDocument document, String... principalIds) {
        for (String principalId : principalIds) {
            document.switchPrincipal(principalId);
            assertFalse("Approve should *NOT* have been requested for '" + principalId + "'", document.isApprovalRequested());
        }
    }

}
