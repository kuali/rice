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
package org.kuali.rice.kew.xml;

import static org.kuali.rice.core.api.impex.xml.XmlConstants.ACTION_REQUESTED;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.APPROVE_POLICY;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.ATTRIBUTE_CLASS_NAME;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.DELEGATIONS;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.DELEGATION_TYPE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.DESCRIPTION;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.DOCUMENT_TYPE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.FORCE_ACTION;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.FROM_DATE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.GROUP_ID;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.GROUP_NAME;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.NAME;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.NAMESPACE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.PARENT_RESPONSIBILITY;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.PARENT_RULE_NAME;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.PRINCIPAL_ID;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.PRINCIPAL_NAME;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.PRIORITY;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RESPONSIBILITIES;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RESPONSIBILITY;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RESPONSIBILITY_ID;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.ROLE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.ROLE_NAME;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RULE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RULES;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RULE_DELEGATION;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RULE_DELEGATIONS;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RULE_EXPRESSION;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RULE_EXTENSIONS;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RULE_NAMESPACE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.RULE_TEMPLATE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.TO_DATE;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.USER;
import static org.kuali.rice.core.api.impex.xml.XmlConstants.WORKGROUP;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.core.util.xml.XmlException;
import org.kuali.rice.core.util.xml.XmlHelper;
import org.kuali.rice.kew.api.document.actions.ActionRequestPolicy;
import org.kuali.rice.kew.api.document.actions.DelegationType;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.rule.Role;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kew.rule.RuleExpressionDef;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kim.api.entity.principal.Principal;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.xml.sax.SAXException;

