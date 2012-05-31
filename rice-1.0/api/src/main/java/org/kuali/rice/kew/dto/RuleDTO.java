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

import java.util.Collection;
import java.util.Iterator;

public class RuleDTO implements java.io.Serializable {
    
    private static final long serialVersionUID = -6559003022586974704L;

    private Long ruleTemplateId;
    private Boolean activeInd;
    private String description;
    private String docTypeName;
    private String fromDate;
    private String toDate;
    private Boolean forceAction;
    private RuleResponsibilityDTO[] ruleResponsibilities;
    private RuleExtensionDTO[] ruleExtensions;
    private String ruleTemplateName;
    
    public RuleDTO() {}

    public Boolean getActiveInd() {
        return activeInd;
    }

    public void setActiveInd(Boolean activeInd) {
        this.activeInd = activeInd;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocTypeName() {
        return docTypeName;
    }

    public void setDocTypeName(String docTypeName) {
        this.docTypeName = docTypeName;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public Boolean getForceAction() {
        return forceAction;
    }

    public void setForceAction(Boolean forceAction) {
        this.forceAction = forceAction;
    }

    public RuleExtensionDTO[] getRuleExtensions() {
        return ruleExtensions;
    }

    public void setRuleExtensions(RuleExtensionDTO[] ruleExtensions) {
        this.ruleExtensions = ruleExtensions;
    }

    public void addRuleExtensions(Collection<RuleExtensionDTO> additionalRuleExtensions) {
        if (getRuleExtensions() == null) {
            setRuleExtensions(new RuleExtensionDTO[0]);
        }
        RuleExtensionDTO[] newRuleExtensions = new RuleExtensionDTO[getRuleExtensions().length+additionalRuleExtensions.size()];
        System.arraycopy(getRuleExtensions(), 0, newRuleExtensions, 0, getRuleExtensions().length);
        int i = getRuleExtensions().length;
        for (Iterator iter = additionalRuleExtensions.iterator(); iter.hasNext();) {
            RuleExtensionDTO ruleExtension = (RuleExtensionDTO) iter.next();
            newRuleExtensions[i] = ruleExtension;
            i++;
        }
        setRuleExtensions(newRuleExtensions);
    }
    
    public RuleResponsibilityDTO[] getRuleResponsibilities() {
        return ruleResponsibilities;
    }

    public void setRuleResponsibilities(RuleResponsibilityDTO[] ruleResponsibilities) {
        this.ruleResponsibilities = ruleResponsibilities;
    }

    public void addRuleResponsibility(RuleResponsibilityDTO ruleResponsibility) {
        if (getRuleResponsibilities() == null) {
            setRuleResponsibilities(new RuleResponsibilityDTO[0]);
        }
        RuleResponsibilityDTO[] newRuleResponsibilities = new RuleResponsibilityDTO[getRuleResponsibilities().length+1];
        System.arraycopy(getRuleResponsibilities(), 0, newRuleResponsibilities, 0, getRuleResponsibilities().length);
        newRuleResponsibilities[getRuleResponsibilities().length] = ruleResponsibility;
        setRuleResponsibilities(newRuleResponsibilities);
    }
    
    public Long getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(Long ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }

    public String getRuleTemplateName() {
        return ruleTemplateName;
    }

    public void setRuleTemplateName(String ruleTemplateName) {
        this.ruleTemplateName = ruleTemplateName;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
    
    
}
