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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.jws.WebParam;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionTaken;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentContent;
import org.kuali.rice.kew.api.document.DocumentDetail;
import org.kuali.rice.kew.api.document.DocumentLink;
import org.kuali.rice.kew.api.document.RouteNodeInstance;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.kew.dto.DTOConverter;
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
	public boolean doesDocumentExist(String documentId) {
	    if (StringUtils.isBlank(documentId)) {
            throw new RiceIllegalArgumentException("documentId was blank or null");
        }
	    DocumentRouteHeaderValue documentBo = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
	    return documentBo != null;
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
	public DocumentDetail getDocumentDetail(@WebParam(name = "documentId") String documentId) {
		if (StringUtils.isBlank(documentId)) {
            throw new IllegalArgumentException("documentId was null or blank");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Fetching DocumentDetail [id="+documentId+"]");
        }
        DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
        if (document == null) {
        	return null;
        }
        DocumentDetail documentDetailVO = DTOConverter.convertDocumentDetailNew(document);
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Returning DocumentDetailVO [id=" + documentId + "]");
        }
        return documentDetailVO;
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

			// TODO: KULRICE-5329 verify that the rewrite of the numeric logic below is reasonable -- I'm guessing it's not -- this one's a fairly radical change since I had to throw
			// away the whole premise of using the longValue of the id as a strategy, so I think I'm massively oversimplifying the original goal of the logic
			List<org.kuali.rice.kew.engine.node.RouteNodeInstance> routeNodeInstances = KEWServiceLocator.getRouteNodeService().getFlattenedNodeInstances(document, false);
			Set<String> routeNodeNames = new LinkedHashSet<String>();
			if (routeNodeInstances != null) {
				for (org.kuali.rice.kew.engine.node.RouteNodeInstance routeNodeInstance : routeNodeInstances) {
					if (routeNodeInstance.isComplete()) {
						routeNodeNames.add(routeNodeInstance.getName());
					}
				}
			}
//			List<org.kuali.rice.kew.engine.node.RouteNodeInstance> activeNodeInstances = KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(document);
//			long largetActivatedNodeId = 0;
//			for (org.kuali.rice.kew.engine.node.RouteNodeInstance routeNodeInstance : activeNodeInstances) {
//				if (routeNodeInstance.getRouteNode().getRouteNodeId().longValue() > largetActivatedNodeId) {
//					largetActivatedNodeId = routeNodeInstance.getRouteNode().getRouteNodeId().longValue();
//				}
//			}
//
//			List<org.kuali.rice.kew.engine.node.RouteNodeInstance> routeNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodeInstances(document, false);
//			List<String> nodeNames = new ArrayList<String>();
//
//			for (org.kuali.rice.kew.engine.node.RouteNodeInstance routeNode : routeNodes) {
//				if (routeNode.isComplete() && !nodeNames.contains(routeNode.getName())) {
//					//if the prototype of the nodeInstance we're analyzing is less than the largest id of all our active prototypes
//					//then add it to the list.  This is an attempt to account for return to previous hitting a single node multiple times
//					if (routeNode.getRouteNode().getRouteNodeId().longValue() < largetActivatedNodeId) {
//						nodeNames.add(routeNode.getName());
//					}
//				}
//			}
			return Collections.unmodifiableList(new ArrayList<String>(routeNodeNames));
		} else {
			return Collections.emptyList();
		}
	}
	
	@Override
	public DocumentLink addDocumentLink(DocumentLink documentLink) throws RiceIllegalArgumentException {
		if (documentLink == null) {
			throw new RiceIllegalArgumentException("documentLink was null");
		}
		if (documentLink.getId() != null) {
			throw new RiceIllegalArgumentException("the given documentLink already has an id, cannot add a document link with an existing id");
		}
		org.kuali.rice.kew.documentlink.DocumentLink documentLinkBo = org.kuali.rice.kew.documentlink.DocumentLink.from(documentLink);
		KEWServiceLocator.getDocumentLinkService().saveDocumentLink(documentLinkBo);
		return org.kuali.rice.kew.documentlink.DocumentLink.to(documentLinkBo);
	}

	@Override
	public DocumentLink deleteDocumentLink(String documentLinkId) throws RiceIllegalArgumentException {
		if (StringUtils.isBlank(documentLinkId)) {
			throw new RiceIllegalArgumentException("documentLinkId was null or blank");
		}
		org.kuali.rice.kew.documentlink.DocumentLink documentLinkBo = KEWServiceLocator.getDocumentLinkService().getDocumentLink(Long.valueOf(documentLinkId));
		if (documentLinkBo == null) {
			throw new RiceIllegalArgumentException("Failed to locate document link with the given documentLinkId: " + documentLinkId);
		}
		KEWServiceLocator.getDocumentLinkService().deleteDocumentLink(documentLinkBo);
		return org.kuali.rice.kew.documentlink.DocumentLink.to(documentLinkBo);
	}
	    
	@Override
	public List<DocumentLink> deleteDocumentLinksByDocumentId(String originatingDocumentId) throws RiceIllegalArgumentException {
		if (StringUtils.isBlank(originatingDocumentId)) {
			throw new RiceIllegalArgumentException("originatingDocumentId was null or blank");
		}
		List<org.kuali.rice.kew.documentlink.DocumentLink> documentLinkBos = KEWServiceLocator.getDocumentLinkService().getLinkedDocumentsByDocId(originatingDocumentId);
		if (documentLinkBos == null || documentLinkBos.isEmpty()) {
			return Collections.emptyList();
		}
		List<DocumentLink> deletedDocumentLinks = new ArrayList<DocumentLink>();
		for (org.kuali.rice.kew.documentlink.DocumentLink documentLinkBo : documentLinkBos) {
			deletedDocumentLinks.add(org.kuali.rice.kew.documentlink.DocumentLink.to(documentLinkBo));
			KEWServiceLocator.getDocumentLinkService().deleteDocumentLink(documentLinkBo);
		}
		return Collections.unmodifiableList(deletedDocumentLinks);
    }
	    
	@Override
	public List<DocumentLink> getOutgoingDocumentLinks(String originatingDocumentId) throws RiceIllegalArgumentException {
		if (StringUtils.isBlank(originatingDocumentId)) {
			throw new RiceIllegalArgumentException("originatingDocumentId was null or blank");
		}
		List<org.kuali.rice.kew.documentlink.DocumentLink> outgoingDocumentLinkBos = KEWServiceLocator.getDocumentLinkService().getLinkedDocumentsByDocId(originatingDocumentId);
		List<DocumentLink> outgoingDocumentLinks = new ArrayList<DocumentLink>();
		for (org.kuali.rice.kew.documentlink.DocumentLink outgoingDocumentLinkBo : outgoingDocumentLinkBos) {
			outgoingDocumentLinks.add(org.kuali.rice.kew.documentlink.DocumentLink.to(outgoingDocumentLinkBo));
		}
		return Collections.unmodifiableList(outgoingDocumentLinks);
    }
	
	@Override
	public List<DocumentLink> getIncomingDocumentLinks(String destinationDocumentId) throws RiceIllegalArgumentException {
		if (StringUtils.isBlank(destinationDocumentId)) {
			throw new RiceIllegalArgumentException("destinationDocumentId was null or blank");
		}
		List<org.kuali.rice.kew.documentlink.DocumentLink> incomingDocumentLinkBos = KEWServiceLocator.getDocumentLinkService().getOutgoingLinkedDocumentsByDocId(destinationDocumentId);
		List<DocumentLink> incomingDocumentLinks = new ArrayList<DocumentLink>();
		for (org.kuali.rice.kew.documentlink.DocumentLink incomingDocumentLinkBo : incomingDocumentLinkBos) {
			incomingDocumentLinks.add(org.kuali.rice.kew.documentlink.DocumentLink.to(incomingDocumentLinkBo));
		}
		return Collections.unmodifiableList(incomingDocumentLinks);
    }
	    
	@Override
	public DocumentLink getDocumentLink(String documentLinkId) throws RiceIllegalArgumentException {
		if (StringUtils.isBlank(documentLinkId)) {
			throw new RiceIllegalArgumentException("documentLinkId was null or blank");
		}
		org.kuali.rice.kew.documentlink.DocumentLink documentLinkBo = KEWServiceLocator.getDocumentLinkService().getDocumentLink(Long.valueOf(documentLinkId));
		return org.kuali.rice.kew.documentlink.DocumentLink.to(documentLinkBo);
    }
	
}
