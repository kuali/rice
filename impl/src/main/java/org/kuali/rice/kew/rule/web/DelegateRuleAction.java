/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kew.rule.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.kew.web.KewKualiAction;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * Struts action for handling the initial Delegate Rule screen for selecting
 * the parent rule and responsibility. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DelegateRuleAction extends KewKualiAction {

	private static final String PARENT_RULE_PROPERTY = "parentRuleId";
	private static final String PARENT_RESPONSIBILITY_PROPERTY = "parentResponsibilityId";
	
	private static final String PARENT_RULE_ERROR = "delegateRule.parentRule.required";
	private static final String PARENT_RESPONSIBILITY_ERROR = "delegateRule.parentResponsibility.required";
	
	public ActionForward createDelegateRule(ActionMapping mapping, ActionForm actionForm, HttpServletRequest request, HttpServletResponse response) throws Exception {
		DelegateRuleForm form = (DelegateRuleForm) actionForm;
		if (!validateCreateDelegateRule(form)) {
			return mapping.findForward("basic");
		}
		return mapping.findForward("basic");
	}
	
	protected boolean validateCreateDelegateRule(DelegateRuleForm form) {
		if (form.getParentRule() == null) {
			GlobalVariables.getErrorMap().putError(PARENT_RULE_PROPERTY, PARENT_RULE_ERROR);
		}
		if (form.getParentResponsibility() == null) {
			GlobalVariables.getErrorMap().putError(PARENT_RESPONSIBILITY_PROPERTY, PARENT_RESPONSIBILITY_ERROR);
		}
		return GlobalVariables.getErrorMap().isEmpty();
	}
	
}
