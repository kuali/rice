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

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.WorkflowPersistable;

/**
 * A model bean representing the delegation of a rule from a responsibility to
 * another rule.  Specifies the delegation type which can be either 
 * {@link EdenConstants#DELEGATION_PRIMARY} or {@link EdenConstants#DELEGATION_SECONDARY}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleDelegation implements WorkflowPersistable {
    
	private static final long serialVersionUID = 7989203310473741293L;
	private Long ruleDelegationId;
    private Long ruleResponsibilityId;
    private Long delegateRuleId;
    private String delegationType = EdenConstants.DELEGATION_PRIMARY;
    private Integer lockVerNbr;
    
    private RuleBaseValues delegationRuleBaseValues;
    private RuleResponsibility ruleResponsibility;
    
    public RuleDelegation() {
    }

    public Object copy(boolean preserveKeys) {
        RuleDelegation clone = new RuleDelegation();
        if (ruleDelegationId != null && preserveKeys) {
            clone.setRuleDelegationId(new Long(ruleDelegationId.longValue()));
        }
        clone.setDelegationRuleBaseValues(delegationRuleBaseValues);
        clone.setDelegateRuleId(delegationRuleBaseValues.getRuleBaseValuesId());
        if (delegationType != null) {
            clone.setDelegationType(new String(delegationType));
        }
        return clone;
    }

    public Long getDelegateRuleId() {
        return delegateRuleId;
    }
    public void setDelegateRuleId(Long delegateRuleId) {
        this.delegateRuleId = delegateRuleId;
    }
    public RuleBaseValues getDelegationRuleBaseValues() {
        return delegationRuleBaseValues;
    }
    public void setDelegationRuleBaseValues(RuleBaseValues delegationRuleBaseValues) {
        this.delegationRuleBaseValues = delegationRuleBaseValues;
    }
    public String getDelegationType() {
        return delegationType;
    }
    public void setDelegationType(String delegationType) {
        this.delegationType = delegationType;
    }
    public Integer getLockVerNbr() {
        return lockVerNbr;
    }
    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }
    public Long getRuleDelegationId() {
        return ruleDelegationId;
    }
    public void setRuleDelegationId(Long ruleDelegationId) {
        this.ruleDelegationId = ruleDelegationId;
    }
    public RuleResponsibility getRuleResponsibility() {
        return ruleResponsibility;
    }
    public void setRuleResponsibility(RuleResponsibility ruleResponsibility) {
        this.ruleResponsibility = ruleResponsibility;
    }
    public Long getRuleResponsibilityId() {
        return ruleResponsibilityId;
    }
    public void setRuleResponsibilityId(Long ruleResponsibilityId) {
        this.ruleResponsibilityId = ruleResponsibilityId;
    }
}
