/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kim.impl.role;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.NonUniqueResultException;
import javax.xml.namespace.QName;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.framework.role.RoleEbo;
import org.kuali.rice.kim.framework.role.RoleTypeService;
import org.kuali.rice.kim.framework.type.KimTypeService;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateTypeBo;
import org.kuali.rice.kim.impl.responsibility.ResponsibilityInternalService;
import org.kuali.rice.kim.impl.services.KimImplServiceLocator;
import org.kuali.rice.kim.impl.type.KimTypeBo;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.util.KRADPropertyConstants;

abstract class RoleServiceBase {
    private static final Logger LOG = Logger.getLogger( RoleServiceBase.class );

    protected DataObjectService dataObjectService;
    protected IdentityService identityService;
    protected GroupService groupService;
    protected ResponsibilityInternalService responsibilityInternalService;
    protected RoleDao roleDao;

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
    protected Map<String, String> convertQualifierKeys(Map<String, String> qualification) {
        Map<String, String> convertedQualification = new HashMap<String, String>();
        if (qualification != null && CollectionUtils.isNotEmpty(qualification.entrySet())) {
            for (Map.Entry<String, String> entry : qualification.entrySet()) {
                String kimAttributeId = getKimAttributeId(entry.getKey());
                if (StringUtils.isNotEmpty(kimAttributeId)) {
                    convertedQualification.put(kimAttributeId, entry.getValue());
                }
            }
        }
        return convertedQualification;
    }

    protected void getNestedRoleTypeMemberIds(String roleId, Set<String> members) {
        ArrayList<String> roleList = new ArrayList<String>(1);
        roleList.add(roleId);
        List<RoleMemberBo> firstLevelMembers = getStoredRoleMembersForRoleIds(roleList, MemberType.ROLE.getCode(), Collections.<String, String>emptyMap());
        for (RoleMemberBo member : firstLevelMembers) {
            if (MemberType.ROLE.equals(member.getType())) {
                if (!members.contains(member.getMemberId())) {
                    members.add(member.getMemberId());
                    getNestedRoleTypeMemberIds(member.getMemberId(), members);
                }
            }
        }
    }

    protected List<RoleMemberBo> getRoleMembersForPrincipalId(Collection<String> roleIds, String principalId) {
        return getRoleMembersForPrincipalId(roleIds, principalId, new HashMap<String, String>(0) );
    }

    protected List<RoleMemberBo> getRoleMembersForPrincipalId(Collection<String> roleIds, String principalId, Map<String,String> qualification ) {
        List<Predicate> criteria = new ArrayList<Predicate>();

        if (CollectionUtils.isNotEmpty(roleIds)) {
            if (roleIds.size() == 1) {
                criteria.add( PredicateFactory.equal(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds.iterator().next()) );
            } else {
                criteria.add( PredicateFactory.in(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds) );
            }
        }
        if ( StringUtils.isNotBlank(principalId) ) {
            criteria.add( PredicateFactory.equal(KIMPropertyConstants.RoleMember.MEMBER_ID, principalId) );
        }
        criteria.add( PredicateFactory.equal(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, MemberType.PRINCIPAL.getCode()));

        Predicate roleQualificationPredicate = getRoleQualificationPredicate(qualification);
        if ( roleQualificationPredicate != null ) {
            criteria.add( roleQualificationPredicate );
        }

        List<RoleMemberBo> coll = dataObjectService.findMatching(RoleMemberBo.class, QueryByCriteria.Builder.fromPredicates(criteria) ).getResults();
        ArrayList<RoleMemberBo> results = new ArrayList<RoleMemberBo>(coll.size());
        for (RoleMemberBo rm : coll) {
            if (rm.isActive(new Timestamp(System.currentTimeMillis()))) {
                results.add(rm);
            }
        }
        return results;
    }

