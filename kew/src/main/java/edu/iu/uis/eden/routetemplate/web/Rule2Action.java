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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.clientapp.IDocHandler;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.DocumentTypeVO;
import edu.iu.uis.eden.clientapp.vo.WorkflowIdVO;
import edu.iu.uis.eden.exception.WorkflowException;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.export.ExportFormat;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;
import edu.iu.uis.eden.routeheader.DocumentRouteHeaderValue;
import edu.iu.uis.eden.routetemplate.MyRules2;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleDelegation;
import edu.iu.uis.eden.routetemplate.RuleDelegationService;
import edu.iu.uis.eden.routetemplate.RuleResponsibility;
import edu.iu.uis.eden.routetemplate.RuleService;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.routetemplate.RuleTemplateService;
import edu.iu.uis.eden.util.CodeTranslator;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.web.ShowHideTree;
import edu.iu.uis.eden.web.WorkflowAction;

/**
 * A Struts Action for interacting with the Rules engine.  Provides creation,
 * editing, and report for rules.
 * 
 * @see RuleService
 * @see RuleBaseValues
 * @see WebRuleBaseValues
 * @see WebRuleResponsibility
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Rule2Action extends WorkflowAction {

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form rule2Form = (Rule2Form) form;
        rule2Form.setChoosingTemplate(true);
        //createFlexDoc(request, rule2Form);
        return mapping.findForward("basic");
    }

    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;

        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + mapping.getModuleConfig().getPrefix();;
        
        String lookupType = ruleForm.getLookupType();
        ruleForm.setLookupType(null);

        StringBuffer lookupUrl = new StringBuffer(basePath);
        lookupUrl.append("/Lookup.do?methodToCall=start&docFormKey=").append(getUserSession(request).addObject(form)).append("&lookupableImplServiceName=");
        lookupUrl.append(request.getParameter("lookupableImplServiceName"));
        lookupUrl.append("&conversionFields=").append(request.getParameter("conversionFields"));

        if (lookupType != null && !lookupType.equals("")) {
            WorkflowLookupable workflowLookupable = (WorkflowLookupable)GlobalResourceLoader.getService(request.getParameter("lookupableImplServiceName"));// SpringServiceLocator.getExtensionService().getLookupable(request.getParameter("lookupableImplServiceName"));
            if (!Utilities.isEmpty(request.getParameter("conversionFields"))) {
                lookupUrl.append(",");
            }
            for (Iterator iterator = workflowLookupable.getDefaultReturnType().iterator(); iterator.hasNext();) {
                String returnType = (String) iterator.next();
                lookupUrl.append(returnType).append(":").append(lookupType);
            }
        }

        lookupUrl.append("&returnLocation=").append(basePath).append(mapping.getPath()).append(".do");
        return new ActionForward(lookupUrl.toString(), true);
    }

    public ActionForward report(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        List rules = new ArrayList();
        if (ruleForm.getCurrentRuleId() != null) {
            RuleBaseValues rule = getRuleService().findRuleBaseValuesById(ruleForm.getCurrentRuleId());
            if (rule != null) {
                WebRuleBaseValues webRule = new WebRuleBaseValues(rule);
                rules.add(webRule);
                initializeShowHide(ruleForm.getShowHide(), webRule);
                /**
                 * added on 2006-04-04 to support function of showing link to document type report page
                 */
                if(!Utilities.isEmpty(webRule.getDocTypeName())){
                	DocumentTypeVO docType=KEWServiceLocator.getDocumentTypeService().getDocumentTypeVO(webRule.getDocTypeName());
                	if(docType!=null){
                		ruleForm.setDocTypeId(docType.getDocTypeId());
                	}
                }
            }
        } else if (ruleForm.getDocId() != null) {
            List docRules = getRuleService().findByRouteHeaderId(ruleForm.getDocId());
            for (Iterator iterator = docRules.iterator(); iterator.hasNext();) {
                RuleBaseValues docRule = (RuleBaseValues) iterator.next();
                WebRuleBaseValues webRule = new WebRuleBaseValues(docRule);
                rules.add(webRule);
                initializeShowHide(ruleForm.getShowHide(), webRule);               
            }
            /**
             * added on 2006-04-04 to support function of showing link to document type report page
             */
           DocumentRouteHeaderValue doc=KEWServiceLocator.getRouteHeaderService().getRouteHeader(ruleForm.getDocId());
           if(doc!=null){
           		ruleForm.setDocTypeId(doc.getDocumentTypeId());
           }
           
        }
        ruleForm.setRules(rules);

        // set up show/hide of delegation rules
        int ruleIndex = 0;
        for (Iterator ruleIt = rules.iterator(); ruleIt.hasNext();) {
            int respIndex = 0;
            for (Iterator respIt = ((RuleBaseValues) ruleIt.next()).getResponsibilities().iterator(); respIt.hasNext();) {
                RuleResponsibility responsibility = (RuleResponsibility) respIt.next();
                String key = "currrule" + ruleIndex + "resp" + respIndex;
                if (ruleForm.getShowDelegationsMap().get(key) == null) {
                    int numDelegations = responsibility.getDelegationRules().size();
                    ruleForm.getShowDelegationsMap().put(key, new Boolean(numDelegations <= Integer.parseInt(Utilities.getApplicationConstant("Config.Application.DelegateLimit"))).toString());
                }
                respIndex++;
            }
            ruleIndex++;
        }

        establishRequiredState(request, form);
        return mapping.findForward("report");
    }
    
    public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        List rules = new ArrayList();
        if (ruleForm.getCurrentRuleId() != null) {
            RuleBaseValues rule = getRuleService().findRuleBaseValuesById(ruleForm.getCurrentRuleId());
            if (rule != null) {
                rules.add(rule);
            }
        } else if (ruleForm.getDocId() != null) {
            List docRules = getRuleService().findByRouteHeaderId(ruleForm.getDocId());
            for (Iterator iterator = docRules.iterator(); iterator.hasNext();) {
                RuleBaseValues docRule = (RuleBaseValues) iterator.next();
                rules.add(docRule);
            }
        }        
        ExportDataSet dataSet = new ExportDataSet(ExportFormat.XML);
        dataSet.getRules().addAll(rules);
        return exportDataSet(request, dataSet);
    }

    public ActionForward createNew(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        List errors = new ArrayList();
        if (ruleForm.getRuleCreationValues().getRuleTemplateId() == null) {
            errors.add(new WorkflowServiceErrorImpl("Rule template id required.", "rule.template.required"));
        }
        if (ruleForm.getRuleCreationValues().getDocTypeName() == null || ruleForm.getRuleCreationValues().getDocTypeName().equals("")) {
            errors.add(new WorkflowServiceErrorImpl("Document type name required.", "rule.docType.name.required"));
        }
        if (!errors.isEmpty()) {
            throw new WorkflowServiceErrorException("Errors populating rule attributes.", errors);
        }

        RuleBaseValues defaultRule = getRuleService().findDefaultRuleByRuleTemplateId(ruleForm.getRuleCreationValues().getRuleTemplateId());
        WebRuleBaseValues rule = new WebRuleBaseValues();
        
        if (defaultRule != null) {
            defaultRule.setActivationDate(null);
            defaultRule.setCurrentInd(null);
            defaultRule.setDeactivationDate(null);
            defaultRule.setDocTypeName(null);
            defaultRule.setLockVerNbr(null);
            defaultRule.setRuleBaseValuesId(null);
            defaultRule.setTemplateRuleInd(Boolean.FALSE);
            defaultRule.setVersionNbr(null);
            rule.load(defaultRule);
        }

        rule.setRuleTemplateId(ruleForm.getRuleCreationValues().getRuleTemplateId());
        rule.setDocTypeName(ruleForm.getRuleCreationValues().getDocTypeName());
        rule.setDelegateRule(Boolean.FALSE);
        rule.loadFieldsWithDefaultValues();
        
        WebRuleResponsibility responsibility = rule.createNewRuleResponsibility();
        RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateId(rule.getRuleTemplateId());
        if(ruleTemplate.getDefaultActionRequestValue() != null && ruleTemplate.getDefaultActionRequestValue().getValue() != null){
            responsibility.setActionRequestedCd(ruleTemplate.getDefaultActionRequestValue().getValue());
        }
        if (ruleTemplate.getDelegationTemplate() != null) {
            responsibility.setHasDelegateRuleTemplate(true);
        }

        responsibility.setDelegationRulesMaterialized(true);
        ruleForm.getMyRules().addRule(rule);
        createFlexDoc(request, ruleForm, ruleForm.getMyRules().getRules());
        ruleForm.getShowHide().append().append();
        establishRequiredState(request, ruleForm);
        return mapping.findForward("basic");
    }

    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        List errors = new ArrayList();
        WebRuleBaseValues webRule = null;
        if (ruleForm.getCurrentRuleId() != null) {
            RuleBaseValues rule = getRuleService().findRuleBaseValuesById(ruleForm.getCurrentRuleId());
            if (rule != null) {
                if (rule.getDelegateRule().booleanValue()) {
                    return new ActionForward("DelegateRule.do?methodToCall=copy&currentRuleId=" + ruleForm.getCurrentRuleId(), true);
                }
                webRule = WebRuleUtils.copyToNewRule(new WebRuleBaseValues(rule));
                initializeShowHide(ruleForm.getShowHide(), webRule);
            } else {
                errors.add(new WorkflowServiceErrorImpl("Could not locate rule for given id " + ruleForm.getCurrentRuleId(), "general.workflow.error", "Could not locate rule for given id " + ruleForm.getCurrentRuleId()));
            }
        } else {
            errors.add(new WorkflowServiceErrorImpl("Error copying rule, no id specified.", "general.workflow.error", "Error copying rule, no id specified."));
        }
        if (!errors.isEmpty()) {
            throw new WorkflowServiceErrorException("Errors copying rule.", errors);
        }
        
        // set up show/hide of delegation rules
        /*int ruleIndex = 0;
        for (Iterator ruleIt = rules.iterator(); ruleIt.hasNext();) {
            int respIndex = 0;
            for (Iterator respIt = ((RuleBaseValues) ruleIt.next()).getResponsibilities().iterator(); respIt.hasNext();) {
                RuleResponsibility responsibility = (RuleResponsibility) respIt.next();
                String key = "currrule" + ruleIndex + "resp" + respIndex;
                if (ruleForm.getShowDelegationsMap().get(key) == null) {
                    int numDelegations = responsibility.getDelegationRules().size();
                    ruleForm.getShowDelegationsMap().put(key, new Boolean(numDelegations <= Integer.parseInt(Utilities.getApplicationConstant("Config.Application.DelegateLimit"))).toString());
                }
                respIndex++;
            }
            ruleIndex++;
        }*/
        
        
        //responsibility.setDelegationRulesMaterialized(true);
        ruleForm.getMyRules().addRule(webRule);
        createFlexDoc(request, ruleForm, ruleForm.getMyRules().getRules());
        ruleForm.getShowHide().append().append();
        establishRequiredState(request, ruleForm);
        return mapping.findForward("basic");
    }
    
    
    
    public ActionForward copyRule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        WebRuleBaseValues rule = ruleForm.getMyRules().getRule(ruleForm.getRuleIndex().intValue());
        WebRuleBaseValues ruleCopy = WebRuleUtils.copyRuleOntoExistingDocument(rule);
                
        ruleForm.getMyRules().addRule(ruleCopy);
