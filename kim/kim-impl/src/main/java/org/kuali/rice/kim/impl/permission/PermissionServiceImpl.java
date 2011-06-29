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
package org.kuali.rice.kim.impl.permission;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.core.api.mo.common.Attributes;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.api.common.assignee.Assignee;
import org.kuali.rice.kim.api.common.delegate.Delegate;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.permission.PermissionTypeService;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.service.KIMServiceLocatorWeb;
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
	private PermissionDao permissionDao;
    private PermissionTypeService defaultPermissionTypeService;
    private DataDictionaryService dataDictionaryService;
	
 	private List<Template> allTemplates;
	
    // --------------------
    // Authorization Checks
    // --------------------
    
	protected PermissionTypeService getPermissionTypeService( String namespaceCode, String permissionTemplateName, String permissionName, String permissionId ) {
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
		PermissionTypeService service = getPermissionTypeServiceByNameCache().get(key);
		if ( service == null ) {
			PermissionTemplateBo permTemplate = null;
			if ( permissionTemplateName != null ) {
				PermissionBo perms = getPermissionImplsByTemplateName(namespaceCode, permissionTemplateName);
					permTemplate = perms.getTemplate();
			} else if ( permissionName != null ) {
				PermissionBo perms = getPermissionImplsByName(namespaceCode, permissionName);
					permTemplate = perms.getTemplate();
			} else if ( permissionId != null ) {
				PermissionBo perm = getPermissionImpl(permissionId);
				if ( perm != null ) {
					permTemplate = perm.getTemplate();
				}
			}
			service = getPermissionTypeService( permTemplate );
			getPermissionTypeServiceByNameCache().put(key, service);
		}
		return service;
	}

    protected PermissionTypeService getPermissionTypeService( PermissionTemplateBo permissionTemplate ) {
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
	    	Object service = KIMServiceLocatorWeb.getService(serviceName);
	    	// if we have a service name, it must exist
	    	if ( service == null ) {
				throw new RuntimeException("null returned for permission type service for service name: " + serviceName);
	    	}
	    	// whatever we retrieved must be of the correct type
	    	if ( !(service instanceof PermissionTypeService)  ) {
	    		throw new RuntimeException( "Service " + serviceName + " was not a PermissionTypeService.  Was: " + service.getClass().getName() );
	    	}
	    	return (PermissionTypeService)service;
    	} catch( Exception ex ) {
    		// sometimes service locators throw exceptions rather than returning null, handle that
    		throw new RuntimeException( "Error retrieving service: " + serviceName + " from the KIMServiceLocatorInternal.", ex );
    	}
    }
    
    protected PermissionTypeService getDefaultPermissionTypeService() {
    	if ( defaultPermissionTypeService == null ) {
    		//TODO getBean() -> getService()?
    		defaultPermissionTypeService = (PermissionTypeService) KIMServiceLocatorWeb.getService(DEFAULT_PERMISSION_TYPE_SERVICE);
    	}
		return defaultPermissionTypeService;
	}
	
    /**
     * @see org.kuali.rice.kim.service.PermissionService#hasPermission(java.lang.String, String, java.lang.String, AttributeSet)
     */
    public boolean hasPermission(String principalId, String namespaceCode, String permissionName, Attributes permissionDetails) {
    	return isAuthorized( principalId, namespaceCode, permissionName, permissionDetails, null );
    }

    /**
     * @see org.kuali.rice.kim.service.PermissionService#isAuthorized( java.lang.String, String, java.lang.String, AttributeSet, AttributeSet)
     */
    public boolean isAuthorized(String principalId, String namespaceCode, String permissionName, Attributes permissionDetails, Attributes qualification ) {
    	List<String> roleIds = getRoleIdsForPermission( namespaceCode, permissionName, permissionDetails );
    	if ( roleIds.isEmpty() ) {
    		return false;
    	}
		return getRoleService().principalHasRole( principalId, roleIds, new AttributeSet(qualification.toMap()));
    }

    /**
     * @see org.kuali.rice.kim.service.PermissionService#hasPermission(String, String, String, AttributeSet)
     */
    public boolean hasPermissionByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, Attributes permissionDetails) {
    	return isAuthorizedByTemplateName( principalId, namespaceCode, permissionTemplateName, permissionDetails, null );
    }

    /**
     * @see org.kuali.rice.kim.service.PermissionService#isAuthorized( java.lang.String, String, java.lang.String, AttributeSet, AttributeSet)
     */
    public boolean isAuthorizedByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, Attributes permissionDetails, Attributes qualification ) {
    	List<String> roleIds = getRoleIdsForPermissionTemplate( namespaceCode, permissionTemplateName, permissionDetails );
    	if ( roleIds.isEmpty() ) {
    		return false;
    	}
    	return getRoleService().principalHasRole( principalId, roleIds, new AttributeSet(qualification.toMap()));
    }

    /**
     * @see org.kuali.rice.kim.service.PermissionService#getAuthorizedPermissions(String, String, String, AttributeSet, AttributeSet)
     */
    public List<Permission> getAuthorizedPermissions( String principalId, String namespaceCode, String permissionName, Attributes permissionDetails, Attributes qualification ) {
    	// get all the permission objects whose name match that requested
    	PermissionBo permissions = getPermissionImplsByName( namespaceCode, permissionName );
    	// now, filter the full list by the detail passed
    	List<Permission> applicablePermissions = getMatchingPermissions( Collections.singletonList(permissions), permissionDetails );
    	return getPermissionsForUser(principalId, applicablePermissions, qualification);
    }

    /**
     * @see org.kuali.rice.kim.service.PermissionService#getAuthorizedPermissionsByTemplateName(String, String, String, AttributeSet, AttributeSet)
     */
    public List<Permission> getAuthorizedPermissionsByTemplateName( String principalId, String namespaceCode, String permissionTemplateName, Attributes permissionDetails, Attributes qualification ) {
    	// get all the permission objects whose name match that requested
    	PermissionBo permissions = getPermissionImplsByTemplateName( namespaceCode, permissionTemplateName );
    	// now, filter the full list by the detail passed
    	List<Permission> applicablePermissions = getMatchingPermissions( Collections.singletonList(permissions), permissionDetails );
    	return getPermissionsForUser(principalId, applicablePermissions, qualification);
    }
    
    /**
     * Checks the list of permissions against the principal's roles and returns a subset of the list which match.
     */
    protected List<Permission> getPermissionsForUser( String principalId, List<Permission> permissions, Attributes qualification ) {
    	ArrayList<Permission> results = new ArrayList<Permission>();
    	List<Permission> tempList = new ArrayList<Permission>(1);
    	for ( Permission perm : permissions ) {
    		tempList.clear();
    		tempList.add( perm );
    		List<String> roleIds = permissionDao.getRoleIdsForPermissions( tempList );
    		// TODO: This could be made a little better by collecting the role IDs into
    		// a set and then processing the distinct list rather than a check
    		// for every permission
    		if ( roleIds != null && !roleIds.isEmpty() ) {
    			if ( getRoleService().principalHasRole( principalId, roleIds, new AttributeSet(qualification.toMap())) ) {
    				results.add( perm );
    			}
    		}
    	}
    	
    	return results;    	
    }

    protected Map<String,PermissionTypeService> getPermissionTypeServicesByTemplateId( Collection<PermissionBo> permissions ) {
    	Map<String,PermissionTypeService> permissionTypeServices = new HashMap<String, PermissionTypeService>( permissions.size() );
    	for ( PermissionBo perm : permissions ) {
    		permissionTypeServices.put(perm.getTemplateId(), getPermissionTypeService( perm.getTemplate() ) );    				
    	}
    	return permissionTypeServices;
    }
    
    @SuppressWarnings("static-access")
	protected Map<String,List<Permission>> groupPermissionsByTemplate( Collection<PermissionBo> permissions ) {
    	Map<String,List<Permission>> results = new HashMap<String,List<Permission>>();
    	for ( PermissionBo perm : permissions ) {
    		List<Permission> perms = results.get( perm.getTemplateId() );
    		if ( perms == null ) {
    			perms = new ArrayList<Permission>();
    			results.put( perm.getTemplateId(), perms );
    		}
    		perms.add( perm.to(perm) );
    	}
    	return results;
    }
    
	/**
     * Compare each of the passed in permissions with the given permissionDetails.  Those that
     * match are added to the result list.
     */
    protected List<Permission> getMatchingPermissions( List<PermissionBo> permissions, Attributes permissionDetails ) {
    	List<Permission> applicablePermissions = new ArrayList<Permission>();    	
    	if ( permissionDetails == null || permissionDetails.isEmpty() ) {
    		// if no details passed, assume that all match
    		for ( PermissionBo perm : permissions ) {
    			applicablePermissions.add( PermissionBo.to(perm) );
    		}
    	} else {
    		// otherwise, attempt to match the permission details
    		// build a map of the template IDs to the type services
    		Map<String,PermissionTypeService> permissionTypeServices = getPermissionTypeServicesByTemplateId( permissions );
    		// build a map of permissions by template ID
    		Map<String,List<Permission>> permissionMap = groupPermissionsByTemplate( permissions );
    		// loop over the different templates, matching all of the same template against the type
    		// service at once
    		for ( String templateId : permissionMap.keySet() ) {
    			PermissionTypeService permissionTypeService = permissionTypeServices.get( templateId );
    			List<Permission> permissionList = permissionMap.get( templateId );
				applicablePermissions.addAll( permissionTypeService.getMatchingPermissions( permissionDetails, permissionList ) );    				
    		}
    	}
    	return applicablePermissions;
    }

    /**
     * @see org.kuali.rice.kim.service.PermissionService#getPermissionAssignees(String, String, AttributeSet, AttributeSet)
     */
    public List<Assignee> getPermissionAssignees( String namespaceCode, String permissionName, Attributes permissionDetails, Attributes qualification ) {
    	List<Assignee> results = new ArrayList<Assignee>();
    	List<String> roleIds = getRoleIdsForPermission( namespaceCode, permissionName, permissionDetails);
    	if ( roleIds.isEmpty() ) {
    		return results;
    	}
    	Collection<RoleMembership> roleMembers = getRoleService().getRoleMembers( roleIds, new AttributeSet(qualification.toMap()) );
    	for ( RoleMembership rm : roleMembers ) {
			List<Delegate.Builder> delegateBuilderList = new ArrayList<Delegate.Builder>();
			if (!rm.getDelegates().isEmpty()) {
    			for (Delegate delegate : rm.getDelegates()){        				
    				delegateBuilderList.add(Delegate.Builder.create(delegate.getDelegationId(), delegate.getDelegationTypeCode(), delegate.getMemberId(), delegate.getMemberTypeCode(), delegate.getRoleMemberId(), delegate.getQualifier()));
    			}
			}
    		if ( rm.getMemberTypeCode().equals( Role.PRINCIPAL_MEMBER_TYPE ) ) {
    			results.add (Assignee.Builder.create(rm.getMemberId(), null, delegateBuilderList).build());
    		} else if ( rm.getMemberTypeCode().equals( Role.GROUP_MEMBER_TYPE ) ) {
    			results.add (Assignee.Builder.create(null, rm.getMemberId(), delegateBuilderList).build());
    		}
    	}
    	return results;
    }
    
    public List<Assignee> getPermissionAssigneesForTemplateName( String namespaceCode, String permissionTemplateName, Attributes permissionDetails, Attributes qualification ) {
    	List<Assignee> results = new ArrayList<Assignee>();
    	List<String> roleIds = getRoleIdsForPermissionTemplate( namespaceCode, permissionTemplateName, permissionDetails);
    	if ( roleIds.isEmpty() ) {
    		return results;
    	}
    	Collection<RoleMembership> roleMembers = getRoleService().getRoleMembers( roleIds, new AttributeSet(qualification.toMap()));
    	for ( RoleMembership rm : roleMembers ) {
			List<Delegate.Builder> delegateBuilderList = new ArrayList<Delegate.Builder>();
			if (!rm.getDelegates().isEmpty()) {
    			for (Delegate delegate : rm.getDelegates()){        				
    				delegateBuilderList.add(Delegate.Builder.create(delegate.getDelegationId(), delegate.getDelegationTypeCode(), delegate.getMemberId(), delegate.getMemberTypeCode(), delegate.getRoleMemberId(), delegate.getQualifier()));
    			}
			}
    		if ( rm.getMemberTypeCode().equals( Role.PRINCIPAL_MEMBER_TYPE ) ) {
    			results.add (Assignee.Builder.create(rm.getMemberId(), null, delegateBuilderList).build());
    		} else { // a group membership
    			results.add (Assignee.Builder.create(null, rm.getMemberId(), delegateBuilderList).build());
    		}
    	}
    	return results;
    }
    
    public boolean isPermissionAssigned( String namespaceCode, String permissionName, Attributes permissionDetails ) {
    	return !getRoleIdsForPermission(namespaceCode, permissionName, permissionDetails).isEmpty();
    }
    
    public boolean isPermissionDefined( String namespaceCode, String permissionName, Attributes permissionDetails ) {
    	// get all the permission objects whose name match that requested
    	PermissionBo permissions = getPermissionImplsByName( namespaceCode, permissionName );
    	// now, filter the full list by the detail passed
    	return !getMatchingPermissions( Collections.singletonList(permissions), permissionDetails ).isEmpty();
    }
    
    public boolean isPermissionDefinedForTemplateName( String namespaceCode, String permissionTemplateName, Attributes permissionDetails ) {
    	// get all the permission objects whose name match that requested
    	PermissionBo permissions = getPermissionImplsByTemplateName( namespaceCode, permissionTemplateName );
    	// now, filter the full list by the detail passed
    	return !getMatchingPermissions( Collections.singletonList(permissions), permissionDetails ).isEmpty();
    }
 
    public List<String> getRoleIdsForPermission( String namespaceCode, String permissionName, Attributes permissionDetails) {
    	// get all the permission objects whose name match that requested
    	PermissionBo permissions = getPermissionImplsByName( namespaceCode, permissionName );
    	// now, filter the full list by the detail passed
    	List<Permission> applicablePermissions = getMatchingPermissions( Collections.singletonList(permissions), permissionDetails );
    	List<String> roleIds = getRolesForPermissionsFromCache( applicablePermissions );
    	if ( roleIds == null ) {
    		roleIds = permissionDao.getRoleIdsForPermissions( applicablePermissions );
    		addRolesForPermissionsToCache( applicablePermissions, roleIds );
    	}
    	return roleIds;    	
    }

    protected List<String> getRoleIdsForPermissionTemplate( String namespaceCode, String permissionTemplateName, Attributes permissionDetails ) {
    	// get all the permission objects whose name match that requested
    	PermissionBo permissions = getPermissionImplsByTemplateName( namespaceCode, permissionTemplateName );
    	// now, filter the full list by the detail passed
    	List<Permission> applicablePermissions = getMatchingPermissions( Collections.singletonList(permissions), permissionDetails );
    	List<String> roleIds = getRolesForPermissionsFromCache( applicablePermissions );
    	if ( roleIds == null ) {
    		roleIds = permissionDao.getRoleIdsForPermissions( applicablePermissions );
    		addRolesForPermissionsToCache( applicablePermissions, roleIds );
    	}
    	return roleIds;
    }
    
    public List<String> getRoleIdsForPermissions( List<Permission> permissions ) {
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
    public Permission getPermission(String permissionId) {
    	PermissionBo impl = getPermissionImpl( permissionId );
    	if ( impl != null ) {
    		return PermissionBo.to(impl);
    	}
    	return null;
    }
    
    /**
     * @see org.kuali.rice.kim.service.PermissionService#getPermissionsByTemplateName(String, String)
     */
    public Permission getPermissionsByTemplateName(String namespaceCode, String permissionTemplateName) {
    	PermissionBo impls = getPermissionImplsByTemplateName( namespaceCode, permissionTemplateName );
    	return PermissionBo.to(impls);
    }

	/**
     * @see org.kuali.rice.kim.service.PermissionService#getPermissionsByName(String, String)
     */
    public Permission getPermissionsByName(String namespaceCode, String permissionName) {
    	PermissionBo impls = getPermissionImplsByName( namespaceCode, permissionName );
    	return PermissionBo.to(impls);
    }
    
    @SuppressWarnings("unchecked")
	protected PermissionBo getPermissionImpl(String permissionId) {
    	if ( StringUtils.isBlank( permissionId ) ) {
    		return null;
    	}
    	String cacheKey = getPermissionImplByIdCacheKey(permissionId);
    	List<PermissionBo> permissions = (List<PermissionBo>) cacheAdministrator.getFromCache(cacheKey);
    	if ( permissions == null ) {
	    	HashMap<String,Object> pk = new HashMap<String,Object>( 1 );
	    	pk.put( KimConstants.PrimaryKeyConstants.PERMISSION_ID, permissionId );
	    	permissions = Collections.singletonList( (PermissionBo) businessObjectService.findByPrimaryKey( PermissionBo.class, pk ) );
	    	cacheAdministrator.putInCache(cacheKey, permissions, PERMISSION_IMPL_CACHE_GROUP);
    	}
    	return permissions.get( 0 );
    }
    
    @SuppressWarnings("unchecked")
	protected PermissionBo getPermissionImplsByTemplateName( String namespaceCode, String permissionTemplateName ) {
    	String cacheKey = getPermissionImplByTemplateNameCacheKey(namespaceCode, permissionTemplateName);
    	PermissionBo permissions = (PermissionBo) cacheAdministrator.getFromCache(cacheKey);
    	if ( permissions == null ) {    	
	    	HashMap<String,Object> pk = new HashMap<String,Object>( 3 );
	    	pk.put( "template.namespaceCode", namespaceCode );
	    	pk.put( "template.name", permissionTemplateName );
			pk.put( KRADPropertyConstants.ACTIVE, "Y" );
	    	permissions = ((List<PermissionBo>) businessObjectService.findMatching( PermissionBo.class, pk )).get(0);
	    	cacheAdministrator.putInCache(cacheKey, permissions, PERMISSION_IMPL_CACHE_GROUP);
    	}
    	return permissions;
    }

    @SuppressWarnings("unchecked")
	protected PermissionBo getPermissionImplsByName( String namespaceCode, String permissionName ) {
    	String cacheKey = getPermissionImplByNameCacheKey(namespaceCode, permissionName);
    	PermissionBo permissions = (PermissionBo) cacheAdministrator.getFromCache(cacheKey);
    	if ( permissions == null ) {
	    	HashMap<String,Object> pk = new HashMap<String,Object>( 3 );
	    	pk.put( KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode );
	    	pk.put( KimConstants.UniqueKeyConstants.PERMISSION_NAME, permissionName );
			pk.put( KRADPropertyConstants.ACTIVE, "Y" );
	    	permissions = ((List<PermissionBo>) businessObjectService.findMatching( PermissionBo.class, pk )).get(0);
	    	cacheAdministrator.putInCache(cacheKey, permissions, PERMISSION_IMPL_CACHE_GROUP);
    	}
    	return permissions;
    }

    
    
    // --------------------
    // Support Methods
    // --------------------
	
	
	protected RoleService getRoleService() {
		if ( roleService == null ) {
			roleService = (RoleService) KimApiServiceLocator.getRoleManagementService();
		}

		return roleService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	public PermissionDao getPermissionDao() {
		return this.permissionDao;
	}

	public void setPermissionDao(PermissionDao permissionDao) {
		this.permissionDao = permissionDao;
	}

	@SuppressWarnings("unchecked")
	public List<Permission> lookupPermissions(Map<String, String> searchCriteria, boolean unbounded ){
		Collection baseResults = null;
		Lookupable permissionLookupable = KRADServiceLocatorWeb.getLookupable(
				KRADServiceLocatorWeb.getBusinessObjectDictionaryService().getLookupableID(PermissionBo.class)
        );
		permissionLookupable.setBusinessObjectClass(PermissionBo.class);
		if ( unbounded ) {
		    baseResults = permissionLookupable.getSearchResultsUnbounded( searchCriteria );
		} else {
			baseResults = permissionLookupable.getSearchResults(searchCriteria);
		}
		List<Permission> results = new ArrayList<Permission>( baseResults.size() );
		for ( PermissionBo resp : (Collection<PermissionBo>)baseResults ) {
			results.add( PermissionBo.to(resp) );
		}
		if ( baseResults instanceof CollectionIncomplete ) {
			results = new CollectionIncomplete<Permission>( results, ((CollectionIncomplete<Permission>)baseResults).getActualSizeIfTruncated() ); 
		}		
		return results;
	}

	public String getPermissionDetailLabel( String permissionId, String kimTypeId, String attributeName) {
    	// get the type service for this permission
		PermissionTypeService typeService = getPermissionTypeService(null, null, null, permissionId);
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
	public Template getPermissionTemplate(String permissionTemplateId) {
		PermissionTemplateBo impl = businessObjectService.findBySinglePrimaryKey( PermissionTemplateBo.class, permissionTemplateId );
		if ( impl != null ) {
			return PermissionTemplateBo.to(impl);
		}
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.service.PermissionService#getPermissionTemplateByName(java.lang.String, java.lang.String)
	 */
	public Template getPermissionTemplateByName(String namespaceCode,
			String permissionTemplateName) {
		Map<String,String> criteria = new HashMap<String,String>(2);
		criteria.put( KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode );
		criteria.put( KimConstants.UniqueKeyConstants.PERMISSION_TEMPLATE_NAME, permissionTemplateName );
		PermissionTemplateBo impl = (PermissionTemplateBo) businessObjectService.findByPrimaryKey( PermissionTemplateBo.class, criteria );
		if ( impl != null ) {
			return PermissionTemplateBo.to(impl);
		}
		return null;
	}
	
	public List<Template> getAllTemplates() {
		if ( allTemplates == null ) {
			Map<String,String> criteria = new HashMap<String,String>(1);
			criteria.put( KRADPropertyConstants.ACTIVE, "Y" );
			List<PermissionTemplateBo> impls = (List<PermissionTemplateBo>) businessObjectService.findMatching( PermissionTemplateBo.class, criteria );
			List<Template> infos = new ArrayList<Template>( impls.size() );
			for ( PermissionTemplateBo impl : impls ) {
				infos.add( PermissionTemplateBo.to(impl) );
			}
			Collections.sort(infos, new Comparator<Template>() {
				public int compare(Template tmpl1,
						Template tmpl2) {
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
     * Sets the dataDictionaryService attribute value.
     *
     * @param dataDictionaryService The dataDictionaryService to set.
     */
	protected void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}
	

    public List<String> getRoleIdsForPermissionId(String permissionId) {
        Permission permissionInfo = getPermission(permissionId);

        List<Permission> applicablePermissions = new ArrayList<Permission>();
        applicablePermissions.add(permissionInfo);

        List<String> roleIds = getRolesForPermissionsFromCache(applicablePermissions);
        if (roleIds == null) {
            roleIds = permissionDao.getRoleIdsForPermissions(applicablePermissions);
            addRolesForPermissionsToCache(applicablePermissions, roleIds);
        }

        return roleIds;
    }

    public Permission getPermissionsByNameIncludingInactive(String namespaceCode, String permissionName) {
        PermissionBo impls = getPermissionImplsByNameIncludingInactive(namespaceCode, permissionName);
        return PermissionBo.to(impls);
    }
	
    @SuppressWarnings("unchecked")
    protected PermissionBo getPermissionImplsByNameIncludingInactive(String namespaceCode, String permissionName) {
        String cacheKey = getPermissionImplByNameCacheKey(namespaceCode, permissionName + "inactive");
        PermissionBo permissions = (PermissionBo) cacheAdministrator.getFromCache(cacheKey);
        if (permissions == null) {
            HashMap<String, Object> pk = new HashMap<String, Object>(2);
            pk.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode);
            pk.put(KimConstants.UniqueKeyConstants.PERMISSION_NAME, permissionName);
            permissions = ((List<PermissionBo>) businessObjectService.findMatching(PermissionBo.class, pk)).get(0);
            cacheAdministrator.putInCache(cacheKey, permissions, PERMISSION_IMPL_CACHE_GROUP);
        }
        return permissions;
    }

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.api.permission.PermissionService#createPermission(org.kuali.rice.kim.api.permission.Permission)
	 */
	@Override
	public String createPermission(Permission permission)
			throws RiceIllegalArgumentException, RiceIllegalStateException {
		// TODO eldavid - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kim.api.permission.PermissionService#updatePermission(org.kuali.rice.kim.api.permission.Permission)
	 */
	@Override
	public void updatePermission(Permission permission)
			throws RiceIllegalArgumentException, RiceIllegalStateException {
		// TODO eldavid - THIS METHOD NEEDS JAVADOCS
		
	}
	
}
