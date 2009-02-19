/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.rice.kew.rule;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.web.Rule2Form;
import org.kuali.rice.kew.rule.web.WebRuleBaseValues;
import org.kuali.rice.kew.rule.web.WebRuleResponsibility;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;


/**
 * Tests adding a delegation rule
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class AddRuleDelegationTest extends KEWTestCase {

	protected void loadTestData() throws Exception {
		loadXmlFile("AddRuleDelegationTestData.xml");
	}

	/**
	 * Tests adding a delegation rule.  The implementation is mostly a cut-and-paste copy of
	 * createDelegateRule and routeRule methods from DelegatRule2Action Struts action.
	 */
	@Test
	public void testAddRuleDelegation() throws Exception {
		
		//set system parameters that aren't defined in test data
		Parameter parameter = new Parameter(KEWConstants.RULE_DELEGATE_LIMIT, "1000", "A");
		parameter.setParameterNamespaceCode(KEWConstants.KEW_NAMESPACE);
		parameter.setParameterTypeCode("CONFG");
		parameter.setParameterDetailTypeCode(KNSConstants.DetailTypes.RULE_DETAIL_TYPE);
		//parameter.setParameterWorkgroupName(KEWConstants.WORKFLOW_SUPER_USER_WORKGROUP_NAME);
		KNSServiceLocator.getBusinessObjectService().save(parameter);

		parameter = new Parameter(KEWConstants.RULE_GENERATE_ACTION_REQESTS_IND, KEWConstants.YES_RULE_CHANGE_AR_GENERATION_VALUE, "A");
		parameter.setParameterNamespaceCode(KEWConstants.KEW_NAMESPACE);
		parameter.setParameterTypeCode("CONFG");
		parameter.setParameterDetailTypeCode(KNSConstants.DetailTypes.RULE_DETAIL_TYPE);
		//parameter.setParameterWorkgroupName(KEWConstants.WORKFLOW_SUPER_USER_WORKGROUP_NAME);

		KNSServiceLocator.getBusinessObjectService().save(parameter);

		final String DELEGATE_USER = "user2";
		final String DELEGATE_USER2 = "pmckown";

		final String DOCTYPE = "AddDelegationTest_DocType";
		final String RULE_TEMPLATE = "AddDelegationTest_RuleTemplate";
		final String DELEGATION_TEMPLATE = "AddDelegationTest_DelegationTemplate";

		List<RuleBaseValues> existingRules = KEWServiceLocator.getRuleService().fetchAllCurrentRulesForTemplateDocCombination(RULE_TEMPLATE, DOCTYPE);
		assertNotNull(existingRules);
		assertEquals(1, existingRules.size());

		RuleBaseValues originalRule = existingRules.get(0);
		assertTrue("Original rule should be current.", originalRule.getCurrentInd());

		List<RuleResponsibility> originalResps = originalRule.getResponsibilities();
		assertEquals(1, originalResps.size());

		RuleResponsibility originalResp = originalResps.get(0);
		Long originalRuleResponsibilityKey = originalResp.getRuleResponsibilityKey();

		RuleTemplate rt = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(DELEGATION_TEMPLATE);
		assertNotNull(rt);
		assertNotNull(rt.getRuleTemplateId());
		assertFalse(StringUtils.isEmpty(rt.getName()));

		KimPrincipal principal2 = KEWServiceLocator.getIdentityHelperService().getPrincipal(new NetworkIdDTO(DELEGATE_USER));

		// save the new rule delegation
		saveNewRuleDelegation(originalRule, rt, originalResp, principal2);
	
		// check the original rule, it should be the same (i.e. not be re-versioned as KEW used to do pre 1.0 when a delegate was added)
		originalRule = KEWServiceLocator.getRuleService().findRuleBaseValuesById(originalRule.getRuleBaseValuesId());
		assertTrue("Original rule should be current.", originalRule.getCurrentInd());
		List<RuleResponsibility> responsibilities = originalRule.getResponsibilities();
		originalResp = responsibilities.get(0);
		assertEquals("Original rule should have 1 delegation now.", 1, originalResp.getDelegationRules().size());

		List<RuleDelegation> newRuleDelegations = KEWServiceLocator.getRuleDelegationService().findByResponsibilityId(originalResp.getResponsibilityId());
		assertEquals("Should be 1 delegation", 1, newRuleDelegations.size());
		
		RuleDelegation newRuleDelegation = newRuleDelegations.get(0);
		assertEquals("Incorrect responsibility id", originalResp.getResponsibilityId(), newRuleDelegation.getResponsibilityId());
		assertNotNull("Name should not be null", newRuleDelegation.getDelegationRuleBaseValues().getName());
		assertTrue("delegate rule should be current", newRuleDelegation.getDelegationRuleBaseValues().getCurrentInd());
		assertTrue("delegate rule should be flagged as a delegate", newRuleDelegation.getDelegationRuleBaseValues().getDelegateRule());
		assertEquals("Should have 1 responsibility", 1, newRuleDelegation.getDelegationRuleBaseValues().getResponsibilities().size());
		assertEquals("Incorrect responsibility name", principal2.getPrincipalId(), newRuleDelegation.getDelegationRuleBaseValues().getResponsibilities().get(0).getRuleResponsibilityName());
		assertEquals("Incorrect responsibility type", KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID, newRuleDelegation.getDelegationRuleBaseValues().getResponsibilities().get(0).getRuleResponsibilityType());
		assertEquals("Incorrect delegation type", KEWConstants.DELEGATION_PRIMARY, newRuleDelegation.getDelegationType());
		

		/**
		 * Let's add another delegate rule.
		 */

		KimPrincipal delegatePrincipal = KIMServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(DELEGATE_USER2);

		// let's save the new rule delegation
		saveNewRuleDelegation(originalRule, rt, originalResp, delegatePrincipal);
		
		List<RuleDelegation> ruleDelegations = KEWServiceLocator.getRuleDelegationService().findByResponsibilityId(originalResp.getResponsibilityId());
		assertEquals("There should be 2 delegation rules", 2, ruleDelegations.size());
		boolean foundFirstDelegateRule = false;
		for (RuleDelegation ruleDelegation : ruleDelegations) {
			if (ruleDelegation.getRuleDelegationId().equals(newRuleDelegation.getRuleDelegationId())) {
				foundFirstDelegateRule = true;
				assertEquals("Rule Version should not have changed.", ruleDelegation.getVersionNumber(), newRuleDelegation.getVersionNumber());
			} else {
				// this should be our new rule delegation
				assertEquals("Incorrect responsibility id", originalResp.getResponsibilityId(), ruleDelegation.getResponsibilityId());
				assertNotNull("Name should not be null", ruleDelegation.getDelegationRuleBaseValues().getName());
				assertTrue("delegate rule should be current", ruleDelegation.getDelegationRuleBaseValues().getCurrentInd());
				assertTrue("delegate rule should be flagged as a delegate", ruleDelegation.getDelegationRuleBaseValues().getDelegateRule());
				assertEquals("Should have 1 responsibility", 1, ruleDelegation.getDelegationRuleBaseValues().getResponsibilities().size());
				assertEquals("Incorrect responsibility name", delegatePrincipal.getPrincipalId(), ruleDelegation.getDelegationRuleBaseValues().getResponsibilities().get(0).getRuleResponsibilityName());
				assertEquals("Incorrect responsibility type", KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID, ruleDelegation.getDelegationRuleBaseValues().getResponsibilities().get(0).getRuleResponsibilityType());
			}
		}
		assertTrue("Failed to find the first delegate rule", foundFirstDelegateRule);

		/**
		 *  now let's try editing our first delegate rule
		 */
		
		Long newRuleDelegationId = newRuleDelegation.getRuleDelegationId();
		// change the delegation type to secondary
		newRuleDelegation.setDelegationType(KEWConstants.DELEGATION_SECONDARY);
		saveNewVersion(newRuleDelegation);
		Long newRuleDelegationId2 = newRuleDelegation.getRuleDelegationId();
		
		// let's check the original and verify that its been re-versioned
		newRuleDelegation = KEWServiceLocator.getRuleDelegationService().findByRuleDelegationId(newRuleDelegationId);
		assertNotNull(newRuleDelegation);
		assertFalse("Rule delegation should no longer be current.", newRuleDelegation.getDelegationRuleBaseValues().getCurrentInd());
		
		// there should still be 2 rule delegations, however one of them has been reversioned
		ruleDelegations = KEWServiceLocator.getRuleDelegationService().findByResponsibilityId(originalResp.getResponsibilityId());
		assertEquals("There should be 2 delegation rules", 2, ruleDelegations.size());
		boolean foundReversionedDelegateRule = false;
		for (RuleDelegation ruleDelegation : ruleDelegations) {
			if (ruleDelegation.getRuleDelegationId().equals(newRuleDelegationId2)) {
				// this is our reversioned rule
				foundReversionedDelegateRule = true;
				assertEquals("Previous version relationship should be set up now", newRuleDelegation.getDelegationRuleBaseValues().getRuleBaseValuesId(), ruleDelegation.getDelegationRuleBaseValues().getPreviousVersionId());
				assertEquals("Rule Version should have been incremented.", new Long(newRuleDelegation.getVersionNumber().longValue() + 1), ruleDelegation.getVersionNumber());
			}
		}
		assertTrue("Failed to find the reversioned delegate rule", foundReversionedDelegateRule);
	}

	private void saveNewRuleDelegation(RuleBaseValues parentRule, RuleTemplate delegationTemplate, RuleResponsibility parentResponsibility, KimPrincipal delegatePrincipal) {
		RuleDelegation ruleDelegation = new RuleDelegation();
		ruleDelegation.setResponsibilityId(parentResponsibility.getResponsibilityId());
		ruleDelegation.setDelegationType(KEWConstants.DELEGATION_PRIMARY);
		RuleBaseValues rule = new RuleBaseValues();
		ruleDelegation.setDelegationRuleBaseValues(rule);
		rule.setDelegateRule(true);
		rule.setActiveInd(true);
		rule.setCurrentInd(true);
		rule.setDocTypeName(parentRule.getDocTypeName());
		rule.setRuleTemplateId(delegationTemplate.getDelegationTemplateId());
		rule.setRuleTemplate(delegationTemplate);
		rule.setDescription("Description of this delegate rule");
		rule.setIgnorePrevious(true);
		RuleResponsibility delegationResponsibility = new RuleResponsibility();
		rule.getResponsibilities().add(delegationResponsibility);
		delegationResponsibility.setRuleBaseValues(rule);
		delegationResponsibility.setRuleResponsibilityName(delegatePrincipal.getPrincipalId());
		delegationResponsibility.setRuleResponsibilityType(KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID);
		KEWServiceLocator.getRuleService().saveRuleDelegation(ruleDelegation, true);
	}
	
	private void saveNewVersion(RuleDelegation ruleDelegation) {
		// clear out the keys
		ruleDelegation.setRuleDelegationId(null);
		ruleDelegation.setDelegateRuleId(null);
		for (RuleResponsibility ruleResponsibility : ruleDelegation.getDelegationRuleBaseValues().getResponsibilities()) {
			ruleResponsibility.setRuleBaseValuesId(null);
			ruleResponsibility.setRuleBaseValues(null);
			ruleResponsibility.setResponsibilityId(null);
			ruleResponsibility.setRuleResponsibilityKey(null);
		}
		KEWServiceLocator.getRuleService().saveRuleDelegation(ruleDelegation, true);
	}

}
