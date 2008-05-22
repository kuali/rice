/*
 * Copyright 2005-2006 The Kuali Foundation.
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

package org.kuali.core.datadictionary.conversion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.exception.AttributeValidationException;

/**
 * Contains sorting-related information for DataDictionary entries.
 * 
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 * 
 * 
 */
public class SortDefinition extends DataDictionaryDefinitionBase {
    // logger
    private static Log LOG = LogFactory.getLog(SortDefinition.class);

    private boolean sortAscending = true;
    private List<String> attributeNames = new ArrayList<String>();

    public SortDefinition() {
        LOG.debug("creating new SortDefinition");
    }


    /**
     * Sets attributeName to the given value.
     * 
     * @param attributeName
     * @throws IllegalArgumentException if the given attributeName is blank
     */
    public void setAttributeName(String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }
        if (attributeNames.size() != 0) {
            throw new IllegalStateException("unable to set sort attributeName when sortAttributes have already been added");
        }

        attributeNames.add(attributeName);
    }

    /**
     * Adds the given attribute to the list of attributes associated with this sortDefinition.
     * 
     * @param sortAttributeDefinition
     */
    @Deprecated
    public void addSortAttribute(String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }
        this.attributeNames.add(attributeName);
    }


    /**
     * @return the List of associated attribute names as Strings
     */
    public List<String> getAttributeNames() {
        return this.attributeNames;
    }


    /**
     * @return true if items should sort in ascending order
     */
    public boolean getSortAscending() {
        return sortAscending;
    }

    /**
     * Sets sortAscending to the given value
     * 
     * @param sortAscending
     */
    public void setSortAscending(boolean sortAscending) {
        this.sortAscending = sortAscending;
    }


    /**
     * Directly validate simple fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        for ( String attributeName : attributeNames ) {
            if (!validationCompletionUtils.isPropertyOf(rootBusinessObjectClass, attributeName)) {
                throw new AttributeValidationException("unable to find sort attribute '" + attributeName + "' in rootBusinessObjectClass '" + rootBusinessObjectClass.getName() + "' (" + getParseLocation() + ")");
            }            
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer attrList = new StringBuffer("[");
        for (Iterator<String> i = attributeNames.iterator(); i.hasNext();) {
            attrList.append(i.next());
            if (i.hasNext()) {
                attrList.append(",");
            }
        }
        attrList.append("]");

        return "SortDefinition for attribute " + attrList.toString();
    }


    public void setAttributeNames(List<String> attributeNames) {
        this.attributeNames = attributeNames;
    }
    
}