    protected List<RoleMemberBo> getRoleMembersForGroupIds(String roleId, List<String> groupIds) {
        if (CollectionUtils.isEmpty(groupIds)) {
            return new ArrayList<RoleMemberBo>();
        }
        List<RoleMemberBo> coll = dataObjectService.findMatching( RoleMemberBo.class,
                QueryByCriteria.Builder.fromPredicates(
                        PredicateFactory.equal(KIMPropertyConstants.RoleMember.ROLE_ID, roleId),
                        PredicateFactory.equal(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, MemberType.GROUP.getCode()),
                        PredicateFactory.in(KIMPropertyConstants.RoleMember.MEMBER_ID, groupIds) ) ).getResults();
        List<RoleMemberBo> results = new ArrayList<RoleMemberBo>(coll.size());
        for (RoleMemberBo rm : coll) {
            if (rm.isActive(new Timestamp(System.currentTimeMillis()))) {
                results.add(rm);
            }
        }
        return results;
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
                return getRoleMembersForPrincipalId(roleIds, principalId, convertedQualification);
            case ROLE_GROUPS_FOR_GROUP_IDS_AND_ROLE_IDS: // Search for group role members only.
                return getRoleGroupsForGroupIdsAndRoleIds(roleIds, groupIds, convertedQualification);
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

    public List<RoleMemberBo> getRoleGroupsForGroupIdsAndRoleIds(Collection<String> roleIds, Collection<String> groupIds, Map<String, String> qualification) {
        List<Predicate> criteria = new ArrayList<Predicate>();

        if (CollectionUtils.isNotEmpty(roleIds)) {
            criteria.add( PredicateFactory.in(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds) );
        }
        if (CollectionUtils.isNotEmpty(groupIds)) {
            criteria.add( PredicateFactory.in(KIMPropertyConstants.RoleMember.MEMBER_ID, groupIds) );
        }
        criteria.add( PredicateFactory.equal(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, MemberType.GROUP.getCode()));

        Predicate roleQualificationPredicate = getRoleQualificationPredicate(qualification);
        if ( roleQualificationPredicate != null ) {
            criteria.add( roleQualificationPredicate );
        }

        Collection<RoleMemberBo> coll = dataObjectService.findMatching(RoleMemberBo.class, QueryByCriteria.Builder.fromPredicates(criteria) ).getResults();
        ArrayList<RoleMemberBo> results = new ArrayList<RoleMemberBo>(coll.size());
        for (RoleMemberBo rm : coll) {
            if (rm.isActive(new Timestamp(System.currentTimeMillis()))) {
                results.add(rm);
            }
        }
        return results;
    }

    /**
     * Attempts to add predicates to the query to filter based on a subquery against the attribute
     * data table.
     *
     * FIXME: This has not been re-implemented in JPA.  We need subquery support in the Predicate APIs.
     *
     *  This should not be too difficult.  See the first answer here:
     *  http://stackoverflow.com/questions/4483576/jpa-2-0-criteria-api-subqueries-in-expressions
     *
     *  PredicateFactory.subquery( String parentAttributeName, Class subQueryDataObject, Predicate... predicates )
     *  (or something like that - could also pass in a build QueryByCriteria object)
     *
     *  Other consideration used in the code below which the above does not address...
     *      What about referencing the outer query?  What's the syntax for that.  OJB had a special constant.
     *
     * @param c
     * @param qualification
     */
    protected Predicate getRoleQualificationPredicate(Map<String, String> qualification) {
        return null;
//        if (qualification != null && CollectionUtils.isNotEmpty(qualification.keySet())) {
//            for (Map.Entry<String, String> qualifier : qualification.entrySet()) {
//                if (StringUtils.isNotBlank(qualifier.getValue())) {
//                    String value = (qualifier.getValue()).replace('*', '%');
//                    PredicateFactory.and(
//                            PredicateFactory.like("attributeValue", value),
//                            PredicateFactory.equal("kimAttributeId", qualifier.getKey()),
//                            PredicateFactory.equal("attributeValue", value),
//
//                    subCrit.addLike("attributeValue", value);
//                    subCrit.addEqualTo("kimAttributeId", qualifier.getKey());
//                    subCrit.addEqualToField("assignedToId", Criteria.PARENT_QUERY_PREFIX + "id");
//                    ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(RoleMemberAttributeDataBo.class, subCrit);
//                    c.addExists(subQuery);
//                }
//            }
//        }
    }

    protected List<RoleMemberBo> getRoleMembershipsForMemberId(String memberType, String memberId, Map<String, String> qualification) {
        if (StringUtils.isBlank(memberId) || StringUtils.isBlank(memberType)) {
            return new ArrayList<RoleMemberBo>(0);
        }

        List<Predicate> criteria = new ArrayList<Predicate>();

        criteria.add( PredicateFactory.equal(KIMPropertyConstants.RoleMember.MEMBER_ID, memberId) );
        criteria.add( PredicateFactory.equal(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, memberType) );

        Predicate roleQualificationPredicate = getRoleQualificationPredicate(qualification);
        if ( roleQualificationPredicate != null ) {
            criteria.add( roleQualificationPredicate );
        }

        Collection<RoleMemberBo> coll = dataObjectService.findMatching(RoleMemberBo.class, QueryByCriteria.Builder.fromPredicates(criteria) ).getResults();
        ArrayList<RoleMemberBo> results = new ArrayList<RoleMemberBo>(coll.size());
        for (RoleMemberBo rm : coll) {
            if (rm.isActive(new Timestamp(System.currentTimeMillis()))) {
                results.add(rm);
            }
        }
        return results;
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
     * Retrieves a RoleMemberBo object by its ID. If the role member already exists in the cache, this method will return the cached
     * version; otherwise, it will retrieve the uncached version from the database and then cache it (if it belongs to a role that allows
     * its members to be cached) before returning it.
     */
    protected RoleMemberBo getRoleMemberBo(String roleMemberId) {
        if (StringUtils.isBlank(roleMemberId)) {
            return null;
        }

        return getDataObjectService().find(RoleMemberBo.class, roleMemberId);
    }

    /**
     * Retrieves a RoleResponsibilityActionBo object by its ID.
     */
    protected RoleResponsibilityActionBo getRoleResponsibilityActionBo(String roleResponsibilityActionId) {
        if (StringUtils.isBlank(roleResponsibilityActionId)) {
            return null;
        }

        return getDataObjectService().find(RoleResponsibilityActionBo.class, roleResponsibilityActionId);
    }

    /**
     *
     */
    protected Map<String, DelegateTypeBo> getStoredDelegationImplMapFromRoleIds(Collection<String> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            Map<String, DelegateTypeBo> results = new HashMap<String, DelegateTypeBo>();
            Collection<DelegateTypeBo> coll = dataObjectService.findMatching(DelegateTypeBo.class,
                    QueryByCriteria.Builder.fromPredicates(
                            PredicateFactory.in(KIMPropertyConstants.Delegation.ROLE_ID, roleIds),
                            PredicateFactory.equal(KIMPropertyConstants.Delegation.ACTIVE, Boolean.TRUE) ) ).getResults();
            for (DelegateTypeBo delegateBo : coll) {
                results.put(delegateBo.getDelegationId(), delegateBo);
            }
            return results;
        }

        return Collections.emptyMap();
    }

    /**
     *
     */
    protected List<DelegateTypeBo> getStoredDelegationImplsForRoleIds(Collection<String> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            List<DelegateTypeBo> coll = dataObjectService.findMatching(DelegateTypeBo.class,
                    QueryByCriteria.Builder.fromPredicates(
                            PredicateFactory.in(KIMPropertyConstants.Delegation.ROLE_ID, roleIds),
                            PredicateFactory.equal(KIMPropertyConstants.Delegation.ACTIVE, Boolean.TRUE) ) ).getResults();

            return new ArrayList<DelegateTypeBo>( coll );
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
        return getDelegationMemberBoList(RoleDaoAction.DELEGATION_PRINCIPALS_FOR_PRINCIPAL_ID_AND_DELEGATION_IDS,
                delegationIds, principalId, null);
    }

    /**
     * Retrieves a DelegateMemberBo object by its ID. If the delegation member already exists in the cache, this method will return the cached
     * version; otherwise, it will retrieve the uncached version from the database and then cache it before returning it.
     */
    protected DelegateMemberBo getDelegateMemberBo(String delegationMemberId) {
        if (StringUtils.isBlank(delegationMemberId)) {
            return null;
        }

        return getDataObjectService().find(DelegateMemberBo.class,delegationMemberId);
    }

    /**
     * Retrieves a DelegateMemberBo List by (principal/group/role) member ID and delegation ID. If the List already exists in the cache,
     * this method will return the cached one; otherwise, it will retrieve the uncached version from the database and then cache it before returning it.
     */
    protected List<DelegateMemberBo> getDelegationMemberBoListByMemberAndDelegationId(String memberId, String delegationId) {

        Map<String, String> searchCriteria = new HashMap<String, String>(2);
        searchCriteria.put(KimConstants.PrimaryKeyConstants.MEMBER_ID, memberId);
        searchCriteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegationId);
        return new ArrayList<DelegateMemberBo>(getDataObjectService().findMatching(DelegateMemberBo.class, QueryByCriteria.Builder.andAttributes(searchCriteria).build()).getResults());
    }

