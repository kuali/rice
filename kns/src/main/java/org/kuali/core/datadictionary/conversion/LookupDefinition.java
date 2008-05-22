/*
 * Copyright 2005-2007 The Kuali Foundation.
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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.exception.DuplicateEntryException;

/**
 * Contains lookup-related information relating to the parent BusinessObject.
 *
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process.
 *
 *
 */
public class LookupDefinition extends DataDictionaryDefinitionBase {
    // logger
    private static Log LOG = LogFactory.getLog(LookupDefinition.class);

    private String lookupableID;
    private String title;
    private String menubar;
    private String instructions;
    private SortDefinition defaultSort;

    private List<FieldDefinition> lookupFields;
    private Map<String,FieldDefinition> lookupFieldMap;
    private List<FieldDefinition> resultFields;
    private Map<String,FieldDefinition> resultFieldMap;

    private Integer resultSetLimit;

    private String extraButtonSource;
    private String extraButtonParams;

    public LookupDefinition() {
        LOG.debug("creating new LookupDefinition");

        lookupFields = new ArrayList<FieldDefinition>();
        resultFields = new ArrayList<FieldDefinition>();
        lookupFieldMap = new LinkedHashMap<String, FieldDefinition>();
        resultFieldMap = new LinkedHashMap<String, FieldDefinition>();
    }

    /**
     * @param lookupableID
     */
    public void setLookupableID(String lookupableID) {
        if (lookupableID == null) {
            throw new IllegalArgumentException("invalid (null) lookupableID");
        }

        this.lookupableID = lookupableID;
    }

