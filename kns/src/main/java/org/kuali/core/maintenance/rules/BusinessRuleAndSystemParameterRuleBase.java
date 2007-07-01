/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.maintenance.rules;

import java.util.List;

import org.kuali.KeyConstants;
import org.kuali.core.bo.BusinessRule;
import org.kuali.core.bo.FinancialSystemParameter;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.lookup.keyvalues.ModuleValuesFinder;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.ui.KeyLabelPair;

public class BusinessRuleAndSystemParameterRuleBase extends MaintenanceDocumentRuleBase {

	ModuleValuesFinder moduleValuesFinder;
	
	public BusinessRuleAndSystemParameterRuleBase() {
		super();
        moduleValuesFinder = new ModuleValuesFinder();
	}
	
	@Override
	protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
		boolean valid = super.processCustomSaveDocumentBusinessRules(document);
		String theModuleCodeEntered = null;
		if (document.getDocumentBusinessObject() instanceof BusinessRule) {
            theModuleCodeEntered = ((BusinessRule)document.getDocumentBusinessObject()).getModuleCode();
		} 
		else {
		    theModuleCodeEntered = ((FinancialSystemParameter)document.getDocumentBusinessObject()).getModuleCode();
		}
        for (Object moduleKeyLabelPair : moduleValuesFinder.getKeyValues()) {
        	if (((KeyLabelPair)moduleKeyLabelPair).getKey().toString().equalsIgnoreCase(theModuleCodeEntered)) {
        		return valid;
        	}
        }
        //If we reach here, it means we couldn't find the module code, so return an error.
        GlobalVariables.getErrorMap().addToErrorPath("document.newMaintainableObject");
        GlobalVariables.getErrorMap().putError("moduleCode", KeyConstants.ERROR_EXISTENCE, "Module Code");
        return false;
    }
}