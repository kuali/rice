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
package org.kuali.rice.kew.rule.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.kuali.rice.kew.KEWServiceLocator;
import org.kuali.rice.kew.export.ExportDataSet;
import org.kuali.rice.kew.export.ExportFormat;
import org.kuali.rice.kew.rule.RuleTemplate;
import org.kuali.rice.kew.rule.service.RuleAttributeService;
import org.kuali.rice.kew.rule.service.RuleDelegationService;
import org.kuali.rice.kew.rule.service.RuleService;
import org.kuali.rice.kew.rule.service.RuleTemplateService;
import org.kuali.rice.kew.util.CodeTranslator;
import org.kuali.rice.kew.web.WorkflowAction;


/**
 * A Struts Action for interactig with {@link RuleTemplate}s.
 * 
 * @see RuleTemplateService
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleTemplateAction extends WorkflowAction {

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return report(mapping, form, request, response);
    }

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        RuleTemplateForm ruleTemplateForm = (RuleTemplateForm) form;
        ruleTemplateForm.setRuleAttributes(getRuleAttributeService().findAll());
        ruleTemplateForm.setActionRequestCodes(CodeTranslator.arLabels);
        return null;
    }

    public ActionForward report(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RuleTemplateForm ruleTemplateForm = (RuleTemplateForm) form;
        if (ruleTemplateForm.getCurrentRuleTemplateId() != null) {
            RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateId(ruleTemplateForm.getCurrentRuleTemplateId());
            ruleTemplateForm.setRuleTemplate(ruleTemplate);
        }

        //        establishRequiredState(request, form);
        return mapping.findForward("report");
    }
    
    public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RuleTemplateForm ruleTemplateForm = (RuleTemplateForm) form;
        ExportDataSet dataSet = new ExportDataSet(ExportFormat.XML);
        if (ruleTemplateForm.getCurrentRuleTemplateId() != null) {
            RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateId(ruleTemplateForm.getCurrentRuleTemplateId());
            dataSet.getRuleTemplates().add(ruleTemplate);
        }
        return exportDataSet(request, dataSet);
    }

    private RuleTemplateService getRuleTemplateService() {
        return (RuleTemplateService) KEWServiceLocator.getService(KEWServiceLocator.RULE_TEMPLATE_SERVICE);
    }

    private RuleService getRuleService() {
        return (RuleService) KEWServiceLocator.getService(KEWServiceLocator.RULE_SERVICE);
    }

    private RuleDelegationService getRuleDelegationService() {
        return (RuleDelegationService) KEWServiceLocator.getService(KEWServiceLocator.RULE_DELEGATION_SERVICE);
    }

    private RuleAttributeService getRuleAttributeService() {
        return (RuleAttributeService) KEWServiceLocator.getService(KEWServiceLocator.RULE_ATTRIBUTE_SERVICE);
    }
}