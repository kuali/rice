/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.workflow.role;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.workflow.role.dao.RoleDAO;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.messaging.KEWXMLService;
import edu.iu.uis.eden.messaging.MessageServiceNames;
import edu.iu.uis.eden.plugin.attributes.RoleAttribute;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routetemplate.FlexRM;
import edu.iu.uis.eden.routetemplate.RolePoker;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.routetemplate.RuleTemplateAttribute;
import edu.iu.uis.eden.util.Utilities;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoleServiceImpl implements RoleService {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RoleServiceImpl.class);

	private RoleDAO dao;

	public QualifiedRole findQualifiedRoleById(Long qualifiedRoleId) {
		return dao.findQualifiedRoleById(qualifiedRoleId);
	}

	public Role findRoleById(Long roleId) {
		return dao.findRoleById(roleId);
	}

	public Role findRoleByName(String roleName) {
		return dao.findRoleByName(roleName);
	}

	public List<QualifiedRole> findQualifiedRolesForRole(String roleName, Timestamp effectiveDate) {
		Collection qualifiedRoles = dao.findQualifiedRolesForRole(roleName);
		List<QualifiedRole> filteredQualifiedRoles = new ArrayList<QualifiedRole>(qualifiedRoles.size());
		if (effectiveDate != null) {
			for (Iterator iterator = qualifiedRoles.iterator(); iterator.hasNext();) {
				QualifiedRole qualifiedRole = (QualifiedRole) iterator.next();
				boolean withinEffectiveDate = false;
				// TODO this logic is crazy, there's got to be an easier way to do this
				if (qualifiedRole.getActivationDate() == null || qualifiedRole.getActivationDate().compareTo(effectiveDate) <= 0) {
					withinEffectiveDate = true;
				}
				if (qualifiedRole.getDeactivationDate() == null || qualifiedRole.getDeactivationDate().compareTo(effectiveDate) >= 0) {
					withinEffectiveDate = withinEffectiveDate && true;
				} else {
					withinEffectiveDate = false;
				}
				if (withinEffectiveDate) {
					filteredQualifiedRoles.add(qualifiedRole);
				}
			}
		} else {
			filteredQualifiedRoles.addAll(qualifiedRoles);
		}
		return filteredQualifiedRoles;
	}

	public void save(Role role) {
		dao.save(role);
	}

	public void save(QualifiedRole qualifiedRole) {
		dao.save(qualifiedRole);
		updateResponsibilityIds(qualifiedRole);
	}

	protected void updateResponsibilityIds(QualifiedRole qualifiedRole) {
		boolean updated = false;
		for (QualifiedRoleMember member : qualifiedRole.getMembers()) {
			if (member.getResponsibilityId() == null) {
				member.setResponsibilityId(member.getQualifiedRoleMemberId());
				updated = true;
			}
		}
		if (updated) {
			dao.save(qualifiedRole);
		}
	}

    public void reResolveRole(DocumentType documentType, String roleName) throws WorkflowException {
    	String infoString = "documentType="+(documentType == null ? null : documentType.getName())+", role="+roleName;
        if (documentType == null ||
                Utilities.isEmpty(roleName)) {
            throw new IllegalArgumentException("Cannot pass null or empty arguments to reResolveRole: "+infoString);
        }
        LOG.debug("Re-resolving role asynchronously for "+infoString);
    	Set routeHeaderIds = new HashSet();
    	findAffectedDocuments(documentType, roleName, null, routeHeaderIds);
    	LOG.debug(routeHeaderIds.size()+" documents were affected by this re-resolution, requeueing with the RolePokerProcessor");
    	for (Iterator iterator = routeHeaderIds.iterator(); iterator.hasNext();) {
    		Long documentId = (Long) iterator.next();
    		QName rolePokerName = new QName(documentType.getMessageEntity(), MessageServiceNames.ROLE_POKER);
    		RolePoker rolePoker = (RolePoker)KSBServiceLocator.getMessageHelper().getServiceAsynchronously(rolePokerName);
    		rolePoker.reResolveRole(documentId, roleName);

//			String parameters = generateProcessorParameters(roleName, null);
//			SpringServiceLocator.getRouteQueueService().requeueDocument(routeHeaderId, EdenConstants.ROUTE_QUEUE_RERESOLVE_PRIORITY, new Long(0), RolePokerProcessor.class.getName(), parameters);
		}
    }

    public void reResolveQualifiedRole(DocumentType documentType, String roleName, String qualifiedRoleNameLabel) throws WorkflowException {
    	String infoString = "documentType="+(documentType == null ? null : documentType.getName())+", role="+roleName+", qualifiedRole="+qualifiedRoleNameLabel;
        if (documentType == null ||
                Utilities.isEmpty(roleName) ||
                Utilities.isEmpty(qualifiedRoleNameLabel)) {
            throw new IllegalArgumentException("Cannot pass null or empty arguments to reResolveQualifiedRole: "+infoString);
        }
        LOG.debug("Re-resolving qualified role asynchronously for "+infoString);
    	Set routeHeaderIds = new HashSet();
    	findAffectedDocuments(documentType, roleName, qualifiedRoleNameLabel, routeHeaderIds);
    	LOG.debug(routeHeaderIds.size()+" documents were affected by this re-resolution, requeueing with the RolePokerProcessor");
    	for (Iterator iterator = routeHeaderIds.iterator(); iterator.hasNext();) {
    		Long documentId = (Long) iterator.next();

    		QName rolePokerName = new QName(documentType.getMessageEntity(), MessageServiceNames.ROLE_POKER);
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
                Utilities.isEmpty(roleName) ||
                Utilities.isEmpty(qualifiedRoleNameLabel)) {
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
        			List actionRequests = flexRM.getActionRequests(routeHeader, ruleTemplate.getName());
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
                Utilities.isEmpty(roleName)) {
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
        			List actionRequests = flexRM.getActionRequests(routeHeader, ruleTemplate.getName());
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
        if (Utilities.isEmpty(activeNodeInstances)) {
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
                    edu.iu.uis.eden.routetemplate.Role role = (edu.iu.uis.eden.routetemplate.Role) roleIt.next();
                    if (role.getLabel().equals(roleName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void requeueDocument(DocumentRouteHeaderValue document) {
    	QName documentServiceName = new QName(document.getDocumentType().getMessageEntity(), MessageServiceNames.DOCUMENT_ROUTING_SERVICE);
    	KEWXMLService documentRoutingService = (KEWXMLService)MessageServiceNames.getServiceAsynchronously(documentServiceName, document);
    	try {
			documentRoutingService.invoke(String.valueOf(document.getRouteHeaderId()));
		} catch (Exception e) {
			throw new WorkflowRuntimeException(e);
		}
    }

	public void setRoleDAO(RoleDAO dao) {
		this.dao = dao;
	}

}
