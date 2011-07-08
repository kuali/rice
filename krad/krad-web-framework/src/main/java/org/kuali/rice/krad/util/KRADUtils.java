/*
 * Copyright 2007-2009 The Kuali Foundation
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
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.core.framework.parameter.ParameterConstants;
import org.kuali.rice.core.framework.parameter.ParameterService;
import org.kuali.rice.core.framework.services.CoreFrameworkServiceLocator;
import org.kuali.rice.core.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.BooleanFormatter;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Constructor;
import java.security.GeneralSecurityException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Miscellaneous Utility Methods.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class KRADUtils {
    private static KualiModuleService kualiModuleService;

    private KRADUtils() {
        throw new UnsupportedOperationException("do not call");
    }

    public final static String getBusinessTitleForClass(Class<? extends Object> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException(
                    "The getBusinessTitleForClass method of KRADUtils requires a non-null class");
        }
        String className = clazz.getSimpleName();

        StringBuffer label = new StringBuffer(className.substring(0, 1));
        for (int i = 1; i < className.length(); i++) {
            if (Character.isLowerCase(className.charAt(i))) {
                label.append(className.charAt(i));
            } else {
                label.append(" ").append(className.charAt(i));
            }
        }
        return label.toString().trim();
    }

    /**
     * Picks off the filename from the full path. Takes care of different OS seperators.
     */
    public final static List<String> getFileNameFromPath(List<String> fullFileNames) {
        List<String> fileNameList = new ArrayList<String>();

        for (String fullFileName : fullFileNames) {
            if (StringUtils.contains(fullFileName, "/")) {
                fileNameList.add(StringUtils.substringAfterLast(fullFileName, "/"));
            } else {
                fileNameList.add(StringUtils.substringAfterLast(fullFileName, "\\"));
            }
        }

        return fileNameList;
    }

    private static final KualiDecimal ONE_HUNDRED = new KualiDecimal("100.00");

    /**
     * Convert the given monney amount into an interger string. Since the return string cannot have decimal point,
     * multiplies the
     * amount by 100 so the decimal places are not lost, for example, 320.15 is converted into 32015.
     *
     * @return an integer string of the given money amount through multiplicating by 100 and removing the fraction
     *         portion.
     */
    public final static String convertDecimalIntoInteger(KualiDecimal decimalNumber) {
        KualiDecimal decimalAmount = decimalNumber.multiply(ONE_HUNDRED);
        NumberFormat formatter = NumberFormat.getIntegerInstance();
        String formattedAmount = formatter.format(decimalAmount);

        return StringUtils.replace(formattedAmount, ",", "");
    }

    public static Integer getIntegerValue(String numberStr) {
        Integer numberInt = null;
        try {
            numberInt = new Integer(numberStr);
        } catch (NumberFormatException nfe) {
            Double numberDbl = new Double(numberStr);
            numberInt = new Integer(numberDbl.intValue());
        }
        return numberInt;
    }

    public static Object createObject(Class<?> clazz, Class<?>[] argumentClasses, Object[] argumentValues) {
        if (clazz == null)
            return null;
        try {
            Constructor<?> constructor = clazz.getConstructor(argumentClasses);
            return constructor.newInstance(argumentValues);
        } catch (Exception e) {
            return null;
        }
    }

    public static String joinWithQuotes(List<String> list) {
        if (list == null || list.size() == 0)
            return "";

        return KRADConstants.SINGLE_QUOTE +
                StringUtils.join(list.iterator(), KRADConstants.SINGLE_QUOTE + "," + KRADConstants.SINGLE_QUOTE) +
                KRADConstants.SINGLE_QUOTE;
    }

    private static KualiModuleService getKualiModuleService() {
        if (kualiModuleService == null) {
            kualiModuleService = KRADServiceLocatorWeb.getKualiModuleService();
        }
        return kualiModuleService;
    }

    /**
     * TODO this method will probably need to be exposed in a public KRADUtils class as it is used
     * by several different modules.  That will have to wait until ModuleService and KualiModuleService are moved
     * to core though.
     */
    public static String getNamespaceCode(Class<? extends Object> clazz) {
        ModuleService moduleService = getKualiModuleService().getResponsibleModuleService(clazz);
        if (moduleService == null) {
            return KRADConstants.DEFAULT_NAMESPACE;
        }
        return moduleService.getModuleConfiguration().getNamespaceCode();
    }

    public static Map<String, String> getNamespaceAndComponentSimpleName(Class<? extends Object> clazz) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(KRADConstants.NAMESPACE_CODE, getNamespaceCode(clazz));
        map.put(KRADConstants.COMPONENT_NAME, getComponentSimpleName(clazz));
        return map;
    }

    public static Map<String, String> getNamespaceAndComponentFullName(Class<? extends Object> clazz) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(KRADConstants.NAMESPACE_CODE, getNamespaceCode(clazz));
        map.put(KRADConstants.COMPONENT_NAME, getComponentFullName(clazz));
        return map;
    }

    public static Map<String, String> getNamespaceAndActionClass(Class<? extends Object> clazz) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(KRADConstants.NAMESPACE_CODE, getNamespaceCode(clazz));
        map.put(KRADConstants.ACTION_CLASS, clazz.getName());
        return map;
    }

    private static String getComponentSimpleName(Class<? extends Object> clazz) {
        return clazz.getSimpleName();
    }

    private static String getComponentFullName(Class<? extends Object> clazz) {
        return clazz.getName();
    }

    /**
     * Parses a string that is in map format (commas separating map entries, colon separates
     * map key/value) to a new map instance
     *
     * @param parameter - string parameter to parse
     * @return Map<String, String> instance populated from string parameter
     */
    public static Map<String, String> convertStringParameterToMap(String parameter) {
        Map<String, String> map = new HashMap<String, String>();

        if (StringUtils.isNotBlank(parameter)) {
            if (StringUtils.contains(parameter, ",")) {
                String[] fieldConversions = StringUtils.split(parameter, ",");

                for (int i = 0; i < fieldConversions.length; i++) {
                    String fieldConversionStr = fieldConversions[i];
                    if (StringUtils.isNotBlank(fieldConversionStr)) {
                        if (StringUtils.contains(fieldConversionStr, ":")) {
                            String[] fieldConversion = StringUtils.split(fieldConversionStr, ":");
                            map.put(fieldConversion[0], fieldConversion[1]);
                        } else {
                            map.put(fieldConversionStr, fieldConversionStr);
                        }
                    }
                }
            } else if (StringUtils.contains(parameter, ":")) {
                String[] fieldConversion = StringUtils.split(parameter, ":");
                map.put(fieldConversion[0], fieldConversion[1]);
            } else {
                map.put(parameter, parameter);
            }
        }

        return map;
    }

    /**
     * Parses a string that is in list format (commas separating list entries) to a new List instance
     *
     * @param parameter - string parameter to parse
     * @return List<String> instance populated from string parameter
     */
    public static List<String> convertStringParameterToList(String parameter) {
        List<String> list = new ArrayList<String>();

        if (StringUtils.isNotBlank(parameter)) {
            if (StringUtils.contains(parameter, ",")) {
                String[] parameters = StringUtils.split(parameter, ",");
                list = Arrays.asList(parameters);
            } else {
                list.add(parameter);
            }
        }

        return list;
    }

    /**
     * Translates characters in the given string like brackets that will cause
     * problems with binding to characters that do not affect the binding
     *
     * @param key - string to translate
     * @return String translated string
     */
    public static String translateToMapSafeKey(String key) {
        String safeKey = key;

        safeKey = StringUtils.replace(safeKey, "[", "_");
        safeKey = StringUtils.replace(safeKey, "]", "_");

        return safeKey;
    }

    /**
     * Builds a string from the given map by joining each entry with a comma and
     * each key/value pair with a colon
     *
     * @param map - map instance to build string for
     * @return String of map entries
     */
    public static String buildMapParameterString(Map<String, String> map) {
        String parameterString = "";

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (StringUtils.isNotBlank(parameterString)) {
                parameterString += ",";
            }

            parameterString += entry.getKey() + ":" + entry.getValue();
        }

        return parameterString;
    }

    /**
     * Parses the given string into a Map by splitting on the comma to get the
     * map entries and within each entry splitting by colon to get the key/value
     * pairs
     *
     * @param parameterString - string to parse into map
     * @return Map<String, String> map from string
     */
    public static Map<String, String> getMapFromParameterString(String parameterString) {
        Map<String, String> map = new HashMap<String, String>();

        String[] entries = parameterString.split(",");
        for (int i = 0; i < entries.length; i++) {
            String[] keyValue = entries[i].split(":");
            if (keyValue.length != 2) {
                throw new RuntimeException("malformed field conversion pair: " + Arrays.toString(keyValue));
            }

            map.put(keyValue[0], keyValue[1]);
        }

        return map;
    }

    /**
     * Retrieves value for the given parameter name in the request and attempts to convert to a Boolean using
     * the <code>BooleanFormatter</code>
     *
     * @param request - servlet request containing parameters
     * @param parameterName - name of parameter to retrieve value for
     * @return Boolean set to value of parameter, or null if parameter was not found in request
     */
    public static Boolean getRequestParameterAsBoolean(ServletRequest request, String parameterName) {
        Boolean parameterValue = null;

        String parameterValueStr = request.getParameter(parameterName);
        if (StringUtils.isNotBlank(parameterValueStr)) {
            parameterValue = (Boolean) new BooleanFormatter().convertFromPresentationFormat(parameterValueStr);
        }

        return parameterValue;
    }

    /**
     * Translates the given Map of String keys and String array values to a Map
     * of String key and values. If the String array contains more than one
     * value, the single string is built by joining the values with the vertical
     * bar character
     *
     * @param requestParameters - Map of request parameters to translate
     * @return Map<String, String> translated Map
     */
    public static Map<String, String> translateRequestParameterMap(Map<String, String[]> requestParameters) {
        Map<String, String> parameters = new HashMap<String, String>();

        for (Map.Entry<String, String[]> parameter : requestParameters.entrySet()) {
            String parameterValue = "";
            if (parameter.getValue().length > 1) {
                parameterValue = StringUtils.join(parameter.getValue(), "|");
            } else {
                parameterValue = parameter.getValue()[0];
            }
            parameters.put(parameter.getKey(), parameterValue);
        }

        return parameters;
    }

    /**
     * Retrieves parameter values from the request that match the requested
     * names. In addition, based on the object class an authorization check is
     * performed to determine if the values are secure and should be decrypted.
     * If true, the value is decrypted before returning
     *
     * @param parameterNames - names of the parameters whose values should be retrieved
     * from the request
     * @param parentObjectClass - object class that contains the parameter names as properties
     * and should be consulted for security checks
     * @param requestParameters - all request parameters to pull from
     * @return Map<String, String> populated with parameter name/value pairs
     *         pulled from the request
     */
    public static Map<String, String> getParametersFromRequest(List<String> parameterNames, Class<?> parentObjectClass,
            Map<String, String> requestParameters) {
        Map<String, String> parameterValues = new HashMap<String, String>();

        for (Iterator<String> iter = parameterNames.iterator(); iter.hasNext(); ) {
            String keyPropertyName = iter.next();

            if (requestParameters.get(keyPropertyName) != null) {
                String keyValue = requestParameters.get(keyPropertyName);

                // Check if this element was encrypted, if it was decrypt it
                if (KRADServiceLocatorWeb.getDataObjectAuthorizationService()
                        .attributeValueNeedsToBeEncryptedOnFormsAndLinks(parentObjectClass, keyPropertyName)) {
                    try {
                        keyValue = StringUtils.removeEnd(keyValue, EncryptionService.ENCRYPTION_POST_PREFIX);
                        keyValue = CoreApiServiceLocator.getEncryptionService().decrypt(keyValue);
                    } catch (GeneralSecurityException e) {
                        throw new RuntimeException(e);
                    }
                }

                parameterValues.put(keyPropertyName, keyValue);
            }
        }

        return parameterValues;
    }

    public static boolean containsSensitiveDataPatternMatch(String fieldValue) {
        if (StringUtils.isBlank(fieldValue)) {
            return false;
        }
        ParameterService parameterService = CoreFrameworkServiceLocator.getParameterService();
        Collection<String> sensitiveDataPatterns = parameterService
                .getParameterValuesAsString(KRADConstants.KRAD_NAMESPACE, ParameterConstants.ALL_COMPONENT,
                        KRADConstants.SystemGroupParameterNames.SENSITIVE_DATA_PATTERNS);
        for (String pattern : sensitiveDataPatterns) {
            if (Pattern.compile(pattern).matcher(fieldValue).find()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the UserSession object from the HttpServletRequest object's
     * associated session.
     *
     * <p>
     * In some cases (different threads) the UserSession cannot be retrieved
     * from GlobalVariables but can still be accessed via the session object
     * </p>
     */
    public static final UserSession getUserSessionFromRequest(HttpServletRequest request) {
        return (UserSession) request.getSession().getAttribute(KRADConstants.USER_SESSION_KEY);
    }

    /**
     * @return whether the deploy environment is production
     */
    public static boolean isProductionEnvironment() {
        return KRADServiceLocator.getKualiConfigurationService().isProductionEnvironment();
    }
}
