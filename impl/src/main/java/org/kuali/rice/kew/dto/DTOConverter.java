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
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.xml.XmlHelper;
import org.kuali.rice.core.api.util.xml.XmlJotter;
import org.kuali.rice.core.framework.persistence.jdbc.sql.SQLUtils;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.KimGroupRecipient;
import org.kuali.rice.kew.actionrequest.KimPrincipalRecipient;
import org.kuali.rice.kew.actionrequest.Recipient;
import org.kuali.rice.kew.actions.ValidActions;
import org.kuali.rice.kew.actiontaken.ActionTakenValue;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.api.action.ActionRequest;
import org.kuali.rice.kew.api.action.ActionTaken;
import org.kuali.rice.kew.api.action.AdHocRevoke;
import org.kuali.rice.kew.api.document.DocumentContentUpdate;
import org.kuali.rice.kew.api.document.DocumentDetail;
import org.kuali.rice.kew.api.document.InvalidDocumentContentException;
import org.kuali.rice.kew.api.document.attribute.WorkflowAttributeDefinition;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kew.api.extension.ExtensionUtils;
import org.kuali.rice.kew.definition.AttributeDefinition;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.engine.node.Branch;
import org.kuali.rice.kew.engine.node.BranchState;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.engine.node.State;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.framework.document.attribute.SearchableAttribute;
import org.kuali.rice.kew.notes.Note;
import org.kuali.rice.kew.notes.service.NoteService;
import org.kuali.rice.kew.postprocessor.ActionTakenEvent;
import org.kuali.rice.kew.postprocessor.AfterProcessEvent;
import org.kuali.rice.kew.postprocessor.BeforeProcessEvent;
import org.kuali.rice.kew.postprocessor.DeleteEvent;
import org.kuali.rice.kew.postprocessor.DocumentLockingEvent;
import org.kuali.rice.kew.postprocessor.DocumentRouteLevelChange;
import org.kuali.rice.kew.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.StandardDocumentContent;
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
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
                ExtensionDefinition extensionDefinition = definition.getExtensionDefinition();
                Object attribute = ExtensionUtils.loadExtension(extensionDefinition);

                // TODO - Rice 2.0 - Remove this once we have eliminated XmlConfiguredAttribute
                if (attribute instanceof XmlConfiguredAttribute) {
                    ((XmlConfiguredAttribute)attribute).setRuleAttribute(definition.getRuleAttribute());
                }
                boolean propertiesAsMap = false;
                if (KEWConstants.RULE_XML_ATTRIBUTE_TYPE.equals(extensionDefinition.getType())) {
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
                    } else if (attribute instanceof SearchableAttribute) {
                        SearchableAttribute searchableAttribute = (SearchableAttribute) attribute;
                        String searchableAttributeContent = searchableAttribute.generateSearchContent(extensionDefinition, documentTypeName,
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
        ExtensionDefinition extensionDefinition = KewApiServiceLocator.getExtensionRepositoryService().getExtensionByName(definition.getAttributeName());
        if (extensionDefinition == null) {
            throw new WorkflowRuntimeException("Extension " + definition.getAttributeName() + " not found");
        }
        RuleAttribute ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByName(definition.getAttributeName());
        if (ruleAttribute == null) {
            throw new WorkflowRuntimeException("Attribute " + definition.getAttributeName() + " not found");
        }

        ObjectDefinition objectDefinition = new ObjectDefinition(extensionDefinition.getResourceDescriptor());
        if (definition.getParameters() != null) {
            for (String parameter : definition.getParameters()) {
                objectDefinition.addConstructorParameter(new DataDefinition(parameter, String.class));
            }
        }
        boolean propertiesAsMap = KEWConstants.RULE_XML_ATTRIBUTE_TYPE.equals(extensionDefinition.getType()) || KEWConstants
                .SEARCHABLE_XML_ATTRIBUTE_TYPE.equals(extensionDefinition.getType());
        if (!propertiesAsMap && definition.getPropertyDefinitions() != null) {
            for (org.kuali.rice.kew.api.document.PropertyDefinition propertyDefinition : definition
                    .getPropertyDefinitions()) {
                objectDefinition.addProperty(new PropertyDefinition(propertyDefinition.getName(), new DataDefinition(
                        propertyDefinition.getValue(), String.class)));
            }
        }

        return new AttributeDefinition(ruleAttribute, extensionDefinition, objectDefinition);
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
        actionTakenEventVO.setActionTaken(ActionTakenValue.to(actionTakenEvent.getActionTaken()));
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

    private static DocumentType getDocumentTypeByName(String documentTypeName) {
        return KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
    }

    private static void handleException(String message, Exception e) throws WorkflowException {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else if (e instanceof WorkflowException) {
            throw (WorkflowException) e;
        }
        throw new WorkflowException(message, e);
    }

}
