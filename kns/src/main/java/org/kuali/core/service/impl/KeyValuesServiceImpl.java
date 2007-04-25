/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.PropertyConstants;
import org.kuali.core.dao.BusinessObjectDao;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.util.spring.Cached;
import org.kuali.rice.KNSServiceLocator;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class provides collection retrievals to populate key value pairs of business objects.
 */
@Transactional
@Cached
public class KeyValuesServiceImpl implements KeyValuesService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KeyValuesServiceImpl.class);

    private BusinessObjectDao businessObjectDao;
    private PersistenceStructureService persistenceStructureService;

    /**
     * @see org.kuali.core.service.KeyValuesService#findAll(java.lang.Class)
     */
    public Collection findAll(Class clazz) {
        if (containsActiveIndicator(clazz)) {
            return businessObjectDao.findAllActive(clazz);
        }
        else {
            if (LOG.isDebugEnabled()) LOG.debug("Active indicator not found for class " + clazz.getName());
            return businessObjectDao.findAll(clazz);
        }
    }

    /**
     * @see org.kuali.core.service.KeyValuesService#findAllOrderBy(java.lang.Class, java.lang.String, boolean)
     */
    public Collection findAllOrderBy(Class clazz, String sortField, boolean sortAscending) {
        if (containsActiveIndicator(clazz)) {
            return businessObjectDao.findAllActiveOrderBy(clazz, sortField, sortAscending);
        }
        else {
            if (LOG.isDebugEnabled()) LOG.debug("Active indicator not found for class " + clazz.getName());
            return businessObjectDao.findAllOrderBy(clazz, sortField, sortAscending);
        }
    }

    /**
     * @see org.kuali.core.service.BusinessObjectService#findMatching(java.lang.Class, java.util.Map)
     */
    public Collection findMatching(Class clazz, Map fieldValues) {
        if (containsActiveIndicator(clazz)) {
            return businessObjectDao.findMatchingActive(clazz, fieldValues);
        }
        else {
            if (LOG.isDebugEnabled()) LOG.debug("Active indicator not found for class " + clazz.getName());
            return businessObjectDao.findMatching(clazz, fieldValues);
        }
    }



    /**
     * @return Returns the businessObjectDao.
     */
    public BusinessObjectDao getBusinessObjectDao() {
        return businessObjectDao;
    }

    /**
     * @param businessObjectDao The businessObjectDao to set.
     */
    public void setBusinessObjectDao(BusinessObjectDao businessObjectDao) {
        this.businessObjectDao = businessObjectDao;
    }

    /**
     * Gets the persistenceStructureService attribute.
     * 
     * @return Returns the persistenceStructureService.
     */
    public PersistenceStructureService getPersistenceStructureService() {
        return persistenceStructureService;
    }

    /**
     * Sets the persistenceStructureService attribute value.
     * 
     * @param persistenceStructureService The persistenceStructureService to set.
     */
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * Uses persistence service to determine if the active column is mapped up in ojb.
     * 
     * @param clazz
     * @return boolean if active column is mapped for Class
     */
    private boolean containsActiveIndicator(Class clazz) {
        boolean containsActive = false;

        if (persistenceStructureService.listFieldNames(clazz).contains(PropertyConstants.ACTIVE)) {
            containsActive = true;
        }

        return containsActive;
    }
}
