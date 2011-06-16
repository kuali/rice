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
package org.kuali.rice.kew.xml.export;

import static org.kuali.rice.core.api.impex.xml.XmlConstants.ACTION_REQUESTED;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.APPROVE_POLICY;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.ATTRIBUTE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.DELEGATION_TYPE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.DESCRIPTION;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.DOCUMENT_TYPE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.EXPRESSION;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.FORCE_ACTION;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.FROM_DATE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.GROUP_NAME;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.KEY;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.NAME;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.NAMESPACE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.PARENT_RESPONSIBILITY;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.PARENT_RULE_NAME;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.PRINCIPAL_NAME;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.PRIORITY;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RESPONSIBILITIES;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RESPONSIBILITY;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RESPONSIBILITY_ID;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.ROLE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RULE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RULES;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RULE_DELEGATION;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RULE_EXTENSION;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RULE_EXTENSIONS;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RULE_EXTENSION_VALUE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RULE_EXTENSION_VALUES;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RULE_SCHEMA_LOCATION;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RULE_TEMPLATE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.SCHEMA_LOCATION_ATTR;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.SCHEMA_NAMESPACE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.TO_DATE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.VALUE;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.Namespace;
import org.kuali.rice.core.api.impex.ExportDataSet;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.core.framework.impex.xml.XmlExporter;
import org.kuali.rice.core.util.xml.XmlRenderer;
import org.kuali.rice.kew.export.KewExportDataSet;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kew.rule.RuleExtension;
import org.kuali.rice.kew.rule.RuleExtensionValue;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttribute;
import org.kuali.rice.kew.rule.web.WebRuleUtils;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.group.Group;

