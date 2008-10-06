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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kim.bo.role.RoleGroup;
import org.kuali.rice.kim.bo.role.RolePrincipal;
import org.kuali.rice.kim.bo.role.RoleRelationship;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.role.impl.RoleGroupImpl;
import org.kuali.rice.kim.bo.role.impl.RolePrincipalImpl;
import org.kuali.rice.kim.bo.role.impl.RoleRelationshipImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.RoleMembershipInfo;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.support.KimRoleTypeService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoleServiceImpl implements RoleService {

	private static final Logger LOG = Logger.getLogger( RoleServiceImpl.class );
	
	private BusinessObjectService businessObjectService;
	private GroupService groupService;

    // --------------------
    // Role Data
    // --------------------
   
	protected KimRoleImpl getRoleImpl(String roleId) {
		if ( StringUtils.isBlank( roleId ) ) {
			return null;
		}
		AttributeSet criteria = new AttributeSet();
		criteria.put("roleId", roleId);
		return (KimRoleImpl)getBusinessObjectService().findByPrimaryKey(KimRoleImpl.class, criteria);
	}

	/**
	 * @see org.kuali.rice.kim.service.RoleService#getRole(java.lang.String)
	 */
	public KimRoleInfo getRole(String roleId) {		
		KimRoleImpl role = getRoleImpl( roleId );
		if ( role == null ) {
			return null;
		}
		return role.toSimpleInfo();
	}
	
	/**
	 * @see org.kuali.rice.kim.service.RoleService#getRoleIdByName(java.lang.String, java.lang.String)
	 */
	public String getRoleIdByName(String namespaceCode, String roleName) {
		KimRoleInfo role = getRoleByName( namespaceCode, roleName );
		if ( role == null ) {
			return null;
		}
		return role.getRoleId();
	}

	/**
	 * @see org.kuali.rice.kim.service.RoleService#getRoleByName(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public KimRoleInfo getRoleByName( String namespaceCode, String roleName ) {
		if ( StringUtils.isBlank( namespaceCode ) 
				|| StringUtils.isBlank( roleName ) ) {
			return null;
		}
		AttributeSet criteria = new AttributeSet();
		criteria.put("namespaceCode", namespaceCode);
		criteria.put("roleName", roleName);
		criteria.put("active", "Y");
		KimRoleImpl role = (KimRoleImpl)getBusinessObjectService().findByPrimaryKey(KimRoleImpl.class, criteria);
		if ( role != null ) {
			return role.toSimpleInfo();
		}
		return null;
	}
	
   	
	@SuppressWarnings("unchecked")
	public List<KimRoleInfo> lookupRoles(Map<String, String> searchCriteria) {
		return (List<KimRoleInfo>) getBusinessObjectService().findMatching(KimRoleImpl.class, searchCriteria);
	}
	
	public boolean isRoleActive( String roleId ) {
		AttributeSet criteria = new AttributeSet();
		criteria.put("roleId", roleId);
		criteria.put("active", "Y");
		return getBusinessObjectService().countMatching(KimRoleImpl.class, criteria) > 0;
	}

    /**
     * @see org.kuali.rice.kim.service.RoleService#principalHasRole(java.lang.String, java.lang.String)
     */
	public boolean principalHasRole(String principalId, String roleId) {
		// need to check all of user's roles and see if they contain the given role
		// LOGIC:  You have a role if it is directly assigned 
		//    OR it is a child role (of variable depth) of a role that is directly assigned
		
		// so, first check for a direct role assignment
		List<String> roles = getRoleIdsForPrincipal(principalId);
		for ( String role : roles ) {
			if ( role.equals(roleId) ) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @see org.kuali.rice.kim.service.RoleService#principalHasQualifiedRole(java.lang.String, java.lang.String, AttributeSet)
	 */
    public boolean principalHasQualifiedRole(String principalId, String roleId, AttributeSet qualification) {
    	KimRoleImpl role = getRoleImpl( roleId );
    	return principalHasQualifiedRole( principalId, role, qualification );
    }

    /**
     * @see org.kuali.rice.kim.service.RoleService#principalHasQualifiedRole(java.lang.String, org.kuali.rice.kim.bo.role.KimRole, AttributeSet)
     */
    protected boolean principalHasQualifiedRole(String principalId, KimRoleImpl role, AttributeSet qualification) {
    	if ( StringUtils.isEmpty( principalId ) || role == null ) {
    		return false;
    	}
    	// determine the principal's membership in the role
    	//   check direct assignment
    	//   get all the principal's groups, then check against that list
    	//    (or, get all member groups and check if the user is in those)
     	KimRoleTypeService roleTypeService = (KimRoleTypeService)KIMServiceLocator.getService( role.getKimRoleType().getKimTypeServiceName() );
     	if ( roleTypeService == null ) {
     		LOG.error( "Unable to obtain service for KimRoleType: " + role.getKimRoleType().getKimTypeServiceName() );
     	}
     	// direct assignment
    	for ( RolePrincipal rp : role.getMemberPrincipals() ) {
    		if ( rp.getPrincipalId().equals( principalId ) ) {
    			if ( rp.hasQualifier() 
    					&& roleTypeService.doesRoleQualifierMatchQualification( qualification, rp.getQualifier() ) ) {
    				return true;
    			}
    		}
    	}
    	// assigned by group
    	for ( RoleGroup rg : role.getMemberGroups() ) {
    		if ( getGroupService().isMemberOfGroup( principalId, rg.getGroupId() ) ) {
    			if ( rg.hasQualifier() 
    					&& roleTypeService.doesRoleQualifierMatchQualification( qualification, rg.getQualifier() ) ) {
    				return true;
    			}
    		}
    	}
    	// TODO: check higher level roles (since qualifiers at that level is what matters
    	// TODO: rewrite to use getQualifiedRolesForPrincipal()
    	return false;
    }

    /**
     * @see org.kuali.rice.kim.service.RoleService#getPrincipalIdsWithRole(java.lang.String)
     */
    public List<String> getPrincipalIdsWithRole(String roleId) {
    	return getPrincipalIdsWithRole(roleId, true);
    }

    /**
     * @see org.kuali.rice.kim.service.RoleService#getPrincipalIdsWithRole(java.lang.String)
     */
    protected List<String> getPrincipalIdsWithRole(String roleId, boolean recurse) {
    	Set<String> ids = new HashSet<String>();

		KimRoleImpl role = getRoleImpl(roleId);
		List<RolePrincipal> rolePrincipals = role.getMemberPrincipals();
		List<RoleGroup> roleGroups = role.getMemberGroups();

		for (RolePrincipal rolePrincipal : rolePrincipals) {
			ids.add(rolePrincipal.getPrincipalId());
		}
		for (RoleGroup roleGroup : roleGroups) {
			ids.addAll(getGroupService().getMemberPrincipalIds(roleGroup.getGroupId()));
		}
		
		// difference from groups - do not recurse, principals in lower level roles do not automatically belong to
		// the higher level roles
				
		// but, if someone has a higher level role, then they also have the lower level role
		// so, we need to look for higher level roles to the one given (recursively)
		if ( recurse ) {
			List<String> parentRoles = getImplyingRoleIds(roleId);
			for ( String parentRoleId : parentRoles ) {
				ids.addAll( getPrincipalIdsWithRole(parentRoleId, false));
			}		
		}
		return new ArrayList<String>(ids);
    }

    /**
     * @see org.kuali.rice.kim.service.RoleService#getPrincipalIdsWithQualifiedRole(java.lang.String, AttributeSet)
     */
    public List<String> getPrincipalIdsWithQualifiedRole(String roleId, AttributeSet qualifications) {
    	Set<String> principalIds = new HashSet<String>();
    	// get matching qualified role members
    	KimRoleImpl role = getRoleImpl(roleId);
    	if ( role == null ) {
    		return new ArrayList<String>(principalIds);
    	}
    	// add directly assigned principals
    	KimRoleTypeService kimRoleTypeService = (KimRoleTypeService)KIMServiceLocator.getService( role.getKimRoleType().getKimTypeServiceName() );
    	for ( RolePrincipal rm : role.getMemberPrincipals() ) {
    		AttributeSet qualifier = rm.getQualifier();
    		if ( kimRoleTypeService.doesRoleQualifierMatchQualification(qualifications, qualifier)  ) {
    			principalIds.add(rm.getPrincipalId());
    		}
    	}
    	// resolve directly assigned matching groups into principals
    	for ( RoleGroup rm : role.getMemberGroups() ) {
    		AttributeSet qualifier = rm.getQualifier();
    		if ( kimRoleTypeService.doesRoleQualifierMatchQualification(qualifications, qualifier)  ) {
    			principalIds.addAll( getGroupService().getMemberPrincipalIds(rm.getGroupId()) );
    		}
    	}    	
    	// now, find all implying roles and test them as well
    	List<String> implyingRoles = getImplyingRoleIds(roleId);
    	for ( String implyingRoleId : implyingRoles ) {
    		principalIds.addAll( getPrincipalIdsWithQualifiedRole(implyingRoleId, qualifications) );	
    	}
    	    	
    	return new ArrayList<String>(principalIds);
    }
    
//    public List<String> getGroupIdsWithRole(String roleId) {
//    	throw new UnsupportedOperationException();
//    }
//
//    public List<String> getGroupIdsWithQualifiedRole(String roleId, AttributeSet qualifications) {
//    	// check the directly assigned groups
//    	// get those which match the qualification
//    	// pull all sub-groups as well
//    	throw new UnsupportedOperationException();
//    }

   

    // --------------------
    // Role Membership Methods
    // --------------------

    /**
     * @see org.kuali.rice.kim.service.RoleService#getRoleMembers(java.util.List, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    public List<RoleMembershipInfo> getRoleMembers(List<String> roleIds, AttributeSet qualification) {
    	throw new UnsupportedOperationException();
    	// return null;
    }
    
    /**
     * @see org.kuali.rice.kim.service.RoleService#principalHasRole(java.lang.String, java.util.List, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    public boolean principalHasRole(String principalId, List<String> roleIds,
    		AttributeSet qualification) {
    	throw new UnsupportedOperationException();
    	// return false;
    }
    
    
	public List<String> getRoleIdsForPrincipal(String principalId) {
		return getRoleIdsInNamespaceForPrincipal( principalId, null );
	}

	@SuppressWarnings("unchecked")
	public List<String> getRoleIdsInNamespaceForPrincipal(String principalId, String namespaceCode) {
		if ( StringUtils.isBlank( principalId ) ) {
			return new ArrayList<String>(0);
		}
		// get the direct principal assignments
		AttributeSet criteria = new AttributeSet();
		criteria.put("memberId", principalId);
		Set<String> roles = new HashSet<String>();
		List<RolePrincipalImpl> principalRoles = (List<RolePrincipalImpl>) getBusinessObjectService().findMatching(RolePrincipalImpl.class, criteria);
		for ( RolePrincipalImpl rp : principalRoles ) {
			KimRoleImpl role = getRoleImpl( rp.getRoleId() );
			if ( role.isActive() 
					&& (namespaceCode==null
							||role.getNamespaceCode().equals( namespaceCode ) ) ) {
				roles.add( rp.getRoleId() );
				roles.addAll( getImpliedRoleIds( role.getRoleId() ) );
			}
		}
		// get the list of groups (and parent groups) assigned to this principal (via GroupService)
		// then run queries against the RoleGroups table
		List<String> groups = getGroupService().getGroupIdsForPrincipal( principalId );
		for ( String groupId : groups ) {
			List<RoleGroupImpl> groupRoles = getRolesForDirectGroup( groupId );
			// TODO: optimize me - get all role infos at once
			for ( RoleGroupImpl rp : groupRoles ) {
				KimRoleImpl role = getRoleImpl( rp.getRoleId() );
				if ( namespaceCode == null || role.getNamespaceCode().equals( namespaceCode ) ) {
					roles.add( role.getRoleId() );
				}
			}
		}
				
		return new ArrayList<String>( roles );
	}
	
	public List<String> getImpliedRoleIds(String roleId) {
		List<String> roleIds = new ArrayList<String>();
		for ( KimRoleImpl role : getImpliedRoles(roleId) ) {
			roleIds.add( role.getRoleId() );
		}
		return roleIds;
	}

    
	protected List<KimRoleImpl> getImpliedRoles(String roleId) {
		// using set to automatically remove duplicates
		KimRoleImpl role = getRoleImpl(roleId);
		return getImpliedRoles( role );
	}

	protected List<KimRoleImpl> getImpliedRoles(KimRoleImpl role) {
		if ( role == null ) {
			return new ArrayList<KimRoleImpl>(0);
		}
		// using set to automatically remove duplicates
		Set<KimRoleImpl> roles = new HashSet<KimRoleImpl>();

		// add the top-level role
		roles.add( role );
		getImpliedRolesInternal( role, roles );
		
		return new ArrayList<KimRoleImpl>(roles);
	}
	
	/**
	 * This is a helper method to allow visibility of what's been added to the set before
	 * to prevent infinite recursion. 
	 */
	protected void getImpliedRolesInternal( KimRoleImpl role, Set<KimRoleImpl> roles ) {
		if ( role == null ) {
			return;
		}
		List<RoleRelationship> roleRoles = role.getContainedRoles();

		for (RoleRelationship roleRole : roleRoles) {
			KimRoleImpl containedRole = getRoleImpl(roleRole.getContainedRoleId());
			// if we've already seen that role, don't recurse into it
			if ( !roles.contains( containedRole ) ) {
				roles.add(containedRole);
				getImpliedRolesInternal(containedRole,roles);
			}
		}
	}
    
	public List<String> getImplyingRoleIds( String roleId ) {
		Set<String> roleIds = new HashSet<String>();
		
		// add the given role
		roleIds.add(roleId);
		getImplyingRolesInternal(roleId, roleIds);		
		
		return new ArrayList<String>( roleIds );
	}
	
	@SuppressWarnings("unchecked")
	protected void getImplyingRolesInternal( String roleId, Set<String> roles ) {
		if ( roleId == null ) {
			return;
		}
		// search role relationships where the given role is the child
		AttributeSet criteria = new AttributeSet();
		criteria.put( "containedRoleId", roleId );
		Collection<RoleRelationship> rels = getBusinessObjectService().findMatching(RoleRelationshipImpl.class, criteria);
		for ( RoleRelationship rel : rels ) {
			if ( !roles.contains(rel.getRoleId()) ) {
				if ( isRoleActive( rel.getRoleId() ) ) { 
					roles.add( rel.getRoleId() );
					getImplyingRolesInternal(rel.getRoleId(), roles);
				}
			}
		}
	}
	
    // getRoleIdsForPrincipal(prin)
    // getRolePermissionIdsForRoleAndPermission(role,perm)
    // List<AttributeSet> getPermissionQualifiers( List<String> rolePermissionIds )
    
	/**
	 * Gets the roles directly assigned to the given role.
	 */
	@SuppressWarnings("unchecked")
	protected List<RoleGroupImpl> getRolesForDirectGroup(String groupId) {
		AttributeSet criteria = new AttributeSet();
		criteria.put("memberId", groupId);

		List<RoleGroupImpl> roles = (List<RoleGroupImpl>) getBusinessObjectService().findMatching(RoleGroupImpl.class, criteria);
				
		return roles;
	}	
	
	@SuppressWarnings("unchecked")
	public List<String> getRoleIdsMatchingQualification( String principalId, AttributeSet qualification ) {
		// get the direct principal assignments
		AttributeSet criteria = new AttributeSet();
		criteria.put("memberId", principalId);
		Set<String> roles = new HashSet<String>();
		List<RolePrincipalImpl> principalRoles = (List<RolePrincipalImpl>) getBusinessObjectService().findMatching(RolePrincipalImpl.class, criteria);
		// TODO: optimize me - pull all the role details with one SQL
		for ( RolePrincipalImpl rp : principalRoles ) {
			KimRoleImpl role = getRoleImpl( rp.getRoleId() );
			KimRoleTypeService roleTypeService = (KimRoleTypeService)KIMServiceLocator.getService( role.getKimRoleType().getKimTypeServiceName() );
			if ( role.isActive() 
					&& roleTypeService.doesRoleQualifierMatchQualification( qualification, rp.getQualifier() ) 
					) {
				roles.add( role.getRoleId() );
				roles.addAll( getImpliedRoleIds( role.getRoleId() ) );
			}
		}
		// get the list of groups (and parent groups) assigned to this principal (via GroupService)
		// then run queries against the RoleGroups table
		List<String> groups = groupService.getGroupIdsForPrincipal( principalId );
		// TODO: optimize me - pull all the group details with one SQL
		for ( String groupId : groups ) {
			List<RoleGroupImpl> groupRoles = getRolesForDirectGroup( groupId );
			for ( RoleGroupImpl rg : groupRoles ) {
				KimRoleImpl role = getRoleImpl( rg.getRoleId() );
				KimRoleTypeService roleTypeService = (KimRoleTypeService)KIMServiceLocator.getService( role.getKimRoleType().getKimTypeServiceName() );
				if ( role.isActive() 
						&& roleTypeService.doesRoleQualifierMatchQualification( qualification, rg.getQualifier() ) 
						) {
					roles.add( role.getRoleId() );
				}
			}
		}
				
		return new ArrayList<String>( roles );
	}

    // --------------------
    // Persistence Methods
    // --------------------
	
	public void saveRole(KimRoleInfo role) {
		throw new UnsupportedOperationException();
//		if ( role == null ) {
//			return;
//		}
//		KimRoleImpl impl = new KimRoleImpl();
//		impl.fromSimpleInfo();
//		if ( role instanceof PersistableBusinessObject ) {
//			getBusinessObjectService().save((PersistableBusinessObject)role);
//		} else {
//    		throw new IllegalArgumentException( "saveRole: role was not a PersistableBusinessObject.  It can not be persisted" +
//    				"through this implementation.  was: " + role.getClass().getName() );
//		}
	}

    public void assignQualifiedRoleToPrincipal(String principalId, String roleId, AttributeSet qualifications) {
    	throw new UnsupportedOperationException();
    }

    public void assignQualifiedRoleToGroup(String groupId, String roleId, AttributeSet qualifications) {
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
	
}
