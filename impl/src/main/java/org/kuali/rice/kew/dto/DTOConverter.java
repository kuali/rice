/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.rice.kew.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.reflect.DataDefinition;
import org.kuali.rice.core.reflect.ObjectDefinition;
import org.kuali.rice.core.reflect.PropertyDefinition;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionrequest.ActionRequestFactory;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actions.AdHocRevoke;
import org.kuali.rice.kew.actions.MovePoint;
import org.kuali.rice.kew.actions.ValidActions;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.definition.AttributeDefinition;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaVO;
import org.kuali.rice.kew.docsearch.DocSearchUtils;
import org.kuali.rice.kew.docsearch.DocumentSearchResult;
import org.kuali.rice.kew.docsearch.DocumentSearchResultComponents;
import org.kuali.rice.kew.docsearch.SearchableAttribute;
import org.kuali.rice.kew.docsearch.web.SearchAttributeFormContainer;
import org.kuali.rice.kew.docsearch.xml.GenericXMLSearchableAttribute;
import org.kuali.rice.kew.doctype.DocumentType;
import org.kuali.rice.kew.dto.ActionItemDTO;
import org.kuali.rice.kew.dto.ActionRequestDTO;
import org.kuali.rice.kew.dto.ActionTakenEventDTO;
import org.kuali.rice.kew.dto.ActionTakenDTO;
import org.kuali.rice.kew.dto.AdHocRevokeDTO;
import org.kuali.rice.kew.dto.AfterProcessEventDTO;
import org.kuali.rice.kew.dto.BeforeProcessEventDTO;
import org.kuali.rice.kew.dto.DeleteEventDTO;
import org.kuali.rice.kew.dto.DocumentContentDTO;
import org.kuali.rice.kew.dto.DocumentDetailDTO;
import org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.dto.DocumentSearchCriteriaDTO;
import org.kuali.rice.kew.dto.DocumentSearchResultRowDTO;
import org.kuali.rice.kew.dto.DocumentSearchResultDTO;
import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.dto.EmplIdDTO;
import org.kuali.rice.kew.dto.KeyValueDTO;
import org.kuali.rice.kew.dto.LookupableColumnDTO;
import org.kuali.rice.kew.dto.MovePointDTO;
import org.kuali.rice.kew.dto.NetworkIdDTO;
import org.kuali.rice.kew.dto.NoteDTO;
import org.kuali.rice.kew.dto.ProcessDTO;
import org.kuali.rice.kew.dto.PropertyDefinitionDTO;
import org.kuali.rice.kew.dto.ReportActionToTakeDTO;
import org.kuali.rice.kew.dto.ReportCriteriaDTO;
import org.kuali.rice.kew.dto.ResponsiblePartyDTO;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kew.dto.RouteNodeInstanceDTO;
import org.kuali.rice.kew.dto.RouteNodeDTO;
import org.kuali.rice.kew.dto.RoutePathDTO;
import org.kuali.rice.kew.dto.RouteTemplateEntryDTO;
import org.kuali.rice.kew.dto.RuleDelegationDTO;
import org.kuali.rice.kew.dto.RuleExtensionDTO;
import org.kuali.rice.kew.dto.RuleResponsibilityDTO;
import org.kuali.rice.kew.dto.RuleDTO;
import org.kuali.rice.kew.dto.StateDTO;
import org.kuali.rice.kew.dto.UserIdDTO;
import org.kuali.rice.kew.dto.UserDTO;
import org.kuali.rice.kew.dto.UuIdDTO;
import org.kuali.rice.kew.dto.ValidActionsDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeDefinitionDTO;
import org.kuali.rice.kew.dto.WorkflowAttributeValidationErrorDTO;
import org.kuali.rice.kew.dto.WorkflowGroupIdDTO;
import org.kuali.rice.kew.dto.WorkflowIdDTO;
import org.kuali.rice.kew.dto.WorkgroupIdDTO;
import org.kuali.rice.kew.dto.WorkgroupNameIdDTO;
import org.kuali.rice.kew.dto.WorkgroupDTO;
import org.kuali.rice.kew.engine.CompatUtils;
import org.kuali.rice.kew.engine.node.BranchState;
import org.kuali.rice.kew.engine.node.KeyValuePair;
import org.kuali.rice.kew.engine.node.Process;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.engine.node.State;
import org.kuali.rice.kew.engine.simulation.SimulationActionToTake;
import org.kuali.rice.kew.engine.simulation.SimulationCriteria;
import org.kuali.rice.kew.exception.DocumentTypeNotFoundException;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.lookupable.Column;
import org.kuali.rice.kew.notes.Note;
import org.kuali.rice.kew.notes.service.NoteService;
import org.kuali.rice.kew.postprocessor.ActionTakenEvent;
import org.kuali.rice.kew.postprocessor.AfterProcessEvent;
import org.kuali.rice.kew.postprocessor.BeforeProcessEvent;
import org.kuali.rice.kew.postprocessor.DeleteEvent;
import org.kuali.rice.kew.postprocessor.DocumentRouteLevelChange;
import org.kuali.rice.kew.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.StandardDocumentContent;
import org.kuali.rice.kew.rule.RuleAttribute;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kew.rule.RuleExtension;
import org.kuali.rice.kew.rule.RuleExtensionValue;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.rule.WorkflowAttribute;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kew.rule.WorkflowAttributeXmlValidator;
import org.kuali.rice.kew.rule.xmlrouting.GenericXMLRuleAttribute;
import org.kuali.rice.kew.user.AuthenticationUserId;
import org.kuali.rice.kew.user.EmplId;
import org.kuali.rice.kew.user.Recipient;
import org.kuali.rice.kew.user.RoleRecipient;
import org.kuali.rice.kew.user.UserId;
import org.kuali.rice.kew.user.UuId;
import org.kuali.rice.kew.user.WorkflowUser;
import org.kuali.rice.kew.user.WorkflowUserId;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.ResponsibleParty;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.util.XmlHelper;
import org.kuali.rice.kew.web.KeyValueSort;
import org.kuali.rice.kew.workgroup.GroupId;
import org.kuali.rice.kew.workgroup.GroupNameId;
import org.kuali.rice.kew.workgroup.WorkflowGroupId;
import org.kuali.rice.kew.workgroup.Workgroup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Translates Workflow server side beans into client side VO beans.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DTOConverter {
    private static final Logger LOG = Logger.getLogger(DTOConverter.class);

    public static RouteHeaderDTO convertRouteHeader(DocumentRouteHeaderValue routeHeader, WorkflowUser user) throws WorkflowException, KEWUserNotFoundException {
        RouteHeaderDTO routeHeaderVO = new RouteHeaderDTO();
        if (routeHeader == null) {
            return null;
        }
        populateRouteHeaderVO(routeHeaderVO, routeHeader);

        if (user != null) {
            routeHeaderVO.setUserBlanketApprover(false); // default to false
            if (routeHeader.getDocumentType() != null) {
                routeHeaderVO.setUserBlanketApprover(routeHeader.getDocumentType().isUserBlanketApprover(user));
            }
            String topActionRequested = KEWConstants.ACTION_REQUEST_FYI_REQ;
            for (Iterator iter = routeHeader.getActionRequests().iterator(); iter.hasNext();) {
                ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
                // below will control what buttons are drawn on the client we only want the
                // heaviest action button to show on the client making this code a little combersome
                if (actionRequest.isRecipientRoutedRequest(user) && actionRequest.isActive()) {
                    int actionRequestComparison = ActionRequestValue.compareActionCode(actionRequest.getActionRequested(), topActionRequested);
                    if (actionRequest.isFYIRequest() && actionRequestComparison >= 0) {
                        routeHeaderVO.setFyiRequested(true);
                    } else if (actionRequest.isAcknowledgeRequest() && actionRequestComparison >= 0) {
                        routeHeaderVO.setAckRequested(true);
                        routeHeaderVO.setFyiRequested(false);
                        topActionRequested = actionRequest.getActionRequested();
                    } else if (actionRequest.isApproveRequest() && actionRequestComparison >= 0) {
                        routeHeaderVO.setApproveRequested(true);
                        routeHeaderVO.setAckRequested(false);
                        routeHeaderVO.setFyiRequested(false);
                        topActionRequested = actionRequest.getActionRequested();
                        if (actionRequest.isCompleteRequst()) {
                            routeHeaderVO.setCompleteRequested(true);
                        }
                    }
                }
            }
            // Update notes and notesToDelete arrays in routeHeaderVO
            routeHeaderVO.setNotesToDelete(null);
            routeHeaderVO.setNotes(convertNotesArrayListToNoteVOArray(routeHeader.getNotes()));
        }

        if (user != null) {
            routeHeaderVO.setValidActions(convertValidActions(KEWServiceLocator.getActionRegistry().getValidActions(user, routeHeader)));
        }
        return routeHeaderVO;
    }

    public static RouteHeaderDTO convertActionListRouteHeader(DocumentRouteHeaderValue routeHeader, WorkflowUser user) throws WorkflowException, KEWUserNotFoundException {
        RouteHeaderDTO routeHeaderVO = new RouteHeaderDTO();
        if (routeHeader == null) {
            return null;
        }
        populateRouteHeaderVO(routeHeaderVO, routeHeader);

        if (user != null) {
            routeHeaderVO.setUserBlanketApprover(false); // default to false
            if (routeHeader.getDocumentType() != null) {
                routeHeaderVO.setUserBlanketApprover(routeHeader.getDocumentType().isUserBlanketApprover(user));
            }
            String topActionRequested = KEWConstants.ACTION_REQUEST_FYI_REQ;
            for (Iterator iter = routeHeader.getActionRequests().iterator(); iter.hasNext();) {
                ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
                // below will control what buttons are drawn on the client we only want the
                // heaviest action button to show on the client making this code a little combersome
                if (actionRequest.isRecipientRoutedRequest(user) && actionRequest.isActive()) {
                    int actionRequestComparison = ActionRequestValue.compareActionCode(actionRequest.getActionRequested(), topActionRequested);
                    if (actionRequest.isFYIRequest() && actionRequestComparison >= 0) {
                        routeHeaderVO.setFyiRequested(true);
                    } else if (actionRequest.isAcknowledgeRequest() && actionRequestComparison >= 0) {
                        routeHeaderVO.setAckRequested(true);
                        routeHeaderVO.setFyiRequested(false);
                        topActionRequested = actionRequest.getActionRequested();
                    } else if (actionRequest.isApproveRequest() && actionRequestComparison >= 0) {
                        routeHeaderVO.setApproveRequested(true);
                        routeHeaderVO.setAckRequested(false);
                        routeHeaderVO.setFyiRequested(false);
                        topActionRequested = actionRequest.getActionRequested();
                        if (actionRequest.isCompleteRequst()) {
                            routeHeaderVO.setCompleteRequested(true);
                        }
                    }
                }
            }
        }

        routeHeaderVO.setValidActions(convertValidActions(KEWServiceLocator.getActionRegistry().getValidActions(user, routeHeader)));
        return routeHeaderVO;
    }

    public static ValidActionsDTO convertValidActions(ValidActions validActions) {
        ValidActionsDTO validActionsVO = new ValidActionsDTO();
        for (Iterator iter = validActions.getActionTakenCodes().iterator(); iter.hasNext();) {
            String actionTakenCode = (String) iter.next();
            validActionsVO.addValidActionsAllowed(actionTakenCode);
        }
        return validActionsVO;
    }

    // private static void populateActionListRouteHeaderVO(RouteHeaderVO routeHeaderVO, DocumentRouteHeaderValue routeHeader)
    // throws WorkflowException {
    // routeHeaderVO.setAppDocId(routeHeader.getAppDocId());
    // routeHeaderVO.setDateApproved(Utilities.convertTimestamp(routeHeader.getApprovedDate()));
    // routeHeaderVO.setDateCreated(Utilities.convertTimestamp(routeHeader.getCreateDate()));
    // routeHeaderVO.setDateFinalized(Utilities.convertTimestamp(routeHeader.getFinalizedDate()));
    // routeHeaderVO.setDateLastModified(Utilities.convertTimestamp(routeHeader.getStatusModDate()));
    // //routeHeaderVO.setDocumentContent(convertDocumentContent(routeHeader.getDocContent()));
    // routeHeaderVO.setDocRouteLevel(routeHeader.getDocRouteLevel());
    //
    // Collection activeNodes =
    // SpringServiceLocator.getRouteNodeService().getActiveNodeInstances(routeHeaderVO.getRouteHeaderId());
    // routeHeaderVO.setNodeNames(new String[activeNodes.size()]);
    // int index = 0;
    // for (Iterator iterator = activeNodes.iterator(); iterator.hasNext(); index++) {
    // RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
    // routeHeaderVO.getNodeNames()[index++] = nodeInstance.getRouteNode().getRouteNodeName();
    // }
    //
    // routeHeaderVO.setDocRouteStatus(routeHeader.getDocRouteStatus());
    // routeHeaderVO.setDocTitle(routeHeader.getDocTitle());
    // if (routeHeader.getDocumentType() != null) {
    // routeHeaderVO.setDocTypeName(routeHeader.getDocumentType().getName());
    // routeHeaderVO.setDocumentUrl(routeHeader.getDocumentType().getDocHandlerUrl());
    // }
    // routeHeaderVO.setDocVersion(routeHeader.getDocVersion());
    // routeHeaderVO.setInitiator(convertUser(routeHeader.getInitiatorUser()));
    // routeHeaderVO.setOverrideInd(routeHeader.getOverrideInd());
    // routeHeaderVO.setRouteHeaderId(routeHeader.getRouteHeaderId());
    // }

    private static void populateRouteHeaderVO(RouteHeaderDTO routeHeaderVO, DocumentRouteHeaderValue routeHeader) throws WorkflowException {
        routeHeaderVO.setRouteHeaderId(routeHeader.getRouteHeaderId());
        routeHeaderVO.setAppDocId(routeHeader.getAppDocId());
        routeHeaderVO.setDateApproved(Utilities.convertTimestamp(routeHeader.getApprovedDate()));
        routeHeaderVO.setDateCreated(Utilities.convertTimestamp(routeHeader.getCreateDate()));
        routeHeaderVO.setDateFinalized(Utilities.convertTimestamp(routeHeader.getFinalizedDate()));
        routeHeaderVO.setDateLastModified(Utilities.convertTimestamp(routeHeader.getStatusModDate()));

        /**
         * This is the original code which set everything up for lazy loading of document content
         */
        // by default, a non-initialized document content object will be sent so that it can be fetched lazily
        // DocumentContentVO documentContentVO = new DocumentContentVO();
        // documentContentVO.setRouteHeaderId(routeHeader.getRouteHeaderId());
        // routeHeaderVO.setDocumentContent(documentContentVO);
        /**
         * Since we removed the lazy loading in the 2.3 release, this is the code which bypasses lazy loading
         */
        // routeHeaderVO.setDocumentContent(convertDocumentContent(routeHeader.getDocContent(),
        // routeHeader.getRouteHeaderId()));
        routeHeaderVO.setDocRouteLevel(routeHeader.getDocRouteLevel());
        routeHeaderVO.setCurrentRouteNodeNames(routeHeader.getCurrentRouteLevelName());

        /*
         * Collection activeNodes =
         * SpringServiceLocator.getRouteNodeService().getActiveNodeInstances(routeHeaderVO.getRouteHeaderId());
         * routeHeaderVO.setNodeNames(new String[activeNodes.size()]); int index = 0; for (Iterator iterator =
         * activeNodes.iterator(); iterator.hasNext();) { RouteNodeInstance nodeInstance = (RouteNodeInstance)
         * iterator.next(); routeHeaderVO.getNodeNames()[index++] = nodeInstance.getRouteNode().getRouteNodeName(); }
         */

        routeHeaderVO.setDocRouteStatus(routeHeader.getDocRouteStatus());
        routeHeaderVO.setDocTitle(routeHeader.getDocTitle());
        if (routeHeader.getDocumentType() != null) {
            routeHeaderVO.setDocTypeName(routeHeader.getDocumentType().getName());
            routeHeaderVO.setDocumentUrl(routeHeader.getDocumentType().getDocHandlerUrl());
            routeHeaderVO.setDocTypeId(routeHeader.getDocumentTypeId());
        }
        routeHeaderVO.setDocVersion(routeHeader.getDocVersion());
        routeHeaderVO.setInitiator(convertUser(routeHeader.getInitiatorUser()));
        routeHeaderVO.setRoutedByUser(convertUser(routeHeader.getRoutedByUser()));
   //     routeHeaderVO.setOverrideInd(routeHeader.getOverrideInd());

        /* populate the routeHeaderVO with the document variables */
        // FIXME: we assume there is only one for now
        RouteNodeInstance routeNodeInstance = (RouteNodeInstance) routeHeader.getInitialRouteNodeInstance(0);
        // Ok, we are using the "branch state" as the arbitrary convenient repository for flow/process/edoc variables
        // so we need to stuff them into the VO
        if (routeNodeInstance.getBranch() != null) {
            List listOfBranchStates = routeNodeInstance.getBranch().getBranchState();
            Iterator it = listOfBranchStates.iterator();
            while (it.hasNext()) {
                BranchState bs = (BranchState) it.next();
                if (bs.getKey() != null && bs.getKey().startsWith(BranchState.VARIABLE_PREFIX)) {
                    LOG.debug("Setting branch state variable on vo: " + bs.getKey() + "=" + bs.getValue());
                    routeHeaderVO.setVariable(bs.getKey().substring(BranchState.VARIABLE_PREFIX.length()), bs.getValue());
                }
            }
        }
    }

    public static DocumentRouteHeaderValue convertRouteHeaderVO(RouteHeaderDTO routeHeaderVO) throws WorkflowException, KEWUserNotFoundException {
        DocumentRouteHeaderValue routeHeader = new DocumentRouteHeaderValue();
        routeHeader.setAppDocId(routeHeaderVO.getAppDocId());
        routeHeader.setApprovedDate(Utilities.convertCalendar(routeHeaderVO.getDateApproved()));
        routeHeader.setCreateDate(Utilities.convertCalendar(routeHeaderVO.getDateCreated()));
        // String updatedDocumentContent = buildUpdatedDocumentContent(routeHeaderVO);
        // if null is returned from this method it indicates that the document content on the route header
        // contained no changes, since we are creating a new document here, we will default the
        // document content approriately if no changes are detected on the incoming DocumentContentVO
        // if (updatedDocumentContent != null) {
        // routeHeader.setDocContent(updatedDocumentContent);
        // } else {
        // routeHeader.setDocContent(KEWConstants.DEFAULT_DOCUMENT_CONTENT);
        // }
        if (StringUtils.isEmpty(routeHeader.getDocContent())) {
            routeHeader.setDocContent(KEWConstants.DEFAULT_DOCUMENT_CONTENT);
        }
        routeHeader.setDocRouteLevel(routeHeaderVO.getDocRouteLevel());
        routeHeader.setDocRouteStatus(routeHeaderVO.getDocRouteStatus());
        routeHeader.setDocTitle(routeHeaderVO.getDocTitle());
        if (routeHeaderVO.getDocTypeName() != null) {
            DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(routeHeaderVO.getDocTypeName());
            if (documentType == null) {
                throw new DocumentTypeNotFoundException("Could not locate the given document type name: " + routeHeaderVO.getDocTypeName());
            }
            routeHeader.setDocumentTypeId(documentType.getDocumentTypeId());
        }
        routeHeader.setDocVersion(routeHeaderVO.getDocVersion());
        routeHeader.setFinalizedDate(Utilities.convertCalendar(routeHeaderVO.getDateFinalized()));
        if (routeHeaderVO.getInitiator() != null) {
            routeHeader.setInitiatorWorkflowId(routeHeaderVO.getInitiator().getWorkflowId());
        }
        if (routeHeaderVO.getRoutedByUser() != null) {
            routeHeader.setRoutedByUserWorkflowId(routeHeaderVO.getRoutedByUser().getWorkflowId());
        }
     //   routeHeader.setOverrideInd(routeHeaderVO.getOverrideInd());
        routeHeader.setRouteHeaderId(routeHeaderVO.getRouteHeaderId());
        routeHeader.setStatusModDate(Utilities.convertCalendar(routeHeaderVO.getDateLastModified()));

        return routeHeader;
    }
    
    public static ActionItemDTO convertActionItem(ActionItem actionItem) throws KEWUserNotFoundException {
        ActionItemDTO actionItemVO = new ActionItemDTO();
        actionItemVO.setActionItemId(actionItem.getActionItemId());
        actionItemVO.setActionItemIndex(actionItem.getActionItemIndex());
        actionItemVO.setActionRequestCd(actionItem.getActionRequestCd());
        actionItemVO.setActionRequestId(actionItem.getActionRequestId());
        actionItemVO.setActionToTake(actionItem.getActionToTake());
        actionItemVO.setDateAssigned(actionItem.getDateAssigned());
        actionItemVO.setDateAssignedString(actionItem.getDateAssignedString());
        actionItemVO.setDelegationType(actionItem.getDelegationType());
        actionItemVO.setDelegatorWorkflowId(actionItem.getDelegatorWorkflowId());
        if (StringUtils.isNotEmpty(actionItem.getDelegatorWorkflowId())) {
            actionItemVO.setDelegatorUser(convertUser(actionItem.getDelegatorUser()));
        }
        actionItemVO.setDelegatorWorkgroupId(actionItem.getDelegatorWorkgroupId());
        if (actionItem.getDelegatorWorkgroupId() != null) {
            actionItemVO.setDelegatorWorkgroup(convertWorkgroup(actionItem.getDelegatorWorkgroup()));
        }
        actionItemVO.setDocHandlerURL(actionItem.getDocHandlerURL());
        actionItemVO.setDocLabel(actionItem.getDocLabel());
        actionItemVO.setDocName(actionItem.getDocName());
        actionItemVO.setDocTitle(actionItem.getDocTitle());
        actionItemVO.setResponsibilityId(actionItem.getResponsibilityId());
        actionItemVO.setRoleName(actionItem.getRoleName());
        actionItemVO.setRouteHeaderId(actionItem.getRouteHeaderId());
        actionItemVO.setWorkflowId(actionItem.getWorkflowId());
        if (StringUtils.isNotEmpty(actionItem.getWorkflowId())) {
            actionItemVO.setUser(convertUser(actionItem.getUser()));
        }
        actionItemVO.setWorkgroupId(actionItem.getWorkgroupId());
        if (actionItem.getWorkgroupId() != null) {
            actionItemVO.setWorkgroup(convertWorkgroup(actionItem.getWorkgroup()));
        }
        return actionItemVO;
    }

    /**
     * Converts the given DocumentContentVO to a document content string. This method considers existing content on the
     * document and updates approriately. The string returned will be the new document content for the document. If null is
     * returned, then the document content is unchanged.
     */
    public static String buildUpdatedDocumentContent(DocumentContentDTO documentContentVO) throws WorkflowException {
        DocumentType documentType = null;
        String documentContent = KEWConstants.DEFAULT_DOCUMENT_CONTENT;
        try {
            // parse the existing content on the document
            String existingDocContent = KEWConstants.DEFAULT_DOCUMENT_CONTENT;
            if (documentContentVO.getRouteHeaderId() != null) {
                DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentContentVO.getRouteHeaderId());
                documentType = document.getDocumentType();
                existingDocContent = document.getDocContent();
            }
            StandardDocumentContent standardDocContent = new StandardDocumentContent(existingDocContent);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.newDocument();
            Element root = document.createElement(KEWConstants.DOCUMENT_CONTENT_ELEMENT);
            document.appendChild(root);
            Element applicationContentElement = standardDocContent.getApplicationContent();
            if (documentContentVO.getApplicationContent() != null) {
                // application content has changed
                if (!Utilities.isEmpty(documentContentVO.getApplicationContent())) {
                    applicationContentElement = document.createElement(KEWConstants.APPLICATION_CONTENT_ELEMENT);
                    XmlHelper.appendXml(applicationContentElement, documentContentVO.getApplicationContent());
                } else {
                    // they've cleared the application content
                    applicationContentElement = null;
                }
            }
            Element attributeContentElement = createDocumentContentSection(document, standardDocContent.getAttributeContent(), documentContentVO.getAttributeDefinitions(), documentContentVO.getAttributeContent(), KEWConstants.ATTRIBUTE_CONTENT_ELEMENT, documentType);
            Element searchableContentElement = createDocumentContentSection(document, standardDocContent.getSearchableContent(), documentContentVO.getSearchableDefinitions(), documentContentVO.getSearchableContent(), KEWConstants.SEARCHABLE_CONTENT_ELEMENT, documentType);
            if (applicationContentElement != null) {
                root.appendChild(applicationContentElement);
            }
            if (attributeContentElement != null) {
                root.appendChild(attributeContentElement);
            }
            if (searchableContentElement != null) {
                root.appendChild(searchableContentElement);
            }
            documentContent = XmlHelper.writeNode(document);
        } catch (Exception e) {
            handleException("Error parsing document content.", e);
        }
        return documentContent;
    }

    private static Element createDocumentContentSection(Document document, Element existingAttributeElement, WorkflowAttributeDefinitionDTO[] definitions, String content, String elementName, DocumentType documentType) throws Exception {
        Element contentSectionElement = existingAttributeElement;
        // if they've updated the content, we're going to re-build the content section element from scratch
        if (content != null) {
            if (!Utilities.isEmpty(content)) {
                contentSectionElement = document.createElement(elementName);
                // if they didn't merely clear the content, let's build the content section element by combining the children
                // of the incoming XML content
                Element incomingAttributeElement = XmlHelper.readXml(content).getDocumentElement();
                NodeList children = incomingAttributeElement.getChildNodes();
                for (int index = 0; index < children.getLength(); index++) {
                    contentSectionElement.appendChild(document.importNode(children.item(index), true));
                }
            } else {
                contentSectionElement = null;
            }
        }
        // if they have new definitions we're going to append those to the existing content section
        if (!Utilities.isEmpty(definitions)) {
            String errorMessage = "";
            boolean inError = false;
            if (contentSectionElement == null) {
                contentSectionElement = document.createElement(elementName);
            }
            for (int index = 0; index < definitions.length; index++) {
                WorkflowAttributeDefinitionDTO definitionVO = definitions[index];
                AttributeDefinition definition = convertWorkflowAttributeDefinitionVO(definitionVO, documentType);
                RuleAttribute ruleAttribute = definition.getRuleAttribute();
                Object attribute = GlobalResourceLoader.getResourceLoader().getObject(definition.getObjectDefinition());
                boolean propertiesAsMap = false;
                if (KEWConstants.RULE_XML_ATTRIBUTE_TYPE.equals(ruleAttribute.getType())) {
                    ((GenericXMLRuleAttribute) attribute).setRuleAttribute(ruleAttribute);
                    propertiesAsMap = true;
                } else if (KEWConstants.SEARCHABLE_XML_ATTRIBUTE_TYPE.equals(ruleAttribute.getType())) {
                    ((GenericXMLSearchableAttribute) attribute).setRuleAttribute(ruleAttribute);
                    propertiesAsMap = true;
                }
                if (propertiesAsMap) {
                    for (PropertyDefinitionDTO propertyDefinitionVO : definitionVO.getProperties()) {
                        if (attribute instanceof GenericXMLRuleAttribute) {
                            ((GenericXMLRuleAttribute) attribute).getParamMap().put(propertyDefinitionVO.getName(), propertyDefinitionVO.getValue());
                        } else if (attribute instanceof GenericXMLSearchableAttribute) {
                            ((GenericXMLSearchableAttribute) attribute).getParamMap().put(propertyDefinitionVO.getName(), propertyDefinitionVO.getValue());
                        }
                    }
                }

                // validate inputs from client application if the attribute is capable
                if (attribute instanceof WorkflowAttributeXmlValidator) {
                    List errors = ((WorkflowAttributeXmlValidator) attribute).validateClientRoutingData();
                    if (!errors.isEmpty()) {
                        inError = true;
                        errorMessage += "Error validating attribute " + definitions[index].getAttributeName() + " ";
                        for (Iterator iter = errors.iterator(); iter.hasNext();) {
                            WorkflowAttributeValidationError error = (WorkflowAttributeValidationError) iter.next();
                            errorMessage += error.getMessage() + " ";
                        }
                    }
                }
                // dont add to xml if attribute is in error
                if (!inError) {
                    if (attribute instanceof WorkflowAttribute) {
                        String attributeDocContent = ((WorkflowAttribute) attribute).getDocContent();
                        if (!StringUtils.isEmpty(attributeDocContent)) {
                            XmlHelper.appendXml(contentSectionElement, attributeDocContent);
                        }
                    } else if (attribute instanceof SearchableAttribute) {
                        String searcheAttributeContent = ((SearchableAttribute) attribute).getSearchContent();
                        if (!StringUtils.isEmpty(searcheAttributeContent)) {
                            XmlHelper.appendXml(contentSectionElement, searcheAttributeContent);
                        }
                    }
                }
            }
            if (inError) {
                throw new WorkflowRuntimeException(errorMessage);
            }

        }
        if (contentSectionElement != null) {
            // always be sure and import the element into the new document, if it originated from the existing doc content
            // and
            // appended to it, it will need to be imported
            contentSectionElement = (Element) document.importNode(contentSectionElement, true);
        }
        return contentSectionElement;
    }

    public static DocumentContentDTO convertDocumentContent(String documentContentValue, Long documentId) throws WorkflowException {
        if (documentContentValue == null) {
            return null;
        }
        DocumentContentDTO documentContentVO = new DocumentContentDTO();
        // initialize the content fields
        documentContentVO.setApplicationContent("");
        documentContentVO.setAttributeContent("");
        documentContentVO.setSearchableContent("");
        documentContentVO.setRouteHeaderId(documentId);
        try {
            DocumentContent documentContent = new StandardDocumentContent(documentContentValue);
            if (documentContent.getApplicationContent() != null) {
                documentContentVO.setApplicationContent(XmlHelper.writeNode(documentContent.getApplicationContent()));
            }
            if (documentContent.getAttributeContent() != null) {
                documentContentVO.setAttributeContent(XmlHelper.writeNode(documentContent.getAttributeContent()));
            }
            if (documentContent.getSearchableContent() != null) {
                documentContentVO.setSearchableContent(XmlHelper.writeNode(documentContent.getSearchableContent()));
            }
        } catch (Exception e) {
            handleException("Error parsing document content.", e);
        }
        return documentContentVO;
    }

    public static WorkgroupDTO convertWorkgroup(Workgroup workgroup) {
        if (workgroup == null) {
            return null;
        }
        WorkgroupDTO workgroupVO = new WorkgroupDTO();
        workgroupVO.setActiveInd(workgroup.getActiveInd().booleanValue());
        workgroupVO.setDescription(workgroup.getDescription());
        workgroupVO.setWorkgroupId(workgroup.getWorkflowGroupId().getGroupId());
        workgroupVO.setWorkgroupName(workgroup.getGroupNameId().getNameId());
        workgroupVO.setWorkgroupType(workgroup.getWorkgroupType());
        if (workgroup.getUsers() != null) {
            workgroupVO.setMembers(new UserDTO[workgroup.getUsers().size()]);
            int index = 0;
            for (Iterator iterator = workgroup.getUsers().iterator(); iterator.hasNext(); index++) {
                WorkflowUser user = (WorkflowUser) iterator.next();
                workgroupVO.getMembers()[index] = convertUser(user);
            }
        }
        return workgroupVO;
    }

    public static UserDTO convertUser(WorkflowUser user) {
        if (user == null) {
            return null;
        }
        UserDTO userVO = new UserDTO();
        userVO.setNetworkId(user.getAuthenticationUserId() == null ? null : user.getAuthenticationUserId().getAuthenticationId());
        userVO.setUuId(user.getUuId() == null ? null : user.getUuId().getUuId());
        userVO.setEmplId(user.getEmplId() == null ? null : user.getEmplId().getEmplId());
        userVO.setWorkflowId(user.getWorkflowUserId() == null ? null : user.getWorkflowUserId().getWorkflowId());
        userVO.setDisplayName(user.getDisplayName());
        userVO.setLastName(user.getLastName());
        userVO.setFirstName(user.getGivenName());
        userVO.setEmailAddress(user.getEmailAddress());
        // Preferences preferences = SpringServiceLocator.getPreferencesService().getPreferences(user);
        // userVO.setUserPreferencePopDocHandler(KEWConstants.PREFERENCES_YES_VAL.equals(preferences.getOpenNewWindow()));

        userVO.setUserPreferencePopDocHandler(true);
        return userVO;
    }

    public static WorkflowUser convertUserVO(UserDTO userVO) throws KEWUserNotFoundException {
        if (userVO == null) {
            return null;
        }
        UserId userId = null;
        if (userVO.getWorkflowId() != null) {
            userId = new WorkflowUserId(userVO.getWorkflowId());
        } else if (userVO.getNetworkId() != null) {
            userId = new AuthenticationUserId(userVO.getNetworkId());
        } else if (userVO.getEmplId() != null) {
            userId = new EmplId(userVO.getEmplId());
        } else if (userVO.getUuId() != null) {
            userId = new UuId(userVO.getUuId());
        } else {
            throw new KEWUserNotFoundException("Cannot convert the given UserVO, it does not contain any valid user ids.");
        }
        return KEWServiceLocator.getUserService().getWorkflowUser(userId);
    }

    public static DocumentTypeDTO convertDocumentType(DocumentType docType) {
        DocumentTypeDTO docTypeVO = new DocumentTypeDTO();
        docTypeVO.setDocTypeParentId(docType.getDocTypeParentId());
        if (docType.getParentDocType() != null) {
            docTypeVO.setDocTypeParentName(docType.getParentDocType().getName());
        }

        docTypeVO.setDocTypeDescription(docType.getDescription());
        docTypeVO.setDocTypeHandlerUrl(docType.getDocHandlerUrl());
        docTypeVO.setDocTypeId(docType.getDocumentTypeId());
        docTypeVO.setDocTypeLabel(docType.getLabel());
        docTypeVO.setName(docType.getName());
        docTypeVO.setDocTypeVersion(docType.getVersion());
        Boolean currentInd = docType.getCurrentInd();
        if (currentInd == null) {
            docTypeVO.setDocTypeCurrentInd(null);
        } else if (currentInd.booleanValue()) {
            docTypeVO.setDocTypeCurrentInd(KEWConstants.ACTIVE_CD);
        } else {
            docTypeVO.setDocTypeCurrentInd(KEWConstants.INACTIVE_CD);
        }
        docTypeVO.setPostProcessorName(docType.getPostProcessorName());
        docTypeVO.setDocTypeJndiFactoryClass(null);
        docTypeVO.setDocTypeActiveInd(docType.getActiveInd().booleanValue());
        if (docType.getParentDocType() != null) {
            docTypeVO.setDocTypeActiveInherited(true);
        } else {
            docTypeVO.setDocTypeActiveInherited(false);
        }
        docTypeVO.setDocTypePreApprovalPolicy(docType.getPreApprovePolicy().getPolicyValue().booleanValue());
        Workgroup blanketWorkgroup = docType.getBlanketApproveWorkgroup();
        if (blanketWorkgroup != null) {
            docTypeVO.setBlanketApproveWorkgroupId(blanketWorkgroup.getWorkflowGroupId().getGroupId());
        }
        docTypeVO.setBlanketApprovePolicy(docType.getBlanketApprovePolicy());
        if (CompatUtils.isRouteLevelCompatible(docType)) {
            List nodes = CompatUtils.getRouteLevelCompatibleNodeList(docType);
            RouteTemplateEntryDTO[] templates = new RouteTemplateEntryDTO[nodes.size()];
            int index = 0;
            for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
                RouteNode node = (RouteNode) iterator.next();
                templates[index++] = convertRouteTemplateEntry(node);
            }
            docTypeVO.setRouteTemplates(templates);
        }
        docTypeVO.setRoutePath(convertRoutePath(docType));
        return docTypeVO;
    }

    public static RouteTemplateEntryDTO convertRouteTemplateEntry(RouteNode node) {
        RouteTemplateEntryDTO entryVO = new RouteTemplateEntryDTO();
        entryVO.setFinalApprover(node.getFinalApprovalInd().booleanValue());
        entryVO.setMandatoryRoute(node.getMandatoryRouteInd().booleanValue());
        entryVO.setRouteLevel(CompatUtils.getLevelForNode(node.getDocumentType(), node.getRouteNodeName()));
        entryVO.setRouteLevelName(node.getRouteNodeName());
        entryVO.setRouteMethodName(node.getRouteMethodName());
        entryVO.setDocTypeId(node.getDocumentTypeId());
        entryVO.setExceptionWorkgroupId(node.getExceptionWorkgroupId());
        entryVO.setJrf_ver_nbr(node.getLockVerNbr());
        entryVO.setMandatoryRoute(node.getMandatoryRouteInd().toString());
        return entryVO;
    }

    public static RoutePathDTO convertRoutePath(DocumentType documentType) {
        RoutePathDTO routePath = new RoutePathDTO();
        ProcessDTO[] processes = new ProcessDTO[documentType.getProcesses().size()];
        int index = 0;
        for (Iterator iterator = documentType.getProcesses().iterator(); iterator.hasNext();) {
            Process process = (Process) iterator.next();
            processes[index++] = convertProcess(process);
        }
        routePath.setProcesses(processes);
        return routePath;
    }

    public static ActionRequestDTO convertActionRequest(ActionRequestValue actionRequest) throws KEWUserNotFoundException {
        // TODO some newly added actionrequest properties are not here (delegation stuff)
        ActionRequestDTO actionRequestVO = new ActionRequestDTO();
        actionRequestVO.setActionRequested(actionRequest.getActionRequested());
        actionRequestVO.setActionRequestId(actionRequest.getActionRequestId());

        if (actionRequest.getActionTaken() != null) {
            actionRequestVO.setActionTakenId(actionRequest.getActionTakenId());
            actionRequestVO.setActionTaken(convertActionTaken(actionRequest.getActionTaken()));
        }

        actionRequestVO.setAnnotation(actionRequest.getAnnotation());
        actionRequestVO.setDateCreated(Utilities.convertTimestamp(actionRequest.getCreateDate()));
        actionRequestVO.setDocVersion(actionRequest.getDocVersion());
        actionRequestVO.setUserVO(convertUser(actionRequest.getWorkflowUser()));
        if (actionRequest.getWorkflowId() != null) {
            // TODO switch this to a user vo
            actionRequestVO.setEmplyId(actionRequest.getWorkflowUser().getEmplId().getEmplId());
        }
        actionRequestVO.setIgnorePrevAction(actionRequest.getIgnorePrevAction());
        actionRequestVO.setPriority(actionRequest.getPriority());
        actionRequestVO.setRecipientTypeCd(actionRequest.getRecipientTypeCd());
        actionRequestVO.setResponsibilityDesc(actionRequest.getResponsibilityDesc());
        actionRequestVO.setResponsibilityId(actionRequest.getResponsibilityId());
        actionRequestVO.setRouteHeaderId(actionRequest.getRouteHeaderId());
        actionRequestVO.setRouteLevel(actionRequest.getRouteLevel());
        actionRequestVO.setNodeName(actionRequest.getPotentialNodeName());
        actionRequestVO.setNodeInstanceId((actionRequest.getNodeInstance() == null ? null : actionRequest.getNodeInstance().getRouteNodeInstanceId()));
        // actionRequestVO.setRouteMethodName(actionRequest.getRouteMethodName());
        // TODO delyea - should below be using actionRequest.getRoleName()?
        actionRequestVO.setRoleName(actionRequest.getQualifiedRoleName());
        actionRequestVO.setQualifiedRoleName(actionRequest.getQualifiedRoleName());
        actionRequestVO.setQualifiedRoleNameLabel(actionRequest.getQualifiedRoleNameLabel());
        actionRequestVO.setStatus(actionRequest.getStatus());
        if (actionRequest.isWorkgroupRequest()) {
            actionRequestVO.setWorkgroupId(actionRequest.getWorkgroupId());
            actionRequestVO.setWorkgroupVO(convertWorkgroup(actionRequest.getWorkgroup()));
        }
        ActionRequestDTO[] childRequestVOs = new ActionRequestDTO[actionRequest.getChildrenRequests().size()];
        int index = 0;
        for (Iterator iterator = actionRequest.getChildrenRequests().iterator(); iterator.hasNext();) {
            ActionRequestValue childRequest = (ActionRequestValue) iterator.next();
            ActionRequestDTO childRequestVO = convertActionRequest(childRequest);
            childRequestVO.setParentActionRequest(actionRequestVO);
            childRequestVOs[index++] = childRequestVO;
        }
        actionRequestVO.setChildrenRequests(childRequestVOs);
        return actionRequestVO;
    }

    public static ActionTakenDTO convertActionTaken(ActionTakenValue actionTaken) throws KEWUserNotFoundException {
        if (actionTaken == null) {
            return null;
        }
        ActionTakenDTO actionTakenVO = new ActionTakenDTO();
        actionTakenVO.setActionDate(Utilities.convertTimestamp(actionTaken.getActionDate()));
        actionTakenVO.setActionTaken(actionTaken.getActionTaken());
        actionTakenVO.setActionTakenId(actionTaken.getActionTakenId());
        actionTakenVO.setAnnotation(actionTaken.getAnnotation());
        actionTakenVO.setDocVersion(actionTaken.getDocVersion());
        actionTakenVO.setRouteHeaderId(actionTaken.getRouteHeaderId());
        WorkflowUser user = actionTaken.getWorkflowUser();
        if (user != null) {
            actionTakenVO.setUserVO(convertUser(user));
        }
        WorkflowUser delegator = actionTaken.getDelegatorUser();
        if (delegator != null) {
            actionTakenVO.setDelegatorVO(convertUser(delegator));
        }
        return actionTakenVO;
    }

    public static WorkgroupIdDTO convertGroupId(GroupId groupId) {
        WorkgroupIdDTO workgroupId = null;
        if (groupId instanceof GroupNameId) {
            GroupNameId groupName = (GroupNameId) groupId;
            workgroupId = new WorkgroupNameIdDTO(groupName.getNameId());
        } else if (groupId instanceof WorkflowGroupId) {
            WorkflowGroupId workflowGroupId = (WorkflowGroupId) groupId;
            workgroupId = new WorkflowGroupIdDTO(workflowGroupId.getGroupId());
        }
        return workgroupId;
    }

    public static GroupId convertWorkgroupIdVO(WorkgroupIdDTO workgroupId) {
        GroupId groupId = null;
        if (workgroupId instanceof WorkgroupNameIdDTO) {
            WorkgroupNameIdDTO workgroupName = (WorkgroupNameIdDTO) workgroupId;
            groupId = new GroupNameId(workgroupName.getWorkgroupName());
        } else if (workgroupId instanceof WorkflowGroupIdDTO) {
            WorkflowGroupIdDTO workflowGroupId = (WorkflowGroupIdDTO) workgroupId;
            groupId = new WorkflowGroupId(workflowGroupId.getWorkgroupId());
        }

        return groupId;
    }

    public static UserIdDTO convertUserId(UserId userId) {
        UserIdDTO userIdVO = null;
        if (userId instanceof AuthenticationUserId) {
            AuthenticationUserId id = (AuthenticationUserId) userId;
            userIdVO = new NetworkIdDTO(id.getAuthenticationId());
        } else if (userId instanceof EmplId) {
            EmplId id = (EmplId) userId;
            userIdVO = new EmplIdDTO(id.getEmplId());
        } else if (userId instanceof UuId) {
            UuId id = (UuId) userId;
            userIdVO = new UuIdDTO(id.getUuId());
        } else if (userId instanceof WorkflowUserId) {
            WorkflowUserId id = (WorkflowUserId) userId;
            userIdVO = new WorkflowIdDTO(id.getWorkflowId());
        }
        return userIdVO;
    }

    public static UserId convertUserIdVO(UserIdDTO userIdVO) {
        UserId userId = null;
        if (userIdVO instanceof NetworkIdDTO) {
            NetworkIdDTO id = (NetworkIdDTO) userIdVO;
            userId = new AuthenticationUserId(id.getNetworkId());
            if (userId.isEmpty()) {
                throw new RuntimeException("Attempting to use empty NetworkId");
            }
        } else if (userIdVO instanceof EmplIdDTO) {
            EmplIdDTO id = (EmplIdDTO) userIdVO;
            userId = new EmplId(id.getEmplId());
            if (userId.isEmpty()) {
                throw new RuntimeException("Attempting to use empty EmplId");
            }
        } else if (userIdVO instanceof UuIdDTO) {
            UuIdDTO id = (UuIdDTO) userIdVO;
            userId = new UuId(id.getUuId());
            if (userId.isEmpty()) {
                throw new RuntimeException("Attempting to use empty UuId");
            }
        } else if (userIdVO instanceof WorkflowIdDTO) {
            WorkflowIdDTO id = (WorkflowIdDTO) userIdVO;
            userId = new WorkflowUserId(id.getWorkflowId());
            if (userId.isEmpty()) {
                throw new RuntimeException("Attempting to use empty WorkflowId");
            }
        }
        return userId;
    }

    public static ResponsiblePartyDTO convertResponsibleParty(ResponsibleParty responsibleParty) {
        if (responsibleParty == null) {
            return null;
        }
        ResponsiblePartyDTO responsiblePartyVO = new ResponsiblePartyDTO();
        responsiblePartyVO.setWorkgroupId(DTOConverter.convertGroupId(responsibleParty.getGroupId()));
        responsiblePartyVO.setUserId(DTOConverter.convertUserId(responsibleParty.getUserId()));
        responsiblePartyVO.setRoleName(responsibleParty.getRoleName());
        return responsiblePartyVO;
    }

    public static ResponsibleParty convertResponsiblePartyVO(ResponsiblePartyDTO responsiblePartyVO) {
        if (responsiblePartyVO == null) {
            return null;
        }
        ResponsibleParty responsibleParty = new ResponsibleParty();
        responsibleParty.setGroupId(DTOConverter.convertWorkgroupIdVO(responsiblePartyVO.getWorkgroupId()));
        responsibleParty.setUserId(DTOConverter.convertUserIdVO(responsiblePartyVO.getUserId()));
        responsibleParty.setRoleName(responsiblePartyVO.getRoleName());
        return responsibleParty;
    }

    /**
     * refactor name to convertResponsiblePartyVO when ResponsibleParty object is gone
     * 
     * @param responsiblePartyVO
     * @return
     * @throws KEWUserNotFoundException
     */
    public static Recipient convertResponsiblePartyVOtoRecipient(ResponsiblePartyDTO responsiblePartyVO) throws KEWUserNotFoundException {
        if (responsiblePartyVO == null) {
            return null;
        }
        if (responsiblePartyVO.getRoleName() != null) {
            return new RoleRecipient(responsiblePartyVO.getRoleName());
        }
        GroupId groupId = convertWorkgroupIdVO(responsiblePartyVO.getWorkgroupId());
        if (groupId != null) {
            return KEWServiceLocator.getWorkgroupService().getWorkgroup(groupId);
        }
        UserId userId = convertUserIdVO(responsiblePartyVO.getUserId());
        if (userId != null) {
            return KEWServiceLocator.getUserService().getWorkflowUser(userId);
        }
        throw new WorkflowRuntimeException("ResponsibleParty of unknown type");
    }

    /**
     * Converts an ActionRequestVO to an ActionRequest. The ActionRequestVO passed in must be the root action request in the
     * graph, otherwise an IllegalArgumentException is thrown. This is to avoid potentially sticky issues with circular
     * references in the conversion. NOTE: This method's primary purpose is to convert ActionRequestVOs returned from a
     * RouteModule. Incidentally, the VO's returned from the route module will be lacking some information (like the node
     * instance) so no attempts are made to convert this data since further initialization is handled by a higher level
     * component (namely ActionRequestService.initializeActionRequestGraph).
     */
    public static ActionRequestValue convertActionRequestVO(ActionRequestDTO actionRequestVO) throws KEWUserNotFoundException {
        if (actionRequestVO == null) {
            return null;
        }
        if (actionRequestVO.getParentActionRequest() != null || actionRequestVO.getParentActionRequestId() != null) {
            throw new IllegalArgumentException("Cannot convert a non-root ActionRequestVO");
        }
        ActionRequestValue actionRequest = new ActionRequestFactory().createBlankActionRequest();
        populateActionRequest(actionRequest, actionRequestVO);
        if (actionRequestVO.getChildrenRequests() != null) {
            for (int i = 0; i < actionRequestVO.getChildrenRequests().length; i++) {
                ActionRequestDTO childVO = actionRequestVO.getChildrenRequests()[i];
                actionRequest.getChildrenRequests().add(convertActionRequestVO(childVO, actionRequest));
            }
        }
        return actionRequest;
    }

    public static ActionRequestValue convertActionRequestVO(ActionRequestDTO actionRequestVO, ActionRequestValue parentActionRequest) throws KEWUserNotFoundException {
        if (actionRequestVO == null) {
            return null;
        }
        ActionRequestValue actionRequest = new ActionRequestFactory().createBlankActionRequest();
        populateActionRequest(actionRequest, actionRequestVO);
        actionRequest.setParentActionRequest(parentActionRequest);
        actionRequest.setParentActionRequestId(parentActionRequest.getActionRequestId());
        if (actionRequestVO.getChildrenRequests() != null) {
            for (int i = 0; i < actionRequestVO.getChildrenRequests().length; i++) {
                ActionRequestDTO childVO = actionRequestVO.getChildrenRequests()[i];
                actionRequest.getChildrenRequests().add(convertActionRequestVO(childVO, actionRequest));
            }
        }
        return actionRequest;
    }

    /**
     * This method converts everything except for the parent and child requests
     */
    private static void populateActionRequest(ActionRequestValue actionRequest, ActionRequestDTO actionRequestVO) throws KEWUserNotFoundException {

        actionRequest.setActionRequested(actionRequestVO.getActionRequested());
        actionRequest.setActionRequestId(actionRequestVO.getActionRequestId());
        actionRequest.setActionTakenId(actionRequestVO.getActionTakenId());
        actionRequest.setAnnotation(actionRequestVO.getAnnotation());
        actionRequest.setApprovePolicy(actionRequestVO.getApprovePolicy());
        actionRequest.setCreateDate(new Timestamp(new Date().getTime()));
        actionRequest.setCurrentIndicator(actionRequestVO.getCurrentIndicator());
        actionRequest.setDelegationType(actionRequestVO.getDelegationType());
        actionRequest.setDocVersion(actionRequestVO.getDocVersion());
        actionRequest.setIgnorePrevAction(actionRequestVO.getIgnorePrevAction());
        actionRequest.setPriority(actionRequestVO.getPriority());
        actionRequest.setQualifiedRoleName(actionRequestVO.getQualifiedRoleName());
        actionRequest.setQualifiedRoleNameLabel(actionRequestVO.getQualifiedRoleNameLabel());
        actionRequest.setRecipientTypeCd(actionRequestVO.getRecipientTypeCd());
        actionRequest.setResponsibilityDesc(actionRequestVO.getResponsibilityDesc());
        actionRequest.setResponsibilityId(actionRequestVO.getResponsibilityId());
        actionRequest.setRoleName(actionRequestVO.getRoleName());
        Long routeHeaderId = actionRequestVO.getRouteHeaderId();
        if (routeHeaderId != null) {
            actionRequest.setRouteHeaderId(routeHeaderId);
            actionRequest.setRouteHeader(KEWServiceLocator.getRouteHeaderService().getRouteHeader(routeHeaderId));
        }
        // properties set in routemanagerservice
        actionRequest.setRouteLevel(actionRequestVO.getRouteLevel());
        // TODO add the node instance to the VO
        // actionRequest.setRouteMethodName(actionRequestVO.getRouteMethodName());
        actionRequest.setStatus(actionRequestVO.getStatus());
        // TODO this should be moved to a validate somewhere's...
        boolean userSet = false;
        if (actionRequestVO.getUserIdVO() != null) {
            UserId userId = convertUserIdVO(actionRequestVO.getUserIdVO());
            WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userId);
            actionRequest.setWorkflowId(user.getWorkflowId());
            userSet = true;
        } else if (actionRequestVO.getEmplyId() != null) {
            WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(new EmplId(actionRequestVO.getEmplyId()));
            actionRequest.setWorkflowId(user.getWorkflowId());
            userSet = true;
        } else if (actionRequestVO.getUserVO() != null) {
            WorkflowUser user = convertUserVO(actionRequestVO.getUserVO());
            actionRequest.setWorkflowId(user.getWorkflowId());
            userSet = true;
        }
        if (actionRequestVO.getWorkgroupId() != null) {
            Long workgroupId = actionRequestVO.getWorkgroupId();
            // validate that the workgroup is good.
            Workgroup workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(workgroupId));
            if (workgroup == null) {
                throw new RuntimeException("Workgroup Id " + workgroupId + " is invalid.  Action Request cannot be activated.");
            }
            actionRequest.setWorkgroupId(workgroupId);
            userSet = true;
        } else if (actionRequestVO.getWorkgroupVO() != null) {
            Long workgroupId = actionRequestVO.getWorkgroupVO().getWorkgroupId();
            // validate that the workgroup is good.
            Workgroup workgroup = KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(workgroupId));
            if (workgroup == null) {
                throw new RuntimeException("Workgroup Id " + workgroupId + " is invalid.  Action Request cannot be activated.");
            }
            actionRequest.setWorkgroupId(workgroupId);
            userSet = true;
        }
        // TODO role requests will not have a user or workgroup, so this code needs to handle that case
        if (!userSet) {
            throw new RuntimeException("Post processor didn't set a user or workgroup on the request");
        }
    }

    public static ActionTakenValue convertActionTakenVO(ActionTakenDTO actionTakenVO) throws KEWUserNotFoundException {
        if (actionTakenVO == null) {
            return null;
        }
        ActionTakenValue actionTaken = new ActionTakenValue();
        actionTaken.setActionDate(new Timestamp(actionTakenVO.getActionDate().getTimeInMillis()));
        actionTaken.setActionTaken(actionTakenVO.getActionTaken());
        actionTaken.setActionTakenId(actionTakenVO.getActionTakenId());
        actionTaken.setAnnotation(actionTakenVO.getAnnotation());
        actionTaken.setCurrentIndicator(Boolean.TRUE);
        WorkflowUser delegator = convertUserVO(actionTakenVO.getDelegatorVO());
        actionTaken.setDelegator(delegator);
        if (delegator != null) {
            actionTaken.setDelegatorWorkflowId(delegator.getWorkflowUserId().getWorkflowId());
        }
        actionTaken.setDocVersion(actionTakenVO.getDocVersion());
        DocumentRouteHeaderValue routeHeader = KEWServiceLocator.getRouteHeaderService().getRouteHeader(actionTakenVO.getRouteHeaderId());
        actionTaken.setRouteHeader(routeHeader);
        actionTaken.setRouteHeaderId(actionTaken.getRouteHeaderId());
        WorkflowUser user = convertUserVO(actionTakenVO.getUserVO());
        actionTaken.setWorkflowId(user.getWorkflowUserId().getWorkflowId());
        return actionTaken;
    }

    public static DocumentRouteStatusChangeDTO convertDocumentRouteStatusChange(DocumentRouteStatusChange statusChange) {
        if (statusChange == null) {
            return null;
        }
        DocumentRouteStatusChangeDTO statusChangeVO = new DocumentRouteStatusChangeDTO();
        statusChangeVO.setRouteHeaderId(statusChange.getRouteHeaderId());
        statusChangeVO.setAppDocId(statusChange.getAppDocId());
        statusChangeVO.setOldRouteStatus(statusChange.getOldRouteStatus());
        statusChangeVO.setNewRouteStatus(statusChange.getNewRouteStatus());
        return statusChangeVO;
    }

    public static DocumentRouteLevelChangeDTO convertDocumentRouteLevelChange(DocumentRouteLevelChange routeLevelChange) {
        if (routeLevelChange == null) {
            return null;
        }
        DocumentRouteLevelChangeDTO routeLevelChangeVO = new DocumentRouteLevelChangeDTO();
        routeLevelChangeVO.setRouteHeaderId(routeLevelChange.getRouteHeaderId());
        routeLevelChangeVO.setAppDocId(routeLevelChange.getAppDocId());
        routeLevelChangeVO.setOldRouteLevel(routeLevelChange.getOldRouteLevel());
        routeLevelChangeVO.setNewRouteLevel(routeLevelChange.getNewRouteLevel());
        routeLevelChangeVO.setOldNodeName(routeLevelChange.getOldNodeName());
        routeLevelChangeVO.setNewNodeName(routeLevelChange.getNewNodeName());
        routeLevelChangeVO.setOldNodeInstanceId(routeLevelChange.getOldNodeInstanceId());
        routeLevelChangeVO.setNewNodeInstanceId(routeLevelChange.getNewNodeInstanceId());
        return routeLevelChangeVO;
    }

    public static DeleteEventDTO convertDeleteEvent(DeleteEvent deleteEvent) {
        if (deleteEvent == null) {
            return null;
        }
        DeleteEventDTO deleteEventVO = new DeleteEventDTO();
        deleteEventVO.setRouteHeaderId(deleteEvent.getRouteHeaderId());
        deleteEventVO.setAppDocId(deleteEvent.getAppDocId());
        return deleteEventVO;
    }

    public static ActionTakenEventDTO convertActionTakenEvent(ActionTakenEvent actionTakenEvent) throws KEWUserNotFoundException {
        if (actionTakenEvent == null) {
            return null;
        }
        ActionTakenEventDTO actionTakenEventVO = new ActionTakenEventDTO();
        actionTakenEventVO.setRouteHeaderId(actionTakenEvent.getRouteHeaderId());
        actionTakenEventVO.setAppDocId(actionTakenEvent.getAppDocId());
        actionTakenEventVO.setActionTaken(convertActionTaken(actionTakenEvent.getActionTaken()));
        return actionTakenEventVO;
    }

    public static BeforeProcessEventDTO convertBeforeProcessEvent(BeforeProcessEvent event) throws KEWUserNotFoundException {
        if (event == null) {
            return null;
        }
        BeforeProcessEventDTO beforeProcessEvent = new BeforeProcessEventDTO();
        beforeProcessEvent.setRouteHeaderId(event.getRouteHeaderId());
        beforeProcessEvent.setAppDocId(event.getAppDocId());
        beforeProcessEvent.setNodeInstanceId(event.getNodeInstanceId());
        return beforeProcessEvent;
    }

    public static AfterProcessEventDTO convertAfterProcessEvent(AfterProcessEvent event) throws KEWUserNotFoundException {
        if (event == null) {
            return null;
        }
        AfterProcessEventDTO afterProcessEvent = new AfterProcessEventDTO();
        afterProcessEvent.setRouteHeaderId(event.getRouteHeaderId());
        afterProcessEvent.setAppDocId(event.getAppDocId());
        afterProcessEvent.setNodeInstanceId(event.getNodeInstanceId());
        afterProcessEvent.setSuccessfullyProcessed(event.isSuccessfullyProcessed());
        return afterProcessEvent;
    }

    public static AttributeDefinition convertWorkflowAttributeDefinitionVO(WorkflowAttributeDefinitionDTO definitionVO, org.kuali.rice.kew.doctype.DocumentType documentType) {
        if (definitionVO == null) {
            return null;
        }
        // get the rule attribute so we can get's it's message antity and not blow up if it's remote
        RuleAttribute ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByClassName(definitionVO.getAttributeName());
        if (ruleAttribute == null) {
            ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByName(definitionVO.getAttributeName());
        }
        if (ruleAttribute == null) {
            throw new WorkflowRuntimeException("Attribute " + definitionVO.getAttributeName() + " not found");
        }

        ObjectDefinition definition = new ObjectDefinition(ruleAttribute.getClassName());
        for (int index = 0; index < definitionVO.getConstructorParameters().length; index++) {
            String parameter = definitionVO.getConstructorParameters()[index];
            definition.addConstructorParameter(new DataDefinition(parameter, String.class));
        }
        boolean propertiesAsMap = KEWConstants.RULE_XML_ATTRIBUTE_TYPE.equals(ruleAttribute.getType()) || KEWConstants.SEARCHABLE_XML_ATTRIBUTE_TYPE.equals(ruleAttribute.getType());
        if (!propertiesAsMap) {
            for (int index = 0; index < definitionVO.getProperties().length; index++) {
                PropertyDefinitionDTO propertyDefVO = definitionVO.getProperties()[index];
                definition.addProperty(new PropertyDefinition(propertyDefVO.getName(), new DataDefinition(propertyDefVO.getValue(), String.class)));
            }
        }

        // this is likely from an EDL validate call and ME may needed to be added to the AttDefinitionVO.
        if (ruleAttribute.getMessageEntity() != null) {
            definition.setMessageEntity(ruleAttribute.getMessageEntity());
        } else {
            // get the me from the document type if it's been passed in - the document is having action taken on it.
            if (documentType != null) {
                definition.setMessageEntity(documentType.getMessageEntity());
            }
        }

        return new AttributeDefinition(ruleAttribute, definition);
    }

    public static DocumentDetailDTO convertDocumentDetail(DocumentRouteHeaderValue routeHeader) throws WorkflowException {
        if (routeHeader == null) {
            return null;
        }
        DocumentDetailDTO detail = new DocumentDetailDTO();
        populateRouteHeaderVO(detail, routeHeader);
        Map nodeInstances = new HashMap();
        List actionRequestVOs = new ArrayList();
        List rootActionRequests = KEWServiceLocator.getActionRequestService().getRootRequests(routeHeader.getActionRequests());
        for (Iterator iterator = rootActionRequests.iterator(); iterator.hasNext();) {
            ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
            actionRequestVOs.add(convertActionRequest(actionRequest));
            RouteNodeInstance nodeInstance = actionRequest.getNodeInstance();
            if (nodeInstance == null) {
                continue;
            }
            if (nodeInstance.getRouteNodeInstanceId() == null) {
                throw new WorkflowException("Error creating document detail structure because of NULL node instance id.");
            }
            nodeInstances.put(nodeInstance.getRouteNodeInstanceId(), nodeInstance);
        }
        detail.setActionRequests((ActionRequestDTO[]) actionRequestVOs.toArray(new ActionRequestDTO[0]));
        List nodeInstanceVOs = new ArrayList();
        for (Iterator iterator = nodeInstances.values().iterator(); iterator.hasNext();) {
            RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
            nodeInstanceVOs.add(convertRouteNodeInstance(nodeInstance));
        }
        detail.setNodeInstances((RouteNodeInstanceDTO[]) nodeInstanceVOs.toArray(new RouteNodeInstanceDTO[0]));
        List actionTakenVOs = new ArrayList();
        for (Iterator iterator = routeHeader.getActionsTaken().iterator(); iterator.hasNext();) {
            ActionTakenValue actionTaken = (ActionTakenValue) iterator.next();
            actionTakenVOs.add(convertActionTaken(actionTaken));
        }
        detail.setActionsTaken((ActionTakenDTO[]) actionTakenVOs.toArray(new ActionTakenDTO[0]));
        return detail;
    }

    public static RouteNodeInstanceDTO convertRouteNodeInstance(RouteNodeInstance nodeInstance) throws WorkflowException {
        if (nodeInstance == null) {
            return null;
        }
        RouteNodeInstanceDTO nodeInstanceVO = new RouteNodeInstanceDTO();
        nodeInstanceVO.setActive(nodeInstance.isActive());
        nodeInstanceVO.setBranchId(nodeInstance.getBranch().getBranchId());
        nodeInstanceVO.setComplete(nodeInstance.isComplete());
        nodeInstanceVO.setDocumentId(nodeInstance.getDocumentId());
        nodeInstanceVO.setInitial(nodeInstance.isInitial());
        nodeInstanceVO.setName(nodeInstance.getName());
        nodeInstanceVO.setProcessId(nodeInstance.getProcess() != null ? nodeInstance.getProcess().getRouteNodeInstanceId() : null);
        nodeInstanceVO.setRouteNodeId(nodeInstance.getRouteNode().getRouteNodeId());
        nodeInstanceVO.setRouteNodeInstanceId(nodeInstance.getRouteNodeInstanceId());
        nodeInstanceVO.setState(convertStates(nodeInstance.getState()));

        nodeInstanceVO.setNextNodes(new RouteNodeInstanceDTO[nodeInstance.getNextNodeInstances().size()]);
        int i = 0;
        for (Iterator iter = nodeInstance.getNextNodeInstances().iterator(); iter.hasNext(); i++) {
            RouteNodeInstance nextNodeInstance = (RouteNodeInstance) iter.next();
            nodeInstanceVO.getNextNodes()[i] = convertRouteNodeInstance(nextNodeInstance);
        }

        return nodeInstanceVO;
    }

    public static StateDTO[] convertStates(Collection states) {
        if (states == null) {
            return null;
        }
        StateDTO[] stateVOs = new StateDTO[states.size()];
        int index = 0;
        for (Iterator iterator = states.iterator(); iterator.hasNext();) {
            State state = (State) iterator.next();
            stateVOs[index++] = convertState(state);
        }
        return stateVOs;
    }

    public static StateDTO convertState(State nodeState) {
        if (nodeState == null) {
            return null;
        }
        StateDTO stateVO = new StateDTO();
        stateVO.setStateId(nodeState.getStateId());
        stateVO.setKey(nodeState.getKey());
        stateVO.setValue(nodeState.getValue());
        return stateVO;
    }

    public static RouteNodeDTO convertRouteNode(RouteNode node) {
        if (node == null) {
            return null;
        }
        RouteNodeDTO nodeVO = new RouteNodeDTO();
        nodeVO.setActivationType(node.getActivationType());
        nodeVO.setBranchName(node.getBranch() != null ? node.getBranch().getName() : null);
        nodeVO.setDocumentTypeId(node.getDocumentTypeId());
        try {
            nodeVO.setExceptionWorkgroup(convertWorkgroup(node.getExceptionWorkgroup()));
        } catch (KEWUserNotFoundException e) {
            throw new WorkflowRuntimeException("Could not locate users in exception workgroup for node " + node.getRouteNodeId() + ".", e);
        }
        nodeVO.setFinalApprovalInd(node.getFinalApprovalInd().booleanValue());
        nodeVO.setMandatoryRouteInd(node.getMandatoryRouteInd().booleanValue());
        nodeVO.setNodeType(node.getNodeType());
        nodeVO.setRouteMethodCode(node.getRouteMethodCode());
        nodeVO.setRouteMethodName(node.getRouteMethodName());
        nodeVO.setRouteNodeId(node.getRouteNodeId());
        nodeVO.setRouteNodeName(node.getRouteNodeName());
        int index = 0;
        Long[] previousNodeIds = new Long[node.getPreviousNodes().size()];
        for (Iterator iterator = node.getPreviousNodes().iterator(); iterator.hasNext();) {
            RouteNode prevNode = (RouteNode) iterator.next();
            previousNodeIds[index++] = prevNode.getRouteNodeId();
        }
        nodeVO.setPreviousNodeIds(previousNodeIds);
        index = 0;
        Long[] nextNodeIds = new Long[node.getNextNodes().size()];
        for (Iterator iterator = node.getNextNodes().iterator(); iterator.hasNext();) {
            RouteNode nextNode = (RouteNode) iterator.next();
            nextNodeIds[index++] = nextNode.getRouteNodeId();
        }
        nodeVO.setNextNodeIds(nextNodeIds);
        return nodeVO;
    }

    public static ProcessDTO convertProcess(Process process) {
        ProcessDTO processVO = new ProcessDTO();
        processVO.setInitial(process.isInitial());
        processVO.setInitialRouteNode(convertRouteNode(process.getInitialRouteNode()));
        processVO.setName(process.getName());
        processVO.setProcessId(process.getProcessId());
        return processVO;
    }

    public static MovePoint convertMovePointVO(MovePointDTO movePointVO) {
        MovePoint movePoint = new MovePoint();
        movePoint.setStartNodeName(movePointVO.getStartNodeName());
        movePoint.setStepsToMove(movePointVO.getStepsToMove());
        return movePoint;
    }

    public static AdHocRevoke convertAdHocRevokeVO(AdHocRevokeDTO revokeVO) throws WorkflowException {
        AdHocRevoke revoke = new AdHocRevoke();
        revoke.setActionRequestId(revokeVO.getActionRequestId());
        revoke.setNodeName(revokeVO.getNodeName());
        if (revokeVO.getUserId() != null) {
            revoke.setUser(KEWServiceLocator.getUserService().getWorkflowUser(revokeVO.getUserId()));
        }
        if (revokeVO.getWorkgroupId() != null) {
            revoke.setWorkgroup(KEWServiceLocator.getWorkgroupService().getWorkgroup(revokeVO.getWorkgroupId()));
        }
        return revoke;
    }

    public static WorkflowAttributeValidationErrorDTO convertWorkflowAttributeValidationError(WorkflowAttributeValidationError error) {
        return new WorkflowAttributeValidationErrorDTO(error.getKey(), error.getMessage());
    }

    // Method added for updating notes on server sites based on NoteVO change. Modfy on April 7, 2006
    public static void updateNotes(RouteHeaderDTO routeHeaderVO, Long routeHeaderId) {
        NoteDTO[] notes = routeHeaderVO.getNotes();
        NoteDTO[] notesToDelete = routeHeaderVO.getNotesToDelete();
        Note noteToDelete = null;
        Note noteToSave = null;

        // Add or update notes to note table based on notes array in RouteHeaderVO
        if (notes != null) {
            for (int i = 0; i < notes.length; i++) {
                if (notes[i] != null) {
                    noteToSave = new Note();
                    noteToSave.setNoteId(notes[i].getNoteId());
                    noteToSave.setRouteHeaderId(routeHeaderId);
                    noteToSave.setNoteAuthorWorkflowId(notes[i].getNoteAuthorWorkflowId());
                    noteToSave.setNoteCreateDate(Utilities.convertCalendar(notes[i].getNoteCreateDate()));
                    noteToSave.setNoteText(notes[i].getNoteText());
                    noteToSave.setLockVerNbr(notes[i].getLockVerNbr());
                    // if notes[i].getNoteId() == null, add note to note table, otherwise update note to note table
                    getNoteService().saveNote(noteToSave);
                }
            }

        }

        // Delete notes from note table based on notesToDelete array in RouteHeaderVO
        if (notesToDelete != null) {
            for (int i = 0; i < notesToDelete.length; i++) {
                noteToDelete = getNoteService().getNoteByNoteId(notesToDelete[i].getNoteId());
                if (noteToDelete != null) {
                    getNoteService().deleteNote(noteToDelete);
                }
            }
            routeHeaderVO.setNotesToDelete(null);
        }
    }

    private static NoteService getNoteService() {
        return (NoteService) KEWServiceLocator.getService(KEWServiceLocator.NOTE_SERVICE);
    }

    private static NoteDTO[] convertNotesArrayListToNoteVOArray(List notesArrayList) {
        if (notesArrayList.size() > 0) {
            NoteDTO[] noteVOArray = new NoteDTO[notesArrayList.size()];
            int i = 0;
            Note tempNote;
            NoteDTO tempNoteVO;
            for (Iterator it = notesArrayList.iterator(); it.hasNext();) {
                tempNote = (Note) it.next();
                tempNoteVO = new NoteDTO();
                tempNoteVO.setNoteId(tempNote.getNoteId());
                tempNoteVO.setRouteHeaderId(tempNote.getRouteHeaderId());
                tempNoteVO.setNoteAuthorWorkflowId(tempNote.getNoteAuthorWorkflowId());
                tempNoteVO.setNoteCreateDate(Utilities.convertTimestamp(tempNote.getNoteCreateDate()));
                tempNoteVO.setNoteText(tempNote.getNoteText());
                tempNoteVO.setLockVerNbr(tempNote.getLockVerNbr());
                noteVOArray[i] = tempNoteVO;
                i++;
            }
            return noteVOArray;
        } else {
            return null;
        }
    }

    public static SimulationCriteria convertReportCriteriaVO(ReportCriteriaDTO criteriaVO) throws KEWUserNotFoundException {
        if (criteriaVO == null) {
            return null;
        }
        SimulationCriteria criteria = new SimulationCriteria();
        criteria.setDestinationNodeName(criteriaVO.getTargetNodeName());
        criteria.setDocumentId(criteriaVO.getRouteHeaderId());
        criteria.setDocumentTypeName(criteriaVO.getDocumentTypeName());
        criteria.setXmlContent(criteriaVO.getXmlContent());
        criteria.setActivateRequests(criteriaVO.getActivateRequests());
        if (criteriaVO.getRoutingUser() != null) {
            WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(criteriaVO.getRoutingUser());
            if (user == null) {
                throw new KEWUserNotFoundException("Could not locate user for the given id: " + criteriaVO.getRoutingUser());
            }
            criteria.setRoutingUser(user);
        }
        if (criteriaVO.getRuleTemplateNames() != null) {
            for (int index = 0; index < criteriaVO.getRuleTemplateNames().length; index++) {
                String ruleTemplateName = criteriaVO.getRuleTemplateNames()[index];
                criteria.getRuleTemplateNames().add(ruleTemplateName);
            }
        }
        if (criteriaVO.getNodeNames() != null) {
            for (int i = 0; i < criteriaVO.getNodeNames().length; i++) {
                String nodeName = criteriaVO.getNodeNames()[i];
                criteria.getNodeNames().add(nodeName);
            }
        }
        if (criteriaVO.getTargetUsers() != null) {
            for (int index = 0; index < criteriaVO.getTargetUsers().length; index++) {
                UserIdDTO userIdVO = criteriaVO.getTargetUsers()[index];
                WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userIdVO);
                if (user == null) {
                    throw new KEWUserNotFoundException("Could not locate user for the given id: " + userIdVO);
                }
                criteria.getDestinationRecipients().add(user);
            }
        }
        if (criteriaVO.getActionsToTake() != null) {
            for (int index = 0; index < criteriaVO.getActionsToTake().length; index++) {
                ReportActionToTakeDTO actionToTakeVO = criteriaVO.getActionsToTake()[index];
                criteria.getActionsToTake().add(convertReportActionToTakeVO(actionToTakeVO));
            }
        }
        return criteria;
    }

    public static SimulationActionToTake convertReportActionToTakeVO(ReportActionToTakeDTO actionToTakeVO) throws KEWUserNotFoundException {
        if (actionToTakeVO == null) {
            return null;
        }
        SimulationActionToTake actionToTake = new SimulationActionToTake();
        actionToTake.setNodeName(actionToTakeVO.getNodeName());
        if (StringUtils.isBlank(actionToTakeVO.getActionToPerform())) {
            throw new IllegalArgumentException("ReportActionToTakeVO must contain an action taken code and does not");
        }
        actionToTake.setActionToPerform(actionToTakeVO.getActionToPerform());
        if (actionToTakeVO.getUserIdVO() == null) {
            throw new IllegalArgumentException("ReportActionToTakeVO must contain a userId and does not");
        }
        WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(actionToTakeVO.getUserIdVO());
        if (user == null) {
            throw new KEWUserNotFoundException("Could not locate user for the given id: " + actionToTakeVO.getUserIdVO());
        }
        actionToTake.setUser(user);
        return actionToTake;
    }

    public static RuleDelegationDTO convertRuleDelegation(RuleDelegation ruleDelegation) throws WorkflowException {
        if (ruleDelegation == null) {
            return null;
        }
        RuleDelegationDTO ruleDelegationVO = new RuleDelegationDTO();
        ruleDelegationVO.setDelegationType(ruleDelegation.getDelegationType());
        ruleDelegationVO.setDelegationRule(convertRule(ruleDelegation.getDelegationRuleBaseValues()));
        return ruleDelegationVO;
    }

    // public static RuleDelegation convertRuleExtensionVO(RuleExtensionVO ruleExtensionVO) throws WorkflowException {}

    public static Collection<RuleExtensionDTO> convertRuleExtension(RuleExtension ruleExtension) throws WorkflowException {
        if (ruleExtension == null) {
            return null;
        }
        List<RuleExtensionDTO> extensionVOs = new ArrayList<RuleExtensionDTO>();
        for (Iterator iter = ruleExtension.getExtensionValues().iterator(); iter.hasNext();) {
            RuleExtensionValue extensionValue = (RuleExtensionValue) iter.next();
            extensionVOs.add(new RuleExtensionDTO(extensionValue.getKey(), extensionValue.getValue()));
        }
        return extensionVOs;
    }

    public static KeyValuePair convertRuleExtensionVO(RuleExtensionDTO ruleExtensionVO) throws WorkflowException {
        if (ruleExtensionVO == null) {
            return null;
        }
        return new KeyValuePair(ruleExtensionVO.getKey(), ruleExtensionVO.getValue());
    }

    public static RuleResponsibilityDTO convertRuleResponsibility(RuleResponsibility ruleResponsibility) throws WorkflowException {
        if (ruleResponsibility == null) {
            return null;
        }
        RuleResponsibilityDTO ruleResponsibilityVO = new RuleResponsibilityDTO();
        ruleResponsibilityVO.setActionRequestedCd(ruleResponsibility.getActionRequestedCd());
        ruleResponsibilityVO.setApprovePolicy(ruleResponsibility.getApprovePolicy());
        ruleResponsibilityVO.setPriority(ruleResponsibility.getPriority());
        ruleResponsibilityVO.setResponsibilityId(ruleResponsibility.getResponsibilityId());
        ruleResponsibilityVO.setRoleName(ruleResponsibility.getRole());
        ruleResponsibilityVO.setUser(convertUser(ruleResponsibility.getWorkflowUser()));
        ruleResponsibilityVO.setWorkgroup(convertWorkgroup(ruleResponsibility.getWorkgroup()));
        for (Iterator iter = ruleResponsibility.getDelegationRules().iterator(); iter.hasNext();) {
            RuleDelegation ruleDelegation = (RuleDelegation) iter.next();
            ruleResponsibilityVO.addDelegationRule(convertRuleDelegation(ruleDelegation));
        }
        return ruleResponsibilityVO;
    }

    // public static KeyValuePair convertRuleResponsibilityVO(RuleResponsibilityVO ruleResponsibilityVO) throws
    // WorkflowException {}

    public static RuleDTO convertRule(RuleBaseValues ruleValues) throws WorkflowException {
        if (ruleValues == null) {
            return null;
        }
        RuleDTO rule = new RuleDTO();
        rule.setActiveInd(ruleValues.getActiveInd());
        rule.setDescription(ruleValues.getDescription());
        rule.setDocTypeName(ruleValues.getDocTypeName());
        rule.setFromDate(ruleValues.getFromDateString());
        rule.setToDate(ruleValues.getToDateString());
        rule.setIgnorePrevious(ruleValues.getIgnorePrevious());
        rule.setRuleTemplateId(ruleValues.getRuleTemplateId());
        rule.setRuleTemplateName(ruleValues.getRuleTemplateName());

        // get keyPair values to setup RuleExtensionVOs
        for (Iterator iter = ruleValues.getRuleExtensions().iterator(); iter.hasNext();) {
            RuleExtension ruleExtension = (RuleExtension) iter.next();
            rule.addRuleExtensions(convertRuleExtension(ruleExtension));
        }
        // get keyPair values to setup RuleExtensionVOs
        for (Iterator iter = ruleValues.getResponsibilities().iterator(); iter.hasNext();) {
            RuleResponsibility ruleResponsibility = (RuleResponsibility) iter.next();
            rule.addRuleResponsibility(convertRuleResponsibility(ruleResponsibility));
        }
        return rule;
    }
    
    public static DocSearchCriteriaVO convertDocumentSearchCriteriaVO(DocumentSearchCriteriaDTO criteriaVO) throws WorkflowException {
        DocSearchCriteriaVO criteria = new DocSearchCriteriaVO();
        criteria.setAppDocId(criteriaVO.getAppDocId());
        criteria.setApprover(criteriaVO.getApprover());
        criteria.setDocRouteStatus(criteriaVO.getDocRouteStatus());
        criteria.setDocTitle(criteriaVO.getDocTitle());
        criteria.setDocTypeFullName(criteriaVO.getDocTypeFullName());
        criteria.setDocVersion(criteriaVO.getDocVersion());
        criteria.setFromDateApproved(criteriaVO.getFromDateApproved());
        criteria.setFromDateCreated(criteriaVO.getFromDateCreated());
        criteria.setFromDateFinalized(criteriaVO.getFromDateFinalized());
        criteria.setFromDateLastModified(criteriaVO.getFromDateLastModified());
        criteria.setInitiator(criteriaVO.getInitiator());
        criteria.setIsAdvancedSearch((criteriaVO.isAdvancedSearch()) ? DocSearchCriteriaVO.ADVANCED_SEARCH_INDICATOR_STRING : "NO");
        criteria.setSuperUserSearch((criteriaVO.isSuperUserSearch()) ? DocSearchCriteriaVO.SUPER_USER_SEARCH_INDICATOR_STRING : "NO");
        criteria.setRouteHeaderId(criteriaVO.getRouteHeaderId());
        criteria.setViewer(criteriaVO.getViewer());
        criteria.setWorkgroupViewerName(criteriaVO.getWorkgroupViewerName());
        criteria.setToDateApproved(criteriaVO.getToDateApproved());
        criteria.setToDateCreated(criteriaVO.getToDateCreated());
        criteria.setToDateFinalized(criteriaVO.getToDateFinalized());
        criteria.setToDateLastModified(criteriaVO.getToDateLastModified());
        criteria.setThreshold(criteriaVO.getThreshold());
        criteria.setSaveSearchForUser(criteriaVO.isSaveSearchForUser());
        
        // generate the route node criteria
        if ( (StringUtils.isNotBlank(criteriaVO.getDocRouteNodeName())) && (StringUtils.isBlank(criteriaVO.getDocTypeFullName())) ) {
            throw new WorkflowException("No document type name specified when attempting to search by route node name '" + criteriaVO.getDocRouteNodeName() + "'");
        } else if ( (StringUtils.isNotBlank(criteriaVO.getDocRouteNodeName())) && (StringUtils.isNotBlank(criteriaVO.getDocTypeFullName())) ) {
            criteria.setDocRouteNodeLogic(criteriaVO.getDocRouteNodeLogic());
            List routeNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(getDocumentTypeByName(criteria.getDocTypeFullName()), true);
            boolean foundRouteNode = false;
            for (Iterator iterator = routeNodes.iterator(); iterator.hasNext();) {
                RouteNode routeNode = (RouteNode) iterator.next();
                if (criteriaVO.getDocRouteNodeName().equals(routeNode.getRouteNodeName())) {
                    foundRouteNode = true;
                    break;
                }
            }
            if (!foundRouteNode) {
                throw new WorkflowException("Could not find route node name '" + criteriaVO.getDocRouteNodeName() + "' for document type name '" + criteriaVO.getDocTypeFullName() + "'");
            }
            criteria.setDocRouteNodeId(criteriaVO.getDocRouteNodeName());
        }
        
        // build a map of the search attributes passed in from the client creating lists where keys are duplicated
        HashMap<String, List<String>> searchAttributeValues = new HashMap<String,List<String>>();
        for (KeyValueDTO keyValueVO : criteriaVO.getSearchAttributeValues()) {
            if (searchAttributeValues.containsKey(keyValueVO.getKey())) {
                searchAttributeValues.get(keyValueVO.getKey()).add(keyValueVO.getValue());
            } else {
                searchAttributeValues.put(keyValueVO.getKey(), Arrays.asList(new String[]{keyValueVO.getValue()}));
            }
        }
        // build the list of SearchAttributeFormContainer objects
        List propertyFields = new ArrayList();
        for (String key : searchAttributeValues.keySet()) {
            List<String> values = searchAttributeValues.get(key);
            SearchAttributeFormContainer container = null;
            if (values.size() == 1) {
                container = new SearchAttributeFormContainer(key, values.get(0));
            } else if (values.size() > 1) {
                container = new SearchAttributeFormContainer(key, (String[])values.toArray());
            }
            if (container != null) {
                propertyFields.add(container);
            }
        }
        DocSearchUtils.addSearchableAttributesToCriteria(criteria, propertyFields, true);
        return criteria;
    }
    
    private static DocumentType getDocumentTypeByName(String documentTypeName) {
        return KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
    }
    
    public static DocumentSearchResultDTO convertDocumentSearchResultComponents(DocumentSearchResultComponents searchResult) throws WorkflowException {
        DocumentSearchResultDTO resultsVO = new DocumentSearchResultDTO();
        resultsVO.setColumns(convertColumns(searchResult.getColumns()));
        resultsVO.setSearchResults(convertDocumentSearchResults(searchResult.getSearchResults()));
        return resultsVO;
    }
    
    private static List<DocumentSearchResultRowDTO> convertDocumentSearchResults(List<DocumentSearchResult> searchResults) throws WorkflowException {
        List<DocumentSearchResultRowDTO> rowVOs = new ArrayList<DocumentSearchResultRowDTO>();
        for (DocumentSearchResult documentSearchResult : searchResults) {
            rowVOs.add(convertDocumentSearchResult(documentSearchResult));
        }
        return rowVOs;
    }
    
    public static DocumentSearchResultRowDTO convertDocumentSearchResult(DocumentSearchResult resultRow) throws WorkflowException {
        DocumentSearchResultRowDTO rowVO = new DocumentSearchResultRowDTO();
        List<KeyValueDTO> fieldValues = new ArrayList<KeyValueDTO>();
        for (KeyValueSort keyValueSort : resultRow.getResultContainers()) {
            fieldValues.add(new KeyValueDTO(keyValueSort.getKey(),keyValueSort.getValue(),keyValueSort.getUserDisplayValue()));
        }
        rowVO.setFieldValues(fieldValues);
        return rowVO;
    }
    
    private static List<LookupableColumnDTO> convertColumns(List<Column> columns) throws WorkflowException {
        List<LookupableColumnDTO> columnVOs = new ArrayList<LookupableColumnDTO>();
        for (Column column : columns) {
            columnVOs.add(convertColumn(column));
        }
        return columnVOs;
    }
    
    public static LookupableColumnDTO convertColumn(Column column) throws WorkflowException {
        LookupableColumnDTO columnVO = new LookupableColumnDTO();
        columnVO.setColumnTitle(column.getColumnTitle());
        columnVO.setKey(column.getKey());
        columnVO.setPropertyName(column.getPropertyName());
        columnVO.setSortable(column.isSortable());
        columnVO.setSortPropertyName(column.getSortPropertyName());
        columnVO.setType(column.getType());
        List<KeyValueDTO> displayParameters = new ArrayList<KeyValueDTO>();
        for (String key : column.getDisplayParameters().keySet()) {
            displayParameters.add(new KeyValueDTO(key,column.getDisplayParameters().get(key)));
        }
        columnVO.setDisplayParameters(displayParameters);
        return null;
    }

    //    public static RuleBaseValues convertRuleVO(RuleVO ruleVO) throws WorkflowException {}

    private static void handleException(String message, Exception e) throws WorkflowException {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else if (e instanceof WorkflowException) {
            throw (WorkflowException) e;
        }
        throw new WorkflowException(message, e);
    }

}