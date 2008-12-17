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
	
     /**
     * @param document
     * @param user
     * @return Map with keys AuthorizationConstants.EditMode value (String) which indicates what operations the user is currently
     *         allowed to take on that document.
     */
	@Deprecated
    public Map getEditMode(Document document, Person user);
 
    /**
     * @param document
     * @param user
     * @return Set of actions are permitted the given user on the given document
     */
    public Set getDocumentActions(Document document, Person user, Set<String> documentActions);
    
    @Deprecated
    public DocumentActionFlags getDocumentActionFlags(Document document, Person user);
    

    /**
     * @param documentTypeName
     * @param user
     * @return true if the given user is allowed to initiate documents of the given document type
     */
    public void canInitiate(String documentTypeName, Person user);
    
    /**
     * @param documentTypeName
     * @param user
     * @returns boolean indicating whether a user can copy a document
     */
    @Deprecated
    public boolean canCopy(String documentTypeName, Person user);

    /**
     * 
     * @param attachmentTypeName
     * @param document
     * @param user
     * @return
     */
    public boolean canViewAttachment(String attachmentTypeName, Document document, Person user);
    
    /**
     * @param document
     * @param user
     * @return true if the given user is allowed to open the document
     */
    public boolean canOpen(Document document, Person user);
    
    /**
     * @param document
     * @param user
     * @return true if the given user is allowed to edit the document
     */
    public boolean canEdit(Document document, Person user);
    
    /**
     * @param document
     * @param user
     * @return true if the given user is allowed to copy the document
     */
    public boolean canCopy(Document document, Person user);
    
    /**
     * @param document
     * @param user
     * @return true if the given user is allowed to cancel the document
     */
    public boolean canCancel(Document document, Person user);
    
    /**
     * @param document
     * @param user
     * @return true if the given user is allowed to Route the document
     */
    public boolean canRoute(Document document, Person user);
    
    /**
     * @param document
     * @param user
     * @return true if the given user is allowed to save the document
     */
    public boolean canSave(Document document, Person user);
    
    /**
     * @param document
     * @param user
     * @return true if the given user is allowed to approve the document
     */
    public boolean canBlanketApprove(Document document, Person user); 
    
    /**
     * @param document
     * @param user
     * @param actionRequestCode
     * @return true if the given user is allowed to receive ad hoc
     */
    public boolean canReceiveAdHoc(Document document, Person user, String actionRequestCode);
    
    /**
     * @param document
     * @param user
     * @return true if the given user is allowed to approve the document
     */
    public boolean canApprove(Document document, Person user); 
    
    /**
     * @param document
     * @param user
     * @return true if the given user is allowed to clear FYI
     */
    public boolean canClearFYI(Document document, Person user);
    
    /**
     * @param document
     * @param user
     * @return true if the given user is allowed to acknowledge
     */
    public boolean canAcknowledge(Document document, Person user); 
    
    /**
     * @param document
     * @param user
     * @return true if the given user is allowed to complete the document
     */
    public boolean canComplete(Document document, Person user ); 
    
    /**
     * @param document
     * @param user
     * @return true if the given user is allowed to disapprove the document
     */
    public boolean canDisapprove(Document document, Person user); 
    
    
    /**
     * @param document
     * @param user
     * @return true if the given user is allowed to annotate the document
     */
    public boolean canAnnotate(Document document, Person user); 
    
       
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

