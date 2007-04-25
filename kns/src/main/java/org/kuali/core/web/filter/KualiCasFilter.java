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
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * This class is to do cas filtering for the kuali application
 * 
 * 
 */
public class KualiCasFilter implements Filter {

    private static Logger LOG = Logger.getLogger(KualiCasFilter.class);

    private static final String USERNAME_HASH = "org.kuali.web.filter.UsernameHash";

    private static final String PRE_CAS_REDIRECT_PARAMS = "org.kuali.request.cas-parameters";

    private static final String CAS_SERVICE = "KUALI";

    private String validationURL;

    private String loginURL;

    /**
     * during init we set the ssl handler, validation url, and login url
     * 
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        validationURL = filterConfig.getInitParameter("edu.yale.its.tp.cas.client.filter.validateUrl");
        loginURL = filterConfig.getInitParameter("edu.yale.its.tp.cas.client.filter.loginUrl");
    }

    /**
     * In this method, we check if the user has been authenticated already and if they have then we let the request through,
     * otherwise we check for a cas ticket as a request parameter, if it is not there then we redirect to cas for login. If it is
     * there then we attempt to validate the ticket. If the ticket is valid then we redirect them back to thier originating request,
     * otherwise we redirect them back to CAS again.
     * 
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest hrequest = (HttpServletRequest) request;
        HttpServletResponse hresponse = (HttpServletResponse) response;
        HttpSession session = hrequest.getSession();

        HashMap userList = (HashMap) session.getAttribute(USERNAME_HASH);

        if (userList == null) {
            userList = new HashMap();
            session.setAttribute(USERNAME_HASH, userList);
        }

        if (userList.get(CAS_SERVICE) != null) {
            chain.doFilter(request, response);
        }
        else {
            String ticket = hrequest.getParameter("ticket");
            if (ticket == null) {
                saveRequestState(hrequest, getRequestStateMap(hrequest));
                redirectToCas(hrequest, hresponse);
            }
            else {
                String username = null;
                try {
                    username = validate(hrequest, ticket);
                }
                catch (IOException ex) {
                    username = null;
                }
                if (username == null) {
                    saveRequestState(hrequest, getRequestStateMap(hrequest));
                    redirectToCas(hrequest, hresponse);
                }
                else {
                    userList.put(CAS_SERVICE, username);
                    Map savedRequestState = getSavedRequestState(hrequest);
                    clearSavedRequestState(hrequest);
                    redirectBackToOriginalRequest(hrequest, savedRequestState, hresponse);
                }
            }
        }
    }

    /**
     * Redirects browser to CAS
     * 
     * @param hrequest
     * @param hresponse
     * @throws IOException
     */
    private void redirectToCas(HttpServletRequest hrequest, HttpServletResponse hresponse) throws java.io.IOException {
        LOG.info("redirecting to cas: " + loginURL + "?service=" + hrequest.getRequestURL().toString());
        hresponse.sendRedirect(hresponse.encodeRedirectURL(loginURL + "?service=" + hrequest.getRequestURL().toString()));
    }

    /**
     * This method uses a form to reinstate the orignal request, with noscript tags for users who do not use javascript, and with an
     * automatic post for those who do have javascript enabled.
     * 
     * @param hrequest
     * @param hresponse
     * @throws java.io.IOException
     */
    private void redirectBackToOriginalRequest(HttpServletRequest hrequest, Map originalRequestParameterMap, HttpServletResponse hresponse) throws java.io.IOException {
        LOG.info("redirecting back to original request: " + hrequest.getRequestURL().toString());
        hresponse.setContentType("text/html");
        hresponse.setStatus(HttpServletResponse.SC_OK);
        hresponse.getOutputStream().println("<html><head><title>Session Timeout Recovery Page</title></head><body onload=\"document.forms[0].submit()\"><form method=\"POST\" action=\"" + hrequest.getRequestURL().toString() + "\" >" + generateHiddenInputFields(originalRequestParameterMap) + "<noscript>Session Expired Click Here to Resume<input type=\"SUBMIT\" value=\"RESUME\"></noscript></form></body></html>");
        hresponse.flushBuffer();
    }

    /**
     * This method saves of the request state in a known variable
     * 
     * @param hrequest
     * @param parameters
     */
    private void saveRequestState(HttpServletRequest hrequest, Map parameters) {
        hrequest.getSession().setAttribute(PRE_CAS_REDIRECT_PARAMS, parameters);
    }

    /**
     * This method gets the saved request state from a known variable
     * 
     * @param hrequest
     * @param parameters
     */
    private Map getSavedRequestState(HttpServletRequest hrequest) {
        return (Map) hrequest.getSession().getAttribute(PRE_CAS_REDIRECT_PARAMS);
    }