/**
 * Parses rules from XML.
 *
 * @see RuleBaseValues
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RuleXmlParser {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RuleXmlParser.class);

    /**
     * Priority to use if rule responsibility omits priority
     */
    private static final int DEFAULT_RULE_PRIORITY = 1;
    /**
     * Value of Force Action flag if omitted; default to false, we will NOT force action for approvals
     */
    private static final boolean DEFAULT_FORCE_ACTION = false;
    /**
     * Default approve policy, if omitted; defaults to FIRST_APPROVE, the request will be satisfied by the first approval
     */
    private static final String DEFAULT_APPROVE_POLICY = ActionRequestPolicy.FIRST.getCode();
    /**
     * Default action requested, if omitted; defaults to "A"pprove
     */
    private static final String DEFAULT_ACTION_REQUESTED = KEWConstants.ACTION_REQUEST_APPROVE_REQ;

    public List<RuleDelegation> parseRuleDelegations(InputStream input) throws IOException, XmlException {
    	try {
            Document doc = XmlHelper.trimSAXXml(input);
            Element root = doc.getRootElement();
            return parseRuleDelegations(root);
        } catch (JDOMException e) {
            throw new XmlException("Parse error.", e);
        } catch (SAXException e){
            throw new XmlException("Parse error.",e);
        } catch(ParserConfigurationException e){
            throw new XmlException("Parse error.",e);
        }
    }
    
    public List<RuleBaseValues> parseRules(InputStream input) throws IOException, XmlException {
        try {
            Document doc = XmlHelper.trimSAXXml(input);
            Element root = doc.getRootElement();
            return parseRules(root);
        } catch (JDOMException e) {
            throw new XmlException("Parse error.", e);
        } catch (SAXException e){
            throw new XmlException("Parse error.",e);
        } catch(ParserConfigurationException e){
            throw new XmlException("Parse error.",e);
        }
    }

    /**
     * Parses and saves rules
     * @param element top-level 'data' element which should contain a <rules> child element
     * @throws XmlException
     */
    public List<RuleBaseValues> parseRules(Element element) throws XmlException {
    	List<RuleBaseValues> rulesToSave = new ArrayList<RuleBaseValues>();
        for (Element rulesElement: (List<Element>) element.getChildren(RULES, RULE_NAMESPACE)) {
            for (Element ruleElement: (List<Element>) rulesElement.getChildren(RULE, RULE_NAMESPACE)) {
                RuleBaseValues rule = parseRule(ruleElement);
                rulesToSave.add(rule);
            }
        }
        checkForDuplicateRules(rulesToSave);
        return KEWServiceLocator.getRuleService().saveRules(rulesToSave, false);
    }
    
    /**
     * Parses and saves rule delegations
     * @param element top-level 'data' element which should contain a <rules> child element
     * @throws XmlException
     */
    public List<RuleDelegation> parseRuleDelegations(Element element) throws XmlException {
    	List<RuleDelegation> ruleDelegationsToSave = new ArrayList<RuleDelegation>();
        for (Element ruleDelegationsElement: (List<Element>) element.getChildren(RULE_DELEGATIONS, RULE_NAMESPACE)) {
            for (Element ruleDelegationElement: (List<Element>) ruleDelegationsElement.getChildren(RULE_DELEGATION, RULE_NAMESPACE)) {
                RuleDelegation ruleDelegation = parseRuleDelegation(ruleDelegationElement);
                ruleDelegationsToSave.add(ruleDelegation);
            }
        }
        //checkForDuplicateRuleDelegations(ruleDelegationsToSave);
        return KEWServiceLocator.getRuleService().saveRuleDelegations(ruleDelegationsToSave, false);
    }
    
    /**
     * Checks for rules in the List that duplicate other Rules already in the system 
     */
    private void checkForDuplicateRules(List<RuleBaseValues> rules) throws XmlException {
    	for (RuleBaseValues rule : rules) {
    		if (StringUtils.isBlank(rule.getName())) {
    			LOG.debug("Checking for rule duplication on an anonymous rule.");
    			checkRuleForDuplicate(rule);
    		}
    	}
    }
    
    /**
     * Checks for rule delegations in the List that duplicate other Rules already in the system 
     */
    private void checkForDuplicateRuleDelegations(List<RuleDelegation> ruleDelegations) throws XmlException {
    	for (RuleDelegation ruleDelegation : ruleDelegations) {
    		if (StringUtils.isBlank(ruleDelegation.getDelegationRuleBaseValues().getName())) {
    			LOG.debug("Checking for rule duplication on an anonymous rule delegation.");
    			checkRuleDelegationForDuplicate(ruleDelegation);
    		}
    	}
    }

    private RuleDelegation parseRuleDelegation(Element element) throws XmlException {
    	RuleDelegation ruleDelegation = new RuleDelegation();
    	Element parentResponsibilityElement = element.getChild(PARENT_RESPONSIBILITY, element.getNamespace());
    	if (parentResponsibilityElement == null) {
    		throw new XmlException("parent responsibility was not defined");
    	}
    	Long parentResponsibilityId = parseParentResponsibilityId(parentResponsibilityElement);
    	String delegationType = element.getChildText(DELEGATION_TYPE, element.getNamespace());
        if (delegationType == null || !(delegationType.equals(DelegationType.PRIMARY.getCode()) || delegationType.equals(DelegationType.SECONDARY.getCode()))) {
            throw new XmlException("Invalid delegation type specified for delegate rule '" + delegationType + "'");
        }
        
        ruleDelegation.setResponsibilityId(parentResponsibilityId);
        ruleDelegation.setDelegationType(delegationType);
        
        Element ruleElement = element.getChild(RULE, element.getNamespace());
        RuleBaseValues rule = parseRule(ruleElement);
        rule.setDelegateRule(true);
        ruleDelegation.setDelegationRuleBaseValues(rule);
    	
    	return ruleDelegation;
    }
    
    private Long parseParentResponsibilityId(Element element) throws XmlException {
    	String responsibilityId = element.getChildText(RESPONSIBILITY_ID, element.getNamespace());
    	if (!StringUtils.isBlank(responsibilityId)) {
    		return Long.valueOf(responsibilityId);
    	}
    	String parentRuleName = element.getChildText(PARENT_RULE_NAME, element.getNamespace());
    	if (StringUtils.isBlank(parentRuleName)) {
    		throw new XmlException("One of responsibilityId or parentRuleName needs to be defined");
    	}
    	RuleBaseValues parentRule = KEWServiceLocator.getRuleService().getRuleByName(parentRuleName);
    	if (parentRule == null) {
    		throw new XmlException("Could find the parent rule with name '" + parentRuleName + "'");
    	}
    	RuleResponsibility ruleResponsibilityNameAndType = parseResponsibilityNameAndType(element);
    	if (ruleResponsibilityNameAndType == null) {
    		throw new XmlException("Could not locate a valid responsibility declaration for the parent responsibility.");
    	}
    	Long parentResponsibilityId = KEWServiceLocator.getRuleService().findResponsibilityIdForRule(parentRuleName, 
    			ruleResponsibilityNameAndType.getRuleResponsibilityName(),
    			ruleResponsibilityNameAndType.getRuleResponsibilityType());
    	if (parentResponsibilityId == null) {
    		throw new XmlException("Failed to locate parent responsibility for rule with name '" + parentRuleName + "' and responsibility " + ruleResponsibilityNameAndType);
    	}
    	return parentResponsibilityId;
    }
    
    /**
     * Parses, and only parses, a rule definition (be it a top-level rule, or a rule delegation).  This method will
     * NOT dirty or save any existing data, it is side-effect-free.
     * @param element the rule element
     * @param ruleDelegation the ruleDelegation object if this rule is being parsed as a delegation
     * @return a new RuleBaseValues object which is not yet saved
     * @throws XmlException
     */
    private RuleBaseValues parseRule(Element element) throws XmlException {
        String name = element.getChildText(NAME, element.getNamespace());
        RuleBaseValues rule = createRule(name);
        
        setDefaultRuleValues(rule);
        rule.setName(name);
        
        String toDatestr = element.getChildText( TO_DATE, element.getNamespace());
        String fromDatestr = element.getChildText( FROM_DATE, element.getNamespace());
        rule.setToDate(formatDate("toDate", toDatestr));
        rule.setFromDate(formatDate("fromDate", fromDatestr));

        String description = element.getChildText(DESCRIPTION, element.getNamespace());
        if (StringUtils.isBlank(description)) {
            throw new XmlException("Rule must have a description.");
        }
                
        String documentTypeName = element.getChildText(DOCUMENT_TYPE, element.getNamespace());
        if (StringUtils.isBlank(documentTypeName)) {
        	throw new XmlException("Rule must have a document type.");
        }
        DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
        if (documentType == null) {
        	throw new XmlException("Could not locate document type '" + documentTypeName + "'");
        }

        RuleTemplate ruleTemplate = null;
        String ruleTemplateName = element.getChildText(RULE_TEMPLATE, element.getNamespace());        
        Element ruleExtensionsElement = element.getChild(RULE_EXTENSIONS, element.getNamespace());
        if (!StringUtils.isBlank(ruleTemplateName)) {
        	ruleTemplate = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(ruleTemplateName);
        	if (ruleTemplate == null) {
        		throw new XmlException("Could not locate rule template '" + ruleTemplateName + "'");
        	}
        } else {
        	if (ruleExtensionsElement != null) {
        		throw new XmlException("Templateless rules may not have rule extensions");
        	}
        }

        RuleExpressionDef ruleExpressionDef = null;
        Element exprElement = element.getChild(RULE_EXPRESSION, element.getNamespace());
        if (exprElement != null) {
        	String exprType = exprElement.getAttributeValue("type");
        	if (StringUtils.isEmpty(exprType)) {
        		throw new XmlException("Expression type must be specified");
        	}
        	String expression = exprElement.getTextTrim();
        	ruleExpressionDef = new RuleExpressionDef();
        	ruleExpressionDef.setType(exprType);
        	ruleExpressionDef.setExpression(expression);
        }
        
        String forceActionValue = element.getChildText(FORCE_ACTION, element.getNamespace());
        Boolean forceAction = Boolean.valueOf(DEFAULT_FORCE_ACTION);
        if (!StringUtils.isBlank(forceActionValue)) {
            forceAction = Boolean.valueOf(forceActionValue);
        }

        rule.setDocTypeName(documentType.getName());
        if (ruleTemplate != null) {
            rule.setRuleTemplateId(ruleTemplate.getRuleTemplateId());
            rule.setRuleTemplate(ruleTemplate);
        }
        if (ruleExpressionDef != null) {
            rule.setRuleExpressionDef(ruleExpressionDef);
        }
        rule.setDescription(description);
        rule.setForceAction(forceAction);

        Element responsibilitiesElement = element.getChild(RESPONSIBILITIES, element.getNamespace());
        rule.setResponsibilities(parseResponsibilities(responsibilitiesElement, rule));
        rule.setRuleExtensions(parseRuleExtensions(ruleExtensionsElement, rule));

        return rule;
    }
    
    /**
     * Creates the rule that the parser will populate.  If a rule with the given name
     * already exists, it's keys and responsibilities will be copied over to the
     * new rule.  The calling code will then sort through the original responsibilities
     * and compare them with those being defined on the XML being parsed.
     */
    private RuleBaseValues createRule(String ruleName) {
    	RuleBaseValues rule = new RuleBaseValues();
    	RuleBaseValues existingRule = (ruleName != null) ? KEWServiceLocator.getRuleService().getRuleByName(ruleName) : null;
    	if (existingRule != null) {
    		// copy keys and responsibiliities from the existing rule
    		rule.setRuleBaseValuesId(existingRule.getRuleBaseValuesId());
    		rule.setPreviousVersionId(existingRule.getPreviousVersionId());
    		rule.setPreviousVersion(existingRule.getPreviousVersion());
    		rule.setResponsibilities(existingRule.getResponsibilities());
    	}
    	return rule;
    }

    /**
     * Checks to see whether this anonymous rule duplicates an existing rule.
     * Currently the uniqueness is on ruleResponsibilityName, and extension key/values.
     * @param rule the rule to check
     * @throws XmlException if this incoming rule duplicates an existing rule
     */
    private void checkRuleForDuplicate(RuleBaseValues rule) throws XmlException {
        Long ruleId = KEWServiceLocator.getRuleService().getDuplicateRuleId(rule);
        if (ruleId != null) {
        	throw new XmlException("Rule '" + rule.getDescription() + "' on doc '" + rule.getDocTypeName() + "' is a duplicate of rule with rule Id " + ruleId);
        }
    }
    
    private void checkRuleDelegationForDuplicate(RuleDelegation ruleDelegation) throws XmlException {
    	checkRuleForDuplicate(ruleDelegation.getDelegationRuleBaseValues());
    }

    private void setDefaultRuleValues(RuleBaseValues rule) throws XmlException {
        rule.setForceAction(Boolean.FALSE);
        rule.setActivationDate(new Timestamp(System.currentTimeMillis()));
        rule.setActiveInd(Boolean.TRUE);
        rule.setCurrentInd(Boolean.TRUE);
        rule.setTemplateRuleInd(Boolean.FALSE);
        rule.setVersionNbr(new Integer(0));
        rule.setDelegateRule(false);
    }

    private List<RuleResponsibility> parseResponsibilities(Element element, RuleBaseValues rule) throws XmlException {
        if (element == null) {
            return new ArrayList<RuleResponsibility>(0);
        }
        List<RuleResponsibility> existingResponsibilities = rule.getResponsibilities();
        List<RuleResponsibility> responsibilities = new ArrayList<RuleResponsibility>();
        List responsibilityElements = element.getChildren(RESPONSIBILITY, element.getNamespace());
        for (Iterator iterator = responsibilityElements.iterator(); iterator.hasNext();) {
            Element responsibilityElement = (Element) iterator.next();
            RuleResponsibility responsibility = parseResponsibility(responsibilityElement, rule);
            reconcileWithExistingResponsibility(responsibility, existingResponsibilities);
            responsibilities.add(responsibility);
        }
        if (responsibilities.size() == 0) {
            throw new XmlException("Rule responsibility list must have at least one responsibility.");
        }
        return responsibilities;
    }

    public RuleResponsibility parseResponsibility(Element element, RuleBaseValues rule) throws XmlException {
        RuleResponsibility responsibility = new RuleResponsibility();
        responsibility.setRuleBaseValues(rule);
        String actionRequested = null;
        String priority = null;
        actionRequested = element.getChildText(ACTION_REQUESTED, element.getNamespace());
        if (StringUtils.isBlank(actionRequested)) {
        	actionRequested = DEFAULT_ACTION_REQUESTED;
        }
        priority = element.getChildText(PRIORITY, element.getNamespace());
        if (StringUtils.isBlank(priority)) {
        	priority = String.valueOf(DEFAULT_RULE_PRIORITY);
        }
        String approvePolicy = element.getChildText(APPROVE_POLICY, element.getNamespace());
        Element delegations = element.getChild(DELEGATIONS, element.getNamespace());
        if (actionRequested == null) {
            throw new XmlException("actionRequested is required on responsibility");
        }
        if (!actionRequested.equals(KEWConstants.ACTION_REQUEST_COMPLETE_REQ) && !actionRequested.equals(KEWConstants.ACTION_REQUEST_APPROVE_REQ) && !actionRequested.equals(KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ) && !actionRequested.equals(KEWConstants.ACTION_REQUEST_FYI_REQ)) {
            throw new XmlException("Invalid action requested code '" + actionRequested + "'");
        }
        if (StringUtils.isBlank(approvePolicy)) {
            approvePolicy = DEFAULT_APPROVE_POLICY;
        }
        if (!approvePolicy.equals(ActionRequestPolicy.ALL.getCode()) && !approvePolicy.equals(ActionRequestPolicy.FIRST.getCode())) {
            throw new XmlException("Invalid approve policy '" + approvePolicy + "'");
        }
        Integer priorityNumber = Integer.valueOf(priority);
        responsibility.setActionRequestedCd(actionRequested);
        responsibility.setPriority(priorityNumber);
        responsibility.setApprovePolicy(approvePolicy);
        
        RuleResponsibility responsibilityNameAndType = parseResponsibilityNameAndType(element);
        if (responsibilityNameAndType == null) {
        	throw new XmlException("Could not locate a valid responsibility declaration on a responsibility on rule with description '" + rule.getDescription() + "'");
        }
        if (responsibilityNameAndType.getRuleResponsibilityType().equals(KEWConstants.RULE_RESPONSIBILITY_GROUP_ID)
        		&& responsibility.getApprovePolicy().equals(ActionRequestPolicy.ALL.getCode())) {
        	throw new XmlException("Invalid approve policy '" + approvePolicy + "'.  This policy is not supported with Groups.");
        }
        responsibility.setRuleResponsibilityName(responsibilityNameAndType.getRuleResponsibilityName());
        responsibility.setRuleResponsibilityType(responsibilityNameAndType.getRuleResponsibilityType());
        
        return responsibility;
    }

    public RuleResponsibility parseResponsibilityNameAndType(Element element) throws XmlException {
    	RuleResponsibility responsibility = new RuleResponsibility();
    	
    	String principalId = element.getChildText(PRINCIPAL_ID, element.getNamespace());
        String principalName = element.getChildText(PRINCIPAL_NAME, element.getNamespace());
        String groupId = element.getChildText(GROUP_ID, element.getNamespace());
        Element groupNameElement = element.getChild(GROUP_NAME, element.getNamespace());
        String role = element.getChildText(ROLE, element.getNamespace());
        Element roleNameElement = element.getChild(ROLE_NAME, element.getNamespace());
        
        String user = element.getChildText(USER, element.getNamespace());
        String workgroup = element.getChildText(WORKGROUP, element.getNamespace());
        
        if (!StringUtils.isEmpty(user)) {
        	principalName = user;
        	LOG.warn("Rule XML is using deprecated element 'user', please use 'principalName' instead.");
        }
        
        // in code below, we allow core config parameter replacement in responsibilities
        if (!StringUtils.isBlank(principalId)) {
        	principalId = Utilities.substituteConfigParameters(principalId);
        	Principal principal = KimApiServiceLocator.getIdentityManagementService().getPrincipal(principalId);
            if (principal == null) {
            	throw new XmlException("Could not locate principal with the given id: " + principalId);
            }
            responsibility.setRuleResponsibilityName(principalId);
            responsibility.setRuleResponsibilityType(KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID);
        } else if (!StringUtils.isBlank(principalName)) {
        	principalName = Utilities.substituteConfigParameters(principalName);
        	Principal principal = KimApiServiceLocator.getIdentityManagementService().getPrincipalByPrincipalName(principalName);
            if (principal == null) {
            	throw new XmlException("Could not locate principal with the given name: " + principalName);
            }
            responsibility.setRuleResponsibilityName(principal.getPrincipalId());
            responsibility.setRuleResponsibilityType(KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID);
        } else if (!StringUtils.isBlank(groupId)) {
            groupId = Utilities.substituteConfigParameters(groupId);
            Group group = KimApiServiceLocator.getIdentityManagementService().getGroup(groupId);
            if (group == null) {
                throw new XmlException("Could not locate group with the given id: " + groupId);
            }
            responsibility.setRuleResponsibilityName(groupId);
            responsibility.setRuleResponsibilityType(KEWConstants.RULE_RESPONSIBILITY_GROUP_ID);
        } else if (groupNameElement != null) {
        	String groupName = groupNameElement.getText();
        	String groupNamespace = groupNameElement.getAttributeValue(NAMESPACE);
        	if (StringUtils.isBlank(groupName)) {
        		throw new XmlException("Group name element has no value");
        	}
        	if (StringUtils.isBlank(groupNamespace)) {
        		throw new XmlException("namespace attribute must be specified");
        	}
            groupName = Utilities.substituteConfigParameters(groupName);
            groupNamespace = Utilities.substituteConfigParameters(groupNamespace);
            Group group = KimApiServiceLocator.getIdentityManagementService().getGroupByName(groupNamespace, groupName);
            if (group == null) {
                throw new XmlException("Could not locate group with the given namespace: " + groupNamespace + " and name: " + groupName);
            }
            responsibility.setRuleResponsibilityName(group.getId());
            responsibility.setRuleResponsibilityType(KEWConstants.RULE_RESPONSIBILITY_GROUP_ID);
        } else if (!StringUtils.isBlank(role)) {
        	role = Utilities.substituteConfigParameters(role);
        	responsibility.setRuleResponsibilityName(role);
            responsibility.setRuleResponsibilityType(KEWConstants.RULE_RESPONSIBILITY_ROLE_ID);
        } else if (roleNameElement != null) {
        	String roleName = roleNameElement.getText();
        	String attributeClassName = roleNameElement.getAttributeValue(ATTRIBUTE_CLASS_NAME);
        	if (StringUtils.isBlank(roleName)) {
        		throw new XmlException("Role name element has no value");
        	}
        	if (StringUtils.isBlank(attributeClassName)) {
        		throw new XmlException("attributeClassName attribute must be specified");
        	}
        	roleName = Utilities.substituteConfigParameters(roleName);
        	attributeClassName = Utilities.substituteConfigParameters(attributeClassName);
        	responsibility.setRuleResponsibilityName(Role.constructRoleValue(attributeClassName, roleName));
            responsibility.setRuleResponsibilityType(KEWConstants.RULE_RESPONSIBILITY_ROLE_ID);
        } else if (!StringUtils.isBlank(workgroup)) {
        	LOG.warn("Rule XML is using deprecated element 'workgroup', please use 'groupName' instead.");
            workgroup = Utilities.substituteConfigParameters(workgroup);
            String workgroupNamespace = Utilities.parseGroupNamespaceCode(workgroup);
            String workgroupName = Utilities.parseGroupName(workgroup);

            Group workgroupObject = KimApiServiceLocator.getIdentityManagementService().getGroupByName(workgroupNamespace, workgroupName);
            if (workgroupObject == null) {
                throw new XmlException("Could not locate workgroup: " + workgroup);
            }
            responsibility.setRuleResponsibilityName(workgroupObject.getId());
            responsibility.setRuleResponsibilityType(KEWConstants.RULE_RESPONSIBILITY_GROUP_ID);
        } else {
        	return null;
        }
        
        return responsibility;
    }
    
    /**
     * Attempts to reconcile the given RuleResponsibility with the list of existing responsibilities (in the case of a
     * rule being updated via the XML).  This goal of this code is to copy responsibility ids from existing responsibilities
     * to the new responsibility where appropriate.  The code will attempt to find exact matches based on the values found
     * on the responsibilities.
     */
    private void reconcileWithExistingResponsibility(RuleResponsibility responsibility, List<RuleResponsibility> existingResponsibilities) {
    	if (existingResponsibilities == null || existingResponsibilities.isEmpty()) {
    		return;
    	}
    	RuleResponsibility exactMatch = null;
    	for (RuleResponsibility existingResponsibility : existingResponsibilities) {
    		if (isExactResponsibilityMatch(responsibility, existingResponsibility)) {
    			exactMatch = existingResponsibility;
    			break;
    		}
    	}
    	if (exactMatch != null) {
    		responsibility.setResponsibilityId(exactMatch.getResponsibilityId());
    	}
    }
    
    /**
     * Checks if the given responsibilities are exact matches of one another.
     */
    private boolean isExactResponsibilityMatch(RuleResponsibility newResponsibility, RuleResponsibility existingResponsibility) {
    	if (existingResponsibility.getResponsibilityId().equals(newResponsibility.getResponsibilityId())) {
    		return true;
    	}
    	if (existingResponsibility.getRuleResponsibilityName().equals(newResponsibility.getRuleResponsibilityName()) &&
    			existingResponsibility.getRuleResponsibilityType().equals(newResponsibility.getRuleResponsibilityType()) &&
    			existingResponsibility.getApprovePolicy().equals(newResponsibility.getApprovePolicy()) &&
    			existingResponsibility.getActionRequestedCd().equals(newResponsibility.getActionRequestedCd()) &&
    			existingResponsibility.getPriority().equals(newResponsibility.getPriority())) {
    		return true;
    	}
    	return false;
    }

    private List parseRuleExtensions(Element element, RuleBaseValues rule) throws XmlException {
        if (element == null) {
            return new ArrayList();
        }
        RuleExtensionXmlParser parser = new RuleExtensionXmlParser();
        return parser.parseRuleExtensions(element, rule);
    }
    
    public Timestamp formatDate(String dateLabel, String dateString) throws XmlException {
    	if (StringUtils.isBlank(dateString)) {
    		return null;
    	}
    	try {
    		return new Timestamp(RiceConstants.getDefaultDateFormat().parse(dateString).getTime());
    	} catch (ParseException e) {
    		throw new XmlException(dateLabel + " is not in the proper format.  Should have been: " + RiceConstants.DEFAULT_DATE_FORMAT_PATTERN);
    	}
    }
    
}
