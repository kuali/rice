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
package org.kuali.rice.krad.uif.field;

import java.beans.PropertyEditor;
import java.util.List;

import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.mask.MaskFormatter;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.uif.component.ComponentSecurity;
import org.kuali.rice.krad.uif.component.DataBinding;
import org.kuali.rice.krad.uif.widget.Helpable;
import org.kuali.rice.krad.uif.widget.Inquiry;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * Component interface for data fields. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DataField extends DataBinding, Helpable, Field {

    /**
     * Defaults the properties of the <code>DataField</code> to the
     * corresponding properties of its <code>AttributeDefinition</code>
     * retrieved from the dictionary (if such an entry exists). If the field
     * already contains a value for a property, the definitions value is not
     * used.
     *
     * @param attributeDefinition AttributeDefinition instance the property values should be
     * copied from
     */
    void copyFromAttributeDefinition(AttributeDefinition attributeDefinition);

    /**
     * Indicates whether the data field instance allows input, subclasses should override and set to
     * true if input is allowed
     *
     * @return true if input is allowed, false if read only
     */
    boolean isInputAllowed();

    /**
     * Setter for the component's property name
     *
     * @param propertyName
     */
    void setPropertyName(String propertyName);

    /**
     * Performs formatting of the field value for display and then converting the value back to its
     * expected type from a string
     *
     * <p>
     * Note property editors exist and are already registered for the basic Java types and the
     * common Kuali types such as [@link KualiDecimal}. Registration with this property is only
     * needed for custom property editors
     * </p>
     *
     * @return PropertyEditor property editor instance to use for this field
     */
    PropertyEditor getPropertyEditor();

    /**
     * Setter for the custom property editor to use for the field
     *
     * @param propertyEditor
     */
    void setPropertyEditor(PropertyEditor propertyEditor);

    /**
     * Convenience setter for configuring a property editor by class
     *
     * @param propertyEditorClass
     */
    void setPropertyEditorClass(Class<? extends PropertyEditor> propertyEditorClass);

    /**
     * Returns the full binding path (the path used in the name attribute of the input).
     * This differs from propertyName in that it uses BindingInfo to determine the path.
     *
     * @return full binding path name
     */
    String getName();

    /**
     * Name of the attribute within the data dictionary the attribute field is
     * associated with
     *
     * <p>
     * During the initialize phase for the <code>View</code>, properties for
     * attribute fields are defaulted from a corresponding
     * <code>AttributeDefinition</code> in the data dictionary. Based on the
     * propertyName and parent object class the framework attempts will
     * determine the attribute definition that is associated with the field and
     * set this property. However this property can also be set in the fields
     * configuration to use another dictionary attribute.
     * </p>
     *
     * <p>
     * The attribute name is used along with the dictionary object entry to find
     * the <code>AttributeDefinition</code>
     * </p>
     *
     * @return attribute name
     */
    String getDictionaryAttributeName();

    /**
     * Setter for the dictionary attribute name
     *
     * @param dictionaryAttributeName
     */
    void setDictionaryAttributeName(String dictionaryAttributeName);

    /**
     * Object entry name in the data dictionary the associated attribute is
     * apart of
     *
     * <p>
     * During the initialize phase for the <code>View</code>, properties for
     * attribute fields are defaulted from a corresponding
     * <code>AttributeDefinition</code> in the data dictionary. Based on the
     * parent object class the framework will determine the object entry for the
     * associated attribute. However the object entry can be set in the field's
     * configuration to use another object entry for the attribute
     * </p>
     *
     * <p>
     * The attribute name is used along with the dictionary object entry to find
     * the <code>AttributeDefinition</code>
     * </p>
     *
     * @return String
     */
    String getDictionaryObjectEntry();

    /**
     * Setter for the dictionary object entry
     *
     * @param dictionaryObjectEntry
     */
    void setDictionaryObjectEntry(String dictionaryObjectEntry);

    /**
     * Default value for the model property the field points to
     *
     * <p>
     * When a new <code>View</code> instance is requested, the corresponding
     * model will be newly created. During this initialization process the value
     * for the model property will be set to the given default value, if it was null.
     * This will only work on properties which can be determined to be null.
     * Therefore a String property with an empty string value will
     * not be ovewritten with the defaultValue set here.
     * </p>
     *
     * <p>
     * In addition, int, boolean, and other primitive types
     * will not use this default value because they inherently have a value in Java (0 for int, false for boolean, etc).
     * To use such types either using a primitive wrapper type (Integer, Boolean, etc) so an unset variable can
     * be determined to be null, or explicitly set the default value on the form/object itself for these types and
     * not through this property.
     * </p>
     *
     * @return default value
     */
    Object getDefaultValue();

    /**
     * Setter for the fields default value
     *
     * @param defaultValue
     */
    void setDefaultValue(Object defaultValue);

    /**
     * Gives Class that should be invoked to produce the default value for the
     * field
     *
     * @return default value finder class
     */
    Class<? extends ValueFinder> getDefaultValueFinderClass();

    /**
     * Setter for the default value finder class
     *
     * @param defaultValueFinderClass
     */
    void setDefaultValueFinderClass(Class<? extends ValueFinder> defaultValueFinderClass);

    /**
     * Array of default values for the model property the field points to
     *
     * <p>
     * When a new <code>View</code> instance is requested, the corresponding
     * model will be newly created. During this initialization process the value
     * for the model property will be set to the given default values (if set)
     * </p>
     *
     * @return default value
     */
    List<Object> getDefaultValues();

    /**
     * Setter for the fields default values
     *
     * @param defaultValues
     */
    void setDefaultValues(List<Object> defaultValues);

    /**
     * For read only DataFields, if forcedValue has a value, the value of it will be used instead of the value
     * received from the propertyName specified for this field;
     * this can be combined with SpringEL to format a property value in some way, for example.
     *
     * @return the forced value
     */
    String getForcedValue();

    /**
     * @see org.kuali.rice.krad.uif.field.DataField#setForcedValue(String)
     */
    void setForcedValue(String forcedValue);

    /**
     * Summary help text for the field
     *
     * @return summary help text
     */
    String getHelpSummary();

    /**
     * Setter for the summary help text
     *
     * @param helpSummary
     */
    void setHelpSummary(String helpSummary);

    /**
     * Data Field Security object that indicates what authorization (permissions) exist for the field
     *
     * @return DataFieldSecurity instance
     */
    DataFieldSecurity getDataFieldSecurity();

    /**
     * Override to assert a {@link DataFieldSecurity} instance is set
     *
     * @param componentSecurity instance of DataFieldSecurity
     */
    void setComponentSecurity(ComponentSecurity componentSecurity);

    /**
     * Indicates the field should be read-only but also a hidden should be generated for the field
     *
     * <p>
     * Useful for when a value is just displayed but is needed by script
     * </p>
     *
     * @return true if field should be readOnly hidden, false if not
     */
    boolean isAddHiddenWhenReadOnly();

    /**
     * Setter for the read-only hidden indicator
     *
     * @param addHiddenWhenReadOnly
     */
    void setAddHiddenWhenReadOnly(boolean addHiddenWhenReadOnly);

    /**
     * Inquiry widget for the field
     *
     * <p>
     * The inquiry widget will render a link for the field value when read-only
     * that points to the associated inquiry view for the field. The inquiry can
     * be configured to point to a certain <code>InquiryView</code>, or the
     * framework will attempt to associate the field with a inquiry based on its
     * metadata (in particular its relationships in the model)
     * </p>
     *
     * @return Inquiry field inquiry
     */
    Inquiry getInquiry();

    /**
     * Setter for the inquiry widget
     *
     * @param inquiry
     */
    void setInquiry(Inquiry inquiry);

    /**
     * Indicates whether inquiries should be automatically set when a relationship for the field's property
     * is found
     *
     * <p>
     * Note this only applies when the {@link #getInquiry()} widget has not been configured (is null)
     * and is set to true by default
     * </p>
     *
     * @return true if auto inquiries are enabled, false if not
     */
    boolean isEnableAutoInquiry();

    /**
     * Setter for enabling automatic inquiries
     *
     * @param enableAutoInquiry
     */
    void setEnableAutoInquiry(boolean enableAutoInquiry);

    /**
     * When true, render the info message span which contains can contain additional information
     * about the field (used by Field Query functionality)
     *
     * @return true if the span will be rendered, false otherwise
     */
    boolean isRenderInfoMessageSpan();

    /**
     * @see org.kuali.rice.krad.uif.field.DataField#isRenderInfoMessageSpan()
     * @param renderInfoMessageSpan
     */
    void setRenderInfoMessageSpan(boolean renderInfoMessageSpan);

    /**
     * When true, render the marker icon span to show icons related to the field (used by CompareFieldCreateModifier on
     * maintenance documetnts to mark editted fields)
     *
     * @return true if the the marker icon span will be rendered, false otherwise
     */
    boolean isRenderMarkerIconSpan();

    /**
     * @see org.kuali.rice.krad.uif.field.DataField#isRenderMarkerIconSpan()
     * @param renderMarkerIconSpan
     */
    void setRenderMarkerIconSpan(boolean renderMarkerIconSpan);

    /**
     * Additional display attribute name, which will be displayed next to the actual field value
     * when the field is readonly with hyphen in between like PropertyValue - AdditionalPropertyValue
     *
     * @param readOnlyDisplaySuffixPropertyName name of the additional display property
     */
    void setReadOnlyDisplaySuffixPropertyName(String readOnlyDisplaySuffixPropertyName);

    /**
     * Returns the additional display attribute name to be displayed when the field is readonly
     *
     * @return additional display attribute name
     */
    String getReadOnlyDisplaySuffixPropertyName();

    /**
     * Sets the alternate display attribute name to be displayed when the field is readonly.
     * This properties value will be displayed instead of actual fields value when the field is readonly.
     *
     * @param readOnlyDisplayReplacementPropertyName alternate display property name
     */
    void setReadOnlyDisplayReplacementPropertyName(String readOnlyDisplayReplacementPropertyName);

    /**
     * Returns the alternate display attribute name to be displayed when the field is readonly.
     *
     * @return alternate Display Property Name
     */
    String getReadOnlyDisplayReplacementPropertyName();

    /**
     * Returns the alternate display value
     *
     * @return the alternate display value set for this field
     */
    String getReadOnlyDisplayReplacement();

    /**
     * Setter for the alternative display value
     *
     * @param value
     */
    void setReadOnlyDisplayReplacement(String value);

    /**
     * Returns the additional display value.
     *
     * @return the additional display value set for this field
     */
    String getReadOnlyDisplaySuffix();

    /**
     * Setter for the additional display value
     *
     * @param value
     */
    void setReadOnlyDisplaySuffix(String value);

    /**
     * Gets the readOnlyListDisplayType.
     *
     * <p>When this is not set, the list will default to the delimited list display with a default of comma and space
     * (", ") - if readOnlyListDelimiter is not set as well.  The type can be set as the following:
     * <ul>
     * <li>"DELIMITED" - list will be output with delimiters between each item defined by readOnlyListDelimiter</li>
     * <li>"BREAK" - list will be output with breaks between each item</li>
     * <li>"OL" - list will be output in ordered list format (numbered)</li>
     * <li>"UL" - list will be output in unordered list format (bulleted)</li>
     * </ul>
     * </p>
     *
     * @return the display type to use
     */
    String getReadOnlyListDisplayType();

    /**
     * Set the readOnlyListDisplayType
     *
     * @param readOnlyListDisplayType
     */
    void setReadOnlyListDisplayType(String readOnlyListDisplayType);

    /**
     * The readOnlyListDelimiter is used to set the delimiter used when "DELIMITED" type is set for
     * readOnlyListDisplayType
     *
     * @return the delimiter to use in readOnly list output with "DELIMITED" type set
     */
    String getReadOnlyListDelimiter();

    /**
     * Set the readOnlyListDelimiter
     *
     * @param readOnlyListDelimiter
     */
    void setReadOnlyListDelimiter(String readOnlyListDelimiter);

    /**
     * Indicates whether the value for the field should be masked (or partially masked) on display
     *
     * <p>
     * If set to true, the field value will be masked by applying the configured {@link #getMaskFormatter()}
     * </p>
     *
     * <p>
     * If a KIM permission exists that should be checked to determine whether the value should be masked or not,
     * this value should not be set but instead the mask or partialMask property on {@link #getComponentSecurity()}
     * should be set to true. This indicates there is a mask permission that should be consulted. If the user
     * does not have the permission, this flag will be set to true by the framework and the value masked using
     * the mask formatter configured on the security object
     * </p>
     *
     * @return true if the field value should be masked, false if not
     */
    boolean isApplyMask();

    /**
     * Setter for the apply value mask flag
     *
     * @param applyMask
     */
    void setApplyMask(boolean applyMask);

    /**
     * MaskFormatter instance that will be used to mask the field value when {@link #isApplyMask()} is true
     *
     * <p>
     * Note in cases where the mask is applied due to security (KIM permissions), the mask or partial mask formatter
     * configured on {@link #getComponentSecurity()} will be used instead of this mask formatter
     * </p>
     *
     * @return MaskFormatter instance
     */
    MaskFormatter getMaskFormatter();

    /**
     * Setter for the MaskFormatter instance to apply when the value is masked
     *
     * @param maskFormatter
     */
    void setMaskFormatter(MaskFormatter maskFormatter);

    /**
     * Allows specifying hidden property names without having to specify as a
     * field in the group config (that might impact layout)
     *
     * @return hidden property names
     */
    List<String> getAdditionalHiddenPropertyNames();

    /**
     * Setter for the hidden property names
     *
     * @param additionalHiddenPropertyNames
     */
    void setAdditionalHiddenPropertyNames(List<String> additionalHiddenPropertyNames);

    /**
     * List of property names whose values should be displayed read-only under this field
     *
     * <p>
     * In the attribute field template for each information property name given its values is
     * outputted read-only. Informational property values can also be updated dynamically with
     * the use of field attribute query
     * </p>
     *
     * <p>
     * Simple property names can be given if the property has the same binding parent as this
     * field, in which case the binding path will be adjusted by the framework. If the property
     * names starts with org.kuali.rice.krad.uif.UifConstants#NO_BIND_ADJUST_PREFIX, no binding
     * prefix will be added.
     * </p>
     *
     * @return informational property names
     */
    List<String> getPropertyNamesForAdditionalDisplay();

    /**
     * Setter for the list of informational property names
     *
     * @param propertyNamesForAdditionalDisplay
     */
    void setPropertyNamesForAdditionalDisplay(List<String> propertyNamesForAdditionalDisplay);

    /**
     * Sets HTML escaping for this property value. HTML escaping will be handled in alternate and additional fields
     * also.
     */
    void setEscapeHtmlInPropertyValue(boolean escapeHtmlInPropertyValue);

    /**
     * Returns true if HTML escape allowed for this field
     *
     * @return true if escaping allowed
     */
    boolean isEscapeHtmlInPropertyValue();

    /**
     * Returns true if this field is of type {@code TextAreaControl}.
     *
     * <p>
     * Used to preserve text formatting in a textarea when the view
     * is readOnly by enclosing the text in a </pre> tag.
     * </p>
     *
     * @return true if the field is of type {@code TextAreaControl}
     */
    boolean isMultiLineReadOnlyDisplay();

    /**
     * Setter for multiLineReadOnlyDisplay
     *
     * @param multiLineReadOnlyDisplay
     */
    void setMultiLineReadOnlyDisplay(boolean multiLineReadOnlyDisplay);

    /**
     * Indicates whether the value for the field is secure.
     *
     * <p>
     * A value will be secured if masking has been applied (by configuration or a failed KIM permission) or the field
     * has been marked as hidden due to a required KIM permission check failing.
     * </p>
     *
     * @return true if value is secure, false if not
     */
    boolean hasSecureValue();

    boolean isRenderFieldset();

    /**
     * Sets the sort type if this field is used within a collection
     *
     * <p>
     * The default sort type is the Java class of the
     * property being referenced. Since a String property may actually contain numeric or date values only this property
     * can be used to better set the sort type.
     * </p>
     *
     * @return string representation of the sort type
     */
    public String getSortAs();

    public void setSortAs(String sortAs);

    /**
     * @see org.kuali.rice.krad.uif.component.Component#completeValidation
     */
    void completeValidation(ValidationTrace tracer);

}