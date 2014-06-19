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
import org.joda.time.DateTime;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.util.Truth;
import org.kuali.rice.kim.api.common.attribute.KimAttribute;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeBo;
import org.kuali.rice.kim.impl.type.KimTypeBo;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

public class RoleDaoJdbc implements RoleDao {

    protected DataSource dataSource;

    @Override
    public List<RoleMemberBo> getRoleMembersForRoleIds(Collection<String> roleIds, String memberTypeCode,
            Map<String, String> qualification) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        final List<String> roleIDs = new ArrayList<String>(roleIds);
        final String memberTypeCd = memberTypeCode;
        final Map<String, String> qual = qualification;
        final List<RoleMemberBo> roleMemberBos = new ArrayList<RoleMemberBo>();
        template.execute(new PreparedStatementCreator() {

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

                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        /*
                         The query returns multiple lines for each role by joining a role with each of its members. This allows us to get all the role member
                         and role data in a single query (even though we are duplicating the role information across the role members). The cost of this
                         comes out to be cheaper than firing indiviudual queries for each role in cases where there are over 500 roles
                        */
                        StringBuilder sql1 = new StringBuilder("SELECT "
                                + " A0.ROLE_MBR_ID AS ROLE_MBR_ID,A0.ROLE_ID AS ROLE_ID,A0.MBR_ID AS MBR_ID,A0.MBR_TYP_CD AS MBR_TYP_CD,A0.VER_NBR AS ROLE_MBR_VER_NBR,A0.OBJ_ID AS ROLE_MBR_OBJ_ID,A0.ACTV_FRM_DT AS ROLE_MBR_ACTV_FRM_DT ,A0.ACTV_TO_DT AS ROLE_MBR_ACTV_TO_DT, "
                                + " BO.KIM_TYP_ID AS KIM_TYP_ID, BO.KIM_ATTR_DEFN_ID AS KIM_ATTR_DEFN_ID, BO.ATTR_VAL AS ATTR_VAL, BO.ATTR_DATA_ID AS ATTR_DATA_ID, BO.OBJ_ID AS ATTR_DATA_OBJ_ID, BO.VER_NBR AS ATTR_DATA_VER_NBR,  "
                                + " C0.KIM_ATTR_DEFN_ID AS KIM_ATTR_DEFN_ID, C0.OBJ_ID AS ATTR_DEFN_OBJ_ID, C0.VER_NBR as ATTR_DEFN_VER_NBR, C0.NM AS ATTR_NAME, C0.LBL as ATTR_DEFN_LBL, C0.ACTV_IND as ATTR_DEFN_ACTV_IND, C0.NMSPC_CD AS ATTR_DEFN_NMSPC_CD, C0.CMPNT_NM AS ATTR_DEFN_CMPNT_NM "
                                + " FROM KRIM_ROLE_MBR_T A0 JOIN KRIM_ROLE_MBR_ATTR_DATA_T BO ON A0.ROLE_MBR_ID = BO.ROLE_MBR_ID "
                                + " JOIN KRIM_ATTR_DEFN_T C0 ON BO.KIM_ATTR_DEFN_ID = C0.KIM_ATTR_DEFN_ID  ");

                        StringBuilder sql2 = new StringBuilder("SELECT"
                                + " D0.ROLE_MBR_ID AS ROLE_MBR_ID,D0.ROLE_ID AS ROLE_ID,D0.MBR_ID AS MBR_ID,D0.MBR_TYP_CD AS MBR_TYP_CD,D0.VER_NBR AS ROLE_MBR_VER_NBR,D0.OBJ_ID AS ROLE_MBR_OBJ_ID,D0.ACTV_FRM_DT AS ROLE_MBR_ACTV_FRM_DT ,D0.ACTV_TO_DT AS ROLE_MBR_ACTV_TO_DT, "
                                + " '' AS KIM_TYP_ID, '' AS KIM_ATTR_DEFN_ID, '' AS ATTR_VAL, '' AS ATTR_DATA_ID, '' AS ATTR_DATA_OBJ_ID, NULL AS ATTR_DATA_VER_NBR,"
                                + " '' AS KIM_ATTR_DEFN_ID,'' AS ATTR_DEFN_OBJ_ID, NULL as ATTR_DEFN_VER_NBR, '' AS ATTR_NAME, '' as ATTR_DEFN_LBL, '' as ATTR_DEFN_ACTV_IND, '' AS ATTR_DEFN_NMSPC_CD, '' AS ATTR_DEFN_CMPNT_NM "
                                + " FROM KRIM_ROLE_MBR_T D0 "
                                + " WHERE D0.ROLE_MBR_ID NOT IN (SELECT DISTINCT (E0.ROLE_MBR_ID) FROM KRIM_ROLE_MBR_ATTR_DATA_T E0)");

                        StringBuilder criteria = new StringBuilder();

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
                            sql2 = new StringBuilder();

                            if (criteria.length() > 0) {
                                sql1.append(" AND ");
                            } else {
                                sql1.append(" WHERE ");
                            }

                            sql1.append(" EXISTS (SELECT B1.ROLE_MBR_ID FROM KRIM_ROLE_MBR_ATTR_DATA_T B1 WHERE ");
                            int conditionCount = 0;
                            for (Map.Entry<String, String> qualifier : qual.entrySet()) {
                                if (StringUtils.isNotEmpty(qualifier.getValue())) {
                                    // advance the number of times we have found a non-null qualifier
                                    conditionCount++;

                                    // add '(' if encountering a non-null qualifier for the first time
                                    if (conditionCount == 1) {
                                        sql1.append("(");
                                    }

                                    // add the qualifier template with the parameters
                                    String value = (qualifier.getValue()).replace('*', '%');
                                    sql1.append(" (B1.ATTR_VAL LIKE ? AND B1.KIM_ATTR_DEFN_ID = ?) ");
                                    params1.add(value);
                                    params1.add(qualifier.getKey());
                                }

                                sql1.append("OR");
                            }
                            // remove the last OR
                            sql1.delete(sql1.length() - 2, sql1.length());
                            // add ') AND' if we encountered a non-null qualifier sub-query above
                            if (conditionCount != 0) {
                                sql1.append(") AND");
                            }
                            sql1.append(" B1.ROLE_MBR_ID = A0.ROLE_MBR_ID)");
                        }

                        StringBuilder sql = new StringBuilder(sql1.toString());

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
            @Override
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
                        if (processRolemember && StringUtils.isNotEmpty(kimTypeId)) {
                            KimType theType = KimApiServiceLocator.getKimTypeInfoService().getKimType(kimTypeId);
                            // Create RoleMemberAttributeDataBo for this row
                            RoleMemberAttributeDataBo roleMemAttrDataBo = new RoleMemberAttributeDataBo();

                            KimAttribute.Builder attrBuilder = KimAttribute.Builder.create(
                                    rs.getString("ATTR_DEFN_CMPNT_NM")
                                    , rs.getString("ATTR_NAME")
                                    , rs.getString("ATTR_DEFN_NMSPC_CD"));
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
                            roleMemAttrDataBo.setAttributeValue(rs.getString("ATTR_VAL"));
                            roleMemAttrDataBo.setVersionNumber(attrBuilder.getVersionNumber());
                            roleMemAttrDataBo.setObjectId(attrBuilder.getObjectId());

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

    public void setDataSource(DataSource dataSource) {
        this.dataSource = new TransactionAwareDataSourceProxy(dataSource);
    }

}
