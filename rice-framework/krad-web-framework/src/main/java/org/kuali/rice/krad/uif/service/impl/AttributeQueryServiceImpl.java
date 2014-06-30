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
package org.kuali.rice.krad.uif.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.component.MethodInvokerConfig;
import org.kuali.rice.krad.uif.field.AttributeQuery;
import org.kuali.rice.krad.uif.field.AttributeQueryResult;
import org.kuali.rice.krad.uif.lifecycle.ComponentPostMetadata;
import org.kuali.rice.krad.uif.lifecycle.ViewPostMetadata;
import org.kuali.rice.krad.uif.service.AttributeQueryService;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.widget.LocationSuggest;
import org.kuali.rice.krad.uif.widget.Suggest;
import org.kuali.rice.krad.util.BeanPropertyComparator;

/**
 * Implementation of <code>AttributeQueryService</code> that prepares the attribute queries and
 * delegates to the <code>LookupService</code>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AttributeQueryServiceImpl implements AttributeQueryService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AttributeQueryServiceImpl.class);

    private LookupService lookupService;
    private ConfigurationService configurationService;

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributeQueryResult performFieldSuggestQuery(ViewPostMetadata viewPostMetadata, String fieldId,
            String fieldTerm, Map<String, String> queryParameters) {
        AttributeQueryResult queryResult = new AttributeQueryResult();

        ComponentPostMetadata inputFieldMetaData = viewPostMetadata.getComponentPostMetadata(fieldId);
        if (inputFieldMetaData == null) {
            throw new RuntimeException("Unable to find attribute field instance for id: " + fieldId);
        }

        Suggest.SuggestPostData suggestPostData = (Suggest.SuggestPostData) inputFieldMetaData.getData(
                UifConstants.PostMetadata.SUGGEST);

        AttributeQuery suggestQuery = suggestPostData.getSuggestQuery();

        boolean isUppercaseValue = Boolean.TRUE.equals(inputFieldMetaData.getData(
                UifConstants.PostMetadata.INPUT_FIELD_IS_UPPERCASE));

        // add term as a like criteria
        Map<String, String> additionalCriteria = new HashMap<String, String>();
        if (isUppercaseValue) {
            additionalCriteria.put(suggestPostData.getValuePropertyName(), fieldTerm.toUpperCase() + "*");
        } else {
            additionalCriteria.put(suggestPostData.getValuePropertyName(), fieldTerm + "*");
        }

        // execute suggest query
        Collection<?> results = null;
        if (suggestQuery.hasConfiguredMethod()) {
            Object queryMethodResult = executeAttributeQueryMethod(suggestQuery, queryParameters, true, fieldTerm);
            if ((queryMethodResult != null) && (queryMethodResult instanceof Collection<?>)) {
                results = (Collection<?>) queryMethodResult;
            }
        } else {
            results = executeAttributeQueryCriteria(suggestQuery, queryParameters, additionalCriteria, new ArrayList<String>());
        }

        // build list of suggest data from result records
        if (results != null) {
            if (suggestPostData.isReturnFullQueryObject()) {
                queryResult.setResultData((List<Object>) results);
            } else {
                retrievePropertiesOnResults(queryResult, results, suggestPostData);
            }
        }

        return queryResult;
    }

    /**
     * Instead of returning the full object this method fills in queryResult with data that contain the properties
     * of each result object, as configured through the fieldSuggest, from the set of results.
     *
     * @param queryResult the queryResult to fill in
     * @param results the set of original results
     * @param suggestPostData post data for the suggest widget
     */
    protected void retrievePropertiesOnResults(AttributeQueryResult queryResult, Collection<?> results,
            Suggest.SuggestPostData suggestPostData) {
        List<Object> suggestData = new ArrayList<Object>();
        for (Object result : results) {
            if (result == null) {
                continue;
            }

            Map<String, String> propMap = new HashMap<String, String>();

            // if result is type string, use as both value and label
            if (result instanceof String) {
                propMap.put(UifParameters.VALUE, (String) result);
                propMap.put(UifParameters.LABEL, (String) result);
            }

            // value prop
            String suggestFieldValue = null;
            if (StringUtils.isNotBlank(suggestPostData.getValuePropertyName())) {
                suggestFieldValue = ObjectPropertyUtils.getPropertyValueAsText(result,
                        suggestPostData.getValuePropertyName());
            } else if (ObjectPropertyUtils.isReadableProperty(result, UifParameters.VALUE)) {
                suggestFieldValue = ObjectPropertyUtils.getPropertyValueAsText(result, UifParameters.VALUE);
            }

            if (suggestFieldValue != null) {
                propMap.put(UifParameters.VALUE, suggestFieldValue);
            }

            // label prop
            String suggestFieldLabel = null;
            if (StringUtils.isNotBlank(suggestPostData.getLabelPropertyName())) {
                suggestFieldLabel = ObjectPropertyUtils.getPropertyValueAsText(result,
                        suggestPostData.getLabelPropertyName());
            } else if (ObjectPropertyUtils.isReadableProperty(result, UifParameters.LABEL)) {
                suggestFieldLabel = ObjectPropertyUtils.getPropertyValueAsText(result, UifParameters.LABEL);
            }

            if (suggestFieldLabel != null) {
                propMap.put(UifParameters.LABEL, suggestFieldLabel);
            }

            // location suggest specific properties
            if (suggestPostData instanceof LocationSuggest.LocationSuggestPostData) {
                handleLocationSuggestProperties((LocationSuggest.LocationSuggestPostData) suggestPostData, result,
                        propMap);
            }

            // additional properties
            handleAdditionalSuggestProperties(suggestPostData, result, propMap);

            // only add if there was a property to send back
            if (!propMap.isEmpty()) {
                //TODO: need to apply formatter for field or have method in object property utils
                suggestData.add(propMap);
            }
        }

        queryResult.setResultData(suggestData);
    }

    /**
     * Handle the custom additionalProperties set back for a suggestion query.  These will be added to the propMap.
     *
     * @param suggestPostData post data for the suggest widget
     * @param result the result to pull properties from
     * @param propMap the propMap to add properties to
     */
    private void handleAdditionalSuggestProperties(Suggest.SuggestPostData suggestPostData, Object result,
            Map<String, String> propMap) {
        if (suggestPostData.getAdditionalPropertiesToReturn() != null) {
            //add properties for each valid property name
            for (String propName : suggestPostData.getAdditionalPropertiesToReturn()) {
                String propValue = null;

                if (StringUtils.isNotBlank(propName) && ObjectPropertyUtils.isReadableProperty(result, propName)) {
                    propValue = ObjectPropertyUtils.getPropertyValueAsText(result, propName);
                }

                if (propValue != null) {
                    propMap.put(propName, propValue);
                }
            }
        }
    }

    /**
     * Handle the LocationSuggest specific properties and add them to the map.
     *
     * @param suggestPostData post data for the suggest widget
     * @param result the result to pull properties from
     * @param propMap the propMap to add properties to
     */
    private void handleLocationSuggestProperties(LocationSuggest.LocationSuggestPostData suggestPostData, Object result,
            Map<String, String> propMap) {
        // href property
        String suggestHrefValue = null;
        if (StringUtils.isNotBlank(suggestPostData.getHrefPropertyName()) && ObjectPropertyUtils.isReadableProperty(
                result, suggestPostData.getHrefPropertyName())) {
            suggestHrefValue = ObjectPropertyUtils.getPropertyValueAsText(result,
                    suggestPostData.getHrefPropertyName());
        }

        // add if found
        if (suggestHrefValue != null) {
            propMap.put(suggestPostData.getHrefPropertyName(), suggestHrefValue);
        }

        // url addition/appendage property
        String addUrlValue = null;
        if (StringUtils.isNotBlank(suggestPostData.getAdditionalUrlPathPropertyName()) &&
                ObjectPropertyUtils.isReadableProperty(result, suggestPostData.getAdditionalUrlPathPropertyName())) {
            addUrlValue = ObjectPropertyUtils.getPropertyValueAsText(result,
                    suggestPostData.getAdditionalUrlPathPropertyName());
        }

        // add if found
        if (addUrlValue != null) {
            propMap.put(suggestPostData.getAdditionalUrlPathPropertyName(), addUrlValue);
        }

        if (suggestPostData.getRequestParameterPropertyNames() == null) {
            return;
        }

        // add properties for each valid requestParameter property name
        for (String key : suggestPostData.getRequestParameterPropertyNames().keySet()) {
            String prop = suggestPostData.getRequestParameterPropertyNames().get(key);
            String propValue = null;

            if (StringUtils.isNotBlank(prop) && ObjectPropertyUtils.isReadableProperty(result, prop)) {
                propValue = ObjectPropertyUtils.getPropertyValueAsText(result, prop);
            }

            if (propValue != null) {
                propMap.put(prop, propValue);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributeQueryResult performFieldQuery(ViewPostMetadata viewPostMetadata, String fieldId,
            Map<String, String> queryParameters) {
        AttributeQueryResult queryResult = new AttributeQueryResult();

        // retrieve attribute field from view index
        ComponentPostMetadata inputFieldMetaData = viewPostMetadata.getComponentPostMetadata(fieldId);
        if (inputFieldMetaData == null) {
            throw new RuntimeException("Unable to find attribute field instance for id: " + fieldId);
        }

        AttributeQuery fieldQuery = (AttributeQuery) inputFieldMetaData
                .getData(UifConstants.PostMetadata.INPUT_FIELD_ATTRIBUTE_QUERY);
        if (fieldQuery == null) {
            throw new RuntimeException("Field query not defined for field instance with id: " + fieldId);
        }

        // execute query and get result
        Object resultObject = null;
        if (fieldQuery.hasConfiguredMethod()) {
            Object queryMethodResult = executeAttributeQueryMethod(fieldQuery, queryParameters, false, null);
            if (queryMethodResult != null) {
                // if method returned the result then no further processing needed
                if (queryMethodResult instanceof AttributeQueryResult) {
                    return (AttributeQueryResult) queryMethodResult;
                }

                // if method returned collection, take first record
                if (queryMethodResult instanceof Collection<?>) {
                    Collection<?> methodResultCollection = (Collection<?>) queryMethodResult;
                    if (!methodResultCollection.isEmpty()) {
                        resultObject = methodResultCollection.iterator().next();
                    }
                } else {
                    resultObject = queryMethodResult;
                }
            }
        } else {
            // execute field query as object lookup
            Collection<?> results = executeAttributeQueryCriteria(fieldQuery, queryParameters, null,
                    new ArrayList<String>(queryParameters.keySet()));

            if ((results != null) && !results.isEmpty()) {
                // expect only one returned row for field query
                if (results.size() > 1) {
                    //finding too many results in a not found message (not specific enough)
                    resultObject = null;
                } else {
                    resultObject = results.iterator().next();
                }
            }
        }

        if (resultObject != null) {
            // build result field data map
            Map<String, String> resultFieldData = new HashMap<String, String>();
            for (String fromField : fieldQuery.getReturnFieldMapping().keySet()) {
                String returnField = fieldQuery.getReturnFieldMapping().get(fromField);

                String fieldValueStr = "";
                fieldValueStr = ObjectPropertyUtils.getPropertyValueAsText(resultObject, fromField);

                resultFieldData.put(returnField, fieldValueStr);
            }
            queryResult.setResultFieldData(resultFieldData);

            fieldQuery.setReturnMessageText("");
        } else {
            // add data not found message
            if (fieldQuery.isRenderNotFoundMessage()) {
                String messageTemplate = getConfigurationService().getPropertyValueAsString(
                        UifConstants.MessageKeys.QUERY_DATA_NOT_FOUND);
                String message = MessageFormat.format(messageTemplate, inputFieldMetaData.getData(
                        UifConstants.PostMetadata.LABEL));
                fieldQuery.setReturnMessageText(message.toLowerCase());
            }
        }

        // set message and message style classes on query result
        queryResult.setResultMessage(fieldQuery.getReturnMessageText());
        queryResult.setResultMessageStyleClasses(fieldQuery.getReturnMessageStyleClasses());

        return queryResult;
    }

    /**
     * Prepares the method configured on the attribute query then performs the method invocation
     *
     * @param attributeQuery attribute query instance to execute
     * @param queryParameters map of query parameters that provide values for the method arguments
     * @param isSuggestQuery indicates whether the query is for forming suggest options
     * @param queryTerm if being called for a suggest, the term for the query field
     * @return type depends on method being invoked, could be AttributeQueryResult in which
     * case the method has prepared the return result, or an Object that needs to be parsed for the result
     */
    protected Object executeAttributeQueryMethod(AttributeQuery attributeQuery, Map<String, String> queryParameters,
            boolean isSuggestQuery, String queryTerm) {
        String queryMethodToCall = attributeQuery.getQueryMethodToCall();
        MethodInvokerConfig queryMethodInvoker = attributeQuery.getQueryMethodInvokerConfig();

        if (queryMethodInvoker == null) {
            queryMethodInvoker = new MethodInvokerConfig();
        }

        // if method not set on invoker, use queryMethodToCall, note staticMethod could be set(don't know since
        // there is not a getter), if so it will override the target method in prepare
        if (StringUtils.isBlank(queryMethodInvoker.getTargetMethod())) {
            queryMethodInvoker.setTargetMethod(queryMethodToCall);
        }

        // setup query method arguments
        List<Object> arguments = new ArrayList<Object>();
        if ((attributeQuery.getQueryMethodArgumentFieldList() != null) &&
                (!attributeQuery.getQueryMethodArgumentFieldList().isEmpty())) {
            // retrieve argument types for conversion and verify method arguments
            int numQueryMethodArguments = attributeQuery.getQueryMethodArgumentFieldList().size();
            if (isSuggestQuery) {
                numQueryMethodArguments += 1;
            }

            // Empty arguments used to handle overloaded method case
            queryMethodInvoker.setArguments(new Object[numQueryMethodArguments]);
            Class<?>[] argumentTypes = queryMethodInvoker.getArgumentTypes();

            if ((argumentTypes == null) || (argumentTypes.length != numQueryMethodArguments)) {
                throw new RuntimeException(
                        "Query method argument field list size does not match found number of method arguments");
            }

            for (int i = 0; i < attributeQuery.getQueryMethodArgumentFieldList().size(); i++) {
                String methodArgumentFromField = attributeQuery.getQueryMethodArgumentFieldList().get(i);
                if (queryParameters.containsKey(methodArgumentFromField)) {
                    arguments.add(queryParameters.get(methodArgumentFromField));
                } else {
                    arguments.add(null);
                }
            }
        }

        if (isSuggestQuery) {
            arguments.add(queryTerm);
        }

        queryMethodInvoker.setArguments(arguments.toArray());

        try {
            queryMethodInvoker.prepare();

            return queryMethodInvoker.invoke();
        } catch (Exception e) {
            throw new RuntimeException("Unable to invoke query method: " + queryMethodInvoker.getTargetMethod(), e);
        }
    }

    /**
     * Prepares a query using the configured data object, parameters, and criteria, then executes
     * the query and returns the result Collection
     *
     * @param attributeQuery attribute query instance to perform query for
     * @param queryParameters map of parameters that will be used in the query criteria
     * @param additionalCriteria map of additional name/value pairs to add to the critiera
     * @param wildcardAsLiteralPropertyNames - List of property names with wildcards disabled
     * @return results of query
     */
    protected Collection<?> executeAttributeQueryCriteria(AttributeQuery attributeQuery,
            Map<String, String> queryParameters, Map<String, String> additionalCriteria,
            List<String> wildcardAsLiteralPropertyNames) {
        // build criteria for query
        boolean allQueryFieldsPresent = true;

        Map<String, String> queryCriteria = new HashMap<String, String>();
        for (String fieldName : attributeQuery.getQueryFieldMapping().values()) {
            if (queryParameters.containsKey(fieldName) && StringUtils.isNotBlank(queryParameters.get(fieldName))) {
                queryCriteria.put(fieldName, queryParameters.get(fieldName));
            } else {
                allQueryFieldsPresent = false;
                break;
            }
        }

        // for a field query we need all the criteria
        if (!allQueryFieldsPresent) {
            attributeQuery.setRenderNotFoundMessage(false);

            return null;
        }

        // add any static criteria
        for (String fieldName : attributeQuery.getAdditionalCriteria().keySet()) {
            queryCriteria.put(fieldName, attributeQuery.getAdditionalCriteria().get(fieldName));
        }

        // add additional criteria
        if (additionalCriteria != null) {
            queryCriteria.putAll(additionalCriteria);
        }

        Class<?> queryClass;
        try {
            queryClass = Class.forName(attributeQuery.getDataObjectClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "Invalid data object class given for suggest query: " + attributeQuery.getDataObjectClassName(), e);
        }

        // run query
        Collection<?> results = getLookupService().findCollectionBySearchHelper(queryClass, queryCriteria,
                wildcardAsLiteralPropertyNames, true, null);

        // sort results
        if (!attributeQuery.getSortPropertyNames().isEmpty() && (results != null) && (results.size() > 1)) {
            Collections.sort((List<?>) results, new BeanPropertyComparator(attributeQuery.getSortPropertyNames()));
        }

        return results;
    }

    /**
     * Gets the lookup service
     *
     * @return LookupService lookup service
     */
    protected LookupService getLookupService() {
        if (lookupService == null) {
            lookupService = KRADServiceLocatorWeb.getLookupService();
        }

        return lookupService;
    }

    /**
     * Sets the lookup service
     *
     * @param lookupService
     */
    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    /**
     * Gets the configuration service
     *
     * @return configuration service
     */
    protected ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = CoreApiServiceLocator.getKualiConfigurationService();
        }

        return configurationService;
    }

    /**
     * Sets the configuration service
     *
     * @param configurationService
     */
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
