/**
 * Copyright 2005-2014 The Kuali Foundation
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

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.Truth;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.TypeUtils;
import org.kuali.rice.core.web.format.BooleanFormatter;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.messages.MessageService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.component.ClientSideState;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.element.Action;
import org.kuali.rice.krad.uif.element.Image;
import org.kuali.rice.krad.uif.element.Link;
import org.kuali.rice.krad.uif.field.ActionField;
import org.kuali.rice.krad.uif.field.DataField;
import org.kuali.rice.krad.uif.field.Field;
import org.kuali.rice.krad.uif.field.FieldGroup;
import org.kuali.rice.krad.uif.field.ImageField;
import org.kuali.rice.krad.uif.field.LinkField;
import org.kuali.rice.krad.uif.field.MessageField;
import org.kuali.rice.krad.uif.field.SpaceField;
import org.kuali.rice.krad.uif.util.CopyUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ViewModelUtils;
import org.kuali.rice.krad.uif.view.ExpressionEvaluator;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;

/**
 * Miscellaneous Utility Methods.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class KRADUtils {
    private static final Logger LOG = Logger.getLogger(KRADUtils.class);

    private static KualiModuleService kualiModuleService;
    private static final KualiDecimal ONE_HUNDRED = new KualiDecimal("100.00");

    /**
     * Prevent instantiation of the class.
     */
    private KRADUtils() {
        throw new UnsupportedOperationException("do not call");
    }

    /**
     * Retrieve the title for a business object class
     *
     * <p>
     * The title is a nicely formatted version of the simple class name.
     * </p>
     *
     * @param clazz business object class
     * @return title of the business object class
     */
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
     * Picks off the filename from the full path
     *
     * <p>
     * The different OS path separators are being taken into consideration.
     * </p>
     *
     * @param fullFileNames file name with path
     * @return file name
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

    /**
     * Convert the given money amount into an integer string.
     *
     * <p>
     * Since the return string cannot have decimal point, multiplies the amount by 100 so the decimal places
     * are not lost, for example, 320.15 is converted into 32015.
     * </p>
     *
     * @param decimalNumber decimal number to be converted
     * @return an integer string of the given money amount through multiplying by 100 and removing the fraction
     * portion.
     */
    public final static String convertDecimalIntoInteger(KualiDecimal decimalNumber) {
        KualiDecimal decimalAmount = decimalNumber.multiply(ONE_HUNDRED);
        NumberFormat formatter = NumberFormat.getIntegerInstance();
        String formattedAmount = formatter.format(decimalAmount);

        return StringUtils.replace(formattedAmount, ",", "");
    }

    /**
     * Return the integer value of a string
     *
     * <p>
     * If the string contains a decimal value everything after the decimal point is dropped.
     * </p>
     *
     * @param numberStr string
     * @return integer representation of the given string
     */
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

    /**
     * Attempt to coerce a String attribute value to the given propertyType.  If the transformation can't be made,
     * either because the propertyType is null or because the transformation required exceeds this method's very small
     * bag of tricks, then null is returned.
     *
     * @param propertyType the Class to coerce the attributeValue to
     * @param attributeValue the String value to coerce
     * @return an instance of the propertyType class, or null the transformation can't be made.
     */
    public static Object hydrateAttributeValue(Class<?> propertyType, String attributeValue) {
        Object attributeValueObject = null;
        if (propertyType != null && attributeValue != null) {
            if (String.class.equals(propertyType)) {
                // it's already a String
                attributeValueObject = attributeValue;
            } // KULRICE-6808: Kim Role Maintenance - Custom boolean role qualifier values are not being converted properly
            else if (Boolean.class.equals(propertyType) || Boolean.TYPE.equals(propertyType)) {
                attributeValueObject = Truth.strToBooleanIgnoreCase(attributeValue);
            } else {
                // try to create one with KRADUtils for other misc data types
                attributeValueObject = KRADUtils.createObject(propertyType, new Class[]{String.class},
                        new Object[]{attributeValue});
                // if that didn't work, we'll get a null back
            }
        }
        return attributeValueObject;
    }

    public static Object createObject(Class<?> clazz, Class<?>[] argumentClasses, Object[] argumentValues) {
        if (clazz == null) {
            return null;
        }
        if (argumentClasses.length == 1 && argumentClasses[0] == String.class) {
            if (argumentValues.length == 1 && argumentValues[0] != null) {
                if (clazz == String.class) {
                    // this means we're trying to create a String from a String
                    // don't new up Strings, it's a bad idea
                    return argumentValues[0];
                } else {
                    // maybe it's a type that supports valueOf?
                    Method valueOfMethod = null;
                    try {
                        valueOfMethod = clazz.getMethod("valueOf", String.class);
                    } catch (NoSuchMethodException e) {
                        // ignored
                    }
                    if (valueOfMethod != null) {
                        try {
                            return valueOfMethod.invoke(null, argumentValues[0]);
                        } catch (Exception e) {
                            // ignored
                        }
                    }
                }
            }
        }
        try {
            Constructor<?> constructor = clazz.getConstructor(argumentClasses);
            return constructor.newInstance(argumentValues);
        } catch (Exception e) {
            // ignored
        }
        return null;
    }

    /**
     * Creates a comma separated String representation of the given list.
     *
     * <p>
     * For example 'a','b',c'.
     * </p>
     *
     * @param list
     * @return the joined String, empty if the list is null or has no elements
     */
    public static String joinWithQuotes(List<String> list) {
        if (list == null || list.size() == 0) {
            return "";
        }

        return KRADConstants.SINGLE_QUOTE +
                StringUtils.join(list.iterator(), KRADConstants.SINGLE_QUOTE + "," + KRADConstants.SINGLE_QUOTE) +
                KRADConstants.SINGLE_QUOTE;
    }

    /**
     * TODO this method will probably need to be exposed in a public KRADUtils class as it is used
     * by several different modules.  That will have to wait until ModuleService and KualiModuleService are moved
     * to core though.
     *
     * @param clazz class to get a namespace code for
     * @return namespace code
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
                List arraysList = Arrays.asList(parameters);
                list.addAll(arraysList);
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
     * pulled from the request
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

    /**
     * Builds a Map containing a key/value pair for each property given in the property names list, general
     * security is checked to determine if the value needs to be encrypted along with applying formatting to
     * the value
     *
     * @param propertyNames - list of property names to get key/value pairs for
     * @param dataObject - object instance containing the properties for which the values will be pulled
     * @return Map<String, String> containing entry for each property name with the property name as the map key
     * and the property value as the value
     */
    public static Map<String, String> getPropertyKeyValuesFromDataObject(List<String> propertyNames,
            Object dataObject) {
        return getPropertyKeyValuesFromDataObject(propertyNames, Collections.<String>emptyList(), dataObject);
    }

    /**
     * Builds a Map containing a key/value pair for each property given in the property names list, general
     * security is checked to determine if the value needs to be encrypted along with applying formatting to
     * the value
     *
     * @param propertyNames - list of property names to get key/value pairs for
     * @param securePropertyNames - list of secure property names to match for encryption
     * @param dataObject - object instance containing the properties for which the values will be pulled
     * @return Map<String, String> containing entry for each property name with the property name as the map key
     * and the property value as the value
     */
    public static Map<String, String> getPropertyKeyValuesFromDataObject(List<String> propertyNames,
            List<String> securePropertyNames, Object dataObject) {
        Map<String, String> propertyKeyValues = new HashMap<String, String>();

        if (dataObject == null) {
            return propertyKeyValues;
        }

        // iterate through properties and add a map entry for each
        for (String propertyName : propertyNames) {
            String propertyValue = ObjectPropertyUtils.getPropertyValueAsText(dataObject, propertyName);
            if (propertyValue == null) {
                propertyValue = StringUtils.EMPTY;
            }

            // secure values are not returned
            if (!isSecure(propertyName, securePropertyNames, dataObject, propertyValue)) {
                propertyKeyValues.put(propertyName, propertyValue);
            }

        }

        return propertyKeyValues;
    }

    /**
     * Determines whether a property name should be secured, either based on installed sensitive data patterns, a list
     * of secure property name patterns, or attributes in the Data Dictionary.
     *
     * @param propertyName The property name to check for security
     * @param securePropertyNames The secure property name patterns to check
     * @param dataObject The object containing this property
     * @param propertyValue The value of the property
     * @return true if the property needs to be secure, false otherwise
     */
    private static boolean isSecure(String propertyName, List<String> securePropertyNames, Object dataObject,
            Object propertyValue) {
        if (propertyValue instanceof String && containsSensitiveDataPatternMatch((String) propertyValue)) {
            return true;
        }

        if (containsSecurePropertyName(propertyName, securePropertyNames)) {
            return true;
        }

        return KRADServiceLocatorWeb.getDataObjectAuthorizationService()
                .attributeValueNeedsToBeEncryptedOnFormsAndLinks(dataObject.getClass(), propertyName);
    }

    /**
     * Helper method to identify if propertyName contains a secure property name element.
     * Check handles simple or compound names and ignores partial matches.
     *
     * @param propertyName property name as a single term or compound term (i.e. items[0].propertyName)
     * @param securePropertyNames list of secure property names to match
     * @return true if any of the secure property names are found in the property name, false otherwise
     */
    private static boolean containsSecurePropertyName(String propertyName, List<String> securePropertyNames) {
        if (securePropertyNames == null) {
            return false;
        }

        for (String securePropertyName : securePropertyNames) {
            // pattern prefix and suffix used to handle compound names and ignore partial name matches
            if (Pattern.compile("(?:\\.|^)" + Pattern.quote(securePropertyName) + "(?:\\.|\\[|$)").matcher(propertyName)
                    .find()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Utility method to convert a Map to a Properties object
     *
     * @param parameters - map to convert
     * @return Properties object containing all the map entries
     */
    public static Properties convertMapToProperties(Map<String, String> parameters) {
        Properties properties = new Properties();

        if (parameters != null) {
            for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                properties.put(parameter.getKey(), parameter.getValue());
            }
        }

        return properties;
    }

    /**
     * Utility method to convert a Request Parameters Map to a Properties object
     *
     * <p>
     * Multiple values for a parameter are joined together with comma delimiter
     * </p>
     *
     * @param requestParameters - map to convert
     * @return Properties object containing all the map entries
     */
    public static Properties convertRequestMapToProperties(Map<String, String[]> requestParameters) {
        Properties properties = new Properties();

        if (requestParameters != null) {
            for (Map.Entry<String, String[]> parameter : requestParameters.entrySet()) {
                String[] parameterValue = parameter.getValue();
                String parameterValueString = StringUtils.join(parameterValue, ",");

                properties.put(parameter.getKey(), parameterValueString);
            }
        }

        return properties;
    }

    /**
     * This method converts request parameters coming in as String to native types in case of Boolean, Number or
     * java.sql.Date.
     * For boolean the conversion is performed only if an @Converter annotation is set on the JPA entity field.
     *
     * @param dataObjectClass - business object class
     * @param parameters - map of request parameters with field values as String for the fields in the dataObjectClass
     * @return Map <String,Object> converted values
     */
    public static Map<String, Object> coerceRequestParameterTypes(Class<?> dataObjectClass,
            Map<String, String> parameters) {
        Map<String, Object> filteredFieldValues = new HashMap<String, Object>();
        List<java.lang.reflect.Field> allFields = ObjectPropertyUtils.getAllFields(
                new ArrayList<java.lang.reflect.Field>(), dataObjectClass, Object.class);

        for (String fieldName : parameters.keySet()) {
            Class<?> propertyType = ObjectPropertyUtils.getPropertyType(dataObjectClass, fieldName);

            String strValue = parameters.get(fieldName);

            if (TypeUtils.isBooleanClass(propertyType) && isConvertAnnotationPresent(allFields, fieldName)) {
                filteredFieldValues.put(fieldName, Truth.strToBooleanIgnoreCase(strValue));
            } else if (TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType)) {
                try {
                    filteredFieldValues.put(fieldName, hydrateAttributeValue(propertyType, strValue));
                } catch (Exception nfe) {
                    GlobalVariables.getMessageMap().putError("parameters[" + fieldName + "]",
                            RiceKeyConstants.ERROR_NUMBER, strValue);
                    throw new RuntimeException("Could not parse property value into Number for " + fieldName);
                }
            } else if (TypeUtils.isTemporalClass(propertyType)) {
                try {
                    filteredFieldValues.put(fieldName, CoreApiServiceLocator.getDateTimeService().convertToSqlDate(
                            strValue));
                } catch (ParseException pe) {
                    GlobalVariables.getMessageMap().putError("parameters[" + fieldName + "]",
                            RiceKeyConstants.ERROR_DATE_TIME, strValue);
                    throw new RuntimeException("Could not parse property value into java.sql.Date for " + fieldName);
                }
            }

            // If value not converted set the value from parameters
            if (filteredFieldValues.get(fieldName) == null) {
                filteredFieldValues.put(fieldName, parameters.get(fieldName));
            }

        }
        return filteredFieldValues;

    }

    /**
     * Checks to see if the specified field from the list of allFields has the @Convert annotation set on it
     *
     * @param allFields List of all fields on the entity
     * @param fieldName Field name to check for @Convert annotation
     * @return true if annotation is present else false
     */
    private static boolean isConvertAnnotationPresent(List<java.lang.reflect.Field> allFields, String fieldName) {
        //Check if there is a @Convert annotation on the field
        boolean convertAnnotationFound = false;
        for (java.lang.reflect.Field f : allFields) {
            if (f.getName().equalsIgnoreCase(fieldName)) {
                if (f.getAnnotation(javax.persistence.Convert.class) != null) {
                    convertAnnotationFound = true;
                }
                break;
            }
        }

        return convertAnnotationFound;
    }

    /**
     * Check if data might be sensitive
     *
     * <p>
     * The sensitivity of the data is checked by matching it against the sensitive data patterns that are specified
     * in the system parameter table.
     * </p>
     *
     * @param fieldValue data to be checked for sensitivity
     * @return true if the data matches the sensitive data pattern, false otherwise
     */
    public static boolean containsSensitiveDataPatternMatch(String fieldValue) {
        if (StringUtils.isBlank(fieldValue)) {
            return false;
        }

        ParameterService parameterService = CoreFrameworkServiceLocator.getParameterService();
        Collection<String> sensitiveDataPatterns = parameterService.getParameterValuesAsString(
                KRADConstants.KNS_NAMESPACE, ParameterConstants.ALL_COMPONENT,
                KRADConstants.SystemGroupParameterNames.SENSITIVE_DATA_PATTERNS);

        for (String pattern : sensitiveDataPatterns) {
            if (Pattern.compile(pattern).matcher(fieldValue).find()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Strips out common patterns used in cross side scripting.
     *
     * @param values string array to strip patterns from
     * @return cleaned string array
     */
    public static String[] stripXSSPatterns(String[] values) {
        ArrayList<String> strippedValues = new ArrayList<String>();

        for (String value : values) {
            strippedValues.add(stripXSSPatterns(value));
        }

        return strippedValues.toArray(new String[]{});
    }

    /**
     * Strips out common patterns used in cross side scripting.
     *
     * @param value string to strip patterns from
     * @return cleaned string
     */
    public static String stripXSSPatterns(String value) {
        if (value == null) {
            return null;
        }

        // Avoid null characters
        value = value.replaceAll("", "");

        // Avoid anything between script tags
        Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid anything in a src='...' type of expression
        scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Remove any lonesome </script> tag
        scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Remove any lonesome <script ...> tag
        scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid eval(...) expressions
        scriptPattern = Pattern.compile("eval\\((.*?)\\)",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid expression(...) expressions
        scriptPattern = Pattern.compile("expression\\((.*?)\\)",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid javascript:... expressions
        scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid vbscript:... expressions
        scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
        value = scriptPattern.matcher(value).replaceAll("");

        // Avoid onload= expressions
        scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
        value = scriptPattern.matcher(value).replaceAll("");

        return value;
    }

    /**
     * Gets the UserSession object from the HttpServletRequest object's
     * associated session.
     *
     * <p>
     * In some cases (different threads) the UserSession cannot be retrieved
     * from GlobalVariables but can still be accessed via the session object
     * </p>
     *
     * @param request servlet request
     * @return user session found in the request's servlet session
     */
    public static final UserSession getUserSessionFromRequest(HttpServletRequest request) {
        return (UserSession) request.getSession().getAttribute(KRADConstants.USER_SESSION_KEY);
    }

    /**
     * Check if current deployment is the production environment
     *
     * @return true if the deploy environment is production, false otherwise
     */
    public static boolean isProductionEnvironment() {
        return CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                KRADConstants.PROD_ENVIRONMENT_CODE_KEY).equalsIgnoreCase(
                CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                        KRADConstants.ENVIRONMENT_KEY));
    }

    /**
     * Gets the message associated with ErrorMessage object passed in, using message service.
     * The prefix and suffix will be appended to the retrieved message if processPrefixSuffix is true and if those
     * settings are set on the ErrorMessage passed in.
     *
     * @param errorMessage the ErrorMessage object containing the message key(s)
     * @param processPrefixSuffix if true appends the prefix and suffix to the message if they exist on ErrorMessage
     * @return the converted/retrieved message
     */
    public static String getMessageText(ErrorMessage errorMessage, boolean processPrefixSuffix) {
        String message = "";
        if (errorMessage != null && errorMessage.getErrorKey() != null) {
            MessageService messageService = KRADServiceLocatorWeb.getMessageService();

            // find message by key
            message = messageService.getMessageText(errorMessage.getNamespaceCode(), errorMessage.getComponentCode(),
                    errorMessage.getErrorKey());
            if (message == null) {
                message = "Intended message with key: " + errorMessage.getErrorKey() + " not found.";
            }

            if (errorMessage.getMessageParameters() != null && StringUtils.isNotBlank(message)) {
                message = message.replace("'", "''");
                message = MessageFormat.format(message, (Object[]) errorMessage.getMessageParameters());
            }

            // add prefix
            if (StringUtils.isNotBlank(errorMessage.getMessagePrefixKey()) && processPrefixSuffix) {
                String prefix = messageService.getMessageText(errorMessage.getNamespaceCode(),
                        errorMessage.getComponentCode(), errorMessage.getMessagePrefixKey());

                if (errorMessage.getMessagePrefixParameters() != null && StringUtils.isNotBlank(prefix)) {
                    prefix = prefix.replace("'", "''");
                    prefix = MessageFormat.format(prefix, (Object[]) errorMessage.getMessagePrefixParameters());
                }

                if (StringUtils.isNotBlank(prefix)) {
                    message = prefix + " " + message;
                }
            }

            // add suffix
            if (StringUtils.isNotBlank(errorMessage.getMessageSuffixKey()) && processPrefixSuffix) {
                String suffix = messageService.getMessageText(errorMessage.getNamespaceCode(),
                        errorMessage.getComponentCode(), errorMessage.getMessageSuffixKey());

                if (errorMessage.getMessageSuffixParameters() != null && StringUtils.isNotBlank(suffix)) {
                    suffix = suffix.replace("'", "''");
                    suffix = MessageFormat.format(suffix, (Object[]) errorMessage.getMessageSuffixParameters());
                }

                if (StringUtils.isNotBlank(suffix)) {
                    message = message + " " + suffix;
                }
            }
        }

        return message;
    }

    /**
     * Logs the error messages if any in the message map
     */
    public static void logErrors() {
        if (!GlobalVariables.getMessageMap().hasErrors()) {
            return;
        }

        for (Iterator<Map.Entry<String, List<ErrorMessage>>> i =
                     GlobalVariables.getMessageMap().getAllPropertiesAndErrors().iterator(); i.hasNext(); ) {
            Map.Entry<String, List<ErrorMessage>> e = i.next();

            StringBuffer logMessage = buildMessage(e);
            LOG.error(logMessage);
        }
    }

    /**
     * Builds the message for a given entry in the messageMap. The entry could have multiple messages for a given key.
     * The messages are appended separated by a ;
     *
     * @param e Map entry of property and errors for that property
     * @return logMessage
     */
    private static StringBuffer buildMessage(Map.Entry<String, List<ErrorMessage>> e) {
        StringBuffer logMessage = new StringBuffer();
        logMessage.append("[" + e.getKey() + "] ");
        boolean first = true;

        List<ErrorMessage> errorList = e.getValue();
        for (Iterator<ErrorMessage> j = errorList.iterator(); j.hasNext(); ) {
            ErrorMessage em = j.next();

            // if its the first message for the key
            if (first) {
                first = false;
            } else {
                logMessage.append(";");
            }
            logMessage.append(em);
        }
        return logMessage;
    }

    /**
     * Generate the request parameter portion of the url based on the map of key value pairs passed in
     *
     * @param requestParameters the request parameters to use in the string
     * @return a request parameter string starting with "?" with "&" separators, or blank if the mapped passed in is
     * blank
     */
    public static String getRequestStringFromMap(Map<String, String> requestParameters) {
        String requestString = "";

        if (requestParameters.isEmpty()) {
            return requestString;
        }

        URLCodec urlCodec = new URLCodec(KRADConstants.DEFAULT_ENCODING);

        for (String key : requestParameters.keySet()) {
            String value = null;
            try {
                value = urlCodec.encode(requestParameters.get(key));
            } catch (EncoderException e) {
                throw new RuntimeException("Unable to encode parameter name or value: " + key + "=" + value, e);
            }

            if (StringUtils.isNotBlank(requestString)) {
                requestString = requestString + "&";
            }

            requestString = requestString + key + "=" + value;
        }

        return "?" + requestString;
    }

    /**
     * Adds the header and content of an attachment to the response.
     *
     * @param response HttpServletResponse instance
     * @param contentType the content type of the attachment
     * @param inputStream the content of the attachment
     * @param fileName the file name of the attachment
     * @param fileSize the size of the attachment
     * @throws IOException if attachment to the results fails due to an IO error
     */
    public static void addAttachmentToResponse(HttpServletResponse response, InputStream inputStream,
            String contentType, String fileName, long fileSize) throws IOException {

        // If there are quotes in the name, we should replace them to avoid issues.
        // The filename will be wrapped with quotes below when it is set in the header
        String updateFileName;
        if (fileName.contains("\"")) {
            updateFileName = fileName.replaceAll("\"", "");
        } else {
            updateFileName = fileName;
        }

        // set response
        response.setContentType(contentType);
        response.setContentLength(org.springframework.util.NumberUtils.convertNumberToTargetClass(fileSize,
                Integer.class));
        response.setHeader("Content-disposition", "attachment; filename=\"" + updateFileName + "\"");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");

        // Copy the input stream to the response
        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }

    /**
     * Helper method for building a URL that will invoke the given controller and render the given
     * KRAD view
     *
     * @param baseUrl base url (domain, port)
     * @param controllerMapping mapping for the controller that should be invoked
     * @param viewId id for the view that should be rendered
     * @return url for invoking the view
     */
    public static String buildViewUrl(String baseUrl, String controllerMapping, String viewId) {
        Assert.hasLength(baseUrl, "base url is null or empty");
        Assert.hasLength(controllerMapping, "controller mapping is null or empty");
        Assert.hasLength(viewId, "view id is null or empty");

        StringBuffer url = new StringBuffer();

        url.append(baseUrl);

        if (!baseUrl.endsWith("/")) {
            url.append("/");
        }

        url.append(controllerMapping);

        url.append("?");
        url.append(UifParameters.VIEW_ID);
        url.append("=");
        url.append(viewId);

        return url.toString();
    }

    /**
     * Removes parameters from the given properties object that are request specific (useful when manupulating the
     * current URL to invoke something else)
     *
     * @param requestParameters properties instance containing the parameters to clean
     */
    public static void cleanRequestParameters(Properties requestParameters) {
        requestParameters.remove(UifParameters.SESSION_ID);
        requestParameters.remove(UifParameters.AJAX_REQUEST);
        requestParameters.remove(UifParameters.AJAX_RETURN_TYPE);
        requestParameters.remove(UifParameters.FORM_KEY);
        requestParameters.remove(UifParameters.JUMP_TO_ID);
        requestParameters.remove(UifParameters.FOCUS_ID);
    }

    /**
     * Get the full url for a request (requestURL + queryString)
     *
     * @param request the request
     * @return the fullUrl
     */
    public static String getFullURL(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    /**
     * Determines whether two URLs point at the same domain.
     *
     * @param firstDomain first URL string to compare
     * @param secondDomain second URL string to compare
     * @return true if the domains are different, false otherwise
     */
    public static boolean areDifferentDomains(String firstDomain, String secondDomain) {
        try {
            URL urlOne = new URL(firstDomain.toLowerCase());
            URL urlTwo = new URL(secondDomain.toLowerCase());

            if (urlOne.getHost().equals(urlTwo.getHost())) {
                LOG.debug("Hosts "
                        + urlOne.getHost()
                        + " of domains "
                        + firstDomain
                        + " and "
                        + secondDomain
                        + " were determined to be equal");

                return false;
            } else {
                LOG.debug("Hosts "
                        + urlOne.getHost()
                        + " of domains "
                        + firstDomain
                        + " and "
                        + secondDomain
                        + " are not equal");

                return true;
            }
        } catch (MalformedURLException mue) {
            LOG.error("Unable to successfully compare domains " + firstDomain + " and " + secondDomain);
        }

        return true;
    }

    /**
     * Attempts to generate a unique view title by combining the View's headerText with the title attribute for the
     * dataObjectClass found through the DataObjectMetaDataService.  If the title attribute cannot be found, just the
     * headerText is returned.
     *
     * @param form the form
     * @param view the view
     * @return the headerText with the title attribute in parenthesis or just the headerText if it title attribute
     * cannot be determined
     */
    public static String generateUniqueViewTitle(UifFormBase form, View view) {
        String title = view.getHeader().getHeaderText();

        String viewLabelPropertyName = "";

        Class<?> dataObjectClass;
        if (StringUtils.isNotBlank(view.getDefaultBindingObjectPath())) {
            dataObjectClass = ObjectPropertyUtils.getPropertyType(form, view.getDefaultBindingObjectPath());
        } else {
            dataObjectClass = view.getFormClass();
        }

        if (dataObjectClass != null) {
            viewLabelPropertyName = KRADServiceLocatorWeb.getLegacyDataAdapter().getTitleAttribute(dataObjectClass);
        }

        String viewLabelPropertyPath = "";
        if (StringUtils.isNotBlank(viewLabelPropertyName)) {
            // adjust binding prefix
            if (!viewLabelPropertyName.startsWith(UifConstants.NO_BIND_ADJUST_PREFIX)) {
                if (StringUtils.isNotBlank(view.getDefaultBindingObjectPath())) {
                    viewLabelPropertyPath = view.getDefaultBindingObjectPath() + "." + viewLabelPropertyName;
                }
            } else {
                viewLabelPropertyPath = StringUtils.removeStart(viewLabelPropertyName,
                        UifConstants.NO_BIND_ADJUST_PREFIX);
            }
        } else {
            // attempt to get title attribute
            if (StringUtils.isNotBlank(view.getDefaultBindingObjectPath())) {
                dataObjectClass = ViewModelUtils.getObjectClassForMetadata(view, form,
                        view.getDefaultBindingObjectPath());
            } else {
                dataObjectClass = view.getFormClass();
            }

            if (dataObjectClass != null) {
                String titleAttribute = KRADServiceLocatorWeb.getLegacyDataAdapter().getTitleAttribute(dataObjectClass);
                if (StringUtils.isNotBlank(titleAttribute)) {
                    viewLabelPropertyPath = view.getDefaultBindingObjectPath() + "." + titleAttribute;
                }
            }
        }

        Object viewLabelPropertyValue = null;
        if (StringUtils.isNotBlank(viewLabelPropertyPath) && ObjectPropertyUtils.isReadableProperty(form,
                viewLabelPropertyPath)) {
            viewLabelPropertyValue = ObjectPropertyUtils.getPropertyValueAsText(form, viewLabelPropertyPath);
        }

        if (viewLabelPropertyValue != null && StringUtils.isNotBlank(viewLabelPropertyValue.toString()) && StringUtils
                .isNotBlank(title)) {
            return title + " (" + viewLabelPropertyValue.toString() + ")";
        } else {
            return title;
        }
    }

    /**
     * Helper method for building title text for an element and a map of key/value pairs,
     *
     * <p>
     * Each key of the key value map is assumed to be an attribute for the given element class. The label is then
     * retrieved for the attribute from the data dictionary and used in the title (instead of the key)
     * </p>
     *
     * @param prependText text to prepend to the title
     * @param element element class the title is being generated for, used as the parent for getting the key labels
     * @param keyValueMap map of key value pairs to add to the title text
     * @return title string
     */
    public static String buildAttributeTitleString(String prependText, Class<?> element,
            Map<String, String> keyValueMap) {
        StringBuffer titleText = new StringBuffer(prependText);

        for (String key : keyValueMap.keySet()) {
            String fieldVal = keyValueMap.get(key).toString();

            titleText.append(" "
                    + KRADServiceLocatorWeb.getDataDictionaryService().getAttributeLabel(element, key)
                    + "="
                    + fieldVal.toString());
        }

        return titleText.toString();
    }

    /**
     * Attempts to extract a string value out of the field passed in, varies depending on field type
     *
     * <p>If the field is a dataField, it will use its propertyName to retrieve a value, otherwise it will try to
     * retrieve textual content out of various component types.  If the field is a FieldGroup, only the first
     * component's determined value will be used.  This function is used for sorting.</p>
     *
     * @param model the current model
     * @param field the field to get a value from
     * @return the field's String value, false if it cant be determined
     */
    public static String getSimpleFieldValue(Object model, Field field) {
        if (field == null) {
            return null;
        }

        String value = null;
        // check for what type of field this is
        if (field instanceof DataField) {
            String propertyPath = ((DataField) field).getBindingInfo().getBindingPath();
            Object valueObject = null;

            if (field.isHidden()) {
                return "";
            }

            // check if readable
            if (ObjectPropertyUtils.isReadableProperty(model, propertyPath)) {
                valueObject = ObjectPropertyUtils.getPropertyValueAsText(model, propertyPath);
            }

            // use object's string value
            if (valueObject != null && !((DataField) field).isApplyMask()) {
                value = valueObject.toString();
            } else if (valueObject != null && ((DataField) field).isApplyMask()) {
                value = ((DataField) field).getMaskFormatter().maskValue(valueObject);
            }
        } else if (field instanceof ActionField) {
            value = ((ActionField) field).getActionLabel();

            // use image alt text if any
            if (StringUtils.isBlank(value) && ((ActionField) field).getActionImage() != null) {
                value = ((ActionField) field).getActionImage().getAltText();
            }
        } else if (field instanceof LinkField) {
            value = ((LinkField) field).getLinkText();
        } else if (field instanceof ImageField) {
            value = ((ImageField) field).getAltText();
        } else if (field instanceof MessageField && ((MessageField) field).getMessage() != null) {
            value = ((MessageField) field).getMessage().getMessageText();
        } else if (field instanceof SpaceField) {
            value = "";
        } else if (field instanceof FieldGroup
                && ((FieldGroup) field).getGroup() != null
                && ((FieldGroup) field).getGroup().getItems() != null
                && !((FieldGroup) field).getGroup().getItems().isEmpty()) {
            // using first components type for assumed value
            Component firstComponent = ((FieldGroup) field).getGroup().getItems().get(0);

            // check first component type to extract value
            if (firstComponent != null && firstComponent instanceof Field) {
                value = getSimpleFieldValue(model, (Field) firstComponent);
            } else if (firstComponent instanceof Action && StringUtils.isNotBlank(
                    ((Action) firstComponent).getActionLabel())) {
                value = ((Action) firstComponent).getActionLabel();
            } else if (firstComponent instanceof Action && ((Action) firstComponent).getActionImage() != null) {
                value = ((Action) firstComponent).getActionImage().getAltText();
            } else if (firstComponent instanceof Link) {
                value = ((Link) firstComponent).getLinkText();
            } else if (firstComponent instanceof Image) {
                value = ((Image) firstComponent).getAltText();
            } else if (firstComponent instanceof org.kuali.rice.krad.uif.element.Message) {
                value = ((org.kuali.rice.krad.uif.element.Message) firstComponent).getMessageText();
            } else {
                value = null;
            }
        }

        return value;
    }

    /**
     * Helper method to change common characters into HTML attribute safe characters
     *
     * @param message the string to convert
     * @return the converted string with quotes, sing quotes, and slash replaced
     */
    public static String convertToHTMLAttributeSafeString(String message) {
        if (StringUtils.isBlank(message)) {
            return message;
        }

        if (message.contains("\"")) {
            message = message.replace("\"", "&quot;");
        }
        if (message.contains("'")) {
            message = message.replace("'", "&#39;");
        }
        if (message.contains("\\")) {
            message = message.replace("\\", "&#92;");
        }

        return message;
    }

    /**
     * Get the rowCss for the line specified, by evaluating the conditionalRowCssClasses map for that row
     *
     * @param conditionalRowCssClasses the conditionalRowCssClass map, where key is the condition and value is
     * the class(es)
     * @param lineIndex the line/row index
     * @param isOdd true if the row is considered odd
     * @param lineContext the lineContext for expressions, pass null if not applicable
     * @param expressionEvaluator the expressionEvaluator, pass null if not applicable
     * @return row csss class String for the class attribute of this row
     */
    public static String generateRowCssClassString(Map<String, String> conditionalRowCssClasses, int lineIndex,
            boolean isOdd, Map<String, Object> lineContext, ExpressionEvaluator expressionEvaluator) {
        String rowCss = "";
        if (conditionalRowCssClasses == null || conditionalRowCssClasses.isEmpty()) {
            return rowCss;
        }

        for (String cssRule : conditionalRowCssClasses.keySet()) {
            if (cssRule.startsWith(UifConstants.EL_PLACEHOLDER_PREFIX) && lineContext != null &&
                    expressionEvaluator != null) {
                String outcome = expressionEvaluator.evaluateExpressionTemplate(lineContext, cssRule);
                if (outcome != null && Boolean.parseBoolean(outcome)) {
                    rowCss = rowCss + " " + conditionalRowCssClasses.get(cssRule);
                }
            } else if (cssRule.equals(UifConstants.RowSelection.ALL)) {
                rowCss = rowCss + " " + conditionalRowCssClasses.get(cssRule);
            } else if (cssRule.equals(UifConstants.RowSelection.EVEN) && !isOdd) {
                rowCss = rowCss + " " + conditionalRowCssClasses.get(cssRule);
            } else if (cssRule.equals(UifConstants.RowSelection.ODD) && isOdd) {
                rowCss = rowCss + " " + conditionalRowCssClasses.get(cssRule);
            } else if (StringUtils.isNumeric(cssRule) && (lineIndex + 1) == Integer.parseInt(cssRule)) {
                rowCss = rowCss + " " + conditionalRowCssClasses.get(cssRule);
            }
        }

        rowCss = StringUtils.removeStart(rowCss, " ");

        return rowCss;
    }

    /**
     * LegacyCase - This method simply uses PojoPropertyUtilsBean logic to get the Class of a Class property.
     * This method does not have any of the logic needed to obtain the Class of an element of a Collection specified in
     * the DataDictionary.
     *
     * @param object An instance of the Class of which we're trying to get the property Class.
     * @param propertyName The name of the property.
     * @return property type
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    static public Class easyGetPropertyType(Object object,
            String propertyName) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (LegacyUtils.useLegacyForObject(object)) {
            return PropertyUtils.getPropertyType(object, propertyName);
        }
        return KradDataServiceLocator.getDataObjectService().wrap(object).getPropertyType(propertyName);
    }

    /**
     * Sets the property of an object with the given value. Converts using the formatter of the type for the property.
     * Note: propertyType does not need passed, is found by util method.
     *
     * @param bo business object
     * @param propertyName property name
     * @param propertyValue propery value
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public static void setObjectProperty(Object bo, String propertyName,
            Object propertyValue) throws FormatException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Class propertyType = easyGetPropertyType(bo, propertyName);
        setObjectProperty(bo, propertyName, propertyType, propertyValue);

    }

    /**
     * Sets the property of an object with the given value. Converts using the formatter of the given type if one is
     * found.
     *
     * @param bo
     * @param propertyName
     * @param propertyType
     * @param propertyValue
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static void setObjectProperty(Object bo, String propertyName, Class propertyType,
            Object propertyValue) throws FormatException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        KRADServiceLocatorWeb.getLegacyDataAdapter().setObjectProperty(bo, propertyName, propertyType, propertyValue);
    }

    /**
     * Removes all query characters from a string.
     *
     * @param string
     * @return Cleaned string
     */
    public static String clean(String string) {
        for (SearchOperator op : SearchOperator.QUERY_CHARACTERS) {
            string = StringUtils.replace(string, op.op(), KRADConstants.EMPTY_STRING);
        }
        return string;
    }

    /**
     * This method is a confirms if object is null, and unproxies if necessary to determine this
     *
     * @param object - any object, proxied or not, materialized or not
     * @return true if the object (or underlying materialized object) is null, false otherwise
     */
    public static boolean isNull(Object object) {
        if (object == null) {
            return true;
        }
        return KRADServiceLocatorWeb.getLegacyDataAdapter().isNull(object);
    }

    /**
     * This method is a confirms if object is not null, and unproxies if necessary to determine this
     *
     * @param object - any object, proxied or not, materialized or not
     * @return true if the object (or underlying materialized object) is not null, true if its null
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    /**
     * Attempts to find the Class for the given potentially proxied object
     *
     * @param object the potentially proxied object to find the Class of
     * @return the best Class which could be found for the given object
     */
    public static Class materializeClassForProxiedObject(Object object) {
        return KRADServiceLocatorWeb.getLegacyDataAdapter().materializeClassForProxiedObject(object);
    }

    /**
     * This method runs the KRADUtils.isNotNull() method for each item in a list of BOs. KRADUtils.isNotNull() will
     * materialize
     * the objects if they are currently OJB proxies.
     *
     * @param possiblyProxiedObjects - a Collection of objects that may be proxies
     */
    public static void materializeObjects(Collection possiblyProxiedObjects) {
        for (Iterator i = possiblyProxiedObjects.iterator(); i.hasNext(); ) {
            KRADUtils.isNotNull(i.next());
        }
    }

    /**
     * Returns the prefix of a nested attribute name, or the empty string if the attribute name is not nested.
     *
     * @param attributeName
     * @return everything BEFORE the last "." character in attributeName
     */
    public static String getNestedAttributePrefix(String attributeName) {
        int lastIndex = PropertyAccessorUtils.getLastNestedPropertySeparatorIndex(attributeName);

        return lastIndex != -1 ? StringUtils.substring(attributeName, 0, lastIndex) : StringUtils.EMPTY;
    }

    /**
     * Returns the primitive part of an attribute name string.
     *
     * @param attributeName
     * @return everything AFTER the last "." character in attributeName
     */
    public static String getNestedAttributePrimitive(String attributeName) {
        int lastIndex = PropertyAccessorUtils.getLastNestedPropertySeparatorIndex(attributeName);

        return lastIndex != -1 ? StringUtils.substring(attributeName, lastIndex + 1) : attributeName;
    }

    /**
     * This method safely extracts either simple values OR nested values. For example, if the bo is SubAccount, and the
     * fieldName is
     * a21SubAccount.subAccountTypeCode, this thing makes sure it gets the value off the very end attribute, no matter
     * how deeply
     * nested it is. The code would be slightly simpler if this was done recursively, but this is safer, and consumes a
     * constant
     * amount of memory, no matter how deeply nested it goes.
     *
     * @param bo
     * @param fieldName
     * @return The field value if it exists. If it doesnt, and the name is invalid, and
     */
    public static Object getNestedValue(Object bo, String fieldName) {
        return KRADServiceLocatorWeb.getLegacyDataAdapter().getNestedValue(bo, fieldName);
    }

    /**
     * This method safely creates a object from a class
     * Convenience method to create new object and throw a runtime exception if it cannot
     * If the class is an {@link org.kuali.rice.krad.bo.ExternalizableBusinessObject}, this method will determine the
     * interface for the EBO and
     * query the
     * appropriate module service to create a new instance.
     *
     * @param clazz
     * @return a newInstance() of clazz
     */
    public static <T> T createNewObjectFromClass(Class<T> clazz) {
        if (clazz == null) {
            throw new RuntimeException("BO class was passed in as null");
        }
        return (T) KRADServiceLocatorWeb.getLegacyDataAdapter().createNewObjectFromClass(clazz);
    }

    private static KualiModuleService getKualiModuleService() {
        if (kualiModuleService == null) {
            kualiModuleService = KRADServiceLocatorWeb.getKualiModuleService();
        }
        return kualiModuleService;
    }

    /**
     * Updates the properties of the given component instance with the value found from the
     * corresponding map of client state (if found)
     *
     * @param component component instance to update
     * @param clientSideState map of state to sync with
     */
    public static void syncClientSideStateForComponent(Component component, Map<String, Object> clientSideState) {
        // find the map of state that was sent for component (if any)
        Map<String, Object> componentState = null;
        if (component instanceof View) {
            componentState = clientSideState;
        } else {
            if (clientSideState.containsKey(component.getId())) {
                componentState = (Map<String, Object>) clientSideState.get(component.getId());
            }
        }

        // if state was sent, match with fields on the component that are annotated to have client state
        if ((componentState != null) && (!componentState.isEmpty())) {
            Map<String, Annotation> annotatedFields = CopyUtils.getFieldsWithAnnotation(component.getClass(),
                    ClientSideState.class);

            for (Map.Entry<String, Annotation> annotatedField : annotatedFields.entrySet()) {
                ClientSideState clientSideStateAnnot = (ClientSideState) annotatedField.getValue();

                String variableName = clientSideStateAnnot.variableName();
                if (StringUtils.isBlank(variableName)) {
                    variableName = annotatedField.getKey();
                }

                if (componentState.containsKey(variableName)) {
                    Object value = componentState.get(variableName);
                    ObjectPropertyUtils.setPropertyValue(component, annotatedField.getKey(), value);
                }
            }
        }
    }

}