    protected Object getMember(String memberTypeCode, String memberId) {
        if (StringUtils.isBlank(memberId)) {
            return null;
        }
        if (MemberType.PRINCIPAL.getCode().equals(memberTypeCode)) {
            return getIdentityService().getPrincipal(memberId);
        } else if (MemberType.GROUP.getCode().equals(memberTypeCode)) {
            return getGroupService().getGroup(memberId);
        } else if (MemberType.ROLE.getCode().equals(memberTypeCode)) {
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
        return getDataObjectService().find(RoleBo.class, roleId);
    }

    protected RoleBoLite getRoleBoLite(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            return null;
        }
        return getDataObjectService().find(RoleBoLite.class, roleId);
    }

    protected DelegateTypeBo getDelegationOfType(String roleId, DelegationType delegationType) {
        List<DelegateTypeBo> roleDelegates = getRoleDelegations(roleId);
        if (isDelegationPrimary(delegationType)) {
            return getPrimaryDelegation(roleId, roleDelegates);
        } else {
            return getSecondaryDelegation(roleId, roleDelegates);
        }
    }

    private DelegateTypeBo getSecondaryDelegation(String roleId, List<DelegateTypeBo> roleDelegates) {
        DelegateTypeBo secondaryDelegate = null;
        RoleBoLite roleBo = getRoleBoLite(roleId);
        for (DelegateTypeBo delegate : roleDelegates) {
            if (isDelegationSecondary(delegate.getDelegationType())) {
                secondaryDelegate = delegate;
            }
        }
        if (secondaryDelegate == null) {
            secondaryDelegate = new DelegateTypeBo();
            secondaryDelegate.setRoleId(roleId);
            secondaryDelegate.setDelegationType(DelegationType.SECONDARY);
            secondaryDelegate.setKimTypeId(roleBo.getKimTypeId());
        }
        return secondaryDelegate;
    }

