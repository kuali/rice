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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.KualiModule;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.bo.user.KualiModuleUser;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.KualiModuleService;

public class KualiModuleServiceImpl implements KualiModuleService {

    private List<KualiModule> installedModules = new ArrayList<KualiModule>();;
    
    public List<KualiModule> getInstalledModules() {
        return installedModules;
    }

    public Map<String,KualiModuleUser> getModuleUsers(UniversalUser user) {
        Map<String, KualiModuleUser> moduleUsers = new HashMap<String, KualiModuleUser>();
        for (KualiModule module : installedModules) {
            try {
                moduleUsers.put(module.getModuleId(), module.getModuleUserService().getModuleUser(user));
            }
            catch (UserNotFoundException e) {
                // if the user does not exist, create an empty one and set as inactive
            }
        }
        return moduleUsers;
    }

    public List<KualiModuleUser> getModuleUserList(UniversalUser user) {
        List<KualiModuleUser> moduleUsers = new ArrayList<KualiModuleUser>();
        for (KualiModule module : installedModules) {
            try {
                moduleUsers.add( module.getModuleUserService().getModuleUser(user) );
            }
            catch (UserNotFoundException e) {
                // ignore, if the user does not exist, skip it
            }
        }
        return moduleUsers;
    }

    public KualiModule getModule(String moduleId) {
        for (KualiModule module : installedModules) {
            if ( module.getModuleId().equals( moduleId ) ) {
                return module;
            }
        } 
        return null;
    }

    
    /**
     * @see org.kuali.core.service.KualiModuleService#getModuleByCode(java.lang.String)
     */
    public KualiModule getModuleByCode(String moduleCode) {
        for (KualiModule module : installedModules) {
            if ( module.getModuleCode().equals( moduleCode ) ) {
                return module;
            }
        } 
        return null;
    }

    public boolean isModuleInstalled(String moduleId) {
        for (KualiModule module : installedModules) {
            if ( module.getModuleId().equals( moduleId ) ) {
                return true;
            }
        } 
        return false;
    }

    public KualiModule getResponsibleModule(Class boClass) {
        for (KualiModule module : installedModules) {
            if ( module.getModuleAuthorizer() != null ) {
                if ( module.getModuleAuthorizer().isResponsibleFor( boClass ) ) {
                    return module;
                }
            }
        }
        return null;
    }

    public void setInstalledModules(List<KualiModule> installedModules) {
        this.installedModules = installedModules;
    }

    public boolean isAuthorized( UniversalUser user, AuthorizationType authType ) {
        if ( user != null && authType != null ) {
            KualiModule module = getResponsibleModule( authType.getTargetObjectClass() );
            if ( module != null ) {
                if ( module.getModuleAuthorizer() != null ) {
                    if ( !module.getModuleAuthorizer().isAuthorized( user, authType ) ) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public List<String> getDataDictionaryPackages() {
        List<String> packages  = new ArrayList<String>();
        for ( KualiModule module : installedModules ) {
            if ( module.getDataDictionaryPackages() != null ) {
                packages.addAll( module.getDataDictionaryPackages() );
            }
        }
        return packages;
    }
}
