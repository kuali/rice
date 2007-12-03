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
package edu.iu.uis.eden.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.ActionTakenVO;
import edu.iu.uis.eden.clientapp.vo.DocumentContentVO;
import edu.iu.uis.eden.clientapp.vo.DocumentDetailVO;
import edu.iu.uis.eden.clientapp.vo.DocumentTypeVO;
import edu.iu.uis.eden.clientapp.vo.PropertyDefinitionVO;
import edu.iu.uis.eden.clientapp.vo.ReportCriteriaVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.clientapp.vo.RouteNodeInstanceVO;
import edu.iu.uis.eden.clientapp.vo.RouteTemplateEntryVO;
import edu.iu.uis.eden.clientapp.vo.RuleExtensionVO;
import edu.iu.uis.eden.clientapp.vo.RuleReportCriteriaVO;
import edu.iu.uis.eden.clientapp.vo.RuleVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.UserVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeValidationErrorVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupVO;
import edu.iu.uis.eden.definition.AttributeDefinition;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.engine.ActivationContext;
import edu.iu.uis.eden.engine.CompatUtils;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.node.KeyValuePair;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.engine.simulation.SimulationCriteria;
import edu.iu.uis.eden.engine.simulation.SimulationEngine;
import edu.iu.uis.eden.engine.simulation.SimulationResults;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttributeXmlValidator;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routetemplate.FlexRM;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.WorkflowAttributeValidationError;
import edu.iu.uis.eden.routetemplate.xmlrouting.GenericXMLRuleAttribute;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.workgroup.Workgroup;

@SuppressWarnings("deprecation")
public class WorkflowUtilityWebServiceImpl implements WorkflowUtility {

    private static final Logger LOG = Logger.getLogger(WorkflowUtilityWebServiceImpl.class);

    public RouteHeaderVO getRouteHeaderWithUser(UserIdVO userId, Long documentId) throws WorkflowException {
        if (documentId == null) {
            LOG.error("null routeHeaderId passed in.  Throwing RuntimeExcpetion");
            throw new RuntimeException("Null documentId passed in.");
        }
        if (userId == null) {
            LOG.error("null userId passed in.");
            throw new RuntimeException("null userId passed in");
        }
        LOG.debug("Fetching RouteHeaderVO [id="+documentId+", user="+userId+"]");
        DocumentRouteHeaderValue document = loadDocument(documentId);
        WorkflowUser user = null;
        if (userId != null) {
            user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        }
        RouteHeaderVO routeHeaderVO = BeanConverter.convertRouteHeader(document, user);
        if (routeHeaderVO == null) {
        	LOG.error("Returning null RouteHeaderVO [id=" + documentId + ", user=" + userId + "]");
        }
        LOG.debug("Returning RouteHeaderVO [id=" + documentId + ", user=" + userId + "]");
        return routeHeaderVO;
    }

    public RouteHeaderVO getRouteHeader(Long documentId) throws WorkflowException {
        if (documentId == null) {
            LOG.error("null routeHeaderId passed in.");
            throw new RuntimeException("null routeHeaderId passed in");
        }
        LOG.debug("Fetching RouteHeaderVO [id="+documentId+"]");
        DocumentRouteHeaderValue document = loadDocument(documentId);
        RouteHeaderVO routeHeaderVO = BeanConverter.convertRouteHeader(document, null);
        if (routeHeaderVO == null) {
        	LOG.error("Returning null RouteHeaderVO [id=" + documentId + "]");
        }
        LOG.debug("Returning RouteHeaderVO [id=" + documentId + "]");
        return routeHeaderVO;
    }

    public String getDocumentStatus(Long documentId) throws WorkflowException {
	if (documentId == null) {
	    LOG.error("null documentId passed in.");
            throw new IllegalArgumentException("null documentId passed in");
	}
	String documentStatus = KEWServiceLocator.getRouteHeaderService().getDocumentStatus(documentId);
	if (StringUtils.isEmpty(documentStatus)) {
	    throw new WorkflowException("Could not locate a document with the ID " + documentId);
	}
	return documentStatus;
    }

    public DocumentDetailVO getDocumentDetail(Long documentId) throws WorkflowException {
        if (documentId == null) {
            LOG.error("null documentId passed in.");
            throw new RuntimeException("null documentId passed in");
        }
        LOG.debug("Fetching DocumentDetailVO [id="+documentId+"]");
        DocumentRouteHeaderValue document = loadDocument(documentId);
        DocumentDetailVO documentDetailVO = BeanConverter.convertDocumentDetail(document);
        if (documentDetailVO == null) {
        	LOG.error("Returning null DocumentDetailVO [id=" + documentId + "]");
        }
        LOG.debug("Returning DocumentDetailVO [id=" + documentId + "]");
        return documentDetailVO;
    }

