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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;
import edu.iu.uis.eden.routetemplate.MyRules2;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleDelegation;
import edu.iu.uis.eden.routetemplate.RuleDelegationService;
import edu.iu.uis.eden.routetemplate.RuleResponsibility;
import edu.iu.uis.eden.routetemplate.RuleService;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.routetemplate.RuleTemplateService;
import edu.iu.uis.eden.util.CodeTranslator;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.ShowHideTree;
import edu.iu.uis.eden.web.WorkflowAction;

/**
 * A Struts Action for creating a routing delegate rules.
 * 
 * @see RuleService
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DelegateRule2Action extends WorkflowAction {

    
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form rule2Form = (Rule2Form) form;
        rule2Form.getRuleCreationValues().setCreating(true);
        //createFlexDoc(request, rule2Form);
        return mapping.findForward("basic");
    }

    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        String lookupType = ruleForm.getLookupType();
        ruleForm.setLookupType(null);

        String delegateOnly = ruleForm.getDelegationSearchOnly();
        ruleForm.setDelegationSearchOnly(null);

        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + mapping.getModuleConfig().getPrefix();;
        StringBuffer lookupUrl = new StringBuffer(basePath);
        lookupUrl.append("/Lookup.do?methodToCall=start&docFormKey=").append(getUserSession(request).addObject(form)).append("&lookupableImplServiceName=");
        lookupUrl.append(request.getParameter("lookupableImplServiceName"));
        if (!Utilities.isEmpty(delegateOnly)) {
            lookupUrl.append("&ruleDelegationOnly=true");
        }
        lookupUrl.append("&conversionFields=").append(request.getParameter("conversionFields"));

        if (lookupType != null && !lookupType.equals("")) {
            WorkflowLookupable workflowLookupable = (WorkflowLookupable)GlobalResourceLoader.getService(request.getParameter("lookupableImplServiceName"));//(WorkflowLookupable) SpringServiceLocator.getExtensionService().getLookupable(request.getParameter("lookupableImplServiceName"));
            if (!Utilities.isEmpty(request.getParameter("conversionFields"))) {
                lookupUrl.append(",");
            }
            for (Iterator iterator = workflowLookupable.getDefaultReturnType().iterator(); iterator.hasNext();) {
                String returnType = (String) iterator.next();
                lookupUrl.append(returnType).append(":").append(lookupType);
            }
        }

        lookupUrl.append("&returnLocation=").append(basePath).append(mapping.getPath()).append(".do");
        return new ActionForward(lookupUrl.toString(), true);
    }

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        ruleForm.setActionRequestCodes(CodeTranslator.arLabels);
        ruleForm.setApprovePolicyCodes(CodeTranslator.approvePolicyLabels);

        //first time through edit I don't get called
        if (ruleForm.getRuleCreationValues().getRuleId() != null) {
            RuleBaseValues parentRule = getRuleService().findRuleBaseValuesById(ruleForm.getRuleCreationValues().getRuleId());
            RuleResponsibility responsibility = null;
            if (ruleForm.getRuleCreationValues().getRuleResponsibilityKey() != null) {
                responsibility = parentRule.getResponsibility(ruleForm.getRuleCreationValues().getRuleResponsibilityKey());
            }
            //this is so only the responsibility we're working on get's displayed
            stripParentRule(parentRule, responsibility);
            ruleForm.setParentRule(new WebRuleBaseValues(parentRule));
            if (ruleForm.getParentShowHide().getChildren().isEmpty()) {
                ShowHideTree parentRuleTree = ruleForm.getParentShowHide().append();
                parentRuleTree.setShow(Boolean.FALSE);
                for (Iterator iterator = ruleForm.getParentRule().getResponsibilities().iterator(); iterator.hasNext();) {
                    iterator.next();
                    parentRuleTree.append();
                }
            }
        }
        if (!ruleForm.getMyRules().getRules().isEmpty()) {
            ruleForm.getRuleDelegation().setDelegationRuleBaseValues((RuleBaseValues) ruleForm.getMyRules().getRule(0));
        }
        MyRules2 myRules = ruleForm.getMyRules();
        for (Iterator ruleIt = myRules.getRules().iterator(); ruleIt.hasNext();) {
            WebRuleBaseValues rule = (WebRuleBaseValues) ruleIt.next();
            rule.establishRequiredState();
        }
        if(ruleForm.getDocId() != null && ruleForm.getFlexDoc() == null){
            ruleForm.setFlexDoc(new WorkflowDocument(new WorkflowIdVO(getUserSession(request).getWorkflowUser().getWorkflowId()), ruleForm.getDocId()));
        }
        ruleForm.establishVisibleActionRequestCds();
        return null;
    }

    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        List errors = new ArrayList();
        WebRuleBaseValues webRule = null;
        if (ruleForm.getCurrentRuleId() != null) {
            RuleBaseValues rule = getRuleService().findRuleBaseValuesById(ruleForm.getCurrentRuleId());
            if (rule != null) {
                if (!rule.getDelegateRule().booleanValue()) {
                    errors.add(new WorkflowServiceErrorImpl("Rule with given id is not a delegate rule " + ruleForm.getCurrentRuleId(), "general.workflow.error", "Rule with given id is not a delegate rule " + ruleForm.getCurrentRuleId()));
                } else {
                    List delegations = getRuleDelegationService().findByDelegateRuleId(rule.getRuleBaseValuesId());
                    //hunt for the current parent rule of this delegation to determine what rule to check locking against
                    RuleDelegation ruleDelegation = null;
                    for (Iterator iter = delegations.iterator(); iter.hasNext();) {
                        RuleDelegation tmpRuleDelegation = (RuleDelegation) iter.next();
                        if (tmpRuleDelegation.getRuleResponsibility().getRuleBaseValues().getCurrentInd().booleanValue()) {
                            ruleDelegation = tmpRuleDelegation;
                            break;
                        }
                    }
                    if (ruleDelegation == null) {
                        errors.add(new WorkflowServiceErrorImpl("This rule is not being delegated from a current rule, it cannot be copied.", "general.workflow.error", "This rule is not currently being delegated from a current rule, it cannot be copied."));
                    } else {
                        ruleForm.setRuleDelegation(ruleDelegation);
                        RuleBaseValues parentRule = ruleDelegation.getRuleResponsibility().getRuleBaseValues();
                        ruleForm.getRuleCreationValues().setRuleId(parentRule.getRuleBaseValuesId());
                        webRule = WebRuleUtils.copyToNewRule(new WebRuleBaseValues(rule));
                        initializeShowHide(ruleForm.getShowHide(), webRule);
                    }
                }
            } else {
                errors.add(new WorkflowServiceErrorImpl("Could not locate rule for given id " + ruleForm.getCurrentRuleId(), "general.workflow.error", "Could not locate rule for given id " + ruleForm.getCurrentRuleId()));
            }
        } else {
            errors.add(new WorkflowServiceErrorImpl("Error copying rule, no id specified.", "general.workflow.error", "Error copying rule, no id specified."));
        }
        if (!errors.isEmpty()) {
            throw new WorkflowServiceErrorException("Errors copying rule.", errors);
        }
        ruleForm.getMyRules().addRule(webRule);
        ruleForm.getRuleCreationValues().setCreating(false);
        createFlexDoc(request, ruleForm, ruleForm.getMyRules().getRules());
        
        ruleForm.getRuleCreationValues().setRuleResponsibilityKey(ruleForm.getRuleDelegation().getRuleResponsibilityId());
        
        ruleForm.getRuleDelegation().setDelegationRuleBaseValues(webRule);
        ruleForm.setEditingDelegate(true);
        webRule.establishRequiredState();
        establishRequiredState(request, ruleForm);
        return mapping.findForward("basic");                
    }
    
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        RuleBaseValues rule = getRuleService().findRuleBaseValuesById(ruleForm.getCurrentRuleId());
        if (rule != null) {
            ActionErrors errors = new ActionErrors();
            if (!rule.getDelegateRule().booleanValue()) {
                errors.add("hasErrors", new ActionMessage("routetemplate.select", "valid delegation rule for editing.  The rule with id " + rule.getRuleBaseValuesId() + " is not a delegate rule"));
                saveErrors(request, errors);
                return mapping.findForward("basic");
            }

            List delegations = getRuleDelegationService().findByDelegateRuleId(rule.getRuleBaseValuesId());
            //hunt for the current parent rule of this delegation to determine what rule to check locking against
            for (Iterator iter = delegations.iterator(); iter.hasNext();) {
                RuleDelegation ruleDelegation = (RuleDelegation) iter.next();
                if (ruleDelegation.getRuleResponsibility().getRuleBaseValues().getCurrentInd().booleanValue()) {
                    ruleForm.setRuleDelegation(ruleDelegation);
                    break;
                }
            }

            RuleBaseValues oldParentRule = ruleForm.getRuleDelegation().getRuleResponsibility().getRuleBaseValues();
            stripParentRule(oldParentRule, ruleForm.getRuleDelegation().getRuleResponsibility());
            WebRuleBaseValues parentRule = new WebRuleBaseValues(oldParentRule);
            ruleForm.setParentRule(parentRule);

            if (checkLockedForRouting(errors, parentRule)) {
                saveErrors(request, errors);
                return mapping.findForward("basic");
            }

            /*
             * ShowHideTree ruleTree = ruleForm.getParentShowHide().append(); // be sure not to go down into the delegations for (Iterator iterator = parentRule.getResponsibilities().iterator(); iterator.hasNext();) { RuleResponsibility responsibility = (RuleResponsibility) iterator.next(); ruleTree.append(); } ruleTree.setShow(Boolean.FALSE);
             */
            WebRuleBaseValues webRule = new WebRuleBaseValues(rule);
            webRule.setPreviousVersionId(rule.getRuleBaseValuesId());
            ruleForm.getMyRules().addRule(webRule);
            ruleForm.getRuleCreationValues().setCreating(false);
            ruleForm.getRuleCreationValues().setRuleId(parentRule.getRuleBaseValuesId());
            ruleForm.getRuleCreationValues().setRuleResponsibilityKey(ruleForm.getRuleDelegation().getRuleResponsibilityId());
            ruleForm.setEditingDelegate(true);
            ruleForm.setShowHide(initializeShowHide(ruleForm.getMyRules()));
            String ruleDocTypeName = getRuleService().getRuleDocmentTypeName(ruleForm.getMyRules().getRules());
            ruleForm.setFlexDoc(new WorkflowDocument(new WorkflowIdVO(getUserSession(request).getWorkflowUser().getWorkflowId()), ruleDocTypeName));
            ruleForm.setDocId(ruleForm.getFlexDoc().getRouteHeaderId());
            ruleForm.establishVisibleActionRequestCds();
        }
        //establishRequiredState(request, ruleForm);
        return mapping.findForward("basic");
    }

    private ActionForward checkLocked(ActionMapping mapping, Rule2Form ruleForm, HttpServletRequest request, HttpServletResponse response) {
        if ("true".equalsIgnoreCase(Utilities.getApplicationConstant(EdenConstants.RULE_LOCKING_ON))) {
            Long routeHeaderId = getRuleService().isLockedForRouting(ruleForm.getRuleCreationValues().getRuleId());
            if (routeHeaderId != null) {
                ActionErrors lockErrors = new ActionErrors();
                lockErrors.add("hasErrors", new ActionMessage("routetemplate.RuleService.ruleInRoute", "" + routeHeaderId.longValue()));
                saveErrors(request, lockErrors);
                return mapping.findForward("basic");
            }
        }
        return null;
    }
    
    public ActionForward createDelegateRule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        ActionErrors errors = validateCreateDelegateRule(ruleForm);

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return mapping.findForward("basic");
        }

        ActionForward checkLocked = checkLocked(mapping, ruleForm, request, response);
        if (checkLocked != null) {
            return checkLocked;
        }

        ruleForm.getRuleCreationValues().setCreating(false);
        WebRuleBaseValues rule = new WebRuleBaseValues();
        
        RuleBaseValues defaultRule = getRuleService().findDefaultRuleByRuleTemplateId(ruleForm.getParentRule().getRuleTemplate().getDelegationTemplateId());
        if (defaultRule != null) {
            List ruleDelegations = getRuleDelegationService().findByDelegateRuleId(defaultRule.getRuleBaseValuesId());
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
        rule.setDelegateRule(Boolean.TRUE);
        rule.loadFieldsWithDefaultValues();
        rule.createNewRuleResponsibility();

        ruleForm.getRuleDelegation().setDelegationRuleBaseValues(rule);
        ruleForm.getMyRules().addRule(rule);
        createFlexDoc(request, ruleForm, ruleForm.getMyRules().getRules());
        ruleForm.getShowHide().append().append();
        ruleForm.setEditingDelegate(true);
        rule.establishRequiredState();
        return mapping.findForward("basic");
    }

    public ActionForward removeRule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        /*
         * if (ruleForm.getRuleCreationValues().getRuleId() != null) { RuleBaseValues rule = getRuleService().findRuleBaseValuesById(ruleForm.getRuleCreationValues().getRuleId()); initializeDelegationTemplate(ruleForm, rule); ruleForm.getMyRules().getRules().clear(); }
         */
        ruleForm.getMyRules().getRules().clear();
        ruleForm.getShowHide().remove(0);
        ruleForm.getRuleCreationValues().setCreating(true);
        ruleForm.setEditingDelegate(false);
        return mapping.findForward("basic");
    }

    public ActionErrors validateCreateDelegateRule(Rule2Form ruleForm) {
        ActionErrors errors = new ActionErrors();
        if (ruleForm.getRuleCreationValues().getRuleId() == null) {
            errors.add("ruleCreationValues.ruleId", new ActionMessage("routetemplate.select", "Rule"));
        } else {
            RuleBaseValues rule = getRuleService().findRuleBaseValuesById(ruleForm.getRuleCreationValues().getRuleId());
            if (rule.getDelegateRule().booleanValue()) {
                errors.add("ruleCreationValues.ruleId", new ActionMessage("routetemplate.select", "valid Delegating Rule.  The rule \"" + rule.getDescription() + "\" is already a delegate rule"));
            }
            if (rule.getRuleTemplate().getDelegationTemplate() == null) {
                errors.add("ruleCreationValues.ruleId", new ActionMessage("routetemplate.select.ruletemplate.delegate.invalid"));
            }
        }
        if (ruleForm.getRuleCreationValues().getRuleResponsibilityKey() == null) {
            errors.add("ruleCreationValues.ruleResponsibilityKey", new ActionMessage("routetemplate.select", "responsibility to delegate"));
        }
        return errors;
    }

    public ActionForward addNewResponsibility(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        int index = ruleForm.getRuleIndex().intValue();
        WebRuleBaseValues webRule = (WebRuleBaseValues) ruleForm.getMyRules().getRule(index);
        WebRuleResponsibility responsibility = webRule.createNewRuleResponsibility();
        ruleForm.getShowHide().getChild(index).append();
        responsibility.establishRequiredState();
//        establishRequiredState(request, form);
        return mapping.findForward("basic");
    }

    public ActionForward removeResponsibility(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        int removeIndex = ruleForm.getResponsibilityIndex().intValue();
        RuleBaseValues rule = ruleForm.getMyRules().getRule(ruleForm.getRuleIndex().intValue());
        rule.removeResponsibility(removeIndex);
        ruleForm.getShowHide().getChild(ruleForm.getRuleIndex()).remove(removeIndex);
        return mapping.findForward("basic");
    }

    public ActionForward showDelegations(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        ruleForm.getRuleIndex().intValue();
        int respIndex = ruleForm.getResponsibilityIndex().intValue();
        // this should only happen for the parent rule
        WebRuleResponsibility responsibility = (WebRuleResponsibility) ruleForm.getParentRule().getResponsibility(respIndex);
        responsibility.setShowDelegations(true);
        responsibility.loadDelegations();
        return mapping.findForward(ruleForm.getForward());
    }

    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        ActionErrors errors = new ActionErrors();
        ActionForward forward = routeRule(errors, mapping, form, request, response, true);
        if (!errors.isEmpty()) {
            return forward;
        }
        ActionErrors messages = new ActionErrors();
        messages.add("hasErrors", new ActionMessage("general.routing.blanketApproved", "Delegation Rule with document id = " + ruleForm.getDocId()));
        saveErrors(request, messages);
        resetAfterRoute(ruleForm);
        return forward;
    }

    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        ActionErrors errors = new ActionErrors();
        ActionForward forward = routeRule(errors, mapping, form, request, response, false);
        if (!errors.isEmpty()) {
            return forward;
        }
        ActionErrors messages = new ActionErrors();
        messages.add("hasErrors", new ActionMessage("rule.route.confirmation", "" + ruleForm.getDocId()));
        saveErrors(request, messages);
        resetAfterRoute(ruleForm);
        return forward;
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return route(mapping, form, request, response);
    }

    public ActionForward routeRule(ActionErrors errors, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, boolean blanketApprove) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;

        MyRules2 rules = ruleForm.getMyRules();
        if (rules.getRules().size() > 1) {
            errors.add("hasErrors", new ActionMessage("rule.delegation.multipleRoute"));
            saveErrors(request, errors);
            return mapping.findForward("basic");
        }
        int ruleIndex = 0;
        for (Iterator iter = rules.getRules().iterator(); iter.hasNext();) {
            WebRuleBaseValues rule = (WebRuleBaseValues) iter.next();
            rule.validateRule("myRules.rule[" + ruleIndex + "].", errors);
            ruleIndex++;
        }
        if (!errors.isEmpty()) {
            errors.add("hasErrors", new ActionMessage("general.hasErrors"));
            saveErrors(request, errors);
            return mapping.findForward("basic");
        }

        WebRuleBaseValues delegateRule = rules.getRule(0);
        delegateRule.setCurrentInd(Boolean.FALSE);
        boolean locked = false;
        if (delegateRule.getPreviousVersionId() != null) {
            locked = checkLockedForRouting(errors, delegateRule);
        } else {
            locked = checkLockedForRouting(errors, ruleForm.getParentRule());
        }
        if (locked) {
            saveErrors(request, errors);
            return mapping.findForward("basic");
        }
        
        // get the parent rule of the delegation-rule being submitted
        RuleBaseValues parentRule = getRuleService().findRuleBaseValuesById(ruleForm.getParentRule().getRuleBaseValuesId());
        // new delegation rule
        //        if (delegateRule.getPreviousVersionId() == null) {
        
        Long previousVersionId = parentRule.getRuleBaseValuesId();

        RuleResponsibility delegateResponsibility = parentRule.getResponsibility(ruleForm.getRuleCreationValues().getRuleResponsibilityKey());

        RuleDelegation ruleDelegation = ruleForm.getRuleDelegation();
        ruleDelegation.setDelegationRuleBaseValues(delegateRule);
        ruleDelegation.setRuleResponsibility(delegateResponsibility);

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

        getRuleService().routeRuleWithDelegate(ruleForm.getDocId(), parentRule, delegateRule, getUserSession(request).getWorkflowUser(), ruleForm.getAnnotation(), blanketApprove);
        ruleForm.setCurrentRuleId(delegateRule.getRuleBaseValuesId());
        return new Rule2Action().report(mapping, form, request, response);
    }

    private void resetAfterRoute(Rule2Form ruleForm) {
        ruleForm.getMyRules().getRules().clear();
        ruleForm.getRuleCreationValues().setCreating(true);
        ruleForm.setEditingDelegate(false);
        ruleForm.setDocId(null);
    }

    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form rule2Form = (Rule2Form) form;
        String ruleBaseValuesId = request.getParameter("ruleBaseValuesId");
        if ("".equals(ruleBaseValuesId)) {
            rule2Form.setParentRule(new WebRuleBaseValues());
            rule2Form.getRuleCreationValues().setRuleId(null);
        } else if (ruleBaseValuesId != null) {
            Long ruleId = new Long(ruleBaseValuesId);
            RuleBaseValues rule = getRuleService().findRuleBaseValuesById(ruleId);

            if (rule.getDelegateRule().booleanValue()) {
                ActionErrors errors = new ActionErrors();
                errors.add("ruleCreationValues.ruleId", new ActionMessage("routetemplate.select", "valid Delegating Rule.  The rule \"" + rule.getDescription() + "\" is already a delegate rule"));
                saveErrors(request, errors);
                return mapping.findForward("basic");
            }
            //rule2Form.getParentRule().edit(rule);
            rule2Form.getRuleCreationValues().setRuleId(ruleId);
            rule2Form.setResponsibility(null);
            initializeDelegationTemplate(rule2Form, rule);
        }
        String ruleTemplateIdValue = request.getParameter("ruleTemplate.ruleTemplateId");
        if ("".equals(ruleTemplateIdValue)) {
            rule2Form.getRuleCreationValues().setRuleTemplateId(null);
            rule2Form.getRuleCreationValues().setRuleTemplateName("");
            rule2Form.getRuleCreationValues().setManualDelegationTemplate(false);
        } else if (ruleTemplateIdValue != null) {
            Long ruleTemplateId = new Long(ruleTemplateIdValue);
            rule2Form.getRuleCreationValues().setRuleTemplateId(ruleTemplateId);
            RuleTemplate template = getRuleTemplateService().findByRuleTemplateId(rule2Form.getRuleCreationValues().getRuleTemplateId());
            rule2Form.getRuleCreationValues().setRuleTemplateName(template.getName());
            rule2Form.getRuleCreationValues().setManualDelegationTemplate(true);
        }
        establishRequiredState(request, form);
        return mapping.findForward("basic");
    }

    private void createFlexDoc(HttpServletRequest request, Rule2Form rule2Form, List rules) throws WorkflowException {
        if (rule2Form.getFlexDoc() == null) {
            
//            rule2Form.setFlexDoc(new WorkflowDocument(EdenConstants.RULE_DOCUMENT_NAME, getUserSession(request).getWorkflowUser(), EdenConstants.EDEN_APP_CODE));
        	String ruleDocTypeName = getRuleService().getRuleDocmentTypeName(rules);
            rule2Form.setFlexDoc(new WorkflowDocument(new WorkflowIdVO(getUserSession(request).getWorkflowUser().getWorkflowId()), ruleDocTypeName));
            rule2Form.setDocId(rule2Form.getFlexDoc().getRouteHeaderId());
            rule2Form.establishVisibleActionRequestCds();
        }
    }

    private boolean checkLockedForRouting(ActionErrors errors, RuleBaseValues rule) {
        if ("true".equalsIgnoreCase(Utilities.getApplicationConstant(EdenConstants.RULE_LOCKING_ON))) {
            Long id = rule.getRuleBaseValuesId();
            if (id != null) {
                Long routeHeaderId = getRuleService().isLockedForRouting(id);
                if (routeHeaderId != null) {
                    errors.add("hasErrors", new ActionMessage("routetemplate.RuleService.ruleInRoute", "" + routeHeaderId.longValue()));
                    return true;
                }
            }
        }
        return false;
    }

    private void stripParentRule(RuleBaseValues parentRule, RuleResponsibility ruleResponsibility) {
        for (Iterator iterator = parentRule.getResponsibilities().iterator(); iterator.hasNext();) {
            RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
            if (ruleResponsibility != null && !responsibility.getRuleResponsibilityKey().equals(ruleResponsibility.getRuleResponsibilityKey())) {
                iterator.remove();
            } else {
                responsibility.setDelegationRules(new ArrayList());
            }
        }
    }

    private void initializeDelegationTemplate(Rule2Form form, RuleBaseValues rule) {
        RuleTemplate delegationTemplate = rule.getRuleTemplate().getDelegationTemplate();
        if (delegationTemplate != null && !form.getRuleCreationValues().isManualDelegationTemplate()) {
            form.getRuleCreationValues().setRuleTemplateId(delegationTemplate.getRuleTemplateId());
            form.getRuleCreationValues().setRuleTemplateName(delegationTemplate.getName());
        }
    }

    private ShowHideTree initializeShowHide(MyRules2 myRules) {
        ShowHideTree showHide = new ShowHideTree();
        for (Iterator ruleIt = myRules.getRules().iterator(); ruleIt.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) ruleIt.next();
            initializeShowHide(showHide, rule);
        }
        return showHide;
    }

    private ShowHideTree initializeShowHide(ShowHideTree showHide, RuleBaseValues rule) {
        ShowHideTree ruleTree = showHide.append();
        for (Iterator respIt = rule.getResponsibilities().iterator(); respIt.hasNext();) {
            RuleResponsibility responsibility = (RuleResponsibility) respIt.next();
            ShowHideTree respTree = ruleTree.append();
            for (Iterator delIt = responsibility.getDelegationRules().iterator(); delIt.hasNext();) {
                RuleDelegation delegation = (RuleDelegation) delIt.next();
                ShowHideTree delTree = respTree.append();
                for (Iterator delRespIt = delegation.getDelegationRuleBaseValues().getResponsibilities().iterator(); delRespIt.hasNext();) {
                    delRespIt.next();
                    delTree.append();
                }
            }
        }
        return ruleTree;
    }

    private RuleService getRuleService() {
        return (RuleService) KEWServiceLocator.getService(KEWServiceLocator.RULE_SERVICE);
    }

    private RuleTemplateService getRuleTemplateService() {
        return (RuleTemplateService) KEWServiceLocator.getService(KEWServiceLocator.RULE_TEMPLATE_SERVICE);
    }

    private RuleDelegationService getRuleDelegationService() {
        return (RuleDelegationService) KEWServiceLocator.getService(KEWServiceLocator.RULE_DELEGATION_SERVICE);
    }
}