/**
 * Exports rules to XML.
 *
 * @see RuleBaseValues
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RuleXmlExporter implements XmlExporter {

    protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());

    private XmlRenderer renderer;
    
    public RuleXmlExporter(Namespace namespace) {
    	this.renderer = new XmlRenderer(namespace);
    }
    
	@Override
	public boolean supportPrettyPrint() {
		return true;
	}

    public Element export(ExportDataSet exportDataSet) {
    	KewExportDataSet dataSet = KewExportDataSet.fromExportDataSet(exportDataSet);
        if (!dataSet.getRules().isEmpty()) {
            Element rootElement = renderer.renderElement(null, RULES);
            rootElement.setAttribute(SCHEMA_LOCATION_ATTR, RULE_SCHEMA_LOCATION, SCHEMA_NAMESPACE);
            for (Iterator iterator = dataSet.getRules().iterator(); iterator.hasNext();) {
            	RuleBaseValues rule = (RuleBaseValues) iterator.next();
            	exportRule(rootElement, rule);
            	//turn below on if need export delegates in rule exportation
//            	if(rule.getDelegateRule().booleanValue()){	
//            		exportRuleDelegations(rootElement, rule);
//            	}
            }
            return rootElement;
        }
        return null;
    }

    public void exportRule(Element parent, RuleBaseValues rule) {
    	Element ruleElement = renderer.renderElement(parent, RULE);
        if (rule.getName() != null) {
            renderer.renderTextElement(ruleElement, NAME, rule.getName());
        }
        renderer.renderTextElement(ruleElement, DOCUMENT_TYPE, rule.getDocTypeName());
        if (rule.getRuleTemplateName() != null) {
            renderer.renderTextElement(ruleElement, RULE_TEMPLATE, rule.getRuleTemplateName());
        }
        renderer.renderTextElement(ruleElement, DESCRIPTION, rule.getDescription());
        if(rule.getFromDateString() != null){
            renderer.renderTextElement(ruleElement, FROM_DATE, rule.getFromDateString());
        }
        if(rule.getToDateString() != null){
            renderer.renderTextElement(ruleElement, TO_DATE, rule.getToDateString());
        }
        if (rule.getRuleExpressionDef() != null) {
            Element expressionElement = renderer.renderTextElement(ruleElement, EXPRESSION, rule.getRuleExpressionDef().getExpression());
            if (rule.getRuleExpressionDef().getType() != null) {
                expressionElement.setAttribute("type", rule.getRuleExpressionDef().getType());
            }
        }
        renderer.renderBooleanElement(ruleElement, FORCE_ACTION, rule.getForceAction(), false);
        
        if (CollectionUtils.isEmpty(rule.getRuleExtensions()) && 
        		/* field values is not empty */
        		!(rule.getFieldValues() == null || rule.getFieldValues().size() == 0)) {
        	// the rule is in the wrong state (as far as we are concerned).
        	// translate it
        	WebRuleUtils.translateResponsibilitiesForSave(rule);
        	WebRuleUtils.translateFieldValuesForSave(rule);
        	
        	// do our exports
    		exportRuleExtensions(ruleElement, rule.getRuleExtensions());
        	
        	// translate it back
        	WebRuleUtils.populateRuleMaintenanceFields(rule);
        } else { 
        	exportRuleExtensions(ruleElement, rule.getRuleExtensions());
        }
        
        // put responsibilities in a single collection 
        Set<RuleResponsibility> responsibilities = new HashSet<RuleResponsibility>();
        responsibilities.addAll(rule.getResponsibilities());
        responsibilities.addAll(rule.getPersonResponsibilities());
        responsibilities.addAll(rule.getGroupResponsibilities());
        responsibilities.addAll(rule.getRoleResponsibilities());
        
        exportResponsibilities(ruleElement, responsibilities);
    }

    private void exportRuleExtensions(Element parent, List ruleExtensions) {
        if (!ruleExtensions.isEmpty()) {
            Element extsElement = renderer.renderElement(parent, RULE_EXTENSIONS);
            for (Iterator iterator = ruleExtensions.iterator(); iterator.hasNext();) {
                RuleExtension extension = (RuleExtension) iterator.next();
                Element extElement = renderer.renderElement(extsElement, RULE_EXTENSION);
                RuleTemplateAttribute attribute = extension.getRuleTemplateAttribute();
                renderer.renderTextElement(extElement, ATTRIBUTE, attribute.getRuleAttribute().getName());
                renderer.renderTextElement(extElement, RULE_TEMPLATE, attribute.getRuleTemplate().getName());
                exportRuleExtensionValues(extElement, extension.getExtensionValues());
            }
        }
    }

    private void exportRuleExtensionValues(Element parent, List extensionValues) {
        if (!extensionValues.isEmpty()) {
            Element extValuesElement = renderer.renderElement(parent, RULE_EXTENSION_VALUES);
            for (Iterator iterator = extensionValues.iterator(); iterator.hasNext();) {
                RuleExtensionValue extensionValue = (RuleExtensionValue) iterator.next();
                Element extValueElement = renderer.renderElement(extValuesElement, RULE_EXTENSION_VALUE);
                renderer.renderTextElement(extValueElement, KEY, extensionValue.getKey());
                renderer.renderTextElement(extValueElement, VALUE, extensionValue.getValue());
            }
        }
    }

    private void exportResponsibilities(Element parent, Collection<? extends RuleResponsibility> responsibilities) {
        if (responsibilities != null && !responsibilities.isEmpty()) {
            Element responsibilitiesElement = renderer.renderElement(parent, RESPONSIBILITIES);
            for (RuleResponsibility ruleResponsibility : responsibilities) {
                Element respElement = renderer.renderElement(responsibilitiesElement, RESPONSIBILITY);
                renderer.renderTextElement(respElement, RESPONSIBILITY_ID, "" + ruleResponsibility.getResponsibilityId());
                if (ruleResponsibility.isUsingWorkflowUser()) {
				    renderer.renderTextElement(respElement, PRINCIPAL_NAME, ruleResponsibility.getPrincipal().getPrincipalName());
				} else if (ruleResponsibility.isUsingGroup()) {
					Group group = ruleResponsibility.getGroup();
				    Element groupElement = renderer.renderTextElement(respElement, GROUP_NAME, group.getName());
				    groupElement.setAttribute(NAMESPACE, group.getNamespaceCode());
				} else if (ruleResponsibility.isUsingRole()) {
				    renderer.renderTextElement(respElement, ROLE, ruleResponsibility.getRuleResponsibilityName());
				    renderer.renderTextElement(respElement, APPROVE_POLICY, ruleResponsibility.getApprovePolicy());
				}
                if (!StringUtils.isBlank(ruleResponsibility.getActionRequestedCd())) {
                	renderer.renderTextElement(respElement, ACTION_REQUESTED, ruleResponsibility.getActionRequestedCd());
                }
                if (ruleResponsibility.getPriority() != null) {
                	renderer.renderTextElement(respElement, PRIORITY, ruleResponsibility.getPriority().toString());
                }
            }
        }
    }
    
    //below are for exporting rule delegations in rule exportation
    private void exportRuleDelegations(Element rootElement, RuleBaseValues rule){
		List<RuleDelegation> ruleDelegationDefaults = KEWServiceLocator.getRuleDelegationService().findByDelegateRuleId(rule.getRuleBaseValuesId());
		for(RuleDelegation dele : ruleDelegationDefaults){
			if (LOG.isInfoEnabled()) {
				LOG.info("*******delegates********\t"  +  dele.getRuleDelegationId()) ;
			}
			exportRuleDelegation(rootElement, dele);	
		}
    }
    
    private void exportRuleDelegation(Element parent, RuleDelegation ruleDelegation) {
    	Element ruleDelegationElement = renderer.renderElement(parent, RULE_DELEGATION);
    	exportRuleDelegationParentResponsibility(ruleDelegationElement, ruleDelegation);
    	renderer.renderTextElement(ruleDelegationElement, DELEGATION_TYPE, ruleDelegation.getDelegationType());
    	exportRule(ruleDelegationElement, ruleDelegation.getDelegationRuleBaseValues());
    }
    
    private void exportRuleDelegationParentResponsibility(Element parent, RuleDelegation delegation) {
        Element parentResponsibilityElement = renderer.renderElement(parent, PARENT_RESPONSIBILITY);
        RuleResponsibility ruleResponsibility = KEWServiceLocator.getRuleService().findRuleResponsibility(delegation.getResponsibilityId());
        renderer.renderTextElement(parentResponsibilityElement, PARENT_RULE_NAME, ruleResponsibility.getRuleBaseValues().getName());
        if (ruleResponsibility.isUsingWorkflowUser()) {
        	Principal principal = ruleResponsibility.getPrincipal();
        	renderer.renderTextElement(parentResponsibilityElement, PRINCIPAL_NAME, principal.getPrincipalName());
        } else if (ruleResponsibility.isUsingGroup()) {
        	Group group = ruleResponsibility.getGroup();
        	Element groupElement = renderer.renderElement(parentResponsibilityElement, GROUP_NAME);
        	groupElement.setText(group.getName());
        	groupElement.setAttribute(NAMESPACE, group.getNamespaceCode());
        } else if (ruleResponsibility.isUsingRole()) {
        	renderer.renderTextElement(parentResponsibilityElement, ROLE, ruleResponsibility.getRuleResponsibilityName());
        } else {
        	throw new RiceRuntimeException("Encountered a rule responsibility when exporting with an invalid type of '" + ruleResponsibility.getRuleResponsibilityType());
        }
    }

}