    /**
     * This method clears the saved request state from the known variable
     * 
     * @param hrequest
     */
    private void clearSavedRequestState(HttpServletRequest hrequest) {
        hrequest.getSession().removeAttribute(PRE_CAS_REDIRECT_PARAMS);
    }

    /**
     * This method will look at the request and generate a map of all of the parameters
     * 
     * @param hrequest
     * @return
     */
    private Map getRequestStateMap(HttpServletRequest hrequest) {
        Map parameters = null;
        try {
            if (FileUpload.isMultipartContent(hrequest)) {
                DiskFileUpload upload = new DiskFileUpload();
                List items = upload.parseRequest(hrequest);
                parameters = new HashMap();
                Iterator iter = items.iterator();
                while (iter.hasNext()) {
                    FileItem item = (FileItem) iter.next();
                    if (item.isFormField()) {
                        String name = item.getFieldName();
                        String value = item.getString();
                        if (parameters.containsKey(name)) {
                            // then we add to the string array
                            String[] newValues = new String[((String[]) parameters.get(name)).length];
                            for (int i = 0; i < ((String[]) parameters.get(name)).length; i++) {
                                newValues[i] = ((String[]) parameters.get(name))[i];
                            }
                            newValues[newValues.length - 1] = value;
                            parameters.put(name, newValues);
                        }
                        else {
                            parameters.put(name, new String[] { value });
                        }
                    }
                    else {
                        // TODO drop these for now -- may want to display a message to the user that there
                        // file was not uploaded due to thier timeout, if we get into this set of code
                    }
                }
                if (parameters.containsKey("ticket")) {
                    parameters.remove("ticket");
                }
            }
            else {
                parameters = new HashMap();
                if (hrequest.getQueryString() != null && hrequest.getQueryString().indexOf("channelUrl") >= 0) {
                    parameters.put("channelUrl", new String[] { hrequest.getQueryString().substring(hrequest.getQueryString().indexOf("channelUrl") + 11, hrequest.getQueryString().length()) });
                    parameters.put("channelTitle", new String[] { hrequest.getParameter("channelTitle") });
                }
                else {
                    String parameterName;
                    String[] parameterVals;
                    Enumeration parameterEnum = hrequest.getParameterNames();
                    while (parameterEnum.hasMoreElements()) {
                        parameterName = (String) parameterEnum.nextElement();
                        parameterVals = hrequest.getParameterValues(parameterName);
                        if (!parameterName.equals("ticket")) {
                            parameters.put(parameterName, parameterVals);
                        }
                    }
                }
            }
        }
        catch (FileUploadException e) {
            LOG.error("Error caught while getting parameters to save off before sending to CAS", e);
        }
        return parameters;
    }

    /**
     * This method will generate a query string rep for a map of String Arrays
     * 
     * @param requestParameterMap
     * @return
     */
    private String generateHiddenInputFields(Map requestParameterMap) {
        StringBuffer hiddenInputFieldString = new StringBuffer();
        for (Iterator iter = requestParameterMap.entrySet().iterator(); iter.hasNext();) {
            Map.Entry element = (Map.Entry) iter.next();
            if (element.getValue() != null) {
                String[] values = (String[]) element.getValue();
                for (int i = 0; i < values.length; i++) {
                    hiddenInputFieldString.append("<input type=\"hidden\" name=\"" + element.getKey() + "\" value=\"" + values[i] + "\">");
                }
            }
        }
        return hiddenInputFieldString.toString();
    }

    /**
     * This method will validate a cas ticket
     * 
     * @param hrequest
     * @param ticket
     * @return
     * @throws java.io.IOException
     */
    private String validate(HttpServletRequest hrequest, String ticket) throws java.io.IOException {
        URL url = new URL(validationURL + "?ticket=" + ticket + "&service=" + hrequest.getRequestURL().toString());
        return StringUtils.substringBetween(getFullTextResponseForUrlRequest(url), "<cas:user>", "</cas:user>");
    }

    /**
     * This method reads the response from a url request, and builds up a string representation of it then returns it to the caller
     * 
     * @param url
     * @return
     * @throws IOException
     */
    private String getFullTextResponseForUrlRequest(URL url) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String textLine = "";
        String fullText = "";
        while ((textLine = in.readLine()) != null) {
            fullText += textLine;
        }
        try {
            in.close();
        }
        catch (IOException e) {
            LOG.error("caught exception closing response URL: " + e.getMessage());
        }
        return fullText;
    }

    /**
     * This method will return the network id used to authenticate to cas
     * 
     * @param request
     * @return
     */
    public static String getRemoteUser(HttpServletRequest request) {
        HashMap userList = (HashMap) request.getSession().getAttribute(USERNAME_HASH);
        if (userList == null) {
            return null;
        }
        LOG.info("getRemoteUser returning: " + (String) userList.get(CAS_SERVICE));
        return (String) userList.get(CAS_SERVICE);
    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        LOG.info("Shutting down cas filter");
    }

}
