/*
 * Copyright 2005-2007 The Kuali Foundation.
 *
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
package edu.iu.uis.eden.workgroup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.WorkflowUser;

/**
 * Tests the BaseWorkgroupService.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BaseWorkgroupServiceTest extends KEWTestCase {

	/**
	 * Tests that Workgroup caching works as expected.
	 *
	 * @throws Exception
	 */
	@Test public void testCaching() throws Exception {
		// Let's start by flushing out the workgroup caches.
		// Test harness startup will invariably result in some workgroups getting cached.
		KEWServiceLocator.getCacheAdministrator().flushGroup(BaseWorkgroupService.WORKGROUP_ID_CACHE_GROUP);
		KEWServiceLocator.getCacheAdministrator().flushGroup(BaseWorkgroupService.WORKGROUP_NAME_CACHE_GROUP);

		// verify that the TestWorkgroup is not in the cache
		Workgroup cachedTestWorkgroup = (Workgroup)KEWServiceLocator.getCacheAdministrator().getFromCache(getCacheNameKey("TestWorkgroup"));
		assertNull("The test workgroup should not be cached yet.", cachedTestWorkgroup);

		// fetch the TestWorkgroup from the service, it should then be cached
		BaseWorkgroup testWorkgroup = (BaseWorkgroup)KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("TestWorkgroup"));
		assertNotNull("TestWorkgroup should exist.", testWorkgroup);
		cachedTestWorkgroup = (Workgroup)KEWServiceLocator.getCacheAdministrator().getFromCache(getCacheNameKey("TestWorkgroup"));
		assertNotNull("TestWorkgroup should now be cached.", cachedTestWorkgroup);

		// the cached version and the version returned from the service should be the same instance
		assertEquals("Workgroups should be the same.", testWorkgroup, cachedTestWorkgroup);

		// now, if we fetch the TestWorkgroup from the ID cache, we should also recieve the same workgroup
		Workgroup testWorkgroupById = (Workgroup)KEWServiceLocator.getCacheAdministrator().getFromCache(getCacheIdKey(testWorkgroup.getWorkflowGroupId().getGroupId()));
		assertNotNull("TestWorkgroup should be in ID cache.", testWorkgroupById);
		assertEquals("Workgroups should be the same.", testWorkgroup, testWorkgroupById);

		// Modify the workgroup and save it, it should get flushed from the cache

		// Let's verify that user1 is not currently a member of this workgroup for good measure
		WorkflowUser user1 = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("user1"));
		assertFalse("user1 should not be a member of TestWorkgroup by default as configured in DefaultTestData.xml", testWorkgroup.hasMember(user1));
		// build the BaseWorkgroupMember
		BaseWorkgroupMember member = new BaseWorkgroupMember();
		member.setWorkflowId(user1.getWorkflowId());
		member.setMemberType(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
		member.setWorkgroup(testWorkgroup);
		member.setWorkgroupId(testWorkgroup.getWorkflowGroupId().getGroupId());
		member.setWorkgroupVersionNumber(testWorkgroup.getVersionNumber());
		testWorkgroup.getWorkgroupMembers().add(member);
		KEWServiceLocator.getWorkgroupService().save(testWorkgroup);

		// the workgroup should no longer be cached
		Workgroup cachedTestWorkgroup2 = (Workgroup)KEWServiceLocator.getCacheAdministrator().getFromCache(getCacheNameKey("TestWorkgroup"));
		assertNull("TestWorkgroup should no longer be in the name cache.", cachedTestWorkgroup2);
		Workgroup testWorkgroupById2 = (Workgroup)KEWServiceLocator.getCacheAdministrator().getFromCache(getCacheIdKey(testWorkgroup.getWorkgroupId()));
		assertNull("TestWorkgroup should no longer be in the ID cache.", testWorkgroupById2);

		// fetch the workgroup from the service and verify that we have the updated version with the new member
		Workgroup testWorkgroup2 = KEWServiceLocator.getWorkgroupService().getWorkgroup(testWorkgroup.getWorkflowGroupId());
		assertNotNull("TestWorkgroup should exist.", testWorkgroup2);
		assertTrue("Workgroup should have user1 as a member.", testWorkgroup2.hasMember(user1));
		// since the Workgroup should be newly fetched from the database, the instances should not be the same
		assertFalse("The old and new TestWorkgroups should not be the same", testWorkgroup.equals(testWorkgroup2));

		// the TestWorkgroup should now be in both caches
		cachedTestWorkgroup2 = (Workgroup)KEWServiceLocator.getCacheAdministrator().getFromCache(getCacheNameKey("TestWorkgroup"));
		assertNotNull("New workgroup should be available in name cache.", cachedTestWorkgroup2);
		testWorkgroupById2 = (Workgroup)KEWServiceLocator.getCacheAdministrator().getFromCache(getCacheIdKey(testWorkgroup.getWorkflowGroupId().getGroupId()));
		assertNotNull("New workgroup should be available in id cache.", testWorkgroupById2);
	}

	@Test
	public void testNestedWorkgroups() throws Exception {
		BaseWorkgroup workgroup1 = new BaseWorkgroup();
		workgroup1.setActiveInd(Boolean.TRUE);
		workgroup1.setCurrentInd(Boolean.TRUE);
		workgroup1.setDescription("description");
		workgroup1.setGroupNameId(new GroupNameId("TestNestedWorkgroups1"));
		workgroup1.setWorkgroupType(null);

		// user 1 is not a member of TestWorkgroup, add as a member
		BaseWorkgroupMember member1 = new BaseWorkgroupMember();
		WorkflowUser user1 = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("user1"));
		member1.setWorkflowId(user1.getWorkflowId());
		member1.setMemberType(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
		member1.setWorkgroup(workgroup1);
		workgroup1.getWorkgroupMembers().add(member1);

		// add TestWorkgroup as a member
		BaseWorkgroupMember member2 = new BaseWorkgroupMember();
		Workgroup testWorkgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("TestWorkgroup"));
		member2.setWorkflowId(testWorkgroup.getWorkflowGroupId().getGroupId().toString());
		member2.setMemberType(EdenConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD);
		member2.setWorkgroup(workgroup1);
		workgroup1.getWorkgroupMembers().add(member2);

		// add ewestfal as a member (already in TestWorkgroup)
		BaseWorkgroupMember member3 = new BaseWorkgroupMember();
		WorkflowUser user2 = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("ewestfal"));
		member3.setWorkflowId(user2.getWorkflowId());
		member3.setMemberType(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
		member3.setWorkgroup(workgroup1);
		workgroup1.getWorkgroupMembers().add(member3);

		KEWServiceLocator.getWorkgroupService().save(workgroup1);

		Workgroup testNestedWorkgroups1 = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("TestNestedWorkgroups1"));
		assertNotNull(testNestedWorkgroups1);
		assertEquals("Workgroup should have 3 members.", 3, testNestedWorkgroups1.getMembers().size());

	}

	@Test public void testGetWorkgroupsGroups() throws Exception {
		loadXmlFile("NestedWorkgroups.xml");
		Workgroup NWG1 = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("NWG1"));
		Workgroup NWG2 = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("NWG2"));
		Workgroup NWGNested1 = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("NWGNested1"));
		Workgroup NWGNested2 = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("NWGNested2"));

		List<Workgroup> workgroupsGroups = KEWServiceLocator.getWorkgroupService().getWorkgroupsGroups(NWG1);
		assertEquals(2, workgroupsGroups.size());
		boolean foundNWGNested1 = false;
		boolean foundNWGNested2 = false;
		for (Workgroup workgroup : workgroupsGroups) {
			if (workgroup.getGroupNameId().getNameId().equals("NWGNested1")) {
				foundNWGNested1 = true;
			} else if (workgroup.getGroupNameId().getNameId().equals("NWGNested2")) {
				foundNWGNested2 = true;
			}
		}
		assertTrue("Should have found NWGNested1", foundNWGNested1);
		assertTrue("Should have found NWGNested2", foundNWGNested2);
		workgroupsGroups = KEWServiceLocator.getWorkgroupService().getWorkgroupsGroups(NWG2);
		assertEquals(1, workgroupsGroups.size());
		assertEquals("NWGNested2", workgroupsGroups.get(0).getGroupNameId().getNameId());
		workgroupsGroups = KEWServiceLocator.getWorkgroupService().getWorkgroupsGroups(NWGNested1);
		assertEquals(1, workgroupsGroups.size());
		assertEquals("NWGNested2", workgroupsGroups.get(0).getGroupNameId().getNameId());
		workgroupsGroups = KEWServiceLocator.getWorkgroupService().getWorkgroupsGroups(NWGNested2);
		assertEquals(0, workgroupsGroups.size());
	}

	@Test public void testNestedWorkgroupsDuplicateUsers() throws Exception {
		loadXmlFile("NestedWorkgroups.xml");
		Workgroup dupe1 = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("NestedWGDupe1"));
		Workgroup dupe2 = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("NestedWGDupe2"));
		Workgroup dupe3 = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("NestedWGDupe3"));
		Workgroup dupe4 = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("NestedWGDupe4"));

		assertEquals("dupe1 should have 6 users.", 6, dupe1.getUsers().size());
		assertEquals("dupe2 should have 5 users.", 5, dupe2.getUsers().size());
		assertEquals("dupe3 should have 5 users.", 5, dupe3.getUsers().size());
		assertEquals("dupe4 should have 3 users.", 3, dupe4.getUsers().size());

		Set<String> users = new HashSet<String>();
		for (WorkflowUser user : dupe1.getUsers()) {
			users.add(user.getAuthenticationUserId().getAuthenticationId());
		}
		assertEquals("dupe1 should have 6 unique users.", 6, users.size());
		assertTrue(users.contains("ewestfal"));
		assertTrue(users.contains("rkirkend"));
		assertTrue(users.contains("bmcgough"));
		assertTrue(users.contains("xqi"));
		assertTrue(users.contains("jhopf"));
		assertTrue(users.contains("jitrue"));
	}

	@Test public void testGroupMembership() throws Exception {
		loadXmlFile("NestedWorkgroups.xml");

		WorkgroupService wgService = KEWServiceLocator.getWorkgroupService();

		// ewestfal should be a member of NWG1, NWGNested1, NWGNested2, NestedWGDupe1, NestedWGDupe2, NestedWGDupe3, NestedWGDupe4
		WorkflowUser ewestfal = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("ewestfal"));
		List<Workgroup> groups = wgService.getUsersGroups(ewestfal);
		assertTrue("Should be member of NWG1", wgService.isUserMemberOfGroup(new GroupNameId("NWG1"), ewestfal));
		assertTrue("Should be member of NWG1", isMember("NWG1", groups));
		assertTrue("Should be member of NWGNested1", wgService.isUserMemberOfGroup(new GroupNameId("NWGNested1"), ewestfal));
		assertTrue("Should be member of NWGNested1", isMember("NWGNested1", groups));
		assertTrue("Should be member of NWGNested2", wgService.isUserMemberOfGroup(new GroupNameId("NWGNested2"), ewestfal));
		assertTrue("Should be member of NWGNested2", isMember("NWGNested2", groups));
		assertTrue("Should be member of NestedWGDupe1", wgService.isUserMemberOfGroup(new GroupNameId("NestedWGDupe1"), ewestfal));
		assertTrue("Should be member of NestedWGDupe1", isMember("NestedWGDupe1", groups));
		assertTrue("Should be member of NestedWGDupe2", wgService.isUserMemberOfGroup(new GroupNameId("NestedWGDupe2"), ewestfal));
		assertTrue("Should be member of NestedWGDupe2", isMember("NestedWGDupe2", groups));
		assertTrue("Should be member of NestedWGDupe3", wgService.isUserMemberOfGroup(new GroupNameId("NestedWGDupe3"), ewestfal));
		assertTrue("Should be member of NestedWGDupe3", isMember("NestedWGDupe3", groups));
		assertTrue("Should be member of NestedWGDupe4", wgService.isUserMemberOfGroup(new GroupNameId("NestedWGDupe4"), ewestfal));
		assertTrue("Should be member of NestedWGDupe4", isMember("NestedWGDupe4", groups));
		// ewestfal should not be a member of NWG2
		assertFalse("Should NOT be member of NWG2", wgService.isUserMemberOfGroup(new GroupNameId("NWG2"), ewestfal));
		assertFalse("Should NOT be member of NWG2", isMember("NWG2", groups));
	}

	private boolean isMember(String groupName, List<Workgroup> groups) {
		for (Workgroup group : groups) {
			if (group.getGroupNameId().getNameId().equals(groupName)) {
				return true;
			}
		}
		return false;
	}

