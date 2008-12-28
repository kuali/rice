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

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;

/**
 * Extension to DocumentAuthorizer for Maintenance Document specific methods
 * 
 * 
 */
public interface MaintenanceDocumentAuthorizer extends DocumentAuthorizer {

	/**
     * 
     * This method returns adds restrictions based on the document and the logged-in user
     * 
     * @param document the document
     * @param user the logged-in user
     * @return MaintenanceDocumentAuthorizations
     * 
     */
    public void addMaintenanceDocumentRestrictions(MaintenanceDocumentAuthorizations auths, MaintenanceDocument document, Person user);
    
    /**
     * @param boClass
     * @param user
     * @returns boolean indicating whether a user can create a new record
     */
    public boolean canCreate(Class boClass, Person user);
    
    /**
     * @param boClass
     * @param primaryKeys
     * @param user
     * @returns boolean indicating whether a user can maintain existing record
     */
    public boolean canMaintain(Class boClass, Map primaryKeys, Person user);
    
    /**
     * @param document
     * @param primaryKeys
     * @param user
     * @returns boolean indicating whether a user can create new record or maintain existing record
     */
    public boolean canCreateOrMaintain(Class boClass, Map primaryKeys, Person user);
    
}

