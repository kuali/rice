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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Default implementation of {@link BulkUpdateMaintenanceDataObject}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BulkUpdateMaintenanceDataObjectBase implements BulkUpdateMaintenanceDataObject {
    private static final long serialVersionUID = -2703891255485833554L;

    private Map<String, ?> updateFieldValues = new HashMap<String, Object>();

    private List<?> targetUpdateDataObjects = new ArrayList<Object>();

    /**
     * @see BulkUpdateMaintenanceDataObjectBase#getUpdateFieldValues()
     */
    @Override
    public Map<String, ?> getUpdateFieldValues() {
        return updateFieldValues;
    }

    /**
     * @see BulkUpdateMaintenanceDataObjectBase#getTargetUpdateDataObjects()
     */
    @Override
    public List<?> getTargetUpdateDataObjects() {
        return targetUpdateDataObjects;
    }

}
