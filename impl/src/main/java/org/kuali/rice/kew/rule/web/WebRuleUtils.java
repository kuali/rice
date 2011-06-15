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
package org.kuali.rice.kew.rule.web;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceRuntimeException;
import org.kuali.rice.kew.api.action.ActionRequestPolicy;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.rule.GroupRuleResponsibility;
import org.kuali.rice.kew.rule.PersonRuleResponsibility;
import org.kuali.rice.kew.rule.RoleRuleResponsibility;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kew.rule.RuleExtension;
import org.kuali.rice.kew.rule.RuleExtensionValue;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.rule.WorkflowAttribute;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttribute;
import org.kuali.rice.kew.rule.service.RuleService;
import org.kuali.rice.kew.rule.xmlrouting.GenericXMLRuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.entity.principal.Principal;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.krad.web.ui.Field;
import org.kuali.rice.krad.web.ui.Row;
import org.kuali.rice.krad.web.ui.Section;


/**
 * Some utilities which are utilized by the {@link RuleAction}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class WebRuleUtils {

	public static final String RULE_TEMPLATE_ID_PARAM = "ruleCreationValues.ruleTemplateId";
	public static final String RULE_TEMPLATE_NAME_PARAM = "ruleCreationValues.ruleTemplateName";
	public static final String DOCUMENT_TYPE_NAME_PARAM = "ruleCreationValues.docTypeName";
	public static final String RESPONSIBILITY_ID_PARAM = "ruleCreationValues.responsibilityId";
	
	private static final String ID_SEPARATOR = ":";
	private static final String RULE_ATTRIBUTES_SECTION_ID = "RuleAttributes";
	private static final String RULE_ATTRIBUTES_SECTION_TITLE = "Rule Attributes";
	private static final String ROLES_MAINTENANCE_SECTION_ID = "RolesMaintenance";
	
	private WebRuleUtils() {
		throw new UnsupportedOperationException("do not call");
	}
	
	/**
	 * Copies the existing rule onto the current document.  This is used within the web-based rule GUI to make a
	 * copy of a rule on the existing document.  Essentially, this method makes a copy of the rule and all
	 * delegates but preserves the document ID of the original rule.
	 */
    public static WebRuleBaseValues copyRuleOntoExistingDocument(WebRuleBaseValues rule) throws Exception {
        WebRuleBaseValues ruleCopy = new WebRuleBaseValues();
        PropertyUtils.copyProperties(ruleCopy, rule);
        ruleCopy.setPreviousVersionId(null);
        ruleCopy.setCurrentInd(null);
        ruleCopy.setVersionNbr(null);

        List responsibilities = new ArrayList();
        for (Iterator iter = ruleCopy.getResponsibilities().iterator(); iter.hasNext();) {
            WebRuleResponsibility responsibility = (WebRuleResponsibility) iter.next();
            WebRuleResponsibility responsibilityCopy = new WebRuleResponsibility();
            PropertyUtils.copyProperties(responsibilityCopy, responsibility);

            responsibilityCopy.setResponsibilityId(null);
            responsibilityCopy.setRuleResponsibilityKey(null);
            
            List delegations = new ArrayList();
            for (Iterator iterator = responsibilityCopy.getDelegationRules().iterator(); iterator.hasNext();) {
                RuleDelegation delegation = (RuleDelegation) iterator.next();
                RuleDelegation delegationCopy = new RuleDelegation();
                PropertyUtils.copyProperties(delegationCopy, delegation);

                delegationCopy.setDelegateRuleId(null);
                delegationCopy.setVersionNumber(null);
                delegationCopy.setRuleDelegationId(null);
                delegationCopy.setResponsibilityId(null);

                WebRuleBaseValues delegationRule = ((WebRuleBaseValues) delegation.getDelegationRuleBaseValues());
                WebRuleBaseValues ruleDelegateCopy = new WebRuleBaseValues();
                PropertyUtils.copyProperties(ruleDelegateCopy, delegationRule);

                ruleDelegateCopy.setPreviousVersionId(null);
                ruleDelegateCopy.setCurrentInd(null);
                ruleDelegateCopy.setVersionNbr(null);

                List delegateResps = new ArrayList();
                for (Iterator iterator1 = ruleDelegateCopy.getResponsibilities().iterator(); iterator1.hasNext();) {
                    WebRuleResponsibility delegateResp = (WebRuleResponsibility) iterator1.next();
                    WebRuleResponsibility delegateRespCopy = new WebRuleResponsibility();
                    PropertyUtils.copyProperties(delegateRespCopy, delegateResp);

                    delegateRespCopy.setResponsibilityId(null);
                    delegateRespCopy.setRuleResponsibilityKey(null);
                    delegateResps.add(delegateRespCopy);
                }
                ruleDelegateCopy.setResponsibilities(delegateResps);
                delegationCopy.setDelegationRuleBaseValues(ruleDelegateCopy);
                delegations.add(delegationCopy);
            }
            //responsibilityCopy.setDelegationRules(delegations);
            responsibilities.add(responsibilityCopy);
        }
        ruleCopy.setResponsibilities(responsibilities);
        return ruleCopy;
    }
    
    /**
     * Makes a copy of the rule and clears the document id on the rule and any of its delegates.
     * This method is used for making a copy of a rule for a new document.  It essentially calls
     * the copyRuleOntoExistingDocument method and then clears out the document IDs.
     * 
     * @param webRuleBaseValues
     */
    public static WebRuleBaseValues copyToNewRule(WebRuleBaseValues webRuleBaseValues) throws Exception {
    	WebRuleBaseValues newRule = copyRuleOntoExistingDocument(webRuleBaseValues);
    	// clear out all document IDs on the rule and it's delegates
    	newRule.setDocumentId(null);
    	for (Iterator iterator = newRule.getResponsibilities().iterator(); iterator.hasNext(); ) {
			RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
			for (Iterator iterator2 = responsibility.getDelegationRules().iterator(); iterator2.hasNext(); ) {
				RuleDelegation delegation = (RuleDelegation) iterator2.next();
				delegation.getDelegationRuleBaseValues().setDocumentId(null);
			}
		}
    	return newRule;
    }

    public static void validateRuleTemplateAndDocumentType(RuleBaseValues oldRule, RuleBaseValues newRule, Map<String, String[]> parameters) {
		String[] ruleTemplateIds = parameters.get(RULE_TEMPLATE_ID_PARAM);
		String[] ruleTemplateNames = parameters.get(RULE_TEMPLATE_NAME_PARAM);
		String[] documentTypeNames = parameters.get(DOCUMENT_TYPE_NAME_PARAM);
		if (ArrayUtils.isEmpty(ruleTemplateIds) && ArrayUtils.isEmpty(ruleTemplateNames)) {
			throw new RiceRuntimeException("Rule document must be initiated with a valid rule template id or rule template name.");
		}
		if (ArrayUtils.isEmpty(documentTypeNames)) {
			throw new RiceRuntimeException("Rule document must be initiated with a valid document type name.");
		}
		RuleTemplate ruleTemplate = null;
		if (!ArrayUtils.isEmpty(ruleTemplateIds)) {
			String ruleTemplateId = ruleTemplateIds[0];
			ruleTemplate = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateId(new Long(ruleTemplateId));
			if (ruleTemplate == null) {
				throw new RiceRuntimeException("Failed to load rule template with id '" + ruleTemplateId + "'");
			}
		}
		if (ruleTemplate == null) {
			String ruleTemplateName = ruleTemplateNames[0];
			ruleTemplate = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateName(ruleTemplateName);
			if (ruleTemplate == null) {
				throw new RiceRuntimeException("Failed to load rule template with name '" + ruleTemplateName + "'");
			}
		}
		String documentTypeName = documentTypeNames[0];
		DocumentType documentType = KEWServiceLocator.getDocumentTypeService().findByName(documentTypeName);
		if (documentType == null) {
			throw new RiceRuntimeException("Failed to locate document type with name '" + documentTypeName + "'");
		}
		
		// it appears that there is always an old maintainable, even in the case of a new document creation,
		// if we don't initialize both the old and new versions we get errors during meshSections
		initializeRuleAfterNew(oldRule, ruleTemplate, documentTypeName);
		initializeRuleAfterNew(newRule, ruleTemplate, documentTypeName);
	}
    
	private static void initializeRuleAfterNew(RuleBaseValues rule, RuleTemplate ruleTemplate, String documentTypeName) {
		rule.setRuleTemplate(ruleTemplate);
		rule.setRuleTemplateId(ruleTemplate.getRuleTemplateId());
		rule.setDocTypeName(documentTypeName);
	}
	
	public static void validateRuleAndResponsibility(RuleDelegation oldRuleDelegation, RuleDelegation newRuleDelegation, Map<String, String[]> parameters) {
		String[] responsibilityIds = parameters.get(RESPONSIBILITY_ID_PARAM);
		if (ArrayUtils.isEmpty(responsibilityIds)) {
			throw new RiceRuntimeException("Delegation rule document must be initiated with a valid responsibility ID to delegate from.");
		}
		if (!ArrayUtils.isEmpty(responsibilityIds)) {
			Long responsibilityId = new Long(responsibilityIds[0]);
			RuleResponsibility ruleResponsibility = KEWServiceLocator.getRuleService().findRuleResponsibility(responsibilityId);
			if (ruleResponsibility == null) {
				throw new RiceRuntimeException("Failed to locate a rule responsibility for responsibility ID " + responsibilityId);
			}
			oldRuleDelegation.setResponsibilityId(responsibilityId);
			newRuleDelegation.setResponsibilityId(responsibilityId);
		}
		
	}

	public static void establishDefaultRuleValues(RuleBaseValues rule) {
		rule.setActiveInd(true);

        RuleBaseValues defaultRule = ((RuleService) KEWServiceLocator.getService(KEWServiceLocator.RULE_SERVICE)).findDefaultRuleByRuleTemplateId(
        		rule.getRuleTemplate().getDelegationTemplateId());
        if (defaultRule != null) {
            defaultRule.setActivationDate(null);
            defaultRule.setCurrentInd(null);
            defaultRule.setDeactivationDate(null);
            defaultRule.setDocTypeName(null);
            defaultRule.setVersionNumber(null);
            defaultRule.setRuleBaseValuesId(null);
            defaultRule.setTemplateRuleInd(Boolean.FALSE);
            defaultRule.setVersionNbr(null);
            try {
				PropertyUtils.copyProperties(rule, defaultRule);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
        }
	}
	

	public static List customizeSections(RuleBaseValues rule, List<Section> sections, boolean delegateRule) {

		List<Section> finalSections = new ArrayList<Section>();
		for (Section section : sections) {
			// unfortunately, in the case of an inquiry the sectionId will always be null so we have to check section title
			if (section.getSectionTitle().equals(RULE_ATTRIBUTES_SECTION_TITLE) || 
					RULE_ATTRIBUTES_SECTION_ID.equals(section.getSectionId())) {
				List<Row> ruleTemplateRows = getRuleTemplateRows(rule, delegateRule);
				if (!ruleTemplateRows.isEmpty()) {
					section.setRows(ruleTemplateRows);
					finalSections.add(section);
				}
			} else if (ROLES_MAINTENANCE_SECTION_ID.equals(section.getSectionId())) {
				if (hasRoles(rule)) {
					finalSections.add(section);
				}
			} else {
				finalSections.add(section);
			}
		}
		
		return finalSections;
	}
	
	
	

	public static List<Row> getRuleTemplateRows(RuleBaseValues rule, boolean delegateRule) {

		List<Row> rows = new ArrayList<Row>();
		RuleTemplate ruleTemplate = rule.getRuleTemplate();
		Map<String, String> fieldNameMap = new HashMap<String, String>();
		// refetch rule template from service because after persistence in KNS, it comes back without any rule template attributes
		if (ruleTemplate != null){
			ruleTemplate = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateId(ruleTemplate.getRuleTemplateId());
			if (ruleTemplate != null) {
				
				List<RuleTemplateAttribute> ruleTemplateAttributes = ruleTemplate.getActiveRuleTemplateAttributes();
				Collections.sort(ruleTemplateAttributes);

				for (RuleTemplateAttribute ruleTemplateAttribute : ruleTemplateAttributes) {
					if (!ruleTemplateAttribute.isWorkflowAttribute()) {
						continue;
					}
					WorkflowAttribute workflowAttribute = ruleTemplateAttribute.getWorkflowAttribute();
					RuleAttribute ruleAttribute = ruleTemplateAttribute.getRuleAttribute();
					if (ruleAttribute.getType().equals(KEWConstants.RULE_XML_ATTRIBUTE_TYPE)) {
						((GenericXMLRuleAttribute) workflowAttribute).setRuleAttribute(ruleAttribute);
					}
					Map<String, String> parameterMap = getFieldMapForRuleTemplateAttribute(rule, ruleTemplateAttribute);
					workflowAttribute.validateRuleData(parameterMap);
					List<Row> attributeRows = transformAndPopulateAttributeRows(workflowAttribute.getRuleRows(), ruleTemplateAttribute, rule, fieldNameMap, delegateRule);
					rows.addAll(attributeRows);

				}
			}
			transformFieldConversions(rows, fieldNameMap);
		}
		return rows;
	}
	
	public static void transformFieldConversions(List<Row> rows, Map<String, String> fieldNameMap) {
		for (Row row : rows) {
			Map<String, String> transformedFieldConversions = new HashMap<String, String>();
			for (Field field : row.getFields()) {
				Map<String, String> fieldConversions = field.getFieldConversionMap();
				for (String lookupFieldName : fieldConversions.keySet()) {
					String localFieldName = fieldConversions.get(lookupFieldName);
					if (fieldNameMap.containsKey(localFieldName)) {
						// set the transformed value
						transformedFieldConversions.put(lookupFieldName, fieldNameMap.get(localFieldName));
					} else {
						// set the original value (not sure if this case will happen, but just in case)
						transformedFieldConversions.put(lookupFieldName, fieldConversions.get(lookupFieldName));
					}
				}
				field.setFieldConversions(transformedFieldConversions);
			}
		}
	}
	


	
	private static boolean hasRoles(RuleBaseValues rule) {
		RuleTemplate ruleTemplate = rule.getRuleTemplate();
		return !ruleTemplate.getRoles().isEmpty();
	}
	
	/**
	 * Processes the Fields on the various attributes Rows to assign an appropriate field name to them so that the
	 * field name rendered in the maintenance HTML will properly assign the value to RuleBaseValues.fieldValues.
	 */
	
	public static List<Row> transformAndPopulateAttributeRows(List<Row> attributeRows, RuleTemplateAttribute ruleTemplateAttribute, RuleBaseValues rule, Map<String, String> fieldNameMap, boolean delegateRule) {

		for (Row row : attributeRows) {
			for (Field field : row.getFields()) {
				String fieldName = field.getPropertyName();
				if (!StringUtils.isBlank(fieldName)) {
					String valueKey = ruleTemplateAttribute.getRuleTemplateAttributeId() + ID_SEPARATOR + fieldName;

					String propertyName;
					
					if (delegateRule) {
						propertyName = "delegationRuleBaseValues.fieldValues(" + valueKey + ")"; 
					} else {
						propertyName = "fieldValues(" + valueKey + ")"; 
					}

					fieldNameMap.put(fieldName, propertyName);
					field.setPropertyName(propertyName);
					field.setPropertyValue(rule.getFieldValues().get(valueKey));
				}
			}
		}
		return attributeRows;
	}
	
	/**
	 * Since editing of a Rule should actually result in a rule with a new ID and new
	 * entries in the rule and rule responsibility tables, we need to clear out
	 * the primary keys of the rule and related objects.
	 */
	public static void clearKeysForSave(RuleBaseValues rule) {
		rule.setRuleBaseValuesId(null);
		rule.setActivationDate(null);
		rule.setDeactivationDate(null);
		rule.setCurrentInd(false);
		rule.setVersionNbr(null);
		rule.setObjectId(null);
		rule.setVersionNumber(0L);
	}
	
	public static void clearKeysForSave(RuleDelegation ruleDelegation) {
		ruleDelegation.setRuleDelegationId(null);
		ruleDelegation.setObjectId(null);
		ruleDelegation.setVersionNumber(0L);
		clearKeysForSave(ruleDelegation.getDelegationRuleBaseValues());
	}
	
    public static void translateResponsibilitiesForSave(RuleBaseValues rule) {
		rule.getResponsibilities().clear();
		for (PersonRuleResponsibility responsibility : rule.getPersonResponsibilities()) {
			RuleResponsibility ruleResponsibility = new RuleResponsibility();
			ruleResponsibility.setActionRequestedCd(responsibility.getActionRequestedCd());
			ruleResponsibility.setPriority(responsibility.getPriority());
			ruleResponsibility.setResponsibilityId(responsibility.getResponsibilityId());
			if (ruleResponsibility.getResponsibilityId() == null) {
				ruleResponsibility.setResponsibilityId(KEWServiceLocator.getResponsibilityIdService().getNewResponsibilityId());
			}
			String principalId = KEWServiceLocator.getIdentityHelperService().getIdForPrincipalName(responsibility.getPrincipalName());
			ruleResponsibility.setRuleResponsibilityName(principalId);
			ruleResponsibility.setRuleResponsibilityType(KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID);
			// default the approve policy to First Approve
			ruleResponsibility.setApprovePolicy(ActionRequestPolicy.FIRST.getCode());
			rule.getResponsibilities().add(ruleResponsibility);
		}
		for (GroupRuleResponsibility responsibility : rule.getGroupResponsibilities()) {
			RuleResponsibility ruleResponsibility = new RuleResponsibility();
			ruleResponsibility.setActionRequestedCd(responsibility.getActionRequestedCd());
			ruleResponsibility.setPriority(responsibility.getPriority());
			ruleResponsibility.setResponsibilityId(responsibility.getResponsibilityId());
			if (ruleResponsibility.getResponsibilityId() == null) {
				ruleResponsibility.setResponsibilityId(KEWServiceLocator.getResponsibilityIdService().getNewResponsibilityId());
			}
			Group group = KEWServiceLocator.getIdentityHelperService().getGroupByName(responsibility.getNamespaceCode(), responsibility.getName());
			ruleResponsibility.setRuleResponsibilityName(group.getId());
			ruleResponsibility.setRuleResponsibilityType(KEWConstants.RULE_RESPONSIBILITY_GROUP_ID);
			ruleResponsibility.setApprovePolicy(ActionRequestPolicy.FIRST.getCode());
			rule.getResponsibilities().add(ruleResponsibility);
		}
		for (RoleRuleResponsibility responsibility : rule.getRoleResponsibilities()) {
			RuleResponsibility ruleResponsibility = new RuleResponsibility();
			ruleResponsibility.setActionRequestedCd(responsibility.getActionRequestedCd());
			ruleResponsibility.setPriority(responsibility.getPriority());
			ruleResponsibility.setResponsibilityId(responsibility.getResponsibilityId());
			if (ruleResponsibility.getResponsibilityId() == null) {
				ruleResponsibility.setResponsibilityId(KEWServiceLocator.getResponsibilityIdService().getNewResponsibilityId());
			}
			ruleResponsibility.setRuleResponsibilityName(responsibility.getRoleName());
			ruleResponsibility.setRuleResponsibilityType(KEWConstants.RULE_RESPONSIBILITY_ROLE_ID);
			ruleResponsibility.setApprovePolicy(responsibility.getApprovePolicy());
			rule.getResponsibilities().add(ruleResponsibility);
		}
	}
    
    public static void translateFieldValuesForSave(RuleBaseValues rule) {
    	RuleTemplate ruleTemplate = KEWServiceLocator.getRuleTemplateService().findByRuleTemplateId(rule.getRuleTemplateId());

		/** Populate rule extension values * */
		List extensions = new ArrayList();
		for (Iterator iterator = ruleTemplate.getActiveRuleTemplateAttributes().iterator(); iterator.hasNext();) {
			RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) iterator.next();
			if (!ruleTemplateAttribute.isWorkflowAttribute()) {
				continue;
			}
			WorkflowAttribute workflowAttribute = ruleTemplateAttribute.getWorkflowAttribute();

			RuleAttribute ruleAttribute = ruleTemplateAttribute.getRuleAttribute();
			if (ruleAttribute.getType().equals(KEWConstants.RULE_XML_ATTRIBUTE_TYPE)) {
				((GenericXMLRuleAttribute) workflowAttribute).setRuleAttribute(ruleAttribute);
			}

			Map<String, String> parameterMap = getFieldMapForRuleTemplateAttribute(rule, ruleTemplateAttribute);
						
			// validate rule data populates the rule extension values for us
			List attValidationErrors = workflowAttribute.validateRuleData(parameterMap);

			// because validation should be handled by business rules now, if we encounter a validation error at this point in
			// time, let's throw an exception
			if (attValidationErrors != null && !attValidationErrors.isEmpty()) {
				throw new RiceRuntimeException("Encountered attribute validation errors when attempting to save the Rule!");
			}
			
			List ruleExtensionValues = workflowAttribute.getRuleExtensionValues();
			if (ruleExtensionValues != null && !ruleExtensionValues.isEmpty()) {
				RuleExtension ruleExtension = new RuleExtension();
				ruleExtension.setRuleTemplateAttributeId(ruleTemplateAttribute.getRuleTemplateAttributeId());

				ruleExtension.setExtensionValues(ruleExtensionValues);
				extensions.add(ruleExtension);
			}
				
		}
		rule.setRuleExtensions(extensions);

		for (Iterator iterator = rule.getRuleExtensions().iterator(); iterator.hasNext();) {
			RuleExtension ruleExtension = (RuleExtension) iterator.next();
			ruleExtension.setRuleBaseValues(rule);

			for (Iterator iterator2 = ruleTemplate.getActiveRuleTemplateAttributes().iterator(); iterator2.hasNext();) {
				RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) iterator2.next();
				if (ruleTemplateAttribute.getRuleTemplateAttributeId().longValue() == ruleExtension.getRuleTemplateAttributeId().longValue()) {
					ruleExtension.setRuleTemplateAttribute(ruleTemplateAttribute);
					break;
				}
			}

			for (Iterator iterator2 = ruleExtension.getExtensionValues().iterator(); iterator2.hasNext();) {
				RuleExtensionValue ruleExtensionValue = (RuleExtensionValue) iterator2.next();
				ruleExtensionValue.setExtension(ruleExtension);
			}
		}
    }

    /**
     * Based on original logic implemented in Rule system.  Essentially constructs a Map of field values related
     * to the given RuleTemplateAttribute.
     */
    public static Map<String, String> getFieldMapForRuleTemplateAttribute(RuleBaseValues rule, RuleTemplateAttribute ruleTemplateAttribute) {
    	Map<String, String> fieldMap = new HashMap<String, String>();
    	for (String fieldKey : rule.getFieldValues().keySet()) {
    		String ruleTemplateAttributeId = fieldKey.substring(0, fieldKey.indexOf(ID_SEPARATOR));
    		String fieldName = fieldKey.substring(fieldKey.indexOf(ID_SEPARATOR) + 1);
    		if (ruleTemplateAttribute.getRuleTemplateAttributeId().toString().equals(ruleTemplateAttributeId)) {
    			fieldMap.put(fieldName, rule.getFieldValues().get(fieldKey));
    		}
    	}
    	return fieldMap;
    }
    
    public static void processRuleForDelegationSave(RuleDelegation ruleDelegation) {
    	RuleBaseValues rule = ruleDelegation.getDelegationRuleBaseValues();
    	rule.setDelegateRule(true);
    	// certain items on a delegated rule responsibility are inherited from parent responsibility, set them to null
    	for (RuleResponsibility responsibility : rule.getResponsibilities()) {
    		responsibility.setActionRequestedCd(null);
    		responsibility.setPriority(null);
    	}
    }
    
    public static void populateForCopyOrEdit(RuleBaseValues oldRule, RuleBaseValues newRule) {
		populateRuleMaintenanceFields(oldRule);
		populateRuleMaintenanceFields(newRule);
		// in the case of copy, our fields which are marked read only are cleared, this includes the rule template
		// name and the document type name but we don't want these cleared
		if (newRule.getRuleTemplate().getName() == null) {
			newRule.getRuleTemplate().setName(oldRule.getRuleTemplate().getName());
		}
		if (newRule.getDocTypeName() == null) {
			newRule.setDocTypeName(oldRule.getDocTypeName());
		}
	}
    
    /**
	 * This method populates fields on RuleBaseValues which are used only for
	 * maintenance purposes.  In otherwords, it populates the non-persistent fields
	 * on the RuleBaseValues which the maintenance document needs to function
	 * (such as the extension field values and responsibilities).
	 */
	public static void populateRuleMaintenanceFields(RuleBaseValues rule) {
		translateResponsibilitiesForLoad(rule);
		translateRuleExtensionsForLoad(rule);
	}
	
	public static void translateResponsibilitiesForLoad(RuleBaseValues rule) {
		for (RuleResponsibility responsibility : rule.getResponsibilities()) {
			if (responsibility.getRuleResponsibilityType().equals(KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID)) {
				PersonRuleResponsibility personResponsibility = new PersonRuleResponsibility();
				copyResponsibility(responsibility, personResponsibility);
				Principal principal = KEWServiceLocator.getIdentityHelperService().getPrincipal(personResponsibility.getRuleResponsibilityName());
				personResponsibility.setPrincipalName(principal.getPrincipalName());
				rule.getPersonResponsibilities().add(personResponsibility);
			} else if (responsibility.getRuleResponsibilityType().equals(KEWConstants.RULE_RESPONSIBILITY_GROUP_ID)) {
				GroupRuleResponsibility groupResponsibility = new GroupRuleResponsibility();
				copyResponsibility(responsibility, groupResponsibility);
				Group group = KEWServiceLocator.getIdentityHelperService().getGroup(groupResponsibility.getRuleResponsibilityName());
				groupResponsibility.setNamespaceCode(group.getNamespaceCode());
				groupResponsibility.setName(group.getName());
				rule.getGroupResponsibilities().add(groupResponsibility);
			} else if (responsibility.getRuleResponsibilityType().equals(KEWConstants.RULE_RESPONSIBILITY_ROLE_ID)) {
				RoleRuleResponsibility roleResponsibility = new RoleRuleResponsibility();
				copyResponsibility(responsibility, roleResponsibility);
				rule.getRoleResponsibilities().add(roleResponsibility);
			} else {
				throw new RiceRuntimeException("Original responsibility with id '" + responsibility.getRuleResponsibilityKey() + "' contained a bad type code of '" + responsibility.getRuleResponsibilityType());
			}
		}
		// since we've loaded the responsibilities, let's clear the originals so they don't get serialized to the maint doc XML
		rule.getResponsibilities().clear();
	}
	
	public static void copyResponsibility(RuleResponsibility source, RuleResponsibility target) {
		try {
			BeanUtils.copyProperties(target, source);
		} catch (Exception e) {
			throw new RiceRuntimeException("Failed to copy properties from source to target responsibility", e);
		}
	}
	
	public static void translateRuleExtensionsForLoad(RuleBaseValues rule) {
		for (RuleExtension ruleExtension : rule.getRuleExtensions()) {
			Long ruleTemplateAttributeId = ruleExtension.getRuleTemplateAttributeId();
			for (RuleExtensionValue ruleExtensionValue : ruleExtension.getExtensionValues()) {
				String fieldMapKey = ruleTemplateAttributeId + ID_SEPARATOR + ruleExtensionValue.getKey();
				rule.getFieldValues().put(fieldMapKey, ruleExtensionValue.getValue());
			}
		}
		// since we've loaded the extensions, let's clear the originals so that they don't get serialized to the maint doc XML
		rule.getRuleExtensions().clear();
	}
	
	public static void processRuleForCopy(String documentNumber, RuleBaseValues oldRule, RuleBaseValues newRule) {
		WebRuleUtils.populateForCopyOrEdit(oldRule, newRule);
		clearKeysForCopy(newRule);
		newRule.setDocumentId(documentNumber);
	}
	
	public static void clearKeysForCopy(RuleBaseValues rule) {    	
    	rule.setRuleBaseValuesId(null);
    	rule.setPreviousVersionId(null);
    	rule.setPreviousVersion(null);
    	rule.setName(null);
    	for (PersonRuleResponsibility responsibility : rule.getPersonResponsibilities()) {
    		clearResponsibilityKeys(responsibility);
    	}
    	for (GroupRuleResponsibility responsibility : rule.getGroupResponsibilities()) {
    		clearResponsibilityKeys(responsibility);
    	}
    	for (RoleRuleResponsibility responsibility : rule.getRoleResponsibilities()) {
    		clearResponsibilityKeys(responsibility);
    	}
    }

    private static void clearResponsibilityKeys(RuleResponsibility responsibility) {
		responsibility.setResponsibilityId(null);
		responsibility.setRuleResponsibilityKey(null);
		responsibility.setRuleBaseValuesId(null);
    }
    
}
