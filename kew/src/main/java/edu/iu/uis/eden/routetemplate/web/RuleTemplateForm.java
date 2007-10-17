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

import java.util.List;
import java.util.Map;

import org.apache.struts.action.ActionForm;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.routetemplate.RuleDelegation;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.routetemplate.RuleTemplateAttribute;
import edu.iu.uis.eden.util.Utilities;

/**
 * A Struts ActionForm for the {@link RuleTemplateAction}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleTemplateForm extends ActionForm {

	private static final long serialVersionUID = -3540750513624610628L;
	private RuleTemplate ruleTemplate;
    private RuleTemplateAttribute ruleTemplateAttribute;
    private List ruleAttributes;
    private Integer removeAttribute;
    private Integer editAttribute;
    private Integer moveAttribute;
    private int ruleTemplateAttributeSize;
    private String ruleAttributeName;
    private String methodToCall = "";
    private String instructionForCreateNew;
    private String delegationTemplateName;
    private String lookupableImplServiceName;
    private String conversionFields;
    private Long currentRuleTemplateId;
    private WebRuleBaseValues rule = new WebRuleBaseValues();
    private RuleDelegation ruleDelegation;
    private Map actionRequestCodes;
    
    private boolean createDefaultRule;
    private boolean createDefaultDelegateRule;

    public RuleTemplateForm() {
        this.ruleDelegation = new RuleDelegation();
        this.ruleTemplate = new RuleTemplate();
        this.ruleTemplateAttribute = new RuleTemplateAttribute();
        methodToCall = "";
        instructionForCreateNew = Utilities.getApplicationConstant(EdenConstants.RULE_TEMPLATE_CREATE_NEW_INSTRUCTION_KEY);
    }

    public Integer getEditAttribute() {
        return editAttribute;
    }

    public void setEditAttribute(Integer editAttribute) {
        this.editAttribute = editAttribute;
    }

    public Integer getRemoveAttribute() {
        return removeAttribute;
    }

    public void setRemoveAttribute(Integer removeAttribute) {
        this.removeAttribute = removeAttribute;
    }

    public List getRuleAttributes() {
        return ruleAttributes;
    }

    public void setRuleAttributes(List ruleAttributes) {
        this.ruleAttributes = ruleAttributes;
    }

    public RuleTemplate getRuleTemplate() {
        return ruleTemplate;
    }

    public void setRuleTemplate(RuleTemplate ruleTemplate) {
        this.ruleTemplate = ruleTemplate;
    }

    public RuleTemplateAttribute getRuleTemplateAttribute() {
        return ruleTemplateAttribute;
    }

    public void setRuleTemplateAttribute(RuleTemplateAttribute ruleTemplateAttribute) {
        this.ruleTemplateAttribute = ruleTemplateAttribute;
    }

    /**
     * @return Returns the ruleTemplateAttributeSize.
     */
    public int getRuleTemplateAttributeSize() {
        return ruleTemplateAttributeSize;
    }

    /**
     * @param ruleTemplateAttributeSize
     *            The ruleTemplateAttributeSize to set.
     */
    public void setRuleTemplateAttributeSize(int ruleTemplateAttributeSize) {
        this.ruleTemplateAttributeSize = ruleTemplateAttributeSize;
    }

    /**
     * @return Returns the moveAttribute.
     */
    public Integer getMoveAttribute() {
        return moveAttribute;
    }

    /**
     * @param moveAttribute
     *            The moveAttribute to set.
     */
    public void setMoveAttribute(Integer moveAttribute) {
        this.moveAttribute = moveAttribute;
    }

    public String getRuleAttributeName() {
        return ruleAttributeName;
    }

    public void setRuleAttributeName(String ruleAttributeName) {
        this.ruleAttributeName = ruleAttributeName;
    }
    public String getMethodToCall() {
        return methodToCall;
    }
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }
    
    public String getInstructionForCreateNew() {
        return instructionForCreateNew;
    }
    public void setInstructionForCreateNew(String instructionForCreateNew) {
        this.instructionForCreateNew = instructionForCreateNew;
    }
    
    public String getDelegationTemplateName() {
        return delegationTemplateName;
    }
    public void setDelegationTemplateName(String delegationTemplateName) {
        this.delegationTemplateName = delegationTemplateName;
    }
    
    public String getLookupableImplServiceName() {
        return lookupableImplServiceName;
    }
    public void setLookupableImplServiceName(String lookupableImplServiceName) {
        this.lookupableImplServiceName = lookupableImplServiceName;
    }
    
    public String getConversionFields() {
        return conversionFields;
    }
    public void setConversionFields(String conversionFields) {
        this.conversionFields = conversionFields;
    }
    public Long getCurrentRuleTemplateId() {
        return currentRuleTemplateId;
    }
    public void setCurrentRuleTemplateId(Long currentRuleTemplateId) {
        this.currentRuleTemplateId = currentRuleTemplateId;
    }
    public WebRuleBaseValues getRule() {
        return rule;
    }
    public void setRule(WebRuleBaseValues rule) {
        this.rule = rule;
    }
    public RuleDelegation getRuleDelegation() {
        return ruleDelegation;
    }
    public void setRuleDelegation(RuleDelegation ruleDelegation) {
        this.ruleDelegation = ruleDelegation;
    }
    public boolean isCreateDefaultRule() {
        return createDefaultRule;
    }
    public void setCreateDefaultRule(boolean createDefaultRule) {
        this.createDefaultRule = createDefaultRule;
    }
    public boolean isCreateDefaultDelegateRule() {
        return createDefaultDelegateRule;
    }
    public void setCreateDefaultDelegateRule(boolean createDefaultDelegateRule) {
        this.createDefaultDelegateRule = createDefaultDelegateRule;
    }
    public Map getActionRequestCodes() {
        return actionRequestCodes;
    }
    public void setActionRequestCodes(Map actionRequestCodes) {
        this.actionRequestCodes = actionRequestCodes;
    }
}