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
package org.kuali.rice.krad.dao.proxy;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.dao.LookupDao;
import org.kuali.rice.krad.dao.impl.LookupDaoOjb;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.LegacyUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Transactional
public class LookupDaoProxy implements LookupDao {
    
	private LookupDao lookupDaoOjb;
    private static KualiModuleService kualiModuleService;
    private static Map<String, LookupDao> lookupDaoValues = Collections.synchronizedMap(new HashMap<String, LookupDao>());
	
	public void setLookupDaoOjb(LookupDao lookupDaoOjb) {
		this.lookupDaoOjb = lookupDaoOjb;
	}
	
    private LookupDao getDao(Class clazz) {
        ModuleService moduleService = getKualiModuleService().getResponsibleModuleService(clazz);
        if (moduleService != null) {
            ModuleConfiguration moduleConfig = moduleService.getModuleConfiguration();
            String dataSourceName = "";
            if (moduleConfig != null) {
                dataSourceName = moduleConfig.getDataSourceName();
            }

            if (StringUtils.isNotEmpty(dataSourceName)) {
                if (lookupDaoValues.get(dataSourceName) != null) {
                    return lookupDaoValues.get(dataSourceName);
                } else {
                    LookupDaoOjb classSpecificLookupDaoOjb = new LookupDaoOjb();
                    classSpecificLookupDaoOjb.setJcdAlias(dataSourceName);
                    classSpecificLookupDaoOjb.setPersistenceStructureService(
                            KNSServiceLocator.getPersistenceStructureService());
                    classSpecificLookupDaoOjb.setDateTimeService(CoreApiServiceLocator.getDateTimeService());
                    classSpecificLookupDaoOjb.setDataDictionaryService(
                            KRADServiceLocatorWeb.getDataDictionaryService());
                    lookupDaoValues.put(dataSourceName, classSpecificLookupDaoOjb);
                    return classSpecificLookupDaoOjb;
                }

            }
        }
        return lookupDaoOjb;
    }
    
	/**
	 * @see org.kuali.rice.krad.dao.LookupDao#createCriteria(java.lang.Object, java.lang.String, java.lang.String, java.lang.Object)
	 */
	public boolean createCriteria(Object example, String searchValue, String propertyName, Object criteria) {
		return getDao(example.getClass()).createCriteria(example, searchValue, propertyName, criteria);
	}

	/**
	 * @see org.kuali.rice.krad.dao.LookupDao#createCriteria(java.lang.Object, java.lang.String, java.lang.String, boolean, java.lang.Object)
	 */
	public boolean createCriteria(Object example, String searchValue, String propertyName, boolean caseInsensitive, boolean treatWildcardsAndOperatorsAsLiteral, Object criteria) {
		return getDao(example.getClass()).createCriteria(example, searchValue, propertyName, caseInsensitive,
                treatWildcardsAndOperatorsAsLiteral, criteria);
	}

    /**
     * Since 2.3
     * This version of findCollectionBySearchHelper is needed for version compatibility.   It allows executeSearch
     * to behave the same way as it did prior to 2.3. In the LookupDao, the value for searchResultsLimit will be
     * retrieved from the KNS version of LookupUtils in the LookupDao.
     *
     * @see org.kuali.rice.krad.dao.LookupDao#findCollectionBySearchHelper(java.lang.Class, java.util.Map, boolean,
     *      boolean)
     */
    public Collection findCollectionBySearchHelper(Class businessObjectClass, Map formProps, boolean unbounded,
            boolean usePrimaryKeyValuesOnly) {
        return getDao(businessObjectClass).findCollectionBySearchHelper(businessObjectClass, formProps, unbounded,
                usePrimaryKeyValuesOnly);
    }

    /**
     * @see org.kuali.rice.krad.dao.LookupDao#findCollectionBySearchHelper(java.lang.Class, java.util.Map, boolean,
     *      boolean, Integer)
     */
    public Collection findCollectionBySearchHelper(Class businessObjectClass, Map formProps, boolean unbounded,
            boolean usePrimaryKeyValuesOnly, Integer searchResultsLimit) {
        return getDao(businessObjectClass).findCollectionBySearchHelper(businessObjectClass, formProps, unbounded,
                usePrimaryKeyValuesOnly, searchResultsLimit);
    }

	/**
	 * @see org.kuali.rice.krad.dao.LookupDao#findCountByMap(java.lang.Object, java.util.Map)
	 */
	public Long findCountByMap(Object example, Map formProps) {
		return getDao(example.getClass()).findCountByMap(example, formProps);
	}

    @Override
    public <T extends Object> T findObjectByMap(Class<T> type, Map<String, String> formProps) {
        return getDao(type).findObjectByMap(type, formProps);
    }

	private static KualiModuleService getKualiModuleService() {
        if (kualiModuleService == null) {
            kualiModuleService = KRADServiceLocatorWeb.getKualiModuleService();
        }
        return kualiModuleService;
    }
}
