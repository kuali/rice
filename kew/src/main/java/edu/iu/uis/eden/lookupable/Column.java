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
import java.util.Map;

import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;
import edu.iu.uis.eden.util.Utilities;

/**
 * Represents a column within a table in the user interface for Lookupables.
 *
 * @see WorkflowLookupable
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class Column {

	public static final String COLUMN_IS_SORTABLE_VALUE = "true";
	public static final String COLUMN_NOT_SORTABLE_VALUE = "false";

	public static final String TEXT = "text";
	public static final String INTEGER = "integer";
	public static final String LONG = "long";
	public static final String FLOAT = "float";
	public static final String DATETIME = "datetime";

	private String columnTitle;
	private String sortable;
	private String key;
	private String propertyName;
	private String sortPropertyName;
	private String type = TEXT;
	private Map<String,String> displayParameters = new HashMap<String,String>();

	public Column() {

	}

	public Column(String columnTitle, String sortable, String propertyName) {
		this.columnTitle = columnTitle;
		this.sortable = sortable;
		this.propertyName = propertyName;
	}

	public Column(String columnTitle, String sortable, String propertyName, String sortPropertyName, String key, Map<String,String> displayParameters) {
		this.columnTitle = columnTitle;
		this.sortable = sortable;
		this.propertyName = propertyName;
		this.sortPropertyName = sortPropertyName;
		this.key = key;
		this.displayParameters = displayParameters;
	}

	public String getSortName() {
		if (!Utilities.isEmpty(this.sortPropertyName)) {
			return this.sortPropertyName;
		}
		return this.propertyName;
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
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return Returns the columnTitle.
	 */
	public String getColumnTitle() {
		return columnTitle;
	}

	/**
	 * @param columnTitle
	 *            The columnTitle to set.
	 */
	public void setColumnTitle(String columnTitle) {
		this.columnTitle = columnTitle;
	}

	/**
	 * @return Returns the propertyName.
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @param propertyName
	 *            The propertyName to set.
	 */
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * @return the sortPropertyName
	 */
	public String getSortPropertyName() {
		return sortPropertyName;
	}

	/**
	 * @param sortPropertyName the sortPropertyName to set
	 */
	public void setSortPropertyName(String sortPropertyName) {
		this.sortPropertyName = sortPropertyName;
	}

	/**
	 * @return Returns the sortable.
	 */
	public String getSortable() {
		return sortable;
	}

	/**
	 * @param sortable
	 *            The sortable to set.
	 */
	public void setSortable(String sortable) {
		this.sortable = sortable;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
}
