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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.util.MaxAgeSoftReference;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.impl.PermissionImpl;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.KimPermissionTemplateInfo;
import org.kuali.rice.kim.bo.role.dto.PermissionAssigneeInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.bo.role.impl.KimPermissionTemplateImpl;
import org.kuali.rice.kim.bo.role.impl.PermissionAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.dto.KimTypeAttributeInfo;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.dao.KimPermissionDao;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.PermissionUpdateService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.support.KimPermissionTypeService;
import org.kuali.rice.kim.util.KIMWebServiceConstants;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.util.KNSPropertyConstants;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@WebService(endpointInterface = KIMWebServiceConstants.PermissionService.INTERFACE_CLASS, serviceName = KIMWebServiceConstants.PermissionService.WEB_SERVICE_NAME, portName = KIMWebServiceConstants.PermissionService.WEB_SERVICE_PORT, targetNamespace = KIMWebServiceConstants.MODULE_TARGET_NAMESPACE)
public class PermissionServiceImpl implements PermissionService, PermissionUpdateService {
	private static final String DEFAULT_PERMISSION_TYPE_SERVICE = "defaultPermissionTypeService";
	private static final Logger LOG = Logger.getLogger( PermissionServiceImpl.class );
	
	private BusinessObjectService businessObjectService;
	private RoleService roleService;
	private KimPermissionDao permissionDao;
    private KimPermissionTypeService defaultPermissionTypeService;
	private SequenceAccessorService sequenceAccessorService;
	private List<KimPermissionTemplateInfo> allTemplates;

    private static final long CACHE_MAX_AGE_SECONDS = 60L;

    private Map<String,MaxAgeSoftReference<List<KimPermissionImpl>>> permissionCache = Collections.synchronizedMap( new HashMap<String,MaxAgeSoftReference<List<KimPermissionImpl>>>() );
    private Map<List<KimPermissionInfo>,MaxAgeSoftReference<List<String>>> permissionToRoleCache = Collections.synchronizedMap( new HashMap<List<KimPermissionInfo>,MaxAgeSoftReference<List<String>>>() );

    // Not ThreadLocal or time limited- should not change during the life of the system
	private Map<String,KimPermissionTypeService> permissionTypeServiceByNameCache = Collections.synchronizedMap( new HashMap<String, KimPermissionTypeService>() );
	
    // --------------------
    // Authorization Checks
    // --------------------
    
	protected KimPermissionTypeService getPermissionTypeService( String namespaceCode, String permissionTemplateName, String permissionName, String permissionId ) {
		StringBuffer cacheKey = new StringBuffer();
		if ( namespaceCode != null ) {
			cacheKey.append( namespaceCode );
		}
		cacheKey.append( '|' );
		if ( permissionTemplateName != null ) {
			cacheKey.append( permissionTemplateName );
		}
		cacheKey.append( '|' );
		if ( permissionName != null ) {
			cacheKey.append( permissionName );
		}
		cacheKey.append( '|' );
		if ( permissionId != null ) {
			cacheKey.append( permissionId );
		}
		String key = cacheKey.toString();
		KimPermissionTypeService service = permissionTypeServiceByNameCache.get(key);
		if ( service == null ) {
			KimPermissionTemplateImpl permTemplate = null;
			if ( permissionTemplateName != null ) {
				List<KimPermissionImpl> perms = getPermissionImplsByTemplateName(namespaceCode, permissionTemplateName);
				if ( !perms.isEmpty() ) {
					permTemplate = perms.get(0).getTemplate();
				}
			} else if ( permissionName != null ) {
				List<KimPermissionImpl> perms = getPermissionImplsByName(namespaceCode, permissionName); 
				if ( !perms.isEmpty() ) {
					permTemplate = perms.get(0).getTemplate();
				}
			} else if ( permissionId != null ) {
				KimPermissionImpl perm = getPermissionImpl(permissionId);
				if ( perm != null ) {
					permTemplate = perm.getTemplate();
				}
			}
			service = getPermissionTypeService( permTemplate );
    		permissionTypeServiceByNameCache.put(key, service);
		}
		return service;
	}

