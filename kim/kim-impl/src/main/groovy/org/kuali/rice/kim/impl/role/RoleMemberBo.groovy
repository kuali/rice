/*
 * Copyright 2006-2011 The Kuali Foundation
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


import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.persistence.Transient
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Parameter
import org.kuali.rice.core.util.AttributeSet
import org.kuali.rice.kim.api.role.Role
import org.kuali.rice.kim.api.role.RoleMember
import org.kuali.rice.kim.api.role.RoleMemberContract
import org.kuali.rice.kim.api.role.RoleService
import org.kuali.rice.kim.api.services.KimApiServiceLocator
import org.kuali.rice.kim.api.type.KimType
import org.kuali.rice.kim.api.type.KimTypeAttribute
import org.kuali.rice.kim.api.type.KimTypeInfoService
import org.kuali.rice.kim.impl.membership.AbstractMemberBo
import org.springframework.util.AutoPopulatingList

@Entity
@Table(name = "KRIM_ROLE_MBR_T")
public class RoleMemberBo extends AbstractMemberBo implements RoleMemberContract {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "KRIM_ROLE_MBR_ID_S")
    @GenericGenerator(name = "KRIM_ROLE_MBR_ID_S", strategy = "org.kuali.rice.core.jpa.spring.RiceNumericStringSequenceStyleGenerator", parameters = [
    @Parameter(name = "sequence_name", value = "KRIM_ROLE_MBR_ID_S"),
    @Parameter(name = "value_column", value = "id")
    ])
    @Column(name = "ROLE_MBR_ID")
    String roleMemberId;

    @Column(name = "ROLE_ID")
    String roleId;

    @Column(name = "MBR_TYP_CD")
    String memberTypeCode

    @OneToMany(targetEntity = RoleMemberAttributeDataBo.class, cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "ROLE_MBR_ID", insertable = false, updatable = false)
    List<RoleMemberAttributeDataBo> attributes; // = new AutoPopulatingList(RoleMemberAttributeDataBo.class);

    @Transient
    List<RoleResponsibilityActionBo> roleRspActions;

    @Transient
    transient AttributeSet qualifier = null;


    List<RoleMemberAttributeDataBo> getAttributes() {
        if (this.attributes == null) {
            return new AutoPopulatingList(RoleMemberAttributeDataBo.class);
        }
        return this.attributes;
    }

    void setAttributes(List<RoleMemberAttributeDataBo> attributes) {
        this.attributes = attributes;
    }

    AttributeSet getQualifier() {
        if (qualifier == null) {
            Role role = getRoleService().getRole(roleId);
            KimType kimType = getTypeInfoService().getKimType(role.getKimTypeId());
            AttributeSet m = new AttributeSet();
            for (RoleMemberAttributeDataBo data: getAttributes()) {
                KimTypeAttribute attribute = null;
                if (kimType != null) {
                    attribute = kimType.getAttributeDefinitionById(data.getKimAttributeId());
                }
                if (attribute != null) {
                    m.put(attribute.getKimAttribute().getAttributeName(), data.getAttributeValue());
                } else {
                    m.put(data.getKimAttribute().getAttributeName(), data.getAttributeValue());
                }
            }
            qualifier = m;
        }
        return qualifier;
    }

    private transient static KimTypeInfoService kimTypeInfoService;
    private transient static RoleService roleService;

    protected static KimTypeInfoService getTypeInfoService() {
        if (kimTypeInfoService == null) {
            kimTypeInfoService = KimApiServiceLocator.getKimTypeInfoService();
        }
        return kimTypeInfoService;
    }

    protected static RoleService getRoleService() {
        if (roleService == null) {
            roleService = KimApiServiceLocator.getRoleManagementService();
        }
        return roleService;
    }



    public static RoleMember to(RoleMemberBo bo) {
        if (bo == null) {return null;}
        return RoleMember.Builder.create(bo).build();
    }

    public static RoleMemberBo from(RoleMember immutable) {
        if (immutable == null) { return null; }

        return new RoleMemberBo(
                roleMemberId: immutable.roleMemberId,
                roleId: immutable.roleId,
                qualifier: immutable.qualifier,
                roleRspActions: immutable.roleRspActions,
                memberId: immutable.memberId,
                memberTypeCode: immutable.memberTypeCode,
                activeFromDate: immutable.activeFromDate,
                activeToDate: immutable.activeToDate,
        )
    }
}
