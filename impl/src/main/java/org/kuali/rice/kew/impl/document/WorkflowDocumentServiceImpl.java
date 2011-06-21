/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kew.impl.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionTaken;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContent;
import org.kuali.rice.kew.api.document.RouteNodeInstance;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValueContent;
import org.kuali.rice.kew.service.KEWServiceLocator;

/**
 * TODO
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class WorkflowDocumentServiceImpl implements WorkflowDocumentService {

	private static final Logger LOG = Logger.getLogger(WorkflowDocumentServiceImpl.class);
	
	@Override
	public Document getDocument(String documentId) {
		if (StringUtils.isBlank(documentId)) {
			throw new RiceIllegalArgumentException("documentId was blank or null");
		}
		DocumentRouteHeaderValue documentBo = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
		return DocumentRouteHeaderValue.to(documentBo);
	}

	@Override
	public DocumentContent getDocumentContent(String documentId) {
		if (StringUtils.isBlank(documentId)) {
			throw new RiceIllegalArgumentException("documentId was blank or null");
		}
		DocumentRouteHeaderValueContent content = KEWServiceLocator.getRouteHeaderService().getContent(documentId);
		return DocumentRouteHeaderValueContent.to(content);
	}

	@Override
	public List<ActionRequest> getRootActionRequests(String documentId) {
		List<ActionRequest> actionRequests = new ArrayList<ActionRequest>();
		List<ActionRequestValue> actionRequestBos = KEWServiceLocator.getActionRequestService().findAllRootActionRequestsByDocumentId(documentId);
		for (ActionRequestValue actionRequestBo : actionRequestBos) {
			actionRequests.add(ActionRequestValue.to(actionRequestBo));
		}
		return Collections.unmodifiableList(actionRequests);
	}
	
	public List<ActionRequest> getActionRequests(String documentId, String nodeName, String principalId) {
        if (StringUtils.isBlank(documentId)) {
        	throw new RiceIllegalArgumentException("documentId was null or blank");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Fetching ActionRequests [docId="+documentId+", nodeName="+nodeName+", principalId="+principalId+"]");
        }
        List<ActionRequestValue> actionRequestBos = KEWServiceLocator.getActionRequestService().findAllActionRequestsByDocumentId(documentId);
        List<ActionRequestValue> matchingActionRequests = new ArrayList<ActionRequestValue>();
        for (ActionRequestValue actionRequestValue : actionRequestBos) {
            if (actionRequestMatches(actionRequestValue, nodeName, principalId)) {
                matchingActionRequests.add(actionRequestValue);
            }
        }
        List<ActionRequest> actionRequests = new ArrayList<ActionRequest>(matchingActionRequests.size());
        for (ActionRequestValue matchingActionRequest : matchingActionRequests) {
        	actionRequests.add(ActionRequestValue.to(matchingActionRequest));
        }
        return actionRequests;
    }
	

    protected boolean actionRequestMatches(ActionRequestValue actionRequest, String nodeName, String principalId) {
        boolean matchesUserId = true;  // assume a match in case user is empty
        boolean matchesNodeName = true;  // assume a match in case node name is empty
        if (StringUtils.isNotBlank(nodeName)) {
            matchesNodeName = nodeName.equals(actionRequest.getPotentialNodeName());
        }
        if (principalId != null) {
            matchesUserId = actionRequest.isRecipientRoutedRequest(principalId);
        }
        return matchesNodeName && matchesUserId;
    }


	@Override
	public List<ActionTaken> getActionsTaken(String documentId) {
		List<ActionTaken> actionTakens = new ArrayList<ActionTaken>();
		Collection<ActionTakenValue> actionTakenBos = KEWServiceLocator.getActionTakenService().findByDocumentId(documentId);
		for (ActionTakenValue actionTakenBo : actionTakenBos) {
			actionTakens.add(ActionTakenValue.to(actionTakenBo));
		}
		return actionTakens;
	}
	
	@Override
	public List<RouteNodeInstance> getActiveNodeInstances(String documentId) {
		if ( LOG.isDebugEnabled() ) {
    		LOG.debug("Fetching active RouteNodeInstancess [documentId=" + documentId + "]");
    	}
		DocumentRouteHeaderValue documentRouteHeaderValue = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
		if (documentRouteHeaderValue == null) {
			throw new IllegalArgumentException("Failed to locate a document for the given documentId: " + documentId);
		}
		List<RouteNodeInstance> activeNodeInstances = new ArrayList<RouteNodeInstance>();
        List<org.kuali.rice.kew.engine.node.RouteNodeInstance> routeNodeInstanceBos = KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(documentId);
        for (org.kuali.rice.kew.engine.node.RouteNodeInstance routeNodeInstanceBo : routeNodeInstanceBos) {
        	activeNodeInstances.add(org.kuali.rice.kew.engine.node.RouteNodeInstance.to(routeNodeInstanceBo));
        }
        return activeNodeInstances;
	}

}
