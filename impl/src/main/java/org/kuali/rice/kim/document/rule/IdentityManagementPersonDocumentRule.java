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
package org.kuali.rice.kim.document.rule;

import java.util.List;

import org.kuali.rice.kim.bo.ui.PersonDocumentAffiliation;
import org.kuali.rice.kim.bo.ui.PersonDocumentBoDefaultBase;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmploymentInfo;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.TransactionalDocumentRuleBase;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.RiceKeyConstants;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class IdentityManagementPersonDocumentRule extends TransactionalDocumentRuleBase {

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        if (!(document instanceof IdentityManagementPersonDocument)) {
            return false;
        }

        IdentityManagementPersonDocument personDoc = (IdentityManagementPersonDocument)document;
        boolean valid = true;

        GlobalVariables.getErrorMap().addToErrorPath("document");

        //KNSServiceLocator.getDictionaryValidationService().validateDocument(document);
        getDictionaryValidationService().validateDocumentAndUpdatableReferencesRecursively(document, getMaxDictionaryValidationDepth(), true, false);
        valid &= checkMultipleDefault (personDoc.getAffiliations(), "affiliations");
        valid &= checkMultipleDefault (personDoc.getNames(), "names");
        valid &= checkMultipleDefault (personDoc.getAddrs(), "addrs");
        valid &= checkMultipleDefault (personDoc.getPhones(), "phones");
        valid &= checkMultipleDefault (personDoc.getEmails(), "emails");
        valid &= checkPeimaryEmploymentInfo (personDoc.getAffiliations());

        GlobalVariables.getErrorMap().removeFromErrorPath("document");

        return valid;
    }
    
    
    
    private boolean checkMultipleDefault (List <? extends PersonDocumentBoDefaultBase> boList, String listName) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
    	boolean valid = true;
    	boolean isDefaultSet = false;
    	int i = 0;
    	for (PersonDocumentBoDefaultBase item : boList) {
     		if (item.isDflt()) {
     			if (isDefaultSet) {
                    errorMap.putError(listName+"[" + i + "].dflt",RiceKeyConstants.ERROR_MULTIPLE_DEFAULT_SELETION);
     				valid = false;
     			} else {
     				isDefaultSet = true;
     			}
     		}
     		i++;
    	}
    	return valid;
    }
    
    private boolean checkPeimaryEmploymentInfo (List <PersonDocumentAffiliation> affiliations) {
        ErrorMap errorMap = GlobalVariables.getErrorMap();
    	boolean valid = true;
    	int i = 0;
    	for (PersonDocumentAffiliation affiliation : affiliations) {
    		int j = 0;
        	boolean isPrimarySet = false;
    		for (PersonDocumentEmploymentInfo empInfo : affiliation.getEmpInfos()) {
	     		if (empInfo.isPrimary()) {
	     			if (isPrimarySet) {
	     				// primary per principal or primary per affiliation ?
	                    errorMap.putError("affiliations[" + i + "].empInfos["+ j +"].primary",RiceKeyConstants.ERROR_MULTIPLE_PRIMARY_EMPLOYMENT);
	     				valid = false;
	     			} else {
	     				isPrimarySet = true;
	     			}
	     			j++;
	     		}
    		}
     		i++;
    	}
    	return valid;
    }
    
    
}
