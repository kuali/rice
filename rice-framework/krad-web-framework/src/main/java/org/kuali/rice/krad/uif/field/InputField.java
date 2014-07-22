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

import java.util.List;

import org.kuali.rice.core.api.data.DataType;
import org.kuali.rice.krad.datadictionary.validation.capability.CaseConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.MustOccurConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.PrerequisiteConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.SimpleConstrainable;
import org.kuali.rice.krad.datadictionary.validation.capability.ValidCharactersConstrainable;
import org.kuali.rice.krad.datadictionary.validation.constraint.CaseConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.MustOccurConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.PrerequisiteConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.SimpleConstraint;
import org.kuali.rice.krad.datadictionary.validation.constraint.ValidCharactersConstraint;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.control.Control;
import org.kuali.rice.krad.uif.element.FieldValidationMessages;
import org.kuali.rice.krad.uif.element.Message;
import org.kuali.rice.krad.uif.element.ValidationMessages;
import org.kuali.rice.krad.uif.widget.QuickFinder;
import org.kuali.rice.krad.uif.widget.Suggest;

/**
 * TODO mark don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface InputField extends SimpleConstrainable, CaseConstrainable, PrerequisiteConstrainable, MustOccurConstrainable, ValidCharactersConstrainable, DataField {

    /**
     * @see DataField#isInputAllowed()
     */
    boolean isInputAllowed();

    /**
     * {@code Control} instance that should be used to input data for the
     * field
     *
     * <p>
     * When the field is editable, the control will be rendered so the user can
     * input a value(s). Controls typically are part of a Form and render
     * standard HTML control elements such as text input, select, and checkbox
     * </p>
     *
     * @return Control instance
     */
    Control getControl();

    /**
     * Setter for the field's control
     *
     * @param control
     */
    void setControl(Control control);

    /**
     * When inlineEdit is enabled, the field will appear as text, and when clicked the user will be able to edit that
     * field's value and save that new value.
     *
     * <p>The method that is called by inlineEdit is saveField.</p>
     *
     * @return inlineEdit if set to true the field will have the ability to be edited inline
     */
    public boolean isInlineEdit();

    /**
     * @see org.kuali.rice.krad.uif.field.InputFieldBase#isInlineEdit()
     */
    public void setInlineEdit(boolean inlineEdit);

    /**
     * When ajaxInlineEdit is enabled, the field will appear as text, and when clicked, the input version of that field
     * is retrieved from the server; the user will be able to edit that field's value and save that new value.
     *
     * @return ajaxInlneEdit if true the field will have the ability to be edited inline via ajax call
     */
    public boolean isAjaxInlineEdit();

    /**
     * @see InputFieldBase#isAjaxInlineEdit()
     */
    public void setAjaxInlineEdit(boolean ajaxInlineEdit);

    /**
     * Field that contains the messages (errors) for the input field. The
     * {@code ValidationMessages} holds configuration on associated messages along
     * with information on rendering the messages in the user interface
     *
     * @return ValidationMessages instance
     */
    FieldValidationMessages getValidationMessages();

    /**
     * Setter for the input field's errors field
     *
     * @param validationMessages
     */
    void setValidationMessages(FieldValidationMessages validationMessages);

    /**
     * Instance of {@code KeyValuesFinder} that should be invoked to
     * provide a List of values the field can have. Generally used to provide
     * the options for a multi-value control or to validate the submitted field
     * value
     *
     * @return KeyValuesFinder instance
     */
    KeyValuesFinder getOptionsFinder();

    /**
     * Setter for the field's KeyValuesFinder instance
     *
     * @param optionsFinder
     */
    void setOptionsFinder(KeyValuesFinder optionsFinder);

    /**
     * Get the class of the optionsFinder being used by this InputField
     *
     * @return the class of the set optionsFinder, if not set or not applicable, returns null
     */
    Class<? extends KeyValuesFinder> getOptionsFinderClass();

    /**
     * Setter that takes in the class name for the options finder and creates a
     * new instance to use as the finder for the input field
     *
     * @param optionsFinderClass the options finder class to set
     */
    void setOptionsFinderClass(Class<? extends KeyValuesFinder> optionsFinderClass);

    /**
     * Indicates whether direct inquiries should be automatically set when a relationship for
     * the field's property is found
     *
     * <p>
     * Note this only applies when the {@link #getInquiry()} widget has not been configured (is null)
     * and is set to true by default
     * </p>
     *
     * @return true if auto direct inquiries are enabled, false if not
     */
    boolean isEnableAutoDirectInquiry();

    /**
     * Setter for enabling automatic direct inquiries
     *
     * @param enableAutoDirectInquiry
     */
    void setEnableAutoDirectInquiry(boolean enableAutoDirectInquiry);

    /**
     * Lookup finder widget for the field
     *
     * <p>
     * The quickfinder widget places a small icon next to the field that allows
     * the user to bring up a search screen for finding valid field values. The
     * {@code Widget} instance can be configured to point to a certain
     * {@code LookupView}, or the framework will attempt to associate the
     * field with a lookup based on its metadata (in particular its
     * relationships in the model)
     * </p>
     *
     * @return QuickFinder lookup widget
     */
    QuickFinder getQuickfinder();

    /**
     * Setter for the lookup widget
     *
     * @param quickfinder the field lookup widget to set
     */
    void setQuickfinder(QuickFinder quickfinder);

    /**
     * Indicates whether quickfinders should be automatically set when a relationship for the field's
     * property is found
     *
     * <p>
     * Note this only applies when the {@link #getQuickfinder()} widget has not been configured (is null)
     * and is set to true by default
     * </p>
     *
     * @return true if auto quickfinders are enabled, false if not
     */
    boolean isEnableAutoQuickfinder();

    /**
     * Setter for enabling automatic quickfinders
     *
     * @param enableAutoQuickfinder
     */
    void setEnableAutoQuickfinder(boolean enableAutoQuickfinder);

    /**
     * Suggest box widget for the input field
     *
     * <p>
     * If enabled (by render flag), as the user inputs data into the
     * fields control a dynamic query is performed to provide the user
     * suggestions on values which they can then select
     * </p>
     *
     * <p>
     * Note the Suggest widget is only valid when using a standard TextControl
     * </p>
     *
     * @return Suggest instance
     */
    Suggest getSuggest();

    /**
     * Setter for the fields suggest widget
     *
     * @param suggest the field suggest widget to  set
     */
    void setSuggest(Suggest suggest);

    /**
     * Indicates indicates whether the field can only be updated through a widget
     *
     * widgetInputOnly behaves similar to ReadOnly with the exception that the value of the input field
     * can be changed via the associated widget (e.g. spinner, date picker, quickfinder, etc).
     *
     * @return true if only widget input is allowed, false otherwise
     */
    boolean isWidgetInputOnly();

    /**
     * Setter for the widget input only indicator
     *
     * @param widgetInputOnly
     */
    void setWidgetInputOnly(boolean widgetInputOnly);

    /**
     * Forces rendering of the input group div around the control.
     *
     * <p>If other components add content through script that should be grouped with the control, this flag
     * can be set to true to generate the input group, even though {@link InputField#getPostInputAddons()} may
     * be empty</p>
     *
     * @return boolean true to force rendering of the input group, false if not
     */
    boolean isRenderInputAddonGroup();

    /**
     * @see InputField#isRenderInputAddonGroup()
     */
    void setRenderInputAddonGroup(boolean renderInputAddonGroup);

    /**
     * List of CSS classes that will be applied to the span that wraps the post input components.
     *
     * TODO: revisist this, possibly getting the classes from component wrapper css classes once created
     *
     * @return List of CSS classes
     */
    List<String> getPostInputCssClasses();

    /**
     * Returns the list of post input css classes as a string formed by joining the classes with a space.
     *
     * @return post input css classes string
     */
    String getPostInputCssClassesAsString();

    /**
     * @see InputField#getPostInputCssClasses()
     */
    void setPostInputCssClasses(List<String> postInputCssClasses);

    /**
     * List of components that will be grouped with the input field control to form an input group.
     *
     * <p>Generally these are icon, link, or button components that should be rendered with the control.</p>
     *
     * <p>See <a href="http://getbootstrap.com/components/#input-groups">Bootstrap Input Groups</a></p>
     *
     * @return List of post input components
     */
    List<Component> getPostInputAddons();

    /**
     * @see org.kuali.rice.krad.uif.field.InputField#getPostInputAddons()
     */
    void setPostInputAddons(List<Component> postInputAddons);

    /**
     * Adds a component to the list of post input addon components.
     *
     * @param addOn component to add
     * @see InputField#getPostInputAddons()
     */
    void addPostInputAddon(Component addOn);

    /**
     * Instructional text that display an explanation of the field usage
     *
     * <p>
     * Text explaining how to use the field, including things like what values should be selected
     * in certain cases and so on (instructions)
     * </p>
     *
     * @return instructional message
     */
    String getInstructionalText();

    /**
     * Setter for the instructional message
     *
     * @param instructionalText the instructional text to set
     */
    void setInstructionalText(String instructionalText);

    /**
     * Message field that displays instructional text
     *
     * <p>
     * This message field can be configured to for adjusting how the instructional text will display. Generally
     * the styleClasses property will be of most interest
     * </p>
     *
     * @return instructional message field
     */
    Message getInstructionalMessage();

    /**
     * Setter for the instructional text message field
     *
     * <p>
     * Note this is the setter for the field that will render the instructional text. The actual text can be
     * set on the field but can also be set using {@link #setInstructionalText(String)}
     * </p>
     *
     * @param instructionalMessage the instructional message to set
     */
    void setInstructionalMessage(Message instructionalMessage);

    /**
     * Help text that displays under the control and is disclosed on focus.
     *
     * @return String help text for input
     */
    String getHelperText();

    /**
     * @see InputField#getHelperText()
     */
    void setHelperText(String helperText);

    /**
     * Text that display a restriction on the value a field can hold
     *
     * <p>
     * For example when the value must be a valid format (phone number, email), certain length, min/max value and
     * so on this text can be used to indicate the constraint to the user. Generally displays with the control so
     * it is visible when the user tabs to the field
     * </p>
     *
     * @return text to display for the constraint message
     */
    String getConstraintText();

    /**
     * Setter for the constraint message text
     *
     * @param constraintText the constraint text to set
     */
    void setConstraintText(String constraintText);

    /**
     * Message field that displays constraint text
     *
     * <p>
     * This message field can be configured to for adjusting how the constrain text will display. Generally
     * the styleClasses property will be of most interest
     * </p>
     *
     * @return constraint message field
     */
    Message getConstraintMessage();

    /**
     * Setter for the constraint text message field
     *
     * <p>
     * Note this is the setter for the field that will render the constraint text. The actual text can be
     * set on the field but can also be set using {@link #setConstraintText(String)}
     * </p>
     *
     * @param constraintMessage the constrain message field to set
     */
    void setConstraintMessage(Message constraintMessage);

    /**
     * Setter for {@code validCharacterConstraint}
     *
     * @param validCharactersConstraint the {@code ValidCharactersConstraint} to set
     */
    void setValidCharactersConstraint(ValidCharactersConstraint validCharactersConstraint);

    /**
     * Setter for {@code caseConstraint}
     *
     * @param caseConstraint the {@code CaseConstraint} to set
     */
    void setCaseConstraint(CaseConstraint caseConstraint);

    /**
     * List of {@code PrerequisiteConstraint} that apply to this {@code InputField}
     *
     * @return the dependency constraints for this input field
     */
    List<PrerequisiteConstraint> getDependencyConstraints();

    /**
     * Setter for {@code dependencyConstraints}
     *
     * @param dependencyConstraints list of {@code PrerequisiteConstraint} to set
     */
    void setDependencyConstraints(List<PrerequisiteConstraint> dependencyConstraints);

    /**
     * Setter for {@code mustOccurConstraints}
     *
     * @param mustOccurConstraints list of {@code MustOccurConstraint} to set
     */
    void setMustOccurConstraints(List<MustOccurConstraint> mustOccurConstraints);

    /**
     * Setter for simple constraint
     *
     * <p>
     * When a simple constraint is set on this object ALL simple validation
     * constraints set directly will be overridden - recommended to use this or
     * the other gets/sets for defining simple constraints, not both.
     * </p>
     *
     * @param simpleConstraint the simple constraint to set
     */
    void setSimpleConstraint(SimpleConstraint simpleConstraint);

    /**
     * This does not have to be set, represents the DataType constraint of this field.
     * This is only checked during server side validation.
     *
     * @param dataType the dataType to set
     */
    void setDataType(DataType dataType);

    void setDataType(String dataType);

    /**
     * Gets the DataType of this InputField, note that DataType set to be date
     * when this field is using a date picker with a TextControl and has not otherwise been
     * explicitly set.
     *
     * @return DataType
     */
    DataType getDataType();

    /**
     * Maximum number of characters the input field value is allowed to have
     *
     * <p>
     * The maximum length determines the maximum allowable length of the value
     * for data entry editing purposes.  The maximum length is inclusive and can
     * be smaller or longer than the actual control size.  The constraint
     * is enforced on all data types (e.g. a numeric data type needs to meet the
     * maximum length constraint in which digits and symbols are counted).
     * </p>
     *
     * @return the maximum length of the input field
     */
    Integer getMaxLength();

    /**
     * Setter for input field max length
     *
     * @param maxLength the maximum length to set
     */
    void setMaxLength(Integer maxLength);

    /**
     * Minimum number of characters the input field value needs to be
     *
     * <p>
     * The minimum length determines the minimum required length of the value for
     * data entry editing purposes.  The minimum length is inclusive. The constraint
     * is enforced on all data types (e.g. a numeric data type needs to meet the
     * minimum length requirement in which digits and symbols are counted).
     * </p>
     *
     * @return the minimum length of the input field
     */
    Integer getMinLength();

    /**
     * Setter for input field minimum length
     *
     * @param minLength the minLength to set
     */
    void setMinLength(Integer minLength);

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#getRequired()
     */
    Boolean getRequired();

    /**
     * @see org.kuali.rice.krad.uif.component.ComponentBase#setRequired(java.lang.Boolean)
     */
    void setRequired(Boolean required);

    /**
     * The exclusive minimum value for numeric or date field.
     *
     * <p>
     * The exclusiveMin element determines the minimum allowable value for data
     * entry editing purposes. This constrain is supported for numeric and
     * date fields and to be used in conjunction with the appropriate
     * {@link ValidCharactersConstraint}.
     *
     * For numeric constraint the value can be an integer or decimal such as -.001 or 99.
     * </p>
     *
     * @return the exclusive minimum numeric value of the input field
     */
    String getExclusiveMin();

    /**
     * Setter for the field's exclusive minimum value
     *
     * @param exclusiveMin the minimum value to set
     */
    void setExclusiveMin(String exclusiveMin);

    /**
     * The inclusive maximum value for numeric or date field.
     *
     * <p>
     * The inclusiveMax element determines the maximum allowable value for data
     * entry editing purposes. This constrain is supported for numeric and
     * date fields and to be used in conjunction with the appropriate
     * {@link ValidCharactersConstraint}.
     *
     * For numeric constraint the value can be an integer or decimal such as -.001 or 99.
     * </p>
     *
     * @return the inclusive maximum numeric value of the input field
     */
    String getInclusiveMax();

    /**
     * Setter for the field's inclusive maximum value
     *
     * @param inclusiveMax the maximum value to set
     */
    void setInclusiveMax(String inclusiveMax);

    /**
     * Attribute query instance configured for this field to dynamically pull information back for
     * updates other fields or providing messages
     *
     * <p>
     * If field attribute query is not null, associated event script will be generated to trigger the
     * query from the UI. This will invoke the {@code AttributeQueryService} to
     * execute the query and return an instance of {@code AttributeQueryResult} that is then
     * read by the script to update the UI. Typically used to update informational property values or
     * other field values
     * </p>
     *
     * @return AttributeQuery instance
     */
    AttributeQuery getAttributeQuery();

    /**
     * Setter for this field's attribute query
     *
     * @param attributeQuery
     */
    void setAttributeQuery(AttributeQuery attributeQuery);

    /**
     * Perform uppercase flag for this field to force input to uppercase.
     *
     * <p>
     * It this flag is set to true the 'text-transform' style on the field will be set to 'uppercase'
     * which will automatically change any text input into the field to uppercase.
     * </p>
     *
     * @return performUppercase flag
     */
    boolean isUppercaseValue();

    /**
     * Setter for this field's performUppercase flag
     *
     * @param uppercaseValue boolean flag
     */
    void setUppercaseValue(boolean uppercaseValue);

    /**
     * Indicates whether the browser autocomplete functionality should be disabled for the
     * input field (adds autocomplete="off")
     *
     * <p>
     * The browser's native autocomplete functionality can cause issues with security fields and also fields
     * with the UIF suggest widget enabled
     * </p>
     *
     * @return true if the native autocomplete should be turned off for the input field, false if not
     */
    boolean isDisableNativeAutocomplete();

    /**
     * Setter to disable browser autocomplete for the input field
     *
     * @param disableNativeAutocomplete
     */
    void setDisableNativeAutocomplete(boolean disableNativeAutocomplete);

    boolean isRenderFieldset();

    /**
     * @see org.kuali.rice.krad.uif.component.Component#completeValidation
     */
    void completeValidation(ValidationTrace tracer);

}