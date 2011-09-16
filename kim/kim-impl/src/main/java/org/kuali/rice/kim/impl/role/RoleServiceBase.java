package org.kuali.rice.kim.impl.role;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kew.api.action.DelegationType;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.framework.role.RoleTypeService;
import org.kuali.rice.kim.framework.type.KimTypeService;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateTypeBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberBo;
import org.kuali.rice.kim.impl.responsibility.ResponsibilityInternalService;
import org.kuali.rice.kim.impl.services.KIMServiceLocatorInternal;
import org.kuali.rice.kim.impl.type.KimTypeBo;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.KRADPropertyConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoleServiceBase {
    private static final Logger LOG = Logger.getLogger( RoleServiceBase.class );

    private BusinessObjectService businessObjectService;
    private LookupService lookupService;
    private SequenceAccessorService sequenceAccessorService;
    private IdentityService identityService;
    private GroupService groupService;
    private ResponsibilityInternalService responsibilityInternalService;
    private RoleDao roleDao;

    /**
     * A helper enumeration for indicating which KimRoleDao method to use when attempting to get role/delegation-related lists that are not in the cache.
     *
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    protected static enum RoleDaoAction {
        ROLE_PRINCIPALS_FOR_PRINCIPAL_ID_AND_ROLE_IDS,
        ROLE_GROUPS_FOR_GROUP_IDS_AND_ROLE_IDS,
        ROLE_MEMBERS_FOR_ROLE_IDS,
        ROLE_MEMBERSHIPS_FOR_ROLE_IDS_AS_MEMBERS,
        ROLE_MEMBERS_FOR_ROLE_IDS_WITH_FILTERS,
        DELEGATION_PRINCIPALS_FOR_PRINCIPAL_ID_AND_DELEGATION_IDS,
        DELEGATION_GROUPS_FOR_GROUP_IDS_AND_DELEGATION_IDS,
        DELEGATION_MEMBERS_FOR_DELEGATION_IDS
    }

    /**
     * Converts the Qualifier Name/Value Role qualification set into Qualifier AttributeID/Value set
     *
     * @param qualification The original role qualification attribute set
     * @return Converted Map<String, String> containing ID/value pairs
     */
    private Map<String, String> convertQualifierKeys(Map<String, String> qualification) {
        Map<String, String> convertedQualification = new HashMap<String, String>();
        if (qualification != null && CollectionUtils.isNotEmpty(qualification.keySet())) {
            for (Map.Entry<String, String> entry : qualification.entrySet()) {
                if (StringUtils.isNotEmpty(getKimAttributeId(entry.getKey()))) {
                    convertedQualification.put(getKimAttributeId(entry.getKey()), entry.getValue());
                }
            }
        }
        return convertedQualification;
    }

    public Set<String> getRoleTypeRoleMemberIds(String roleId) {
        Set<String> results = new HashSet<String>();
        getNestedRoleTypeMemberIds(roleId, results);
        return results;
    }

    protected void getNestedRoleTypeMemberIds(String roleId, Set<String> members) {
        ArrayList<String> roleList = new ArrayList<String>(1);
        roleList.add(roleId);
        List<RoleMemberBo> firstLevelMembers = getStoredRoleMembersForRoleIds(roleList, KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE, Collections.<String, String>emptyMap());
        for (RoleMemberBo member : firstLevelMembers) {
            if (KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(member.getMemberTypeCode())) {
                if (!members.contains(member.getMemberId())) {
                    members.add(member.getMemberId());
                    getNestedRoleTypeMemberIds(member.getMemberId(), members);
                }
            }
        }
    }

    public List<String> getMemberParentRoleIds(String memberType, String memberId) {
        List<RoleMemberBo> parentRoleMembers = roleDao.getRoleMembershipsForMemberId(memberType, memberId, Collections.<String, String>emptyMap());

        List<String> parentRoleIds = new ArrayList<String>(parentRoleMembers.size());
        for (RoleMemberBo parentRoleMember : parentRoleMembers) {
            parentRoleIds.add(parentRoleMember.getRoleId());
        }

        return parentRoleIds;
    }

    /**
     * Retrieves a list of RoleMemberBo instances from the KimRoleDao.
     *
     * @param daoActionToTake An indicator for which KimRoleDao method should be used to get the results if the desired RoleMemberBos are not cached.
     * @param roleIds         The role IDs to filter by; may get used as the IDs for members that are also roles, depending on the daoActionToTake value.
     * @param principalId     The principal ID to filter by; may get ignored depending on the daoActionToTake value.
     * @param groupIds        The group IDs to filter by; may get ignored depending on the daoActionToTake value.
     * @param memberTypeCode  The member type code to filter by; may get overridden depending on the daoActionToTake value.
     * @param qualification   The original role qualification attribute set
     * @return A list of RoleMemberBo instances based on the provided parameters.
     * @throws IllegalArgumentException if daoActionToTake refers to an enumeration constant that is not role-member-related.
     */
    protected List<RoleMemberBo> getRoleMemberBoList(RoleDaoAction daoActionToTake, Collection<String> roleIds, String principalId,
                                                     Collection<String> groupIds, String memberTypeCode, Map<String, String> qualification) {
        Map<String, String> convertedQualification = convertQualifierKeys(qualification);

        if (roleIds == null || roleIds.isEmpty()) {
            roleIds = Collections.emptyList();
        }
        if (groupIds == null || groupIds.isEmpty()) {
            groupIds = Collections.emptyList();
        }

        switch (daoActionToTake) {
            case ROLE_PRINCIPALS_FOR_PRINCIPAL_ID_AND_ROLE_IDS: // Search for principal role members only.
                return roleDao.getRolePrincipalsForPrincipalIdAndRoleIds(roleIds, principalId, convertedQualification);
            case ROLE_GROUPS_FOR_GROUP_IDS_AND_ROLE_IDS: // Search for group role members only.
               return roleDao.getRoleGroupsForGroupIdsAndRoleIds(roleIds, groupIds, convertedQualification);
            case ROLE_MEMBERS_FOR_ROLE_IDS: // Search for role members with the given member type code.
               return roleDao.getRoleMembersForRoleIds(roleIds, memberTypeCode, convertedQualification);
            case ROLE_MEMBERSHIPS_FOR_ROLE_IDS_AS_MEMBERS: // Search for role members who are also roles.
                return roleDao.getRoleMembershipsForRoleIdsAsMembers(roleIds, convertedQualification);
            case ROLE_MEMBERS_FOR_ROLE_IDS_WITH_FILTERS: // Search for role members that might be roles, principals, or groups.
                return roleDao.getRoleMembersForRoleIdsWithFilters(roleIds, principalId, groupIds, convertedQualification);
            default: // This should never happen, since the previous switch block should handle this case appropriately.
                throw new IllegalArgumentException("The 'daoActionToTake' parameter cannot refer to a non-role-member-related value!");
        }
    }

    /**
     * Calls the KimRoleDao's "getRolePrincipalsForPrincipalIdAndRoleIds" method and/or retrieves any corresponding members from the cache.
     */
    protected List<RoleMemberBo> getStoredRolePrincipalsForPrincipalIdAndRoleIds(Collection<String> roleIds, String principalId, Map<String, String> qualification) {
        return getRoleMemberBoList(RoleDaoAction.ROLE_PRINCIPALS_FOR_PRINCIPAL_ID_AND_ROLE_IDS, roleIds, principalId, Collections.<String>emptyList(), null, qualification);
    }

    /**
     * Calls the KimRoleDao's "getRoleGroupsForGroupIdsAndRoleIds" method and/or retrieves any corresponding members from the cache.
     */
    protected List<RoleMemberBo> getStoredRoleGroupsForGroupIdsAndRoleIds(Collection<String> roleIds, Collection<String> groupIds, Map<String, String> qualification) {
        return getRoleMemberBoList(RoleDaoAction.ROLE_GROUPS_FOR_GROUP_IDS_AND_ROLE_IDS, roleIds, null, groupIds, null, qualification);
    }

    /**
     * Calls the KimRoleDao's "getRoleMembersForRoleIds" method and/or retrieves any corresponding members from the cache.
     */
    protected List<RoleMemberBo> getStoredRoleMembersForRoleIds(Collection<String> roleIds, String memberTypeCode, Map<String, String> qualification) {
        return getRoleMemberBoList(RoleDaoAction.ROLE_MEMBERS_FOR_ROLE_IDS, roleIds, null, Collections.<String>emptyList(), memberTypeCode, qualification);
    }

    /**
     * Calls the KimRoleDao's "getRoleMembershipsForRoleIdsAsMembers" method and/or retrieves any corresponding members from the cache.
     */
    protected List<RoleMemberBo> getStoredRoleMembershipsForRoleIdsAsMembers(Collection<String> roleIds, Map<String, String> qualification) {
        return getRoleMemberBoList(RoleDaoAction.ROLE_MEMBERSHIPS_FOR_ROLE_IDS_AS_MEMBERS, roleIds, null, Collections.<String>emptyList(), null, qualification);
    }

    /**
     * Calls the KimRoleDao's "getRoleMembersForRoleIdsWithFilters" method and/or retrieves any corresponding members from the cache.
     */
    protected List<RoleMemberBo> getStoredRoleMembersForRoleIdsWithFilters(Collection<String> roleIds, String principalId, List<String> groupIds, Map<String, String> qualification) {
        return getRoleMemberBoList(RoleDaoAction.ROLE_MEMBERS_FOR_ROLE_IDS_WITH_FILTERS, roleIds, principalId, groupIds, null, qualification);
    }

    /**
     * Determines whether or not the given role should allow its members to be cached. The default implementation always returns true, but
     * subclasses can override this method if other non-default Role implementations forbid their members from being cached.
     *
     * @param roleId The ID of the role to check for determining whether or not to allow caching of its members.
     * @return True if the given role allows its members to be cached; false otherwise.
     */
    protected boolean shouldCacheMembersOfRole(String roleId) {
        return true;
    }

    /**
     * Retrieves a RoleMemberBo object by its ID. If the role member already exists in the cache, this method will return the cached
     * version; otherwise, it will retrieve the uncached version from the database and then cache it (if it belongs to a role that allows
     * its members to be cached) before returning it.
     */
    protected RoleMemberBo getRoleMemberBo(String roleMemberId) {
        if (StringUtils.isBlank(roleMemberId)) {
            return null;
        }

        return getBusinessObjectService().findByPrimaryKey(RoleMemberBo.class,
                Collections.singletonMap(KIMPropertyConstants.RoleMember.ROLE_MEMBER_ID, roleMemberId));
    }

    /**
     * Calls the KimRoleDao's "getDelegationImplMapFromRoleIds" method and/or retrieves any corresponding delegations from the cache.
     */
    protected Map<String, DelegateTypeBo> getStoredDelegationImplMapFromRoleIds(Collection<String> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            return roleDao.getDelegationImplMapFromRoleIds(roleIds);
        }

        return Collections.emptyMap();
    }

    /**
     * Calls the KimRoleDao's "getDelegationBosForRoleIds" method and/or retrieves any corresponding delegations from the cache.
     */
    protected List<DelegateTypeBo> getStoredDelegationImplsForRoleIds(Collection<String> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            return roleDao.getDelegationBosForRoleIds(roleIds);
        }
        return Collections.emptyList();
    }

    /**
     * Retrieves a List of delegation members from the KimRoleDao as appropriate.
     *
     * @param daoActionToTake An indicator for which KimRoleDao method to use for retrieving results.
     * @param delegationIds   The IDs of the delegations that the members belong to.
     * @param principalId     The principal ID of the principal delegation members; may get ignored depending on the RoleDaoAction value.
     * @param groupIds        The group IDs of the group delegation members; may get ignored depending on the RoleDaoAction value.
     * @return A List of DelegateMemberBo objects based on the provided parameters.
     * @throws IllegalArgumentException if daoActionToTake does not represent a delegation-member-list-related enumeration value.
     */
    protected List<DelegateMemberBo> getDelegationMemberBoList(RoleDaoAction daoActionToTake, Collection<String> delegationIds,
                                                               String principalId, List<String> groupIds) {
        if (delegationIds == null || delegationIds.isEmpty()) {
            delegationIds = Collections.emptyList();
        }
        if (groupIds == null || groupIds.isEmpty()) {
            groupIds = Collections.emptyList();
        }

        switch (daoActionToTake) {
            case DELEGATION_PRINCIPALS_FOR_PRINCIPAL_ID_AND_DELEGATION_IDS: // Search for principal delegation members.
                return roleDao.getDelegationPrincipalsForPrincipalIdAndDelegationIds(delegationIds, principalId);
            case DELEGATION_GROUPS_FOR_GROUP_IDS_AND_DELEGATION_IDS: // Search for group delegation members.
                return roleDao.getDelegationGroupsForGroupIdsAndDelegationIds(delegationIds, groupIds);
            default: // This should never happen since the previous switch block should handle this case appropriately.
                throw new IllegalArgumentException("The 'daoActionToTake' parameter cannot refer to a non-delegation-member-list-related value!");
        }
    }

    /**
     * Calls the KimRoleDao's "getDelegationPrincipalsForPrincipalIdAndDelegationIds" method and/or retrieves any corresponding members from the cache.
     */
    protected List<DelegateMemberBo> getStoredDelegationPrincipalsForPrincipalIdAndDelegationIds(Collection<String> delegationIds, String principalId) {
        return getDelegationMemberBoList(RoleDaoAction.DELEGATION_PRINCIPALS_FOR_PRINCIPAL_ID_AND_DELEGATION_IDS, delegationIds, principalId, null);
    }

    /**
     * Calls the KimRoleDao's "getDelegationGroupsForGroupIdAndDelegationIds" method and/or retrieves any corresponding members from the cache.
     */
    protected List<DelegateMemberBo> getStoredDelegationGroupsForGroupIdsAndDelegationIds(Collection<String> delegationIds, List<String> groupIds) {
        return getDelegationMemberBoList(RoleDaoAction.DELEGATION_GROUPS_FOR_GROUP_IDS_AND_DELEGATION_IDS, delegationIds, null, groupIds);
    }

    /**
     * Calls the KimRoleDao's "getDelegationMembersForDelegationIds" method and/or retrieves any corresponding members from the cache.
     */
    protected Map<String, List<DelegateMemberBo>> getStoredDelegationMembersForDelegationIds(List<String> delegationIds) {
        if (delegationIds != null && !delegationIds.isEmpty()) {
            return roleDao.getDelegationMembersForDelegationIds(delegationIds);
        }

        return Collections.emptyMap();
    }

    /**
     * Retrieves a DelegateMemberBo object by its ID. If the delegation member already exists in the cache, this method will return the cached
     * version; otherwise, it will retrieve the uncached version from the database and then cache it before returning it.
     */
    protected DelegateMemberBo getDelegateMemberBo(String delegationMemberId) {
        if (StringUtils.isBlank(delegationMemberId)) {
            return null;
        }

        return getBusinessObjectService().findByPrimaryKey(DelegateMemberBo.class,
                Collections.singletonMap(KimConstants.PrimaryKeyConstants.DELEGATION_MEMBER_ID, delegationMemberId));
    }

    /**
     * Retrieves a DelegateMemberBo List by (principal/group/role) member ID and delegation ID. If the List already exists in the cache,
     * this method will return the cached one; otherwise, it will retrieve the uncached version from the database and then cache it before returning it.
     */
    protected List<DelegateMemberBo> getDelegationMemberBoListByMemberAndDelegationId(String memberId, String delegationId) {

        Map<String, String> searchCriteria = new HashMap<String, String>();
        searchCriteria.put(KimConstants.PrimaryKeyConstants.MEMBER_ID, memberId);
        searchCriteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegationId);
        return new ArrayList<DelegateMemberBo>(getBusinessObjectService().findMatching(DelegateMemberBo.class, searchCriteria));
    }

    public RoleMember findRoleMember(String roleMemberId) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId);
        List<RoleMember> roleMembers = findRoleMembers(fieldValues);
        if (roleMembers != null && !roleMembers.isEmpty()) {
            return roleMembers.get(0);
        }
        return null;
    }

    public List<RoleMember> findRoleMembers(Map<String, String> fieldValues) {
        List<RoleMember> roleMembers = new ArrayList<RoleMember>();
        List<RoleMemberBo> roleMemberBos = (List<RoleMemberBo>) getLookupService().findCollectionBySearchHelper(
                RoleMemberBo.class, fieldValues, true);

        for (RoleMemberBo bo : roleMemberBos) {
            RoleMember roleMember = RoleMemberBo.to(bo);
            roleMembers.add(roleMember);
        }
        return roleMembers;
    }

    public List<RoleResponsibilityAction> getRoleMemberResponsibilityActions(String roleMemberId) {
        Map<String, String> criteria = new HashMap<String, String>(1);
        criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId);

        List<RoleResponsibilityActionBo> responsibilityActionBoList = (List<RoleResponsibilityActionBo>)
                getBusinessObjectService().findMatching(RoleResponsibilityActionBo.class, criteria);

        List<RoleResponsibilityAction> roleResponsibilityActionsList = new ArrayList<RoleResponsibilityAction>();
        for (RoleResponsibilityActionBo roleResponsibilityActionBo : responsibilityActionBoList) {
            RoleResponsibilityAction roleResponsibility = RoleResponsibilityActionBo.to(roleResponsibilityActionBo);
            roleResponsibilityActionsList.add(roleResponsibility);
        }
        return roleResponsibilityActionsList;
    }

    public List<DelegateMember> findDelegateMembers(final Map<String, String> fieldValues) {
        List<DelegateMember> delegateMembers = new ArrayList<DelegateMember>();
        List<DelegateTypeBo> delegateBoList = (List<DelegateTypeBo>) getLookupService().findCollectionBySearchHelper(
                DelegateTypeBo.class, fieldValues, true);

        if (delegateBoList != null && !delegateBoList.isEmpty()) {
            Map<String, String> delegationMemberFieldValues = new HashMap<String, String>();
            for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                if (entry.getKey().startsWith(KimConstants.KimUIConstants.MEMBER_ID_PREFIX)) {
                    delegationMemberFieldValues.put(
                            entry.getKey().substring(entry.getKey().indexOf(
                                    KimConstants.KimUIConstants.MEMBER_ID_PREFIX) + KimConstants.KimUIConstants.MEMBER_ID_PREFIX.length()),
                            entry.getValue());
                }
            }

            StringBuilder memberQueryString = new StringBuilder();
            for (DelegateTypeBo delegate : delegateBoList) {
                memberQueryString.append(delegate.getDelegationId()).append(KimConstants.KimUIConstants.OR_OPERATOR);
            }
            delegationMemberFieldValues.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID,
                    StringUtils.stripEnd(memberQueryString.toString(), KimConstants.KimUIConstants.OR_OPERATOR));
            List<DelegateMemberBo> delegateMemberBoList = (List<DelegateMemberBo>) getLookupService().findCollectionBySearchHelper(
                    DelegateMemberBo.class, delegationMemberFieldValues, true);


            for (DelegateMemberBo delegateMemberBo : delegateMemberBoList) {
                DelegateMember delegateMember = DelegateMemberBo.to(delegateMemberBo);
                delegateMembers.add(delegateMember);
            }
        }
        return delegateMembers;
    }

    protected Object getMember(String memberTypeCode, String memberId) {
        if (StringUtils.isBlank(memberId)) {
            return null;
        }
        if (KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)) {
            return getIdentityService().getPrincipal(memberId);
        } else if (KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)) {
            return getGroupService().getGroup(memberId);
        } else if (KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)) {
            return getRoleBo(memberId);
        }
        return null;
    }

    protected String getMemberName(Object member) {
        if (member == null) {
            return "";
        }
        if (member instanceof Principal) {
            return ((Principal) member).getPrincipalName();
        }
        if (member instanceof Group) {
            return ((Group) member).getName();
        }
        if (member instanceof Role) {
            return ((Role) member).getName();
        }
        return member.toString();
    }

    protected RoleBo getRoleBo(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            return null;
        }
        return getBusinessObjectService().findBySinglePrimaryKey(RoleBo.class, roleId);
    }

    protected DelegateTypeBo getDelegationOfType(String roleId, String delegationTypeCode) {
        List<DelegateTypeBo> roleDelegates = getRoleDelegations(roleId);
        if (isDelegationPrimary(delegationTypeCode)) {
            return getPrimaryDelegation(roleId, roleDelegates);
        } else {
            return getSecondaryDelegation(roleId, roleDelegates);
        }
    }

    private DelegateTypeBo getSecondaryDelegation(String roleId, List<DelegateTypeBo> roleDelegates) {
        DelegateTypeBo secondaryDelegate = null;
        RoleBo roleBo = getRoleBo(roleId);
        for (DelegateTypeBo delegate : roleDelegates) {
            if (isDelegationSecondary(delegate.getDelegationTypeCode())) {
                secondaryDelegate = delegate;
            }
        }
        if (secondaryDelegate == null) {
            secondaryDelegate = new DelegateTypeBo();
            secondaryDelegate.setRoleId(roleId);
            secondaryDelegate.setDelegationId(getNewDelegationId());
            secondaryDelegate.setDelegationTypeCode(DelegationType.PRIMARY.getCode());
            secondaryDelegate.setKimTypeId(roleBo.getKimTypeId());
        }
        return secondaryDelegate;
    }

    protected DelegateTypeBo getPrimaryDelegation(String roleId, List<DelegateTypeBo> roleDelegates) {
        DelegateTypeBo primaryDelegate = null;
        RoleBo roleBo = getRoleBo(roleId);
        for (DelegateTypeBo delegate : roleDelegates) {
            if (isDelegationPrimary(delegate.getDelegationTypeCode())) {
                primaryDelegate = delegate;
            }
        }
        if (primaryDelegate == null) {
            primaryDelegate = new DelegateTypeBo();
            primaryDelegate.setRoleId(roleId);
            primaryDelegate.setDelegationId(getNewDelegationId());
            primaryDelegate.setDelegationTypeCode(DelegationType.PRIMARY.getCode());
            primaryDelegate.setKimTypeId(roleBo.getKimTypeId());
        }
        return primaryDelegate;
    }

    protected RoleMemberBo matchingMemberRecord(List<RoleMemberBo> roleMembers, String memberId, String memberTypeCode, Map<String, String> qualifier) {
        for (RoleMemberBo rm : roleMembers) {
            if (doesMemberMatch(rm, memberId, memberTypeCode, qualifier)) {
                return rm;
            }
        }
        return null;
    }

    protected boolean isDelegationPrimary(String delegationTypeCode) {
        return DelegationType.PRIMARY.getCode().equals(delegationTypeCode);
    }

    protected boolean isDelegationSecondary(String delegationTypeCode) {
        return DelegationType.PRIMARY.getCode().equals(delegationTypeCode);
    }


    private List<DelegateTypeBo> getRoleDelegations(String roleId) {
        if (roleId == null) {
            return new ArrayList<DelegateTypeBo>();
        }
        return getStoredDelegationImplsForRoleIds(Collections.singletonList(roleId));

    }

    protected RoleBo getRoleBoByName(String namespaceCode, String roleName) {
        if (StringUtils.isBlank(namespaceCode)
                || StringUtils.isBlank(roleName)) {
            return null;
        }
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode);
        criteria.put(KimConstants.UniqueKeyConstants.NAME, roleName);
        criteria.put(KRADPropertyConstants.ACTIVE, "Y");
        // while this is not actually the primary key - there will be at most one row with these criteria
        return getBusinessObjectService().findByPrimaryKey(RoleBo.class, criteria);
    }

	protected boolean doAnyMemberRecordsMatchByExactQualifier( RoleBo role, String memberId, RoleDaoAction daoActionToTake, Map<String, String> qualifier ) {
		if(CollectionUtils.isNotEmpty(getRoleMembersByExactQualifierMatch(role, memberId, daoActionToTake, qualifier))) {
			return true;
		}

		return false;
	}
	
	protected List<RoleMemberBo> getRoleMembersByExactQualifierMatch(RoleBo role, String memberId, RoleDaoAction daoActionToTake, Map<String, String> qualifier) {
		List<RoleMemberBo> rms = new ArrayList<RoleMemberBo>();
		RoleTypeService roleTypeService = getRoleTypeService( role.getId() );
		if(roleTypeService != null) {
    		List<String> attributesForExactMatch = roleTypeService.getQualifiersForExactMatch();
    		if(CollectionUtils.isNotEmpty(attributesForExactMatch)) {
    			switch (daoActionToTake) {
	    			case ROLE_GROUPS_FOR_GROUP_IDS_AND_ROLE_IDS : // Search for group role members only.
	        			rms = getStoredRoleGroupsForGroupIdsAndRoleIds(Collections.singletonList(role.getId()), Collections.singletonList(memberId), populateQualifiersForExactMatch(qualifier, attributesForExactMatch));
	    				break;
	    			case ROLE_PRINCIPALS_FOR_PRINCIPAL_ID_AND_ROLE_IDS : // Search for principal role members only.
	        			rms = getStoredRolePrincipalsForPrincipalIdAndRoleIds(Collections.singletonList(role.getId()), memberId, populateQualifiersForExactMatch(qualifier, attributesForExactMatch));
	    				break;
	    			case ROLE_MEMBERSHIPS_FOR_ROLE_IDS_AS_MEMBERS : // Search for roles as role members only.
	    				List<RoleMemberBo> allRoleMembers = getStoredRoleMembershipsForRoleIdsAsMembers(Collections.singletonList(role.getId()), populateQualifiersForExactMatch(qualifier, attributesForExactMatch));
	        			for(RoleMemberBo rm : allRoleMembers) {
	        				if ( rm.getMemberId().equals(memberId) ) { 
	        					rms.add(rm);
	        				}
	        			}
	        			break;	    				
	    			default : // The daoActionToTake parameter is invalid; throw an exception.
	    				throw new IllegalArgumentException("The 'daoActionToTake' parameter cannot refer to a non-role-member-related value!");
    			}
    			
    		} 
		}
		return rms;
	}
    
    protected boolean doAnyMemberRecordsMatch(List<RoleMemberBo> roleMembers, String memberId, String memberTypeCode, Map<String, String> qualifier) {
        for (RoleMemberBo rm : roleMembers) {
            if (doesMemberMatch(rm, memberId, memberTypeCode, qualifier)) {
                return true;
            }
        }
        return false;
    }

    protected boolean doesMemberMatch(RoleMemberBo roleMember, String memberId, String memberTypeCode, Map<String, String> qualifier) {
        if (roleMember.getMemberId().equals(memberId) && roleMember.getMemberTypeCode().equals(memberTypeCode)) {
            // member ID/type match
            Map<String, String> roleQualifier = roleMember.getAttributes();
            if ((qualifier == null || qualifier.isEmpty())
                    && (roleQualifier == null || roleQualifier.isEmpty())) {
                return true; // blank qualifier match
            } else {
                if (qualifier != null && roleQualifier != null && qualifier.equals(roleQualifier)) {
                    return true; // qualifier match
                }
            }
        }
        return false;
    }
    
    /**
     * Retrieves the role type service associated with the given role ID
     *
     * @param roleId the role ID to get the role type service for
     * @return the Role Type Service
     */
    protected RoleTypeService getRoleTypeService(String roleId) {
        RoleBo roleBo = getRoleBo(roleId);
        KimType roleType = KimTypeBo.to(roleBo.getKimRoleType());
        if (roleType != null) {
            return getRoleTypeService(roleType);
        }
        return null;
    }

    protected RoleTypeService getRoleTypeService(KimType typeInfo) {
        String serviceName = typeInfo.getServiceName();
        if (serviceName != null) {
            try {
                KimTypeService service = (KimTypeService) KIMServiceLocatorInternal.getService(serviceName);
                if (service != null && service instanceof RoleTypeService) {
                    return (RoleTypeService) service;
                }
                return (RoleTypeService) KIMServiceLocatorInternal.getService("kimNoMembersRoleTypeService");
            } catch (Exception ex) {
                LOG.error("Unable to find role type service with name: " + serviceName, ex);
                return (RoleTypeService) KIMServiceLocatorInternal.getService("kimNoMembersRoleTypeService");
            }
        }
        return null;
    }
    
    protected Map<String, String> populateQualifiersForExactMatch(Map<String, String> defaultQualification, List<String> attributes) {
        Map<String,String> qualifiersForExactMatch = new HashMap<String,String>();
        if (defaultQualification != null && CollectionUtils.isNotEmpty(defaultQualification.keySet())) {
            for (String attributeName : attributes) {
                if (StringUtils.isNotEmpty(defaultQualification.get(attributeName))) {
                    qualifiersForExactMatch.put(attributeName, defaultQualification.get(attributeName));
                }
            }
        }
        return qualifiersForExactMatch;
    }

    /**
     * This method tests to see if assigning a roleBo to another roleBo will create a circular reference.
     * The Role is checked to see if it is a member (direct or nested) of the roleBo to be assigned as a member.
     *
     * @param newMemberId
     * @param roleBo
     * @return true  - assignment is allowed, no circular reference will be created.
     *         false - illegal assignment, it will create a circular membership
     */
    protected boolean checkForCircularRoleMembership(String newMemberId, RoleBo roleBo) {
        // get all nested roleBo members that are of type roleBo
        Set<String> newRoleMemberIds = getRoleTypeRoleMemberIds(newMemberId);
        return !newRoleMemberIds.contains(roleBo.getId());
    }

    // TODO: pulling attribute IDs repeatedly is inefficient - consider caching the entire list as a map
    protected String getKimAttributeId(String attributeName) {

        Map<String, Object> critieria = new HashMap<String, Object>(1);
        critieria.put("attributeName", attributeName);
        Collection<KimAttributeBo> defs = getBusinessObjectService().findMatching(KimAttributeBo.class, critieria);
        String result = null;
        if (CollectionUtils.isNotEmpty(defs)) {
            result = defs.iterator().next().getId();
        }
        return result;
    }

    protected String getNewDelegationId() {
        SequenceAccessorService sas = getSequenceAccessorService();
        Long nextSeq = sas.getNextAvailableSequenceNumber(
                KimConstants.SequenceNames.KRIM_DLGN_ID_S,
                DelegateTypeBo.class);
        return nextSeq.toString();
    }

    protected String getNewAttributeDataId() {
        SequenceAccessorService sas = getSequenceAccessorService();
        Long nextSeq = sas.getNextAvailableSequenceNumber(
                KimConstants.SequenceNames.KRIM_ATTR_DATA_ID_S,
                RoleMemberAttributeDataBo.class);
        return nextSeq.toString();
    }

    protected String getNewDelegationMemberId() {
        SequenceAccessorService sas = getSequenceAccessorService();
        Long nextSeq = sas.getNextAvailableSequenceNumber(
                KimConstants.SequenceNames.KRIM_DLGN_MBR_ID_S,
                DelegateTypeBo.class);
        return nextSeq.toString();
    }

    protected BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = KRADServiceLocator.getBusinessObjectService();
        }
        return businessObjectService;
    }

    /**
     * @return the lookupService
     */
    protected LookupService getLookupService() {
        if (lookupService == null) {
            lookupService = KRADServiceLocatorWeb.getLookupService();
        }
        return lookupService;
    }

    protected IdentityService getIdentityService() {
        if (identityService == null) {
            identityService = KimApiServiceLocator.getIdentityService();
        }

        return identityService;
    }
    
    protected GroupService getGroupService() {
        if (groupService == null) {
            groupService = KimApiServiceLocator.getGroupService();
        }

        return groupService;
    }

    protected SequenceAccessorService getSequenceAccessorService() {
        if (sequenceAccessorService == null) {
            sequenceAccessorService = KRADServiceLocator.getSequenceAccessorService();
        }
        return sequenceAccessorService;
    }

    protected ResponsibilityInternalService getResponsibilityInternalService() {
        if (responsibilityInternalService == null) {
            responsibilityInternalService = KIMServiceLocatorInternal.getResponsibilityInternalService();
        }
        return responsibilityInternalService;
    }

    /**
     * @return the roleDao
     */
    public RoleDao getRoleDao() {
        return this.roleDao;
    }

    /**
     * @param roleDao the roleDao to set
     */
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

}
