package org.kuali.rice.kim.service.impl;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

public class IdentityManagementServiceImpl implements IdentityManagementService {
	
	protected AuthenticationService authenticationService; 
//	protected AuthorizationService authorizationService; 
	protected PermissionService permissionService; 
	protected ResponsibilityService responsibilityService;  
	protected IdentityService identityService;
	protected GroupService groupService;
	
	protected Map<String,SoftReference<KimEntity>> entityByIdCache = new HashMap<String,SoftReference<KimEntity>>( 100 );
	protected Map<String,SoftReference<KimEntity>> entityByPrincipalNameCache = new HashMap<String,SoftReference<KimEntity>>( 100 );
	protected Map<String,SoftReference<KimPrincipal>> principalByIdCache = new HashMap<String,SoftReference<KimPrincipal>>( 100 );
	protected Map<String,SoftReference<KimPrincipal>> principalByNameCache = new HashMap<String,SoftReference<KimPrincipal>>( 100 );
	protected Map<String,SoftReference<GroupInfo>> groupByIdCache = new HashMap<String,SoftReference<GroupInfo>>( 100 );
	protected Map<String,SoftReference<GroupInfo>> groupByNameCache = new HashMap<String,SoftReference<GroupInfo>>( 100 );
	protected Map<String,SoftReference<List<String>>> groupIdsForPrincipalCache = new HashMap<String,SoftReference<List<String>>>( 100 );
	protected Map<String,SoftReference<List<? extends KimGroup>>> groupsForPrincipalCache = new HashMap<String,SoftReference<List<? extends KimGroup>>>( 100 );
	protected Map<String,SoftReference<Boolean>> isMemberOfGroupCache = new HashMap<String,SoftReference<Boolean>>( 100 );
	protected Map<String,SoftReference<Boolean>> isMemberOfGroupByNameCache = new HashMap<String,SoftReference<Boolean>>( 100 );
	protected Map<String,SoftReference<List<String>>> groupMemberPrincipalIdsCache = new HashMap<String,SoftReference<List<String>>>( 100 );
	protected Map<String,SoftReference<Boolean>> hasPermissionCache = new HashMap<String,SoftReference<Boolean>>( 100 );
	protected Map<String,SoftReference<Boolean>> hasPermissionByTemplateCache = new HashMap<String,SoftReference<Boolean>>( 100 );
	
	protected KimEntity getEntityByIdCache( String entityId ) {
		SoftReference<KimEntity> entityRef = entityByIdCache.get( entityId );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}

	protected KimEntity getEntityByPrincipalNameCache( String entityName ) {
		SoftReference<KimEntity> entityRef = entityByPrincipalNameCache.get( entityName );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}
	
	protected KimPrincipal getPrincipalByIdCache( String principalId ) {
		SoftReference<KimPrincipal> principalRef = principalByIdCache.get( principalId );
		if ( principalRef != null ) {
			return principalRef.get();
		}
		return null;
	}

	protected KimPrincipal getPrincipalByNameCache( String principalName ) {
		SoftReference<KimPrincipal> principalRef = principalByNameCache.get( principalName );
		if ( principalRef != null ) {
			return principalRef.get();
		}
		return null;
	}
	
	protected GroupInfo getGroupByIdCache( String groupId ) {
		SoftReference<GroupInfo> groupRef = groupByIdCache.get( groupId );
		if ( groupRef != null ) {
			return groupRef.get();
		}
		return null;
	}

	protected GroupInfo getGroupByNameCache( String groupName ) {
		SoftReference<GroupInfo> groupRef = groupByNameCache.get( groupName );
		if ( groupRef != null ) {
			return groupRef.get();
		}
		return null;
	}

	protected List<String> getGroupIdsForPrincipalCache( String principalId ) {
		SoftReference<List<String>> groupIdsRef = groupIdsForPrincipalCache.get( principalId );
		if ( groupIdsRef != null ) {
			return groupIdsRef.get();
		}
		return null;
	}
	
	protected List<? extends KimGroup> getGroupsForPrincipalCache( String principalId ) {
		SoftReference<List<? extends KimGroup>> groupsRef = groupsForPrincipalCache.get( principalId );
		if ( groupsRef != null ) {
			return groupsRef.get();
		}
		return null;
	}

	protected Boolean getIsMemberOfGroupCache( String principalId, String groupId ) {
		SoftReference<Boolean> isMemberRef = isMemberOfGroupCache.get( principalId + "-" + groupId );
		if ( isMemberRef != null ) {
			return isMemberRef.get();
		}
		return null;
	}

