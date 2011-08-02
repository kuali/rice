package org.kuali.rice.core.api.uif;

/**
 * Defines configuration for an attribute which supports range-based lookups.
 * This allows the party executing the lookup against this attribute to enter a value for both ends
 * (lower and upper bounds) in order to determine if the attribute is "between" those
 * two values.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface AttributeRange {

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
