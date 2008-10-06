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
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.PermissionDetailsInfo;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.bo.role.impl.RolePermissionImpl;
import org.kuali.rice.kim.bo.types.KimType;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.support.KimPermissionTypeService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PermissionServiceImpl implements PermissionService {

//	private static final Logger LOG = Logger.getLogger( AuthorizationServiceBaseImpl.class );
	
	private BusinessObjectService businessObjectService;
	private GroupService groupService;
	private RoleService roleService;

	// TODO: potential optimization - get list of candidate roles first by checking
	// their attached permissions and sending that into the role service as
	// a subset to search from when matching against the principal
	// Ex: boolean doesPrincipalHaveAnyOfTheseQualifiedRoles( String principalId, List<String> roleIds )
	
    // --------------------
    // Authorization Checks
    // --------------------

	/**  
	 * @see org.kuali.rice.kim.service.AuthorizationService#hasPermission(java.lang.String, java.lang.String)
	 */
    public boolean hasPermission(String principalId, String permissionId) {
    	// find all the roles for the principal
    	// derive all the permissions
    	// check the resulting permissions
    	if ( principalId == null || permissionId == null ) {
    		return false;
    	}
    	List<String> roleIds = getRoleService().getRoleIdsForPrincipal( principalId );
    	for ( String roleId : roleIds ) {
    		List<String> perms = getPermissionIdsForRole( roleId );
    		if ( perms.contains( permissionId ) ) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * @see org.kuali.rice.kim.service.AuthorizationService#hasQualifiedPermission(java.lang.String, java.lang.String, AttributeSet)
     */
    public boolean hasQualifiedPermission(String principalId, String permissionId, AttributeSet qualification) {
    	if ( principalId == null || permissionId == null || qualification == null ) {
    		return false;
    	}
    	// find all the roles for the principal
    	// check the qualifications for the principals membership in that role
    	//   principal direct
    	//   group member
    	// derive the permissions if the qualifications match
    	// check the resulting permissions
    	List<String> roleIds = getRoleService().getRoleIdsForPrincipal( principalId );
    	for ( String roleId : roleIds ) {
    		// find the principal's membership
    		if ( getRoleService().principalHasQualifiedRole( principalId, roleId, qualification ) ) {
        		List<String> perms = getPermissionIdsForRole( roleId );
        		if ( perms.contains( permissionId ) ) {
        			return true;
        		}
    		}
    	}
    	return false;
    }
    
    /**
     * @see org.kuali.rice.kim.service.AuthorizationService#hasPermissionWithDetails(java.lang.String, java.lang.String, AttributeSet)
     */
    public boolean hasPermissionWithDetails(String principalId, String permissionId, AttributeSet permissionDetails) {
    	List<String> roleIds = getRoleService().getRoleIdsForPrincipal( principalId );
    	// build a list of all granted permissions
    	List<PermissionDetailsInfo> perms = new ArrayList<PermissionDetailsInfo>();
    	for ( String roleId : roleIds ) {
    		perms.addAll( getPermissionsForRole( roleId ) );
    	}
    	// filter to just those that match
    	perms = getMatchingPermissions( perms, permissionDetails );
    	
    	return !perms.isEmpty();
    }
    
    /**
     * @see org.kuali.rice.kim.service.AuthorizationService#hasQualifiedPermissionWithDetails(java.lang.String, java.lang.String, AttributeSet, AttributeSet)
     */
    public boolean hasQualifiedPermissionWithDetails(String principalId,
    		String permissionId, AttributeSet qualification,
    		AttributeSet permissionDetails) {
    	List<String> roleIds = getRoleService().getRoleIdsMatchingQualification( principalId, qualification );
    	List<PermissionDetailsInfo> perms = new ArrayList<PermissionDetailsInfo>();
    	for ( String roleId : roleIds ) {
    		perms.addAll( getPermissionsForRole( roleId ) );
    	}
    	// filter to just those that match
    	perms = getMatchingPermissions( perms, permissionDetails );
    	
    	return !perms.isEmpty();
    }

    /**
     * @see org.kuali.rice.kim.service.AuthorizationService#hasQualifiedPermissionByName( java.lang.String, java.lang.String, AttributeSet)
     */
    public boolean hasQualifiedPermissionByName(String principalId, String permissionName, AttributeSet qualification) {
    	KimPermissionImpl perm = getPermissionImplByName( permissionName );
    	if ( perm == null ) {
    		return false;
    	}
    	return hasQualifiedPermission( principalId, perm.getPermissionId(), qualification );
    }

	
    // --------------------
    // Role Permission Methods
    // --------------------
	
    
	/**
	 * Return all the permission IDs granted by having a given role.
	 */
    protected List<String> getPermissionIdsForRole(String roleId) {
    	List<String> perms = new ArrayList<String>();
    	List<PermissionDetailsInfo> rolePerms = getPermissionsForRole(roleId);
    	for ( PermissionDetailsInfo perm : rolePerms ) {
    		perms.add( perm.getPermissionId() );
    	}
    	return perms;
    }

    @SuppressWarnings("unchecked")
	protected List<PermissionDetailsInfo> getPermissionsForRole(String roleId ) {
    	List<PermissionDetailsInfo> perms = new ArrayList<PermissionDetailsInfo>();
    	List<String> impliedRoles = getRoleService().getImpliedRoleIds( roleId );
    	AttributeSet rolePermCriteria = new AttributeSet();
    	for ( String impliedRoleId : impliedRoles ) {
    		// TODO: optimize me - get all role permission objects at once
    		rolePermCriteria.put("roleId", impliedRoleId);
        	List<RolePermissionImpl> rolePerms = (List)getBusinessObjectService().findMatching(RolePermissionImpl.class, rolePermCriteria);
        	for ( RolePermissionImpl perm : rolePerms ) {
    			perms.add( perm.getPermission().toSimpleInfo() );
        	}
    	}
    	return perms;
    }
    
    /**
     * @see org.kuali.rice.kim.service.AuthorizationService#getPermissionDetails(java.lang.String, java.lang.String, AttributeSet)
     */
    public List<AttributeSet> getPermissionDetails(String principalId, String permissionId, AttributeSet qualification) {
    	throw new UnsupportedOperationException();
    	// TODO: implement me!
    }
    

    // --------------------
    // Permission Data
    // --------------------
    
    /**
     * @see org.kuali.rice.kim.service.AuthorizationService#getPermission(java.lang.String)
     */
    public KimPermissionInfo getPermission(String permissionId) {
    	KimPermissionImpl impl = getPermissionImpl( permissionId );
    	if ( impl != null ) {
    		return impl.toSimpleInfo();
    	}
    	return null;
    }
    
    /**
     * @see org.kuali.rice.kim.service.AuthorizationService#getPermissionByName(java.lang.String)
     */
    public KimPermissionInfo getPermissionByName(String permissionName) {
    	KimPermissionImpl impl = getPermissionImplByName( permissionName );
    	if ( impl != null ) {
    		return impl.toSimpleInfo();
    	}
    	return null;
    }
    
    /**
     * @see org.kuali.rice.kim.service.AuthorizationService#lookupPermissions(AttributeSet)
     */
    public List<KimPermissionInfo> lookupPermissions(AttributeSet searchCriteria) {
    	//return (List<KimPermission>)getBusinessObjectService().findMatching( KimPermissionImpl.class, searchCriteria );
    	throw new UnsupportedOperationException();
    }
    
    protected KimPermissionImpl getPermissionImpl(String permissionId) {
    	if ( StringUtils.isBlank( permissionId ) ) {
    		return null;
    	}
    	HashMap<String,Object> pk = new HashMap<String,Object>( 1 );
    	pk.put( "permissionId", permissionId );
    	return (KimPermissionImpl)getBusinessObjectService().findByPrimaryKey( KimPermissionImpl.class, pk );
    }
    
    protected KimPermissionImpl getPermissionImplByName( String permissionName ) {
    	HashMap<String,Object> pk = new HashMap<String,Object>( 3 );
    	pk.put( "name", permissionName );
		pk.put("active", "Y");
    	return (KimPermissionImpl)getBusinessObjectService().findByPrimaryKey( KimPermissionImpl.class, pk );
    }
    
    /**
     * @see org.kuali.rice.kim.service.AuthorizationService#getPermissionIdByName(java.lang.String)
     */
    public String getPermissionIdByName( String permissionName) {
    	KimPermissionImpl perm = getPermissionImplByName( permissionName );
    	if ( perm == null ) {
    		return null;
    	}
    	return perm.getPermissionId();
    }

    protected List<PermissionDetailsInfo> getMatchingPermissions( List<PermissionDetailsInfo> permissions, AttributeSet details ) {
    	List<PermissionDetailsInfo> perms = new ArrayList<PermissionDetailsInfo>();
    	for ( PermissionDetailsInfo perm : permissions ) {
    		String serviceName = getPermissionTypeServiceName( perm.getPermissionId() );
    		KimPermissionTypeService kimPermissionService = (KimPermissionTypeService)KIMServiceLocator
					.getService( serviceName );
    		if ( kimPermissionService == null
    				|| kimPermissionService.doesPermissionDetailMatch( details, perm.getDetails() ) ) {
    			perms.add( perm );
    		}
    	}
    	return perms;
    }
    
    protected String getPermissionTypeServiceName( String permissionId ) {
    	KimType permType = getPermissionImpl( permissionId ).getTemplate().getKimType();
    	if ( permType != null ) {
    		return permType.getKimTypeServiceName();
    	}
    	return null;
    }



    public void savePermission(KimPermissionInfo permission) {
    	throw new UnsupportedOperationException();
//    	if ( permission == null ) {
//    		return;
//    	}
//    	KimPermissionImpl impl = new KimPermissionImpl();
//    	impl.fromInfo( permission );
//		getBusinessObjectService().save( impl );
    }

	
    public void assignQualifiedPermissionToRole(String roleId, String permissionId, AttributeSet qualifier) {
    	throw new UnsupportedOperationException();
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
			roleService = KIMServiceLocator.getRoleService();		
		}

		return roleService;
	}

}
