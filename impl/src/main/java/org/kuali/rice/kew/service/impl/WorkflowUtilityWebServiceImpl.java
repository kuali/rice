/*
 * Copyright 2006-2011 The Kuali Foundation
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

package org.kuali.rice.kew.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.core.util.KeyValue;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.KimPrincipalRecipient;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.definition.AttributeDefinition;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocumentSearchResultComponents;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.documentlink.DocumentLink;
import org.kuali.rice.kew.dto.ActionItemDTO;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.ActionTakenDTO;
import org.kuali.rice.kew.dto.DTOConverter;
import org.kuali.rice.kew.dto.DocumentContentDTO;
import org.kuali.rice.kew.dto.DocumentDetailDTO;
import org.kuali.rice.kew.dto.DocumentLinkDTO;
import org.kuali.rice.kew.dto.DocumentSearchCriteriaDTO;
import org.kuali.rice.kew.dto.DocumentSearchResultDTO;
import org.kuali.rice.kew.dto.DocumentStatusTransitionDTO;
import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.dto.PropertyDefinitionDTO;
import org.kuali.rice.kew.dto.ReportCriteriaDTO;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.dto.RouteNodeInstanceDTO;
import org.kuali.rice.kew.dto.RuleDTO;
import org.kuali.rice.kew.dto.RuleExtensionDTO;
import org.kuali.rice.kew.dto.RuleReportCriteriaDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeValidationErrorDTO;
import org.kuali.rice.kew.engine.ActivationContext;
import org.kuali.rice.kew.engine.CompatUtils;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.engine.simulation.SimulationCriteria;
import org.kuali.rice.kew.engine.simulation.SimulationResults;
import org.kuali.rice.kew.engine.simulation.SimulationWorkflowEngine;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.DocumentStatusTransition;
import org.kuali.rice.kew.rule.FlexRM;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.WorkflowAttribute;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kew.rule.WorkflowAttributeXmlValidator;
import org.kuali.rice.kew.rule.xmlrouting.GenericXMLRuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowUtility;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.KEWWebServiceConstants;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import javax.jws.WebService;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked"})
@WebService(endpointInterface = KEWWebServiceConstants.WorkflowUtility.INTERFACE_CLASS,
        serviceName = KEWWebServiceConstants.WorkflowUtility.WEB_SERVICE_NAME,
        portName = KEWWebServiceConstants.WorkflowUtility.WEB_SERVICE_PORT,
        targetNamespace = KEWWebServiceConstants.MODULE_TARGET_NAMESPACE)
public class WorkflowUtilityWebServiceImpl implements WorkflowUtility {

    private static final Logger LOG = Logger.getLogger(WorkflowUtilityWebServiceImpl.class);

    public RouteHeaderDTO getRouteHeaderWithPrincipal(String principalId, String documentId) throws WorkflowException {
        if (documentId == null) {
            LOG.error("null documentId passed in.  Throwing RuntimeExcpetion");
            throw new RuntimeException("Null documentId passed in.");
        }
        if (principalId == null) {
            LOG.error("null principalId passed in.");
            throw new RuntimeException("null principalId passed in");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("Fetching RouteHeaderVO [id="+documentId+", user="+principalId+"]");
        }
        DocumentRouteHeaderValue document = loadDocument(documentId);
        RouteHeaderDTO routeHeaderVO = DTOConverter.convertRouteHeader(document, principalId);
        if (routeHeaderVO == null) {
        	LOG.error("Returning null RouteHeaderVO [id=" + documentId + ", user=" + principalId + "]");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("Returning RouteHeaderVO [id=" + documentId + ", user=" + principalId + "]");
        }
        return routeHeaderVO;
    }

    public AttributeSet getActionsRequested(String principalId, String documentId) {
        if (documentId == null) {
            LOG.error("null documentId passed in.  Throwing RuntimeExcpetion");
            throw new RuntimeException("Null documentId passed in.");
        }
        if (principalId == null) {
            LOG.error("null principalId passed in.");
            throw new RuntimeException("null principalId passed in");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("Fetching DocumentRouteHeaderValue [id="+documentId+", user="+principalId+"]");
        }
        DocumentRouteHeaderValue document = loadDocument(documentId);
        return KEWServiceLocator.getActionRequestService().getActionsRequested(document, principalId, true);
    }

    public RouteHeaderDTO getRouteHeader(String documentId) throws WorkflowException {
        if (documentId == null) {
            LOG.error("null documentId passed in.");
            throw new RuntimeException("null documentId passed in");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("Fetching RouteHeaderVO [id="+documentId+"]");
        }
        DocumentRouteHeaderValue document = loadDocument(documentId);
        
        UserSession userSession = GlobalVariables.getUserSession();
        String principalId = null;
        if (userSession != null) { // get the principalId if we can
        	principalId = userSession.getPrincipalId();
        }
        
        RouteHeaderDTO routeHeaderVO = DTOConverter.convertRouteHeader(document, principalId);
        if (routeHeaderVO == null) {
        	LOG.error("Returning null RouteHeaderVO [id=" + documentId + "]");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("Returning RouteHeaderVO [id=" + documentId + "]");
        }
        return routeHeaderVO;
    }

    public String getDocumentStatus(String documentId) throws WorkflowException {
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

    public DocumentDetailDTO getDocumentDetail(String documentId) throws WorkflowException {
        if (documentId == null) {
            LOG.error("null documentId passed in.");
            throw new RuntimeException("null documentId passed in");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Fetching DocumentDetailVO [id="+documentId+"]");
        }
        DocumentRouteHeaderValue document = loadDocument(documentId);
        DocumentDetailDTO documentDetailVO = DTOConverter.convertDocumentDetail(document);
        if (documentDetailVO == null) {
        	LOG.error("Returning null DocumentDetailVO [id=" + documentId + "]");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Returning DocumentDetailVO [id=" + documentId + "]");
        }
        return documentDetailVO;
    }

    public RouteNodeInstanceDTO getNodeInstance(Long nodeInstanceId) throws WorkflowException {
        if (nodeInstanceId == null) {
            LOG.error("null nodeInstanceId passed in.");
            throw new RuntimeException("null nodeInstanceId passed in");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Fetching RouteNodeInstanceVO [id="+nodeInstanceId+"]");
        }
        RouteNodeInstance nodeInstance = KEWServiceLocator.getRouteNodeService().findRouteNodeInstanceById(nodeInstanceId);
        return DTOConverter.convertRouteNodeInstance(nodeInstance);
    }

    public DocumentTypeDTO getDocumentType(String documentTypeId) throws WorkflowException {
        if (documentTypeId == null) {
            LOG.error("null documentTypeId passed in.");
            throw new RuntimeException("null documentTypeId passed in.");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Fetching DocumentTypeVO [documentTypeId="+documentTypeId+"]");
        }
        return KEWServiceLocator.getDocumentTypeService().getDocumentTypeVOById(documentTypeId);
    }

    public DocumentTypeDTO getDocumentTypeByName(String documentTypeName) throws WorkflowException {
        if (documentTypeName == null) {
            LOG.error("null documentTypeName passed in.");
            throw new RuntimeException("null documentTypeName passed in");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Fetching DocumentTypeVO [documentTypeName="+documentTypeName+"]");
        }
        DocumentTypeDTO documentType = KEWServiceLocator.getDocumentTypeService().getDocumentTypeVOByName(documentTypeName);
        return documentType;
    }

    public Long getNewResponsibilityId() {
    	LOG.debug("Getting new responsibility id.");
        Long rid = KEWServiceLocator.getResponsibilityIdService().getNewResponsibilityId();
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("returning responsibility Id " + rid);
        }
        return rid;
    }

    public Integer getUserActionItemCount(String principalId) throws WorkflowException {
        return Integer.valueOf(KEWServiceLocator.getActionListService().getCount(principalId));
    }

	public ActionItemDTO[] getActionItemsForPrincipal(String principalId) throws WorkflowException {
        //added by Derek
        Collection actionItems = KEWServiceLocator.getActionListService().getActionList(principalId, null);
        ActionItemDTO[] actionItemVOs = new ActionItemDTO[actionItems.size()];
        int i = 0;
        for (Iterator iterator = actionItems.iterator(); iterator.hasNext(); i++) {
            ActionItem actionItem = (ActionItem) iterator.next();
            actionItemVOs[i] = DTOConverter.convertActionItem(actionItem);
        }
        return actionItemVOs;
    }

    public ActionItemDTO[] getAllActionItems(String documentId) throws WorkflowException {
        Collection actionItems = KEWServiceLocator.getActionListService().getActionListForSingleDocument(documentId);
        ActionItemDTO[] actionItemVOs = new ActionItemDTO[actionItems.size()];
        int i = 0;
        for (Iterator iterator = actionItems.iterator(); iterator.hasNext(); i++) {
            ActionItem actionItem = (ActionItem) iterator.next();
            actionItemVOs[i] = DTOConverter.convertActionItem(actionItem);
        }
        return actionItemVOs;
    }

    public ActionItemDTO[] getActionItems(String documentId, String[] actionRequestedCodes) throws WorkflowException {
        List<String> actionRequestedCds = Arrays.asList(actionRequestedCodes);
        ActionItemDTO[] actionItems = getAllActionItems(documentId);
        List<ActionItemDTO> matchingActionitems = new ArrayList<ActionItemDTO>();
        for (ActionItemDTO actionItemVO : actionItems) {
            if (actionRequestedCds.contains(actionItemVO.getActionRequestCd())) {
                matchingActionitems.add(actionItemVO);
            }
        }
        ActionItemDTO[] returnActionItems = new ActionItemDTO[matchingActionitems.size()];
        int j = 0;
        for (ActionItemDTO actionItemVO : matchingActionitems) {
            returnActionItems[j] = actionItemVO;
            j++;
        }
        return returnActionItems;
    }

    public ActionRequestDTO[] getAllActionRequests(String documentId) throws WorkflowException {
        return getActionRequests(documentId, null, null);
    }

    /**
     * Returns a flattened list of ActionRequests which match the given criteria.
     * Because the list is flattened, that means that all children requests from
     * all graphs are returned in the top-level list.
     */
    public ActionRequestDTO[] getActionRequests(String documentId, String nodeName, String principalId) throws WorkflowException {
        if (documentId == null) {
            LOG.error("null documentId passed in.");
            throw new RuntimeException("null documentId passed in.");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Fetching ActionRequestVOs [docId="+documentId+"]");
        }
        List actionRequests = KEWServiceLocator.getActionRequestService().findAllActionRequestsByDocumentId(documentId);
        List matchingActionRequests = new ArrayList();
        for (Iterator iterator = actionRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue actionRequestValue = (ActionRequestValue) iterator.next();
            if (actionRequestMatches(actionRequestValue, nodeName, principalId)) {
                matchingActionRequests.add(actionRequestValue);
            }
        }
        ActionRequestDTO[] actionRequestVOs = new ActionRequestDTO[matchingActionRequests.size()];
        int i = 0;
        for (Iterator iter = matchingActionRequests.iterator(); iter.hasNext(); i++) {
            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
            actionRequestVOs[i] = DTOConverter.convertActionRequest(actionRequest);
        }
        return actionRequestVOs;
    }

    private boolean actionRequestMatches(ActionRequestValue actionRequest, String nodeName, String principalId) throws WorkflowException {
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

    public ActionTakenDTO[] getActionsTaken(String documentId) throws WorkflowException {
        if (documentId == null) {
            LOG.error("null documentId passed in.");
            throw new RuntimeException("null documentId passed in.");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Fetching ActionTakenVOs [docId="+documentId+"]");
        }
        Collection actionsTaken = KEWServiceLocator.getActionTakenService().findByDocumentId(documentId);
        ActionTakenDTO[] actionTakenVOs = new ActionTakenDTO[actionsTaken.size()];
        int i = 0;
        for (Iterator iter = actionsTaken.iterator(); iter.hasNext(); i++) {
            ActionTakenValue actionTaken = (ActionTakenValue) iter.next();
            actionTakenVOs[i] = DTOConverter.convertActionTakenWithActionRequests(actionTaken);
        }
        return actionTakenVOs;
    }

    /**
     * This work is also being done in the bowels of convertDocumentContentVO in DTOConverter so some code
     * could be reduced.
     *
     * @param definition
     * @return WorkflowAttributeValidationErrorVO[] errors from client input into attribute
     */
    public WorkflowAttributeValidationErrorDTO[] validateWorkflowAttributeDefinitionVO(WorkflowAttributeDefinitionDTO definition) throws WorkflowException {
        if (definition == null) {
            LOG.error("null definition passed in.");
            throw new RuntimeException("null definition passed in.");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Validating WorkflowAttributeDefinitionVO [attributeName="+definition.getAttributeName()+"]");
        }
        AttributeDefinition attributeDefinition = DTOConverter.convertWorkflowAttributeDefinitionVO(definition, null);
        WorkflowAttribute attribute = null;
        if (attributeDefinition != null) {
        	attribute = (WorkflowAttribute) GlobalResourceLoader.getObject(attributeDefinition.getObjectDefinition());
        }
        if (attribute instanceof GenericXMLRuleAttribute) {
            Map<String, String> attributePropMap = new HashMap<String, String>();
            GenericXMLRuleAttribute xmlAttribute = (GenericXMLRuleAttribute)attribute;
            xmlAttribute.setRuleAttribute(attributeDefinition.getRuleAttribute());
            for (int i = 0; i < definition.getProperties().length; i++) {
		PropertyDefinitionDTO property = definition.getProperties()[i];
		attributePropMap.put(property.getName(), property.getValue());
	    }
            xmlAttribute.setParamMap(attributePropMap);
	}
        //validate inputs from client application if the attribute is capable
        if (attribute instanceof WorkflowAttributeXmlValidator) {
            List errors = ((WorkflowAttributeXmlValidator)attribute).validateClientRoutingData();
            WorkflowAttributeValidationErrorDTO[] errorVOs = new WorkflowAttributeValidationErrorDTO[errors.size()];
            for (int i = 0; i < errorVOs.length; i++) {
                errorVOs[i] = DTOConverter.convertWorkflowAttributeValidationError((WorkflowAttributeValidationError)errors.get(i));
            }
            return errorVOs;
        } else {
            // WORKAROUND: if it is not validatable, then just quietly succeed
            return new WorkflowAttributeValidationErrorDTO[0];
        }
    }

    public RouteNodeInstanceDTO[] getDocumentRouteNodeInstances(String documentId) throws WorkflowException {
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug("Fetching RouteNodeInstanceVOs [docId=" + documentId + "]");
    	}
    	return convertRouteNodeInstances(KEWServiceLocator.getRouteNodeService().getFlattenedNodeInstances(loadDocument(documentId), true));
    }

    public RouteNodeInstanceDTO[] getActiveNodeInstances(String documentId) throws WorkflowException {
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug("Fetching active RouteNodeInstanceVOs [docId=" + documentId + "]");
    	}
        loadDocument(documentId);
        return convertRouteNodeInstances(KEWServiceLocator.getRouteNodeService().getActiveNodeInstances(documentId));
    }

    public RouteNodeInstanceDTO[] getTerminalNodeInstances(String documentId) throws WorkflowException {
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug("Fetching terminal RouteNodeInstanceVOs [docId=" + documentId + "]");
    	}
    	loadDocument(documentId);
        return convertRouteNodeInstances(KEWServiceLocator.getRouteNodeService().getTerminalNodeInstances(documentId));
    }

    public RouteNodeInstanceDTO[] getCurrentNodeInstances(String documentId) throws WorkflowException {
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug("Fetching current RouteNodeInstanceVOs [docId=" + documentId + "]");
    	}
    	loadDocument(documentId);
    	return convertRouteNodeInstances(KEWServiceLocator.getRouteNodeService().getCurrentNodeInstances(documentId));
    }

    private RouteNodeInstanceDTO[] convertRouteNodeInstances(List nodeInstances) throws WorkflowException {
        RouteNodeInstanceDTO[] nodeInstanceVOs = new RouteNodeInstanceDTO[nodeInstances.size()];
        int i = 0;
        for (Iterator iter = nodeInstances.iterator(); iter.hasNext(); ) {
            nodeInstanceVOs[i++] = DTOConverter.convertRouteNodeInstance((RouteNodeInstance) iter.next());
        }
        return nodeInstanceVOs;
    }

    public boolean isUserInRouteLog(String documentId, String principalId, boolean lookFuture) throws WorkflowException {
    	return isUserInRouteLogWithOptionalFlattening(documentId, principalId, lookFuture, false);
    }
    
    public boolean isUserInRouteLogWithOptionalFlattening(String documentId, String principalId, boolean lookFuture, boolean flattenNodes) throws WorkflowException {
        if (documentId == null) {
            LOG.error("null documentId passed in.");
            throw new RuntimeException("null documentId passed in.");
        }
        if (principalId == null ){
            LOG.error("null principalId passed in.");
            throw new RiceRuntimeException("null principalId passed in.");
        }
        boolean authorized = false;
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Evaluating isUserInRouteLog [docId=" + documentId + ", principalId=" + principalId + ", lookFuture=" + lookFuture + "]");
        }
        DocumentRouteHeaderValue routeHeader = loadDocument(documentId);
        Principal principal = KEWServiceLocator.getIdentityHelperService().getPrincipal(principalId);
        List actionsTaken = KEWServiceLocator.getActionTakenService().findByDocumentIdWorkflowId(documentId, principal.getPrincipalId());

        if(routeHeader.getInitiatorWorkflowId().equals(principal.getPrincipalId())){
        	return true;
        }

        if (actionsTaken.size() > 0) {
        	LOG.debug("found action taken by user");
        	authorized = true;
        }

        List actionRequests = KEWServiceLocator.getActionRequestService().findAllActionRequestsByDocumentId(documentId);
        if (actionRequestListHasPrincipal(principal, actionRequests)) {
        	authorized = true;
        }

        if (!lookFuture) {
        	return authorized;
        }


        SimulationWorkflowEngine simulationEngine = KEWServiceLocator.getSimulationEngine();
        SimulationCriteria criteria = SimulationCriteria.createSimulationCritUsingDocumentId(documentId);
        criteria.setDestinationNodeName(null); // process entire document to conclusion
        criteria.getDestinationRecipients().add(new KimPrincipalRecipient(principal));
        criteria.setFlattenNodes(flattenNodes);

        try {
        	SimulationResults results = simulationEngine.runSimulation(criteria);
        	if (actionRequestListHasPrincipal(principal, results.getSimulatedActionRequests())) {
        		authorized = true;
        	}
        } catch (Exception e) {
        	throw new RiceRuntimeException(e);
        }

        return authorized;
    }

    /**
     * @see org.kuali.rice.kew.service.WorkflowUtility#getPrincipalIdsInRouteLog(java.lang.Long, boolean)
     */
    public String[] getPrincipalIdsInRouteLog(String documentId, boolean lookFuture) throws WorkflowException {
        if (documentId == null) {
            LOG.error("null documentId passed in.");
            throw new RuntimeException("null documentId passed in.");
        }
    	Set<String> principalIds = new HashSet<String>();
        try {
        	if ( LOG.isDebugEnabled() ) {
        		LOG.debug("Evaluating isUserInRouteLog [docId=" + documentId + ", lookFuture=" + lookFuture + "]");
        	}
            DocumentRouteHeaderValue routeHeader = loadDocument(documentId);
            List<ActionTakenValue> actionsTakens =
            	(List<ActionTakenValue>)KEWServiceLocator.getActionTakenService().findByDocumentId(documentId);
            //TODO: confirm that the initiator is not already there in the actionstaken
            principalIds.add(routeHeader.getInitiatorWorkflowId());
            for(ActionTakenValue actionTaken: actionsTakens){
            	principalIds.add(actionTaken.getPrincipalId());
            }
            List<ActionRequestValue> actionRequests =
            	KEWServiceLocator.getActionRequestService().findAllActionRequestsByDocumentId(documentId);
            for(ActionRequestValue actionRequest: actionRequests){
            	principalIds.addAll(getPrincipalIdsForActionRequest(actionRequest));
            }
            if (!lookFuture) {
            	return principalIds.toArray(new String[]{});
            }
            SimulationWorkflowEngine simulationEngine = KEWServiceLocator.getSimulationEngine();
            SimulationCriteria criteria = SimulationCriteria.createSimulationCritUsingDocumentId(documentId);
            criteria.setDestinationNodeName(null); // process entire document to conclusion
            SimulationResults results = simulationEngine.runSimulation(criteria);
            actionRequests = (List<ActionRequestValue>)results.getSimulatedActionRequests();
            for(ActionRequestValue actionRequest: actionRequests){
            	principalIds.addAll(getPrincipalIdsForActionRequest(actionRequest));
            }
        } catch (Exception ex) {
            LOG.warn("Problems getting principalIds in Route Log for documentId: "+documentId+". Exception:"+ex.getMessage(),ex);
        }
    	return principalIds.toArray(new String[]{});
    }

	/**
	 * This method gets all of the principalIds for the given ActionRequestValue.  It drills down into
	 * groups if need be.
	 * 
	 * @param actionRequest
	 */
	private List<String> getPrincipalIdsForActionRequest(ActionRequestValue actionRequest) {
		List<String> results = Collections.emptyList();
		if (actionRequest.getPrincipalId() != null) {
			results = Collections.singletonList(actionRequest.getPrincipalId());
		} else if (actionRequest.getGroupId() != null) {
			List<String> principalIdsForGroup = 
				KimApiServiceLocator.getGroupService().getMemberPrincipalIds(actionRequest.getGroupId());
			if (principalIdsForGroup != null) {
				results = principalIdsForGroup;
			}
		}
		return results;
	}

    /***
     * @see org.kuali.rice.kew.service.WorkflowUtility#getPrincipalIdsWithPendingActionRequestByActionRequestedAndDocId(java.lang.String, java.lang.Long)
     */
    public String[] getPrincipalIdsWithPendingActionRequestByActionRequestedAndDocId(String actionRequestedCd, String documentId){
    	List<String> results = KEWServiceLocator.getActionRequestService().
    				getPrincipalIdsWithPendingActionRequestByActionRequestedAndDocId(actionRequestedCd, documentId);
    	if (ObjectUtils.isNull(results)) {
    		return null;
    	}
    	return results.toArray(new String[]{});
    }

    private boolean actionRequestListHasPrincipal(Principal principal, List actionRequests) throws WorkflowException {
        for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
            if (actionRequest.isRecipientRoutedRequest(new KimPrincipalRecipient(principal))) {
                return true;
            }
        }
        return false;
    }

    private boolean isRecipientRoutedRequest(ActionRequestValue actionRequest, List<Recipient> recipients) throws WorkflowException {
        for (Recipient recipient : recipients) {
            if (actionRequest.isRecipientRoutedRequest(recipient)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.kuali.rice.kew.service.WorkflowUtility#documentWillHaveAtLeastOneActionRequest(org.kuali.rice.kew.dto.ReportCriteriaDTO, java.lang.String[], boolean)
     */
    public boolean documentWillHaveAtLeastOneActionRequest(ReportCriteriaDTO reportCriteriaDTO, String[] actionRequestedCodes, boolean ignoreCurrentActionRequests) {
        try {
	        SimulationWorkflowEngine simulationEngine = KEWServiceLocator.getSimulationEngine();
	        SimulationCriteria criteria = DTOConverter.convertReportCriteriaDTO(reportCriteriaDTO);
	        // set activate requests to true by default so force action works correctly
	        criteria.setActivateRequests(Boolean.TRUE);
	        SimulationResults results = simulationEngine.runSimulation(criteria);
            List actionRequestsToProcess = results.getSimulatedActionRequests();
            if (!ignoreCurrentActionRequests) {
                actionRequestsToProcess.addAll(results.getDocument().getActionRequests());
            }
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
		    	for (String requestedActionRequestCode : actionRequestedCodes) {
					if (requestedActionRequestCode.equals(actionRequest.getActionRequested())) {
					    boolean satisfiesDestinationUserCriteria = (criteria.getDestinationRecipients().isEmpty()) || (isRecipientRoutedRequest(actionRequest,criteria.getDestinationRecipients()));
					    if (satisfiesDestinationUserCriteria) {
					        if (StringUtils.isBlank(criteria.getDestinationNodeName())) {
					            return true;
					        } else if (StringUtils.equals(criteria.getDestinationNodeName(),actionRequest.getNodeInstance().getName())) {
					            return true;
					        }
					    }
					}
				}
			}
	        return false;
        } catch (Exception ex) {
        	String error = "Problems evaluating documentWillHaveAtLeastOneActionRequest: " + ex.getMessage();
            LOG.error(error,ex);
            if (ex instanceof RuntimeException) {
            	throw (RuntimeException)ex;
            }
            throw new RuntimeException(error, ex);
        }
    }

    public boolean isLastApproverInRouteLevel(String documentId, String principalId, Integer routeLevel) throws WorkflowException {
        if (routeLevel == null) {
            LOG.error("null routeLevel passed in.");
            throw new RuntimeException("null routeLevel passed in.");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Evaluating isLastApproverInRouteLevel [docId=" + documentId + ", principalId=" + principalId + ", routeLevel=" + routeLevel + "]");
        }
        DocumentRouteHeaderValue document = loadDocument(documentId);
        RouteNode node = CompatUtils.getNodeForLevel(document.getDocumentType(), routeLevel);
        if (node == null) {
            throw new RuntimeException("Cannot resolve given route level to an approriate node name: " + routeLevel);
        }
        return isLastApproverAtNode(documentId, principalId, node.getRouteNodeName());
    }

    public boolean isLastApproverAtNode(String documentId, String principalId, String nodeName) throws WorkflowException {
        if (documentId == null) {
            LOG.error("null documentId passed in.");
            throw new RuntimeException("null documentId passed in.");
        }
        if (principalId == null ){
            LOG.error("null principalId passed in.");
            throw new RuntimeException("null principalId passed in.");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Evaluating isLastApproverAtNode [docId=" + documentId + ", principalId=" + principalId + ", nodeName=" + nodeName + "]");
        }
        loadDocument(documentId);
        // If this app constant is set to true, then we will attempt to simulate activation of non-active requests before
        // attempting to deactivate them, this is in order to address the force action issue reported by EPIC in issue
        // http://fms.dfa.cornell.edu:8080/browse/KULWF-366
        Boolean activateFirst = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsBoolean(KEWConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.FEATURE_DETAIL_TYPE, KEWConstants.IS_LAST_APPROVER_ACTIVATE_FIRST_IND);
        if (activateFirst == null) {
            activateFirst = Boolean.FALSE;
        }

        List requests = KEWServiceLocator.getActionRequestService().findPendingByDocRequestCdNodeName(documentId, KEWConstants.ACTION_REQUEST_APPROVE_REQ, nodeName);
        if (requests == null || requests.isEmpty()) {
            return false;
        }
        
        // Deep-copy the action requests for the simulation.
        for (int i = requests.size() - 1; i >= 0; i--) {
        	ActionRequestValue actionRequest = (ActionRequestValue) ObjectUtils.deepCopy((ActionRequestValue)requests.get(i));
        	// Deep-copy the action items as well, since they are indirectly retrieved from the action request via service calls.
        	for (ActionItem actionItem : actionRequest.getActionItems()) {
        		actionRequest.getSimulatedActionItems().add((ActionItem) ObjectUtils.deepCopy(actionItem));
        	}
        	requests.set(i, actionRequest);
        }
        
        ActivationContext activationContext = new ActivationContext(ActivationContext.CONTEXT_IS_SIMULATION);
        for (Iterator iterator = requests.iterator(); iterator.hasNext();) {
            ActionRequestValue request = (ActionRequestValue) iterator.next();
            if (activateFirst && !request.isActive()) {
                KEWServiceLocator.getActionRequestService().activateRequest(request, activationContext);
            }
            if (request.isUserRequest() && request.getPrincipalId().equals(principalId)) {
                KEWServiceLocator.getActionRequestService().deactivateRequest(null, request, activationContext);
            } else if (request.isGroupRequest() && KimApiServiceLocator.getGroupService().isMemberOfGroup(principalId, request.getGroup().getId())) {
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
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Evaluating routeLevelHasApproverActionRequest [docTypeName=" + documentTypeName + ", routeLevel=" + routeLevel + "]");
        }
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
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Evaluating routeNodeHasApproverActionRequest [docTypeName=" + documentTypeName + ", nodeName=" + nodeName + "]");
        }
        DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
        RouteNode routeNode = KEWServiceLocator.getRouteNodeService().findRouteNodeByName(documentType.getDocumentTypeId(), nodeName);
        return routeNodeHasApproverActionRequest(documentType, docContent, routeNode, new Integer(KEWConstants.INVALID_ROUTE_LEVEL));
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
        routeHeader.setDocumentId("");
        routeHeader.setDocumentTypeId(documentType.getDocumentTypeId());
        routeHeader.setDocRouteLevel(routeLevel);
        routeHeader.setDocVersion(new Integer(KewApiConstants.DocumentContentVersions.CURRENT));

        if (node.getRuleTemplate() != null && node.isFlexRM()) {
            String ruleTemplateName = node.getRuleTemplate().getName();
            routeHeader.setDocContent(docContent);
            routeHeader.setDocRouteStatus(KEWConstants.ROUTE_HEADER_INITIATED_CD);
            FlexRM flexRM = new FlexRM();
    		RouteContext context = RouteContext.getCurrentRouteContext();
    		context.setDocument(routeHeader);
    		try {
    			List actionRequests = flexRM.getActionRequests(routeHeader, node, null, ruleTemplateName);
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

    public void reResolveRoleByDocTypeName(String documentTypeName, String roleName, String qualifiedRoleNameLabel) throws WorkflowException {
        incomingParamCheck(documentTypeName, "documentTypeName");
        incomingParamCheck(roleName, "roleName");
        incomingParamCheck(qualifiedRoleNameLabel, "qualifiedRoleNameLabel");
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Re-resolving Role [docTypeName=" + documentTypeName + ", roleName=" + roleName + ", qualifiedRoleNameLabel=" + qualifiedRoleNameLabel + "]");
        }
    	DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
    	if (org.apache.commons.lang.StringUtils.isEmpty(qualifiedRoleNameLabel)) {
    		KEWServiceLocator.getRoleService().reResolveRole(documentType, roleName);
    	} else {
    		KEWServiceLocator.getRoleService().reResolveQualifiedRole(documentType, roleName, qualifiedRoleNameLabel);
    	}
    }

    public void reResolveRoleByDocumentId(String documentId, String roleName, String qualifiedRoleNameLabel) throws WorkflowException {
        incomingParamCheck(documentId, "documentId");
        incomingParamCheck(roleName, "roleName");
        incomingParamCheck(qualifiedRoleNameLabel, "qualifiedRoleNameLabel");
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Re-resolving Role [documentId=" + documentId + ", roleName=" + roleName + ", qualifiedRoleNameLabel=" + qualifiedRoleNameLabel + "]");
        }
        DocumentRouteHeaderValue routeHeader = loadDocument(documentId);
    	if (org.apache.commons.lang.StringUtils.isEmpty(qualifiedRoleNameLabel)) {
    		KEWServiceLocator.getRoleService().reResolveRole(routeHeader, roleName);
    	} else {
    		KEWServiceLocator.getRoleService().reResolveQualifiedRole(routeHeader, roleName, qualifiedRoleNameLabel);
    	}
    }

    public DocumentDetailDTO routingReport(ReportCriteriaDTO reportCriteria) throws WorkflowException {
        incomingParamCheck(reportCriteria, "reportCriteria");
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Executing routing report [docId=" + reportCriteria.getDocumentId() + ", docTypeName=" + reportCriteria.getDocumentTypeName() + "]");
        }
        SimulationCriteria criteria = DTOConverter.convertReportCriteriaDTO(reportCriteria);
        return DTOConverter.convertDocumentDetail(KEWServiceLocator.getRoutingReportService().report(criteria));
    }

    public boolean isFinalApprover(String documentId, String principalId) throws WorkflowException {
        incomingParamCheck(documentId, "documentId");
        incomingParamCheck(principalId, "principalId");
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Evaluating isFinalApprover [docId=" + documentId + ", principalId=" + principalId + "]");
        }
        DocumentRouteHeaderValue routeHeader = loadDocument(documentId);
        List requests = KEWServiceLocator.getActionRequestService().findPendingByDoc(documentId);
        List finalApproverNodes = KEWServiceLocator.getRouteNodeService().findFinalApprovalRouteNodes(routeHeader.getDocumentType().getDocumentTypeId());
        if (finalApproverNodes.isEmpty()) {
        	if ( LOG.isDebugEnabled() ) {
        		LOG.debug("Could not locate final approval nodes for document " + documentId);
        	}
            return false;
        }
        Set finalApproverNodeNames = new HashSet();
        for (Iterator iterator = finalApproverNodes.iterator(); iterator.hasNext();) {
            RouteNode node = (RouteNode) iterator.next();
            finalApproverNodeNames.add(node.getRouteNodeName());
        }

        int approveRequest = 0;
        for (Iterator iter = requests.iterator(); iter.hasNext();) {
            ActionRequestValue request = (ActionRequestValue) iter.next();
            RouteNodeInstance nodeInstance = request.getNodeInstance();
            if (nodeInstance == null) {
            	if ( LOG.isDebugEnabled() ) {
            		LOG.debug("Found an action request on the document with a null node instance, indicating EXCEPTION routing.");
            	}
                return false;
            }
            if (finalApproverNodeNames.contains(nodeInstance.getRouteNode().getRouteNodeName())) {
                if (request.isApproveOrCompleteRequest()) {
                    approveRequest++;
                    if ( LOG.isDebugEnabled() ) {
                    	LOG.debug("Found request is approver " + request.getActionRequestId());
                    }
                    if (! request.isRecipientRoutedRequest(principalId)) {
                    	if ( LOG.isDebugEnabled() ) {
                    		LOG.debug("Action Request not for user " + principalId);
                    	}
                        return false;
                    }
                }
            }
        }

        if (approveRequest == 0) {
            return false;
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Principal "+principalId+" is final approver for document " + documentId);
        }
        return true;
    }

    public boolean isSuperUserForDocumentType(String principalId, String documentTypeId) throws WorkflowException {
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug("Determining super user status [principalId=" + principalId + ", documentTypeId=" + documentTypeId + "]");
    	}
    	DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findById(documentTypeId);
    	boolean isSuperUser = KEWServiceLocator.getDocumentTypePermissionService().canAdministerRouting(principalId, documentType);
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug("Super user status is " + isSuperUser + ".");
    	}
    	return isSuperUser;
    }

    private DocumentRouteHeaderValue loadDocument(String documentId) {
        return KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
    }

    public DocumentContentDTO getDocumentContent(String documentId) throws WorkflowException {
    	if ( LOG.isDebugEnabled() ) {
    		LOG.debug("Fetching document content [docId=" + documentId + "]");
    	}
    	DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId);
    	return DTOConverter.convertDocumentContent(document.getDocContent(), documentId);
    }

	public String[] getPreviousRouteNodeNames(String documentId) throws WorkflowException {
		if ( LOG.isDebugEnabled() ) {
			LOG.debug("Fetching previous node names [docId=" + documentId + "]");
		}
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

    public RuleDTO[] ruleReport(RuleReportCriteriaDTO ruleReportCriteria) throws WorkflowException {
        incomingParamCheck(ruleReportCriteria, "ruleReportCriteria");
        if (ruleReportCriteria == null) {
            throw new IllegalArgumentException("At least one criterion must be sent in a RuleReportCriteriaDTO object");
        }
        if ( LOG.isDebugEnabled() ) {
        	LOG.debug("Executing rule report [responsibleUser=" + ruleReportCriteria.getResponsiblePrincipalId() + ", responsibleWorkgroup=" +
                    ruleReportCriteria.getResponsibleGroupId() + "]");
        }
        Map extensionValues = new HashMap();
        if (ruleReportCriteria.getRuleExtensionVOs() != null) {
            for (int i = 0; i < ruleReportCriteria.getRuleExtensionVOs().length; i++) {
                RuleExtensionDTO ruleExtensionVO = ruleReportCriteria.getRuleExtensionVOs()[i];
                KeyValue ruleExtension = DTOConverter.convertRuleExtensionVO(ruleExtensionVO);
                extensionValues.put(ruleExtension.getKey(), ruleExtension.getValue());
            }
        }
        Collection<String> actionRequestCodes = new ArrayList<String>();
        if ( (ruleReportCriteria.getActionRequestCodes() != null) && (ruleReportCriteria.getActionRequestCodes().length != 0) ) {
            actionRequestCodes = Arrays.asList(ruleReportCriteria.getActionRequestCodes());
        }
        Collection rulesFound = KEWServiceLocator.getRuleService().search(ruleReportCriteria.getDocumentTypeName(),ruleReportCriteria.getRuleTemplateName(),
                ruleReportCriteria.getRuleDescription(), ruleReportCriteria.getResponsibleGroupId(),
                ruleReportCriteria.getResponsiblePrincipalId(),
                ruleReportCriteria.isConsiderWorkgroupMembership(),ruleReportCriteria.isIncludeDelegations(),
                ruleReportCriteria.isActiveIndicator(),extensionValues,actionRequestCodes);
        RuleDTO[] returnableRules = new RuleDTO[rulesFound.size()];
        int i = 0;
        for (Iterator iter = rulesFound.iterator(); iter.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) iter.next();
            returnableRules[i] = DTOConverter.convertRule(rule);
            i++;
        }
        return returnableRules;
    }

    public DocumentSearchResultDTO performDocumentSearch(DocumentSearchCriteriaDTO criteriaVO) throws WorkflowException {
        return performDocumentSearchWithPrincipal(null, criteriaVO);
    }

    public DocumentSearchResultDTO performDocumentSearchWithPrincipal(String principalId, DocumentSearchCriteriaDTO criteriaVO) throws WorkflowException {
        DocSearchCriteriaDTO criteria = DTOConverter.convertDocumentSearchCriteriaDTO(criteriaVO);
        criteria.setOverridingUserSession(true);
        if (principalId != null) {
        	KEWServiceLocator.getIdentityHelperService().validatePrincipalId(principalId);
        } else {
        	// if the principal is null then we need to use the system "kr" user for execution of the search
        	principalId = KEWServiceLocator.getIdentityHelperService().getSystemPrincipal().getPrincipalId();
        }
        DocumentSearchResultComponents components = KEWServiceLocator.getDocumentSearchService().getListRestrictedByCriteria(principalId, criteria);
        DocumentSearchResultDTO resultVO = DTOConverter.convertDocumentSearchResultComponents(components);
        resultVO.setOverThreshold(criteria.isOverThreshold());
        resultVO.setSecurityFilteredRows(Integer.valueOf(criteria.getSecurityFilteredRows()));
        return resultVO;
    }

    /**
     * @see org.kuali.rice.kew.service.WorkflowUtility#getDocumentInitiatorPrincipalId(java.lang.Long)
     */
    public String getDocumentInitiatorPrincipalId(String documentId)
    		throws WorkflowException {
        if (documentId == null) {
            LOG.error("null documentId passed in.");
            throw new RuntimeException("null documentId passed in.");
        }

        DocumentRouteHeaderValue header = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId, false);
        if ( header == null) {
        	return null;
        }
    	return header.getInitiatorWorkflowId();
    }
    /**
     * @see org.kuali.rice.kew.service.WorkflowUtility#getDocumentRoutedByPrincipalId(java.lang.Long)
     */
    public String getDocumentRoutedByPrincipalId(String documentId)
    		throws WorkflowException {
        if (documentId == null) {
            LOG.error("null documentId passed in.");
            throw new RuntimeException("null documentId passed in.");
        }

        DocumentRouteHeaderValue header = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId, false);
        if ( header == null) {
        	return null;
        }
    	return header.getRoutedByUserWorkflowId();
    }

    /**
	 *
	 * @see org.kuali.rice.kew.service.WorkflowUtility#getSearchableAttributeDateTimeValuesByKey(java.lang.Long, java.lang.String)
	 */
	public Timestamp[] getSearchableAttributeDateTimeValuesByKey(
			String documentId, String key) {
		List<Timestamp> results = KEWServiceLocator.getRouteHeaderService().getSearchableAttributeDateTimeValuesByKey(documentId, key);
		if (ObjectUtils.isNull(results)) {
			return null;
		}
		return results.toArray(new Timestamp[]{});
	}

	/**
	 *
	 * @see org.kuali.rice.kew.service.WorkflowUtility#getSearchableAttributeFloatValuesByKey(java.lang.Long, java.lang.String)
	 */
	public BigDecimal[] getSearchableAttributeFloatValuesByKey(String documentId, String key) {
		List<BigDecimal> results = KEWServiceLocator.getRouteHeaderService().getSearchableAttributeFloatValuesByKey(documentId, key);
		if (ObjectUtils.isNull(results)) {
			return null;
		}
		return results.toArray(new BigDecimal[]{});
	}

	/**
	 *
	 * @see org.kuali.rice.kew.service.WorkflowUtility#getSearchableAttributeLongValuesByKey(java.lang.Long, java.lang.String)
	 */
	public Long[] getSearchableAttributeLongValuesByKey(String documentId, String key) {
		List<Long> results = KEWServiceLocator.getRouteHeaderService().getSearchableAttributeLongValuesByKey(documentId, key);
		if (ObjectUtils.isNull(results)) {
			return null;
		}
		return results.toArray(new Long[]{});
	}

	/**
	 *
	 * @see org.kuali.rice.kew.service.WorkflowUtility#getSearchableAttributeStringValuesByKey(java.lang.Long, java.lang.String)
	 */
	public String[] getSearchableAttributeStringValuesByKey(String documentId, String key) {
		List<String> results = KEWServiceLocator.getRouteHeaderService().getSearchableAttributeStringValuesByKey(documentId, key);
		if (ObjectUtils.isNull(results)) {
			return null;
		}
		return results.toArray(new String[]{});
	}

    public String getFutureRequestsKey(String principalId) {
        return KEWConstants.RECEIVE_FUTURE_REQUESTS_BRANCH_STATE_KEY + "," + principalId + "," + new Date().toString() + ", " + Math.random();
    }

    public String getReceiveFutureRequestsValue() {
        return KEWConstants.RECEIVE_FUTURE_REQUESTS_BRANCH_STATE_VALUE;
    }

    public String getDoNotReceiveFutureRequestsValue() {
        return KEWConstants.DONT_RECEIVE_FUTURE_REQUESTS_BRANCH_STATE_VALUE;
    }

    public String getClearFutureRequestsValue() {
        return KEWConstants.CLEAR_FUTURE_REQUESTS_BRANCH_STATE_VALUE;
    }
    
    public boolean hasRouteNode(String documentTypeName, String routeNodeName) throws WorkflowException {
        if (documentTypeName == null) {
            LOG.error("null documentTypeName passed in.");
            throw new RuntimeException("null documentTypeName passed in");
        }
        if (routeNodeName == null) {
            LOG.error("null routeNodeName passed in.");
            throw new RuntimeException("null routeNodeName passed in");
        }
    	DocumentTypeDTO docType = getDocumentTypeByName(documentTypeName);
    	if(docType==null){
            LOG.error("docType null for the documentTypeName passed in "+documentTypeName);
            throw new RuntimeException("docType null for the documentTypeName passed in "+documentTypeName);
        }
    	RouteNode routeNode = KEWServiceLocator.getRouteNodeService().findRouteNodeByName(docType.getDocTypeId(), routeNodeName);
    	
    	if(routeNode==null){
    		if(docType.getDocTypeParentName() == null)
    			return false;
    		else
    			return hasRouteNode(docType.getDocTypeParentName(), routeNodeName);	
    	}
    	else
    		return true;
    	
    }

    public boolean isCurrentActiveDocumentType(String documentTypeName) throws WorkflowException {
    	DocumentType docType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
    	return docType != null && docType.isActive();
    }
    
	public DocumentDetailDTO getDocumentDetailFromAppId(
			String documentTypeName, String appId) throws WorkflowException {
        if (documentTypeName == null) {
            LOG.error("null documentTypeName passed in.");
            throw new RuntimeException("null documentTypeName passed in");
        }
        if (appId == null) {
            LOG.error("null appId passed in.");
            throw new RuntimeException("null appId passed in");
        }
        
        Collection documentIds = KEWServiceLocator.getRouteHeaderService().findByDocTypeAndAppId(documentTypeName, appId);
        
        if(documentIds==null||documentIds.isEmpty()){
            LOG.error("No RouteHeader Ids found for criteria");
    		throw new WorkflowException("No RouteHeader Ids found for criteria");
        }
        if(documentIds.size()>1){
            LOG.error("More than one RouteHeader Id found for criteria");
    		throw new WorkflowException("More than one RouteHeader Id found for criteria");
		}

        return getDocumentDetail((String)documentIds.iterator().next());
	}
	
	public String getAppDocId(String documentId) {
 	 	return KEWServiceLocator.getRouteHeaderService().getAppDocId(documentId);
 	}
    
    public DocumentStatusTransitionDTO[] getDocumentStatusTransitionHistory(String documentId) throws WorkflowException {
        if (documentId == null) {
            LOG.error("null documentId passed in.");
            throw new RuntimeException("null documentId passed in");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("Fetching document status transition history [id="+documentId+"]");
        }
        DocumentRouteHeaderValue document = loadDocument(documentId);
        
        UserSession userSession = GlobalVariables.getUserSession();
        String principalId = null;
        if (userSession != null) { // get the principalId if we can
        	principalId = userSession.getPrincipalId();
        }
        List<DocumentStatusTransition> list = document.getAppDocStatusHistory();

        DocumentStatusTransitionDTO[] transitionHistory = new DocumentStatusTransitionDTO[list.size()];        
        int i = 0;
        for (Object element : list) {
        	DocumentStatusTransition transition = (DocumentStatusTransition) element;
            transitionHistory[i] = DTOConverter.convertDocumentStatusTransition(transition);
            i++;
        }
        return transitionHistory;
    }

	//for document link

	public void deleteDocumentLink(DocumentLinkDTO docLink) throws WorkflowException {
		KEWServiceLocator.getDocumentLinkService().deleteDocumentLink(initDocLink(docLink));
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.routeheader.service.WorkflowDocumentService#addDocumentLink(org.kuali.rice.kew.documentlink.DocumentLink)
	 */
	public void addDocumentLink(DocumentLinkDTO docLinkVO) throws WorkflowException {
		KEWServiceLocator.getDocumentLinkService().saveDocumentLink(initDocLink(docLinkVO));
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.routeheader.service.WorkflowDocumentService#getgetLinkedDocumentsByDocId(java.lang.Long)
	 */
	public List<DocumentLinkDTO> getLinkedDocumentsByDocId(String documentId) throws WorkflowException {
		return DTOConverter.convertDocumentLinkToArrayList(KEWServiceLocator.getDocumentLinkService().getLinkedDocumentsByDocId(documentId));
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.routeheader.service.WorkflowDocumentService#getDocumentLink(org.kuali.rice.kew.documentlink.DocumentLink)
	 */
	public DocumentLinkDTO getLinkedDocument(DocumentLinkDTO docLinkVO) throws WorkflowException{
		return DTOConverter.convertDocumentLink(KEWServiceLocator.getDocumentLinkService().getLinkedDocument(initDocLink(docLinkVO)));
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kew.routeheader.service.WorkflowDocumentService#deleteDocumentLinkByDocId(java.lang.Long)
	 */
	public void deleteDocumentLinksByDocId(String documentId) throws WorkflowException{
		KEWServiceLocator.getDocumentLinkService().deleteDocumentLinksByDocId(documentId);
	}

	private DocumentLink initDocLink(DocumentLinkDTO docLinkVO){
		DocumentLink docLink = new DocumentLink();
		docLink.setDocLinkId(docLinkVO.getLinbkId());
		docLink.setOrgnDocId(docLinkVO.getOrgnDocId());
		docLink.setDestDocId(docLinkVO.getDestDocId());

		return docLink;
	}
}
