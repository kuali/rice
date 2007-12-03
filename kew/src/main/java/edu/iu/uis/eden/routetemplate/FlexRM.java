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
package edu.iu.uis.eden.routetemplate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestFactory;
import edu.iu.uis.eden.actionrequests.ActionRequestService;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.node.RouteNodeInstance;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.plugin.attributes.MassRuleAttribute;
import edu.iu.uis.eden.plugin.attributes.RoleAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.routeheader.DocumentContent;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.user.Recipient;
import edu.iu.uis.eden.user.RoleRecipient;
import edu.iu.uis.eden.user.WorkflowUserId;
import edu.iu.uis.eden.util.PerformanceLogger;
import edu.iu.uis.eden.util.ResponsibleParty;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;

/**
 * Generates Action Requests for a Document using the rule system and the specified
 * {@link RuleTemplate}.
 * 
 * @see ActionRequestValue
 * @see RuleTemplate
 * @see RuleBaseValues
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class FlexRM {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FlexRM.class);
	
	private Timestamp effectiveDate;
	private int numberOfMatchingRules;
	private ActionRequestFactory arFactory;

	public FlexRM() {
	}

	public FlexRM(Timestamp effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

    public void setActionRequestFactory(ActionRequestFactory arFactory) {
        this.arFactory = arFactory;
    }

    /* Used by InlineRequestsRouteModule to generate requests for responsibilities directly */
    public ActionRequestFactory getActionRequestFactory() {
        return arFactory;
    }

	public List getActionRequests(DocumentRouteHeaderValue routeHeader, String ruleTemplateName) throws EdenUserNotFoundException, WorkflowException {
		return getActionRequests(routeHeader, null, ruleTemplateName);
	}

	public List getActionRequests(DocumentRouteHeaderValue routeHeader, RouteNodeInstance nodeInstance, String ruleTemplateName) throws EdenUserNotFoundException, WorkflowException {

		LOG.debug("Making action requests for document " + routeHeader.getRouteHeaderId());
		Set massRules = new HashSet();
		RuleTemplate template = getRuleTemplateService().findByRuleTemplateName(ruleTemplateName);
		if (template == null) {
			throw new WorkflowRuntimeException("Could not locate the rule template with name " + ruleTemplateName + " on document " + routeHeader.getRouteHeaderId());
		}
		for (Iterator iter = template.getActiveRuleTemplateAttributes().iterator(); iter.hasNext();) {

			RuleTemplateAttribute templateAttribute = (RuleTemplateAttribute) iter.next();
			if (!templateAttribute.isWorkflowAttribute()) {
				continue;
			}
			WorkflowAttribute attribute = templateAttribute.getWorkflowAttribute();
			if (attribute instanceof MassRuleAttribute) {
				massRules.add(attribute);
			}

		}

		List rules = null;
		if (effectiveDate != null) {
			rules = getRuleService().fetchAllCurrentRulesForTemplateDocCombination(ruleTemplateName, routeHeader.getDocumentType().getName(), effectiveDate);
		} else {
			rules = getRuleService().fetchAllCurrentRulesForTemplateDocCombination(ruleTemplateName, routeHeader.getDocumentType().getName());
		}
		numberOfMatchingRules = rules.size();

		RouteContext context = RouteContext.getCurrentRouteContext();
		// TODO really the route context just needs to be able to support nested create and clears
		// (i.e. a Stack model similar to transaction intercepting in Spring) and we wouldn't have to do this
		if (context.getDocument() == null) {
			context.setDocument(routeHeader);
		}
		if (context.getNodeInstance() == null) {
			context.setNodeInstance(nodeInstance);
		}
		DocumentContent documentContent = context.getDocumentContent();
		PerformanceLogger performanceLogger = new PerformanceLogger();
		// have all mass rule attributes filter the list of non applicable rules
		for (Iterator iter = massRules.iterator(); iter.hasNext();) {
			MassRuleAttribute massRuleAttribute = (MassRuleAttribute) iter.next();
			rules = massRuleAttribute.filterNonMatchingRules(context, rules);
		}
		performanceLogger.log("Time to filter massRules for template " + template.getName());

		arFactory = new ActionRequestFactory(routeHeader, context.getNodeInstance());
		performanceLogger = new PerformanceLogger();
		List actionRequests = new ArrayList();
		for (Iterator iter = rules.iterator(); iter.hasNext();) {
			RuleBaseValues rule = (RuleBaseValues) iter.next();
			if (rule.isMatch(documentContent)) {
//				actionRequests.addAll(makeActionRequests(context, rule, routeHeader, null, null));
				makeActionRequests(context, rule, routeHeader, null, null);
			}
		}
		actionRequests = new ArrayList(arFactory.getRequestGraphs());
		performanceLogger.log("Time to make action request for template " + template.getName());

		return actionRequests;
	}

	public ResponsibleParty resolveResponsibilityId(Long responsibilityId) {
		RuleResponsibility resp = getRuleService().findRuleResponsibility(responsibilityId);
		if (resp.isUsingRole()) {
			return new ResponsibleParty(resp.getResolvedRoleName());
		} else if (resp.isUsingWorkflowUser()) {
			return new ResponsibleParty(new WorkflowUserId(resp.getRuleResponsibilityName()));
		} else {
			return new ResponsibleParty(new WorkflowGroupId(new Long(resp.getRuleResponsibilityName())));
		}
	}

	private void makeActionRequests(RouteContext context, RuleBaseValues rule, DocumentRouteHeaderValue routeHeader, ActionRequestValue parentRequest, RuleDelegation ruleDelegation)
			throws EdenUserNotFoundException, WorkflowException {

		List responsibilities = rule.getResponsibilities();
        makeActionRequests(responsibilities, context, rule, routeHeader, parentRequest, ruleDelegation);
	}

    public void makeActionRequests(List<RuleResponsibility> responsibilities, RouteContext context, RuleBaseValues rule, DocumentRouteHeaderValue routeHeader, ActionRequestValue parentRequest, RuleDelegation ruleDelegation)
        throws EdenUserNotFoundException, WorkflowException {

//      Set actionRequests = new HashSet();
        for (Iterator iter = responsibilities.iterator(); iter.hasNext();) {
            RuleResponsibility resp = (RuleResponsibility) iter.next();
//          arFactory = new ActionRequestFactory(routeHeader);
            
            if (resp.isUsingRole()) {
                makeRoleActionRequests(context, rule, resp, routeHeader, parentRequest, ruleDelegation);
            } else {
                makeActionRequest(context, rule, routeHeader, resp, parentRequest, ruleDelegation);
            }
//          if (arFactory.getRequestGraph() != null) {
//              actionRequests.add(arFactory.getRequestGraph());
//          }
        }
    }

	private void buildDelegationGraph(RouteContext context, RuleBaseValues delegationRule, DocumentRouteHeaderValue routeHeaderValue, ActionRequestValue parentRequest, RuleDelegation ruleDelegation)
			throws EdenUserNotFoundException, WorkflowException {
		context.setActionRequest(parentRequest);
		if (delegationRule.getActiveInd().booleanValue() && delegationRule.getToDate().after(new Date()) && delegationRule.getFromDate().before(new Date())) {
			for (Iterator iter = delegationRule.getResponsibilities().iterator(); iter.hasNext();) {
				RuleResponsibility delegationResp = (RuleResponsibility) iter.next();
				if (delegationResp.isUsingRole()) {
					makeRoleActionRequests(context, delegationRule, delegationResp, routeHeaderValue, parentRequest, ruleDelegation);
				} else if (delegationRule.isMatch(context.getDocumentContent())) {
					makeActionRequest(context, delegationRule, routeHeaderValue, delegationResp, parentRequest, ruleDelegation);
				}
			}
		}
	}

	private void makeRoleActionRequests(RouteContext context, RuleBaseValues rule, RuleResponsibility resp, DocumentRouteHeaderValue routeHeader, ActionRequestValue parentRequest,
			RuleDelegation ruleDelegation) throws EdenUserNotFoundException, WorkflowException {
		
		String roleName = resp.getResolvedRoleName();
		RoleAttribute roleAttribute = resp.resolveRoleAttribute();
		List<String> qualifiedRoleNames = new ArrayList<String>();
		if (parentRequest != null && parentRequest.getQualifiedRoleName() != null) {
			qualifiedRoleNames.add(parentRequest.getQualifiedRoleName());
		} else {
			qualifiedRoleNames.addAll(roleAttribute.getQualifiedRoleNames(roleName, context.getDocumentContent()));
		}
		for (Iterator iter = qualifiedRoleNames.iterator(); iter.hasNext();) {
			String qualifiedRoleName = (String) iter.next();
			if (parentRequest == null && isDuplicateActionRequestDetected(rule, routeHeader, context.getNodeInstance(), resp, qualifiedRoleName)) {
				continue;
			}

			ResolvedQualifiedRole resolvedRole = roleAttribute.resolveQualifiedRole(context, roleName, qualifiedRoleName);
			RoleRecipient recipient = new RoleRecipient(roleName, qualifiedRoleName, resolvedRole);
			if (parentRequest == null) {
				ActionRequestValue roleRequest = arFactory.addRoleRequest(recipient, resp.getActionRequestedCd(), resp.getApprovePolicy(), resp.getPriority(), resp.getResponsibilityId(), rule
						.getIgnorePrevious(), rule.getDescription(), rule.getRuleBaseValuesId());
				if (resp.isDelegating()) {
					// create delegations for all the children
					for (Iterator iterator = roleRequest.getChildrenRequests().iterator(); iterator.hasNext();) {
						ActionRequestValue request = (ActionRequestValue) iterator.next();
						for (Iterator ruleDelegationIterator = resp.getDelegationRules().iterator(); ruleDelegationIterator.hasNext();) {
							RuleDelegation childRuleDelegation = (RuleDelegation) ruleDelegationIterator.next();
							buildDelegationGraph(context, childRuleDelegation.getDelegationRuleBaseValues(), routeHeader, request, childRuleDelegation);
						}
					}
				}
			} else {
				arFactory.addDelegationRoleRequest(parentRequest, resp.getApprovePolicy(), recipient, resp.getResponsibilityId(), rule.getIgnorePrevious(), ruleDelegation.getDelegationType(), rule.getDescription(), rule.getRuleBaseValuesId());
			}
		}
	}

	private void makeActionRequest(RouteContext context, RuleBaseValues rule, DocumentRouteHeaderValue routeHeader, RuleResponsibility resp, ActionRequestValue parentRequest,
			RuleDelegation ruleDelegation) throws EdenUserNotFoundException, WorkflowException {
		if (parentRequest == null && isDuplicateActionRequestDetected(rule, routeHeader, context.getNodeInstance(), resp, null)) {
			return;
		}
		Recipient recipient;
		if (resp.isUsingWorkflowUser()) {
			recipient = KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(resp.getRuleResponsibilityName()));
		} else {
			recipient = KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(new Long(resp.getRuleResponsibilityName())));
		}
		ActionRequestValue actionRequest;
		if (parentRequest == null) {
			actionRequest = arFactory.addRootActionRequest(resp.getActionRequestedCd(),
                    resp.getPriority(), 
                    recipient, 
                    rule.getDescription(), 
                    resp.getResponsibilityId(), 
                    rule.getIgnorePrevious(),
					resp.getApprovePolicy(),
                    rule.getRuleBaseValuesId());
			if (resp.isDelegating()) {
				for (Iterator iterator = resp.getDelegationRules().iterator(); iterator.hasNext();) {
					RuleDelegation childRuleDelegation = (RuleDelegation) iterator.next();
					buildDelegationGraph(context, childRuleDelegation.getDelegationRuleBaseValues(), routeHeader, actionRequest, childRuleDelegation);
				}
			}
		} else {
			arFactory.addDelegationRequest(parentRequest, recipient, resp.getResponsibilityId(), rule.getIgnorePrevious(), ruleDelegation.getDelegationType(), rule.getDescription(), rule.getRuleBaseValuesId());
		}
	}

	private boolean isDuplicateActionRequestDetected(RuleBaseValues rule, DocumentRouteHeaderValue routeHeader, RouteNodeInstance nodeInstance, RuleResponsibility resp, String qualifiedRoleName) {
		List requests = getActionRequestService().findByStatusAndDocId(EdenConstants.ACTION_REQUEST_DONE_STATE, routeHeader.getRouteHeaderId());
		for (Iterator iterator = requests.iterator(); iterator.hasNext();) {
			ActionRequestValue request = (ActionRequestValue) iterator.next();
			if (((nodeInstance != null && request.getNodeInstance() != null && request.getNodeInstance().getRouteNodeInstanceId().equals(nodeInstance.getRouteNodeInstanceId())) || request
					.getRouteLevel().equals(routeHeader.getDocRouteLevel()))
					&& request.getResponsibilityId().equals(resp.getResponsibilityId()) && ObjectUtils.equals(request.getQualifiedRoleName(), qualifiedRoleName)) {
				return true;
			}
		}
		return false;
	}

	public RuleService getRuleService() {
		return (RuleService) KEWServiceLocator.getService(KEWServiceLocator.RULE_SERVICE);
	}

	private ActionRequestService getActionRequestService() {
		return (ActionRequestService) KEWServiceLocator.getService(KEWServiceLocator.ACTION_REQUEST_SRV);
	}

	private RuleTemplateService getRuleTemplateService() {
		return (RuleTemplateService) KEWServiceLocator.getService(KEWServiceLocator.RULE_TEMPLATE_SERVICE);
	}

	public int getNumberOfMatchingRules() {
		return numberOfMatchingRules;
	}
//
//	private DocumentContent parseDocumentContent(RouteContext context) throws WorkflowException {
//		try {
//			return new StandardDocumentContent(context.getDocument().getDocContent(), context);
//		} catch (Exception e) {
//			throw new WorkflowException("Error parsing doc content for document " + context.getDocument().getRouteHeaderId());
//		}
//	}
}