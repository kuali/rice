/**
 * Copyright 2005-2018 The Kuali Foundation
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
package org.kuali.rice.krad.criteria;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.kuali.rice.core.api.criteria.LookupCustomizer;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.LegacyUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CriteriaLookupDaoProxy implements CriteriaLookupDao {
    CriteriaLookupDao criteriaLookupDaoOjb;

    private static KualiModuleService kualiModuleService;
    private static Map<String, CriteriaLookupDao> lookupDaoValues = Collections.synchronizedMap(new HashMap<String, CriteriaLookupDao>());

	public void setCriteriaLookupDaoOjb(CriteriaLookupDao lookupDaoOjb) {
		this.criteriaLookupDaoOjb = lookupDaoOjb;
	}

    private CriteriaLookupDao getDao(Class clazz) {
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
        			CriteriaLookupDaoOjb classSpecificLookupDaoOjb = new CriteriaLookupDaoOjb();
                    classSpecificLookupDaoOjb.setJcdAlias(dataSourceName);
                    lookupDaoValues.put(dataSourceName, classSpecificLookupDaoOjb);
                    return classSpecificLookupDaoOjb;
                }

            }
        }
        return criteriaLookupDaoOjb;
    }

    @Override
    public <T> GenericQueryResults<T> lookup(Class<T> queryClass, QueryByCriteria criteria) {
        return getDao(queryClass).lookup(queryClass, criteria);
    }

    @Override
    public <T> GenericQueryResults<T> lookup(Class<T> queryClass, QueryByCriteria criteria,
            LookupCustomizer<T> customizer) {
        return getDao(queryClass).lookup(queryClass, criteria, customizer);
    }

    private static KualiModuleService getKualiModuleService() {
        if (kualiModuleService == null) {
            kualiModuleService = KRADServiceLocatorWeb.getKualiModuleService();
        }
        return kualiModuleService;
    }
}
