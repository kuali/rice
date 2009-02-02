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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.rice.core.util.MaxAgeSoftReference;
import org.kuali.rice.core.util.MaxSizeMap;
import org.kuali.rice.core.util.RiceDebugUtils;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.role.KimRole;
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
	private static final Logger LOG = Logger.getLogger( RoleManagementServiceImpl.class );
	
	protected RoleService roleService;
	
	// Max age defined in seconds
	protected int roleCacheMaxSize = 200;
	protected int roleCacheMaxAgeSeconds = 30;
	
	protected MaxSizeMap<String,MaxAgeSoftReference<KimRoleInfo>> roleByIdCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<KimRoleInfo>> roleByNameCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<Collection<RoleMembershipInfo>>> roleMembersWithDelegationCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<List<AttributeSet>>> roleQualifiersForPrincipalCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<Boolean>> principalHasRoleCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<Collection<String>>> memberPrincipalIdsCache;
	
	public void afterPropertiesSet() throws Exception {
		roleByIdCache = new MaxSizeMap<String,MaxAgeSoftReference<KimRoleInfo>>( roleCacheMaxSize );
		roleByNameCache = new MaxSizeMap<String,MaxAgeSoftReference<KimRoleInfo>>( roleCacheMaxSize );
		roleMembersWithDelegationCache = new MaxSizeMap<String,MaxAgeSoftReference<Collection<RoleMembershipInfo>>>( roleCacheMaxSize );
		roleQualifiersForPrincipalCache = new MaxSizeMap<String,MaxAgeSoftReference<List<AttributeSet>>>( roleCacheMaxSize );
		principalHasRoleCache = new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( roleCacheMaxSize );
		memberPrincipalIdsCache = new MaxSizeMap<String,MaxAgeSoftReference<Collection<String>>>(roleCacheMaxSize );
	}
	
	public void flushRoleCaches() {
		roleByIdCache.clear();
		roleByNameCache.clear();
		roleMembersWithDelegationCache.clear();
		roleQualifiersForPrincipalCache.clear();
		principalHasRoleCache.clear();
		memberPrincipalIdsCache.clear();		
	}
	
	// Caching helper methods

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
	
	protected Boolean getPrincipalHasRoleCacheCache( String key ) {
		MaxAgeSoftReference<Boolean> hasRoleRef = principalHasRoleCache.get( key );
		if ( hasRoleRef != null ) {
			return hasRoleRef.get();
		}
		return null;
	}
	
	protected void addRoleToCaches( KimRoleInfo role ) {
		if ( role != null ) {
			roleByNameCache.put( role.getNamespaceCode() + "-" + role.getRoleName(), new MaxAgeSoftReference<KimRoleInfo>( roleCacheMaxAgeSeconds, role ) );
			roleByIdCache.put( role.getRoleId(), new MaxAgeSoftReference<KimRoleInfo>( roleCacheMaxAgeSeconds, role ) );
		}
	}

	protected void addRoleMembersWithDelegationToCache( String key, Collection<RoleMembershipInfo> members ) {
		if ( members != null ) {
			roleMembersWithDelegationCache.put( key, new MaxAgeSoftReference<Collection<RoleMembershipInfo>>( roleCacheMaxAgeSeconds, members ) );
		}
	}

	protected void addRoleQualifiersForPrincipalToCache( String key, List<AttributeSet> qualifiers ) {
		if ( qualifiers != null ) {
			roleQualifiersForPrincipalCache.put( key, new MaxAgeSoftReference<List<AttributeSet>>( roleCacheMaxAgeSeconds, qualifiers ) );
		}
	}
	
	protected void addPrincipalHasRoleToCache( String key, boolean hasRole ) {
		principalHasRoleCache.put( key, new MaxAgeSoftReference<Boolean>( roleCacheMaxAgeSeconds, hasRole ) );
	}
		
	// Cached methods

	protected Collection<String> getRoleMemberPrincipalIdsCache(String key) {
		MaxAgeSoftReference<Collection<String>> memberPrincipalIdsRef = memberPrincipalIdsCache.get(key);
		if ( memberPrincipalIdsRef != null ) {
			return memberPrincipalIdsRef.get();
		}
		return null;
	}
	protected void addRoleMemberPrincipalIdsToCache(String key, Collection<String> principalIds) {
		memberPrincipalIdsCache.put(key, new MaxAgeSoftReference<Collection<String>>(roleCacheMaxAgeSeconds, principalIds ));
	}
	
	public Collection<String> getRoleMemberPrincipalIds(String namespaceCode, String roleName, AttributeSet qualification) {
		StringBuffer cacheKey = new StringBuffer();
		cacheKey.append( namespaceCode ).append( '/' ).append( roleName );
		addAttributesToKey(cacheKey, qualification);
		String key = cacheKey.toString();
		Collection<String> principalIds = getRoleMemberPrincipalIdsCache(key);
		if (principalIds != null) {
			return principalIds;
		}
		principalIds = getRoleService().getRoleMemberPrincipalIds(namespaceCode, roleName, qualification);
		addRoleMemberPrincipalIdsToCache(key, principalIds);
		return principalIds;
	}

	public KimRoleInfo getRole(String roleId) {
		KimRoleInfo role = getRoleByIdCache(roleId);
		if (role != null) {
			return role;
		}
		role = getRoleService().getRole(roleId);
		addRoleToCaches(role);
    	return role;
	}

	public KimRoleInfo getRoleByName(String namespaceCode, String roleName) {
		KimRoleInfo role = getRoleByNameCache(namespaceCode + "-" + roleName);
		if (role != null) {
			return role;
		}
		role = getRoleService().getRoleByName(namespaceCode, roleName);
		addRoleToCaches(role);
    	return role;
	}

	public String getRoleIdByName(String namespaceCode, String roleName) {
		KimRoleInfo role = getRoleByName( namespaceCode, roleName );
		if ( role == null ) {
			return null;
		}
		return role.getRoleId();
	}
	
	protected void addIdsToKey( StringBuffer key, List<String> idList ) {
		if ( idList == null || idList.isEmpty() ) {
			key.append( "[null]" );
		} else {
			for ( String id : idList ) {
				key.append( '|' ).append( id ).append( '|' );
			}
		}
	}
	
	protected void addAttributesToKey( StringBuffer key, AttributeSet attributes ) {
		if ( attributes == null || attributes.isEmpty() ) {
			key.append( "[null]" );
		} else {
			for (Map.Entry<String, String> entry : attributes.entrySet()) {
				key.append( entry.getKey() ).append( '=' ).append( entry.getValue() ).append( '|' );
			}
		}
	}

	public Collection<RoleMembershipInfo> getRoleMembers(List<String> roleIds, AttributeSet qualification) {
		StringBuffer cacheKey = new StringBuffer();
		addIdsToKey( cacheKey, roleIds );
		cacheKey.append(  '/' );
		addAttributesToKey( cacheKey, qualification );
		String key = cacheKey.toString();
		Collection<RoleMembershipInfo> members = getRoleMembersWithDelegationCache(key);
		if (members != null) {
			return members;
		}
		members = getRoleService().getRoleMembers(roleIds, qualification);
		addRoleMembersWithDelegationToCache(key, members);
    	return members;
    }

	public List<AttributeSet> getRoleQualifiersForPrincipal(String principalId, List<String> roleIds, AttributeSet qualification) {		
		StringBuffer cacheKey = new StringBuffer( principalId );
		cacheKey.append( '/' );
		addIdsToKey( cacheKey, roleIds );
		cacheKey.append(  '/' );
		addAttributesToKey( cacheKey, qualification );
		String key = cacheKey.toString();
		List<AttributeSet> qualifiers = getRoleQualifiersForPrincipalCache(key);
		if (qualifiers != null) {
			return qualifiers;
		}
		qualifiers = getRoleService().getRoleQualifiersForPrincipal(principalId, roleIds, qualification);
		addRoleQualifiersForPrincipalToCache(key, qualifiers);
    	return qualifiers;
	}

	public List<AttributeSet> getRoleQualifiersForPrincipal(String principalId, String namespaceCode, String roleName, AttributeSet qualification) {
		StringBuffer cacheKey = new StringBuffer( principalId );
		cacheKey.append( '/' );
		cacheKey.append( namespaceCode ).append( '-' ).append( roleName );
		cacheKey.append( '/' );
		addAttributesToKey( cacheKey, qualification );
		String key = cacheKey.toString();
		List<AttributeSet> qualifiers = getRoleQualifiersForPrincipalCache(key);
		if (qualifiers != null) {
			return qualifiers;
		}
		qualifiers = getRoleService().getRoleQualifiersForPrincipal(principalId, namespaceCode, roleName, qualification);
		addRoleQualifiersForPrincipalToCache(key, qualifiers);
    	return qualifiers;
	}

	public boolean isRoleActive(String roleId) {
		KimRoleInfo role = getRole( roleId );
		return role != null && role.isActive();
	}

	public boolean principalHasRole(String principalId, List<String> roleIds, AttributeSet qualification) {
		if ( LOG.isDebugEnabled() ) {
			logPrincipalHasRoleCheck(principalId, roleIds, qualification);
		}
		StringBuffer cacheKey = new StringBuffer( principalId );
		cacheKey.append( '/' );
		addIdsToKey( cacheKey, roleIds );
		cacheKey.append( '/' );
		addAttributesToKey( cacheKey, qualification );
		String key = cacheKey.toString();
		Boolean hasRole = getPrincipalHasRoleCacheCache(key);
		if (hasRole == null) {
			hasRole = getRoleService().principalHasRole(principalId, roleIds, qualification);
			addPrincipalHasRoleToCache(key, hasRole);
    		if ( LOG.isDebugEnabled() ) {
    			LOG.debug( "Result: " + hasRole );
    		}
		} else {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug( "Result Found in cache using key: " + key + "\nResult: " + hasRole );
			}
		}
    	return hasRole;
	}

	

	// Helper methods
	
	public void removeCacheEntries( String roleId, String principalId ) {
		if ( principalId != null ) {
			String keyPrefix = principalId + "-";
			Iterator<String> cacheIterator = principalHasRoleCache.keySet().iterator();
			while ( cacheIterator.hasNext() ) {
				String cacheKey = cacheIterator.next();
				if ( cacheKey.startsWith( keyPrefix ) ) {
					cacheIterator.remove();
				}
			}
			cacheIterator = roleQualifiersForPrincipalCache.keySet().iterator();
			while ( cacheIterator.hasNext() ) {
				String cacheKey = cacheIterator.next();
				if ( cacheKey.startsWith( keyPrefix ) ) {
					cacheIterator.remove();
				}
			}
		}
		if ( roleId != null ) {
			roleByIdCache.remove( roleId );
			roleByNameCache.clear();
			String keySubstring = "|" + roleId + "|";
			Iterator<String> cacheIterator = principalHasRoleCache.keySet().iterator();
			while ( cacheIterator.hasNext() ) {
				String cacheKey = cacheIterator.next();
				if( cacheKey.contains( keySubstring ) ) {
					cacheIterator.remove();
				}
			}
			cacheIterator = roleQualifiersForPrincipalCache.keySet().iterator();
			while ( cacheIterator.hasNext() ) {
				String cacheKey = cacheIterator.next();
				if( cacheKey.contains( keySubstring ) ) {
					cacheIterator.remove();
				}
			}
			cacheIterator = roleMembersWithDelegationCache.keySet().iterator();
			while ( cacheIterator.hasNext() ) {
				String cacheKey = cacheIterator.next();
				if( cacheKey.contains( keySubstring ) ) {
					cacheIterator.remove();
				}
			}
			cacheIterator = memberPrincipalIdsCache.keySet().iterator();
			while ( cacheIterator.hasNext() ) {
				String cacheKey = cacheIterator.next();
				if( cacheKey.contains( keySubstring ) ) {
					cacheIterator.remove();
				}
			}
			
		}
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.RoleService#getRoleQualifiersForPrincipalIncludingNested(java.lang.String, java.util.List, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public List<AttributeSet> getRoleQualifiersForPrincipalIncludingNested(
			String principalId, List<String> roleIds, AttributeSet qualification) {
		return getRoleService().getRoleQualifiersForPrincipalIncludingNested(principalId, roleIds, qualification);
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.RoleService#getRoleQualifiersForPrincipalIncludingNested(java.lang.String, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public List<AttributeSet> getRoleQualifiersForPrincipalIncludingNested(
			String principalId, String namespaceCode, String roleName,
			AttributeSet qualification) {
		return getRoleService().getRoleQualifiersForPrincipalIncludingNested(principalId, namespaceCode, roleName, qualification);
	}
	
	public void assignGroupToRole(String groupId, String namespaceCode, String roleName,
			AttributeSet qualifications) {
		getRoleService().assignGroupToRole( groupId, namespaceCode, roleName, qualifications );
		KimRoleInfo role = getRoleByName( namespaceCode, roleName );
		removeCacheEntries( role.getRoleId(), null );
	}

	public void assignPrincipalToRole(String principalId, String namespaceCode, String roleName,
			AttributeSet qualifications) {
		KimRoleInfo role = getRoleByName( namespaceCode, roleName );
		getRoleService().assignPrincipalToRole( principalId, namespaceCode, roleName, qualifications );
		removeCacheEntries( role.getRoleId(), principalId );
	}

	public void removeGroupFromRole(String groupId, String namespaceCode, String roleName,
			AttributeSet qualifications) {
		getRoleService().removeGroupFromRole( groupId, namespaceCode, roleName, qualifications );
		KimRoleInfo role = getRoleByName( namespaceCode, roleName );
		removeCacheEntries( role.getRoleId(), null );
	}

	public void removePrincipalFromRole(String principalId, String namespaceCode, String roleName,
			AttributeSet qualifications) {
		KimRoleInfo role = getRoleByName( namespaceCode, roleName );
		getRoleService().removePrincipalFromRole( principalId, namespaceCode, roleName, qualifications );
		removeCacheEntries( role.getRoleId(), principalId );
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

	public void setRoleCacheMaxAgeSeconds(int roleCacheMaxAge) {
		this.roleCacheMaxAgeSeconds = roleCacheMaxAge;
	}

	/**
	 * @see org.kuali.rice.kim.service.RoleService#getRolesSearchResults(java.util.Map)
	 */
	public List<? extends KimRole> getRolesSearchResults(
			Map<String, String> fieldValues) {
		return getRoleService().getRolesSearchResults(fieldValues);
	}

    protected void logPrincipalHasRoleCheck(String principalId, List<String> roleIds, AttributeSet roleQualifiers ) {
		StringBuffer sb = new StringBuffer();
		sb.append(  '\n' );
		sb.append( "Has Role     : " ).append( roleIds ).append( '\n' );
		for ( String roleId : roleIds ) {
			KimRoleInfo role = getRole( roleId );
			sb.append( "        Name : " ).append( role.getNamespaceCode() ).append( '/').append( role.getRoleName() );
			sb.append( " (" ).append( roleId ).append( ')' );
			sb.append( '\n' );
		}
		sb.append( "   Principal : " ).append( principalId );
		if ( principalId != null ) {
			KimPrincipal principal = KIMServiceLocator.getIdentityManagementService().getPrincipal( principalId );
			if ( principal != null ) {
				sb.append( " (" ).append( principal.getPrincipalName() ).append( ')' );
			}
		}
		sb.append( '\n' );
		sb.append( "     Details :\n" );
		if ( roleQualifiers != null ) {
			sb.append( roleQualifiers.formattedDump( 15 ) );
		} else {
			sb.append( "               [null]\n" );
		}
		LOG.debug( sb.append( RiceDebugUtils.getTruncatedStackTrace(true) ).toString() );
    }
	
}
