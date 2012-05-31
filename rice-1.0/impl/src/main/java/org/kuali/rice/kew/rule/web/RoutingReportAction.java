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
package org.kuali.rice.kew.rule.web;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.engine.ActivationContext;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.routeheader.AttributeDocumentContent;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routelog.web.RouteLogAction;
import org.kuali.rice.kew.routelog.web.RouteLogForm;
import org.kuali.rice.kew.rule.FlexRM;
import org.kuali.rice.kew.rule.WorkflowAttribute;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttribute;
import org.kuali.rice.kew.rule.service.RuleTemplateService;
import org.kuali.rice.kew.rule.xmlrouting.GenericXMLRuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.web.KewKualiAction;
import org.kuali.rice.kew.web.session.UserSession;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;


/**
 * A Struts Action for executing routing reports and retrieving the results.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RoutingReportAction extends KewKualiAction {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RoutingReportAction.class);

	public static final String DOC_TYPE_REPORTING = "documentType";
	public static final String TEMPLATE_REPORTING = "template";

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        this.initiateForm(request, form);
        RoutingReportForm routingForm = (RoutingReportForm)form;
        if (Utilities.isEmpty(routingForm.getDateRef())) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            routingForm.setEffectiveHour("5");
            routingForm.setEffectiveMinute("0");
            routingForm.setAmPm("1");
            routingForm.setDateRef(sdf.format(new Date()));
            routingForm.setReportType(TEMPLATE_REPORTING);
        }
        if (DOC_TYPE_REPORTING.equals(routingForm.getReportType())) {
            if (Utilities.isEmpty(routingForm.getDocumentTypeParam())) {
                throw new RuntimeException("No document type was given");
            }
            if (Utilities.isEmpty(routingForm.getInitiatorPrincipalId())) {
                throw new RuntimeException("No initiator principal id was given");
            }
            if (Utilities.isEmpty(routingForm.getDocumentContent())) {
                throw new RuntimeException("No document content was given");
            }
        } else if (!(TEMPLATE_REPORTING.equals(routingForm.getReportType()))) {
            // report type is not Document Type or Template Type... error out
            throw new RuntimeException("The Routing Report type is not set");
        }
        return super.execute(mapping, form, request, response);
    }

	public ActionForward calculateRoute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		RoutingReportForm routingForm = (RoutingReportForm) form;

		List errors = new ArrayList();

		if (getDocumentTypeService().findByName(routingForm.getDocumentType()) == null) {
		    GlobalVariables.getMessageMap().putError("Document type is required.", "doctype.documenttypeservice.doctypename.required");
		}
		Timestamp date = null;
		if (!Utilities.isEmpty(routingForm.getDateRef())) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			try {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(sdf.parse(routingForm.getDateRef()));
				calendar.set(Calendar.HOUR, Integer.parseInt(routingForm.getEffectiveHour()));
				calendar.set(Calendar.MINUTE, Integer.parseInt(routingForm.getEffectiveMinute()));
				calendar.set(Calendar.AM_PM, Integer.parseInt(routingForm.getAmPm()));
				date = new Timestamp(calendar.getTimeInMillis());
			} catch (Exception e) {
				LOG.error("error parsing date", e);
				GlobalVariables.getMessageMap().putError("Invalid date.", "routereport.effectiveDate.invalid");
			}
		}

		if (!GlobalVariables.getMessageMap().isEmpty()) {
            throw new ValidationException("Errors populating rule attributes.");
        }

		DocumentTypeService documentTypeService = (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
		DocumentType docType = documentTypeService.findByName(routingForm.getDocumentType());

		DocumentRouteHeaderValue routeHeader = new DocumentRouteHeaderValue();
		routeHeader.setRouteHeaderId(new Long(0));
		routeHeader.setDocumentTypeId(docType.getDocumentTypeId());
		routeHeader.setDocRouteLevel(new Integer(0));
        routeHeader.setDocVersion(new Integer(KEWConstants.CURRENT_DOCUMENT_VERSION));

        List<RouteReportRuleTemplateContainer> ruleTemplateContainers = new ArrayList<RouteReportRuleTemplateContainer>();
		if (routingForm.getReportType().equals(DOC_TYPE_REPORTING)) {

          List routeNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(docType, true);
			for (Iterator iter = routeNodes.iterator(); iter.hasNext();) {
                RouteNode routeNode = (RouteNode) iter.next();
				if (routeNode.isFlexRM()) {
					RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateName(routeNode.getRouteMethodName());
					if (ruleTemplate != null) {
					    ruleTemplateContainers.add(new RouteReportRuleTemplateContainer(ruleTemplate, routeNode));
						if (ruleTemplate.getDelegationTemplate() != null) {
						    ruleTemplateContainers.add(new RouteReportRuleTemplateContainer(ruleTemplate.getDelegationTemplate(), routeNode));
						}
					}
				}
			}
		} else {
			RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateId(routingForm.getRuleTemplateId());
			RouteNode routeNode = new RouteNode();
			routeNode.setRouteNodeName(ruleTemplate.getName());
			ruleTemplateContainers.add(new RouteReportRuleTemplateContainer(ruleTemplate, routeNode));
			if (ruleTemplate.getDelegationTemplate() != null) {
			    ruleTemplateContainers.add(new RouteReportRuleTemplateContainer(ruleTemplate.getDelegationTemplate(), routeNode));
			}
		}

        String xmlDocumentContent = routingForm.getDocumentContent();
        if (routingForm.getReportType().equals(TEMPLATE_REPORTING)) {
            List attributes = new ArrayList();
            for (Iterator iterator = ruleTemplateContainers.iterator(); iterator.hasNext();) {
                RouteReportRuleTemplateContainer ruleTemplateContainer = (RouteReportRuleTemplateContainer) iterator.next();
                RuleTemplate ruleTemplate = ruleTemplateContainer.ruleTemplate;
                for (Iterator iter = ruleTemplate.getActiveRuleTemplateAttributes().iterator(); iter.hasNext();) {
                    RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) iter.next();
                    if (!ruleTemplateAttribute.isWorkflowAttribute()) {
                        continue;
                    }
                    WorkflowAttribute workflowAttribute = ruleTemplateAttribute.getWorkflowAttribute();

                    RuleAttribute ruleAttribute = ruleTemplateAttribute.getRuleAttribute();
                    if (ruleAttribute.getType().equals(KEWConstants.RULE_XML_ATTRIBUTE_TYPE)) {
                        ((GenericXMLRuleAttribute) workflowAttribute).setRuleAttribute(ruleAttribute);
                    }
                    List attValidationErrors = workflowAttribute.validateRoutingData(routingForm.getFields());
                    if (attValidationErrors != null && !attValidationErrors.isEmpty()) {
                        errors.addAll(attValidationErrors);
                    }
                    attributes.add(workflowAttribute);
                }
            }

            if (!GlobalVariables.getMessageMap().isEmpty()) {
                throw new ValidationException("errors in search criteria");
            }

            DocumentContent docContent = new AttributeDocumentContent(attributes);
            xmlDocumentContent = docContent.getDocContent();
        }

		routeHeader.setDocContent(xmlDocumentContent);
		routeHeader.setInitiatorWorkflowId(getUserSession(request).getPrincipalId());
		routeHeader.setDocRouteStatus(KEWConstants.ROUTE_HEADER_INITIATED_CD);
		routeHeader.setDocTitle("Routing Report");
		routeHeader.setRoutingReport(true);
		long magicCounter = 0;

		FlexRM flexRM = new FlexRM(date);

		int numberOfRules = 0;
		int numberOfActionRequests = 0;
		Set<String> alreadyProcessedRuleTemplateNames = new HashSet<String>();
		for (Iterator iterator = ruleTemplateContainers.iterator(); iterator.hasNext();) {
			// initialize the RouteContext
		    RouteContext context = RouteContext.createNewRouteContext();
		context.setActivationContext(new ActivationContext(ActivationContext.CONTEXT_IS_SIMULATION));
			try {
			    RouteReportRuleTemplateContainer ruleTemplateContainer = (RouteReportRuleTemplateContainer) iterator.next();
				RuleTemplate ruleTemplate = ruleTemplateContainer.ruleTemplate;
				RouteNode routeLevel = ruleTemplateContainer.routeNode;

				if (!alreadyProcessedRuleTemplateNames.contains(ruleTemplate.getName())) {
				    alreadyProcessedRuleTemplateNames.add(ruleTemplate.getName());
    				List actionRequests = flexRM.getActionRequests(routeHeader, routeLevel, null, ruleTemplate.getName());

    				numberOfActionRequests += actionRequests.size();
    				numberOfRules += flexRM.getNumberOfMatchingRules();

    				magicCounter = populateActionRequestsWithRouteLevelInformationAndIterateMagicCounter(routeLevel, actionRequests, magicCounter);
    				routeHeader.getActionRequests().addAll(actionRequests);
				}
			} finally {
				RouteContext.clearCurrentRouteContext();
			}
		}

		if (numberOfActionRequests == 0) {
			if (numberOfRules == 0) {
			    GlobalVariables.getMessageMap().putError("*", "routereport.noRules");
			} else {
			    GlobalVariables.getMessageMap().putError("*", "routereport.noMatchingRules");
			}
			if (GlobalVariables.getMessageMap().hasErrors()) {
	            throw new ValidationException("errors in search criteria");
	        }
		}


		// PROBLEM HERE!!!!
		RouteLogForm routeLogForm = new RouteLogForm();
		routeLogForm.setShowFuture(true);
        if (StringUtils.isNotBlank(routingForm.getBackUrl())) {
            routeLogForm.setReturnUrlLocation(routingForm.getBackUrl());
        }
        LOG.debug("Value of getDisplayCloseButton " + routingForm.getShowCloseButton());
        LOG.debug("Value of isDisplayCloseButton " + routingForm.isDisplayCloseButton());
        routeLogForm.setShowCloseButton(routingForm.isDisplayCloseButton());
		request.setAttribute("routeHeader", routeHeader);
		new RouteLogAction().populateRouteLogFormActionRequests(routeLogForm, routeHeader);
		request.setAttribute("KualiForm", routeLogForm);
		//END PROBLEM AREA

		//return mapping.findForward("basic");
		return mapping.findForward("routeLog");
	}

	private class RouteReportRuleTemplateContainer {
	    public RuleTemplate ruleTemplate = null;
	    public RouteNode routeNode = null;
	    public RouteReportRuleTemplateContainer(RuleTemplate template, RouteNode node) {
	        this.ruleTemplate = template;
	        this.routeNode = node;
	    }
	}

	public long populateActionRequestsWithRouteLevelInformationAndIterateMagicCounter(RouteNode routeLevel, List actionRequests, long magicCounter) {

		for (Iterator iter = actionRequests.iterator(); iter.hasNext();) {
			ActionRequestValue actionRequest = (ActionRequestValue) iter.next();
			populateActionRequestsWithRouteLevelInformationAndIterateMagicCounter(routeLevel, actionRequest.getChildrenRequests(), magicCounter);
			actionRequest.setStatus(KEWConstants.ACTION_REQUEST_INITIALIZED);
//			actionRequest.setRouteMethodName(routeLevel.getRouteMethodName());
			RouteNodeInstance routeNode = new RouteNodeInstance();
			routeNode.setRouteNode(routeLevel);
			actionRequest.setNodeInstance(routeNode);
			actionRequest.setRouteLevel(new Integer(0));
			magicCounter++;
			actionRequest.setActionRequestId(new Long(magicCounter));
		}
		return magicCounter;
	}

	public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	    return mapping.findForward("basic");
	}

	private ActionMessages initiateForm(HttpServletRequest request, ActionForm form) throws Exception {
        RoutingReportForm routingReportForm = (RoutingReportForm) form;
        if (routingReportForm.getReportType() == null) {
            // no report type means we must check for potential setup
            if ( (!Utilities.isEmpty(routingReportForm.getDocumentTypeParam())) ||
                 (!Utilities.isEmpty(routingReportForm.getInitiatorPrincipalId())) ||
                 (!Utilities.isEmpty(routingReportForm.getDocumentContent())) ) {
                // at least one parameter was passed... attempt to use Doc Type Report
                routingReportForm.setReportType(DOC_TYPE_REPORTING);
            } else {
                // no parameters passed... default to Template Type Rreport
                routingReportForm.setReportType(TEMPLATE_REPORTING);
            }
        }

        if (routingReportForm.getReportType().equals(DOC_TYPE_REPORTING)) {
            if (Utilities.isEmpty(routingReportForm.getDocumentTypeParam())) {
                throw new RuntimeException("Document Type was not given");
            } else {
                DocumentType docType = getDocumentTypeService().findByName(routingReportForm.getDocumentTypeParam());
                if (docType == null) {
                    throw new RuntimeException("Document Type is invalid");
                }
            }
            if (Utilities.isEmpty(routingReportForm.getInitiatorPrincipalId())) {
                throw new RuntimeException("Initiator Principal ID was not given");
            } else {
                KimPrincipal initiatorPrincipal = KEWServiceLocator.getIdentityHelperService().getPrincipal(routingReportForm.getInitiatorPrincipalId());
            }
            if (Utilities.isEmpty(routingReportForm.getDocumentContent())) {
                throw new RuntimeException("Document Content was not given");
            }

            if (!Utilities.isEmpty(routingReportForm.getDocumentType())) {
                DocumentType docType = getDocumentTypeService().findByName(routingReportForm.getDocumentType());
                if (docType == null) {
                    throw new RuntimeException("Document Type is missing or invalid");
                }
                routingReportForm.getRuleTemplateAttributes().clear();
                List routeNodes = KEWServiceLocator.getRouteNodeService().getFlattenedNodes(docType, true);
                for (Iterator iter = routeNodes.iterator(); iter.hasNext();) {
                    RouteNode routeNode = (RouteNode) iter.next();
                    if (routeNode.isFlexRM()) {
                        RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateName(routeNode.getRouteMethodName());
                        if (ruleTemplate != null) {
                            loadRuleTemplateOnForm(ruleTemplate, routingReportForm, request, false);
                            if (ruleTemplate.getDelegationTemplate() != null) {
                                loadRuleTemplateOnForm(ruleTemplate.getDelegationTemplate(), routingReportForm, request, true);
                            }
                        }
                    }
                }
            }
//          routingReportForm.setShowFields(true);
        } else if (routingReportForm.getReportType().equals(TEMPLATE_REPORTING)) {
            routingReportForm.setRuleTemplates(getRuleTemplateService().findAll());
            if (routingReportForm.getRuleTemplateId() != null) {
                RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateId(routingReportForm.getRuleTemplateId());
                routingReportForm.getRuleTemplateAttributes().clear();
                loadRuleTemplateOnForm(ruleTemplate, routingReportForm, request, false);
                if (ruleTemplate.getDelegationTemplate() != null) {
                    loadRuleTemplateOnForm(ruleTemplate.getDelegationTemplate(), routingReportForm, request, true);
                }
            }
        }
        return null;
	}

	private void loadRuleTemplateOnForm(RuleTemplate ruleTemplate, RoutingReportForm routingReportForm, HttpServletRequest request, boolean isDelegate) {

		Map fieldValues = new HashMap();

		List ruleTemplateAttributes = ruleTemplate.getActiveRuleTemplateAttributes();
		Collections.sort(ruleTemplateAttributes);

		List rows = new ArrayList();
		for (Iterator iter = ruleTemplateAttributes.iterator(); iter.hasNext();) {
			RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) iter.next();
			if (!ruleTemplateAttribute.isWorkflowAttribute()) {
				continue;
			}
			WorkflowAttribute workflowAttribute = ruleTemplateAttribute.getWorkflowAttribute();

			RuleAttribute ruleAttribute = ruleTemplateAttribute.getRuleAttribute();
			if (ruleAttribute.getType().equals(KEWConstants.RULE_XML_ATTRIBUTE_TYPE)) {
				((GenericXMLRuleAttribute) workflowAttribute).setRuleAttribute(ruleAttribute);
			}
			for (Iterator iterator = workflowAttribute.getRoutingDataRows().iterator(); iterator.hasNext();) {
				Row row = (Row) iterator.next();

				List fields = new ArrayList();
				for (Iterator iterator2 = row.getFields().iterator(); iterator2.hasNext();) {
					Field field = (Field) iterator2.next();
					if (request.getParameter(field.getPropertyName()) != null) {
						field.setPropertyValue(request.getParameter(field.getPropertyName()));
					} else if (routingReportForm.getFields() != null && !routingReportForm.getFields().isEmpty()) {
						field.setPropertyValue((String) routingReportForm.getFields().get(field.getPropertyName()));
					}
					fields.add(field);
					fieldValues.put(field.getPropertyName(), field.getPropertyValue());
				}
			}

			workflowAttribute.validateRuleData(fieldValues);// populate attribute
			List<Row> rdRows = workflowAttribute.getRoutingDataRows();
			for (Row row : rdRows)
			{
				List fields = new ArrayList();
				List<Field> rowFields = row.getFields();
				for (Field field : rowFields )
				{
					if (request.getParameter(field.getPropertyName()) != null) {
						field.setPropertyValue(request.getParameter(field.getPropertyName()));
					} else if (routingReportForm.getFields() != null && !routingReportForm.getFields().isEmpty()) {
						field.setPropertyValue((String) routingReportForm.getFields().get(field.getPropertyName()));
					}
					fields.add(field);
					fieldValues.put(field.getPropertyName(), field.getPropertyValue());
				}
				row.setFields(fields);
				rows.add(row);

			}
		}

		routingReportForm.getFields().putAll(fieldValues);
		routingReportForm.getRuleTemplateAttributes().addAll(rows);
		routingReportForm.setShowFields(true);
		routingReportForm.setShowViewResults(true);
	}

	public ActionForward loadTemplate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		RoutingReportForm routingReportForm = (RoutingReportForm) form;
		if (Utilities.isEmpty(routingReportForm.getDateRef())) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			routingReportForm.setEffectiveHour("5");
			routingReportForm.setEffectiveMinute("0");
			routingReportForm.setAmPm("1");
			routingReportForm.setDateRef(sdf.format(new Date()));
		}
		return mapping.findForward("basic");
	}

	private void makeLookupPathParam(ActionMapping mapping, HttpServletRequest request) {
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + mapping.getModuleConfig().getPrefix();
        request.setAttribute("basePath", basePath);
    }

	private RuleTemplateService getRuleTemplateService() {
		return (RuleTemplateService) KEWServiceLocator.getService(KEWServiceLocator.RULE_TEMPLATE_SERVICE);
	}

	private DocumentTypeService getDocumentTypeService() {
		return (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
	}

	private UserSession getUserSession(HttpServletRequest request) {
	    return UserSession.getAuthenticatedUser();
	}




}