    public RouteNodeInstanceVO getNodeInstance(Long nodeInstanceId) throws WorkflowException {
        if (nodeInstanceId == null) {
            LOG.error("null nodeInstanceId passed in.");
            throw new RuntimeException("null nodeInstanceId passed in");
        }
        LOG.debug("Fetching RouteNodeInstanceVO [id="+nodeInstanceId+"]");
        RouteNodeInstance nodeInstance = KEWServiceLocator.getRouteNodeService().findRouteNodeInstanceById(nodeInstanceId);
        return BeanConverter.convertRouteNodeInstance(nodeInstance);
    }

    public WorkgroupVO getWorkgroup(WorkgroupIdVO workgroupId) throws WorkflowException {
        if (workgroupId == null) {
            LOG.error("null workgroupId passed in.");
            throw new RuntimeException("null workgroupId passed in.");
        }
        LOG.debug("Fetching WorkgroupVO [id="+workgroupId+"]");
        Workgroup workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(workgroupId);
        WorkgroupVO workgroupVO = BeanConverter.convertWorkgroup(workgroup);
        if (workgroupVO == null) {
        	LOG.error("Returning null WorkgroupVO [id=" + workgroupId + "]");
        } else {
        	LOG.debug("Returning WorkgroupVO [id=" + workgroupId + ", memberCount="+workgroupVO.getMembers().length+"]");
        }
        return workgroupVO;
    }

    public UserVO getWorkflowUser(UserIdVO userId) throws WorkflowException {
        if (userId == null) {
            LOG.error("null userId passed in.");
            throw new RuntimeException("null userId passed in.");
        }
        LOG.debug("Fetching UserVO [id="+userId+"]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        UserVO userVO = BeanConverter.convertUser(user);
        if (userVO == null) {
        	LOG.error("Returning null UserVO [id=" + userId + "]");
        }
        return userVO;
    }

    public RouteTemplateEntryVO[] getDocRoute(String docName) throws WorkflowException {
        if (docName == null) {
            LOG.error("null docName passed in.");
            throw new RuntimeException("null docName passed in.");
        }
        LOG.debug("Fetching RouteTemplateEntryVOs [docName="+docName+"]");
        return KEWServiceLocator.getDocumentTypeService().getDocumentTypeVO(docName).getRouteTemplates();
    }

    public DocumentTypeVO getDocumentType(Long documentTypeId) throws WorkflowException {
        if (documentTypeId == null) {
            LOG.error("null documentTypeId passed in.");
            throw new RuntimeException("null documentTypeId passed in.");
        }
        LOG.debug("Fetching DocumentTypeVO [documentTypeId="+documentTypeId+"]");
        return KEWServiceLocator.getDocumentTypeService().getDocumentTypeVO(documentTypeId);
    }

    public DocumentTypeVO getDocumentTypeByName(String documentTypeName) throws WorkflowException {
        if (documentTypeName == null) {
            LOG.error("null documentTypeName passed in.");
            throw new RuntimeException("null documentTypeName passed in");
        }
        LOG.debug("Fetching DocumentTypeVO [documentTypeName="+documentTypeName+"]");
        DocumentTypeVO documentType = KEWServiceLocator.getDocumentTypeService().getDocumentTypeVO(documentTypeName);
        return documentType;
    }

    public Long getNewResponsibilityId() {
    	LOG.debug("Getting new responsibility id.");
        Long rid = KEWServiceLocator.getResponsibilityIdService().getNewResponsibilityId();
        LOG.debug("returning responsibility Id " + rid);
        return rid;
    }

    public WorkgroupVO[] getUserWorkgroups(UserIdVO userId) throws WorkflowException {

        if (userId == null ){
            LOG.error("null userId passed in.");
            throw new RuntimeException("null userId passed in.");
        }
        LOG.debug("Fetching user's workgroups [userId="+userId+"]");
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        List workgroups = KEWServiceLocator.getWorkgroupService().getUsersGroups(user);

        WorkgroupVO[] workgroupVOs = new WorkgroupVO[workgroups.size()];
        int i = 0;
        for (Iterator iter = workgroups.iterator(); iter.hasNext(); i++) {
            Workgroup workgroup = (Workgroup) iter.next();
            workgroupVOs[i] = BeanConverter.convertWorkgroup(workgroup);
        }
        return workgroupVOs;
    }

    public ActionRequestVO[] getActionRequests(Long routeHeaderId) throws WorkflowException {
        return getActionRequests(routeHeaderId, null, null);
    }

    public ActionRequestVO[] getActionRequests(Long routeHeaderId, String nodeName, UserIdVO userId) throws WorkflowException {
        if (routeHeaderId == null) {
            LOG.error("null routeHeaderId passed in.");
            throw new RuntimeException("null routeHeaderId passed in.");
        }
        LOG.debug("Fetching ActionRequestVOs [docId="+routeHeaderId+"]");
        List actionRequests = KEWServiceLocator.getActionRequestService().findAllActionRequestsByRouteHeaderId(routeHeaderId);
        List matchingActionRequests = new ArrayList();
        for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue actionRequestValue = (ActionRequestValue) iterator.next();
            if (actionRequestMatches(actionRequestValue, nodeName, userId)) {
                matchingActionRequests.add(actionRequestValue);
            }
        }
        ActionRequestVO[] actionRequestVOs = new ActionRequestVO[matchingActionRequests.size()];
        int i = 0;
        for (Iterator iter = matchingActionRequests.iterator(); iter.hasNext(); i++) {
            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
            actionRequestVOs[i] = BeanConverter.convertActionRequest(actionRequest);
        }
        return actionRequestVOs;
    }

