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

package org.kuali.core.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.exception.AttributeValidationException;

/**
 * A single Collection attribute definition in the DataDictionary, which contains information relating to the display, validation,
 * and general maintenance of a specific Collection attribute of an entry.
 * 
 * 
 */
public class CollectionDefinition extends DataDictionaryDefinitionBase {
    // logger
    private static Log LOG = LogFactory.getLog(CollectionDefinition.class);

    private String name;
    private String label;
    private String shortLabel;
    private String elementLabel;
    
    private String summary;
    private String description;

    public CollectionDefinition() {
        LOG.debug("creating new CollectionDefinition");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("invalid (blank) name");
        }
        LOG.debug("calling setName '" + name + "'");

        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        if (StringUtils.isBlank(label)) {
            throw new IllegalArgumentException("invalid (blank) label");
        }
        LOG.debug("calling setLabel '" + label + "'");

        this.label = label;
    }

    /**
     * @return the shortLabel, or the label if no shortLabel has been set
     */
    public String getShortLabel() {
        return (shortLabel != null) ? shortLabel : getLabel();
    }

    public void setShortLabel(String shortLabel) {
        if (StringUtils.isBlank(shortLabel)) {
            throw new IllegalArgumentException("invalid (blank) shortLabel");
        }
        LOG.debug("calling setShortLabel '" + shortLabel + "'");

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
     * Sets the elementLabel attribute value.
     * @param elementLabel The elementLabel to set.
     */
    public void setElementLabel(String elementLabel) {
        this.elementLabel = elementLabel;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        if (StringUtils.isBlank(summary)) {
            throw new IllegalArgumentException("invalid (blank) summary");
        }
        LOG.debug("calling setSummary '" + summary + "'");

        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (StringUtils.isBlank(description)) {
            throw new IllegalArgumentException("invalid (blank) description");
        }
        LOG.debug("calling setDescription '" + description + "'");

        this.description = description;
    }


    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryEntry#completeValidation()
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {

        if (!validationCompletionUtils.isCollectionPropertyOf(rootBusinessObjectClass, name)) {
            throw new AttributeValidationException("property '" + name + "' is not a collection property of class '" + rootBusinessObjectClass + "' (" + getParseLocation() + ")");
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "CollectionDefinition for collection " + getName();
    }
}