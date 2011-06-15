/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.rice.kew.rule;


import static org.junit.Assert.assertTrue;

import java.util.List;

import org.kuali.rice.kew.api.action.DelegationType;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.workgroup.GroupNameId;
import org.kuali.rice.kim.api.entity.principal.Principal;
import org.kuali.rice.kim.api.entity.principal.PrincipalContract;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

/**
 * This is a description of what this class does - gilesp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public final class RuleTestUtils {
	
	private RuleTestUtils() {
		throw new UnsupportedOperationException("do not call");
	}
	
	/**
	 * <p>This method will create a delegate rule for the rule (assumed to be cardinality of 1) specified by the given
	 * docType and ruleTemplate. 
	 * 
	 * <p>As a side effect, active documents of this type will be requeued for workflow processing.
	 * 
	 * @param delegateUser the user who will be the delegate
	 */
	public static RuleDelegation createDelegationToUser(String docType, String ruleTemplate, String delegateUser) {
		// create and save a rule delegation 
    	RuleBaseValues originalRule = getRule(docType, ruleTemplate);
    	List<RuleResponsibility> responsibilities = originalRule.getResponsibilities();
    	assertTrue("assuming there is 1 responsibility", responsibilities != null && responsibilities.size() == 1);
    	
    	RuleResponsibility originalResp = responsibilities.get(0);

    	Principal delegatePrincipal = KimApiServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(delegateUser);

		// save the new rule delegation
		// this *SHOULD* requeue
		return createRuleDelegationToUser(originalRule, originalResp, delegatePrincipal);
	}

	/**
	 * <p>This method will create a delegate rule for the rule (assumed to be cardinality of 1) specified by the given
	 * docType and ruleTemplate. 
	 * 
	 * <p>As a side effect, active documents of this type will be requeued for workflow processing.
	 * 
	 * @param delegateUser the user who will be the delegate
	 */
	public static RuleDelegation createDelegationToGroup(String docType, String ruleTemplate, String delegateGroupId) {
		// create and save a rule delegation 
    	RuleBaseValues originalRule = getRule(docType, ruleTemplate);
    	List<RuleResponsibility> responsibilities = originalRule.getResponsibilities();
    	assertTrue("assuming there is 1 responsibility", responsibilities != null && responsibilities.size() == 1);
    	
    	RuleResponsibility originalResp = responsibilities.get(0);
    	Group delegateGroup = KEWServiceLocator.getIdentityHelperService().getGroup(new GroupNameId(delegateGroupId));
    	
		// save the new rule delegation
		// this *SHOULD* requeue
		return createRuleDelegationToGroup(originalRule, originalResp, delegateGroup);
	}
	/**
	 * This method gets a rule from a docType / ruleTemplate combo
	 */
	public static RuleBaseValues getRule(String docType, String ruleTemplate) {
		List rules = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination(ruleTemplate, docType);
    	assertTrue("assuming there is 1 rule", rules != null && rules.size() == 1);
    	
    	RuleBaseValues originalRule = (RuleBaseValues)rules.get(0);
		return originalRule;
	}

	/**
	 * <p>This method creates and saves a rule delegation
	 * 
	 * <p>As a side effect, active documents of this type will be requeued for workflow processing.
	 * 
	 * @param parentRule
	 * @param parentResponsibility
	 * @param delegatePrincipal
	 */
	public static RuleDelegation createRuleDelegationToUser(RuleBaseValues parentRule, RuleResponsibility parentResponsibility, PrincipalContract delegatePrincipal) {
		return createRuleDelegation(parentRule, parentResponsibility, delegatePrincipal.getPrincipalId(), KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID);
	}
	
	/**
	 * <p>This method creates and saves a rule delegation
	 * 
	 * <p>As a side effect, active documents of this type will be requeued for workflow processing.
	 * 
	 * @param parentRule
	 * @param parentResponsibility
	 * @param delegateGroup
	 */
	public static RuleDelegation createRuleDelegationToGroup(RuleBaseValues parentRule, RuleResponsibility parentResponsibility, Group delegateGroup) {
		return createRuleDelegation(parentRule, parentResponsibility, delegateGroup.getId(), KEWConstants.RULE_RESPONSIBILITY_GROUP_ID);
	}
	
	/**
	 * <p>This method creates and saves a rule delegation
	 * 
	 * <p>As a side effect, active documents of this type will be requeued for workflow processing.
	 */
	private static RuleDelegation createRuleDelegation(RuleBaseValues parentRule, RuleResponsibility parentResponsibility, String delegateId, String groupTypeCode) {
    	RuleTemplate delegationTemplate = parentRule.getRuleTemplate();
		RuleDelegation ruleDelegation = new RuleDelegation();
		ruleDelegation.setResponsibilityId(parentResponsibility.getResponsibilityId());
		ruleDelegation.setDelegationType(DelegationType.PRIMARY.getCode());
		RuleBaseValues rule = new RuleBaseValues();
		ruleDelegation.setDelegationRuleBaseValues(rule);
		rule.setDelegateRule(true);
		rule.setActiveInd(true);
		rule.setCurrentInd(true);
		rule.setDocTypeName(parentRule.getDocTypeName());
		rule.setRuleTemplateId(delegationTemplate.getDelegationTemplateId());
		rule.setRuleTemplate(delegationTemplate);
		rule.setDescription("Description of this delegate rule");
		rule.setForceAction(true);
		RuleResponsibility delegationResponsibility = new RuleResponsibility();
		rule.getResponsibilities().add(delegationResponsibility);
		delegationResponsibility.setRuleBaseValues(rule);
		delegationResponsibility.setRuleResponsibilityName(delegateId);
		delegationResponsibility.setRuleResponsibilityType(groupTypeCode);
		KEWServiceLocator.getRuleService().saveRuleDelegation(ruleDelegation, true);
		return ruleDelegation;
	}
}
