/*
 * Copyright 2007 The Kuali Foundation
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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.namespace.QName;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.util.MaxAgeSoftReference;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.bo.group.dto.GroupMembershipInfo;
import org.kuali.rice.kim.bo.group.impl.GroupMemberImpl;
import org.kuali.rice.kim.bo.impl.GroupImpl;
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.dto.DelegateInfo;
import org.kuali.rice.kim.bo.role.dto.DelegateMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.DelegateTypeInfo;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.role.dto.RoleResponsibilityActionInfo;
import org.kuali.rice.kim.bo.role.dto.RoleResponsibilityInfo;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityActionImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;
import org.kuali.rice.kim.dao.KimRoleDao;
import org.kuali.rice.kim.service.IdentityManagementNotificationService;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.ResponsibilityInternalService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.RoleUpdateService;
import org.kuali.rice.kim.service.support.KimDelegationTypeService;
import org.kuali.rice.kim.service.support.KimRoleTypeService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kim.util.KIMWebServiceConstants;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.LookupService;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.ksb.service.KSBServiceLocator;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@WebService(endpointInterface = KIMWebServiceConstants.RoleService.INTERFACE_CLASS, serviceName = KIMWebServiceConstants.RoleService.WEB_SERVICE_NAME, portName = KIMWebServiceConstants.RoleService.WEB_SERVICE_PORT, targetNamespace = KIMWebServiceConstants.MODULE_TARGET_NAMESPACE)
public class RoleServiceImpl implements RoleService, RoleUpdateService {

	private static final Logger LOG = Logger.getLogger( RoleServiceImpl.class );

	private BusinessObjectService businessObjectService;
	private SequenceAccessorService sequenceAccessorService;
	private IdentityManagementService identityManagementService;
	private ResponsibilityInternalService responsibilityInternalService;
	private KimRoleDao roleDao;
	private LookupService lookupService;

    private static final long CACHE_MAX_AGE_SECONDS = 60L;

    private Map<String,MaxAgeSoftReference<RoleImpl>> roleCache = Collections.synchronizedMap( new HashMap<String,MaxAgeSoftReference<RoleImpl>>() );
    private Map<String,MaxAgeSoftReference<List<String>>> impliedRoleCache = Collections.synchronizedMap( new HashMap<String,MaxAgeSoftReference<List<String>>>() );
    private Map<Collection<String>,MaxAgeSoftReference<Map<String,RoleImpl>>> roleImplMapCache = Collections.synchronizedMap( new HashMap<Collection<String>,MaxAgeSoftReference<Map<String,RoleImpl>>>() );

    private Map<String,KimRoleTypeService> roleTypeServiceCache = Collections.synchronizedMap( new HashMap<String,KimRoleTypeService>() );
    private Map<String,KimDelegationTypeService> delegationTypeServiceCache = Collections.synchronizedMap( new HashMap<String,KimDelegationTypeService>() );

    // --------------------
    // Role Data
    // --------------------

	protected RoleImpl getRoleFromCache( String roleId ) {
    	RoleImpl cachedResult = null;
    	MaxAgeSoftReference<RoleImpl> cacheRef = roleCache.get( roleId );
    	if ( cacheRef != null ) {
    		cachedResult = cacheRef.get();
    	}
    	return cachedResult;
    }

	protected RoleImpl getRoleFromCache( String namespaceCode, String roleName ) {
    	RoleImpl cachedResult = null;
    	MaxAgeSoftReference<RoleImpl> cacheRef = roleCache.get( namespaceCode + "-" + roleName );
    	if ( cacheRef != null ) {
    		cachedResult = cacheRef.get();
    	}
    	return cachedResult;
    }

    protected void addRoleImplToCache( RoleImpl role ) {
    	if (role != null) {
    		synchronized ( roleCache ) {
	    		roleCache.put( role.getRoleId(), new MaxAgeSoftReference<RoleImpl>( CACHE_MAX_AGE_SECONDS, role ) );
	    		roleCache.put( role.getNamespaceCode() + "-" + role.getRoleName(), new MaxAgeSoftReference<RoleImpl>( CACHE_MAX_AGE_SECONDS, role ) );
    		}
    	}
    }


	protected List<String> getImpliedRoleIdsFromCache( String roleId ) {
		List<String> cachedResult = null;
    	MaxAgeSoftReference<List<String>> cacheRef = impliedRoleCache.get( roleId );
    	if ( cacheRef != null ) {
    		cachedResult = cacheRef.get();
    	}
    	return cachedResult;
    }

    protected void addImpliedRoleIdsToCache( String roleId, List<String> roleIds ) {
    	impliedRoleCache.put( roleId, new MaxAgeSoftReference<List<String>>( CACHE_MAX_AGE_SECONDS, roleIds ) );
    }

	protected Map<String,RoleImpl> getRoleImplMapFromCache( Collection<String> roleIds ) {
		Map<String,RoleImpl> cachedResult = null;
    	MaxAgeSoftReference<Map<String,RoleImpl>> cacheRef = roleImplMapCache.get( roleIds );
    	if ( cacheRef != null ) {
    		cachedResult = cacheRef.get();
    	}
    	return cachedResult;
    }

    protected void addRoleImplMapToCache( Collection<String> roleIds, Map<String,RoleImpl> roleMap ) {
    	roleImplMapCache.put( roleIds, new MaxAgeSoftReference<Map<String,RoleImpl>>( CACHE_MAX_AGE_SECONDS, roleMap ) );
    }

	protected RoleImpl getRoleImpl(String roleId) {
		if ( StringUtils.isBlank( roleId ) ) {
			return null;
		}
		// check for a non-null result in the cache, return it if found
		RoleImpl cachedResult = getRoleFromCache( roleId );
		if ( cachedResult != null ) {
			return cachedResult;
		}
		// otherwise, run the query
		AttributeSet criteria = new AttributeSet();
		criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleId);
		RoleImpl result = (RoleImpl)getBusinessObjectService().findByPrimaryKey(RoleImpl.class, criteria);
		addRoleImplToCache( result );
		return result;
	}

	/**
	 * @see org.kuali.rice.kim.service.RoleService#getRole(java.lang.String)
	 */
	public KimRoleInfo getRole(String roleId) {
		RoleImpl role = getRoleImpl( roleId );
		if ( role == null ) {
			return null;
		}
		return role.toSimpleInfo();
	}

	/**
	 * @see org.kuali.rice.kim.service.RoleService#getRoleIdByName(java.lang.String, java.lang.String)
	 */
	public String getRoleIdByName(String namespaceCode, String roleName) {
		RoleImpl role = getRoleImplByName( namespaceCode, roleName );
		if ( role == null ) {
			return null;
		}
		return role.getRoleId();
	}

	/**
	 * @see org.kuali.rice.kim.service.RoleService#getRoleByName(java.lang.String, java.lang.String)
	 */
	public KimRoleInfo getRoleByName( String namespaceCode, String roleName ) {
		RoleImpl role = getRoleImplByName( namespaceCode, roleName );
		if ( role != null ) {
			return role.toSimpleInfo();
		}
		return null;
	}

	protected RoleImpl getRoleImplByName( String namespaceCode, String roleName ) {
		if ( StringUtils.isBlank( namespaceCode )
				|| StringUtils.isBlank( roleName ) ) {
			return null;
		}
		// check for a non-null result in the cache, return it if found
		RoleImpl cachedResult = getRoleFromCache( namespaceCode, roleName );
		if ( cachedResult != null ) {
			return cachedResult;
		}
		AttributeSet criteria = new AttributeSet();
		criteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode);
		criteria.put(KimConstants.UniqueKeyConstants.ROLE_NAME, roleName);
		criteria.put(KNSPropertyConstants.ACTIVE, "Y");
		// while this is not actually the primary key - there will be at most one row with these criteria
		RoleImpl result = (RoleImpl)getBusinessObjectService().findByPrimaryKey(RoleImpl.class, criteria);
		addRoleImplToCache( result );
		return result;
	}

	protected Map<String,RoleImpl> getRoleImplMap(Collection<String> roleIds) {
		// check for a non-null result in the cache, return it if found
		Map<String,RoleImpl> cachedResult = getRoleImplMapFromCache( roleIds );
		if ( cachedResult != null ) {
			return cachedResult;
		}
		// otherwise, run the query
		Map<String,RoleImpl> result = roleDao.getRoleImplMap(roleIds);
		addRoleImplMapToCache( roleIds, result );
		return result;
	}

	public List<KimRoleInfo> getRoles(List<String> roleIds) {
		Collection<RoleImpl> roles = getRoleImplMap(roleIds).values();
		List<KimRoleInfo> roleInfos = new ArrayList<KimRoleInfo>( roles.size() );
		for ( RoleImpl r : roles ) {
			roleInfos.add( r.toSimpleInfo() );
		}
		return roleInfos;
	}


	@SuppressWarnings("unchecked")
	public List<KimRoleInfo> lookupRoles(Map<String, String> searchCriteria) {
		Collection<RoleImpl> results = getBusinessObjectService().findMatching(RoleImpl.class, searchCriteria);
		ArrayList<KimRoleInfo> infoResults = new ArrayList<KimRoleInfo>( results.size() );
		for ( RoleImpl role : results ) {
			infoResults.add( role.toSimpleInfo() );
		}
		return infoResults;
	}

	public boolean isRoleActive( String roleId ) {
		RoleImpl role = getRoleImpl( roleId );
		return role != null && role.isActive();
	}

	public List<AttributeSet> getRoleQualifiersForPrincipalIncludingNested( String principalId, String namespaceCode, String roleName, AttributeSet qualification ) {
		String roleId = getRoleIdByName(namespaceCode, roleName);
		if ( roleId == null ) {
			return new ArrayList<AttributeSet>(0);
		}
		return getRoleQualifiersForPrincipalIncludingNested(principalId, Collections.singletonList(roleId), qualification);
	}


	public List<AttributeSet> getRoleQualifiersForPrincipalIncludingNested( String principalId, List<String> roleIds, AttributeSet qualification ) {
		List<AttributeSet> results = new ArrayList<AttributeSet>();

    	Map<String,RoleImpl> roles = getRoleImplMap(roleIds);

    	// get the person's groups
    	List<String> groupIds = getIdentityManagementService().getGroupIdsForPrincipal(principalId);
    	List<RoleMemberImpl> rms = roleDao.getRoleMembersForRoleIdsWithFilters(roleIds, principalId, groupIds);

    	Map<String,List<RoleMembershipInfo>> roleIdToMembershipMap = new HashMap<String,List<RoleMembershipInfo>>();
    	for ( RoleMemberImpl rm : rms ) {
    		KimRoleTypeService roleTypeService = getRoleTypeService( rm.getRoleId() );
    		// gather up the qualifier sets and the service they go with
    		if ( rm.getMemberTypeCode().equals( Role.PRINCIPAL_MEMBER_TYPE )
					|| rm.getMemberTypeCode().equals( Role.GROUP_MEMBER_TYPE ) ) {
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
    		} else if ( rm.getMemberTypeCode().equals( Role.ROLE_MEMBER_TYPE )  ) {
    			// find out if the user has the role
    			// need to convert qualification using this role's service
    			AttributeSet nestedQualification = qualification;
    			if ( roleTypeService != null ) {
    				RoleImpl role = roles.get(rm.getRoleId());
    				// pulling from here as the nested role is not necessarily (and likely is not)
    				// in the roles Map created earlier
    				RoleImpl nestedRole = getRoleImpl(rm.getMemberId());
    				//it is possible that the the roleTypeService is coming from a remote application 
                    // and therefore it can't be guarenteed that it is up and working, so using a try/catch to catch this possibility.
    				try {
    				    nestedQualification = roleTypeService.convertQualificationForMemberRoles(role.getNamespaceCode(), role.getRoleName(), nestedRole.getNamespaceCode(), nestedRole.getRoleName(), qualification);
    				} catch (Exception ex) {
                        LOG.warn("Not able to retrieve RoleTypeService from remote system for role Id: " + role.getRoleId(), ex);
                    }
    			}
    			List<String> nestedRoleId = new ArrayList<String>(1);
    			nestedRoleId.add( rm.getMemberId() );
    			// if the user has the given role, add the qualifier the *nested role* has with the
    			// originally queries role
    			if ( principalHasRole( principalId, nestedRoleId, nestedQualification, false ) ) {
    				results.add( rm.getQualifier() );
    			}
    		}
    	}
		for ( String roleId : roleIdToMembershipMap.keySet() ) {
			KimRoleTypeService roleTypeService = getRoleTypeService( roleId );
			//it is possible that the the roleTypeService is coming from a remote application 
            // and therefore it can't be guarenteed that it is up and working, so using a try/catch to catch this possibility.
            try {
    			List<RoleMembershipInfo> matchingMembers = roleTypeService.doRoleQualifiersMatchQualification( qualification, roleIdToMembershipMap.get( roleId ) );
    			for ( RoleMembershipInfo rmi : matchingMembers ) {
    				results.add( rmi.getQualifier() );
    			}
            } catch (Exception ex) {
                LOG.warn("Not able to retrieve RoleTypeService from remote system for role Id: " + roleId, ex);
            }
		}
    	return results;
	}

	public List<AttributeSet> getRoleQualifiersForPrincipal( String principalId, List<String> roleIds, AttributeSet qualification ) {
		List<AttributeSet> results = new ArrayList<AttributeSet>();

    	// TODO: ? get groups for principal and get those as well?
    	// this implementation may be incomplete, as groups and sub-roles are not considered
    	List<RoleMemberImpl> rms = roleDao.getRoleMembersForRoleIdsWithFilters(roleIds, principalId, null);

    	Map<String,List<RoleMembershipInfo>> roleIdToMembershipMap = new HashMap<String,List<RoleMembershipInfo>>();
    	for ( RoleMemberImpl rm : rms ) {
    		// gather up the qualifier sets and the service they go with
    		if ( rm.getMemberTypeCode().equals( Role.PRINCIPAL_MEMBER_TYPE )) {
	    		KimRoleTypeService roleTypeService = getRoleTypeService( rm.getRoleId() );
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
    	}
		for ( String roleId : roleIdToMembershipMap.keySet() ) {
			KimRoleTypeService roleTypeService = getRoleTypeService( roleId );
			//it is possible that the the roleTypeService is coming from a remote application 
            // and therefore it can't be guarenteed that it is up and working, so using a try/catch to catch this possibility.
            try {
    			List<RoleMembershipInfo> matchingMembers = roleTypeService.doRoleQualifiersMatchQualification( qualification, roleIdToMembershipMap.get( roleId ) );
    			for ( RoleMembershipInfo rmi : matchingMembers ) {
    				results.add( rmi.getQualifier() );
    			}
            } catch (Exception ex) {
                LOG.warn("Not able to retrieve RoleTypeService from remote system for role Id: " + roleId, ex);
            }
		}
    	return results;
	}

	public List<AttributeSet> getRoleQualifiersForPrincipal( String principalId, String namespaceCode, String roleName, AttributeSet qualification ) {
		return getRoleQualifiersForPrincipal(
				principalId,
				Collections.singletonList(getRoleIdByName(namespaceCode, roleName)),
				qualification);
	}


    // --------------------
    // Role Membership Methods
    // --------------------

    /**
     * @see org.kuali.rice.kim.service.RoleService#getRoleMembers(java.util.List, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    public List<RoleMembershipInfo> getRoleMembers(List<String> roleIds, AttributeSet qualification) {
    	return getRoleMembers(roleIds, qualification, true);
    }

    protected RoleMemberImpl getRoleMemberImpl( String roleMemberId ) {
    	return (RoleMemberImpl)getBusinessObjectService().findByPrimaryKey(
    			RoleMemberImpl.class,
    			Collections.singletonMap(KIMPropertyConstants.RoleMember.ROLE_MEMBER_ID, roleMemberId) );
    }

	public Collection<String> getRoleMemberPrincipalIds(String namespaceCode, String roleName, AttributeSet qualification) {
		Set<String> principalIds = new HashSet<String>();
		List<String> roleIds = Collections.singletonList(getRoleIdByName(namespaceCode, roleName));
    	for (RoleMembershipInfo roleMembershipInfo : getRoleMembers(roleIds, qualification, false)) {
    		if (Role.GROUP_MEMBER_TYPE.equals(roleMembershipInfo.getMemberTypeCode())) {
    			principalIds.addAll(getIdentityManagementService().getGroupMemberPrincipalIds(roleMembershipInfo.getMemberId()));
    		}
    		else {
    			principalIds.add(roleMembershipInfo.getMemberId());
    		}
		}
    	return principalIds;
	}

    protected Collection<RoleMembershipInfo> getNestedRoleMembers( AttributeSet qualification, RoleMembershipInfo rm ) {
		ArrayList<String> roleIdList = new ArrayList<String>( 1 );
		roleIdList.add( rm.getMemberId() );
		// get the list of members from the nested role - ignore delegations on those sub-roles
		Collection<RoleMembershipInfo> nestedRoleMembers = getRoleMembers( roleIdList, qualification, false );
		// add the roles  whose members matched to the list for delegation checks later
		for ( RoleMembershipInfo rmi : nestedRoleMembers ) {
			// use the member ID of the parent role (needed for responsibility joining)
			rmi.setRoleMemberId( rm.getRoleMemberId() );
			// store the role ID, so we know where this member actually came from
			rmi.setRoleId( rm.getRoleId() );
			rmi.setEmbeddedRoleId( rm.getMemberId() );
		}
		return nestedRoleMembers;
    }

	/**
     * @see org.kuali.rice.kim.service.RoleService#getRoleMembers(java.util.List, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    protected List<RoleMembershipInfo> getRoleMembers(List<String> roleIds, AttributeSet qualification, boolean followDelegations ) {
    	List<RoleMembershipInfo> results = new ArrayList<RoleMembershipInfo>();
    	Set<String> allRoleIds = new HashSet<String>();
    	for ( String roleId : roleIds ) {
    		if ( isRoleActive(roleId) ) {
    			allRoleIds.add(roleId);
    		}
    	}
    	// short-circuit if no roles match
    	if ( allRoleIds.isEmpty() ) {
    		return results;
    	}
    	Set<String> matchingRoleIds = new HashSet<String>( allRoleIds.size() );
    	// for efficiency, retrieve all roles and store in a map
    	Map<String,RoleImpl> roles = getRoleImplMap(allRoleIds);

    	List<RoleMemberImpl> rms = roleDao.getRoleMembersForRoleIds( allRoleIds, null );
    	// build a map of role ID to membership information
    	// this will be used for later qualification checks
    	Map<String,List<RoleMembershipInfo>> roleIdToMembershipMap = new HashMap<String,List<RoleMembershipInfo>>();
    	for ( RoleMemberImpl rm : rms ) {
			RoleMembershipInfo mi = new RoleMembershipInfo( rm.getRoleId(), rm.getRoleMemberId(), rm.getMemberId(), rm.getMemberTypeCode(), rm.getQualifier() );
			// if the qualification check does not need to be made, just add the result
			if ( (qualification == null || qualification.isEmpty()) || getRoleTypeService( rm.getRoleId() ) == null ) {
				if ( rm.getMemberTypeCode().equals( Role.ROLE_MEMBER_TYPE ) ) {
					// if a role member type, do a non-recursive role member check
					// to obtain the group and principal members of that role
					// given the qualification
					AttributeSet nestedRoleQualification = qualification;
					if ( getRoleTypeService( rm.getRoleId() ) != null ) {
	                    // get the member role object
					    RoleImpl memberRole = getRoleImpl( mi.getMemberId() );
						nestedRoleQualification = getRoleTypeService( rm.getRoleId() )
						         .convertQualificationForMemberRoles(
						                 roles.get(rm.getRoleId()).getNamespaceCode(),
						                 roles.get(rm.getRoleId()).getRoleName(),
						                 memberRole.getNamespaceCode(),
						                 memberRole.getRoleName(),
						                 qualification );
					}
					if ( isRoleActive( rm.getRoleId() ) ) {
						Collection<RoleMembershipInfo> nestedRoleMembers = getNestedRoleMembers( nestedRoleQualification, mi );
						if ( !nestedRoleMembers.isEmpty() ) {
							results.addAll( nestedRoleMembers );
							matchingRoleIds.add( rm.getRoleId() );
						}
					}
				} else { // not a role member type
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
    		    //it is possible that the the roleTypeService is coming from a remote application 
                // and therefore it can't be guarenteed that it is up and working, so using a try/catch to catch this possibility.
                try {
        			KimRoleTypeService roleTypeService = getRoleTypeService( roleId );
        			List<RoleMembershipInfo> matchingMembers = roleTypeService.doRoleQualifiersMatchQualification( qualification, roleIdToMembershipMap.get( roleId ) );
        			// loop over the matching entries, adding them to the results
        			for ( RoleMembershipInfo mi : matchingMembers ) {
        				if ( mi.getMemberTypeCode().equals( Role.ROLE_MEMBER_TYPE ) ) {
        					// if a role member type, do a non-recursive role member check
        					// to obtain the group and principal members of that role
        					// given the qualification
                            // get the member role object
                            RoleImpl memberRole = getRoleImpl( mi.getMemberId() );
                            if ( memberRole.isActive() ) {
    	    					AttributeSet nestedRoleQualification = roleTypeService.convertQualificationForMemberRoles(
    	    					        roles.get(mi.getRoleId()).getNamespaceCode(),
    	    					        roles.get(mi.getRoleId()).getRoleName(),
    	                                memberRole.getNamespaceCode(),
    	                                memberRole.getRoleName(),
    	    					        qualification );
    	    					Collection<RoleMembershipInfo> nestedRoleMembers = getNestedRoleMembers( nestedRoleQualification, mi );
    	    					if ( !nestedRoleMembers.isEmpty() ) {
    	    						results.addAll( nestedRoleMembers );
    	    						matchingRoleIds.add( mi.getRoleId() );
    	    					}
                            }
        				} else { // not a role member
        					results.add( mi );
        					matchingRoleIds.add( mi.getRoleId() );
        				}
        			}
        		} catch (Exception ex) {
                    LOG.warn("Not able to retrieve RoleTypeService from remote system for role Id: " + roleId, ex);
                }
    		}
    	}

    	// handle application roles
    	for ( String roleId : allRoleIds ) {
    		KimRoleTypeService roleTypeService = getRoleTypeService( roleId );
    		// check if an application role
            try {
        		if ( roleTypeService != null && roleTypeService.isApplicationRoleType() ) {
        			RoleImpl role = roles.get( roleId );
                    // for each application role, get the list of principals and groups which are in that role given the qualification (per the role type service)
        			List<RoleMembershipInfo> roleMembers = roleTypeService.getRoleMembersFromApplicationRole(role.getNamespaceCode(), role.getRoleName(), qualification);
        			if ( !roleMembers.isEmpty()  ) {
        				matchingRoleIds.add( roleId );
        			}
        			for ( RoleMembershipInfo rm : roleMembers ) {
        			    rm.setRoleId(roleId);
        			    rm.setRoleMemberId("*");
        			}
        			results.addAll(roleMembers);
        		}
            } catch (Exception ex) {
                LOG.warn("Not able to retrieve RoleTypeService from remote system for role Id: " + roleId, ex);
            }
    	}

    	if ( followDelegations && !matchingRoleIds.isEmpty() ) {
	    	// we have a list of RoleMembershipInfo objects
	    	// need to get delegations for distinct list of roles in that list
	    	Map<String,KimDelegationImpl> delegations = roleDao.getDelegationImplMapFromRoleIds( matchingRoleIds );

	    	matchDelegationsToRoleMembers( results, delegations.values() );
	    	resolveDelegationMembers( results, qualification );
    	}

    	// sort the results if a single role type service can be identified for
    	// all the matching role members
    	if ( results.size() > 1 ) {
        	// if a single role: easy case
        	if ( matchingRoleIds.size() == 1 ) {
        	    String roleId = matchingRoleIds.iterator().next();
        		KimRoleTypeService kimRoleTypeService = getRoleTypeService( roleId );
        		//it is possible that the the roleTypeService is coming from a remote application 
                // and therefore it can't be guarenteed that it is up and working, so using a try/catch to catch this possibility.
                try {
            		if ( kimRoleTypeService != null ) {
            			results = kimRoleTypeService.sortRoleMembers( results );
            		}
                } catch (Exception ex) {
                    LOG.warn("Not able to retrieve RoleTypeService from remote system for role Id: " + roleId, ex);
                }
        	} else if ( matchingRoleIds.size() > 1 ) {
        		// if more than one, check if there is only a single role type service
            	String prevServiceName = null;
            	boolean multipleServices = false;
        		for ( String roleId : matchingRoleIds ) {
        			String serviceName = getRoleImpl( roleId ).getKimRoleType().getKimTypeServiceName();
        			if ( prevServiceName != null && !StringUtils.equals( prevServiceName, serviceName ) ) {
        				multipleServices = true;
        				break;
        			}
    				prevServiceName = serviceName;
        		}
        		if ( !multipleServices ) {
        		    String roleId = matchingRoleIds.iterator().next();
        		    //it is possible that the the roleTypeService is coming from a remote application 
                    // and therefore it can't be guarenteed that it is up and working, so using a try/catch to catch this possibility.
                    try {                       
                		KimRoleTypeService kimRoleTypeService = getRoleTypeService( roleId );
                		if ( kimRoleTypeService != null ) {
                			results = kimRoleTypeService.sortRoleMembers( results );
                		}
            		} catch (Exception ex) {
                        LOG.warn("Not able to retrieve RoleTypeService from remote system for role Id: " + roleId, ex);
                    }
        		} else {
        			LOG.warn( "Did not sort role members - multiple role type services found.  Role Ids: " + matchingRoleIds );
        		}
        	}
    	}

    	return results;
    }

    protected KimRoleTypeService getRoleTypeService( String roleId ) {
        KimRoleTypeService service = roleTypeServiceCache.get( roleId );
    	if ( service == null && !roleTypeServiceCache.containsKey( roleId ) ) {
    		RoleImpl role = getRoleImpl( roleId );
    		KimTypeInfo roleType = role.getKimRoleType();
    		if ( roleType != null ) {
        		service = getRoleTypeService(roleType);
    		}
			roleTypeServiceCache.put(roleId, service);
    	}
    	return service;
    }
    
    protected KimRoleTypeService getRoleTypeService( KimTypeInfo typeInfo ) {
		String serviceName = typeInfo.getKimTypeServiceName();
		if ( serviceName != null ) {
			try {
				KimTypeService service = (KimTypeService)KIMServiceLocator.getService( serviceName );
				if ( service != null && service instanceof KimRoleTypeService) {
					return (KimRoleTypeService)service;
				} else {
					return (KimRoleTypeService)KIMServiceLocator.getService( "kimNoMembersRoleTypeService" );
				}
			} catch ( Exception ex ) {
				LOG.error( "Unable to find role type service with name: " + serviceName );
				LOG.error( ex.getClass().getName() + " : " + ex.getMessage() );
				return (KimRoleTypeService)KIMServiceLocator.getService( "kimNoMembersRoleTypeService" );
			}
		}
		return null;
    }

    protected KimDelegationTypeService getDelegationTypeService( String delegationId ) {
    	KimDelegationTypeService service = delegationTypeServiceCache.get( delegationId );
    	if ( service == null && !delegationTypeServiceCache.containsKey( delegationId ) ) {
    		Map<String,String> pk = new HashMap<String,String>(1);
    		pk.put( KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegationId );
    		KimDelegationImpl delegation = (KimDelegationImpl)getBusinessObjectService().findByPrimaryKey( KimDelegationImpl.class, pk );
    		KimTypeInfo delegationType = delegation.getKimType();
    		if ( delegationType != null ) {
    			KimTypeService tempService = KimCommonUtils.getKimTypeService(delegationType);
    			if ( tempService != null && tempService instanceof KimDelegationTypeService ) {
    				service = (KimDelegationTypeService)tempService;
    			} else {
    				LOG.error( "Service returned for type " + delegationType + "("+delegationType.getKimTypeServiceName()+") was not a KimDelegationTypeService.  Was a " + tempService.getClass() );
    			}
    		} else { // delegation has no type - default to role type if possible
    			KimRoleTypeService roleTypeService = getRoleTypeService( delegation.getRoleId() );
    			if ( roleTypeService != null && roleTypeService instanceof KimDelegationTypeService ) {
    				service = (KimDelegationTypeService)roleTypeService;
    			}
    		}
    		delegationTypeServiceCache.put(delegationId, service);
    	}
    	return service;
    }

    /**
     * Checks each of the result records to determine if there are potentially applicable
     * delegation members for that role membership.  If so, it adds to the delegates
     * list on that RoleMembershipInfo object.
     *
     * The final determination of whether that delegation member matches the qualification happens in
     * a later step ( {@link #resolveDelegationMembers(List, Map, AttributeSet)} )
     */
    protected void matchDelegationsToRoleMembers( List<RoleMembershipInfo> results,
    		Collection<KimDelegationImpl> delegations ) {
    	// check each delegation's qualifier to see if they are applicable - matching
    	// against the qualifiers used for that role
    	// so - for each role,
    	for ( RoleMembershipInfo mi : results ) {
    		// get the delegations specific to the role on this line
    		for ( KimDelegationImpl delegation : delegations ) {
    			// only check at the moment if the role IDs match
    			if ( StringUtils.equals( delegation.getRoleId(), mi.getRoleId() ) ) {
    	    		KimRoleTypeService roleTypeService = getRoleTypeService( delegation.getRoleId() );
    	    		for ( KimDelegationMemberImpl delegationMember : delegation.getMembers() ) {
    	    			// first, check the delegation to see if it has a role member ID set
    	    			// if so, then this is a personal delegation rather than a qualifier delegation
    	    			// in this case, the qualifiers must also match, allowing a person to
    	    			// delegate only part of their authority.
    	    			if ( StringUtils.isBlank( delegationMember.getRoleMemberId() )
    	    					|| StringUtils.equals( delegationMember.getRoleMemberId(), mi.getRoleMemberId() ) ) {
    	    	    		// this check is against the qualifier from the role relationship
    	    	    		// that resulted in this record
    	    	    		// This is to determine that the delegation
    	    				// but - don't need to check the role qualifiers against delegation qualifiers
    	    				// if the delegation is linked directly to the role member
		    	    		if ( (StringUtils.isNotBlank( delegationMember.getRoleMemberId() ) 
		    	    					&& StringUtils.equals( delegationMember.getRoleMemberId(), mi.getRoleMemberId() )
		    	    					)
		    	    				|| roleTypeService == null
		    	    				|| roleTypeService.doesRoleQualifierMatchQualification( mi.getQualifier(), delegationMember.getQualifier() ) ) {
		    	    			// add the delegate member to the role member information list
		    	    			// these will be checked by a later method against the request
		    	    			// qualifications
		    	    			mi.getDelegates().add(
		    	    					new DelegateInfo(
		    	    							delegation.getDelegationId(),
		    	    							delegation.getDelegationTypeCode(),
		    	    							delegationMember.getMemberId(),
		    	    							delegationMember.getMemberTypeCode(),
		    	    							delegationMember.getRoleMemberId(),
		    	    							delegationMember.getQualifier() ) );
		    				}
    	    			}
    	    		}
    			}
    		}
    	}

    }

    /**
     * Once the delegations for a RoleMembershipInfo object have been determined,
     * any "role" member types need to be resolved into groups and principals so that
     * further KIM requests are not needed.
     */
    protected void resolveDelegationMembers( List<RoleMembershipInfo> results,
    		AttributeSet qualification ) {

		// check delegations assigned to this role
		for ( RoleMembershipInfo mi : results ) {
			// the applicable delegation IDs will already be set in the RoleMembershipInfo object
			// this code examines those delegations and obtains the member groups and principals
			ListIterator<DelegateInfo> i = mi.getDelegates().listIterator();
			while ( i.hasNext() ) {
				DelegateInfo di = i.next();
				KimDelegationTypeService delegationTypeService = getDelegationTypeService( di.getDelegationId() );
				// get the principals and groups for this delegation
				// purge any entries that do not match per the type service
				if ( delegationTypeService == null || delegationTypeService.doesDelegationQualifierMatchQualification( qualification, di.getQualifier() )) {
					// check if a role type which needs to be resolved
					if ( di.getMemberTypeCode().equals( Role.ROLE_MEMBER_TYPE ) ) {
					    i.remove();
            			// loop over delegation roles and extract the role IDs where the qualifications match
        				ArrayList<String> roleIdTempList = new ArrayList<String>( 1 );
        				roleIdTempList.add( di.getMemberId() );

        				// get the members of this role
            			Collection<RoleMembershipInfo> delegateMembers = getRoleMembers(roleIdTempList, qualification, false);
            			// loop over the role members and create the needed DelegationInfo objects
            			for ( RoleMembershipInfo rmi : delegateMembers ) {
            				i.add(
            						new DelegateInfo(
            								di.getDelegationId(),
            								di.getDelegationTypeCode(),
            								rmi.getMemberId(),
            								rmi.getMemberTypeCode(),
            								di.getRoleMemberId(),
            								di.getQualifier() ) );
            			} // delegate member loop
					} // if is role member type
				} else { // delegation does not match - remove from list
					i.remove();
				}
			}
		}
    }

    /**
     * @see org.kuali.rice.kim.service.RoleService#principalHasRole(java.lang.String, java.util.List, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    public boolean principalHasRole(String principalId, List<String> roleIds, AttributeSet qualification) {
    	return principalHasRole( principalId, roleIds, qualification, true );
    }

    /**
     * @see org.kuali.rice.kim.service.RoleService#getPrincipalIdSubListWithRole(java.util.List, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    public List<String> getPrincipalIdSubListWithRole( List<String> principalIds, String roleNamespaceCode, String roleName, AttributeSet qualification ) {
    	List<String> subList = new ArrayList<String>();
    	RoleImpl role = getRoleImplByName( roleNamespaceCode, roleName );
    	for ( String principalId : principalIds ) {
    		if ( principalHasRole( principalId, Collections.singletonList( role.getRoleId() ), qualification ) ) {
    			subList.add( principalId );
    		}
    	}
    	return subList;
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
    protected boolean getRoleIdToMembershipMap( Map<String,List<RoleMembershipInfo>> roleIdToMembershipMap, List<RoleMemberImpl> roleMembers ) {
    	for ( RoleMemberImpl rm : roleMembers ) {
			RoleMembershipInfo mi = new RoleMembershipInfo( rm.getRoleId(), rm.getRoleMemberId(), rm.getMemberId(), rm.getMemberTypeCode(), rm.getQualifier() );
    		// if the role type service is null, assume that all qualifiers match
			if ( getRoleTypeService( rm.getRoleId() ) == null ) {
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
    	// remove inactive roles
    	for ( String roleId : roleIds ) {
    		if ( isRoleActive(roleId) ) {
    			allRoleIds.add(roleId);
    		}
    	}
    	// short-circuit if no roles match
    	if ( allRoleIds.isEmpty() ) {
    		return false;
    	}
    	// for efficiency, retrieve all roles and store in a map
    	Map<String,RoleImpl> roles = getRoleImplMap(allRoleIds);
    	// get all roles to which the principal is assigned
    	List<RoleMemberImpl> rps = roleDao.getRolePrincipalsForPrincipalIdAndRoleIds(allRoleIds, principalId);

    	// if the qualification is null and the role list is not, then any role in the list will match
    	// so since the role ID list is not blank, we can return true at this point
    	if ( (qualification == null || qualification.isEmpty()) && !rps.isEmpty() ) {
    		return true;
    	}

    	// check each membership to see if the principal matches

    	// build a map of role ID to membership information
    	// this will be used for later qualification checks
    	Map<String,List<RoleMembershipInfo>> roleIdToMembershipMap = new HashMap<String,List<RoleMembershipInfo>>();
    	if ( getRoleIdToMembershipMap( roleIdToMembershipMap, rps ) ) {
    		return true;
    	}

    	// perform the checks against the role type services
		for ( String roleId : roleIdToMembershipMap.keySet() ) {
		    try {
    			KimRoleTypeService roleTypeService = getRoleTypeService( roleId );
    			if ( !roleTypeService.doRoleQualifiersMatchQualification( qualification, roleIdToMembershipMap.get( roleId ) ).isEmpty() ) {
    				return true;
    			}
		    } catch ( Exception ex ) {
                LOG.warn( "Unable to find role type service with id: " + roleId );
            }
		}

    	// find the groups that the principal belongs to
    	List<String> principalGroupIds = getIdentityManagementService().getGroupIdsForPrincipal(principalId);
    	// find the role/group associations
    	if ( !principalGroupIds.isEmpty() ) {
	    	List<RoleMemberImpl> rgs = roleDao.getRoleGroupsForGroupIdsAndRoleIds( allRoleIds, principalGroupIds);
			roleIdToMembershipMap.clear(); // clear the role/member map for further use
	    	if ( getRoleIdToMembershipMap( roleIdToMembershipMap, rgs ) ) {
	    		return true;
	    	}

	    	// perform the checks against the role type services
			for ( String roleId : roleIdToMembershipMap.keySet() ) {
			    try {
    				KimRoleTypeService roleTypeService = getRoleTypeService( roleId );
    				if ( !roleTypeService.doRoleQualifiersMatchQualification( qualification, roleIdToMembershipMap.get( roleId ) ).isEmpty() ) {
    					return true;
    				}
    			} catch ( Exception ex ) {
                    LOG.warn( "Unable to find role type service with id: " + roleId );
                }
			}
    	}

    	// check member roles
    	// first, check that the qualifiers on the role membership match
    	// then, perform a principalHasRole on the embedded role
    	List<RoleMemberImpl> rrs = roleDao.getRoleMembersForRoleIds( roleIds, Role.ROLE_MEMBER_TYPE );
    	for ( RoleMemberImpl rr : rrs ) {
    		KimRoleTypeService roleTypeService = getRoleTypeService( rr.getRoleId() );
    		if ( roleTypeService != null ) {
    			//it is possible that the the roleTypeService is coming from a remote application 
    		    // and therefore it can't be guarenteed that it is up and working, so using a try/catch to catch this possibility.
    		    try {
        		    if ( roleTypeService.doesRoleQualifierMatchQualification( qualification, rr.getQualifier() ) ) {
                        RoleImpl memberRole = getRoleImpl( rr.getMemberId() );
    					AttributeSet nestedRoleQualification = roleTypeService.convertQualificationForMemberRoles(
    					        roles.get(rr.getRoleId()).getNamespaceCode(),
    					        roles.get(rr.getRoleId()).getRoleName(),
                                memberRole.getNamespaceCode(),
                                memberRole.getRoleName(),
                                qualification );
                        ArrayList<String> roleIdTempList = new ArrayList<String>( 1 );
                        roleIdTempList.add( rr.getMemberId() );
        				if ( principalHasRole( principalId, roleIdTempList, nestedRoleQualification, true ) ) {
        					return true;
        				}
        			}	
    			} catch (Exception ex) {
                    LOG.warn("Not able to retrieve RoleTypeService from remote system for role Id: " + rr.getRoleId(), ex);
                    //return false; 
                }
    		} else {
    			// no qualifiers - role is always used - check membership
				ArrayList<String> roleIdTempList = new ArrayList<String>( 1 );
				roleIdTempList.add( rr.getMemberId() );
				// no role type service, so can't convert qualification - just pass as is
				if ( principalHasRole( principalId, roleIdTempList, qualification, true ) ) {
					return true;
				}
    		}
    	    
    	}


    	// check for application roles and extract principals and groups from that - then check them against the
    	// role type service passing in the qualification and principal - the qualifier comes from the
    	// external system (application)

    	// loop over the allRoleIds list
    	for ( String roleId : allRoleIds ) {
    		KimRoleTypeService roleTypeService = getRoleTypeService( roleId );
    		// check if an application role
    		//it is possible that the the roleTypeService is coming from a remote application 
            // and therefore it can't be guarenteed that it is up and working, so using a try/catch to catch this possibility.
            try {
        		if ( roleTypeService != null && roleTypeService.isApplicationRoleType() ) {
        			RoleImpl role = roles.get( roleId );
        			if ( roleTypeService.hasApplicationRole(principalId, principalGroupIds, role.getNamespaceCode(), role.getRoleName(), qualification) ) {
        				return true;
        			}
        		}
            } catch (Exception ex) {
                LOG.warn("Not able to retrieve RoleTypeService from remote system for role Id: " + roleId, ex);
                //return false; 
            }
    	}

    	// delegations
    	if ( checkDelegations ) {
	    	if ( matchesOnDelegation( allRoleIds, principalId, principalGroupIds, qualification ) ) {
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
    protected boolean matchesOnDelegation( Set<String> allRoleIds, String principalId, List<String> principalGroupIds, AttributeSet qualification ) {
    	// get the list of delegations for the roles
    	Map<String,KimDelegationImpl> delegations = roleDao.getDelegationImplMapFromRoleIds(allRoleIds);
    	// loop over the delegations - determine those which need to be inspected more directly
    	for ( KimDelegationImpl delegation : delegations.values() ) {
        	// check if each one matches via the original role type service
    		if ( !delegation.isActive() ) {
    			continue;
    		}
    		KimRoleTypeService roleTypeService = getRoleTypeService( delegation.getRoleId() );
    		for ( KimDelegationMemberImpl dmi : delegation.getMembers() ) {
    			if ( !dmi.isActive() ) {
    				continue;
    			}
    			// check if this delegation record applies to the given person
    			if ( dmi.getMemberTypeCode().equals( Role.PRINCIPAL_MEMBER_TYPE )
    					&& !dmi.getMemberId().equals( principalId ) ) {
    				continue; // no match on principal
    			}
    			// or if a group
    			if ( dmi.getMemberTypeCode().equals( Role.GROUP_MEMBER_TYPE )
    					&& !principalGroupIds.contains( dmi.getMemberId() ) ) {
    				continue; // no match on group
    			}
    			// or if a role
    			if ( dmi.getMemberTypeCode().equals( Role.ROLE_MEMBER_TYPE )
    					&& !principalHasRole( principalId, Collections.singletonList( dmi.getMemberId() ), qualification, false ) ) {
    				continue; // no match on role
    			}
    			// OK, the member matches the current user, now check the qualifications

    			// NOTE: this compare is slightly different then the member enumeration
    			// since the requested qualifier is always being used rather than
    			// the role qualifier for the member (which is not available)
        		if ( roleTypeService == null || roleTypeService.doesRoleQualifierMatchQualification( qualification, dmi.getQualifier() ) ) {
    				// role service matches this qualifier
        			// now try the delegation service
        			KimDelegationTypeService delegationTypeService = getDelegationTypeService( dmi.getDelegationId());
        			// QUESTION: does the qualifier map need to be merged with the main delegation qualification?
        			if ( delegationTypeService != null && !delegationTypeService.doesDelegationQualifierMatchQualification(qualification, dmi.getQualifier())) {
        				continue; // no match - skip to next record
        			}
        			// check if a role member ID is present on the delegation record
        			// if so, check that the original role member would match the given qualifiers
        			if ( StringUtils.isNotBlank( dmi.getRoleMemberId() ) ) {
        				RoleMemberImpl rm = getRoleMemberImpl( dmi.getRoleMemberId() );
        				if ( rm != null ) {
        					// check that the original role member's is active and that their
        					// qualifier would have matched this request's
        					// qualifications (that the original person would have the permission/responsibility
        					// for an action)
        					// this prevents a role-membership based delegation from surviving the inactivation/
        					// changing of the main person's role membership
        					if ( !rm.isActive() ) {
        						continue;
        					}
        					AttributeSet roleQualifier = rm.getQualifier();
        					if ( !roleTypeService.doesRoleQualifierMatchQualification(qualification, roleQualifier) ) {
        						continue;
        					}
        				} else {
        					LOG.warn( "Unknown role member ID cited in the delegation member table:" );
        					LOG.warn( "       delegationMemberId: " + dmi.getDelegationMemberId() + " / roleMemberId: " + dmi.getRoleMemberId() );
        				}
        			}
        			// all tests passed, return true
        			return true;
    			} else {
    				continue; // no match - skip to next record
    			}
    		}
    	}
    	return false;
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
			a.setRoleMemberId( roleMember.getRoleMemberId() );
			// look up the attribute ID
			a.setKimAttributeId( getKimAttributeId( attributeName ) );
			
	    	Map<String, String> criteria = new HashMap<String, String>();
	    	criteria.put(KimConstants.PrimaryKeyConstants.KIM_ATTRIBUTE_ID, a.getKimAttributeId());
	    	criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMember.getRoleMemberId());
			List<RoleMemberAttributeDataImpl> origRoleMemberAttributes = 
	    		(List<RoleMemberAttributeDataImpl>)getBusinessObjectService().findMatching(RoleMemberAttributeDataImpl.class, criteria);
			RoleMemberAttributeDataImpl origRoleMemberAttribute = 
	    		(origRoleMemberAttributes!=null && origRoleMemberAttributes.size()>0) ? origRoleMemberAttributes.get(0) : null;
	    	if(origRoleMemberAttribute!=null){
	    		a.setAttributeDataId(origRoleMemberAttribute.getAttributeDataId());
	    		a.setVersionNumber(origRoleMemberAttribute.getVersionNumber());
	    	} else{
				// pull the next sequence number for the data ID
				a.setAttributeDataId(getNewAttributeDataId());
	    	}
			attributes.add( a );
		}
		roleMember.setAttributes( attributes );
	}

	private void deleteNullMemberAttributeData(List<RoleMemberAttributeDataImpl> attributes) {
		List<RoleMemberAttributeDataImpl> attributesToDelete = new ArrayList<RoleMemberAttributeDataImpl>();
		for(RoleMemberAttributeDataImpl attribute: attributes){
			if(attribute.getAttributeValue()==null){
				attributesToDelete.add(attribute);
			}
		}
		getBusinessObjectService().delete(attributesToDelete);
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
    	RoleImpl role = getRoleImplByName( namespaceCode, roleName );
    	role.refreshReferenceObject("members");
    	
    	// check that identical member does not already exist
    	if ( doAnyMemberRecordsMatch( role.getMembers(), principalId, Role.PRINCIPAL_MEMBER_TYPE, qualifier ) ) {
    		return;
    	}
    	// create the new role member object
    	RoleMemberImpl newRoleMember = new RoleMemberImpl();
    	// get a new ID from the sequence
    	SequenceAccessorService sas = getSequenceAccessorService();
    	Long nextSeq = sas.getNextAvailableSequenceNumber( 
    			KimConstants.SequenceNames.KRIM_ROLE_MBR_ID_S, RoleMemberImpl.class );    	
    	newRoleMember.setRoleMemberId( nextSeq.toString() );

    	newRoleMember.setRoleId( role.getRoleId() );
    	newRoleMember.setMemberId( principalId );
    	newRoleMember.setMemberTypeCode( Role.PRINCIPAL_MEMBER_TYPE );

    	// build role member attribute objects from the given AttributeSet
    	addMemberAttributeData( newRoleMember, qualifier, role.getKimTypeId() );

    	// add row to member table
    	// When members are added to roles, clients must be notified.
    	getResponsibilityInternalService().saveRoleMember(newRoleMember);
    	getIdentityManagementNotificationService().roleUpdated();
    }

    public void assignGroupToRole(String groupId, String namespaceCode, String roleName, AttributeSet qualifier) {
    	// look up the role
    	RoleImpl role = getRoleImplByName( namespaceCode, roleName );
    	// check that identical member does not already exist
    	if ( doAnyMemberRecordsMatch( role.getMembers(), groupId, Role.GROUP_MEMBER_TYPE, qualifier ) ) {
    		return;
    	}
    	// create the new role member object
    	RoleMemberImpl newRoleMember = new RoleMemberImpl();
    	// get a new ID from the sequence
    	SequenceAccessorService sas = getSequenceAccessorService();
    	Long nextSeq = sas.getNextAvailableSequenceNumber(
    			KimConstants.SequenceNames.KRIM_ROLE_MBR_ID_S, RoleMemberImpl.class);
    	newRoleMember.setRoleMemberId( nextSeq.toString() );

    	newRoleMember.setRoleId( role.getRoleId() );
    	newRoleMember.setMemberId( groupId );
    	newRoleMember.setMemberTypeCode( Role.GROUP_MEMBER_TYPE );

    	// build role member attribute objects from the given AttributeSet
    	addMemberAttributeData( newRoleMember, qualifier, role.getKimTypeId() );

    	// When members are added to roles, clients must be notified.
    	getResponsibilityInternalService().saveRoleMember(newRoleMember);
    	getIdentityManagementNotificationService().roleUpdated();
    }

    public void removePrincipalFromRole(String principalId, String namespaceCode, String roleName, AttributeSet qualifier ) {
    	// look up the role
    	RoleImpl role = getRoleImplByName( namespaceCode, roleName );
    	// pull all the principal members
    	role.refreshReferenceObject("members");
    	// look for an exact qualifier match
		for ( RoleMemberImpl rm : role.getMembers() ) {
			if ( doesMemberMatch( rm, principalId, Role.PRINCIPAL_MEMBER_TYPE, qualifier ) ) {
		    	// if found, remove
				// When members are removed from roles, clients must be notified.
		    	getResponsibilityInternalService().removeRoleMember(rm);
			}
		}
		getIdentityManagementNotificationService().roleUpdated();
    }

    public void removeGroupFromRole(String groupId, String namespaceCode, String roleName, AttributeSet qualifier) {
    	// look up the role
    	RoleImpl role = getRoleImplByName( namespaceCode, roleName );
    	// pull all the group role members
    	// look for an exact qualifier match
		for ( RoleMemberImpl rm : role.getMembers() ) {
			if ( doesMemberMatch( rm, groupId, Role.GROUP_MEMBER_TYPE, qualifier ) ) {
		    	// if found, remove
				// When members are removed from roles, clients must be notified.
		    	getResponsibilityInternalService().removeRoleMember(rm);
			}
		}
		getIdentityManagementNotificationService().roleUpdated();
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

    public List<RoleImpl> getRolesSearchResults(java.util.Map<String,String> fieldValues) {
    	return roleDao.getRoles(fieldValues);
    }

    /**
     * @see org.kuali.rice.kim.service.RoleService#roleInactivated(java.lang.String)
     */
    public void roleInactivated(String roleId){
    	Timestamp yesterday = new Timestamp( new java.util.Date().getTime() - (24*60*60*1000) );
    	List<String> roleIds = new ArrayList<String>();
    	roleIds.add(roleId);
    	inactivateRoleMemberships(roleIds, yesterday);
    	inactivateRoleDelegations(roleIds, yesterday);
    	inactivateMembershipsForRoleAsMember(roleIds, yesterday);
    }
    
    private void inactivateRoleMemberships(List<String> roleIds, Timestamp yesterday){
    	List<RoleMemberImpl> roleMembers = roleDao.getRoleMembersForRoleIds(roleIds, null);
    	for(RoleMemberImpl rm: roleMembers){
    		rm.setActiveToDate( new Date(yesterday.getTime()) );
    	}
    	getBusinessObjectService().save(roleMembers);
    }

    private void inactivateMembershipsForRoleAsMember(List<String> roleIds, Timestamp yesterday){
    	List<RoleMemberImpl> roleMembers = roleDao.getRoleMembershipsForRoleIdsAsMembers(roleIds);
    	for(RoleMemberImpl rm: roleMembers){
    		rm.setActiveToDate( new Date(yesterday.getTime()) );
    	}
    	getBusinessObjectService().save(roleMembers);
    }

    private void inactivateRoleDelegations(List<String> roleIds, Timestamp yesterday){
    	List<KimDelegationImpl> delegations = roleDao.getDelegationImplsForRoleIds(roleIds);
    	for(KimDelegationImpl delegation: delegations){
    		delegation.setActive(false);
    		for(KimDelegationMemberImpl delegationMember: delegation.getMembers()){
    			delegationMember.setActiveToDate(new Date(yesterday.getTime()));
    		}
    	}
    	getBusinessObjectService().save(delegations);
    }
    
    /**
     * @see org.kuali.rice.kim.service.RoleService#principalInactivated(java.lang.String)
     */
    public void principalInactivated(String principalId) {
    	Timestamp yesterday = new Timestamp( System.currentTimeMillis() - (24*60*60*1000) );
    	inactivatePrincipalRoleMemberships(principalId, yesterday);
    	inactivatePrincipalGroupMemberships(principalId, yesterday);
    	inactivatePrincipalDelegations(principalId, yesterday);
    	inactivateApplicationRoleMemberships( principalId, yesterday );
    }

    @SuppressWarnings("unchecked")
	protected void inactivateApplicationRoleMemberships( String principalId, Timestamp yesterday){
    	// get all role type services
    	Collection<KimTypeInfo> types = KIMServiceLocator.getTypeInfoService().getAllTypes();
    	// create sub list of only application role types
    	ArrayList<KimTypeInfo> applicationRoleTypes = new ArrayList<KimTypeInfo>( types.size() );
    	for ( KimTypeInfo typeInfo : types ) {
    		KimRoleTypeService service = getRoleTypeService(typeInfo);
    		if ( service != null ) {
    			if ( service.isApplicationRoleType() ) {
    				applicationRoleTypes.add(typeInfo);
    			}
    		}
    	}
    	Map<String,Object> roleLookupMap = new HashMap<String, Object>(2);
    	roleLookupMap.put( KIMPropertyConstants.Role.ACTIVE, "Y");
    	// loop over application types
    	for ( KimTypeInfo typeInfo : applicationRoleTypes ) {
    		KimRoleTypeService service = getRoleTypeService(typeInfo);
    		// get all roles for that type
        	roleLookupMap.put( KIMPropertyConstants.Role.KIM_TYPE_ID, typeInfo.getKimTypeId());
    		Collection<RoleImpl> roles = getBusinessObjectService().findMatching( RoleImpl.class, roleLookupMap);
        	// loop over all roles in those types
    		for ( RoleImpl role : roles ) {
    	    	// call the principalInactivated() on the role type service for each role
    			service.principalInactivated(principalId, role.getNamespaceCode(), role.getRoleName());
    		}
    	}
    }
    
    protected void inactivatePrincipalRoleMemberships(String principalId, Timestamp yesterday){
    	// go through all roles and post-date them
    	List<RoleMemberImpl> roleMembers = roleDao.getRolePrincipalsForPrincipalIdAndRoleIds(null, principalId);
    	Set<String> roleIds = new HashSet<String>( roleMembers.size() );
    	for ( RoleMemberImpl rm : roleMembers ) {
    		rm.setActiveToDate( new Date(yesterday.getTime()) );
    		roleIds.add(rm.getRoleId()); // add to the set of IDs
    	}
    	getBusinessObjectService().save(roleMembers);
    	// find all distinct role IDs and type services
    	for ( String roleId : roleIds ) {
    		RoleImpl role = getRoleImpl(roleId);
    		KimRoleTypeService roleTypeService = getRoleTypeService(roleId);
    		try {
	    		if ( roleTypeService != null ) {
	    			roleTypeService.principalInactivated( principalId, role.getNamespaceCode(), role.getRoleName() );
	    		}
    		} catch ( Exception ex ) {
    			LOG.error( "Problem notifying role type service of principal inactivation: " + role.getKimRoleType().getKimTypeServiceName(), ex );
    		}
    	}
    }
    
    protected void inactivatePrincipalGroupMemberships(String principalId, Timestamp yesterday){
        List<GroupMembershipInfo> groupMemberInfos = roleDao.getGroupPrincipalsForPrincipalIdAndGroupIds(null, principalId);
        List<GroupMemberImpl> groupMembers = new ArrayList<GroupMemberImpl>(groupMemberInfos.size());
        for ( GroupMembershipInfo rm : groupMemberInfos ) {
            rm.setActiveToDate( new Date(yesterday.getTime()) );
            groupMembers.add(toGroupMemberImpl(rm));
        }
        
    	getBusinessObjectService().save(groupMembers);
    }

    protected void inactivatePrincipalDelegations(String principalId, Timestamp yesterday){
    	List<KimDelegationMemberImpl> delegationMembers = roleDao.getDelegationPrincipalsForPrincipalIdAndDelegationIds(null, principalId);
    	for ( KimDelegationMemberImpl rm : delegationMembers ) {
    		rm.setActiveToDate( new Date(yesterday.getTime()) );
    	}
    	getBusinessObjectService().save(delegationMembers);
    }

    public List<RoleMembershipInfo> getFirstLevelRoleMembers(List<String> roleIds){
    	List<RoleMemberImpl> rms = roleDao.getRoleMembersForRoleIds(roleIds, null );
    	List<RoleMembershipInfo> roleMembershipInfoList = new ArrayList<RoleMembershipInfo>();
    	for ( RoleMemberImpl rm : rms ) {
    		roleMembershipInfoList.add(new RoleMembershipInfo( rm.getRoleId(), rm.getRoleMemberId(), rm.getMemberId(), rm.getMemberTypeCode(), rm.getQualifier()));
    	}
    	return roleMembershipInfoList;
    }
    

    /**
     * When a group is inactivated, inactivate the memberships of principals in that group 
	 * and the memberships of that group in roles 
     * 
     * @see org.kuali.rice.kim.service.GroupUpdateService#groupInactivated(java.lang.String)
     */
    public void groupInactivated(String groupId) {
    	Timestamp yesterday = new Timestamp( System.currentTimeMillis() - (24*60*60*1000) );
    	List<String> groupIds = new ArrayList<String>();
    	groupIds.add(groupId);
    	inactivatePrincipalGroupMemberships(groupIds, yesterday);
    	inactivateGroupRoleMemberships(groupIds, yesterday);
    }
    
    protected void inactivatePrincipalGroupMemberships(List<String> groupIds, Timestamp yesterday){
        List<GroupMembershipInfo> groupMemberInfos = roleDao.getGroupMembers(groupIds);
        List<GroupMemberImpl> groupMembers = new ArrayList<GroupMemberImpl>(groupMemberInfos.size());
        for ( GroupMembershipInfo rm : groupMemberInfos ) {
            rm.setActiveToDate( new Date(yesterday.getTime()) );
            groupMembers.add(toGroupMemberImpl(rm));
        }
    	getBusinessObjectService().save(groupMembers);
    }

    protected void inactivateGroupRoleMemberships(List<String> groupIds, Timestamp yesterday){
    	List<RoleMemberImpl> roleMembersOfGroupType = roleDao.getRoleGroupsForGroupIdsAndRoleIds(null, groupIds);
    	for(RoleMemberImpl rm: roleMembersOfGroupType){
    		rm.setActiveToDate( new Date(yesterday.getTime()) );
    	}
    	getBusinessObjectService().save(roleMembersOfGroupType);
    }
    
    protected GroupMemberImpl toGroupMemberImpl(GroupMembershipInfo kimGroupMember) {
        GroupMemberImpl groupMemberImpl = null;

        if (kimGroupMember != null) {
            groupMemberImpl = new GroupMemberImpl();
            groupMemberImpl.setGroupId(kimGroupMember.getGroupId());
            groupMemberImpl.setGroupMemberId(kimGroupMember.getGroupMemberId());
            groupMemberImpl.setMemberId(kimGroupMember.getMemberId());
            groupMemberImpl.setMemberTypeCode(kimGroupMember.getMemberTypeCode());
            groupMemberImpl.setActiveFromDate(kimGroupMember.getActiveFromDate());
            groupMemberImpl.setActiveToDate(kimGroupMember.getActiveToDate());
            groupMemberImpl.setVersionNumber(kimGroupMember.getVersionNumber());
        }

        return groupMemberImpl;
    }

    public List<RoleMembershipInfo> findRoleMembers(Map<String,String> fieldValues){
    	return roleDao.getRoleMembers(fieldValues);
    }

	protected RoleMemberImpl matchingMemberRecord( List<RoleMemberImpl> roleMembers, String memberId, String memberTypeCode, AttributeSet qualifier ) {
		for ( RoleMemberImpl rm : roleMembers ) {
			if ( doesMemberMatch( rm, memberId, memberTypeCode, qualifier ) ) {
				return rm;
			}
		}
		return null;
	}
	


    public void assignRoleToRole(String roleId, String namespaceCode, String roleName, AttributeSet qualifier) {
    	// look up the role
    	RoleImpl role = getRoleImplByName( namespaceCode, roleName );
    	// check that identical member does not already exist
    	if ( doAnyMemberRecordsMatch( role.getMembers(), roleId, Role.ROLE_MEMBER_TYPE, qualifier ) ) {
    		return;
    	}
    	// create the new role member object
    	RoleMemberImpl newRoleMember = new RoleMemberImpl();
    	// get a new ID from the sequence
    	SequenceAccessorService sas = getSequenceAccessorService();
    	Long nextSeq = sas.getNextAvailableSequenceNumber(
    			KimConstants.SequenceNames.KRIM_ROLE_MBR_ID_S, RoleMemberImpl.class);
    	newRoleMember.setRoleMemberId( nextSeq.toString() );

    	newRoleMember.setRoleId( role.getRoleId() );
    	newRoleMember.setMemberId( roleId );
    	newRoleMember.setMemberTypeCode( Role.ROLE_MEMBER_TYPE );
    	// build role member attribute objects from the given AttributeSet
    	addMemberAttributeData( newRoleMember, qualifier, role.getKimTypeId() );

    	// When members are added to roles, clients must be notified.
    	getResponsibilityInternalService().saveRoleMember(newRoleMember);
    	getIdentityManagementNotificationService().roleUpdated();
    }
    
    public RoleMemberCompleteInfo saveRoleMemberForRole(String roleMemberId, String memberId, String memberTypeCode, String roleId, 
    		AttributeSet qualifications, Date activeFromDate, Date activeToDate) throws UnsupportedOperationException{
    	if(StringUtils.isEmpty(roleMemberId) && StringUtils.isEmpty(memberId) && StringUtils.isEmpty(roleId)){
    		throw new IllegalArgumentException("Either Role member ID or a combination of member ID and role ID must be passed in.");
    	}
    	RoleMemberImpl origRoleMember = null;
    	RoleImpl role;
    	// create the new role member object
    	RoleMemberImpl newRoleMember = new RoleMemberImpl();
    	if(StringUtils.isEmpty(roleMemberId)){
	    	// look up the role
	    	role = getRoleImpl(roleId);
	    	// check that identical member does not already exist
	    	origRoleMember = matchingMemberRecord( role.getMembers(), memberId, memberTypeCode, qualifications );
    	} else{
    		origRoleMember = getRoleMemberImpl(roleMemberId);
    		role = getRoleImpl(origRoleMember.getRoleId());
    	}
    	
    	if(origRoleMember !=null){
    		newRoleMember.setRoleMemberId(origRoleMember.getRoleMemberId());
    		newRoleMember.setVersionNumber(origRoleMember.getVersionNumber());
    	} else {
	    	// get a new ID from the sequence
	    	SequenceAccessorService sas = getSequenceAccessorService();
	    	Long nextSeq = sas.getNextAvailableSequenceNumber(
	    			KimConstants.SequenceNames.KRIM_ROLE_MBR_ID_S, RoleMemberImpl.class);
	    	newRoleMember.setRoleMemberId( nextSeq.toString() );
    	}
    	newRoleMember.setRoleId(role.getRoleId());
    	newRoleMember.setMemberId( memberId );
    	newRoleMember.setMemberTypeCode( memberTypeCode );
    	newRoleMember.setActiveFromDate(activeFromDate);
    	newRoleMember.setActiveToDate(activeToDate);
    	// build role member attribute objects from the given AttributeSet
    	addMemberAttributeData( newRoleMember, qualifications, role.getKimTypeId() );

    	// When members are added to roles, clients must be notified.
    	getResponsibilityInternalService().saveRoleMember(newRoleMember);
    	deleteNullMemberAttributeData(newRoleMember.getAttributes());    
    	getIdentityManagementNotificationService().roleUpdated();
    	
    	return findRoleMemberCompleteInfo(newRoleMember.getRoleMemberId());
    }



    public void removeRoleFromRole(String roleId, String namespaceCode, String roleName, AttributeSet qualifier) {
    	// look up the role
    	RoleImpl role = getRoleImplByName( namespaceCode, roleName );
    	// pull all the group role members
    	// look for an exact qualifier match
		for ( RoleMemberImpl rm : role.getMembers() ) {
			if ( doesMemberMatch( rm, roleId, Role.ROLE_MEMBER_TYPE, qualifier ) ) {
		    	// if found, remove
				// When members are removed from roles, clients must be notified.
		    	getResponsibilityInternalService().removeRoleMember(rm);
			}
		}
		getIdentityManagementNotificationService().roleUpdated();
    }
    
    public RoleMemberCompleteInfo findRoleMemberCompleteInfo(String roleMemberId){
    	Map<String, String> fieldValues = new HashMap<String, String>();
    	fieldValues.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId);
    	List<RoleMemberCompleteInfo> roleMemberInfos = findRoleMembersCompleteInfo(fieldValues);
    	if(roleMemberInfos!=null && roleMemberInfos.size()>0)
    		return roleMemberInfos.get(0);
    	return null;
    }

    public List<RoleMemberCompleteInfo> findRoleMembersCompleteInfo(Map<String, String> fieldValues){
    	List<RoleMemberCompleteInfo> roleMembersCompleteInfos = new ArrayList<RoleMemberCompleteInfo>();
    	RoleMemberCompleteInfo roleMembersCompleteInfo;
    	List<RoleMemberImpl> roleMembers = (List<RoleMemberImpl>)getLookupService().findCollectionBySearchHelper(
				RoleMemberImpl.class, fieldValues, true);
    	for(RoleMemberImpl roleMember: roleMembers){
    		roleMembersCompleteInfo = roleMember.toSimpleInfo();
			BusinessObject member = getMember(roleMembersCompleteInfo.getMemberTypeCode(), roleMembersCompleteInfo.getMemberId());
			roleMembersCompleteInfo.setMemberName(getMemberName(roleMembersCompleteInfo.getMemberTypeCode(), member));
			roleMembersCompleteInfo.setMemberNamespaceCode(getMemberNamespaceCode(roleMembersCompleteInfo.getMemberTypeCode(), member));
			roleMembersCompleteInfo.setRoleRspActions(getRoleMemberResponsibilityActionInfo(roleMember.getRoleMemberId()));
			roleMembersCompleteInfos.add(roleMembersCompleteInfo);
    	}
    	return roleMembersCompleteInfos;
    }
    
    public List<DelegateMemberCompleteInfo> findDelegateMembersCompleteInfo(final Map<String, String> fieldValues){
    	List<DelegateMemberCompleteInfo> delegateMembersCompleteInfo = new ArrayList<DelegateMemberCompleteInfo>();
    	DelegateMemberCompleteInfo delegateMemberCompleteInfo;
    	List<KimDelegationImpl> delegations = (List<KimDelegationImpl>)getLookupService().findCollectionBySearchHelper(
				KimDelegationImpl.class, fieldValues, true);
    	if(delegations!=null && !delegations.isEmpty()){
    		Map<String, String> delegationMemberFieldValues = new HashMap<String, String>();
    		for(String key: fieldValues.keySet()){
    			if(key.startsWith(KimConstants.KimUIConstants.MEMBER_ID_PREFIX)){
    				delegationMemberFieldValues.put(
    						key.substring(key.indexOf(
    						KimConstants.KimUIConstants.MEMBER_ID_PREFIX)+KimConstants.KimUIConstants.MEMBER_ID_PREFIX.length()), 
    						fieldValues.get(key));
    			}
    		}
			StringBuffer memberQueryString = new StringBuffer();
	    	for(KimDelegationImpl delegation: delegations)
	    		memberQueryString.append(delegation.getDelegationId()+KimConstants.KimUIConstants.OR_OPERATOR);
	    	delegationMemberFieldValues.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID, 
	    			KimCommonUtils.stripEnd(memberQueryString.toString(), KimConstants.KimUIConstants.OR_OPERATOR));
	    	List<KimDelegationMemberImpl> delegateMembers = (List<KimDelegationMemberImpl>)getLookupService().findCollectionBySearchHelper(
					KimDelegationMemberImpl.class, delegationMemberFieldValues, true);
	    	KimDelegationImpl delegationTemp;
	    	for(KimDelegationMemberImpl delegateMember: delegateMembers){
	    		delegateMemberCompleteInfo = delegateMember.toSimpleInfo();
	    		delegationTemp = getDelegationImpl(delegations, delegateMember.getDelegationId());
	    		delegateMemberCompleteInfo.setRoleId(delegationTemp.getRoleId());
	    		delegateMemberCompleteInfo.setDelegationTypeCode(delegationTemp.getDelegationTypeCode());
				BusinessObject member = getMember(delegateMemberCompleteInfo.getMemberTypeCode(), delegateMemberCompleteInfo.getMemberId());
				delegateMemberCompleteInfo.setMemberName(getMemberName(delegateMemberCompleteInfo.getMemberTypeCode(), member));
				delegateMemberCompleteInfo.setMemberNamespaceCode(getMemberNamespaceCode(delegateMemberCompleteInfo.getMemberTypeCode(), member));
				delegateMembersCompleteInfo.add(delegateMemberCompleteInfo);
	    	}

    	}
    	return delegateMembersCompleteInfo;
    }
    
    private KimDelegationImpl getDelegationImpl(List<KimDelegationImpl> delegations, String delegationId){
    	if(StringUtils.isEmpty(delegationId) || delegations==null)
    		return null;
    	for(KimDelegationImpl delegation: delegations){
    		if(StringUtils.equals(delegation.getDelegationId(), delegationId))
    			return delegation;
    	}
    	return null;
    }
    /**
     * 
     * This overridden method ...
     * 
     * @see org.kuali.rice.kim.service.RoleUpdateService#assignRoleAsDelegationMemberToRole(java.lang.String, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    public void saveDelegationMemberForRole(String delegationMemberId,
    			String roleMemberId, String memberId, String memberTypeCode, String delegationTypeCode, 
    			String roleId, AttributeSet qualifications, Date activeFromDate, Date activeToDate) throws UnsupportedOperationException{
    	if(StringUtils.isEmpty(delegationMemberId) && StringUtils.isEmpty(memberId) && StringUtils.isEmpty(roleId)){
    		throw new IllegalArgumentException("Either Delegation member ID or a combination of member ID and role ID must be passed in.");
    	}
    	// look up the role
    	RoleImpl role = getRoleImpl(roleId);
    	KimDelegationImpl delegation = getDelegationOfType(role.getRoleId(), delegationTypeCode);
    	// create the new role member object
    	KimDelegationMemberImpl newDelegationMember = new KimDelegationMemberImpl();

    	Map<String, String> criteria = new HashMap<String, String>();
    	KimDelegationMemberImpl origDelegationMember = null;
    	if(StringUtils.isNotEmpty(delegationMemberId)){
    		criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_MEMBER_ID, delegationMemberId);
	    	origDelegationMember = (KimDelegationMemberImpl)getBusinessObjectService().findByPrimaryKey(KimDelegationMemberImpl.class, criteria);
    	} else{
	    	criteria.put(KimConstants.PrimaryKeyConstants.MEMBER_ID, memberId);
	    	criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegation.getDelegationId());
	    	List<KimDelegationMemberImpl> origDelegationMembers = (List<KimDelegationMemberImpl>)getBusinessObjectService().findMatching(KimDelegationMemberImpl.class, criteria);
	    	origDelegationMember = 
	    		(origDelegationMembers!=null && origDelegationMembers.size()>0) ? origDelegationMembers.get(0) : null;
    	}
    	if(origDelegationMember!=null){
    		newDelegationMember.setDelegationMemberId(origDelegationMember.getDelegationMemberId());
    		newDelegationMember.setVersionNumber(origDelegationMember.getVersionNumber());
    	} else{
    		newDelegationMember.setDelegationMemberId(getNewDelegationMemberId());
    	}
    	newDelegationMember.setMemberId(memberId);
    	newDelegationMember.setDelegationId(delegation.getDelegationId());
    	newDelegationMember.setRoleMemberId(roleMemberId);
    	newDelegationMember.setMemberTypeCode(memberTypeCode);
    	newDelegationMember.setActiveFromDate(activeFromDate);
    	newDelegationMember.setActiveToDate(activeToDate);

    	// build role member attribute objects from the given AttributeSet
    	addDelegationMemberAttributeData( newDelegationMember, qualifications, role.getKimTypeId() );

    	List<KimDelegationMemberImpl> delegationMembers = new ArrayList<KimDelegationMemberImpl>();
    	delegationMembers.add(newDelegationMember);
    	delegation.setMembers(delegationMembers);

    	getBusinessObjectService().save(delegation);
    	for(KimDelegationMemberImpl delegationMember: delegation.getMembers()){
    		deleteNullDelegationMemberAttributeData(delegationMember.getAttributes());
    	}
    	getIdentityManagementNotificationService().roleUpdated();
    }

	protected void addDelegationMemberAttributeData( KimDelegationMemberImpl delegationMember, AttributeSet qualifier, String kimTypeId ) {
		List<KimDelegationMemberAttributeDataImpl> attributes = new ArrayList<KimDelegationMemberAttributeDataImpl>();
		for ( String attributeName : qualifier.keySet() ) {
			KimDelegationMemberAttributeDataImpl a = new KimDelegationMemberAttributeDataImpl();
			a.setAttributeValue( qualifier.get( attributeName ) );
			a.setKimTypeId( kimTypeId );
			a.setDelegationMemberId( delegationMember.getDelegationMemberId() );
			// look up the attribute ID
			a.setKimAttributeId( getKimAttributeId( attributeName ) );
	    	Map<String, String> criteria = new HashMap<String, String>();
	    	criteria.put(KimConstants.PrimaryKeyConstants.KIM_ATTRIBUTE_ID, a.getKimAttributeId());
	    	criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_MEMBER_ID, delegationMember.getDelegationMemberId());
			List<KimDelegationMemberAttributeDataImpl> origDelegationMemberAttributes = 
	    		(List<KimDelegationMemberAttributeDataImpl>)getBusinessObjectService().findMatching(KimDelegationMemberAttributeDataImpl.class, criteria);
			KimDelegationMemberAttributeDataImpl origDelegationMemberAttribute = 
	    		(origDelegationMemberAttributes!=null && origDelegationMemberAttributes.size()>0) ? origDelegationMemberAttributes.get(0) : null;
	    	if(origDelegationMemberAttribute!=null){
	    		a.setAttributeDataId(origDelegationMemberAttribute.getAttributeDataId());
	    		a.setVersionNumber(origDelegationMemberAttribute.getVersionNumber());
	    	} else{
				// pull the next sequence number for the data ID
				a.setAttributeDataId(getNewAttributeDataId());
	    	}
			attributes.add( a );
		}
		delegationMember.setAttributes( attributes );
	}

	private void deleteNullDelegationMemberAttributeData(List<KimDelegationMemberAttributeDataImpl> attributes) {
		List<KimDelegationMemberAttributeDataImpl> attributesToDelete = new ArrayList<KimDelegationMemberAttributeDataImpl>();
		for(KimDelegationMemberAttributeDataImpl attribute: attributes){
			if(attribute.getAttributeValue()==null){
				attributesToDelete.add(attribute);
			}
		}
		getBusinessObjectService().delete(attributesToDelete);
	}

	private List<KimDelegationImpl> getRoleDelegations(String roleId){
		if(roleId==null)
			return new ArrayList<KimDelegationImpl>();
		Map<String,String> criteria = new HashMap<String,String>(1);
		criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleId);
		return (List<KimDelegationImpl>)getBusinessObjectService().findMatching(KimDelegationImpl.class, criteria);
	}

    private KimDelegationImpl getDelegationOfType(String roleId, String delegationTypeCode){
    	List<KimDelegationImpl> roleDelegations = getRoleDelegations(roleId);
        if(isDelegationPrimary(delegationTypeCode))
            return getPrimaryDelegation(roleId, roleDelegations);
        else
            return getSecondaryDelegation(roleId, roleDelegations);
    }

    public DelegateTypeInfo getDelegateTypeInfo(String roleId, String delegationTypeCode){
    	KimDelegationImpl delegation = getDelegationOfType(roleId, delegationTypeCode);
    	if(delegation==null) return null;
    	return delegation.toSimpleInfo();
    }

    public DelegateTypeInfo getDelegateTypeInfoById(String delegationId){
		if(delegationId==null) return null;
		Map<String,String> criteria = new HashMap<String,String>(1);
		criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegationId);
		KimDelegationImpl delegation = (KimDelegationImpl)getBusinessObjectService().findByPrimaryKey(KimDelegationImpl.class, criteria);
    	if(delegation==null) return null;
        return delegation.toSimpleInfo();
    }
    
    protected KimDelegationImpl getPrimaryDelegation(String roleId, List<KimDelegationImpl> roleDelegations){
        KimDelegationImpl primaryDelegation = null;
        RoleImpl roleImpl = getRoleImpl(roleId);
        for(KimDelegationImpl delegation: roleDelegations){
            if(isDelegationPrimary(delegation.getDelegationTypeCode()))
                primaryDelegation = delegation;
        }
        if(primaryDelegation==null){
            primaryDelegation = new KimDelegationImpl();
            primaryDelegation.setRoleId(roleId);
            primaryDelegation.setDelegationId(getNewDelegationId());
            primaryDelegation.setDelegationTypeCode(KEWConstants.DELEGATION_PRIMARY);
            primaryDelegation.setKimTypeId(roleImpl.getKimTypeId());
        }
        return primaryDelegation;
    }

    protected String getNewDelegationId(){
    	SequenceAccessorService sas = getSequenceAccessorService();
    	Long nextSeq = sas.getNextAvailableSequenceNumber(
                KimConstants.SequenceNames.KRIM_DLGN_ID_S,
                KimDelegationImpl.class);
        return nextSeq.toString();
    }

    protected String getNewAttributeDataId(){
		SequenceAccessorService sas = getSequenceAccessorService();		
		Long nextSeq = sas.getNextAvailableSequenceNumber(
				KimConstants.SequenceNames.KRIM_ATTR_DATA_ID_S, 
				RoleMemberAttributeDataImpl.class );
		return nextSeq.toString();
    }
    
    protected String getNewDelegationMemberId(){
    	SequenceAccessorService sas = getSequenceAccessorService();
    	Long nextSeq = sas.getNextAvailableSequenceNumber(
                KimConstants.SequenceNames.KRIM_DLGN_MBR_ID_S,
                KimDelegationImpl.class);
        return nextSeq.toString();
    }
    
    protected boolean isDelegationPrimary(String delegationTypeCode){
        return KEWConstants.DELEGATION_PRIMARY.equals(delegationTypeCode);
    }

    protected boolean isDelegationSecondary(String delegationTypeCode){
        return KEWConstants.DELEGATION_SECONDARY.equals(delegationTypeCode);
    }

    private KimDelegationImpl getSecondaryDelegation(String roleId, List<KimDelegationImpl> roleDelegations){
        KimDelegationImpl secondaryDelegation = null;
        RoleImpl roleImpl = getRoleImpl(roleId);
        for(KimDelegationImpl delegation: roleDelegations){
            if(isDelegationSecondary(delegation.getDelegationTypeCode()))
                secondaryDelegation = delegation;
        }
        if(secondaryDelegation==null){
            secondaryDelegation = new KimDelegationImpl();
            secondaryDelegation.setRoleId(roleId);
            secondaryDelegation.setDelegationId(getNewDelegationId());
            secondaryDelegation.setDelegationTypeCode(KEWConstants.DELEGATION_SECONDARY);
            secondaryDelegation.setKimTypeId(roleImpl.getKimTypeId());
        }
        return secondaryDelegation;
    }

	public List<DelegateMemberCompleteInfo> getDelegationMembersByDelegationId(String delegationId){
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegationId);
        KimDelegationImpl delegation = (KimDelegationImpl)
        	KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimDelegationImpl.class, criteria);
        if(delegation==null) return null;
        
        return getDelegationInfoForDelegation(delegation);
	}
	
	public DelegateMemberCompleteInfo getDelegationMemberByDelegationAndMemberId(String delegationId, String delegationMemberId){
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegationId);
        KimDelegationImpl delegation = (KimDelegationImpl)
        	KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimDelegationImpl.class, criteria);

        criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_MEMBER_ID, delegationMemberId);
        List<KimDelegationMemberImpl> delegationMembers = (List<KimDelegationMemberImpl>)
        	KNSServiceLocator.getBusinessObjectService().findMatching(KimDelegationMemberImpl.class, criteria);
        if(delegationMembers==null || delegationMembers.isEmpty()) return null;
        
        return getDelegateCompleteInfo(delegation, delegationMembers.get(0));
	}
	
	public DelegateMemberCompleteInfo getDelegationMemberById(@WebParam(name="delegationMemberId") String delegationMemberId){
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_MEMBER_ID, delegationMemberId);
        KimDelegationMemberImpl delegationMember = (KimDelegationMemberImpl)
        	KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimDelegationMemberImpl.class, criteria);
        if(delegationMember==null) return null;
        
        criteria = new HashMap<String, String>();
        criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegationMember.getDelegationId());
        KimDelegationImpl delegation = (KimDelegationImpl)
    		KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimDelegationImpl.class, criteria);

        return getDelegateCompleteInfo(delegation, delegationMember);
	}

	private List<DelegateMemberCompleteInfo> getDelegationInfoForDelegation(KimDelegationImpl delegation){
		if(delegation==null || delegation.getMembers()==null) return null;
        List<DelegateMemberCompleteInfo> delegateInfoList = new ArrayList<DelegateMemberCompleteInfo>();
		for(KimDelegationMemberImpl delegationMember: delegation.getMembers()){
        	delegateInfoList.add(getDelegateCompleteInfo(delegation, delegationMember));
        }
        return delegateInfoList;
	}

	private DelegateMemberCompleteInfo getDelegateCompleteInfo(KimDelegationImpl delegation, KimDelegationMemberImpl delegationMember){
		if(delegation==null || delegationMember==null) return null;
		DelegateMemberCompleteInfo delegateMemberCompleteInfo = delegationMember.toSimpleInfo();
		delegateMemberCompleteInfo.setDelegationTypeCode(delegation.getDelegationTypeCode());
		BusinessObject member = getMember(delegationMember.getMemberTypeCode(), delegationMember.getMemberId());
		delegateMemberCompleteInfo.setMemberName(getMemberName(delegationMember.getMemberTypeCode(), member));
		delegateMemberCompleteInfo.setMemberNamespaceCode(getMemberNamespaceCode(delegationMember.getMemberTypeCode(), member));
		return delegateMemberCompleteInfo;
	}

	public List<RoleResponsibilityActionInfo> getRoleMemberResponsibilityActionInfo(String roleMemberId){
		Map<String, String> criteria = new HashMap<String, String>(1);		
		criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId);
		List<RoleResponsibilityActionImpl> responsibilityImpls = (List<RoleResponsibilityActionImpl>)
			getBusinessObjectService().findMatching(RoleResponsibilityActionImpl.class, criteria);
		List<RoleResponsibilityActionInfo> roleResponsibilityActionInfos = new ArrayList<RoleResponsibilityActionInfo>();
		RoleResponsibilityActionInfo roleResponsibilityActionInfo;
		for(RoleResponsibilityActionImpl responsibilityActionImpl: responsibilityImpls){
			roleResponsibilityActionInfo = new RoleResponsibilityActionInfo();
			KimCommonUtils.copyProperties(roleResponsibilityActionInfo, responsibilityActionImpl);
			roleResponsibilityActionInfos.add(roleResponsibilityActionInfo);
		}
		return roleResponsibilityActionInfos;
	}

	public List<RoleResponsibilityInfo> getRoleResponsibilities(String roleId){
		Map<String, String> criteria = new HashMap<String, String>(1);		
		criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleId);
		List<RoleResponsibilityImpl> responsibilityImpls = (List<RoleResponsibilityImpl>)
			getBusinessObjectService().findMatching(RoleResponsibilityImpl.class, criteria);
		List<RoleResponsibilityInfo> roleResponsibilities = new ArrayList<RoleResponsibilityInfo>();
		RoleResponsibilityInfo roleResponsibilityInfo;
		for(RoleResponsibilityImpl roleResponsibilityImpl: responsibilityImpls){
			roleResponsibilityInfo = new RoleResponsibilityInfo();
			KimCommonUtils.copyProperties(roleResponsibilityInfo, roleResponsibilityImpl);
			roleResponsibilities.add(roleResponsibilityInfo);
		}
		return roleResponsibilities;
	}

	public void saveRoleRspActions(String roleResponsibilityActionId, String roleId, String roleResponsibilityId, String roleMemberId, 
			String actionTypeCode, String actionPolicyCode, Integer priorityNumber, Boolean forceAction){
		RoleResponsibilityActionImpl newRoleRspAction = new RoleResponsibilityActionImpl();
		newRoleRspAction.setActionPolicyCode(actionPolicyCode);
		newRoleRspAction.setActionTypeCode(actionTypeCode);
		newRoleRspAction.setPriorityNumber(priorityNumber);
		newRoleRspAction.setForceAction(forceAction);
		newRoleRspAction.setRoleMemberId(roleMemberId);
		newRoleRspAction.setRoleResponsibilityId(roleResponsibilityId);
		if(StringUtils.isEmpty(roleResponsibilityActionId)){
			//If there is an existing one
			Map<String, String> criteria = new HashMap<String, String>(1);		
			criteria.put(KimConstants.PrimaryKeyConstants.ROLE_RESPONSIBILITY_ID, roleResponsibilityId);
			criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId);
			List<RoleResponsibilityActionImpl> roleResponsibilityActionImpls = (List<RoleResponsibilityActionImpl>)
				getBusinessObjectService().findMatching(RoleResponsibilityActionImpl.class, criteria);
			if(roleResponsibilityActionImpls!=null && roleResponsibilityActionImpls.size()>0){
				newRoleRspAction.setRoleResponsibilityActionId(roleResponsibilityActionImpls.get(0).getRoleResponsibilityActionId());
				newRoleRspAction.setVersionNumber(roleResponsibilityActionImpls.get(0).getVersionNumber());
			} else{
	//			 get a new ID from the sequence
		    	SequenceAccessorService sas = getSequenceAccessorService();
		    	Long nextSeq = sas.getNextAvailableSequenceNumber(
		    			KimConstants.SequenceNames.KRIM_ROLE_RSP_ACTN_ID_S, RoleResponsibilityActionImpl.class);
		    	newRoleRspAction.setRoleResponsibilityActionId(nextSeq.toString());
			}
		} else{
			Map<String, String> criteria = new HashMap<String, String>(1);		
			criteria.put(KimConstants.PrimaryKeyConstants.ROLE_RESPONSIBILITY_ACTION_ID, roleResponsibilityActionId);
			List<RoleResponsibilityActionImpl> roleResponsibilityActionImpls = (List<RoleResponsibilityActionImpl>)
				getBusinessObjectService().findMatching(RoleResponsibilityActionImpl.class, criteria);
			if(CollectionUtils.isNotEmpty(roleResponsibilityActionImpls) && roleResponsibilityActionImpls.size()>0){
				newRoleRspAction.setRoleResponsibilityActionId(roleResponsibilityActionImpls.get(0).getRoleResponsibilityActionId());
				newRoleRspAction.setVersionNumber(roleResponsibilityActionImpls.get(0).getVersionNumber());
			}
		}
		getBusinessObjectService().save(newRoleRspAction);
		getIdentityManagementNotificationService().roleUpdated();
	}
	
    private BusinessObject getMember(String memberTypeCode, String memberId){
        Class<? extends BusinessObject> roleMemberTypeClass = null;
        String roleMemberIdName = "";
        if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
            roleMemberTypeClass = KimPrincipalImpl.class;
            roleMemberIdName = KimConstants.PrimaryKeyConstants.PRINCIPAL_ID;
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
            roleMemberTypeClass = GroupImpl.class;
            roleMemberIdName = KimConstants.PrimaryKeyConstants.GROUP_ID;
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
            roleMemberTypeClass = RoleImpl.class;
            roleMemberIdName = KimConstants.PrimaryKeyConstants.ROLE_ID;
        }
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(roleMemberIdName, memberId);
        return KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(roleMemberTypeClass, criteria);
    }

    private String getMemberName(String memberTypeCode, BusinessObject member){
        String roleMemberName = "";
        if(member==null) return roleMemberName;
        if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
            roleMemberName = ((KimPrincipalImpl)member).getPrincipalName();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
            roleMemberName = ((GroupImpl)member).getGroupName();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
            roleMemberName = ((RoleImpl)member).getRoleName();
        }
        return roleMemberName;
    }

    private String getMemberNamespaceCode(String memberTypeCode, BusinessObject member){
        String roleMemberNamespaceCode = "";
        if(member==null) return roleMemberNamespaceCode;
        if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)){
            roleMemberNamespaceCode = "";
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)){
            roleMemberNamespaceCode = ((GroupImpl)member).getNamespaceCode();
        } else if(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)){
            roleMemberNamespaceCode = ((RoleImpl)member).getNamespaceCode();
        }
        return roleMemberNamespaceCode;
    }

    public void applicationRoleMembershipChanged( String roleId ) {
    	getResponsibilityInternalService().updateActionRequestsForRoleChange(roleId);
    }
    
	/**
	 * @return the lookupService
	 */
    protected LookupService getLookupService() {
		if(lookupService == null) {
			lookupService = KNSServiceLocator.getLookupService();
		}
		return lookupService;
	}

	protected ResponsibilityInternalService getResponsibilityInternalService() {
		if ( responsibilityInternalService == null ) {
			responsibilityInternalService = KIMServiceLocator.getResponsibilityInternalService();
		}
		return responsibilityInternalService;
	}
	
	protected IdentityManagementNotificationService getIdentityManagementNotificationService() {
        return (IdentityManagementNotificationService)KSBServiceLocator.getMessageHelper().getServiceAsynchronously(new QName(KimConstants.NAMESPACE_CODE, "IdentityManagementNotificationService"));
    }
}