//	@Test public void testNestedWorkgroupsCycles() throws Exception {
//		BaseWorkgroup workgroup1 = new BaseWorkgroup();
//		workgroup1.setActiveInd(Boolean.TRUE);
//		workgroup1.setCurrentInd(Boolean.TRUE);
//		workgroup1.setDescription("description");
//		workgroup1.setGroupNameId(new GroupNameId("TestNestedWorkgroups1"));
//		workgroup1.setWorkgroupType(null);
//
//		// user 1 is not a member of TestWorkgroup, add as a member
//		BaseWorkgroupMember member1 = new BaseWorkgroupMember();
//		WorkflowUser user1 = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("user1"));
//		member1.setWorkflowId(user1.getWorkflowId());
//		member1.setMemberType(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
//		member1.setWorkgroup(workgroup1);
//		workgroup1.getWorkgroupMembers().add(member1);
//
//		// add ewestfal as a member (already in TestWorkgroup)
//		BaseWorkgroupMember member3 = new BaseWorkgroupMember();
//		WorkflowUser user2 = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("ewestfal"));
//		member3.setWorkflowId(user2.getWorkflowId());
//		member3.setMemberType(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
//		member3.setWorkgroup(workgroup1);
//		workgroup1.getWorkgroupMembers().add(member3);
//
//		KEWServiceLocator.getWorkgroupService().save(workgroup1);
//
//		BaseWorkgroup workgroup2 = new BaseWorkgroup();
//		workgroup2.setActiveInd(Boolean.TRUE);
//		workgroup2.setCurrentInd(Boolean.TRUE);
//		workgroup2.setDescription("description");
//		workgroup2.setGroupNameId(new GroupNameId("TestNestedWorkgroups2"));
//		workgroup2.setWorkgroupType(null);
//
//		// user 1 is not a member of TestWorkgroup, add as a member
//		member1 = new BaseWorkgroupMember();
//		WorkflowUser rkirkend = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId("rkirkend"));
//		member1.setWorkflowId(rkirkend.getWorkflowId());
//		member1.setMemberType(EdenConstants.ACTION_REQUEST_USER_RECIPIENT_CD);
//		member1.setWorkgroup(workgroup2);
//		workgroup2.getWorkgroupMembers().add(member1);
//
//		// add TestWorkgroup as a member
//		BaseWorkgroupMember member2 = new BaseWorkgroupMember();
//		member2.setWorkflowId(workgroup1.getWorkflowGroupId().getGroupId().toString());
//		member2.setMemberType(EdenConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD);
//		member2.setWorkgroup(workgroup2);
//		workgroup2.getWorkgroupMembers().add(member2);
//
//		KEWServiceLocator.getWorkgroupService().save(workgroup2);
//
//		// now add TestedNestedWorkgroups2 as a member of TestNestedWorkgroups1
//		member1 = new BaseWorkgroupMember();
//		member1.setWorkflowId(workgroup2.getWorkflowGroupId().getGroupId().toString());
//		member1.setMemberType(EdenConstants.ACTION_REQUEST_WORKGROUP_RECIPIENT_CD);
//		member1.setWorkgroup(workgroup1);
//		workgroup1.getWorkgroupMembers().add(member1);
//
//		KEWServiceLocator.getWorkgroupService().save(workgroup1);
//
//		Workgroup testNestedWorkgroups1 = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("TestNestedWorkgroups1"));
//		assertNotNull(testNestedWorkgroups1);
//		assertEquals("Workgroup should have 3 members.", 3, testNestedWorkgroups1.getMembers().size());
//		assertEquals("Workgroup should have 3 users.", 3, testNestedWorkgroups1.getUsers().size());
//
//		Workgroup testNestedWorkgroups2 = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId("TestNestedWorkgroups2"));
//		assertNotNull(testNestedWorkgroups2);
//		assertEquals("Workgroup should have 3 members.", 3, testNestedWorkgroups2.getMembers().size());
//		assertEquals("Workgroup should have 3 users.", 3, testNestedWorkgroups2.getUsers().size());
//
//	}

	private String getCacheNameKey(String workgroupName) {
		return new BaseWorkgroupService().generateCacheKey(new GroupNameId(workgroupName));
	}

	private String getCacheIdKey(Long workgroupId) {
		return new BaseWorkgroupService().generateCacheKey(new WorkflowGroupId(workgroupId));
	}

}
