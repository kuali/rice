/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.kew.role.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.rice.core.reflect.ObjectDefinition;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.messaging.MessageServiceNames;
import org.kuali.rice.kew.role.service.RoleService;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.rule.FlexRM;
import org.kuali.rice.kew.rule.RoleAttribute;
import org.kuali.rice.kew.rule.RolePoker;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.ksb.messaging.service.KSBXMLService;
import org.kuali.rice.ksb.service.KSBServiceLocator;

import javax.xml.namespace.QName;
import java.util.*;


/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RoleServiceImpl implements RoleService {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RoleServiceImpl.class);

    public void reResolveRole(DocumentType documentType, String roleName) throws WorkflowException {
    	String infoString = "documentType="+(documentType == null ? null : documentType.getName())+", role="+roleName;
        if (documentType == null ||
                org.apache.commons.lang.StringUtils.isEmpty(roleName)) {
            throw new IllegalArgumentException("Cannot pass null or empty arguments to reResolveRole: "+infoString);
        }
        LOG.debug("Re-resolving role asynchronously for "+infoString);
    	Set routeHeaderIds = new HashSet();
    	findAffectedDocuments(documentType, roleName, null, routeHeaderIds);
    	LOG.debug(routeHeaderIds.size()+" documents were affected by this re-resolution, requeueing with the RolePokerProcessor");
    	for (Iterator iterator = routeHeaderIds.iterator(); iterator.hasNext();) {
    		Long documentId = (Long) iterator.next();
    		QName rolePokerName = new QName(documentType.getServiceNamespace(), MessageServiceNames.ROLE_POKER);
    		RolePoker rolePoker = (RolePoker)KSBServiceLocator.getMessageHelper().getServiceAsynchronously(rolePokerName);
    		rolePoker.reResolveRole(documentId, roleName);
		}
    }

    public void reResolveQualifiedRole(DocumentType documentType, String roleName, String qualifiedRoleNameLabel) throws WorkflowException {
    	String infoString = "documentType="+(documentType == null ? null : documentType.getName())+", role="+roleName+", qualifiedRole="+qualifiedRoleNameLabel;
        if (documentType == null ||
                org.apache.commons.lang.StringUtils.isEmpty(roleName) ||
                org.apache.commons.lang.StringUtils.isEmpty(qualifiedRoleNameLabel)) {
            throw new IllegalArgumentException("Cannot pass null or empty arguments to reResolveQualifiedRole: "+infoString);
        }
        LOG.debug("Re-resolving qualified role asynchronously for "+infoString);
    	Set routeHeaderIds = new HashSet();
    	findAffectedDocuments(documentType, roleName, qualifiedRoleNameLabel, routeHeaderIds);
    	LOG.debug(routeHeaderIds.size()+" documents were affected by this re-resolution, requeueing with the RolePokerProcessor");
    	for (Iterator iterator = routeHeaderIds.iterator(); iterator.hasNext();) {
    		Long documentId = (Long) iterator.next();

    		QName rolePokerName = new QName(documentType.getServiceNamespace(), MessageServiceNames.ROLE_POKER);
    		RolePoker rolePoker = (RolePoker)KSBServiceLocator.getMessageHelper().getServiceAsynchronously(rolePokerName);
    		rolePoker.reResolveRole(documentId, roleName, qualifiedRoleNameLabel);
		}
    }

    /**
     *
     * route level and then filters in the approriate ones.
     */
    public void reResolveQualifiedRole(DocumentRouteHeaderValue routeHeader, String roleName, String qualifiedRoleNameLabel) throws WorkflowException {
        String infoString = "routeHeader="+(routeHeader == null ? null : routeHeader.getRouteHeaderId())+", role="+roleName+", qualifiedRole="+qualifiedRoleNameLabel;
        if (routeHeader == null ||
                org.apache.commons.lang.StringUtils.isEmpty(roleName) ||
                org.apache.commons.lang.StringUtils.isEmpty(qualifiedRoleNameLabel)) {
            throw new IllegalArgumentException("Cannot pass null arguments to reResolveQualifiedRole: "+infoString);
        }
        LOG.debug("Re-resolving qualified role synchronously for "+infoString);
        List nodeInstances = findNodeInstances(routeHeader, roleName);
        int requestsGenerated = 0;
        if (!nodeInstances.isEmpty()) {
            deletePendingRoleRequests(routeHeader.getRouteHeaderId(), roleName, qualifiedRoleNameLabel);
            for (Iterator nodeIt = nodeInstances.iterator(); nodeIt.hasNext();) {
                RouteNodeInstance nodeInstance = (RouteNodeInstance)nodeIt.next();
                RuleTemplate ruleTemplate = nodeInstance.getRouteNode().getRuleTemplate();
                FlexRM flexRM = new FlexRM();
        		RouteContext context = RouteContext.getCurrentRouteContext();
        		context.setDocument(routeHeader);
        		context.setNodeInstance(nodeInstance);
        		try {
        			List actionRequests = flexRM.getActionRequests(routeHeader, nodeInstance, ruleTemplate.getName());
        			for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
        				ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
        				if (roleName.equals(actionRequest.getRoleName()) && qualifiedRoleNameLabel.equals(actionRequest.getQualifiedRoleNameLabel())) {
        					actionRequest = KEWServiceLocator.getActionRequestService().initializeActionRequestGraph(actionRequest, routeHeader, nodeInstance);
        					KEWServiceLocator.getActionRequestService().saveActionRequest(actionRequest);
        					requestsGenerated++;
        				}
        			}
        		} catch (Exception e) {
        			RouteContext.clearCurrentRouteContext();
        		}

            }
        }
        LOG.debug("Generated "+requestsGenerated+" action requests after re-resolve: "+infoString);
        requeueDocument(routeHeader);
    }

    public void reResolveRole(DocumentRouteHeaderValue routeHeader, String roleName) throws WorkflowException {
    	String infoString = "routeHeader="+(routeHeader == null ? null : routeHeader.getRouteHeaderId())+", role="+roleName;
        if (routeHeader == null ||
                org.apache.commons.lang.StringUtils.isEmpty(roleName)) {
            throw new IllegalArgumentException("Cannot pass null arguments to reResolveRole: "+infoString);
        }
        LOG.debug("Re-resolving role synchronously for "+infoString);
        List nodeInstances = findNodeInstances(routeHeader, roleName);
        int requestsGenerated = 0;
        if (!nodeInstances.isEmpty()) {
            deletePendingRoleRequests(routeHeader.getRouteHeaderId(), roleName, null);
            for (Iterator nodeIt = nodeInstances.iterator(); nodeIt.hasNext();) {
                RouteNodeInstance nodeInstance = (RouteNodeInstance)nodeIt.next();
                RuleTemplate ruleTemplate = nodeInstance.getRouteNode().getRuleTemplate();
                FlexRM flexRM = new FlexRM();
        		RouteContext context = RouteContext.getCurrentRouteContext();
        		context.setDocument(routeHeader);
        		context.setNodeInstance(nodeInstance);
        		try {
        			List actionRequests = flexRM.getActionRequests(routeHeader, nodeInstance, ruleTemplate.getName());
        			for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
        				ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
        				if (roleName.equals(actionRequest.getRoleName())) {
        					actionRequest = KEWServiceLocator.getActionRequestService().initializeActionRequestGraph(actionRequest, routeHeader, nodeInstance);
        					KEWServiceLocator.getActionRequestService().saveActionRequest(actionRequest);
        					requestsGenerated++;
        				}
        			}
        		} finally {
        			RouteContext.clearCurrentRouteContext();
        		}
            }
        }
        LOG.debug("Generated "+requestsGenerated+" action requests after re-resolve: "+infoString);
        requeueDocument(routeHeader);
    }

    // search the document type and all its children
    private void findAffectedDocuments(DocumentType documentType, String roleName, String qualifiedRoleNameLabel, Set routeHeaderIds) {
    	List pendingRequests = KEWServiceLocator.getActionRequestService().findPendingRootRequestsByDocumentType(documentType.getDocumentTypeId());
    	for (Iterator iterator = pendingRequests.iterator(); iterator.hasNext();) {
			ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
			if (roleName.equals(actionRequest.getRoleName()) &&
					(qualifiedRoleNameLabel == null || qualifiedRoleNameLabel.equals(actionRequest.getQualifiedRoleNameLabel()))) {
				routeHeaderIds.add(actionRequest.getRouteHeaderId());
			}
		}
    	for (Iterator iterator = documentType.getChildrenDocTypes().iterator(); iterator.hasNext();) {
			DocumentType childDocumentType = (DocumentType) iterator.next();
			findAffectedDocuments(childDocumentType, roleName, qualifiedRoleNameLabel, routeHeaderIds);
		}
    }

    private void deletePendingRoleRequests(Long routeHeaderId, String roleName, String qualifiedRoleNameLabel) {
        List pendingRequests = KEWServiceLocator.getActionRequestService().findPendingByDoc(routeHeaderId);
        pendingRequests = KEWServiceLocator.getActionRequestService().getRootRequests(pendingRequests);
        List requestsToDelete = new ArrayList();
        for (Iterator iterator = pendingRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
            if (roleName.equals(actionRequest.getRoleName()) &&
            		(qualifiedRoleNameLabel == null || qualifiedRoleNameLabel.equals(actionRequest.getQualifiedRoleNameLabel()))) {
                requestsToDelete.add(actionRequest);
            }
        }
        LOG.debug("Deleting "+requestsToDelete.size()+" action requests for roleName="+roleName+", qualifiedRoleNameLabel="+qualifiedRoleNameLabel);
        for (Iterator iterator = requestsToDelete.iterator(); iterator.hasNext();) {
            KEWServiceLocator.getActionRequestService().deleteActionRequestGraph((ActionRequestValue)iterator.next());
        }
    }

    private List findNodeInstances(DocumentRouteHeaderValue routeHeader, String roleName) throws WorkflowException {
        List nodeInstances = new ArrayList();
        Collection activeNodeInstances = KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(routeHeader.getRouteHeaderId());
        if (CollectionUtils.isEmpty(activeNodeInstances)) {
            throw new WorkflowException("Document does not currently have any active nodes so re-resolving is not legal.");
        }
        for (Iterator iterator = activeNodeInstances.iterator(); iterator.hasNext();) {
            RouteNodeInstance activeNodeInstance = (RouteNodeInstance) iterator.next();
            RuleTemplate template = activeNodeInstance.getRouteNode().getRuleTemplate();
            if (templateHasRole(template, roleName)) {
                nodeInstances.add(activeNodeInstance);
            }
        }
        if (nodeInstances.isEmpty()) {
            throw new WorkflowException("Could not locate given role to re-resolve: " + roleName);
        }
        return nodeInstances;
    }

    private boolean templateHasRole(RuleTemplate template, String roleName) throws WorkflowException {
        List templateAttributes = template.getRuleTemplateAttributes();
        for (Iterator iterator = templateAttributes.iterator(); iterator.hasNext();) {
            RuleTemplateAttribute templateAttribute = (RuleTemplateAttribute) iterator.next();
            RuleAttribute ruleAttribute = templateAttribute.getRuleAttribute();
            Object workflowAttribute = GlobalResourceLoader.getResourceLoader().getObject(new ObjectDefinition(ruleAttribute.getClassName()));//SpringServiceLocator.getExtensionService().getWorkflowAttribute(ruleAttribute.getClassName());
            if (workflowAttribute instanceof RoleAttribute) {
                List roleNames = ((RoleAttribute)workflowAttribute).getRoleNames();
                for (Iterator roleIt = roleNames.iterator(); roleIt.hasNext();) {
                    org.kuali.rice.kew.rule.Role role = (org.kuali.rice.kew.rule.Role) roleIt.next();
                    if (role.getLabel().equals(roleName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void requeueDocument(DocumentRouteHeaderValue document) {
    	QName documentServiceName = new QName(document.getDocumentType().getServiceNamespace(), MessageServiceNames.DOCUMENT_ROUTING_SERVICE);
    	KSBXMLService documentRoutingService = (KSBXMLService)MessageServiceNames.getServiceAsynchronously(documentServiceName, document);
    	try {
			documentRoutingService.invoke(String.valueOf(document.getRouteHeaderId()));
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
    }

}
