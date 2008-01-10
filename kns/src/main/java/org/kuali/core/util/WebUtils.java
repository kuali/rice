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
package org.kuali.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServletWrapper;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.upload.MultipartRequestHandler;
import org.apache.struts.upload.MultipartRequestWrapper;
import org.apache.struts.util.ModuleUtils;
import org.kuali.RiceConstants;
import org.kuali.core.exceptions.FileUploadLimitExceededException;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.web.struts.form.KualiForm;

/**
 * General helper methods for handling requests.
 * 
 * 
 */
public class WebUtils {
    private static final Logger LOG = Logger.getLogger(WebUtils.class);

    /**
     * Checks for methodToCall parameter, and picks off the value using set dot notation. Handles the problem of image submits.
     * 
     * @param request
     * @return methodToCall String
     */
    public static String parseMethodToCall(HttpServletRequest request) {
        String methodToCall = null;

        // check if is specified cleanly
        if (StringUtils.isNotBlank(request.getParameter(RiceConstants.DISPATCH_REQUEST_PARAMETER))) {
            methodToCall = request.getParameter(RiceConstants.DISPATCH_REQUEST_PARAMETER);
        }

        if (methodToCall == null) {
            // iterate through parameters looking for methodToCall
            for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
                String parameterName = (String) i.nextElement();

                // check if the parameter name is a specifying the methodToCall
                if (parameterName.startsWith(RiceConstants.DISPATCH_REQUEST_PARAMETER) && parameterName.endsWith(".x")) {
                    methodToCall = StringUtils.substringBetween(parameterName, RiceConstants.DISPATCH_REQUEST_PARAMETER + ".", ".");
                    request.setAttribute(RiceConstants.METHOD_TO_CALL_ATTRIBUTE, parameterName);
                    // Fix for KRACOEUS-267, KULRICE-1412, KULRICE-1425, and KFSMI-110
                    // Add this to return the method to call once it is matched
                    break; 
                } else { 
                    // KULRICE-1218: Check if the parameter's values match (not just the name)
                    for (String value : request.getParameterValues(parameterName)) {
                        if (value.startsWith(RiceConstants.DISPATCH_REQUEST_PARAMETER) && value.endsWith(".x")) {
                            methodToCall = StringUtils.substringBetween(value, RiceConstants.DISPATCH_REQUEST_PARAMETER + ".", ".");
                            request.setAttribute(RiceConstants.METHOD_TO_CALL_ATTRIBUTE, value);
                        }
                    }
                }
            }
        }
        
