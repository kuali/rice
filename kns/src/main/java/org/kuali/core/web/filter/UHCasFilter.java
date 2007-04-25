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
package org.kuali.core.web.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * This class is the Filter to use CAS for authentication.
 * 
 * 
 */

/**
 * <p>
 * Title: CASFilter
 * </p>
 * <p>
 * Description: Filter for Servlet 2.3 spec server to use CAS for authentication. Map any url pattern to this filter to support CAS
 * authentication for that url pattern. If login is successful at CAS the FilterCASBean will be available to you in the user's
 * session under attribute name "filterCASBean".
 * </p>
 * <p>
 * Authenticates a user by redirecting their browser to CAS for authentication. CAS puts casticket parameter on querystring after
 * successful login. The value of casticket is verified against CAS using https. If the casticket is valid we put the user's user
 * name and any key/value pairs returned by CAS (from the https request verifying casticket) into the FilterCASBean java bean and
 * save the bean in the session. Now we can check against nullness of this bean to verify user authentication.
 * </p>
 * The following init parameters are needed for each instance of this filter. These are placed in the web.xml file.
 * 
 * <filter> <filter-name>cas</filter-name> <filter-class>org.kuali.web.filter.UHCASFilter</filter-class> <init-param>
 * <param-name>serviceParamName</param-name> <param-value>service</param-value> </init-param> <init-param>
 * <param-name>ticketParamName</param-name> <param-value>ticket</param-value> </init-param> <init-param> <param-name>validationURL</param-name>
 * <param-value>https://login.its.hawaii.edu:8445/cas/validate</param-value> </init-param> <init-param> <param-name>loginURL</param-name>
 * <param-value>https://login.its.hawaii.edu:8445/cas/login</param-value> </init-param> <init-param> <param-name>logoutURL</param-name>
 * <param-value>https://login.its.hawaii.edu:8445/cas/logout</param-value> </init-param> </filter>
 * 
 * <filter-mapping> <filter-name>cas</filter-name> <servlet-name>action</servlet-name> </filter-mapping>
 * 
 * TODO: add simple param validation
 * 
 * TODO: rebuild it to work with either single or multiple URL params
 * 
 * IU's CAS server receives a URL of the form:
 * https://cas.iu.edu/cas/login?cassvc=MYANY&casurl=https://onestart.iu.edu:443/my-prd/Kerberos/Login.do generates a URL of the
 * form: https://onestart.iu.edu:443/my-prd/Portal.do?casticket=ST-285420-LYbpu3QKAjyC7D468WS2& UH's CAS server receives a URL of
 * the form: https://login.its.hawaii.edu:8445/cas/login?service=https://localhost:8443/casTest/casLogin.do generates a URL of the
 * form: https://localhost:8443/casTest/casLogin.do?ticket='ST-492-fzmviDIliftbdJrF1Q30'
 */
public class UHCasFilter implements Filter {
    private static Logger LOG = Logger.getLogger(KualiCasFilter.class);

    public static final String USERNAME_HASH = "edu.hawaii.its.filter.UsernameHash";
    public static final String USERNAME_HASH_KEY = "edu.hawaii.its.filter.BaseContext";

    private String serviceParamName;
    private String ticketParamName;

    private String validationURL;
    private String loginURL;
    private String logoutURL;

    private String autoLoginUserName;
    private String beginPostPage;
    private String endPostPage;


