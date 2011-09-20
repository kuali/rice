package org.kuali.rice.kew.impl.peopleflow;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.peopleflow.MemberType;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowMember;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowService;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.test.BaselineTestCase;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * An integration test which tests a document type with a node on it which routes to a list of PeopleFlows.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class PeopleFlowRoutingTest extends KEWTestCase {

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
        
    }


}
