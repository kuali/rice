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

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleDelegation;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.routetemplate.RuleTemplateAttribute;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.util.XmlHelper;

/**
 * Parses {@link RuleTemplate}s from XML.
 *
 * @see RuleTemplate
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleTemplateXmlParser implements XmlConstants {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RuleTemplateXmlParser.class);

    /**
     * By default make attributes defined without a &lt;required&gt; element
     */
    private static final boolean DEFAULT_ATTRIBUTE_REQUIRED = true;
    private static final boolean DEFAULT_ATTRIBUTE_ACTIVE = true;

    private int templateAttributeCounter = 0;

    public List parseRuleTemplates(InputStream input) throws IOException, InvalidXmlException {

	try {
	    Document doc = XmlHelper.trimSAXXml(input);
	    Element root = doc.getRootElement();
	    return parseRuleTemplates(root);
	} catch (JDOMException e) {
	    throw new InvalidXmlException("Parse error.", e);
	} catch (SAXException e) {
	    throw new InvalidXmlException("Parse error.", e);
	} catch (ParserConfigurationException e) {
	    throw new InvalidXmlException("Parse error.", e);
	}
    }

    public List parseRuleTemplates(Element element) throws InvalidXmlException {
	List ruleTemplates = new ArrayList();

	// iterate over any RULE_TEMPLATES elements
	Vector ruleTemplatesElements = XmlHelper.findElements(element, RULE_TEMPLATES);
	Iterator ruleTemplatesIterator = ruleTemplatesElements.iterator();
	while (ruleTemplatesIterator.hasNext()) {
	    Element ruleTemplatesElement = (Element) ruleTemplatesIterator.next();
	    Vector ruleTemplateElements = XmlHelper.findElements(ruleTemplatesElement, RULE_TEMPLATE);
	    for (Iterator iterator = ruleTemplateElements.iterator(); iterator.hasNext();) {
		ruleTemplates.add(parseRuleTemplate((Element) iterator.next(), ruleTemplates));
	    }
	}
	return ruleTemplates;
    }

    private RuleTemplate parseRuleTemplate(Element element, List ruleTemplates) throws InvalidXmlException {
	String name = element.getChildText(NAME, RULE_TEMPLATE_NAMESPACE);
	String description = element.getChildText(DESCRIPTION, RULE_TEMPLATE_NAMESPACE);
	Attribute allowOverwriteAttrib = element.getAttribute("allowOverwrite");
	boolean allowOverwrite = false;
	if (allowOverwriteAttrib != null) {
	    allowOverwrite = Boolean.valueOf(allowOverwriteAttrib.getValue()).booleanValue();
	}
	if (Utilities.isEmpty(name)) {
	    throw new InvalidXmlException("RuleTemplate must have a name");
	}
	if (Utilities.isEmpty(description)) {
	    throw new InvalidXmlException("RuleTemplate must have a description");
	}
	RuleTemplate ruleTemplate = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(name);

	if (ruleTemplate == null) {
	    ruleTemplate = new RuleTemplate();
	} else if (!allowOverwrite) {
	    throw new RuntimeException("Attempting to overwrite template " + name + " without allowOverwrite set");
	} else {
	    ruleTemplate.initializeOptions();
	}

	ruleTemplate.setName(name);
	ruleTemplate.setDescription(description);
	String delegateTemplateName = element.getChildText(DELEGATION_TEMPLATE, RULE_TEMPLATE_NAMESPACE);

	if (delegateTemplateName != null) {
	    RuleTemplate delegateTemplate = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(delegateTemplateName);
	    if (delegateTemplate != null) {
		ruleTemplate.setDelegationTemplateId(delegateTemplate.getRuleTemplateId());
		ruleTemplate.setDelegationTemplate(delegateTemplate);
	    } else {
		boolean delegationTemplateFound = false;
		for (Iterator ruleTemplateIter = ruleTemplates.iterator(); ruleTemplateIter.hasNext();) {
		    RuleTemplate rt = (RuleTemplate) ruleTemplateIter.next();
		    if (delegateTemplateName.equalsIgnoreCase(rt.getName())) {
			Long ruleTemplateId = KEWServiceLocator.getRuleTemplateService().getNextRuleTemplateId();
			rt.setRuleTemplateId(ruleTemplateId);
			ruleTemplate.setDelegationTemplateId(ruleTemplateId);
			ruleTemplate.setDelegationTemplate(rt);
			delegationTemplateFound = true;
			break;
		    }
		}
		if (!delegationTemplateFound) {
		    throw new InvalidXmlException("Cannot find delegation template " + delegateTemplateName);
		}
	    }
	}

	if (!ruleTemplate.getRuleTemplateAttributes().isEmpty()) {
	    // inactivate all current attributes
	    for (Iterator iterator = ruleTemplate.getRuleTemplateAttributes().iterator(); iterator.hasNext();) {
                RuleTemplateAttribute currentRuleTemplateAttribute = (RuleTemplateAttribute) iterator.next();
                String ruleAttributeName = (currentRuleTemplateAttribute.getRuleAttribute() != null) ? currentRuleTemplateAttribute.getRuleAttribute().getName() : "(null)";
                LOG.debug("Inactivating rule template attribute with id " + currentRuleTemplateAttribute.getRuleTemplateAttributeId() + " and rule attribute with name " + ruleAttributeName);
                currentRuleTemplateAttribute.setActive(Boolean.FALSE);
	    }
	}

	Element attributesElement = element.getChild(ATTRIBUTES, RULE_TEMPLATE_NAMESPACE);
	if (attributesElement != null) {
	    //remove all the associated attributes if we're in overwrite mode.
	    List attributes = parseRuleTemplateAttributes(attributesElement, ruleTemplate);
	    for (Iterator iter = attributes.iterator(); iter.hasNext();) {
		RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) iter.next();
                RuleTemplateAttribute potentialExistingTemplateAttribute = ruleTemplate.getRuleTemplateAttribute(ruleTemplateAttribute);
                if (potentialExistingTemplateAttribute != null) {
                    // template attribute exists on rule template already... check it's activation level
                    potentialExistingTemplateAttribute.setActive(ruleTemplateAttribute.getActive());
                }
                else {
                    // template attribute does not yet exist on template so add it
                    ruleTemplate.getRuleTemplateAttributes().add(ruleTemplateAttribute);
                }
	    }
	}

	KEWServiceLocator.getRuleTemplateService().save(ruleTemplate);

	Element defaultsElement = element.getChild(RULE_DEFAULTS, RULE_TEMPLATE_NAMESPACE);
	if (defaultsElement != null) {
	    parseDefaults(defaultsElement, ruleTemplate);
	    // save again so that the rule template options get persisted
	    KEWServiceLocator.getRuleTemplateService().save(ruleTemplate);
	}
	return ruleTemplate;
    }

    private List parseRuleTemplateAttributes(Element element, RuleTemplate ruleTemplate) throws InvalidXmlException {
	List ruleTemplateAttributes = new ArrayList();
	Vector attributeElements = XmlHelper.findElements(element, ATTRIBUTE);
	for (Iterator iterator = attributeElements.iterator(); iterator.hasNext();) {
	    ruleTemplateAttributes.add(parseRuleTemplateAttribute((Element) iterator.next(), ruleTemplate));
	}
	return ruleTemplateAttributes;
    }

    private RuleTemplateAttribute parseRuleTemplateAttribute(Element element, RuleTemplate ruleTemplate) throws InvalidXmlException {
	String attributeName = element.getChildText(NAME, RULE_TEMPLATE_NAMESPACE);
	String requiredValue = element.getChildText(REQUIRED, RULE_TEMPLATE_NAMESPACE);
        String activeValue = element.getChildText(ACTIVE, RULE_TEMPLATE_NAMESPACE);
	if (Utilities.isEmpty(attributeName)) {
	    throw new InvalidXmlException("Attribute name must be non-empty");
	}
	boolean required = DEFAULT_ATTRIBUTE_REQUIRED;
	if (requiredValue != null) {
	    required = Boolean.parseBoolean(requiredValue);
	}
	boolean active = DEFAULT_ATTRIBUTE_ACTIVE;
	if (activeValue != null) {
	    active = Boolean.parseBoolean(activeValue);
	}
	RuleAttribute ruleAttribute = KEWServiceLocator.getRuleAttributeService().findByName(attributeName);
	if (ruleAttribute == null) {
	    throw new InvalidXmlException("Could not locate rule attribute for name '" + attributeName + "'");
	}
	RuleTemplateAttribute templateAttribute = new RuleTemplateAttribute();
	templateAttribute.setRuleAttribute(ruleAttribute);
	templateAttribute.setRuleAttributeId(ruleAttribute.getRuleAttributeId());
	templateAttribute.setRuleTemplate(ruleTemplate);
	templateAttribute.setRequired(Boolean.valueOf(required));
	templateAttribute.setActive(Boolean.valueOf(active));
	templateAttribute.setDisplayOrder(new Integer(templateAttributeCounter++));
	return templateAttribute;
    }

    /*
    <element name="ruleInstructions" type="c:LongStringType"/>
        <element name="description" type="c:LongStringType"/>
    <element name="fromDate" type="c:ShortStringType" minOccurs="0"/>
        <element name="toDate" type="c:ShortStringType" minOccurs="0"/>
    <element name="ignorePrevious" type="boolean"/>
    <element name="active" type="boolean"/>
        <element name="defaultActionRequested" type="c:ShortStringType"/>
        <element name="supportsComplete" type="boolean" default="true"/>
        <element name="supportsApprove" type="boolean" default="true"/>
        <element name="supportsAcknowledge" type="boolean" default="true"/>
        <element name="supportsFYI" type="boolean" default="true"/>
     */
    /**
     * Parses the defaults for this RuleTemplate.
     */
    private void parseDefaults(Element defaultsElement, RuleTemplate ruleTemplate) throws InvalidXmlException {
	// delete any existing defaults, we're going to replace them
	if (ruleTemplate.getRuleTemplateId() != null) {
	    RuleBaseValues ruleDefaults = KEWServiceLocator.getRuleService().findDefaultRuleByRuleTemplateId(ruleTemplate.getRuleTemplateId());
	    if (ruleDefaults != null) {
		List ruleDelegationDefaults = KEWServiceLocator.getRuleDelegationService().findByDelegateRuleId(ruleDefaults.getRuleBaseValuesId());
		KEWServiceLocator.getRuleService().delete(ruleDefaults.getRuleBaseValuesId());
		for (Iterator iterator = ruleDelegationDefaults.iterator(); iterator.hasNext();) {
		    RuleDelegation ruleDelegation = (RuleDelegation) iterator.next();
		    KEWServiceLocator.getRuleDelegationService().delete(ruleDelegation.getRuleDelegationId());
		}
	    }
	    if (ruleTemplate.getAcknowledge().getRuleTemplateOptionId() != null) {
		KEWServiceLocator.getRuleTemplateService().deleteRuleTemplateOption(ruleTemplate.getAcknowledge().getRuleTemplateOptionId());
	    }
	    if (ruleTemplate.getApprove().getRuleTemplateOptionId() != null) {
		KEWServiceLocator.getRuleTemplateService().deleteRuleTemplateOption(ruleTemplate.getApprove().getRuleTemplateOptionId());
	    }
	    if (ruleTemplate.getComplete().getRuleTemplateOptionId() != null) {
		KEWServiceLocator.getRuleTemplateService().deleteRuleTemplateOption(ruleTemplate.getComplete().getRuleTemplateOptionId());
	    }
	    if (ruleTemplate.getFyi().getRuleTemplateOptionId() != null) {
		KEWServiceLocator.getRuleTemplateService().deleteRuleTemplateOption(ruleTemplate.getFyi().getRuleTemplateOptionId());
	    }
	    if (ruleTemplate.getDefaultActionRequestValue().getRuleTemplateOptionId() != null) {
		KEWServiceLocator.getRuleTemplateService().deleteRuleTemplateOption(ruleTemplate.getDefaultActionRequestValue().getRuleTemplateOptionId());
	    }

	}
	// now create the defaults
	String delegationType = defaultsElement.getChildText(DELEGATION_TYPE, RULE_TEMPLATE_NAMESPACE);
	boolean isDelegation = !Utilities.isEmpty(delegationType);
	if (isDelegation && !EdenConstants.DELEGATION_PRIMARY.equals(delegationType) && !EdenConstants.DELEGATION_SECONDARY.equals(delegationType)) {
	    throw new InvalidXmlException("Invalid delegation type '" + delegationType + "'." + "  Expected one of: "
		    + EdenConstants.DELEGATION_PRIMARY + "," + EdenConstants.DELEGATION_SECONDARY);
	}
	RuleBaseValues ruleDefaults = new RuleBaseValues();

	// set up the default values
	ruleDefaults.setRuleTemplate(ruleTemplate);
	ruleDefaults.setDocTypeName("dummyDocumentType");
	ruleDefaults.setTemplateRuleInd(Boolean.TRUE);
	ruleDefaults.setCurrentInd(Boolean.TRUE);
	ruleDefaults.setVersionNbr(new Integer(0));
	try {
	    ruleDefaults.setDeactivationDate(new Timestamp(EdenConstants.getDefaultDateFormat().parse("01/01/2100").getTime()));
	    if (ruleDefaults.getActivationDate() == null) {
		ruleDefaults.setActivationDate(new Timestamp(System.currentTimeMillis()));
	    }
	} catch (ParseException e) {
	}

	String ruleInstructions = defaultsElement.getChildText(RULE_INSTRUCTIONS, RULE_TEMPLATE_NAMESPACE);
	String description = defaultsElement.getChildText(DESCRIPTION, RULE_TEMPLATE_NAMESPACE);
	String fromDate = defaultsElement.getChildText(FROM_DATE, RULE_TEMPLATE_NAMESPACE);
	String toDate = defaultsElement.getChildText(TO_DATE, RULE_TEMPLATE_NAMESPACE);
	Boolean ignorePrevious = Boolean.valueOf(defaultsElement.getChildText(IGNORE_PREVIOUS, RULE_TEMPLATE_NAMESPACE));
	Boolean active = Boolean.valueOf(defaultsElement.getChildText(ACTIVE, RULE_TEMPLATE_NAMESPACE));
	String defaultActionRequested = defaultsElement.getChildText(DEFAULT_ACTION_REQUESTED, RULE_TEMPLATE_NAMESPACE);
	Boolean supportsComplete = Boolean.valueOf(defaultsElement.getChildText(SUPPORTS_COMPLETE, RULE_TEMPLATE_NAMESPACE));
	Boolean supportsApprove = Boolean.valueOf(defaultsElement.getChildText(SUPPORTS_APPROVE, RULE_TEMPLATE_NAMESPACE));
	Boolean supportsAcknowledge = Boolean.valueOf(defaultsElement.getChildText(SUPPORTS_ACKNOWLEDGE, RULE_TEMPLATE_NAMESPACE));
	Boolean supportsFYI = Boolean.valueOf(defaultsElement.getChildText(SUPPORTS_FYI, RULE_TEMPLATE_NAMESPACE));

	RuleDelegation ruleDelegationDefaults = null;
	if (isDelegation) {
	    ruleDelegationDefaults = new RuleDelegation();
	    ruleDelegationDefaults.setDelegationRuleBaseValues(ruleDefaults);
	    ruleDelegationDefaults.setDelegationType(delegationType);
	    ruleDelegationDefaults.setRuleResponsibilityId(new Long(-1));
	} else {
	    ruleTemplate.getDefaultActionRequestValue().setValue(defaultActionRequested);
	    ruleTemplate.getComplete().setValue(supportsComplete.toString());
	    ruleTemplate.getApprove().setValue(supportsApprove.toString());
	    ruleTemplate.getAcknowledge().setValue(supportsAcknowledge.toString());
	    ruleTemplate.getFyi().setValue(supportsFYI.toString());
	}
	ruleTemplate.getInstructions().setValue(ruleInstructions);
	ruleDefaults.setDescription(description);
	if (Utilities.isEmpty(fromDate)) {
	    ruleDefaults.setFromDate(new Timestamp(System.currentTimeMillis()));
	} else {
	    ruleDefaults.setFromDateString(fromDate);
	}
	if (Utilities.isEmpty(toDate)) {
	    try {
		ruleDefaults.setToDate(new Timestamp(EdenConstants.getDefaultDateFormat().parse("01/01/2100").getTime()));
	    } catch (ParseException e) {}
	} else {
	    ruleDefaults.setToDateString(toDate);
	}
	ruleDefaults.setIgnorePrevious(ignorePrevious);
	ruleDefaults.setActiveInd(active);

	KEWServiceLocator.getRuleTemplateService().save(ruleDelegationDefaults, ruleDefaults);
    }

}
