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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

/**
 * This class Provides utility methods for re/building URLs.
 * 
 * 
 */

public class UrlFactory {
    /**
     * Creates a new URL by taking the given URL and appending the parameter names and values from the given Properties instance to
     * it. Note: parameter names must be non-blank; parameter values must be non-null.
     * 
     * @param baseUrl the URL string used as the basis for reconstruction
     * @param params Properties instance containing the desired parameters and their values
     * @throws IllegalArgumentException if the given url is null or empty
     * @throws IllegalArgumentException if the given Properties instance is null
     * @throws IllegalArgumentException if a parameter name is null or empty, or a parameter value is null
     * @throws RuntimeException if there is a problem encoding a parameter name or value into UTF-8
     * @return a newly-constructed URL string which has the given parameters and their values appended to it
     */
    public static String parameterizeUrl(String baseUrl, Properties params) {
        baseUrl = StringUtils.trim(baseUrl);
        if (StringUtils.isEmpty(baseUrl)) {
            throw new IllegalArgumentException("invalid (blank) base URL");
        }
        if (params == null) {
            throw new IllegalArgumentException("invalid (null) Properties");
        }


        StringBuffer ret = new StringBuffer(baseUrl);

        if (params.size() > 0) {
            Iterator i = params.keySet().iterator();
            String paramName = (String) i.next();
            ret.append(pathEntry("?", paramName, params.getProperty(paramName)));

            while (i.hasNext()) {
                paramName = (String) i.next();
                ret.append(pathEntry("&", paramName, params.getProperty(paramName)));
            }
        }

        return ret.toString();
    }

    private static String pathEntry(String separator, String paramName, String paramValue) {
        paramName = StringUtils.trim(paramName);
        if (StringUtils.isEmpty(paramName)) {
            throw new IllegalArgumentException("invalid (blank) paramName");
        }
        if (paramValue == null) {
            throw new IllegalArgumentException("invalid (null) paramValue");
        }


        StringBuffer entry = new StringBuffer(separator);

        entry.append(encode(paramName));
        entry.append("=");
        entry.append(encode(paramValue));

        return entry.toString();
    }

    public static String encode(String raw) {
        String enc = null;
        try {
            enc = URLEncoder.encode(raw, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            // this should never happen
            throw new RuntimeException(e);
        }

        return enc;
    }


    /**
     * Constructs a document action URL from the given documentTypeName, with the given Properties as URL parameters.
     * 
     * @param documentTypeName
     * @param urlParams
     * @return document action URL
     */
    public static String buildDocumentActionUrl(String documentTypeName, Properties urlParams) {
        if (StringUtils.isBlank(documentTypeName)) {
            throw new IllegalArgumentException("invalid (blank) documentTypeName");
        }

        if (!documentTypeName.startsWith("Kuali")) {
            throw new IllegalArgumentException("documentTypeName '" + documentTypeName + "' doesn't start with the literal string 'Kuali'");
        }
        if (!documentTypeName.endsWith("Document")) {
            throw new IllegalArgumentException("documentTypeName '" + documentTypeName + "' doesn't end with the literal string 'Document'");
        }

        String actionBase = "financial" + StringUtils.substringBetween(documentTypeName, "Kuali", "Document") + ".do";
        String actionUrl = parameterizeUrl(actionBase, urlParams);

        return actionUrl;
    }
}