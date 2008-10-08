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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kim.bo.role.RoleRelationship;
import org.kuali.rice.kim.bo.role.dto.DelegateInfo;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.role.impl.KimDelegationGroupImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationPrincipalImpl;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.role.impl.RoleGroupImpl;
import org.kuali.rice.kim.bo.role.impl.RolePrincipalImpl;
import org.kuali.rice.kim.bo.role.impl.RoleRelationshipImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.dao.KimRoleDao;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.support.KimDelegationTypeService;
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
	private KimRoleDao roleDao; 

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

//    /**
//     * @see org.kuali.rice.kim.service.RoleService#principalHasRole(java.lang.String, java.lang.String)
//     */
//	public boolean principalHasRole(String principalId, String roleId) {
//		// need to check all of user's roles and see if they contain the given role
//		// LOGIC:  You have a role if it is directly assigned 
//		//    OR it is a child role (of variable depth) of a role that is directly assigned
//		
//		// so, first check for a direct role assignment
//		List<String> roles = getRoleIdsForPrincipal(principalId);
//		for ( String role : roles ) {
//			if ( role.equals(roleId) ) {
//				return true;
//			}
//		}
//
//		return false;
//	}
//
//	/**
//	 * @see org.kuali.rice.kim.service.RoleService#principalHasQualifiedRole(java.lang.String, java.lang.String, AttributeSet)
//	 */
//    public boolean principalHasQualifiedRole(String principalId, String roleId, AttributeSet qualification) {
//    	KimRoleImpl role = getRoleImpl( roleId );
//    	return principalHasQualifiedRole( principalId, role, qualification );
//    }
//
//    /**
//     * @see org.kuali.rice.kim.service.RoleService#principalHasQualifiedRole(java.lang.String, org.kuali.rice.kim.bo.role.KimRole, AttributeSet)
//     */
//    protected boolean principalHasQualifiedRole(String principalId, KimRoleImpl role, AttributeSet qualification) {
//    	if ( StringUtils.isEmpty( principalId ) || role == null ) {
//    		return false;
//    	}
//    	// determine the principal's membership in the role
//    	//   check direct assignment
//    	//   get all the principal's groups, then check against that list
//    	//    (or, get all member groups and check if the user is in those)
//     	KimRoleTypeService roleTypeService = (KimRoleTypeService)KIMServiceLocator.getService( role.getKimRoleType().getKimTypeServiceName() );
//     	if ( roleTypeService == null ) {
//     		LOG.error( "Unable to obtain service for KimRoleType: " + role.getKimRoleType().getKimTypeServiceName() );
//     	}
//     	// direct assignment
//    	for ( RolePrincipal rp : role.getMemberPrincipals() ) {
//    		if ( rp.getPrincipalId().equals( principalId ) ) {
//    			if ( rp.hasQualifier() 
//    					&& roleTypeService.doesRoleQualifierMatchQualification( qualification, rp.getQualifier() ) ) {
//    				return true;
//    			}
//    		}
//    	}
//    	// assigned by group
//    	for ( RoleGroup rg : role.getMemberGroups() ) {
//    		if ( getGroupService().isMemberOfGroup( principalId, rg.getGroupId() ) ) {
//    			if ( rg.hasQualifier() 
//    					&& roleTypeService.doesRoleQualifierMatchQualification( qualification, rg.getQualifier() ) ) {
//    				return true;
//    			}
//    		}
//    	}
//    	// TODO: check higher level roles (since qualifiers at that level is what matters
//    	// TODO: rewrite to use getQualifiedRolesForPrincipal()
//    	return false;
//    }
//
//    /**
//     * @see org.kuali.rice.kim.service.RoleService#getPrincipalIdsWithRole(java.lang.String)
//     */
//    public List<String> getPrincipalIdsWithRole(String roleId) {
//    	return getPrincipalIdsWithRole(roleId, true);
//    }
//
//    /**
//     * @see org.kuali.rice.kim.service.RoleService#getPrincipalIdsWithRole(java.lang.String)
//     */
//    protected List<String> getPrincipalIdsWithRole(String roleId, boolean recurse) {
//    	Set<String> ids = new HashSet<String>();
//
//		KimRoleImpl role = getRoleImpl(roleId);
//		List<RolePrincipal> rolePrincipals = role.getMemberPrincipals();
//		List<RoleGroup> roleGroups = role.getMemberGroups();
//
//		for (RolePrincipal rolePrincipal : rolePrincipals) {
//			ids.add(rolePrincipal.getPrincipalId());
//		}
//		for (RoleGroup roleGroup : roleGroups) {
//			ids.addAll(getGroupService().getMemberPrincipalIds(roleGroup.getGroupId()));
//		}
//		
//		// difference from groups - do not recurse, principals in lower level roles do not automatically belong to
//		// the higher level roles
//				
//		// but, if someone has a higher level role, then they also have the lower level role
//		// so, we need to look for higher level roles to the one given (recursively)
//		if ( recurse ) {
//			List<String> parentRoles = getImplyingRoleIds(roleId);
//			for ( String parentRoleId : parentRoles ) {
//				ids.addAll( getPrincipalIdsWithRole(parentRoleId, false));
//			}		
//		}
//		return new ArrayList<String>(ids);
//    }
//
//    /**
//     * @see org.kuali.rice.kim.service.RoleService#getPrincipalIdsWithQualifiedRole(java.lang.String, AttributeSet)
//     */
//    public List<String> getPrincipalIdsWithQualifiedRole(String roleId, AttributeSet qualifications) {
//    	Set<String> principalIds = new HashSet<String>();
//    	// get matching qualified role members
//    	KimRoleImpl role = getRoleImpl(roleId);
//    	if ( role == null ) {
//    		return new ArrayList<String>(principalIds);
//    	}
//    	// add directly assigned principals
//    	KimRoleTypeService kimRoleTypeService = (KimRoleTypeService)KIMServiceLocator.getService( role.getKimRoleType().getKimTypeServiceName() );
//    	for ( RolePrincipal rm : role.getMemberPrincipals() ) {
//    		AttributeSet qualifier = rm.getQualifier();
//    		if ( kimRoleTypeService.doesRoleQualifierMatchQualification(qualifications, qualifier)  ) {
//    			principalIds.add(rm.getPrincipalId());
//    		}
//    	}
//    	// resolve directly assigned matching groups into principals
//    	for ( RoleGroup rm : role.getMemberGroups() ) {
//    		AttributeSet qualifier = rm.getQualifier();
//    		if ( kimRoleTypeService.doesRoleQualifierMatchQualification(qualifications, qualifier)  ) {
//    			principalIds.addAll( getGroupService().getMemberPrincipalIds(rm.getGroupId()) );
//    		}
//    	}    	
//    	// now, find all implying roles and test them as well
//    	List<String> implyingRoles = getImplyingRoleIds(roleId);
//    	for ( String implyingRoleId : implyingRoles ) {
//    		principalIds.addAll( getPrincipalIdsWithQualifiedRole(implyingRoleId, qualifications) );	
//    	}
//    	    	
//    	return new ArrayList<String>(principalIds);
//    }
//    
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
    public Collection<RoleMembershipInfo> getRoleMembers(List<String> roleIds, AttributeSet qualification) {
    	List<RoleMembershipInfo> results = new ArrayList<RoleMembershipInfo>();
    	Set<String> allRoleIds = new HashSet<String>();
    	// get all implying roles (this also filters to active roles only)
    	for ( String roleId : allRoleIds ) {
    		allRoleIds.addAll( getImplyingRoleIds(roleId) );
    	}
    	// short-circuit if no roles match
    	if ( allRoleIds.isEmpty() ) {
    		return results;
    	}
    	Set<String> matchingRoleIds = new HashSet<String>( allRoleIds.size() );
    	// for efficiency, retrieve all roles and store in a map
    	Map<String,KimRoleImpl> roles = roleDao.getRoleImplMap(allRoleIds);
    	// again, for efficiency, obtain and store all role-type services by roleId
    	Map<String,KimRoleTypeService> roleTypeServices = getRoleTypeServicesByRoleId( roles.values() );
    	
    	List<RolePrincipalImpl> rps = roleDao.getRolePrincipalsForRoleIds( roleIds );
    	// check each membership to see if the qualifier matches
    	for ( RolePrincipalImpl rp : rps ) {
			// check the qualifications
    		KimRoleTypeService roleTypeService = roleTypeServices.get( rp.getRoleId() );
    		// if the role type service is null, assume that all qualifiers match
			if ( qualification == null || roleTypeService == null || roleTypeService.doesRoleQualifierMatchQualification( qualification, rp.getQualifier() ) ) {
				RoleMembershipInfo mi = new RoleMembershipInfo( rp.getPrincipalId(), null, rp.getRoleId(), rp.getQualifier() );
				results.add( mi );
				matchingRoleIds.add( rp.getRoleId() );
			}
    	}   	
    	
    	List<RoleGroupImpl> rgs = roleDao.getRoleGroupsForRoleIds( roleIds );
    	// check each membership to see if the qualifier matches
    	for ( RoleGroupImpl rg : rgs ) {
			// check the qualifications
    		KimRoleTypeService roleTypeService = roleTypeServices.get( rg.getRoleId() );
    		// if the role type service is null, assume that all qualifiers match
			if ( qualification == null || roleTypeService == null || roleTypeService.doesRoleQualifierMatchQualification( qualification, rg.getQualifier() ) ) {
				RoleMembershipInfo mi = new RoleMembershipInfo( null, rg.getGroupId(), rg.getRoleId(), rg.getQualifier() );
				results.add( mi );
				matchingRoleIds.add( rg.getRoleId() );
			}
    	}
    	
    	// List of RoleMembershipInfo objects
    	// need to get delegations for distinct list of roles in that list
    	Map<String,KimDelegationImpl> delegations = roleDao.getDelegationImplMapFromRoleIds( matchingRoleIds );
    	List<String> applicableDelegationIds = new ArrayList<String>();
    	// check each delegation's qualifier to see if they are applicable - matching
    	// against the qualifiers used for that role
    	// so - for each role,
    	for ( RoleMembershipInfo mi : results ) {
    		// get the delegations specific to the role on this line
    		for ( KimDelegationImpl delegation : delegations.values() ) {
    			// only check at the moment if the role IDs match
    			if ( delegation.getRoleId().equals( mi.getRoleId() ) ) {
    	    		KimRoleTypeService roleTypeService = roleTypeServices.get( delegation.getRoleId() );
    	    		// this check is against the qualifier from the role relationship
    	    		// that resulted in this record
    	    		// This is to determine that the delegation
    	    		if ( roleTypeService == null 
    	    				|| roleTypeService.doesRoleQualifierMatchQualification( mi.getQualifier(), delegation.getQualifier() ) ) {
    					// add this delegation to a list to check the principal and group membership
    	    			applicableDelegationIds.add(delegation.getDelegationId());
    	    			// add a reference to this delegation for further checking
    	    			mi.getDelegates().add( new DelegateInfo( delegation.getDelegationId(), delegation.getDelegationTypeCode() ) );
    				}
    			}
    		}
    	}
    			
//    	if ( !applicableDelegationIds.isEmpty() ) {
//        	Map<String,List<KimDelegationPrincipalImpl>> delegationPrincipals = roleDao.getDelegationPrincipalsForDelegationIds(applicableDelegationIds);
//        	Map<String,List<KimDelegationGroupImpl>> delegationGroups = roleDao.getDelegationGroupsForDelegationIds(applicableDelegationIds);
//        	
//    		// get the services for use later
//    		Map<String,KimDelegationTypeService> delegationTypeServices = getDelegationTypeServicesByDelegationId(applicableDelegationIds, delegations);
//    		// check principals assigned to this role
//    		for ( RoleMembershipInfo mi : results ) {
//    			Iterator<DelegateInfo> iter = mi.getDelegates().iterator();
//    			while ( iter.hasNext() ) {
//    				DelegateInfo di = iter.next();
//    				KimDelegationTypeService delegationTypeService = delegationTypeServices.get( di.getDelegationId() );
//    				// get the principals for this delegation
//    				// get the groups for this delegation
//    				// if one matches, update the delegation info
//    				
//        			if ( delegationTypeService == null 
//        					|| delegationTypeService.doesRoleQualifierMatchQualification(qualification, dp.getQualifier())) {
//        				// add the delegation information
//        			} else { 
//        				// doesn't match - remove from the list
//        				iter.remove();
//        			}
//    			}
//    		}
    		
    		
//    		
//    		for ( KimDelegationPrincipalImpl dp : dps ) {
//    			KimDelegationTypeService delegationTypeService = delegationTypeServices.get(dp.getDelegationId());
//    			// QUESTION: does the qualifier map need to be merged with the main delegation qualification?
//    			if ( delegationTypeService == null || delegationTypeService.doesRoleQualifierMatchQualification(qualification, dp.getQualifier())) {
//    				
//    			}
//    		}
//    		// check groups assigned to this role - use the list of groups from earlier
//        	if ( !groupIds.isEmpty() ) {
//    	    	List<KimDelegationGroupImpl> dgs = roleDao.getDelegationGroupsForDelegationIds( allRoleIds );
//    	    	
//    	    	for ( KimDelegationGroupImpl dg : dgs ) {
//    	    		// check the qualifications
//    	    		KimDelegationTypeService delegationTypeService = delegationTypeServices.get( dg.getDelegationId() );
//    	    		// if the delegation type service is null, assume that all qualifiers match
//    				if ( delegationTypeService == null || delegationTypeService.doesRoleQualifierMatchQualification( qualification, dg.getQualifier() ) ) {
//    					return true;
//    				}
//    	    	}
//        	}    		

//    	}
    	
    	// loop over the results - extract the roleIds
    	// loop over the results - extract roleId->set of qualifications
    	
    	// get the list of delegations for the roles
    	
    	// determine the applicable
    	
    	// store in a map(roleId->List<KimDelegationImpl))
    	// TODO: get the list of delegate members for the delegations
//    	roleDao.getDelegationGroupsForDelegationIds( delegationIds, roleIds )
    	// store in a map (delegationId->List<Delegation>
    	
    	
    	
    	
    	return results;
    }
    
    /**
     * @see org.kuali.rice.kim.service.RoleService#principalHasRole(java.lang.String, java.util.List, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    public boolean principalHasRole(String principalId, List<String> roleIds, AttributeSet qualification) {
    	Set<String> allRoleIds = new HashSet<String>();
    	// get all implying roles (this also filters to active roles only)
    	for ( String roleId : allRoleIds ) {
    		allRoleIds.addAll( getImplyingRoleIds(roleId) );
    	}
    	// short-circuit if no roles match
    	if ( allRoleIds.isEmpty() ) {
    		return false;
    	}
    	// if the qualification is null, then any role in the list will match
    	// so since the role ID list is not blank, we can return true at this point
    	if ( qualification == null ) {
    		return true;
    	}    	
    	// for efficiency, retrieve all roles and store in a map
    	Map<String,KimRoleImpl> roles = roleDao.getRoleImplMap(allRoleIds);
    	// again, for efficiency, obtain and store all role-type services by roleId
    	Map<String,KimRoleTypeService> roleTypeServices = getRoleTypeServicesByRoleId( roles.values() );
    	// get all roles to which the principal is assigned
    	List<RolePrincipalImpl> rps = roleDao.getRolePrincipalsForPrincipalIdAndRoleIds(allRoleIds, principalId);
    	// check each membership to see if the principal matches
    	for ( RolePrincipalImpl rp : rps ) {
    		//KimRoleImpl role = roles.get( rp.getRoleId() );
			// check the qualifications
    		KimRoleTypeService roleTypeService = roleTypeServices.get( rp.getRoleId() );
    		// if the role type service is null, assume that all qualifiers match
			if ( roleTypeService == null || roleTypeService.doesRoleQualifierMatchQualification( qualification, rp.getQualifier() ) ) {
				return true;
			}
    	}
    	// TODO: check for application roles and extract principals from that - then check them against the
    	// role type service passing in the qualification and principal - the qualifier comes from the
    	// external system (application)
    	
    	
    	// find the groups that the principal belongs to
    	List<String> groupIds = getGroupService().getGroupIdsForPrincipal(principalId);
    	// find the role/group associations
    	if ( !groupIds.isEmpty() ) {
	    	List<RoleGroupImpl> rgs = roleDao.getRoleGroupsForGroupIdsAndRoleIds( allRoleIds, groupIds);
	    	
	    	for ( RoleGroupImpl rg : rgs ) {
	    		//KimRoleImpl role = roles.get( rg.getRoleId() );
	    		// check the qualifications
	    		KimRoleTypeService roleTypeService = roleTypeServices.get( rg.getRoleId() );
	    		// if the role type service is null, assume that all qualifiers match
				if ( roleTypeService == null || roleTypeService.doesRoleQualifierMatchQualification( qualification, rg.getQualifier() ) ) {
					return true;
				}
	    	}
    	}
    	// delegations
    	if ( matchesOnDelegation( allRoleIds, principalId, groupIds, qualification, roleTypeServices ) ) {
    		return true;
    	}
    	
    	// NOTE: this logic is a little different from the getRoleMembers method
    	// If there is no primary (matching non-delegate), this method will still return true
    	return false;
    }
    
    /**
     * Support method for principalHasRole.  Checks delegations on the passed in roles for the given principal and groups.  (It's assumed that the principal
     * belongs to the given groups.)
     * 
     * Delegation checks are mostly the same as role checks except that the delegation itself is qualified against the original role (like a RolePrincipal
     * or RoleGroup.)  And then, the members of that delegation may have additional qualifiers which are not part of the original role qualifiers.
     * 
     * For example:
     * 
     * A role could be qualified by organization.  So, there is a person in the organization with primary authority for that org.  But, then they delegate authority
     * for that organization (not their authority - the delegation is attached to the org.)  So, in this case the delegation has a qualifier of the organization
     * when it is attached to the role.
     * 
     * The principals then attached to that delegation (which is specific to the organization), may have additional qualifiers.  
     * For Example: dollar amount range, effective dates, document types.
     * As a subsequent step, those qualifiers are checked against the qualification passed in from the client.
     */
    protected boolean matchesOnDelegation( Set<String> allRoleIds, String principalId, List<String> groupIds, AttributeSet qualification, Map<String,KimRoleTypeService> roleTypeServices ) {
    	// get the list of delegations for the roles 
    	Map<String,KimDelegationImpl> delegations = roleDao.getDelegationImplMapFromRoleIds(allRoleIds);
    	List<String> applicableDelegationIds = new ArrayList<String>();
    	// loop over the delegations - determine those which need to be inspected more directly
    	for ( KimDelegationImpl delegation : delegations.values() ) {
        	// check if each one matches via the original role type service
    		KimRoleTypeService roleTypeService = roleTypeServices.get( delegation.getRoleId() );
    		if ( roleTypeService == null || roleTypeService.doesRoleQualifierMatchQualification( qualification, delegation.getQualifier() ) ) {
				// add this delegation to a list to check the principal and group membership
    			applicableDelegationIds.add(delegation.getDelegationId());
			}
    	}
    	if ( !applicableDelegationIds.isEmpty() ) {
    		// get the services for use later
    		Map<String,KimDelegationTypeService> delegationTypeServices = getDelegationTypeServicesByDelegationId(applicableDelegationIds, delegations);
    		// check principals assigned to this role
    		List<KimDelegationPrincipalImpl> dps = roleDao.getDelegationPrincipalsForPrincipalIdAndDelegationIds(applicableDelegationIds, principalId);
    		for ( KimDelegationPrincipalImpl dp : dps ) {
    			KimDelegationTypeService delegationTypeService = delegationTypeServices.get(dp.getDelegationId());
    			// QUESTION: does the qualifier map need to be merged with the main delegation qualification?
    			if ( delegationTypeService == null || delegationTypeService.doesRoleQualifierMatchQualification(qualification, dp.getQualifier())) {
    				return true;
    			}
    		}
    		// check groups assigned to this role - use the list of groups from earlier
        	if ( !groupIds.isEmpty() ) {
    	    	List<KimDelegationGroupImpl> dgs = roleDao.getDelegationGroupsForGroupIdsAndDelegationIds( applicableDelegationIds, groupIds);
    	    	
    	    	for ( KimDelegationGroupImpl dg : dgs ) {
    	    		// check the qualifications
    	    		KimDelegationTypeService delegationTypeService = delegationTypeServices.get( dg.getDelegationId() );
    	    		// if the delegation type service is null, assume that all qualifiers match
    				if ( delegationTypeService == null || delegationTypeService.doesRoleQualifierMatchQualification( qualification, dg.getQualifier() ) ) {
    					return true;
    				}
    	    	}
        	}    		
    	}
    	// TODO: handle roles assigned as members of a delegation - DO NOT follow the role further down
    	// this is really there for application roles whose members are only known to the client application
    	
    	return false;
    }
    
    protected Map<String,KimRoleTypeService> getRoleTypeServicesByRoleId( Collection<KimRoleImpl> roles ) {
    	Map<String,KimRoleTypeService> roleTypeServices = new HashMap<String, KimRoleTypeService>( roles.size() );
    	for ( KimRoleImpl role : roles ) {
    		String serviceName = role.getKimRoleType().getKimTypeServiceName();
    		if ( serviceName != null ) {
    			KimRoleTypeService roleTypeService = (KimRoleTypeService)KIMServiceLocator.getService( serviceName );
    			if ( roleTypeService != null ) {
    	    		roleTypeServices.put(role.getRoleId(), roleTypeService );    				
    			}
    		}
    	}
    	return roleTypeServices;
    }
    
    protected Map<String,KimDelegationTypeService> getDelegationTypeServicesByDelegationId( Collection<String> delegationIds, Map<String,KimDelegationImpl> delegations ) {
    	Map<String,KimDelegationTypeService> roleTypeServices = new HashMap<String, KimDelegationTypeService>( delegationIds.size() );
    	for ( String delegationId : delegationIds ) {
    		String serviceName = delegations.get(delegationId).getKimType().getKimTypeServiceName();
    		if ( serviceName != null ) {
    			KimDelegationTypeService delegationTypeService = (KimDelegationTypeService)KIMServiceLocator.getService( serviceName );
    			if ( delegationTypeService != null ) {
    	    		roleTypeServices.put(delegationId, delegationTypeService );    				
    			}
    		}
    	}
    	return roleTypeServices;
    }
    
