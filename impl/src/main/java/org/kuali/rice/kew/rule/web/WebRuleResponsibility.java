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

package org.kuali.rice.kew.rule.web;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.kew.api.action.ActionRequestPolicy;
import org.kuali.rice.kew.api.action.DelegationType;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kew.rule.RuleExtension;
import org.kuali.rice.kew.rule.RuleExtensionValue;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.rule.service.RuleService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.entity.principal.Principal;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.util.KRADConstants;


/**
 * A decorator around a {@link RuleResponsibility} object which provides some
 * convienance functions for interacting with the bean from the web-tier.
 * This helps to alleviate some of the weaknesses of JSTL.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WebRuleResponsibility extends RuleResponsibility {

	private static final long serialVersionUID = -8422695726158274189L;

	private static final String DISPLAY_INLINE = "display:inline";

	private static final String DISPLAY_NONE = "display:none";

	private String reviewer;

	private String reviewerStyle = "";

	private String personLookupStyle = "";

	private String workgroupLookupStyle = "";

	private String roleReviewer;

	private String roleAreaStyle = "";

	private boolean delegationRulesMaterialized = false;

	private boolean showDelegations = false;

	private int numberOfDelegations;

	private int index = 0;

	private boolean hasDelegateRuleTemplate = false;

	/**
	 * "reviewerId added to support links to workgroup report or user report
	 */

	private String reviewerId;

	public String getReviewerId() {
		return reviewerId;
	}

	public void setReviewerId(String reviewerId) {
		this.reviewerId = reviewerId;
	}

	public WebRuleResponsibility() {
		setRuleResponsibilityType(KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID);
		setApprovePolicy(ActionRequestPolicy.FIRST.getCode());
	}

	public void initialize() throws Exception {
		if (getDelegationRules().size() <= Integer.parseInt(CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KEWConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.RULE_DETAIL_TYPE, KEWConstants.RULE_DELEGATE_LIMIT))) {
			showDelegations = true;
		}
		setNumberOfDelegations(getDelegationRules().size());
		if (delegationRulesMaterialized) {
			for (Iterator iterator = getDelegationRules().iterator(); iterator.hasNext();) {
				RuleDelegation ruleDelegation = (RuleDelegation) iterator.next();
				WebRuleBaseValues webRule = (WebRuleBaseValues) ruleDelegation.getDelegationRuleBaseValues();
				webRule.initialize();
			}
		}
		establishRequiredState();
	}

	private void loadWebValues() throws Exception {
		if (!org.apache.commons.lang.StringUtils.isEmpty(getRuleResponsibilityName())) {
			if (KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID.equals(getRuleResponsibilityType())) {
				// setReviewer(getUserService().getWorkflowUser(new
				// WorkflowUserId(getRuleResponsibilityName())).getPrincipalName().getAuthenticationId());
				Principal principal = KEWServiceLocator.getIdentityHelperService().getPrincipal(getRuleResponsibilityName());
				setReviewer(principal.getPrincipalName());
				setReviewerId(principal.getPrincipalId());
			} else if (KEWConstants.RULE_RESPONSIBILITY_GROUP_ID.equals(getRuleResponsibilityType())) {
				// setReviewer(getWorkgroupService().getWorkgroup(new
				// WorkflowGroupId(new
				// Long(getRuleResponsibilityName()))).getGroupNameId().getNameId());
				Group group = KimApiServiceLocator.getIdentityManagementService().
	                  getGroup(getRuleResponsibilityName());
				setReviewer(group.getName());
				setReviewerId(group.getId());
			} else if (KEWConstants.RULE_RESPONSIBILITY_ROLE_ID.equals(getRuleResponsibilityType())) {
				setRoleReviewer(getRuleResponsibilityName());
				setReviewer(getResolvedRoleName());
			}
		}
	}

	private void injectWebMembers() throws Exception {
        DelegationRulesProxy delegationRulesProxy = new DelegationRulesProxy(getDelegationRules());
        Class delegationRulesClass = getDelegationRules().getClass();
        //System.err.println("delegation rules class: "+ delegationRulesClass);
        Class[] delegationRulesInterfaces = new Class[0]; // = delegationRulesClass.getInterfaces();
        List<Class> delegationRulesInterfaceList = (List<Class>) ClassUtils.getAllInterfaces(delegationRulesClass);
        delegationRulesInterfaces = delegationRulesInterfaceList.toArray(delegationRulesInterfaces);
        ClassLoader delegationRulesClassLoader = getDelegationRules().getClass().getClassLoader();
        Object o = Proxy.newProxyInstance(delegationRulesClassLoader, delegationRulesInterfaces, delegationRulesProxy);
        //setDelegationRules((List) o);

		if (Integer.parseInt(CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString(KEWConstants.KEW_NAMESPACE, KRADConstants.DetailTypes.RULE_DETAIL_TYPE, KEWConstants.RULE_DELEGATE_LIMIT)) > getDelegationRules().size() || showDelegations) {
			for (Iterator iterator = getDelegationRules().iterator(); iterator.hasNext();) {
				RuleDelegation ruleDelegation = (RuleDelegation) iterator.next();
				WebRuleBaseValues webRule = new WebRuleBaseValues();
				webRule.load(ruleDelegation.getDelegationRuleBaseValues());
				webRule.edit(ruleDelegation.getDelegationRuleBaseValues());
				ruleDelegation.setDelegationRuleBaseValues(webRule);
			}
		}
	}

	public RuleDelegation addNewDelegation() {
		RuleDelegation ruleDelegation = new RuleDelegation();
		ruleDelegation.setDelegationRuleBaseValues(new WebRuleBaseValues());
		ruleDelegation.setDelegationType(DelegationType.PRIMARY.getCode());
		ruleDelegation.getDelegationRuleBaseValues().setDelegateRule(Boolean.TRUE);
		ruleDelegation.getDelegationRuleBaseValues().setDocTypeName(getRuleBaseValues().getDocTypeName());
		getDelegationRules().add(ruleDelegation);
		showDelegations = true;
		return ruleDelegation;
	}

	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}

	public void setWorkgroupId(String workgroupId) {
	    Group workgroup = KimApiServiceLocator.getIdentityManagementService().getGroup(workgroupId);
		//Workgroup workgroup = getWorkgroupService().getWorkgroup(new WorkflowGroupId(workgroupId));
		if (workgroup != null) {
			setReviewer(workgroup.getName());
		} else {
			setReviewer("");
		}
	}

	public String getPersonLookupStyle() {
		return personLookupStyle;
	}

	public void setPersonLookupStyle(String personLookupStyle) {
		this.personLookupStyle = personLookupStyle;
	}

	public String getReviewerStyle() {
		return reviewerStyle;
	}

	public void setReviewerStyle(String reviewerStyle) {
		this.reviewerStyle = reviewerStyle;
	}

	public String getRoleAreaStyle() {
		return roleAreaStyle;
	}

	public void setRoleAreaStyle(String roleAreaLookupStyle) {
		this.roleAreaStyle = roleAreaLookupStyle;
	}

	public String getWorkgroupLookupStyle() {
		return workgroupLookupStyle;
	}

	public void setWorkgroupLookupStyle(String workgroupLookupStyle) {
		this.workgroupLookupStyle = workgroupLookupStyle;
	}

	public RuleDelegation getDelegationRule(int index) {
		while (getDelegationRules().size() <= index) {
			addNewDelegation();
		}
		return (RuleDelegation) getDelegationRules().get(index);
	}

	public int getNumberOfDelegations() {
		return numberOfDelegations;
	}

	public void setNumberOfDelegations(int numberOfDelegations) {
		this.numberOfDelegations = numberOfDelegations;
	}

	public boolean isDelegationRulesMaterialized() {
		return delegationRulesMaterialized;
	}

	public void setDelegationRulesMaterialized(boolean isDelegationRulesMaterialized) {
		this.delegationRulesMaterialized = isDelegationRulesMaterialized;
	}

	public String getRoleReviewer() {
		return roleReviewer;
	}

	public void setRoleReviewer(String roleReviewer) {
		this.roleReviewer = roleReviewer;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isShowDelegations() {
		return showDelegations;
	}

	public void setShowDelegations(boolean showDelegations) {
		this.showDelegations = showDelegations;
	}

	public void establishRequiredState() throws Exception {
		if (KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID.equals(getRuleResponsibilityType())) {
			reviewerStyle = DISPLAY_INLINE;
			personLookupStyle = DISPLAY_INLINE;
			workgroupLookupStyle = DISPLAY_NONE;
			roleAreaStyle = DISPLAY_NONE;
		}
		if (KEWConstants.RULE_RESPONSIBILITY_GROUP_ID.equals(getRuleResponsibilityType())) {
			reviewerStyle = DISPLAY_INLINE;
			personLookupStyle = DISPLAY_NONE;
			workgroupLookupStyle = DISPLAY_INLINE;
			roleAreaStyle = DISPLAY_NONE;
		}
		if (KEWConstants.RULE_RESPONSIBILITY_ROLE_ID.equals(getRuleResponsibilityType())) {
			reviewerStyle = DISPLAY_NONE;
			personLookupStyle = DISPLAY_NONE;
			workgroupLookupStyle = DISPLAY_NONE;
			roleAreaStyle = DISPLAY_INLINE;
		}
		loadWebValues();
		if (delegationRulesMaterialized) {
			for (Iterator iterator = getDelegationRules().iterator(); iterator.hasNext();) {
				RuleDelegation delegation = (RuleDelegation) iterator.next();
				((WebRuleBaseValues) delegation.getDelegationRuleBaseValues()).establishRequiredState();
			}
		}
	}

	public void validateResponsibility(String keyPrefix, ActionErrors errors) {
		if (KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID.equals(getRuleResponsibilityType())) {
			boolean invalidUser = org.apache.commons.lang.StringUtils.isEmpty(getReviewer());
			if (!invalidUser)
			{
				//chb: 10Jan2009: not using KEW IdentityHelperService b/c we want to deal w/ exception here
				Principal principal = KimApiServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(getReviewer());
				if( principal != null)
				{
					setRuleResponsibilityName(principal.getPrincipalId());
				}
				else
				{
					invalidUser = true;
				}
			}
			if (invalidUser) {
				errors.add(keyPrefix + "reviewer", new ActionMessage("routetemplate.ruleservice.user.invalid"));
			}
		} else if (KEWConstants.RULE_RESPONSIBILITY_GROUP_ID.equals(getRuleResponsibilityType())) {
			boolean invalidWorkgroup = org.apache.commons.lang.StringUtils.isEmpty(getReviewer());
			;
			if (!invalidWorkgroup) {
			    Group workgroup = KimApiServiceLocator.getIdentityManagementService().getGroup(getReviewerId());
				if (workgroup == null) {
					invalidWorkgroup = true;
				} else {
					setRuleResponsibilityName(workgroup.getId());
				}
			} else {
				errors.add(keyPrefix + "reviewer", new ActionMessage("routetemplate.ruleservice.workgroup.invalid"));
			}

		} else if (KEWConstants.RULE_RESPONSIBILITY_ROLE_ID.equals(getRuleResponsibilityType())) {
			setRuleResponsibilityName(getRoleReviewer());
		}

		int delIndex = 0;
		for (Iterator respIterator = getDelegationRules().iterator(); respIterator.hasNext();) {
			String delPrefix = keyPrefix + "delegationRule[" + delIndex + "].delegationRuleBaseValues.";
			RuleDelegation ruleDelegation = (RuleDelegation) respIterator.next();
			((WebRuleBaseValues) ruleDelegation.getDelegationRuleBaseValues()).validateRule(delPrefix, errors);
		}
	}

	public void edit(RuleResponsibility ruleResponsibility) throws Exception {
		load(ruleResponsibility);
		initialize();
	}

	public void load(RuleResponsibility ruleResponsibility) throws Exception {
		PropertyUtils.copyProperties(this, ruleResponsibility);
		injectWebMembers();
	}

	public void loadDelegations() throws Exception {
		fetchDelegations();

		for (Iterator iterator = getDelegationRules().iterator(); iterator.hasNext();) {
			RuleDelegation ruleDelegation = (RuleDelegation) iterator.next();
			WebRuleBaseValues webRule = new WebRuleBaseValues();
			webRule.edit(ruleDelegation.getDelegationRuleBaseValues());
			ruleDelegation.setDelegationRuleBaseValues(webRule);
		}
		delegationRulesMaterialized = true;
		populatePreviousVersionIds();
	}

	public void populatePreviousVersionIds() {
		if (delegationRulesMaterialized) {
			for (Iterator iterator = getDelegationRules().iterator(); iterator.hasNext();) {
				RuleDelegation delegation = (RuleDelegation) iterator.next();
				((WebRuleBaseValues) delegation.getDelegationRuleBaseValues()).populatePreviousVersionIds();
			}
		}
	}

	private void fetchDelegations() {
		if (getRuleResponsibilityKey() != null) {
			RuleResponsibility responsibility = getRuleService().findByRuleResponsibilityId(getRuleResponsibilityKey());
			if (responsibility == null) {
				return;
			}
			getDelegationRules().addAll(responsibility.getDelegationRules());
		}
	}

	public void prepareHiddenDelegationsForRoute() {
		if (showDelegations) {
			return;
		}

		fetchDelegations();

		for (Iterator iter = getDelegationRules().iterator(); iter.hasNext();) {
			RuleDelegation delegation = (RuleDelegation) iter.next();
			delegation.setDelegateRuleId(null);
			delegation.setVersionNumber(null);
			delegation.setRuleDelegationId(null);
			//delegation.setRuleResponsibility(this);
			delegation.setResponsibilityId(null);

			RuleBaseValues rule = delegation.getDelegationRuleBaseValues();
			rule.setVersionNumber(null);
			rule.setPreviousVersionId(rule.getRuleBaseValuesId());
			rule.setDocumentId(null);
			rule.setRuleBaseValuesId(null);

			for (Iterator iterator = rule.getResponsibilities().iterator(); iterator.hasNext();) {
				RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
				responsibility.setVersionNumber(null);
				responsibility.setRuleBaseValuesId(null);
				responsibility.setRuleBaseValues(rule);
				responsibility.setRuleResponsibilityKey(null);
			}

			for (Iterator iterator = rule.getRuleExtensions().iterator(); iterator.hasNext();) {
				RuleExtension extension = (RuleExtension) iterator.next();
				extension.setLockVerNbr(null);
				extension.setRuleBaseValues(rule);
				extension.setRuleBaseValuesId(null);
				extension.setRuleExtensionId(null);

				for (Iterator iter2 = extension.getExtensionValues().iterator(); iter2.hasNext();) {
					RuleExtensionValue value = (RuleExtensionValue) iter2.next();
					value.setExtension(extension);
					value.setLockVerNbr(null);
					value.setRuleExtensionId(null);
					value.setRuleExtensionValueId(null);
				}
			}
		}
	}

	public boolean isHasDelegateRuleTemplate() {
		return hasDelegateRuleTemplate;
	}

	public void setHasDelegateRuleTemplate(boolean hasDelegateRuleTemplate) {
		this.hasDelegateRuleTemplate = hasDelegateRuleTemplate;
	}

	private RuleService getRuleService() {
		return (RuleService) KEWServiceLocator.getService(KEWServiceLocator.RULE_SERVICE);
	}

	/**
	 * Just a little dynamic proxy to keep us from establishing required state
	 * on the delegation rules if they haven't been materialized from the
	 * database yet (they are currenty proxied by OJB)
	 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
	 */
	private class DelegationRulesProxy implements InvocationHandler, java.io.Serializable {

		private static final long serialVersionUID = 7046323200221509473L;

		private List delegationRules;

		public DelegationRulesProxy(List delegationRules) {
			this.delegationRules = delegationRules;
		}

		public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
			if (!delegationRulesMaterialized && !m.getName().equals("isEmpty") && !m.getName().equals("size")) {
				for (Iterator iterator = delegationRules.iterator(); iterator.hasNext();) {
					RuleDelegation ruleDelegation = (RuleDelegation) iterator.next();
					WebRuleBaseValues webRule = new WebRuleBaseValues();
					webRule.load(ruleDelegation.getDelegationRuleBaseValues());
					webRule.establishRequiredState();
					ruleDelegation.setDelegationRuleBaseValues(webRule);
				}
				delegationRulesMaterialized = true;

			}
			return m.invoke(delegationRules, args);
		}

	}

}
