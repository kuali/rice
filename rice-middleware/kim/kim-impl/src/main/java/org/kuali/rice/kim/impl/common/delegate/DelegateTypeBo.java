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
package org.kuali.rice.kim.impl.common.delegate;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.kim.api.common.delegate.DelegateTypeContract;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.springframework.util.AutoPopulatingList;

@Entity
@Table(name = "KRIM_DLGN_T")
public class DelegateTypeBo extends DataObjectBase implements DelegateTypeContract {

    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_DLGN_ID_S")
    @GeneratedValue(generator = "KRIM_DLGN_ID_S")
    @Id
    @Column(name = "DLGN_ID")
    private String delegationId;

    @Column(name = "ROLE_ID")
    private String roleId;

    @Column(name = "ACTV_IND")
    @Convert(converter = BooleanYNConverter.class)
    private boolean active = true;

    @Column(name = "KIM_TYP_ID")
    private String kimTypeId;

    @Column(name = "DLGN_TYP_CD")
    private String delegationTypeCode;

    @OneToMany(targetEntity = DelegateMemberBo.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "DLGN_ID", referencedColumnName = "DLGN_ID", insertable = false, updatable = false)
    private List<DelegateMemberBo> members = new AutoPopulatingList<DelegateMemberBo>(DelegateMemberBo.class);

    public void setDelegationType(DelegationType type) {
        this.delegationTypeCode = type.getCode();
    }

    @Override
    public DelegationType getDelegationType() {
        return DelegationType.fromCode(this.delegationTypeCode);
    }

    public static DelegateType to(DelegateTypeBo bo) {
        return DelegateType.Builder.create(bo).build();
    }

    public static DelegateTypeBo from(DelegateType immutable) {
        // build list of DelegateMemberBo                      
        ArrayList<DelegateMemberBo> tmpMembers = new ArrayList<DelegateMemberBo>();
        for (DelegateMember member : immutable.getMembers()) {
            tmpMembers.add(DelegateMemberBo.from(member));
        }
        DelegateTypeBo bo = new DelegateTypeBo();
        bo.setDelegationId(immutable.getDelegationId());
        bo.setRoleId(immutable.getRoleId());
        bo.setActive(immutable.isActive());
        bo.setKimTypeId(immutable.getKimTypeId());
        bo.setDelegationTypeCode(immutable.getDelegationType().getCode());
        bo.setMembers(tmpMembers);
        return bo;
    }

    @Override
    public String getDelegationId() {
        return delegationId;
    }

    public void setDelegationId(String delegationId) {
        this.delegationId = delegationId;
    }

    @Override
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public boolean getActive() {
        return active;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String getKimTypeId() {
        return kimTypeId;
    }

    public void setKimTypeId(String kimTypeId) {
        this.kimTypeId = kimTypeId;
    }

    public String getDelegationTypeCode() {
        return delegationTypeCode;
    }

    public void setDelegationTypeCode(String delegationTypeCode) {
        this.delegationTypeCode = delegationTypeCode;
    }

    @Override
    public List<DelegateMemberBo> getMembers() {
        return members;
    }

    public void setMembers(List<DelegateMemberBo> members) {
        this.members = members;
    }
}
