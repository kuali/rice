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

public class RuleReportCriteriaDTO implements java.io.Serializable {
    
    private static final long serialVersionUID = -5778071030243010078L;

    private String ruleDescription;
    private String documentTypeName;
    private String ruleTemplateName;
    private String[] actionRequestCodes;
    private String responsiblePrincipalId;
    private String responsibleGroupId;
    private String responsibleRoleName;
    private RuleExtensionDTO[] ruleExtensionVOs;
    private Boolean activeIndicator;
    private Boolean considerWorkgroupMembership = Boolean.TRUE;
    private Boolean includeDelegations;
    
    public RuleReportCriteriaDTO() {}

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

    public String getResponsiblePrincipalId() {
        return responsiblePrincipalId;
    }

    public void setResponsiblePrincipalId(String responsiblePrincipalId) {
        this.responsiblePrincipalId = responsiblePrincipalId;
    }

	public String getResponsibleGroupId() {
		return this.responsibleGroupId;
	}

	public void setResponsibleGroupId(String responsibleGroupId) {
		this.responsibleGroupId = responsibleGroupId;
	}

	public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    public RuleExtensionDTO[] getRuleExtensionVOs() {
        return ruleExtensionVOs;
    }

    public void setRuleExtensionVOs(RuleExtensionDTO[] ruleExtensionVOs) {
        this.ruleExtensionVOs = ruleExtensionVOs;
    }

    public String getRuleTemplateName() {
        return ruleTemplateName;
    }

    public void setRuleTemplateName(String ruleTemplateName) {
        this.ruleTemplateName = ruleTemplateName;
    }

}
