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
package org.kuali.core.document.authorization;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Parameter;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.exceptions.AuthorizationException;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ParameterMaintenanceDocumentAuthorizer extends MaintenanceDocumentAuthorizerBase {

    /**
     * This overridden method ...
     * 
     * @see org.kuali.core.document.authorization.DocumentAuthorizerBase#hasInitiateAuthorization(org.kuali.core.document.Document, org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public boolean hasInitiateAuthorization(Document document, UniversalUser user) {
        boolean hasInitiateAuth = super.hasInitiateAuthorization(document, user);
        
        // user can not initiate if they are not in the workgroup
        if ( hasInitiateAuth ) {
            Parameter parm = (Parameter)((MaintenanceDocument)document).getNewMaintainableObject().getBusinessObject();
            if ( parm != null 
        	    && StringUtils.isNotBlank( parm.getParameterWorkgroupName() ) 
        	    && !user.isMember( parm.getParameterWorkgroupName() ) ) {
            	hasInitiateAuth = false;
            }
        }
        
        return hasInitiateAuth;
    }
    
    /**
     * This overridden method ...
     * 
     * @see org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase#getEditMode(org.kuali.core.document.Document, org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public Map getEditMode(Document document, UniversalUser user) {
        
        Map editModes = super.getEditMode(document, user);        
        if ( document.getDocumentHeader().getWorkflowDocument().stateIsInitiated() && !hasInitiateAuthorization(document, user) ) {
            Parameter parm = (Parameter)((MaintenanceDocument)document).getNewMaintainableObject().getBusinessObject();
       	    throw new AuthorizationException( user.getPersonUserIdentifier(), "edit parameter", parm.getParameterNamespaceCode()+"/"+parm.getParameterDetailTypeCode()+"/" + parm.getParameterName() );
        }
            
        return editModes;
    }
}
