/*
 * Copyright 2005-2008 The Kuali Foundation
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

package org.kuali.rice.kns.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;

/**
 * A single Collection attribute definition in the DataDictionary, which contains information relating to the display, validation,
 * and general maintenance of a specific Collection attribute of an entry.
 * 
 * 
 */
public class CollectionDefinition extends DataDictionaryDefinitionBase {
    private static final long serialVersionUID = -2644072136271281041L;
    
	protected String name;
    protected String label;
    protected String shortLabel;
    protected String elementLabel;
    
    protected String summary;
    protected String description;

    public CollectionDefinition() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("invalid (blank) name");
        }
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        if (StringUtils.isBlank(label)) {
            throw new IllegalArgumentException("invalid (blank) label");
        }
        this.label = label;
    }

    /**
     * @return the shortLabel, or the label if no shortLabel has been set
     */
    public String getShortLabel() {
        return (shortLabel != null) ? shortLabel : label;
    }

    public void setShortLabel(String shortLabel) {
        if (StringUtils.isBlank(shortLabel)) {
            throw new IllegalArgumentException("invalid (blank) shortLabel");
        }
        this.shortLabel = shortLabel;
    }

    /**
     * Gets the elementLabel attribute. 
     * @return Returns the elementLabel.
     */
    public String getElementLabel() {
        return elementLabel;
    }

    /**
 The elementLabel defines the name to be used for a single object
                within the collection.  For example: "Address" may be the name
                of one object within the "Addresses" collection.
     */
    public void setElementLabel(String elementLabel) {
        this.elementLabel = elementLabel;
    }

    public String getSummary() {
        return summary;
    }

    /**
                      The summary element is used to provide a short description of the
                      attribute or collection.  This is designed to be used for help purposes.
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    /**
                      The description element is used to provide a long description of the
                      attribute or collection.  This is designed to be used for help purposes.
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     * 
     * @see org.kuali.rice.kns.datadictionary.DataDictionaryEntry#completeValidation()
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (!DataDictionary.isCollectionPropertyOf(rootBusinessObjectClass, name)) {
            throw new AttributeValidationException("property '" + name + "' is not a collection property of class '" + rootBusinessObjectClass + "' (" + "" + ")");
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "CollectionDefinition for collection " + getName();
    }
}
