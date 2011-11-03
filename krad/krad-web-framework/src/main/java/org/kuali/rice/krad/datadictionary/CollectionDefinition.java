/**
 * Copyright 2005-2011 The Kuali Foundation
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
package org.kuali.rice.krad.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.krad.datadictionary.validation.capability.CollectionSizeConstrainable;

/**
 * A single Collection attribute definition in the DataDictionary, which contains information relating to the display, validation,
 * and general maintenance of a specific Collection attribute of an entry.
 * 
 * 
 */
public class CollectionDefinition extends DataDictionaryDefinitionBase implements CollectionSizeConstrainable{
    private static final long serialVersionUID = -2644072136271281041L;
    
    protected String dataObjectClass;
    protected String name;
    protected String label;
    protected String shortLabel;
    protected String elementLabel;
    
    protected String summary;

	protected String description;
    
	protected Integer minOccurs;
	protected Integer maxOccurs;

    public CollectionDefinition() {
    	//empty
    }

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
 	 * The elementLabel defines the name to be used for a single object
     * within the collection.  For example: "Address" may be the name
     * of one object within the "Addresses" collection.
     */
    public void setElementLabel(String elementLabel) {
        this.elementLabel = elementLabel;
    }

    public String getSummary() {
        return summary;
    }

    /**
	 * The summary element is used to provide a short description of the
     * attribute or collection.  This is designed to be used for help purposes.
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    /**
	 * The description element is used to provide a long description of the
	 * attribute or collection.  This is designed to be used for help purposes.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
           
    /**
	 * @return the dataObjectClass
	 */
	public String getDataObjectClass() {
		return this.dataObjectClass;
	}

	/**
	 * @param objectClass the dataObjectClass to set
	 */
	public void setDataObjectClass(String dataObjectClass) {
		this.dataObjectClass = dataObjectClass;
	}

	/**
     * Directly validate simple fields, call completeValidation on Definition fields.
     * 
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryEntry#completeValidation()
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (!DataDictionary.isCollectionPropertyOf(rootBusinessObjectClass, name)) {
            throw new AttributeValidationException("property '" + name + "' is not a collection property of class '" + rootBusinessObjectClass + "' (" + "" + ")");
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CollectionDefinition for collection " + getName();
    }

	/**
	 * @see org.kuali.rice.krad.datadictionary.validation.constraint.CollectionSizeConstraint#getMaximumNumberOfElements()
	 */
	@Override
	public Integer getMaximumNumberOfElements() {
		return this.maxOccurs;
	}

	/**
	 * @see org.kuali.rice.krad.datadictionary.validation.constraint.CollectionSizeConstraint#getMinimumNumberOfElements()
	 */
	@Override
	public Integer getMinimumNumberOfElements() {
		return this.minOccurs;
	}

    /**
	 * @return the minOccurs
	 */
	public Integer getMinOccurs() {
		return this.minOccurs;
	}

	/**
	 * @param minOccurs the minOccurs to set
	 */
	public void setMinOccurs(Integer minOccurs) {
		this.minOccurs = minOccurs;
	}

	/**
	 * @return the maxOccurs
	 */
	public Integer getMaxOccurs() {
		return this.maxOccurs;
	}

	/**
	 * @param maxOccurs the maxOccurs to set
	 */
	public void setMaxOccurs(Integer maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

}
