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

    private static final String SINGLE_PEOPLE_FLOW_PARALLEL_APPROVE = "SinglePeopleFlow-Parallel-Approve";
    private static final String SINGLE_PEOPLE_FLOW_SEQUENTIAL_APPROVE = "SinglePeopleFlow-Sequential-Approve";

    private PeopleFlowService peopleFlowService;

    private String user1;
    private String user2;
    private String user3;
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
        // create the required PeopleFlow
        PeopleFlowDefinition.Builder peopleFlow = PeopleFlowDefinition.Builder.create(NAMESPACE_CODE, PEOPLE_FLOW_1);
        peopleFlow.addPrincipal(getPrincipalIdForName("user1")).setPriority(1);
        peopleFlow.addPrincipal(getPrincipalIdForName("user2")).setPriority(2);
        peopleFlow.addGroup(getGroupIdForName("KR-WKFLW", "TestWorkgroup")).setPriority(3);
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
        document.switchPrincipal(user1);
        assertTrue(document.isApprovalRequested());
        document.switchPrincipal(user2);
        assertTrue(document.isApprovalRequested());
        document.switchPrincipal(ewestfal);
        assertTrue(document.isApprovalRequested());

        // now approve as each, the document should be FINAL at the end
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

        document.switchPrincipal(user2);
        assertFalse(document.isApprovalRequested());
        document.switchPrincipal(ewestfal);
        assertFalse(document.isApprovalRequested());

        // user1 should have the request for approval, however
        document.switchPrincipal(user1);
        assertTrue(document.isApprovalRequested());
        document.approve("");

        // should still be enroute
        assertTrue(document.isEnroute());

        // now user1 should no longer have it, it should be activated approve to user2 with TestWorkgroup still initialized but not activated
        assertFalse(document.isApprovalRequested());
        document.switchPrincipal(ewestfal);
        assertFalse(document.isApprovalRequested());
        document.switchPrincipal(user2);
        assertTrue(document.isApprovalRequested());

        // approve as user2
        document.approve("");
        // should still be enroute
        assertTrue(document.isEnroute());

        // now user1 and user2 have approved, should be activated to TestWorkgroup of which ewestfal is a member
        assertFalse(document.isApprovalRequested());
        document.switchPrincipal(user1);
        assertFalse(document.isApprovalRequested());

        // ewestfal should have an approve request because he is a member of TestWorkgroup
        document.switchPrincipal(ewestfal);
        assertTrue(document.isApprovalRequested());
        document.approve("");

        // now document should be final!
        assertTrue(document.isFinal());
    }



}
