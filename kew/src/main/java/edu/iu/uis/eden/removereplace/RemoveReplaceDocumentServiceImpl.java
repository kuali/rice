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
package edu.iu.uis.eden.removereplace;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.export.ExportFormat;
import edu.iu.uis.eden.removereplace.dao.RemoveReplaceDocumentDAO;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.util.XmlHelper;
import edu.iu.uis.eden.web.session.UserSession;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;
import edu.iu.uis.eden.workgroup.Workgroup;
import edu.iu.uis.eden.workgroup.WorkgroupRoutingService;

public class RemoveReplaceDocumentServiceImpl implements RemoveReplaceDocumentService {

    private RemoveReplaceDocumentDAO dao;

    public void save(RemoveReplaceDocument document) {
	if (document.getDocumentId() == null) {
	    throw new WorkflowRuntimeException("The given document has a null document ID.  Please assign a document ID prior to saving.");
	}
	dao.save(document);
    }

    public RemoveReplaceDocument findById(Long documentId) {
	return dao.findById(documentId);
    }

    public void blanketApprove(RemoveReplaceDocument document, UserSession user, String annotation) {
	save(document);
	try {
	    WorkflowDocument workflowDoc = new WorkflowDocument(new NetworkIdVO(user.getNetworkId()), document.getDocumentId());
	    constructTitle(document, workflowDoc);
	    attachDocumentContent(document, workflowDoc);
	    workflowDoc.blanketApprove(annotation);
	} catch (WorkflowException e) {
	    throw new WorkflowRuntimeException(e);
	}
    }

    public void route(RemoveReplaceDocument document, UserSession user, String annotation) {
	save(document);
	try {
	    WorkflowDocument workflowDoc = new WorkflowDocument(new NetworkIdVO(user.getNetworkId()), document.getDocumentId());
	    constructTitle(document, workflowDoc);
	    attachDocumentContent(document, workflowDoc);
	    workflowDoc.routeDocument(annotation);
	} catch (WorkflowException e) {
	    throw new WorkflowRuntimeException(e);
	}
    }

