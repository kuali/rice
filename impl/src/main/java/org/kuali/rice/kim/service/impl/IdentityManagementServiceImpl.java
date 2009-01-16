package org.kuali.rice.kim.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.core.util.MaxSizeMap;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.role.KimResponsibility;
import org.kuali.rice.kim.bo.role.dto.ResponsibilityActionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.AuthenticationService;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.springframework.beans.factory.InitializingBean;

public class IdentityManagementServiceImpl implements IdentityManagementService, InitializingBean {
	
	protected AuthenticationService authenticationService; 
//	protected AuthorizationService authorizationService; 
	protected PermissionService permissionService; 
	protected ResponsibilityService responsibilityService;  
	protected IdentityService identityService;
	protected GroupService groupService;
	
	// Max age defined in seconds
	protected int entityPrincipalCacheMaxSize = 200;
	protected int entityPrincipalCacheMaxAge = 30;
	protected int groupCacheMaxSize = 200;
	protected int groupCacheMaxAge = 30;
	protected int permissionCacheMaxSize = 200;
	protected int permissionCacheMaxAge = 30;
	protected int responsibilityCacheMaxSize = 200;
	protected int responsibilityCacheMaxAge = 30;
	
	protected MaxSizeMap<String,MaxAgeSoftReference<KimEntity>> entityByIdCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<KimEntity>> entityByPrincipalNameCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<KimPrincipal>> principalByIdCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<KimPrincipal>> principalByNameCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<GroupInfo>> groupByIdCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<GroupInfo>> groupByNameCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<List<String>>> groupIdsForPrincipalCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<List<? extends KimGroup>>> groupsForPrincipalCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<Boolean>> isMemberOfGroupCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<Boolean>> isGroupMemberOfGroupCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<List<String>>> groupMemberPrincipalIdsCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<Boolean>> hasPermissionCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<Boolean>> hasPermissionByTemplateCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<Boolean>> isAuthorizedCache;
	protected MaxSizeMap<String,MaxAgeSoftReference<Boolean>> isAuthorizedByTemplateNameCache;
    protected MaxSizeMap<String,MaxAgeSoftReference<Boolean>> isPermissionDefinedForTemplateNameCache;
	
	public void afterPropertiesSet() throws Exception {
		entityByIdCache = new MaxSizeMap<String,MaxAgeSoftReference<KimEntity>>( entityPrincipalCacheMaxSize );
		entityByPrincipalNameCache = new MaxSizeMap<String,MaxAgeSoftReference<KimEntity>>( entityPrincipalCacheMaxSize );
		principalByIdCache = new MaxSizeMap<String,MaxAgeSoftReference<KimPrincipal>>( entityPrincipalCacheMaxSize );
		principalByNameCache = new MaxSizeMap<String,MaxAgeSoftReference<KimPrincipal>>( entityPrincipalCacheMaxSize );
		groupByIdCache = new MaxSizeMap<String,MaxAgeSoftReference<GroupInfo>>( groupCacheMaxSize );
		groupByNameCache = new MaxSizeMap<String,MaxAgeSoftReference<GroupInfo>>( groupCacheMaxSize );
		groupIdsForPrincipalCache = new MaxSizeMap<String,MaxAgeSoftReference<List<String>>>( groupCacheMaxSize );
		groupsForPrincipalCache = new MaxSizeMap<String,MaxAgeSoftReference<List<? extends KimGroup>>>( groupCacheMaxSize );
		isMemberOfGroupCache = new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( groupCacheMaxSize );
		groupMemberPrincipalIdsCache = new MaxSizeMap<String,MaxAgeSoftReference<List<String>>>( groupCacheMaxSize );
		hasPermissionCache = new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( permissionCacheMaxSize );
		hasPermissionByTemplateCache = new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( permissionCacheMaxSize );
		isPermissionDefinedForTemplateNameCache = new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( permissionCacheMaxSize );
		isAuthorizedByTemplateNameCache = new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( permissionCacheMaxSize );
		isAuthorizedCache = new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( permissionCacheMaxSize );
	}

