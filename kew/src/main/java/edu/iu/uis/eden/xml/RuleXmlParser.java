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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.doctype.DocumentType;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.exception.InvalidXmlException;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleDelegation;
import edu.iu.uis.eden.routetemplate.RuleResponsibility;
import edu.iu.uis.eden.routetemplate.RuleService;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.util.XmlHelper;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.Workgroup;

/**
 * Parses rules from XML.
 * 
 * @see RuleBaseValues
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleXmlParser implements XmlConstants {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RuleXmlParser.class);

    /**
     * Priority to use if rule responsibility omits priority
     */
    private static final int DEFAULT_RULE_PRIORITY = 1;
    /**
     * Value of Ignore Previous flag if omitted; default to false, we will NOT ignore previous approvals
     */
    private static final boolean DEFAULT_IGNORE_PREVIOUS = false;
    /**
     * Default approve policy, if omitted; defaults to FIRST_APPROVE, the request will be satisfied by the first approval
     */
    private static final String DEFAULT_APPROVE_POLICY = EdenConstants.APPROVE_POLICY_FIRST_APPROVE;
    /**
     * Default action requested, if omitted; defaults to "A"pprove
     */
    private static final String DEFAULT_ACTION_REQUESTED = EdenConstants.ACTION_REQUEST_APPROVE_REQ;

    /** original code
    public List parseRules(InputStream input) throws IOException, InvalidXmlException {
    
        SAXBuilder builder = new SAXBuilder(false);
        try {
            Document doc = builder.build(input);
            Element root = doc.getRootElement();
            return parseRules(root);
        } catch (JDOMException e) {
            throw new InvalidXmlException("Parse error.", e);
        }
    }
    */
    
    public List<RuleBaseValues> parseRules(InputStream input) throws IOException, InvalidXmlException {
        try {
            Document doc = XmlHelper.trimSAXXml(input);
            Element root = doc.getRootElement();
            return parseRules(root);
        } catch (JDOMException e) {
            throw new InvalidXmlException("Parse error.", e);
        } catch (SAXException e){
			throw new InvalidXmlException("Parse error.",e);
		} catch(ParserConfigurationException e){
			throw new InvalidXmlException("Parse error.",e);
		}
    }
    
    /**
     * Parses and saves rules
     * @param element top-level 'data' element which should contain a <rules> child element
     * @return a list of parsed and saved, current, rules; if parsing resulted in a rule update only the current rule version is returned
     * @throws InvalidXmlException
     */
    public List<RuleBaseValues> parseRules(Element element) throws InvalidXmlException {
        List<RuleBaseValues> rules = new ArrayList<RuleBaseValues>();
        for (Element rulesElement: (List<Element>) element.getChildren(RULES, RULE_NAMESPACE)) {

            for (Element ruleElement: (List<Element>) rulesElement.getChildren(RULE, RULE_NAMESPACE)) {
                rules.add(parseAndUpdateRule(ruleElement, null));
            }
        }
        for (RuleBaseValues rule: rules) {
            RuleService ruleService = KEWServiceLocator.getRuleService();
            try {
                ruleService.save2(rule);
                ruleService.notifyCacheOfRuleChange(rule, null);
            } catch (Exception e) {
                LOG.error("Error saving rule entered by XML", e);
                throw new RuntimeException("Error parsing rules.", e);
            }
        }
        return rules;
    }

    /**
     * Updates a rule, if necessary.  A rule may be named, in which case the parser will search for a previous version
     * and if found, deactivate the previous version and link the new version to it.  This will result in a save of the old
     * rule (so this method is not side-effect free).  If the rule is anonymous, it will be checked to see if it is a "duplicate"
     * of an existing, anonymous, rule.
     * @param rule
     * @throws InvalidXmlException
     */
    private void updateRule(RuleBaseValues rule) throws InvalidXmlException {
        if (rule.getName() == null) {
            LOG.debug("Creating a new anonymous rule");
            checkRuleForDuplicate(rule);
            return;
        }
        
        if (rule.getDelegateRule().booleanValue()) {
            throw new InvalidXmlException("Rule delegations cannot be named!");
        }

        // have to deactivate existing rule and create new rule...
        RuleBaseValues foundRule = KEWServiceLocator.getRuleService().getRuleByName(rule.getName());
        // have to deactivate existing rule and create new rule...
        if (foundRule == null) {
            LOG.error("Rule named '" + rule.getName() + "' not found, creating new rule named '" + rule.getName() + "'");
        } else {
            LOG.error("Rule named '" + rule.getName() + "' found, creating a new version");
            foundRule.setActiveInd(Boolean.FALSE);
            foundRule.setCurrentInd(Boolean.FALSE);
            try {
                KEWServiceLocator.getRuleService().save2(foundRule);
            } catch (Exception e) {
                throw new RuntimeException("Error parsing rules.", e);
            }
            rule.setPreviousVersion(foundRule);
            rule.setVersionNbr(foundRule.getVersionNbr() + 1);
        }
    }

    /**
     * Parses, and only parses, a rule definition (be it a top-level rule, or a rule delegation).  This method will
     * NOT dirty or save any existing data, it is side-effect-free.
     * @param element the rule element
     * @param ruleDelegation the ruleDelegation object if this rule is being parsed as a delegation
     * @return a new RuleBaseValues object which is not yet saved
     * @throws InvalidXmlException
     */
    private RuleBaseValues parseRule(Element element, RuleDelegation ruleDelegation) throws InvalidXmlException {
        String name = element.getChildText(NAME, RULE_NAMESPACE);

        RuleBaseValues rule = new RuleBaseValues();

        if (name != null && ruleDelegation != null) {
            throw new RuntimeException("Rule delegations cannot be named!");
        }

        rule.setName(name);

        setDefaultRuleValues(rule);
        rule.setDelegateRule(new Boolean(ruleDelegation != null));
        String description = element.getChildText(DESCRIPTION, RULE_NAMESPACE);
        String ignorePreviousValue = element.getChildText(IGNORE_PREVIOUS, RULE_NAMESPACE);
        Element responsibilitiesElement = element.getChild(RESPONSIBILITIES, RULE_NAMESPACE);
        Element ruleExtensionsElement = element.getChild(RULE_EXTENSIONS, RULE_NAMESPACE);
        if (description == null) {
            throw new InvalidXmlException("Rule must have a description.");
        }
        DocumentType documentType = null;
        RuleTemplate ruleTemplate = null;
        String ruleTemplateName = element.getChildText(RULE_TEMPLATE, RULE_NAMESPACE);

        if (ruleDelegation != null && Utilities.isEmpty(ruleTemplateName)) {
            RuleBaseValues parentRule = ruleDelegation.getRuleResponsibility().getRuleBaseValues();
            documentType = KEWServiceLocator.getDocumentTypeService().findByName(parentRule.getDocTypeName());
            ruleTemplate = parentRule.getRuleTemplate().getDelegationTemplate();
            if (ruleTemplate == null) {
                throw new InvalidXmlException("Rule template of parent rule does not have a proper delegation template.");
            }
        } else {
            String documentTypeName = element.getChildText(DOCUMENT_TYPE, RULE_NAMESPACE);
            if (documentTypeName == null) {
                throw new InvalidXmlException("Rule must have a document type.");
            }
            if (ruleTemplateName == null) {
                throw new InvalidXmlException("Rule must have a rule template.");
            }
            documentType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
            ruleTemplate = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(ruleTemplateName);
            if (documentType == null) {
                throw new InvalidXmlException("Could not locate document type '" + documentTypeName + "'");
            }
            if (ruleTemplate == null) {
                throw new InvalidXmlException("Could not locate rule template '" + ruleTemplateName + "'");
            }
        }
        Boolean ignorePrevious = Boolean.valueOf(DEFAULT_IGNORE_PREVIOUS);
        if (ignorePreviousValue != null) {
            ignorePrevious = Boolean.valueOf(ignorePreviousValue);
        }

        rule.setDocTypeName(documentType.getName());
        rule.setRuleTemplateId(ruleTemplate.getRuleTemplateId());
        rule.setRuleTemplate(ruleTemplate);
        rule.setDescription(description);
        rule.setIgnorePrevious(ignorePrevious);
        rule.setResponsibilities(parseResponsibilities(responsibilitiesElement, rule, ruleDelegation));
        rule.setRuleExtensions(parseRuleExtensions(ruleExtensionsElement, rule));
        
        return rule;
    }

    /**
     * Parses and updates a single rule definition.  A rule may be named, in which case the parser will search for a previous version
     * and if found, deactivate the previous version and link the new version to it.  This will result in a save of the old
     * rule (so this method is not side-effect free).
     * @param element the rule element
     * @param ruleDelegation the ruleDelegation object if this rule is being parsed as a delegation
     * @return a RuleBaseValues object which is not yet saved
     * @throws InvalidXmlException
     */
    private RuleBaseValues parseAndUpdateRule(Element element, RuleDelegation ruleDelegation) throws InvalidXmlException {
        RuleBaseValues rule = parseRule(element, ruleDelegation);
        updateRule(rule);
        return rule;
    }

    /**
     * Checks to see whether this anonymous rule duplicates an existing rule.
     * Currently the uniqueness is on ruleResponsibilityName, and extension key/values.
     * @param rule the rule to check
     * @throws InvalidXmlException if this incoming rule duplicates an existing rule
     */
    private void checkRuleForDuplicate(RuleBaseValues rule) throws InvalidXmlException {
        List responsibilities = rule.getResponsibilities();
        List extensions = rule.getRuleExtensions();
        String docTypeName = rule.getDocTypeName();
        // should we use name or id? which should we consider the primary key for purposes
        // of equality evaluation?
        String ruleTemplateName = rule.getRuleTemplateName();
        List rules = KEWServiceLocator.getRuleService().fetchAllRules(true);
        Iterator it = rules.iterator();
        while (it.hasNext()) {
            RuleBaseValues r = (RuleBaseValues) it.next();
            if (Utilities.equals(docTypeName, r.getDocTypeName()) &&
                Utilities.equals(ruleTemplateName, r.getRuleTemplateName()) &&
                Utilities.collectionsEquivalent(responsibilities, r.getResponsibilities()) &&
                Utilities.collectionsEquivalent(extensions, r.getRuleExtensions())) {
                // we have a duplicate
                throw new InvalidXmlException("Rule '" + rule.getDescription() + "' on doc '" + rule.getDocTypeName() + "' is a duplicate of rule '" + r.getDescription() + "' on doc '" + r.getDocTypeName() + "'");
            }
        }
    }

    private void setDefaultRuleValues(RuleBaseValues rule) throws InvalidXmlException {
        rule.setIgnorePrevious(Boolean.FALSE);
        rule.setActivationDate(new Timestamp(System.currentTimeMillis()));
        rule.setActiveInd(Boolean.TRUE);
        rule.setCurrentInd(Boolean.TRUE);
        rule.setFromDate(new Timestamp(System.currentTimeMillis()));
        rule.setTemplateRuleInd(Boolean.FALSE);
        rule.setVersionNbr(new Integer(0));
        try {
            rule.setDeactivationDate(new Timestamp(new SimpleDateFormat("MM/dd/yyyy").parse("01/01/2100").getTime()));
            rule.setToDate(new Timestamp(new SimpleDateFormat("MM/dd/yyyy").parse("01/01/2100").getTime()));
        } catch (ParseException e) {
            throw new InvalidXmlException("Could not parse toDate.", e);
        }
    }

    private List parseResponsibilities(Element element, RuleBaseValues rule, RuleDelegation ruleDelegation) throws InvalidXmlException {
        if (element == null) {
            throw new InvalidXmlException("Rule must have at least one responsibility.");
        }
        List responsibilities = new ArrayList();
        List responsibilityElements = element.getChildren(RESPONSIBILITY, RULE_NAMESPACE);
        for (Iterator iterator = responsibilityElements.iterator(); iterator.hasNext();) {
            Element responsibilityElement = (Element) iterator.next();
            responsibilities.add(parseResponsibility(responsibilityElement, rule, ruleDelegation));
        }
        if (responsibilities.size() == 0) {
            throw new InvalidXmlException("Rule must have at least one responsibility.");
        }
        return responsibilities;
    }

    public RuleResponsibility parseResponsibility(Element element, RuleBaseValues rule, RuleDelegation ruleDelegation) throws InvalidXmlException {
        RuleResponsibility responsibility = new RuleResponsibility();
        responsibility.setRuleBaseValues(rule);
        String actionRequested = null;
        String priority = null;
        if (ruleDelegation == null) {
            actionRequested = element.getChildText(ACTION_REQUESTED, RULE_NAMESPACE);
            if (actionRequested == null) {
                actionRequested = DEFAULT_ACTION_REQUESTED;
            }
            priority = element.getChildText(PRIORITY, RULE_NAMESPACE);
            if (priority == null) {
                priority = String.valueOf(DEFAULT_RULE_PRIORITY);
            }
        } else {
            actionRequested = ruleDelegation.getRuleResponsibility().getActionRequestedCd();
            priority = ruleDelegation.getRuleResponsibility().getPriority().toString();
        }
        String user = element.getChildText(USER, RULE_NAMESPACE);
        String workgroup = element.getChildText(WORKGROUP, RULE_NAMESPACE);
        String role = element.getChildText(ROLE, RULE_NAMESPACE);
        String approvePolicy = element.getChildText(APPROVE_POLICY, RULE_NAMESPACE);
        Element delegations = element.getChild(DELEGATIONS, RULE_NAMESPACE);
        if (actionRequested == null) {
            throw new InvalidXmlException("actionRequested is required on responsibility");
        }
        if (!actionRequested.equals(EdenConstants.ACTION_REQUEST_COMPLETE_REQ) && !actionRequested.equals(EdenConstants.ACTION_REQUEST_APPROVE_REQ) && !actionRequested.equals(EdenConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ) && !actionRequested.equals(EdenConstants.ACTION_REQUEST_FYI_REQ)) {
            throw new InvalidXmlException("Invalid action requested code '" + actionRequested + "'");
        }
        if (user == null && workgroup == null && role == null) {
            throw new InvalidXmlException("One of user, workgroup, or role must be specified on responsibility.");
        }
        if (approvePolicy == null) {
            approvePolicy = DEFAULT_APPROVE_POLICY;
        }
        if (!approvePolicy.equals(EdenConstants.APPROVE_POLICY_ALL_APPROVE) && !approvePolicy.equals(EdenConstants.APPROVE_POLICY_FIRST_APPROVE)) {
            throw new InvalidXmlException("Invalid approve policy '" + approvePolicy + "'");
        }
        if (priority == null) {
            throw new InvalidXmlException("Must specify a priority on responsibility");
        }
        Integer priorityNumber = Integer.valueOf(priority);
        responsibility.setActionRequestedCd(actionRequested);
        responsibility.setPriority(priorityNumber);
        responsibility.setApprovePolicy(approvePolicy);
        if (user != null) {
            // allow core config parameter replacement in responsibilities
            user = Utilities.substituteConfigParameters(user);
            try {
                WorkflowUser workflowUser = KEWServiceLocator.getUserService().getWorkflowUser(new AuthenticationUserId(user));
                if (workflowUser == null) {
                    throw new InvalidXmlException("Could not locate workflow user for given network id: " + user);
                }
                responsibility.setRuleResponsibilityName(workflowUser.getWorkflowId());
                responsibility.setRuleResponsibilityType(EdenConstants.RULE_RESPONSIBILITY_WORKFLOW_ID);
            } catch (EdenUserNotFoundException e) {
                throw new InvalidXmlException(e);
            }
        } else if (workgroup != null) {
            // allow core config parameter replacement in responsibilities
            workgroup = Utilities.substituteConfigParameters(workgroup);
            Workgroup workgroupObject = KEWServiceLocator.getWorkgroupService().getWorkgroup(new GroupNameId(workgroup));
            if (workgroupObject == null) {
                throw new InvalidXmlException("Could not locate workgroup: " + workgroup);
            }
            responsibility.setRuleResponsibilityName(workgroupObject.getWorkflowGroupId().getGroupId().toString());
            responsibility.setRuleResponsibilityType(EdenConstants.RULE_RESPONSIBILITY_WORKGROUP_ID);
        } else if (role != null) {
            responsibility.setRuleResponsibilityName(role);
            responsibility.setRuleResponsibilityType(EdenConstants.RULE_RESPONSIBILITY_ROLE_ID);
        }
        if (ruleDelegation == null && delegations != null) {
            responsibility.setDelegationRules(parseRuleDelegations(delegations, responsibility));
        }
        return responsibility;
    }

    private List parseRuleDelegations(Element element, RuleResponsibility responsibility) throws InvalidXmlException {
        List ruleDelegations = new ArrayList();
        List ruleElements = element.getChildren(RULE, RULE_NAMESPACE);
        for (Iterator iterator = ruleElements.iterator(); iterator.hasNext();) {
            ruleDelegations.add(parseRuleDelegation((Element) iterator.next(), responsibility));
        }
        return ruleDelegations;
    }

    private RuleDelegation parseRuleDelegation(Element element, RuleResponsibility responsibility) throws InvalidXmlException {
        String delegationType = element.getChildText(DELEGATION_TYPE, RULE_NAMESPACE);
        if (delegationType == null || !(delegationType.equals(EdenConstants.DELEGATION_PRIMARY) || delegationType.equals(EdenConstants.DELEGATION_SECONDARY))) {
            throw new InvalidXmlException("Invalid delegation type specified for delegate rule '" + delegationType + "'");
        }
        RuleDelegation ruleDelegation = new RuleDelegation();
        ruleDelegation.setDelegationType(delegationType);
        ruleDelegation.setRuleResponsibility(responsibility);
        ruleDelegation.setDelegationRuleBaseValues(parseAndUpdateRule(element, ruleDelegation));
        return ruleDelegation;
    }

    private List parseRuleExtensions(Element element, RuleBaseValues rule) throws InvalidXmlException {
        if (element == null) {
            return new ArrayList();
        }
        RuleExtensionXmlParser parser = new RuleExtensionXmlParser();
        return parser.parseRuleExtensions(element, rule);
    }

}
