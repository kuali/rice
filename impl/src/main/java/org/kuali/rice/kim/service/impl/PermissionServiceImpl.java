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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.impl.PermissionImpl;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.KimPermissionTemplateInfo;
import org.kuali.rice.kim.bo.role.dto.PermissionAssigneeInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.bo.role.impl.KimPermissionTemplateImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.dao.KimPermissionDao;
import org.kuali.rice.kim.service.KIMServiceLocatorInternal;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.support.KimPermissionTypeService;
import org.kuali.rice.kim.util.KIMWebServiceConstants;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.lookup.Lookupable;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.KRADPropertyConstants;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@WebService(endpointInterface = KIMWebServiceConstants.PermissionService.INTERFACE_CLASS, serviceName = KIMWebServiceConstants.PermissionService.WEB_SERVICE_NAME, portName = KIMWebServiceConstants.PermissionService.WEB_SERVICE_PORT, targetNamespace = KIMWebServiceConstants.MODULE_TARGET_NAMESPACE)
public class PermissionServiceImpl extends PermissionServiceBase implements PermissionService {
	private static final Logger LOG = Logger.getLogger( PermissionServiceImpl.class );

   	private RoleService roleService;
	private KimPermissionDao permissionDao;
    private KimPermissionTypeService defaultPermissionTypeService;
	
