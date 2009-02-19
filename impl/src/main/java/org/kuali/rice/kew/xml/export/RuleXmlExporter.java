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
package org.kuali.rice.kew.xml.export;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.Namespace;
import org.kuali.rice.kew.export.ExportDataSet;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleExtension;
import org.kuali.rice.kew.rule.RuleExtensionValue;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttribute;
import org.kuali.rice.kew.xml.XmlConstants;
import org.kuali.rice.kew.xml.XmlRenderer;
import org.kuali.rice.kim.bo.group.KimGroup;


/**
 * Exports rules to XML.
 *
 * @see RuleBaseValues
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleXmlExporter implements XmlExporter, XmlConstants {

    protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());

    private XmlRenderer renderer;

    public RuleXmlExporter(Namespace namespace) {
    	this.renderer = new XmlRenderer(namespace);
    }
    
    public Element export(ExportDataSet dataSet) {
        if (!dataSet.getRules().isEmpty()) {
            Element rootElement = renderer.renderElement(null, RULES);
            rootElement.setAttribute(SCHEMA_LOCATION_ATTR, RULE_SCHEMA_LOCATION, SCHEMA_NAMESPACE);
            for (Iterator iterator = dataSet.getRules().iterator(); iterator.hasNext();) {
                RuleBaseValues rule = (RuleBaseValues) iterator.next();
                if (!rule.getDelegateRule().booleanValue()) {
                    exportRule(rootElement, rule);
                } else {
                    LOG.info("Not exporting a top-level delegate rule that was in the result set: " + rule.getRuleBaseValuesId());
                }
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
        renderer.renderBooleanElement(ruleElement, IGNORE_PREVIOUS, rule.getIgnorePrevious(), false);
        exportRuleExtensions(ruleElement, rule.getRuleExtensions());
        exportResponsibilities(ruleElement, rule.getResponsibilities());

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

    private void exportResponsibilities(Element parent, List responsibilities) {
        if (!responsibilities.isEmpty()) {
            Element responsibilitiesElement = renderer.renderElement(parent, RESPONSIBILITIES);
            for (Iterator iterator = responsibilities.iterator(); iterator.hasNext();) {
                RuleResponsibility ruleResponsibility = (RuleResponsibility) iterator.next();
                Element respElement = renderer.renderElement(responsibilitiesElement, RESPONSIBILITY);
                renderer.renderTextElement(respElement, RESPONSIBILITY_ID, "" + ruleResponsibility.getResponsibilityId());
                if (ruleResponsibility.isUsingWorkflowUser()) {
				    renderer.renderTextElement(respElement, PRINCIPAL_NAME, ruleResponsibility.getPrincipal().getPrincipalName());
				} else if (ruleResponsibility.isUsingGroup()) {
					KimGroup group = ruleResponsibility.getGroup();
				    Element groupElement = renderer.renderTextElement(respElement, GROUP_NAME, group.getGroupName());
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

}
