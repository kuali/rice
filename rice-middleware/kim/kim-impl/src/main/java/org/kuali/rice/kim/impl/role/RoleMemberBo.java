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
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupContract;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.principal.PrincipalContract;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleContract;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMemberContract;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.kim.impl.membership.AbstractMemberBo;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.springframework.util.AutoPopulatingList;

/**
 * The column names have been used in a native query in RoleDaoOjb and will need to be modified if any changes to the
 * column names are made here.
 */
@Entity
@Table(name = "KRIM_ROLE_MBR_T")
public class RoleMemberBo extends AbstractMemberBo implements RoleMemberContract {

    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_ROLE_MBR_ID_S")
    @GeneratedValue(generator = "KRIM_ROLE_MBR_ID_S")
    @Id
    @Column(name = "ROLE_MBR_ID")
    private String id;

    @Column(name = "ROLE_ID")
    private String roleId;

    @OneToMany(targetEntity = RoleMemberAttributeDataBo.class, orphanRemoval = true, cascade = { CascadeType.ALL })
    @JoinColumn(name = "ROLE_MBR_ID", referencedColumnName = "ROLE_MBR_ID")
    private List<RoleMemberAttributeDataBo> attributeDetails;

    @Transient
    private List<RoleResponsibilityActionBo> roleRspActions;

    @Transient
    private Map<String, String> attributes;

    @Transient
    protected String memberName;

    @Transient
    protected String memberNamespaceCode;

    public List<RoleMemberAttributeDataBo> getAttributeDetails() {
        if (this.attributeDetails == null) {
            return new AutoPopulatingList<RoleMemberAttributeDataBo>(RoleMemberAttributeDataBo.class);
        }
        return this.attributeDetails;
    }

    public void setAttributeDetails(List<RoleMemberAttributeDataBo> attributeDetails) {
        this.attributeDetails = attributeDetails;
    }

    @Override
    public Map<String, String> getAttributes() {
        return attributeDetails != null ? KimAttributeDataBo.toAttributes(attributeDetails) : attributes;
    }

    public static RoleMember to(RoleMemberBo bo) {
        if (bo == null) {
            return null;
        }
        return RoleMember.Builder.create(bo).build();
    }

    public static RoleMemberBo from(RoleMember immutable) {
        if (immutable == null) {
            return null;
        }
        RoleMemberBo bo = new RoleMemberBo();
        bo.memberName = immutable.getMemberName();
        bo.memberNamespaceCode = immutable.getMemberNamespaceCode();
        bo.setId(immutable.getId());
        bo.setRoleId(immutable.getRoleId());
        List<RoleResponsibilityActionBo> actions = new ArrayList<RoleResponsibilityActionBo>();
        if (CollectionUtils.isNotEmpty(immutable.getRoleRspActions())) {
            for (RoleResponsibilityAction roleRespActn : immutable.getRoleRspActions()) {
                actions.add(RoleResponsibilityActionBo.from(roleRespActn));
            }
        }
        bo.setRoleRspActions(actions);
        bo.setMemberId(immutable.getMemberId());
        bo.setTypeCode(immutable.getType().getCode());
        bo.setActiveFromDateValue(immutable.getActiveFromDate() == null ? null : new Timestamp(immutable.getActiveFromDate().getMillis()));
        bo.setActiveToDateValue(immutable.getActiveToDate() == null ? null : new Timestamp(immutable.getActiveToDate().getMillis()));
        bo.setObjectId(immutable.getObjectId());
        bo.setVersionNumber(immutable.getVersionNumber());
        return bo;
    }

    protected Object getMember(MemberType memberType, String memberId) {
        if (MemberType.PRINCIPAL.equals(memberType)) {
            return KimApiServiceLocator.getIdentityService().getPrincipal(memberId);
        } else if (MemberType.GROUP.equals(memberType)) {
            return KimApiServiceLocator.getGroupService().getGroup(memberId);
        } else if (MemberType.ROLE.equals(memberType)) {
            return KimApiServiceLocator.getRoleService().getRole(memberId);
        }
        return null;
    }

    @Override
    public String getMemberName() {
        if (getType() == null || StringUtils.isEmpty(getMemberId())) {
            return "";
        }
        Object member = getMember(getType(), getMemberId());
        if (member == null) {
            this.memberName = "";
            Principal kp = KimApiServiceLocator.getIdentityService().getPrincipal(getMemberId());
            if (kp != null && kp.getPrincipalName() != null && !"".equals(kp.getPrincipalName())) {
                this.memberName = kp.getPrincipalName();
            }
            return this.memberName;
        }
        return getRoleMemberName(getType(), member);
    }

    public String getRoleMemberName(MemberType memberType, Object member) {
        String roleMemberName = "";
        if (MemberType.PRINCIPAL.equals(memberType)) {
            roleMemberName = ((PrincipalContract) member).getPrincipalName();
        } else if (MemberType.GROUP.equals(memberType)) {
            roleMemberName = ((GroupContract) member).getName();
        } else if (MemberType.ROLE.equals(memberType)) {
            roleMemberName = ((RoleContract) member).getName();
        }
        return roleMemberName;
    }

