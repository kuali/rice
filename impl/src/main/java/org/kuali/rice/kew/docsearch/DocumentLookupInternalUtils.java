package org.kuali.rice.kew.docsearch;

import org.kuali.rice.core.api.uif.AttributeLookupSettings;
import org.kuali.rice.core.api.uif.DataType;
import org.kuali.rice.core.api.uif.RemotableAttributeField;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Defines various utilities for internal use in the reference implementation of the document lookup functionality.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DocumentLookupInternalUtils {

    private static final boolean CASE_SENSITIVE_DEFAULT = true;

    private static final String STRING_ATTRIBUTE_TABLE_NAME = "KREW_DOC_HDR_EXT_T";
    private static final String DATE_TIME_ATTRIBUTE_TABLE_NAME = "KREW_DOC_HDR_EXT_DT_T";
    private static final String DECIMAL_ATTRIBUTE_TABLE_NAME = "KREW_DOC_HDR_EXT_FLT_T";
    private static final String INTEGER_ATTRIBUTE_TABLE_NAME = "KREW_DOC_HDR_EXT_LONG_T";

    private static final List<SearchableAttributeConfiguration> CONFIGURATIONS =
            new ArrayList<SearchableAttributeConfiguration>();
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

    // TODO - Rice 2.0 - need to determine what the default for case sensitivity is and should be, right now defaulting
    // to "true"
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

}
