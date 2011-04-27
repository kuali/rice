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

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.MaxAgeSoftReference;
import org.kuali.rice.core.util.MaxSizeMap;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.role.dto.DelegateMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.DelegateTypeInfo;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.role.dto.RoleResponsibilityActionInfo;
import org.kuali.rice.kim.bo.role.dto.RoleResponsibilityInfo;
import org.kuali.rice.kim.api.services.KIMServiceLocator;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.RoleUpdateService;
import org.kuali.rice.kim.service.support.KimRoleTypeService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.springframework.beans.factory.InitializingBean;

import javax.jws.WebParam;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RoleManagementServiceImpl implements RoleManagementService, InitializingBean {
	private static final Logger LOG = Logger.getLogger( RoleManagementServiceImpl.class );
	
	private RoleService roleService;
	private KimTypeInfoService typeInfoService;
	private RoleUpdateService roleUpdateService;
	
	// Max age defined in seconds
	protected int roleCacheMaxSize = 200;
	protected int roleCacheMaxAgeSeconds = 30;
	
	protected Map<String,MaxAgeSoftReference<KimRoleInfo>> roleByIdCache;
	protected Map<String,MaxAgeSoftReference<KimRoleInfo>> roleByNameCache;
	protected Map<String,MaxAgeSoftReference<List<RoleMembershipInfo>>> roleMembersWithDelegationCache;
	protected Map<String,MaxAgeSoftReference<List<AttributeSet>>> roleQualifiersForPrincipalCache;
	protected Map<String,MaxAgeSoftReference<Boolean>> principalHasRoleCache;
	protected Map<String,MaxAgeSoftReference<Collection<String>>> memberPrincipalIdsCache;
	
	protected Map<String, Boolean> shouldCacheRoleCache;
	
	public void afterPropertiesSet() throws Exception {
		roleByIdCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<KimRoleInfo>>( roleCacheMaxSize ) );
		roleByNameCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<KimRoleInfo>>( roleCacheMaxSize ) );
		roleMembersWithDelegationCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<List<RoleMembershipInfo>>>( roleCacheMaxSize ) );
		roleQualifiersForPrincipalCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<List<AttributeSet>>>( roleCacheMaxSize ) );
		principalHasRoleCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( roleCacheMaxSize ) );
		memberPrincipalIdsCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<Collection<String>>>( roleCacheMaxSize ) );
		shouldCacheRoleCache = Collections.synchronizedMap(new HashMap<String, Boolean>());
	}
	
	public void flushRoleCaches() {
		flushInternalRoleCache();
		flushInternalRoleMemberCache();
		flushInternalDelegationCache();
		flushInternalDelegationMemberCache();
		roleByIdCache.clear();
		roleByNameCache.clear();
		roleMembersWithDelegationCache.clear();
		roleQualifiersForPrincipalCache.clear();
		principalHasRoleCache.clear();
		memberPrincipalIdsCache.clear();
		shouldCacheRoleCache.clear();
	}
	
	public void flushRoleMemberCaches() {
		flushInternalRoleMemberCache();
		roleMembersWithDelegationCache.clear();
		memberPrincipalIdsCache.clear();
	}
	
	public void flushDelegationCaches() {
		flushInternalDelegationCache();
		flushInternalDelegationMemberCache();
		roleMembersWithDelegationCache.clear();
	}
	
	public void flushDelegationMemberCaches() {
		flushInternalDelegationMemberCache();
		roleMembersWithDelegationCache.clear();
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
	
	protected List<RoleMembershipInfo> getRoleMembersWithDelegationCache( String key ) {
		MaxAgeSoftReference<List<RoleMembershipInfo>> roleMembersRef = roleMembersWithDelegationCache.get( key );
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

	protected void addRoleMembersWithDelegationToCache( String key, List<RoleMembershipInfo> members ) {
		if ( members != null ) {
			roleMembersWithDelegationCache.put( key, new MaxAgeSoftReference<List<RoleMembershipInfo>>( roleCacheMaxAgeSeconds, members ) );
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
	
	/**
	 * @see org.kuali.rice.kim.service.RoleService#getRoleMemberPrincipalIds(java.lang.String, java.lang.String, org.kuali.rice.core.util.AttributeSet)
	 */
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

	/**
	 * @see org.kuali.rice.kim.service.RoleService#getRole(java.lang.String)
	 */
	public KimRoleInfo getRole(String roleId) {
		KimRoleInfo role = getRoleByIdCache(roleId);
		if (role != null) {
			return role;
		}
		role = getRoleService().getRole(roleId);
		addRoleToCaches(role);
    	return role;
	}

	/**
	 * @see org.kuali.rice.kim.service.RoleService#getRoleByName(java.lang.String, java.lang.String)
	 */
	public KimRoleInfo getRoleByName(String namespaceCode, String roleName) {
		KimRoleInfo role = getRoleByNameCache(namespaceCode + "-" + roleName);
		if (role != null) {
			return role;
		}
		role = getRoleService().getRoleByName(namespaceCode, roleName);
		addRoleToCaches(role);
    	return role;
	}

	/**
	 * @see org.kuali.rice.kim.service.RoleService#getRoleIdByName(java.lang.String, java.lang.String)
	 */
	public String getRoleIdByName(String namespaceCode, String roleName) {
		KimRoleInfo role = getRoleByName( namespaceCode, roleName );
		if ( role == null ) {
			return null;
		}
		return role.getRoleId();
	}
		
	/**
	 * @see org.kuali.rice.kim.service.RoleService#getRoles(java.util.List)
	 */
	public List<KimRoleInfo> getRoles(List<String> roleIds) {
		return getRoleService().getRoles(roleIds);
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

	/**
	 * @see org.kuali.rice.kim.service.RoleService#getRoleMembers(java.util.List, org.kuali.rice.core.util.AttributeSet)
	 */
	public List<RoleMembershipInfo> getRoleMembers(List<String> roleIds, AttributeSet qualification) {
		List<String>[] filteredRoles = filterRoleIdsByCachingAbility(roleIds);
		List<String> cacheRoles = filteredRoles[0];
		List<String> noCacheRoles = filteredRoles[1];
		
		List<RoleMembershipInfo> members = null;
		String key = null;
		
		if (!cacheRoles.isEmpty()) {
			StringBuffer cacheKey = new StringBuffer();
			addIdsToKey( cacheKey, cacheRoles );
			cacheKey.append(  '/' );
			addAttributesToKey( cacheKey, qualification );
			key = cacheKey.toString();
			members = getRoleMembersWithDelegationCache(key);
		}
		if (members != null) {
			if (!noCacheRoles.isEmpty()) {
				members.addAll(getRoleService().getRoleMembers(noCacheRoles, qualification));
			}
			return members;
		}
		
		if (!cacheRoles.isEmpty()) {
			members = getRoleService().getRoleMembers(cacheRoles, qualification);
			addRoleMembersWithDelegationToCache(key, members);
		} else {
			members = new ArrayList<RoleMembershipInfo>();
		}
		if (!noCacheRoles.isEmpty()) {
			members.addAll(getRoleService().getRoleMembers(noCacheRoles, qualification));
		}
    	return members;
    }

	/**
	 * @see org.kuali.rice.kim.service.RoleService#getRoleQualifiersForPrincipal(java.lang.String, java.util.List, org.kuali.rice.core.util.AttributeSet)
	 */
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

	/**
	 * @see org.kuali.rice.kim.service.RoleService#getRoleQualifiersForPrincipal(java.lang.String, java.lang.String, java.lang.String, org.kuali.rice.core.util.AttributeSet)
	 */
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
		List<String>[] filteredRoles = filterRoleIdsByCachingAbility(roleIds);
		List<String> cacheRoles = filteredRoles[0];
		List<String> noCacheRoles = filteredRoles[1];
		
		Boolean hasRole = null;
		String key = null;
		if (!cacheRoles.isEmpty()) {
			StringBuffer cacheKey = new StringBuffer();
			cacheKey.append( principalId );
			cacheKey.append( '/' );
			addIdsToKey( cacheKey, cacheRoles );
			cacheKey.append( '/' );
			addAttributesToKey( cacheKey, qualification );
			key = cacheKey.toString();
			hasRole = getPrincipalHasRoleCacheCache(key);
		}
		if (hasRole == null || !hasRole.booleanValue()) {
			if (!cacheRoles.isEmpty()) {
				hasRole = getRoleService().principalHasRole(principalId, cacheRoles, qualification);
				addPrincipalHasRoleToCache(key, hasRole);
			}
			if ((hasRole == null || !hasRole.booleanValue()) && !noCacheRoles.isEmpty()) {
				hasRole = getRoleService().principalHasRole(principalId, noCacheRoles, qualification);
			}
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
	
	/**
	 * Determines if the role with the given id will be cached
	 * 
	 * @param roleId the role id of the role to determine caching on
	 * @return true if the role should be cached, false otherwise
	 */
	protected boolean shouldCacheRole(String roleId) {
		Boolean shouldCacheRoleAnswer = shouldCacheRoleCache.get(roleId);
		if (shouldCacheRoleAnswer == null) {
		
			final KimRoleTypeService roleType = getRoleTypeService(roleId);
			final KimRoleInfo roleInfo = this.getRole(roleId);
			if (roleType != null && roleInfo != null) {
			    try {
			        shouldCacheRoleAnswer = new Boolean(roleType.shouldCacheRoleMembershipResults(roleInfo.getNamespaceCode(), roleInfo.getRoleName()));
			        shouldCacheRoleCache.put(roleId, shouldCacheRoleAnswer);
			    } catch (Exception e) {//was: RiceRemoteServiceConnectionException
			        LOG.warn("Unable to connect to remote service for roleType " + roleInfo.getNamespaceCode() + "-" + roleInfo.getRoleName());
                    LOG.warn(e.getMessage());
			        return Boolean.FALSE;
			    }
			} else {
				shouldCacheRoleAnswer = Boolean.TRUE; // no type?  that means we get to do the default - cache
				shouldCacheRoleCache.put(roleId, shouldCacheRoleAnswer);
			}
		}
		return shouldCacheRoleAnswer.booleanValue();
	}
	
	/**
	 * Splits the given List of role ids into two Lists, one with roles to cache, and one with roles
	 * not to cache
	 * 
	 * @param roleIds the List of role ids to split
	 * @return an array of two Lists of role ids - the first, roles which can be cached and the second, roles which should not be cached
	 */
	@SuppressWarnings("unchecked")
	protected List<String>[] filterRoleIdsByCachingAbility(List<String> roleIds) {
		List<String> cacheRoles = new ArrayList<String>();
		List<String> noCacheRoles = new ArrayList<String>();
		Set<String> alreadyFilteredRoles = new HashSet<String>();
		
		for (String roleId : roleIds) {
			if (!alreadyFilteredRoles.contains(roleId)) {
				alreadyFilteredRoles.add(roleId);
				if (shouldCacheRole(roleId)) {
					cacheRoles.add(roleId);
				} else {
					noCacheRoles.add(roleId);
				}
			}
		}
		return (List<String>[])new List[] { cacheRoles, noCacheRoles };
	}
	
	/**
     * Retrieves the role type service associated with the given role ID
     * 
     * @param roleId the role ID to get the role type service for
     * @return the Role Type Service
     */
    protected KimRoleTypeService getRoleTypeService( String roleId ) {
    	KimRoleTypeService service = null;
    		
    	final KimRoleInfo roleInfo = getRoleService().getRole(roleId);
    	if (roleInfo != null) {
	    	final KimType roleType = getTypeInfoService().getKimType(roleInfo.getKimTypeId());
	    	if ( roleType != null ) {
	        	service = getRoleTypeService(roleType);
	    	}
    	}
    	return service;
    }

	/**
	 * Retrieves a role type from the given type info
	 * 
	 * @param typeInfo
	 * @return
	 */
	protected KimRoleTypeService getRoleTypeService( KimType typeInfo ) {
		String serviceName = typeInfo.getServiceName();
		if ( serviceName != null ) {
			try {
				KimTypeService service = (KimTypeService) KIMServiceLocatorInternal.getService(serviceName);
				if ( service != null && service instanceof KimRoleTypeService) {
					return (KimRoleTypeService)service;
				} else {
					return (KimRoleTypeService) KIMServiceLocatorInternal.getService("kimNoMembersRoleTypeService");
				}
			} catch ( Exception ex ) {
				LOG.error( "Unable to find role type service with name: " + serviceName, ex);
				return (KimRoleTypeService) KIMServiceLocatorInternal.getService("kimNoMembersRoleTypeService");
			}
		}
		return null;
	}

	/**
	 * @see org.kuali.rice.kim.service.RoleService#getPrincipalIdSubListWithRole(java.util.List, java.lang.String, java.lang.String, org.kuali.rice.core.util.AttributeSet)
	 */
	public List<String> getPrincipalIdSubListWithRole(
			List<String> principalIds, String roleNamespaceCode,
			String roleName, AttributeSet qualification) {
		return getRoleService().getPrincipalIdSubListWithRole(principalIds,
				roleNamespaceCode, roleName, qualification);
	}

	

	// Helper methods
	
	public void removeCacheEntries( String roleId, String principalId ) {
		if ( principalId != null ) {
			String keyPrefix = principalId + "-";
			synchronized ( principalHasRoleCache ) {
				Iterator<String> cacheIterator = principalHasRoleCache.keySet().iterator();
				while ( cacheIterator.hasNext() ) {
					String cacheKey = cacheIterator.next();
					if ( cacheKey.startsWith( keyPrefix ) ) {
						cacheIterator.remove();
					}
				}
			}
			synchronized ( roleQualifiersForPrincipalCache ) {
				Iterator<String> cacheIterator = roleQualifiersForPrincipalCache.keySet().iterator();
				while ( cacheIterator.hasNext() ) {
					String cacheKey = cacheIterator.next();
					if ( cacheKey.startsWith( keyPrefix ) ) {
						cacheIterator.remove();
					}
				}
			}
		}
		if ( roleId != null ) {
			roleByIdCache.remove( roleId );
			roleByNameCache.clear();
			String keySubstring = "|" + roleId + "|";
			synchronized ( principalHasRoleCache ) {
				Iterator<String> cacheIterator = principalHasRoleCache.keySet().iterator();
				while ( cacheIterator.hasNext() ) {
					String cacheKey = cacheIterator.next();
					if( cacheKey.contains( keySubstring ) ) {
						cacheIterator.remove();
					}
				}
			}
			synchronized ( roleQualifiersForPrincipalCache ) {
				Iterator<String> cacheIterator = roleQualifiersForPrincipalCache.keySet().iterator();
				while ( cacheIterator.hasNext() ) {
					String cacheKey = cacheIterator.next();
					if( cacheKey.contains( keySubstring ) ) {
						cacheIterator.remove();
					}
				}
			}
			synchronized ( roleMembersWithDelegationCache ) {
				Iterator<String> cacheIterator = roleMembersWithDelegationCache.keySet().iterator();
				while ( cacheIterator.hasNext() ) {
					String cacheKey = cacheIterator.next();
					if( cacheKey.contains( keySubstring ) ) {
						cacheIterator.remove();
					}
				}
			}
			synchronized ( memberPrincipalIdsCache ) {
				Iterator<String> cacheIterator = memberPrincipalIdsCache.keySet().iterator();
				while ( cacheIterator.hasNext() ) {
					String cacheKey = cacheIterator.next();
					if( cacheKey.contains( keySubstring ) ) {
						cacheIterator.remove();
					}
				}
			}
			shouldCacheRoleCache.remove(roleId);
		}
	}
	
	/**
	 * @see org.kuali.rice.kim.service.RoleService#getRoleQualifiersForPrincipalIncludingNested(java.lang.String, java.util.List, org.kuali.rice.core.util.AttributeSet)
	 */
	public List<AttributeSet> getRoleQualifiersForPrincipalIncludingNested(
			String principalId, List<String> roleIds, AttributeSet qualification) {
		return getRoleService().getRoleQualifiersForPrincipalIncludingNested(principalId, roleIds, qualification);
	}
	
	/**
	 * @see org.kuali.rice.kim.service.RoleService#getRoleQualifiersForPrincipalIncludingNested(java.lang.String, java.lang.String, java.lang.String, org.kuali.rice.core.util.AttributeSet)
	 */
	public List<AttributeSet> getRoleQualifiersForPrincipalIncludingNested(
			String principalId, String namespaceCode, String roleName,
			AttributeSet qualification) {
		return getRoleService().getRoleQualifiersForPrincipalIncludingNested(principalId, namespaceCode, roleName, qualification);
	}
	
	public void assignGroupToRole(String groupId, String namespaceCode, String roleName,
			AttributeSet qualifications) {
		getRoleUpdateService().assignGroupToRole( groupId, namespaceCode, roleName, qualifications );
		KimRoleInfo role = getRoleByName( namespaceCode, roleName );
		removeCacheEntries( role.getRoleId(), null );
	}

	public void assignPrincipalToRole(String principalId, String namespaceCode, String roleName,
			AttributeSet qualifications) {
		KimRoleInfo role = getRoleByName( namespaceCode, roleName );
		getRoleUpdateService().assignPrincipalToRole( principalId, namespaceCode, roleName, qualifications );
		removeCacheEntries( role.getRoleId(), principalId );
	}

	public void removeGroupFromRole(String groupId, String namespaceCode, String roleName,
			AttributeSet qualifications) {
		getRoleUpdateService().removeGroupFromRole( groupId, namespaceCode, roleName, qualifications );
		KimRoleInfo role = getRoleByName( namespaceCode, roleName );
		removeCacheEntries( role.getRoleId(), null );
	}

	public void removePrincipalFromRole(String principalId, String namespaceCode, String roleName,
			AttributeSet qualifications) {
		KimRoleInfo role = getRoleByName( namespaceCode, roleName );
		getRoleUpdateService().removePrincipalFromRole( principalId, namespaceCode, roleName, qualifications );
		removeCacheEntries( role.getRoleId(), principalId );
	}

	/**
	 * @see org.kuali.rice.kim.service.RoleService#getRolesSearchResults(java.util.Map)
	 */
	public List<? extends Role> getRolesSearchResults(
			Map<String, String> fieldValues) {
		return getRoleService().getRolesSearchResults(fieldValues);
	}

    protected void logPrincipalHasRoleCheck(String principalId, List<String> roleIds, AttributeSet roleQualifiers ) {
		StringBuilder sb = new StringBuilder();
		sb.append(  '\n' );
		sb.append( "Has Role     : " ).append( roleIds ).append( '\n' );
		if ( roleIds != null ) {
			for ( String roleId : roleIds ) {
				KimRoleInfo role = getRole( roleId );
				if ( role != null ) {
					sb.append( "        Name : " ).append( role.getNamespaceCode() ).append( '/').append( role.getRoleName() );
					sb.append( " (" ).append( roleId ).append( ')' );
					sb.append( '\n' );
				}
			}
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
		if (LOG.isTraceEnabled()) { 
			LOG.trace( sb.append(ExceptionUtils.getStackTrace(new Throwable())));
		} else {
			LOG.debug(sb.toString());
		}
    }

    /**
     * @see org.kuali.rice.kim.service.RoleService#principalInactivated(java.lang.String)
     */
    public void principalInactivated(String principalId) {
    	getRoleService().principalInactivated(principalId);    
    	removeCacheEntries(null, principalId);
    }
    
    /**
     * @see org.kuali.rice.kim.service.RoleService#roleInactivated(java.lang.String)
     */
    public void roleInactivated(String roleId) {
    	getRoleService().roleInactivated(roleId);    
    	removeCacheEntries(roleId, null);
    }
    
    /**
     * @see org.kuali.rice.kim.service.RoleService#roleInactivated(java.lang.String)
     */
    public void groupInactivated(String groupId) {
    	getRoleService().groupInactivated(groupId);    
    }

    public List<RoleMembershipInfo> getFirstLevelRoleMembers(List<String> roleIds){
    	return getRoleService().getFirstLevelRoleMembers(roleIds);
    }

    public List<RoleMembershipInfo> findRoleMembers(Map<String, String> fieldValues){
    	return getRoleService().findRoleMembers(fieldValues);
    }

	public void assignRoleToRole(String roleId, String namespaceCode, String roleName,
			AttributeSet qualifications) {
		getRoleUpdateService().assignRoleToRole( 
				roleId, namespaceCode, roleName, qualifications);
		KimRoleInfo role = getRoleByName( namespaceCode, roleName );
		removeCacheEntries( role.getRoleId(), null );
	}

    /**
     * @see org.kuali.rice.kim.service.RoleUpdateService#assignRoleAsDelegationMemberToRole(java.lang.String, java.lang.String, java.lang.String, org.kuali.rice.core.util.AttributeSet)
     */
	public void saveDelegationMemberForRole(String delegationMemberId, String roleMemberId, String memberId, String memberTypeCode, 
			String delegationTypeCode, String roleId, AttributeSet qualifications, 
			Date activeFromDate, Date activeToDate) throws UnsupportedOperationException{
    	getRoleUpdateService().saveDelegationMemberForRole(delegationMemberId, roleMemberId, memberId, memberTypeCode, delegationTypeCode, roleId, qualifications, activeFromDate, activeToDate);
		KimRoleInfo role = getRole( roleId );
		removeCacheEntries( role.getRoleId(), null );
    }

    public RoleMemberCompleteInfo saveRoleMemberForRole(String roleMemberId, String memberId, String memberTypeCode, 
    		String roleId, AttributeSet qualifications, Date activeFromDate, Date activeToDate) throws UnsupportedOperationException{
		KimRoleInfo role = getRole( roleId );
		RoleMemberCompleteInfo roleMemberCompleteInfo = getRoleUpdateService().saveRoleMemberForRole(roleMemberId, memberId, memberTypeCode, roleId, qualifications, activeFromDate, activeToDate);
		removeCacheEntries( role.getRoleId(), memberId );
		return roleMemberCompleteInfo;
    }
    
	public void removeRoleFromRole(String roleId, String namespaceCode, String roleName,
			AttributeSet qualifications) {
		getRoleUpdateService().removeRoleFromRole( roleId, namespaceCode, roleName, qualifications );
		KimRoleInfo role = getRoleByName( namespaceCode, roleName );
		removeCacheEntries( role.getRoleId(), null );
	}

    public List<RoleMemberCompleteInfo> findRoleMembersCompleteInfo(Map<String, String> fieldValues){
    	return getRoleService().findRoleMembersCompleteInfo(fieldValues);
    }
    
    public List<DelegateMemberCompleteInfo> findDelegateMembersCompleteInfo(Map<String, String> fieldValues){
    	return getRoleService().findDelegateMembersCompleteInfo(fieldValues);
    }
	
    public List<DelegateMemberCompleteInfo> getDelegationMembersByDelegationId(@WebParam(name="delegationId") String delegationId){
		return getRoleService().getDelegationMembersByDelegationId(delegationId);
	}
	
	public DelegateMemberCompleteInfo getDelegationMemberByDelegationAndMemberId(@WebParam(name="delegationId") String delegationId, @WebParam(name="memberId") String memberId){
		return getRoleService().getDelegationMemberByDelegationAndMemberId(delegationId, memberId);
	}
	
	public DelegateMemberCompleteInfo getDelegationMemberById(@WebParam(name="delegationMemberId") String delegationMemberId){
		return getRoleService().getDelegationMemberById(delegationMemberId);	
	}

	public List<RoleResponsibilityActionInfo> getRoleMemberResponsibilityActionInfo(String roleMemberId){
		return getRoleService().getRoleMemberResponsibilityActionInfo(roleMemberId);
	}
	
	public DelegateTypeInfo getDelegateTypeInfo(String roleId, String delegationTypeCode){
		return getRoleService().getDelegateTypeInfo(roleId, delegationTypeCode);
	}

	public DelegateTypeInfo getDelegateTypeInfoById(String delegationId){
		return getRoleService().getDelegateTypeInfoById(delegationId);
	}
	
	public void saveRoleRspActions(String roleResponsibilityActionId, String roleId, String roleResponsibilityId, String roleMemberId, 
			String actionTypeCode, String actionPolicyCode, Integer priorityNumber, Boolean forceAction){
    	getRoleUpdateService().saveRoleRspActions(roleResponsibilityActionId, roleId, roleResponsibilityId, roleMemberId, actionTypeCode, actionPolicyCode, priorityNumber, forceAction);
		removeCacheEntries(roleId, null);
	}

	public List<RoleResponsibilityInfo> getRoleResponsibilities(String roleId){
		return getRoleService().getRoleResponsibilities(roleId);
	}
	
	public void applicationRoleMembershipChanged(String roleId) {
		removeCacheEntries(roleId, null);
		getRoleService().applicationRoleMembershipChanged(roleId);		
	}
	
	// Spring and injection methods
	
	public RoleService getRoleService() {
		if ( roleService == null ) {
			roleService = KIMServiceLocator.getRoleService();
		}
		return roleService;
	}
	
	public KimTypeInfoService getTypeInfoService() {
		if (typeInfoService == null) {
			typeInfoService = KimApiServiceLocator.getKimTypeInfoService();
		}
		return typeInfoService;
	}

	public RoleUpdateService getRoleUpdateService() {
		try {
			if ( roleUpdateService == null ) {
				roleUpdateService = KIMServiceLocatorInternal.getRoleUpdateService();
				if ( roleUpdateService == null ) {
					throw new UnsupportedOperationException( "null returned for RoleUpdateService, unable to update role data");
				}
			}
		} catch ( Exception ex ) {
			throw new UnsupportedOperationException( "unable to obtain a RoleUpdateService, unable to update role data", ex);
		}
		return roleUpdateService;
	}
	
	public void setRoleCacheMaxSize(int roleCacheMaxSize) {
		this.roleCacheMaxSize = roleCacheMaxSize;
	}

	public void setRoleCacheMaxAgeSeconds(int roleCacheMaxAge) {
		this.roleCacheMaxAgeSeconds = roleCacheMaxAge;
	}
	
	/**
	 * This overridden method looks up roles based on criteria.  If you want
	 * to return all roles pass in an empty map.
	 *
	 * @see org.kuali.rice.kim.service.RoleService#lookupRoles(java.util.Map)
	 */
	public List<KimRoleInfo> lookupRoles(Map<String, String> searchCriteria) {
		return getRoleService().lookupRoles(searchCriteria);
	}

	public void flushInternalRoleCache() {
		getRoleService().flushInternalRoleCache();
	}
	
	public void flushInternalRoleMemberCache() {
		getRoleService().flushInternalRoleMemberCache();
	}
	
	public void flushInternalDelegationCache() {
		getRoleService().flushInternalDelegationCache();
	}
	
	public void flushInternalDelegationMemberCache() {
		getRoleService().flushInternalDelegationMemberCache();
	}

    public void assignPermissionToRole(String permissionId, String roleId) throws UnsupportedOperationException {
        getRoleUpdateService().assignPermissionToRole(permissionId, roleId);
    }

    public String getNextAvailableRoleId() throws UnsupportedOperationException {
        return getRoleUpdateService().getNextAvailableRoleId();
    }

    public void saveRole(String roleId, String roleName, String roleDescription, boolean active, String kimTypeId, String namespaceCode) throws UnsupportedOperationException {
        getRoleUpdateService().saveRole(roleId, roleName, roleDescription, active, kimTypeId, namespaceCode);
    }

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.RoleService#getMemberParentRoleIds(java.lang.String, java.lang.String)
	 */
	public List<String> getMemberParentRoleIds(String memberType,
			String memberId) {
		return getRoleService().getMemberParentRoleIds(memberType, memberId);
	}
}
