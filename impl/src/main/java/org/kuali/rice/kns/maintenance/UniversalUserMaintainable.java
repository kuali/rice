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
package org.kuali.rice.kns.maintenance;

import java.util.Map;

import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.user.AuthenticationUserId;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.UniversalUserService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;


public class UniversalUserMaintainable extends KualiMaintainableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UniversalUserMaintainable.class);

    private static KualiConfigurationService configService;
    private static String userEditWorkgroupName;
    private static boolean usersMaintainedByKuali;
    private static DocumentService documentService;
    private static UniversalUserService universalUserService;
    
    @Override
    public void saveBusinessObject() {
        initStatics();
        // only attempt to save the UU object if the initiator is in the appropriate group
        // get the group name that we need here
        UniversalUser initiator = null;
        try {
            Document doc = documentService.getByDocumentHeaderId( documentNumber );
            if ( doc != null ) {
                String initiatorId = doc.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
                if ( initiatorId != null ) {
                    initiator = universalUserService.getUniversalUserByAuthenticationUserId(initiatorId);
                }
            }
        } catch ( WorkflowException ex ) {
            LOG.error( "unable to get initiator ID for document " + documentNumber, ex );
        } catch ( UserNotFoundException ex ) {
            LOG.error( "unable to get initator UniversalUser for for document " + documentNumber, ex );
        }
        // only save the primary UniversalUser business object if the conditions are met
        if ( usersMaintainedByKuali && initiator != null && initiator.isMember( userEditWorkgroupName ) ) {        
            super.saveBusinessObject();
        }
    }
    
    private void initStatics() {
        if ( documentService == null ) { // they're all set at the same time, so only need one check
            configService = KNSServiceLocator.getKualiConfigurationService();
            universalUserService = KNSServiceLocator.getUniversalUserService();
            documentService = KNSServiceLocator.getDocumentService();
            userEditWorkgroupName = configService.getParameterValue(KNSConstants.KNS_NAMESPACE, KNSConstants.DetailTypes.UNIVERSAL_USER_DETAIL_TYPE, KNSConstants.CoreApcParms.UNIVERSAL_USER_EDIT_WORKGROUP);
            // check whether users are editable within Kuali
            usersMaintainedByKuali = configService.getPropertyAsBoolean( KNSConstants.MAINTAIN_USERS_LOCALLY_KEY );
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.Maintainable#populateBusinessObject(java.util.Map)
     */
    public Map populateBusinessObject(Map fieldValues) {
        // need to make sure that the UUID is populated first for later fields
        if ( fieldValues.containsKey( KNSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER ) ) {
            ((UniversalUser)getBusinessObject()).setPersonUniversalIdentifier( (String)fieldValues.get( KNSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER ) );
        }
        return super.populateBusinessObject( fieldValues );
    }
    
    /**
     * @see org.kuali.rice.kns.maintenance.Maintainable#processAfterCopy()
     */
    @Override
    public void processAfterCopy( MaintenanceDocument document, Map<String,String[]> parameters ) {
        UniversalUser user = (UniversalUser) businessObject;
        user.setPersonUserIdentifier("");
        super.processAfterCopy( document, parameters );
    }
}
