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
package org.kuali.rice.kim.impl.services;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.config.module.RunMode;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.framework.role.RoleTypeService;
import org.kuali.rice.kim.impl.data.DataIntegrityService;
import org.kuali.rice.kim.impl.group.GroupInternalService;
import org.kuali.rice.kim.impl.responsibility.ResponsibilityInternalService;
import org.kuali.rice.kim.impl.role.RoleDao;
import org.kuali.rice.kim.impl.role.RoleInternalService;
import org.springframework.cache.CacheManager;

import javax.sql.DataSource;
import javax.xml.namespace.QName;

public class KimImplServiceLocator {
    private static final Logger LOG = Logger.getLogger(KimImplServiceLocator.class);

    public static final String KIM_RUN_MODE_PROPERTY = "kim.mode";
    public static final String RESPONSIBILITY_INTERNAL_SERVICE = "responsibilityInternalService";
    public static final String GROUP_INTERNAL_SERVICE = "groupInternalService";
    public static final String ROLE_INTERNAL_SERVICE = "kimRoleInternalService";
    public static final String LOCAL_CACHE_MANAGER = "kimLocalCacheManager";
    public static final String DEFAULT_ROLE_TYPE_SERVICE = "kimRoleTypeService";
    public static final String DATA_INTEGRITY_SERVICE = "kimDataIntegrityService";

    public static final String KIM_ROLE_DAO = "kimRoleDao";
    public static final String KIM_DATA_SOURCE = "kimDataSource";

    public static Object getService(String serviceName) {
        return getBean(serviceName);
    }

    public static Object getBean(String serviceName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Fetching service " + serviceName);
        }
        QName name = new QName(serviceName);
        RunMode kimRunMode = RunMode.valueOf(ConfigContext.getCurrentContextConfig().getProperty(KIM_RUN_MODE_PROPERTY));
        if (kimRunMode == RunMode.REMOTE || kimRunMode == RunMode.THIN) {
            name = new QName(KimConstants.KIM_MODULE_NAMESPACE, serviceName);
        }
        return GlobalResourceLoader.getResourceLoader().getService(name);
    }

    public static ResponsibilityInternalService getResponsibilityInternalService() {
        return (ResponsibilityInternalService) getService(RESPONSIBILITY_INTERNAL_SERVICE);
    }

    public static GroupInternalService getGroupInternalService() {
        return (GroupInternalService) getService(GROUP_INTERNAL_SERVICE);
    }

    public static RoleInternalService getRoleInternalService() {
        return (RoleInternalService) getService(ROLE_INTERNAL_SERVICE);
    }

    public static CacheManager getLocalCacheManager() {
        return (CacheManager) getService(LOCAL_CACHE_MANAGER);
    }

    public static RoleTypeService getDefaultRoleTypeService() {
        return (RoleTypeService) getService(DEFAULT_ROLE_TYPE_SERVICE);
    }

    public static DataIntegrityService getDataIntegrityService() {
        return (DataIntegrityService) getService(DATA_INTEGRITY_SERVICE);
    }

    public static RoleDao getRoleDao() {
        return (RoleDao) getService(KIM_ROLE_DAO);
    }

    public static DataSource getDataSource() {
        return (DataSource)getService(KIM_DATA_SOURCE);
    }
}
