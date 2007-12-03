/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package edu.iu.uis.eden.lookupable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import edu.iu.uis.eden.docsearch.SearchableAttribute;
import edu.iu.uis.eden.engine.node.KeyValuePair;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;

/**
 * A field of data used by {@link WorkflowLookupable} implementations.  The field is typed according to
 * the type of field it is and how it should be rendered on the Lookupable.
 *
 * @see WorkflowLookupable
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Field implements java.io.Serializable {

	private static final long serialVersionUID = 8497421452176749283L;

	public static final String HIDDEN = "hidden";
    public static final String TEXT = "text";
    public static final String DROPDOWN = "dropdown";
    public static final String RADIO = "radio";
    public static final String QUICKFINDER = "quickFinder";
    public static final String DATEPICKER = "datePicker";
    public static final String LOOKUP_RESULT_ONLY = "lookupresultonly";
    public static final String DROPDOWN_REFRESH = "dropdown_refresh";
    public static final String MULTIBOX = "multibox";
    public static final String CHECKBOX_YES_NO = "checkbox_yes_no";
    public static final String CHECKBOX_PRESENT = "checkbox_present";

    public static final Set SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES;
    public static final Set MULTI_VALUE_FIELD_TYPES = new HashSet();
    static {
    	SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES = new HashSet();
    	SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(HIDDEN);
    	SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(TEXT);
    	SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(DROPDOWN);
    	SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(RADIO);
    	SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(DROPDOWN_REFRESH);
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(MULTIBOX);
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(CHECKBOX_YES_NO);
        SEARCH_RESULT_DISPLAYABLE_FIELD_TYPES.add(CHECKBOX_PRESENT);

        MULTI_VALUE_FIELD_TYPES.add(MULTIBOX);
    }

    private static final Boolean DEFAULT_ALLOW_WILDCARD_VALUE = Boolean.TRUE;
    private static final Boolean DEFAULT_AUTO_WILDCARD_BEGINNING_VALUE = Boolean.FALSE;
    private static final Boolean DEFAULT_AUTO_WILDCARD_ENDING_VALUE = Boolean.FALSE;
    private static final Boolean DEFAULT_CASE_SENSITIVE_VALUE = Boolean.TRUE;
    private static final Boolean DEFAULT_RANGE_FIELD_INCLUSIVE_VALUE = Boolean.TRUE;
    public static final String CHECKBOX_VALUE_CHECKED = "Yes";
    public static final String CHECKBOX_VALUE_UNCHECKED = "No";

    // fields for display
    private String fieldType;
    private boolean hasLookupable;
    private Boolean hasDatePicker = null;//
    private String fieldLabel;
    private String fieldHelpUrl;
    private String propertyName;
    private String propertyValue;
    private String[] propertyValues;
    private String defaultLookupableName;
    private List fieldValidValues;
    private String quickFinderClassNameImpl;
    private Map<String,String> displayParameters = new HashMap<String,String>();
    private List<KeyValuePair> customConversions;

    // this field is currently a hack to allow us to indicate whether or not the column of data associated
    // with a particular field will be visible in the result set of a search or not
    private boolean isColumnVisible = true;

    // fields for behind the scenes
    private String fieldDataType = SearchableAttribute.DEFAULT_SEARCHABLE_ATTRIBUTE_TYPE_NAME;//
    private Boolean allowWildcards;//
    private Boolean autoWildcardBeginning;//
    private Boolean autoWildcardEnding;//
    private Boolean caseSensitive;//

    private boolean searchable = true;

    // following values used in ranged searches
    private String mainFieldLabel;  // the fieldLabel holds things like "From" and "Ending" and this field holds things like "Total Amount"
    private Boolean rangeFieldInclusive;//
    private String savablePropertyName = null;//
    private boolean memberOfRange = false;//

    public Field() {}

    public Field(String fieldLabel, String fieldHelpUrl, String fieldType, boolean hasLookupable, String propertyName, String propertyValue, List fieldValidValues, String quickFinderClassNameImpl) {
        this(fieldLabel, fieldHelpUrl, fieldType, hasLookupable, propertyName, propertyValue, fieldValidValues, quickFinderClassNameImpl, null);
    }

    public Field(String fieldLabel, String fieldHelpUrl, String fieldType, boolean hasLookupable, String propertyName, String[] propertyValues, List fieldValidValues, String quickFinderClassNameImpl) {
        this(fieldLabel, fieldHelpUrl, fieldType, hasLookupable, propertyName, propertyValues, fieldValidValues, quickFinderClassNameImpl, null);
    }

    public Field(String fieldLabel, String fieldHelpUrl, String fieldType, boolean hasLookupable, String propertyName, String propertyValue, List fieldValidValues, String quickFinderClassNameImpl, String defaultLookupableName) {
        setupField(fieldLabel, fieldHelpUrl, fieldType, hasLookupable, propertyName, fieldValidValues, quickFinderClassNameImpl, defaultLookupableName);
        this.propertyValue = propertyValue;
    }

    public Field(String fieldLabel, String fieldHelpUrl, String fieldType, boolean hasLookupable, String propertyName, String[] propertyValues, List fieldValidValues, String quickFinderClassNameImpl, String defaultLookupableName) {
        setupField(fieldLabel, fieldHelpUrl, fieldType, hasLookupable, propertyName, fieldValidValues, quickFinderClassNameImpl, defaultLookupableName);
        this.propertyValues = propertyValues;
    }

    private void setupField(String fieldLabel, String fieldHelpUrl, String fieldType, boolean hasLookupable, String propertyName, List fieldValidValues, String quickFinderClassNameImpl, String defaultLookupableName) {
        this.fieldLabel = fieldLabel;
        this.fieldHelpUrl = fieldHelpUrl;
        this.fieldType = fieldType;
        this.hasLookupable = hasLookupable;
        this.propertyName = propertyName;
        this.savablePropertyName = propertyName;
        this.fieldValidValues = fieldValidValues;
        this.quickFinderClassNameImpl = quickFinderClassNameImpl;
        this.defaultLookupableName = defaultLookupableName;
    }

    public void populateFieldFromExistingField(Field existingField) {
        setColumnVisible(existingField.isColumnVisible());
        setFieldDataType(existingField.getFieldDataType());
        setFieldHelpUrl(existingField.getFieldHelpUrl());
        setFieldType(existingField.getFieldType());
        setMainFieldLabel(existingField.getFieldLabel());
        setFieldValidValues(existingField.getFieldValidValues());
        setSavablePropertyName(existingField.getPropertyName());
        setQuickFinderClassNameImpl(existingField.getQuickFinderClassNameImpl());
        setHasLookupable(existingField.isHasLookupable());
        setDefaultLookupableName(existingField.getDefaultLookupableName());
        setDisplayParameters(existingField.getDisplayParameters());
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

    private boolean getPolicyBooleanValue(Boolean defaultValue,Boolean valueSet) {
    	if (valueSet != null) {
    		return valueSet.booleanValue();
    	}
    	return defaultValue.booleanValue();
    }

    /**
     * Helper method to determine if this is a field that collects data.
     *
     * @param fieldType
     */
    public boolean isInputField() {
        if (StringUtils.isBlank(fieldType)) {
            return false;
        }
        if (fieldType.equals(Field.DROPDOWN) ||
        		fieldType.equals(Field.DROPDOWN_REFRESH) ||
        		fieldType.equals(Field.TEXT) ||
        		fieldType.equals(Field.RADIO) ||
        		fieldType.equals(Field.HIDDEN)) {
            return true;
        }
        else {
            return false;
        }

    }

    public boolean isUsingCustomConversions() {
	return (this.customConversions != null) && (!this.customConversions.isEmpty());
    }


    	/**
	 * @return the customConversions
	 */
	public List<KeyValuePair> getCustomConversions() {
	    return this.customConversions;
	}

	/**
	 * @param customConversions the customConversions to set
	 */
	public void setCustomConversions(List<KeyValuePair> customConversions) {
	    this.customConversions = customConversions;
	}

	   public String getDisplayParameterValue(String key) {
	        return displayParameters.get(key);
	    }

	    public void addDisplayParameter(String key, String value) {
	        displayParameters.put(key, value);
	    }

	    /**
	     * @return the displayParameters
	     */
	    public Map<String, String> getDisplayParameters() {
	        return displayParameters;
	    }

	    /**
	     * @param displayParameters the displayParameters to set
	     */
	    public void setDisplayParameters(Map<String, String> displayParameters) {
	        this.displayParameters = displayParameters;
	    }

	/**
	 * @return the allowWildcards
	 */
	public Boolean getAllowWildcards() {
		return allowWildcards;
	}

	/**
	 * @param allowWildcards the allowWildcards to set
	 */
	public void setAllowWildcards(Boolean allowWildcards) {
		this.allowWildcards = allowWildcards;
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

	/**
	 * @return the caseSensitive
	 */
	public Boolean getCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * @param caseSensitive the caseSensitive to set
	 */
	public void setCaseSensitive(Boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * @return the defaultLookupableName
	 */
	public String getDefaultLookupableName() {
		return defaultLookupableName;
	}

	/**
	 * @param defaultLookupableName the defaultLookupableName to set
	 */
	public void setDefaultLookupableName(String defaultLookupableName) {
		this.defaultLookupableName = defaultLookupableName;
	}

	/**
	 * @return the fieldDataType
	 */
	public String getFieldDataType() {
		return fieldDataType;
	}

	/**
	 * @param fieldDataType the fieldDataType to set
	 */
	public void setFieldDataType(String fieldDataType) {
		this.fieldDataType = fieldDataType;
	}

	/**
	 * @return the fieldHelpUrl
	 */
	public String getFieldHelpUrl() {
		return fieldHelpUrl;
	}

	/**
	 * @param fieldHelpUrl the fieldHelpUrl to set
	 */
	public void setFieldHelpUrl(String fieldHelpUrl) {
		this.fieldHelpUrl = fieldHelpUrl;
	}

	/**
	 * @return the fieldLabel
	 */
	public String getFieldLabel() {
		return fieldLabel;
	}

	/**
	 * @param fieldLabel the fieldLabel to set
	 */
	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}

	/**
	 * @return the fieldType
	 */
	public String getFieldType() {
		return fieldType;
	}

	/**
	 * @param fieldType the fieldType to set
	 */
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * @return the fieldValidValues
	 */
	public List getFieldValidValues() {
		return fieldValidValues;
	}

	/**
	 * @param fieldValidValues the fieldValidValues to set
	 */
	public void setFieldValidValues(List fieldValidValues) {
		this.fieldValidValues = fieldValidValues;
	}

	/**
	 * @return the hasDatePicker
	 */
	public Boolean getHasDatePicker() {
		return hasDatePicker;
	}

	/**
	 * @param hasDatePicker the hasDatePicker to set
	 */
	public void setHasDatePicker(Boolean hasDatePicker) {
		this.hasDatePicker = hasDatePicker;
	}

	/**
	 * @return the hasLookupable
	 */
	public boolean isHasLookupable() {
		return hasLookupable;
	}

	/**
	 * @param hasLookupable the hasLookupable to set
	 */
	public void setHasLookupable(boolean hasLookupable) {
		this.hasLookupable = hasLookupable;
	}

	/**
	 * @return the isColumnVisible
	 */
	public boolean isColumnVisible() {
		return isColumnVisible;
	}

	/**
	 * @param isColumnVisible the isColumnVisible to set
	 */
	public void setColumnVisible(boolean isColumnVisible) {
		this.isColumnVisible = isColumnVisible;
	}

	/**
	 * @return the memberOfRange
	 */
	public boolean isMemberOfRange() {
		return memberOfRange;
	}

	/**
	 * @param memberOfRange the memberOfRange to set
	 */
	public void setMemberOfRange(boolean memberOfRange) {
		this.memberOfRange = memberOfRange;
	}

	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @param propertyName the propertyName to set
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * @return the propertyValue
	 */
	public String getPropertyValue() {
		return propertyValue;
	}

	/**
	 * @param propertyValue the propertyValue to set
	 */
	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}

	/**
	 * @return the quickFinderClassNameImpl
	 */
	public String getQuickFinderClassNameImpl() {
		return quickFinderClassNameImpl;
	}

	/**
	 * @param quickFinderClassNameImpl the quickFinderClassNameImpl to set
	 */
	public void setQuickFinderClassNameImpl(String quickFinderClassNameImpl) {
		this.quickFinderClassNameImpl = quickFinderClassNameImpl;
	}

	/**
	 * @return the rangeFieldInclusive
	 */
	public Boolean getRangeFieldInclusive() {
		return rangeFieldInclusive;
	}

	/**
	 * @param rangeFieldInclusive the rangeFieldInclusive to set
	 */
	public void setRangeFieldInclusive(Boolean rangeFieldInclusive) {
		this.rangeFieldInclusive = rangeFieldInclusive;
	}

	/**
	 * @return the savablePropertyName
	 */
	public String getSavablePropertyName() {
		return savablePropertyName;
	}

	/**
	 * @param savablePropertyName the savablePropertyName to set
	 */
	public void setSavablePropertyName(String savablePropertyName) {
		this.savablePropertyName = savablePropertyName;
	}

	/**
     * @return the mainFieldLabel
     */
    public String getMainFieldLabel() {
        return mainFieldLabel;
    }

    /**
     * @param mainFieldLabel the mainFieldLabel to set
     */
    public void setMainFieldLabel(String mainFieldLabel) {
        this.mainFieldLabel = mainFieldLabel;
    }

    /**
     * @return the propertyValues
     */
    public String[] getPropertyValues() {
        return propertyValues;
    }

    /**
     * @param propertyValues the propertyValues to set
     */
    public void setPropertyValues(String[] propertyValues) {
        this.propertyValues = propertyValues;
    }

    /**
     * @return the searchable
     */
    public boolean isSearchable() {
        return searchable;
    }

    /**
     * @param searchable the searchable to set
     */
    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    /**
     * @return the cHECKBOX_PRESENT
     */
    public String getCHECKBOX_PRESENT() {
        return CHECKBOX_PRESENT;
    }

    /**
     * @return the cHECKBOX_YES_NO
     */
    public String getCHECKBOX_YES_NO() {
        return CHECKBOX_YES_NO;
    }

    /**
     * @return the dATEPICKER
     */
    public String getDATEPICKER() {
        return DATEPICKER;
    }

    /**
     * @return the dROPDOWN
     */
    public String getDROPDOWN() {
        return DROPDOWN;
    }

    /**
     * @return the dROPDOWN_REFRESH
     */
    public String getDROPDOWN_REFRESH() {
        return DROPDOWN_REFRESH;
    }

    /**
     * @return the hIDDEN
     */
    public String getHIDDEN() {
        return HIDDEN;
    }

    /**
     * @return the lOOKUP_RESULT_ONLY
     */
    public String getLOOKUP_RESULT_ONLY() {
        return LOOKUP_RESULT_ONLY;
    }

    /**
     * @return the mULTIBOX
     */
    public String getMULTIBOX() {
        return MULTIBOX;
    }

    /**
     * @return the qUICKFINDER
     */
    public String getQUICKFINDER() {
        return QUICKFINDER;
    }

    /**
     * @return the rADIO
     */
    public String getRADIO() {
        return RADIO;
    }

    /**
     * @return the tEXT
     */
    public String getTEXT() {
        return TEXT;
    }

    /**
     * @return the cHECKBOX_VALUE_CHECKED
     */
    public String getCHECKBOX_VALUE_CHECKED() {
        return CHECKBOX_VALUE_CHECKED;
    }

    /**
     * @return the cHECKBOX_VALUE_UNCHECKED
     */
    public String getCHECKBOX_VALUE_UNCHECKED() {
        return CHECKBOX_VALUE_UNCHECKED;
    }

}