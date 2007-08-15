/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.rules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.RicePropertyConstants;
import org.kuali.core.KualiModule;
import org.kuali.core.bo.user.KualiModuleUser;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.datadictionary.control.ControlDefinition;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.util.FieldUtils;
import org.kuali.core.web.ui.Field;
import org.kuali.rice.KNSServiceLocator;

public class UniversalUserPreRules extends PreRulesContinuationBase {

    public boolean doRules(Document document) {

        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;

        UniversalUser newUser = (UniversalUser) maintenanceDocument.getNewMaintainableObject().getBusinessObject();

        //KULCOA-1164: If FULLNAME is blank, replace it with "Last,First"
        Field nameField = FieldUtils.getPropertyField( UniversalUser.class, RicePropertyConstants.PERSON_NAME, false );
        ControlDefinition controlDef = KNSServiceLocator.getDataDictionaryService().getAttributeControlDefinition(UniversalUser.class, RicePropertyConstants.PERSON_NAME );
        // KULCOA-3104 - always set the display name to match the first/last if the field is not visible or editable 
        if ( controlDef.isHidden() || nameField.isReadOnly() || StringUtils.isBlank( newUser.getPersonName() ) ){
            if ( !StringUtils.isBlank( newUser.getPersonFirstName() ) && !StringUtils.isBlank( newUser.getPersonLastName() ) ){
                if ( !StringUtils.isBlank( newUser.getPersonMiddleName() ) ) {
                    newUser.setPersonName( newUser.getPersonLastName()+"," +newUser.getPersonFirstName() + " " + newUser.getPersonMiddleName() );
                } else {
                    newUser.setPersonName( newUser.getPersonLastName()+"," +newUser.getPersonFirstName() );
                }
            }
        }
        boolean success = true;
        List<KualiModule> modules = KNSServiceLocator.getKualiModuleService().getInstalledModules();
        PreRulesContinuationBase rule = null;
        for ( KualiModule module : modules ) {
            rule = (PreRulesContinuationBase) module.getModuleUserPreRules();
            if ( rule != null ) {
                success &= rule.doRules( document );
            }
        }
        
        // determine what modules user data has changed for and set that info on the new maintainable for use in workflow
        // see TODO associated with this property in universal user
        deriveChangedModuleCodes(maintenanceDocument);
        
        return success;
    }
    
    private void deriveChangedModuleCodes(MaintenanceDocument document) {
        Set<String> changedModuleCodes = new HashSet<String>();
        UniversalUser newUser = (UniversalUser) document.getNewMaintainableObject().getBusinessObject();
        for (KualiModuleUser newModuleUser : newUser.getModuleUsers().values()) {
            if (newModuleUser.isModified(document.isEdit() ? (UniversalUser) document.getOldMaintainableObject().getBusinessObject(): null, newUser)) {
                changedModuleCodes.add(KNSServiceLocator.getKualiModuleService().getModule(newModuleUser.getModuleId()).getModuleCode());
            }
        }
        newUser.setChangedModuleCodes(changedModuleCodes);
    }
}
