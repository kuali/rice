/**
 * Copyright 2005-2011 The Kuali Foundation
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
import org.kuali.rice.kim.api.role.Role
import org.kuali.rice.kim.api.role.RoleMember
import org.kuali.rice.kim.api.role.RoleMemberContract
import org.kuali.rice.kim.api.services.KimApiServiceLocator
import org.kuali.rice.kim.api.type.KimTypeInfoService
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo
import org.kuali.rice.kim.impl.membership.AbstractMemberBo
import org.springframework.util.AutoPopulatingList
import java.sql.Timestamp
import org.kuali.rice.core.api.membership.MemberType

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
    String id;

    @Column(name = "ROLE_ID")
    String roleId;

    @OneToMany(targetEntity = RoleMemberAttributeDataBo.class, cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "ROLE_MBR_ID", insertable = false, updatable = false)
    List<RoleMemberAttributeDataBo> attributeDetails; // = new AutoPopulatingList(RoleMemberAttributeDataBo.class);

    @Transient
    List<RoleResponsibilityActionBo> roleRspActions;

    @Transient
    Map<String,String> attributes


    List<RoleMemberAttributeDataBo> getAttributeDetails() {
        if (this.attributeDetails == null) {
            return new AutoPopulatingList(RoleMemberAttributeDataBo.class);
        }
        return this.attributeDetails;
    }

    void setAttributeDetails(List<RoleMemberAttributeDataBo> attributeDetails) {
        this.attributeDetails = attributeDetails;
    }

    Map<String,String> getAttributes() {
        return attributeDetails != null ? KimAttributeDataBo.toAttributes(attributeDetails) : attributes
    }

    public static RoleMember to(RoleMemberBo bo) {
        if (bo == null) {return null;}
        return RoleMember.Builder.create(bo).build();
    }

    public static RoleMemberBo from(RoleMember immutable) {
        if (immutable == null) { return null; }

        return new RoleMemberBo(
                id: immutable.id,
                roleId: immutable.roleId,
                roleRspActions: immutable.roleRspActions.collect { RoleResponsibilityActionBo.from(it) },
                memberId: immutable.memberId,
                typeCode: immutable.getType().code,
                activeFromDateValue: immutable.activeFromDate == null ? null : new Timestamp(immutable.activeFromDate.getMillis()),
                activeToDateValue: immutable.activeToDate == null ? null : new Timestamp(immutable.activeToDate.getMillis()),
                objectId : immutable.objectId,
                versionNumber: immutable.versionNumber
        )
    }

    public getTypeCode() {
        return this.typeCode
    }
}