	public void flushAllCaches() {
		flushEntityPrincipalCaches();
		flushGroupCaches();
		flushPermissionCaches();
		flushResponsibilityCaches();
	}
	
	public void flushEntityPrincipalCaches() {
		entityByIdCache.clear();
		entityByPrincipalNameCache.clear();
		principalByIdCache.clear();
		principalByNameCache.clear();
	}
	
	public void flushGroupCaches() {
		groupByIdCache.clear();
		groupByNameCache.clear();
		groupIdsForPrincipalCache.clear();
		groupsForPrincipalCache.clear();
		isMemberOfGroupCache.clear();
		groupMemberPrincipalIdsCache.clear();
	}
	
	public void flushPermissionCaches() {
		hasPermissionCache.clear();
		hasPermissionByTemplateCache.clear();
		isPermissionDefinedForTemplateNameCache.clear();
		isAuthorizedByTemplateNameCache.clear();
		isAuthorizedCache.clear();
	}

	public void flushResponsibilityCaches() {
		// nothing currently being cached
	}
	
	protected KimEntity getEntityByIdCache( String entityId ) {
		MaxAgeSoftReference<KimEntity> entityRef = entityByIdCache.get( entityId );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}

	protected KimEntity getEntityByPrincipalNameCache( String entityName ) {
		MaxAgeSoftReference<KimEntity> entityRef = entityByPrincipalNameCache.get( entityName );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}
	
	protected KimPrincipal getPrincipalByIdCache( String principalId ) {
		MaxAgeSoftReference<KimPrincipal> principalRef = principalByIdCache.get( principalId );
		if ( principalRef != null ) {
			return principalRef.get();
		}
		return null;
	}

	protected KimPrincipal getPrincipalByNameCache( String principalName ) {
		MaxAgeSoftReference<KimPrincipal> principalRef = principalByNameCache.get( principalName );
		if ( principalRef != null ) {
			return principalRef.get();
		}
		return null;
	}

	public List<KimEntity> lookupEntitys(Map<String,String> searchCriteria) {
		return identityService.lookupEntitys( searchCriteria );
	}
	
	protected GroupInfo getGroupByIdCache( String groupId ) {
		MaxAgeSoftReference<GroupInfo> groupRef = groupByIdCache.get( groupId );
		if ( groupRef != null ) {
			return groupRef.get();
		}
		return null;
	}

	protected GroupInfo getGroupByNameCache( String groupName ) {
		MaxAgeSoftReference<GroupInfo> groupRef = groupByNameCache.get( groupName );
		if ( groupRef != null ) {
			return groupRef.get();
		}
		return null;
	}

	protected List<String> getGroupIdsForPrincipalCache( String principalId ) {
		MaxAgeSoftReference<List<String>> groupIdsRef = groupIdsForPrincipalCache.get( principalId );
		if ( groupIdsRef != null ) {
			return groupIdsRef.get();
		}
		return null;
	}
	
	protected List<? extends KimGroup> getGroupsForPrincipalCache( String principalId ) {
		MaxAgeSoftReference<List<? extends KimGroup>> groupsRef = groupsForPrincipalCache.get( principalId );
		if ( groupsRef != null ) {
			return groupsRef.get();
		}
		return null;
	}

	protected Boolean getIsMemberOfGroupCache( String principalId, String groupId ) {
		MaxAgeSoftReference<Boolean> isMemberRef = isMemberOfGroupCache.get( principalId + "-" + groupId );
		if ( isMemberRef != null ) {
			return isMemberRef.get();
		}
		return null;
	}

