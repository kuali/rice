package org.kuali.rice.shareddata.api.state;

/**
 * This is the contract for a State.
 */
public interface StateContract {

	/**
     * This the postal code for the State.  This cannot be null or a blank string.
     *
     * @return postal code
     */
    String getPostalCode();

    /**
     * This the postal name for the State.  This cannot be null or a blank string.
     *
     * @return postal code
     */
    String getPostalName();

    /**
     * This the postal country code for the State.  This cannot be null or a blank string.
     *
     * @return postal code
     */
    String getPostalCountryCode();

    /**
     * This the active flag for the State.
     *
     * @return the active flag of the State
     */
    boolean isActive();
}
