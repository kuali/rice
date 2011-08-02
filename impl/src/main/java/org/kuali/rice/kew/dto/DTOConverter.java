/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kew.dto;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.api.reflect.DataDefinition;
import org.kuali.rice.core.api.reflect.ObjectDefinition;
import org.kuali.rice.core.api.reflect.PropertyDefinition;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.xml.XmlHelper;
import org.kuali.rice.core.api.util.xml.XmlJotter;
import org.kuali.rice.core.framework.persistence.jdbc.sql.SQLUtils;
import org.kuali.rice.kew.actionitem.ActionItem;
import org.kuali.rice.kew.actionrequest.ActionRequestFactory;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.KimGroupRecipient;
import org.kuali.rice.kew.actionrequest.KimPrincipalRecipient;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.actions.ValidActions;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionTaken;
import org.kuali.rice.kew.api.action.AdHocRevoke;
import org.kuali.rice.kew.api.action.MovePoint;
import org.kuali.rice.kew.api.document.DocumentContentUpdate;
import org.kuali.rice.kew.api.document.DocumentDetail;
import org.kuali.rice.kew.api.document.InvalidDocumentContentException;
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition;
import org.kuali.rice.kew.definition.AttributeDefinition;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocSearchUtils;
import org.kuali.rice.kew.docsearch.DocumentSearchResult;
import org.kuali.rice.kew.docsearch.DocumentSearchResultComponents;
import org.kuali.rice.kew.docsearch.SearchableAttributeOld;
import org.kuali.rice.kew.docsearch.web.SearchAttributeFormContainer;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.documentlink.DocumentLink;
import org.kuali.rice.kew.engine.node.Branch;
import org.kuali.rice.kew.engine.node.BranchState;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.engine.node.State;
import org.kuali.rice.kew.engine.simulation.SimulationActionToTake;
import org.kuali.rice.kew.engine.simulation.SimulationCriteria;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.framework.docsearch.SearchableAttribute;
import org.kuali.rice.kew.notes.Note;
import org.kuali.rice.kew.notes.service.NoteService;
import org.kuali.rice.kew.postprocessor.ActionTakenEvent;
import org.kuali.rice.kew.postprocessor.AfterProcessEvent;
import org.kuali.rice.kew.postprocessor.BeforeProcessEvent;
import org.kuali.rice.kew.postprocessor.DeleteEvent;
import org.kuali.rice.kew.postprocessor.DocumentLockingEvent;
import org.kuali.rice.kew.postprocessor.DocumentRouteLevelChange;
import org.kuali.rice.kew.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.DocumentStatusTransition;
import org.kuali.rice.kew.routeheader.StandardDocumentContent;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kew.rule.RuleExtension;
import org.kuali.rice.kew.rule.RuleExtensionValue;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.rule.WorkflowAttribute;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kew.rule.WorkflowAttributeXmlValidator;
import org.kuali.rice.kew.rule.XmlConfiguredAttribute;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.xmlrouting.GenericXMLRuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.RoleRecipient;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.ResponsibleParty;
import org.kuali.rice.kew.web.KeyValueSort;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.bo.Person;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Translates Workflow server side beans into client side VO beans.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DTOConverter {
    private static final Logger LOG = Logger.getLogger(DTOConverter.class);

    public static RouteHeaderDTO convertRouteHeader(DocumentRouteHeaderValue routeHeader,
            String principalId) throws WorkflowException {
        RouteHeaderDTO routeHeaderVO = new RouteHeaderDTO();
        if (routeHeader == null) {
            return null;
        }
        populateRouteHeaderVO(routeHeaderVO, routeHeader);

        if (principalId != null) {
            routeHeaderVO.setUserBlanketApprover(false); // default to false
            if (routeHeader.getDocumentType() != null) {
                boolean isBlanketApprover = KEWServiceLocator.getDocumentTypePermissionService().canBlanketApprove(
                        principalId, routeHeader.getDocumentType(), routeHeader.getDocRouteStatus(),
                        routeHeader.getInitiatorWorkflowId());
                routeHeaderVO.setUserBlanketApprover(isBlanketApprover);
            }
            Map<String, String> actionsRequested = KEWServiceLocator.getActionRequestService().getActionsRequested(
                    routeHeader, principalId, true);
            for (String actionRequestCode : actionsRequested.keySet()) {
                if (KEWConstants.ACTION_REQUEST_FYI_REQ.equals(actionRequestCode)) {
                    routeHeaderVO.setFyiRequested(Boolean.parseBoolean(actionsRequested.get(actionRequestCode)));
                } else if (KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ.equals(actionRequestCode)) {
                    routeHeaderVO.setAckRequested(Boolean.parseBoolean(actionsRequested.get(actionRequestCode)));
                } else if (KEWConstants.ACTION_REQUEST_APPROVE_REQ.equals(actionRequestCode)) {
                    routeHeaderVO.setApproveRequested(Boolean.parseBoolean(actionsRequested.get(actionRequestCode)));
                } else {
                    routeHeaderVO.setCompleteRequested(Boolean.parseBoolean(actionsRequested.get(actionRequestCode)));
                }
            }
            // Update notes and notesToDelete arrays in routeHeaderVO
            routeHeaderVO.setNotesToDelete(null);
            routeHeaderVO.setNotes(convertNotesArrayListToNoteVOArray(routeHeader.getNotes()));
        }

        if (principalId != null) {
            Principal principal = KEWServiceLocator.getIdentityHelperService().getPrincipal(principalId);
            routeHeaderVO.setValidActions(convertValidActions(KEWServiceLocator.getActionRegistry().getValidActions(
                    principal, routeHeader)));
        }
        return routeHeaderVO;
    }

    public static ValidActionsDTO convertValidActions(ValidActions validActions) {
        ValidActionsDTO validActionsVO = new ValidActionsDTO();
        for (String actionTakenCode : validActions.getActionTakenCodes()) {
            validActionsVO.addValidActionsAllowed(actionTakenCode);
        }
        return validActionsVO;
    }

    private static void populateRouteHeaderVO(RouteHeaderDTO routeHeaderVO,
            DocumentRouteHeaderValue routeHeader) throws WorkflowException {
        routeHeaderVO.setDocumentId(routeHeader.getDocumentId());
        routeHeaderVO.setAppDocId(routeHeader.getAppDocId());
        routeHeaderVO.setDateApproved(SQLUtils.convertTimestamp(routeHeader.getApprovedDate()));
        routeHeaderVO.setDateCreated(SQLUtils.convertTimestamp(routeHeader.getCreateDate()));
        routeHeaderVO.setDateFinalized(SQLUtils.convertTimestamp(routeHeader.getFinalizedDate()));
        routeHeaderVO.setDateLastModified(SQLUtils.convertTimestamp(routeHeader.getStatusModDate()));
        routeHeaderVO.setAppDocStatus(routeHeader.getAppDocStatus());
        routeHeaderVO.setAppDocStatusDate(SQLUtils.convertTimestamp(routeHeader.getAppDocStatusDate()));

        /**
         * This is the original code which set everything up for lazy loading of document content
         */
        // by default, a non-initialized document content object will be sent so that it can be fetched lazily
        // DocumentContentVO documentContentVO = new DocumentContentVO();
        // documentContentVO.setDocumentId(routeHeader.getDocumentId());
        // routeHeaderVO.setDocumentContent(documentContentVO);
        /**
         * Since we removed the lazy loading in the 2.3 release, this is the code which bypasses lazy loading
         */
        // routeHeaderVO.setDocumentContent(convertDocumentContent(routeHeader.getDocContent(),
        // routeHeader.getDocumentId()));
        routeHeaderVO.setDocRouteLevel(routeHeader.getDocRouteLevel());
        routeHeaderVO.setCurrentRouteNodeNames(routeHeader.getCurrentRouteLevelName());

        /*
         * Collection activeNodes =
         * SpringServiceLocator.getRouteNodeService().getActiveNodeInstances(routeHeaderVO.getDocumentId());
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
        routeHeaderVO.setInitiatorPrincipalId(routeHeader.getInitiatorWorkflowId());
        routeHeaderVO.setRoutedByPrincipalId(routeHeader.getRoutedByUserWorkflowId());

        /* populate the routeHeaderVO with the document variables */
        // FIXME: we assume there is only one for now
        Branch routeNodeInstanceBranch = routeHeader.getRootBranch();
        // Ok, we are using the "branch state" as the arbitrary convenient repository for flow/process/edoc variables
        // so we need to stuff them into the VO
        if (routeNodeInstanceBranch != null) {
            List listOfBranchStates = routeNodeInstanceBranch.getBranchState();
            Iterator it = listOfBranchStates.iterator();
            while (it.hasNext()) {
                BranchState bs = (BranchState) it.next();
                if (bs.getKey() != null && bs.getKey().startsWith(BranchState.VARIABLE_PREFIX)) {
                    LOG.debug("Setting branch state variable on vo: " + bs.getKey() + "=" + bs.getValue());
                    routeHeaderVO.setVariable(bs.getKey().substring(BranchState.VARIABLE_PREFIX.length()),
                            bs.getValue());
                }
            }
        }
    }

    public static DocumentRouteHeaderValue convertRouteHeaderVO(RouteHeaderDTO routeHeaderVO) throws WorkflowException {
        DocumentRouteHeaderValue routeHeader = new DocumentRouteHeaderValue();
        routeHeader.setAppDocId(routeHeaderVO.getAppDocId());
        routeHeader.setApprovedDate(SQLUtils.convertCalendar(routeHeaderVO.getDateApproved()));
        routeHeader.setCreateDate(SQLUtils.convertCalendar(routeHeaderVO.getDateCreated()));
        if (StringUtils.isEmpty(routeHeader.getDocContent())) {
            routeHeader.setDocContent(KEWConstants.DEFAULT_DOCUMENT_CONTENT);
        }
        routeHeader.setDocRouteLevel(routeHeaderVO.getDocRouteLevel());
        routeHeader.setDocRouteStatus(routeHeaderVO.getDocRouteStatus());
        routeHeader.setDocTitle(routeHeaderVO.getDocTitle());
        if (routeHeaderVO.getDocTypeName() != null) {
            DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(
                    routeHeaderVO.getDocTypeName());
            if (documentType == null) {
                throw new RiceRuntimeException(
                        "Could not locate the given document type name: " + routeHeaderVO.getDocTypeName());
            }
            routeHeader.setDocumentTypeId(documentType.getDocumentTypeId());
        }
        routeHeader.setDocVersion(routeHeaderVO.getDocVersion());
        routeHeader.setFinalizedDate(SQLUtils.convertCalendar(routeHeaderVO.getDateFinalized()));
        routeHeader.setInitiatorWorkflowId(routeHeaderVO.getInitiatorPrincipalId());
        routeHeader.setRoutedByUserWorkflowId(routeHeaderVO.getRoutedByPrincipalId());
        routeHeader.setDocumentId(routeHeaderVO.getDocumentId());
        routeHeader.setStatusModDate(SQLUtils.convertCalendar(routeHeaderVO.getDateLastModified()));
        routeHeader.setAppDocStatus(routeHeaderVO.getAppDocStatus());
        routeHeader.setAppDocStatusDate(SQLUtils.convertCalendar(routeHeaderVO.getAppDocStatusDate()));

        // Convert the variables
        List<KeyValue> variables = routeHeaderVO.getVariables();
        if (variables != null && !variables.isEmpty()) {
            for (KeyValue kvp : variables) {
                routeHeader.setVariable(kvp.getKey(), kvp.getValue());
            }
        }

        return routeHeader;
    }

    public static ActionItemDTO convertActionItem(ActionItem actionItem) {
        ActionItemDTO actionItemVO = new ActionItemDTO();
        actionItemVO.setActionItemId(actionItem.getActionItemId());
        actionItemVO.setActionItemIndex(actionItem.getActionItemIndex());
        actionItemVO.setActionRequestCd(actionItem.getActionRequestCd());
        actionItemVO.setActionRequestId(actionItem.getActionRequestId());
        actionItemVO.setActionToTake(actionItem.getActionToTake());
        actionItemVO.setDateAssigned(actionItem.getDateAssigned());
        actionItemVO.setDateAssignedString(actionItem.getDateAssignedString());
        actionItemVO.setDelegationType(actionItem.getDelegationType());
        actionItemVO.setDelegatorPrincipalId(actionItem.getDelegatorWorkflowId());
        actionItemVO.setDelegatorGroupId(actionItem.getDelegatorGroupId());
        actionItemVO.setDocHandlerURL(actionItem.getDocHandlerURL());
        actionItemVO.setDocLabel(actionItem.getDocLabel());
        actionItemVO.setDocName(actionItem.getDocName());
        actionItemVO.setDocTitle(actionItem.getDocTitle());
        actionItemVO.setResponsibilityId(actionItem.getResponsibilityId());
        actionItemVO.setRoleName(actionItem.getRoleName());
        actionItemVO.setDocumentId(actionItem.getDocumentId());
        actionItemVO.setPrincipalId(actionItem.getPrincipalId());
        actionItemVO.setGroupId(actionItem.getGroupId());
        return actionItemVO;
    }

    public static String buildUpdatedDocumentContent(String existingDocContent,
            DocumentContentUpdate documentContentUpdate, String documentTypeName) {
        if (existingDocContent == null) {
            existingDocContent = KEWConstants.DEFAULT_DOCUMENT_CONTENT;
        }
        String documentContent = KEWConstants.DEFAULT_DOCUMENT_CONTENT;
        StandardDocumentContent standardDocContent = new StandardDocumentContent(existingDocContent);
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.newDocument();
            Element root = document.createElement(KEWConstants.DOCUMENT_CONTENT_ELEMENT);
            document.appendChild(root);
            Element applicationContentElement = standardDocContent.getApplicationContent();
            if (documentContentUpdate.getApplicationContent() != null) {
                // application content has changed
                if (!StringUtils.isEmpty(documentContentUpdate.getApplicationContent())) {
                    applicationContentElement = document.createElement(KEWConstants.APPLICATION_CONTENT_ELEMENT);
                    XmlHelper.appendXml(applicationContentElement, documentContentUpdate.getApplicationContent());
                } else {
                    // they've cleared the application content
                    applicationContentElement = null;
                }
            }
            Element attributeContentElement = createDocumentContentSection(document,
                    standardDocContent.getAttributeContent(), documentContentUpdate.getAttributeDefinitions(),
                    documentContentUpdate.getAttributeContent(), KEWConstants.ATTRIBUTE_CONTENT_ELEMENT,
                    documentTypeName);
            Element searchableContentElement = createDocumentContentSection(document,
                    standardDocContent.getSearchableContent(), documentContentUpdate.getSearchableDefinitions(),
                    documentContentUpdate.getSearchableContent(), KEWConstants.SEARCHABLE_CONTENT_ELEMENT,
                    documentTypeName);
            if (applicationContentElement != null) {
                root.appendChild(applicationContentElement);
            }
            if (attributeContentElement != null) {
                root.appendChild(attributeContentElement);
            }
            if (searchableContentElement != null) {
                root.appendChild(searchableContentElement);
            }
            documentContent = XmlJotter.jotNode(document);
        } catch (ParserConfigurationException e) {
            throw new RiceRuntimeException("Failed to initialize XML parser.", e);
        } catch (SAXException e) {
            throw new InvalidDocumentContentException("Failed to parse XML.", e);
        } catch (IOException e) {
            throw new InvalidDocumentContentException("Failed to parse XML.", e);
        } catch (TransformerException e) {
            throw new InvalidDocumentContentException("Failed to parse XML.", e);
        }
        return documentContent;
    }

    private static Element createDocumentContentSection(Document document, Element existingAttributeElement,
            List<WorkflowAttributeDefinition> definitions, String content, String elementName,
            String documentTypeName) throws TransformerException, SAXException, IOException, ParserConfigurationException {
        Element contentSectionElement = existingAttributeElement;
        // if they've updated the content, we're going to re-build the content section element from scratch
        if (content != null) {
            if (!org.apache.commons.lang.StringUtils.isEmpty(content)) {
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
        if (definitions != null && !definitions.isEmpty()) {
            String errorMessage = "";
            boolean inError = false;
            if (contentSectionElement == null) {
                contentSectionElement = document.createElement(elementName);
            }
            for (WorkflowAttributeDefinition definitionVO : definitions) {
                AttributeDefinition definition = convertWorkflowAttributeDefinition(definitionVO);
                RuleAttribute ruleAttribute = definition.getRuleAttribute();
                Object attribute = KEWServiceLocator.getRuleAttributeService().loadRuleAttributeService(ruleAttribute,
                        null);
                if (attribute instanceof XmlConfiguredAttribute) {
                    ((XmlConfiguredAttribute)attribute).setRuleAttribute(ruleAttribute);
                }
                boolean propertiesAsMap = false;
                if (KEWConstants.RULE_XML_ATTRIBUTE_TYPE.equals(ruleAttribute.getType())) {
                    propertiesAsMap = true;
                }
                if (propertiesAsMap) {
                    for (org.kuali.rice.kew.api.document.PropertyDefinition propertyDefinitionVO : definitionVO
                            .getPropertyDefinitions()) {
                        if (attribute instanceof GenericXMLRuleAttribute) {
                            ((GenericXMLRuleAttribute) attribute).getParamMap().put(propertyDefinitionVO.getName(),
                                    propertyDefinitionVO.getValue());
                        }
                    }
                }

                // validate inputs from client application if the attribute is capable
                if (attribute instanceof WorkflowAttributeXmlValidator) {
                    List<WorkflowAttributeValidationError> errors =
                            ((WorkflowAttributeXmlValidator) attribute).validateClientRoutingData();
                    if (!errors.isEmpty()) {
                        inError = true;
                        errorMessage += "Error validating attribute " + definitionVO.getAttributeName() + " ";
                        for (WorkflowAttributeValidationError error : errors) {
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
                    } else if (attribute instanceof SearchableAttributeOld) {
                        // TODO remove this section once we remove SearchableAttributeOld
                        String searcheAttributeContent = ((SearchableAttributeOld) attribute).getSearchContent(
                                DocSearchUtils.getDocumentSearchContext("", documentTypeName, ""));
                        if (!StringUtils.isEmpty(searcheAttributeContent)) {
                            XmlHelper.appendXml(contentSectionElement, searcheAttributeContent);
                        }
                    } else if (attribute instanceof SearchableAttribute) {
                        SearchableAttribute searchableAttribute = (SearchableAttribute) attribute;
                        String searchableAttributeContent = searchableAttribute.generateSearchContent(documentTypeName,
                                definitionVO);
                        if (!StringUtils.isBlank(searchableAttributeContent)) {
                            XmlHelper.appendXml(contentSectionElement, searchableAttributeContent);
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

    /**
     * New for Rice 2.0
     */
    public static AttributeDefinition convertWorkflowAttributeDefinition(WorkflowAttributeDefinition definition) {
        if (definition == null) {
            return null;
        }
        // get the rule attribute so we can get's it's message antity and not blow up if it's remote
        RuleAttribute ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByClassName(
                definition.getAttributeName());
        if (ruleAttribute == null) {
            ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByName(definition.getAttributeName());
        }
        if (ruleAttribute == null) {
            throw new WorkflowRuntimeException("Attribute " + definition.getAttributeName() + " not found");
        }

        ObjectDefinition objectDefinition = new ObjectDefinition(ruleAttribute.getClassName());
        if (definition.getParameters() != null) {
            for (String parameter : definition.getParameters()) {
                objectDefinition.addConstructorParameter(new DataDefinition(parameter, String.class));
            }
        }
        boolean propertiesAsMap = KEWConstants.RULE_XML_ATTRIBUTE_TYPE.equals(ruleAttribute.getType()) || KEWConstants
                .SEARCHABLE_XML_ATTRIBUTE_TYPE.equals(ruleAttribute.getType());
        if (!propertiesAsMap && definition.getPropertyDefinitions() != null) {
            for (org.kuali.rice.kew.api.document.PropertyDefinition propertyDefinition : definition
                    .getPropertyDefinitions()) {
                objectDefinition.addProperty(new PropertyDefinition(propertyDefinition.getName(), new DataDefinition(
                        propertyDefinition.getValue(), String.class)));
            }
        }

        //        // this is likely from an EDL validate call and ME may needed to be added to the AttDefinitionVO.
        //        if (ruleAttribute.getApplicationId() != null) {
        //            definition.setApplicationId(ruleAttribute.getApplicationId());
        //        } else {
        //            // get the me from the document type if it's been passed in - the document is having action taken on it.
        //            if (documentType != null) {
        //                definition.setApplicationId(documentType.getApplicationId());
        //            }
        //        }

        return new AttributeDefinition(ruleAttribute, objectDefinition);
    }

    public static DocumentContentDTO convertDocumentContent(String documentContentValue,
            String documentId) throws WorkflowException {
        if (documentContentValue == null) {
            return null;
        }
        DocumentContentDTO documentContentVO = new DocumentContentDTO();
        // initialize the content fields
        documentContentVO.setApplicationContent("");
        documentContentVO.setAttributeContent("");
        documentContentVO.setSearchableContent("");
        documentContentVO.setDocumentId(documentId);
        try {
            DocumentContent documentContent = new StandardDocumentContent(documentContentValue);
            if (documentContent.getApplicationContent() != null) {
                documentContentVO.setApplicationContent(XmlJotter.jotNode(documentContent.getApplicationContent()));
            }
            if (documentContent.getAttributeContent() != null) {
                documentContentVO.setAttributeContent(XmlJotter.jotNode(documentContent.getAttributeContent()));
            }
            if (documentContent.getSearchableContent() != null) {
                documentContentVO.setSearchableContent(XmlJotter.jotNode(documentContent.getSearchableContent()));
            }
        } catch (Exception e) {
            handleException("Error parsing document content.", e);
        }
        return documentContentVO;
    }

    public static ActionRequestDTO convertActionRequest(ActionRequestValue actionRequest) {
        return convertActionRequest(actionRequest, true);
    }

    protected static ActionRequestDTO convertActionRequest(ActionRequestValue actionRequest,
            boolean includeActionTaken) {
        ActionRequestDTO actionRequestVO = new ActionRequestDTO();
        actionRequestVO.setActionRequested(actionRequest.getActionRequested());
        actionRequestVO.setActionRequestId(actionRequest.getActionRequestId());

        if (includeActionTaken && (actionRequest.getActionTaken() != null)) {
            actionRequestVO.setActionTakenId(actionRequest.getActionTakenId());
            actionRequestVO.setActionTaken(convertActionTaken(actionRequest.getActionTaken()));
        }

        actionRequestVO.setAnnotation(actionRequest.getAnnotation());
        actionRequestVO.setDateCreated(SQLUtils.convertTimestamp(actionRequest.getCreateDate()));
        actionRequestVO.setDocVersion(actionRequest.getDocVersion());
        actionRequestVO.setPrincipalId(actionRequest.getPrincipalId());
        actionRequestVO.setForceAction(actionRequest.getForceAction());
        actionRequestVO.setPriority(actionRequest.getPriority());
        actionRequestVO.setRecipientTypeCd(actionRequest.getRecipientTypeCd());
        actionRequestVO.setResponsibilityDesc(actionRequest.getResponsibilityDesc());
        actionRequestVO.setResponsibilityId(actionRequest.getResponsibilityId());
        actionRequestVO.setDocumentId(actionRequest.getDocumentId());
        actionRequestVO.setRouteLevel(actionRequest.getRouteLevel());
        actionRequestVO.setNodeName(actionRequest.getPotentialNodeName());
        actionRequestVO.setNodeInstanceId((actionRequest.getNodeInstance() == null ? null :
                actionRequest.getNodeInstance().getRouteNodeInstanceId()));
        // TODO delyea - should below be using actionRequest.getRoleName()?
        actionRequestVO.setRoleName(actionRequest.getQualifiedRoleName());
        actionRequestVO.setQualifiedRoleName(actionRequest.getQualifiedRoleName());
        actionRequestVO.setQualifiedRoleNameLabel(actionRequest.getQualifiedRoleNameLabel());
        actionRequestVO.setStatus(actionRequest.getStatus());
        actionRequestVO.setGroupId(actionRequest.getGroupId());
        actionRequestVO.setDelegationType(actionRequest.getDelegationType());
        actionRequestVO.setParentActionRequestId(actionRequest.getParentActionRequestId());
        actionRequestVO.setRequestLabel(actionRequest.getRequestLabel());
        ActionRequestDTO[] childRequestVOs = new ActionRequestDTO[actionRequest.getChildrenRequests().size()];
        int index = 0;
        for (ActionRequestValue childRequest : actionRequest.getChildrenRequests()) {
            ActionRequestDTO childRequestVO = convertActionRequest(childRequest);
            childRequestVOs[index++] = childRequestVO;
        }
        actionRequestVO.setChildrenRequests(childRequestVOs);
        return actionRequestVO;
    }

    public static ActionTakenDTO convertActionTakenWithActionRequests(ActionTakenValue actionTaken) {
        return convertActionTaken(actionTaken, true);
    }

    public static ActionTakenDTO convertActionTaken(ActionTakenValue actionTaken) {
        return convertActionTaken(actionTaken, false);
    }

    protected static ActionTakenDTO convertActionTaken(ActionTakenValue actionTaken, boolean fetchActionRequests) {
        if (actionTaken == null) {
            return null;
        }
        ActionTakenDTO actionTakenVO = new ActionTakenDTO();
        actionTakenVO.setActionDate(SQLUtils.convertTimestamp(actionTaken.getActionDate()));
        actionTakenVO.setActionTaken(actionTaken.getActionTaken());
        actionTakenVO.setActionTakenId(actionTaken.getActionTakenId());
        actionTakenVO.setAnnotation(actionTaken.getAnnotation());
        actionTakenVO.setDocVersion(actionTaken.getDocVersion());
        actionTakenVO.setDocumentId(actionTaken.getDocumentId());
        actionTakenVO.setPrincipalId(actionTaken.getPrincipalId());
        actionTakenVO.setDelegatorPrincpalId(actionTaken.getDelegatorPrincipalId());
        actionTakenVO.setDelegatorGroupId(actionTaken.getDelegatorGroupId());
        if (fetchActionRequests) {
            ActionRequestDTO[] actionRequests = new ActionRequestDTO[actionTaken.getActionRequests().size()];
            int index = 0;
            for (Object element : actionTaken.getActionRequests()) {
                ActionRequestValue actionRequest = (ActionRequestValue) element;
                actionRequests[index++] = convertActionRequest(actionRequest, false);
            }
            actionTakenVO.setActionRequests(actionRequests);
        }
        return actionTakenVO;
    }

    public static ResponsiblePartyDTO convertResponsibleParty(ResponsibleParty responsibleParty) {
        if (responsibleParty == null) {
            return null;
        }
        ResponsiblePartyDTO responsiblePartyVO = new ResponsiblePartyDTO();
        responsiblePartyVO.setGroupId(responsibleParty.getGroupId());
        responsiblePartyVO.setPrincipalId(responsibleParty.getPrincipalId());
        responsiblePartyVO.setRoleName(responsibleParty.getRoleName());
        return responsiblePartyVO;
    }

    public static ResponsibleParty convertResponsiblePartyVO(ResponsiblePartyDTO responsiblePartyVO) {
        if (responsiblePartyVO == null) {
            return null;
        }
        ResponsibleParty responsibleParty = new ResponsibleParty();
        responsibleParty.setGroupId(responsiblePartyVO.getGroupId());
        responsibleParty.setPrincipalId(responsiblePartyVO.getPrincipalId());
        responsibleParty.setRoleName(responsiblePartyVO.getRoleName());
        return responsibleParty;
    }

    /**
     * refactor name to convertResponsiblePartyVO when ResponsibleParty object is gone
     *
     * @param responsiblePartyVO
     * @return
     */
    public static Recipient convertResponsiblePartyVOtoRecipient(ResponsiblePartyDTO responsiblePartyVO) {
        if (responsiblePartyVO == null) {
            return null;
        }
        if (responsiblePartyVO.getRoleName() != null) {
            return new RoleRecipient(responsiblePartyVO.getRoleName());
        }
        String groupId = responsiblePartyVO.getGroupId();
        if (groupId != null) {
            Group group = KimApiServiceLocator.getGroupService().getGroup(groupId);
            if (group == null) {
                throw new RiceRuntimeException("Failed to locate group with ID: " + groupId);
            }
            return new KimGroupRecipient(group);
        }
        String principalId = responsiblePartyVO.getPrincipalId();
        if (principalId != null) {
            return new KimPrincipalRecipient(principalId);
        }
        throw new WorkflowRuntimeException("ResponsibleParty of unknown type");
    }

    /**
     * Interface for a simple service providing RouteNodeInstanceS based on their IDs
     */
    public static interface RouteNodeInstanceLoader {
        RouteNodeInstance load(String routeNodeInstanceID);
    }

    /**
     * Converts an ActionRequestVO to an ActionRequest. The ActionRequestDTO passed in must be the root action request
     * in the
     * graph, otherwise an IllegalArgumentException is thrown. This is to avoid potentially sticky issues with circular
     * references in the conversion. NOTE: This method's primary purpose is to convert ActionRequestVOs returned from a
     * RouteModule. Incidentally, the DTO's returned from the route module will be lacking some information (like the
     * node
     * instance) so no attempts are made to convert this data since further initialization is handled by a higher level
     * component (namely ActionRequestService.initializeActionRequestGraph).
     */
    public static ActionRequestValue convertActionRequestDTO(ActionRequestDTO actionRequestDTO) {
        return convertActionRequestDTO(actionRequestDTO, null);
    }

    /**
     * Converts an ActionRequestVO to an ActionRequest. The ActionRequestDTO passed in must be the root action request
     * in the
     * graph, otherwise an IllegalArgumentException is thrown. This is to avoid potentially sticky issues with circular
     * references in the conversion.
     *
     * @param routeNodeInstanceLoader a service that will provide routeNodeInstanceS based on their IDs.
     */
    public static ActionRequestValue convertActionRequestDTO(ActionRequestDTO actionRequestDTO,
            RouteNodeInstanceLoader routeNodeInstanceLoader) {

        if (actionRequestDTO == null) {
            return null;
        }
        if (actionRequestDTO.getParentActionRequestId() != null) {
            throw new IllegalArgumentException("Cannot convert a non-root ActionRequestVO");
        }
        ActionRequestValue actionRequest = new ActionRequestFactory().createBlankActionRequest();
        populateActionRequest(actionRequest, actionRequestDTO, routeNodeInstanceLoader);
        if (actionRequestDTO.getChildrenRequests() != null) {
            for (int i = 0; i < actionRequestDTO.getChildrenRequests().length; i++) {
                ActionRequestDTO childVO = actionRequestDTO.getChildrenRequests()[i];
                actionRequest.getChildrenRequests().add(convertActionRequestVO(childVO, actionRequest,
                        routeNodeInstanceLoader));
            }
        }
        return actionRequest;
    }

    // TODO: should this be private?  If so, rename to convertActionRequestDTO for consistency.
    protected static ActionRequestValue convertActionRequestVO(ActionRequestDTO actionRequestDTO,
            ActionRequestValue parentActionRequest, RouteNodeInstanceLoader routeNodeInstanceLoader) {
        if (actionRequestDTO == null) {
            return null;
        }
        ActionRequestValue actionRequest = new ActionRequestFactory().createBlankActionRequest();
        populateActionRequest(actionRequest, actionRequestDTO, routeNodeInstanceLoader);
        actionRequest.setParentActionRequest(parentActionRequest);
        actionRequest.setParentActionRequestId(parentActionRequest.getActionRequestId());
        if (actionRequestDTO.getChildrenRequests() != null) {
            for (int i = 0; i < actionRequestDTO.getChildrenRequests().length; i++) {
                ActionRequestDTO childVO = actionRequestDTO.getChildrenRequests()[i];
                actionRequest.getChildrenRequests().add(convertActionRequestVO(childVO, actionRequest,
                        routeNodeInstanceLoader));
            }
        }
        return actionRequest;
    }

    /**
     * This method converts everything except for the parent and child requests
     */
    private static void populateActionRequest(ActionRequestValue actionRequest, ActionRequestDTO actionRequestDTO,
            RouteNodeInstanceLoader routeNodeInstanceLoader) {

        actionRequest.setActionRequested(actionRequestDTO.getActionRequested());
        actionRequest.setActionRequestId(actionRequestDTO.getActionRequestId());
        actionRequest.setActionTakenId(actionRequestDTO.getActionTakenId());
        actionRequest.setAnnotation(actionRequestDTO.getAnnotation());
        actionRequest.setApprovePolicy(actionRequestDTO.getApprovePolicy());
        actionRequest.setCreateDate(new Timestamp(new Date().getTime()));
        actionRequest.setCurrentIndicator(actionRequestDTO.getCurrentIndicator());
        actionRequest.setDelegationType(actionRequestDTO.getDelegationType());
        actionRequest.setDocVersion(actionRequestDTO.getDocVersion());
        actionRequest.setForceAction(actionRequestDTO.getForceAction());
        actionRequest.setPriority(actionRequestDTO.getPriority());
        actionRequest.setQualifiedRoleName(actionRequestDTO.getQualifiedRoleName());
        actionRequest.setQualifiedRoleNameLabel(actionRequestDTO.getQualifiedRoleNameLabel());
        actionRequest.setRecipientTypeCd(actionRequestDTO.getRecipientTypeCd());
        actionRequest.setResponsibilityDesc(actionRequestDTO.getResponsibilityDesc());
        actionRequest.setResponsibilityId(actionRequestDTO.getResponsibilityId());
        actionRequest.setRoleName(actionRequestDTO.getRoleName());
        String documentId = actionRequestDTO.getDocumentId();
        if (documentId != null) {
            actionRequest.setDocumentId(documentId);
            //actionRequest.setRouteHeader(KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentId));
        }
        actionRequest.setRouteLevel(actionRequestDTO.getRouteLevel());

        actionRequest.setStatus(actionRequestDTO.getStatus());
        actionRequest.setPrincipalId(actionRequestDTO.getPrincipalId());
        actionRequest.setGroupId(actionRequestDTO.getGroupId());

        if (routeNodeInstanceLoader != null && actionRequestDTO.getNodeInstanceId() != null) {
            actionRequest.setNodeInstance(routeNodeInstanceLoader.load(actionRequestDTO.getNodeInstanceId()));
        }
    }

    public static ActionTakenValue convertActionTakenVO(ActionTakenDTO actionTakenVO) {
        if (actionTakenVO == null) {
            return null;
        }
        ActionTakenValue actionTaken = new ActionTakenValue();
        actionTaken.setActionDate(new Timestamp(actionTakenVO.getActionDate().getTimeInMillis()));
        actionTaken.setActionTaken(actionTakenVO.getActionTaken());
        actionTaken.setActionTakenId(actionTakenVO.getActionTakenId());
        actionTaken.setAnnotation(actionTakenVO.getAnnotation());
        actionTaken.setCurrentIndicator(Boolean.TRUE);
        actionTaken.setPrincipalId(actionTakenVO.getPrincipalId());
        actionTaken.setDelegatorPrincipalId(actionTakenVO.getDelegatorPrincpalId());
        actionTaken.setDelegatorGroupId(actionTakenVO.getDelegatorGroupId());
        actionTaken.setDocVersion(actionTakenVO.getDocVersion());
        KEWServiceLocator.getRouteHeaderService().getRouteHeader(actionTakenVO.getDocumentId());
        //actionTaken.setRouteHeader(routeHeader);
        actionTaken.setDocumentId(actionTaken.getDocumentId());
        return actionTaken;
    }

    public static DocumentRouteStatusChangeDTO convertDocumentRouteStatusChange(
            DocumentRouteStatusChange statusChange) {
        if (statusChange == null) {
            return null;
        }
        DocumentRouteStatusChangeDTO statusChangeVO = new DocumentRouteStatusChangeDTO();
        statusChangeVO.setDocumentId(statusChange.getDocumentId());
        statusChangeVO.setAppDocId(statusChange.getAppDocId());
        statusChangeVO.setOldRouteStatus(statusChange.getOldRouteStatus());
        statusChangeVO.setNewRouteStatus(statusChange.getNewRouteStatus());
        return statusChangeVO;
    }

    public static DocumentRouteLevelChangeDTO convertDocumentRouteLevelChange(
            DocumentRouteLevelChange routeLevelChange) {
        if (routeLevelChange == null) {
            return null;
        }
        DocumentRouteLevelChangeDTO routeLevelChangeVO = new DocumentRouteLevelChangeDTO();
        routeLevelChangeVO.setDocumentId(routeLevelChange.getDocumentId());
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
        deleteEventVO.setDocumentId(deleteEvent.getDocumentId());
        deleteEventVO.setAppDocId(deleteEvent.getAppDocId());
        return deleteEventVO;
    }

    public static ActionTakenEventDTO convertActionTakenEvent(ActionTakenEvent actionTakenEvent) {
        if (actionTakenEvent == null) {
            return null;
        }
        ActionTakenEventDTO actionTakenEventVO = new ActionTakenEventDTO();
        actionTakenEventVO.setDocumentId(actionTakenEvent.getDocumentId());
        actionTakenEventVO.setAppDocId(actionTakenEvent.getAppDocId());
        actionTakenEventVO.setActionTaken(convertActionTaken(actionTakenEvent.getActionTaken()));
        return actionTakenEventVO;
    }

    public static BeforeProcessEventDTO convertBeforeProcessEvent(BeforeProcessEvent event) {
        if (event == null) {
            return null;
        }
        BeforeProcessEventDTO beforeProcessEvent = new BeforeProcessEventDTO();
        beforeProcessEvent.setDocumentId(event.getDocumentId());
        beforeProcessEvent.setAppDocId(event.getAppDocId());
        beforeProcessEvent.setNodeInstanceId(event.getNodeInstanceId());
        return beforeProcessEvent;
    }

    public static AfterProcessEventDTO convertAfterProcessEvent(AfterProcessEvent event) {
        if (event == null) {
            return null;
        }
        AfterProcessEventDTO afterProcessEvent = new AfterProcessEventDTO();
        afterProcessEvent.setDocumentId(event.getDocumentId());
        afterProcessEvent.setAppDocId(event.getAppDocId());
        afterProcessEvent.setNodeInstanceId(event.getNodeInstanceId());
        afterProcessEvent.setSuccessfullyProcessed(event.isSuccessfullyProcessed());
        return afterProcessEvent;
    }

    public static DocumentLockingEventDTO convertDocumentLockingEvent(DocumentLockingEvent event) {
        if (event == null) {
            return null;
        }
        DocumentLockingEventDTO documentLockingEvent = new DocumentLockingEventDTO();
        documentLockingEvent.setDocumentId(event.getDocumentId());
        documentLockingEvent.setAppDocId(event.getAppDocId());
        return documentLockingEvent;
    }

    public static AttributeDefinition convertWorkflowAttributeDefinitionVO(WorkflowAttributeDefinitionDTO definitionVO,
            org.kuali.rice.kew.doctype.bo.DocumentType documentType) {
        if (definitionVO == null) {
            return null;
        }
        // get the rule attribute so we can get's it's message antity and not blow up if it's remote
        RuleAttribute ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByClassName(
                definitionVO.getAttributeName());
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
        boolean propertiesAsMap = KEWConstants.RULE_XML_ATTRIBUTE_TYPE.equals(ruleAttribute.getType()) || KEWConstants
                .SEARCHABLE_XML_ATTRIBUTE_TYPE.equals(ruleAttribute.getType());
        if (!propertiesAsMap) {
            for (int index = 0; index < definitionVO.getProperties().length; index++) {
                PropertyDefinitionDTO propertyDefVO = definitionVO.getProperties()[index];
                definition.addProperty(new PropertyDefinition(propertyDefVO.getName(), new DataDefinition(
                        propertyDefVO.getValue(), String.class)));
            }
        }

        // this is likely from an EDL validate call and ME may needed to be added to the AttDefinitionVO.
        if (ruleAttribute.getApplicationId() != null) {
            definition.setApplicationId(ruleAttribute.getApplicationId());
        } else {
            // get the me from the document type if it's been passed in - the document is having action taken on it.
            if (documentType != null) {
                definition.setApplicationId(documentType.getApplicationId());
            }
        }

        return new AttributeDefinition(ruleAttribute, definition);
    }

    public static DocumentDetailDTO convertDocumentDetail(
            DocumentRouteHeaderValue routeHeader) throws WorkflowException {
        if (routeHeader == null) {
            return null;
        }
        DocumentDetailDTO detail = new DocumentDetailDTO();
        populateRouteHeaderVO(detail, routeHeader);
        Map nodeInstances = new HashMap();
        List actionRequestVOs = new ArrayList();
        List rootActionRequests = KEWServiceLocator.getActionRequestService().getRootRequests(
                routeHeader.getActionRequests());
        for (Iterator iterator = rootActionRequests.iterator(); iterator.hasNext(); ) {
            ActionRequestValue actionRequest = (ActionRequestValue) iterator.next();
            actionRequestVOs.add(convertActionRequest(actionRequest));
            RouteNodeInstance nodeInstance = actionRequest.getNodeInstance();
            if (nodeInstance == null) {
                continue;
            }
            if (nodeInstance.getRouteNodeInstanceId() == null) {
                throw new WorkflowException(
                        "Error creating document detail structure because of NULL node instance id.");
            }
            nodeInstances.put(nodeInstance.getRouteNodeInstanceId(), nodeInstance);
        }
        detail.setActionRequests((ActionRequestDTO[]) actionRequestVOs.toArray(new ActionRequestDTO[0]));
        List nodeInstanceVOs = new ArrayList();
        for (Iterator iterator = nodeInstances.values().iterator(); iterator.hasNext(); ) {
            RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
            nodeInstanceVOs.add(convertRouteNodeInstance(nodeInstance));
        }
        detail.setNodeInstances((RouteNodeInstanceDTO[]) nodeInstanceVOs.toArray(new RouteNodeInstanceDTO[0]));
        List actionTakenVOs = new ArrayList();
        for (Object element : routeHeader.getActionsTaken()) {
            ActionTakenValue actionTaken = (ActionTakenValue) element;
            actionTakenVOs.add(convertActionTaken(actionTaken));
        }
        detail.setActionsTaken((ActionTakenDTO[]) actionTakenVOs.toArray(new ActionTakenDTO[0]));
        return detail;
    }

    public static DocumentDetail convertDocumentDetailNew(DocumentRouteHeaderValue routeHeader) {
        if (routeHeader == null) {
            return null;
        }
        org.kuali.rice.kew.api.document.Document document = DocumentRouteHeaderValue.to(routeHeader);
        DocumentDetail.Builder detail = DocumentDetail.Builder.create(document);
        Map<String, RouteNodeInstance> nodeInstances = new HashMap<String, RouteNodeInstance>();
        List<ActionRequest> actionRequestVOs = new ArrayList<ActionRequest>();
        List<ActionRequestValue> rootActionRequests = KEWServiceLocator.getActionRequestService().getRootRequests(
                routeHeader.getActionRequests());
        for (Iterator<ActionRequestValue> iterator = rootActionRequests.iterator(); iterator.hasNext(); ) {
            ActionRequestValue actionRequest = iterator.next();
            actionRequestVOs.add(ActionRequestValue.to(actionRequest));
            RouteNodeInstance nodeInstance = actionRequest.getNodeInstance();
            if (nodeInstance == null) {
                continue;
            }
            if (nodeInstance.getRouteNodeInstanceId() == null) {
                throw new IllegalStateException(
                        "Error creating document detail structure because of NULL node instance id.");
            }
            nodeInstances.put(nodeInstance.getRouteNodeInstanceId(), nodeInstance);
        }
        detail.setActionRequests(actionRequestVOs);
        List<org.kuali.rice.kew.api.document.node.RouteNodeInstance> nodeInstanceVOs =
                new ArrayList<org.kuali.rice.kew.api.document.node.RouteNodeInstance>();
        for (Iterator<RouteNodeInstance> iterator = nodeInstances.values().iterator(); iterator.hasNext(); ) {
            RouteNodeInstance nodeInstance = iterator.next();
            nodeInstanceVOs.add(RouteNodeInstance.to(nodeInstance));
        }
        detail.setRouteNodeInstances(nodeInstanceVOs);
        List<ActionTaken> actionTakenVOs = new ArrayList<ActionTaken>();
        for (Object element : routeHeader.getActionsTaken()) {
            ActionTakenValue actionTaken = (ActionTakenValue) element;
            actionTakenVOs.add(ActionTakenValue.to(actionTaken));
        }
        detail.setActionsTaken(actionTakenVOs);
        return detail.build();
    }

    public static RouteNodeInstanceDTO convertRouteNodeInstance(
            RouteNodeInstance nodeInstance) throws WorkflowException {
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
        nodeInstanceVO.setProcessId(
                nodeInstance.getProcess() != null ? nodeInstance.getProcess().getRouteNodeInstanceId() : null);
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
        for (Iterator iterator = states.iterator(); iterator.hasNext(); ) {
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

    public static MovePoint convertMovePointVO(MovePointDTO movePointVO) {
        return MovePoint.create(movePointVO.getStartNodeName(), movePointVO.getStepsToMove());
    }

    /**
     * TODO: Temporary, just to keep compiler happy.  Remove once AdHocRevokeDTO can be removed.
     */
    public static AdHocRevoke convertAdHocRevokeVO(AdHocRevokeDTO revokeVO) throws WorkflowException {
        Set<String> nodeNames = new HashSet<String>();
        Set<String> principalIds = new HashSet<String>();
        Set<String> groupIds = new HashSet<String>();
        if (revokeVO.getNodeName() != null) {
            nodeNames.add(revokeVO.getNodeName());
        }
        if (revokeVO.getPrincipalId() != null) {
            principalIds.add(revokeVO.getPrincipalId());
        }
        if (revokeVO.getGroupId() != null) {
            principalIds.add(revokeVO.getGroupId());
        }
        return AdHocRevoke.create(nodeNames, principalIds, groupIds);
    }

    public static WorkflowAttributeValidationErrorDTO convertWorkflowAttributeValidationError(
            WorkflowAttributeValidationError error) {
        return new WorkflowAttributeValidationErrorDTO(error.getKey(), error.getMessage());
    }

    // Method added for updating notes on server sites based on NoteVO change. Modfy on April 7, 2006
    public static void updateNotes(RouteHeaderDTO routeHeaderVO, String documentId) {
        NoteDTO[] notes = routeHeaderVO.getNotes();
        NoteDTO[] notesToDelete = routeHeaderVO.getNotesToDelete();
        Note noteToDelete = null;
        Note noteToSave = null;

        // Add or update notes to note table based on notes array in RouteHeaderVO
        if (notes != null) {
            for (NoteDTO note : notes) {
                if (note != null) {
                    noteToSave = new Note();
                    noteToSave.setNoteId(note.getNoteId());
                    noteToSave.setDocumentId(documentId);
                    noteToSave.setNoteAuthorWorkflowId(note.getNoteAuthorWorkflowId());
                    noteToSave.setNoteCreateDate(SQLUtils.convertCalendar(note.getNoteCreateDate()));
                    noteToSave.setNoteText(note.getNoteText());
                    noteToSave.setLockVerNbr(note.getLockVerNbr());
                    // if notes[i].getNoteId() == null, add note to note table, otherwise update note to note table
                    getNoteService().saveNote(noteToSave);
                }
            }

        }

        // Delete notes from note table based on notesToDelete array in RouteHeaderVO
        if (notesToDelete != null) {
            for (NoteDTO element : notesToDelete) {
                noteToDelete = getNoteService().getNoteByNoteId(element.getNoteId());
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
            for (Iterator it = notesArrayList.iterator(); it.hasNext(); ) {
                tempNote = (Note) it.next();
                tempNoteVO = new NoteDTO();
                tempNoteVO.setNoteId(tempNote.getNoteId());
                tempNoteVO.setDocumentId(tempNote.getDocumentId());
                tempNoteVO.setNoteAuthorWorkflowId(tempNote.getNoteAuthorWorkflowId());
                tempNoteVO.setNoteCreateDate(SQLUtils.convertTimestamp(tempNote.getNoteCreateDate()));
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

    public static SimulationCriteria convertReportCriteriaDTO(ReportCriteriaDTO criteriaVO) {
        if (criteriaVO == null) {
            return null;
        }
        SimulationCriteria criteria = new SimulationCriteria();
        criteria.setDestinationNodeName(criteriaVO.getTargetNodeName());
        criteria.setDocumentId(criteriaVO.getDocumentId());
        criteria.setDocumentTypeName(criteriaVO.getDocumentTypeName());
        criteria.setXmlContent(criteriaVO.getXmlContent());
        criteria.setActivateRequests(criteriaVO.getActivateRequests());
        criteria.setFlattenNodes(criteriaVO.isFlattenNodes());
        if (criteriaVO.getRoutingPrincipalId() != null) {
            Principal kPrinc = KEWServiceLocator.getIdentityHelperService().getPrincipal(
                    criteriaVO.getRoutingPrincipalId());
            Person user = KimApiServiceLocator.getPersonService().getPerson(kPrinc.getPrincipalId());
            if (user == null) {
                throw new RiceRuntimeException(
                        "Could not locate user for the given id: " + criteriaVO.getRoutingPrincipalId());
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
        if (criteriaVO.getTargetPrincipalIds() != null) {
            for (String targetPrincipalId : criteriaVO.getTargetPrincipalIds()) {
                Principal principal = KEWServiceLocator.getIdentityHelperService().getPrincipal(targetPrincipalId);
                criteria.getDestinationRecipients().add(new KimPrincipalRecipient(principal));
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

    public static SimulationActionToTake convertReportActionToTakeVO(ReportActionToTakeDTO actionToTakeVO) {
        if (actionToTakeVO == null) {
            return null;
        }
        SimulationActionToTake actionToTake = new SimulationActionToTake();
        actionToTake.setNodeName(actionToTakeVO.getNodeName());
        if (StringUtils.isBlank(actionToTakeVO.getActionToPerform())) {
            throw new IllegalArgumentException("ReportActionToTakeVO must contain an action taken code and does not");
        }
        actionToTake.setActionToPerform(actionToTakeVO.getActionToPerform());
        if (actionToTakeVO.getPrincipalId() == null) {
            throw new IllegalArgumentException("ReportActionToTakeVO must contain a principalId and does not");
        }
        Principal kPrinc = KEWServiceLocator.getIdentityHelperService().getPrincipal(actionToTakeVO.getPrincipalId());
        Person user = KimApiServiceLocator.getPersonService().getPerson(kPrinc.getPrincipalId());
        if (user == null) {
            throw new RiceRuntimeException(
                    "Could not locate Person for the given id: " + actionToTakeVO.getPrincipalId());
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

    public static Collection<RuleExtensionDTO> convertRuleExtension(
            RuleExtension ruleExtension) throws WorkflowException {
        if (ruleExtension == null) {
            return null;
        }
        List<RuleExtensionDTO> extensionVOs = new ArrayList<RuleExtensionDTO>();
        for (Object element : ruleExtension.getExtensionValues()) {
            RuleExtensionValue extensionValue = (RuleExtensionValue) element;
            extensionVOs.add(new RuleExtensionDTO(extensionValue.getKey(), extensionValue.getValue()));
        }
        return extensionVOs;
    }

    public static KeyValue convertRuleExtensionVO(RuleExtensionDTO ruleExtensionVO) throws WorkflowException {
        if (ruleExtensionVO == null) {
            return null;
        }
        return new ConcreteKeyValue(ruleExtensionVO.getKey(), ruleExtensionVO.getValue());
    }

    public static RuleResponsibilityDTO convertRuleResponsibility(
            RuleResponsibility ruleResponsibility) throws WorkflowException {
        if (ruleResponsibility == null) {
            return null;
        }
        RuleResponsibilityDTO ruleResponsibilityVO = new RuleResponsibilityDTO();
        ruleResponsibilityVO.setActionRequestedCd(ruleResponsibility.getActionRequestedCd());
        ruleResponsibilityVO.setApprovePolicy(ruleResponsibility.getApprovePolicy());
        ruleResponsibilityVO.setPriority(ruleResponsibility.getPriority());
        ruleResponsibilityVO.setResponsibilityId(ruleResponsibility.getResponsibilityId());
        ruleResponsibilityVO.setRoleName(ruleResponsibility.getRole());
        if (ruleResponsibility.getPrincipal() != null) {
            ruleResponsibilityVO.setPrincipalId(ruleResponsibility.getPrincipal().getPrincipalId());
        } else if (ruleResponsibility.getGroup() != null) {
            ruleResponsibilityVO.setGroupId(ruleResponsibility.getGroup().getId());
        } else if (ruleResponsibility.getRole() != null) {
            ruleResponsibilityVO.setRoleName(ruleResponsibility.getRole());
        }
        for (Object element : ruleResponsibility.getDelegationRules()) {
            RuleDelegation ruleDelegation = (RuleDelegation) element;
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
        rule.setForceAction(ruleValues.getForceAction());
        rule.setRuleTemplateId(ruleValues.getRuleTemplateId());
        rule.setRuleTemplateName(ruleValues.getRuleTemplateName());

        // get keyPair values to setup RuleExtensionVOs
        for (Object element : ruleValues.getRuleExtensions()) {
            RuleExtension ruleExtension = (RuleExtension) element;
            rule.addRuleExtensions(convertRuleExtension(ruleExtension));
        }
        // get keyPair values to setup RuleExtensionVOs
        for (Object element : ruleValues.getResponsibilities()) {
            RuleResponsibility ruleResponsibility = (RuleResponsibility) element;
            rule.addRuleResponsibility(convertRuleResponsibility(ruleResponsibility));
        }
        return rule;
    }

    public static DocSearchCriteriaDTO convertDocumentSearchCriteriaDTO(
            DocumentSearchCriteriaDTO criteriaVO) throws WorkflowException {
        DocSearchCriteriaDTO criteria = new DocSearchCriteriaDTO();
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
        criteria.setIsAdvancedSearch(
                (criteriaVO.isAdvancedSearch()) ? DocSearchCriteriaDTO.ADVANCED_SEARCH_INDICATOR_STRING : "NO");
        criteria.setSuperUserSearch(
                (criteriaVO.isSuperUserSearch()) ? DocSearchCriteriaDTO.SUPER_USER_SEARCH_INDICATOR_STRING : "NO");
        criteria.setDocumentId(criteriaVO.getDocumentId());
        criteria.setViewer(criteriaVO.getViewer());
        criteria.setWorkgroupViewerName(criteriaVO.getGroupViewerName());
        criteria.setToDateApproved(criteriaVO.getToDateApproved());
        criteria.setToDateCreated(criteriaVO.getToDateCreated());
        criteria.setToDateFinalized(criteriaVO.getToDateFinalized());
        criteria.setToDateLastModified(criteriaVO.getToDateLastModified());
        criteria.setThreshold(criteriaVO.getThreshold());
        criteria.setSaveSearchForUser(criteriaVO.isSaveSearchForUser());

        // generate the route node criteria
        if ((StringUtils.isNotBlank(criteriaVO.getDocRouteNodeName())) && (StringUtils.isBlank(
                criteriaVO.getDocTypeFullName()))) {
            throw new WorkflowException("No document type name specified when attempting to search by route node name '"
                    + criteriaVO.getDocRouteNodeName()
                    + "'");
        } else if ((StringUtils.isNotBlank(criteriaVO.getDocRouteNodeName())) && (StringUtils.isNotBlank(
                criteriaVO.getDocTypeFullName()))) {
            criteria.setDocRouteNodeLogic(criteriaVO.getDocRouteNodeLogic());
            List routeNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(getDocumentTypeByName(
                    criteria.getDocTypeFullName()), true);
            boolean foundRouteNode = false;
            for (Iterator iterator = routeNodes.iterator(); iterator.hasNext(); ) {
                RouteNode routeNode = (RouteNode) iterator.next();
                if (criteriaVO.getDocRouteNodeName().equals(routeNode.getRouteNodeName())) {
                    foundRouteNode = true;
                    break;
                }
            }
            if (!foundRouteNode) {
                throw new WorkflowException("Could not find route node name '"
                        + criteriaVO.getDocRouteNodeName()
                        + "' for document type name '"
                        + criteriaVO.getDocTypeFullName()
                        + "'");
            }
            criteria.setDocRouteNodeId(criteriaVO.getDocRouteNodeName());
        }

        // build a map of the search attributes passed in from the client creating lists where keys are duplicated
        HashMap<String, List<String>> searchAttributeValues = new HashMap<String, List<String>>();
        for (KeyValue keyValueVO : criteriaVO.getSearchAttributeValues()) {
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
                container = new SearchAttributeFormContainer(key, (String[]) values.toArray());
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

    public static DocumentSearchResultDTO convertDocumentSearchResultComponents(
            DocumentSearchResultComponents searchResult) throws WorkflowException {
        DocumentSearchResultDTO resultsVO = new DocumentSearchResultDTO();
        resultsVO.setSearchResults(convertDocumentSearchResults(searchResult.getSearchResults()));
        return resultsVO;
    }

    private static List<DocumentSearchResultRowDTO> convertDocumentSearchResults(
            List<DocumentSearchResult> searchResults) throws WorkflowException {
        List<DocumentSearchResultRowDTO> rowVOs = new ArrayList<DocumentSearchResultRowDTO>();
        for (DocumentSearchResult documentSearchResult : searchResults) {
            rowVOs.add(convertDocumentSearchResult(documentSearchResult));
        }
        return rowVOs;
    }

    public static DocumentSearchResultRowDTO convertDocumentSearchResult(
            DocumentSearchResult resultRow) throws WorkflowException {
        DocumentSearchResultRowDTO rowVO = new DocumentSearchResultRowDTO();
        List<ConcreteKeyValue> fieldValues = new ArrayList<ConcreteKeyValue>();
        for (KeyValueSort keyValueSort : resultRow.getResultContainers()) {
            fieldValues.add(new ConcreteKeyValue(keyValueSort.getKey(), keyValueSort.getUserDisplayValue()));
        }
        rowVO.setFieldValues(fieldValues);
        return rowVO;
    }

    public static DocumentStatusTransitionDTO convertDocumentStatusTransition(
            DocumentStatusTransition transition) throws WorkflowException {
        DocumentStatusTransitionDTO tranVO = new DocumentStatusTransitionDTO();
        tranVO.setStatusTransitionId(transition.getStatusTransitionId());
        tranVO.setDocumentId(transition.getDocumentId());
        tranVO.setOldAppDocStatus(transition.getOldAppDocStatus());
        tranVO.setNewAppDocStatus(transition.getNewAppDocStatus());
        tranVO.setStatusTransitionDate(transition.getStatusTransitionDate());
        return tranVO;
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

    //convert DocumentLink beans to array of DocumentLinkDTO
    public static DocumentLinkDTO[] convertDocumentLink(Collection<DocumentLink> links) {
        if (links == null) {
            return null;
        }
        DocumentLinkDTO[] docLinkVOs = new DocumentLinkDTO[links.size()];

        int index = 0;

        for (DocumentLink link : links) {
            docLinkVOs[index++] = convertDocumentLink(link);
        }
        return docLinkVOs;
    }

    //convert DocumentLink beans to list of DocumentLinkDTO
    public static List<DocumentLinkDTO> convertDocumentLinkToArrayList(Collection<DocumentLink> links) {
        if (links == null) {
            return null;
        }
        List<DocumentLinkDTO> docLinkVOs = new ArrayList<DocumentLinkDTO>(links.size());

        for (DocumentLink link : links) {
            docLinkVOs.add(convertDocumentLink(link));
        }
        return docLinkVOs;
    }

    //covert DocumentLink bean to DocumentLinkDTO
    public static DocumentLinkDTO convertDocumentLink(DocumentLink link) {
        if (link == null) {
            return null;
        }
        DocumentLinkDTO linkVO = new DocumentLinkDTO();
        linkVO.setLinbkId(link.getDocLinkId());
        linkVO.setOrgnDocId(link.getOrgnDocId());
        linkVO.setDestDocId(link.getDestDocId());

        return linkVO;
    }

}