	protected Boolean getIsGroupMemberOfGroupCache( String potentialMemberId, String potentialParentId ) 
	{
		MaxAgeSoftReference<Boolean> isMemberRef = isGroupMemberOfGroupCache.get( potentialMemberId + "-" + potentialParentId );
		if ( isMemberRef != null ) {
			return isMemberRef.get();
		}
		return null;
	}
		protected List<String> getGroupMemberPrincipalIdsCache( String groupId ) {
		MaxAgeSoftReference<List<String>> memberIdsRef = groupMemberPrincipalIdsCache.get( groupId );
		if ( memberIdsRef != null ) {
			return memberIdsRef.get();
		}
		return null;
	}
	
	protected Boolean getHasPermissionCache( String key ) {
		MaxAgeSoftReference<Boolean> hasPermissionRef = hasPermissionCache.get( key );
		if ( hasPermissionRef != null ) {
			return hasPermissionRef.get(); 
		}
		return null;
	}
	
	protected Boolean getHasPermissionByTemplateCache( String key ) {
		MaxAgeSoftReference<Boolean> hasPermissionRef = hasPermissionByTemplateCache.get( key );
		if ( hasPermissionRef != null ) {
			return hasPermissionRef.get();
		}
		return null;
	}

	protected Boolean getIsAuthorizedByTemplateNameFromCache( String key ) {
		MaxAgeSoftReference<Boolean> cacheEntryRef = isAuthorizedByTemplateNameCache.get( key );
		if ( cacheEntryRef != null ) {
			return cacheEntryRef.get();
		}
		return null;
	}

	protected Boolean getIsAuthorizedFromCache( String key ) {
		MaxAgeSoftReference<Boolean> cacheEntryRef = isAuthorizedCache.get( key );
		if ( cacheEntryRef != null ) {
			return cacheEntryRef.get();
		}
		return null;
	}
	
	protected void addEntityToCache( KimEntity entity ) {
		if ( entity != null ) {
			entityByPrincipalNameCache.put( entity.getPrincipals().get(0).getPrincipalName(), new MaxAgeSoftReference<KimEntity>( entityPrincipalCacheMaxAge, entity ) );
			entityByIdCache.put( entity.getEntityId(), new MaxAgeSoftReference<KimEntity>( entityPrincipalCacheMaxAge, entity ) );
		}
	}
	
	protected void addPrincipalToCache( KimPrincipal principal ) {
		if ( principal != null ) {
			principalByNameCache.put( principal.getPrincipalName(), new MaxAgeSoftReference<KimPrincipal>( entityPrincipalCacheMaxAge, principal ) );
			principalByIdCache.put( principal.getPrincipalId(), new MaxAgeSoftReference<KimPrincipal>( entityPrincipalCacheMaxAge, principal ) );
		}
	}
	
	protected void addGroupToCache( GroupInfo group ) {
		if ( group != null ) {
			groupByNameCache.put( group.getGroupName(), new MaxAgeSoftReference<GroupInfo>( groupCacheMaxAge, group ) );
			groupByIdCache.put( group.getGroupId(), new MaxAgeSoftReference<GroupInfo>( groupCacheMaxAge, group ) );
		}
	}

	protected void addGroupIdsForPrincipalToCache( String principalId, List<String> ids ) {
		if ( ids != null ) {
			groupIdsForPrincipalCache.put( principalId, new MaxAgeSoftReference<List<String>>( groupCacheMaxAge, ids ) );
		}
	}

	protected void addGroupsForPrincipalToCache( String principalId, List<? extends KimGroup> groups ) {
		if ( groups != null ) {
			groupsForPrincipalCache.put( principalId, new MaxAgeSoftReference<List<? extends KimGroup>>( groupCacheMaxAge, groups ) );
			List<String> groupIds = new ArrayList<String>( groups.size() );
			for ( KimGroup group : groups ) {
				groupIds.add( group.getGroupId() );
			}
			addGroupIdsForPrincipalToCache( principalId, groupIds );
		}
	}
	
	protected void addIsMemberOfGroupToCache( String principalId, String groupId, boolean member ) {
		isMemberOfGroupCache.put( principalId + "-" + groupId, new MaxAgeSoftReference<Boolean>( groupCacheMaxAge, member ) );
	}
	
