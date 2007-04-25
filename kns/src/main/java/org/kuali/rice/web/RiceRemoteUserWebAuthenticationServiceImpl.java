/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.rice.web;

import javax.servlet.http.HttpServletRequest;

import edu.iu.uis.eden.web.RemoteUserWebAuthenticationService;
import edu.iu.uis.eden.web.WebAuthenticationService;
import edu.iu.uis.eden.web.session.UserSession;

/**
 * 
 * A shim to bridge the kuali authentication and workflow authentication.  This is used to 
 * feed kuali code user information instead of the kuali cas auth service.  It's wired up in the 
 * core override spring beans.
 * 
 * @author rkirkend
 */
public class RiceRemoteUserWebAuthenticationServiceImpl extends RemoteUserWebAuthenticationService implements org.kuali.core.service.WebAuthenticationService {

    public boolean isValidatePassword() {
        return false;
    }

}