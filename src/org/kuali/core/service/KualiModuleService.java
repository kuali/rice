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
package org.kuali.core.service;

import java.util.List;
import java.util.Map;

import org.kuali.core.KualiModule;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.bo.user.KualiModuleUser;
import org.kuali.core.bo.user.UniversalUser;

public interface KualiModuleService {

    /**
     * get the list of all installed modules
     * 
     * @return
     */
    List<KualiModule> getInstalledModules();

    /**
     * Returns a map of all KualiModuleUser objects for the given user keyed by the module ID.
     * 
     * @param user
     * @return
     */
    Map<String, KualiModuleUser> getModuleUsers(UniversalUser user);

    /**
     * Returns a list of all KualiModuleUser objects.
     * 
     * @param user
     * @return
     */
    List<KualiModuleUser> getModuleUserList(UniversalUser user);

    /**
     * Returns the module with the given ID or null if the module ID is not found.
     * 
     * @param moduleId
     * @return
     */
    KualiModule getModule(String moduleId);

    /**
     * Returns the module with the given moduleCode or null if the moduleCode is not found.
     * 
     * @param moduleId
     * @return
     */
    KualiModule getModuleByCode(String moduleCode);
    
    boolean isModuleInstalled(String moduleId);

    /**
     * Given a class, this method will return the module which is responsible for authorizing access to it. It returns null if no
     * module is found.
     * 
     * @param boClass
     * @return
     */
    KualiModule getResponsibleModule(Class boClass);

    /**
     * Checks whether the user can perform the requested operation.
     * 
     * @param user
     * @param authType
     * @return
     */
    public boolean isAuthorized(UniversalUser user, AuthorizationType authType);

    public void setInstalledModules(List<KualiModule> modules);
    
    public List<String> getDataDictionaryPackages();
}
