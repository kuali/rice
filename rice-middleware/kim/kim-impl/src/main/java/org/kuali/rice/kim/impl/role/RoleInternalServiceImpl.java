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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.group.GroupMember;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateTypeBo;
import org.kuali.rice.kim.impl.group.GroupMemberBo;

public class RoleInternalServiceImpl extends RoleServiceBase implements RoleInternalService{
    @Override
    public void principalInactivated(String principalId) {
        if (StringUtils.isBlank(principalId)) {
            throw new IllegalArgumentException("principalId is null or blank");
        }

        long oneDayInMillis = TimeUnit.DAYS.toMillis(1);
        Timestamp yesterday = new Timestamp(System.currentTimeMillis() - oneDayInMillis);

        inactivatePrincipalRoleMemberships(principalId, yesterday);
        inactivatePrincipalGroupMemberships(principalId, yesterday);
        inactivatePrincipalDelegations(principalId, yesterday);
        inactivateApplicationRoleMemberships(principalId, yesterday);
    }

    @Override
    public void roleInactivated(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            throw new IllegalArgumentException("roleId is null or blank");
        }

        long oneDayInMillis = TimeUnit.DAYS.toMillis(1);
        Timestamp yesterday = new Timestamp(System.currentTimeMillis() - oneDayInMillis);

