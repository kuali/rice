/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.web.ui;

import java.util.Comparator;

import org.kuali.core.web.format.Formatter;

/**
 * This class represents a column in a result table.
 * 
 * 
 */

public class Column implements java.io.Serializable {
    private static final long serialVersionUID = -5916942413570667803L;
    private String columnTitle;
    private String sortable = "true";
    private String propertyName;
    private String propertyValue;
    private String propertyURL;
    private Formatter formatter;
    private Comparator comparator;
    
    /**
     * A comparator used to compare the propertyValue values
     */
    private Comparator valueComparator;
    
    /**
     * Represents the maximum column length.  If propertyValue's length exceeds this value, then 
     * it will be truncated to this length when displayed
     */
    private int maxLength;
    
    public Column() {
    }

    public Column(String columnTitle, String sortable, String propertyName) {
        this.columnTitle = columnTitle;
        this.sortable = sortable;
        this.propertyName = propertyName;
    }

    public Column(String columnTitle, String sortable, String propertyName, Comparator comparator) {
        this(columnTitle, sortable, propertyName);
        this.comparator = comparator;
    }

    public Column(String columnTitle, String propertyName, Formatter formatter) {
        this.columnTitle = columnTitle;
        this.propertyName = propertyName;
        this.formatter = formatter;
    }

    /**
     * @return Returns the comparator.
     */
    public Comparator getComparator() {
        return comparator;
    }


    /**
     * @param comparator The comparator to set.
     */
    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }


    /**
     * @return Returns the columnTitle.
     */
    public String getColumnTitle() {
        return columnTitle;
    }


    /**
     * @param columnTitle The columnTitle to set.
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
     * @param propertyName The propertyName to set.
     */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }


    /**
     * @return Returns the sortable.
     */
    public String getSortable() {
        return sortable;
    }


    /**
     * @param sortable The sortable to set.
     */
    public void setSortable(String sortable) {
        this.sortable = sortable;
    }


    /**
     * @return Returns the propertyURL.
     */
    public String getPropertyURL() {
        return propertyURL;
    }


    /**
     * @param propertyURL The propertyURL to set.
     */
    public void setPropertyURL(String propertyURL) {
        this.propertyURL = propertyURL;
    }


    /**
     * @return Returns the propertyValue.
     */
    public String getPropertyValue() {
        return propertyValue;
    }


    /**
     * @param propertyValue The propertyValue to set.
     */
    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }


    /**
     * @return Returns the formatter.
     */
    public Formatter getFormatter() {
        return formatter;
    }


    /**
     * @param formatter The formatter to set.
     */
    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    public Comparator getValueComparator() {
        return valueComparator;
    }

    public void setValueComparator(Comparator valueComparator) {
        this.valueComparator = valueComparator;
    }

    /**
     * Returns the maximum column length.  If propertyValue's length exceeds this value, then 
     * it will be truncated to this length when displayed
     * @return
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Sets the maximum column length.  If propertyValue's length exceeds this value, then 
     * it will be truncated to this length when displayed
     * @param maxColumnLength
     */
    public void setMaxLength(int maxColumnLength) {
        this.maxLength = maxColumnLength;
    }
}