    @Override
    public String getMemberNamespaceCode() {
        if (getType() == null || StringUtils.isEmpty(getMemberId())) {
            return "";
        }
        this.memberNamespaceCode = "";
        if (MemberType.PRINCIPAL.equals(getType())) {
            this.memberNamespaceCode = "";
        } else if (MemberType.GROUP.equals(getType())) {
            Group groupInfo = KimApiServiceLocator.getGroupService().getGroup(getMemberId());
            if (groupInfo != null) {
                this.memberNamespaceCode = groupInfo.getNamespaceCode();
            }
        } else if (MemberType.ROLE.equals(getType())) {
            Role role = KimApiServiceLocator.getRoleService().getRole(getMemberId());
            if (role != null) {
                this.memberNamespaceCode = role.getNamespaceCode();
            }
        }
        return this.memberNamespaceCode;
    }

    /**
     * This method compares role member passed with this role member object and returns true if no differences or returns
     * false.
     *
     * @param targetMbrBo
     * @return boolean true if the member has not changed, false if the member has changed
     */
    public boolean equals(RoleMemberBo targetMbrBo) {
        if (!StringUtils.equals(getId(), targetMbrBo.getId())) {
            return false;
        }
        if (!StringUtils.equals(getType().getCode(), targetMbrBo.getType().getCode())) {
            return false;
        }
        if (!StringUtils.equals(getMemberId(), targetMbrBo.getMemberId())) {
            return false;
        }
        if (!ObjectUtils.equals(getActiveFromDate(), targetMbrBo.getActiveFromDate())) {
            return false;
        }
        if (!ObjectUtils.equals(getActiveToDate(), targetMbrBo.getActiveToDate())) {
            return false;
        }
        // Prepare list of attributes from this role member eliminating blank attributes
        Map<String, String> sourceMbrAttrDataList = new HashMap<String, String>();
        for (Map.Entry<String, String> mbrAttr : getAttributes().entrySet()) {
            if (StringUtils.isNotBlank(mbrAttr.getValue())) {
                ((HashMap<String, String>) sourceMbrAttrDataList).put(mbrAttr.getKey(), mbrAttr.getValue());
            }
        }
        // Prepare list of attributes from target role member eliminating blank attributes
        Map<String, String> targetMbrAttrDataList = new HashMap<String, String>();
        for (Map.Entry<String, String> mbrAttr : targetMbrBo.getAttributes().entrySet()) {
            if (StringUtils.isNotBlank(mbrAttr.getValue())) {
                ((HashMap<String, String>) targetMbrAttrDataList).put(mbrAttr.getKey(), mbrAttr.getValue());
            }
        }
        if (targetMbrAttrDataList.size() != sourceMbrAttrDataList.size()) {
            return false;
        }
        // Check if any attributes changed, then return false
        Map<String, String> matchedAttrs = new HashMap<String, String>();
        for (Map.Entry<String, String> newAttr : sourceMbrAttrDataList.entrySet()) {
            for (Map.Entry<String, String> origAttr : targetMbrAttrDataList.entrySet()) {
                if (StringUtils.equals(origAttr.getKey(), newAttr.getKey())) {
                    if (StringUtils.equals(origAttr.getValue(), newAttr.getValue())) {
                        ((HashMap<String, String>) matchedAttrs).put(newAttr.getKey(), newAttr.getValue());
                    }
                }
            }
        }
        if (matchedAttrs.size() != sourceMbrAttrDataList.size()) {
            return false;
        }
        // Check responsibility actions
        int targetMbrActionsSize = (targetMbrBo.getRoleRspActions() == null) ? 0 : targetMbrBo.getRoleRspActions().size();
        int sourceMbrActionsSize = (getRoleRspActions() == null) ? 0 : getRoleRspActions().size();
        if (targetMbrActionsSize != sourceMbrActionsSize) {
            return false;
        }
        if (sourceMbrActionsSize != 0) {
            List<RoleResponsibilityActionBo> matchedRspActions = new ArrayList<RoleResponsibilityActionBo>();
            // Check if any responsibility actions changed
            for (RoleResponsibilityActionBo newAction : getRoleRspActions()) {
                for (RoleResponsibilityActionBo origAction : targetMbrBo.getRoleRspActions()) {
                    if (StringUtils.equals(origAction.getId(), newAction.getId())) {
                        if (origAction.equals(newAction)) {
                            matchedRspActions.add(newAction);
                        }
                    }
                }
            }
            if (matchedRspActions.size() != getRoleRspActions().size()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    public List<RoleResponsibilityActionBo> getRoleRspActions() {
        if (this.roleRspActions == null) {
            return new AutoPopulatingList<RoleResponsibilityActionBo>(RoleResponsibilityActionBo.class);
        }
        return this.roleRspActions;
    }

    public void setRoleRspActions(List<RoleResponsibilityActionBo> roleRspActions) {
        this.roleRspActions = roleRspActions;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