        List<String> roleIds = new ArrayList<String>();
        roleIds.add(roleId);
        inactivateRoleMemberships(roleIds, yesterday);
        inactivateRoleDelegations(roleIds, yesterday);
        inactivateMembershipsForRoleAsMember(roleIds, yesterday);
    }

    private void inactivateRoleMemberships(List<String> roleIds, Timestamp yesterday) {
        List<RoleMemberBo> roleMemberBoList = getStoredRoleMembersForRoleIds(roleIds, null, null);
        for (RoleMemberBo roleMemberBo : roleMemberBoList) {
            roleMemberBo.setActiveToDateValue(yesterday);
            getDataObjectService().save(roleMemberBo);
        }
    }

    private void inactivateRoleDelegations(List<String> roleIds, Timestamp yesterday) {
        List<DelegateTypeBo> delegations = getStoredDelegationImplsForRoleIds(roleIds);
        for (DelegateTypeBo delegation : delegations) {
            delegation.setActive(false);
            for (DelegateMemberBo delegationMember : delegation.getMembers()) {
                delegationMember.setActiveToDateValue(yesterday);
                getDataObjectService().save(delegationMember);
            }
        }
    }

    private void inactivateMembershipsForRoleAsMember(List<String> roleIds, Timestamp yesterday) {
        List<RoleMemberBo> roleMemberBoList = getStoredRoleMembershipsForRoleIdsAsMembers(roleIds, null);
        for (RoleMemberBo roleMemberBo : roleMemberBoList) {
            roleMemberBo.setActiveToDateValue(yesterday);
            getDataObjectService().save(roleMemberBo);
        }
    }

    @Override
    public void groupInactivated(String groupId) {
        if (StringUtils.isBlank(groupId)) {
            throw new IllegalArgumentException("groupId is null or blank");
        }

        long oneDayInMillis = TimeUnit.DAYS.toMillis(1);
        Timestamp yesterday = new Timestamp(System.currentTimeMillis() - oneDayInMillis);

        List<String> groupIds = new ArrayList<String>();
        groupIds.add(groupId);
        inactivatePrincipalGroupMemberships(groupIds, yesterday);
        inactivateGroupRoleMemberships(groupIds, yesterday);
    }

    protected void inactivateApplicationRoleMemberships(String principalId, Timestamp yesterday) {

    }

    protected void inactivatePrincipalRoleMemberships(String principalId, Timestamp yesterday) {
        // go through all roles and post-date them
        List<RoleMemberBo> roleMembers = getStoredRolePrincipalsForPrincipalIdAndRoleIds(null, principalId, null);
        Set<String> roleIds = new HashSet<String>(roleMembers.size());
        for (RoleMemberBo roleMemberBo : roleMembers) {
            roleMemberBo.setActiveToDateValue(yesterday);
            roleIds.add(roleMemberBo.getRoleId()); // add to the set of IDs
            getDataObjectService().save(roleMemberBo);
        }
    }

    protected void inactivateGroupRoleMemberships(List<String> groupIds, Timestamp yesterday) {
        List<RoleMemberBo> roleMemberBosOfGroupType = getStoredRoleGroupsForGroupIdsAndRoleIds(null, groupIds, null);
        for (RoleMemberBo roleMemberBo : roleMemberBosOfGroupType) {
            roleMemberBo.setActiveToDateValue(yesterday);
            getDataObjectService().save(roleMemberBo);
        }
    }

    protected void inactivatePrincipalGroupMemberships(String principalId, Timestamp yesterday) {
        if ( StringUtils.isBlank(principalId) ) {
            return;
        }
        // get all the groups the person is in
        List<String> groupIds = getGroupService().getGroupIdsByPrincipalId(principalId);
        if (groupIds.isEmpty() ) {
            return;
        }
        // get all the member records for those groups
        Collection<GroupMember> groupMembers = getGroupService().getMembers(groupIds);
        List<GroupMember> groupPrincipals = new ArrayList<GroupMember>(groupMembers.size());
        for (GroupMember groupMembershipInfo : groupMembers) {
            if (MemberType.PRINCIPAL.equals(groupMembershipInfo.getType())
                    && StringUtils.equals(principalId, groupMembershipInfo.getMemberId())
                    && groupMembershipInfo.isActive(new DateTime())) {
                groupPrincipals.add(groupMembershipInfo);
                // FIXME: Is there a reason we are not calling the responsible group service?
                //getGroupService().removePrincipalFromGroup(groupMembershipInfo.getMemberId(), groupMembershipInfo.getGroupId());
            }
        }
        // FIXME: Is there a reason we are doing this directly and *not* calling the group service???
        for (GroupMember gm : groupPrincipals) {
            GroupMember.Builder builder = GroupMember.Builder.create(gm);
            builder.setActiveToDate(new DateTime(yesterday.getTime()));
            getDataObjectService().save(GroupMemberBo.from(builder.build()));
        }
    }

    protected void inactivatePrincipalGroupMemberships(List<String> groupIds, Timestamp yesterday) {
        if (groupIds == null || groupIds.isEmpty() ) {
            return;
        }
        Collection<GroupMember> groupMemberships = getGroupService().getMembers(groupIds);
        if ( groupMemberships.isEmpty() ) {
            return;
        }
        List<GroupMember> groupMembers = new ArrayList<GroupMember>();
        for (GroupMember groupMembershipInfo : groupMemberships) {
            if (MemberType.GROUP.equals(groupMembershipInfo.getType())
                    && groupMembershipInfo.isActive(new DateTime())) {
                groupMembers.add(groupMembershipInfo);
            }
        }
        // FIXME: Is there a reason we are doing this directly and *not* calling the group service???
        for (GroupMember groupMember : groupMembers) {
            GroupMember.Builder builder = GroupMember.Builder.create(groupMember);
            builder.setActiveToDate(new DateTime(yesterday.getTime()));
            getDataObjectService().save(GroupMemberBo.from(builder.build()));
        }
    }

    protected void inactivatePrincipalDelegations(String principalId, Timestamp yesterday) {
        List<DelegateMemberBo> delegationMembers = getStoredDelegationPrincipalsForPrincipalIdAndDelegationIds(null,
                principalId);
        for (DelegateMemberBo delegateMemberBo : delegationMembers) {
            delegateMemberBo.setActiveToDateValue(yesterday);
            getDataObjectService().save(delegateMemberBo);
        }
    }
}
