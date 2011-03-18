/*
 * Copyright 2005-2008 The Kuali Foundation
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.util.KEWConstants;

import java.io.Serializable;
import java.util.List;


/**
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class SearchAttributeCriteriaComponent implements Serializable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SearchAttributeCriteriaComponent.class);
	
	private static final long serialVersionUID = -5927435567057306529L;

	private String formKey;  // this if the field that is used in the UI for the form
    private String value;
    private List<String> values;
    private String lookupableFieldType;
    private boolean caseSensitive = false;
    private boolean searchInclusive = true;  // not just for ranges... used by single date searches
    private SearchableAttributeValue searchableAttributeValue;
    private boolean searchable = true;
    private boolean canHoldMultipleValues = false;
    
    // range properties
    private boolean rangeSearch = false;
    private boolean allowInlineRange = false;
    // this is the field that is saved to the database
    private String savedKey;

	/**
	 * @param formKey key value associated with the search form
	 * @param value value the user is searching on
	 * @param savedKey key value associated with the value saved in the database
	 */
	public SearchAttributeCriteriaComponent(String formKey, String value, boolean rangeSearch) {
		super();
		this.formKey = formKey;
		this.value = value;
		this.rangeSearch = rangeSearch;
		if (!rangeSearch) {
			this.savedKey = formKey;
		}
	}
	
	/**
	 * @param formKey key value associated with the search form
	 * @param value value the user is searching on
	 * @param savedKey key value associated with the value saved in the database
	 */
	public SearchAttributeCriteriaComponent(String formKey, String value, String savedKey) {
		super();
		this.formKey = formKey;
		this.value = value;
		this.savedKey = savedKey;
	}
	
	/**
	 * @param formKey key value associated with the search form
	 * @param value value the user is searching on
	 * @param savedKey key value associated with the value saved in the database
	 * @param searchableAttributeValue
	 */
	public SearchAttributeCriteriaComponent(String formKey, String value, String savedKey, SearchableAttributeValue searchableAttributeValue) {
		super();
		this.formKey = formKey;
		this.value = value;
		this.savedKey = savedKey;
		this.searchableAttributeValue = searchableAttributeValue;
	}
	
	public boolean isComponentLowerBoundValue() {
		return isComponentGivenBoundValue(KEWConstants.SearchableAttributeConstants.RANGE_LOWER_BOUND_PROPERTY_PREFIX);
	}
	
	public boolean isComponentUpperBoundValue() {
		return isComponentGivenBoundValue(KEWConstants.SearchableAttributeConstants.RANGE_UPPER_BOUND_PROPERTY_PREFIX);
	}
	
	private boolean isComponentGivenBoundValue(String boundKeyPrefix) {
		if (!isRangeSearch()) {
			String errorMsg = "Criteria Component with formKey value '" + formKey + "' is not part of a range search";
			LOG.error("isComponentGivenBoundValue() " + errorMsg);
			throw new RuntimeException(errorMsg);
		}
		return formKey.indexOf(boundKeyPrefix) == 0;
	}
    
    public boolean isNonBlankValueGiven() {
        return ( (StringUtils.isNotBlank(getValue())) || (!CollectionUtils.isEmpty(getValues())) );
    }

	/**
     * @return the canHoldMultipleValues
     */
    public boolean isCanHoldMultipleValues() {
        return canHoldMultipleValues;
    }

    /**
     * @param canHoldMultipleValues the canHoldMultipleValues to set
     */
    public void setCanHoldMultipleValues(boolean canHoldMultipleValues) {
        this.canHoldMultipleValues = canHoldMultipleValues;
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
	 * @return the caseSensitive
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * @param caseSensitive the caseSensitive to set
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * @return the formKey
	 */
	public String getFormKey() {
		return formKey;
	}

	/**
	 * @param formKey the formKey to set
	 */
	public void setFormKey(String formKey) {
		this.formKey = formKey;
	}

	/**
	 * @return the rangeSearch
	 */
	public boolean isRangeSearch() {
		return rangeSearch;
	}

	/**
	 * @param rangeSearch the rangeSearch to set
	 */
	public void setRangeSearch(boolean rangeSearch) {
		this.rangeSearch = rangeSearch;
	}

	/**
	 * @return the savedKey
	 */
	public String getSavedKey() {
		return savedKey;
	}

	/**
	 * @param savedKey the savedKey to set
	 */
	public void setSavedKey(String savedKey) {
		this.savedKey = savedKey;
	}

	/**
	 * @return the searchableAttributeValue
	 */
	public SearchableAttributeValue getSearchableAttributeValue() {
		return searchableAttributeValue;
	}

	/**
	 * @param searchableAttributeValue the searchableAttributeValue to set
	 */
	public void setSearchableAttributeValue(
			SearchableAttributeValue searchableAttributeValue) {
		this.searchableAttributeValue = searchableAttributeValue;
	}

	/**
	 * @return the searchInclusive
	 */
	public boolean isSearchInclusive() {
		return searchInclusive;
	}

	/**
	 * @param searchInclusive the searchInclusive to set
	 */
	public void setSearchInclusive(boolean searchInclusive) {
		this.searchInclusive = searchInclusive;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

    /**
     * @return the values
     */
    public List<String> getValues() {
        return values;
    }

    /**
     * @param values the values to set
     */
    public void setValues(List<String> values) {
        this.values = values;
    }

    /**
     * @return the lookupableFieldType
     */
    public String getLookupableFieldType() {
        return lookupableFieldType;
    }

    /**
     * @param lookupableFieldType the lookupableFieldType to set
     */
    public void setLookupableFieldType(String lookupableFieldType) {
        this.lookupableFieldType = lookupableFieldType;
    }

	/**
	 * @return the allowInlineRange
	 */
	public boolean isAllowInlineRange() {
		return this.allowInlineRange;
	}

	/**
	 * @param allowInlineRange the allowInlineRange to set
	 */
	public void setAllowInlineRange(boolean allowInlineRange) {
		this.allowInlineRange = allowInlineRange;
	}

}
