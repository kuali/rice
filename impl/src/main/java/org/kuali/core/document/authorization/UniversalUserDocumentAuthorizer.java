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
package org.kuali.core.document.authorization;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.authorization.UniversalUserAuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;

/**
 * Universal User specific authorization rules.
 * 
 * 
 */
public class UniversalUserDocumentAuthorizer extends MaintenanceDocumentAuthorizerBase {

    //private static final Logger LOG = Logger.getLogger(UniversalUserDocumentAuthorizer.class);
    
    private transient static KualiConfigurationService configService;
    private transient static String userEditWorkgroupName;
    private transient static boolean usersMaintainedByKuali;
    private transient static boolean passwordEditingEnabled;
    
    /**
     * Constructs a UniversalUserDocumentAuthorizer.
     */
    public UniversalUserDocumentAuthorizer() {
        super();
    }

    /**
     * @see org.kuali.core.document.MaintenanceDocumentAuthorizerBase#getEditMode(org.kuali.core.document.Document, org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public Map getEditMode(Document document, UniversalUser user) {
        Map editModes = new HashMap();
        if (!(document.getDocumentHeader().getWorkflowDocument().stateIsInitiated() || document.getDocumentHeader().getWorkflowDocument().stateIsSaved())) {
            editModes.put(UniversalUserAuthorizationConstants.MaintenanceEditMode.VIEW_ONLY, "TRUE");
        } else {
            editModes = super.getEditMode(document, user);            
        }
        initStatics();
        
        // check for ssn edit mode
        if (user.isMember( userEditWorkgroupName ) ) {
            editModes.put(UniversalUserAuthorizationConstants.MaintenanceEditMode.SSN_EDIT_ENTRY, "TRUE");
        }
        
        return editModes;
    }

    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, UniversalUser user) {
        MaintenanceDocumentAuthorizations auths = new MaintenanceDocumentAuthorizations();
        initStatics();

        // prevent users not in the UU edit group from changing base UU properties
        if ( !(usersMaintainedByKuali && user.isMember( userEditWorkgroupName )) ) {
            auths.addReadonlyAuthField( KNSPropertyConstants.PERSON_USER_IDENTIFIER );
            auths.addReadonlyAuthField( KNSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER );
            auths.addHiddenAuthField( "personTaxIdentifier" );
            auths.addHiddenAuthField( "personTaxIdentifierTypeCode" );
            auths.addReadonlyAuthField( KNSPropertyConstants.PERSON_NAME );
            auths.addReadonlyAuthField( KNSPropertyConstants.CAMPUS_CODE );
            auths.addReadonlyAuthField( "primaryDepartmentCode" );
            auths.addHiddenAuthField( "personPayrollIdentifier" );
            auths.addReadonlyAuthField( KNSPropertyConstants.EMPLOYEE_STATUS_CODE );
            auths.addReadonlyAuthField( KNSPropertyConstants.EMPLOYEE_TYPE_CODE );
            auths.addReadonlyAuthField( "student" );
            auths.addReadonlyAuthField( "staff" );
            auths.addReadonlyAuthField( "faculty" );
            auths.addReadonlyAuthField( "affiliate" );
            auths.addHiddenAuthField( KNSPropertyConstants.PERSON_FIRST_NAME );
            auths.addHiddenAuthField( KNSPropertyConstants.PERSON_LAST_NAME );
            auths.addHiddenAuthField( "personMiddleName" );
            auths.addHiddenAuthField( KNSPropertyConstants.PERSON_LOCAL_PHONE_NUMBER );
            auths.addHiddenAuthField( KNSPropertyConstants.PERSON_CAMPUS_ADDRESS );
            auths.addHiddenAuthField( KNSPropertyConstants.PERSON_EMAIL_ADDRESS );
            auths.addHiddenAuthField( KNSPropertyConstants.PERSON_BASE_SALARY_AMOUNT );
            auths.addHiddenAuthField( "financialSystemsEncryptedPasswordText" );
        } else {
            if ( !passwordEditingEnabled ) {
                auths.addHiddenAuthField( "financialSystemsEncryptedPasswordText" );
            }
        }

        return auths;
    }
   
    private void initStatics() {
        if ( configService == null ) {
            configService = KNSServiceLocator.getKualiConfigurationService();
        }
        // get the group name that we need here
        if ( userEditWorkgroupName == null ) {
            userEditWorkgroupName = configService.getParameterValue(KNSConstants.KNS_NAMESPACE, KNSConstants.DetailTypes.UNIVERSAL_USER_DETAIL_TYPE, KNSConstants.CoreApcParms.UNIVERSAL_USER_EDIT_WORKGROUP);
            // check whether users are editable within Kuali
            usersMaintainedByKuali = configService.getPropertyAsBoolean( KNSConstants.MAINTAIN_USERS_LOCALLY_KEY );
            // check whether local CAS is in use
            passwordEditingEnabled = KNSServiceLocator.getWebAuthenticationService().isValidatePassword();
        }
    }
}
