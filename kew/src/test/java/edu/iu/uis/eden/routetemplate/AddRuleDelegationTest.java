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
package edu.iu.uis.eden.routetemplate;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.applicationconstants.ApplicationConstant;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.routetemplate.web.Rule2Form;
import edu.iu.uis.eden.routetemplate.web.WebRuleBaseValues;
import edu.iu.uis.eden.routetemplate.web.WebRuleResponsibility;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.WorkflowUser;

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
        // set some application constants that aren't defined in test data, and will cause NPEs if not defined
        KEWServiceLocator.getApplicationConstantsService().save(new ApplicationConstant(EdenConstants.RULE_DELEGATE_LIMIT_KEY, "1000"));
        KEWServiceLocator.getApplicationConstantsService().save(new ApplicationConstant(EdenConstants.RULE_CHANGE_AR_GENERATION_KEY, EdenConstants.YES_RULE_CHANGE_AR_GENERATION_VALUE));
        KEWServiceLocator.getApplicationConstantsService().save(new ApplicationConstant(EdenConstants.DELEGATE_CHANGE_AR_GENERATION_KEY, EdenConstants.YES_DELEGATE_CHANGE_AR_GENERATION_VALUE));


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

        WorkflowUser user2 = KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdVO(DELEGATE_USER));

        /**
         * Route the delegate rule
         */

        Rule2Form ruleForm = routeNewDelegateRule(originalRule, rt, originalRuleResponsibilityKey, user2);

        // verify doc is ENROUTE still
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("rkirkend"), ruleForm.getDocId());
        assertTrue("Document should be enroute: " + document.getStatusDisplayValue(), document.stateIsEnroute());
        assertTrue(document.isApprovalRequested());

        // after rule with delegate is routed, verify the integrity of the original rule

        originalRule = KEWServiceLocator.getRuleService().findRuleBaseValuesById(originalRule.getRuleBaseValuesId());
        assertTrue("Original rule should be current.", originalRule.getCurrentInd());
        List responsibilities = originalRule.getResponsibilities();
        assertEquals("Should have 1 responsibility", 1, responsibilities.size());
        originalResp = (RuleResponsibility)responsibilities.get(0);
        assertEquals("Original rule should have no delegations.", 0, originalResp.getDelegationRules().size());

        // verify the integrity of the enroute rule

        List newRules = KEWServiceLocator.getRuleService().findByRouteHeaderId(document.getRouteHeaderId());
        assertEquals("There should be 2 new rules, one parent and one delegate.", 2, newRules.size());
        RuleBaseValues newParent = null;
        RuleBaseValues newDelegate = null;
        for (Iterator iterator = newRules.iterator(); iterator.hasNext();) {
	    RuleBaseValues newRule = (RuleBaseValues) iterator.next();
	    if (newRule.getDelegateRule()) {
		newDelegate = newRule;
	    } else {
		newParent = newRule;
	    }
	}
        assertNotNull(newParent);
        assertNotNull(newDelegate);

        // check the parent rule
        assertFalse("New parent rule should NOT be current.", newParent.getCurrentInd());
        assertEquals("New parent rule should have original as previous version.", originalRule.getRuleBaseValuesId(), newParent.getPreviousVersionId());
        responsibilities = newParent.getResponsibilities();
        assertEquals("Should have 1 responsibility", 1, responsibilities.size());
        originalResp = (RuleResponsibility)responsibilities.get(0);
