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
package org.kuali.core.exceptions;

import org.kuali.RiceKeyConstants;
import org.kuali.core.KualiModule;
import org.kuali.core.authorization.AuthorizationType;

/**
 * This class represents an exception that is thrown when a given user is not authorized to use a given piece of functionality by the owning module.
 */
public class ModuleAuthorizationException extends AuthorizationException {

    public ModuleAuthorizationException(String userId, AuthorizationType authType, KualiModule module ) {
        super(userId, authType.getName(), module.getModuleName(), "user '" + userId + "' is not authorized to use the '" + authType + "' within module: '" + module.getModuleName() + "'");
    }

    /**
     * @see org.kuali.core.exceptions.AuthorizationException#getErrorMessageKey()
     */
    public String getErrorMessageKey() {
        return RiceKeyConstants.AUTHORIZATION_ERROR_MODULE;
    }

}
