/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.dao;

import java.util.List;

import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.util.LegacyDataFramework;

/**
 * This interface defines basic methods that MaintenanceDocument Dao's must provide
 * @deprecated use new KRAD Data framework {@link org.kuali.rice.krad.data.DataObjectService}
 */
@Deprecated
@LegacyDataFramework
public interface MaintenanceDocumentDao {
    /**
     * 
     * This method looks for a document that is locking the given lockingRepresentation. If one is found, then it
     * retrieves the documentNumber, and returns it.
     * 
     * @param lockingRepresentation - locking representation to check for
     * @param documentNumber - document number to ignore, optional argument
     * @return returns an empty string if no locking document is found, otherwise returns the documentNumber of the locking document
     * 
     */
    public String getLockingDocumentNumber(String lockingRepresentation, String documentNumber);

}
