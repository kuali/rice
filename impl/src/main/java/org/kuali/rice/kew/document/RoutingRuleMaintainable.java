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

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.rule.GroupRuleResponsibility;
import org.kuali.rice.kew.rule.PersonRuleResponsibility;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.group.KimGroup;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;

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
    	RuleBaseValues rule = (RuleBaseValues)getBusinessObject();
    	translateResponsibilitiesBeforeSave(rule);
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
		
		RuleBaseValues newRule = getNewRule(document);
		newRule.setRuleTemplate(ruleTemplate);
		newRule.setRuleTemplateId(ruleTemplate.getRuleTemplateId());
		newRule.setDocTypeName(documentTypeName);
		
	}
	
	protected void translateResponsibilitiesBeforeSave(RuleBaseValues rule) {
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
	
	/**
	 * Returns the new RuleBaseValues business object.
	 */
	protected RuleBaseValues getNewRule(MaintenanceDocument document) {
		return (RuleBaseValues)document.getNewMaintainableObject().getBusinessObject();
	}
    
    

}
