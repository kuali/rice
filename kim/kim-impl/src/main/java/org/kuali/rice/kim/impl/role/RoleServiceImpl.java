package org.kuali.rice.kim.impl.role;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.kim.api.group.GroupMember;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleResponsibility;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.framework.common.delegate.DelegationTypeService;
import org.kuali.rice.kim.framework.role.RoleTypeService;
import org.kuali.rice.kim.framework.services.KimFrameworkServiceLocator;
import org.kuali.rice.kim.framework.type.KimTypeService;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberAttributeDataBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateTypeBo;
import org.kuali.rice.kim.impl.group.GroupMemberBo;
import org.kuali.rice.krad.service.KRADServiceLocator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RoleServiceImpl extends RoleServiceBase implements RoleService {
    private static final Logger LOG = Logger.getLogger(RoleServiceImpl.class);

    private static final Map<String, RoleDaoAction> memberTypeToRoleDaoActionMap = populateMemberTypeToRoleDaoActionMap();

    private static Map<String, RoleDaoAction> populateMemberTypeToRoleDaoActionMap() {
        Map<String, RoleDaoAction> map = new HashMap<String, RoleDaoAction>();
        map.put(Role.GROUP_MEMBER_TYPE, RoleDaoAction.ROLE_GROUPS_FOR_GROUP_IDS_AND_ROLE_IDS);
        map.put(Role.PRINCIPAL_MEMBER_TYPE, RoleDaoAction.ROLE_PRINCIPALS_FOR_PRINCIPAL_ID_AND_ROLE_IDS);
        map.put(Role.ROLE_MEMBER_TYPE, RoleDaoAction.ROLE_MEMBERSHIPS_FOR_ROLE_IDS_AS_MEMBERS);     
        return Collections.unmodifiableMap(map);
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

    protected RoleMember findRoleMember(String roleMemberId) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId);
        List<RoleMember> roleMembers = findRoleMembers(fieldValues);
        if (roleMembers != null && !roleMembers.isEmpty()) {
            return roleMembers.get(0);
        }
        return null;
    }

    @Override
    public List<RoleMember> findRoleMembers(Map<String, String> fieldValues) {
        if (fieldValues == null) {
            throw new RiceIllegalArgumentException("fieldValues is null");
        }

        List<RoleMember> roleMembers = new ArrayList<RoleMember>();
        List<RoleMemberBo> roleMemberBos = (List<RoleMemberBo>) getLookupService().findCollectionBySearchHelper(
                RoleMemberBo.class, fieldValues, true);

        for (RoleMemberBo bo : roleMemberBos) {
            RoleMember roleMember = RoleMemberBo.to(bo);
            roleMembers.add(roleMember);
        }
        return roleMembers;
    }

    @Override
    public Set<String> getRoleTypeRoleMemberIds(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            throw new RiceIllegalArgumentException("roleId is null or blank");
        }

        Set<String> results = new HashSet<String>();
        getNestedRoleTypeMemberIds(roleId, results);
        return Collections.unmodifiableSet(results);
    }

    @Override
    public List<String> getMemberParentRoleIds(String memberType, String memberId) {
        if (StringUtils.isBlank(memberType)) {
            throw new RiceIllegalArgumentException("memberType is null or blank");
        }

        if (StringUtils.isBlank(memberId)) {
            throw new RiceIllegalArgumentException("memberId is null or blank");
        }

        List<RoleMemberBo> parentRoleMembers = getRoleDao().getRoleMembershipsForMemberId(memberType, memberId, Collections.<String, String>emptyMap());

        List<String> parentRoleIds = new ArrayList<String>(parentRoleMembers.size());
        for (RoleMemberBo parentRoleMember : parentRoleMembers) {
            parentRoleIds.add(parentRoleMember.getRoleId());
        }

        return parentRoleIds;
    }

    @Override
    public List<RoleResponsibilityAction> getRoleMemberResponsibilityActions(String roleMemberId) {
        if (StringUtils.isBlank(roleMemberId)) {
            throw new RiceIllegalArgumentException("roleMemberId is null or blank");
        }

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

    @Override
    public List<DelegateMember> findDelegateMembers(final Map<String, String> fieldValues) {
        if (fieldValues == null) {
            throw new RiceIllegalArgumentException("fieldValues is null or blank");
        }

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

    @Override
    public Role getRole(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            throw new RiceIllegalArgumentException("roleId is null or blank");
        }

        RoleBo roleBo = getRoleBo(roleId);
        if (roleBo == null) {
            return null;
        }
        return RoleBo.to(roleBo);
    }

    protected Map<String, RoleBo> getRoleBoMap(Collection<String> roleIds) {
        Map<String, RoleBo> result;
        // check for a non-null result in the cache, return it if found
        if (roleIds.size() == 1) {
            String roleId = roleIds.iterator().next();
            RoleBo bo = getRoleBo(roleId);
            result = bo.isActive() ? Collections.singletonMap(roleId, bo) :  Collections.<String, RoleBo>emptyMap();
        } else {
            result = new HashMap<String, RoleBo>(roleIds.size());
            for (String roleId : roleIds) {
                RoleBo bo = getRoleBo(roleId);
                if (bo.isActive()) {
                    result.put(roleId, bo);
                }
            }
        }
        return result;
    }

    @Override
    public List<Role> getRoles(List<String> roleIds) {
        if (roleIds == null) {
            throw new RiceIllegalArgumentException("roleIds is null");
        }

        Collection<RoleBo> roleBos = getRoleBoMap(roleIds).values();
        List<Role> roles = new ArrayList<Role>(roleBos.size());
        for (RoleBo bo : roleBos) {
            roles.add(RoleBo.to(bo));
        }
        return Collections.unmodifiableList(roles);
    }

    @Override
    public Role getRoleByNameAndNamespaceCode(String namespaceCode, String roleName) {
        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode is blank or null");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName is blank or null");
        }

        RoleBo roleBo = getRoleBoByName(namespaceCode, roleName);
        if (roleBo != null) {
            return RoleBo.to(roleBo);
        }
        return null;
    }

    @Override
    public String getRoleIdByNameAndNamespaceCode(String namespaceCode, String roleName) {
        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode is blank or null");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName is blank or null");
        }

        Role role = getRoleByNameAndNamespaceCode(namespaceCode, roleName);
        if (role != null) {
            return role.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean isRoleActive(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            throw new RiceIllegalArgumentException("roleId is blank or null");
        }

        RoleBo roleBo = getRoleBo(roleId);
        return roleBo != null && roleBo.isActive();
    }

    @Override
    public List<Map<String, String>> getRoleQualifiersForPrincipal(String principalId,
                                                            List<String> roleIds,
                                                            Map<String, String> qualification) {
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId is blank or null");
        }

        if (roleIds == null) {
            throw new RiceIllegalArgumentException("roleIds is null");
        }

        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification is null");
        }

        List<Map<String, String>> results = new ArrayList<Map<String, String>>();

        List<RoleMemberBo> roleMemberBoList = getStoredRoleMembersUsingExactMatchOnQualification(principalId, null, roleIds, qualification);

        Map<String, List<RoleMembership>> roleIdToMembershipMap = new HashMap<String, List<RoleMembership>>();
        for (RoleMemberBo roleMemberBo : roleMemberBoList) {
            // gather up the qualifier sets and the service they go with
            if (roleMemberBo.getMemberTypeCode().equals(Role.PRINCIPAL_MEMBER_TYPE)) {
                RoleTypeService roleTypeService = getRoleTypeService(roleMemberBo.getRoleId());
                if (roleTypeService != null) {
                    List<RoleMembership> las = roleIdToMembershipMap.get(roleMemberBo.getRoleId());
                    if (las == null) {
                        las = new ArrayList<RoleMembership>();
                        roleIdToMembershipMap.put(roleMemberBo.getRoleId(), las);
                    }
                    RoleMembership mi = RoleMembership.Builder.create(
                            roleMemberBo.getRoleId(),
                            roleMemberBo.getRoleMemberId(),
                            roleMemberBo.getMemberId(),
                            roleMemberBo.getMemberTypeCode(),
                            roleMemberBo.getAttributes()).build();

                    las.add(mi);
                } else {
                    results.add(roleMemberBo.getAttributes());
                }
            }
        }
        for (Map.Entry<String, List<RoleMembership>> entry : roleIdToMembershipMap.entrySet()) {
            RoleTypeService roleTypeService = getRoleTypeService(entry.getKey());
            //it is possible that the the roleTypeService is coming from a remote application
            // and therefore it can't be guaranteed that it is up and working, so using a try/catch to catch this possibility.
            try {
                List<RoleMembership> matchingMembers = roleTypeService.getMatchingRoleMemberships(qualification, entry.getValue());
                for (RoleMembership rmi : matchingMembers) {
                    results.add(rmi.getQualifier());
                }
            } catch (Exception ex) {
                LOG.warn("Not able to retrieve RoleTypeService from remote system for role Id: " + entry.getKey(), ex);
            }
        }
        return Collections.unmodifiableList(results);
    }

    @Override
    public List<Map<String, String>> getRoleQualifiersForPrincipal(String principalId, String namespaceCode,  String roleName, Map<String, String> qualification) {
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId is blank or null");
        }

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode is blank or null");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName is blank or null");
        }

        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification is null");
        }

        String roleId = getRoleIdByNameAndNamespaceCode(namespaceCode, roleName);
        if (roleId == null) {
            return Collections.emptyList();
        }
        return getNestedRoleQualifiersForPrincipal(principalId, Collections.singletonList(roleId), qualification);
    }

    @Override
    public List<Map<String, String>> getNestedRoleQualifiersForPrincipal(String principalId, String namespaceCode, String roleName, Map<String, String> qualification) {
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId is blank or null");
        }

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode is blank or null");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName is blank or null");
        }

        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification is null");
        }

        String roleId = getRoleIdByNameAndNamespaceCode(namespaceCode, roleName);
        if (roleId == null) {
            return new ArrayList<Map<String, String>>(0);
        }
        return getNestedRoleQualifiersForPrincipal(principalId, Collections.singletonList(roleId), qualification);
    }

    @Override
    public List<Map<String, String>> getNestedRoleQualifiersForPrincipal(String principalId, List<String> roleIds, Map<String, String> qualification) {
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId is blank or null");
        }

        if (roleIds == null) {
            throw new RiceIllegalArgumentException("roleIds is null");
        }

        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification is null");
        }


        List<Map<String, String>> results = new ArrayList<Map<String, String>>();

        Map<String, RoleBo> roleBosById = getRoleBoMap(roleIds);

        // get the person's groups
        List<String> groupIds = getGroupService().getGroupIdsByPrincipalId(principalId);
        List<RoleMemberBo> roleMemberBos = getStoredRoleMembersUsingExactMatchOnQualification(principalId, groupIds, roleIds, qualification);

        Map<String, List<RoleMembership>> roleIdToMembershipMap = new HashMap<String, List<RoleMembership>>();
        for (RoleMemberBo roleMemberBo : roleMemberBos) {
            RoleTypeService roleTypeService = getRoleTypeService(roleMemberBo.getRoleId());
            // gather up the qualifier sets and the service they go with
            if (roleMemberBo.getMemberTypeCode().equals(Role.PRINCIPAL_MEMBER_TYPE)
                    || roleMemberBo.getMemberTypeCode().equals(Role.GROUP_MEMBER_TYPE)) {
                if (roleTypeService != null) {
                    List<RoleMembership> las = roleIdToMembershipMap.get(roleMemberBo.getRoleId());
                    if (las == null) {
                        las = new ArrayList<RoleMembership>();
                        roleIdToMembershipMap.put(roleMemberBo.getRoleId(), las);
                    }
                    RoleMembership mi = RoleMembership.Builder.create(
                            roleMemberBo.getRoleId(),
                            roleMemberBo.getRoleMemberId(),
                            roleMemberBo.getMemberId(),
                            roleMemberBo.getMemberTypeCode(),
                            roleMemberBo.getAttributes()).build();

                    las.add(mi);
                } else {
                    results.add(roleMemberBo.getAttributes());
                }
            } else if (roleMemberBo.getMemberTypeCode().equals(Role.ROLE_MEMBER_TYPE)) {
                // find out if the user has the role
                // need to convert qualification using this role's service
                Map<String, String> nestedQualification = qualification;
                if (roleTypeService != null) {
                    RoleBo roleBo = roleBosById.get(roleMemberBo.getRoleId());
                    // pulling from here as the nested roleBo is not necessarily (and likely is not)
                    // in the roleBosById Map created earlier
                    RoleBo nestedRole = getRoleBo(roleMemberBo.getMemberId());
                    //it is possible that the the roleTypeService is coming from a remote application
                    // and therefore it can't be guaranteed that it is up and working, so using a try/catch to catch this possibility.
                    try {
                        nestedQualification = roleTypeService.convertQualificationForMemberRoles(roleBo.getNamespaceCode(), roleBo.getName(), nestedRole.getNamespaceCode(), nestedRole.getName(), qualification);
                    } catch (Exception ex) {
                        LOG.warn("Not able to retrieve RoleTypeService from remote system for roleBo Id: " + roleBo.getId(), ex);
                    }
                }
                List<String> nestedRoleId = new ArrayList<String>(1);
                nestedRoleId.add(roleMemberBo.getMemberId());
                // if the user has the given role, add the qualifier the *nested role* has with the
                // originally queries role
                if (principalHasRole(principalId, nestedRoleId, nestedQualification, false)) {
                    results.add(roleMemberBo.getAttributes());
                }
            }
        }
        for (Map.Entry<String, List<RoleMembership>> entry : roleIdToMembershipMap.entrySet()) {
            RoleTypeService roleTypeService = getRoleTypeService(entry.getKey());
            //it is possible that the the roleTypeService is coming from a remote application
            // and therefore it can't be guaranteed that it is up and working, so using a try/catch to catch this possibility.
            try {
                List<RoleMembership> matchingMembers = roleTypeService.getMatchingRoleMemberships(qualification,
                        entry.getValue());
                for (RoleMembership roleMembership : matchingMembers) {
                    results.add(roleMembership.getQualifier());
                }
            } catch (Exception ex) {
                LOG.warn("Not able to retrieve RoleTypeService from remote system for role Id: " + entry.getKey(), ex);
            }
        }
        return Collections.unmodifiableList(results);
    }

    @Override
    public List<RoleMembership> getRoleMembers(List<String> roleIds, Map<String, String> qualification) {
        if (roleIds == null) {
            throw new RiceIllegalArgumentException("roleIds is null");
        }

        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification is null");
        }

        Set<String> foundRoleTypeMembers = new HashSet<String>();
        return getRoleMembers(roleIds, qualification, true, foundRoleTypeMembers);
    }

    @Override
    public Collection<String> getRoleMemberPrincipalIds(String namespaceCode, String roleName, Map<String, String> qualification) {
        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode is blank or null");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName is blank or null");
        }

        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification is null");
        }

        Set<String> principalIds = new HashSet<String>();
        Set<String> foundRoleTypeMembers = new HashSet<String>();
        List<String> roleIds = Collections.singletonList(getRoleIdByNameAndNamespaceCode(namespaceCode, roleName));
        for (RoleMembership roleMembership : getRoleMembers(roleIds, qualification, false, foundRoleTypeMembers)) {
            if (Role.GROUP_MEMBER_TYPE.equals(roleMembership.getMemberTypeCode())) {
                principalIds.addAll(getGroupService().getMemberPrincipalIds(roleMembership.getMemberId()));
            } else {
                principalIds.add(roleMembership.getMemberId());
            }
        }
        return Collections.unmodifiableSet(principalIds);
    }

    @Override
    public boolean principalHasRole(String principalId, List<String> roleIds, Map<String, String> qualification) {
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId is blank or null");
        }

        if (roleIds == null) {
            throw new RiceIllegalArgumentException("roleIds is null");
        }

        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification is null");
        }

        return principalHasRole(principalId, roleIds, qualification, true);
    }

    @Override
    public List<String> getPrincipalIdSubListWithRole(List<String> principalIds, String roleNamespaceCode, String roleName, Map<String, String> qualification) {
        if (principalIds == null) {
            throw new RiceIllegalArgumentException("principalIds is null");
        }

        if (StringUtils.isBlank(roleNamespaceCode)) {
            throw new RiceIllegalArgumentException("roleNamespaceCode is null");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName is null");
        }

        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification is null");
        }

        List<String> subList = new ArrayList<String>();
        RoleBo role = getRoleBoByName(roleNamespaceCode, roleName);
        for (String principalId : principalIds) {
            if (principalHasRole(principalId, Collections.singletonList(role.getId()), qualification)) {
                subList.add(principalId);
            }
        }
        return Collections.unmodifiableList(subList);
    }

    @Override
    public List<Role> getRolesSearchResults(Map<String, String> fieldValues) {
        if (fieldValues == null) {
            throw new RiceIllegalArgumentException("fieldValues is null");
        }

        List<RoleBo> roleBoList = getRoleDao().getRoles(fieldValues);
        List<Role> roles = new ArrayList<Role>();
        for (RoleBo roleBo : roleBoList) {
            roles.add(RoleBo.to(roleBo));
        }
        return Collections.unmodifiableList(roles);
    }

    @Override
    public List<RoleMembership> getFirstLevelRoleMembers(List<String> roleIds) {
        if (roleIds == null) {
            throw new RiceIllegalArgumentException("roleIds is null");
        }

        List<RoleMemberBo> roleMemberBoList = getStoredRoleMembersForRoleIds(roleIds, null, null);
        List<RoleMembership> roleMemberships = new ArrayList<RoleMembership>();
        for (RoleMemberBo roleMemberBo : roleMemberBoList) {
            RoleMembership roleMembeship = RoleMembership.Builder.create(
                    roleMemberBo.getRoleId(),
                    roleMemberBo.getRoleMemberId(),
                    roleMemberBo.getMemberId(),
                    roleMemberBo.getMemberTypeCode(),
                    roleMemberBo.getAttributes()).build();
            roleMemberships.add(roleMembeship);
        }
        return Collections.unmodifiableList(roleMemberships);
    }

    @Override
    public List<RoleMembership> findRoleMemberships( Map<String, String> fieldValues) {
        if (fieldValues == null) {
            throw new RiceIllegalArgumentException("fieldValues is null");
        }
        List<RoleMembership> l = getRoleDao().getRoleMembers(fieldValues);
        return l != null ? Collections.unmodifiableList(l) : Collections.<RoleMembership>emptyList();
    }

    @Override
    public List<DelegateMember> getDelegationMembersByDelegationId(String delegationId) {
        if (StringUtils.isBlank(delegationId)) {
            throw new RiceIllegalArgumentException("delegationId is null or blank");
        }

        DelegateTypeBo delegateBo = getKimDelegationImpl(delegationId);
        if (delegateBo == null) {return Collections.emptyList();}

        return getDelegateMembersForDelegation(delegateBo);
    }

    @Override
    public DelegateMember getDelegationMemberByDelegationAndMemberId(String delegationId, String memberId) {
        if (StringUtils.isBlank(delegationId)) {
            throw new RiceIllegalArgumentException("delegationId is null or blank");
        }

        if (StringUtils.isBlank(memberId)) {
            throw new RiceIllegalArgumentException("memberId is null or blank");
        }

        DelegateTypeBo delegateBo = getKimDelegationImpl(delegationId);
        DelegateMemberBo delegationMember = getKimDelegationMemberImplByDelegationAndId(delegationId, memberId);

        return getDelegateCompleteInfo(delegateBo, delegationMember);
    }

    @Override
    public DelegateMember getDelegationMemberById(String delegationMemberId) {

        if (StringUtils.isBlank(delegationMemberId)) {
            throw new RiceIllegalArgumentException("delegationMemberId is null or blank");
        }

        DelegateMemberBo delegateMemberBo = getDelegateMemberBo(delegationMemberId);
        if (delegateMemberBo == null) {
            return null;
        }

        DelegateTypeBo delegateBo = getKimDelegationImpl(delegateMemberBo.getDelegationId());

        return getDelegateCompleteInfo(delegateBo, delegateMemberBo);
    }

    @Override
    public List<RoleResponsibility> getRoleResponsibilities(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            throw new RiceIllegalArgumentException("roleId is null or blank");
        }

        Map<String, String> criteria = new HashMap<String, String>(1);
        criteria.put(KimConstants.PrimaryKeyConstants.SUB_ROLE_ID, roleId);
        List<RoleResponsibilityBo> roleResponsibilityBos = (List<RoleResponsibilityBo>)
                getBusinessObjectService().findMatching(RoleResponsibilityBo.class, criteria);
        List<RoleResponsibility> roleResponsibilities = new ArrayList<RoleResponsibility>();

        for (RoleResponsibilityBo roleResponsibilityImpl : roleResponsibilityBos) {
            roleResponsibilities.add(RoleResponsibilityBo.to(roleResponsibilityImpl));
        }
        return Collections.unmodifiableList(roleResponsibilities);
    }

    @Override
    public DelegateType getDelegateTypeInfo(String roleId, String delegationTypeCode) {
        if (StringUtils.isBlank(roleId)) {
            throw new RiceIllegalArgumentException("roleId is null or blank");
        }

        if (StringUtils.isBlank(delegationTypeCode)) {
            throw new RiceIllegalArgumentException("delegationTypeCode is null or blank");
        }

        DelegateTypeBo delegateBo = getDelegationOfType(roleId, delegationTypeCode);
        return DelegateTypeBo.to(delegateBo);
    }

    @Override
    public DelegateType getDelegateTypeInfoById(String delegationId) {
        if (StringUtils.isBlank(delegationId)) {
            throw new RiceIllegalArgumentException("delegationId is null or blank");
        }

        DelegateTypeBo delegateBo = getKimDelegationImpl(delegationId);
        return DelegateTypeBo.to(delegateBo);
    }

    @Override
    public void applicationRoleMembershipChanged(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            throw new RiceIllegalArgumentException("roleId is null or blank");
        }

        getResponsibilityInternalService().updateActionRequestsForRoleChange(roleId);
    }

    @Override
    public List<Role> lookupRoles(Map<String, String> searchCriteria) {
        if (searchCriteria == null) {
            throw new RiceIllegalArgumentException("searchCriteria is null");
        }

        Collection<RoleBo> roleBoCollection = getBusinessObjectService().findMatching(RoleBo.class, searchCriteria);
        ArrayList<Role> roleList = new ArrayList<Role>();
        for (RoleBo roleBo : roleBoCollection) {
            roleList.add(RoleBo.to(roleBo));
        }
        return Collections.unmodifiableList(roleList);
    }

    protected List<RoleMembership> getRoleMembers(List<String> roleIds, Map<String, String> qualification, boolean followDelegations, Set<String> foundRoleTypeMembers) {
        List<RoleMembership> results = new ArrayList<RoleMembership>();
        Set<String> allRoleIds = new HashSet<String>();
        for (String roleId : roleIds) {
            if (isRoleActive(roleId)) {
                allRoleIds.add(roleId);
            }
        }
        // short-circuit if no roles match
        if (allRoleIds.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> matchingRoleIds = new HashSet<String>(allRoleIds.size());
        // for efficiency, retrieve all roles and store in a map
        Map<String, RoleBo> roles = getRoleBoMap(allRoleIds);

        List<String> copyRoleIds = new ArrayList<String>(allRoleIds);
        List<RoleMemberBo> rms = new ArrayList<RoleMemberBo>();

        for (String roleId : allRoleIds) {
            RoleTypeService roleTypeService = getRoleTypeService(roleId);
            if (roleTypeService != null) {
                List<String> attributesForExactMatch = roleTypeService.getQualifiersForExactMatch();
                if (CollectionUtils.isNotEmpty(attributesForExactMatch)) {
                    copyRoleIds.remove(roleId);
                    rms.addAll(getStoredRoleMembersForRoleIds(Collections.singletonList(roleId), null, populateQualifiersForExactMatch(qualification, attributesForExactMatch)));
                }
            }
        }
        if (CollectionUtils.isNotEmpty(copyRoleIds)) {
            rms.addAll(getStoredRoleMembersForRoleIds(copyRoleIds, null, null));
        }

        // build a map of role ID to membership information
        // this will be used for later qualification checks
        Map<String, List<RoleMembership>> roleIdToMembershipMap = new HashMap<String, List<RoleMembership>>();
        for (RoleMemberBo roleMemberBo : rms) {
            RoleMembership mi = RoleMembership.Builder.create(
                    roleMemberBo.getRoleId(),
                    roleMemberBo.getRoleMemberId(),
                    roleMemberBo.getMemberId(),
                    roleMemberBo.getMemberTypeCode(),
                    roleMemberBo.getAttributes()).build();

            // if the qualification check does not need to be made, just add the result
            if ((qualification == null || qualification.isEmpty()) || getRoleTypeService(roleMemberBo.getRoleId()) == null) {
                if (roleMemberBo.getMemberTypeCode().equals(Role.ROLE_MEMBER_TYPE)) {
                    // if a role member type, do a non-recursive role member check
                    // to obtain the group and principal members of that role
                    // given the qualification
                    Map<String, String> nestedRoleQualification = qualification;
                    if (getRoleTypeService(roleMemberBo.getRoleId()) != null) {
                        // get the member role object
                        RoleBo memberRole = getRoleBo(mi.getMemberId());
                        nestedRoleQualification = getRoleTypeService(roleMemberBo.getRoleId())
                                .convertQualificationForMemberRoles(
                                        roles.get(roleMemberBo.getRoleId()).getNamespaceCode(),
                                        roles.get(roleMemberBo.getRoleId()).getName(),
                                        memberRole.getNamespaceCode(),
                                        memberRole.getName(),
                                        qualification);
                    }
                    if (isRoleActive(roleMemberBo.getRoleId())) {
                        Collection<RoleMembership> nestedRoleMembers = getNestedRoleMembers(nestedRoleQualification, mi, foundRoleTypeMembers);
                        if (!nestedRoleMembers.isEmpty()) {
                            results.addAll(nestedRoleMembers);
                            matchingRoleIds.add(roleMemberBo.getRoleId());
                        }
                    }
                } else { // not a role member type
                    results.add(mi);
                    matchingRoleIds.add(roleMemberBo.getRoleId());
                }
                matchingRoleIds.add(roleMemberBo.getRoleId());
            } else {
                List<RoleMembership> lrmi = roleIdToMembershipMap.get(mi.getRoleId());
                if (lrmi == null) {
                    lrmi = new ArrayList<RoleMembership>();
                    roleIdToMembershipMap.put(mi.getRoleId(), lrmi);
                }
                lrmi.add(mi);
            }
        }
        // if there is anything in the role to membership map, we need to check the role type services
        // for those entries
        if (!roleIdToMembershipMap.isEmpty()) {
            // for each role, send in all the qualifiers for that role to the type service
            // for evaluation, the service will return those which match
            for (Map.Entry<String, List<RoleMembership>> entry : roleIdToMembershipMap.entrySet()) {
                //it is possible that the the roleTypeService is coming from a remote application
                // and therefore it can't be guaranteed that it is up and working, so using a try/catch to catch this possibility.
                try {
                    RoleTypeService roleTypeService = getRoleTypeService(entry.getKey());
                    List<RoleMembership> matchingMembers = roleTypeService.getMatchingRoleMemberships(qualification,
                            entry.getValue());
                    // loop over the matching entries, adding them to the results
                    for (RoleMembership roleMemberships : matchingMembers) {
                        if (roleMemberships.getMemberTypeCode().equals(Role.ROLE_MEMBER_TYPE)) {
                            // if a role member type, do a non-recursive role member check
                            // to obtain the group and principal members of that role
                            // given the qualification
                            // get the member role object
                            RoleBo memberRole = getRoleBo(roleMemberships.getMemberId());
                            if (memberRole.isActive()) {
                                Map<String, String> nestedRoleQualification = roleTypeService.convertQualificationForMemberRoles(
                                        roles.get(roleMemberships.getRoleId()).getNamespaceCode(),
                                        roles.get(roleMemberships.getRoleId()).getName(),
                                        memberRole.getNamespaceCode(),
                                        memberRole.getName(),
                                        qualification);
                                Collection<RoleMembership> nestedRoleMembers = getNestedRoleMembers(nestedRoleQualification, roleMemberships, foundRoleTypeMembers);
                                if (!nestedRoleMembers.isEmpty()) {
                                    results.addAll(nestedRoleMembers);
                                    matchingRoleIds.add(roleMemberships.getRoleId());
                                }
                            }
                        } else { // not a role member
                            results.add(roleMemberships);
                            matchingRoleIds.add(roleMemberships.getRoleId());
                        }
                    }
                } catch (Exception ex) {
                    LOG.warn("Not able to retrieve RoleTypeService from remote system for role Id: " + entry.getKey(), ex);
                }
            }
        }
        return Collections.unmodifiableList(results);
    }


    protected boolean principalHasRole(String principalId, List<String> roleIds, Map<String, String> qualification, boolean checkDelegations) {
        if (StringUtils.isBlank(principalId)) {
            return false;
        }
        Set<String> allRoleIds = new HashSet<String>();
        // remove inactive roles
        for (String roleId : roleIds) {
            if (isRoleActive(roleId)) {
                allRoleIds.add(roleId);
            }
        }
        // short-circuit if no roles match
        if (allRoleIds.isEmpty()) {
            return false;
        }
        // for efficiency, retrieve all roles and store in a map
        Map<String, RoleBo> roles = getRoleBoMap(allRoleIds);
        // get all roles to which the principal is assigned
        List<String> copyRoleIds = new ArrayList<String>(allRoleIds);
        List<RoleMemberBo> rps = new ArrayList<RoleMemberBo>();

        for (String roleId : allRoleIds) {
            RoleTypeService roleTypeService = getRoleTypeService(roleId);
            if (roleTypeService != null) {
                List<String> attributesForExactMatch = roleTypeService.getQualifiersForExactMatch();
                if (CollectionUtils.isNotEmpty(attributesForExactMatch)) {
                    copyRoleIds.remove(roleId);
                    rps.addAll(getStoredRolePrincipalsForPrincipalIdAndRoleIds(Collections.singletonList(roleId), principalId, populateQualifiersForExactMatch(qualification, attributesForExactMatch)));
                }
            }
        }
        if (CollectionUtils.isNotEmpty(copyRoleIds)) {
            rps.addAll(getStoredRolePrincipalsForPrincipalIdAndRoleIds(copyRoleIds, principalId, null));
        }

        // if the qualification is null and the role list is not, then any role in the list will match
        // so since the role ID list is not blank, we can return true at this point
        if ((qualification == null || qualification.isEmpty()) && !rps.isEmpty()) {
            return true;
        }

        // check each membership to see if the principal matches

        // build a map of role ID to membership information
        // this will be used for later qualification checks
        Map<String, List<RoleMembership>> roleIdToMembershipMap = new HashMap<String, List<RoleMembership>>();
        if (getRoleIdToMembershipMap(roleIdToMembershipMap, rps)) {
            return true;
        }

        // perform the checks against the role type services
        for (Map.Entry<String, List<RoleMembership>> entry : roleIdToMembershipMap.entrySet()) {
            try {
                RoleTypeService roleTypeService = getRoleTypeService(entry.getKey());
                if (!roleTypeService.getMatchingRoleMemberships(qualification, entry.getValue()).isEmpty()) {
                    return true;
                }
            } catch (Exception ex) {
                LOG.warn("Unable to find role type service with id: " + entry.getKey());
            }
        }

        // find the groups that the principal belongs to
        List<String> principalGroupIds = getGroupService().getGroupIdsByPrincipalId(principalId);
        // find the role/group associations
        if (!principalGroupIds.isEmpty()) {
            List<RoleMemberBo> rgs = getStoredRoleGroupsUsingExactMatchOnQualification(principalGroupIds, allRoleIds, qualification);
            roleIdToMembershipMap.clear(); // clear the role/member map for further use
            if (getRoleIdToMembershipMap(roleIdToMembershipMap, rgs)) {
                return true;
            }

            // perform the checks against the role type services
            for (Map.Entry<String, List<RoleMembership>> entry : roleIdToMembershipMap.entrySet()) {
                try {
                    RoleTypeService roleTypeService = getRoleTypeService(entry.getKey());
                    if (!roleTypeService.getMatchingRoleMemberships(qualification, entry.getValue()).isEmpty()) {
                        return true;
                    }
                } catch (Exception ex) {
                    LOG.warn("Unable to find role type service with id: " + entry.getKey(), ex);
                }
            }
        }

        // check member roles
        // first, check that the qualifiers on the role membership match
        // then, perform a principalHasRole on the embedded role
        List<RoleMemberBo> roleMemberBos = getStoredRoleMembersForRoleIds(roleIds, Role.ROLE_MEMBER_TYPE, null);
        for (RoleMemberBo roleMemberBo : roleMemberBos) {
            RoleTypeService roleTypeService = getRoleTypeService(roleMemberBo.getRoleId());
            if (roleTypeService != null) {
                //it is possible that the the roleTypeService is coming from a remote application
                // and therefore it can't be guaranteed that it is up and working, so using a try/catch to catch this possibility.
                try {
                    if (roleTypeService.doesRoleQualifierMatchQualification(qualification, roleMemberBo.getAttributes())) {
                        RoleBo memberRole = getRoleBo(roleMemberBo.getMemberId());
                        Map<String, String> nestedRoleQualification = roleTypeService.convertQualificationForMemberRoles(
                                roles.get(roleMemberBo.getRoleId()).getNamespaceCode(),
                                roles.get(roleMemberBo.getRoleId()).getName(),
                                memberRole.getNamespaceCode(),
                                memberRole.getName(),
                                qualification);
                        ArrayList<String> roleIdTempList = new ArrayList<String>(1);
                        roleIdTempList.add(roleMemberBo.getMemberId());
                        if (principalHasRole(principalId, roleIdTempList, nestedRoleQualification, true)) {
                            return true;
                        }
                    }
                } catch (Exception ex) {
                    LOG.warn("Not able to retrieve RoleTypeService from remote system for role Id: " + roleMemberBo.getRoleId(), ex);
                    //return false;
                }
            } else {
                // no qualifiers - role is always used - check membership
                ArrayList<String> roleIdTempList = new ArrayList<String>(1);
                roleIdTempList.add(roleMemberBo.getMemberId());
                // no role type service, so can't convert qualification - just pass as is
                if (principalHasRole(principalId, roleIdTempList, qualification, true)) {
                    return true;
                }
            }

        }


        // check for application roles and extract principals and groups from that - then check them against the
        // role type service passing in the qualification and principal - the qualifier comes from the
        // external system (application)

        // loop over the allRoleIds list
        for (String roleId : allRoleIds) {
            RoleBo role = roles.get(roleId);
            RoleTypeService roleTypeService = getRoleTypeService(roleId);
            // check if an application role
            //it is possible that the the roleTypeService is coming from a remote application
            // and therefore it can't be guaranteed that it is up and working, so using a try/catch to catch this possibility.
            try {
                if (isApplicationRoleType(role.getKimTypeId(), roleTypeService)) {
                    if (roleTypeService.hasApplicationRole(principalId, principalGroupIds, role.getNamespaceCode(), role.getName(), qualification)) {
                        return true;
                    }
                }
            } catch (Exception ex) {
                LOG.warn("Not able to retrieve RoleTypeService from remote system for role Id: " + roleId, ex);
                //return false;
            }
        }

        // delegations
        if (checkDelegations) {
            if (matchesOnDelegation(allRoleIds, principalId, principalGroupIds, qualification)) {
                return true;
            }
        }

        // NOTE: this logic is a little different from the getRoleMembers method
        // If there is no primary (matching non-delegate), this method will still return true
        return false;
    }


    protected boolean isApplicationRoleType(String roleTypeId, RoleTypeService service) {
        return service != null && service.isApplicationRoleType();
    }

    /**
     * Support method for principalHasRole.  Checks delegations on the passed in roles for the given principal and groups.  (It's assumed that the principal
     * belongs to the given groups.)
     * <p/>
     * Delegation checks are mostly the same as role checks except that the delegateBo itself is qualified against the original role (like a RolePrincipal
     * or RoleGroup.)  And then, the members of that delegateBo may have additional qualifiers which are not part of the original role qualifiers.
     * <p/>
     * For example:
     * <p/>
     * A role could be qualified by organization.  So, there is a person in the organization with primary authority for that org.  But, then they delegate authority
     * for that organization (not their authority - the delegateBo is attached to the org.)  So, in this case the delegateBo has a qualifier of the organization
     * when it is attached to the role.
     * <p/>
     * The principals then attached to that delegateBo (which is specific to the organization), may have additional qualifiers.
     * For Example: dollar amount range, effective dates, document types.
     * As a subsequent step, those qualifiers are checked against the qualification passed in from the client.
     */
    protected boolean matchesOnDelegation(Set<String> allRoleIds, String principalId, List<String> principalGroupIds, Map<String, String> qualification) {
        // get the list of delegations for the roles
        Map<String, DelegateTypeBo> delegations = getStoredDelegationImplMapFromRoleIds(allRoleIds);
        // loop over the delegations - determine those which need to be inspected more directly
        for (DelegateTypeBo delegation : delegations.values()) {
            // check if each one matches via the original role type service
            if (!delegation.isActive()) {
                continue;
            }
            RoleTypeService roleTypeService = getRoleTypeService(delegation.getRoleId());
            for (DelegateMemberBo delegateMemberBo : delegation.getMembers()) {
                if (!delegateMemberBo.isActive(new Timestamp(new Date().getTime()))) {
                    continue;
                }
                // check if this delegateBo record applies to the given person
                if (delegateMemberBo.getTypeCode().equals(Role.PRINCIPAL_MEMBER_TYPE)
                        && !delegateMemberBo.getMemberId().equals(principalId)) {
                    continue; // no match on principal
                }
                // or if a group
                if (delegateMemberBo.getTypeCode().equals(Role.GROUP_MEMBER_TYPE)
                        && !principalGroupIds.contains(delegateMemberBo.getMemberId())) {
                    continue; // no match on group
                }
                // or if a role
                if (delegateMemberBo.getTypeCode().equals(Role.ROLE_MEMBER_TYPE)
                        && !principalHasRole(principalId, Collections.singletonList(delegateMemberBo.getMemberId()), qualification, false)) {
                    continue; // no match on role
                }
                // OK, the member matches the current user, now check the qualifications

                // NOTE: this compare is slightly different than the member enumeration
                // since the requested qualifier is always being used rather than
                // the role qualifier for the member (which is not available)

                //it is possible that the the roleTypeService is coming from a remote application
                // and therefore it can't be guaranteed that it is up and working, so using a try/catch to catch this possibility.
                try {
                    //TODO: remove reference to Attributes here and use Attributes instead.
                    if (roleTypeService != null && !roleTypeService.doesRoleQualifierMatchQualification(qualification, delegateMemberBo.getQualifier())) {
                        continue; // no match - skip to next record
                    }
                } catch (Exception ex) {
                    LOG.warn("Unable to call doesRoleQualifierMatchQualification on role type service for role Id: " + delegation.getRoleId() + " / " + qualification + " / " + delegateMemberBo.getQualifier(), ex);
                    continue;
                }

                // role service matches this qualifier
                // now try the delegateBo service
                DelegationTypeService delegationTypeService = getDelegationTypeService(delegateMemberBo.getDelegationId());
                // QUESTION: does the qualifier map need to be merged with the main delegateBo qualification?
                if (delegationTypeService != null && !delegationTypeService.doesDelegationQualifierMatchQualification(qualification, delegateMemberBo.getQualifier())) {
                    continue; // no match - skip to next record
                }
                // check if a role member ID is present on the delegateBo record
                // if so, check that the original role member would match the given qualifiers
                if (StringUtils.isNotBlank(delegateMemberBo.getRoleMemberId())) {
                    RoleMemberBo rm = getRoleMemberBo(delegateMemberBo.getRoleMemberId());
                    if (rm != null) {
                        // check that the original role member's is active and that their
                        // qualifier would have matched this request's
                        // qualifications (that the original person would have the permission/responsibility
                        // for an action)
                        // this prevents a role-membership based delegateBo from surviving the inactivation/
                        // changing of the main person's role membership
                        if (!rm.isActive(new Timestamp(new Date().getTime()))) {
                            continue;
                        }
                        Map<String, String> roleQualifier = rm.getAttributes();
                        //it is possible that the the roleTypeService is coming from a remote application
                        // and therefore it can't be guaranteed that it is up and working, so using a try/catch to catch this possibility.
                        try {
                            if (roleTypeService != null && !roleTypeService.doesRoleQualifierMatchQualification(qualification, roleQualifier)) {
                                continue;
                            }
                        } catch (Exception ex) {
                            LOG.warn("Unable to call doesRoleQualifierMatchQualification on role type service for role Id: " + delegation.getRoleId() + " / " + qualification + " / " + roleQualifier, ex);
                            continue;
                        }
                    } else {
                        LOG.warn("Unknown role member ID cited in the delegateBo member table:");
                        LOG.warn("       assignedToId: " + delegateMemberBo.getDelegationMemberId() + " / roleMemberId: " + delegateMemberBo.getRoleMemberId());
                    }
                }
                // all tests passed, return true
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method used by principalHasRole to build the role ID -> list of members map.
     *
     * @return <b>true</b> if no further checks are needed because no role service is defined
     */
    protected boolean getRoleIdToMembershipMap(Map<String, List<RoleMembership>> roleIdToMembershipMap, List<RoleMemberBo> roleMembers) {
        for (RoleMemberBo roleMemberBo : roleMembers) {
            RoleMembership roleMembership = RoleMembership.Builder.create(
                    roleMemberBo.getRoleId(),
                    roleMemberBo.getRoleMemberId(),
                    roleMemberBo.getMemberId(),
                    roleMemberBo.getMemberTypeCode(),
                    roleMemberBo.getAttributes()).build();

            // if the role type service is null, assume that all qualifiers match
            if (getRoleTypeService(roleMemberBo.getRoleId()) == null) {
                return true;
            }
            List<RoleMembership> lrmi = roleIdToMembershipMap.get(roleMembership.getRoleId());
            if (lrmi == null) {
                lrmi = new ArrayList<RoleMembership>();
                roleIdToMembershipMap.put(roleMembership.getRoleId(), lrmi);
            }
            lrmi.add(roleMembership);
        }
        return false;
    }

    /**
     * Retrieves a KimDelegationImpl object by its ID. If the delegateBo already exists in the cache, this method will return the cached
     * version; otherwise, it will retrieve the uncached version from the database and then cache it before returning it.
     */
    protected DelegateTypeBo getKimDelegationImpl(String delegationId) {
        if (StringUtils.isBlank(delegationId)) {
            return null;
        }

        return getBusinessObjectService().findByPrimaryKey(DelegateTypeBo.class,
                Collections.singletonMap(KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegationId));
    }

    protected DelegationTypeService getDelegationTypeService(String delegationId) {
        DelegationTypeService service = null;
        DelegateTypeBo delegateBo = getKimDelegationImpl(delegationId);
        KimType delegationType = KimApiServiceLocator.getKimTypeInfoService().getKimType(delegateBo.getKimTypeId());
        if (delegationType != null) {
            KimTypeService tempService = KimFrameworkServiceLocator.getKimTypeService(delegationType);
            if (tempService != null && tempService instanceof DelegationTypeService) {
                service = (DelegationTypeService) tempService;
            } else {
                LOG.error("Service returned for type " + delegationType + "(" + delegationType.getName() + ") was not a DelegationTypeService.  Was a " + (tempService != null ? tempService.getClass() : "(null)"));
            }
        } else { // delegateBo has no type - default to role type if possible
            RoleTypeService roleTypeService = getRoleTypeService(delegateBo.getRoleId());
            if (roleTypeService != null && roleTypeService instanceof DelegationTypeService) {
                service = (DelegationTypeService) roleTypeService;
            }
        }
        return service;
    }

    protected Collection<RoleMembership> getNestedRoleMembers(Map<String, String> qualification, RoleMembership rm, Set<String> foundRoleTypeMembers) {
        // If this role has already been traversed, skip it
        if (foundRoleTypeMembers.contains(rm.getMemberId())) {
            return new ArrayList<RoleMembership>();  // return an empty list
        }
        foundRoleTypeMembers.add(rm.getMemberId());

        ArrayList<String> roleIdList = new ArrayList<String>(1);
        roleIdList.add(rm.getMemberId());

        // get the list of members from the nested role - ignore delegations on those sub-roles
        Collection<RoleMembership> currentNestedRoleMembers = getRoleMembers(roleIdList, qualification, false, foundRoleTypeMembers);

        // add the roles whose members matched to the list for delegateBo checks later
        Collection<RoleMembership> returnRoleMembers = new ArrayList<RoleMembership>();
        for (RoleMembership roleMembership : currentNestedRoleMembers) {
            RoleMembership.Builder rmBuilder = RoleMembership.Builder.create(roleMembership);

            // use the member ID of the parent role (needed for responsibility joining)
            rmBuilder.setRoleMemberId(rm.getRoleMemberId());
            // store the role ID, so we know where this member actually came from
            rmBuilder.setRoleId(rm.getRoleId());
            rmBuilder.setEmbeddedRoleId(rm.getMemberId());
            returnRoleMembers.add(rmBuilder.build());
        }
        return returnRoleMembers;
    }

    /**
     * Retrieves a KimDelegationMemberImpl object by its ID and the ID of the delegation it belongs to. If the delegation member exists in the cache,
     * this method will return the cached one; otherwise, it will retrieve the uncached version from the database and then cache it before returning it.
     */
    protected DelegateMemberBo getKimDelegationMemberImplByDelegationAndId(String delegationId, String delegationMemberId) {
        if (StringUtils.isBlank(delegationId) || StringUtils.isBlank(delegationMemberId)) {
            return null;
        }

        Map<String, String> searchCriteria = new HashMap<String, String>();
        searchCriteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegationId);
        searchCriteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_MEMBER_ID, delegationMemberId);
        List<DelegateMemberBo> memberList =
                (List<DelegateMemberBo>) getBusinessObjectService().findMatching(DelegateMemberBo.class, searchCriteria);
        if (memberList != null && !memberList.isEmpty()) {
            return memberList.get(0);
        }
        return null;
    }

    private List<RoleMemberBo> getStoredRoleMembersUsingExactMatchOnQualification(String principalId, List<String> groupIds, List<String> roleIds, Map<String, String> qualification) {
        List<String> copyRoleIds = new ArrayList<String>(roleIds);
        List<RoleMemberBo> roleMemberBoList = new ArrayList<RoleMemberBo>();

        for (String roleId : roleIds) {
            RoleTypeService roleTypeService = getRoleTypeService(roleId);
            if (roleTypeService != null) {
                List<String> attributesForExactMatch = roleTypeService.getQualifiersForExactMatch();
                if (CollectionUtils.isNotEmpty(attributesForExactMatch)) {
                    copyRoleIds.remove(roleId);
                    roleMemberBoList.addAll(getStoredRoleMembersForRoleIdsWithFilters(Collections.singletonList(roleId), principalId, groupIds, populateQualifiersForExactMatch(qualification, attributesForExactMatch)));
                }
            }
        }
        if (CollectionUtils.isNotEmpty(copyRoleIds)) {
            roleMemberBoList.addAll(getStoredRoleMembersForRoleIdsWithFilters(copyRoleIds, principalId, groupIds, null));
        }
        return roleMemberBoList;
    }

    private List<RoleMemberBo> getStoredRoleGroupsUsingExactMatchOnQualification(List<String> groupIds, Set<String> roleIds, Map<String, String> qualification) {
        List<String> copyRoleIds = new ArrayList<String>(roleIds);
        List<RoleMemberBo> roleMemberBos = new ArrayList<RoleMemberBo>();

        for (String roleId : roleIds) {
            RoleTypeService roleTypeService = getRoleTypeService(roleId);
            if (roleTypeService != null) {
                List<String> attributesForExactMatch = roleTypeService.getQualifiersForExactMatch();
                if (CollectionUtils.isNotEmpty(attributesForExactMatch)) {
                    copyRoleIds.remove(roleId);
                    roleMemberBos.addAll(getStoredRoleGroupsForGroupIdsAndRoleIds(Collections.singletonList(roleId), groupIds, populateQualifiersForExactMatch(qualification, attributesForExactMatch)));
                }
            }
        }
        if (CollectionUtils.isNotEmpty(copyRoleIds)) {
            roleMemberBos.addAll(getStoredRoleGroupsForGroupIdsAndRoleIds(copyRoleIds, groupIds, null));
        }
        return roleMemberBos;
    }

    private List<DelegateMember> getDelegateMembersForDelegation(DelegateTypeBo delegateBo) {
        if (delegateBo == null || delegateBo.getMembers() == null) {return null;}
        List<DelegateMember> delegateMembersReturnList = new ArrayList<DelegateMember>();
        for (DelegateMemberBo delegateMemberBo : delegateBo.getMembers()) {
            //FIXME: What is up with this!?!
            DelegateMember delegateMember = getDelegateCompleteInfo(delegateBo, delegateMemberBo);

            delegateMembersReturnList.add(DelegateMemberBo.to(delegateMemberBo));
        }
        return Collections.unmodifiableList(delegateMembersReturnList);
    }

    private DelegateMember getDelegateCompleteInfo(DelegateTypeBo delegateBo, DelegateMemberBo delegateMemberBo) {
        if (delegateBo == null || delegateMemberBo == null) {return null;}

        DelegateMember.Builder delegateMemberBuilder = DelegateMember.Builder.create(delegateMemberBo);
        delegateMemberBuilder.setTypeCode(delegateBo.getDelegationTypeCode());
        return delegateMemberBuilder.build();
    }

    @Override
    public void assignPrincipalToRole(String principalId, String namespaceCode, String roleName, Map<String, String> qualifier) {
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId is null");
        }

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode is null");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName is null");
        }

        if (qualifier == null) {
            throw new RiceIllegalArgumentException("qualifier is null");
        }

        // look up the role
        RoleBo role = getRoleBoByName(namespaceCode, roleName);
        role.refreshReferenceObject("members");

        // check that identical member does not already exist
    	if ( doAnyMemberRecordsMatchByExactQualifier(role, principalId, memberTypeToRoleDaoActionMap.get(Role.PRINCIPAL_MEMBER_TYPE), qualifier) || 
    			doAnyMemberRecordsMatch( role.getMembers(), principalId, Role.PRINCIPAL_MEMBER_TYPE, qualifier ) ) {
    		return;
    	}
        // create the new role member object
        RoleMemberBo newRoleMember = new RoleMemberBo();

        newRoleMember.setRoleId(role.getId());
        newRoleMember.setMemberId(principalId);
        newRoleMember.setMemberTypeCode(Role.PRINCIPAL_MEMBER_TYPE);

        // build role member attribute objects from the given Map<String, String>
        addMemberAttributeData(newRoleMember, qualifier, role.getKimTypeId());

        // add row to member table
        // When members are added to roles, clients must be notified.
        getResponsibilityInternalService().saveRoleMember(newRoleMember);
    }

    @Override
    public void assignGroupToRole(String groupId, String namespaceCode, String roleName, Map<String, String> qualifier) {
        if (StringUtils.isBlank(groupId)) {
            throw new RiceIllegalArgumentException("groupId is null");
        }

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode is null");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName is null");
        }

        if (qualifier == null) {
            throw new RiceIllegalArgumentException("qualifier is null");
        }

        // look up the role
        RoleBo role = getRoleBoByName(namespaceCode, roleName);
        // check that identical member does not already exist
    	if ( doAnyMemberRecordsMatchByExactQualifier(role, groupId, memberTypeToRoleDaoActionMap.get(Role.GROUP_MEMBER_TYPE), qualifier) || 
    			doAnyMemberRecordsMatch( role.getMembers(), groupId, Role.GROUP_MEMBER_TYPE, qualifier ) ) { 
    		return;
    	}
        // create the new role member object
        RoleMemberBo newRoleMember = new RoleMemberBo();
        newRoleMember.setRoleId(role.getId());
        newRoleMember.setMemberId(groupId);
        newRoleMember.setMemberTypeCode(Role.GROUP_MEMBER_TYPE);

        // build role member attribute objects from the given Map<String, String>
        addMemberAttributeData(newRoleMember, qualifier, role.getKimTypeId());

        // When members are added to roles, clients must be notified.
        getResponsibilityInternalService().saveRoleMember(newRoleMember);
    }

    @Override
    public void assignRoleToRole(String roleId, String namespaceCode, String roleName, Map<String, String> qualifier) {
        if (StringUtils.isBlank(roleId)) {
            throw new RiceIllegalArgumentException("roleId is null");
        }

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode is null");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName is null");
        }

        if (qualifier == null) {
            throw new RiceIllegalArgumentException("qualifier is null");
        }

        // look up the roleBo
        RoleBo roleBo = getRoleBoByName(namespaceCode, roleName);
        // check that identical member does not already exist
    	if ( doAnyMemberRecordsMatchByExactQualifier(roleBo, roleId, memberTypeToRoleDaoActionMap.get(Role.ROLE_MEMBER_TYPE), qualifier) || 
    			doAnyMemberRecordsMatch( roleBo.getMembers(), roleId, Role.ROLE_MEMBER_TYPE, qualifier ) ) {
    		return;
    	}
        // Check to make sure this doesn't create a circular membership
        if (!checkForCircularRoleMembership(roleId, roleBo)) {
            throw new IllegalArgumentException("Circular roleBo reference.");
        }
        // create the new roleBo member object
        RoleMemberBo newRoleMember = new RoleMemberBo();
        newRoleMember.setRoleId(roleBo.getId());
        newRoleMember.setMemberId(roleId);
        newRoleMember.setMemberTypeCode(Role.ROLE_MEMBER_TYPE);
        // build roleBo member attribute objects from the given Map<String, String>
        addMemberAttributeData(newRoleMember, qualifier, roleBo.getKimTypeId());

        // When members are added to roles, clients must be notified.
        getResponsibilityInternalService().saveRoleMember(newRoleMember);
    }

    @Override
    public RoleMember saveRoleMemberForRole(String roleMemberId, String memberId, String memberTypeCode, String roleId, Map<String, String> qualifications, DateTime activeFromDate, DateTime activeToDate) {
        if(StringUtils.isBlank(roleMemberId) && StringUtils.isBlank(memberId) && StringUtils.isBlank(roleId)){
    		throw new RiceIllegalArgumentException("Either Role member ID or a combination of member ID and roleBo ID must be passed in.");
    	}

        if (StringUtils.isBlank(memberTypeCode)) {
            throw new RiceIllegalArgumentException("memberTypeCode is null");
        }

        if (qualifications == null) {
            throw new RiceIllegalArgumentException("qualifications is null");
        }

    	RoleMemberBo origRoleMemberBo;
    	RoleBo roleBo;
    	// create the new roleBo member object
    	RoleMemberBo newRoleMember = new RoleMemberBo();
    	if(StringUtils.isEmpty(roleMemberId)){
	    	// look up the roleBo
	    	roleBo = getRoleBo(roleId);
	    	// check that identical member does not already exist
	    	origRoleMemberBo = matchingMemberRecord( roleBo.getMembers(), memberId, memberTypeCode, qualifications );
    	} else{
    		origRoleMemberBo = getRoleMemberBo(roleMemberId);
    		roleBo = getRoleBo(origRoleMemberBo.getRoleId());
    	}

    	if(origRoleMemberBo !=null){
    		newRoleMember.setRoleMemberId(origRoleMemberBo.getRoleMemberId());
    		newRoleMember.setVersionNumber(origRoleMemberBo.getVersionNumber());
    	}
    	newRoleMember.setRoleId(roleBo.getId());
    	newRoleMember.setMemberId( memberId );
    	newRoleMember.setMemberTypeCode( memberTypeCode );
		if (activeFromDate != null) {
			newRoleMember.setActiveFromDateValue(new java.sql.Timestamp(activeFromDate.getMillis()));
		}
		if (activeToDate != null) {
			newRoleMember.setActiveToDateValue(new java.sql.Timestamp(activeToDate.getMillis()));
		}
    	// build roleBo member attribute objects from the given Map<String, String>
    	addMemberAttributeData( newRoleMember, qualifications, roleBo.getKimTypeId() );

    	// When members are added to roles, clients must be notified.
    	getResponsibilityInternalService().saveRoleMember(newRoleMember);
    	deleteNullMemberAttributeData(newRoleMember.getAttributeDetails());
    	return findRoleMember(newRoleMember.getRoleMemberId());
    }

    @Override
    public void saveRoleRspActions(String roleResponsibilityActionId, String roleId, String roleResponsibilityId, String roleMemberId, String actionTypeCode, String actionPolicyCode, Integer priorityNumber, Boolean forceAction) {
        if (StringUtils.isBlank(roleResponsibilityActionId)) {
            throw new RiceIllegalArgumentException("roleResponsibilityActionId is null");
        }

        if (StringUtils.isBlank(roleId)) {
            throw new RiceIllegalArgumentException("roleId is null or blank");
        }

        if (StringUtils.isBlank(roleResponsibilityId)) {
            throw new RiceIllegalArgumentException("roleResponsibilityId is null or blank");
        }

        if (StringUtils.isBlank(roleMemberId)) {
            throw new RiceIllegalArgumentException("roleMemberId is null");
        }

        if (StringUtils.isBlank(actionTypeCode)) {
            throw new RiceIllegalArgumentException("actionTypeCode is null or blank");
        }

        if (StringUtils.isBlank(actionPolicyCode)) {
            throw new RiceIllegalArgumentException("actionPolicyCode is null or blank");
        }

        if (priorityNumber == null) {
            throw new RiceIllegalArgumentException("priorityNumber is null");
        }

        if (forceAction == null) {
            throw new RiceIllegalArgumentException("forceAction is null");
        }

        RoleResponsibilityActionBo newRoleRspAction = new RoleResponsibilityActionBo();
		newRoleRspAction.setActionPolicyCode(actionPolicyCode);
		newRoleRspAction.setActionTypeCode(actionTypeCode);
		newRoleRspAction.setPriorityNumber(priorityNumber);
		newRoleRspAction.setForceAction(forceAction.booleanValue());
		newRoleRspAction.setRoleMemberId(roleMemberId);
		newRoleRspAction.setRoleResponsibilityId(roleResponsibilityId);
		if(StringUtils.isEmpty(roleResponsibilityActionId)){
			//If there is an existing one
			Map<String, String> criteria = new HashMap<String, String>(1);
			criteria.put(KimConstants.PrimaryKeyConstants.ROLE_RESPONSIBILITY_ID, roleResponsibilityId);
			criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId);
			List<RoleResponsibilityActionBo> roleResponsibilityActionImpls = (List<RoleResponsibilityActionBo>)
				getBusinessObjectService().findMatching(RoleResponsibilityActionBo.class, criteria);
			if(roleResponsibilityActionImpls!=null && !roleResponsibilityActionImpls.isEmpty()){
				newRoleRspAction.setId(roleResponsibilityActionImpls.get(0).getId());
				newRoleRspAction.setVersionNumber(roleResponsibilityActionImpls.get(0).getVersionNumber());
			}
		} else{
			Map<String, String> criteria = new HashMap<String, String>(1);
			criteria.put(KimConstants.PrimaryKeyConstants.ROLE_RESPONSIBILITY_ACTION_ID, roleResponsibilityActionId);
			List<RoleResponsibilityActionBo> roleResponsibilityActionImpls = (List<RoleResponsibilityActionBo>)
				getBusinessObjectService().findMatching(RoleResponsibilityActionBo.class, criteria);
			if(CollectionUtils.isNotEmpty(roleResponsibilityActionImpls) && !roleResponsibilityActionImpls.isEmpty()){
				newRoleRspAction.setId(roleResponsibilityActionImpls.get(0).getId());
				newRoleRspAction.setVersionNumber(roleResponsibilityActionImpls.get(0).getVersionNumber());
			}
		}
		getBusinessObjectService().save(newRoleRspAction);
    }

    @Override
    public void saveDelegationMemberForRole(String delegationMemberId, String roleMemberId, String memberId, String memberTypeCode, String delegationTypeCode, String roleId, Map<String, String> qualifications, DateTime activeFromDate, DateTime activeToDate) {
        if(StringUtils.isBlank(delegationMemberId) && StringUtils.isBlank(memberId) && StringUtils.isBlank(roleId)){
    		throw new IllegalArgumentException("Either Delegation member ID or a combination of member ID and role ID must be passed in.");
    	}

        if (StringUtils.isBlank(memberTypeCode)) {
            throw new RiceIllegalArgumentException("memberTypeCode is null");
        }

        if (StringUtils.isBlank(delegationTypeCode)) {
            throw new RiceIllegalArgumentException("delegationTypeCode is null");
        }

        if (qualifications == null) {
            throw new RiceIllegalArgumentException("qualifications is null");
        }

    	// look up the role
    	RoleBo role = getRoleBo(roleId);
    	DelegateTypeBo delegation = getDelegationOfType(role.getId(), delegationTypeCode);
    	// create the new role member object
    	DelegateMemberBo newDelegationMember = new DelegateMemberBo();

    	DelegateMemberBo origDelegationMember;
    	if(StringUtils.isNotEmpty(delegationMemberId)){
    		origDelegationMember = getDelegateMemberBo(delegationMemberId);
    	} else{
    		List<DelegateMemberBo> origDelegationMembers =
                    this.getDelegationMemberBoListByMemberAndDelegationId(memberId, delegation.getDelegationId());
	    	origDelegationMember =
	    		(origDelegationMembers!=null && !origDelegationMembers.isEmpty()) ? origDelegationMembers.get(0) : null;
    	}
    	if(origDelegationMember!=null){
    		newDelegationMember.setDelegationMemberId(origDelegationMember.getDelegationMemberId());
    		newDelegationMember.setVersionNumber(origDelegationMember.getVersionNumber());
    	}
    	newDelegationMember.setMemberId(memberId);
    	newDelegationMember.setDelegationId(delegation.getDelegationId());
    	newDelegationMember.setRoleMemberId(roleMemberId);
    	newDelegationMember.setTypeCode(memberTypeCode);
		if (activeFromDate != null) {
			newDelegationMember.setActiveFromDateValue(new java.sql.Timestamp(activeFromDate.getMillis()));
		}
		if (activeToDate != null) {
			newDelegationMember.setActiveToDateValue(new java.sql.Timestamp(activeToDate.getMillis()));
		}

    	// build role member attribute objects from the given Map<String, String>
    	addDelegationMemberAttributeData( newDelegationMember, qualifications, role.getKimTypeId() );

    	List<DelegateMemberBo> delegationMembers = new ArrayList<DelegateMemberBo>();
    	delegationMembers.add(newDelegationMember);
    	delegation.setMembers(delegationMembers);

    	getBusinessObjectService().save(delegation);
    	for(DelegateMemberBo delegationMember: delegation.getMembers()){
    		deleteNullDelegationMemberAttributeData(delegationMember.getAttributes());
    	}
    }

    private void removeRoleMembers(List<RoleMemberBo> members) {
        if(CollectionUtils.isNotEmpty(members)) {
            for ( RoleMemberBo rm : members ) {
                getResponsibilityInternalService().removeRoleMember(rm);
            }
        }
    }
    
    private List<RoleMemberBo> getRoleMembersByDefaultStrategy(RoleBo role, String memberId, String memberTypeCode, Map<String, String> qualifier) {
        List<RoleMemberBo> rms = new ArrayList<RoleMemberBo>();
        role.refreshReferenceObject("members");
        for ( RoleMemberBo rm : role.getMembers() ) {
            if ( doesMemberMatch( rm, memberId, memberTypeCode, qualifier ) ) {
                // if found, remove
                rms.add(rm);
            }
        }
        return rms;
    }
    
    @Override
    public void removePrincipalFromRole(String principalId, String namespaceCode, String roleName, Map<String, String> qualifier) {
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId is null");
        }

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode is null");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName is null");
        }

        if (qualifier == null) {
            throw new RiceIllegalArgumentException("qualifier is null");
        }

        // look up the role
    	RoleBo role = getRoleBoByName(namespaceCode, roleName);
    	// pull all the principal members
    	// look for an exact qualifier match
        List<RoleMemberBo> rms = getRoleMembersByExactQualifierMatch(role, principalId, memberTypeToRoleDaoActionMap.get(Role.PRINCIPAL_MEMBER_TYPE), qualifier);
        if(CollectionUtils.isEmpty(rms)) {
            rms = getRoleMembersByDefaultStrategy(role, principalId, Role.PRINCIPAL_MEMBER_TYPE, qualifier);
        } 
        removeRoleMembers(rms);
    }
    
    @Override
    public void removeGroupFromRole(String groupId, String namespaceCode, String roleName, Map<String, String> qualifier) {
        if (StringUtils.isBlank(groupId)) {
            throw new RiceIllegalArgumentException("groupId is null");
        }

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode is null");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName is null");
        }

        if (qualifier == null) {
            throw new RiceIllegalArgumentException("qualifier is null");
        }

        // look up the roleBo
    	RoleBo roleBo = getRoleBoByName(namespaceCode, roleName);
    	// pull all the group roleBo members
    	// look for an exact qualifier match
        List<RoleMemberBo> rms = getRoleMembersByExactQualifierMatch(roleBo, groupId, memberTypeToRoleDaoActionMap.get(Role.GROUP_MEMBER_TYPE), qualifier);
        if(CollectionUtils.isEmpty(rms)) {
            rms = getRoleMembersByDefaultStrategy(roleBo, groupId, Role.GROUP_MEMBER_TYPE, qualifier);
        } 
        removeRoleMembers(rms);
    }   
    
    @Override
    public void removeRoleFromRole(String roleId, String namespaceCode, String roleName, Map<String, String> qualifier) {
        if (StringUtils.isBlank(roleId)) {
            throw new RiceIllegalArgumentException("roleId is null");
        }

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode is null");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName is null");
        }

        if (qualifier == null) {
            throw new RiceIllegalArgumentException("qualifier is null");
        }

        // look up the role
    	RoleBo role = getRoleBoByName(namespaceCode, roleName);
    	// pull all the group role members
    	// look for an exact qualifier match
        List<RoleMemberBo> rms = getRoleMembersByExactQualifierMatch(role, roleId, memberTypeToRoleDaoActionMap.get(Role.ROLE_MEMBER_TYPE), qualifier);
        if(CollectionUtils.isEmpty(rms)) {
            rms = getRoleMembersByDefaultStrategy(role, roleId, Role.ROLE_MEMBER_TYPE, qualifier);
        } 
        removeRoleMembers(rms);
    }    

    @Override
    public void saveRole(String roleId, String roleName, String roleDescription, boolean active, String kimTypeId, String namespaceCode) {
        if (StringUtils.isBlank(roleId)) {
            throw new RiceIllegalArgumentException("roleId is null");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName is null");
        }

        if (StringUtils.isBlank(roleDescription)) {
            throw new RiceIllegalArgumentException("roleDescription is null");
        }

        if (StringUtils.isBlank(kimTypeId)) {
            throw new RiceIllegalArgumentException("kimTypeId is null");
        }

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode is null");
        }

        // look for existing role
        RoleBo role = getBusinessObjectService().findBySinglePrimaryKey(RoleBo.class, roleId);
        if (role == null) {
            role = new RoleBo();
            role.setId(roleId);
        }

        role.setName(roleName);
        role.setDescription(roleDescription);
        role.setActive(active);
        role.setKimTypeId(kimTypeId);
        role.setNamespaceCode(namespaceCode);

        getBusinessObjectService().save(role);
    }

    @Override
    public void assignPermissionToRole(String permissionId, String roleId) {
        if (StringUtils.isBlank(permissionId)) {
            throw new RiceIllegalArgumentException("permissionId is null");
        }

        if (StringUtils.isBlank(roleId)) {
            throw new RiceIllegalArgumentException("roleId is null");
        }

        RolePermissionBo newRolePermission = new RolePermissionBo();

        Long nextSeq = KRADServiceLocator.getSequenceAccessorService().getNextAvailableSequenceNumber(KimConstants.SequenceNames.KRIM_ROLE_PERM_ID_S, RolePermissionBo.class);

        if (nextSeq == null) {
            LOG.error("Unable to get new role permission id from sequence " + KimConstants.SequenceNames.KRIM_ROLE_PERM_ID_S);
            throw new RuntimeException("Unable to get new role permission id from sequence " + KimConstants.SequenceNames.KRIM_ROLE_PERM_ID_S);
        }

        newRolePermission.setId(nextSeq.toString());
        newRolePermission.setRoleId(roleId);
        newRolePermission.setPermissionId(permissionId);
        newRolePermission.setActive(true);

        getBusinessObjectService().save(newRolePermission);
    }

    protected void addMemberAttributeData(RoleMemberBo roleMember, Map<String, String> qualifier, String kimTypeId) {
        List<RoleMemberAttributeDataBo> attributes = new ArrayList<RoleMemberAttributeDataBo>();
        for (Map.Entry<String, String> entry : qualifier.entrySet()) {
            RoleMemberAttributeDataBo roleMemberAttrBo = new RoleMemberAttributeDataBo();
            roleMemberAttrBo.setAttributeValue(entry.getValue());
            roleMemberAttrBo.setKimTypeId(kimTypeId);
            roleMemberAttrBo.setAssignedToId(roleMember.getRoleMemberId());
            // look up the attribute ID
            roleMemberAttrBo.setKimAttributeId(getKimAttributeId(entry.getKey()));

            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put(KimConstants.PrimaryKeyConstants.KIM_ATTRIBUTE_ID, roleMemberAttrBo.getKimAttributeId());
            criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMember.getRoleMemberId());
            List<RoleMemberAttributeDataBo> origRoleMemberAttributes =
                    (List<RoleMemberAttributeDataBo>) getBusinessObjectService().findMatching(RoleMemberAttributeDataBo.class, criteria);
            RoleMemberAttributeDataBo origRoleMemberAttribute =
                    (origRoleMemberAttributes != null && !origRoleMemberAttributes.isEmpty()) ? origRoleMemberAttributes.get(0) : null;
            if (origRoleMemberAttribute != null) {
                roleMemberAttrBo.setId(origRoleMemberAttribute.getId());
                roleMemberAttrBo.setVersionNumber(origRoleMemberAttribute.getVersionNumber());
            }
            attributes.add(roleMemberAttrBo);
        }
        roleMember.setAttributeDetails(attributes);
    }

    protected void addDelegationMemberAttributeData( DelegateMemberBo delegationMember, Map<String, String> qualifier, String kimTypeId ) {
		List<DelegateMemberAttributeDataBo> attributes = new ArrayList<DelegateMemberAttributeDataBo>();
		for (  Map.Entry<String, String> entry : qualifier.entrySet() ) {
			DelegateMemberAttributeDataBo delegateMemberAttrBo = new DelegateMemberAttributeDataBo();
			delegateMemberAttrBo.setAttributeValue(entry.getValue());
			delegateMemberAttrBo.setKimTypeId(kimTypeId);
			delegateMemberAttrBo.setAssignedToId(delegationMember.getDelegationMemberId());
			// look up the attribute ID
			delegateMemberAttrBo.setKimAttributeId(getKimAttributeId(entry.getKey()));
	    	Map<String, String> criteria = new HashMap<String, String>();
	    	criteria.put(KimConstants.PrimaryKeyConstants.KIM_ATTRIBUTE_ID, delegateMemberAttrBo.getKimAttributeId());
	    	criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_MEMBER_ID, delegationMember.getDelegationMemberId());
			List<DelegateMemberAttributeDataBo> origDelegationMemberAttributes =
	    		(List<DelegateMemberAttributeDataBo>)getBusinessObjectService().findMatching(DelegateMemberAttributeDataBo.class, criteria);
			DelegateMemberAttributeDataBo origDelegationMemberAttribute =
	    		(origDelegationMemberAttributes!=null && !origDelegationMemberAttributes.isEmpty()) ? origDelegationMemberAttributes.get(0) : null;
	    	if(origDelegationMemberAttribute!=null){
	    		delegateMemberAttrBo.setId(origDelegationMemberAttribute.getId());
	    		delegateMemberAttrBo.setVersionNumber(origDelegationMemberAttribute.getVersionNumber());
	    	}
			attributes.add( delegateMemberAttrBo );
		}
		delegationMember.setAttributes( attributes );
	}



     // --------------------
    // Persistence Methods
    // --------------------

	private void deleteNullMemberAttributeData(List<RoleMemberAttributeDataBo> attributes) {
		List<RoleMemberAttributeDataBo> attributesToDelete = new ArrayList<RoleMemberAttributeDataBo>();
		for(RoleMemberAttributeDataBo attribute: attributes){
			if(attribute.getAttributeValue()==null){
				attributesToDelete.add(attribute);
			}
		}
		getBusinessObjectService().delete(attributesToDelete);
	}

    private void deleteNullDelegationMemberAttributeData(List<DelegateMemberAttributeDataBo> attributes) {
        List<DelegateMemberAttributeDataBo> attributesToDelete = new ArrayList<DelegateMemberAttributeDataBo>();

		for(DelegateMemberAttributeDataBo attribute: attributes){
			if(attribute.getAttributeValue()==null){
				attributesToDelete.add(attribute);
			}
		}
		getBusinessObjectService().delete(attributesToDelete);
	}

}
