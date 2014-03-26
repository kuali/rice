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
package org.kuali.rice.kim.bo.ui;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.role.RoleBo;
import org.kuali.rice.kim.impl.type.KimTypeAttributesHelper;
import org.kuali.rice.kim.impl.type.KimTypeBo;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.springframework.util.AutoPopulatingList;

/**
 * 
 * @author Kuali Rice Team (kuali-rice@googleroles.com)
 *
 */
@Entity
@Table(name = "KRIM_PND_DLGN_MBR_T")
public class RoleDocumentDelegationMember extends KimDocumentBoActivatableToFromBase {

    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_DLGN_MBR_ID_S")
    @GeneratedValue(generator = "KRIM_DLGN_MBR_ID_S")
    @Id
    @Column(name = "DLGN_MBR_ID")
    protected String delegationMemberId;

    @Column(name = "ROLE_MBR_ID")
    protected String roleMemberId;

    @Transient
    protected String roleMemberMemberId;

    @Transient
    protected String roleMemberMemberTypeCode;

    @Transient
    protected String roleMemberName;

    @Transient
    protected String roleMemberNamespaceCode;

    @Transient
    private KimTypeAttributesHelper attributesHelper;

    //For Person Document UI - flattening the delegation - delegation member hierarchy                       
    @Transient
    protected RoleBo roleBo = new RoleBo();

    @Column(name = "DLGN_ID")
    protected String delegationId;

    @Column(name = "MBR_ID")
    protected String memberId;

    @Column(name = "MBR_TYP_CD")
    protected String memberTypeCode = KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE.getCode();

    @Transient
    protected String memberNamespaceCode;

    @Column(name = "MBR_NM")
    protected String memberName;

    @OneToMany(targetEntity = RoleDocumentDelegationMemberQualifier.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumns({ 
        @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false), 
        @JoinColumn(name = "DLGN_MBR_ID", referencedColumnName = "DLGN_MBR_ID", insertable = false, updatable = false) })
    protected List<RoleDocumentDelegationMemberQualifier> qualifiers = new AutoPopulatingList<RoleDocumentDelegationMemberQualifier>(RoleDocumentDelegationMemberQualifier.class);

    @Transient
    protected String delegationTypeCode;

    public String getDelegationTypeCode() {
        return this.delegationTypeCode;
    }

    public void setDelegationTypeCode(String delegationTypeCode) {
        this.delegationTypeCode = delegationTypeCode;
    }

    public String getDelegationId() {
        return this.delegationId;
    }

    public void setDelegationId(String delegationId) {
        this.delegationId = delegationId;
    }

    /**
	 * @return the qualifiers
	 */
    public List<RoleDocumentDelegationMemberQualifier> getQualifiers() {
        return this.qualifiers;
    }

    public RoleDocumentDelegationMemberQualifier getQualifier(String kimAttributeDefnId) {
        for (RoleDocumentDelegationMemberQualifier qualifier : qualifiers) {
            if (qualifier.getKimAttrDefnId().equals(kimAttributeDefnId)) {
                return qualifier;
            }
        }
        return null;
    }

    /**
	 * @param qualifiers the qualifiers to set
	 */
    public void setQualifiers(List<RoleDocumentDelegationMemberQualifier> qualifiers) {
        this.qualifiers = qualifiers;
    }

    public int getNumberOfQualifiers() {
        return qualifiers == null ? 0 : qualifiers.size();
    }

    /**
	 * @return the memberId
	 */
    public String getMemberId() {
        return this.memberId;
    }

    /**
	 * @param memberId the memberId to set
	 */
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    /**
	 * @return the memberName
	 */
    public String getMemberName() {
        return this.memberName;
    }

    /**
	 * @param memberName the memberName to set
	 */
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    /**
	 * @return the assignedToId
	 */
    public String getDelegationMemberId() {
        return this.delegationMemberId;
    }

    /**
	 * @param delegationMemberId the assignedToId to set
	 */
    public void setDelegationMemberId(String delegationMemberId) {
        this.delegationMemberId = delegationMemberId;
    }

    /**
	 * @return the memberTypeCode
	 */
    public String getMemberTypeCode() {
        return this.memberTypeCode;
    }

    /**
	 * @param memberTypeCode the memberTypeCode to set
	 */
    public void setMemberTypeCode(String memberTypeCode) {
        this.memberTypeCode = memberTypeCode;
    }

    public boolean isDelegationPrimary() {
        return DelegationType.PRIMARY.getCode().equals(getDelegationTypeCode());
    }

