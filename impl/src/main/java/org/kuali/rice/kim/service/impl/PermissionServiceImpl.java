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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kim.bo.role.KimRole;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.PermissionAssigneeInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.dao.KimPermissionDao;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.support.KimPermissionTypeService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PermissionServiceImpl implements PermissionService {

	private static final Logger LOG = Logger.getLogger( PermissionServiceImpl.class );
	
	private BusinessObjectService businessObjectService;
	private GroupService groupService;
	private RoleService roleService;
	private KimPermissionDao permissionDao;
    private KimPermissionTypeService defaultPermissionTypeService;

    // --------------------
    // Authorization Checks
    // --------------------
    
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
    
    public KimPermissionTypeService getDefaultPermissionTypeService() {
    	if ( defaultPermissionTypeService == null ) {
    		defaultPermissionTypeService = (KimPermissionTypeService)KIMServiceLocator.getBean( KimConstants.DEFAULT_PERMISSION_TYPE_SERVICE );
    	}
		return defaultPermissionTypeService;
	}

    protected Map<String,KimPermissionTypeService> getPermissionTypeServicesByTemplateId( Collection<KimPermissionImpl> permissions ) {
    	Map<String,KimPermissionTypeService> permissionTypeServices = new HashMap<String, KimPermissionTypeService>( permissions.size() );
    	for ( KimPermissionImpl perm : permissions ) {
    		String serviceName = perm.getTemplate().getKimType().getKimTypeServiceName();
    		if ( serviceName != null ) {
    			KimPermissionTypeService permissionTypeService = (KimPermissionTypeService)KIMServiceLocator.getService( serviceName );
    			if ( permissionTypeService != null ) {
    	    		permissionTypeServices.put(perm.getTemplateId(), permissionTypeService );    				
    			} else {
					LOG.error("Can't find permission type service for " + perm + " permission type service bean name " + serviceName);
					throw new RuntimeException("Can't find permission type service for " + perm + " permission type service bean name " + serviceName);
    			}
    		}
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
    			if ( permissionTypeService == null ) {
    				permissionTypeService = getDefaultPermissionTypeService();
    			}
				applicablePermissions.addAll( permissionTypeService.doPermissionDetailsMatch( permissionDetails, permissionList ) );    				
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
    	Collection<RoleMembershipInfo> roleMembers = getRoleService().getRoleMembers( roleIds, qualification );
    	for ( RoleMembershipInfo rm : roleMembers ) {
    		if ( rm.getMemberTypeCode().equals( KimRole.PRINCIPAL_MEMBER_TYPE ) ) {
    			results.add( new PermissionAssigneeInfo( rm.getMemberId(), null, rm.getDelegates() ) );
    		} else if ( rm.getMemberTypeCode().equals( KimRole.GROUP_MEMBER_TYPE ) ) {
    			results.add( new PermissionAssigneeInfo( null, rm.getMemberId(), rm.getDelegates() ) );
    		}
    	}
    	return results;
    }
    
    public List<PermissionAssigneeInfo> getPermissionAssigneesForTemplateName( String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	List<PermissionAssigneeInfo> results = new ArrayList<PermissionAssigneeInfo>();
    	List<String> roleIds = getRoleIdsForPermissionTemplate( namespaceCode, permissionTemplateName, permissionDetails);
    	Collection<RoleMembershipInfo> roleMembers = getRoleService().getRoleMembers( roleIds, qualification );
    	for ( RoleMembershipInfo rm : roleMembers ) {
    		if ( rm.getMemberTypeCode().equals( KimRole.PRINCIPAL_MEMBER_TYPE ) ) {
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
    	List<KimPermissionImpl> permissions = getPermissionImplsByName( namespaceCode, permissionTemplateName );
    	// now, filter the full list by the detail passed
    	return !getMatchingPermissions( permissions, permissionDetails ).isEmpty();   
    }

    protected List<String> getRoleIdsForPermission( String namespaceCode, String permissionName, AttributeSet permissionDetails) {
    	// get all the permission objects whose name match that requested
    	List<KimPermissionImpl> permissions = getPermissionImplsByName( namespaceCode, permissionName );
    	// now, filter the full list by the detail passed
    	List<KimPermissionInfo> applicablePermissions = getMatchingPermissions( permissions, permissionDetails );    	
    	return permissionDao.getRoleIdsForPermissions( applicablePermissions );    	
    }

    protected List<String> getRoleIdsForPermissionTemplate( String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails ) {
    	// get all the permission objects whose name match that requested
    	List<KimPermissionImpl> permissions = getPermissionImplsByTemplateName( namespaceCode, permissionTemplateName );
    	// now, filter the full list by the detail passed
    	// FIXME: certain generic permissions (like view and edit) could have hundreds of rows - this will not scale
    	List<KimPermissionInfo> applicablePermissions = getMatchingPermissions( permissions, permissionDetails );    	
    	return permissionDao.getRoleIdsForPermissions( applicablePermissions );    	
    }
    
    // --------------------
    // Role Permission Methods
    // --------------------
	
    /**
     * @see org.kuali.rice.kim.service.PermissionService#getPermissionDetails(java.lang.String, java.lang.String, AttributeSet)
     */
    public List<AttributeSet> getPermissionDetails(String principalId, String permissionId, AttributeSet qualification) {
    	throw new UnsupportedOperationException();
    	// TODO: implement me!
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
    
    /**
     * @see org.kuali.rice.kim.service.PermissionService#lookupPermissions(AttributeSet)
     */
    public List<KimPermissionInfo> lookupPermissions(AttributeSet searchCriteria) {
    	//return (List<KimPermission>)getBusinessObjectService().findMatching( KimPermissionImpl.class, searchCriteria );
    	throw new UnsupportedOperationException();
    }

    public List<AttributeSet> getRoleQualifiersByPermissionName( String principalId, String namespaceCode, String permissionName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	List<KimPermissionImpl> impls = getPermissionImplsByName( namespaceCode, permissionName );    	
    	List<KimPermissionInfo> applicablePermissions = getMatchingPermissions( impls, permissionDetails );    	
    	List<String> roleIds = permissionDao.getRoleIdsForPermissions(applicablePermissions);
    	return getRoleService().getRoleQualifiersForPrincipal(principalId, roleIds, qualification);    	
    }

    public List<AttributeSet> getRoleQualifiersByTemplateName( String principalId, String namespaceCode, String permissionTemplateName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	List<KimPermissionImpl> impls = getPermissionImplsByTemplateName( namespaceCode, permissionTemplateName );    	
    	List<KimPermissionInfo> applicablePermissions = getMatchingPermissions( impls, permissionDetails );    	
    	List<String> roleIds = permissionDao.getRoleIdsForPermissions(applicablePermissions);
    	return getRoleService().getRoleQualifiersForPrincipal(principalId, roleIds, qualification);
    }

    
    protected KimPermissionImpl getPermissionImpl(String permissionId) {
    	if ( StringUtils.isBlank( permissionId ) ) {
    		return null;
    	}
    	HashMap<String,Object> pk = new HashMap<String,Object>( 1 );
    	pk.put( "permissionId", permissionId );
    	return (KimPermissionImpl)getBusinessObjectService().findByPrimaryKey( KimPermissionImpl.class, pk );
    }
    
    @SuppressWarnings("unchecked")
	protected List<KimPermissionImpl> getPermissionImplsByTemplateName( String namespaceCode, String permissionTemplateName ) {
    	HashMap<String,Object> pk = new HashMap<String,Object>( 3 );
    	pk.put( "template.namespaceCode", namespaceCode );
    	pk.put( "template.name", permissionTemplateName );
		pk.put( "active", "Y" );
    	return (List<KimPermissionImpl>)getBusinessObjectService().findMatching( KimPermissionImpl.class, pk );
    }

    @SuppressWarnings("unchecked")
	protected List<KimPermissionImpl> getPermissionImplsByName( String namespaceCode, String permissionName ) {
    	HashMap<String,Object> pk = new HashMap<String,Object>( 3 );
    	pk.put( "namespaceCode", namespaceCode );
    	pk.put( "name", permissionName );
		pk.put( "active", "Y" );
    	return (List<KimPermissionImpl>)getBusinessObjectService().findMatching( KimPermissionImpl.class, pk );
    }
    
    protected String getPermissionTypeServiceName( String permissionId ) {
    	KimTypeImpl permType = getPermissionImpl( permissionId ).getTemplate().getKimType();
    	if ( permType != null ) {
    		return permType.getKimTypeServiceName();
    	}
    	return null;
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

    
	protected GroupService getGroupService() {
		if ( groupService == null ) {
			groupService = KIMServiceLocator.getGroupService();		
		}

		return groupService;
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

}
