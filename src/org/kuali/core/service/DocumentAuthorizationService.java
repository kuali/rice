/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.service;

import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentAuthorizer;

/**
 * Service used to control user access to classes which implement AuthorizationTarget
 * 
 * 
 */
public interface DocumentAuthorizationService {

    /**
     * @param documentTypeName
     * @return DocumentAuthorizer for the given documentType
     */
    public DocumentAuthorizer getDocumentAuthorizer(String documentTypeName);

    /**
     * @param document
     * @return DocumentAuthorizer for the given document's documentType
     */
    public DocumentAuthorizer getDocumentAuthorizer(Document document);
}
