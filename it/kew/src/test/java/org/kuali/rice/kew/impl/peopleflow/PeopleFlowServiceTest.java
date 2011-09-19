package org.kuali.rice.kew.impl.peopleflow;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.peopleflow.MemberType;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowDefinition;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowMemberDefinition;
import org.kuali.rice.kew.api.peopleflow.PeopleFlowService;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.test.BaselineTestCase;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * An integration test which tests the PeopleFlowService reference implementation.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class PeopleFlowServiceTest extends KEWTestCase {

    private static final String NAMESPACE_CODE = "MyNamespace";
    private static final String NAME = "MyFlow";

    private PeopleFlowService peopleFlowService;

    @Before
    public void setupServiceUnderTest() {
        setPeopleFlowService(KewApiServiceLocator.getPeopleFlowService());
    }

    protected void setPeopleFlowService(PeopleFlowService peopleFlowService) {
        this.peopleFlowService = peopleFlowService;
    }

    @Test
    public void testCreate() throws Exception {

        // create a flow with a principal and a group member
        PeopleFlowDefinition.Builder builder = PeopleFlowDefinition.Builder.create(NAMESPACE_CODE, NAME);
        PeopleFlowMemberDefinition.Builder memberBuilder = PeopleFlowMemberDefinition.Builder.create("admin", MemberType.PRINCIPAL);
        memberBuilder.setPriority(1);
        builder.getMembers().add(memberBuilder);
        Group group = KimApiServiceLocator.getGroupService().getGroupByNameAndNamespaceCode("KR-WKFLW", "TestWorkgroup");
        memberBuilder = PeopleFlowMemberDefinition.Builder.create(group.getId(), MemberType.GROUP);
        memberBuilder.setPriority(2);
        builder.getMembers().add(memberBuilder);
        
        // now create it
        PeopleFlowDefinition peopleFlow = peopleFlowService.createPeopleFlow(builder.build());
        assertPeopleFlowCreate(peopleFlow, group);

        // load by id and check it's state after a fresh load
        peopleFlow = peopleFlowService.getPeopleFlow(peopleFlow.getId());
        assertPeopleFlowCreate(peopleFlow, group);

        // load by name and check it's state after a fresh load
        peopleFlow = peopleFlowService.getPeopleFlowByName(NAMESPACE_CODE, NAME);
        assertPeopleFlowCreate(peopleFlow, group);

    }

    private void assertPeopleFlowCreate(PeopleFlowDefinition peopleFlow, Group groupMember) {
        assertNotNull(peopleFlow);
        assertNotNull(peopleFlow.getId());
        assertEquals(2, peopleFlow.getMembers().size());
        
        for (PeopleFlowMemberDefinition member : peopleFlow.getMembers()) {
            assertNotNull("member should have an id", member.getId());
            assertEquals("member peopleflow id should be same", peopleFlow.getId(), member.getPeopleFlowId());
            assertNotNull("should have a non-null version number", member.getVersionNumber());
            assertNull("should not a delegated from id", member.getDelegatedFromId());
            if (MemberType.PRINCIPAL == member.getMemberType()) {
                assertEquals(1, member.getPriority());
                assertEquals("admin", member.getMemberId());
            } else if (MemberType.GROUP == member.getMemberType()) {
                assertEquals(2, member.getPriority());
                assertEquals(groupMember.getId(), member.getMemberId());
            } else {
                fail("Invalid member found!");
            }
        }

        assertTrue("should have no attributes", peopleFlow.getAttributes().isEmpty());
        assertNull("description should be null", peopleFlow.getDescription());
        assertEquals(NAMESPACE_CODE, peopleFlow.getNamespaceCode());
        assertEquals(NAME, peopleFlow.getName());
        assertNull(peopleFlow.getTypeId());
        assertTrue(peopleFlow.isActive());
        assertNotNull("should have a non-null version number", peopleFlow.getVersionNumber());
    }


}
