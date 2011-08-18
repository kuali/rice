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
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.common.assignee.Assignee;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.type.KimAttributeField;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.framework.permission.PermissionTypeService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.service.BusinessObjectDictionaryService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.KRADPropertyConstants;

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
@SuppressWarnings("unused")
public class PermissionServiceImpl extends PermissionServiceBase implements PermissionService {
	private static final Logger LOG = Logger.getLogger( PermissionServiceImpl.class );

	private RoleService roleService;
	private PermissionDao permissionDao;
    private PermissionTypeService defaultPermissionTypeService;
    private DataDictionaryService dataDictionaryService;
    private KimTypeInfoService kimTypeInfoService;
    private BusinessObjectDictionaryService businessObjectDictionaryService;
	
 	private List<Template> allTemplates;
	
    // --------------------
    // Authorization Checks
    // --------------------
    
	protected PermissionTypeService getPermissionTypeService( String namespaceCode, String permissionTemplateName, String permissionName, String permissionId ) {
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
        return getPermissionTypeService( permTemplate );
	}

    protected PermissionTypeService getPermissionTypeService( PermissionTemplateBo permissionTemplate ) {
    	if ( permissionTemplate == null ) {
    		throw new IllegalArgumentException( "permissionTemplate may not be null" );
    	}
    	KimType kimType = kimTypeInfoService.getKimType( permissionTemplate.getKimTypeId() );
    	String serviceName = kimType.getServiceName();
    	// if no service specified, return a default implementation
    	if ( StringUtils.isBlank( serviceName ) ) {
    		return defaultPermissionTypeService;
    	}
    	try {
	    	Object service = GlobalResourceLoader.getService(serviceName);
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
    
    /**
     * @see org.kuali.rice.kim.service.PermissionService#hasPermission(java.lang.String, String, java.lang.String, Map<String, String>)
     */
    public boolean hasPermission(String principalId, String namespaceCode, String permissionName, Map<String, String> permissionDetails) {
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId is null or blank");
        }

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode is null or blank");
        }

        if (StringUtils.isBlank(permissionName)) {
            throw new RiceIllegalArgumentException("permissionName is null or blank");
        }
        return isAuthorized( principalId, namespaceCode, permissionName, permissionDetails, null );
    }