    protected DelegateTypeBo getPrimaryDelegation(String roleId, List<DelegateTypeBo> roleDelegates) {
        DelegateTypeBo primaryDelegate = null;
        RoleBoLite roleBo = getRoleBoLite(roleId);
        for (DelegateTypeBo delegate : roleDelegates) {
            if (isDelegationPrimary(delegate.getDelegationType())) {
                primaryDelegate = delegate;
            }
        }
        if (primaryDelegate == null) {
            primaryDelegate = new DelegateTypeBo();
            primaryDelegate.setRoleId(roleId);
            primaryDelegate.setDelegationType(DelegationType.PRIMARY);
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

    protected boolean isDelegationPrimary(DelegationType delegationType) {
        return DelegationType.PRIMARY.equals(delegationType);
    }

    protected boolean isDelegationSecondary(DelegationType delegationType) {
        return DelegationType.SECONDARY.equals(delegationType);
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
        Map<String, Object> criteria = new HashMap<String, Object>(3);
        criteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode);
        criteria.put(KimConstants.UniqueKeyConstants.NAME, roleName);
        criteria.put(KRADPropertyConstants.ACTIVE, Boolean.TRUE);
        QueryResults<RoleBo> results =
                getDataObjectService().findMatching(RoleBo.class, QueryByCriteria.Builder.andAttributes(criteria).build());
        if (results.getResults().isEmpty()) {
            return null;
        } else if (results.getResults().size() > 1) {
            throw new NonUniqueResultException("Finding a role by name should return a unique role, "
                    + "but encountered multiple. namespaceCode='" + namespaceCode + "', name='" + roleName +"'");
        }
        return results.getResults().get(0);
    }

    protected RoleBoLite getRoleBoLiteByName(String namespaceCode, String roleName) {
        if (StringUtils.isBlank(namespaceCode)
                || StringUtils.isBlank(roleName)) {
            return null;
        }
        Map<String, Object> criteria = new HashMap<String, Object>(3);
        criteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode);
        criteria.put(KimConstants.UniqueKeyConstants.NAME, roleName);
        criteria.put(KRADPropertyConstants.ACTIVE, Boolean.TRUE);
        QueryResults<RoleBoLite> results =
                getDataObjectService().findMatching(RoleBoLite.class, QueryByCriteria.Builder.andAttributes(criteria).build());
        if (results.getResults().isEmpty()) {
            return null;
        } else if (results.getResults().size() > 1) {
            throw new NonUniqueResultException("Finding a role by name should return a unique role, "
                    + "but encountered multiple. namespaceCode='" + namespaceCode + "', name='" + roleName +"'");
        }
        return results.getResults().get(0);
    }

