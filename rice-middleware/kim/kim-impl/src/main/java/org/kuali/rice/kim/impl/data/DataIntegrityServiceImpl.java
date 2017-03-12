/**
 * Copyright 2005-2016 The Kuali Foundation
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
package org.kuali.rice.kim.impl.data;

import org.kuali.rice.krad.data.platform.MaxValueIncrementerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * An implementation of DataIntegrityService which repairs two common data issues with delegations that occurred
 * because of bad code in KIM.
 *
 * The first issue is one where duplicate delegations for a given role, delegation type, and KIM type exist. This can
 * cause issues with the role document when editing delegations.
 *
 * The second issue is one where delegation members point to a role member for a role that doesn't match the role of
 * their delegation.
 *
 * @author Eric Westfall
 */
public class DataIntegrityServiceImpl implements DataIntegrityService, InitializingBean {

    private static final String DUPLICATE_DELEGATIONS = "select role_id, dlgn_typ_cd, kim_typ_id, count(*) cnt from krim_dlgn_t where actv_ind = 'Y' group by role_id, dlgn_typ_cd, kim_typ_id having cnt > 1";
    private static final String BAD_DELEGATION_MEMBERS = "select m.dlgn_mbr_id, m.role_mbr_id, rm.role_id, r.kim_typ_id, d.dlgn_id, d.role_id, d.dlgn_typ_cd from krim_dlgn_t d, krim_dlgn_mbr_t m, krim_role_mbr_t rm, krim_role_t r where d.actv_ind = 'Y' and d.dlgn_id=m.dlgn_id and m.role_mbr_id=rm.role_mbr_id and rm.role_id=r.role_id and d.role_id != rm.role_id";

    private static final String DUPLICATE_DELEGATION_IDS = "select dlgn_id from krim_dlgn_t where role_id = ? and dlgn_typ_cd = ? and kim_typ_id = ? and actv_ind = 'Y'";
    private static final String FIX_DUPLICATE_DELEGATION_ID = "update krim_dlgn_mbr_t set dlgn_id = ? where dlgn_id = ?";
    private static final String DELETE_DUPLICATE_DELEGATION = "delete from krim_dlgn_t where dlgn_id = ?";

    private static final String FIND_TARGET_DELEGATION = "select dlgn_id from krim_dlgn_t where role_id = ? and dlgn_typ_cd = ? and actv_ind = 'Y'";
    private static final String CREATE_DELEGATION = "insert into krim_dlgn_t (dlgn_id, obj_id, role_id, kim_typ_id, dlgn_typ_cd, ver_nbr, actv_ind) values (?, ?, ?, ?, ?, 1, 'Y')";
    private static final String FIX_BAD_DELEGATION_MEMBER = "update krim_dlgn_mbr_t set dlgn_id = ? where dlgn_mbr_id = ?";

