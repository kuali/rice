/**
 * Copyright 2005-2014 The Kuali Foundation
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

import java.beans.PropertyEditor;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.krad.datadictionary.control.ControlDefinition;
import org.kuali.rice.krad.datadictionary.mask.MaskFormatterLiteral;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.validation.ValidationPattern;
import org.kuali.rice.krad.datadictionary.validation.capability.CaseConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.Formatable;
import org.kuali.rice.krad.datadictionary.validation.capability.HierarchicallyConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.MustOccurConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.PrerequisiteConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.ValidCharactersConstrainable;
import org.kuali.rice.krad.datadictionary.validation.constraint.CaseConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.MustOccurConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.util.KRADUtils;

/**
 * A single attribute definition in the DataDictionary, which contains
 * information relating to the display, validation, and general maintenance of a
 * specific attribute of an entry.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "attributeDefinition")
public class AttributeDefinition extends AttributeDefinitionBase implements CaseConstrainable, PrerequisiteConstrainable, Formatable, HierarchicallyConstrainable, MustOccurConstrainable, ValidCharactersConstrainable {
    private static final long serialVersionUID = -2490613377818442742L;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AttributeDefinition.class);

    protected Boolean forceUppercase;

    protected Boolean unique;

    //These are deprecated DO NOT USE with new KRAD implementations
    @Deprecated
    protected ValidationPattern validationPattern;

    @Deprecated
    protected ControlDefinition control;

    // TODO: rename to control once ControlDefinition is removed
    protected Control controlField;
    protected Control cachedDerivedControl = null;

    @Deprecated
    protected String formatterClass;
    protected PropertyEditor propertyEditor;

    protected AttributeSecurity attributeSecurity;

    protected Boolean dynamic;

    // KRAD constraints
    protected String customValidatorClass;
    protected ValidCharactersConstraint validCharactersConstraint;
    protected CaseConstraint caseConstraint;
    protected List<PrerequisiteConstraint> dependencyConstraints;
    protected List<MustOccurConstraint> mustOccurConstraints;

    //TODO: This may not be required since we now use ComplexAttributeDefinition
    protected String childEntryName;

    private KeyValuesFinder optionsFinder;

    protected String alternateDisplayAttributeName;
    protected String additionalDisplayAttributeName;

    public AttributeDefinition() {
        super();
    }

    /**
     * Setter for force upper case
     *
     * @param forceUppercase
     */
    public void setForceUppercase(Boolean forceUppercase) {
        this.forceUppercase = forceUppercase;
    }

    /**
     * Indicates whether user entry should be converted to upper case
     *
     * <p>
     * If set all user input will be changed to uppercase. Values from the database will also be forced to display
     * as upper case and thus be persisted as upper case.
     * </p>
     *
     * If not set and embedded metadata is present, the ForceUppercase value will be read from the linked metadata.
     *
     * @return boolean true if force upper case is set
     */
    @BeanTagAttribute
    public Boolean getForceUppercase() {
        if ( forceUppercase != null ) {
            return forceUppercase;
        }
        if ( getDataObjectAttribute() != null ) {
            return getDataObjectAttribute().isForceUppercase();
        }
        return Boolean.FALSE;
    }

    /**
     * Returns the maximum length for this field, if set.  If not set, it attempts to pull from
     * the embedded metadata, if any.
     *z
     * @see org.kuali.rice.krad.datadictionary.validation.constraint.LengthConstraint#getMaxLength()
     */
    @BeanTagAttribute
    public Integer getMaxLength() {
        if ( getSimpleConstraint().getMaxLength() != null ) {
            return getSimpleConstraint().getMaxLength();
        }
        if ( getDataObjectAttribute() != null ) {
            if ( getDataObjectAttribute().getMaxLength() != null ) {
                return new Integer( getDataObjectAttribute().getMaxLength().intValue() );
            }
        }
        return null;
    }

    /**
     * Setter for maximum length
     *
     * @param maxLength
     */
    public void setMaxLength(Integer maxLength) {
        this.getSimpleConstraint().setMaxLength(maxLength);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.validation.constraint.RangeConstraint#getExclusiveMin()
     */
    @BeanTagAttribute
    public String getExclusiveMin() {
        return this.getSimpleConstraint().getExclusiveMin();
    }

    /**
     * Setter for minimum value
     *
     * @param exclusiveMin - minimum allowed value
     */
    public void setExclusiveMin(String exclusiveMin) {
        this.getSimpleConstraint().setExclusiveMin(exclusiveMin);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.validation.constraint.RangeConstraint#getInclusiveMax()
     */
    @BeanTagAttribute
    public String getInclusiveMax() {
        return this.getSimpleConstraint().getInclusiveMax();
    }

    /**
     * Setter for maximum value
     *
     * @param inclusiveMax - max allowed value
     */
    public void setInclusiveMax(String inclusiveMax) {
        this.getSimpleConstraint().setInclusiveMax(inclusiveMax);
    }

    /**
     * The validationPattern element defines the allowable character-level or
     * field-level values for an attribute.
     *
     * JSTL: validationPattern is a Map which is accessed using a key of
     * "validationPattern". Each entry may contain some of the keys listed
     * below. The keys that may be present for a given attribute are dependent
     * upon the type of validationPattern.
     *
     * maxLength (String) exactLength type allowWhitespace allowUnderscore
     * allowPeriod validChars precision scale allowNegative
     *
     * The allowable keys (in addition to type) for each type are: Type****
     * ***Keys*** alphanumeric exactLength maxLength allowWhitespace
     * allowUnderscore allowPeriod
     *
     * alpha exactLength maxLength allowWhitespace
     *
     * anyCharacter exactLength maxLength allowWhitespace
     *
     * charset validChars
     *
     * numeric exactLength maxLength
     *
     * fixedPoint allowNegative precision scale
     *
     * floatingPoint allowNegative
     *
     * date n/a emailAddress n/a javaClass n/a month n/a phoneNumber n/a
     * timestamp n/a year n/a zipcode n/a
     *
     * Note: maxLength and exactLength are mutually exclusive. If one is
     * entered, the other may not be entered.
     *
     * Note: See ApplicationResources.properties for exact regex patterns. e.g.
     * validationPatternRegex.date for regex used in date validation.
     */
    @Deprecated
    public void setValidationPattern(ValidationPattern validationPattern) {
        this.validationPattern = validationPattern;
    }

    /**
     * Indicates whether a validation pattern has been set
     * @return boolean
     */
    @Deprecated
    public boolean hasValidationPattern() {
        return (validationPattern != null);
    }

    /**
     * Defines the allowable character-level or
     * field-level values for an attribute
     *
     * <p>
     * ValidationPattern is a Map which is accessed using a key of "validationPattern". Each entry may contain
     * some of the keys listed below. The keys that may be present for a given attribute are dependent
     * upon the type of validationPattern.
     *
     * maxLength (String) exactLength type allowWhitespace allowUnderscore
     * allowPeriod validChars precision scale allowNegative
     *
     * The allowable keys (in addition to type) for each type are: Type****
     * ***Keys*** alphanumeric exactLength maxLength allowWhitespace
     * allowUnderscore allowPeriod
     *
     * alpha exactLength maxLength allowWhitespace
     *
     * anyCharacter exactLength maxLength allowWhitespace
     *
     * charset validChars
     *
     * numeric exactLength maxLength
     *
     * fixedPoint allowNegative precision scale
     *
     * floatingPoint allowNegative
     *
     * date n/a emailAddress n/a javaClass n/a month n/a phoneNumber n/a
     * timestamp n/a year n/a zipcode n/a
     *
     * Note: maxLength and exactLength are mutually exclusive. If one is
     * entered, the other may not be entered.
     *
     * Note: See ApplicationResources.properties for exact regex patterns. e.g.
     * validationPatternRegex.date for regex used in date validation.
     * </p>
     *
     * @return ValidationPattern
     */
    @Deprecated
    public ValidationPattern getValidationPattern() {
        return this.validationPattern;
    }

    /**
     * @return control
     */
    @BeanTagAttribute(name = "oldControl", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    @Deprecated
    public ControlDefinition getControl() {
        return control;
    }

    /**
     * The control element defines the manner in which an attribute is displayed
     * and the manner in which the attribute value is entered.
     *
     * JSTL: control is a Map representing an HTML control. It is accessed using
     * a key of "control". The table below shows the types of entries associated
     * with each type of control.
     *
     * * Control Type** **Key** **Value** checkbox checkbox boolean String
     *
     * hidden hidden boolean String
     *
     * radio radio boolean String valuesFinder valuesFinder class name
     * dataObjectClass String keyAttribute String labelAttribute String
     * includeKeyInLabel boolean String
     *
     * select select boolean String valuesFinder valuesFinder class name
     * dataObjectClass String keyAttribute String labelAttribute String
     * includeBlankRow boolean String includeKeyInLabel boolean String
     *
     * apcSelect apcSelect boolean String paramNamespace String
     * parameterDetailType String parameterName String
     *
     * text text boolean String size String
     *
     * textarea textarea boolean String rows cols
     *
     * currency currency boolean String size String formattedMaxLength String
     *
     * kualiUser kualiUser boolean String universalIdAttributeName String
     * userIdAttributeName String personNameAttributeName String
     *
     * lookupHidden lookupHidden boolean String
     *
     * lookupReadonly lookupReadonly boolean String
     *
     * @param control
     * @throws IllegalArgumentException if the given control is null
     */
    @Deprecated
    public void setControl(ControlDefinition control) {
        if (control == null) {
            throw new IllegalArgumentException("invalid (null) control");
        }
        this.control = control;
    }

    @Deprecated
    public boolean hasFormatterClass() {
        return (formatterClass != null);
    }

    @Override
    @BeanTagAttribute
    @Deprecated
    public String getFormatterClass() {
        return formatterClass;
    }

    /**
     * The formatterClass element is used when custom formatting is required for
     * display of the field value. This field specifies the name of the java
     * class to be used for the formatting. About 15 different classes are
     * available including BooleanFormatter, CurrencyFormatter, DateFormatter,
     * etc.
     */
    @Deprecated
    public void setFormatterClass(String formatterClass) {
        if (formatterClass == null) {
            throw new IllegalArgumentException("invalid (null) formatterClass");
        }
        this.formatterClass = formatterClass;
    }

    /**
     * Performs formatting of the field value for display and then converting the value back to its
     * expected type from a string.
     *
     * If not set in the AttributeDefinition, it attempts to pull from the embedded metadata, if any.
     *
     * <p>
     * Note property editors exist and are already registered for the basic Java types and the
     * common Kuali types such as [@link KualiDecimal}. Registration with this property is only
     * needed for custom property editors
     * </p>
     *
     * @return PropertyEditor property editor instance to use for this field
     */
    @BeanTagAttribute
    public PropertyEditor getPropertyEditor() {
        if ( propertyEditor != null ) {
            return propertyEditor;
        }
        if ( getDataObjectAttribute() != null ) {
            return getDataObjectAttribute().getPropertyEditor();
        }
        return null;
    }

    /**
     * Setter for the custom property editor to use for the field
     *
     * @param propertyEditor
     */
    public void setPropertyEditor(PropertyEditor propertyEditor) {
        this.propertyEditor = propertyEditor;
    }

    /**
     * Convenience setter for configuring a property editor by class
     *
     * @param propertyEditorClass
     */
    public void setPropertyEditorClass(Class<? extends PropertyEditor> propertyEditorClass) {
        this.propertyEditor = KRADUtils.createNewObjectFromClass(propertyEditorClass);
    }

    /**
     * This overridden method ...
     *
     * @see org.kuali.rice.krad.datadictionary.DictionaryBeanBase#dataDictionaryPostProcessing()
     */
    @Override
    public void dataDictionaryPostProcessing() {
        super.dataDictionaryPostProcessing();
        if ( getAttributeSecurity() != null ) {
            getAttributeSecurity().dataDictionaryPostProcessing();
        }
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition
     * fields.
     *
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryEntry#completeValidation()
     */
    @Override
    @Deprecated
    public void completeValidation(Class<?> rootObjectClass, Class<?> otherObjectClass) {
        completeValidation(rootObjectClass, otherObjectClass, new ValidationTrace());
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition
     * fields.
     *
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryEntry#completeValidation(org.kuali.rice.krad.datadictionary.validator.ValidationTrace)
     */
    @Override
    public void completeValidation(Class rootObjectClass, Class otherObjectClass, ValidationTrace tracer) {
        tracer.addBean(this.getClass().getSimpleName(), "id: " + getId());
        try {
            if (StringUtils.isBlank(getName())) {
                String currentValues[] = {"id = " + getId(), "class = " + rootObjectClass.getName()};
                tracer.createError("AttributeDefinition missing name", currentValues);
            }
            if (!DataDictionary.isPropertyOf(rootObjectClass, getName())) {
                String currentValues[] = {"property = " + getName(), "class = " + rootObjectClass.getName()};
                tracer.createError("Property is not found in class. Ensure that the property is defined on the class and that there is at least a public 'getter' for it.", currentValues);
            }

            if (getControl() == null && getControlField() == null) {
                String currentValues[] = {"property = " + getName(), "class = " + rootObjectClass.getName()};
                tracer.createError("Property does not have a control defined in the class", currentValues);
            }

            if (getAttributeSecurity() != null) {
                getAttributeSecurity().completeValidation(rootObjectClass, otherObjectClass, tracer.getCopy());
            }

            // KNS Controls - do not use KRAD Validation style
            if (getControl() != null) {
                getControl().completeValidation(rootObjectClass, otherObjectClass);
            }
            if (validationPattern != null) {
                 validationPattern.completeValidation();
            }

            if (getFormatterClass() != null) {
                try {
                    Class formatterClassObject = ClassUtils.getClass(ClassLoaderUtils.getDefaultClassLoader(),
                            getFormatterClass());
                    if (!Formatter.class.isAssignableFrom(formatterClassObject)) {
                        String currentValues[] = {"formatterClassObject = " + formatterClassObject.getName()};
                        tracer.createError("FormatterClass is not a valid instance", currentValues);
                    }
                } catch (ClassNotFoundException e) {
                    String currentValues[] = {"class = " + getFormatterClass()};
                    tracer.createError("FormatterClass could not be found", currentValues);
                }
            }
        } catch (RuntimeException ex) {
            String currentValues[] =
                    {"attribute = " + rootObjectClass + "." + getName(), "Exception = " + ex.getMessage()};
            tracer.createError("Unable to validate attribute", currentValues);
            LOG.error("Exception while validating AttributeDefinition: " + getId(), ex );
        }
    }

    @BeanTagAttribute(name = "attributeSecurity", type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public AttributeSecurity getAttributeSecurity() {
        if ( attributeSecurity != null ) {
            return attributeSecurity;
        }
        // If we have an embedded attribute definition and this attribute is
        // listed as "sensitive", then set the field to be masked by default on the UI
        if ( getDataObjectAttribute() != null ) {
            if ( getDataObjectAttribute().isSensitive() ) {
                AttributeSecurity attrSec = new AttributeSecurity();
                attrSec.setMask(true);
                attrSec.setMaskFormatter(new MaskFormatterLiteral());
                attributeSecurity = attrSec;
            }
        }
        return attributeSecurity;
    }

    public void setAttributeSecurity(AttributeSecurity attributeSecurity) {
        this.attributeSecurity = attributeSecurity;
    }

    public boolean hasAttributeSecurity() {
        return (getAttributeSecurity() != null);
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

    /**
     * Default {@code Control} to use when the attribute is to be rendered
     * for the UI. Used by the UIF when a control is not defined for an
     * {@code InputField}
     *
     * If not set in the AttributeDefinition, a default will be generated from the metadata for this field.
     *
     * @return Control instance
     */
    @BeanTagAttribute(name = "control", type = BeanTagAttribute.AttributeType.BYTYPE)
    public Control getControlField() {
        if ( controlField != null ) {
            return controlField;
        }
        if ( cachedDerivedControl == null ) {
            if ( GlobalResourceLoader.isInitialized() ) {
                cachedDerivedControl = KRADServiceLocatorWeb.getUifDefaultingService().deriveControlAttributeFromMetadata(this);
            }
        }
        return cachedDerivedControl;
    }

    /**
     * Setter for the default control
     *
     * @param controlField
     */
    public void setControlField(Control controlField) {
        this.controlField = controlField;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.validation.constraint.LengthConstraint#getMinLength()
     */
    @BeanTagAttribute
    public Integer getMinLength() {
        if ( getSimpleConstraint().getMinLength() != null ) {
            return getSimpleConstraint().getMinLength();
        }
        if ( getDataObjectAttribute() != null ) {
            if ( getDataObjectAttribute().getMinLength() != null ) {
                return new Integer( getDataObjectAttribute().getMinLength().intValue() );
            }
        }
        return null;
    }

    /**
     * Setter for minumum length
     *
     * @param minLength
     */
    public void setMinLength(Integer minLength) {
        this.getSimpleConstraint().setMinLength(minLength);
    }

    /**
     * Returns the Kuali datatype for this field.  See {@link DataType} for the defined types.
     *
     * If not defined in the AttributeDefinition, it will be retrieved from the embedded metadata, if defined.
     *
     * If not defined by either, will return {@link DataType#STRING}.
     */
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.SINGLEBEAN)
    public DataType getDataType() {
        if ( simpleConstraint.getDataType() != null ) {
            return simpleConstraint.getDataType();
        }
        if ( getDataObjectAttribute() != null ) {
            return getDataObjectAttribute().getDataType();
        }
        return DataType.STRING;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(DataType dataType) {
        simpleConstraint.setDataType(dataType);
    }

    public void setDataType(String dataType) {
        simpleConstraint.setDataType(DataType.valueOf(dataType));
    }

    /**
     * @return the customValidatorClass
     */
    @BeanTagAttribute
    public String getCustomValidatorClass() {
        return this.customValidatorClass;
    }

    /**
     * @param customValidatorClass the customValidatorClass to set
     */
    public void setCustomValidatorClass(String customValidatorClass) {
        this.customValidatorClass = customValidatorClass;
    }

    /**
     * @return the validChars
     */
    @Override
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public ValidCharactersConstraint getValidCharactersConstraint() {
        if ( validCharactersConstraint == null ) {
            // If there is no constraint set, attempt to derive one
            if ( GlobalResourceLoader.isInitialized() ) {
                // We don't set a default validation pattern if the field is hidden
                if ( getControlField() != null && !getControlField().isHidden() && !Boolean.TRUE.equals(getControlField().getReadOnly()) ) {
                    validCharactersConstraint = KRADServiceLocatorWeb.getUifDefaultingService().deriveValidCharactersConstraint( this );
                }
            }
        }
        return validCharactersConstraint;
    }

    /**
     * @param validCharactersConstraint the validChars to set
     */
    public void setValidCharactersConstraint(ValidCharactersConstraint validCharactersConstraint) {
        this.validCharactersConstraint = validCharactersConstraint;
    }

    /**
     * @return the caseConstraint
     */
    @Override
    @BeanTagAttribute(type = BeanTagAttribute.AttributeType.DIRECTORBYTYPE)
    public CaseConstraint getCaseConstraint() {
        return this.caseConstraint;
    }

    /**
     * @param caseConstraint the caseConstraint to set
     */
    public void setCaseConstraint(CaseConstraint caseConstraint) {
        this.caseConstraint = caseConstraint;
    }

    /**
     * @return the requireConstraint
     */
    @Override
    @BeanTagAttribute
    public List<PrerequisiteConstraint> getPrerequisiteConstraints() {
        return this.dependencyConstraints;
    }

    /**
     * @param dependencyConstraints the requireConstraint to set
     */
    public void setPrerequisiteConstraints(List<PrerequisiteConstraint> dependencyConstraints) {
        this.dependencyConstraints = dependencyConstraints;
    }

    /**
     * @return the occursConstraint
     */
    @Override
    @BeanTagAttribute
    public List<MustOccurConstraint> getMustOccurConstraints() {
        return this.mustOccurConstraints;
    }

    /**
     * @param mustOccurConstraints the occursConstraint to set
     */
    public void setMustOccurConstraints(List<MustOccurConstraint> mustOccurConstraints) {
        this.mustOccurConstraints = mustOccurConstraints;
    }

    /**
     * @return the childEntryName
     */
    @Override
    @BeanTagAttribute
    public String getChildEntryName() {
        return this.childEntryName;
    }

    /**
     * @param childEntryName the childEntryName to set
     */
    public void setChildEntryName(String childEntryName) {
        this.childEntryName = childEntryName;
    }

    /**
     * Instance of {@code KeyValluesFinder} that should be invoked to
     * provide a List of values the field can have. Generally used to provide
     * the options for a multi-value control or to validate the submitted field
     * value
     *
     * @return KeyValuesFinder instance
     */
    @BeanTagAttribute
    public KeyValuesFinder getOptionsFinder() {
        if ( optionsFinder == null ) {
            if ( getDataObjectAttribute() != null && getDataObjectAttribute().getValidValues() != null ) {
                return getDataObjectAttribute().getValidValues();
            }
        }
        return optionsFinder;
    }

    /**
     * Setter for the field's KeyValuesFinder instance
     *
     * @param optionsFinder
     */
    public void setOptionsFinder(KeyValuesFinder optionsFinder) {
        this.optionsFinder = optionsFinder;
    }

    /**
     * Setter that takes in the class name for the options finder and creates a
     * new instance to use as the finder for the attribute field
     *
     * @param optionsFinderClass
     */
    public void setOptionsFinderClass(Class<? extends KeyValuesFinder> optionsFinderClass) {
        this.optionsFinder = KRADUtils.createNewObjectFromClass(optionsFinderClass);
    }

    public void setAdditionalDisplayAttributeName(String additionalDisplayAttributeName) {
        this.additionalDisplayAttributeName = additionalDisplayAttributeName;
    }

    @BeanTagAttribute
    public String getAdditionalDisplayAttributeName() {
        return this.additionalDisplayAttributeName;
    }

    public void setAlternateDisplayAttributeName(String alternateDisplayAttributeName) {
        this.alternateDisplayAttributeName = alternateDisplayAttributeName;
    }

    @BeanTagAttribute
    public String getAlternateDisplayAttributeName() {
        return this.alternateDisplayAttributeName;
    }

    /**
     * Gets dependency constraints for this AttributeDefinition.  Same as getPrerequisiteConstraints.
     *
     * @return dependency constraints
     */
    public List<PrerequisiteConstraint> getDependencyConstraints() {
        return dependencyConstraints;
    }

    /**
     * Sets dependency constraints for this AttributeDefinition.  Same as setPrerequisiteConstraints.
     *
     * @param dependencyConstraints dependency constraints
     */
    public void setDependencyConstraints(List<PrerequisiteConstraint> dependencyConstraints) {
        this.dependencyConstraints = dependencyConstraints;
    }

}
