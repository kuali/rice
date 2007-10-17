/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.user;

/**
 * A {@link UserId} which represents the id a user would use to authenticate
 * with the system.  Also known as a "Network ID".
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public final class AuthenticationUserId implements UserId {

	private static final long serialVersionUID = -7572471214298368811L;

	private String authenticationId;

    public AuthenticationUserId(String authenticationId) {
        setAuthenticationId(authenticationId);
    }

    public AuthenticationUserId() {
    }

    public String getId() {
        return getAuthenticationId();
    }
    
    public String getAuthenticationId() {
        return authenticationId;
    }

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
     * If you make this class non-final, you must rewrite equals to work for subclasses.
     */
    public boolean equals(Object obj) {

        if (obj != null && (obj instanceof AuthenticationUserId)) {
            AuthenticationUserId a = (AuthenticationUserId) obj;

            if (getAuthenticationId() == null) {
                return false;
            }

            return authenticationId.equals(a.authenticationId);
        }

        return false;
    }

    public int hashCode() {
        return authenticationId == null ? 0 : authenticationId.hashCode();
    }

    public String toString() {
        if (authenticationId == null) {
            return "authenticationId: null";
        }
        return "authenticationId: " + authenticationId;
    }
}