    /**
     * @return custom lookupable id
     */
    public String getLookupableID() {
        return this.lookupableID;
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title to the given value.
     *
     * @param title
     * @throws IllegalArgumentException if the given title is blank
     */
    public void setTitle(String title) {
        if (StringUtils.isBlank(title)) {
            throw new IllegalArgumentException("invalid (blank) title");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling setTitle '" + title + "'");
        }
        this.title = title;
    }

    /**
     * @return true if this instance has a menubar
     */
    public boolean hasMenubar() {
        return (menubar != null);
    }

    /**
     * @return menubar
     */
    public String getMenubar() {
        return menubar;
    }

    /**
     * Sets menubar to the given value.
     *
     * @param menubar
     * @throws IllegalArgumentException if the given menubar is blank
     */
    public void setMenubar(String menubar) {
        if (StringUtils.isBlank(menubar)) {
            throw new IllegalArgumentException("invalid (blank) menubar");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling setMenubar '" + menubar + "'");
        }
        this.menubar = menubar;
    }


    /**
     * @return true if this instance has instructions
     */
    public boolean hasInstructions() {
        return (instructions != null);
    }

    /**
     * @return instructions
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * Sets instructions to the given value.
     *
     * @param title
     * @throws IllegalArgumentException if the given instructions are blank
     */
    public void setInstructions(String instructions) {
        if (StringUtils.isBlank(instructions)) {
            throw new IllegalArgumentException("invalid (blank) instructions");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling setInstructions '" + instructions + "'");
        }
        this.instructions = instructions;
    }

    /**
     * @return true if this instance has a default sort defined
     */
    public boolean hasDefaultSort() {
        return (defaultSort != null);
    }

    /**
     * @return defaultSort
     */
    public SortDefinition getDefaultSort() {
        return defaultSort;
    }

    /**
     * Sets defaultSort to the given value.
     *
     * @param defaultSort
     * @throws IllegalArgumentException if the given defaultSort is blank
     */
    public void setDefaultSort(SortDefinition defaultSort) {
        if (defaultSort == null) {
            throw new IllegalArgumentException("invalid (null) defaultSort");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling setDefaultSort '" + defaultSort.toString() + "', " + (defaultSort.getSortAscending() ? "ascending" : "descending"));
        }
        this.defaultSort = defaultSort;
    }

    /**
     * @param lookupField
     * @throws IllegalArgumentException if the given lookupField is null
     */    
    public void addLookupField(FieldDefinition lookupField) {
        if (lookupField == null) {
            throw new IllegalArgumentException("invalid (null) lookupField");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling addLookupField for field '" + lookupField.getAttributeName() + "'");
        }
        String keyName = lookupField.getAttributeName();
        if (this.lookupFieldMap.containsKey(keyName)) {
            throw new DuplicateEntryException("duplicate lookupField entry for attribute '" + keyName + "'");
        }
        lookupFields.add(lookupField);

        this.lookupFieldMap.put(keyName, lookupField);
    }

    /**
     * @return List of attributeNames of all lookupField FieldDefinitions associated with this LookupDefinition, in the order in
     *         which they were added
     */
    public List getLookupFieldNames() {
        List fieldNames = new ArrayList();
        fieldNames.addAll(this.lookupFieldMap.keySet());

        return Collections.unmodifiableList(fieldNames);
    }

    /**
     * @return Collection of all lookupField FieldDefinitions associated with this LookupDefinition, in the order in which they were
     *         added
     */
    public List<FieldDefinition> getLookupFields() {
        return lookupFields;
    }

    /**
     * @return FieldDefinition associated with the named lookup field, or null if there is none
     * @param fieldName
     */
    public FieldDefinition getLookupField(String attributeName) {
        return lookupFieldMap.get(attributeName);
    }

    /**
     * @param resultField
     * @throws IllegalArgumentException if the given resultField is null
     */
    public void addResultField(FieldDefinition resultField) {
        if (resultField == null) {
            throw new IllegalArgumentException("invalid (null) resultField");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling addResultField for field '" + resultField.getAttributeName() + "'");
        }

        String keyName = resultField.getAttributeName();
        if (this.resultFieldMap.containsKey(keyName)) {
            throw new DuplicateEntryException("duplicate resultField entry for attribute '" + keyName + "'");
        }

        resultFields.add(resultField);
        resultFieldMap.put(keyName, resultField);
    }

    /**
     * @return List of attributeNames of all resultField FieldDefinitions associated with this LookupDefinition, in the order in
     *         which they were added
     */
    public List<String> getResultFieldNames() {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.addAll(this.resultFieldMap.keySet());

        return Collections.unmodifiableList(fieldNames);
    }

    /**
     * @return Collection of all resultField FieldDefinitions associated with this LookupDefinition, in the order in which they were
     *         added
     */
    public List<FieldDefinition> getResultFields() {
        return resultFields;
    }


    /**
     * @return FieldDefinition associated with the named result field, or null if there is none
     * @param fieldName
     */
    public FieldDefinition getResultField(String attributeName) {
        return resultFieldMap.get(attributeName);
    }

    /**
     * @param resultSetLimit the resultSetLimit to set
     */
    public void setResultSetLimit(Integer resultSetLimit) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling setResultSetLimit '" + resultSetLimit + "'");
        }
        this.resultSetLimit = resultSetLimit;
    }

    /**
     * @return true if this instance has instructions
     */
    public boolean hasResultSetLimit() {
        return (resultSetLimit != null);
    }


    /**
     * @return the resultSetLimit
     */
    public Integer getResultSetLimit() {
        return resultSetLimit;
    }
    
    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     *
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        if (hasDefaultSort()) {
            defaultSort.completeValidation(rootBusinessObjectClass, null, validationCompletionUtils);
        }

        for ( FieldDefinition lookupField : lookupFields ) {
            lookupField.completeValidation(rootBusinessObjectClass, null, validationCompletionUtils);
        }

        for ( FieldDefinition resultField : resultFields ) {
            resultField.completeValidation(rootBusinessObjectClass, null, validationCompletionUtils);
        }
    }

    /**
     * @return true if this instance has extraButtonSource
     */
    public boolean hasExtraButtonSource() {
        return extraButtonSource != null;
    }

    /**
     * @return extraButtonSource
     */
    public String getExtraButtonSource() {
        return extraButtonSource;
    }

    /**
     * Sets extraButtonParams to the given value.
     *
     * @param extraButtonParams
     * @throws IllegalArgumentException if the given source is blank
     */
    public void setExtraButtonSource(String extraButtonSource) {
        if (StringUtils.isBlank(extraButtonSource)) {
            throw new IllegalArgumentException("invalid (blank) button source");
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling setExtraButtonSource '" + extraButtonSource + "'");
        }
        this.extraButtonSource = extraButtonSource;
    }

    /**
     * @return true if this instance has extraButtonParams
     */
    public boolean hasExtraButtonParams() {
        return extraButtonParams != null;
    }

    /**
     * @return extraButtonParams
     */
    public String getExtraButtonParams() {
        return extraButtonParams;
    }

    /**
     * Sets extraButtonParams to the given value.
     *
     * @param extraButtonParams
     */
    public void setExtraButtonParams(String extraButtonParams) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("calling setExtraButtonParams '" + extraButtonParams + "'");
        }
        this.extraButtonParams = extraButtonParams;
    }

    public String toString() {
        return "LookupDefinition '" + getTitle() + "'";
    }

    public void setLookupFields(List<FieldDefinition> lookupFields) {
        this.lookupFields = lookupFields;
    }

    public void setResultFields(List<FieldDefinition> resultFields) {
        this.resultFields = resultFields;
    }
}