/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.docsearch;

import org.kuali.rice.kns.web.ui.Field;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import org.kuali.rice.kew.engine.node.KeyValuePair;


/**
 * This is a description of what this class does - jjhanso don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class DocumentSearchField extends Field {
    public static final String MULTIBOX = "multibox";
    public static final String DATEPICKER = "datePicker";

    private static final Boolean DEFAULT_ALLOW_WILDCARD_VALUE = Boolean.TRUE;
    private static final Boolean DEFAULT_AUTO_WILDCARD_BEGINNING_VALUE = Boolean.FALSE;
    private static final Boolean DEFAULT_AUTO_WILDCARD_ENDING_VALUE = Boolean.FALSE;
    private static final Boolean DEFAULT_CASE_SENSITIVE_VALUE = Boolean.TRUE;
    private static final Boolean DEFAULT_RANGE_FIELD_INCLUSIVE_VALUE = Boolean.TRUE;

    // fields for behind the scenes
    private String fieldDataType = SearchableAttribute.DEFAULT_SEARCHABLE_ATTRIBUTE_TYPE_NAME;
    private Boolean hasDatePicker = null;
    private Boolean allowWildcards;
    private Boolean autoWildcardBeginning;
    private Boolean autoWildcardEnding;
    private Boolean caseSensitive;

    private boolean searchable = true;

    // following values used in ranged searches
    private String mainFieldLabel;  // the fieldLabel holds things like "From" and "Ending" and this field holds things like "Total Amount"
    private Boolean rangeFieldInclusive;
    private String savablePropertyName = null;
    private boolean memberOfRange = false;
    private String[] propertyValues;

    public static final Set<String> SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES;
    public static final Set<String> MULTI_VALUE_FIELD_TYPES = new HashSet<String>();
    static {
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES = new HashSet<String>();
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(HIDDEN);
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(TEXT);
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(DROPDOWN);
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(RADIO);
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(DROPDOWN_REFRESH);
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(MULTIBOX);

        MULTI_VALUE_FIELD_TYPES.add(MULTIBOX);
    }


    private Map<String,String> displayParameters = new HashMap<String,String>();
    private List<KeyValuePair> customConversions;  //related to fieldConversions??

    // below boolean used by criteria processor to hide field without removing classic 'field type' variable
    private boolean hidden = false;

    // this field is currently a hack to allow us to indicate whether or not the column of data associated
    // with a particular field will be visible in the result set of a search or not
    private boolean isColumnVisible = true;

    public DocumentSearchField() {
        super.setFieldLevelHelpEnabled(false);
    }

    public DocumentSearchField(String fieldLabel,
                               String fieldHelpUrl,
                               String fieldType,
                               String propertyName,
                               String propertyValue,
                               List fieldValidValues,
                               String quickFinderClassNameImpl) {
        super(propertyName, fieldLabel);
        super.setFieldHelpUrl(fieldHelpUrl);
        super.setFieldType(fieldType);
        this.savablePropertyName = propertyName;
        super.setPropertyValue(propertyValue);
        super.setFieldValidValues(fieldValidValues);
        super.setQuickFinderClassNameImpl(quickFinderClassNameImpl);
    }

    public DocumentSearchField(String fieldLabel,
                               String fieldHelpUrl,
                               String fieldType,
                               String propertyName,
                               String[] propertyValues,
                               List fieldValidValues,
                               String quickFinderClassNameImpl) {
        super(propertyName, fieldLabel);
        super.setFieldHelpUrl(fieldHelpUrl);
        super.setFieldType(fieldType);
        this.setPropertyValues(propertyValues);
        this.savablePropertyName = propertyName;
        super.setFieldValidValues(fieldValidValues);
        super.setQuickFinderClassNameImpl(quickFinderClassNameImpl);
    }


    private boolean getPolicyBooleanValue(Boolean defaultValue,Boolean valueSet) {
        if (valueSet != null) {
            return valueSet.booleanValue();
        }
        return defaultValue.booleanValue();
    }

    /**
     * @return the autoWildcardBeginning
     */
    public Boolean getAutoWildcardBeginning() {
        return autoWildcardBeginning;
    }

    /**
     * @param autoWildcardBeginning the autoWildcardBeginning to set
     */
    public void setAutoWildcardBeginning(Boolean autoWildcardBeginning) {
        this.autoWildcardBeginning = autoWildcardBeginning;
    }

    /**
     * @return the autoWildcardEnding
     */
    public Boolean getAutoWildcardEnding() {
        return autoWildcardEnding;
    }

    /**
     * @param autoWildcardEnding the autoWildcardEnding to set
     */
    public void setAutoWildcardEnding(Boolean autoWildcardEnding) {
        this.autoWildcardEnding = autoWildcardEnding;
    }

    public String getFieldDataType() {
        return this.fieldDataType;
    }

    public Boolean getAllowWildcards() {
        return this.allowWildcards;
    }

    public Boolean getCaseSensitive() {
        return this.caseSensitive;
    }

    public void setFieldDataType(String fieldDataType) {
        this.fieldDataType = fieldDataType;
    }

    public void setAllowWildcards(Boolean allowWildcards) {
        this.allowWildcards = allowWildcards;
    }

    public void setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isSearchable() {
        return this.searchable;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    public String getMainFieldLabel() {
        return this.mainFieldLabel;
    }

    public Boolean getRangeFieldInclusive() {
        return this.rangeFieldInclusive;
    }

    public String getSavablePropertyName() {
        return this.savablePropertyName;
    }

    public boolean isMemberOfRange() {
        return this.memberOfRange;
    }

    public void setMainFieldLabel(String mainFieldLabel) {
        this.mainFieldLabel = mainFieldLabel;
    }

    public void setRangeFieldInclusive(Boolean rangeFieldInclusive) {
        this.rangeFieldInclusive = rangeFieldInclusive;
    }

    public void setSavablePropertyName(String savablePropertyName) {
        this.savablePropertyName = savablePropertyName;
    }

    public void setMemberOfRange(boolean memberOfRange) {
        this.memberOfRange = memberOfRange;
    }

    public String[] getPropertyValues() {
        return this.propertyValues;
    }

    public Map<String, String> getDisplayParameters() {
        return this.displayParameters;
    }

    public void setPropertyValues(String[] propertyValues) {
        this.propertyValues = propertyValues;
    }

    public void setDisplayParameters(Map<String, String> displayParameters) {
        this.displayParameters = displayParameters;
    }

    public List<KeyValuePair> getCustomConversions() {
        return this.customConversions;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public boolean isColumnVisible() {
        return this.isColumnVisible;
    }

    public void setCustomConversions(List<KeyValuePair> customConversions) {
        this.customConversions = customConversions;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setColumnVisible(boolean isColumnVisible) {
        this.isColumnVisible = isColumnVisible;
    }

    public boolean isInclusive() {
        return getPolicyBooleanValue(DEFAULT_RANGE_FIELD_INCLUSIVE_VALUE, rangeFieldInclusive);
    }

    public boolean isAllowingWildcards() {
        return getPolicyBooleanValue(DEFAULT_ALLOW_WILDCARD_VALUE, allowWildcards);
    }

    public boolean isCaseSensitive() {
        return getPolicyBooleanValue(DEFAULT_CASE_SENSITIVE_VALUE, caseSensitive);
    }

    public boolean isAutoWildcardAtBeginning() {
        return getPolicyBooleanValue(DEFAULT_AUTO_WILDCARD_BEGINNING_VALUE, autoWildcardBeginning);
    }

    public boolean isAutoWildcardAtEnding() {
        return getPolicyBooleanValue(DEFAULT_AUTO_WILDCARD_ENDING_VALUE, autoWildcardEnding);
    }

    public boolean isUsingDatePicker() {
        return getPolicyBooleanValue(Boolean.valueOf(SearchableAttribute.DATA_TYPE_DATE.equalsIgnoreCase(this.fieldDataType)), hasDatePicker);
    }
    
    public void setHasDatePicker(Boolean hasDatePicker) {
    	this.hasDatePicker = hasDatePicker;
    }

    public boolean isUsingCustomConversions() {
        return (this.customConversions != null) && (!this.customConversions.isEmpty());
        }


    public String getDisplayParameterValue(String key) {
        return displayParameters.get(key);
    }

    public void addDisplayParameter(String key, String value) {
        displayParameters.put(key, value);
    }


}