	protected List<RoleMember> doAnyMemberRecordsMatchByExactQualifier( RoleEbo role, String memberId, RoleDaoAction daoActionToTake, Map<String, String> qualifier ) {
		List<RoleMemberBo> roleMemberBos = getRoleMembersByExactQualifierMatch(role, memberId, daoActionToTake, qualifier);
        List<RoleMember> roleMembers = new ArrayList<RoleMember>();
        if(CollectionUtils.isNotEmpty(roleMemberBos)) {
            for (RoleMemberBo bo : roleMemberBos) {
                roleMembers.add(RoleMemberBo.to(bo));
            }
			return roleMembers;
		}

		return Collections.emptyList();
	}

	protected List<RoleMemberBo> getRoleMembersByExactQualifierMatch(RoleEbo role, String memberId, RoleDaoAction daoActionToTake, Map<String, String> qualifier) {
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

    //return roleMemberId of match or null if no match
    protected RoleMember doAnyMemberRecordsMatch(List<RoleMemberBo> roleMembers, String memberId, String memberTypeCode, Map<String, String> qualifier) {
        for (RoleMemberBo rm : roleMembers) {
            if (rm.isActive() && doesMemberMatch(rm, memberId, memberTypeCode, qualifier)) {
                return RoleMemberBo.to(rm);
            }
        }
        return null;
    }

    protected boolean doesMemberMatch(RoleMemberBo roleMember, String memberId, String memberTypeCode, Map<String, String> qualifier) {
        if (roleMember.getMemberId().equals(memberId) && roleMember.getType().getCode().equals(memberTypeCode)) {
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
        RoleBoLite roleBo = getRoleBoLite(roleId);
        if(roleBo != null){
            KimType roleType = KimTypeBo.to(roleBo.getKimRoleType());
            if (roleType != null) {
                return getRoleTypeService(roleType);
            }
        }
        return KimImplServiceLocator.getDefaultRoleTypeService();
    }

    /**
     * Retrieves the role type service for the given service name.
     *
     * @param serviceName the name of the service to retrieve
     * @return the Role Type Service
     */
    protected RoleTypeService getRoleTypeServiceByName(String serviceName) {
        try {
            KimTypeService service = (KimTypeService) GlobalResourceLoader.getService(QName.valueOf(serviceName));
            if (service != null && service instanceof RoleTypeService) {
                return (RoleTypeService) service;
            }
            LOG.warn("Unable to find role type service by name: " + serviceName + ". Defaulting to: kimNoMembersRoleTypeService ");
            return (RoleTypeService) KimImplServiceLocator.getService("kimNoMembersRoleTypeService");
        } catch (Exception ex) {
            LOG.warn("Unable to find role type service by name: " + serviceName, ex);
            return (RoleTypeService) KimImplServiceLocator.getService("kimNoMembersRoleTypeService");
        }
    }

    protected RoleTypeService getRoleTypeService(KimType typeInfo) {
        String serviceName = typeInfo.getServiceName();
        if (serviceName != null) {
            try {
                KimTypeService service = (KimTypeService) GlobalResourceLoader.getService(QName.valueOf(serviceName));
                if (service != null && service instanceof RoleTypeService) {
                    return (RoleTypeService) service;
                }
                LOG.warn("Unable to find role type service with name: " + serviceName + ". Defaulting to: kimNoMembersRoleTypeService ");
                return (RoleTypeService) KimImplServiceLocator.getService("kimNoMembersRoleTypeService");
            } catch (Exception ex) {
                LOG.error("Unable to find role type service with name: " + serviceName, ex);
                return (RoleTypeService) KimImplServiceLocator.getService("kimNoMembersRoleTypeService");
            }
        }
        return KimImplServiceLocator.getDefaultRoleTypeService();
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

    // TODO: pulling attribute IDs repeatedly is inefficient - consider caching the entire list as a map
    protected String getKimAttributeId(String attributeName) {
        QueryResults<KimAttributeBo> defs = getDataObjectService().findMatching(KimAttributeBo.class, QueryByCriteria.Builder.forAttribute("attributeName", attributeName).build());
        String result = null;
        if ( !defs.getResults().isEmpty() ) {
            result = defs.getResults().get(0).getId();
        }
        return result;
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

    protected ResponsibilityInternalService getResponsibilityInternalService() {
        if (responsibilityInternalService == null) {
            responsibilityInternalService = KimImplServiceLocator.getResponsibilityInternalService();
        }
        return responsibilityInternalService;
    }

    protected RoleDao getRoleDao() {
        return this.roleDao;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public DataObjectService getDataObjectService() {
        if ( dataObjectService == null ) {
            dataObjectService = KradDataServiceLocator.getDataObjectService();
        }
        return dataObjectService;
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

}