    public boolean isAuthorized(String principalId, String namespaceCode, String permissionName, Map<String, String> permissionDetails, Map<String, String> qualification ) {
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId is null or blank");
        }

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode is null or blank");
        }

        if (StringUtils.isBlank(permissionName)) {
            throw new RiceIllegalArgumentException("permissionName is null or blank");
        }
        List<String> roleIds = getRoleIdsForPermission( namespaceCode, permissionName, permissionDetails );
    	if ( roleIds.isEmpty() ) {
    		return false;
    	}

		//return roleService.principalHasRole( principalId, roleIds, new Map<String, String>(qualification.toMap()));

		return roleService.principalHasRole( principalId, roleIds, qualification);

    }

    public boolean hasPermissionByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, Map<String, String> permissionDetails) {
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId is null or blank");
        }

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode is null or blank");
        }

        if (StringUtils.isBlank(permissionTemplateName)) {
            throw new RiceIllegalArgumentException("permissionTemplateName is null or blank");
        }
        if (permissionDetails == null) {
            throw new RiceIllegalArgumentException("permissionDetails is null");
        }
        return isAuthorizedByTemplateName( principalId, namespaceCode, permissionTemplateName, permissionDetails, null );
    }

    public boolean isAuthorizedByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, Map<String, String> permissionDetails, Map<String, String> qualification ) {
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId is null or blank");
        }

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode is null or blank");
        }

        if (StringUtils.isBlank(permissionTemplateName)) {
            throw new RiceIllegalArgumentException("permissionTemplateName is null or blank");
        }
        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification is null");
        }
        if (permissionDetails == null) {
            throw new RiceIllegalArgumentException("permissionDetails is null");
        }
        List<String> roleIds = getRoleIdsForPermissionTemplate( namespaceCode, permissionTemplateName, permissionDetails );
    	if ( roleIds.isEmpty() ) {
    		return false;
    	}
    	return roleService.principalHasRole( principalId, roleIds, qualification);
    }

    public List<Permission> getAuthorizedPermissions( String principalId, String namespaceCode, String permissionName, Map<String, String> permissionDetails, Map<String, String> qualification ) {
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId is null or blank");
        }

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode is null or blank");
        }

        if (StringUtils.isBlank(permissionName)) {
            throw new RiceIllegalArgumentException("permissionName is null or blank");
        }
        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification is null");
        }
        if (permissionDetails == null) {
            throw new RiceIllegalArgumentException("permissionDetails is null");
        }
        // get all the permission objects whose name match that requested
    	PermissionBo permissions = getPermissionImplsByName( namespaceCode, permissionName );
    	// now, filter the full list by the detail passed
    	List<Permission> applicablePermissions = getMatchingPermissions( Collections.singletonList(permissions), permissionDetails );
    	return getPermissionsForUser(principalId, applicablePermissions, qualification);
    }

    public List<Permission> getAuthorizedPermissionsByTemplateName( String principalId, String namespaceCode, String permissionTemplateName, Map<String, String> permissionDetails, Map<String, String> qualification ) {
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId is null or blank");
        }

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode is null or blank");
        }

        if (StringUtils.isBlank(permissionTemplateName)) {
            throw new RiceIllegalArgumentException("permissionTemplateName is null or blank");
        }
        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification is null");
        }
        if (permissionDetails == null) {
            throw new RiceIllegalArgumentException("permissionDetails is null");
        }
        // get all the permission objects whose name match that requested
    	PermissionBo permissions = getPermissionImplsByTemplateName( namespaceCode, permissionTemplateName );
    	// now, filter the full list by the detail passed
    	List<Permission> applicablePermissions = getMatchingPermissions( Collections.singletonList(permissions), permissionDetails );
    	return getPermissionsForUser(principalId, applicablePermissions, qualification);
    }
    
    /**
     * Checks the list of permissions against the principal's roles and returns a subset of the list which match.
     */
    protected List<Permission> getPermissionsForUser( String principalId, List<Permission> permissions, Map<String, String> qualification ) {
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
    			if ( roleService.principalHasRole( principalId, roleIds, qualification) ) {
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
    protected List<Permission> getMatchingPermissions( List<PermissionBo> permissions, Map<String, String> permissionDetails ) {
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

    public List<Assignee> getPermissionAssignees( String namespaceCode, String permissionName, Map<String, String> permissionDetails, Map<String, String> qualification ) {
    	List<Assignee> results = new ArrayList<Assignee>();
    	List<String> roleIds = getRoleIdsForPermission( namespaceCode, permissionName, permissionDetails);
    	if ( roleIds.isEmpty() ) {
    		return results;
    	}
    	Collection<RoleMembership> roleMembers = roleService.getRoleMembers( roleIds,qualification );
    	for ( RoleMembership rm : roleMembers ) {
			List<DelegateType.Builder> delegateBuilderList = new ArrayList<DelegateType.Builder>();
			if (!rm.getDelegates().isEmpty()) {
    			for (DelegateType delegate : rm.getDelegates()){
                    delegateBuilderList.add(DelegateType.Builder.create(delegate));
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
    
    public List<Assignee> getPermissionAssigneesForTemplateName( String namespaceCode, String permissionTemplateName, Map<String, String> permissionDetails, Map<String, String> qualification ) {
    	List<Assignee> results = new ArrayList<Assignee>();
    	List<String> roleIds = getRoleIdsForPermissionTemplate( namespaceCode, permissionTemplateName, permissionDetails);
    	if ( roleIds.isEmpty() ) {
    		return results;
    	}
    	Collection<RoleMembership> roleMembers = roleService.getRoleMembers( roleIds,qualification);
    	for ( RoleMembership rm : roleMembers ) {
			List<DelegateType.Builder> delegateBuilderList = new ArrayList<DelegateType.Builder>();
			if (!rm.getDelegates().isEmpty()) {
    			for (DelegateType delegate : rm.getDelegates()){
                    delegateBuilderList.add(DelegateType.Builder.create(delegate));
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
    
    public boolean isPermissionAssigned( String namespaceCode, String permissionName, Map<String, String> permissionDetails ) {
    	return !getRoleIdsForPermission(namespaceCode, permissionName, permissionDetails).isEmpty();
    }
    
    public boolean isPermissionDefined( String namespaceCode, String permissionName, Map<String, String> permissionDetails ) {
    	// get all the permission objects whose name match that requested
    	PermissionBo permissions = getPermissionImplsByName( namespaceCode, permissionName );
    	// now, filter the full list by the detail passed
    	return !getMatchingPermissions( Collections.singletonList(permissions), permissionDetails ).isEmpty();
    }
    
    public boolean isPermissionDefinedForTemplateName( String namespaceCode, String permissionTemplateName, Map<String, String> permissionDetails ) {
    	// get all the permission objects whose name match that requested
    	PermissionBo permissions = getPermissionImplsByTemplateName( namespaceCode, permissionTemplateName );
    	// now, filter the full list by the detail passed
    	return !getMatchingPermissions( Collections.singletonList(permissions), permissionDetails ).isEmpty();
    }
 
    public List<String> getRoleIdsForPermission( String namespaceCode, String permissionName, Map<String, String> permissionDetails) {
    	// get all the permission objects whose name match that requested
    	PermissionBo permissions = getPermissionImplsByName( namespaceCode, permissionName );
    	// now, filter the full list by the detail passed
    	List<Permission> applicablePermissions = getMatchingPermissions( Collections.singletonList(permissions), permissionDetails );
        return permissionDao.getRoleIdsForPermissions( applicablePermissions );
    }

    protected List<String> getRoleIdsForPermissionTemplate( String namespaceCode, String permissionTemplateName, Map<String, String> permissionDetails ) {
    	// get all the permission objects whose name match that requested
    	PermissionBo permissions = getPermissionImplsByTemplateName( namespaceCode, permissionTemplateName );
    	// now, filter the full list by the detail passed
    	List<Permission> applicablePermissions = getMatchingPermissions( Collections.singletonList(permissions), permissionDetails );
    	return permissionDao.getRoleIdsForPermissions( applicablePermissions );
    }
    
    public List<String> getRoleIdsForPermissions( List<Permission> permissions ) {
   		return permissionDao.getRoleIdsForPermissions( permissions );
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
        HashMap<String,Object> pk = new HashMap<String,Object>( 1 );
        pk.put( KimConstants.PrimaryKeyConstants.PERMISSION_ID, permissionId );
        return businessObjectService.findByPrimaryKey( PermissionBo.class, pk );
    }
    
    protected PermissionBo getPermissionImplsByTemplateName( String namespaceCode, String permissionTemplateName ){
        HashMap<String,Object> pk = new HashMap<String,Object>( 3 );
        pk.put( "template.namespaceCode", namespaceCode );
        pk.put( "template.name", permissionTemplateName );
        pk.put( KRADPropertyConstants.ACTIVE, "Y" );
        return ((List<PermissionBo>) businessObjectService.findMatching( PermissionBo.class, pk )).get(0);
    }

    protected PermissionBo getPermissionImplsByName( String namespaceCode, String permissionName ) {
        HashMap<String,Object> pk = new HashMap<String,Object>( 3 );
        pk.put( KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode );
        pk.put( KimConstants.UniqueKeyConstants.PERMISSION_NAME, permissionName );
        pk.put( KRADPropertyConstants.ACTIVE, "Y" );
        
        return ((List<PermissionBo>) businessObjectService.findMatching( PermissionBo.class, pk )).get(0);
    }

    
    
    // --------------------
    // Support Methods
    // --------------------
	
	
    public PermissionDao getPermissionDao() {
		return this.permissionDao;
	}

	public void setPermissionDao(PermissionDao permissionDao) {
		this.permissionDao = permissionDao;
	}

    @SuppressWarnings("unchecked")
	public List<Permission> lookupPermissions(Map<String, String> searchCriteria, boolean unbounded ){
		Collection baseResults = null;
		Lookupable permissionLookupable = KNSServiceLocator.getLookupable(
                KRADServiceLocatorWeb.getBusinessObjectDictionaryService().getLookupableID(PermissionBo.class));
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
			final KimAttributeField attributeDef = KimAttributeField.findAttribute(attributeName, typeService.getAttributeDefinitions( kimTypeId ));
			return attributeDef != null ? attributeDef.getAttributeField().getLongLabel() : "Missing Def: " + attributeName;
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

    public List<String> getRoleIdsForPermissionId(String permissionId) {
        Permission permissionInfo = getPermission(permissionId);

        List<Permission> applicablePermissions = new ArrayList<Permission>();
        applicablePermissions.add(permissionInfo);

        return permissionDao.getRoleIdsForPermissions(applicablePermissions);
    }

    public Permission getPermissionsByNameIncludingInactive(String namespaceCode, String permissionName) {
        PermissionBo impls = getPermissionImplsByNameIncludingInactive(namespaceCode, permissionName);
        return PermissionBo.to(impls);
    }
	
    protected PermissionBo getPermissionImplsByNameIncludingInactive(String namespaceCode, String permissionName) {
        HashMap<String, Object> pk = new HashMap<String, Object>(2);
        pk.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode);
        pk.put(KimConstants.UniqueKeyConstants.PERMISSION_NAME, permissionName);
        return ((List<PermissionBo>) businessObjectService.findMatching(PermissionBo.class, pk)).get(0);
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
	
    /**
     * Sets the dataDictionaryService attribute value.
     *
     * @param dataDictionaryService The dataDictionaryService to set.
     */
	public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
		this.dataDictionaryService = dataDictionaryService;
	}
	
	/**
     * Sets the kimTypeInfoService attribute value.
     *
     * @param kimTypeInfoService The kimTypeInfoService to set.
     */
	public void setKimTypeInfoService(KimTypeInfoService kimTypeInfoService) {
		this.kimTypeInfoService = kimTypeInfoService;
	}
	
	/**
     * Sets the defaultPermissionTypeService attribute value.
     *
     * @param defaultPermissionTypeService The defaultPermissionTypeService to set.
     */
	public void setDefaultPermissionTypeService(PermissionTypeService defaultPermissionTypeService) {
    	this.defaultPermissionTypeService = defaultPermissionTypeService;
	}
	
	// TODO remove business object dd service?
	/**
     * Sets the businessObjectDictionaryService attribute value.
     *
     * @param businessObjectDictionaryService The businessObjectDictionaryService to set.
     */
	public void setBusinessObjectDictionaryService(BusinessObjectDictionaryService businessObjectDictionaryService) {
		this.businessObjectDictionaryService = businessObjectDictionaryService;
	}
	
	/**
     * Sets the roleService attribute value.
     *
     * @param roleService The roleService to set.
     */
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}
	
}
