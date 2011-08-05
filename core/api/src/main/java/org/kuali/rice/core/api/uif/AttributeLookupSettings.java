package org.kuali.rice.core.api.uif;

/**
 * Defines configuration for an attribute which may be used as part of a lookups.  This includes indicating if the
 * field is displayed in the lookup criteria or lookup result set, as well as defining information about this field
 * if it used as part of a range-based lookup.
 *
 * <p>The range bounds allows the party executing the lookup against this attribute to enter a value for both ends
 * (lower and upper bounds) in order to determine if the attribute is "between" those two values.</p>
 *
 * <p>Note that an attribute range only makes sense if the {@code DataType} of the attribute is
 * a date or numerical data type.  The consumer of these settings is free to ignore the given
 * attribute range information if it does not believe it is possible to successfully present a range-based
 * lookup option based on the data type (or other settings) of the {@code AttributeField}.</p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface AttributeLookupSettings {

    /**
     * Returns true if this field should be included as part of the lookup criteria, false if not.
     *
     * @return true if this field should be included as part of the lookup criteria, false if not
     */
    boolean isInCriteria();

    /**
     * Returns true if this field should be included in the result set of the lookup, false if not.
     *
     * @return true if this field should be included in the result set of the lookup, false if not
     */
    boolean isInResults();

    /**
     * Returns true if lookups against this field should be handled as a lookup operation supporting a range-based
     * search of data against the field.
     *
     * @return true if lookups against this attribute should allow for ranged lookup fields, false otherwise
     */
    boolean isRanged();

    /**
     * Returns the name to assign to the lower bound of the range.  It is important to ensure that this
     * does not conflict with any other attribute names being used on the lookup.
     *
     * @return the name of the lower bound of the range
     */
    String getLowerBoundName();

    /**
     * Returns the label to use for the lower bound of the range.  If no label is defined, then the
     * framework will generate one.
     *
     * @return the label of the lower bound of the range
     */
    String getLowerBoundLabel();


    /**
     * Returns true if the lower bound should be treated as inclusive when executing a ranged
     * lookup against the attribute, false if it should be treated as exclusive.
     *
     * @return true if the lower bound is inclusive, false if it is exclusive
     */
    boolean isLowerBoundInclusive();

    /**
     * Returns the name to assign to the upper bound of the range.  It is important to ensure that this
     * does not conflict with any other attribute names being used on the lookup.
     *
     * @return the name of the upper bound of the range
     */
    String getUpperBoundName();

    /**
     * Returns the label to use for the upper bound of the range.  If no label is defined, then the
     * framework will generate one.
     *
     * @return the label of the upper bound of the range
     */
    String getUpperBoundLabel();

    /**
     * Returns true if the upper bound should be treated as inclusive when executing a ranged
     * lookup against the attribute, false if it should be treated as exclusive.
     *
     * @return true if the upper bound is inclusive, false if it is exclusive
     */
    boolean isUpperBoundInclusive();

}
