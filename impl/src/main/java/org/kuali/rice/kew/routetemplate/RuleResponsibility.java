/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.routetemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.kuali.rice.core.reflect.ObjectDefinition;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.WorkflowPersistable;
import org.kuali.rice.kew.actionrequests.ActionRequestValue;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.plugin.attributes.RoleAttribute;
import org.kuali.rice.kew.user.UserService;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.user.WorkflowUserId;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.workgroup.WorkflowGroupId;
import org.kuali.rice.kew.workgroup.Workgroup;
import org.kuali.rice.kew.workgroup.WorkgroupService;


/**
 * A model bean representing the responsibility of a user, workgroup, or role
 * to perform some action on a document.  Used by the rule system to
 * identify the appropriate responsibile parties to generate
 * {@link ActionRequestValue}s to.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="EN_RULE_RSP_T")
public class RuleResponsibility implements WorkflowPersistable {

	private static final long serialVersionUID = -1565688857123316797L;
	@Id
	@Column(name="RULE_RSP_ID")
	private Long ruleResponsibilityKey;
    @Column(name="RSP_ID")
	private Long responsibilityId;
    @Column(name="RULE_BASE_VAL_ID")
	private Long ruleBaseValuesId;
    @Column(name="ACTION_RQST_CD")
	private String actionRequestedCd;
    @Column(name="RULE_RSP_NM")
	private String ruleResponsibilityName;
    @Column(name="RULE_RSP_TYP")
	private String ruleResponsibilityType;
    @Version
	@Column(name="DB_LOCK_VER_NBR")
	private Integer lockVerNbr;
    @Column(name="RULE_RSP_PRIO_NBR")
	private Integer priority;
    @Column(name="RULE_RSP_APPR_PLCY")
	private String approvePolicy;

    @ManyToOne(fetch=FetchType.EAGER, cascade={CascadeType.PERSIST})
	@JoinColumn(name="RULE_BASE_VAL_ID", insertable=false, updatable=false)
	private RuleBaseValues ruleBaseValues;
    @Transient
    private List delegationRules = new ArrayList();

    public WorkflowUser getWorkflowUser() throws KEWUserNotFoundException {
        if (isUsingWorkflowUser()) {
            return ((UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE)).getWorkflowUser(new WorkflowUserId(ruleResponsibilityName));
        }
        return null;
    }

    public Workgroup getWorkgroup() throws KEWUserNotFoundException {
        if (isUsingWorkgroup()) {
            return ((WorkgroupService) KEWServiceLocator.getService(KEWServiceLocator.WORKGROUP_SRV)).getWorkgroup(new WorkflowGroupId(new Long(ruleResponsibilityName)));
        }
        return null;
    }

    public String getRole() {
        if (isUsingRole()) {
            return ruleResponsibilityName;
        }
        return null;
    }

    public String getResolvedRoleName() {
        if (isUsingRole()) {
            return getRole().substring(getRole().indexOf("!") + 1, getRole().length());
        }
        return null;
    }

    public String getRoleAttributeName() {
	return getRole().substring(0, getRole().indexOf("!"));
    }
    
    public RoleAttribute resolveRoleAttribute() throws WorkflowException {
        if (isUsingRole()) {
            String attributeName = getRoleAttributeName();
            return (RoleAttribute) GlobalResourceLoader.getResourceLoader().getObject(new ObjectDefinition(attributeName));
        }
        return null;
    }

    public boolean isUsingRole() {
    	return (ruleResponsibilityName != null && ruleResponsibilityType != null && ruleResponsibilityType.equals(KEWConstants.RULE_RESPONSIBILITY_ROLE_ID));
    }

    public boolean isUsingWorkflowUser() {
    	return (ruleResponsibilityName != null && !ruleResponsibilityName.trim().equals("") && ruleResponsibilityType != null && ruleResponsibilityType.equals(KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID));
    }

    public boolean isUsingWorkgroup() {
    	return (ruleResponsibilityName != null && !ruleResponsibilityName.trim().equals("") && ruleResponsibilityType != null && ruleResponsibilityType.equals(KEWConstants.RULE_RESPONSIBILITY_WORKGROUP_ID));
    }

    public Long getRuleBaseValuesId() {
        return ruleBaseValuesId;
    }

    public void setRuleBaseValuesId(Long ruleBaseValuesId) {
        this.ruleBaseValuesId = ruleBaseValuesId;
    }

    public RuleBaseValues getRuleBaseValues() {
        return ruleBaseValues;
    }

    public void setRuleBaseValues(RuleBaseValues ruleBaseValues) {
        this.ruleBaseValues = ruleBaseValues;
    }

    public String getActionRequestedCd() {
        return actionRequestedCd;
    }

    public void setActionRequestedCd(String actionRequestedCd) {
        this.actionRequestedCd = actionRequestedCd;
    }

    public Integer getLockVerNbr() {
        return lockVerNbr;
    }

    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }

    public Long getRuleResponsibilityKey() {
        return ruleResponsibilityKey;
    }

    public void setRuleResponsibilityKey(Long ruleResponsibilityId) {
        this.ruleResponsibilityKey = ruleResponsibilityId;
    }
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getApprovePolicy() {
        return approvePolicy;
    }

    public void setApprovePolicy(String approvePolicy) {
        this.approvePolicy = approvePolicy;
    }

    public Object copy(boolean preserveKeys) {
        RuleResponsibility ruleResponsibilityClone = new RuleResponsibility();
        ruleResponsibilityClone.setApprovePolicy(getApprovePolicy());
        if (actionRequestedCd != null) {
            ruleResponsibilityClone.setActionRequestedCd(new String(actionRequestedCd));
        }
        if (ruleResponsibilityKey != null && preserveKeys) {
            ruleResponsibilityClone.setRuleResponsibilityKey(new Long(ruleResponsibilityKey.longValue()));
        }

        if (responsibilityId != null) {
            ruleResponsibilityClone.setResponsibilityId(new Long(responsibilityId.longValue()));
        }

        if (ruleResponsibilityName != null) {
            ruleResponsibilityClone.setRuleResponsibilityName(new String(ruleResponsibilityName));
        }
        if (ruleResponsibilityType != null) {
            ruleResponsibilityClone.setRuleResponsibilityType(new String(ruleResponsibilityType));
        }
        if (priority != null) {
            ruleResponsibilityClone.setPriority(new Integer(priority.intValue()));
        }
        if (delegationRules != null) {
            for (Iterator iter = delegationRules.iterator(); iter.hasNext();) {
                RuleDelegation delegation = (RuleDelegation) iter.next();
                RuleDelegation delegationClone = (RuleDelegation)delegation.copy(preserveKeys);
                delegationClone.setRuleResponsibility(ruleResponsibilityClone);
                ruleResponsibilityClone.getDelegationRules().add(delegationClone);

            }
        }
        return ruleResponsibilityClone;
    }

    public String getRuleResponsibilityName() {
        return ruleResponsibilityName;
    }

    public void setRuleResponsibilityName(String ruleResponsibilityName) {
        this.ruleResponsibilityName = ruleResponsibilityName;
    }

    public String getRuleResponsibilityType() {
        return ruleResponsibilityType;
    }

    public void setRuleResponsibilityType(String ruleResponsibilityType) {
        this.ruleResponsibilityType = ruleResponsibilityType;
    }

    public Long getResponsibilityId() {
        return responsibilityId;
    }
    public void setResponsibilityId(Long responsibilityId) {
        this.responsibilityId = responsibilityId;
    }
    public boolean isDelegating() {
        return !getDelegationRules().isEmpty();
    }

    public List getDelegationRules() {
        return delegationRules;
    }
    public void setDelegationRules(List delegationRules) {
        this.delegationRules = delegationRules;
    }

    public RuleDelegation getDelegationRule(int index) {
        while (getDelegationRules().size() <= index) {
            RuleDelegation ruleDelegation = new RuleDelegation();
            ruleDelegation.setRuleResponsibility(this);
            ruleDelegation.setDelegationRuleBaseValues(new RuleBaseValues());
            getDelegationRules().add(ruleDelegation);
        }
        return (RuleDelegation) getDelegationRules().get(index);
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof RuleResponsibility)) return false;
        RuleResponsibility pred = (RuleResponsibility) o;
        return Utilities.equals(ruleResponsibilityName, pred.getRuleResponsibilityName()) &&
               Utilities.equals(actionRequestedCd, pred.getActionRequestedCd()) &&
               Utilities.equals(priority, pred.getPriority()) &&
               Utilities.equals(approvePolicy, pred.getApprovePolicy());
    }

    public String toString() {
        return "[RuleResponsibility:"
               +  " responsibilityId=" + responsibilityId
               + ", ruleResponsibilityKey=" + ruleResponsibilityKey
               + ", ruleResponsibilityName=" + ruleResponsibilityName
               + ", ruleResponsibilityType=" + ruleResponsibilityType
               + ", ruleBaseValuesId=" + ruleBaseValuesId
               + ", actionRequestedCd=" + actionRequestedCd
               + ", priority=" + priority
               + ", approvePolicy=" + approvePolicy
               + ", lockVerNbr=" + lockVerNbr
               + "]";
    }
}
