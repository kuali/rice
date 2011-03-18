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
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.group.dto.GroupMembershipInfo;
import org.kuali.rice.kim.bo.group.impl.GroupMemberImpl;
import org.kuali.rice.kim.service.impl.GroupServiceImpl;
import org.kuali.rice.kim.service.impl.GroupUpdateServiceImpl;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KimConstants.KimGroupMemberTypes;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.springframework.util.CollectionUtils;

import javax.xml.namespace.QName;
import java.util.*;

import static org.junit.Assert.*;

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

	/*
		The following assertions (borrowed from GroupServiceImplTest) should hold at the start of a test:

 		assertTrue( "g1 must contain group g2", groupIds.contains( "g2" ) );
		assertTrue( "g1 must contain group g3", groupIds.contains( "g3" ) );
		assertFalse( "g1 must not contain group g4 (inactive)", groupIds.contains( "g4" ) );
		assertTrue( "g2 must contain group g3", groupIds.contains( "g3" ) );
		assertFalse( "g2 must not contain group g4 (inactive)", groupIds.contains( "g4" ) );

		assertTrue( "p1 must be in g2", groupService.isMemberOfGroup("p1", "g2") );
		assertTrue( "p1 must be direct member of g2", groupService.isDirectMemberOfGroup("p1", "g2") );
		assertTrue( "p3 must be in g2", groupService.isMemberOfGroup("p3", "g2") );
		assertFalse( "p3 should not be a direct member of g2", groupService.isDirectMemberOfGroup("p3", "g2") );
		assertFalse( "p4 should not be reported as a member of g2 (g4 is inactive)", groupService.isMemberOfGroup("p4", "g2") );
	 */

	public void setUp() throws Exception {
		super.setUp();
		groupService = (GroupServiceImpl)GlobalResourceLoader.getService(new QName("KIM", "kimGroupService"));
		groupUpdateService = (GroupUpdateServiceImpl)GlobalResourceLoader.getService(new QName("KIM", "kimGroupUpdateService"));
		businessObjectService = KNSServiceLocator.getBusinessObjectService();
	}

	@Test
	public void testCreateGroup() {
		// Silly test
		GroupInfo groupInfo = new GroupInfo();
		groupInfo.setActive(true);
		groupInfo.setGroupName("gA");
		groupInfo.setNamespaceCode("KUALI");
		groupInfo.setKimTypeId("1");

		groupUpdateService.createGroup(groupInfo);

		GroupInfo result = groupService.getGroupInfoByName("KUALI", "gA");

		assertEquals(groupInfo.isActive(), result.isActive());
		assertTrue(groupInfo.getNamespaceCode().equals(result.getNamespaceCode()));
		assertTrue(groupInfo.getGroupName().equals(result.getGroupName()));
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
		List<GroupMemberImpl> members = getActiveAndInactiveGroupTypeMembers("g1");
		GroupMemberImpl g2 = null;
		for (GroupMemberImpl member : members) {
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
		List<GroupMemberImpl> members = getActiveAndInactivePrincipalTypeMembers("g2");
		GroupMemberImpl p1 = null;
		for (GroupMemberImpl member : members) {
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

		groupUpdateService.removeAllGroupMembers("g1");

		Collection<GroupMembershipInfo> memberInfos = groupService.getGroupMembersOfGroup("g1");
		assertTrue("should be no active members", CollectionUtils.isEmpty(memberInfos));

		// historical information should be preserved
		List<GroupMemberImpl> members = getActiveAndInactivePrincipalTypeMembers("g1");
		members.addAll(getActiveAndInactiveGroupTypeMembers("g1"));

		GroupMemberImpl p1 = null;
		GroupMemberImpl g2 = null;
		for (GroupMemberImpl member : members) {
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
//	@Test
//	public void testUpdateGroup() {}
//
//	@Test
//	public void testAddGroupToGroup() {}
//
//	@Test
//	public void testAddPrincipalToGroup() {}


	private List<GroupMemberImpl> getActiveAndInactiveGroupTypeMembers(String groupId) {

		Map<String,Object> criteria = new HashMap<String,Object>();
        criteria.put(KIMPropertyConstants.GroupMember.GROUP_ID, groupId);
        criteria.put(KIMPropertyConstants.GroupMember.MEMBER_TYPE_CODE, KimGroupMemberTypes.GROUP_MEMBER_TYPE);

        return new ArrayList<GroupMemberImpl>(businessObjectService.findMatching(GroupMemberImpl.class, criteria));
	}

	private List<GroupMemberImpl> getActiveAndInactivePrincipalTypeMembers(String groupId) {

		Map<String,Object> criteria = new HashMap<String,Object>();
        criteria.put(KIMPropertyConstants.GroupMember.GROUP_ID, groupId);
        criteria.put(KIMPropertyConstants.GroupMember.MEMBER_TYPE_CODE, KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE);

        return new ArrayList<GroupMemberImpl>(businessObjectService.findMatching(GroupMemberImpl.class, criteria));
	}

}