	protected void addIsGroupMemberOfGroupToCache( String potentialMemberId, String potentialParentId, boolean member ) 
	{
        isMemberOfGroupCache.put( potentialMemberId + "-" + potentialParentId, new MaxAgeSoftReference<Boolean>( groupCacheMaxAge, member ) );
    }
	
	protected void addGroupMemberPrincipalIdsToCache( String groupId, List<String> ids ) {
		if ( ids != null ) {
			groupMemberPrincipalIdsCache.put( groupId, new MaxAgeSoftReference<List<String>>( groupCacheMaxAge, ids ) );
		}
	}

	protected void addHasPermissionToCache( String key, boolean hasPerm ) {
		hasPermissionCache.put( key, new MaxAgeSoftReference<Boolean>( permissionCacheMaxAge, hasPerm ) );
	}

	protected void addHasPermissionByTemplateToCache( String key, boolean hasPerm ) {
		hasPermissionByTemplateCache.put( key, new MaxAgeSoftReference<Boolean>( permissionCacheMaxAge, hasPerm ) );
	}

	protected void addIsAuthorizedByTemplateNameToCache( String key, boolean authorized ) {
		isAuthorizedByTemplateNameCache.put( key, new MaxAgeSoftReference<Boolean>( permissionCacheMaxAge, authorized ) );
	}

	protected void addIsAuthorizedToCache( String key, boolean authorized ) {
		isAuthorizedCache.put( key, new MaxAgeSoftReference<Boolean>( permissionCacheMaxAge, authorized ) );
	}
	
	// AUTHENTICATION SERVICE
	
	public String getAuthenticatedPrincipalName(HttpServletRequest request) {
		return getAuthenticationService().getPrincipalName(request);
	}

    public boolean authenticationServiceValidatesPassword() {
    	return getAuthenticationService().validatePassword();
    }
    
    // AUTHORIZATION SERVICE
    
    public boolean hasPermission(String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails) {
    	StringBuffer cacheKey = new StringBuffer();
    	cacheKey.append( principalId ).append(  '/' );
    	cacheKey.append( namespaceCode ).append( '-' ).append( permissionName ).append( '/' );
    	addAttributeSetToKey( permissionDetails, cacheKey );
    	Boolean hasPerm = getHasPermissionCache(cacheKey.toString());
		if (hasPerm == null) {
			hasPerm = getPermissionService().hasPermission( principalId, namespaceCode, permissionName, permissionDetails );
	    	addHasPermissionToCache(cacheKey.toString(), hasPerm);
		}
    	return hasPerm;        	
    }
    
    public boolean isAuthorized(String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	if ( qualification == null ) {
    		return hasPermission( principalId, namespaceCode, permissionName, permissionDetails );
    	}
    	StringBuffer cacheKey = new StringBuffer();
    	cacheKey.append( principalId ).append(  '/' );
    	cacheKey.append( namespaceCode ).append( '-' ).append( permissionName ).append( '/' );
    	addAttributeSetToKey( permissionDetails, cacheKey );
    	cacheKey.append( '/' );
    	addAttributeSetToKey( qualification, cacheKey );
    	Boolean isAuthorized = getIsAuthorizedFromCache( cacheKey.toString() );
    	if ( isAuthorized == null ) {
    		isAuthorized = getPermissionService().isAuthorized( principalId, namespaceCode, permissionName, permissionDetails, qualification );
    		addIsAuthorizedToCache( cacheKey.toString(), isAuthorized );
    	}
    	return isAuthorized;
    }