//        assertEquals("New parent should have 1 delegation.", 1, originalResp.getDelegationRules().size());
//        RuleBaseValues theDelegation = originalResp.getDelegationRule(0).getDelegationRuleBaseValues();
//        assertEquals("Delegation rule id should be id of new delegation", newDelegate.getRuleBaseValuesId(), theDelegation.getRuleBaseValuesId());

        // check delegate rule
        assertFalse("New delegate should NOT be current.", newDelegate.getCurrentInd());
        assertNull("New delegate should have no previous version.", newDelegate.getPreviousVersionId());

        /**
         * Push the docuument through to FINAL so the PostProcessor will run
         */

        document.approve("");
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), ruleForm.getDocId());
        assertTrue("Document should be final: " + document.getStatusDisplayValue(), document.stateIsFinal());

        originalRule = KEWServiceLocator.getRuleService().findRuleBaseValuesById(originalRule.getRuleBaseValuesId());
        assertFalse("Original rule should be NON current.", originalRule.getCurrentInd());
        responsibilities = originalRule.getResponsibilities();
        originalResp = (RuleResponsibility)responsibilities.get(0);
        assertEquals("Original rule should have no delegations.", 0, originalResp.getDelegationRules().size());

        // after going FINAL, verify the integrity of the new delegation rule

        newRules = KEWServiceLocator.getRuleService().findByRouteHeaderId(document.getRouteHeaderId());
        assertEquals("There should be 2 new rules, one parent and one delegate.", 2, newRules.size());
        newParent = null;
        newDelegate = null;
        for (Iterator iterator = newRules.iterator(); iterator.hasNext();) {
	    RuleBaseValues newRule = (RuleBaseValues) iterator.next();
	    if (newRule.getDelegateRule()) {
		newDelegate = newRule;
	    } else {
		newParent = newRule;
	    }
	}
        assertNotNull(newParent);
        assertNotNull(newDelegate);

        // check parent rule
        assertTrue("New parent rule should be current.", newParent.getCurrentInd());
        assertEquals("New parent rule should have original as previous version.", originalRule.getRuleBaseValuesId(), newParent.getPreviousVersionId());
        responsibilities = newParent.getResponsibilities();
        assertEquals("Should have 1 responsibility", 1, responsibilities.size());
        originalResp = (RuleResponsibility)responsibilities.get(0);
        assertEquals("New parent should have 1 delegation.", 1, originalResp.getDelegationRules().size());
        RuleBaseValues theDelegation = originalResp.getDelegationRule(0).getDelegationRuleBaseValues();
        assertEquals("Delegation rule id should be id of new delegation", newDelegate.getRuleBaseValuesId(), theDelegation.getRuleBaseValuesId());

        // check delegate rule
        assertTrue("New delegate should be current.", newDelegate.getCurrentInd());
        assertNull("New delegate should have no previous version.", newDelegate.getPreviousVersionId());
        RuleBaseValues newDelegateParent = KEWServiceLocator.getRuleService().getParentRule(newDelegate.getRuleBaseValuesId());
        assertEquals("New delegate's parent should be the same as the new parent.", newParent.getRuleBaseValuesId(), newDelegateParent.getRuleBaseValuesId());

        /**
         * Let's add another delegate rule.  This is where we had the production problem where Rule Delegations were not getting pulled forward successfully.
         */

        originalRule = newParent;
        originalResps = originalRule.getResponsibilities();
        assertEquals(1, originalResps.size());
        originalResp = originalResps.get(0);
        originalRuleResponsibilityKey = originalResp.getRuleResponsibilityKey();
        WorkflowUser delegateUser = KEWServiceLocator.getUserService().getWorkflowUser(new NetworkIdVO(DELEGATE_USER2));

        Rule2Form ruleForm2 = routeNewDelegateRule(originalRule, rt, originalRuleResponsibilityKey, delegateUser);

        // verify doc is ENROUTE still

        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), ruleForm2.getDocId());
        assertTrue("Document should be enroute: " + document.getStatusDisplayValue(), document.stateIsEnroute());
        assertTrue(document.isApprovalRequested());

        originalRule = KEWServiceLocator.getRuleService().findRuleBaseValuesById(originalRule.getRuleBaseValuesId());
        assertTrue("rule should be current.", originalRule.getCurrentInd());

        responsibilities = originalRule.getResponsibilities();
        originalResp = (RuleResponsibility)responsibilities.get(0);
        assertEquals("Rule should have one delegation.", 1, originalResp.getDelegationRules().size());

        RuleDelegation ruleDelegation = originalResp.getDelegationRule(0);
        RuleBaseValues originalDelegationRule = ruleDelegation.getDelegationRuleBaseValues();
        assertTrue("Original delegation rule should be current.", originalDelegationRule.getCurrentInd());

        // pull out the enroute ones



        // Finally approve it, let's check the end result

        document.approve("");
        document = new WorkflowDocument(new NetworkIdVO("rkirkend"), ruleForm2.getDocId());
        assertTrue("Document should be final: " + document.getStatusDisplayValue(), document.stateIsFinal());

        newRules = KEWServiceLocator.getRuleService().findByRouteHeaderId(document.getRouteHeaderId());
        assertEquals("There should be 2 new rules, one parent and one delegate.", 2, newRules.size());
        newParent = null;
        newDelegate = null;
        for (Iterator iterator = newRules.iterator(); iterator.hasNext();) {
	    RuleBaseValues newRule = (RuleBaseValues) iterator.next();
	    if (newRule.getDelegateRule()) {
		newDelegate = newRule;
	    } else {
		newParent = newRule;
	    }
	}
        assertNotNull(newParent);
        assertNotNull(newDelegate);

        assertTrue("New parent should be current.", newParent.getCurrentInd());
        responsibilities = newParent.getResponsibilities();
        originalResp = (RuleResponsibility)responsibilities.get(0);
        assertEquals("Rule should have two delegations.", 2, originalResp.getDelegationRules().size());
        for (Iterator iterator = originalResp.getDelegationRules().iterator(); iterator.hasNext();) {
	    ruleDelegation = (RuleDelegation) iterator.next();
	    assertTrue("New rule delegation should be current.", ruleDelegation.getDelegationRuleBaseValues().getCurrentInd());
	}

    }

    private Rule2Form routeNewDelegateRule(RuleBaseValues originalRule, RuleTemplate rt, Long originalRuleResponsibilityKey, WorkflowUser delegateUser) throws Exception {

	final String A_WF_ADMIN_USERNAME = "ewestfal";

	Rule2Form ruleForm = new Rule2Form();
	ruleForm.setParentRule(new WebRuleBaseValues(originalRule));
	ruleForm.getRuleCreationValues().setCreating(true);
	ruleForm.getRuleCreationValues().setRuleId(originalRule.getRuleBaseValuesId());
	ruleForm.getRuleCreationValues().setManualDelegationTemplate(false);
	ruleForm.getRuleCreationValues().setRuleTemplateId(rt.getRuleTemplateId());
	ruleForm.getRuleCreationValues().setRuleTemplateName(rt.getName());
	ruleForm.getRuleCreationValues().setRuleResponsibilityKey(originalRuleResponsibilityKey);

    WebRuleBaseValues rule = new WebRuleBaseValues();

    {
        RuleBaseValues defaultRule = KEWServiceLocator.getRuleService().findDefaultRuleByRuleTemplateId(originalRule.getRuleTemplate().getDelegationTemplateId());
        if (defaultRule != null) {
            List ruleDelegations = KEWServiceLocator.getRuleDelegationService().findByDelegateRuleId(defaultRule.getRuleBaseValuesId());
            defaultRule.setActivationDate(null);
            defaultRule.setCurrentInd(null);
            defaultRule.setDeactivationDate(null);
            defaultRule.setDocTypeName(null);
            defaultRule.setLockVerNbr(null);
            defaultRule.setRuleBaseValuesId(null);
            defaultRule.setTemplateRuleInd(Boolean.FALSE);
            defaultRule.setVersionNbr(null);
            rule.load(defaultRule);

            if (ruleDelegations != null && !ruleDelegations.isEmpty()) {
                RuleDelegation defaultDelegation = (RuleDelegation) ruleDelegations.get(0);
                ruleForm.getRuleDelegation().setDelegationType(defaultDelegation.getDelegationType());
            }
        }

        rule.setDocTypeName(ruleForm.getParentRule().getDocTypeName());
        rule.setRuleTemplateId(ruleForm.getParentRule().getRuleTemplate().getDelegationTemplateId());
        rule.setRuleTemplateName(ruleForm.getParentRule().getRuleTemplate().getDelegateTemplateName());
        // have to set the ruletemplate object explicitly...the struts action must do this itself somewhere...
        rule.setRuleTemplate(KEWServiceLocator.getRuleTemplateService().findByRuleTemplateId(rule.getRuleTemplateId()));
        rule.setDelegateRule(Boolean.TRUE);
        rule.loadFieldsWithDefaultValues();
        rule.createNewRuleResponsibility();

        ruleForm.getRuleDelegation().setDelegationRuleBaseValues(rule);
        ruleForm.getMyRules().addRule(rule);

        //createFlexDoc(request, ruleForm, ruleForm.getMyRules().getRules());

        if (ruleForm.getFlexDoc() == null) {
//          rule2Form.setFlexDoc(new WorkflowDocument(EdenConstants.RULE_DOCUMENT_NAME, getUserSession(request).getWorkflowUser(), EdenConstants.EDEN_APP_CODE));
            String ruleDocTypeName = KEWServiceLocator.getRuleService().getRuleDocmentTypeName(ruleForm.getMyRules().getRules());
            WorkflowDocument workflowDocument = new WorkflowDocument(new NetworkIdVO(A_WF_ADMIN_USERNAME), ruleDocTypeName);
            // adhoc it to rkirkend so it will stop before going final
            workflowDocument.appSpecificRouteDocumentToUser(EdenConstants.ACTION_REQUEST_APPROVE_REQ, "", new NetworkIdVO("rkirkend"), "", true);
            ruleForm.setFlexDoc(workflowDocument);
            ruleForm.setDocId(ruleForm.getFlexDoc().getRouteHeaderId());
            ruleForm.establishVisibleActionRequestCds();
        }

        ruleForm.getShowHide().append().append();
        ruleForm.setEditingDelegate(true);
        rule.establishRequiredState();
    }
    // ---

    // ok, "update the form"
    ruleForm.getRuleCreationValues().setCreating(false);

    MyRules2 rules = ruleForm.getMyRules();

    WebRuleBaseValues delegateRule = rules.getRule(0);
    delegateRule.setDescription("A delegate rule");
    WebRuleResponsibility resp = (WebRuleResponsibility) delegateRule.getResponsibility(0);
    resp.setNumberOfDelegations(0);
    resp.setShowDelegations(false);
    resp.setDelegationRulesMaterialized(false);
    resp.setHasDelegateRuleTemplate(false);
    resp.setReviewer(delegateUser.getAuthenticationUserId().getId());
    resp.setRuleResponsibilityType(EdenConstants.RULE_RESPONSIBILITY_WORKFLOW_ID);
    resp.setRuleResponsibilityName(delegateUser.getWorkflowId()); // This is taken as a WorkflowId (not NetworkId, etc.)

    delegateRule.setCurrentInd(Boolean.FALSE);

    // get the parent rule of the delegation-rule being submitted
    RuleBaseValues parentRule = KEWServiceLocator.getRuleService().findRuleBaseValuesById(ruleForm.getParentRule().getRuleBaseValuesId());
    // new delegation rule
    //        if (delegateRule.getPreviousVersionId() == null) {

    Long previousVersionId = parentRule.getRuleBaseValuesId();

    RuleResponsibility delegateResponsibility = parentRule.getResponsibility(ruleForm.getRuleCreationValues().getRuleResponsibilityKey());

    // get the RuleDelegation being submitted and connect the
    // delegate rule and the delegat responsibility
    RuleDelegation ruleDelegation = ruleForm.getRuleDelegation();
    ruleDelegation.setDelegationRuleBaseValues(delegateRule);
    ruleDelegation.setRuleResponsibility(delegateResponsibility);

    // if the delegateRule itself has a previous version, then iterate
    // through the rules associated with the delegate responsibility and
    // make sure that the previous, delegate rule version is no longer
    // associated with the responsibility
    if (delegateRule.getPreviousVersionId() != null) {
        for (Iterator iter = delegateResponsibility.getDelegationRules().iterator(); iter.hasNext();) {
            RuleDelegation delegation = (RuleDelegation) iter.next();
            if (delegation.getDelegateRuleId().longValue() == delegateRule.getPreviousVersionId().longValue()) {
                iter.remove();
                break;
            }
        }
    }

    // add the new rule delegation to the responsibility
    delegateResponsibility.getDelegationRules().add(ruleDelegation);

    // make a copy of the parent rule, which will be the new version
    // of the parent rule, that contains this new delegation
    // XXX
    parentRule = (RuleBaseValues) parentRule.copy(false);
    parentRule.setPreviousVersionId(previousVersionId);
    //        }

    /*
     * RuleBaseValues parentRule = getRuleService().findRuleBaseValuesById(ruleForm.getParentRule().getRuleBaseValuesId()); Long previousVersionId = parentRule.getRuleBaseValuesId(); RuleResponsibility delegateResponsibility = parentRule.getResponsibility(ruleForm.getRuleCreationValues().getRuleResponsibilityKey()); RuleDelegation ruleDelegation = ruleForm.getRuleDelegation(); ruleDelegation.setDelegationRuleBaseValues(delegateRule); ruleDelegation.setRuleResponsibility(delegateResponsibility); // replace the existing delegation in the parent rule boolean foundDelegation = false; int delIndex = 0; for (Iterator iterator = delegateResponsibility.getDelegationRules().iterator(); iterator.hasNext();) { RuleDelegation delegation = (RuleDelegation) iterator.next(); if (delegation.getDelegateRuleId().equals(delegateRule.getPreviousVersionId())) { iterator.remove(); foundDelegation = true; break; } delIndex++; } if (foundDelegation) {
     * delegateResponsibility.getDelegationRules().add(delIndex, ruleDelegation); } else { delegateResponsibility.getDelegationRules().add(ruleDelegation); } parentRule = (RuleBaseValues) parentRule.copy(false); parentRule.setPreviousVersionId(previousVersionId);
     */

    // null out the responsibility keys for the delegate rule
    for (Iterator iterator = delegateRule.getResponsibilities().iterator(); iterator.hasNext();) {
        RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
        responsibility.setRuleResponsibilityKey(null);
    }

    WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(A_WF_ADMIN_USERNAME));
    KEWServiceLocator.getRuleService().routeRuleWithDelegate(ruleForm.getDocId(), parentRule, delegateRule, user, ruleForm.getAnnotation(), false);

    return ruleForm;
    }

}
