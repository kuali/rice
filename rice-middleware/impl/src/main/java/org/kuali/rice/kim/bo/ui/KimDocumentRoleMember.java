/**
 * Copyright 2005-2017 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.role.RoleMemberBo;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.springframework.util.AutoPopulatingList;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Entity
@Table(name = "KRIM_PND_ROLE_MBR_MT")
public class KimDocumentRoleMember extends KimDocumentBoActivatableToFromEditableBase {

    private static final long serialVersionUID = -2463865643038170979L;

    @Id
    @Column(name = "ROLE_MBR_ID")
    protected String roleMemberId;

    @Column(name = "ROLE_ID")
    protected String roleId;

    @Column(name = "MBR_ID")
    protected String memberId;

    //TODO: remove the default                       
    @Column(name = "MBR_TYP_CD")
    protected String memberTypeCode = KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE.getCode();

    @Transient
    protected String memberName;

    @Transient
    protected String memberNamespaceCode;

    @Transient
    protected String memberFullName;

    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToMany(targetEntity = KimDocumentRoleQualifier.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumns({ 
        @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false), 
        @JoinColumn(name = "ROLE_MBR_ID", referencedColumnName = "ROLE_MBR_ID", insertable = false, updatable = false) })
    protected List<KimDocumentRoleQualifier> qualifiers = new AutoPopulatingList<KimDocumentRoleQualifier>(KimDocumentRoleQualifier.class);

    @Transient
    protected String qualifiersToDisplay;

    @JoinFetch(value= JoinFetchType.OUTER)
    @OneToMany(targetEntity = KimDocumentRoleResponsibilityAction.class, orphanRemoval = true, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinColumns({ 
        @JoinColumn(name = "FDOC_NBR", referencedColumnName = "FDOC_NBR", insertable = false, updatable = false), 
        @JoinColumn(name = "ROLE_MBR_ID", referencedColumnName = "ROLE_MBR_ID", insertable = false, updatable = false) })
    private List<KimDocumentRoleResponsibilityAction> roleRspActions;

    public KimDocumentRoleMember() {
        qualifiers = new ArrayList<KimDocumentRoleQualifier>();
        roleRspActions = new ArrayList<KimDocumentRoleResponsibilityAction>();
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

    public String getRoleMemberId() {
        return this.roleMemberId;
    }

    public void setRoleMemberId(String roleMemberId) {
        this.roleMemberId = roleMemberId;
    }

    public String getRoleId() {
        return this.roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public KimDocumentRoleQualifier getQualifier(String kimAttributeDefnId) {
        for (KimDocumentRoleQualifier qualifier : qualifiers) {
            if (qualifier.getKimAttrDefnId().equals(kimAttributeDefnId)) {
                return qualifier;
            }
        }
        return null;
    }

    public List<KimDocumentRoleQualifier> getQualifiers() {
        return this.qualifiers;
    }

    public void setQualifiers(List<KimDocumentRoleQualifier> qualifiers) {
        this.qualifiers = qualifiers;
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

    /**
	 * @return the memberName
	 */
    public String getMemberName() {
        if (memberName == null) {
            populateDerivedValues();
        }
        return memberName;
    }

    /**
	 * @param memberName the memberName to set
	 */
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public List<KimDocumentRoleResponsibilityAction> getRoleRspActions() {
        return this.roleRspActions;
    }

    public void setRoleRspActions(List<KimDocumentRoleResponsibilityAction> roleRspActions) {
        this.roleRspActions = roleRspActions;
    }

    /**
	 * @return the memberNamespaceCode
	 */
    public String getMemberNamespaceCode() {
        if (memberNamespaceCode == null) {
            populateDerivedValues();
        }
        return memberNamespaceCode;
    }

    /**
	 * @param memberNamespaceCode the memberNamespaceCode to set
	 */
    public void setMemberNamespaceCode(String memberNamespaceCode) {
        this.memberNamespaceCode = memberNamespaceCode;
    }

    protected void populateDerivedValues() {
        if (!StringUtils.isEmpty(getMemberId())) {
            if (MemberType.PRINCIPAL.getCode().equals(getMemberTypeCode())) {
                Principal principalInfo = KimApiServiceLocator.getIdentityService().getPrincipal(getMemberId());
                if (principalInfo != null) {
                    setMemberName(principalInfo.getPrincipalName());
                }
            } else if (MemberType.GROUP.getCode().equals(getMemberTypeCode())) {
                Group groupInfo = KimApiServiceLocator.getGroupService().getGroup(getMemberId());
                if (groupInfo != null) {
                    setMemberName(groupInfo.getName());
                    setMemberNamespaceCode(groupInfo.getNamespaceCode());
                }
            } else if (MemberType.ROLE.getCode().equals(getMemberTypeCode())) {
                Role roleInfo = KimApiServiceLocator.getRoleService().getRole(getMemberId());
                if (roleInfo != null) {
                    setMemberName(roleInfo.getName());
                    setMemberNamespaceCode(roleInfo.getNamespaceCode());
                }
            }
        }
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

    public Map<String, String> getQualifierAsMap() {
        Map<String, String> m = new HashMap<String, String>();
        for (KimDocumentRoleQualifier data : getQualifiers()) {
            if (data.getKimAttribute() == null) {
                KradDataServiceLocator.getDataObjectService().wrap(this).fetchRelationship("kimAttribute");
            }
            if (data.getKimAttribute() != null) {
                m.put(data.getKimAttribute().getAttributeName(), data.getAttrVal());
            }
        }
        return m;
    }

    public String getQualifiersToDisplay() {
        return this.qualifiersToDisplay;
    }

    public void setQualifiersToDisplay(String qualifiersToDisplay) {
        this.qualifiersToDisplay = qualifiersToDisplay;
    }

    public String getMemberFullName() {
        return this.memberFullName;
    }

	public void setMemberFullName(String memberFullName) {
		this.memberFullName = memberFullName;
	}

    public boolean isMemberNameNull() {
        if (memberName==null) {
            return true;
        }
        return false;
    }

    public boolean isMemberNameSpaceCodeNull() {
        if (memberNamespaceCode==null) {
            return true;
        }
        return false;
    }

    public static void copyProperties(KimDocumentRoleMember copyToKimDocRoleMember, RoleMemberBo copyFromRoleMbrBo){
        if(copyToKimDocRoleMember!=null && copyFromRoleMbrBo!=null) {
            copyToKimDocRoleMember.setRoleId(copyFromRoleMbrBo.getRoleId());
            copyToKimDocRoleMember.setMemberId(copyFromRoleMbrBo.getMemberId());
            copyToKimDocRoleMember.setMemberName(copyFromRoleMbrBo.getMemberName());
            copyToKimDocRoleMember.setMemberNamespaceCode(copyFromRoleMbrBo.getMemberNamespaceCode());
            copyToKimDocRoleMember.setActive(copyFromRoleMbrBo.isActive());
            copyToKimDocRoleMember.setActiveFromDate(copyFromRoleMbrBo.getActiveFromDateValue());
            copyToKimDocRoleMember.setActiveToDate(copyFromRoleMbrBo.getActiveToDateValue());
            copyToKimDocRoleMember.setVersionNumber(copyFromRoleMbrBo.getVersionNumber());
            copyToKimDocRoleMember.setObjectId(copyFromRoleMbrBo.getObjectId());
        }
    }
}