	protected Boolean getIsMemberOfGroupByNameCache( String principalId, String namespaceCode, String groupName ) {
		SoftReference<Boolean> isMemberRef = isMemberOfGroupByNameCache.get( principalId + "-" + namespaceCode + "-" + groupName );
		if ( isMemberRef != null ) {
			return isMemberRef.get();
		}
		return null;
	}
	
	protected List<String> getGroupMemberPrincipalIdsCache( String groupId ) {
		SoftReference<List<String>> memberIdsRef = groupMemberPrincipalIdsCache.get( groupId );
		if ( memberIdsRef != null ) {
			return memberIdsRef.get();
		}
		return null;
	}
	
	protected Boolean getHasPermissionCache( String key) {
		SoftReference<Boolean> hasPermissionRef = hasPermissionCache.get( key );
		if ( hasPermissionRef != null ) {
			return hasPermissionRef.get();
		}
		return null;
	}
	
	protected Boolean getHasPermissionByTemplateCache( String key) {
		SoftReference<Boolean> hasPermissionRef = hasPermissionByTemplateCache.get( key );
		if ( hasPermissionRef != null ) {
			return hasPermissionRef.get();
		}
		return null;
	}
	
	protected void addEntityToCache( KimEntity entity ) {
		if ( entity != null ) {
			entityByPrincipalNameCache.put( entity.getPrincipals().get(0).getPrincipalName(), new SoftReference<KimEntity>( entity ) );
			entityByIdCache.put( entity.getEntityId(), new SoftReference<KimEntity>( entity ) );
		}
	}
	
	protected void addPrincipalToCache( KimPrincipal principal ) {
		if ( principal != null ) {
			principalByNameCache.put( principal.getPrincipalName(), new SoftReference<KimPrincipal>( principal ) );
			principalByIdCache.put( principal.getPrincipalId(), new SoftReference<KimPrincipal>( principal ) );
		}
	}
	
	protected void addGroupToCache( GroupInfo group ) {
		if ( group != null ) {
			groupByNameCache.put( group.getGroupName(), new SoftReference<GroupInfo>( group ) );
			groupByIdCache.put( group.getGroupId(), new SoftReference<GroupInfo>( group ) );
		}
	}

	protected void addGroupIdsForPrincipalToCache( String principalId, List<String> ids ) {
		if ( ids != null ) {
			groupIdsForPrincipalCache.put( principalId, new SoftReference<List<String>>( ids ) );
		}
	}

	protected void addGroupsForPrincipalToCache( String principalId, List<? extends KimGroup> groups ) {
		if ( groups != null ) {
			groupsForPrincipalCache.put( principalId, new SoftReference<List<? extends KimGroup>>( groups ) );
		}
	}
	
	protected void addIsMemberOfGroupToCache( String principalId, String groupId, boolean member ) {
		isMemberOfGroupCache.put( principalId + "-" + groupId, new SoftReference<Boolean>( member ) );
	}
	
	protected void addIsMemberOfGroupByNameToCache( String principalId, String namespaceCode, String groupName, boolean member ) {
		isMemberOfGroupCache.put( principalId + "-" + namespaceCode + "-" + groupName, new SoftReference<Boolean>( member ) );
	}
	
	protected void addGroupMemberPrincipalIdsToCache( String groupId, List<String> ids ) {
		if ( ids != null ) {
			groupMemberPrincipalIdsCache.put( groupId, new SoftReference<List<String>>( ids ) );
		}
	}

	protected void addHasPermissionToCache( String key, boolean hasPerm ) {
		hasPermissionCache.put( key, new SoftReference<Boolean>( hasPerm ) );
	}

	protected void addHasPermissionByTemplateToCache( String key, boolean hasPerm ) {
		hasPermissionByTemplateCache.put( key, new SoftReference<Boolean>( hasPerm ) );
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
		String key = principalId + "-" + namespaceCode + "-" + permissionName + "-";
    	key = finishKey(permissionDetails, key);
    	Boolean hasPerm = getHasPermissionCache(key);
		if (hasPerm != null) {
			return hasPerm;
		}
		hasPerm = getPermissionService().hasPermission( principalId, namespaceCode, permissionName, permissionDetails );
    	addHasPermissionToCache(key, hasPerm);
    	return hasPerm;        	
    }
    
