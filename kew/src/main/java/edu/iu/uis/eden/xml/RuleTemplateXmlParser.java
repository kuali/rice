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

import org.apache.commons.lang.BooleanUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.kuali.notification.exception.InvalidXMLException;
import org.xml.sax.SAXException;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleDelegation;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.routetemplate.RuleTemplateAttribute;
import edu.iu.uis.eden.routetemplate.RuleTemplateOption;
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

    /**
     * Default date used for deactivation and 'to' of the default rule. "Far in the future"
     */
    private static final String FAR_IN_THE_FUTURE = "01/01/2100";
    /**
     * A dummy document type used in the default rule
     */
    private static final String DUMMY_DOCUMENT_TYPE = "dummyDocumentType";

    /**
     * Used to set the display order of attributes encountered in parsing runs during the lifetime of this object
     */
    private int templateAttributeCounter = 0;

    public List<RuleTemplate> parseRuleTemplates(InputStream input) throws IOException, InvalidXmlException {

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

    public List<RuleTemplate> parseRuleTemplates(Element element) throws InvalidXmlException {
        List<RuleTemplate> ruleTemplates = new ArrayList<RuleTemplate>();

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

    private RuleTemplate parseRuleTemplate(Element element, List<RuleTemplate> ruleTemplates) throws InvalidXmlException {
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

        // look up the rule template by name first
        RuleTemplate ruleTemplate = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(name);

        if (ruleTemplate == null) {
            // if it does not exist create a new one
            ruleTemplate = new RuleTemplate();
        } else {
            // if it does exist, update it, only if allowOverwrite is set
            if (!allowOverwrite) {
                throw new RuntimeException("Attempting to overwrite template " + name + " without allowOverwrite set");
            }

            // the name should be equal if one was actually found
            assert(name.equals(ruleTemplate.getName())) : "Existing template definition name does not match incoming definition name";
        } 

        // overwrite simple properties
        ruleTemplate.setName(name);
        ruleTemplate.setDescription(description);

        // update the delegation template
        updateDelegationTemplate(element, ruleTemplate, ruleTemplates);

        // update the attribute relationships
        updateRuleTemplateAttributes(element, ruleTemplate);

        // save the rule template first so that the default/template rule that is generated
        // in the process of setting defaults is associated properly
        KEWServiceLocator.getRuleTemplateService().save(ruleTemplate);

        // update the default options
        updateRuleTemplateDefaultOptions(element, ruleTemplate);

        KEWServiceLocator.getRuleTemplateService().save(ruleTemplate);

        return ruleTemplate;
    }

    /**
     * Updates the rule template default options.  Updates any existing options, removes any omitted ones.
     * @param ruleTemplateElement the rule template XML element
     * @param updatedRuleTemplate the RuleTemplate being updated
     * @throws InvalidXMLException
     */
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
    protected void updateRuleTemplateDefaultOptions(Element ruleTemplateElement, RuleTemplate updatedRuleTemplate) throws InvalidXmlException {
        Element defaultsElement = ruleTemplateElement.getChild(RULE_DEFAULTS, RULE_TEMPLATE_NAMESPACE);

        // the possible defaults options
        // NOTE: the current implementation will remove any existing RuleTemplateOption records for any values which are null,
        // i.e. not set in the incoming XML.
        // to pro-actively set default values for omitted options, simply set those values here, and records will be added
        // if not present
        String delegationType = null;
        // note, rule instructions is mandatory in rule template defaults XSD
        String ruleInstructions = null;

        // note, description is mandatory in rule template defaults XSD
        String description = null;
        String fromDate = null;
        String toDate = null;
        Boolean ignorePrevious = null;
        Boolean active = null;
        
        String defaultActionRequested = null;
        Boolean supportsComplete = null;
        Boolean supportsApprove = null;
        Boolean supportsAcknowledge = null;
        Boolean supportsFYI = null;

        if (defaultsElement != null) {
            // there's a defaults element, parse the values

            delegationType = defaultsElement.getChildText(DELEGATION_TYPE, RULE_TEMPLATE_NAMESPACE);
            ruleInstructions = defaultsElement.getChildText(RULE_INSTRUCTIONS, RULE_TEMPLATE_NAMESPACE);

            description = defaultsElement.getChildText(DESCRIPTION, RULE_TEMPLATE_NAMESPACE);
            
            // catch two fields that would normally be validated via schema but might not be present if invoking RuleXmlParser directly
            if (description == null) {
                throw new InvalidXmlException("Description must be specified in rule defaults");
            }
            if (ruleInstructions == null) {
                throw new InvalidXmlException("Instructions must be specified in rule defaults");
            }

            fromDate = defaultsElement.getChildText(FROM_DATE, RULE_TEMPLATE_NAMESPACE);
            toDate = defaultsElement.getChildText(TO_DATE, RULE_TEMPLATE_NAMESPACE);
            // toBooleanObject ensures that if the value is null (not set) that the Boolean object will likewise be null (will not default to a value)
            ignorePrevious = BooleanUtils.toBooleanObject(defaultsElement.getChildText(IGNORE_PREVIOUS, RULE_TEMPLATE_NAMESPACE));
            active = BooleanUtils.toBooleanObject(defaultsElement.getChildText(ACTIVE, RULE_TEMPLATE_NAMESPACE));
            
            defaultActionRequested = defaultsElement.getChildText(DEFAULT_ACTION_REQUESTED, RULE_TEMPLATE_NAMESPACE);
            supportsComplete = BooleanUtils.toBooleanObject(defaultsElement.getChildText(SUPPORTS_COMPLETE, RULE_TEMPLATE_NAMESPACE));
            supportsApprove = BooleanUtils.toBooleanObject(defaultsElement.getChildText(SUPPORTS_APPROVE, RULE_TEMPLATE_NAMESPACE));
            supportsAcknowledge = BooleanUtils.toBooleanObject(defaultsElement.getChildText(SUPPORTS_ACKNOWLEDGE, RULE_TEMPLATE_NAMESPACE));
            supportsFYI = BooleanUtils.toBooleanObject(defaultsElement.getChildText(SUPPORTS_FYI, RULE_TEMPLATE_NAMESPACE));
        }

        // corner case: rule template is updated but defaults are not specified (this is permitted)
        // in which case we need to initialize the otherwise mandatory rule defaults
        if (ruleInstructions == null) {
            ruleInstructions = "No default instructions specified";
        }
        if (description == null) {
            description = "No default description specified";
        }

        // remove any RuleTemplateOptions the template may have but that we know we aren't going to update/reset
        // (not sure if this case even exists...does anything else set rule template options?)
        updatedRuleTemplate.removeNonDefaultOptions();

        // NOTE: implementation detail: in contrast with the other options, the delegate template, and the rule attributes,
        // we unilaterally blow away the default rule and re-create it (we don't update the existing one, if there is one)
        if (updatedRuleTemplate.getRuleTemplateId() != null) {
            RuleBaseValues ruleDefaults = KEWServiceLocator.getRuleService().findDefaultRuleByRuleTemplateId(updatedRuleTemplate.getRuleTemplateId());
            if (ruleDefaults != null) {
                List ruleDelegationDefaults = KEWServiceLocator.getRuleDelegationService().findByDelegateRuleId(ruleDefaults.getRuleBaseValuesId());
                // delete the rule
                KEWServiceLocator.getRuleService().delete(ruleDefaults.getRuleBaseValuesId());
                // delete the associated rule delegation defaults
                for (Iterator iterator = ruleDelegationDefaults.iterator(); iterator.hasNext();) {
                    RuleDelegation ruleDelegation = (RuleDelegation) iterator.next();
                    KEWServiceLocator.getRuleDelegationService().delete(ruleDelegation.getRuleDelegationId());
                }
            }
            /*
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
            }*/

        }

        // now update the values

        boolean isDelegation = !Utilities.isEmpty(delegationType);
        if (isDelegation && !EdenConstants.DELEGATION_PRIMARY.equals(delegationType) && !EdenConstants.DELEGATION_SECONDARY.equals(delegationType)) {
            throw new InvalidXmlException("Invalid delegation type '" + delegationType + "'." + "  Expected one of: "
                    + EdenConstants.DELEGATION_PRIMARY + "," + EdenConstants.DELEGATION_SECONDARY);
        }

        // create our "default rule" which encapsulates the defaults for the rule
        RuleBaseValues ruleDefaults = new RuleBaseValues();

        // set simple values
        ruleDefaults.setRuleTemplate(updatedRuleTemplate);
        ruleDefaults.setDocTypeName(DUMMY_DOCUMENT_TYPE);
        ruleDefaults.setTemplateRuleInd(Boolean.TRUE);
        ruleDefaults.setCurrentInd(Boolean.TRUE);
        ruleDefaults.setVersionNbr(new Integer(0));
        ruleDefaults.setDescription(description);

        // these are non-nullable fields, so default them if they were not set in the defaults section
        ruleDefaults.setIgnorePrevious(Boolean.valueOf(BooleanUtils.isTrue(ignorePrevious)));
        ruleDefaults.setActiveInd(Boolean.valueOf(BooleanUtils.isTrue(active)));

        // set dates
        long farInTheFutureTime = -1;
        try {
            farInTheFutureTime = EdenConstants.getDefaultDateFormat().parse(FAR_IN_THE_FUTURE).getTime();
        } catch (ParseException e) {
            assert(false) : "This should never happen - fix the hardcoded date"; // or put it in a static initializer
            LOG.error("Error parsing default future date: " + FAR_IN_THE_FUTURE, e);
        }

        // if the future date is legit, set it
        if (farInTheFutureTime != -1) {
            ruleDefaults.setDeactivationDate(new Timestamp(farInTheFutureTime));
        }
        if (ruleDefaults.getActivationDate() == null) {
            ruleDefaults.setActivationDate(new Timestamp(System.currentTimeMillis()));
        }

        if (Utilities.isEmpty(fromDate)) {
            ruleDefaults.setFromDate(new Timestamp(System.currentTimeMillis()));
        } else {
            ruleDefaults.setFromDateString(fromDate);
        }
        if (Utilities.isEmpty(toDate)) {
            ruleDefaults.setToDate(new Timestamp(farInTheFutureTime));
        } else {
            ruleDefaults.setToDateString(toDate);
        }

        // ok, if this is a "Delegate Template", then we need to set this other RuleDelegation object which contains
        // some delegation-related info
        RuleDelegation ruleDelegationDefaults = null;
        if (isDelegation) {
            ruleDelegationDefaults = new RuleDelegation();
            ruleDelegationDefaults.setDelegationRuleBaseValues(ruleDefaults);
            ruleDelegationDefaults.setDelegationType(delegationType);
            ruleDelegationDefaults.setRuleResponsibilityId(new Long(-1));
        } else {
            // _otherwise_ it's a  normal template...store the template options that govern rule action constraints
            // in the RuleTemplateOptions of the template
            updateOrDeleteRuleTemplateOption(updatedRuleTemplate, EdenConstants.ACTION_REQUEST_DEFAULT_CD, defaultActionRequested);
            updateOrDeleteRuleTemplateOption(updatedRuleTemplate, EdenConstants.ACTION_REQUEST_APPROVE_REQ, supportsApprove);
            updateOrDeleteRuleTemplateOption(updatedRuleTemplate, EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, supportsAcknowledge);
            updateOrDeleteRuleTemplateOption(updatedRuleTemplate, EdenConstants.ACTION_REQUEST_FYI_REQ, supportsFYI);
            updateOrDeleteRuleTemplateOption(updatedRuleTemplate, EdenConstants.ACTION_REQUEST_COMPLETE_REQ, supportsComplete);
        }

        // always update the instructions RuleTemplateOption in either case
        updateOrDeleteRuleTemplateOption(updatedRuleTemplate, EdenConstants.RULE_INSTRUCTIONS_CD, ruleInstructions);

        // explicitly save the new rule delegation defaults and default rule
        KEWServiceLocator.getRuleTemplateService().save(ruleDelegationDefaults, ruleDefaults);
    }

    /**
     * Updates or deletes a specified rule template option on the rule template
     * @param updatedRuleTemplate the RuleTemplate being updated
     * @param key the option key
     * @param value the option value
     */
    protected void updateOrDeleteRuleTemplateOption(RuleTemplate updatedRuleTemplate, String key, Object value) {
        if (value != null) {
            // if the option exists and the incoming value is non-null (it's set), update it
            RuleTemplateOption option = updatedRuleTemplate.getRuleTemplateOption(key);
            if (option != null) {
                option.setValue(value.toString());
            } else {
                updatedRuleTemplate.getRuleTemplateOptions().add(new RuleTemplateOption(key, value.toString()));
            }
        } else {
            // otherwise if the incoming value IS null (not set), then explicitly remove the entry (if it exists)
            Iterator<RuleTemplateOption> options = updatedRuleTemplate.getRuleTemplateOptions().iterator();
            while (options.hasNext()) {
                RuleTemplateOption opt = options.next();
                if (key.equals(opt.getKey())) {
                    options.remove();
                    //KEWServiceLocator.getRuleTemplateService().deleteRuleTemplateOption(opt.getRuleTemplateOptionId());
                    break;
                }
            }
        }
    }

    /**
     * Updates the rule template delegation template with the one specified in the XML (if any)
     * @param ruleTemplateElement the XML ruleTemplate element
     * @param updatedRuleTemplate the rule template to update
     * @param parsedRuleTemplates the rule templates parsed in this parsing run
     * @throws InvalidXmlException if a delegation template was specified but could not be found
     */
    protected void updateDelegationTemplate(Element ruleTemplateElement, RuleTemplate updatedRuleTemplate, List<RuleTemplate> parsedRuleTemplates) throws InvalidXmlException {
        String delegateTemplateName = ruleTemplateElement.getChildText(DELEGATION_TEMPLATE, RULE_TEMPLATE_NAMESPACE);

        if (delegateTemplateName != null) {
            // if a delegateTemplate was set in the XML, then look it up and set it on the RuleTemplate object
            // first try looking up an existing delegateTemplate in the system
            RuleTemplate delegateTemplate = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(delegateTemplateName);

            // if not found, try the list of templates currently parsed
            if (delegateTemplate == null) {
                for (RuleTemplate rt: parsedRuleTemplates) {
                    if (delegateTemplateName.equalsIgnoreCase(rt.getName())) {
                        // set the expected next rule template id on the target delegateTemplate
                        Long ruleTemplateId = KEWServiceLocator.getRuleTemplateService().getNextRuleTemplateId();
                        rt.setRuleTemplateId(ruleTemplateId);
                        delegateTemplate = rt;
                        break;
                    }
                }
            }

            if (delegateTemplate == null) {
                throw new InvalidXmlException("Cannot find delegation template " + delegateTemplateName);
            }

            updatedRuleTemplate.setDelegationTemplateId(delegateTemplate.getDelegationTemplateId());
            updatedRuleTemplate.setDelegationTemplate(delegateTemplate);           
        } else {
            // remove the rule template association (...?)
            /*updatedRuleTemplate.setDelegationTemplate(null);
            updatedRuleTemplate.setDelegationTemplateId(null);*/
            // the previously referenced template is left in the system
        }
    }

    /**
     * Updates the attributes set on the RuleTemplate
     * @param ruleTemplateElement the XML ruleTemplate element
     * @param updatedRuleTemplate the RuleTemplate being updated
     * @throws InvalidXmlException if there was a problem parsing the rule template attributes
     */
    protected void updateRuleTemplateAttributes(Element ruleTemplateElement, RuleTemplate updatedRuleTemplate) throws InvalidXmlException {
        // add any newly defined rule template attributes to the rule template,
        // update the active and required flags of any existing ones.
        // if this is an update of an existing rule template, related attribute objects will be present in this rule template object,
        // otherwise none will be present (so they'll all be new)

        Element attributesElement = ruleTemplateElement.getChild(ATTRIBUTES, RULE_TEMPLATE_NAMESPACE);
        List<RuleTemplateAttribute> incomingAttributes = new ArrayList<RuleTemplateAttribute>();
        if (attributesElement != null) {
            incomingAttributes.addAll(parseRuleTemplateAttributes(attributesElement, updatedRuleTemplate));
        }

        // inactivate all current attributes
        for (RuleTemplateAttribute currentRuleTemplateAttribute: updatedRuleTemplate.getRuleTemplateAttributes()) {
            String ruleAttributeName = (currentRuleTemplateAttribute.getRuleAttribute() != null) ? currentRuleTemplateAttribute.getRuleAttribute().getName() : "(null)";
            LOG.debug("Inactivating rule template attribute with id " + currentRuleTemplateAttribute.getRuleTemplateAttributeId() + " and rule attribute with name " + ruleAttributeName);
            currentRuleTemplateAttribute.setActive(Boolean.FALSE);
        }
        // NOTE: attributes are deactivated, not removed
        // remove any attributes that were omitted
        //removeOmittedAttributes(updatedRuleTemplate, incomingAttributes);

        // add/update any new attributes
        for (RuleTemplateAttribute ruleTemplateAttribute: incomingAttributes) {
            RuleTemplateAttribute potentialExistingTemplateAttribute = updatedRuleTemplate.getRuleTemplateAttribute(ruleTemplateAttribute);
            if (potentialExistingTemplateAttribute != null) {
                // template attribute exists on rule template already; update the options
                potentialExistingTemplateAttribute.setActive(ruleTemplateAttribute.getActive());
                potentialExistingTemplateAttribute.setRequired(ruleTemplateAttribute.getRequired());
            } else {
                // template attribute does not yet exist on template so add it
                updatedRuleTemplate.getRuleTemplateAttributes().add(ruleTemplateAttribute);
            }
        }
    }

    /**
     * Removes any attributes which are defined on the rule template but omitted from the incoming list of attributes
     * @param updatedRuleTemplate the RuleTemplate being updated
     * @param incomingAttributes the incoming list of attributes
     */
//    protected void removeOmittedAttributes(RuleTemplate updatedRuleTemplate, List<RuleTemplateAttribute> incomingAttributes) {
//        Iterator<RuleTemplateAttribute> it = updatedRuleTemplate.getRuleTemplateAttributes().iterator();
//        while (it.hasNext()) {
//            if (!containsRuleTemplateAttributeName(incomingAttributes, it.next().getRuleAttribute().getName())) {
//                it.remove();
//            }
//        }
//    }

    /**
     * @param attributes a list of RuleTemplateAttribute
     * @param name name of RuleTemplateAttribute to find
     * @return whether the specified list of rule template attributes specifies the named attribute 
     */
//    protected boolean containsRuleTemplateAttributeName(List<RuleTemplateAttribute> attributes, String name) {
//        for (RuleTemplateAttribute rta: attributes) {
//            if (rta.getRuleAttribute() != null && name.equals(rta.getRuleAttribute().getName())) {
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * Parses the RuleTemplateAttributes defined on the rule template element
     * @param attributesElement the jdom Element object for the Rule Template attributes
     * @param ruleTemplate the RuleTemplate object
     * @return the RuleTemplateAttributes defined on the rule template element
     * @throws InvalidXmlException
     */
    private List<RuleTemplateAttribute> parseRuleTemplateAttributes(Element attributesElement, RuleTemplate ruleTemplate) throws InvalidXmlException {
        List<RuleTemplateAttribute> ruleTemplateAttributes = new ArrayList<RuleTemplateAttribute>();
        Vector attributeElements = XmlHelper.findElements(attributesElement, ATTRIBUTE);
        for (Iterator iterator = attributeElements.iterator(); iterator.hasNext();) {
            ruleTemplateAttributes.add(parseRuleTemplateAttribute((Element) iterator.next(), ruleTemplate));
        }
        return ruleTemplateAttributes;
    }

    /**
     * Parses a rule template attribute
     * @param element the attribute XML element
     * @param ruleTemplate the ruleTemplate to update
     * @return a parsed rule template attribute
     * @throws InvalidXmlException if the attribute does not exist
     */
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

    /**
     * Parses the defaults for this RuleTemplate.
     * @deprecated implementation moved to {@link #updateRuleTemplateDefaultOptions(Element, RuleTemplate)}
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
            /*
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
            }*/

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
