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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.util.Truth;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kim.api.common.attribute.KimAttribute;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberBo;
import org.kuali.rice.kim.impl.common.delegate.DelegateTypeBo;
import org.kuali.rice.kim.impl.type.KimTypeBo;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

public class RoleDaoOjb extends PlatformAwareDaoBaseOjb implements RoleDao {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = new TransactionAwareDataSourceProxy(dataSource);
    }

//    /**
//     * Adds SubCriteria to the Query Criteria using the role qualification passed in
//     *
//     * @param c             The Query Criteria object to be used
//     * @param qualification The role qualification
//     */
//    private void addSubCriteriaBasedOnRoleQualification(Criteria c, Map<String, String> qualification) {
//        if (qualification != null && CollectionUtils.isNotEmpty(qualification.keySet())) {
//            for (Map.Entry<String, String> qualifier : qualification.entrySet()) {
//                Criteria subCrit = new Criteria();
//                if (StringUtils.isNotEmpty(qualifier.getValue())) {
//                    String value = (qualifier.getValue()).replace('*', '%');
//                    subCrit.addLike("attributeValue", value);
//                    subCrit.addEqualTo("kimAttributeId", qualifier.getKey());
//                    subCrit.addEqualToField("assignedToId", Criteria.PARENT_QUERY_PREFIX + "id");
//                    ReportQueryByCriteria subQuery = QueryFactory.newReportQuery(RoleMemberAttributeDataBo.class, subCrit);
//                    c.addExists(subQuery);
//                }
//            }
//        }
//    }

    @SuppressWarnings("unchecked")
    public List<RoleMemberBo> getRolePrincipalsForPrincipalIdAndRoleIds(Collection<String> roleIds, String principalId, Map<String, String> qualification) {

        Criteria c = new Criteria();

        if (CollectionUtils.isNotEmpty(roleIds)) {
            if (roleIds.size() == 1) {
                c.addEqualTo(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds.iterator().next());
            } else {
            	c.addIn(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds);
            }
        }
        if (principalId != null) {
            c.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_ID, principalId);
        }
        c.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, MemberType.PRINCIPAL.getCode());
//        addSubCriteriaBasedOnRoleQualification(c, qualification);

        Query query = QueryFactory.newQuery(RoleMemberBo.class, c);
        Collection<RoleMemberBo> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        ArrayList<RoleMemberBo> results = new ArrayList<RoleMemberBo>(coll.size());
        for (RoleMemberBo rm : coll) {
            if (rm.isActive(new Timestamp(System.currentTimeMillis()))) {
                results.add(rm);
            }
        }
        return results;
    }


    @SuppressWarnings("unchecked")
    public List<RoleMemberBo> getRoleGroupsForGroupIdsAndRoleIds(Collection<String> roleIds, Collection<String> groupIds, Map<String, String> qualification) {
        Criteria c = new Criteria();
        if (roleIds != null && !roleIds.isEmpty()) {
            c.addIn(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds);
        }
        if (groupIds != null && !groupIds.isEmpty()) {
            c.addIn(KIMPropertyConstants.RoleMember.MEMBER_ID, groupIds);
        }
        c.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, MemberType.GROUP.getCode());
