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
package org.kuali.rice.krad.dao.proxy;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.core.proxy.ProxyHelper;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.dao.PersistenceDao;
import org.kuali.rice.krad.dao.impl.PersistenceDaoOjb;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.LegacyUtils;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Deprecated
@Transactional
public class PersistenceDaoProxy implements PersistenceDao {
	private PersistenceDao persistenceDaoOjb;
	private static KualiModuleService kualiModuleService;
	private static HashMap<String, PersistenceDao> persistenceDaoValues = new HashMap<String, PersistenceDao>();

	public void setPersistenceDaoOjb(PersistenceDao persistenceDaoOjb) {
		this.persistenceDaoOjb = persistenceDaoOjb;
	}
	
    private PersistenceDao getDao(Class clazz) {
        ModuleService moduleService = getKualiModuleService().getResponsibleModuleService(clazz);
        if (moduleService != null) {
            ModuleConfiguration moduleConfig = moduleService.getModuleConfiguration();
            String dataSourceName = "";
            if (moduleConfig != null) {
                dataSourceName = moduleConfig.getDataSourceName();
            }

            if (StringUtils.isNotEmpty(dataSourceName)) {
                if (persistenceDaoValues.get(dataSourceName) != null) {
                    return persistenceDaoValues.get(dataSourceName);
                } else {
                    //using OJB
                    PersistenceDaoOjb persistDaoOjb = new PersistenceDaoOjb();
                    persistDaoOjb.setJcdAlias(dataSourceName);

                    persistenceDaoValues.put(dataSourceName, persistDaoOjb);
                    return persistDaoOjb;
                }

            }
        }
    	return persistenceDaoOjb;
    }

	/**
     * @see org.kuali.rice.krad.dao.PersistenceDao#clearCache()
     */
    public void clearCache() {
        persistenceDaoOjb.clearCache();
    }

    /**
     * @see org.kuali.rice.krad.dao.PersistenceDao#resolveProxy(java.lang.Object)
     */
    public Object resolveProxy(Object o) {
    	return getDao(ObjectUtils.materializeClassForProxiedObject(o)).resolveProxy(o);
    }

    /**
     * @see org.kuali.rice.krad.dao.PersistenceDao#retrieveAllReferences(java.lang.Object)
     */
    public void retrieveAllReferences(Object o) {
    	getDao(ObjectUtils.materializeClassForProxiedObject(o)).retrieveAllReferences(o);
    }

    /**
     * @see org.kuali.rice.krad.dao.PersistenceDao#retrieveReference(java.lang.Object, java.lang.String)
     */
    public void retrieveReference(Object o, String referenceName) {
    	getDao(ObjectUtils.materializeClassForProxiedObject(o)).retrieveReference(o, referenceName);
    }
 
    /**
	 * Asks proper DAO implementation if the object is proxied
	 * 
	 * @see org.kuali.rice.krad.dao.PersistenceDao#isProxied(java.lang.Object)
	 */
	public boolean isProxied(Object object) {
		//if (object instanceof HibernateProxy) return true;
		if (ProxyHelper.isProxy(object)) return true;
		return false;
	}

	private static KualiModuleService getKualiModuleService() {
        if (kualiModuleService == null) {
            kualiModuleService = KRADServiceLocatorWeb.getKualiModuleService();
        }
        return kualiModuleService;
    }
}
