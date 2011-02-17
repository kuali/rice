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

import java.math.BigDecimal;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.util.ClassLoaderUtils;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kns.datadictionary.control.ControlDefinition;
import org.kuali.rice.kns.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.kns.datadictionary.exception.ClassValidationException;
import org.kuali.rice.kns.datadictionary.validation.ValidationPattern;
import org.springframework.beans.factory.InitializingBean;


/**
 * A single attribute definition in the DataDictionary, which contains information relating to the display, validation, and general
 * maintenance of a specific attribute of an entry.
 * 
 * 
 */
public class AttributeDefinition extends DataDictionaryDefinitionBase implements InitializingBean {
    private static final long serialVersionUID = -2490613377818442742L;

	protected Boolean forceUppercase = Boolean.FALSE;

    protected String name;
    protected String label;
    protected String shortLabel;
    protected String displayLabelAttribute;

    protected Integer maxLength;
    protected Boolean unique;
    
    protected BigDecimal exclusiveMin;
    protected BigDecimal inclusiveMax;

    protected ValidationPattern validationPattern;
    protected Boolean required = Boolean.FALSE;

    protected ControlDefinition control;

    protected String summary;
    protected String description;

    protected String formatterClass;

    protected AttributeSecurity attributeSecurity;

    public AttributeDefinition() {}


    /**
        * forceUppercase = convert user entry to uppercase and always display
            database value as uppercase.
     */
    public void setForceUppercase(Boolean forceUppercase) {
        this.forceUppercase = forceUppercase;
    }

    public Boolean getForceUppercase() {
        return this.forceUppercase;
    }

    public String getName() {
        return name;
    }