    protected void constructTitle(RemoveReplaceDocument document, WorkflowDocument workflowDoc) throws WorkflowException {
	WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(document.getUserWorkflowId()));
	StringBuffer title = new StringBuffer();
	if (document.getOperation().equals(RemoveReplaceDocument.REMOVE_OPERATION)) {
	    title.append("Removing " + user.getAuthenticationUserId().getAuthenticationId() + " from ");
	} else if (document.getOperation().equals(RemoveReplaceDocument.REPLACE_OPERATION)) {
	    WorkflowUser replaceWithUser = KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(document.getReplacementUserWorkflowId()));
	    title.append("Replacing " + user.getAuthenticationUserId().getAuthenticationId() + " with " + replaceWithUser.getAuthenticationUserId().getAuthenticationId() + " in ");
	}
	title.append(document.getRuleTargets().size() + " rules and " + document.getWorkgroupTargets().size() + " workgroups");
	workflowDoc.setTitle(title.toString());
    }

    /**
     * Attaches document content to the WorkflowDocument for the given RemoveReplaceDocument.
     */
    protected void attachDocumentContent(RemoveReplaceDocument document, WorkflowDocument workflowDoc) {
	try {
	    WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(document.getUserWorkflowId()));
	    Element rootElement = new Element("removeReplaceUserDocument");
	    Element removeReplaceElement = null;
	    if (document.getOperation().equals(RemoveReplaceDocument.REMOVE_OPERATION)) {
		removeReplaceElement = new Element("remove");
		Element userElement = new Element("user");
		userElement.setText(user.getAuthenticationUserId().getAuthenticationId());
		removeReplaceElement.addContent(userElement);
	    } else if (document.getOperation().equals(RemoveReplaceDocument.REPLACE_OPERATION)) {
		removeReplaceElement = new Element("replace");
		Element userElement = new Element("user");
		userElement.setText(user.getAuthenticationUserId().getAuthenticationId());
		removeReplaceElement.addContent(userElement);
		Element replaceWithElement = new Element("replaceWith");
		WorkflowUser replaceWithUser = KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(document.getReplacementUserWorkflowId()));
		replaceWithElement.setText(replaceWithUser.getAuthenticationUserId().getAuthenticationId());
		removeReplaceElement.addContent(replaceWithElement);
	    } else {
		throw new WorkflowRuntimeException("Invalid remove/replace operation specified: " + document.getOperation());
	    }
	    rootElement.addContent(removeReplaceElement);

	    // add rules
	    List<RuleBaseValues> rules = loadRules(document);
	    if (!rules.isEmpty()) {
		ExportDataSet ruleDataSet = new ExportDataSet(ExportFormat.XML);
		ruleDataSet.getRules().addAll(rules);
		Element rulesElement = KEWServiceLocator.getRuleService().export(ruleDataSet);
		removeReplaceElement.addContent(rulesElement);
	    }

	    // add workgroups
	    List<Workgroup> workgroups = loadWorkgroups(document);
	    if (!workgroups.isEmpty()) {
		ExportDataSet workgroupDataSet = new ExportDataSet(ExportFormat.XML);
		workgroupDataSet.getWorkgroups().addAll(workgroups);
		Element workgroupsElement = KEWServiceLocator.getWorkgroupService().export(workgroupDataSet);
		removeReplaceElement.addContent(workgroupsElement);
	    }
	    workflowDoc.setApplicationContent(XmlHelper.jotNode(rootElement));
	} catch (EdenUserNotFoundException e) {
	    throw new WorkflowRuntimeException(e);
	}
    }

    protected List<Workgroup> loadWorkgroups(RemoveReplaceDocument document) {
	List<Workgroup> workgroups = new ArrayList<Workgroup>();
	for (WorkgroupTarget workgroupTarget : document.getWorkgroupTargets()) {
	    Workgroup workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(workgroupTarget.getWorkgroupId()));
	    if (workgroup == null) {
		throw new WorkflowRuntimeException("Failed to locate workgroup to change with id " + workgroupTarget.getWorkgroupId());
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
		throw new WorkflowRuntimeException("Failed to locate rule to change with id " + ruleTarget.getRuleId());
	    }
	    rules.add(rule);
	}
	return rules;
    }


    public void finalize(Long documentId) {
	RemoveReplaceDocument document = findById(documentId);

	if (document == null) {
	    throw new WorkflowRuntimeException("Failed to locate the RemoveReplaceDocument with id " + documentId);
	}
	if (StringUtils.isEmpty(document.getUserWorkflowId())) {
	    throw new WorkflowRuntimeException("RemoveReplaceDocument does not have a user id.");
	}

	List<Long> ruleIds = new ArrayList<Long>();
	if (document.getRuleTargets() != null) {
	    for (RuleTarget ruleTarget : document.getRuleTargets()) {
		ruleIds.add(ruleTarget.getRuleId());
	    }
	}

	List<Long> workgroupIds = new ArrayList<Long>();
	if (document.getWorkgroupTargets() != null) {
	    for (WorkgroupTarget workgroupTarget : document.getWorkgroupTargets()) {
		workgroupIds.add(workgroupTarget.getWorkgroupId());
	    }
	}

	RuleService ruleService = KEWServiceLocator.getRuleService();
	WorkgroupRoutingService workgroupRoutingService = KEWServiceLocator.getWorkgroupRoutingService();
	try {
	    if (RemoveReplaceDocument.REPLACE_OPERATION.equals(document.getOperation())) {
		if (StringUtils.isEmpty(document.getReplacementUserWorkflowId())) {
		    throw new WorkflowRuntimeException("Replacement operation was indicated but RemoveReplaceDocument does not have a replacement user id.");
		}
		ruleService.replaceRuleInvolvement(new WorkflowUserId(document.getUserWorkflowId()), new WorkflowUserId(document.getReplacementUserWorkflowId()), ruleIds, documentId);
		workgroupRoutingService.replaceWorkgroupInvolvement(new WorkflowUserId(document.getUserWorkflowId()), new WorkflowUserId(document.getReplacementUserWorkflowId()), workgroupIds, documentId);
	    } else if (RemoveReplaceDocument.REMOVE_OPERATION.equals(document.getOperation())) {
		ruleService.removeRuleInvolvement(new WorkflowUserId(document.getUserWorkflowId()), ruleIds, documentId);
		workgroupRoutingService.removeWorkgroupInvolvement(new WorkflowUserId(document.getUserWorkflowId()), workgroupIds, documentId);
	    } else {
		throw new WorkflowRuntimeException("Invalid operation was specified on the RemoveReplaceDocument: " + document.getOperation());
	    }
	} catch (WorkflowException e) {
	    throw new WorkflowRuntimeException(e);
	}
    }

    public void setRemoveReplaceDocumentDAO(RemoveReplaceDocumentDAO dao) {
	this.dao = dao;
    }

}