    private boolean actionRequestMatches(ActionRequestValue actionRequest, String nodeName, UserIdVO userId) throws WorkflowException {
        boolean matchesUserId = true;  // assume a match in case user is empty
        boolean matchesNodeName = true;  // assume a match in case node name is empty
        if (StringUtils.isNotBlank(nodeName)) {
            matchesNodeName = nodeName.equals(actionRequest.getPotentialNodeName());
        }
        if (userId != null) {
            matchesUserId = actionRequest.isRecipientRoutedRequest(KEWServiceLocator.getUserService().getWorkflowUser(userId));
        }
        return matchesNodeName && matchesUserId;
    }

    public ActionTakenVO[] getActionsTaken(Long routeHeaderId) throws WorkflowException {
        if (routeHeaderId == null) {
            LOG.error("null routeHeaderId passed in.");
            throw new RuntimeException("null routeHeaderId passed in.");
        }
        LOG.debug("Fetching ActionTakenVOs [docId="+routeHeaderId+"]");
        Collection actionsTaken = KEWServiceLocator.getActionTakenService().findByRouteHeaderId(routeHeaderId);
        ActionTakenVO[] actionTakenVOs = new ActionTakenVO[actionsTaken.size()];
        int i = 0;
        for (Iterator iter = actionsTaken.iterator(); iter.hasNext(); i++) {
            ActionTakenValue actionTaken = (ActionTakenValue) iter.next();
            actionTakenVOs[i] = BeanConverter.convertActionTaken(actionTaken);
        }
        return actionTakenVOs;
    }

    /**
     * This work is also being done in the bowels of convertDocumentContentVO in BeanConverter so some code
     * could be reduced.
     *
     * @param definition
     * @return WorkflowAttributeValidationErrorVO[] errors from client input into attribute
     */
    public WorkflowAttributeValidationErrorVO[] validateWorkflowAttributeDefinitionVO(WorkflowAttributeDefinitionVO definition) throws WorkflowException {
        if (definition == null) {
            LOG.error("null definition passed in.");
            throw new RuntimeException("null definition passed in.");
        }
        LOG.debug("Validating WorkflowAttributeDefinitionVO [attributeName="+definition.getAttributeName()+"]");
        AttributeDefinition attributeDefinition = BeanConverter.convertWorkflowAttributeDefinitionVO(definition, null);
        WorkflowAttribute attribute = null;
        if (attributeDefinition != null) {
        	attribute = (WorkflowAttribute) GlobalResourceLoader.getObject(attributeDefinition.getObjectDefinition());
        }
        if (attribute instanceof GenericXMLRuleAttribute) {
            Map<String, String> attributePropMap = new HashMap<String, String>();
            GenericXMLRuleAttribute xmlAttribute = (GenericXMLRuleAttribute)attribute;
            xmlAttribute.setRuleAttribute(attributeDefinition.getRuleAttribute());
            for (int i = 0; i < definition.getProperties().length; i++) {
		PropertyDefinitionVO property = definition.getProperties()[i];
		attributePropMap.put(property.getName(), property.getValue());
	    }
            xmlAttribute.setParamMap(attributePropMap);
	}
        //validate inputs from client application if the attribute is capable
        if (attribute instanceof WorkflowAttributeXmlValidator) {
            List errors = ((WorkflowAttributeXmlValidator)attribute).validateClientRoutingData();
            WorkflowAttributeValidationErrorVO[] errorVOs = new WorkflowAttributeValidationErrorVO[errors.size()];
            for (int i = 0; i < errorVOs.length; i++) {
                errorVOs[i] = BeanConverter.convertWorkflowAttributeValidationError((WorkflowAttributeValidationError)errors.get(i));
            }
            return errorVOs;
        } else {
            // WORKAROUND: if it is not validatable, then just quietly succeed
            return new WorkflowAttributeValidationErrorVO[0];
        }
    }

