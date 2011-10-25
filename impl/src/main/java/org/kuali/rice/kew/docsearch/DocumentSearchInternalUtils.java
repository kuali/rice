package org.kuali.rice.kew.docsearch;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.reflect.ObjectDefinition;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.uif.AttributeLookupSettings;
import org.kuali.rice.core.api.uif.DataType;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.framework.resourceloader.ObjectDefinitionResolver;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Defines various utilities for internal use in the reference implementation of the document search functionality.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentSearchInternalUtils {

    private static final Logger LOG = Logger.getLogger(DocumentSearchInternalUtils.class);

    private static final boolean CASE_SENSITIVE_DEFAULT = true;

    private static final String STRING_ATTRIBUTE_TABLE_NAME = "KREW_DOC_HDR_EXT_T";
    private static final String DATE_TIME_ATTRIBUTE_TABLE_NAME = "KREW_DOC_HDR_EXT_DT_T";
    private static final String DECIMAL_ATTRIBUTE_TABLE_NAME = "KREW_DOC_HDR_EXT_FLT_T";
    private static final String INTEGER_ATTRIBUTE_TABLE_NAME = "KREW_DOC_HDR_EXT_LONG_T";

    private static final List<SearchableAttributeConfiguration> CONFIGURATIONS =
            new ArrayList<SearchableAttributeConfiguration>();
    public static final List<Class<? extends SearchableAttributeValue>> SEARCHABLE_ATTRIBUTE_BASE_CLASS_LIST =
            new ArrayList<Class<? extends SearchableAttributeValue>>();
    static {
        SEARCHABLE_ATTRIBUTE_BASE_CLASS_LIST.add(SearchableAttributeStringValue.class);
        SEARCHABLE_ATTRIBUTE_BASE_CLASS_LIST.add(SearchableAttributeFloatValue.class);
        SEARCHABLE_ATTRIBUTE_BASE_CLASS_LIST.add(SearchableAttributeLongValue.class);
        SEARCHABLE_ATTRIBUTE_BASE_CLASS_LIST.add(SearchableAttributeDateTimeValue.class);
    }

    static {

        CONFIGURATIONS.add(new SearchableAttributeConfiguration(
                STRING_ATTRIBUTE_TABLE_NAME,
                EnumSet.of(DataType.BOOLEAN, DataType.STRING, DataType.MARKUP),
                String.class));

        CONFIGURATIONS.add(new SearchableAttributeConfiguration(
                DATE_TIME_ATTRIBUTE_TABLE_NAME,
                EnumSet.of(DataType.DATE, DataType.TRUNCATED_DATE),
                Timestamp.class));

        CONFIGURATIONS.add(new SearchableAttributeConfiguration(
                DECIMAL_ATTRIBUTE_TABLE_NAME,
                EnumSet.of(DataType.FLOAT, DataType.DOUBLE),
                Float.TYPE));

        CONFIGURATIONS.add(new SearchableAttributeConfiguration(
                INTEGER_ATTRIBUTE_TABLE_NAME,
                EnumSet.of(DataType.INTEGER, DataType.LONG),
                Long.TYPE));

    }

    public static boolean isLookupCaseSensitive(RemotableAttributeField remotableAttributeField) {
        if (remotableAttributeField == null) {
            throw new IllegalArgumentException("remotableAttributeField was null");
        }
        AttributeLookupSettings lookupSettings = remotableAttributeField.getAttributeLookupSettings();
        if (lookupSettings != null) {
            if (lookupSettings.isCaseSensitive() != null) {
                return lookupSettings.isCaseSensitive().booleanValue();
            }
        }
        return CASE_SENSITIVE_DEFAULT;
    }

    public static String getAttributeTableName(RemotableAttributeField attributeField) {
        return getConfigurationForField(attributeField).getTableName();
    }

    public static Class<?> getDataTypeClass(RemotableAttributeField attributeField) {
        return getConfigurationForField(attributeField).getDataTypeClass();
    }

    private static SearchableAttributeConfiguration getConfigurationForField(RemotableAttributeField attributeField) {
        for (SearchableAttributeConfiguration configuration : CONFIGURATIONS) {
            DataType dataType = attributeField.getDataType();
            if (dataType == null) {
                dataType = DataType.STRING;
            }
            if (configuration.getSupportedDataTypes().contains(dataType))  {
                return configuration;
            }
        }
        throw new IllegalArgumentException("Failed to determine proper searchable attribute configuration for given data type of '" + attributeField.getDataType() + "'");
    }

    public static List<SearchableAttributeValue> getSearchableAttributeValueObjectTypes() {
        List<SearchableAttributeValue> searchableAttributeValueClasses = new ArrayList<SearchableAttributeValue>();
        for (Class<? extends SearchableAttributeValue> searchAttributeValueClass : SEARCHABLE_ATTRIBUTE_BASE_CLASS_LIST) {
            ObjectDefinition objDef = new ObjectDefinition(searchAttributeValueClass);
            SearchableAttributeValue attributeValue = (SearchableAttributeValue) ObjectDefinitionResolver.createObject(
                    objDef, ClassLoaderUtils.getDefaultClassLoader(), false);
            searchableAttributeValueClasses.add(attributeValue);
        }
        return searchableAttributeValueClasses;
    }

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

    public static String getDisplayValueWithDateOnly(DateTime value) {
        return getDisplayValueWithDateOnly(new Timestamp(value.getMillis()));
    }

    public static String getDisplayValueWithDateOnly(Timestamp value) {
        return RiceConstants.getDefaultDateFormat().format(new Date(value.getTime()));
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

    public static class SearchableAttributeConfiguration {

        private final String tableName;
        private final EnumSet<DataType> supportedDataTypes;
        private final Class<?> dataTypeClass;

        public SearchableAttributeConfiguration(String tableName,
                EnumSet<DataType> supportedDataTypes,
                Class<?> dataTypeClass) {
            this.tableName = tableName;
            this.supportedDataTypes = supportedDataTypes;
            this.dataTypeClass = dataTypeClass;
        }

        public String getTableName() {
            return tableName;
        }

        public EnumSet<DataType> getSupportedDataTypes() {
            return supportedDataTypes;
        }

        public Class<?> getDataTypeClass() {
            return dataTypeClass;
        }

    }

    /**
     * Unmarshals a DocumentSearchCriteria from JSON string
     * @param string the JSON
     * @return unmarshalled DocumentSearchCriteria
     * @throws IOException
     */
    public static DocumentSearchCriteria unmarshalDocumentSearchCriteria(String string) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        DocumentSearchCriteria.Builder builder = (DocumentSearchCriteria.Builder) jsonMapper.readValue(string, DocumentSearchCriteria.Builder.class); // see JacksonRiceModule for details of unmarshalling
        // fix up the Joda DateTimes
        builder.normalizeDateTimes();
        // build() it
        return builder.build();
    }

    /**
     * Marshals a DocumentSearchCriteria to JSON string
     * @param criteria the criteria
     * @return a JSON string
     * @throws IOException
     */
    public static String marshalDocumentSearchCriteria(DocumentSearchCriteria criteria) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        jsonMapper.getSerializationConfig().setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        // Jackson XC support not included by Rice, so no auto-magic JAXB-compatibility
        // AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        // // make deserializer use JAXB annotations (only)
        // mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
        // // make serializer use JAXB annotations (only)
        // mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
        return jsonMapper.writeValueAsString(criteria);
    }
}