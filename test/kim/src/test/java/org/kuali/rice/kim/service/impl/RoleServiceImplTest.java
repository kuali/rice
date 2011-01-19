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
package org.kuali.rice.kim.service.impl;

import org.junit.Test;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.xml.dto.AttributeSet;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.test.KIMTestCase;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

import javax.xml.namespace.QName;
import java.sql.Timestamp;
import java.util.*;

import static org.junit.Assert.*;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RoleServiceImplTest extends KIMTestCase {

	private RoleServiceImpl roleService;
	private RoleUpdateServiceImpl roleUpdateService;

	public void setUp() throws Exception {
		super.setUp();
		roleService = (RoleServiceImpl)GlobalResourceLoader.getService(new QName("KIM", "kimRoleService"));
		roleUpdateService = (RoleUpdateServiceImpl)GlobalResourceLoader.getService(new QName("KIM", "kimRoleUpdateService"));
	}

	@Test
	public void testPrincipaHasRoleOfDirectAssignment() {
		List <String>roleIds = new ArrayList<String>();
		roleIds.add("r1");
		assertTrue( "p1 has direct role r1", roleService.principalHasRole("p1", roleIds, null ));	
		//assertFalse( "p4 has no direct/higher level role r1", roleService.principalHasRole("p4", roleIds, null ));	
		AttributeSet qualification = new AttributeSet();
		qualification.put("Attribute 2", "CHEM");
		assertTrue( "p1 has direct role r1 with rp2 attr data", roleService.principalHasRole("p1", roleIds, qualification ));	
		qualification.clear();
		//requested qualification rolls up to a higher element in some hierarchy 
		// method not implemented yet, not quite clear how this works
		qualification.put("Attribute 3", "PHYS");
		assertTrue( "p1 has direct role r1 with rp2 attr data", roleService.principalHasRole("p1", roleIds, qualification ));	
	}

	@Test
	public void testPrincipalHasRoleOfHigherLevel() {
		// "p3" is in "r2" and "r2 contains "r1"
		List <String>roleIds = new ArrayList<String>();
		roleIds.add("r2");
		assertTrue( "p1 has assigned in higher level role r1", roleService.principalHasRole("p1", roleIds, null ));		
	}
	
	@Test
	public void testPrincipalHasRoleContainsGroupAssigned() {
		// "p2" is in "g1" and "g1" assigned to "r2"
		List <String>roleIds = new ArrayList<String>();
		roleIds.add("r2");
		assertTrue( "p2 is assigned to g1 and g1 assigned to r2", roleService.principalHasRole("p2", roleIds, null ));		
	}

	/**
	 * Tests to ensure that the RoleServiceImpl's caching mechanisms are behaving as expected.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCachingBehavesCorrectly() throws Exception {
		RoleServiceTestImpl roleServiceTestImpl = new RoleServiceTestImpl();
		roleServiceTestImpl.setRoleDao(roleService.getRoleDao());
		
		// Ensure that read-only searching operations function properly for the role-related and delegation-related caches.
		roleServiceTestImpl.assertRoleCachingWorksAsExpected();
		roleServiceTestImpl.assertDelegationCachingWorksAsExpected();
		
		// Ensure that modifications to role-related and delegation-related objects will clear out the appropriate caches.
		roleServiceTestImpl.assertCachesAreClearedOnUpdatesAsExpected();
	}
	
	/**
	 * Tests to ensure that a circular role membership cannot be created via the RoleUpdateService.
	 * 
	 * @throws Exception
	 */
	@Test (expected=IllegalArgumentException.class)
	public void testCircularRoleAssignment() {
		AttributeSet attributeSet = new AttributeSet();
		List <String>roleIds = new ArrayList<String>();
		roleIds.add("r1");
		roleUpdateService.assignRoleToRole("r5", "AUTH_SVC_TEST2", "RoleThree", attributeSet);
	}
	
	/**
	 * A convenience interface for reducing duplicated code when comparing KIM objects and testing certain KIM-object-caching capabilities.
	 * 
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 */
	private interface KimObjectTestChecker<T extends PersistableBusinessObjectBase> {
		/* Gets the simple class name of the KIM object, mostly for usage in error messages. */
		public String getKimObjectName();
		/* Gets the ID of the KIM object. */
		public String getKimObjectId(T kimObject);
		/* Used to compare certain fields on a KIM object for equality. */
		public void assertKimObjectsAreEqual(T oldKimObject, T newKimObject) throws Exception;
		/* Used to retrieve a KIM object from the cache based on its ID. */
		public T getKimObjectFromCacheById(String kimObjectId);
		/* Used to retrieve a KIM object from the RoleService based on its ID. */
		public T getKimObjectById(String kimObjectId);
		/* Indicates whether or not the KIM object is expected to be cleared from the cache when the delegation cache is emptied out. */
		public boolean isUnaffectedByClearingDelegationCache();
	}

	/**
	 * A subclass of RoleServiceImpl for providing a convenient way to test the RoleServiceImpl's caching.
	 * For testing purposes, this subclass also overrides the shouldCacheMembersOfRole() method so that it will not cache the members or role "r6".
	 * 
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 */
	private final class RoleServiceTestImpl extends RoleServiceImpl {
		
		// Used for checking the caching capabilities of RoleImpl objects.
		private final KimObjectTestChecker<RoleImpl> ROLE_IMPL_CHECKER = new KimObjectTestChecker<RoleImpl>() {
			public String getKimObjectName() { return "RoleImpl"; }
			public String getKimObjectId(RoleImpl role) { return role.getRoleId(); }
			public void assertKimObjectsAreEqual(RoleImpl oldRole, RoleImpl newRole) throws Exception {
				assertEquals("Role IDs do not match.", oldRole.getRoleId(), newRole.getRoleId());
				assertEquals("KIM type IDs do not match.", oldRole.getKimTypeId(), newRole.getKimTypeId());
				assertEquals("Namespace codes do not match.", oldRole.getNamespaceCode(), newRole.getNamespaceCode());
				assertEquals("Role names do not match.", oldRole.getRoleName(), newRole.getRoleName());
				assertEquals("Role descriptions do not match.", oldRole.getRoleDescription(), newRole.getRoleDescription());
				assertEquals("Role active statuses do not match.", oldRole.isActive(), newRole.isActive());
			}
			public RoleImpl getKimObjectFromCacheById(String roleId) { return getRoleFromCache(roleId); }
			public RoleImpl getKimObjectById(String roleId) { return getRoleImpl(roleId); }
			public boolean isUnaffectedByClearingDelegationCache() { return true; }
		};
		
		// Used for checking the caching capabilities of RoleMemberImpl objects.
		private final KimObjectTestChecker<RoleMemberImpl> ROLE_MEMBER_IMPL_CHECKER = new KimObjectTestChecker<RoleMemberImpl>() {
			public String getKimObjectName() { return "RoleMemberImpl"; }
			public String getKimObjectId(RoleMemberImpl roleMember) { return roleMember.getRoleMemberId(); }
			public void assertKimObjectsAreEqual(RoleMemberImpl oldMember, RoleMemberImpl newMember) throws Exception {
				assertEquals("Role member IDs do not match.", oldMember.getRoleMemberId(), newMember.getRoleMemberId());
				assertEquals("Role IDs do not match.", oldMember.getRoleId(), newMember.getRoleId());
				assertEquals("Role members' member IDs do not match.", oldMember.getMemberId(), newMember.getMemberId());
				assertEquals("Role members' member type codes do not match.", oldMember.getMemberTypeCode(), newMember.getMemberTypeCode());
				assertEquals("Role member active statuses do not match.", oldMember.isActive(), newMember.isActive());
			}
			public RoleMemberImpl getKimObjectFromCacheById(String roleMemberId) { return getRoleMemberFromCache(roleMemberId); }
			public RoleMemberImpl getKimObjectById(String roleMemberId) { return getRoleMemberImpl(roleMemberId); }
			public boolean isUnaffectedByClearingDelegationCache() { return true; }
		};
		
		// Used for checking the caching capabilities of KimDelegationImpl objects.
		private final KimObjectTestChecker<KimDelegationImpl> KIM_DELEGATION_IMPL_CHECKER = new KimObjectTestChecker<KimDelegationImpl>() {
			public String getKimObjectName() { return "KimDelegationImpl"; }
			public String getKimObjectId(KimDelegationImpl delegation) { return delegation.getDelegationId(); }
			public void assertKimObjectsAreEqual(KimDelegationImpl oldDelegation, KimDelegationImpl newDelegation) throws Exception {
				assertEquals("Delegation IDs do not match.", oldDelegation.getDelegationId(), newDelegation.getDelegationId());
				assertEquals("Role IDs do not match.", oldDelegation.getRoleId(), newDelegation.getRoleId());
				assertEquals("KIM type IDs do not match.", oldDelegation.getKimTypeId(), newDelegation.getKimTypeId());
				assertEquals("Delegation type codes do not match.", oldDelegation.getDelegationTypeCode(), newDelegation.getDelegationTypeCode());
				assertEquals("Delegation active statuses do not match.", oldDelegation.isActive(), newDelegation.isActive());
			}
			public KimDelegationImpl getKimObjectFromCacheById(String delegationId) { return getDelegationFromCache(delegationId); }
			public KimDelegationImpl getKimObjectById(String delegationId) { return getKimDelegationImpl(delegationId); }
			public boolean isUnaffectedByClearingDelegationCache() { return false; }
		};
		
		// Used for checking the caching capabilities of KimDelegationMemberImpl objects.
		private final KimObjectTestChecker<KimDelegationMemberImpl> KIM_DLGN_MBR_IMPL_CHECKER = new KimObjectTestChecker<KimDelegationMemberImpl>() {
			public String getKimObjectName() { return "KimDelegationMemberImpl"; }
			public String getKimObjectId(KimDelegationMemberImpl dlgnMember) { return dlgnMember.getDelegationMemberId(); }
			public void assertKimObjectsAreEqual(KimDelegationMemberImpl oldMember, KimDelegationMemberImpl newMember) throws Exception {
				assertEquals("Delegation member IDs do not match.", oldMember.getDelegationMemberId(), newMember.getDelegationMemberId());
				assertEquals("Delegation IDs do not match.", oldMember.getDelegationMemberId(), newMember.getDelegationMemberId());
				assertEquals("Delegation members' role member IDs do not match.", oldMember.getRoleMemberId(), newMember.getRoleMemberId());
				assertEquals("Delegation members' member IDs do not match.", oldMember.getMemberId(), newMember.getMemberId());
				assertEquals("Delegation members' member type codes do not match.", oldMember.getMemberTypeCode(), newMember.getMemberTypeCode());
				assertEquals("Delegation member active statuses do not match.", oldMember.isActive(), newMember.isActive());
			}
			public KimDelegationMemberImpl getKimObjectFromCacheById(String delegationMemberId) { return getDelegationMemberFromCache(delegationMemberId); }
			public KimDelegationMemberImpl getKimObjectById(String delegationMemberId) { return getKimDelegationMemberImpl(delegationMemberId); }
			public boolean isUnaffectedByClearingDelegationCache() { return false; }
		};
		
		/*
		 * Just for testing, this method has been overridden so that it will return false for the role ID "r6".
		 */
		@Override
		protected boolean shouldCacheMembersOfRole(String roleId) {
			return super.shouldCacheMembersOfRole(roleId) && !"r6".equals(roleId);
		}
		
		// --------------------------------------------------------------------------------------------------------------------
		// Methods related to testing read-only search operations involving the cache.
		// --------------------------------------------------------------------------------------------------------------------
		
		/*
		 * Tests the searches on the RoleImpl and RoleMemberImpl caches.
		 */
		private void assertRoleCachingWorksAsExpected() throws Exception {
			List<String> roleIds = Arrays.asList(new String[] {"r3", "r4", "r5", "r6"});
			String[][] roleNames = { {"AUTH_SVC_TEST2","RoleThree"}, {"AUTH_SVC_TEST2","RoleFour"},
					{"AUTH_SVC_TEST2","RoleFive"},{"AUTH_SVC_TEST2","RoleSix"} };
			
			// Ensure that by-ID caching of individual roles is working properly.
			assertKimObjectCachingByIdIsWorking(roleIds, roleIds, ROLE_IMPL_CHECKER);
			
			// Ensure that roles can be obtained properly from the cache by name.
			Map<String,RoleImpl> firstRoleMap = getRoleImplMapByName(roleNames);
			assertRolesAreCachedByNameAsExpected(roleNames, true);
			Map<String,RoleImpl> secondRoleMap = getRoleImplMapByName(roleNames);
			assertKimObjectResultsAreEqual(roleIds.size(), firstRoleMap, secondRoleMap, ROLE_IMPL_CHECKER);
			// Ensure that roles cached by name can be cleared properly.
			getIdentityManagementNotificationService().roleUpdated();
			assertRolesAreCachedByNameAsExpected(roleNames, false);
			firstRoleMap = getRoleImplMapByName(roleNames);
			assertKimObjectResultsAreEqual(roleIds.size(), secondRoleMap, firstRoleMap, ROLE_IMPL_CHECKER);
			// Clean up the cache.
			getIdentityManagementNotificationService().roleUpdated();
			
			// ----------------------------------------------------------------------------------------------------------------
			
			String[] cachedRoleMemberIdsArray = {"r3p5", "r4p10", "r4g5", "r5p6", "r5g6", "r5r3"};
			String[] allRoleMemberIdsArray = {"r3p5", "r4p10", "r4g5", "r5p6", "r5g6", "r5r3", "r6p9", "r6r4"};
			List<String> groupIds = Arrays.asList(new String[] {"g5","g6"});
			List<String> oneGroupId = Collections.singletonList("g5");
			List<String> oneRoleId = Collections.singletonList("r3");
			List<String> oneRoleId2 = Collections.singletonList("r5");
			List<String> oneRoleId3 = Collections.singletonList("r6");
			List<String> oneRoleId4 = Collections.singletonList("r4");
			RoleDaoAction[] daoActions = { RoleDaoAction.ROLE_PRINCIPALS_FOR_PRINCIPAL_ID_AND_ROLE_IDS,
					RoleDaoAction.ROLE_GROUPS_FOR_GROUP_IDS_AND_ROLE_IDS, RoleDaoAction.ROLE_MEMBERSHIPS_FOR_ROLE_IDS_AS_MEMBERS,
							RoleDaoAction.ROLE_MEMBERS_FOR_ROLE_IDS, RoleDaoAction.ROLE_MEMBERS_FOR_ROLE_IDS_WITH_FILTERS };
			
			String[][] results = { {"r3p5"}, {"r5p6"}, {"r3p5","r4p10","r5p6"}, {"r4g5","r5g6"}, {"r4g5"}, {"r5g6"}, {"r5r3"},
					{"r4p10","r4g5","r5r3"}, {"r4p10","r4g5","r5g6","r5r3"}, {"r5g6","r5r3"} };
			String[][] allResults = {{"r6p9"}, {"r3p5","r4p10","r5p6","r6p9"}, {"r5r3","r6r4"}, {"r6r4"},
					{"r4p10","r4g5","r5r3","r6r4"}, {"r4p10","r4g5","r5g6","r5r3","r6r4"}};
			
			// Ensure that by-ID caching of individual role members is working properly.
			assertKimObjectCachingByIdIsWorking(Arrays.asList(cachedRoleMemberIdsArray), Arrays.asList(allRoleMemberIdsArray), ROLE_MEMBER_IMPL_CHECKER);
			
			// Ensure that lists of role members can be cached properly. Note that the role members "r6p9" and "r6r4" will never be cached
			// because this class intentionally blocks the members of role "r6" from being cached; however, they should still be in the search results.
			assertRoleMemberListCachingIsWorking(daoActions[0], roleIds, "p5", null, null, results[0], results[0]);
			assertRoleMemberListCachingIsWorking(daoActions[0], roleIds, "p6", null, null, results[1], results[1]);
			assertRoleMemberListCachingIsWorking(daoActions[0], oneRoleId3, "p9", null, null, new String[]{}, allResults[0]);
			assertRoleMemberListCachingIsWorking(daoActions[0], roleIds, null, null, null, results[2], allResults[1]);
			assertRoleMemberListCachingIsWorking(daoActions[0], oneRoleId2, null, null, null, results[1], results[1]);
			assertRoleMemberListCachingIsWorking(daoActions[0], null, "p5", null, null, results[0], results[0]);
			assertRoleMemberListCachingIsWorking(daoActions[1], roleIds, null, groupIds, null, results[3], results[3]);
			assertRoleMemberListCachingIsWorking(daoActions[1], roleIds, null, oneGroupId, null, results[4], results[4]);
			assertRoleMemberListCachingIsWorking(daoActions[1], null, null, groupIds, null, results[3], results[3]);
			assertRoleMemberListCachingIsWorking(daoActions[1], null, null, oneGroupId, null, results[4], results[4]);
			assertRoleMemberListCachingIsWorking(daoActions[1], roleIds, null, null, null, results[3], results[3]);
			assertRoleMemberListCachingIsWorking(daoActions[1], oneRoleId, null, null, null, new String[]{}, new String[]{});
			assertRoleMemberListCachingIsWorking(daoActions[1], oneRoleId2, null, null, null, results[5], results[5]);
			assertRoleMemberListCachingIsWorking(daoActions[2], roleIds, null, null, null, results[6], allResults[2]);
			assertRoleMemberListCachingIsWorking(daoActions[2], oneRoleId, null, null, null, results[6], results[6]);
			assertRoleMemberListCachingIsWorking(daoActions[2], oneRoleId2, null, null, null, new String[]{}, new String[]{});
			assertRoleMemberListCachingIsWorking(daoActions[2], oneRoleId4, null, null, null, new String[]{}, allResults[3]);
			assertRoleMemberListCachingIsWorking(daoActions[3], roleIds, null, null, Role.PRINCIPAL_MEMBER_TYPE, results[2], allResults[1]);
			assertRoleMemberListCachingIsWorking(daoActions[3], roleIds, null, null, Role.GROUP_MEMBER_TYPE, results[3], results[3]);
			assertRoleMemberListCachingIsWorking(daoActions[3], roleIds, null, null, Role.ROLE_MEMBER_TYPE, results[6], allResults[2]);
			assertRoleMemberListCachingIsWorking(daoActions[3], roleIds, null, null, null, cachedRoleMemberIdsArray, allRoleMemberIdsArray);
			assertRoleMemberListCachingIsWorking(daoActions[4], roleIds, null, groupIds, null, cachedRoleMemberIdsArray, allRoleMemberIdsArray);
			assertRoleMemberListCachingIsWorking(daoActions[4], roleIds, "p10", oneGroupId, null, results[7], allResults[4]);
			assertRoleMemberListCachingIsWorking(daoActions[4], roleIds, "p10", null, null, results[8], allResults[5]);
			assertRoleMemberListCachingIsWorking(daoActions[4], oneRoleId2, "p5", groupIds, null, results[9], results[9]);
		}
		
		/*
		 * Tests the searches on the KimDelegationImpl and KimDelegationMemberImpl caches.
		 */
		private void assertDelegationCachingWorksAsExpected() throws Exception {
			List<String> roleIds1 = Arrays.asList(new String[] {"r3", "r4", "r5"});
			List<String> roleIds2 = Collections.singletonList("r5");
			List<String> roleIds3 = Collections.singletonList("r4");
			List<String> delegationIds = Arrays.asList(new String[] {"d1", "d2"});
			List<String> delegationMemberIds = Arrays.asList(new String[] {"d1p7", "d1r4", "d2g7"});
			
			// Ensure that by-ID caching of individual delegations is working properly.
			assertKimObjectCachingByIdIsWorking(delegationIds, delegationIds, KIM_DELEGATION_IMPL_CHECKER);
			
			// Ensure that lists of delegations can be cached properly.
			assertDelegationListCachingIsWorking(roleIds1, delegationIds, true);
			assertDelegationListCachingIsWorking(roleIds1, delegationIds, false);
			assertDelegationListCachingIsWorking(roleIds2, delegationIds, true);
			assertDelegationListCachingIsWorking(roleIds2, delegationIds, false);
			assertDelegationListCachingIsWorking(roleIds3, new ArrayList<String>(), true);
			assertDelegationListCachingIsWorking(roleIds3, new ArrayList<String>(), false);
			assertDelegationListCachingIsWorking(null, new ArrayList<String>(), true);
			assertDelegationListCachingIsWorking(null, new ArrayList<String>(), false);
			
			// -----------------------------------------------------------------------------------
			
			List<String> oneDelegationId1 = Collections.singletonList("d1");
			List<String> oneDelegationId2 = Collections.singletonList("d2");
			List<String> groupIds = Arrays.asList(new String[] {"g6", "g7"});
			List<String> oneGroupId1 = Collections.singletonList("g7");
			List<String> oneGroupId2 = Collections.singletonList("g6");
			String[][] results = {{}, {"d1p7"}, {"d2g7"}, {"d1p7","d1r4"}, {"d1p7","d1r4","d2g7"}};
			RoleDaoAction delegationPrincipals = RoleDaoAction.DELEGATION_PRINCIPALS_FOR_PRINCIPAL_ID_AND_DELEGATION_IDS;
			RoleDaoAction delegationGroups = RoleDaoAction.DELEGATION_GROUPS_FOR_GROUP_IDS_AND_DELEGATION_IDS;
			RoleDaoAction delegationMembers = RoleDaoAction.DELEGATION_MEMBERS_FOR_DELEGATION_IDS;
			
			// Ensure that by-ID caching of individual delegation members is working properly.
			assertKimObjectCachingByIdIsWorking(delegationMemberIds, delegationMemberIds, KIM_DLGN_MBR_IMPL_CHECKER);
			
			// Ensure that by-ID, by-delegation caching of individual delegation members is working properly.
			String[] dlgnMemberIdArray = {"d1p7","d1r4","d2g7"};
			String[][] idArray = { {"d1","d1p7"}, {"d1","d1r4"}, {"d2","d2g7"} };
			Map<String,KimDelegationMemberImpl> firstMemberMap = getKimDelegationMemberImplMapByIdAndDelegationId(idArray);
			for (String dlgnMemberId : dlgnMemberIdArray) {
				assertNotNull("The Map of members by delegation member ID and delegation ID should have contained an entry for ID " + dlgnMemberId,
						firstMemberMap.get(dlgnMemberId));
			}
			assertDelegationMembersAreCachedByIdAndDelegationIdAsExpected(idArray, true);
			Map<String,KimDelegationMemberImpl> secondMemberMap = getKimDelegationMemberImplMapByIdAndDelegationId(idArray);
			assertKimObjectResultsAreEqual(idArray.length, firstMemberMap, secondMemberMap, KIM_DLGN_MBR_IMPL_CHECKER);
			// Ensure that delegation members cached by ID and delegation ID can be cleared properly.
			getIdentityManagementNotificationService().delegationUpdated();
			assertDelegationMembersAreCachedByIdAndDelegationIdAsExpected(idArray, false);
			firstMemberMap = getKimDelegationMemberImplMapByIdAndDelegationId(idArray);
			assertKimObjectResultsAreEqual(idArray.length, secondMemberMap, firstMemberMap, KIM_DLGN_MBR_IMPL_CHECKER);
			// Clean up the cache.
			getIdentityManagementNotificationService().roleUpdated();
			
			// Ensure that by-member-ID, by-delegation-ID caching of delegation member Lists is working properly.
			String[][] idArray2 = { {"p7","d1","d1p7"}, {"r4","d1","d1r4"}, {"g7","d2","d2g7"}};
			firstMemberMap = getKimDelegationMemberImplMapByMemberAndDelegationId(idArray2);
			for (String dlgnMemberId : dlgnMemberIdArray) {
				assertNotNull("The Map of members by member ID and delegation ID should have contained an entry for ID " + dlgnMemberId,
						firstMemberMap.get(dlgnMemberId));
			}
			assertDelegationMemberListsAreCachedByMemberAndDelegationIdAsExpected(idArray2, true);
			secondMemberMap = getKimDelegationMemberImplMapByMemberAndDelegationId(idArray2);
			assertKimObjectResultsAreEqual(idArray.length, firstMemberMap, secondMemberMap, KIM_DLGN_MBR_IMPL_CHECKER);
			// Ensure that delegation member Lists cached by member ID and delegation ID can be cleared properly.
			getIdentityManagementNotificationService().delegationUpdated();
			assertDelegationMemberListsAreCachedByMemberAndDelegationIdAsExpected(idArray2, false);
			firstMemberMap = getKimDelegationMemberImplMapByMemberAndDelegationId(idArray2);
			assertKimObjectResultsAreEqual(idArray.length, secondMemberMap, firstMemberMap, KIM_DLGN_MBR_IMPL_CHECKER);
			// Clean up the cache.
			getIdentityManagementNotificationService().roleUpdated();
			
			// Ensure that lists of delegation members can be cached properly.
			assertDelegationMemberListCachingIsWorking(delegationPrincipals, delegationIds, "p7", null, results[1]);
			assertDelegationMemberListCachingIsWorking(delegationPrincipals, oneDelegationId1, "p7", null, results[1]);
			assertDelegationMemberListCachingIsWorking(delegationPrincipals, oneDelegationId2, "p7", null, results[0]);
			assertDelegationMemberListCachingIsWorking(delegationPrincipals, delegationIds, "p10", null, results[0]);
			assertDelegationMemberListCachingIsWorking(delegationPrincipals, null, "p7", null, results[1]);
			assertDelegationMemberListCachingIsWorking(delegationPrincipals, delegationIds, null, null, results[1]);
			assertDelegationMemberListCachingIsWorking(delegationGroups, delegationIds, null, groupIds, results[2]);
			assertDelegationMemberListCachingIsWorking(delegationGroups, oneDelegationId2, null, groupIds, results[2]);
			assertDelegationMemberListCachingIsWorking(delegationGroups, oneDelegationId1, null, groupIds, results[0]);
			assertDelegationMemberListCachingIsWorking(delegationGroups, delegationIds, null, oneGroupId1, results[2]);
			assertDelegationMemberListCachingIsWorking(delegationGroups, delegationIds, null, oneGroupId2, results[0]);
			assertDelegationMemberListCachingIsWorking(delegationGroups, null, null, groupIds, results[2]);
			assertDelegationMemberListCachingIsWorking(delegationGroups, delegationIds, null, null, results[2]);
			assertDelegationMemberListCachingIsWorking(delegationMembers, oneDelegationId1, null, null, results[3]);
			assertDelegationMemberListCachingIsWorking(delegationMembers, oneDelegationId2, null, null, results[2]);
			assertDelegationMemberListCachingIsWorking(delegationMembers, delegationIds, null, null, results[4]);
			assertDelegationMemberListCachingIsWorking(delegationMembers, null, null, null, results[4]);
		}
		
		// --------------------------------------------------------------------------------------------------------------------
		// Methods related to testing the clearing of the cache upon updates to various objects.
		// --------------------------------------------------------------------------------------------------------------------
		
		/*
		 * Tests various update and deletion operations to ensure that the correct caches are cleared as a result of the modifications.
		 * Note that after updating/deleting role members or delegation members, this method needs to refresh the member list on the
		 * associated role/delegation manually in order for the deletion testing to work as expected, at least for OJB.
		 */
		private void assertCachesAreClearedOnUpdatesAsExpected() throws Exception {
			AttributeSet attributeSet = new AttributeSet();
			Timestamp yesterday = new Timestamp( System.currentTimeMillis() - (24*60*60*1000) );
			// Some sample roles, role members, delegations, and delegation members to use for testing the statuses of the caches.
			List<String> roleIds = Arrays.asList(new String[] {"r4", "r5"});
			List<String> roleMemberIds = Arrays.asList(new String[] {"r4p10", "r4g5"});
			List<String> delegationIds = Arrays.asList(new String[] {"d1", "d2"});
			List<String> delegationMemberIds = Arrays.asList(new String[] {"d1p7","d1r4"});
			// The primary keys to use for object insertion/deletion.
			List<String> oneRoleId = Collections.singletonList("r3");
			List<String> oneGroupId = Collections.singletonList("g8");
			List<String> oneGroupId2 = Collections.singletonList("g7");
			List<String> oneRoleIdAsMember = Collections.singletonList("r6");
			List<String> oneDelegationId = null;
			String principalRoleMemberId = null;
			String groupRoleMemberId = null;
			String roleAsMemberRoleMemberId = null;
			String delegationId = null;
			String principalDelegationMemberId = null;
			String groupDelegationMemberId = null;
			
			// Ensure that role-assigning tasks will reset the correct caches.
			assertAndPopulateCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds);
			roleUpdateService.assignPrincipalToRole("p8", "AUTH_SVC_TEST2", "RoleThree", attributeSet);
			assertCorrectObjectsWereClearedFromCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds, false);
			getBusinessObjectService().findBySinglePrimaryKey(RoleImpl.class, "r3").refreshReferenceObject("members");
			principalRoleMemberId = assertRoleMemberUpdateSucceeded("p8", getStoredRolePrincipalsForPrincipalIdAndRoleIds(oneRoleId, "p8", attributeSet), true);
			
			assertAndPopulateCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds);
			roleUpdateService.assignGroupToRole("g8", "AUTH_SVC_TEST2", "RoleThree", attributeSet);
			assertCorrectObjectsWereClearedFromCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds, false);
			getBusinessObjectService().findBySinglePrimaryKey(RoleImpl.class, "r3").refreshReferenceObject("members");
			groupRoleMemberId = assertRoleMemberUpdateSucceeded("g8", getStoredRoleGroupsForGroupIdsAndRoleIds(oneRoleId, oneGroupId, null), true);
			
			assertAndPopulateCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds);
			roleUpdateService.assignRoleToRole("r6", "AUTH_SVC_TEST2", "RoleThree", attributeSet);
			assertCorrectObjectsWereClearedFromCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds, false);
			getBusinessObjectService().findBySinglePrimaryKey(RoleImpl.class, "r3").refreshReferenceObject("members");
			roleAsMemberRoleMemberId = assertRoleMemberUpdateSucceeded("r6", getStoredRoleMembershipsForRoleIdsAsMembers(oneRoleIdAsMember, attributeSet), true);
			
			// Ensure that deletion tasks will reset the correct caches.
			assertAndPopulateCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds);
			roleUpdateService.removePrincipalFromRole("p8", "AUTH_SVC_TEST2", "RoleThree", attributeSet);
			assertCorrectObjectsWereClearedFromCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds, false);
			getBusinessObjectService().findBySinglePrimaryKey(RoleImpl.class, "r3").refreshReferenceObject("members");
			assertRoleMemberUpdateSucceeded("p8", getStoredRolePrincipalsForPrincipalIdAndRoleIds(oneRoleId, "p8", attributeSet), false);
			assertRoleMemberHasExpectedExistence(principalRoleMemberId, false);
			
			assertAndPopulateCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds);
			roleUpdateService.removeGroupFromRole("g8", "AUTH_SVC_TEST2", "RoleThree", attributeSet);
			assertCorrectObjectsWereClearedFromCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds, false);
			getBusinessObjectService().findBySinglePrimaryKey(RoleImpl.class, "r3").refreshReferenceObject("members");
			assertRoleMemberUpdateSucceeded("g8", getStoredRoleGroupsForGroupIdsAndRoleIds(oneRoleId, oneGroupId, null), false);
			assertRoleMemberHasExpectedExistence(groupRoleMemberId, false);
			
			assertAndPopulateCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds);
			roleUpdateService.removeRoleFromRole("r6", "AUTH_SVC_TEST2", "RoleThree", attributeSet);
			assertCorrectObjectsWereClearedFromCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds, false);
			getBusinessObjectService().findBySinglePrimaryKey(RoleImpl.class, "r3").refreshReferenceObject("members");
			assertRoleMemberUpdateSucceeded("r6", getStoredRoleMembershipsForRoleIdsAsMembers(oneRoleIdAsMember, attributeSet), false);
			assertRoleMemberHasExpectedExistence(roleAsMemberRoleMemberId, false);
			
			// This time, do role-assigning tasks via the "saveRoleMemberForRole" method.
			assertAndPopulateCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds);
			roleUpdateService.saveRoleMemberForRole(null, "p8", "P", "r3", attributeSet, null, null);
			assertCorrectObjectsWereClearedFromCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds, false);
			getBusinessObjectService().findBySinglePrimaryKey(RoleImpl.class, "r3").refreshReferenceObject("members");
			principalRoleMemberId = assertRoleMemberUpdateSucceeded("p8", getStoredRolePrincipalsForPrincipalIdAndRoleIds(oneRoleId, "p8", attributeSet), true);
			
			assertAndPopulateCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds);
			roleUpdateService.saveRoleMemberForRole(null, "g8", "G", "r3", attributeSet, null, null);
			assertCorrectObjectsWereClearedFromCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds, false);
			getBusinessObjectService().findBySinglePrimaryKey(RoleImpl.class, "r3").refreshReferenceObject("members");
			groupRoleMemberId = assertRoleMemberUpdateSucceeded("g8", getStoredRoleGroupsForGroupIdsAndRoleIds(oneRoleId, oneGroupId, null), true);
			
			assertAndPopulateCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds);
			roleUpdateService.saveRoleMemberForRole(null, "r6", "R", "r3", attributeSet, null, null);
			assertCorrectObjectsWereClearedFromCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds, false);
			getBusinessObjectService().findBySinglePrimaryKey(RoleImpl.class, "r3").refreshReferenceObject("members");
			roleAsMemberRoleMemberId = assertRoleMemberUpdateSucceeded("r6", getStoredRoleMembershipsForRoleIdsAsMembers(oneRoleIdAsMember, attributeSet), true);
			
			// Now perform some delegation-assigning tasks, which currently clear the role-related caches in addition to the delegation-related caches.
			assertAndPopulateCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds);
			roleUpdateService.saveDelegationMemberForRole(null, principalRoleMemberId, "p6", "P", "P", "r3", attributeSet, null, null);
			assertCorrectObjectsWereClearedFromCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds, false);
			
			List<KimDelegationImpl> newDelegation = this.getStoredDelegationImplsForRoleIds(oneRoleId);
			assertEquals("The wrong number of new delegations were found for role r3.", 1, newDelegation.size());
			delegationId = newDelegation.get(0).getDelegationId();
			oneDelegationId = Collections.singletonList(delegationId);
			
			getBusinessObjectService().findBySinglePrimaryKey(KimDelegationImpl.class, delegationId).refreshReferenceObject("members");
			principalDelegationMemberId =
				assertDelegationMemberUpdateSucceeded("p6", this.getStoredDelegationPrincipalsForPrincipalIdAndDelegationIds(oneDelegationId, "p6"), true);
			
			assertAndPopulateCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds);
			roleUpdateService.saveDelegationMemberForRole(null, principalRoleMemberId, "g7", "G", "P", "r3", attributeSet, null, null);
			assertCorrectObjectsWereClearedFromCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds, false);
			getBusinessObjectService().findBySinglePrimaryKey(KimDelegationImpl.class, delegationId).refreshReferenceObject("members");
			groupDelegationMemberId =
				assertDelegationMemberUpdateSucceeded("g7", this.getStoredDelegationGroupsForGroupIdsAndDelegationIds(oneDelegationId, oneGroupId2), true);
			
			// Ensure that inactivation of principal role/delegation members and group role members works properly.
			assertAndPopulateCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds);
			inactivatePrincipalRoleMemberships("p8", yesterday);
			assertCorrectObjectsWereClearedFromCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds, false);
			getBusinessObjectService().findBySinglePrimaryKey(RoleImpl.class, "r3").refreshReferenceObject("members");
			assertRoleMemberUpdateSucceeded("p8", getStoredRolePrincipalsForPrincipalIdAndRoleIds(oneRoleId, "p8", null), false);
			assertRoleMemberHasExpectedExistence(principalRoleMemberId, true);
			
			assertAndPopulateCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds);
			inactivateGroupRoleMemberships(oneGroupId, yesterday);
			assertCorrectObjectsWereClearedFromCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds, false);
			getBusinessObjectService().findBySinglePrimaryKey(RoleImpl.class, "r3").refreshReferenceObject("members");
			assertRoleMemberUpdateSucceeded("g8", getStoredRoleGroupsForGroupIdsAndRoleIds(oneRoleId, oneGroupId, null), false);
			assertRoleMemberHasExpectedExistence(groupRoleMemberId, true);
			
			assertAndPopulateCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds);
			inactivatePrincipalDelegations("p6", yesterday);
			assertCorrectObjectsWereClearedFromCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds, true);
			getBusinessObjectService().findBySinglePrimaryKey(KimDelegationImpl.class, delegationId).refreshReferenceObject("members");
			assertDelegationMemberUpdateSucceeded("p6", this.getStoredDelegationPrincipalsForPrincipalIdAndDelegationIds(oneDelegationId, "p6"), false);
			assertDelegationMemberHasExpectedExistence(principalDelegationMemberId, true);
			
			// Ensure that general role inactivation (including its delegations and memberships) works properly.
			// Note that the code below will inactivate the roles "r6" and "r3".
			
			assertAndPopulateCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds);
			roleInactivated("r6");
			assertCorrectObjectsWereClearedFromCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds, false);
			getBusinessObjectService().findBySinglePrimaryKey(RoleImpl.class, "r3").refreshReferenceObject("members");
			assertRoleMemberUpdateSucceeded("r6", getStoredRoleMembershipsForRoleIdsAsMembers(oneRoleIdAsMember, null), false);
			assertRoleMemberHasExpectedExistence(roleAsMemberRoleMemberId, true);
			
			assertAndPopulateCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds);
			roleInactivated("r3");
			assertCorrectObjectsWereClearedFromCache(roleIds, roleMemberIds, delegationIds, delegationMemberIds, false);
			getBusinessObjectService().findBySinglePrimaryKey(KimDelegationImpl.class, delegationId).refreshReferenceObject("members");
			assertDelegationMemberUpdateSucceeded("g7", this.getStoredDelegationGroupsForGroupIdsAndDelegationIds(oneDelegationId, oneGroupId2), false);
			assertDelegationMemberHasExpectedExistence(groupDelegationMemberId, true);
			
			// Clean up the cache when done.
			getIdentityManagementNotificationService().roleUpdated();
		}
		
		/*
		 * A convenience method for populating the cache and asserting that it has been populated correctly.
		 */
		private void assertAndPopulateCache(List<String> roleIds, List<String> roleMbrIds, List<String> dlgnIds, List<String> dlgnMbrIds) throws Exception {
			getKimObjectMapById(roleIds, ROLE_IMPL_CHECKER);
			getKimObjectMapById(roleMbrIds, ROLE_MEMBER_IMPL_CHECKER);
			getKimObjectMapById(dlgnIds, KIM_DELEGATION_IMPL_CHECKER);
			getKimObjectMapById(dlgnMbrIds, KIM_DLGN_MBR_IMPL_CHECKER);
			assertKimObjectsAreCachedByIdAsExpected(roleIds, true, ROLE_IMPL_CHECKER);
			assertKimObjectsAreCachedByIdAsExpected(roleMbrIds, true, ROLE_MEMBER_IMPL_CHECKER);
			assertKimObjectsAreCachedByIdAsExpected(dlgnIds, true, KIM_DELEGATION_IMPL_CHECKER);
			assertKimObjectsAreCachedByIdAsExpected(dlgnMbrIds, true, KIM_DLGN_MBR_IMPL_CHECKER);
		}
		
		/*
		 * A convenience method for ensuring that the correct objects have been cleared from the cache.
		 */
		private void assertCorrectObjectsWereClearedFromCache(List<String> roleIds, List<String> roleMbrIds, List<String> dlgnIds,
				List<String> dlgnMbrIds, boolean onlyClearedDelegationCache) throws Exception {
			assertKimObjectsAreCachedByIdAsExpected(roleIds, onlyClearedDelegationCache, ROLE_IMPL_CHECKER);
			assertKimObjectsAreCachedByIdAsExpected(roleMbrIds, onlyClearedDelegationCache, ROLE_MEMBER_IMPL_CHECKER);
			assertKimObjectsAreCachedByIdAsExpected(dlgnIds, false, KIM_DELEGATION_IMPL_CHECKER);
			assertKimObjectsAreCachedByIdAsExpected(dlgnMbrIds, false, KIM_DLGN_MBR_IMPL_CHECKER);
		}
		
		/*
		 * A convenience method for ensuring that the given role member has been added to or removed from the given role.
		 * It is assumed that memberList was obtained via a searching mechanism that automatically excludes inactive role members, so even if
		 * memberList is empty, that does not necessarily mean the member was deleted; use assertRoleMemberHasExpectedExistence() to find inactive members.
		 * If the assertions succeed, this method will return null if shouldBeInList is false, or the role member's ID if shouldBeInList is true.
		 */
		private String assertRoleMemberUpdateSucceeded(String memberId, List<RoleMemberImpl> memberList, boolean shouldBeInList) {
			if (shouldBeInList) {
				assertEquals("The role member list has the wrong number of members.", 1, memberList.size());
				assertEquals("The role member does not have the expected member ID", memberId, memberList.get(0).getMemberId());
				return memberList.get(0).getRoleMemberId();
			} else {
				assertEquals("The role member list has the wrong number of members.", 0, memberList.size());
				return null;
			}
		}
		
		/*
		 * A convenience method for checking whether or not a given role member is truly deleted or is just inactive.
		 */
		private void assertRoleMemberHasExpectedExistence(String roleMemberId, boolean justInactive) {
			RoleMemberImpl roleMember = getRoleMemberImpl(roleMemberId);
			if (justInactive) {
				assertNotNull("There should be an inactive but existing role member with ID " + roleMemberId, roleMember);
				assertFalse(roleMemberId + " should be an inactive role member", roleMember.isActive());
			} else {
				assertNull("There should not be an existing role member with ID " + roleMemberId, roleMember);
			}
		}
		
		/*
		 * A convenience method for ensuring that the given delegation member has been added to or removed from the given delegation.
		 * It is assumed that memberList was obtained via a searching mechanism that automatically excludes inactive delegation members, so even if
		 * memberList is empty, that does not always mean the member was deleted; use assertDelegationMemberHasExpectedExistence() to find inactive members.
		 * If the assertions succeed, this method will return null if shouldBeInList is false, or the delegation member's ID if shouldBeInList is true.
		 */
		private String assertDelegationMemberUpdateSucceeded(String memberId, List<KimDelegationMemberImpl> memberList, boolean shouldBeInList) {
			if (shouldBeInList) {
				assertEquals("The delegation member list has the wrong number of members.", 1, memberList.size());
				assertEquals("The delegation member does not have the expected member ID", memberId, memberList.get(0).getMemberId());
				return memberList.get(0).getDelegationMemberId();
			} else {
				assertEquals("The delegation member list has the wrong number of members.", 0, memberList.size());
				return null;
			}
		}
		
		/*
		 * A convenience method for checking whether or not a given delegation member is truly deleted or is just inactive.
		 */
		private void assertDelegationMemberHasExpectedExistence(String delegationMemberId, boolean justInactive) {
			KimDelegationMemberImpl delegationMember = getKimDelegationMemberImpl(delegationMemberId);
			if (justInactive) {
				assertNotNull("There should be an inactive but existing delegation member with ID " + delegationMemberId, delegationMember);
				assertFalse(delegationMemberId + " should be an inactive delegation member", delegationMember.isActive());
			} else {
				assertNull("There should not be an existing delegation member with ID " + delegationMemberId, delegationMember);
			}
		}
		
		// --------------------------------------------------------------------------------------------------------------------
		// Convenience methods related to RoleImpl, RoleMemberImpl, KimDelegationImpl, and KimDelegationMemberImpl caching.
		// --------------------------------------------------------------------------------------------------------------------
		
		/*
		 * A convenience method for obtaining KIM object Maps based on a list of IDs.
		 */
		private <T extends PersistableBusinessObjectBase> Map<String,T> getKimObjectMapById(
				List<String> kimObjectIds, KimObjectTestChecker<T> objectTestChecker) {
			Map<String,T> kimObjectMap = new HashMap<String,T>();
			for (String kimObjectId : kimObjectIds) {
				T tempKimObject = objectTestChecker.getKimObjectById(kimObjectId);
				if (tempKimObject != null) {
					kimObjectMap.put(kimObjectId, tempKimObject);
				}
			}
			return kimObjectMap;
		}

		/*
		 * A convenience method for converting a List of KIM objects into a Map containing mappings between IDs and objects.
		 */
		private <T extends PersistableBusinessObjectBase> Map<String,T> convertKimObjectListToMap(List<T> kimObjectList,
				KimObjectTestChecker<T> objectTestChecker) {
			Map<String,T> kimObjectMap = new HashMap<String,T>();
			if (kimObjectList != null && !kimObjectList.isEmpty()) {
				for (T tempKimObject : kimObjectList) {
					kimObjectMap.put(objectTestChecker.getKimObjectId(tempKimObject), tempKimObject);
				}
			}
			return kimObjectMap;
		}
		
		/*
		 * Checks to ensure that two Maps have the expected size and whose corresponding KIM objects have certain identical fields.
		 */
		private <T extends PersistableBusinessObjectBase> void assertKimObjectResultsAreEqual(int objectCount, Map<String,T> oldKimObjects,
				Map<String,T> newKimObjects, KimObjectTestChecker<T> objectTestChecker) throws Exception {
			final String objectName = objectTestChecker.getKimObjectName();
			assertEquals("Original " + objectName + " map has the wrong number of elements", objectCount, oldKimObjects.size());
			assertEquals("New " + objectName + " map has the wrong number of elements", objectCount, newKimObjects.size());
			for (Map.Entry<String,T> originalKimObject : oldKimObjects.entrySet()) {
				T oldKimObject = originalKimObject.getValue();
				T newKimObject = newKimObjects.get(originalKimObject.getKey());
				assertNotNull("Old " + objectName + " cannot be null", oldKimObject);
				assertNotNull("New " + objectName + " cannot be null", newKimObject);
				if (oldKimObject != newKimObject) {
					objectTestChecker.assertKimObjectsAreEqual(oldKimObject, newKimObject);
				}
			}
		}
		
		/*
		 * A convenience method for ensuring that the by-ID caching for individual KIM objects is working properly.
		 */
		private <T extends PersistableBusinessObjectBase> void assertKimObjectCachingByIdIsWorking(
				List<String> cachedKimObjectIds, List<String> allKimObjectIds, KimObjectTestChecker<T> objectTestChecker) throws Exception {
			// Ensure that KIM objects can be obtained properly from the cache by ID.
			Map<String,T> firstKimObjectMap = getKimObjectMapById(allKimObjectIds, objectTestChecker);
			for (String kimObjectId : allKimObjectIds) {
				assertNotNull("The results should have included the item with ID " + kimObjectId, firstKimObjectMap.get(kimObjectId));
			}
			assertKimObjectsAreCachedByIdAsExpected(cachedKimObjectIds, true, objectTestChecker);
			Map<String,T> secondKimObjectMap = getKimObjectMapById(allKimObjectIds, objectTestChecker);
			assertKimObjectResultsAreEqual(allKimObjectIds.size(), firstKimObjectMap, secondKimObjectMap, objectTestChecker);
			
			// Ensure that KIM objects cached by ID can be cleared properly.
			getIdentityManagementNotificationService().roleUpdated();
			assertKimObjectsAreCachedByIdAsExpected(cachedKimObjectIds, false, objectTestChecker);
			firstKimObjectMap = getKimObjectMapById(allKimObjectIds, objectTestChecker);
			assertKimObjectResultsAreEqual(allKimObjectIds.size(), secondKimObjectMap, firstKimObjectMap, objectTestChecker);
			
			// Ensure that clearing the delegation cache has the desired effect (or lack of effect) on the KIM object's cache.
			getIdentityManagementNotificationService().delegationUpdated();
			assertKimObjectsAreCachedByIdAsExpected(cachedKimObjectIds, objectTestChecker.isUnaffectedByClearingDelegationCache(), objectTestChecker);
			secondKimObjectMap = getKimObjectMapById(allKimObjectIds, objectTestChecker);
			assertKimObjectResultsAreEqual(allKimObjectIds.size(), firstKimObjectMap, secondKimObjectMap, objectTestChecker);
			
			// Clean up the cache when done.
			getIdentityManagementNotificationService().roleUpdated();
		}
		
		/*
		 * A convenience method for checking whether or not certain KIM objects are in their by-ID caches.
		 */
		private void assertKimObjectsAreCachedByIdAsExpected(List<String> kimObjectIds, boolean shouldBeInCache,
				KimObjectTestChecker<? extends PersistableBusinessObjectBase> objectTestChecker) {
			for (String kimObjectId : kimObjectIds) {
				PersistableBusinessObjectBase tempKimObject = objectTestChecker.getKimObjectFromCacheById(kimObjectId);
				if (shouldBeInCache) {
					if (tempKimObject == null) {
						assertNotNull(objectTestChecker.getKimObjectName() + " with ID '" + kimObjectId + "' should have been in the cache",tempKimObject);
					}
				} else if (tempKimObject != null) {
					assertNull(objectTestChecker.getKimObjectName() + " with ID '" + kimObjectId + "' should not have been in the cache",tempKimObject);
				}
			}
		}
		
		// --------------------------------------------------------------------------------------------------------------------
		// Convenience methods related to RoleImpl caching.
		// --------------------------------------------------------------------------------------------------------------------
		
		/*
		 * A convenience method for retrieving multiple RoleImpl objects by namespace code and name.
		 */
		private Map<String,RoleImpl> getRoleImplMapByName(String[][] roleNames) {
			Map<String,RoleImpl> roleMap = new HashMap<String,RoleImpl>();
			for (int i = 0; i < roleNames.length; i++) {
				RoleImpl tempRole = getRoleImplByName(roleNames[i][0], roleNames[i][1]);
				if (tempRole != null && tempRole.isActive()) {
					roleMap.put(tempRole.getRoleId(), tempRole);
				}
			}
			return roleMap;
		}
		
		/*
		 * A convenience method for checking whether or not certain roles are in the by-name cache.
		 */
		private void assertRolesAreCachedByNameAsExpected(String[][] roleNames, boolean shouldBeInCache) {
			for (int i = 0; i < roleNames.length; i++) {
				RoleImpl tempRole = getRoleFromCache(roleNames[i][0], roleNames[i][1]);
				if (shouldBeInCache) {
					if (tempRole == null) {
						assertNotNull("Role with namespace '" + roleNames[i][0] + "' and name '" + roleNames[i][1] +
								"' should have been in the cache", tempRole);
					}
				} else if (tempRole != null) {
					assertNull("Role with namespace '" + roleNames[i][0] + "' and name '" + roleNames[i][1] +
							"' should not have been in the cache", tempRole);
				}
			}
		}
		
		// --------------------------------------------------------------------------------------------------------------------
		// Convenience methods related to RoleMemberImpl caching.
		// --------------------------------------------------------------------------------------------------------------------
		
		/*
		 * A convenience method for choosing the correct helper method that accesses "getRoleMemberImplList".
		 */
		private List<RoleMemberImpl> getCorrectRoleMemberImplList(RoleDaoAction daoAction, List<String> roleIds, String principalId,
				List<String> groupIds, String memberTypeCode) {
			switch (daoAction) {
				case ROLE_PRINCIPALS_FOR_PRINCIPAL_ID_AND_ROLE_IDS : return getStoredRolePrincipalsForPrincipalIdAndRoleIds(roleIds, principalId, null);
				case ROLE_GROUPS_FOR_GROUP_IDS_AND_ROLE_IDS : return getStoredRoleGroupsForGroupIdsAndRoleIds(roleIds, groupIds, null);
				case ROLE_MEMBERS_FOR_ROLE_IDS : return getStoredRoleMembersForRoleIds(roleIds, memberTypeCode, null);
				case ROLE_MEMBERSHIPS_FOR_ROLE_IDS_AS_MEMBERS : return getStoredRoleMembershipsForRoleIdsAsMembers(roleIds, null);
				case ROLE_MEMBERS_FOR_ROLE_IDS_WITH_FILTERS : return getStoredRoleMembersForRoleIdsWithFilters(roleIds, principalId, groupIds, null);
				default : throw new IllegalArgumentException("The 'daoAction' parameter cannot be a non-role-member-related value!");
			}
		}
		
		/*
		 * A convenience method for checking whether or not a certain RoleMemberImpl List should be allowed in the cache. If we are using the KimRoleDao's
		 * "getRoleMembershipsForRoleIdsAsMembers" method, then the list obtained via the given ID should be allowed in the cache if its members belong
		 * to roles that permit member caching. Otherwise, the list should be allowed in the cache if shouldCacheMembersOfRole(roleId) is true.
		 */
		private boolean listShouldBeAllowedInCache(RoleDaoAction daoAction, String roleId) {
			if (RoleDaoAction.ROLE_MEMBERSHIPS_FOR_ROLE_IDS_AS_MEMBERS.equals(daoAction)) {
				List<RoleMemberImpl> roleMembers = getRoleDao().getRoleMembershipsForRoleIdsAsMembers(Collections.singletonList(roleId), null);
				for (RoleMemberImpl roleMember : roleMembers) {
					if (!shouldCacheMembersOfRole(roleMember.getRoleId())) {
						return false;
					}
				}
				return true;
			} else {
				return shouldCacheMembersOfRole(roleId);
			}
		}
		
		/*
		 * A convenience method for checking whether or not certain RoleMemberImpl Lists are being cached.
		 */
		private void assertRoleMemberListCachingIsWorking(RoleDaoAction daoAction, List<String> roleIds, String principalId,
				List<String> groupIds, String memberTypeCode, String[] expectedCachedMembersArray, String[] expectedMembersArray) throws Exception {
			List<String> expectedCachedMembers = Arrays.asList(expectedCachedMembersArray);
			List<String> expectedMembers = Arrays.asList(expectedMembersArray);
			// Ensure that the RoleMemberImpl lists are getting cached as expected.
			Map<String,RoleMemberImpl> firstMemberMap = convertKimObjectListToMap(
					getCorrectRoleMemberImplList(daoAction, roleIds, principalId, groupIds, memberTypeCode), ROLE_MEMBER_IMPL_CHECKER);
			for (String expectedMember : expectedMembers) {
				assertNotNull("The retrieved role members should have included the member with ID " + expectedMember, firstMemberMap.get(expectedMember));
			}
			assertRoleMemberListsAreCachedAsExpected(daoAction, roleIds, principalId, groupIds, memberTypeCode, expectedCachedMembers, true);
			Map<String,RoleMemberImpl> secondMemberMap = convertKimObjectListToMap(
					getCorrectRoleMemberImplList(daoAction, roleIds, principalId, groupIds, memberTypeCode), ROLE_MEMBER_IMPL_CHECKER);
			assertKimObjectResultsAreEqual(expectedMembers.size(), firstMemberMap, secondMemberMap, ROLE_MEMBER_IMPL_CHECKER);
			
			// Ensure that the RoleMemberImpl lists can be cleared from the cache properly.
			getIdentityManagementNotificationService().roleUpdated();
			assertRoleMemberListsAreCachedAsExpected(daoAction, roleIds, principalId, groupIds, memberTypeCode, expectedCachedMembers, false);
			firstMemberMap = convertKimObjectListToMap(
					getCorrectRoleMemberImplList(daoAction, roleIds, principalId, groupIds, memberTypeCode), ROLE_MEMBER_IMPL_CHECKER);
			assertKimObjectResultsAreEqual(expectedMembers.size(), secondMemberMap, firstMemberMap, ROLE_MEMBER_IMPL_CHECKER);
			
			// Ensure that clearing the delegation caches does not end up emptying the role member list or by-ID caches.
			getIdentityManagementNotificationService().delegationUpdated();
			assertRoleMemberListsAreCachedAsExpected(daoAction, roleIds, principalId, groupIds, memberTypeCode, expectedCachedMembers, true);
			secondMemberMap = convertKimObjectListToMap(
					getCorrectRoleMemberImplList(daoAction, roleIds, principalId, groupIds, memberTypeCode), ROLE_MEMBER_IMPL_CHECKER);
			assertKimObjectResultsAreEqual(expectedMembers.size(), firstMemberMap, secondMemberMap, ROLE_MEMBER_IMPL_CHECKER);
			
			// Clean up the cache when done.
			getIdentityManagementNotificationService().roleUpdated();
		}
		
		/*
		 * A convenience method for checking whether or not certain role member lists are in the cache, as well as whether or not the role members from
		 * the cached lists are also cached individually.
		 */
		private void assertRoleMemberListsAreCachedAsExpected(RoleDaoAction daoAction, List<String> roleIds, String principalId,
				List<String> groupIds, String memberTypeCode, List<String> expectedMembers, boolean shouldBeInCache) {
			Map<String,RoleMemberImpl> cachedMembers = new HashMap<String,RoleMemberImpl>();
			// Generate the cache keys to use.
			List<String[]> cacheKeyHelp = new ArrayList<String[]>();
			if (roleIds == null || roleIds.isEmpty()) { roleIds = Collections.singletonList(null); }
			if (groupIds == null || groupIds.isEmpty()) { groupIds = Collections.singletonList(null); }
			switch (daoAction) {
				case ROLE_PRINCIPALS_FOR_PRINCIPAL_ID_AND_ROLE_IDS : // Search for principal role members only.
					for (String roleId : roleIds) {
						cacheKeyHelp.add(new String[] {roleId, getRoleMemberListCacheKey(daoAction, roleId, principalId, null, null)} );
					}
					break;
				case ROLE_GROUPS_FOR_GROUP_IDS_AND_ROLE_IDS : // Search for group role members only.
					for (String roleId : roleIds) {
						for (String groupId : groupIds) {
							cacheKeyHelp.add(new String[] {roleId, getRoleMemberListCacheKey(daoAction, roleId, null, groupId, null)} );
						}
					}
					break;
				case ROLE_MEMBERS_FOR_ROLE_IDS : // Search for role members with the given member type code.
					for (String roleId : roleIds) {
						cacheKeyHelp.add(new String[] {roleId, getRoleMemberListCacheKey(daoAction, roleId, null, null, memberTypeCode)} );
					}
					break;
				case ROLE_MEMBERSHIPS_FOR_ROLE_IDS_AS_MEMBERS : // Search for role members who are also roles.
					for (String roleId : roleIds) {
						cacheKeyHelp.add(new String[] {roleId, getRoleMemberListCacheKey(daoAction, roleId, null, null, null)} );
					}
					break;
				case ROLE_MEMBERS_FOR_ROLE_IDS_WITH_FILTERS : // Search for role members that might be roles, principals, or groups.
					for (String roleId : roleIds) {
						cacheKeyHelp.add(new String[] {roleId, getRoleMemberListCacheKey(daoAction, roleId, null, null, Role.ROLE_MEMBER_TYPE)} );
						cacheKeyHelp.add(new String[] {roleId, getRoleMemberListCacheKey(daoAction, roleId, principalId, null,Role.PRINCIPAL_MEMBER_TYPE)});
						for (String groupId : groupIds) {
							cacheKeyHelp.add( new String[] {roleId, getRoleMemberListCacheKey(daoAction, roleId, null, groupId, Role.GROUP_MEMBER_TYPE)} );
						}
					}
					break;
				default : // The daoActionToTake parameter is invalid; throw an exception.
					throw new IllegalArgumentException("The 'daoActionToTake' parameter cannot be a non-role-member-related value!");
			}
			
			// Ensure that the lists are present or absent from the cache as expected.
			for (String[] cacheData : cacheKeyHelp) {
				List<RoleMemberImpl> cachedList = (List<RoleMemberImpl>) getCacheAdministrator().getFromCache(cacheData[1]);
				if (cachedList == null) {
					if (shouldBeInCache && listShouldBeAllowedInCache(daoAction, cacheData[0])) {
						fail("The role member list with key '" + cacheData[1] + "' should have been cached");
					}
				} else if (!shouldBeInCache || !listShouldBeAllowedInCache(daoAction, cacheData[0])) {
					fail("The role member list with key '" + cacheData[1] +
							((shouldBeInCache) ? "' should have been excluded from the cache" : "' should not have been cached"));
				} else {
					for (RoleMemberImpl cachedObject : cachedList) { cachedMembers.put(cachedObject.getRoleMemberId(), cachedObject); }
				}
			}
			
			// Ensure that the expected role members were found in (or were absent from) the list cache and the by-ID cache.
			if (shouldBeInCache) {
				assertEquals("The wrong number of role members were obtained from the role member list cache.",expectedMembers.size(),cachedMembers.size());
				for (String expectedRoleMemberId : expectedMembers) {
					assertNotNull("The role member lists from the cache did not contain the role member with ID " + expectedRoleMemberId,
							cachedMembers.get(expectedRoleMemberId));
				}
			} else {
				assertEquals("No role members should have been obtained from the role member list cache.", 0, cachedMembers.size());
			}
			assertKimObjectsAreCachedByIdAsExpected(expectedMembers, shouldBeInCache, ROLE_MEMBER_IMPL_CHECKER);
		}
		
		// --------------------------------------------------------------------------------------------------------------------
		// Convenience methods related to KimDelegationImpl caching.
		// --------------------------------------------------------------------------------------------------------------------
		
		/*
		 * A convenience method for checking whether or not certain KimDelegationImpl Lists are being cached.
		 */
		private void assertDelegationListCachingIsWorking(List<String> roleIds, List<String> expectedDelegations, boolean getsMaps) throws Exception {
			// Ensure that the KimDelegationImpl lists are getting cached as expected.
			Map<String,KimDelegationImpl> firstDelegationMap = (getsMaps) ? getStoredDelegationImplMapFromRoleIds(roleIds) :
				convertKimObjectListToMap(getStoredDelegationImplsForRoleIds(roleIds), KIM_DELEGATION_IMPL_CHECKER);
			assertDelegationListsAreCachedAsExpected(roleIds, expectedDelegations, true);
			Map<String,KimDelegationImpl> secondDelegationMap = (getsMaps) ? getStoredDelegationImplMapFromRoleIds(roleIds) :
				convertKimObjectListToMap(getStoredDelegationImplsForRoleIds(roleIds), KIM_DELEGATION_IMPL_CHECKER);
			assertKimObjectResultsAreEqual(expectedDelegations.size(), firstDelegationMap, secondDelegationMap, KIM_DELEGATION_IMPL_CHECKER);
			
			// Ensure that the KimDelegationImpl lists can be cleared from the cache properly when roleUpdated() is called.
			getIdentityManagementNotificationService().roleUpdated();
			assertDelegationListsAreCachedAsExpected(roleIds, expectedDelegations, false);
			firstDelegationMap = (getsMaps) ? getStoredDelegationImplMapFromRoleIds(roleIds) :
				convertKimObjectListToMap(getStoredDelegationImplsForRoleIds(roleIds), KIM_DELEGATION_IMPL_CHECKER);
			assertKimObjectResultsAreEqual(expectedDelegations.size(), secondDelegationMap, firstDelegationMap, KIM_DELEGATION_IMPL_CHECKER);
			
			// Ensure that the KimDelegationImpl lists are cleared out when delegationUpdated() is called.
			getIdentityManagementNotificationService().delegationUpdated();
			assertDelegationListsAreCachedAsExpected(roleIds, expectedDelegations, false);
			secondDelegationMap = (getsMaps) ? getStoredDelegationImplMapFromRoleIds(roleIds) :
				convertKimObjectListToMap(getStoredDelegationImplsForRoleIds(roleIds), KIM_DELEGATION_IMPL_CHECKER);
			assertKimObjectResultsAreEqual(expectedDelegations.size(), firstDelegationMap, secondDelegationMap, KIM_DELEGATION_IMPL_CHECKER);
			
			// Clean up the cache when done.
			getIdentityManagementNotificationService().roleUpdated();
		}
		
		/*
		 * A convenience method for checking whether or not certain delegation Lists are in the cache.
		 */
		private void assertDelegationListsAreCachedAsExpected(List<String> roleIds, List<String> expectedDelegations, boolean shouldBeInCache) {
			Map<String,KimDelegationImpl> cachedDelegations = new HashMap<String,KimDelegationImpl>();
			
			// Ensure that the lists are present or absent from the cache as expected.
			if (roleIds != null && !roleIds.isEmpty()) {
				for (String roleId : roleIds) {
					List<KimDelegationImpl> cachedList = (List<KimDelegationImpl>) getCacheAdministrator().getFromCache(getDelegationListCacheKey(roleId));
					if (cachedList == null) {
						if (shouldBeInCache) {
							fail("The delegation list for role ID '" + roleId + "' should have been cached");
						}
					} else if (!shouldBeInCache) {
						fail("The delegation list for role ID '" + roleId + "' should not have been cached");
					} else {
						for (KimDelegationImpl cachedObject : cachedList) { cachedDelegations.put(cachedObject.getDelegationId(), cachedObject); }
					}
				}
			}
			
			// Ensure that the expected delegations were found in (or were absent from) the list cache and the by-ID cache.
			if (shouldBeInCache) {
				assertEquals("The wrong number of delegations were obtained from the delegation list cache.",
						expectedDelegations.size(), cachedDelegations.size());
				for (String expectedDelegationId : expectedDelegations) {
					assertNotNull("The delegation lists from the cache did not contain the delegation with ID " + expectedDelegationId,
							cachedDelegations.get(expectedDelegationId));
				}
			} else {
				assertEquals("No delegations should have been obtained from the delegation list cache.", 0, cachedDelegations.size());
			}
			assertKimObjectsAreCachedByIdAsExpected(expectedDelegations, shouldBeInCache, KIM_DELEGATION_IMPL_CHECKER);
		}
		
		// --------------------------------------------------------------------------------------------------------------------
		// Convenience methods related to KimDelegationMemberImpl caching.
		// --------------------------------------------------------------------------------------------------------------------

		/*
		 * A convenience method for retrieving Maps of delegation members by delegation and delegation member ID.
		 */
		private Map<String,KimDelegationMemberImpl> getKimDelegationMemberImplMapByIdAndDelegationId(String[][] idArray) {
			Map<String,KimDelegationMemberImpl> finalResults = new HashMap<String,KimDelegationMemberImpl>();
			for (String[] ids : idArray) {
				KimDelegationMemberImpl delegationMember = getKimDelegationMemberImplByDelegationAndId(ids[0], ids[1]);
				if (delegationMember != null && delegationMember.isActive()) {
					finalResults.put(delegationMember.getDelegationMemberId(), delegationMember);
				}
			}
			return finalResults;
		}
		
		/*
		 * A convenience method for retrieving Maps of delegation members by member ID and delegation ID.
		 */
		private Map<String,KimDelegationMemberImpl> getKimDelegationMemberImplMapByMemberAndDelegationId(String[][] idArray) {
			Map<String,KimDelegationMemberImpl> finalResults = new HashMap<String,KimDelegationMemberImpl>();
			for (String[] ids : idArray) {
				List<KimDelegationMemberImpl> memberList = getKimDelegationMemberImplListByMemberAndDelegationId(ids[0], ids[1]);
				if (memberList != null && !memberList.isEmpty()) {
					for (KimDelegationMemberImpl delegationMember : memberList) {
						if (delegationMember != null && delegationMember.isActive()) {
							finalResults.put(delegationMember.getDelegationMemberId(), delegationMember);
						}
					}
				}
			}
			return finalResults;
		}
		
		/*
		 * A convenience method for testing whether or not delegation members obtained by delegation ID and delegation member ID are cached as expected.
		 */
		private void assertDelegationMembersAreCachedByIdAndDelegationIdAsExpected(String[][] idArray, boolean shouldBeInCache) throws Exception {
			for (String[] ids : idArray) {
				KimDelegationMemberImpl tempMember = getDelegationMemberByDelegationAndIdFromCache(ids[0], ids[1]);
				if (shouldBeInCache) {
					if (tempMember == null) {
						assertNotNull("Delegation member with ID '" + ids[1] + "' and belonging to delegation with ID '" + ids[0] +
								"' should have been in the cache", tempMember);
					}
				} else if (tempMember != null) {
					assertNull("Delegation member with ID '" + ids[1] + "' and belonging to delegation with ID '" + ids[0] +
							"' should not have been in the cache", tempMember);
				}
			}
		}
		
		/*
		 * A convenience method for testing whether or not delegation member lists obtained by member and delegation ID are cached as expected.
		 */
		private void assertDelegationMemberListsAreCachedByMemberAndDelegationIdAsExpected(String[][] idArray, boolean shouldBeInCache) throws Exception {
			for (String[] ids : idArray) {
				List<KimDelegationMemberImpl> tempList = getDelegationMemberListByMemberAndDelegationIdFromCache(ids[0], ids[1]);
				if (shouldBeInCache) {
					if (tempList == null) {
						assertNotNull("Delegation member list for member ID '" + ids[0] + "' and delegation ID '" + ids[1] +
								"' should have been in the cache", tempList);
					} else {
						if (ids.length - 2 != tempList.size()) {
							assertEquals("Delegation member list for member ID '" + ids[0] + "' and delegation ID '" + ids[1] +
									"' contains the wrong number of elements.", ids.length - 2, tempList.size());
						} else {
							for (int i = 2; i < ids.length; i++) {
								boolean foundMember = false;
								for (KimDelegationMemberImpl tempMember : tempList) {
									if (tempMember.getDelegationMemberId().equals(ids[i])) {
										foundMember = true;
										break;
									}
								}
								if (!foundMember) {
									fail("Delegation member list for member ID '" + ids[0] + "' and delegation ID '" + ids[1] +
											"' should have contained the delegation member with ID " + ids[i]);
								}
							}
						}
					}
				} else if (tempList != null) {
					assertNull("Delegation member list for member ID '" + ids[0] + "' and delegation ID '" + ids[1] +
							"' should not have been in the cache", tempList);
				}
			}
		}
		
		/*
		 * A convenience method for choosing the correct helper method for retrieving delegation member lists (or maps converted to lists).
		 */
		private List<KimDelegationMemberImpl> getCorrectKimDelegationMemberImplList(RoleDaoAction daoAction, List<String> delegationIds,
				String principalId, List<String> groupIds) {
			switch (daoAction) {
				case DELEGATION_PRINCIPALS_FOR_PRINCIPAL_ID_AND_DELEGATION_IDS :
					return getStoredDelegationPrincipalsForPrincipalIdAndDelegationIds(delegationIds, principalId);
				case DELEGATION_GROUPS_FOR_GROUP_IDS_AND_DELEGATION_IDS :
					return getStoredDelegationGroupsForGroupIdsAndDelegationIds(delegationIds, groupIds);
				case DELEGATION_MEMBERS_FOR_DELEGATION_IDS :
					Map<String,List<KimDelegationMemberImpl>> memberMap = getStoredDelegationMembersForDelegationIds(delegationIds);
					List<KimDelegationMemberImpl> finalResults = new ArrayList<KimDelegationMemberImpl>();
					for (List<KimDelegationMemberImpl> subList : memberMap.values()) {
						finalResults.addAll(subList);
					}
					return finalResults;
				default : throw new IllegalArgumentException("The 'daoAction' parameter cannot be a non-delegation-member-related value!");
			}
		}
		
		/*
		 * A convenience method for checking whether or not certain KimDelegationMemberImpl Lists are being cached.
		 */
		private void assertDelegationMemberListCachingIsWorking(RoleDaoAction daoAction, List<String> delegationIds, String principalId,
				List<String> groupIds, String[] expectedMembersArray) throws Exception {
			List<String> expectedMembers = Arrays.asList(expectedMembersArray);
			// Ensure that the KimDelegationMemberImpl lists are getting cached as expected.
			Map<String,KimDelegationMemberImpl> firstMemberMap = convertKimObjectListToMap(getCorrectKimDelegationMemberImplList(
					daoAction, delegationIds, principalId, groupIds), KIM_DLGN_MBR_IMPL_CHECKER);
			assertDelegationMemberListsAreCachedAsExpected(daoAction, delegationIds, principalId, groupIds, expectedMembers, true);
			Map<String,KimDelegationMemberImpl> secondMemberMap = convertKimObjectListToMap(getCorrectKimDelegationMemberImplList(
					daoAction, delegationIds, principalId, groupIds), KIM_DLGN_MBR_IMPL_CHECKER);
			assertKimObjectResultsAreEqual(expectedMembers.size(), firstMemberMap, secondMemberMap, KIM_DLGN_MBR_IMPL_CHECKER);
			
			// Ensure that the KimDelegationMemberImpl lists can be cleared from the cache properly when roleUpdated() is called.
			getIdentityManagementNotificationService().roleUpdated();
			assertDelegationMemberListsAreCachedAsExpected(daoAction, delegationIds, principalId, groupIds, expectedMembers, false);
			firstMemberMap = convertKimObjectListToMap(getCorrectKimDelegationMemberImplList(
					daoAction, delegationIds, principalId, groupIds), KIM_DLGN_MBR_IMPL_CHECKER);
			assertKimObjectResultsAreEqual(expectedMembers.size(), secondMemberMap, firstMemberMap, KIM_DLGN_MBR_IMPL_CHECKER);
			
			// Ensure that the KimDelegationMemberImpl lists are cleared out when delegationUpdated() is called.
			getIdentityManagementNotificationService().delegationUpdated();
			assertDelegationMemberListsAreCachedAsExpected(daoAction, delegationIds, principalId, groupIds, expectedMembers, false);
			secondMemberMap = convertKimObjectListToMap(getCorrectKimDelegationMemberImplList(
					daoAction, delegationIds, principalId, groupIds), KIM_DLGN_MBR_IMPL_CHECKER);
			assertKimObjectResultsAreEqual(expectedMembers.size(), firstMemberMap, secondMemberMap, KIM_DLGN_MBR_IMPL_CHECKER);
			
			// Clean up the cache when done.
			getIdentityManagementNotificationService().roleUpdated();
		}
		
		/*
		 * A convenience method for checking whether or not certain delegation member Lists are in the cache.
		 */
		private void assertDelegationMemberListsAreCachedAsExpected(RoleDaoAction daoAction, List<String> delegationIds, String principalId,
				List<String> groupIds, List<String> expectedMembers, boolean shouldBeInCache) {
			Map<String,KimDelegationMemberImpl> cachedMembers = new HashMap<String,KimDelegationMemberImpl>();
			List<String[]> cacheKeys = new ArrayList<String[]>();
			if (delegationIds == null || delegationIds.isEmpty()) { delegationIds = Collections.singletonList(null); }
			if (groupIds == null || groupIds.isEmpty()) { groupIds = Collections.singletonList(null); }
			
			// Create the cache key generation parameters based on the intended KimRoleDao action.
			switch (daoAction) {
				case DELEGATION_PRINCIPALS_FOR_PRINCIPAL_ID_AND_DELEGATION_IDS : // Search for principal delegation members.
					for (String delegationId : delegationIds) {
						cacheKeys.add(new String[] {delegationId, principalId, null});
					}
					break;
				case DELEGATION_GROUPS_FOR_GROUP_IDS_AND_DELEGATION_IDS : // Search for group delegation members.
					for (String delegationId : delegationIds) {
						for (String groupId : groupIds) {
							cacheKeys.add(new String[] {delegationId, null, groupId});
						}
					}
					break;
				case DELEGATION_MEMBERS_FOR_DELEGATION_IDS : // Search for delegation members regardless of their member type.
					for (String delegationId : delegationIds) {
						cacheKeys.add(new String[] {delegationId, null, null});
					}
					break;
				default : // daoAction is invalid; throw an exception.
					throw new IllegalArgumentException("The 'daoAction' parameter cannot refer to a non-delegation-member-related value!");
			}
			
			// Ensure that the lists are present or absent from the cache as expected.
			for (String[] cacheKey : cacheKeys) {
				List<KimDelegationMemberImpl> cachedList = (List<KimDelegationMemberImpl>) getCacheAdministrator().getFromCache(
						getDelegationMemberListCacheKey(daoAction, cacheKey[0], cacheKey[1], cacheKey[2]));
				if (cachedList == null) {
					if (shouldBeInCache) {
						fail("The delegation member list with key '" +
								getDelegationMemberListCacheKey(daoAction, cacheKey[0], cacheKey[1], cacheKey[2]) + "' should have been cached");
					}
				} else if (!shouldBeInCache) {
					fail("The delegation member list with key '" +
							getDelegationMemberListCacheKey(daoAction, cacheKey[0], cacheKey[1], cacheKey[2]) + "' should not have been cached");
				} else {
					for (KimDelegationMemberImpl cachedObject : cachedList) { cachedMembers.put(cachedObject.getDelegationMemberId(), cachedObject); }
				}
			}
			
			// Ensure that the expected delegation members were found in (or were absent from) the list cache and the by-ID cache.
			if (shouldBeInCache) {
				assertEquals("The wrong number of delegation members were obtained from the delegation member list cache.",
						expectedMembers.size(), cachedMembers.size());
				for (String expectedDelegationMemberId : expectedMembers) {
					assertNotNull("The delegation member lists from the cache did not contain the delegation member with ID " + expectedDelegationMemberId,
							cachedMembers.get(expectedDelegationMemberId));
				}
			} else {
				assertEquals("No delegation members should have been obtained from the delegation member list cache.", 0, cachedMembers.size());
			}
			assertKimObjectsAreCachedByIdAsExpected(expectedMembers, shouldBeInCache, KIM_DLGN_MBR_IMPL_CHECKER);
		}
	}
}
