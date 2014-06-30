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
package org.kuali.rice.krad.lookup;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.data.KradDataServiceLocator;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.datadictionary.exception.UnknownBusinessClassAttributeException;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifPropertyPaths;
import org.kuali.rice.krad.uif.lifecycle.ComponentPostMetadata;
import org.kuali.rice.krad.uif.lifecycle.ViewPostMetadata;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.util.ExternalizableBusinessObjectUtils;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.beans.PropertyAccessorUtils;

import javax.servlet.http.HttpServletRequest;

import java.security.GeneralSecurityException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides static utility methods for use within the lookup framework.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LookupUtils {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupUtils.class);
    private static final String[] searchList = new String[SearchOperator.QUERY_CHARACTERS.size()];

    static {
        int index = 0;
        for (SearchOperator operator : SearchOperator.QUERY_CHARACTERS) {
            searchList[index++] = operator.op();
        }
    }

    private static final String[] replacementList = Collections.nCopies(searchList.length, "").toArray(new String[0]);

    private LookupUtils() {}

    /**
     * Retrieves the value for the given parameter name to send as a lookup parameter.
     *
     * @param form form instance to retrieve values from
     * @param request request object to retrieve parameters from
     * @param lookupObjectClass data object class associated with the lookup, used to check whether the
     * value needs to be encyrpted
     * @param propertyName name of the property associated with the parameter, used to check whether the
     * value needs to be encrypted
     * @param parameterName name of the parameter to retrieve the value for
     * @return String parameter value or empty string if no value was found
     */
    public static String retrieveLookupParameterValue(UifFormBase form, HttpServletRequest request,
            Class<?> lookupObjectClass, String propertyName, String parameterName) {
        String parameterValue = "";

        // get literal parameter values first
        if (StringUtils.startsWith(parameterName, "'") && StringUtils.endsWith(parameterName, "'")) {
            parameterValue = StringUtils.substringBetween(parameterName, "'");
        } else if (parameterValue.startsWith(KRADConstants.LOOKUP_PARAMETER_LITERAL_PREFIX
                + KRADConstants.LOOKUP_PARAMETER_LITERAL_DELIMITER)) {
            parameterValue = StringUtils.removeStart(parameterValue, KRADConstants.LOOKUP_PARAMETER_LITERAL_PREFIX
                    + KRADConstants.LOOKUP_PARAMETER_LITERAL_DELIMITER);
        }
        // check if parameter is in request
        else if (request.getParameterMap().containsKey(parameterName)) {
            parameterValue = request.getParameter(parameterName);
        }
        // get parameter value from form object
        else {
            parameterValue = ObjectPropertyUtils.getPropertyValueAsText(form, parameterName);
        }

        if (parameterValue != null && lookupObjectClass != null
                && KRADServiceLocatorWeb.getDataObjectAuthorizationService()
                .attributeValueNeedsToBeEncryptedOnFormsAndLinks(lookupObjectClass, propertyName)) {
            try {
                if (CoreApiServiceLocator.getEncryptionService().isEnabled()) {
                    parameterValue = CoreApiServiceLocator.getEncryptionService().encrypt(parameterValue)
                            + EncryptionService.ENCRYPTION_POST_PREFIX;
                }
            } catch (GeneralSecurityException e) {
                LOG.error("Unable to encrypt value for property name: " + propertyName);
                throw new RuntimeException(e);
            }
        }

        return parameterValue;
    }

    /**
     * Retrieves the default KRAD base lookup URL, used to build lookup URLs in code
     *
     * @return String base lookup URL (everything except query string)
     */
    public static String getBaseLookupUrl() {
        return CoreApiServiceLocator.getKualiConfigurationService().getPropertyValueAsString(
                KRADConstants.KRAD_LOOKUP_URL_KEY);
    }

    /**
     * Uses the DataDictionary to determine whether to force uppercase the value, and if it should, then it does the
     * uppercase, and returns the upper-cased value.
     *
     * @param dataObjectClass parent DO class that the fieldName is a member of
     * @param fieldName name of the field to be forced to uppercase
     * @param fieldValue value of the field that may be uppercased
     * @return the correctly uppercased fieldValue if it should be uppercased, otherwise fieldValue is returned
     *         unchanged
     */
    public static String forceUppercase(Class<?> dataObjectClass, String fieldName, String fieldValue) {
        // short-circuit to exit if there isnt enough information to do the forceUppercase
        if (StringUtils.isBlank(fieldValue)) {
            return fieldValue;
        }

        // parameter validation
        if (dataObjectClass == null) {
            throw new IllegalArgumentException("Parameter dataObjectClass passed in with null value.");
        }

        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("Parameter fieldName passed in with empty value.");
        }

        if (!KRADServiceLocatorWeb.getDataDictionaryService().isAttributeDefined(dataObjectClass, fieldName)
                .booleanValue()) {
            return fieldValue;
        }

        boolean forceUpperCase = false;
        try {
            forceUpperCase = KRADServiceLocatorWeb.getDataDictionaryService()
                    .getAttributeForceUppercase(dataObjectClass, fieldName).booleanValue();
        } catch (UnknownBusinessClassAttributeException ubae) {
            // do nothing, don't alter the fieldValue
        }

        if (forceUpperCase && !fieldValue.endsWith(EncryptionService.ENCRYPTION_POST_PREFIX)) {
            return fieldValue.toUpperCase();
        }

        return fieldValue;
    }

    /**
     * Uses the DataDictionary to determine whether to force uppercase the values, and if it should, then it does the
     * uppercase, and returns the upper-cased Map of fieldname/fieldValue pairs.
     *
     * @param dataObjectClass parent DO class that the fieldName is a member of
     * @param fieldValues a Map<String,String> where the key is the fieldName and the value is the fieldValue
     * @return the same Map is returned, with the appropriate values uppercased (if any)
     */
    public static Map<String, String> forceUppercase(Class<?> dataObjectClass, Map<String, String> fieldValues) {
        if (dataObjectClass == null) {
            throw new IllegalArgumentException("Parameter boClass passed in with null value.");
        }

        if (fieldValues == null) {
            throw new IllegalArgumentException("Parameter fieldValues passed in with null value.");
        }

        for (String fieldName : fieldValues.keySet()) {
            fieldValues.put(fieldName, forceUppercase(dataObjectClass, fieldName, fieldValues.get(fieldName)));
        }

        return fieldValues;
    }

    /**
     * Parses and returns the lookup result set limit, checking first for the limit for the specific view,
     * then the class being looked up, and then the global application limit if there isn't a limit specific
     * to this data object class.
     *
     * @param dataObjectClass class to get limit for
     * @param lookupForm lookupForm to use.  May be null if the form is unknown. If lookupForm is null, only the
     * dataObjectClass will be used to find the search results set limit
     * @return result set limit
     */
    public static Integer getSearchResultsLimit(Class dataObjectClass, LookupForm lookupForm) {
        Integer limit = KRADServiceLocatorWeb.getViewDictionaryService().getResultSetLimitForLookup(dataObjectClass,
                lookupForm);
        if (limit == null) {
            limit = getApplicationSearchResultsLimit();
        }

        return limit;
    }

    /**
     * Retrieves the default application search limit configured through a system parameter.
     *
     * @return default result set limit of the application
     */
    public static Integer getApplicationSearchResultsLimit() {
        String limitString = CoreFrameworkServiceLocator.getParameterService()
                .getParameterValueAsString(KRADConstants.KRAD_NAMESPACE,
                        KRADConstants.DetailTypes.LOOKUP_PARM_DETAIL_TYPE,
                        KRADConstants.SystemGroupParameterNames.LOOKUP_RESULTS_LIMIT);
        if (limitString != null) {
            return Integer.valueOf(limitString);
        }

        return null;
    }

    /**
     * Retrieves the default application multiple value search limit configured through a system parameter.
     *
     * @return default multiple value result set limit of the application
     */
    public static Integer getApplicationMultipleValueSearchResultsLimit() {
        String limitString = CoreFrameworkServiceLocator.getParameterService()
                .getParameterValueAsString(KRADConstants.KRAD_NAMESPACE,
                        KRADConstants.DetailTypes.LOOKUP_PARM_DETAIL_TYPE,
                        KRADConstants.SystemGroupParameterNames.MULTIPLE_VALUE_LOOKUP_RESULTS_LIMIT);
        if (limitString != null) {
            return Integer.valueOf(limitString);
        }

        return null;
    }

    /**
     * Determines what Timestamp should be used for active queries on effective dated records. Determination made as
     * follows:
     *
     * <ul>
     * <li>Use activeAsOfDate value from search values Map if value is not empty</li>
     * <li>If search value given, try to convert to sql date, if conversion fails, try to convert to Timestamp</li>
     * <li>If search value empty, use current Date</li>
     * <li>If Timestamp value not given, create Timestamp from given Date setting the time as 1 second before midnight
     * </ul>
     *
     * @param searchValues map containing search key/value pairs
     * @return timestamp to be used for active criteria
     */
    public static Timestamp getActiveDateTimestampForCriteria(Map searchValues) {
        Date activeDate = CoreApiServiceLocator.getDateTimeService().getCurrentSqlDate();

        Timestamp activeTimestamp = null;
        if (searchValues.containsKey(KRADPropertyConstants.ACTIVE_AS_OF_DATE)) {
            String activeAsOfDate = (String) searchValues.get(KRADPropertyConstants.ACTIVE_AS_OF_DATE);
            if (StringUtils.isNotBlank(activeAsOfDate)) {
                try {
                    activeDate = CoreApiServiceLocator.getDateTimeService()
                            .convertToSqlDate(KRADUtils.clean(activeAsOfDate));
                } catch (ParseException e) {
                    // try to parse as timestamp
                    try {
                        activeTimestamp = CoreApiServiceLocator.getDateTimeService()
                                .convertToSqlTimestamp(KRADUtils.clean(activeAsOfDate));
                    } catch (ParseException e1) {
                        throw new RuntimeException("Unable to convert date: " + KRADUtils.clean(activeAsOfDate));
                    }
                }
            }
        }

        // if timestamp not given set to 1 second before midnight on the given date
        if (activeTimestamp == null) {
            Calendar cal = Calendar.getInstance();

            cal.setTime(activeDate);
            cal.set(Calendar.HOUR, cal.getMaximum(Calendar.HOUR));
            cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
            cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));

            activeTimestamp = new Timestamp(cal.getTime().getTime());
        }

        return activeTimestamp;
    }

    /**
     * Changes from/to dates into the range operators the lookupable dao expects ("..",">" etc) this method modifies
     * the passed in map and returns an updated search criteria map.
     *
     * @param searchCriteria map of criteria currently set for which the date criteria will be adjusted
     * @return map updated search criteria
     */
    public static Map<String, String> preprocessDateFields(Map<String, String> searchCriteria) {
        Map<String, String> fieldsToUpdate = new HashMap<String, String>();
        Map<String, String> searchCriteriaUpdated = new HashMap<String, String>(searchCriteria);

        Set<String> fieldsForLookup = searchCriteria.keySet();
        for (String propName : fieldsForLookup) {
            if (propName.startsWith(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX)) {
                String from_DateValue = searchCriteria.get(propName);
                String dateFieldName =
                        StringUtils.remove(propName, KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX);
                String to_DateValue = searchCriteria.get(dateFieldName);
                String newPropValue = to_DateValue;

                if (StringUtils.isNotEmpty(from_DateValue) && StringUtils.isNotEmpty(to_DateValue)) {
                    newPropValue = from_DateValue + SearchOperator.BETWEEN + to_DateValue;
                } else if (StringUtils.isNotEmpty(from_DateValue) && StringUtils.isEmpty(to_DateValue)) {
                    newPropValue = SearchOperator.GREATER_THAN_EQUAL.op() + from_DateValue;
                } else if (StringUtils.isNotEmpty(to_DateValue) && StringUtils.isEmpty(from_DateValue)) {
                    newPropValue = SearchOperator.LESS_THAN_EQUAL.op() + to_DateValue;
                } // could optionally continue on else here

                fieldsToUpdate.put(dateFieldName, newPropValue);
            }
        }

        // update lookup values from found date values to update
        Set<String> keysToUpdate = fieldsToUpdate.keySet();
        for (String updateKey : keysToUpdate) {
            searchCriteriaUpdated.put(updateKey, fieldsToUpdate.get(updateKey));
        }

        return searchCriteriaUpdated;
    }

    /**
     * Checks whether any of the fieldValues being passed refer to a property within an ExternalizableBusinessObject.
     *
     * @param boClass business object class of the lookup
     * @param fieldValues map of the lookup criteria values
     * @return true if externalizable business object are contained, false otherwise
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static boolean hasExternalBusinessObjectProperty(Class<?> boClass,
            Map<String, String> fieldValues) throws IllegalAccessException, InstantiationException {
        Object sampleBo = boClass.newInstance();
        for (String key : fieldValues.keySet()) {
            if (isExternalBusinessObjectProperty(sampleBo, key)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check whether the given property represents a property within an EBO starting with the sampleBo object given.
     * This is used to determine if a criteria needs to be applied to the EBO first,
     * before sending to the normal lookup DAO.
     *
     * @param sampleBo business object of the property to be tested
     * @param propertyName property name to be tested
     * @return true if the property is within an externalizable business object.
     */
    public static boolean isExternalBusinessObjectProperty(Object sampleBo, String propertyName) {
        if (propertyName.indexOf(".") > 0 && !StringUtils.contains(propertyName, "add.")) {
            Class<?> propertyClass =
                    ObjectPropertyUtils.getPropertyType(sampleBo, StringUtils.substringBeforeLast(propertyName, "."));
            if (propertyClass != null) {
                return ExternalizableBusinessObjectUtils.isExternalizableBusinessObjectInterface(propertyClass);
            }
        }

        return false;
    }

    /**
     * Returns a map stripped of any properties which refer to ExternalizableBusinessObjects. These values may not be
     * passed into the lookup service, since the objects they refer to are not in the
     * local database.
     *
     * @param boClass business object class of the lookup
     * @param fieldValues map of lookup criteria from which to remove the externalizable business objects
     * @return map of lookup criteria without externalizable business objects
     */
    public static Map<String, String> removeExternalizableBusinessObjectFieldValues(Class<?> boClass,
            Map<String, String> fieldValues) throws IllegalAccessException, InstantiationException {
        Map<String, String> eboFieldValues = new HashMap<String, String>();
        Object sampleBo = boClass.newInstance();
        for (String key : fieldValues.keySet()) {
            if (!isExternalBusinessObjectProperty(sampleBo, key)) {
                eboFieldValues.put(key, fieldValues.get(key));
            }
        }

        return eboFieldValues;
    }

    /**
     * Return the EBO fieldValue entries explicitly for the given eboPropertyName. (I.e., any properties with the given
     * property name as a prefix.
     *
     * @param eboPropertyName the externalizable business object property name to retrieve
     * @param fieldValues map of lookup criteria
     * return map of lookup criteria for the given eboPropertyName
     */
    public static Map<String, String> getExternalizableBusinessObjectFieldValues(String eboPropertyName,
            Map<String, String> fieldValues) {
        Map<String, String> eboFieldValues = new HashMap<String, String>();
        for (String key : fieldValues.keySet()) {
            if (key.startsWith(eboPropertyName + ".")) {
                eboFieldValues.put(StringUtils.substringAfterLast(key, "."), fieldValues.get(key));
            }
        }

        return eboFieldValues;
    }

    /**
     * Get the complete list of all properties referenced in the fieldValues that are ExternalizableBusinessObjects.
     *
     * <p>
     * This is a list of the EBO object references themselves, not of the properties within them.
     * </p>
     *
     * @param boClass business object class of the lookup
     * @param fieldValues map of lookup criteria from which to return the externalizable business objects
     * @return map of lookup criteria that are externalizable business objects
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static List<String> getExternalizableBusinessObjectProperties(Class<?> boClass,
            Map<String, String> fieldValues) throws IllegalAccessException, InstantiationException {
        Set<String> eboPropertyNames = new HashSet<String>();

        Object sampleBo = boClass.newInstance();
        for (String key : fieldValues.keySet()) {
            if (isExternalBusinessObjectProperty(sampleBo, key)) {
                eboPropertyNames.add(StringUtils.substringBeforeLast(key, "."));
            }
        }

        return new ArrayList<String>(eboPropertyNames);
    }

    /**
     * Given an property on the main BO class, return the defined type of the ExternalizableBusinessObject. This will
     * be used by other code to determine the correct module service to call for the lookup.
     *
     * @param boClass business object class of the lookup
     * @param propertyName property of which the externalizable business object type is to be determined
     * @return externalizable business object type
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Class<? extends ExternalizableBusinessObject> getExternalizableBusinessObjectClass(Class<?> boClass,
            String propertyName) throws IllegalAccessException, InstantiationException {
        return (Class<? extends ExternalizableBusinessObject>) ObjectPropertyUtils
                .getPropertyType(boClass.newInstance(), StringUtils.substringBeforeLast(propertyName, "."));
    }

    /**
     * Looks for criteria against nested EBOs and performs a search against that EBO and updates the criteria.
     *
     * @param searchCriteria map of criteria currently set
     * @param unbounded indicates whether the complete result should be returned.  When set to false the result is
     * limited (if necessary) to the max search result limit configured.
     * @return Map of adjusted criteria for nested EBOs
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static Map<String, String> adjustCriteriaForNestedEBOs(Class<?> dataObjectClass,
            Map<String, String> searchCriteria,
            boolean unbounded) throws InstantiationException, IllegalAccessException {
        // remove the EBO criteria
        Map<String, String> nonEboFieldValues = removeExternalizableBusinessObjectFieldValues(
                dataObjectClass, searchCriteria);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Non EBO properties removed: " + nonEboFieldValues);
        }

        // get the list of EBO properties attached to this object
        List<String> eboPropertyNames = getExternalizableBusinessObjectProperties(dataObjectClass, searchCriteria);
        if (LOG.isDebugEnabled()) {
            LOG.debug("EBO properties: " + eboPropertyNames);
        }

        // loop over those properties
        for (String eboPropertyName : eboPropertyNames) {
            // extract the properties as known to the EBO
            Map<String, String> eboFieldValues = LookupUtils.getExternalizableBusinessObjectFieldValues(eboPropertyName,
                    searchCriteria);
            if (LOG.isDebugEnabled()) {
                LOG.debug("EBO properties for master EBO property: " + eboPropertyName);
                LOG.debug("properties: " + eboFieldValues);
            }

            // run search against attached EBO's module service
            ModuleService eboModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(
                    getExternalizableBusinessObjectClass(dataObjectClass, eboPropertyName));

            // KULRICE-4401 made eboResults an empty list and only filled if service is found.
            List<?> eboResults = Collections.emptyList();
            if (eboModuleService != null) {
                eboResults = eboModuleService.getExternalizableBusinessObjectsListForLookup(
                        getExternalizableBusinessObjectClass(dataObjectClass, eboPropertyName),
                        (Map) eboFieldValues, unbounded);
            } else {
                LOG.debug("EBO ModuleService is null: " + eboPropertyName);
            }

            // get the parent property type
            Class<?> eboParentClass;
            String eboParentPropertyName;
            if (PropertyAccessorUtils.isNestedOrIndexedProperty(eboPropertyName)) {
                eboParentPropertyName = StringUtils.substringBeforeLast(eboPropertyName, ".");
                try {
                    eboParentClass = KradDataServiceLocator.getDataObjectService().wrap(dataObjectClass.newInstance()).getPropertyType(
                            eboParentPropertyName);
                } catch (Exception ex) {
                    throw new RuntimeException(
                            "Unable to create an instance of the business object class: " + dataObjectClass
                                    .getName(), ex);
                }
            } else {
                eboParentClass = dataObjectClass;
                eboParentPropertyName = null;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("determined EBO parent class/property name: " + eboParentClass + "/" + eboParentPropertyName);
            }

            // look that up in the DD (BOMDS) find the appropriate relationship
            // CHECK THIS: what if eboPropertyName is a nested attribute - need to strip off the
            // eboParentPropertyName if not null
            RelationshipDefinition rd = KRADServiceLocatorWeb.getLegacyDataAdapter().getDictionaryRelationship(
                    eboParentClass, eboPropertyName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Obtained RelationshipDefinition for " + eboPropertyName);
                LOG.debug(rd);
            }

            // copy the needed properties (primary only) to the field values KULRICE-4446 do
            // so only if the relationship definition exists
            // NOTE: this will work only for single-field PK unless the ORM
            // layer is directly involved
            // (can't make (field1,field2) in ( (v1,v2),(v3,v4) ) style
            // queries in the lookup framework
            if (KRADUtils.isNotNull(rd)) {
                if (rd.getPrimitiveAttributes().size() > 1) {
                    throw new RuntimeException(
                            "EBO Links don't work for relationships with multiple-field primary keys.");
                }
                String boProperty = rd.getPrimitiveAttributes().get(0).getSourceName();
                String eboProperty = rd.getPrimitiveAttributes().get(0).getTargetName();
                StringBuffer boPropertyValue = new StringBuffer();

                // loop over the results, making a string that the lookup DAO will convert into an
                // SQL "IN" clause
                for (Object ebo : eboResults) {
                    if (boPropertyValue.length() != 0) {
                        boPropertyValue.append(SearchOperator.OR.op());
                    }
                    try {
                        boPropertyValue.append(PropertyUtils.getProperty(ebo, eboProperty).toString());
                    } catch (Exception ex) {
                        LOG.warn("Unable to get value for " + eboProperty + " on " + ebo);
                    }
                }

                if (eboParentPropertyName == null) {
                    // non-nested property containing the EBO
                    nonEboFieldValues.put(boProperty, boPropertyValue.toString());
                } else {
                    // property nested within the main searched-for BO that contains the EBO
                    nonEboFieldValues.put(eboParentPropertyName + "." + boProperty, boPropertyValue.toString());
                }
            }
        }

        return nonEboFieldValues;
    }

    /**
     * Removes query characters (such as wildcards) from the given string value.
     *
     * @param criteriaValue string to clean
     * @return string with query characters removed
     */
    public static String scrubQueryCharacters(String criteriaValue) {
        return StringUtils.replaceEach(criteriaValue, searchList, replacementList);
    }

    /**
     * Generates a key string in case of multivalue return. The values are extracted
     * from the list of properties on the lineDataObject.
     *
     * If fieldConversionKeys is empty return the identifier string for the lineDataObject
     *
     * @param lineDataObject   Object from which to extract values
     * @param fieldConversionKeys List of keys whose values have to be concatenated
     * @return string representing the multivalue key 
     */
    public static String generateMultiValueKey(Object lineDataObject, List<String> fieldConversionKeys) {
        String lineIdentifier = "";

        if(fieldConversionKeys == null || fieldConversionKeys.isEmpty()) {
            lineIdentifier =
                    KRADServiceLocatorWeb.getLegacyDataAdapter().getDataObjectIdentifierString(lineDataObject);
        } else {
            Collections.sort(fieldConversionKeys);
            for (String fromFieldName : fieldConversionKeys) {
                Object fromFieldValue = ObjectPropertyUtils.getPropertyValue(lineDataObject, fromFieldName);

                if (fromFieldValue != null) {
                    lineIdentifier += fromFieldValue;
                }

                lineIdentifier += ":";
            }
            lineIdentifier = StringUtils.removeEnd(lineIdentifier, ":");
        }

        return lineIdentifier;
    }

    /**
     * Merges the lookup result selections that are part of the request with the selectedLookupResultsCache maintained in
     * the session.
     *
     * @param form lookup form instance containing the selected results and lookup configuration
     */
    public static void refreshLookupResultSelections(LookupForm form) {
        int displayStart = 0;
        int displayLength = 0;

        // avoid blowing the stack if the session expired
        ViewPostMetadata viewPostMetadata = form.getViewPostMetadata();
        if (viewPostMetadata != null) {

            // only one concurrent request per view please
            synchronized (viewPostMetadata) {
                ComponentPostMetadata oldCollectionGroup = viewPostMetadata.getComponentPostMetadata("uLookupResults");
                displayStart = (Integer) oldCollectionGroup.getData(UifConstants.PostMetadata.COLL_DISPLAY_START);
                displayLength = (Integer) oldCollectionGroup.getData(UifConstants.PostMetadata.COLL_DISPLAY_LENGTH);
            }
        }

        List<? extends Object> lookupResults = (List<? extends Object>) form.getLookupResults();
        List<String> fromFieldNames = form.getMultiValueReturnFields();

        Set<String> selectedLines = form.getSelectedCollectionLines().get(UifPropertyPaths.LOOKUP_RESULTS);
        Set<String> selectedLookupResultsCache = form.getSelectedLookupResultsCache();

        selectedLines = (selectedLines == null) ? new HashSet<String>() : selectedLines;

        for(int i = displayStart; i < displayStart + displayLength; i++ ) {
            if(i >= form.getLookupResults().size()) break;

            Object lineItem = lookupResults.get(i);
            String lineIdentifier = LookupUtils.generateMultiValueKey(lineItem, fromFieldNames);

            if(!selectedLines.contains(lineIdentifier)) {
                 selectedLookupResultsCache.remove(lineIdentifier);
            } else {
                selectedLookupResultsCache.add(lineIdentifier);
            }
        }

        selectedLines.addAll( selectedLookupResultsCache );

        form.getSelectedCollectionLines().put(UifPropertyPaths.LOOKUP_RESULTS, selectedLines);
    }

}
