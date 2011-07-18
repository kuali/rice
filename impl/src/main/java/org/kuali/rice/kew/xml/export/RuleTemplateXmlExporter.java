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

import org.jdom.Element;
import org.kuali.rice.core.api.impex.ExportDataSet;
import org.kuali.rice.core.api.util.xml.XmlRenderer;
import org.kuali.rice.core.framework.impex.xml.XmlExporter;
import org.kuali.rice.kew.export.KewExportDataSet;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kew.rule.RuleTemplateOption;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;

import java.util.Iterator;
import java.util.List;

import static org.kuali.rice.core.api.impex.xml.XmlConstants.*;
/**
 * Exports {@link RuleTemplate}s to XML.
 * 
 * @see RuleTemplate
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RuleTemplateXmlExporter implements XmlExporter {

    protected final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(getClass());
    
    private XmlRenderer renderer = new XmlRenderer(RULE_TEMPLATE_NAMESPACE);
    
	@Override
	public boolean supportPrettyPrint() {
		return true;
	}

    public Element export(ExportDataSet exportDataSet) {
    	KewExportDataSet dataSet = KewExportDataSet.fromExportDataSet(exportDataSet);
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
            renderer.renderTextElement(defaultsElement, DESCRIPTION, defaultRuleValues.getDescription());
            if (defaultRuleValues.getFromDate() != null) {
            	renderer.renderDateElement(defaultsElement, FROM_DATE, defaultRuleValues.getFromDate());
            }
            if (defaultRuleValues.getToDate() != null) {
            	renderer.renderDateElement(defaultsElement, TO_DATE, defaultRuleValues.getToDate());
            }
            renderer.renderBooleanElement(defaultsElement, FORCE_ACTION, defaultRuleValues.getForceAction(), false);
            renderer.renderBooleanElement(defaultsElement, ACTIVE, defaultRuleValues.getActiveInd(), true);
            if (defaultDelegationValues == null) {
                RuleTemplateOption defaultActionOption = ruleTemplate.getDefaultActionRequestValue();
                RuleTemplateOption supportsComplete = ruleTemplate.getComplete();
                RuleTemplateOption supportsApprove = ruleTemplate.getApprove();
                RuleTemplateOption supportsAck = ruleTemplate.getAcknowledge();
                RuleTemplateOption supportsFYI = ruleTemplate.getFyi();
                if (defaultActionOption != null) {
                	String defaultActionValue = (defaultActionOption == null ? null : defaultActionOption.getValue());
                	renderer.renderTextElement(defaultsElement, DEFAULT_ACTION_REQUESTED, defaultActionValue);
                }
                if (supportsComplete != null) {
                	String supportsCompleteValue = supportsComplete.getValue();
                	renderer.renderTextElement(defaultsElement, SUPPORTS_COMPLETE, supportsCompleteValue);
                }
                if (supportsApprove != null) {
                	String supportsApproveValue = supportsApprove.getValue();
                	renderer.renderTextElement(defaultsElement, SUPPORTS_APPROVE, supportsApproveValue);
                }
                if (supportsAck != null) {
                	String supportsAckValue = supportsAck.getValue();
                	renderer.renderTextElement(defaultsElement, SUPPORTS_ACKNOWLEDGE, supportsAckValue);
                }
                if (supportsFYI != null) {
                	String supportsFYIValue = supportsFYI.getValue();
                	renderer.renderTextElement(defaultsElement, SUPPORTS_FYI, supportsFYIValue);
                }
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