    public RouteNodeInstanceVO[] getDocumentRouteNodeInstances(Long documentId) throws WorkflowException {
    	LOG.debug("Fetching RouteNodeInstanceVOs [docId=" + documentId + "]");
    	return convertRouteNodeInstances(KEWServiceLocator.getRouteNodeService().getFlattenedNodeInstances(loadDocument(documentId), true));
    }

    public RouteNodeInstanceVO[] getActiveNodeInstances(Long documentId) throws WorkflowException {
    	LOG.debug("Fetching active RouteNodeInstanceVOs [docId=" + documentId + "]");
        loadDocument(documentId);
        return convertRouteNodeInstances(KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(documentId));
    }

    public RouteNodeInstanceVO[] getTerminalNodeInstances(Long documentId) throws WorkflowException {
    	LOG.debug("Fetching terminal RouteNodeInstanceVOs [docId=" + documentId + "]");
    	loadDocument(documentId);
        return convertRouteNodeInstances(KEWServiceLocator.getRouteNodeService().getTerminalNodeInstances(documentId));
    }

    private RouteNodeInstanceVO[] convertRouteNodeInstances(List nodeInstances) throws WorkflowException {
        RouteNodeInstanceVO[] nodeInstanceVOs = new RouteNodeInstanceVO[nodeInstances.size()];
        int i = 0;
        for (Iterator iter = nodeInstances.iterator(); iter.hasNext(); ) {
            nodeInstanceVOs[i++] = BeanConverter.convertRouteNodeInstance((RouteNodeInstance) iter.next());
        }
        return nodeInstanceVOs;
    }

    public boolean isUserInRouteLog(Long routeHeaderId, UserIdVO userId, boolean lookFuture) throws WorkflowException {
        if (routeHeaderId == null) {
            LOG.error("null routeHeaderId passed in.");
            throw new RuntimeException("null routeHeaderId passed in.");
        }
        if (userId == null ){
            LOG.error("null userId passed in.");
            throw new RuntimeException("null userId passed in.");
        }
        boolean authorized = false;
        try {
        	LOG.debug("Evaluating isUserInRouteLog [docId=" + routeHeaderId + ", userId=" + userId + ", lookFuture=" + lookFuture + "]");
            DocumentRouteHeaderValue routeHeader = loadDocument(routeHeaderId);
            WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
            List actionsTaken = KEWServiceLocator.getActionTakenService().findByRouteHeaderIdWorkflowId(routeHeaderId, user.getWorkflowUserId().getWorkflowId());

            if(routeHeader.getInitiatorWorkflowId().equals(user.getWorkflowId())){
                return true;
            }

            if (actionsTaken.size() > 0) {
                LOG.debug("found action taken by user");
                authorized = true;
            }

            List actionRequests = KEWServiceLocator.getActionRequestService().findAllActionRequestsByRouteHeaderId(routeHeaderId);
            if (actionRequestListHasUser(user, actionRequests)) {
                authorized = true;
            }

            //using app constant to turn the future look off if need be
            //TODO remove this app constant it has out lived it's usefulness
            lookFuture = lookFuture && new Boolean(Utilities.getApplicationConstant(EdenConstants.CHECK_ROUTE_LOG_AUTH_FUTURE)).booleanValue();
            if (!lookFuture) {
                return authorized;
            }


            SimulationEngine simulationEngine = new SimulationEngine();
            SimulationCriteria criteria = new SimulationCriteria(routeHeaderId);
            criteria.setDestinationNodeName(null); // process entire document to conclusion
            criteria.getDestinationRecipients().add(user);
            SimulationResults results = simulationEngine.runSimulation(criteria);
            if (actionRequestListHasUser(user, results.getSimulatedActionRequests())) {
                authorized = true;
            }
        } catch (Exception ex) {
            LOG.warn("Problems evaluating isUserInRouteLog: " + ex.getMessage(),ex);
        }
        return authorized;
    }

