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
package edu.iu.uis.eden.routetemplate.web;

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

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.doctype.DocumentTypeService;
import edu.iu.uis.eden.engine.ActivationContext;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.AttributeDocumentContent;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routelog.web.RouteLogAction;
import edu.iu.uis.eden.routelog.web.RouteLogForm;
import edu.iu.uis.eden.routetemplate.FlexRM;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.routetemplate.RuleTemplateAttribute;
import edu.iu.uis.eden.routetemplate.RuleTemplateService;
import edu.iu.uis.eden.routetemplate.xmlrouting.GenericXMLRuleAttribute;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.WorkflowAction;

/**
 * A Struts Action for executing routing reports and retrieving the results.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoutingReportAction extends WorkflowAction {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RoutingReportAction.class);

	public static final String DOC_TYPE_REPORTING = "documentType";
	public static final String TEMPLATE_REPORTING = "template";

	public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		RoutingReportForm routingForm = (RoutingReportForm) form;
		if (Utilities.isEmpty(routingForm.getDateRef())) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			routingForm.setEffectiveHour("5");
			routingForm.setEffectiveMinute("0");
			routingForm.setAmPm("1");
			routingForm.setDateRef(sdf.format(new Date()));
		}
        if (DOC_TYPE_REPORTING.equals(routingForm.getReportType())) {
            if (Utilities.isEmpty(routingForm.getDocumentTypeParam())) {
                throw new RuntimeException("No document type was given");
            }
            if (Utilities.isEmpty(routingForm.getInitiatorNetworkId())) {
                throw new RuntimeException("No initiator network id was given");
            }
            if (Utilities.isEmpty(routingForm.getDocumentContent())) {
                throw new RuntimeException("No document content was given");
            }
            return calculateRoute(mapping, form, request, response);
        } else if (!(TEMPLATE_REPORTING.equals(routingForm.getReportType()))) {
            // report type is not Document Type or Template Type... error out
            throw new RuntimeException("The Routing Report type is not set");
        }
        return mapping.findForward("basic");
	}

	public ActionForward calculateRoute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		RoutingReportForm routingForm = (RoutingReportForm) form;

		List errors = new ArrayList();

		if (getDocumentTypeService().findByName(routingForm.getDocumentType()) == null) {
			errors.add(new WorkflowServiceErrorImpl("Document type is required.", "doctype.documenttypeservice.doctypename.required"));
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
				errors.add(new WorkflowServiceErrorImpl("Invalid date.", "routereport.effectiveDate.invalid"));
			}
		}

		if (!errors.isEmpty()) {
			throw new WorkflowServiceErrorException("Errors populating rule attributes.", errors);
		}

		DocumentTypeService documentTypeService = (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
		DocumentType docType = documentTypeService.findByName(routingForm.getDocumentType());

		DocumentRouteHeaderValue routeHeader = new DocumentRouteHeaderValue();
		routeHeader.setRouteHeaderId(new Long(0));
		routeHeader.setDocumentTypeId(docType.getDocumentTypeId());
		routeHeader.setDocRouteLevel(new Integer(0));
        routeHeader.setDocVersion(new Integer(EdenConstants.CURRENT_DOCUMENT_VERSION));

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
                    if (ruleAttribute.getType().equals(EdenConstants.RULE_XML_ATTRIBUTE_TYPE)) {
                        ((GenericXMLRuleAttribute) workflowAttribute).setRuleAttribute(ruleAttribute);
                    }
                    List attValidationErrors = workflowAttribute.validateRoutingData(routingForm.getFields());
                    if (attValidationErrors != null && !attValidationErrors.isEmpty()) {
                        errors.addAll(attValidationErrors);
                    }
                    attributes.add(workflowAttribute);
                }
            }

            if (!errors.isEmpty()) {
                throw new WorkflowServiceErrorException("Errors populating rule attributes.", errors);
            }

            DocumentContent docContent = new AttributeDocumentContent(attributes);
            xmlDocumentContent = docContent.getDocContent();
        }

		routeHeader.setDocContent(xmlDocumentContent);
		routeHeader.setInitiatorWorkflowId(getUserSession(request).getWorkflowUser().getWorkflowUserId().getWorkflowId());
		routeHeader.setDocRouteStatus(EdenConstants.ROUTE_HEADER_INITIATED_CD);
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
				List actionRequests = flexRM.getActionRequests(routeHeader, ruleTemplate.getName());

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
				errors.add(new WorkflowServiceErrorImpl("There are no rules.", "routereport.noRules"));
			} else {
				errors.add(new WorkflowServiceErrorImpl("There are rules, but no matches.", "routereport.noMatchingRules"));
			}
			if (!errors.isEmpty()) {
				throw new WorkflowServiceErrorException("Errors populating rule attributes.", errors);
			}
		}

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
		request.setAttribute("RouteLogForm", routeLogForm);

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
			actionRequest.setStatus(EdenConstants.ACTION_REQUEST_INITIALIZED);
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

	public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
		RoutingReportForm routingReportForm = (RoutingReportForm) form;
		if (routingReportForm.getReportType() == null) {
            // no report type means we must check for potential setup
            if ( (!Utilities.isEmpty(routingReportForm.getDocumentTypeParam())) ||
                 (!Utilities.isEmpty(routingReportForm.getInitiatorNetworkId())) ||
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
            if (Utilities.isEmpty(routingReportForm.getInitiatorNetworkId())) {
                throw new RuntimeException("Initiator Network ID was not given");
            } else {
                WorkflowUser initiatorUser = getUserService().getWorkflowUser(new AuthenticationUserId(routingReportForm.getInitiatorNetworkId()));
                if (initiatorUser == null) {
                    throw new RuntimeException("Initiator Network ID is invalid");
                }
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
//			routingReportForm.setShowFields(true);
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
			if (ruleAttribute.getType().equals(EdenConstants.RULE_XML_ATTRIBUTE_TYPE)) {
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
				// row.setFields(fields);
				// rows.add(row);
			}

			workflowAttribute.validateRuleData(fieldValues);// populate attribute
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
				row.setFields(fields);
				if (isDelegate) {
					row.setRowsGroupLabel("Delegate " + row.getRowsGroupLabel());
				}
				rows.add(row);

			}
		}

		routingReportForm.getFields().putAll(fieldValues);
		// routingReportForm.setFields(fieldValues);
		routingReportForm.getRuleTemplateAttributes().addAll(rows);
		// routingReportForm.setRuleTemplateAttributes(rows);
		routingReportForm.setShowFields(true);
		routingReportForm.setShowViewResults(true);
	}

	public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		RoutingReportForm routingReportForm = (RoutingReportForm) form;

		String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + mapping.getModuleConfig().getPrefix();
		StringBuffer lookupUrl = new StringBuffer(basePath);
		lookupUrl.append("/Lookup.do?methodToCall=start&docFormKey=").append(getUserSession(request).addObject(form)).append("&lookupableImplServiceName=");
		lookupUrl.append(request.getParameter("lookupableImplServiceName"));

		lookupUrl.append("&conversionFields=");

		List ruleTemplateAttributes = null;

		if (routingReportForm.getReportType().equals(DOC_TYPE_REPORTING)) {
			if (!Utilities.isEmpty(routingReportForm.getDocumentType())) {
				DocumentType docType = getDocumentTypeService().findByName(routingReportForm.getDocumentType());
				if (docType == null) {
					throw new RuntimeException("Document Type is null");
				}
				ruleTemplateAttributes = new ArrayList();
			}
		} else if (routingReportForm.getRuleTemplateId() != null) {
			RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateId(routingReportForm.getRuleTemplateId());
			ruleTemplateAttributes = ruleTemplate.getActiveRuleTemplateAttributes();
		}
		if (ruleTemplateAttributes != null) {
			Collections.sort(ruleTemplateAttributes);
			for (Iterator iter = ruleTemplateAttributes.iterator(); iter.hasNext();) {
				RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) iter.next();
				if (!ruleTemplateAttribute.isWorkflowAttribute()) {
					continue;
				}
				WorkflowAttribute workflowAttribute = ruleTemplateAttribute.getWorkflowAttribute();

				RuleAttribute ruleAttribute = ruleTemplateAttribute.getRuleAttribute();
				if (ruleAttribute.getType().equals(EdenConstants.RULE_XML_ATTRIBUTE_TYPE)) {
					((GenericXMLRuleAttribute) workflowAttribute).setRuleAttribute(ruleAttribute);
				}
				boolean foundQuickFinder = false;
				for (Iterator iterator = workflowAttribute.getRoutingDataRows().iterator(); iterator.hasNext();) {
					Row row = (Row) iterator.next();
					for (Iterator iterator2 = row.getFields().iterator(); iterator2.hasNext();) {
						Field field = (Field) iterator2.next();
						if (field.getFieldType().equals(Field.QUICKFINDER) && field.getQuickFinderClassNameImpl().equals(request.getParameter("lookupableImplServiceName"))) {
							foundQuickFinder = true;
						}
					}
				}

				if (foundQuickFinder) {
					StringBuffer conversionFields = new StringBuffer();
					for (Iterator iterator = workflowAttribute.getRoutingDataRows().iterator(); iterator.hasNext();) {
						Row row = (Row) iterator.next();
						for (Iterator iterator2 = row.getFields().iterator(); iterator2.hasNext();) {
							Field field = (Field) iterator2.next();
							if (!Utilities.isEmpty(field.getDefaultLookupableName())) {
								conversionFields.append(field.getDefaultLookupableName()).append(":").append(field.getPropertyName()).append(",");
							}
						}
					}
					if (!Utilities.isEmpty(conversionFields.toString())) {
						lookupUrl.append(conversionFields.substring(0, conversionFields.lastIndexOf(",")));
					}
				}
			}
		}

		lookupUrl.append("&returnLocation=").append(basePath).append(mapping.getPath()).append(".do");
		return new ActionForward(lookupUrl.toString(), true);

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

    private UserService getUserService() {
        return (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
    }

	private RuleTemplateService getRuleTemplateService() {
		return (RuleTemplateService) KEWServiceLocator.getService(KEWServiceLocator.RULE_TEMPLATE_SERVICE);
	}

	private DocumentTypeService getDocumentTypeService() {
		return (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
	}
}