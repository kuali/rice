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
package org.kuali.rice.kns.dao.proxy;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.config.ConfigurationException;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kns.bo.ModuleConfiguration;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.dao.BusinessObjectDao;
import org.kuali.rice.kns.dao.impl.BusinessObjectDaoJpa;
import org.kuali.rice.kns.dao.impl.BusinessObjectDaoOjb;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ModuleService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class BusinessObjectDaoProxy implements BusinessObjectDao {

	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BusinessObjectDaoProxy.class);

	private BusinessObjectDao businessObjectDaoJpa;
	private BusinessObjectDao businessObjectDaoOjb;
    private static KualiModuleService kualiModuleService;
    private static HashMap<String, BusinessObjectDao> boDaoValues = new HashMap<String, BusinessObjectDao>();

    private BusinessObjectDao getDao(Class clazz) {
        ModuleService moduleService = getKualiModuleService().getResponsibleModuleService(clazz);
        if (moduleService != null) {
            ModuleConfiguration moduleConfig = moduleService.getModuleConfiguration();
            String dataSourceName = "";
            EntityManager entityManager = null;
            if (moduleConfig != null) {
                dataSourceName = moduleConfig.getDataSourceName();
                entityManager = moduleConfig.getEntityManager();
            }

            if (StringUtils.isNotEmpty(dataSourceName)) {
                if (boDaoValues.get(dataSourceName) != null) {
                    return boDaoValues.get(dataSourceName);
                } else {
                    if (OrmUtils.isJpaAnnotated(clazz) && OrmUtils.isJpaEnabled()) {
                        //using JPA
                        BusinessObjectDaoJpa boDaoJpa = new BusinessObjectDaoJpa();
                        if (entityManager != null) {
                            boDaoJpa.setEntityManager(entityManager);
                            boDaoJpa.setPersistenceStructureService(KNSServiceLocator.getPersistenceStructureService());
                            boDaoValues.put(dataSourceName, boDaoJpa);
                            return boDaoJpa;
                        } else {
                            throw new ConfigurationException("EntityManager is null. EntityManager must be set in the Module Configuration bean in the appropriate spring beans xml. (see nested exception for details).");
                        }

                    } else {
                        //using OJB
                        BusinessObjectDaoOjb boDaoOjb = new BusinessObjectDaoOjb();
                        boDaoOjb.setJcdAlias(dataSourceName);
                        boDaoOjb.setPersistenceStructureService(KNSServiceLocator.getPersistenceStructureService());
                        boDaoValues.put(dataSourceName, boDaoOjb);
                        return boDaoOjb;
                    }

                }

            }
        }
        return (OrmUtils.isJpaAnnotated(clazz) && OrmUtils.isJpaEnabled()) ? businessObjectDaoJpa : businessObjectDaoOjb;
    }

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#countMatching(java.lang.Class, java.util.Map)
	 */
	public int countMatching(Class clazz, Map fieldValues) {
		return getDao(clazz).countMatching(clazz, fieldValues);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#countMatching(java.lang.Class, java.util.Map, java.util.Map)
	 */
	public int countMatching(Class clazz, Map positiveFieldValues, Map negativeFieldValues) {
		return getDao(clazz).countMatching(clazz, positiveFieldValues, negativeFieldValues);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#delete(org.kuali.rice.kns.bo.PersistableBusinessObject)
	 */
	public void delete(PersistableBusinessObject bo) {
		if (bo != null) {
			getDao(bo.getClass()).delete(bo);
		}
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#delete(java.util.List)
	 */
	public void delete(List<? extends PersistableBusinessObject> boList) {
		if (!boList.isEmpty()) {
			getDao(boList.get(0).getClass()).delete(boList);
		}
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#deleteMatching(java.lang.Class, java.util.Map)
	 */
	public void deleteMatching(Class clazz, Map fieldValues) {
		getDao(clazz).deleteMatching(clazz, fieldValues);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findAll(java.lang.Class)
	 */
	public Collection findAll(Class clazz) {
		return getDao(clazz).findAll(clazz);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findAllActive(java.lang.Class)
	 */
	public Collection findAllActive(Class clazz) {
		return getDao(clazz).findAllActive(clazz);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findAllInactive(java.lang.Class)
	 */
	public Collection findAllInactive(Class clazz) {
		return getDao(clazz).findAllInactive(clazz);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findAllActiveOrderBy(java.lang.Class, java.lang.String, boolean)
	 */
	public Collection findAllActiveOrderBy(Class clazz, String sortField, boolean sortAscending) {
		return getDao(clazz).findAllActiveOrderBy(clazz, sortField, sortAscending);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findAllOrderBy(java.lang.Class, java.lang.String, boolean)
	 */
	public Collection findAllOrderBy(Class clazz, String sortField, boolean sortAscending) {
		return getDao(clazz).findAllOrderBy(clazz, sortField, sortAscending);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findBySinglePrimaryKey(java.lang.Class, java.lang.Object)
	 */
	public PersistableBusinessObject findBySinglePrimaryKey(Class clazz, Object primaryKey) {
		return getDao(clazz).findBySinglePrimaryKey(clazz, primaryKey);
	}
	
	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findByPrimaryKey(java.lang.Class, java.util.Map)
	 */
	public PersistableBusinessObject findByPrimaryKey(Class clazz, Map primaryKeys) {
		return getDao(clazz).findByPrimaryKey(clazz, primaryKeys);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findMatching(java.lang.Class, java.util.Map)
	 */
	public Collection findMatching(Class clazz, Map fieldValues) {
		return getDao(clazz).findMatching(clazz, fieldValues);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findMatchingActive(java.lang.Class, java.util.Map)
	 */
	public Collection findMatchingActive(Class clazz, Map fieldValues) {
		return getDao(clazz).findMatchingActive(clazz, fieldValues);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#findMatchingOrderBy(java.lang.Class, java.util.Map, java.lang.String, boolean)
	 */
	public Collection findMatchingOrderBy(Class clazz, Map fieldValues, String sortField, boolean sortAscending) {
		return getDao(clazz).findMatchingOrderBy(clazz, fieldValues, sortField, sortAscending);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#retrieve(org.kuali.rice.kns.bo.PersistableBusinessObject)
	 */
	public PersistableBusinessObject retrieve(PersistableBusinessObject object) {
		return getDao(object.getClass()).retrieve(object);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#save(org.kuali.rice.kns.bo.PersistableBusinessObject)
	 */
	public void save(PersistableBusinessObject bo) {
		getDao(bo.getClass()).save(bo);
	}

	/**
	 * @see org.kuali.rice.kns.dao.BusinessObjectDao#save(java.util.List)
	 */
	public void save(List businessObjects) {
		if (!businessObjects.isEmpty()) {
			getDao(businessObjects.get(0).getClass()).save(businessObjects);
		}
	}

    private static KualiModuleService getKualiModuleService() {
        if (kualiModuleService == null) {
            kualiModuleService = KNSServiceLocator.getKualiModuleService();
        }
        return kualiModuleService;
    }

	public void setBusinessObjectDaoJpa(BusinessObjectDao businessObjectDaoJpa) {
		this.businessObjectDaoJpa = businessObjectDaoJpa;
	}

	public void setBusinessObjectDaoOjb(BusinessObjectDao businessObjectDaoOjb) {
		this.businessObjectDaoOjb = businessObjectDaoOjb;
	}

}
