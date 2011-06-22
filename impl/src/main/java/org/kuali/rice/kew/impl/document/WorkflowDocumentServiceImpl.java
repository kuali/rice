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
	
	@Override
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
	public List<RouteNodeInstance> getRouteNodeInstances(String documentId) {
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug("Fetching RouteNodeInstances [documentId=" + documentId + "]");
    	}
    	DocumentRouteHeaderValue documentBo = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
    	if (documentBo == null) {
    		return Collections.emptyList();
    	}
    	return convertRouteNodeInstances(KEWServiceLocator.getRouteNodeService().getFlattenedNodeInstances(documentBo, true));
    }	
	
	@Override
	public List<RouteNodeInstance> getActiveRouteNodeInstances(String documentId) {
		if ( LOG.isDebugEnabled() ) {
    		LOG.debug("Fetching active RouteNodeInstances [documentId=" + documentId + "]");
    	}
        return convertRouteNodeInstances(KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(documentId));
	}

	private List<RouteNodeInstance> convertRouteNodeInstances(List<org.kuali.rice.kew.engine.node.RouteNodeInstance> routeNodeInstanceBos) {
		List<RouteNodeInstance> routeNodeInstances = new ArrayList<RouteNodeInstance>();
        for (org.kuali.rice.kew.engine.node.RouteNodeInstance routeNodeInstanceBo : routeNodeInstanceBos) {
        	routeNodeInstances.add(org.kuali.rice.kew.engine.node.RouteNodeInstance.to(routeNodeInstanceBo));
        }
        return Collections.unmodifiableList(routeNodeInstances);
	}
	
	@Override
	public List<String> getPreviousRouteNodeNames(String documentId) {
		if ( LOG.isDebugEnabled() ) {
			LOG.debug("Fetching previous node names [documentId=" + documentId + "]");
		}
		DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
		
		// TODO validate that the doc is null or not instead of just throwing NPE?
		
		//going conservative for now.  if the doc isn't enroute or exception nothing will be returned.
		if (document.isEnroute() || document.isInException()) {

			List<org.kuali.rice.kew.engine.node.RouteNodeInstance> activeNodeInstances = KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(document);
			long largetActivatedNodeId = 0;
			for (org.kuali.rice.kew.engine.node.RouteNodeInstance routeNodeInstance : activeNodeInstances) {
				if (routeNodeInstance.getRouteNode().getRouteNodeId().longValue() > largetActivatedNodeId) {
					largetActivatedNodeId = routeNodeInstance.getRouteNode().getRouteNodeId().longValue();
				}
			}

			List<org.kuali.rice.kew.engine.node.RouteNodeInstance> routeNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodeInstances(document, false);
			List<String> nodeNames = new ArrayList<String>();

			for (org.kuali.rice.kew.engine.node.RouteNodeInstance routeNode : routeNodes) {
				if (routeNode.isComplete() && !nodeNames.contains(routeNode.getName())) {
					//if the prototype of the nodeInstance we're analyzing is less than the largest id of all our active prototypes
					//then add it to the list.  This is an attempt to account for return to previous hitting a single node multiple times
					if (routeNode.getRouteNode().getRouteNodeId().longValue() < largetActivatedNodeId) {
						nodeNames.add(routeNode.getName());
					}
				}
			}
			return Collections.unmodifiableList(nodeNames);
		} else {
			return Collections.emptyList();
		}
	}
	
}
