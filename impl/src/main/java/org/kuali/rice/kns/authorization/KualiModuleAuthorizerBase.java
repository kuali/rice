/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.rice.kns.authorization;

import java.util.List;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.ModuleService;

public class KualiModuleAuthorizerBase implements KualiModuleAuthorizer {

    private ModuleService moduleService;
    private List<String> packagePrefixes;
    
    public boolean isAuthorized(Person user, AuthorizationType authType) {
        return moduleService.canAccessModule(user);
    }

    public final ModuleService getModuleService() {
        return moduleService;
    }

    public final void setModuleService(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    public List<String> getPackagePrefixes() {
        return packagePrefixes;
    }
    public void setPackagePrefixes(List<String> packagePrefixes) {
        this.packagePrefixes = packagePrefixes;
    }

    public boolean isResponsibleFor(Class boClass) {
        if ( packagePrefixes == null ) {
            return false;
        }
        for ( String prefix : packagePrefixes ) {
            if ( boClass.getPackage().getName().startsWith( prefix ) ) {
                return true;
            }
        }
        return false;
    }
    
}

