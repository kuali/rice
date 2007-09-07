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

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.removereplace.dao.RemoveReplaceDocumentDAO;
import edu.iu.uis.eden.routetemplate.RuleService;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.web.session.UserSession;
import edu.iu.uis.eden.workgroup.WorkgroupService;

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
	    WorkflowDocument flexDoc = new WorkflowDocument(new NetworkIdVO(user.getNetworkId()), document.getDocumentId());
	    flexDoc.blanketApprove(annotation);
	} catch (WorkflowException e) {
	    throw new WorkflowRuntimeException(e);
	}
    }

    public void route(RemoveReplaceDocument document, UserSession user, String annotation) {
	save(document);
	try {
	    WorkflowDocument flexDoc = new WorkflowDocument(new NetworkIdVO(user.getNetworkId()), document.getDocumentId());
	    flexDoc.routeDocument(annotation);
	} catch (WorkflowException e) {
	    throw new WorkflowRuntimeException(e);
	}
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
	WorkgroupService workgroupService = KEWServiceLocator.getWorkgroupService();
	if (RemoveReplaceDocument.REPLACE_OPERATION.equals(document.getOperation())) {
	    if (StringUtils.isEmpty(document.getReplacementUserWorkflowId())) {
		throw new WorkflowRuntimeException("Replacement operation was indicated but RemoveReplaceDocument does not have a replacement user id.");
	    }
	    ruleService.replaceRuleInvolvement(new WorkflowUserId(document.getUserWorkflowId()), new WorkflowUserId(document.getReplacementUserWorkflowId()), ruleIds, documentId);
	    workgroupService.replaceWorkgroupInvolvement(new WorkflowUserId(document.getUserWorkflowId()), new WorkflowUserId(document.getReplacementUserWorkflowId()), workgroupIds, documentId);
	} else if (RemoveReplaceDocument.REMOVE_OPERATION.equals(document.getOperation())) {
	    ruleService.removeRuleInvolvement(new WorkflowUserId(document.getUserWorkflowId()), ruleIds, documentId);
	    workgroupService.removeWorkgroupInvolvement(new WorkflowUserId(document.getUserWorkflowId()), workgroupIds, documentId);
	} else {
	    throw new WorkflowRuntimeException("Invalid operation was specified on the RemoveReplaceDocument: " + document.getOperation());
	}
    }

    public void setRemoveReplaceDocumentDAO(RemoveReplaceDocumentDAO dao) {
	this.dao = dao;
    }

}
