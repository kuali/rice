/*
 * Copyright 2005-2006 The Kuali Foundation.
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

import org.kuali.KeyConstants;

/**
 * This class represents an exception that is thrown when a given user is not authorized to take the given action on the given
 * target type.
 */
public class AuthorizationException extends RuntimeException {
    private static final long serialVersionUID = -3874239711783179351L;
    protected final String userId;
    protected final String action;
    protected final String targetType;


    public AuthorizationException(String userId, String action, String targetType) {
        this(userId, action, targetType, "user '" + userId + "' is not authorized to take action '" + action + "' on targets of type '" + targetType + "'");
    }

    protected AuthorizationException(String userId, String action, String targetType, String message) {
        super(message);

        this.userId = userId;
        this.action = action;
        this.targetType = targetType;
    }

    

    public String getUserId() {
        return userId;
    }

    public String getAction() {
        return action;
    }

    public String getTargetType() {
        return targetType;
    }

    /**
     * @return message key used by Struts to select the error message to be displayed
     */
    public String getErrorMessageKey() {
        return KeyConstants.AUTHORIZATION_ERROR_GENERAL;
    }
}