//        ruleForm.getShowHide().append().append();
//
//        for (int i = 0; i < responsibilities.size(); i++) {
//            ruleForm.getShowHide().getChild(ruleForm.getRuleIndex().intValue()+1).getChild(i).append().append();    
//        }
        ruleForm.setShowHide(initializeShowHide(ruleForm.getMyRules()));
        
        establishRequiredState(request, ruleForm);
        return mapping.findForward("basic");
    }
    
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        String ruleTemplateIdValue = request.getParameter("ruleTemplate.ruleTemplateId");
        if ("".equals(ruleTemplateIdValue)) {
            ruleForm.getRuleCreationValues().setRuleTemplateId(null);
            ruleForm.getRuleCreationValues().setRuleTemplateName("");
        } else if (ruleTemplateIdValue != null) {
            Long ruleTemplateId = new Long(ruleTemplateIdValue);
            ruleForm.getRuleCreationValues().setRuleTemplateId(ruleTemplateId);
            RuleTemplate template = getRuleTemplateService().findByRuleTemplateId(ruleForm.getRuleCreationValues().getRuleTemplateId());
            ruleForm.getRuleCreationValues().setRuleTemplateName(template.getName());
        }
        return mapping.findForward("basic");
    }

    public ActionForward removeResponsibility(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        int removeIndex = ruleForm.getResponsibilityIndex().intValue();
        RuleBaseValues rule = ruleForm.getMyRules().getRule(ruleForm.getRuleIndex().intValue());
        if (ruleForm.getDelegationIndex() != null) {
            removeIndex = ruleForm.getDelegationResponsibilityIndex().intValue();
            rule = rule.getResponsibility(ruleForm.getResponsibilityIndex().intValue()).getDelegationRule(ruleForm.getDelegationIndex().intValue()).getDelegationRuleBaseValues();
            ruleForm.getShowHide().getChild(ruleForm.getRuleIndex()).getChild(ruleForm.getResponsibilityIndex()).getChild(ruleForm.getDelegationIndex()).remove(removeIndex);
        } else {
            ruleForm.getShowHide().getChild(ruleForm.getRuleIndex()).remove(removeIndex);
        }
        rule.removeResponsibility(removeIndex);
        return mapping.findForward("basic");
    }

    public ActionForward removeRule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        int ruleIndex = ruleForm.getRuleIndex().intValue();
        if (ruleForm.getDelegationIndex() != null) {
            int respIndex = ruleForm.getResponsibilityIndex().intValue();
            int delIndex = ruleForm.getDelegationIndex().intValue();
            ruleForm.getMyRules().getRule(ruleIndex).getResponsibility(respIndex).getDelegationRules().remove(delIndex);
            ruleForm.getShowHide().getChild(ruleIndex).getChild(respIndex).remove(delIndex);
        } else {
            ruleForm.getMyRules().getRules().remove(ruleIndex);
            ruleForm.getShowHide().remove(ruleIndex);
        }
        return mapping.findForward("basic");
    }

    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        RuleBaseValues rule = getRuleService().findRuleBaseValuesById(ruleForm.getCurrentRuleId());
        if (rule != null) {
            ActionErrors errors = new ActionErrors();
            if (checkLockedForRouting(errors, rule, false)) {
                saveErrors(request, errors);
                return mapping.findForward("basic");
            }
            if (rule.getDelegateRule().booleanValue()) {
                //ruleForm.setMethodToCall("edit");
                //return mapping.findForward("delegateEdit");
                return new ActionForward("DelegateRule.do?methodToCall=edit&currentRuleId=" + ruleForm.getCurrentRuleId(), true);
            }

            WebRuleBaseValues webRule = new WebRuleBaseValues(rule);
            webRule.populatePreviousVersionIds();
            webRule.setPreviousVersionId(rule.getRuleBaseValuesId());
            ruleForm.getRuleCreationValues().setDocTypeName(webRule.getDocTypeName());
            ruleForm.getRuleCreationValues().setRuleTemplateName(webRule.getRuleTemplateName());
            ruleForm.getRuleCreationValues().setRuleTemplateId(webRule.getRuleTemplateId());

            for (Iterator iter = webRule.getResponsibilities().iterator(); iter.hasNext();) {
                WebRuleResponsibility responsibility = (WebRuleResponsibility) iter.next();
                if (rule.getRuleTemplate() != null && rule.getRuleTemplate().getDelegationTemplate() != null) {
                    responsibility.setHasDelegateRuleTemplate(true);
                }
            }

            ruleForm.getMyRules().addRule(webRule);
            if (rule.getDelegateRule().booleanValue()) {
                ruleForm.setEditingDelegate(true);
            }
            ruleForm.setShowHide(initializeShowHide(ruleForm.getMyRules()));
            String ruleDocumentTypeName = getRuleService().getRuleDocmentTypeName(ruleForm.getMyRules().getRules());
            ruleForm.setFlexDoc(new WorkflowDocument(new WorkflowIdVO(getUserSession(request).getWorkflowUser().getWorkflowId()), ruleDocumentTypeName));
            ruleForm.setDocId(ruleForm.getFlexDoc().getRouteHeaderId());
        }
        establishRequiredState(request, ruleForm);
        return mapping.findForward("basic");
    }

    public ActionForward delegateResponsibility(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        WebRuleBaseValues rule = ruleForm.getMyRules().getRule(ruleForm.getRuleIndex().intValue());
        WebRuleResponsibility webResponsibility = (WebRuleResponsibility) rule.getResponsibility(ruleForm.getResponsibilityIndex().intValue());
        
        RuleDelegation delegation = webResponsibility.addNewDelegation();
        WebRuleBaseValues delegationRule = ((WebRuleBaseValues) delegation.getDelegationRuleBaseValues());
        delegationRule.createNewRuleResponsibility();
        delegationRule.setDocTypeName(rule.getDocTypeName());
        
        RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateId(rule.getRuleTemplateId());
        if (ruleTemplate.getDelegationTemplate() != null) {
            delegation.getDelegationRuleBaseValues().setRuleTemplateId(ruleTemplate.getDelegationTemplate().getRuleTemplateId());

            RuleBaseValues defaultRule = getRuleService().findDefaultRuleByRuleTemplateId(ruleTemplate.getDelegationTemplate().getRuleTemplateId());
            if (defaultRule != null) {
                List ruleDelegations = getRuleDelegationService().findByDelegateRuleId(defaultRule.getRuleBaseValuesId());
                defaultRule.setActivationDate(null);
                defaultRule.setCurrentInd(null);
                defaultRule.setDeactivationDate(null);
                defaultRule.setDocTypeName(null);
                defaultRule.setLockVerNbr(null);
                defaultRule.setRuleBaseValuesId(null);
                defaultRule.setTemplateRuleInd(Boolean.FALSE);
                defaultRule.setVersionNbr(null);
                delegationRule.load(defaultRule);

                if (ruleDelegations != null && !ruleDelegations.isEmpty()) {
                    RuleDelegation defaultDelegation = (RuleDelegation) ruleDelegations.get(0);
                    delegation.setDelegationType(defaultDelegation.getDelegationType());
                }
            }
            delegationRule.loadFieldsWithDefaultValues();
        }
        webResponsibility.setDelegationRulesMaterialized(true); // TODO this is kinda nasty
        ruleForm.getShowHide().getChild(ruleForm.getRuleIndex()).getChild(ruleForm.getResponsibilityIndex()).append().append();
        establishRequiredState(request, form);
        return mapping.findForward("basic");
    }

    public ActionForward addNewResponsibility(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        int index = ruleForm.getRuleIndex().intValue();
        WebRuleBaseValues webRule = (WebRuleBaseValues) ruleForm.getMyRules().getRule(index);
        if (ruleForm.getDelegationIndex() != null) {
            webRule = (WebRuleBaseValues) webRule.getResponsibility(ruleForm.getResponsibilityIndex().intValue()).getDelegationRule(ruleForm.getDelegationIndex().intValue()).getDelegationRuleBaseValues();
            ruleForm.getShowHide().getChild(index).getChild(ruleForm.getResponsibilityIndex()).getChild(ruleForm.getDelegationIndex()).append();
        } else {
            ruleForm.getShowHide().getChild(index).append();
        }
        WebRuleResponsibility responsibility = webRule.createNewRuleResponsibility();

        RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateId(webRule.getRuleTemplateId());
        if(ruleTemplate.getDefaultActionRequestValue() != null && ruleTemplate.getDefaultActionRequestValue().getValue() != null){
            responsibility.setActionRequestedCd(ruleTemplate.getDefaultActionRequestValue().getValue());
        }
        if (ruleTemplate.getDelegationTemplate() != null) {
            responsibility.setHasDelegateRuleTemplate(true);
        }

        responsibility.setDelegationRulesMaterialized(true);
        establishRequiredState(request, form);
        return mapping.findForward("basic");
    }

    public ActionForward showDelegations(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        int ruleIndex = ruleForm.getRuleIndex().intValue();
        int respIndex = ruleForm.getResponsibilityIndex().intValue();
        WebRuleResponsibility responsibility = (WebRuleResponsibility) ruleForm.getMyRules().getRule(ruleIndex).getResponsibility(respIndex);
        responsibility.setShowDelegations(true);
        responsibility.loadDelegations();
        ShowHideTree respTree = ruleForm.getShowHide().getChild(ruleIndex).getChild(respIndex);
        for (Iterator iterator = responsibility.getDelegationRules().iterator(); iterator.hasNext();) {
            RuleDelegation delegation = (RuleDelegation) iterator.next();
            ShowHideTree delTree = respTree.append();
            for (Iterator delRespIt = delegation.getDelegationRuleBaseValues().getResponsibilities().iterator(); delRespIt.hasNext();) {
                delRespIt.next();
                delTree.append();
            }
        }
        return mapping.findForward(ruleForm.getForward());
    }

    public ActionForward showDelegationsReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward reportForward = report(mapping, form, request, response);
        Rule2Form ruleForm = (Rule2Form) form;
        int ruleIndex = ruleForm.getRuleIndex().intValue();
        int respIndex = ruleForm.getResponsibilityIndex().intValue();
        String extraId = (ruleForm.getExtraId() == null ? "" : ruleForm.getExtraId());
        WebRuleResponsibility responsibility = (WebRuleResponsibility) ((WebRuleBaseValues) ruleForm.getRules().get(ruleIndex)).getResponsibility(respIndex);
        ShowHideTree respTree = ruleForm.getShowHide().getChild(ruleIndex).getChild(respIndex);
        for (Iterator iterator = responsibility.getDelegationRules().iterator(); iterator.hasNext();) {
            RuleDelegation delegation = (RuleDelegation) iterator.next();
            ShowHideTree delTree = respTree.append();
            for (Iterator delRespIt = delegation.getDelegationRuleBaseValues().getResponsibilities().iterator(); delRespIt.hasNext();) {
                delRespIt.next();
                delTree.append();
            }
        }
        ruleForm.getShowDelegationsMap().put(extraId + "rule" + ruleIndex + "resp" + respIndex, "true");
        return reportForward;
    }

    public ActionForward showDelegationsDocHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        ActionForward forward = docHandler(mapping, form, request, response);
        int ruleIndex = ruleForm.getRuleIndex().intValue();
        int respIndex = ruleForm.getResponsibilityIndex().intValue();
        String extraId = (ruleForm.getExtraId() == null ? "" : ruleForm.getExtraId());
        WebRuleResponsibility responsibility = null;
        ShowHideTree respTree = null;
        if ("prev".equals(extraId)) {
            WebRuleBaseValues previousRuleBaseValVersion = new WebRuleBaseValues(((RuleBaseValues) ruleForm.getRules().get(ruleIndex)).getPreviousVersion());
            responsibility = (WebRuleResponsibility) previousRuleBaseValVersion.getResponsibility(respIndex);
            respTree = ruleForm.getParentShowHide().getChild(ruleIndex).getChild(respIndex);
        } else {
            responsibility = (WebRuleResponsibility) ((WebRuleBaseValues) ruleForm.getRules().get(ruleIndex)).getResponsibility(respIndex);
            respTree = ruleForm.getShowHide().getChild(ruleIndex).getChild(respIndex);
        }
        for (Iterator iterator = responsibility.getDelegationRules().iterator(); iterator.hasNext();) {
            RuleDelegation delegation = (RuleDelegation) iterator.next();
            ShowHideTree delTree = respTree.append();
            for (Iterator delRespIt = delegation.getDelegationRuleBaseValues().getResponsibilities().iterator(); delRespIt.hasNext();) {
                delRespIt.next();
                delTree.append();
            }
        }
        ruleForm.getShowDelegationsMap().put(extraId + "rule" + ruleIndex + "resp" + respIndex, "true");
        return forward;
    }

    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        ActionErrors errors = new ActionErrors();
        ActionForward forward = routeRule(errors, mapping, form, request, response, true);
        if (!errors.isEmpty()) {
            return forward;
        }
        ActionErrors messages = new ActionErrors();
        messages.add("hasErrors", new ActionMessage("general.routing.blanketApproved", "Rule with document id = " + ruleForm.getDocId()));
        saveErrors(request, messages);
        return forward;
    }

    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        ActionErrors errors = new ActionErrors();
        ActionForward forward = routeRule(errors, mapping, form, request, response, false);
        if (!errors.isEmpty()) {
            return forward;
        }
        ActionErrors messages = new ActionErrors();
        messages.add("hasErrors", new ActionMessage("rule.route.confirmation", "" + ruleForm.getDocId()));
        saveErrors(request, messages);
        return forward;
    }

    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return route(mapping, form, request, response);
    }

    public ActionForward routeRule(ActionErrors errors, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, boolean blanketApprove) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;

        MyRules2 rules = ruleForm.getMyRules();
        int ruleIndex = 0;
        for (Iterator iter = rules.getRules().iterator(); iter.hasNext();) {
            WebRuleBaseValues rule = (WebRuleBaseValues) iter.next();
            rule.validateRule("myRules.rule[" + ruleIndex + "].", errors);
            ruleIndex++;
        }
        if (!errors.isEmpty()) {
            errors.add("hasErrors", new ActionMessage("general.hasErrors"));
            saveErrors(request, errors);
            return mapping.findForward("basic");
        }
        for (Iterator iter = rules.getRules().iterator(); iter.hasNext();) {
            WebRuleBaseValues rule = (WebRuleBaseValues) iter.next();

            if (checkLockedForRouting(errors, rule, true)) {
                saveErrors(request, errors);
                return mapping.findForward("basic");
            }
            rule.materialize();
            for (Iterator iterator = rule.getResponsibilities().iterator(); iterator.hasNext();) {
                WebRuleResponsibility responsibility = (WebRuleResponsibility) iterator.next();
                if (responsibility.isShowDelegations()) {
                    for (Iterator iterator1 = responsibility.getDelegationRules().iterator(); iterator1.hasNext();) {
                        RuleDelegation delegation = (RuleDelegation) iterator1.next();
                        delegation.setRuleResponsibility(responsibility);
                        ((WebRuleBaseValues) delegation.getDelegationRuleBaseValues()).materialize();
                        for (Iterator iterator2 = delegation.getDelegationRuleBaseValues().getResponsibilities().iterator(); iterator2.hasNext();) {
                            RuleResponsibility responsibilityDelegate = (RuleResponsibility) iterator2.next();
                            responsibilityDelegate.setRuleResponsibilityKey(null);
                        }
                    }
                } else {
                    responsibility.prepareHiddenDelegationsForRoute();
                }
                responsibility.setRuleResponsibilityKey(null);
            }
        }

        Long routeId = getRuleService().route2(ruleForm.getDocId(), rules, getUserSession(request).getWorkflowUser(), ruleForm.getAnnotation(), blanketApprove);

        ruleForm.reset();
        ruleForm.setChoosingTemplate(true);
        ruleForm.setDocId(routeId);
        ruleForm.setCurrentRuleId(null);
        return report(mapping, ruleForm, request, response);
    }

    public ActionMessages establishRequiredState(HttpServletRequest request, ActionForm form) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;
        ruleForm.setActionRequestCodes(CodeTranslator.arLabels);
        ruleForm.setRuleTemplates(getRuleTemplateService().findAll());
        MyRules2 myRules = ruleForm.getMyRules();
        for (Iterator ruleIt = myRules.getRules().iterator(); ruleIt.hasNext();) {
            WebRuleBaseValues rule = (WebRuleBaseValues) ruleIt.next();
            rule.establishRequiredState();
        }
        if (ruleForm.getDocId() != null && ruleForm.getFlexDoc() == null) {
            ruleForm.setFlexDoc(new WorkflowDocument(new WorkflowIdVO(getUserSession(request).getWorkflowUser().getWorkflowId()), ruleForm.getDocId()));
        }
        ruleForm.establishVisibleActionRequestCds();
        return null;
    }

    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Rule2Form ruleForm = (Rule2Form) form;

        if (IDocHandler.INITIATE_COMMAND.equalsIgnoreCase(ruleForm.getCommand())) {
            return start(mapping, form, request, response);
        } else {
             WorkflowDocument flexDoc = new WorkflowDocument(new WorkflowIdVO(getUserSession(request).getWorkflowUser().getWorkflowId()), ruleForm.getDocId());
            ruleForm.setFlexDoc(flexDoc);
            loadDocHandlerRules(flexDoc.getRouteHeaderId(), ruleForm);
            return mapping.findForward("docHandler");
        }
    }

    private void createFlexDoc(HttpServletRequest request, Rule2Form rule2Form, List rules) throws WorkflowException {
        if (rule2Form.getFlexDoc() == null) {
            try {
            	String ruleDocTypeName = getRuleService().getRuleDocmentTypeName(rules);
                rule2Form.setFlexDoc(new WorkflowDocument(new WorkflowIdVO(getUserSession(request).getWorkflowUser().getWorkflowId()), ruleDocTypeName));
            } catch (Exception e) {
                throw new WorkflowException(e);
            }

            rule2Form.setDocId(rule2Form.getFlexDoc().getRouteHeaderId());
            rule2Form.establishVisibleActionRequestCds();
        }
    }

    private void loadDocHandlerRules(Long routeHeaderId, Rule2Form ruleForm) throws Exception {
        List rules = getRuleService().findByRouteHeaderId(routeHeaderId);
        rules = filterDocHandlerRules(rules);
        List webRules = new ArrayList();
        for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) iterator.next();
            WebRuleBaseValues webRule = new WebRuleBaseValues(rule);
            if (webRule.getPreviousVersionId() != null) {
                webRule.setPreviousVersion(new WebRuleBaseValues(webRule.getPreviousVersion()));
                ((WebRuleBaseValues) webRule.getPreviousVersion()).establishRequiredState();
            }
            if (webRule.getCurrentInd().booleanValue()) {
                webRule.setPreviousVersion(null);
                webRule.setPreviousVersionId(null);
            }
            webRules.add(webRule);
            initializeShowHide(ruleForm.getShowHide(), webRule);
            if (webRule.getPreviousVersion() != null) {
                initializeShowHide(ruleForm.getParentShowHide(), webRule.getPreviousVersion());
            }
        }
        ruleForm.setRules(webRules);

        //      set up show/hide of delegation rules
        int ruleIndex = 0;
        for (Iterator ruleIt = webRules.iterator(); ruleIt.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) ruleIt.next();
            int respIndex = 0;
            for (Iterator respIt = rule.getResponsibilities().iterator(); respIt.hasNext();) {
                RuleResponsibility responsibility = (RuleResponsibility) respIt.next();
                String key = "currrule" + ruleIndex + "resp" + respIndex;
                if (ruleForm.getShowDelegationsMap().get(key) == null) {
                    int numDelegations = responsibility.getDelegationRules().size();
                    ruleForm.getShowDelegationsMap().put(key, new Boolean(numDelegations <= Integer.parseInt(Utilities.getApplicationConstant("Config.Application.DelegateLimit"))).toString());
                }
                respIndex++;
            }
            ruleIndex++;
            if (rule.getPreviousVersion() != null) {
                int prevRespIndex = 0;
                for (Iterator prevRespIt = rule.getPreviousVersion().getResponsibilities().iterator(); prevRespIt.hasNext();) {
                    RuleResponsibility responsibility = (RuleResponsibility) prevRespIt.next();
                    String key = "prevrule" + ruleIndex + "resp" + prevRespIndex;
                    if (ruleForm.getShowDelegationsMap().get(key) == null) {
                        int numDelegations = responsibility.getDelegationRules().size();
                        ruleForm.getShowDelegationsMap().put(key, new Boolean(numDelegations <= Integer.parseInt(Utilities.getApplicationConstant("Config.Application.DelegateLimit"))).toString());
                    }
                    prevRespIndex++;
                }
            }
        }
    }

    /**
     * This method looks for cases where we routed a delegate rule and filters the parent rule out of the list. This is done so that it doesn't show up in the doc handler list. From the user's perspective, they are really only interested in the delegate rule.
     */
    private List filterDocHandlerRules(List rules) {
        Map ruleMap = new HashMap();
        for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) iterator.next();
            ruleMap.put(rule.getRuleBaseValuesId(), rule);
        }
        for (Iterator iterator = rules.iterator(); iterator.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) iterator.next();
            boolean foundDelegate = false;
            resp: for (Iterator respIt = rule.getResponsibilities().iterator(); respIt.hasNext();) {
                RuleResponsibility responsibility = (RuleResponsibility) respIt.next();
                for (Iterator delIt = responsibility.getDelegationRules().iterator(); delIt.hasNext();) {
                    RuleDelegation delegation = (RuleDelegation) delIt.next();
                    if (ruleMap.containsKey(delegation.getDelegateRuleId())) {
                        foundDelegate = true;
                        break resp;
                    }
                }
            }
            if (foundDelegate) {
                ruleMap.remove(rule.getRuleBaseValuesId());
            }
        }
        return new ArrayList(ruleMap.values());
    }

    private ShowHideTree initializeShowHide(MyRules2 myRules) {
        ShowHideTree showHide = new ShowHideTree();
        for (Iterator ruleIt = myRules.getRules().iterator(); ruleIt.hasNext();) {
            RuleBaseValues rule = (RuleBaseValues) ruleIt.next();
            initializeShowHide(showHide, rule);
        }
        return showHide;
    }

    private ShowHideTree initializeShowHide(ShowHideTree showHide, RuleBaseValues rule) {
        ShowHideTree ruleTree = showHide.append();
        for (Iterator respIt = rule.getResponsibilities().iterator(); respIt.hasNext();) {
            WebRuleResponsibility responsibility = (WebRuleResponsibility) respIt.next();
            ShowHideTree respTree = ruleTree.append();
            if (responsibility.isShowDelegations()) {
                for (Iterator delIt = responsibility.getDelegationRules().iterator(); delIt.hasNext();) {
                    RuleDelegation delegation = (RuleDelegation) delIt.next();
                    ShowHideTree delTree = respTree.append();
                    for (Iterator delRespIt = delegation.getDelegationRuleBaseValues().getResponsibilities().iterator(); delRespIt.hasNext();) {
                        RuleResponsibility delResponsibility = (RuleResponsibility) delRespIt.next();
                        delTree.append();
                    }
                }
            }
        }
        return ruleTree;
    }

    private boolean checkLockedForRouting(ActionErrors errors, RuleBaseValues rule, boolean usePreviousId) {
        if ("true".equalsIgnoreCase(Utilities.getApplicationConstant(EdenConstants.RULE_LOCKING_ON))) {
            Long id = (usePreviousId ? rule.getPreviousVersionId() : rule.getRuleBaseValuesId());
            if (id != null) {
                Long routeHeaderId = getRuleService().isLockedForRouting(id);
                if (routeHeaderId != null) {
                    errors.add("hasErrors", new ActionMessage("routetemplate.RuleService.ruleInRoute", "" + routeHeaderId.longValue()));
                    return true;
                }
            }
        }
        return false;
    }

    private RuleService getRuleService() {
        return (RuleService) KEWServiceLocator.getService(KEWServiceLocator.RULE_SERVICE);
    }

    private RuleTemplateService getRuleTemplateService() {
        return (RuleTemplateService) KEWServiceLocator.getService(KEWServiceLocator.RULE_TEMPLATE_SERVICE);
    }

    private RuleDelegationService getRuleDelegationService() {
        return (RuleDelegationService) KEWServiceLocator.getService(KEWServiceLocator.RULE_DELEGATION_SERVICE);
    }

}