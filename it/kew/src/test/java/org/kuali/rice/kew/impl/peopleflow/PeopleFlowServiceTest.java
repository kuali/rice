package org.kuali.rice.kew.impl.peopleflow;

import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kew.api.KewApiServiceLocator;
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
 * An integration test which tests the PeopleFlowService reference implementation.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BaselineTestCase.BaselineMode(BaselineTestCase.Mode.CLEAR_DB)
public class PeopleFlowServiceTest extends KEWTestCase {

    private static final String NAMESPACE_CODE = "MyNamespace";
    private static final String NAME = "MyFlow";

    private static final String NAME2 = "MyFlowUpdate";
    private static final String DESCRIPTION = "Description after update";

    private PeopleFlowService peopleFlowService;

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
    public void testCRUD() throws Exception {

        // create a flow with a principal and a group member
        PeopleFlowDefinition.Builder builder = PeopleFlowDefinition.Builder.create(NAMESPACE_CODE, NAME);
        PeopleFlowMember.Builder memberBuilder = PeopleFlowMember.Builder.create("admin", MemberType.PRINCIPAL);
        memberBuilder.setPriority(1);
        builder.getMembers().add(memberBuilder);
        Group group = KimApiServiceLocator.getGroupService().getGroupByNameAndNamespaceCode("KR-WKFLW", "TestWorkgroup");
        memberBuilder = PeopleFlowMember.Builder.create(group.getId(), MemberType.GROUP);
        memberBuilder.setPriority(2);
        builder.getMembers().add(memberBuilder);
        
        // now create it
        PeopleFlowDefinition peopleFlow = getPeopleFlowService().createPeopleFlow(builder.build());
        assertPeopleFlowCreate(peopleFlow, group);

        // load by id and check it's state after a fresh load
        peopleFlow = getPeopleFlowService().getPeopleFlow(peopleFlow.getId());
        assertPeopleFlowCreate(peopleFlow, group);

        // load by name and check it's state after a fresh load
        peopleFlow = getPeopleFlowService().getPeopleFlowByName(NAMESPACE_CODE, NAME);
        assertPeopleFlowCreate(peopleFlow, group);

        // try an update
        builder = PeopleFlowDefinition.Builder.create(peopleFlow);
        builder.setName(NAME2);
        // create a new member and update an existing member, remove the group member first
        for (Iterator<PeopleFlowMember.Builder> iterator = builder.getMembers().iterator(); iterator.hasNext();) {
            PeopleFlowMember.Builder member = iterator.next();
            if (member.getMemberType() == MemberType.GROUP) {
                iterator.remove();
            }
        }
        // save the admin member for some checks later
        PeopleFlowMember.Builder adminMember = builder.getMembers().get(0);
        assertEquals(1, builder.getMembers().size());
        memberBuilder = PeopleFlowMember.Builder.create("ewestfal", MemberType.PRINCIPAL);
        builder.getMembers().add(memberBuilder);
        builder.setDescription(DESCRIPTION);

        // execute the update
        PeopleFlowDefinition updatedPeopleFlow = getPeopleFlowService().updatePeopleFlow(builder.build());
        updatedPeopleFlow = getPeopleFlowService().getPeopleFlow(updatedPeopleFlow.getId());
        assertNotNull(updatedPeopleFlow);
        assertEquals(NAME2, updatedPeopleFlow.getName());
        assertEquals(DESCRIPTION, updatedPeopleFlow.getDescription());
        assertEquals("Ids should be the same", peopleFlow.getId(), updatedPeopleFlow.getId());
        assertEquals("Version number should be one higher", new Long(peopleFlow.getVersionNumber() + 1),
                updatedPeopleFlow.getVersionNumber());
        assertEquals("Should have 2 members", 2, updatedPeopleFlow.getMembers().size());
        
        // now check the members
        for (PeopleFlowMember member : updatedPeopleFlow.getMembers()) {
            assertTrue("should not have any delegates", member.getDelegates().isEmpty());
            assertEquals(MemberType.PRINCIPAL, member.getMemberType());
            assertEquals(1, member.getPriority());
            if (!(member.getMemberId().equals("admin") || member.getMemberId().equals("ewestfal"))) {
                fail("Encountered a member that shouldn't exist! " + member.getMemberId());
            }
        }

    }

    private void assertPeopleFlowCreate(PeopleFlowDefinition peopleFlow, Group groupMember) {
        assertNotNull(peopleFlow);
        assertNotNull(peopleFlow.getId());
        assertEquals(2, peopleFlow.getMembers().size());
        
        for (PeopleFlowMember member : peopleFlow.getMembers()) {
            assertTrue("should not have any delegates", member.getDelegates().isEmpty());
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
