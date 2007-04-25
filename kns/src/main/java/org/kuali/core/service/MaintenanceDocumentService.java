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

import java.util.List;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.MaintenanceLock;

/**
 * This interface defines methods that a Maintenance Document Service must provide.
 * 
 * 
 */
public interface MaintenanceDocumentService {

    /**
     * 
     * This method attempts to find any other active documents that are pending on the same maintenance record.
     * 
     * If any are pending and locked, thereby blocking this document, then the docHeaderId/documentNumber of the blocking
     * locked document is returned.
     * 
     * Otherwise, if nothing is blocking, then null is returned.
     * 
     * @param document - document to test
     * @return A String representing the docHeaderId of any blocking document, or null if none are blocking
     * 
     */
    public String getLockingDocumentId(MaintenanceDocument document);

    /**
     * Retrieves maintenance documents locked by the given bo class name, then materializes the pending changes to objects of the
     * given class.
     * 
     * @param businessObjectClass
     * @return
     */
    public List getPendingObjects(Class businessObjectClass);

    /**
     * This method is here to call the same-named method in the Dao, since the service has access to the Dao, but the caller doesn't.
     * 
     * This method deletes the locks for the given document number.  It is called when the document is final,
     * thus it can be unlocked, or when the locks need to be regenerated (thus they get cleared first).
     * 
     * @param documentNumber - document number whose locks should be deleted
     */
    public void deleteLocks(String documentNumber);

    /**
     * This method is here to call the same-named method in the Dao, since the service has access to the Dao, but the caller doesn't.
     * 
     * This method stores the given list of maintenance locks.  Typically these will all be for the same document.
     * 
     * @param maintenanceLocks - the list of maintenance locks to be stored
     */
    public void storeLocks(List<MaintenanceLock> maintenanceLocks);

}