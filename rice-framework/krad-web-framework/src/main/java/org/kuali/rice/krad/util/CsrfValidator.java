/**
 * Copyright 2005-2016 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import java.util.UUID;

/**
 * Simple utility class that will validate the given request to determine if it has any required CSRF information,
 * setting appropriate response errors if not.
 *
 * @author Eric Westfall
 */
public class CsrfValidator {

    private static final Logger LOG = Logger.getLogger(CsrfValidator.class);

    public static final String CSRF_PARAMETER = "csrfToken";
    public static final String CSRF_SESSION_TOKEN = "csrfSessionToken";

    /**
     * Applies CSRF protection for any HTTP method other than GET, HEAD, or OPTIONS.
     *
     * @param request the http request to check
     * @param response the http response associated with the given request
     *
     * @return true if the request validated successfully, false otherwise. If false is returned, calling code should
     * act immediately to terminate any additional work performed on the response.
     */
    public static boolean validateCsrf(HttpServletRequest request, HttpServletResponse response) {
        if (HttpMethod.GET.equals(request.getMethod()) ||
                HttpMethod.HEAD.equals(request.getMethod()) ||
                HttpMethod.OPTIONS.equals(request.getMethod())) {
            // if it's a GET and there's not already a CSRF token, then we need to generate and place a CSRF token
            placeSessionToken(request);
        } else {
            String givenCsrf = getRequestToken(request);
            String actualCsrf = getSessionToken(request);
            if (actualCsrf == null) {
                LOG.error("CSRF check failed because no CSRF token has been established on the session");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            } else if (!StringUtils.equals(givenCsrf, actualCsrf)) {
                LOG.error("CSRF check failed, actual value was: " + actualCsrf + ", given value was: " + givenCsrf + ", requested URL was: " + request.getRequestURL());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }
        return true;
    }


    /**
     * Retrieve the CSRF token that is associated with the session for the given request, or null if the session has none.
     *
     * @param request the request to check the session for the CSRF token
     * @return the CSRF token on the request's session, or null if the session has none
     */
    public static String getSessionToken(HttpServletRequest request) {
        return (String)request.getSession().getAttribute(CSRF_SESSION_TOKEN);
    }

    /**
     * Retrieve the CSRF token parameter that is on the given request, or null if the request has none.
     *
     * @param request the request to check for the CSRF token parameter
     * @return the CSRF token parameter on the request, or null if the request has none
     */
    public static String getRequestToken(HttpServletRequest request) {
        return request.getParameter(CSRF_PARAMETER);
    }

    /**
     * If the session associated with the given request has no CSRF token, this method will generate that token and
     * add it as an attribute on the session. If the session already has a CSRF token, this method will do nothing.
     *
     * @param request the request with the session on which to place the session token if needed
     */
    private static void placeSessionToken(HttpServletRequest request) {
        if (getSessionToken(request) == null) {
            request.getSession().setAttribute(CSRF_SESSION_TOKEN, UUID.randomUUID().toString());
        }
    }

}
