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
package org.kuali.rice.kns.document.authorization;

import java.util.Map;
import java.util.Set;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;

/**
 * The DocumentAuthorizer class associated with a given Document is used to dynamically determine what editing mode and what actions
 * are allowed for a given user on a given document instance.
 * 
 * 
 */
public interface DocumentAuthorizer {
	
    public Set getDocumentActions(Document document, Person user, Set<String> documentActions);
    public boolean canReceiveAdHoc(Document document, Person user, String actionRequestCode);
    public boolean canInitiate(String documentTypeName, Person user);

    /**
     *  Perform an authorization check on the given document.  This is a helper method which includes the needed permission details
     *  and role qualifiers automatically before calling the IdentityManagementService.
     *  
     *  @see org.kuali.rice.kim.service.IdentityManagementService#isAuthorized(String, String, String, AttributeSet, AttributeSet)
     */
    boolean isAuthorized( Document document, String namespaceCode, String permissionName, String principalId );
    
    /**
     *  Perform an authorization check on the given document.  This is a helper method which includes the needed permission details
     *  and role qualifiers automatically before calling the IdentityManagementService.
     *  
     *  @see org.kuali.rice.kim.service.IdentityManagementService#isAuthorizedByTemplateName(String, String, String, AttributeSet, AttributeSet)
     */
    boolean isAuthorizedByTemplate( Document document, String namespaceCode, String permissionTemplateName, String principalId );

    /**
     * Same as {@link #isAuthorized(Document, String, String, String)} except that it takes additional permission details
     * and/or role qualifiers which will be merged (overwriting) the base document qualifiers and details.
     * 
     * This can be used for checking row-level permissions where the information that needs to be passed may vary
     * within a single document.
     * 
     * nulls may be passed in for the attribute sets, in which case the original, document-level attributes will be sent unmodified.
     */
    public boolean isAuthorized( Document document, String namespaceCode, String permissionName, String principalId, Map<String,String> additionalPermissionDetails, Map<String,String> additionalRoleQualifiers );
    
    /**
     * @see #isAuthorized(Document, String, String, String, AttributeSet, AttributeSet)
     */
    public boolean isAuthorizedByTemplate( Document document, String namespaceCode, String permissionTemplateName, String principalId, Map<String,String> additionalPermissionDetails, Map<String,String> additionalRoleQualifiers );
}