    /**
     * Initialize filter constansts
     * 
     * @param filterConfig
     * @throws ServletException
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");

        autoLoginUserName = filterConfig.getInitParameter("autoLoginUserName");

        serviceParamName = filterConfig.getInitParameter("serviceParamName");
        ticketParamName = filterConfig.getInitParameter("ticketParamName");

        validationURL = filterConfig.getInitParameter("validationURL");
        loginURL = filterConfig.getInitParameter("loginURL");
        logoutURL = filterConfig.getInitParameter("logoutURL");

        beginPostPage = "<html><head><script>\n" + "function pf(){\n" + "\tdocument.f.submit();\n}" + "\n</script>\n</head>" + "<title>Auth Redirect</title>" + "<body onload=\"pf()\">\n";
        endPostPage = "<input type='submit'></form></body></html>";

        LOG.debug("FilterCAS: init() ValidationURL: " + validationURL + "\nLogin URL: " + loginURL);
    }

    /**
     * Intercepts any requesting url pattern it's mapped to and directs traffic according to user's current step in authentication
     * process
     * 
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest hrequest = (HttpServletRequest) request;
        HttpSession session = hrequest.getSession();

        HashMap userList = (HashMap) session.getAttribute(USERNAME_HASH);

        if (userList == null) {
            userList = new HashMap();
            session.setAttribute(USERNAME_HASH, userList);
        }

        // If the username list has the cassvc they've been authenticated just pass request on
        String baseContext = hrequest.getContextPath();
        String serviceName = hrequest.getRequestURL().toString();

        if (userList.get(baseContext) != null) {
            LOG.debug("CASFilter doFilter(): Already Authenticated");
            request.setAttribute(USERNAME_HASH_KEY, baseContext);
            chain.doFilter(request, response);
        }
        else {
            if (!StringUtils.isBlank(autoLoginUserName)) {
                userList.put(baseContext, autoLoginUserName);
                request.setAttribute(USERNAME_HASH_KEY, baseContext);
                LOG.debug("CASFilter doFilter(): autoLoginUserName = " + autoLoginUserName);
                LOG.info("CASFilter doFilter(): spoofed authentication successful");
                chain.doFilter(request, response);
            }
            else {
                String casticket = hrequest.getParameter(ticketParamName);
                LOG.debug("CASFilter doFilter(): " + ticketParamName + "=" + casticket);
                if (casticket == null) {
                    // user hasn't been to CAS yet redirect them
                    LOG.debug("CASFilter doFilter(): no casticket redirecting browser to CAS Server");
                    redirect(hrequest, (HttpServletResponse) response);
                }
                else {
                    // user has been to CAS but casticket hasn't been verified, otherwise
                    // we'd have filterCASBean in session
                    LOG.debug("CASFilter doFilter(): casticket exists verifying it");
                    String username = null;
                    try {
                        username = validate(serviceName, casticket);
                    }
                    catch (IOException ex) {
                        LOG.error("CASFilter doFilter(): Error validating casticket");
                        username = null;
                    }

                    if (username == null) {
                        // failed validation, bad casticket, user is going back to CAS to login
                        // and get new casticket
                        LOG.debug("CASFilter doFilter(): casticket invalid redirect to browser");
                        LOG.debug("CASFilter doFilter(): query_string = " + hrequest.getQueryString());
                        redirect(hrequest, (HttpServletResponse) response);
                    }
                    else {
                        // user's casticket verified as good, adding to username list
                        // and passing request on
                        userList.put(baseContext, username);
                        request.setAttribute(USERNAME_HASH_KEY, baseContext);
                        LOG.debug("CASFilter doFilter(): username = " + username);
                        LOG.debug("CASFilter doFilter(): authentication successful");
                        chain.doFilter(request, response);
                    }
                }
            }
        }
    }

    /**
     * Determines if request method is get or post and redirects browser to CAS accordingly.
     * 
     * @param hrequest
     * @param hresponse
     * @throws IOException
     */
    private void redirect(HttpServletRequest hrequest, HttpServletResponse hresponse) throws java.io.IOException {

        LOG.debug("CASFilter redirect(): Beginning Redirect");
        String method = hrequest.getMethod();
        if (method.equals("POST")) {
            sendPostRedirect(hrequest, hresponse);
        }
        else {
            sendGetRedirect(hrequest, hresponse);
        }
    }

    /**
     * redirects post request to CAS. Puts all params, including those on querystring, removes any bad casticket params. Didn't
     * preserve querystring because still boils down to requesting params, form or querystring based, through request
     * 
     * @param hrequest
     * @param hresponse
     * @throws IOException
     */
    private void sendPostRedirect(HttpServletRequest hrequest, HttpServletResponse hresponse) throws java.io.IOException {

        StringBuffer casURLBuf = hrequest.getRequestURL();
        String redirectURL = loginURL + "?" + serviceParamName + "=" + casURLBuf.toString();
        PrintWriter out = hresponse.getWriter();
        out.print(beginPostPage);
        out.print("<form action=\"" + redirectURL + "\" method=\"post\" name=\"f\">");

        /*
         * Preserve all request parameters in hidden form fields, page will send form to CAS with body onLoad dhtml event (see
         * String beginPostPage) Strip off any bad casticket coming our way
         */
        StringBuffer formParams = new StringBuffer();
        String parameterName;
        String[] parameterVals;
        Enumeration parameterEnum = hrequest.getParameterNames();
        while (parameterEnum.hasMoreElements()) {
            parameterName = (String) parameterEnum.nextElement();
            parameterVals = hrequest.getParameterValues(parameterName);
            if (!parameterName.equals(ticketParamName)) {
                for (int i = 0; i < parameterVals.length; i++) {
                    formParams.append("<input type=\"hidden\" name=\"");
                    formParams.append(parameterName);
                    formParams.append("\" value=\"");
                    formParams.append(parameterVals[i]);
                    formParams.append("\">\n");
                }
            }
        }
        LOG.debug("CASFilter sendPostRedirect(): Sending POST redirect");
        out.print(formParams.toString());
        out.print(endPostPage);
    }

