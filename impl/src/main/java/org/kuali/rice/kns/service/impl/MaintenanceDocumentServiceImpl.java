/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kns.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.database.TransactionalNoValidationExceptionRollback;
import org.kuali.rice.kns.dao.MaintenanceDocumentDao;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.MaintenanceDocumentService;

/**
 * This class is the service implementation for the MaintenanceDocument structure. This is the default implementation, that is
 * delivered with Kuali.
 */
@TransactionalNoValidationExceptionRollback
public class MaintenanceDocumentServiceImpl implements MaintenanceDocumentService {

    private MaintenanceDocumentDao maintenanceDocumentDao;

    /**
     * @see org.kuali.rice.kns.service.MaintenanceDocumentService#getLockingDocumentId(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    public String getLockingDocumentId(MaintenanceDocument document) {
        return getLockingDocumentId(document.getNewMaintainableObject(), document.getDocumentNumber());
    }

    /**
     * @see org.kuali.rice.kns.service.MaintenanceDocumentService#getLockingDocumentId(org.kuali.rice.kns.maintenance.Maintainable, java.lang.String)
     */
    public String getLockingDocumentId(Maintainable maintainable, String documentNumber) {
        String lockingDocId = null;
        List<MaintenanceLock> maintenanceLocks = maintainable.generateMaintenanceLocks();
        for (MaintenanceLock maintenanceLock : maintenanceLocks) {
            lockingDocId = maintenanceDocumentDao.getLockingDocumentNumber(maintenanceLock.getLockingRepresentation(),documentNumber);
            if (StringUtils.isNotBlank(lockingDocId)) {
                break;
            }
        }
        return lockingDocId;
    }

//    /**
//     * @see org.kuali.rice.kns.service.MaintenanceDocumentService#getPendingObjects(java.lang.Class)
//     */
//    public List getPendingObjects(Class businessObjectClass) {
//        List pendingObjects = new ArrayList();
//
//        Collection pendingMaintDocs = maintenanceDocumentDao.getPendingDocumentsForClass(businessObjectClass);
//        for (Iterator iter = pendingMaintDocs.iterator(); iter.hasNext();) {
//            MaintenanceDocument maintDoc = (MaintenanceDocument) iter.next();
//            maintDoc.populateMaintainablesFromXmlDocumentContents();
//            pendingObjects.add(maintDoc.getNewMaintainableObject().getBusinessObject());
//        }
//
//        return pendingObjects;
//    }
    
    /**
     * @see org.kuali.rice.kns.service.MaintenanceDocumentService#deleteLocks(String)
     */
    public void deleteLocks(String documentNumber) {
        maintenanceDocumentDao.deleteLocks(documentNumber);
    }

    /**
     * @see org.kuali.rice.kns.service.MaintenanceDocumentService#saveLocks(List)
     */
    public void storeLocks(List<MaintenanceLock> maintenanceLocks) {
        maintenanceDocumentDao.storeLocks(maintenanceLocks);
    }

    /**
     * @return Returns the maintenanceDocumentDao.
     */
    public MaintenanceDocumentDao getMaintenanceDocumentDao() {
        return maintenanceDocumentDao;
    }

    /**
     * @param maintenanceDocumentDao The maintenanceDocumentDao to set.
     */
    public void setMaintenanceDocumentDao(MaintenanceDocumentDao maintenanceDocumentDao) {
        this.maintenanceDocumentDao = maintenanceDocumentDao;
    }
}
