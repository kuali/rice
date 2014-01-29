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

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateMemberContract;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.kim.impl.membership.AbstractMemberBo;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.springframework.util.AutoPopulatingList;

@Entity
@Table(name = "KRIM_DLGN_MBR_T")
public class DelegateMemberBo extends AbstractMemberBo implements DelegateMemberContract {
    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_DLGN_MBR_ID_S")
    @GeneratedValue(generator = "KRIM_DLGN_MBR_ID_S")
    @Id
    @Column(name = "DLGN_MBR_ID")
    private String delegationMemberId;

    @Column(name = "DLGN_ID")
    private String delegationId;

    @Column(name = "ROLE_MBR_ID")
    private String roleMemberId;

    @OneToMany(targetEntity = DelegateMemberAttributeDataBo.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumn(name = "DLGN_MBR_ID", referencedColumnName = "DLGN_MBR_ID", insertable = false, updatable = false)
    private List<DelegateMemberAttributeDataBo> attributeDetails = new AutoPopulatingList<DelegateMemberAttributeDataBo>(DelegateMemberAttributeDataBo.class);

    @Transient
    private Map<String, String> attributes;

    /**
     * Returns Attributes derived from the internal List of DelegateMemberAttributeDataBos.  This field is
     * not exposed in the DelegateMemberContract as it is not a required field in the DelegateMember DTO
     *
     * @return
     */
    public Map<String, String> getQualifier() {
        Map<String, String> attribs = new HashMap<String, String>();
        if (attributeDetails == null) {
            return attribs;
        }
        for (DelegateMemberAttributeDataBo attr : attributeDetails) {
            attribs.put(attr.getKimAttribute().getAttributeName(), attr.getAttributeValue());
        }
        return attribs;
    }

    public List<DelegateMemberAttributeDataBo> getAttributeDetails() {
        if (this.attributeDetails == null) {
            return new AutoPopulatingList<DelegateMemberAttributeDataBo>(DelegateMemberAttributeDataBo.class);
        }
        return this.attributeDetails;
    }

    public void setAttributeDetails(List<DelegateMemberAttributeDataBo> attributeDetails) {
        this.attributeDetails = attributeDetails;
    }

    @Override
    public Map<String, String> getAttributes() {
        return CollectionUtils.isNotEmpty(attributeDetails) ? KimAttributeDataBo.toAttributes(attributeDetails) : attributes;
    }

    public static DelegateMember to(DelegateMemberBo bo) {
        if (bo == null) {
            return null;
        }
        return DelegateMember.Builder.create(bo).build();
    }

    public static DelegateMemberBo from(DelegateMember immutable) {
        if (immutable == null) {
            return null;
        }
        DelegateMemberBo bo = new DelegateMemberBo();
        bo.setDelegationMemberId(immutable.getDelegationMemberId());
        bo.setActiveFromDateValue(immutable.getActiveFromDate() == null ? null : new Timestamp(immutable.getActiveFromDate().getMillis()));
        bo.setActiveToDateValue(immutable.getActiveToDate() == null ? null : new Timestamp(immutable.getActiveToDate().getMillis()));
        bo.setDelegationId(immutable.getDelegationId());
        bo.setMemberId(immutable.getMemberId());
        bo.setRoleMemberId(immutable.getRoleMemberId());
        bo.setTypeCode(immutable.getType().getCode());
        bo.setVersionNumber(immutable.getVersionNumber());
        bo.setAttributes(immutable.getAttributes());
        return bo;
    }

    @Override
    public String getDelegationMemberId() {
        return delegationMemberId;
    }

    public void setDelegationMemberId(String delegationMemberId) {
        this.delegationMemberId = delegationMemberId;
    }

    @Override
    public String getDelegationId() {
        return delegationId;
    }

    public void setDelegationId(String delegationId) {
        this.delegationId = delegationId;
    }

    @Override
    public String getRoleMemberId() {
        return roleMemberId;
    }

    public void setRoleMemberId(String roleMemberId) {
        this.roleMemberId = roleMemberId;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
