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

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleDelegation;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.routetemplate.RuleTemplateAttribute;
import edu.iu.uis.eden.routetemplate.RuleTemplateOption;
import edu.iu.uis.eden.xml.XmlConstants;

/**
 * Exports {@link RuleTemplate}s to XML.
 * 
 * @see RuleTemplate
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleTemplateXmlExporter implements XmlExporter, XmlConstants {

    protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());
    
    private ExportRenderer renderer = new ExportRenderer(RULE_TEMPLATE_NAMESPACE);
    
    public Element export(ExportDataSet dataSet) {
        if (!dataSet.getRuleTemplates().isEmpty()) {
            Element rootElement = renderer.renderElement(null, RULE_TEMPLATES);
            rootElement.setAttribute(SCHEMA_LOCATION_ATTR, RULE_TEMPLATE_SCHEMA_LOCATION, SCHEMA_NAMESPACE);
            for (Iterator iterator = dataSet.getRuleTemplates().iterator(); iterator.hasNext();) {
                RuleTemplate template = (RuleTemplate)iterator.next();
                exportRuleTemplate(rootElement, template);
            }
            return rootElement;
        }
        return null;
    }
    
    private void exportRuleTemplate(Element parent, RuleTemplate ruleTemplate) {
        Element templateElement = renderer.renderElement(parent, RULE_TEMPLATE);
        renderer.renderTextElement(templateElement, NAME, ruleTemplate.getName());
        renderer.renderTextElement(templateElement, DESCRIPTION, ruleTemplate.getDescription());
        if (ruleTemplate.getDelegationTemplate() != null) {
            renderer.renderTextElement(templateElement, DELEGATION_TEMPLATE, ruleTemplate.getDelegationTemplate().getName());
        }
        exportAttributes(templateElement, ruleTemplate.getActiveRuleTemplateAttributes());
        exportDefaults(templateElement, ruleTemplate);
    }

    private void exportAttributes(Element parent, List ruleTemplateAttributes) {
        if (!ruleTemplateAttributes.isEmpty()) {
            Element attributesElement = renderer.renderElement(parent, ATTRIBUTES);
            for (Iterator iterator = ruleTemplateAttributes.iterator(); iterator.hasNext();) {
                RuleTemplateAttribute attribute = (RuleTemplateAttribute) iterator.next();
                Element attributeElement = renderer.renderElement(attributesElement, ATTRIBUTE);
                renderer.renderTextElement(attributeElement, NAME, attribute.getRuleAttribute().getName());
                renderer.renderBooleanElement(attributeElement, REQUIRED, attribute.getRequired(), false);
            }
        }
    }
    
    private void exportDefaults(Element parent, RuleTemplate ruleTemplate) {
        RuleBaseValues defaultRuleValues = KEWServiceLocator.getRuleService().findDefaultRuleByRuleTemplateId(ruleTemplate.getRuleTemplateId());
        if (defaultRuleValues != null) {
            RuleDelegation defaultDelegationValues = getDefaultDelegationValues(defaultRuleValues);
            Element defaultsElement = renderer.renderElement(parent, RULE_DEFAULTS);
            if (defaultDelegationValues != null) {
                renderer.renderTextElement(defaultsElement, DELEGATION_TYPE, defaultDelegationValues.getDelegationType());
            }
            RuleTemplateOption instructionsOption = ruleTemplate.getInstructions();
            String instructionsValue = (instructionsOption == null ? "" : instructionsOption.getValue());
            renderer.renderTextElement(defaultsElement, RULE_INSTRUCTIONS, instructionsValue);
            renderer.renderTextElement(defaultsElement, DESCRIPTION, defaultRuleValues.getDescription());
            renderer.renderDateElement(defaultsElement, FROM_DATE, defaultRuleValues.getFromDate());
            renderer.renderDateElement(defaultsElement, TO_DATE, defaultRuleValues.getToDate());
            renderer.renderBooleanElement(defaultsElement, IGNORE_PREVIOUS, defaultRuleValues.getIgnorePrevious(), false);
            renderer.renderBooleanElement(defaultsElement, ACTIVE, defaultRuleValues.getActiveInd(), true);
            if (defaultDelegationValues == null) {
                RuleTemplateOption defaultActionOption = ruleTemplate.getDefaultActionRequestValue();
                RuleTemplateOption supportsComplete = ruleTemplate.getComplete();
                RuleTemplateOption supportsApprove = ruleTemplate.getApprove();
                RuleTemplateOption supportsAck = ruleTemplate.getAcknowledge();
                RuleTemplateOption supportsFYI = ruleTemplate.getFyi();
                String defaultActionValue = (defaultActionOption == null ? EdenConstants.ACTION_REQUEST_APPROVE_REQ : defaultActionOption.getValue());
                String supportsCompleteValue = (supportsComplete == null ? Boolean.TRUE.toString() : supportsComplete.getValue());
                String supportsApproveValue = (supportsApprove == null ? Boolean.TRUE.toString() : supportsApprove.getValue());
                String supportsAckValue = (supportsAck == null ? Boolean.TRUE.toString() : supportsAck.getValue());
                String supportsFYIValue = (supportsFYI == null ? Boolean.TRUE.toString() : supportsFYI.getValue());
                renderer.renderTextElement(defaultsElement, DEFAULT_ACTION_REQUESTED, defaultActionValue);
                renderer.renderTextElement(defaultsElement, SUPPORTS_COMPLETE, supportsCompleteValue);
                renderer.renderTextElement(defaultsElement, SUPPORTS_APPROVE, supportsApproveValue);
                renderer.renderTextElement(defaultsElement, SUPPORTS_ACKNOWLEDGE, supportsAckValue);
                renderer.renderTextElement(defaultsElement, SUPPORTS_FYI, supportsFYIValue);
            }
        }
    }
    
    private RuleDelegation getDefaultDelegationValues(RuleBaseValues defaultRuleValues) {
        List ruleDelegations = KEWServiceLocator.getRuleDelegationService().findByDelegateRuleId(defaultRuleValues.getRuleBaseValuesId());
        if (ruleDelegations.size() > 1) {
            LOG.warn("The rule defaults has more than one associated delegation defaults.");
        }
        return (ruleDelegations.isEmpty() ? null : (RuleDelegation)ruleDelegations.get(0));
    }
    
}
