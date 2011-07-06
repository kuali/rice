/*
 * Copyright 2005-2009 The Kuali Foundation
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

import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.web.struts.form.KualiForm;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Struts ActionForm for {@link DelegateRuleAction}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DelegateRuleForm extends KualiForm {

	private static final long serialVersionUID = 5412969516727713859L;

	private Long parentRuleId;
	private Long parentResponsibilityId;

	private RuleBaseValues parentRule;
	private RuleResponsibility parentResponsibility;
		
	private List<String> reviewers = new ArrayList<String>();
	private List<String> responsibilityTypes = new ArrayList<String>();
	private List<String> actionRequestCodes = new ArrayList<String>();
	
	public Long getParentRuleId() {
		return this.parentRuleId;
	}

	public void setParentRuleId(Long parentRuleId) {
		this.parentRuleId = parentRuleId;
	}

	public Long getParentResponsibilityId() {
		return this.parentResponsibilityId;
	}

	public void setParentResponsibilityId(Long parentResponsibilityId) {
		this.parentResponsibilityId = parentResponsibilityId;
	}

	public RuleBaseValues getParentRule() {
		return this.parentRule;
	}

	public void setParentRule(RuleBaseValues parentRule) {
	    if (this.parentRule != null 
	            && parentRule != null
	            && this.parentResponsibility != null) {
	        if (this.parentRule.getRuleBaseValuesId().longValue() != parentRule.getRuleBaseValuesId().longValue()) {
	            this.parentResponsibility = null;
	            this.parentResponsibilityId = null;
	        }
	    }
		this.parentRule = parentRule;
	}

	public RuleResponsibility getParentResponsibility() {
		return this.parentResponsibility;
	}

	public void setParentResponsibility(RuleResponsibility parentResponsibility) {
		this.parentResponsibility = parentResponsibility;
	}

	public List<String> getReviewers() {
		return this.reviewers;
	}

	public void setReviewers(List<String> reviewers) {
		this.reviewers = reviewers;
	}

	public List<String> getResponsibilityTypes() {
		return this.responsibilityTypes;
	}

	public void setResponsibilityTypes(List<String> responsibilityTypes) {
		this.responsibilityTypes = responsibilityTypes;
	}

	public List<String> getActionRequestCodes() {
		return this.actionRequestCodes;
	}

	public void setActionRequestCodes(List<String> actionRequestCodes) {
		this.actionRequestCodes = actionRequestCodes;
	}

	public String getRuleDescription() {
		if (getParentRule() == null) {
			return "";
		}
		return getParentRule().getDescription();
	}

	@Override
	public void populate(HttpServletRequest request) {
				
		super.populate(request);

		reviewers.clear();
		responsibilityTypes.clear();
		actionRequestCodes.clear();
		
		if (getParentRuleId() != null) {
			setParentRule(KEWServiceLocator.getRuleService().findRuleBaseValuesById(getParentRuleId()));
		}
		if (getParentResponsibilityId() != null && getParentRule() != null) {
			for (RuleResponsibility responsibility : getParentRule().getResponsibilities()) {
				if (responsibility.getResponsibilityId().equals(getParentResponsibilityId())) {
					setParentResponsibility(responsibility);
					break;
				}
			}
		}
		
		if (getParentRule() != null) {
			for (RuleResponsibility responsibility : getParentRule().getResponsibilities()) {
				if (KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID.equals(responsibility.getRuleResponsibilityType())) {
					Principal principal = KEWServiceLocator.getIdentityHelperService().getPrincipal(responsibility.getRuleResponsibilityName());
					if (principal != null) {
					    reviewers.add(principal.getPrincipalName());
					}
					responsibilityTypes.add(KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID_LABEL);
				} else if (KEWConstants.RULE_RESPONSIBILITY_GROUP_ID.equals(responsibility.getRuleResponsibilityType())) {
					Group group = KimApiServiceLocator.getGroupService().getGroup(responsibility.getRuleResponsibilityName());
					if (group != null) {
					    reviewers.add(group.getNamespaceCode() + " " + group.getName());
					}
					responsibilityTypes.add(KEWConstants.RULE_RESPONSIBILITY_GROUP_ID_LABEL);
				} else if (KEWConstants.RULE_RESPONSIBILITY_ROLE_ID.equals(responsibility.getRuleResponsibilityType())) {
					reviewers.add(responsibility.getResolvedRoleName());
					responsibilityTypes.add(KEWConstants.RULE_RESPONSIBILITY_ROLE_ID_LABEL);
				} else {
					throw new RiceRuntimeException("Encountered a responsibility with an invalid type, type value was " + responsibility.getRuleResponsibilityType());
				}
				actionRequestCodes.add(KEWConstants.ACTION_REQUEST_CODES.get(responsibility.getActionRequestedCd()));
			}
		}
		
	}

	

}
