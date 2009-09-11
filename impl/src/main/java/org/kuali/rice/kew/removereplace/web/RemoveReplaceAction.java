/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kew.removereplace.web;

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
import org.kuali.rice.kew.dto.WorkflowIdDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.removereplace.RemoveReplaceDocument;
import org.kuali.rice.kew.removereplace.RuleTarget;
import org.kuali.rice.kew.removereplace.WorkgroupTarget;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowDocument;
import org.kuali.rice.kew.util.CodeTranslator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.web.KewKualiAction;
import org.kuali.rice.kew.web.session.UserSession;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;


/**
 * Struts Action for the Remove/Replace User Document.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RemoveReplaceAction extends KewKualiAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RemoveReplaceAction.class);

    private static final String INVALID_USER_ID_MSG = "removereplace.invalidUserId";
    private static final String USER_ID_NOT_FOUND_MSG = "removereplace.userIdNotFound";
    private static final String INVALID_REPLACEMENT_USER_ID_MSG = "removereplace.invalidReplacementUserId";
    private static final String REPLACEMENT_USER_ID_NOT_FOUND_MSG = "removereplace.replacementUserIdNotFound";
    private static final String REPLACEMENT_USER_ID_REQUIRED_MSG = "removereplace.replacementUserIdRequired";
    private static final String INVALID_OPERATION_MSG = "removereplace.invalidOperation";
    private static final String FAILED_DOCUMENT_LOAD_MSG = "removereplace.failedDocumentLoad";

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        // TODO jjhanso - THIS METHOD NEEDS JAVADOCS
        initForm(request, form);
        return super.execute(mapping, form, request, response);
    }

    @Override
    public ActionForward start(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
        HttpServletResponse response) throws Exception {
        RemoveReplaceForm form = (RemoveReplaceForm) actionForm;
        form.getShowHide().getChild(0).setShow(true);
        form.getShowHide().getChild(1).setShow(true);
        return super.start(mapping, actionForm, request, response);
    }

    public ActionMessages initForm(HttpServletRequest request, ActionForm actionForm) throws Exception {
	ActionMessages messages = new ActionMessages();
	RemoveReplaceForm form = (RemoveReplaceForm)actionForm;
	form.setActionRequestCodes(CodeTranslator.arLabels);
	boolean isCreating = false;
	if (form.getDocId() != null) {
            form.setWorkflowDocument(new WorkflowDocument(getUserSession(request).getPrincipalId(), form.getDocId()));
        } else {
            // we're creating a new one if this action is being invoked without a method to call or with "start"
            isCreating = StringUtils.isEmpty(form.getMethodToCall()) || form.getMethodToCall().equals("start");
            if (isCreating) {
        	form.setWorkflowDocument(createDocument());
        	form.setDocId(form.getWorkflowDocument().getRouteHeaderId());
        	form.establishVisibleActionRequestCds();
            }
        }
	form.establishVisibleActionRequestCds();
	if (!StringUtils.isEmpty(form.getUserId())) {
	    Person user = null;
	    try {
	    user = KIMServiceLocator.getPersonService().getPersonByPrincipalName(form.getUserId());
	    } catch (Exception e) {
		LOG.warn("User not found.", e);
	    }
	    form.setUser(user);
	    if (user == null) {
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(USER_ID_NOT_FOUND_MSG, form.getUserId()));
	    }
	}
	if (!StringUtils.isEmpty(form.getReplacementUserId())) {
	    Person replacementUser = null;
	    try {
		replacementUser = KIMServiceLocator.getPersonService().getPersonByPrincipalName(form.getReplacementUserId());
	    } catch (Exception e) {
		LOG.warn("Replacement user not found.", e);
	    }
	    form.setReplacementUser(replacementUser);
	    if (replacementUser == null) {
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(REPLACEMENT_USER_ID_NOT_FOUND_MSG, form.getReplacementUserId()));
	    }
	}

        return messages;
    }

    private WorkflowDocument createDocument() throws WorkflowException {
	return new WorkflowDocument(new WorkflowIdDTO(UserSession.getAuthenticatedUser().getPrincipalId()), KEWConstants.REMOVE_REPLACE_DOCUMENT_TYPE);
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
        if (KEWConstants.INITIATE_COMMAND.equalsIgnoreCase(form.getCommand())) {
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

    protected void loadFormForReport(RemoveReplaceForm form, RemoveReplaceDocument document) {
        form.setDocument(document);
        form.setOperation(document.getOperation());
        Person user = KIMServiceLocator.getPersonService().getPerson(document.getUserWorkflowId());
        form.setUserId(user.getPrincipalName());
        form.setUser(user);
        if (!StringUtils.isBlank(document.getReplacementUserWorkflowId())) {
            Person replacementUser = KIMServiceLocator.getPersonService().getPerson(document.getReplacementUserWorkflowId());
            form.setReplacementUserId(replacementUser.getPrincipalId());
            form.setReplacementUser(replacementUser);
        }
        form.setRules(loadRemoveReplaceRules(form, loadRules(document)));
        form.setWorkgroups(loadRemoveReplaceWorkgroups(form, loadWorkgroups(document)));
        form.setReport(true);
        form.getShowHide().getChild(0).setShow(!form.getRules().isEmpty());
        form.getShowHide().getChild(1).setShow(!form.getWorkgroups().isEmpty());
    }

    protected List<? extends Group> loadWorkgroups(RemoveReplaceDocument document) {
	List<Group> workgroups = new ArrayList<Group>();
	for (WorkgroupTarget workgroupTarget : document.getWorkgroupTargets()) {
		Group group = KIMServiceLocator.getIdentityManagementService().getGroup(workgroupTarget.getWorkgroupId());
	    if (group == null) {
	    	throw new WorkflowRuntimeException("Failed to locate group with id " + workgroupTarget.getWorkgroupId());
	    }
	    workgroups.add(group);
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
	List<RuleBaseValues> rules = KEWServiceLocator.getRuleService().findRuleBaseValuesByResponsibilityReviewerTemplateDoc(form.getRuleRuleTemplate(), form.getRuleDocumentTypeName(), form.getUser().getPrincipalId(), KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID);
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
	    if (KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID.equals(responsibility.getRuleResponsibilityType()) &&
		    responsibility.getRuleResponsibilityName().equals(form.getUser().getPrincipalId())) {
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
	List<? extends Group> groups = KIMServiceLocator.getIdentityManagementService().getGroupsForPrincipal(form.getUser().getPrincipalId());
	form.getWorkgroups().clear();
	form.getWorkgroups().addAll(loadRemoveReplaceWorkgroups(form, groups));
	return mapping.findForward("basic");
    }

    /**
     * Constructs a list of RemoveReplaceWorkgroup objects from the given list of Workgroups.
     */
    protected List<RemoveReplaceWorkgroup> loadRemoveReplaceWorkgroups(RemoveReplaceForm form, List<? extends Group> groups) {
	List<RemoveReplaceWorkgroup> removeReplaceWorkgroups = new ArrayList<RemoveReplaceWorkgroup>();
	Set<String> selectedWorkgroupIds = getSelectedWorkgroupIds(form);
	for (Group group : groups) {
	    RemoveReplaceWorkgroup removeReplaceWorkgroup = new RemoveReplaceWorkgroup();
	    removeReplaceWorkgroup.setId(group.getGroupId());
	    removeReplaceWorkgroup.setName(group.getGroupName());
	    removeReplaceWorkgroup.setType(group.getKimTypeId());
	    removeReplaceWorkgroup.setWarning("");
	    // if workgroup was selected previously, keep it selected
	    removeReplaceWorkgroup.setSelected(selectedWorkgroupIds.contains(group.getGroupId()));
	    boolean isOnlyMember = true;
	    boolean foundMember = false;
	    List<String> directMembers = KIMServiceLocator.getIdentityManagementService().getDirectGroupMemberPrincipalIds(group.getGroupId());
	    for (String principalId : directMembers) {
	    	if (principalId.equals(form.getUser().getPrincipalId())) {
	    		foundMember = true;
	    	} else {
	    		isOnlyMember = false;
	    	}
	    }
	    List<String> warnings = new ArrayList<String>();
	    // TODO: to be replaced during conversion to KNS
	    //try {
		//Long documentId = KEWServiceLocator.getWorkgroupRoutingService().getLockingDocumentId(group.getWorkflowGroupId());
		//if (documentId != null) {
		//    warnings.add("Workgroup is locked by document " + documentId + " and cannot be modified.");
		//    removeReplaceWorkgroup.setDisabled(true);
		//    removeReplaceWorkgroup.setSelected(false);
		//}
	    //} catch (WorkflowException e) {
		//throw new WorkflowRuntimeException(e);
	    //}
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
		LOG.warn("Failed to find a valid member on workgroup " + group.getGroupName() + " for user " + form.getUserId() + ".  This workgroup will not be added to the list for selection.");
	    } else {
		removeReplaceWorkgroups.add(removeReplaceWorkgroup);
	    }
	}
	return removeReplaceWorkgroups;
    }

    private Set<String> getSelectedWorkgroupIds(RemoveReplaceForm form) {
	Set<String> selectedWorkgroupIds = new HashSet<String>();
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
	document.setUserWorkflowId(form.getUser().getPrincipalId());
	if (form.getReplacementUser() != null) {
	    document.setReplacementUserWorkflowId(form.getReplacementUser().getPrincipalId());
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
        form.getWorkflowDocument().cancel("");
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

    private static UserSession getUserSession(HttpServletRequest request) {
        return UserSession.getAuthenticatedUser();
    }



}
