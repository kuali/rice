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
package org.kuali.core.bo.user;

import java.io.Serializable;

/**
 * This class is to hold our users network ids and wrap them in a strongly typed object
 * 
 * 
 */
public final class AuthenticationUserId implements UserId, Serializable {
    private static final long serialVersionUID = 2540727071768501528L;

    public static final AuthenticationUserId NOT_FOUND = new AuthenticationUserId("not found");

    private String authenticationId;

    /**
     * Constructor that takes in a string authenticationId
     * 
     * @param authenticationId
     */
    public AuthenticationUserId(String authenticationId) {
        setAuthenticationId(authenticationId);
    }

    /**
     * Empty constructor, available to support standard bean activity
     * 
     */
    public AuthenticationUserId() {
    }

    /**
     * getter which returns the string authenticationId
     * 
     * @return
     */
    public String getAuthenticationId() {
        return authenticationId;
    }

    /**
     * setter which takes the string authenticationId
     * 
     * @param authenticationId
     */
    public void setAuthenticationId(String authenticationId) {
        this.authenticationId = (authenticationId == null ? null : authenticationId.trim());
    }

    /**
     * Returns true if this userId has an empty value. Empty userIds can't be used as keys in a Hash, among other things.
     * 
     * @return true if this instance doesn't have a value
     */
    public boolean isEmpty() {
        return (authenticationId == null || authenticationId.trim().length() == 0);
    }

    /**
     * override equals so that we can compare authenticationIds If you make this class non-final, you must rewrite equals to work
     * for subclasses.
     */
    public boolean equals(Object obj) {
        boolean isEqual = false;

        if (obj != null && (obj instanceof AuthenticationUserId)) {
            AuthenticationUserId a = (AuthenticationUserId) obj;

            if (getAuthenticationId() == null) {
                return false;
            }

            return authenticationId.equals(a.authenticationId);
        }

        return false;
    }

    /**
     * override hashCode because we overrode equals
     */
    public int hashCode() {
        return authenticationId == null ? 0 : authenticationId.hashCode();
    }

    /**
     * override toString so that it prints out the authenticationId String
     */
    public String toString() {
        return authenticationId;
    }
}