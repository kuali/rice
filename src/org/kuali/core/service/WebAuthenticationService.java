/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import javax.servlet.http.HttpServletRequest;

/**
 * This interface defines methods that a WebAuthentication Service must provide.
 * 
 * 
 */
public interface WebAuthenticationService extends edu.iu.uis.eden.web.WebAuthenticationService {
    /**
     * This method retrieves the network id from the servlet request.
     * 
     * @param request
     * @return The network Id of the user as a string.
     */
    public String getNetworkId(HttpServletRequest request);
    
    /**
     * Returns whether the authentication service should accept and validate a password.
     */
    public boolean isValidatePassword();
}