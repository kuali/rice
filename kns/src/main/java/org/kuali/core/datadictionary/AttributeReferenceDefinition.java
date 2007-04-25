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
import org.kuali.core.datadictionary.control.ControlDefinition;
import org.kuali.core.datadictionary.exception.AttributeValidationException;
import org.kuali.core.datadictionary.exception.ClassValidationException;
import org.kuali.core.datadictionary.exception.CompletionException;
import org.kuali.core.datadictionary.exception.ReferenceValidationException;
import org.kuali.core.datadictionary.mask.Mask;
import org.kuali.core.datadictionary.validation.ValidationPattern;
import org.kuali.core.web.format.Formatter;


/**
 * A single attribute definition in the DataDictionary, which contains information relating to the display, validation, and general
 * maintenance of a specific attribute of an entry.
 * 
 * 
 */
public class AttributeReferenceDefinition extends AttributeDefinition {
    // logger
    private static Log LOG = LogFactory.getLog(AttributeReferenceDefinition.class);


    private String sourceClassName;
    private String sourceAttributeName;
    private AttributeDefinition delegate;

    /**
     * Constructs an AttributeReferenceDefinition
     */
    public AttributeReferenceDefinition() {
        LOG.debug("creating new AttributeReferenceDefinition");
    }

    public void setSourceClassName(String sourceClassName) {
        if (StringUtils.isBlank(sourceClassName)) {
            throw new IllegalArgumentException("invalid (blank) sourceClassName");
        }

        this.sourceClassName = sourceClassName;
    }

    public String getSourceClassName() {
        return this.sourceClassName;
    }

    public void setSourceAttributeName(String sourceAttributeName) {
        if (StringUtils.isBlank(sourceAttributeName)) {
            throw new IllegalArgumentException("invalid (blank) sourceAttributeName");
        }

        this.sourceAttributeName = sourceAttributeName;
    }

    public String getSourceAttributeName() {
        return this.sourceAttributeName;
    }


    /**
     * @return AttributeDefinition acting as delegate for this AttributeReferenceDefinition
     */
    AttributeDefinition getDelegate() {
        if (delegate == null) {
            throw new IllegalStateException("unable to retrieve null delegate");
        }

        return delegate;
    }

    /**
     * Sets the given AttributeDefinition as the delegate for this instance
     * 
     * @param delegate
     */
    void setDelegate(AttributeDefinition delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("invalid (null) delegate");
        }

