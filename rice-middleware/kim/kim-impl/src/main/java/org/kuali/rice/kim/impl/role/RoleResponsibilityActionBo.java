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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.role.RoleResponsibilityActionContract;
import org.kuali.rice.krad.bo.DataObjectBase;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.data.jpa.converters.BooleanYNConverter;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ROLE_RSP_ACTN_T")
public class RoleResponsibilityActionBo extends DataObjectBase implements RoleResponsibilityActionContract {

    private static final long serialVersionUID = 1L;

    @PortableSequenceGenerator(name = "KRIM_ROLE_RSP_ACTN_ID_S")
    @GeneratedValue(generator = "KRIM_ROLE_RSP_ACTN_ID_S")
    @Id
    @Column(name = "ROLE_RSP_ACTN_ID")
    private String id;

    @Column(name = "ROLE_RSP_ID")
    private String roleResponsibilityId;

    @Column(name = "ROLE_MBR_ID")
    private String roleMemberId;

    @Column(name = "ACTN_TYP_CD")
    private String actionTypeCode;

    @Column(name = "ACTN_PLCY_CD")
    private String actionPolicyCode;

    @Column(name = "FRC_ACTN")
    @Convert(converter = BooleanYNConverter.class)
    private boolean forceAction;

    @Column(name = "PRIORITY_NBR")
    private Integer priorityNumber;

    @ManyToOne(targetEntity = RoleResponsibilityBo.class, cascade = { CascadeType.REFRESH })
    @JoinColumn(name = "ROLE_RSP_ID", referencedColumnName = "ROLE_RSP_ID", insertable = false, updatable = false)
    private RoleResponsibilityBo roleResponsibility;

    @Override
    public RoleResponsibilityBo getRoleResponsibility() {
        return roleResponsibility;
    }

    public static RoleResponsibilityAction to(RoleResponsibilityActionBo bo) {
        if (bo == null) {
            return null;
        }
        return RoleResponsibilityAction.Builder.create(bo).build();
    }

    public static RoleResponsibilityActionBo from(RoleResponsibilityAction immutable) {
        if (immutable == null) {
            return null;
        }
        RoleResponsibilityActionBo bo = new RoleResponsibilityActionBo();
        bo.id = immutable.getId();
        bo.roleResponsibilityId = immutable.getRoleResponsibilityId();
        bo.roleMemberId = immutable.getRoleMemberId();
        bo.actionTypeCode = immutable.getActionTypeCode();
        bo.actionPolicyCode = immutable.getActionPolicyCode();
        bo.forceAction = immutable.isForceAction();
        bo.priorityNumber = immutable.getPriorityNumber();
        bo.roleResponsibility = RoleResponsibilityBo.from(immutable.getRoleResponsibility());
        bo.setVersionNumber(immutable.getVersionNumber());
        return bo;
    }

    public boolean equals(RoleResponsibilityActionBo roleRspActn) {
        if (!StringUtils.equals(roleRspActn.getRoleMemberId(), getRoleMemberId())) {
            return false;
        }
        if (!StringUtils.equals(roleRspActn.getRoleResponsibilityId(), getRoleResponsibilityId())) {
            return false;
        }
        if (!StringUtils.equals(roleRspActn.getActionTypeCode(), getActionTypeCode())) {
            return false;
        }
        if (!StringUtils.equals(roleRspActn.getActionPolicyCode(), getActionPolicyCode())) {
            return false;
        }
        if (!ObjectUtils.equals(roleRspActn.getPriorityNumber(), getPriorityNumber())) {
            return false;
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
    public String getRoleResponsibilityId() {
        return roleResponsibilityId;
    }

    public void setRoleResponsibilityId(String roleResponsibilityId) {
        this.roleResponsibilityId = roleResponsibilityId;
    }

    @Override
    public String getRoleMemberId() {
        return roleMemberId;
    }

    public void setRoleMemberId(String roleMemberId) {
        this.roleMemberId = roleMemberId;
    }

    @Override
    public String getActionTypeCode() {
        return actionTypeCode;
    }

    public void setActionTypeCode(String actionTypeCode) {
        this.actionTypeCode = actionTypeCode;
    }

    @Override
    public String getActionPolicyCode() {
        return actionPolicyCode;
    }

    public void setActionPolicyCode(String actionPolicyCode) {
        this.actionPolicyCode = actionPolicyCode;
    }

    public boolean getForceAction() {
        return forceAction;
    }

    @Override
    public boolean isForceAction() {
        return forceAction;
    }

    public void setForceAction(boolean forceAction) {
        this.forceAction = forceAction;
    }

    @Override
    public Integer getPriorityNumber() {
        return priorityNumber;
    }

    public void setPriorityNumber(Integer priorityNumber) {
        this.priorityNumber = priorityNumber;
    }

    public void setRoleResponsibility(RoleResponsibilityBo roleResponsibility) {
        this.roleResponsibility = roleResponsibility;
    }
}
