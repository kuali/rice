package org.kuali.rice.core.api.mo;

/**
 * Complete model objects in rice override {@link #equals(Object)}, {@link #hashCode()},
 * in addition to what is defined in the ModelObjectBasic interface.
 *
 * An example of a "Complete" Model object are the immutable transfer object
 * that rice uses in it's service APIs.
 */
public interface ModelObjectComplete extends ModelObjectBasic {

    /**
     * All "Complete" model object's should adhere to the {@link #equals(Object)} contract.
     *
     * @param o to object to compare for equality
     * @return if equal
     */
    @Override
    boolean equals(Object o);

    /**
     * All "Complete" model object's should adhere to the {@link #hashCode()} contract.
     *
     * @return the hashCode value
     */
    @Override
    int hashCode();
}
