/*
 * Copyright 2008-2009 The Kuali Foundation
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
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.entity.dto.KimEntityInfo;
import org.kuali.rice.kim.bo.entity.dto.KimPrincipalInfo;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.reference.dto.AddressTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.AffiliationTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.CitizenshipStatusInfo;
import org.kuali.rice.kim.bo.reference.dto.EmailTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.EmploymentStatusInfo;
import org.kuali.rice.kim.bo.reference.dto.EmploymentTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.EntityNameTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.EntityTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.ExternalIdentifierTypeInfo;
import org.kuali.rice.kim.bo.reference.dto.KimCodeInfoBase;
import org.kuali.rice.kim.bo.reference.dto.PhoneTypeInfo;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.KimResponsibilityInfo;
import org.kuali.rice.kim.bo.role.dto.PermissionAssigneeInfo;
import org.kuali.rice.kim.bo.role.dto.ResponsibilityActionInfo;
import org.kuali.rice.kim.service.AuthenticationService;
import org.kuali.rice.kim.service.GroupUpdateService;
import org.kuali.rice.kim.service.IdentityService;
import org.kuali.rice.kim.service.IdentityUpdateService;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.ResponsibilityService;
import org.kuali.rice.kim.util.KIMWebServiceConstants;
import org.springframework.beans.factory.InitializingBean;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import org.kuali.rice.kim.service.GroupService;

@WebService(endpointInterface = KIMWebServiceConstants.IdentityManagementService.INTERFACE_CLASS, serviceName = KIMWebServiceConstants.IdentityManagementService.WEB_SERVICE_NAME, portName = KIMWebServiceConstants.IdentityManagementService.WEB_SERVICE_PORT, targetNamespace = KIMWebServiceConstants.MODULE_TARGET_NAMESPACE)
public class IdentityManagementServiceImpl implements IdentityManagementService, InitializingBean {
	private static final Logger LOG = Logger.getLogger( IdentityManagementServiceImpl.class );

	private AuthenticationService authenticationService;
	private PermissionService permissionService;
	private ResponsibilityService responsibilityService;
	private IdentityService identityService;
	private GroupService groupService;
	private GroupUpdateService groupUpdateService;
	private IdentityUpdateService identityUpdateService;


	// Max age defined in seconds
	protected int entityPrincipalCacheMaxSize = 200;
	protected int entityPrincipalCacheMaxAgeSeconds = 30;
	protected int groupCacheMaxSize = 200;
	protected int groupCacheMaxAgeSeconds = 30;
	protected int permissionCacheMaxSize = 200;
	protected int permissionCacheMaxAgeSeconds = 30;
	protected int responsibilityCacheMaxSize = 200;
	protected int responsibilityCacheMaxAgeSeconds = 30;

	protected Map<String,MaxAgeSoftReference<KimEntityDefaultInfo>> entityDefaultInfoCache;
	protected Map<String,MaxAgeSoftReference<KimEntity>> entityCache;
	protected Map<String,MaxAgeSoftReference<KimEntityInfo>> entityInfoCache;
	protected Map<String,MaxAgeSoftReference<KimPrincipalInfo>> principalByIdCache;
	protected Map<String,MaxAgeSoftReference<KimPrincipalInfo>> principalByNameCache;
	protected Map<String,MaxAgeSoftReference<Group>> groupByIdCache;
	protected Map<String,MaxAgeSoftReference<Group>> groupByNameCache;
	protected Map<String,MaxAgeSoftReference<List<String>>> groupIdsForPrincipalCache;
	protected Map<String,MaxAgeSoftReference<List<? extends Group>>> groupsForPrincipalCache;
	protected Map<String,MaxAgeSoftReference<Boolean>> isMemberOfGroupCache;
	protected Map<String,MaxAgeSoftReference<Boolean>> isGroupMemberOfGroupCache;
	protected Map<String,MaxAgeSoftReference<List<String>>> groupMemberPrincipalIdsCache;
	protected Map<String,MaxAgeSoftReference<Boolean>> hasPermissionCache;
	protected Map<String,MaxAgeSoftReference<Boolean>> hasPermissionByTemplateCache;
	protected Map<String,MaxAgeSoftReference<Boolean>> isAuthorizedCache;
	protected Map<String,MaxAgeSoftReference<Boolean>> isAuthorizedByTemplateNameCache;
    protected Map<String,MaxAgeSoftReference<Boolean>> isPermissionDefinedForTemplateNameCache;

    protected HashMap<String,KimCodeInfoBase> kimReferenceTypeCache = new HashMap<String, KimCodeInfoBase>();

	public void afterPropertiesSet() throws Exception {
		entityDefaultInfoCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<KimEntityDefaultInfo>>( entityPrincipalCacheMaxSize ) );
		entityCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<KimEntity>>( entityPrincipalCacheMaxSize ) );
		entityInfoCache = Collections.synchronizedMap( new MaxSizeMap<String, MaxAgeSoftReference<KimEntityInfo>>(entityPrincipalCacheMaxSize));
		principalByIdCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<KimPrincipalInfo>>( entityPrincipalCacheMaxSize ) );
		principalByNameCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<KimPrincipalInfo>>( entityPrincipalCacheMaxSize ) );
		groupByIdCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<Group>>( groupCacheMaxSize ) );
		groupByNameCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<Group>>( groupCacheMaxSize ) );
		groupIdsForPrincipalCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<List<String>>>( groupCacheMaxSize ) );
		groupsForPrincipalCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<List<? extends Group>>>( groupCacheMaxSize ) );
		isMemberOfGroupCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( groupCacheMaxSize ) );
		groupMemberPrincipalIdsCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<List<String>>>( groupCacheMaxSize ) );
		hasPermissionCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( permissionCacheMaxSize ) );
		hasPermissionByTemplateCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( permissionCacheMaxSize ) );
		isPermissionDefinedForTemplateNameCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( permissionCacheMaxSize ) );
		isAuthorizedByTemplateNameCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( permissionCacheMaxSize ) );
		isAuthorizedCache = Collections.synchronizedMap( new MaxSizeMap<String,MaxAgeSoftReference<Boolean>>( permissionCacheMaxSize ) );
	}

	public void flushAllCaches() {
		flushEntityPrincipalCaches();
		flushGroupCaches();
		flushPermissionCaches();
		flushResponsibilityCaches();
	}

	public void flushEntityPrincipalCaches() {
		entityDefaultInfoCache.clear();
		entityCache.clear();
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

	protected KimEntityDefaultInfo getEntityDefaultInfoFromCache( String entityId ) {
		MaxAgeSoftReference<KimEntityDefaultInfo> entityRef = entityDefaultInfoCache.get( "entityId="+entityId );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}

	protected KimEntityDefaultInfo getEntityDefaultInfoFromCacheByPrincipalId( String principalId ) {
		MaxAgeSoftReference<KimEntityDefaultInfo> entityRef = entityDefaultInfoCache.get( "principalId="+principalId );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}

	protected KimEntityDefaultInfo getEntityDefaultInfoFromCacheByPrincipalName( String principalName ) {
		MaxAgeSoftReference<KimEntityDefaultInfo> entityRef = entityDefaultInfoCache.get( "principalName="+principalName );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}

	protected KimEntityInfo getEntityInfoFromCache( String entityId ) {
		MaxAgeSoftReference<KimEntityInfo> entityRef = entityInfoCache.get( "entityId="+entityId );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}

	protected KimEntityInfo getEntityInfoFromCacheByPrincipalId( String principalId ) {
		MaxAgeSoftReference<KimEntityInfo> entityRef = entityInfoCache.get( "principalId="+principalId );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}

	protected KimEntityInfo getEntityInfoFromCacheByPrincipalName( String principalName ) {
		MaxAgeSoftReference<KimEntityInfo> entityRef = entityInfoCache.get( "principalName="+principalName );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}

	protected KimEntity getEntityFromCache( String entityId ) {
		MaxAgeSoftReference<KimEntity> entityRef = entityCache.get( "entityId="+entityId );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}

	protected KimEntity getEntityFromCacheByPrincipalId( String principalId ) {
		MaxAgeSoftReference<KimEntity> entityRef = entityCache.get( "principalId="+principalId );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}

	protected KimEntity getEntityFromCacheByPrincipalName( String principalName ) {
		MaxAgeSoftReference<KimEntity> entityRef = entityCache.get( "principalName="+principalName );
		if ( entityRef != null ) {
			return entityRef.get();
		}
		return null;
	}

	protected KimPrincipalInfo getPrincipalByIdCache( String principalId ) {
		MaxAgeSoftReference<KimPrincipalInfo> principalRef = principalByIdCache.get( principalId );
		if ( principalRef != null ) {
			return principalRef.get();
		}
		return null;
	}

	protected KimPrincipalInfo getPrincipalByNameCache( String principalName ) {
		MaxAgeSoftReference<KimPrincipalInfo> principalRef = principalByNameCache.get( principalName );
		if ( principalRef != null ) {
			return principalRef.get();
		}
		return null;
	}

	protected Group getGroupByIdCache( String groupId ) {
		MaxAgeSoftReference<Group> groupRef = groupByIdCache.get( groupId );
		if ( groupRef != null ) {
			return groupRef.get();
		}
		return null;
	}

	protected Group getGroupByNameCache( String groupName ) {
		MaxAgeSoftReference<Group> groupRef = groupByNameCache.get( groupName );
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

	protected List<? extends Group> getGroupsForPrincipalCache( String principalId ) {
		MaxAgeSoftReference<List<? extends Group>> groupsRef = groupsForPrincipalCache.get( principalId );
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
			entityCache.put( "entityId="+entity.getEntityId(), new MaxAgeSoftReference<KimEntity>( entityPrincipalCacheMaxAgeSeconds, entity ) );
			for ( KimPrincipal p : entity.getPrincipals() ) {
				entityCache.put( "principalId="+p.getPrincipalId(), new MaxAgeSoftReference<KimEntity>( entityPrincipalCacheMaxAgeSeconds, entity ) );
				entityCache.put( "principalName="+p.getPrincipalName(), new MaxAgeSoftReference<KimEntity>( entityPrincipalCacheMaxAgeSeconds, entity ) );
			}
		}
	}

	protected void addEntityDefaultInfoToCache( KimEntityDefaultInfo entity ) {
		if ( entity != null ) {
			entityDefaultInfoCache.put( "entityId="+entity.getEntityId(), new MaxAgeSoftReference<KimEntityDefaultInfo>( entityPrincipalCacheMaxAgeSeconds, entity ) );
			for ( KimPrincipal p : entity.getPrincipals() ) {
				entityDefaultInfoCache.put( "principalId="+p.getPrincipalId(), new MaxAgeSoftReference<KimEntityDefaultInfo>( entityPrincipalCacheMaxAgeSeconds, entity ) );
				entityDefaultInfoCache.put( "principalName="+p.getPrincipalName(), new MaxAgeSoftReference<KimEntityDefaultInfo>( entityPrincipalCacheMaxAgeSeconds, entity ) );
			}
		}
	}

	protected void addEntityInfoToCache( KimEntityInfo entity ) {
		if ( entity != null ) {
			entityInfoCache.put( "entityId="+entity.getEntityId(), new MaxAgeSoftReference<KimEntityInfo>( entityPrincipalCacheMaxAgeSeconds, entity ) );
			for ( KimPrincipal p : entity.getPrincipals() ) {
				entityInfoCache.put( "principalId="+p.getPrincipalId(), new MaxAgeSoftReference<KimEntityInfo>( entityPrincipalCacheMaxAgeSeconds, entity ) );
				entityInfoCache.put( "principalName="+p.getPrincipalName(), new MaxAgeSoftReference<KimEntityInfo>( entityPrincipalCacheMaxAgeSeconds, entity ) );
			}
		}
	}

	protected void addPrincipalToCache( KimPrincipalInfo principal ) {
		if ( principal != null ) {
			principalByNameCache.put( principal.getPrincipalName(), new MaxAgeSoftReference<KimPrincipalInfo>( entityPrincipalCacheMaxAgeSeconds, principal ) );
			principalByIdCache.put( principal.getPrincipalId(), new MaxAgeSoftReference<KimPrincipalInfo>( entityPrincipalCacheMaxAgeSeconds, principal ) );
		}
	}

	protected void addGroupToCache( Group group ) {
		if ( group != null ) {
			groupByNameCache.put( group.getName(), new MaxAgeSoftReference<Group>( groupCacheMaxAgeSeconds, group ) );
			groupByIdCache.put( group.getId(), new MaxAgeSoftReference<Group>( groupCacheMaxAgeSeconds, group ) );
		}
	}

	protected void addGroupIdsForPrincipalToCache( String principalId, List<String> ids ) {
		if ( ids != null ) {
			groupIdsForPrincipalCache.put( principalId, new MaxAgeSoftReference<List<String>>( groupCacheMaxAgeSeconds, ids ) );
		}
	}

	protected void addGroupsForPrincipalToCache( String principalId, List<? extends Group> groups ) {
		if ( groups != null ) {
			groupsForPrincipalCache.put( principalId, new MaxAgeSoftReference<List<? extends Group>>( groupCacheMaxAgeSeconds, groups ) );
			List<String> groupIds = new ArrayList<String>( groups.size() );
			for ( Group group : groups ) {
				groupIds.add( group.getId() );
			}
			addGroupIdsForPrincipalToCache( principalId, groupIds );
		}
	}

	protected void addIsMemberOfGroupToCache( String principalId, String groupId, boolean member ) {
		isMemberOfGroupCache.put( principalId + "-" + groupId, new MaxAgeSoftReference<Boolean>( groupCacheMaxAgeSeconds, member ) );
	}

	protected void addIsGroupMemberOfGroupToCache( String potentialMemberId, String potentialParentId, boolean member )
	{
        isMemberOfGroupCache.put( potentialMemberId + "-" + potentialParentId, new MaxAgeSoftReference<Boolean>( groupCacheMaxAgeSeconds, member ) );
    }

	protected void addGroupMemberPrincipalIdsToCache( String groupId, List<String> ids ) {
		if ( ids != null ) {
			groupMemberPrincipalIdsCache.put( groupId, new MaxAgeSoftReference<List<String>>( groupCacheMaxAgeSeconds, ids ) );
		}
	}

	protected void addHasPermissionToCache( String key, boolean hasPerm ) {
		hasPermissionCache.put( key, new MaxAgeSoftReference<Boolean>( permissionCacheMaxAgeSeconds, hasPerm ) );
	}

	protected void addHasPermissionByTemplateToCache( String key, boolean hasPerm ) {
		hasPermissionByTemplateCache.put( key, new MaxAgeSoftReference<Boolean>( permissionCacheMaxAgeSeconds, hasPerm ) );
	}

	protected void addIsAuthorizedByTemplateNameToCache( String key, boolean authorized ) {
		isAuthorizedByTemplateNameCache.put( key, new MaxAgeSoftReference<Boolean>( permissionCacheMaxAgeSeconds, authorized ) );
	}

	protected void addIsAuthorizedToCache( String key, boolean authorized ) {
		isAuthorizedCache.put( key, new MaxAgeSoftReference<Boolean>( permissionCacheMaxAgeSeconds, authorized ) );
	}

    // AUTHORIZATION SERVICE

    public boolean hasPermission(String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails) {
    	if ( LOG.isDebugEnabled() ) {
    		logHasPermissionCheck("Permission", principalId, namespaceCode, permissionName, permissionDetails);
    	}
    	StringBuffer cacheKey = new StringBuffer();
    	cacheKey.append( principalId ).append(  '/' );
    	cacheKey.append( namespaceCode ).append( '-' ).append( permissionName ).append( '/' );
    	addAttributeSetToKey( permissionDetails, cacheKey );
    	String key = cacheKey.toString();
    	Boolean hasPerm = getHasPermissionCache(key);
		if (hasPerm == null) {
			hasPerm = getPermissionService().hasPermission( principalId, namespaceCode, permissionName, permissionDetails );
	    	addHasPermissionToCache(key, hasPerm);
    		if ( LOG.isDebugEnabled() ) {
    			LOG.debug( "Result: " + hasPerm );
    		}
		} else {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug( "Result Found in cache using key: " + key + "\nResult: " + hasPerm );
			}
		}
    	return hasPerm;
    }

    public boolean isAuthorized(String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	if ( qualification == null || qualification.isEmpty() ) {
    		return hasPermission( principalId, namespaceCode, permissionName, permissionDetails );
    	}
    	if ( LOG.isDebugEnabled() ) {
    		logAuthorizationCheck("Permission", principalId, namespaceCode, permissionName, permissionDetails, qualification);
    	}
    	StringBuffer cacheKey = new StringBuffer();
    	cacheKey.append( principalId ).append(  '/' );
    	cacheKey.append( namespaceCode ).append( '-' ).append( permissionName ).append( '/' );
    	addAttributeSetToKey( permissionDetails, cacheKey );
    	cacheKey.append( '/' );
    	addAttributeSetToKey( qualification, cacheKey );
    	String key = cacheKey.toString();
    	Boolean isAuthorized = getIsAuthorizedFromCache( key );
    	if ( isAuthorized == null ) {
    		isAuthorized = getPermissionService().isAuthorized( principalId, namespaceCode, permissionName, permissionDetails, qualification );
    		addIsAuthorizedToCache( key, isAuthorized );
    		if ( LOG.isDebugEnabled() ) {
    			LOG.debug( "Result: " + isAuthorized );
    		}
		} else {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug( "Result Found in cache using key: " + key + "\nResult: " + isAuthorized );
			}
    	}
    	return isAuthorized;
    }

    public boolean hasPermissionByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails) {
    	if ( LOG.isDebugEnabled() ) {
    		logHasPermissionCheck("Perm Templ", principalId, namespaceCode, permissionTemplateName, permissionDetails);
    	}
    	StringBuffer cacheKey = new StringBuffer();
    	cacheKey.append( principalId ).append(  '/' );
    	cacheKey.append( namespaceCode ).append( '-' ).append( permissionTemplateName ).append( '/' );
    	addAttributeSetToKey( permissionDetails, cacheKey );
    	String key = cacheKey.toString();
    	Boolean hasPerm = getHasPermissionByTemplateCache(key);
		if (hasPerm == null) {
			hasPerm = getPermissionService().hasPermissionByTemplateName( principalId, namespaceCode, permissionTemplateName, permissionDetails );
	    	addHasPermissionByTemplateToCache(key, hasPerm);
    		if ( LOG.isDebugEnabled() ) {
    			LOG.debug( "Result: " + hasPerm );
    		}
		} else {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug( "Result Found in cache using key: " + key + "\nResult: " + hasPerm );
			}
		}
    	return hasPerm;
    }

    public boolean isAuthorizedByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	if ( qualification == null || qualification.isEmpty() ) {
    		return hasPermissionByTemplateName( principalId, namespaceCode, permissionTemplateName, permissionDetails );
    	}
    	if ( LOG.isDebugEnabled() ) {
    		logAuthorizationCheck("Perm Templ", principalId, namespaceCode, permissionTemplateName, permissionDetails, qualification);
    	}
    	StringBuffer cacheKey = new StringBuffer();
    	cacheKey.append( principalId ).append(  '/' );
    	cacheKey.append( namespaceCode ).append( '-' ).append( permissionTemplateName ).append( '/' );
    	addAttributeSetToKey( permissionDetails, cacheKey );
    	cacheKey.append( '/' );
    	addAttributeSetToKey( qualification, cacheKey );
    	String key = cacheKey.toString();
    	Boolean isAuthorized = getIsAuthorizedByTemplateNameFromCache( key );
    	if ( isAuthorized == null ) {
    		isAuthorized = getPermissionService().isAuthorizedByTemplateName( principalId, namespaceCode, permissionTemplateName, permissionDetails, qualification );
    		addIsAuthorizedByTemplateNameToCache( key, isAuthorized );
    		if ( LOG.isDebugEnabled() ) {
    			LOG.debug( "Result: " + isAuthorized );
    		}
		} else {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug( "Result Found in cache using key: " + key + "\nResult: " + isAuthorized );
			}
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
     * @see org.kuali.rice.kim.api.services.IdentityManagementService#getAuthorizedPermissions(java.lang.String, String, java.lang.String, org.kuali.rice.core.util.AttributeSet, org.kuali.rice.core.util.AttributeSet)
     */
    public List<? extends KimPermissionInfo> getAuthorizedPermissions(String principalId,
    		String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification) {
    	return getPermissionService().getAuthorizedPermissions( principalId, namespaceCode, permissionName, permissionDetails, qualification );
    }

    public List<? extends KimPermissionInfo> getAuthorizedPermissionsByTemplateName(String principalId,
    		String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification) {
    	return getPermissionService().getAuthorizedPermissionsByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails, qualification);
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
        isPermissionDefinedForTemplateNameCache.put(key.toString(),new MaxAgeSoftReference<Boolean>( permissionCacheMaxAgeSeconds, result ));
        return result;
    }


	public List<PermissionAssigneeInfo> getPermissionAssignees(String namespaceCode,
			String permissionName, AttributeSet permissionDetails, AttributeSet qualification) {
		return this.permissionService.getPermissionAssignees( namespaceCode, permissionName,
				permissionDetails, qualification );
	}

	public List<PermissionAssigneeInfo> getPermissionAssigneesForTemplateName(String namespaceCode,
			String permissionTemplateName, AttributeSet permissionDetails,
			AttributeSet qualification) {
		return this.permissionService.getPermissionAssigneesForTemplateName( namespaceCode,
				permissionTemplateName, permissionDetails, qualification );
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
		Group group = getGroupByName(namespaceCode, groupName);
		if ( group == null ) {
			return false;
		}
		return isMemberOfGroup(principalId, group.getId());
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

    public List<? extends Group> getGroupsForPrincipal(String principalId) {
    	List<? extends Group> groups = getGroupsForPrincipalCache(principalId);
		if (groups != null) {
			return groups;
		}
		groups = getGroupService().getGroupsForPrincipal(principalId);
    	addGroupsForPrincipalToCache(principalId, groups);
    	return groups;
	}

    public List<? extends Group> getGroupsForPrincipal(String principalId, String namespaceCode ) {
    	List<? extends Group> groups = getGroupsForPrincipalCache(principalId + "-" + namespaceCode);
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

    public Group getGroup(String groupId) {
    	Group group = getGroupByIdCache(groupId);
		if (group != null) {
			return group;
		}
		group = getGroupService().getGroup(groupId);
    	addGroupToCache(group);
    	return group;
	}

    public Group getGroupByName(String namespaceCode, String groupName) {
    	Group group = getGroupByNameCache(namespaceCode + "-" + groupName);
		if (group != null) {
			return group;
		}
		group = getGroupService().getGroupByName( namespaceCode, groupName );
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
    		synchronized (isMemberOfGroupCache) {
    		Iterator<String> keys = isMemberOfGroupCache.keySet().iterator();
    		while ( keys.hasNext() ) {
    			String key = keys.next();
    			if ( key.endsWith("-"+groupId) ) {
    				keys.remove();
    			}
    		}
			}
    		// NOTE: There's no good way to selectively purge the other two group caches or the permission caches which could be
    		// affected - is this necessary or do we just wait for the cache items to expire
    	}
    	groupMemberPrincipalIdsCache.remove(groupId);
    }


    public boolean addGroupToGroup(String childId, String parentId) {
    	clearGroupCachesForPrincipalAndGroup(null, parentId);
        return getGroupUpdateService().addGroupToGroup(childId, parentId);
    }

    public boolean addPrincipalToGroup(String principalId, String groupId) {
    	clearGroupCachesForPrincipalAndGroup(principalId, groupId);
        return getGroupUpdateService().addPrincipalToGroup(principalId, groupId);
    }

    public boolean removeGroupFromGroup(String childId, String parentId) {
    	clearGroupCachesForPrincipalAndGroup(null, parentId);
        return getGroupUpdateService().removeGroupFromGroup(childId, parentId);
    }

    public boolean removePrincipalFromGroup(String principalId, String groupId) {
    	clearGroupCachesForPrincipalAndGroup(principalId, groupId);
        return getGroupUpdateService().removePrincipalFromGroup(principalId, groupId);
    }

    /**
	 * This delegate method ...
	 *
	 * @param groupInfo
	 * @return
	 * @see org.kuali.rice.kim.service.GroupUpdateService#createGroup(org.kuali.rice.kim.bo.group.dto.GroupInfo)
	 */
	public GroupInfo createGroup(GroupInfo groupInfo) {
    	clearGroupCachesForPrincipalAndGroup(null,groupInfo.getGroupId());
		return getGroupUpdateService().createGroup(groupInfo);
	}

	/**
	 * This delegate method ...
	 *
	 * @param groupId
	 * @see org.kuali.rice.kim.service.GroupUpdateService#removeAllGroupMembers(java.lang.String)
	 */
	public void removeAllGroupMembers(String groupId) {
    	clearGroupCachesForPrincipalAndGroup(null, groupId);
		getGroupUpdateService().removeAllGroupMembers(groupId);
	}

	/**
	 * This delegate method ...
	 *
	 * @param groupId
	 * @param groupInfo
	 * @return
	 * @see org.kuali.rice.kim.service.GroupUpdateService#updateGroup(java.lang.String, org.kuali.rice.kim.bo.group.dto.GroupInfo)
	 */
	public GroupInfo updateGroup(String groupId, GroupInfo groupInfo) {
    	clearGroupCachesForPrincipalAndGroup(null, groupId);
		return getGroupUpdateService().updateGroup(groupId, groupInfo);
	}


    // IDENTITY SERVICE

	public KimPrincipalInfo getPrincipal(String principalId) {
    	KimPrincipalInfo principal = getPrincipalByIdCache(principalId);
		if (principal != null) {
			return principal;
		}
		principal = getIdentityService().getPrincipal(principalId);
    	addPrincipalToCache(principal);
    	return principal;
	}

    public KimPrincipalInfo getPrincipalByPrincipalName(String principalName) {
    	KimPrincipalInfo principal = getPrincipalByNameCache(principalName);
		if (principal != null) {
			return principal;
		}
		principal = getIdentityService().getPrincipalByPrincipalName(principalName);
    	addPrincipalToCache(principal);
    	return principal;
    }

    /**
     * @see org.kuali.rice.kim.api.services.IdentityManagementService#getPrincipalByPrincipalNameAndPassword(java.lang.String, java.lang.String)
     */
    public KimPrincipalInfo getPrincipalByPrincipalNameAndPassword(String principalName, String password) {
    	// TODO: cache this?
    	return getIdentityService().getPrincipalByPrincipalNameAndPassword( principalName, password );
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.api.services.IdentityManagementService#getEntityDefaultInfo(java.lang.String)
     */
    public KimEntityDefaultInfo getEntityDefaultInfo(String entityId) {
    	KimEntityDefaultInfo entity = getEntityDefaultInfoFromCache( entityId );
    	if ( entity == null ) {
    		entity = getIdentityService().getEntityDefaultInfo(entityId);
    		addEntityDefaultInfoToCache( entity );
    	}
    	return entity;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.api.services.IdentityManagementService#getEntityDefaultInfoByPrincipalId(java.lang.String)
     */
    public KimEntityDefaultInfo getEntityDefaultInfoByPrincipalId(
    		String principalId) {
    	KimEntityDefaultInfo entity = getEntityDefaultInfoFromCacheByPrincipalId( principalId );
    	if ( entity == null ) {
	    	entity = getIdentityService().getEntityDefaultInfoByPrincipalId(principalId);
			addEntityDefaultInfoToCache( entity );
    	}
    	return entity;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.api.services.IdentityManagementService#getEntityDefaultInfoByPrincipalName(java.lang.String)
     */
    public KimEntityDefaultInfo getEntityDefaultInfoByPrincipalName(
    		String principalName) {
    	KimEntityDefaultInfo entity = getEntityDefaultInfoFromCacheByPrincipalName( principalName );
    	if ( entity == null ) {
	    	entity = getIdentityService().getEntityDefaultInfoByPrincipalName(principalName);
			addEntityDefaultInfoToCache( entity );
    	}
    	return entity;
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.kim.api.services.IdentityManagementService#lookupEntityDefaultInfo(Map, boolean)
     */
    public List<? extends KimEntityDefaultInfo> lookupEntityDefaultInfo(
    		Map<String, String> searchCriteria, boolean unbounded) {
    	return getIdentityService().lookupEntityDefaultInfo(searchCriteria, unbounded);
    }


    /**
	 * @see org.kuali.rice.kim.api.services.IdentityManagementService#getEntityInfo(java.lang.String)
	 */
	public KimEntityInfo getEntityInfo(String entityId) {
    	KimEntityInfo entity = getEntityInfoFromCache( entityId );
    	if ( entity == null ) {
    		entity = getIdentityService().getEntityInfo(entityId);
    		addEntityInfoToCache( entity );
    	}
    	return entity;
	}

	/**
	 * @see org.kuali.rice.kim.api.services.IdentityManagementService#getEntityInfoByPrincipalId(java.lang.String)
	 */
	public KimEntityInfo getEntityInfoByPrincipalId(String principalId) {
    	KimEntityInfo entity = getEntityInfoFromCacheByPrincipalId( principalId );
    	if ( entity == null ) {
	    	entity = getIdentityService().getEntityInfoByPrincipalId(principalId);
			addEntityInfoToCache( entity );
    	}
    	return entity;
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.api.services.IdentityManagementService#getEntityInfoByPrincipalName(java.lang.String)
	 */
	public KimEntityInfo getEntityInfoByPrincipalName(String principalName) {
		KimEntityInfo entity = getEntityInfoFromCacheByPrincipalName( principalName );
    	if ( entity == null ) {
	    	entity = getIdentityService().getEntityInfoByPrincipalName( principalName );
			addEntityInfoToCache( entity );
    	}
    	return entity;
	}

	/**
	 * @see org.kuali.rice.kim.api.services.IdentityManagementService#lookupEntityInfo(java.util.Map, boolean)
	 */
	public List<KimEntityInfo> lookupEntityInfo(
			Map<String, String> searchCriteria, boolean unbounded) {
		return getIdentityService().lookupEntityInfo(searchCriteria, unbounded);
	}

	/**
     * @see org.kuali.rice.kim.api.services.IdentityManagementService#getMatchingEntityCount(java.util.Map)
     */
    public int getMatchingEntityCount(Map<String,String> searchCriteria) {
    	return getIdentityService().getMatchingEntityCount( searchCriteria );
    }

	public AddressTypeInfo getAddressType( String code ) {
		AddressTypeInfo type = (AddressTypeInfo)kimReferenceTypeCache.get(AddressTypeInfo.class.getSimpleName()+"-"+code);
		if ( type == null ) {
			type = getIdentityService().getAddressType(code);
			kimReferenceTypeCache.put(AddressTypeInfo.class.getSimpleName()+"-"+code, type);
		}
		return type;
	}
	public AffiliationTypeInfo getAffiliationType( String code ) {
		AffiliationTypeInfo type = (AffiliationTypeInfo)kimReferenceTypeCache.get(AffiliationTypeInfo.class.getSimpleName()+"-"+code);
		if ( type == null ) {
			type = getIdentityService().getAffiliationType(code);
			kimReferenceTypeCache.put(AddressTypeInfo.class.getSimpleName()+"-"+code, type);
		}
		return type;
	}
	public CitizenshipStatusInfo getCitizenshipStatus( String code ) {
		CitizenshipStatusInfo type = (CitizenshipStatusInfo)kimReferenceTypeCache.get(CitizenshipStatusInfo.class.getSimpleName()+"-"+code);
		if ( type == null ) {
			type = getIdentityService().getCitizenshipStatus(code);
			kimReferenceTypeCache.put(CitizenshipStatusInfo.class.getSimpleName()+"-"+code, type);
		}
		return type;
	}
	public EmailTypeInfo getEmailType( String code ) {
		EmailTypeInfo type = (EmailTypeInfo)kimReferenceTypeCache.get(EmailTypeInfo.class.getSimpleName()+"-"+code);
		if ( type == null ) {
			type = getIdentityService().getEmailType(code);
			kimReferenceTypeCache.put(EmailTypeInfo.class.getSimpleName()+"-"+code, type);
		}
		return type;
	}
	public EmploymentStatusInfo getEmploymentStatus( String code ) {
		EmploymentStatusInfo type = (EmploymentStatusInfo)kimReferenceTypeCache.get(EmploymentStatusInfo.class.getSimpleName()+"-"+code);
		if ( type == null ) {
			type = getIdentityService().getEmploymentStatus(code);
			kimReferenceTypeCache.put(EmploymentStatusInfo.class.getSimpleName()+"-"+code, type);
		}
		return type;
	}
	public EmploymentTypeInfo getEmploymentType( String code ) {
		EmploymentTypeInfo type = (EmploymentTypeInfo)kimReferenceTypeCache.get(EmploymentTypeInfo.class.getSimpleName()+"-"+code);
		if ( type == null ) {
			type = getIdentityService().getEmploymentType(code);
			kimReferenceTypeCache.put(EmploymentTypeInfo.class.getSimpleName()+"-"+code, type);
		}
		return type;
	}
	public EntityNameTypeInfo getEntityNameType( String code ) {
		EntityNameTypeInfo type = (EntityNameTypeInfo)kimReferenceTypeCache.get(EntityNameTypeInfo.class.getSimpleName()+"-"+code);
		if ( type == null ) {
			type = getIdentityService().getEntityNameType(code);
			kimReferenceTypeCache.put(EntityNameTypeInfo.class.getSimpleName()+"-"+code, type);
		}
		return type;
	}
	public EntityTypeInfo getEntityType( String code ) {
		EntityTypeInfo type = (EntityTypeInfo)kimReferenceTypeCache.get(EntityTypeInfo.class.getSimpleName()+"-"+code);
		if ( type == null ) {
			type = getIdentityService().getEntityType(code);
			kimReferenceTypeCache.put(EntityTypeInfo.class.getSimpleName()+"-"+code, type);
		}
		return type;
	}
	public ExternalIdentifierTypeInfo getExternalIdentifierType( String code ) {
		ExternalIdentifierTypeInfo type = (ExternalIdentifierTypeInfo)kimReferenceTypeCache.get(ExternalIdentifierTypeInfo.class.getSimpleName()+"-"+code);
		if ( type == null ) {
			type = getIdentityService().getExternalIdentifierType(code);
			kimReferenceTypeCache.put(ExternalIdentifierTypeInfo.class.getSimpleName()+"-"+code, type);
		}
		return type;
	}
	public PhoneTypeInfo getPhoneType( String code ) {
		PhoneTypeInfo type = (PhoneTypeInfo)kimReferenceTypeCache.get(PhoneTypeInfo.class.getSimpleName()+"-"+code);
		if ( type == null ) {
			type = getIdentityService().getPhoneType(code);
			kimReferenceTypeCache.put(PhoneTypeInfo.class.getSimpleName()+"-"+code, type);
		}
		return type;
	}

	// OTHER METHODS

	public IdentityService getIdentityService() {
		if ( identityService == null ) {
			identityService = KimApiServiceLocator.getIdentityService();
		}
		return identityService;
	}

	public GroupService getGroupService() {
		if ( groupService == null ) {
			groupService = KimApiServiceLocator.getGroupService();
		}
		return groupService;
	}

	public PermissionService getPermissionService() {
		if ( permissionService == null ) {
			permissionService = KimApiServiceLocator.getPermissionService();
		}
		return permissionService;
	}

	public ResponsibilityService getResponsibilityService() {
		if ( responsibilityService == null ) {
			responsibilityService = KimApiServiceLocator.getResponsibilityService();
		}
		return responsibilityService;
	}

    // ----------------------
    // Responsibility Methods
    // ----------------------

	/**
	 * @see org.kuali.rice.kim.api.services.IdentityManagementService#getResponsibility(java.lang.String)
	 */
	public KimResponsibilityInfo getResponsibility(String responsibilityId) {
		return getResponsibilityService().getResponsibility( responsibilityId );
	}

	/**
	 * @see org.kuali.rice.kim.api.services.IdentityManagementService#hasResponsibility(java.lang.String, String, java.lang.String, AttributeSet, AttributeSet)
	 */
	public boolean hasResponsibility(String principalId, String namespaceCode,
			String responsibilityName, AttributeSet qualification,
			AttributeSet responsibilityDetails) {
		return getResponsibilityService().hasResponsibility( principalId, namespaceCode, responsibilityName, qualification, responsibilityDetails );
	}

	public List<? extends KimResponsibilityInfo> getResponsibilitiesByName( String namespaceCode, String responsibilityName) {
		return getResponsibilityService().getResponsibilitiesByName( namespaceCode, responsibilityName );
	}

	public List<ResponsibilityActionInfo> getResponsibilityActions( String namespaceCode, String responsibilityName,
    		AttributeSet qualification, AttributeSet responsibilityDetails) {
		return getResponsibilityService().getResponsibilityActions( namespaceCode, responsibilityName, qualification, responsibilityDetails );
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.api.services.IdentityManagementService#getResponsibilityActionsByTemplateName(java.lang.String, java.lang.String, org.kuali.rice.core.util.AttributeSet, org.kuali.rice.core.util.AttributeSet)
	 */
	public List<ResponsibilityActionInfo> getResponsibilityActionsByTemplateName(
			String namespaceCode, String responsibilityTemplateName,
			AttributeSet qualification, AttributeSet responsibilityDetails) {
		return getResponsibilityService().getResponsibilityActionsByTemplateName(namespaceCode, responsibilityTemplateName, qualification, responsibilityDetails);
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kim.api.services.IdentityManagementService#hasResponsibilityByTemplateName(java.lang.String, java.lang.String, java.lang.String, org.kuali.rice.core.util.AttributeSet, org.kuali.rice.core.util.AttributeSet)
	 */
	public boolean hasResponsibilityByTemplateName(String principalId,
			String namespaceCode, String responsibilityTemplateName,
			AttributeSet qualification, AttributeSet responsibilityDetails) {
		return getResponsibilityService().hasResponsibilityByTemplateName(principalId, namespaceCode, responsibilityTemplateName, qualification, responsibilityDetails);
	}

	public void setEntityPrincipalCacheMaxSize(int entityPrincipalCacheMaxSize) {
		this.entityPrincipalCacheMaxSize = entityPrincipalCacheMaxSize;
	}

	public void setEntityPrincipalCacheMaxAgeSeconds(int entityPrincipalCacheMaxAge) {
		this.entityPrincipalCacheMaxAgeSeconds = entityPrincipalCacheMaxAge;
	}

	public void setGroupCacheMaxSize(int groupCacheMaxSize) {
		this.groupCacheMaxSize = groupCacheMaxSize;
	}

	public void setGroupCacheMaxAgeSeconds(int groupCacheMaxAge) {
		this.groupCacheMaxAgeSeconds = groupCacheMaxAge;
	}

	public void setPermissionCacheMaxSize(int permissionCacheMaxSize) {
		this.permissionCacheMaxSize = permissionCacheMaxSize;
	}

	public void setPermissionCacheMaxAgeSeconds(int permissionCacheMaxAge) {
		this.permissionCacheMaxAgeSeconds = permissionCacheMaxAge;
	}

	public void setResponsibilityCacheMaxSize(int responsibilityCacheMaxSize) {
		this.responsibilityCacheMaxSize = responsibilityCacheMaxSize;
	}

	public void setResponsibilityCacheMaxAgeSeconds(int responsibilityCacheMaxAge) {
		this.responsibilityCacheMaxAgeSeconds = responsibilityCacheMaxAge;
	}

    protected void logAuthorizationCheck(String checkType, String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification ) {
		StringBuilder sb = new StringBuilder();
		sb.append(  '\n' );
		sb.append( "Is AuthZ for " ).append( checkType ).append( ": " ).append( namespaceCode ).append( "/" ).append( permissionName ).append( '\n' );
		sb.append( "             Principal:  " ).append( principalId );
		if ( principalId != null ) {
			KimPrincipalInfo principal = getPrincipal( principalId );
			if ( principal != null ) {
				sb.append( " (" ).append( principal.getPrincipalName() ).append( ')' );
			}
		}
		sb.append( '\n' );
		sb.append( "             Details:\n" );
		if ( permissionDetails != null ) {
			sb.append( permissionDetails.formattedDump( 25 ) );
		} else {
			sb.append( "                         [null]\n" );
		}
		sb.append( "             Qualifiers:\n" );
		if ( qualification != null && !qualification.isEmpty() ) {
			sb.append( qualification.formattedDump( 25 ) );
		} else {
			sb.append( "                         [null]\n" );
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace( sb.append(ExceptionUtils.getStackTrace(new Throwable())));
		} else {
			LOG.debug(sb.toString());
		}
    }

    protected void logHasPermissionCheck(String checkType, String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails ) {
		StringBuilder sb = new StringBuilder();
		sb.append(  '\n' );
		sb.append( "Has Perm for " ).append( checkType ).append( ": " ).append( namespaceCode ).append( "/" ).append( permissionName ).append( '\n' );
		sb.append( "             Principal:  " ).append( principalId );
		if ( principalId != null ) {
			KimPrincipalInfo principal = getPrincipal( principalId );
			if ( principal != null ) {
				sb.append( " (" ).append( principal.getPrincipalName() ).append( ')' );
			}
		}
		sb.append(  '\n' );
		sb.append( "             Details:\n" );
		if ( permissionDetails != null ) {
			sb.append( permissionDetails.formattedDump( 25 ) );
		} else {
			sb.append( "                         [null]\n" );
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace( sb.append( ExceptionUtils.getStackTrace(new Throwable())) );
		} else {
			LOG.debug(sb.toString());
		}
    }

	public GroupUpdateService getGroupUpdateService() {
		try {
			if ( groupUpdateService == null ) {
				groupUpdateService = KimApiServiceLocator.getGroupUpdateService();
				if ( groupUpdateService == null ) {
					throw new UnsupportedOperationException( "null returned for GroupUpdateService, unable to update group data");
				}
			}
		} catch ( Exception ex ) {
			throw new UnsupportedOperationException( "unable to obtain a GroupUpdateService, unable to update group data", ex);
		}
		return groupUpdateService;
	}

	public IdentityUpdateService getIdentityUpdateService() {
		try {
			if ( identityUpdateService == null ) {
				identityUpdateService = KimApiServiceLocator.getIdentityUpdateService();
				if ( identityUpdateService == null ) {
					throw new UnsupportedOperationException( "null returned for IdentityUpdateService, unable to update identity data");
				}
			}
		} catch ( Exception ex ) {
			throw new UnsupportedOperationException( "unable to obtain an IdentityUpdateService, unable to update identity data", ex);
		}
		return identityUpdateService;
	}
}