    public boolean isDelegationSecondary() {
        return DelegationType.SECONDARY.getCode().equals(getDelegationTypeCode());
    }

    /**
	 * @return the memberNamespaceCode
	 */
    public String getMemberNamespaceCode() {
        if (memberNamespaceCode == null) {
            populateDerivedValues();
        }
        return this.memberNamespaceCode;
    }

    /**
	 * @param memberNamespaceCode the memberNamespaceCode to set
	 */
    public void setMemberNamespaceCode(String memberNamespaceCode) {
        this.memberNamespaceCode = memberNamespaceCode;
    }

    protected void populateDerivedValues() {
        if (!StringUtils.isEmpty(getMemberId())) {
            if (MemberType.GROUP.getCode().equals(getMemberTypeCode())) {
                Group groupInfo = KimApiServiceLocator.getGroupService().getGroup(getMemberId());
                if (groupInfo != null) {
                    setMemberNamespaceCode(groupInfo.getNamespaceCode());
                }
            } else if (MemberType.ROLE.getCode().equals(getMemberTypeCode())) {
                Role roleInfo = KimApiServiceLocator.getRoleService().getRole(getMemberId());
                if (roleInfo != null) {
                    setMemberNamespaceCode(roleInfo.getNamespaceCode());
                }
            }
        }
    }

    /**
	 * @return the roleMemberId
	 */
    public String getRoleMemberId() {
        return this.roleMemberId;
    }

    /**
	 * @param roleMemberId the roleMemberId to set
	 */
    public void setRoleMemberId(String roleMemberId) {
        this.roleMemberId = roleMemberId;
    }

    public boolean isRole() {
        return getMemberTypeCode() != null && getMemberTypeCode().equals(MemberType.ROLE.getCode());
    }

    public boolean isGroup() {
        return getMemberTypeCode() != null && getMemberTypeCode().equals(MemberType.GROUP.getCode());
    }

    public boolean isPrincipal() {
        return getMemberTypeCode() != null && getMemberTypeCode().equals(MemberType.PRINCIPAL.getCode());
    }

    /**
	 * @return the roleMemberName
	 */
    public String getRoleMemberName() {
        return this.roleMemberName;
    }

    /**
	 * @param roleMemberName the roleMemberName to set
	 */
    public void setRoleMemberName(String roleMemberName) {
        this.roleMemberName = roleMemberName;
    }

    /**
	 * @return the roleMemberNamespaceCode
	 */
    public String getRoleMemberNamespaceCode() {
        return this.roleMemberNamespaceCode;
    }

    /**
	 * @param roleMemberNamespaceCode the roleMemberNamespaceCode to set
	 */
    public void setRoleMemberNamespaceCode(String roleMemberNamespaceCode) {
        this.roleMemberNamespaceCode = roleMemberNamespaceCode;
    }

    /**
	 * @return the roleBo
	 */
    public RoleBo getRoleBo() {
        return this.roleBo;
    }

    /**
	 * @param roleBo the roleBo to set
	 */
    public void setRoleBo(RoleBo roleBo) {
        this.roleBo = roleBo;
        setAttributesHelper(new KimTypeAttributesHelper(KimTypeBo.to(roleBo.getKimRoleType())));
    }

    /**
	 * @return the attributesHelper
	 */
    public KimTypeAttributesHelper getAttributesHelper() {
        return this.attributesHelper;
    }

    /**
	 * @param attributesHelper the attributesHelper to set
	 */
    public void setAttributesHelper(KimTypeAttributesHelper attributesHelper) {
        this.attributesHelper = attributesHelper;
    }

    /**
	 * @return the roleMemberMemberId
	 */
    public String getRoleMemberMemberId() {
        return this.roleMemberMemberId;
    }

    /**
	 * @param roleMemberMemberId the roleMemberMemberId to set
	 */
    public void setRoleMemberMemberId(String roleMemberMemberId) {
        this.roleMemberMemberId = roleMemberMemberId;
    }

    /**
	 * @return the roleMemberMemberTypeCode
	 */
    public String getRoleMemberMemberTypeCode() {
        return this.roleMemberMemberTypeCode;
    }

    /**
	 * @param roleMemberMemberTypeCode the roleMemberMemberTypeCode to set
	 */
    public void setRoleMemberMemberTypeCode(String roleMemberMemberTypeCode) {
        this.roleMemberMemberTypeCode = roleMemberMemberTypeCode;
    }
}
