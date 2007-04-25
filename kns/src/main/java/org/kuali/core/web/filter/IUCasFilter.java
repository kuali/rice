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

import org.apache.log4j.Logger;

/**
 * This class is the Kuali Group Dao interface default implementation
 * 
 * 
 */

public class IUCasFilter implements Filter {
    private static final Logger LOG = Logger.getLogger(IUCasFilter.class);

    public static final String USERNAME_HASH = "edu.iu.uis.cas.filter.UsernameHash";

    public static final String REQUEST_APPLICATION_CODE = "edu.iu.uis.cas.filter.ApplicationCode";

    private String validationURL;

    private String loginURL;

    private String cassvc;

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
        cassvc = filterConfig.getInitParameter("cassvc");
        validationURL = filterConfig.getInitParameter("validationURL") + "?cassvc=" + cassvc;
        loginURL = filterConfig.getInitParameter("loginURL");
        beginPostPage = "<html><head><script>\n" + "function pf(){\n" + "\tdocument.f.submit();\n}" + "\n</script>\n</head>" + "<title>Auth Redirect</title>" + "<body onload=\"pf()\">\n";
        endPostPage = "<input type='submit'></form></body></html>";
        log("IUCasFilter: init() ValidationURL: " + validationURL + "\nLogin URL: " + loginURL + "\nCAS Service: " + cassvc);
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

        // If the username list has the cassvc they've been authenticated just
        // pass request on
        if (userList.get(cassvc) != null) {
            log("CASFilter doFilter(): Already Authenticated");
            request.setAttribute(REQUEST_APPLICATION_CODE, cassvc);
            chain.doFilter(request, response);
        }
        else {
            String casticket = hrequest.getParameter("casticket");
            log("CASFilter doFilter(): casticket = " + casticket);
            if (casticket == null) {
                // user hasn't been to CAS yet redirect them
                log("CASFilter doFilter(): no casticket redirecting browser to CAS Server");
                redirect(hrequest, (HttpServletResponse) response);
            }
            else {
                // user has been to CAS but casticket hasn't been verified,
                // otherwise
                // we'd have filterCASBean in session
                log("CASFilter doFilter(): casticket exists verifying it");
                String username = null;
                try {
                    username = validate(cassvc, casticket);
                }
                catch (IOException ex) {
                    log("CASFilter doFilter(): Error talking to validation server");
                    username = null;
                }
                if (username == null) {
                    // failed validation, bad casticket, user is going back to
                    // CAS to login
                    // and get new casticket
                    log("CASFilter doFilter(): casticket invalid redirect to browser");
                    log("CASFilter doFilter(): query_string = " + hrequest.getQueryString());
                    redirect(hrequest, (HttpServletResponse) response);
                }
                else {
                    // user's casticket verified as good, adding to username
                    // list
                    // and passing request on
                    userList.put(cassvc, username);
                    request.setAttribute(REQUEST_APPLICATION_CODE, cassvc);
                    log("CASFilter doFilter(): username = " + username);
                    log("CASFilter doFilter(): authentication successful");
                    chain.doFilter(request, response);
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

        log("CASFilter redirect(): Beginning Redirect");
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
        String redirectURL;
        // put finished redirect url together
        redirectURL = loginURL + "?cassvc=" + cassvc + "&casurl=" + casURLBuf.toString();
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
            if (!parameterName.equals("casticket")) {
                for (int i = 0; i < parameterVals.length; i++) {
                    formParams.append("<input type=\"hidden\" name=\"");
                    formParams.append(parameterName);
                    formParams.append("\" value=\"");
                    formParams.append(parameterVals[i]);
                    formParams.append("\">\n");
                }
            }
        }
        log("CASFilter sendPostRedirect(): Sending POST redirect");
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
            if (!paramName.equals("casticket")) {
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
        redirectURL.append("?cassvc=");
        redirectURL.append(cassvc);
        redirectURL.append("&casurl=");
        redirectURL.append(hrequest.getRequestURL().toString());
        redirectURL.append("?");
        redirectURL.append(queryStringBuf.toString());
        log("CASFilter sendGetRedirect(): Sending GET redirect");
        hresponse.sendRedirect(redirectURL.toString());
    }

    /**
     * Open a stream using https to validate a CAS ticket against the service. Return a FilterCASBean set with username and any
     * key/value pairs returned by CAS. If connection fails or CAS returns "no" return null.
     * 
     * @param service The service in CAS this filter is set to validate against
     * @param ticket The ticket CAS puts as querystring parameter before redirecting browser back to this server
     * 
     * @exception IOException if an input/output error occurs
     * @return null if casticket invalid or FilterCASBean
     */
    private String validate(String service, String ticket) throws java.io.IOException {

        String casValURL = validationURL + "&casticket=" + ticket;
        log("CASFilter validate(): validate URL = " + casValURL);
        URL u = new URL(casValURL);
        BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream()));
        if (in == null) {
            return null;
        }
        else {
            String line1 = in.readLine();
            String line2 = in.readLine();
            if (line1.equals("no")) {
                return null;
            }
            else {
                return line2;
            }
        }
    }

    /**
     * This isn't needed for this application, but required to implement the Filter class.
     */
    public void destroy() {
    }

    /**
     * Handle all the logging messages. Right now, they aren't printed, but at some point in the future, they could be.
     * 
     * @param o The object to print out
     */
    private void log(Object o) {
        LOG.error(o);
    }

    public static String getRemoteUser(HttpServletRequest request) {
        String cassvc = (String) request.getAttribute(REQUEST_APPLICATION_CODE);
        if (cassvc == null) {
            return null;
        }

        HashMap userList = (HashMap) request.getSession().getAttribute(USERNAME_HASH);

        if (userList == null) {
            return null;
        }

        String username = (String) userList.get(cassvc);
        return username;
    }
}