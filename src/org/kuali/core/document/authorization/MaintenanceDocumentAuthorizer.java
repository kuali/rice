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

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;

/**
 * Extension to DocumentAuthorizer for Maintenance Document specific methods
 * 
 * 
 */
public interface MaintenanceDocumentAuthorizer extends DocumentAuthorizer {

    /**
     * 
     * This method returns the set of authorization restrictions (if any) that apply to this object in this context.
     * 
     * @param document
     * @param user
     * @return MaintenanceDocumentAuthorizations
     * 
     */
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, UniversalUser user);

}