    /**
     * redirects get request to CAS. Builds URL requested minus any casticket params
     * 
     * @param hrequest
     * @param hresponse
     * @throws IOException
     */
    private void sendGetRedirect(HttpServletRequest hrequest, HttpServletResponse hresponse) throws java.io.IOException {

        StringBuffer queryStringBuf = new StringBuffer();
        String[] values = null;
        String paramName = null;
        Enumeration paramEnum = hrequest.getParameterNames();
        int cnt = 0;
        // Strip off existing bad casticket if one exists
        // otherwise we'll always pick up the first one each time, which is bad
        // causing redirect loop
        while (paramEnum.hasMoreElements()) {
            paramName = (String) paramEnum.nextElement();
            if (!paramName.equals(ticketParamName)) {
                values = hrequest.getParameterValues(paramName);
                if (cnt > 0) {
                    queryStringBuf.append("&");
                }
                for (int i = 0; i < values.length; i++) {
                    if (i > 0) {
                        queryStringBuf.append("&");
                    }
                    queryStringBuf.append(paramName);
                    queryStringBuf.append("=");
                    queryStringBuf.append(values[i]);
                }
                cnt++;
            }
        }

        // build entire redirect url and send to client
        StringBuffer redirectURL = new StringBuffer();
        redirectURL.append(loginURL);
        redirectURL.append("?" + serviceParamName + "=" + hrequest.getRequestURL().toString());

        if (!StringUtils.isEmpty(queryStringBuf.toString())) {
            redirectURL.append("&");
            redirectURL.append(queryStringBuf.toString());
        }
        LOG.debug("CASFilter sendGetRedirect(): Sending GET redirect");
        hresponse.sendRedirect(redirectURL.toString());
    }

    /**
     * Open a stream using https to validate a CAS ticket against the service. Return a FilterCASBean set with username and any
     * key/value pairs returned by CAS. If connection fails or CAS returns "no" return null.
     * 
     * @param service The service in CAS this filter is set to validate against
     * @param ticket The ticket CAS puts as querystring parameter before redirecting browser back to this server
     * @exception IOException if an input/output error occurs
     * @return null if casticket invalid or FilterCASBean
     */
    private String validate(String service, String ticket) throws java.io.IOException {
        String result = null;

        String casValURL = validationURL + "?" + serviceParamName + "=" + service + "&" + ticketParamName + "=" + ticket;
        // TODO: if you have additional parameters, add them in here

        LOG.debug("CASFilter validate(): validate URL = " + casValURL);
        URL u = new URL(casValURL);
        BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream()));
        if (in != null) {
            String line1 = in.readLine();
            String line2 = in.readLine();

            if (!"no".equals(line1)) {
                result = line2;
            }

            try {
                in.close();
            }
            catch (IOException e) {
                LOG.error("caught IOException closing validation URL: " + e.getMessage());
            }
        }

        return result;
    }

    /**
     * This isn't needed for this application, but required to implement the Filter class.
     */
    public void destroy() {
        // this space intentionally left blank
    }


    public static String getRemoteUser(HttpServletRequest request) {
        String cassvc = (String) request.getAttribute(USERNAME_HASH_KEY);
        if (cassvc == null) {
            // Don't know what to do
            return null;
        }

        HashMap userList = (HashMap) request.getSession().getAttribute(USERNAME_HASH);

        if (userList == null) {
            // Again, Don't know what to do
            return null;
        }

        String username = (String) userList.get(cassvc);
        return username;
    }
}