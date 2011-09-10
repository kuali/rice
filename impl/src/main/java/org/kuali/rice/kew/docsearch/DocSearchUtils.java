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
import org.joda.time.MutableDateTime;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.reflect.ObjectDefinition;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.uif.DataType;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.framework.resourceloader.ObjectDefinitionResolver;
import org.kuali.rice.kew.api.WorkflowRuntimeException;
import org.kuali.rice.kew.framework.document.lookup.DocumentLookupCriteriaConfiguration;
import org.kuali.rice.kew.docsearch.web.SearchAttributeFormContainer;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.user.UserUtils;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        if (dataType == null || dataType == DataType.STRING || dataType == DataType.BOOLEAN || dataType == DataType.MARKUP) {
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

            DocumentLookupCriteriaConfiguration lookupConfiguration =
                    KEWServiceLocator.getDocumentLookupCustomizationMediator().
                            getDocumentLookupCriteriaConfiguration(docType);
            if (lookupConfiguration != null) {
                List<RemotableAttributeField> searchFields = lookupConfiguration.getFlattenedSearchAttributeFields();

                for (RemotableAttributeField searchField : searchFields) {
                    List<SearchAttributeCriteriaComponent> components = DocSearchUtils.translateSearchFieldToCriteriaComponent(searchField);
                    for (SearchAttributeCriteriaComponent searchableAttributeComponent : components) {
                        criteriaComponentsByKey.put(searchField.getName(), searchableAttributeComponent);
                    }
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

    public static DateTime getLowerDateTimeBound(String dateRange) throws ParseException {
        Range range = parseRange(dateRange);
        if (range == null) {
            throw new IllegalArgumentException("Failed to parse date range from given string: " + dateRange);
        }
        if (range.getLowerBoundValue() != null) {
            java.util.Date lowerRangeDate = CoreApiServiceLocator.getDateTimeService().convertToDate(range.getLowerBoundValue());
            MutableDateTime dateTime = new MutableDateTime(lowerRangeDate);
            dateTime.setMillisOfDay(0);
            return dateTime.toDateTime();
        }
        return null;
    }

    public static DateTime getUpperDateTimeBound(String dateRange) throws ParseException {
        Range range = parseRange(dateRange);
        if (range == null) {
            throw new IllegalArgumentException("Failed to parse date range from given string: " + dateRange);
        }
        if (range.getUpperBoundValue() != null) {
            java.util.Date upperRangeDate = CoreApiServiceLocator.getDateTimeService().convertToDate(range.getUpperBoundValue());
            MutableDateTime dateTime = new MutableDateTime(upperRangeDate);
            // set it to the last millisecond of the day
            dateTime.setMillisOfDay((24 * 60 * 60 * 1000) - 1);
            return dateTime.toDateTime();
        }
        return null;
    }

    public static Range parseRange(String rangeString) {
        if (StringUtils.isBlank(rangeString)) {
            throw new IllegalArgumentException("rangeString was null or blank");
        }
        Range range = new Range();
        rangeString = rangeString.trim();
        if (rangeString.startsWith(SearchOperator.LESS_THAN_EQUAL.op())) {
            rangeString = StringUtils.remove(rangeString, SearchOperator.LESS_THAN_EQUAL.op()).trim();
            range.setUpperBoundValue(rangeString);
            range.setUpperBoundInclusive(true);
        } else if (rangeString.startsWith(SearchOperator.LESS_THAN.op())) {
            rangeString = StringUtils.remove(rangeString, SearchOperator.LESS_THAN.op()).trim();
            range.setUpperBoundValue(rangeString);
            range.setUpperBoundInclusive(false);
        } else if (rangeString.startsWith(SearchOperator.GREATER_THAN_EQUAL.op())) {
            rangeString = StringUtils.remove(rangeString, SearchOperator.GREATER_THAN_EQUAL.op()).trim();
            range.setLowerBoundValue(rangeString);
            range.setLowerBoundInclusive(true);
        } else if (rangeString.startsWith(SearchOperator.GREATER_THAN.op())) {
            rangeString = StringUtils.remove(rangeString, SearchOperator.GREATER_THAN.op()).trim();
            range.setLowerBoundValue(rangeString);
            range.setLowerBoundInclusive(false);
        } else if (rangeString.contains(SearchOperator.BETWEEN_EXCLUSIVE_UPPER.op())) {
            String[] rangeBounds = StringUtils.split(rangeString, SearchOperator.BETWEEN_EXCLUSIVE_UPPER.op());
            range.setLowerBoundValue(rangeBounds[0]);
            range.setLowerBoundInclusive(true);
            range.setUpperBoundValue(rangeBounds[1]);
            range.setUpperBoundInclusive(false);
        } else if (rangeString.contains(SearchOperator.BETWEEN.op())) {
            String[] rangeBounds = StringUtils.split(rangeString, SearchOperator.BETWEEN.op());
            range.setLowerBoundValue(rangeBounds[0]);
            range.setLowerBoundInclusive(true);
            range.setUpperBoundValue(rangeBounds[1]);
            range.setUpperBoundInclusive(true);
        } else {
            // if it has no range specification, return null
            return null;
        }
        return range;
    }

/**
     * Cleans upper bounds on an entire list of values.
     * @param stringDates list
     * @return list of dates
     */
    private static List<String> cleanUpperBounds(List<String> stringDates) {
        List<String> lRet = null;
        if(stringDates != null && !stringDates.isEmpty()){
            lRet = new ArrayList<String>();
            for(String stringDate:stringDates){
                lRet.add(cleanUpperBound(stringDate));
            }
        }
        return lRet;
    }

    /**
     * When dealing with upperbound dates, it is a business requirement that if a timestamp isn't already
     * stated append 23:59:59 to the end of the date.  This ensures that you are searching for the entire
     * day.
     * @param stringDate
     * @return upper bound date
     */
    private static String cleanUpperBound(String stringDate){
        try{
            java.sql.Timestamp dt = CoreApiServiceLocator.getDateTimeService().convertToSqlTimestamp(stringDate);
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

            if("00:00:00".equals(sdfTime.format(dt))){
                stringDate = stringDate + " 23:59:59";
            }
        } catch (Exception ex){
            GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS, RiceKeyConstants.ERROR_CUSTOM, new String[] { "Invalid Date Input: " + stringDate });
        }
        return stringDate;
    }


    /**
     *
     * This method takes in any valid date string, like <12/30/09 and convert
     * it into <12/30/09 23:59:59, but only for upper bound type values.
     *
     *  This method only really cares about .., <, <=. other operators
     *  are not evaluated.
     *
     * In order to do this it has to parse the inline date string
     *
     * @param propertyValue
     * @return
     */
    private static String parseAndConvertDateToRange(String propertyValue) {

        String sRet = propertyValue;

        if (StringUtils.contains(propertyValue, SearchOperator.BETWEEN.op())) {
            String[] rangeValues = propertyValue.split("\\.\\."); // this translate to the .. operator
            sRet = ObjectUtils.clean(rangeValues[0].trim()) + " " + SearchOperator.BETWEEN.op() + " " + cleanUpperBound(ObjectUtils.clean(rangeValues[1].trim()));
        }  else if (propertyValue.startsWith(SearchOperator.LESS_THAN_EQUAL.op())) {
            sRet = SearchOperator.LESS_THAN_EQUAL + cleanUpperBound(ObjectUtils.clean(propertyValue));
        }  else if (propertyValue.startsWith(SearchOperator.LESS_THAN.op())) {
            sRet = SearchOperator.LESS_THAN + cleanUpperBound(ObjectUtils.clean(propertyValue));
        }

        return sRet;
    }


}
