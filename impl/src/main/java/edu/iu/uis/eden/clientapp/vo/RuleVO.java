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

import java.util.Collection;
import java.util.Iterator;

/**
 * @workflow.webservice-object
 */
public class RuleVO implements java.io.Serializable {
    
    private static final long serialVersionUID = -6559003022586974704L;

    private Long ruleTemplateId;
    private Boolean activeInd;
    private String description;
    private String docTypeName;
    private String fromDate;
    private String toDate;
    private Boolean ignorePrevious;
    private RuleResponsibilityVO[] ruleResponsibilities;
    private RuleExtensionVO[] ruleExtensions;
    private String ruleTemplateName;
    
    public RuleVO() {}

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

    public Boolean getIgnorePrevious() {
        return ignorePrevious;
    }

    public void setIgnorePrevious(Boolean ignorePrevious) {
        this.ignorePrevious = ignorePrevious;
    }

    public RuleExtensionVO[] getRuleExtensions() {
        return ruleExtensions;
    }

    public void setRuleExtensions(RuleExtensionVO[] ruleExtensions) {
        this.ruleExtensions = ruleExtensions;
    }

    public void addRuleExtensions(Collection<RuleExtensionVO> additionalRuleExtensions) {
        if (getRuleExtensions() == null) {
            setRuleExtensions(new RuleExtensionVO[0]);
        }
        RuleExtensionVO[] newRuleExtensions = new RuleExtensionVO[getRuleExtensions().length+additionalRuleExtensions.size()];
        System.arraycopy(getRuleExtensions(), 0, newRuleExtensions, 0, getRuleExtensions().length);
        int i = getRuleExtensions().length;
        for (Iterator iter = additionalRuleExtensions.iterator(); iter.hasNext();) {
            RuleExtensionVO ruleExtension = (RuleExtensionVO) iter.next();
            newRuleExtensions[i] = ruleExtension;
            i++;
        }
        setRuleExtensions(newRuleExtensions);
    }
    
    public RuleResponsibilityVO[] getRuleResponsibilities() {
        return ruleResponsibilities;
    }

    public void setRuleResponsibilities(RuleResponsibilityVO[] ruleResponsibilities) {
        this.ruleResponsibilities = ruleResponsibilities;
    }

    public void addRuleResponsibility(RuleResponsibilityVO ruleResponsibility) {
        if (getRuleResponsibilities() == null) {
            setRuleResponsibilities(new RuleResponsibilityVO[0]);
        }
        RuleResponsibilityVO[] newRuleResponsibilities = new RuleResponsibilityVO[getRuleResponsibilities().length+1];
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