    public boolean hasPermissionByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails) {
    	StringBuffer cacheKey = new StringBuffer();
    	cacheKey.append( principalId ).append(  '/' );
    	cacheKey.append( namespaceCode ).append( '-' ).append( permissionTemplateName ).append( '/' );
    	addAttributeSetToKey( permissionDetails, cacheKey );
    	Boolean hasPerm = getHasPermissionByTemplateCache(cacheKey.toString());
		if (hasPerm == null) {
			hasPerm = getPermissionService().hasPermissionByTemplateName( principalId, namespaceCode, permissionTemplateName, permissionDetails );
	    	addHasPermissionByTemplateToCache(cacheKey.toString(), hasPerm);
		}
    	return hasPerm;        	
    }
    
    public boolean isAuthorizedByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	if ( qualification == null ) {
    		return hasPermissionByTemplateName( principalId, namespaceCode, permissionTemplateName, permissionDetails );
    	}
    	StringBuffer cacheKey = new StringBuffer();
    	cacheKey.append( principalId ).append(  '/' );
    	cacheKey.append( namespaceCode ).append( '-' ).append( permissionTemplateName ).append( '/' );
    	addAttributeSetToKey( permissionDetails, cacheKey );
    	cacheKey.append( '/' );
    	addAttributeSetToKey( qualification, cacheKey );
    	Boolean isAuthorized = getIsAuthorizedByTemplateNameFromCache( cacheKey.toString() );
    	if ( isAuthorized == null ) {
    		isAuthorized = getPermissionService().isAuthorizedByTemplateName( principalId, namespaceCode, permissionTemplateName, permissionDetails, qualification );
    		addIsAuthorizedByTemplateNameToCache( cacheKey.toString(), isAuthorized );
    	}
    	return isAuthorized;
    }

	private void addAttributeSetToKey(AttributeSet attributes, StringBuffer key) {
		if ( attributes != null ) {
			for ( Map.Entry<String, String> entry : attributes.entrySet() ) {
				key.append( entry.getKey() ).append( '=' ).append( entry.getValue() ).append('|');
	    	}
		} else {
			key.append( "[null]" );
		}
	}
	
    /**
     * @see org.kuali.rice.kim.service.IdentityManagementService#getAuthorizedPermissions(java.lang.String, String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    public List<? extends KimPermission> getAuthorizedPermissions(String principalId,
    		String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification) {
    	return getPermissionService().getAuthorizedPermissions( principalId, namespaceCode, permissionName, permissionDetails, qualification );
    }

    public List<? extends KimPermission> getAuthorizedPermissionsByTemplateName(String principalId,
    		String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification) {
    	return getPermissionService().getAuthorizedPermissionsByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails, qualification);
    }
    
    public List<AttributeSet> getRoleQualifiersByPermissionName( String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	return getPermissionService().getRoleQualifiersByPermissionName(principalId, namespaceCode, permissionName, permissionDetails, qualification);
    }

    public List<AttributeSet> getRoleQualifiersByTemplateName( String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	return getPermissionService().getRoleQualifiersByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails, qualification);
    }
    
    public boolean isPermissionDefinedForTemplateName(String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails) {
    	StringBuffer key = new StringBuffer();
    	key.append( namespaceCode ).append( '-' ).append( permissionTemplateName ).append( '/' );
        addAttributeSetToKey(permissionDetails, key);
        MaxAgeSoftReference<Boolean> resultEntry = isPermissionDefinedForTemplateNameCache.get(key.toString());
        if ( resultEntry != null ) {
            Boolean result = resultEntry.get();
            if ( result != null ) {
                return result;
            }
        }
        boolean result = getPermissionService().isPermissionDefinedForTemplateName(namespaceCode, permissionTemplateName, permissionDetails);
        isPermissionDefinedForTemplateNameCache.put(key.toString(),new MaxAgeSoftReference<Boolean>( permissionCacheMaxAge, result ));
        return result; 
    }
    
    // GROUP SERVICE
    
	public boolean isMemberOfGroup(String principalId, String groupId) {
    	Boolean isMember = getIsMemberOfGroupCache(principalId, groupId);
		if (isMember != null) {
			return isMember;
		}
		isMember = getGroupService().isMemberOfGroup(principalId, groupId);
    	addIsMemberOfGroupToCache(principalId, groupId, isMember);
    	return isMember;    	
	}

	public boolean isMemberOfGroup(String principalId, String namespaceCode, String groupName) {
		KimGroup group = getGroupByName(namespaceCode, groupName);
		if ( group == null ) {
			return false;
		}
		return isMemberOfGroup(principalId, group.getGroupId());
    }

	public boolean isGroupMemberOfGroup(String potentialMemberId, String potentialParentId)
	{
	    Boolean isMember = getIsGroupMemberOfGroupCache(potentialMemberId, potentialParentId);
	    if(isMember != null)
	    {
	        return isMember;
	    }
	    else
        {
	        isMember = getGroupService()
	                .isGroupMemberOfGroup(potentialMemberId, potentialParentId);
        }
	    addIsGroupMemberOfGroupToCache(potentialMemberId, potentialParentId, isMember);
	    return isMember;
	}
	public List<String> getGroupMemberPrincipalIds(String groupId) {
    	List<String> ids = getGroupMemberPrincipalIdsCache(groupId);
		if (ids != null) {
			return ids;
		}
		ids = getGroupService().getMemberPrincipalIds(groupId);
    	addGroupMemberPrincipalIdsToCache(groupId, ids);
    	return ids;    		
	}

	public List<String> getDirectGroupMemberPrincipalIds(String groupId) {
		return getGroupService().getDirectMemberPrincipalIds(groupId);
	}

    public List<String> getGroupIdsForPrincipal(String principalId) {
    	List<String> ids = getGroupIdsForPrincipalCache(principalId);
		if (ids != null) {
			return ids;
		}
		ids = getGroupService().getGroupIdsForPrincipal(principalId);
    	addGroupIdsForPrincipalToCache(principalId, ids);
    	return ids;    	
	}

    public List<String> getGroupIdsForPrincipal(String principalId, String namespaceCode ) {
		return getGroupService().getGroupIdsForPrincipalByNamespace(principalId, namespaceCode );
	}

    public List<? extends KimGroup> getGroupsForPrincipal(String principalId) {
    	List<? extends KimGroup> groups = getGroupsForPrincipalCache(principalId);
		if (groups != null) {
			return groups;
		}
		groups = getGroupService().getGroupsForPrincipal(principalId);
    	addGroupsForPrincipalToCache(principalId, groups);
    	return groups;    	
	}

    public List<? extends KimGroup> getGroupsForPrincipal(String principalId, String namespaceCode ) {
    	List<? extends KimGroup> groups = getGroupsForPrincipalCache(principalId + "-" + namespaceCode);
		if (groups != null) {
			return groups;
		}
		groups = getGroupService().getGroupsForPrincipalByNamespace(principalId, namespaceCode );
    	addGroupsForPrincipalToCache(principalId, groups);
    	return groups;    	
	}
    
    public List<String> getMemberGroupIds(String groupId) {
		return getGroupService().getMemberGroupIds(groupId);
	}

    public List<String> getDirectMemberGroupIds(String groupId) {
		return getGroupService().getDirectMemberGroupIds(groupId);
	}

    public GroupInfo getGroup(String groupId) {
    	GroupInfo group = getGroupByIdCache(groupId);
		if (group != null) {
			return group;
		}
		group = getGroupService().getGroupInfo(groupId);
    	addGroupToCache(group);
    	return group;
	}
    
    public GroupInfo getGroupByName(String namespaceCode, String groupName) {
    	GroupInfo group = getGroupByNameCache(namespaceCode + "-" + groupName);
		if (group != null) {
			return group;
		}
		group = getGroupService().getGroupInfoByName( namespaceCode, groupName );
    	addGroupToCache(group);
    	return group;    	
    }
    
    public List<String> getParentGroupIds(String groupId) {
		return getGroupService().getParentGroupIds( groupId );
	}

    public List<String> getDirectParentGroupIds(String groupId) {
		return getGroupService().getDirectParentGroupIds( groupId );
	}
    
    protected void clearGroupCachesForPrincipalAndGroup( String principalId, String groupId ) {
    	if ( principalId != null ) {
	    	groupIdsForPrincipalCache.remove(principalId);
	    	groupsForPrincipalCache.remove(principalId);
	    	isMemberOfGroupCache.remove(principalId + "-" + groupId);
    	} else {
    		// added or removed a group - perform a more extensive purge
    		Iterator<String> keys = isMemberOfGroupCache.keySet().iterator();
    		while ( keys.hasNext() ) {
    			String key = keys.next();
    			if ( key.endsWith("-"+groupId) ) {
    				keys.remove();
    			}
    		}
    		// NOTE: There's no good way to selectively purge the other two group caches or the permission caches which could be
    		// affected - is this necessary or do we just wait for the cache items to expire    		
    	}
    	groupMemberPrincipalIdsCache.remove(groupId);
    }
    
    
    public boolean addGroupToGroup(String childId, String parentId) {
    	clearGroupCachesForPrincipalAndGroup(null, parentId);
        return getGroupService().addGroupToGroup(childId, parentId);
    }

    public boolean addPrincipalToGroup(String principalId, String groupId) {
    	clearGroupCachesForPrincipalAndGroup(principalId, groupId);
        return getGroupService().addPrincipalToGroup(principalId, groupId);
    }

    public boolean removeGroupFromGroup(String childId, String parentId) {
    	clearGroupCachesForPrincipalAndGroup(null, parentId);
        return getGroupService().removeGroupFromGroup(childId, parentId);
    }

    public boolean removePrincipalFromGroup(String principalId, String groupId) {
    	clearGroupCachesForPrincipalAndGroup(principalId, groupId);
        return getGroupService().removePrincipalFromGroup(principalId, groupId);
    }


    
    // IDENTITY SERVICE
    
    
    public KimEntity getEntityByPrincipalName(String principalName) {
		KimEntity entity = getEntityByPrincipalNameCache(principalName);
		if (entity != null) {
			return entity;
		}
    	entity = getIdentityService().getEntityByPrincipalName(principalName);
    	addEntityToCache(entity);
    	return entity;
	}

    public KimPrincipal getPrincipal(String principalId) {
    	KimPrincipal principal = getPrincipalByIdCache(principalId);
		if (principal != null) {
			return principal;
		}
		principal = getIdentityService().getPrincipal(principalId);
    	addPrincipalToCache(principal);
    	return principal;
	}
    
    public KimPrincipal getPrincipalByPrincipalName(String principalName) {
    	KimPrincipal principal = getPrincipalByNameCache(principalName);
		if (principal != null) {
			return principal;
		}
		principal = getIdentityService().getPrincipalByPrincipalName(principalName);
    	addPrincipalToCache(principal);
    	return principal;
    }
	
	public KimEntity getEntity(String entityId) {
		KimEntity entity = getEntityByIdCache(entityId);
		if (entity != null) {
			return entity;
		}
    	entity = getIdentityService().getEntity(entityId);
    	addEntityToCache(entity);
    	return entity;
	}

	
	
	// OTHER METHODS
	
	
	
	public AuthenticationService getAuthenticationService() {
		if ( authenticationService == null ) {
			authenticationService = KIMServiceLocator.getAuthenticationService();
		}
		return authenticationService;
	}
	public IdentityService getIdentityService() {
		if ( identityService == null ) {
			identityService = KIMServiceLocator.getIdentityService();
		}
		return identityService;
	}

	public GroupService getGroupService() {
		if ( groupService == null ) {
			groupService = KIMServiceLocator.getGroupService();
		}
		return groupService;
	}

//	public AuthorizationService getAuthorizationService() {
//		if ( authorizationService == null ) {
//			authorizationService = KIMServiceLocator.getAuthorizationService();
//		}
//		return authorizationService;
//	}

	public PermissionService getPermissionService() {
		if ( permissionService == null ) {
			permissionService = KIMServiceLocator.getPermissionService();
		}
		return permissionService;
	}

	public ResponsibilityService getResponsibilityService() {
		if ( responsibilityService == null ) {
			responsibilityService = KIMServiceLocator.getResponsibilityService();
		}
		return responsibilityService;
	}
	
    // ----------------------
    // Responsibility Methods
    // ----------------------

	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#getResponsibility(java.lang.String)
	 */
	public KimResponsibility getResponsibility(String responsibilityId) {
		return getResponsibilityService().getResponsibility( responsibilityId );
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#hasResponsibility(java.lang.String, String, java.lang.String, AttributeSet, AttributeSet)
	 */
	public boolean hasResponsibility(String principalId, String namespaceCode,
			String responsibilityName, AttributeSet qualification,
			AttributeSet responsibilityDetails) {
		return getResponsibilityService().hasResponsibility( principalId, namespaceCode, responsibilityName, qualification, responsibilityDetails );
	}

	public List<? extends KimResponsibility> getResponsibilitiesByName( String namespaceCode, String responsibilityName) {
		return getResponsibilityService().getResponsibilitiesByName( namespaceCode, responsibilityName );
	}
	
	public List<ResponsibilityActionInfo> getResponsibilityActions( String namespaceCode, String responsibilityName,
    		AttributeSet qualification, AttributeSet responsibilityDetails) {
		return getResponsibilityService().getResponsibilityActions( namespaceCode, responsibilityName, qualification, responsibilityDetails );
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.IdentityManagementService#getResponsibilityActionsByTemplateName(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public List<ResponsibilityActionInfo> getResponsibilityActionsByTemplateName(
			String namespaceCode, String responsibilityTemplateName,
			AttributeSet qualification, AttributeSet responsibilityDetails) {
		return getResponsibilityService().getResponsibilityActionsByTemplateName(namespaceCode, responsibilityTemplateName, qualification, responsibilityDetails);
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.IdentityManagementService#hasResponsibilityByTemplateName(java.lang.String, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public boolean hasResponsibilityByTemplateName(String principalId,
			String namespaceCode, String responsibilityTemplateName,
			AttributeSet qualification, AttributeSet responsibilityDetails) {
		return getResponsibilityService().hasResponsibilityByTemplateName(principalId, namespaceCode, responsibilityTemplateName, qualification, responsibilityDetails);
	}

	public void setEntityPrincipalCacheMaxSize(int entityPrincipalCacheMaxSize) {
		this.entityPrincipalCacheMaxSize = entityPrincipalCacheMaxSize;
	}

	public void setEntityPrincipalCacheMaxAge(int entityPrincipalCacheMaxAge) {
		this.entityPrincipalCacheMaxAge = entityPrincipalCacheMaxAge;
	}

	public void setGroupCacheMaxSize(int groupCacheMaxSize) {
		this.groupCacheMaxSize = groupCacheMaxSize;
	}

	public void setGroupCacheMaxAge(int groupCacheMaxAge) {
		this.groupCacheMaxAge = groupCacheMaxAge;
	}

	public void setPermissionCacheMaxSize(int permissionCacheMaxSize) {
		this.permissionCacheMaxSize = permissionCacheMaxSize;
	}

	public void setPermissionCacheMaxAge(int permissionCacheMaxAge) {
		this.permissionCacheMaxAge = permissionCacheMaxAge;
	}

	public void setResponsibilityCacheMaxSize(int responsibilityCacheMaxSize) {
		this.responsibilityCacheMaxSize = responsibilityCacheMaxSize;
	}

	public void setResponsibilityCacheMaxAge(int responsibilityCacheMaxAge) {
		this.responsibilityCacheMaxAge = responsibilityCacheMaxAge;
	}
	
}
