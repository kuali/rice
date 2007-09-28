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

import java.util.Collections;
import java.util.List;

import org.kuali.RiceConstants;
import org.kuali.core.KualiModule;
import org.kuali.core.bo.user.KualiModuleUser;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.bo.user.UserId;
import org.kuali.core.dao.KualiModuleUserDao;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiModuleUserService;
import org.kuali.core.service.UniversalUserService;

public abstract class KualiModuleUserServiceBaseImpl<T extends KualiModuleUser> implements KualiModuleUserService<T> {

    protected UniversalUserService universalUserService;
    protected List<String> propertyList = Collections.EMPTY_LIST;
    protected Class moduleUserClass;
    protected KualiConfigurationService configService;
    protected KualiModule module;
    protected KualiModuleUserDao kualiModuleUserDao;
    
    public T getModuleUser(String moduleID, String personUniversalIdentifier) {
        try {
            return (T)universalUserService.getUniversalUser( personUniversalIdentifier ).getModuleUser( moduleID );
        } catch ( UserNotFoundException ex ) {
            return null;
        }
    }
        
    public T getModuleUser(String moduleID, UserId userId) throws UserNotFoundException {
        try {
            return (T)universalUserService.getUniversalUser( userId ).getModuleUser( moduleID );
        } catch ( UserNotFoundException ex ) {
            return null;
        }
    }

    public T getModuleUser(String moduleID, UniversalUser universalUser) {
        return (T)universalUser.getModuleUser( moduleID );
    }

    public List<String> getPropertyList() {
        return propertyList;
    }
    
    public void setPropertyList(List<String> propertyList) {
        this.propertyList = propertyList;
    }

    public UniversalUserService getUniversalUserService() {
        return universalUserService;
    }

    public void setUniversalUserService(UniversalUserService universalUserService) {
        this.universalUserService = universalUserService;
    }

    public Class getModuleUserClass() {
        return moduleUserClass;
    }

    public void setModuleUserClass(Class moduleUserClass) {
        this.moduleUserClass = moduleUserClass;
    }

    public KualiConfigurationService getConfigService() {
        return configService;
    }

    public void setConfigService(KualiConfigurationService configService) {
        this.configService = configService;
    }

    public KualiModule getModule() {
        return module;
    }

    public void setModule(KualiModule module) {
        this.module = module;
    }

	public KualiModuleUserDao getKualiModuleUserDao() {
		return kualiModuleUserDao;
	}

	public void setKualiModuleUserDao(KualiModuleUserDao kualiModuleUserDao) {
		this.kualiModuleUserDao = kualiModuleUserDao;
	}
    
	public Object getUserActiveCriteria() {
		return kualiModuleUserDao.getActiveUserQueryCriteria( module.getModuleId() );
	}
    
	public Object getActiveFacultyStaffAffiliateCriteria() {
        String[] allowedEmployeeStatusValues = getConfigService().getParameterValues( RiceConstants.KNS_NAMESPACE, RiceConstants.DetailTypes.KUALI_MODULE_USER_DETAIL_TYPE, RiceConstants.ALLOWED_EMPLOYEE_STATUS_RULE);
		return kualiModuleUserDao.getActiveFacultyStaffAffiliateCriteria(allowedEmployeeStatusValues);
	}
}