	private List<KimPermissionTemplateInfo> allTemplates;
	
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
		KimPermissionTypeService service = getPermissionTypeServiceByNameCache().get(key);
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
			getPermissionTypeServiceByNameCache().put(key, service);
		}
		return service;
	}

    protected KimPermissionTypeService getPermissionTypeService( KimPermissionTemplateImpl permissionTemplate ) {
    	if ( permissionTemplate == null ) {
    		throw new IllegalArgumentException( "permissionTemplate may not be null" );
    	}
    	KimType kimType = KimApiServiceLocator.getKimTypeInfoService().getKimType( permissionTemplate.getKimTypeId() );
    	String serviceName = kimType.getServiceName();
    	// if no service specified, return a default implementation
    	if ( StringUtils.isBlank( serviceName ) ) {
    		return getDefaultPermissionTypeService();
    	}
    	try {
	    	Object service = KIMServiceLocatorInternal.getService(serviceName);
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
    		throw new RuntimeException( "Error retrieving service: " + serviceName + " from the KIMServiceLocatorInternal.", ex );
    	}
    }
    
    protected KimPermissionTypeService getDefaultPermissionTypeService() {
    	if ( defaultPermissionTypeService == null ) {
    		defaultPermissionTypeService = (KimPermissionTypeService) KIMServiceLocatorInternal.getBean(DEFAULT_PERMISSION_TYPE_SERVICE);
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
    
    @SuppressWarnings("unchecked")
	protected KimPermissionImpl getPermissionImpl(String permissionId) {
    	if ( StringUtils.isBlank( permissionId ) ) {
    		return null;
    	}
    	String cacheKey = getPermissionImplByIdCacheKey(permissionId);
    	List<KimPermissionImpl> permissions = (List<KimPermissionImpl>)getCacheAdministrator().getFromCache(cacheKey, getRefreshPeriodInSeconds());
    	if ( permissions == null ) {
	    	HashMap<String,Object> pk = new HashMap<String,Object>( 1 );
	    	pk.put( KimConstants.PrimaryKeyConstants.PERMISSION_ID, permissionId );
	    	permissions = Collections.singletonList( (KimPermissionImpl)getBusinessObjectService().findByPrimaryKey( KimPermissionImpl.class, pk ) );
	    	getCacheAdministrator().putInCache(cacheKey, permissions, PERMISSION_IMPL_CACHE_GROUP);
    	}
    	return permissions.get( 0 );
    }
    
    @SuppressWarnings("unchecked")
	protected List<KimPermissionImpl> getPermissionImplsByTemplateName( String namespaceCode, String permissionTemplateName ) {
    	String cacheKey = getPermissionImplByTemplateNameCacheKey(namespaceCode, permissionTemplateName);
    	List<KimPermissionImpl> permissions = (List<KimPermissionImpl>)getCacheAdministrator().getFromCache(cacheKey, getRefreshPeriodInSeconds());
    	if ( permissions == null ) {    	
	    	HashMap<String,Object> pk = new HashMap<String,Object>( 3 );
	    	pk.put( "template.namespaceCode", namespaceCode );
	    	pk.put( "template.name", permissionTemplateName );
			pk.put( KRADPropertyConstants.ACTIVE, "Y" );
	    	permissions = (List<KimPermissionImpl>)getBusinessObjectService().findMatching( KimPermissionImpl.class, pk );
	    	getCacheAdministrator().putInCache(cacheKey, permissions, PERMISSION_IMPL_CACHE_GROUP);
    	}
    	return permissions;
    }

    @SuppressWarnings("unchecked")
	protected List<KimPermissionImpl> getPermissionImplsByName( String namespaceCode, String permissionName ) {
    	String cacheKey = getPermissionImplByNameCacheKey(namespaceCode, permissionName);
    	List<KimPermissionImpl> permissions = (List<KimPermissionImpl>)getCacheAdministrator().getFromCache(cacheKey, getRefreshPeriodInSeconds());
    	if ( permissions == null ) {
	    	HashMap<String,Object> pk = new HashMap<String,Object>( 3 );
	    	pk.put( KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode );
	    	pk.put( KimConstants.UniqueKeyConstants.PERMISSION_NAME, permissionName );
			pk.put( KRADPropertyConstants.ACTIVE, "Y" );
	    	permissions = (List<KimPermissionImpl>)getBusinessObjectService().findMatching( KimPermissionImpl.class, pk );
	    	getCacheAdministrator().putInCache(cacheKey, permissions, PERMISSION_IMPL_CACHE_GROUP);
    	}
    	return permissions;
    }

    
    
    // --------------------
    // Support Methods
    // --------------------
	
	
	protected RoleService getRoleService() {
		if ( roleService == null ) {
			roleService = KimApiServiceLocator.getRoleManagementService();
		}

		return roleService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
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
		Lookupable permissionLookupable = KRADServiceLocatorWeb.getLookupable(
                KRADServiceLocatorWeb.getBusinessObjectDictionaryService().getLookupableID(PermissionImpl.class));
		permissionLookupable.setBusinessObjectClass(PermissionImpl.class);
		if ( unbounded ) {
		    baseResults = permissionLookupable.getSearchResultsUnbounded( searchCriteria );
		} else {
			baseResults = permissionLookupable.getSearchResults(searchCriteria);
		}
		List<KimPermissionInfo> results = new ArrayList<KimPermissionInfo>( baseResults.size() );
		for ( KimPermissionImpl resp : (Collection<PermissionImpl>)baseResults ) {
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
			criteria.put( KRADPropertyConstants.ACTIVE, "Y" );
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

    
	
	private DataDictionaryService dataDictionaryService;
	protected DataDictionaryService getDataDictionaryService() {
		if(dataDictionaryService == null){
			dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
		}
		return dataDictionaryService;
	}
	

    public List<String> getRoleIdsForPermissionId(String permissionId) {
        KimPermissionInfo permissionInfo = getPermission(permissionId);

        List<KimPermissionInfo> applicablePermissions = new ArrayList<KimPermissionInfo>();
        applicablePermissions.add(permissionInfo);

        List<String> roleIds = getRolesForPermissionsFromCache(applicablePermissions);
        if (roleIds == null) {
            roleIds = permissionDao.getRoleIdsForPermissions(applicablePermissions);
            addRolesForPermissionsToCache(applicablePermissions, roleIds);
        }

        return roleIds;
    }

    public List<KimPermissionInfo> getPermissionsByNameIncludingInactive(String namespaceCode, String permissionName) {
        List<KimPermissionImpl> impls = getPermissionImplsByNameIncludingInactive(namespaceCode, permissionName);
        List<KimPermissionInfo> results = new ArrayList<KimPermissionInfo>(impls.size());
        for (KimPermissionImpl impl : impls) {
            results.add(impl.toSimpleInfo());
        }
        return results;
    }
	
    @SuppressWarnings("unchecked")
    protected List<KimPermissionImpl> getPermissionImplsByNameIncludingInactive(String namespaceCode, String permissionName) {
        String cacheKey = getPermissionImplByNameCacheKey(namespaceCode, permissionName + "inactive");
        List<KimPermissionImpl> permissions = (List<KimPermissionImpl>) getCacheAdministrator().getFromCache(cacheKey, getRefreshPeriodInSeconds());
        if (permissions == null) {
            HashMap<String, Object> pk = new HashMap<String, Object>(2);
            pk.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode);
            pk.put(KimConstants.UniqueKeyConstants.PERMISSION_NAME, permissionName);
            permissions = (List<KimPermissionImpl>) getBusinessObjectService().findMatching(KimPermissionImpl.class, pk);
            getCacheAdministrator().putInCache(cacheKey, permissions, PERMISSION_IMPL_CACHE_GROUP);
        }
        return permissions;
    }
    
    public int getRefreshPeriodInSeconds() {
        try {
            return (int)(new Integer(ConfigContext.getCurrentContextConfig().getProperty(KimConstants.CacheRefreshPeriodSeconds.KIM_CACHE_PERMISSION_REFRESH_PERIOD_SECONDS)));
        } catch (NumberFormatException e) {
    		// The cache will never expire when refreshPeriod is set to -1
    		return -1;        		
        }
    }
}
