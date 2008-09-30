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
package org.kuali.rice.kns.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.datadictionary.control.ControlDefinition;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.web.ui.Field;

public class UniversalUserPreRules extends PreRulesContinuationBase {

    public boolean doRules(Document document) {

        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;

        UniversalUser newUser = (UniversalUser) maintenanceDocument.getNewMaintainableObject().getBusinessObject();

        //KULCOA-1164: If FULLNAME is blank, replace it with "Last,First"
        Field nameField = FieldUtils.getPropertyField( UniversalUser.class, KNSPropertyConstants.PERSON_NAME, false );
        ControlDefinition controlDef = KNSServiceLocator.getDataDictionaryService().getAttributeControlDefinition(UniversalUser.class, KNSPropertyConstants.PERSON_NAME );
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
        
        return success;
    }
    
}
