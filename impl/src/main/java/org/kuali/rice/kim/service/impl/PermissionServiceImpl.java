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

import org.apache.commons.lang.StringUtils;
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
	private KimPermissionDao permissionDao;

    // --------------------
    // Authorization Checks
    // --------------------
    
    /**
     * @see org.kuali.rice.kim.service.PermissionService#hasPermission(java.lang.String, java.lang.String, AttributeSet)
     */
    public boolean hasPermission(String principalId, String permissionName, AttributeSet permissionDetails) {
    	return isAuthorized( principalId, permissionName, permissionDetails, null );
    }

    /**
     * @see org.kuali.rice.kim.service.PermissionService#isAuthorized( java.lang.String, java.lang.String, AttributeSet, AttributeSet)
     */
    public boolean isAuthorized(String principalId, String permissionName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	List<String> roleIds = getRoleIdsForPermission( permissionName, permissionDetails, qualification );
    	return getRoleService().principalHasRole( principalId, roleIds, qualification );
    }

    public List<KimPermissionInfo> getAuthorizedPermissions( String principalId, String permissionName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	throw new UnsupportedOperationException();
    }
    /**
     * Compare each of the passed in permissions with the given permissionDetails.  Those that
     * match are added to the result list.
     */
    protected List<KimPermissionImpl> getMatchingPermissions( List<KimPermissionImpl> permissions, AttributeSet permissionDetails ) {
    	List<KimPermissionImpl> applicablePermissions;    	
    	if ( permissionDetails == null || permissionDetails.isEmpty() ) {
    		// if no details passed, assume that all match
    		applicablePermissions = permissions;
    	} else {
    		// otherwise, attempt to match the 
    		applicablePermissions = new ArrayList<KimPermissionImpl>();
    		for ( KimPermissionImpl perm : permissions ) {
    			String serviceName = perm.getTemplate().getKimType().getKimTypeServiceName();
    			if ( serviceName == null ) { // no service - assume a match
    				applicablePermissions.add( perm );
    			} else {
    				KimPermissionTypeService permissionTypeService = (KimPermissionTypeService)KIMServiceLocator.getBean( serviceName );
    				if ( permissionTypeService == null ) { // can't find the service - assume a match
    					applicablePermissions.add( perm );
    				} else { // got a service - check with it
    					if ( permissionTypeService.doesPermissionDetailMatch( permissionDetails, perm.getDetails() ) ) {
    						applicablePermissions.add( perm );
    					}
    				}
    			}
    		}
    	}
    	return applicablePermissions;
    }
	
    public List<PermissionAssigneeInfo> getPermissionAssignees( String permissionName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	List<PermissionAssigneeInfo> results = new ArrayList<PermissionAssigneeInfo>();
    	List<String> roleIds = getRoleIdsForPermission( permissionName, permissionDetails, qualification );
    	Collection<RoleMembershipInfo> roleMembers = getRoleService().getRoleMembers( roleIds, qualification );
    	for ( RoleMembershipInfo rm : roleMembers ) {
    		results.add( new PermissionAssigneeInfo( rm.getPrincipalId(), rm.getGroupId(), rm.getDelegates() ) );
    	}
    	return results;
    }
    
    protected List<String> getRoleIdsForPermission( String permissionName, AttributeSet permissionDetails, AttributeSet qualification ) {
    	// get all the permission objects whose name match that requested
    	List<KimPermissionImpl> permissions = getPermissionImplsByName( permissionName );
    	// now, filter the full list by the detail passed
    	List<KimPermissionImpl> applicablePermissions = getMatchingPermissions( permissions, permissionDetails );    	
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
     * @see org.kuali.rice.kim.service.PermissionService#getPermissionsByName(java.lang.String)
     */
    public List<KimPermissionInfo> getPermissionsByName(String permissionName) {
    	List<KimPermissionImpl> impls = getPermissionImplsByName( permissionName );
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
    
    protected KimPermissionImpl getPermissionImpl(String permissionId) {
    	if ( StringUtils.isBlank( permissionId ) ) {
    		return null;
    	}
    	HashMap<String,Object> pk = new HashMap<String,Object>( 1 );
    	pk.put( "permissionId", permissionId );
    	return (KimPermissionImpl)getBusinessObjectService().findByPrimaryKey( KimPermissionImpl.class, pk );
    }
    
    @SuppressWarnings("unchecked")
	protected List<KimPermissionImpl> getPermissionImplsByName( String permissionName ) {
    	HashMap<String,Object> pk = new HashMap<String,Object>( 3 );
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

	public KimPermissionDao getPermissionDao() {
		return this.permissionDao;
	}

	public void setPermissionDao(KimPermissionDao permissionDao) {
		this.permissionDao = permissionDao;
	}

}