    protected KimPermissionTypeService getPermissionTypeService( KimPermissionTemplateImpl permissionTemplate ) {
    	if ( permissionTemplate == null ) {
    		throw new IllegalArgumentException( "permissionTemplate may not be null" );
    	}
    	KimTypeInfo kimType = KIMServiceLocator.getTypeInfoService().getKimType( permissionTemplate.getKimTypeId() );
    	String serviceName = kimType.getKimTypeServiceName();
    	// if no service specified, return a default implementation
    	if ( StringUtils.isBlank( serviceName ) ) {
    		return getDefaultPermissionTypeService();
    	}
    	try {
	    	Object service = KIMServiceLocator.getService( serviceName );
	    	// if we have a service name, it must exist
	    	if ( service == null ) {
				throw new RuntimeException("null returned for permission type service for service name: " + serviceName);
	    	}
	    	// whatever we retrieved must be of the correct type
	    	if ( !(service instanceof KimPermissionTypeService)  ) {
	    		throw new RuntimeException( "Service " + serviceName + " was not a KimPermissionTypeService.  Was: " + service.getClass().getName() );
	    	}
	    	return (KimPermissionTypeService)service;
    	} catch( Exception ex ) {
    		// sometimes service locators throw exceptions rather than returning null, handle that
    		throw new RuntimeException( "Error retrieving service: " + serviceName + " from the KIMServiceLocator.", ex );
    	}
    }
    
    protected KimPermissionTypeService getDefaultPermissionTypeService() {
    	if ( defaultPermissionTypeService == null ) {
    		defaultPermissionTypeService = (KimPermissionTypeService)KIMServiceLocator.getBean(DEFAULT_PERMISSION_TYPE_SERVICE);
    	}
		return defaultPermissionTypeService;
	}
	
    /**
     * @see org.kuali.rice.kim.service.PermissionService#hasPermission(java.lang.String, String, java.lang.String, AttributeSet)
     */
    public boolean hasPermission(String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails) {
    	return isAuthorized( principalId, namespaceCode, permissionName, permissionDetails, null );
    }

    /**
     * @see org.kuali.rice.kim.service.PermissionService#isAuthorized( java.lang.String, String, java.lang.String, AttributeSet, AttributeSet)
     */
    public boolean isAuthorized(String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	List<String> roleIds = getRoleIdsForPermission( namespaceCode, permissionName, permissionDetails );
    	if ( roleIds.isEmpty() ) {
    		return false;
    	}
		return getRoleService().principalHasRole( principalId, roleIds, qualification );
    }

