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
package edu.iu.uis.eden.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;
import org.jdom.Namespace;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleExtension;
import edu.iu.uis.eden.routetemplate.RuleExtensionValue;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.routetemplate.RuleTemplateAttribute;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.util.XmlHelper;

/**
 * Parses {@link RuleExtension}s from XML.
 *
 * @see RuleExtension
 * @see RuleExtensionValue
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleExtensionXmlParser {

    private static final Namespace NAMESPACE = Namespace.getNamespace("", "ns:workflow/Rule");
    private static final String RULE_EXTENSION = "ruleExtension";
    private static final String ATTRIBUTE = "attribute";
    private static final String RULE_TEMPLATE = "ruleTemplate";
    private static final String RULE_EXTENSION_VALUES = "ruleExtensionValues";
    private static final String RULE_EXTENSION_VALUE = "ruleExtensionValue";
    private static final String KEY = "key";
    private static final String VALUE = "value";

    public List parseRuleExtensions(Element element, RuleBaseValues rule) throws InvalidXmlException {
	List ruleExtensions = new ArrayList();
	Vector ruleElements = XmlHelper.findElements(element, RULE_EXTENSION);
	for (Iterator iterator = ruleElements.iterator(); iterator.hasNext();) {
	    ruleExtensions.add(parseRuleExtension((Element) iterator.next(), rule));
	}
	return ruleExtensions;
    }

    private RuleExtension parseRuleExtension(Element element, RuleBaseValues rule) throws InvalidXmlException {
	String attributeName = element.getChildText(ATTRIBUTE, NAMESPACE);
	String templateName = element.getChildText(RULE_TEMPLATE, NAMESPACE);
	Element valuesElement = element.getChild(RULE_EXTENSION_VALUES, NAMESPACE);
	if (attributeName == null) {
	    throw new InvalidXmlException("Rule extension must have a valid attribute.");
	}
	if (templateName == null) {
	    throw new InvalidXmlException("Rule extension must have a valid rule template.");
	}
	RuleAttribute ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByName(attributeName);
	if (ruleAttribute == null) {
	    throw new InvalidXmlException("Could not locate attribute for the given name '" + attributeName + "'");
	}
	RuleTemplate ruleTemplate = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(templateName);
	if (ruleTemplate == null) {
	    throw new InvalidXmlException("Could not locate rule template for the given name '" + templateName + "'");
	}
	RuleExtension extension = new RuleExtension();
	extension.setRuleBaseValues(rule);
	boolean attributeFound = false;
	for (Iterator iter = ruleTemplate.getActiveRuleTemplateAttributes().iterator(); iter.hasNext();) {
	    RuleTemplateAttribute templateAttribute = (RuleTemplateAttribute) iter.next();
	    if (templateAttribute.getRuleAttributeId().equals(ruleAttribute.getRuleAttributeId())) {
		extension.setRuleTemplateAttribute(templateAttribute);
		extension.setRuleTemplateAttributeId(templateAttribute.getRuleTemplateAttributeId());
		attributeFound = true;
		break;
	    }
	}

	if (!attributeFound) {
	    // TODO: need test case for this
	    throw new InvalidXmlException("Attribute '" + attributeName + "' not found on template '" + ruleTemplate.getName() + "'");
	}

	extension.setExtensionValues(parseRuleExtensionValues(valuesElement, extension));
	return extension;
    }

    private List parseRuleExtensionValues(Element element, RuleExtension ruleExtension) throws InvalidXmlException {
	List values = new ArrayList();
	if (element == null) {
	    return values;
	}
	List valueElements = XmlHelper.findElements(element, RULE_EXTENSION_VALUE);
	for (Iterator iterator = valueElements.iterator(); iterator.hasNext();) {
	    Element valueElement = (Element) iterator.next();
	    values.add(parseRuleExtensionValue(valueElement, ruleExtension));
	}
	return values;
    }

    private RuleExtensionValue parseRuleExtensionValue(Element element, RuleExtension ruleExtension) throws InvalidXmlException {
	String key = element.getChildText(KEY, NAMESPACE);
	String value = element.getChildText(VALUE, NAMESPACE);
	if (Utilities.isEmpty(key)) {
	    throw new InvalidXmlException("RuleExtensionValue must have a non-empty key.");
	}
	if (value == null) {
	    throw new InvalidXmlException("RuleExtensionValue must have a non-null value.");
	}
	RuleExtensionValue extensionValue = new RuleExtensionValue(key, value);
	extensionValue.setExtension(ruleExtension);
	return extensionValue;
    }

}
