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
package edu.iu.uis.eden.xml.export;

import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.WorkflowRuntimeException;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleDelegation;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;
import edu.iu.uis.eden.routetemplate.RuleResponsibility;
import edu.iu.uis.eden.routetemplate.RuleTemplateAttribute;
import edu.iu.uis.eden.xml.XmlConstants;

/**
 * Exports rules to XML.
 * 
 * @see RuleBaseValues
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleXmlExporter implements XmlExporter, XmlConstants {

    protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());
    
    private ExportRenderer renderer = new ExportRenderer(RULE_NAMESPACE);
    
    public Element export(ExportDataSet dataSet) {
        if (!dataSet.getRules().isEmpty()) {
            Element rootElement = renderer.renderElement(null, RULES);
            rootElement.setAttribute(SCHEMA_LOCATION_ATTR, RULE_SCHEMA_LOCATION, SCHEMA_NAMESPACE);
            for (Iterator iterator = dataSet.getRules().iterator(); iterator.hasNext();) {
                RuleBaseValues rule = (RuleBaseValues) iterator.next();
                if (!rule.getDelegateRule().booleanValue()) {
                    exportRule(rootElement, rule, null);
                } else {
                    LOG.info("Not exporting a top-level delegate rule that was in the result set: " + rule.getRuleBaseValuesId());
                }
            }
            return rootElement;
        }
        return null;
    }
    
    private void exportRule(Element parent, RuleBaseValues rule, RuleDelegation delegation) {
        Element ruleElement = renderer.renderElement(parent, RULE);
        renderer.renderTextElement(ruleElement, DOCUMENT_TYPE, rule.getDocTypeName());
        renderer.renderTextElement(ruleElement, RULE_TEMPLATE, rule.getRuleTemplateName());
        renderer.renderTextElement(ruleElement, DESCRIPTION, rule.getDescription());
        renderer.renderBooleanElement(ruleElement, IGNORE_PREVIOUS, rule.getIgnorePrevious(), false);
        exportRuleExtensions(ruleElement, rule.getRuleExtensions());
        exportResponsibilities(ruleElement, rule.getResponsibilities(), delegation);
        if (delegation != null) {
            renderer.renderTextElement(ruleElement, DELEGATION_TYPE, delegation.getDelegationType());
        }
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
    
    private void exportResponsibilities(Element parent, List responsibilities, RuleDelegation delegation) {
        if (!responsibilities.isEmpty()) {
            Element responsibilitiesElement = renderer.renderElement(parent, RESPONSIBILITIES);
            for (Iterator iterator = responsibilities.iterator(); iterator.hasNext();) {
                RuleResponsibility ruleResponsibility = (RuleResponsibility) iterator.next();
                Element respElement = renderer.renderElement(responsibilitiesElement, RESPONSIBILITY);
                try {
                    if (ruleResponsibility.isUsingWorkflowUser()) {
                        renderer.renderTextElement(respElement, USER, ruleResponsibility.getWorkflowUser().getAuthenticationUserId().getId());
                    } else if (ruleResponsibility.isUsingWorkgroup()) {
                        renderer.renderTextElement(respElement, WORKGROUP, ruleResponsibility.getWorkgroup().getGroupNameId().getNameId());
                    } else if (ruleResponsibility.isUsingRole()) {
                        renderer.renderTextElement(respElement, ROLE, ruleResponsibility.getRuleResponsibilityName());
                        renderer.renderTextElement(respElement, APPROVE_POLICY, ruleResponsibility.getApprovePolicy());
                    }
                } catch (EdenUserNotFoundException e) {
                    throw new WorkflowRuntimeException("Could not locate user when attempting to export responsibility.");
                }
                if (delegation == null) {
                    renderer.renderTextElement(respElement, ACTION_REQUESTED, ruleResponsibility.getActionRequestedCd());
                    renderer.renderTextElement(respElement, PRIORITY, ruleResponsibility.getPriority().toString());
                    exportDelegations(respElement, ruleResponsibility.getDelegationRules());
                }
            }
        }
    }
    
    private void exportDelegations(Element parent, List ruleDelegations) {
        if (!ruleDelegations.isEmpty()) {
            Element delegationsElement = renderer.renderElement(parent, DELEGATIONS);
            for (Iterator iterator = ruleDelegations.iterator(); iterator.hasNext();) {
                RuleDelegation delegation = (RuleDelegation) iterator.next();
                exportRule(delegationsElement, delegation.getDelegationRuleBaseValues(), delegation);
            }
        }
    }

}
