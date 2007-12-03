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
package edu.iu.uis.eden.docsearch.web;

import java.io.Serializable;

/**
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class SearchAttributeFormContainer implements Serializable {
    private static final long serialVersionUID = 8034659910798901330L;
    
    private String key;
    private String value;
    private String[] values;
    private String alternateValue;
    private boolean valueSet = false;

    public SearchAttributeFormContainer() {
    }

    public SearchAttributeFormContainer(String key, String value) {
        this.key = key;
        this.value = value;
        valueSet = true;
    }

    public SearchAttributeFormContainer(String key, String[] values) {
        this.key = key;
        this.values = values;
    }

    public SearchAttributeFormContainer(String key, String value, String alternateValue) {
        this.key = key;
        this.value = value;
        valueSet = true;
        this.alternateValue = alternateValue;
    }

    
    public String getAlternateValue() {
        return alternateValue;
    }

    public void setAlternateValue(String alternateValue) {
        this.alternateValue = alternateValue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        valueSet = true;
        this.value = value;
    }

    /**
     * This should only be called for UI editable implementations
     * @deprecated
     */
    public String getValueForUserInterface() {
        valueSet = false;
        return value;
    }

    /**
     * This should only be called for UI editable implementations
     * @deprecated
     */
    public void setValueForUserInterface(String value) {
        valueSet = true;
        this.value = value;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public boolean isValueSet() {
        return valueSet;
    }

}