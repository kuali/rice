package org.kuali.rice.kew.impl.peopleflow;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.WorkflowDocumentFactory;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowService;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.test.BaselineTestCase;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * An integration test which tests a document type with a node on it which routes to a list of PeopleFlows.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class PeopleFlowRoutingTest extends KEWTestCase {

    private static final String NAMESPACE_CODE = "TEST";
    private static final String PEOPLE_FLOW_1 = "PeopleFlow1";

    private static final String SINGLE_PEOPLE_FLOW_PARALLEL_APPROVE = "SinglePeopleFlow-Parallel-Approve";

    private PeopleFlowService peopleFlowService;

    protected void loadTestData() throws Exception {
        loadXmlFile("PeopleFlowRoutingTest.xml");
    }

    @Before
    public void setupServiceUnderTest() {
        setPeopleFlowService(KewApiServiceLocator.getPeopleFlowService());
    }

    protected void setPeopleFlowService(PeopleFlowService peopleFlowService) {
        this.peopleFlowService = peopleFlowService;
    }

    protected PeopleFlowService getPeopleFlowService() {
        return peopleFlowService;
    }

    @Test
    public void test_SinglePeopleFlow_Parallel_Approve() throws Exception {
        // create the required PeopleFlow
        PeopleFlowDefinition.Builder peopleFlow = PeopleFlowDefinition.Builder.create(NAMESPACE_CODE, PEOPLE_FLOW_1);
        peopleFlow.addPrincipal(getPrincipalIdForName("user1")).setPriority(1);
        peopleFlow.addPrincipal(getPrincipalIdForName("user2")).setPriority(2);
        peopleFlow.addGroup(getGroupIdForName("KR-WKFLW", "TestWorkgroup")).setPriority(3);
        peopleFlowService.createPeopleFlow(peopleFlow.build());

        // now route a document to it
        WorkflowDocument document = WorkflowDocumentFactory.createDocument("user3", SINGLE_PEOPLE_FLOW_PARALLEL_APPROVE);
        document.route("");
        assertTrue("Document should be enroute.", document.isEnroute());

        // user3 should not have an approval request since they initiated the document
        document.switchPrincipal(getPrincipalIdForName("user3"));

        // user1, user2, and TestWorkgroup (of which ewestfal is a member) should have the request
        document.switchPrincipal(getPrincipalIdForName("user1"));
        assertTrue(document.isApprovalRequested());
        document.switchPrincipal(getPrincipalIdForName("user2"));
        assertTrue(document.isApprovalRequested());
        document.switchPrincipal(getPrincipalIdForName("ewestfal"));
        assertTrue(document.isApprovalRequested());

        // now approve as each, the document should be FINAL at the end
        document.approve("approving as ewestfal");
        assertTrue("Document should still be enroute.", document.isEnroute());
        document.switchPrincipal(getPrincipalIdForName("user1"));
        document.approve("approving as user1");
        assertTrue("Document should still be enroute.", document.isEnroute());
        document.switchPrincipal(getPrincipalIdForName("user2"));
        document.approve("approving as user2");
        assertTrue("Document should now be FINAL.", document.isFinal());
    }


}
