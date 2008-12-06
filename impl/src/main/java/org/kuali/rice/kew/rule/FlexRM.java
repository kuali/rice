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
package org.kuali.rice.kew.rule;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.kew.actionrequest.ActionRequestFactory;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.service.ActionRequestService;
import org.kuali.rice.kew.dto.GroupIdDTO;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.node.NodeState;
import org.kuali.rice.kew.engine.node.RouteNode;
import org.kuali.rice.kew.engine.node.RouteNodeInstance;
import org.kuali.rice.kew.exception.KEWUserNotFoundException;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;
import org.kuali.rice.kew.identity.IdentityFactory;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttribute;
import org.kuali.rice.kew.rule.service.RuleService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.Recipient;
import org.kuali.rice.kew.user.RoleRecipient;
import org.kuali.rice.kew.user.WorkflowUserId;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.PerformanceLogger;
import org.kuali.rice.kew.util.ResponsibleParty;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kew.workgroup.WorkflowGroupId;


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

    private static final Logger LOG = Logger.getLogger(FlexRM.class);
    
    /**
     * The default type of rule selector implementation to use if none is explicitly
     * specified for the node.
     */
    public static final String DEFAULT_RULE_SELECTOR = "Template";
    /**
     * Package in which rule selector implementations live
     */
    private static final String RULE_SELECTOR_PACKAGE = "org.kuali.rice.kew.rule";
    /**
     * The class name suffix all rule selectors should have; e.g. FooRuleSelector
     */
    private static final String RULE_SELECTOR_SUFFIX= "RuleSelector";

    private final Timestamp effectiveDate;
    /**
     * An accumulator that keeps track of the number of rules that have been selected over the lifespan of
     * this FlexRM instance.
     */
    private int selectedRules;

    public FlexRM() {
        this.effectiveDate = null;
    }

    public FlexRM(Timestamp effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    /*public List<ActionRequestValue> getActionRequests(DocumentRouteHeaderValue routeHeader, String ruleTemplateName) throws KEWUserNotFoundException, WorkflowException {
	return getActionRequests(routeHeader, null, ruleTemplateName);
    }*/

    // loads a RuleSelector implementation
    protected RuleSelector loadRuleSelector(RouteNode routeNodeDef, RouteNodeInstance nodeInstance) throws WorkflowException {
        // first see if there ruleselector is configured on a nodeinstance basis
        NodeState ns = null;
        if (nodeInstance != null) {
            ns = nodeInstance.getNodeState(KEWConstants.RULE_SELECTOR_NODE_STATE_KEY);
        }
        String ruleSelectorName = null;
        if (ns != null) {
            ruleSelectorName = ns.getValue();
        } else {
            // otherwise pull it from the RouteNode definition/prototype
            Map<String, String> nodeCfgParams = Utilities.getKeyValueCollectionAsMap(
                    routeNodeDef.
                    getConfigParams());
            ruleSelectorName = nodeCfgParams.get(RouteNode.RULE_SELECTOR_CFG_KEY);
        }

        if (ruleSelectorName == null) {
            ruleSelectorName = DEFAULT_RULE_SELECTOR;
        }
        ruleSelectorName = StringUtils.capitalize(ruleSelectorName);

        // load up the rule selection implementation
        String className = RULE_SELECTOR_PACKAGE + "." + ruleSelectorName + RULE_SELECTOR_SUFFIX;
        Class<?> ruleSelectorClass;
        try {
            ruleSelectorClass = ClassLoaderUtils.getDefaultClassLoader().loadClass(className);
        } catch (ClassNotFoundException cnfe) {
            throw new WorkflowException("Rule selector implementation '" + className + "' not found", cnfe);
        }
        if (!RuleSelector.class.isAssignableFrom(ruleSelectorClass)) {
            throw new WorkflowException("Specified class '" + ruleSelectorClass + "' does not implement RuleSelector interface");
        }
        RuleSelector ruleSelector;
        try {
            ruleSelector = ((Class<RuleSelector>) ruleSelectorClass).newInstance();
        } catch (Exception e) {
            throw new WorkflowException("Error instantiating rule selector implementation '" + ruleSelectorClass + "'", e);
        }

        return ruleSelector;
    }

    /**
     * Generates action requests
     * @param routeHeader the document route header
     * @param nodeInstance the route node instance; this may NOT be null
     * @param ruleTemplateName the rule template
     * @return list of action requests
     * @throws KEWUserNotFoundException
     * @throws WorkflowException
     */
    public List<ActionRequestValue> getActionRequests(DocumentRouteHeaderValue routeHeader, RouteNodeInstance nodeInstance, String ruleTemplateName) throws KEWUserNotFoundException, WorkflowException {
        return getActionRequests(routeHeader, nodeInstance.getRouteNode(), nodeInstance, ruleTemplateName);
    }

    /**
     * Generates action requests
     * @param routeHeader the document route header
     * @param routeNodeDef the RouteNode definition of the route node instance
     * @param nodeInstance the route node instance; this may be null!
     * @param ruleTemplateName the rule template
     * @return list of action requests
     * @throws KEWUserNotFoundException
     * @throws WorkflowException
     */
    public List<ActionRequestValue> getActionRequests(DocumentRouteHeaderValue routeHeader, RouteNode routeNodeDef, RouteNodeInstance nodeInstance, String ruleTemplateName) throws KEWUserNotFoundException, WorkflowException {
	RouteContext context = RouteContext.getCurrentRouteContext();
	// TODO really the route context just needs to be able to support nested create and clears
	// (i.e. a Stack model similar to transaction intercepting in Spring) and we wouldn't have to do this
	if (context.getDocument() == null) {
	    context.setDocument(routeHeader);
	}
        if (context.getNodeInstance() == null) {
            context.setNodeInstance(nodeInstance);
        }

	LOG.debug("Making action requests for document " + routeHeader.getRouteHeaderId());
	
	RuleSelector ruleSelector = loadRuleSelector(routeNodeDef, nodeInstance);

	List<Rule> rules = ruleSelector.selectRules(context, routeHeader, nodeInstance, ruleTemplateName, effectiveDate);

	// XXX: FIXME: this is a special case hack to expose info from the default selection implementation
	// this is used in exactly one place, RoutingReportAction, to make a distinction between no rules being
	// selected, and no rules actually matching when evaluated
	// if (numberOfRules == 0) {
        //   errors.add(new WorkflowServiceErrorImpl("There are no rules.", "routereport.noRules"));
        // } else {
        //   errors.add(new WorkflowServiceErrorImpl("There are rules, but no matches.", "routereport.noMatchingRules"));
        // }
	if (ruleSelector instanceof TemplateRuleSelector) {
	    selectedRules += ((TemplateRuleSelector) ruleSelector).getNumberOfSelectedRules();
	}

	PerformanceLogger performanceLogger = new PerformanceLogger();

	ActionRequestFactory arFactory = new ActionRequestFactory(routeHeader, context.getNodeInstance());
	
	List<ActionRequestValue> actionRequests = new ArrayList<ActionRequestValue>();
	if (rules != null) {
	    for (Rule rule: rules) {
	        RuleExpressionResult result = rule.evaluate(rule, context);
	        if (result.isSuccess() && result.getResponsibilities() != null) {
	            // actionRequests.addAll(makeActionRequests(context, rule, routeHeader, null, null));
	            makeActionRequests(arFactory, result.getResponsibilities(), context, rule.getDefinition(), routeHeader, null, null);
	        }
	    }
	}
	actionRequests = new ArrayList<ActionRequestValue>(arFactory.getRequestGraphs());
	performanceLogger.log("Time to make action request for template " + ruleTemplateName);

	return actionRequests;
    }

    public ResponsibleParty resolveResponsibilityId(Long responsibilityId) {
    	if (responsibilityId == null) {
    		throw new IllegalArgumentException("A null responsibilityId was passed to resolve responsibility!");
    	}
    	RuleResponsibility resp = getRuleService().findRuleResponsibility(responsibilityId);
    	if (resp.isUsingRole()) {
    		return new ResponsibleParty(resp.getResolvedRoleName());
    	} else if (resp.isUsingWorkflowUser()) {
    		return new ResponsibleParty(new WorkflowUserId(resp.getRuleResponsibilityName()));
    	} else if (resp.isUsingGroup()) {
    		GroupIdDTO groupId = IdentityFactory.newGroupId(resp.getRuleResponsibilityName());
    		return new ResponsibleParty(groupId);
    	}
    	throw new RiceRuntimeException("Failed to resolve responsibility from responsibility ID " + responsibilityId + ".  Responsibility was an invalid type: " + resp);
    }

    private void makeActionRequests(ActionRequestFactory arFactory, RouteContext context, RuleBaseValues rule, DocumentRouteHeaderValue routeHeader, ActionRequestValue parentRequest, RuleDelegation ruleDelegation)
    throws KEWUserNotFoundException, WorkflowException {

	List responsibilities = rule.getResponsibilities();
	makeActionRequests(arFactory, responsibilities, context, rule, routeHeader, parentRequest, ruleDelegation);
    }

    public void makeActionRequests(ActionRequestFactory arFactory, List<RuleResponsibility> responsibilities, RouteContext context, RuleBaseValues rule, DocumentRouteHeaderValue routeHeader, ActionRequestValue parentRequest, RuleDelegation ruleDelegation)
    throws KEWUserNotFoundException, WorkflowException {

//	Set actionRequests = new HashSet();
	for (Iterator iter = responsibilities.iterator(); iter.hasNext();) {
	    RuleResponsibility resp = (RuleResponsibility) iter.next();
//	    arFactory = new ActionRequestFactory(routeHeader);

	    if (resp.isUsingRole()) {
		makeRoleActionRequests(arFactory, context, rule, resp, routeHeader, parentRequest, ruleDelegation);
	    } else {
		makeActionRequest(arFactory, context, rule, routeHeader, resp, parentRequest, ruleDelegation);
	    }
//	    if (arFactory.getRequestGraph() != null) {
//	    actionRequests.add(arFactory.getRequestGraph());
//	    }
	}
    }

    private void buildDelegationGraph(ActionRequestFactory arFactory, RouteContext context, RuleBaseValues delegationRule, DocumentRouteHeaderValue routeHeaderValue, ActionRequestValue parentRequest, RuleDelegation ruleDelegation)
    throws KEWUserNotFoundException, WorkflowException {
	context.setActionRequest(parentRequest);
	if (delegationRule.getActiveInd().booleanValue() && delegationRule.getToDate().after(new Date()) && delegationRule.getFromDate().before(new Date())) {
	    for (Iterator iter = delegationRule.getResponsibilities().iterator(); iter.hasNext();) {
		RuleResponsibility delegationResp = (RuleResponsibility) iter.next();
		if (delegationResp.isUsingRole()) {
		    makeRoleActionRequests(arFactory, context, delegationRule, delegationResp, routeHeaderValue, parentRequest, ruleDelegation);
		} else if (delegationRule.isMatch(context.getDocumentContent())) {
		    makeActionRequest(arFactory, context, delegationRule, routeHeaderValue, delegationResp, parentRequest, ruleDelegation);
		}
	    }
	}
    }

    /**
     * Generates action requests for a role responsibility
     */
    private void makeRoleActionRequests(ActionRequestFactory arFactory, RouteContext context, RuleBaseValues rule, RuleResponsibility resp, DocumentRouteHeaderValue routeHeader, ActionRequestValue parentRequest,
	    RuleDelegation ruleDelegation) throws KEWUserNotFoundException, WorkflowException {

	String roleName = resp.getResolvedRoleName();
	RoleAttribute roleAttribute = resp.resolveRoleAttribute();
	setRuleAttribute(roleAttribute, rule, resp.getRoleAttributeName());
	List<String> qualifiedRoleNames = new ArrayList<String>();
	if (parentRequest != null && parentRequest.getQualifiedRoleName() != null) {
	    qualifiedRoleNames.add(parentRequest.getQualifiedRoleName());
	} else {
	    qualifiedRoleNames.addAll(roleAttribute.getQualifiedRoleNames(roleName, context.getDocumentContent()));
	}
	for (Iterator iter = qualifiedRoleNames.iterator(); iter.hasNext();) {
	    String qualifiedRoleName = (String) iter.next();
	    if (parentRequest == null && isDuplicateActionRequestDetected(routeHeader, context.getNodeInstance(), resp, qualifiedRoleName)) {
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
			    buildDelegationGraph(arFactory, context, childRuleDelegation.getDelegationRuleBaseValues(), routeHeader, request, childRuleDelegation);
			}
		    }
		}
	    } else {
		arFactory.addDelegationRoleRequest(parentRequest, resp.getApprovePolicy(), recipient, resp.getResponsibilityId(), rule.getIgnorePrevious(), ruleDelegation.getDelegationType(), rule.getDescription(), rule.getRuleBaseValuesId());
	    }
	}
    }
    
    /**
	 * Determines if the attribute has a setRuleAttribute method and then sets the value appropriately if it does.
	 */
	private void setRuleAttribute(RoleAttribute roleAttribute, RuleBaseValues rule, String roleAttributeName) {
	    // look for a setRuleAttribute method on the RoleAttribute
	    Method setRuleAttributeMethod = null;
	    try {
		setRuleAttributeMethod = roleAttribute.getClass().getMethod("setRuleAttribute", RuleAttribute.class);
	    } catch (NoSuchMethodException e) {}
	    if (setRuleAttributeMethod == null) {
		return;
	    }
	    // find the RuleAttribute by looking through the RuleTemplate
	    RuleTemplate ruleTemplate = rule.getRuleTemplate();
	    if (ruleTemplate != null) {
		for (Iterator iterator = ruleTemplate.getActiveRuleTemplateAttributes().iterator(); iterator.hasNext();) {
		    RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) iterator.next();
		    RuleAttribute ruleAttribute = ruleTemplateAttribute.getRuleAttribute();
		    if (ruleAttribute.getClassName().equals(roleAttributeName)) {
			// this is our RuleAttribute!
			try {
			    setRuleAttributeMethod.invoke(roleAttribute, ruleAttribute);
			    break;
			} catch (Exception e) {
			    throw new WorkflowRuntimeException("Failed to set RuleAttribute on our RoleAttribute!", e);
			}
		    }
		} 
	    }
	}

    /**
     * Generates action requests for a non-role responsibility, either a user or workgroup
     */
    private void makeActionRequest(ActionRequestFactory arFactory, RouteContext context, RuleBaseValues rule, DocumentRouteHeaderValue routeHeader, RuleResponsibility resp, ActionRequestValue parentRequest,
	    RuleDelegation ruleDelegation) throws KEWUserNotFoundException, WorkflowException {
	if (parentRequest == null && isDuplicateActionRequestDetected(routeHeader, context.getNodeInstance(), resp, null)) {
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
		    buildDelegationGraph(arFactory, context, childRuleDelegation.getDelegationRuleBaseValues(), routeHeader, actionRequest, childRuleDelegation);
		}
	    }
	} else {
	    arFactory.addDelegationRequest(parentRequest, recipient, resp.getResponsibilityId(), rule.getIgnorePrevious(), ruleDelegation.getDelegationType(), rule.getDescription(), rule.getRuleBaseValuesId());
	}
    }

    private boolean isDuplicateActionRequestDetected(DocumentRouteHeaderValue routeHeader, RouteNodeInstance nodeInstance, RuleResponsibility resp, String qualifiedRoleName) {
	List requests = getActionRequestService().findByStatusAndDocId(KEWConstants.ACTION_REQUEST_DONE_STATE, routeHeader.getRouteHeaderId());
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

    public int getNumberOfMatchingRules() {
	return selectedRules;
    }

//  private DocumentContent parseDocumentContent(RouteContext context) throws WorkflowException {
//  try {
//  return new StandardDocumentContent(context.getDocument().getDocContent(), context);
//  } catch (Exception e) {
//  throw new WorkflowException("Error parsing doc content for document " + context.getDocument().getRouteHeaderId());
//  }
//  }
}