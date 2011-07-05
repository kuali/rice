/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kew.document;

import java.util.ArrayList;
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
import org.kuali.rice.kew.rule.web.WebRuleUtils;
import org.kuali.rice.kew.rule.xmlrouting.GenericXMLRuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.KEWPropertyConstants;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;

/**
 * This is a description of what this class does - Garey don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RoutingRuleMaintainableBusRule extends MaintenanceDocumentRuleBase {

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.krad.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.krad.document.MaintenanceDocument)
	 */
	@Override
	protected boolean processCustomSaveDocumentBusinessRules(
			MaintenanceDocument document) {

		boolean isValid = true;

		RuleBaseValues ruleBaseValues = this.getRuleBaseValues(document);
		RuleBaseValues oldRuleBaseValues = this.getOldRuleBaseValues(document);
		
		if (oldRuleBaseValues != null) {
			ruleBaseValues.setPreviousVersionId(oldRuleBaseValues.getRuleBaseValuesId());
        }
		isValid &= this.populateErrorMap(ruleBaseValues);


		return isValid;
	}

	protected RuleBaseValues getRuleBaseValues(MaintenanceDocument document){
		return (RuleBaseValues)document.getNewMaintainableObject().getBusinessObject();
	}
	
	protected RuleBaseValues getOldRuleBaseValues(MaintenanceDocument document){
		return (RuleBaseValues)document.getOldMaintainableObject().getBusinessObject();
	}
	

	protected void populateErrorMap(Map<String,String> errorMap){
		for(Map.Entry<String, String> entry : errorMap.entrySet()){
			this.putFieldError(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * This overridden method ...
	 *
	 * @see org.kuali.rice.krad.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.krad.document.MaintenanceDocument, java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
	 */
	@Override
	public boolean processCustomAddCollectionLineBusinessRules(
			MaintenanceDocument document, String collectionName,
			PersistableBusinessObject line) {

		boolean isValid = true;

		if(getPersonSectionName().equals(collectionName)){
			PersonRuleResponsibility pr = (PersonRuleResponsibility)line;
			String name = pr.getPrincipalName();

			if(!personExists(name)){
				isValid &= false;
				this.putFieldError(getPersonSectionName(), "error.document.personResponsibilities.principleDoesNotExist");
			}
		}else if(getGroupSectionName().equals(collectionName)){
			GroupRuleResponsibility gr = (GroupRuleResponsibility)line;
			if(!groupExists(gr.getNamespaceCode(), gr.getName())){
				isValid &= false;
				this.putFieldError(getGroupSectionName(), "error.document.personResponsibilities.groupDoesNotExist");
			}
		}

		return isValid;
	}

	protected String getPersonSectionName(){
		return KEWPropertyConstants.PERSON_RESP_SECTION;
	}
	protected String getGroupSectionName(){
		return KEWPropertyConstants.GROUP_RESP_SECTION;
	}

	protected boolean personExists(String principalName){
		boolean bRet = false;
		try{
			KEWServiceLocator.getIdentityHelperService().getIdForPrincipalName(principalName);
			bRet = true;
		}catch(Exception ex){
			bRet = false;
			//ex.printStackTrace();
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
			//ex.printStackTrace();
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
        	if(ruleExists(ruleBaseValues)){
        		this.putFieldError("name", "routetemplate.ruleservice.name.unique");
            	isValid &= false;
        	}
        }

        /*
         * Logic: If both from and to dates exist, make sure toDate is after fromDate
         */
        if(ruleBaseValues.getToDate() != null && ruleBaseValues.getFromDate() != null){
        	if (ruleBaseValues.getToDate().before(ruleBaseValues.getFromDate())) {
    			this.putFieldError("toDate", "error.document.maintainableItems.toDate");
    			isValid &= false;
            }
        }

		if (ruleBaseValues.getForceAction() == null) {
			this.putFieldError("forceAction", "routetemplate.ruleservice.forceAction.required");
			isValid &= false;
        }


		if(!setRuleAttributeErrors(ruleBaseValues)){
			isValid &= false;
		}

		// This doesn't map directly to a single field. It's either the person or the group tab
        if (ruleBaseValues.getResponsibilities().isEmpty()) {
        	this.putFieldError("Responsibilities", "error.document.responsibility.required");
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

	protected boolean ruleExists(RuleBaseValues rule){
		boolean bRet = false;

		RuleBaseValues tmp = KEWServiceLocator.getRuleService().getRuleByName(rule.getName());

		if(tmp != null) {
		    if ((rule.getPreviousVersionId() == null) 
		         || (rule.getPreviousVersionId() != null
		            && !rule.getPreviousVersionId().equals(tmp.getRuleBaseValuesId()))) {
			    bRet = true;
		    }
		}

		return bRet;
	}

	protected DocumentTypeService getDocumentTypeService() {
        return (DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE);
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

			Map<String, String> parameterMap = WebRuleUtils.getFieldMapForRuleTemplateAttribute(rule, ruleTemplateAttribute);

			// validate rule data populates the rule extension values for us
			List<WorkflowServiceErrorImpl> attValidationErrors = null;
			try{
				attValidationErrors = workflowAttribute.validateRuleData(parameterMap);
			}catch(Exception ex){
				isValid = false;
				this.putFieldError("RuleAttributes", "routetemplate.xmlattribute.required.error");
				//ex.printStackTrace();
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

}
