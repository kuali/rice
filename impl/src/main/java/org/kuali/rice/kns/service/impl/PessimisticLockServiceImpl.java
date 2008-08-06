/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.KNSServiceLocator;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.document.authorization.PessimisticLock;
import org.kuali.rice.kns.exception.AuthorizationException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.PessimisticLockService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is a service implementation for pessimistic locking 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
@Transactional
public class PessimisticLockServiceImpl implements PessimisticLockService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PessimisticLockServiceImpl.class);

    private BusinessObjectService businessObjectService;
    
    /**
     * @see org.kuali.rice.kns.service.PessimisticLockService#delete(java.lang.String)
     */
    public void delete(String id) {
        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("An invalid blank id was passed to delete a Pessimistic Lock.");
        }
        Map primaryKeys = new HashMap();
        primaryKeys.put(KNSPropertyConstants.ID, Long.valueOf(id));
        PessimisticLock lock = (PessimisticLock) businessObjectService.findByPrimaryKey(PessimisticLock.class, primaryKeys);
        if (ObjectUtils.isNull(lock)) {
            throw new IllegalArgumentException("Pessimistic Lock with id " + id + " cannot be found in the database.");
        }
        if ( (!lock.isOwnedByUser(user)) && (!isPessimisticLockAdminUser(user)) ) {
            throw new AuthorizationException(user.getPersonName(),"delete", "Pessimistick Lock (id " + id + ")");
        }
        delete(lock);
    }
    
    private void delete(PessimisticLock lock) {
        LOG.debug("Deleting lock: " + lock);
        getBusinessObjectService().delete(lock);
    }

    /**
     * @see org.kuali.rice.kns.service.PessimisticLockService#generateNewLock()
     */
    public PessimisticLock generateNewLock(String documentNumber) {
        return generateNewLock(documentNumber, GlobalVariables.getUserSession().getUniversalUser());
    }

    /**
     * @see org.kuali.rice.kns.service.PessimisticLockService#generateNewLock(java.lang.String)
     */
    public PessimisticLock generateNewLock(String documentNumber, String lockDescriptor) {
        return generateNewLock(documentNumber, lockDescriptor, GlobalVariables.getUserSession().getUniversalUser());
    }

    /**
     * @see org.kuali.rice.kns.service.PessimisticLockService#generateNewLock(java.lang.String, org.kuali.rice.kns.bo.user.UniversalUser)
     */
    public PessimisticLock generateNewLock(String documentNumber, UniversalUser user) {
        return generateNewLock(documentNumber, PessimisticLock.DEFAUL_LOCK_DESCRIPTOR, user);
    }

    /**
     * @see org.kuali.rice.kns.service.PessimisticLockService#generateNewLock(java.lang.String, java.lang.String, org.kuali.rice.kns.bo.user.UniversalUser)
     */
    public PessimisticLock generateNewLock(String documentNumber, String lockDescriptor, UniversalUser user) {
        PessimisticLock lock = new PessimisticLock(documentNumber, lockDescriptor, user);
        save(lock);
        LOG.debug("Generated new lock: " + lock);
        return lock;
    }

    /**
     * @see org.kuali.rice.kns.service.PessimisticLockService#getPessimisticLocksForDocument(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<PessimisticLock> getPessimisticLocksForDocument(String documentNumber) {
        Map fieldValues = new HashMap();
        fieldValues.put(KNSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        return (List<PessimisticLock>) getBusinessObjectService().findMatching(PessimisticLock.class, fieldValues);
    }
    
    /**
     * @see org.kuali.rice.kns.service.PessimisticLockService#isPessimisticLockAdminUser(org.kuali.rice.kns.bo.user.UniversalUser)
     */
    public boolean isPessimisticLockAdminUser(UniversalUser user) {
        String workgroupName = KNSServiceLocator.getKualiConfigurationService().getParameterValue(KNSConstants.KNS_NAMESPACE, KNSConstants.DetailTypes.DOCUMENT_DETAIL_TYPE, KNSConstants.PESSIMISTIC_LOCK_ADMIN_GROUP_PARM_NM);
        if (StringUtils.isNotBlank(workgroupName)) {
            boolean returnValue = user.isMember(workgroupName);
            return returnValue;
        }
        return false;
    }

    /**
     * @see org.kuali.rice.kns.service.PessimisticLockService#releaseAllLocksForUser(java.util.List, org.kuali.rice.kns.bo.user.UniversalUser)
     */
    public void releaseAllLocksForUser(List<PessimisticLock> locks, UniversalUser user) {
        for (Iterator<PessimisticLock> iterator = locks.iterator(); iterator.hasNext();) {
            PessimisticLock lock = (PessimisticLock) iterator.next();
            if (lock.isOwnedByUser(user)) {
                delete(lock);
            }
        }
    }

    /**
     * @see org.kuali.rice.kns.service.PessimisticLockService#releaseAllLocksForUser(java.util.List, org.kuali.rice.kns.bo.user.UniversalUser, java.lang.String)
     */
    public void releaseAllLocksForUser(List<PessimisticLock> locks, UniversalUser user, String lockDescriptor) {
        for (Iterator<PessimisticLock> iterator = locks.iterator(); iterator.hasNext();) {
            PessimisticLock lock = (PessimisticLock) iterator.next();
            if ( (lock.isOwnedByUser(user)) && (lockDescriptor.equals(lock.getLockDescriptor())) ) {
                delete(lock);
            }
        }
    }

    /**
     * @see org.kuali.rice.kns.service.PessimisticLockService#save(org.kuali.rice.kns.document.authorization.PessimisticLock)
     */
    public void save(PessimisticLock lock) {
        LOG.debug("Saving lock: " + lock);
        getBusinessObjectService().save(lock);
    }

    public BusinessObjectService getBusinessObjectService() {
        return this.businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
