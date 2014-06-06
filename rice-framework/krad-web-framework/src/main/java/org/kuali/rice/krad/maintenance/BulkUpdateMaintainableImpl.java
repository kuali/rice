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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@link BulkUpdateMaintainable}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class BulkUpdateMaintainableImpl extends MaintainableImpl implements BulkUpdateMaintainable {
    private static final long serialVersionUID = 6656390440709425848L;

    /**
     * The bulk update maintenance document including the data objects to be updated by this bulk update maintenance
     * document needs to be persistable.
     *
     * @see BulkUpdateMaintainable#isPersistable
     */
    @Override
    public boolean isPersistable() {
        if (StringUtils.isBlank(getDocumentNumber())) {
            return false;
        }

        BulkUpdateMaintenanceDataObject dataObject = (BulkUpdateMaintenanceDataObject) getDataObject();
        List<?> targetUpdateDataObjects = dataObject.getTargetUpdateDataObjects();

        for (Object targetUpdateDataObject : targetUpdateDataObjects) {
            if (!getLegacyDataAdapter().hasPrimaryKeyFieldValues(targetUpdateDataObject)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @see BulkUpdateMaintainable#generateUpdatableObjects()
     */
    @Override
    public List<Object> generateUpdatableObjects() {
        List<Object> updatableObjects = new ArrayList<Object>();

        BulkUpdateMaintenanceDataObject bulkUpdateMaintenanceDataObject = (BulkUpdateMaintenanceDataObject) getDataObject();
        List<?> targetUpdateDataObjects = bulkUpdateMaintenanceDataObject.getTargetUpdateDataObjects();

        for (Object targetUpdateDataObject : targetUpdateDataObjects) {
            Map<String, Object> primaryKeys = getDataObjectService().wrap(targetUpdateDataObject).getPrimaryKeyValues();
            Object updatableObject = getDataObjectService().findUnique(targetUpdateDataObject.getClass(),
                    QueryByCriteria.Builder.andAttributes(primaryKeys).build());

            for (Map.Entry<String, ?> updateFieldValue : bulkUpdateMaintenanceDataObject.getUpdateFieldValues().entrySet()) {
                String propertyName = updateFieldValue.getKey();
                Object propertyValue = updateFieldValue.getValue();

                if (ObjectPropertyUtils.isWritableProperty(updatableObject, propertyName)) {
                    ObjectPropertyUtils.setPropertyValue(updatableObject, propertyName, propertyValue);
                    updatableObjects.add(updatableObject);
                }
            }
        }

        return updatableObjects;
    }

    /**
     * @see Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        List<MaintenanceLock> maintenanceLocks = super.generateMaintenanceLocks();

        BulkUpdateMaintenanceDataObject dataObject = (BulkUpdateMaintenanceDataObject) getDataObject();

        for (Object targetUpdateDataObjects : dataObject.getTargetUpdateDataObjects()) {
            String documentTypeName = getDocumentDictionaryService().getMaintenanceDocumentTypeName(targetUpdateDataObjects.getClass());
            maintenanceLocks.addAll(generateMaintenanceLocks(getDocumentNumber(), documentTypeName, targetUpdateDataObjects.getClass(), targetUpdateDataObjects));
        }

        return maintenanceLocks;
    }

    /**
     * @see Maintainable#saveDataObject()
     */
    @Override
    public void saveDataObject() {
        for (Object updateableObject : generateUpdatableObjects()) {
            getLegacyDataAdapter().linkAndSave(updateableObject);
        }
    }

}
