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
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegation;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.web.WebRuleUtils;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;

/**
 * This class is the maintainable implementation for Routing Rules 
 * in KEW (represented by the {@link RuleBaseValues} business object). 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DelegationRoutingRuleMaintainable extends KualiMaintainableImpl {

	private static final String RESPONSIBILITY_ID_PARAM = "ruleCreationValues.responsibilityId";
	
	/**
	 * On creation of a new rule document, we must validate that a rule template and document type are set. 
	 */
	@Override
	public void processAfterNew(MaintenanceDocument document,
			Map<String, String[]> parameters) {
		initializeBusinessObjects(document);
		WebRuleUtils.validateRuleTemplateAndDocumentType(getOldRule(document), getNewRule(document), parameters);
		validateRuleAndResponsibility(document, parameters);
		WebRuleUtils.establishDefaultRuleValues(getNewRule(document));
		getNewRule(document).setRouteHeaderId(new Long(document.getDocumentHeader().getDocumentNumber()));
	}
	
	private void validateRuleAndResponsibility(MaintenanceDocument document, Map<String, String[]> parameters) {
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
			getOldRuleDelegation(document).setResponsibilityId(responsibilityId);
			getNewRuleDelegation(document).setResponsibilityId(responsibilityId);
		}
		
	}
	
	/**
	 * Creates the initial structure of the new business object so that it can be properly
	 * populated with non-null object references.
	 */
	private void initializeBusinessObjects(MaintenanceDocument document) {
		RuleDelegation oldRuleDelegation = getOldRuleDelegation(document);
		RuleDelegation newRuleDelegation = getNewRuleDelegation(document);
		if (oldRuleDelegation.getDelegationRuleBaseValues() == null) {
			oldRuleDelegation.setDelegationRuleBaseValues(new RuleBaseValues());
		}
		if (newRuleDelegation.getDelegationRuleBaseValues() == null) {
			newRuleDelegation.setDelegationRuleBaseValues(new RuleBaseValues());
		}
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
	
	protected RuleDelegation getNewRuleDelegation(MaintenanceDocument document) {
		return (RuleDelegation)document.getNewMaintainableObject().getBusinessObject();
	}
	
	protected RuleDelegation getOldRuleDelegation(MaintenanceDocument document) {
		return (RuleDelegation)document.getOldMaintainableObject().getBusinessObject();
	}

	protected RuleDelegation getThisRuleDelegation() {
		return (RuleDelegation)getBusinessObject();
	}

	protected RuleBaseValues getNewRule(MaintenanceDocument document) {
		return getNewRuleDelegation(document).getDelegationRuleBaseValues();
	}

	protected RuleBaseValues getOldRule(MaintenanceDocument document) {
		return getOldRuleDelegation(document).getDelegationRuleBaseValues();
	}

	protected RuleBaseValues getThisRule() {
		return getThisRuleDelegation().getDelegationRuleBaseValues();
	}
	
	

	
	
}
