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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.role.KimRole;
import org.kuali.rice.kim.bo.role.dto.DelegateInfo;
import org.kuali.rice.kim.bo.role.dto.KimDelegationMemberInfo;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberImpl;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleRelationshipImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.dao.KimRoleDao;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.support.KimDelegationTypeService;
import org.kuali.rice.kim.service.support.KimRoleTypeService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.SequenceAccessorService;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoleServiceImpl implements RoleService {

//	private static final Logger LOG = Logger.getLogger( RoleServiceImpl.class );
	
	protected static final String ROLE_MEMBER_SEQUENCE = "KRIM_ROLE_MBR_ID_S";
	protected static final String ROLE_MEMBER_DATA_SEQUENCE = "KRIM_ATTR_DATA_ID_S";
	
	private BusinessObjectService businessObjectService;
	private SequenceAccessorService sequenceAccessorService;
	private IdentityManagementService identityManagementService;
	private KimRoleDao roleDao; 

	private ThreadLocal<Map<String,Boolean>> activeRoleCache = new ThreadLocal<Map<String,Boolean>>();
	private ThreadLocal<Map<String,KimRoleImpl>> roleCache = new ThreadLocal<Map<String,KimRoleImpl>>();
	private ThreadLocal<Map<String,List<String>>> impliedRoleCache = new ThreadLocal<Map<String,List<String>>>();
	
    // --------------------
    // Role Data
    // --------------------
   
	protected KimRoleImpl getRoleImpl(String roleId) {
		if ( StringUtils.isBlank( roleId ) ) {
			return null;
		}
		// check the cache
		Map<String,KimRoleImpl> cache = roleCache.get();
		// create the cache if necessary
		if ( cache == null ) {
			cache = new HashMap<String,KimRoleImpl>();
			roleCache.set( cache );
		}
		// check for a non-null result in the cache, return it if found
		KimRoleImpl cachedResult = cache.get( roleId );
		if ( cachedResult != null ) {
			return cachedResult;
		}		
		// otherwise, run the query
		AttributeSet criteria = new AttributeSet();
		criteria.put("roleId", roleId);
		KimRoleImpl result = (KimRoleImpl)getBusinessObjectService().findByPrimaryKey(KimRoleImpl.class, criteria);
		cache.put( roleId, result );
		return result;
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
		KimRoleImpl role = getRoleImplByName( namespaceCode, roleName );
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
		KimRoleImpl role = getRoleImplByName( namespaceCode, roleName );
		if ( role != null ) {
			return role.toSimpleInfo();
		}
		return null;
	}
	
	protected KimRoleImpl getRoleImplByName( String namespaceCode, String roleName ) {
		if ( StringUtils.isBlank( namespaceCode ) 
				|| StringUtils.isBlank( roleName ) ) {
			return null;
		}
		AttributeSet criteria = new AttributeSet();
		criteria.put("namespaceCode", namespaceCode);
		criteria.put("roleName", roleName);
		criteria.put("active", "Y");
		return (KimRoleImpl)getBusinessObjectService().findByPrimaryKey(KimRoleImpl.class, criteria);		
	}
	
   	
	@SuppressWarnings("unchecked")
	public List<KimRoleInfo> lookupRoles(Map<String, String> searchCriteria) {
		return (List<KimRoleInfo>) getBusinessObjectService().findMatching(KimRoleImpl.class, searchCriteria);
	}
	
	public boolean isRoleActive( String roleId ) {
		// check the cache
		Map<String,Boolean> cache = activeRoleCache.get();
		// create the cache if necessary
		if ( cache == null ) {
			cache = new HashMap<String,Boolean>();
			activeRoleCache.set( cache );
		}
		// check for a non-null result in the cache, return it if found
		Boolean cachedResult = cache.get( roleId );
		if ( cachedResult != null ) {
			return cachedResult;
		}		
		// otherwise, run the query
		AttributeSet criteria = new AttributeSet();
		criteria.put("roleId", roleId);
		criteria.put("active", "Y");
		boolean result = getBusinessObjectService().countMatching(KimRoleImpl.class, criteria) > 0;
		cache.put( roleId, result );
		return result;
	}

	public List<AttributeSet> getRoleQualifiersForPrincipal( String principalId, List<String> roleIds, AttributeSet qualification ) {
		List<AttributeSet> results = new ArrayList<AttributeSet>();
		
    	Map<String,KimRoleImpl> roles = roleDao.getRoleImplMap(roleIds);
    	Map<String,KimRoleTypeService> roleTypeServices = getRoleTypeServicesByRoleId( roles.values() );
    	
    	// TODO: ? get groups for principal and get those as well?
    	// this implementation may be incomplete, as groups and sub-roles are not considered
    	List<RoleMemberImpl> rms = roleDao.getRoleMembersForRoleIdsWithFilters(roleIds, principalId, null);

    	Map<String,List<RoleMembershipInfo>> roleIdToMembershipMap = new HashMap<String,List<RoleMembershipInfo>>();
    	for ( RoleMemberImpl rm : rms ) {
    		// gather up the qualifier sets and the service they go with
    		KimRoleTypeService roleTypeService = roleTypeServices.get( rm.getRoleId() );
    		if ( roleTypeService != null ) {
    			List<RoleMembershipInfo> las = roleIdToMembershipMap.get( rm.getRoleId() );
    			if ( las == null ) {
    				las = new ArrayList<RoleMembershipInfo>();
    				roleIdToMembershipMap.put( rm.getRoleId(), las );
    			}
        		RoleMembershipInfo mi = new RoleMembershipInfo( rm.getRoleId(), rm.getRoleMemberId(), rm.getMemberId(), rm.getMemberTypeCode(), rm.getQualifier() );    		
    			las.add( mi );
    		} else {
    			results.add(rm.getQualifier());
    		}
    	}
		for ( String roleId : roleIdToMembershipMap.keySet() ) {
			KimRoleTypeService roleTypeService = roleTypeServices.get( roleId );
			List<RoleMembershipInfo> matchingMembers = roleTypeService.doRoleQualifiersMatchQualification( qualification, roleIdToMembershipMap.get( roleId ) );
			for ( RoleMembershipInfo rmi : matchingMembers ) {
				results.add( rmi.getQualifier() );
			}
		}
    	return results;    	
	}

	public List<AttributeSet> getRoleQualifiersForPrincipal( String principalId, String namespaceCode, String roleName, AttributeSet qualification ) {
		List<String> roleIds = new ArrayList<String>(1);
		roleIds.add(getRoleIdByName(namespaceCode, roleName));
		return getRoleQualifiersForPrincipal(principalId, roleIds, qualification);
	}

	
    // --------------------
    // Role Membership Methods
    // --------------------

    /**
     * @see org.kuali.rice.kim.service.RoleService#getRoleMembers(java.util.List, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    public Collection<RoleMembershipInfo> getRoleMembers(List<String> roleIds, AttributeSet qualification) {
    	return getRoleMembers(roleIds, qualification, true);
    }	
    
    protected Collection<RoleMembershipInfo> getNestedRoleMembers( AttributeSet qualification, RoleMembershipInfo rm ) {
		ArrayList<String> roleIdList = new ArrayList<String>( 1 );
		roleIdList.add( rm.getMemberId() );
		// get the list of members from the nested role - ignore delegations on those sub-roles
		Collection<RoleMembershipInfo> nestedRoleMembers = getRoleMembers( roleIdList, qualification, false );
		// add the roles  whose members matched to the list for delegation checks later
		for ( RoleMembershipInfo rmi : nestedRoleMembers ) {
			rmi.setEmbeddedRoleId( rm.getMemberId() );
		}
		return nestedRoleMembers;
    }
    
	/**
     * @see org.kuali.rice.kim.service.RoleService#getRoleMembers(java.util.List, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    protected Collection<RoleMembershipInfo> getRoleMembers(List<String> roleIds, AttributeSet qualification, boolean followDelegations ) {
    	List<RoleMembershipInfo> results = new ArrayList<RoleMembershipInfo>();
    	Set<String> allRoleIds = new HashSet<String>();
    	// get all implying roles (this also filters to active roles only)
    	for ( String roleId : roleIds ) {
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
    	
    	List<RoleMemberImpl> rms = roleDao.getRoleMembersForRoleIds( allRoleIds, null );    	
    	// build a map of role ID to membership information
    	// this will be used for later qualification checks
    	Map<String,List<RoleMembershipInfo>> roleIdToMembershipMap = new HashMap<String,List<RoleMembershipInfo>>();
    	for ( RoleMemberImpl rm : rms ) {
			RoleMembershipInfo mi = new RoleMembershipInfo( rm.getRoleId(), rm.getRoleMemberId(), rm.getMemberId(), rm.getMemberTypeCode(), rm.getQualifier() );
			// if the qualification check does not need to be made, just add the result
			if ( qualification == null || roleTypeServices.get( rm.getRoleId() ) == null ) {
				if ( rm.getMemberTypeCode().equals( KimRole.ROLE_MEMBER_TYPE ) ) {
					// if a role member type, do a non-recursive role member check
					// to obtain the group and principal members of that role
					// given the qualification
					AttributeSet nestedRoleQualification = qualification; 
					if ( roleTypeServices.get( rm.getRoleId() ) != null ) {
						 nestedRoleQualification = roleTypeServices.get( rm.getRoleId() ).convertQualificationForMemberRoles( roles.get(rm.getRoleId()).getNamespaceCode(), roles.get(rm.getRoleId()).getRoleName(), qualification );
					}
					Collection<RoleMembershipInfo> nestedRoleMembers = getNestedRoleMembers( nestedRoleQualification, mi );
					if ( !nestedRoleMembers.isEmpty() ) {
						results.addAll( nestedRoleMembers );
						matchingRoleIds.add( rm.getRoleId() );
					}
				} else {
					results.add( mi );
					matchingRoleIds.add( rm.getRoleId() );
				}
				matchingRoleIds.add( rm.getRoleId() );
			} else {
				List<RoleMembershipInfo> lrmi = roleIdToMembershipMap.get( mi.getRoleId() );
				if ( lrmi == null ) {
					lrmi = new ArrayList<RoleMembershipInfo>();
					roleIdToMembershipMap.put( mi.getRoleId(), lrmi );
				}
				lrmi.add( mi );
			}
    	}
    	// if there is anything in the role to membership map, we need to check the role type services
    	// for those entries
    	if ( !roleIdToMembershipMap.isEmpty() ) {
    		// for each role, send in all the qualifiers for that role to the type service
    		// for evaluation, the service will return those which match
    		for ( String roleId : roleIdToMembershipMap.keySet() ) {
    			KimRoleTypeService roleTypeService = roleTypeServices.get( roleId );
    			List<RoleMembershipInfo> matchingMembers = roleTypeService.doRoleQualifiersMatchQualification( qualification, roleIdToMembershipMap.get( roleId ) );
    			// loop over the matching entries, adding them to the results
    			for ( RoleMembershipInfo mi : matchingMembers ) {
    				if ( mi.getMemberTypeCode().equals( KimRole.ROLE_MEMBER_TYPE ) ) {
    					// if a role member type, do a non-recursive role member check
    					// to obtain the group and principal members of that role
    					// given the qualification
    					AttributeSet nestedRoleQualification = roleTypeService.convertQualificationForMemberRoles( roles.get(mi.getRoleId()).getNamespaceCode(), roles.get(mi.getRoleId()).getRoleName(), qualification );
    					Collection<RoleMembershipInfo> nestedRoleMembers = getNestedRoleMembers( nestedRoleQualification, mi );
    					if ( !nestedRoleMembers.isEmpty() ) {
    						results.addAll( nestedRoleMembers );
    						matchingRoleIds.add( mi.getRoleId() );
    					}
    				} else {
    					results.add( mi );
    					matchingRoleIds.add( mi.getRoleId() );
    				}
    			}
    		}
    	}
    	
    	// handle application roles
    	for ( String roleId : allRoleIds ) {
    		KimRoleTypeService roleTypeService = roleTypeServices.get( roleId );
    		// check if an application role
    		if ( roleTypeService != null && roleTypeService.isApplicationRoleType() ) {
    			KimRoleImpl role = roles.get( roleId );
        		// for each application role, get the list of principals and groups which are in that role given the qualification (per the role type service)
    			List<String> rolePrincipalIds = roleTypeService.getPrincipalIdsFromApplicationRole( role.getNamespaceCode(), role.getRoleName(), qualification );
    			if ( !rolePrincipalIds.isEmpty() ) {
    				matchingRoleIds.add(roleId);
    				for ( String rolePrincipalId : rolePrincipalIds ) {
    					RoleMembershipInfo mi = new RoleMembershipInfo( roleId, "*", rolePrincipalId, KimRole.PRINCIPAL_MEMBER_TYPE, null ); // CHECK ME: is this correct?  How do we tell what the true "qualifier" is for an application role?
    					results.add( mi );
    				}
    			}
    			// get the groups
    			List<String> roleGroupIds = roleTypeService.getGroupIdsFromApplicationRole( role.getNamespaceCode(), role.getRoleName(), qualification );
    			if ( !roleGroupIds.isEmpty() ) {
    				matchingRoleIds.add(roleId);
    				for ( String roleGroupId : roleGroupIds ) {
    					RoleMembershipInfo mi = new RoleMembershipInfo( roleId, "*", roleGroupId, KimRole.GROUP_MEMBER_TYPE, null ); // CHECK ME: is this correct?  How do we tell what the true "qualifier" is for an application role?
    					results.add( mi );
    				}
    			}
    		}
    	}    	
    	
    	if ( followDelegations && !matchingRoleIds.isEmpty() ) {
	    	// we have a list of RoleMembershipInfo objects
	    	// need to get delegations for distinct list of roles in that list
	    	Map<String,KimDelegationImpl> delegations = roleDao.getDelegationImplMapFromRoleIds( matchingRoleIds );
	    	List<String> applicableDelegationIds = new ArrayList<String>();
	    			
	    	determineDelegations( results, delegations.values(), applicableDelegationIds, roleTypeServices );
	    	addDelegateInformation( results, delegations, applicableDelegationIds, qualification );
    	}
    	
    	return results;
    }
    
    /**
     * Checks each of the result records to determine if there is a potentially applicable
     * delegation for that role membership.  If so, it adds to the applicableDelegationIds
     * list and augments the RoleMembershipInfo object with that delegation ID.
     * 
     * The final determination of whether that delegation has any valid members happens in 
     * a later step ( {@link #addDelegateInformation(List, Map, List, AttributeSet)} )
     */
    protected void determineDelegations( List<RoleMembershipInfo> results,
    		Collection<KimDelegationImpl> delegations,
    		List<String> applicableDelegationIds,
    		Map<String,KimRoleTypeService> roleTypeServices ) {
    	// check each delegation's qualifier to see if they are applicable - matching
    	// against the qualifiers used for that role
    	// so - for each role,
    	for ( RoleMembershipInfo mi : results ) {
    		// get the delegations specific to the role on this line
    		for ( KimDelegationImpl delegation : delegations ) {
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
    	    			mi.getDelegationIds().add( delegation.getDelegationId() );
    				}
    			}
    		}
    	}
    	
    }
    
    protected void addDelegateInformation( List<RoleMembershipInfo> results, 
    		Map<String,KimDelegationImpl> delegations,
    		List<String> applicableDelegationIds,
    		AttributeSet qualification ) {
    	// no applicable delegations, just return
    	if ( applicableDelegationIds.isEmpty() ) {
    		return;
    	}
    	
    	// pull the needed objects from the role DAO
    	Map<String,List<KimDelegationMemberImpl>> delegationMembers = roleDao.getDelegationMembersForDelegationIds(applicableDelegationIds);
    	
		// cache the delegation services for use later
		Map<String,KimDelegationTypeService> delegationTypeServices = getDelegationTypeServicesByDelegationId(applicableDelegationIds, delegations);
		// check delegations assigned to this role
		for ( RoleMembershipInfo mi : results ) {
			// the applicable delegation IDs will already be set in the RoleMembershipInfo object
			// this code examines those delegations and obtains the member groups and principals
			for ( String delegationId : mi.getDelegationIds() ) {
				KimDelegationTypeService delegationTypeService = delegationTypeServices.get( delegationId );
				KimDelegationImpl delegation = delegations.get( delegationId );
				// get the principals and groups for this delegation
				List<KimDelegationMemberInfo> delegationMemberInfo = new ArrayList<KimDelegationMemberInfo>();
				for ( KimDelegationMemberImpl dm : delegationMembers.get( delegationId ) ) {
					delegationMemberInfo.add( dm.toInfo() );
				}
				if ( delegationTypeService != null ) {
					delegationMemberInfo = delegationTypeService.doDelegationQualifiersMatchQualification( qualification, delegationMemberInfo );
				}
				for ( KimDelegationMemberInfo dmi : delegationMemberInfo ) {
    				if ( dmi.getMemberTypeCode().equals( KimRole.PRINCIPAL_MEMBER_TYPE ) 
    						|| dmi.getMemberTypeCode().equals( KimRole.GROUP_MEMBER_TYPE ) ) {
        				// add the delegation information
        				mi.getDelegates().add( 
        						new DelegateInfo( 
        								delegationId, 
        								delegation.getDelegationTypeCode(),
        								dmi.getMemberId(),
        								dmi.getMemberTypeCode(),
        								dmi.getQualifier() ) );
        			} else { // role members
            			// loop over delegation roles and extract the role IDs where the qualifications match
        				ArrayList<String> roleIdTempList = new ArrayList<String>( 1 );
        				roleIdTempList.add( dmi.getMemberId() );
        				
        				// get the members of this role
            			Collection<RoleMembershipInfo> delegateMembers = getRoleMembers(roleIdTempList, qualification, false);
            			// loop over the role members and create the needed DelegationInfo objects
            			for ( RoleMembershipInfo rmi : delegateMembers ) {
            				if ( rmi.getMemberTypeCode().equals( KimRole.PRINCIPAL_MEMBER_TYPE ) ) {
                				mi.getDelegates().add( 
                						new DelegateInfo( 
                								delegationId, 
                								delegation.getDelegationTypeCode(),
                								rmi.getMemberId(),
                								null,
                								dmi.getQualifier() ) );
            				} else if ( rmi.getMemberTypeCode().equals( KimRole.GROUP_MEMBER_TYPE ) ) {
                				mi.getDelegates().add( 
                						new DelegateInfo( 
                								delegationId, 
                								delegation.getDelegationTypeCode(),
                								null,
                								rmi.getMemberId(),
                								dmi.getQualifier() ) );
            				}
            			} // delegate member loop
        			} // if: group/principal or role
				}
			} // delegation IDs for a role membership
		} // role memberships
    }
    
    /**
     * @see org.kuali.rice.kim.service.RoleService#principalHasRole(java.lang.String, java.util.List, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    public boolean principalHasRole(String principalId, List<String> roleIds, AttributeSet qualification) {
    	return principalHasRole( principalId, roleIds, qualification, true );
    }

//    protected Map<String,List<RoleMembershipInfo>> getRoleIdToMembershipMap( List<RoleMemberImpl> roleMembers, AttributeSet qualifications, Map<String,KimRoleTypeService> roleTypeServices, List<RoleMembershipInfo> finalResults, List<String> matchingRoleIds, boolean includeNullServiceMembers, boolean failFast ) {
//    	Map<String,List<RoleMembershipInfo>> roleIdToMembershipMap = new HashMap<String,List<RoleMembershipInfo>>();
//    	for ( RoleMemberImpl rm : roleMembers ) {
//			RoleMembershipInfo mi = new RoleMembershipInfo( rm.getRoleId(), rm.getRoleMemberId(), rm.getMemberId(), rm.getMemberTypeCode(), rm.getQualifier() );
//			List<RoleMembershipInfo> lrmi = roleIdToMembershipMap.get( mi.getRoleId() );
//			if ( lrmi == null ) {
//				lrmi = new ArrayList<RoleMembershipInfo>();
//				roleIdToMembershipMap.put( mi.getRoleId(), lrmi );
//			}
//			lrmi.add( mi );
//    		// if the role type service is null, assume that all qualifiers match
//			if ( roleTypeServices.get( rm.getRoleId() ) == null ) {
//				if ( failFast ) {
//					return roleIdToMembershipMap;
//				}
//			} else {
//			}
//    	}
//    	return roleIdToMembershipMap;
//    }

    /**
     * Helper method used by principalHasRole to build the role ID -> list of members map.
     * 
     * @return <b>true</b> if no further checks are needed because no role service is defined
     */
    protected boolean getRoleIdToMembershipMap( Map<String,List<RoleMembershipInfo>> roleIdToMembershipMap, List<RoleMemberImpl> roleMembers, Map<String,KimRoleTypeService> roleTypeServices ) {
    	for ( RoleMemberImpl rm : roleMembers ) {
			RoleMembershipInfo mi = new RoleMembershipInfo( rm.getRoleId(), rm.getRoleMemberId(), rm.getMemberId(), rm.getMemberTypeCode(), rm.getQualifier() );
    		// if the role type service is null, assume that all qualifiers match
			if ( roleTypeServices.get( rm.getRoleId() ) == null ) {
				return true;
			} else {
				List<RoleMembershipInfo> lrmi = roleIdToMembershipMap.get( mi.getRoleId() );
				if ( lrmi == null ) {
					lrmi = new ArrayList<RoleMembershipInfo>();
					roleIdToMembershipMap.put( mi.getRoleId(), lrmi );
				}
				lrmi.add( mi );
			}
    	}
    	return false;
  	}
    
    
    protected boolean principalHasRole(String principalId, List<String> roleIds, AttributeSet qualification, boolean checkDelegations ) {
    	if ( StringUtils.isBlank( principalId ) ) {
    		return false;
    	}
    	Set<String> allRoleIds = new HashSet<String>();
    	// get all implying roles (this also filters to active roles only)
    	for ( String roleId : roleIds ) {
    		allRoleIds.addAll( getImplyingRoleIds(roleId) );
    	}
    	// short-circuit if no roles match
    	if ( allRoleIds.isEmpty() ) {
    		return false;
    	}
    	// for efficiency, retrieve all roles and store in a map
    	Map<String,KimRoleImpl> roles = roleDao.getRoleImplMap(allRoleIds);
    	// again, for efficiency, obtain and store all role-type services by roleId
    	Map<String,KimRoleTypeService> roleTypeServices = getRoleTypeServicesByRoleId( roles.values() );
    	// get all roles to which the principal is assigned
    	List<RoleMemberImpl> rps = roleDao.getRolePrincipalsForPrincipalIdAndRoleIds(allRoleIds, principalId);

    	// if the qualification is null and the role list is not, then any role in the list will match
    	// so since the role ID list is not blank, we can return true at this point
    	if ( qualification == null && !rps.isEmpty() ) {
    		return true;
    	}    	

    	// check each membership to see if the principal matches
    	
    	// build a map of role ID to membership information
    	// this will be used for later qualification checks
    	Map<String,List<RoleMembershipInfo>> roleIdToMembershipMap = new HashMap<String,List<RoleMembershipInfo>>();
    	if ( getRoleIdToMembershipMap( roleIdToMembershipMap, rps, roleTypeServices ) ) {
    		return true;
    	}
    	
    	// perform the checks against the role type services 
		for ( String roleId : roleIdToMembershipMap.keySet() ) {
			KimRoleTypeService roleTypeService = roleTypeServices.get( roleId );
			if ( !roleTypeService.doRoleQualifiersMatchQualification( qualification, roleIdToMembershipMap.get( roleId ) ).isEmpty() ) {
				return true;
			}
		}

    	// find the groups that the principal belongs to
    	List<String> principalGroupIds = getIdentityManagementService().getGroupIdsForPrincipal(principalId);
    	// find the role/group associations
    	if ( !principalGroupIds.isEmpty() ) {
	    	List<RoleMemberImpl> rgs = roleDao.getRoleGroupsForGroupIdsAndRoleIds( allRoleIds, principalGroupIds);
			roleIdToMembershipMap.clear(); // clear the role/member map for further use 
	    	if ( getRoleIdToMembershipMap( roleIdToMembershipMap, rgs, roleTypeServices ) ) {
	    		return true;
	    	}
	    	
	    	// perform the checks against the role type services 
			for ( String roleId : roleIdToMembershipMap.keySet() ) {
				KimRoleTypeService roleTypeService = roleTypeServices.get( roleId );
				if ( !roleTypeService.doRoleQualifiersMatchQualification( qualification, roleIdToMembershipMap.get( roleId ) ).isEmpty() ) {
					return true;
				}
			}
    	}
    	
    	// check member roles
    	// first, check that the qualifiers on the role membership match
    	// then, perform a principalHasRole on the embedded role
    	List<RoleMemberImpl> rrs = roleDao.getRoleMembersForRoleIds( roleIds, KimRole.ROLE_MEMBER_TYPE );
    	for ( RoleMemberImpl rr : rrs ) {
    		KimRoleTypeService roleTypeService = roleTypeServices.get( rr.getRoleId() );
    		if ( roleTypeService != null ) {
    			if ( roleTypeService.doesRoleQualifierMatchQualification( qualification, rr.getQualifier() ) ) {
    				ArrayList<String> roleIdTempList = new ArrayList<String>( 1 );
    				roleIdTempList.add( rr.getMemberId() );
					AttributeSet nestedRoleQualification = roleTypeService.convertQualificationForMemberRoles( roles.get(rr.getRoleId()).getNamespaceCode(), roles.get(rr.getRoleId()).getRoleName(), qualification );
    				if ( principalHasRole( principalId, roleIdTempList, nestedRoleQualification, false ) ) {
    					return true;
    				}
    			}
    		} else {
    			// no qualifiers - role is always used - check membership
				ArrayList<String> roleIdTempList = new ArrayList<String>( 1 );
				roleIdTempList.add( rr.getMemberId() );
				// no role type service, so can't convert qualification - just pass as is
				if ( principalHasRole( principalId, roleIdTempList, qualification, false ) ) {
					return true;
				}
    		}
    	}
    	
    	
    	// check for application roles and extract principals and groups from that - then check them against the
    	// role type service passing in the qualification and principal - the qualifier comes from the
    	// external system (application)

    	// loop over the allRoleIds list
    	for ( String roleId : allRoleIds ) {
    		KimRoleTypeService roleTypeService = roleTypeServices.get( roleId );
    		// check if an application role
    		if ( roleTypeService != null && roleTypeService.isApplicationRoleType() ) {
    			KimRoleImpl role = roles.get( roleId );
    			if ( roleTypeService.hasApplicationRole(principalId, principalGroupIds, role.getNamespaceCode(), role.getRoleName(), qualification) ) {
    				return true;
    			}
    		}
    	}
    	
    	// delegations
    	if ( checkDelegations ) {
	    	if ( matchesOnDelegation( allRoleIds, principalId, principalGroupIds, qualification, roleTypeServices ) ) {
	    		return true;
	    	}
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
    protected boolean matchesOnDelegation( Set<String> allRoleIds, String principalId, List<String> principalGroupIds, AttributeSet qualification, Map<String,KimRoleTypeService> roleTypeServices ) {
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
    		List<KimDelegationMemberImpl> dps = roleDao.getDelegationPrincipalsForPrincipalIdAndDelegationIds(applicableDelegationIds, principalId);
    		for ( KimDelegationMemberImpl dp : dps ) {
    			KimDelegationTypeService delegationTypeService = delegationTypeServices.get(dp.getDelegationId());
    			// QUESTION: does the qualifier map need to be merged with the main delegation qualification?
    			if ( delegationTypeService == null || delegationTypeService.doesDelegationQualifierMatchQualification(qualification, dp.getQualifier())) {
    				return true;
    			}
    		}
        	// handle roles assigned as members of a delegation - DO NOT follow the role further down
        	// this is really there for application roles whose members are only known to the client application
    		
    		// check groups assigned to this role - use the list of groups from earlier
        	if ( !principalGroupIds.isEmpty() ) {
    	    	List<KimDelegationMemberImpl> dgs = roleDao.getDelegationGroupsForGroupIdsAndDelegationIds( applicableDelegationIds, principalGroupIds);
    	    	
    	    	for ( KimDelegationMemberImpl dg : dgs ) {
    	    		// check the qualifications
    	    		KimDelegationTypeService delegationTypeService = delegationTypeServices.get( dg.getDelegationId() );
    	    		// if the delegation type service is null, assume that all qualifiers match
    				if ( delegationTypeService == null || delegationTypeService.doesDelegationQualifierMatchQualification( qualification, dg.getQualifier() ) ) {
    					return true;
    				}
    	    	}
        	}    		
        	for ( String delegationId : applicableDelegationIds ) {
        		KimDelegationImpl d = delegations.get( delegationId );
        		List<String> roleIdTemp = d.getMemberRoleIds();
        		// recurse to the main role check - but don't allow further delegations to be followed
    			if ( principalHasRole( principalId, roleIdTemp, qualification, false ) ) {
    				return true;
    			}
        	}
    	}
    	
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
    		KimTypeImpl delegationType = delegations.get(delegationId).getKimType();
    		if ( delegationType != null ) {
	    		String serviceName = delegationType.getKimTypeServiceName();
	    		if ( serviceName != null ) {
	    			KimDelegationTypeService delegationTypeService = (KimDelegationTypeService)KIMServiceLocator.getService( serviceName );
	    			if ( delegationTypeService != null ) {
	    	    		roleTypeServices.put(delegationId, delegationTypeService );    				
	    			}
	    		}
    		}
    	}
    	return roleTypeServices;
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
		List<RoleRelationshipImpl> roleRoles = role.getAssignedRoles();

		for (RoleRelationshipImpl roleRole : roleRoles) {
			KimRoleImpl containedRole = getRoleImpl(roleRole.getContainedRoleId());
			// if we've already seen that role (or it's not active), don't recurse into it
			if ( !containedRole.isActive() && !roles.contains( containedRole ) ) {
				roles.add(containedRole);
				getImpliedRolesInternal(containedRole,roles);
			}
		}
	}
    
	public List<String> getImplyingRoleIds( String roleId ) {
		// check the cache
		Map<String,List<String>> cache = impliedRoleCache.get();
		// create the cache if necessary
		if ( cache == null ) {
			cache = new HashMap<String,List<String>>();
			impliedRoleCache.set( cache );
		}
		// check for a non-null result in the cache, return it if found
		List<String> cachedResult = cache.get( roleId );
		if ( cachedResult != null ) {
			return cachedResult;
		}		
		// otherwise, run the query
		Set<String> roleIds = new HashSet<String>();
		
		// add the given role
		if ( isRoleActive( roleId ) ) {
			roleIds.add(roleId);
			getImplyingRolesInternal(roleId, roleIds);		
		}
		List<String> result = new ArrayList<String>( roleIds );
		cache.put( roleId, result );
		return result;
	}
	
	@SuppressWarnings("unchecked")
	protected void getImplyingRolesInternal( String roleId, Set<String> roles ) {
		if ( roleId == null ) {
			return;
		}
		// search role relationships where the given role is the child
		AttributeSet criteria = new AttributeSet();
		criteria.put( "containedRoleId", roleId );
		Collection<RoleRelationshipImpl> rels = getBusinessObjectService().findMatching(RoleRelationshipImpl.class, criteria);
		for ( RoleRelationshipImpl rel : rels ) {
			if ( !roles.contains(rel.getRoleId()) ) {
				if ( isRoleActive( rel.getRoleId() ) ) { 
					roles.add( rel.getRoleId() );
					getImplyingRolesInternal(rel.getRoleId(), roles);
				}
			}
		}
	}
	    
	/**
	 * Gets the roles directly assigned to the given role.
	 */
	@SuppressWarnings("unchecked")
	protected List<RoleMemberImpl> getRolesForDirectGroup(String groupId) {
		AttributeSet criteria = new AttributeSet();
		criteria.put("memberId", groupId);
		criteria.put("memberTypeCode", KimRole.GROUP_MEMBER_TYPE );

		List<RoleMemberImpl> roles = (List<RoleMemberImpl>) getBusinessObjectService().findMatching(RoleMemberImpl.class, criteria);
				
		return roles;
	}	
	

    // --------------------
    // Persistence Methods
    // --------------------

	// TODO: pulling attribute IDs repeadedly is inefficient - consider caching the entire list as a map
	
	@SuppressWarnings("unchecked")
	protected String getKimAttributeId( String attributeName ) {
		Map<String,Object> critieria = new HashMap<String,Object>( 1 );
		critieria.put( "attributeName", attributeName );
		Collection<KimAttributeImpl> defs = getBusinessObjectService().findMatching( KimAttributeImpl.class, critieria );
		return defs.iterator().next().getKimAttributeId();
	}
	
	protected void addMemberAttributeData( RoleMemberImpl roleMember, AttributeSet qualifier, String kimTypeId ) {
		List<RoleMemberAttributeDataImpl> attributes = new ArrayList<RoleMemberAttributeDataImpl>();
		for ( String attributeName : qualifier.keySet() ) {
			RoleMemberAttributeDataImpl a = new RoleMemberAttributeDataImpl();
			a.setAttributeValue( qualifier.get( attributeName ) );
			a.setKimTypeId( kimTypeId );
			a.setTargetPrimaryKey( roleMember.getRoleMemberId() );
			// look up the attribute ID
			a.setKimAttributeId( getKimAttributeId( attributeName ) );
			// pull the next sequence number for the data ID
			a.setAttributeDataId( getSequenceAccessorService().getNextAvailableSequenceNumber( ROLE_MEMBER_DATA_SEQUENCE ).toString() );
			attributes.add( a );
		}
		roleMember.setAttributes( attributes );
	}
	
	protected boolean doesMemberMatch( RoleMemberImpl roleMember, String memberId, String memberTypeCode, AttributeSet qualifier ) {
		if ( roleMember.getMemberId().equals( memberId ) && roleMember.getMemberTypeCode().equals( memberTypeCode ) ) {
			// member ID/type match
    		AttributeSet roleQualifier = roleMember.getQualifier();
    		if ( (qualifier == null || qualifier.isEmpty()) 
    				&& (roleQualifier == null || roleQualifier.isEmpty()) ) {
    			return true; // blank qualifier match
    		} else {
    			if ( qualifier != null && roleQualifier != null && qualifier.equals( roleQualifier ) ) {
    				return true; // qualifier match
    			}
    		}
		}
		return false;
	}
	
	protected boolean doAnyMemberRecordsMatch( List<RoleMemberImpl> roleMembers, String memberId, String memberTypeCode, AttributeSet qualifier ) {
		for ( RoleMemberImpl rm : roleMembers ) {
			if ( doesMemberMatch( rm, memberId, memberTypeCode, qualifier ) ) {
				return true;
			}
		}
		return false;
	}
	
    public void assignPrincipalToRole(String principalId, String namespaceCode, String roleName, AttributeSet qualifier) {
    	// look up the role
    	KimRoleImpl role = getRoleImplByName( namespaceCode, roleName );
    	// check that identical member does not already exist
    	if ( doAnyMemberRecordsMatch( role.getMembers(), principalId, KimRole.PRINCIPAL_MEMBER_TYPE, qualifier ) ) {
    		return;
    	}
    	// create the new role member object
    	RoleMemberImpl newRoleMember = new RoleMemberImpl();
    	// get a new ID from the sequence    	
    	newRoleMember.setRoleMemberId( getSequenceAccessorService().getNextAvailableSequenceNumber( ROLE_MEMBER_SEQUENCE ).toString() );

    	newRoleMember.setRoleId( role.getRoleId() );
    	newRoleMember.setMemberId( principalId );
    	newRoleMember.setMemberTypeCode( KimRole.PRINCIPAL_MEMBER_TYPE );

    	// build role member attribute objects from the given AttributeSet
    	addMemberAttributeData( newRoleMember, qualifier, role.getKimTypeId() );
    	    	
    	// add row to member table
    	getBusinessObjectService().save( newRoleMember );
    }

    public void assignGroupToRole(String groupId, String namespaceCode, String roleName, AttributeSet qualifier) {
    	// look up the role
    	KimRoleImpl role = getRoleImplByName( namespaceCode, roleName );
    	// check that identical member does not already exist
    	if ( doAnyMemberRecordsMatch( role.getMembers(), groupId, KimRole.GROUP_MEMBER_TYPE, qualifier ) ) {
    		return;
    	}
    	// create the new role member object
    	RoleMemberImpl newRoleMember = new RoleMemberImpl();
    	// get a new ID from the sequence    	
    	newRoleMember.setRoleMemberId( getSequenceAccessorService().getNextAvailableSequenceNumber( ROLE_MEMBER_SEQUENCE ).toString() );

    	newRoleMember.setRoleId( role.getRoleId() );
    	newRoleMember.setMemberId( groupId );
    	newRoleMember.setMemberTypeCode( KimRole.GROUP_MEMBER_TYPE );

    	// build role member attribute objects from the given AttributeSet
    	addMemberAttributeData( newRoleMember, qualifier, role.getKimTypeId() );
    	    	
    	// add row to member table
    	getBusinessObjectService().save( newRoleMember );
    }

    public void removePrincipalFromRole(String principalId, String namespaceCode, String roleName, AttributeSet qualifier ) {
    	// look up the role
    	KimRoleImpl role = getRoleImplByName( namespaceCode, roleName );
    	// pull all the principal members
    	// look for an exact qualifier match
		for ( RoleMemberImpl rm : role.getMembers() ) {
			if ( doesMemberMatch( rm, principalId, KimRole.PRINCIPAL_MEMBER_TYPE, qualifier ) ) {
		    	// if found, remove
				getBusinessObjectService().delete( rm );
			}
		}
    }

    public void removeGroupFromRole(String groupId, String namespaceCode, String roleName, AttributeSet qualifier) {
    	// look up the role
    	KimRoleImpl role = getRoleImplByName( namespaceCode, roleName );
    	// pull all the group role members
    	// look for an exact qualifier match
		for ( RoleMemberImpl rm : role.getMembers() ) {
			if ( doesMemberMatch( rm, groupId, KimRole.GROUP_MEMBER_TYPE, qualifier ) ) {
		    	// if found, remove
				getBusinessObjectService().delete( rm );
			}
		}
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

    
	protected IdentityManagementService getIdentityManagementService() {
		if ( identityManagementService == null ) {
			identityManagementService = KIMServiceLocator.getIdentityManagementService();		
		}

		return identityManagementService;
	}

	protected SequenceAccessorService getSequenceAccessorService() {
		if ( sequenceAccessorService == null ) {
			sequenceAccessorService = KNSServiceLocator.getSequenceAccessorService();
		}
		return sequenceAccessorService;
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
	
    public List<KimRoleImpl> getRolesSearchResults(java.util.Map<String,String> fieldValues) {
    	String kimTypeId=null;
        for (Map.Entry<String,String> entry : fieldValues.entrySet()) {
        	if (entry.getKey().equals("kimTypeId")) {
        		kimTypeId=entry.getValue();
        		break;
        	}
        }
    	return roleDao.getRoles(fieldValues, kimTypeId);

    	
    }

}