        this.delegate = delegate;
    }


    /**
     * If forceUppercase wasn't set on this instance, use the value from its delegate.
     * 
     * @see org.kuali.core.datadictionary.AttributeDefinition#getForceUppercase()
     */
    public Boolean getForceUppercase() {
        Boolean value = super.getForceUppercase();
        if (value == null) {
            value = getDelegate().getForceUppercase();
        }

        return value;
    }

    /**
     * If name wasn't set on this instance, use the value from its delegate.
     * 
     * @see org.kuali.core.datadictionary.AttributeDefinition#getName()
     */
    public String getName() {
        String name = super.getName();
        if (name == null) {
            name = getDelegate().getName();
        }

        return name;
    }

    /**
     * If label wasn't set on this instance, use the value from its delegate.
     * 
     * @see org.kuali.core.datadictionary.AttributeDefinition#getLabel()
     */
    public String getLabel() {
        String label = super.getLabel();

        if (label == null) {
            label = getDelegate().getLabel();
        }

        return label;
    }


    /**
     * If shortlabel wasn't set on this instance, use the value from its delegate.
     * 
     * @see org.kuali.core.datadictionary.AttributeDefinition#getShortLabel()
     */
    public String getShortLabel() {
        String shortLabel = super.getDirectShortLabel();
        if (shortLabel == null) {
            shortLabel = getDelegate().getShortLabel();
        }

        return shortLabel;
    }

    /**
     * If maxLength wasn't set on this instance, use the value from its delegate.
     * 
     * @see org.kuali.core.datadictionary.AttributeDefinition#getMaxLength()
     */
    public Integer getMaxLength() {
        Integer maxLength = super.getMaxLength();
        if (maxLength == null) {
            maxLength = getDelegate().getMaxLength();
        }

        return maxLength;
    }


    /**
     * @return true if a validationPattern is available, directly or indirectly
     * 
     * @see org.kuali.core.datadictionary.AttributeDefinition#hasValidationPattern()
     */
    public boolean hasValidationPattern() {
        return (getValidationPattern() != null);
    }

    /**
     * If validationPattern wasn't set on this instance, use the value from its delegate.
     * 
     * @see org.kuali.core.datadictionary.AttributeDefinition#getValidationPattern()
     */
    public ValidationPattern getValidationPattern() {
        ValidationPattern validationPattern = super.getValidationPattern();
        if (validationPattern == null) {
            validationPattern = getDelegate().getValidationPattern();
        }

        return validationPattern;
    }

    /**
     * If required wasn't set on this instance, use the value from its delegate.
     * 
     * @see org.kuali.core.datadictionary.AttributeDefinition#isRequired()
     */
    public Boolean isRequired() {
        Boolean required = super.isRequired();
        if (required == null) {
            required = getDelegate().isRequired();
        }

        return required;
    }

    /**
     * If control wasn't set on this instance, use the value from its delegate.
     * 
     * @see org.kuali.core.datadictionary.AttributeDefinition#getControl()
     */
    public ControlDefinition getControl() {
        ControlDefinition control = super.getControl();
        if (control == null) {
            control = getDelegate().getControl();
        }

        return control;
    }


    /**
     * If summary wasn't set on this instance, use the value from its delegate.
     * 
     * @see org.kuali.core.datadictionary.AttributeDefinition#getSummary()
     */
    public String getSummary() {
        String summary = super.getSummary();
        if (summary == null) {
            summary = getDelegate().getSummary();
        }

        return summary;
    }

    /**
     * If description wasn't set on this instance, use the value from its delegate.
     * 
     * @see org.kuali.core.datadictionary.AttributeDefinition#getDescription()
     */
    public String getDescription() {
        String description = super.getDescription();
        if (description == null) {
            description = getDelegate().getDescription();
        }

        return description;
    }


    /**
     * @return true if a formatterClass is available, directly or indirectly
     * 
     * @see org.kuali.core.datadictionary.AttributeDefinition#hasFormatterClass()
     */
    public boolean hasFormatterClass() {
        return (getFormatterClass() != null);
    }

    /**
     * If a formatterClass wasn't set for this instance, use the value from its delegate.
     * 
     * @see org.kuali.core.datadictionary.AttributeDefinition#getFormatterClass()
     */
    public Class getFormatterClass() {
        Class formatterClass = super.getFormatterClass();
        if (formatterClass == null) {
            formatterClass = getDelegate().getFormatterClass();
        }

        return formatterClass;
    }
    

    /**
     * @see org.kuali.core.datadictionary.AttributeDefinition#getDisplayMask()
     */
    @Override
    public Mask getDisplayMask() {
        Mask displayMask = super.getDisplayMask();
        if (displayMask == null){
            displayMask = getDelegate().getDisplayMask();
        }
        return displayMask;
    }

    /**
     * @see org.kuali.core.datadictionary.AttributeDefinition#getDisplayWorkgroup()
     */
    @Override
    public String getDisplayWorkgroup() {
        String displayWorkgroup = super.getDisplayWorkgroup();
        if (StringUtils.isBlank(displayWorkgroup)){
            displayWorkgroup = getDelegate().getDisplayWorkgroup();
        }
        return displayWorkgroup;
    }

    /**
     * @see org.kuali.core.datadictionary.AttributeDefinition#hasDisplayMask()
     */
    @Override
    public boolean hasDisplayMask() {
        return (super.getDisplayMask() != null || getDelegate().getDisplayMask() != null);
    }

    
    /**
     * @see org.kuali.core.datadictionary.AttributeDefinition#getDisplayLabelAttribute()
     */
    @Override
    public String getDisplayLabelAttribute() {
        String displayLabelAttribute = super.getDisplayLabelAttribute();
        if (StringUtils.isBlank(displayLabelAttribute)){
            displayLabelAttribute = getDelegate().getDisplayLabelAttribute();
        }
        return displayLabelAttribute;
    }

    /**
     * Validate the fields associated with locating the delegate. Other validation must be deferred until the delegate class has
     * been assigned.
     * 
     * @see org.kuali.core.datadictionary.DataDictionaryEntry#completeValidation()
     */
    public void completeValidation(Class rootObjectClass, Class otherObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        if (StringUtils.isBlank(sourceClassName)) {
            throw new IllegalArgumentException("invalid (blank) sourceClassName for attribute '" + rootObjectClass.getName() + "." + getName() + "'");
        }
        if (StringUtils.isBlank(sourceAttributeName)) {
            throw new IllegalArgumentException("invalid (blank) sourceAttributeName for attribute '" + rootObjectClass.getName() + "." + getName() + "'");
        }

        // defer the rest of the validation until assignDelegate gets called, since you (may) need to DataDictionary
        // to be completely constructed before you can verify the existence of sourceClass and sourceAttribute
    }


    /**
     * Use instance parameters to locate and assign an existing AttributeDefinition as delegate
     * 
     * @param dataDictionary
     * @throws CompletionException if unable to find class and attribute matching sourceClassName and sourceAttributeName
     */
    public void assignDelegate(DataDictionaryEntryBase parentObjectEntry, DataDictionary dataDictionary) {
        String parentClassName = StringUtils.substringAfter(parentObjectEntry.getEntryClass().getName(), parentObjectEntry.getEntryClass().getPackage().getName() + ".");
        String msgPrefix = "error validating " + parentClassName + "." + this.getName() + ": ";
        String msgSuffix = " (" + getParseLocation() + ")";

        BusinessObjectEntry delegateEntry = dataDictionary.getBusinessObjectEntry(getSourceClassName());
        if (delegateEntry == null) {
            throw new CompletionException(msgPrefix + "no BusinessObjectEntry exists for sourceClassName '" + getSourceClassName() + "'" + msgSuffix);
        }
        AttributeDefinition delegateDefinition = delegateEntry.getAttributeDefinition(getSourceAttributeName());
        if (delegateDefinition == null) {
            throw new CompletionException(msgPrefix + "no AttributeDefnintion exists for sourceAttributeName '" + getSourceClassName() + "." + getSourceAttributeName() + "'" + msgSuffix);
            // This error could be caused by AttributeReferenceDummy.xml.
        }
        
        if (delegateDefinition instanceof AttributeReferenceDefinition) {
            ((AttributeReferenceDefinition) delegateDefinition).assignDelegate(parentObjectEntry, dataDictionary);
        }

        setDelegate(delegateDefinition);
    }

    /**
     * Complete deferred validation
     * 
     * @param rootBusinessObjectClass
     */
    public void completeDeferredValidation(Class rootBusinessObjectClass, ValidationCompletionUtils validationCompletionUtils) {
        // the above throws a CompletionException if delegate is null
        // (ain't side-effects grand)
        getDelegate();

        // name can't be delegated, so validate it directly
        // (where "directly" means "by getting it from the superclass")
        String name = super.getName();
        
        if (!validationCompletionUtils.isPropertyOf(rootBusinessObjectClass, name)) {
            throw new AttributeValidationException("property '" + name + "' is not a property of class '" + rootBusinessObjectClass.getName() + "' (" + getParseLocation() + ")");
        }

        boolean isDelegate = false;

        Class formatterClass = null;
        if (super.hasFormatterClass()) {
            isDelegate = false;
            formatterClass = super.getFormatterClass();
        }
        else if (hasFormatterClass()) {
            isDelegate = true;
            formatterClass = getFormatterClass();
        }
        if (formatterClass != null) {
            if (!Formatter.class.isAssignableFrom(formatterClass)) {
                String source = isDelegate ? "delegated" : "overridden";
                throw new ClassValidationException(source + " formatterClass '" + formatterClass.getName() + "' for attribute '" + name + "' is not a subclass of pojo.format.Formatter (" + getParseLocation() + ")");
            }
        }

        ControlDefinition controlDefinition = null;
        if (super.getControl() != null) {
            isDelegate = false;
            controlDefinition = super.getControl();
        }
        else if (getControl() != null) {
            isDelegate = true;
            controlDefinition = getControl();
        }
        if (controlDefinition != null) {
            try {
                controlDefinition.completeValidation(rootBusinessObjectClass, null, validationCompletionUtils);
            }
            catch (DataDictionaryException e) {
                String source = isDelegate ? "delegated" : "overridden";
                throw buildReferenceValidationException("error validating " + source + " control", e);
            }
        }
    }

    private ReferenceValidationException buildReferenceValidationException(String newMessage, DataDictionaryException e) {
        String caughtMessage = StringUtils.substringBeforeLast(e.getMessage(), "(");
        String referenceMessage = caughtMessage + "(" + getParseLocation() + ")";

        return new ReferenceValidationException(newMessage + ": " + referenceMessage, e);
    }


    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String name = super.getName();

        // workaround for the mysterious, still-unreproducible-on-my-machine, null delegate exception on Tomcat startup
        if ((name == null) && (delegate != null)) {
            name = getDelegate().getName();
        }
        return "AttributeReferenceDefinition for attribute " + name;
    }
}