    private DataSource dataSource;
    private JdbcTemplate template;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.template = new JdbcTemplate(dataSource);
    }

    @Override
    public List<String> checkIntegrity() {
        List<String> messages = new ArrayList<>();
        messages.addAll(reportDuplicateDelegations(findDuplicateDelegations()));
        messages.addAll(reportBadDelegationMembers(findBadDelegationMembers()));
        return messages;
    }

    private List<String> reportDuplicateDelegations(List<DuplicateRoleDelegation> duplicateRoleDelegations) {
        List<String> reports = new ArrayList<>();
        for (DuplicateRoleDelegation duplicateRoleDelegation : duplicateRoleDelegations) {
            reports.add(duplicateRoleDelegation.report());
        }
        return reports;
    }

    private List<String> reportBadDelegationMembers(List<BadDelegationMember> badDelegationMembers) {
        List<String> reports = new ArrayList<>();
        for (BadDelegationMember badDelegationMember : badDelegationMembers) {
            reports.add(badDelegationMember.report());
        }
        return reports;
    }

    @Override
    public List<String> repair() {
        List<String> messages = new ArrayList<>();
        messages.addAll(repairDuplicateDelegations());
        messages.addAll(repairBadDelegationMembers());
        return messages;
    }

    private List<String> repairDuplicateDelegations() {
        List<String> messages = new ArrayList<>();
        List<DuplicateRoleDelegation> duplicateRoleDelegations = findDuplicateDelegations();
        for (DuplicateRoleDelegation duplicateRoleDelegation : duplicateRoleDelegations) {
            messages.add(repairDuplicateDelegation(duplicateRoleDelegation));
        }
        return messages;
    }

    private String repairDuplicateDelegation(DuplicateRoleDelegation duplicateRoleDelegation) {

        // first let's find all of the duplicate delegation ids
        List<String> delegationIds = template.query(DUPLICATE_DELEGATION_IDS,
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getString(1);
                    }
                },
                duplicateRoleDelegation.roleId,
                duplicateRoleDelegation.delegationTypeCode,
                duplicateRoleDelegation.kimTypeId);
        // we'll keep the first delegation and repoint all members to it instead, then delete the remaining
        // duplicate delegations
        String delegationIdToKeep = delegationIds.remove(0);

        for (String delegationId : delegationIds) {
            template.update(FIX_DUPLICATE_DELEGATION_ID, delegationIdToKeep, delegationId);
            template.update(DELETE_DUPLICATE_DELEGATION, delegationId);
        }

        return reportRepairDuplicateDelegation(duplicateRoleDelegation, delegationIdToKeep, delegationIds);
    }

    private String reportRepairDuplicateDelegation(DuplicateRoleDelegation duplicateRoleDelegation,
                                                   String delegationIdToKeep, List<String> duplicateDelegationIds) {
        StringBuilder message = new StringBuilder();
        message.append("Repaired duplicate delegations with roleId = ").append(duplicateRoleDelegation.roleId)
                .append(", delegationTypeCode = ").append(duplicateRoleDelegation.delegationTypeCode)
                .append(", and kimTypeId = ").append(duplicateRoleDelegation.kimTypeId)
                .append(". Retained delegation with id ").append(delegationIdToKeep)
                .append(". Deleted the following delegations and repointed their members to delegation id ")
                .append(delegationIdToKeep).append(": [ ");
        for (String duplicateDelegationId : duplicateDelegationIds) {
            message.append(duplicateDelegationId).append(", ");
        }
        message.delete(message.length() - 2, message.length());
        message.append(" ]");
        return message.toString();
    }

    private List<String> repairBadDelegationMembers() {
        List<String> messages = new ArrayList<>();
        List<BadDelegationMember> badDelegationMembers = findBadDelegationMembers();
        for (BadDelegationMember badDelegationMember : badDelegationMembers) {
            messages.add(repairBadDelegationMember(badDelegationMember));
        }
        return messages;
    }

    private String repairBadDelegationMember(BadDelegationMember badDelegationMember) {
        // first attempt to find an existing delegation for the proper role id + delegation type code
        List<String> delegationIds = template.query(FIND_TARGET_DELEGATION,
                new RowMapper<String>() {
                    @Override
                    public String mapRow(ResultSet resultSet, int i) throws SQLException {
                        return resultSet.getString(1);
                    }
                },
                badDelegationMember.roleMemberRoleId,
                badDelegationMember.delegationTypeCode);

        // if delegationIds is empty, then we will need to manufacture a delegation, otherwise there should only be one
        // target delegation id since we just previously ran the repair to get rid of duplicate delegations
        String targetDelegationId;
        boolean newDelegationCreated = false;
        if (delegationIds.isEmpty()) {
            targetDelegationId = getNextDelegationId();
            String objectId = UUID.randomUUID().toString();
            template.update(CREATE_DELEGATION, targetDelegationId, objectId, badDelegationMember.roleMemberRoleId,
                    badDelegationMember.roleMemberRoleKimTypeId, badDelegationMember.delegationTypeCode);
            newDelegationCreated = true;
        } else {
            targetDelegationId = delegationIds.get(0);
        }
        // now that we have the target delegation id, let's update our delegation member and point it to the proper delegation
        template.update(FIX_BAD_DELEGATION_MEMBER, targetDelegationId, badDelegationMember.delegationMemberId);
        return reportRepairBadDelegationMember(badDelegationMember, targetDelegationId, newDelegationCreated);
    }

    private String reportRepairBadDelegationMember(BadDelegationMember badDelegationMember,
                                                   String targetDelegationId, boolean newDelegationCreated) {
        StringBuilder message = new StringBuilder();
        message.append("Repaired bad delegation member ").append(badDelegationMember.toString());
        if (newDelegationCreated) {
            message.append(" New delegation created with id ").append(targetDelegationId)
                    .append(" since there was no existing delegation that matched for the proper role id and delegation type.");
        } else {
            message.append(" Reassigned the delegation member to an existing delegation with id ")
                    .append(targetDelegationId).append(" because it matched the proper role id and delegation type.");
        }
        return message.toString();
    }

    private String getNextDelegationId() {
        DataFieldMaxValueIncrementer incrementer = MaxValueIncrementerFactory.getIncrementer(dataSource, "KRIM_DLGN_ID_S");
        return incrementer.nextStringValue();
    }


    private List<DuplicateRoleDelegation> findDuplicateDelegations() {
        return template.query(DUPLICATE_DELEGATIONS, new RowMapper<DuplicateRoleDelegation>() {
            @Override
            public DuplicateRoleDelegation mapRow(ResultSet resultSet, int i) throws SQLException {
                return new DuplicateRoleDelegation(resultSet.getString(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getInt(4));
            }
        });
    }

    private List<BadDelegationMember> findBadDelegationMembers() {
        return template.query(BAD_DELEGATION_MEMBERS, new RowMapper<BadDelegationMember>() {
            @Override
            public BadDelegationMember mapRow(ResultSet resultSet, int i) throws SQLException {
                return new BadDelegationMember(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3),
                        resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getString(7));
            }
        });
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    class DuplicateRoleDelegation {

        final String roleId;
        final String delegationTypeCode;
        final String kimTypeId;
        final int numMatching;

        DuplicateRoleDelegation(String roleId, String delegationTypeCode, String kimTypeId, int numMatching) {
            this.roleId = roleId;
            this.delegationTypeCode = delegationTypeCode;
            this.kimTypeId = kimTypeId;
            this.numMatching = numMatching;
        }

        String report() {
            return "Found duplicate role delegation " + toString();
        }

        public String toString() {
            return String.format("[roleId = %s, delegationTypeCode = %s, kimTypeId = %s, num of matching delegations = %d]",
                    roleId, delegationTypeCode, kimTypeId, numMatching);
        }


    }

    class BadDelegationMember {

        final String delegationMemberId;
        final String roleMemberId;
        final String roleMemberRoleId;
        final String roleMemberRoleKimTypeId;
        final String delegationId;
        final String delegationRoleId;
        final String delegationTypeCode;

        BadDelegationMember(String delegationMemberId, String roleMemberId, String roleMemberRoleId,
                            String roleMemberRoleKimTypeId, String delegationId, String delegationRoleId,
                            String delegationTypeCode) {
            this.delegationMemberId = delegationMemberId;
            this.roleMemberId = roleMemberId;
            this.roleMemberRoleId = roleMemberRoleId;
            this.roleMemberRoleKimTypeId = roleMemberRoleKimTypeId;
            this.delegationId = delegationId;
            this.delegationRoleId = delegationRoleId;
            this.delegationTypeCode = delegationTypeCode;
        }

        String report() {
            return "Found bad delegation member " + toString();
        }

        public String toString() {
            return String.format("[delegationMemberId = %s, roleMemberId = %s, " +
                            "roleMemberRoleId = %s, roleMemberRoleKimTypeId = %s, delegationId = %s, " +
                            "delegationRoleId = %s, delegationTypeCode = %s]",
                    delegationMemberId, roleMemberId, roleMemberRoleId, roleMemberRoleKimTypeId, delegationId,
                    delegationRoleId, delegationTypeCode);
        }

    }
}
