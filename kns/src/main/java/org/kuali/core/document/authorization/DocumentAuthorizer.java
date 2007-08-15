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

import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;

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
    public Map getEditMode(Document document, UniversalUser user);

    /**
     * @param document
     * @param user
     * @return DocumentActionFlags instance indicating which actions are permitted the given user on the given document
     */
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user);


    /**
     * @param documentTypeName
     * @param user
     * @return true if the given user is allowed to initiate documents of the given document type
     */
    public void canInitiate(String documentTypeName, UniversalUser user);
    
    /**
     * @param documentTypeName
     * @param user
     * @returns boolean indicating whether a user can copy a document
     */
    public boolean canCopy(String documentTypeName, UniversalUser user);

    /**
     * 
     * @param attachmentTypeName
     * @param document
     * @param user
     * @return
     */
    public boolean canViewAttachment(String attachmentTypeName, Document document, UniversalUser user);
}
