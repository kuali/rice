package org.kuali.rice.core.api.uif;

import java.util.Collection;

/**
 * This interface describes an attribute.  It can be considered the definition for an attribute.
 * It also contains preferred rendering instructions for an attribute. ie when rendering an attribute
 * in a user interface use this control with these widgets.
 */
public interface AttributeField {

    /**
     * The name of the attribute.  Cannot be null or blank.
     *
     * @return the name.
     */
    String getName();

    /**
     * The dataType of the attribute. Can be null.
     *
     * @return the datatype or null.
     */
    DataType getDataType();

    /**
     * The short label of the attribute. Can be null.
     *
     * @return the short label or null.
     */
    String getShortLabel();

    /**
     * The long label of the attribute. Can be null.
     *
     * @return the long label or null.
     */
    String getLongLabel();

    /**
     * The help summary of the attribute. Can be null.
     *
     * @return the help summary or null.
     */
    String getHelpSummary();

    /**
     * The help constraint of the attribute. Can be null.
     *
     * @return the help constraint or null.
     */
    String getHelpConstraint();

    /**
     * The help description of the attribute. Can be null.
     *
     * @return the help description or null.
     */
    String getHelpDescription();

    /**
     * Should the attribute always be in uppercase. Defaults to false.
     *
     * @return force uppercase.
     */
    boolean isForceUpperCase();

    /**
     * The inclusive minimum length of the attribute. Can be null. Cannot be less than 1.
     *
     * @return minimum length.
     */
    Integer getMinLength();

    /**
     * The inclusive maximum length of the attribute. Can be null. Cannot be less than 1.
     *
     * @return maximum length.
     */
    Integer getMaxLength();

    /**
     * The inclusive minimum value of the attribute. Can be null.
     *
     * @return minimum value.
     */
    Double getMinValue();

    /**
     * The inclusive maximum value of the attribute. Can be null.
     *
     * @return maximum value.
     */
    Double getMaxValue();

    /**
     * The regex constraint to apply to the attribute field for validation. Can be null.
     *
     * @return the constraint.
     */
    String getRegexConstraint();

    /**
     * The message to display if the regex constraint fails. Can be null.
     *
     * @return the constraint message.
     */
    String getRegexContraintMsg();

    /**
     * Whether the attribute is a required attribute. Defaults to false.
     * @return whether the attribute is required.
     */
    boolean isRequired();

    /**
     * The default values for the attribute.  In the case where the "control" associated
     * with the attribute only allows a single default value then only one item in this list will be used.
     * Cannot be null.  Will always return an immutable list.
     *
     * @return collection of default values
     */
    Collection<String> getDefaultValues();

    /**
     * The control associated with the attribute.  Can be null.
     * @return the control.
     */
    Control getControl();

    /**
     * The widgets for the attribute. Will always return an immutable list.
     *
     * @return collection of widgets
     */
    Collection<? extends Widget> getWidgets();

    /**
     * Indicates if lookups which use this attribute should execute the lookup against this attribute
     * in a case sensitive fashion.  If this method returns null, it means that the system-level
     * default for case sensitivity of attributes on lookups should be used.
     *
     * @return true if the attribute should be case sensitive on lookups, false if it should not, and
     * null if the system-level default should be used
     */
    Boolean isLookupCaseSensitive();

    /**
     * If this method returns a non-null value, it indicates that on a lookup, this attribute should be
     * treated as a range.  This will typically translate to multiple UI controls that can be used to
     * input both ends (lower and upper bounds) of the ranged lookup on the attribute.
     *
     * <p>Note that an attribute range only makes sense if the {@code DataType} of this attribute is
     * a date or numerical data type.  The caller of this method is free to ignore the given
     * {@code AttributeRange} if it does not believe it is possible to successfully present a range-based
     * lookup option based on the data type (or other factors) of the {@code AttributeField}.</p>
     *
     * @return the attribute range configuration for this attribute, or null if this attribute should
     * not be treated as a range in a lookup
     */
    AttributeRange getAttributeRange();

}
