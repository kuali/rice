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

import org.kuali.core.KualiModule;
import org.kuali.core.bo.user.UniversalUser;

/**
 * Interface implemented by each module in the system to control access to the module resources as a whole.
 * The intent is to detect the package name in the class and make a decision based on that.  However, this 
 * interface could be used to perform more granular access control (e.g., based on the class name) if desired.  
 */
public interface KualiModuleAuthorizer {
    public boolean isAuthorized(UniversalUser user, AuthorizationType authType);
    
    public KualiModule getModule();
    
    public void setModule( KualiModule module );

    /** Tests if this authorizer is responsible for an attempt to use/access the given class 
     * 
     */
    public boolean isResponsibleFor( Class boClass );
}
