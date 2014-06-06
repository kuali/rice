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
package org.kuali.rice.krad.maintenance;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Provides contract for the data object of the bulk update maintenance document.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface BulkUpdateMaintenanceDataObject extends Serializable {

    /**
     * Returns the list of fields of the maintenance documents to be updated along with the new value.
     *
     * @return map where the key is the maintenance document field to be updated and the value is the new value of the
     *         field
     */
    Map<String, ?> getUpdateFieldValues();

    /**
     * Returns the list of data objects of the maintenance documents that are to be updated.
     *
     * @return list of data objects to be updated
     */
    List<?> getTargetUpdateDataObjects();
}
