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

package org.kuali.core.datadictionary.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.core.datadictionary.ValidationCompletionUtils;
import org.kuali.core.datadictionary.exception.CompletionException;

/**
 * A single HTML control definition in the DataDictionary, which contains information relating to the HTML control used to realize a
 * specific attribute. All types of controls are represented by an instance of this class; you have to call one of the is* methods
 * to figure out which of the other accessors should return useful values.
 * 
 * 
 */
public abstract class ControlDefinitionBase extends DataDictionaryDefinitionBase implements ControlDefinition {

    // logger
    private static Log LOG = LogFactory.getLog(ControlDefinitionBase.class);

    private boolean datePicker;
    private String script;
    private Class valuesFinderClass;
    private Integer size;
    private Integer rows;
    private Integer cols;


    public ControlDefinitionBase() {
        LOG.debug("creating new ControlDefinition");
    }
    
    public boolean isDatePicker() {
        return datePicker;
    }

    public void setDatePicker(boolean datePicker) {
        this.datePicker=datePicker; 
    }


    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#isCheckbox()
     */
    public boolean isCheckbox() {
        return false;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#isHidden()
     */
    public boolean isHidden() {
        return false;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#isRadio()
     */
    public boolean isRadio() {
        return false;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#isSelect()
     */
    public boolean isSelect() {
        return false;
    }

    /**
     * 
     * @see org.kuali.core.datadictionary.control.ControlDefinition#isApcSelect()
     */

    public boolean isApcSelect() {
        return false;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#isText()
     */
    public boolean isText() {
        return false;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#isTextarea()
     */
    public boolean isTextarea() {
        return false;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#isCurrency()
     */
    public boolean isCurrency() {
        return false;
    }

    /**
     * 
     * @see org.kuali.core.datadictionary.control.ControlDefinition#isKualiUser()
     */
    public boolean isKualiUser() {
        return false;
    }
    
    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#isWorkgroup()
     */
    public boolean isWorkflowWorkgroup() {
        return false;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#isLookupHidden()
     */
    public boolean isLookupHidden() {
        return false;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#isLookupReadonly()
     */
    public boolean isLookupReadonly() {
        return false;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#setKeyValuesFinder(java.lang.String)
     */
    public void setValuesFinderClass(Class valuesFinderClass) {
        if (valuesFinderClass == null) {
            throw new IllegalArgumentException("invalid (null) valuesFinderClass");
        }
        LOG.debug("calling setValuesKey '" + valuesFinderClass.getName() + "'");

        this.valuesFinderClass = valuesFinderClass;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#getKeyValuesFinder()
     */
    public Class getValuesFinderClass() {
        return valuesFinderClass;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#setSize(int)
     */
    public void setSize(Integer size) {
        LOG.debug("calling setSize '" + size + "'");

        this.size = size;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#getSize()
     */
    public Integer getSize() {
        return size;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#hasScript()
     */
    public boolean hasScript() {
        return false;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#setRows(int)
     */
    public void setRows(Integer rows) {
        LOG.debug("calling setRows '" + rows + "'");

        this.rows = rows;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#getRows()
     */
    public Integer getRows() {
        return rows;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#setCols(int)
     */
    public void setCols(Integer cols) {
        LOG.debug("calling setCols '" + cols + "'");

        this.cols = cols;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#getCols()
     */
    public Integer getCols() {
        return cols;
    }

    /**
     * Directly validate simple fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        if (!isCheckbox() && !isHidden() && !isRadio() && !isSelect() && !isApcSelect() && !isText() && !isTextarea() && !isCurrency() && !isKualiUser() && !isLookupHidden() && !isLookupReadonly() && !isWorkflowWorkgroup()) {
            throw new CompletionException("error validating " + rootBusinessObjectClass.getName() + " control: unknown control type in control definition (" + getParseLocation() + ")");
        }
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#getScript()
     */
    public String getScript() {
        return script;
    }

    /**
     * @see org.kuali.core.datadictionary.control.ControlDefinition#setScript()
     */

    public void setScript(String script) {
        this.script = script;
    }
}