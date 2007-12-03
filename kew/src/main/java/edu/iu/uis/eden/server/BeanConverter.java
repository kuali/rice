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
package edu.iu.uis.eden.server;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.kuali.rice.definition.DataDefinition;
import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.definition.PropertyDefinition;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.iu.uis.eden.ActionTakenEvent;
import edu.iu.uis.eden.DocumentRouteLevelChange;
import edu.iu.uis.eden.DocumentRouteStatusChange;
import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestFactory;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.actions.AdHocRevoke;
import edu.iu.uis.eden.actions.MovePoint;
import edu.iu.uis.eden.actions.ValidActions;
import edu.iu.uis.eden.actiontaken.ActionTakenValue;
import edu.iu.uis.eden.clientapp.DeleteEvent;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO;
import edu.iu.uis.eden.clientapp.vo.ActionTakenVO;
import edu.iu.uis.eden.clientapp.vo.AdHocRevokeVO;
import edu.iu.uis.eden.clientapp.vo.DeleteEventVO;
import edu.iu.uis.eden.clientapp.vo.DocumentContentVO;
import edu.iu.uis.eden.clientapp.vo.DocumentDetailVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteStatusChangeVO;
import edu.iu.uis.eden.clientapp.vo.DocumentTypeVO;
import edu.iu.uis.eden.clientapp.vo.EmplIdVO;
import edu.iu.uis.eden.clientapp.vo.MovePointVO;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.clientapp.vo.NoteVO;
import edu.iu.uis.eden.clientapp.vo.ProcessVO;
import edu.iu.uis.eden.clientapp.vo.PropertyDefinitionVO;
import edu.iu.uis.eden.clientapp.vo.ReportActionToTakeVO;
import edu.iu.uis.eden.clientapp.vo.ReportCriteriaVO;
import edu.iu.uis.eden.clientapp.vo.ResponsiblePartyVO;
import edu.iu.uis.eden.clientapp.vo.RouteHeaderVO;
import edu.iu.uis.eden.clientapp.vo.RouteNodeInstanceVO;
import edu.iu.uis.eden.clientapp.vo.RouteNodeVO;
import edu.iu.uis.eden.clientapp.vo.RoutePathVO;
import edu.iu.uis.eden.clientapp.vo.RouteTemplateEntryVO;
import edu.iu.uis.eden.clientapp.vo.RuleDelegationVO;
import edu.iu.uis.eden.clientapp.vo.RuleExtensionVO;
import edu.iu.uis.eden.clientapp.vo.RuleResponsibilityVO;
import edu.iu.uis.eden.clientapp.vo.RuleVO;
import edu.iu.uis.eden.clientapp.vo.StateVO;
import edu.iu.uis.eden.clientapp.vo.UserIdVO;
import edu.iu.uis.eden.clientapp.vo.UserVO;
import edu.iu.uis.eden.clientapp.vo.UuIdVO;
import edu.iu.uis.eden.clientapp.vo.ValidActionsVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeDefinitionVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowAttributeValidationErrorVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowGroupIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupNameIdVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupVO;
import edu.iu.uis.eden.definition.AttributeDefinition;
import edu.iu.uis.eden.docsearch.SearchableAttribute;
import edu.iu.uis.eden.docsearch.xml.GenericXMLSearchableAttribute;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.engine.CompatUtils;
import edu.iu.uis.eden.engine.node.BranchState;
import edu.iu.uis.eden.engine.node.KeyValuePair;
import edu.iu.uis.eden.engine.node.Process;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.engine.node.State;
import edu.iu.uis.eden.engine.simulation.SimulationActionToTake;
import edu.iu.uis.eden.engine.simulation.SimulationCriteria;
import edu.iu.uis.eden.exception.DocumentTypeNotFoundException;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.notes.Note;
import edu.iu.uis.eden.notes.NoteService;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttributeXmlValidator;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routeheader.StandardDocumentContent;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleDelegation;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;
import edu.iu.uis.eden.routetemplate.RuleResponsibility;
import edu.iu.uis.eden.routetemplate.WorkflowAttributeValidationError;
import edu.iu.uis.eden.routetemplate.xmlrouting.GenericXMLRuleAttribute;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.EmplId;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.RoleRecipient;
import edu.iu.uis.eden.user.UserId;
import edu.iu.uis.eden.user.UuId;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.util.ResponsibleParty;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.util.XmlHelper;
import edu.iu.uis.eden.workgroup.GroupId;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * Translates Workflow server side beans into client side VO beans.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BeanConverter {
    private static final Logger LOG = Logger.getLogger(BeanConverter.class);

    public static RouteHeaderVO convertRouteHeader(DocumentRouteHeaderValue routeHeader, WorkflowUser user) throws WorkflowException, EdenUserNotFoundException {
        RouteHeaderVO routeHeaderVO = new RouteHeaderVO();
        if (routeHeader == null) {
            return null;
        }
        populateRouteHeaderVO(routeHeaderVO, routeHeader);

        if (user != null) {
        	routeHeaderVO.setUserBlanketApprover(false);  // default to false
        	if (routeHeader.getDocumentType() != null) {
                routeHeaderVO.setUserBlanketApprover(routeHeader.getDocumentType().isUserBlanketApprover(user));
        	}
            String topActionRequested = EdenConstants.ACTION_REQUEST_FYI_REQ;
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

    public static RouteHeaderVO convertActionListRouteHeader(DocumentRouteHeaderValue routeHeader, WorkflowUser user) throws WorkflowException, EdenUserNotFoundException {
        RouteHeaderVO routeHeaderVO = new RouteHeaderVO();
        if (routeHeader == null) {
            return null;
        }
        populateRouteHeaderVO(routeHeaderVO, routeHeader);

        if (user != null) {
        	routeHeaderVO.setUserBlanketApprover(false);  // default to false
        	if (routeHeader.getDocumentType() != null) {
                routeHeaderVO.setUserBlanketApprover(routeHeader.getDocumentType().isUserBlanketApprover(user));
        	}
            String topActionRequested = EdenConstants.ACTION_REQUEST_FYI_REQ;
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

    public static ValidActionsVO convertValidActions(ValidActions validActions) {
        ValidActionsVO validActionsVO = new ValidActionsVO();
        for (Iterator iter = validActions.getActionTakenCodes().iterator(); iter.hasNext();) {
            String actionTakenCode = (String) iter.next();
            validActionsVO.addValidActionsAllowed(actionTakenCode);
        }
        return validActionsVO;
    }

    // private static void populateActionListRouteHeaderVO(RouteHeaderVO routeHeaderVO, DocumentRouteHeaderValue routeHeader) throws WorkflowException {
    // routeHeaderVO.setAppDocId(routeHeader.getAppDocId());
    // routeHeaderVO.setDateApproved(Utilities.convertTimestamp(routeHeader.getApprovedDate()));
    // routeHeaderVO.setDateCreated(Utilities.convertTimestamp(routeHeader.getCreateDate()));
    // routeHeaderVO.setDateFinalized(Utilities.convertTimestamp(routeHeader.getFinalizedDate()));
    // routeHeaderVO.setDateLastModified(Utilities.convertTimestamp(routeHeader.getStatusModDate()));
    // //routeHeaderVO.setDocumentContent(convertDocumentContent(routeHeader.getDocContent()));
    // routeHeaderVO.setDocRouteLevel(routeHeader.getDocRouteLevel());
    //
    // Collection activeNodes = SpringServiceLocator.getRouteNodeService().getActiveNodeInstances(routeHeaderVO.getRouteHeaderId());
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

    private static void populateRouteHeaderVO(RouteHeaderVO routeHeaderVO, DocumentRouteHeaderValue routeHeader) throws WorkflowException {
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
        //DocumentContentVO documentContentVO = new DocumentContentVO();
        //documentContentVO.setRouteHeaderId(routeHeader.getRouteHeaderId());
        //routeHeaderVO.setDocumentContent(documentContentVO);

        /**
         * Since we removed the lazy loading in the 2.3 release, this is the code which bypasses lazy loading
         */
        //routeHeaderVO.setDocumentContent(convertDocumentContent(routeHeader.getDocContent(), routeHeader.getRouteHeaderId()));

        routeHeaderVO.setDocRouteLevel(routeHeader.getDocRouteLevel());
        routeHeaderVO.setCurrentRouteNodeNames(routeHeader.getCurrentRouteLevelName());

        /*Collection activeNodes = SpringServiceLocator.getRouteNodeService().getActiveNodeInstances(routeHeaderVO.getRouteHeaderId());
        routeHeaderVO.setNodeNames(new String[activeNodes.size()]);
        int index = 0;
        for (Iterator iterator = activeNodes.iterator(); iterator.hasNext();) {
            RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
            routeHeaderVO.getNodeNames()[index++] = nodeInstance.getRouteNode().getRouteNodeName();
        }*/

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
        routeHeaderVO.setOverrideInd(routeHeader.getOverrideInd());

        /* populate the routeHeaderVO with the document variables */
        // FIXME: we assume there is only one for now
        RouteNodeInstance routeNodeInstance = (RouteNodeInstance) routeHeader.getInitialRouteNodeInstance(0);
        // Ok, we are using the "branch state" as the arbitrary convenient repository for flow/process/edoc variables
        // so we need to stuff them into the VO
        if (routeNodeInstance.getBranch() != null) {
            List listOfBranchStates = routeNodeInstance.getBranch().getBranchState();
            Iterator it = listOfBranchStates.iterator();
            while (it.hasNext()) {
                BranchState bs = (BranchState)  it.next();
                if (bs.getKey() != null && bs.getKey().startsWith(BranchState.VARIABLE_PREFIX)) {
                    LOG.debug("Setting branch state variable on vo: " + bs.getKey() + "=" + bs.getValue());
                    routeHeaderVO.setVariable(bs.getKey().substring(BranchState.VARIABLE_PREFIX.length()), bs.getValue());
                }
            }
        }
    }

    public static DocumentRouteHeaderValue convertRouteHeaderVO(RouteHeaderVO routeHeaderVO) throws WorkflowException, EdenUserNotFoundException {
        DocumentRouteHeaderValue routeHeader = new DocumentRouteHeaderValue();
        routeHeader.setAppDocId(routeHeaderVO.getAppDocId());
        routeHeader.setApprovedDate(Utilities.convertCalendar(routeHeaderVO.getDateApproved()));
        routeHeader.setCreateDate(Utilities.convertCalendar(routeHeaderVO.getDateCreated()));
        //String updatedDocumentContent = buildUpdatedDocumentContent(routeHeaderVO);
        // if null is returned from this method it indicates that the document content on the route header
        // contained no changes, since we are creating a new document here, we will default the
        // document content approriately if no changes are detected on the incoming DocumentContentVO
        //if (updatedDocumentContent != null) {
        //	routeHeader.setDocContent(updatedDocumentContent);
        //} else {
        //	routeHeader.setDocContent(EdenConstants.DEFAULT_DOCUMENT_CONTENT);
        //}
        if (StringUtils.isEmpty(routeHeader.getDocContent())) {
        	routeHeader.setDocContent(EdenConstants.DEFAULT_DOCUMENT_CONTENT);
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
        routeHeader.setOverrideInd(routeHeaderVO.getOverrideInd());
        routeHeader.setRouteHeaderId(routeHeaderVO.getRouteHeaderId());
        routeHeader.setStatusModDate(Utilities.convertCalendar(routeHeaderVO.getDateLastModified()));

        return routeHeader;
    }

//    private static boolean hasDocumentContentChanged(DocumentContentVO documentContentVO) {
//    	return documentContentVO.getApplicationContent() != null ||
//    		documentContentVO.getAttributeContent() != null ||
//    		documentContentVO.getSearchableContent() != null ||
//    		documentContentVO.getAttributeDefinitions().length > 0 ||
//    		documentContentVO.getSearchableDefinitions().length > 0;
//    }

    /**
     * Converts the given DocumentContentVO to a document content string.  This method considers existing
     * content on the document and updates approriately.  The string returned will be the new document
     * content for the document.  If null is returned, then the document content is unchanged.
     */
//    public static String buildUpdatedDocumentContent(RouteHeaderVO routeHeaderVO) throws WorkflowException {
//    	// prevent the document content from auto materializing when we are looking through it
//
//    	//documentContentVO.turnOffMaterialization();
//    	DocumentContentVO documentContentVO = routeHeaderVO.getDocumentContent();
//    	// if the document content hasn't changed, no need to eagerly update it
//    	if (!hasDocumentContentChanged(documentContentVO)) {
//    		return null;
//    	}
//        String documentContent = EdenConstants.DEFAULT_DOCUMENT_CONTENT;
//        try {
//        	// parse the existing content on the document
//        	String existingDocContent = EdenConstants.DEFAULT_DOCUMENT_CONTENT;
//        	if (documentContentVO.getRouteHeaderId() != null) {
//        		DocumentRouteHeaderValue document = SpringServiceLocator.getRouteHeaderService().getRouteHeader(documentContentVO.getRouteHeaderId());
//        		existingDocContent = document.getDocContent();
//        	}
//        	StandardDocumentContent standardDocContent = new StandardDocumentContent(existingDocContent);
//        	DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//        	Document document = builder.newDocument();
//        	Element root = document.createElement(EdenConstants.DOCUMENT_CONTENT_ELEMENT);
//        	document.appendChild(root);
//        	Element applicationContentElement = standardDocContent.getApplicationContent();
//        	if (documentContentVO.getApplicationContent() != null) {
//        		// application content has changed
//            	if (!Utilities.isEmpty(documentContentVO.getApplicationContent())) {
//            		applicationContentElement = document.createElement(EdenConstants.APPLICATION_CONTENT_ELEMENT);
//            		XmlHelper.appendXml(applicationContentElement, documentContentVO.getApplicationContent());
//            	} else {
//            		// they've cleared the application content
//            		applicationContentElement = null;
//            	}
//        	}
//        	Element attributeContentElement = createDocumentContentSection(document, standardDocContent.getAttributeContent(), documentContentVO.getAttributeDefinitions(), documentContentVO.getAttributeContent(), EdenConstants.ATTRIBUTE_CONTENT_ELEMENT, routeHeaderVO);
//        	Element searchableContentElement = createDocumentContentSection(document, standardDocContent.getSearchableContent(), documentContentVO.getSearchableDefinitions(), documentContentVO.getSearchableContent(), EdenConstants.SEARCHABLE_CONTENT_ELEMENT, routeHeaderVO);
//        	if (applicationContentElement != null) {
//        		root.appendChild(applicationContentElement);
//        	}
//        	if (attributeContentElement != null) {
//        		root.appendChild(attributeContentElement);
//        	}
//        	if (searchableContentElement != null) {
//        		root.appendChild(searchableContentElement);
//        	}
//        	documentContent = XmlHelper.writeNode(document);
//        } catch (Exception e) {
//            handleException("Error parsing document content.", e);
//        }
//        return documentContent;
//    }

  public static String buildUpdatedDocumentContent(DocumentContentVO documentContentVO) throws WorkflowException {
    	// if the document content hasn't changed, no need to eagerly update it
//		if (!hasDocumentContentChanged(documentContentVO)) {
//			return null;
//		}
		DocumentType documentType = null;
        String documentContent = EdenConstants.DEFAULT_DOCUMENT_CONTENT;
        try {
        	// parse the existing content on the document
        	String existingDocContent = EdenConstants.DEFAULT_DOCUMENT_CONTENT;
        	if (documentContentVO.getRouteHeaderId() != null) {
        		DocumentRouteHeaderValue document = KEWServiceLocator.getRouteHeaderService().getRouteHeader(documentContentVO.getRouteHeaderId());
				documentType = document.getDocumentType();
        		existingDocContent = document.getDocContent();
        	}
        	StandardDocumentContent standardDocContent = new StandardDocumentContent(existingDocContent);
        	DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        	Document document = builder.newDocument();
        	Element root = document.createElement(EdenConstants.DOCUMENT_CONTENT_ELEMENT);
        	document.appendChild(root);
        	Element applicationContentElement = standardDocContent.getApplicationContent();
        	if (documentContentVO.getApplicationContent() != null) {
        		// application content has changed
            	if (!Utilities.isEmpty(documentContentVO.getApplicationContent())) {
            		applicationContentElement = document.createElement(EdenConstants.APPLICATION_CONTENT_ELEMENT);
            		XmlHelper.appendXml(applicationContentElement, documentContentVO.getApplicationContent());
            	} else {
            		// they've cleared the application content
            		applicationContentElement = null;
            	}
        	}
			Element attributeContentElement = createDocumentContentSection(document, standardDocContent.getAttributeContent(), documentContentVO.getAttributeDefinitions(), documentContentVO.getAttributeContent(), EdenConstants.ATTRIBUTE_CONTENT_ELEMENT, documentType);
			Element searchableContentElement = createDocumentContentSection(document, standardDocContent.getSearchableContent(), documentContentVO.getSearchableDefinitions(), documentContentVO.getSearchableContent(), EdenConstants.SEARCHABLE_CONTENT_ELEMENT, documentType);
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

    private static Element createDocumentContentSection(Document document, Element existingAttributeElement, WorkflowAttributeDefinitionVO[] definitions, String content, String elementName, DocumentType documentType) throws Exception {
    	Element contentSectionElement = existingAttributeElement;
    	// if they've updated the content, we're going to re-build the content section element from scratch
    	if (content != null) {
    		if (!Utilities.isEmpty(content)) {
        		contentSectionElement = document.createElement(elementName);
    			// if they didn't merely clear the content, let's build the content section element by combining the children of the incoming XML content
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
            	WorkflowAttributeDefinitionVO definitionVO = definitions[index];
            	AttributeDefinition definition = convertWorkflowAttributeDefinitionVO(definitionVO, documentType);
            	RuleAttribute ruleAttribute = definition.getRuleAttribute();
                Object attribute = GlobalResourceLoader.getResourceLoader().getObject(definition.getObjectDefinition());
                boolean propertiesAsMap = false;
                if (EdenConstants.RULE_XML_ATTRIBUTE_TYPE.equals(ruleAttribute.getType())) {
                	((GenericXMLRuleAttribute) attribute).setRuleAttribute(ruleAttribute);
                	propertiesAsMap = true;
                } else if (EdenConstants.SEARCHABLE_XML_ATTRIBUTE_TYPE.equals(ruleAttribute.getType())) {
                	((GenericXMLSearchableAttribute) attribute).setRuleAttribute(ruleAttribute);
                	propertiesAsMap = true;
                }
                if (propertiesAsMap) {
                	for (PropertyDefinitionVO propertyDefinitionVO : definitionVO.getProperties()) {
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
    		// always be sure and import the element into the new document, if it originated from the existing doc content and
    		// appended to it, it will need to be imported
    		contentSectionElement = (Element)document.importNode(contentSectionElement, true);
    	}
        return contentSectionElement;
    }

    public static DocumentContentVO convertDocumentContent(String documentContentValue, Long documentId) throws WorkflowException {
        if (documentContentValue == null) {
            return null;
        }
        DocumentContentVO documentContentVO = new DocumentContentVO();
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

    public static WorkgroupVO convertWorkgroup(Workgroup workgroup) {
        if (workgroup == null) {
            return null;
        }
        WorkgroupVO workgroupVO = new WorkgroupVO();
        workgroupVO.setActiveInd(workgroup.getActiveInd().booleanValue());
        workgroupVO.setDescription(workgroup.getDescription());
        workgroupVO.setWorkgroupId(workgroup.getWorkflowGroupId().getGroupId());
        workgroupVO.setWorkgroupName(workgroup.getGroupNameId().getNameId());
        workgroupVO.setWorkgroupType(workgroup.getWorkgroupType());
        if (workgroup.getUsers() != null) {
            workgroupVO.setMembers(new UserVO[workgroup.getUsers().size()]);
            int index = 0;
            for (Iterator iterator = workgroup.getUsers().iterator(); iterator.hasNext(); index++) {
                WorkflowUser user = (WorkflowUser) iterator.next();
                workgroupVO.getMembers()[index] = convertUser(user);
            }
        }
        return workgroupVO;
    }

    public static UserVO convertUser(WorkflowUser user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        userVO.setNetworkId(user.getAuthenticationUserId() == null ? null : user.getAuthenticationUserId().getAuthenticationId());
        userVO.setUuId(user.getUuId() == null ? null : user.getUuId().getUuId());
        userVO.setEmplId(user.getEmplId() == null ? null : user.getEmplId().getEmplId());
        userVO.setWorkflowId(user.getWorkflowUserId() == null ? null : user.getWorkflowUserId().getWorkflowId());
        userVO.setDisplayName(user.getDisplayName());
        userVO.setLastName(user.getLastName());
        userVO.setFirstName(user.getGivenName());
        userVO.setEmailAddress(user.getEmailAddress());
        // Preferences preferences = SpringServiceLocator.getPreferencesService().getPreferences(user);
        // userVO.setUserPreferencePopDocHandler(EdenConstants.PREFERENCES_YES_VAL.equals(preferences.getOpenNewWindow()));

        userVO.setUserPreferencePopDocHandler(true);
        return userVO;
    }

    public static WorkflowUser convertUserVO(UserVO userVO) throws EdenUserNotFoundException {
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
            throw new EdenUserNotFoundException("Cannot convert the given UserVO, it does not contain any valid user ids.");
        }
        return KEWServiceLocator.getUserService().getWorkflowUser(userId);
    }

    public static DocumentTypeVO convertDocumentType(DocumentType docType) {
        DocumentTypeVO docTypeVO = new DocumentTypeVO();
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
            docTypeVO.setDocTypeCurrentInd(EdenConstants.ACTIVE_CD);
        } else {
            docTypeVO.setDocTypeCurrentInd(EdenConstants.INACTIVE_CD);
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
            RouteTemplateEntryVO[] templates = new RouteTemplateEntryVO[nodes.size()];
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

    public static RouteTemplateEntryVO convertRouteTemplateEntry(RouteNode node) {
        RouteTemplateEntryVO entryVO = new RouteTemplateEntryVO();
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

    public static RoutePathVO convertRoutePath(DocumentType documentType) {
        RoutePathVO routePath = new RoutePathVO();
        ProcessVO[] processes = new ProcessVO[documentType.getProcesses().size()];
        int index = 0;
        for (Iterator iterator = documentType.getProcesses().iterator(); iterator.hasNext();) {
            Process process = (Process) iterator.next();
            processes[index++] = convertProcess(process);
        }
        routePath.setProcesses(processes);
        return routePath;
    }

    public static ActionRequestVO convertActionRequest(ActionRequestValue actionRequest) throws EdenUserNotFoundException {
        // TODO some newly added actionrequest properties are not here (delegation stuff)
        ActionRequestVO actionRequestVO = new ActionRequestVO();
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
//        actionRequestVO.setRouteMethodName(actionRequest.getRouteMethodName());
//      TODO delyea - should below be using actionRequest.getRoleName()?
        actionRequestVO.setRoleName(actionRequest.getQualifiedRoleName());
        actionRequestVO.setQualifiedRoleName(actionRequest.getQualifiedRoleName());
        actionRequestVO.setQualifiedRoleNameLabel(actionRequest.getQualifiedRoleNameLabel());
        actionRequestVO.setStatus(actionRequest.getStatus());
        if (actionRequest.isWorkgroupRequest()) {
            actionRequestVO.setWorkgroupId(actionRequest.getWorkgroupId());
            actionRequestVO.setWorkgroupVO(convertWorkgroup(actionRequest.getWorkgroup()));
        }
        ActionRequestVO[] childRequestVOs = new ActionRequestVO[actionRequest.getChildrenRequests().size()];
        int index = 0;
        for (Iterator iterator = actionRequest.getChildrenRequests().iterator(); iterator.hasNext();) {
            ActionRequestValue childRequest = (ActionRequestValue) iterator.next();
            ActionRequestVO childRequestVO = convertActionRequest(childRequest);
            childRequestVO.setParentActionRequest(actionRequestVO);
            childRequestVOs[index++] = childRequestVO;
        }
        actionRequestVO.setChildrenRequests(childRequestVOs);
        return actionRequestVO;
    }

    public static ActionTakenVO convertActionTaken(ActionTakenValue actionTaken) throws EdenUserNotFoundException {
    	if (actionTaken == null) {
    		return null;
    	}
        ActionTakenVO actionTakenVO = new ActionTakenVO();
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

    public static WorkgroupIdVO convertGroupId(GroupId groupId) {
        WorkgroupIdVO workgroupId = null;
        if (groupId instanceof GroupNameId) {
            GroupNameId groupName = (GroupNameId) groupId;
            workgroupId = new WorkgroupNameIdVO(groupName.getNameId());
        } else if (groupId instanceof WorkflowGroupId) {
            WorkflowGroupId workflowGroupId = (WorkflowGroupId) groupId;
            workgroupId = new WorkflowGroupIdVO(workflowGroupId.getGroupId());
        }
        return workgroupId;
    }

    public static GroupId convertWorkgroupIdVO(WorkgroupIdVO workgroupId) {
        GroupId groupId = null;
        if (workgroupId instanceof WorkgroupNameIdVO) {
            WorkgroupNameIdVO workgroupName = (WorkgroupNameIdVO) workgroupId;
            groupId = new GroupNameId(workgroupName.getWorkgroupName());
        } else if (workgroupId instanceof WorkflowGroupIdVO) {
            WorkflowGroupIdVO workflowGroupId = (WorkflowGroupIdVO) workgroupId;
            groupId = new WorkflowGroupId(workflowGroupId.getWorkgroupId());
        }

        return groupId;
    }

    public static UserIdVO convertUserId(UserId userId) {
        UserIdVO userIdVO = null;
        if (userId instanceof AuthenticationUserId) {
            AuthenticationUserId id = (AuthenticationUserId) userId;
            userIdVO = new NetworkIdVO(id.getAuthenticationId());
        } else if (userId instanceof EmplId) {
            EmplId id = (EmplId) userId;
            userIdVO = new EmplIdVO(id.getEmplId());
        } else if (userId instanceof UuId) {
            UuId id = (UuId) userId;
            userIdVO = new UuIdVO(id.getUuId());
        } else if (userId instanceof WorkflowUserId) {
            WorkflowUserId id = (WorkflowUserId) userId;
            userIdVO = new WorkflowIdVO(id.getWorkflowId());
        }
        return userIdVO;
    }

    public static UserId convertUserIdVO(UserIdVO userIdVO) {
        UserId userId = null;
        if (userIdVO instanceof NetworkIdVO) {
            NetworkIdVO id = (NetworkIdVO) userIdVO;
            userId = new AuthenticationUserId(id.getNetworkId());
            if (userId.isEmpty()) {
                throw new RuntimeException("Attempting to use empty NetworkId");
            }
        } else if (userIdVO instanceof EmplIdVO) {
            EmplIdVO id = (EmplIdVO) userIdVO;
            userId = new EmplId(id.getEmplId());
            if (userId.isEmpty()) {
                throw new RuntimeException("Attempting to use empty EmplId");
            }
        } else if (userIdVO instanceof UuIdVO) {
            UuIdVO id = (UuIdVO) userIdVO;
            userId = new UuId(id.getUuId());
            if (userId.isEmpty()) {
                throw new RuntimeException("Attempting to use empty UuId");
            }
        } else if (userIdVO instanceof WorkflowIdVO) {
            WorkflowIdVO id = (WorkflowIdVO) userIdVO;
            userId = new WorkflowUserId(id.getWorkflowId());
            if (userId.isEmpty()) {
                throw new RuntimeException("Attempting to use empty WorkflowId");
            }
        }
        return userId;
    }

    public static ResponsiblePartyVO convertResponsibleParty(ResponsibleParty responsibleParty) {
        if (responsibleParty == null) {
            return null;
        }
        ResponsiblePartyVO responsiblePartyVO = new ResponsiblePartyVO();
        responsiblePartyVO.setWorkgroupId(BeanConverter.convertGroupId(responsibleParty.getGroupId()));
        responsiblePartyVO.setUserId(BeanConverter.convertUserId(responsibleParty.getUserId()));
        responsiblePartyVO.setRoleName(responsibleParty.getRoleName());
        return responsiblePartyVO;
    }

    public static ResponsibleParty convertResponsiblePartyVO(ResponsiblePartyVO responsiblePartyVO) {
        if (responsiblePartyVO == null) {
            return null;
        }
        ResponsibleParty responsibleParty = new ResponsibleParty();
        responsibleParty.setGroupId(BeanConverter.convertWorkgroupIdVO(responsiblePartyVO.getWorkgroupId()));
        responsibleParty.setUserId(BeanConverter.convertUserIdVO(responsiblePartyVO.getUserId()));
        responsibleParty.setRoleName(responsiblePartyVO.getRoleName());
        return responsibleParty;
    }

    /**
     * refactor name to convertResponsiblePartyVO when ResponsibleParty object is gone
     * @param responsiblePartyVO
     * @return
     * @throws EdenUserNotFoundException
     */
    public static Recipient convertResponsiblePartyVOtoRecipient(ResponsiblePartyVO responsiblePartyVO) throws EdenUserNotFoundException {
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
     * Converts an ActionRequestVO to an ActionRequest. The ActionRequestVO passed in must be the root action request in the graph, otherwise an IllegalArgumentException is thrown. This is to avoid potentially sticky issues with circular references in the conversion. NOTE: This method's primary purpose is to convert ActionRequestVOs returned from a RouteModule. Incidentally, the VO's returned from the route module will be lacking some information (like the node instance) so no attempts are made to convert this data since further initialization is handled by a higher level component (namely ActionRequestService.initializeActionRequestGraph).
     */
    public static ActionRequestValue convertActionRequestVO(ActionRequestVO actionRequestVO) throws EdenUserNotFoundException {
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
                ActionRequestVO childVO = actionRequestVO.getChildrenRequests()[i];
                actionRequest.getChildrenRequests().add(convertActionRequestVO(childVO, actionRequest));
            }
        }
        return actionRequest;
    }

    public static ActionRequestValue convertActionRequestVO(ActionRequestVO actionRequestVO, ActionRequestValue parentActionRequest) throws EdenUserNotFoundException {
        if (actionRequestVO == null) {
            return null;
        }
        ActionRequestValue actionRequest = new ActionRequestFactory().createBlankActionRequest();
        populateActionRequest(actionRequest, actionRequestVO);
        actionRequest.setParentActionRequest(parentActionRequest);
        actionRequest.setParentActionRequestId(parentActionRequest.getActionRequestId());
        if (actionRequestVO.getChildrenRequests() != null) {
            for (int i = 0; i < actionRequestVO.getChildrenRequests().length; i++) {
                ActionRequestVO childVO = actionRequestVO.getChildrenRequests()[i];
                actionRequest.getChildrenRequests().add(convertActionRequestVO(childVO, actionRequest));
            }
        }
        return actionRequest;
    }

    /**
     * This method converts everything except for the parent and child requests
     */
    private static void populateActionRequest(ActionRequestValue actionRequest, ActionRequestVO actionRequestVO) throws EdenUserNotFoundException {

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
//        actionRequest.setRouteMethodName(actionRequestVO.getRouteMethodName());
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

    public static ActionTakenValue convertActionTakenVO(ActionTakenVO actionTakenVO) throws EdenUserNotFoundException {
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

    public static DocumentRouteStatusChangeVO convertDocumentRouteStatusChange(DocumentRouteStatusChange statusChange) {
        if (statusChange == null) {
            return null;
        }
        DocumentRouteStatusChangeVO statusChangeVO = new DocumentRouteStatusChangeVO();
        statusChangeVO.setRouteHeaderId(statusChange.getRouteHeaderId());
        statusChangeVO.setAppDocId(statusChange.getAppDocId());
        statusChangeVO.setOldRouteStatus(statusChange.getOldRouteStatus());
        statusChangeVO.setNewRouteStatus(statusChange.getNewRouteStatus());
        return statusChangeVO;
    }

    public static DocumentRouteLevelChangeVO convertDocumentRouteLevelChange(DocumentRouteLevelChange routeLevelChange) {
        if (routeLevelChange == null) {
            return null;
        }
        DocumentRouteLevelChangeVO routeLevelChangeVO = new DocumentRouteLevelChangeVO();
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

    public static DeleteEventVO convertDeleteEvent(DeleteEvent deleteEvent) {
        if (deleteEvent == null) {
            return null;
        }
        DeleteEventVO deleteEventVO = new DeleteEventVO();
        deleteEventVO.setRouteHeaderId(deleteEvent.getRouteHeaderId());
        deleteEventVO.setAppDocId(deleteEvent.getAppDocId());
        return deleteEventVO;
    }

    public static ActionTakenEventVO convertActionTakenEvent(ActionTakenEvent actionTakenEvent) throws EdenUserNotFoundException {
        if (actionTakenEvent == null) {
            return null;
        }
        ActionTakenEventVO actionTakenEventVO = new ActionTakenEventVO();
        actionTakenEventVO.setRouteHeaderId(actionTakenEvent.getRouteHeaderId());
        actionTakenEventVO.setAppDocId(actionTakenEvent.getAppDocId());
        actionTakenEventVO.setActionTaken(convertActionTaken(actionTakenEvent.getActionTaken()));
        return actionTakenEventVO;
    }

    public static AttributeDefinition convertWorkflowAttributeDefinitionVO(WorkflowAttributeDefinitionVO definitionVO, edu.iu.uis.eden.doctype.DocumentType documentType) {
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
        boolean propertiesAsMap = EdenConstants.RULE_XML_ATTRIBUTE_TYPE.equals(ruleAttribute.getType()) ||
        	EdenConstants.SEARCHABLE_XML_ATTRIBUTE_TYPE.equals(ruleAttribute.getType());
        if (!propertiesAsMap) {
        	for (int index = 0; index < definitionVO.getProperties().length; index++) {
        		PropertyDefinitionVO propertyDefVO = definitionVO.getProperties()[index];
        		definition.addProperty(new PropertyDefinition(propertyDefVO.getName(), new DataDefinition(propertyDefVO.getValue(), String.class)));
        	}
        }

        //this is likely from an EDL validate call and ME may needed to be added to the AttDefinitionVO.
        if (ruleAttribute.getMessageEntity() != null) {
        	definition.setMessageEntity(ruleAttribute.getMessageEntity());
        } else {
        	//get the me from the document type if it's been passed in - the document is having action taken on it.
        	if (documentType != null) {
        		definition.setMessageEntity(documentType.getMessageEntity());
        	}
        }

        return new AttributeDefinition(ruleAttribute, definition);
    }

    public static DocumentDetailVO convertDocumentDetail(DocumentRouteHeaderValue routeHeader) throws WorkflowException {
        if (routeHeader == null) {
            return null;
        }
        DocumentDetailVO detail = new DocumentDetailVO();
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
        detail.setActionRequests((ActionRequestVO[]) actionRequestVOs.toArray(new ActionRequestVO[0]));
        List nodeInstanceVOs = new ArrayList();
        for (Iterator iterator = nodeInstances.values().iterator(); iterator.hasNext();) {
            RouteNodeInstance nodeInstance = (RouteNodeInstance) iterator.next();
            nodeInstanceVOs.add(convertRouteNodeInstance(nodeInstance));
        }
        detail.setNodeInstances((RouteNodeInstanceVO[]) nodeInstanceVOs.toArray(new RouteNodeInstanceVO[0]));
        List actionTakenVOs = new ArrayList();
        for (Iterator iterator = routeHeader.getActionsTaken().iterator(); iterator.hasNext();) {
            ActionTakenValue actionTaken = (ActionTakenValue) iterator.next();
            actionTakenVOs.add(convertActionTaken(actionTaken));
        }
        detail.setActionsTaken((ActionTakenVO[]) actionTakenVOs.toArray(new ActionTakenVO[0]));
        return detail;
    }

    public static RouteNodeInstanceVO convertRouteNodeInstance(RouteNodeInstance nodeInstance) throws WorkflowException {
        if (nodeInstance == null) {
            return null;
        }
        RouteNodeInstanceVO nodeInstanceVO = new RouteNodeInstanceVO();
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

        nodeInstanceVO.setNextNodes(new RouteNodeInstanceVO[nodeInstance.getNextNodeInstances().size()]);
        int i = 0;
        for (Iterator iter = nodeInstance.getNextNodeInstances().iterator(); iter.hasNext(); i++) {
            RouteNodeInstance nextNodeInstance = (RouteNodeInstance) iter.next();
            nodeInstanceVO.getNextNodes()[i] = convertRouteNodeInstance(nextNodeInstance);
        }

        return nodeInstanceVO;
    }

    public static StateVO[] convertStates(Collection states) {
        if (states == null) {
            return null;
        }
        StateVO[] stateVOs = new StateVO[states.size()];
        int index = 0;
        for (Iterator iterator = states.iterator(); iterator.hasNext();) {
            State state = (State) iterator.next();
            stateVOs[index++] = convertState(state);
        }
        return stateVOs;
    }

    public static StateVO convertState(State nodeState) {
        if (nodeState == null) {
            return null;
        }
        StateVO stateVO = new StateVO();
        stateVO.setStateId(nodeState.getStateId());
        stateVO.setKey(nodeState.getKey());
        stateVO.setValue(nodeState.getValue());
        return stateVO;
    }

    public static RouteNodeVO convertRouteNode(RouteNode node) {
        if (node == null) {
            return null;
        }
        RouteNodeVO nodeVO = new RouteNodeVO();
        nodeVO.setActivationType(node.getActivationType());
        nodeVO.setBranchName(node.getBranch() != null ? node.getBranch().getName() : null);
        nodeVO.setDocumentTypeId(node.getDocumentTypeId());
        try {
            nodeVO.setExceptionWorkgroup(convertWorkgroup(node.getExceptionWorkgroup()));
        } catch (EdenUserNotFoundException e) {
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

    public static ProcessVO convertProcess(Process process) {
        ProcessVO processVO = new ProcessVO();
        processVO.setInitial(process.isInitial());
        processVO.setInitialRouteNode(convertRouteNode(process.getInitialRouteNode()));
        processVO.setName(process.getName());
        processVO.setProcessId(process.getProcessId());
        return processVO;
    }

    public static MovePoint convertMovePointVO(MovePointVO movePointVO) {
        MovePoint movePoint = new MovePoint();
        movePoint.setStartNodeName(movePointVO.getStartNodeName());
        movePoint.setStepsToMove(movePointVO.getStepsToMove());
        return movePoint;
    }

    public static AdHocRevoke convertAdHocRevokeVO(AdHocRevokeVO revokeVO) throws WorkflowException {
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

    public static WorkflowAttributeValidationErrorVO convertWorkflowAttributeValidationError(WorkflowAttributeValidationError error) {
        return new WorkflowAttributeValidationErrorVO(error.getKey(), error.getMessage());
    }

    //Method added for updating notes on server sites based on NoteVO change. Modfy on April 7, 2006
    public static void updateNotes (RouteHeaderVO routeHeaderVO, Long routeHeaderId){
    	NoteVO[] notes = routeHeaderVO.getNotes();
    	NoteVO[] notesToDelete = routeHeaderVO.getNotesToDelete();
    	Note noteToDelete = null;
    	Note noteToSave = null;

    	// Add or update notes to note table based on notes array in RouteHeaderVO
    	if (notes != null){
    		for (int i=0; i<notes.length; i++){
    			if (notes[i]!=null){
	    			noteToSave = new Note();
	    			noteToSave.setNoteId(notes[i].getNoteId());
	    			noteToSave.setRouteHeaderId(routeHeaderId);
	    			noteToSave.setNoteAuthorWorkflowId(notes[i].getNoteAuthorWorkflowId());
	    			noteToSave.setNoteCreateDate(Utilities.convertCalendar(notes[i].getNoteCreateDate()));
	    			noteToSave.setNoteText(notes[i].getNoteText());
	    			noteToSave.setLockVerNbr(notes[i].getLockVerNbr());
//	    			 if notes[i].getNoteId() == null, add note to note table, otherwise update note to note table
	    			getNoteService().saveNote(noteToSave);
    			}
    		}

    	}

    	//  Delete notes from note table based on notesToDelete array in RouteHeaderVO
    	if (notesToDelete != null){
    		for (int i=0; i< notesToDelete.length; i++){
    			noteToDelete = getNoteService().getNoteByNoteId(notesToDelete[i].getNoteId());
    			if (noteToDelete != null){
    				getNoteService().deleteNote(noteToDelete);
    			}
    		}
    		routeHeaderVO.setNotesToDelete(null);
    	}
    }

    private static NoteService getNoteService() {
        return (NoteService) KEWServiceLocator.getService(KEWServiceLocator.NOTE_SERVICE);
    }

    private static NoteVO[] convertNotesArrayListToNoteVOArray (List notesArrayList){
    	if (notesArrayList.size()>0){
    		NoteVO[] noteVOArray = new NoteVO[notesArrayList.size()];
    		int i = 0;
    		Note tempNote;
    		NoteVO tempNoteVO;
    		for (Iterator it = notesArrayList.iterator(); it.hasNext();){
    			tempNote = (Note)it.next();
    			tempNoteVO = new NoteVO();
    			tempNoteVO.setNoteId(tempNote.getNoteId());
    			tempNoteVO.setRouteHeaderId(tempNote.getRouteHeaderId());
    			tempNoteVO.setNoteAuthorWorkflowId(tempNote.getNoteAuthorWorkflowId());
    			tempNoteVO.setNoteCreateDate(Utilities.convertTimestamp(tempNote.getNoteCreateDate()));
    			tempNoteVO.setNoteText(tempNote.getNoteText());
    			tempNoteVO.setLockVerNbr(tempNote.getLockVerNbr());
    			noteVOArray[i]= tempNoteVO;
    			i++;
    		}
    		return noteVOArray;
    	}else {
    		return null;
    	}
    }

    public static SimulationCriteria convertReportCriteriaVO(ReportCriteriaVO criteriaVO) throws EdenUserNotFoundException {
    	if (criteriaVO == null) {
    		return null;
    	}
    	SimulationCriteria criteria = new SimulationCriteria();
    	criteria.setDestinationNodeName(criteriaVO.getTargetNodeName());
    	criteria.setDocumentId(criteriaVO.getRouteHeaderId());
    	criteria.setDocumentTypeName(criteriaVO.getDocumentTypeName());
    	criteria.setXmlContent(criteriaVO.getXmlContent());
    	if (criteriaVO.getRoutingUser() != null) {
			WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(criteriaVO.getRoutingUser());
			if (user == null) {
				throw new EdenUserNotFoundException("Could not locate user for the given id: " + criteriaVO.getRoutingUser());
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
    			UserIdVO userIdVO = criteriaVO.getTargetUsers()[index];
    			WorkflowUser user = KEWServiceLocator.getUserService().getWorkflowUser(userIdVO);
    			if (user == null) {
    				throw new EdenUserNotFoundException("Could not locate user for the given id: " + userIdVO);
    			}
    			criteria.getDestinationRecipients().add(user);
    		}
    	}
    	if (criteriaVO.getActionsToTake() != null) {
    		for (int index = 0; index < criteriaVO.getActionsToTake().length; index++) {
    			ReportActionToTakeVO actionToTakeVO = criteriaVO.getActionsToTake()[index];
    			criteria.getActionsToTake().add(convertReportActionToTakeVO(actionToTakeVO));
			}
    	}
    	return criteria;
    }

    public static SimulationActionToTake convertReportActionToTakeVO(ReportActionToTakeVO actionToTakeVO) throws EdenUserNotFoundException {
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
			throw new EdenUserNotFoundException("Could not locate user for the given id: " + actionToTakeVO.getUserIdVO());
		}
		actionToTake.setUser(user);
		return actionToTake;
    }

    public static RuleDelegationVO convertRuleDelegation(RuleDelegation ruleDelegation) throws WorkflowException {
        if (ruleDelegation == null) {
            return null;
        }
        RuleDelegationVO ruleDelegationVO = new RuleDelegationVO();
        ruleDelegationVO.setDelegationType(ruleDelegation.getDelegationType());
        ruleDelegationVO.setDelegationRule(convertRule(ruleDelegation.getDelegationRuleBaseValues()));
        return ruleDelegationVO;
    }

//    public static RuleDelegation convertRuleExtensionVO(RuleExtensionVO ruleExtensionVO) throws WorkflowException {}

    public static Collection<RuleExtensionVO> convertRuleExtension(RuleExtension ruleExtension) throws WorkflowException {
        if (ruleExtension == null) {
            return null;
        }
        List<RuleExtensionVO> extensionVOs =  new ArrayList<RuleExtensionVO>();
        for (Iterator iter = ruleExtension.getExtensionValues().iterator(); iter.hasNext();) {
            RuleExtensionValue extensionValue = (RuleExtensionValue) iter.next();
            extensionVOs.add(new RuleExtensionVO(extensionValue.getKey(),extensionValue.getValue()));
        }
        return extensionVOs;
    }

    public static KeyValuePair convertRuleExtensionVO(RuleExtensionVO ruleExtensionVO) throws WorkflowException {
        if (ruleExtensionVO == null) {
            return null;
        }
        return new KeyValuePair(ruleExtensionVO.getKey(),ruleExtensionVO.getValue());
    }

    public static RuleResponsibilityVO convertRuleResponsibility(RuleResponsibility ruleResponsibility) throws WorkflowException {
        if (ruleResponsibility == null) {
            return null;
        }
        RuleResponsibilityVO ruleResponsibilityVO = new RuleResponsibilityVO();
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

//    public static KeyValuePair convertRuleResponsibilityVO(RuleResponsibilityVO ruleResponsibilityVO) throws WorkflowException {}

    public static RuleVO convertRule(RuleBaseValues ruleValues) throws WorkflowException {
        if (ruleValues == null) {
            return null;
        }
        RuleVO rule = new RuleVO();
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