    public boolean isAuthorized(String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification ) {    	
		String key = principalId + "-" + namespaceCode + "-" + permissionName + "-";
    	key = finishKey(permissionDetails, key);
    	Boolean hasPerm = getHasPermissionCache(key);
		if (hasPerm != null) {
			if (!hasPerm) {
				return false;
			} else {
				addHasPermissionToCache(key, hasPerm);
			}
		}    	
    	return getPermissionService().isAuthorized( principalId, namespaceCode, permissionName, permissionDetails, qualification );
    }

    public boolean hasPermissionByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails) {
		String key = principalId + "-" + namespaceCode + "-" + permissionTemplateName + "-";
    	key = finishKey(permissionDetails, key);
    	Boolean hasPerm = getHasPermissionByTemplateCache(key);
		if (hasPerm != null) {
			return hasPerm;
		}
		hasPerm = getPermissionService().hasPermissionByTemplateName( principalId, namespaceCode, permissionTemplateName, permissionDetails );
    	addHasPermissionByTemplateToCache(key, hasPerm);
    	return hasPerm;   
    }
    
    public boolean isAuthorizedByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification ) {
		String key = principalId + "-" + namespaceCode + "-" + permissionTemplateName + "-";
    	key = finishKey(permissionDetails, key);
    	Boolean hasPerm = getHasPermissionByTemplateCache(key);
		if (hasPerm != null) {
			if (!hasPerm) {
				return false;
			} else {
				addHasPermissionByTemplateToCache(key, hasPerm);
			}
		}
		return getPermissionService().isAuthorizedByTemplateName( principalId, namespaceCode, permissionTemplateName, permissionDetails, qualification );
    }

	private String finishKey(AttributeSet permissionDetails, String key) {
		for ( Map.Entry<String, String> entry : permissionDetails.entrySet() ) {
    		key += entry.getKey() + "-" + entry.getValue();
    	}
		return key;
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
    
    @Deprecated
    public boolean hasPermission(String principalId, String permissionName, AttributeSet permissionDetails) {
    	return getPermissionService().hasPermission( principalId, permissionName, permissionDetails );
    }
    
    @Deprecated
    public boolean isAuthorized(String principalId,
    		String permissionName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	return getPermissionService().isAuthorized( principalId, permissionName, permissionDetails, qualification );
    }

    @Deprecated
    public boolean hasPermissionByTemplateName(String principalId, String permissionTemplateName, AttributeSet permissionDetails) {
    	return getPermissionService().hasPermissionByTemplateName( principalId, permissionTemplateName, permissionDetails );
    }
    
    @Deprecated
    public boolean isAuthorizedByTemplateName(String principalId,
    		String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	return getPermissionService().isAuthorizedByTemplateName( principalId, permissionTemplateName, permissionDetails, qualification );
    }
    
    /**
     * @see org.kuali.rice.kim.service.IdentityManagementService#getAuthorizedPermissions(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Deprecated
    public List<? extends KimPermission> getAuthorizedPermissions(String principalId,
    		String permissionName, AttributeSet permissionDetails, AttributeSet qualification) {
    	return getPermissionService().getAuthorizedPermissions( principalId, permissionName, permissionDetails, qualification );
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
    	Boolean isMember = getIsMemberOfGroupByNameCache(principalId, namespaceCode, groupName);
		if (isMember != null) {
			return isMember;
		}
		KimGroup group = getGroupByName(namespaceCode, groupName);
		isMember = getGroupService().isMemberOfGroup(principalId, group.getGroupId());
    	addIsMemberOfGroupByNameToCache(principalId, namespaceCode, groupName, isMember);
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

	@Deprecated
	public List<? extends KimResponsibility> getResponsibilitiesByName( String responsibilityName) {
		return getResponsibilityService().getResponsibilitiesByName( responsibilityName );
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
	
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#hasResponsibility(java.lang.String, java.lang.String, AttributeSet, AttributeSet)
	 */
	@Deprecated
	public boolean hasResponsibility(String principalId,
			String responsibilityName, AttributeSet qualification,
			AttributeSet responsibilityDetails) {
		return getResponsibilityService().hasResponsibility( principalId, responsibilityName, qualification, responsibilityDetails );
	}
	
	/**
	 * @see org.kuali.rice.kim.service.IdentityManagementService#getResponsibilityActions(java.lang.String, AttributeSet, AttributeSet)
	 */
	@Deprecated
	public List<ResponsibilityActionInfo> getResponsibilityActions(String responsibilityName,
			AttributeSet qualification, AttributeSet responsibilityDetails) {
		return getResponsibilityService().getResponsibilityActions( responsibilityName, qualification, responsibilityDetails );
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

}