//	public List<String> getRoleIdsForPrincipal(String principalId) {
//		return getRoleIdsInNamespaceForPrincipal( principalId, null );
//	}
//
//	@SuppressWarnings("unchecked")
//	public List<String> getRoleIdsInNamespaceForPrincipal(String principalId, String namespaceCode) {
//		if ( StringUtils.isBlank( principalId ) ) {
//			return new ArrayList<String>(0);
//		}
//		// get the direct principal assignments
//		AttributeSet criteria = new AttributeSet();
//		criteria.put("memberId", principalId);
//		Set<String> roles = new HashSet<String>();
//		List<RolePrincipalImpl> principalRoles = (List<RolePrincipalImpl>) getBusinessObjectService().findMatching(RolePrincipalImpl.class, criteria);
//		for ( RolePrincipalImpl rp : principalRoles ) {
//			KimRoleImpl role = getRoleImpl( rp.getRoleId() );
//			if ( role.isActive() 
//					&& (namespaceCode==null
//							||role.getNamespaceCode().equals( namespaceCode ) ) ) {
//				roles.add( rp.getRoleId() );
//				roles.addAll( getImpliedRoleIds( role.getRoleId() ) );
//			}
//		}
//		// get the list of groups (and parent groups) assigned to this principal (via GroupService)
//		// then run queries against the RoleGroups table
//		List<String> groups = getGroupService().getGroupIdsForPrincipal( principalId );
//		for ( String groupId : groups ) {
//			List<RoleGroupImpl> groupRoles = getRolesForDirectGroup( groupId );
//			// TODO: optimize me - get all role infos at once
//			for ( RoleGroupImpl rp : groupRoles ) {
//				KimRoleImpl role = getRoleImpl( rp.getRoleId() );
//				if ( namespaceCode == null || role.getNamespaceCode().equals( namespaceCode ) ) {
//					roles.add( role.getRoleId() );
//				}
//			}
//		}
//				
//		return new ArrayList<String>( roles );
//	}
	
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
		if ( role == null || !role.isActive() ) {
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
			// if we've already seen that role (or it's not active), don't recurse into it
			if ( !containedRole.isActive() && !roles.contains( containedRole ) ) {
				roles.add(containedRole);
				getImpliedRolesInternal(containedRole,roles);
			}
		}
	}
    
	public List<String> getImplyingRoleIds( String roleId ) {
		Set<String> roleIds = new HashSet<String>();
		
		// add the given role
		if ( isRoleActive( roleId ) ) {
			roleIds.add(roleId);
			getImplyingRolesInternal(roleId, roleIds);		
		}
		
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
	
//	@SuppressWarnings("unchecked")
//	public List<String> getRoleIdsMatchingQualification( String principalId, AttributeSet qualification ) {
//		// get the direct principal assignments
//		AttributeSet criteria = new AttributeSet();
//		criteria.put("memberId", principalId);
//		Set<String> roles = new HashSet<String>();
//		List<RolePrincipalImpl> principalRoles = (List<RolePrincipalImpl>) getBusinessObjectService().findMatching(RolePrincipalImpl.class, criteria);
//		// TODO: optimize me - pull all the role details with one SQL
//		for ( RolePrincipalImpl rp : principalRoles ) {
//			KimRoleImpl role = getRoleImpl( rp.getRoleId() );
//			KimRoleTypeService roleTypeService = (KimRoleTypeService)KIMServiceLocator.getService( role.getKimRoleType().getKimTypeServiceName() );
//			if ( role.isActive() 
//					&& roleTypeService.doesRoleQualifierMatchQualification( qualification, rp.getQualifier() ) 
//					) {
//				roles.add( role.getRoleId() );
//				roles.addAll( getImpliedRoleIds( role.getRoleId() ) );
//			}
//		}
//		// get the list of groups (and parent groups) assigned to this principal (via GroupService)
//		// then run queries against the RoleGroups table
//		List<String> groups = groupService.getGroupIdsForPrincipal( principalId );
//		// TODO: optimize me - pull all the group details with one SQL
//		for ( String groupId : groups ) {
//			List<RoleGroupImpl> groupRoles = getRolesForDirectGroup( groupId );
//			for ( RoleGroupImpl rg : groupRoles ) {
//				KimRoleImpl role = getRoleImpl( rg.getRoleId() );
//				KimRoleTypeService roleTypeService = (KimRoleTypeService)KIMServiceLocator.getService( role.getKimRoleType().getKimTypeServiceName() );
//				if ( role.isActive() 
//						&& roleTypeService.doesRoleQualifierMatchQualification( qualification, rg.getQualifier() ) 
//						) {
//					roles.add( role.getRoleId() );
//				}
//			}
//		}
//				
//		return new ArrayList<String>( roles );
//	}

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

	/**
	 * @return the roleDao
	 */
	public KimRoleDao getRoleDao() {
		return this.roleDao;
	}

	/**
	 * @param roleDao the roleDao to set
	 */
	public void setRoleDao(KimRoleDao roleDao) {
		this.roleDao = roleDao;
	}
	
}