    /*
     * name = name of attribute
     */
    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("invalid (blank) name");
        }
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    /**
                    The label element is the field or collection name that will be shown on inquiry and
                    maintenance screens.
                    This will be overridden by presence of displayLabelAttribute element.
     */
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
        return (shortLabel != null) ? shortLabel : getLabel();
    }

    /**
     * @return the shortLabel directly, without substituting in the label
     */
    protected String getDirectShortLabel() {
        return shortLabel;
    }

    /**
                    The shortLabel element is the field or collection name that will be used
                    in applications when a shorter name (than the label element) is required.
                    This will be overridden by presence of displayLabelAttribute element.
     */
    public void setShortLabel(String shortLabel) {
        if (StringUtils.isBlank(shortLabel)) {
            throw new IllegalArgumentException("invalid (blank) shortLabel");
        }
        this.shortLabel = shortLabel;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    /**
                    The maxLength element determines the maximum size of the field
                    for data entry edit purposes and for display purposes.
     */
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public BigDecimal getExclusiveMin() {
        return exclusiveMin;
    }

    /**
                    The exclusiveMin element determines the minimum allowable value
                    for data entry editing purposes.  Value can be an integer or decimal
                    value such as -.001 or 99.
     */
    public void setExclusiveMin(BigDecimal exclusiveMin) {
        this.exclusiveMin = exclusiveMin;
    }

    /**
                    The inclusiveMax element determines the maximum allowable value
                    for data entry editing purposes. Value can be an integer or decimal
                    value such as -.001 or 99.

                    JSTL: This field is mapped into the field named "exclusiveMax".
     */
    public BigDecimal getInclusiveMax() {
        return inclusiveMax;
    }

    /**
                    The inclusiveMax element determines the maximum allowable value
                    for data entry editing purposes. Value can be an integer or decimal
                    value such as -.001 or 99.

                    JSTL: This field is mapped into the field named "exclusiveMax".
     */
    public void setInclusiveMax(BigDecimal inclusiveMax) {
        this.inclusiveMax = inclusiveMax;
    }

    /**
     * @return true if a validationPattern has been set
     */
    public boolean hasValidationPattern() {
        return (validationPattern != null);
    }

    public ValidationPattern getValidationPattern() {
        return this.validationPattern;
    }

    /**
                    The validationPattern element defines the allowable character-level
                    or field-level values for an attribute.

                    JSTL: validationPattern is a Map which is accessed using a key
                    of "validationPattern". Each entry may contain some of the keys
                    listed below.  The keys that may be present for a given attribute
                    are dependent upon the type of validationPattern.

                        * maxLength (String)
                        * exactLength
                        * type
                        * allowWhitespace
                        * allowUnderscore
                        * allowPeriod
                        * validChars
                        * precision
                        * scale
                        * allowNegative

                    The allowable keys (in addition to type) for each type are:
                        ****Type****    ***Keys***
                        alphanumeric    exactLength
                                        maxLength
                                        allowWhitespace
                                        allowUnderscore
                                        allowPeriod

                        alpha           exactLength
                                        maxLength
                                        allowWhitespace

                        anyCharacter    exactLength
                                        maxLength
                                        allowWhitespace

                        charset         validChars

                        numeric         exactLength
                                        maxLength

                        fixedPoint      allowNegative
                                        precision
                                        scale

                        floatingPoint   allowNegative

                        date            n/a
                        emailAddress    n/a
                        javaClass       n/a
                        month           n/a
                        phoneNumber     n/a
                        timestamp       n/a
                        year            n/a
                        zipcode         n/a

                    Note: maxLength and exactLength are mutually exclusive.
                    If one is entered, the other may not be entered.

                    Note:  See ApplicationResources.properties for
                    exact regex patterns.
                    e.g. validationPatternRegex.date for regex used in date validation.
     */
    public void setValidationPattern(ValidationPattern validationPattern) {
        this.validationPattern = validationPattern;
    }


    /**
                    The required element allows values of "true" or "false".
                    A value of "true" indicates that a value must be entered for this
                    business object when creating or editing a new business object.
     */
    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean isRequired() {
        return this.required;
    }


    /**
     * @return control
     */
    public ControlDefinition getControl() {
        return control;
    }

    /**
     *                     The control element defines the manner in which an attribute is
                    displayed and the manner in which the attribute value is entered.

                    JSTL: control is a Map representing an HTML control.  It is accessed
                    using a key of "control".  The table below shows the types of entries
                    associated with each type of control.

                    **Control Type**    **Key**             **Value**
                    checkbox            checkbox            boolean String

                    hidden              hidden              boolean String

                    radio               radio               boolean String
                                        valuesFinder        valuesFinder class name
                                        businessObjectClass String
                                        keyAttribute        String
                                        labelAttribute      String
                                        includeKeyInLabel   boolean String

                    select              select              boolean String
                                        valuesFinder        valuesFinder class name
                                        businessObjectClass String
                                        keyAttribute        String
                                        labelAttribute      String
                                        includeBlankRow		boolean String
                                        includeKeyInLabel   boolean String

                    text                text                boolean String
                                        size                String

                    textarea            textarea            boolean String
                                        rows
                                        cols

                    currency            currency            boolean String
                                        size                String
                                        formattedMaxLength  String

                    kualiUser           kualiUser           boolean String
                                        universalIdAttributeName    String
                                        userIdAttributeName         String
                                        personNameAttributeName     String

                    lookupHidden        lookupHidden        boolean String

                    lookupReadonly      lookupReadonly      boolean String

     * @param control
     * @throws IllegalArgumentException if the given control is null
     */
    public void setControl(ControlDefinition control) {
        if (control == null) {
            throw new IllegalArgumentException("invalid (null) control");
        }
        this.control = control;
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
     *                       The description element is used to provide a long description of the
                      attribute or collection.  This is designed to be used for help purposes.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean hasFormatterClass() {
        return (formatterClass != null);
    }

    public String getFormatterClass() {
        return formatterClass;
    }

    /**
                      The formatterClass element is used when custom formatting is
                      required for display of the field value.  This field specifies
                      the name of the java class to be used for the formatting.  About
                      15 different classes are available including BooleanFormatter,
                      CurrencyFormatter, DateFormatter, etc.
     */
    public void setFormatterClass(String formatterClass) {
        if (formatterClass == null) {
            throw new IllegalArgumentException("invalid (null) formatterClass");
        }
        this.formatterClass = formatterClass;
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     * 
     * @see org.kuali.rice.kns.datadictionary.DataDictionaryEntry#completeValidation()
     */
    public void completeValidation(Class rootObjectClass, Class otherObjectClass) {
        try {
            if (!DataDictionary.isPropertyOf(rootObjectClass, getName())) {
                throw new AttributeValidationException("property '" + getName() + "' is not a property of class '" + rootObjectClass.getName() + "' (" + "" + ")");
            }
    
            if ( getControl() == null ) {
                throw new AttributeValidationException( "property '" + getName() + "' in class '" + rootObjectClass.getName() + " does not have a control defined" );
            }
            
            getControl().completeValidation(rootObjectClass, otherObjectClass);    
    
            if(attributeSecurity != null){
            	attributeSecurity.completeValidation(rootObjectClass, otherObjectClass);
            }
            
            if (validationPattern != null) {
            	validationPattern.completeValidation();
            }
            
            if (formatterClass != null) {
            	try {
            		Class formatterClassObject = ClassUtils.getClass(ClassLoaderUtils.getDefaultClassLoader(), getFormatterClass());
            		if (!Formatter.class.isAssignableFrom(formatterClassObject)) {
            			throw new ClassValidationException("formatterClass is not a valid instance of " + Formatter.class.getName() + " instead was: " + formatterClassObject.getName());
            		}
            	} catch (ClassNotFoundException e) {
            		throw new ClassValidationException("formatterClass could not be found: " + getFormatterClass(), e);
            	}
            }
        } catch ( RuntimeException ex ) {
            Logger.getLogger(getClass()).error("Unable to validate attribute " + rootObjectClass + "." + getName() + ": " + ex.getMessage(), ex );
            throw ex;
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AttributeDefinition for attribute " + getName();
    }


    public String getDisplayLabelAttribute() {
        return displayLabelAttribute;
    }


    /**
                    The displayLabelAttribute element is used to indicate that the
                    label and short label should be obtained from another attribute.

                    The label element and short label element defined for this attribute
                    will be overridden.  Instead, the label and short label values
                    will be obtained by referencing the corresponding values from the
                    attribute indicated by this element.
     */
    public void setDisplayLabelAttribute(String displayLabelAttribute) {
        this.displayLabelAttribute = displayLabelAttribute;
    }


	/**
	 * @return the attributeSecurity
	 */
	public AttributeSecurity getAttributeSecurity() {
		return this.attributeSecurity;
	}


	/**
	 * @param attributeSecurity the attributeSecurity to set
	 */
	public void setAttributeSecurity(AttributeSecurity attributeSecurity) {
		this.attributeSecurity = attributeSecurity;
	}
	
	 public boolean hasAttributeSecurity() {
	        return (attributeSecurity != null);
    }
	 
	 
    /**
     * This overridden method ...
     * 
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
    	if ( StringUtils.isEmpty(name) ) {
    		throw new RuntimeException( "blank name for bean: " + id );
    	}
    	
    }


	/**
	 * @return the unique
	 */
	public Boolean getUnique() {
		return this.unique;
	}


	/**
	 * @param unique the unique to set
	 */
	public void setUnique(Boolean unique) {
		this.unique = unique;
	}
}
