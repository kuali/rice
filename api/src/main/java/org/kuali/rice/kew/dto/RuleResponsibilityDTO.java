/*
 * Copyright 2005-2008 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.dto;

public class RuleResponsibilityDTO implements java.io.Serializable {

    private static final long serialVersionUID = -5253556415865901457L;

    private String responsibilityId;
    private String actionRequestedCd;
    private Integer priority;
    private String approvePolicy;
/* 
 *  Below we have replaced the two fields below with
 *  specific fields for what could be associated with
 *  this rule responsibility:
 *  
 *  ruleResponsibilityName
 *  ruleResponsibilityType
 */
    private String principalId;
    private String groupId;
    private String roleName;
    
    private RuleDelegationDTO[] delegationRules;
    
    public RuleResponsibilityDTO() {}

    public String getActionRequestedCd() {
        return actionRequestedCd;
    }

    public void setActionRequestedCd(String actionRequestedCd) {
        this.actionRequestedCd = actionRequestedCd;
    }

    public String getApprovePolicy() {
        return approvePolicy;
    }

    public void setApprovePolicy(String approvePolicy) {
        this.approvePolicy = approvePolicy;
    }

    public RuleDelegationDTO[] getDelegationRules() {
        return delegationRules;
    }

    public void setDelegationRules(RuleDelegationDTO[] delegationRules) {
        this.delegationRules = delegationRules;
    }
    
    public void addDelegationRule(RuleDelegationDTO delegationRule) {
        if (getDelegationRules() == null) {
            setDelegationRules(new RuleDelegationDTO[0]);
        }
        RuleDelegationDTO[] newDelegationRules = new RuleDelegationDTO[getDelegationRules().length+1];
        System.arraycopy(getDelegationRules(), 0, newDelegationRules, 0, getDelegationRules().length);
        newDelegationRules[getDelegationRules().length] = delegationRule;
        setDelegationRules(newDelegationRules);
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getResponsibilityId() {
        return responsibilityId;
    }

    public void setResponsibilityId(String responsibilityId) {
        this.responsibilityId = responsibilityId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

}