    private boolean actionRequestListHasUser(WorkflowUser user, List actionRequests) throws WorkflowException {
        for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
            if (actionRequest.isRecipientRoutedRequest(user)) {
                return true;
            }
        }
        return false;
    }

    public boolean documentWillHaveAtLeastOneActionRequest(ReportCriteriaVO reportCriteriaVO, String[] actionRequestedCodes) throws RemoteException {
    	try {
	        SimulationEngine simulationEngine = new SimulationEngine();
	        SimulationCriteria criteria = BeanConverter.convertReportCriteriaVO(reportCriteriaVO);
	        SimulationResults results = simulationEngine.runSimulation(criteria);
            List actionRequestsToProcess = results.getSimulatedActionRequests();
            actionRequestsToProcess.addAll(results.getDocument().getActionRequests());
            for (Iterator iter = actionRequestsToProcess.iterator(); iter.hasNext();) {
				ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
                if (actionRequest.isDone()) {
                    // an action taken has eliminated this request from being active
                    continue;
                }
				// if no action request codes are passed in.... assume any request found is
		    	if ( (actionRequestedCodes == null) || (actionRequestedCodes.length == 0) ) {
		    		// we found an action request
		    		return true;
		    	}
		    	// check the action requested codes passed in
		    	for (int i = 0; i < actionRequestedCodes.length; i++) {
					String requestedActionRequestCode = actionRequestedCodes[i];
					if (requestedActionRequestCode.equals(actionRequest.getActionRequested())) {
                        if (StringUtils.isBlank(reportCriteriaVO.getTargetNodeName())) {
                            return true;
                        } else if (reportCriteriaVO.getTargetNodeName().equals(actionRequest.getNodeInstance().getName())) {
                            return true;
                        }
					}
				}
			}
	        return false;
        } catch (Exception ex) {
        	String error = "Problems evaluating documentWillHaveAtLeastOneActionRequest: " + ex.getMessage();
            LOG.error(error,ex);
            throw new RemoteException(error,ex);
        }
    }

    public boolean isLastApproverInRouteLevel(Long routeHeaderId, UserIdVO userId, Integer routeLevel) throws WorkflowException {
        if (routeLevel == null) {
            LOG.error("null routeLevel passed in.");
            throw new RuntimeException("null routeLevel passed in.");
        }
        LOG.debug("Evaluating isLastApproverInRouteLevel [docId=" + routeHeaderId + ", userId=" + userId + ", routeLevel=" + routeLevel + "]");
        DocumentRouteHeaderValue document = loadDocument(routeHeaderId);
        RouteNode node = CompatUtils.getNodeForLevel(document.getDocumentType(), routeLevel);
        if (node == null) {
            throw new RuntimeException("Cannot resolve given route level to an approriate node name: " + routeLevel);
        }
        return isLastApproverAtNode(routeHeaderId, userId, node.getRouteNodeName());
    }

    public boolean isLastApproverAtNode(Long routeHeaderId, UserIdVO userId, String nodeName) throws WorkflowException {
        if (routeHeaderId == null) {
            LOG.error("null routeHeaderId passed in.");
            throw new RuntimeException("null routeHeaderId passed in.");
        }
        if (userId == null ){
            LOG.error("null userId passed in.");
            throw new RuntimeException("null userId passed in.");
        }
        LOG.debug("Evaluating isLastApproverAtNode [docId=" + routeHeaderId + ", userId=" + userId + ", nodeName=" + nodeName + "]");
        loadDocument(routeHeaderId);
        // If this app constant is set to true, then we will attempt to simulate activation of non-active requests before
        // attempting to deactivate them, this is in order to address the ignore previous issue reported by EPIC in issue
        // http://fms.dfa.cornell.edu:8080/browse/KULWF-366
        boolean activateFirst = false;
        String activateFirstValue = Utilities.getApplicationConstant(EdenConstants.IS_LAST_APPROVER_ACTIVATE_FIRST);
        if (!Utilities.isEmpty(activateFirstValue)) {
            activateFirst = new Boolean(activateFirstValue).booleanValue();
        }
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        List requests = KEWServiceLocator.getActionRequestService().findPendingByDocRequestCdNodeName(routeHeaderId, EdenConstants.ACTION_REQUEST_APPROVE_REQ, nodeName);
        if (requests == null || requests.isEmpty()) {
            return false;
        }
        ActivationContext activationContext = new ActivationContext(ActivationContext.CONTEXT_IS_SIMULATION);
        for (Iterator iterator = requests.iterator(); iterator.hasNext();) {
            ActionRequestValue request = (ActionRequestValue) iterator.next();
            if (activateFirst && !request.isActive()) {
                KEWServiceLocator.getActionRequestService().activateRequest(request, activationContext);
            }
            if (request.isUserRequest() && request.getWorkflowId().equals(user.getWorkflowUserId().getWorkflowId())) {
                KEWServiceLocator.getActionRequestService().deactivateRequest(null, request, activationContext);
            } else if (request.isWorkgroupRequest() && request.getWorkgroup().hasMember(user)) {
                KEWServiceLocator.getActionRequestService().deactivateRequest(null, request, activationContext);
            }
        }
        boolean allDeactivated = true;
        for (Iterator iter = requests.iterator(); iter.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
            allDeactivated = allDeactivated && actionRequest.isDeactivated();
        }
        return allDeactivated;
    }

    /**
     * Used to determine if a given route level will produce Approve Action Requests.
     *
     * @deprecated use routeNodeHasApproverActionRequest instead
     */
    public boolean routeLevelHasApproverActionRequest(String documentTypeName, String docContent, Integer routeLevel) throws WorkflowException {
        if (documentTypeName == null) {
            LOG.error("null document type name passed in.");
            throw new RuntimeException("null document type passed in.");
        }
        if (routeLevel == null) {
            LOG.error("null routeLevel passed in.");
            throw new RuntimeException("null routeLevel passed in.");
        }
        LOG.debug("Evaluating routeLevelHasApproverActionRequest [docTypeName=" + documentTypeName + ", routeLevel=" + routeLevel + "]");
        DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
        if (!CompatUtils.isRouteLevelCompatible(documentType)) {
            throw new WorkflowException("The given document type is not route level compatible: " + documentTypeName);
        }
        RouteNode routeNode = CompatUtils.getNodeForLevel(documentType, routeLevel);
        return routeNodeHasApproverActionRequest(documentType, docContent, routeNode, routeLevel);
    }

    public boolean routeNodeHasApproverActionRequest(String documentTypeName, String docContent, String nodeName) throws WorkflowException {
        if (documentTypeName == null) {
            LOG.error("null docType passed in.");
            throw new RuntimeException("null docType passed in.");
        }
        if (nodeName == null) {
            LOG.error("null nodeName passed in.");
            throw new RuntimeException("null nodeName passed in.");
        }
        LOG.debug("Evaluating routeNodeHasApproverActionRequest [docTypeName=" + documentTypeName + ", nodeName=" + nodeName + "]");
        DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
        RouteNode routeNode = KEWServiceLocator.getRouteNodeService().findRouteNodeByName(documentType.getDocumentTypeId(), nodeName);
        return routeNodeHasApproverActionRequest(documentType, docContent, routeNode, new Integer(EdenConstants.INVALID_ROUTE_LEVEL));
    }

    /**
     * Really this method needs to be implemented using the routingReport functionality (the SimulationEngine).
     * This would get rid of the needs for us to call to FlexRM directly.
     */
    private boolean routeNodeHasApproverActionRequest(DocumentType documentType, String docContent, RouteNode node, Integer routeLevel) throws WorkflowException {
        if (documentType == null) {
            LOG.error("could not locate document type.");
            throw new RuntimeException("could not locate document type.");
        }
        if (docContent == null) {
            LOG.error("null docContent passed in.");
            throw new RuntimeException("null docContent passed in.");
        }
        if (node == null) {
            LOG.error("could not locate route node.");
            throw new RuntimeException("could not locate route node.");
        }

        DocumentRouteHeaderValue routeHeader = new DocumentRouteHeaderValue();
        routeHeader.setRouteHeaderId(new Long(0));
        routeHeader.setDocumentTypeId(documentType.getDocumentTypeId());
        routeHeader.setDocRouteLevel(routeLevel);
        routeHeader.setDocVersion(new Integer(EdenConstants.CURRENT_DOCUMENT_VERSION));

        if (node.getRuleTemplate() != null && node.isFlexRM()) {
            String ruleTemplateName = node.getRuleTemplate().getName();
            routeHeader.setDocContent(docContent);
            routeHeader.setDocRouteStatus(EdenConstants.ROUTE_HEADER_INITIATED_CD);
            FlexRM flexRM = new FlexRM();
    		RouteContext context = RouteContext.getCurrentRouteContext();
    		context.setDocument(routeHeader);
    		try {
    			List actionRequests = flexRM.getActionRequests(routeHeader, ruleTemplateName);
    			for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
    				ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
    				if (actionRequest.isApproveOrCompleteRequest()) {
    					return true;
    				}
    			}
    		} finally {
    			RouteContext.clearCurrentRouteContext();
    		}
        }
        return false;
    }

    private void incomingParamCheck(Object object, String name) {
        if (object == null) {
            LOG.error("null " + name + " passed in.");
            throw new RuntimeException("null " + name + " passed in.");
        }
    }

    public void reResolveRole(String documentTypeName, String roleName, String qualifiedRoleNameLabel) throws WorkflowException {
        incomingParamCheck(documentTypeName, "documentTypeName");
        incomingParamCheck(roleName, "roleName");
        incomingParamCheck(qualifiedRoleNameLabel, "qualifiedRoleNameLabel");
        LOG.debug("Re-resolving Role [docTypeName=" + documentTypeName + ", roleName=" + roleName + ", qualifiedRoleNameLabel=" + qualifiedRoleNameLabel + "]");
    	DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
    	if (Utilities.isEmpty(qualifiedRoleNameLabel)) {
    		KEWServiceLocator.getRoleService().reResolveRole(documentType, roleName);
    	} else {
    		KEWServiceLocator.getRoleService().reResolveQualifiedRole(documentType, roleName, qualifiedRoleNameLabel);
    	}
    }

    public void reResolveRoleByDocumentId(Long documentId, String roleName, String qualifiedRoleNameLabel) throws WorkflowException {
        incomingParamCheck(documentId, "documentId");
        incomingParamCheck(roleName, "roleName");
        incomingParamCheck(qualifiedRoleNameLabel, "qualifiedRoleNameLabel");
        LOG.debug("Re-resolving Role [documentId=" + documentId + ", roleName=" + roleName + ", qualifiedRoleNameLabel=" + qualifiedRoleNameLabel + "]");
        DocumentRouteHeaderValue routeHeader = loadDocument(documentId);
    	if (Utilities.isEmpty(qualifiedRoleNameLabel)) {
    		KEWServiceLocator.getRoleService().reResolveRole(routeHeader, roleName);
    	} else {
    		KEWServiceLocator.getRoleService().reResolveQualifiedRole(routeHeader, roleName, qualifiedRoleNameLabel);
    	}
    }

    public DocumentDetailVO routingReport(ReportCriteriaVO reportCriteria) throws WorkflowException {
        incomingParamCheck(reportCriteria, "reportCriteria");
        LOG.debug("Executing routing report [docId=" + reportCriteria.getRouteHeaderId() + ", docTypeName=" + reportCriteria.getDocumentTypeName() + "]");
        SimulationCriteria criteria = BeanConverter.convertReportCriteriaVO(reportCriteria);
        return BeanConverter.convertDocumentDetail(KEWServiceLocator.getRoutingReportService().report(criteria));
    }

    public boolean isFinalApprover(Long routeHeaderId, UserIdVO userId) throws WorkflowException {
        incomingParamCheck(routeHeaderId, "routeHeaderId");
        incomingParamCheck(userId, "userId");
        LOG.debug("Evaluating isFinalApprover [docId=" + routeHeaderId + ", userId=" + userId + "]");
        DocumentRouteHeaderValue routeHeader = loadDocument(routeHeaderId);
        List requests = KEWServiceLocator.getActionRequestService().findPendingByDoc(routeHeaderId);
        List finalApproverNodes = KEWServiceLocator.getRouteNodeService().findFinalApprovalRouteNodes(routeHeader.getDocumentType().getDocumentTypeId());
        if (finalApproverNodes.isEmpty()) {
            LOG.debug("Could not locate final approval nodes for document " + routeHeaderId);
            return false;
        }
        Set finalApproverNodeNames = new HashSet();
        for (Iterator iterator = finalApproverNodes.iterator(); iterator.hasNext();) {
            RouteNode node = (RouteNode) iterator.next();
            finalApproverNodeNames.add(node.getRouteNodeName());
        }

        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
        int approveRequest = 0;
        for (Iterator iter = requests.iterator(); iter.hasNext();) {
            ActionRequestValue request = (ActionRequestValue) iter.next();
            RouteNodeInstance nodeInstance = request.getNodeInstance();
            if (nodeInstance == null) {
                LOG.debug("Found an action request on the document with a null node instance, indicating EXCEPTION routing.");
                return false;
            }
            if (finalApproverNodeNames.contains(nodeInstance.getRouteNode().getRouteNodeName())) {
                if (request.isApproveOrCompleteRequest()) {
                    approveRequest++;
                    LOG.debug("Found request is approver " + request.getActionRequestId());
                    if (! request.isRecipientRoutedRequest(user)) {
                        LOG.debug("Action Request not for user " + user.getAuthenticationUserId().getAuthenticationId());
                        return false;
                    }
                }
            }
        }

        if (approveRequest == 0) {
            return false;
        }
        LOG.debug("User "+userId+" is final approver for document " + routeHeaderId);
        return true;
    }

    public boolean isSuperUserForDocumentType(UserIdVO userId, Long documentTypeId) throws WorkflowException {
    	LOG.debug("Determining super user status [userId=" + userId + ", documentTypeId=" + documentTypeId + "]");
    	DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findById(documentTypeId);
    	WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
    	boolean isSuperUser = documentType.isSuperUser(user);
    	LOG.debug("Super user status is " + isSuperUser + ".");
    	return isSuperUser;
    }

    private DocumentRouteHeaderValue loadDocument(Long documentId) {
    	KEWServiceLocator.getRouteHeaderService().lockRouteHeader(documentId, true);
        return KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
    }

    public DocumentContentVO getDocumentContent(Long routeHeaderId) throws WorkflowException {
    	LOG.debug("Fetching document content [docId=" + routeHeaderId + "]");
    	DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(routeHeaderId);
    	return BeanConverter.convertDocumentContent(document.getDocContent(), routeHeaderId);
    }

	public String[] getPreviousRouteNodeNames(Long documentId) throws RemoteException, WorkflowException {
		LOG.debug("Fetching previous node names [docId=" + documentId + "]");
		DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
		//going conservative for now.  if the doc isn't enroute or exception nothing will be returned.
		if (document.isEnroute() || document.isInException()) {

			List activeNodeInstances = KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(document);
			long largetActivatedNodeId = 0;
			for (Iterator iter = activeNodeInstances.iterator(); iter.hasNext();) {
				RouteNodeInstance routeNodeInstance = (RouteNodeInstance) iter.next();
				if (routeNodeInstance.getRouteNode().getRouteNodeId().longValue() > largetActivatedNodeId) {
					largetActivatedNodeId = routeNodeInstance.getRouteNode().getRouteNodeId().longValue();
				}
			}

			List routeNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodeInstances(document, false);
			List nodeNames = new ArrayList();
			
			for (Iterator iter = routeNodes.iterator(); iter.hasNext();) {
				RouteNodeInstance routeNode = (RouteNodeInstance) iter.next();
				if (routeNode.isComplete() && !nodeNames.contains(routeNode.getName())) {
					//if the prototype of the nodeInstance we're analyzing is less than the largest id of all our active prototypes
					//then add it to the list.  This is an attempt to account for return to previous hitting a single node multiple times
					if (routeNode.getRouteNode().getRouteNodeId().longValue() < largetActivatedNodeId) {
						nodeNames.add(routeNode.getName());
					}
				}
			}
			return (String[]) nodeNames.toArray(new String[nodeNames.size()]);
		} else {
			return new String[0];
		}
	}

    public RuleVO[] ruleReport(RuleReportCriteriaVO ruleReportCriteria) throws RemoteException, WorkflowException {
        incomingParamCheck(ruleReportCriteria, "ruleReportCriteria");
        if (ruleReportCriteria == null) {
            throw new IllegalArgumentException("At least one criterion must be sent in a RuleReportCriteriaVO object");
        }
        LOG.debug("Executing rule report [responsibleUser=" + ruleReportCriteria.getResponsibleUser() + ", responsibleWorkgroup=" +
                ruleReportCriteria.getResponsibleWorkgroup() + "]");
        Map extensionValues = new HashMap();
        if (ruleReportCriteria.getRuleExtensionVOs() != null) {
            for (int i = 0; i < ruleReportCriteria.getRuleExtensionVOs().length; i++) {
                RuleExtensionVO ruleExtensionVO = ruleReportCriteria.getRuleExtensionVOs()[i];
                KeyValuePair ruleExtension = BeanConverter.convertRuleExtensionVO(ruleExtensionVO);
                extensionValues.put(ruleExtension.getKey(), ruleExtension.getValue());
            }
        }
        Collection<String> actionRequestCodes = null;
        if ( (ruleReportCriteria.getActionRequestCodes() != null) && (ruleReportCriteria.getActionRequestCodes().length != 0) ) {
            actionRequestCodes = Arrays.asList(ruleReportCriteria.getActionRequestCodes());
        }
        Collection rulesFound = KEWServiceLocator.getRuleService().search(ruleReportCriteria.getDocumentTypeName(),ruleReportCriteria.getRuleTemplateName(),
                ruleReportCriteria.getRuleDescription(),BeanConverter.convertWorkgroupIdVO(ruleReportCriteria.getResponsibleWorkgroup()),
                BeanConverter.convertUserIdVO(ruleReportCriteria.getResponsibleUser()),ruleReportCriteria.getResponsibleRoleName(),
                ruleReportCriteria.isConsiderWorkgroupMembership(),ruleReportCriteria.isIncludeDelegations(),
                ruleReportCriteria.isActiveIndicator(),extensionValues,actionRequestCodes);
        RuleVO[] returnableRules = new RuleVO[rulesFound.size()];
        int i = 0;
        for (Iterator iter = rulesFound.iterator(); iter.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) iter.next();
            returnableRules[i] = BeanConverter.convertRule(rule);
            i++;
        }
        return returnableRules;
    }
}