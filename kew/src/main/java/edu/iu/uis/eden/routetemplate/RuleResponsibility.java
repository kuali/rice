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
package edu.iu.uis.eden.routetemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowPersistable;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.plugin.attributes.RoleAttribute;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;
import edu.iu.uis.eden.workgroup.Workgroup;
import edu.iu.uis.eden.workgroup.WorkgroupService;

/**
 * A model bean representing the responsibility of a user, workgroup, or role
 * to perform some action on a document.  Used by the rule system to
 * identify the appropriate responsibile parties to generate
 * {@link ActionRequestValue}s to.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleResponsibility implements WorkflowPersistable {

	private static final long serialVersionUID = -1565688857123316797L;
	private Long ruleResponsibilityKey;
    private Long responsibilityId;
    private Long ruleBaseValuesId;
    private String actionRequestedCd;
    private String ruleResponsibilityName;
    private String ruleResponsibilityType;
    private Integer lockVerNbr;
    private Integer priority;
    private String approvePolicy;

    private RuleBaseValues ruleBaseValues;
    private List delegationRules = new ArrayList();

    public WorkflowUser getWorkflowUser() throws EdenUserNotFoundException {
        if (isUsingWorkflowUser()) {
            return ((UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE)).getWorkflowUser(new WorkflowUserId(ruleResponsibilityName));
        }
        return null;
    }

    public Workgroup getWorkgroup() throws EdenUserNotFoundException {
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

    public RoleAttribute resolveRoleAttribute() throws WorkflowException {
        if (isUsingRole()) {
            String attributeName = getRole().substring(0, getRole().indexOf("!"));
            return (RoleAttribute) GlobalResourceLoader.getResourceLoader().getObject(new ObjectDefinition(attributeName));
        }
        return null;
    }

    public boolean isUsingRole() {
    	return (ruleResponsibilityName != null && ruleResponsibilityType != null && ruleResponsibilityType.equals(EdenConstants.RULE_RESPONSIBILITY_ROLE_ID));
    }

    public boolean isUsingWorkflowUser() {
    	return (ruleResponsibilityName != null && !ruleResponsibilityName.trim().equals("") && ruleResponsibilityType != null && ruleResponsibilityType.equals(EdenConstants.RULE_RESPONSIBILITY_WORKFLOW_ID));
    }

    public boolean isUsingWorkgroup() {
    	return (ruleResponsibilityName != null && !ruleResponsibilityName.trim().equals("") && ruleResponsibilityType != null && ruleResponsibilityType.equals(EdenConstants.RULE_RESPONSIBILITY_WORKGROUP_ID));
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