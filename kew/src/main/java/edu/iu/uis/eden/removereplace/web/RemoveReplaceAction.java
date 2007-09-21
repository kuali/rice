/*
 * Copyright 2007 The Kuali Foundation
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
package edu.iu.uis.eden.removereplace.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.removereplace.RemoveReplaceDocument;
import edu.iu.uis.eden.removereplace.RuleTarget;
import edu.iu.uis.eden.removereplace.WorkgroupTarget;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleResponsibility;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.CodeTranslator;
import edu.iu.uis.eden.web.WorkflowAction;
import edu.iu.uis.eden.web.session.UserSession;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * Struts Action for the Remove/Replace User Document.
 *
 * @author Eric Westfall
 *
 */
public class RemoveReplaceAction extends WorkflowAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RemoveReplaceAction.class);

    @Override
    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm actionForm) throws Exception {
	RemoveReplaceForm form = (RemoveReplaceForm)actionForm;
	form.setActionRequestCodes(CodeTranslator.arLabels);
	boolean isCreating = false;
	if (form.getDocId() != null) {
            form.setFlexDoc(new WorkflowDocument(new WorkflowIdVO(getUserSession(request).getWorkflowUser().getWorkflowId()), form.getDocId()));
        } else {
            // we're creating a new one if this action is being invoked without a method to call or with "start"
            isCreating = StringUtils.isEmpty(form.getMethodToCall()) || form.getMethodToCall().equals("start");
            if (isCreating) {
        	form.setFlexDoc(createDocument());
        	form.setDocId(form.getFlexDoc().getRouteHeaderId());
        	form.establishVisibleActionRequestCds();
            }
        }
	form.establishVisibleActionRequestCds();
	if (!StringUtils.isEmpty(form.getUserId())) {
	    WorkflowUser user = null;
	    try {
		user = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(form.getUserId()));
	    } catch (EdenUserNotFoundException e) {
		LOG.warn("User not found.", e);
	    }
	    form.setUser(user);
	    if (user == null) {
		// TODO encode this in an error message that shows up next to the field
		throw new RuntimeException("Please enter a valid user id.");
	    }
	}
	if (!StringUtils.isEmpty(form.getReplacementUserId())) {
	    WorkflowUser replacementUser = null;
	    try {
		replacementUser = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(form.getReplacementUserId()));
	    } catch (EdenUserNotFoundException e) {
		LOG.warn("Replacement user not found.", e);
	    }
	    form.setReplacementUser(replacementUser);
	    if (replacementUser == null) {
		// TODO encode this in an error message that shows up next to the field
		throw new RuntimeException("Please enter a valid replacement user id.");
	    }
	}
	form.setWorkgroupTypes(KEWServiceLocator.getWorkgroupTypeService().findAllActive());
        form.getWorkgroupTypes().add(0, RemoveReplaceForm.createDefaultWorkgroupType());
	return null;
    }

    private WorkflowDocument createDocument() throws WorkflowException {
	return new WorkflowDocument(new WorkflowIdVO(UserSession.getAuthenticatedUser().getWorkflowUser().getWorkflowId()), EdenConstants.REMOVE_REPLACE_DOCUMENT_TYPE);
    }

    @Override
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	return mapping.findForward("basic");
    }

    public ActionForward chooseRules(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	RemoveReplaceForm form = (RemoveReplaceForm)actionForm;
	if (form.getUser() == null) {
	    // TODO an error message, user must be selected before choosing rules
	    throw new RuntimeException("Please enter a valid user id before choosing rules.");
	}
	List<RuleBaseValues> rules = KEWServiceLocator.getRuleService().findRuleBaseValuesByResponsibilityReviewerTemplateDoc(form.getRuleRuleTemplate(), form.getRuleDocumentTypeName(), form.getUser().getWorkflowId(), EdenConstants.RULE_RESPONSIBILITY_WORKFLOW_ID);
	Set<Long> selectedRuleIds = getSelectedRuleIds(form);
	form.getRules().clear();
	for (RuleBaseValues rule : rules) {
	    RemoveReplaceRule removeReplaceRule = new RemoveReplaceRule();
	    removeReplaceRule.setRule(rule);
	    removeReplaceRule.setRuleTemplateName(rule.getRuleTemplateName());
	    // if rule was selected previously, keep it selected
	    removeReplaceRule.setSelected(selectedRuleIds.contains(rule.getRuleBaseValuesId()));
	    ResponsibilityEvaluation eval = evaluateResponsibility(form, rule);
	    if (!eval.foundResponsibility) {
		LOG.warn("Failed to find a valid responsbility on rule " + rule.getRuleBaseValuesId() + " for user " + form.getUserId() + ".  This rule will not be added to the list for selection.");
	    }
	    for (String warning : eval.warnings) {
		if (!StringUtils.isEmpty(removeReplaceRule.getWarning())) {
		    removeReplaceRule.setWarning(removeReplaceRule.getWarning().concat("<br>"));
		}
		removeReplaceRule.setWarning(removeReplaceRule.getWarning().concat(warning));
	    }

	    form.getRules().add(removeReplaceRule);
	}
	return mapping.findForward("basic");
    }

    private Set<Long> getSelectedRuleIds(RemoveReplaceForm form) {
	Set<Long> selectedRuleIds = new HashSet<Long>();
	for (RemoveReplaceRule rule : form.getRules()) {
	    if (rule.isSelected()) {
		selectedRuleIds.add(rule.getRule().getRuleBaseValuesId());
	    }
	}
	return selectedRuleIds;
    }

    private ResponsibilityEvaluation evaluateResponsibility(RemoveReplaceForm form, RuleBaseValues rule) {
	ResponsibilityEvaluation eval = new ResponsibilityEvaluation();
	List<RuleResponsibility> responsibilities = rule.getResponsibilities();
	for (RuleResponsibility responsibility : responsibilities) {
	    if (EdenConstants.RULE_RESPONSIBILITY_WORKFLOW_ID.equals(responsibility.getRuleResponsibilityType()) &&
		    responsibility.getRuleResponsibilityName().equals(form.getUser().getWorkflowId())) {
		eval.foundResponsibility = true;
		eval.hasDelegations = responsibility.getDelegationRules().size() > 0;
	    } else {
		eval.hasOtherResponsibility = true;
	    }
	}
	if (RemoveReplaceDocument.REMOVE_OPERATION.equals(form.getOperation()) && !eval.hasOtherResponsibility) {
	    eval.warnings.add("Only one user on the rule, removing them will inactivate the rule.");
	}
	if (eval.hasDelegations) {
	    eval.warnings.add("This rule has delegations which will be inactivated.");
	}
	return eval;
    }

    public ActionForward chooseWorkgroups(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	RemoveReplaceForm form = (RemoveReplaceForm)actionForm;
	if (form.getUser() == null) {
	    // TODO an error message, user must be selected before choosing workgroups
	    throw new RuntimeException("Please enter a valid user id before choosing workgroups.");
	}
	Workgroup template = KEWServiceLocator.getWorkgroupService().getBlankWorkgroup();
	template.setActiveInd(Boolean.TRUE);
	if (!StringUtils.isBlank(form.getWorkgroupType())) {
	    template.setWorkgroupType(form.getWorkgroupType());
	}
	List<Workgroup> workgroups = KEWServiceLocator.getWorkgroupService().search(template, null, form.getUser());
	Set<Long> selectedWorkgroupIds = getSelectedWorkgroupIds(form);
	form.getWorkgroups().clear();
	for (Workgroup workgroup : workgroups) {
	    RemoveReplaceWorkgroup removeReplaceWorkgroup = new RemoveReplaceWorkgroup();
	    removeReplaceWorkgroup.setId(workgroup.getWorkflowGroupId().getGroupId());
	    removeReplaceWorkgroup.setName(workgroup.getGroupNameId().getNameId());
	    removeReplaceWorkgroup.setType(workgroup.getWorkgroupType());
	    removeReplaceWorkgroup.setWarning("");
	    // if workgroup was selected previously, keep it selected
	    removeReplaceWorkgroup.setSelected(selectedWorkgroupIds.contains(workgroup.getWorkflowGroupId().getGroupId()));
	    boolean isOnlyMember = true;
	    boolean foundMember = false;
	    for (Recipient member : workgroup.getMembers()) {
		if (member instanceof WorkflowUser && ((WorkflowUser)member).getWorkflowId().equals(form.getUser().getWorkflowId())) {
		    foundMember = true;
		} else {
		    isOnlyMember = false;
		}
	    }
	    List<String> warnings = new ArrayList<String>();
	    if (RemoveReplaceDocument.REMOVE_OPERATION.equals(form.getOperation()) && isOnlyMember) {
		warnings.add("Only one member on the workgroup, removing them will inactivate the workgroup.");
	    }
	    for (String warning : warnings) {
		if (!StringUtils.isEmpty(removeReplaceWorkgroup.getWarning())) {
		    removeReplaceWorkgroup.setWarning(removeReplaceWorkgroup.getWarning().concat("<br>"));
		}
		removeReplaceWorkgroup.setWarning(removeReplaceWorkgroup.getWarning().concat(warning));
	    }
	    if (!foundMember) {
		LOG.warn("Failed to find a valid member on workgroup " + workgroup.getDisplayName() + " for user " + form.getUserId() + ".  This workgroup will not be added to the list for selection.");
	    }
	    form.getWorkgroups().add(removeReplaceWorkgroup);
	}
	return mapping.findForward("basic");
    }

    private Set<Long> getSelectedWorkgroupIds(RemoveReplaceForm form) {
	Set<Long> selectedWorkgroupIds = new HashSet<Long>();
	for (RemoveReplaceWorkgroup workgroup : form.getWorkgroups()) {
	    if (workgroup.isSelected()) {
		selectedWorkgroupIds.add(workgroup.getId());
	    }
	}
	return selectedWorkgroupIds;
    }

    private class ResponsibilityEvaluation {
	public boolean foundResponsibility = false;
	public boolean hasOtherResponsibility = false;
	public boolean hasDelegations = false;
	public List<String> warnings = new ArrayList<String>();
    }

    public ActionForward blanketApprove(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	RemoveReplaceForm form = (RemoveReplaceForm)actionForm;
	validateSubmission(form);
	RemoveReplaceDocument document = createRemoveReplaceDocument(form);
	KEWServiceLocator.getRemoveReplaceDocumentService().blanketApprove(document, UserSession.getAuthenticatedUser(), form.getAnnotation());
	return mapping.findForward("basic");
    }

    public ActionForward route(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	RemoveReplaceForm form = (RemoveReplaceForm)actionForm;
	validateSubmission(form);
	RemoveReplaceDocument document = createRemoveReplaceDocument(form);
	KEWServiceLocator.getRemoveReplaceDocumentService().blanketApprove(document, UserSession.getAuthenticatedUser(), form.getAnnotation());
	return mapping.findForward("basic");
    }

    private void validateSubmission(RemoveReplaceForm form) {
	if (form.getUser() == null) {
	    throw new RuntimeException("Please select a user.");
	}
	if (form.getOperation().equals(RemoveReplaceDocument.REPLACE_OPERATION) && form.getReplacementUser() == null) {
	    throw new RuntimeException("Please select a replacement user.");
	}
    }

    private RemoveReplaceDocument createRemoveReplaceDocument(RemoveReplaceForm form) {
	RemoveReplaceDocument document = new RemoveReplaceDocument();
	document.setDocumentId(form.getDocId());
	document.setOperation(form.getOperation());
	document.setUserWorkflowId(form.getUser().getWorkflowId());
	if (form.getReplacementUser() != null) {
	    document.setReplacementUserWorkflowId(form.getReplacementUser().getWorkflowId());
	}
	List<RuleTarget> ruleTargets = new ArrayList<RuleTarget>();
	for (RemoveReplaceRule rule : form.getRules()) {
	    if (rule.isSelected()) {
		RuleTarget target = new RuleTarget();
		target.setRuleId(rule.getRule().getRuleBaseValuesId());
		ruleTargets.add(target);
	    }
	}
	List<WorkgroupTarget> workgroupTargets = new ArrayList<WorkgroupTarget>();
	for (RemoveReplaceWorkgroup workgroup : form.getWorkgroups()) {
	    if (workgroup.isSelected()) {
		WorkgroupTarget target = new WorkgroupTarget();
		target.setWorkgroupId(workgroup.getId());
		workgroupTargets.add(target);
	    }
	}
	document.setRuleTargets(ruleTargets);
	document.setWorkgroupTargets(workgroupTargets);
	return document;
    }


}
