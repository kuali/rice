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
package org.kuali.rice.kew.rule.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocument;


/**
 * Some utilities which are utilized by the {@link Rule2Action}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WebRuleUtils {

	public static final String RULE_TEMPLATE_ID_PARAM = "ruleCreationValues.ruleTemplateId";
	public static final String RULE_TEMPLATE_NAME_PARAM = "ruleCreationValues.ruleTemplateName";
	public static final String DOCUMENT_TYPE_NAME_PARAM = "ruleCreationValues.docTypeName";
	public static final String RESPONSIBILITY_ID_PARAM = "ruleCreationValues.responsibilityId";
	
	/**
	 * Copies the existing rule onto the current document.  This is used within the web-based rule GUI to make a
	 * copy of a rule on the existing document.  Essentially, this method makes a copy of the rule and all
	 * delegates but preserves the document ID of the original rule.
	 */
    public static WebRuleBaseValues copyRuleOntoExistingDocument(WebRuleBaseValues rule) throws Exception {
        WebRuleBaseValues ruleCopy = new WebRuleBaseValues();
        PropertyUtils.copyProperties(ruleCopy, rule);
        ruleCopy.setPreviousVersionId(null);
        ruleCopy.setCurrentInd(null);
        ruleCopy.setVersionNbr(null);

        List responsibilities = new ArrayList();
        for (Iterator iter = ruleCopy.getResponsibilities().iterator(); iter.hasNext();) {
            WebRuleResponsibility responsibility = (WebRuleResponsibility) iter.next();
            WebRuleResponsibility responsibilityCopy = new WebRuleResponsibility();
            PropertyUtils.copyProperties(responsibilityCopy, responsibility);

            responsibilityCopy.setResponsibilityId(null);
            responsibilityCopy.setRuleResponsibilityKey(null);
            
            List delegations = new ArrayList();
            for (Iterator iterator = responsibilityCopy.getDelegationRules().iterator(); iterator.hasNext();) {
                RuleDelegation delegation = (RuleDelegation) iterator.next();
                RuleDelegation delegationCopy = new RuleDelegation();
                PropertyUtils.copyProperties(delegationCopy, delegation);

                delegationCopy.setDelegateRuleId(null);
                delegationCopy.setVersionNumber(null);
                delegationCopy.setRuleDelegationId(null);
                delegationCopy.setResponsibilityId(null);

                WebRuleBaseValues delegationRule = ((WebRuleBaseValues) delegation.getDelegationRuleBaseValues());
                WebRuleBaseValues ruleDelegateCopy = new WebRuleBaseValues();
                PropertyUtils.copyProperties(ruleDelegateCopy, delegationRule);

                ruleDelegateCopy.setPreviousVersionId(null);
                ruleDelegateCopy.setCurrentInd(null);
                ruleDelegateCopy.setVersionNbr(null);

                List delegateResps = new ArrayList();
                for (Iterator iterator1 = ruleDelegateCopy.getResponsibilities().iterator(); iterator1.hasNext();) {
                    WebRuleResponsibility delegateResp = (WebRuleResponsibility) iterator1.next();
                    WebRuleResponsibility delegateRespCopy = new WebRuleResponsibility();
                    PropertyUtils.copyProperties(delegateRespCopy, delegateResp);

                    delegateRespCopy.setResponsibilityId(null);
                    delegateRespCopy.setRuleResponsibilityKey(null);
                    delegateResps.add(delegateRespCopy);
                }
                ruleDelegateCopy.setResponsibilities(delegateResps);
                delegationCopy.setDelegationRuleBaseValues(ruleDelegateCopy);
                delegations.add(delegationCopy);
            }
            //responsibilityCopy.setDelegationRules(delegations);
            responsibilities.add(responsibilityCopy);
        }
        ruleCopy.setResponsibilities(responsibilities);
        return ruleCopy;
    }
    
    /**
     * Makes a copy of the rule and clears the document id on the rule and any of its delegates.
     * This method is used for making a copy of a rule for a new document.  It essentially calls
     * the copyRuleOntoExistingDocument method and then clears out the document IDs.
     * 
     * @param webRuleBaseValues
     */
    public static WebRuleBaseValues copyToNewRule(WebRuleBaseValues webRuleBaseValues) throws Exception {
    	WebRuleBaseValues newRule = copyRuleOntoExistingDocument(webRuleBaseValues);
    	// clear out all document IDs on the rule and it's delegates
    	newRule.setRouteHeaderId(null);
    	for (Iterator iterator = newRule.getResponsibilities().iterator(); iterator.hasNext(); ) {
			RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
			for (Iterator iterator2 = responsibility.getDelegationRules().iterator(); iterator2.hasNext(); ) {
				RuleDelegation delegation = (RuleDelegation) iterator2.next();
				delegation.getDelegationRuleBaseValues().setRouteHeaderId(null);
			}
		}
    	return newRule;
    }

    public static void validateRuleTemplateAndDocumentType(RuleBaseValues oldRule, RuleBaseValues newRule, Map<String, String[]> parameters) {
		String[] ruleTemplateIds = parameters.get(RULE_TEMPLATE_ID_PARAM);
		String[] ruleTemplateNames = parameters.get(RULE_TEMPLATE_NAME_PARAM);
		String[] documentTypeNames = parameters.get(DOCUMENT_TYPE_NAME_PARAM);
		if (ArrayUtils.isEmpty(ruleTemplateIds) && ArrayUtils.isEmpty(ruleTemplateNames)) {
			throw new RiceRuntimeException("Rule document must be initiated with a valid rule template id or rule template name.");
		}
		if (ArrayUtils.isEmpty(documentTypeNames)) {
			throw new RiceRuntimeException("Rule document must be initiated with a valid document type name.");
		}
		RuleTemplate ruleTemplate = null;
		if (!ArrayUtils.isEmpty(ruleTemplateIds)) {
			String ruleTemplateId = ruleTemplateIds[0];
			ruleTemplate = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateId(new Long(ruleTemplateId));
			if (ruleTemplate == null) {
				throw new RiceRuntimeException("Failed to load rule template with id '" + ruleTemplateId + "'");
			}
		}
		if (ruleTemplate == null) {
			String ruleTemplateName = ruleTemplateNames[0];
			ruleTemplate = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(ruleTemplateName);
			if (ruleTemplate == null) {
				throw new RiceRuntimeException("Failed to load rule template with name '" + ruleTemplateName + "'");
			}
		}
		String documentTypeName = documentTypeNames[0];
		DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
		if (documentType == null) {
			throw new RiceRuntimeException("Failed to locate document type with name '" + documentTypeName + "'");
		}
		
		// it appears that there is always an old maintainable, even in the case of a new document creation,
		// if we don't initialize both the old and new versions we get errors during meshSections
		initializeRuleAfterNew(oldRule, ruleTemplate, documentTypeName);
		initializeRuleAfterNew(newRule, ruleTemplate, documentTypeName);
	}
    
	private static void initializeRuleAfterNew(RuleBaseValues rule, RuleTemplate ruleTemplate, String documentTypeName) {
		rule.setRuleTemplate(ruleTemplate);
		rule.setRuleTemplateId(ruleTemplate.getRuleTemplateId());
		rule.setDocTypeName(documentTypeName);
	}
	
	public static void validateRuleAndResponsibility(RuleDelegation oldRuleDelegation, RuleDelegation newRuleDelegation, Map<String, String[]> parameters) {
		String[] responsibilityIds = parameters.get(RESPONSIBILITY_ID_PARAM);
		if (ArrayUtils.isEmpty(responsibilityIds)) {
			throw new RiceRuntimeException("Delegation rule document must be initiated with a valid responsibility ID to delegate from.");
		}
		if (!ArrayUtils.isEmpty(responsibilityIds)) {
			Long responsibilityId = new Long(responsibilityIds[0]);
			RuleResponsibility ruleResponsibility = KEWServiceLocator.getRuleService().findRuleResponsibility(responsibilityId);
			if (ruleResponsibility == null) {
				throw new RiceRuntimeException("Failed to locate a rule responsibility for responsibility ID " + responsibilityId);
			}
			oldRuleDelegation.setResponsibilityId(responsibilityId);
			newRuleDelegation.setResponsibilityId(responsibilityId);
		}
		
	}

	public static void establishDefaultRuleValues(RuleBaseValues rule) {
		rule.setActiveInd(true);
	}
    
    
}
