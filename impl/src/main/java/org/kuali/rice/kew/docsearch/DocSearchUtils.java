/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.kew.docsearch;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.reflect.ObjectDefinition;
import org.kuali.rice.core.api.uif.DataType;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.framework.resourceloader.ObjectDefinitionResolver;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.api.document.lookup.DocumentLookupConfiguration;
import org.kuali.rice.kew.docsearch.web.SearchAttributeFormContainer;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.UserUtils;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * Various static utility methods for helping with Searcha.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public final class DocSearchUtils {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocSearchUtils.class);

    public static final List<Class<? extends SearchableAttributeValue>> SEARCHABLE_ATTRIBUTE_BASE_CLASS_LIST =
            new ArrayList<Class<? extends SearchableAttributeValue>>();
    static {
        SEARCHABLE_ATTRIBUTE_BASE_CLASS_LIST.add(SearchableAttributeStringValue.class);
        SEARCHABLE_ATTRIBUTE_BASE_CLASS_LIST.add(SearchableAttributeFloatValue.class);
        SEARCHABLE_ATTRIBUTE_BASE_CLASS_LIST.add(SearchableAttributeLongValue.class);
        SEARCHABLE_ATTRIBUTE_BASE_CLASS_LIST.add(SearchableAttributeDateTimeValue.class);
    }

    private DocSearchUtils() {
    	throw new UnsupportedOperationException("do not call");
    }
    
    public static List<SearchableAttributeValue> getSearchableAttributeValueObjectTypes() {
        List<SearchableAttributeValue> searchableAttributeValueClasses = new ArrayList<SearchableAttributeValue>();
        for (Class<? extends SearchableAttributeValue> searchAttributeValueClass : SEARCHABLE_ATTRIBUTE_BASE_CLASS_LIST) {
            ObjectDefinition objDef = new ObjectDefinition(searchAttributeValueClass);
            SearchableAttributeValue attributeValue = (SearchableAttributeValue) ObjectDefinitionResolver.createObject(objDef, ClassLoaderUtils.getDefaultClassLoader(), false);
            searchableAttributeValueClasses.add(attributeValue);
        }
        return searchableAttributeValueClasses;
    }

    /**
     * TODO - Rice 2.0 - Move once migrated over to new doc search framework
     */
    public static SearchableAttributeValue getSearchableAttributeValueByDataTypeString(String dataType) {
        SearchableAttributeValue returnableValue = null;
        if (StringUtils.isBlank(dataType)) {
            return returnableValue;
        }
        for (SearchableAttributeValue attValue : getSearchableAttributeValueObjectTypes())
        {
            if (dataType.equalsIgnoreCase(attValue.getAttributeDataType()))
            {
                if (returnableValue != null)
                {
                    String errorMsg = "Found two SearchableAttributeValue objects with same data type string ('" + dataType + "' while ignoring case):  " + returnableValue.getClass().getName() + " and " + attValue.getClass().getName();
                    LOG.error("getSearchableAttributeValueByDataTypeString() " + errorMsg);
                    throw new RuntimeException(errorMsg);
                }
                LOG.debug("getSearchableAttributeValueByDataTypeString() SearchableAttributeValue class name is " + attValue.getClass().getName() + "... ojbConcreteClassName is " + attValue.getOjbConcreteClass());
                ObjectDefinition objDef = new ObjectDefinition(attValue.getClass());
                returnableValue = (SearchableAttributeValue) ObjectDefinitionResolver.createObject(objDef, ClassLoaderUtils.getDefaultClassLoader(), false);
            }
        }
        return returnableValue;
    }

    public static SearchableAttributeValue getSearchableAttributeValueByDataTypeString(DataType dataType) {
        if (dataType == null || dataType == DataType.STRING || dataType == DataType.BOOLEAN) {
            return new SearchableAttributeStringValue();
        } else if (dataType == DataType.DATE || dataType == DataType.TRUNCATED_DATE) {
            return new SearchableAttributeDateTimeValue();
        } else if (dataType == DataType.FLOAT || dataType == DataType.DOUBLE) {
            return new SearchableAttributeFloatValue();
        } else if (dataType == DataType.INTEGER || dataType == DataType.LONG) {
            return new SearchableAttributeLongValue();
        }
        throw new IllegalArgumentException("Could not determine appropriate searchable attribute data type to use for the given DataType: " + dataType);
    }

    public static  List<SearchAttributeCriteriaComponent> translateSearchFieldToCriteriaComponent(RemotableAttributeField searchField) {
        List<SearchAttributeCriteriaComponent> components = new ArrayList<SearchAttributeCriteriaComponent>();
        SearchableAttributeValue searchableAttributeValue = DocSearchUtils.getSearchableAttributeValueByDataTypeString(searchField.getDataType());
        List<Field> fields = FieldUtils.convertRemotableAttributeField(searchField);
        for (Field field : fields) {
            SearchAttributeCriteriaComponent sacc = new SearchAttributeCriteriaComponent(field.getPropertyName(), null, field.getPropertyName(), searchableAttributeValue);

            sacc.setRangeSearch(field.isMemberOfRange());
            sacc.setSearchInclusive(field.isInclusive());
            sacc.setSearchable(field.isIndexedForSearch());
            sacc.setLookupableFieldType(field.getFieldType());
            sacc.setCanHoldMultipleValues(Field.MULTI_VALUE_FIELD_TYPES.contains(field.getFieldType()));
            components.add(sacc);
        }
        return components;
    }

    public static String getDisplayValueWithDateOnly(DateTime value) {
        return getDisplayValueWithDateOnly(new Timestamp(value.getMillis()));
    }

    public static String getDisplayValueWithDateOnly(Timestamp value) {
        return RiceConstants.getDefaultDateFormat().format(new Date(value.getTime()));
    }

    public static String getDisplayValueWithDateTime(Timestamp value) {
        return RiceConstants.getDefaultDateAndTimeFormat().format(new Date(value.getTime()));
    }



    private static final String CURRENT_USER_PREFIX = "CURRENT_USER.";

    /**
     * Build List of searchable attributes from saved searchable attributes string
     *
     * @param searchableAttributeString
     *            String representation of searchable attributes
     * @param documentTypeName document type name
     * @return searchable attributes list
     */
    public static List<SearchAttributeCriteriaComponent> buildSearchableAttributesFromString(String searchableAttributeString, String documentTypeName) {
        List<SearchAttributeCriteriaComponent> searchableAttributes = new ArrayList<SearchAttributeCriteriaComponent>();
        Map<String, SearchAttributeCriteriaComponent> criteriaComponentsByKey = new HashMap<String, SearchAttributeCriteriaComponent>();

        DocumentType docType = getDocumentType(documentTypeName);

        if (docType != null) {

            DocumentLookupConfiguration lookupConfiguration = KEWServiceLocator.getDocumentLookupCustomizationMediator().getDocumentLookupConfiguration(docType);
            List<RemotableAttributeField> searchFields = lookupConfiguration.getFlattenedSearchAttributeFields();

            for (RemotableAttributeField searchField : searchFields) {
                List<SearchAttributeCriteriaComponent> components = DocSearchUtils.translateSearchFieldToCriteriaComponent(searchField);
                for (SearchAttributeCriteriaComponent searchableAttributeComponent : components) {
                    criteriaComponentsByKey.put(searchField.getName(), searchableAttributeComponent);
                }
            }

        }

        Map<String, List<String>> checkForMultiValueSearchableAttributes = new HashMap<String, List<String>>();
        if ((searchableAttributeString != null) && (searchableAttributeString.trim().length() > 0)) {
            StringTokenizer tokenizer = new StringTokenizer(searchableAttributeString, ",");
            while (tokenizer.hasMoreTokens()) {
                String searchableAttribute = tokenizer.nextToken();
                int index = searchableAttribute.indexOf(":");
                if (index != -1) {
                    String key = searchableAttribute.substring(0, index);
                    String value = searchableAttribute.substring(index + 1);
                    if (value.startsWith(CURRENT_USER_PREFIX)) {
                        String idType = value.substring(CURRENT_USER_PREFIX.length());
                        UserSession session = GlobalVariables.getUserSession();
                        String idValue = UserUtils.getIdValue(idType, session.getPerson());
                        if (!StringUtils.isBlank(idValue)) {
                            value = idValue;
                        }
                    }
                    SearchAttributeCriteriaComponent critComponent = criteriaComponentsByKey.get(key);
                    if (critComponent == null) {
                        // here we potentially have a change to the searchable attributes dealing with naming or ranges... so
                        // we just ignore the values
                        continue;
                    }
                    if (critComponent.getSearchableAttributeValue() == null) {
                        String errorMsg = "Cannot find SearchableAttributeValue for given key '" + key + "'";
                        LOG.error("buildSearchableAttributesFromString() " + errorMsg);
                        throw new RuntimeException(errorMsg);
                    }
                    if (critComponent.isCanHoldMultipleValues()) {
                        // should be multivalue
                        if (checkForMultiValueSearchableAttributes.containsKey(key)) {
                            List<String> keyList = checkForMultiValueSearchableAttributes.get(key);
                            keyList.add(value);
                            checkForMultiValueSearchableAttributes.put(key, keyList);
                        } else {
                            List<String> tempList = new ArrayList<String>();
                            tempList.add(value);
                            // tempList.addAll(Arrays.asList(new String[]{value}));
                            checkForMultiValueSearchableAttributes.put(key, tempList);
                            searchableAttributes.add(critComponent);
                        }
                    } else {
                        // should be single value
                        if (checkForMultiValueSearchableAttributes.containsKey(key)) {
                            // attempting to use multiple values in a field that does not support it
                            String error = "Attempting to add multiple values to a search attribute (key: '" + key + "') that does not suppor them";
                            LOG.error("buildSearchableAttributesFromString() " + error);
                            // we don't blow chunks here in case an attribute has been altered from multi-value to
                            // non-multi-value
                        }
                        critComponent.setValue(value);
                        searchableAttributes.add(critComponent);
                    }

                }
            }
            for (SearchAttributeCriteriaComponent criteriaComponent : searchableAttributes)
            {
                if (criteriaComponent.isCanHoldMultipleValues())
                {
                    List<String> values = checkForMultiValueSearchableAttributes.get(criteriaComponent.getFormKey());
                    criteriaComponent.setValue(null);
                    criteriaComponent.setValues(values);
                }
            }
        }

        return searchableAttributes;
    }

    /**
     * This method takes the given <code>propertyFields</code> parameter and populates the {@link DocSearchCriteriaDTO}
     * object search attributes based on the document type name set on the <code>criteria</code> object.<br>
     * <br>
     * This is identical to calling {@link #addSearchableAttributesToCriteria(DocSearchCriteriaDTO, List, String, boolean)}
     * with a boolean value of false for the <code>setAttributesStrictly</code> parameter.
     *
     * @param criteria -
     *            The object that needs a list of {@link SearchAttributeCriteriaComponent} objects set up based on the
     *            document type name and <code>propertyFields</code> parameter
     * @param propertyFields -
     *            The list of {@link SearchAttributeFormContainer} objects that need to be converted to
     *            {@link SearchAttributeCriteriaComponent} objects and set on the <code>criteria</code> parameter
     * @param searchAttributesString -
     *            A potential string that must be parsed to use to set attributes on the <code>criteria</code> object
     */
    public static void addSearchableAttributesToCriteria(DocSearchCriteriaDTO criteria, List propertyFields, String searchAttributesString) {
        addSearchableAttributesToCriteria(criteria, propertyFields, searchAttributesString, false);
    }

    /**
     * This method takes the given <code>propertyFields</code> parameter and populates the {@link DocSearchCriteriaDTO}
     * object search attributes based on the document type name set on the <code>criteria</code> object.<br>
     * <br>
     * This is identical to calling {@link #addSearchableAttributesToCriteria(DocSearchCriteriaDTO, List, String, boolean)}
     * with a null value for the <code>searchAttributesString</code> parameter.
     *
     * @param criteria -
     *            The object that needs a list of {@link SearchAttributeCriteriaComponent} objects set up based on the
     *            document type name and <code>propertyFields</code> parameter
     * @param propertyFields -
     *            The list of {@link SearchAttributeFormContainer} objects that need to be converted to
     *            {@link SearchAttributeCriteriaComponent} objects and set on the <code>criteria</code> parameter
     * @param setAttributesStrictly -
     *            A boolean to specify whether to explicitly throw an error when a given value from
     *            <code>propertyFields</code> does not match a search attribute on the specified document type. If set to
     *            true an error with be thrown. If set to false the mismatch will be ignored.
     */
    public static void addSearchableAttributesToCriteria(DocSearchCriteriaDTO criteria, List propertyFields, boolean setAttributesStrictly) {
        addSearchableAttributesToCriteria(criteria, propertyFields, null, setAttributesStrictly);
    }

    /**
     * This method takes the given <code>propertyFields</code> parameter and populates the {@link DocSearchCriteriaDTO}
     * object search attributes based on the document type name set on the <code>criteria</code> object.
     *
     * @param criteria -
     *            The object that needs a list of {@link SearchAttributeCriteriaComponent} objects set up based on the
     *            document type name and <code>propertyFields</code> parameter
     * @param propertyFields -
     *            The list of {@link SearchAttributeFormContainer} objects that need to be converted to
     *            {@link SearchAttributeCriteriaComponent} objects and set on the <code>criteria</code> parameter
     * @param searchAttributesString -
     *            A potential string that must be parsed to use to set attributes on the <code>criteria</code> object
     * @param setAttributesStrictly -
     *            A boolean to specify whether to explicitly throw an error when a given value from
     *            <code>propertyFields</code> does not match a search attribute on the specified document type. If set to
     *            true an error with be thrown. If set to false the mismatch will be ignored.
     */
    public static void addSearchableAttributesToCriteria(DocSearchCriteriaDTO criteria, List propertyFields, String searchAttributesString, boolean setAttributesStrictly) {
        if (criteria != null) {
            DocumentType docType = getDocumentType(criteria.getDocTypeFullName());
            if (docType == null) {
                return;
            }
            criteria.getSearchableAttributes().clear();
            Map<String, SearchAttributeCriteriaComponent> urlParameterSearchAttributesByFormKey = new HashMap<String, SearchAttributeCriteriaComponent>();

            if (!StringUtils.isBlank(searchAttributesString)) {
                List<SearchAttributeCriteriaComponent> components = buildSearchableAttributesFromString(searchAttributesString, docType.getName());
                for (SearchAttributeCriteriaComponent component : components) {
                    urlParameterSearchAttributesByFormKey.put(component.getFormKey(), component);
                    criteria.addSearchableAttribute(component);
                }
            }

            if (!propertyFields.isEmpty()) {
                Map<String, SearchAttributeCriteriaComponent> criteriaComponentsByFormKey = new HashMap<String, SearchAttributeCriteriaComponent>();

                DocumentLookupConfiguration lookupConfiguration = KEWServiceLocator.getDocumentLookupCustomizationMediator().getDocumentLookupConfiguration(docType);
                List<RemotableAttributeField> searchFields = lookupConfiguration.getFlattenedSearchAttributeFields();

                for (RemotableAttributeField searchField : searchFields) {
                    List<SearchAttributeCriteriaComponent> components = DocSearchUtils.translateSearchFieldToCriteriaComponent(searchField);
                    for (SearchAttributeCriteriaComponent searchableAttributeComponent : components) {
                        criteriaComponentsByFormKey.put(searchField.getName(), searchableAttributeComponent);
                    }
                }

                for (Iterator iterator = propertyFields.iterator(); iterator.hasNext();) {
                    SearchAttributeFormContainer propertyField = (SearchAttributeFormContainer) iterator.next();
                    SearchAttributeCriteriaComponent sacc = criteriaComponentsByFormKey.get(propertyField.getKey());
                    if (sacc != null) {
                        if (sacc.getSearchableAttributeValue() == null) {
                            String errorMsg = "Searchable attribute with form field key " + sacc.getFormKey() + " does not have a valid SearchableAttributeValue";
                            LOG.error("addSearchableAttributesToCriteria() " + errorMsg);
                            throw new RuntimeException(errorMsg);
                        }
                        // if the url parameter has already set up the search attribute change the propertyField
                        if (urlParameterSearchAttributesByFormKey.containsKey(sacc.getFormKey())) {
                            setupPropertyField(urlParameterSearchAttributesByFormKey.get(sacc.getFormKey()), propertyFields);
                        } else {
                            if (Field.MULTI_VALUE_FIELD_TYPES.contains(sacc.getLookupableFieldType())) {
                                // set the multivalue lookup indicator
                                sacc.setCanHoldMultipleValues(true);
                                if (propertyField.getValues() == null) {
                                    sacc.setValues(new ArrayList<String>());
                                } else {
                                    sacc.setValues(Arrays.asList(propertyField.getValues()));
                                }
                            } else {
                                sacc.setValue(propertyField.getValue());
                            }
                            criteria.addSearchableAttribute(sacc);
                        }
                    } else {
                        if (setAttributesStrictly) {
                            String message = "Cannot find matching search attribute with key '" + propertyField.getKey() + "' on document type '" + docType.getName() + "'";
                            LOG.error(message);
                            throw new WorkflowRuntimeException(message);
                        }
                    }
                }
            }
        }
    }

    public static void setupPropertyField(SearchAttributeCriteriaComponent searchableAttribute, List propertyFields) {
        SearchAttributeFormContainer propertyField = getPropertyField(searchableAttribute.getFormKey(), propertyFields);
        if (propertyField != null) {
            propertyField.setValue(searchableAttribute.getValue());
            if (searchableAttribute.getValues() != null) {
                propertyField.setValues(searchableAttribute.getValues().toArray(new String[searchableAttribute.getValues().size()]));
            }
        }
    }

    public static SearchAttributeFormContainer getPropertyField(String key, List propertyFields) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        for (Iterator iter = propertyFields.iterator(); iter.hasNext();) {
            SearchAttributeFormContainer container = (SearchAttributeFormContainer) iter.next();
            if (key.equals(container.getKey())) {
                return container;
            }
        }
        return null;
    }

    private static DocumentType getDocumentType(String docTypeName) {
        if ((docTypeName != null && !"".equals(docTypeName))) {
            return ((DocumentTypeService) KEWServiceLocator.getService(KEWServiceLocator.DOCUMENT_TYPE_SERVICE)).findByName(docTypeName);
        }
        return null;
    }

}
