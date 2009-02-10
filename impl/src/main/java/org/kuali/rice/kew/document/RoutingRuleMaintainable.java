/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kew.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.rule.GroupRuleResponsibility;
import org.kuali.rice.kew.rule.PersonRuleResponsibility;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleExtension;
import org.kuali.rice.kew.rule.RuleExtensionValue;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.rule.WorkflowAttribute;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttribute;
import org.kuali.rice.kew.rule.web.WebRuleUtils;
import org.kuali.rice.kew.rule.xmlrouting.GenericXMLRuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;

/**
 * This class is the maintainable implementation for Routing Rules 
 * in KEW (represented by the {@link RuleBaseValues} business object). 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoutingRuleMaintainable extends KualiMaintainableImpl {

    private static final long serialVersionUID = -5920808902137192662L;
    
	private static final String RULE_ATTRIBUTES_SECTION_ID = "RuleAttributes";
	private static final String ID_SEPARATOR = ":";

	/**
	 * Override the getSections method on this maintainable so that the Section Containing the various Rule Attributes
	 * can be dynamically generated based on the RuleTemplate which is selected.
	 */
	@Override
	public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
		List<Section> sections = super.getSections(document, oldMaintainable);
		List<Section> finalSections = new ArrayList<Section>();
		finalSections = new ArrayList<Section>();
		for (Section section : sections) {
			if (section.getSectionId().equals(RULE_ATTRIBUTES_SECTION_ID)) {
				List<Row> ruleTemplateRows = getRuleTemplateRows(document, oldMaintainable);
				if (!ruleTemplateRows.isEmpty()) {
					section.setRows(ruleTemplateRows);
					finalSections.add(section);
				}
			} else {
				finalSections.add(section);
			}
		}
		
		return finalSections;
	}
	
	protected List<Row> getRuleTemplateRows(MaintenanceDocument document, Maintainable oldMaintainable) {
		List<Row> rows = new ArrayList<Row>();
		RuleTemplate ruleTemplate = getThisRule().getRuleTemplate();
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
				
				// TODO move this validation else where
				//workflowAttribute.validateRuleData(getFieldMap(ruleTemplateAttribute.getRuleTemplateAttributeId()+""));
				
				List<Row> attributeRows = transformAndPopulateAttributeRows(workflowAttribute.getRuleRows(), ruleTemplateAttribute, getThisRule());
				rows.addAll(attributeRows);
				
				// TODO move this "role" code else where
				// if (workflowAttribute instanceof RoleAttribute) {
				//	RoleAttribute roleAttribute = (RoleAttribute) workflowAttribute;
				//	getRoles().addAll(roleAttribute.getRoleNames());
				//}
				
			}
		}
		return rows;
	}
	
	/**
	 * Processes the Fields on the various attributes Rows to assign an appropriate field name to them so that the
	 * field name rendered in the maintenance HTML will properly assign the value to RuleBaseValues.fieldValues.
	 */
	protected List<Row> transformAndPopulateAttributeRows(List<Row> attributeRows, RuleTemplateAttribute ruleTemplateAttribute, RuleBaseValues rule) {
		for (Row row : attributeRows) {
			for (Field field : row.getFields()) {
				String fieldName = field.getPropertyName();
				if (!StringUtils.isBlank(fieldName)) {
					String valueKey = ruleTemplateAttribute.getRuleTemplateAttributeId() + ID_SEPARATOR + fieldName;
					field.setPropertyName("fieldValues(" + valueKey + ")");
					field.setPropertyValue(rule.getFieldValues().get(valueKey));
				}
			}
		}
		return attributeRows;
	}
	
	/**
	 * On creation of a new rule document, we must validate that a rule template and document type are set. 
	 */
	@Override
	public void processAfterNew(MaintenanceDocument document,
			Map<String, String[]> parameters) {
		WebRuleUtils.validateRuleTemplateAndDocumentType(getOldRule(document), getNewRule(document), parameters);
		WebRuleUtils.establishDefaultRuleValues(getNewRule(document));
		getNewRule(document).setRouteHeaderId(new Long(document.getDocumentHeader().getDocumentNumber()));
	}
	
	/**
	 * This is a hack to get around the fact that when a document is first created, this value is
 	 * true which causes issues if you want to be able to initialize fields on  the document using
 	 * request parameters.  See SectionBridge.toSection for the "if" block where it populates
 	 * Field.propertyValue to see why this causes problems
	 */
	@Override
	public boolean isGenerateDefaultValues() {		
		return false;
	}
			
    /**
     * This is a complete override which does not call into
     * {@link KualiMaintainableImpl}. This method calls
     * {@link DocumentTypeService#versionAndSave(DocumentType)}.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
    	clearKeysForSave(getThisRule());
    	translateResponsibilitiesForSave(getThisRule());
    	translateFieldValuesForSave(getThisRule());
    	KEWServiceLocator.getRuleService().makeCurrent(getThisRule());
    }
    
	/**
	 * Since editing of a Rule should actually result in a rule with a new ID and new
	 * entries in the rule and rule responsibility tables, we need to clear out
	 * the primary keys of the rule and related objects.
	 */
	protected void clearKeysForSave(RuleBaseValues rule) {
		rule.setRuleBaseValuesId(null);
		rule.setActivationDate(null);
		rule.setDeactivationDate(null);
		rule.setCurrentInd(false);
		rule.setVersionNbr(null);
		rule.setVersionNumber(0L);
	}

    
    protected void translateResponsibilitiesForSave(RuleBaseValues rule) {
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
			ruleResponsibility.setApprovePolicy(KEWConstants.APPROVE_POLICY_FIRST_APPROVE);
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
			KimGroup group = KEWServiceLocator.getIdentityHelperService().getGroupByName(responsibility.getNamespaceCode(), responsibility.getName());
			ruleResponsibility.setRuleResponsibilityName(group.getGroupId());
			ruleResponsibility.setRuleResponsibilityType(KEWConstants.RULE_RESPONSIBILITY_GROUP_ID);
			ruleResponsibility.setApprovePolicy(KEWConstants.APPROVE_POLICY_FIRST_APPROVE);
			rule.getResponsibilities().add(ruleResponsibility);
		}
		// TODO add role responsibilities
	}
    
    protected void translateFieldValuesForSave(RuleBaseValues rule) {
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

			// TODO hook validation of rule data into PreRules
			// if (attValidationErrors != null && !attValidationErrors.isEmpty()) {
			// errorList.addAll(attValidationErrors);
			// } else {
			
				List ruleExtensionValues = workflowAttribute.getRuleExtensionValues();
				if (ruleExtensionValues != null && !ruleExtensionValues.isEmpty()) {
					RuleExtension ruleExtension = new RuleExtension();
					ruleExtension.setRuleTemplateAttributeId(ruleTemplateAttribute.getRuleTemplateAttributeId());

					ruleExtension.setExtensionValues(ruleExtensionValues);
					extensions.add(ruleExtension);
				}
				
			//}
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
    protected Map<String, String> getFieldMapForRuleTemplateAttribute(RuleBaseValues rule, RuleTemplateAttribute ruleTemplateAttribute) {
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
	
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
    	populateForCopyOrEdit(document);
    	clearKeysForCopy(document);
    	getNewRule(document).setRouteHeaderId(new Long(document.getDocumentHeader().getDocumentNumber()));
        super.processAfterCopy(document, parameters);
    }
    
    protected void clearKeysForCopy(MaintenanceDocument document) {
    	RuleBaseValues rule = getNewRule(document);
    	rule.setRuleBaseValuesId(null);
    	rule.setPreviousVersionId(null);
    	rule.setPreviousVersion(null);
    	for (PersonRuleResponsibility responsibility : rule.getPersonResponsibilities()) {
    		clearResponsibilityKeys(responsibility);
    	}
    	for (GroupRuleResponsibility responsibility : rule.getGroupResponsibilities()) {
    		clearResponsibilityKeys(responsibility);
    	}
    	// TODO - add roles
    }
    
    private void clearResponsibilityKeys(RuleResponsibility responsibility) {
		responsibility.setResponsibilityId(null);
		responsibility.setRuleResponsibilityKey(null);
		responsibility.setRuleBaseValuesId(null);
    }

	@Override
	public void processAfterEdit(MaintenanceDocument document,
			Map<String, String[]> parameters) {
		if (!getOldRule(document).getCurrentInd()) {
			throw new RiceRuntimeException("Cannot edit a non-current version of a rule.");
		}
		populateForCopyOrEdit(document);
		getNewRule(document).setRouteHeaderId(new Long(document.getDocumentHeader().getDocumentNumber()));
		super.processAfterEdit(document, parameters);
	}

	protected void populateForCopyOrEdit(MaintenanceDocument document) {
		RuleBaseValues oldRule = getOldRule(document);
		RuleBaseValues newRule = getNewRule(document);
		populateRuleMaintenanceFields(oldRule);
		populateRuleMaintenanceFields(newRule);
	}
	
	protected void establishPreviousVersion(RuleBaseValues oldRule, RuleBaseValues newRule) {
		newRule.setPreviousVersionId(oldRule.getRuleBaseValuesId());
	}
	
	/**
	 * This method populates fields on RuleBaseValues which are used only for
	 * maintenance purposes.  In otherwords, it populates the non-persistent fields
	 * on the RuleBaseValues which the maintenance document needs to function
	 * (such as the extension field values and responsibilities).
	 */
	protected void populateRuleMaintenanceFields(RuleBaseValues rule) {
		translateResponsibilitiesForLoad(rule);
		translateRuleExtensionsForLoad(rule);
	}
	
	protected void translateResponsibilitiesForLoad(RuleBaseValues rule) {
		for (RuleResponsibility responsibility : rule.getResponsibilities()) {
			if (responsibility.getRuleResponsibilityType().equals(KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID)) {
				PersonRuleResponsibility personResponsibility = new PersonRuleResponsibility();
				copyResponsibility(responsibility, personResponsibility);
				KimPrincipal principal = KEWServiceLocator.getIdentityHelperService().getPrincipal(personResponsibility.getRuleResponsibilityName());
				personResponsibility.setPrincipalName(principal.getPrincipalName());
				rule.getPersonResponsibilities().add(personResponsibility);
			} else if (responsibility.getRuleResponsibilityType().equals(KEWConstants.RULE_RESPONSIBILITY_GROUP_ID)) {
				GroupRuleResponsibility groupResponsibility = new GroupRuleResponsibility();
				copyResponsibility(responsibility, groupResponsibility);
				KimGroup group = KEWServiceLocator.getIdentityHelperService().getGroup(groupResponsibility.getRuleResponsibilityName());
				groupResponsibility.setNamespaceCode(group.getNamespaceCode());
				groupResponsibility.setName(group.getGroupName());
				rule.getGroupResponsibilities().add(groupResponsibility);
			} else if (responsibility.getRuleResponsibilityType().equals(KEWConstants.RULE_RESPONSIBILITY_ROLE_ID)) {
				// TODO add roles!
			} else {
				throw new RiceRuntimeException("Original responsibility with id '" + responsibility.getRuleResponsibilityKey() + "' contained a bad type code of '" + responsibility.getRuleResponsibilityType());
			}
		}
		// since we've loaded the responsibilities, let's clear the originals so they don't get serialized to the maint doc XML
		rule.getResponsibilities().clear();
	}
	
	private void copyResponsibility(RuleResponsibility source, RuleResponsibility target) {
		try {
			BeanUtils.copyProperties(target, source);
		} catch (Exception e) {
			throw new RiceRuntimeException("Failed to copy properties from source to target responsibility", e);
		}
	}
	
	protected void translateRuleExtensionsForLoad(RuleBaseValues rule) {
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
	
	/**
	 * Returns the new RuleBaseValues business object.
	 */
	protected RuleBaseValues getNewRule(MaintenanceDocument document) {
		return (RuleBaseValues)document.getNewMaintainableObject().getBusinessObject();
	}
	
	/**
	 * Returns the old RuleBaseValues business object.
	 */
	protected RuleBaseValues getOldRule(MaintenanceDocument document) {
		return (RuleBaseValues)document.getOldMaintainableObject().getBusinessObject();
	}
	
	/**
	 * Returns the RuleBaseValues business object associated with this Maintainable.
	 */
	protected RuleBaseValues getThisRule() {
		return (RuleBaseValues)getBusinessObject();
	}

	/**
	 * Overridden implementation of maintenance locks.  The default locking for Routing Rules
	 * is based on previous version (can't route more than one rule based off the same 
	 * previous verison).  However, for the first version of a rule, the previous version id
	 * will be null.
	 * 
	 * So for a new Route Rule maintenance document we don't want any locks generated.
	 * 
	 * TODO can we just let the locking key be the primary key? (ruleBaseValuesId)
	 */
	@Override
	public List<MaintenanceLock> generateMaintenanceLocks() {
		if (getThisRule().getPreviousVersionId() == null) {
			return Collections.emptyList();
		}
		return super.generateMaintenanceLocks();
	}

	@Override
	public String getDocumentTitle(MaintenanceDocument document) {
		StringBuffer title = new StringBuffer();
        RuleBaseValues rule = getThisRule();
        if (rule.getPreviousVersionId() != null) {
            title.append("Editing Rule '").append(rule.getDescription()).append("'");
        } else {
            title.append("Adding Rule '").append(rule.getDescription()).append("'");
        }
        return title.toString();	
	}

}
