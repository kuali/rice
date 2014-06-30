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

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.TypeUtils;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.rice.krad.service.DataObjectAuthorizationService;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.control.FilterableLookupCriteriaControl;
import org.kuali.rice.krad.uif.control.FilterableLookupCriteriaControlPostData;
import org.kuali.rice.krad.uif.control.HiddenControl;
import org.kuali.rice.krad.uif.control.ValueConfiguredControl;
import org.kuali.rice.krad.uif.element.Link;
import org.kuali.rice.krad.uif.field.InputField;
import org.kuali.rice.krad.uif.lifecycle.ViewPostMetadata;
import org.kuali.rice.krad.uif.service.impl.ViewHelperServiceImpl;
import org.kuali.rice.krad.uif.util.ComponentUtils;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.UrlFactory;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * View helper service that implements {@link Lookupable} and executes a search using the
 * {@link org.kuali.rice.krad.service.LookupService}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @see LookupForm
 * @see LookupView
 * @see org.kuali.rice.krad.service.LookupService
 */
public class LookupableImpl extends ViewHelperServiceImpl implements Lookupable {
    private static final long serialVersionUID = 1885161468871327740L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupableImpl.class);

    private Class<?> dataObjectClass;

    private transient DataObjectAuthorizationService dataObjectAuthorizationService;
    private transient DocumentDictionaryService documentDictionaryService;
    private transient LookupService lookupService;
    private transient EncryptionService encryptionService;

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<?> performSearch(LookupForm form, Map<String, String> searchCriteria, boolean bounded) {
        // removed blank search values and decrypt any encrypted search values
        Map<String, String> adjustedSearchCriteria = processSearchCriteria(form, searchCriteria);

        boolean isValidCriteria = validateSearchParameters(form, adjustedSearchCriteria);
        if (!isValidCriteria) {
            return new ArrayList<Object>();
        }

        List<String> wildcardAsLiteralSearchCriteria = identifyWildcardDisabledFields(form, adjustedSearchCriteria);

        // return empty search results (none found) when the search doesn't have any adjustedSearchCriteria although
        // a filtered search criteria is specified
        if (adjustedSearchCriteria == null) {
            return new ArrayList<Object>();
        }

        // if this class is an EBO, just call the module service to get the results
        if (ExternalizableBusinessObject.class.isAssignableFrom(getDataObjectClass())) {
            return getSearchResultsForEBO(adjustedSearchCriteria, !bounded);
        }

        // if any of the properties refer to an embedded EBO, call the EBO lookups first and apply to the local lookup
        try {
            if (LookupUtils.hasExternalBusinessObjectProperty(getDataObjectClass(), adjustedSearchCriteria)) {
                adjustedSearchCriteria = LookupUtils.adjustCriteriaForNestedEBOs(getDataObjectClass(),
                        adjustedSearchCriteria, !bounded);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Passing these results into the lookup service: " + adjustedSearchCriteria);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error trying to check for nested external business objects", e);
        } catch (InstantiationException e1) {
            throw new RuntimeException("Error trying to check for nested external business objects", e1);
        }

        Integer searchResultsLimit = null;
        if (bounded) {
            searchResultsLimit = LookupUtils.getSearchResultsLimit(getDataObjectClass(), form);
        }

        // invoke the lookup search to carry out the search
        Collection<?> searchResults = executeSearch(adjustedSearchCriteria, wildcardAsLiteralSearchCriteria, bounded,
                searchResultsLimit);

        generateLookupResultsMessages(adjustedSearchCriteria, searchResults, bounded, searchResultsLimit);

        Collection<?> sortedResults;
        if (searchResults != null) {
            sortedResults = new ArrayList<Object>(searchResults);

            sortSearchResults(form, (List<?>) sortedResults);
        } else {
            sortedResults = new ArrayList<Object>();
        }

        return sortedResults;
    }

    /**
     * Invoked to execute the search with the given criteria and restrictions.
     *
     * @param adjustedSearchCriteria map of criteria that has been adjusted (encyrption, ebos, etc)
     * @param wildcardAsLiteralSearchCriteria map of criteria to treat as literals (wildcards disabled)
     * @param bounded indicates whether the search should be bounded
     * @param searchResultsLimit for bounded searches, the result limit
     * @return Collection<?> collection of data object instances from the search results
     */
    protected Collection<?> executeSearch(Map<String, String> adjustedSearchCriteria,
            List<String> wildcardAsLiteralSearchCriteria, boolean bounded, Integer searchResultsLimit) {
        return getLookupService().findCollectionBySearchHelper(getDataObjectClass(),
                adjustedSearchCriteria, wildcardAsLiteralSearchCriteria, !bounded, searchResultsLimit);
    }

    /**
     * Filters the search criteria to be used with the lookup.
     *
     * <p>Processing entails primarily of the removal of filtered and unused/blank search criteria.  Encrypted field
     * values are decrypted, and date range fields are combined into a single criteria entry.</p>
     *
     * @param lookupForm lookup form instance containing the lookup data
     * @param searchCriteria map of criteria to process
     * @return map of processed criteria
     */
    protected Map<String, String> processSearchCriteria(LookupForm lookupForm, Map<String, String> searchCriteria) {
        Map<String, InputField> criteriaFields = new HashMap<String, InputField>();
        if (lookupForm.getView() != null) {
            criteriaFields = getCriteriaFieldsForValidation((LookupView) lookupForm.getView(), lookupForm);
        }

        // combine date range criteria
        Map<String, String> filteredSearchCriteria = LookupUtils.preprocessDateFields(searchCriteria);

        // allow lookup inputs to filter the criteria
        for (String fieldName : searchCriteria.keySet()) {
            InputField inputField = criteriaFields.get(fieldName);

            if ((inputField == null) || !(inputField instanceof LookupInputField)) {
                continue;
            }

            LookupInputField lookupInputField = (LookupInputField) inputField;
            String propertyName = lookupInputField.getPropertyName();

            // get the post data for the filterable controls
            ViewPostMetadata viewPostMetadata = lookupForm.getViewPostMetadata();
            Object componentPostData = viewPostMetadata.getComponentPostData(lookupForm.getViewId(), UifConstants.PostMetadata.FILTERABLE_LOOKUP_CRITERIA);
            Map<String, FilterableLookupCriteriaControlPostData> filterableLookupCriteria = (Map<String, FilterableLookupCriteriaControlPostData>) componentPostData;

            // first filter the results using the filter on the control
            if (filterableLookupCriteria != null && filterableLookupCriteria.containsKey(propertyName)) {
                FilterableLookupCriteriaControlPostData postData = filterableLookupCriteria.get(propertyName);
                Class<? extends FilterableLookupCriteriaControl> controlClass = postData.getControlClass();
                FilterableLookupCriteriaControl control = KRADUtils.createNewObjectFromClass(controlClass);

                filteredSearchCriteria = control.filterSearchCriteria(propertyName, filteredSearchCriteria, postData);
            }

            // second filter the results using the filter in the input field
            filteredSearchCriteria = lookupInputField.filterSearchCriteria(filteredSearchCriteria);

            // early return if we have no results
            if (filteredSearchCriteria == null) {
                return null;
            }
        }

        // decryption any encrypted search values
        Map<String, String> processedSearchCriteria = new HashMap<String, String>();
        for (String fieldName : filteredSearchCriteria.keySet()) {
            String fieldValue = filteredSearchCriteria.get(fieldName);

            // do not add hidden or blank criteria
            InputField inputField = criteriaFields.get(fieldName);
            if (((inputField != null) && (inputField.getControl() instanceof HiddenControl)) || StringUtils.isBlank(
                    fieldValue)) {
                continue;
            }

            if (fieldValue.endsWith(EncryptionService.ENCRYPTION_POST_PREFIX)) {
                String encryptedValue = StringUtils.removeEnd(fieldValue, EncryptionService.ENCRYPTION_POST_PREFIX);
                try {
                    fieldValue = getEncryptionService().decrypt(encryptedValue);
                } catch (GeneralSecurityException e) {
                    throw new RuntimeException("Error decrypting value for business object class " +
                            getDataObjectClass() + " attribute " + fieldName, e);
                }
            }

            processedSearchCriteria.put(fieldName, fieldValue);
        }

        return processedSearchCriteria;
    }

    /**
     * Determines which searchCriteria have been configured with wildcard characters disabled.
     *
     * @param lookupForm form used to collect search criteria
     * @param searchCriteria Map of property names and values to use as search parameters
     * @return List of property names which have wildcard characters disabled
     */
    protected List<String> identifyWildcardDisabledFields(LookupForm lookupForm, Map<String, String> searchCriteria) {
        List<String> wildcardAsLiteralPropertyNames = new ArrayList<String>();

        Map<String, InputField> criteriaFields = new HashMap<String, InputField>();
        if (lookupForm.getView() != null) {
            criteriaFields = getCriteriaFieldsForValidation((LookupView) lookupForm.getView(), lookupForm);
        }

        for (String fieldName : searchCriteria.keySet()) {
            InputField inputField = criteriaFields.get(fieldName);
            if ((inputField == null) || !(inputField instanceof LookupInputField)) {
                continue;
            }

            if ((LookupInputField.class.isAssignableFrom(inputField.getClass())) && (((LookupInputField) inputField)
                    .isDisableWildcardsAndOperators())) {
                wildcardAsLiteralPropertyNames.add(fieldName);
            }
        }

        return wildcardAsLiteralPropertyNames;
    }

    /**
     * Invoked to perform validation on the search criteria before the search is performed.
     *
     * <li>Check required criteria have a value</li>
     * <li>Check that criteria data type supports wildcards/operators</li>
     * <li>Check that wildcards/operators are not used on a secure criteria</li>
     * <li>Display info message when wildcards/operators are disabled</li>
     * <li>Throw exception when invalid criteria are specified</li>
     *
     * @param form lookup form instance containing the lookup data
     * @param searchCriteria map of criteria where key is search property name and value is
     * search value (which can include wildcards)
     * @return boolean true if validation was successful, false if there were errors and the search
     *         should not be performed
     */
    protected boolean validateSearchParameters(LookupForm form, Map<String, String> searchCriteria) {
        boolean valid = true;

        // The form view can't be relied upon since the complete lifecycle hasn't ran against it.  Instead
        // the viewPostMetadata is being used for the validation.
        // If the view was not previously posted then it's impossible to validate the search parameters because
        // of the missing viewPostMetadata.  When this happens we assume the search parameters are correct.
        // (Calling the search controller method directly without displaying the lookup first can cause
        // this situation.)
        if (form.getViewPostMetadata() == null) {
            return valid;
        }

        Set<String> unprocessedSearchCriteria = new HashSet<String>(searchCriteria.keySet());
        for (Map.Entry<String, Map<String, Object>> lookupCriteria :
                form.getViewPostMetadata().getLookupCriteria().entrySet()) {
            String propertyName = lookupCriteria.getKey();
            Map<String, Object> lookupCriteriaAttributes = lookupCriteria.getValue();

            unprocessedSearchCriteria.remove(propertyName);

            if (isCriteriaRequired(lookupCriteriaAttributes) && StringUtils.isBlank(searchCriteria.get(propertyName))) {
                GlobalVariables.getMessageMap().putError(propertyName, RiceKeyConstants.ERROR_REQUIRED,
                        getCriteriaLabel(form, (String) lookupCriteriaAttributes.get(
                                UifConstants.LookupCriteriaPostMetadata.COMPONENT_ID)));
            }

            ValidCharactersConstraint constraint = getSearchCriteriaConstraint(lookupCriteriaAttributes);
            if (constraint != null) {
                validateSearchParameterConstraint(form, propertyName, lookupCriteriaAttributes, searchCriteria.get(
                        propertyName), constraint);
            }

            if (searchCriteria.containsKey(propertyName)) {
                validateSearchParameterWildcardAndOperators(form, propertyName, lookupCriteriaAttributes,
                        searchCriteria.get(propertyName));
            }
        }

        if (!unprocessedSearchCriteria.isEmpty()) {
            throw new RuntimeException(
                    "Invalid search value sent for property name(s): " + unprocessedSearchCriteria.toString());
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            valid = false;
        }

        return valid;
    }

    /**
     * Validates that any wildcards contained within the search value are valid wildcards and allowed for the
     * property type for which the field is searching.
     *
     * @param form lookup form instance containing the lookup data
     * @param propertyName property name of the search criteria field to be validated
     * @param searchPropertyValue value given for field to search for
     */
    protected void validateSearchParameterWildcardAndOperators(LookupForm form, String propertyName,
            Map<String, Object> lookupCriteriaAttributes, String searchPropertyValue) {
        if (StringUtils.isBlank(searchPropertyValue)) {
            return;
        }

        // make sure a wildcard/operator is in the value
        boolean found = false;
        for (SearchOperator op : SearchOperator.QUERY_CHARACTERS) {
            String queryCharacter = op.op();

            if (searchPropertyValue.contains(queryCharacter)) {
                found = true;
            }
        }

        // no query characters to validate
        if (!found) {
            return;
        }

        if (isCriteriaWildcardDisabled(lookupCriteriaAttributes)) {
            Class<?> propertyType = ObjectPropertyUtils.getPropertyType(getDataObjectClass(), propertyName);

            if (TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType) ||
                    TypeUtils.isTemporalClass(propertyType)) {
                GlobalVariables.getMessageMap().putError(propertyName,
                        RiceKeyConstants.ERROR_WILDCARDS_AND_OPERATORS_NOT_ALLOWED_ON_FIELD, getCriteriaLabel(form,
                        (String) lookupCriteriaAttributes.get(UifConstants.LookupCriteriaPostMetadata.COMPONENT_ID)));
            } else if (TypeUtils.isStringClass(propertyType)) {
                GlobalVariables.getMessageMap().putInfo(propertyName,
                        RiceKeyConstants.INFO_WILDCARDS_AND_OPERATORS_TREATED_LITERALLY, getCriteriaLabel(form,
                        (String) lookupCriteriaAttributes.get(UifConstants.LookupCriteriaPostMetadata.COMPONENT_ID)));
            }
        } else if (isCriteriaSecure(lookupCriteriaAttributes)) {
            GlobalVariables.getMessageMap().putError(propertyName, RiceKeyConstants.ERROR_SECURE_FIELD,
                    getCriteriaLabel(form, (String) lookupCriteriaAttributes.get(
                            UifConstants.LookupCriteriaPostMetadata.COMPONENT_ID))
            );
        }
    }

    /**
     * Validates that the searchPropertyValue is a valid value based on any constraint that may exist for the property
     *
     * @param form lookup form instance containing the lookup data
     * @param propertyName property name of the search criteria field to be validated
     * @param lookupCriteriaAttributes attributes for the lookup criteria
     * @param searchPropertyValue value given for field to search for
     * @param validCharactersConstraint constraint on the lookup criteria field
     */
    protected void validateSearchParameterConstraint(LookupForm form, String propertyName,
            Map<String, Object> lookupCriteriaAttributes, String searchPropertyValue,
            ValidCharactersConstraint validCharactersConstraint) {
        if (StringUtils.isBlank(searchPropertyValue)) {
            return;
        }

        Matcher matcher = Pattern.compile(validCharactersConstraint.getValue()).matcher(searchPropertyValue);
        if (!matcher.find()) {
            String[] prefixParams = {getCriteriaLabel(form, (String) lookupCriteriaAttributes.get(
                    UifConstants.LookupCriteriaPostMetadata.COMPONENT_ID))};
            ErrorMessage errorMessage = new ErrorMessage(validCharactersConstraint.getMessageKey(),
                    validCharactersConstraint.getValidationMessageParamsArray());
            errorMessage.setMessagePrefixKey(UifConstants.Messages.PROPERTY_NAME_PREFIX);
            errorMessage.setMessagePrefixParameters(prefixParams);
            GlobalVariables.getMessageMap().putError(propertyName, errorMessage);
        }
    }

    /**
     * Returns the label of the search criteria field.
     *
     * @param form lookup form instance containing the lookup data
     * @param componentId component id of the search criteria field
     * @return label of the search criteria field
     */
    protected String getCriteriaLabel(LookupForm form, String componentId) {
        return (String) form.getViewPostMetadata().getComponentPostData(componentId, UifConstants.PostMetadata.LABEL);
    }

    /**
     * Indicator if wildcards and operators are disabled on this search criteria.
     *
     * @param lookupCriteria the viewPostMetadata with the attributes of the search criteria field
     * @return true if wildcards and operators are disabled, false otherwise
     */
    protected boolean isCriteriaWildcardDisabled(Map lookupCriteria) {
        return BooleanUtils.isTrue((Boolean) lookupCriteria.get(UifConstants.LookupCriteriaPostMetadata.DISABLE_WILDCARDS_AND_OPERATORS));
    }

    /**
     * Indicator if the search criteria is required.
     *
     * @param lookupCriteria the viewPostMetadata with the attributes of the search criteria field
     * @return true if the search criteria is required, false otherwise
     */
    protected boolean isCriteriaRequired(Map lookupCriteria) {
        return BooleanUtils.isTrue((Boolean) lookupCriteria.get(UifConstants.LookupCriteriaPostMetadata.REQUIRED));
    }

    /**
     * Indicator if the search criteria is on a secure field.
     *
     * @param lookupCriteria the viewPostMetadata with the attributes of the search criteria field
     * @return true if the search criteria is a secure field, false otherwise
     */
    protected boolean isCriteriaSecure(Map lookupCriteria) {
        return BooleanUtils.isTrue((Boolean) lookupCriteria.get(UifConstants.LookupCriteriaPostMetadata.SECURE_VALUE));
    }

    /**
     * Indicator if the search criteria has a valid Characters Constraint.
     *
     * @param lookupCriteria the viewPostMetadata with the attributes of the search criteria field
     * @return the ValidCharactersConstraint if the search criteria has a valid characters constraint
     */
    protected ValidCharactersConstraint getSearchCriteriaConstraint(Map lookupCriteria) {
        return (ValidCharactersConstraint) lookupCriteria.get(
                UifConstants.LookupCriteriaPostMetadata.VALID_CHARACTERS_CONSTRAINT);
    }

    /**
     * Generates messages for the user based on the search results.
     *
     * <p>Messages are generated for the number of results, if the results exceed the result limit, and if the
     * search was done using the primary keys for the data object.</p>
     *
     * @param searchCriteria map of search criteria that was used for the search
     * @param searchResults list of result data objects from the search
     * @param bounded whether the search was bounded
     * @param searchResultsLimit maximum number of search results to return
     */
    protected void generateLookupResultsMessages(Map<String, String> searchCriteria, Collection<?> searchResults,
            boolean bounded, Integer searchResultsLimit) {
        MessageMap messageMap = GlobalVariables.getMessageMap();

        Long searchResultsSize = Long.valueOf(0);
        if (searchResults instanceof CollectionIncomplete
                && ((CollectionIncomplete<?>) searchResults).getActualSizeIfTruncated() > 0) {
            searchResultsSize = ((CollectionIncomplete<?>) searchResults).getActualSizeIfTruncated();
        } else if (searchResults != null) {
            searchResultsSize = Long.valueOf(searchResults.size());
        }

        if (searchResultsSize == 0) {
            messageMap.putInfoForSectionId(UifConstants.MessageKeys.LOOKUP_RESULT_MESSAGES,
                    RiceKeyConstants.INFO_LOOKUP_RESULTS_NONE_FOUND);
        } else if (searchResultsSize == 1) {
            messageMap.putInfoForSectionId(UifConstants.MessageKeys.LOOKUP_RESULT_MESSAGES,
                    RiceKeyConstants.INFO_LOOKUP_RESULTS_DISPLAY_ONE);
        } else if (searchResultsSize > 1) {
            boolean resultsExceedsLimit =
                    bounded && (searchResultsLimit != null) && (searchResultsSize > searchResultsLimit);

            if (resultsExceedsLimit) {
                messageMap.putInfoForSectionId(UifConstants.MessageKeys.LOOKUP_RESULT_MESSAGES,
                        RiceKeyConstants.INFO_LOOKUP_RESULTS_EXCEEDS_LIMIT, searchResultsSize.toString(),
                        searchResultsLimit.toString());
            } else {
                messageMap.putInfoForSectionId(UifConstants.MessageKeys.LOOKUP_RESULT_MESSAGES,
                        RiceKeyConstants.INFO_LOOKUP_RESULTS_DISPLAY_ALL, searchResultsSize.toString());
            }
        }

        Boolean usingPrimaryKey = getLookupService().allPrimaryKeyValuesPresentAndNotWildcard(getDataObjectClass(),
                searchCriteria);

        if (usingPrimaryKey) {
            List<String> pkNames = getLegacyDataAdapter().listPrimaryKeyFieldNames(getDataObjectClass());

            List<String> pkLabels = new ArrayList<String>();
            for (String pkName : pkNames) {
                pkLabels.add(getDataDictionaryService().getAttributeLabel(getDataObjectClass(), pkName));
            }

            messageMap.putInfoForSectionId(UifConstants.MessageKeys.LOOKUP_RESULT_MESSAGES,
                    RiceKeyConstants.INFO_LOOKUP_RESULTS_USING_PRIMARY_KEY, StringUtils.join(pkLabels, ","));
        }
    }

    /**
     * Sorts the given list of search results based on the lookup view's configured sort attributes.
     *
     * <p>First if the posted view exists we grab the sort attributes from it. This will take into account expressions
     * that might have been configured on the sort attributes. If the posted view does not exist (because we did a
     * search from a get request or form session storage is off), we get the sort attributes from the view that we
     * will be rendered (and was initialized before controller call). However, expressions will not be evaluated yet,
     * thus if expressions were configured we don't know the results and can not sort the list</p>
     *
     * @param form lookup form instance containing view information
     * @param searchResults list of search results to sort
     * @TODO: revisit this when we have a solution for the posted view problem
     */
    protected void sortSearchResults(LookupForm form, List<?> searchResults) {
        List<String> defaultSortColumns = null;
        boolean defaultSortAscending = true;

        if (form.getView() != null) {
            defaultSortColumns = ((LookupView) form.getView()).getDefaultSortAttributeNames();
            defaultSortAscending = ((LookupView) form.getView()).isDefaultSortAscending();
        }

        boolean hasExpression = false;
        if (defaultSortColumns != null) {
            for (String sortColumn : defaultSortColumns) {
                if (sortColumn == null) {
                    hasExpression = true;
                }
            }
        }

        if (hasExpression) {
            defaultSortColumns = null;
        }

        if ((defaultSortColumns != null) && (!defaultSortColumns.isEmpty())) {
            BeanPropertyComparator comparator = new BeanPropertyComparator(defaultSortColumns, true);
            if (defaultSortAscending) {
                Collections.sort(searchResults, comparator);
            } else {
                Collections.sort(searchResults, Collections.reverseOrder(comparator));
            }
        }
    }

    /**
     * Performs a search against an {@link org.kuali.rice.krad.bo.ExternalizableBusinessObject} by invoking the
     * module service
     *
     * @param searchCriteria map of criteria currently set
     * @param unbounded indicates whether the complete result should be returned.  When set to false the result is
     * limited (if necessary) to the max search result limit configured.
     * @return list of result objects, possibly bounded
     */
    protected List<?> getSearchResultsForEBO(Map<String, String> searchCriteria, boolean unbounded) {
        ModuleService eboModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(
                getDataObjectClass());

        BusinessObjectEntry ddEntry = eboModuleService.getExternalizableBusinessObjectDictionaryEntry(
                getDataObjectClass());

        Map<String, String> filteredFieldValues = new HashMap<String, String>();
        for (String fieldName : searchCriteria.keySet()) {
            if (ddEntry.getAttributeNames().contains(fieldName)) {
                filteredFieldValues.put(fieldName, searchCriteria.get(fieldName));
            }
        }

        Map<String, Object> translatedValues  = KRADUtils.coerceRequestParameterTypes(
                (Class<? extends ExternalizableBusinessObject>) getDataObjectClass(), filteredFieldValues);

        List<?> searchResults = eboModuleService.getExternalizableBusinessObjectsListForLookup(
                (Class<? extends ExternalizableBusinessObject>) getDataObjectClass(), (Map) translatedValues,
                unbounded);

        return searchResults;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> performClear(LookupForm form, Map<String, String> lookupCriteria) {
        Map<String, String> clearedLookupCriteria = new HashMap<String, String>();

        Map<String, InputField> criteriaFieldMap = new HashMap<String, InputField>();
        if (form.getView() != null) {
            criteriaFieldMap = getCriteriaFieldsForValidation((LookupView) form.getView(), form);
        }

        // fields marked as read only through the initial request should not be cleared
        List<String> readOnlyFieldsList = form.getReadOnlyFieldsList();

        for (Map.Entry<String, String> searchKeyValue : lookupCriteria.entrySet()) {
            String searchPropertyName = searchKeyValue.getKey();
            String searchPropertyValue = searchKeyValue.getValue();

            InputField inputField = criteriaFieldMap.get(searchPropertyName);

            if (readOnlyFieldsList == null || !readOnlyFieldsList.contains(searchPropertyName)) {
                if (inputField != null && inputField.getDefaultValue() != null  ) {
                    searchPropertyValue = inputField.getDefaultValue().toString();
                } else {
                    searchPropertyValue = "";
                }
            }

            clearedLookupCriteria.put(searchPropertyName, searchPropertyValue);
        }

        return clearedLookupCriteria;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildReturnUrlForResult(Link returnLink, Object model) {
        LookupForm lookupForm = (LookupForm) model;

        Map<String, Object> returnLinkContext = returnLink.getContext();
        LookupView lookupView = returnLinkContext == null ? null : (LookupView) returnLinkContext
                .get(UifConstants.ContextVariableNames.VIEW);
        Object dataObject = returnLinkContext == null ? null : returnLinkContext
                .get(UifConstants.ContextVariableNames.LINE);

        // don't render return link if the object is null or if the row is not returnable
        if ((dataObject == null) || (!isResultReturnable(dataObject))) {
            returnLink.setRender(false);

            return;
        }

        String dataReturnValue = "true";
        if (lookupForm.isReturnByScript()) {
            Map<String, String> translatedKeyValues = getTranslatedReturnKeyValues(lookupView, lookupForm, dataObject);

            dataReturnValue = ScriptUtils.translateValue(translatedKeyValues);

            returnLink.setHref("#");
        } else if (StringUtils.isBlank(returnLink.getHref())) {
            String href = getReturnUrl(lookupView, lookupForm, dataObject);

            if (StringUtils.isNotBlank(href)) {
                returnLink.setHref(href);
            } else {
                returnLink.setRender(false);
                return;
            }

            String target = lookupForm.getReturnTarget();

            if (StringUtils.isNotBlank(target)) {
                returnLink.setTarget(target);
            }
        }

        // add data attribute for attaching event handlers on the return links
        returnLink.addDataAttribute(UifConstants.DataAttributes.RETURN, dataReturnValue);

        // build return link title if not already set
        if (StringUtils.isBlank(returnLink.getTitle())) {
            String linkLabel = getConfigurationService().getPropertyValueAsString(
                    KRADConstants.Lookup.TITLE_RETURN_URL_PREPENDTEXT_PROPERTY);

            Map<String, String> returnKeyValues = getReturnKeyValues(lookupView, lookupForm, dataObject);

            String title = KRADUtils.buildAttributeTitleString(linkLabel, getDataObjectClass(), returnKeyValues);
            returnLink.setTitle(title);
        }
    }

    /**
     * Determines whether a given data object that's returned as one of the lookup's results is considered returnable,
     * which means that for single-value lookups, a "return value" link may be rendered, and for multiple
     * value lookups, a checkbox is rendered.
     *
     * <p>Note that this can be part of an authorization mechanism, but not the complete authorization mechanism. The
     * component that invoked the lookup/ lookup caller (e.g. document, nesting lookup, etc.) needs to check
     * that the object that was passed to it was returnable as well because there are ways around this method
     * (e.g. crafting a custom return URL).</p>
     *
     * @param dataObject an object from the search result set
     * @return true if the row is returnable and false if it is not
     */
    protected boolean isResultReturnable(Object dataObject) {
        return true;
    }

    /**
     * Builds the URL for returning the given data object result row.
     *
     * <p>Note return URL will only be built if a return location is specified on the lookup form</p>
     *
     * @param lookupView lookup view instance containing lookup configuration
     * @param lookupForm lookup form instance containing the data
     * @param dataObject data object instance for the current line and for which the return URL is being built
     * @return String return URL or blank if URL cannot be built
     */
    protected String getReturnUrl(LookupView lookupView, LookupForm lookupForm, Object dataObject) {
        Properties props = getReturnUrlParameters(lookupView, lookupForm, dataObject);

        String href = "";
        if (StringUtils.isNotBlank(lookupForm.getReturnLocation())) {
            href = UrlFactory.parameterizeUrl(lookupForm.getReturnLocation(), props);
        }

        return href;
    }

    /**
     * Builds up a {@code Properties} object that will be used to provide the request parameters for the
     * return URL link
     *
     * @param lookupView lookup view instance containing lookup configuration
     * @param lookupForm lookup form instance containing the data
     * @param dataObject data object instance for the current line and for which the return URL is being built
     * @return Properties instance containing request parameters for return URL
     */
    protected Properties getReturnUrlParameters(LookupView lookupView, LookupForm lookupForm, Object dataObject) {
        Properties props = new Properties();
        props.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.RETURN_METHOD_TO_CALL);

        if (StringUtils.isNotBlank(lookupForm.getReturnFormKey())) {
            props.put(UifParameters.FORM_KEY, lookupForm.getReturnFormKey());
        }

        props.put(KRADConstants.REFRESH_CALLER, lookupView.getId());
        props.put(KRADConstants.REFRESH_DATA_OBJECT_CLASS, getDataObjectClass().getName());

        if (StringUtils.isNotBlank(lookupForm.getReferencesToRefresh())) {
            props.put(UifParameters.REFERENCES_TO_REFRESH, lookupForm.getReferencesToRefresh());
        }

        if (StringUtils.isNotBlank(lookupForm.getQuickfinderId())) {
            props.put(UifParameters.QUICKFINDER_ID, lookupForm.getQuickfinderId());
        }

        Map<String, String> returnKeyValues = getTranslatedReturnKeyValues(lookupView, lookupForm, dataObject);
        props.putAll(returnKeyValues);

        return props;
    }

    /**
     * Returns a map of the configured return keys translated to their corresponding field conversion with
     * the associated values.
     *
     * @param lookupView lookup view instance containing lookup configuration
     * @param lookupForm lookup form instance containing the data
     * @param dataObject data object instance
     * @return Map<String, String> map of translated return key/value pairs
     */
    protected Map<String, String> getTranslatedReturnKeyValues(LookupView lookupView, LookupForm lookupForm,
            Object dataObject) {
        Map<String, String> translatedKeyValues = new HashMap<String, String>();

        Map<String, String> returnKeyValues = getReturnKeyValues(lookupView, lookupForm, dataObject);

        for (String returnKey : returnKeyValues.keySet()) {
            String returnValue = returnKeyValues.get(returnKey);

            // get name of the property on the calling view to pass back the parameter value as
            if (lookupForm.getFieldConversions().containsKey(returnKey)) {
                returnKey = lookupForm.getFieldConversions().get(returnKey);
            }

            translatedKeyValues.put(returnKey, returnValue);
        }

        return translatedKeyValues;
    }

    /**
     * Returns a map of the configured return keys with their selected values.
     *
     * @param lookupView lookup view instance containing lookup configuration
     * @param lookupForm lookup form instance containing the data
     * @param dataObject data object instance
     * @return Map<String, String> map of return key/value pairs
     */
    protected Map<String, String> getReturnKeyValues(LookupView lookupView, LookupForm lookupForm, Object dataObject) {
        List<String> returnKeys;

        if (lookupForm.getFieldConversions() != null && !lookupForm.getFieldConversions().isEmpty()) {
            returnKeys = new ArrayList<String>(lookupForm.getFieldConversions().keySet());
        } else {
            returnKeys = getLegacyDataAdapter().listPrimaryKeyFieldNames(getDataObjectClass());
        }

        List<String> secureReturnKeys = lookupView.getAdditionalSecurePropertyNames();

        return KRADUtils.getPropertyKeyValuesFromDataObject(returnKeys, secureReturnKeys, dataObject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildMaintenanceActionLink(Link actionLink, Object model, String maintenanceMethodToCall) {
        LookupForm lookupForm = (LookupForm) model;

        Map<String, Object> actionLinkContext = actionLink.getContext();
        Object dataObject = actionLinkContext == null ? null : actionLinkContext
                .get(UifConstants.ContextVariableNames.LINE);

        List<String> pkNames = getLegacyDataAdapter().listPrimaryKeyFieldNames(getDataObjectClass());

        // build maintenance link href if needed
        if (StringUtils.isBlank(actionLink.getHref())) {
            String href = getMaintenanceActionUrl(lookupForm, dataObject, maintenanceMethodToCall, pkNames);
            if (StringUtils.isBlank(href)) {
                actionLink.setRender(false);

                return;
            }

            actionLink.setHref(href);
        }

        // build action title if not set
        if (StringUtils.isBlank(actionLink.getTitle())) {
            String objectLabel = "";
            DataObjectEntry dataObjectEntry = getDataDictionaryService().getDataDictionary()
                    .getDataObjectEntry(getDataObjectClass().getName());

            // check to see if there was a data object entry found for the class
            // if there is a data entry object, then set the object label, else let it be empty string
            if( dataObjectEntry != null && dataObjectEntry.getObjectLabel() != null ) {
                objectLabel = dataObjectEntry.getObjectLabel();
            }

            ConfigurationService service = getConfigurationService();
            String val = service.getPropertyValueAsString( KRADConstants.Lookup.TITLE_ACTION_URL_PREPENDTEXT_PROPERTY );
            String prependTitleText = actionLink.getLinkText() + " " + objectLabel + " " +
                    getConfigurationService().getPropertyValueAsString(
                            KRADConstants.Lookup.TITLE_ACTION_URL_PREPENDTEXT_PROPERTY);

            Map<String, String> primaryKeyValues = KRADUtils.getPropertyKeyValuesFromDataObject(pkNames, dataObject);
            String title = KRADUtils.buildAttributeTitleString(prependTitleText, getDataObjectClass(),
                    primaryKeyValues);

            actionLink.setTitle(title);
        }
    }

    /**
     * Generates a URL to perform a maintenance action on the given result data object.
     *
     * <p>Will build a URL containing keys of the data object to invoke the given maintenance action method
     * within the maintenance controller</p>
     * 
     * @param lookupForm lookup form
     * @param dataObject data object instance for the line to build the maintenance action link for
     * @param methodToCall method name on the maintenance controller that should be invoked
     * @param pkNames list of primary key field names for the data object whose key/value pairs will be added to
     * the maintenance link
     *
     * @return String URL link for the maintenance action
     */
    protected String getMaintenanceActionUrl(LookupForm lookupForm, Object dataObject, String methodToCall,
            List<String> pkNames) {
        LookupView lookupView = (LookupView) lookupForm.getView();

        Properties props = new Properties();
        props.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, methodToCall);

        Map<String, String> primaryKeyValues = KRADUtils.getPropertyKeyValuesFromDataObject(pkNames, dataObject);
        for (String primaryKey : primaryKeyValues.keySet()) {
            String primaryKeyValue = primaryKeyValues.get(primaryKey);

            props.put(primaryKey, primaryKeyValue);
        }

        if (StringUtils.isNotBlank(lookupForm.getReturnLocation())) {
            props.put(KRADConstants.RETURN_LOCATION_PARAMETER, lookupForm.getReturnLocation());
        }

        props.put(UifParameters.DATA_OBJECT_CLASS_NAME, lookupForm.getDataObjectClassName());
        props.put(UifParameters.VIEW_TYPE_NAME, UifConstants.ViewType.MAINTENANCE.name());

        String maintenanceMapping = KRADConstants.Maintenance.REQUEST_MAPPING_MAINTENANCE;
        if (lookupView != null && StringUtils.isNotBlank(lookupView.getMaintenanceUrlMapping())) {
            maintenanceMapping = lookupView.getMaintenanceUrlMapping();
        }

        return UrlFactory.parameterizeUrl(maintenanceMapping, props);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildMultiValueSelectField(InputField selectField, Object model) {
        LookupForm lookupForm = (LookupForm) model;

        Map<String, Object> selectFieldContext = selectField.getContext();
        Object lineDataObject = selectFieldContext == null ? null : selectFieldContext
                .get(UifConstants.ContextVariableNames.LINE);
        if (lineDataObject == null) {
            throw new RuntimeException("Unable to get data object for line from component: " + selectField.getId());
        }

        Control selectControl = selectField.getControl();
        if ((selectControl != null) && (selectControl instanceof ValueConfiguredControl)) {
            // get value for each field conversion from line and add to lineIdentifier
            List<String> fromFieldNames = new ArrayList<String>(lookupForm.getFieldConversions().keySet());
            // Per KULRICE-12125 we need to remove secure field names from this list.
            fromFieldNames = new ArrayList<String>(
                    KRADUtils.getPropertyKeyValuesFromDataObject(fromFieldNames, lineDataObject).keySet());

            Collections.sort(fromFieldNames);
            lookupForm.setMultiValueReturnFields(fromFieldNames);
            String lineIdentifier = "";
            for (String fromFieldName : fromFieldNames) {
                Object fromFieldValue = ObjectPropertyUtils.getPropertyValue(lineDataObject, fromFieldName);

                if (fromFieldValue != null) {
                    lineIdentifier += fromFieldValue;
                }

                lineIdentifier += ":";
            }
            lineIdentifier = StringUtils.removeEnd(lineIdentifier, ":");

            ((ValueConfiguredControl) selectControl).setValue(lineIdentifier);
        }
    }

    /**
     * Determines if given data object has associated maintenance document that allows new or copy
     * maintenance actions.
     *
     * @return boolean true if the maintenance new or copy action is allowed for the data object instance, false
     *         otherwise
     */
    public boolean allowsMaintenanceNewOrCopyAction() {
        boolean allowsNewOrCopy = false;

        String maintDocTypeName = getMaintenanceDocumentTypeName();
        if (StringUtils.isNotBlank(maintDocTypeName)) {
            allowsNewOrCopy = getDataObjectAuthorizationService().canCreate(getDataObjectClass(),
                    GlobalVariables.getUserSession().getPerson(), maintDocTypeName);
        }

        return allowsNewOrCopy;
    }

    /**
     * Determines if given data object has associated maintenance document that allows edit maintenance
     * actions.
     * @param dataObject data object
     *
     * @return boolean true if the maintenance edit action is allowed for the data object instance, false otherwise
     */
    public boolean allowsMaintenanceEditAction(Object dataObject) {
        boolean allowsEdit = false;

        String maintDocTypeName = getMaintenanceDocumentTypeName();
        if (StringUtils.isNotBlank(maintDocTypeName)) {
            allowsEdit = getDataObjectAuthorizationService().canMaintain(dataObject,
                    GlobalVariables.getUserSession().getPerson(), maintDocTypeName);
        }

        return allowsEdit;
    }

    /**
     * Determines if given data object has associated maintenance document that allows delete maintenance
     * actions.
     * @param dataObject data object
     *
     * @return boolean true if the maintenance delete action is allowed for the data object instance, false otherwise
     */
    public boolean allowsMaintenanceDeleteAction(Object dataObject) {
        boolean allowsMaintain = false;

        String maintDocTypeName = getMaintenanceDocumentTypeName();
        if (StringUtils.isNotBlank(maintDocTypeName)) {
            allowsMaintain = getDataObjectAuthorizationService().canMaintain(dataObject,
                    GlobalVariables.getUserSession().getPerson(), maintDocTypeName);
        }

        boolean allowsDelete = getDocumentDictionaryService().getAllowsRecordDeletion(getDataObjectClass());

        return allowsDelete && allowsMaintain;
    }

    /**
     * Returns the maintenance document type associated with the business object class or null if one does not exist.
     *
     * @return String representing the maintenance document type name
     */
    protected String getMaintenanceDocumentTypeName() {
        DocumentDictionaryService dd = getDocumentDictionaryService();

        return dd.getMaintenanceDocumentTypeName(getDataObjectClass());
    }

    /**
     * Returns the criteria fields in a map keyed by the field property name.
     *
     * @param lookupView lookup view instance to pull criteria fields from
     * @param form lookup form instance containing the lookup data
     * @return map of criteria fields
     */
    protected Map<String, InputField> getCriteriaFieldsForValidation(LookupView lookupView, LookupForm form) {
        Map<String, InputField> criteriaFieldMap = new HashMap<String, InputField>();

        if (lookupView.getCriteriaFields() == null) {
            return criteriaFieldMap;
        }

        List<InputField> fields = null;
        if (lookupView.getCriteriaGroup().getItems().size() > 0) {
            fields = ComponentUtils.getNestedContainerComponents(lookupView.getCriteriaGroup(), InputField.class);
        } else if (lookupView.getCriteriaFields().size() > 0) {
            // If criteriaGroup items are empty look to see if criteriaFields has any input components.
            // This is to ensure that if initializeGroup hasn't been called on the view, the validations will still happen on criteriaFields
            fields = ComponentUtils.getComponentsOfType(lookupView.getCriteriaFields(), InputField.class);
        } else {
            fields = new ArrayList<InputField>();
        }
        for (InputField field : fields) {
            criteriaFieldMap.put(field.getPropertyName(), field);
        }

        return criteriaFieldMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getDataObjectClass() {
        return this.dataObjectClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDataObjectClass(Class<?> dataObjectClass) {
        this.dataObjectClass = dataObjectClass;
    }

    protected DataObjectAuthorizationService getDataObjectAuthorizationService() {
        if (dataObjectAuthorizationService == null) {
            this.dataObjectAuthorizationService = KRADServiceLocatorWeb.getDataObjectAuthorizationService();
        }
        return dataObjectAuthorizationService;
    }

    public void setDataObjectAuthorizationService(DataObjectAuthorizationService dataObjectAuthorizationService) {
        this.dataObjectAuthorizationService = dataObjectAuthorizationService;
    }

    public DocumentDictionaryService getDocumentDictionaryService() {
        if (documentDictionaryService == null) {
            documentDictionaryService = KRADServiceLocatorWeb.getDocumentDictionaryService();
        }
        return documentDictionaryService;
    }

    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }

    protected LookupService getLookupService() {
        if (lookupService == null) {
            this.lookupService = KRADServiceLocatorWeb.getLookupService();
        }
        return lookupService;
    }

    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    protected EncryptionService getEncryptionService() {
        if (encryptionService == null) {
            this.encryptionService = CoreApiServiceLocator.getEncryptionService();
        }
        return encryptionService;
    }

    public void setEncryptionService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    /**
     * Creates a copy of this {@code LookupableImpl}.
     *
     * @return a copy of this {@code LookupableImpl}
     */
    public LookupableImpl copy() {
        LookupableImpl lookupableImplCopy = KRADUtils.createNewObjectFromClass(getClass());

        if (this.getDataObjectClass() != null) {
            lookupableImplCopy.setDataObjectClass(this.getDataObjectClass());
        }

        if (this.getConfigurationService() != null) {
            lookupableImplCopy.setConfigurationService(this.getConfigurationService());
        }

        if (this.getDataDictionaryService() != null) {
            lookupableImplCopy.setDataDictionaryService(this.getDataDictionaryService());
        }

        if (this.getDataObjectAuthorizationService() != null) {
            lookupableImplCopy.setDataObjectAuthorizationService(this.getDataObjectAuthorizationService());
        }

        if (this.getDataObjectService() != null) {
            lookupableImplCopy.setDataObjectService(this.getDataObjectService());
        }

        if (this.getDocumentDictionaryService() != null) {
            lookupableImplCopy.setDocumentDictionaryService(this.getDocumentDictionaryService());
        }

        if (this.getEncryptionService() != null) {
            lookupableImplCopy.setEncryptionService(this.getEncryptionService());
        }

        if (this.getExpressionEvaluatorFactory() != null) {
            lookupableImplCopy.setExpressionEvaluatorFactory(this.getExpressionEvaluatorFactory());
        }

        if (this.getLegacyDataAdapter() != null) {
            lookupableImplCopy.setLegacyDataAdapter(this.getLegacyDataAdapter());
        }

        if (this.getLookupService() != null) {
            lookupableImplCopy.setLookupService(this.getLookupService());
        }

        if (this.getViewDictionaryService() != null) {
            lookupableImplCopy.setViewDictionaryService(this.getViewDictionaryService());
        }

        return lookupableImplCopy;
    }
}
