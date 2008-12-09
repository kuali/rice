/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kim.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.service.RoleService;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoleManagementServiceImpl implements RoleManagementService, InitializingBean {
	
	protected RoleService roleService;
	
	// Max age defined in seconds
	protected int roleCacheMaxSize = 200;
	protected int roleCacheMaxAge = 30;
	
	protected Map<String,MaxAgeSoftReference<List<String>>> impliedRoleIdsCache;
	protected Map<String,MaxAgeSoftReference<List<String>>> implyingRoleIdsCache;
	protected Map<String,MaxAgeSoftReference<KimRoleInfo>> roleByIdCache;
	protected Map<String,MaxAgeSoftReference<KimRoleInfo>> roleByNameCache;
	protected Map<String,MaxAgeSoftReference<String>> roleIdByNameCache;
	protected Map<String,MaxAgeSoftReference<Collection<RoleMembershipInfo>>> roleMembersWithDelegationCache;
	protected Map<String,MaxAgeSoftReference<List<AttributeSet>>> roleQualifiersForPrincipalCache;
	protected Map<String,MaxAgeSoftReference<Boolean>> isRoleActiveCache;
	protected Map<String,MaxAgeSoftReference<Boolean>> principalHasRoleCache;
	
	public void afterPropertiesSet() throws Exception {
		impliedRoleIdsCache = new HashMap<String,MaxAgeSoftReference<List<String>>>( roleCacheMaxSize );
		implyingRoleIdsCache = new HashMap<String,MaxAgeSoftReference<List<String>>>( roleCacheMaxSize );
		roleByIdCache = new HashMap<String,MaxAgeSoftReference<KimRoleInfo>>( roleCacheMaxSize );
		roleByNameCache = new HashMap<String,MaxAgeSoftReference<KimRoleInfo>>( roleCacheMaxSize );
		roleIdByNameCache = new HashMap<String,MaxAgeSoftReference<String>>( roleCacheMaxSize );
		roleMembersWithDelegationCache = new HashMap<String,MaxAgeSoftReference<Collection<RoleMembershipInfo>>>( roleCacheMaxSize );
		roleQualifiersForPrincipalCache = new HashMap<String,MaxAgeSoftReference<List<AttributeSet>>>( roleCacheMaxSize );
		isRoleActiveCache = new HashMap<String,MaxAgeSoftReference<Boolean>>( roleCacheMaxSize );
		principalHasRoleCache = new HashMap<String,MaxAgeSoftReference<Boolean>>( roleCacheMaxSize );
	}
	
	// Caching helper methods
	
	protected List<String> getImpliedRoleIdsCache( String roleId ) {
		MaxAgeSoftReference<List<String>> roleIdRef = impliedRoleIdsCache.get( roleId );
		if ( roleIdRef != null ) {
			return roleIdRef.get();
		}
		return null;
	}
	
	protected List<String> getImplyingRoleIdsCache( String roleId ) {
		MaxAgeSoftReference<List<String>> roleIdRef = implyingRoleIdsCache.get( roleId );
		if ( roleIdRef != null ) {
			return roleIdRef.get();
		}
		return null;
	}

	protected KimRoleInfo getRoleByIdCache( String roleId ) {
		MaxAgeSoftReference<KimRoleInfo> roleRef = roleByIdCache.get( roleId );
		if ( roleRef != null ) {
			return roleRef.get();
		}
		return null;
	}

	protected KimRoleInfo getRoleByNameCache( String key ) {
		MaxAgeSoftReference<KimRoleInfo> roleRef = roleByNameCache.get( key );
		if ( roleRef != null ) {
			return roleRef.get();
		}
		return null;
	}
	
	protected String getRoleIdByNameCache( String key ) {
		MaxAgeSoftReference<String> roleIdRef = roleIdByNameCache.get( key );
		if ( roleIdRef != null ) {
			return roleIdRef.get();
		}
		return null;
	}

	protected Collection<RoleMembershipInfo> getRoleMembersWithDelegationCache( String key ) {
		MaxAgeSoftReference<Collection<RoleMembershipInfo>> roleMembersRef = roleMembersWithDelegationCache.get( key );
		if ( roleMembersRef != null ) {
			return roleMembersRef.get();
		}
		return null;
	}
	
	protected List<AttributeSet> getRoleQualifiersForPrincipalCache( String key ) {
		MaxAgeSoftReference<List<AttributeSet>> qualifiersRef = roleQualifiersForPrincipalCache.get( key );
		if ( qualifiersRef != null ) {
			return qualifiersRef.get();
		}
		return null;
	}
	
	protected Boolean getIsRoleActiveCache( String key ) {
		MaxAgeSoftReference<Boolean> activeRef = isRoleActiveCache.get( key );
		if ( activeRef != null ) {
			return activeRef.get();
		}
		return null;
	}

	protected Boolean getPrincipalHasRoleCacheCache( String key ) {
		MaxAgeSoftReference<Boolean> hasRoleRef = principalHasRoleCache.get( key );
		if ( hasRoleRef != null ) {
			return hasRoleRef.get();
		}
		return null;
	}
	
	protected void addImpliedRoleIdsToCache( String roleId, List<String> ids ) {
		if ( ids != null ) {
			impliedRoleIdsCache.put( roleId, new MaxAgeSoftReference<List<String>>( roleCacheMaxAge, ids ) );
		}
	}

	protected void addImplyingRoleIdsToCache( String roleId, List<String> ids ) {
		if ( ids != null ) {
			implyingRoleIdsCache.put( roleId, new MaxAgeSoftReference<List<String>>( roleCacheMaxAge, ids ) );
		}
	}
	
	protected void addRoleByIdToCache( KimRoleInfo role ) {
		if ( role != null ) {
			roleByIdCache.put( role.getRoleId(), new MaxAgeSoftReference<KimRoleInfo>( roleCacheMaxAge, role ) );
		}
	}

	protected void addRoleByNameToCache( String key, KimRoleInfo role ) {
		if ( role != null ) {
			roleByNameCache.put( key, new MaxAgeSoftReference<KimRoleInfo>( roleCacheMaxAge, role ) );
		}
	}

	protected void addRoleIdByNameToCache( String key, String roleId ) {
		if ( roleId != null ) {
			roleIdByNameCache.put( key, new MaxAgeSoftReference<String>( roleCacheMaxAge, roleId ) );
		}
	}

	protected void addRoleMembersWithDelegationToCache( String key, Collection<RoleMembershipInfo> members ) {
		if ( members != null ) {
			roleMembersWithDelegationCache.put( key, new MaxAgeSoftReference<Collection<RoleMembershipInfo>>( roleCacheMaxAge, members ) );
		}
	}

	protected void addRoleQualifiersForPrincipalToCache( String key, List<AttributeSet> qualifiers ) {
		if ( qualifiers != null ) {
			roleQualifiersForPrincipalCache.put( key, new MaxAgeSoftReference<List<AttributeSet>>( roleCacheMaxAge, qualifiers ) );
		}
	}
	
	protected void addIsRoleActiveToCache( String key, boolean active ) {
		isRoleActiveCache.put( key, new MaxAgeSoftReference<Boolean>( roleCacheMaxAge, active ) );
	}
	
	protected void addPrincipalHasRoleCacheToCache( String key, boolean hasRole ) {
		principalHasRoleCache.put( key, new MaxAgeSoftReference<Boolean>( roleCacheMaxAge, hasRole ) );
	}
		
	// Cached methods
	
	public List<String> getImpliedRoleIds(String roleId) {
		List<String> ids = getImpliedRoleIdsCache(roleId);
		if (ids != null) {
			return ids;
		}
    	ids = getRoleService().getImpliedRoleIds(roleId);
    	addImpliedRoleIdsToCache(roleId, ids);
    	return ids;
	}

	public List<String> getImplyingRoleIds(String roleId) {
		List<String> ids = getImplyingRoleIdsCache(roleId);
		if (ids != null) {
			return ids;
		}
    	ids = getRoleService().getImplyingRoleIds(roleId);
    	addImplyingRoleIdsToCache(roleId, ids);
    	return ids;
	}

	public KimRoleInfo getRole(String roleId) {
		KimRoleInfo role = getRoleByIdCache(roleId);
		if (role != null) {
			return role;
		}
		role = getRoleService().getRole(roleId);
		addRoleByIdToCache(role);
    	return role;
	}

	public KimRoleInfo getRoleByName(String namespaceCode, String roleName) {
		String key = namespaceCode + "-" + roleName;
		KimRoleInfo role = getRoleByNameCache(key);
		if (role != null) {
			return role;
		}
		role = getRoleService().getRoleByName(namespaceCode, roleName);
		addRoleByNameToCache(key, role);
    	return role;
	}

	public String getRoleIdByName(String namespaceCode, String roleName) {
		String key = namespaceCode + "-" + roleName;
		String roleId = getRoleIdByNameCache(key);
		if (roleId != null) {
			return roleId;
		}
		roleId = getRoleService().getRoleIdByName(namespaceCode, roleName);
		addRoleIdByNameToCache(key, roleId);
    	return roleId;
	}

	public Collection<RoleMembershipInfo> getRoleMembers(List<String> roleIds, AttributeSet qualification) {
		String key = buildIdsKey(roleIds) + "-" + buildQualificationKey(qualification);
		Collection<RoleMembershipInfo> members = getRoleMembersWithDelegationCache(key);
		if (members != null) {
			return members;
		}
		members = getRoleService().getRoleMembers(roleIds, qualification);
		addRoleMembersWithDelegationToCache(key, members);
    	return members;
    }

	public List<AttributeSet> getRoleQualifiersForPrincipal(String principalId, List<String> roleIds, AttributeSet qualification) {		
		String key = principalId + "-" + buildIdsKey(roleIds) + "-" + buildQualificationKey(qualification);
		List<AttributeSet> qualifiers = getRoleQualifiersForPrincipalCache(key);
		if (qualifiers != null) {
			return qualifiers;
		}
		qualifiers = getRoleService().getRoleQualifiersForPrincipal(principalId, roleIds, qualification);
		addRoleQualifiersForPrincipalToCache(key, qualifiers);
    	return qualifiers;
	}

	public List<AttributeSet> getRoleQualifiersForPrincipal(String principalId, String namespaceCode, String roleName, AttributeSet qualification) {
		String key = principalId + "-" + namespaceCode + "-" + roleName + "-" + buildQualificationKey(qualification);
		List<AttributeSet> qualifiers = getRoleQualifiersForPrincipalCache(key);
		if (qualifiers != null) {
			return qualifiers;
		}
		qualifiers = getRoleService().getRoleQualifiersForPrincipal(principalId, namespaceCode, roleName, qualification);
		addRoleQualifiersForPrincipalToCache(key, qualifiers);
    	return qualifiers;
	}

	public boolean isRoleActive(String roleId) {
		Boolean active = getIsRoleActiveCache(roleId);
		if (active != null) {
			return active;
		}
		active = getRoleService().isRoleActive(roleId);
		addIsRoleActiveToCache(roleId, active);
    	return active;
	}

	public boolean principalHasRole(String principalId, List<String> roleIds, AttributeSet qualification) {
		String key = principalId + "-" + buildIdsKey(roleIds) + "-" + buildQualificationKey(qualification);
		Boolean hasRole = getPrincipalHasRoleCacheCache(key);
		if (hasRole != null) {
			return hasRole;
		}
		hasRole = getRoleService().principalHasRole(principalId, roleIds, qualification);
		addPrincipalHasRoleCacheToCache(key, hasRole);
    	return hasRole;
	}

	// Methods that are not cached
	
	public void assignQualifiedRoleToGroup(String groupId, String roleId, AttributeSet qualifier) {
		getRoleService().assignQualifiedRoleToGroup(groupId, roleId, qualifier);
	}

	public void assignQualifiedRoleToPrincipal(String principalId, String roleId, AttributeSet qualifier) {
		getRoleService().assignQualifiedRoleToPrincipal(principalId, roleId, qualifier);		
	}

	public void saveRole(KimRoleInfo role) {
		getRoleService().saveRole(role);
	}

	// Helper methods
	
	private String buildIdsKey(List<String> roleIds) {
		if ( roleIds == null || roleIds.isEmpty() ) {
			return "null";
		}
		String key = "";
		for (String id : roleIds) {
			key += id + "-";
		}
		return key.substring(0, key.length() - 1);
	}

	private String buildQualificationKey(AttributeSet qualifications) {
		if ( qualifications == null || qualifications.isEmpty() ) {
			return "null";
		}
		String key = "";
		for (Map.Entry<String, String> qualification : qualifications.entrySet()) {
			key += qualification.getKey() + ":" + qualification.getValue() + "-";
		}
		return key.substring(0, key.length() - 1);
	}

	// Spring and injection methods
	
	public RoleService getRoleService() {
		if ( roleService == null ) {
			roleService = KIMServiceLocator.getRoleService();
		}
		return roleService;
	}
	
	public void setRoleCacheMaxSize(int roleCacheMaxSize) {
		this.roleCacheMaxSize = roleCacheMaxSize;
	}

	public void setRoleCacheMaxAge(int roleCacheMaxAge) {
		this.roleCacheMaxAge = roleCacheMaxAge;
	}

}