    /**
     * @see org.kuali.rice.kim.service.PermissionService#hasPermission(String, String, String, AttributeSet)
     */
    public boolean hasPermissionByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails) {
    	return isAuthorizedByTemplateName( principalId, namespaceCode, permissionTemplateName, permissionDetails, null );
    }

    /**
     * @see org.kuali.rice.kim.service.PermissionService#isAuthorized( java.lang.String, String, java.lang.String, AttributeSet, AttributeSet)
     */
    public boolean isAuthorizedByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	List<String> roleIds = getRoleIdsForPermissionTemplate( namespaceCode, permissionTemplateName, permissionDetails );
    	if ( roleIds.isEmpty() ) {
    		return false;
    	}
    	return getRoleService().principalHasRole( principalId, roleIds, qualification );
    }

    /**
     * @see org.kuali.rice.kim.service.PermissionService#getAuthorizedPermissions(String, String, String, AttributeSet, AttributeSet)
     */
    public List<KimPermissionInfo> getAuthorizedPermissions( String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	// get all the permission objects whose name match that requested
    	List<KimPermissionImpl> permissions = getPermissionImplsByName( namespaceCode, permissionName );
    	// now, filter the full list by the detail passed
    	List<KimPermissionInfo> applicablePermissions = getMatchingPermissions( permissions, permissionDetails );  
    	return getPermissionsForUser(principalId, applicablePermissions, qualification);
    }

    /**
     * @see org.kuali.rice.kim.service.PermissionService#getAuthorizedPermissionsByTemplateName(String, String, String, AttributeSet, AttributeSet)
     */
    public List<KimPermissionInfo> getAuthorizedPermissionsByTemplateName( String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	// get all the permission objects whose name match that requested
    	List<KimPermissionImpl> permissions = getPermissionImplsByTemplateName( namespaceCode, permissionTemplateName );
    	// now, filter the full list by the detail passed
    	List<KimPermissionInfo> applicablePermissions = getMatchingPermissions( permissions, permissionDetails );  
    	return getPermissionsForUser(principalId, applicablePermissions, qualification);
    }
    
    /**
     * Checks the list of permissions against the principal's roles and returns a subset of the list which match.
     */
    protected List<KimPermissionInfo> getPermissionsForUser( String principalId, List<KimPermissionInfo> permissions, AttributeSet qualification ) {
    	ArrayList<KimPermissionInfo> results = new ArrayList<KimPermissionInfo>();
    	List<KimPermissionInfo> tempList = new ArrayList<KimPermissionInfo>(1);
    	for ( KimPermissionInfo perm : permissions ) {
    		tempList.clear();
    		tempList.add( perm );
    		List<String> roleIds = permissionDao.getRoleIdsForPermissions( tempList );
    		// TODO: This could be made a little better by collecting the role IDs into
    		// a set and then processing the distinct list rather than a check
    		// for every permission
    		if ( roleIds != null && !roleIds.isEmpty() ) {
    			if ( getRoleService().principalHasRole( principalId, roleIds, qualification ) ) {
    				results.add( perm );
    			}
    		}
    	}
    	
    	return results;    	
    }

    protected Map<String,KimPermissionTypeService> getPermissionTypeServicesByTemplateId( Collection<KimPermissionImpl> permissions ) {
    	Map<String,KimPermissionTypeService> permissionTypeServices = new HashMap<String, KimPermissionTypeService>( permissions.size() );
    	for ( KimPermissionImpl perm : permissions ) {
    		permissionTypeServices.put(perm.getTemplateId(), getPermissionTypeService( perm.getTemplate() ) );    				
    	}
    	return permissionTypeServices;
    }
    
    protected Map<String,List<KimPermissionInfo>> groupPermissionsByTemplate( Collection<KimPermissionImpl> permissions ) {
    	Map<String,List<KimPermissionInfo>> results = new HashMap<String,List<KimPermissionInfo>>();
    	for ( KimPermissionImpl perm : permissions ) {
    		List<KimPermissionInfo> perms = results.get( perm.getTemplateId() );
    		if ( perms == null ) {
    			perms = new ArrayList<KimPermissionInfo>();
    			results.put( perm.getTemplateId(), perms );
    		}
    		perms.add( perm.toSimpleInfo() );
    	}
    	return results;
    }
    
	/**
     * Compare each of the passed in permissions with the given permissionDetails.  Those that
     * match are added to the result list.
     */
    protected List<KimPermissionInfo> getMatchingPermissions( List<KimPermissionImpl> permissions, AttributeSet permissionDetails ) {
    	List<KimPermissionInfo> applicablePermissions = new ArrayList<KimPermissionInfo>();    	
    	if ( permissionDetails == null || permissionDetails.isEmpty() ) {
    		// if no details passed, assume that all match
    		for ( KimPermissionImpl perm : permissions ) {
    			applicablePermissions.add( perm.toSimpleInfo() );
    		}
    	} else {
    		// otherwise, attempt to match the permission details
    		// build a map of the template IDs to the type services
    		Map<String,KimPermissionTypeService> permissionTypeServices = getPermissionTypeServicesByTemplateId( permissions );
    		// build a map of permissions by template ID
    		Map<String,List<KimPermissionInfo>> permissionMap = groupPermissionsByTemplate( permissions );
    		// loop over the different templates, matching all of the same template against the type
    		// service at once
    		for ( String templateId : permissionMap.keySet() ) {
    			KimPermissionTypeService permissionTypeService = permissionTypeServices.get( templateId );
    			List<KimPermissionInfo> permissionList = permissionMap.get( templateId );
				applicablePermissions.addAll( permissionTypeService.getMatchingPermissions( permissionDetails, permissionList ) );    				
    		}
    	}
    	return applicablePermissions;
    }

    /**
     * @see org.kuali.rice.kim.service.PermissionService#getPermissionAssignees(String, String, AttributeSet, AttributeSet)
     */
    public List<PermissionAssigneeInfo> getPermissionAssignees( String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	List<PermissionAssigneeInfo> results = new ArrayList<PermissionAssigneeInfo>();
    	List<String> roleIds = getRoleIdsForPermission( namespaceCode, permissionName, permissionDetails);
    	if ( roleIds.isEmpty() ) {
    		return results;
    	}
    	Collection<RoleMembershipInfo> roleMembers = getRoleService().getRoleMembers( roleIds, qualification );
    	for ( RoleMembershipInfo rm : roleMembers ) {
    		if ( rm.getMemberTypeCode().equals( Role.PRINCIPAL_MEMBER_TYPE ) ) {
    			results.add( new PermissionAssigneeInfo( rm.getMemberId(), null, rm.getDelegates() ) );
    		} else if ( rm.getMemberTypeCode().equals( Role.GROUP_MEMBER_TYPE ) ) {
    			results.add( new PermissionAssigneeInfo( null, rm.getMemberId(), rm.getDelegates() ) );
    		}
    	}
    	return results;
    }
    
    public List<PermissionAssigneeInfo> getPermissionAssigneesForTemplateName( String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	List<PermissionAssigneeInfo> results = new ArrayList<PermissionAssigneeInfo>();
    	List<String> roleIds = getRoleIdsForPermissionTemplate( namespaceCode, permissionTemplateName, permissionDetails);
    	if ( roleIds.isEmpty() ) {
    		return results;
    	}
    	Collection<RoleMembershipInfo> roleMembers = getRoleService().getRoleMembers( roleIds, qualification );
    	for ( RoleMembershipInfo rm : roleMembers ) {
    		if ( rm.getMemberTypeCode().equals( Role.PRINCIPAL_MEMBER_TYPE ) ) {
    			results.add( new PermissionAssigneeInfo( rm.getMemberId(), null, rm.getDelegates() ) );
    		} else { // a group membership
    			results.add( new PermissionAssigneeInfo( null, rm.getMemberId(), rm.getDelegates() ) );
    		}
    	}
    	return results;
    }
    
    public boolean isPermissionAssigned( String namespaceCode, String permissionName, AttributeSet permissionDetails ) {
    	return !getRoleIdsForPermission(namespaceCode, permissionName, permissionDetails).isEmpty();
    }
    
    public boolean isPermissionDefined( String namespaceCode, String permissionName, AttributeSet permissionDetails ) {
    	// get all the permission objects whose name match that requested
    	List<KimPermissionImpl> permissions = getPermissionImplsByName( namespaceCode, permissionName );
    	// now, filter the full list by the detail passed
    	return !getMatchingPermissions( permissions, permissionDetails ).isEmpty();   
    }
    
    public boolean isPermissionDefinedForTemplateName( String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails ) {
    	// get all the permission objects whose name match that requested
    	List<KimPermissionImpl> permissions = getPermissionImplsByTemplateName( namespaceCode, permissionTemplateName );
    	// now, filter the full list by the detail passed
    	return !getMatchingPermissions( permissions, permissionDetails ).isEmpty();   
    }

	protected List<String> getRolesForPermissionsFromCache( List<KimPermissionInfo> key ) {
    	List<String> roleIds = null; 
    	MaxAgeSoftReference<List<String>> cacheRef = permissionToRoleCache.get( key );
    	if ( cacheRef != null ) {
    		roleIds = cacheRef.get();
    	}
    	return roleIds;
    }

    protected void addRolesForPermissionsToCache( List<KimPermissionInfo> key, List<String> roleIds ) {
    	permissionToRoleCache.put( key, new MaxAgeSoftReference<List<String>>( CACHE_MAX_AGE_SECONDS, roleIds ) );
    }
 
    public List<String> getRoleIdsForPermission( String namespaceCode, String permissionName, AttributeSet permissionDetails) {
    	// get all the permission objects whose name match that requested
    	List<KimPermissionImpl> permissions = getPermissionImplsByName( namespaceCode, permissionName );
    	// now, filter the full list by the detail passed
    	List<KimPermissionInfo> applicablePermissions = getMatchingPermissions( permissions, permissionDetails );    	
    	List<String> roleIds = getRolesForPermissionsFromCache( applicablePermissions );
    	if ( roleIds == null ) {
    		roleIds = permissionDao.getRoleIdsForPermissions( applicablePermissions );
    		addRolesForPermissionsToCache( applicablePermissions, roleIds );
    	}
    	return roleIds;    	
    }

    protected List<String> getRoleIdsForPermissionTemplate( String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails ) {
    	// get all the permission objects whose name match that requested
    	List<KimPermissionImpl> permissions = getPermissionImplsByTemplateName( namespaceCode, permissionTemplateName );
    	// now, filter the full list by the detail passed
    	List<KimPermissionInfo> applicablePermissions = getMatchingPermissions( permissions, permissionDetails );
    	List<String> roleIds = getRolesForPermissionsFromCache( applicablePermissions );
    	if ( roleIds == null ) {
    		roleIds = permissionDao.getRoleIdsForPermissions( applicablePermissions );
    		addRolesForPermissionsToCache( applicablePermissions, roleIds );
    	}
    	return roleIds;
    }
    
    public List<String> getRoleIdsForPermissions( List<KimPermissionInfo> permissions ) {
    	List<String> roleIds = getRolesForPermissionsFromCache( permissions );
    	if ( roleIds == null ) {
    		roleIds = permissionDao.getRoleIdsForPermissions( permissions );
    		addRolesForPermissionsToCache( permissions, roleIds );
    	}
    	return roleIds;
    }

    // --------------------
    // Permission Data
    // --------------------
    
    /**
     * @see org.kuali.rice.kim.service.PermissionService#getPermission(java.lang.String)
     */
    public KimPermissionInfo getPermission(String permissionId) {
    	KimPermissionImpl impl = getPermissionImpl( permissionId );
    	if ( impl != null ) {
    		return impl.toSimpleInfo();
    	}
    	return null;
    }
    
    /**
     * @see org.kuali.rice.kim.service.PermissionService#getPermissionsByTemplateName(String, String)
     */
    public List<KimPermissionInfo> getPermissionsByTemplateName(String namespaceCode, String permissionTemplateName) {
    	List<KimPermissionImpl> impls = getPermissionImplsByTemplateName( namespaceCode, permissionTemplateName );
    	List<KimPermissionInfo> results = new ArrayList<KimPermissionInfo>( impls.size() );
    	for ( KimPermissionImpl impl : impls ) {
    		results.add( impl.toSimpleInfo() );
    	}
    	return results;
    }

	/**
     * @see org.kuali.rice.kim.service.PermissionService#getPermissionsByName(String, String)
     */
    public List<KimPermissionInfo> getPermissionsByName(String namespaceCode, String permissionName) {
    	List<KimPermissionImpl> impls = getPermissionImplsByName( namespaceCode, permissionName );
    	List<KimPermissionInfo> results = new ArrayList<KimPermissionInfo>( impls.size() );
    	for ( KimPermissionImpl impl : impls ) {
    		results.add( impl.toSimpleInfo() );
    	}
    	return results;
    }
    
	protected List<KimPermissionImpl> getPermissionsFromCache( String key ) {
    	List<KimPermissionImpl> permissions = null; 
    	MaxAgeSoftReference<List<KimPermissionImpl>> cacheRef = permissionCache.get( key );
    	if ( cacheRef != null ) {
    		permissions = cacheRef.get();
    	}
    	return permissions;
    }

    protected void addPermissionsToCache( String key, List<KimPermissionImpl> permissions ) {
    	permissionCache.put( key, new MaxAgeSoftReference<List<KimPermissionImpl>>( CACHE_MAX_AGE_SECONDS, permissions ) );
    }
    
    protected KimPermissionImpl getPermissionImpl(String permissionId) {
    	if ( StringUtils.isBlank( permissionId ) ) {
    		return null;
    	}
    	List<KimPermissionImpl> permissions = getPermissionsFromCache( permissionId );
    	if ( permissions == null ) {
	    	HashMap<String,Object> pk = new HashMap<String,Object>( 1 );
	    	pk.put( KimConstants.PrimaryKeyConstants.PERMISSION_ID, permissionId );
	    	permissions = new ArrayList<KimPermissionImpl>( 1 );
	    	permissions.add( (KimPermissionImpl)getBusinessObjectService().findByPrimaryKey( KimPermissionImpl.class, pk ) );
	    	addPermissionsToCache( permissionId, permissions );
    	}
    	return permissions.get( 0 );
    }
    
    @SuppressWarnings("unchecked")
	protected List<KimPermissionImpl> getPermissionImplsByTemplateName( String namespaceCode, String permissionTemplateName ) {
    	List<KimPermissionImpl> permissions = getPermissionsFromCache( namespaceCode+"-TEMPLATE-"+permissionTemplateName );
    	if ( permissions == null ) {    	
	    	HashMap<String,Object> pk = new HashMap<String,Object>( 3 );
	    	pk.put( "template.namespaceCode", namespaceCode );
	    	pk.put( "template.name", permissionTemplateName );
			pk.put( KNSPropertyConstants.ACTIVE, "Y" );
	    	permissions = (List<KimPermissionImpl>)getBusinessObjectService().findMatching( KimPermissionImpl.class, pk );
	    	addPermissionsToCache( namespaceCode+"-TEMPLATE-"+permissionTemplateName, permissions );
    	}
    	return permissions;
    }

    @SuppressWarnings("unchecked")
	protected List<KimPermissionImpl> getPermissionImplsByName( String namespaceCode, String permissionName ) {
    	List<KimPermissionImpl> permissions = getPermissionsFromCache( namespaceCode+"-"+permissionName );
    	if ( permissions == null ) {
	    	HashMap<String,Object> pk = new HashMap<String,Object>( 3 );
	    	pk.put( KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode );
	    	pk.put( KimConstants.UniqueKeyConstants.PERMISSION_NAME, permissionName );
			pk.put( KNSPropertyConstants.ACTIVE, "Y" );
	    	permissions = (List<KimPermissionImpl>)getBusinessObjectService().findMatching( KimPermissionImpl.class, pk );
	    	addPermissionsToCache( namespaceCode+"-"+permissionName, permissions );
    	}
    	return permissions;
    }
    
    // --------------------
    // Support Methods
    // --------------------
	
	protected BusinessObjectService getBusinessObjectService() {
		if ( businessObjectService == null ) {
			businessObjectService = KNSServiceLocator.getBusinessObjectService();
		}
		return businessObjectService;
	}
   
	protected RoleService getRoleService() {
		if ( roleService == null ) {
			roleService = KIMServiceLocator.getRoleManagementService();		
		}

		return roleService;
	}

	public KimPermissionDao getPermissionDao() {
		return this.permissionDao;
	}

	public void setPermissionDao(KimPermissionDao permissionDao) {
		this.permissionDao = permissionDao;
	}

	@SuppressWarnings("unchecked")
	public List<KimPermissionInfo> lookupPermissions(Map<String, String> searchCriteria, boolean unbounded ){
		Collection baseResults = null; 
		Lookupable permissionLookupable = KNSServiceLocator.getLookupable(
				KNSServiceLocator.getBusinessObjectDictionaryService().getLookupableID(PermissionImpl.class)
				);
		permissionLookupable.setBusinessObjectClass(PermissionImpl.class);
		if ( unbounded ) {
			baseResults = permissionLookupable.getSearchResultsUnbounded( searchCriteria );
		} else {
			baseResults = permissionLookupable.getSearchResults(searchCriteria);
		}
		List<KimPermissionInfo> results = new ArrayList<KimPermissionInfo>( baseResults.size() );
		for ( PermissionImpl resp : (Collection<PermissionImpl>)baseResults ) {
			results.add( resp.toSimpleInfo() );
		}
		if ( baseResults instanceof CollectionIncomplete ) {
			results = new CollectionIncomplete<KimPermissionInfo>( results, ((CollectionIncomplete<KimPermissionInfo>)baseResults).getActualSizeIfTruncated() ); 
		}		
		return results;
	}

	public String getPermissionDetailLabel( String permissionId, String kimTypeId, String attributeName) {
    	// get the type service for this permission
		KimPermissionTypeService typeService = getPermissionTypeService(null, null, null, permissionId);
		if ( typeService != null ) {
			// ask the type service for the attribute definition for the given attribute name
			AttributeDefinitionMap attributes = typeService.getAttributeDefinitions( kimTypeId );
			String label = null;
			for ( AttributeDefinition attributeDef : attributes.values() ) {
				if ( attributeDef.getName().equals(attributeName) ) {
					label = attributeDef.getLabel();
				}
			}
			// return the attribute label
			if ( label != null ) {
				return label;
			} else {
				return "Missing Def: " + attributeName;
			}
		} else {
			return "No Label: " + attributeName;
		}
	}
	
	/**
	 * @see org.kuali.rice.kim.service.PermissionService#getPermissionTemplate(java.lang.String)
	 */
	public KimPermissionTemplateInfo getPermissionTemplate(String permissionTemplateId) {
		KimPermissionTemplateImpl impl = getBusinessObjectService().findBySinglePrimaryKey( KimPermissionTemplateImpl.class, permissionTemplateId );
		if ( impl != null ) {
			return impl.toSimpleInfo();
		}
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.PermissionService#getPermissionTemplateByName(java.lang.String, java.lang.String)
	 */
	public KimPermissionTemplateInfo getPermissionTemplateByName(String namespaceCode,
			String permissionTemplateName) {
		Map<String,String> criteria = new HashMap<String,String>(2);
		criteria.put( KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode );
		criteria.put( KimConstants.UniqueKeyConstants.PERMISSION_TEMPLATE_NAME, permissionTemplateName );
		KimPermissionTemplateImpl impl = (KimPermissionTemplateImpl)getBusinessObjectService().findByPrimaryKey( KimPermissionTemplateImpl.class, criteria );
		if ( impl != null ) {
			return impl.toSimpleInfo();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<KimPermissionTemplateInfo> getAllTemplates() {
		if ( allTemplates == null ) {
			Map<String,String> criteria = new HashMap<String,String>(1);
			criteria.put( KNSPropertyConstants.ACTIVE, "Y" );
			List<KimPermissionTemplateImpl> impls = (List<KimPermissionTemplateImpl>)getBusinessObjectService().findMatching( KimPermissionTemplateImpl.class, criteria );
			List<KimPermissionTemplateInfo> infos = new ArrayList<KimPermissionTemplateInfo>( impls.size() );
			for ( KimPermissionTemplateImpl impl : impls ) {
				infos.add( impl.toSimpleInfo() );
			}
			Collections.sort(infos, new Comparator<KimPermissionTemplateInfo>() {
				public int compare(KimPermissionTemplateInfo tmpl1,
						KimPermissionTemplateInfo tmpl2) {
					int result = 0;
					result = tmpl1.getNamespaceCode().compareTo(tmpl2.getNamespaceCode());
					if ( result != 0 ) {
						return result;
					}
					result = tmpl1.getName().compareTo(tmpl2.getName());
					return result;
				}
			});
			allTemplates = infos;
		}
		return allTemplates;
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.PermissionUpdateService#savePermission(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	public void savePermission(String permissionId, String permissionTemplateId,
			String namespaceCode, String name, String description, boolean active,
			AttributeSet permissionDetails) {
    	// look for an existing permission of the given type
    	try {
	    	KimPermissionImpl perm = getBusinessObjectService().findBySinglePrimaryKey(KimPermissionImpl.class, permissionId);
	    	if ( perm == null ) {
	    		perm = new KimPermissionImpl();
	    		perm.setPermissionId(permissionId);
	    	}
	    	perm.setTemplateId(permissionTemplateId);
	    	perm.refreshReferenceObject( "template" );
	    	perm.setNamespaceCode(namespaceCode);
	    	perm.setName(name);
	    	perm.setDescription(description);
	    	perm.setActive(active);
	    	AttributeSet attributesToAdd = new AttributeSet( permissionDetails );
	    	List<PermissionAttributeDataImpl> details = perm.getDetailObjects();
	    	Iterator<PermissionAttributeDataImpl> detailIter = details.iterator();
	    	while ( detailIter.hasNext() ) {
	    		PermissionAttributeDataImpl detail = detailIter.next();
	    		String attrName = detail.getKimAttribute().getAttributeName();
	    		String attrValue = attributesToAdd.get(attrName);
	    		// if not present in the list or is blank, remove from the list
	    		if ( StringUtils.isBlank(attrValue) ) {
	    			detailIter.remove();
	    		} else {
	    			detail.setAttributeValue(attrValue);
	    		}
	    		// remove from detail map - used to add new ones later
	    		attributesToAdd.remove(attrName);
	    	}
	    	for ( String attrName : attributesToAdd.keySet() ) {
	    		KimTypeAttributeInfo attr = perm.getTemplate().getKimType().getAttributeDefinitionByName(attrName);
	    		if ( attr != null ) {
		    		PermissionAttributeDataImpl newDetail = new PermissionAttributeDataImpl();
		    		newDetail.setAttributeDataId(getNewAttributeDataId());
		    		newDetail.setKimAttributeId(attr.getKimAttributeId());
		    		newDetail.setKimTypeId(perm.getTemplate().getKimTypeId());
		    		newDetail.setPermissionId(permissionId);
		    		newDetail.setAttributeValue(attributesToAdd.get(attrName));
		    		details.add(newDetail);
	    		} else {
	    			LOG.error( "Unknown attribute name saving permission: '" + attrName + "'" );
	    		}
	    	}
	    	getBusinessObjectService().save(perm);
	    	KIMServiceLocator.getIdentityManagementService().flushPermissionCaches();
    	} catch ( RuntimeException ex ) {
    		LOG.error( "Exception in savePermission: ", ex );
    		throw ex;
    	}
	}

    protected String getNewAttributeDataId(){
		SequenceAccessorService sas = getSequenceAccessorService();		
		Long nextSeq = sas.getNextAvailableSequenceNumber(
				KimConstants.SequenceNames.KRIM_ATTR_DATA_ID_S, 
				RoleMemberAttributeDataImpl.class );
		return nextSeq.toString();
    }
	
	private DataDictionaryService dataDictionaryService;
	protected DataDictionaryService getDataDictionaryService() {
		if(dataDictionaryService == null){
			dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
		}
		return dataDictionaryService;
	}
	protected SequenceAccessorService getSequenceAccessorService() {
		if ( sequenceAccessorService == null ) {
			sequenceAccessorService = KNSServiceLocator.getSequenceAccessorService();
		}
		return sequenceAccessorService;
	}
	
}