        return methodToCall;
    }


    /**
     * Iterates through and logs (at the given level) all attributes and parameters of the given request onto the given Logger
     * 
     * @param request
     * @param logger
     */
    public static void logRequestContents(Logger logger, Level level, HttpServletRequest request) {
        if (logger.isEnabledFor(level)) {
            logger.log(level, "--------------------");
            logger.log(level, "HttpRequest attributes:");
            for (Enumeration e = request.getAttributeNames(); e.hasMoreElements();) {
                String attrName = (String) e.nextElement();
                Object attrValue = request.getAttribute(attrName);

                if (attrValue.getClass().isArray()) {
                    logCollection(logger, level, attrName, Arrays.asList((Object[]) attrValue));
                }
                else if (attrValue instanceof Collection) {
                    logCollection(logger, level, attrName, (Collection) attrValue);
                }
                else if (attrValue instanceof Map) {
                    logMap(logger, level, attrName, (Map) attrValue);
                }
                else {
                    logObject(logger, level, attrName, attrValue);
                }
            }

            logger.log(level, "--------------------");
            logger.log(level, "HttpRequest parameters:");
            for (Enumeration i = request.getParameterNames(); i.hasMoreElements();) {
                String paramName = (String) i.nextElement();
                String[] paramValues = (String[]) request.getParameterValues(paramName);

                logArray(logger, level, paramName, paramValues);
            }

            logger.log(level, "--------------------");
        }
    }


    private static void logArray(Logger logger, Level level, String arrayName, Object[] array) {
        StringBuffer value = new StringBuffer("[");
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) {
                value.append(",");
            }
            value.append(array[i]);
        }
        value.append("]");

        logThing(logger, level, arrayName, value);
    }

    private static void logCollection(Logger logger, Level level, String collectionName, Collection c) {
        StringBuffer value = new StringBuffer("{");
        for (Iterator i = c.iterator(); i.hasNext();) {
            value.append(i.next());
            if (i.hasNext()) {
                value.append(",");
            }
        }
        value.append("}");

        logThing(logger, level, collectionName, value);
    }

    private static void logMap(Logger logger, Level level, String mapName, Map m) {
        StringBuffer value = new StringBuffer("{");
        for (Iterator i = m.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            value.append("('" + e.getKey() + "','" + e.getValue() + "')");
        }
        value.append("}");

        logThing(logger, level, mapName, value);
    }

    private static void logObject(Logger logger, Level level, String objectName, Object o) {
        logThing(logger, level, objectName, "'" + o + "'");
    }

    private static void logThing(Logger logger, Level level, String thingName, Object thing) {
        logger.log(level, "    '" + thingName + "' => " + thing);
    }

    /**
     * A file that is not of type text/plain or text/html can be output through the response using this method.
     * 
     * @param response
     * @param contentType
     * @param outStream
     * @param fileName
     */
    public static void saveMimeOutputStreamAsFile(HttpServletResponse response, String contentType, ByteArrayOutputStream byteArrayOutputStream, String fileName) throws IOException {

        // set response
        response.setContentType(contentType);
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentLength(byteArrayOutputStream.size());

        // write to output
        OutputStream outputStream = response.getOutputStream();
        byteArrayOutputStream.writeTo(response.getOutputStream());
        outputStream.flush();
        outputStream.close();
    }

    /**
     * A file that is not of type text/plain or text/html can be output through the response using this method.
     * 
     * @param response
     * @param contentType
     * @param outStream
     * @param fileName
     */
    public static void saveMimeInputStreamAsFile(HttpServletResponse response, String contentType, InputStream inStream, String fileName, int fileSize) throws IOException {

        // set response
        response.setContentType(contentType);
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentLength(fileSize);

        // write to output
        OutputStream out = response.getOutputStream();
        while (inStream.available() > 0) {
            out.write(inStream.read());
        }
        out.flush();
    }
    
    /**
     * JSTL function to return the tab state of the tab from the form. 
     * 
     * @param form
     * @param tabKey
     * @return
     */
    public static String getTabState(KualiForm form, String tabKey) {
        return form.getTabState(tabKey);
    }
    
    public static void incrementTabIndex(KualiForm form, String tabKey) {
    	form.incrementTabIndex();
    }
    
    /**
     * Generates a String from the title that can be used as a Map key.
     * 
     * @param tabTitle
     * @return
     */
    public static String generateTabKey(String tabTitle) {
        String key = "";
        if (!StringUtils.isBlank(tabTitle)) {
            key = tabTitle.replaceAll("\\W", "");
//            if (key.length() > 25) {
//                key = key.substring(0, 24);
//            }
        }
        
        return key;
    }
    
    // start multipart - refactored to be shared by pojoformbase & kualirequestprocessor
    
    public static Map getMultipartParameters(HttpServletRequest request, ActionServletWrapper servletWrapper) {
        Map params = new HashMap();
        
        // Get the ActionServletWrapper from the form bean
        //ActionServletWrapper servletWrapper = getServletWrapper();
        boolean isMultipart = false;
        try {
            // Obtain a MultipartRequestHandler
            MultipartRequestHandler multipartHandler = getMultipartHandler(request);

            if (multipartHandler != null) {
                isMultipart = true;
                // Set servlet and mapping info
                if (servletWrapper != null) {
                    // from pojoformbase
                    // servlet only affects tempdir on local disk
                    servletWrapper.setServletFor(multipartHandler);
                }
                multipartHandler.setMapping((ActionMapping) request.getAttribute(Globals.MAPPING_KEY));
                // Initialize multipart request class handler
                multipartHandler.handleRequest(request);
                // stop here if the maximum length has been exceeded
                Boolean maxLengthExceeded = (Boolean) request.getAttribute(MultipartRequestHandler.ATTRIBUTE_MAX_LENGTH_EXCEEDED);
                if ((maxLengthExceeded != null) && (maxLengthExceeded.booleanValue())) {
                    throw new FileUploadLimitExceededException("");
                }
                // get file elements for kualirequestprocessor
                if (servletWrapper == null) {
                    request.setAttribute("fileElements",getFileParametersForMultipartRequest(request, multipartHandler));
                }
                // retrieve form values and put into properties
                Map multipartParameters = getAllParametersForMultipartRequest(request, multipartHandler);
                Enumeration names = Collections.enumeration(multipartParameters.keySet());
                while (names.hasMoreElements()) {
                    String name = (String) names.nextElement();
                    String stripped = name;
                    Object parameterValue = null;
                    if (isMultipart) {
                        parameterValue = multipartParameters.get(name);
                    }
                    else {
                        parameterValue = request.getParameterValues(name);
                    }

                    // Populate parameters, except "standard" struts attributes
                    // such as 'org.apache.struts.action.CANCEL'
                    if (!(stripped.startsWith("org.apache.struts."))) {
                        params.put(name, parameterValue);
                    }
                }
            }
        }
        catch (ServletException e) {
            throw new ValidationException("unable to handle multipart request " + e.getMessage());
        }
        return params;
    }
    
    private static MultipartRequestHandler getMultipartHandler(HttpServletRequest request) throws ServletException {
        Timer t0 = new Timer("PojoFormBase.getMultipartHandler");

        MultipartRequestHandler multipartHandler = null;
        String multipartClass = (String) request.getAttribute(Globals.MULTIPART_KEY);
        request.removeAttribute(Globals.MULTIPART_KEY);

        // Try to initialize the mapping specific request handler
        if (multipartClass != null) {
            try {
                multipartHandler = (MultipartRequestHandler) Thread.currentThread().getContextClassLoader().loadClass(multipartClass).newInstance();
            }
            catch (ClassNotFoundException cnfe) {
                LOG.error("MultipartRequestHandler class \"" + multipartClass + "\" in mapping class not found, " + "defaulting to global multipart class");
            }
            catch (InstantiationException ie) {
                LOG.error("InstantiationException when instantiating " + "MultipartRequestHandler \"" + multipartClass + "\", " + "defaulting to global multipart class, exception: " + ie.getMessage());
            }
            catch (IllegalAccessException iae) {
                LOG.error("IllegalAccessException when instantiating " + "MultipartRequestHandler \"" + multipartClass + "\", " + "defaulting to global multipart class, exception: " + iae.getMessage());
            }

            if (multipartHandler != null) {
                t0.log();
                return multipartHandler;
            }
        }

        ModuleConfig moduleConfig = ModuleUtils.getInstance().getModuleConfig(request);

        multipartClass = moduleConfig.getControllerConfig().getMultipartClass();

        // Try to initialize the global request handler
        if (multipartClass != null) {
            try {
                multipartHandler = (MultipartRequestHandler) Thread.currentThread().getContextClassLoader().loadClass(multipartClass).newInstance();

            }
            catch (ClassNotFoundException cnfe) {
                throw new ServletException("Cannot find multipart class \"" + multipartClass + "\"" + ", exception: " + cnfe.getMessage());

            }
            catch (InstantiationException ie) {
                throw new ServletException("InstantiationException when instantiating " + "multipart class \"" + multipartClass + "\", exception: " + ie.getMessage());

            }
            catch (IllegalAccessException iae) {
                throw new ServletException("IllegalAccessException when instantiating " + "multipart class \"" + multipartClass + "\", exception: " + iae.getMessage());
            }

            if (multipartHandler != null) {
                t0.log();
                return multipartHandler;
            }
        }

        t0.log();
        return multipartHandler;
    }

    /**
     * <p>
     * Create a <code>Map</code> containing all of the parameters supplied for a multipart request, keyed by parameter name. In
     * addition to text and file elements from the multipart body, query string parameters are included as well.
     * </p>
     *
     * @param request The (wrapped) HTTP request whose parameters are to be added to the map.
     * @param multipartHandler The multipart handler used to parse the request.
     *
     * @return the map containing all parameters for this multipart request.
     */
    private static Map getAllParametersForMultipartRequest(HttpServletRequest request, MultipartRequestHandler multipartHandler) {
        Timer t0 = new Timer("PojoFormBase.getAllParametersForMultipartRequest");

        Map parameters = new HashMap();
        Hashtable elements = multipartHandler.getAllElements();
        Enumeration e = elements.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            parameters.put(key, elements.get(key));
        }

        if (request instanceof MultipartRequestWrapper) {
            request = ((MultipartRequestWrapper) request).getRequest();
            e = request.getParameterNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                parameters.put(key, request.getParameterValues(key));
            }
        }
        else {
            LOG.debug("Gathering multipart parameters for unwrapped request");
        }

        t0.log();
        return parameters;
    }

    private static Map getFileParametersForMultipartRequest(HttpServletRequest request, MultipartRequestHandler multipartHandler) {
        Timer t0 = new Timer("PojoFormBase.getFileParametersForMultipartRequest");

        Map parameters = new HashMap();
        Hashtable elements = multipartHandler.getFileElements();
        Enumeration e = elements.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            parameters.put(key, elements.get(key));
        }

        if (request instanceof MultipartRequestWrapper) {
            request = ((MultipartRequestWrapper) request).getRequest();
            e = request.getParameterNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                parameters.put(key, request.getParameterValues(key));
            }
        }
        else {
            LOG.debug("Gathering multipart parameters for unwrapped request");
        }

        t0.log();
        return parameters;
    }


    // end multipart
}
