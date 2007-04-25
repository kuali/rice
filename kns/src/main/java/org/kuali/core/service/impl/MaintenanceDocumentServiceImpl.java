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
package org.kuali.core.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.dao.MaintenanceDocumentDao;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.service.MaintenanceDocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the service implementation for the MaintenanceDocument structure. This is the default implementation, that is
 * delivered with Kuali.
 */
@Transactional
public class MaintenanceDocumentServiceImpl implements MaintenanceDocumentService {
    
    private MaintenanceDocumentDao maintenanceDocumentDao;

    /**
     * @see org.kuali.core.service.MaintenanceDocumentService#getLockingDocumentId(org.kuali.core.document.MaintenanceDocument)
     */
    public String getLockingDocumentId(MaintenanceDocument document) {

        String lockingDocId = null;
        String documentNumber = document.getDocumentNumber();

        List<MaintenanceLock> maintenanceLocks = document.getNewMaintainableObject().generateMaintenanceLocks();
        for (MaintenanceLock maintenanceLock : maintenanceLocks) {
            lockingDocId = maintenanceDocumentDao.getLockingDocumentNumber(maintenanceLock.getLockingRepresentation(),documentNumber);
            if (StringUtils.isNotBlank(lockingDocId)) {
                break;
            }
        }

        return lockingDocId;
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentService#getPendingObjects(java.lang.Class)
     */
    public List getPendingObjects(Class businessObjectClass) {
        List pendingObjects = new ArrayList();

        Collection pendingMaintDocs = maintenanceDocumentDao.getPendingDocumentsForClass(businessObjectClass);
        for (Iterator iter = pendingMaintDocs.iterator(); iter.hasNext();) {
            MaintenanceDocument maintDoc = (MaintenanceDocument) iter.next();
            maintDoc.populateMaintainablesFromXmlDocumentContents();
            pendingObjects.add(maintDoc.getNewMaintainableObject().getBusinessObject());
        }

        return pendingObjects;
    }
    
    /**
     * @see org.kuali.core.service.MaintenanceDocumentService#deleteLocks(String)
     */
    public void deleteLocks(String documentNumber) {
        maintenanceDocumentDao.deleteLocks(documentNumber);
    }

    /**
     * @see org.kuali.core.service.MaintenanceDocumentService#saveLocks(List)
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