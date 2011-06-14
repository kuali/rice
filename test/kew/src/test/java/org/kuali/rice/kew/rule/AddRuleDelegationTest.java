/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kew.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import mocks.MockDocumentRequeuerImpl;

import org.junit.Test;
import org.kuali.rice.kew.api.document.actions.DelegationType;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.entity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

/**
 * Tests adding a delegation rule
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AddRuleDelegationTest extends KEWTestCase {
	
	private static final String DELEGATE_USER = "user2";
	private static final String DELEGATE_USER2 = "pmckown";

	private static final String DOCTYPE = "AddDelegationTest_DocType";
	private static final String RULE_TEMPLATE = "AddDelegationTest_RuleTemplate";
	private static final String DELEGATION_TEMPLATE = "AddDelegationTest_DelegationTemplate";

	protected void loadTestData() throws Exception {
		loadXmlFile("AddRuleDelegationTestData.xml");
	}
	
	/**
	 * 
	 * Tests that adding a delegation for a rule for which a document has a pending action request causes
	 * the document to be requeued. See KULRICE-3575
	 * 
	 * @throws Exception
	 */
    @Test public void testNewDelegationTriggersRequeue() throws Exception {
    	String docType = "RiceDocument.testNewDelegationTriggersRequeue";
    	
    	// route a document of this type
    	WorkflowDocument wd = WorkflowDocument.createDocument(getPrincipalIdForName("ewestfal"), DOCTYPE);
    	wd.routeDocument("");
    	
    	// clear the current set of requeued document ids
		MockDocumentRequeuerImpl.clearRequeuedDocumentIds();
    	
    	// create and save a rule delegation 
		RuleTestUtils.createDelegationToUser(DOCTYPE, RULE_TEMPLATE, DELEGATE_USER);
    	
		assertTrue("our document should have been requeued!", 
				MockDocumentRequeuerImpl.getRequeuedDocumentIds().contains(wd.getDocumentId()));
    }


	/**
	 * Tests adding a delegation rule.  The implementation is mostly a cut-and-paste copy of
	 * createDelegateRule and routeRule methods from DelegatRule2Action Struts action.
	 */
	@Test
	public void testAddRuleDelegation() throws Exception {

		RuleBaseValues originalRule = RuleTestUtils.getRule(DOCTYPE, RULE_TEMPLATE);
		
    	List<RuleResponsibility> originalResps = originalRule.getResponsibilities();
    	assertTrue("assuming there is 1 responsibility", originalResps != null && originalResps.size() == 1);
    	
    	RuleResponsibility originalResp = originalResps.get(0);
    	
		RuleTestUtils.createDelegationToUser(DOCTYPE, RULE_TEMPLATE, DELEGATE_USER);

		Principal principal2 = KimApiServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(DELEGATE_USER);

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
		assertEquals("Incorrect delegation type", DelegationType.PRIMARY.getCode(), newRuleDelegation.getDelegationType());


		/**
		 * Let's add another delegate rule.
		 */

		Principal delegatePrincipal = KimApiServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(DELEGATE_USER2);

		// let's save the new rule delegation
		RuleTestUtils.createRuleDelegationToUser(originalRule, originalResp, delegatePrincipal);

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
		newRuleDelegation.setDelegationType(DelegationType.SECONDARY.getCode());
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
				assertEquals("Rule Version should have been incremented.", 
						Long.valueOf(newRuleDelegation.getVersionNumber().longValue() + 1), 
						ruleDelegation.getVersionNumber());
			}
		}
		assertTrue("Failed to find the reversioned delegate rule", foundReversionedDelegateRule);
	}

	private void saveNewVersion(RuleDelegation ruleDelegation) {
		// clear out the keys
		ruleDelegation.setRuleDelegationId(null);
		ruleDelegation.setDelegateRuleId(null);
		for (RuleResponsibility ruleResponsibility : ruleDelegation.getDelegationRuleBaseValues().getResponsibilities()) {
			ruleResponsibility.setRuleBaseValuesId(null);
			//ruleResponsibility.setRuleBaseValues(null);
			ruleResponsibility.setResponsibilityId(null);
			ruleResponsibility.setRuleResponsibilityKey(null);
		}
		KEWServiceLocator.getRuleService().saveRuleDelegation(ruleDelegation, true);
	}

}
