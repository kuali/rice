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
import org.kuali.rice.kew.rule.xmlrouting.GenericXMLRuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;

/**
 * This class is the maintainable implementation for the Workflow {@link DocumentType} 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoutingRuleMaintainable extends KualiMaintainableImpl {

    private static final long serialVersionUID = -5920808902137192662L;

	private static final String RULE_TEMPLATE_ID_PARAM = "ruleCreationValues.ruleTemplateId";
	private static final String RULE_TEMPLATE_NAME_PARAM = "ruleCreationValues.ruleTemplateName";
	private static final String DOCUMENT_TYPE_NAME_PARAM = "ruleCreationValues.docTypeName";

	private static final String RULE_ATTRIBUTES_SECTION_ID = "RuleAttributes";
	private static final String ID_SEPARATOR = ":";
	
    /**
     * This overridden method resets the name
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterCopy(document, parameters);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#handleRouteStatusChange(org.kuali.rice.kns.bo.DocumentHeader)
     */
    @Override
    public void handleRouteStatusChange(DocumentHeader documentHeader) {
        super.handleRouteStatusChange(documentHeader);
        
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
    	translateResponsibilitiesForSave(getThisRule());
    	translateFieldValuesForSave(getThisRule());
    	// TODO execute save on RuleService
    }
    
    protected void translateResponsibilitiesForSave(RuleBaseValues rule) {
		rule.getResponsibilities().clear();
		for (PersonRuleResponsibility responsibility : rule.getPersonResponsibilities()) {
			RuleResponsibility ruleResponsibility = new RuleResponsibility();
			ruleResponsibility.setActionRequestedCd(responsibility.getActionRequestedCd());
			ruleResponsibility.setPriority(responsibility.getPriority());
			ruleResponsibility.setResponsibilityId(ruleResponsibility.getResponsibilityId());
			String principalId = KEWServiceLocator.getIdentityHelperService().getIdForPrincipalName(responsibility.getPrincipalName());
			ruleResponsibility.setRuleResponsibilityName(principalId);
			ruleResponsibility.setRuleResponsibilityType(KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID);
		}
		for (GroupRuleResponsibility responsibility : rule.getGroupResponsibilities()) {
			RuleResponsibility ruleResponsibility = new RuleResponsibility();
			ruleResponsibility.setActionRequestedCd(responsibility.getActionRequestedCd());
			ruleResponsibility.setPriority(responsibility.getPriority());
			ruleResponsibility.setResponsibilityId(ruleResponsibility.getResponsibilityId());
			KimGroup group = KEWServiceLocator.getIdentityHelperService().getGroupByName(responsibility.getNamespaceCode(), responsibility.getName());
			ruleResponsibility.setRuleResponsibilityName(group.getGroupId());
			ruleResponsibility.setRuleResponsibilityType(KEWConstants.RULE_RESPONSIBILITY_GROUP_ID);
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

	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterNew(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
	 */
	@Override
	public void processAfterNew(MaintenanceDocument document,
			Map<String, String[]> parameters) {
		validateRuleTemplateAndDocumentType(document, parameters);
		establishDefaultValues(document);
	}
	
	protected void validateRuleTemplateAndDocumentType(MaintenanceDocument document, Map<String, String[]> parameters) {
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
		initializeRuleAfterNew(getOldRule(document), ruleTemplate, documentTypeName);
		initializeRuleAfterNew(getNewRule(document), ruleTemplate, documentTypeName);
	}
	
	protected void initializeRuleAfterNew(RuleBaseValues rule, RuleTemplate ruleTemplate, String documentTypeName) {
		rule.setRuleTemplate(ruleTemplate);
		rule.setRuleTemplateId(ruleTemplate.getRuleTemplateId());
		rule.setDocTypeName(documentTypeName);
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
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#isGenerateDefaultValues()
	 */
	@Override
	public boolean isGenerateDefaultValues() {
		// This is a hack to get around the fact that when a document is first created, this value is
		// true which causes issues if you want to be able to initialize fields on  the document using
		// request parameters.  See SectionBridge.toSection for the "if" block where it populates
		// Field.propertyValue to see why this causes problems
		return false;
	}
	
	protected void establishDefaultValues(MaintenanceDocument document) {
		RuleBaseValues rule = getNewRule(document);
		rule.setActiveInd(true);
	}

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
	 * Overridden implementation of maintenance locks.  The default locking for Routing Rules
	 * is based on previous version (can't route more than one rule based off the same 
	 * previous verison).  However, for the first version of a rule, the previous version id
	 * will be null.
	 * 
	 * So for a new Route Rule maintenance document we don't want any locks generated.
	 */
	@Override
	public List<MaintenanceLock> generateMaintenanceLocks() {
		if (getThisRule().getPreviousVersionId() == null) {
			return Collections.emptyList();
		}
		return super.generateMaintenanceLocks();
	}
	
	
    
	
    

}
