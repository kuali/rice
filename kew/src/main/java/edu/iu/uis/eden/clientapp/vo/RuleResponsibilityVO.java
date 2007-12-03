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
package edu.iu.uis.eden.clientapp.vo;

/**
 * @workflow.webservice-object
 */
public class RuleResponsibilityVO implements java.io.Serializable {

    private static final long serialVersionUID = -5253556415865901457L;

    private Long responsibilityId;
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
    private UserVO user;
    private WorkgroupVO workgroup;
    private String roleName;
    
    private RuleDelegationVO[] delegationRules;
    
    public RuleResponsibilityVO() {}

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

    public RuleDelegationVO[] getDelegationRules() {
        return delegationRules;
    }

    public void setDelegationRules(RuleDelegationVO[] delegationRules) {
        this.delegationRules = delegationRules;
    }
    
    public void addDelegationRule(RuleDelegationVO delegationRule) {
        if (getDelegationRules() == null) {
            setDelegationRules(new RuleDelegationVO[0]);
        }
        RuleDelegationVO[] newDelegationRules = new RuleDelegationVO[getDelegationRules().length+1];
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

    public Long getResponsibilityId() {
        return responsibilityId;
    }

    public void setResponsibilityId(Long responsibilityId) {
        this.responsibilityId = responsibilityId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public UserVO getUser() {
        return user;
    }

    public void setUser(UserVO user) {
        this.user = user;
    }

    public WorkgroupVO getWorkgroup() {
        return workgroup;
    }

    public void setWorkgroup(WorkgroupVO workgroup) {
        this.workgroup = workgroup;
    }

}
