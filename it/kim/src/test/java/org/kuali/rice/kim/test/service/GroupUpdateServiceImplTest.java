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
package org.kuali.rice.kim.test.service;

import org.junit.Test;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupMember;
import org.kuali.rice.kim.impl.group.GroupMemberBo;
import org.kuali.rice.kim.impl.group.GroupServiceImpl;
import org.kuali.rice.kim.service.impl.GroupUpdateServiceImpl;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimConstants.KimGroupMemberTypes;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.springframework.util.CollectionUtils;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Unit test for {@link GroupUpdateServiceImpl}
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class GroupUpdateServiceImplTest extends KIMTestCase {

	private GroupServiceImpl groupService;
	private GroupUpdateServiceImpl groupUpdateService;
	private BusinessObjectService businessObjectService;

	public void setUp() throws Exception {
		super.setUp();
		groupService = (GroupServiceImpl)GlobalResourceLoader.getService(new QName("KIM", "kimGroupService"));
		groupUpdateService = (GroupUpdateServiceImpl) GlobalResourceLoader.getService(new QName("KIM", "kimGroupUpdateService"));
		businessObjectService = KRADServiceLocator.getBusinessObjectService();
	}

	@Test
	public void testCreateGroup() {
		Group groupInfo = createGroup();

		Group result = groupService.getGroupByName("KUALI", "gA");

		assertEquals(groupInfo.isActive(), result.isActive());
		assertTrue(groupInfo.getNamespaceCode().equals(result.getNamespaceCode()));
		assertTrue(groupInfo.getName().equals(result.getName()));
		assertTrue(groupInfo.getKimTypeId().equals(result.getKimTypeId()));
	}

	@Test
	public void testRemoveGroupFromGroup() {
		List<String> preGroupIds = groupService.getDirectMemberGroupIds("g1");

		assertTrue( "g1 must contain group g2", preGroupIds.contains( "g2" ) );

		groupUpdateService.removeGroupFromGroup("g2", "g1");

		List<String> postGroupIds = groupService.getDirectMemberGroupIds("g1");

		assertFalse( "g1 must not contain group g2", postGroupIds.contains( "g2" ) );

		// add it back in, and the two lists should contain the same elements
		postGroupIds.add("g2");
		assertTrue(postGroupIds.containsAll(preGroupIds) && preGroupIds.containsAll(postGroupIds));

		// historical information should be preserved
		List<GroupMemberBo> members = getActiveAndInactiveGroupTypeMembers("g1");
		GroupMemberBo g2 = null;
		for (GroupMemberBo member : members) {
			if (member.getMemberId().equals("g2")) {
				g2 = member;
			}
		}

		// it exists
		assertNotNull("should have found g2", g2);
		// it is inactive
		assertFalse("g2 should be inactive", g2.isActive());
	}

	@Test
	public void testRemovePrincipalFromGroup() {
		List<String> preDirectPrincipalMemberIds = groupService.getDirectMemberPrincipalIds("g2");
		assertTrue( "p1 must be direct member of g2", preDirectPrincipalMemberIds.contains("p1") );

		groupUpdateService.removePrincipalFromGroup("p1", "g2");

		List<String> postDirectPrincipalMemberIds = groupService.getDirectMemberPrincipalIds("g2");
		assertFalse( "p1 must no longer be a direct member of g2", postDirectPrincipalMemberIds.contains("p1") );

		// add p1 back to the list, and pre & post should contain the same elements
		postDirectPrincipalMemberIds.add("p1");
		assertTrue(preDirectPrincipalMemberIds.containsAll(postDirectPrincipalMemberIds) &&
				postDirectPrincipalMemberIds.containsAll(preDirectPrincipalMemberIds));

		// historical information should be preserved
		List<GroupMemberBo> members = getActiveAndInactivePrincipalTypeMembers("g2");
		GroupMemberBo p1 = null;
		for (GroupMemberBo member : members) {
			if (member.getMemberId().equals("p1")) {
				p1 = member;
			}
		}

		// it exists
		assertNotNull("should have found p1", p1);
		// it is inactive
		assertFalse("p1 should be inactive", p1.isActive());
	}

	@Test
	public void testRemoveGroupMembers() {
		List<String> before = groupService.getMemberPrincipalIds("g1");

		groupUpdateService.addPrincipalToGroup("p1", "g1");

		assertTrue( "p1 must be direct member of g1", groupService.isDirectMemberOfGroup("p1", "g1") );
		assertTrue( "g2 must be direct member of g1", groupService.isGroupMemberOfGroup("g2", "g1") );

		groupUpdateService.removeAllMembers("g1");

		List<GroupMember> memberInfos = groupService.getMembersOfGroup("g1");
		assertTrue("should be no active members", CollectionUtils.isEmpty(memberInfos));

		// historical information should be preserved
		List<GroupMemberBo> members = getActiveAndInactivePrincipalTypeMembers("g1");
		members.addAll(getActiveAndInactiveGroupTypeMembers("g1"));

		GroupMemberBo p1 = null;
		GroupMemberBo g2 = null;
		for (GroupMemberBo member : members) {
			if (member.getMemberId().equals("p1")) {
				p1 = member;
			}
			if (member.getMemberId().equals("g2")) {
				g2 = member;
			}
		}

		// it exists
		assertNotNull("should have found p1", p1);
		assertNotNull("should have found g2", g2);
		// it is inactive
		assertFalse("p1 should be inactive", p1.isActive());
		assertFalse("g2 should be inactive", g2.isActive());
	}

	/* Stubs to test other GroupUpdateService methods: */
	@Test
	public void testUpdateGroup() {

        Group group = createGroup();
        Group.Builder builder = Group.Builder.create(group);
		builder.setDescription("This is a new description.  It is useful.");


		groupUpdateService.updateGroup(group.getId(), builder.build());

		Group result = groupService.getGroupByName("KUALI", "gA");

		assertEquals(group.isActive(), result.isActive());
		assertEquals(group.getNamespaceCode(), result.getNamespaceCode());
		assertEquals(group.getName(), result.getName());
		assertEquals(group.getKimTypeId(), result.getKimTypeId());
        assertEquals(builder.getDescription(), result.getDescription());
    }

	@Test
	public void testAddGroupToGroup() {
        Group group = createGroup();

        //make sure g1 is not a member of gA
        assertFalse(groupService.isGroupMemberOfGroup("g1", group.getId()));

        //add g1 to gA
        groupUpdateService.addGroupToGroup("g1", group.getId());

        //make sure g1 is now a member of gA
        groupUpdateService.isGroupMemberOfGroup("g1", group.getId());

    }

	@Test
	public void testAddPrincipalToGroup() {
        Group group = createGroup();

        //make sure g1 is not a member of gA
        assertFalse(groupService.isMemberOfGroup("p1", group.getId()));

        //add g1 to gA
        groupUpdateService.addPrincipalToGroup("p1", group.getId());

        //make sure g1 is now a member of gA
        groupUpdateService.isMemberOfGroup("p1", group.getId());
    }


	private List<GroupMemberBo> getActiveAndInactiveGroupTypeMembers(String groupId) {

		Map<String,Object> criteria = new HashMap<String,Object>();
        criteria.put(KIMPropertyConstants.GroupMember.GROUP_ID, groupId);
        criteria.put(KIMPropertyConstants.GroupMember.MEMBER_TYPE_CODE, KimGroupMemberTypes.GROUP_MEMBER_TYPE);

        return new ArrayList<GroupMemberBo>(businessObjectService.findMatching(GroupMemberBo.class, criteria));
	}

	private List<GroupMemberBo> getActiveAndInactivePrincipalTypeMembers(String groupId) {

		Map<String,Object> criteria = new HashMap<String,Object>();
        criteria.put(KIMPropertyConstants.GroupMember.GROUP_ID, groupId);
        criteria.put(KIMPropertyConstants.GroupMember.MEMBER_TYPE_CODE, KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);

        return new ArrayList<GroupMemberBo>(businessObjectService.findMatching(GroupMemberBo.class, criteria));
	}

    private Group createGroup() {
        Group.Builder groupInfo = Group.Builder.create("KUALI", "gA", "1");
		groupInfo.setActive(true);

		return groupUpdateService.createGroup(groupInfo.build());
    }
}
