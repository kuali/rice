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
package org.kuali.core.authorization;

import java.util.List;

import org.kuali.core.KualiModule;
import org.kuali.core.bo.user.UniversalUser;

public class KualiModuleAuthorizerBase implements KualiModuleAuthorizer {

    private KualiModule module;
    private List<String> packagePrefixes;
    
    public boolean isAuthorized(UniversalUser user, AuthorizationType authType) {
        return user.isActiveForAnyModule();
    }

    public final KualiModule getModule() {
        return module;
    }

    public final void setModule(KualiModule module) {
        this.module = module;
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
