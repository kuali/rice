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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.workflow.routemodule.BaseRouteModule;
import org.kuali.workflow.routemodule.RouteModuleException;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.actionrequests.ActionRequestFactory;
import edu.iu.uis.eden.actionrequests.ActionRequestService;
import edu.iu.uis.eden.actionrequests.ActionRequestValue;
import edu.iu.uis.eden.engine.RouteContext;
import edu.iu.uis.eden.engine.node.RouteNode;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routetemplate.FlexRM;
import edu.iu.uis.eden.util.PerformanceLogger;

/**
 * The RouteModule which is responsible for generating Action Requests from Roles configured in the system.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RoleRouteModule extends BaseRouteModule {

private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FlexRM.class);

	public static String EFFECTIVE_DATE_PARAM = "RoleRouteModule.EffectiveDate";

	public List findActionRequests(RouteContext context) throws Exception {
		DocumentRouteHeaderValue document = context.getDocument();
		Role role = getRole(context);
		LOG.debug("Generating Action Requests for role '" + role.getName() + "' on document " + document.getRouteHeaderId());
		List<QualifiedRole> roleEvaluationSet = loadRoleEvaluationSet(context, role);
		List<RoleResolver> roleResolvers = findRoleResolvers(role, context);
		if (roleResolvers == null || roleResolvers.isEmpty()) {
			throw new RouteModuleException("Could not find a RoleResolver for this role '" + role.getName() + "'");
		}
		List<QualifiedRole> matchingQualifiedRoles = applyRoleResolvers(context, role, roleEvaluationSet, roleResolvers);
		return generateActionRequests(context, matchingQualifiedRoles, role);
	}

	protected Role getRole(RouteContext context) {
		String roleName = getRoleName(context);
		Role role = KEWServiceLocator.getRoleService().findRoleByName(roleName);
		if (role == null) {
			throw new RouteModuleException("Could not locate the role with the given name '" + roleName + "'");
		}
		return role;
	}

	protected List<QualifiedRole> loadRoleEvaluationSet(RouteContext context, Role role) {
		return getRoleService().findQualifiedRolesForRole(role.getName(), getEffectiveDate(context));
	}

	protected Timestamp getEffectiveDate(RouteContext context) {
		return (Timestamp)context.getParameters().get(EFFECTIVE_DATE_PARAM);
	}

	protected List<QualifiedRole> applyRoleResolvers(RouteContext context, Role role, List<QualifiedRole> roleEvaluationSet, List<RoleResolver> resolvers) {
		List<List<QualifiedRole>> resolvedQualifiedRoles = new ArrayList<List<QualifiedRole>>();
		for (RoleResolver resolver : resolvers) {
			List<QualifiedRole> resolved = resolver.resolve(context, role, roleEvaluationSet);
			if (resolved != null && !resolved.isEmpty()) {
				resolvedQualifiedRoles.add(resolved);
			}
		}
		return mergeResolvedQualifiedRoles(context, role, resolvedQualifiedRoles);
	}

	protected List<QualifiedRole> mergeResolvedQualifiedRoles(RouteContext context, Role role, List<List<QualifiedRole>> resolvedQualifiedRoles) {
		Set<Long> mergedIds = new HashSet<Long>();
		List<QualifiedRole> qualifiedRoles = new ArrayList<QualifiedRole>();
		for (List<QualifiedRole> qualifiedRoleSet : resolvedQualifiedRoles) {
			for (QualifiedRole qualifiedRole : qualifiedRoleSet) {
				Long qRoleId = qualifiedRole.getQualifiedRoleId();
				if (!mergedIds.contains(qRoleId)) {
					qualifiedRoles.add(qualifiedRole);
					if (qRoleId != null) {
						mergedIds.add(qRoleId);
					}
				}
			}
		}
		return qualifiedRoles;
	}

	protected String getRoleName(RouteContext context) {
		RouteNode node = context.getNodeInstance().getRouteNode();
		return node.getRouteMethodName();
	}

	protected List<RoleResolver> findRoleResolvers(Role role, RouteContext context) {
		// TODO
		return null;
	}

	protected List<ActionRequestValue> generateActionRequests(RouteContext context, List<QualifiedRole> qualifiedRoles, Role role) {
		PerformanceLogger performanceLogger = new PerformanceLogger();
		ActionRequestFactory factory = new ActionRequestFactory(context.getDocument(), context.getNodeInstance());
		for (QualifiedRole qualifiedRole : qualifiedRoles) {
			generateActionRequests(context, qualifiedRole, factory);
		}
		List<ActionRequestValue> actionRequests = factory.getRequestGraphs();
		performanceLogger.log("Time to make action requests for role '" + role.getName() + "'");
		return actionRequests;
	}

	protected void generateActionRequests(RouteContext context, QualifiedRole qualifiedRole, ActionRequestFactory factory) {
		// TODO
	}

//	public List getActionRequests(DocumentRouteHeaderValue routeHeader, RouteNodeInstance nodeInstance, String ruleTemplateName) throws EdenUserNotFoundException, WorkflowException {
//		LOG.debug("Generating Action Requests for document " + routeHeader.getRouteHeaderId());
//		RuleTemplate template = getRuleTemplateService().findByRuleTemplateName(ruleTemplateName);
//		if (template == null) {
//			throw new WorkflowRuntimeException("Could not locate the rule template with name " + ruleTemplateName + " on document " + routeHeader.getRouteHeaderId());
//		}
//		for (Iterator iter = template.getRuleTemplateAttributes().iterator(); iter.hasNext();) {
//
//			RuleTemplateAttribute templateAttribute = (RuleTemplateAttribute) iter.next();
//			if (!templateAttribute.isWorkflowAttribute()) {
//				continue;
//			}
//			WorkflowAttribute attribute = templateAttribute.getWorkflowAttribute();
//			if (attribute instanceof MassRuleAttribute) {
//				massRules.add(attribute);
//			}
//
//		}
//
//		List rules = null;
//		if (getEffectiveDate() != null) {
//			rules = getRuleService().fetchAllCurrentRulesForTemplateDocCombination(ruleTemplateName, routeHeader.getDocumentType().getName(), effectiveDate);
//		} else {
//			rules = getRuleService().fetchAllCurrentRulesForTemplateDocCombination(ruleTemplateName, routeHeader.getDocumentType().getName());
//		}
//		numberOfMatchingRules = rules.size();
//
//		RouteContext context = RouteContext.getCurrentRouteContext();
//		// TODO really the route context just needs to be able to support nested create and clears
//		// (i.e. a Stack model similar to transaction intercepting in Spring) and we wouldn't have to do this
//		if (context.getDocument() == null) {
//			context.setDocument(routeHeader);
//		}
//		if (context.getNodeInstance() == null) {
//			context.setNodeInstance(nodeInstance);
//		}
//		DocumentContent documentContent = context.getDocumentContent();
//		PerformanceLogger performanceLogger = new PerformanceLogger();
//		// have all mass rule attributes filter the list of non applicable rules
//		for (Iterator iter = massRules.iterator(); iter.hasNext();) {
//			MassRuleAttribute massRuleAttribute = (MassRuleAttribute) iter.next();
//			rules = massRuleAttribute.filterNonMatchingRules(context, rules);
//		}
//		performanceLogger.log("Time to filter massRules for template " + template.getName());
//
//		arFactory = new ActionRequestFactory(routeHeader, context.getNodeInstance());
//		performanceLogger = new PerformanceLogger();
//		List actionRequests = new ArrayList();
//		for (Iterator iter = rules.iterator(); iter.hasNext();) {
//			RuleBaseValues rule = (RuleBaseValues) iter.next();
//			if (rule.isMatch(documentContent)) {
////				actionRequests.addAll(makeActionRequests(context, rule, routeHeader, null, null));
//				makeActionRequests(context, rule, routeHeader, null, null);
//			}
//		}
//		actionRequests = new ArrayList(arFactory.getRequestGraphs());
//		performanceLogger.log("Time to make action request for template " + template.getName());
//
//		return actionRequests;
//	}

//	public ResponsibleParty resolveResponsibilityId(Long responsibilityId) {
//		RuleResponsibility resp = getRuleService().findRuleResponsibility(responsibilityId);
//		if (resp.isUsingRole()) {
//			return new ResponsibleParty(resp.getResolvedRoleName());
//		} else if (resp.isUsingWorkflowUser()) {
//			return new ResponsibleParty(new WorkflowUserId(resp.getRuleResponsibilityName()));
//		} else {
//			return new ResponsibleParty(new WorkflowGroupId(new Long(resp.getRuleResponsibilityName())));
//		}
//	}

//	private void makeActionRequests(RouteContext context, RuleBaseValues rule, DocumentRouteHeaderValue routeHeader, ActionRequestValue parentRequest, RuleDelegation ruleDelegation)
//			throws EdenUserNotFoundException, WorkflowException {
//		List responsibilities = rule.getResponsibilities();
//		for (Iterator iter = responsibilities.iterator(); iter.hasNext();) {
//			RuleResponsibility resp = (RuleResponsibility) iter.next();
//			if (resp.isUsingRole()) {
//				makeRoleActionRequests(context, rule, resp, routeHeader, parentRequest, ruleDelegation);
//			} else {
//				makeActionRequest(context, rule, routeHeader, resp, parentRequest, ruleDelegation);
//			}
//		}
//	}
//
//	private void buildDelegationGraph(RouteContext context, RuleBaseValues delegationRule, DocumentRouteHeaderValue routeHeaderValue, ActionRequestValue parentRequest, RuleDelegation ruleDelegation)
//			throws EdenUserNotFoundException, WorkflowException {
//		context.setActionRequest(parentRequest);
//		if (delegationRule.getActiveInd().booleanValue() && delegationRule.getToDate().after(new Date()) && delegationRule.getFromDate().before(new Date())) {
//			for (Iterator iter = delegationRule.getResponsibilities().iterator(); iter.hasNext();) {
//				RuleResponsibility delegationResp = (RuleResponsibility) iter.next();
//				if (delegationResp.isUsingRole()) {
//					makeRoleActionRequests(context, delegationRule, delegationResp, routeHeaderValue, parentRequest, ruleDelegation);
//				} else if (delegationRule.isMatch(context.getDocumentContent())) {
//					makeActionRequest(context, delegationRule, routeHeaderValue, delegationResp, parentRequest, ruleDelegation);
//				}
//			}
//		}
//	}
//
//	private void makeRoleActionRequests(RouteContext context, RuleBaseValues rule, RuleResponsibility resp, DocumentRouteHeaderValue routeHeader, ActionRequestValue parentRequest,
//			RuleDelegation ruleDelegation) throws EdenUserNotFoundException, WorkflowException {
//
//		String roleName = resp.getResolvedRoleName();
//		RoleAttribute roleAttribute = resp.resolveRoleAttribute();
//		List<String> qualifiedRoleNames = new ArrayList<String>();
//		if (parentRequest != null && parentRequest.getQualifiedRoleName() != null) {
//			qualifiedRoleNames.add(parentRequest.getQualifiedRoleName());
//		} else {
//			qualifiedRoleNames.addAll(roleAttribute.getQualifiedRoleNames(roleName, context.getDocumentContent()));
//		}
//		for (Iterator iter = qualifiedRoleNames.iterator(); iter.hasNext();) {
//			String qualifiedRoleName = (String) iter.next();
//			if (parentRequest == null && isDuplicateActionRequestDetected(rule, routeHeader, context.getNodeInstance(), resp, qualifiedRoleName)) {
//				continue;
//			}
//
//			ResolvedQualifiedRole resolvedRole = roleAttribute.resolveQualifiedRole(context, roleName, qualifiedRoleName);
//			RoleRecipient recipient = new RoleRecipient(roleName, qualifiedRoleName, resolvedRole);
//			if (parentRequest == null) {
//				ActionRequestValue roleRequest = arFactory.addRoleRequest(recipient, resp.getActionRequestedCd(), resp.getApprovePolicy(), resp.getPriority(), resp.getResponsibilityId(), rule
//						.getIgnorePrevious(), rule.getDescription(), rule.getRuleBaseValuesId());
//				if (resp.isDelegating()) {
//					// create delegations for all the children
//					for (Iterator iterator = roleRequest.getChildrenRequests().iterator(); iterator.hasNext();) {
//						ActionRequestValue request = (ActionRequestValue) iterator.next();
//						for (Iterator ruleDelegationIterator = resp.getDelegationRules().iterator(); ruleDelegationIterator.hasNext();) {
//							RuleDelegation childRuleDelegation = (RuleDelegation) ruleDelegationIterator.next();
//							buildDelegationGraph(context, childRuleDelegation.getDelegationRuleBaseValues(), routeHeader, request, childRuleDelegation);
//						}
//					}
//				}
//			} else {
//				arFactory.addDelegationRoleRequest(parentRequest, resp.getApprovePolicy(), recipient, resp.getResponsibilityId(), rule.getIgnorePrevious(), ruleDelegation.getDelegationType(), rule.getDescription(), rule.getRuleBaseValuesId());
//			}
//		}
//	}
//
//	private void makeActionRequest(RouteContext context, RuleBaseValues rule, DocumentRouteHeaderValue routeHeader, RuleResponsibility resp, ActionRequestValue parentRequest,
//			RuleDelegation ruleDelegation) throws EdenUserNotFoundException, WorkflowException {
//		if (parentRequest == null && isDuplicateActionRequestDetected(rule, routeHeader, context.getNodeInstance(), resp, null)) {
//			return;
//		}
//		Recipient recipient;
//		if (resp.isUsingWorkflowUser()) {
//			recipient = KEWServiceLocator.getUserService().getWorkflowUser(new WorkflowUserId(resp.getRuleResponsibilityName()));
//		} else {
//			recipient = KEWServiceLocator.getWorkgroupService().getWorkgroup(new WorkflowGroupId(new Long(resp.getRuleResponsibilityName())));
//		}
//		ActionRequestValue actionRequest;
//		if (parentRequest == null) {
//			actionRequest = arFactory.addRootActionRequest(resp.getActionRequestedCd(), resp.getPriority(), recipient, rule.getDescription(), resp.getResponsibilityId(), rule.getIgnorePrevious(),
//					resp.getApprovePolicy(), rule.getRuleBaseValuesId());
//			if (resp.isDelegating()) {
//				for (Iterator iterator = resp.getDelegationRules().iterator(); iterator.hasNext();) {
//					RuleDelegation childRuleDelegation = (RuleDelegation) iterator.next();
//					buildDelegationGraph(context, childRuleDelegation.getDelegationRuleBaseValues(), routeHeader, actionRequest, childRuleDelegation);
//				}
//			}
//		} else {
//			arFactory.addDelegationRequest(parentRequest, recipient, resp.getResponsibilityId(), rule.getIgnorePrevious(), ruleDelegation.getDelegationType(), rule.getDescription(), rule.getRuleBaseValuesId());
//		}
//	}
//
//	private boolean isDuplicateActionRequestDetected(RuleBaseValues rule, DocumentRouteHeaderValue routeHeader, RouteNodeInstance nodeInstance, RuleResponsibility resp, String qualifiedRoleName) {
//		List requests = getActionRequestService().findByStatusAndDocId(EdenConstants.ACTION_REQUEST_DONE_STATE, routeHeader.getRouteHeaderId());
//		for (Iterator iterator = requests.iterator(); iterator.hasNext();) {
//			ActionRequestValue request = (ActionRequestValue) iterator.next();
//			if (((nodeInstance != null && request.getNodeInstance() != null && request.getNodeInstance().getRouteNodeInstanceId().equals(nodeInstance.getRouteNodeInstanceId())) || request
//					.getRouteLevel().equals(routeHeader.getDocRouteLevel()))
//					&& request.getResponsibilityId().equals(resp.getResponsibilityId()) && ObjectUtils.equals(request.getQualifiedRoleName(), qualifiedRoleName)) {
//				return true;
//			}
//		}
//		return false;
//	}

	private RoleService getRoleService() {
		return KEWServiceLocator.getRoleService();
	}

	private ActionRequestService getActionRequestService() {
		return KEWServiceLocator.getActionRequestService();
	}

}
