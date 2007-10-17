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
package edu.iu.uis.eden.routetemplate.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.routetemplate.MyRules2;
import edu.iu.uis.eden.routetemplate.RuleDelegation;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.ShowHideTree;
import edu.iu.uis.eden.web.WorkflowRoutingForm;

/**
 * Struts ActionForm for {@link Rule2Action}.
 *
 * @see Rule2Action
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Rule2Form extends WorkflowRoutingForm {

	private static final long serialVersionUID = 5412969516727713859L;

    private ShowHideTree showHide = new ShowHideTree();
    private ShowHideTree parentShowHide = new ShowHideTree();
    private Map showDelegationsMap = new HashMap();
    
    private Long currentRuleId;
    private Integer ruleIndex;
    private Integer responsibilityIndex;
    private Integer delegationIndex;
    private Integer delegationResponsibilityIndex;
    private String extraId = "";
    private List ruleTemplates;
    private List priorities;
    private Map actionRequestCodes;
    private Map approvePolicyCodes;
    private String lookupableImplServiceName;
    private boolean choosingTemplate;
    private String methodToCall = "";
    private boolean editingDelegate = false;
    
    private List rules;
    private MyRules2 myRules;
    private Map attributeLabels;
    private String instructionForCreateNew;
    private String instructionForGlobalReviewerReplace;
    private String conversionFields;
    private int delegationLimit;
    private String lookupType;
    private String delegationSearchOnly;
    
    private RuleCreationValues ruleCreationValues = new RuleCreationValues();
    private WebRuleBaseValues parentRule = new WebRuleBaseValues();
    private WebRuleResponsibility responsibility = new WebRuleResponsibility();
    private RuleDelegation ruleDelegation = new RuleDelegation();
    
    private String forward = "basic";
    
    /**
     * added on 2006-04-04 to support: 
     * 		1. on rule report page, showing link to document type report page
     */
    private Long docTypeId;

    public Rule2Form() {
        delegationLimit = Integer.parseInt(Utilities.getApplicationConstant(EdenConstants.RULE_DELEGATE_LIMIT_KEY));
        instructionForCreateNew = Utilities.getApplicationConstant(EdenConstants.RULE_CREATE_NEW_INSTRUCTION_KEY);
        instructionForGlobalReviewerReplace = Utilities.getApplicationConstant(EdenConstants.GLOBAL_REVIEWER_REPLACE_INSTRUCTION_KEY);
        reset();
    }

    public void reset() {
        this.myRules = new MyRules2();
        this.ruleCreationValues = new RuleCreationValues();
    }

    public Long getCurrentRuleId() {
        return currentRuleId;
    }

    public void setCurrentRuleId(Long currentRuleId) {
        this.currentRuleId = currentRuleId;
    }

    public String getMethodToCall() {
        return methodToCall;
    }

    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }

    public boolean isChoosingTemplate() {
        return choosingTemplate;
    }

    public void setChoosingTemplate(boolean choosingTemplate) {
        this.choosingTemplate = choosingTemplate;
    }

    public String getLookupableImplServiceName() {
        return lookupableImplServiceName;
    }

    public void setLookupableImplServiceName(String lookupableImplServiceName) {
        this.lookupableImplServiceName = lookupableImplServiceName;
    }

    public Map getActionRequestCodes() {
        return actionRequestCodes;
    }

    public void setActionRequestCodes(Map actionRequestCodes) {
        this.actionRequestCodes = actionRequestCodes;
    }

    public List getPriorities() {
        return priorities;
    }

    public void setPriorities(List priorities) {
        this.priorities = priorities;
    }

    public List getRuleTemplates() {
        return ruleTemplates;
    }

    public void setRuleTemplates(List ruleTemplates) {
        this.ruleTemplates = ruleTemplates;
    }

    public List getRules() {
        return rules;
    }

    public void setRules(List rules) {
        this.rules = rules;
    }
    
    public MyRules2 getMyRules() {
        return myRules;
    }

    public void setMyRules(MyRules2 myRules) {
        this.myRules = myRules;
    }

    public Map getAttributeLabels() {
        return attributeLabels;
    }

    public void setAttributeLabels(Map attributeLabels) {
        this.attributeLabels = attributeLabels;
    }

    public String getInstructionForCreateNew() {
        return instructionForCreateNew;
    }

    public void setInstructionForCreateNew(String instructionForCreateNew) {
        this.instructionForCreateNew = instructionForCreateNew;
    }

    public String getInstructionForGlobalReviewerReplace() {
        return instructionForGlobalReviewerReplace;
    }

    public void setInstructionForGlobalReviewerReplace(String instructionForGlobalReviewerReplace) {
        this.instructionForGlobalReviewerReplace = instructionForGlobalReviewerReplace;
    }

    public Integer getDelegationIndex() {
        return delegationIndex;
    }

    public void setDelegationIndex(Integer delegationIndex) {
        this.delegationIndex = delegationIndex;
    }

    public Integer getDelegationResponsibilityIndex() {
        return delegationResponsibilityIndex;
    }

    public void setDelegationResponsibilityIndex(Integer delegationResponsibilityIndex) {
        this.delegationResponsibilityIndex = delegationResponsibilityIndex;
    }

    public Integer getResponsibilityIndex() {
        return responsibilityIndex;
    }

    public void setResponsibilityIndex(Integer responsibilityIndex) {
        this.responsibilityIndex = responsibilityIndex;
    }

    public Integer getRuleIndex() {
        return ruleIndex;
    }

    public void setRuleIndex(Integer ruleIndex) {
        this.ruleIndex = ruleIndex;
    }

    public RuleCreationValues getRuleCreationValues() {
        return ruleCreationValues;
    }

    public void setRuleCreationValues(RuleCreationValues ruleCreationValues) {
        this.ruleCreationValues = ruleCreationValues;
    }

    public Map getApprovePolicyCodes() {
        return approvePolicyCodes;
    }
    
    public void setApprovePolicyCodes(Map approvePolicyCodes) {
        this.approvePolicyCodes = approvePolicyCodes;
    }
    
    public void setDocTypeFullName(String docTypeFullName) {
        this.ruleCreationValues.setDocTypeName(docTypeFullName);
    }
    
    public WebRuleBaseValues getParentRule() {
        return parentRule;
    }
    public void setParentRule(WebRuleBaseValues parentRule) {
        this.parentRule = parentRule;
    }
    public class RuleCreationValues implements java.io.Serializable {
        /**
		 * 
		 */
		private static final long serialVersionUID = -8211316354702964152L;
		private Long ruleId;
        private Long ruleTemplateId;
        private String ruleTemplateName;
        private String docTypeName;
        private Long ruleResponsibilityKey;
        private boolean creating;
        private boolean manualDelegationTemplate;

        public Long getRuleId() {
            return ruleId;
        }
        
        public void setRuleId(Long ruleId) {
            this.ruleId = ruleId;
        }
        
        public String getDocTypeName() {
            return docTypeName;
        }

        public void setDocTypeName(String docTypeName) {
            this.docTypeName = docTypeName;
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
        public boolean isCreating() {
            return creating;
        }
        public void setCreating(boolean creating) {
            this.creating = creating;
        }
        
        public Long getRuleResponsibilityKey() {
            return ruleResponsibilityKey;
        }
        public void setRuleResponsibilityKey(Long ruleResponsibilityKey) {
            this.ruleResponsibilityKey = ruleResponsibilityKey;
        }
        public boolean isManualDelegationTemplate() {
            return manualDelegationTemplate;
        }
        public void setManualDelegationTemplate(boolean manualDelegationTemplate) {
            this.manualDelegationTemplate = manualDelegationTemplate;
        }
    }

    public String getConversionFields() {
        return conversionFields;
    }

    public void setConversionFields(String conversionFields) {
        this.conversionFields = conversionFields;
    }

    public boolean isEditingDelegate() {
        return editingDelegate;
    }

    public void setEditingDelegate(boolean editingDelegate) {
        this.editingDelegate = editingDelegate;
    }
    public WebRuleResponsibility getResponsibility() {
        return responsibility;
    }
    public void setResponsibility(WebRuleResponsibility responsibility) {
        this.responsibility = responsibility;
    }
    
    public RuleDelegation getRuleDelegation() {
        return ruleDelegation;
    }
    public void setRuleDelegation(RuleDelegation ruleDelegation) {
        this.ruleDelegation = ruleDelegation;
    }
    
    public ShowHideTree getShowHide() {
        return showHide;
    }
    public void setShowHide(ShowHideTree showHide) {
        this.showHide = showHide;
    }
    public ShowHideTree getParentShowHide() {
        return parentShowHide;
    }
    public void setParentShowHide(ShowHideTree parentShowHide) {
        this.parentShowHide = parentShowHide;
    }
    public String getRouteLogPopup() {
        return Utilities.getApplicationConstant(EdenConstants.RULE_ROUTE_LOG_POPUP_KEY).trim();
    }
    
    public String getForward() {
        return forward;
    }
    public void setForward(String forward) {
        this.forward = forward;
    }
    
    public String getExtraId() {
        return extraId;
    }
    public void setExtraId(String extraId) {
        this.extraId = extraId;
    }
    
    public Map getShowDelegationsMap() {
        return showDelegationsMap;
    }
    public void setShowDelegationsMap(Map showDelegationsMap) {
        this.showDelegationsMap = showDelegationsMap;
    }
    public Boolean getShowDelegations(String key) {
        return (Boolean)getShowDelegationsMap().get(key);
    }
    public int getDelegationLimit() {
        return delegationLimit;
    }
    public void setDelegationLimit(int delegationLimit) {
        this.delegationLimit = delegationLimit;
    }
    public String getLookupType() {
        return lookupType;
    }
    public void setLookupType(String lookupType) {
        this.lookupType = lookupType;
    }
    public String getDelegationSearchOnly() {
        return delegationSearchOnly;
    }
    public void setDelegationSearchOnly(String delegationSearchOnly) {
        this.delegationSearchOnly = delegationSearchOnly;
    }
	public Long getDocTypeId() {
		return docTypeId;
	}
	public void setDocTypeId(Long docTypeId) {
		this.docTypeId = docTypeId;
	}
    
    
}