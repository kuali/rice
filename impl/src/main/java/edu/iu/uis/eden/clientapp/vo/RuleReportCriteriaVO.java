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
public class RuleReportCriteriaVO implements java.io.Serializable {
    
    private static final long serialVersionUID = -5778071030243010078L;

    private String ruleDescription;
    private String documentTypeName;
    private String ruleTemplateName;
    private String[] actionRequestCodes;
    private UserIdVO responsibleUser;
    private WorkgroupIdVO responsibleWorkgroup;
    private String responsibleRoleName;
    private RuleExtensionVO[] ruleExtensionVOs;
    private Boolean activeIndicator;
    private Boolean considerWorkgroupMembership = Boolean.TRUE;
    private Boolean includeDelegations;
    
    public RuleReportCriteriaVO() {}

    public String[] getActionRequestCodes() {
        return actionRequestCodes;
    }

    public void setActionRequestCodes(String[] actionRequestCodes) {
        this.actionRequestCodes = actionRequestCodes;
    }

    public Boolean isActiveIndicator() {
        return activeIndicator;
    }

    public void setActiveIndicator(Boolean activeIndicator) {
        this.activeIndicator = activeIndicator;
    }

    public Boolean isConsiderWorkgroupMembership() {
        return considerWorkgroupMembership;
    }

    public void setConsiderWorkgroupMembership(Boolean considerWorkgroupMembership) {
        this.considerWorkgroupMembership = considerWorkgroupMembership;
    }

    public String getDocumentTypeName() {
        return documentTypeName;
    }

    public void setDocumentTypeName(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }

    public Boolean isIncludeDelegations() {
        return includeDelegations;
    }

    public void setIncludeDelegations(Boolean includeDelegations) {
        this.includeDelegations = includeDelegations;
    }

    public String getResponsibleRoleName() {
        return responsibleRoleName;
    }

    public void setResponsibleRoleName(String responsibleRoleName) {
        this.responsibleRoleName = responsibleRoleName;
    }

    public UserIdVO getResponsibleUser() {
        return responsibleUser;
    }

    public void setResponsibleUser(UserIdVO responsibleUser) {
        this.responsibleUser = responsibleUser;
    }

    public WorkgroupIdVO getResponsibleWorkgroup() {
        return responsibleWorkgroup;
    }

    public void setResponsibleWorkgroup(WorkgroupIdVO responsibleWorkgroup) {
        this.responsibleWorkgroup = responsibleWorkgroup;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    public RuleExtensionVO[] getRuleExtensionVOs() {
        return ruleExtensionVOs;
    }

    public void setRuleExtensionVOs(RuleExtensionVO[] ruleExtensionVOs) {
        this.ruleExtensionVOs = ruleExtensionVOs;
    }

    public String getRuleTemplateName() {
        return ruleTemplateName;
    }

    public void setRuleTemplateName(String ruleTemplateName) {
        this.ruleTemplateName = ruleTemplateName;
    }

}