package org.kuali.rice.kim.impl.role;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.persistence.annotations.Convert;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.role.RoleResponsibilityActionContract;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_ROLE_RSP_ACTN_T")
public class RoleResponsibilityActionBo extends PersistableBusinessObjectBase implements RoleResponsibilityActionContract {
    private static final long serialVersionUID = 1L;
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
    @javax.persistence.Convert(converter=org.kuali.rice.krad.data.converters.BooleanYNConverter.class)
    private boolean forceAction;
    @Column(name = "PRIORITY_NBR")
    private Integer priorityNumber;
    @ManyToOne(targetEntity = RoleResponsibilityBo.class, fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "ROLE_RSP_ID", insertable = false, updatable = false)
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
