package org.kuali.rice.krad.uif.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.container.View;
import org.kuali.rice.krad.uif.core.MethodInvokerConfig;
import org.kuali.rice.krad.uif.field.AttributeField;
import org.kuali.rice.krad.uif.field.AttributeQuery;
import org.kuali.rice.krad.uif.field.AttributeQueryResult;
import org.kuali.rice.krad.uif.service.AttributeQueryService;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.widget.Suggest;
import org.kuali.rice.krad.util.BeanPropertyComparator;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of <code>AttributeQueryService</code> that prepares the attribute queries and
 * delegates to the <code>LookupService</code>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class AttributeQueryServiceImpl implements AttributeQueryService {

    private LookupService lookupService;
    private ConfigurationService configurationService;

    /**
     * @see org.kuali.rice.krad.uif.service.AttributeQueryService#performFieldSuggestQuery(
     *org.kuali.rice.krad.uif.container.View, java.lang.String, java.lang.String, java.util.Map<java.lang.String,
     *      java.lang.String>)
     */
    @Override
    public AttributeQueryResult performFieldSuggestQuery(View view, String fieldId, String fieldTerm,
            Map<String, String> queryParameters) {
        AttributeQueryResult queryResult = new AttributeQueryResult();

        // retrieve attribute field from view index
        AttributeField attributeField = (AttributeField) view.getViewIndex().getComponentById(fieldId);
        if (attributeField == null) {
            throw new RuntimeException("Unable to find attribute field instance for id: " + fieldId);
        }

        Suggest fieldSuggest = attributeField.getFieldSuggest();
        AttributeQuery suggestQuery = fieldSuggest.getSuggestQuery();

        // add term as a like criteria
        Map<String, String> additionalCriteria = new HashMap<String, String>();
        additionalCriteria.put(fieldSuggest.getSourcePropertyName(), fieldTerm + "*");

        // execute suggest query
        Collection<?> results = null;
        if (suggestQuery.hasConfiguredMethod()) {
            Object queryMethodResult = executeAttributeQueryMethod(view, suggestQuery, queryParameters);
            if ((queryMethodResult != null) && (queryMethodResult instanceof Collection<?>)) {
                results = (Collection<?>) queryMethodResult;
            }
        } else {
            results = executeAttributeQueryCriteria(suggestQuery, queryParameters, additionalCriteria);
        }

        // build list of suggest data from result records
        if (results != null) {
            List<String> suggestData = new ArrayList<String>();
            for (Object result : results) {
                Object suggestFieldValue =
                        ObjectPropertyUtils.getPropertyValue(result, fieldSuggest.getSourcePropertyName());
                if (suggestFieldValue != null) {
                    // TODO: need to apply formatter for field or have method in object property utils
                    suggestData.add(suggestFieldValue.toString());
                }
            }

            queryResult.setResultData(suggestData);
        }

        return queryResult;
    }

    /**
     * @see org.kuali.rice.krad.uif.service.AttributeQueryService#performFieldQuery(org.kuali.rice.krad.uif.container.View,
     *      java.lang.String, java.util.Map<java.lang.String,java.lang.String>)
     */
    @Override
    public AttributeQueryResult performFieldQuery(View view, String fieldId, Map<String, String> queryParameters) {
        AttributeQueryResult queryResult = new AttributeQueryResult();

        // retrieve attribute field from view index
        AttributeField attributeField = (AttributeField) view.getViewIndex().getComponentById(fieldId);
        if (attributeField == null) {
            throw new RuntimeException("Unable to find attribute field instance for id: " + fieldId);
        }

        AttributeQuery fieldQuery = attributeField.getFieldAttributeQuery();
        if (fieldQuery == null) {
            throw new RuntimeException("Field query not defined for field instance with id: " + fieldId);
        }

        // execute query and get result
        Object resultObject = null;
        if (fieldQuery.hasConfiguredMethod()) {
            Object queryMethodResult = executeAttributeQueryMethod(view, fieldQuery, queryParameters);
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
            Collection<?> results = executeAttributeQueryCriteria(fieldQuery, queryParameters, null);

            if ((results != null) && !results.isEmpty()) {
                // expect only one returned row for field query
                if (results.size() > 1) {
                    throw new RuntimeException("");
                }

                resultObject = results.iterator().next();
            }
        }

        if (resultObject != null) {
            // build result field data map
            Map<String, String> resultFieldData = new HashMap<String, String>();
            for (String fromField : fieldQuery.getReturnFieldMapping().keySet()) {
                String returnField = fieldQuery.getReturnFieldMapping().get(fromField);

                String fieldValueStr = "";
                Object fieldValue = ObjectPropertyUtils.getPropertyValue(resultObject, fromField);
                if (fieldValue != null) {
                    fieldValueStr = fieldValue.toString();
                }
                resultFieldData.put(returnField, fieldValueStr);
            }
            queryResult.setResultFieldData(resultFieldData);

            fieldQuery.setReturnMessageText("");
        } else {
            // add data not found message
            if (fieldQuery.isRenderNotFoundMessage()) {
                String messageTemplate =
                        getConfigurationService().getPropertyValueAsString(
                                UifConstants.MessageKeys.QUERY_DATA_NOT_FOUND);
                String message = MessageFormat.format(messageTemplate, attributeField.getLabel());
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
     * @param view - view instance the field is contained within
     * @param attributeQuery - attribute query instance to execute
     * @param queryParameters - map of query parameters that provide values for the method arguments
     * @return Object type depends on method being invoked, could be AttributeQueryResult in which
     *         case the method has prepared the return result, or an Object that needs to be parsed for the result
     */
    protected Object executeAttributeQueryMethod(View view, AttributeQuery attributeQuery,
            Map<String, String> queryParameters) {
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

        // if target class or object not set, use view helper service
        if ((queryMethodInvoker.getTargetClass() == null) && (queryMethodInvoker.getTargetObject() == null)) {
            queryMethodInvoker.setTargetObject(view.getViewHelperService());
        }

        // setup query method arguments
        Object[] arguments = null;
        if ((attributeQuery.getQueryMethodArgumentFieldList() != null) &&
                (!attributeQuery.getQueryMethodArgumentFieldList().isEmpty())) {
            // retrieve argument types for conversion
            Class[] argumentTypes = queryMethodInvoker.getArgumentTypes();
            if ((argumentTypes == null) ||
                    (argumentTypes.length != attributeQuery.getQueryMethodArgumentFieldList().size())) {
                throw new RuntimeException(
                        "Query method argument field list size does not match found method argument list size");
            }

            arguments = new Object[attributeQuery.getQueryMethodArgumentFieldList().size()];
            for (int i = 0; i < attributeQuery.getQueryMethodArgumentFieldList().size(); i++) {
                String methodArgumentFromField = attributeQuery.getQueryMethodArgumentFieldList().get(i);
                if (queryParameters.containsKey(methodArgumentFromField)) {
                    arguments[i] = queryParameters.get(methodArgumentFromField);
                } else {
                    arguments[i] = null;
                }
            }
        }
        queryMethodInvoker.setArguments(arguments);

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
     * @param attributeQuery - attribute query instance to perform query for
     * @param queryParameters - map of parameters that will be used in the query criteria
     * @param additionalCriteria - map of additional name/value pairs to add to the critiera
     * @return Collection<?> results of query
     */
    protected Collection<?> executeAttributeQueryCriteria(AttributeQuery attributeQuery,
            Map<String, String> queryParameters, Map<String, String> additionalCriteria) {
        Collection<?> results = null;

        // build criteria for query
        Map<String, String> queryCriteria = new HashMap<String, String>();
        for (String fieldName : attributeQuery.getQueryFieldMapping().keySet()) {
            if (queryParameters.containsKey(fieldName) && StringUtils.isNotBlank(queryParameters.get(fieldName))) {
                queryCriteria.put(fieldName, queryParameters.get(fieldName));
            }
        }

        // add any static criteria
        for (String fieldName : attributeQuery.getAdditionalCriteria().keySet()) {
            queryCriteria.put(fieldName, attributeQuery.getAdditionalCriteria().get(fieldName));
        }

        // add additional criteria
        if (additionalCriteria != null) {
            queryCriteria.putAll(additionalCriteria);
        }

        Class<?> queryClass = null;
        try {
            queryClass = Class.forName(attributeQuery.getDataObjectClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "Invalid data object class given for suggest query: " + attributeQuery.getDataObjectClassName(), e);
        }

        // run query
        results = getLookupService().findCollectionBySearchUnbounded(queryClass, queryCriteria);

        // sort results
        if (!attributeQuery.getSortPropertyNames().isEmpty() && (results != null) && (results.size() > 1)) {
            Collections.sort((List<?>) results, new BeanPropertyComparator(attributeQuery.getSortPropertyNames()));
        }

        return results;
    }

    protected LookupService getLookupService() {
        if (lookupService == null) {
            lookupService = KRADServiceLocatorWeb.getLookupService();
        }

        return lookupService;
    }

    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    protected ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = KRADServiceLocator.getKualiConfigurationService();
        }

        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
