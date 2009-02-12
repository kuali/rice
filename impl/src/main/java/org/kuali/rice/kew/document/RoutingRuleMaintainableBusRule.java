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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.rule.GroupRuleResponsibility;
import org.kuali.rice.kew.rule.PersonRuleResponsibility;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.rule.WorkflowAttribute;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttribute;
import org.kuali.rice.kew.rule.xmlrouting.GenericXMLRuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;



/**
 * This is a description of what this class does - Garey don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoutingRuleMaintainableBusRule extends MaintenanceDocumentRuleBase {

	private static final String ID_SEPARATOR = ":";

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
	 */
	@Override
	protected boolean processCustomSaveDocumentBusinessRules(
			MaintenanceDocument document) {

		boolean isValid = true;

		RuleBaseValues ruleBaseValues = getNewRule(document);

		isValid &= this.populateErrorMap(ruleBaseValues);


		return isValid;
	}

	protected void populateErrorMap(Map<String,String> errorMap){
		for(Map.Entry<String, String> entry : errorMap.entrySet()){
			this.putFieldError(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, org.kuali.rice.kns.bo.PersistableBusinessObject)
	 */
	@Override
	public boolean processCustomAddCollectionLineBusinessRules(
			MaintenanceDocument document, String collectionName,
			PersistableBusinessObject line) {

		boolean isValid = true;

		if("personResponsibilities".equals(collectionName)){
			PersonRuleResponsibility pr = (PersonRuleResponsibility)line;
			String name = pr.getPrincipalName();

			if(!personExists(name)){
				isValid &= false;
				this.putFieldError("personResponsibilities", "error.document.personResponsibilities.principleDoesNotExist");
			}
		}else if("groupResponsibilities".equals(collectionName)){
			GroupRuleResponsibility gr = (GroupRuleResponsibility)line;
			if(!groupExists(gr.getNamespaceCode(), gr.getName())){
				isValid &= false;
				this.putFieldError("groupResponsibilities", "error.document.personResponsibilities.groupDoesNotExist");
			}
		}

		return isValid;
	}

	protected boolean personExists(String principalName){
		boolean bRet = false;
		try{
			KEWServiceLocator.getIdentityHelperService().getIdForPrincipalName(principalName);
			bRet = true;
		}catch(Exception ex){
			bRet = false;
			ex.printStackTrace();
		}

		return bRet;
	}

	protected boolean groupExists(String namespaceCode, String groupName){
		boolean bRet = false;
		try{
			KEWServiceLocator.getIdentityHelperService().getGroupByName(namespaceCode, groupName);
			bRet = true;
		}catch(Exception ex){
			bRet = false;
			ex.printStackTrace();
		}
		return bRet;
	}

	protected boolean populateErrorMap(RuleBaseValues ruleBaseValues){

		boolean isValid = true;

		if (getDocumentTypeService().findByName(ruleBaseValues.getDocTypeName()) == null) {
            this.putFieldError("docTypeName", "doctype.documenttypeservice.doctypename.required");
            isValid &= false;
        }
        if (ruleBaseValues.getActiveInd() == null) {
        	this.putFieldError("activeInd", "routetemplate.ruleservice.activeind.required");
        	isValid &= false;
        }
        if(ruleBaseValues.getName() != null){
        	if(ruleExists(ruleBaseValues.getName())){
        		this.putFieldError("name", "routetemplate.ruleservice.name.unique");
            	isValid &= false;
        	}
        }
        if (ruleBaseValues.getDescription() == null || ruleBaseValues.getDescription().equals("")) {
        	this.putFieldError("description", "routetemplate.ruleservice.description.required");
        	isValid &= false;
        }
        // one of these has a value
        if(ruleBaseValues.getToDate() != null || ruleBaseValues.getFromDate() != null){
        	if (ruleBaseValues.getToDate() == null || ruleBaseValues.getToDate().before(ruleBaseValues.getFromDate())) {
    			this.putFieldError("toDate", "error.document.maintainableItems.toDate");
    			isValid &= false;
            }
        }

		if (ruleBaseValues.getIgnorePrevious() == null) {
			this.putFieldError("ignorePrevious", "routetemplate.ruleservice.ignoreprevious.required");
			isValid &= false;
        }


		if(!setRuleAttributeErrors(ruleBaseValues)){
			isValid &= false;
		}

		// This doesn't map directly to a single field. It's either the person or the group tab
        if (ruleBaseValues.getResponsibilities().isEmpty()) {
        	this.putFieldError("A responsibility is required", "routetemplate.ruleservice.responsibility.required");
        	isValid &= false;
        } else {
            for (Iterator<RuleResponsibility> iter = ruleBaseValues.getResponsibilities().iterator(); iter.hasNext();) {
                RuleResponsibility responsibility = iter.next();
                if (responsibility.getRuleResponsibilityName() != null && KEWConstants.RULE_RESPONSIBILITY_GROUP_ID.equals(responsibility.getRuleResponsibilityType())) {
                    if (getIdentityManagementService().getGroup(responsibility.getRuleResponsibilityName()) == null) {
                    	this.putFieldError("Groups", "routetemplate.ruleservice.workgroup.invalid");
                    	isValid &= false;
                    }
                } else if (responsibility.getPrincipal() == null && responsibility.getRole() == null) {
                	this.putFieldError("Persons", "routetemplate.ruleservice.user.invalid");
                	isValid &= false;
                }
            }
        }

        return isValid;
	}

	protected boolean ruleExists(String ruleName){
		boolean bRet = false;

		RuleBaseValues tmp = KEWServiceLocator.getRuleService().getRuleByName(ruleName);

		if(tmp != null)
			bRet = true;

		return bRet;
	}

	protected DocumentTypeService getDocumentTypeService() {
        return (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
    }
	/**
	 * Returns the new RuleBaseValues business object.
	 */
	protected RuleBaseValues getNewRule(MaintenanceDocument document) {
		return (RuleBaseValues)document.getNewMaintainableObject().getBusinessObject();
	}

	protected boolean setRuleAttributeErrors(RuleBaseValues rule){

		boolean isValid = true;

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
			List<WorkflowServiceErrorImpl> attValidationErrors = null;
			try{
				attValidationErrors = workflowAttribute.validateRuleData(parameterMap);
			}catch(Exception ex){
				isValid = false;
				this.putFieldError("RuleAttributes", "routetemplate.xmlattribute.required.error");
				ex.printStackTrace();
			}
			// TODO hook validation of rule data into PreRules
			 if (attValidationErrors != null && !attValidationErrors.isEmpty()) {
				 isValid = false;
				 for(WorkflowServiceErrorImpl error: attValidationErrors){
					 this.putFieldError("RuleAttributes", error.getKey(), error.getArg1());
				 }
			 }
		}
		return isValid;

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

}
