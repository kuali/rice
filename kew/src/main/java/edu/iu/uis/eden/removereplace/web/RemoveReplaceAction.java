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
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.IDocHandler;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.removereplace.RemoveReplaceDocument;
import edu.iu.uis.eden.removereplace.RuleTarget;
import edu.iu.uis.eden.removereplace.WorkgroupTarget;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleResponsibility;
import edu.iu.uis.eden.routing.web.DocHandlerForm;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.util.CodeTranslator;
import edu.iu.uis.eden.util.KeyLabelPair;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.WorkflowAction;
import edu.iu.uis.eden.web.session.UserSession;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;
import edu.iu.uis.eden.workgroup.Workgroup;
import edu.iu.uis.eden.workgroup.web.WorkgroupForm;

/**
 * Struts Action for the Remove/Replace User Document.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RemoveReplaceAction extends WorkflowAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RemoveReplaceAction.class);

    private static final String INVALID_USER_ID_MSG = "removereplace.invalidUserId";
    private static final String USER_ID_NOT_FOUND_MSG = "removereplace.userIdNotFound";
    private static final String INVALID_REPLACEMENT_USER_ID_MSG = "removereplace.invalidReplacementUserId";
    private static final String REPLACEMENT_USER_ID_NOT_FOUND_MSG = "removereplace.replacementUserIdNotFound";
    private static final String REPLACEMENT_USER_ID_REQUIRED_MSG = "removereplace.replacementUserIdRequired";
    private static final String INVALID_OPERATION_MSG = "removereplace.invalidOperation";
    private static final String FAILED_DOCUMENT_LOAD_MSG = "removereplace.failedDocumentLoad";

    @Override
    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm actionForm) throws Exception {
	ActionMessages messages = new ActionMessages();
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
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(USER_ID_NOT_FOUND_MSG, form.getUserId()));
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
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(REPLACEMENT_USER_ID_NOT_FOUND_MSG, form.getReplacementUserId()));
	    }
	}
	form.setWorkgroupTypes(KEWServiceLocator.getWorkgroupTypeService().findAllActive());
        form.getWorkgroupTypes().add(0, RemoveReplaceForm.createDefaultWorkgroupType());
	return messages;
    }

    private WorkflowDocument createDocument() throws WorkflowException {
	return new WorkflowDocument(new WorkflowIdVO(UserSession.getAuthenticatedUser().getWorkflowUser().getWorkflowId()), EdenConstants.REMOVE_REPLACE_DOCUMENT_TYPE);
    }

    @Override
    public ActionForward start(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	RemoveReplaceForm form = (RemoveReplaceForm) actionForm;
        form.getShowHide().getChild(0).setShow(true);
        form.getShowHide().getChild(1).setShow(true);
	return mapping.findForward("basic");
    }

    public ActionForward selectOperation(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
	ActionMessages messages = new ActionMessages();
	RemoveReplaceForm form = (RemoveReplaceForm) actionForm;
	// validate that an operation was entered
	if (!form.isRemove() && !form.isReplace()) {
	    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(INVALID_OPERATION_MSG));
	}
	// validity of IDs are validated in establishRequiredState, we just need to validate existence here
	if (StringUtils.isBlank(form.getUserId())) {
	    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(INVALID_USER_ID_MSG));
	}
	if (form.isReplace() && StringUtils.isBlank(form.getReplacementUserId())) {
	    messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(REPLACEMENT_USER_ID_REQUIRED_MSG));
	}
	if (!messages.isEmpty()) {
	    saveMessages(request, messages);
	    return mapping.findForward("basic");
	}
	form.setOperationSelected(true);
	// clear out any workgroups or rules, because they might have just changed the user
	form.getRules().clear();
	form.getWorkgroups().clear();
	form.getShowHide().getChild(0).setShow(true);
        form.getShowHide().getChild(1).setShow(true);
	return mapping.findForward("basic");
    }

    public ActionForward changeOperation(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
	RemoveReplaceForm form = (RemoveReplaceForm) actionForm;
	form.setOperationSelected(false);
	return mapping.findForward("basic");
    }

    public ActionForward docHandler(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RemoveReplaceForm form = (RemoveReplaceForm) actionForm;
        if (IDocHandler.INITIATE_COMMAND.equalsIgnoreCase(form.getCommand())) {
            return start(mapping, form, request, response);
        }
        Long documentId = form.getDocId();
        RemoveReplaceDocument removeReplaceDocument = KEWServiceLocator.getRemoveReplaceDocumentService().findById(documentId);
        if (removeReplaceDocument == null) {
            ActionMessages messages = new ActionMessages();
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(FAILED_DOCUMENT_LOAD_MSG, documentId));
            saveMessages(request, messages);
            return mapping.findForward("docHandler");
        }
        loadFormForReport(form, removeReplaceDocument);
        return mapping.findForward("docHandler");
    }

    protected void loadFormForReport(RemoveReplaceForm form, RemoveReplaceDocument document) throws EdenUserNotFoundException {
        form.setDocument(document);
        form.setOperation(document.getOperation());
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(document.getUserWorkflowId()));
        form.setUserId(user.getAuthenticationUserId().getAuthenticationId());
        form.setUser(user);
        if (!StringUtils.isBlank(document.getReplacementUserWorkflowId())) {
            WorkflowUser replacementUser = KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(document.getReplacementUserWorkflowId()));
            form.setReplacementUserId(replacementUser.getAuthenticationUserId().getAuthenticationId());
            form.setReplacementUser(replacementUser);
        }
        form.setRules(loadRemoveReplaceRules(form, loadRules(document)));
        form.setWorkgroups(loadRemoveReplaceWorkgroups(form, loadWorkgroups(document)));
        form.setReport(true);
        form.getShowHide().getChild(0).setShow(!form.getRules().isEmpty());
        form.getShowHide().getChild(1).setShow(!form.getWorkgroups().isEmpty());
    }

    protected List<Workgroup> loadWorkgroups(RemoveReplaceDocument document) {
	List<Workgroup> workgroups = new ArrayList<Workgroup>();
	for (WorkgroupTarget workgroupTarget : document.getWorkgroupTargets()) {
	    Workgroup workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(workgroupTarget.getWorkgroupId()));
	    if (workgroup == null) {
		throw new WorkflowRuntimeException("Failed to locate workgroup with id " + workgroupTarget.getWorkgroupId());
	    }
	    workgroups.add(workgroup);
	}
	return workgroups;
    }

    protected List<RuleBaseValues> loadRules(RemoveReplaceDocument document) {
	List<RuleBaseValues> rules = new ArrayList<RuleBaseValues>();
	for (RuleTarget ruleTarget : document.getRuleTargets()) {
	    RuleBaseValues rule = KEWServiceLocator.getRuleService().findRuleBaseValuesById(ruleTarget.getRuleId());
	    if (rule == null) {
		throw new WorkflowRuntimeException("Failed to locate rule with id " + ruleTarget.getRuleId());
	    }
	    rules.add(rule);
	}
	return rules;
    }


    public ActionForward chooseRules(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	RemoveReplaceForm form = (RemoveReplaceForm)actionForm;
	// this condition should already be satisfied but throw an error if it's not
	if (form.getUser() == null) {
	    throw new RuntimeException("Please enter a valid user id before choosing rules.");
	}
	List<RuleBaseValues> rules = KEWServiceLocator.getRuleService().findRuleBaseValuesByResponsibilityReviewerTemplateDoc(form.getRuleRuleTemplate(), form.getRuleDocumentTypeName(), form.getUser().getWorkflowId(), EdenConstants.RULE_RESPONSIBILITY_WORKFLOW_ID);
	form.getRules().clear();
	form.getRules().addAll(loadRemoveReplaceRules(form, rules));
	return mapping.findForward("basic");
    }

    /**
     * Constructs a list of RemoveReplaceRule objects from the given list of RuleBaseValues.
     */
    protected List<RemoveReplaceRule> loadRemoveReplaceRules(RemoveReplaceForm form, List<RuleBaseValues> rules) {
	List<RemoveReplaceRule> removeReplaceRules = new ArrayList<RemoveReplaceRule>();
	Set<Long> selectedRuleIds = getSelectedRuleIds(form);
	for (RuleBaseValues rule : rules) {
	    RemoveReplaceRule removeReplaceRule = new RemoveReplaceRule();
	    removeReplaceRule.setRule(rule);
	    removeReplaceRule.setRuleTemplateName(rule.getRuleTemplateName());
	    // if rule was selected previously, keep it selected
	    removeReplaceRule.setSelected(selectedRuleIds.contains(rule.getRuleBaseValuesId()));
	    Long documentId = KEWServiceLocator.getRuleService().isLockedForRouting(rule.getRuleBaseValuesId());
	    if (documentId != null) {
		removeReplaceRule.setWarning(removeReplaceRule.getWarning().concat("Rule is locked by document " + documentId + " and cannot be modified."));
		removeReplaceRule.setDisabled(true);
		removeReplaceRule.setSelected(false);
	    }
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
	    removeReplaceRules.add(removeReplaceRule);
	}
	return removeReplaceRules;
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
	    // this condition should already be satisfied but throw an error if it's not
	    throw new RuntimeException("Please enter a valid user id before choosing workgroups.");
	}
	Workgroup template = KEWServiceLocator.getWorkgroupService().getBlankWorkgroup();
	template.setActiveInd(Boolean.TRUE);
	if (!StringUtils.isBlank(form.getWorkgroupType())) {
	    template.setWorkgroupType(form.getWorkgroupType());
	}
	List<Workgroup> workgroups = KEWServiceLocator.getWorkgroupService().search(template, null, form.getUser());
	form.getWorkgroups().clear();
	form.getWorkgroups().addAll(loadRemoveReplaceWorkgroups(form, workgroups));
	return mapping.findForward("basic");
    }

    /**
     * Constructs a list of RemoveReplaceWorkgroup objects from the given list of Workgroups.
     */
    protected List<RemoveReplaceWorkgroup> loadRemoveReplaceWorkgroups(RemoveReplaceForm form, List<Workgroup> workgroups) {
	List<RemoveReplaceWorkgroup> removeReplaceWorkgroups = new ArrayList<RemoveReplaceWorkgroup>();
	Set<Long> selectedWorkgroupIds = getSelectedWorkgroupIds(form);
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
	    try {
		Long documentId = KEWServiceLocator.getWorkgroupRoutingService().getLockingDocumentId(workgroup.getWorkflowGroupId());
		if (documentId != null) {
		    warnings.add("Workgroup is locked by document " + documentId + " and cannot be modified.");
		    removeReplaceWorkgroup.setDisabled(true);
		    removeReplaceWorkgroup.setSelected(false);
		}
	    } catch (WorkflowException e) {
		throw new WorkflowRuntimeException(e);
	    }
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
	    } else {
		removeReplaceWorkgroups.add(removeReplaceWorkgroup);
	    }
	}
	return removeReplaceWorkgroups;
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
	loadFormForReport(form, document);
	ActionMessages messages = new ActionMessages();
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("general.routing.blanketApproved", "Remove/Replace User Document"));
        saveMessages(request, messages);
	return mapping.findForward("summary");
    }

    public ActionForward route(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
	    HttpServletResponse response) throws Exception {
	RemoveReplaceForm form = (RemoveReplaceForm)actionForm;
	validateSubmission(form);
	RemoveReplaceDocument document = createRemoveReplaceDocument(form);
	KEWServiceLocator.getRemoveReplaceDocumentService().route(document, UserSession.getAuthenticatedUser(), form.getAnnotation());
	loadFormForReport(form, document);
	ActionMessages messages = new ActionMessages();
        messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("general.routing.routed", "Remove/Replace User Document"));
        saveMessages(request, messages);
	return mapping.findForward("summary");
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

    public ActionForward performLookup(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String lookupService = request.getParameter("lookupableImplServiceName");
        String conversionFields = request.getParameter("conversionFields");
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + mapping.getModuleConfig().getPrefix();
        StringBuffer lookupUrl = new StringBuffer(basePath);
        lookupUrl.append("/Lookup.do?methodToCall=start&docFormKey=").append(getUserSession(request).addObject(actionForm)).append("&lookupableImplServiceName=");
        lookupUrl.append(lookupService);
        lookupUrl.append("&conversionFields=").append(conversionFields);
        lookupUrl.append("&returnLocation=").append(basePath).append(mapping.getPath()).append(".do");
        return new ActionForward(lookupUrl.toString(), true);
    }

    public ActionForward cancel(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("entering cancel() method ...");
        RemoveReplaceForm form = (RemoveReplaceForm)actionForm;
        form.getFlexDoc().cancel("");
        saveDocumentActionMessage("general.routing.canceled", request);
        LOG.info("forwarding to actionTaken from cancel()");
        return mapping.findForward("actionTaken");
    }

    public void saveDocumentActionMessage(String messageKey, HttpServletRequest request) {
        saveDocumentActionMessage(messageKey, request, null);
    }

    public void saveDocumentActionMessage(String messageKey, HttpServletRequest request, String secondMessageParameter) {
        ActionMessages messages = new ActionMessages();
        if (Utilities.isEmpty(secondMessageParameter)) {
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(messageKey, "document"));
        } else {
            messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(messageKey, "document", secondMessageParameter));
        }
        saveMessages(request, messages);
    }


}