//        addSubCriteriaBasedOnRoleQualification(c, qualification);

        Query query = QueryFactory.newQuery(RoleMemberBo.class, c);
        Collection<RoleMemberBo> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        ArrayList<RoleMemberBo> results = new ArrayList<RoleMemberBo>(coll.size());
        for (RoleMemberBo rm : coll) {
            if (rm.isActive(new Timestamp(System.currentTimeMillis()))) {
                results.add(rm);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public Map<String, DelegateTypeBo> getDelegationImplMapFromRoleIds(Collection<String> roleIds) {
        Map<String, DelegateTypeBo> results = new HashMap<String, DelegateTypeBo>();
        if (CollectionUtils.isNotEmpty(roleIds)) {
            Criteria c = new Criteria();
            c.addIn(KIMPropertyConstants.Delegation.ROLE_ID, roleIds);
            c.addEqualTo(KIMPropertyConstants.Delegation.ACTIVE, Boolean.TRUE);
            Query query = QueryFactory.newQuery(DelegateTypeBo.class, c);
            Collection<DelegateTypeBo> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
            for (DelegateTypeBo delegateBo : coll) {
                results.put(delegateBo.getDelegationId(), delegateBo);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public List<DelegateMemberBo> getDelegationPrincipalsForPrincipalIdAndDelegationIds(
            Collection<String> delegationIds, String principalId) {
        Criteria c = new Criteria();

        if (principalId != null) {
            c.addEqualTo(KIMPropertyConstants.DelegationMember.MEMBER_ID, principalId);
        }
        c.addEqualTo(KIMPropertyConstants.DelegationMember.MEMBER_TYPE_CODE, MemberType.PRINCIPAL.getCode());
        if (delegationIds != null && !delegationIds.isEmpty()) {
            c.addIn(KIMPropertyConstants.DelegationMember.DELEGATION_ID, delegationIds);
        }
        Query query = QueryFactory.newQuery(DelegateMemberBo.class, c);
        Collection<DelegateMemberBo> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        ArrayList<DelegateMemberBo> results = new ArrayList<DelegateMemberBo>(coll.size());
        for (DelegateMemberBo rm : coll) {
            if (rm.isActive(new Timestamp(System.currentTimeMillis()))) {
                results.add(rm);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public List<DelegateMemberBo> getDelegationGroupsForGroupIdsAndDelegationIds(Collection<String> delegationIds,
            List<String> groupIds) {
        Criteria c = new Criteria();
        if (delegationIds != null && !delegationIds.isEmpty()) {
            c.addIn(KIMPropertyConstants.DelegationMember.DELEGATION_ID, delegationIds);
        }
        if (groupIds != null && !groupIds.isEmpty()) {
            c.addIn(KIMPropertyConstants.DelegationMember.MEMBER_ID, groupIds);
        }
        c.addEqualTo(KIMPropertyConstants.DelegationMember.MEMBER_TYPE_CODE, MemberType.GROUP.getCode());
        Query query = QueryFactory.newQuery(DelegateMemberBo.class, c);
        Collection<DelegateMemberBo> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        ArrayList<DelegateMemberBo> results = new ArrayList<DelegateMemberBo>(coll.size());
        for (DelegateMemberBo rm : coll) {
            if (rm.isActive(new Timestamp(System.currentTimeMillis()))) {
                results.add(rm);
            }
        }
        return results;
    }

    public List<RoleMemberBo> getRoleMembersForRoleIds(Collection<String> roleIds, String memberTypeCode,
            Map<String, String> qualification) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        final List<String> roleIDs = new ArrayList<String>(roleIds);
        final String memberTypeCd = memberTypeCode;
        final Map<String, String> qual = qualification;
        final List<RoleMemberBo> roleMemberBos = new ArrayList<RoleMemberBo>();
        List<RoleMemberBo> results = template.execute(new PreparedStatementCreator() {

                    /*
                     SAMPLE QUERY

                    SELECT A0.ROLE_MBR_ID AS ROLE_MBR_ID,A0.ROLE_ID AS ROLE_ID,A0.MBR_ID AS MBR_ID,A0.MBR_TYP_CD AS MBR_TYP_CD,A0.VER_NBR AS ROLE_MBR_VER_NBR,A0.OBJ_ID AS ROLE_MBR_OBJ_ID,A0.ACTV_FRM_DT AS ROLE_MBR_ACTV_FRM_DT ,A0.ACTV_TO_DT AS ROLE_MBR_ACTV_TO_DT,
                    BO.KIM_TYP_ID AS KIM_TYP_ID, BO.KIM_ATTR_DEFN_ID AS KIM_ATTR_DEFN_ID, BO.ATTR_VAL AS ATTR_VAL, BO.ATTR_DATA_ID AS ATTR_DATA_ID, BO.OBJ_ID AS ATTR_DATA_OBJ_ID, BO.VER_NBR AS ATTR_DATA_VER_NBR,
                    CO.OBJ_ID AS ATTR_DEFN_OBJ_ID, CO.VER_NBR as ATTR_DEFN_VER_NBR, CO.NM AS ATTR_NAME, CO.LBL as ATTR_DEFN_LBL, CO.ACTV_IND as ATTR_DEFN_ACTV_IND, CO.NMSPC_CD AS ATTR_DEFN_NMSPC_CD, CO.CMPNT_NM AS ATTR_DEFN_CMPNT_NM
                    FROM KRIM_ROLE_MBR_T A0 JOIN KRIM_ROLE_MBR_ATTR_DATA_T BO ON A0.ROLE_MBR_ID = BO.ROLE_MBR_ID  JOIN KRIM_ATTR_DEFN_T CO ON BO.KIM_ATTR_DEFN_ID = CO.KIM_ATTR_DEFN_ID
                    WHERE A0.ROLE_ID in ('100000')

                    UNION ALL

                    SELECT D0.ROLE_MBR_ID AS ROLE_MBR_ID,D0.ROLE_ID AS ROLE_ID,D0.MBR_ID AS MBR_ID,D0.MBR_TYP_CD AS MBR_TYP_CD,D0.VER_NBR AS ROLE_MBR_VER_NBR,D0.OBJ_ID AS ROLE_MBR_OBJ_ID,D0.ACTV_FRM_DT AS ROLE_MBR_ACTV_FRM_DT ,D0.ACTV_TO_DT AS ROLE_MBR_ACTV_TO_DT,
                    '' AS KIM_TYP_ID, '' AS KIM_ATTR_DEFN_ID, '' AS ATTR_VAL, '' AS ATTR_DATA_ID, '' AS ATTR_DATA_OBJ_ID, NULL AS ATTR_DATA_VER_NBR,
                    '' AS ATTR_DEFN_OBJ_ID, NULL as ATTR_DEFN_VER_NBR, '' AS ATTR_NAME, '' as ATTR_DEFN_LBL, '' as ATTR_DEFN_ACTV_IND, '' AS ATTR_DEFN_NMSPC_CD, '' AS ATTR_DEFN_CMPNT_NM
                    FROM KRIM_ROLE_MBR_T D0
                    WHERE D0.ROLE_MBR_ID NOT IN (SELECT DISTINCT (E0.ROLE_MBR_ID) FROM KRIM_ROLE_MBR_ATTR_DATA_T E0)
                    AND D0.ROLE_ID IN ('100000')
                    */

                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        /*
                         The query returns multiple lines for each role by joining a role with each of its members. This allows us to get all the role member
                         and role data in a single query (even though we are duplicating the role information across the role members). The cost of this
                         comes out to be cheaper than firing indiviudual queries for each role in cases where there are over 500 roles
                        */
                        StringBuffer sql1 = new StringBuffer("SELECT "
                                + " A0.ROLE_MBR_ID AS ROLE_MBR_ID,A0.ROLE_ID AS ROLE_ID,A0.MBR_ID AS MBR_ID,A0.MBR_TYP_CD AS MBR_TYP_CD,A0.VER_NBR AS ROLE_MBR_VER_NBR,A0.OBJ_ID AS ROLE_MBR_OBJ_ID,A0.ACTV_FRM_DT AS ROLE_MBR_ACTV_FRM_DT ,A0.ACTV_TO_DT AS ROLE_MBR_ACTV_TO_DT, "
                                + " BO.KIM_TYP_ID AS KIM_TYP_ID, BO.KIM_ATTR_DEFN_ID AS KIM_ATTR_DEFN_ID, BO.ATTR_VAL AS ATTR_VAL, BO.ATTR_DATA_ID AS ATTR_DATA_ID, BO.OBJ_ID AS ATTR_DATA_OBJ_ID, BO.VER_NBR AS ATTR_DATA_VER_NBR,  "
                                + " C0.KIM_ATTR_DEFN_ID AS KIM_ATTR_DEFN_ID, C0.OBJ_ID AS ATTR_DEFN_OBJ_ID, C0.VER_NBR as ATTR_DEFN_VER_NBR, C0.NM AS ATTR_NAME, C0.LBL as ATTR_DEFN_LBL, C0.ACTV_IND as ATTR_DEFN_ACTV_IND, C0.NMSPC_CD AS ATTR_DEFN_NMSPC_CD, C0.CMPNT_NM AS ATTR_DEFN_CMPNT_NM "
                                + " FROM KRIM_ROLE_MBR_T A0 JOIN KRIM_ROLE_MBR_ATTR_DATA_T BO ON A0.ROLE_MBR_ID = BO.ROLE_MBR_ID "
                                + " JOIN KRIM_ATTR_DEFN_T C0 ON BO.KIM_ATTR_DEFN_ID = C0.KIM_ATTR_DEFN_ID  ");

                        StringBuffer sql2 = new StringBuffer("SELECT"
                                + " D0.ROLE_MBR_ID AS ROLE_MBR_ID,D0.ROLE_ID AS ROLE_ID,D0.MBR_ID AS MBR_ID,D0.MBR_TYP_CD AS MBR_TYP_CD,D0.VER_NBR AS ROLE_MBR_VER_NBR,D0.OBJ_ID AS ROLE_MBR_OBJ_ID,D0.ACTV_FRM_DT AS ROLE_MBR_ACTV_FRM_DT ,D0.ACTV_TO_DT AS ROLE_MBR_ACTV_TO_DT, "
                                + " '' AS KIM_TYP_ID, '' AS KIM_ATTR_DEFN_ID, '' AS ATTR_VAL, '' AS ATTR_DATA_ID, '' AS ATTR_DATA_OBJ_ID, NULL AS ATTR_DATA_VER_NBR,"
                                + " '' AS KIM_ATTR_DEFN_ID,'' AS ATTR_DEFN_OBJ_ID, NULL as ATTR_DEFN_VER_NBR, '' AS ATTR_NAME, '' as ATTR_DEFN_LBL, '' as ATTR_DEFN_ACTV_IND, '' AS ATTR_DEFN_NMSPC_CD, '' AS ATTR_DEFN_CMPNT_NM "
                                + " FROM KRIM_ROLE_MBR_T D0 "
                                + " WHERE D0.ROLE_MBR_ID NOT IN (SELECT DISTINCT (E0.ROLE_MBR_ID) FROM KRIM_ROLE_MBR_ATTR_DATA_T E0)");

                        StringBuffer criteria = new StringBuffer();

                        List<String> params1 = new ArrayList<String>();
                        List<String> params2 = new ArrayList<String>();

                        if (roleIDs != null && !roleIDs.isEmpty()) {
                            criteria.append("A0.ROLE_ID IN (");

                            for (String roleId : roleIDs) {
                                criteria.append("?,");
                                params1.add(roleId);
                                params2.add(roleId);
                            }
                            criteria.deleteCharAt(criteria.length() - 1);
                            criteria.append(")");
                        }

                        if (memberTypeCd != null) {
                            if (criteria.length() > 0) {
                                criteria.append(" AND ");
                            }

                            criteria.append("A0.MBR_TYP_CD = ?");
                            params1.add(memberTypeCd);
                            params2.add(memberTypeCd);
                        }

                        // Assuming that at least a role id or role member type code is specified
                        if (criteria.length() > 0) {
                            sql1.append(" WHERE ");
                            sql2.append(" AND ");
                            sql1.append(criteria);
                            sql2.append(criteria.toString().replaceAll("A0", "D0"));
                        }

                        if (qual != null && CollectionUtils.isNotEmpty(qual.keySet())) {

                            // If Qualifiers present then sql2 should not be returning any result as it finds
                            // rolemembers with now attributes
                            sql2 = new StringBuffer();

                            if (criteria.length() > 0) {
                                sql1.append(" AND ");
                            } else {
                                sql1.append(" WHERE ");
                            }

                            sql1.append(" EXISTS (SELECT B1.ROLE_MBR_ID FROM KRIM_ROLE_MBR_ATTR_DATA_T B1 WHERE (");
                            for (Map.Entry<String, String> qualifier : qual.entrySet()) {
                                if (StringUtils.isNotEmpty(qualifier.getValue())) {
                                    String value = (qualifier.getValue()).replace('*', '%');
                                    sql1.append(" (B1.ATTR_VAL LIKE ? AND B1.KIM_ATTR_DEFN_ID = ? ) ");
                                    params1.add(value);
                                    params1.add(qualifier.getKey());
                                }
                                sql1.append("OR");
                            }
                            sql1.delete(sql1.length() - 2, sql1.length());
                            sql1.append(") AND B1.ROLE_MBR_ID = A0.ROLE_MBR_ID )");

                        }

                        StringBuffer sql = new StringBuffer(sql1.toString());

                        if (sql2.length() > 0) {
                            sql.append(" UNION ALL ");
                            sql.append(sql2.toString());
                        }

                        sql.append(" ORDER BY ROLE_MBR_ID ");

                        PreparedStatement statement = connection.prepareStatement(sql.toString());
                        int i = 1;
                        for (String param : params1) {
                            statement.setString(i, param);
                            i++;
                        }

                        if (sql2.length() > 0) {
                            for (String param : params2) {
                                statement.setString(i, param);
                                i++;
                            }
                        }

                        return statement;
                    }
                }, new PreparedStatementCallback<List<RoleMemberBo>>() {
            public List<RoleMemberBo> doInPreparedStatement(
                    PreparedStatement statement) throws SQLException, DataAccessException {
                ResultSet rs = statement.executeQuery();
                try {
                    RoleMemberBo lastRoleMember = null;
                    while (rs.next()) {
                        boolean processRolemember = true;

                        String roleId = rs.getString("ROLE_ID");
                        String id = rs.getString("ROLE_MBR_ID");
                        String memberId = rs.getString("MBR_ID");

                        MemberType memberType = MemberType.fromCode(rs.getString("MBR_TYP_CD"));
                        DateTime activeFromDate = rs.getDate("ROLE_MBR_ACTV_FRM_DT") == null ? null: new DateTime(rs.getDate("ROLE_MBR_ACTV_FRM_DT"));
                        DateTime activeToDate =   rs.getDate("ROLE_MBR_ACTV_TO_DT") == null ? null: new DateTime(rs.getDate("ROLE_MBR_ACTV_TO_DT"));

                        // Since we are joining role members and attributes we would have multiple role member rows
                        // but one row per attribute so check if its the first time we are seeing the role member
                        if (lastRoleMember == null || !id.equals(lastRoleMember.getId())) {
                            RoleMember roleMember = RoleMember.Builder.create(roleId, id, memberId, memberType,
                                    activeFromDate, activeToDate, new HashMap<String, String>(), "", "").build();
                            Long roleVersionNbr = rs.getLong("ROLE_MBR_VER_NBR");
                            String roleObjId = rs.getString("ROLE_MBR_OBJ_ID");

                            RoleMemberBo roleMemberBo = RoleMemberBo.from(roleMember);
                            roleMemberBo.setVersionNumber(roleVersionNbr);
                            roleMemberBo.setObjectId(roleObjId);
                            List<RoleMemberAttributeDataBo> roleMemAttrBos = new ArrayList<RoleMemberAttributeDataBo>();

                            roleMemberBo.setAttributeDetails(roleMemAttrBos);
                            if(roleMemberBo.isActive(new Timestamp(System.currentTimeMillis()))){
                                roleMemberBos.add(roleMemberBo);
                            } else {
                                processRolemember = false;
                            }

                            lastRoleMember = roleMemberBo;
                        }

                        String kimTypeId = rs.getString("KIM_TYP_ID");
                        String attrKey = rs.getString("KIM_ATTR_DEFN_ID");
                        String attrVal = rs.getString("ATTR_VAL");
                        if (processRolemember && StringUtils.isNotEmpty(kimTypeId)) {
                            KimType theType = KimApiServiceLocator.getKimTypeInfoService().getKimType(kimTypeId);
                            // Create RoleMemberAttributeDataBo for this row
                            RoleMemberAttributeDataBo roleMemAttrDataBo = new RoleMemberAttributeDataBo();

                            KimAttribute.Builder attrBuilder = KimAttribute.Builder.create(rs.getString(
                                    "ATTR_DEFN_CMPNT_NM"), rs.getString("ATTR_NAME"), rs.getString(
                                    "ATTR_DEFN_NMSPC_CD"));
                            attrBuilder.setActive(Truth.strToBooleanIgnoreCase(rs.getString("ATTR_DEFN_ACTV_IND")));
                            attrBuilder.setAttributeLabel(rs.getString("ATTR_DEFN_LBL"));
                            attrBuilder.setId(rs.getString("KIM_ATTR_DEFN_ID"));
                            attrBuilder.setObjectId(rs.getString("ATTR_DEFN_OBJ_ID"));
                            attrBuilder.setVersionNumber(rs.getLong("ATTR_DEFN_VER_NBR"));

                            roleMemAttrDataBo.setId(rs.getString("ATTR_DATA_ID"));
                            roleMemAttrDataBo.setAssignedToId(id);
                            roleMemAttrDataBo.setKimTypeId(kimTypeId);
                            roleMemAttrDataBo.setKimType(KimTypeBo.from(theType));
                            roleMemAttrDataBo.setKimAttributeId(attrBuilder.getId());
                            roleMemAttrDataBo.setAttributeValue(attrVal);
                            roleMemAttrDataBo.setVersionNumber(rs.getLong("ATTR_DATA_VER_NBR"));
                            roleMemAttrDataBo.setObjectId(rs.getString("ATTR_DATA_OBJ_ID"));

                            roleMemAttrDataBo.setKimAttribute(KimAttributeBo.from(attrBuilder.build()));
                            lastRoleMember.getAttributeDetails().add(roleMemAttrDataBo);
                        }

                    }
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                }
                return roleMemberBos;
            }
        }
        );
        return roleMemberBos;
    }

    @SuppressWarnings("unchecked")
    public List<RoleMemberBo> getRoleMembershipsForRoleIdsAsMembers(Collection<String> roleIds, Map<String, String> qualification) {
        Criteria c = new Criteria();

        if (roleIds != null && !roleIds.isEmpty()) {
            c.addIn(KIMPropertyConstants.RoleMember.MEMBER_ID, roleIds);
        }
        c.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, MemberType.ROLE.getCode());
//        addSubCriteriaBasedOnRoleQualification(c, qualification);

        Query query = QueryFactory.newQuery(RoleMemberBo.class, c);
        Collection<RoleMemberBo> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        ArrayList<RoleMemberBo> results = new ArrayList<RoleMemberBo>(coll.size());
        for (RoleMemberBo rm : coll) {
            if (rm.isActive(new Timestamp(System.currentTimeMillis()))) {
                results.add(rm);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public List<RoleMemberBo> getRoleMembersForRoleIdsWithFilters(Collection<String> roleIds, String principalId, Collection<String> groupIds, Map<String, String> qualification) {
        Criteria c = new Criteria();

        if (roleIds != null && !roleIds.isEmpty()) {
            c.addIn(KIMPropertyConstants.RoleMember.ROLE_ID, roleIds);
        }
        Criteria orSet = new Criteria();
        orSet.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, MemberType.ROLE.getCode());
        Criteria principalCheck = new Criteria();
        if (principalId != null) {
            principalCheck.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_ID, principalId);
        }
        principalCheck.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, MemberType.PRINCIPAL.getCode());
        orSet.addOrCriteria(principalCheck);
        Criteria groupCheck = new Criteria();
        if (groupIds != null && !groupIds.isEmpty()) {
            groupCheck.addIn(KIMPropertyConstants.RoleMember.MEMBER_ID, groupIds);
        }
        groupCheck.addEqualTo(KIMPropertyConstants.RoleMember.MEMBER_TYPE_CODE, MemberType.GROUP.getCode());
        orSet.addOrCriteria(groupCheck);
        c.addAndCriteria(orSet);
//        addSubCriteriaBasedOnRoleQualification(c, qualification);

        Query query = QueryFactory.newQuery(RoleMemberBo.class, c);
        Collection<RoleMemberBo> coll = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        ArrayList<RoleMemberBo> results = new ArrayList<RoleMemberBo>(coll.size());
        for (RoleMemberBo rm : coll) {
            if (rm.isActive(new Timestamp(System.currentTimeMillis()))) {
                results.add(rm);
            }
        }
        return results;
    }

}
